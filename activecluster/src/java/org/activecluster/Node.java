begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  *  * Copyright 2004 The Apache Software Foundation  *  * Licensed under the Apache License, Version 2.0 (the "License");  * you may not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  * http://www.apache.org/licenses/LICENSE-2.0  *   * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,   * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.   * See the License for the specific language governing permissions and   * limitations under the License.   *   **/
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
name|Destination
import|;
end_import

begin_comment
comment|/**  * Represents a node member in a cluster  *  * @version $Revision: 1.3 $  */
end_comment

begin_interface
specifier|public
interface|interface
name|Node
block|{
comment|/**      * Access to the queue to send messages direct to this node.      *      * @return the destination to send messages to this node while its available      */
specifier|public
name|Destination
name|getDestination
parameter_list|()
function_decl|;
comment|/**      * @return an immutable map of the nodes state      */
specifier|public
name|Map
name|getState
parameter_list|()
function_decl|;
comment|/**      * @return the name of the node      */
specifier|public
name|String
name|getName
parameter_list|()
function_decl|;
comment|/**      * @return true if this node has been elected as coordinator      */
specifier|public
name|boolean
name|isCoordinator
parameter_list|()
function_decl|;
comment|/**      * Returns the Zone of this node - typically the DMZ zone or the subnet on which the      * node is on      */
specifier|public
name|Object
name|getZone
parameter_list|()
function_decl|;
block|}
end_interface

end_unit

