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
name|maven
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
name|maven
operator|.
name|plugin
operator|.
name|MojoExecutionException
import|;
end_import

begin_comment
comment|/**  * Manager of the broker used by the maven plugin.  */
end_comment

begin_interface
specifier|public
interface|interface
name|MavenBrokerManager
block|{
comment|/**      * Start the broker using the fork setting and configuration at the given URI.      *      * @param fork true => run the broker asynchronously; false => run the broker synchronously (this method does not      *             return until the broker shuts down)      * @param configUri URI of the broker configuration; prefix with "xbean:file" to read XML configuration from a file.      * @throws MojoExecutionException      */
name|void
name|start
parameter_list|(
name|boolean
name|fork
parameter_list|,
name|String
name|configUri
parameter_list|)
throws|throws
name|MojoExecutionException
function_decl|;
comment|/**      * Stop the broker.      *      * @throws MojoExecutionException      */
name|void
name|stop
parameter_list|()
throws|throws
name|MojoExecutionException
function_decl|;
comment|/**      * Return the broker service created.      */
name|BrokerService
name|getBroker
parameter_list|()
function_decl|;
comment|/**      * Set the broker service managed to the one given.      *      * @param broker activemq instance to manage.      */
name|void
name|setBroker
parameter_list|(
name|BrokerService
name|broker
parameter_list|)
function_decl|;
block|}
end_interface

end_unit

