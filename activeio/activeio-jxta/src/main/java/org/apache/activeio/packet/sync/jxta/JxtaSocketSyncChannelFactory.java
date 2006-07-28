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
name|jxta
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
name|UnknownHostException
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|net
operator|.
name|ServerSocketFactory
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|net
operator|.
name|SocketFactory
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
name|SocketSyncChannelFactory
import|;
end_import

begin_import
import|import
name|org
operator|.
name|p2psockets
operator|.
name|P2PServerSocket
import|;
end_import

begin_import
import|import
name|org
operator|.
name|p2psockets
operator|.
name|P2PSocket
import|;
end_import

begin_comment
comment|/**  * A SslSynchChannelFactory creates {@see org.apache.activeio.net.TcpSynchChannel}  * and {@see org.apache.activeio.net.TcpSynchChannelServer} objects that use SSL.  *   * @version $Revision$  */
end_comment

begin_class
specifier|public
class|class
name|JxtaSocketSyncChannelFactory
extends|extends
name|SocketSyncChannelFactory
block|{
specifier|static
specifier|public
specifier|final
class|class
name|JxtaServerSocketFactory
extends|extends
name|ServerSocketFactory
block|{
specifier|private
specifier|static
name|JxtaServerSocketFactory
name|defaultJxtaServerSocketFactory
init|=
operator|new
name|JxtaServerSocketFactory
argument_list|()
decl_stmt|;
specifier|public
specifier|static
name|ServerSocketFactory
name|getDefault
parameter_list|()
block|{
return|return
name|defaultJxtaServerSocketFactory
return|;
block|}
specifier|private
name|JxtaServerSocketFactory
parameter_list|()
block|{}
specifier|public
name|ServerSocket
name|createServerSocket
parameter_list|(
name|int
name|localPort
parameter_list|)
throws|throws
name|IOException
block|{
return|return
operator|new
name|P2PServerSocket
argument_list|(
name|localPort
argument_list|)
return|;
block|}
specifier|public
name|ServerSocket
name|createServerSocket
parameter_list|(
name|int
name|localPort
parameter_list|,
name|int
name|backlog
parameter_list|)
throws|throws
name|IOException
block|{
return|return
operator|new
name|P2PServerSocket
argument_list|(
name|localPort
argument_list|,
name|backlog
argument_list|)
return|;
block|}
specifier|public
name|ServerSocket
name|createServerSocket
parameter_list|(
name|int
name|localPort
parameter_list|,
name|int
name|backlog
parameter_list|,
name|InetAddress
name|localHost
parameter_list|)
throws|throws
name|IOException
block|{
return|return
operator|new
name|P2PServerSocket
argument_list|(
name|localPort
argument_list|,
name|backlog
argument_list|,
name|localHost
argument_list|)
return|;
block|}
block|}
specifier|static
specifier|public
specifier|final
class|class
name|JxtaSocketFactory
extends|extends
name|SocketFactory
block|{
specifier|private
specifier|static
name|JxtaSocketFactory
name|defaultJxtaSocketFactory
init|=
operator|new
name|JxtaSocketFactory
argument_list|()
decl_stmt|;
specifier|public
specifier|static
name|SocketFactory
name|getDefault
parameter_list|()
block|{
return|return
name|defaultJxtaSocketFactory
return|;
block|}
specifier|private
name|JxtaSocketFactory
parameter_list|()
block|{}
specifier|public
name|Socket
name|createSocket
parameter_list|(
name|String
name|remoteHost
parameter_list|,
name|int
name|remotePort
parameter_list|)
throws|throws
name|IOException
throws|,
name|UnknownHostException
block|{
return|return
operator|new
name|P2PSocket
argument_list|(
name|remoteHost
argument_list|,
name|remotePort
argument_list|)
return|;
block|}
specifier|public
name|Socket
name|createSocket
parameter_list|(
name|String
name|remoteHost
parameter_list|,
name|int
name|remotePort
parameter_list|,
name|InetAddress
name|localHost
parameter_list|,
name|int
name|localPort
parameter_list|)
throws|throws
name|IOException
throws|,
name|UnknownHostException
block|{
return|return
operator|new
name|P2PSocket
argument_list|(
name|remoteHost
argument_list|,
name|remotePort
argument_list|,
name|localHost
argument_list|,
name|localPort
argument_list|)
return|;
block|}
specifier|public
name|Socket
name|createSocket
parameter_list|(
name|InetAddress
name|remoteHost
parameter_list|,
name|int
name|remotePort
parameter_list|)
throws|throws
name|IOException
block|{
return|return
operator|new
name|P2PSocket
argument_list|(
name|remoteHost
argument_list|,
name|remotePort
argument_list|)
return|;
block|}
specifier|public
name|Socket
name|createSocket
parameter_list|(
name|InetAddress
name|remoteHost
parameter_list|,
name|int
name|remotePort
parameter_list|,
name|InetAddress
name|localHost
parameter_list|,
name|int
name|localPort
parameter_list|)
throws|throws
name|IOException
block|{
return|return
operator|new
name|P2PSocket
argument_list|(
name|remoteHost
argument_list|,
name|remotePort
argument_list|,
name|localHost
argument_list|,
name|localPort
argument_list|)
return|;
block|}
block|}
specifier|public
name|JxtaSocketSyncChannelFactory
parameter_list|()
block|{
name|super
argument_list|(
name|JxtaSocketFactory
operator|.
name|getDefault
argument_list|()
argument_list|,
name|JxtaServerSocketFactory
operator|.
name|getDefault
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

