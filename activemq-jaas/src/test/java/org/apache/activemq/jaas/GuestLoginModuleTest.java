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
name|net
operator|.
name|URL
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
name|GuestLoginModuleTest
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
name|GuestLoginModuleTest
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
literal|"GuestLogin"
argument_list|,
operator|new
name|CallbackHandler
argument_list|()
block|{
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
name|assertEquals
argument_list|(
literal|"Should have no Callbacks"
argument_list|,
literal|0
argument_list|,
name|callbacks
operator|.
name|length
argument_list|)
expr_stmt|;
block|}
block|}
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
literal|"Should have two principals"
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
name|assertTrue
argument_list|(
literal|"User principal is 'foo'"
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
name|contains
argument_list|(
operator|new
name|UserPrincipal
argument_list|(
literal|"foo"
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"Should have one group principal"
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
name|assertTrue
argument_list|(
literal|"Group principal is 'bar'"
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
name|contains
argument_list|(
operator|new
name|GroupPrincipal
argument_list|(
literal|"bar"
argument_list|)
argument_list|)
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
name|testLoginWithDefaults
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
literal|"GuestLoginWithDefaults"
argument_list|,
operator|new
name|CallbackHandler
argument_list|()
block|{
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
name|assertEquals
argument_list|(
literal|"Should have no Callbacks"
argument_list|,
literal|0
argument_list|,
name|callbacks
operator|.
name|length
argument_list|)
expr_stmt|;
block|}
block|}
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
literal|"Should have two principals"
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
name|assertTrue
argument_list|(
literal|"User principal is 'guest'"
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
name|contains
argument_list|(
operator|new
name|UserPrincipal
argument_list|(
literal|"guest"
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"Should have one group principal"
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
name|assertTrue
argument_list|(
literal|"Group principal is 'guests'"
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
name|contains
argument_list|(
operator|new
name|GroupPrincipal
argument_list|(
literal|"guests"
argument_list|)
argument_list|)
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
block|}
end_class

end_unit

