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

begin_comment
comment|/**  * Simple BitArray to enable setting multiple boolean values efficently Used instead of BitSet because BitSet does not  * allow for efficent serialization.  * Will store up to 64 boolean values  *   * @version $Revision: 1.1.1.1 $  */
end_comment

begin_class
specifier|public
class|class
name|BitArray
block|{
specifier|static
specifier|final
name|int
name|LONG_SIZE
init|=
literal|64
decl_stmt|;
specifier|static
specifier|final
name|int
name|INT_SIZE
init|=
literal|32
decl_stmt|;
specifier|static
specifier|final
name|int
name|SHORT_SIZE
init|=
literal|16
decl_stmt|;
specifier|static
specifier|final
name|int
name|BYTE_SIZE
init|=
literal|8
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|long
index|[]
name|BIT_VALUES
init|=
block|{
literal|0x0000000000000001L
block|,
literal|0x0000000000000002L
block|,
literal|0x0000000000000004L
block|,
literal|0x0000000000000008L
block|,
literal|0x0000000000000010L
block|,
literal|0x0000000000000020L
block|,
literal|0x0000000000000040L
block|,
literal|0x0000000000000080L
block|,
literal|0x0000000000000100L
block|,
literal|0x0000000000000200L
block|,
literal|0x0000000000000400L
block|,
literal|0x0000000000000800L
block|,
literal|0x0000000000001000L
block|,
literal|0x0000000000002000L
block|,
literal|0x0000000000004000L
block|,
literal|0x0000000000008000L
block|,
literal|0x0000000000010000L
block|,
literal|0x0000000000020000L
block|,
literal|0x0000000000040000L
block|,
literal|0x0000000000080000L
block|,
literal|0x0000000000100000L
block|,
literal|0x0000000000200000L
block|,
literal|0x0000000000400000L
block|,
literal|0x0000000000800000L
block|,
literal|0x0000000001000000L
block|,
literal|0x0000000002000000L
block|,
literal|0x0000000004000000L
block|,
literal|0x0000000008000000L
block|,
literal|0x0000000010000000L
block|,
literal|0x0000000020000000L
block|,
literal|0x0000000040000000L
block|,
literal|0x0000000080000000L
block|,
literal|0x0000000100000000L
block|,
literal|0x0000000200000000L
block|,
literal|0x0000000400000000L
block|,
literal|0x0000000800000000L
block|,
literal|0x0000001000000000L
block|,
literal|0x0000002000000000L
block|,
literal|0x0000004000000000L
block|,
literal|0x0000008000000000L
block|,
literal|0x0000010000000000L
block|,
literal|0x0000020000000000L
block|,
literal|0x0000040000000000L
block|,
literal|0x0000080000000000L
block|,
literal|0x0000100000000000L
block|,
literal|0x0000200000000000L
block|,
literal|0x0000400000000000L
block|,
literal|0x0000800000000000L
block|,
literal|0x0001000000000000L
block|,
literal|0x0002000000000000L
block|,
literal|0x0004000000000000L
block|,
literal|0x0008000000000000L
block|,
literal|0x0010000000000000L
block|,
literal|0x0020000000000000L
block|,
literal|0x0040000000000000L
block|,
literal|0x0080000000000000L
block|,
literal|0x0100000000000000L
block|,
literal|0x0200000000000000L
block|,
literal|0x0400000000000000L
block|,
literal|0x0800000000000000L
block|,
literal|0x1000000000000000L
block|,
literal|0x2000000000000000L
block|,
literal|0x4000000000000000L
block|,
literal|0x8000000000000000L
block|}
decl_stmt|;
specifier|private
name|long
name|bits
decl_stmt|;
specifier|private
name|int
name|length
decl_stmt|;
comment|/**      * @return the length of bits set      */
specifier|public
name|int
name|length
parameter_list|()
block|{
return|return
name|length
return|;
block|}
comment|/**      * @return the long containing the bits      */
specifier|public
name|long
name|getBits
parameter_list|()
block|{
return|return
name|bits
return|;
block|}
comment|/**      * set the boolean value at the index      *      * @param index      * @param flag      * @return the old value held at this index      */
specifier|public
name|boolean
name|set
parameter_list|(
name|int
name|index
parameter_list|,
name|boolean
name|flag
parameter_list|)
block|{
name|length
operator|=
name|Math
operator|.
name|max
argument_list|(
name|length
argument_list|,
name|index
operator|+
literal|1
argument_list|)
expr_stmt|;
name|boolean
name|oldValue
init|=
operator|(
name|bits
operator|&
name|BIT_VALUES
index|[
name|index
index|]
operator|)
operator|!=
literal|0
decl_stmt|;
if|if
condition|(
name|flag
condition|)
block|{
name|bits
operator||=
name|BIT_VALUES
index|[
name|index
index|]
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|oldValue
condition|)
block|{
name|bits
operator|&=
operator|~
operator|(
name|BIT_VALUES
index|[
name|index
index|]
operator|)
expr_stmt|;
block|}
return|return
name|oldValue
return|;
block|}
comment|/**      * @param index      * @return the boolean value at this index      */
specifier|public
name|boolean
name|get
parameter_list|(
name|int
name|index
parameter_list|)
block|{
return|return
operator|(
name|bits
operator|&
name|BIT_VALUES
index|[
name|index
index|]
operator|)
operator|!=
literal|0
return|;
block|}
comment|/**      * reset all the bit values to false      */
specifier|public
name|void
name|reset
parameter_list|()
block|{
name|bits
operator|=
literal|0
expr_stmt|;
block|}
comment|/**      * reset all the bits to the value supplied      * @param bits      */
specifier|public
name|void
name|reset
parameter_list|(
name|long
name|bits
parameter_list|)
block|{
name|this
operator|.
name|bits
operator|=
name|bits
expr_stmt|;
block|}
comment|/**      * write the bits to an output stream      *      * @param dataOut      * @throws IOException      */
specifier|public
name|void
name|writeToStream
parameter_list|(
name|DataOutput
name|dataOut
parameter_list|)
throws|throws
name|IOException
block|{
name|dataOut
operator|.
name|writeByte
argument_list|(
name|length
argument_list|)
expr_stmt|;
if|if
condition|(
name|length
operator|<=
name|BYTE_SIZE
condition|)
block|{
name|dataOut
operator|.
name|writeByte
argument_list|(
operator|(
name|int
operator|)
name|bits
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|length
operator|<=
name|SHORT_SIZE
condition|)
block|{
name|dataOut
operator|.
name|writeShort
argument_list|(
operator|(
name|short
operator|)
name|bits
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|length
operator|<=
name|INT_SIZE
condition|)
block|{
name|dataOut
operator|.
name|writeInt
argument_list|(
operator|(
name|int
operator|)
name|bits
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|dataOut
operator|.
name|writeLong
argument_list|(
name|bits
argument_list|)
expr_stmt|;
block|}
block|}
comment|/**      * read the bits from an input stream      *      * @param dataIn      * @throws IOException      */
specifier|public
name|void
name|readFromStream
parameter_list|(
name|DataInput
name|dataIn
parameter_list|)
throws|throws
name|IOException
block|{
name|length
operator|=
name|dataIn
operator|.
name|readByte
argument_list|()
expr_stmt|;
if|if
condition|(
name|length
operator|<=
name|BYTE_SIZE
condition|)
block|{
name|bits
operator|=
name|dataIn
operator|.
name|readByte
argument_list|()
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|length
operator|<=
name|SHORT_SIZE
condition|)
block|{
name|bits
operator|=
name|dataIn
operator|.
name|readShort
argument_list|()
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|length
operator|<=
name|INT_SIZE
condition|)
block|{
name|bits
operator|=
name|dataIn
operator|.
name|readInt
argument_list|()
expr_stmt|;
block|}
else|else
block|{
name|bits
operator|=
name|dataIn
operator|.
name|readLong
argument_list|()
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

