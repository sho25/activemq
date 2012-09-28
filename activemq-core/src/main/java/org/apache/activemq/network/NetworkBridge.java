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
name|javax
operator|.
name|management
operator|.
name|ObjectName
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
name|Service
import|;
end_import

begin_comment
comment|/**  * Represents a network bridge interface  */
end_comment

begin_interface
specifier|public
interface|interface
name|NetworkBridge
extends|extends
name|Service
block|{
comment|/**      * Service an exception received from the Remote Broker connection.      * @param error      */
name|void
name|serviceRemoteException
parameter_list|(
name|Throwable
name|error
parameter_list|)
function_decl|;
comment|/**      * Service an exception received from the Local Broker connection.      * @param error      */
name|void
name|serviceLocalException
parameter_list|(
name|Throwable
name|error
parameter_list|)
function_decl|;
comment|/**      * Set the NetworkBridgeFailedListener      * @param listener      */
name|void
name|setNetworkBridgeListener
parameter_list|(
name|NetworkBridgeListener
name|listener
parameter_list|)
function_decl|;
comment|/**      * @return the network address of the remote broker connection.      */
name|String
name|getRemoteAddress
parameter_list|()
function_decl|;
comment|/**      * @return the name of the remote broker this bridge is connected to.      */
name|String
name|getRemoteBrokerName
parameter_list|()
function_decl|;
comment|/**      * @return the network address of the local broker connection.      */
name|String
name|getLocalAddress
parameter_list|()
function_decl|;
comment|/**      * @return the name of the local broker this bridge is connected to.      */
name|String
name|getLocalBrokerName
parameter_list|()
function_decl|;
comment|/**      * @return the current number of enqueues this bridge has.      */
name|long
name|getEnqueueCounter
parameter_list|()
function_decl|;
comment|/**      * @return the current number of dequeues this bridge has.      */
name|long
name|getDequeueCounter
parameter_list|()
function_decl|;
comment|/**      * @param objectName      *      The ObjectName assigned to this bridge in the MBean server.      */
name|void
name|setMbeanObjectName
parameter_list|(
name|ObjectName
name|objectName
parameter_list|)
function_decl|;
comment|/**      * @return the MBean name used to identify this bridge in the MBean server.      */
name|ObjectName
name|getMbeanObjectName
parameter_list|()
function_decl|;
block|}
end_interface

end_unit

