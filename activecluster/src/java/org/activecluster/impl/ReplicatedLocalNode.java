begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**   *   * Copyright 2005 LogicBlaze, Inc. (http://www.logicblaze.com)  *   * Licensed under the Apache License, Version 2.0 (the "License");   * you may not use this file except in compliance with the License.   * You may obtain a copy of the License at   *   * http://www.apache.org/licenses/LICENSE-2.0  *   * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,   * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.   * See the License for the specific language governing permissions and   * limitations under the License.   *   **/
end_comment

begin_package
package|package
name|org
operator|.
name|activecluster
operator|.
name|impl
package|;
end_package

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
name|javax
operator|.
name|jms
operator|.
name|JMSException
import|;
end_import

begin_import
import|import
name|org
operator|.
name|activecluster
operator|.
name|LocalNode
import|;
end_import

begin_import
import|import
name|org
operator|.
name|activecluster
operator|.
name|Service
import|;
end_import

begin_comment
comment|/**  * Default implementation of a local Node which has its  * state replicated across the cluster  *  * @version $Revision: 1.3 $  */
end_comment

begin_class
specifier|public
class|class
name|ReplicatedLocalNode
extends|extends
name|NodeImpl
implements|implements
name|LocalNode
implements|,
name|Service
block|{
comment|/**      *       */
specifier|private
specifier|static
specifier|final
name|long
name|serialVersionUID
init|=
literal|4626381612145333540L
decl_stmt|;
specifier|private
name|StateService
name|serviceStub
decl_stmt|;
comment|/**      * Create ReplicatedLocalNode      * @param name      * @param destination      * @param serviceStub      */
specifier|public
name|ReplicatedLocalNode
parameter_list|(
name|String
name|name
parameter_list|,
name|String
name|destination
parameter_list|,
name|StateService
name|serviceStub
parameter_list|)
block|{
name|super
argument_list|(
name|name
argument_list|,
name|destination
argument_list|)
expr_stmt|;
name|this
operator|.
name|serviceStub
operator|=
name|serviceStub
expr_stmt|;
block|}
comment|/**      * Set the State of the local node      * @param state       */
specifier|public
name|void
name|setState
parameter_list|(
name|Map
name|state
parameter_list|)
block|{
name|super
operator|.
name|setState
argument_list|(
name|state
argument_list|)
expr_stmt|;
name|serviceStub
operator|.
name|keepAlive
argument_list|(
name|this
argument_list|)
expr_stmt|;
block|}
comment|/**      * ping remote nodes      *      */
specifier|public
name|void
name|pingRemoteNodes
parameter_list|()
block|{
name|serviceStub
operator|.
name|keepAlive
argument_list|(
name|this
argument_list|)
expr_stmt|;
block|}
comment|/**      * start (lifecycle)      * @throws JMSException       */
specifier|public
name|void
name|start
parameter_list|()
throws|throws
name|JMSException
block|{     }
comment|/**      * stop (lifecycle)      * @throws JMSException       */
specifier|public
name|void
name|stop
parameter_list|()
throws|throws
name|JMSException
block|{     }
block|}
end_class

end_unit

