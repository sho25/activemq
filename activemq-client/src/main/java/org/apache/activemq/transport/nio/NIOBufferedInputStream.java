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
name|Channel
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
name|ClosedChannelException
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
name|ReadableByteChannel
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
name|SelectionKey
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
name|Selector
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
name|SocketChannel
import|;
end_import

begin_comment
comment|/**  * Implementation of InputStream using Java NIO channel,direct buffer and  * Selector  */
end_comment

begin_class
specifier|public
class|class
name|NIOBufferedInputStream
extends|extends
name|InputStream
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
name|SocketChannel
name|sc
init|=
literal|null
decl_stmt|;
specifier|private
name|ByteBuffer
name|bb
init|=
literal|null
decl_stmt|;
specifier|private
name|Selector
name|rs
init|=
literal|null
decl_stmt|;
specifier|public
name|NIOBufferedInputStream
parameter_list|(
name|ReadableByteChannel
name|channel
parameter_list|,
name|int
name|size
parameter_list|)
throws|throws
name|ClosedChannelException
throws|,
name|IOException
block|{
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
name|this
operator|.
name|bb
operator|=
name|ByteBuffer
operator|.
name|allocateDirect
argument_list|(
name|size
argument_list|)
expr_stmt|;
name|this
operator|.
name|sc
operator|=
operator|(
name|SocketChannel
operator|)
name|channel
expr_stmt|;
name|this
operator|.
name|sc
operator|.
name|configureBlocking
argument_list|(
literal|false
argument_list|)
expr_stmt|;
name|this
operator|.
name|rs
operator|=
name|Selector
operator|.
name|open
argument_list|()
expr_stmt|;
name|sc
operator|.
name|register
argument_list|(
name|rs
argument_list|,
name|SelectionKey
operator|.
name|OP_READ
argument_list|)
expr_stmt|;
name|bb
operator|.
name|position
argument_list|(
literal|0
argument_list|)
expr_stmt|;
name|bb
operator|.
name|limit
argument_list|(
literal|0
argument_list|)
expr_stmt|;
block|}
specifier|public
name|NIOBufferedInputStream
parameter_list|(
name|ReadableByteChannel
name|channel
parameter_list|)
throws|throws
name|ClosedChannelException
throws|,
name|IOException
block|{
name|this
argument_list|(
name|channel
argument_list|,
name|BUFFER_SIZE
argument_list|)
expr_stmt|;
block|}
specifier|public
name|int
name|available
parameter_list|()
throws|throws
name|IOException
block|{
if|if
condition|(
operator|!
name|rs
operator|.
name|isOpen
argument_list|()
condition|)
throw|throw
operator|new
name|IOException
argument_list|(
literal|"Input Stream Closed"
argument_list|)
throw|;
return|return
name|bb
operator|.
name|remaining
argument_list|()
return|;
block|}
specifier|public
name|void
name|close
parameter_list|()
throws|throws
name|IOException
block|{
if|if
condition|(
name|rs
operator|.
name|isOpen
argument_list|()
condition|)
block|{
name|rs
operator|.
name|close
argument_list|()
expr_stmt|;
if|if
condition|(
name|sc
operator|.
name|isOpen
argument_list|()
condition|)
block|{
name|sc
operator|.
name|socket
argument_list|()
operator|.
name|shutdownInput
argument_list|()
expr_stmt|;
name|sc
operator|.
name|socket
argument_list|()
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
name|bb
operator|=
literal|null
expr_stmt|;
name|sc
operator|=
literal|null
expr_stmt|;
block|}
block|}
specifier|public
name|int
name|read
parameter_list|()
throws|throws
name|IOException
block|{
if|if
condition|(
operator|!
name|rs
operator|.
name|isOpen
argument_list|()
condition|)
throw|throw
operator|new
name|IOException
argument_list|(
literal|"Input Stream Closed"
argument_list|)
throw|;
if|if
condition|(
operator|!
name|bb
operator|.
name|hasRemaining
argument_list|()
condition|)
block|{
try|try
block|{
name|fill
argument_list|(
literal|1
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|ClosedChannelException
name|e
parameter_list|)
block|{
name|close
argument_list|()
expr_stmt|;
return|return
operator|-
literal|1
return|;
block|}
block|}
return|return
operator|(
name|bb
operator|.
name|get
argument_list|()
operator|&
literal|0xFF
operator|)
return|;
block|}
specifier|public
name|int
name|read
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
throws|throws
name|IOException
block|{
name|int
name|bytesCopied
init|=
operator|-
literal|1
decl_stmt|;
if|if
condition|(
operator|!
name|rs
operator|.
name|isOpen
argument_list|()
condition|)
throw|throw
operator|new
name|IOException
argument_list|(
literal|"Input Stream Closed"
argument_list|)
throw|;
while|while
condition|(
name|bytesCopied
operator|==
operator|-
literal|1
condition|)
block|{
if|if
condition|(
name|bb
operator|.
name|hasRemaining
argument_list|()
condition|)
block|{
name|bytesCopied
operator|=
operator|(
name|len
operator|<
name|bb
operator|.
name|remaining
argument_list|()
condition|?
name|len
else|:
name|bb
operator|.
name|remaining
argument_list|()
operator|)
expr_stmt|;
name|bb
operator|.
name|get
argument_list|(
name|b
argument_list|,
name|off
argument_list|,
name|bytesCopied
argument_list|)
expr_stmt|;
block|}
else|else
block|{
try|try
block|{
name|fill
argument_list|(
literal|1
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|ClosedChannelException
name|e
parameter_list|)
block|{
name|close
argument_list|()
expr_stmt|;
return|return
operator|-
literal|1
return|;
block|}
block|}
block|}
return|return
name|bytesCopied
return|;
block|}
specifier|public
name|long
name|skip
parameter_list|(
name|long
name|n
parameter_list|)
throws|throws
name|IOException
block|{
name|long
name|skiped
init|=
literal|0
decl_stmt|;
if|if
condition|(
operator|!
name|rs
operator|.
name|isOpen
argument_list|()
condition|)
throw|throw
operator|new
name|IOException
argument_list|(
literal|"Input Stream Closed"
argument_list|)
throw|;
while|while
condition|(
name|n
operator|>
literal|0
condition|)
block|{
if|if
condition|(
name|n
operator|<=
name|bb
operator|.
name|remaining
argument_list|()
condition|)
block|{
name|skiped
operator|+=
name|n
expr_stmt|;
name|bb
operator|.
name|position
argument_list|(
name|bb
operator|.
name|position
argument_list|()
operator|+
operator|(
name|int
operator|)
name|n
argument_list|)
expr_stmt|;
name|n
operator|=
literal|0
expr_stmt|;
block|}
else|else
block|{
name|skiped
operator|+=
name|bb
operator|.
name|remaining
argument_list|()
expr_stmt|;
name|n
operator|-=
name|bb
operator|.
name|remaining
argument_list|()
expr_stmt|;
name|bb
operator|.
name|position
argument_list|(
name|bb
operator|.
name|limit
argument_list|()
argument_list|)
expr_stmt|;
try|try
block|{
name|fill
argument_list|(
operator|(
name|int
operator|)
name|n
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|ClosedChannelException
name|e
parameter_list|)
block|{
name|close
argument_list|()
expr_stmt|;
return|return
name|skiped
return|;
block|}
block|}
block|}
return|return
name|skiped
return|;
block|}
specifier|private
name|void
name|fill
parameter_list|(
name|int
name|n
parameter_list|)
throws|throws
name|IOException
throws|,
name|ClosedChannelException
block|{
name|int
name|bytesRead
init|=
operator|-
literal|1
decl_stmt|;
if|if
condition|(
operator|(
name|n
operator|<=
literal|0
operator|)
operator|||
operator|(
name|n
operator|<=
name|bb
operator|.
name|remaining
argument_list|()
operator|)
condition|)
return|return;
name|bb
operator|.
name|compact
argument_list|()
expr_stmt|;
name|n
operator|=
operator|(
name|bb
operator|.
name|remaining
argument_list|()
operator|<
name|n
condition|?
name|bb
operator|.
name|remaining
argument_list|()
else|:
name|n
operator|)
expr_stmt|;
for|for
control|(
init|;
condition|;
control|)
block|{
name|bytesRead
operator|=
name|sc
operator|.
name|read
argument_list|(
name|bb
argument_list|)
expr_stmt|;
if|if
condition|(
name|bytesRead
operator|==
operator|-
literal|1
condition|)
throw|throw
operator|new
name|ClosedChannelException
argument_list|()
throw|;
name|n
operator|-=
name|bytesRead
expr_stmt|;
if|if
condition|(
name|n
operator|<=
literal|0
condition|)
break|break;
name|rs
operator|.
name|select
argument_list|(
literal|0
argument_list|)
expr_stmt|;
name|rs
operator|.
name|selectedKeys
argument_list|()
operator|.
name|clear
argument_list|()
expr_stmt|;
block|}
name|bb
operator|.
name|flip
argument_list|()
expr_stmt|;
block|}
block|}
end_class

end_unit

