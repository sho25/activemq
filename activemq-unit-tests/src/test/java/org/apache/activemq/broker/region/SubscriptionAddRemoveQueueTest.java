begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *      http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

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
operator|.
name|region
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
name|Collections
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
name|Future
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
name|InvalidSelectorException
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
name|ConnectionContext
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
name|ProducerBrokerExchange
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
name|MessageDispatchNotification
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
name|MessageId
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
name|MessagePull
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
name|Response
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
name|filter
operator|.
name|MessageEvaluationContext
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
name|state
operator|.
name|ProducerState
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
name|store
operator|.
name|MessageStore
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
name|thread
operator|.
name|TaskRunnerFactory
import|;
end_import

begin_class
specifier|public
class|class
name|SubscriptionAddRemoveQueueTest
extends|extends
name|TestCase
block|{
name|Queue
name|queue
decl_stmt|;
name|ConsumerInfo
name|info
init|=
operator|new
name|ConsumerInfo
argument_list|()
decl_stmt|;
name|List
argument_list|<
name|SimpleImmediateDispatchSubscription
argument_list|>
name|subs
init|=
operator|new
name|ArrayList
argument_list|<
name|SimpleImmediateDispatchSubscription
argument_list|>
argument_list|()
decl_stmt|;
name|ConnectionContext
name|context
init|=
operator|new
name|ConnectionContext
argument_list|()
decl_stmt|;
name|ProducerBrokerExchange
name|producerBrokerExchange
init|=
operator|new
name|ProducerBrokerExchange
argument_list|()
decl_stmt|;
name|ProducerInfo
name|producerInfo
init|=
operator|new
name|ProducerInfo
argument_list|()
decl_stmt|;
name|ProducerState
name|producerState
init|=
operator|new
name|ProducerState
argument_list|(
name|producerInfo
argument_list|)
decl_stmt|;
name|ActiveMQDestination
name|destination
init|=
operator|new
name|ActiveMQQueue
argument_list|(
literal|"TEST"
argument_list|)
decl_stmt|;
name|int
name|numSubscriptions
init|=
literal|1000
decl_stmt|;
name|boolean
name|working
init|=
literal|true
decl_stmt|;
name|int
name|senders
init|=
literal|20
decl_stmt|;
annotation|@
name|Override
specifier|public
name|void
name|setUp
parameter_list|()
throws|throws
name|Exception
block|{
name|BrokerService
name|brokerService
init|=
operator|new
name|BrokerService
argument_list|()
decl_stmt|;
name|brokerService
operator|.
name|start
argument_list|()
expr_stmt|;
name|DestinationStatistics
name|parentStats
init|=
operator|new
name|DestinationStatistics
argument_list|()
decl_stmt|;
name|parentStats
operator|.
name|setEnabled
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|TaskRunnerFactory
name|taskFactory
init|=
operator|new
name|TaskRunnerFactory
argument_list|()
decl_stmt|;
name|MessageStore
name|store
init|=
literal|null
decl_stmt|;
name|info
operator|.
name|setDestination
argument_list|(
name|destination
argument_list|)
expr_stmt|;
name|info
operator|.
name|setPrefetchSize
argument_list|(
literal|100
argument_list|)
expr_stmt|;
name|producerBrokerExchange
operator|.
name|setProducerState
argument_list|(
name|producerState
argument_list|)
expr_stmt|;
name|producerBrokerExchange
operator|.
name|setConnectionContext
argument_list|(
name|context
argument_list|)
expr_stmt|;
name|queue
operator|=
operator|new
name|Queue
argument_list|(
name|brokerService
argument_list|,
name|destination
argument_list|,
name|store
argument_list|,
name|parentStats
argument_list|,
name|taskFactory
argument_list|)
expr_stmt|;
name|queue
operator|.
name|initialize
argument_list|()
expr_stmt|;
block|}
specifier|public
name|void
name|testNoDispatchToRemovedConsumers
parameter_list|()
throws|throws
name|Exception
block|{
specifier|final
name|AtomicInteger
name|producerId
init|=
operator|new
name|AtomicInteger
argument_list|()
decl_stmt|;
name|Runnable
name|sender
init|=
operator|new
name|Runnable
argument_list|()
block|{
specifier|public
name|void
name|run
parameter_list|()
block|{
name|AtomicInteger
name|id
init|=
operator|new
name|AtomicInteger
argument_list|()
decl_stmt|;
name|int
name|producerIdAndIncrement
init|=
name|producerId
operator|.
name|getAndIncrement
argument_list|()
decl_stmt|;
while|while
condition|(
name|working
condition|)
block|{
try|try
block|{
name|Message
name|msg
init|=
operator|new
name|ActiveMQMessage
argument_list|()
decl_stmt|;
name|msg
operator|.
name|setDestination
argument_list|(
name|destination
argument_list|)
expr_stmt|;
name|msg
operator|.
name|setMessageId
argument_list|(
operator|new
name|MessageId
argument_list|(
name|producerIdAndIncrement
operator|+
literal|":0:"
operator|+
name|id
operator|.
name|getAndIncrement
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|queue
operator|.
name|send
argument_list|(
name|producerBrokerExchange
argument_list|,
name|msg
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
name|e
operator|.
name|printStackTrace
argument_list|()
expr_stmt|;
name|fail
argument_list|(
literal|"unexpected exception in sendMessage, ex:"
operator|+
name|e
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|}
decl_stmt|;
name|Runnable
name|subRemover
init|=
operator|new
name|Runnable
argument_list|()
block|{
specifier|public
name|void
name|run
parameter_list|()
block|{
for|for
control|(
name|Subscription
name|sub
range|:
name|subs
control|)
block|{
try|try
block|{
name|queue
operator|.
name|removeSubscription
argument_list|(
name|context
argument_list|,
name|sub
argument_list|,
literal|0
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
name|e
operator|.
name|printStackTrace
argument_list|()
expr_stmt|;
name|fail
argument_list|(
literal|"unexpected exception in removeSubscription, ex:"
operator|+
name|e
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|}
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
name|numSubscriptions
condition|;
name|i
operator|++
control|)
block|{
name|SimpleImmediateDispatchSubscription
name|sub
init|=
operator|new
name|SimpleImmediateDispatchSubscription
argument_list|()
decl_stmt|;
name|subs
operator|.
name|add
argument_list|(
name|sub
argument_list|)
expr_stmt|;
name|queue
operator|.
name|addSubscription
argument_list|(
name|context
argument_list|,
name|sub
argument_list|)
expr_stmt|;
block|}
name|assertEquals
argument_list|(
literal|"there are X subscriptions"
argument_list|,
name|numSubscriptions
argument_list|,
name|queue
operator|.
name|getDestinationStatistics
argument_list|()
operator|.
name|getConsumers
argument_list|()
operator|.
name|getCount
argument_list|()
argument_list|)
expr_stmt|;
name|ExecutorService
name|executor
init|=
name|Executors
operator|.
name|newCachedThreadPool
argument_list|()
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
name|senders
condition|;
name|i
operator|++
control|)
block|{
name|executor
operator|.
name|submit
argument_list|(
name|sender
argument_list|)
expr_stmt|;
block|}
name|Thread
operator|.
name|sleep
argument_list|(
literal|1000
argument_list|)
expr_stmt|;
for|for
control|(
name|SimpleImmediateDispatchSubscription
name|sub
range|:
name|subs
control|)
block|{
name|assertTrue
argument_list|(
literal|"There are some locked messages in the subscription"
argument_list|,
name|hasSomeLocks
argument_list|(
name|sub
operator|.
name|dispatched
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|Future
argument_list|<
name|?
argument_list|>
name|result
init|=
name|executor
operator|.
name|submit
argument_list|(
name|subRemover
argument_list|)
decl_stmt|;
name|result
operator|.
name|get
argument_list|()
expr_stmt|;
name|working
operator|=
literal|false
expr_stmt|;
name|assertEquals
argument_list|(
literal|"there are no subscriptions"
argument_list|,
literal|0
argument_list|,
name|queue
operator|.
name|getDestinationStatistics
argument_list|()
operator|.
name|getConsumers
argument_list|()
operator|.
name|getCount
argument_list|()
argument_list|)
expr_stmt|;
for|for
control|(
name|SimpleImmediateDispatchSubscription
name|sub
range|:
name|subs
control|)
block|{
name|assertTrue
argument_list|(
literal|"There are no locked messages in any removed subscriptions"
argument_list|,
operator|!
name|hasSomeLocks
argument_list|(
name|sub
operator|.
name|dispatched
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
specifier|private
name|boolean
name|hasSomeLocks
parameter_list|(
name|List
argument_list|<
name|MessageReference
argument_list|>
name|dispatched
parameter_list|)
block|{
name|boolean
name|hasLock
init|=
literal|false
decl_stmt|;
for|for
control|(
name|MessageReference
name|mr
range|:
name|dispatched
control|)
block|{
name|QueueMessageReference
name|qmr
init|=
operator|(
name|QueueMessageReference
operator|)
name|mr
decl_stmt|;
if|if
condition|(
name|qmr
operator|.
name|getLockOwner
argument_list|()
operator|!=
literal|null
condition|)
block|{
name|hasLock
operator|=
literal|true
expr_stmt|;
break|break;
block|}
block|}
return|return
name|hasLock
return|;
block|}
specifier|public
class|class
name|SimpleImmediateDispatchSubscription
implements|implements
name|Subscription
implements|,
name|LockOwner
block|{
name|List
argument_list|<
name|MessageReference
argument_list|>
name|dispatched
init|=
name|Collections
operator|.
name|synchronizedList
argument_list|(
operator|new
name|ArrayList
argument_list|<
name|MessageReference
argument_list|>
argument_list|()
argument_list|)
decl_stmt|;
specifier|public
name|void
name|acknowledge
parameter_list|(
name|ConnectionContext
name|context
parameter_list|,
name|MessageAck
name|ack
parameter_list|)
throws|throws
name|Exception
block|{         }
specifier|public
name|void
name|add
parameter_list|(
name|MessageReference
name|node
parameter_list|)
throws|throws
name|Exception
block|{
comment|// immediate dispatch
name|QueueMessageReference
name|qmr
init|=
operator|(
name|QueueMessageReference
operator|)
name|node
decl_stmt|;
name|qmr
operator|.
name|lock
argument_list|(
name|this
argument_list|)
expr_stmt|;
name|dispatched
operator|.
name|add
argument_list|(
name|qmr
argument_list|)
expr_stmt|;
block|}
specifier|public
name|ConnectionContext
name|getContext
parameter_list|()
block|{
return|return
literal|null
return|;
block|}
annotation|@
name|Override
specifier|public
name|int
name|getCursorMemoryHighWaterMark
parameter_list|()
block|{
return|return
literal|0
return|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|setCursorMemoryHighWaterMark
parameter_list|(
name|int
name|cursorMemoryHighWaterMark
parameter_list|)
block|{         }
annotation|@
name|Override
specifier|public
name|boolean
name|isSlowConsumer
parameter_list|()
block|{
return|return
literal|false
return|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|unmatched
parameter_list|(
name|MessageReference
name|node
parameter_list|)
throws|throws
name|IOException
block|{         }
annotation|@
name|Override
specifier|public
name|long
name|getTimeOfLastMessageAck
parameter_list|()
block|{
return|return
literal|0
return|;
block|}
annotation|@
name|Override
specifier|public
name|long
name|getConsumedCount
parameter_list|()
block|{
return|return
literal|0
return|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|incrementConsumedCount
parameter_list|()
block|{         }
annotation|@
name|Override
specifier|public
name|void
name|resetConsumedCount
parameter_list|()
block|{         }
specifier|public
name|void
name|add
parameter_list|(
name|ConnectionContext
name|context
parameter_list|,
name|Destination
name|destination
parameter_list|)
throws|throws
name|Exception
block|{         }
specifier|public
name|void
name|destroy
parameter_list|()
block|{         }
specifier|public
name|void
name|gc
parameter_list|()
block|{         }
specifier|public
name|ConsumerInfo
name|getConsumerInfo
parameter_list|()
block|{
return|return
name|info
return|;
block|}
specifier|public
name|long
name|getDequeueCounter
parameter_list|()
block|{
return|return
literal|0
return|;
block|}
specifier|public
name|long
name|getDispatchedCounter
parameter_list|()
block|{
return|return
literal|0
return|;
block|}
specifier|public
name|int
name|getDispatchedQueueSize
parameter_list|()
block|{
return|return
literal|0
return|;
block|}
specifier|public
name|long
name|getEnqueueCounter
parameter_list|()
block|{
return|return
literal|0
return|;
block|}
specifier|public
name|int
name|getInFlightSize
parameter_list|()
block|{
return|return
literal|0
return|;
block|}
specifier|public
name|int
name|getInFlightUsage
parameter_list|()
block|{
return|return
literal|0
return|;
block|}
specifier|public
name|ObjectName
name|getObjectName
parameter_list|()
block|{
return|return
literal|null
return|;
block|}
specifier|public
name|int
name|getPendingQueueSize
parameter_list|()
block|{
return|return
literal|0
return|;
block|}
specifier|public
name|int
name|getPrefetchSize
parameter_list|()
block|{
return|return
literal|0
return|;
block|}
specifier|public
name|String
name|getSelector
parameter_list|()
block|{
return|return
literal|null
return|;
block|}
specifier|public
name|boolean
name|isBrowser
parameter_list|()
block|{
return|return
literal|false
return|;
block|}
specifier|public
name|boolean
name|isFull
parameter_list|()
block|{
return|return
literal|false
return|;
block|}
specifier|public
name|boolean
name|isHighWaterMark
parameter_list|()
block|{
return|return
literal|false
return|;
block|}
specifier|public
name|boolean
name|isLowWaterMark
parameter_list|()
block|{
return|return
literal|false
return|;
block|}
specifier|public
name|boolean
name|isRecoveryRequired
parameter_list|()
block|{
return|return
literal|false
return|;
block|}
specifier|public
name|boolean
name|isSlave
parameter_list|()
block|{
return|return
literal|false
return|;
block|}
specifier|public
name|boolean
name|matches
parameter_list|(
name|MessageReference
name|node
parameter_list|,
name|MessageEvaluationContext
name|context
parameter_list|)
throws|throws
name|IOException
block|{
return|return
literal|true
return|;
block|}
specifier|public
name|boolean
name|matches
parameter_list|(
name|ActiveMQDestination
name|destination
parameter_list|)
block|{
return|return
literal|false
return|;
block|}
specifier|public
name|void
name|processMessageDispatchNotification
parameter_list|(
name|MessageDispatchNotification
name|mdn
parameter_list|)
throws|throws
name|Exception
block|{         }
specifier|public
name|Response
name|pullMessage
parameter_list|(
name|ConnectionContext
name|context
parameter_list|,
name|MessagePull
name|pull
parameter_list|)
throws|throws
name|Exception
block|{
return|return
literal|null
return|;
block|}
specifier|public
name|List
argument_list|<
name|MessageReference
argument_list|>
name|remove
parameter_list|(
name|ConnectionContext
name|context
parameter_list|,
name|Destination
name|destination
parameter_list|)
throws|throws
name|Exception
block|{
return|return
operator|new
name|ArrayList
argument_list|<
name|MessageReference
argument_list|>
argument_list|(
name|dispatched
argument_list|)
return|;
block|}
specifier|public
name|void
name|setObjectName
parameter_list|(
name|ObjectName
name|objectName
parameter_list|)
block|{         }
specifier|public
name|void
name|setSelector
parameter_list|(
name|String
name|selector
parameter_list|)
throws|throws
name|InvalidSelectorException
throws|,
name|UnsupportedOperationException
block|{         }
specifier|public
name|void
name|updateConsumerPrefetch
parameter_list|(
name|int
name|newPrefetch
parameter_list|)
block|{         }
specifier|public
name|boolean
name|addRecoveredMessage
parameter_list|(
name|ConnectionContext
name|context
parameter_list|,
name|MessageReference
name|message
parameter_list|)
throws|throws
name|Exception
block|{
return|return
literal|false
return|;
block|}
specifier|public
name|ActiveMQDestination
name|getActiveMQDestination
parameter_list|()
block|{
return|return
literal|null
return|;
block|}
specifier|public
name|int
name|getLockPriority
parameter_list|()
block|{
return|return
literal|0
return|;
block|}
specifier|public
name|boolean
name|isLockExclusive
parameter_list|()
block|{
return|return
literal|false
return|;
block|}
specifier|public
name|void
name|addDestination
parameter_list|(
name|Destination
name|destination
parameter_list|)
block|{         }
specifier|public
name|void
name|removeDestination
parameter_list|(
name|Destination
name|destination
parameter_list|)
block|{         }
specifier|public
name|int
name|countBeforeFull
parameter_list|()
block|{
return|return
literal|10
return|;
block|}
block|}
block|}
end_class

end_unit

