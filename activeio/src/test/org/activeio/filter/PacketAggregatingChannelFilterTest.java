begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**   *   * Copyright 2004 Hiram Chirino  *   * Licensed under the Apache License, Version 2.0 (the "License");   * you may not use this file except in compliance with the License.   * You may obtain a copy of the License at   *   * http://www.apache.org/licenses/LICENSE-2.0  *   * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,   * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.   * See the License for the specific language governing permissions and   * limitations under the License.   *   **/
end_comment

begin_package
package|package
name|org
operator|.
name|activeio
operator|.
name|filter
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
name|net
operator|.
name|URISyntaxException
import|;
end_import

begin_import
import|import
name|org
operator|.
name|activeio
operator|.
name|AcceptListener
import|;
end_import

begin_import
import|import
name|org
operator|.
name|activeio
operator|.
name|AsyncChannelServer
import|;
end_import

begin_import
import|import
name|org
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
name|activeio
operator|.
name|ChannelRequestTestSupport
import|;
end_import

begin_import
import|import
name|org
operator|.
name|activeio
operator|.
name|ChannelServer
import|;
end_import

begin_import
import|import
name|org
operator|.
name|activeio
operator|.
name|RequestChannel
import|;
end_import

begin_import
import|import
name|org
operator|.
name|activeio
operator|.
name|RequestListener
import|;
end_import

begin_import
import|import
name|org
operator|.
name|activeio
operator|.
name|SyncChannelFactory
import|;
end_import

begin_import
import|import
name|org
operator|.
name|activeio
operator|.
name|adapter
operator|.
name|AsyncChannelToClientRequestChannel
import|;
end_import

begin_import
import|import
name|org
operator|.
name|activeio
operator|.
name|adapter
operator|.
name|AsyncChannelToServerRequestChannel
import|;
end_import

begin_import
import|import
name|org
operator|.
name|activeio
operator|.
name|adapter
operator|.
name|SyncToAsyncChannel
import|;
end_import

begin_import
import|import
name|org
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
name|activeio
operator|.
name|net
operator|.
name|SocketMetadata
import|;
end_import

begin_import
import|import
name|org
operator|.
name|activeio
operator|.
name|net
operator|.
name|SocketSyncChannelFactory
import|;
end_import

begin_comment
comment|/**  */
end_comment

begin_class
specifier|public
class|class
name|PacketAggregatingChannelFilterTest
extends|extends
name|ChannelRequestTestSupport
block|{
specifier|private
name|URI
name|serverURI
decl_stmt|;
specifier|protected
name|ChannelServer
name|createChannelServer
parameter_list|(
specifier|final
name|RequestListener
name|requestListener
parameter_list|)
throws|throws
name|IOException
throws|,
name|URISyntaxException
block|{
name|SyncChannelFactory
name|factory
init|=
operator|new
name|SocketSyncChannelFactory
argument_list|()
decl_stmt|;
name|AsyncChannelServer
name|server
init|=
operator|new
name|SyncToAsyncChannelServer
argument_list|(
name|factory
operator|.
name|bindSyncChannel
argument_list|(
operator|new
name|URI
argument_list|(
literal|"tcp://localhost:0"
argument_list|)
argument_list|)
argument_list|)
decl_stmt|;
name|server
operator|.
name|setAcceptListener
argument_list|(
operator|new
name|AcceptListener
argument_list|()
block|{
specifier|public
name|void
name|onAccept
parameter_list|(
name|Channel
name|channel
parameter_list|)
block|{
name|RequestChannel
name|requestChannel
init|=
literal|null
decl_stmt|;
try|try
block|{
operator|(
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
operator|)
operator|.
name|setTcpNoDelay
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|requestChannel
operator|=
operator|new
name|AsyncChannelToServerRequestChannel
argument_list|(
operator|new
name|PacketAggregatingAsyncChannel
argument_list|(
name|SyncToAsyncChannel
operator|.
name|adapt
argument_list|(
name|channel
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|requestChannel
operator|.
name|setRequestListener
argument_list|(
name|requestListener
argument_list|)
expr_stmt|;
name|requestChannel
operator|.
name|start
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
if|if
condition|(
name|requestChannel
operator|!=
literal|null
condition|)
name|requestChannel
operator|.
name|dispose
argument_list|()
expr_stmt|;
else|else
name|channel
operator|.
name|dispose
argument_list|()
expr_stmt|;
block|}
block|}
specifier|public
name|void
name|onAcceptError
parameter_list|(
name|IOException
name|error
parameter_list|)
block|{
name|error
operator|.
name|printStackTrace
argument_list|()
expr_stmt|;
block|}
block|}
argument_list|)
expr_stmt|;
name|serverURI
operator|=
name|server
operator|.
name|getConnectURI
argument_list|()
expr_stmt|;
return|return
name|server
return|;
block|}
comment|/**      * @return      * @throws IOException      */
specifier|protected
name|RequestChannel
name|createClientRequestChannel
parameter_list|()
throws|throws
name|IOException
block|{
name|SyncChannelFactory
name|factory
init|=
operator|new
name|SocketSyncChannelFactory
argument_list|()
decl_stmt|;
name|PacketAggregatingSyncChannel
name|channel
init|=
operator|new
name|PacketAggregatingSyncChannel
argument_list|(
name|factory
operator|.
name|openSyncChannel
argument_list|(
name|serverURI
argument_list|)
argument_list|)
decl_stmt|;
operator|(
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
operator|)
operator|.
name|setTcpNoDelay
argument_list|(
literal|true
argument_list|)
expr_stmt|;
return|return
operator|new
name|AsyncChannelToClientRequestChannel
argument_list|(
name|channel
argument_list|)
return|;
block|}
block|}
end_class

end_unit

