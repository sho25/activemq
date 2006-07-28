begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  *  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  * http://www.apache.org/licenses/LICENSE-2.0  *   * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,   * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.   * See the License for the specific language governing permissions and   * limitations under the License.   *   **/
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

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|activecluster
operator|.
name|LocalNode
import|;
end_import

begin_comment
comment|/**  * Default implementation of a local Node which doesn't  * have its state replicated  *   * @version $Revision: 1.4 $  */
end_comment

begin_class
specifier|public
class|class
name|NonReplicatedLocalNode
extends|extends
name|NodeImpl
implements|implements
name|LocalNode
block|{
specifier|private
specifier|static
specifier|final
name|long
name|serialVersionUID
init|=
literal|2525565639637967143L
decl_stmt|;
comment|/**      * Create a Non-replicated local node      * @param name      * @param destination      */
specifier|public
name|NonReplicatedLocalNode
parameter_list|(
name|String
name|name
parameter_list|,
name|Destination
name|destination
parameter_list|)
block|{
name|super
argument_list|(
name|name
argument_list|,
name|destination
argument_list|)
expr_stmt|;
block|}
comment|/**      * Set the local state      * @param state       */
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
block|}
comment|/**      * Shouldn't be called for non-replicated local nodes      */
specifier|public
name|void
name|pingRemoteNodes
parameter_list|()
block|{
throw|throw
operator|new
name|RuntimeException
argument_list|(
literal|"Non-Replicated Local Node should not distribute it's state!"
argument_list|)
throw|;
block|}
block|}
end_class

end_unit

