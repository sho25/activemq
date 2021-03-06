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
name|util
operator|.
name|Map
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
name|login
operator|.
name|FailedLoginException
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
name|apache
operator|.
name|activemq
operator|.
name|jaas
operator|.
name|GroupPrincipal
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
name|UserPrincipal
import|;
end_import

begin_class
specifier|public
class|class
name|StubLoginModule
implements|implements
name|LoginModule
block|{
specifier|public
specifier|static
specifier|final
name|String
name|ALLOW_LOGIN_PROPERTY
init|=
literal|"org.apache.activemq.jaas.stubproperties.allow_login"
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|String
name|USERS_PROPERTY
init|=
literal|"org.apache.activemq.jaas.stubproperties.users"
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|String
name|GROUPS_PROPERTY
init|=
literal|"org.apache.activemq.jaas.stubproperties.groups"
decl_stmt|;
specifier|private
name|Subject
name|subject
decl_stmt|;
specifier|private
name|String
name|userNames
index|[]
decl_stmt|;
specifier|private
name|String
name|groupNames
index|[]
decl_stmt|;
specifier|private
name|boolean
name|allowLogin
decl_stmt|;
annotation|@
name|Override
annotation|@
name|SuppressWarnings
argument_list|(
literal|"rawtypes"
argument_list|)
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
name|String
name|allowLoginString
init|=
call|(
name|String
call|)
argument_list|(
name|options
operator|.
name|get
argument_list|(
name|ALLOW_LOGIN_PROPERTY
argument_list|)
argument_list|)
decl_stmt|;
name|String
name|usersString
init|=
call|(
name|String
call|)
argument_list|(
name|options
operator|.
name|get
argument_list|(
name|USERS_PROPERTY
argument_list|)
argument_list|)
decl_stmt|;
name|String
name|groupsString
init|=
call|(
name|String
call|)
argument_list|(
name|options
operator|.
name|get
argument_list|(
name|GROUPS_PROPERTY
argument_list|)
argument_list|)
decl_stmt|;
name|this
operator|.
name|subject
operator|=
name|subject
expr_stmt|;
name|allowLogin
operator|=
name|Boolean
operator|.
name|parseBoolean
argument_list|(
name|allowLoginString
argument_list|)
expr_stmt|;
name|userNames
operator|=
name|usersString
operator|.
name|split
argument_list|(
literal|","
argument_list|)
expr_stmt|;
name|groupNames
operator|=
name|groupsString
operator|.
name|split
argument_list|(
literal|","
argument_list|)
expr_stmt|;
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
if|if
condition|(
operator|!
name|allowLogin
condition|)
block|{
throw|throw
operator|new
name|FailedLoginException
argument_list|(
literal|"Login was not allowed (as specified in configuration)."
argument_list|)
throw|;
block|}
return|return
literal|true
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
operator|!
name|allowLogin
condition|)
block|{
throw|throw
operator|new
name|FailedLoginException
argument_list|(
literal|"Login was not allowed (as specified in configuration)."
argument_list|)
throw|;
block|}
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|userNames
operator|.
name|length
condition|;
operator|++
name|i
control|)
block|{
if|if
condition|(
name|userNames
index|[
name|i
index|]
operator|.
name|length
argument_list|()
operator|>
literal|0
condition|)
block|{
name|subject
operator|.
name|getPrincipals
argument_list|()
operator|.
name|add
argument_list|(
operator|new
name|UserPrincipal
argument_list|(
name|userNames
index|[
name|i
index|]
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|groupNames
operator|.
name|length
condition|;
operator|++
name|i
control|)
block|{
if|if
condition|(
name|groupNames
index|[
name|i
index|]
operator|.
name|length
argument_list|()
operator|>
literal|0
condition|)
block|{
name|subject
operator|.
name|getPrincipals
argument_list|()
operator|.
name|add
argument_list|(
operator|new
name|GroupPrincipal
argument_list|(
name|groupNames
index|[
name|i
index|]
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
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
name|clear
argument_list|()
expr_stmt|;
return|return
literal|true
return|;
block|}
block|}
end_class

end_unit

