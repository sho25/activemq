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
name|javax
operator|.
name|management
operator|.
name|openmbean
operator|.
name|CompositeData
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|management
operator|.
name|openmbean
operator|.
name|OpenDataException
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|management
operator|.
name|openmbean
operator|.
name|TabularData
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
name|RemoveSubscriptionInfo
import|;
end_import

begin_comment
comment|/**  * @version $Revision: 1.5 $  */
end_comment

begin_class
specifier|public
class|class
name|DurableSubscriptionView
extends|extends
name|SubscriptionView
implements|implements
name|DurableSubscriptionViewMBean
block|{
specifier|protected
name|ManagedRegionBroker
name|broker
decl_stmt|;
specifier|protected
name|String
name|subscriptionName
decl_stmt|;
specifier|protected
name|DurableTopicSubscription
name|durableSub
decl_stmt|;
comment|/**      * Constructor      *       * @param clientId      * @param sub      */
specifier|public
name|DurableSubscriptionView
parameter_list|(
name|ManagedRegionBroker
name|broker
parameter_list|,
name|String
name|clientId
parameter_list|,
name|Subscription
name|sub
parameter_list|)
block|{
name|super
argument_list|(
name|clientId
argument_list|,
name|sub
argument_list|)
expr_stmt|;
name|this
operator|.
name|broker
operator|=
name|broker
expr_stmt|;
name|this
operator|.
name|durableSub
operator|=
operator|(
name|DurableTopicSubscription
operator|)
name|sub
expr_stmt|;
if|if
condition|(
name|sub
operator|!=
literal|null
condition|)
block|{
name|this
operator|.
name|subscriptionName
operator|=
name|sub
operator|.
name|getConsumerInfo
argument_list|()
operator|.
name|getSubscriptionName
argument_list|()
expr_stmt|;
block|}
block|}
comment|/**      * @return name of the durable consumer      */
specifier|public
name|String
name|getSubscriptionName
parameter_list|()
block|{
return|return
name|subscriptionName
return|;
block|}
comment|/**      * Browse messages for this durable subscriber      *       * @return messages      * @throws OpenDataException      */
specifier|public
name|CompositeData
index|[]
name|browse
parameter_list|()
throws|throws
name|OpenDataException
block|{
return|return
name|broker
operator|.
name|browse
argument_list|(
name|this
argument_list|)
return|;
block|}
comment|/**      * Browse messages for this durable subscriber      *       * @return messages      * @throws OpenDataException      */
specifier|public
name|TabularData
name|browseAsTable
parameter_list|()
throws|throws
name|OpenDataException
block|{
return|return
name|broker
operator|.
name|browseAsTable
argument_list|(
name|this
argument_list|)
return|;
block|}
comment|/**      * Destroys the durable subscription so that messages will no longer be      * stored for this subscription      */
specifier|public
name|void
name|destroy
parameter_list|()
throws|throws
name|Exception
block|{
name|RemoveSubscriptionInfo
name|info
init|=
operator|new
name|RemoveSubscriptionInfo
argument_list|()
decl_stmt|;
name|info
operator|.
name|setClientId
argument_list|(
name|clientId
argument_list|)
expr_stmt|;
name|info
operator|.
name|setSubscriptionName
argument_list|(
name|subscriptionName
argument_list|)
expr_stmt|;
name|ConnectionContext
name|context
init|=
operator|new
name|ConnectionContext
argument_list|()
decl_stmt|;
name|context
operator|.
name|setBroker
argument_list|(
name|broker
argument_list|)
expr_stmt|;
name|context
operator|.
name|setClientId
argument_list|(
name|clientId
argument_list|)
expr_stmt|;
name|broker
operator|.
name|removeSubscription
argument_list|(
name|context
argument_list|,
name|info
argument_list|)
expr_stmt|;
block|}
specifier|public
name|String
name|toString
parameter_list|()
block|{
return|return
literal|"ActiveDurableSubscriptionView: "
operator|+
name|getClientId
argument_list|()
operator|+
literal|":"
operator|+
name|getSubscriptionName
argument_list|()
return|;
block|}
specifier|public
name|int
name|cursorSize
parameter_list|()
block|{
if|if
condition|(
name|durableSub
operator|!=
literal|null
operator|&&
name|durableSub
operator|.
name|getPending
argument_list|()
operator|!=
literal|null
condition|)
block|{
return|return
name|durableSub
operator|.
name|getPending
argument_list|()
operator|.
name|size
argument_list|()
return|;
block|}
return|return
literal|0
return|;
block|}
specifier|public
name|boolean
name|doesCursorHaveMessagesBuffered
parameter_list|()
block|{
if|if
condition|(
name|durableSub
operator|!=
literal|null
operator|&&
name|durableSub
operator|.
name|getPending
argument_list|()
operator|!=
literal|null
condition|)
block|{
return|return
name|durableSub
operator|.
name|getPending
argument_list|()
operator|.
name|hasMessagesBufferedToDeliver
argument_list|()
return|;
block|}
return|return
literal|false
return|;
block|}
specifier|public
name|boolean
name|doesCursorHaveSpace
parameter_list|()
block|{
if|if
condition|(
name|durableSub
operator|!=
literal|null
operator|&&
name|durableSub
operator|.
name|getPending
argument_list|()
operator|!=
literal|null
condition|)
block|{
return|return
name|durableSub
operator|.
name|getPending
argument_list|()
operator|.
name|hasSpace
argument_list|()
return|;
block|}
return|return
literal|false
return|;
block|}
comment|/* (non-Javadoc)      * @see org.apache.activemq.broker.jmx.DurableSubscriptionViewMBean#getCursorMemoryUsage()      */
specifier|public
name|long
name|getCursorMemoryUsage
parameter_list|()
block|{
if|if
condition|(
name|durableSub
operator|!=
literal|null
operator|&&
name|durableSub
operator|.
name|getPending
argument_list|()
operator|!=
literal|null
operator|&&
name|durableSub
operator|.
name|getPending
argument_list|()
operator|.
name|getSystemUsage
argument_list|()
operator|!=
literal|null
condition|)
block|{
return|return
name|durableSub
operator|.
name|getPending
argument_list|()
operator|.
name|getSystemUsage
argument_list|()
operator|.
name|getMemoryUsage
argument_list|()
operator|.
name|getUsage
argument_list|()
return|;
block|}
return|return
literal|0
return|;
block|}
specifier|public
name|int
name|getCursorPercentUsage
parameter_list|()
block|{
if|if
condition|(
name|durableSub
operator|!=
literal|null
operator|&&
name|durableSub
operator|.
name|getPending
argument_list|()
operator|!=
literal|null
operator|&&
name|durableSub
operator|.
name|getPending
argument_list|()
operator|.
name|getSystemUsage
argument_list|()
operator|!=
literal|null
condition|)
block|{
return|return
name|durableSub
operator|.
name|getPending
argument_list|()
operator|.
name|getSystemUsage
argument_list|()
operator|.
name|getMemoryUsage
argument_list|()
operator|.
name|getPercentUsage
argument_list|()
return|;
block|}
return|return
literal|0
return|;
block|}
specifier|public
name|boolean
name|isCursorFull
parameter_list|()
block|{
if|if
condition|(
name|durableSub
operator|!=
literal|null
operator|&&
name|durableSub
operator|.
name|getPending
argument_list|()
operator|!=
literal|null
condition|)
block|{
return|return
name|durableSub
operator|.
name|getPending
argument_list|()
operator|.
name|isFull
argument_list|()
return|;
block|}
return|return
literal|false
return|;
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|isActive
parameter_list|()
block|{
return|return
name|durableSub
operator|.
name|isActive
argument_list|()
return|;
block|}
block|}
end_class

end_unit

