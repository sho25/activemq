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
name|network
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
name|command
operator|.
name|Message
import|;
end_import

begin_comment
comment|/**  * called when a bridge fails  *   *   */
end_comment

begin_interface
specifier|public
interface|interface
name|NetworkBridgeListener
block|{
comment|/**      * called when the transport fails      */
name|void
name|bridgeFailed
parameter_list|()
function_decl|;
comment|/**      * called after the bridge is started.      */
name|void
name|onStart
parameter_list|(
name|NetworkBridge
name|bridge
parameter_list|)
function_decl|;
comment|/**      * called before the bridge is stopped.      */
name|void
name|onStop
parameter_list|(
name|NetworkBridge
name|bridge
parameter_list|)
function_decl|;
comment|/**      * Called when message forwarded over the network      * @param bridge      * @param message      */
name|void
name|onOutboundMessage
parameter_list|(
name|NetworkBridge
name|bridge
parameter_list|,
name|Message
name|message
parameter_list|)
function_decl|;
comment|/**      * Called for when a message arrives over the network      * @param bridge      * @param message      */
name|void
name|onInboundMessage
parameter_list|(
name|NetworkBridge
name|bridge
parameter_list|,
name|Message
name|message
parameter_list|)
function_decl|;
block|}
end_interface

end_unit

