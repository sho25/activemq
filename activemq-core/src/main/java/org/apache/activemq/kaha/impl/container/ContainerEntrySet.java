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
name|kaha
operator|.
name|impl
operator|.
name|container
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

begin_comment
comment|/**  * Set of Map.Entry objects for a container  *   *   */
end_comment

begin_class
specifier|public
class|class
name|ContainerEntrySet
extends|extends
name|ContainerCollectionSupport
implements|implements
name|Set
block|{
name|ContainerEntrySet
parameter_list|(
name|MapContainerImpl
name|container
parameter_list|)
block|{
name|super
argument_list|(
name|container
argument_list|)
expr_stmt|;
block|}
specifier|public
name|boolean
name|contains
parameter_list|(
name|Object
name|o
parameter_list|)
block|{
return|return
name|container
operator|.
name|entrySet
argument_list|()
operator|.
name|contains
argument_list|(
name|o
argument_list|)
return|;
block|}
specifier|public
name|Iterator
name|iterator
parameter_list|()
block|{
return|return
operator|new
name|ContainerEntrySetIterator
argument_list|(
name|container
argument_list|,
name|buildEntrySet
argument_list|()
operator|.
name|iterator
argument_list|()
argument_list|)
return|;
block|}
specifier|public
name|Object
index|[]
name|toArray
parameter_list|()
block|{
return|return
name|buildEntrySet
argument_list|()
operator|.
name|toArray
argument_list|()
return|;
block|}
specifier|public
name|Object
index|[]
name|toArray
parameter_list|(
name|Object
index|[]
name|a
parameter_list|)
block|{
return|return
name|buildEntrySet
argument_list|()
operator|.
name|toArray
argument_list|(
name|a
argument_list|)
return|;
block|}
specifier|public
name|boolean
name|add
parameter_list|(
name|Object
name|o
parameter_list|)
block|{
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|(
literal|"Cannot add here"
argument_list|)
throw|;
block|}
specifier|public
name|boolean
name|remove
parameter_list|(
name|Object
name|o
parameter_list|)
block|{
name|boolean
name|result
init|=
literal|false
decl_stmt|;
if|if
condition|(
name|buildEntrySet
argument_list|()
operator|.
name|remove
argument_list|(
name|o
argument_list|)
condition|)
block|{
name|ContainerMapEntry
name|entry
init|=
operator|(
name|ContainerMapEntry
operator|)
name|o
decl_stmt|;
name|container
operator|.
name|remove
argument_list|(
name|entry
operator|.
name|getKey
argument_list|()
argument_list|)
expr_stmt|;
block|}
return|return
name|result
return|;
block|}
specifier|public
name|boolean
name|containsAll
parameter_list|(
name|Collection
name|c
parameter_list|)
block|{
return|return
name|buildEntrySet
argument_list|()
operator|.
name|containsAll
argument_list|(
name|c
argument_list|)
return|;
block|}
specifier|public
name|boolean
name|addAll
parameter_list|(
name|Collection
name|c
parameter_list|)
block|{
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|(
literal|"Cannot add here"
argument_list|)
throw|;
block|}
specifier|public
name|boolean
name|retainAll
parameter_list|(
name|Collection
name|c
parameter_list|)
block|{
name|List
argument_list|<
name|Object
argument_list|>
name|tmpList
init|=
operator|new
name|ArrayList
argument_list|<
name|Object
argument_list|>
argument_list|()
decl_stmt|;
for|for
control|(
name|Iterator
name|i
init|=
name|c
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
name|o
init|=
name|i
operator|.
name|next
argument_list|()
decl_stmt|;
if|if
condition|(
operator|!
name|contains
argument_list|(
name|o
argument_list|)
condition|)
block|{
name|tmpList
operator|.
name|add
argument_list|(
name|o
argument_list|)
expr_stmt|;
block|}
block|}
name|boolean
name|result
init|=
literal|false
decl_stmt|;
for|for
control|(
name|Iterator
argument_list|<
name|Object
argument_list|>
name|i
init|=
name|tmpList
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
name|result
operator||=
name|remove
argument_list|(
name|i
operator|.
name|next
argument_list|()
argument_list|)
expr_stmt|;
block|}
return|return
name|result
return|;
block|}
specifier|public
name|boolean
name|removeAll
parameter_list|(
name|Collection
name|c
parameter_list|)
block|{
name|boolean
name|result
init|=
literal|true
decl_stmt|;
for|for
control|(
name|Iterator
name|i
init|=
name|c
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
if|if
condition|(
operator|!
name|remove
argument_list|(
name|i
operator|.
name|next
argument_list|()
argument_list|)
condition|)
block|{
name|result
operator|=
literal|false
expr_stmt|;
block|}
block|}
return|return
name|result
return|;
block|}
specifier|public
name|void
name|clear
parameter_list|()
block|{
name|container
operator|.
name|clear
argument_list|()
expr_stmt|;
block|}
specifier|protected
name|Set
argument_list|<
name|ContainerMapEntry
argument_list|>
name|buildEntrySet
parameter_list|()
block|{
name|Set
argument_list|<
name|ContainerMapEntry
argument_list|>
name|set
init|=
operator|new
name|HashSet
argument_list|<
name|ContainerMapEntry
argument_list|>
argument_list|()
decl_stmt|;
for|for
control|(
name|Iterator
name|i
init|=
name|container
operator|.
name|keySet
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
name|ContainerMapEntry
name|entry
init|=
operator|new
name|ContainerMapEntry
argument_list|(
name|container
argument_list|,
name|i
operator|.
name|next
argument_list|()
argument_list|)
decl_stmt|;
name|set
operator|.
name|add
argument_list|(
name|entry
argument_list|)
expr_stmt|;
block|}
return|return
name|set
return|;
block|}
block|}
end_class

end_unit

