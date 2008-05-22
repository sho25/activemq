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
operator|.
name|policy
package|;
end_package

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
name|region
operator|.
name|BaseDestination
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
name|DurableTopicSubscription
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
name|Queue
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
name|Topic
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
name|TopicSubscription
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
name|cursors
operator|.
name|PendingMessageCursor
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
name|group
operator|.
name|MessageGroupHashBucketFactory
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
name|group
operator|.
name|MessageGroupMapFactory
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
name|DestinationMapEntry
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

begin_comment
comment|/**  * Represents an entry in a {@link PolicyMap} for assigning policies to a  * specific destination or a hierarchical wildcard area of destinations.  *   * @org.apache.xbean.XBean  * @version $Revision: 1.1 $  */
end_comment

begin_class
specifier|public
class|class
name|PolicyEntry
extends|extends
name|DestinationMapEntry
block|{
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
name|PolicyEntry
operator|.
name|class
argument_list|)
decl_stmt|;
specifier|private
name|DispatchPolicy
name|dispatchPolicy
decl_stmt|;
specifier|private
name|SubscriptionRecoveryPolicy
name|subscriptionRecoveryPolicy
decl_stmt|;
specifier|private
name|boolean
name|sendAdvisoryIfNoConsumers
decl_stmt|;
specifier|private
name|DeadLetterStrategy
name|deadLetterStrategy
decl_stmt|;
specifier|private
name|PendingMessageLimitStrategy
name|pendingMessageLimitStrategy
decl_stmt|;
specifier|private
name|MessageEvictionStrategy
name|messageEvictionStrategy
decl_stmt|;
specifier|private
name|long
name|memoryLimit
decl_stmt|;
specifier|private
name|MessageGroupMapFactory
name|messageGroupMapFactory
decl_stmt|;
specifier|private
name|PendingQueueMessageStoragePolicy
name|pendingQueuePolicy
decl_stmt|;
specifier|private
name|PendingDurableSubscriberMessageStoragePolicy
name|pendingDurableSubscriberPolicy
decl_stmt|;
specifier|private
name|PendingSubscriberMessageStoragePolicy
name|pendingSubscriberPolicy
decl_stmt|;
specifier|private
name|int
name|maxProducersToAudit
init|=
literal|32
decl_stmt|;
specifier|private
name|int
name|maxAuditDepth
init|=
literal|2048
decl_stmt|;
specifier|private
name|int
name|maxQueueAuditDepth
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
name|boolean
name|producerFlowControl
init|=
literal|true
decl_stmt|;
specifier|private
name|boolean
name|optimizedDispatch
init|=
literal|false
decl_stmt|;
specifier|private
name|int
name|maxPageSize
init|=
literal|100
decl_stmt|;
specifier|private
name|boolean
name|useCache
init|=
literal|true
decl_stmt|;
specifier|private
name|long
name|minimumMessageSize
init|=
literal|1024
decl_stmt|;
specifier|private
name|boolean
name|useConsumerPriority
init|=
literal|true
decl_stmt|;
specifier|private
name|boolean
name|strictOrderDispatch
init|=
literal|false
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
specifier|public
name|void
name|configure
parameter_list|(
name|Broker
name|broker
parameter_list|,
name|Queue
name|queue
parameter_list|)
block|{
name|baseConfiguration
argument_list|(
name|queue
argument_list|)
expr_stmt|;
if|if
condition|(
name|dispatchPolicy
operator|!=
literal|null
condition|)
block|{
name|queue
operator|.
name|setDispatchPolicy
argument_list|(
name|dispatchPolicy
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|deadLetterStrategy
operator|!=
literal|null
condition|)
block|{
name|queue
operator|.
name|setDeadLetterStrategy
argument_list|(
name|deadLetterStrategy
argument_list|)
expr_stmt|;
block|}
name|queue
operator|.
name|setMessageGroupMapFactory
argument_list|(
name|getMessageGroupMapFactory
argument_list|()
argument_list|)
expr_stmt|;
if|if
condition|(
name|memoryLimit
operator|>
literal|0
condition|)
block|{
name|queue
operator|.
name|getMemoryUsage
argument_list|()
operator|.
name|setLimit
argument_list|(
name|memoryLimit
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|pendingQueuePolicy
operator|!=
literal|null
condition|)
block|{
name|PendingMessageCursor
name|messages
init|=
name|pendingQueuePolicy
operator|.
name|getQueuePendingMessageCursor
argument_list|(
name|broker
argument_list|,
name|queue
argument_list|)
decl_stmt|;
name|queue
operator|.
name|setMessages
argument_list|(
name|messages
argument_list|)
expr_stmt|;
block|}
name|queue
operator|.
name|setUseConsumerPriority
argument_list|(
name|isUseConsumerPriority
argument_list|()
argument_list|)
expr_stmt|;
name|queue
operator|.
name|setStrictOrderDispatch
argument_list|(
name|isStrictOrderDispatch
argument_list|()
argument_list|)
expr_stmt|;
name|queue
operator|.
name|setOptimizedDispatch
argument_list|(
name|isOptimizedDispatch
argument_list|()
argument_list|)
expr_stmt|;
name|queue
operator|.
name|setLazyDispatch
argument_list|(
name|isLazyDispatch
argument_list|()
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|configure
parameter_list|(
name|Topic
name|topic
parameter_list|)
block|{
name|baseConfiguration
argument_list|(
name|topic
argument_list|)
expr_stmt|;
if|if
condition|(
name|dispatchPolicy
operator|!=
literal|null
condition|)
block|{
name|topic
operator|.
name|setDispatchPolicy
argument_list|(
name|dispatchPolicy
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|deadLetterStrategy
operator|!=
literal|null
condition|)
block|{
name|topic
operator|.
name|setDeadLetterStrategy
argument_list|(
name|deadLetterStrategy
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|subscriptionRecoveryPolicy
operator|!=
literal|null
condition|)
block|{
name|topic
operator|.
name|setSubscriptionRecoveryPolicy
argument_list|(
name|subscriptionRecoveryPolicy
operator|.
name|copy
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|topic
operator|.
name|setSendAdvisoryIfNoConsumers
argument_list|(
name|sendAdvisoryIfNoConsumers
argument_list|)
expr_stmt|;
if|if
condition|(
name|memoryLimit
operator|>
literal|0
condition|)
block|{
name|topic
operator|.
name|getMemoryUsage
argument_list|()
operator|.
name|setLimit
argument_list|(
name|memoryLimit
argument_list|)
expr_stmt|;
block|}
name|topic
operator|.
name|setLazyDispatch
argument_list|(
name|isLazyDispatch
argument_list|()
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|baseConfiguration
parameter_list|(
name|BaseDestination
name|destination
parameter_list|)
block|{
name|destination
operator|.
name|setProducerFlowControl
argument_list|(
name|isProducerFlowControl
argument_list|()
argument_list|)
expr_stmt|;
name|destination
operator|.
name|setEnableAudit
argument_list|(
name|isEnableAudit
argument_list|()
argument_list|)
expr_stmt|;
name|destination
operator|.
name|setMaxAuditDepth
argument_list|(
name|getMaxQueueAuditDepth
argument_list|()
argument_list|)
expr_stmt|;
name|destination
operator|.
name|setMaxProducersToAudit
argument_list|(
name|getMaxProducersToAudit
argument_list|()
argument_list|)
expr_stmt|;
name|destination
operator|.
name|setMaxPageSize
argument_list|(
name|getMaxPageSize
argument_list|()
argument_list|)
expr_stmt|;
name|destination
operator|.
name|setUseCache
argument_list|(
name|isUseCache
argument_list|()
argument_list|)
expr_stmt|;
name|destination
operator|.
name|setMinimumMessageSize
argument_list|(
operator|(
name|int
operator|)
name|getMinimumMessageSize
argument_list|()
argument_list|)
expr_stmt|;
name|destination
operator|.
name|setAdvisoryForConsumed
argument_list|(
name|isAdvisoryForConsumed
argument_list|()
argument_list|)
expr_stmt|;
name|destination
operator|.
name|setAdvisoryForDelivery
argument_list|(
name|isAdvisoryForDelivery
argument_list|()
argument_list|)
expr_stmt|;
name|destination
operator|.
name|setAdvisoryForDiscardingMessages
argument_list|(
name|isAdvisoryForDiscardingMessages
argument_list|()
argument_list|)
expr_stmt|;
name|destination
operator|.
name|setAdvisoryForSlowConsumers
argument_list|(
name|isAdvisoryForSlowConsumers
argument_list|()
argument_list|)
expr_stmt|;
name|destination
operator|.
name|setAdvisdoryForFastProducers
argument_list|(
name|isAdvisdoryForFastProducers
argument_list|()
argument_list|)
expr_stmt|;
name|destination
operator|.
name|setAdvisoryWhenFull
argument_list|(
name|isAdvisoryWhenFull
argument_list|()
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|configure
parameter_list|(
name|Broker
name|broker
parameter_list|,
name|SystemUsage
name|memoryManager
parameter_list|,
name|TopicSubscription
name|subscription
parameter_list|)
block|{
if|if
condition|(
name|pendingMessageLimitStrategy
operator|!=
literal|null
condition|)
block|{
name|int
name|value
init|=
name|pendingMessageLimitStrategy
operator|.
name|getMaximumPendingMessageLimit
argument_list|(
name|subscription
argument_list|)
decl_stmt|;
name|int
name|consumerLimit
init|=
name|subscription
operator|.
name|getInfo
argument_list|()
operator|.
name|getMaximumPendingMessageLimit
argument_list|()
decl_stmt|;
if|if
condition|(
name|consumerLimit
operator|>
literal|0
condition|)
block|{
if|if
condition|(
name|value
operator|<
literal|0
operator|||
name|consumerLimit
operator|<
name|value
condition|)
block|{
name|value
operator|=
name|consumerLimit
expr_stmt|;
block|}
block|}
if|if
condition|(
name|value
operator|>=
literal|0
condition|)
block|{
if|if
condition|(
name|LOG
operator|.
name|isDebugEnabled
argument_list|()
condition|)
block|{
name|LOG
operator|.
name|debug
argument_list|(
literal|"Setting the maximumPendingMessages size to: "
operator|+
name|value
operator|+
literal|" for consumer: "
operator|+
name|subscription
operator|.
name|getInfo
argument_list|()
operator|.
name|getConsumerId
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|subscription
operator|.
name|setMaximumPendingMessages
argument_list|(
name|value
argument_list|)
expr_stmt|;
block|}
block|}
if|if
condition|(
name|messageEvictionStrategy
operator|!=
literal|null
condition|)
block|{
name|subscription
operator|.
name|setMessageEvictionStrategy
argument_list|(
name|messageEvictionStrategy
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|pendingSubscriberPolicy
operator|!=
literal|null
condition|)
block|{
name|String
name|name
init|=
name|subscription
operator|.
name|getContext
argument_list|()
operator|.
name|getClientId
argument_list|()
operator|+
literal|"_"
operator|+
name|subscription
operator|.
name|getConsumerInfo
argument_list|()
operator|.
name|getConsumerId
argument_list|()
decl_stmt|;
name|int
name|maxBatchSize
init|=
name|subscription
operator|.
name|getConsumerInfo
argument_list|()
operator|.
name|getPrefetchSize
argument_list|()
decl_stmt|;
name|subscription
operator|.
name|setMatched
argument_list|(
name|pendingSubscriberPolicy
operator|.
name|getSubscriberPendingMessageCursor
argument_list|(
name|broker
argument_list|,
name|name
argument_list|,
name|maxBatchSize
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
specifier|public
name|void
name|configure
parameter_list|(
name|Broker
name|broker
parameter_list|,
name|SystemUsage
name|memoryManager
parameter_list|,
name|DurableTopicSubscription
name|sub
parameter_list|)
block|{
name|String
name|clientId
init|=
name|sub
operator|.
name|getSubscriptionKey
argument_list|()
operator|.
name|getClientId
argument_list|()
decl_stmt|;
name|String
name|subName
init|=
name|sub
operator|.
name|getSubscriptionKey
argument_list|()
operator|.
name|getSubscriptionName
argument_list|()
decl_stmt|;
name|int
name|prefetch
init|=
name|sub
operator|.
name|getPrefetchSize
argument_list|()
decl_stmt|;
if|if
condition|(
name|pendingDurableSubscriberPolicy
operator|!=
literal|null
condition|)
block|{
name|PendingMessageCursor
name|cursor
init|=
name|pendingDurableSubscriberPolicy
operator|.
name|getSubscriberPendingMessageCursor
argument_list|(
name|broker
argument_list|,
name|clientId
argument_list|,
name|subName
argument_list|,
name|prefetch
argument_list|,
name|sub
argument_list|)
decl_stmt|;
name|cursor
operator|.
name|setSystemUsage
argument_list|(
name|memoryManager
argument_list|)
expr_stmt|;
name|sub
operator|.
name|setPending
argument_list|(
name|cursor
argument_list|)
expr_stmt|;
block|}
name|sub
operator|.
name|setMaxAuditDepth
argument_list|(
name|getMaxAuditDepth
argument_list|()
argument_list|)
expr_stmt|;
name|sub
operator|.
name|setMaxProducersToAudit
argument_list|(
name|getMaxProducersToAudit
argument_list|()
argument_list|)
expr_stmt|;
block|}
comment|// Properties
comment|// -------------------------------------------------------------------------
specifier|public
name|DispatchPolicy
name|getDispatchPolicy
parameter_list|()
block|{
return|return
name|dispatchPolicy
return|;
block|}
specifier|public
name|void
name|setDispatchPolicy
parameter_list|(
name|DispatchPolicy
name|policy
parameter_list|)
block|{
name|this
operator|.
name|dispatchPolicy
operator|=
name|policy
expr_stmt|;
block|}
specifier|public
name|SubscriptionRecoveryPolicy
name|getSubscriptionRecoveryPolicy
parameter_list|()
block|{
return|return
name|subscriptionRecoveryPolicy
return|;
block|}
specifier|public
name|void
name|setSubscriptionRecoveryPolicy
parameter_list|(
name|SubscriptionRecoveryPolicy
name|subscriptionRecoveryPolicy
parameter_list|)
block|{
name|this
operator|.
name|subscriptionRecoveryPolicy
operator|=
name|subscriptionRecoveryPolicy
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
comment|/**      * Sends an advisory message if a non-persistent message is sent and there      * are no active consumers      */
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
specifier|public
name|DeadLetterStrategy
name|getDeadLetterStrategy
parameter_list|()
block|{
return|return
name|deadLetterStrategy
return|;
block|}
comment|/**      * Sets the policy used to determine which dead letter queue destination      * should be used      */
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
name|PendingMessageLimitStrategy
name|getPendingMessageLimitStrategy
parameter_list|()
block|{
return|return
name|pendingMessageLimitStrategy
return|;
block|}
comment|/**      * Sets the strategy to calculate the maximum number of messages that are      * allowed to be pending on consumers (in addition to their prefetch sizes).      * Once the limit is reached, non-durable topics can then start discarding      * old messages. This allows us to keep dispatching messages to slow      * consumers while not blocking fast consumers and discarding the messages      * oldest first.      */
specifier|public
name|void
name|setPendingMessageLimitStrategy
parameter_list|(
name|PendingMessageLimitStrategy
name|pendingMessageLimitStrategy
parameter_list|)
block|{
name|this
operator|.
name|pendingMessageLimitStrategy
operator|=
name|pendingMessageLimitStrategy
expr_stmt|;
block|}
specifier|public
name|MessageEvictionStrategy
name|getMessageEvictionStrategy
parameter_list|()
block|{
return|return
name|messageEvictionStrategy
return|;
block|}
comment|/**      * Sets the eviction strategy used to decide which message to evict when the      * slow consumer needs to discard messages      */
specifier|public
name|void
name|setMessageEvictionStrategy
parameter_list|(
name|MessageEvictionStrategy
name|messageEvictionStrategy
parameter_list|)
block|{
name|this
operator|.
name|messageEvictionStrategy
operator|=
name|messageEvictionStrategy
expr_stmt|;
block|}
specifier|public
name|long
name|getMemoryLimit
parameter_list|()
block|{
return|return
name|memoryLimit
return|;
block|}
comment|/**      * @org.apache.xbean.Property propertyEditor="org.apache.activemq.util.MemoryPropertyEditor"      */
specifier|public
name|void
name|setMemoryLimit
parameter_list|(
name|long
name|memoryLimit
parameter_list|)
block|{
name|this
operator|.
name|memoryLimit
operator|=
name|memoryLimit
expr_stmt|;
block|}
specifier|public
name|MessageGroupMapFactory
name|getMessageGroupMapFactory
parameter_list|()
block|{
if|if
condition|(
name|messageGroupMapFactory
operator|==
literal|null
condition|)
block|{
name|messageGroupMapFactory
operator|=
operator|new
name|MessageGroupHashBucketFactory
argument_list|()
expr_stmt|;
block|}
return|return
name|messageGroupMapFactory
return|;
block|}
comment|/**      * Sets the factory used to create new instances of {MessageGroupMap} used      * to implement the<a      * href="http://activemq.apache.org/message-groups.html">Message Groups</a>      * functionality.      */
specifier|public
name|void
name|setMessageGroupMapFactory
parameter_list|(
name|MessageGroupMapFactory
name|messageGroupMapFactory
parameter_list|)
block|{
name|this
operator|.
name|messageGroupMapFactory
operator|=
name|messageGroupMapFactory
expr_stmt|;
block|}
comment|/**      * @return the pendingDurableSubscriberPolicy      */
specifier|public
name|PendingDurableSubscriberMessageStoragePolicy
name|getPendingDurableSubscriberPolicy
parameter_list|()
block|{
return|return
name|this
operator|.
name|pendingDurableSubscriberPolicy
return|;
block|}
comment|/**      * @param pendingDurableSubscriberPolicy the pendingDurableSubscriberPolicy      *                to set      */
specifier|public
name|void
name|setPendingDurableSubscriberPolicy
parameter_list|(
name|PendingDurableSubscriberMessageStoragePolicy
name|pendingDurableSubscriberPolicy
parameter_list|)
block|{
name|this
operator|.
name|pendingDurableSubscriberPolicy
operator|=
name|pendingDurableSubscriberPolicy
expr_stmt|;
block|}
comment|/**      * @return the pendingQueuePolicy      */
specifier|public
name|PendingQueueMessageStoragePolicy
name|getPendingQueuePolicy
parameter_list|()
block|{
return|return
name|this
operator|.
name|pendingQueuePolicy
return|;
block|}
comment|/**      * @param pendingQueuePolicy the pendingQueuePolicy to set      */
specifier|public
name|void
name|setPendingQueuePolicy
parameter_list|(
name|PendingQueueMessageStoragePolicy
name|pendingQueuePolicy
parameter_list|)
block|{
name|this
operator|.
name|pendingQueuePolicy
operator|=
name|pendingQueuePolicy
expr_stmt|;
block|}
comment|/**      * @return the pendingSubscriberPolicy      */
specifier|public
name|PendingSubscriberMessageStoragePolicy
name|getPendingSubscriberPolicy
parameter_list|()
block|{
return|return
name|this
operator|.
name|pendingSubscriberPolicy
return|;
block|}
comment|/**      * @param pendingSubscriberPolicy the pendingSubscriberPolicy to set      */
specifier|public
name|void
name|setPendingSubscriberPolicy
parameter_list|(
name|PendingSubscriberMessageStoragePolicy
name|pendingSubscriberPolicy
parameter_list|)
block|{
name|this
operator|.
name|pendingSubscriberPolicy
operator|=
name|pendingSubscriberPolicy
expr_stmt|;
block|}
comment|/**      * @return true if producer flow control enabled      */
specifier|public
name|boolean
name|isProducerFlowControl
parameter_list|()
block|{
return|return
name|producerFlowControl
return|;
block|}
comment|/**      * @param producerFlowControl      */
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
name|int
name|getMaxQueueAuditDepth
parameter_list|()
block|{
return|return
name|maxQueueAuditDepth
return|;
block|}
specifier|public
name|void
name|setMaxQueueAuditDepth
parameter_list|(
name|int
name|maxQueueAuditDepth
parameter_list|)
block|{
name|this
operator|.
name|maxQueueAuditDepth
operator|=
name|maxQueueAuditDepth
expr_stmt|;
block|}
specifier|public
name|boolean
name|isOptimizedDispatch
parameter_list|()
block|{
return|return
name|optimizedDispatch
return|;
block|}
specifier|public
name|void
name|setOptimizedDispatch
parameter_list|(
name|boolean
name|optimizedDispatch
parameter_list|)
block|{
name|this
operator|.
name|optimizedDispatch
operator|=
name|optimizedDispatch
expr_stmt|;
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
name|long
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
name|long
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
name|isUseConsumerPriority
parameter_list|()
block|{
return|return
name|useConsumerPriority
return|;
block|}
specifier|public
name|void
name|setUseConsumerPriority
parameter_list|(
name|boolean
name|useConsumerPriority
parameter_list|)
block|{
name|this
operator|.
name|useConsumerPriority
operator|=
name|useConsumerPriority
expr_stmt|;
block|}
specifier|public
name|boolean
name|isStrictOrderDispatch
parameter_list|()
block|{
return|return
name|strictOrderDispatch
return|;
block|}
specifier|public
name|void
name|setStrictOrderDispatch
parameter_list|(
name|boolean
name|strictOrderDispatch
parameter_list|)
block|{
name|this
operator|.
name|strictOrderDispatch
operator|=
name|strictOrderDispatch
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
block|}
end_class

end_unit

