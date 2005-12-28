begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  *  * Copyright 2004 The Apache Software Foundation  *  * Licensed under the Apache License, Version 2.0 (the "License");  * you may not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  * http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
package|package
name|org
operator|.
name|apache
operator|.
name|activecluster
operator|.
name|election
package|;
end_package

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
name|apache
operator|.
name|activecluster
operator|.
name|Cluster
import|;
end_import

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
comment|/**   *<p><code>Service</code> Used by the Cluster to elect a coordinator.</p>  *  * @version $Revision: 1.2 $  */
end_comment

begin_interface
specifier|public
interface|interface
name|ElectionStrategy
block|{
comment|/**      * Elect a coordinator.       * @param cluster      * @return the elected Node      * @throws JMSException      */
specifier|public
name|Node
name|doElection
parameter_list|(
name|Cluster
name|cluster
parameter_list|)
throws|throws
name|JMSException
function_decl|;
block|}
end_interface

end_unit

