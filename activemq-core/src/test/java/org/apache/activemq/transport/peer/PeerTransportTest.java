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
operator|.
name|peer
package|;
end_package

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
name|MessageConsumer
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
name|advisory
operator|.
name|AdvisorySupport
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
name|ActiveMQDestination
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
name|ActiveMQMessage
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
name|command
operator|.
name|ConsumerInfo
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
name|MessageIdList
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|commons
operator|.
name|logging
operator|.
name|Log
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|commons
operator|.
name|logging
operator|.
name|LogFactory
import|;
end_import

begin_comment
comment|/**  * @version $Revision: 1.1.1.1 $  */
end_comment

begin_class
specifier|public
class|class
name|PeerTransportTest
extends|extends
name|TestCase
block|{
specifier|protected
name|Log
name|log
init|=
name|LogFactory
operator|.
name|getLog
argument_list|(
name|getClass
argument_list|()
argument_list|)
decl_stmt|;
specifier|protected
name|ActiveMQDestination
name|destination
decl_stmt|;
specifier|protected
name|boolean
name|topic
init|=
literal|true
decl_stmt|;
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
name|MessageIdList
name|messageIdList
index|[]
decl_stmt|;
specifier|protected
name|void
name|setUp
parameter_list|()
throws|throws
name|Exception
block|{
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
name|messageIdList
operator|=
operator|new
name|MessageIdList
index|[
name|NUMBER_IN_CLUSTER
index|]
expr_stmt|;
name|ActiveMQDestination
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
name|connections
index|[
name|i
index|]
operator|=
name|createConnection
argument_list|(
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
name|messageIdList
index|[
name|i
index|]
operator|=
operator|new
name|MessageIdList
argument_list|()
expr_stmt|;
name|consumer
operator|.
name|setMessageListener
argument_list|(
name|messageIdList
index|[
name|i
index|]
argument_list|)
expr_stmt|;
block|}
name|log
operator|.
name|info
argument_list|(
literal|"Waiting for cluster to be fully connected"
argument_list|)
expr_stmt|;
comment|// Each connection should see that NUMBER_IN_CLUSTER consumers get
comment|// registered on the destination.
name|ActiveMQDestination
name|advisoryDest
init|=
name|AdvisorySupport
operator|.
name|getConsumerAdvisoryTopic
argument_list|(
name|destination
argument_list|)
decl_stmt|;
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
name|MessageConsumer
name|consumer
init|=
name|createMessageConsumer
argument_list|(
name|session
argument_list|,
name|advisoryDest
argument_list|)
decl_stmt|;
name|int
name|j
init|=
literal|0
decl_stmt|;
while|while
condition|(
name|j
operator|<
name|NUMBER_IN_CLUSTER
condition|)
block|{
name|ActiveMQMessage
name|message
init|=
operator|(
name|ActiveMQMessage
operator|)
name|consumer
operator|.
name|receive
argument_list|(
literal|1000
argument_list|)
decl_stmt|;
if|if
condition|(
name|message
operator|==
literal|null
condition|)
block|{
name|fail
argument_list|(
literal|"Connection "
operator|+
name|i
operator|+
literal|" saw "
operator|+
name|j
operator|+
literal|" consumers, expected: "
operator|+
name|NUMBER_IN_CLUSTER
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|message
operator|.
name|getDataStructure
argument_list|()
operator|!=
literal|null
operator|&&
name|message
operator|.
name|getDataStructure
argument_list|()
operator|.
name|getDataStructureType
argument_list|()
operator|==
name|ConsumerInfo
operator|.
name|DATA_STRUCTURE_TYPE
condition|)
block|{
name|j
operator|++
expr_stmt|;
block|}
block|}
name|session
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
name|log
operator|.
name|info
argument_list|(
literal|"Cluster is online."
argument_list|)
expr_stmt|;
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
name|Connection
name|createConnection
parameter_list|(
name|int
name|i
parameter_list|)
throws|throws
name|JMSException
block|{
name|log
operator|.
name|info
argument_list|(
literal|"creating connection ...."
argument_list|)
expr_stmt|;
name|ActiveMQConnectionFactory
name|fac
init|=
operator|new
name|ActiveMQConnectionFactory
argument_list|(
literal|"peer://"
operator|+
name|getClass
argument_list|()
operator|.
name|getName
argument_list|()
operator|+
literal|"/node"
operator|+
name|i
argument_list|)
decl_stmt|;
return|return
name|fac
operator|.
name|createConnection
argument_list|()
return|;
block|}
specifier|protected
name|ActiveMQDestination
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
name|ActiveMQDestination
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
literal|"MSG-NO: "
operator|+
name|i
operator|+
literal|" in cluster: "
operator|+
name|x
argument_list|)
expr_stmt|;
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
name|messageIdList
index|[
name|i
index|]
operator|.
name|assertMessagesReceived
argument_list|(
name|expectedReceiveCount
argument_list|()
argument_list|)
expr_stmt|;
block|}
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
return|;
block|}
block|}
end_class

end_unit

