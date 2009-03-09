begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *      http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
package|package
name|org
operator|.
name|apache
operator|.
name|activemq
operator|.
name|web
operator|.
name|config
package|;
end_package

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Collection
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|jms
operator|.
name|ConnectionFactory
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|management
operator|.
name|remote
operator|.
name|JMXServiceURL
import|;
end_import

begin_comment
comment|/**  * The configuration used for the web console.  *   * @version $Revision: $  */
end_comment

begin_interface
specifier|public
interface|interface
name|WebConsoleConfiguration
block|{
comment|/** 	 * The connection factory to use for sending/receiving messages. 	 *  	 * @return not<code>null</code> 	 */
name|ConnectionFactory
name|getConnectionFactory
parameter_list|()
function_decl|;
comment|/** 	 * The URL to the JMX connectors of the broker. The names of any failover 	 * (master-slave configuration) must also be specified. 	 *  	 * @return not<code>null</code>, must contain at least one entry 	 */
name|Collection
argument_list|<
name|JMXServiceURL
argument_list|>
name|getJmxUrls
parameter_list|()
function_decl|;
comment|/** 	 * The user that is used in case of authenticated JMX connections. The user 	 * must be the same for all the brokers. 	 *  	 * @return<code>null</code> if no authentication should be used. 	 */
name|String
name|getJmxUser
parameter_list|()
function_decl|;
comment|/** 	 * Password for the JMX-user. 	 *  	 * @see #getJmxUser() 	 * @return<code>null</code> if no authentication 	 */
name|String
name|getJmxPassword
parameter_list|()
function_decl|;
block|}
end_interface

end_unit

