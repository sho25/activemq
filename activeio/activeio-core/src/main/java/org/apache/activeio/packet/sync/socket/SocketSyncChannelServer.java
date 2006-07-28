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
name|sync
operator|.
name|socket
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
name|ServerSocket
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
name|SyncChannelServer
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
name|SocketStreamChannelServer
import|;
end_import

begin_comment
comment|/**  * A SynchChannelServer that creates  * {@see org.apache.activeio.net.TcpSynchChannel}objects from accepted  * TCP socket connections.  *   * @version $Revision$  */
end_comment

begin_class
specifier|public
class|class
name|SocketSyncChannelServer
implements|implements
name|SyncChannelServer
block|{
specifier|private
specifier|final
name|SocketStreamChannelServer
name|server
decl_stmt|;
specifier|public
name|SocketSyncChannelServer
parameter_list|(
name|SocketStreamChannelServer
name|server
parameter_list|)
block|{
name|this
operator|.
name|server
operator|=
name|server
expr_stmt|;
block|}
specifier|public
name|SocketSyncChannelServer
parameter_list|(
name|ServerSocket
name|socket
parameter_list|,
name|URI
name|bindURI
parameter_list|,
name|URI
name|connectURI
parameter_list|)
block|{
name|this
argument_list|(
operator|new
name|SocketStreamChannelServer
argument_list|(
name|socket
argument_list|,
name|bindURI
argument_list|,
name|connectURI
argument_list|)
argument_list|)
expr_stmt|;
block|}
specifier|public
name|Channel
name|accept
parameter_list|(
name|long
name|timeout
parameter_list|)
throws|throws
name|IOException
block|{
name|Channel
name|channel
init|=
name|server
operator|.
name|accept
argument_list|(
name|timeout
argument_list|)
decl_stmt|;
if|if
condition|(
name|channel
operator|!=
literal|null
condition|)
block|{
name|channel
operator|=
name|createChannel
argument_list|(
operator|(
name|SocketStreamChannel
operator|)
name|channel
argument_list|)
expr_stmt|;
block|}
return|return
name|channel
return|;
block|}
specifier|protected
name|Channel
name|createChannel
parameter_list|(
name|SocketStreamChannel
name|channel
parameter_list|)
throws|throws
name|IOException
block|{
return|return
operator|new
name|SocketSyncChannel
argument_list|(
name|channel
argument_list|)
return|;
block|}
comment|/**      * @see org.apache.activeio.Disposable#dispose()      */
specifier|public
name|void
name|dispose
parameter_list|()
block|{
name|server
operator|.
name|dispose
argument_list|()
expr_stmt|;
block|}
comment|/**      * @return Returns the bindURI.      */
specifier|public
name|URI
name|getBindURI
parameter_list|()
block|{
return|return
name|server
operator|.
name|getBindURI
argument_list|()
return|;
block|}
comment|/**      * @return Returns the connectURI.      */
specifier|public
name|URI
name|getConnectURI
parameter_list|()
block|{
return|return
name|server
operator|.
name|getConnectURI
argument_list|()
return|;
block|}
specifier|public
name|void
name|start
parameter_list|()
throws|throws
name|IOException
block|{
name|server
operator|.
name|start
argument_list|()
expr_stmt|;
block|}
specifier|public
name|void
name|stop
parameter_list|()
throws|throws
name|IOException
block|{
name|server
operator|.
name|stop
argument_list|()
expr_stmt|;
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
name|server
operator|.
name|getAdapter
argument_list|(
name|target
argument_list|)
return|;
block|}
specifier|public
name|String
name|toString
parameter_list|()
block|{
return|return
name|server
operator|.
name|toString
argument_list|()
return|;
block|}
block|}
end_class

end_unit

