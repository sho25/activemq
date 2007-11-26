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
name|org
operator|.
name|apache
operator|.
name|activemq
operator|.
name|kaha
operator|.
name|StoreEntry
import|;
end_import

begin_comment
comment|/**  * A linked list used by IndexItems  *   * @version $Revision$  */
end_comment

begin_class
specifier|public
class|class
name|DiskIndexLinkedList
implements|implements
name|IndexLinkedList
block|{
specifier|protected
name|IndexManager
name|indexManager
decl_stmt|;
specifier|protected
specifier|transient
name|IndexItem
name|root
decl_stmt|;
specifier|protected
specifier|transient
name|IndexItem
name|last
decl_stmt|;
specifier|protected
specifier|transient
name|int
name|size
decl_stmt|;
comment|/**      * Constructs an empty list.      */
specifier|public
name|DiskIndexLinkedList
parameter_list|(
name|IndexManager
name|im
parameter_list|,
name|IndexItem
name|header
parameter_list|)
block|{
name|this
operator|.
name|indexManager
operator|=
name|im
expr_stmt|;
name|this
operator|.
name|root
operator|=
name|header
expr_stmt|;
block|}
specifier|public
specifier|synchronized
name|IndexItem
name|getRoot
parameter_list|()
block|{
return|return
name|root
return|;
block|}
specifier|public
name|void
name|setRoot
parameter_list|(
name|IndexItem
name|e
parameter_list|)
block|{
name|this
operator|.
name|root
operator|=
name|e
expr_stmt|;
block|}
comment|/**      * Returns the first element in this list.      *       * @return the first element in this list.      */
specifier|public
specifier|synchronized
name|IndexItem
name|getFirst
parameter_list|()
block|{
if|if
condition|(
name|size
operator|==
literal|0
condition|)
block|{
return|return
literal|null
return|;
block|}
return|return
name|getNextEntry
argument_list|(
name|root
argument_list|)
return|;
block|}
comment|/**      * Returns the last element in this list.      *       * @return the last element in this list.      */
specifier|public
specifier|synchronized
name|IndexItem
name|getLast
parameter_list|()
block|{
if|if
condition|(
name|size
operator|==
literal|0
condition|)
block|{
return|return
literal|null
return|;
block|}
if|if
condition|(
name|last
operator|!=
literal|null
condition|)
block|{
name|last
operator|.
name|next
operator|=
literal|null
expr_stmt|;
name|last
operator|.
name|setNextItem
argument_list|(
name|IndexItem
operator|.
name|POSITION_NOT_SET
argument_list|)
expr_stmt|;
block|}
return|return
name|last
return|;
block|}
comment|/**      * Removes and returns the first element from this list.      *       * @return the first element from this list.      */
specifier|public
specifier|synchronized
name|StoreEntry
name|removeFirst
parameter_list|()
block|{
if|if
condition|(
name|size
operator|==
literal|0
condition|)
block|{
return|return
literal|null
return|;
block|}
name|IndexItem
name|result
init|=
name|getNextEntry
argument_list|(
name|root
argument_list|)
decl_stmt|;
name|remove
argument_list|(
name|result
argument_list|)
expr_stmt|;
return|return
name|result
return|;
block|}
comment|/**      * Removes and returns the last element from this list.      *       * @return the last element from this list.      */
specifier|public
specifier|synchronized
name|Object
name|removeLast
parameter_list|()
block|{
if|if
condition|(
name|size
operator|==
literal|0
condition|)
block|{
return|return
literal|null
return|;
block|}
name|StoreEntry
name|result
init|=
name|last
decl_stmt|;
name|remove
argument_list|(
name|last
argument_list|)
expr_stmt|;
return|return
name|result
return|;
block|}
comment|/**      * Inserts the given element at the beginning of this list.      *       * @param o the element to be inserted at the beginning of this list.      */
specifier|public
specifier|synchronized
name|void
name|addFirst
parameter_list|(
name|IndexItem
name|item
parameter_list|)
block|{
if|if
condition|(
name|size
operator|==
literal|0
condition|)
block|{
name|last
operator|=
name|item
expr_stmt|;
block|}
name|size
operator|++
expr_stmt|;
block|}
comment|/**      * Appends the given element to the end of this list. (Identical in function      * to the<tt>add</tt> method; included only for consistency.)      *       * @param o the element to be inserted at the end of this list.      */
specifier|public
specifier|synchronized
name|void
name|addLast
parameter_list|(
name|IndexItem
name|item
parameter_list|)
block|{
name|size
operator|++
expr_stmt|;
name|last
operator|=
name|item
expr_stmt|;
block|}
comment|/**      * Returns the number of elements in this list.      *       * @return the number of elements in this list.      */
specifier|public
specifier|synchronized
name|int
name|size
parameter_list|()
block|{
return|return
name|size
return|;
block|}
comment|/**      * is the list empty?      *       * @return true if there are no elements in the list      */
specifier|public
specifier|synchronized
name|boolean
name|isEmpty
parameter_list|()
block|{
return|return
name|size
operator|==
literal|0
return|;
block|}
comment|/**      * Appends the specified element to the end of this list.      *       * @param o element to be appended to this list.      * @return<tt>true</tt> (as per the general contract of      *<tt>Collection.add</tt>).      */
specifier|public
specifier|synchronized
name|boolean
name|add
parameter_list|(
name|IndexItem
name|item
parameter_list|)
block|{
name|addLast
argument_list|(
name|item
argument_list|)
expr_stmt|;
return|return
literal|true
return|;
block|}
comment|/**      * Removes all of the elements from this list.      */
specifier|public
specifier|synchronized
name|void
name|clear
parameter_list|()
block|{
name|last
operator|=
literal|null
expr_stmt|;
name|size
operator|=
literal|0
expr_stmt|;
block|}
comment|// Positional Access Operations
comment|/**      * Returns the element at the specified position in this list.      *       * @param index index of element to return.      * @return the element at the specified position in this list.      * @throws IndexOutOfBoundsException if the specified index is is out of      *                 range (<tt>index&lt; 0 || index&gt;= size()</tt>).      */
specifier|public
specifier|synchronized
name|IndexItem
name|get
parameter_list|(
name|int
name|index
parameter_list|)
block|{
return|return
name|entry
argument_list|(
name|index
argument_list|)
return|;
block|}
comment|/**      * Inserts the specified element at the specified position in this list.      * Shifts the element currently at that position (if any) and any subsequent      * elements to the right (adds one to their indices).      *       * @param index index at which the specified element is to be inserted.      * @param element element to be inserted.      * @throws IndexOutOfBoundsException if the specified index is out of range (<tt>index&lt; 0 || index&gt; size()</tt>).      */
specifier|public
specifier|synchronized
name|void
name|add
parameter_list|(
name|int
name|index
parameter_list|,
name|IndexItem
name|element
parameter_list|)
block|{
if|if
condition|(
name|index
operator|==
name|size
condition|)
block|{
name|last
operator|=
name|element
expr_stmt|;
block|}
name|size
operator|++
expr_stmt|;
block|}
comment|/**      * Removes the element at the specified position in this list. Shifts any      * subsequent elements to the left (subtracts one from their indices).      * Returns the element that was removed from the list.      *       * @param index the index of the element to removed.      * @return the element previously at the specified position.      * @throws IndexOutOfBoundsException if the specified index is out of range (<tt>index&lt; 0 || index&gt;= size()</tt>).      */
specifier|public
specifier|synchronized
name|Object
name|remove
parameter_list|(
name|int
name|index
parameter_list|)
block|{
name|IndexItem
name|e
init|=
name|entry
argument_list|(
name|index
argument_list|)
decl_stmt|;
name|remove
argument_list|(
name|e
argument_list|)
expr_stmt|;
return|return
name|e
return|;
block|}
comment|/**      * Return the indexed entry.      */
specifier|private
name|IndexItem
name|entry
parameter_list|(
name|int
name|index
parameter_list|)
block|{
if|if
condition|(
name|index
operator|<
literal|0
operator|||
name|index
operator|>=
name|size
condition|)
block|{
throw|throw
operator|new
name|IndexOutOfBoundsException
argument_list|(
literal|"Index: "
operator|+
name|index
operator|+
literal|", Size: "
operator|+
name|size
argument_list|)
throw|;
block|}
name|IndexItem
name|e
init|=
name|root
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<=
name|index
condition|;
name|i
operator|++
control|)
block|{
name|e
operator|=
name|getNextEntry
argument_list|(
name|e
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|e
operator|!=
literal|null
operator|&&
name|last
operator|!=
literal|null
operator|&&
name|last
operator|.
name|equals
argument_list|(
name|e
argument_list|)
condition|)
block|{
name|last
operator|=
name|e
expr_stmt|;
block|}
return|return
name|e
return|;
block|}
comment|// Search Operations
comment|/**      * Returns the index in this list of the first occurrence of the specified      * element, or -1 if the List does not contain this element. More formally,      * returns the lowest index i such that      *<tt>(o==null ? get(i)==null : o.equals(get(i)))</tt>, or -1 if there      * is no such index.      *       * @param o element to search for.      * @return the index in this list of the first occurrence of the specified      *         element, or -1 if the list does not contain this element.      */
specifier|public
specifier|synchronized
name|int
name|indexOf
parameter_list|(
name|StoreEntry
name|o
parameter_list|)
block|{
name|int
name|index
init|=
literal|0
decl_stmt|;
if|if
condition|(
name|size
operator|>
literal|0
condition|)
block|{
for|for
control|(
name|IndexItem
name|e
init|=
name|getNextEntry
argument_list|(
name|root
argument_list|)
init|;
name|e
operator|!=
literal|null
condition|;
name|e
operator|=
name|getNextEntry
argument_list|(
name|e
argument_list|)
control|)
block|{
if|if
condition|(
name|o
operator|.
name|equals
argument_list|(
name|e
argument_list|)
condition|)
block|{
return|return
name|index
return|;
block|}
name|index
operator|++
expr_stmt|;
block|}
block|}
return|return
operator|-
literal|1
return|;
block|}
comment|/**      * Retrieve the next entry after this entry      *       * @param entry      * @return next entry      */
specifier|public
specifier|synchronized
name|IndexItem
name|getNextEntry
parameter_list|(
name|IndexItem
name|current
parameter_list|)
block|{
name|IndexItem
name|result
init|=
literal|null
decl_stmt|;
if|if
condition|(
name|current
operator|!=
literal|null
condition|)
block|{
name|current
operator|=
operator|(
name|IndexItem
operator|)
name|refreshEntry
argument_list|(
name|current
argument_list|)
expr_stmt|;
if|if
condition|(
name|current
operator|.
name|getNextItem
argument_list|()
operator|>=
literal|0
condition|)
block|{
try|try
block|{
name|result
operator|=
name|indexManager
operator|.
name|getIndex
argument_list|(
name|current
operator|.
name|getNextItem
argument_list|()
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|RuntimeException
argument_list|(
literal|"Failed to get next index from "
operator|+
name|indexManager
operator|+
literal|" for "
operator|+
name|current
argument_list|,
name|e
argument_list|)
throw|;
block|}
block|}
block|}
comment|// essential last get's updated consistently
if|if
condition|(
name|result
operator|!=
literal|null
operator|&&
name|last
operator|!=
literal|null
operator|&&
name|last
operator|.
name|equals
argument_list|(
name|result
argument_list|)
condition|)
block|{
name|result
operator|=
name|last
expr_stmt|;
block|}
return|return
name|result
return|;
block|}
comment|/** 	 * Retrive the prev entry after this entry 	 *  	 * @param entry 	 * @return prev entry 	 */
specifier|public
specifier|synchronized
name|IndexItem
name|getPrevEntry
parameter_list|(
name|IndexItem
name|current
parameter_list|)
block|{
name|IndexItem
name|result
init|=
literal|null
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
name|getPreviousItem
argument_list|()
operator|>=
literal|0
condition|)
block|{
name|current
operator|=
operator|(
name|IndexItem
operator|)
name|refreshEntry
argument_list|(
name|current
argument_list|)
expr_stmt|;
try|try
block|{
name|result
operator|=
name|indexManager
operator|.
name|getIndex
argument_list|(
name|current
operator|.
name|getPreviousItem
argument_list|()
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|RuntimeException
argument_list|(
literal|"Failed to  get current index for "
operator|+
name|current
argument_list|,
name|e
argument_list|)
throw|;
block|}
block|}
block|}
comment|// essential root get's updated consistently
if|if
condition|(
name|result
operator|!=
literal|null
operator|&&
name|root
operator|!=
literal|null
operator|&&
name|root
operator|.
name|equals
argument_list|(
name|result
argument_list|)
condition|)
block|{
return|return
literal|null
return|;
block|}
return|return
name|result
return|;
block|}
specifier|public
specifier|synchronized
name|StoreEntry
name|getEntry
parameter_list|(
name|StoreEntry
name|current
parameter_list|)
block|{
name|StoreEntry
name|result
init|=
literal|null
decl_stmt|;
if|if
condition|(
name|current
operator|!=
literal|null
operator|&&
name|current
operator|.
name|getOffset
argument_list|()
operator|>=
literal|0
condition|)
block|{
try|try
block|{
name|result
operator|=
name|indexManager
operator|.
name|getIndex
argument_list|(
name|current
operator|.
name|getOffset
argument_list|()
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|RuntimeException
argument_list|(
literal|"Failed to index"
argument_list|,
name|e
argument_list|)
throw|;
block|}
block|}
comment|// essential root get's updated consistently
if|if
condition|(
name|result
operator|!=
literal|null
operator|&&
name|root
operator|!=
literal|null
operator|&&
name|root
operator|.
name|equals
argument_list|(
name|result
argument_list|)
condition|)
block|{
return|return
name|root
return|;
block|}
return|return
name|result
return|;
block|}
comment|/**      * Update the indexes of a StoreEntry      *       * @param current      */
specifier|public
specifier|synchronized
name|StoreEntry
name|refreshEntry
parameter_list|(
name|StoreEntry
name|current
parameter_list|)
block|{
name|StoreEntry
name|result
init|=
literal|null
decl_stmt|;
if|if
condition|(
name|current
operator|!=
literal|null
operator|&&
name|current
operator|.
name|getOffset
argument_list|()
operator|>=
literal|0
condition|)
block|{
try|try
block|{
name|result
operator|=
name|indexManager
operator|.
name|refreshIndex
argument_list|(
operator|(
name|IndexItem
operator|)
name|current
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|RuntimeException
argument_list|(
literal|"Failed to index"
argument_list|,
name|e
argument_list|)
throw|;
block|}
block|}
comment|// essential root get's updated consistently
if|if
condition|(
name|result
operator|!=
literal|null
operator|&&
name|root
operator|!=
literal|null
operator|&&
name|root
operator|.
name|equals
argument_list|(
name|result
argument_list|)
condition|)
block|{
return|return
name|root
return|;
block|}
return|return
name|result
return|;
block|}
specifier|public
specifier|synchronized
name|void
name|remove
parameter_list|(
name|IndexItem
name|e
parameter_list|)
block|{
if|if
condition|(
name|e
operator|==
literal|null
operator|||
name|e
operator|==
name|root
operator|||
name|e
operator|.
name|equals
argument_list|(
name|root
argument_list|)
condition|)
block|{
return|return;
block|}
if|if
condition|(
name|e
operator|==
name|last
operator|||
name|e
operator|.
name|equals
argument_list|(
name|last
argument_list|)
condition|)
block|{
if|if
condition|(
name|size
operator|>
literal|1
condition|)
block|{
name|last
operator|=
operator|(
name|IndexItem
operator|)
name|refreshEntry
argument_list|(
name|last
argument_list|)
expr_stmt|;
name|last
operator|=
name|getPrevEntry
argument_list|(
name|last
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|last
operator|=
literal|null
expr_stmt|;
block|}
block|}
name|size
operator|--
expr_stmt|;
block|}
block|}
end_class

end_unit

