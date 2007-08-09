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
name|InetSocketAddress
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
name|URI
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|HashMap
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Map
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
name|command
operator|.
name|BrokerInfo
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
name|CommandJoiner
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
name|InactivityMonitor
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
name|Transport
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
name|TransportListener
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
name|TransportServer
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
name|TransportServerSupport
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
name|ReliableTransport
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
name|ReplayStrategy
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
name|Replayer
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

begin_comment
comment|/**  * A UDP based implementation of {@link TransportServer}  *   * @version $Revision$  */
end_comment

begin_class
specifier|public
class|class
name|UdpTransportServer
extends|extends
name|TransportServerSupport
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
name|UdpTransportServer
operator|.
name|class
argument_list|)
decl_stmt|;
specifier|private
name|UdpTransport
name|serverTransport
decl_stmt|;
specifier|private
name|ReplayStrategy
name|replayStrategy
decl_stmt|;
specifier|private
name|Transport
name|configuredTransport
decl_stmt|;
specifier|private
name|boolean
name|usingWireFormatNegotiation
decl_stmt|;
specifier|private
name|Map
name|transports
init|=
operator|new
name|HashMap
argument_list|()
decl_stmt|;
specifier|public
name|UdpTransportServer
parameter_list|(
name|URI
name|connectURI
parameter_list|,
name|UdpTransport
name|serverTransport
parameter_list|,
name|Transport
name|configuredTransport
parameter_list|,
name|ReplayStrategy
name|replayStrategy
parameter_list|)
block|{
name|super
argument_list|(
name|connectURI
argument_list|)
expr_stmt|;
name|this
operator|.
name|serverTransport
operator|=
name|serverTransport
expr_stmt|;
name|this
operator|.
name|configuredTransport
operator|=
name|configuredTransport
expr_stmt|;
name|this
operator|.
name|replayStrategy
operator|=
name|replayStrategy
expr_stmt|;
block|}
specifier|public
name|String
name|toString
parameter_list|()
block|{
return|return
literal|"UdpTransportServer@"
operator|+
name|serverTransport
return|;
block|}
specifier|public
name|void
name|run
parameter_list|()
block|{     }
specifier|public
name|UdpTransport
name|getServerTransport
parameter_list|()
block|{
return|return
name|serverTransport
return|;
block|}
specifier|public
name|void
name|setBrokerInfo
parameter_list|(
name|BrokerInfo
name|brokerInfo
parameter_list|)
block|{     }
specifier|protected
name|void
name|doStart
parameter_list|()
throws|throws
name|Exception
block|{
name|log
operator|.
name|info
argument_list|(
literal|"Starting "
operator|+
name|this
argument_list|)
expr_stmt|;
name|configuredTransport
operator|.
name|setTransportListener
argument_list|(
operator|new
name|TransportListener
argument_list|()
block|{
specifier|public
name|void
name|onCommand
parameter_list|(
name|Object
name|o
parameter_list|)
block|{
specifier|final
name|Command
name|command
init|=
operator|(
name|Command
operator|)
name|o
decl_stmt|;
name|processInboundConnection
argument_list|(
name|command
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|onException
parameter_list|(
name|IOException
name|error
parameter_list|)
block|{
name|log
operator|.
name|error
argument_list|(
literal|"Caught: "
operator|+
name|error
argument_list|,
name|error
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|transportInterupted
parameter_list|()
block|{             }
specifier|public
name|void
name|transportResumed
parameter_list|()
block|{             }
block|}
argument_list|)
expr_stmt|;
name|configuredTransport
operator|.
name|start
argument_list|()
expr_stmt|;
block|}
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
name|configuredTransport
operator|.
name|stop
argument_list|()
expr_stmt|;
block|}
specifier|protected
name|void
name|processInboundConnection
parameter_list|(
name|Command
name|command
parameter_list|)
block|{
name|DatagramEndpoint
name|endpoint
init|=
operator|(
name|DatagramEndpoint
operator|)
name|command
operator|.
name|getFrom
argument_list|()
decl_stmt|;
if|if
condition|(
name|log
operator|.
name|isDebugEnabled
argument_list|()
condition|)
block|{
name|log
operator|.
name|debug
argument_list|(
literal|"Received command on: "
operator|+
name|this
operator|+
literal|" from address: "
operator|+
name|endpoint
operator|+
literal|" command: "
operator|+
name|command
argument_list|)
expr_stmt|;
block|}
name|Transport
name|transport
init|=
literal|null
decl_stmt|;
synchronized|synchronized
init|(
name|transports
init|)
block|{
name|transport
operator|=
operator|(
name|Transport
operator|)
name|transports
operator|.
name|get
argument_list|(
name|endpoint
argument_list|)
expr_stmt|;
if|if
condition|(
name|transport
operator|==
literal|null
condition|)
block|{
if|if
condition|(
name|usingWireFormatNegotiation
operator|&&
operator|!
name|command
operator|.
name|isWireFormatInfo
argument_list|()
condition|)
block|{
name|log
operator|.
name|error
argument_list|(
literal|"Received inbound server communication from: "
operator|+
name|command
operator|.
name|getFrom
argument_list|()
operator|+
literal|" expecting WireFormatInfo but was command: "
operator|+
name|command
argument_list|)
expr_stmt|;
block|}
else|else
block|{
if|if
condition|(
name|log
operator|.
name|isDebugEnabled
argument_list|()
condition|)
block|{
name|log
operator|.
name|debug
argument_list|(
literal|"Creating a new UDP server connection"
argument_list|)
expr_stmt|;
block|}
try|try
block|{
name|transport
operator|=
name|createTransport
argument_list|(
name|command
argument_list|,
name|endpoint
argument_list|)
expr_stmt|;
name|transport
operator|=
name|configureTransport
argument_list|(
name|transport
argument_list|)
expr_stmt|;
name|transports
operator|.
name|put
argument_list|(
name|endpoint
argument_list|,
name|transport
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
name|log
operator|.
name|error
argument_list|(
literal|"Caught: "
operator|+
name|e
argument_list|,
name|e
argument_list|)
expr_stmt|;
name|getAcceptListener
argument_list|()
operator|.
name|onAcceptError
argument_list|(
name|e
argument_list|)
expr_stmt|;
block|}
block|}
block|}
else|else
block|{
name|log
operator|.
name|warn
argument_list|(
literal|"Discarding duplicate command to server from: "
operator|+
name|endpoint
operator|+
literal|" command: "
operator|+
name|command
argument_list|)
expr_stmt|;
block|}
block|}
block|}
specifier|protected
name|Transport
name|configureTransport
parameter_list|(
name|Transport
name|transport
parameter_list|)
block|{
name|transport
operator|=
operator|new
name|InactivityMonitor
argument_list|(
name|transport
argument_list|)
expr_stmt|;
name|getAcceptListener
argument_list|()
operator|.
name|onAccept
argument_list|(
name|transport
argument_list|)
expr_stmt|;
return|return
name|transport
return|;
block|}
specifier|protected
name|Transport
name|createTransport
parameter_list|(
specifier|final
name|Command
name|command
parameter_list|,
name|DatagramEndpoint
name|endpoint
parameter_list|)
throws|throws
name|IOException
block|{
if|if
condition|(
name|endpoint
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|IOException
argument_list|(
literal|"No endpoint available for command: "
operator|+
name|command
argument_list|)
throw|;
block|}
specifier|final
name|SocketAddress
name|address
init|=
name|endpoint
operator|.
name|getAddress
argument_list|()
decl_stmt|;
specifier|final
name|OpenWireFormat
name|connectionWireFormat
init|=
name|serverTransport
operator|.
name|getWireFormat
argument_list|()
operator|.
name|copy
argument_list|()
decl_stmt|;
specifier|final
name|UdpTransport
name|transport
init|=
operator|new
name|UdpTransport
argument_list|(
name|connectionWireFormat
argument_list|,
name|address
argument_list|)
decl_stmt|;
specifier|final
name|ReliableTransport
name|reliableTransport
init|=
operator|new
name|ReliableTransport
argument_list|(
name|transport
argument_list|,
name|transport
argument_list|)
decl_stmt|;
name|Replayer
name|replayer
init|=
name|reliableTransport
operator|.
name|getReplayer
argument_list|()
decl_stmt|;
name|reliableTransport
operator|.
name|setReplayStrategy
argument_list|(
name|replayStrategy
argument_list|)
expr_stmt|;
comment|// Joiner must be on outside as the inbound messages must be processed
comment|// by the reliable transport first
return|return
operator|new
name|CommandJoiner
argument_list|(
name|reliableTransport
argument_list|,
name|connectionWireFormat
argument_list|)
block|{
specifier|public
name|void
name|start
parameter_list|()
throws|throws
name|Exception
block|{
name|super
operator|.
name|start
argument_list|()
expr_stmt|;
name|reliableTransport
operator|.
name|onCommand
argument_list|(
name|command
argument_list|)
expr_stmt|;
block|}
block|}
return|;
comment|/**          * final WireFormatNegotiator wireFormatNegotiator = new          * WireFormatNegotiator(configuredTransport, transport.getWireFormat(),          * serverTransport .getMinmumWireFormatVersion()) { public void start()          * throws Exception { super.start(); log.debug("Starting a new server          * transport: " + this + " with command: " + command);          * onCommand(command); } // lets use the specific addressing of wire          * format protected void sendWireFormat(WireFormatInfo info) throws          * IOException { log.debug("#### we have negotiated the wireformat;          * sending a wireformat to: " + address); transport.oneway(info,          * address); } }; return wireFormatNegotiator;          */
block|}
specifier|public
name|InetSocketAddress
name|getSocketAddress
parameter_list|()
block|{
return|return
name|serverTransport
operator|.
name|getLocalSocketAddress
argument_list|()
return|;
block|}
block|}
end_class

end_unit

