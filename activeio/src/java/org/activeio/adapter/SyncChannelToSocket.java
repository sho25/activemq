begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  *  * Copyright 2004 Hiram Chirino  *  *  Licensed under the Apache License, Version 2.0 (the "License");  *  you may not use this file except in compliance with the License.  *  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  *  Unless required by applicable law or agreed to in writing, software  *  distributed under the License is distributed on an "AS IS" BASIS,  *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  *  See the License for the specific language governing permissions and  *  limitations under the License.  */
end_comment

begin_package
package|package
name|org
operator|.
name|activeio
operator|.
name|adapter
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
name|io
operator|.
name|OutputStream
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
name|ByteArrayPacket
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
name|Packet
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
name|sync
operator|.
name|SyncChannel
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
comment|/**  * Provides a {@see java.net.Socket} interface to a {@see org.activeio.SynchChannel}.  *   * If the {@see org.activeio.SynchChannel} being adapted can not be   * {@see org.activeio.Channel#narrow(Class)}ed to a {@see org.activeio.net.SocketMetadata}   * then all methods accessing socket metadata will throw a {@see java.net.SocketException}.  *    */
end_comment

begin_class
specifier|public
class|class
name|SyncChannelToSocket
extends|extends
name|Socket
block|{
specifier|private
specifier|final
name|SyncChannel
name|channel
decl_stmt|;
specifier|private
specifier|final
name|SyncChannelToInputStream
name|inputStream
decl_stmt|;
specifier|private
specifier|final
name|SyncChannelToOutputStream
name|outputStream
decl_stmt|;
specifier|private
specifier|final
name|SocketMetadata
name|socketMetadata
decl_stmt|;
specifier|private
specifier|final
name|Packet
name|urgentPackget
init|=
operator|new
name|ByteArrayPacket
argument_list|(
operator|new
name|byte
index|[
literal|1
index|]
argument_list|)
decl_stmt|;
name|boolean
name|closed
decl_stmt|;
specifier|public
name|SyncChannelToSocket
parameter_list|(
name|SyncChannel
name|channel
parameter_list|)
block|{
name|this
argument_list|(
name|channel
argument_list|,
operator|(
name|SocketMetadata
operator|)
name|channel
operator|.
name|getAdapter
argument_list|(
name|SocketMetadata
operator|.
name|class
argument_list|)
argument_list|)
expr_stmt|;
block|}
specifier|public
name|SyncChannelToSocket
parameter_list|(
name|SyncChannel
name|channel
parameter_list|,
name|SocketMetadata
name|socketMetadata
parameter_list|)
block|{
name|this
operator|.
name|channel
operator|=
name|channel
expr_stmt|;
name|this
operator|.
name|socketMetadata
operator|=
name|socketMetadata
expr_stmt|;
name|this
operator|.
name|inputStream
operator|=
operator|new
name|SyncChannelToInputStream
argument_list|(
name|channel
argument_list|)
expr_stmt|;
name|this
operator|.
name|outputStream
operator|=
operator|new
name|SyncChannelToOutputStream
argument_list|(
name|channel
argument_list|)
expr_stmt|;
block|}
specifier|public
name|boolean
name|isConnected
parameter_list|()
block|{
return|return
literal|true
return|;
block|}
specifier|public
name|boolean
name|isBound
parameter_list|()
block|{
return|return
literal|true
return|;
block|}
specifier|public
name|boolean
name|isClosed
parameter_list|()
block|{
return|return
name|closed
return|;
block|}
specifier|public
name|void
name|bind
parameter_list|(
name|SocketAddress
name|bindpoint
parameter_list|)
throws|throws
name|IOException
block|{
throw|throw
operator|new
name|IOException
argument_list|(
literal|"Not supported"
argument_list|)
throw|;
block|}
specifier|public
specifier|synchronized
name|void
name|close
parameter_list|()
throws|throws
name|IOException
block|{
if|if
condition|(
name|closed
condition|)
return|return;
name|closed
operator|=
literal|true
expr_stmt|;
name|inputStream
operator|.
name|close
argument_list|()
expr_stmt|;
name|outputStream
operator|.
name|close
argument_list|()
expr_stmt|;
name|channel
operator|.
name|stop
argument_list|()
expr_stmt|;
block|}
specifier|public
name|void
name|connect
parameter_list|(
name|SocketAddress
name|endpoint
parameter_list|)
throws|throws
name|IOException
block|{
throw|throw
operator|new
name|IOException
argument_list|(
literal|"Not supported"
argument_list|)
throw|;
block|}
specifier|public
name|void
name|connect
parameter_list|(
name|SocketAddress
name|endpoint
parameter_list|,
name|int
name|timeout
parameter_list|)
throws|throws
name|IOException
block|{
throw|throw
operator|new
name|IOException
argument_list|(
literal|"Not supported"
argument_list|)
throw|;
block|}
specifier|public
name|SocketChannel
name|getChannel
parameter_list|()
block|{
return|return
literal|null
return|;
block|}
specifier|public
name|InputStream
name|getInputStream
parameter_list|()
throws|throws
name|IOException
block|{
return|return
name|inputStream
return|;
block|}
specifier|public
name|OutputStream
name|getOutputStream
parameter_list|()
throws|throws
name|IOException
block|{
return|return
name|outputStream
return|;
block|}
specifier|public
name|boolean
name|isInputShutdown
parameter_list|()
block|{
return|return
name|inputStream
operator|.
name|isClosed
argument_list|()
return|;
block|}
specifier|public
name|boolean
name|isOutputShutdown
parameter_list|()
block|{
return|return
name|outputStream
operator|.
name|isClosed
argument_list|()
return|;
block|}
specifier|public
name|void
name|sendUrgentData
parameter_list|(
name|int
name|data
parameter_list|)
throws|throws
name|IOException
block|{
name|urgentPackget
operator|.
name|clear
argument_list|()
expr_stmt|;
name|urgentPackget
operator|.
name|write
argument_list|(
name|data
argument_list|)
expr_stmt|;
name|urgentPackget
operator|.
name|flip
argument_list|()
expr_stmt|;
name|channel
operator|.
name|write
argument_list|(
name|urgentPackget
argument_list|)
expr_stmt|;
block|}
specifier|public
name|int
name|getSoTimeout
parameter_list|()
throws|throws
name|SocketException
block|{
return|return
operator|(
name|int
operator|)
name|inputStream
operator|.
name|getTimeout
argument_list|()
return|;
block|}
specifier|public
specifier|synchronized
name|void
name|setSoTimeout
parameter_list|(
name|int
name|timeout
parameter_list|)
throws|throws
name|SocketException
block|{
name|inputStream
operator|.
name|setTimeout
argument_list|(
name|timeout
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|shutdownOutput
parameter_list|()
throws|throws
name|IOException
block|{
name|outputStream
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
specifier|public
name|void
name|shutdownInput
parameter_list|()
throws|throws
name|IOException
block|{
name|inputStream
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
specifier|protected
name|SocketMetadata
name|getSocketMetadata
parameter_list|()
throws|throws
name|SocketException
block|{
if|if
condition|(
name|socketMetadata
operator|==
literal|null
condition|)
throw|throw
operator|new
name|SocketException
argument_list|(
literal|"No socket metadata available."
argument_list|)
throw|;
return|return
name|socketMetadata
return|;
block|}
specifier|public
name|InetAddress
name|getInetAddress
parameter_list|()
block|{
if|if
condition|(
name|socketMetadata
operator|==
literal|null
condition|)
return|return
literal|null
return|;
return|return
name|socketMetadata
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
name|getSocketMetadata
argument_list|()
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
if|if
condition|(
name|socketMetadata
operator|==
literal|null
condition|)
return|return
literal|null
return|;
return|return
name|socketMetadata
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
if|if
condition|(
name|socketMetadata
operator|==
literal|null
condition|)
return|return
operator|-
literal|1
return|;
return|return
name|socketMetadata
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
if|if
condition|(
name|socketMetadata
operator|==
literal|null
condition|)
return|return
literal|null
return|;
return|return
name|socketMetadata
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
name|getSocketMetadata
argument_list|()
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
if|if
condition|(
name|socketMetadata
operator|==
literal|null
condition|)
return|return
operator|-
literal|1
return|;
return|return
name|socketMetadata
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
name|getSocketMetadata
argument_list|()
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
if|if
condition|(
name|socketMetadata
operator|==
literal|null
condition|)
return|return
literal|null
return|;
return|return
name|socketMetadata
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
name|getSocketMetadata
argument_list|()
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
name|getSocketMetadata
argument_list|()
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
name|getSocketMetadata
argument_list|()
operator|.
name|getSoLinger
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
name|getSocketMetadata
argument_list|()
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
name|getSocketMetadata
argument_list|()
operator|.
name|getTrafficClass
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
name|getSocketMetadata
argument_list|()
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
name|getSocketMetadata
argument_list|()
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
name|getSocketMetadata
argument_list|()
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
name|getSocketMetadata
argument_list|()
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
name|getSocketMetadata
argument_list|()
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
name|getSocketMetadata
argument_list|()
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
name|getSocketMetadata
argument_list|()
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
name|getSocketMetadata
argument_list|()
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

