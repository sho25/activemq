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
name|ssl
package|;
end_package

begin_import
import|import
name|javax
operator|.
name|net
operator|.
name|ssl
operator|.
name|SSLServerSocketFactory
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|net
operator|.
name|ssl
operator|.
name|SSLSocketFactory
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
name|socket
operator|.
name|SocketSyncChannelFactory
import|;
end_import

begin_comment
comment|/**  * A SslSynchChannelFactory creates {@see org.activeio.net.TcpSynchChannel}  * and {@see org.activeio.net.TcpSynchChannelServer} objects that use SSL.  *   * @version $Revision$  */
end_comment

begin_class
specifier|public
class|class
name|SslSocketSyncChannelFactory
extends|extends
name|SocketSyncChannelFactory
block|{
specifier|public
name|SslSocketSyncChannelFactory
parameter_list|()
block|{
name|super
argument_list|(
name|SSLSocketFactory
operator|.
name|getDefault
argument_list|()
argument_list|,
name|SSLServerSocketFactory
operator|.
name|getDefault
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

