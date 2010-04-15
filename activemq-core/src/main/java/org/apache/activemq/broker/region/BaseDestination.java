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
name|javax
operator|.
name|jms
operator|.
name|ResourceAllocationException
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
name|SystemUsage
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
name|commons
operator|.
name|logging
operator|.
name|Log
import|;
end_import

begin_comment
comment|/**  * @version $Revision: 1.12 $  */
end_comment

begin_class
specifier|public
specifier|abstract
class|class
name|BaseDestination
implements|implements
name|Destination
block|{
comment|/**      * The maximum number of messages to page in to the destination from      * persistent storage      */
specifier|public
specifier|static
specifier|final
name|int
name|MAX_PAGE_SIZE
init|=
literal|200
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|int
name|MAX_BROWSE_PAGE_SIZE
init|=
name|MAX_PAGE_SIZE
operator|*
literal|2
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|long
name|EXPIRE_MESSAGE_PERIOD
init|=
literal|30
operator|*
literal|1000
decl_stmt|;
specifier|protected
specifier|final
name|ActiveMQDestination
name|destination
decl_stmt|;
specifier|protected
specifier|final
name|Broker
name|broker
decl_stmt|;
specifier|protected
specifier|final
name|MessageStore
name|store
decl_stmt|;
specifier|protected
name|SystemUsage
name|systemUsage
decl_stmt|;
specifier|protected
name|MemoryUsage
name|memoryUsage
decl_stmt|;
specifier|private
name|boolean
name|producerFlowControl
init|=
literal|true
decl_stmt|;
specifier|protected
name|boolean
name|warnOnProducerFlowControl
init|=
literal|true
decl_stmt|;
specifier|protected
name|long
name|blockedProducerWarningInterval
init|=
name|DEFAULT_BLOCKED_PRODUCER_WARNING_INTERVAL
decl_stmt|;
specifier|private
name|int
name|maxProducersToAudit
init|=
literal|1024
decl_stmt|;
specifier|private
name|int
name|maxAuditDepth
init|=
literal|2048
decl_stmt|;
specifier|private
name|boolean
name|enableAudit
init|=
literal|true
decl_stmt|;
specifier|private
name|int
name|maxPageSize
init|=
name|MAX_PAGE_SIZE
decl_stmt|;
specifier|private
name|int
name|maxBrowsePageSize
init|=
name|MAX_BROWSE_PAGE_SIZE
decl_stmt|;
specifier|private
name|boolean
name|useCache
init|=
literal|true
decl_stmt|;
specifier|private
name|int
name|minimumMessageSize
init|=
literal|1024
decl_stmt|;
specifier|private
name|boolean
name|lazyDispatch
init|=
literal|false
decl_stmt|;
specifier|private
name|boolean
name|advisoryForSlowConsumers
decl_stmt|;
specifier|private
name|boolean
name|advisdoryForFastProducers
decl_stmt|;
specifier|private
name|boolean
name|advisoryForDiscardingMessages
decl_stmt|;
specifier|private
name|boolean
name|advisoryWhenFull
decl_stmt|;
specifier|private
name|boolean
name|advisoryForDelivery
decl_stmt|;
specifier|private
name|boolean
name|advisoryForConsumed
decl_stmt|;
specifier|private
name|boolean
name|sendAdvisoryIfNoConsumers
decl_stmt|;
specifier|protected
specifier|final
name|DestinationStatistics
name|destinationStatistics
init|=
operator|new
name|DestinationStatistics
argument_list|()
decl_stmt|;
specifier|protected
specifier|final
name|BrokerService
name|brokerService
decl_stmt|;
specifier|protected
specifier|final
name|Broker
name|regionBroker
decl_stmt|;
specifier|protected
name|DeadLetterStrategy
name|deadLetterStrategy
init|=
name|DEFAULT_DEAD_LETTER_STRATEGY
decl_stmt|;
specifier|protected
name|long
name|expireMessagesPeriod
init|=
name|EXPIRE_MESSAGE_PERIOD
decl_stmt|;
specifier|private
name|int
name|maxExpirePageSize
init|=
name|MAX_BROWSE_PAGE_SIZE
decl_stmt|;
specifier|protected
name|int
name|cursorMemoryHighWaterMark
init|=
literal|70
decl_stmt|;
specifier|protected
name|int
name|storeUsageHighWaterMark
init|=
literal|100
decl_stmt|;
comment|/**      * @param broker      * @param store      * @param destination      * @param parentStats      * @throws Exception      */
specifier|public
name|BaseDestination
parameter_list|(
name|BrokerService
name|brokerService
parameter_list|,
name|MessageStore
name|store
parameter_list|,
name|ActiveMQDestination
name|destination
parameter_list|,
name|DestinationStatistics
name|parentStats
parameter_list|)
throws|throws
name|Exception
block|{
name|this
operator|.
name|brokerService
operator|=
name|brokerService
expr_stmt|;
name|this
operator|.
name|broker
operator|=
name|brokerService
operator|.
name|getBroker
argument_list|()
expr_stmt|;
name|this
operator|.
name|store
operator|=
name|store
expr_stmt|;
name|this
operator|.
name|destination
operator|=
name|destination
expr_stmt|;
comment|// let's copy the enabled property from the parent DestinationStatistics
name|this
operator|.
name|destinationStatistics
operator|.
name|setEnabled
argument_list|(
name|parentStats
operator|.
name|isEnabled
argument_list|()
argument_list|)
expr_stmt|;
name|this
operator|.
name|destinationStatistics
operator|.
name|setParent
argument_list|(
name|parentStats
argument_list|)
expr_stmt|;
name|this
operator|.
name|systemUsage
operator|=
operator|new
name|SystemUsage
argument_list|(
name|brokerService
operator|.
name|getProducerSystemUsage
argument_list|()
argument_list|,
name|destination
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|this
operator|.
name|memoryUsage
operator|=
name|this
operator|.
name|systemUsage
operator|.
name|getMemoryUsage
argument_list|()
expr_stmt|;
name|this
operator|.
name|memoryUsage
operator|.
name|setUsagePortion
argument_list|(
literal|1.0f
argument_list|)
expr_stmt|;
name|this
operator|.
name|regionBroker
operator|=
name|brokerService
operator|.
name|getRegionBroker
argument_list|()
expr_stmt|;
block|}
comment|/**      * initialize the destination      *       * @throws Exception      */
specifier|public
name|void
name|initialize
parameter_list|()
throws|throws
name|Exception
block|{
comment|// Let the store know what usage manager we are using so that he can
comment|// flush messages to disk when usage gets high.
if|if
condition|(
name|store
operator|!=
literal|null
condition|)
block|{
name|store
operator|.
name|setMemoryUsage
argument_list|(
name|this
operator|.
name|memoryUsage
argument_list|)
expr_stmt|;
block|}
block|}
comment|/**      * @return the producerFlowControl      */
specifier|public
name|boolean
name|isProducerFlowControl
parameter_list|()
block|{
return|return
name|producerFlowControl
return|;
block|}
comment|/**      * @param producerFlowControl the producerFlowControl to set      */
specifier|public
name|void
name|setProducerFlowControl
parameter_list|(
name|boolean
name|producerFlowControl
parameter_list|)
block|{
name|this
operator|.
name|producerFlowControl
operator|=
name|producerFlowControl
expr_stmt|;
block|}
comment|/**      * Set's the interval at which warnings about producers being blocked by      * resource usage will be triggered. Values of 0 or less will disable      * warnings      *       * @param blockedProducerWarningInterval the interval at which warning about      *            blocked producers will be triggered.      */
specifier|public
name|void
name|setBlockedProducerWarningInterval
parameter_list|(
name|long
name|blockedProducerWarningInterval
parameter_list|)
block|{
name|this
operator|.
name|blockedProducerWarningInterval
operator|=
name|blockedProducerWarningInterval
expr_stmt|;
block|}
comment|/**      *       * @return the interval at which warning about blocked producers will be      *         triggered.      */
specifier|public
name|long
name|getBlockedProducerWarningInterval
parameter_list|()
block|{
return|return
name|blockedProducerWarningInterval
return|;
block|}
comment|/**      * @return the maxProducersToAudit      */
specifier|public
name|int
name|getMaxProducersToAudit
parameter_list|()
block|{
return|return
name|maxProducersToAudit
return|;
block|}
comment|/**      * @param maxProducersToAudit the maxProducersToAudit to set      */
specifier|public
name|void
name|setMaxProducersToAudit
parameter_list|(
name|int
name|maxProducersToAudit
parameter_list|)
block|{
name|this
operator|.
name|maxProducersToAudit
operator|=
name|maxProducersToAudit
expr_stmt|;
block|}
comment|/**      * @return the maxAuditDepth      */
specifier|public
name|int
name|getMaxAuditDepth
parameter_list|()
block|{
return|return
name|maxAuditDepth
return|;
block|}
comment|/**      * @param maxAuditDepth the maxAuditDepth to set      */
specifier|public
name|void
name|setMaxAuditDepth
parameter_list|(
name|int
name|maxAuditDepth
parameter_list|)
block|{
name|this
operator|.
name|maxAuditDepth
operator|=
name|maxAuditDepth
expr_stmt|;
block|}
comment|/**      * @return the enableAudit      */
specifier|public
name|boolean
name|isEnableAudit
parameter_list|()
block|{
return|return
name|enableAudit
return|;
block|}
comment|/**      * @param enableAudit the enableAudit to set      */
specifier|public
name|void
name|setEnableAudit
parameter_list|(
name|boolean
name|enableAudit
parameter_list|)
block|{
name|this
operator|.
name|enableAudit
operator|=
name|enableAudit
expr_stmt|;
block|}
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
name|destinationStatistics
operator|.
name|getProducers
argument_list|()
operator|.
name|increment
argument_list|()
expr_stmt|;
block|}
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
name|destinationStatistics
operator|.
name|getProducers
argument_list|()
operator|.
name|decrement
argument_list|()
expr_stmt|;
block|}
specifier|public
specifier|final
name|MemoryUsage
name|getMemoryUsage
parameter_list|()
block|{
return|return
name|memoryUsage
return|;
block|}
specifier|public
name|DestinationStatistics
name|getDestinationStatistics
parameter_list|()
block|{
return|return
name|destinationStatistics
return|;
block|}
specifier|public
name|ActiveMQDestination
name|getActiveMQDestination
parameter_list|()
block|{
return|return
name|destination
return|;
block|}
specifier|public
specifier|final
name|String
name|getName
parameter_list|()
block|{
return|return
name|getActiveMQDestination
argument_list|()
operator|.
name|getPhysicalName
argument_list|()
return|;
block|}
specifier|public
specifier|final
name|MessageStore
name|getMessageStore
parameter_list|()
block|{
return|return
name|store
return|;
block|}
specifier|public
specifier|final
name|boolean
name|isActive
parameter_list|()
block|{
return|return
name|destinationStatistics
operator|.
name|getConsumers
argument_list|()
operator|.
name|getCount
argument_list|()
operator|!=
literal|0
operator|||
name|destinationStatistics
operator|.
name|getProducers
argument_list|()
operator|.
name|getCount
argument_list|()
operator|!=
literal|0
return|;
block|}
specifier|public
name|int
name|getMaxPageSize
parameter_list|()
block|{
return|return
name|maxPageSize
return|;
block|}
specifier|public
name|void
name|setMaxPageSize
parameter_list|(
name|int
name|maxPageSize
parameter_list|)
block|{
name|this
operator|.
name|maxPageSize
operator|=
name|maxPageSize
expr_stmt|;
block|}
specifier|public
name|int
name|getMaxBrowsePageSize
parameter_list|()
block|{
return|return
name|this
operator|.
name|maxBrowsePageSize
return|;
block|}
specifier|public
name|void
name|setMaxBrowsePageSize
parameter_list|(
name|int
name|maxPageSize
parameter_list|)
block|{
name|this
operator|.
name|maxBrowsePageSize
operator|=
name|maxPageSize
expr_stmt|;
block|}
specifier|public
name|int
name|getMaxExpirePageSize
parameter_list|()
block|{
return|return
name|this
operator|.
name|maxExpirePageSize
return|;
block|}
specifier|public
name|void
name|setMaxExpirePageSize
parameter_list|(
name|int
name|maxPageSize
parameter_list|)
block|{
name|this
operator|.
name|maxExpirePageSize
operator|=
name|maxPageSize
expr_stmt|;
block|}
specifier|public
name|void
name|setExpireMessagesPeriod
parameter_list|(
name|long
name|expireMessagesPeriod
parameter_list|)
block|{
name|this
operator|.
name|expireMessagesPeriod
operator|=
name|expireMessagesPeriod
expr_stmt|;
block|}
specifier|public
name|long
name|getExpireMessagesPeriod
parameter_list|()
block|{
return|return
name|expireMessagesPeriod
return|;
block|}
specifier|public
name|boolean
name|isUseCache
parameter_list|()
block|{
return|return
name|useCache
return|;
block|}
specifier|public
name|void
name|setUseCache
parameter_list|(
name|boolean
name|useCache
parameter_list|)
block|{
name|this
operator|.
name|useCache
operator|=
name|useCache
expr_stmt|;
block|}
specifier|public
name|int
name|getMinimumMessageSize
parameter_list|()
block|{
return|return
name|minimumMessageSize
return|;
block|}
specifier|public
name|void
name|setMinimumMessageSize
parameter_list|(
name|int
name|minimumMessageSize
parameter_list|)
block|{
name|this
operator|.
name|minimumMessageSize
operator|=
name|minimumMessageSize
expr_stmt|;
block|}
specifier|public
name|boolean
name|isLazyDispatch
parameter_list|()
block|{
return|return
name|lazyDispatch
return|;
block|}
specifier|public
name|void
name|setLazyDispatch
parameter_list|(
name|boolean
name|lazyDispatch
parameter_list|)
block|{
name|this
operator|.
name|lazyDispatch
operator|=
name|lazyDispatch
expr_stmt|;
block|}
specifier|protected
name|long
name|getDestinationSequenceId
parameter_list|()
block|{
return|return
name|regionBroker
operator|.
name|getBrokerSequenceId
argument_list|()
return|;
block|}
comment|/**      * @return the advisoryForSlowConsumers      */
specifier|public
name|boolean
name|isAdvisoryForSlowConsumers
parameter_list|()
block|{
return|return
name|advisoryForSlowConsumers
return|;
block|}
comment|/**      * @param advisoryForSlowConsumers the advisoryForSlowConsumers to set      */
specifier|public
name|void
name|setAdvisoryForSlowConsumers
parameter_list|(
name|boolean
name|advisoryForSlowConsumers
parameter_list|)
block|{
name|this
operator|.
name|advisoryForSlowConsumers
operator|=
name|advisoryForSlowConsumers
expr_stmt|;
block|}
comment|/**      * @return the advisoryForDiscardingMessages      */
specifier|public
name|boolean
name|isAdvisoryForDiscardingMessages
parameter_list|()
block|{
return|return
name|advisoryForDiscardingMessages
return|;
block|}
comment|/**      * @param advisoryForDiscardingMessages the advisoryForDiscardingMessages to      *            set      */
specifier|public
name|void
name|setAdvisoryForDiscardingMessages
parameter_list|(
name|boolean
name|advisoryForDiscardingMessages
parameter_list|)
block|{
name|this
operator|.
name|advisoryForDiscardingMessages
operator|=
name|advisoryForDiscardingMessages
expr_stmt|;
block|}
comment|/**      * @return the advisoryWhenFull      */
specifier|public
name|boolean
name|isAdvisoryWhenFull
parameter_list|()
block|{
return|return
name|advisoryWhenFull
return|;
block|}
comment|/**      * @param advisoryWhenFull the advisoryWhenFull to set      */
specifier|public
name|void
name|setAdvisoryWhenFull
parameter_list|(
name|boolean
name|advisoryWhenFull
parameter_list|)
block|{
name|this
operator|.
name|advisoryWhenFull
operator|=
name|advisoryWhenFull
expr_stmt|;
block|}
comment|/**      * @return the advisoryForDelivery      */
specifier|public
name|boolean
name|isAdvisoryForDelivery
parameter_list|()
block|{
return|return
name|advisoryForDelivery
return|;
block|}
comment|/**      * @param advisoryForDelivery the advisoryForDelivery to set      */
specifier|public
name|void
name|setAdvisoryForDelivery
parameter_list|(
name|boolean
name|advisoryForDelivery
parameter_list|)
block|{
name|this
operator|.
name|advisoryForDelivery
operator|=
name|advisoryForDelivery
expr_stmt|;
block|}
comment|/**      * @return the advisoryForConsumed      */
specifier|public
name|boolean
name|isAdvisoryForConsumed
parameter_list|()
block|{
return|return
name|advisoryForConsumed
return|;
block|}
comment|/**      * @param advisoryForConsumed the advisoryForConsumed to set      */
specifier|public
name|void
name|setAdvisoryForConsumed
parameter_list|(
name|boolean
name|advisoryForConsumed
parameter_list|)
block|{
name|this
operator|.
name|advisoryForConsumed
operator|=
name|advisoryForConsumed
expr_stmt|;
block|}
comment|/**      * @return the advisdoryForFastProducers      */
specifier|public
name|boolean
name|isAdvisdoryForFastProducers
parameter_list|()
block|{
return|return
name|advisdoryForFastProducers
return|;
block|}
comment|/**      * @param advisdoryForFastProducers the advisdoryForFastProducers to set      */
specifier|public
name|void
name|setAdvisdoryForFastProducers
parameter_list|(
name|boolean
name|advisdoryForFastProducers
parameter_list|)
block|{
name|this
operator|.
name|advisdoryForFastProducers
operator|=
name|advisdoryForFastProducers
expr_stmt|;
block|}
specifier|public
name|boolean
name|isSendAdvisoryIfNoConsumers
parameter_list|()
block|{
return|return
name|sendAdvisoryIfNoConsumers
return|;
block|}
specifier|public
name|void
name|setSendAdvisoryIfNoConsumers
parameter_list|(
name|boolean
name|sendAdvisoryIfNoConsumers
parameter_list|)
block|{
name|this
operator|.
name|sendAdvisoryIfNoConsumers
operator|=
name|sendAdvisoryIfNoConsumers
expr_stmt|;
block|}
comment|/**      * @return the dead letter strategy      */
specifier|public
name|DeadLetterStrategy
name|getDeadLetterStrategy
parameter_list|()
block|{
return|return
name|deadLetterStrategy
return|;
block|}
comment|/**      * set the dead letter strategy      *       * @param deadLetterStrategy      */
specifier|public
name|void
name|setDeadLetterStrategy
parameter_list|(
name|DeadLetterStrategy
name|deadLetterStrategy
parameter_list|)
block|{
name|this
operator|.
name|deadLetterStrategy
operator|=
name|deadLetterStrategy
expr_stmt|;
block|}
specifier|public
name|int
name|getCursorMemoryHighWaterMark
parameter_list|()
block|{
return|return
name|this
operator|.
name|cursorMemoryHighWaterMark
return|;
block|}
specifier|public
name|void
name|setCursorMemoryHighWaterMark
parameter_list|(
name|int
name|cursorMemoryHighWaterMark
parameter_list|)
block|{
name|this
operator|.
name|cursorMemoryHighWaterMark
operator|=
name|cursorMemoryHighWaterMark
expr_stmt|;
block|}
comment|/**      * called when message is consumed      *       * @param context      * @param messageReference      */
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
if|if
condition|(
name|advisoryForConsumed
condition|)
block|{
name|broker
operator|.
name|messageConsumed
argument_list|(
name|context
argument_list|,
name|messageReference
argument_list|)
expr_stmt|;
block|}
block|}
comment|/**      * Called when message is delivered to the broker      *       * @param context      * @param messageReference      */
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
if|if
condition|(
name|advisoryForDelivery
condition|)
block|{
name|broker
operator|.
name|messageDelivered
argument_list|(
name|context
argument_list|,
name|messageReference
argument_list|)
expr_stmt|;
block|}
block|}
comment|/**      * Called when a message is discarded - e.g. running low on memory This will      * happen only if the policy is enabled - e.g. non durable topics      *       * @param context      * @param messageReference      */
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
if|if
condition|(
name|advisoryForDiscardingMessages
condition|)
block|{
name|broker
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
block|}
comment|/**      * Called when there is a slow consumer      *       * @param context      * @param subs      */
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
if|if
condition|(
name|advisoryForSlowConsumers
condition|)
block|{
name|broker
operator|.
name|slowConsumer
argument_list|(
name|context
argument_list|,
name|this
argument_list|,
name|subs
argument_list|)
expr_stmt|;
block|}
block|}
comment|/**      * Called to notify a producer is too fast      *       * @param context      * @param producerInfo      */
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
if|if
condition|(
name|advisdoryForFastProducers
condition|)
block|{
name|broker
operator|.
name|fastProducer
argument_list|(
name|context
argument_list|,
name|producerInfo
argument_list|)
expr_stmt|;
block|}
block|}
comment|/**      * Called when a Usage reaches a limit      *       * @param context      * @param usage      */
specifier|public
name|void
name|isFull
parameter_list|(
name|ConnectionContext
name|context
parameter_list|,
name|Usage
name|usage
parameter_list|)
block|{
if|if
condition|(
name|advisoryWhenFull
condition|)
block|{
name|broker
operator|.
name|isFull
argument_list|(
name|context
argument_list|,
name|this
argument_list|,
name|usage
argument_list|)
expr_stmt|;
block|}
block|}
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
if|if
condition|(
name|this
operator|.
name|store
operator|!=
literal|null
condition|)
block|{
name|this
operator|.
name|store
operator|.
name|removeAllMessages
argument_list|(
name|context
argument_list|)
expr_stmt|;
name|this
operator|.
name|store
operator|.
name|dispose
argument_list|(
name|context
argument_list|)
expr_stmt|;
block|}
name|this
operator|.
name|destinationStatistics
operator|.
name|setParent
argument_list|(
literal|null
argument_list|)
expr_stmt|;
name|this
operator|.
name|memoryUsage
operator|.
name|stop
argument_list|()
expr_stmt|;
block|}
comment|/**      * Provides a hook to allow messages with no consumer to be processed in      * some way - such as to send to a dead letter queue or something..      */
specifier|protected
name|void
name|onMessageWithNoConsumers
parameter_list|(
name|ConnectionContext
name|context
parameter_list|,
name|Message
name|msg
parameter_list|)
throws|throws
name|Exception
block|{
if|if
condition|(
operator|!
name|msg
operator|.
name|isPersistent
argument_list|()
condition|)
block|{
if|if
condition|(
name|isSendAdvisoryIfNoConsumers
argument_list|()
condition|)
block|{
comment|// allow messages with no consumers to be dispatched to a dead
comment|// letter queue
if|if
condition|(
name|destination
operator|.
name|isQueue
argument_list|()
operator|||
operator|!
name|AdvisorySupport
operator|.
name|isAdvisoryTopic
argument_list|(
name|destination
argument_list|)
condition|)
block|{
name|Message
name|message
init|=
name|msg
operator|.
name|copy
argument_list|()
decl_stmt|;
comment|// The original destination and transaction id do not get
comment|// filled when the message is first sent,
comment|// it is only populated if the message is routed to another
comment|// destination like the DLQ
if|if
condition|(
name|message
operator|.
name|getOriginalDestination
argument_list|()
operator|!=
literal|null
condition|)
block|{
name|message
operator|.
name|setOriginalDestination
argument_list|(
name|message
operator|.
name|getDestination
argument_list|()
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|message
operator|.
name|getOriginalTransactionId
argument_list|()
operator|!=
literal|null
condition|)
block|{
name|message
operator|.
name|setOriginalTransactionId
argument_list|(
name|message
operator|.
name|getTransactionId
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|ActiveMQTopic
name|advisoryTopic
decl_stmt|;
if|if
condition|(
name|destination
operator|.
name|isQueue
argument_list|()
condition|)
block|{
name|advisoryTopic
operator|=
name|AdvisorySupport
operator|.
name|getNoQueueConsumersAdvisoryTopic
argument_list|(
name|destination
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|advisoryTopic
operator|=
name|AdvisorySupport
operator|.
name|getNoTopicConsumersAdvisoryTopic
argument_list|(
name|destination
argument_list|)
expr_stmt|;
block|}
name|message
operator|.
name|setDestination
argument_list|(
name|advisoryTopic
argument_list|)
expr_stmt|;
name|message
operator|.
name|setTransactionId
argument_list|(
literal|null
argument_list|)
expr_stmt|;
comment|// Disable flow control for this since since we don't want
comment|// to block.
name|boolean
name|originalFlowControl
init|=
name|context
operator|.
name|isProducerFlowControl
argument_list|()
decl_stmt|;
try|try
block|{
name|context
operator|.
name|setProducerFlowControl
argument_list|(
literal|false
argument_list|)
expr_stmt|;
name|ProducerBrokerExchange
name|producerExchange
init|=
operator|new
name|ProducerBrokerExchange
argument_list|()
decl_stmt|;
name|producerExchange
operator|.
name|setMutable
argument_list|(
literal|false
argument_list|)
expr_stmt|;
name|producerExchange
operator|.
name|setConnectionContext
argument_list|(
name|context
argument_list|)
expr_stmt|;
name|producerExchange
operator|.
name|setProducerState
argument_list|(
operator|new
name|ProducerState
argument_list|(
operator|new
name|ProducerInfo
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|context
operator|.
name|getBroker
argument_list|()
operator|.
name|send
argument_list|(
name|producerExchange
argument_list|,
name|message
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|context
operator|.
name|setProducerFlowControl
argument_list|(
name|originalFlowControl
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|}
block|}
specifier|public
name|void
name|processDispatchNotification
parameter_list|(
name|MessageDispatchNotification
name|messageDispatchNotification
parameter_list|)
throws|throws
name|Exception
block|{     }
specifier|public
specifier|final
name|int
name|getStoreUsageHighWaterMark
parameter_list|()
block|{
return|return
name|this
operator|.
name|storeUsageHighWaterMark
return|;
block|}
specifier|public
name|void
name|setStoreUsageHighWaterMark
parameter_list|(
name|int
name|storeUsageHighWaterMark
parameter_list|)
block|{
name|this
operator|.
name|storeUsageHighWaterMark
operator|=
name|storeUsageHighWaterMark
expr_stmt|;
block|}
specifier|protected
specifier|final
name|void
name|waitForSpace
parameter_list|(
name|ConnectionContext
name|context
parameter_list|,
name|Usage
argument_list|<
name|?
argument_list|>
name|usage
parameter_list|,
name|String
name|warning
parameter_list|)
throws|throws
name|IOException
throws|,
name|InterruptedException
throws|,
name|ResourceAllocationException
block|{
name|waitForSpace
argument_list|(
name|context
argument_list|,
name|usage
argument_list|,
literal|100
argument_list|,
name|warning
argument_list|)
expr_stmt|;
block|}
specifier|protected
specifier|final
name|void
name|waitForSpace
parameter_list|(
name|ConnectionContext
name|context
parameter_list|,
name|Usage
argument_list|<
name|?
argument_list|>
name|usage
parameter_list|,
name|int
name|highWaterMark
parameter_list|,
name|String
name|warning
parameter_list|)
throws|throws
name|IOException
throws|,
name|InterruptedException
throws|,
name|ResourceAllocationException
block|{
if|if
condition|(
name|systemUsage
operator|.
name|getSendFailIfNoSpaceAfterTimeout
argument_list|()
operator|!=
literal|0
condition|)
block|{
if|if
condition|(
operator|!
name|usage
operator|.
name|waitForSpace
argument_list|(
name|systemUsage
operator|.
name|getSendFailIfNoSpaceAfterTimeout
argument_list|()
argument_list|,
name|highWaterMark
argument_list|)
condition|)
block|{
throw|throw
operator|new
name|ResourceAllocationException
argument_list|(
name|warning
argument_list|)
throw|;
block|}
block|}
else|else
block|{
name|long
name|start
init|=
name|System
operator|.
name|currentTimeMillis
argument_list|()
decl_stmt|;
name|long
name|nextWarn
init|=
name|start
decl_stmt|;
while|while
condition|(
operator|!
name|usage
operator|.
name|waitForSpace
argument_list|(
literal|1000
argument_list|,
name|highWaterMark
argument_list|)
condition|)
block|{
if|if
condition|(
name|context
operator|.
name|getStopping
argument_list|()
operator|.
name|get
argument_list|()
condition|)
block|{
throw|throw
operator|new
name|IOException
argument_list|(
literal|"Connection closed, send aborted."
argument_list|)
throw|;
block|}
name|long
name|now
init|=
name|System
operator|.
name|currentTimeMillis
argument_list|()
decl_stmt|;
if|if
condition|(
name|now
operator|>=
name|nextWarn
condition|)
block|{
name|getLog
argument_list|()
operator|.
name|info
argument_list|(
name|warning
operator|+
literal|" (blocking for: "
operator|+
operator|(
name|now
operator|-
name|start
operator|)
operator|/
literal|1000
operator|+
literal|"s)"
argument_list|)
expr_stmt|;
name|nextWarn
operator|=
name|now
operator|+
name|blockedProducerWarningInterval
expr_stmt|;
block|}
block|}
block|}
block|}
specifier|protected
specifier|abstract
name|Log
name|getLog
parameter_list|()
function_decl|;
block|}
end_class

end_unit

