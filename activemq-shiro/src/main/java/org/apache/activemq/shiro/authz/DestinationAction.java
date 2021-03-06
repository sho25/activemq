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
name|shiro
operator|.
name|authz
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
name|command
operator|.
name|ActiveMQDestination
import|;
end_import

begin_comment
comment|/**  * A {@code DestinationAction} represents behavior being taken on a particular {@link ActiveMQDestination}, such as  * creation, removal, and reading messages from it or writing messages to it.  The exact behavior being taken on the  * specific {@link #getDestination() destination} is represented as a {@link #getVerb() verb} property, which is one of  * the following string tokens:  *<table>  *<tr>  *<th>Verb</th>  *<th>Description</th>  *</tr>  *<tr>  *<td>{@code create}</td>  *<td>Create a specific destination.</td>  *</tr>  *<tr>  *<td>{@code remove}</td>  *<td>Remove a specific destination.</td>  *</tr>  *<tr>  *<td>{@code read}</td>  *<td>Read (consume) messages from a specific destination.</td>  *</tr>  *<tr>  *<td>{@code write}</td>  *<td>Write messages to a specific destination.</td>  *</tr>  *</table>  *  * @since 5.10.0  */
end_comment

begin_class
specifier|public
class|class
name|DestinationAction
implements|implements
name|Action
block|{
specifier|private
specifier|final
name|ConnectionContext
name|connectionContext
decl_stmt|;
specifier|private
specifier|final
name|ActiveMQDestination
name|destination
decl_stmt|;
specifier|private
specifier|final
name|String
name|verb
decl_stmt|;
specifier|public
name|DestinationAction
parameter_list|(
name|ConnectionContext
name|connectionContext
parameter_list|,
name|ActiveMQDestination
name|destination
parameter_list|,
name|String
name|verb
parameter_list|)
block|{
if|if
condition|(
name|connectionContext
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"ConnectionContext argument cannot be null."
argument_list|)
throw|;
block|}
if|if
condition|(
name|destination
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"ActiveMQDestination argument cannot be null."
argument_list|)
throw|;
block|}
if|if
condition|(
name|verb
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"verb argument cannot be null."
argument_list|)
throw|;
block|}
name|this
operator|.
name|connectionContext
operator|=
name|connectionContext
expr_stmt|;
name|this
operator|.
name|destination
operator|=
name|destination
expr_stmt|;
name|this
operator|.
name|verb
operator|=
name|verb
expr_stmt|;
block|}
specifier|public
name|ConnectionContext
name|getConnectionContext
parameter_list|()
block|{
return|return
name|connectionContext
return|;
block|}
specifier|public
name|ActiveMQDestination
name|getDestination
parameter_list|()
block|{
return|return
name|destination
return|;
block|}
specifier|public
name|String
name|getVerb
parameter_list|()
block|{
return|return
name|verb
return|;
block|}
annotation|@
name|Override
specifier|public
name|String
name|toString
parameter_list|()
block|{
return|return
name|this
operator|.
name|verb
operator|+
literal|" destination: "
operator|+
name|destination
return|;
block|}
block|}
end_class

end_unit

