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
name|broker
package|;
end_package

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
name|Map
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
name|command
operator|.
name|ConnectionId
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
name|command
operator|.
name|ConsumerId
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
name|command
operator|.
name|ProducerId
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
name|command
operator|.
name|SessionId
import|;
end_import

begin_comment
comment|/**  *   */
end_comment

begin_interface
specifier|public
interface|interface
name|TransportConnectionStateRegister
block|{
name|TransportConnectionState
name|registerConnectionState
parameter_list|(
name|ConnectionId
name|connectionId
parameter_list|,
name|TransportConnectionState
name|state
parameter_list|)
function_decl|;
name|TransportConnectionState
name|unregisterConnectionState
parameter_list|(
name|ConnectionId
name|connectionId
parameter_list|)
function_decl|;
name|List
argument_list|<
name|TransportConnectionState
argument_list|>
name|listConnectionStates
parameter_list|()
function_decl|;
name|Map
argument_list|<
name|ConnectionId
argument_list|,
name|TransportConnectionState
argument_list|>
name|mapStates
parameter_list|()
function_decl|;
name|TransportConnectionState
name|lookupConnectionState
parameter_list|(
name|String
name|connectionId
parameter_list|)
function_decl|;
name|TransportConnectionState
name|lookupConnectionState
parameter_list|(
name|ConsumerId
name|id
parameter_list|)
function_decl|;
name|TransportConnectionState
name|lookupConnectionState
parameter_list|(
name|ProducerId
name|id
parameter_list|)
function_decl|;
name|TransportConnectionState
name|lookupConnectionState
parameter_list|(
name|SessionId
name|id
parameter_list|)
function_decl|;
name|TransportConnectionState
name|lookupConnectionState
parameter_list|(
name|ConnectionId
name|connectionId
parameter_list|)
function_decl|;
name|boolean
name|isEmpty
parameter_list|()
function_decl|;
name|boolean
name|doesHandleMultipleConnectionStates
parameter_list|()
function_decl|;
name|void
name|intialize
parameter_list|(
name|TransportConnectionStateRegister
name|other
parameter_list|)
function_decl|;
name|void
name|clear
parameter_list|()
function_decl|;
block|}
end_interface

end_unit
