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
name|shiro
operator|.
name|subject
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
name|broker
operator|.
name|ConnectionContext
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
name|ConnectionInfo
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|shiro
operator|.
name|env
operator|.
name|DefaultEnvironment
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

begin_comment
comment|/**  * @since 5.10.0  */
end_comment

begin_class
specifier|public
class|class
name|SubjectSecurityContextTest
block|{
name|SubjectSecurityContext
name|ctx
decl_stmt|;
annotation|@
name|Before
specifier|public
name|void
name|setUp
parameter_list|()
block|{
name|SubjectConnectionReference
name|conn
init|=
operator|new
name|SubjectConnectionReference
argument_list|(
operator|new
name|ConnectionContext
argument_list|()
argument_list|,
operator|new
name|ConnectionInfo
argument_list|()
argument_list|,
operator|new
name|DefaultEnvironment
argument_list|()
argument_list|,
operator|new
name|SubjectAdapter
argument_list|()
argument_list|)
decl_stmt|;
name|ctx
operator|=
operator|new
name|SubjectSecurityContext
argument_list|(
name|conn
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
argument_list|(
name|expected
operator|=
name|UnsupportedOperationException
operator|.
name|class
argument_list|)
specifier|public
name|void
name|testInOneOf
parameter_list|()
block|{
name|ctx
operator|.
name|isInOneOf
argument_list|(
literal|null
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
argument_list|(
name|expected
operator|=
name|UnsupportedOperationException
operator|.
name|class
argument_list|)
specifier|public
name|void
name|testGetAuthorizedReadDests
parameter_list|()
block|{
name|ctx
operator|.
name|getAuthorizedReadDests
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
argument_list|(
name|expected
operator|=
name|UnsupportedOperationException
operator|.
name|class
argument_list|)
specifier|public
name|void
name|testGetAuthorizedWriteDests
parameter_list|()
block|{
name|ctx
operator|.
name|getAuthorizedWriteDests
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
argument_list|(
name|expected
operator|=
name|UnsupportedOperationException
operator|.
name|class
argument_list|)
specifier|public
name|void
name|testGetPrincipals
parameter_list|()
block|{
name|ctx
operator|.
name|getPrincipals
argument_list|()
expr_stmt|;
block|}
block|}
end_class

end_unit

