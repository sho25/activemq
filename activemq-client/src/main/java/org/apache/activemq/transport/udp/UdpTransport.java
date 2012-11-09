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
name|EOFException
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
name|BindException
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

begin_import
import|import
name|java
operator|.
name|nio
operator|.
name|channels
operator|.
name|AsynchronousCloseException
import|;
end_import

begin_import
import|import
name|java
operator|.
name|nio
operator|.
name|channels
operator|.
name|DatagramChannel
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
name|Service
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
name|command
operator|.
name|Endpoint
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
name|TransportThreadSupport
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
name|ExceptionIfDroppedReplayStrategy
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
name|InetAddressUtil
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
name|slf4j
operator|.
name|Logger
import|;
end_import

begin_import
import|import
name|org
operator|.
name|slf4j
operator|.
name|LoggerFactory
import|;
end_import

begin_comment
comment|/**  * An implementation of the {@link Transport} interface using raw UDP  *   *   */
end_comment

begin_class
specifier|public
class|class
name|UdpTransport
extends|extends
name|TransportThreadSupport
implements|implements
name|Transport
implements|,
name|Service
implements|,
name|Runnable
block|{
specifier|private
specifier|static
specifier|final
name|Logger
name|LOG
init|=
name|LoggerFactory
operator|.
name|getLogger
argument_list|(
name|UdpTransport
operator|.
name|class
argument_list|)
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|int
name|MAX_BIND_ATTEMPTS
init|=
literal|50
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|long
name|BIND_ATTEMPT_DELAY
init|=
literal|100
decl_stmt|;
specifier|private
name|CommandChannel
name|commandChannel
decl_stmt|;
specifier|private
name|OpenWireFormat
name|wireFormat
decl_stmt|;
specifier|private
name|ByteBufferPool
name|bufferPool
decl_stmt|;
specifier|private
name|ReplayStrategy
name|replayStrategy
init|=
operator|new
name|ExceptionIfDroppedReplayStrategy
argument_list|()
decl_stmt|;
specifier|private
name|ReplayBuffer
name|replayBuffer
decl_stmt|;
specifier|private
name|int
name|datagramSize
init|=
literal|4
operator|*
literal|1024
decl_stmt|;
specifier|private
name|SocketAddress
name|targetAddress
decl_stmt|;
specifier|private
name|SocketAddress
name|originalTargetAddress
decl_stmt|;
specifier|private
name|DatagramChannel
name|channel
decl_stmt|;
specifier|private
name|boolean
name|trace
decl_stmt|;
specifier|private
name|boolean
name|useLocalHost
init|=
literal|false
decl_stmt|;
specifier|private
name|int
name|port
decl_stmt|;
specifier|private
name|int
name|minmumWireFormatVersion
decl_stmt|;
specifier|private
name|String
name|description
decl_stmt|;
specifier|private
name|IntSequenceGenerator
name|sequenceGenerator
decl_stmt|;
specifier|private
name|boolean
name|replayEnabled
init|=
literal|true
decl_stmt|;
specifier|protected
name|UdpTransport
parameter_list|(
name|OpenWireFormat
name|wireFormat
parameter_list|)
throws|throws
name|IOException
block|{
name|this
operator|.
name|wireFormat
operator|=
name|wireFormat
expr_stmt|;
block|}
specifier|public
name|UdpTransport
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
name|this
argument_list|(
name|wireFormat
argument_list|)
expr_stmt|;
name|this
operator|.
name|targetAddress
operator|=
name|createAddress
argument_list|(
name|remoteLocation
argument_list|)
expr_stmt|;
name|description
operator|=
name|remoteLocation
operator|.
name|toString
argument_list|()
operator|+
literal|"@"
expr_stmt|;
block|}
specifier|public
name|UdpTransport
parameter_list|(
name|OpenWireFormat
name|wireFormat
parameter_list|,
name|SocketAddress
name|socketAddress
parameter_list|)
throws|throws
name|IOException
block|{
name|this
argument_list|(
name|wireFormat
argument_list|)
expr_stmt|;
name|this
operator|.
name|targetAddress
operator|=
name|socketAddress
expr_stmt|;
name|this
operator|.
name|description
operator|=
name|getProtocolName
argument_list|()
operator|+
literal|"ServerConnection@"
expr_stmt|;
block|}
comment|/**      * Used by the server transport      */
specifier|public
name|UdpTransport
parameter_list|(
name|OpenWireFormat
name|wireFormat
parameter_list|,
name|int
name|port
parameter_list|)
throws|throws
name|UnknownHostException
throws|,
name|IOException
block|{
name|this
argument_list|(
name|wireFormat
argument_list|)
expr_stmt|;
name|this
operator|.
name|port
operator|=
name|port
expr_stmt|;
name|this
operator|.
name|targetAddress
operator|=
literal|null
expr_stmt|;
name|this
operator|.
name|description
operator|=
name|getProtocolName
argument_list|()
operator|+
literal|"Server@"
expr_stmt|;
block|}
comment|/**      * Creates a replayer for working with the reliable transport      */
specifier|public
name|Replayer
name|createReplayer
parameter_list|()
throws|throws
name|IOException
block|{
if|if
condition|(
name|replayEnabled
condition|)
block|{
return|return
name|getCommandChannel
argument_list|()
return|;
block|}
return|return
literal|null
return|;
block|}
comment|/**      * A one way asynchronous send      */
specifier|public
name|void
name|oneway
parameter_list|(
name|Object
name|command
parameter_list|)
throws|throws
name|IOException
block|{
name|oneway
argument_list|(
name|command
argument_list|,
name|targetAddress
argument_list|)
expr_stmt|;
block|}
comment|/**      * A one way asynchronous send to a given address      */
specifier|public
name|void
name|oneway
parameter_list|(
name|Object
name|command
parameter_list|,
name|SocketAddress
name|address
parameter_list|)
throws|throws
name|IOException
block|{
if|if
condition|(
name|LOG
operator|.
name|isDebugEnabled
argument_list|()
condition|)
block|{
name|LOG
operator|.
name|debug
argument_list|(
literal|"Sending oneway from: "
operator|+
name|this
operator|+
literal|" to target: "
operator|+
name|targetAddress
operator|+
literal|" command: "
operator|+
name|command
argument_list|)
expr_stmt|;
block|}
name|checkStarted
argument_list|()
expr_stmt|;
name|commandChannel
operator|.
name|write
argument_list|(
operator|(
name|Command
operator|)
name|command
argument_list|,
name|address
argument_list|)
expr_stmt|;
block|}
comment|/**      * @return pretty print of 'this'      */
specifier|public
name|String
name|toString
parameter_list|()
block|{
if|if
condition|(
name|description
operator|!=
literal|null
condition|)
block|{
return|return
name|description
operator|+
name|port
return|;
block|}
else|else
block|{
return|return
name|getProtocolUriScheme
argument_list|()
operator|+
name|targetAddress
operator|+
literal|"@"
operator|+
name|port
return|;
block|}
block|}
comment|/**      * reads packets from a Socket      */
specifier|public
name|void
name|run
parameter_list|()
block|{
name|LOG
operator|.
name|trace
argument_list|(
literal|"Consumer thread starting for: "
operator|+
name|toString
argument_list|()
argument_list|)
expr_stmt|;
while|while
condition|(
operator|!
name|isStopped
argument_list|()
condition|)
block|{
try|try
block|{
name|Command
name|command
init|=
name|commandChannel
operator|.
name|read
argument_list|()
decl_stmt|;
name|doConsume
argument_list|(
name|command
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|AsynchronousCloseException
name|e
parameter_list|)
block|{
comment|// DatagramChannel closed
try|try
block|{
name|stop
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e2
parameter_list|)
block|{
name|LOG
operator|.
name|warn
argument_list|(
literal|"Caught in: "
operator|+
name|this
operator|+
literal|" while closing: "
operator|+
name|e2
operator|+
literal|". Now Closed"
argument_list|,
name|e2
argument_list|)
expr_stmt|;
block|}
block|}
catch|catch
parameter_list|(
name|SocketException
name|e
parameter_list|)
block|{
comment|// DatagramSocket closed
name|LOG
operator|.
name|debug
argument_list|(
literal|"Socket closed: "
operator|+
name|e
argument_list|,
name|e
argument_list|)
expr_stmt|;
try|try
block|{
name|stop
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e2
parameter_list|)
block|{
name|LOG
operator|.
name|warn
argument_list|(
literal|"Caught in: "
operator|+
name|this
operator|+
literal|" while closing: "
operator|+
name|e2
operator|+
literal|". Now Closed"
argument_list|,
name|e2
argument_list|)
expr_stmt|;
block|}
block|}
catch|catch
parameter_list|(
name|EOFException
name|e
parameter_list|)
block|{
comment|// DataInputStream closed
name|LOG
operator|.
name|debug
argument_list|(
literal|"Socket closed: "
operator|+
name|e
argument_list|,
name|e
argument_list|)
expr_stmt|;
try|try
block|{
name|stop
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e2
parameter_list|)
block|{
name|LOG
operator|.
name|warn
argument_list|(
literal|"Caught in: "
operator|+
name|this
operator|+
literal|" while closing: "
operator|+
name|e2
operator|+
literal|". Now Closed"
argument_list|,
name|e2
argument_list|)
expr_stmt|;
block|}
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
try|try
block|{
name|stop
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e2
parameter_list|)
block|{
name|LOG
operator|.
name|warn
argument_list|(
literal|"Caught in: "
operator|+
name|this
operator|+
literal|" while closing: "
operator|+
name|e2
operator|+
literal|". Now Closed"
argument_list|,
name|e2
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|e
operator|instanceof
name|IOException
condition|)
block|{
name|onException
argument_list|(
operator|(
name|IOException
operator|)
name|e
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|LOG
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
name|e
operator|.
name|printStackTrace
argument_list|()
expr_stmt|;
block|}
block|}
block|}
block|}
comment|/**      * We have received the WireFormatInfo from the server on the actual channel      * we should use for all future communication with the server, so lets set      * the target to be the actual channel that the server has chosen for us to      * talk on.      */
specifier|public
name|void
name|setTargetEndpoint
parameter_list|(
name|Endpoint
name|newTarget
parameter_list|)
block|{
if|if
condition|(
name|newTarget
operator|instanceof
name|DatagramEndpoint
condition|)
block|{
name|DatagramEndpoint
name|endpoint
init|=
operator|(
name|DatagramEndpoint
operator|)
name|newTarget
decl_stmt|;
name|SocketAddress
name|address
init|=
name|endpoint
operator|.
name|getAddress
argument_list|()
decl_stmt|;
if|if
condition|(
name|address
operator|!=
literal|null
condition|)
block|{
if|if
condition|(
name|originalTargetAddress
operator|==
literal|null
condition|)
block|{
name|originalTargetAddress
operator|=
name|targetAddress
expr_stmt|;
block|}
name|targetAddress
operator|=
name|address
expr_stmt|;
name|commandChannel
operator|.
name|setTargetAddress
argument_list|(
name|address
argument_list|)
expr_stmt|;
block|}
block|}
block|}
comment|// Properties
comment|// -------------------------------------------------------------------------
specifier|public
name|boolean
name|isTrace
parameter_list|()
block|{
return|return
name|trace
return|;
block|}
specifier|public
name|void
name|setTrace
parameter_list|(
name|boolean
name|trace
parameter_list|)
block|{
name|this
operator|.
name|trace
operator|=
name|trace
expr_stmt|;
block|}
specifier|public
name|int
name|getDatagramSize
parameter_list|()
block|{
return|return
name|datagramSize
return|;
block|}
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
name|boolean
name|isUseLocalHost
parameter_list|()
block|{
return|return
name|useLocalHost
return|;
block|}
comment|/**      * Sets whether 'localhost' or the actual local host name should be used to      * make local connections. On some operating systems such as Macs its not      * possible to connect as the local host name so localhost is better.      */
specifier|public
name|void
name|setUseLocalHost
parameter_list|(
name|boolean
name|useLocalHost
parameter_list|)
block|{
name|this
operator|.
name|useLocalHost
operator|=
name|useLocalHost
expr_stmt|;
block|}
specifier|public
name|CommandChannel
name|getCommandChannel
parameter_list|()
throws|throws
name|IOException
block|{
if|if
condition|(
name|commandChannel
operator|==
literal|null
condition|)
block|{
name|commandChannel
operator|=
name|createCommandChannel
argument_list|()
expr_stmt|;
block|}
return|return
name|commandChannel
return|;
block|}
comment|/**      * Sets the implementation of the command channel to use.      */
specifier|public
name|void
name|setCommandChannel
parameter_list|(
name|CommandDatagramChannel
name|commandChannel
parameter_list|)
block|{
name|this
operator|.
name|commandChannel
operator|=
name|commandChannel
expr_stmt|;
block|}
specifier|public
name|ReplayStrategy
name|getReplayStrategy
parameter_list|()
block|{
return|return
name|replayStrategy
return|;
block|}
comment|/**      * Sets the strategy used to replay missed datagrams      */
specifier|public
name|void
name|setReplayStrategy
parameter_list|(
name|ReplayStrategy
name|replayStrategy
parameter_list|)
block|{
name|this
operator|.
name|replayStrategy
operator|=
name|replayStrategy
expr_stmt|;
block|}
specifier|public
name|int
name|getPort
parameter_list|()
block|{
return|return
name|port
return|;
block|}
comment|/**      * Sets the port to connect on      */
specifier|public
name|void
name|setPort
parameter_list|(
name|int
name|port
parameter_list|)
block|{
name|this
operator|.
name|port
operator|=
name|port
expr_stmt|;
block|}
specifier|public
name|int
name|getMinmumWireFormatVersion
parameter_list|()
block|{
return|return
name|minmumWireFormatVersion
return|;
block|}
specifier|public
name|void
name|setMinmumWireFormatVersion
parameter_list|(
name|int
name|minmumWireFormatVersion
parameter_list|)
block|{
name|this
operator|.
name|minmumWireFormatVersion
operator|=
name|minmumWireFormatVersion
expr_stmt|;
block|}
specifier|public
name|OpenWireFormat
name|getWireFormat
parameter_list|()
block|{
return|return
name|wireFormat
return|;
block|}
specifier|public
name|IntSequenceGenerator
name|getSequenceGenerator
parameter_list|()
block|{
if|if
condition|(
name|sequenceGenerator
operator|==
literal|null
condition|)
block|{
name|sequenceGenerator
operator|=
operator|new
name|IntSequenceGenerator
argument_list|()
expr_stmt|;
block|}
return|return
name|sequenceGenerator
return|;
block|}
specifier|public
name|void
name|setSequenceGenerator
parameter_list|(
name|IntSequenceGenerator
name|sequenceGenerator
parameter_list|)
block|{
name|this
operator|.
name|sequenceGenerator
operator|=
name|sequenceGenerator
expr_stmt|;
block|}
specifier|public
name|boolean
name|isReplayEnabled
parameter_list|()
block|{
return|return
name|replayEnabled
return|;
block|}
comment|/**      * Sets whether or not replay should be enabled when using the reliable      * transport. i.e. should we maintain a buffer of messages that can be      * replayed?      */
specifier|public
name|void
name|setReplayEnabled
parameter_list|(
name|boolean
name|replayEnabled
parameter_list|)
block|{
name|this
operator|.
name|replayEnabled
operator|=
name|replayEnabled
expr_stmt|;
block|}
specifier|public
name|ByteBufferPool
name|getBufferPool
parameter_list|()
block|{
if|if
condition|(
name|bufferPool
operator|==
literal|null
condition|)
block|{
name|bufferPool
operator|=
operator|new
name|DefaultBufferPool
argument_list|()
expr_stmt|;
block|}
return|return
name|bufferPool
return|;
block|}
specifier|public
name|void
name|setBufferPool
parameter_list|(
name|ByteBufferPool
name|bufferPool
parameter_list|)
block|{
name|this
operator|.
name|bufferPool
operator|=
name|bufferPool
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
throws|throws
name|IOException
block|{
name|this
operator|.
name|replayBuffer
operator|=
name|replayBuffer
expr_stmt|;
name|getCommandChannel
argument_list|()
operator|.
name|setReplayBuffer
argument_list|(
name|replayBuffer
argument_list|)
expr_stmt|;
block|}
comment|// Implementation methods
comment|// -------------------------------------------------------------------------
comment|/**      * Creates an address from the given URI      */
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
name|String
name|host
init|=
name|resolveHostName
argument_list|(
name|remoteLocation
operator|.
name|getHost
argument_list|()
argument_list|)
decl_stmt|;
return|return
operator|new
name|InetSocketAddress
argument_list|(
name|host
argument_list|,
name|remoteLocation
operator|.
name|getPort
argument_list|()
argument_list|)
return|;
block|}
specifier|protected
name|String
name|resolveHostName
parameter_list|(
name|String
name|host
parameter_list|)
throws|throws
name|UnknownHostException
block|{
name|String
name|localName
init|=
name|InetAddressUtil
operator|.
name|getLocalHostName
argument_list|()
decl_stmt|;
if|if
condition|(
name|localName
operator|!=
literal|null
operator|&&
name|isUseLocalHost
argument_list|()
condition|)
block|{
if|if
condition|(
name|localName
operator|.
name|equals
argument_list|(
name|host
argument_list|)
condition|)
block|{
return|return
literal|"localhost"
return|;
block|}
block|}
return|return
name|host
return|;
block|}
specifier|protected
name|void
name|doStart
parameter_list|()
throws|throws
name|Exception
block|{
name|getCommandChannel
argument_list|()
operator|.
name|start
argument_list|()
expr_stmt|;
name|super
operator|.
name|doStart
argument_list|()
expr_stmt|;
block|}
specifier|protected
name|CommandChannel
name|createCommandChannel
parameter_list|()
throws|throws
name|IOException
block|{
name|SocketAddress
name|localAddress
init|=
name|createLocalAddress
argument_list|()
decl_stmt|;
name|channel
operator|=
name|DatagramChannel
operator|.
name|open
argument_list|()
expr_stmt|;
name|channel
operator|=
name|connect
argument_list|(
name|channel
argument_list|,
name|targetAddress
argument_list|)
expr_stmt|;
name|DatagramSocket
name|socket
init|=
name|channel
operator|.
name|socket
argument_list|()
decl_stmt|;
name|bind
argument_list|(
name|socket
argument_list|,
name|localAddress
argument_list|)
expr_stmt|;
if|if
condition|(
name|port
operator|==
literal|0
condition|)
block|{
name|port
operator|=
name|socket
operator|.
name|getLocalPort
argument_list|()
expr_stmt|;
block|}
return|return
name|createCommandDatagramChannel
argument_list|()
return|;
block|}
specifier|protected
name|CommandChannel
name|createCommandDatagramChannel
parameter_list|()
block|{
return|return
operator|new
name|CommandDatagramChannel
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
name|getChannel
argument_list|()
argument_list|,
name|getBufferPool
argument_list|()
argument_list|)
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
name|IOException
block|{
name|channel
operator|.
name|configureBlocking
argument_list|(
literal|true
argument_list|)
expr_stmt|;
if|if
condition|(
name|LOG
operator|.
name|isDebugEnabled
argument_list|()
condition|)
block|{
name|LOG
operator|.
name|debug
argument_list|(
literal|"Binding to address: "
operator|+
name|localAddress
argument_list|)
expr_stmt|;
block|}
comment|//
comment|// We have noticed that on some platfoms like linux, after you close
comment|// down
comment|// a previously bound socket, it can take a little while before we can
comment|// bind it again.
comment|//
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|MAX_BIND_ATTEMPTS
condition|;
name|i
operator|++
control|)
block|{
try|try
block|{
name|socket
operator|.
name|bind
argument_list|(
name|localAddress
argument_list|)
expr_stmt|;
return|return;
block|}
catch|catch
parameter_list|(
name|BindException
name|e
parameter_list|)
block|{
if|if
condition|(
name|i
operator|+
literal|1
operator|==
name|MAX_BIND_ATTEMPTS
condition|)
block|{
throw|throw
name|e
throw|;
block|}
try|try
block|{
name|Thread
operator|.
name|sleep
argument_list|(
name|BIND_ATTEMPT_DELAY
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|InterruptedException
name|e1
parameter_list|)
block|{
name|Thread
operator|.
name|currentThread
argument_list|()
operator|.
name|interrupt
argument_list|()
expr_stmt|;
throw|throw
name|e
throw|;
block|}
block|}
block|}
block|}
specifier|protected
name|DatagramChannel
name|connect
parameter_list|(
name|DatagramChannel
name|channel
parameter_list|,
name|SocketAddress
name|targetAddress2
parameter_list|)
throws|throws
name|IOException
block|{
comment|// TODO
comment|// connect to default target address to avoid security checks each time
comment|// channel = channel.connect(targetAddress);
return|return
name|channel
return|;
block|}
specifier|protected
name|SocketAddress
name|createLocalAddress
parameter_list|()
block|{
return|return
operator|new
name|InetSocketAddress
argument_list|(
name|port
argument_list|)
return|;
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
if|if
condition|(
name|channel
operator|!=
literal|null
condition|)
block|{
name|channel
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
specifier|protected
name|DatagramHeaderMarshaller
name|createDatagramHeaderMarshaller
parameter_list|()
block|{
return|return
operator|new
name|DatagramHeaderMarshaller
argument_list|()
return|;
block|}
specifier|protected
name|String
name|getProtocolName
parameter_list|()
block|{
return|return
literal|"Udp"
return|;
block|}
specifier|protected
name|String
name|getProtocolUriScheme
parameter_list|()
block|{
return|return
literal|"udp://"
return|;
block|}
specifier|protected
name|SocketAddress
name|getTargetAddress
parameter_list|()
block|{
return|return
name|targetAddress
return|;
block|}
specifier|protected
name|DatagramChannel
name|getChannel
parameter_list|()
block|{
return|return
name|channel
return|;
block|}
specifier|protected
name|void
name|setChannel
parameter_list|(
name|DatagramChannel
name|channel
parameter_list|)
block|{
name|this
operator|.
name|channel
operator|=
name|channel
expr_stmt|;
block|}
specifier|public
name|InetSocketAddress
name|getLocalSocketAddress
parameter_list|()
block|{
if|if
condition|(
name|channel
operator|==
literal|null
condition|)
block|{
return|return
literal|null
return|;
block|}
else|else
block|{
return|return
operator|(
name|InetSocketAddress
operator|)
name|channel
operator|.
name|socket
argument_list|()
operator|.
name|getLocalSocketAddress
argument_list|()
return|;
block|}
block|}
specifier|public
name|String
name|getRemoteAddress
parameter_list|()
block|{
if|if
condition|(
name|targetAddress
operator|!=
literal|null
condition|)
block|{
return|return
literal|""
operator|+
name|targetAddress
return|;
block|}
return|return
literal|null
return|;
block|}
specifier|public
name|int
name|getReceiveCounter
parameter_list|()
block|{
if|if
condition|(
name|commandChannel
operator|==
literal|null
condition|)
block|{
return|return
literal|0
return|;
block|}
return|return
name|commandChannel
operator|.
name|getReceiveCounter
argument_list|()
return|;
block|}
block|}
end_class

end_unit
