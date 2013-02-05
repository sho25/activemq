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
name|jaas
operator|.
name|UserPrincipal
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
name|shared
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
name|junit
operator|.
name|Test
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
name|assertTrue
import|;
end_import

begin_class
specifier|public
specifier|abstract
class|class
name|AbstractCachedLDAPAuthorizationModuleTest
extends|extends
name|AbstractCachedLDAPAuthorizationMapLegacyTest
block|{
specifier|static
specifier|final
name|UserPrincipal
name|JDOE
init|=
operator|new
name|UserPrincipal
argument_list|(
literal|"jdoe"
argument_list|)
decl_stmt|;
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
literal|"TEST.FOOBAR"
argument_list|)
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|"set size: "
operator|+
name|readACLs
argument_list|,
literal|3
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
name|assertTrue
argument_list|(
literal|"Contains jdoe user"
argument_list|,
name|readACLs
operator|.
name|contains
argument_list|(
name|JDOE
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
name|super
operator|.
name|testQuery
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Override
specifier|protected
specifier|final
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
name|getMemberAttributeValueForModifyRequest
argument_list|()
argument_list|)
expr_stmt|;
block|}
specifier|protected
specifier|abstract
name|String
name|getMemberAttributeValueForModifyRequest
parameter_list|()
function_decl|;
annotation|@
name|Override
specifier|protected
name|SimpleCachedLDAPAuthorizationMap
name|createMap
parameter_list|()
block|{
name|SimpleCachedLDAPAuthorizationMap
name|map
init|=
name|super
operator|.
name|createMap
argument_list|()
decl_stmt|;
name|map
operator|.
name|setLegacyGroupMapping
argument_list|(
literal|false
argument_list|)
expr_stmt|;
return|return
name|map
return|;
block|}
block|}
end_class

end_unit
