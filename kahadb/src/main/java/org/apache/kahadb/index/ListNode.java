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
name|kahadb
operator|.
name|index
package|;
end_package

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|DataInput
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|DataOutput
import|;
end_import

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
name|Iterator
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Map
operator|.
name|Entry
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|NoSuchElementException
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|kahadb
operator|.
name|page
operator|.
name|Page
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|kahadb
operator|.
name|page
operator|.
name|Transaction
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|kahadb
operator|.
name|util
operator|.
name|LinkedNode
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|kahadb
operator|.
name|util
operator|.
name|LinkedNodeList
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|kahadb
operator|.
name|util
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
name|kahadb
operator|.
name|util
operator|.
name|VariableMarshaller
import|;
end_import

begin_comment
comment|/**  * The ListNode class represents a node in the List object graph.  It is stored in  * one overflowing Page of a PageFile.  */
end_comment

begin_class
specifier|public
specifier|final
class|class
name|ListNode
parameter_list|<
name|Key
parameter_list|,
name|Value
parameter_list|>
block|{
specifier|private
specifier|final
specifier|static
name|boolean
name|ADD_FIRST
init|=
literal|true
decl_stmt|;
specifier|private
specifier|final
specifier|static
name|boolean
name|ADD_LAST
init|=
literal|false
decl_stmt|;
comment|// The index that this node is part of.
specifier|private
name|ListIndex
argument_list|<
name|Key
argument_list|,
name|Value
argument_list|>
name|containingList
decl_stmt|;
comment|// The page associated with this node
specifier|private
name|Page
argument_list|<
name|ListNode
argument_list|<
name|Key
argument_list|,
name|Value
argument_list|>
argument_list|>
name|page
decl_stmt|;
specifier|private
name|LinkedNodeList
argument_list|<
name|KeyValueEntry
argument_list|<
name|Key
argument_list|,
name|Value
argument_list|>
argument_list|>
name|entries
init|=
operator|new
name|LinkedNodeList
argument_list|<
name|KeyValueEntry
argument_list|<
name|Key
argument_list|,
name|Value
argument_list|>
argument_list|>
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|String
name|toString
parameter_list|()
block|{
return|return
literal|"PageId:"
operator|+
name|page
operator|.
name|getPageId
argument_list|()
operator|+
literal|", index:"
operator|+
name|containingList
operator|+
name|super
operator|.
name|toString
argument_list|()
return|;
block|}
block|}
decl_stmt|;
comment|// The next page after this one.
specifier|private
name|long
name|next
init|=
name|ListIndex
operator|.
name|NOT_SET
decl_stmt|;
specifier|static
specifier|final
class|class
name|KeyValueEntry
parameter_list|<
name|Key
parameter_list|,
name|Value
parameter_list|>
extends|extends
name|LinkedNode
argument_list|<
name|KeyValueEntry
argument_list|<
name|Key
argument_list|,
name|Value
argument_list|>
argument_list|>
implements|implements
name|Entry
argument_list|<
name|Key
argument_list|,
name|Value
argument_list|>
block|{
specifier|private
specifier|final
name|Key
name|key
decl_stmt|;
specifier|private
specifier|final
name|Value
name|value
decl_stmt|;
specifier|public
name|KeyValueEntry
parameter_list|(
name|Key
name|key
parameter_list|,
name|Value
name|value
parameter_list|)
block|{
name|this
operator|.
name|key
operator|=
name|key
expr_stmt|;
name|this
operator|.
name|value
operator|=
name|value
expr_stmt|;
block|}
specifier|public
name|Key
name|getKey
parameter_list|()
block|{
return|return
name|key
return|;
block|}
specifier|public
name|Value
name|getValue
parameter_list|()
block|{
return|return
name|value
return|;
block|}
specifier|public
name|Value
name|setValue
parameter_list|(
name|Value
name|value
parameter_list|)
block|{
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|()
throw|;
block|}
annotation|@
name|Override
specifier|public
name|String
name|toString
parameter_list|()
block|{
return|return
literal|"{"
operator|+
name|key
operator|+
literal|":"
operator|+
name|value
operator|+
literal|"}"
return|;
block|}
block|}
specifier|private
specifier|final
class|class
name|ListNodeIterator
implements|implements
name|Iterator
argument_list|<
name|ListNode
argument_list|<
name|Key
argument_list|,
name|Value
argument_list|>
argument_list|>
block|{
specifier|private
specifier|final
name|Transaction
name|tx
decl_stmt|;
specifier|private
specifier|final
name|ListIndex
argument_list|<
name|Key
argument_list|,
name|Value
argument_list|>
name|index
decl_stmt|;
name|ListNode
argument_list|<
name|Key
argument_list|,
name|Value
argument_list|>
name|nextEntry
decl_stmt|;
specifier|private
name|ListNodeIterator
parameter_list|(
name|Transaction
name|tx
parameter_list|,
name|ListNode
argument_list|<
name|Key
argument_list|,
name|Value
argument_list|>
name|current
parameter_list|)
block|{
name|this
operator|.
name|tx
operator|=
name|tx
expr_stmt|;
name|nextEntry
operator|=
name|current
expr_stmt|;
name|index
operator|=
name|current
operator|.
name|getContainingList
argument_list|()
expr_stmt|;
block|}
specifier|public
name|boolean
name|hasNext
parameter_list|()
block|{
return|return
name|nextEntry
operator|!=
literal|null
return|;
block|}
specifier|public
name|ListNode
argument_list|<
name|Key
argument_list|,
name|Value
argument_list|>
name|next
parameter_list|()
block|{
name|ListNode
argument_list|<
name|Key
argument_list|,
name|Value
argument_list|>
name|current
init|=
name|nextEntry
decl_stmt|;
if|if
condition|(
name|current
operator|!=
literal|null
condition|)
block|{
if|if
condition|(
name|current
operator|.
name|next
operator|!=
name|ListIndex
operator|.
name|NOT_SET
condition|)
block|{
try|try
block|{
name|nextEntry
operator|=
name|index
operator|.
name|loadNode
argument_list|(
name|tx
argument_list|,
name|current
operator|.
name|next
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|unexpected
parameter_list|)
block|{
name|IllegalStateException
name|e
init|=
operator|new
name|IllegalStateException
argument_list|(
literal|"failed to load next: "
operator|+
name|current
operator|.
name|next
operator|+
literal|", reason: "
operator|+
name|unexpected
operator|.
name|getLocalizedMessage
argument_list|()
argument_list|)
decl_stmt|;
name|e
operator|.
name|initCause
argument_list|(
name|unexpected
argument_list|)
expr_stmt|;
throw|throw
name|e
throw|;
block|}
block|}
else|else
block|{
name|nextEntry
operator|=
literal|null
expr_stmt|;
block|}
block|}
return|return
name|current
return|;
block|}
specifier|public
name|void
name|remove
parameter_list|()
block|{
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|()
throw|;
block|}
block|}
specifier|private
specifier|final
class|class
name|ListIterator
implements|implements
name|Iterator
argument_list|<
name|Entry
argument_list|<
name|Key
argument_list|,
name|Value
argument_list|>
argument_list|>
block|{
specifier|private
specifier|final
name|Transaction
name|tx
decl_stmt|;
specifier|private
specifier|final
name|ListIndex
argument_list|<
name|Key
argument_list|,
name|Value
argument_list|>
name|targetList
decl_stmt|;
name|ListNode
argument_list|<
name|Key
argument_list|,
name|Value
argument_list|>
name|currentNode
decl_stmt|,
name|previousNode
decl_stmt|;
name|KeyValueEntry
argument_list|<
name|Key
argument_list|,
name|Value
argument_list|>
name|nextEntry
decl_stmt|;
name|KeyValueEntry
argument_list|<
name|Key
argument_list|,
name|Value
argument_list|>
name|entryToRemove
decl_stmt|;
specifier|private
name|ListIterator
parameter_list|(
name|Transaction
name|tx
parameter_list|,
name|ListNode
argument_list|<
name|Key
argument_list|,
name|Value
argument_list|>
name|current
parameter_list|,
name|long
name|start
parameter_list|)
block|{
name|this
operator|.
name|tx
operator|=
name|tx
expr_stmt|;
name|this
operator|.
name|currentNode
operator|=
name|current
expr_stmt|;
name|this
operator|.
name|targetList
operator|=
name|current
operator|.
name|getContainingList
argument_list|()
expr_stmt|;
name|nextEntry
operator|=
name|current
operator|.
name|entries
operator|.
name|getHead
argument_list|()
expr_stmt|;
if|if
condition|(
name|start
operator|>
literal|0
condition|)
block|{
name|moveToRequestedStart
argument_list|(
name|start
argument_list|)
expr_stmt|;
block|}
block|}
specifier|private
name|void
name|moveToRequestedStart
parameter_list|(
specifier|final
name|long
name|start
parameter_list|)
block|{
name|long
name|count
init|=
literal|0
decl_stmt|;
while|while
condition|(
name|hasNext
argument_list|()
operator|&&
name|count
operator|<
name|start
condition|)
block|{
name|next
argument_list|()
expr_stmt|;
name|count
operator|++
expr_stmt|;
block|}
if|if
condition|(
operator|!
name|hasNext
argument_list|()
condition|)
block|{
throw|throw
operator|new
name|NoSuchElementException
argument_list|(
literal|"Index "
operator|+
name|start
operator|+
literal|" out of current range: "
operator|+
name|count
argument_list|)
throw|;
block|}
block|}
specifier|private
name|KeyValueEntry
argument_list|<
name|Key
argument_list|,
name|Value
argument_list|>
name|getFromNextNode
parameter_list|()
block|{
name|KeyValueEntry
argument_list|<
name|Key
argument_list|,
name|Value
argument_list|>
name|result
init|=
literal|null
decl_stmt|;
if|if
condition|(
name|currentNode
operator|.
name|getNext
argument_list|()
operator|!=
name|ListIndex
operator|.
name|NOT_SET
condition|)
block|{
try|try
block|{
name|previousNode
operator|=
name|currentNode
expr_stmt|;
name|currentNode
operator|=
name|targetList
operator|.
name|loadNode
argument_list|(
name|tx
argument_list|,
name|currentNode
operator|.
name|getNext
argument_list|()
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|unexpected
parameter_list|)
block|{
name|NoSuchElementException
name|e
init|=
operator|new
name|NoSuchElementException
argument_list|(
name|unexpected
operator|.
name|getLocalizedMessage
argument_list|()
argument_list|)
decl_stmt|;
name|e
operator|.
name|initCause
argument_list|(
name|unexpected
argument_list|)
expr_stmt|;
throw|throw
name|e
throw|;
block|}
name|result
operator|=
name|currentNode
operator|.
name|entries
operator|.
name|getHead
argument_list|()
expr_stmt|;
block|}
return|return
name|result
return|;
block|}
specifier|public
name|boolean
name|hasNext
parameter_list|()
block|{
if|if
condition|(
name|nextEntry
operator|==
literal|null
condition|)
block|{
name|nextEntry
operator|=
name|getFromNextNode
argument_list|()
expr_stmt|;
block|}
return|return
name|nextEntry
operator|!=
literal|null
return|;
block|}
specifier|public
name|Entry
argument_list|<
name|Key
argument_list|,
name|Value
argument_list|>
name|next
parameter_list|()
block|{
if|if
condition|(
name|nextEntry
operator|!=
literal|null
condition|)
block|{
name|entryToRemove
operator|=
name|nextEntry
expr_stmt|;
name|nextEntry
operator|=
name|entryToRemove
operator|.
name|getNext
argument_list|()
expr_stmt|;
return|return
name|entryToRemove
return|;
block|}
else|else
block|{
throw|throw
operator|new
name|NoSuchElementException
argument_list|()
throw|;
block|}
block|}
specifier|public
name|void
name|remove
parameter_list|()
block|{
if|if
condition|(
name|entryToRemove
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|IllegalStateException
argument_list|(
literal|"can only remove once, call hasNext();next() again"
argument_list|)
throw|;
block|}
try|try
block|{
name|entryToRemove
operator|.
name|unlink
argument_list|()
expr_stmt|;
name|entryToRemove
operator|=
literal|null
expr_stmt|;
name|ListNode
argument_list|<
name|Key
argument_list|,
name|Value
argument_list|>
name|toRemoveNode
init|=
literal|null
decl_stmt|;
if|if
condition|(
name|currentNode
operator|.
name|entries
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
comment|// may need to free this node
if|if
condition|(
name|currentNode
operator|.
name|isHead
argument_list|()
operator|&&
name|currentNode
operator|.
name|isTail
argument_list|()
condition|)
block|{
comment|// store empty list
block|}
elseif|else
if|if
condition|(
name|currentNode
operator|.
name|isHead
argument_list|()
condition|)
block|{
comment|// new head
name|toRemoveNode
operator|=
name|currentNode
expr_stmt|;
name|nextEntry
operator|=
name|getFromNextNode
argument_list|()
expr_stmt|;
name|targetList
operator|.
name|setHeadPageId
argument_list|(
name|currentNode
operator|.
name|getPageId
argument_list|()
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|currentNode
operator|.
name|isTail
argument_list|()
condition|)
block|{
name|toRemoveNode
operator|=
name|currentNode
expr_stmt|;
name|previousNode
operator|.
name|setNext
argument_list|(
name|ListIndex
operator|.
name|NOT_SET
argument_list|)
expr_stmt|;
name|previousNode
operator|.
name|store
argument_list|(
name|tx
argument_list|)
expr_stmt|;
name|targetList
operator|.
name|setTailPageId
argument_list|(
name|previousNode
operator|.
name|getPageId
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
name|targetList
operator|.
name|onRemove
argument_list|()
expr_stmt|;
if|if
condition|(
name|toRemoveNode
operator|!=
literal|null
condition|)
block|{
name|tx
operator|.
name|free
argument_list|(
name|toRemoveNode
operator|.
name|getPage
argument_list|()
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|currentNode
operator|.
name|store
argument_list|(
name|tx
argument_list|)
expr_stmt|;
block|}
block|}
catch|catch
parameter_list|(
name|IOException
name|unexpected
parameter_list|)
block|{
name|IllegalStateException
name|e
init|=
operator|new
name|IllegalStateException
argument_list|(
name|unexpected
operator|.
name|getLocalizedMessage
argument_list|()
argument_list|)
decl_stmt|;
name|e
operator|.
name|initCause
argument_list|(
name|unexpected
argument_list|)
expr_stmt|;
throw|throw
name|e
throw|;
block|}
block|}
block|}
comment|/**      * The Marshaller is used to store and load the data in the ListNode into a Page.      *      * @param<Key>      * @param<Value>      */
specifier|static
specifier|public
specifier|final
class|class
name|NodeMarshaller
parameter_list|<
name|Key
parameter_list|,
name|Value
parameter_list|>
extends|extends
name|VariableMarshaller
argument_list|<
name|ListNode
argument_list|<
name|Key
argument_list|,
name|Value
argument_list|>
argument_list|>
block|{
specifier|private
specifier|final
name|Marshaller
argument_list|<
name|Key
argument_list|>
name|keyMarshaller
decl_stmt|;
specifier|private
specifier|final
name|Marshaller
argument_list|<
name|Value
argument_list|>
name|valueMarshaller
decl_stmt|;
specifier|public
name|NodeMarshaller
parameter_list|(
name|Marshaller
argument_list|<
name|Key
argument_list|>
name|keyMarshaller
parameter_list|,
name|Marshaller
argument_list|<
name|Value
argument_list|>
name|valueMarshaller
parameter_list|)
block|{
name|this
operator|.
name|keyMarshaller
operator|=
name|keyMarshaller
expr_stmt|;
name|this
operator|.
name|valueMarshaller
operator|=
name|valueMarshaller
expr_stmt|;
block|}
specifier|public
name|void
name|writePayload
parameter_list|(
name|ListNode
argument_list|<
name|Key
argument_list|,
name|Value
argument_list|>
name|node
parameter_list|,
name|DataOutput
name|os
parameter_list|)
throws|throws
name|IOException
block|{
name|os
operator|.
name|writeLong
argument_list|(
name|node
operator|.
name|next
argument_list|)
expr_stmt|;
name|short
name|count
init|=
operator|(
name|short
operator|)
name|node
operator|.
name|entries
operator|.
name|size
argument_list|()
decl_stmt|;
comment|// cast may truncate value...
if|if
condition|(
name|count
operator|!=
name|node
operator|.
name|entries
operator|.
name|size
argument_list|()
condition|)
block|{
throw|throw
operator|new
name|IOException
argument_list|(
literal|"short over flow, too many entries in list: "
operator|+
name|node
operator|.
name|entries
operator|.
name|size
argument_list|()
argument_list|)
throw|;
block|}
name|os
operator|.
name|writeShort
argument_list|(
name|count
argument_list|)
expr_stmt|;
name|KeyValueEntry
argument_list|<
name|Key
argument_list|,
name|Value
argument_list|>
name|entry
init|=
name|node
operator|.
name|entries
operator|.
name|getHead
argument_list|()
decl_stmt|;
while|while
condition|(
name|entry
operator|!=
literal|null
condition|)
block|{
name|keyMarshaller
operator|.
name|writePayload
argument_list|(
operator|(
name|Key
operator|)
name|entry
operator|.
name|getKey
argument_list|()
argument_list|,
name|os
argument_list|)
expr_stmt|;
name|valueMarshaller
operator|.
name|writePayload
argument_list|(
operator|(
name|Value
operator|)
name|entry
operator|.
name|getValue
argument_list|()
argument_list|,
name|os
argument_list|)
expr_stmt|;
name|entry
operator|=
name|entry
operator|.
name|getNext
argument_list|()
expr_stmt|;
block|}
block|}
annotation|@
name|SuppressWarnings
argument_list|(
literal|"unchecked"
argument_list|)
specifier|public
name|ListNode
argument_list|<
name|Key
argument_list|,
name|Value
argument_list|>
name|readPayload
parameter_list|(
name|DataInput
name|is
parameter_list|)
throws|throws
name|IOException
block|{
name|ListNode
argument_list|<
name|Key
argument_list|,
name|Value
argument_list|>
name|node
init|=
operator|new
name|ListNode
argument_list|<
name|Key
argument_list|,
name|Value
argument_list|>
argument_list|()
decl_stmt|;
name|node
operator|.
name|next
operator|=
name|is
operator|.
name|readLong
argument_list|()
expr_stmt|;
specifier|final
name|short
name|size
init|=
name|is
operator|.
name|readShort
argument_list|()
decl_stmt|;
for|for
control|(
name|short
name|i
init|=
literal|0
init|;
name|i
operator|<
name|size
condition|;
name|i
operator|++
control|)
block|{
name|node
operator|.
name|entries
operator|.
name|addLast
argument_list|(
operator|new
name|KeyValueEntry
argument_list|(
name|keyMarshaller
operator|.
name|readPayload
argument_list|(
name|is
argument_list|)
argument_list|,
name|valueMarshaller
operator|.
name|readPayload
argument_list|(
name|is
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
return|return
name|node
return|;
block|}
block|}
specifier|public
name|Value
name|put
parameter_list|(
name|Transaction
name|tx
parameter_list|,
name|Key
name|key
parameter_list|,
name|Value
name|value
parameter_list|)
throws|throws
name|IOException
block|{
if|if
condition|(
name|key
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"Key cannot be null"
argument_list|)
throw|;
block|}
name|entries
operator|.
name|addLast
argument_list|(
operator|new
name|KeyValueEntry
argument_list|<
name|Key
argument_list|,
name|Value
argument_list|>
argument_list|(
name|key
argument_list|,
name|value
argument_list|)
argument_list|)
expr_stmt|;
name|store
argument_list|(
name|tx
argument_list|,
name|ADD_LAST
argument_list|)
expr_stmt|;
return|return
literal|null
return|;
block|}
specifier|public
name|Value
name|addFirst
parameter_list|(
name|Transaction
name|tx
parameter_list|,
name|Key
name|key
parameter_list|,
name|Value
name|value
parameter_list|)
throws|throws
name|IOException
block|{
if|if
condition|(
name|key
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"Key cannot be null"
argument_list|)
throw|;
block|}
name|entries
operator|.
name|addFirst
argument_list|(
operator|new
name|KeyValueEntry
argument_list|<
name|Key
argument_list|,
name|Value
argument_list|>
argument_list|(
name|key
argument_list|,
name|value
argument_list|)
argument_list|)
expr_stmt|;
name|store
argument_list|(
name|tx
argument_list|,
name|ADD_FIRST
argument_list|)
expr_stmt|;
return|return
literal|null
return|;
block|}
specifier|private
name|void
name|store
parameter_list|(
name|Transaction
name|tx
parameter_list|,
name|boolean
name|addFirst
parameter_list|)
throws|throws
name|IOException
block|{
try|try
block|{
name|getContainingList
argument_list|()
operator|.
name|storeNode
argument_list|(
name|tx
argument_list|,
name|this
argument_list|,
literal|false
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Transaction
operator|.
name|PageOverflowIOException
name|e
parameter_list|)
block|{
comment|// If we get an overflow
name|split
argument_list|(
name|tx
argument_list|,
name|addFirst
argument_list|)
expr_stmt|;
block|}
block|}
specifier|private
name|void
name|store
parameter_list|(
name|Transaction
name|tx
parameter_list|)
throws|throws
name|IOException
block|{
name|getContainingList
argument_list|()
operator|.
name|storeNode
argument_list|(
name|tx
argument_list|,
name|this
argument_list|,
literal|false
argument_list|)
expr_stmt|;
block|}
specifier|private
name|void
name|split
parameter_list|(
name|Transaction
name|tx
parameter_list|,
name|boolean
name|isAddFirst
parameter_list|)
throws|throws
name|IOException
block|{
name|ListNode
argument_list|<
name|Key
argument_list|,
name|Value
argument_list|>
name|extension
init|=
name|getContainingList
argument_list|()
operator|.
name|createNode
argument_list|(
name|tx
argument_list|)
decl_stmt|;
if|if
condition|(
name|isAddFirst
condition|)
block|{
comment|// head keeps the first entry, insert extension with the rest
name|extension
operator|.
name|setNext
argument_list|(
name|this
operator|.
name|getNext
argument_list|()
argument_list|)
expr_stmt|;
name|this
operator|.
name|setNext
argument_list|(
name|extension
operator|.
name|getPageId
argument_list|()
argument_list|)
expr_stmt|;
name|extension
operator|.
name|setEntries
argument_list|(
name|entries
operator|.
name|getHead
argument_list|()
operator|.
name|splitAfter
argument_list|()
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|this
operator|.
name|setNext
argument_list|(
name|extension
operator|.
name|getPageId
argument_list|()
argument_list|)
expr_stmt|;
name|extension
operator|.
name|setEntries
argument_list|(
name|entries
operator|.
name|getTail
argument_list|()
operator|.
name|getPrevious
argument_list|()
operator|.
name|splitAfter
argument_list|()
argument_list|)
expr_stmt|;
name|getContainingList
argument_list|()
operator|.
name|setTailPageId
argument_list|(
name|extension
operator|.
name|getPageId
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|extension
operator|.
name|store
argument_list|(
name|tx
argument_list|,
name|isAddFirst
argument_list|)
expr_stmt|;
name|store
argument_list|(
name|tx
argument_list|)
expr_stmt|;
block|}
comment|// called after a split
specifier|private
name|void
name|setEntries
parameter_list|(
name|LinkedNodeList
argument_list|<
name|KeyValueEntry
argument_list|<
name|Key
argument_list|,
name|Value
argument_list|>
argument_list|>
name|list
parameter_list|)
block|{
name|this
operator|.
name|entries
operator|=
name|list
expr_stmt|;
block|}
specifier|public
name|Value
name|get
parameter_list|(
name|Transaction
name|tx
parameter_list|,
name|Key
name|key
parameter_list|)
block|{
if|if
condition|(
name|key
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"Key cannot be null"
argument_list|)
throw|;
block|}
name|Value
name|result
init|=
literal|null
decl_stmt|;
name|KeyValueEntry
argument_list|<
name|Key
argument_list|,
name|Value
argument_list|>
name|nextEntry
init|=
name|entries
operator|.
name|getTail
argument_list|()
decl_stmt|;
while|while
condition|(
name|nextEntry
operator|!=
literal|null
condition|)
block|{
if|if
condition|(
name|nextEntry
operator|.
name|getKey
argument_list|()
operator|.
name|equals
argument_list|(
name|key
argument_list|)
condition|)
block|{
name|result
operator|=
name|nextEntry
operator|.
name|getValue
argument_list|()
expr_stmt|;
break|break;
block|}
name|nextEntry
operator|=
name|nextEntry
operator|.
name|getPrevious
argument_list|()
expr_stmt|;
block|}
return|return
name|result
return|;
block|}
specifier|public
name|boolean
name|isEmpty
parameter_list|(
specifier|final
name|Transaction
name|tx
parameter_list|)
block|{
return|return
name|entries
operator|.
name|isEmpty
argument_list|()
return|;
block|}
specifier|public
name|Entry
argument_list|<
name|Key
argument_list|,
name|Value
argument_list|>
name|getFirst
parameter_list|(
name|Transaction
name|tx
parameter_list|)
block|{
return|return
name|entries
operator|.
name|getHead
argument_list|()
return|;
block|}
specifier|public
name|Entry
argument_list|<
name|Key
argument_list|,
name|Value
argument_list|>
name|getLast
parameter_list|(
name|Transaction
name|tx
parameter_list|)
block|{
return|return
name|entries
operator|.
name|getTail
argument_list|()
return|;
block|}
specifier|public
name|Iterator
argument_list|<
name|Entry
argument_list|<
name|Key
argument_list|,
name|Value
argument_list|>
argument_list|>
name|iterator
parameter_list|(
specifier|final
name|Transaction
name|tx
parameter_list|,
name|long
name|pos
parameter_list|)
throws|throws
name|IOException
block|{
return|return
operator|new
name|ListIterator
argument_list|(
name|tx
argument_list|,
name|this
argument_list|,
name|pos
argument_list|)
return|;
block|}
specifier|public
name|Iterator
argument_list|<
name|Entry
argument_list|<
name|Key
argument_list|,
name|Value
argument_list|>
argument_list|>
name|iterator
parameter_list|(
specifier|final
name|Transaction
name|tx
parameter_list|)
throws|throws
name|IOException
block|{
return|return
operator|new
name|ListIterator
argument_list|(
name|tx
argument_list|,
name|this
argument_list|,
literal|0
argument_list|)
return|;
block|}
name|Iterator
argument_list|<
name|ListNode
argument_list|<
name|Key
argument_list|,
name|Value
argument_list|>
argument_list|>
name|listNodeIterator
parameter_list|(
specifier|final
name|Transaction
name|tx
parameter_list|)
throws|throws
name|IOException
block|{
return|return
operator|new
name|ListNodeIterator
argument_list|(
name|tx
argument_list|,
name|this
argument_list|)
return|;
block|}
specifier|public
name|void
name|clear
parameter_list|(
name|Transaction
name|tx
parameter_list|)
throws|throws
name|IOException
block|{
name|entries
operator|.
name|clear
argument_list|()
expr_stmt|;
name|tx
operator|.
name|free
argument_list|(
name|this
operator|.
name|getPageId
argument_list|()
argument_list|)
expr_stmt|;
block|}
specifier|public
name|boolean
name|contains
parameter_list|(
name|Transaction
name|tx
parameter_list|,
name|Key
name|key
parameter_list|)
block|{
if|if
condition|(
name|key
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"Key cannot be null"
argument_list|)
throw|;
block|}
name|boolean
name|found
init|=
literal|false
decl_stmt|;
name|KeyValueEntry
argument_list|<
name|Key
argument_list|,
name|Value
argument_list|>
name|nextEntry
init|=
name|entries
operator|.
name|getTail
argument_list|()
decl_stmt|;
while|while
condition|(
name|nextEntry
operator|!=
literal|null
condition|)
block|{
if|if
condition|(
name|nextEntry
operator|.
name|getKey
argument_list|()
operator|.
name|equals
argument_list|(
name|key
argument_list|)
condition|)
block|{
name|found
operator|=
literal|true
expr_stmt|;
break|break;
block|}
name|nextEntry
operator|=
name|nextEntry
operator|.
name|getPrevious
argument_list|()
expr_stmt|;
block|}
return|return
name|found
return|;
block|}
comment|///////////////////////////////////////////////////////////////////
comment|// Implementation methods
comment|///////////////////////////////////////////////////////////////////
specifier|public
name|long
name|getPageId
parameter_list|()
block|{
return|return
name|page
operator|.
name|getPageId
argument_list|()
return|;
block|}
specifier|public
name|Page
argument_list|<
name|ListNode
argument_list|<
name|Key
argument_list|,
name|Value
argument_list|>
argument_list|>
name|getPage
parameter_list|()
block|{
return|return
name|page
return|;
block|}
specifier|public
name|void
name|setPage
parameter_list|(
name|Page
argument_list|<
name|ListNode
argument_list|<
name|Key
argument_list|,
name|Value
argument_list|>
argument_list|>
name|page
parameter_list|)
block|{
name|this
operator|.
name|page
operator|=
name|page
expr_stmt|;
block|}
specifier|public
name|long
name|getNext
parameter_list|()
block|{
return|return
name|next
return|;
block|}
specifier|public
name|void
name|setNext
parameter_list|(
name|long
name|next
parameter_list|)
block|{
name|this
operator|.
name|next
operator|=
name|next
expr_stmt|;
block|}
specifier|public
name|void
name|setContainingList
parameter_list|(
name|ListIndex
argument_list|<
name|Key
argument_list|,
name|Value
argument_list|>
name|list
parameter_list|)
block|{
name|this
operator|.
name|containingList
operator|=
name|list
expr_stmt|;
block|}
specifier|public
name|ListIndex
argument_list|<
name|Key
argument_list|,
name|Value
argument_list|>
name|getContainingList
parameter_list|()
block|{
return|return
name|containingList
return|;
block|}
specifier|public
name|boolean
name|isHead
parameter_list|()
block|{
return|return
name|getPageId
argument_list|()
operator|==
name|containingList
operator|.
name|getHeadPageId
argument_list|()
return|;
block|}
specifier|public
name|boolean
name|isTail
parameter_list|()
block|{
return|return
name|getPageId
argument_list|()
operator|==
name|containingList
operator|.
name|getTailPageId
argument_list|()
return|;
block|}
specifier|public
name|int
name|size
parameter_list|(
name|Transaction
name|tx
parameter_list|)
block|{
return|return
name|entries
operator|.
name|size
argument_list|()
return|;
block|}
annotation|@
name|Override
specifier|public
name|String
name|toString
parameter_list|()
block|{
return|return
literal|"[ListNode("
operator|+
operator|(
name|page
operator|!=
literal|null
condition|?
name|page
operator|.
name|getPageId
argument_list|()
operator|+
literal|"->"
operator|+
name|next
else|:
literal|"null"
operator|)
operator|+
literal|")["
operator|+
name|entries
operator|.
name|size
argument_list|()
operator|+
literal|"]]"
return|;
block|}
block|}
end_class

end_unit

