begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  *  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  * http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
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

begin_interface
interface|interface
name|IndexLinkedList
block|{
comment|/**      * @return the root used by the List      */
specifier|public
name|IndexItem
name|getRoot
parameter_list|()
function_decl|;
comment|/**      * Returns the first element in this list.      *       * @return the first element in this list.      */
specifier|public
name|IndexItem
name|getFirst
parameter_list|()
function_decl|;
comment|/**      * Returns the last element in this list.      *       * @return the last element in this list.      */
specifier|public
name|IndexItem
name|getLast
parameter_list|()
function_decl|;
comment|/**      * Removes and returns the first element from this list.      *       * @return the first element from this list.      */
specifier|public
name|IndexItem
name|removeFirst
parameter_list|()
function_decl|;
comment|/**      * Removes and returns the last element from this list.      *       * @return the last element from this list.      */
specifier|public
name|Object
name|removeLast
parameter_list|()
function_decl|;
comment|/**      * Inserts the given element at the beginning of this list.      *       * @param o the element to be inserted at the beginning of this list.      */
specifier|public
name|void
name|addFirst
parameter_list|(
name|IndexItem
name|item
parameter_list|)
function_decl|;
comment|/**      * Appends the given element to the end of this list. (Identical in function to the<tt>add</tt> method; included      * only for consistency.)      *       * @param o the element to be inserted at the end of this list.      */
specifier|public
name|void
name|addLast
parameter_list|(
name|IndexItem
name|item
parameter_list|)
function_decl|;
comment|/**      * Returns the number of elements in this list.      *       * @return the number of elements in this list.      */
specifier|public
name|int
name|size
parameter_list|()
function_decl|;
comment|/**      * is the list empty?      *       * @return true if there are no elements in the list      */
specifier|public
name|boolean
name|isEmpty
parameter_list|()
function_decl|;
comment|/**      * Appends the specified element to the end of this list.      *       * @param o element to be appended to this list.      * @return<tt>true</tt> (as per the general contract of<tt>Collection.add</tt>).      */
specifier|public
name|boolean
name|add
parameter_list|(
name|IndexItem
name|item
parameter_list|)
function_decl|;
comment|/**      * Removes all of the elements from this list.      */
specifier|public
name|void
name|clear
parameter_list|()
function_decl|;
comment|// Positional Access Operations
comment|/**      * Returns the element at the specified position in this list.      *       * @param index index of element to return.      * @return the element at the specified position in this list.      *       * @throws IndexOutOfBoundsException if the specified index is is out of range (<tt>index&lt; 0 || index&gt;= size()</tt>).      */
specifier|public
name|IndexItem
name|get
parameter_list|(
name|int
name|index
parameter_list|)
function_decl|;
comment|/**      * Inserts the specified element at the specified position in this list. Shifts the element currently at that      * position (if any) and any subsequent elements to the right (adds one to their indices).      *       * @param index index at which the specified element is to be inserted.      * @param element element to be inserted.      *       * @throws IndexOutOfBoundsException if the specified index is out of range (<tt>index&lt; 0 || index&gt; size()</tt>).      */
specifier|public
name|void
name|add
parameter_list|(
name|int
name|index
parameter_list|,
name|IndexItem
name|element
parameter_list|)
function_decl|;
comment|/**      * Removes the element at the specified position in this list. Shifts any subsequent elements to the left (subtracts      * one from their indices). Returns the element that was removed from the list.      *       * @param index the index of the element to removed.      * @return the element previously at the specified position.      *       * @throws IndexOutOfBoundsException if the specified index is out of range (<tt>index&lt; 0 || index&gt;= size()</tt>).      */
specifier|public
name|Object
name|remove
parameter_list|(
name|int
name|index
parameter_list|)
function_decl|;
comment|// Search Operations
comment|/**      * Returns the index in this list of the first occurrence of the specified element, or -1 if the List does not      * contain this element. More formally, returns the lowest index i such that      *<tt>(o==null ? get(i)==null : o.equals(get(i)))</tt>, or -1 if there is no such index.      *       * @param o element to search for.      * @return the index in this list of the first occurrence of the specified element, or -1 if the list does not      *         contain this element.      */
specifier|public
name|int
name|indexOf
parameter_list|(
name|IndexItem
name|o
parameter_list|)
function_decl|;
comment|/**      * Retrieve the next entry after this entry      *       * @param entry      * @return next entry      */
specifier|public
name|IndexItem
name|getNextEntry
parameter_list|(
name|IndexItem
name|entry
parameter_list|)
function_decl|;
comment|/**      * Retrive the prev entry after this entry      *       * @param entry      * @return prev entry      */
specifier|public
name|IndexItem
name|getPrevEntry
parameter_list|(
name|IndexItem
name|entry
parameter_list|)
function_decl|;
comment|/**      * remove an entry      * @param e      */
specifier|public
name|void
name|remove
parameter_list|(
name|IndexItem
name|e
parameter_list|)
function_decl|;
comment|/**      * Ensure we have the up to date entry      * @param current      * @return the entry      */
specifier|public
name|IndexItem
name|getEntry
parameter_list|(
name|IndexItem
name|current
parameter_list|)
function_decl|;
block|}
end_interface

end_unit

