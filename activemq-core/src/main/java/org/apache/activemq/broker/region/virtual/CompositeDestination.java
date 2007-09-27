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
name|virtual
package|;
end_package

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Collection
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
name|Destination
import|;
end_import

begin_comment
comment|/**  *   * @version $Revision$  */
end_comment

begin_class
specifier|public
specifier|abstract
class|class
name|CompositeDestination
implements|implements
name|VirtualDestination
block|{
specifier|private
name|String
name|name
decl_stmt|;
specifier|private
name|Collection
name|forwardTo
decl_stmt|;
specifier|private
name|boolean
name|forwardOnly
init|=
literal|true
decl_stmt|;
specifier|private
name|boolean
name|copyMessage
init|=
literal|true
decl_stmt|;
specifier|public
name|Destination
name|intercept
parameter_list|(
name|Destination
name|destination
parameter_list|)
block|{
return|return
operator|new
name|CompositeDestinationFilter
argument_list|(
name|destination
argument_list|,
name|getForwardTo
argument_list|()
argument_list|,
name|isForwardOnly
argument_list|()
argument_list|,
name|isCopyMessage
argument_list|()
argument_list|)
return|;
block|}
specifier|public
name|String
name|getName
parameter_list|()
block|{
return|return
name|name
return|;
block|}
comment|/**      * Sets the name of this composite destination      */
specifier|public
name|void
name|setName
parameter_list|(
name|String
name|name
parameter_list|)
block|{
name|this
operator|.
name|name
operator|=
name|name
expr_stmt|;
block|}
specifier|public
name|Collection
name|getForwardTo
parameter_list|()
block|{
return|return
name|forwardTo
return|;
block|}
comment|/**      * Sets the list of destinations to forward to      */
specifier|public
name|void
name|setForwardTo
parameter_list|(
name|Collection
name|forwardDestinations
parameter_list|)
block|{
name|this
operator|.
name|forwardTo
operator|=
name|forwardDestinations
expr_stmt|;
block|}
specifier|public
name|boolean
name|isForwardOnly
parameter_list|()
block|{
return|return
name|forwardOnly
return|;
block|}
comment|/**      * Sets if the virtual destination is forward only (and so there is no      * physical queue to match the virtual queue) or if there is also a physical      * queue with the same name).      */
specifier|public
name|void
name|setForwardOnly
parameter_list|(
name|boolean
name|forwardOnly
parameter_list|)
block|{
name|this
operator|.
name|forwardOnly
operator|=
name|forwardOnly
expr_stmt|;
block|}
specifier|public
name|boolean
name|isCopyMessage
parameter_list|()
block|{
return|return
name|copyMessage
return|;
block|}
comment|/**      * Sets whether a copy of the message will be sent to each destination.      * Defaults to true so that the forward destination is set as the      * destination of the message      */
specifier|public
name|void
name|setCopyMessage
parameter_list|(
name|boolean
name|copyMessage
parameter_list|)
block|{
name|this
operator|.
name|copyMessage
operator|=
name|copyMessage
expr_stmt|;
block|}
block|}
end_class

end_unit

