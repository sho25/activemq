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
operator|.
name|async
operator|.
name|aio
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
name|InterruptedIOException
import|;
end_import

begin_import
import|import
name|java
operator|.
name|net
operator|.
name|InetAddress
import|;
end_import

begin_import
import|import
name|java
operator|.
name|net
operator|.
name|Socket
import|;
end_import

begin_import
import|import
name|java
operator|.
name|net
operator|.
name|SocketAddress
import|;
end_import

begin_import
import|import
name|java
operator|.
name|net
operator|.
name|SocketException
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
name|org
operator|.
name|apache
operator|.
name|activeio
operator|.
name|packet
operator|.
name|ByteBufferPacket
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|activeio
operator|.
name|packet
operator|.
name|EOSPacket
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|activeio
operator|.
name|packet
operator|.
name|Packet
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|activeio
operator|.
name|packet
operator|.
name|async
operator|.
name|AsyncChannel
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|activeio
operator|.
name|packet
operator|.
name|async
operator|.
name|AsyncChannelListener
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|activeio
operator|.
name|stream
operator|.
name|sync
operator|.
name|socket
operator|.
name|SocketMetadata
import|;
end_import

begin_import
import|import
name|edu
operator|.
name|emory
operator|.
name|mathcs
operator|.
name|backport
operator|.
name|java
operator|.
name|util
operator|.
name|concurrent
operator|.
name|CountDownLatch
import|;
end_import

begin_import
import|import
name|edu
operator|.
name|emory
operator|.
name|mathcs
operator|.
name|backport
operator|.
name|java
operator|.
name|util
operator|.
name|concurrent
operator|.
name|TimeUnit
import|;
end_import

begin_import
import|import
name|edu
operator|.
name|emory
operator|.
name|mathcs
operator|.
name|backport
operator|.
name|java
operator|.
name|util
operator|.
name|concurrent
operator|.
name|atomic
operator|.
name|AtomicBoolean
import|;
end_import

begin_import
import|import
name|com
operator|.
name|ibm
operator|.
name|io
operator|.
name|async
operator|.
name|AsyncSocketChannel
import|;
end_import

begin_import
import|import
name|com
operator|.
name|ibm
operator|.
name|io
operator|.
name|async
operator|.
name|IAbstractAsyncFuture
import|;
end_import

begin_import
import|import
name|com
operator|.
name|ibm
operator|.
name|io
operator|.
name|async
operator|.
name|IAsyncFuture
import|;
end_import

begin_import
import|import
name|com
operator|.
name|ibm
operator|.
name|io
operator|.
name|async
operator|.
name|ICompletionListener
import|;
end_import

begin_comment
comment|/**  * @version $Revision$  */
end_comment

begin_class
specifier|final
specifier|public
class|class
name|AIOAsyncChannel
implements|implements
name|AsyncChannel
implements|,
name|ICompletionListener
implements|,
name|SocketMetadata
block|{
specifier|protected
specifier|static
specifier|final
name|int
name|DEFAULT_BUFFER_SIZE
init|=
name|ByteBufferPacket
operator|.
name|DEFAULT_DIRECT_BUFFER_SIZE
decl_stmt|;
specifier|private
specifier|final
name|AsyncSocketChannel
name|socketChannel
decl_stmt|;
specifier|private
specifier|final
name|Socket
name|socket
decl_stmt|;
specifier|private
name|AsyncChannelListener
name|channelListener
decl_stmt|;
specifier|private
name|ByteBuffer
name|inputByteBuffer
decl_stmt|;
specifier|private
specifier|final
name|AtomicBoolean
name|running
init|=
operator|new
name|AtomicBoolean
argument_list|(
literal|false
argument_list|)
decl_stmt|;
specifier|private
name|CountDownLatch
name|doneCountDownLatch
decl_stmt|;
specifier|protected
name|AIOAsyncChannel
parameter_list|(
name|AsyncSocketChannel
name|socketChannel
parameter_list|)
throws|throws
name|IOException
block|{
name|this
operator|.
name|socketChannel
operator|=
name|socketChannel
expr_stmt|;
name|this
operator|.
name|socket
operator|=
name|socketChannel
operator|.
name|socket
argument_list|()
expr_stmt|;
name|this
operator|.
name|socket
operator|.
name|setSendBufferSize
argument_list|(
name|DEFAULT_BUFFER_SIZE
argument_list|)
expr_stmt|;
name|this
operator|.
name|socket
operator|.
name|setReceiveBufferSize
argument_list|(
name|DEFAULT_BUFFER_SIZE
argument_list|)
expr_stmt|;
name|this
operator|.
name|socket
operator|.
name|setSoTimeout
argument_list|(
literal|0
argument_list|)
expr_stmt|;
block|}
specifier|private
name|ByteBuffer
name|allocateBuffer
parameter_list|()
block|{
return|return
name|ByteBuffer
operator|.
name|allocateDirect
argument_list|(
name|DEFAULT_BUFFER_SIZE
argument_list|)
return|;
block|}
specifier|public
name|void
name|setAsyncChannelListener
parameter_list|(
name|AsyncChannelListener
name|channelListener
parameter_list|)
block|{
name|this
operator|.
name|channelListener
operator|=
name|channelListener
expr_stmt|;
block|}
specifier|public
name|AsyncChannelListener
name|getAsyncChannelListener
parameter_list|()
block|{
return|return
name|channelListener
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
block|{
if|if
condition|(
name|running
operator|.
name|get
argument_list|()
operator|&&
name|channelListener
operator|!=
literal|null
condition|)
block|{
name|channelListener
operator|.
name|onPacketError
argument_list|(
operator|new
name|SocketException
argument_list|(
literal|"Socket closed."
argument_list|)
argument_list|)
expr_stmt|;
block|}
try|try
block|{
name|stop
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{         }
try|try
block|{
name|socketChannel
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{         }
block|}
specifier|public
name|void
name|start
parameter_list|()
throws|throws
name|IOException
block|{
if|if
condition|(
name|running
operator|.
name|compareAndSet
argument_list|(
literal|false
argument_list|,
literal|true
argument_list|)
condition|)
block|{
name|doneCountDownLatch
operator|=
operator|new
name|CountDownLatch
argument_list|(
literal|1
argument_list|)
expr_stmt|;
name|requestNextRead
argument_list|()
expr_stmt|;
block|}
block|}
specifier|public
name|void
name|stop
parameter_list|()
throws|throws
name|IOException
block|{
if|if
condition|(
name|running
operator|.
name|compareAndSet
argument_list|(
literal|true
argument_list|,
literal|false
argument_list|)
condition|)
block|{
try|try
block|{
name|doneCountDownLatch
operator|.
name|await
argument_list|(
literal|5
argument_list|,
name|TimeUnit
operator|.
name|SECONDS
argument_list|)
expr_stmt|;
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
block|}
specifier|public
name|void
name|write
parameter_list|(
name|Packet
name|packet
parameter_list|)
throws|throws
name|IOException
block|{
name|ByteBuffer
name|data
init|=
operator|(
operator|(
name|ByteBufferPacket
operator|)
name|packet
operator|)
operator|.
name|getByteBuffer
argument_list|()
decl_stmt|;
while|while
condition|(
name|data
operator|.
name|hasRemaining
argument_list|()
condition|)
block|{
name|IAsyncFuture
name|future
init|=
name|socketChannel
operator|.
name|write
argument_list|(
name|data
argument_list|)
decl_stmt|;
try|try
block|{
name|future
operator|.
name|getByteCount
argument_list|()
expr_stmt|;
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
block|}
specifier|public
name|void
name|flush
parameter_list|()
throws|throws
name|IOException
block|{     }
specifier|public
name|void
name|futureCompleted
parameter_list|(
name|IAbstractAsyncFuture
name|abstractFuture
parameter_list|,
name|Object
name|attribute
parameter_list|)
block|{
name|IAsyncFuture
name|future
init|=
operator|(
name|IAsyncFuture
operator|)
name|abstractFuture
decl_stmt|;
try|try
block|{
if|if
condition|(
name|inputByteBuffer
operator|.
name|position
argument_list|()
operator|>
literal|0
condition|)
block|{
name|ByteBuffer
name|remaining
init|=
name|inputByteBuffer
operator|.
name|slice
argument_list|()
decl_stmt|;
name|Packet
name|data
init|=
operator|new
name|ByteBufferPacket
argument_list|(
operator|(
operator|(
name|ByteBuffer
operator|)
name|inputByteBuffer
operator|.
name|flip
argument_list|()
operator|)
operator|.
name|slice
argument_list|()
argument_list|)
decl_stmt|;
name|channelListener
operator|.
name|onPacket
argument_list|(
name|data
argument_list|)
expr_stmt|;
comment|// Keep the remaining buffer around to fill with data.
name|inputByteBuffer
operator|=
name|remaining
expr_stmt|;
name|requestNextRead
argument_list|()
expr_stmt|;
block|}
else|else
block|{
name|channelListener
operator|.
name|onPacket
argument_list|(
name|EOSPacket
operator|.
name|EOS_PACKET
argument_list|)
expr_stmt|;
block|}
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
name|channelListener
operator|.
name|onPacketError
argument_list|(
name|e
argument_list|)
expr_stmt|;
block|}
block|}
specifier|private
name|void
name|requestNextRead
parameter_list|()
throws|throws
name|InterruptedIOException
block|{
comment|// Don't do next read if we have stopped running.
if|if
condition|(
operator|!
name|running
operator|.
name|get
argument_list|()
condition|)
block|{
name|doneCountDownLatch
operator|.
name|countDown
argument_list|()
expr_stmt|;
return|return;
block|}
try|try
block|{
if|if
condition|(
name|inputByteBuffer
operator|==
literal|null
operator|||
operator|!
name|inputByteBuffer
operator|.
name|hasRemaining
argument_list|()
condition|)
block|{
name|inputByteBuffer
operator|=
name|allocateBuffer
argument_list|()
expr_stmt|;
block|}
name|IAsyncFuture
name|future
init|=
name|socketChannel
operator|.
name|read
argument_list|(
name|inputByteBuffer
argument_list|)
decl_stmt|;
name|future
operator|.
name|addCompletionListener
argument_list|(
name|this
argument_list|,
literal|null
argument_list|,
literal|false
argument_list|)
expr_stmt|;
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
specifier|public
name|InetAddress
name|getInetAddress
parameter_list|()
block|{
return|return
name|socket
operator|.
name|getInetAddress
argument_list|()
return|;
block|}
specifier|public
name|boolean
name|getKeepAlive
parameter_list|()
throws|throws
name|SocketException
block|{
return|return
name|socket
operator|.
name|getKeepAlive
argument_list|()
return|;
block|}
specifier|public
name|InetAddress
name|getLocalAddress
parameter_list|()
block|{
return|return
name|socket
operator|.
name|getLocalAddress
argument_list|()
return|;
block|}
specifier|public
name|int
name|getLocalPort
parameter_list|()
block|{
return|return
name|socket
operator|.
name|getLocalPort
argument_list|()
return|;
block|}
specifier|public
name|SocketAddress
name|getLocalSocketAddress
parameter_list|()
block|{
return|return
name|socket
operator|.
name|getLocalSocketAddress
argument_list|()
return|;
block|}
specifier|public
name|boolean
name|getOOBInline
parameter_list|()
throws|throws
name|SocketException
block|{
return|return
name|socket
operator|.
name|getOOBInline
argument_list|()
return|;
block|}
specifier|public
name|int
name|getPort
parameter_list|()
block|{
return|return
name|socket
operator|.
name|getPort
argument_list|()
return|;
block|}
specifier|public
name|int
name|getReceiveBufferSize
parameter_list|()
throws|throws
name|SocketException
block|{
return|return
name|socket
operator|.
name|getReceiveBufferSize
argument_list|()
return|;
block|}
specifier|public
name|SocketAddress
name|getRemoteSocketAddress
parameter_list|()
block|{
return|return
name|socket
operator|.
name|getRemoteSocketAddress
argument_list|()
return|;
block|}
specifier|public
name|boolean
name|getReuseAddress
parameter_list|()
throws|throws
name|SocketException
block|{
return|return
name|socket
operator|.
name|getReuseAddress
argument_list|()
return|;
block|}
specifier|public
name|int
name|getSendBufferSize
parameter_list|()
throws|throws
name|SocketException
block|{
return|return
name|socket
operator|.
name|getSendBufferSize
argument_list|()
return|;
block|}
specifier|public
name|int
name|getSoLinger
parameter_list|()
throws|throws
name|SocketException
block|{
return|return
name|socket
operator|.
name|getSoLinger
argument_list|()
return|;
block|}
specifier|public
name|int
name|getSoTimeout
parameter_list|()
throws|throws
name|SocketException
block|{
return|return
name|socket
operator|.
name|getSoTimeout
argument_list|()
return|;
block|}
specifier|public
name|boolean
name|getTcpNoDelay
parameter_list|()
throws|throws
name|SocketException
block|{
return|return
name|socket
operator|.
name|getTcpNoDelay
argument_list|()
return|;
block|}
specifier|public
name|int
name|getTrafficClass
parameter_list|()
throws|throws
name|SocketException
block|{
return|return
name|socket
operator|.
name|getTrafficClass
argument_list|()
return|;
block|}
specifier|public
name|boolean
name|isBound
parameter_list|()
block|{
return|return
name|socket
operator|.
name|isBound
argument_list|()
return|;
block|}
specifier|public
name|boolean
name|isClosed
parameter_list|()
block|{
return|return
name|socket
operator|.
name|isClosed
argument_list|()
return|;
block|}
specifier|public
name|boolean
name|isConnected
parameter_list|()
block|{
return|return
name|socket
operator|.
name|isConnected
argument_list|()
return|;
block|}
specifier|public
name|void
name|setKeepAlive
parameter_list|(
name|boolean
name|on
parameter_list|)
throws|throws
name|SocketException
block|{
name|socket
operator|.
name|setKeepAlive
argument_list|(
name|on
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|setOOBInline
parameter_list|(
name|boolean
name|on
parameter_list|)
throws|throws
name|SocketException
block|{
name|socket
operator|.
name|setOOBInline
argument_list|(
name|on
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|setReceiveBufferSize
parameter_list|(
name|int
name|size
parameter_list|)
throws|throws
name|SocketException
block|{
name|socket
operator|.
name|setReceiveBufferSize
argument_list|(
name|size
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|setReuseAddress
parameter_list|(
name|boolean
name|on
parameter_list|)
throws|throws
name|SocketException
block|{
name|socket
operator|.
name|setReuseAddress
argument_list|(
name|on
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|setSendBufferSize
parameter_list|(
name|int
name|size
parameter_list|)
throws|throws
name|SocketException
block|{
name|socket
operator|.
name|setSendBufferSize
argument_list|(
name|size
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|setSoLinger
parameter_list|(
name|boolean
name|on
parameter_list|,
name|int
name|linger
parameter_list|)
throws|throws
name|SocketException
block|{
name|socket
operator|.
name|setSoLinger
argument_list|(
name|on
argument_list|,
name|linger
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|setTcpNoDelay
parameter_list|(
name|boolean
name|on
parameter_list|)
throws|throws
name|SocketException
block|{
name|socket
operator|.
name|setTcpNoDelay
argument_list|(
name|on
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|setTrafficClass
parameter_list|(
name|int
name|tc
parameter_list|)
throws|throws
name|SocketException
block|{
name|socket
operator|.
name|setTrafficClass
argument_list|(
name|tc
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|setSoTimeout
parameter_list|(
name|int
name|i
parameter_list|)
throws|throws
name|SocketException
block|{
name|socket
operator|.
name|setSoTimeout
argument_list|(
name|i
argument_list|)
expr_stmt|;
block|}
specifier|public
name|String
name|toString
parameter_list|()
block|{
return|return
literal|"AIO Connection: "
operator|+
name|getLocalSocketAddress
argument_list|()
operator|+
literal|" -> "
operator|+
name|getRemoteSocketAddress
argument_list|()
return|;
block|}
block|}
end_class

end_unit

