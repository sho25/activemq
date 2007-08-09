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
name|filter
package|;
end_package

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|ArrayList
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Collection
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|HashMap
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|HashSet
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
name|List
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
name|java
operator|.
name|util
operator|.
name|Set
import|;
end_import

begin_comment
comment|/**  * An implementation class used to implement {@link DestinationMap}  *   * @version $Revision: 1.2 $  */
end_comment

begin_class
specifier|public
class|class
name|DestinationMapNode
implements|implements
name|DestinationNode
block|{
specifier|protected
specifier|static
specifier|final
name|String
name|ANY_CHILD
init|=
name|DestinationMap
operator|.
name|ANY_CHILD
decl_stmt|;
specifier|protected
specifier|static
specifier|final
name|String
name|ANY_DESCENDENT
init|=
name|DestinationMap
operator|.
name|ANY_DESCENDENT
decl_stmt|;
comment|// we synchornize at the DestinationMap level
specifier|private
name|DestinationMapNode
name|parent
decl_stmt|;
specifier|private
name|List
name|values
init|=
operator|new
name|ArrayList
argument_list|()
decl_stmt|;
specifier|private
name|Map
name|childNodes
init|=
operator|new
name|HashMap
argument_list|()
decl_stmt|;
specifier|private
name|String
name|path
init|=
literal|"Root"
decl_stmt|;
comment|// private DestinationMapNode anyChild;
specifier|private
name|int
name|pathLength
decl_stmt|;
specifier|public
name|DestinationMapNode
parameter_list|(
name|DestinationMapNode
name|parent
parameter_list|)
block|{
name|this
operator|.
name|parent
operator|=
name|parent
expr_stmt|;
if|if
condition|(
name|parent
operator|==
literal|null
condition|)
block|{
name|pathLength
operator|=
literal|0
expr_stmt|;
block|}
else|else
block|{
name|pathLength
operator|=
name|parent
operator|.
name|pathLength
operator|+
literal|1
expr_stmt|;
block|}
block|}
comment|/**      * Returns the child node for the given named path or null if it does not      * exist      */
specifier|public
name|DestinationMapNode
name|getChild
parameter_list|(
name|String
name|path
parameter_list|)
block|{
return|return
operator|(
name|DestinationMapNode
operator|)
name|childNodes
operator|.
name|get
argument_list|(
name|path
argument_list|)
return|;
block|}
comment|/**      * Returns the child nodes      */
specifier|public
name|Collection
name|getChildren
parameter_list|()
block|{
return|return
name|childNodes
operator|.
name|values
argument_list|()
return|;
block|}
specifier|public
name|int
name|getChildCount
parameter_list|()
block|{
return|return
name|childNodes
operator|.
name|size
argument_list|()
return|;
block|}
comment|/**      * Returns the child node for the given named path, lazily creating one if      * it does not yet exist      */
specifier|public
name|DestinationMapNode
name|getChildOrCreate
parameter_list|(
name|String
name|path
parameter_list|)
block|{
name|DestinationMapNode
name|answer
init|=
operator|(
name|DestinationMapNode
operator|)
name|childNodes
operator|.
name|get
argument_list|(
name|path
argument_list|)
decl_stmt|;
if|if
condition|(
name|answer
operator|==
literal|null
condition|)
block|{
name|answer
operator|=
name|createChildNode
argument_list|()
expr_stmt|;
name|answer
operator|.
name|path
operator|=
name|path
expr_stmt|;
name|childNodes
operator|.
name|put
argument_list|(
name|path
argument_list|,
name|answer
argument_list|)
expr_stmt|;
block|}
return|return
name|answer
return|;
block|}
comment|/**      * Returns the node which represents all children (i.e. the * node)      */
comment|// public DestinationMapNode getAnyChildNode() {
comment|// if (anyChild == null) {
comment|// anyChild = createChildNode();
comment|// }
comment|// return anyChild;
comment|// }
comment|/**      * Returns a mutable List of the values available at this node in the tree      */
specifier|public
name|List
name|getValues
parameter_list|()
block|{
return|return
name|values
return|;
block|}
comment|/**      * Returns a mutable List of the values available at this node in the tree      */
specifier|public
name|List
name|removeValues
parameter_list|()
block|{
name|ArrayList
name|v
init|=
operator|new
name|ArrayList
argument_list|(
name|values
argument_list|)
decl_stmt|;
comment|// parent.getAnyChildNode().getValues().removeAll(v);
name|values
operator|.
name|clear
argument_list|()
expr_stmt|;
name|pruneIfEmpty
argument_list|()
expr_stmt|;
return|return
name|v
return|;
block|}
specifier|public
name|Set
name|removeDesendentValues
parameter_list|()
block|{
name|Set
name|answer
init|=
operator|new
name|HashSet
argument_list|()
decl_stmt|;
name|removeDesendentValues
argument_list|(
name|answer
argument_list|)
expr_stmt|;
return|return
name|answer
return|;
block|}
specifier|protected
name|void
name|removeDesendentValues
parameter_list|(
name|Set
name|answer
parameter_list|)
block|{
comment|// if (anyChild != null) {
comment|// anyChild.removeDesendentValues(answer);
comment|// }
name|answer
operator|.
name|addAll
argument_list|(
name|removeValues
argument_list|()
argument_list|)
expr_stmt|;
block|}
comment|/**      * Returns a list of all the values from this node down the tree      */
specifier|public
name|Set
name|getDesendentValues
parameter_list|()
block|{
name|Set
name|answer
init|=
operator|new
name|HashSet
argument_list|()
decl_stmt|;
name|appendDescendantValues
argument_list|(
name|answer
argument_list|)
expr_stmt|;
return|return
name|answer
return|;
block|}
specifier|public
name|void
name|add
parameter_list|(
name|String
index|[]
name|paths
parameter_list|,
name|int
name|idx
parameter_list|,
name|Object
name|value
parameter_list|)
block|{
if|if
condition|(
name|idx
operator|>=
name|paths
operator|.
name|length
condition|)
block|{
name|values
operator|.
name|add
argument_list|(
name|value
argument_list|)
expr_stmt|;
block|}
else|else
block|{
comment|// if (idx == paths.length - 1) {
comment|// getAnyChildNode().getValues().add(value);
comment|// }
comment|// else {
comment|// getAnyChildNode().add(paths, idx + 1, value);
comment|// }
name|getChildOrCreate
argument_list|(
name|paths
index|[
name|idx
index|]
argument_list|)
operator|.
name|add
argument_list|(
name|paths
argument_list|,
name|idx
operator|+
literal|1
argument_list|,
name|value
argument_list|)
expr_stmt|;
block|}
block|}
specifier|public
name|void
name|remove
parameter_list|(
name|String
index|[]
name|paths
parameter_list|,
name|int
name|idx
parameter_list|,
name|Object
name|value
parameter_list|)
block|{
if|if
condition|(
name|idx
operator|>=
name|paths
operator|.
name|length
condition|)
block|{
name|values
operator|.
name|remove
argument_list|(
name|value
argument_list|)
expr_stmt|;
name|pruneIfEmpty
argument_list|()
expr_stmt|;
block|}
else|else
block|{
comment|// if (idx == paths.length - 1) {
comment|// getAnyChildNode().getValues().remove(value);
comment|// }
comment|// else {
comment|// getAnyChildNode().remove(paths, idx + 1, value);
comment|// }
name|getChildOrCreate
argument_list|(
name|paths
index|[
name|idx
index|]
argument_list|)
operator|.
name|remove
argument_list|(
name|paths
argument_list|,
operator|++
name|idx
argument_list|,
name|value
argument_list|)
expr_stmt|;
block|}
block|}
specifier|public
name|void
name|removeAll
parameter_list|(
name|Set
name|answer
parameter_list|,
name|String
index|[]
name|paths
parameter_list|,
name|int
name|startIndex
parameter_list|)
block|{
name|DestinationNode
name|node
init|=
name|this
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
name|startIndex
init|,
name|size
init|=
name|paths
operator|.
name|length
init|;
name|i
operator|<
name|size
operator|&&
name|node
operator|!=
literal|null
condition|;
name|i
operator|++
control|)
block|{
name|String
name|path
init|=
name|paths
index|[
name|i
index|]
decl_stmt|;
if|if
condition|(
name|path
operator|.
name|equals
argument_list|(
name|ANY_DESCENDENT
argument_list|)
condition|)
block|{
name|answer
operator|.
name|addAll
argument_list|(
name|node
operator|.
name|removeDesendentValues
argument_list|()
argument_list|)
expr_stmt|;
break|break;
block|}
name|node
operator|.
name|appendMatchingWildcards
argument_list|(
name|answer
argument_list|,
name|paths
argument_list|,
name|i
argument_list|)
expr_stmt|;
if|if
condition|(
name|path
operator|.
name|equals
argument_list|(
name|ANY_CHILD
argument_list|)
condition|)
block|{
comment|// node = node.getAnyChildNode();
name|node
operator|=
operator|new
name|AnyChildDestinationNode
argument_list|(
name|node
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|node
operator|=
name|node
operator|.
name|getChild
argument_list|(
name|path
argument_list|)
expr_stmt|;
block|}
block|}
if|if
condition|(
name|node
operator|!=
literal|null
condition|)
block|{
name|answer
operator|.
name|addAll
argument_list|(
name|node
operator|.
name|removeValues
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
specifier|public
name|void
name|appendDescendantValues
parameter_list|(
name|Set
name|answer
parameter_list|)
block|{
name|answer
operator|.
name|addAll
argument_list|(
name|values
argument_list|)
expr_stmt|;
comment|// lets add all the children too
name|Iterator
name|iter
init|=
name|childNodes
operator|.
name|values
argument_list|()
operator|.
name|iterator
argument_list|()
decl_stmt|;
while|while
condition|(
name|iter
operator|.
name|hasNext
argument_list|()
condition|)
block|{
name|DestinationNode
name|child
init|=
operator|(
name|DestinationNode
operator|)
name|iter
operator|.
name|next
argument_list|()
decl_stmt|;
name|child
operator|.
name|appendDescendantValues
argument_list|(
name|answer
argument_list|)
expr_stmt|;
block|}
comment|// TODO???
comment|// if (anyChild != null) {
comment|// anyChild.appendDescendantValues(answer);
comment|// }
block|}
comment|/**      * Factory method to create a child node      */
specifier|protected
name|DestinationMapNode
name|createChildNode
parameter_list|()
block|{
return|return
operator|new
name|DestinationMapNode
argument_list|(
name|this
argument_list|)
return|;
block|}
comment|/**      * Matches any entries in the map containing wildcards      */
specifier|public
name|void
name|appendMatchingWildcards
parameter_list|(
name|Set
name|answer
parameter_list|,
name|String
index|[]
name|paths
parameter_list|,
name|int
name|idx
parameter_list|)
block|{
if|if
condition|(
name|idx
operator|-
literal|1
operator|>
name|pathLength
condition|)
block|{
return|return;
block|}
name|DestinationMapNode
name|wildCardNode
init|=
name|getChild
argument_list|(
name|ANY_CHILD
argument_list|)
decl_stmt|;
if|if
condition|(
name|wildCardNode
operator|!=
literal|null
condition|)
block|{
name|wildCardNode
operator|.
name|appendMatchingValues
argument_list|(
name|answer
argument_list|,
name|paths
argument_list|,
name|idx
operator|+
literal|1
argument_list|)
expr_stmt|;
block|}
name|wildCardNode
operator|=
name|getChild
argument_list|(
name|ANY_DESCENDENT
argument_list|)
expr_stmt|;
if|if
condition|(
name|wildCardNode
operator|!=
literal|null
condition|)
block|{
name|answer
operator|.
name|addAll
argument_list|(
name|wildCardNode
operator|.
name|getDesendentValues
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
specifier|public
name|void
name|appendMatchingValues
parameter_list|(
name|Set
name|answer
parameter_list|,
name|String
index|[]
name|paths
parameter_list|,
name|int
name|startIndex
parameter_list|)
block|{
name|DestinationNode
name|node
init|=
name|this
decl_stmt|;
name|boolean
name|couldMatchAny
init|=
literal|true
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
name|startIndex
init|,
name|size
init|=
name|paths
operator|.
name|length
init|;
name|i
operator|<
name|size
operator|&&
name|node
operator|!=
literal|null
condition|;
name|i
operator|++
control|)
block|{
name|String
name|path
init|=
name|paths
index|[
name|i
index|]
decl_stmt|;
if|if
condition|(
name|path
operator|.
name|equals
argument_list|(
name|ANY_DESCENDENT
argument_list|)
condition|)
block|{
name|answer
operator|.
name|addAll
argument_list|(
name|node
operator|.
name|getDesendentValues
argument_list|()
argument_list|)
expr_stmt|;
name|couldMatchAny
operator|=
literal|false
expr_stmt|;
break|break;
block|}
name|node
operator|.
name|appendMatchingWildcards
argument_list|(
name|answer
argument_list|,
name|paths
argument_list|,
name|i
argument_list|)
expr_stmt|;
if|if
condition|(
name|path
operator|.
name|equals
argument_list|(
name|ANY_CHILD
argument_list|)
condition|)
block|{
name|node
operator|=
operator|new
name|AnyChildDestinationNode
argument_list|(
name|node
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|node
operator|=
name|node
operator|.
name|getChild
argument_list|(
name|path
argument_list|)
expr_stmt|;
block|}
block|}
if|if
condition|(
name|node
operator|!=
literal|null
condition|)
block|{
name|answer
operator|.
name|addAll
argument_list|(
name|node
operator|.
name|getValues
argument_list|()
argument_list|)
expr_stmt|;
if|if
condition|(
name|couldMatchAny
condition|)
block|{
comment|// lets allow FOO.BAR to match the FOO.BAR.> entry in the map
name|DestinationNode
name|child
init|=
name|node
operator|.
name|getChild
argument_list|(
name|ANY_DESCENDENT
argument_list|)
decl_stmt|;
if|if
condition|(
name|child
operator|!=
literal|null
condition|)
block|{
name|answer
operator|.
name|addAll
argument_list|(
name|child
operator|.
name|getValues
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|}
specifier|public
name|String
name|getPath
parameter_list|()
block|{
return|return
name|path
return|;
block|}
specifier|protected
name|void
name|pruneIfEmpty
parameter_list|()
block|{
if|if
condition|(
name|parent
operator|!=
literal|null
operator|&&
name|childNodes
operator|.
name|isEmpty
argument_list|()
operator|&&
name|values
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
name|parent
operator|.
name|removeChild
argument_list|(
name|this
argument_list|)
expr_stmt|;
block|}
block|}
specifier|protected
name|void
name|removeChild
parameter_list|(
name|DestinationMapNode
name|node
parameter_list|)
block|{
name|childNodes
operator|.
name|remove
argument_list|(
name|node
operator|.
name|getPath
argument_list|()
argument_list|)
expr_stmt|;
name|pruneIfEmpty
argument_list|()
expr_stmt|;
block|}
block|}
end_class

end_unit

