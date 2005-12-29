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
operator|.
name|impl
package|;
end_package

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|activecluster
operator|.
name|Node
import|;
end_import

begin_comment
comment|/**  * A client side proxy to the remove cluster  *  * @version $Revision: 1.3 $  */
end_comment

begin_interface
specifier|public
interface|interface
name|StateService
block|{
comment|/**      * Sends a keep alive to the cluster      *      * @param node      */
specifier|public
name|void
name|keepAlive
parameter_list|(
name|Node
name|node
parameter_list|)
function_decl|;
comment|/**      * Sends a shutdown message to the cluster      */
specifier|public
name|void
name|shutdown
parameter_list|(
name|Node
name|node
parameter_list|)
function_decl|;
block|}
end_interface

end_unit

