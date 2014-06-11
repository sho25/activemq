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
name|util
package|;
end_package

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|Serializable
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

begin_comment
comment|/**  * Holder for many bitArrays - used for message audit  *  *  */
end_comment

begin_class
specifier|public
class|class
name|BitArrayBin
implements|implements
name|Serializable
block|{
specifier|private
specifier|static
specifier|final
name|long
name|serialVersionUID
init|=
literal|1L
decl_stmt|;
specifier|private
specifier|final
name|LinkedList
argument_list|<
name|BitArray
argument_list|>
name|list
decl_stmt|;
specifier|private
name|int
name|maxNumberOfArrays
decl_stmt|;
specifier|private
name|long
name|firstIndex
init|=
operator|-
literal|1
decl_stmt|;
specifier|private
name|long
name|lastInOrderBit
init|=
operator|-
literal|1
decl_stmt|;
comment|/**      * Create a BitArrayBin to a certain window size (number of messages to      * keep)      *      * @param windowSize      */
specifier|public
name|BitArrayBin
parameter_list|(
name|int
name|windowSize
parameter_list|)
block|{
name|maxNumberOfArrays
operator|=
operator|(
operator|(
name|windowSize
operator|+
literal|1
operator|)
operator|/
name|BitArray
operator|.
name|LONG_SIZE
operator|)
operator|+
literal|1
expr_stmt|;
name|maxNumberOfArrays
operator|=
name|Math
operator|.
name|max
argument_list|(
name|maxNumberOfArrays
argument_list|,
literal|1
argument_list|)
expr_stmt|;
name|list
operator|=
operator|new
name|LinkedList
argument_list|<
name|BitArray
argument_list|>
argument_list|()
expr_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|maxNumberOfArrays
condition|;
name|i
operator|++
control|)
block|{
name|list
operator|.
name|add
argument_list|(
literal|null
argument_list|)
expr_stmt|;
block|}
block|}
comment|/**      * Set a bit      *      * @param index      * @param value      * @return true if set      */
specifier|public
name|boolean
name|setBit
parameter_list|(
name|long
name|index
parameter_list|,
name|boolean
name|value
parameter_list|)
block|{
name|boolean
name|answer
init|=
literal|false
decl_stmt|;
name|BitArray
name|ba
init|=
name|getBitArray
argument_list|(
name|index
argument_list|)
decl_stmt|;
if|if
condition|(
name|ba
operator|!=
literal|null
condition|)
block|{
name|int
name|offset
init|=
name|getOffset
argument_list|(
name|index
argument_list|)
decl_stmt|;
if|if
condition|(
name|offset
operator|>=
literal|0
condition|)
block|{
name|answer
operator|=
name|ba
operator|.
name|set
argument_list|(
name|offset
argument_list|,
name|value
argument_list|)
expr_stmt|;
block|}
block|}
return|return
name|answer
return|;
block|}
comment|/**      * Test if in order      * @param index      * @return true if next message is in order      */
specifier|public
name|boolean
name|isInOrder
parameter_list|(
name|long
name|index
parameter_list|)
block|{
name|boolean
name|result
init|=
literal|false
decl_stmt|;
if|if
condition|(
name|lastInOrderBit
operator|==
operator|-
literal|1
condition|)
block|{
name|result
operator|=
literal|true
expr_stmt|;
block|}
else|else
block|{
name|result
operator|=
name|lastInOrderBit
operator|+
literal|1
operator|==
name|index
expr_stmt|;
block|}
name|lastInOrderBit
operator|=
name|index
expr_stmt|;
return|return
name|result
return|;
block|}
comment|/**      * Get the boolean value at the index      *      * @param index      * @return true/false      */
specifier|public
name|boolean
name|getBit
parameter_list|(
name|long
name|index
parameter_list|)
block|{
name|boolean
name|answer
init|=
name|index
operator|>=
name|firstIndex
decl_stmt|;
name|BitArray
name|ba
init|=
name|getBitArray
argument_list|(
name|index
argument_list|)
decl_stmt|;
if|if
condition|(
name|ba
operator|!=
literal|null
condition|)
block|{
name|int
name|offset
init|=
name|getOffset
argument_list|(
name|index
argument_list|)
decl_stmt|;
if|if
condition|(
name|offset
operator|>=
literal|0
condition|)
block|{
name|answer
operator|=
name|ba
operator|.
name|get
argument_list|(
name|offset
argument_list|)
expr_stmt|;
return|return
name|answer
return|;
block|}
block|}
else|else
block|{
comment|// gone passed range for previous bins so assume set
name|answer
operator|=
literal|true
expr_stmt|;
block|}
return|return
name|answer
return|;
block|}
comment|/**      * Get the BitArray for the index      *      * @param index      * @return BitArray      */
specifier|private
name|BitArray
name|getBitArray
parameter_list|(
name|long
name|index
parameter_list|)
block|{
name|int
name|bin
init|=
name|getBin
argument_list|(
name|index
argument_list|)
decl_stmt|;
name|BitArray
name|answer
init|=
literal|null
decl_stmt|;
if|if
condition|(
name|bin
operator|>=
literal|0
condition|)
block|{
if|if
condition|(
name|bin
operator|>=
name|maxNumberOfArrays
condition|)
block|{
name|int
name|overShoot
init|=
name|bin
operator|-
name|maxNumberOfArrays
operator|+
literal|1
decl_stmt|;
while|while
condition|(
name|overShoot
operator|>
literal|0
condition|)
block|{
name|list
operator|.
name|removeFirst
argument_list|()
expr_stmt|;
name|firstIndex
operator|+=
name|BitArray
operator|.
name|LONG_SIZE
expr_stmt|;
name|list
operator|.
name|add
argument_list|(
operator|new
name|BitArray
argument_list|()
argument_list|)
expr_stmt|;
name|overShoot
operator|--
expr_stmt|;
block|}
name|bin
operator|=
name|maxNumberOfArrays
operator|-
literal|1
expr_stmt|;
block|}
name|answer
operator|=
name|list
operator|.
name|get
argument_list|(
name|bin
argument_list|)
expr_stmt|;
if|if
condition|(
name|answer
operator|==
literal|null
condition|)
block|{
name|answer
operator|=
operator|new
name|BitArray
argument_list|()
expr_stmt|;
name|list
operator|.
name|set
argument_list|(
name|bin
argument_list|,
name|answer
argument_list|)
expr_stmt|;
block|}
block|}
return|return
name|answer
return|;
block|}
comment|/**      * Get the index of the bin from the total index      *      * @param index      * @return the index of the bin      */
specifier|private
name|int
name|getBin
parameter_list|(
name|long
name|index
parameter_list|)
block|{
name|int
name|answer
init|=
literal|0
decl_stmt|;
if|if
condition|(
name|firstIndex
operator|<
literal|0
condition|)
block|{
name|firstIndex
operator|=
call|(
name|int
call|)
argument_list|(
name|index
operator|-
operator|(
name|index
operator|%
name|BitArray
operator|.
name|LONG_SIZE
operator|)
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|firstIndex
operator|>=
literal|0
condition|)
block|{
name|answer
operator|=
call|(
name|int
call|)
argument_list|(
operator|(
name|index
operator|-
name|firstIndex
operator|)
operator|/
name|BitArray
operator|.
name|LONG_SIZE
argument_list|)
expr_stmt|;
block|}
return|return
name|answer
return|;
block|}
comment|/**      * Get the offset into a bin from the total index      *      * @param index      * @return the relative offset into a bin      */
specifier|private
name|int
name|getOffset
parameter_list|(
name|long
name|index
parameter_list|)
block|{
name|int
name|answer
init|=
literal|0
decl_stmt|;
if|if
condition|(
name|firstIndex
operator|>=
literal|0
condition|)
block|{
name|answer
operator|=
call|(
name|int
call|)
argument_list|(
operator|(
name|index
operator|-
name|firstIndex
operator|)
operator|-
operator|(
name|BitArray
operator|.
name|LONG_SIZE
operator|*
name|getBin
argument_list|(
name|index
argument_list|)
operator|)
argument_list|)
expr_stmt|;
block|}
return|return
name|answer
return|;
block|}
specifier|public
name|long
name|getLastSetIndex
parameter_list|()
block|{
name|long
name|result
init|=
operator|-
literal|1
decl_stmt|;
if|if
condition|(
name|firstIndex
operator|>=
literal|0
condition|)
block|{
name|result
operator|=
name|firstIndex
expr_stmt|;
name|BitArray
name|last
init|=
literal|null
decl_stmt|;
for|for
control|(
name|int
name|lastBitArrayIndex
init|=
name|maxNumberOfArrays
operator|-
literal|1
init|;
name|lastBitArrayIndex
operator|>=
literal|0
condition|;
name|lastBitArrayIndex
operator|--
control|)
block|{
name|last
operator|=
name|list
operator|.
name|get
argument_list|(
name|lastBitArrayIndex
argument_list|)
expr_stmt|;
if|if
condition|(
name|last
operator|!=
literal|null
condition|)
block|{
name|result
operator|+=
name|last
operator|.
name|length
argument_list|()
operator|-
literal|1
expr_stmt|;
name|result
operator|+=
name|lastBitArrayIndex
operator|*
name|BitArray
operator|.
name|LONG_SIZE
expr_stmt|;
break|break;
block|}
block|}
block|}
return|return
name|result
return|;
block|}
block|}
end_class

end_unit

