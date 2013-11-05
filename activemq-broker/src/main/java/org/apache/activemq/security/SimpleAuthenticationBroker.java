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
name|security
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
name|HashSet
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Map
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
name|org
operator|.
name|apache
operator|.
name|activemq
operator|.
name|broker
operator|.
name|Broker
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
name|command
operator|.
name|ConnectionInfo
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
name|jaas
operator|.
name|GroupPrincipal
import|;
end_import

begin_comment
comment|/**  * Handles authenticating a users against a simple user name/password map.  */
end_comment

begin_class
specifier|public
class|class
name|SimpleAuthenticationBroker
extends|extends
name|AbstractAuthenticationBroker
block|{
specifier|private
name|boolean
name|anonymousAccessAllowed
init|=
literal|false
decl_stmt|;
specifier|private
name|String
name|anonymousUser
decl_stmt|;
specifier|private
name|String
name|anonymousGroup
decl_stmt|;
specifier|private
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|userPasswords
decl_stmt|;
specifier|private
name|Map
argument_list|<
name|String
argument_list|,
name|Set
argument_list|<
name|Principal
argument_list|>
argument_list|>
name|userGroups
decl_stmt|;
specifier|public
name|SimpleAuthenticationBroker
parameter_list|(
name|Broker
name|next
parameter_list|,
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|userPasswords
parameter_list|,
name|Map
argument_list|<
name|String
argument_list|,
name|Set
argument_list|<
name|Principal
argument_list|>
argument_list|>
name|userGroups
parameter_list|)
block|{
name|super
argument_list|(
name|next
argument_list|)
expr_stmt|;
name|this
operator|.
name|userPasswords
operator|=
name|userPasswords
expr_stmt|;
name|this
operator|.
name|userGroups
operator|=
name|userGroups
expr_stmt|;
block|}
specifier|public
name|void
name|setAnonymousAccessAllowed
parameter_list|(
name|boolean
name|anonymousAccessAllowed
parameter_list|)
block|{
name|this
operator|.
name|anonymousAccessAllowed
operator|=
name|anonymousAccessAllowed
expr_stmt|;
block|}
specifier|public
name|void
name|setAnonymousUser
parameter_list|(
name|String
name|anonymousUser
parameter_list|)
block|{
name|this
operator|.
name|anonymousUser
operator|=
name|anonymousUser
expr_stmt|;
block|}
specifier|public
name|void
name|setAnonymousGroup
parameter_list|(
name|String
name|anonymousGroup
parameter_list|)
block|{
name|this
operator|.
name|anonymousGroup
operator|=
name|anonymousGroup
expr_stmt|;
block|}
specifier|public
name|void
name|setUserPasswords
parameter_list|(
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|value
parameter_list|)
block|{
name|userPasswords
operator|=
name|value
expr_stmt|;
block|}
specifier|public
name|void
name|setUserGroups
parameter_list|(
name|Map
argument_list|<
name|String
argument_list|,
name|Set
argument_list|<
name|Principal
argument_list|>
argument_list|>
name|value
parameter_list|)
block|{
name|userGroups
operator|=
name|value
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|addConnection
parameter_list|(
name|ConnectionContext
name|context
parameter_list|,
name|ConnectionInfo
name|info
parameter_list|)
throws|throws
name|Exception
block|{
name|SecurityContext
name|s
init|=
name|context
operator|.
name|getSecurityContext
argument_list|()
decl_stmt|;
if|if
condition|(
name|s
operator|==
literal|null
condition|)
block|{
comment|// Check the username and password.
if|if
condition|(
name|anonymousAccessAllowed
operator|&&
name|info
operator|.
name|getUserName
argument_list|()
operator|==
literal|null
operator|&&
name|info
operator|.
name|getPassword
argument_list|()
operator|==
literal|null
condition|)
block|{
name|info
operator|.
name|setUserName
argument_list|(
name|anonymousUser
argument_list|)
expr_stmt|;
name|s
operator|=
operator|new
name|SecurityContext
argument_list|(
name|info
operator|.
name|getUserName
argument_list|()
argument_list|)
block|{
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
name|Set
argument_list|<
name|Principal
argument_list|>
name|groups
init|=
operator|new
name|HashSet
argument_list|<
name|Principal
argument_list|>
argument_list|()
decl_stmt|;
name|groups
operator|.
name|add
argument_list|(
operator|new
name|GroupPrincipal
argument_list|(
name|anonymousGroup
argument_list|)
argument_list|)
expr_stmt|;
return|return
name|groups
return|;
block|}
block|}
expr_stmt|;
block|}
else|else
block|{
name|String
name|pw
init|=
name|userPasswords
operator|.
name|get
argument_list|(
name|info
operator|.
name|getUserName
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
name|pw
operator|==
literal|null
operator|||
operator|!
name|pw
operator|.
name|equals
argument_list|(
name|info
operator|.
name|getPassword
argument_list|()
argument_list|)
condition|)
block|{
throw|throw
operator|new
name|SecurityException
argument_list|(
literal|"User name ["
operator|+
name|info
operator|.
name|getUserName
argument_list|()
operator|+
literal|"] or password is invalid."
argument_list|)
throw|;
block|}
specifier|final
name|Set
argument_list|<
name|Principal
argument_list|>
name|groups
init|=
name|userGroups
operator|.
name|get
argument_list|(
name|info
operator|.
name|getUserName
argument_list|()
argument_list|)
decl_stmt|;
name|s
operator|=
operator|new
name|SecurityContext
argument_list|(
name|info
operator|.
name|getUserName
argument_list|()
argument_list|)
block|{
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
return|return
name|groups
return|;
block|}
block|}
expr_stmt|;
block|}
name|context
operator|.
name|setSecurityContext
argument_list|(
name|s
argument_list|)
expr_stmt|;
name|securityContexts
operator|.
name|add
argument_list|(
name|s
argument_list|)
expr_stmt|;
block|}
try|try
block|{
name|super
operator|.
name|addConnection
argument_list|(
name|context
argument_list|,
name|info
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
name|securityContexts
operator|.
name|remove
argument_list|(
name|s
argument_list|)
expr_stmt|;
name|context
operator|.
name|setSecurityContext
argument_list|(
literal|null
argument_list|)
expr_stmt|;
throw|throw
name|e
throw|;
block|}
block|}
block|}
end_class

end_unit

