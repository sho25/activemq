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
name|junit
operator|.
name|framework
operator|.
name|TestCase
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|jms
operator|.
name|JMSException
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Iterator
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
name|activecluster
operator|.
name|impl
operator|.
name|ActiveMQClusterFactory
import|;
end_import

begin_comment
comment|/**  * @version $Revision: 1.2 $  */
end_comment

begin_class
specifier|public
class|class
name|TestSupport
extends|extends
name|TestCase
block|{
specifier|protected
name|String
name|dumpConnectedNodes
parameter_list|(
name|Map
name|nodes
parameter_list|)
block|{
name|String
name|result
init|=
literal|""
decl_stmt|;
for|for
control|(
name|Iterator
name|i
init|=
name|nodes
operator|.
name|values
argument_list|()
operator|.
name|iterator
argument_list|()
init|;
name|i
operator|.
name|hasNext
argument_list|()
condition|;
control|)
block|{
name|Object
name|value
init|=
name|i
operator|.
name|next
argument_list|()
decl_stmt|;
if|if
condition|(
name|value
operator|instanceof
name|Node
condition|)
block|{
name|Node
name|node
init|=
operator|(
name|Node
operator|)
name|value
decl_stmt|;
name|result
operator|+=
name|node
operator|.
name|getName
argument_list|()
operator|+
literal|","
expr_stmt|;
block|}
else|else
block|{
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"Got node of type: "
operator|+
name|value
operator|.
name|getClass
argument_list|()
argument_list|)
expr_stmt|;
name|result
operator|+=
name|value
operator|+
literal|","
expr_stmt|;
block|}
block|}
return|return
name|result
return|;
block|}
specifier|protected
name|Cluster
name|createCluster
parameter_list|()
throws|throws
name|JMSException
throws|,
name|ClusterException
block|{
name|ClusterFactory
name|factory
init|=
operator|new
name|ActiveMQClusterFactory
argument_list|()
decl_stmt|;
return|return
name|factory
operator|.
name|createCluster
argument_list|(
literal|"ORG.CODEHAUS.ACTIVEMQ.TEST.CLUSTER"
argument_list|)
return|;
block|}
specifier|protected
name|Cluster
name|createCluster
parameter_list|(
name|String
name|name
parameter_list|)
throws|throws
name|JMSException
throws|,
name|ClusterException
block|{
name|Cluster
name|cluster
init|=
name|createCluster
argument_list|()
decl_stmt|;
return|return
name|cluster
return|;
block|}
block|}
end_class

end_unit

