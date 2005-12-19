begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  *   * Copyright 2005 LogicBlaze, Inc. http://www.logicblaze.com  *   * Licensed under the Apache License, Version 2.0 (the "License");   * you may not use this file except in compliance with the License.   * You may obtain a copy of the License at   *   * http://www.apache.org/licenses/LICENSE-2.0  *   * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,   * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.   * See the License for the specific language governing permissions and   * limitations under the License.   *   **/
end_comment

begin_package
package|package
name|org
operator|.
name|activemq
operator|.
name|broker
operator|.
name|region
operator|.
name|policy
package|;
end_package

begin_import
import|import
name|org
operator|.
name|activemq
operator|.
name|command
operator|.
name|ActiveMQDestination
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|jms
operator|.
name|MessageListener
import|;
end_import

begin_comment
comment|/**  * Represents some kind of query which will load messages from some source.  *   * @version $Revision$  */
end_comment

begin_interface
specifier|public
interface|interface
name|MessageQuery
block|{
comment|/**      * Executes the query for messages; each message is passed into the listener      *       * @param destination the destination on which the query is to be performed      * @param listener is the listener to notify as each message is created or loaded      */
specifier|public
name|void
name|execute
parameter_list|(
name|ActiveMQDestination
name|destination
parameter_list|,
name|MessageListener
name|listener
parameter_list|)
function_decl|;
block|}
end_interface

end_unit

