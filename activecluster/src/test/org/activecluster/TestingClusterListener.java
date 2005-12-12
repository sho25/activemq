begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**   *   * Copyright 2005 LogicBlaze, Inc. (http://www.logicblaze.com)  *   * Licensed under the Apache License, Version 2.0 (the "License");   * you may not use this file except in compliance with the License.   * You may obtain a copy of the License at   *   * http://www.apache.org/licenses/LICENSE-2.0  *   * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,   * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.   * See the License for the specific language governing permissions and   * limitations under the License.   *   **/
end_comment

begin_package
package|package
name|org
operator|.
name|activecluster
package|;
end_package

begin_import
import|import
name|org
operator|.
name|activecluster
operator|.
name|impl
operator|.
name|DefaultCluster
import|;
end_import

begin_comment
comment|/**  * @version $Revision: 1.2 $  */
end_comment

begin_class
specifier|public
class|class
name|TestingClusterListener
implements|implements
name|ClusterListener
block|{
specifier|private
name|Cluster
name|cluster
decl_stmt|;
specifier|public
name|TestingClusterListener
parameter_list|(
name|Cluster
name|cluster
parameter_list|)
block|{
name|this
operator|.
name|cluster
operator|=
name|cluster
expr_stmt|;
block|}
specifier|public
name|void
name|onNodeAdd
parameter_list|(
name|ClusterEvent
name|event
parameter_list|)
block|{
name|printEvent
argument_list|(
literal|"ADDED: "
argument_list|,
name|event
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|onNodeUpdate
parameter_list|(
name|ClusterEvent
name|event
parameter_list|)
block|{
name|printEvent
argument_list|(
literal|"UPDATED: "
argument_list|,
name|event
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|onNodeRemoved
parameter_list|(
name|ClusterEvent
name|event
parameter_list|)
block|{
name|printEvent
argument_list|(
literal|"REMOVED: "
argument_list|,
name|event
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|onNodeFailed
parameter_list|(
name|ClusterEvent
name|event
parameter_list|)
block|{
name|printEvent
argument_list|(
literal|"FAILED: "
argument_list|,
name|event
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|onCoordinatorChanged
parameter_list|(
name|ClusterEvent
name|event
parameter_list|)
block|{
name|printEvent
argument_list|(
literal|"COORDINATOR: "
argument_list|,
name|event
argument_list|)
expr_stmt|;
block|}
specifier|protected
name|void
name|printEvent
parameter_list|(
name|String
name|text
parameter_list|,
name|ClusterEvent
name|event
parameter_list|)
block|{
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
name|text
operator|+
name|event
operator|.
name|getNode
argument_list|()
argument_list|)
expr_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"Current cluster is now: "
operator|+
name|cluster
operator|.
name|getNodes
argument_list|()
operator|.
name|keySet
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

