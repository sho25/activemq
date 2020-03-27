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
name|net
operator|.
name|URL
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
name|LoginContext
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
name|org
operator|.
name|apache
operator|.
name|commons
operator|.
name|io
operator|.
name|FileUtils
import|;
end_import

begin_import
import|import
name|junit
operator|.
name|framework
operator|.
name|TestCase
import|;
end_import

begin_comment
comment|/**  * @version $Rev: $ $Date: $  */
end_comment

begin_class
specifier|public
class|class
name|PropertiesLoginModuleTest
extends|extends
name|TestCase
block|{
static|static
block|{
name|String
name|path
init|=
name|System
operator|.
name|getProperty
argument_list|(
literal|"java.security.auth.login.config"
argument_list|)
decl_stmt|;
if|if
condition|(
name|path
operator|==
literal|null
condition|)
block|{
name|URL
name|resource
init|=
name|PropertiesLoginModuleTest
operator|.
name|class
operator|.
name|getClassLoader
argument_list|()
operator|.
name|getResource
argument_list|(
literal|"login.config"
argument_list|)
decl_stmt|;
if|if
condition|(
name|resource
operator|!=
literal|null
condition|)
block|{
name|path
operator|=
name|resource
operator|.
name|getFile
argument_list|()
expr_stmt|;
name|System
operator|.
name|setProperty
argument_list|(
literal|"java.security.auth.login.config"
argument_list|,
name|path
argument_list|)
expr_stmt|;
block|}
block|}
block|}
specifier|public
name|void
name|testLogin
parameter_list|()
throws|throws
name|LoginException
block|{
name|LoginContext
name|context
init|=
operator|new
name|LoginContext
argument_list|(
name|getLoginModule
argument_list|()
argument_list|,
operator|new
name|UserPassHandler
argument_list|(
literal|"first"
argument_list|,
literal|"secret"
argument_list|)
argument_list|)
decl_stmt|;
name|context
operator|.
name|login
argument_list|()
expr_stmt|;
name|Subject
name|subject
init|=
name|context
operator|.
name|getSubject
argument_list|()
decl_stmt|;
name|assertEquals
argument_list|(
literal|"Should have three principals"
argument_list|,
literal|3
argument_list|,
name|subject
operator|.
name|getPrincipals
argument_list|()
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"Should have one user principal"
argument_list|,
literal|1
argument_list|,
name|subject
operator|.
name|getPrincipals
argument_list|(
name|UserPrincipal
operator|.
name|class
argument_list|)
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"Should have two group principals"
argument_list|,
literal|2
argument_list|,
name|subject
operator|.
name|getPrincipals
argument_list|(
name|GroupPrincipal
operator|.
name|class
argument_list|)
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|context
operator|.
name|logout
argument_list|()
expr_stmt|;
name|assertEquals
argument_list|(
literal|"Should have zero principals"
argument_list|,
literal|0
argument_list|,
name|subject
operator|.
name|getPrincipals
argument_list|()
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|testLoginReload
parameter_list|()
throws|throws
name|Exception
block|{
name|File
name|targetPropDir
init|=
operator|new
name|File
argument_list|(
literal|"target/loginReloadTest"
argument_list|)
decl_stmt|;
name|File
name|sourcePropDir
init|=
operator|new
name|File
argument_list|(
literal|"src/test/resources"
argument_list|)
decl_stmt|;
name|File
name|usersFile
init|=
operator|new
name|File
argument_list|(
name|targetPropDir
argument_list|,
literal|"users.properties"
argument_list|)
decl_stmt|;
name|File
name|groupsFile
init|=
operator|new
name|File
argument_list|(
name|targetPropDir
argument_list|,
literal|"groups.properties"
argument_list|)
decl_stmt|;
comment|//Set up initial properties
name|FileUtils
operator|.
name|copyFile
argument_list|(
operator|new
name|File
argument_list|(
name|sourcePropDir
argument_list|,
literal|"users.properties"
argument_list|)
argument_list|,
name|usersFile
argument_list|)
expr_stmt|;
name|FileUtils
operator|.
name|copyFile
argument_list|(
operator|new
name|File
argument_list|(
name|sourcePropDir
argument_list|,
literal|"groups.properties"
argument_list|)
argument_list|,
name|groupsFile
argument_list|)
expr_stmt|;
name|LoginContext
name|context
init|=
operator|new
name|LoginContext
argument_list|(
literal|"PropertiesLoginReload"
argument_list|,
operator|new
name|UserPassHandler
argument_list|(
literal|"first"
argument_list|,
literal|"secret"
argument_list|)
argument_list|)
decl_stmt|;
name|context
operator|.
name|login
argument_list|()
expr_stmt|;
name|Subject
name|subject
init|=
name|context
operator|.
name|getSubject
argument_list|()
decl_stmt|;
comment|//test initial principals
name|assertEquals
argument_list|(
literal|"Should have three principals"
argument_list|,
literal|3
argument_list|,
name|subject
operator|.
name|getPrincipals
argument_list|()
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"Should have one user principal"
argument_list|,
literal|1
argument_list|,
name|subject
operator|.
name|getPrincipals
argument_list|(
name|UserPrincipal
operator|.
name|class
argument_list|)
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"Should have two group principals"
argument_list|,
literal|2
argument_list|,
name|subject
operator|.
name|getPrincipals
argument_list|(
name|GroupPrincipal
operator|.
name|class
argument_list|)
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|context
operator|.
name|logout
argument_list|()
expr_stmt|;
name|assertEquals
argument_list|(
literal|"Should have zero principals"
argument_list|,
literal|0
argument_list|,
name|subject
operator|.
name|getPrincipals
argument_list|()
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
comment|//Modify the file and test that the properties are reloaded
name|Thread
operator|.
name|sleep
argument_list|(
literal|1000
argument_list|)
expr_stmt|;
name|FileUtils
operator|.
name|copyFile
argument_list|(
operator|new
name|File
argument_list|(
name|sourcePropDir
argument_list|,
literal|"usersReload.properties"
argument_list|)
argument_list|,
name|usersFile
argument_list|)
expr_stmt|;
name|FileUtils
operator|.
name|copyFile
argument_list|(
operator|new
name|File
argument_list|(
name|sourcePropDir
argument_list|,
literal|"groupsReload.properties"
argument_list|)
argument_list|,
name|groupsFile
argument_list|)
expr_stmt|;
name|FileUtils
operator|.
name|touch
argument_list|(
name|usersFile
argument_list|)
expr_stmt|;
name|FileUtils
operator|.
name|touch
argument_list|(
name|groupsFile
argument_list|)
expr_stmt|;
comment|//Use new password to verify  users file was reloaded
name|context
operator|=
operator|new
name|LoginContext
argument_list|(
literal|"PropertiesLoginReload"
argument_list|,
operator|new
name|UserPassHandler
argument_list|(
literal|"first"
argument_list|,
literal|"secrets"
argument_list|)
argument_list|)
expr_stmt|;
name|context
operator|.
name|login
argument_list|()
expr_stmt|;
name|subject
operator|=
name|context
operator|.
name|getSubject
argument_list|()
expr_stmt|;
comment|//Check that the principals changed
name|assertEquals
argument_list|(
literal|"Should have three principals"
argument_list|,
literal|2
argument_list|,
name|subject
operator|.
name|getPrincipals
argument_list|()
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"Should have one user principal"
argument_list|,
literal|1
argument_list|,
name|subject
operator|.
name|getPrincipals
argument_list|(
name|UserPrincipal
operator|.
name|class
argument_list|)
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"Should have one group principals"
argument_list|,
literal|1
argument_list|,
name|subject
operator|.
name|getPrincipals
argument_list|(
name|GroupPrincipal
operator|.
name|class
argument_list|)
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|context
operator|.
name|logout
argument_list|()
expr_stmt|;
name|assertEquals
argument_list|(
literal|"Should have zero principals"
argument_list|,
literal|0
argument_list|,
name|subject
operator|.
name|getPrincipals
argument_list|()
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|testBadUseridLogin
parameter_list|()
throws|throws
name|Exception
block|{
name|LoginContext
name|context
init|=
operator|new
name|LoginContext
argument_list|(
name|getLoginModule
argument_list|()
argument_list|,
operator|new
name|UserPassHandler
argument_list|(
literal|"BAD"
argument_list|,
literal|"secret"
argument_list|)
argument_list|)
decl_stmt|;
try|try
block|{
name|context
operator|.
name|login
argument_list|()
expr_stmt|;
name|fail
argument_list|(
literal|"Should have thrown a FailedLoginException"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|FailedLoginException
name|doNothing
parameter_list|)
block|{         }
block|}
specifier|public
name|void
name|testBadPWLogin
parameter_list|()
throws|throws
name|Exception
block|{
name|LoginContext
name|context
init|=
operator|new
name|LoginContext
argument_list|(
name|getLoginModule
argument_list|()
argument_list|,
operator|new
name|UserPassHandler
argument_list|(
literal|"first"
argument_list|,
literal|"BAD"
argument_list|)
argument_list|)
decl_stmt|;
try|try
block|{
name|context
operator|.
name|login
argument_list|()
expr_stmt|;
name|fail
argument_list|(
literal|"Should have thrown a FailedLoginException"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|FailedLoginException
name|doNothing
parameter_list|)
block|{         }
block|}
specifier|private
specifier|static
class|class
name|UserPassHandler
implements|implements
name|CallbackHandler
block|{
specifier|private
specifier|final
name|String
name|user
decl_stmt|;
specifier|private
specifier|final
name|String
name|pass
decl_stmt|;
specifier|public
name|UserPassHandler
parameter_list|(
specifier|final
name|String
name|user
parameter_list|,
specifier|final
name|String
name|pass
parameter_list|)
block|{
name|this
operator|.
name|user
operator|=
name|user
expr_stmt|;
name|this
operator|.
name|pass
operator|=
name|pass
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|handle
parameter_list|(
name|Callback
index|[]
name|callbacks
parameter_list|)
throws|throws
name|IOException
throws|,
name|UnsupportedCallbackException
block|{
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|callbacks
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
if|if
condition|(
name|callbacks
index|[
name|i
index|]
operator|instanceof
name|NameCallback
condition|)
block|{
operator|(
operator|(
name|NameCallback
operator|)
name|callbacks
index|[
name|i
index|]
operator|)
operator|.
name|setName
argument_list|(
name|user
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|callbacks
index|[
name|i
index|]
operator|instanceof
name|PasswordCallback
condition|)
block|{
operator|(
operator|(
name|PasswordCallback
operator|)
name|callbacks
index|[
name|i
index|]
operator|)
operator|.
name|setPassword
argument_list|(
name|pass
operator|.
name|toCharArray
argument_list|()
argument_list|)
expr_stmt|;
block|}
else|else
block|{
throw|throw
operator|new
name|UnsupportedCallbackException
argument_list|(
name|callbacks
index|[
name|i
index|]
argument_list|)
throw|;
block|}
block|}
block|}
block|}
specifier|protected
name|String
name|getLoginModule
parameter_list|()
block|{
return|return
literal|"PropertiesLogin"
return|;
block|}
block|}
end_class

end_unit

