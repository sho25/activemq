begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  *<a href="http://activemq.org">ActiveMQ: The Open Source Message Fabric</a>  *  * Copyright 2005 (C) LogicBlaze, Inc. http://www.logicblaze.com  *  * Licensed under the Apache License, Version 2.0 (the "License");  * you may not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  * http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_comment
comment|/**  *  * Copyright 2005 The Apache Software Foundation  *  *  Licensed under the Apache License, Version 2.0 (the "License");  *  you may not use this file except in compliance with the License.  *  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  *  Unless required by applicable law or agreed to in writing, software  *  distributed under the License is distributed on an "AS IS" BASIS,  *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  *  See the License for the specific language governing permissions and  *  limitations under the License.  */
end_comment

begin_package
package|package
name|org
operator|.
name|activemq
operator|.
name|jaas
operator|.
name|ldap
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
name|InetAddress
import|;
end_import

begin_import
import|import
name|java
operator|.
name|net
operator|.
name|InetSocketAddress
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
name|Iterator
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
name|DirContext
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|naming
operator|.
name|ldap
operator|.
name|Control
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|naming
operator|.
name|ldap
operator|.
name|InitialLdapContext
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|naming
operator|.
name|ldap
operator|.
name|LdapContext
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

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|kerberos
operator|.
name|protocol
operator|.
name|KerberosProtocolProvider
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|kerberos
operator|.
name|sam
operator|.
name|SamSubsystem
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|kerberos
operator|.
name|service
operator|.
name|KdcConfiguration
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|kerberos
operator|.
name|store
operator|.
name|JndiPrincipalStoreImpl
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|kerberos
operator|.
name|store
operator|.
name|PrincipalStore
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|ldap
operator|.
name|common
operator|.
name|exception
operator|.
name|LdapConfigurationException
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|ldap
operator|.
name|common
operator|.
name|name
operator|.
name|LdapName
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|ldap
operator|.
name|common
operator|.
name|util
operator|.
name|NamespaceTools
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|ldap
operator|.
name|common
operator|.
name|util
operator|.
name|PropertiesUtils
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|ldap
operator|.
name|server
operator|.
name|jndi
operator|.
name|ContextFactoryService
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|ldap
operator|.
name|server
operator|.
name|jndi
operator|.
name|CoreContextFactory
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|ldap
operator|.
name|server
operator|.
name|protocol
operator|.
name|LdapProtocolProvider
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|mina
operator|.
name|common
operator|.
name|TransportType
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|mina
operator|.
name|registry
operator|.
name|Service
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|mina
operator|.
name|registry
operator|.
name|ServiceRegistry
import|;
end_import

begin_comment
comment|/**  * Adds additional bootstrapping for server socket listeners when firing  * up the server.  *  * @version $Rev: 233391 $ $Date: 2005-08-18 16:38:47 -0600 (Thu, 18 Aug 2005) $  * @see javax.naming.spi.InitialContextFactory  */
end_comment

begin_class
specifier|public
class|class
name|ServerContextFactory
extends|extends
name|CoreContextFactory
block|{
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
name|ServerContextFactory
operator|.
name|class
argument_list|)
decl_stmt|;
specifier|private
specifier|static
name|Service
name|ldapService
decl_stmt|;
specifier|private
specifier|static
name|Service
name|kerberosService
decl_stmt|;
specifier|private
specifier|static
name|ServiceRegistry
name|minaRegistry
decl_stmt|;
specifier|protected
name|ServiceRegistry
name|getMinaRegistry
parameter_list|()
block|{
return|return
name|minaRegistry
return|;
block|}
specifier|public
name|void
name|afterShutdown
parameter_list|(
name|ContextFactoryService
name|service
parameter_list|)
block|{
if|if
condition|(
name|minaRegistry
operator|!=
literal|null
condition|)
block|{
if|if
condition|(
name|ldapService
operator|!=
literal|null
condition|)
block|{
name|minaRegistry
operator|.
name|unbind
argument_list|(
name|ldapService
argument_list|)
expr_stmt|;
if|if
condition|(
name|log
operator|.
name|isInfoEnabled
argument_list|()
condition|)
block|{
name|log
operator|.
name|info
argument_list|(
literal|"Unbind of LDAP Service complete: "
operator|+
name|ldapService
argument_list|)
expr_stmt|;
block|}
name|ldapService
operator|=
literal|null
expr_stmt|;
block|}
if|if
condition|(
name|kerberosService
operator|!=
literal|null
condition|)
block|{
name|minaRegistry
operator|.
name|unbind
argument_list|(
name|kerberosService
argument_list|)
expr_stmt|;
if|if
condition|(
name|log
operator|.
name|isInfoEnabled
argument_list|()
condition|)
block|{
name|log
operator|.
name|info
argument_list|(
literal|"Unbind of KRB5 Service complete: "
operator|+
name|kerberosService
argument_list|)
expr_stmt|;
block|}
name|kerberosService
operator|=
literal|null
expr_stmt|;
block|}
block|}
block|}
specifier|public
name|void
name|afterStartup
parameter_list|(
name|ContextFactoryService
name|service
parameter_list|)
throws|throws
name|NamingException
block|{
name|ServerStartupConfiguration
name|cfg
init|=
operator|(
name|ServerStartupConfiguration
operator|)
name|service
operator|.
name|getConfiguration
argument_list|()
operator|.
name|getStartupConfiguration
argument_list|()
decl_stmt|;
name|Hashtable
name|env
init|=
name|service
operator|.
name|getConfiguration
argument_list|()
operator|.
name|getEnvironment
argument_list|()
decl_stmt|;
if|if
condition|(
name|cfg
operator|.
name|isEnableNetworking
argument_list|()
condition|)
block|{
name|setupRegistry
argument_list|(
name|cfg
argument_list|)
expr_stmt|;
name|startLdapProtocol
argument_list|(
name|cfg
argument_list|,
name|env
argument_list|)
expr_stmt|;
if|if
condition|(
name|cfg
operator|.
name|isEnableKerberos
argument_list|()
condition|)
block|{
name|startKerberosProtocol
argument_list|(
name|env
argument_list|)
expr_stmt|;
block|}
block|}
block|}
comment|/**      * Starts up the MINA registry so various protocol providers can be started.      */
specifier|private
name|void
name|setupRegistry
parameter_list|(
name|ServerStartupConfiguration
name|cfg
parameter_list|)
block|{
name|minaRegistry
operator|=
name|cfg
operator|.
name|getMinaServiceRegistry
argument_list|()
expr_stmt|;
block|}
comment|/**      * Starts the Kerberos protocol provider which is backed by the LDAP store.      *      * @throws NamingException if there are problems starting up the Kerberos provider      */
specifier|private
name|void
name|startKerberosProtocol
parameter_list|(
name|Hashtable
name|env
parameter_list|)
throws|throws
name|NamingException
block|{
comment|/*          * Looks like KdcConfiguration takes properties and we use Hashtable for JNDI          * so I'm copying over the String based properties into a new Properties obj.          */
name|Properties
name|props
init|=
operator|new
name|Properties
argument_list|()
decl_stmt|;
name|Iterator
name|list
init|=
name|env
operator|.
name|keySet
argument_list|()
operator|.
name|iterator
argument_list|()
decl_stmt|;
while|while
condition|(
name|list
operator|.
name|hasNext
argument_list|()
condition|)
block|{
name|String
name|key
init|=
operator|(
name|String
operator|)
name|list
operator|.
name|next
argument_list|()
decl_stmt|;
if|if
condition|(
name|env
operator|.
name|get
argument_list|(
name|key
argument_list|)
operator|instanceof
name|String
condition|)
block|{
name|props
operator|.
name|setProperty
argument_list|(
name|key
argument_list|,
operator|(
name|String
operator|)
name|env
operator|.
name|get
argument_list|(
name|key
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
comment|// construct the configuration, get the port, create the service, and prepare kdc objects
name|KdcConfiguration
name|config
init|=
operator|new
name|KdcConfiguration
argument_list|(
name|props
argument_list|)
decl_stmt|;
name|int
name|port
init|=
name|PropertiesUtils
operator|.
name|get
argument_list|(
name|env
argument_list|,
name|KdcConfiguration
operator|.
name|KERBEROS_PORT_KEY
argument_list|,
name|KdcConfiguration
operator|.
name|DEFAULT_KERBEROS_PORT
argument_list|)
decl_stmt|;
name|Service
name|service
init|=
operator|new
name|Service
argument_list|(
literal|"kerberos"
argument_list|,
name|TransportType
operator|.
name|DATAGRAM
argument_list|,
operator|new
name|InetSocketAddress
argument_list|(
name|port
argument_list|)
argument_list|)
decl_stmt|;
name|LdapContext
name|ctx
init|=
name|getBaseRealmContext
argument_list|(
name|config
argument_list|,
name|env
argument_list|)
decl_stmt|;
name|PrincipalStore
name|store
init|=
operator|new
name|JndiPrincipalStoreImpl
argument_list|(
name|ctx
argument_list|,
operator|new
name|LdapName
argument_list|(
literal|"ou=Users"
argument_list|)
argument_list|)
decl_stmt|;
name|SamSubsystem
operator|.
name|getInstance
argument_list|()
operator|.
name|setUserContext
argument_list|(
operator|(
name|DirContext
operator|)
name|ctx
argument_list|,
literal|"ou=Users"
argument_list|)
expr_stmt|;
try|try
block|{
name|minaRegistry
operator|.
name|bind
argument_list|(
name|service
argument_list|,
operator|new
name|KerberosProtocolProvider
argument_list|(
name|config
argument_list|,
name|store
argument_list|)
argument_list|)
expr_stmt|;
name|kerberosService
operator|=
name|service
expr_stmt|;
if|if
condition|(
name|log
operator|.
name|isInfoEnabled
argument_list|()
condition|)
block|{
name|log
operator|.
name|info
argument_list|(
literal|"Successful bind of KRB5 Service completed: "
operator|+
name|kerberosService
argument_list|)
expr_stmt|;
block|}
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
name|log
operator|.
name|error
argument_list|(
literal|"Could not start the kerberos service on port "
operator|+
name|KdcConfiguration
operator|.
name|DEFAULT_KERBEROS_PORT
argument_list|,
name|e
argument_list|)
expr_stmt|;
block|}
block|}
comment|/**      * Maps a Kerberos Realm name to a position within the DIT.  The primary realm of      * the KDC will use this area for configuration and for storing user entries.      *      * @param config the KDC's configuration      * @param env    the JNDI environment properties      * @return the base context for the primary realm of the KDC      * @throws NamingException      */
specifier|private
name|LdapContext
name|getBaseRealmContext
parameter_list|(
name|KdcConfiguration
name|config
parameter_list|,
name|Hashtable
name|env
parameter_list|)
throws|throws
name|NamingException
block|{
name|Hashtable
name|cloned
init|=
operator|(
name|Hashtable
operator|)
name|env
operator|.
name|clone
argument_list|()
decl_stmt|;
name|String
name|dn
init|=
name|NamespaceTools
operator|.
name|inferLdapName
argument_list|(
name|config
operator|.
name|getPrimaryRealm
argument_list|()
argument_list|)
decl_stmt|;
name|cloned
operator|.
name|put
argument_list|(
name|Context
operator|.
name|PROVIDER_URL
argument_list|,
name|dn
argument_list|)
expr_stmt|;
if|if
condition|(
name|log
operator|.
name|isInfoEnabled
argument_list|()
condition|)
block|{
name|log
operator|.
name|info
argument_list|(
literal|"Getting initial context for realm base at "
operator|+
name|dn
operator|+
literal|" for "
operator|+
name|config
operator|.
name|getPrimaryRealm
argument_list|()
argument_list|)
expr_stmt|;
block|}
return|return
operator|new
name|InitialLdapContext
argument_list|(
name|cloned
argument_list|,
operator|new
name|Control
index|[]
block|{}
argument_list|)
return|;
block|}
comment|/**      * Starts up the LDAP protocol provider to service LDAP requests      *      * @throws NamingException if there are problems starting the LDAP provider      */
specifier|private
name|void
name|startLdapProtocol
parameter_list|(
name|ServerStartupConfiguration
name|cfg
parameter_list|,
name|Hashtable
name|env
parameter_list|)
throws|throws
name|NamingException
block|{
name|int
name|port
init|=
name|cfg
operator|.
name|getLdapPort
argument_list|()
decl_stmt|;
name|InetAddress
name|host
init|=
name|cfg
operator|.
name|getHost
argument_list|()
decl_stmt|;
name|Service
name|service
init|=
operator|new
name|Service
argument_list|(
literal|"ldap"
argument_list|,
name|TransportType
operator|.
name|SOCKET
argument_list|,
operator|new
name|InetSocketAddress
argument_list|(
name|host
argument_list|,
name|port
argument_list|)
argument_list|)
decl_stmt|;
try|try
block|{
name|minaRegistry
operator|.
name|bind
argument_list|(
name|service
argument_list|,
operator|new
name|LdapProtocolProvider
argument_list|(
operator|(
name|Hashtable
operator|)
name|env
operator|.
name|clone
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|ldapService
operator|=
name|service
expr_stmt|;
if|if
condition|(
name|log
operator|.
name|isInfoEnabled
argument_list|()
condition|)
block|{
name|log
operator|.
name|info
argument_list|(
literal|"Successful bind of LDAP Service completed: "
operator|+
name|ldapService
argument_list|)
expr_stmt|;
block|}
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
name|String
name|msg
init|=
literal|"Failed to bind the LDAP protocol service to the service registry: "
operator|+
name|service
decl_stmt|;
name|LdapConfigurationException
name|lce
init|=
operator|new
name|LdapConfigurationException
argument_list|(
name|msg
argument_list|)
decl_stmt|;
name|lce
operator|.
name|setRootCause
argument_list|(
name|e
argument_list|)
expr_stmt|;
name|log
operator|.
name|error
argument_list|(
name|msg
argument_list|,
name|e
argument_list|)
expr_stmt|;
throw|throw
name|lce
throw|;
block|}
block|}
block|}
end_class

end_unit

