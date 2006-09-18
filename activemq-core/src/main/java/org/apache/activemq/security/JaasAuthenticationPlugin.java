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
name|org
operator|.
name|apache
operator|.
name|activemq
operator|.
name|broker
operator|.
name|Broker
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
name|broker
operator|.
name|BrokerPlugin
import|;
end_import

begin_import
import|import
name|java
operator|.
name|net
operator|.
name|URL
import|;
end_import

begin_comment
comment|/**  * Adds a JAAS based authentication security plugin  *   * @org.apache.xbean.XBean description="Provides a JAAS based authentication plugin"  *   * @version $Revision$  */
end_comment

begin_class
specifier|public
class|class
name|JaasAuthenticationPlugin
implements|implements
name|BrokerPlugin
block|{
specifier|protected
name|String
name|configuration
init|=
literal|"activemq-domain"
decl_stmt|;
specifier|protected
name|boolean
name|discoverLoginConfig
init|=
literal|true
decl_stmt|;
specifier|public
name|Broker
name|installPlugin
parameter_list|(
name|Broker
name|broker
parameter_list|)
block|{
name|initialiseJaas
argument_list|()
expr_stmt|;
return|return
operator|new
name|JaasAuthenticationBroker
argument_list|(
name|broker
argument_list|,
name|configuration
argument_list|)
return|;
block|}
comment|// Properties
comment|// -------------------------------------------------------------------------
specifier|public
name|String
name|getConfiguration
parameter_list|()
block|{
return|return
name|configuration
return|;
block|}
comment|/**      * Sets the JAAS configuration domain name used      */
specifier|public
name|void
name|setConfiguration
parameter_list|(
name|String
name|jaasConfiguration
parameter_list|)
block|{
name|this
operator|.
name|configuration
operator|=
name|jaasConfiguration
expr_stmt|;
block|}
specifier|public
name|boolean
name|isDiscoverLoginConfig
parameter_list|()
block|{
return|return
name|discoverLoginConfig
return|;
block|}
comment|/**      * Enables or disables the auto-discovery of the login.config file for JAAS to initialize itself.       * This flag is enabled by default such that if the<b>java.security.auth.login.config</b> system property      * is not defined then it is set to the location of the<b>login.config</b> file on the classpath.      */
specifier|public
name|void
name|setDiscoverLoginConfig
parameter_list|(
name|boolean
name|discoverLoginConfig
parameter_list|)
block|{
name|this
operator|.
name|discoverLoginConfig
operator|=
name|discoverLoginConfig
expr_stmt|;
block|}
comment|// Implementation methods
comment|// -------------------------------------------------------------------------
specifier|protected
name|void
name|initialiseJaas
parameter_list|()
block|{
if|if
condition|(
name|discoverLoginConfig
condition|)
block|{
name|String
name|path
init|=
name|System
operator|.
name|getProperty
argument_list|(
literal|"java.security.auth.login.config"
argument_list|)
decl_stmt|;
if|if
condition|(
name|path
operator|==
literal|null
condition|)
block|{
comment|//URL resource = Thread.currentThread().getContextClassLoader().getResource("login.config");
name|URL
name|resource
init|=
literal|null
decl_stmt|;
if|if
condition|(
name|resource
operator|==
literal|null
condition|)
block|{
name|resource
operator|=
name|getClass
argument_list|()
operator|.
name|getClassLoader
argument_list|()
operator|.
name|getResource
argument_list|(
literal|"login.config"
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|resource
operator|!=
literal|null
condition|)
block|{
name|path
operator|=
name|resource
operator|.
name|getFile
argument_list|()
expr_stmt|;
name|System
operator|.
name|setProperty
argument_list|(
literal|"java.security.auth.login.config"
argument_list|,
name|path
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|}
block|}
end_class

end_unit

