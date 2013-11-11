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
name|RemoveSubscriptionInfo
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
name|SubscriptionInfo
import|;
end_import

begin_comment
comment|/**  *  *  */
end_comment

begin_class
specifier|public
class|class
name|InactiveDurableSubscriptionView
extends|extends
name|DurableSubscriptionView
implements|implements
name|DurableSubscriptionViewMBean
block|{
specifier|protected
name|SubscriptionInfo
name|subscriptionInfo
decl_stmt|;
comment|/**      * Constructor      *      * @param broker      * @param clientId      * @param userName      * @param subInfo      */
specifier|public
name|InactiveDurableSubscriptionView
parameter_list|(
name|ManagedRegionBroker
name|broker
parameter_list|,
name|BrokerService
name|brokerService
parameter_list|,
name|String
name|clientId
parameter_list|,
name|SubscriptionInfo
name|subInfo
parameter_list|,
name|Subscription
name|subscription
parameter_list|)
block|{
name|super
argument_list|(
name|broker
argument_list|,
name|brokerService
argument_list|,
name|clientId
argument_list|,
literal|null
argument_list|,
name|subscription
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
name|subscriptionInfo
operator|=
name|subInfo
expr_stmt|;
block|}
comment|/**      * @return the id of the Subscription      */
annotation|@
name|Override
specifier|public
name|long
name|getSubscriptionId
parameter_list|()
block|{
return|return
operator|-
literal|1
return|;
block|}
comment|/**      * @return the destination name      */
annotation|@
name|Override
specifier|public
name|String
name|getDestinationName
parameter_list|()
block|{
return|return
name|subscriptionInfo
operator|.
name|getDestination
argument_list|()
operator|.
name|getPhysicalName
argument_list|()
return|;
block|}
comment|/**      * @return true if the destination is a Queue      */
annotation|@
name|Override
specifier|public
name|boolean
name|isDestinationQueue
parameter_list|()
block|{
return|return
literal|false
return|;
block|}
comment|/**      * @return true of the destination is a Topic      */
annotation|@
name|Override
specifier|public
name|boolean
name|isDestinationTopic
parameter_list|()
block|{
return|return
literal|true
return|;
block|}
comment|/**      * @return true if the destination is temporary      */
annotation|@
name|Override
specifier|public
name|boolean
name|isDestinationTemporary
parameter_list|()
block|{
return|return
literal|false
return|;
block|}
comment|/**      * @return name of the durable consumer      */
annotation|@
name|Override
specifier|public
name|String
name|getSubscriptionName
parameter_list|()
block|{
return|return
name|subscriptionInfo
operator|.
name|getSubscriptionName
argument_list|()
return|;
block|}
comment|/**      * @return true if the subscriber is active      */
annotation|@
name|Override
specifier|public
name|boolean
name|isActive
parameter_list|()
block|{
return|return
literal|false
return|;
block|}
annotation|@
name|Override
specifier|protected
name|ConsumerInfo
name|getConsumerInfo
parameter_list|()
block|{
comment|// when inactive, consumer info is stale
return|return
literal|null
return|;
block|}
comment|/**      * Browse messages for this durable subscriber      *      * @return messages      * @throws OpenDataException      */
annotation|@
name|Override
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
comment|/**      * Browse messages for this durable subscriber      *      * @return messages      * @throws OpenDataException      */
annotation|@
name|Override
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
annotation|@
name|Override
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
name|subscriptionInfo
operator|.
name|getSubscriptionName
argument_list|()
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
name|brokerService
operator|.
name|getBroker
argument_list|()
operator|.
name|removeSubscription
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
name|String
name|toString
parameter_list|()
block|{
return|return
literal|"InactiveDurableSubscriptionView: "
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
annotation|@
name|Override
specifier|public
name|String
name|getSelector
parameter_list|()
block|{
return|return
name|subscriptionInfo
operator|.
name|getSelector
argument_list|()
return|;
block|}
block|}
end_class

end_unit

