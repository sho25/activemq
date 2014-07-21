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
name|java
operator|.
name|util
operator|.
name|Set
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
name|SubscriptionKey
import|;
end_import

begin_comment
comment|/**  *  *  */
end_comment

begin_class
specifier|public
class|class
name|DestinationFilter
implements|implements
name|Destination
block|{
specifier|protected
specifier|final
name|Destination
name|next
decl_stmt|;
specifier|public
name|DestinationFilter
parameter_list|(
name|Destination
name|next
parameter_list|)
block|{
name|this
operator|.
name|next
operator|=
name|next
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|acknowledge
parameter_list|(
name|ConnectionContext
name|context
parameter_list|,
name|Subscription
name|sub
parameter_list|,
name|MessageAck
name|ack
parameter_list|,
name|MessageReference
name|node
parameter_list|)
throws|throws
name|IOException
block|{
name|next
operator|.
name|acknowledge
argument_list|(
name|context
argument_list|,
name|sub
argument_list|,
name|ack
argument_list|,
name|node
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
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
block|{
name|next
operator|.
name|addSubscription
argument_list|(
name|context
argument_list|,
name|sub
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|Message
index|[]
name|browse
parameter_list|()
block|{
return|return
name|next
operator|.
name|browse
argument_list|()
return|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|dispose
parameter_list|(
name|ConnectionContext
name|context
parameter_list|)
throws|throws
name|IOException
block|{
name|next
operator|.
name|dispose
argument_list|(
name|context
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|isDisposed
parameter_list|()
block|{
return|return
name|next
operator|.
name|isDisposed
argument_list|()
return|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|gc
parameter_list|()
block|{
name|next
operator|.
name|gc
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|markForGC
parameter_list|(
name|long
name|timeStamp
parameter_list|)
block|{
name|next
operator|.
name|markForGC
argument_list|(
name|timeStamp
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|canGC
parameter_list|()
block|{
return|return
name|next
operator|.
name|canGC
argument_list|()
return|;
block|}
annotation|@
name|Override
specifier|public
name|long
name|getInactiveTimeoutBeforeGC
parameter_list|()
block|{
return|return
name|next
operator|.
name|getInactiveTimeoutBeforeGC
argument_list|()
return|;
block|}
annotation|@
name|Override
specifier|public
name|ActiveMQDestination
name|getActiveMQDestination
parameter_list|()
block|{
return|return
name|next
operator|.
name|getActiveMQDestination
argument_list|()
return|;
block|}
annotation|@
name|Override
specifier|public
name|DeadLetterStrategy
name|getDeadLetterStrategy
parameter_list|()
block|{
return|return
name|next
operator|.
name|getDeadLetterStrategy
argument_list|()
return|;
block|}
annotation|@
name|Override
specifier|public
name|DestinationStatistics
name|getDestinationStatistics
parameter_list|()
block|{
return|return
name|next
operator|.
name|getDestinationStatistics
argument_list|()
return|;
block|}
annotation|@
name|Override
specifier|public
name|String
name|getName
parameter_list|()
block|{
return|return
name|next
operator|.
name|getName
argument_list|()
return|;
block|}
annotation|@
name|Override
specifier|public
name|MemoryUsage
name|getMemoryUsage
parameter_list|()
block|{
return|return
name|next
operator|.
name|getMemoryUsage
argument_list|()
return|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|setMemoryUsage
parameter_list|(
name|MemoryUsage
name|memoryUsage
parameter_list|)
block|{
name|next
operator|.
name|setMemoryUsage
argument_list|(
name|memoryUsage
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
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
block|{
name|next
operator|.
name|removeSubscription
argument_list|(
name|context
argument_list|,
name|sub
argument_list|,
name|lastDeliveredSequenceId
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|send
parameter_list|(
name|ProducerBrokerExchange
name|context
parameter_list|,
name|Message
name|messageSend
parameter_list|)
throws|throws
name|Exception
block|{
name|next
operator|.
name|send
argument_list|(
name|context
argument_list|,
name|messageSend
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|start
parameter_list|()
throws|throws
name|Exception
block|{
name|next
operator|.
name|start
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|stop
parameter_list|()
throws|throws
name|Exception
block|{
name|next
operator|.
name|stop
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|List
argument_list|<
name|Subscription
argument_list|>
name|getConsumers
parameter_list|()
block|{
return|return
name|next
operator|.
name|getConsumers
argument_list|()
return|;
block|}
comment|/**      * Sends a message to the given destination which may be a wildcard      *      * @param context broker context      * @param message message to send      * @param destination possibly wildcard destination to send the message to      * @throws Exception on error      */
specifier|protected
name|void
name|send
parameter_list|(
name|ProducerBrokerExchange
name|context
parameter_list|,
name|Message
name|message
parameter_list|,
name|ActiveMQDestination
name|destination
parameter_list|)
throws|throws
name|Exception
block|{
name|Broker
name|broker
init|=
name|context
operator|.
name|getConnectionContext
argument_list|()
operator|.
name|getBroker
argument_list|()
decl_stmt|;
name|Set
argument_list|<
name|Destination
argument_list|>
name|destinations
init|=
name|broker
operator|.
name|getDestinations
argument_list|(
name|destination
argument_list|)
decl_stmt|;
for|for
control|(
name|Destination
name|dest
range|:
name|destinations
control|)
block|{
name|dest
operator|.
name|send
argument_list|(
name|context
argument_list|,
name|message
operator|.
name|copy
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Override
specifier|public
name|MessageStore
name|getMessageStore
parameter_list|()
block|{
return|return
name|next
operator|.
name|getMessageStore
argument_list|()
return|;
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|isProducerFlowControl
parameter_list|()
block|{
return|return
name|next
operator|.
name|isProducerFlowControl
argument_list|()
return|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|setProducerFlowControl
parameter_list|(
name|boolean
name|value
parameter_list|)
block|{
name|next
operator|.
name|setProducerFlowControl
argument_list|(
name|value
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|isAlwaysRetroactive
parameter_list|()
block|{
return|return
name|next
operator|.
name|isAlwaysRetroactive
argument_list|()
return|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|setAlwaysRetroactive
parameter_list|(
name|boolean
name|value
parameter_list|)
block|{
name|next
operator|.
name|setAlwaysRetroactive
argument_list|(
name|value
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|setBlockedProducerWarningInterval
parameter_list|(
name|long
name|blockedProducerWarningInterval
parameter_list|)
block|{
name|next
operator|.
name|setBlockedProducerWarningInterval
argument_list|(
name|blockedProducerWarningInterval
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|long
name|getBlockedProducerWarningInterval
parameter_list|()
block|{
return|return
name|next
operator|.
name|getBlockedProducerWarningInterval
argument_list|()
return|;
block|}
annotation|@
name|Override
specifier|public
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
block|{
name|next
operator|.
name|addProducer
argument_list|(
name|context
argument_list|,
name|info
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
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
block|{
name|next
operator|.
name|removeProducer
argument_list|(
name|context
argument_list|,
name|info
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|int
name|getMaxAuditDepth
parameter_list|()
block|{
return|return
name|next
operator|.
name|getMaxAuditDepth
argument_list|()
return|;
block|}
annotation|@
name|Override
specifier|public
name|int
name|getMaxProducersToAudit
parameter_list|()
block|{
return|return
name|next
operator|.
name|getMaxProducersToAudit
argument_list|()
return|;
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|isEnableAudit
parameter_list|()
block|{
return|return
name|next
operator|.
name|isEnableAudit
argument_list|()
return|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|setEnableAudit
parameter_list|(
name|boolean
name|enableAudit
parameter_list|)
block|{
name|next
operator|.
name|setEnableAudit
argument_list|(
name|enableAudit
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|setMaxAuditDepth
parameter_list|(
name|int
name|maxAuditDepth
parameter_list|)
block|{
name|next
operator|.
name|setMaxAuditDepth
argument_list|(
name|maxAuditDepth
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|setMaxProducersToAudit
parameter_list|(
name|int
name|maxProducersToAudit
parameter_list|)
block|{
name|next
operator|.
name|setMaxProducersToAudit
argument_list|(
name|maxProducersToAudit
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|isActive
parameter_list|()
block|{
return|return
name|next
operator|.
name|isActive
argument_list|()
return|;
block|}
annotation|@
name|Override
specifier|public
name|int
name|getMaxPageSize
parameter_list|()
block|{
return|return
name|next
operator|.
name|getMaxPageSize
argument_list|()
return|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|setMaxPageSize
parameter_list|(
name|int
name|maxPageSize
parameter_list|)
block|{
name|next
operator|.
name|setMaxPageSize
argument_list|(
name|maxPageSize
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|isUseCache
parameter_list|()
block|{
return|return
name|next
operator|.
name|isUseCache
argument_list|()
return|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|setUseCache
parameter_list|(
name|boolean
name|useCache
parameter_list|)
block|{
name|next
operator|.
name|setUseCache
argument_list|(
name|useCache
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|int
name|getMinimumMessageSize
parameter_list|()
block|{
return|return
name|next
operator|.
name|getMinimumMessageSize
argument_list|()
return|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|setMinimumMessageSize
parameter_list|(
name|int
name|minimumMessageSize
parameter_list|)
block|{
name|next
operator|.
name|setMinimumMessageSize
argument_list|(
name|minimumMessageSize
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|wakeup
parameter_list|()
block|{
name|next
operator|.
name|wakeup
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|isLazyDispatch
parameter_list|()
block|{
return|return
name|next
operator|.
name|isLazyDispatch
argument_list|()
return|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|setLazyDispatch
parameter_list|(
name|boolean
name|value
parameter_list|)
block|{
name|next
operator|.
name|setLazyDispatch
argument_list|(
name|value
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|messageExpired
parameter_list|(
name|ConnectionContext
name|context
parameter_list|,
name|PrefetchSubscription
name|prefetchSubscription
parameter_list|,
name|MessageReference
name|node
parameter_list|)
block|{
name|next
operator|.
name|messageExpired
argument_list|(
name|context
argument_list|,
name|prefetchSubscription
argument_list|,
name|node
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|iterate
parameter_list|()
block|{
return|return
name|next
operator|.
name|iterate
argument_list|()
return|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|fastProducer
parameter_list|(
name|ConnectionContext
name|context
parameter_list|,
name|ProducerInfo
name|producerInfo
parameter_list|)
block|{
name|next
operator|.
name|fastProducer
argument_list|(
name|context
argument_list|,
name|producerInfo
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
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
block|{
name|next
operator|.
name|isFull
argument_list|(
name|context
argument_list|,
name|usage
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|messageConsumed
parameter_list|(
name|ConnectionContext
name|context
parameter_list|,
name|MessageReference
name|messageReference
parameter_list|)
block|{
name|next
operator|.
name|messageConsumed
argument_list|(
name|context
argument_list|,
name|messageReference
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|messageDelivered
parameter_list|(
name|ConnectionContext
name|context
parameter_list|,
name|MessageReference
name|messageReference
parameter_list|)
block|{
name|next
operator|.
name|messageDelivered
argument_list|(
name|context
argument_list|,
name|messageReference
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
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
block|{
name|next
operator|.
name|messageDiscarded
argument_list|(
name|context
argument_list|,
name|sub
argument_list|,
name|messageReference
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|slowConsumer
parameter_list|(
name|ConnectionContext
name|context
parameter_list|,
name|Subscription
name|subs
parameter_list|)
block|{
name|next
operator|.
name|slowConsumer
argument_list|(
name|context
argument_list|,
name|subs
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
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
block|{
name|next
operator|.
name|messageExpired
argument_list|(
name|context
argument_list|,
name|subs
argument_list|,
name|node
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|int
name|getMaxBrowsePageSize
parameter_list|()
block|{
return|return
name|next
operator|.
name|getMaxBrowsePageSize
argument_list|()
return|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|setMaxBrowsePageSize
parameter_list|(
name|int
name|maxPageSize
parameter_list|)
block|{
name|next
operator|.
name|setMaxBrowsePageSize
argument_list|(
name|maxPageSize
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|processDispatchNotification
parameter_list|(
name|MessageDispatchNotification
name|messageDispatchNotification
parameter_list|)
throws|throws
name|Exception
block|{
name|next
operator|.
name|processDispatchNotification
argument_list|(
name|messageDispatchNotification
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|int
name|getCursorMemoryHighWaterMark
parameter_list|()
block|{
return|return
name|next
operator|.
name|getCursorMemoryHighWaterMark
argument_list|()
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
block|{
name|next
operator|.
name|setCursorMemoryHighWaterMark
argument_list|(
name|cursorMemoryHighWaterMark
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|isPrioritizedMessages
parameter_list|()
block|{
return|return
name|next
operator|.
name|isPrioritizedMessages
argument_list|()
return|;
block|}
annotation|@
name|Override
specifier|public
name|SlowConsumerStrategy
name|getSlowConsumerStrategy
parameter_list|()
block|{
return|return
name|next
operator|.
name|getSlowConsumerStrategy
argument_list|()
return|;
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|isDoOptimzeMessageStorage
parameter_list|()
block|{
return|return
name|next
operator|.
name|isDoOptimzeMessageStorage
argument_list|()
return|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|setDoOptimzeMessageStorage
parameter_list|(
name|boolean
name|doOptimzeMessageStorage
parameter_list|)
block|{
name|next
operator|.
name|setDoOptimzeMessageStorage
argument_list|(
name|doOptimzeMessageStorage
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|clearPendingMessages
parameter_list|()
block|{
name|next
operator|.
name|clearPendingMessages
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|isDLQ
parameter_list|()
block|{
return|return
name|next
operator|.
name|isDLQ
argument_list|()
return|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|duplicateFromStore
parameter_list|(
name|Message
name|message
parameter_list|,
name|Subscription
name|subscription
parameter_list|)
block|{
name|next
operator|.
name|duplicateFromStore
argument_list|(
name|message
argument_list|,
name|subscription
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|deleteSubscription
parameter_list|(
name|ConnectionContext
name|context
parameter_list|,
name|SubscriptionKey
name|key
parameter_list|)
throws|throws
name|Exception
block|{
if|if
condition|(
name|next
operator|instanceof
name|DestinationFilter
condition|)
block|{
name|DestinationFilter
name|filter
init|=
operator|(
name|DestinationFilter
operator|)
name|next
decl_stmt|;
name|filter
operator|.
name|deleteSubscription
argument_list|(
name|context
argument_list|,
name|key
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|next
operator|instanceof
name|Topic
condition|)
block|{
name|Topic
name|topic
init|=
operator|(
name|Topic
operator|)
name|next
decl_stmt|;
name|topic
operator|.
name|deleteSubscription
argument_list|(
name|context
argument_list|,
name|key
argument_list|)
expr_stmt|;
block|}
block|}
specifier|public
name|Destination
name|getNext
parameter_list|()
block|{
return|return
name|next
return|;
block|}
block|}
end_class

end_unit

