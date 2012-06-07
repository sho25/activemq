begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Copyright 2012 The Apache Software Foundation.  *  * Licensed under the Apache License, Version 2.0 (the "License");  * you may not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *      http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
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
name|org
operator|.
name|apache
operator|.
name|directory
operator|.
name|server
operator|.
name|core
operator|.
name|integ
operator|.
name|AbstractLdapTestUnit
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|directory
operator|.
name|server
operator|.
name|core
operator|.
name|integ
operator|.
name|FrameworkRunner
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|directory
operator|.
name|server
operator|.
name|integ
operator|.
name|ServerIntegrationUtils
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|directory
operator|.
name|server
operator|.
name|ldap
operator|.
name|LdapServer
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|directory
operator|.
name|server
operator|.
name|annotations
operator|.
name|CreateLdapServer
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|directory
operator|.
name|server
operator|.
name|annotations
operator|.
name|CreateTransport
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|directory
operator|.
name|server
operator|.
name|core
operator|.
name|annotations
operator|.
name|ApplyLdifFiles
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|After
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|Before
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|Test
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|runner
operator|.
name|RunWith
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|naming
operator|.
name|Context
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|naming
operator|.
name|NameClassPair
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|naming
operator|.
name|NamingEnumeration
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|naming
operator|.
name|directory
operator|.
name|DirContext
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|naming
operator|.
name|directory
operator|.
name|InitialDirContext
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
name|*
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
name|Hashtable
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
import|import static
name|org
operator|.
name|junit
operator|.
name|Assert
operator|.
name|assertTrue
import|;
end_import

begin_class
annotation|@
name|RunWith
argument_list|(
name|FrameworkRunner
operator|.
name|class
argument_list|)
annotation|@
name|CreateLdapServer
argument_list|(
name|transports
operator|=
block|{
annotation|@
name|CreateTransport
argument_list|(
name|protocol
operator|=
literal|"LDAP"
argument_list|,
name|port
operator|=
literal|1024
argument_list|)
block|}
argument_list|)
annotation|@
name|ApplyLdifFiles
argument_list|(
literal|"test.ldif"
argument_list|)
specifier|public
class|class
name|LDAPModuleRoleExpansionTest
extends|extends
name|AbstractLdapTestUnit
block|{
specifier|public
specifier|static
name|LdapServer
name|ldapServer
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|String
name|PRINCIPAL
init|=
literal|"uid=admin,ou=system"
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|String
name|CREDENTIALS
init|=
literal|"secret"
decl_stmt|;
specifier|private
specifier|final
name|String
name|loginConfigSysPropName
init|=
literal|"java.security.auth.login.config"
decl_stmt|;
specifier|private
name|String
name|oldLoginConfig
decl_stmt|;
annotation|@
name|Before
specifier|public
name|void
name|setLoginConfigSysProperty
parameter_list|()
block|{
name|oldLoginConfig
operator|=
name|System
operator|.
name|getProperty
argument_list|(
name|loginConfigSysPropName
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|System
operator|.
name|setProperty
argument_list|(
name|loginConfigSysPropName
argument_list|,
literal|"src/test/resources/login.config"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|After
specifier|public
name|void
name|resetLoginConfigSysProperty
parameter_list|()
block|{
if|if
condition|(
name|oldLoginConfig
operator|!=
literal|null
condition|)
block|{
name|System
operator|.
name|setProperty
argument_list|(
name|loginConfigSysPropName
argument_list|,
name|oldLoginConfig
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|SuppressWarnings
argument_list|(
literal|"unchecked"
argument_list|)
annotation|@
name|Test
specifier|public
name|void
name|testRunning
parameter_list|()
throws|throws
name|Exception
block|{
name|Hashtable
name|env
init|=
operator|new
name|Hashtable
argument_list|()
decl_stmt|;
name|env
operator|.
name|put
argument_list|(
name|Context
operator|.
name|PROVIDER_URL
argument_list|,
literal|"ldap://localhost:1024"
argument_list|)
expr_stmt|;
name|env
operator|.
name|put
argument_list|(
name|Context
operator|.
name|INITIAL_CONTEXT_FACTORY
argument_list|,
literal|"com.sun.jndi.ldap.LdapCtxFactory"
argument_list|)
expr_stmt|;
name|env
operator|.
name|put
argument_list|(
name|Context
operator|.
name|SECURITY_AUTHENTICATION
argument_list|,
literal|"simple"
argument_list|)
expr_stmt|;
name|env
operator|.
name|put
argument_list|(
name|Context
operator|.
name|SECURITY_PRINCIPAL
argument_list|,
name|PRINCIPAL
argument_list|)
expr_stmt|;
name|env
operator|.
name|put
argument_list|(
name|Context
operator|.
name|SECURITY_CREDENTIALS
argument_list|,
name|CREDENTIALS
argument_list|)
expr_stmt|;
name|DirContext
name|ctx
init|=
operator|new
name|InitialDirContext
argument_list|(
name|env
argument_list|)
decl_stmt|;
name|HashSet
name|set
init|=
operator|new
name|HashSet
argument_list|()
decl_stmt|;
name|NamingEnumeration
name|list
init|=
name|ctx
operator|.
name|list
argument_list|(
literal|"ou=system"
argument_list|)
decl_stmt|;
while|while
condition|(
name|list
operator|.
name|hasMore
argument_list|()
condition|)
block|{
name|NameClassPair
name|ncp
init|=
operator|(
name|NameClassPair
operator|)
name|list
operator|.
name|next
argument_list|()
decl_stmt|;
name|set
operator|.
name|add
argument_list|(
name|ncp
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|assertTrue
argument_list|(
name|set
operator|.
name|contains
argument_list|(
literal|"uid=admin"
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|set
operator|.
name|contains
argument_list|(
literal|"ou=users"
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|set
operator|.
name|contains
argument_list|(
literal|"ou=groups"
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|set
operator|.
name|contains
argument_list|(
literal|"ou=configuration"
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|set
operator|.
name|contains
argument_list|(
literal|"prefNodeName=sysPrefRoot"
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testRoleExpansion
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
literal|"ExpandedLDAPLogin"
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
literal|"first"
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
literal|"secret"
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
name|boolean
name|isAdmin
init|=
literal|false
decl_stmt|;
name|boolean
name|isUser
init|=
literal|false
decl_stmt|;
for|for
control|(
name|Principal
name|principal
range|:
name|subject
operator|.
name|getPrincipals
argument_list|()
control|)
block|{
if|if
condition|(
name|principal
operator|instanceof
name|GroupPrincipal
condition|)
block|{
name|GroupPrincipal
name|groupPrincipal
init|=
operator|(
name|GroupPrincipal
operator|)
name|principal
decl_stmt|;
if|if
condition|(
name|groupPrincipal
operator|.
name|getName
argument_list|()
operator|.
name|equalsIgnoreCase
argument_list|(
literal|"admins"
argument_list|)
condition|)
name|isAdmin
operator|=
literal|true
expr_stmt|;
if|if
condition|(
name|groupPrincipal
operator|.
name|getName
argument_list|()
operator|.
name|equalsIgnoreCase
argument_list|(
literal|"users"
argument_list|)
condition|)
name|isUser
operator|=
literal|true
expr_stmt|;
block|}
block|}
comment|// Should be in users by virtue of being in admins
name|assertTrue
argument_list|(
name|isAdmin
operator|&&
name|isUser
argument_list|)
expr_stmt|;
name|context
operator|.
name|logout
argument_list|()
expr_stmt|;
block|}
block|}
end_class

end_unit
