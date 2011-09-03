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
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|concurrent
operator|.
name|atomic
operator|.
name|AtomicBoolean
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|concurrent
operator|.
name|atomic
operator|.
name|AtomicLong
import|;
end_import

begin_import
import|import
name|org
operator|.
name|slf4j
operator|.
name|Logger
import|;
end_import

begin_import
import|import
name|org
operator|.
name|slf4j
operator|.
name|LoggerFactory
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
name|PageFile
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
name|Marshaller
import|;
end_import

begin_class
specifier|public
class|class
name|ListIndex
parameter_list|<
name|Key
parameter_list|,
name|Value
parameter_list|>
implements|implements
name|Index
argument_list|<
name|Key
argument_list|,
name|Value
argument_list|>
block|{
specifier|private
specifier|static
specifier|final
name|Logger
name|LOG
init|=
name|LoggerFactory
operator|.
name|getLogger
argument_list|(
name|ListIndex
operator|.
name|class
argument_list|)
decl_stmt|;
specifier|public
specifier|final
specifier|static
name|long
name|NOT_SET
init|=
operator|-
literal|1
decl_stmt|;
specifier|protected
name|PageFile
name|pageFile
decl_stmt|;
specifier|protected
name|long
name|headPageId
decl_stmt|;
specifier|protected
name|long
name|tailPageId
decl_stmt|;
specifier|private
name|AtomicLong
name|size
init|=
operator|new
name|AtomicLong
argument_list|(
literal|0
argument_list|)
decl_stmt|;
specifier|protected
name|AtomicBoolean
name|loaded
init|=
operator|new
name|AtomicBoolean
argument_list|()
decl_stmt|;
specifier|private
name|ListNode
operator|.
name|NodeMarshaller
argument_list|<
name|Key
argument_list|,
name|Value
argument_list|>
name|marshaller
decl_stmt|;
specifier|private
name|Marshaller
argument_list|<
name|Key
argument_list|>
name|keyMarshaller
decl_stmt|;
specifier|private
name|Marshaller
argument_list|<
name|Value
argument_list|>
name|valueMarshaller
decl_stmt|;
specifier|public
name|ListIndex
parameter_list|()
block|{     }
specifier|public
name|ListIndex
parameter_list|(
name|PageFile
name|pageFile
parameter_list|,
name|long
name|headPageId
parameter_list|)
block|{
name|this
operator|.
name|pageFile
operator|=
name|pageFile
expr_stmt|;
name|setHeadPageId
argument_list|(
name|headPageId
argument_list|)
expr_stmt|;
block|}
annotation|@
name|SuppressWarnings
argument_list|(
literal|"rawtypes"
argument_list|)
specifier|public
name|ListIndex
parameter_list|(
name|PageFile
name|pageFile
parameter_list|,
name|Page
name|page
parameter_list|)
block|{
name|this
argument_list|(
name|pageFile
argument_list|,
name|page
operator|.
name|getPageId
argument_list|()
argument_list|)
expr_stmt|;
block|}
specifier|synchronized
specifier|public
name|void
name|load
parameter_list|(
name|Transaction
name|tx
parameter_list|)
throws|throws
name|IOException
block|{
if|if
condition|(
name|loaded
operator|.
name|compareAndSet
argument_list|(
literal|false
argument_list|,
literal|true
argument_list|)
condition|)
block|{
name|LOG
operator|.
name|debug
argument_list|(
literal|"loading"
argument_list|)
expr_stmt|;
if|if
condition|(
name|keyMarshaller
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"The key marshaller must be set before loading the ListIndex"
argument_list|)
throw|;
block|}
if|if
condition|(
name|valueMarshaller
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"The value marshaller must be set before loading the ListIndex"
argument_list|)
throw|;
block|}
name|marshaller
operator|=
operator|new
name|ListNode
operator|.
name|NodeMarshaller
argument_list|<
name|Key
argument_list|,
name|Value
argument_list|>
argument_list|(
name|keyMarshaller
argument_list|,
name|valueMarshaller
argument_list|)
expr_stmt|;
specifier|final
name|Page
argument_list|<
name|ListNode
argument_list|<
name|Key
argument_list|,
name|Value
argument_list|>
argument_list|>
name|p
init|=
name|tx
operator|.
name|load
argument_list|(
name|getHeadPageId
argument_list|()
argument_list|,
literal|null
argument_list|)
decl_stmt|;
if|if
condition|(
name|p
operator|.
name|getType
argument_list|()
operator|==
name|Page
operator|.
name|PAGE_FREE_TYPE
condition|)
block|{
comment|// Need to initialize it..
name|ListNode
argument_list|<
name|Key
argument_list|,
name|Value
argument_list|>
name|root
init|=
name|createNode
argument_list|(
name|p
argument_list|)
decl_stmt|;
name|storeNode
argument_list|(
name|tx
argument_list|,
name|root
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|setHeadPageId
argument_list|(
name|p
operator|.
name|getPageId
argument_list|()
argument_list|)
expr_stmt|;
name|setTailPageId
argument_list|(
name|getHeadPageId
argument_list|()
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|ListNode
argument_list|<
name|Key
argument_list|,
name|Value
argument_list|>
name|node
init|=
name|loadNode
argument_list|(
name|tx
argument_list|,
name|getHeadPageId
argument_list|()
argument_list|)
decl_stmt|;
name|setTailPageId
argument_list|(
name|getHeadPageId
argument_list|()
argument_list|)
expr_stmt|;
name|size
operator|.
name|addAndGet
argument_list|(
name|node
operator|.
name|size
argument_list|(
name|tx
argument_list|)
argument_list|)
expr_stmt|;
while|while
condition|(
name|node
operator|.
name|getNext
argument_list|()
operator|!=
name|NOT_SET
condition|)
block|{
name|node
operator|=
name|loadNode
argument_list|(
name|tx
argument_list|,
name|node
operator|.
name|getNext
argument_list|()
argument_list|)
expr_stmt|;
name|size
operator|.
name|addAndGet
argument_list|(
name|node
operator|.
name|size
argument_list|(
name|tx
argument_list|)
argument_list|)
expr_stmt|;
name|setTailPageId
argument_list|(
name|node
operator|.
name|getPageId
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|}
specifier|synchronized
specifier|public
name|void
name|unload
parameter_list|(
name|Transaction
name|tx
parameter_list|)
block|{
if|if
condition|(
name|loaded
operator|.
name|compareAndSet
argument_list|(
literal|true
argument_list|,
literal|false
argument_list|)
condition|)
block|{         }
block|}
specifier|protected
name|ListNode
argument_list|<
name|Key
argument_list|,
name|Value
argument_list|>
name|getHead
parameter_list|(
name|Transaction
name|tx
parameter_list|)
throws|throws
name|IOException
block|{
return|return
name|loadNode
argument_list|(
name|tx
argument_list|,
name|getHeadPageId
argument_list|()
argument_list|)
return|;
block|}
specifier|protected
name|ListNode
argument_list|<
name|Key
argument_list|,
name|Value
argument_list|>
name|getTail
parameter_list|(
name|Transaction
name|tx
parameter_list|)
throws|throws
name|IOException
block|{
return|return
name|loadNode
argument_list|(
name|tx
argument_list|,
name|getTailPageId
argument_list|()
argument_list|)
return|;
block|}
specifier|synchronized
specifier|public
name|boolean
name|containsKey
parameter_list|(
name|Transaction
name|tx
parameter_list|,
name|Key
name|key
parameter_list|)
throws|throws
name|IOException
block|{
name|assertLoaded
argument_list|()
expr_stmt|;
for|for
control|(
name|Iterator
argument_list|<
name|Map
operator|.
name|Entry
argument_list|<
name|Key
argument_list|,
name|Value
argument_list|>
argument_list|>
name|iterator
init|=
name|iterator
argument_list|(
name|tx
argument_list|)
init|;
name|iterator
operator|.
name|hasNext
argument_list|()
condition|;
control|)
block|{
name|Map
operator|.
name|Entry
argument_list|<
name|Key
argument_list|,
name|Value
argument_list|>
name|candidate
init|=
name|iterator
operator|.
name|next
argument_list|()
decl_stmt|;
if|if
condition|(
name|key
operator|.
name|equals
argument_list|(
name|candidate
operator|.
name|getKey
argument_list|()
argument_list|)
condition|)
block|{
return|return
literal|true
return|;
block|}
block|}
return|return
literal|false
return|;
block|}
specifier|synchronized
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
throws|throws
name|IOException
block|{
name|assertLoaded
argument_list|()
expr_stmt|;
for|for
control|(
name|Iterator
argument_list|<
name|Map
operator|.
name|Entry
argument_list|<
name|Key
argument_list|,
name|Value
argument_list|>
argument_list|>
name|iterator
init|=
name|iterator
argument_list|(
name|tx
argument_list|)
init|;
name|iterator
operator|.
name|hasNext
argument_list|()
condition|;
control|)
block|{
name|Map
operator|.
name|Entry
argument_list|<
name|Key
argument_list|,
name|Value
argument_list|>
name|candidate
init|=
name|iterator
operator|.
name|next
argument_list|()
decl_stmt|;
if|if
condition|(
name|key
operator|.
name|equals
argument_list|(
name|candidate
operator|.
name|getKey
argument_list|()
argument_list|)
condition|)
block|{
return|return
name|candidate
operator|.
name|getValue
argument_list|()
return|;
block|}
block|}
return|return
literal|null
return|;
block|}
comment|/**       * appends to the list      * @return null      */
specifier|synchronized
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
return|return
name|add
argument_list|(
name|tx
argument_list|,
name|key
argument_list|,
name|value
argument_list|)
return|;
block|}
specifier|synchronized
specifier|public
name|Value
name|add
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
name|assertLoaded
argument_list|()
expr_stmt|;
name|getTail
argument_list|(
name|tx
argument_list|)
operator|.
name|put
argument_list|(
name|tx
argument_list|,
name|key
argument_list|,
name|value
argument_list|)
expr_stmt|;
name|size
operator|.
name|incrementAndGet
argument_list|()
expr_stmt|;
return|return
literal|null
return|;
block|}
specifier|synchronized
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
name|assertLoaded
argument_list|()
expr_stmt|;
name|getHead
argument_list|(
name|tx
argument_list|)
operator|.
name|addFirst
argument_list|(
name|tx
argument_list|,
name|key
argument_list|,
name|value
argument_list|)
expr_stmt|;
name|size
operator|.
name|incrementAndGet
argument_list|()
expr_stmt|;
return|return
literal|null
return|;
block|}
specifier|synchronized
specifier|public
name|Value
name|remove
parameter_list|(
name|Transaction
name|tx
parameter_list|,
name|Key
name|key
parameter_list|)
throws|throws
name|IOException
block|{
name|assertLoaded
argument_list|()
expr_stmt|;
for|for
control|(
name|Iterator
argument_list|<
name|Map
operator|.
name|Entry
argument_list|<
name|Key
argument_list|,
name|Value
argument_list|>
argument_list|>
name|iterator
init|=
name|iterator
argument_list|(
name|tx
argument_list|)
init|;
name|iterator
operator|.
name|hasNext
argument_list|()
condition|;
control|)
block|{
name|Map
operator|.
name|Entry
argument_list|<
name|Key
argument_list|,
name|Value
argument_list|>
name|candidate
init|=
name|iterator
operator|.
name|next
argument_list|()
decl_stmt|;
if|if
condition|(
name|key
operator|.
name|equals
argument_list|(
name|candidate
operator|.
name|getKey
argument_list|()
argument_list|)
condition|)
block|{
name|iterator
operator|.
name|remove
argument_list|()
expr_stmt|;
return|return
name|candidate
operator|.
name|getValue
argument_list|()
return|;
block|}
block|}
return|return
literal|null
return|;
block|}
specifier|public
name|void
name|onRemove
parameter_list|()
block|{
name|size
operator|.
name|decrementAndGet
argument_list|()
expr_stmt|;
block|}
specifier|public
name|boolean
name|isTransient
parameter_list|()
block|{
return|return
literal|false
return|;
block|}
specifier|synchronized
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
for|for
control|(
name|Iterator
argument_list|<
name|ListNode
argument_list|<
name|Key
argument_list|,
name|Value
argument_list|>
argument_list|>
name|iterator
init|=
name|listNodeIterator
argument_list|(
name|tx
argument_list|)
init|;
name|iterator
operator|.
name|hasNext
argument_list|()
condition|;
control|)
block|{
name|ListNode
argument_list|<
name|Key
argument_list|,
name|Value
argument_list|>
name|candidate
init|=
name|iterator
operator|.
name|next
argument_list|()
decl_stmt|;
name|candidate
operator|.
name|clear
argument_list|(
name|tx
argument_list|)
expr_stmt|;
comment|// break up the transaction
name|tx
operator|.
name|commit
argument_list|()
expr_stmt|;
block|}
name|size
operator|.
name|set
argument_list|(
literal|0
argument_list|)
expr_stmt|;
block|}
specifier|synchronized
specifier|public
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
name|Transaction
name|tx
parameter_list|)
throws|throws
name|IOException
block|{
return|return
name|getHead
argument_list|(
name|tx
argument_list|)
operator|.
name|listNodeIterator
argument_list|(
name|tx
argument_list|)
return|;
block|}
specifier|synchronized
specifier|public
name|boolean
name|isEmpty
parameter_list|(
specifier|final
name|Transaction
name|tx
parameter_list|)
throws|throws
name|IOException
block|{
return|return
name|getHead
argument_list|(
name|tx
argument_list|)
operator|.
name|isEmpty
argument_list|(
name|tx
argument_list|)
return|;
block|}
specifier|synchronized
specifier|public
name|Iterator
argument_list|<
name|Map
operator|.
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
name|getHead
argument_list|(
name|tx
argument_list|)
operator|.
name|iterator
argument_list|(
name|tx
argument_list|)
return|;
block|}
specifier|synchronized
specifier|public
name|Iterator
argument_list|<
name|Map
operator|.
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
name|initialPosition
parameter_list|)
throws|throws
name|IOException
block|{
return|return
name|getHead
argument_list|(
name|tx
argument_list|)
operator|.
name|iterator
argument_list|(
name|tx
argument_list|,
name|initialPosition
argument_list|)
return|;
block|}
specifier|synchronized
specifier|public
name|Map
operator|.
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
throws|throws
name|IOException
block|{
return|return
name|getHead
argument_list|(
name|tx
argument_list|)
operator|.
name|getFirst
argument_list|(
name|tx
argument_list|)
return|;
block|}
specifier|synchronized
specifier|public
name|Map
operator|.
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
throws|throws
name|IOException
block|{
return|return
name|getTail
argument_list|(
name|tx
argument_list|)
operator|.
name|getLast
argument_list|(
name|tx
argument_list|)
return|;
block|}
specifier|private
name|void
name|assertLoaded
parameter_list|()
throws|throws
name|IllegalStateException
block|{
if|if
condition|(
operator|!
name|loaded
operator|.
name|get
argument_list|()
condition|)
block|{
throw|throw
operator|new
name|IllegalStateException
argument_list|(
literal|"TheListIndex is not loaded"
argument_list|)
throw|;
block|}
block|}
name|ListNode
argument_list|<
name|Key
argument_list|,
name|Value
argument_list|>
name|loadNode
parameter_list|(
name|Transaction
name|tx
parameter_list|,
name|long
name|pageId
parameter_list|)
throws|throws
name|IOException
block|{
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
init|=
name|tx
operator|.
name|load
argument_list|(
name|pageId
argument_list|,
name|marshaller
argument_list|)
decl_stmt|;
name|ListNode
argument_list|<
name|Key
argument_list|,
name|Value
argument_list|>
name|node
init|=
name|page
operator|.
name|get
argument_list|()
decl_stmt|;
name|node
operator|.
name|setPage
argument_list|(
name|page
argument_list|)
expr_stmt|;
name|node
operator|.
name|setContainingList
argument_list|(
name|this
argument_list|)
expr_stmt|;
return|return
name|node
return|;
block|}
name|ListNode
argument_list|<
name|Key
argument_list|,
name|Value
argument_list|>
name|createNode
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
name|setPage
argument_list|(
name|page
argument_list|)
expr_stmt|;
name|page
operator|.
name|set
argument_list|(
name|node
argument_list|)
expr_stmt|;
name|node
operator|.
name|setContainingList
argument_list|(
name|this
argument_list|)
expr_stmt|;
return|return
name|node
return|;
block|}
specifier|public
name|ListNode
argument_list|<
name|Key
argument_list|,
name|Value
argument_list|>
name|createNode
parameter_list|(
name|Transaction
name|tx
parameter_list|)
throws|throws
name|IOException
block|{
return|return
name|createNode
argument_list|(
name|tx
operator|.
expr|<
name|ListNode
argument_list|<
name|Key
argument_list|,
name|Value
argument_list|>
operator|>
name|load
argument_list|(
name|tx
operator|.
expr|<
name|ListNode
argument_list|<
name|Key
argument_list|,
name|Value
argument_list|>
operator|>
name|allocate
argument_list|()
operator|.
name|getPageId
argument_list|()
argument_list|,
literal|null
argument_list|)
argument_list|)
return|;
block|}
specifier|public
name|void
name|storeNode
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
name|node
parameter_list|,
name|boolean
name|overflow
parameter_list|)
throws|throws
name|IOException
block|{
name|tx
operator|.
name|store
argument_list|(
name|node
operator|.
name|getPage
argument_list|()
argument_list|,
name|marshaller
argument_list|,
name|overflow
argument_list|)
expr_stmt|;
block|}
specifier|public
name|PageFile
name|getPageFile
parameter_list|()
block|{
return|return
name|pageFile
return|;
block|}
specifier|public
name|void
name|setPageFile
parameter_list|(
name|PageFile
name|pageFile
parameter_list|)
block|{
name|this
operator|.
name|pageFile
operator|=
name|pageFile
expr_stmt|;
block|}
specifier|public
name|long
name|getHeadPageId
parameter_list|()
block|{
return|return
name|headPageId
return|;
block|}
specifier|public
name|void
name|setHeadPageId
parameter_list|(
name|long
name|headPageId
parameter_list|)
block|{
name|this
operator|.
name|headPageId
operator|=
name|headPageId
expr_stmt|;
block|}
specifier|public
name|Marshaller
argument_list|<
name|Key
argument_list|>
name|getKeyMarshaller
parameter_list|()
block|{
return|return
name|keyMarshaller
return|;
block|}
specifier|public
name|void
name|setKeyMarshaller
parameter_list|(
name|Marshaller
argument_list|<
name|Key
argument_list|>
name|keyMarshaller
parameter_list|)
block|{
name|this
operator|.
name|keyMarshaller
operator|=
name|keyMarshaller
expr_stmt|;
block|}
specifier|public
name|Marshaller
argument_list|<
name|Value
argument_list|>
name|getValueMarshaller
parameter_list|()
block|{
return|return
name|valueMarshaller
return|;
block|}
specifier|public
name|void
name|setValueMarshaller
parameter_list|(
name|Marshaller
argument_list|<
name|Value
argument_list|>
name|valueMarshaller
parameter_list|)
block|{
name|this
operator|.
name|valueMarshaller
operator|=
name|valueMarshaller
expr_stmt|;
block|}
specifier|public
name|void
name|setTailPageId
parameter_list|(
name|long
name|tailPageId
parameter_list|)
block|{
name|this
operator|.
name|tailPageId
operator|=
name|tailPageId
expr_stmt|;
block|}
specifier|public
name|long
name|getTailPageId
parameter_list|()
block|{
return|return
name|tailPageId
return|;
block|}
specifier|public
name|long
name|size
parameter_list|()
block|{
return|return
name|size
operator|.
name|get
argument_list|()
return|;
block|}
block|}
end_class

end_unit

