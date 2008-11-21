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
name|IOException
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|InputStream
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|UTFDataFormatException
import|;
end_import

begin_comment
comment|/**  * Optimized ByteArrayInputStream that can be used more than once  *   * @version $Revision$  */
end_comment

begin_class
specifier|public
specifier|final
class|class
name|DataByteArrayInputStream
extends|extends
name|InputStream
implements|implements
name|DataInput
block|{
specifier|private
name|byte
index|[]
name|buf
decl_stmt|;
specifier|private
name|int
name|pos
decl_stmt|;
specifier|private
name|int
name|offset
decl_stmt|;
specifier|private
name|int
name|length
decl_stmt|;
comment|/**      * Creates a<code>StoreByteArrayInputStream</code>.      *       * @param buf the input buffer.      */
specifier|public
name|DataByteArrayInputStream
parameter_list|(
name|byte
name|buf
index|[]
parameter_list|)
block|{
name|this
operator|.
name|buf
operator|=
name|buf
expr_stmt|;
name|this
operator|.
name|pos
operator|=
literal|0
expr_stmt|;
name|this
operator|.
name|offset
operator|=
literal|0
expr_stmt|;
name|this
operator|.
name|length
operator|=
name|buf
operator|.
name|length
expr_stmt|;
block|}
comment|/**      * Creates a<code>StoreByteArrayInputStream</code>.      *       * @param sequence the input buffer.      */
specifier|public
name|DataByteArrayInputStream
parameter_list|(
name|ByteSequence
name|sequence
parameter_list|)
block|{
name|this
operator|.
name|buf
operator|=
name|sequence
operator|.
name|getData
argument_list|()
expr_stmt|;
name|this
operator|.
name|offset
operator|=
name|sequence
operator|.
name|getOffset
argument_list|()
expr_stmt|;
name|this
operator|.
name|pos
operator|=
name|this
operator|.
name|offset
expr_stmt|;
name|this
operator|.
name|length
operator|=
name|sequence
operator|.
name|length
expr_stmt|;
block|}
comment|/**      * Creates<code>WireByteArrayInputStream</code> with a minmalist byte      * array      */
specifier|public
name|DataByteArrayInputStream
parameter_list|()
block|{
name|this
argument_list|(
operator|new
name|byte
index|[
literal|0
index|]
argument_list|)
expr_stmt|;
block|}
comment|/**      * @return the size      */
specifier|public
name|int
name|size
parameter_list|()
block|{
return|return
name|pos
operator|-
name|offset
return|;
block|}
comment|/**      * @return the underlying data array      */
specifier|public
name|byte
index|[]
name|getRawData
parameter_list|()
block|{
return|return
name|buf
return|;
block|}
comment|/**      * reset the<code>StoreByteArrayInputStream</code> to use an new byte      * array      *       * @param newBuff      */
specifier|public
name|void
name|restart
parameter_list|(
name|byte
index|[]
name|newBuff
parameter_list|)
block|{
name|buf
operator|=
name|newBuff
expr_stmt|;
name|pos
operator|=
literal|0
expr_stmt|;
name|length
operator|=
name|newBuff
operator|.
name|length
expr_stmt|;
block|}
specifier|public
name|void
name|restart
parameter_list|()
block|{
name|pos
operator|=
literal|0
expr_stmt|;
name|length
operator|=
name|buf
operator|.
name|length
expr_stmt|;
block|}
comment|/**      * reset the<code>StoreByteArrayInputStream</code> to use an new      * ByteSequence      *       * @param sequence      */
specifier|public
name|void
name|restart
parameter_list|(
name|ByteSequence
name|sequence
parameter_list|)
block|{
name|this
operator|.
name|buf
operator|=
name|sequence
operator|.
name|getData
argument_list|()
expr_stmt|;
name|this
operator|.
name|pos
operator|=
name|sequence
operator|.
name|getOffset
argument_list|()
expr_stmt|;
name|this
operator|.
name|length
operator|=
name|sequence
operator|.
name|getLength
argument_list|()
expr_stmt|;
block|}
comment|/**      * re-start the input stream - reusing the current buffer      *       * @param size      */
specifier|public
name|void
name|restart
parameter_list|(
name|int
name|size
parameter_list|)
block|{
if|if
condition|(
name|buf
operator|==
literal|null
operator|||
name|buf
operator|.
name|length
operator|<
name|size
condition|)
block|{
name|buf
operator|=
operator|new
name|byte
index|[
name|size
index|]
expr_stmt|;
block|}
name|restart
argument_list|(
name|buf
argument_list|)
expr_stmt|;
name|this
operator|.
name|length
operator|=
name|size
expr_stmt|;
block|}
comment|/**      * Reads the next byte of data from this input stream. The value byte is      * returned as an<code>int</code> in the range<code>0</code> to      *<code>255</code>. If no byte is available because the end of the      * stream has been reached, the value<code>-1</code> is returned.      *<p>      * This<code>read</code> method cannot block.      *       * @return the next byte of data, or<code>-1</code> if the end of the      *         stream has been reached.      */
specifier|public
name|int
name|read
parameter_list|()
block|{
return|return
operator|(
name|pos
operator|<
name|length
operator|)
condition|?
operator|(
name|buf
index|[
name|pos
operator|++
index|]
operator|&
literal|0xff
operator|)
else|:
operator|-
literal|1
return|;
block|}
comment|/**      * Reads up to<code>len</code> bytes of data into an array of bytes from      * this input stream.      *       * @param b the buffer into which the data is read.      * @param off the start offset of the data.      * @param len the maximum number of bytes read.      * @return the total number of bytes read into the buffer, or      *<code>-1</code> if there is no more data because the end of the      *         stream has been reached.      */
specifier|public
name|int
name|read
parameter_list|(
name|byte
name|b
index|[]
parameter_list|,
name|int
name|off
parameter_list|,
name|int
name|len
parameter_list|)
block|{
if|if
condition|(
name|b
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|NullPointerException
argument_list|()
throw|;
block|}
if|if
condition|(
name|pos
operator|>=
name|length
condition|)
block|{
return|return
operator|-
literal|1
return|;
block|}
if|if
condition|(
name|pos
operator|+
name|len
operator|>
name|length
condition|)
block|{
name|len
operator|=
name|length
operator|-
name|pos
expr_stmt|;
block|}
if|if
condition|(
name|len
operator|<=
literal|0
condition|)
block|{
return|return
literal|0
return|;
block|}
name|System
operator|.
name|arraycopy
argument_list|(
name|buf
argument_list|,
name|pos
argument_list|,
name|b
argument_list|,
name|off
argument_list|,
name|len
argument_list|)
expr_stmt|;
name|pos
operator|+=
name|len
expr_stmt|;
return|return
name|len
return|;
block|}
comment|/**      * @return the number of bytes that can be read from the input stream      *         without blocking.      */
specifier|public
name|int
name|available
parameter_list|()
block|{
return|return
name|length
operator|-
name|pos
return|;
block|}
specifier|public
name|void
name|readFully
parameter_list|(
name|byte
index|[]
name|b
parameter_list|)
block|{
name|read
argument_list|(
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
name|void
name|readFully
parameter_list|(
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
block|{
name|read
argument_list|(
name|b
argument_list|,
name|off
argument_list|,
name|len
argument_list|)
expr_stmt|;
block|}
specifier|public
name|int
name|skipBytes
parameter_list|(
name|int
name|n
parameter_list|)
block|{
if|if
condition|(
name|pos
operator|+
name|n
operator|>
name|length
condition|)
block|{
name|n
operator|=
name|length
operator|-
name|pos
expr_stmt|;
block|}
if|if
condition|(
name|n
operator|<
literal|0
condition|)
block|{
return|return
literal|0
return|;
block|}
name|pos
operator|+=
name|n
expr_stmt|;
return|return
name|n
return|;
block|}
specifier|public
name|boolean
name|readBoolean
parameter_list|()
block|{
return|return
name|read
argument_list|()
operator|!=
literal|0
return|;
block|}
specifier|public
name|byte
name|readByte
parameter_list|()
block|{
return|return
operator|(
name|byte
operator|)
name|read
argument_list|()
return|;
block|}
specifier|public
name|int
name|readUnsignedByte
parameter_list|()
block|{
return|return
name|read
argument_list|()
return|;
block|}
specifier|public
name|short
name|readShort
parameter_list|()
block|{
name|int
name|ch1
init|=
name|read
argument_list|()
decl_stmt|;
name|int
name|ch2
init|=
name|read
argument_list|()
decl_stmt|;
return|return
call|(
name|short
call|)
argument_list|(
operator|(
name|ch1
operator|<<
literal|8
operator|)
operator|+
operator|(
name|ch2
operator|<<
literal|0
operator|)
argument_list|)
return|;
block|}
specifier|public
name|int
name|readUnsignedShort
parameter_list|()
block|{
name|int
name|ch1
init|=
name|read
argument_list|()
decl_stmt|;
name|int
name|ch2
init|=
name|read
argument_list|()
decl_stmt|;
return|return
operator|(
name|ch1
operator|<<
literal|8
operator|)
operator|+
operator|(
name|ch2
operator|<<
literal|0
operator|)
return|;
block|}
specifier|public
name|char
name|readChar
parameter_list|()
block|{
name|int
name|ch1
init|=
name|read
argument_list|()
decl_stmt|;
name|int
name|ch2
init|=
name|read
argument_list|()
decl_stmt|;
return|return
call|(
name|char
call|)
argument_list|(
operator|(
name|ch1
operator|<<
literal|8
operator|)
operator|+
operator|(
name|ch2
operator|<<
literal|0
operator|)
argument_list|)
return|;
block|}
specifier|public
name|int
name|readInt
parameter_list|()
block|{
name|int
name|ch1
init|=
name|read
argument_list|()
decl_stmt|;
name|int
name|ch2
init|=
name|read
argument_list|()
decl_stmt|;
name|int
name|ch3
init|=
name|read
argument_list|()
decl_stmt|;
name|int
name|ch4
init|=
name|read
argument_list|()
decl_stmt|;
return|return
operator|(
name|ch1
operator|<<
literal|24
operator|)
operator|+
operator|(
name|ch2
operator|<<
literal|16
operator|)
operator|+
operator|(
name|ch3
operator|<<
literal|8
operator|)
operator|+
operator|(
name|ch4
operator|<<
literal|0
operator|)
return|;
block|}
specifier|public
name|long
name|readLong
parameter_list|()
block|{
name|long
name|rc
init|=
operator|(
operator|(
name|long
operator|)
name|buf
index|[
name|pos
operator|++
index|]
operator|<<
literal|56
operator|)
operator|+
operator|(
call|(
name|long
call|)
argument_list|(
name|buf
index|[
name|pos
operator|++
index|]
operator|&
literal|255
argument_list|)
operator|<<
literal|48
operator|)
operator|+
operator|(
call|(
name|long
call|)
argument_list|(
name|buf
index|[
name|pos
operator|++
index|]
operator|&
literal|255
argument_list|)
operator|<<
literal|40
operator|)
operator|+
operator|(
call|(
name|long
call|)
argument_list|(
name|buf
index|[
name|pos
operator|++
index|]
operator|&
literal|255
argument_list|)
operator|<<
literal|32
operator|)
decl_stmt|;
return|return
name|rc
operator|+
operator|(
call|(
name|long
call|)
argument_list|(
name|buf
index|[
name|pos
operator|++
index|]
operator|&
literal|255
argument_list|)
operator|<<
literal|24
operator|)
operator|+
operator|(
operator|(
name|buf
index|[
name|pos
operator|++
index|]
operator|&
literal|255
operator|)
operator|<<
literal|16
operator|)
operator|+
operator|(
operator|(
name|buf
index|[
name|pos
operator|++
index|]
operator|&
literal|255
operator|)
operator|<<
literal|8
operator|)
operator|+
operator|(
operator|(
name|buf
index|[
name|pos
operator|++
index|]
operator|&
literal|255
operator|)
operator|<<
literal|0
operator|)
return|;
block|}
specifier|public
name|float
name|readFloat
parameter_list|()
throws|throws
name|IOException
block|{
return|return
name|Float
operator|.
name|intBitsToFloat
argument_list|(
name|readInt
argument_list|()
argument_list|)
return|;
block|}
specifier|public
name|double
name|readDouble
parameter_list|()
throws|throws
name|IOException
block|{
return|return
name|Double
operator|.
name|longBitsToDouble
argument_list|(
name|readLong
argument_list|()
argument_list|)
return|;
block|}
specifier|public
name|String
name|readLine
parameter_list|()
block|{
name|int
name|start
init|=
name|pos
decl_stmt|;
while|while
condition|(
name|pos
operator|<
name|length
condition|)
block|{
name|int
name|c
init|=
name|read
argument_list|()
decl_stmt|;
if|if
condition|(
name|c
operator|==
literal|'\n'
condition|)
block|{
break|break;
block|}
if|if
condition|(
name|c
operator|==
literal|'\r'
condition|)
block|{
name|c
operator|=
name|read
argument_list|()
expr_stmt|;
if|if
condition|(
name|c
operator|!=
literal|'\n'
operator|&&
name|c
operator|!=
operator|-
literal|1
condition|)
block|{
name|pos
operator|--
expr_stmt|;
block|}
break|break;
block|}
block|}
return|return
operator|new
name|String
argument_list|(
name|buf
argument_list|,
name|start
argument_list|,
name|pos
argument_list|)
return|;
block|}
specifier|public
name|String
name|readUTF
parameter_list|()
throws|throws
name|IOException
block|{
name|int
name|length
init|=
name|readUnsignedShort
argument_list|()
decl_stmt|;
name|char
index|[]
name|characters
init|=
operator|new
name|char
index|[
name|length
index|]
decl_stmt|;
name|int
name|c
decl_stmt|;
name|int
name|c2
decl_stmt|;
name|int
name|c3
decl_stmt|;
name|int
name|count
init|=
literal|0
decl_stmt|;
name|int
name|total
init|=
name|pos
operator|+
name|length
decl_stmt|;
while|while
condition|(
name|pos
operator|<
name|total
condition|)
block|{
name|c
operator|=
operator|(
name|int
operator|)
name|buf
index|[
name|pos
index|]
operator|&
literal|0xff
expr_stmt|;
if|if
condition|(
name|c
operator|>
literal|127
condition|)
block|{
break|break;
block|}
name|pos
operator|++
expr_stmt|;
name|characters
index|[
name|count
operator|++
index|]
operator|=
operator|(
name|char
operator|)
name|c
expr_stmt|;
block|}
while|while
condition|(
name|pos
operator|<
name|total
condition|)
block|{
name|c
operator|=
operator|(
name|int
operator|)
name|buf
index|[
name|pos
index|]
operator|&
literal|0xff
expr_stmt|;
switch|switch
condition|(
name|c
operator|>>
literal|4
condition|)
block|{
case|case
literal|0
case|:
case|case
literal|1
case|:
case|case
literal|2
case|:
case|case
literal|3
case|:
case|case
literal|4
case|:
case|case
literal|5
case|:
case|case
literal|6
case|:
case|case
literal|7
case|:
name|pos
operator|++
expr_stmt|;
name|characters
index|[
name|count
operator|++
index|]
operator|=
operator|(
name|char
operator|)
name|c
expr_stmt|;
break|break;
case|case
literal|12
case|:
case|case
literal|13
case|:
name|pos
operator|+=
literal|2
expr_stmt|;
if|if
condition|(
name|pos
operator|>
name|length
condition|)
block|{
throw|throw
operator|new
name|UTFDataFormatException
argument_list|(
literal|"bad string"
argument_list|)
throw|;
block|}
name|c2
operator|=
operator|(
name|int
operator|)
name|buf
index|[
name|pos
operator|-
literal|1
index|]
expr_stmt|;
if|if
condition|(
operator|(
name|c2
operator|&
literal|0xC0
operator|)
operator|!=
literal|0x80
condition|)
block|{
throw|throw
operator|new
name|UTFDataFormatException
argument_list|(
literal|"bad string"
argument_list|)
throw|;
block|}
name|characters
index|[
name|count
operator|++
index|]
operator|=
call|(
name|char
call|)
argument_list|(
operator|(
operator|(
name|c
operator|&
literal|0x1F
operator|)
operator|<<
literal|6
operator|)
operator||
operator|(
name|c2
operator|&
literal|0x3F
operator|)
argument_list|)
expr_stmt|;
break|break;
case|case
literal|14
case|:
name|pos
operator|+=
literal|3
expr_stmt|;
if|if
condition|(
name|pos
operator|>
name|length
condition|)
block|{
throw|throw
operator|new
name|UTFDataFormatException
argument_list|(
literal|"bad string"
argument_list|)
throw|;
block|}
name|c2
operator|=
operator|(
name|int
operator|)
name|buf
index|[
name|pos
operator|-
literal|2
index|]
expr_stmt|;
name|c3
operator|=
operator|(
name|int
operator|)
name|buf
index|[
name|pos
operator|-
literal|1
index|]
expr_stmt|;
if|if
condition|(
operator|(
operator|(
name|c2
operator|&
literal|0xC0
operator|)
operator|!=
literal|0x80
operator|)
operator|||
operator|(
operator|(
name|c3
operator|&
literal|0xC0
operator|)
operator|!=
literal|0x80
operator|)
condition|)
block|{
throw|throw
operator|new
name|UTFDataFormatException
argument_list|(
literal|"bad string"
argument_list|)
throw|;
block|}
name|characters
index|[
name|count
operator|++
index|]
operator|=
call|(
name|char
call|)
argument_list|(
operator|(
operator|(
name|c
operator|&
literal|0x0F
operator|)
operator|<<
literal|12
operator|)
operator||
operator|(
operator|(
name|c2
operator|&
literal|0x3F
operator|)
operator|<<
literal|6
operator|)
operator||
operator|(
operator|(
name|c3
operator|&
literal|0x3F
operator|)
operator|<<
literal|0
operator|)
argument_list|)
expr_stmt|;
break|break;
default|default:
throw|throw
operator|new
name|UTFDataFormatException
argument_list|(
literal|"bad string"
argument_list|)
throw|;
block|}
block|}
return|return
operator|new
name|String
argument_list|(
name|characters
argument_list|,
literal|0
argument_list|,
name|count
argument_list|)
return|;
block|}
specifier|public
name|int
name|getPos
parameter_list|()
block|{
return|return
name|pos
return|;
block|}
specifier|public
name|void
name|setPos
parameter_list|(
name|int
name|pos
parameter_list|)
block|{
name|this
operator|.
name|pos
operator|=
name|pos
expr_stmt|;
block|}
specifier|public
name|int
name|getLength
parameter_list|()
block|{
return|return
name|length
return|;
block|}
specifier|public
name|void
name|setLength
parameter_list|(
name|int
name|length
parameter_list|)
block|{
name|this
operator|.
name|length
operator|=
name|length
expr_stmt|;
block|}
block|}
end_class

end_unit

