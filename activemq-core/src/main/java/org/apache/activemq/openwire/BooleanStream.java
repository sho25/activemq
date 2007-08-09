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
name|openwire
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
name|nio
operator|.
name|ByteBuffer
import|;
end_import

begin_class
specifier|public
specifier|final
class|class
name|BooleanStream
block|{
name|byte
name|data
index|[]
init|=
operator|new
name|byte
index|[
literal|48
index|]
decl_stmt|;
name|short
name|arrayLimit
decl_stmt|;
name|short
name|arrayPos
decl_stmt|;
name|byte
name|bytePos
decl_stmt|;
specifier|public
name|boolean
name|readBoolean
parameter_list|()
throws|throws
name|IOException
block|{
assert|assert
name|arrayPos
operator|<=
name|arrayLimit
assert|;
name|byte
name|b
init|=
name|data
index|[
name|arrayPos
index|]
decl_stmt|;
name|boolean
name|rc
init|=
operator|(
operator|(
name|b
operator|>>
name|bytePos
operator|)
operator|&
literal|0x01
operator|)
operator|!=
literal|0
decl_stmt|;
name|bytePos
operator|++
expr_stmt|;
if|if
condition|(
name|bytePos
operator|>=
literal|8
condition|)
block|{
name|bytePos
operator|=
literal|0
expr_stmt|;
name|arrayPos
operator|++
expr_stmt|;
block|}
return|return
name|rc
return|;
block|}
specifier|public
name|void
name|writeBoolean
parameter_list|(
name|boolean
name|value
parameter_list|)
throws|throws
name|IOException
block|{
if|if
condition|(
name|bytePos
operator|==
literal|0
condition|)
block|{
name|arrayLimit
operator|++
expr_stmt|;
if|if
condition|(
name|arrayLimit
operator|>=
name|data
operator|.
name|length
condition|)
block|{
comment|// re-grow the array.
name|byte
name|d
index|[]
init|=
operator|new
name|byte
index|[
name|data
operator|.
name|length
operator|*
literal|2
index|]
decl_stmt|;
name|System
operator|.
name|arraycopy
argument_list|(
name|data
argument_list|,
literal|0
argument_list|,
name|d
argument_list|,
literal|0
argument_list|,
name|data
operator|.
name|length
argument_list|)
expr_stmt|;
name|data
operator|=
name|d
expr_stmt|;
block|}
block|}
if|if
condition|(
name|value
condition|)
block|{
name|data
index|[
name|arrayPos
index|]
operator||=
operator|(
literal|0x01
operator|<<
name|bytePos
operator|)
expr_stmt|;
block|}
name|bytePos
operator|++
expr_stmt|;
if|if
condition|(
name|bytePos
operator|>=
literal|8
condition|)
block|{
name|bytePos
operator|=
literal|0
expr_stmt|;
name|arrayPos
operator|++
expr_stmt|;
block|}
block|}
specifier|public
name|void
name|marshal
parameter_list|(
name|DataOutput
name|dataOut
parameter_list|)
throws|throws
name|IOException
block|{
if|if
condition|(
name|arrayLimit
operator|<
literal|64
condition|)
block|{
name|dataOut
operator|.
name|writeByte
argument_list|(
name|arrayLimit
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|arrayLimit
operator|<
literal|256
condition|)
block|{
comment|// max value of unsigned byte
name|dataOut
operator|.
name|writeByte
argument_list|(
literal|0xC0
argument_list|)
expr_stmt|;
name|dataOut
operator|.
name|writeByte
argument_list|(
name|arrayLimit
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|dataOut
operator|.
name|writeByte
argument_list|(
literal|0x80
argument_list|)
expr_stmt|;
name|dataOut
operator|.
name|writeShort
argument_list|(
name|arrayLimit
argument_list|)
expr_stmt|;
block|}
name|dataOut
operator|.
name|write
argument_list|(
name|data
argument_list|,
literal|0
argument_list|,
name|arrayLimit
argument_list|)
expr_stmt|;
name|clear
argument_list|()
expr_stmt|;
block|}
specifier|public
name|void
name|marshal
parameter_list|(
name|ByteBuffer
name|dataOut
parameter_list|)
block|{
if|if
condition|(
name|arrayLimit
operator|<
literal|64
condition|)
block|{
name|dataOut
operator|.
name|put
argument_list|(
operator|(
name|byte
operator|)
name|arrayLimit
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|arrayLimit
operator|<
literal|256
condition|)
block|{
comment|// max value of unsigned byte
name|dataOut
operator|.
name|put
argument_list|(
operator|(
name|byte
operator|)
literal|0xC0
argument_list|)
expr_stmt|;
name|dataOut
operator|.
name|put
argument_list|(
operator|(
name|byte
operator|)
name|arrayLimit
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|dataOut
operator|.
name|put
argument_list|(
operator|(
name|byte
operator|)
literal|0x80
argument_list|)
expr_stmt|;
name|dataOut
operator|.
name|putShort
argument_list|(
name|arrayLimit
argument_list|)
expr_stmt|;
block|}
name|dataOut
operator|.
name|put
argument_list|(
name|data
argument_list|,
literal|0
argument_list|,
name|arrayLimit
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|unmarshal
parameter_list|(
name|DataInput
name|dataIn
parameter_list|)
throws|throws
name|IOException
block|{
name|arrayLimit
operator|=
call|(
name|short
call|)
argument_list|(
name|dataIn
operator|.
name|readByte
argument_list|()
operator|&
literal|0xFF
argument_list|)
expr_stmt|;
if|if
condition|(
name|arrayLimit
operator|==
literal|0xC0
condition|)
block|{
name|arrayLimit
operator|=
call|(
name|short
call|)
argument_list|(
name|dataIn
operator|.
name|readByte
argument_list|()
operator|&
literal|0xFF
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|arrayLimit
operator|==
literal|0x80
condition|)
block|{
name|arrayLimit
operator|=
name|dataIn
operator|.
name|readShort
argument_list|()
expr_stmt|;
block|}
if|if
condition|(
name|data
operator|.
name|length
operator|<
name|arrayLimit
condition|)
block|{
name|data
operator|=
operator|new
name|byte
index|[
name|arrayLimit
index|]
expr_stmt|;
block|}
name|dataIn
operator|.
name|readFully
argument_list|(
name|data
argument_list|,
literal|0
argument_list|,
name|arrayLimit
argument_list|)
expr_stmt|;
name|clear
argument_list|()
expr_stmt|;
block|}
specifier|public
name|void
name|clear
parameter_list|()
block|{
name|arrayPos
operator|=
literal|0
expr_stmt|;
name|bytePos
operator|=
literal|0
expr_stmt|;
block|}
specifier|public
name|int
name|marshalledSize
parameter_list|()
block|{
if|if
condition|(
name|arrayLimit
operator|<
literal|64
condition|)
block|{
return|return
literal|1
operator|+
name|arrayLimit
return|;
block|}
elseif|else
if|if
condition|(
name|arrayLimit
operator|<
literal|256
condition|)
block|{
return|return
literal|2
operator|+
name|arrayLimit
return|;
block|}
else|else
block|{
return|return
literal|3
operator|+
name|arrayLimit
return|;
block|}
block|}
block|}
end_class

end_unit

