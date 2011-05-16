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
name|text
operator|.
name|MessageFormat
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|ArrayList
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
name|java
operator|.
name|util
operator|.
name|List
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
name|naming
operator|.
name|AuthenticationException
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|naming
operator|.
name|CommunicationException
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
name|Name
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|naming
operator|.
name|NameParser
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
name|NamingException
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
name|Attribute
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
name|Attributes
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
name|naming
operator|.
name|directory
operator|.
name|SearchControls
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
name|SearchResult
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
comment|/**  * @version $Rev: $ $Date: $  */
end_comment

begin_class
specifier|public
class|class
name|LDAPLoginModule
implements|implements
name|LoginModule
block|{
specifier|private
specifier|static
specifier|final
name|String
name|INITIAL_CONTEXT_FACTORY
init|=
literal|"initialContextFactory"
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|String
name|CONNECTION_URL
init|=
literal|"connectionURL"
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|String
name|CONNECTION_USERNAME
init|=
literal|"connectionUsername"
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|String
name|CONNECTION_PASSWORD
init|=
literal|"connectionPassword"
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|String
name|CONNECTION_PROTOCOL
init|=
literal|"connectionProtocol"
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|String
name|AUTHENTICATION
init|=
literal|"authentication"
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|String
name|USER_BASE
init|=
literal|"userBase"
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|String
name|USER_SEARCH_MATCHING
init|=
literal|"userSearchMatching"
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|String
name|USER_SEARCH_SUBTREE
init|=
literal|"userSearchSubtree"
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|String
name|ROLE_BASE
init|=
literal|"roleBase"
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|String
name|ROLE_NAME
init|=
literal|"roleName"
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|String
name|ROLE_SEARCH_MATCHING
init|=
literal|"roleSearchMatching"
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|String
name|ROLE_SEARCH_SUBTREE
init|=
literal|"roleSearchSubtree"
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|String
name|USER_ROLE_NAME
init|=
literal|"userRoleName"
decl_stmt|;
specifier|private
specifier|static
name|Logger
name|log
init|=
name|LoggerFactory
operator|.
name|getLogger
argument_list|(
name|LDAPLoginModule
operator|.
name|class
argument_list|)
decl_stmt|;
specifier|protected
name|DirContext
name|context
decl_stmt|;
specifier|private
name|Subject
name|subject
decl_stmt|;
specifier|private
name|CallbackHandler
name|handler
decl_stmt|;
specifier|private
name|LDAPLoginProperty
index|[]
name|config
decl_stmt|;
specifier|private
name|String
name|username
decl_stmt|;
specifier|private
name|Set
argument_list|<
name|GroupPrincipal
argument_list|>
name|groups
init|=
operator|new
name|HashSet
argument_list|<
name|GroupPrincipal
argument_list|>
argument_list|()
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
name|handler
operator|=
name|callbackHandler
expr_stmt|;
name|config
operator|=
operator|new
name|LDAPLoginProperty
index|[]
block|{
operator|new
name|LDAPLoginProperty
argument_list|(
name|INITIAL_CONTEXT_FACTORY
argument_list|,
operator|(
name|String
operator|)
name|options
operator|.
name|get
argument_list|(
name|INITIAL_CONTEXT_FACTORY
argument_list|)
argument_list|)
block|,
operator|new
name|LDAPLoginProperty
argument_list|(
name|CONNECTION_URL
argument_list|,
operator|(
name|String
operator|)
name|options
operator|.
name|get
argument_list|(
name|CONNECTION_URL
argument_list|)
argument_list|)
block|,
operator|new
name|LDAPLoginProperty
argument_list|(
name|CONNECTION_USERNAME
argument_list|,
operator|(
name|String
operator|)
name|options
operator|.
name|get
argument_list|(
name|CONNECTION_USERNAME
argument_list|)
argument_list|)
block|,
operator|new
name|LDAPLoginProperty
argument_list|(
name|CONNECTION_PASSWORD
argument_list|,
operator|(
name|String
operator|)
name|options
operator|.
name|get
argument_list|(
name|CONNECTION_PASSWORD
argument_list|)
argument_list|)
block|,
operator|new
name|LDAPLoginProperty
argument_list|(
name|CONNECTION_PROTOCOL
argument_list|,
operator|(
name|String
operator|)
name|options
operator|.
name|get
argument_list|(
name|CONNECTION_PROTOCOL
argument_list|)
argument_list|)
block|,
operator|new
name|LDAPLoginProperty
argument_list|(
name|AUTHENTICATION
argument_list|,
operator|(
name|String
operator|)
name|options
operator|.
name|get
argument_list|(
name|AUTHENTICATION
argument_list|)
argument_list|)
block|,
operator|new
name|LDAPLoginProperty
argument_list|(
name|USER_BASE
argument_list|,
operator|(
name|String
operator|)
name|options
operator|.
name|get
argument_list|(
name|USER_BASE
argument_list|)
argument_list|)
block|,
operator|new
name|LDAPLoginProperty
argument_list|(
name|USER_SEARCH_MATCHING
argument_list|,
operator|(
name|String
operator|)
name|options
operator|.
name|get
argument_list|(
name|USER_SEARCH_MATCHING
argument_list|)
argument_list|)
block|,
operator|new
name|LDAPLoginProperty
argument_list|(
name|USER_SEARCH_SUBTREE
argument_list|,
operator|(
name|String
operator|)
name|options
operator|.
name|get
argument_list|(
name|USER_SEARCH_SUBTREE
argument_list|)
argument_list|)
block|,
operator|new
name|LDAPLoginProperty
argument_list|(
name|ROLE_BASE
argument_list|,
operator|(
name|String
operator|)
name|options
operator|.
name|get
argument_list|(
name|ROLE_BASE
argument_list|)
argument_list|)
block|,
operator|new
name|LDAPLoginProperty
argument_list|(
name|ROLE_NAME
argument_list|,
operator|(
name|String
operator|)
name|options
operator|.
name|get
argument_list|(
name|ROLE_NAME
argument_list|)
argument_list|)
block|,
operator|new
name|LDAPLoginProperty
argument_list|(
name|ROLE_SEARCH_MATCHING
argument_list|,
operator|(
name|String
operator|)
name|options
operator|.
name|get
argument_list|(
name|ROLE_SEARCH_MATCHING
argument_list|)
argument_list|)
block|,
operator|new
name|LDAPLoginProperty
argument_list|(
name|ROLE_SEARCH_SUBTREE
argument_list|,
operator|(
name|String
operator|)
name|options
operator|.
name|get
argument_list|(
name|ROLE_SEARCH_SUBTREE
argument_list|)
argument_list|)
block|,
operator|new
name|LDAPLoginProperty
argument_list|(
name|USER_ROLE_NAME
argument_list|,
operator|(
name|String
operator|)
name|options
operator|.
name|get
argument_list|(
name|USER_ROLE_NAME
argument_list|)
argument_list|)
block|,         		}
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
literal|"User name"
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
literal|"Password"
argument_list|,
literal|false
argument_list|)
expr_stmt|;
try|try
block|{
name|handler
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
operator|(
name|LoginException
operator|)
operator|new
name|LoginException
argument_list|()
operator|.
name|initCause
argument_list|(
name|ioe
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
operator|(
name|LoginException
operator|)
operator|new
name|LoginException
argument_list|()
operator|.
name|initCause
argument_list|(
name|uce
argument_list|)
throw|;
block|}
name|String
name|password
decl_stmt|;
name|username
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
if|if
condition|(
name|username
operator|==
literal|null
condition|)
return|return
literal|false
return|;
if|if
condition|(
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
operator|!=
literal|null
condition|)
name|password
operator|=
operator|new
name|String
argument_list|(
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
argument_list|)
expr_stmt|;
else|else
name|password
operator|=
literal|""
expr_stmt|;
comment|// authenticate will throw LoginException
comment|// in case of failed authentication
name|authenticate
argument_list|(
name|username
argument_list|,
name|password
argument_list|)
expr_stmt|;
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
name|username
operator|=
literal|null
expr_stmt|;
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
name|Set
argument_list|<
name|Principal
argument_list|>
name|principals
init|=
name|subject
operator|.
name|getPrincipals
argument_list|()
decl_stmt|;
name|principals
operator|.
name|add
argument_list|(
operator|new
name|UserPrincipal
argument_list|(
name|username
argument_list|)
argument_list|)
expr_stmt|;
for|for
control|(
name|GroupPrincipal
name|gp
range|:
name|groups
control|)
block|{
name|principals
operator|.
name|add
argument_list|(
name|gp
argument_list|)
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
name|abort
parameter_list|()
throws|throws
name|LoginException
block|{
name|username
operator|=
literal|null
expr_stmt|;
return|return
literal|true
return|;
block|}
specifier|protected
name|void
name|close
parameter_list|(
name|DirContext
name|context
parameter_list|)
block|{
try|try
block|{
name|context
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
name|log
operator|.
name|error
argument_list|(
name|e
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
specifier|protected
name|boolean
name|authenticate
parameter_list|(
name|String
name|username
parameter_list|,
name|String
name|password
parameter_list|)
throws|throws
name|LoginException
block|{
name|MessageFormat
name|userSearchMatchingFormat
decl_stmt|;
name|boolean
name|userSearchSubtreeBool
decl_stmt|;
name|DirContext
name|context
init|=
literal|null
decl_stmt|;
if|if
condition|(
name|log
operator|.
name|isDebugEnabled
argument_list|()
condition|)
block|{
name|log
operator|.
name|debug
argument_list|(
literal|"Create the LDAP initial context."
argument_list|)
expr_stmt|;
block|}
try|try
block|{
name|context
operator|=
name|open
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|NamingException
name|ne
parameter_list|)
block|{
name|FailedLoginException
name|ex
init|=
operator|new
name|FailedLoginException
argument_list|(
literal|"Error opening LDAP connection"
argument_list|)
decl_stmt|;
name|ex
operator|.
name|initCause
argument_list|(
name|ne
argument_list|)
expr_stmt|;
throw|throw
name|ex
throw|;
block|}
if|if
condition|(
operator|!
name|isLoginPropertySet
argument_list|(
name|USER_SEARCH_MATCHING
argument_list|)
condition|)
return|return
literal|false
return|;
name|userSearchMatchingFormat
operator|=
operator|new
name|MessageFormat
argument_list|(
name|getLDAPPropertyValue
argument_list|(
name|USER_SEARCH_MATCHING
argument_list|)
argument_list|)
expr_stmt|;
name|userSearchSubtreeBool
operator|=
name|Boolean
operator|.
name|valueOf
argument_list|(
name|getLDAPPropertyValue
argument_list|(
name|USER_SEARCH_SUBTREE
argument_list|)
argument_list|)
operator|.
name|booleanValue
argument_list|()
expr_stmt|;
try|try
block|{
name|String
name|filter
init|=
name|userSearchMatchingFormat
operator|.
name|format
argument_list|(
operator|new
name|String
index|[]
block|{
name|username
block|}
argument_list|)
decl_stmt|;
name|SearchControls
name|constraints
init|=
operator|new
name|SearchControls
argument_list|()
decl_stmt|;
if|if
condition|(
name|userSearchSubtreeBool
condition|)
block|{
name|constraints
operator|.
name|setSearchScope
argument_list|(
name|SearchControls
operator|.
name|SUBTREE_SCOPE
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|constraints
operator|.
name|setSearchScope
argument_list|(
name|SearchControls
operator|.
name|ONELEVEL_SCOPE
argument_list|)
expr_stmt|;
block|}
comment|// setup attributes
name|List
argument_list|<
name|String
argument_list|>
name|list
init|=
operator|new
name|ArrayList
argument_list|<
name|String
argument_list|>
argument_list|()
decl_stmt|;
if|if
condition|(
name|isLoginPropertySet
argument_list|(
name|USER_ROLE_NAME
argument_list|)
condition|)
block|{
name|list
operator|.
name|add
argument_list|(
name|getLDAPPropertyValue
argument_list|(
name|USER_ROLE_NAME
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|String
index|[]
name|attribs
init|=
operator|new
name|String
index|[
name|list
operator|.
name|size
argument_list|()
index|]
decl_stmt|;
name|list
operator|.
name|toArray
argument_list|(
name|attribs
argument_list|)
expr_stmt|;
name|constraints
operator|.
name|setReturningAttributes
argument_list|(
name|attribs
argument_list|)
expr_stmt|;
if|if
condition|(
name|log
operator|.
name|isDebugEnabled
argument_list|()
condition|)
block|{
name|log
operator|.
name|debug
argument_list|(
literal|"Get the user DN."
argument_list|)
expr_stmt|;
name|log
operator|.
name|debug
argument_list|(
literal|"Looking for the user in LDAP with "
argument_list|)
expr_stmt|;
name|log
operator|.
name|debug
argument_list|(
literal|"  base DN: "
operator|+
name|getLDAPPropertyValue
argument_list|(
name|USER_BASE
argument_list|)
argument_list|)
expr_stmt|;
name|log
operator|.
name|debug
argument_list|(
literal|"  filter: "
operator|+
name|filter
argument_list|)
expr_stmt|;
block|}
name|NamingEnumeration
argument_list|<
name|SearchResult
argument_list|>
name|results
init|=
name|context
operator|.
name|search
argument_list|(
name|getLDAPPropertyValue
argument_list|(
name|USER_BASE
argument_list|)
argument_list|,
name|filter
argument_list|,
name|constraints
argument_list|)
decl_stmt|;
if|if
condition|(
name|results
operator|==
literal|null
operator|||
operator|!
name|results
operator|.
name|hasMore
argument_list|()
condition|)
block|{
name|log
operator|.
name|warn
argument_list|(
literal|"User "
operator|+
name|username
operator|+
literal|" not found in LDAP."
argument_list|)
expr_stmt|;
throw|throw
operator|new
name|FailedLoginException
argument_list|(
literal|"User "
operator|+
name|username
operator|+
literal|" not found in LDAP."
argument_list|)
throw|;
block|}
name|SearchResult
name|result
init|=
name|results
operator|.
name|next
argument_list|()
decl_stmt|;
if|if
condition|(
name|results
operator|.
name|hasMore
argument_list|()
condition|)
block|{
comment|// ignore for now
block|}
name|NameParser
name|parser
init|=
name|context
operator|.
name|getNameParser
argument_list|(
literal|""
argument_list|)
decl_stmt|;
name|Name
name|contextName
init|=
name|parser
operator|.
name|parse
argument_list|(
name|context
operator|.
name|getNameInNamespace
argument_list|()
argument_list|)
decl_stmt|;
name|Name
name|baseName
init|=
name|parser
operator|.
name|parse
argument_list|(
name|getLDAPPropertyValue
argument_list|(
name|USER_BASE
argument_list|)
argument_list|)
decl_stmt|;
name|Name
name|entryName
init|=
name|parser
operator|.
name|parse
argument_list|(
name|result
operator|.
name|getName
argument_list|()
argument_list|)
decl_stmt|;
name|Name
name|name
init|=
name|contextName
operator|.
name|addAll
argument_list|(
name|baseName
argument_list|)
decl_stmt|;
name|name
operator|=
name|name
operator|.
name|addAll
argument_list|(
name|entryName
argument_list|)
expr_stmt|;
name|String
name|dn
init|=
name|name
operator|.
name|toString
argument_list|()
decl_stmt|;
name|Attributes
name|attrs
init|=
name|result
operator|.
name|getAttributes
argument_list|()
decl_stmt|;
if|if
condition|(
name|attrs
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|FailedLoginException
argument_list|(
literal|"User found, but LDAP entry malformed: "
operator|+
name|username
argument_list|)
throw|;
block|}
name|List
argument_list|<
name|String
argument_list|>
name|roles
init|=
literal|null
decl_stmt|;
if|if
condition|(
name|isLoginPropertySet
argument_list|(
name|USER_ROLE_NAME
argument_list|)
condition|)
block|{
name|roles
operator|=
name|addAttributeValues
argument_list|(
name|getLDAPPropertyValue
argument_list|(
name|USER_ROLE_NAME
argument_list|)
argument_list|,
name|attrs
argument_list|,
name|roles
argument_list|)
expr_stmt|;
block|}
comment|// check the credentials by binding to server
if|if
condition|(
name|bindUser
argument_list|(
name|context
argument_list|,
name|dn
argument_list|,
name|password
argument_list|)
condition|)
block|{
comment|// if authenticated add more roles
name|roles
operator|=
name|getRoles
argument_list|(
name|context
argument_list|,
name|dn
argument_list|,
name|username
argument_list|,
name|roles
argument_list|)
expr_stmt|;
if|if
condition|(
name|log
operator|.
name|isDebugEnabled
argument_list|()
condition|)
block|{
name|log
operator|.
name|debug
argument_list|(
literal|"Roles "
operator|+
name|roles
operator|+
literal|" for user "
operator|+
name|username
argument_list|)
expr_stmt|;
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
name|roles
operator|.
name|size
argument_list|()
condition|;
name|i
operator|++
control|)
block|{
name|groups
operator|.
name|add
argument_list|(
operator|new
name|GroupPrincipal
argument_list|(
name|roles
operator|.
name|get
argument_list|(
name|i
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
else|else
block|{
throw|throw
operator|new
name|FailedLoginException
argument_list|(
literal|"Password does not match for user: "
operator|+
name|username
argument_list|)
throw|;
block|}
block|}
catch|catch
parameter_list|(
name|CommunicationException
name|e
parameter_list|)
block|{
name|FailedLoginException
name|ex
init|=
operator|new
name|FailedLoginException
argument_list|(
literal|"Error contacting LDAP"
argument_list|)
decl_stmt|;
name|ex
operator|.
name|initCause
argument_list|(
name|e
argument_list|)
expr_stmt|;
throw|throw
name|ex
throw|;
block|}
catch|catch
parameter_list|(
name|NamingException
name|e
parameter_list|)
block|{
if|if
condition|(
name|context
operator|!=
literal|null
condition|)
block|{
name|close
argument_list|(
name|context
argument_list|)
expr_stmt|;
block|}
name|FailedLoginException
name|ex
init|=
operator|new
name|FailedLoginException
argument_list|(
literal|"Error contacting LDAP"
argument_list|)
decl_stmt|;
name|ex
operator|.
name|initCause
argument_list|(
name|e
argument_list|)
expr_stmt|;
throw|throw
name|ex
throw|;
block|}
return|return
literal|true
return|;
block|}
specifier|protected
name|List
argument_list|<
name|String
argument_list|>
name|getRoles
parameter_list|(
name|DirContext
name|context
parameter_list|,
name|String
name|dn
parameter_list|,
name|String
name|username
parameter_list|,
name|List
argument_list|<
name|String
argument_list|>
name|currentRoles
parameter_list|)
throws|throws
name|NamingException
block|{
name|List
argument_list|<
name|String
argument_list|>
name|list
init|=
name|currentRoles
decl_stmt|;
name|MessageFormat
name|roleSearchMatchingFormat
decl_stmt|;
name|boolean
name|roleSearchSubtreeBool
decl_stmt|;
name|roleSearchMatchingFormat
operator|=
operator|new
name|MessageFormat
argument_list|(
name|getLDAPPropertyValue
argument_list|(
name|ROLE_SEARCH_MATCHING
argument_list|)
argument_list|)
expr_stmt|;
name|roleSearchSubtreeBool
operator|=
name|Boolean
operator|.
name|valueOf
argument_list|(
name|getLDAPPropertyValue
argument_list|(
name|ROLE_SEARCH_SUBTREE
argument_list|)
argument_list|)
operator|.
name|booleanValue
argument_list|()
expr_stmt|;
if|if
condition|(
name|list
operator|==
literal|null
condition|)
block|{
name|list
operator|=
operator|new
name|ArrayList
argument_list|<
name|String
argument_list|>
argument_list|()
expr_stmt|;
block|}
if|if
condition|(
operator|!
name|isLoginPropertySet
argument_list|(
name|ROLE_NAME
argument_list|)
condition|)
block|{
return|return
name|list
return|;
block|}
name|String
name|filter
init|=
name|roleSearchMatchingFormat
operator|.
name|format
argument_list|(
operator|new
name|String
index|[]
block|{
name|doRFC2254Encoding
argument_list|(
name|dn
argument_list|)
block|,
name|username
block|}
argument_list|)
decl_stmt|;
name|SearchControls
name|constraints
init|=
operator|new
name|SearchControls
argument_list|()
decl_stmt|;
if|if
condition|(
name|roleSearchSubtreeBool
condition|)
block|{
name|constraints
operator|.
name|setSearchScope
argument_list|(
name|SearchControls
operator|.
name|SUBTREE_SCOPE
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|constraints
operator|.
name|setSearchScope
argument_list|(
name|SearchControls
operator|.
name|ONELEVEL_SCOPE
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|log
operator|.
name|isDebugEnabled
argument_list|()
condition|)
block|{
name|log
operator|.
name|debug
argument_list|(
literal|"Get user roles."
argument_list|)
expr_stmt|;
name|log
operator|.
name|debug
argument_list|(
literal|"Looking for the user roles in LDAP with "
argument_list|)
expr_stmt|;
name|log
operator|.
name|debug
argument_list|(
literal|"  base DN: "
operator|+
name|getLDAPPropertyValue
argument_list|(
name|ROLE_BASE
argument_list|)
argument_list|)
expr_stmt|;
name|log
operator|.
name|debug
argument_list|(
literal|"  filter: "
operator|+
name|filter
argument_list|)
expr_stmt|;
block|}
name|NamingEnumeration
argument_list|<
name|SearchResult
argument_list|>
name|results
init|=
name|context
operator|.
name|search
argument_list|(
name|getLDAPPropertyValue
argument_list|(
name|ROLE_BASE
argument_list|)
argument_list|,
name|filter
argument_list|,
name|constraints
argument_list|)
decl_stmt|;
while|while
condition|(
name|results
operator|.
name|hasMore
argument_list|()
condition|)
block|{
name|SearchResult
name|result
init|=
name|results
operator|.
name|next
argument_list|()
decl_stmt|;
name|Attributes
name|attrs
init|=
name|result
operator|.
name|getAttributes
argument_list|()
decl_stmt|;
if|if
condition|(
name|attrs
operator|==
literal|null
condition|)
block|{
continue|continue;
block|}
name|list
operator|=
name|addAttributeValues
argument_list|(
name|getLDAPPropertyValue
argument_list|(
name|ROLE_NAME
argument_list|)
argument_list|,
name|attrs
argument_list|,
name|list
argument_list|)
expr_stmt|;
block|}
return|return
name|list
return|;
block|}
specifier|protected
name|String
name|doRFC2254Encoding
parameter_list|(
name|String
name|inputString
parameter_list|)
block|{
name|StringBuffer
name|buf
init|=
operator|new
name|StringBuffer
argument_list|(
name|inputString
operator|.
name|length
argument_list|()
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
name|inputString
operator|.
name|length
argument_list|()
condition|;
name|i
operator|++
control|)
block|{
name|char
name|c
init|=
name|inputString
operator|.
name|charAt
argument_list|(
name|i
argument_list|)
decl_stmt|;
switch|switch
condition|(
name|c
condition|)
block|{
case|case
literal|'\\'
case|:
name|buf
operator|.
name|append
argument_list|(
literal|"\\5c"
argument_list|)
expr_stmt|;
break|break;
case|case
literal|'*'
case|:
name|buf
operator|.
name|append
argument_list|(
literal|"\\2a"
argument_list|)
expr_stmt|;
break|break;
case|case
literal|'('
case|:
name|buf
operator|.
name|append
argument_list|(
literal|"\\28"
argument_list|)
expr_stmt|;
break|break;
case|case
literal|')'
case|:
name|buf
operator|.
name|append
argument_list|(
literal|"\\29"
argument_list|)
expr_stmt|;
break|break;
case|case
literal|'\0'
case|:
name|buf
operator|.
name|append
argument_list|(
literal|"\\00"
argument_list|)
expr_stmt|;
break|break;
default|default:
name|buf
operator|.
name|append
argument_list|(
name|c
argument_list|)
expr_stmt|;
break|break;
block|}
block|}
return|return
name|buf
operator|.
name|toString
argument_list|()
return|;
block|}
specifier|protected
name|boolean
name|bindUser
parameter_list|(
name|DirContext
name|context
parameter_list|,
name|String
name|dn
parameter_list|,
name|String
name|password
parameter_list|)
throws|throws
name|NamingException
block|{
name|boolean
name|isValid
init|=
literal|false
decl_stmt|;
if|if
condition|(
name|log
operator|.
name|isDebugEnabled
argument_list|()
condition|)
block|{
name|log
operator|.
name|debug
argument_list|(
literal|"Binding the user."
argument_list|)
expr_stmt|;
block|}
name|context
operator|.
name|addToEnvironment
argument_list|(
name|Context
operator|.
name|SECURITY_PRINCIPAL
argument_list|,
name|dn
argument_list|)
expr_stmt|;
name|context
operator|.
name|addToEnvironment
argument_list|(
name|Context
operator|.
name|SECURITY_CREDENTIALS
argument_list|,
name|password
argument_list|)
expr_stmt|;
try|try
block|{
name|context
operator|.
name|getAttributes
argument_list|(
literal|""
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|isValid
operator|=
literal|true
expr_stmt|;
if|if
condition|(
name|log
operator|.
name|isDebugEnabled
argument_list|()
condition|)
block|{
name|log
operator|.
name|debug
argument_list|(
literal|"User "
operator|+
name|dn
operator|+
literal|" successfully bound."
argument_list|)
expr_stmt|;
block|}
block|}
catch|catch
parameter_list|(
name|AuthenticationException
name|e
parameter_list|)
block|{
name|isValid
operator|=
literal|false
expr_stmt|;
if|if
condition|(
name|log
operator|.
name|isDebugEnabled
argument_list|()
condition|)
block|{
name|log
operator|.
name|debug
argument_list|(
literal|"Authentication failed for dn="
operator|+
name|dn
argument_list|)
expr_stmt|;
block|}
block|}
if|if
condition|(
name|isLoginPropertySet
argument_list|(
name|CONNECTION_USERNAME
argument_list|)
condition|)
block|{
name|context
operator|.
name|addToEnvironment
argument_list|(
name|Context
operator|.
name|SECURITY_PRINCIPAL
argument_list|,
name|getLDAPPropertyValue
argument_list|(
name|CONNECTION_USERNAME
argument_list|)
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|context
operator|.
name|removeFromEnvironment
argument_list|(
name|Context
operator|.
name|SECURITY_PRINCIPAL
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|isLoginPropertySet
argument_list|(
name|CONNECTION_PASSWORD
argument_list|)
condition|)
block|{
name|context
operator|.
name|addToEnvironment
argument_list|(
name|Context
operator|.
name|SECURITY_CREDENTIALS
argument_list|,
name|getLDAPPropertyValue
argument_list|(
name|CONNECTION_PASSWORD
argument_list|)
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|context
operator|.
name|removeFromEnvironment
argument_list|(
name|Context
operator|.
name|SECURITY_CREDENTIALS
argument_list|)
expr_stmt|;
block|}
return|return
name|isValid
return|;
block|}
specifier|private
name|List
argument_list|<
name|String
argument_list|>
name|addAttributeValues
parameter_list|(
name|String
name|attrId
parameter_list|,
name|Attributes
name|attrs
parameter_list|,
name|List
argument_list|<
name|String
argument_list|>
name|values
parameter_list|)
throws|throws
name|NamingException
block|{
if|if
condition|(
name|attrId
operator|==
literal|null
operator|||
name|attrs
operator|==
literal|null
condition|)
block|{
return|return
name|values
return|;
block|}
if|if
condition|(
name|values
operator|==
literal|null
condition|)
block|{
name|values
operator|=
operator|new
name|ArrayList
argument_list|<
name|String
argument_list|>
argument_list|()
expr_stmt|;
block|}
name|Attribute
name|attr
init|=
name|attrs
operator|.
name|get
argument_list|(
name|attrId
argument_list|)
decl_stmt|;
if|if
condition|(
name|attr
operator|==
literal|null
condition|)
block|{
return|return
name|values
return|;
block|}
name|NamingEnumeration
argument_list|<
name|?
argument_list|>
name|e
init|=
name|attr
operator|.
name|getAll
argument_list|()
decl_stmt|;
while|while
condition|(
name|e
operator|.
name|hasMore
argument_list|()
condition|)
block|{
name|String
name|value
init|=
operator|(
name|String
operator|)
name|e
operator|.
name|next
argument_list|()
decl_stmt|;
name|values
operator|.
name|add
argument_list|(
name|value
argument_list|)
expr_stmt|;
block|}
return|return
name|values
return|;
block|}
specifier|protected
name|DirContext
name|open
parameter_list|()
throws|throws
name|NamingException
block|{
try|try
block|{
name|Hashtable
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|env
init|=
operator|new
name|Hashtable
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
argument_list|()
decl_stmt|;
name|env
operator|.
name|put
argument_list|(
name|Context
operator|.
name|INITIAL_CONTEXT_FACTORY
argument_list|,
name|getLDAPPropertyValue
argument_list|(
name|INITIAL_CONTEXT_FACTORY
argument_list|)
argument_list|)
expr_stmt|;
if|if
condition|(
name|isLoginPropertySet
argument_list|(
name|CONNECTION_USERNAME
argument_list|)
condition|)
block|{
name|env
operator|.
name|put
argument_list|(
name|Context
operator|.
name|SECURITY_PRINCIPAL
argument_list|,
name|getLDAPPropertyValue
argument_list|(
name|CONNECTION_USERNAME
argument_list|)
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|isLoginPropertySet
argument_list|(
name|CONNECTION_PASSWORD
argument_list|)
condition|)
block|{
name|env
operator|.
name|put
argument_list|(
name|Context
operator|.
name|SECURITY_CREDENTIALS
argument_list|,
name|getLDAPPropertyValue
argument_list|(
name|CONNECTION_PASSWORD
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|env
operator|.
name|put
argument_list|(
name|Context
operator|.
name|SECURITY_PROTOCOL
argument_list|,
name|getLDAPPropertyValue
argument_list|(
name|CONNECTION_PROTOCOL
argument_list|)
argument_list|)
expr_stmt|;
name|env
operator|.
name|put
argument_list|(
name|Context
operator|.
name|PROVIDER_URL
argument_list|,
name|getLDAPPropertyValue
argument_list|(
name|CONNECTION_URL
argument_list|)
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
name|getLDAPPropertyValue
argument_list|(
name|AUTHENTICATION
argument_list|)
argument_list|)
expr_stmt|;
name|context
operator|=
operator|new
name|InitialDirContext
argument_list|(
name|env
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|NamingException
name|e
parameter_list|)
block|{
name|log
operator|.
name|error
argument_list|(
name|e
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
throw|throw
name|e
throw|;
block|}
return|return
name|context
return|;
block|}
specifier|private
name|String
name|getLDAPPropertyValue
parameter_list|(
name|String
name|propertyName
parameter_list|)
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
name|config
operator|.
name|length
condition|;
name|i
operator|++
control|)
if|if
condition|(
name|config
index|[
name|i
index|]
operator|.
name|getPropertyName
argument_list|()
operator|==
name|propertyName
condition|)
return|return
name|config
index|[
name|i
index|]
operator|.
name|getPropertyValue
argument_list|()
return|;
return|return
literal|null
return|;
block|}
specifier|private
name|boolean
name|isLoginPropertySet
parameter_list|(
name|String
name|propertyName
parameter_list|)
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
name|config
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
if|if
condition|(
name|config
index|[
name|i
index|]
operator|.
name|getPropertyName
argument_list|()
operator|==
name|propertyName
operator|&&
name|config
index|[
name|i
index|]
operator|.
name|getPropertyValue
argument_list|()
operator|!=
literal|null
condition|)
return|return
literal|true
return|;
block|}
return|return
literal|false
return|;
block|}
block|}
end_class

end_unit

