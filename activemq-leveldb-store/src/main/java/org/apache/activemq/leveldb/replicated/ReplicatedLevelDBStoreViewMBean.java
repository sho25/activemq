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
name|leveldb
operator|.
name|replicated
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
name|broker
operator|.
name|jmx
operator|.
name|MBeanInfo
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|management
operator|.
name|openmbean
operator|.
name|CompositeData
import|;
end_import

begin_comment
comment|/**  *<p>  *</p>  *  * @author<a href="http://hiramchirino.com">Hiram Chirino</a>  */
end_comment

begin_interface
specifier|public
interface|interface
name|ReplicatedLevelDBStoreViewMBean
block|{
annotation|@
name|MBeanInfo
argument_list|(
literal|"The address of the ZooKeeper server."
argument_list|)
name|String
name|getZkAddress
parameter_list|()
function_decl|;
annotation|@
name|MBeanInfo
argument_list|(
literal|"The path in ZooKeeper to hold master elections."
argument_list|)
name|String
name|getZkPath
parameter_list|()
function_decl|;
annotation|@
name|MBeanInfo
argument_list|(
literal|"The ZooKeeper session timeout."
argument_list|)
name|String
name|getZkSessionTmeout
parameter_list|()
function_decl|;
annotation|@
name|MBeanInfo
argument_list|(
literal|"The address and port the master will bind for the replication protocol."
argument_list|)
name|String
name|getBind
parameter_list|()
function_decl|;
annotation|@
name|MBeanInfo
argument_list|(
literal|"The number of replication nodes that will be part of the replication cluster."
argument_list|)
name|int
name|getReplicas
parameter_list|()
function_decl|;
annotation|@
name|MBeanInfo
argument_list|(
literal|"The role of this node in the replication cluster."
argument_list|)
name|String
name|getNodeRole
parameter_list|()
function_decl|;
annotation|@
name|MBeanInfo
argument_list|(
literal|"The replication status."
argument_list|)
name|String
name|getStatus
parameter_list|()
function_decl|;
annotation|@
name|MBeanInfo
argument_list|(
literal|"The status of the connected slaves."
argument_list|)
name|CompositeData
index|[]
name|getSlaves
parameter_list|()
function_decl|;
annotation|@
name|MBeanInfo
argument_list|(
literal|"The current position of the replication log."
argument_list|)
name|Long
name|getPosition
parameter_list|()
function_decl|;
annotation|@
name|MBeanInfo
argument_list|(
literal|"When the last entry was added to the replication log."
argument_list|)
name|Long
name|getPositionDate
parameter_list|()
function_decl|;
annotation|@
name|MBeanInfo
argument_list|(
literal|"The directory holding the data."
argument_list|)
name|String
name|getDirectory
parameter_list|()
function_decl|;
annotation|@
name|MBeanInfo
argument_list|(
literal|"The sync strategy to use."
argument_list|)
name|String
name|getSync
parameter_list|()
function_decl|;
annotation|@
name|MBeanInfo
argument_list|(
literal|"The node id of this replication node."
argument_list|)
name|String
name|getNodeId
parameter_list|()
function_decl|;
block|}
end_interface

end_unit

