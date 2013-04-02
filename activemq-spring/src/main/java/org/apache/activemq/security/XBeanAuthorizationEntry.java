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
name|springframework
operator|.
name|beans
operator|.
name|factory
operator|.
name|InitializingBean
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|annotation
operator|.
name|PostConstruct
import|;
end_import

begin_comment
comment|/**  * Represents an entry in a {@link DefaultAuthorizationMap} for assigning  * different operations (read, write, admin) of user roles to a specific  * destination or a hierarchical wildcard area of destinations.  *  * @org.apache.xbean.XBean element="authorizationEntry"  *  */
end_comment

begin_class
specifier|public
class|class
name|XBeanAuthorizationEntry
extends|extends
name|AuthorizationEntry
implements|implements
name|InitializingBean
block|{
annotation|@
name|Override
specifier|public
name|void
name|setAdmin
parameter_list|(
name|String
name|roles
parameter_list|)
throws|throws
name|Exception
block|{
name|adminRoles
operator|=
name|roles
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|setRead
parameter_list|(
name|String
name|roles
parameter_list|)
throws|throws
name|Exception
block|{
name|readRoles
operator|=
name|roles
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|setWrite
parameter_list|(
name|String
name|roles
parameter_list|)
throws|throws
name|Exception
block|{
name|writeRoles
operator|=
name|roles
expr_stmt|;
block|}
comment|/**      *      * @org.apache.xbean.InitMethod      */
annotation|@
name|PostConstruct
specifier|public
name|void
name|afterPropertiesSet
parameter_list|()
throws|throws
name|Exception
block|{
if|if
condition|(
name|adminRoles
operator|!=
literal|null
condition|)
block|{
name|setAdminACLs
argument_list|(
name|parseACLs
argument_list|(
name|adminRoles
argument_list|)
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|writeRoles
operator|!=
literal|null
condition|)
block|{
name|setWriteACLs
argument_list|(
name|parseACLs
argument_list|(
name|writeRoles
argument_list|)
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|readRoles
operator|!=
literal|null
condition|)
block|{
name|setReadACLs
argument_list|(
name|parseACLs
argument_list|(
name|readRoles
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit
