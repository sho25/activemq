begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *      http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
package|package
name|org
operator|.
name|apache
operator|.
name|activemq
operator|.
name|transport
operator|.
name|udp
package|;
end_package

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|activemq
operator|.
name|command
operator|.
name|Command
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|activemq
operator|.
name|openwire
operator|.
name|OpenWireFormat
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|activemq
operator|.
name|transport
operator|.
name|reliable
operator|.
name|ReplayBuffer
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|activemq
operator|.
name|util
operator|.
name|IntSequenceGenerator
import|;
end_import

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
name|SocketAddress
import|;
end_import

begin_comment
comment|/**  *   * @version $Revision$  */
end_comment

begin_class
specifier|public
specifier|abstract
class|class
name|CommandChannelSupport
implements|implements
name|CommandChannel
block|{
specifier|protected
name|OpenWireFormat
name|wireFormat
decl_stmt|;
specifier|protected
name|int
name|datagramSize
init|=
literal|4
operator|*
literal|1024
decl_stmt|;
specifier|protected
name|SocketAddress
name|targetAddress
decl_stmt|;
specifier|protected
name|SocketAddress
name|replayAddress
decl_stmt|;
specifier|protected
specifier|final
name|String
name|name
decl_stmt|;
specifier|protected
specifier|final
name|IntSequenceGenerator
name|sequenceGenerator
decl_stmt|;
specifier|protected
name|DatagramHeaderMarshaller
name|headerMarshaller
decl_stmt|;
specifier|private
name|ReplayBuffer
name|replayBuffer
decl_stmt|;
specifier|public
name|CommandChannelSupport
parameter_list|(
name|UdpTransport
name|transport
parameter_list|,
name|OpenWireFormat
name|wireFormat
parameter_list|,
name|int
name|datagramSize
parameter_list|,
name|SocketAddress
name|targetAddress
parameter_list|,
name|DatagramHeaderMarshaller
name|headerMarshaller
parameter_list|)
block|{
name|this
operator|.
name|wireFormat
operator|=
name|wireFormat
expr_stmt|;
name|this
operator|.
name|datagramSize
operator|=
name|datagramSize
expr_stmt|;
name|this
operator|.
name|targetAddress
operator|=
name|targetAddress
expr_stmt|;
name|this
operator|.
name|headerMarshaller
operator|=
name|headerMarshaller
expr_stmt|;
name|this
operator|.
name|name
operator|=
name|transport
operator|.
name|toString
argument_list|()
expr_stmt|;
name|this
operator|.
name|sequenceGenerator
operator|=
name|transport
operator|.
name|getSequenceGenerator
argument_list|()
expr_stmt|;
name|this
operator|.
name|replayAddress
operator|=
name|targetAddress
expr_stmt|;
if|if
condition|(
name|sequenceGenerator
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"No sequenceGenerator on the given transport: "
operator|+
name|transport
argument_list|)
throw|;
block|}
block|}
specifier|public
name|void
name|write
parameter_list|(
name|Command
name|command
parameter_list|)
throws|throws
name|IOException
block|{
name|write
argument_list|(
name|command
argument_list|,
name|targetAddress
argument_list|)
expr_stmt|;
block|}
comment|// Properties
comment|// -------------------------------------------------------------------------
specifier|public
name|int
name|getDatagramSize
parameter_list|()
block|{
return|return
name|datagramSize
return|;
block|}
comment|/**      * Sets the default size of a datagram on the network.      */
specifier|public
name|void
name|setDatagramSize
parameter_list|(
name|int
name|datagramSize
parameter_list|)
block|{
name|this
operator|.
name|datagramSize
operator|=
name|datagramSize
expr_stmt|;
block|}
specifier|public
name|SocketAddress
name|getTargetAddress
parameter_list|()
block|{
return|return
name|targetAddress
return|;
block|}
specifier|public
name|void
name|setTargetAddress
parameter_list|(
name|SocketAddress
name|targetAddress
parameter_list|)
block|{
name|this
operator|.
name|targetAddress
operator|=
name|targetAddress
expr_stmt|;
block|}
specifier|public
name|SocketAddress
name|getReplayAddress
parameter_list|()
block|{
return|return
name|replayAddress
return|;
block|}
specifier|public
name|void
name|setReplayAddress
parameter_list|(
name|SocketAddress
name|replayAddress
parameter_list|)
block|{
name|this
operator|.
name|replayAddress
operator|=
name|replayAddress
expr_stmt|;
block|}
specifier|public
name|String
name|toString
parameter_list|()
block|{
return|return
literal|"CommandChannel#"
operator|+
name|name
return|;
block|}
specifier|public
name|DatagramHeaderMarshaller
name|getHeaderMarshaller
parameter_list|()
block|{
return|return
name|headerMarshaller
return|;
block|}
specifier|public
name|void
name|setHeaderMarshaller
parameter_list|(
name|DatagramHeaderMarshaller
name|headerMarshaller
parameter_list|)
block|{
name|this
operator|.
name|headerMarshaller
operator|=
name|headerMarshaller
expr_stmt|;
block|}
specifier|public
name|ReplayBuffer
name|getReplayBuffer
parameter_list|()
block|{
return|return
name|replayBuffer
return|;
block|}
specifier|public
name|void
name|setReplayBuffer
parameter_list|(
name|ReplayBuffer
name|replayBuffer
parameter_list|)
block|{
name|this
operator|.
name|replayBuffer
operator|=
name|replayBuffer
expr_stmt|;
block|}
block|}
end_class

end_unit

