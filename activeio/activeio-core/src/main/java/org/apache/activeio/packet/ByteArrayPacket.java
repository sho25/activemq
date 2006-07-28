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
name|activeio
operator|.
name|packet
package|;
end_package

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
name|io
operator|.
name|OutputStream
import|;
end_import

begin_import
import|import
name|java
operator|.
name|lang
operator|.
name|reflect
operator|.
name|Constructor
import|;
end_import

begin_comment
comment|/**  * Provides a Packet implementation that is directly backed by a<code>byte[]</code>.  *   * @version $Revision$  */
end_comment

begin_class
specifier|final
specifier|public
class|class
name|ByteArrayPacket
implements|implements
name|Packet
block|{
specifier|private
specifier|final
name|byte
name|buffer
index|[]
decl_stmt|;
specifier|private
specifier|final
name|int
name|offset
decl_stmt|;
specifier|private
specifier|final
name|int
name|capacity
decl_stmt|;
specifier|private
name|int
name|position
decl_stmt|;
specifier|private
name|int
name|limit
decl_stmt|;
specifier|private
name|int
name|remaining
decl_stmt|;
specifier|public
name|ByteArrayPacket
parameter_list|(
name|byte
name|buffer
index|[]
parameter_list|)
block|{
name|this
argument_list|(
name|buffer
argument_list|,
literal|0
argument_list|,
name|buffer
operator|.
name|length
argument_list|)
expr_stmt|;
block|}
specifier|public
name|ByteArrayPacket
parameter_list|(
name|ByteSequence
name|sequence
parameter_list|)
block|{
name|this
argument_list|(
name|sequence
operator|.
name|getData
argument_list|()
argument_list|,
name|sequence
operator|.
name|getOffset
argument_list|()
argument_list|,
name|sequence
operator|.
name|getLength
argument_list|()
argument_list|)
expr_stmt|;
block|}
specifier|public
name|ByteArrayPacket
parameter_list|(
name|byte
name|buffer
index|[]
parameter_list|,
name|int
name|offset
parameter_list|,
name|int
name|capacity
parameter_list|)
block|{
name|this
operator|.
name|buffer
operator|=
name|buffer
expr_stmt|;
name|this
operator|.
name|offset
operator|=
name|offset
expr_stmt|;
name|this
operator|.
name|capacity
operator|=
name|capacity
expr_stmt|;
name|this
operator|.
name|position
operator|=
literal|0
expr_stmt|;
name|this
operator|.
name|limit
operator|=
name|capacity
expr_stmt|;
name|this
operator|.
name|remaining
operator|=
name|limit
operator|-
name|position
expr_stmt|;
block|}
specifier|public
name|int
name|position
parameter_list|()
block|{
return|return
name|position
return|;
block|}
specifier|public
name|void
name|position
parameter_list|(
name|int
name|position
parameter_list|)
block|{
name|this
operator|.
name|position
operator|=
name|position
expr_stmt|;
name|remaining
operator|=
name|limit
operator|-
name|position
expr_stmt|;
block|}
specifier|public
name|int
name|limit
parameter_list|()
block|{
return|return
name|limit
return|;
block|}
specifier|public
name|void
name|limit
parameter_list|(
name|int
name|limit
parameter_list|)
block|{
name|this
operator|.
name|limit
operator|=
name|limit
expr_stmt|;
name|remaining
operator|=
name|limit
operator|-
name|position
expr_stmt|;
block|}
specifier|public
name|void
name|flip
parameter_list|()
block|{
name|limit
operator|=
name|position
expr_stmt|;
name|position
operator|=
literal|0
expr_stmt|;
name|remaining
operator|=
name|limit
operator|-
name|position
expr_stmt|;
block|}
specifier|public
name|int
name|remaining
parameter_list|()
block|{
return|return
name|remaining
return|;
block|}
specifier|public
name|void
name|rewind
parameter_list|()
block|{
name|position
operator|=
literal|0
expr_stmt|;
name|remaining
operator|=
name|limit
operator|-
name|position
expr_stmt|;
block|}
specifier|public
name|boolean
name|hasRemaining
parameter_list|()
block|{
return|return
name|remaining
operator|>
literal|0
return|;
block|}
specifier|public
name|void
name|clear
parameter_list|()
block|{
name|position
operator|=
literal|0
expr_stmt|;
name|limit
operator|=
name|capacity
expr_stmt|;
name|remaining
operator|=
name|limit
operator|-
name|position
expr_stmt|;
block|}
specifier|public
name|int
name|capacity
parameter_list|()
block|{
return|return
name|capacity
return|;
block|}
specifier|public
name|Packet
name|slice
parameter_list|()
block|{
return|return
operator|new
name|ByteArrayPacket
argument_list|(
name|buffer
argument_list|,
name|offset
operator|+
name|position
argument_list|,
name|remaining
argument_list|)
return|;
block|}
specifier|public
name|Packet
name|duplicate
parameter_list|()
block|{
return|return
operator|new
name|ByteArrayPacket
argument_list|(
name|buffer
argument_list|,
name|offset
argument_list|,
name|capacity
argument_list|)
return|;
block|}
specifier|public
name|Object
name|duplicate
parameter_list|(
name|ClassLoader
name|cl
parameter_list|)
throws|throws
name|IOException
block|{
try|try
block|{
name|Class
name|clazz
init|=
name|cl
operator|.
name|loadClass
argument_list|(
name|ByteArrayPacket
operator|.
name|class
operator|.
name|getName
argument_list|()
argument_list|)
decl_stmt|;
name|Constructor
name|constructor
init|=
name|clazz
operator|.
name|getConstructor
argument_list|(
operator|new
name|Class
index|[]
block|{
name|byte
index|[]
operator|.
expr|class
block|,
name|int
operator|.
name|class
block|,
name|int
operator|.
name|class
block|}
argument_list|)
decl_stmt|;
return|return
name|constructor
operator|.
name|newInstance
argument_list|(
operator|new
name|Object
index|[]
block|{
name|buffer
block|,
operator|new
name|Integer
argument_list|(
name|offset
argument_list|)
block|,
operator|new
name|Integer
argument_list|(
name|capacity
argument_list|()
argument_list|)
block|}
argument_list|)
return|;
block|}
catch|catch
parameter_list|(
name|Throwable
name|e
parameter_list|)
block|{
throw|throw
operator|(
name|IOException
operator|)
operator|new
name|IOException
argument_list|(
literal|"Could not duplicate packet in a different classloader: "
operator|+
name|e
argument_list|)
operator|.
name|initCause
argument_list|(
name|e
argument_list|)
throw|;
block|}
block|}
specifier|public
name|void
name|writeTo
parameter_list|(
name|OutputStream
name|out
parameter_list|)
throws|throws
name|IOException
block|{
name|out
operator|.
name|write
argument_list|(
name|buffer
argument_list|,
name|offset
operator|+
name|position
argument_list|,
name|remaining
argument_list|)
expr_stmt|;
name|position
operator|=
name|limit
expr_stmt|;
name|remaining
operator|=
name|limit
operator|-
name|position
expr_stmt|;
block|}
specifier|public
name|void
name|writeTo
parameter_list|(
name|DataOutput
name|out
parameter_list|)
throws|throws
name|IOException
block|{
name|out
operator|.
name|write
argument_list|(
name|buffer
argument_list|,
name|offset
operator|+
name|position
argument_list|,
name|remaining
argument_list|)
expr_stmt|;
name|position
operator|=
name|limit
expr_stmt|;
name|remaining
operator|=
name|limit
operator|-
name|position
expr_stmt|;
block|}
comment|/**      * @see org.apache.activeio.packet.Packet#read()      */
specifier|public
name|int
name|read
parameter_list|()
block|{
if|if
condition|(
operator|!
operator|(
name|remaining
operator|>
literal|0
operator|)
condition|)
return|return
operator|-
literal|1
return|;
name|int
name|rc
init|=
name|buffer
index|[
name|offset
operator|+
name|position
index|]
decl_stmt|;
name|position
operator|++
expr_stmt|;
name|remaining
operator|=
name|limit
operator|-
name|position
expr_stmt|;
return|return
name|rc
operator|&
literal|0xff
return|;
block|}
comment|/**      * @see org.apache.activeio.packet.Packet#read(byte[], int, int)      */
specifier|public
name|int
name|read
parameter_list|(
name|byte
index|[]
name|data
parameter_list|,
name|int
name|offset
parameter_list|,
name|int
name|length
parameter_list|)
block|{
if|if
condition|(
operator|!
operator|(
name|remaining
operator|>
literal|0
operator|)
condition|)
return|return
operator|-
literal|1
return|;
name|int
name|copyLength
init|=
operator|(
operator|(
name|length
operator|<=
name|remaining
operator|)
condition|?
name|length
else|:
name|remaining
operator|)
decl_stmt|;
name|System
operator|.
name|arraycopy
argument_list|(
name|buffer
argument_list|,
name|this
operator|.
name|offset
operator|+
name|position
argument_list|,
name|data
argument_list|,
name|offset
argument_list|,
name|copyLength
argument_list|)
expr_stmt|;
name|position
operator|+=
name|copyLength
expr_stmt|;
name|remaining
operator|=
name|limit
operator|-
name|position
expr_stmt|;
return|return
name|copyLength
return|;
block|}
comment|/**      * @see org.apache.activeio.packet.Packet#write(int)      */
specifier|public
name|boolean
name|write
parameter_list|(
name|int
name|data
parameter_list|)
block|{
if|if
condition|(
operator|!
operator|(
name|remaining
operator|>
literal|0
operator|)
condition|)
return|return
literal|false
return|;
name|buffer
index|[
name|offset
operator|+
name|position
index|]
operator|=
operator|(
name|byte
operator|)
name|data
expr_stmt|;
name|position
operator|++
expr_stmt|;
name|remaining
operator|=
name|limit
operator|-
name|position
expr_stmt|;
return|return
literal|true
return|;
block|}
comment|/**      * @see org.apache.activeio.packet.Packet#write(byte[], int, int)      */
specifier|public
name|int
name|write
parameter_list|(
name|byte
index|[]
name|data
parameter_list|,
name|int
name|offset
parameter_list|,
name|int
name|length
parameter_list|)
block|{
if|if
condition|(
operator|!
operator|(
name|remaining
operator|>
literal|0
operator|)
condition|)
return|return
operator|-
literal|1
return|;
name|int
name|copyLength
init|=
operator|(
operator|(
name|length
operator|<=
name|remaining
operator|)
condition|?
name|length
else|:
name|remaining
operator|)
decl_stmt|;
name|System
operator|.
name|arraycopy
argument_list|(
name|data
argument_list|,
name|offset
argument_list|,
name|buffer
argument_list|,
name|this
operator|.
name|offset
operator|+
name|position
argument_list|,
name|copyLength
argument_list|)
expr_stmt|;
name|position
operator|+=
name|copyLength
expr_stmt|;
name|remaining
operator|=
name|limit
operator|-
name|position
expr_stmt|;
return|return
name|copyLength
return|;
block|}
specifier|public
name|ByteSequence
name|asByteSequence
parameter_list|()
block|{
return|return
operator|new
name|ByteSequence
argument_list|(
name|buffer
argument_list|,
name|offset
operator|+
name|position
argument_list|,
name|remaining
argument_list|)
return|;
block|}
comment|/**      * @see org.apache.activeio.packet.Packet#sliceAsBytes()      */
specifier|public
name|byte
index|[]
name|sliceAsBytes
parameter_list|()
block|{
if|if
condition|(
name|buffer
operator|.
name|length
operator|==
name|remaining
condition|)
block|{
return|return
name|buffer
return|;
block|}
else|else
block|{
name|byte
name|rc
index|[]
init|=
operator|new
name|byte
index|[
name|remaining
index|]
decl_stmt|;
name|int
name|op
init|=
name|position
decl_stmt|;
name|read
argument_list|(
name|rc
argument_list|,
literal|0
argument_list|,
name|remaining
argument_list|)
expr_stmt|;
name|position
operator|=
name|op
expr_stmt|;
name|remaining
operator|=
name|limit
operator|-
name|position
expr_stmt|;
return|return
name|rc
return|;
block|}
block|}
comment|/**      * @param dest      * @return the number of bytes read into the dest.      */
specifier|public
name|int
name|read
parameter_list|(
name|Packet
name|dest
parameter_list|)
block|{
name|int
name|a
init|=
name|dest
operator|.
name|remaining
argument_list|()
decl_stmt|;
name|int
name|rc
init|=
operator|(
operator|(
name|a
operator|<=
name|remaining
operator|)
condition|?
name|a
else|:
name|remaining
operator|)
decl_stmt|;
if|if
condition|(
name|rc
operator|>
literal|0
condition|)
block|{
name|dest
operator|.
name|write
argument_list|(
name|buffer
argument_list|,
name|offset
operator|+
name|position
argument_list|,
name|rc
argument_list|)
expr_stmt|;
name|position
operator|=
name|position
operator|+
name|rc
expr_stmt|;
name|remaining
operator|=
name|limit
operator|-
name|position
expr_stmt|;
block|}
return|return
name|rc
return|;
block|}
specifier|public
name|String
name|toString
parameter_list|()
block|{
return|return
literal|"{position="
operator|+
name|position
operator|+
literal|",limit="
operator|+
name|limit
operator|+
literal|",capacity="
operator|+
name|capacity
operator|+
literal|"}"
return|;
block|}
specifier|public
name|Object
name|getAdapter
parameter_list|(
name|Class
name|target
parameter_list|)
block|{
if|if
condition|(
name|target
operator|.
name|isAssignableFrom
argument_list|(
name|getClass
argument_list|()
argument_list|)
condition|)
block|{
return|return
name|this
return|;
block|}
return|return
literal|null
return|;
block|}
specifier|public
name|byte
index|[]
name|getBuffer
parameter_list|()
block|{
return|return
name|buffer
return|;
block|}
specifier|public
name|void
name|dispose
parameter_list|()
block|{             }
block|}
end_class

end_unit

