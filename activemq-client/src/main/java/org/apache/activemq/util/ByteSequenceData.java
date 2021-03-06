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
name|IOException
import|;
end_import

begin_comment
comment|/**  * Used to write and read primitives to and from a ByteSequence.  */
end_comment

begin_class
specifier|public
specifier|final
class|class
name|ByteSequenceData
block|{
specifier|private
name|ByteSequenceData
parameter_list|()
block|{         }
specifier|public
specifier|static
name|byte
index|[]
name|toByteArray
parameter_list|(
name|ByteSequence
name|packet
parameter_list|)
block|{
if|if
condition|(
name|packet
operator|.
name|offset
operator|==
literal|0
operator|&&
name|packet
operator|.
name|length
operator|==
name|packet
operator|.
name|data
operator|.
name|length
condition|)
block|{
return|return
name|packet
operator|.
name|data
return|;
block|}
name|byte
name|rc
index|[]
init|=
operator|new
name|byte
index|[
name|packet
operator|.
name|length
index|]
decl_stmt|;
name|System
operator|.
name|arraycopy
argument_list|(
name|packet
operator|.
name|data
argument_list|,
name|packet
operator|.
name|offset
argument_list|,
name|rc
argument_list|,
literal|0
argument_list|,
name|packet
operator|.
name|length
argument_list|)
expr_stmt|;
return|return
name|rc
return|;
block|}
specifier|private
specifier|static
name|void
name|spaceNeeded
parameter_list|(
name|ByteSequence
name|packet
parameter_list|,
name|int
name|i
parameter_list|)
block|{
assert|assert
name|packet
operator|.
name|offset
operator|+
name|i
operator|<=
name|packet
operator|.
name|length
assert|;
block|}
specifier|public
specifier|static
name|int
name|remaining
parameter_list|(
name|ByteSequence
name|packet
parameter_list|)
block|{
return|return
name|packet
operator|.
name|length
operator|-
name|packet
operator|.
name|offset
return|;
block|}
specifier|public
specifier|static
name|int
name|read
parameter_list|(
name|ByteSequence
name|packet
parameter_list|)
block|{
return|return
name|packet
operator|.
name|data
index|[
name|packet
operator|.
name|offset
operator|++
index|]
operator|&
literal|0xff
return|;
block|}
specifier|public
specifier|static
name|void
name|readFully
parameter_list|(
name|ByteSequence
name|packet
parameter_list|,
name|byte
index|[]
name|b
parameter_list|)
throws|throws
name|IOException
block|{
name|readFully
argument_list|(
name|packet
argument_list|,
name|b
argument_list|,
literal|0
argument_list|,
name|b
operator|.
name|length
argument_list|)
expr_stmt|;
block|}
specifier|public
specifier|static
name|void
name|readFully
parameter_list|(
name|ByteSequence
name|packet
parameter_list|,
name|byte
index|[]
name|b
parameter_list|,
name|int
name|off
parameter_list|,
name|int
name|len
parameter_list|)
throws|throws
name|IOException
block|{
name|spaceNeeded
argument_list|(
name|packet
argument_list|,
name|len
argument_list|)
expr_stmt|;
name|System
operator|.
name|arraycopy
argument_list|(
name|packet
operator|.
name|data
argument_list|,
name|packet
operator|.
name|offset
argument_list|,
name|b
argument_list|,
name|off
argument_list|,
name|len
argument_list|)
expr_stmt|;
name|packet
operator|.
name|offset
operator|+=
name|len
expr_stmt|;
block|}
specifier|public
specifier|static
name|int
name|skipBytes
parameter_list|(
name|ByteSequence
name|packet
parameter_list|,
name|int
name|n
parameter_list|)
throws|throws
name|IOException
block|{
name|int
name|rc
init|=
name|Math
operator|.
name|min
argument_list|(
name|n
argument_list|,
name|remaining
argument_list|(
name|packet
argument_list|)
argument_list|)
decl_stmt|;
name|packet
operator|.
name|offset
operator|+=
name|rc
expr_stmt|;
return|return
name|rc
return|;
block|}
specifier|public
specifier|static
name|boolean
name|readBoolean
parameter_list|(
name|ByteSequence
name|packet
parameter_list|)
throws|throws
name|IOException
block|{
name|spaceNeeded
argument_list|(
name|packet
argument_list|,
literal|1
argument_list|)
expr_stmt|;
return|return
name|read
argument_list|(
name|packet
argument_list|)
operator|!=
literal|0
return|;
block|}
specifier|public
specifier|static
name|byte
name|readByte
parameter_list|(
name|ByteSequence
name|packet
parameter_list|)
throws|throws
name|IOException
block|{
name|spaceNeeded
argument_list|(
name|packet
argument_list|,
literal|1
argument_list|)
expr_stmt|;
return|return
operator|(
name|byte
operator|)
name|read
argument_list|(
name|packet
argument_list|)
return|;
block|}
specifier|public
specifier|static
name|int
name|readUnsignedByte
parameter_list|(
name|ByteSequence
name|packet
parameter_list|)
throws|throws
name|IOException
block|{
name|spaceNeeded
argument_list|(
name|packet
argument_list|,
literal|1
argument_list|)
expr_stmt|;
return|return
name|read
argument_list|(
name|packet
argument_list|)
return|;
block|}
specifier|public
specifier|static
name|short
name|readShortBig
parameter_list|(
name|ByteSequence
name|packet
parameter_list|)
throws|throws
name|IOException
block|{
name|spaceNeeded
argument_list|(
name|packet
argument_list|,
literal|2
argument_list|)
expr_stmt|;
return|return
call|(
name|short
call|)
argument_list|(
operator|(
name|read
argument_list|(
name|packet
argument_list|)
operator|<<
literal|8
operator|)
operator|+
operator|(
name|read
argument_list|(
name|packet
argument_list|)
operator|<<
literal|0
operator|)
argument_list|)
return|;
block|}
specifier|public
specifier|static
name|short
name|readShortLittle
parameter_list|(
name|ByteSequence
name|packet
parameter_list|)
throws|throws
name|IOException
block|{
name|spaceNeeded
argument_list|(
name|packet
argument_list|,
literal|2
argument_list|)
expr_stmt|;
return|return
call|(
name|short
call|)
argument_list|(
operator|(
name|read
argument_list|(
name|packet
argument_list|)
operator|<<
literal|0
operator|)
operator|+
operator|(
name|read
argument_list|(
name|packet
argument_list|)
operator|<<
literal|8
operator|)
argument_list|)
return|;
block|}
specifier|public
specifier|static
name|int
name|readUnsignedShortBig
parameter_list|(
name|ByteSequence
name|packet
parameter_list|)
throws|throws
name|IOException
block|{
name|spaceNeeded
argument_list|(
name|packet
argument_list|,
literal|2
argument_list|)
expr_stmt|;
return|return
operator|(
name|read
argument_list|(
name|packet
argument_list|)
operator|<<
literal|8
operator|)
operator|+
operator|(
name|read
argument_list|(
name|packet
argument_list|)
operator|<<
literal|0
operator|)
return|;
block|}
specifier|public
specifier|static
name|int
name|readUnsignedShortLittle
parameter_list|(
name|ByteSequence
name|packet
parameter_list|)
throws|throws
name|IOException
block|{
name|spaceNeeded
argument_list|(
name|packet
argument_list|,
literal|2
argument_list|)
expr_stmt|;
return|return
operator|(
name|read
argument_list|(
name|packet
argument_list|)
operator|<<
literal|0
operator|)
operator|+
operator|(
name|read
argument_list|(
name|packet
argument_list|)
operator|<<
literal|8
operator|)
return|;
block|}
specifier|public
specifier|static
name|char
name|readCharBig
parameter_list|(
name|ByteSequence
name|packet
parameter_list|)
throws|throws
name|IOException
block|{
name|spaceNeeded
argument_list|(
name|packet
argument_list|,
literal|2
argument_list|)
expr_stmt|;
return|return
call|(
name|char
call|)
argument_list|(
operator|(
name|read
argument_list|(
name|packet
argument_list|)
operator|<<
literal|8
operator|)
operator|+
operator|(
name|read
argument_list|(
name|packet
argument_list|)
operator|<<
literal|0
operator|)
argument_list|)
return|;
block|}
specifier|public
specifier|static
name|char
name|readCharLittle
parameter_list|(
name|ByteSequence
name|packet
parameter_list|)
throws|throws
name|IOException
block|{
name|spaceNeeded
argument_list|(
name|packet
argument_list|,
literal|2
argument_list|)
expr_stmt|;
return|return
call|(
name|char
call|)
argument_list|(
operator|(
name|read
argument_list|(
name|packet
argument_list|)
operator|<<
literal|0
operator|)
operator|+
operator|(
name|read
argument_list|(
name|packet
argument_list|)
operator|<<
literal|8
operator|)
argument_list|)
return|;
block|}
specifier|public
specifier|static
name|int
name|readIntBig
parameter_list|(
name|ByteSequence
name|packet
parameter_list|)
throws|throws
name|IOException
block|{
name|spaceNeeded
argument_list|(
name|packet
argument_list|,
literal|4
argument_list|)
expr_stmt|;
return|return
operator|(
name|read
argument_list|(
name|packet
argument_list|)
operator|<<
literal|24
operator|)
operator|+
operator|(
name|read
argument_list|(
name|packet
argument_list|)
operator|<<
literal|16
operator|)
operator|+
operator|(
name|read
argument_list|(
name|packet
argument_list|)
operator|<<
literal|8
operator|)
operator|+
operator|(
name|read
argument_list|(
name|packet
argument_list|)
operator|<<
literal|0
operator|)
return|;
block|}
specifier|public
specifier|static
name|int
name|readIntLittle
parameter_list|(
name|ByteSequence
name|packet
parameter_list|)
throws|throws
name|IOException
block|{
name|spaceNeeded
argument_list|(
name|packet
argument_list|,
literal|4
argument_list|)
expr_stmt|;
return|return
operator|(
name|read
argument_list|(
name|packet
argument_list|)
operator|<<
literal|0
operator|)
operator|+
operator|(
name|read
argument_list|(
name|packet
argument_list|)
operator|<<
literal|8
operator|)
operator|+
operator|(
name|read
argument_list|(
name|packet
argument_list|)
operator|<<
literal|16
operator|)
operator|+
operator|(
name|read
argument_list|(
name|packet
argument_list|)
operator|<<
literal|24
operator|)
return|;
block|}
specifier|public
specifier|static
name|long
name|readLongBig
parameter_list|(
name|ByteSequence
name|packet
parameter_list|)
throws|throws
name|IOException
block|{
name|spaceNeeded
argument_list|(
name|packet
argument_list|,
literal|8
argument_list|)
expr_stmt|;
return|return
operator|(
operator|(
name|long
operator|)
name|read
argument_list|(
name|packet
argument_list|)
operator|<<
literal|56
operator|)
operator|+
operator|(
operator|(
name|long
operator|)
name|read
argument_list|(
name|packet
argument_list|)
operator|<<
literal|48
operator|)
operator|+
operator|(
operator|(
name|long
operator|)
name|read
argument_list|(
name|packet
argument_list|)
operator|<<
literal|40
operator|)
operator|+
operator|(
operator|(
name|long
operator|)
name|read
argument_list|(
name|packet
argument_list|)
operator|<<
literal|32
operator|)
operator|+
operator|(
operator|(
name|long
operator|)
name|read
argument_list|(
name|packet
argument_list|)
operator|<<
literal|24
operator|)
operator|+
operator|(
operator|(
name|read
argument_list|(
name|packet
argument_list|)
operator|)
operator|<<
literal|16
operator|)
operator|+
operator|(
operator|(
name|read
argument_list|(
name|packet
argument_list|)
operator|)
operator|<<
literal|8
operator|)
operator|+
operator|(
operator|(
name|read
argument_list|(
name|packet
argument_list|)
operator|)
operator|<<
literal|0
operator|)
return|;
block|}
specifier|public
specifier|static
name|long
name|readLongLittle
parameter_list|(
name|ByteSequence
name|packet
parameter_list|)
throws|throws
name|IOException
block|{
name|spaceNeeded
argument_list|(
name|packet
argument_list|,
literal|8
argument_list|)
expr_stmt|;
return|return
operator|(
name|read
argument_list|(
name|packet
argument_list|)
operator|<<
literal|0
operator|)
operator|+
operator|(
name|read
argument_list|(
name|packet
argument_list|)
operator|<<
literal|8
operator|)
operator|+
operator|(
name|read
argument_list|(
name|packet
argument_list|)
operator|<<
literal|16
operator|)
operator|+
operator|(
operator|(
name|long
operator|)
name|read
argument_list|(
name|packet
argument_list|)
operator|<<
literal|24
operator|)
operator|+
operator|(
operator|(
name|long
operator|)
name|read
argument_list|(
name|packet
argument_list|)
operator|<<
literal|32
operator|)
operator|+
operator|(
operator|(
name|long
operator|)
name|read
argument_list|(
name|packet
argument_list|)
operator|<<
literal|40
operator|)
operator|+
operator|(
operator|(
name|long
operator|)
name|read
argument_list|(
name|packet
argument_list|)
operator|<<
literal|48
operator|)
operator|+
operator|(
operator|(
name|long
operator|)
name|read
argument_list|(
name|packet
argument_list|)
operator|<<
literal|56
operator|)
return|;
block|}
specifier|public
specifier|static
name|double
name|readDoubleBig
parameter_list|(
name|ByteSequence
name|packet
parameter_list|)
throws|throws
name|IOException
block|{
return|return
name|Double
operator|.
name|longBitsToDouble
argument_list|(
name|readLongBig
argument_list|(
name|packet
argument_list|)
argument_list|)
return|;
block|}
specifier|public
specifier|static
name|double
name|readDoubleLittle
parameter_list|(
name|ByteSequence
name|packet
parameter_list|)
throws|throws
name|IOException
block|{
return|return
name|Double
operator|.
name|longBitsToDouble
argument_list|(
name|readLongLittle
argument_list|(
name|packet
argument_list|)
argument_list|)
return|;
block|}
specifier|public
specifier|static
name|float
name|readFloatBig
parameter_list|(
name|ByteSequence
name|packet
parameter_list|)
throws|throws
name|IOException
block|{
return|return
name|Float
operator|.
name|intBitsToFloat
argument_list|(
name|readIntBig
argument_list|(
name|packet
argument_list|)
argument_list|)
return|;
block|}
specifier|public
specifier|static
name|float
name|readFloatLittle
parameter_list|(
name|ByteSequence
name|packet
parameter_list|)
throws|throws
name|IOException
block|{
return|return
name|Float
operator|.
name|intBitsToFloat
argument_list|(
name|readIntLittle
argument_list|(
name|packet
argument_list|)
argument_list|)
return|;
block|}
specifier|public
specifier|static
name|void
name|write
parameter_list|(
name|ByteSequence
name|packet
parameter_list|,
name|int
name|b
parameter_list|)
throws|throws
name|IOException
block|{
name|spaceNeeded
argument_list|(
name|packet
argument_list|,
literal|1
argument_list|)
expr_stmt|;
name|packet
operator|.
name|data
index|[
name|packet
operator|.
name|offset
operator|++
index|]
operator|=
operator|(
name|byte
operator|)
name|b
expr_stmt|;
block|}
specifier|public
specifier|static
name|void
name|write
parameter_list|(
name|ByteSequence
name|packet
parameter_list|,
name|byte
index|[]
name|b
parameter_list|)
throws|throws
name|IOException
block|{
name|write
argument_list|(
name|packet
argument_list|,
name|b
argument_list|,
literal|0
argument_list|,
name|b
operator|.
name|length
argument_list|)
expr_stmt|;
block|}
specifier|public
specifier|static
name|void
name|write
parameter_list|(
name|ByteSequence
name|packet
parameter_list|,
name|byte
index|[]
name|b
parameter_list|,
name|int
name|off
parameter_list|,
name|int
name|len
parameter_list|)
throws|throws
name|IOException
block|{
name|spaceNeeded
argument_list|(
name|packet
argument_list|,
name|len
argument_list|)
expr_stmt|;
name|System
operator|.
name|arraycopy
argument_list|(
name|b
argument_list|,
name|off
argument_list|,
name|packet
operator|.
name|data
argument_list|,
name|packet
operator|.
name|offset
argument_list|,
name|len
argument_list|)
expr_stmt|;
name|packet
operator|.
name|offset
operator|+=
name|len
expr_stmt|;
block|}
specifier|public
specifier|static
name|void
name|writeBoolean
parameter_list|(
name|ByteSequence
name|packet
parameter_list|,
name|boolean
name|v
parameter_list|)
throws|throws
name|IOException
block|{
name|spaceNeeded
argument_list|(
name|packet
argument_list|,
literal|1
argument_list|)
expr_stmt|;
name|write
argument_list|(
name|packet
argument_list|,
name|v
condition|?
literal|1
else|:
literal|0
argument_list|)
expr_stmt|;
block|}
specifier|public
specifier|static
name|void
name|writeByte
parameter_list|(
name|ByteSequence
name|packet
parameter_list|,
name|int
name|v
parameter_list|)
throws|throws
name|IOException
block|{
name|spaceNeeded
argument_list|(
name|packet
argument_list|,
literal|1
argument_list|)
expr_stmt|;
name|write
argument_list|(
name|packet
argument_list|,
name|v
argument_list|)
expr_stmt|;
block|}
specifier|public
specifier|static
name|void
name|writeShortBig
parameter_list|(
name|ByteSequence
name|packet
parameter_list|,
name|int
name|v
parameter_list|)
throws|throws
name|IOException
block|{
name|spaceNeeded
argument_list|(
name|packet
argument_list|,
literal|2
argument_list|)
expr_stmt|;
name|write
argument_list|(
name|packet
argument_list|,
operator|(
name|v
operator|>>>
literal|8
operator|)
operator|&
literal|0xFF
argument_list|)
expr_stmt|;
name|write
argument_list|(
name|packet
argument_list|,
operator|(
name|v
operator|>>>
literal|0
operator|)
operator|&
literal|0xFF
argument_list|)
expr_stmt|;
block|}
specifier|public
specifier|static
name|void
name|writeShortLittle
parameter_list|(
name|ByteSequence
name|packet
parameter_list|,
name|int
name|v
parameter_list|)
throws|throws
name|IOException
block|{
name|spaceNeeded
argument_list|(
name|packet
argument_list|,
literal|2
argument_list|)
expr_stmt|;
name|write
argument_list|(
name|packet
argument_list|,
operator|(
name|v
operator|>>>
literal|0
operator|)
operator|&
literal|0xFF
argument_list|)
expr_stmt|;
name|write
argument_list|(
name|packet
argument_list|,
operator|(
name|v
operator|>>>
literal|8
operator|)
operator|&
literal|0xFF
argument_list|)
expr_stmt|;
block|}
specifier|public
specifier|static
name|void
name|writeCharBig
parameter_list|(
name|ByteSequence
name|packet
parameter_list|,
name|int
name|v
parameter_list|)
throws|throws
name|IOException
block|{
name|spaceNeeded
argument_list|(
name|packet
argument_list|,
literal|2
argument_list|)
expr_stmt|;
name|write
argument_list|(
name|packet
argument_list|,
operator|(
name|v
operator|>>>
literal|8
operator|)
operator|&
literal|0xFF
argument_list|)
expr_stmt|;
name|write
argument_list|(
name|packet
argument_list|,
operator|(
name|v
operator|>>>
literal|0
operator|)
operator|&
literal|0xFF
argument_list|)
expr_stmt|;
block|}
specifier|public
specifier|static
name|void
name|writeCharLittle
parameter_list|(
name|ByteSequence
name|packet
parameter_list|,
name|int
name|v
parameter_list|)
throws|throws
name|IOException
block|{
name|spaceNeeded
argument_list|(
name|packet
argument_list|,
literal|2
argument_list|)
expr_stmt|;
name|write
argument_list|(
name|packet
argument_list|,
operator|(
name|v
operator|>>>
literal|0
operator|)
operator|&
literal|0xFF
argument_list|)
expr_stmt|;
name|write
argument_list|(
name|packet
argument_list|,
operator|(
name|v
operator|>>>
literal|8
operator|)
operator|&
literal|0xFF
argument_list|)
expr_stmt|;
block|}
specifier|public
specifier|static
name|void
name|writeIntBig
parameter_list|(
name|ByteSequence
name|packet
parameter_list|,
name|int
name|v
parameter_list|)
throws|throws
name|IOException
block|{
name|spaceNeeded
argument_list|(
name|packet
argument_list|,
literal|4
argument_list|)
expr_stmt|;
name|write
argument_list|(
name|packet
argument_list|,
operator|(
name|v
operator|>>>
literal|24
operator|)
operator|&
literal|0xFF
argument_list|)
expr_stmt|;
name|write
argument_list|(
name|packet
argument_list|,
operator|(
name|v
operator|>>>
literal|16
operator|)
operator|&
literal|0xFF
argument_list|)
expr_stmt|;
name|write
argument_list|(
name|packet
argument_list|,
operator|(
name|v
operator|>>>
literal|8
operator|)
operator|&
literal|0xFF
argument_list|)
expr_stmt|;
name|write
argument_list|(
name|packet
argument_list|,
operator|(
name|v
operator|>>>
literal|0
operator|)
operator|&
literal|0xFF
argument_list|)
expr_stmt|;
block|}
specifier|public
specifier|static
name|void
name|writeIntLittle
parameter_list|(
name|ByteSequence
name|packet
parameter_list|,
name|int
name|v
parameter_list|)
throws|throws
name|IOException
block|{
name|spaceNeeded
argument_list|(
name|packet
argument_list|,
literal|4
argument_list|)
expr_stmt|;
name|write
argument_list|(
name|packet
argument_list|,
operator|(
name|v
operator|>>>
literal|0
operator|)
operator|&
literal|0xFF
argument_list|)
expr_stmt|;
name|write
argument_list|(
name|packet
argument_list|,
operator|(
name|v
operator|>>>
literal|8
operator|)
operator|&
literal|0xFF
argument_list|)
expr_stmt|;
name|write
argument_list|(
name|packet
argument_list|,
operator|(
name|v
operator|>>>
literal|16
operator|)
operator|&
literal|0xFF
argument_list|)
expr_stmt|;
name|write
argument_list|(
name|packet
argument_list|,
operator|(
name|v
operator|>>>
literal|24
operator|)
operator|&
literal|0xFF
argument_list|)
expr_stmt|;
block|}
specifier|public
specifier|static
name|void
name|writeLongBig
parameter_list|(
name|ByteSequence
name|packet
parameter_list|,
name|long
name|v
parameter_list|)
throws|throws
name|IOException
block|{
name|spaceNeeded
argument_list|(
name|packet
argument_list|,
literal|8
argument_list|)
expr_stmt|;
name|write
argument_list|(
name|packet
argument_list|,
call|(
name|int
call|)
argument_list|(
name|v
operator|>>>
literal|56
argument_list|)
operator|&
literal|0xFF
argument_list|)
expr_stmt|;
name|write
argument_list|(
name|packet
argument_list|,
call|(
name|int
call|)
argument_list|(
name|v
operator|>>>
literal|48
argument_list|)
operator|&
literal|0xFF
argument_list|)
expr_stmt|;
name|write
argument_list|(
name|packet
argument_list|,
call|(
name|int
call|)
argument_list|(
name|v
operator|>>>
literal|40
argument_list|)
operator|&
literal|0xFF
argument_list|)
expr_stmt|;
name|write
argument_list|(
name|packet
argument_list|,
call|(
name|int
call|)
argument_list|(
name|v
operator|>>>
literal|32
argument_list|)
operator|&
literal|0xFF
argument_list|)
expr_stmt|;
name|write
argument_list|(
name|packet
argument_list|,
call|(
name|int
call|)
argument_list|(
name|v
operator|>>>
literal|24
argument_list|)
operator|&
literal|0xFF
argument_list|)
expr_stmt|;
name|write
argument_list|(
name|packet
argument_list|,
call|(
name|int
call|)
argument_list|(
name|v
operator|>>>
literal|16
argument_list|)
operator|&
literal|0xFF
argument_list|)
expr_stmt|;
name|write
argument_list|(
name|packet
argument_list|,
call|(
name|int
call|)
argument_list|(
name|v
operator|>>>
literal|8
argument_list|)
operator|&
literal|0xFF
argument_list|)
expr_stmt|;
name|write
argument_list|(
name|packet
argument_list|,
call|(
name|int
call|)
argument_list|(
name|v
operator|>>>
literal|0
argument_list|)
operator|&
literal|0xFF
argument_list|)
expr_stmt|;
block|}
specifier|public
specifier|static
name|void
name|writeLongLittle
parameter_list|(
name|ByteSequence
name|packet
parameter_list|,
name|long
name|v
parameter_list|)
throws|throws
name|IOException
block|{
name|spaceNeeded
argument_list|(
name|packet
argument_list|,
literal|8
argument_list|)
expr_stmt|;
name|write
argument_list|(
name|packet
argument_list|,
call|(
name|int
call|)
argument_list|(
name|v
operator|>>>
literal|0
argument_list|)
operator|&
literal|0xFF
argument_list|)
expr_stmt|;
name|write
argument_list|(
name|packet
argument_list|,
call|(
name|int
call|)
argument_list|(
name|v
operator|>>>
literal|8
argument_list|)
operator|&
literal|0xFF
argument_list|)
expr_stmt|;
name|write
argument_list|(
name|packet
argument_list|,
call|(
name|int
call|)
argument_list|(
name|v
operator|>>>
literal|16
argument_list|)
operator|&
literal|0xFF
argument_list|)
expr_stmt|;
name|write
argument_list|(
name|packet
argument_list|,
call|(
name|int
call|)
argument_list|(
name|v
operator|>>>
literal|24
argument_list|)
operator|&
literal|0xFF
argument_list|)
expr_stmt|;
name|write
argument_list|(
name|packet
argument_list|,
call|(
name|int
call|)
argument_list|(
name|v
operator|>>>
literal|32
argument_list|)
operator|&
literal|0xFF
argument_list|)
expr_stmt|;
name|write
argument_list|(
name|packet
argument_list|,
call|(
name|int
call|)
argument_list|(
name|v
operator|>>>
literal|40
argument_list|)
operator|&
literal|0xFF
argument_list|)
expr_stmt|;
name|write
argument_list|(
name|packet
argument_list|,
call|(
name|int
call|)
argument_list|(
name|v
operator|>>>
literal|48
argument_list|)
operator|&
literal|0xFF
argument_list|)
expr_stmt|;
name|write
argument_list|(
name|packet
argument_list|,
call|(
name|int
call|)
argument_list|(
name|v
operator|>>>
literal|56
argument_list|)
operator|&
literal|0xFF
argument_list|)
expr_stmt|;
block|}
specifier|public
specifier|static
name|void
name|writeDoubleBig
parameter_list|(
name|ByteSequence
name|packet
parameter_list|,
name|double
name|v
parameter_list|)
throws|throws
name|IOException
block|{
name|writeLongBig
argument_list|(
name|packet
argument_list|,
name|Double
operator|.
name|doubleToLongBits
argument_list|(
name|v
argument_list|)
argument_list|)
expr_stmt|;
block|}
specifier|public
specifier|static
name|void
name|writeDoubleLittle
parameter_list|(
name|ByteSequence
name|packet
parameter_list|,
name|double
name|v
parameter_list|)
throws|throws
name|IOException
block|{
name|writeLongLittle
argument_list|(
name|packet
argument_list|,
name|Double
operator|.
name|doubleToLongBits
argument_list|(
name|v
argument_list|)
argument_list|)
expr_stmt|;
block|}
specifier|public
specifier|static
name|void
name|writeFloatBig
parameter_list|(
name|ByteSequence
name|packet
parameter_list|,
name|float
name|v
parameter_list|)
throws|throws
name|IOException
block|{
name|writeIntBig
argument_list|(
name|packet
argument_list|,
name|Float
operator|.
name|floatToIntBits
argument_list|(
name|v
argument_list|)
argument_list|)
expr_stmt|;
block|}
specifier|public
specifier|static
name|void
name|writeFloatLittle
parameter_list|(
name|ByteSequence
name|packet
parameter_list|,
name|float
name|v
parameter_list|)
throws|throws
name|IOException
block|{
name|writeIntLittle
argument_list|(
name|packet
argument_list|,
name|Float
operator|.
name|floatToIntBits
argument_list|(
name|v
argument_list|)
argument_list|)
expr_stmt|;
block|}
specifier|public
specifier|static
name|void
name|writeRawDoubleBig
parameter_list|(
name|ByteSequence
name|packet
parameter_list|,
name|double
name|v
parameter_list|)
throws|throws
name|IOException
block|{
name|writeLongBig
argument_list|(
name|packet
argument_list|,
name|Double
operator|.
name|doubleToRawLongBits
argument_list|(
name|v
argument_list|)
argument_list|)
expr_stmt|;
block|}
specifier|public
specifier|static
name|void
name|writeRawDoubleLittle
parameter_list|(
name|ByteSequence
name|packet
parameter_list|,
name|double
name|v
parameter_list|)
throws|throws
name|IOException
block|{
name|writeLongLittle
argument_list|(
name|packet
argument_list|,
name|Double
operator|.
name|doubleToRawLongBits
argument_list|(
name|v
argument_list|)
argument_list|)
expr_stmt|;
block|}
specifier|public
specifier|static
name|void
name|writeRawFloatBig
parameter_list|(
name|ByteSequence
name|packet
parameter_list|,
name|float
name|v
parameter_list|)
throws|throws
name|IOException
block|{
name|writeIntBig
argument_list|(
name|packet
argument_list|,
name|Float
operator|.
name|floatToRawIntBits
argument_list|(
name|v
argument_list|)
argument_list|)
expr_stmt|;
block|}
specifier|public
specifier|static
name|void
name|writeRawFloatLittle
parameter_list|(
name|ByteSequence
name|packet
parameter_list|,
name|float
name|v
parameter_list|)
throws|throws
name|IOException
block|{
name|writeIntLittle
argument_list|(
name|packet
argument_list|,
name|Float
operator|.
name|floatToRawIntBits
argument_list|(
name|v
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

