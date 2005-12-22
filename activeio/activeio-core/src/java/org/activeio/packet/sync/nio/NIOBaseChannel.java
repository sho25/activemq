begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  *  * Copyright 2004 The Apache Software Foundation  *  * Licensed under the Apache License, Version 2.0 (the "License");  * you may not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  * http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
package|package
name|org
operator|.
name|activeio
operator|.
name|packet
operator|.
name|sync
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
name|java
operator|.
name|nio
operator|.
name|channels
operator|.
name|SocketChannel
import|;
end_import

begin_import
import|import
name|org
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

begin_comment
comment|/**  * Base class for the Async and Sync implementations of NIO channels.  *   * @version $Revision$  */
end_comment

begin_class
specifier|public
class|class
name|NIOBaseChannel
implements|implements
name|SocketMetadata
block|{
specifier|protected
specifier|final
name|SocketChannel
name|socketChannel
decl_stmt|;
specifier|protected
specifier|final
name|Socket
name|socket
decl_stmt|;
specifier|private
specifier|final
name|boolean
name|useDirect
decl_stmt|;
specifier|private
name|int
name|curentSoTimeout
decl_stmt|;
specifier|private
name|boolean
name|disposed
decl_stmt|;
specifier|private
specifier|final
name|String
name|name
decl_stmt|;
specifier|protected
name|NIOBaseChannel
parameter_list|(
name|SocketChannel
name|socketChannel
parameter_list|,
name|boolean
name|useDirect
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
name|useDirect
operator|=
name|useDirect
expr_stmt|;
name|this
operator|.
name|socket
operator|=
name|this
operator|.
name|socketChannel
operator|.
name|socket
argument_list|()
expr_stmt|;
if|if
condition|(
name|useDirect
condition|)
block|{
name|socket
operator|.
name|setSendBufferSize
argument_list|(
name|ByteBufferPacket
operator|.
name|DEFAULT_DIRECT_BUFFER_SIZE
argument_list|)
expr_stmt|;
name|socket
operator|.
name|setReceiveBufferSize
argument_list|(
name|ByteBufferPacket
operator|.
name|DEFAULT_DIRECT_BUFFER_SIZE
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|socket
operator|.
name|setSendBufferSize
argument_list|(
name|ByteBufferPacket
operator|.
name|DEFAULT_BUFFER_SIZE
argument_list|)
expr_stmt|;
name|socket
operator|.
name|setReceiveBufferSize
argument_list|(
name|ByteBufferPacket
operator|.
name|DEFAULT_BUFFER_SIZE
argument_list|)
expr_stmt|;
block|}
name|this
operator|.
name|name
operator|=
literal|"NIO Socket Connection: "
operator|+
name|getLocalSocketAddress
argument_list|()
operator|+
literal|" -> "
operator|+
name|getRemoteSocketAddress
argument_list|()
expr_stmt|;
block|}
specifier|protected
name|ByteBuffer
name|allocateBuffer
parameter_list|()
block|{
if|if
condition|(
name|useDirect
condition|)
block|{
return|return
name|ByteBuffer
operator|.
name|allocateDirect
argument_list|(
name|ByteBufferPacket
operator|.
name|DEFAULT_DIRECT_BUFFER_SIZE
argument_list|)
return|;
block|}
else|else
block|{
return|return
name|ByteBuffer
operator|.
name|allocate
argument_list|(
name|ByteBufferPacket
operator|.
name|DEFAULT_BUFFER_SIZE
argument_list|)
return|;
block|}
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
if|if
condition|(
name|curentSoTimeout
operator|!=
name|i
condition|)
block|{
name|socket
operator|.
name|setSoTimeout
argument_list|(
name|i
argument_list|)
expr_stmt|;
name|curentSoTimeout
operator|=
name|i
expr_stmt|;
block|}
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
name|String
name|toString
parameter_list|()
block|{
return|return
name|name
return|;
block|}
specifier|public
name|void
name|dispose
parameter_list|()
block|{
if|if
condition|(
name|disposed
condition|)
return|return;
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
name|ignore
parameter_list|)
block|{         }
name|disposed
operator|=
literal|true
expr_stmt|;
block|}
comment|/**      * @see org.activeio.Channel#flush()      */
specifier|public
name|void
name|flush
parameter_list|()
throws|throws
name|IOException
block|{     }
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
block|}
end_class

end_unit

