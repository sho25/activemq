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
name|security
package|;
end_package

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|HashMap
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
name|directory
operator|.
name|InitialDirContext
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
name|command
operator|.
name|ActiveMQQueue
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
name|ActiveMQTopic
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
name|directory
operator|.
name|server
operator|.
name|core
operator|.
name|configuration
operator|.
name|StartupConfiguration
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
name|jndi
operator|.
name|CoreContextFactory
import|;
end_import

begin_import
import|import
name|org
operator|.
name|springframework
operator|.
name|context
operator|.
name|ApplicationContext
import|;
end_import

begin_import
import|import
name|org
operator|.
name|springframework
operator|.
name|context
operator|.
name|support
operator|.
name|ClassPathXmlApplicationContext
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
comment|/**  * This test assumes setup like in file 'AMQauth.ldif'. Contents of this file is  * attached below in comments.  *   * @author ngcutura  *   */
end_comment

begin_class
specifier|public
class|class
name|LDAPAuthorizationMapTest
extends|extends
name|TestCase
block|{
specifier|private
name|LDAPAuthorizationMap
name|authMap
decl_stmt|;
specifier|protected
name|void
name|setUp
parameter_list|()
throws|throws
name|Exception
block|{
name|super
operator|.
name|setUp
argument_list|()
expr_stmt|;
name|startLdapServer
argument_list|()
expr_stmt|;
name|authMap
operator|=
operator|new
name|LDAPAuthorizationMap
argument_list|()
expr_stmt|;
block|}
specifier|protected
name|void
name|startLdapServer
parameter_list|()
throws|throws
name|Exception
block|{
name|ApplicationContext
name|factory
init|=
operator|new
name|ClassPathXmlApplicationContext
argument_list|(
literal|"org/apache/activemq/security/ldap-spring.xml"
argument_list|)
decl_stmt|;
name|StartupConfiguration
name|cfg
init|=
operator|(
name|StartupConfiguration
operator|)
name|factory
operator|.
name|getBean
argument_list|(
literal|"configuration"
argument_list|)
decl_stmt|;
name|Properties
name|env
init|=
operator|(
name|Properties
operator|)
name|factory
operator|.
name|getBean
argument_list|(
literal|"environment"
argument_list|)
decl_stmt|;
name|env
operator|.
name|setProperty
argument_list|(
name|Context
operator|.
name|PROVIDER_URL
argument_list|,
literal|""
argument_list|)
expr_stmt|;
name|env
operator|.
name|setProperty
argument_list|(
name|Context
operator|.
name|INITIAL_CONTEXT_FACTORY
argument_list|,
name|CoreContextFactory
operator|.
name|class
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
name|env
operator|.
name|putAll
argument_list|(
name|cfg
operator|.
name|toJndiEnvironment
argument_list|()
argument_list|)
expr_stmt|;
operator|new
name|InitialDirContext
argument_list|(
name|env
argument_list|)
expr_stmt|;
block|}
specifier|protected
name|void
name|tearDown
parameter_list|()
throws|throws
name|Exception
block|{
name|super
operator|.
name|tearDown
argument_list|()
expr_stmt|;
block|}
specifier|public
name|void
name|testOpen
parameter_list|()
throws|throws
name|Exception
block|{
name|DirContext
name|ctx
init|=
name|authMap
operator|.
name|open
argument_list|()
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
literal|"ou=destinations,o=ActiveMQ,dc=example,dc=com"
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
literal|"ou=topics"
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|set
operator|.
name|contains
argument_list|(
literal|"ou=queues"
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|/*      * Test method for      * 'org.apache.activemq.security.LDAPAuthorizationMap.getAdminACLs(ActiveMQDestination)'      */
specifier|public
name|void
name|testGetAdminACLs
parameter_list|()
block|{
name|ActiveMQDestination
name|q1
init|=
operator|new
name|ActiveMQQueue
argument_list|(
literal|"queue1"
argument_list|)
decl_stmt|;
name|Set
name|aclsq1
init|=
name|authMap
operator|.
name|getAdminACLs
argument_list|(
name|q1
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|aclsq1
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|aclsq1
operator|.
name|contains
argument_list|(
operator|new
name|GroupPrincipal
argument_list|(
literal|"role1"
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|ActiveMQDestination
name|t1
init|=
operator|new
name|ActiveMQTopic
argument_list|(
literal|"topic1"
argument_list|)
decl_stmt|;
name|Set
name|aclst1
init|=
name|authMap
operator|.
name|getAdminACLs
argument_list|(
name|t1
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|aclst1
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|aclst1
operator|.
name|contains
argument_list|(
operator|new
name|GroupPrincipal
argument_list|(
literal|"role1"
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|/*      * Test method for      * 'org.apache.activemq.security.LDAPAuthorizationMap.getReadACLs(ActiveMQDestination)'      */
specifier|public
name|void
name|testGetReadACLs
parameter_list|()
block|{
name|ActiveMQDestination
name|q1
init|=
operator|new
name|ActiveMQQueue
argument_list|(
literal|"queue1"
argument_list|)
decl_stmt|;
name|Set
name|aclsq1
init|=
name|authMap
operator|.
name|getReadACLs
argument_list|(
name|q1
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|aclsq1
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|aclsq1
operator|.
name|contains
argument_list|(
operator|new
name|GroupPrincipal
argument_list|(
literal|"role1"
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|ActiveMQDestination
name|t1
init|=
operator|new
name|ActiveMQTopic
argument_list|(
literal|"topic1"
argument_list|)
decl_stmt|;
name|Set
name|aclst1
init|=
name|authMap
operator|.
name|getReadACLs
argument_list|(
name|t1
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|aclst1
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|aclst1
operator|.
name|contains
argument_list|(
operator|new
name|GroupPrincipal
argument_list|(
literal|"role2"
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|/*      * Test method for      * 'org.apache.activemq.security.LDAPAuthorizationMap.getWriteACLs(ActiveMQDestination)'      */
specifier|public
name|void
name|testGetWriteACLs
parameter_list|()
block|{
name|ActiveMQDestination
name|q1
init|=
operator|new
name|ActiveMQQueue
argument_list|(
literal|"queue1"
argument_list|)
decl_stmt|;
name|Set
name|aclsq1
init|=
name|authMap
operator|.
name|getWriteACLs
argument_list|(
name|q1
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|2
argument_list|,
name|aclsq1
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|aclsq1
operator|.
name|contains
argument_list|(
operator|new
name|GroupPrincipal
argument_list|(
literal|"role1"
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|aclsq1
operator|.
name|contains
argument_list|(
operator|new
name|GroupPrincipal
argument_list|(
literal|"role2"
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|ActiveMQDestination
name|t1
init|=
operator|new
name|ActiveMQTopic
argument_list|(
literal|"topic1"
argument_list|)
decl_stmt|;
name|Set
name|aclst1
init|=
name|authMap
operator|.
name|getWriteACLs
argument_list|(
name|t1
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|aclst1
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|aclst1
operator|.
name|contains
argument_list|(
operator|new
name|GroupPrincipal
argument_list|(
literal|"role3"
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

