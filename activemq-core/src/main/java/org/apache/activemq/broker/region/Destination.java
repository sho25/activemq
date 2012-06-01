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
name|List
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
name|Service
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
name|broker
operator|.
name|region
operator|.
name|policy
operator|.
name|DeadLetterStrategy
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
name|SharedDeadLetterStrategy
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
name|SlowConsumerStrategy
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
name|Task
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
name|usage
operator|.
name|MemoryUsage
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
name|usage
operator|.
name|Usage
import|;
end_import

begin_comment
comment|/**  *  */
end_comment

begin_interface
specifier|public
interface|interface
name|Destination
extends|extends
name|Service
extends|,
name|Task
block|{
specifier|public
specifier|static
specifier|final
name|DeadLetterStrategy
name|DEFAULT_DEAD_LETTER_STRATEGY
init|=
operator|new
name|SharedDeadLetterStrategy
argument_list|()
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|long
name|DEFAULT_BLOCKED_PRODUCER_WARNING_INTERVAL
init|=
literal|30000
decl_stmt|;
name|void
name|addSubscription
parameter_list|(
name|ConnectionContext
name|context
parameter_list|,
name|Subscription
name|sub
parameter_list|)
throws|throws
name|Exception
function_decl|;
name|void
name|removeSubscription
parameter_list|(
name|ConnectionContext
name|context
parameter_list|,
name|Subscription
name|sub
parameter_list|,
name|long
name|lastDeliveredSequenceId
parameter_list|)
throws|throws
name|Exception
function_decl|;
name|void
name|addProducer
parameter_list|(
name|ConnectionContext
name|context
parameter_list|,
name|ProducerInfo
name|info
parameter_list|)
throws|throws
name|Exception
function_decl|;
name|void
name|removeProducer
parameter_list|(
name|ConnectionContext
name|context
parameter_list|,
name|ProducerInfo
name|info
parameter_list|)
throws|throws
name|Exception
function_decl|;
name|void
name|send
parameter_list|(
name|ProducerBrokerExchange
name|producerExchange
parameter_list|,
name|Message
name|messageSend
parameter_list|)
throws|throws
name|Exception
function_decl|;
name|void
name|acknowledge
parameter_list|(
name|ConnectionContext
name|context
parameter_list|,
name|Subscription
name|sub
parameter_list|,
specifier|final
name|MessageAck
name|ack
parameter_list|,
specifier|final
name|MessageReference
name|node
parameter_list|)
throws|throws
name|IOException
function_decl|;
name|long
name|getInactiveTimoutBeforeGC
parameter_list|()
function_decl|;
name|void
name|markForGC
parameter_list|(
name|long
name|timeStamp
parameter_list|)
function_decl|;
name|boolean
name|canGC
parameter_list|()
function_decl|;
name|void
name|gc
parameter_list|()
function_decl|;
name|ActiveMQDestination
name|getActiveMQDestination
parameter_list|()
function_decl|;
name|MemoryUsage
name|getMemoryUsage
parameter_list|()
function_decl|;
name|void
name|dispose
parameter_list|(
name|ConnectionContext
name|context
parameter_list|)
throws|throws
name|IOException
function_decl|;
name|boolean
name|isDisposed
parameter_list|()
function_decl|;
name|DestinationStatistics
name|getDestinationStatistics
parameter_list|()
function_decl|;
name|DeadLetterStrategy
name|getDeadLetterStrategy
parameter_list|()
function_decl|;
name|Message
index|[]
name|browse
parameter_list|()
function_decl|;
name|String
name|getName
parameter_list|()
function_decl|;
name|MessageStore
name|getMessageStore
parameter_list|()
function_decl|;
name|boolean
name|isProducerFlowControl
parameter_list|()
function_decl|;
name|void
name|setProducerFlowControl
parameter_list|(
name|boolean
name|value
parameter_list|)
function_decl|;
name|boolean
name|isAlwaysRetroactive
parameter_list|()
function_decl|;
name|void
name|setAlwaysRetroactive
parameter_list|(
name|boolean
name|value
parameter_list|)
function_decl|;
comment|/**      * Set's the interval at which warnings about producers being blocked by      * resource usage will be triggered. Values of 0 or less will disable      * warnings      *      * @param blockedProducerWarningInterval the interval at which warning about      *            blocked producers will be triggered.      */
specifier|public
name|void
name|setBlockedProducerWarningInterval
parameter_list|(
name|long
name|blockedProducerWarningInterval
parameter_list|)
function_decl|;
comment|/**      *      * @return the interval at which warning about blocked producers will be      *         triggered.      */
specifier|public
name|long
name|getBlockedProducerWarningInterval
parameter_list|()
function_decl|;
name|int
name|getMaxProducersToAudit
parameter_list|()
function_decl|;
name|void
name|setMaxProducersToAudit
parameter_list|(
name|int
name|maxProducersToAudit
parameter_list|)
function_decl|;
name|int
name|getMaxAuditDepth
parameter_list|()
function_decl|;
name|void
name|setMaxAuditDepth
parameter_list|(
name|int
name|maxAuditDepth
parameter_list|)
function_decl|;
name|boolean
name|isEnableAudit
parameter_list|()
function_decl|;
name|void
name|setEnableAudit
parameter_list|(
name|boolean
name|enableAudit
parameter_list|)
function_decl|;
name|boolean
name|isActive
parameter_list|()
function_decl|;
name|int
name|getMaxPageSize
parameter_list|()
function_decl|;
specifier|public
name|void
name|setMaxPageSize
parameter_list|(
name|int
name|maxPageSize
parameter_list|)
function_decl|;
specifier|public
name|int
name|getMaxBrowsePageSize
parameter_list|()
function_decl|;
specifier|public
name|void
name|setMaxBrowsePageSize
parameter_list|(
name|int
name|maxPageSize
parameter_list|)
function_decl|;
specifier|public
name|boolean
name|isUseCache
parameter_list|()
function_decl|;
specifier|public
name|void
name|setUseCache
parameter_list|(
name|boolean
name|useCache
parameter_list|)
function_decl|;
specifier|public
name|int
name|getMinimumMessageSize
parameter_list|()
function_decl|;
specifier|public
name|void
name|setMinimumMessageSize
parameter_list|(
name|int
name|minimumMessageSize
parameter_list|)
function_decl|;
specifier|public
name|int
name|getCursorMemoryHighWaterMark
parameter_list|()
function_decl|;
specifier|public
name|void
name|setCursorMemoryHighWaterMark
parameter_list|(
name|int
name|cursorMemoryHighWaterMark
parameter_list|)
function_decl|;
comment|/**      * optionally called by a Subscriber - to inform the Destination its ready      * for more messages      */
specifier|public
name|void
name|wakeup
parameter_list|()
function_decl|;
comment|/**      * @return true if lazyDispatch is enabled      */
specifier|public
name|boolean
name|isLazyDispatch
parameter_list|()
function_decl|;
comment|/**      * set the lazy dispatch - default is false      *      * @param value      */
specifier|public
name|void
name|setLazyDispatch
parameter_list|(
name|boolean
name|value
parameter_list|)
function_decl|;
comment|/**      * Inform the Destination a message has expired      *      * @param context      * @param subs      * @param node      */
name|void
name|messageExpired
parameter_list|(
name|ConnectionContext
name|context
parameter_list|,
name|Subscription
name|subs
parameter_list|,
name|MessageReference
name|node
parameter_list|)
function_decl|;
comment|/**      * called when message is consumed      *      * @param context      * @param messageReference      */
name|void
name|messageConsumed
parameter_list|(
name|ConnectionContext
name|context
parameter_list|,
name|MessageReference
name|messageReference
parameter_list|)
function_decl|;
comment|/**      * Called when message is delivered to the broker      *      * @param context      * @param messageReference      */
name|void
name|messageDelivered
parameter_list|(
name|ConnectionContext
name|context
parameter_list|,
name|MessageReference
name|messageReference
parameter_list|)
function_decl|;
comment|/**      * Called when a message is discarded - e.g. running low on memory This will      * happen only if the policy is enabled - e.g. non durable topics      *      * @param context      * @param messageReference      * @param sub      */
name|void
name|messageDiscarded
parameter_list|(
name|ConnectionContext
name|context
parameter_list|,
name|Subscription
name|sub
parameter_list|,
name|MessageReference
name|messageReference
parameter_list|)
function_decl|;
comment|/**      * Called when there is a slow consumer      *      * @param context      * @param subs      */
name|void
name|slowConsumer
parameter_list|(
name|ConnectionContext
name|context
parameter_list|,
name|Subscription
name|subs
parameter_list|)
function_decl|;
comment|/**      * Called to notify a producer is too fast      *      * @param context      * @param producerInfo      */
name|void
name|fastProducer
parameter_list|(
name|ConnectionContext
name|context
parameter_list|,
name|ProducerInfo
name|producerInfo
parameter_list|)
function_decl|;
comment|/**      * Called when a Usage reaches a limit      *      * @param context      * @param usage      */
name|void
name|isFull
parameter_list|(
name|ConnectionContext
name|context
parameter_list|,
name|Usage
argument_list|<
name|?
argument_list|>
name|usage
parameter_list|)
function_decl|;
name|List
argument_list|<
name|Subscription
argument_list|>
name|getConsumers
parameter_list|()
function_decl|;
comment|/**      * called on Queues in slave mode to allow dispatch to follow subscription      * choice of master      *      * @param messageDispatchNotification      * @throws Exception      */
name|void
name|processDispatchNotification
parameter_list|(
name|MessageDispatchNotification
name|messageDispatchNotification
parameter_list|)
throws|throws
name|Exception
function_decl|;
name|boolean
name|isPrioritizedMessages
parameter_list|()
function_decl|;
name|SlowConsumerStrategy
name|getSlowConsumerStrategy
parameter_list|()
function_decl|;
name|boolean
name|isDoOptimzeMessageStorage
parameter_list|()
function_decl|;
name|void
name|setDoOptimzeMessageStorage
parameter_list|(
name|boolean
name|doOptimzeMessageStorage
parameter_list|)
function_decl|;
specifier|public
name|void
name|clearPendingMessages
parameter_list|()
function_decl|;
block|}
end_interface

end_unit

