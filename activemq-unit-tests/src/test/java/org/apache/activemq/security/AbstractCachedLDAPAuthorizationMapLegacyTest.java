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
import|import static
name|org
operator|.
name|junit
operator|.
name|Assert
operator|.
name|assertEquals
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
name|assertNull
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
name|io
operator|.
name|InputStream
import|;
end_import

begin_import
import|import
name|java
operator|.
name|lang
operator|.
name|reflect
operator|.
name|InvocationTargetException
import|;
end_import

begin_import
import|import
name|java
operator|.
name|lang
operator|.
name|reflect
operator|.
name|Method
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|LinkedList
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
name|directory
operator|.
name|DirContext
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
name|activemq
operator|.
name|util
operator|.
name|Wait
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
name|api
operator|.
name|ldap
operator|.
name|model
operator|.
name|name
operator|.
name|Dn
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
name|api
operator|.
name|ldap
operator|.
name|model
operator|.
name|name
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
name|directory
operator|.
name|api
operator|.
name|ldap
operator|.
name|model
operator|.
name|ldif
operator|.
name|LdifEntry
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
name|api
operator|.
name|ldap
operator|.
name|model
operator|.
name|ldif
operator|.
name|LdifReader
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
name|api
operator|.
name|ldap
operator|.
name|model
operator|.
name|message
operator|.
name|ModifyRequest
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
name|api
operator|.
name|ldap
operator|.
name|model
operator|.
name|message
operator|.
name|ModifyRequestImpl
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
name|ldap
operator|.
name|client
operator|.
name|api
operator|.
name|LdapConnection
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
name|AbstractLdapTestUnit
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

begin_class
specifier|public
specifier|abstract
class|class
name|AbstractCachedLDAPAuthorizationMapLegacyTest
extends|extends
name|AbstractLdapTestUnit
block|{
specifier|static
specifier|final
name|GroupPrincipal
name|GUESTS
init|=
operator|new
name|GroupPrincipal
argument_list|(
literal|"guests"
argument_list|)
decl_stmt|;
specifier|static
specifier|final
name|GroupPrincipal
name|USERS
init|=
operator|new
name|GroupPrincipal
argument_list|(
literal|"users"
argument_list|)
decl_stmt|;
specifier|static
specifier|final
name|GroupPrincipal
name|ADMINS
init|=
operator|new
name|GroupPrincipal
argument_list|(
literal|"admins"
argument_list|)
decl_stmt|;
specifier|protected
name|LdapConnection
name|connection
decl_stmt|;
specifier|protected
name|SimpleCachedLDAPAuthorizationMap
name|map
decl_stmt|;
annotation|@
name|Before
specifier|public
name|void
name|setup
parameter_list|()
throws|throws
name|Exception
block|{
name|connection
operator|=
name|getLdapConnection
argument_list|()
expr_stmt|;
name|map
operator|=
name|createMap
argument_list|()
expr_stmt|;
block|}
annotation|@
name|After
specifier|public
name|void
name|cleanup
parameter_list|()
throws|throws
name|Exception
block|{
if|if
condition|(
name|connection
operator|!=
literal|null
condition|)
block|{
try|try
block|{
name|connection
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
comment|// Ignore
block|}
block|}
if|if
condition|(
name|map
operator|!=
literal|null
condition|)
block|{
name|map
operator|.
name|destroy
argument_list|()
expr_stmt|;
block|}
block|}
annotation|@
name|Test
specifier|public
name|void
name|testQuery
parameter_list|()
throws|throws
name|Exception
block|{
name|map
operator|.
name|query
argument_list|()
expr_stmt|;
name|Set
argument_list|<
name|?
argument_list|>
name|readACLs
init|=
name|map
operator|.
name|getReadACLs
argument_list|(
operator|new
name|ActiveMQQueue
argument_list|(
literal|"TEST.FOO"
argument_list|)
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|"set size: "
operator|+
name|readACLs
argument_list|,
literal|2
argument_list|,
name|readACLs
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
literal|"Contains admin group"
argument_list|,
name|readACLs
operator|.
name|contains
argument_list|(
name|ADMINS
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
literal|"Contains users group"
argument_list|,
name|readACLs
operator|.
name|contains
argument_list|(
name|USERS
argument_list|)
argument_list|)
expr_stmt|;
name|Set
argument_list|<
name|?
argument_list|>
name|failedACLs
init|=
name|map
operator|.
name|getReadACLs
argument_list|(
operator|new
name|ActiveMQQueue
argument_list|(
literal|"FAILED"
argument_list|)
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|"set size: "
operator|+
name|failedACLs
argument_list|,
literal|0
argument_list|,
name|failedACLs
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testSynchronousUpdate
parameter_list|()
throws|throws
name|Exception
block|{
name|map
operator|.
name|setRefreshInterval
argument_list|(
literal|1
argument_list|)
expr_stmt|;
name|map
operator|.
name|query
argument_list|()
expr_stmt|;
name|Set
argument_list|<
name|?
argument_list|>
name|readACLs
init|=
name|map
operator|.
name|getReadACLs
argument_list|(
operator|new
name|ActiveMQQueue
argument_list|(
literal|"TEST.FOO"
argument_list|)
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|"set size: "
operator|+
name|readACLs
argument_list|,
literal|2
argument_list|,
name|readACLs
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
literal|"Contains admin group"
argument_list|,
name|readACLs
operator|.
name|contains
argument_list|(
name|ADMINS
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
literal|"Contains users group"
argument_list|,
name|readACLs
operator|.
name|contains
argument_list|(
name|USERS
argument_list|)
argument_list|)
expr_stmt|;
name|Set
argument_list|<
name|?
argument_list|>
name|failedACLs
init|=
name|map
operator|.
name|getReadACLs
argument_list|(
operator|new
name|ActiveMQQueue
argument_list|(
literal|"FAILED"
argument_list|)
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|"set size: "
operator|+
name|failedACLs
argument_list|,
literal|0
argument_list|,
name|failedACLs
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|LdifReader
name|reader
init|=
operator|new
name|LdifReader
argument_list|(
name|getRemoveLdif
argument_list|()
argument_list|)
decl_stmt|;
for|for
control|(
name|LdifEntry
name|entry
range|:
name|reader
control|)
block|{
name|connection
operator|.
name|delete
argument_list|(
name|entry
operator|.
name|getDn
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|reader
operator|.
name|close
argument_list|()
expr_stmt|;
name|assertTrue
argument_list|(
literal|"did not get expected size. "
argument_list|,
name|Wait
operator|.
name|waitFor
argument_list|(
operator|new
name|Wait
operator|.
name|Condition
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|boolean
name|isSatisified
parameter_list|()
throws|throws
name|Exception
block|{
return|return
name|map
operator|.
name|getReadACLs
argument_list|(
operator|new
name|ActiveMQQueue
argument_list|(
literal|"TEST.FOO"
argument_list|)
argument_list|)
operator|.
name|size
argument_list|()
operator|==
literal|0
return|;
block|}
block|}
argument_list|)
argument_list|)
expr_stmt|;
name|assertNull
argument_list|(
name|map
operator|.
name|getTempDestinationReadACLs
argument_list|()
argument_list|)
expr_stmt|;
name|assertNull
argument_list|(
name|map
operator|.
name|getTempDestinationWriteACLs
argument_list|()
argument_list|)
expr_stmt|;
name|assertNull
argument_list|(
name|map
operator|.
name|getTempDestinationAdminACLs
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testWildcards
parameter_list|()
throws|throws
name|Exception
block|{
name|map
operator|.
name|query
argument_list|()
expr_stmt|;
name|Set
argument_list|<
name|?
argument_list|>
name|fooACLs
init|=
name|map
operator|.
name|getReadACLs
argument_list|(
operator|new
name|ActiveMQQueue
argument_list|(
literal|"FOO.1"
argument_list|)
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|"set size: "
operator|+
name|fooACLs
argument_list|,
literal|2
argument_list|,
name|fooACLs
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
literal|"Contains admin group"
argument_list|,
name|fooACLs
operator|.
name|contains
argument_list|(
name|ADMINS
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
literal|"Contains users group"
argument_list|,
name|fooACLs
operator|.
name|contains
argument_list|(
name|USERS
argument_list|)
argument_list|)
expr_stmt|;
name|Set
argument_list|<
name|?
argument_list|>
name|barACLs
init|=
name|map
operator|.
name|getReadACLs
argument_list|(
operator|new
name|ActiveMQQueue
argument_list|(
literal|"BAR.2"
argument_list|)
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|"set size: "
operator|+
name|barACLs
argument_list|,
literal|2
argument_list|,
name|barACLs
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
literal|"Contains admin group"
argument_list|,
name|barACLs
operator|.
name|contains
argument_list|(
name|ADMINS
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
literal|"Contains users group"
argument_list|,
name|barACLs
operator|.
name|contains
argument_list|(
name|USERS
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testAdvisory
parameter_list|()
throws|throws
name|Exception
block|{
name|map
operator|.
name|query
argument_list|()
expr_stmt|;
name|Set
argument_list|<
name|?
argument_list|>
name|readACLs
init|=
name|map
operator|.
name|getReadACLs
argument_list|(
operator|new
name|ActiveMQTopic
argument_list|(
literal|"ActiveMQ.Advisory.Connection"
argument_list|)
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|"set size: "
operator|+
name|readACLs
argument_list|,
literal|2
argument_list|,
name|readACLs
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
literal|"Contains admin group"
argument_list|,
name|readACLs
operator|.
name|contains
argument_list|(
name|ADMINS
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
literal|"Contains users group"
argument_list|,
name|readACLs
operator|.
name|contains
argument_list|(
name|USERS
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testTemporary
parameter_list|()
throws|throws
name|Exception
block|{
name|map
operator|.
name|query
argument_list|()
expr_stmt|;
name|Thread
operator|.
name|sleep
argument_list|(
literal|1000
argument_list|)
expr_stmt|;
name|Set
argument_list|<
name|?
argument_list|>
name|readACLs
init|=
name|map
operator|.
name|getTempDestinationReadACLs
argument_list|()
decl_stmt|;
name|assertEquals
argument_list|(
literal|"set size: "
operator|+
name|readACLs
argument_list|,
literal|2
argument_list|,
name|readACLs
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
literal|"Contains admin group"
argument_list|,
name|readACLs
operator|.
name|contains
argument_list|(
name|ADMINS
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
literal|"Contains users group"
argument_list|,
name|readACLs
operator|.
name|contains
argument_list|(
name|USERS
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testAdd
parameter_list|()
throws|throws
name|Exception
block|{
name|map
operator|.
name|query
argument_list|()
expr_stmt|;
name|Set
argument_list|<
name|?
argument_list|>
name|failedACLs
init|=
name|map
operator|.
name|getReadACLs
argument_list|(
operator|new
name|ActiveMQQueue
argument_list|(
literal|"FAILED"
argument_list|)
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|"set size: "
operator|+
name|failedACLs
argument_list|,
literal|0
argument_list|,
name|failedACLs
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|LdifReader
name|reader
init|=
operator|new
name|LdifReader
argument_list|(
name|getAddLdif
argument_list|()
argument_list|)
decl_stmt|;
for|for
control|(
name|LdifEntry
name|entry
range|:
name|reader
control|)
block|{
name|connection
operator|.
name|add
argument_list|(
name|entry
operator|.
name|getEntry
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|reader
operator|.
name|close
argument_list|()
expr_stmt|;
name|Thread
operator|.
name|sleep
argument_list|(
literal|2000
argument_list|)
expr_stmt|;
name|failedACLs
operator|=
name|map
operator|.
name|getReadACLs
argument_list|(
operator|new
name|ActiveMQQueue
argument_list|(
literal|"FAILED"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"set size: "
operator|+
name|failedACLs
argument_list|,
literal|2
argument_list|,
name|failedACLs
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testRemove
parameter_list|()
throws|throws
name|Exception
block|{
name|map
operator|.
name|query
argument_list|()
expr_stmt|;
name|Set
argument_list|<
name|?
argument_list|>
name|failedACLs
init|=
name|map
operator|.
name|getReadACLs
argument_list|(
operator|new
name|ActiveMQQueue
argument_list|(
literal|"TEST.FOO"
argument_list|)
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|"set size: "
operator|+
name|failedACLs
argument_list|,
literal|2
argument_list|,
name|failedACLs
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|LdifReader
name|reader
init|=
operator|new
name|LdifReader
argument_list|(
name|getRemoveLdif
argument_list|()
argument_list|)
decl_stmt|;
for|for
control|(
name|LdifEntry
name|entry
range|:
name|reader
control|)
block|{
name|connection
operator|.
name|delete
argument_list|(
name|entry
operator|.
name|getDn
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|reader
operator|.
name|close
argument_list|()
expr_stmt|;
name|Thread
operator|.
name|sleep
argument_list|(
literal|2000
argument_list|)
expr_stmt|;
name|failedACLs
operator|=
name|map
operator|.
name|getReadACLs
argument_list|(
operator|new
name|ActiveMQQueue
argument_list|(
literal|"TEST.FOO"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"set size: "
operator|+
name|failedACLs
argument_list|,
literal|0
argument_list|,
name|failedACLs
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|map
operator|.
name|getTempDestinationReadACLs
argument_list|()
operator|==
literal|null
operator|||
name|map
operator|.
name|getTempDestinationReadACLs
argument_list|()
operator|.
name|isEmpty
argument_list|()
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|map
operator|.
name|getTempDestinationWriteACLs
argument_list|()
operator|==
literal|null
operator|||
name|map
operator|.
name|getTempDestinationWriteACLs
argument_list|()
operator|.
name|isEmpty
argument_list|()
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|map
operator|.
name|getTempDestinationAdminACLs
argument_list|()
operator|==
literal|null
operator|||
name|map
operator|.
name|getTempDestinationAdminACLs
argument_list|()
operator|.
name|isEmpty
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testRenameDestination
parameter_list|()
throws|throws
name|Exception
block|{
name|map
operator|.
name|query
argument_list|()
expr_stmt|;
comment|// Test for a destination rename
name|Set
argument_list|<
name|?
argument_list|>
name|failedACLs
init|=
name|map
operator|.
name|getReadACLs
argument_list|(
operator|new
name|ActiveMQQueue
argument_list|(
literal|"TEST.FOO"
argument_list|)
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|"set size: "
operator|+
name|failedACLs
argument_list|,
literal|2
argument_list|,
name|failedACLs
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|connection
operator|.
name|rename
argument_list|(
operator|new
name|Dn
argument_list|(
literal|"cn=TEST.FOO,"
operator|+
name|getQueueBaseDn
argument_list|()
argument_list|)
argument_list|,
operator|new
name|Rdn
argument_list|(
literal|"cn=TEST.BAR"
argument_list|)
argument_list|)
expr_stmt|;
name|Thread
operator|.
name|sleep
argument_list|(
literal|2000
argument_list|)
expr_stmt|;
name|failedACLs
operator|=
name|map
operator|.
name|getReadACLs
argument_list|(
operator|new
name|ActiveMQQueue
argument_list|(
literal|"TEST.FOO"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"set size: "
operator|+
name|failedACLs
argument_list|,
literal|0
argument_list|,
name|failedACLs
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|failedACLs
operator|=
name|map
operator|.
name|getReadACLs
argument_list|(
operator|new
name|ActiveMQQueue
argument_list|(
literal|"TEST.BAR"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"set size: "
operator|+
name|failedACLs
argument_list|,
literal|2
argument_list|,
name|failedACLs
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testRenamePermission
parameter_list|()
throws|throws
name|Exception
block|{
name|map
operator|.
name|query
argument_list|()
expr_stmt|;
comment|// Test for a permission rename
name|connection
operator|.
name|delete
argument_list|(
operator|new
name|Dn
argument_list|(
literal|"cn=Read,cn=TEST.FOO,"
operator|+
name|getQueueBaseDn
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|Thread
operator|.
name|sleep
argument_list|(
literal|2000
argument_list|)
expr_stmt|;
name|Set
argument_list|<
name|?
argument_list|>
name|failedACLs
init|=
name|map
operator|.
name|getReadACLs
argument_list|(
operator|new
name|ActiveMQQueue
argument_list|(
literal|"TEST.FOO"
argument_list|)
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|"set size: "
operator|+
name|failedACLs
argument_list|,
literal|0
argument_list|,
name|failedACLs
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|failedACLs
operator|=
name|map
operator|.
name|getWriteACLs
argument_list|(
operator|new
name|ActiveMQQueue
argument_list|(
literal|"TEST.FOO"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"set size: "
operator|+
name|failedACLs
argument_list|,
literal|2
argument_list|,
name|failedACLs
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|connection
operator|.
name|rename
argument_list|(
operator|new
name|Dn
argument_list|(
literal|"cn=Write,cn=TEST.FOO,"
operator|+
name|getQueueBaseDn
argument_list|()
argument_list|)
argument_list|,
operator|new
name|Rdn
argument_list|(
literal|"cn=Read"
argument_list|)
argument_list|)
expr_stmt|;
name|Thread
operator|.
name|sleep
argument_list|(
literal|2000
argument_list|)
expr_stmt|;
name|failedACLs
operator|=
name|map
operator|.
name|getReadACLs
argument_list|(
operator|new
name|ActiveMQQueue
argument_list|(
literal|"TEST.FOO"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"set size: "
operator|+
name|failedACLs
argument_list|,
literal|2
argument_list|,
name|failedACLs
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|failedACLs
operator|=
name|map
operator|.
name|getWriteACLs
argument_list|(
operator|new
name|ActiveMQQueue
argument_list|(
literal|"TEST.FOO"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"set size: "
operator|+
name|failedACLs
argument_list|,
literal|0
argument_list|,
name|failedACLs
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testChange
parameter_list|()
throws|throws
name|Exception
block|{
name|map
operator|.
name|query
argument_list|()
expr_stmt|;
comment|// Change permission entry
name|Set
argument_list|<
name|?
argument_list|>
name|failedACLs
init|=
name|map
operator|.
name|getReadACLs
argument_list|(
operator|new
name|ActiveMQQueue
argument_list|(
literal|"TEST.FOO"
argument_list|)
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|"set size: "
operator|+
name|failedACLs
argument_list|,
literal|2
argument_list|,
name|failedACLs
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|Dn
name|dn
init|=
operator|new
name|Dn
argument_list|(
literal|"cn=read,cn=TEST.FOO,"
operator|+
name|getQueueBaseDn
argument_list|()
argument_list|)
decl_stmt|;
name|ModifyRequest
name|request
init|=
operator|new
name|ModifyRequestImpl
argument_list|()
decl_stmt|;
name|request
operator|.
name|setName
argument_list|(
name|dn
argument_list|)
expr_stmt|;
name|setupModifyRequest
argument_list|(
name|request
argument_list|)
expr_stmt|;
name|connection
operator|.
name|modify
argument_list|(
name|request
argument_list|)
expr_stmt|;
name|Thread
operator|.
name|sleep
argument_list|(
literal|2000
argument_list|)
expr_stmt|;
name|failedACLs
operator|=
name|map
operator|.
name|getReadACLs
argument_list|(
operator|new
name|ActiveMQQueue
argument_list|(
literal|"TEST.FOO"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"set size: "
operator|+
name|failedACLs
argument_list|,
literal|1
argument_list|,
name|failedACLs
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
comment|// Change destination entry
name|request
operator|=
operator|new
name|ModifyRequestImpl
argument_list|()
expr_stmt|;
name|request
operator|.
name|setName
argument_list|(
operator|new
name|Dn
argument_list|(
literal|"cn=TEST.FOO,"
operator|+
name|getQueueBaseDn
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|request
operator|.
name|add
argument_list|(
literal|"description"
argument_list|,
literal|"This is a description!  In fact, it is a very good description."
argument_list|)
expr_stmt|;
name|connection
operator|.
name|modify
argument_list|(
name|request
argument_list|)
expr_stmt|;
name|Thread
operator|.
name|sleep
argument_list|(
literal|2000
argument_list|)
expr_stmt|;
name|failedACLs
operator|=
name|map
operator|.
name|getReadACLs
argument_list|(
operator|new
name|ActiveMQQueue
argument_list|(
literal|"TEST.FOO"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"set size: "
operator|+
name|failedACLs
argument_list|,
literal|1
argument_list|,
name|failedACLs
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testRestartAsync
parameter_list|()
throws|throws
name|Exception
block|{
name|testRestart
argument_list|(
literal|false
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testRestartSync
parameter_list|()
throws|throws
name|Exception
block|{
name|testRestart
argument_list|(
literal|true
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|testRestart
parameter_list|(
specifier|final
name|boolean
name|sync
parameter_list|)
throws|throws
name|Exception
block|{
if|if
condition|(
name|sync
condition|)
block|{
comment|// ldap connection can be slow to close
name|map
operator|.
name|setRefreshInterval
argument_list|(
literal|1000
argument_list|)
expr_stmt|;
block|}
name|map
operator|.
name|query
argument_list|()
expr_stmt|;
name|Set
argument_list|<
name|?
argument_list|>
name|failedACLs
init|=
name|map
operator|.
name|getReadACLs
argument_list|(
operator|new
name|ActiveMQQueue
argument_list|(
literal|"FAILED"
argument_list|)
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|"set size: "
operator|+
name|failedACLs
argument_list|,
literal|0
argument_list|,
name|failedACLs
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|failedACLs
operator|=
name|map
operator|.
name|getReadACLs
argument_list|(
operator|new
name|ActiveMQQueue
argument_list|(
literal|"TEST.FOO"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"set size: "
operator|+
name|failedACLs
argument_list|,
literal|2
argument_list|,
name|failedACLs
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|getLdapServer
argument_list|()
operator|.
name|stop
argument_list|()
expr_stmt|;
comment|// wait for the context to be closed
comment|// as we can't rely on ldar server isStarted()
name|Wait
operator|.
name|waitFor
argument_list|(
operator|new
name|Wait
operator|.
name|Condition
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|boolean
name|isSatisified
parameter_list|()
throws|throws
name|Exception
block|{
if|if
condition|(
name|sync
condition|)
block|{
return|return
operator|!
name|map
operator|.
name|isContextAlive
argument_list|()
return|;
block|}
else|else
block|{
return|return
name|map
operator|.
name|context
operator|==
literal|null
return|;
block|}
block|}
block|}
argument_list|)
expr_stmt|;
name|failedACLs
operator|=
name|map
operator|.
name|getReadACLs
argument_list|(
operator|new
name|ActiveMQQueue
argument_list|(
literal|"TEST.FOO"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"set size: "
operator|+
name|failedACLs
argument_list|,
literal|2
argument_list|,
name|failedACLs
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|getLdapServer
argument_list|()
operator|.
name|start
argument_list|()
expr_stmt|;
name|Thread
operator|.
name|sleep
argument_list|(
literal|2000
argument_list|)
expr_stmt|;
name|connection
operator|=
name|getLdapConnection
argument_list|()
expr_stmt|;
name|LdifReader
name|reader
init|=
operator|new
name|LdifReader
argument_list|(
name|getAddLdif
argument_list|()
argument_list|)
decl_stmt|;
for|for
control|(
name|LdifEntry
name|entry
range|:
name|reader
control|)
block|{
name|connection
operator|.
name|add
argument_list|(
name|entry
operator|.
name|getEntry
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|reader
operator|.
name|close
argument_list|()
expr_stmt|;
name|assertTrue
argument_list|(
literal|"did not get expected size. "
argument_list|,
name|Wait
operator|.
name|waitFor
argument_list|(
operator|new
name|Wait
operator|.
name|Condition
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|boolean
name|isSatisified
parameter_list|()
throws|throws
name|Exception
block|{
return|return
name|map
operator|.
name|getReadACLs
argument_list|(
operator|new
name|ActiveMQQueue
argument_list|(
literal|"FAILED"
argument_list|)
argument_list|)
operator|.
name|size
argument_list|()
operator|==
literal|2
return|;
block|}
block|}
argument_list|)
argument_list|)
expr_stmt|;
block|}
specifier|protected
name|SimpleCachedLDAPAuthorizationMap
name|createMap
parameter_list|()
block|{
return|return
operator|new
name|SimpleCachedLDAPAuthorizationMap
argument_list|()
return|;
block|}
specifier|protected
specifier|abstract
name|InputStream
name|getAddLdif
parameter_list|()
function_decl|;
specifier|protected
specifier|abstract
name|InputStream
name|getRemoveLdif
parameter_list|()
function_decl|;
specifier|protected
name|void
name|setupModifyRequest
parameter_list|(
name|ModifyRequest
name|request
parameter_list|)
block|{
name|request
operator|.
name|remove
argument_list|(
literal|"member"
argument_list|,
literal|"cn=users"
argument_list|)
expr_stmt|;
block|}
specifier|protected
specifier|abstract
name|String
name|getQueueBaseDn
parameter_list|()
function_decl|;
specifier|protected
specifier|abstract
name|LdapConnection
name|getLdapConnection
parameter_list|()
throws|throws
name|Exception
function_decl|;
specifier|public
specifier|static
name|void
name|cleanAndLoad
parameter_list|(
name|String
name|deleteFromDn
parameter_list|,
name|String
name|ldifResourcePath
parameter_list|,
name|String
name|ldapHost
parameter_list|,
name|int
name|ldapPort
parameter_list|,
name|String
name|ldapUser
parameter_list|,
name|String
name|ldapPass
parameter_list|,
name|DirContext
name|context
parameter_list|)
throws|throws
name|Exception
block|{
comment|// Cleanup everything used for testing.
name|List
argument_list|<
name|String
argument_list|>
name|dns
init|=
operator|new
name|LinkedList
argument_list|<
name|String
argument_list|>
argument_list|()
decl_stmt|;
name|dns
operator|.
name|add
argument_list|(
name|deleteFromDn
argument_list|)
expr_stmt|;
while|while
condition|(
operator|!
name|dns
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
name|String
name|name
init|=
name|dns
operator|.
name|get
argument_list|(
name|dns
operator|.
name|size
argument_list|()
operator|-
literal|1
argument_list|)
decl_stmt|;
name|Context
name|currentContext
init|=
operator|(
name|Context
operator|)
name|context
operator|.
name|lookup
argument_list|(
name|name
argument_list|)
decl_stmt|;
name|NamingEnumeration
argument_list|<
name|NameClassPair
argument_list|>
name|namingEnum
init|=
name|currentContext
operator|.
name|list
argument_list|(
literal|""
argument_list|)
decl_stmt|;
if|if
condition|(
name|namingEnum
operator|.
name|hasMore
argument_list|()
condition|)
block|{
while|while
condition|(
name|namingEnum
operator|.
name|hasMore
argument_list|()
condition|)
block|{
name|dns
operator|.
name|add
argument_list|(
name|namingEnum
operator|.
name|next
argument_list|()
operator|.
name|getNameInNamespace
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
else|else
block|{
name|context
operator|.
name|unbind
argument_list|(
name|name
argument_list|)
expr_stmt|;
name|dns
operator|.
name|remove
argument_list|(
name|dns
operator|.
name|size
argument_list|()
operator|-
literal|1
argument_list|)
expr_stmt|;
block|}
block|}
comment|// A bit of a hacked approach to loading an LDIF into OpenLDAP since there isn't an easy way to do it
comment|// otherwise.  This approach invokes the command line tool programmatically but has
comment|// to short-circuit the call to System.exit that the command line tool makes when it finishes.
comment|// We are assuming that there isn't already a security manager in place.
specifier|final
name|SecurityManager
name|securityManager
init|=
operator|new
name|SecurityManager
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|void
name|checkPermission
parameter_list|(
name|java
operator|.
name|security
operator|.
name|Permission
name|permission
parameter_list|)
block|{
if|if
condition|(
name|permission
operator|.
name|getName
argument_list|()
operator|.
name|contains
argument_list|(
literal|"exitVM"
argument_list|)
condition|)
block|{
throw|throw
operator|new
name|SecurityException
argument_list|(
literal|"System.exit calls disabled for the moment."
argument_list|)
throw|;
block|}
block|}
block|}
decl_stmt|;
name|System
operator|.
name|setSecurityManager
argument_list|(
name|securityManager
argument_list|)
expr_stmt|;
name|File
name|file
init|=
operator|new
name|File
argument_list|(
name|AbstractCachedLDAPAuthorizationMapLegacyTest
operator|.
name|class
operator|.
name|getClassLoader
argument_list|()
operator|.
name|getResource
argument_list|(
name|ldifResourcePath
argument_list|)
operator|.
name|toURI
argument_list|()
argument_list|)
decl_stmt|;
name|Class
argument_list|<
name|?
argument_list|>
name|clazz
init|=
name|Class
operator|.
name|forName
argument_list|(
literal|"LDAPModify"
argument_list|)
decl_stmt|;
name|Method
name|mainMethod
init|=
name|clazz
operator|.
name|getMethod
argument_list|(
literal|"main"
argument_list|,
name|String
index|[]
operator|.
expr|class
argument_list|)
decl_stmt|;
try|try
block|{
name|mainMethod
operator|.
name|invoke
argument_list|(
literal|null
argument_list|,
operator|new
name|Object
index|[]
block|{
operator|new
name|String
index|[]
block|{
literal|"-v"
block|,
literal|"-h"
block|,
name|ldapHost
block|,
literal|"-p"
block|,
name|String
operator|.
name|valueOf
argument_list|(
name|ldapPort
argument_list|)
block|,
literal|"-D"
block|,
name|ldapUser
block|,
literal|"-w"
block|,
name|ldapPass
block|,
literal|"-a"
block|,
literal|"-f"
block|,
name|file
operator|.
name|toString
argument_list|()
block|}
block|}
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|InvocationTargetException
name|e
parameter_list|)
block|{
if|if
condition|(
operator|!
operator|(
name|e
operator|.
name|getTargetException
argument_list|()
operator|instanceof
name|SecurityException
operator|)
condition|)
block|{
throw|throw
name|e
throw|;
block|}
block|}
name|System
operator|.
name|setSecurityManager
argument_list|(
literal|null
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

