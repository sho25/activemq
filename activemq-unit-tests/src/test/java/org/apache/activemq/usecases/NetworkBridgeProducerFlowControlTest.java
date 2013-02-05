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
name|Vector
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
name|java
operator|.
name|util
operator|.
name|concurrent
operator|.
name|atomic
operator|.
name|AtomicLong
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
name|JmsMultipleBrokersTestSupport
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
name|region
operator|.
name|policy
operator|.
name|PolicyEntry
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
name|region
operator|.
name|policy
operator|.
name|PolicyMap
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
name|network
operator|.
name|NetworkConnector
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

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|Assert
import|;
end_import

begin_comment
comment|/**  * This test demonstrates and verifies the behaviour of a network bridge when it  * forwards a message to a queue that is full and producer flow control is  * enabled.  *<p/>  * The expected behaviour is that the bridge will stop forwarding messages to  * the full queue once the associated demand consumer's prefetch is full, but  * will continue to forward messages to the other queues that are not full.  *<p/>  * In actuality, a message that is sent<b>asynchronously</b> to a local queue,  * but blocked by producer flow control on the remote queue, will stop the  * bridge from forwarding all subsequent messages, even those destined for  * remote queues that are not full. In the same scenario, but with a message  * that is sent<b>synchronously</b> to the local queue, the bridge continues  * forwarding messages to remote queues that are not full.  *<p/>  * This test demonstrates the differing behaviour via the following scenario:  *<ul>  *<li>broker0, designated as the local broker, produces messages to two shared  * queues  *<li>broker1, designated as the remote broker, has two consumers: the first  * consumes from one of the shared queues as fast as possible, the second  * consumes from the other shared queue with an artificial processing delay for  * each message  *<li>broker0 forwards messages to broker1 over a TCP-based network bridge  * with a demand consumer prefetch of 1  *<li>broker1's consumers have a prefetch of 1  *<li>broker1's "slow consumer" queue has a memory limit that triggers  * producer flow control once the queue contains a small number of messages  *</ul>  * In this scenario, since broker1's consumers have a prefetch of 1, the "slow  * consumer" queue will quickly become full and trigger producer flow control.  * The "fast consumer" queue is unlikely to become full. Since producer flow  * control on the "slow consumer" queue should not affect the "fast consumer"  * queue, the expectation is that the fast consumer in broker1 will finish  * processing all its messages well ahead of the slow consumer.  *<p/>  * The difference between expected and actual behaviour is demonstrated by  * changing the messages produced by broker0 from persistent to non-persistent.  * With persistent messages, broker0 dispatches synchronously and the expected  * behaviour is observed (i.e., the fast consumer on broker1 is much faster than  * the slow consumer). With non-persistent messages, broker0 dispatches  * asynchronously and the expected behaviour is<b>not</b> observed (i.e., the  * fast consumer is only marginally faster than the slow consumer).  *<p/>  * Since the expected behaviour may be desirable for both persistent and  * non-persistent messages, this test also demonstrates an enhancement to the  * network bridge configuration:<tt>isAlwaysSendSync</tt>. When false the  * bridge operates as originally observed. When<tt>true</tt>, the bridge  * operates with the same behaviour as was originally observed with persistent  * messages, for both persistent and non-persistent messages.  *<p/>  * https://issues.apache.org/jira/browse/AMQ-3331  *  * @author schow  */
end_comment

begin_class
specifier|public
class|class
name|NetworkBridgeProducerFlowControlTest
extends|extends
name|JmsMultipleBrokersTestSupport
block|{
comment|// Protect against hanging test.
specifier|private
specifier|static
specifier|final
name|long
name|MAX_TEST_TIME
init|=
literal|120000
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|Log
name|LOG
init|=
name|LogFactory
operator|.
name|getLog
argument_list|(
name|NetworkBridgeProducerFlowControlTest
operator|.
name|class
argument_list|)
decl_stmt|;
comment|// Combo flag set to true/false by the test framework.
specifier|public
name|boolean
name|persistentTestMessages
decl_stmt|;
specifier|public
name|boolean
name|networkIsAlwaysSendSync
decl_stmt|;
specifier|private
name|Vector
argument_list|<
name|Throwable
argument_list|>
name|exceptions
init|=
operator|new
name|Vector
argument_list|<
name|Throwable
argument_list|>
argument_list|()
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
name|NetworkBridgeProducerFlowControlTest
operator|.
name|class
argument_list|)
return|;
block|}
specifier|public
name|void
name|initCombosForTestFastAndSlowRemoteConsumers
parameter_list|()
block|{
name|addCombinationValues
argument_list|(
literal|"persistentTestMessages"
argument_list|,
operator|new
name|Object
index|[]
block|{
operator|new
name|Boolean
argument_list|(
literal|true
argument_list|)
block|,
operator|new
name|Boolean
argument_list|(
literal|false
argument_list|)
block|}
argument_list|)
expr_stmt|;
name|addCombinationValues
argument_list|(
literal|"networkIsAlwaysSendSync"
argument_list|,
operator|new
name|Object
index|[]
block|{
operator|new
name|Boolean
argument_list|(
literal|true
argument_list|)
block|,
operator|new
name|Boolean
argument_list|(
literal|false
argument_list|)
block|}
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|protected
name|void
name|setUp
parameter_list|()
throws|throws
name|Exception
block|{
name|setAutoFail
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|setMaxTestTime
argument_list|(
name|MAX_TEST_TIME
argument_list|)
expr_stmt|;
name|super
operator|.
name|setUp
argument_list|()
expr_stmt|;
block|}
comment|/**      * This test is parameterized by {@link #persistentTestMessages}, which      * determines whether the producer on broker0 sends persistent or      * non-persistent messages, and {@link #networkIsAlwaysSendSync}, which      * determines how the bridge will forward both persistent and non-persistent      * messages to broker1.      *      * @see #initCombosForTestFastAndSlowRemoteConsumers()      */
specifier|public
name|void
name|testFastAndSlowRemoteConsumers
parameter_list|()
throws|throws
name|Exception
block|{
specifier|final
name|int
name|NUM_MESSAGES
init|=
literal|100
decl_stmt|;
specifier|final
name|long
name|TEST_MESSAGE_SIZE
init|=
literal|1024
decl_stmt|;
specifier|final
name|long
name|SLOW_CONSUMER_DELAY_MILLIS
init|=
literal|100
decl_stmt|;
comment|// Consumer prefetch is disabled for broker1's consumers.
specifier|final
name|ActiveMQQueue
name|SLOW_SHARED_QUEUE
init|=
operator|new
name|ActiveMQQueue
argument_list|(
name|NetworkBridgeProducerFlowControlTest
operator|.
name|class
operator|.
name|getSimpleName
argument_list|()
operator|+
literal|".slow.shared?consumer.prefetchSize=1"
argument_list|)
decl_stmt|;
specifier|final
name|ActiveMQQueue
name|FAST_SHARED_QUEUE
init|=
operator|new
name|ActiveMQQueue
argument_list|(
name|NetworkBridgeProducerFlowControlTest
operator|.
name|class
operator|.
name|getSimpleName
argument_list|()
operator|+
literal|".fast.shared?consumer.prefetchSize=1"
argument_list|)
decl_stmt|;
comment|// Start a local and a remote broker.
name|createBroker
argument_list|(
operator|new
name|URI
argument_list|(
literal|"broker:(tcp://localhost:0"
operator|+
literal|")?brokerName=broker0&persistent=false&useJmx=true"
argument_list|)
argument_list|)
expr_stmt|;
name|BrokerService
name|remoteBroker
init|=
name|createBroker
argument_list|(
operator|new
name|URI
argument_list|(
literal|"broker:(tcp://localhost:0"
operator|+
literal|")?brokerName=broker1&persistent=false&useJmx=true"
argument_list|)
argument_list|)
decl_stmt|;
comment|// Set a policy on the remote broker that limits the maximum size of the
comment|// slow shared queue.
name|PolicyEntry
name|policyEntry
init|=
operator|new
name|PolicyEntry
argument_list|()
decl_stmt|;
name|policyEntry
operator|.
name|setMemoryLimit
argument_list|(
literal|5
operator|*
name|TEST_MESSAGE_SIZE
argument_list|)
expr_stmt|;
name|PolicyMap
name|policyMap
init|=
operator|new
name|PolicyMap
argument_list|()
decl_stmt|;
name|policyMap
operator|.
name|put
argument_list|(
name|SLOW_SHARED_QUEUE
argument_list|,
name|policyEntry
argument_list|)
expr_stmt|;
name|remoteBroker
operator|.
name|setDestinationPolicy
argument_list|(
name|policyMap
argument_list|)
expr_stmt|;
comment|// Create an outbound bridge from the local broker to the remote broker.
comment|// The bridge is configured with the remoteDispatchType enhancement.
name|NetworkConnector
name|nc
init|=
name|bridgeBrokers
argument_list|(
literal|"broker0"
argument_list|,
literal|"broker1"
argument_list|)
decl_stmt|;
name|nc
operator|.
name|setAlwaysSyncSend
argument_list|(
name|networkIsAlwaysSendSync
argument_list|)
expr_stmt|;
name|nc
operator|.
name|setPrefetchSize
argument_list|(
literal|1
argument_list|)
expr_stmt|;
name|startAllBrokers
argument_list|()
expr_stmt|;
name|waitForBridgeFormation
argument_list|()
expr_stmt|;
comment|// Send the test messages to the local broker's shared queues. The
comment|// messages are either persistent or non-persistent to demonstrate the
comment|// difference between synchronous and asynchronous dispatch.
name|persistentDelivery
operator|=
name|persistentTestMessages
expr_stmt|;
name|sendMessages
argument_list|(
literal|"broker0"
argument_list|,
name|FAST_SHARED_QUEUE
argument_list|,
name|NUM_MESSAGES
argument_list|)
expr_stmt|;
name|sendMessages
argument_list|(
literal|"broker0"
argument_list|,
name|SLOW_SHARED_QUEUE
argument_list|,
name|NUM_MESSAGES
argument_list|)
expr_stmt|;
comment|// Start two asynchronous consumers on the remote broker, one for each
comment|// of the two shared queues, and keep track of how long it takes for
comment|// each of the consumers to receive all the messages.
specifier|final
name|CountDownLatch
name|fastConsumerLatch
init|=
operator|new
name|CountDownLatch
argument_list|(
name|NUM_MESSAGES
argument_list|)
decl_stmt|;
specifier|final
name|CountDownLatch
name|slowConsumerLatch
init|=
operator|new
name|CountDownLatch
argument_list|(
name|NUM_MESSAGES
argument_list|)
decl_stmt|;
specifier|final
name|long
name|startTimeMillis
init|=
name|System
operator|.
name|currentTimeMillis
argument_list|()
decl_stmt|;
specifier|final
name|AtomicLong
name|fastConsumerTime
init|=
operator|new
name|AtomicLong
argument_list|()
decl_stmt|;
specifier|final
name|AtomicLong
name|slowConsumerTime
init|=
operator|new
name|AtomicLong
argument_list|()
decl_stmt|;
name|Thread
name|fastWaitThread
init|=
operator|new
name|Thread
argument_list|()
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
name|fastConsumerLatch
operator|.
name|await
argument_list|()
expr_stmt|;
name|fastConsumerTime
operator|.
name|set
argument_list|(
name|System
operator|.
name|currentTimeMillis
argument_list|()
operator|-
name|startTimeMillis
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|InterruptedException
name|ex
parameter_list|)
block|{
name|exceptions
operator|.
name|add
argument_list|(
name|ex
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|fail
argument_list|(
name|ex
operator|.
name|getMessage
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
block|}
decl_stmt|;
name|Thread
name|slowWaitThread
init|=
operator|new
name|Thread
argument_list|()
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
name|slowConsumerLatch
operator|.
name|await
argument_list|()
expr_stmt|;
name|slowConsumerTime
operator|.
name|set
argument_list|(
name|System
operator|.
name|currentTimeMillis
argument_list|()
operator|-
name|startTimeMillis
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|InterruptedException
name|ex
parameter_list|)
block|{
name|exceptions
operator|.
name|add
argument_list|(
name|ex
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|fail
argument_list|(
name|ex
operator|.
name|getMessage
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
block|}
decl_stmt|;
name|fastWaitThread
operator|.
name|start
argument_list|()
expr_stmt|;
name|slowWaitThread
operator|.
name|start
argument_list|()
expr_stmt|;
name|createConsumer
argument_list|(
literal|"broker1"
argument_list|,
name|FAST_SHARED_QUEUE
argument_list|,
name|fastConsumerLatch
argument_list|)
expr_stmt|;
name|MessageConsumer
name|slowConsumer
init|=
name|createConsumer
argument_list|(
literal|"broker1"
argument_list|,
name|SLOW_SHARED_QUEUE
argument_list|,
name|slowConsumerLatch
argument_list|)
decl_stmt|;
name|MessageIdList
name|messageIdList
init|=
name|brokers
operator|.
name|get
argument_list|(
literal|"broker1"
argument_list|)
operator|.
name|consumers
operator|.
name|get
argument_list|(
name|slowConsumer
argument_list|)
decl_stmt|;
name|messageIdList
operator|.
name|setProcessingDelay
argument_list|(
name|SLOW_CONSUMER_DELAY_MILLIS
argument_list|)
expr_stmt|;
name|fastWaitThread
operator|.
name|join
argument_list|()
expr_stmt|;
name|slowWaitThread
operator|.
name|join
argument_list|()
expr_stmt|;
name|assertTrue
argument_list|(
literal|"no exceptions on the wait threads:"
operator|+
name|exceptions
argument_list|,
name|exceptions
operator|.
name|isEmpty
argument_list|()
argument_list|)
expr_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"Fast consumer duration (ms): "
operator|+
name|fastConsumerTime
operator|.
name|get
argument_list|()
argument_list|)
expr_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"Slow consumer duration (ms): "
operator|+
name|slowConsumerTime
operator|.
name|get
argument_list|()
argument_list|)
expr_stmt|;
comment|// Verify the behaviour as described in the description of this class.
if|if
condition|(
name|networkIsAlwaysSendSync
condition|)
block|{
name|Assert
operator|.
name|assertTrue
argument_list|(
name|fastConsumerTime
operator|.
name|get
argument_list|()
operator|<
name|slowConsumerTime
operator|.
name|get
argument_list|()
operator|/
literal|10
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|Assert
operator|.
name|assertEquals
argument_list|(
name|persistentTestMessages
argument_list|,
name|fastConsumerTime
operator|.
name|get
argument_list|()
operator|<
name|slowConsumerTime
operator|.
name|get
argument_list|()
operator|/
literal|10
argument_list|)
expr_stmt|;
block|}
block|}
specifier|public
name|void
name|testSendFailIfNoSpaceDoesNotBlockQueueNetwork
parameter_list|()
throws|throws
name|Exception
block|{
comment|// Consumer prefetch is disabled for broker1's consumers.
specifier|final
name|ActiveMQQueue
name|SLOW_SHARED_QUEUE
init|=
operator|new
name|ActiveMQQueue
argument_list|(
name|NetworkBridgeProducerFlowControlTest
operator|.
name|class
operator|.
name|getSimpleName
argument_list|()
operator|+
literal|".slow.shared?consumer.prefetchSize=1"
argument_list|)
decl_stmt|;
specifier|final
name|ActiveMQQueue
name|FAST_SHARED_QUEUE
init|=
operator|new
name|ActiveMQQueue
argument_list|(
name|NetworkBridgeProducerFlowControlTest
operator|.
name|class
operator|.
name|getSimpleName
argument_list|()
operator|+
literal|".fast.shared?consumer.prefetchSize=1"
argument_list|)
decl_stmt|;
name|doTestSendFailIfNoSpaceDoesNotBlockNetwork
argument_list|(
name|SLOW_SHARED_QUEUE
argument_list|,
name|FAST_SHARED_QUEUE
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|testSendFailIfNoSpaceDoesNotBlockTopicNetwork
parameter_list|()
throws|throws
name|Exception
block|{
comment|// Consumer prefetch is disabled for broker1's consumers.
specifier|final
name|ActiveMQTopic
name|SLOW_SHARED_TOPIC
init|=
operator|new
name|ActiveMQTopic
argument_list|(
name|NetworkBridgeProducerFlowControlTest
operator|.
name|class
operator|.
name|getSimpleName
argument_list|()
operator|+
literal|".slow.shared?consumer.prefetchSize=1"
argument_list|)
decl_stmt|;
specifier|final
name|ActiveMQTopic
name|FAST_SHARED_TOPIC
init|=
operator|new
name|ActiveMQTopic
argument_list|(
name|NetworkBridgeProducerFlowControlTest
operator|.
name|class
operator|.
name|getSimpleName
argument_list|()
operator|+
literal|".fast.shared?consumer.prefetchSize=1"
argument_list|)
decl_stmt|;
name|doTestSendFailIfNoSpaceDoesNotBlockNetwork
argument_list|(
name|SLOW_SHARED_TOPIC
argument_list|,
name|FAST_SHARED_TOPIC
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|doTestSendFailIfNoSpaceDoesNotBlockNetwork
parameter_list|(
name|ActiveMQDestination
name|slowDestination
parameter_list|,
name|ActiveMQDestination
name|fastDestination
parameter_list|)
throws|throws
name|Exception
block|{
specifier|final
name|int
name|NUM_MESSAGES
init|=
literal|100
decl_stmt|;
specifier|final
name|long
name|TEST_MESSAGE_SIZE
init|=
literal|1024
decl_stmt|;
specifier|final
name|long
name|SLOW_CONSUMER_DELAY_MILLIS
init|=
literal|100
decl_stmt|;
comment|// Start a local and a remote broker.
name|createBroker
argument_list|(
operator|new
name|URI
argument_list|(
literal|"broker:(tcp://localhost:0"
operator|+
literal|")?brokerName=broker0&persistent=false&useJmx=true"
argument_list|)
argument_list|)
expr_stmt|;
name|BrokerService
name|remoteBroker
init|=
name|createBroker
argument_list|(
operator|new
name|URI
argument_list|(
literal|"broker:(tcp://localhost:0"
operator|+
literal|")?brokerName=broker1&persistent=false&useJmx=true"
argument_list|)
argument_list|)
decl_stmt|;
name|remoteBroker
operator|.
name|getSystemUsage
argument_list|()
operator|.
name|setSendFailIfNoSpace
argument_list|(
literal|true
argument_list|)
expr_stmt|;
comment|// Set a policy on the remote broker that limits the maximum size of the
comment|// slow shared queue.
name|PolicyEntry
name|policyEntry
init|=
operator|new
name|PolicyEntry
argument_list|()
decl_stmt|;
name|policyEntry
operator|.
name|setMemoryLimit
argument_list|(
literal|5
operator|*
name|TEST_MESSAGE_SIZE
argument_list|)
expr_stmt|;
name|PolicyMap
name|policyMap
init|=
operator|new
name|PolicyMap
argument_list|()
decl_stmt|;
name|policyMap
operator|.
name|put
argument_list|(
name|slowDestination
argument_list|,
name|policyEntry
argument_list|)
expr_stmt|;
name|remoteBroker
operator|.
name|setDestinationPolicy
argument_list|(
name|policyMap
argument_list|)
expr_stmt|;
comment|// Create an outbound bridge from the local broker to the remote broker.
comment|// The bridge is configured with the remoteDispatchType enhancement.
name|NetworkConnector
name|nc
init|=
name|bridgeBrokers
argument_list|(
literal|"broker0"
argument_list|,
literal|"broker1"
argument_list|)
decl_stmt|;
name|nc
operator|.
name|setAlwaysSyncSend
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|nc
operator|.
name|setPrefetchSize
argument_list|(
literal|1
argument_list|)
expr_stmt|;
name|startAllBrokers
argument_list|()
expr_stmt|;
name|waitForBridgeFormation
argument_list|()
expr_stmt|;
comment|// Start two asynchronous consumers on the remote broker, one for each
comment|// of the two shared queues, and keep track of how long it takes for
comment|// each of the consumers to receive all the messages.
specifier|final
name|CountDownLatch
name|fastConsumerLatch
init|=
operator|new
name|CountDownLatch
argument_list|(
name|NUM_MESSAGES
argument_list|)
decl_stmt|;
specifier|final
name|CountDownLatch
name|slowConsumerLatch
init|=
operator|new
name|CountDownLatch
argument_list|(
name|NUM_MESSAGES
argument_list|)
decl_stmt|;
specifier|final
name|long
name|startTimeMillis
init|=
name|System
operator|.
name|currentTimeMillis
argument_list|()
decl_stmt|;
specifier|final
name|AtomicLong
name|fastConsumerTime
init|=
operator|new
name|AtomicLong
argument_list|()
decl_stmt|;
specifier|final
name|AtomicLong
name|slowConsumerTime
init|=
operator|new
name|AtomicLong
argument_list|()
decl_stmt|;
name|Thread
name|fastWaitThread
init|=
operator|new
name|Thread
argument_list|()
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
name|fastConsumerLatch
operator|.
name|await
argument_list|()
expr_stmt|;
name|fastConsumerTime
operator|.
name|set
argument_list|(
name|System
operator|.
name|currentTimeMillis
argument_list|()
operator|-
name|startTimeMillis
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|InterruptedException
name|ex
parameter_list|)
block|{
name|exceptions
operator|.
name|add
argument_list|(
name|ex
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|fail
argument_list|(
name|ex
operator|.
name|getMessage
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
block|}
decl_stmt|;
name|Thread
name|slowWaitThread
init|=
operator|new
name|Thread
argument_list|()
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
name|slowConsumerLatch
operator|.
name|await
argument_list|()
expr_stmt|;
name|slowConsumerTime
operator|.
name|set
argument_list|(
name|System
operator|.
name|currentTimeMillis
argument_list|()
operator|-
name|startTimeMillis
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|InterruptedException
name|ex
parameter_list|)
block|{
name|exceptions
operator|.
name|add
argument_list|(
name|ex
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|fail
argument_list|(
name|ex
operator|.
name|getMessage
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
block|}
decl_stmt|;
name|fastWaitThread
operator|.
name|start
argument_list|()
expr_stmt|;
name|slowWaitThread
operator|.
name|start
argument_list|()
expr_stmt|;
name|createConsumer
argument_list|(
literal|"broker1"
argument_list|,
name|fastDestination
argument_list|,
name|fastConsumerLatch
argument_list|)
expr_stmt|;
name|MessageConsumer
name|slowConsumer
init|=
name|createConsumer
argument_list|(
literal|"broker1"
argument_list|,
name|slowDestination
argument_list|,
name|slowConsumerLatch
argument_list|)
decl_stmt|;
name|MessageIdList
name|messageIdList
init|=
name|brokers
operator|.
name|get
argument_list|(
literal|"broker1"
argument_list|)
operator|.
name|consumers
operator|.
name|get
argument_list|(
name|slowConsumer
argument_list|)
decl_stmt|;
name|messageIdList
operator|.
name|setProcessingDelay
argument_list|(
name|SLOW_CONSUMER_DELAY_MILLIS
argument_list|)
expr_stmt|;
comment|// Send the test messages to the local broker's shared queues. The
comment|// messages are either persistent or non-persistent to demonstrate the
comment|// difference between synchronous and asynchronous dispatch.
name|persistentDelivery
operator|=
literal|false
expr_stmt|;
name|sendMessages
argument_list|(
literal|"broker0"
argument_list|,
name|fastDestination
argument_list|,
name|NUM_MESSAGES
argument_list|)
expr_stmt|;
name|sendMessages
argument_list|(
literal|"broker0"
argument_list|,
name|slowDestination
argument_list|,
name|NUM_MESSAGES
argument_list|)
expr_stmt|;
name|fastWaitThread
operator|.
name|join
argument_list|(
name|TimeUnit
operator|.
name|SECONDS
operator|.
name|toMillis
argument_list|(
literal|60
argument_list|)
argument_list|)
expr_stmt|;
name|slowWaitThread
operator|.
name|join
argument_list|(
name|TimeUnit
operator|.
name|SECONDS
operator|.
name|toMillis
argument_list|(
literal|60
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
literal|"no exceptions on the wait threads:"
operator|+
name|exceptions
argument_list|,
name|exceptions
operator|.
name|isEmpty
argument_list|()
argument_list|)
expr_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"Fast consumer duration (ms): "
operator|+
name|fastConsumerTime
operator|.
name|get
argument_list|()
argument_list|)
expr_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"Slow consumer duration (ms): "
operator|+
name|slowConsumerTime
operator|.
name|get
argument_list|()
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
literal|"fast time set"
argument_list|,
name|fastConsumerTime
operator|.
name|get
argument_list|()
operator|>
literal|0
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
literal|"slow time set"
argument_list|,
name|slowConsumerTime
operator|.
name|get
argument_list|()
operator|>
literal|0
argument_list|)
expr_stmt|;
comment|// Verify the behaviour as described in the description of this class.
name|Assert
operator|.
name|assertTrue
argument_list|(
name|fastConsumerTime
operator|.
name|get
argument_list|()
operator|<
name|slowConsumerTime
operator|.
name|get
argument_list|()
operator|/
literal|10
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit
