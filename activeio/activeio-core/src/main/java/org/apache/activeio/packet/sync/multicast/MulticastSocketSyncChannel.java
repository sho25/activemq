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
name|multicast
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
name|MulticastSocket
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
name|datagram
operator|.
name|DatagramSocketSyncChannel
import|;
end_import

begin_comment
comment|/**  * @version $Revision$  */
end_comment

begin_class
specifier|final
specifier|public
class|class
name|MulticastSocketSyncChannel
extends|extends
name|DatagramSocketSyncChannel
block|{
specifier|private
specifier|final
name|InetAddress
name|groupAddress
decl_stmt|;
specifier|protected
name|MulticastSocketSyncChannel
parameter_list|(
name|MulticastSocket
name|socket
parameter_list|,
name|InetAddress
name|groupAddress
parameter_list|)
throws|throws
name|IOException
block|{
name|super
argument_list|(
name|socket
argument_list|)
expr_stmt|;
name|this
operator|.
name|groupAddress
operator|=
name|groupAddress
expr_stmt|;
block|}
specifier|public
name|void
name|start
parameter_list|()
throws|throws
name|IOException
block|{
operator|(
operator|(
name|MulticastSocket
operator|)
name|getSocket
argument_list|()
operator|)
operator|.
name|joinGroup
argument_list|(
name|groupAddress
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|stop
parameter_list|()
throws|throws
name|IOException
block|{
operator|(
operator|(
name|MulticastSocket
operator|)
name|getSocket
argument_list|()
operator|)
operator|.
name|leaveGroup
argument_list|(
name|groupAddress
argument_list|)
expr_stmt|;
block|}
specifier|public
name|String
name|toString
parameter_list|()
block|{
return|return
literal|"MulticastSocket Connection: "
operator|+
name|getSocket
argument_list|()
operator|.
name|getLocalSocketAddress
argument_list|()
operator|+
literal|" -> "
operator|+
name|getSocket
argument_list|()
operator|.
name|getRemoteSocketAddress
argument_list|()
return|;
block|}
block|}
end_class

end_unit

