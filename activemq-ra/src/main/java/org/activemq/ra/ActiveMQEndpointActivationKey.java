begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**   *   * Copyright 2004 Hiram Chirino  * Copyright 2005 LogicBlaze Inc.  *   * Licensed under the Apache License, Version 2.0 (the "License");   * you may not use this file except in compliance with the License.   * You may obtain a copy of the License at   *   * http://www.apache.org/licenses/LICENSE-2.0  *   * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,   * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.   * See the License for the specific language governing permissions and   * limitations under the License.   *   **/
end_comment

begin_package
package|package
name|org
operator|.
name|activemq
operator|.
name|ra
package|;
end_package

begin_import
import|import
name|javax
operator|.
name|resource
operator|.
name|spi
operator|.
name|endpoint
operator|.
name|MessageEndpointFactory
import|;
end_import

begin_class
specifier|public
class|class
name|ActiveMQEndpointActivationKey
block|{
specifier|final
specifier|private
name|MessageEndpointFactory
name|messageEndpointFactory
decl_stmt|;
specifier|final
specifier|private
name|ActiveMQActivationSpec
name|activationSpec
decl_stmt|;
comment|/**      * @return Returns the activationSpec.      */
specifier|public
name|ActiveMQActivationSpec
name|getActivationSpec
parameter_list|()
block|{
return|return
name|activationSpec
return|;
block|}
comment|/**      * @return Returns the messageEndpointFactory.      */
specifier|public
name|MessageEndpointFactory
name|getMessageEndpointFactory
parameter_list|()
block|{
return|return
name|messageEndpointFactory
return|;
block|}
comment|/**      * For testing      */
name|ActiveMQEndpointActivationKey
parameter_list|()
block|{
name|this
argument_list|(
literal|null
argument_list|,
literal|null
argument_list|)
expr_stmt|;
block|}
comment|/**      * @param messageEndpointFactory      * @param activationSpec      */
specifier|public
name|ActiveMQEndpointActivationKey
parameter_list|(
name|MessageEndpointFactory
name|messageEndpointFactory
parameter_list|,
name|ActiveMQActivationSpec
name|activationSpec
parameter_list|)
block|{
name|this
operator|.
name|messageEndpointFactory
operator|=
name|messageEndpointFactory
expr_stmt|;
name|this
operator|.
name|activationSpec
operator|=
name|activationSpec
expr_stmt|;
block|}
comment|/**      * @see java.lang.Object#hashCode()      */
specifier|public
name|int
name|hashCode
parameter_list|()
block|{
return|return
name|messageEndpointFactory
operator|.
name|hashCode
argument_list|()
operator|^
name|activationSpec
operator|.
name|hashCode
argument_list|()
return|;
block|}
comment|/**      * @see java.lang.Object#equals(java.lang.Object)      */
specifier|public
name|boolean
name|equals
parameter_list|(
name|Object
name|obj
parameter_list|)
block|{
if|if
condition|(
name|this
operator|==
name|obj
condition|)
block|{
return|return
literal|true
return|;
block|}
if|if
condition|(
name|obj
operator|==
literal|null
condition|)
block|{
return|return
literal|false
return|;
block|}
name|ActiveMQEndpointActivationKey
name|o
init|=
operator|(
name|ActiveMQEndpointActivationKey
operator|)
name|obj
decl_stmt|;
comment|//Per the 12.4.9 spec:
comment|//   MessageEndpointFactory does not implement equals()
comment|//   ActivationSpec does not implement equals()
return|return
name|o
operator|.
name|activationSpec
operator|==
name|activationSpec
operator|&&
name|o
operator|.
name|messageEndpointFactory
operator|==
name|messageEndpointFactory
return|;
block|}
block|}
end_class

end_unit

