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
name|org
operator|.
name|apache
operator|.
name|activemq
operator|.
name|Service
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
name|broker
operator|.
name|region
operator|.
name|ConnectorStatistics
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
name|BrokerInfo
import|;
end_import

begin_comment
comment|/**  * A connector creates and manages client connections that talk to the Broker.  *   * @version $Revision: 1.3 $  */
end_comment

begin_interface
specifier|public
interface|interface
name|Connector
extends|extends
name|Service
block|{
comment|/**      * @return brokerInfo      */
name|BrokerInfo
name|getBrokerInfo
parameter_list|()
function_decl|;
comment|/**      * @return the statistics for this connector      */
name|ConnectorStatistics
name|getStatistics
parameter_list|()
function_decl|;
comment|/**      * @return true if update client connections when brokers leave/join a cluster      */
specifier|public
name|boolean
name|isUpdateClusterClients
parameter_list|()
function_decl|;
comment|/**      * @return true if clients should be re-balanced across the cluster      */
specifier|public
name|boolean
name|isRebalanceClusterClients
parameter_list|()
function_decl|;
comment|/**      * Update all the connections with information      * about the connected brokers in the cluster      */
specifier|public
name|void
name|updateClientClusterInfo
parameter_list|()
function_decl|;
comment|/**      * @return true if clients should be updated when      * a broker is removed from a broker      */
specifier|public
name|boolean
name|isUpdateClusterClientsOnRemove
parameter_list|()
function_decl|;
block|}
end_interface

end_unit

