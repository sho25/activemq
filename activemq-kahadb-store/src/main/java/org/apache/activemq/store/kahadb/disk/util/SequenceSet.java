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
name|store
operator|.
name|kahadb
operator|.
name|disk
operator|.
name|util
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
name|ArrayList
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
name|NoSuchElementException
import|;
end_import

begin_comment
comment|/**  * Keeps track of a added long values. Collapses ranges of numbers using a  * Sequence representation. Use to keep track of received message ids to find  * out if a message is duplicate or if there are any missing messages.  *  * @author chirino  */
end_comment

begin_class
specifier|public
class|class
name|SequenceSet
extends|extends
name|LinkedNodeList
argument_list|<
name|Sequence
argument_list|>
implements|implements
name|Iterable
argument_list|<
name|Long
argument_list|>
block|{
specifier|public
specifier|static
class|class
name|Marshaller
implements|implements
name|org
operator|.
name|apache
operator|.
name|activemq
operator|.
name|store
operator|.
name|kahadb
operator|.
name|disk
operator|.
name|util
operator|.
name|Marshaller
argument_list|<
name|SequenceSet
argument_list|>
block|{
specifier|public
specifier|static
specifier|final
name|Marshaller
name|INSTANCE
init|=
operator|new
name|Marshaller
argument_list|()
decl_stmt|;
specifier|public
name|SequenceSet
name|readPayload
parameter_list|(
name|DataInput
name|in
parameter_list|)
throws|throws
name|IOException
block|{
name|SequenceSet
name|value
init|=
operator|new
name|SequenceSet
argument_list|()
decl_stmt|;
name|int
name|count
init|=
name|in
operator|.
name|readInt
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
name|count
condition|;
name|i
operator|++
control|)
block|{
if|if
condition|(
name|in
operator|.
name|readBoolean
argument_list|()
condition|)
block|{
name|Sequence
name|sequence
init|=
operator|new
name|Sequence
argument_list|(
name|in
operator|.
name|readLong
argument_list|()
argument_list|,
name|in
operator|.
name|readLong
argument_list|()
argument_list|)
decl_stmt|;
name|value
operator|.
name|addLast
argument_list|(
name|sequence
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|Sequence
name|sequence
init|=
operator|new
name|Sequence
argument_list|(
name|in
operator|.
name|readLong
argument_list|()
argument_list|)
decl_stmt|;
name|value
operator|.
name|addLast
argument_list|(
name|sequence
argument_list|)
expr_stmt|;
block|}
block|}
return|return
name|value
return|;
block|}
specifier|public
name|void
name|writePayload
parameter_list|(
name|SequenceSet
name|value
parameter_list|,
name|DataOutput
name|out
parameter_list|)
throws|throws
name|IOException
block|{
name|out
operator|.
name|writeInt
argument_list|(
name|value
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|Sequence
name|sequence
init|=
name|value
operator|.
name|getHead
argument_list|()
decl_stmt|;
while|while
condition|(
name|sequence
operator|!=
literal|null
condition|)
block|{
if|if
condition|(
name|sequence
operator|.
name|range
argument_list|()
operator|>
literal|1
condition|)
block|{
name|out
operator|.
name|writeBoolean
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|out
operator|.
name|writeLong
argument_list|(
name|sequence
operator|.
name|first
argument_list|)
expr_stmt|;
name|out
operator|.
name|writeLong
argument_list|(
name|sequence
operator|.
name|last
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|out
operator|.
name|writeBoolean
argument_list|(
literal|false
argument_list|)
expr_stmt|;
name|out
operator|.
name|writeLong
argument_list|(
name|sequence
operator|.
name|first
argument_list|)
expr_stmt|;
block|}
name|sequence
operator|=
name|sequence
operator|.
name|getNext
argument_list|()
expr_stmt|;
block|}
block|}
specifier|public
name|int
name|getFixedSize
parameter_list|()
block|{
return|return
operator|-
literal|1
return|;
block|}
specifier|public
name|SequenceSet
name|deepCopy
parameter_list|(
name|SequenceSet
name|value
parameter_list|)
block|{
name|SequenceSet
name|rc
init|=
operator|new
name|SequenceSet
argument_list|()
decl_stmt|;
name|Sequence
name|sequence
init|=
name|value
operator|.
name|getHead
argument_list|()
decl_stmt|;
while|while
condition|(
name|sequence
operator|!=
literal|null
condition|)
block|{
name|rc
operator|.
name|add
argument_list|(
operator|new
name|Sequence
argument_list|(
name|sequence
operator|.
name|first
argument_list|,
name|sequence
operator|.
name|last
argument_list|)
argument_list|)
expr_stmt|;
name|sequence
operator|=
name|sequence
operator|.
name|getNext
argument_list|()
expr_stmt|;
block|}
return|return
name|rc
return|;
block|}
specifier|public
name|boolean
name|isDeepCopySupported
parameter_list|()
block|{
return|return
literal|true
return|;
block|}
block|}
specifier|public
name|void
name|add
parameter_list|(
name|Sequence
name|value
parameter_list|)
block|{
comment|// TODO we can probably optimize this a bit
for|for
control|(
name|long
name|i
init|=
name|value
operator|.
name|first
init|;
name|i
operator|<
name|value
operator|.
name|last
operator|+
literal|1
condition|;
name|i
operator|++
control|)
block|{
name|add
argument_list|(
name|i
argument_list|)
expr_stmt|;
block|}
block|}
specifier|public
name|void
name|merge
parameter_list|(
name|SequenceSet
name|sequenceSet
parameter_list|)
block|{
name|Sequence
name|node
init|=
name|sequenceSet
operator|.
name|getHead
argument_list|()
decl_stmt|;
while|while
condition|(
name|node
operator|!=
literal|null
condition|)
block|{
name|add
argument_list|(
name|node
argument_list|)
expr_stmt|;
name|node
operator|=
name|node
operator|.
name|getNext
argument_list|()
expr_stmt|;
block|}
block|}
specifier|public
name|void
name|remove
parameter_list|(
name|SequenceSet
name|sequenceSet
parameter_list|)
block|{
name|Sequence
name|node
init|=
name|sequenceSet
operator|.
name|getHead
argument_list|()
decl_stmt|;
while|while
condition|(
name|node
operator|!=
literal|null
condition|)
block|{
name|remove
argument_list|(
name|node
argument_list|)
expr_stmt|;
name|node
operator|=
name|node
operator|.
name|getNext
argument_list|()
expr_stmt|;
block|}
block|}
specifier|public
name|void
name|remove
parameter_list|(
name|Sequence
name|value
parameter_list|)
block|{
for|for
control|(
name|long
name|i
init|=
name|value
operator|.
name|first
init|;
name|i
operator|<
name|value
operator|.
name|last
operator|+
literal|1
condition|;
name|i
operator|++
control|)
block|{
name|remove
argument_list|(
name|i
argument_list|)
expr_stmt|;
block|}
block|}
comment|/**      *      * @param value      *            the value to add to the list      * @return false if the value was a duplicate.      */
specifier|public
name|boolean
name|add
parameter_list|(
name|long
name|value
parameter_list|)
block|{
if|if
condition|(
name|isEmpty
argument_list|()
condition|)
block|{
name|addFirst
argument_list|(
operator|new
name|Sequence
argument_list|(
name|value
argument_list|)
argument_list|)
expr_stmt|;
return|return
literal|true
return|;
block|}
comment|// check for append
name|Sequence
name|sequence
init|=
name|getTail
argument_list|()
decl_stmt|;
if|if
condition|(
name|sequence
operator|.
name|isAdjacentToLast
argument_list|(
name|value
argument_list|)
condition|)
block|{
name|sequence
operator|.
name|last
operator|=
name|value
expr_stmt|;
return|return
literal|true
return|;
block|}
comment|// check if the value is greater than the bigger sequence value and if it's not adjacent to it
comment|// in this case, we are sure that the value should be add to the tail of the sequence.
if|if
condition|(
name|sequence
operator|.
name|isBiggerButNotAdjacentToLast
argument_list|(
name|value
argument_list|)
condition|)
block|{
name|addLast
argument_list|(
operator|new
name|Sequence
argument_list|(
name|value
argument_list|)
argument_list|)
expr_stmt|;
return|return
literal|true
return|;
block|}
name|sequence
operator|=
name|getHead
argument_list|()
expr_stmt|;
while|while
condition|(
name|sequence
operator|!=
literal|null
condition|)
block|{
if|if
condition|(
name|sequence
operator|.
name|isAdjacentToLast
argument_list|(
name|value
argument_list|)
condition|)
block|{
comment|// grow the sequence...
name|sequence
operator|.
name|last
operator|=
name|value
expr_stmt|;
comment|// it might connect us to the next sequence..
if|if
condition|(
name|sequence
operator|.
name|getNext
argument_list|()
operator|!=
literal|null
condition|)
block|{
name|Sequence
name|next
init|=
name|sequence
operator|.
name|getNext
argument_list|()
decl_stmt|;
if|if
condition|(
name|next
operator|.
name|isAdjacentToFirst
argument_list|(
name|value
argument_list|)
condition|)
block|{
comment|// Yep the sequence connected.. so join them.
name|sequence
operator|.
name|last
operator|=
name|next
operator|.
name|last
expr_stmt|;
name|next
operator|.
name|unlink
argument_list|()
expr_stmt|;
block|}
block|}
return|return
literal|true
return|;
block|}
if|if
condition|(
name|sequence
operator|.
name|isAdjacentToFirst
argument_list|(
name|value
argument_list|)
condition|)
block|{
comment|// grow the sequence...
name|sequence
operator|.
name|first
operator|=
name|value
expr_stmt|;
comment|// it might connect us to the previous
if|if
condition|(
name|sequence
operator|.
name|getPrevious
argument_list|()
operator|!=
literal|null
condition|)
block|{
name|Sequence
name|prev
init|=
name|sequence
operator|.
name|getPrevious
argument_list|()
decl_stmt|;
if|if
condition|(
name|prev
operator|.
name|isAdjacentToLast
argument_list|(
name|value
argument_list|)
condition|)
block|{
comment|// Yep the sequence connected.. so join them.
name|sequence
operator|.
name|first
operator|=
name|prev
operator|.
name|first
expr_stmt|;
name|prev
operator|.
name|unlink
argument_list|()
expr_stmt|;
block|}
block|}
return|return
literal|true
return|;
block|}
comment|// Did that value land before this sequence?
if|if
condition|(
name|value
operator|<
name|sequence
operator|.
name|first
condition|)
block|{
comment|// Then insert a new entry before this sequence item.
name|sequence
operator|.
name|linkBefore
argument_list|(
operator|new
name|Sequence
argument_list|(
name|value
argument_list|)
argument_list|)
expr_stmt|;
return|return
literal|true
return|;
block|}
comment|// Did that value land within the sequence? The it's a duplicate.
if|if
condition|(
name|sequence
operator|.
name|contains
argument_list|(
name|value
argument_list|)
condition|)
block|{
return|return
literal|false
return|;
block|}
name|sequence
operator|=
name|sequence
operator|.
name|getNext
argument_list|()
expr_stmt|;
block|}
comment|// Then the value is getting appended to the tail of the sequence.
name|addLast
argument_list|(
operator|new
name|Sequence
argument_list|(
name|value
argument_list|)
argument_list|)
expr_stmt|;
return|return
literal|true
return|;
block|}
comment|/**      * Removes the given value from the Sequence set, splitting a      * contained sequence if necessary.      *      * @param value      *          The value that should be removed from the SequenceSet.      *      * @return true if the value was removed from the set, false if there      *         was no sequence in the set that contained the given value.      */
specifier|public
name|boolean
name|remove
parameter_list|(
name|long
name|value
parameter_list|)
block|{
name|Sequence
name|sequence
init|=
name|getHead
argument_list|()
decl_stmt|;
while|while
condition|(
name|sequence
operator|!=
literal|null
condition|)
block|{
if|if
condition|(
name|sequence
operator|.
name|contains
argument_list|(
name|value
argument_list|)
condition|)
block|{
if|if
condition|(
name|sequence
operator|.
name|range
argument_list|()
operator|==
literal|1
condition|)
block|{
name|sequence
operator|.
name|unlink
argument_list|()
expr_stmt|;
return|return
literal|true
return|;
block|}
elseif|else
if|if
condition|(
name|sequence
operator|.
name|getFirst
argument_list|()
operator|==
name|value
condition|)
block|{
name|sequence
operator|.
name|setFirst
argument_list|(
name|value
operator|+
literal|1
argument_list|)
expr_stmt|;
return|return
literal|true
return|;
block|}
elseif|else
if|if
condition|(
name|sequence
operator|.
name|getLast
argument_list|()
operator|==
name|value
condition|)
block|{
name|sequence
operator|.
name|setLast
argument_list|(
name|value
operator|-
literal|1
argument_list|)
expr_stmt|;
return|return
literal|true
return|;
block|}
else|else
block|{
name|sequence
operator|.
name|linkBefore
argument_list|(
operator|new
name|Sequence
argument_list|(
name|sequence
operator|.
name|first
argument_list|,
name|value
operator|-
literal|1
argument_list|)
argument_list|)
expr_stmt|;
name|sequence
operator|.
name|linkAfter
argument_list|(
operator|new
name|Sequence
argument_list|(
name|value
operator|+
literal|1
argument_list|,
name|sequence
operator|.
name|last
argument_list|)
argument_list|)
expr_stmt|;
name|sequence
operator|.
name|unlink
argument_list|()
expr_stmt|;
return|return
literal|true
return|;
block|}
block|}
name|sequence
operator|=
name|sequence
operator|.
name|getNext
argument_list|()
expr_stmt|;
block|}
return|return
literal|false
return|;
block|}
comment|/**      * Removes and returns the first element from this list.      *      * @return the first element from this list.      * @throws NoSuchElementException if this list is empty.      */
specifier|public
name|long
name|removeFirst
parameter_list|()
block|{
if|if
condition|(
name|isEmpty
argument_list|()
condition|)
block|{
throw|throw
operator|new
name|NoSuchElementException
argument_list|()
throw|;
block|}
name|Sequence
name|rc
init|=
name|removeFirstSequence
argument_list|(
literal|1
argument_list|)
decl_stmt|;
return|return
name|rc
operator|.
name|first
return|;
block|}
comment|/**      * Removes and returns the last sequence from this list.      *      * @return the last sequence from this list or null if the list is empty.      */
specifier|public
name|Sequence
name|removeLastSequence
parameter_list|()
block|{
if|if
condition|(
name|isEmpty
argument_list|()
condition|)
block|{
return|return
literal|null
return|;
block|}
name|Sequence
name|rc
init|=
name|getTail
argument_list|()
decl_stmt|;
name|rc
operator|.
name|unlink
argument_list|()
expr_stmt|;
return|return
name|rc
return|;
block|}
comment|/**      * Removes and returns the first sequence that is count range large.      *      * @return a sequence that is count range large, or null if no sequence is that large in the list.      */
specifier|public
name|Sequence
name|removeFirstSequence
parameter_list|(
name|long
name|count
parameter_list|)
block|{
if|if
condition|(
name|isEmpty
argument_list|()
condition|)
block|{
return|return
literal|null
return|;
block|}
name|Sequence
name|sequence
init|=
name|getHead
argument_list|()
decl_stmt|;
while|while
condition|(
name|sequence
operator|!=
literal|null
condition|)
block|{
if|if
condition|(
name|sequence
operator|.
name|range
argument_list|()
operator|==
name|count
condition|)
block|{
name|sequence
operator|.
name|unlink
argument_list|()
expr_stmt|;
return|return
name|sequence
return|;
block|}
if|if
condition|(
name|sequence
operator|.
name|range
argument_list|()
operator|>
name|count
condition|)
block|{
name|Sequence
name|rc
init|=
operator|new
name|Sequence
argument_list|(
name|sequence
operator|.
name|first
argument_list|,
name|sequence
operator|.
name|first
operator|+
name|count
operator|-
literal|1
argument_list|)
decl_stmt|;
name|sequence
operator|.
name|first
operator|+=
name|count
expr_stmt|;
return|return
name|rc
return|;
block|}
name|sequence
operator|=
name|sequence
operator|.
name|getNext
argument_list|()
expr_stmt|;
block|}
return|return
literal|null
return|;
block|}
comment|/**      * @return all the id Sequences that are missing from this set that are not      *         in between the range provided.      */
specifier|public
name|List
argument_list|<
name|Sequence
argument_list|>
name|getMissing
parameter_list|(
name|long
name|first
parameter_list|,
name|long
name|last
parameter_list|)
block|{
name|ArrayList
argument_list|<
name|Sequence
argument_list|>
name|rc
init|=
operator|new
name|ArrayList
argument_list|<
name|Sequence
argument_list|>
argument_list|()
decl_stmt|;
if|if
condition|(
name|first
operator|>
name|last
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"First cannot be more than last"
argument_list|)
throw|;
block|}
if|if
condition|(
name|isEmpty
argument_list|()
condition|)
block|{
comment|// We are missing all the messages.
name|rc
operator|.
name|add
argument_list|(
operator|new
name|Sequence
argument_list|(
name|first
argument_list|,
name|last
argument_list|)
argument_list|)
expr_stmt|;
return|return
name|rc
return|;
block|}
name|Sequence
name|sequence
init|=
name|getHead
argument_list|()
decl_stmt|;
while|while
condition|(
name|sequence
operator|!=
literal|null
operator|&&
name|first
operator|<=
name|last
condition|)
block|{
if|if
condition|(
name|sequence
operator|.
name|contains
argument_list|(
name|first
argument_list|)
condition|)
block|{
name|first
operator|=
name|sequence
operator|.
name|last
operator|+
literal|1
expr_stmt|;
block|}
else|else
block|{
if|if
condition|(
name|first
operator|<
name|sequence
operator|.
name|first
condition|)
block|{
if|if
condition|(
name|last
operator|<
name|sequence
operator|.
name|first
condition|)
block|{
name|rc
operator|.
name|add
argument_list|(
operator|new
name|Sequence
argument_list|(
name|first
argument_list|,
name|last
argument_list|)
argument_list|)
expr_stmt|;
return|return
name|rc
return|;
block|}
else|else
block|{
name|rc
operator|.
name|add
argument_list|(
operator|new
name|Sequence
argument_list|(
name|first
argument_list|,
name|sequence
operator|.
name|first
operator|-
literal|1
argument_list|)
argument_list|)
expr_stmt|;
name|first
operator|=
name|sequence
operator|.
name|last
operator|+
literal|1
expr_stmt|;
block|}
block|}
block|}
name|sequence
operator|=
name|sequence
operator|.
name|getNext
argument_list|()
expr_stmt|;
block|}
if|if
condition|(
name|first
operator|<=
name|last
condition|)
block|{
name|rc
operator|.
name|add
argument_list|(
operator|new
name|Sequence
argument_list|(
name|first
argument_list|,
name|last
argument_list|)
argument_list|)
expr_stmt|;
block|}
return|return
name|rc
return|;
block|}
comment|/**      * @return all the Sequence that are in this list      */
specifier|public
name|List
argument_list|<
name|Sequence
argument_list|>
name|getReceived
parameter_list|()
block|{
name|ArrayList
argument_list|<
name|Sequence
argument_list|>
name|rc
init|=
operator|new
name|ArrayList
argument_list|<
name|Sequence
argument_list|>
argument_list|(
name|size
argument_list|()
argument_list|)
decl_stmt|;
name|Sequence
name|sequence
init|=
name|getHead
argument_list|()
decl_stmt|;
while|while
condition|(
name|sequence
operator|!=
literal|null
condition|)
block|{
name|rc
operator|.
name|add
argument_list|(
operator|new
name|Sequence
argument_list|(
name|sequence
operator|.
name|first
argument_list|,
name|sequence
operator|.
name|last
argument_list|)
argument_list|)
expr_stmt|;
name|sequence
operator|=
name|sequence
operator|.
name|getNext
argument_list|()
expr_stmt|;
block|}
return|return
name|rc
return|;
block|}
comment|/**      * Returns true if the value given is contained within one of the      * sequences held in this set.      *      * @param value      *      The value to search for in the set.      *      * @return true if the value is contained in the set.      */
specifier|public
name|boolean
name|contains
parameter_list|(
name|long
name|value
parameter_list|)
block|{
if|if
condition|(
name|isEmpty
argument_list|()
condition|)
block|{
return|return
literal|false
return|;
block|}
name|Sequence
name|sequence
init|=
name|getHead
argument_list|()
decl_stmt|;
while|while
condition|(
name|sequence
operator|!=
literal|null
condition|)
block|{
if|if
condition|(
name|sequence
operator|.
name|contains
argument_list|(
name|value
argument_list|)
condition|)
block|{
return|return
literal|true
return|;
block|}
name|sequence
operator|=
name|sequence
operator|.
name|getNext
argument_list|()
expr_stmt|;
block|}
return|return
literal|false
return|;
block|}
specifier|public
name|boolean
name|contains
parameter_list|(
name|int
name|first
parameter_list|,
name|int
name|last
parameter_list|)
block|{
if|if
condition|(
name|isEmpty
argument_list|()
condition|)
block|{
return|return
literal|false
return|;
block|}
name|Sequence
name|sequence
init|=
name|getHead
argument_list|()
decl_stmt|;
while|while
condition|(
name|sequence
operator|!=
literal|null
condition|)
block|{
if|if
condition|(
name|sequence
operator|.
name|first
operator|<=
name|first
operator|&&
name|first
operator|<=
name|sequence
operator|.
name|last
condition|)
block|{
return|return
name|last
operator|<=
name|sequence
operator|.
name|last
return|;
block|}
name|sequence
operator|=
name|sequence
operator|.
name|getNext
argument_list|()
expr_stmt|;
block|}
return|return
literal|false
return|;
block|}
specifier|public
name|Sequence
name|get
parameter_list|(
name|int
name|value
parameter_list|)
block|{
if|if
condition|(
operator|!
name|isEmpty
argument_list|()
condition|)
block|{
name|Sequence
name|sequence
init|=
name|getHead
argument_list|()
decl_stmt|;
while|while
condition|(
name|sequence
operator|!=
literal|null
condition|)
block|{
if|if
condition|(
name|sequence
operator|.
name|contains
argument_list|(
name|value
argument_list|)
condition|)
block|{
return|return
name|sequence
return|;
block|}
name|sequence
operator|=
name|sequence
operator|.
name|getNext
argument_list|()
expr_stmt|;
block|}
block|}
return|return
literal|null
return|;
block|}
comment|/**      * Computes the size of this Sequence by summing the values of all      * the contained sequences.      *      * @return the total number of values contained in this set if it      *         were to be iterated over like an array.      */
specifier|public
name|long
name|rangeSize
parameter_list|()
block|{
name|long
name|result
init|=
literal|0
decl_stmt|;
name|Sequence
name|sequence
init|=
name|getHead
argument_list|()
decl_stmt|;
while|while
condition|(
name|sequence
operator|!=
literal|null
condition|)
block|{
name|result
operator|+=
name|sequence
operator|.
name|range
argument_list|()
expr_stmt|;
name|sequence
operator|=
name|sequence
operator|.
name|getNext
argument_list|()
expr_stmt|;
block|}
return|return
name|result
return|;
block|}
specifier|public
name|Iterator
argument_list|<
name|Long
argument_list|>
name|iterator
parameter_list|()
block|{
return|return
operator|new
name|SequenceIterator
argument_list|()
return|;
block|}
specifier|private
class|class
name|SequenceIterator
implements|implements
name|Iterator
argument_list|<
name|Long
argument_list|>
block|{
specifier|private
name|Sequence
name|currentEntry
decl_stmt|;
specifier|private
name|long
name|lastReturned
init|=
operator|-
literal|1
decl_stmt|;
specifier|public
name|SequenceIterator
parameter_list|()
block|{
name|currentEntry
operator|=
name|getHead
argument_list|()
expr_stmt|;
if|if
condition|(
name|currentEntry
operator|!=
literal|null
condition|)
block|{
name|lastReturned
operator|=
name|currentEntry
operator|.
name|first
operator|-
literal|1
expr_stmt|;
block|}
block|}
specifier|public
name|boolean
name|hasNext
parameter_list|()
block|{
return|return
name|currentEntry
operator|!=
literal|null
return|;
block|}
specifier|public
name|Long
name|next
parameter_list|()
block|{
if|if
condition|(
name|currentEntry
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|NoSuchElementException
argument_list|()
throw|;
block|}
if|if
condition|(
name|lastReturned
operator|<
name|currentEntry
operator|.
name|first
condition|)
block|{
name|lastReturned
operator|=
name|currentEntry
operator|.
name|first
expr_stmt|;
if|if
condition|(
name|currentEntry
operator|.
name|range
argument_list|()
operator|==
literal|1
condition|)
block|{
name|currentEntry
operator|=
name|currentEntry
operator|.
name|getNext
argument_list|()
expr_stmt|;
block|}
block|}
else|else
block|{
name|lastReturned
operator|++
expr_stmt|;
if|if
condition|(
name|lastReturned
operator|==
name|currentEntry
operator|.
name|last
condition|)
block|{
name|currentEntry
operator|=
name|currentEntry
operator|.
name|getNext
argument_list|()
expr_stmt|;
block|}
block|}
return|return
name|lastReturned
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
block|}
end_class

end_unit

