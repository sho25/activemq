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
name|Iterator
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
name|naming
operator|.
name|ldap
operator|.
name|LdapName
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
name|Rdn
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
name|advisory
operator|.
name|AdvisorySupport
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
name|ActiveMQDestination
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
name|filter
operator|.
name|DestinationMap
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
name|LDAPLoginModule
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
comment|/**  * An {@link AuthorizationMap} which uses LDAP  *  * @org.apache.xbean.XBean  * @author ngcutura  */
end_comment

begin_class
specifier|public
class|class
name|LDAPAuthorizationMap
implements|implements
name|AuthorizationMap
block|{
specifier|public
specifier|static
specifier|final
name|String
name|INITIAL_CONTEXT_FACTORY
init|=
literal|"initialContextFactory"
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|String
name|CONNECTION_URL
init|=
literal|"connectionURL"
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|String
name|CONNECTION_USERNAME
init|=
literal|"connectionUsername"
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|String
name|CONNECTION_PASSWORD
init|=
literal|"connectionPassword"
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|String
name|CONNECTION_PROTOCOL
init|=
literal|"connectionProtocol"
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|String
name|AUTHENTICATION
init|=
literal|"authentication"
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|String
name|TOPIC_SEARCH_MATCHING
init|=
literal|"topicSearchMatching"
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|String
name|TOPIC_SEARCH_SUBTREE
init|=
literal|"topicSearchSubtree"
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|String
name|QUEUE_SEARCH_MATCHING
init|=
literal|"queueSearchMatching"
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|String
name|QUEUE_SEARCH_SUBTREE
init|=
literal|"queueSearchSubtree"
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|String
name|ADMIN_BASE
init|=
literal|"adminBase"
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|String
name|ADMIN_ATTRIBUTE
init|=
literal|"adminAttribute"
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|String
name|READ_BASE
init|=
literal|"readBase"
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|String
name|READ_ATTRIBUTE
init|=
literal|"readAttribute"
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|String
name|WRITE_BASE
init|=
literal|"writeBAse"
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|String
name|WRITE_ATTRIBUTE
init|=
literal|"writeAttribute"
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
name|LDAPLoginModule
operator|.
name|class
argument_list|)
decl_stmt|;
specifier|private
name|String
name|initialContextFactory
decl_stmt|;
specifier|private
name|String
name|connectionURL
decl_stmt|;
specifier|private
name|String
name|connectionUsername
decl_stmt|;
specifier|private
name|String
name|connectionPassword
decl_stmt|;
specifier|private
name|String
name|connectionProtocol
decl_stmt|;
specifier|private
name|String
name|authentication
decl_stmt|;
specifier|private
name|DirContext
name|context
decl_stmt|;
specifier|private
name|MessageFormat
name|topicSearchMatchingFormat
decl_stmt|;
specifier|private
name|MessageFormat
name|queueSearchMatchingFormat
decl_stmt|;
specifier|private
name|String
name|advisorySearchBase
init|=
literal|"uid=ActiveMQ.Advisory,ou=topics,ou=destinations,o=ActiveMQ,dc=example,dc=com"
decl_stmt|;
specifier|private
name|String
name|tempSearchBase
init|=
literal|"uid=ActiveMQ.Temp,ou=topics,ou=destinations,o=ActiveMQ,dc=example,dc=com"
decl_stmt|;
specifier|private
name|boolean
name|topicSearchSubtreeBool
init|=
literal|true
decl_stmt|;
specifier|private
name|boolean
name|queueSearchSubtreeBool
init|=
literal|true
decl_stmt|;
specifier|private
name|boolean
name|useAdvisorySearchBase
init|=
literal|true
decl_stmt|;
specifier|private
name|String
name|adminBase
decl_stmt|;
specifier|private
name|String
name|adminAttribute
decl_stmt|;
specifier|private
name|String
name|readBase
decl_stmt|;
specifier|private
name|String
name|readAttribute
decl_stmt|;
specifier|private
name|String
name|writeBase
decl_stmt|;
specifier|private
name|String
name|writeAttribute
decl_stmt|;
specifier|public
name|LDAPAuthorizationMap
parameter_list|()
block|{
comment|// lets setup some sensible defaults
name|initialContextFactory
operator|=
literal|"com.sun.jndi.ldap.LdapCtxFactory"
expr_stmt|;
name|connectionURL
operator|=
literal|"ldap://localhost:10389"
expr_stmt|;
name|connectionUsername
operator|=
literal|"uid=admin,ou=system"
expr_stmt|;
name|connectionProtocol
operator|=
literal|"s"
expr_stmt|;
name|authentication
operator|=
literal|"simple"
expr_stmt|;
name|topicSearchMatchingFormat
operator|=
operator|new
name|MessageFormat
argument_list|(
literal|"uid={0},ou=topics,ou=destinations,o=ActiveMQ,dc=example,dc=com"
argument_list|)
expr_stmt|;
name|queueSearchMatchingFormat
operator|=
operator|new
name|MessageFormat
argument_list|(
literal|"uid={0},ou=queues,ou=destinations,o=ActiveMQ,dc=example,dc=com"
argument_list|)
expr_stmt|;
name|adminBase
operator|=
literal|"(cn=admin)"
expr_stmt|;
name|adminAttribute
operator|=
literal|"uniqueMember"
expr_stmt|;
name|readBase
operator|=
literal|"(cn=read)"
expr_stmt|;
name|readAttribute
operator|=
literal|"uniqueMember"
expr_stmt|;
name|writeBase
operator|=
literal|"(cn=write)"
expr_stmt|;
name|writeAttribute
operator|=
literal|"uniqueMember"
expr_stmt|;
block|}
specifier|public
name|LDAPAuthorizationMap
parameter_list|(
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|options
parameter_list|)
block|{
name|initialContextFactory
operator|=
name|options
operator|.
name|get
argument_list|(
name|INITIAL_CONTEXT_FACTORY
argument_list|)
expr_stmt|;
name|connectionURL
operator|=
name|options
operator|.
name|get
argument_list|(
name|CONNECTION_URL
argument_list|)
expr_stmt|;
name|connectionUsername
operator|=
name|options
operator|.
name|get
argument_list|(
name|CONNECTION_USERNAME
argument_list|)
expr_stmt|;
name|connectionPassword
operator|=
name|options
operator|.
name|get
argument_list|(
name|CONNECTION_PASSWORD
argument_list|)
expr_stmt|;
name|connectionProtocol
operator|=
name|options
operator|.
name|get
argument_list|(
name|CONNECTION_PROTOCOL
argument_list|)
expr_stmt|;
name|authentication
operator|=
name|options
operator|.
name|get
argument_list|(
name|AUTHENTICATION
argument_list|)
expr_stmt|;
name|adminBase
operator|=
name|options
operator|.
name|get
argument_list|(
name|ADMIN_BASE
argument_list|)
expr_stmt|;
name|adminAttribute
operator|=
name|options
operator|.
name|get
argument_list|(
name|ADMIN_ATTRIBUTE
argument_list|)
expr_stmt|;
name|readBase
operator|=
name|options
operator|.
name|get
argument_list|(
name|READ_BASE
argument_list|)
expr_stmt|;
name|readAttribute
operator|=
name|options
operator|.
name|get
argument_list|(
name|READ_ATTRIBUTE
argument_list|)
expr_stmt|;
name|writeBase
operator|=
name|options
operator|.
name|get
argument_list|(
name|WRITE_BASE
argument_list|)
expr_stmt|;
name|writeAttribute
operator|=
name|options
operator|.
name|get
argument_list|(
name|WRITE_ATTRIBUTE
argument_list|)
expr_stmt|;
name|String
name|topicSearchMatching
init|=
name|options
operator|.
name|get
argument_list|(
name|TOPIC_SEARCH_MATCHING
argument_list|)
decl_stmt|;
name|String
name|topicSearchSubtree
init|=
name|options
operator|.
name|get
argument_list|(
name|TOPIC_SEARCH_SUBTREE
argument_list|)
decl_stmt|;
name|String
name|queueSearchMatching
init|=
name|options
operator|.
name|get
argument_list|(
name|QUEUE_SEARCH_MATCHING
argument_list|)
decl_stmt|;
name|String
name|queueSearchSubtree
init|=
name|options
operator|.
name|get
argument_list|(
name|QUEUE_SEARCH_SUBTREE
argument_list|)
decl_stmt|;
name|topicSearchMatchingFormat
operator|=
operator|new
name|MessageFormat
argument_list|(
name|topicSearchMatching
argument_list|)
expr_stmt|;
name|queueSearchMatchingFormat
operator|=
operator|new
name|MessageFormat
argument_list|(
name|queueSearchMatching
argument_list|)
expr_stmt|;
name|topicSearchSubtreeBool
operator|=
name|Boolean
operator|.
name|valueOf
argument_list|(
name|topicSearchSubtree
argument_list|)
operator|.
name|booleanValue
argument_list|()
expr_stmt|;
name|queueSearchSubtreeBool
operator|=
name|Boolean
operator|.
name|valueOf
argument_list|(
name|queueSearchSubtree
argument_list|)
operator|.
name|booleanValue
argument_list|()
expr_stmt|;
block|}
specifier|public
name|Set
argument_list|<
name|GroupPrincipal
argument_list|>
name|getTempDestinationAdminACLs
parameter_list|()
block|{
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
name|e
parameter_list|)
block|{
name|LOG
operator|.
name|error
argument_list|(
name|e
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
return|return
operator|new
name|HashSet
argument_list|<
name|GroupPrincipal
argument_list|>
argument_list|()
return|;
block|}
name|SearchControls
name|constraints
init|=
operator|new
name|SearchControls
argument_list|()
decl_stmt|;
name|constraints
operator|.
name|setReturningAttributes
argument_list|(
operator|new
name|String
index|[]
block|{
name|adminAttribute
block|}
argument_list|)
expr_stmt|;
return|return
name|getACLs
argument_list|(
name|tempSearchBase
argument_list|,
name|constraints
argument_list|,
name|adminBase
argument_list|,
name|adminAttribute
argument_list|)
return|;
block|}
specifier|public
name|Set
argument_list|<
name|GroupPrincipal
argument_list|>
name|getTempDestinationReadACLs
parameter_list|()
block|{
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
name|e
parameter_list|)
block|{
name|LOG
operator|.
name|error
argument_list|(
name|e
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
return|return
operator|new
name|HashSet
argument_list|<
name|GroupPrincipal
argument_list|>
argument_list|()
return|;
block|}
name|SearchControls
name|constraints
init|=
operator|new
name|SearchControls
argument_list|()
decl_stmt|;
name|constraints
operator|.
name|setReturningAttributes
argument_list|(
operator|new
name|String
index|[]
block|{
name|readAttribute
block|}
argument_list|)
expr_stmt|;
return|return
name|getACLs
argument_list|(
name|tempSearchBase
argument_list|,
name|constraints
argument_list|,
name|readBase
argument_list|,
name|readAttribute
argument_list|)
return|;
block|}
specifier|public
name|Set
argument_list|<
name|GroupPrincipal
argument_list|>
name|getTempDestinationWriteACLs
parameter_list|()
block|{
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
name|e
parameter_list|)
block|{
name|LOG
operator|.
name|error
argument_list|(
name|e
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
return|return
operator|new
name|HashSet
argument_list|<
name|GroupPrincipal
argument_list|>
argument_list|()
return|;
block|}
name|SearchControls
name|constraints
init|=
operator|new
name|SearchControls
argument_list|()
decl_stmt|;
name|constraints
operator|.
name|setReturningAttributes
argument_list|(
operator|new
name|String
index|[]
block|{
name|writeAttribute
block|}
argument_list|)
expr_stmt|;
return|return
name|getACLs
argument_list|(
name|tempSearchBase
argument_list|,
name|constraints
argument_list|,
name|writeBase
argument_list|,
name|writeAttribute
argument_list|)
return|;
block|}
specifier|public
name|Set
argument_list|<
name|GroupPrincipal
argument_list|>
name|getAdminACLs
parameter_list|(
name|ActiveMQDestination
name|destination
parameter_list|)
block|{
if|if
condition|(
name|destination
operator|.
name|isComposite
argument_list|()
condition|)
block|{
return|return
name|getCompositeACLs
argument_list|(
name|destination
argument_list|,
name|adminBase
argument_list|,
name|adminAttribute
argument_list|)
return|;
block|}
return|return
name|getACLs
argument_list|(
name|destination
argument_list|,
name|adminBase
argument_list|,
name|adminAttribute
argument_list|)
return|;
block|}
specifier|public
name|Set
argument_list|<
name|GroupPrincipal
argument_list|>
name|getReadACLs
parameter_list|(
name|ActiveMQDestination
name|destination
parameter_list|)
block|{
if|if
condition|(
name|destination
operator|.
name|isComposite
argument_list|()
condition|)
block|{
return|return
name|getCompositeACLs
argument_list|(
name|destination
argument_list|,
name|readBase
argument_list|,
name|readAttribute
argument_list|)
return|;
block|}
return|return
name|getACLs
argument_list|(
name|destination
argument_list|,
name|readBase
argument_list|,
name|readAttribute
argument_list|)
return|;
block|}
specifier|public
name|Set
argument_list|<
name|GroupPrincipal
argument_list|>
name|getWriteACLs
parameter_list|(
name|ActiveMQDestination
name|destination
parameter_list|)
block|{
if|if
condition|(
name|destination
operator|.
name|isComposite
argument_list|()
condition|)
block|{
return|return
name|getCompositeACLs
argument_list|(
name|destination
argument_list|,
name|writeBase
argument_list|,
name|writeAttribute
argument_list|)
return|;
block|}
return|return
name|getACLs
argument_list|(
name|destination
argument_list|,
name|writeBase
argument_list|,
name|writeAttribute
argument_list|)
return|;
block|}
comment|// Properties
comment|// -------------------------------------------------------------------------
specifier|public
name|String
name|getAdminAttribute
parameter_list|()
block|{
return|return
name|adminAttribute
return|;
block|}
specifier|public
name|void
name|setAdminAttribute
parameter_list|(
name|String
name|adminAttribute
parameter_list|)
block|{
name|this
operator|.
name|adminAttribute
operator|=
name|adminAttribute
expr_stmt|;
block|}
specifier|public
name|String
name|getAdminBase
parameter_list|()
block|{
return|return
name|adminBase
return|;
block|}
specifier|public
name|void
name|setAdminBase
parameter_list|(
name|String
name|adminBase
parameter_list|)
block|{
name|this
operator|.
name|adminBase
operator|=
name|adminBase
expr_stmt|;
block|}
specifier|public
name|String
name|getAuthentication
parameter_list|()
block|{
return|return
name|authentication
return|;
block|}
specifier|public
name|void
name|setAuthentication
parameter_list|(
name|String
name|authentication
parameter_list|)
block|{
name|this
operator|.
name|authentication
operator|=
name|authentication
expr_stmt|;
block|}
specifier|public
name|String
name|getConnectionPassword
parameter_list|()
block|{
return|return
name|connectionPassword
return|;
block|}
specifier|public
name|void
name|setConnectionPassword
parameter_list|(
name|String
name|connectionPassword
parameter_list|)
block|{
name|this
operator|.
name|connectionPassword
operator|=
name|connectionPassword
expr_stmt|;
block|}
specifier|public
name|String
name|getConnectionProtocol
parameter_list|()
block|{
return|return
name|connectionProtocol
return|;
block|}
specifier|public
name|void
name|setConnectionProtocol
parameter_list|(
name|String
name|connectionProtocol
parameter_list|)
block|{
name|this
operator|.
name|connectionProtocol
operator|=
name|connectionProtocol
expr_stmt|;
block|}
specifier|public
name|String
name|getConnectionURL
parameter_list|()
block|{
return|return
name|connectionURL
return|;
block|}
specifier|public
name|void
name|setConnectionURL
parameter_list|(
name|String
name|connectionURL
parameter_list|)
block|{
name|this
operator|.
name|connectionURL
operator|=
name|connectionURL
expr_stmt|;
block|}
specifier|public
name|String
name|getConnectionUsername
parameter_list|()
block|{
return|return
name|connectionUsername
return|;
block|}
specifier|public
name|void
name|setConnectionUsername
parameter_list|(
name|String
name|connectionUsername
parameter_list|)
block|{
name|this
operator|.
name|connectionUsername
operator|=
name|connectionUsername
expr_stmt|;
block|}
specifier|public
name|DirContext
name|getContext
parameter_list|()
block|{
return|return
name|context
return|;
block|}
specifier|public
name|void
name|setContext
parameter_list|(
name|DirContext
name|context
parameter_list|)
block|{
name|this
operator|.
name|context
operator|=
name|context
expr_stmt|;
block|}
specifier|public
name|String
name|getInitialContextFactory
parameter_list|()
block|{
return|return
name|initialContextFactory
return|;
block|}
specifier|public
name|void
name|setInitialContextFactory
parameter_list|(
name|String
name|initialContextFactory
parameter_list|)
block|{
name|this
operator|.
name|initialContextFactory
operator|=
name|initialContextFactory
expr_stmt|;
block|}
specifier|public
name|MessageFormat
name|getQueueSearchMatchingFormat
parameter_list|()
block|{
return|return
name|queueSearchMatchingFormat
return|;
block|}
specifier|public
name|void
name|setQueueSearchMatchingFormat
parameter_list|(
name|MessageFormat
name|queueSearchMatchingFormat
parameter_list|)
block|{
name|this
operator|.
name|queueSearchMatchingFormat
operator|=
name|queueSearchMatchingFormat
expr_stmt|;
block|}
specifier|public
name|boolean
name|isQueueSearchSubtreeBool
parameter_list|()
block|{
return|return
name|queueSearchSubtreeBool
return|;
block|}
specifier|public
name|void
name|setQueueSearchSubtreeBool
parameter_list|(
name|boolean
name|queueSearchSubtreeBool
parameter_list|)
block|{
name|this
operator|.
name|queueSearchSubtreeBool
operator|=
name|queueSearchSubtreeBool
expr_stmt|;
block|}
specifier|public
name|String
name|getReadAttribute
parameter_list|()
block|{
return|return
name|readAttribute
return|;
block|}
specifier|public
name|void
name|setReadAttribute
parameter_list|(
name|String
name|readAttribute
parameter_list|)
block|{
name|this
operator|.
name|readAttribute
operator|=
name|readAttribute
expr_stmt|;
block|}
specifier|public
name|String
name|getReadBase
parameter_list|()
block|{
return|return
name|readBase
return|;
block|}
specifier|public
name|void
name|setReadBase
parameter_list|(
name|String
name|readBase
parameter_list|)
block|{
name|this
operator|.
name|readBase
operator|=
name|readBase
expr_stmt|;
block|}
specifier|public
name|MessageFormat
name|getTopicSearchMatchingFormat
parameter_list|()
block|{
return|return
name|topicSearchMatchingFormat
return|;
block|}
specifier|public
name|void
name|setTopicSearchMatchingFormat
parameter_list|(
name|MessageFormat
name|topicSearchMatchingFormat
parameter_list|)
block|{
name|this
operator|.
name|topicSearchMatchingFormat
operator|=
name|topicSearchMatchingFormat
expr_stmt|;
block|}
specifier|public
name|boolean
name|isTopicSearchSubtreeBool
parameter_list|()
block|{
return|return
name|topicSearchSubtreeBool
return|;
block|}
specifier|public
name|void
name|setTopicSearchSubtreeBool
parameter_list|(
name|boolean
name|topicSearchSubtreeBool
parameter_list|)
block|{
name|this
operator|.
name|topicSearchSubtreeBool
operator|=
name|topicSearchSubtreeBool
expr_stmt|;
block|}
specifier|public
name|String
name|getWriteAttribute
parameter_list|()
block|{
return|return
name|writeAttribute
return|;
block|}
specifier|public
name|void
name|setWriteAttribute
parameter_list|(
name|String
name|writeAttribute
parameter_list|)
block|{
name|this
operator|.
name|writeAttribute
operator|=
name|writeAttribute
expr_stmt|;
block|}
specifier|public
name|String
name|getWriteBase
parameter_list|()
block|{
return|return
name|writeBase
return|;
block|}
specifier|public
name|void
name|setWriteBase
parameter_list|(
name|String
name|writeBase
parameter_list|)
block|{
name|this
operator|.
name|writeBase
operator|=
name|writeBase
expr_stmt|;
block|}
specifier|public
name|boolean
name|isUseAdvisorySearchBase
parameter_list|()
block|{
return|return
name|useAdvisorySearchBase
return|;
block|}
specifier|public
name|void
name|setUseAdvisorySearchBase
parameter_list|(
name|boolean
name|useAdvisorySearchBase
parameter_list|)
block|{
name|this
operator|.
name|useAdvisorySearchBase
operator|=
name|useAdvisorySearchBase
expr_stmt|;
block|}
specifier|public
name|String
name|getAdvisorySearchBase
parameter_list|()
block|{
return|return
name|advisorySearchBase
return|;
block|}
specifier|public
name|void
name|setAdvisorySearchBase
parameter_list|(
name|String
name|advisorySearchBase
parameter_list|)
block|{
name|this
operator|.
name|advisorySearchBase
operator|=
name|advisorySearchBase
expr_stmt|;
block|}
specifier|public
name|String
name|getTempSearchBase
parameter_list|()
block|{
return|return
name|tempSearchBase
return|;
block|}
specifier|public
name|void
name|setTempSearchBase
parameter_list|(
name|String
name|tempSearchBase
parameter_list|)
block|{
name|this
operator|.
name|tempSearchBase
operator|=
name|tempSearchBase
expr_stmt|;
block|}
specifier|protected
name|Set
argument_list|<
name|GroupPrincipal
argument_list|>
name|getCompositeACLs
parameter_list|(
name|ActiveMQDestination
name|destination
parameter_list|,
name|String
name|roleBase
parameter_list|,
name|String
name|roleAttribute
parameter_list|)
block|{
name|ActiveMQDestination
index|[]
name|dests
init|=
name|destination
operator|.
name|getCompositeDestinations
argument_list|()
decl_stmt|;
name|Set
argument_list|<
name|GroupPrincipal
argument_list|>
name|acls
init|=
literal|null
decl_stmt|;
for|for
control|(
name|ActiveMQDestination
name|dest
range|:
name|dests
control|)
block|{
name|acls
operator|=
name|DestinationMap
operator|.
name|union
argument_list|(
name|acls
argument_list|,
name|getACLs
argument_list|(
name|dest
argument_list|,
name|roleBase
argument_list|,
name|roleAttribute
argument_list|)
argument_list|)
expr_stmt|;
if|if
condition|(
name|acls
operator|==
literal|null
operator|||
name|acls
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
break|break;
block|}
block|}
return|return
name|acls
return|;
block|}
comment|// Implementation methods
comment|// -------------------------------------------------------------------------
specifier|protected
name|Set
argument_list|<
name|GroupPrincipal
argument_list|>
name|getACLs
parameter_list|(
name|ActiveMQDestination
name|destination
parameter_list|,
name|String
name|roleBase
parameter_list|,
name|String
name|roleAttribute
parameter_list|)
block|{
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
name|e
parameter_list|)
block|{
name|LOG
operator|.
name|error
argument_list|(
name|e
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
return|return
operator|new
name|HashSet
argument_list|<
name|GroupPrincipal
argument_list|>
argument_list|()
return|;
block|}
name|String
name|destinationBase
init|=
literal|""
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
name|AdvisorySupport
operator|.
name|isAdvisoryTopic
argument_list|(
name|destination
argument_list|)
operator|&&
name|useAdvisorySearchBase
condition|)
block|{
name|destinationBase
operator|=
name|advisorySearchBase
expr_stmt|;
block|}
else|else
block|{
if|if
condition|(
operator|(
name|destination
operator|.
name|getDestinationType
argument_list|()
operator|&
name|ActiveMQDestination
operator|.
name|QUEUE_TYPE
operator|)
operator|==
name|ActiveMQDestination
operator|.
name|QUEUE_TYPE
condition|)
block|{
name|destinationBase
operator|=
name|queueSearchMatchingFormat
operator|.
name|format
argument_list|(
operator|new
name|String
index|[]
block|{
name|destination
operator|.
name|getPhysicalName
argument_list|()
block|}
argument_list|)
expr_stmt|;
if|if
condition|(
name|queueSearchSubtreeBool
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
block|}
if|if
condition|(
operator|(
name|destination
operator|.
name|getDestinationType
argument_list|()
operator|&
name|ActiveMQDestination
operator|.
name|TOPIC_TYPE
operator|)
operator|==
name|ActiveMQDestination
operator|.
name|TOPIC_TYPE
condition|)
block|{
name|destinationBase
operator|=
name|topicSearchMatchingFormat
operator|.
name|format
argument_list|(
operator|new
name|String
index|[]
block|{
name|destination
operator|.
name|getPhysicalName
argument_list|()
block|}
argument_list|)
expr_stmt|;
if|if
condition|(
name|topicSearchSubtreeBool
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
block|}
block|}
name|constraints
operator|.
name|setReturningAttributes
argument_list|(
operator|new
name|String
index|[]
block|{
name|roleAttribute
block|}
argument_list|)
expr_stmt|;
return|return
name|getACLs
argument_list|(
name|destinationBase
argument_list|,
name|constraints
argument_list|,
name|roleBase
argument_list|,
name|roleAttribute
argument_list|)
return|;
block|}
specifier|protected
name|Set
argument_list|<
name|GroupPrincipal
argument_list|>
name|getACLs
parameter_list|(
name|String
name|destinationBase
parameter_list|,
name|SearchControls
name|constraints
parameter_list|,
name|String
name|roleBase
parameter_list|,
name|String
name|roleAttribute
parameter_list|)
block|{
try|try
block|{
name|Set
argument_list|<
name|GroupPrincipal
argument_list|>
name|roles
init|=
operator|new
name|HashSet
argument_list|<
name|GroupPrincipal
argument_list|>
argument_list|()
decl_stmt|;
name|Set
argument_list|<
name|String
argument_list|>
name|acls
init|=
operator|new
name|HashSet
argument_list|<
name|String
argument_list|>
argument_list|()
decl_stmt|;
name|NamingEnumeration
argument_list|<
name|?
argument_list|>
name|results
init|=
name|context
operator|.
name|search
argument_list|(
name|destinationBase
argument_list|,
name|roleBase
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
operator|(
name|SearchResult
operator|)
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
name|acls
operator|=
name|addAttributeValues
argument_list|(
name|roleAttribute
argument_list|,
name|attrs
argument_list|,
name|acls
argument_list|)
expr_stmt|;
block|}
for|for
control|(
name|Iterator
argument_list|<
name|String
argument_list|>
name|iter
init|=
name|acls
operator|.
name|iterator
argument_list|()
init|;
name|iter
operator|.
name|hasNext
argument_list|()
condition|;
control|)
block|{
name|String
name|roleName
init|=
name|iter
operator|.
name|next
argument_list|()
decl_stmt|;
name|LdapName
name|ldapname
init|=
operator|new
name|LdapName
argument_list|(
name|roleName
argument_list|)
decl_stmt|;
name|Rdn
name|rdn
init|=
name|ldapname
operator|.
name|getRdn
argument_list|(
name|ldapname
operator|.
name|size
argument_list|()
operator|-
literal|1
argument_list|)
decl_stmt|;
name|LOG
operator|.
name|debug
argument_list|(
literal|"Found role: ["
operator|+
name|rdn
operator|.
name|getValue
argument_list|()
operator|.
name|toString
argument_list|()
operator|+
literal|"]"
argument_list|)
expr_stmt|;
name|roles
operator|.
name|add
argument_list|(
operator|new
name|GroupPrincipal
argument_list|(
name|rdn
operator|.
name|getValue
argument_list|()
operator|.
name|toString
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
return|return
name|roles
return|;
block|}
catch|catch
parameter_list|(
name|NamingException
name|e
parameter_list|)
block|{
name|LOG
operator|.
name|error
argument_list|(
name|e
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
return|return
operator|new
name|HashSet
argument_list|<
name|GroupPrincipal
argument_list|>
argument_list|()
return|;
block|}
block|}
specifier|protected
name|Set
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
name|Set
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
name|HashSet
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
if|if
condition|(
name|context
operator|!=
literal|null
condition|)
block|{
return|return
name|context
return|;
block|}
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
name|initialContextFactory
argument_list|)
expr_stmt|;
if|if
condition|(
name|connectionUsername
operator|!=
literal|null
operator|&&
operator|!
literal|""
operator|.
name|equals
argument_list|(
name|connectionUsername
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
name|connectionUsername
argument_list|)
expr_stmt|;
block|}
else|else
block|{
throw|throw
operator|new
name|NamingException
argument_list|(
literal|"Empty username is not allowed"
argument_list|)
throw|;
block|}
if|if
condition|(
name|connectionPassword
operator|!=
literal|null
operator|&&
operator|!
literal|""
operator|.
name|equals
argument_list|(
name|connectionPassword
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
name|connectionPassword
argument_list|)
expr_stmt|;
block|}
else|else
block|{
throw|throw
operator|new
name|NamingException
argument_list|(
literal|"Empty password is not allowed"
argument_list|)
throw|;
block|}
name|env
operator|.
name|put
argument_list|(
name|Context
operator|.
name|SECURITY_PROTOCOL
argument_list|,
name|connectionProtocol
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
name|connectionURL
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
name|authentication
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
name|LOG
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
block|}
end_class

end_unit

