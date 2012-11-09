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
name|index
operator|.
name|IndexItem
import|;
end_import

begin_import
import|import
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
name|index
operator|.
name|IndexLinkedList
import|;
end_import

begin_comment
comment|/**  * Values collection for the MapContainer  *   *   */
end_comment

begin_class
class|class
name|ContainerValueCollection
extends|extends
name|ContainerCollectionSupport
implements|implements
name|Collection
block|{
name|ContainerValueCollection
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
name|containsValue
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
name|IndexLinkedList
name|list
init|=
name|container
operator|.
name|getItemList
argument_list|()
decl_stmt|;
return|return
operator|new
name|ContainerValueCollectionIterator
argument_list|(
name|container
argument_list|,
name|list
argument_list|,
name|list
operator|.
name|getRoot
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
name|Object
index|[]
name|result
init|=
literal|null
decl_stmt|;
name|IndexLinkedList
name|list
init|=
name|container
operator|.
name|getItemList
argument_list|()
decl_stmt|;
synchronized|synchronized
init|(
name|list
init|)
block|{
name|result
operator|=
operator|new
name|Object
index|[
name|list
operator|.
name|size
argument_list|()
index|]
expr_stmt|;
name|IndexItem
name|item
init|=
name|list
operator|.
name|getFirst
argument_list|()
decl_stmt|;
name|int
name|count
init|=
literal|0
decl_stmt|;
while|while
condition|(
name|item
operator|!=
literal|null
condition|)
block|{
name|Object
name|value
init|=
name|container
operator|.
name|getValue
argument_list|(
name|item
argument_list|)
decl_stmt|;
name|result
index|[
name|count
operator|++
index|]
operator|=
name|value
expr_stmt|;
name|item
operator|=
name|list
operator|.
name|getNextEntry
argument_list|(
name|item
argument_list|)
expr_stmt|;
block|}
block|}
return|return
name|result
return|;
block|}
specifier|public
name|Object
index|[]
name|toArray
parameter_list|(
name|Object
index|[]
name|result
parameter_list|)
block|{
name|IndexLinkedList
name|list
init|=
name|container
operator|.
name|getItemList
argument_list|()
decl_stmt|;
synchronized|synchronized
init|(
name|list
init|)
block|{
if|if
condition|(
name|result
operator|.
name|length
operator|<=
name|list
operator|.
name|size
argument_list|()
condition|)
block|{
name|IndexItem
name|item
init|=
name|list
operator|.
name|getFirst
argument_list|()
decl_stmt|;
name|int
name|count
init|=
literal|0
decl_stmt|;
while|while
condition|(
name|item
operator|!=
literal|null
condition|)
block|{
name|Object
name|value
init|=
name|container
operator|.
name|getValue
argument_list|(
name|item
argument_list|)
decl_stmt|;
name|result
index|[
name|count
operator|++
index|]
operator|=
name|value
expr_stmt|;
name|item
operator|=
name|list
operator|.
name|getNextEntry
argument_list|(
name|item
argument_list|)
expr_stmt|;
block|}
block|}
block|}
return|return
name|result
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
literal|"Can't add an object here"
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
return|return
name|container
operator|.
name|removeValue
argument_list|(
name|o
argument_list|)
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
name|boolean
name|result
init|=
operator|!
name|c
operator|.
name|isEmpty
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
if|if
condition|(
operator|!
name|contains
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
break|break;
block|}
block|}
return|return
name|result
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
literal|"Can't add everything here!"
argument_list|)
throw|;
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
name|Object
name|obj
init|=
name|i
operator|.
name|next
argument_list|()
decl_stmt|;
name|result
operator|&=
name|remove
argument_list|(
name|obj
argument_list|)
expr_stmt|;
block|}
return|return
name|result
return|;
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
operator|!
name|tmpList
operator|.
name|isEmpty
argument_list|()
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
block|}
end_class

end_unit
