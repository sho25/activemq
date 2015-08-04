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
comment|/**      * Returns the source address for this connection      *      * @return the source address for this connection      */
annotation|@
name|MBeanInfo
argument_list|(
literal|"source address for this connection"
argument_list|)
name|String
name|getRemoteAddress
parameter_list|()
function_decl|;
comment|/**      * Returns the client identifier for this connection      *      * @return the the client identifier for this connection      */
annotation|@
name|MBeanInfo
argument_list|(
literal|"client id for this connection"
argument_list|)
name|String
name|getClientId
parameter_list|()
function_decl|;
comment|/**      * Returns the number of messages to be dispatched to this connection      * @return the  number of messages pending dispatch      */
annotation|@
name|MBeanInfo
argument_list|(
literal|"The number of messages pending dispatch"
argument_list|)
specifier|public
name|int
name|getDispatchQueueSize
parameter_list|()
function_decl|;
comment|/**      * Returns the User Name used to authorize creation of this Connection.      * This value can be null if display of user name information is disabled.      *      * @return the name of the user that created this Connection      */
annotation|@
name|MBeanInfo
argument_list|(
literal|"User Name used to authorize creation of this connection"
argument_list|)
name|String
name|getUserName
parameter_list|()
function_decl|;
comment|/**      * Returns the ObjectNames of all the Consumers created by this Connection.      *      * @return the ObjectNames of all Consumers created by this Connection.      */
annotation|@
name|MBeanInfo
argument_list|(
literal|"The ObjectNames of all Consumers created by this Connection"
argument_list|)
name|ObjectName
index|[]
name|getConsumers
parameter_list|()
function_decl|;
comment|/**      * Returns the ObjectNames of all the Producers created by this Connection.      *      * @return the ObjectNames of all Producers created by this Connection.      */
annotation|@
name|MBeanInfo
argument_list|(
literal|"The ObjectNames of all Producers created by this Connection"
argument_list|)
name|ObjectName
index|[]
name|getProducers
parameter_list|()
function_decl|;
comment|/**      * Returns the number of active transactions established on this Connection.      *      * @return the number of active transactions established on this Connection..      */
annotation|@
name|MBeanInfo
argument_list|(
literal|"The number of active transactions established on this Connection."
argument_list|)
specifier|public
name|int
name|getActiveTransactionCount
parameter_list|()
function_decl|;
comment|/**      * Returns the number of active transactions established on this Connection.      *      * @return the number of active transactions established on this Connection..      */
annotation|@
name|MBeanInfo
argument_list|(
literal|"The age in ms of the oldest active transaction established on this Connection."
argument_list|)
specifier|public
name|Long
name|getOldestActiveTransactionDuration
parameter_list|()
function_decl|;
block|}
end_interface

end_unit

