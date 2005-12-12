begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/** *<a href="http://activemq.org">ActiveMQ: The Open Source Message Fabric</a> * * Copyright 2005 (C) LogicBlaze, Inc. http://www.logicblaze.com * * Licensed under the Apache License, Version 2.0 (the "License"); * you may not use this file except in compliance with the License. * You may obtain a copy of the License at * * http://www.apache.org/licenses/LICENSE-2.0 * * Unless required by applicable law or agreed to in writing, software * distributed under the License is distributed on an "AS IS" BASIS, * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. * See the License for the specific language governing permissions and * limitations under the License. * **/
end_comment

begin_package
package|package
name|org
operator|.
name|activemq
operator|.
name|command
package|;
end_package

begin_import
import|import
name|org
operator|.
name|activemq
operator|.
name|state
operator|.
name|CommandVisitor
import|;
end_import

begin_comment
comment|/**  * The Command Pattern so that we can send and receive commands  * on the different transports  *  * @version $Revision: 1.7 $  */
end_comment

begin_interface
specifier|public
interface|interface
name|Command
extends|extends
name|DataStructure
block|{
name|void
name|setCommandId
parameter_list|(
name|short
name|value
parameter_list|)
function_decl|;
comment|/**      * @return the unique ID of this request used to map responses to requests      */
name|short
name|getCommandId
parameter_list|()
function_decl|;
name|void
name|setResponseRequired
parameter_list|(
name|boolean
name|responseRequired
parameter_list|)
function_decl|;
name|boolean
name|isResponseRequired
parameter_list|()
function_decl|;
name|boolean
name|isResponse
parameter_list|()
function_decl|;
name|boolean
name|isMessageDispatch
parameter_list|()
function_decl|;
name|boolean
name|isBrokerInfo
parameter_list|()
function_decl|;
name|boolean
name|isWireFormatInfo
parameter_list|()
function_decl|;
name|boolean
name|isMessage
parameter_list|()
function_decl|;
name|Response
name|visit
parameter_list|(
name|CommandVisitor
name|visitor
parameter_list|)
throws|throws
name|Throwable
function_decl|;
block|}
end_interface

end_unit

