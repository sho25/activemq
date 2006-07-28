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
name|File
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
comment|/**  * A mutable version of {@link ServerStartupConfiguration}.  *  * @version $Rev: 233391 $ $Date: 2005-08-18 16:38:47 -0600 (Thu, 18 Aug 2005) $  */
end_comment

begin_class
specifier|public
class|class
name|MutableServerStartupConfiguration
extends|extends
name|ServerStartupConfiguration
block|{
specifier|private
specifier|static
specifier|final
name|long
name|serialVersionUID
init|=
literal|515104910980600099L
decl_stmt|;
specifier|public
name|MutableServerStartupConfiguration
parameter_list|()
block|{
name|super
argument_list|()
expr_stmt|;
block|}
specifier|public
name|void
name|setAllowAnonymousAccess
parameter_list|(
name|boolean
name|arg0
parameter_list|)
block|{
name|super
operator|.
name|setAllowAnonymousAccess
argument_list|(
name|arg0
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|setAuthenticatorConfigurations
parameter_list|(
name|Set
name|arg0
parameter_list|)
block|{
name|super
operator|.
name|setAuthenticatorConfigurations
argument_list|(
name|arg0
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|setBootstrapSchemas
parameter_list|(
name|Set
name|arg0
parameter_list|)
block|{
name|super
operator|.
name|setBootstrapSchemas
argument_list|(
name|arg0
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|setContextPartitionConfigurations
parameter_list|(
name|Set
name|arg0
parameter_list|)
block|{
name|super
operator|.
name|setContextPartitionConfigurations
argument_list|(
name|arg0
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|setInterceptorConfigurations
parameter_list|(
name|List
name|arg0
parameter_list|)
block|{
name|super
operator|.
name|setInterceptorConfigurations
argument_list|(
name|arg0
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|setTestEntries
parameter_list|(
name|List
name|arg0
parameter_list|)
block|{
name|super
operator|.
name|setTestEntries
argument_list|(
name|arg0
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|setWorkingDirectory
parameter_list|(
name|File
name|arg0
parameter_list|)
block|{
name|super
operator|.
name|setWorkingDirectory
argument_list|(
name|arg0
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|setEnableKerberos
parameter_list|(
name|boolean
name|enableKerberos
parameter_list|)
block|{
name|super
operator|.
name|setEnableKerberos
argument_list|(
name|enableKerberos
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|setHost
parameter_list|(
name|InetAddress
name|host
parameter_list|)
block|{
name|super
operator|.
name|setHost
argument_list|(
name|host
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|setLdapPort
parameter_list|(
name|int
name|ldapPort
parameter_list|)
block|{
name|super
operator|.
name|setLdapPort
argument_list|(
name|ldapPort
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|setLdapsPort
parameter_list|(
name|int
name|ldapsPort
parameter_list|)
block|{
name|super
operator|.
name|setLdapsPort
argument_list|(
name|ldapsPort
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|setMinaServiceRegistry
parameter_list|(
name|ServiceRegistry
name|minaServiceRegistry
parameter_list|)
block|{
name|super
operator|.
name|setMinaServiceRegistry
argument_list|(
name|minaServiceRegistry
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

