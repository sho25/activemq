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
name|Service
import|;
end_import

begin_comment
comment|/**  * Represents a network bridge interface  *   * @version $Revision: 1.1 $  */
end_comment

begin_interface
specifier|public
interface|interface
name|NetworkBridge
extends|extends
name|Service
block|{
comment|/**      * Service an exception      * @param error      */
specifier|public
name|void
name|serviceRemoteException
parameter_list|(
name|Throwable
name|error
parameter_list|)
function_decl|;
comment|/**      * servicee an exception      * @param error      */
specifier|public
name|void
name|serviceLocalException
parameter_list|(
name|Throwable
name|error
parameter_list|)
function_decl|;
comment|/**      * Set the NetworkBridgeFailedListener      * @param listener      */
specifier|public
name|void
name|setNetworkBridgeFailedListener
parameter_list|(
name|NetworkBridgeFailedListener
name|listener
parameter_list|)
function_decl|;
block|}
end_interface

end_unit

