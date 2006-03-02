begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  *  * Copyright 2005-2006 The Apache Software Foundation  *  * Licensed under the Apache License, Version 2.0 (the "License");  * you may not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  * http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
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
name|Subscription
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
name|ConsumerInfo
import|;
end_import

begin_comment
comment|/**  * @version $Revision: 1.5 $  */
end_comment

begin_class
specifier|public
class|class
name|SubscriptionView
implements|implements
name|SubscriptionViewMBean
block|{
specifier|protected
specifier|final
name|Subscription
name|subscription
decl_stmt|;
specifier|protected
specifier|final
name|String
name|clientId
decl_stmt|;
comment|/**      * Constructior      * @param subs      */
specifier|public
name|SubscriptionView
parameter_list|(
name|String
name|clientId
parameter_list|,
name|Subscription
name|subs
parameter_list|)
block|{
name|this
operator|.
name|clientId
operator|=
name|clientId
expr_stmt|;
name|this
operator|.
name|subscription
operator|=
name|subs
expr_stmt|;
block|}
comment|/**      * @return the clientId      */
specifier|public
name|String
name|getClientId
parameter_list|()
block|{
return|return
name|clientId
return|;
block|}
comment|/**      * @return the id of the Connection the Subscription is on      */
specifier|public
name|String
name|getConnectionId
parameter_list|()
block|{
name|ConsumerInfo
name|info
init|=
name|getConsumerInfo
argument_list|()
decl_stmt|;
if|if
condition|(
name|info
operator|!=
literal|null
condition|)
block|{
return|return
name|info
operator|.
name|getConsumerId
argument_list|()
operator|.
name|getConnectionId
argument_list|()
return|;
block|}
return|return
literal|"NOTSET"
return|;
block|}
comment|/**      * @return the id of the Session the subscription is on      */
specifier|public
name|long
name|getSessionId
parameter_list|()
block|{
name|ConsumerInfo
name|info
init|=
name|getConsumerInfo
argument_list|()
decl_stmt|;
if|if
condition|(
name|info
operator|!=
literal|null
condition|)
block|{
return|return
name|info
operator|.
name|getConsumerId
argument_list|()
operator|.
name|getSessionId
argument_list|()
return|;
block|}
return|return
literal|0
return|;
block|}
comment|/**      * @return the id of the Subscription      */
specifier|public
name|long
name|getSubcriptionId
parameter_list|()
block|{
name|ConsumerInfo
name|info
init|=
name|getConsumerInfo
argument_list|()
decl_stmt|;
if|if
condition|(
name|info
operator|!=
literal|null
condition|)
block|{
return|return
name|info
operator|.
name|getConsumerId
argument_list|()
operator|.
name|getValue
argument_list|()
return|;
block|}
return|return
literal|0
return|;
block|}
comment|/**      * @return the destination name      */
specifier|public
name|String
name|getDestinationName
parameter_list|()
block|{
name|ConsumerInfo
name|info
init|=
name|getConsumerInfo
argument_list|()
decl_stmt|;
if|if
condition|(
name|info
operator|!=
literal|null
condition|)
block|{
name|ActiveMQDestination
name|dest
init|=
name|info
operator|.
name|getDestination
argument_list|()
decl_stmt|;
return|return
name|dest
operator|.
name|getPhysicalName
argument_list|()
return|;
block|}
return|return
literal|"NOTSET"
return|;
block|}
comment|/**      * @return true if the destination is a Queue      */
specifier|public
name|boolean
name|isDestinationQueue
parameter_list|()
block|{
name|ConsumerInfo
name|info
init|=
name|getConsumerInfo
argument_list|()
decl_stmt|;
if|if
condition|(
name|info
operator|!=
literal|null
condition|)
block|{
name|ActiveMQDestination
name|dest
init|=
name|info
operator|.
name|getDestination
argument_list|()
decl_stmt|;
return|return
name|dest
operator|.
name|isQueue
argument_list|()
return|;
block|}
return|return
literal|false
return|;
block|}
comment|/**      * @return true of the destination is a Topic      */
specifier|public
name|boolean
name|isDestinationTopic
parameter_list|()
block|{
name|ConsumerInfo
name|info
init|=
name|getConsumerInfo
argument_list|()
decl_stmt|;
if|if
condition|(
name|info
operator|!=
literal|null
condition|)
block|{
name|ActiveMQDestination
name|dest
init|=
name|info
operator|.
name|getDestination
argument_list|()
decl_stmt|;
return|return
name|dest
operator|.
name|isTopic
argument_list|()
return|;
block|}
return|return
literal|false
return|;
block|}
comment|/**      * @return true if the destination is temporary      */
specifier|public
name|boolean
name|isDestinationTemporary
parameter_list|()
block|{
name|ConsumerInfo
name|info
init|=
name|getConsumerInfo
argument_list|()
decl_stmt|;
if|if
condition|(
name|info
operator|!=
literal|null
condition|)
block|{
name|ActiveMQDestination
name|dest
init|=
name|info
operator|.
name|getDestination
argument_list|()
decl_stmt|;
return|return
name|dest
operator|.
name|isTemporary
argument_list|()
return|;
block|}
return|return
literal|false
return|;
block|}
comment|/**      * @return true if the subscriber is active      */
specifier|public
name|boolean
name|isActive
parameter_list|()
block|{
return|return
literal|true
return|;
block|}
comment|/**      * The subscription should release as may references as it can to help the garbage collector      * reclaim memory.      */
specifier|public
name|void
name|gc
parameter_list|()
block|{
if|if
condition|(
name|subscription
operator|!=
literal|null
condition|)
block|{
name|subscription
operator|.
name|gc
argument_list|()
expr_stmt|;
block|}
block|}
comment|/**      * @return number of messages pending delivery      */
specifier|public
name|int
name|getPending
parameter_list|()
block|{
return|return
name|subscription
operator|!=
literal|null
condition|?
name|subscription
operator|.
name|pending
argument_list|()
else|:
literal|0
return|;
block|}
comment|/**      * @return number of messages dispatched      */
specifier|public
name|int
name|getDispatched
parameter_list|()
block|{
return|return
name|subscription
operator|!=
literal|null
condition|?
name|subscription
operator|.
name|dispatched
argument_list|()
else|:
literal|0
return|;
block|}
comment|/**      * @return number of messages delivered      */
specifier|public
name|int
name|getDelivered
parameter_list|()
block|{
return|return
name|subscription
operator|!=
literal|null
condition|?
name|subscription
operator|.
name|delivered
argument_list|()
else|:
literal|0
return|;
block|}
specifier|protected
name|ConsumerInfo
name|getConsumerInfo
parameter_list|()
block|{
return|return
name|subscription
operator|!=
literal|null
condition|?
name|subscription
operator|.
name|getConsumerInfo
argument_list|()
else|:
literal|null
return|;
block|}
comment|/**      *@return pretty print      */
specifier|public
name|String
name|toString
parameter_list|()
block|{
return|return
literal|"SubscriptionView: "
operator|+
name|getClientId
argument_list|()
operator|+
literal|":"
operator|+
name|getConnectionId
argument_list|()
return|;
block|}
block|}
end_class

end_unit

