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
name|usecases
package|;
end_package

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
name|command
operator|.
name|ActiveMQQueue
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
comment|/**  * @author Rajani Chennamaneni  */
end_comment

begin_class
specifier|public
class|class
name|DispatchMultipleConsumersTest
extends|extends
name|TestCase
block|{
specifier|private
specifier|final
specifier|static
name|Logger
name|logger
init|=
name|LoggerFactory
operator|.
name|getLogger
argument_list|(
name|DispatchMultipleConsumersTest
operator|.
name|class
argument_list|)
decl_stmt|;
name|BrokerService
name|broker
decl_stmt|;
name|Destination
name|dest
decl_stmt|;
name|String
name|destinationName
init|=
literal|"TEST.Q"
decl_stmt|;
name|String
name|msgStr
init|=
literal|"Test text message"
decl_stmt|;
name|int
name|messagesPerThread
init|=
literal|20
decl_stmt|;
name|int
name|producerThreads
init|=
literal|50
decl_stmt|;
name|int
name|consumerCount
init|=
literal|2
decl_stmt|;
name|AtomicInteger
name|sentCount
decl_stmt|;
name|AtomicInteger
name|consumedCount
decl_stmt|;
name|CountDownLatch
name|producerLatch
decl_stmt|;
name|CountDownLatch
name|consumerLatch
decl_stmt|;
name|String
name|brokerURL
decl_stmt|;
name|String
name|userName
init|=
literal|""
decl_stmt|;
name|String
name|password
init|=
literal|""
decl_stmt|;
annotation|@
name|Override
specifier|protected
name|void
name|setUp
parameter_list|()
throws|throws
name|Exception
block|{
name|super
operator|.
name|setUp
argument_list|()
expr_stmt|;
name|broker
operator|=
operator|new
name|BrokerService
argument_list|()
expr_stmt|;
name|broker
operator|.
name|setPersistent
argument_list|(
literal|true
argument_list|)
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
name|deleteAllMessages
argument_list|()
expr_stmt|;
name|broker
operator|.
name|addConnector
argument_list|(
literal|"tcp://localhost:0"
argument_list|)
expr_stmt|;
name|broker
operator|.
name|start
argument_list|()
expr_stmt|;
name|broker
operator|.
name|waitUntilStarted
argument_list|()
expr_stmt|;
name|dest
operator|=
operator|new
name|ActiveMQQueue
argument_list|(
name|destinationName
argument_list|)
expr_stmt|;
name|resetCounters
argument_list|()
expr_stmt|;
name|brokerURL
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
block|}
annotation|@
name|Override
specifier|protected
name|void
name|tearDown
parameter_list|()
throws|throws
name|Exception
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
name|super
operator|.
name|tearDown
argument_list|()
expr_stmt|;
block|}
specifier|private
name|void
name|resetCounters
parameter_list|()
block|{
name|sentCount
operator|=
operator|new
name|AtomicInteger
argument_list|(
literal|0
argument_list|)
expr_stmt|;
name|consumedCount
operator|=
operator|new
name|AtomicInteger
argument_list|(
literal|0
argument_list|)
expr_stmt|;
name|producerLatch
operator|=
operator|new
name|CountDownLatch
argument_list|(
name|producerThreads
argument_list|)
expr_stmt|;
name|consumerLatch
operator|=
operator|new
name|CountDownLatch
argument_list|(
name|consumerCount
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|testDispatch1
parameter_list|()
block|{
for|for
control|(
name|int
name|i
init|=
literal|1
init|;
name|i
operator|<=
literal|5
condition|;
name|i
operator|++
control|)
block|{
name|resetCounters
argument_list|()
expr_stmt|;
name|dispatch
argument_list|()
expr_stmt|;
name|assertEquals
argument_list|(
literal|"Incorrect messages in Iteration "
operator|+
name|i
argument_list|,
name|sentCount
operator|.
name|get
argument_list|()
argument_list|,
name|consumedCount
operator|.
name|get
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
specifier|private
name|void
name|dispatch
parameter_list|()
block|{
name|startConsumers
argument_list|()
expr_stmt|;
name|startProducers
argument_list|()
expr_stmt|;
try|try
block|{
name|producerLatch
operator|.
name|await
argument_list|()
expr_stmt|;
name|consumerLatch
operator|.
name|await
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|InterruptedException
name|e
parameter_list|)
block|{
name|fail
argument_list|(
literal|"test interrupted!"
argument_list|)
expr_stmt|;
block|}
block|}
specifier|private
name|void
name|startConsumers
parameter_list|()
block|{
name|ActiveMQConnectionFactory
name|connFactory
init|=
operator|new
name|ActiveMQConnectionFactory
argument_list|(
name|userName
argument_list|,
name|password
argument_list|,
name|brokerURL
argument_list|)
decl_stmt|;
name|Connection
name|conn
decl_stmt|;
try|try
block|{
name|conn
operator|=
name|connFactory
operator|.
name|createConnection
argument_list|()
expr_stmt|;
name|conn
operator|.
name|start
argument_list|()
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
name|consumerCount
condition|;
name|i
operator|++
control|)
block|{
operator|new
name|ConsumerThread
argument_list|(
name|conn
argument_list|,
literal|"ConsumerThread"
operator|+
name|i
argument_list|)
expr_stmt|;
block|}
block|}
catch|catch
parameter_list|(
name|JMSException
name|e
parameter_list|)
block|{
name|logger
operator|.
name|error
argument_list|(
literal|"Failed to start consumers"
argument_list|,
name|e
argument_list|)
expr_stmt|;
block|}
block|}
specifier|private
name|void
name|startProducers
parameter_list|()
block|{
name|ActiveMQConnectionFactory
name|connFactory
init|=
operator|new
name|ActiveMQConnectionFactory
argument_list|(
name|userName
argument_list|,
name|password
argument_list|,
name|brokerURL
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
name|producerThreads
condition|;
name|i
operator|++
control|)
block|{
operator|new
name|ProducerThread
argument_list|(
name|connFactory
argument_list|,
name|messagesPerThread
argument_list|,
literal|"ProducerThread"
operator|+
name|i
argument_list|)
expr_stmt|;
block|}
block|}
specifier|private
class|class
name|ConsumerThread
extends|extends
name|Thread
block|{
name|Session
name|session
decl_stmt|;
name|MessageConsumer
name|consumer
decl_stmt|;
specifier|public
name|ConsumerThread
parameter_list|(
name|Connection
name|conn
parameter_list|,
name|String
name|name
parameter_list|)
block|{
name|super
argument_list|()
expr_stmt|;
name|this
operator|.
name|setName
argument_list|(
name|name
argument_list|)
expr_stmt|;
name|logger
operator|.
name|trace
argument_list|(
literal|"Created new consumer thread:"
operator|+
name|name
argument_list|)
expr_stmt|;
try|try
block|{
name|session
operator|=
name|conn
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
name|consumer
operator|=
name|session
operator|.
name|createConsumer
argument_list|(
name|dest
argument_list|)
expr_stmt|;
name|start
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|JMSException
name|e
parameter_list|)
block|{
name|logger
operator|.
name|error
argument_list|(
literal|"Failed to start consumer thread:"
operator|+
name|name
argument_list|,
name|e
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Override
specifier|public
name|void
name|run
parameter_list|()
block|{
name|int
name|msgCount
init|=
literal|0
decl_stmt|;
name|int
name|nullCount
init|=
literal|0
decl_stmt|;
while|while
condition|(
literal|true
condition|)
block|{
try|try
block|{
name|Message
name|msg
init|=
name|consumer
operator|.
name|receive
argument_list|(
literal|1000
argument_list|)
decl_stmt|;
if|if
condition|(
name|msg
operator|==
literal|null
condition|)
block|{
if|if
condition|(
name|producerLatch
operator|.
name|getCount
argument_list|()
operator|>
literal|0
condition|)
block|{
continue|continue;
block|}
name|nullCount
operator|++
expr_stmt|;
if|if
condition|(
name|nullCount
operator|>
literal|10
condition|)
block|{
comment|//assume that we are not getting any more messages
break|break;
block|}
else|else
block|{
continue|continue;
block|}
block|}
else|else
block|{
name|nullCount
operator|=
literal|0
expr_stmt|;
block|}
name|Thread
operator|.
name|sleep
argument_list|(
literal|100
argument_list|)
expr_stmt|;
if|if
condition|(
name|logger
operator|.
name|isTraceEnabled
argument_list|()
condition|)
block|{
name|logger
operator|.
name|trace
argument_list|(
literal|"Message received:"
operator|+
name|msg
operator|.
name|getJMSMessageID
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|msgCount
operator|++
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|JMSException
name|e
parameter_list|)
block|{
name|logger
operator|.
name|error
argument_list|(
literal|"Failed to consume:"
argument_list|,
name|e
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|InterruptedException
name|e
parameter_list|)
block|{
name|logger
operator|.
name|error
argument_list|(
literal|"Interrupted!"
argument_list|,
name|e
argument_list|)
expr_stmt|;
block|}
block|}
try|try
block|{
name|consumer
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
name|logger
operator|.
name|error
argument_list|(
literal|"Failed to close consumer "
operator|+
name|getName
argument_list|()
argument_list|,
name|e
argument_list|)
expr_stmt|;
block|}
name|consumedCount
operator|.
name|addAndGet
argument_list|(
name|msgCount
argument_list|)
expr_stmt|;
name|consumerLatch
operator|.
name|countDown
argument_list|()
expr_stmt|;
name|logger
operator|.
name|trace
argument_list|(
literal|"Consumed "
operator|+
name|msgCount
operator|+
literal|" messages using thread "
operator|+
name|getName
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
specifier|private
class|class
name|ProducerThread
extends|extends
name|Thread
block|{
name|int
name|count
decl_stmt|;
name|Connection
name|conn
decl_stmt|;
name|Session
name|session
decl_stmt|;
name|MessageProducer
name|producer
decl_stmt|;
specifier|public
name|ProducerThread
parameter_list|(
name|ActiveMQConnectionFactory
name|connFactory
parameter_list|,
name|int
name|count
parameter_list|,
name|String
name|name
parameter_list|)
block|{
name|super
argument_list|()
expr_stmt|;
name|this
operator|.
name|count
operator|=
name|count
expr_stmt|;
name|this
operator|.
name|setName
argument_list|(
name|name
argument_list|)
expr_stmt|;
name|logger
operator|.
name|trace
argument_list|(
literal|"Created new producer thread:"
operator|+
name|name
argument_list|)
expr_stmt|;
try|try
block|{
name|conn
operator|=
name|connFactory
operator|.
name|createConnection
argument_list|()
expr_stmt|;
name|conn
operator|.
name|start
argument_list|()
expr_stmt|;
name|session
operator|=
name|conn
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
name|producer
operator|=
name|session
operator|.
name|createProducer
argument_list|(
name|dest
argument_list|)
expr_stmt|;
name|start
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|JMSException
name|e
parameter_list|)
block|{
name|logger
operator|.
name|error
argument_list|(
literal|"Failed to start producer thread:"
operator|+
name|name
argument_list|,
name|e
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Override
specifier|public
name|void
name|run
parameter_list|()
block|{
name|int
name|i
init|=
literal|0
decl_stmt|;
try|try
block|{
for|for
control|(
init|;
name|i
operator|<
name|count
condition|;
name|i
operator|++
control|)
block|{
name|producer
operator|.
name|send
argument_list|(
name|session
operator|.
name|createTextMessage
argument_list|(
name|msgStr
argument_list|)
argument_list|)
expr_stmt|;
name|Thread
operator|.
name|sleep
argument_list|(
literal|500
argument_list|)
expr_stmt|;
block|}
name|conn
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
name|logger
operator|.
name|error
argument_list|(
name|e
operator|.
name|getMessage
argument_list|()
argument_list|,
name|e
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|InterruptedException
name|e
parameter_list|)
block|{
name|logger
operator|.
name|error
argument_list|(
literal|"Interrupted!"
argument_list|,
name|e
argument_list|)
expr_stmt|;
block|}
name|sentCount
operator|.
name|addAndGet
argument_list|(
name|i
argument_list|)
expr_stmt|;
name|producerLatch
operator|.
name|countDown
argument_list|()
expr_stmt|;
if|if
condition|(
name|logger
operator|.
name|isTraceEnabled
argument_list|()
condition|)
block|{
name|logger
operator|.
name|trace
argument_list|(
literal|"Sent "
operator|+
name|i
operator|+
literal|" messages from thread "
operator|+
name|getName
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|}
end_class

end_unit

