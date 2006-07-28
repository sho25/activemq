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
operator|.
name|async
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
name|InetSocketAddress
import|;
end_import

begin_import
import|import
name|java
operator|.
name|net
operator|.
name|URI
import|;
end_import

begin_import
import|import
name|java
operator|.
name|net
operator|.
name|URISyntaxException
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
name|ServerSocketChannel
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
name|apache
operator|.
name|activeio
operator|.
name|adapter
operator|.
name|SyncToAsyncChannelServer
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
name|AsyncChannelFactory
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
name|AsyncChannelServer
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
name|filter
operator|.
name|WriteBufferedAsyncChannel
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
name|util
operator|.
name|URISupport
import|;
end_import

begin_comment
comment|/**  * A TcpAsyncChannelFactory creates {@see org.apache.activeio.net.TcpAsyncChannel}  * and {@see org.apache.activeio.net.TcpAsyncChannelServer} objects.  *   * @version $Revision$  */
end_comment

begin_class
specifier|public
class|class
name|NIOAsyncChannelFactory
implements|implements
name|AsyncChannelFactory
block|{
specifier|protected
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
literal|"org.apache.activeio.net.nio.BufferSize"
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
specifier|protected
specifier|static
specifier|final
name|int
name|DEFAULT_BACKLOG
init|=
literal|500
decl_stmt|;
name|boolean
name|useDirectBuffers
init|=
literal|true
decl_stmt|;
specifier|private
specifier|final
name|boolean
name|createWriteBufferedChannels
decl_stmt|;
specifier|private
name|int
name|backlog
init|=
name|DEFAULT_BACKLOG
decl_stmt|;
specifier|public
name|NIOAsyncChannelFactory
parameter_list|()
block|{
name|this
argument_list|(
literal|true
argument_list|)
expr_stmt|;
block|}
specifier|public
name|NIOAsyncChannelFactory
parameter_list|(
name|boolean
name|createWriteBufferedChannels
parameter_list|)
block|{
name|this
operator|.
name|createWriteBufferedChannels
operator|=
name|createWriteBufferedChannels
expr_stmt|;
block|}
comment|/**      * Uses the {@param location}'s host and port to create a tcp connection to a remote host.      *       * @see org.apache.activeio.AsyncChannelFactory#openAsyncChannel(java.net.URI)      */
specifier|public
name|AsyncChannel
name|openAsyncChannel
parameter_list|(
name|URI
name|location
parameter_list|)
throws|throws
name|IOException
block|{
name|SocketChannel
name|channel
init|=
name|SocketChannel
operator|.
name|open
argument_list|()
decl_stmt|;
name|channel
operator|.
name|connect
argument_list|(
operator|new
name|InetSocketAddress
argument_list|(
name|location
operator|.
name|getHost
argument_list|()
argument_list|,
name|location
operator|.
name|getPort
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
return|return
name|createAsyncChannel
argument_list|(
name|channel
argument_list|)
return|;
block|}
comment|/**      * @param channel      * @return      * @throws IOException      */
specifier|protected
name|AsyncChannel
name|createAsyncChannel
parameter_list|(
name|SocketChannel
name|socketChannel
parameter_list|)
throws|throws
name|IOException
block|{
name|AsyncChannel
name|channel
init|=
operator|new
name|NIOAsyncChannel
argument_list|(
name|socketChannel
argument_list|,
name|useDirectBuffers
argument_list|)
decl_stmt|;
if|if
condition|(
name|createWriteBufferedChannels
condition|)
block|{
name|channel
operator|=
operator|new
name|WriteBufferedAsyncChannel
argument_list|(
name|channel
argument_list|,
name|ByteBufferPacket
operator|.
name|createDefaultBuffer
argument_list|(
name|useDirectBuffers
argument_list|)
argument_list|,
literal|false
argument_list|)
expr_stmt|;
block|}
return|return
name|channel
return|;
block|}
comment|/**      * Binds a server socket a the {@param location}'s port.       *       * @see org.apache.activeio.AsyncChannelFactory#bindAsyncChannel(java.net.URI)      */
specifier|public
name|AsyncChannelServer
name|bindAsyncChannel
parameter_list|(
name|URI
name|bindURI
parameter_list|)
throws|throws
name|IOException
block|{
name|String
name|host
init|=
name|bindURI
operator|.
name|getHost
argument_list|()
decl_stmt|;
name|InetSocketAddress
name|address
decl_stmt|;
if|if
condition|(
name|host
operator|==
literal|null
operator|||
name|host
operator|.
name|length
argument_list|()
operator|==
literal|0
operator|||
name|host
operator|.
name|equals
argument_list|(
literal|"localhost"
argument_list|)
operator|||
name|host
operator|.
name|equals
argument_list|(
literal|"0.0.0.0"
argument_list|)
operator|||
name|InetAddress
operator|.
name|getLocalHost
argument_list|()
operator|.
name|getHostName
argument_list|()
operator|.
name|equals
argument_list|(
name|host
argument_list|)
condition|)
block|{
name|address
operator|=
operator|new
name|InetSocketAddress
argument_list|(
name|bindURI
operator|.
name|getPort
argument_list|()
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|address
operator|=
operator|new
name|InetSocketAddress
argument_list|(
name|bindURI
operator|.
name|getHost
argument_list|()
argument_list|,
name|bindURI
operator|.
name|getPort
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|ServerSocketChannel
name|serverSocketChannel
init|=
name|ServerSocketChannel
operator|.
name|open
argument_list|()
decl_stmt|;
name|serverSocketChannel
operator|.
name|socket
argument_list|()
operator|.
name|bind
argument_list|(
name|address
argument_list|,
name|backlog
argument_list|)
expr_stmt|;
name|URI
name|connectURI
init|=
name|bindURI
decl_stmt|;
try|try
block|{
comment|//            connectURI = URISupport.changeHost(connectURI, InetAddress.getLocalHost().getHostName());
name|connectURI
operator|=
name|URISupport
operator|.
name|changePort
argument_list|(
name|connectURI
argument_list|,
name|serverSocketChannel
operator|.
name|socket
argument_list|()
operator|.
name|getLocalPort
argument_list|()
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|URISyntaxException
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
literal|"Could not build connect URI: "
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
comment|// We won't use non blocking NIO for the server since you only need 1 thread for him anyways.
comment|// Just resuing the SocketChannelSynchChannelServer.
return|return
name|SyncToAsyncChannelServer
operator|.
name|adapt
argument_list|(
operator|new
name|NIOAsyncChannelServer
argument_list|(
name|serverSocketChannel
argument_list|,
name|bindURI
argument_list|,
name|connectURI
argument_list|,
name|createWriteBufferedChannels
argument_list|,
name|useDirectBuffers
argument_list|)
argument_list|)
return|;
block|}
comment|/**      * @return Returns the backlog.      */
specifier|public
name|int
name|getBacklog
parameter_list|()
block|{
return|return
name|backlog
return|;
block|}
comment|/**      * @param backlog      *            The backlog to set.      */
specifier|public
name|void
name|setBacklog
parameter_list|(
name|int
name|backlog
parameter_list|)
block|{
name|this
operator|.
name|backlog
operator|=
name|backlog
expr_stmt|;
block|}
block|}
end_class

end_unit

