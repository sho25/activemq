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
name|kahadb
operator|.
name|replication
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
name|kahadb
operator|.
name|replication
operator|.
name|pb
operator|.
name|PBClusterNodeStatus
import|;
end_import

begin_comment
comment|/**  * This interface is used by the ReplicationService to know when  * it should switch between Slave and Master mode.   *   * @author chirino  */
end_comment

begin_interface
specifier|public
interface|interface
name|ClusterStateManager
extends|extends
name|Service
block|{
comment|/**      * Adds a ClusterListener which is used to get notifications      * of chagnes in the cluster state.      * @param listener      */
name|void
name|addListener
parameter_list|(
name|ClusterListener
name|listener
parameter_list|)
function_decl|;
comment|/** 	 * Removes a previously added ClusterListener 	 * @param listener 	 */
name|void
name|removeListener
parameter_list|(
name|ClusterListener
name|listener
parameter_list|)
function_decl|;
comment|/** 	 * Adds a member to the cluster.  Adding a member does not mean he is online. 	 * Some ClusterStateManager may keep track of a persistent memebership list 	 * so that can determine if there are enough nodes online to form a quorum 	 * for the purposes of electing a master. 	 *  	 * @param node 	 */
specifier|public
name|void
name|addMember
parameter_list|(
specifier|final
name|String
name|node
parameter_list|)
function_decl|;
comment|/**      * Removes a previously added member.      *       * @param node      */
specifier|public
name|void
name|removeMember
parameter_list|(
specifier|final
name|String
name|node
parameter_list|)
function_decl|;
comment|/**      * Updates the status of the local node.      *       * @param status      */
specifier|public
name|void
name|setMemberStatus
parameter_list|(
specifier|final
name|PBClusterNodeStatus
name|status
parameter_list|)
function_decl|;
block|}
end_interface

end_unit

