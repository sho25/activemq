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
name|datagram
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
name|DatagramSocket
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
name|URI
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
name|packet
operator|.
name|sync
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
name|packet
operator|.
name|sync
operator|.
name|SyncChannelServer
import|;
end_import

begin_comment
comment|/**  * A TcpSynchChannelFactory creates {@see org.activeio.net.TcpSynchChannel}  * and {@see org.activeio.net.TcpSynchChannelServer} objects.  *   * @version $Revision$  */
end_comment

begin_class
specifier|public
class|class
name|DatagramSocketSyncChannelFactory
implements|implements
name|SyncChannelFactory
block|{
comment|/**      * Uses the {@param location}'s host and port to create a tcp connection to a remote host.      *       * @see org.activeio.SynchChannelFactory#openSyncChannel(java.net.URI)      */
specifier|public
name|SyncChannel
name|openSyncChannel
parameter_list|(
name|URI
name|location
parameter_list|)
throws|throws
name|IOException
block|{
name|DatagramSocket
name|socket
init|=
literal|null
decl_stmt|;
name|socket
operator|=
operator|new
name|DatagramSocket
argument_list|()
expr_stmt|;
if|if
condition|(
name|location
operator|!=
literal|null
condition|)
block|{
name|InetAddress
name|address
init|=
name|InetAddress
operator|.
name|getByName
argument_list|(
name|location
operator|.
name|getHost
argument_list|()
argument_list|)
decl_stmt|;
name|socket
operator|.
name|connect
argument_list|(
name|address
argument_list|,
name|location
operator|.
name|getPort
argument_list|()
argument_list|)
expr_stmt|;
block|}
return|return
name|createSyncChannel
argument_list|(
name|socket
argument_list|)
return|;
block|}
comment|/**      * Uses the {@param location}'s host and port to create a tcp connection to a remote host.      *       */
specifier|public
name|SyncChannel
name|openSyncChannel
parameter_list|(
name|URI
name|location
parameter_list|,
name|URI
name|localLocation
parameter_list|)
throws|throws
name|IOException
block|{
name|DatagramSocket
name|socket
init|=
literal|null
decl_stmt|;
name|InetAddress
name|address
init|=
name|InetAddress
operator|.
name|getByName
argument_list|(
name|localLocation
operator|.
name|getHost
argument_list|()
argument_list|)
decl_stmt|;
name|socket
operator|=
operator|new
name|DatagramSocket
argument_list|(
name|localLocation
operator|.
name|getPort
argument_list|()
argument_list|,
name|address
argument_list|)
expr_stmt|;
if|if
condition|(
name|location
operator|!=
literal|null
condition|)
block|{
name|address
operator|=
name|InetAddress
operator|.
name|getByName
argument_list|(
name|location
operator|.
name|getHost
argument_list|()
argument_list|)
expr_stmt|;
name|socket
operator|.
name|connect
argument_list|(
name|address
argument_list|,
name|location
operator|.
name|getPort
argument_list|()
argument_list|)
expr_stmt|;
block|}
return|return
name|createSyncChannel
argument_list|(
name|socket
argument_list|)
return|;
block|}
comment|/**      * @param socket      * @return      * @throws IOException      */
specifier|protected
name|SyncChannel
name|createSyncChannel
parameter_list|(
name|DatagramSocket
name|socket
parameter_list|)
throws|throws
name|IOException
block|{
return|return
operator|new
name|DatagramSocketSyncChannel
argument_list|(
name|socket
argument_list|)
return|;
block|}
comment|/**      * @throws IOException allways thrown.      * @see org.activeio.SynchChannelFactory#bindSynchChannel(java.net.URI)      */
specifier|public
name|SyncChannelServer
name|bindSyncChannel
parameter_list|(
name|URI
name|location
parameter_list|)
throws|throws
name|IOException
block|{
throw|throw
operator|new
name|IOException
argument_list|(
literal|"A SynchChannelServer is not available for this channel."
argument_list|)
throw|;
block|}
block|}
end_class

end_unit

