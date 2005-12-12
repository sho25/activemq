begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  *   * Copyright 2004 Hiram Chirino  *   * Licensed under the Apache License, Version 2.0 (the "License");   * you may not use this file except in compliance with the License.   * You may obtain a copy of the License at   *   * http://www.apache.org/licenses/LICENSE-2.0  *   * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,   * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.   * See the License for the specific language governing permissions and   * limitations under the License.   *   **/
end_comment

begin_package
package|package
name|org
operator|.
name|activeio
operator|.
name|net
package|;
end_package

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

begin_comment
comment|/**  */
end_comment

begin_interface
specifier|public
interface|interface
name|SocketMetadata
block|{
specifier|public
name|InetAddress
name|getInetAddress
parameter_list|()
function_decl|;
specifier|public
name|boolean
name|getKeepAlive
parameter_list|()
throws|throws
name|SocketException
function_decl|;
specifier|public
name|InetAddress
name|getLocalAddress
parameter_list|()
function_decl|;
specifier|public
name|int
name|getLocalPort
parameter_list|()
function_decl|;
specifier|public
name|SocketAddress
name|getLocalSocketAddress
parameter_list|()
function_decl|;
specifier|public
name|boolean
name|getOOBInline
parameter_list|()
throws|throws
name|SocketException
function_decl|;
specifier|public
name|int
name|getPort
parameter_list|()
function_decl|;
specifier|public
name|int
name|getReceiveBufferSize
parameter_list|()
throws|throws
name|SocketException
function_decl|;
specifier|public
name|SocketAddress
name|getRemoteSocketAddress
parameter_list|()
function_decl|;
specifier|public
name|boolean
name|getReuseAddress
parameter_list|()
throws|throws
name|SocketException
function_decl|;
specifier|public
name|int
name|getSendBufferSize
parameter_list|()
throws|throws
name|SocketException
function_decl|;
specifier|public
name|int
name|getSoLinger
parameter_list|()
throws|throws
name|SocketException
function_decl|;
specifier|public
name|int
name|getSoTimeout
parameter_list|()
throws|throws
name|SocketException
function_decl|;
specifier|public
name|boolean
name|getTcpNoDelay
parameter_list|()
throws|throws
name|SocketException
function_decl|;
specifier|public
name|int
name|getTrafficClass
parameter_list|()
throws|throws
name|SocketException
function_decl|;
specifier|public
name|boolean
name|isBound
parameter_list|()
function_decl|;
specifier|public
name|boolean
name|isClosed
parameter_list|()
function_decl|;
specifier|public
name|boolean
name|isConnected
parameter_list|()
function_decl|;
specifier|public
name|void
name|setKeepAlive
parameter_list|(
name|boolean
name|on
parameter_list|)
throws|throws
name|SocketException
function_decl|;
specifier|public
name|void
name|setOOBInline
parameter_list|(
name|boolean
name|on
parameter_list|)
throws|throws
name|SocketException
function_decl|;
specifier|public
name|void
name|setReceiveBufferSize
parameter_list|(
name|int
name|size
parameter_list|)
throws|throws
name|SocketException
function_decl|;
specifier|public
name|void
name|setReuseAddress
parameter_list|(
name|boolean
name|on
parameter_list|)
throws|throws
name|SocketException
function_decl|;
specifier|public
name|void
name|setSendBufferSize
parameter_list|(
name|int
name|size
parameter_list|)
throws|throws
name|SocketException
function_decl|;
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
function_decl|;
specifier|public
name|void
name|setTcpNoDelay
parameter_list|(
name|boolean
name|on
parameter_list|)
throws|throws
name|SocketException
function_decl|;
specifier|public
name|void
name|setTrafficClass
parameter_list|(
name|int
name|tc
parameter_list|)
throws|throws
name|SocketException
function_decl|;
block|}
end_interface

end_unit

