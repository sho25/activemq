begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  *  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  * http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
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
comment|/**  * Represents an entry in a {@link PolicyMap} for assigning policies to a  * specific destination or a hierarchical wildcard area of destinations.  *   * @org.apache.xbean.XBean  *   * @version $Revision: 1.1 $  */
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
name|log
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
specifier|public
name|void
name|configure
parameter_list|(
name|Queue
name|queue
parameter_list|)
block|{
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
name|getUsageManager
argument_list|()
operator|.
name|setLimit
argument_list|(
name|memoryLimit
argument_list|)
expr_stmt|;
block|}
block|}
specifier|public
name|void
name|configure
parameter_list|(
name|Topic
name|topic
parameter_list|)
block|{
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
name|getUsageManager
argument_list|()
operator|.
name|setLimit
argument_list|(
name|memoryLimit
argument_list|)
expr_stmt|;
block|}
block|}
specifier|public
name|void
name|configure
parameter_list|(
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
name|log
operator|.
name|isDebugEnabled
argument_list|()
condition|)
block|{
name|log
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
comment|/**      * Sets the strategy to calculate the maximum number of messages that are      * allowed to be pending on consumers (in addition to their prefetch sizes).      *       * Once the limit is reached, non-durable topics can then start discarding      * old messages. This allows us to keep dispatching messages to slow      * consumers while not blocking fast consumers and discarding the messages      * oldest first.      */
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
comment|/**      * Sets the factory used to create new instances of {MessageGroupMap} used to implement the       *<a href="http://incubator.apache.org/activemq/message-groups.html">Message Groups</a> functionality.      */
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
block|}
end_class

end_unit

