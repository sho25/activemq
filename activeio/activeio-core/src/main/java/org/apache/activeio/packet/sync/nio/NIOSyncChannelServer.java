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
name|filter
operator|.
name|WriteBufferedSyncChannel
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
name|socket
operator|.
name|SocketSyncChannelServer
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
name|SocketStreamChannel
import|;
end_import

begin_comment
comment|/**  * A SynchChannelServer that creates  * {@see org.apache.activeio.net.TcpSynchChannel}objects from accepted  * tcp socket connections.  *   * @version $Revision$  */
end_comment

begin_class
specifier|public
class|class
name|NIOSyncChannelServer
extends|extends
name|SocketSyncChannelServer
block|{
specifier|private
specifier|final
name|boolean
name|createWriteBufferedChannels
decl_stmt|;
specifier|private
specifier|final
name|boolean
name|useDirectBuffers
decl_stmt|;
specifier|public
name|NIOSyncChannelServer
parameter_list|(
name|ServerSocketChannel
name|socketChannel
parameter_list|,
name|URI
name|bindURI
parameter_list|,
name|URI
name|connectURI
parameter_list|,
name|boolean
name|createWriteBufferedChannels
parameter_list|,
name|boolean
name|useDirectBuffers
parameter_list|)
block|{
name|super
argument_list|(
name|socketChannel
operator|.
name|socket
argument_list|()
argument_list|,
name|bindURI
argument_list|,
name|connectURI
argument_list|)
expr_stmt|;
name|this
operator|.
name|createWriteBufferedChannels
operator|=
name|createWriteBufferedChannels
expr_stmt|;
name|this
operator|.
name|useDirectBuffers
operator|=
name|useDirectBuffers
expr_stmt|;
block|}
specifier|protected
name|Channel
name|createChannel
parameter_list|(
name|SocketStreamChannel
name|c
parameter_list|)
throws|throws
name|IOException
block|{
name|SyncChannel
name|channel
init|=
operator|new
name|NIOSyncChannel
argument_list|(
name|c
operator|.
name|getSocket
argument_list|()
operator|.
name|getChannel
argument_list|()
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
name|WriteBufferedSyncChannel
argument_list|(
name|channel
argument_list|,
name|ByteBufferPacket
operator|.
name|createDefaultBuffer
argument_list|(
name|useDirectBuffers
argument_list|)
argument_list|)
expr_stmt|;
block|}
return|return
name|channel
return|;
block|}
block|}
end_class

end_unit

