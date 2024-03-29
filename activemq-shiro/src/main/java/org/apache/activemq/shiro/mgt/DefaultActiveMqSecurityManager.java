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
name|mgt
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
name|shiro
operator|.
name|session
operator|.
name|mgt
operator|.
name|DisabledSessionManager
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
name|mgt
operator|.
name|DefaultSecurityManager
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
name|mgt
operator|.
name|DefaultSessionStorageEvaluator
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
name|mgt
operator|.
name|DefaultSubjectDAO
import|;
end_import

begin_comment
comment|/**  * @since 5.10.0  */
end_comment

begin_class
specifier|public
class|class
name|DefaultActiveMqSecurityManager
extends|extends
name|DefaultSecurityManager
block|{
specifier|public
name|DefaultActiveMqSecurityManager
parameter_list|()
block|{
name|super
argument_list|()
expr_stmt|;
comment|//disable sessions entirely:
name|setSessionManager
argument_list|(
operator|new
name|DisabledSessionManager
argument_list|()
argument_list|)
expr_stmt|;
comment|//also prevent the SecurityManager impl from using the Session as a storage medium (i.e. after authc):
name|DefaultSubjectDAO
name|subjectDao
init|=
operator|(
name|DefaultSubjectDAO
operator|)
name|getSubjectDAO
argument_list|()
decl_stmt|;
name|DefaultSessionStorageEvaluator
name|sessionStorageEvaluator
init|=
operator|(
name|DefaultSessionStorageEvaluator
operator|)
name|subjectDao
operator|.
name|getSessionStorageEvaluator
argument_list|()
decl_stmt|;
name|sessionStorageEvaluator
operator|.
name|setSessionStorageEnabled
argument_list|(
literal|false
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

