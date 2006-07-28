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
name|InetSocketAddress
import|;
end_import

begin_import
import|import
name|java
operator|.
name|net
operator|.
name|ServerSocket
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
name|net
operator|.
name|URI
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
name|org
operator|.
name|apache
operator|.
name|activeio
operator|.
name|Channel
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
name|sync
operator|.
name|SyncChannel
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
name|sync
operator|.
name|SyncChannelServer
import|;
end_import

begin_comment
comment|/**  */
end_comment

begin_class
specifier|public
class|class
name|SyncChannelServerToServerSocket
extends|extends
name|ServerSocket
block|{
specifier|private
specifier|final
name|SyncChannelServer
name|channelServer
decl_stmt|;
specifier|private
name|long
name|timeout
init|=
name|Channel
operator|.
name|WAIT_FOREVER_TIMEOUT
decl_stmt|;
name|boolean
name|closed
decl_stmt|;
specifier|private
name|InetAddress
name|inetAddress
decl_stmt|;
specifier|private
name|int
name|localPort
decl_stmt|;
specifier|private
name|SocketAddress
name|localSocketAddress
decl_stmt|;
specifier|private
name|int
name|receiveBufferSize
decl_stmt|;
specifier|private
name|boolean
name|reuseAddress
decl_stmt|;
comment|/**      * @throws IOException      */
specifier|public
name|SyncChannelServerToServerSocket
parameter_list|(
name|SyncChannelServer
name|channelServer
parameter_list|)
throws|throws
name|IOException
block|{
name|this
operator|.
name|channelServer
operator|=
name|channelServer
expr_stmt|;
name|URI
name|connectURI
init|=
name|channelServer
operator|.
name|getConnectURI
argument_list|()
decl_stmt|;
name|localPort
operator|=
name|connectURI
operator|.
name|getPort
argument_list|()
expr_stmt|;
name|inetAddress
operator|=
name|InetAddress
operator|.
name|getByName
argument_list|(
name|connectURI
operator|.
name|getHost
argument_list|()
argument_list|)
expr_stmt|;
name|localSocketAddress
operator|=
operator|new
name|InetSocketAddress
argument_list|(
name|inetAddress
argument_list|,
name|localPort
argument_list|)
expr_stmt|;
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
if|if
condition|(
name|timeout
operator|<=
literal|0
condition|)
name|this
operator|.
name|timeout
operator|=
name|Channel
operator|.
name|WAIT_FOREVER_TIMEOUT
expr_stmt|;
else|else
name|this
operator|.
name|timeout
operator|=
name|timeout
expr_stmt|;
block|}
specifier|public
specifier|synchronized
name|int
name|getSoTimeout
parameter_list|()
throws|throws
name|IOException
block|{
if|if
condition|(
name|timeout
operator|==
name|Channel
operator|.
name|WAIT_FOREVER_TIMEOUT
condition|)
return|return
literal|0
return|;
return|return
operator|(
name|int
operator|)
name|timeout
return|;
block|}
specifier|public
name|Socket
name|accept
parameter_list|()
throws|throws
name|IOException
block|{
name|Channel
name|channel
init|=
name|channelServer
operator|.
name|accept
argument_list|(
name|timeout
argument_list|)
decl_stmt|;
if|if
condition|(
name|channel
operator|==
literal|null
condition|)
throw|throw
operator|new
name|InterruptedIOException
argument_list|()
throw|;
name|SyncChannel
name|syncChannel
init|=
name|AsyncToSyncChannel
operator|.
name|adapt
argument_list|(
name|channel
argument_list|)
decl_stmt|;
name|syncChannel
operator|.
name|start
argument_list|()
expr_stmt|;
return|return
operator|new
name|SyncChannelToSocket
argument_list|(
name|syncChannel
argument_list|)
return|;
block|}
specifier|public
name|void
name|bind
parameter_list|(
name|SocketAddress
name|endpoint
parameter_list|,
name|int
name|backlog
parameter_list|)
throws|throws
name|IOException
block|{
if|if
condition|(
name|isClosed
argument_list|()
condition|)
throw|throw
operator|new
name|SocketException
argument_list|(
literal|"Socket is closed"
argument_list|)
throw|;
throw|throw
operator|new
name|SocketException
argument_list|(
literal|"Already bound"
argument_list|)
throw|;
block|}
specifier|public
name|void
name|bind
parameter_list|(
name|SocketAddress
name|endpoint
parameter_list|)
throws|throws
name|IOException
block|{
if|if
condition|(
name|isClosed
argument_list|()
condition|)
throw|throw
operator|new
name|SocketException
argument_list|(
literal|"Socket is closed"
argument_list|)
throw|;
throw|throw
operator|new
name|SocketException
argument_list|(
literal|"Already bound"
argument_list|)
throw|;
block|}
specifier|public
name|ServerSocketChannel
name|getChannel
parameter_list|()
block|{
return|return
literal|null
return|;
block|}
specifier|public
name|InetAddress
name|getInetAddress
parameter_list|()
block|{
return|return
name|inetAddress
return|;
block|}
specifier|public
name|int
name|getLocalPort
parameter_list|()
block|{
return|return
name|localPort
return|;
block|}
specifier|public
name|SocketAddress
name|getLocalSocketAddress
parameter_list|()
block|{
return|return
name|localSocketAddress
return|;
block|}
specifier|public
specifier|synchronized
name|int
name|getReceiveBufferSize
parameter_list|()
throws|throws
name|SocketException
block|{
return|return
name|receiveBufferSize
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
name|reuseAddress
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
name|setPerformancePreferences
parameter_list|(
name|int
name|connectionTime
parameter_list|,
name|int
name|latency
parameter_list|,
name|int
name|bandwidth
parameter_list|)
block|{     }
specifier|public
specifier|synchronized
name|void
name|setReceiveBufferSize
parameter_list|(
name|int
name|size
parameter_list|)
throws|throws
name|SocketException
block|{
name|this
operator|.
name|receiveBufferSize
operator|=
name|size
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
name|reuseAddress
operator|=
name|on
expr_stmt|;
block|}
block|}
end_class

end_unit

