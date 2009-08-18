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
operator|.
name|jmx
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

begin_interface
specifier|public
interface|interface
name|ConnectionViewMBean
extends|extends
name|Service
block|{
comment|/**      * @return true if the Connection is slow      */
annotation|@
name|MBeanInfo
argument_list|(
literal|"Connection is slow."
argument_list|)
name|boolean
name|isSlow
parameter_list|()
function_decl|;
comment|/**      * @return if after being marked, the Connection is still writing      */
annotation|@
name|MBeanInfo
argument_list|(
literal|"Connection is blocked."
argument_list|)
name|boolean
name|isBlocked
parameter_list|()
function_decl|;
comment|/**      * @return true if the Connection is connected      */
annotation|@
name|MBeanInfo
argument_list|(
literal|"Connection is connected to the broker."
argument_list|)
name|boolean
name|isConnected
parameter_list|()
function_decl|;
comment|/**      * @return true if the Connection is active      */
annotation|@
name|MBeanInfo
argument_list|(
literal|"Connection is active (both connected and receiving messages)."
argument_list|)
name|boolean
name|isActive
parameter_list|()
function_decl|;
comment|/**      * Resets the statistics      */
annotation|@
name|MBeanInfo
argument_list|(
literal|"Resets the statistics"
argument_list|)
name|void
name|resetStatistics
parameter_list|()
function_decl|;
comment|/**      * Returns the source address for this connection      *       * @return the souce address for this connection      */
annotation|@
name|MBeanInfo
argument_list|(
literal|"Source address for this connection"
argument_list|)
name|String
name|getRemoteAddress
parameter_list|()
function_decl|;
block|}
end_interface

end_unit

