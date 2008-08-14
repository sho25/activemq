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
comment|/**      * The default number of messages to page in to the destination      * from persistent storage      */
specifier|public
specifier|static
specifier|final
name|int
name|DEFAULT_PAGE_SIZE
init|=
literal|200
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
name|DEFAULT_PAGE_SIZE
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
comment|/**      * @param broker       * @param store       * @param destination      * @param parentStats      * @throws Exception       */
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
name|brokerService
operator|.
name|getProducerSystemUsage
argument_list|()
expr_stmt|;
name|this
operator|.
name|memoryUsage
operator|=
operator|new
name|MemoryUsage
argument_list|(
name|systemUsage
operator|.
name|getMemoryUsage
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
comment|/**      * initialize the destination      * @throws Exception      */
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
comment|/**      * @param advisoryForDiscardingMessages the advisoryForDiscardingMessages to set      */
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
comment|/**      * set the dead letter strategy      * @param deadLetterStrategy      */
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
comment|/**      * called when message is consumed      * @param context      * @param messageReference      */
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
comment|/**      * Called when message is delivered to the broker      * @param context      * @param messageReference      */
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
comment|/**      * Called when a message is discarded - e.g. running low on memory      * This will happen only if the policy is enabled - e.g. non durable topics      * @param context      * @param messageReference      */
specifier|public
name|void
name|messageDiscarded
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
name|advisoryForDiscardingMessages
condition|)
block|{
name|broker
operator|.
name|messageDiscarded
argument_list|(
name|context
argument_list|,
name|messageReference
argument_list|)
expr_stmt|;
block|}
block|}
comment|/**      * Called when there is a slow consumer      * @param context      * @param subs      */
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
comment|/**      * Called to notify a producer is too fast      * @param context      * @param producerInfo      */
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
comment|/**      * Called when a Usage reaches a limit      * @param context      * @param usage      */
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
block|}
end_class

end_unit

