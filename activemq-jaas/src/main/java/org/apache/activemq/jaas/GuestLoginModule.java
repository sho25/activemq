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
name|jaas
package|;
end_package

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|IOException
import|;
end_import

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
name|javax
operator|.
name|security
operator|.
name|auth
operator|.
name|Subject
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|security
operator|.
name|auth
operator|.
name|callback
operator|.
name|Callback
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|security
operator|.
name|auth
operator|.
name|callback
operator|.
name|CallbackHandler
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|security
operator|.
name|auth
operator|.
name|callback
operator|.
name|PasswordCallback
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|security
operator|.
name|auth
operator|.
name|callback
operator|.
name|UnsupportedCallbackException
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|security
operator|.
name|auth
operator|.
name|login
operator|.
name|LoginException
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|security
operator|.
name|auth
operator|.
name|spi
operator|.
name|LoginModule
import|;
end_import

begin_import
import|import
name|org
operator|.
name|slf4j
operator|.
name|Logger
import|;
end_import

begin_import
import|import
name|org
operator|.
name|slf4j
operator|.
name|LoggerFactory
import|;
end_import

begin_comment
comment|/**  * Always login the user with a default 'guest' identity.  *  * Useful for unauthenticated communication channels being used in the  * same broker as authenticated ones.  *   */
end_comment

begin_class
specifier|public
class|class
name|GuestLoginModule
implements|implements
name|LoginModule
block|{
specifier|private
specifier|static
specifier|final
name|String
name|GUEST_USER
init|=
literal|"org.apache.activemq.jaas.guest.user"
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|String
name|GUEST_GROUP
init|=
literal|"org.apache.activemq.jaas.guest.group"
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|Logger
name|LOG
init|=
name|LoggerFactory
operator|.
name|getLogger
argument_list|(
name|GuestLoginModule
operator|.
name|class
argument_list|)
decl_stmt|;
specifier|private
name|String
name|userName
init|=
literal|"guest"
decl_stmt|;
specifier|private
name|String
name|groupName
init|=
literal|"guests"
decl_stmt|;
specifier|private
name|Subject
name|subject
decl_stmt|;
specifier|private
name|boolean
name|debug
decl_stmt|;
specifier|private
name|boolean
name|credentialsInvalidate
decl_stmt|;
specifier|private
name|Set
argument_list|<
name|Principal
argument_list|>
name|principals
init|=
operator|new
name|HashSet
argument_list|<
name|Principal
argument_list|>
argument_list|()
decl_stmt|;
specifier|private
name|CallbackHandler
name|callbackHandler
decl_stmt|;
comment|/** the authentication status*/
specifier|private
name|boolean
name|succeeded
init|=
literal|false
decl_stmt|;
specifier|private
name|boolean
name|commitSucceeded
init|=
literal|false
decl_stmt|;
annotation|@
name|Override
specifier|public
name|void
name|initialize
parameter_list|(
name|Subject
name|subject
parameter_list|,
name|CallbackHandler
name|callbackHandler
parameter_list|,
name|Map
name|sharedState
parameter_list|,
name|Map
name|options
parameter_list|)
block|{
name|this
operator|.
name|subject
operator|=
name|subject
expr_stmt|;
name|this
operator|.
name|callbackHandler
operator|=
name|callbackHandler
expr_stmt|;
name|debug
operator|=
literal|"true"
operator|.
name|equalsIgnoreCase
argument_list|(
operator|(
name|String
operator|)
name|options
operator|.
name|get
argument_list|(
literal|"debug"
argument_list|)
argument_list|)
expr_stmt|;
name|credentialsInvalidate
operator|=
literal|"true"
operator|.
name|equalsIgnoreCase
argument_list|(
operator|(
name|String
operator|)
name|options
operator|.
name|get
argument_list|(
literal|"credentialsInvalidate"
argument_list|)
argument_list|)
expr_stmt|;
if|if
condition|(
name|options
operator|.
name|get
argument_list|(
name|GUEST_USER
argument_list|)
operator|!=
literal|null
condition|)
block|{
name|userName
operator|=
operator|(
name|String
operator|)
name|options
operator|.
name|get
argument_list|(
name|GUEST_USER
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|options
operator|.
name|get
argument_list|(
name|GUEST_GROUP
argument_list|)
operator|!=
literal|null
condition|)
block|{
name|groupName
operator|=
operator|(
name|String
operator|)
name|options
operator|.
name|get
argument_list|(
name|GUEST_GROUP
argument_list|)
expr_stmt|;
block|}
name|principals
operator|.
name|add
argument_list|(
operator|new
name|UserPrincipal
argument_list|(
name|userName
argument_list|)
argument_list|)
expr_stmt|;
name|principals
operator|.
name|add
argument_list|(
operator|new
name|GroupPrincipal
argument_list|(
name|groupName
argument_list|)
argument_list|)
expr_stmt|;
if|if
condition|(
name|debug
condition|)
block|{
name|LOG
operator|.
name|debug
argument_list|(
literal|"Initialized debug="
operator|+
name|debug
operator|+
literal|" guestUser="
operator|+
name|userName
operator|+
literal|" guestGroup="
operator|+
name|groupName
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|login
parameter_list|()
throws|throws
name|LoginException
block|{
name|succeeded
operator|=
literal|true
expr_stmt|;
if|if
condition|(
name|credentialsInvalidate
condition|)
block|{
name|PasswordCallback
name|passwordCallback
init|=
operator|new
name|PasswordCallback
argument_list|(
literal|"Password: "
argument_list|,
literal|false
argument_list|)
decl_stmt|;
try|try
block|{
name|callbackHandler
operator|.
name|handle
argument_list|(
operator|new
name|Callback
index|[]
block|{
name|passwordCallback
block|}
argument_list|)
expr_stmt|;
if|if
condition|(
name|passwordCallback
operator|.
name|getPassword
argument_list|()
operator|!=
literal|null
condition|)
block|{
if|if
condition|(
name|debug
condition|)
block|{
name|LOG
operator|.
name|debug
argument_list|(
literal|"Guest login failing (credentialsInvalidate=true) on presence of a password"
argument_list|)
expr_stmt|;
block|}
name|succeeded
operator|=
literal|false
expr_stmt|;
name|passwordCallback
operator|.
name|clearPassword
argument_list|()
expr_stmt|;
block|}
empty_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|ioe
parameter_list|)
block|{              }
catch|catch
parameter_list|(
name|UnsupportedCallbackException
name|uce
parameter_list|)
block|{              }
block|}
if|if
condition|(
name|debug
condition|)
block|{
name|LOG
operator|.
name|debug
argument_list|(
literal|"Guest login "
operator|+
name|succeeded
argument_list|)
expr_stmt|;
block|}
return|return
name|succeeded
return|;
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|commit
parameter_list|()
throws|throws
name|LoginException
block|{
if|if
condition|(
name|debug
condition|)
block|{
name|LOG
operator|.
name|debug
argument_list|(
literal|"commit"
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
operator|!
name|succeeded
condition|)
block|{
return|return
literal|false
return|;
block|}
name|subject
operator|.
name|getPrincipals
argument_list|()
operator|.
name|addAll
argument_list|(
name|principals
argument_list|)
expr_stmt|;
name|commitSucceeded
operator|=
literal|true
expr_stmt|;
return|return
literal|true
return|;
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|abort
parameter_list|()
throws|throws
name|LoginException
block|{
if|if
condition|(
name|debug
condition|)
block|{
name|LOG
operator|.
name|debug
argument_list|(
literal|"abort"
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
operator|!
name|succeeded
condition|)
block|{
return|return
literal|false
return|;
block|}
elseif|else
if|if
condition|(
name|succeeded
operator|&&
name|commitSucceeded
condition|)
block|{
comment|// we succeeded, but another required module failed
name|logout
argument_list|()
expr_stmt|;
block|}
else|else
block|{
comment|// our commit failed
name|succeeded
operator|=
literal|false
expr_stmt|;
block|}
return|return
literal|true
return|;
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|logout
parameter_list|()
throws|throws
name|LoginException
block|{
name|subject
operator|.
name|getPrincipals
argument_list|()
operator|.
name|removeAll
argument_list|(
name|principals
argument_list|)
expr_stmt|;
if|if
condition|(
name|debug
condition|)
block|{
name|LOG
operator|.
name|debug
argument_list|(
literal|"logout"
argument_list|)
expr_stmt|;
block|}
name|succeeded
operator|=
literal|false
expr_stmt|;
name|commitSucceeded
operator|=
literal|false
expr_stmt|;
return|return
literal|true
return|;
block|}
block|}
end_class

end_unit

