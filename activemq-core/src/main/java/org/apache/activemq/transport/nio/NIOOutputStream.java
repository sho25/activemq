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
name|transport
operator|.
name|nio
package|;
end_package

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|EOFException
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
name|InterruptedIOException
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
name|nio
operator|.
name|ByteBuffer
import|;
end_import

begin_import
import|import
name|java
operator|.
name|nio
operator|.
name|channels
operator|.
name|WritableByteChannel
import|;
end_import

begin_comment
comment|/**  * An optimized buffered outputstream for Tcp  *   * @version $Revision: 1.1.1.1 $  */
end_comment

begin_class
specifier|public
class|class
name|NIOOutputStream
extends|extends
name|OutputStream
block|{
specifier|private
specifier|final
specifier|static
name|int
name|BUFFER_SIZE
init|=
literal|8192
decl_stmt|;
specifier|private
specifier|final
name|WritableByteChannel
name|out
decl_stmt|;
specifier|private
specifier|final
name|byte
index|[]
name|buffer
decl_stmt|;
specifier|private
specifier|final
name|ByteBuffer
name|byteBuffer
decl_stmt|;
specifier|private
name|int
name|count
decl_stmt|;
specifier|private
name|boolean
name|closed
decl_stmt|;
comment|/**      * Constructor      *       * @param out      */
specifier|public
name|NIOOutputStream
parameter_list|(
name|WritableByteChannel
name|out
parameter_list|)
block|{
name|this
argument_list|(
name|out
argument_list|,
name|BUFFER_SIZE
argument_list|)
expr_stmt|;
block|}
comment|/**      * Creates a new buffered output stream to write data to the specified      * underlying output stream with the specified buffer size.      *       * @param out the underlying output stream.      * @param size the buffer size.      * @throws IllegalArgumentException if size<= 0.      */
specifier|public
name|NIOOutputStream
parameter_list|(
name|WritableByteChannel
name|out
parameter_list|,
name|int
name|size
parameter_list|)
block|{
name|this
operator|.
name|out
operator|=
name|out
expr_stmt|;
if|if
condition|(
name|size
operator|<=
literal|0
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"Buffer size<= 0"
argument_list|)
throw|;
block|}
name|buffer
operator|=
operator|new
name|byte
index|[
name|size
index|]
expr_stmt|;
name|byteBuffer
operator|=
name|ByteBuffer
operator|.
name|wrap
argument_list|(
name|buffer
argument_list|)
expr_stmt|;
block|}
comment|/**      * write a byte on to the stream      *       * @param b - byte to write      * @throws IOException      */
specifier|public
name|void
name|write
parameter_list|(
name|int
name|b
parameter_list|)
throws|throws
name|IOException
block|{
name|checkClosed
argument_list|()
expr_stmt|;
if|if
condition|(
name|availableBufferToWrite
argument_list|()
operator|<
literal|1
condition|)
block|{
name|flush
argument_list|()
expr_stmt|;
block|}
name|buffer
index|[
name|count
operator|++
index|]
operator|=
operator|(
name|byte
operator|)
name|b
expr_stmt|;
block|}
comment|/**      * write a byte array to the stream      *       * @param b the byte buffer      * @param off the offset into the buffer      * @param len the length of data to write      * @throws IOException      */
specifier|public
name|void
name|write
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
throws|throws
name|IOException
block|{
name|checkClosed
argument_list|()
expr_stmt|;
if|if
condition|(
name|availableBufferToWrite
argument_list|()
operator|<
name|len
condition|)
block|{
name|flush
argument_list|()
expr_stmt|;
block|}
if|if
condition|(
name|buffer
operator|.
name|length
operator|>=
name|len
condition|)
block|{
name|System
operator|.
name|arraycopy
argument_list|(
name|b
argument_list|,
name|off
argument_list|,
name|buffer
argument_list|,
name|count
argument_list|,
name|len
argument_list|)
expr_stmt|;
name|count
operator|+=
name|len
expr_stmt|;
block|}
else|else
block|{
name|write
argument_list|(
name|ByteBuffer
operator|.
name|wrap
argument_list|(
name|b
argument_list|,
name|off
argument_list|,
name|len
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
comment|/**      * flush the data to the output stream This doesn't call flush on the      * underlying outputstream, because Tcp is particularly efficent at doing      * this itself ....      *       * @throws IOException      */
specifier|public
name|void
name|flush
parameter_list|()
throws|throws
name|IOException
block|{
if|if
condition|(
name|count
operator|>
literal|0
operator|&&
name|out
operator|!=
literal|null
condition|)
block|{
name|byteBuffer
operator|.
name|position
argument_list|(
literal|0
argument_list|)
expr_stmt|;
name|byteBuffer
operator|.
name|limit
argument_list|(
name|count
argument_list|)
expr_stmt|;
name|write
argument_list|(
name|byteBuffer
argument_list|)
expr_stmt|;
name|count
operator|=
literal|0
expr_stmt|;
block|}
block|}
comment|/**      * close this stream      *       * @throws IOException      */
specifier|public
name|void
name|close
parameter_list|()
throws|throws
name|IOException
block|{
name|super
operator|.
name|close
argument_list|()
expr_stmt|;
name|closed
operator|=
literal|true
expr_stmt|;
block|}
comment|/**      * Checks that the stream has not been closed      *       * @throws IOException      */
specifier|protected
name|void
name|checkClosed
parameter_list|()
throws|throws
name|IOException
block|{
if|if
condition|(
name|closed
condition|)
block|{
throw|throw
operator|new
name|EOFException
argument_list|(
literal|"Cannot write to the stream any more it has already been closed"
argument_list|)
throw|;
block|}
block|}
comment|/**      * @return the amount free space in the buffer      */
specifier|private
name|int
name|availableBufferToWrite
parameter_list|()
block|{
return|return
name|buffer
operator|.
name|length
operator|-
name|count
return|;
block|}
specifier|protected
name|void
name|write
parameter_list|(
name|ByteBuffer
name|data
parameter_list|)
throws|throws
name|IOException
block|{
name|int
name|remaining
init|=
name|data
operator|.
name|remaining
argument_list|()
decl_stmt|;
name|int
name|lastRemaining
init|=
name|remaining
operator|-
literal|1
decl_stmt|;
name|long
name|delay
init|=
literal|1
decl_stmt|;
while|while
condition|(
name|remaining
operator|>
literal|0
condition|)
block|{
comment|// We may need to do a little bit of sleeping to avoid a busy loop.
comment|// Slow down if no data was written out..
if|if
condition|(
name|remaining
operator|==
name|lastRemaining
condition|)
block|{
try|try
block|{
comment|// Use exponential rollback to increase sleep time.
name|Thread
operator|.
name|sleep
argument_list|(
name|delay
argument_list|)
expr_stmt|;
name|delay
operator|*=
literal|2
expr_stmt|;
if|if
condition|(
name|delay
operator|>
literal|1000
condition|)
block|{
name|delay
operator|=
literal|1000
expr_stmt|;
block|}
block|}
catch|catch
parameter_list|(
name|InterruptedException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|InterruptedIOException
argument_list|()
throw|;
block|}
block|}
else|else
block|{
name|delay
operator|=
literal|1
expr_stmt|;
block|}
name|lastRemaining
operator|=
name|remaining
expr_stmt|;
comment|// Since the write is non-blocking, all the data may not have been
comment|// written.
name|out
operator|.
name|write
argument_list|(
name|data
argument_list|)
expr_stmt|;
name|remaining
operator|=
name|data
operator|.
name|remaining
argument_list|()
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

