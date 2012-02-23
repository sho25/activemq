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
name|jmx
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
name|TopicSubscription
import|;
end_import

begin_comment
comment|/**  *  *  */
end_comment

begin_class
specifier|public
class|class
name|TopicSubscriptionView
extends|extends
name|SubscriptionView
implements|implements
name|TopicSubscriptionViewMBean
block|{
specifier|public
name|TopicSubscriptionView
parameter_list|(
name|String
name|clientId
parameter_list|,
name|String
name|userName
parameter_list|,
name|TopicSubscription
name|subs
parameter_list|)
block|{
name|super
argument_list|(
name|clientId
argument_list|,
name|userName
argument_list|,
name|subs
argument_list|)
expr_stmt|;
block|}
specifier|protected
name|TopicSubscription
name|getTopicSubscription
parameter_list|()
block|{
return|return
operator|(
name|TopicSubscription
operator|)
name|subscription
return|;
block|}
comment|/**      * @return the number of messages discarded due to being a slow consumer      */
specifier|public
name|int
name|getDiscardedCount
parameter_list|()
block|{
name|TopicSubscription
name|topicSubscription
init|=
name|getTopicSubscription
argument_list|()
decl_stmt|;
return|return
name|topicSubscription
operator|!=
literal|null
condition|?
name|topicSubscription
operator|.
name|discarded
argument_list|()
else|:
literal|0
return|;
block|}
comment|/**      * @return the maximun number of messages that can be pending.      */
specifier|public
name|int
name|getMaximumPendingQueueSize
parameter_list|()
block|{
name|TopicSubscription
name|topicSubscription
init|=
name|getTopicSubscription
argument_list|()
decl_stmt|;
return|return
name|topicSubscription
operator|!=
literal|null
condition|?
name|topicSubscription
operator|.
name|getMaximumPendingMessages
argument_list|()
else|:
literal|0
return|;
block|}
comment|/**      *      */
specifier|public
name|void
name|setMaximumPendingQueueSize
parameter_list|(
name|int
name|max
parameter_list|)
block|{
name|TopicSubscription
name|topicSubscription
init|=
name|getTopicSubscription
argument_list|()
decl_stmt|;
if|if
condition|(
name|topicSubscription
operator|!=
literal|null
condition|)
block|{
name|topicSubscription
operator|.
name|setMaximumPendingMessages
argument_list|(
name|max
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|isActive
parameter_list|()
block|{
if|if
condition|(
name|subscription
operator|instanceof
name|DurableTopicSubscription
condition|)
block|{
return|return
operator|(
operator|(
name|DurableTopicSubscription
operator|)
name|subscription
operator|)
operator|.
name|isActive
argument_list|()
return|;
block|}
else|else
block|{
return|return
name|super
operator|.
name|isActive
argument_list|()
return|;
block|}
block|}
block|}
end_class

end_unit

