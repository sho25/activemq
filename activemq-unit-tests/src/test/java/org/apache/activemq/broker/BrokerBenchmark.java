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
name|broker
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
name|atomic
operator|.
name|AtomicInteger
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
name|ConnectionInfo
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
name|command
operator|.
name|Message
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
name|ProducerInfo
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
name|SessionInfo
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
comment|/**  * BrokerBenchmark is used to get an idea of the raw performance of a broker.  * Since the broker data structures using in message dispatching are under high  * contention from client requests, it's performance should be monitored closely  * since it typically is the biggest bottleneck in a high performance messaging  * fabric. The benchmarks are run under all the following combinations options:  * Queue vs. Topic, 1 vs. 10 producer threads, 1 vs. 10 consumer threads, and  * Persistent vs. Non-Persistent messages. Message Acking uses client ack style  * batch acking since that typically has the best ack performance.  *   *   */
end_comment

begin_class
specifier|public
class|class
name|BrokerBenchmark
extends|extends
name|BrokerTestSupport
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
name|BrokerBenchmark
operator|.
name|class
argument_list|)
decl_stmt|;
specifier|public
name|int
name|produceCount
init|=
name|Integer
operator|.
name|parseInt
argument_list|(
name|System
operator|.
name|getProperty
argument_list|(
literal|"PRODUCE_COUNT"
argument_list|,
literal|"10000"
argument_list|)
argument_list|)
decl_stmt|;
specifier|public
name|ActiveMQDestination
name|destination
decl_stmt|;
specifier|public
name|int
name|prodcuerCount
decl_stmt|;
specifier|public
name|int
name|consumerCount
decl_stmt|;
specifier|public
name|boolean
name|deliveryMode
decl_stmt|;
specifier|public
name|void
name|initCombosForTestPerformance
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
block|,
operator|new
name|ActiveMQTopic
argument_list|(
literal|"TEST"
argument_list|)
block|}
argument_list|)
expr_stmt|;
name|addCombinationValues
argument_list|(
literal|"PRODUCER_COUNT"
argument_list|,
operator|new
name|Object
index|[]
block|{
operator|new
name|Integer
argument_list|(
literal|"1"
argument_list|)
block|,
operator|new
name|Integer
argument_list|(
literal|"10"
argument_list|)
block|}
argument_list|)
expr_stmt|;
name|addCombinationValues
argument_list|(
literal|"CONSUMER_COUNT"
argument_list|,
operator|new
name|Object
index|[]
block|{
operator|new
name|Integer
argument_list|(
literal|"1"
argument_list|)
block|,
operator|new
name|Integer
argument_list|(
literal|"10"
argument_list|)
block|}
argument_list|)
expr_stmt|;
name|addCombinationValues
argument_list|(
literal|"CONSUMER_COUNT"
argument_list|,
operator|new
name|Object
index|[]
block|{
operator|new
name|Integer
argument_list|(
literal|"1"
argument_list|)
block|,
operator|new
name|Integer
argument_list|(
literal|"10"
argument_list|)
block|}
argument_list|)
expr_stmt|;
name|addCombinationValues
argument_list|(
literal|"deliveryMode"
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
block|}
specifier|public
name|void
name|testPerformance
parameter_list|()
throws|throws
name|Exception
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"Running Benchmark for destination="
operator|+
name|destination
operator|+
literal|", producers="
operator|+
name|prodcuerCount
operator|+
literal|", consumers="
operator|+
name|consumerCount
operator|+
literal|", deliveryMode="
operator|+
name|deliveryMode
argument_list|)
expr_stmt|;
specifier|final
name|int
name|consumeCount
init|=
name|destination
operator|.
name|isTopic
argument_list|()
condition|?
name|consumerCount
operator|*
name|produceCount
else|:
name|produceCount
decl_stmt|;
specifier|final
name|Semaphore
name|consumersStarted
init|=
operator|new
name|Semaphore
argument_list|(
literal|1
operator|-
name|consumerCount
argument_list|)
decl_stmt|;
specifier|final
name|Semaphore
name|producersFinished
init|=
operator|new
name|Semaphore
argument_list|(
literal|1
operator|-
name|prodcuerCount
argument_list|)
decl_stmt|;
specifier|final
name|Semaphore
name|consumersFinished
init|=
operator|new
name|Semaphore
argument_list|(
literal|1
operator|-
name|consumerCount
argument_list|)
decl_stmt|;
specifier|final
name|ProgressPrinter
name|printer
init|=
operator|new
name|ProgressPrinter
argument_list|(
name|produceCount
operator|+
name|consumeCount
argument_list|,
literal|10
argument_list|)
decl_stmt|;
comment|// Start a producer and consumer
name|profilerPause
argument_list|(
literal|"Benchmark ready.  Start profiler "
argument_list|)
expr_stmt|;
name|long
name|start
init|=
name|System
operator|.
name|currentTimeMillis
argument_list|()
decl_stmt|;
specifier|final
name|AtomicInteger
name|receiveCounter
init|=
operator|new
name|AtomicInteger
argument_list|(
literal|0
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
name|consumerCount
condition|;
name|i
operator|++
control|)
block|{
operator|new
name|Thread
argument_list|()
block|{
specifier|public
name|void
name|run
parameter_list|()
block|{
try|try
block|{
comment|// Consume the messages
name|StubConnection
name|connection
init|=
operator|new
name|StubConnection
argument_list|(
name|broker
argument_list|)
decl_stmt|;
name|ConnectionInfo
name|connectionInfo
init|=
name|createConnectionInfo
argument_list|()
decl_stmt|;
name|connection
operator|.
name|send
argument_list|(
name|connectionInfo
argument_list|)
expr_stmt|;
name|SessionInfo
name|sessionInfo
init|=
name|createSessionInfo
argument_list|(
name|connectionInfo
argument_list|)
decl_stmt|;
name|ConsumerInfo
name|consumerInfo
init|=
name|createConsumerInfo
argument_list|(
name|sessionInfo
argument_list|,
name|destination
argument_list|)
decl_stmt|;
name|consumerInfo
operator|.
name|setPrefetchSize
argument_list|(
literal|1000
argument_list|)
expr_stmt|;
name|connection
operator|.
name|send
argument_list|(
name|sessionInfo
argument_list|)
expr_stmt|;
name|connection
operator|.
name|send
argument_list|(
name|consumerInfo
argument_list|)
expr_stmt|;
name|consumersStarted
operator|.
name|release
argument_list|()
expr_stmt|;
while|while
condition|(
name|receiveCounter
operator|.
name|get
argument_list|()
operator|<
name|consumeCount
condition|)
block|{
name|int
name|counter
init|=
literal|0
decl_stmt|;
comment|// Get a least 1 message.
name|Message
name|msg
init|=
name|receiveMessage
argument_list|(
name|connection
argument_list|,
literal|2000
argument_list|)
decl_stmt|;
if|if
condition|(
name|msg
operator|!=
literal|null
condition|)
block|{
name|printer
operator|.
name|increment
argument_list|()
expr_stmt|;
name|receiveCounter
operator|.
name|incrementAndGet
argument_list|()
expr_stmt|;
name|counter
operator|++
expr_stmt|;
comment|// Try to piggy back a few extra message acks if
comment|// they are ready.
name|Message
name|extra
init|=
literal|null
decl_stmt|;
while|while
condition|(
operator|(
name|extra
operator|=
name|receiveMessage
argument_list|(
name|connection
argument_list|,
literal|0
argument_list|)
operator|)
operator|!=
literal|null
condition|)
block|{
name|msg
operator|=
name|extra
expr_stmt|;
name|printer
operator|.
name|increment
argument_list|()
expr_stmt|;
name|receiveCounter
operator|.
name|incrementAndGet
argument_list|()
expr_stmt|;
name|counter
operator|++
expr_stmt|;
block|}
block|}
if|if
condition|(
name|msg
operator|!=
literal|null
condition|)
block|{
name|connection
operator|.
name|send
argument_list|(
name|createAck
argument_list|(
name|consumerInfo
argument_list|,
name|msg
argument_list|,
name|counter
argument_list|,
name|MessageAck
operator|.
name|STANDARD_ACK_TYPE
argument_list|)
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|receiveCounter
operator|.
name|get
argument_list|()
operator|<
name|consumeCount
condition|)
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"Consumer stall, waiting for message #"
operator|+
name|receiveCounter
operator|.
name|get
argument_list|()
operator|+
literal|1
argument_list|)
expr_stmt|;
block|}
block|}
name|connection
operator|.
name|send
argument_list|(
name|closeConsumerInfo
argument_list|(
name|consumerInfo
argument_list|)
argument_list|)
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
block|}
finally|finally
block|{
name|consumersFinished
operator|.
name|release
argument_list|()
expr_stmt|;
block|}
block|}
block|}
operator|.
name|start
argument_list|()
expr_stmt|;
block|}
comment|// Make sure that the consumers are started first to avoid sending
comment|// messages
comment|// before a topic is subscribed so that those messages are not missed.
name|consumersStarted
operator|.
name|acquire
argument_list|()
expr_stmt|;
comment|// Send the messages in an async thread.
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|prodcuerCount
condition|;
name|i
operator|++
control|)
block|{
operator|new
name|Thread
argument_list|()
block|{
specifier|public
name|void
name|run
parameter_list|()
block|{
try|try
block|{
name|StubConnection
name|connection
init|=
operator|new
name|StubConnection
argument_list|(
name|broker
argument_list|)
decl_stmt|;
name|ConnectionInfo
name|connectionInfo
init|=
name|createConnectionInfo
argument_list|()
decl_stmt|;
name|connection
operator|.
name|send
argument_list|(
name|connectionInfo
argument_list|)
expr_stmt|;
name|SessionInfo
name|sessionInfo
init|=
name|createSessionInfo
argument_list|(
name|connectionInfo
argument_list|)
decl_stmt|;
name|ProducerInfo
name|producerInfo
init|=
name|createProducerInfo
argument_list|(
name|sessionInfo
argument_list|)
decl_stmt|;
name|connection
operator|.
name|send
argument_list|(
name|sessionInfo
argument_list|)
expr_stmt|;
name|connection
operator|.
name|send
argument_list|(
name|producerInfo
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
name|produceCount
operator|/
name|prodcuerCount
condition|;
name|i
operator|++
control|)
block|{
name|Message
name|message
init|=
name|createMessage
argument_list|(
name|producerInfo
argument_list|,
name|destination
argument_list|)
decl_stmt|;
name|message
operator|.
name|setPersistent
argument_list|(
name|deliveryMode
argument_list|)
expr_stmt|;
name|message
operator|.
name|setResponseRequired
argument_list|(
literal|false
argument_list|)
expr_stmt|;
name|connection
operator|.
name|send
argument_list|(
name|message
argument_list|)
expr_stmt|;
name|printer
operator|.
name|increment
argument_list|()
expr_stmt|;
block|}
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
block|}
finally|finally
block|{
name|producersFinished
operator|.
name|release
argument_list|()
expr_stmt|;
block|}
block|}
empty_stmt|;
block|}
operator|.
name|start
argument_list|()
expr_stmt|;
block|}
name|producersFinished
operator|.
name|acquire
argument_list|()
expr_stmt|;
name|long
name|end1
init|=
name|System
operator|.
name|currentTimeMillis
argument_list|()
decl_stmt|;
name|consumersFinished
operator|.
name|acquire
argument_list|()
expr_stmt|;
name|long
name|end2
init|=
name|System
operator|.
name|currentTimeMillis
argument_list|()
decl_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"Results for destination="
operator|+
name|destination
operator|+
literal|", producers="
operator|+
name|prodcuerCount
operator|+
literal|", consumers="
operator|+
name|consumerCount
operator|+
literal|", deliveryMode="
operator|+
name|deliveryMode
argument_list|)
expr_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"Produced at messages/sec: "
operator|+
operator|(
name|produceCount
operator|*
literal|1000.0
operator|/
operator|(
name|end1
operator|-
name|start
operator|)
operator|)
argument_list|)
expr_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"Consumed at messages/sec: "
operator|+
operator|(
name|consumeCount
operator|*
literal|1000.0
operator|/
operator|(
name|end2
operator|-
name|start
operator|)
operator|)
argument_list|)
expr_stmt|;
name|profilerPause
argument_list|(
literal|"Benchmark done.  Stop profiler "
argument_list|)
expr_stmt|;
block|}
specifier|public
specifier|static
name|Test
name|suite
parameter_list|()
block|{
return|return
name|suite
argument_list|(
name|BrokerBenchmark
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
name|suite
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

