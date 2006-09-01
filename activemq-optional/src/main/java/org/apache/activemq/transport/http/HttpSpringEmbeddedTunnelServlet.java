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
name|transport
operator|.
name|http
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
name|BrokerService
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
name|xbean
operator|.
name|BrokerFactoryBean
import|;
end_import

begin_import
import|import
name|org
operator|.
name|springframework
operator|.
name|core
operator|.
name|io
operator|.
name|ClassPathResource
import|;
end_import

begin_comment
comment|/**  * This servlet embeds an ActiveMQ broker inside a servlet engine which is  * ideal for deploying ActiveMQ inside a WAR and using this servlet as a HTTP tunnel.  *  * @version $Revision$  */
end_comment

begin_class
specifier|public
class|class
name|HttpSpringEmbeddedTunnelServlet
extends|extends
name|HttpEmbeddedTunnelServlet
block|{
specifier|private
specifier|static
specifier|final
name|long
name|serialVersionUID
init|=
operator|-
literal|6568661997192814908L
decl_stmt|;
comment|/**      * Factory method to create a new broker      */
specifier|protected
name|BrokerService
name|createBroker
parameter_list|()
throws|throws
name|Exception
block|{
name|String
name|configFile
init|=
name|getServletContext
argument_list|()
operator|.
name|getInitParameter
argument_list|(
literal|"org.activemq.config.file"
argument_list|)
decl_stmt|;
if|if
condition|(
name|configFile
operator|==
literal|null
condition|)
block|{
name|configFile
operator|=
literal|"activemq.xml"
expr_stmt|;
block|}
name|BrokerFactoryBean
name|factory
init|=
operator|new
name|BrokerFactoryBean
argument_list|(
operator|new
name|ClassPathResource
argument_list|(
name|configFile
argument_list|)
argument_list|)
decl_stmt|;
name|factory
operator|.
name|afterPropertiesSet
argument_list|()
expr_stmt|;
return|return
name|factory
operator|.
name|getBroker
argument_list|()
return|;
block|}
block|}
end_class

end_unit

