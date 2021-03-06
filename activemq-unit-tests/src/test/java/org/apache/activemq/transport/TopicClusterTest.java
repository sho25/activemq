begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *      http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
package|package
name|org
operator|.
name|apache
operator|.
name|activemq
operator|.
name|transport
package|;
end_package

begin_import
import|import
name|java
operator|.
name|net
operator|.
name|URI
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|ArrayList
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|List
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|concurrent
operator|.
name|atomic
operator|.
name|AtomicInteger
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|jms
operator|.
name|Connection
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|jms
operator|.
name|DeliveryMode
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|jms
operator|.
name|Destination
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|jms
operator|.
name|JMSException
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|jms
operator|.
name|Message
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|jms
operator|.
name|MessageConsumer
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|jms
operator|.
name|MessageListener
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|jms
operator|.
name|MessageProducer
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|jms
operator|.
name|Session
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|jms
operator|.
name|TextMessage
import|;
end_import

begin_import
import|import
name|junit
operator|.
name|framework
operator|.
name|TestCase
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|activemq
operator|.
name|ActiveMQConnectionFactory
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|activemq
operator|.
name|broker
operator|.
name|BrokerService
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|activemq
operator|.
name|broker
operator|.
name|TransportConnector
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|activemq
operator|.
name|command
operator|.
name|ActiveMQQueue
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|activemq
operator|.
name|command
operator|.
name|ActiveMQTextMessage
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|activemq
operator|.
name|command
operator|.
name|ActiveMQTopic
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|activemq
operator|.
name|util
operator|.
name|ServiceStopper
import|;
end_import

begin_import
import|import
name|org
operator|.
name|slf4j
operator|.
name|Logger
import|;
end_import

begin_import
import|import
name|org
operator|.
name|slf4j
operator|.
name|LoggerFactory
import|;
end_import

begin_comment
comment|/**  *   */
end_comment

begin_class
specifier|public
class|class
name|TopicClusterTest
extends|extends
name|TestCase
implements|implements
name|MessageListener
block|{
specifier|protected
specifier|static
specifier|final
name|int
name|MESSAGE_COUNT
init|=
literal|50
decl_stmt|;
specifier|protected
specifier|static
specifier|final
name|int
name|NUMBER_IN_CLUSTER
init|=
literal|3
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|Logger
name|LOG
init|=
name|LoggerFactory
operator|.
name|getLogger
argument_list|(
name|TopicClusterTest
operator|.
name|class
argument_list|)
decl_stmt|;
specifier|protected
name|Destination
name|destination
decl_stmt|;
specifier|protected
name|boolean
name|topic
init|=
literal|true
decl_stmt|;
specifier|protected
name|AtomicInteger
name|receivedMessageCount
init|=
operator|new
name|AtomicInteger
argument_list|(
literal|0
argument_list|)
decl_stmt|;
specifier|protected
name|int
name|deliveryMode
init|=
name|DeliveryMode
operator|.
name|NON_PERSISTENT
decl_stmt|;
specifier|protected
name|MessageProducer
index|[]
name|producers
decl_stmt|;
specifier|protected
name|Connection
index|[]
name|connections
decl_stmt|;
specifier|protected
name|List
argument_list|<
name|BrokerService
argument_list|>
name|services
init|=
operator|new
name|ArrayList
argument_list|<
name|BrokerService
argument_list|>
argument_list|()
decl_stmt|;
specifier|protected
name|String
name|groupId
decl_stmt|;
specifier|protected
name|void
name|setUp
parameter_list|()
throws|throws
name|Exception
block|{
name|groupId
operator|=
literal|"topic-cluster-test-"
operator|+
name|System
operator|.
name|currentTimeMillis
argument_list|()
expr_stmt|;
name|connections
operator|=
operator|new
name|Connection
index|[
name|NUMBER_IN_CLUSTER
index|]
expr_stmt|;
name|producers
operator|=
operator|new
name|MessageProducer
index|[
name|NUMBER_IN_CLUSTER
index|]
expr_stmt|;
name|Destination
name|destination
init|=
name|createDestination
argument_list|()
decl_stmt|;
name|String
name|root
init|=
name|System
operator|.
name|getProperty
argument_list|(
literal|"activemq.store.dir"
argument_list|)
decl_stmt|;
if|if
condition|(
name|root
operator|==
literal|null
condition|)
block|{
name|root
operator|=
literal|"target/store"
expr_stmt|;
block|}
try|try
block|{
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|NUMBER_IN_CLUSTER
condition|;
name|i
operator|++
control|)
block|{
name|System
operator|.
name|setProperty
argument_list|(
literal|"activemq.store.dir"
argument_list|,
name|root
operator|+
literal|"_broker_"
operator|+
name|i
argument_list|)
expr_stmt|;
name|connections
index|[
name|i
index|]
operator|=
name|createConnection
argument_list|(
literal|"broker-"
operator|+
name|i
argument_list|)
expr_stmt|;
name|connections
index|[
name|i
index|]
operator|.
name|setClientID
argument_list|(
literal|"ClusterTest"
operator|+
name|i
argument_list|)
expr_stmt|;
name|connections
index|[
name|i
index|]
operator|.
name|start
argument_list|()
expr_stmt|;
name|Session
name|session
init|=
name|connections
index|[
name|i
index|]
operator|.
name|createSession
argument_list|(
literal|false
argument_list|,
name|Session
operator|.
name|AUTO_ACKNOWLEDGE
argument_list|)
decl_stmt|;
name|producers
index|[
name|i
index|]
operator|=
name|session
operator|.
name|createProducer
argument_list|(
name|destination
argument_list|)
expr_stmt|;
name|producers
index|[
name|i
index|]
operator|.
name|setDeliveryMode
argument_list|(
name|deliveryMode
argument_list|)
expr_stmt|;
name|MessageConsumer
name|consumer
init|=
name|createMessageConsumer
argument_list|(
name|session
argument_list|,
name|destination
argument_list|)
decl_stmt|;
name|consumer
operator|.
name|setMessageListener
argument_list|(
name|this
argument_list|)
expr_stmt|;
block|}
name|LOG
operator|.
name|info
argument_list|(
literal|"Sleeping to ensure cluster is fully connected"
argument_list|)
expr_stmt|;
name|Thread
operator|.
name|sleep
argument_list|(
literal|5000
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|System
operator|.
name|setProperty
argument_list|(
literal|"activemq.store.dir"
argument_list|,
name|root
argument_list|)
expr_stmt|;
block|}
block|}
specifier|protected
name|void
name|tearDown
parameter_list|()
throws|throws
name|Exception
block|{
if|if
condition|(
name|connections
operator|!=
literal|null
condition|)
block|{
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|connections
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|connections
index|[
name|i
index|]
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
name|ServiceStopper
name|stopper
init|=
operator|new
name|ServiceStopper
argument_list|()
decl_stmt|;
name|stopper
operator|.
name|stopServices
argument_list|(
name|services
argument_list|)
expr_stmt|;
block|}
specifier|protected
name|MessageConsumer
name|createMessageConsumer
parameter_list|(
name|Session
name|session
parameter_list|,
name|Destination
name|destination
parameter_list|)
throws|throws
name|JMSException
block|{
return|return
name|session
operator|.
name|createConsumer
argument_list|(
name|destination
argument_list|)
return|;
block|}
specifier|protected
name|ActiveMQConnectionFactory
name|createGenericClusterFactory
parameter_list|(
name|String
name|brokerName
parameter_list|)
throws|throws
name|Exception
block|{
name|BrokerService
name|container
init|=
operator|new
name|BrokerService
argument_list|()
decl_stmt|;
name|container
operator|.
name|setBrokerName
argument_list|(
name|brokerName
argument_list|)
expr_stmt|;
name|String
name|url
init|=
literal|"tcp://localhost:0"
decl_stmt|;
name|TransportConnector
name|connector
init|=
name|container
operator|.
name|addConnector
argument_list|(
name|url
argument_list|)
decl_stmt|;
name|connector
operator|.
name|setDiscoveryUri
argument_list|(
operator|new
name|URI
argument_list|(
literal|"multicast://default?group="
operator|+
name|groupId
argument_list|)
argument_list|)
expr_stmt|;
name|container
operator|.
name|addNetworkConnector
argument_list|(
literal|"multicast://default?group="
operator|+
name|groupId
argument_list|)
expr_stmt|;
name|container
operator|.
name|start
argument_list|()
expr_stmt|;
name|services
operator|.
name|add
argument_list|(
name|container
argument_list|)
expr_stmt|;
return|return
operator|new
name|ActiveMQConnectionFactory
argument_list|(
literal|"vm://"
operator|+
name|brokerName
argument_list|)
return|;
block|}
specifier|protected
name|int
name|expectedReceiveCount
parameter_list|()
block|{
return|return
name|MESSAGE_COUNT
operator|*
name|NUMBER_IN_CLUSTER
operator|*
name|NUMBER_IN_CLUSTER
return|;
block|}
specifier|protected
name|Connection
name|createConnection
parameter_list|(
name|String
name|name
parameter_list|)
throws|throws
name|Exception
block|{
return|return
name|createGenericClusterFactory
argument_list|(
name|name
argument_list|)
operator|.
name|createConnection
argument_list|()
return|;
block|}
specifier|protected
name|Destination
name|createDestination
parameter_list|()
block|{
return|return
name|createDestination
argument_list|(
name|getClass
argument_list|()
operator|.
name|getName
argument_list|()
argument_list|)
return|;
block|}
specifier|protected
name|Destination
name|createDestination
parameter_list|(
name|String
name|name
parameter_list|)
block|{
if|if
condition|(
name|topic
condition|)
block|{
return|return
operator|new
name|ActiveMQTopic
argument_list|(
name|name
argument_list|)
return|;
block|}
else|else
block|{
return|return
operator|new
name|ActiveMQQueue
argument_list|(
name|name
argument_list|)
return|;
block|}
block|}
comment|/**      * @param msg      */
specifier|public
name|void
name|onMessage
parameter_list|(
name|Message
name|msg
parameter_list|)
block|{
comment|// log.info("GOT: " + msg);
name|receivedMessageCount
operator|.
name|incrementAndGet
argument_list|()
expr_stmt|;
synchronized|synchronized
init|(
name|receivedMessageCount
init|)
block|{
if|if
condition|(
name|receivedMessageCount
operator|.
name|get
argument_list|()
operator|>=
name|expectedReceiveCount
argument_list|()
condition|)
block|{
name|receivedMessageCount
operator|.
name|notify
argument_list|()
expr_stmt|;
block|}
block|}
block|}
comment|/**      * @throws Exception      */
specifier|public
name|void
name|testSendReceive
parameter_list|()
throws|throws
name|Exception
block|{
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|MESSAGE_COUNT
condition|;
name|i
operator|++
control|)
block|{
name|TextMessage
name|textMessage
init|=
operator|new
name|ActiveMQTextMessage
argument_list|()
decl_stmt|;
name|textMessage
operator|.
name|setText
argument_list|(
literal|"MSG-NO:"
operator|+
name|i
argument_list|)
expr_stmt|;
for|for
control|(
name|int
name|x
init|=
literal|0
init|;
name|x
operator|<
name|producers
operator|.
name|length
condition|;
name|x
operator|++
control|)
block|{
name|producers
index|[
name|x
index|]
operator|.
name|send
argument_list|(
name|textMessage
argument_list|)
expr_stmt|;
block|}
block|}
synchronized|synchronized
init|(
name|receivedMessageCount
init|)
block|{
if|if
condition|(
name|receivedMessageCount
operator|.
name|get
argument_list|()
operator|<
name|expectedReceiveCount
argument_list|()
condition|)
block|{
name|receivedMessageCount
operator|.
name|wait
argument_list|(
literal|20000
argument_list|)
expr_stmt|;
block|}
block|}
comment|// sleep a little - to check we don't get too many messages
name|Thread
operator|.
name|sleep
argument_list|(
literal|2000
argument_list|)
expr_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"GOT: "
operator|+
name|receivedMessageCount
operator|.
name|get
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"Expected message count not correct"
argument_list|,
name|expectedReceiveCount
argument_list|()
argument_list|,
name|receivedMessageCount
operator|.
name|get
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

