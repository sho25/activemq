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
package|;
end_package

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|IOException
import|;
end_import

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
name|net
operator|.
name|URISyntaxException
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
name|CountDownLatch
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
name|TimeUnit
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|jms
operator|.
name|BytesMessage
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
name|ConnectionFactory
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
name|Topic
import|;
end_import

begin_import
import|import
name|junit
operator|.
name|framework
operator|.
name|Test
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
name|BrokerFactory
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
name|ActiveMQDestination
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
comment|/**  * Small burn test moves sends a moderate amount of messages through the broker,  * to checking to make sure that the broker does not lock up after a while of  * sustained messaging.  *   * @version $Revision$  */
end_comment

begin_class
specifier|public
class|class
name|LoadTestBurnIn
extends|extends
name|JmsTestSupport
block|{
specifier|private
specifier|static
specifier|final
specifier|transient
name|Logger
name|LOG
init|=
name|LoggerFactory
operator|.
name|getLogger
argument_list|(
name|LoadTestBurnIn
operator|.
name|class
argument_list|)
decl_stmt|;
specifier|public
name|ActiveMQDestination
name|destination
decl_stmt|;
specifier|public
name|int
name|deliveryMode
decl_stmt|;
specifier|public
name|byte
name|destinationType
decl_stmt|;
specifier|public
name|boolean
name|durableConsumer
decl_stmt|;
specifier|public
name|int
name|messageCount
init|=
literal|50000
decl_stmt|;
specifier|public
name|int
name|messageSize
init|=
literal|1024
decl_stmt|;
specifier|public
specifier|static
name|Test
name|suite
parameter_list|()
block|{
return|return
name|suite
argument_list|(
name|LoadTestBurnIn
operator|.
name|class
argument_list|)
return|;
block|}
specifier|protected
name|void
name|setUp
parameter_list|()
throws|throws
name|Exception
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"Start: "
operator|+
name|getName
argument_list|()
argument_list|)
expr_stmt|;
name|super
operator|.
name|setUp
argument_list|()
expr_stmt|;
block|}
specifier|protected
name|void
name|tearDown
parameter_list|()
throws|throws
name|Exception
block|{
try|try
block|{
name|super
operator|.
name|tearDown
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Throwable
name|e
parameter_list|)
block|{
name|e
operator|.
name|printStackTrace
argument_list|(
name|System
operator|.
name|out
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"End: "
operator|+
name|getName
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
specifier|public
specifier|static
name|void
name|main
parameter_list|(
name|String
index|[]
name|args
parameter_list|)
block|{
name|junit
operator|.
name|textui
operator|.
name|TestRunner
operator|.
name|run
argument_list|(
name|suite
argument_list|()
argument_list|)
expr_stmt|;
block|}
specifier|protected
name|BrokerService
name|createBroker
parameter_list|()
throws|throws
name|Exception
block|{
return|return
name|BrokerFactory
operator|.
name|createBroker
argument_list|(
operator|new
name|URI
argument_list|(
literal|"broker://(tcp://localhost:0)?useJmx=true"
argument_list|)
argument_list|)
return|;
comment|// return BrokerFactory.createBroker(new
comment|// URI("xbean:org/apache/activemq/broker/store/loadtester.xml"));
block|}
specifier|protected
name|ConnectionFactory
name|createConnectionFactory
parameter_list|()
throws|throws
name|URISyntaxException
throws|,
name|IOException
block|{
return|return
operator|new
name|ActiveMQConnectionFactory
argument_list|(
operator|(
operator|(
name|TransportConnector
operator|)
name|broker
operator|.
name|getTransportConnectors
argument_list|()
operator|.
name|get
argument_list|(
literal|0
argument_list|)
operator|)
operator|.
name|getServer
argument_list|()
operator|.
name|getConnectURI
argument_list|()
argument_list|)
return|;
block|}
specifier|public
name|void
name|initCombosForTestSendReceive
parameter_list|()
block|{
name|addCombinationValues
argument_list|(
literal|"deliveryMode"
argument_list|,
operator|new
name|Object
index|[]
block|{
name|Integer
operator|.
name|valueOf
argument_list|(
name|DeliveryMode
operator|.
name|NON_PERSISTENT
argument_list|)
block|,
name|Integer
operator|.
name|valueOf
argument_list|(
name|DeliveryMode
operator|.
name|PERSISTENT
argument_list|)
block|}
argument_list|)
expr_stmt|;
name|addCombinationValues
argument_list|(
literal|"destinationType"
argument_list|,
operator|new
name|Object
index|[]
block|{
name|Byte
operator|.
name|valueOf
argument_list|(
name|ActiveMQDestination
operator|.
name|TOPIC_TYPE
argument_list|)
block|}
argument_list|)
expr_stmt|;
name|addCombinationValues
argument_list|(
literal|"durableConsumer"
argument_list|,
operator|new
name|Object
index|[]
block|{
name|Boolean
operator|.
name|TRUE
block|}
argument_list|)
expr_stmt|;
name|addCombinationValues
argument_list|(
literal|"messageSize"
argument_list|,
operator|new
name|Object
index|[]
block|{
name|Integer
operator|.
name|valueOf
argument_list|(
literal|101
argument_list|)
block|,
name|Integer
operator|.
name|valueOf
argument_list|(
literal|102
argument_list|)
block|,
name|Integer
operator|.
name|valueOf
argument_list|(
literal|103
argument_list|)
block|,
name|Integer
operator|.
name|valueOf
argument_list|(
literal|104
argument_list|)
block|,
name|Integer
operator|.
name|valueOf
argument_list|(
literal|105
argument_list|)
block|,
name|Integer
operator|.
name|valueOf
argument_list|(
literal|106
argument_list|)
block|,
name|Integer
operator|.
name|valueOf
argument_list|(
literal|107
argument_list|)
block|,
name|Integer
operator|.
name|valueOf
argument_list|(
literal|108
argument_list|)
block|}
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|testSendReceive
parameter_list|()
throws|throws
name|Exception
block|{
comment|// Durable consumer combination is only valid with topics
if|if
condition|(
name|durableConsumer
operator|&&
name|destinationType
operator|!=
name|ActiveMQDestination
operator|.
name|TOPIC_TYPE
condition|)
block|{
return|return;
block|}
name|connection
operator|.
name|setClientID
argument_list|(
name|getName
argument_list|()
argument_list|)
expr_stmt|;
name|connection
operator|.
name|getPrefetchPolicy
argument_list|()
operator|.
name|setAll
argument_list|(
literal|1000
argument_list|)
expr_stmt|;
name|connection
operator|.
name|start
argument_list|()
expr_stmt|;
name|Session
name|session
init|=
name|connection
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
name|destination
operator|=
name|createDestination
argument_list|(
name|session
argument_list|,
name|destinationType
argument_list|)
expr_stmt|;
name|MessageConsumer
name|consumer
decl_stmt|;
if|if
condition|(
name|durableConsumer
condition|)
block|{
name|consumer
operator|=
name|session
operator|.
name|createDurableSubscriber
argument_list|(
operator|(
name|Topic
operator|)
name|destination
argument_list|,
literal|"sub1:"
operator|+
name|System
operator|.
name|currentTimeMillis
argument_list|()
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|consumer
operator|=
name|session
operator|.
name|createConsumer
argument_list|(
name|destination
argument_list|)
expr_stmt|;
block|}
name|profilerPause
argument_list|(
literal|"Ready: "
argument_list|)
expr_stmt|;
specifier|final
name|CountDownLatch
name|producerDoneLatch
init|=
operator|new
name|CountDownLatch
argument_list|(
literal|1
argument_list|)
decl_stmt|;
comment|// Send the messages, async
operator|new
name|Thread
argument_list|()
block|{
specifier|public
name|void
name|run
parameter_list|()
block|{
name|Connection
name|connection2
init|=
literal|null
decl_stmt|;
try|try
block|{
name|connection2
operator|=
name|factory
operator|.
name|createConnection
argument_list|()
expr_stmt|;
name|Session
name|session
init|=
name|connection2
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
name|MessageProducer
name|producer
init|=
name|session
operator|.
name|createProducer
argument_list|(
name|destination
argument_list|)
decl_stmt|;
name|producer
operator|.
name|setDeliveryMode
argument_list|(
name|deliveryMode
argument_list|)
expr_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|messageCount
condition|;
name|i
operator|++
control|)
block|{
name|BytesMessage
name|m
init|=
name|session
operator|.
name|createBytesMessage
argument_list|()
decl_stmt|;
name|m
operator|.
name|writeBytes
argument_list|(
operator|new
name|byte
index|[
name|messageSize
index|]
argument_list|)
expr_stmt|;
name|producer
operator|.
name|send
argument_list|(
name|m
argument_list|)
expr_stmt|;
block|}
name|producer
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|JMSException
name|e
parameter_list|)
block|{
name|e
operator|.
name|printStackTrace
argument_list|()
expr_stmt|;
block|}
finally|finally
block|{
name|safeClose
argument_list|(
name|connection2
argument_list|)
expr_stmt|;
name|producerDoneLatch
operator|.
name|countDown
argument_list|()
expr_stmt|;
block|}
block|}
block|}
operator|.
name|start
argument_list|()
expr_stmt|;
comment|// Make sure all the messages were delivered.
name|Message
name|message
init|=
literal|null
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
name|messageCount
condition|;
name|i
operator|++
control|)
block|{
name|message
operator|=
name|consumer
operator|.
name|receive
argument_list|(
literal|5000
argument_list|)
expr_stmt|;
name|assertNotNull
argument_list|(
literal|"Did not get message: "
operator|+
name|i
argument_list|,
name|message
argument_list|)
expr_stmt|;
block|}
name|profilerPause
argument_list|(
literal|"Done: "
argument_list|)
expr_stmt|;
name|assertNull
argument_list|(
name|consumer
operator|.
name|receiveNoWait
argument_list|()
argument_list|)
expr_stmt|;
name|message
operator|.
name|acknowledge
argument_list|()
expr_stmt|;
comment|// Make sure the producer thread finishes.
name|assertTrue
argument_list|(
name|producerDoneLatch
operator|.
name|await
argument_list|(
literal|5
argument_list|,
name|TimeUnit
operator|.
name|SECONDS
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

