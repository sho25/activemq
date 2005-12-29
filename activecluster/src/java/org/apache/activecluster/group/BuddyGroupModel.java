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
name|group
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
comment|/**  * A kind of {@link GroupModel} in which every {@link Node} has its  * own {@link Group} and other nodes in the cluster act as buddies (slaves)  *  * @version $Revision: 1.2 $  */
end_comment

begin_class
specifier|public
class|class
name|BuddyGroupModel
extends|extends
name|GroupModel
block|{
specifier|public
specifier|synchronized
name|void
name|addNode
parameter_list|(
name|Node
name|node
parameter_list|)
block|{
name|Group
name|group
init|=
name|makeNewGroup
argument_list|(
name|node
argument_list|)
decl_stmt|;
if|if
condition|(
name|group
operator|==
literal|null
condition|)
block|{
if|if
condition|(
operator|!
name|addToExistingGroup
argument_list|(
name|node
argument_list|)
condition|)
block|{
name|addToUnusedNodes
argument_list|(
name|node
argument_list|)
expr_stmt|;
block|}
block|}
else|else
block|{
comment|// now lets try choose some existing nodes to add as buddy's
name|tryToFillGroupWithBuddies
argument_list|(
name|group
argument_list|)
expr_stmt|;
comment|// now that the group may well be filled, add it to the collections
name|addGroup
argument_list|(
name|group
argument_list|)
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

