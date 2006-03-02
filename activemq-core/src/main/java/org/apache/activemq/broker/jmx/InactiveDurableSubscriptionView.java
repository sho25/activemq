begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  *   * Copyright 2005-2006 The Apache Software Foundation  *   * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with  * the License. You may obtain a copy of the License at  *   * http://www.apache.org/licenses/LICENSE-2.0  *   * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on  * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the  * specific language governing permissions and limitations under the License.  */
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
name|command
operator|.
name|SubscriptionInfo
import|;
end_import

begin_comment
comment|/**  * @version $Revision: 1.5 $  */
end_comment

begin_class
specifier|public
class|class
name|InactiveDurableSubscriptionView
extends|extends
name|SubscriptionView
implements|implements
name|DurableSubscriptionViewMBean
block|{
specifier|protected
name|SubscriptionInfo
name|info
decl_stmt|;
specifier|public
name|InactiveDurableSubscriptionView
parameter_list|(
name|String
name|clientId
parameter_list|,
name|SubscriptionInfo
name|sub
parameter_list|)
block|{
name|super
argument_list|(
name|clientId
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|this
operator|.
name|info
operator|=
name|sub
expr_stmt|;
block|}
comment|/**      * @return the id of the Subscription      */
specifier|public
name|long
name|getSubcriptionId
parameter_list|()
block|{
return|return
operator|-
literal|1
return|;
block|}
comment|/**      * @return the destination name      */
specifier|public
name|String
name|getDestinationName
parameter_list|()
block|{
return|return
name|info
operator|.
name|getDestination
argument_list|()
operator|.
name|getPhysicalName
argument_list|()
return|;
block|}
comment|/**      * @return true if the destination is a Queue      */
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
specifier|public
name|String
name|getSubscriptionName
parameter_list|()
block|{
return|return
name|info
operator|.
name|getSubcriptionName
argument_list|()
return|;
block|}
comment|/**      * @return true if the subscriber is active      */
specifier|public
name|boolean
name|isActive
parameter_list|()
block|{
return|return
literal|false
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
literal|null
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
literal|null
return|;
block|}
block|}
end_class

end_unit

