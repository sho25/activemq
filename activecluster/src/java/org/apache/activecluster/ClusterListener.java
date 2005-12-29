begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  *  * Copyright 2005-2006 The Apache Software Foundation  *  * Licensed under the Apache License, Version 2.0 (the "License");  * you may not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  * http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
package|package
name|org
operator|.
name|apache
operator|.
name|activecluster
package|;
end_package

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|EventListener
import|;
end_import

begin_comment
comment|/**  * Listener to events occuring on the cluster  *  * @version $Revision: 1.2 $  */
end_comment

begin_interface
specifier|public
interface|interface
name|ClusterListener
extends|extends
name|EventListener
block|{
comment|/**      * A new node has been added      *      * @param event      */
specifier|public
name|void
name|onNodeAdd
parameter_list|(
name|ClusterEvent
name|event
parameter_list|)
function_decl|;
comment|/**      * A node has updated its state      *      * @param event      */
specifier|public
name|void
name|onNodeUpdate
parameter_list|(
name|ClusterEvent
name|event
parameter_list|)
function_decl|;
comment|/**      * A node has been removed (a clean shutdown)      *      * @param event      */
specifier|public
name|void
name|onNodeRemoved
parameter_list|(
name|ClusterEvent
name|event
parameter_list|)
function_decl|;
comment|/**      * A node has failed due to process or network failure      *      * @param event      */
specifier|public
name|void
name|onNodeFailed
parameter_list|(
name|ClusterEvent
name|event
parameter_list|)
function_decl|;
comment|/**      * An election has occurred and a new coordinator has been selected      * @param event      */
specifier|public
name|void
name|onCoordinatorChanged
parameter_list|(
name|ClusterEvent
name|event
parameter_list|)
function_decl|;
block|}
end_interface

end_unit

