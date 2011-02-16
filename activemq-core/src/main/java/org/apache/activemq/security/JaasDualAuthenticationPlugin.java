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
name|broker
operator|.
name|Broker
import|;
end_import

begin_comment
comment|/**  * A JAAS based SSL certificate authentication plugin.  *  * @org.apache.xbean.XBean description="Provides a JAAS based authentication plugin  * which uses properties for non-SSL and certificates for SSL"  *   *   */
end_comment

begin_class
specifier|public
class|class
name|JaasDualAuthenticationPlugin
extends|extends
name|JaasAuthenticationPlugin
block|{
specifier|private
name|String
name|sslConfiguration
init|=
literal|"activemq-ssl-domain"
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
name|JaasDualAuthenticationBroker
argument_list|(
name|broker
argument_list|,
name|configuration
argument_list|,
name|sslConfiguration
argument_list|)
return|;
block|}
comment|// Properties
comment|// -------------------------------------------------------------------------
comment|/**      * Set the JAAS SSL configuration domain      */
specifier|public
name|void
name|setSslConfiguration
parameter_list|(
name|String
name|sslConfiguration
parameter_list|)
block|{
name|this
operator|.
name|sslConfiguration
operator|=
name|sslConfiguration
expr_stmt|;
block|}
specifier|public
name|String
name|getSslConfiguration
parameter_list|()
block|{
return|return
name|sslConfiguration
return|;
block|}
block|}
end_class

end_unit

