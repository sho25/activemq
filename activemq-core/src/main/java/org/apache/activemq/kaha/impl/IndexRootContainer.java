begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  *   * Copyright 2005-2006 The Apache Software Foundation  *   * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with  * the License. You may obtain a copy of the License at  *   * http://www.apache.org/licenses/LICENSE-2.0  *   * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on  * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the  * specific language governing permissions and limitations under the License.  */
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
package|;
end_package

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|IOException
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|LinkedList
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
name|Marshaller
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
name|ObjectMarshaller
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|commons
operator|.
name|logging
operator|.
name|Log
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|commons
operator|.
name|logging
operator|.
name|LogFactory
import|;
end_import

begin_import
import|import
name|edu
operator|.
name|emory
operator|.
name|mathcs
operator|.
name|backport
operator|.
name|java
operator|.
name|util
operator|.
name|concurrent
operator|.
name|ConcurrentHashMap
import|;
end_import

begin_comment
comment|/** * A container of roots for other Containers *  * @version $Revision: 1.2 $ */
end_comment

begin_class
class|class
name|IndexRootContainer
block|{
specifier|private
specifier|static
specifier|final
name|Log
name|log
init|=
name|LogFactory
operator|.
name|getLog
argument_list|(
name|IndexRootContainer
operator|.
name|class
argument_list|)
decl_stmt|;
specifier|protected
specifier|static
specifier|final
name|Marshaller
name|rootMarshaller
init|=
operator|new
name|ObjectMarshaller
argument_list|()
decl_stmt|;
specifier|protected
name|IndexItem
name|root
decl_stmt|;
specifier|protected
name|IndexManager
name|indexManager
decl_stmt|;
specifier|protected
name|DataManager
name|dataManager
decl_stmt|;
specifier|protected
name|Map
name|map
init|=
operator|new
name|ConcurrentHashMap
argument_list|()
decl_stmt|;
specifier|protected
name|LinkedList
name|list
init|=
operator|new
name|LinkedList
argument_list|()
decl_stmt|;
name|IndexRootContainer
parameter_list|(
name|IndexItem
name|root
parameter_list|,
name|IndexManager
name|im
parameter_list|,
name|DataManager
name|dfm
parameter_list|)
throws|throws
name|IOException
block|{
name|this
operator|.
name|root
operator|=
name|root
expr_stmt|;
name|this
operator|.
name|indexManager
operator|=
name|im
expr_stmt|;
name|this
operator|.
name|dataManager
operator|=
name|dfm
expr_stmt|;
name|long
name|nextItem
init|=
name|root
operator|.
name|getNextItem
argument_list|()
decl_stmt|;
while|while
condition|(
name|nextItem
operator|!=
name|Item
operator|.
name|POSITION_NOT_SET
condition|)
block|{
name|IndexItem
name|item
init|=
name|indexManager
operator|.
name|getIndex
argument_list|(
name|nextItem
argument_list|)
decl_stmt|;
name|DataItem
name|data
init|=
name|item
operator|.
name|getKeyDataItem
argument_list|()
decl_stmt|;
name|Object
name|key
init|=
name|dataManager
operator|.
name|readItem
argument_list|(
name|rootMarshaller
argument_list|,
name|data
argument_list|)
decl_stmt|;
name|map
operator|.
name|put
argument_list|(
name|key
argument_list|,
name|item
argument_list|)
expr_stmt|;
name|list
operator|.
name|add
argument_list|(
name|item
argument_list|)
expr_stmt|;
name|nextItem
operator|=
name|item
operator|.
name|getNextItem
argument_list|()
expr_stmt|;
name|dataManager
operator|.
name|addInterestInFile
argument_list|(
name|item
operator|.
name|getKeyFile
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
name|Set
name|getKeys
parameter_list|()
block|{
return|return
name|map
operator|.
name|keySet
argument_list|()
return|;
block|}
name|IndexItem
name|addRoot
parameter_list|(
name|Object
name|key
parameter_list|)
throws|throws
name|IOException
block|{
if|if
condition|(
name|map
operator|.
name|containsKey
argument_list|(
name|key
argument_list|)
condition|)
block|{
name|removeRoot
argument_list|(
name|key
argument_list|)
expr_stmt|;
block|}
name|DataItem
name|data
init|=
name|dataManager
operator|.
name|storeItem
argument_list|(
name|rootMarshaller
argument_list|,
name|key
argument_list|)
decl_stmt|;
name|IndexItem
name|index
init|=
name|indexManager
operator|.
name|createNewIndex
argument_list|()
decl_stmt|;
name|index
operator|.
name|setKeyData
argument_list|(
name|data
argument_list|)
expr_stmt|;
name|IndexItem
name|newRoot
init|=
name|indexManager
operator|.
name|createNewIndex
argument_list|()
decl_stmt|;
name|indexManager
operator|.
name|updateIndex
argument_list|(
name|newRoot
argument_list|)
expr_stmt|;
name|index
operator|.
name|setValueOffset
argument_list|(
name|newRoot
operator|.
name|getOffset
argument_list|()
argument_list|)
expr_stmt|;
name|IndexItem
name|last
init|=
name|list
operator|.
name|isEmpty
argument_list|()
condition|?
literal|null
else|:
operator|(
name|IndexItem
operator|)
name|list
operator|.
name|getLast
argument_list|()
decl_stmt|;
name|last
operator|=
name|last
operator|==
literal|null
condition|?
name|root
else|:
name|last
expr_stmt|;
name|long
name|prev
init|=
name|last
operator|.
name|getOffset
argument_list|()
decl_stmt|;
name|index
operator|.
name|setPreviousItem
argument_list|(
name|prev
argument_list|)
expr_stmt|;
name|indexManager
operator|.
name|updateIndex
argument_list|(
name|index
argument_list|)
expr_stmt|;
name|last
operator|.
name|setNextItem
argument_list|(
name|index
operator|.
name|getOffset
argument_list|()
argument_list|)
expr_stmt|;
name|indexManager
operator|.
name|updateIndex
argument_list|(
name|last
argument_list|)
expr_stmt|;
name|map
operator|.
name|put
argument_list|(
name|key
argument_list|,
name|index
argument_list|)
expr_stmt|;
name|list
operator|.
name|add
argument_list|(
name|index
argument_list|)
expr_stmt|;
return|return
name|newRoot
return|;
block|}
name|void
name|removeRoot
parameter_list|(
name|Object
name|key
parameter_list|)
throws|throws
name|IOException
block|{
name|IndexItem
name|item
init|=
operator|(
name|IndexItem
operator|)
name|map
operator|.
name|remove
argument_list|(
name|key
argument_list|)
decl_stmt|;
if|if
condition|(
name|item
operator|!=
literal|null
condition|)
block|{
name|dataManager
operator|.
name|removeInterestInFile
argument_list|(
name|item
operator|.
name|getKeyFile
argument_list|()
argument_list|)
expr_stmt|;
name|IndexItem
name|rootIndex
init|=
name|indexManager
operator|.
name|getIndex
argument_list|(
name|item
operator|.
name|getValueOffset
argument_list|()
argument_list|)
decl_stmt|;
name|indexManager
operator|.
name|freeIndex
argument_list|(
name|rootIndex
argument_list|)
expr_stmt|;
name|int
name|index
init|=
name|list
operator|.
name|indexOf
argument_list|(
name|item
argument_list|)
decl_stmt|;
name|IndexItem
name|prev
init|=
name|index
operator|>
literal|0
condition|?
operator|(
name|IndexItem
operator|)
name|list
operator|.
name|get
argument_list|(
name|index
operator|-
literal|1
argument_list|)
else|:
name|root
decl_stmt|;
name|prev
operator|=
name|prev
operator|==
literal|null
condition|?
name|root
else|:
name|prev
expr_stmt|;
name|IndexItem
name|next
init|=
name|index
operator|<
operator|(
name|list
operator|.
name|size
argument_list|()
operator|-
literal|1
operator|)
condition|?
operator|(
name|IndexItem
operator|)
name|list
operator|.
name|get
argument_list|(
name|index
operator|+
literal|1
argument_list|)
else|:
literal|null
decl_stmt|;
if|if
condition|(
name|next
operator|!=
literal|null
condition|)
block|{
name|prev
operator|.
name|setNextItem
argument_list|(
name|next
operator|.
name|getOffset
argument_list|()
argument_list|)
expr_stmt|;
name|next
operator|.
name|setPreviousItem
argument_list|(
name|prev
operator|.
name|getOffset
argument_list|()
argument_list|)
expr_stmt|;
name|indexManager
operator|.
name|updateIndex
argument_list|(
name|next
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|prev
operator|.
name|setNextItem
argument_list|(
name|Item
operator|.
name|POSITION_NOT_SET
argument_list|)
expr_stmt|;
block|}
name|indexManager
operator|.
name|updateIndex
argument_list|(
name|prev
argument_list|)
expr_stmt|;
name|list
operator|.
name|remove
argument_list|(
name|item
argument_list|)
expr_stmt|;
block|}
block|}
name|IndexItem
name|getRoot
parameter_list|(
name|Object
name|key
parameter_list|)
throws|throws
name|IOException
block|{
name|IndexItem
name|index
init|=
operator|(
name|IndexItem
operator|)
name|map
operator|.
name|get
argument_list|(
name|key
argument_list|)
decl_stmt|;
if|if
condition|(
name|index
operator|!=
literal|null
condition|)
block|{
return|return
name|indexManager
operator|.
name|getIndex
argument_list|(
name|index
operator|.
name|getValueOffset
argument_list|()
argument_list|)
return|;
block|}
throw|throw
operator|new
name|IOException
argument_list|(
literal|"Cannot find root for key "
operator|+
name|key
argument_list|)
throw|;
block|}
block|}
end_class

end_unit

