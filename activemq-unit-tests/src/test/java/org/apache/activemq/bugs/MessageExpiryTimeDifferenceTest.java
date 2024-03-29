begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *      http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
package|package
name|org
operator|.
name|apache
operator|.
name|activemq
operator|.
name|bugs
package|;
end_package

begin_import
import|import static
name|org
operator|.
name|junit
operator|.
name|Assert
operator|.
name|assertEquals
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|junit
operator|.
name|Assert
operator|.
name|assertNull
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|junit
operator|.
name|Assert
operator|.
name|fail
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Map
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
name|ExecutorService
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
name|Executors
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
name|Queue
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
name|javax
operator|.
name|management
operator|.
name|ObjectName
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
name|ActiveMQConnection
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
name|Broker
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
name|BrokerFilter
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
name|BrokerPlugin
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
name|ConsumerBrokerExchange
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
name|jmx
operator|.
name|DestinationView
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
name|jmx
operator|.
name|QueueView
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
name|MessageAck
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
name|MessageDispatch
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|After
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|Before
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|Test
import|;
end_import

begin_class
specifier|public
class|class
name|MessageExpiryTimeDifferenceTest
block|{
specifier|public
specifier|static
specifier|final
name|String
name|QUEUE_NAME
init|=
literal|"timeout.test"
decl_stmt|;
specifier|private
name|ActiveMQConnection
name|connection
decl_stmt|;
specifier|private
name|BrokerService
name|broker
decl_stmt|;
specifier|private
name|String
name|connectionUri
decl_stmt|;
annotation|@
name|Before
specifier|public
name|void
name|setUp
parameter_list|()
throws|throws
name|Exception
block|{
name|createBroker
argument_list|()
expr_stmt|;
name|connection
operator|=
name|createConnection
argument_list|()
expr_stmt|;
block|}
annotation|@
name|After
specifier|public
name|void
name|tearDown
parameter_list|()
throws|throws
name|Exception
block|{
if|if
condition|(
name|connection
operator|!=
literal|null
condition|)
block|{
try|try
block|{
name|connection
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{             }
block|}
if|if
condition|(
name|broker
operator|!=
literal|null
condition|)
block|{
name|broker
operator|.
name|stop
argument_list|()
expr_stmt|;
name|broker
operator|.
name|waitUntilStopped
argument_list|()
expr_stmt|;
block|}
block|}
comment|/**      * if the client clock is slightly ahead of the brokers clock a message      * could be expired on the client. When the expiry is sent to the broker it      * checks if the message is also considered expired on the broker side.      *      * If the broker clock is behind the message could be considered not expired      * on the broker and not removed from the broker's dispatched list. This      * leaves the broker reporting a message inflight from the broker's      * perspective even though the message has been expired on the      * consumer(client) side      *      * The BrokerFlight is used to manipulate the expiration timestamp on the      * message when it is sent and ack'd from the consumer to simulate a time      * difference between broker and client in the unit test. This is rather      * invasive but it it difficult to test this deterministically in a unit      * test.      */
annotation|@
name|Test
argument_list|(
name|timeout
operator|=
literal|30000
argument_list|)
specifier|public
name|void
name|testInflightCountAfterExpiry
parameter_list|()
throws|throws
name|Exception
block|{
name|connection
operator|.
name|start
argument_list|()
expr_stmt|;
comment|// push message to queue
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
name|Queue
name|queue
init|=
name|session
operator|.
name|createQueue
argument_list|(
literal|"timeout.test"
argument_list|)
decl_stmt|;
name|MessageProducer
name|producer
init|=
name|session
operator|.
name|createProducer
argument_list|(
name|queue
argument_list|)
decl_stmt|;
name|TextMessage
name|textMessage
init|=
name|session
operator|.
name|createTextMessage
argument_list|(
name|QUEUE_NAME
argument_list|)
decl_stmt|;
name|producer
operator|.
name|send
argument_list|(
name|textMessage
argument_list|)
expr_stmt|;
name|session
operator|.
name|close
argument_list|()
expr_stmt|;
comment|// try to consume message
name|session
operator|=
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
expr_stmt|;
specifier|final
name|MessageConsumer
name|consumer
init|=
name|session
operator|.
name|createConsumer
argument_list|(
name|queue
argument_list|)
decl_stmt|;
specifier|final
name|CountDownLatch
name|messageReceived
init|=
operator|new
name|CountDownLatch
argument_list|(
literal|1
argument_list|)
decl_stmt|;
comment|// call consume in a separate thread
name|ExecutorService
name|executorService
init|=
name|Executors
operator|.
name|newSingleThreadExecutor
argument_list|()
decl_stmt|;
name|executorService
operator|.
name|submit
argument_list|(
operator|new
name|Runnable
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|void
name|run
parameter_list|()
block|{
name|Message
name|message
init|=
literal|null
decl_stmt|;
try|try
block|{
name|message
operator|=
name|consumer
operator|.
name|receive
argument_list|(
literal|1000
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|JMSException
name|e
parameter_list|)
block|{
name|fail
argument_list|()
expr_stmt|;
block|}
comment|// message should be null as it should have expired and the
comment|// consumer.receive(timeout) should return null.
name|assertNull
argument_list|(
name|message
argument_list|)
expr_stmt|;
name|messageReceived
operator|.
name|countDown
argument_list|()
expr_stmt|;
block|}
block|}
argument_list|)
expr_stmt|;
name|messageReceived
operator|.
name|await
argument_list|(
literal|20
argument_list|,
name|TimeUnit
operator|.
name|SECONDS
argument_list|)
expr_stmt|;
name|QueueView
name|queueView
init|=
name|getQueueView
argument_list|(
name|broker
argument_list|,
name|QUEUE_NAME
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|"Should be No inflight messages"
argument_list|,
literal|0
argument_list|,
name|queueView
operator|.
name|getInFlightCount
argument_list|()
argument_list|)
expr_stmt|;
block|}
specifier|private
name|void
name|createBroker
parameter_list|()
throws|throws
name|Exception
block|{
name|broker
operator|=
operator|new
name|BrokerService
argument_list|()
expr_stmt|;
name|broker
operator|.
name|setUseJmx
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|broker
operator|.
name|setPersistent
argument_list|(
literal|false
argument_list|)
expr_stmt|;
name|broker
operator|.
name|setAdvisorySupport
argument_list|(
literal|false
argument_list|)
expr_stmt|;
name|broker
operator|.
name|addConnector
argument_list|(
literal|"tcp://localhost:0"
argument_list|)
expr_stmt|;
comment|// add a plugin to ensure the expiry happens on the client side the
comment|// acknowledge() reset the expiration time to 30 seconds in the future.
comment|//
comment|// this simulates a scenario where the client clock is *0 seconds ahead
comment|// of the broker's clock.
name|broker
operator|.
name|setPlugins
argument_list|(
operator|new
name|BrokerPlugin
index|[]
block|{
operator|new
name|BrokerPlugin
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|Broker
name|installPlugin
parameter_list|(
name|Broker
name|broker
parameter_list|)
throws|throws
name|Exception
block|{
return|return
operator|new
name|BrokerFilter
argument_list|(
name|broker
argument_list|)
block|{
specifier|private
name|AtomicInteger
name|counter
init|=
operator|new
name|AtomicInteger
argument_list|()
decl_stmt|;
specifier|private
name|org
operator|.
name|apache
operator|.
name|activemq
operator|.
name|command
operator|.
name|Message
name|dispatchedMessage
decl_stmt|;
annotation|@
name|Override
specifier|public
name|void
name|preProcessDispatch
parameter_list|(
name|MessageDispatch
name|messageDispatch
parameter_list|)
block|{
if|if
condition|(
name|counter
operator|.
name|get
argument_list|()
operator|==
literal|0
operator|&&
name|messageDispatch
operator|.
name|getDestination
argument_list|()
operator|.
name|getPhysicalName
argument_list|()
operator|.
name|contains
argument_list|(
literal|"timeout.test"
argument_list|)
condition|)
block|{
comment|// Set the expiration to now
name|dispatchedMessage
operator|=
name|messageDispatch
operator|.
name|getMessage
argument_list|()
expr_stmt|;
name|dispatchedMessage
operator|.
name|setExpiration
argument_list|(
name|System
operator|.
name|currentTimeMillis
argument_list|()
operator|-
literal|100
argument_list|)
expr_stmt|;
name|counter
operator|.
name|incrementAndGet
argument_list|()
expr_stmt|;
block|}
name|super
operator|.
name|preProcessDispatch
argument_list|(
name|messageDispatch
argument_list|)
argument_list|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|acknowledge
parameter_list|(
name|ConsumerBrokerExchange
name|consumerExchange
parameter_list|,
name|MessageAck
name|ack
parameter_list|)
throws|throws
name|Exception
block|{
comment|// set the expiration in the future, to simulate broker's clock is
comment|// 30 seconds behind client clock
if|if
condition|(
name|ack
operator|.
name|isExpiredAck
argument_list|()
condition|)
block|{
name|dispatchedMessage
operator|.
name|setExpiration
argument_list|(
name|System
operator|.
name|currentTimeMillis
argument_list|()
operator|+
literal|300000
argument_list|)
expr_stmt|;
block|}
name|super
operator|.
name|acknowledge
argument_list|(
name|consumerExchange
argument_list|,
name|ack
argument_list|)
expr_stmt|;
block|}
block|}
end_class

begin_empty_stmt
empty_stmt|;
end_empty_stmt

begin_empty_stmt
unit|}         } })
empty_stmt|;
end_empty_stmt

begin_expr_stmt
name|broker
operator|.
name|start
argument_list|()
expr_stmt|;
end_expr_stmt

begin_expr_stmt
name|broker
operator|.
name|waitUntilStarted
argument_list|()
expr_stmt|;
end_expr_stmt

begin_expr_stmt
name|connectionUri
operator|=
name|broker
operator|.
name|getTransportConnectors
argument_list|()
operator|.
name|get
argument_list|(
literal|0
argument_list|)
operator|.
name|getPublishableConnectString
argument_list|()
expr_stmt|;
end_expr_stmt

begin_function
unit|}      protected
name|ActiveMQConnectionFactory
name|createConnectionFactory
parameter_list|()
throws|throws
name|Exception
block|{
return|return
operator|new
name|ActiveMQConnectionFactory
argument_list|(
name|connectionUri
argument_list|)
return|;
block|}
end_function

begin_function
specifier|protected
name|ActiveMQConnection
name|createConnection
parameter_list|()
throws|throws
name|Exception
block|{
return|return
operator|(
name|ActiveMQConnection
operator|)
name|createConnectionFactory
argument_list|()
operator|.
name|createConnection
argument_list|()
return|;
block|}
end_function

begin_function
specifier|private
name|QueueView
name|getQueueView
parameter_list|(
name|BrokerService
name|broker
parameter_list|,
name|String
name|queueName
parameter_list|)
throws|throws
name|Exception
block|{
name|Map
argument_list|<
name|ObjectName
argument_list|,
name|DestinationView
argument_list|>
name|queueViews
init|=
name|broker
operator|.
name|getAdminView
argument_list|()
operator|.
name|getBroker
argument_list|()
operator|.
name|getQueueViews
argument_list|()
decl_stmt|;
for|for
control|(
name|ObjectName
name|key
range|:
name|queueViews
operator|.
name|keySet
argument_list|()
control|)
block|{
name|DestinationView
name|destinationView
init|=
name|queueViews
operator|.
name|get
argument_list|(
name|key
argument_list|)
decl_stmt|;
if|if
condition|(
name|destinationView
operator|instanceof
name|QueueView
condition|)
block|{
name|QueueView
name|queueView
init|=
operator|(
name|QueueView
operator|)
name|destinationView
decl_stmt|;
if|if
condition|(
name|queueView
operator|.
name|getName
argument_list|()
operator|.
name|equals
argument_list|(
name|queueName
argument_list|)
condition|)
block|{
return|return
name|queueView
return|;
block|}
block|}
block|}
return|return
literal|null
return|;
block|}
end_function

unit|}
end_unit

