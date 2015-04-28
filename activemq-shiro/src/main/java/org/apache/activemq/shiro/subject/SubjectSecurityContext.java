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
name|subject
package|;
end_package

begin_import
import|import
name|java
operator|.
name|security
operator|.
name|Principal
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Set
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|concurrent
operator|.
name|ConcurrentMap
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
name|security
operator|.
name|SecurityContext
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|shiro
operator|.
name|subject
operator|.
name|Subject
import|;
end_import

begin_comment
comment|/**  * ActiveMQ {@code SecurityContext} implementation that retains a Shiro {@code Subject} instance for use during  * security checks and other security-related operations.  *  * @since 5.10.0  */
end_comment

begin_class
specifier|public
class|class
name|SubjectSecurityContext
extends|extends
name|SecurityContext
block|{
specifier|private
specifier|final
name|Subject
name|subject
decl_stmt|;
specifier|public
name|SubjectSecurityContext
parameter_list|(
name|SubjectConnectionReference
name|conn
parameter_list|)
block|{
comment|//The username might not be available at the time this object is instantiated (the Subject might be
comment|//anonymous).  Instead we override the getUserName() method below and that will always delegate to the
comment|//Subject to return the most accurate/freshest username available.
name|super
argument_list|(
literal|null
argument_list|)
expr_stmt|;
name|this
operator|.
name|subject
operator|=
name|conn
operator|.
name|getSubject
argument_list|()
expr_stmt|;
block|}
specifier|public
name|Subject
name|getSubject
parameter_list|()
block|{
return|return
name|subject
return|;
block|}
specifier|private
specifier|static
name|String
name|getUsername
parameter_list|(
name|Subject
name|subject
parameter_list|)
block|{
if|if
condition|(
name|subject
operator|!=
literal|null
condition|)
block|{
name|Object
name|principal
init|=
name|subject
operator|.
name|getPrincipal
argument_list|()
decl_stmt|;
if|if
condition|(
name|principal
operator|!=
literal|null
condition|)
block|{
return|return
name|String
operator|.
name|valueOf
argument_list|(
name|principal
argument_list|)
return|;
block|}
block|}
return|return
literal|null
return|;
block|}
annotation|@
name|Override
specifier|public
name|String
name|getUserName
parameter_list|()
block|{
return|return
name|getUsername
argument_list|(
name|this
operator|.
name|subject
argument_list|)
return|;
block|}
specifier|private
specifier|static
name|UnsupportedOperationException
name|notAllowed
parameter_list|(
name|String
name|methodName
parameter_list|)
block|{
name|String
name|msg
init|=
literal|"Do not invoke the '"
operator|+
name|methodName
operator|+
literal|"' method or use a broker filter that invokes it.  Use one "
operator|+
literal|"of the Shiro-based security filters instead."
decl_stmt|;
return|return
operator|new
name|UnsupportedOperationException
argument_list|(
name|msg
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|isInOneOf
parameter_list|(
name|Set
argument_list|<
name|?
argument_list|>
name|allowedPrincipals
parameter_list|)
block|{
throw|throw
name|notAllowed
argument_list|(
literal|"isInOneOf"
argument_list|)
throw|;
block|}
annotation|@
name|Override
specifier|public
name|ConcurrentMap
argument_list|<
name|ActiveMQDestination
argument_list|,
name|ActiveMQDestination
argument_list|>
name|getAuthorizedReadDests
parameter_list|()
block|{
throw|throw
name|notAllowed
argument_list|(
literal|"getAuthorizedReadDests"
argument_list|)
throw|;
block|}
annotation|@
name|Override
specifier|public
name|ConcurrentMap
argument_list|<
name|ActiveMQDestination
argument_list|,
name|ActiveMQDestination
argument_list|>
name|getAuthorizedWriteDests
parameter_list|()
block|{
throw|throw
name|notAllowed
argument_list|(
literal|"getAuthorizedWriteDests"
argument_list|)
throw|;
block|}
annotation|@
name|Override
specifier|public
name|Set
argument_list|<
name|Principal
argument_list|>
name|getPrincipals
parameter_list|()
block|{
throw|throw
name|notAllowed
argument_list|(
literal|"getPrincipals"
argument_list|)
throw|;
block|}
block|}
end_class

end_unit

