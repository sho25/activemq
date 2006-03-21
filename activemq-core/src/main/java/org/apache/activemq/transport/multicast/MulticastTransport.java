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
name|activemq
operator|.
name|transport
operator|.
name|multicast
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
name|udp
operator|.
name|CommandChannel
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
name|udp
operator|.
name|CommandDatagramSocket
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
name|udp
operator|.
name|DatagramHeaderMarshaller
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
name|udp
operator|.
name|UdpTransport
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
name|ServiceStopper
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|commons
operator|.
name|logging
operator|.
name|Log
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|commons
operator|.
name|logging
operator|.
name|LogFactory
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
name|InetSocketAddress
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
name|net
operator|.
name|UnknownHostException
import|;
end_import

begin_comment
comment|/**  * A multicast based transport.  *   * @version $Revision$  */
end_comment

begin_class
specifier|public
class|class
name|MulticastTransport
extends|extends
name|UdpTransport
block|{
specifier|private
specifier|static
specifier|final
name|Log
name|log
init|=
name|LogFactory
operator|.
name|getLog
argument_list|(
name|MulticastTransport
operator|.
name|class
argument_list|)
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|int
name|DEFAULT_IDLE_TIME
init|=
literal|5000
decl_stmt|;
specifier|private
name|MulticastSocket
name|socket
decl_stmt|;
specifier|private
name|InetAddress
name|mcastAddress
decl_stmt|;
specifier|private
name|int
name|mcastPort
decl_stmt|;
specifier|private
name|int
name|timeToLive
init|=
literal|1
decl_stmt|;
specifier|private
name|boolean
name|loopBackMode
init|=
literal|false
decl_stmt|;
specifier|private
name|long
name|keepAliveInterval
init|=
name|DEFAULT_IDLE_TIME
decl_stmt|;
specifier|public
name|MulticastTransport
parameter_list|(
name|OpenWireFormat
name|wireFormat
parameter_list|,
name|URI
name|remoteLocation
parameter_list|)
throws|throws
name|UnknownHostException
throws|,
name|IOException
block|{
name|super
argument_list|(
name|wireFormat
argument_list|,
name|remoteLocation
argument_list|)
expr_stmt|;
block|}
specifier|public
name|long
name|getKeepAliveInterval
parameter_list|()
block|{
return|return
name|keepAliveInterval
return|;
block|}
specifier|public
name|void
name|setKeepAliveInterval
parameter_list|(
name|long
name|keepAliveInterval
parameter_list|)
block|{
name|this
operator|.
name|keepAliveInterval
operator|=
name|keepAliveInterval
expr_stmt|;
block|}
specifier|public
name|boolean
name|isLoopBackMode
parameter_list|()
block|{
return|return
name|loopBackMode
return|;
block|}
specifier|public
name|void
name|setLoopBackMode
parameter_list|(
name|boolean
name|loopBackMode
parameter_list|)
block|{
name|this
operator|.
name|loopBackMode
operator|=
name|loopBackMode
expr_stmt|;
block|}
specifier|public
name|int
name|getTimeToLive
parameter_list|()
block|{
return|return
name|timeToLive
return|;
block|}
specifier|public
name|void
name|setTimeToLive
parameter_list|(
name|int
name|timeToLive
parameter_list|)
block|{
name|this
operator|.
name|timeToLive
operator|=
name|timeToLive
expr_stmt|;
block|}
specifier|protected
name|String
name|getProtocolName
parameter_list|()
block|{
return|return
literal|"Multicast"
return|;
block|}
specifier|protected
name|String
name|getProtocolUriScheme
parameter_list|()
block|{
return|return
literal|"multicast://"
return|;
block|}
specifier|protected
name|void
name|bind
parameter_list|(
name|DatagramSocket
name|socket
parameter_list|,
name|SocketAddress
name|localAddress
parameter_list|)
throws|throws
name|SocketException
block|{     }
specifier|protected
name|void
name|doStop
parameter_list|(
name|ServiceStopper
name|stopper
parameter_list|)
throws|throws
name|Exception
block|{
name|super
operator|.
name|doStop
argument_list|(
name|stopper
argument_list|)
expr_stmt|;
if|if
condition|(
name|socket
operator|!=
literal|null
condition|)
block|{
try|try
block|{
name|socket
operator|.
name|leaveGroup
argument_list|(
name|getMulticastAddress
argument_list|()
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
name|stopper
operator|.
name|onException
argument_list|(
name|this
argument_list|,
name|e
argument_list|)
expr_stmt|;
block|}
name|socket
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
specifier|protected
name|CommandChannel
name|createCommandChannel
parameter_list|()
throws|throws
name|IOException
block|{
name|socket
operator|=
operator|new
name|MulticastSocket
argument_list|(
name|mcastPort
argument_list|)
expr_stmt|;
name|socket
operator|.
name|setLoopbackMode
argument_list|(
name|loopBackMode
argument_list|)
expr_stmt|;
name|socket
operator|.
name|setTimeToLive
argument_list|(
name|timeToLive
argument_list|)
expr_stmt|;
name|log
operator|.
name|debug
argument_list|(
literal|"Joining multicast address: "
operator|+
name|getMulticastAddress
argument_list|()
argument_list|)
expr_stmt|;
name|socket
operator|.
name|joinGroup
argument_list|(
name|getMulticastAddress
argument_list|()
argument_list|)
expr_stmt|;
name|socket
operator|.
name|setSoTimeout
argument_list|(
operator|(
name|int
operator|)
name|keepAliveInterval
argument_list|)
expr_stmt|;
return|return
operator|new
name|CommandDatagramSocket
argument_list|(
name|this
argument_list|,
name|getWireFormat
argument_list|()
argument_list|,
name|getDatagramSize
argument_list|()
argument_list|,
name|getTargetAddress
argument_list|()
argument_list|,
name|createDatagramHeaderMarshaller
argument_list|()
argument_list|,
name|getSocket
argument_list|()
argument_list|)
return|;
block|}
specifier|protected
name|InetAddress
name|getMulticastAddress
parameter_list|()
block|{
return|return
name|mcastAddress
return|;
block|}
specifier|protected
name|MulticastSocket
name|getSocket
parameter_list|()
block|{
return|return
name|socket
return|;
block|}
specifier|protected
name|void
name|setSocket
parameter_list|(
name|MulticastSocket
name|socket
parameter_list|)
block|{
name|this
operator|.
name|socket
operator|=
name|socket
expr_stmt|;
block|}
specifier|protected
name|InetSocketAddress
name|createAddress
parameter_list|(
name|URI
name|remoteLocation
parameter_list|)
throws|throws
name|UnknownHostException
throws|,
name|IOException
block|{
name|this
operator|.
name|mcastAddress
operator|=
name|InetAddress
operator|.
name|getByName
argument_list|(
name|remoteLocation
operator|.
name|getHost
argument_list|()
argument_list|)
expr_stmt|;
name|this
operator|.
name|mcastPort
operator|=
name|remoteLocation
operator|.
name|getPort
argument_list|()
expr_stmt|;
return|return
operator|new
name|InetSocketAddress
argument_list|(
name|mcastAddress
argument_list|,
name|mcastPort
argument_list|)
return|;
block|}
specifier|protected
name|DatagramHeaderMarshaller
name|createDatagramHeaderMarshaller
parameter_list|()
block|{
return|return
operator|new
name|MulticastDatagramHeaderMarshaller
argument_list|(
literal|"udp://dummyHostName:"
operator|+
name|getPort
argument_list|()
argument_list|)
return|;
block|}
block|}
end_class

end_unit

