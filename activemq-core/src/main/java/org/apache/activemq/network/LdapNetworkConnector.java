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
name|network
package|;
end_package

begin_import
import|import
name|java
operator|.
name|net
operator|.
name|URI
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
name|Map
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Random
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
name|ConcurrentHashMap
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
name|naming
operator|.
name|event
operator|.
name|EventDirContext
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|naming
operator|.
name|event
operator|.
name|NamespaceChangeListener
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|naming
operator|.
name|event
operator|.
name|NamingEvent
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|naming
operator|.
name|event
operator|.
name|NamingExceptionEvent
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|naming
operator|.
name|event
operator|.
name|ObjectChangeListener
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
name|util
operator|.
name|URISupport
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
name|util
operator|.
name|URISupport
operator|.
name|CompositeData
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
comment|/**  * class to create dynamic network connectors listed in an directory  * server using the LDAP v3 protocol as defined in RFC 2251, the  * entries listed in the directory server must implement the ipHost  * and ipService objectClasses as defined in RFC 2307.  *   * @author Trevor Pounds  * @see<a href="http://www.faqs.org/rfcs/rfc2251.html">RFC 2251</a>  * @see<a href="http://www.faqs.org/rfcs/rfc2307.html">RFC 2307</a>  *  * @org.apache.xbean.XBean element="ldapNetworkConnector"  */
end_comment

begin_class
specifier|public
class|class
name|LdapNetworkConnector
extends|extends
name|NetworkConnector
implements|implements
name|NamespaceChangeListener
implements|,
name|ObjectChangeListener
block|{
specifier|private
specifier|static
specifier|final
name|Log
name|LOG
init|=
name|LogFactory
operator|.
name|getLog
argument_list|(
name|LdapNetworkConnector
operator|.
name|class
argument_list|)
decl_stmt|;
comment|// force returned entries to implement the ipHost and ipService object classes (RFC 2307)
specifier|private
specifier|static
specifier|final
name|String
name|REQUIRED_OBJECT_CLASS_FILTER
init|=
literal|"(&(objectClass=ipHost)(objectClass=ipService))"
decl_stmt|;
comment|// connection
specifier|private
name|URI
index|[]
name|availableURIs
init|=
literal|null
decl_stmt|;
specifier|private
name|int
name|availableURIsIndex
init|=
literal|0
decl_stmt|;
specifier|private
name|String
name|base
init|=
literal|null
decl_stmt|;
specifier|private
name|boolean
name|failover
init|=
literal|false
decl_stmt|;
specifier|private
name|long
name|curReconnectDelay
init|=
literal|1000
decl_stmt|;
comment|/* 1 sec */
specifier|private
name|long
name|maxReconnectDelay
init|=
literal|30000
decl_stmt|;
comment|/* 30 sec */
comment|// authentication
specifier|private
name|String
name|user
init|=
literal|null
decl_stmt|;
specifier|private
name|String
name|password
init|=
literal|null
decl_stmt|;
specifier|private
name|boolean
name|anonymousAuthentication
init|=
literal|false
decl_stmt|;
comment|// search
specifier|private
name|SearchControls
name|searchControls
init|=
operator|new
name|SearchControls
argument_list|(
comment|/* ONELEVEL_SCOPE */
argument_list|)
decl_stmt|;
specifier|private
name|String
name|searchFilter
init|=
name|REQUIRED_OBJECT_CLASS_FILTER
decl_stmt|;
specifier|private
name|boolean
name|searchEventListener
init|=
literal|false
decl_stmt|;
comment|// connector management
specifier|private
name|Map
argument_list|<
name|URI
argument_list|,
name|NetworkConnector
argument_list|>
name|connectorMap
init|=
operator|new
name|ConcurrentHashMap
argument_list|()
decl_stmt|;
specifier|private
name|Map
argument_list|<
name|URI
argument_list|,
name|Integer
argument_list|>
name|referenceMap
init|=
operator|new
name|ConcurrentHashMap
argument_list|()
decl_stmt|;
specifier|private
name|Map
argument_list|<
name|String
argument_list|,
name|URI
argument_list|>
name|uuidMap
init|=
operator|new
name|ConcurrentHashMap
argument_list|()
decl_stmt|;
comment|// local context
specifier|private
name|DirContext
name|context
init|=
literal|null
decl_stmt|;
comment|/**     * returns the next URI from the configured list     *     * @return random URI from the configured list     */
specifier|public
name|URI
name|getUri
parameter_list|()
block|{
return|return
name|availableURIs
index|[
operator|++
name|availableURIsIndex
operator|%
name|availableURIs
operator|.
name|length
index|]
return|;
block|}
comment|/**     * sets the LDAP server URI     *     * @param _uri LDAP server URI     */
specifier|public
name|void
name|setUri
parameter_list|(
name|URI
name|_uri
parameter_list|)
throws|throws
name|Exception
block|{
name|CompositeData
name|data
init|=
name|URISupport
operator|.
name|parseComposite
argument_list|(
name|_uri
argument_list|)
decl_stmt|;
if|if
condition|(
name|data
operator|.
name|getScheme
argument_list|()
operator|.
name|equals
argument_list|(
literal|"failover"
argument_list|)
condition|)
block|{
name|availableURIs
operator|=
name|data
operator|.
name|getComponents
argument_list|()
expr_stmt|;
name|failover
operator|=
literal|true
expr_stmt|;
block|}
else|else
block|{
name|availableURIs
operator|=
operator|new
name|URI
index|[]
block|{
name|_uri
block|}
expr_stmt|;
block|}
block|}
comment|/**     * sets the base LDAP dn used for lookup operations     *     * @param _base LDAP base dn     */
specifier|public
name|void
name|setBase
parameter_list|(
name|String
name|_base
parameter_list|)
block|{
name|base
operator|=
name|_base
expr_stmt|;
block|}
comment|/**     * sets the LDAP user for access credentials     *     * @param _user LDAP dn of user     */
specifier|public
name|void
name|setUser
parameter_list|(
name|String
name|_user
parameter_list|)
block|{
name|user
operator|=
name|_user
expr_stmt|;
block|}
comment|/**     * sets the LDAP password for access credentials     *     * @param _password user password     */
specifier|public
name|void
name|setPassword
parameter_list|(
name|String
name|_password
parameter_list|)
block|{
name|password
operator|=
name|_password
expr_stmt|;
block|}
comment|/**     * sets LDAP anonymous authentication access credentials     *     * @param _anonymousAuthentication set to true to use anonymous authentication     */
specifier|public
name|void
name|setAnonymousAuthentication
parameter_list|(
name|boolean
name|_anonymousAuthentication
parameter_list|)
block|{
name|anonymousAuthentication
operator|=
name|_anonymousAuthentication
expr_stmt|;
block|}
comment|/**     * sets the LDAP search scope     *     * @param _searchScope LDAP JNDI search scope     */
specifier|public
name|void
name|setSearchScope
parameter_list|(
name|String
name|_searchScope
parameter_list|)
throws|throws
name|Exception
block|{
name|int
name|scope
decl_stmt|;
if|if
condition|(
name|_searchScope
operator|.
name|equals
argument_list|(
literal|"OBJECT_SCOPE"
argument_list|)
condition|)
block|{
name|scope
operator|=
name|SearchControls
operator|.
name|OBJECT_SCOPE
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|_searchScope
operator|.
name|equals
argument_list|(
literal|"ONELEVEL_SCOPE"
argument_list|)
condition|)
block|{
name|scope
operator|=
name|SearchControls
operator|.
name|ONELEVEL_SCOPE
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|_searchScope
operator|.
name|equals
argument_list|(
literal|"SUBTREE_SCOPE"
argument_list|)
condition|)
block|{
name|scope
operator|=
name|SearchControls
operator|.
name|SUBTREE_SCOPE
expr_stmt|;
block|}
else|else
block|{
throw|throw
operator|new
name|Exception
argument_list|(
literal|"ERR: unknown LDAP search scope specified: "
operator|+
name|_searchScope
argument_list|)
throw|;
block|}
name|searchControls
operator|.
name|setSearchScope
argument_list|(
name|scope
argument_list|)
expr_stmt|;
block|}
comment|/**     * sets the LDAP search filter as defined in RFC 2254     *     * @param _searchFilter LDAP search filter     * @see<a href="http://www.faqs.org/rfcs/rfc2254.html">RFC 2254</a>     */
specifier|public
name|void
name|setSearchFilter
parameter_list|(
name|String
name|_searchFilter
parameter_list|)
block|{
name|searchFilter
operator|=
literal|"(&"
operator|+
name|REQUIRED_OBJECT_CLASS_FILTER
operator|+
literal|"("
operator|+
name|_searchFilter
operator|+
literal|"))"
expr_stmt|;
block|}
comment|/**     * enables/disable a persistent search to the LDAP server as defined     * in draft-ietf-ldapext-psearch-03.txt (2.16.840.1.113730.3.4.3)     *     * @param _searchEventListener enable = true, disable = false (default)     * @see<a href="http://www.ietf.org/proceedings/01mar/I-D/draft-ietf-ldapext-psearch-03.txt">draft-ietf-ldapext-psearch-03.txt</a>     */
specifier|public
name|void
name|setSearchEventListener
parameter_list|(
name|boolean
name|_searchEventListener
parameter_list|)
block|{
name|searchEventListener
operator|=
name|_searchEventListener
expr_stmt|;
block|}
comment|/**     * start the connector     */
specifier|public
name|void
name|start
parameter_list|()
throws|throws
name|Exception
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"connecting..."
argument_list|)
expr_stmt|;
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
literal|"com.sun.jndi.ldap.LdapCtxFactory"
argument_list|)
expr_stmt|;
name|URI
name|uri
init|=
name|getUri
argument_list|()
decl_stmt|;
name|LOG
operator|.
name|debug
argument_list|(
literal|"    URI ["
operator|+
name|uri
operator|+
literal|"]"
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
name|uri
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
if|if
condition|(
name|anonymousAuthentication
condition|)
block|{
name|LOG
operator|.
name|debug
argument_list|(
literal|"    login credentials [anonymous]"
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
literal|"none"
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|LOG
operator|.
name|debug
argument_list|(
literal|"    login credentials ["
operator|+
name|user
operator|+
literal|":******]"
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
name|user
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
name|password
argument_list|)
expr_stmt|;
block|}
name|boolean
name|isConnected
init|=
literal|false
decl_stmt|;
while|while
condition|(
operator|!
name|isConnected
condition|)
block|{
try|try
block|{
name|context
operator|=
operator|new
name|InitialDirContext
argument_list|(
name|env
argument_list|)
expr_stmt|;
name|isConnected
operator|=
literal|true
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|CommunicationException
name|err
parameter_list|)
block|{
if|if
condition|(
name|failover
condition|)
block|{
name|uri
operator|=
name|getUri
argument_list|()
expr_stmt|;
name|LOG
operator|.
name|error
argument_list|(
literal|"connection error ["
operator|+
name|env
operator|.
name|get
argument_list|(
name|Context
operator|.
name|PROVIDER_URL
argument_list|)
operator|+
literal|"], failover connection to ["
operator|+
name|uri
operator|.
name|toString
argument_list|()
operator|+
literal|"]"
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
name|uri
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|Thread
operator|.
name|sleep
argument_list|(
name|curReconnectDelay
argument_list|)
expr_stmt|;
name|curReconnectDelay
operator|=
name|Math
operator|.
name|min
argument_list|(
name|curReconnectDelay
operator|*
literal|2
argument_list|,
name|maxReconnectDelay
argument_list|)
expr_stmt|;
block|}
else|else
block|{
throw|throw
name|err
throw|;
block|}
block|}
block|}
comment|// add connectors from search results
name|LOG
operator|.
name|info
argument_list|(
literal|"searching for network connectors..."
argument_list|)
expr_stmt|;
name|LOG
operator|.
name|debug
argument_list|(
literal|"    base   ["
operator|+
name|base
operator|+
literal|"]"
argument_list|)
expr_stmt|;
name|LOG
operator|.
name|debug
argument_list|(
literal|"    filter ["
operator|+
name|searchFilter
operator|+
literal|"]"
argument_list|)
expr_stmt|;
name|LOG
operator|.
name|debug
argument_list|(
literal|"    scope  ["
operator|+
name|searchControls
operator|.
name|getSearchScope
argument_list|()
operator|+
literal|"]"
argument_list|)
expr_stmt|;
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
name|base
argument_list|,
name|searchFilter
argument_list|,
name|searchControls
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
name|addConnector
argument_list|(
name|results
operator|.
name|next
argument_list|()
argument_list|)
expr_stmt|;
block|}
comment|// register persistent search event listener
if|if
condition|(
name|searchEventListener
condition|)
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"registering persistent search listener..."
argument_list|)
expr_stmt|;
name|EventDirContext
name|eventContext
init|=
operator|(
name|EventDirContext
operator|)
name|context
operator|.
name|lookup
argument_list|(
literal|""
argument_list|)
decl_stmt|;
name|eventContext
operator|.
name|addNamingListener
argument_list|(
name|base
argument_list|,
name|searchFilter
argument_list|,
name|searchControls
argument_list|,
name|this
argument_list|)
expr_stmt|;
block|}
else|else
comment|// otherwise close context (i.e. connection as it is no longer needed)
block|{
name|context
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
comment|/**     * stop the connector     */
specifier|public
name|void
name|stop
parameter_list|()
throws|throws
name|Exception
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"stopping context..."
argument_list|)
expr_stmt|;
for|for
control|(
name|NetworkConnector
name|connector
range|:
name|connectorMap
operator|.
name|values
argument_list|()
control|)
block|{
name|connector
operator|.
name|stop
argument_list|()
expr_stmt|;
block|}
name|connectorMap
operator|.
name|clear
argument_list|()
expr_stmt|;
name|referenceMap
operator|.
name|clear
argument_list|()
expr_stmt|;
name|uuidMap
operator|.
name|clear
argument_list|()
expr_stmt|;
name|context
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
comment|/**     * returns the name of the connector     *     * @return connector name     */
specifier|public
name|String
name|getName
parameter_list|()
block|{
return|return
name|toString
argument_list|()
return|;
block|}
comment|/**     * add connector of the given URI     *     * @param result search result of connector to add     */
specifier|protected
specifier|synchronized
name|void
name|addConnector
parameter_list|(
name|SearchResult
name|result
parameter_list|)
throws|throws
name|Exception
block|{
name|String
name|uuid
init|=
name|toUUID
argument_list|(
name|result
argument_list|)
decl_stmt|;
if|if
condition|(
name|uuidMap
operator|.
name|containsKey
argument_list|(
name|uuid
argument_list|)
condition|)
block|{
name|LOG
operator|.
name|warn
argument_list|(
literal|"connector already regsitered for UUID ["
operator|+
name|uuid
operator|+
literal|"]"
argument_list|)
expr_stmt|;
return|return;
block|}
name|URI
name|connectorURI
init|=
name|toURI
argument_list|(
name|result
argument_list|)
decl_stmt|;
if|if
condition|(
name|connectorMap
operator|.
name|containsKey
argument_list|(
name|connectorURI
argument_list|)
condition|)
block|{
name|int
name|referenceCount
init|=
name|referenceMap
operator|.
name|get
argument_list|(
name|connectorURI
argument_list|)
operator|+
literal|1
decl_stmt|;
name|LOG
operator|.
name|warn
argument_list|(
literal|"connector reference added for URI ["
operator|+
name|connectorURI
operator|+
literal|"], UUID ["
operator|+
name|uuid
operator|+
literal|"], total reference(s) ["
operator|+
name|referenceCount
operator|+
literal|"]"
argument_list|)
expr_stmt|;
name|referenceMap
operator|.
name|put
argument_list|(
name|connectorURI
argument_list|,
name|referenceCount
argument_list|)
expr_stmt|;
name|uuidMap
operator|.
name|put
argument_list|(
name|uuid
argument_list|,
name|connectorURI
argument_list|)
expr_stmt|;
return|return;
block|}
comment|// FIXME: disable JMX listing of LDAP managed connectors, we will
comment|//       want to map/manage these differently in the future
comment|//      boolean useJMX = getBrokerService().isUseJmx();
comment|//      getBrokerService().setUseJmx(false);
name|NetworkConnector
name|connector
init|=
name|getBrokerService
argument_list|()
operator|.
name|addNetworkConnector
argument_list|(
name|connectorURI
argument_list|)
decl_stmt|;
comment|//      getBrokerService().setUseJmx(useJMX);
comment|// propogate std connector properties that may have been set via XML
name|connector
operator|.
name|setDynamicOnly
argument_list|(
name|isDynamicOnly
argument_list|()
argument_list|)
expr_stmt|;
name|connector
operator|.
name|setDecreaseNetworkConsumerPriority
argument_list|(
name|isDecreaseNetworkConsumerPriority
argument_list|()
argument_list|)
expr_stmt|;
name|connector
operator|.
name|setNetworkTTL
argument_list|(
name|getNetworkTTL
argument_list|()
argument_list|)
expr_stmt|;
name|connector
operator|.
name|setConduitSubscriptions
argument_list|(
name|isConduitSubscriptions
argument_list|()
argument_list|)
expr_stmt|;
name|connector
operator|.
name|setExcludedDestinations
argument_list|(
name|getExcludedDestinations
argument_list|()
argument_list|)
expr_stmt|;
name|connector
operator|.
name|setDynamicallyIncludedDestinations
argument_list|(
name|getDynamicallyIncludedDestinations
argument_list|()
argument_list|)
expr_stmt|;
name|connector
operator|.
name|setDuplex
argument_list|(
name|isDuplex
argument_list|()
argument_list|)
expr_stmt|;
comment|// XXX: set in the BrokerService.startAllConnectors method and is
comment|//      required to prevent remote broker exceptions upon connection
name|connector
operator|.
name|setLocalUri
argument_list|(
name|getBrokerService
argument_list|()
operator|.
name|getVmConnectorURI
argument_list|()
argument_list|)
expr_stmt|;
name|connector
operator|.
name|setBrokerName
argument_list|(
name|getBrokerService
argument_list|()
operator|.
name|getBrokerName
argument_list|()
argument_list|)
expr_stmt|;
name|connector
operator|.
name|setDurableDestinations
argument_list|(
name|getBrokerService
argument_list|()
operator|.
name|getBroker
argument_list|()
operator|.
name|getDurableDestinations
argument_list|()
argument_list|)
expr_stmt|;
comment|// start network connector
name|connectorMap
operator|.
name|put
argument_list|(
name|connectorURI
argument_list|,
name|connector
argument_list|)
expr_stmt|;
name|referenceMap
operator|.
name|put
argument_list|(
name|connectorURI
argument_list|,
literal|1
argument_list|)
expr_stmt|;
name|uuidMap
operator|.
name|put
argument_list|(
name|uuid
argument_list|,
name|connectorURI
argument_list|)
expr_stmt|;
name|connector
operator|.
name|start
argument_list|()
expr_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"connector added with URI ["
operator|+
name|connectorURI
operator|+
literal|"]"
argument_list|)
expr_stmt|;
block|}
comment|/**     * remove connector of the given URI     *     * @param result search result of connector to remove     */
specifier|protected
specifier|synchronized
name|void
name|removeConnector
parameter_list|(
name|SearchResult
name|result
parameter_list|)
throws|throws
name|Exception
block|{
name|String
name|uuid
init|=
name|toUUID
argument_list|(
name|result
argument_list|)
decl_stmt|;
if|if
condition|(
operator|!
name|uuidMap
operator|.
name|containsKey
argument_list|(
name|uuid
argument_list|)
condition|)
block|{
name|LOG
operator|.
name|warn
argument_list|(
literal|"connector not regsitered for UUID ["
operator|+
name|uuid
operator|+
literal|"]"
argument_list|)
expr_stmt|;
return|return;
block|}
name|URI
name|connectorURI
init|=
name|uuidMap
operator|.
name|get
argument_list|(
name|uuid
argument_list|)
decl_stmt|;
if|if
condition|(
operator|!
name|connectorMap
operator|.
name|containsKey
argument_list|(
name|connectorURI
argument_list|)
condition|)
block|{
name|LOG
operator|.
name|warn
argument_list|(
literal|"connector not regisitered for URI ["
operator|+
name|connectorURI
operator|+
literal|"]"
argument_list|)
expr_stmt|;
return|return;
block|}
name|int
name|referenceCount
init|=
name|referenceMap
operator|.
name|get
argument_list|(
name|connectorURI
argument_list|)
operator|-
literal|1
decl_stmt|;
name|referenceMap
operator|.
name|put
argument_list|(
name|connectorURI
argument_list|,
name|referenceCount
argument_list|)
expr_stmt|;
name|uuidMap
operator|.
name|remove
argument_list|(
name|uuid
argument_list|)
expr_stmt|;
name|LOG
operator|.
name|debug
argument_list|(
literal|"connector referenced removed for URI ["
operator|+
name|connectorURI
operator|+
literal|"], UUID ["
operator|+
name|uuid
operator|+
literal|"], remaining reference(s) ["
operator|+
name|referenceCount
operator|+
literal|"]"
argument_list|)
expr_stmt|;
if|if
condition|(
name|referenceCount
operator|>
literal|0
condition|)
block|{
return|return;
block|}
name|NetworkConnector
name|connector
init|=
name|connectorMap
operator|.
name|remove
argument_list|(
name|connectorURI
argument_list|)
decl_stmt|;
name|connector
operator|.
name|stop
argument_list|()
expr_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"connector removed with URI ["
operator|+
name|connectorURI
operator|+
literal|"]"
argument_list|)
expr_stmt|;
block|}
comment|/**     * convert search result into URI     *     * @param result search result to convert to URI     */
specifier|protected
name|URI
name|toURI
parameter_list|(
name|SearchResult
name|result
parameter_list|)
throws|throws
name|Exception
block|{
name|Attributes
name|attributes
init|=
name|result
operator|.
name|getAttributes
argument_list|()
decl_stmt|;
name|String
name|address
init|=
operator|(
name|String
operator|)
name|attributes
operator|.
name|get
argument_list|(
literal|"iphostnumber"
argument_list|)
operator|.
name|get
argument_list|()
decl_stmt|;
name|String
name|port
init|=
operator|(
name|String
operator|)
name|attributes
operator|.
name|get
argument_list|(
literal|"ipserviceport"
argument_list|)
operator|.
name|get
argument_list|()
decl_stmt|;
name|String
name|protocol
init|=
operator|(
name|String
operator|)
name|attributes
operator|.
name|get
argument_list|(
literal|"ipserviceprotocol"
argument_list|)
operator|.
name|get
argument_list|()
decl_stmt|;
name|URI
name|connectorURI
init|=
operator|new
name|URI
argument_list|(
literal|"static:("
operator|+
name|protocol
operator|+
literal|"://"
operator|+
name|address
operator|+
literal|":"
operator|+
name|port
operator|+
literal|")"
argument_list|)
decl_stmt|;
name|LOG
operator|.
name|debug
argument_list|(
literal|"retrieved URI from SearchResult ["
operator|+
name|connectorURI
operator|+
literal|"]"
argument_list|)
expr_stmt|;
return|return
name|connectorURI
return|;
block|}
comment|/**     * convert search result into URI     *     * @param result search result to convert to URI     */
specifier|protected
name|String
name|toUUID
parameter_list|(
name|SearchResult
name|result
parameter_list|)
block|{
name|String
name|uuid
init|=
name|result
operator|.
name|getNameInNamespace
argument_list|()
decl_stmt|;
name|LOG
operator|.
name|debug
argument_list|(
literal|"retrieved UUID from SearchResult ["
operator|+
name|uuid
operator|+
literal|"]"
argument_list|)
expr_stmt|;
return|return
name|uuid
return|;
block|}
comment|/**     * invoked when an entry has been added during a persistent search     */
specifier|public
name|void
name|objectAdded
parameter_list|(
name|NamingEvent
name|event
parameter_list|)
block|{
name|LOG
operator|.
name|debug
argument_list|(
literal|"entry added"
argument_list|)
expr_stmt|;
try|try
block|{
name|addConnector
argument_list|(
operator|(
name|SearchResult
operator|)
name|event
operator|.
name|getNewBinding
argument_list|()
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|err
parameter_list|)
block|{
name|LOG
operator|.
name|error
argument_list|(
literal|"ERR: caught unexpected exception"
argument_list|,
name|err
argument_list|)
expr_stmt|;
block|}
block|}
comment|/**     * invoked when an entry has been removed during a persistent search     */
specifier|public
name|void
name|objectRemoved
parameter_list|(
name|NamingEvent
name|event
parameter_list|)
block|{
name|LOG
operator|.
name|debug
argument_list|(
literal|"entry removed"
argument_list|)
expr_stmt|;
try|try
block|{
name|removeConnector
argument_list|(
operator|(
name|SearchResult
operator|)
name|event
operator|.
name|getOldBinding
argument_list|()
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|err
parameter_list|)
block|{
name|LOG
operator|.
name|error
argument_list|(
literal|"ERR: caught unexpected exception"
argument_list|,
name|err
argument_list|)
expr_stmt|;
block|}
block|}
comment|/**     * invoked when an entry has been renamed during a persistent search     */
specifier|public
name|void
name|objectRenamed
parameter_list|(
name|NamingEvent
name|event
parameter_list|)
block|{
name|LOG
operator|.
name|debug
argument_list|(
literal|"entry renamed"
argument_list|)
expr_stmt|;
comment|// XXX: getNameInNamespace method does not seem to work properly,
comment|//      but getName seems to provide the result we want
name|String
name|uuidOld
init|=
name|event
operator|.
name|getOldBinding
argument_list|()
operator|.
name|getName
argument_list|()
decl_stmt|;
name|String
name|uuidNew
init|=
name|event
operator|.
name|getNewBinding
argument_list|()
operator|.
name|getName
argument_list|()
decl_stmt|;
name|URI
name|connectorURI
init|=
name|uuidMap
operator|.
name|remove
argument_list|(
name|uuidOld
argument_list|)
decl_stmt|;
name|uuidMap
operator|.
name|put
argument_list|(
name|uuidNew
argument_list|,
name|connectorURI
argument_list|)
expr_stmt|;
name|LOG
operator|.
name|debug
argument_list|(
literal|"connector reference renamed for URI ["
operator|+
name|connectorURI
operator|+
literal|"], Old UUID ["
operator|+
name|uuidOld
operator|+
literal|"], New UUID ["
operator|+
name|uuidNew
operator|+
literal|"]"
argument_list|)
expr_stmt|;
block|}
comment|/**     * invoked when an entry has been changed during a persistent search     */
specifier|public
name|void
name|objectChanged
parameter_list|(
name|NamingEvent
name|event
parameter_list|)
block|{
name|LOG
operator|.
name|debug
argument_list|(
literal|"entry changed"
argument_list|)
expr_stmt|;
try|try
block|{
name|SearchResult
name|result
init|=
operator|(
name|SearchResult
operator|)
name|event
operator|.
name|getNewBinding
argument_list|()
decl_stmt|;
name|removeConnector
argument_list|(
name|result
argument_list|)
expr_stmt|;
name|addConnector
argument_list|(
name|result
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|err
parameter_list|)
block|{
name|LOG
operator|.
name|error
argument_list|(
literal|"ERR: caught unexpected exception"
argument_list|,
name|err
argument_list|)
expr_stmt|;
block|}
block|}
comment|/**     * invoked when an exception has occurred during a persistent search     */
specifier|public
name|void
name|namingExceptionThrown
parameter_list|(
name|NamingExceptionEvent
name|event
parameter_list|)
block|{
name|LOG
operator|.
name|error
argument_list|(
literal|"ERR: caught unexpected exception"
argument_list|,
name|event
operator|.
name|getException
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

