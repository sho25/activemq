begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  *  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  * http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
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
name|File
import|;
end_import

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
name|util
operator|.
name|Enumeration
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
name|Properties
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
name|NameCallback
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
name|commons
operator|.
name|logging
operator|.
name|Log
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|commons
operator|.
name|logging
operator|.
name|LogFactory
import|;
end_import

begin_comment
comment|/**  * @version $Rev: $ $Date: $  */
end_comment

begin_class
specifier|public
class|class
name|PropertiesLoginModule
implements|implements
name|LoginModule
block|{
specifier|private
specifier|final
name|String
name|USER_FILE
init|=
literal|"org.apache.activemq.jaas.properties.user"
decl_stmt|;
specifier|private
specifier|final
name|String
name|GROUP_FILE
init|=
literal|"org.apache.activemq.jaas.properties.group"
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|Log
name|log
init|=
name|LogFactory
operator|.
name|getLog
argument_list|(
name|PropertiesLoginModule
operator|.
name|class
argument_list|)
decl_stmt|;
specifier|private
name|Subject
name|subject
decl_stmt|;
specifier|private
name|CallbackHandler
name|callbackHandler
decl_stmt|;
specifier|private
name|boolean
name|debug
decl_stmt|;
specifier|private
name|String
name|usersFile
decl_stmt|;
specifier|private
name|String
name|groupsFile
decl_stmt|;
specifier|private
name|Properties
name|users
init|=
operator|new
name|Properties
argument_list|()
decl_stmt|;
specifier|private
name|Properties
name|groups
init|=
operator|new
name|Properties
argument_list|()
decl_stmt|;
specifier|private
name|String
name|user
decl_stmt|;
specifier|private
name|Set
name|principals
init|=
operator|new
name|HashSet
argument_list|()
decl_stmt|;
specifier|private
name|File
name|baseDir
decl_stmt|;
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
if|if
condition|(
name|System
operator|.
name|getProperty
argument_list|(
literal|"java.security.auth.login.config"
argument_list|)
operator|!=
literal|null
condition|)
block|{
name|baseDir
operator|=
operator|new
name|File
argument_list|(
name|System
operator|.
name|getProperty
argument_list|(
literal|"java.security.auth.login.config"
argument_list|)
argument_list|)
operator|.
name|getParentFile
argument_list|()
expr_stmt|;
block|}
else|else
block|{
name|baseDir
operator|=
operator|new
name|File
argument_list|(
literal|"."
argument_list|)
expr_stmt|;
block|}
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
name|usersFile
operator|=
operator|(
name|String
operator|)
name|options
operator|.
name|get
argument_list|(
name|USER_FILE
argument_list|)
operator|+
literal|""
expr_stmt|;
name|groupsFile
operator|=
operator|(
name|String
operator|)
name|options
operator|.
name|get
argument_list|(
name|GROUP_FILE
argument_list|)
operator|+
literal|""
expr_stmt|;
if|if
condition|(
name|debug
condition|)
block|{
name|log
operator|.
name|debug
argument_list|(
literal|"Initialized debug="
operator|+
name|debug
operator|+
literal|" usersFile="
operator|+
name|usersFile
operator|+
literal|" groupsFile="
operator|+
name|groupsFile
operator|+
literal|" basedir="
operator|+
name|baseDir
argument_list|)
expr_stmt|;
block|}
block|}
specifier|public
name|boolean
name|login
parameter_list|()
throws|throws
name|LoginException
block|{
name|File
name|f
init|=
operator|new
name|File
argument_list|(
name|baseDir
argument_list|,
name|usersFile
argument_list|)
decl_stmt|;
try|try
block|{
name|users
operator|.
name|load
argument_list|(
operator|new
name|java
operator|.
name|io
operator|.
name|FileInputStream
argument_list|(
name|f
argument_list|)
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|ioe
parameter_list|)
block|{
throw|throw
operator|new
name|LoginException
argument_list|(
literal|"Unable to load user properties file "
operator|+
name|f
argument_list|)
throw|;
block|}
name|f
operator|=
operator|new
name|File
argument_list|(
name|baseDir
argument_list|,
name|groupsFile
argument_list|)
expr_stmt|;
try|try
block|{
name|groups
operator|.
name|load
argument_list|(
operator|new
name|java
operator|.
name|io
operator|.
name|FileInputStream
argument_list|(
name|f
argument_list|)
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|ioe
parameter_list|)
block|{
throw|throw
operator|new
name|LoginException
argument_list|(
literal|"Unable to load group properties file "
operator|+
name|f
argument_list|)
throw|;
block|}
name|Callback
index|[]
name|callbacks
init|=
operator|new
name|Callback
index|[
literal|2
index|]
decl_stmt|;
name|callbacks
index|[
literal|0
index|]
operator|=
operator|new
name|NameCallback
argument_list|(
literal|"Username: "
argument_list|)
expr_stmt|;
name|callbacks
index|[
literal|1
index|]
operator|=
operator|new
name|PasswordCallback
argument_list|(
literal|"Password: "
argument_list|,
literal|false
argument_list|)
expr_stmt|;
try|try
block|{
name|callbackHandler
operator|.
name|handle
argument_list|(
name|callbacks
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|ioe
parameter_list|)
block|{
throw|throw
operator|new
name|LoginException
argument_list|(
name|ioe
operator|.
name|getMessage
argument_list|()
argument_list|)
throw|;
block|}
catch|catch
parameter_list|(
name|UnsupportedCallbackException
name|uce
parameter_list|)
block|{
throw|throw
operator|new
name|LoginException
argument_list|(
name|uce
operator|.
name|getMessage
argument_list|()
operator|+
literal|" not available to obtain information from user"
argument_list|)
throw|;
block|}
name|user
operator|=
operator|(
operator|(
name|NameCallback
operator|)
name|callbacks
index|[
literal|0
index|]
operator|)
operator|.
name|getName
argument_list|()
expr_stmt|;
name|char
index|[]
name|tmpPassword
init|=
operator|(
operator|(
name|PasswordCallback
operator|)
name|callbacks
index|[
literal|1
index|]
operator|)
operator|.
name|getPassword
argument_list|()
decl_stmt|;
if|if
condition|(
name|tmpPassword
operator|==
literal|null
condition|)
name|tmpPassword
operator|=
operator|new
name|char
index|[
literal|0
index|]
expr_stmt|;
name|String
name|password
init|=
name|users
operator|.
name|getProperty
argument_list|(
name|user
argument_list|)
decl_stmt|;
if|if
condition|(
name|password
operator|==
literal|null
condition|)
throw|throw
operator|new
name|FailedLoginException
argument_list|(
literal|"User does exist"
argument_list|)
throw|;
if|if
condition|(
operator|!
name|password
operator|.
name|equals
argument_list|(
operator|new
name|String
argument_list|(
name|tmpPassword
argument_list|)
argument_list|)
condition|)
throw|throw
operator|new
name|FailedLoginException
argument_list|(
literal|"Password does not match"
argument_list|)
throw|;
name|users
operator|.
name|clear
argument_list|()
expr_stmt|;
if|if
condition|(
name|debug
condition|)
block|{
name|log
operator|.
name|debug
argument_list|(
literal|"login "
operator|+
name|user
argument_list|)
expr_stmt|;
block|}
return|return
literal|true
return|;
block|}
specifier|public
name|boolean
name|commit
parameter_list|()
throws|throws
name|LoginException
block|{
name|principals
operator|.
name|add
argument_list|(
operator|new
name|UserPrincipal
argument_list|(
name|user
argument_list|)
argument_list|)
expr_stmt|;
for|for
control|(
name|Enumeration
name|enumeration
init|=
name|groups
operator|.
name|keys
argument_list|()
init|;
name|enumeration
operator|.
name|hasMoreElements
argument_list|()
condition|;
control|)
block|{
name|String
name|name
init|=
operator|(
name|String
operator|)
name|enumeration
operator|.
name|nextElement
argument_list|()
decl_stmt|;
name|String
index|[]
name|userList
init|=
operator|(
operator|(
name|String
operator|)
name|groups
operator|.
name|getProperty
argument_list|(
name|name
argument_list|)
operator|+
literal|""
operator|)
operator|.
name|split
argument_list|(
literal|","
argument_list|)
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|userList
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
if|if
condition|(
name|user
operator|.
name|equals
argument_list|(
name|userList
index|[
name|i
index|]
argument_list|)
condition|)
block|{
name|principals
operator|.
name|add
argument_list|(
operator|new
name|GroupPrincipal
argument_list|(
name|name
argument_list|)
argument_list|)
expr_stmt|;
break|break;
block|}
block|}
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
name|clear
argument_list|()
expr_stmt|;
if|if
condition|(
name|debug
condition|)
block|{
name|log
operator|.
name|debug
argument_list|(
literal|"commit"
argument_list|)
expr_stmt|;
block|}
return|return
literal|true
return|;
block|}
specifier|public
name|boolean
name|abort
parameter_list|()
throws|throws
name|LoginException
block|{
name|clear
argument_list|()
expr_stmt|;
if|if
condition|(
name|debug
condition|)
block|{
name|log
operator|.
name|debug
argument_list|(
literal|"abort"
argument_list|)
expr_stmt|;
block|}
return|return
literal|true
return|;
block|}
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
name|principals
operator|.
name|clear
argument_list|()
expr_stmt|;
if|if
condition|(
name|debug
condition|)
block|{
name|log
operator|.
name|debug
argument_list|(
literal|"logout"
argument_list|)
expr_stmt|;
block|}
return|return
literal|true
return|;
block|}
specifier|private
name|void
name|clear
parameter_list|()
block|{
name|groups
operator|.
name|clear
argument_list|()
expr_stmt|;
name|user
operator|=
literal|null
expr_stmt|;
block|}
block|}
end_class

end_unit

