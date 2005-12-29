begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  *  * Copyright 2005-2006 The Apache Software Foundation  *  * Licensed under the Apache License, Version 2.0 (the "License");  * you may not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  * http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
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

begin_import
import|import
name|java
operator|.
name|nio
operator|.
name|ByteBuffer
import|;
end_import

begin_comment
comment|/**  * Provides a Packet implementation that is backed by a {@see java.nio.ByteBuffer}  *   * @version $Revision$  */
end_comment

begin_class
specifier|final
specifier|public
class|class
name|ByteBufferPacket
implements|implements
name|Packet
block|{
specifier|public
specifier|static
specifier|final
name|int
name|DEFAULT_BUFFER_SIZE
init|=
name|Integer
operator|.
name|parseInt
argument_list|(
name|System
operator|.
name|getProperty
argument_list|(
literal|"org.apache.activeio.DefaultByteBufferSize"
argument_list|,
literal|""
operator|+
operator|(
literal|64
operator|*
literal|1024
operator|)
argument_list|)
argument_list|)
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|int
name|DEFAULT_DIRECT_BUFFER_SIZE
init|=
name|Integer
operator|.
name|parseInt
argument_list|(
name|System
operator|.
name|getProperty
argument_list|(
literal|"org.apache.activeio.DefaultDirectByteBufferSize"
argument_list|,
literal|""
operator|+
operator|(
literal|8
operator|*
literal|1024
operator|)
argument_list|)
argument_list|)
decl_stmt|;
specifier|private
specifier|final
name|ByteBuffer
name|buffer
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|int
name|TEMP_BUFFER_SIZE
init|=
literal|64
operator|*
literal|1024
decl_stmt|;
specifier|public
name|ByteBufferPacket
parameter_list|(
name|ByteBuffer
name|buffer
parameter_list|)
block|{
name|this
operator|.
name|buffer
operator|=
name|buffer
expr_stmt|;
name|clear
argument_list|()
expr_stmt|;
block|}
specifier|public
name|ByteBuffer
name|getByteBuffer
parameter_list|()
block|{
return|return
name|buffer
return|;
block|}
specifier|public
specifier|static
name|ByteBufferPacket
name|createDefaultBuffer
parameter_list|(
name|boolean
name|direct
parameter_list|)
block|{
if|if
condition|(
name|direct
condition|)
return|return
operator|new
name|ByteBufferPacket
argument_list|(
name|ByteBuffer
operator|.
name|allocateDirect
argument_list|(
name|DEFAULT_DIRECT_BUFFER_SIZE
argument_list|)
argument_list|)
return|;
return|return
operator|new
name|ByteBufferPacket
argument_list|(
name|ByteBuffer
operator|.
name|allocate
argument_list|(
name|DEFAULT_BUFFER_SIZE
argument_list|)
argument_list|)
return|;
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
if|if
condition|(
name|buffer
operator|.
name|hasArray
argument_list|()
condition|)
block|{
comment|// If the buffer is backed by an array.. then use it directly.
name|out
operator|.
name|write
argument_list|(
name|buffer
operator|.
name|array
argument_list|()
argument_list|,
name|position
argument_list|()
argument_list|,
name|remaining
argument_list|()
argument_list|)
expr_stmt|;
name|position
argument_list|(
name|limit
argument_list|()
argument_list|)
expr_stmt|;
block|}
else|else
block|{
comment|// It's not backed by a buffer.. We can only dump it to a OutputStream via a byte[] so,
comment|// create a temp buffer that we can use to chunk it out.
name|byte
name|temp
index|[]
init|=
operator|new
name|byte
index|[
name|TEMP_BUFFER_SIZE
index|]
decl_stmt|;
while|while
condition|(
name|buffer
operator|.
name|hasRemaining
argument_list|()
condition|)
block|{
name|int
name|maxWrite
init|=
name|buffer
operator|.
name|remaining
argument_list|()
operator|>
name|temp
operator|.
name|length
condition|?
name|temp
operator|.
name|length
else|:
name|buffer
operator|.
name|remaining
argument_list|()
decl_stmt|;
name|buffer
operator|.
name|get
argument_list|(
name|temp
argument_list|,
literal|0
argument_list|,
name|maxWrite
argument_list|)
expr_stmt|;
name|out
operator|.
name|write
argument_list|(
name|temp
argument_list|,
literal|0
argument_list|,
name|maxWrite
argument_list|)
expr_stmt|;
block|}
block|}
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
if|if
condition|(
name|buffer
operator|.
name|hasArray
argument_list|()
condition|)
block|{
comment|// If the buffer is backed by an array.. then use it directly.
name|out
operator|.
name|write
argument_list|(
name|buffer
operator|.
name|array
argument_list|()
argument_list|,
name|position
argument_list|()
argument_list|,
name|remaining
argument_list|()
argument_list|)
expr_stmt|;
name|position
argument_list|(
name|limit
argument_list|()
argument_list|)
expr_stmt|;
block|}
else|else
block|{
comment|// It's not backed by a buffer.. We can only dump it to a OutputStream via a byte[] so,
comment|// create a temp buffer that we can use to chunk it out.
name|byte
name|temp
index|[]
init|=
operator|new
name|byte
index|[
name|TEMP_BUFFER_SIZE
index|]
decl_stmt|;
while|while
condition|(
name|buffer
operator|.
name|hasRemaining
argument_list|()
condition|)
block|{
name|int
name|maxWrite
init|=
name|buffer
operator|.
name|remaining
argument_list|()
operator|>
name|temp
operator|.
name|length
condition|?
name|temp
operator|.
name|length
else|:
name|buffer
operator|.
name|remaining
argument_list|()
decl_stmt|;
name|buffer
operator|.
name|get
argument_list|(
name|temp
argument_list|,
literal|0
argument_list|,
name|maxWrite
argument_list|)
expr_stmt|;
name|out
operator|.
name|write
argument_list|(
name|temp
argument_list|,
literal|0
argument_list|,
name|maxWrite
argument_list|)
expr_stmt|;
block|}
block|}
block|}
specifier|public
name|int
name|capacity
parameter_list|()
block|{
return|return
name|buffer
operator|.
name|capacity
argument_list|()
return|;
block|}
specifier|public
name|void
name|clear
parameter_list|()
block|{
name|buffer
operator|.
name|clear
argument_list|()
expr_stmt|;
block|}
specifier|public
name|Packet
name|compact
parameter_list|()
block|{
name|buffer
operator|.
name|compact
argument_list|()
expr_stmt|;
return|return
name|this
return|;
block|}
specifier|public
name|void
name|flip
parameter_list|()
block|{
name|buffer
operator|.
name|flip
argument_list|()
expr_stmt|;
block|}
specifier|public
name|boolean
name|hasRemaining
parameter_list|()
block|{
return|return
name|buffer
operator|.
name|hasRemaining
argument_list|()
return|;
block|}
specifier|public
name|boolean
name|isDirect
parameter_list|()
block|{
return|return
name|buffer
operator|.
name|isDirect
argument_list|()
return|;
block|}
specifier|public
name|boolean
name|isReadOnly
parameter_list|()
block|{
return|return
name|buffer
operator|.
name|isReadOnly
argument_list|()
return|;
block|}
specifier|public
name|int
name|limit
parameter_list|()
block|{
return|return
name|buffer
operator|.
name|limit
argument_list|()
return|;
block|}
specifier|public
name|void
name|limit
parameter_list|(
name|int
name|arg0
parameter_list|)
block|{
name|buffer
operator|.
name|limit
argument_list|(
name|arg0
argument_list|)
expr_stmt|;
block|}
specifier|public
name|Packet
name|mark
parameter_list|()
block|{
name|buffer
operator|.
name|mark
argument_list|()
expr_stmt|;
return|return
name|this
return|;
block|}
specifier|public
name|int
name|position
parameter_list|()
block|{
return|return
name|buffer
operator|.
name|position
argument_list|()
return|;
block|}
specifier|public
name|void
name|position
parameter_list|(
name|int
name|arg0
parameter_list|)
block|{
name|buffer
operator|.
name|position
argument_list|(
name|arg0
argument_list|)
expr_stmt|;
block|}
specifier|public
name|int
name|remaining
parameter_list|()
block|{
return|return
name|buffer
operator|.
name|remaining
argument_list|()
return|;
block|}
specifier|public
name|void
name|rewind
parameter_list|()
block|{
name|buffer
operator|.
name|rewind
argument_list|()
expr_stmt|;
block|}
specifier|public
name|Packet
name|slice
parameter_list|()
block|{
return|return
operator|new
name|ByteBufferPacket
argument_list|(
name|buffer
operator|.
name|slice
argument_list|()
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
name|ByteBufferPacket
argument_list|(
name|buffer
operator|.
name|duplicate
argument_list|()
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
name|ByteBufferPacket
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
name|ByteBuffer
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
operator|.
name|duplicate
argument_list|()
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
comment|/**      * @see org.apache.activeio.packet.Packet#read()      */
specifier|public
name|int
name|read
parameter_list|()
block|{
if|if
condition|(
operator|!
name|buffer
operator|.
name|hasRemaining
argument_list|()
condition|)
return|return
operator|-
literal|1
return|;
return|return
name|buffer
operator|.
name|get
argument_list|()
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
name|hasRemaining
argument_list|()
condition|)
return|return
operator|-
literal|1
return|;
name|int
name|copyLength
init|=
name|Math
operator|.
name|min
argument_list|(
name|length
argument_list|,
name|remaining
argument_list|()
argument_list|)
decl_stmt|;
name|buffer
operator|.
name|get
argument_list|(
name|data
argument_list|,
name|offset
argument_list|,
name|copyLength
argument_list|)
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
name|buffer
operator|.
name|hasRemaining
argument_list|()
condition|)
return|return
literal|false
return|;
name|buffer
operator|.
name|put
argument_list|(
operator|(
name|byte
operator|)
name|data
argument_list|)
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
name|hasRemaining
argument_list|()
condition|)
return|return
operator|-
literal|1
return|;
name|int
name|copyLength
init|=
name|Math
operator|.
name|min
argument_list|(
name|length
argument_list|,
name|remaining
argument_list|()
argument_list|)
decl_stmt|;
name|buffer
operator|.
name|put
argument_list|(
name|data
argument_list|,
name|offset
argument_list|,
name|copyLength
argument_list|)
expr_stmt|;
return|return
name|copyLength
return|;
block|}
comment|/**      * @see org.apache.activeio.packet.Packet#asByteSequence()      */
specifier|public
name|ByteSequence
name|asByteSequence
parameter_list|()
block|{
if|if
condition|(
name|buffer
operator|.
name|hasArray
argument_list|()
condition|)
block|{
name|byte
index|[]
name|bs
init|=
name|buffer
operator|.
name|array
argument_list|()
decl_stmt|;
return|return
operator|new
name|ByteSequence
argument_list|(
name|bs
argument_list|,
name|buffer
operator|.
name|position
argument_list|()
argument_list|,
name|buffer
operator|.
name|remaining
argument_list|()
argument_list|)
return|;
block|}
comment|// TODO: implement the direct case.
return|return
literal|null
return|;
block|}
comment|/**      * @see org.apache.activeio.packet.Packet#sliceAsBytes()      */
specifier|public
name|byte
index|[]
name|sliceAsBytes
parameter_list|()
block|{
comment|// TODO Auto-generated method stub
return|return
literal|null
return|;
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
name|rc
init|=
name|Math
operator|.
name|min
argument_list|(
name|dest
operator|.
name|remaining
argument_list|()
argument_list|,
name|remaining
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
name|rc
operator|>
literal|0
condition|)
block|{
if|if
condition|(
name|dest
operator|.
name|getClass
argument_list|()
operator|==
name|ByteBufferPacket
operator|.
name|class
condition|)
block|{
comment|// Adjust our limit so that we don't overflow the dest buffer.
name|int
name|limit
init|=
name|limit
argument_list|()
decl_stmt|;
name|limit
argument_list|(
name|position
argument_list|()
operator|+
name|rc
argument_list|)
expr_stmt|;
operator|(
operator|(
name|ByteBufferPacket
operator|)
name|dest
operator|)
operator|.
name|buffer
operator|.
name|put
argument_list|(
name|buffer
argument_list|)
expr_stmt|;
comment|// restore the limit.
name|limit
argument_list|(
name|limit
argument_list|)
expr_stmt|;
return|return
literal|0
return|;
block|}
else|else
block|{
name|ByteSequence
name|sequence
init|=
name|dest
operator|.
name|asByteSequence
argument_list|()
decl_stmt|;
name|rc
operator|=
name|read
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
name|dest
operator|.
name|position
argument_list|(
name|dest
operator|.
name|position
argument_list|()
operator|+
name|rc
argument_list|)
expr_stmt|;
block|}
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
argument_list|()
operator|+
literal|",limit="
operator|+
name|limit
argument_list|()
operator|+
literal|",capacity="
operator|+
name|capacity
argument_list|()
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
name|void
name|dispose
parameter_list|()
block|{             }
block|}
end_class

end_unit

