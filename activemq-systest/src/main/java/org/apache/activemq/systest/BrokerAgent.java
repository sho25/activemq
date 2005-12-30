begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  *  * Copyright 2005-2006 The Apache Software Foundation  *  * Licensed under the Apache License, Version 2.0 (the "License");  * you may not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  * http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
package|package
name|org
operator|.
name|apache
operator|.
name|activemq
operator|.
name|systest
package|;
end_package

begin_import
import|import
name|javax
operator|.
name|jms
operator|.
name|ConnectionFactory
import|;
end_import

begin_comment
comment|/**  *   * @version $Revision: 1.1 $  */
end_comment

begin_interface
specifier|public
interface|interface
name|BrokerAgent
extends|extends
name|Agent
block|{
comment|/**      * Kills the given broker, if possible avoiding a clean shutdown      *       * @throws Exception      */
name|void
name|kill
parameter_list|()
throws|throws
name|Exception
function_decl|;
comment|/**      * Returns the connection factory to connect to this broker      */
name|ConnectionFactory
name|getConnectionFactory
parameter_list|()
function_decl|;
comment|/**      * Returns the connection URI to use to connect to this broker      */
name|String
name|getConnectionURI
parameter_list|()
function_decl|;
comment|/**      * Sets up a network connection to the given broker      * @throws Exception       */
name|void
name|connectTo
parameter_list|(
name|BrokerAgent
name|broker
parameter_list|)
throws|throws
name|Exception
function_decl|;
block|}
end_interface

end_unit

