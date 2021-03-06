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
name|Callable
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
name|Semaphore
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
comment|/**  * Benchmarks the broker by starting many consumer and producers against the  * same destination. Make sure you run with jvm option -server (makes a big  * difference). The tests simulate storing 1000 1k jms messages to see the rate  * of processing msg/sec.  *  *  */
end_comment

begin_class
specifier|public
class|class
name|JmsBenchmark
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
name|JmsBenchmark
operator|.
name|class
argument_list|)
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|long
name|SAMPLE_DELAY
init|=
name|Integer
operator|.
name|parseInt
argument_list|(
name|System
operator|.
name|getProperty
argument_list|(
literal|"SAMPLE_DELAY"
argument_list|,
literal|""
operator|+
literal|1000
operator|*
literal|5
argument_list|)
argument_list|)
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|long
name|SAMPLES
init|=
name|Integer
operator|.
name|parseInt
argument_list|(
name|System
operator|.
name|getProperty
argument_list|(
literal|"SAMPLES"
argument_list|,
literal|"10"
argument_list|)
argument_list|)
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|long
name|SAMPLE_DURATION
init|=
name|Integer
operator|.
name|parseInt
argument_list|(
name|System
operator|.
name|getProperty
argument_list|(
literal|"SAMPLES_DURATION"
argument_list|,
literal|""
operator|+
literal|1000
operator|*
literal|60
argument_list|)
argument_list|)
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|int
name|PRODUCER_COUNT
init|=
name|Integer
operator|.
name|parseInt
argument_list|(
name|System
operator|.
name|getProperty
argument_list|(
literal|"PRODUCER_COUNT"
argument_list|,
literal|"10"
argument_list|)
argument_list|)
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|int
name|CONSUMER_COUNT
init|=
name|Integer
operator|.
name|parseInt
argument_list|(
name|System
operator|.
name|getProperty
argument_list|(
literal|"CONSUMER_COUNT"
argument_list|,
literal|"10"
argument_list|)
argument_list|)
decl_stmt|;
specifier|public
name|ActiveMQDestination
name|destination
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
name|JmsBenchmark
operator|.
name|class
argument_list|)
return|;
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
name|JmsBenchmark
operator|.
name|class
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|initCombos
parameter_list|()
block|{
name|addCombinationValues
argument_list|(
literal|"destination"
argument_list|,
operator|new
name|Object
index|[]
block|{
operator|new
name|ActiveMQQueue
argument_list|(
literal|"TEST"
argument_list|)
block|}
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
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
literal|"broker://(tcp://localhost:0)?persistent=false"
argument_list|)
argument_list|)
return|;
block|}
annotation|@
name|Override
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
name|getServer
argument_list|()
operator|.
name|getConnectURI
argument_list|()
argument_list|)
return|;
block|}
comment|/**      * @throws Throwable      */
specifier|public
name|void
name|testConcurrentSendReceive
parameter_list|()
throws|throws
name|Throwable
block|{
specifier|final
name|Semaphore
name|connectionsEstablished
init|=
operator|new
name|Semaphore
argument_list|(
literal|1
operator|-
operator|(
name|CONSUMER_COUNT
operator|+
name|PRODUCER_COUNT
operator|)
argument_list|)
decl_stmt|;
specifier|final
name|Semaphore
name|workerDone
init|=
operator|new
name|Semaphore
argument_list|(
literal|1
operator|-
operator|(
name|CONSUMER_COUNT
operator|+
name|PRODUCER_COUNT
operator|)
argument_list|)
decl_stmt|;
specifier|final
name|CountDownLatch
name|sampleTimeDone
init|=
operator|new
name|CountDownLatch
argument_list|(
literal|1
argument_list|)
decl_stmt|;
specifier|final
name|AtomicInteger
name|producedMessages
init|=
operator|new
name|AtomicInteger
argument_list|(
literal|0
argument_list|)
decl_stmt|;
specifier|final
name|AtomicInteger
name|receivedMessages
init|=
operator|new
name|AtomicInteger
argument_list|(
literal|0
argument_list|)
decl_stmt|;
specifier|final
name|Callable
argument_list|<
name|Object
argument_list|>
name|producer
init|=
operator|new
name|Callable
argument_list|<
name|Object
argument_list|>
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|Object
name|call
parameter_list|()
throws|throws
name|JMSException
throws|,
name|InterruptedException
block|{
name|Connection
name|connection
init|=
name|factory
operator|.
name|createConnection
argument_list|()
decl_stmt|;
name|connections
operator|.
name|add
argument_list|(
name|connection
argument_list|)
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
name|DeliveryMode
operator|.
name|NON_PERSISTENT
argument_list|)
expr_stmt|;
name|BytesMessage
name|message
init|=
name|session
operator|.
name|createBytesMessage
argument_list|()
decl_stmt|;
name|message
operator|.
name|writeBytes
argument_list|(
operator|new
name|byte
index|[
literal|1024
index|]
argument_list|)
expr_stmt|;
name|connection
operator|.
name|start
argument_list|()
expr_stmt|;
name|connectionsEstablished
operator|.
name|release
argument_list|()
expr_stmt|;
while|while
condition|(
operator|!
name|sampleTimeDone
operator|.
name|await
argument_list|(
literal|0
argument_list|,
name|TimeUnit
operator|.
name|MILLISECONDS
argument_list|)
condition|)
block|{
name|producer
operator|.
name|send
argument_list|(
name|message
argument_list|)
expr_stmt|;
name|producedMessages
operator|.
name|incrementAndGet
argument_list|()
expr_stmt|;
block|}
name|connection
operator|.
name|close
argument_list|()
expr_stmt|;
name|workerDone
operator|.
name|release
argument_list|()
expr_stmt|;
return|return
literal|null
return|;
block|}
block|}
decl_stmt|;
specifier|final
name|Callable
argument_list|<
name|Object
argument_list|>
name|consumer
init|=
operator|new
name|Callable
argument_list|<
name|Object
argument_list|>
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|Object
name|call
parameter_list|()
throws|throws
name|JMSException
throws|,
name|InterruptedException
block|{
name|Connection
name|connection
init|=
name|factory
operator|.
name|createConnection
argument_list|()
decl_stmt|;
name|connections
operator|.
name|add
argument_list|(
name|connection
argument_list|)
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
name|MessageConsumer
name|consumer
init|=
name|session
operator|.
name|createConsumer
argument_list|(
name|destination
argument_list|)
decl_stmt|;
name|consumer
operator|.
name|setMessageListener
argument_list|(
operator|new
name|MessageListener
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|void
name|onMessage
parameter_list|(
name|Message
name|msg
parameter_list|)
block|{
name|receivedMessages
operator|.
name|incrementAndGet
argument_list|()
expr_stmt|;
block|}
block|}
argument_list|)
expr_stmt|;
name|connection
operator|.
name|start
argument_list|()
expr_stmt|;
name|connectionsEstablished
operator|.
name|release
argument_list|()
expr_stmt|;
name|sampleTimeDone
operator|.
name|await
argument_list|()
expr_stmt|;
name|connection
operator|.
name|close
argument_list|()
expr_stmt|;
name|workerDone
operator|.
name|release
argument_list|()
expr_stmt|;
return|return
literal|null
return|;
block|}
block|}
decl_stmt|;
specifier|final
name|Throwable
name|workerError
index|[]
init|=
operator|new
name|Throwable
index|[
literal|1
index|]
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
name|PRODUCER_COUNT
condition|;
name|i
operator|++
control|)
block|{
operator|new
name|Thread
argument_list|(
literal|"Producer:"
operator|+
name|i
argument_list|)
block|{
annotation|@
name|Override
specifier|public
name|void
name|run
parameter_list|()
block|{
try|try
block|{
name|producer
operator|.
name|call
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
argument_list|()
expr_stmt|;
name|workerError
index|[
literal|0
index|]
operator|=
name|e
expr_stmt|;
block|}
block|}
block|}
operator|.
name|start
argument_list|()
expr_stmt|;
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
name|CONSUMER_COUNT
condition|;
name|i
operator|++
control|)
block|{
operator|new
name|Thread
argument_list|(
literal|"Consumer:"
operator|+
name|i
argument_list|)
block|{
annotation|@
name|Override
specifier|public
name|void
name|run
parameter_list|()
block|{
try|try
block|{
name|consumer
operator|.
name|call
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
argument_list|()
expr_stmt|;
name|workerError
index|[
literal|0
index|]
operator|=
name|e
expr_stmt|;
block|}
block|}
block|}
operator|.
name|start
argument_list|()
expr_stmt|;
block|}
name|LOG
operator|.
name|info
argument_list|(
name|getName
argument_list|()
operator|+
literal|": Waiting for Producers and Consumers to startup."
argument_list|)
expr_stmt|;
name|connectionsEstablished
operator|.
name|acquire
argument_list|()
expr_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"Producers and Consumers are now running.  Waiting for system to reach steady state: "
operator|+
operator|(
name|SAMPLE_DELAY
operator|/
literal|1000.0f
operator|)
operator|+
literal|" seconds"
argument_list|)
expr_stmt|;
name|Thread
operator|.
name|sleep
argument_list|(
literal|1000
operator|*
literal|10
argument_list|)
expr_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"Starting sample: "
operator|+
name|SAMPLES
operator|+
literal|" each lasting "
operator|+
operator|(
name|SAMPLE_DURATION
operator|/
literal|1000.0f
operator|)
operator|+
literal|" seconds"
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
name|SAMPLES
condition|;
name|i
operator|++
control|)
block|{
name|long
name|start
init|=
name|System
operator|.
name|currentTimeMillis
argument_list|()
decl_stmt|;
name|producedMessages
operator|.
name|set
argument_list|(
literal|0
argument_list|)
expr_stmt|;
name|receivedMessages
operator|.
name|set
argument_list|(
literal|0
argument_list|)
expr_stmt|;
name|Thread
operator|.
name|sleep
argument_list|(
name|SAMPLE_DURATION
argument_list|)
expr_stmt|;
name|long
name|end
init|=
name|System
operator|.
name|currentTimeMillis
argument_list|()
decl_stmt|;
name|int
name|r
init|=
name|receivedMessages
operator|.
name|get
argument_list|()
decl_stmt|;
name|int
name|p
init|=
name|producedMessages
operator|.
name|get
argument_list|()
decl_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"published: "
operator|+
name|p
operator|+
literal|" msgs at "
operator|+
operator|(
name|p
operator|*
literal|1000f
operator|/
operator|(
name|end
operator|-
name|start
operator|)
operator|)
operator|+
literal|" msgs/sec, "
operator|+
literal|"consumed: "
operator|+
name|r
operator|+
literal|" msgs at "
operator|+
operator|(
name|r
operator|*
literal|1000f
operator|/
operator|(
name|end
operator|-
name|start
operator|)
operator|)
operator|+
literal|" msgs/sec"
argument_list|)
expr_stmt|;
block|}
name|LOG
operator|.
name|info
argument_list|(
literal|"Sample done."
argument_list|)
expr_stmt|;
name|sampleTimeDone
operator|.
name|countDown
argument_list|()
expr_stmt|;
name|workerDone
operator|.
name|acquire
argument_list|()
expr_stmt|;
if|if
condition|(
name|workerError
index|[
literal|0
index|]
operator|!=
literal|null
condition|)
block|{
throw|throw
name|workerError
index|[
literal|0
index|]
throw|;
block|}
block|}
block|}
end_class

end_unit

