begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  *  * Copyright 2004 The Apache Software Foundation  *  * Licensed under the Apache License, Version 2.0 (the "License");  * you may not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  * http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
package|package
name|org
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
name|Set
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|SortedSet
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|TreeSet
import|;
end_import

begin_import
import|import
name|org
operator|.
name|activemq
operator|.
name|command
operator|.
name|ActiveMQDestination
import|;
end_import

begin_comment
comment|/**  * A Map-like data structure allowing values to be indexed by {@link ActiveMQDestination}  * and retrieved by destination - supporting both * and&gt; style of wildcard  * as well as composite destinations.  *<br>  * This class assumes that the index changes rarely but that fast lookup into the index is required.  * So this class maintains a pre-calculated index for destination steps. So looking up the values  * for "TEST.*" or "*.TEST" will be pretty fast.  *<br>  * Looking up of a value could return a single value or a List of matching values if a wildcard or  * composite destination is used.  *  * @version $Revision: 1.3 $  */
end_comment

begin_class
specifier|public
class|class
name|DestinationMap
block|{
specifier|private
name|DestinationMapNode
name|rootNode
init|=
operator|new
name|DestinationMapNode
argument_list|(
literal|null
argument_list|)
decl_stmt|;
specifier|protected
specifier|static
specifier|final
name|String
name|ANY_DESCENDENT
init|=
name|DestinationFilter
operator|.
name|ANY_DESCENDENT
decl_stmt|;
specifier|protected
specifier|static
specifier|final
name|String
name|ANY_CHILD
init|=
name|DestinationFilter
operator|.
name|ANY_CHILD
decl_stmt|;
comment|/**      * Looks up the value(s) matching the given Destination key. For simple destinations      * this is typically a List of one single value, for wildcards or composite destinations this will typically be      * a List of matching values.      *      * @param key the destination to lookup      * @return a List of matching values or an empty list if there are no matching values.      */
specifier|public
specifier|synchronized
name|Set
name|get
parameter_list|(
name|ActiveMQDestination
name|key
parameter_list|)
block|{
if|if
condition|(
name|key
operator|.
name|isComposite
argument_list|()
condition|)
block|{
name|ActiveMQDestination
index|[]
name|destinations
init|=
name|key
operator|.
name|getCompositeDestinations
argument_list|()
decl_stmt|;
name|Set
name|answer
init|=
operator|new
name|HashSet
argument_list|(
name|destinations
operator|.
name|length
argument_list|)
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|destinations
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|ActiveMQDestination
name|childDestination
init|=
name|destinations
index|[
name|i
index|]
decl_stmt|;
name|Object
name|value
init|=
name|get
argument_list|(
name|childDestination
argument_list|)
decl_stmt|;
if|if
condition|(
name|value
operator|instanceof
name|Set
condition|)
block|{
name|answer
operator|.
name|addAll
argument_list|(
operator|(
name|Set
operator|)
name|value
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|value
operator|!=
literal|null
condition|)
block|{
name|answer
operator|.
name|add
argument_list|(
name|value
argument_list|)
expr_stmt|;
block|}
block|}
return|return
name|answer
return|;
block|}
return|return
name|findWildcardMatches
argument_list|(
name|key
argument_list|)
return|;
block|}
specifier|public
specifier|synchronized
name|void
name|put
parameter_list|(
name|ActiveMQDestination
name|key
parameter_list|,
name|Object
name|value
parameter_list|)
block|{
if|if
condition|(
name|key
operator|.
name|isComposite
argument_list|()
condition|)
block|{
name|ActiveMQDestination
index|[]
name|destinations
init|=
name|key
operator|.
name|getCompositeDestinations
argument_list|()
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|destinations
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|ActiveMQDestination
name|childDestination
init|=
name|destinations
index|[
name|i
index|]
decl_stmt|;
name|put
argument_list|(
name|childDestination
argument_list|,
name|value
argument_list|)
expr_stmt|;
block|}
return|return;
block|}
name|String
index|[]
name|paths
init|=
name|key
operator|.
name|getDestinationPaths
argument_list|()
decl_stmt|;
name|rootNode
operator|.
name|add
argument_list|(
name|paths
argument_list|,
literal|0
argument_list|,
name|value
argument_list|)
expr_stmt|;
block|}
comment|/**      * Removes the value from the associated destination      */
specifier|public
specifier|synchronized
name|void
name|remove
parameter_list|(
name|ActiveMQDestination
name|key
parameter_list|,
name|Object
name|value
parameter_list|)
block|{
if|if
condition|(
name|key
operator|.
name|isComposite
argument_list|()
condition|)
block|{
name|ActiveMQDestination
index|[]
name|destinations
init|=
name|key
operator|.
name|getCompositeDestinations
argument_list|()
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|destinations
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|ActiveMQDestination
name|childDestination
init|=
name|destinations
index|[
name|i
index|]
decl_stmt|;
name|remove
argument_list|(
name|childDestination
argument_list|,
name|value
argument_list|)
expr_stmt|;
block|}
return|return;
block|}
name|String
index|[]
name|paths
init|=
name|key
operator|.
name|getDestinationPaths
argument_list|()
decl_stmt|;
name|rootNode
operator|.
name|remove
argument_list|(
name|paths
argument_list|,
literal|0
argument_list|,
name|value
argument_list|)
expr_stmt|;
block|}
specifier|public
name|int
name|getRootChildCount
parameter_list|()
block|{
return|return
name|rootNode
operator|.
name|getChildCount
argument_list|()
return|;
block|}
comment|// Implementation methods
comment|//-------------------------------------------------------------------------
comment|/**      * A helper method to allow the destination map to be populated from a dependency injection      * framework such as Spring      */
specifier|protected
name|void
name|setEntries
parameter_list|(
name|List
name|entries
parameter_list|)
block|{
for|for
control|(
name|Iterator
name|iter
init|=
name|entries
operator|.
name|iterator
argument_list|()
init|;
name|iter
operator|.
name|hasNext
argument_list|()
condition|;
control|)
block|{
name|Object
name|element
init|=
operator|(
name|Object
operator|)
name|iter
operator|.
name|next
argument_list|()
decl_stmt|;
name|Class
name|type
init|=
name|getEntryClass
argument_list|()
decl_stmt|;
if|if
condition|(
name|type
operator|.
name|isInstance
argument_list|(
name|element
argument_list|)
condition|)
block|{
name|DestinationMapEntry
name|entry
init|=
operator|(
name|DestinationMapEntry
operator|)
name|element
decl_stmt|;
name|put
argument_list|(
name|entry
operator|.
name|getDestination
argument_list|()
argument_list|,
name|entry
operator|.
name|getValue
argument_list|()
argument_list|)
expr_stmt|;
block|}
else|else
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"Each entry must be an instance of type: "
operator|+
name|type
operator|.
name|getName
argument_list|()
operator|+
literal|" but was: "
operator|+
name|element
argument_list|)
throw|;
block|}
block|}
block|}
comment|/**      * Returns the type of the allowed entries which can be set via the {@link #setEntries(List)} method.      * This allows derived classes to further restrict the type of allowed entries to make a type safe       * destination map for custom policies.      */
specifier|protected
name|Class
name|getEntryClass
parameter_list|()
block|{
return|return
name|DestinationMapEntry
operator|.
name|class
return|;
block|}
specifier|protected
name|Set
name|findWildcardMatches
parameter_list|(
name|ActiveMQDestination
name|key
parameter_list|)
block|{
name|String
index|[]
name|paths
init|=
name|key
operator|.
name|getDestinationPaths
argument_list|()
decl_stmt|;
name|Set
name|answer
init|=
operator|new
name|HashSet
argument_list|()
decl_stmt|;
name|rootNode
operator|.
name|appendMatchingValues
argument_list|(
name|answer
argument_list|,
name|paths
argument_list|,
literal|0
argument_list|)
expr_stmt|;
return|return
name|answer
return|;
block|}
comment|/**      * @param dest      */
specifier|public
name|void
name|removeAll
parameter_list|(
name|ActiveMQDestination
name|key
parameter_list|)
block|{
if|if
condition|(
name|key
operator|.
name|isComposite
argument_list|()
condition|)
block|{
name|ActiveMQDestination
index|[]
name|destinations
init|=
name|key
operator|.
name|getCompositeDestinations
argument_list|()
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|destinations
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|removeAll
argument_list|(
name|destinations
index|[
name|i
index|]
argument_list|)
expr_stmt|;
block|}
return|return;
block|}
name|String
index|[]
name|paths
init|=
name|key
operator|.
name|getDestinationPaths
argument_list|()
decl_stmt|;
name|rootNode
operator|.
name|removeAll
argument_list|(
name|paths
argument_list|,
literal|0
argument_list|)
expr_stmt|;
block|}
comment|/**      * Returns the value which matches the given destination or null if there is no matching      * value. If there are multiple values, the results are sorted and the last item (the biggest)      * is returned.      *       * @param destination the destination to find the value for      * @return the largest matching value or null if no value matches      */
specifier|public
name|Object
name|chooseValue
parameter_list|(
name|ActiveMQDestination
name|destination
parameter_list|)
block|{
name|Set
name|set
init|=
name|get
argument_list|(
name|destination
argument_list|)
decl_stmt|;
if|if
condition|(
name|set
operator|==
literal|null
operator|||
name|set
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
return|return
literal|null
return|;
block|}
name|SortedSet
name|sortedSet
init|=
operator|new
name|TreeSet
argument_list|(
name|set
argument_list|)
decl_stmt|;
return|return
name|sortedSet
operator|.
name|last
argument_list|()
return|;
block|}
block|}
end_class

end_unit

