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
name|udp
operator|.
name|replay
operator|.
name|DatagramReplayStrategy
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
name|replay
operator|.
name|ExceptionIfDroppedPacketStrategy
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
name|io
operator|.
name|InterruptedIOException
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
name|SocketTimeoutException
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
name|DatagramChannel
import|;
end_import

begin_comment
comment|/**  * An implementation of the {@link Transport} interface using raw UDP  *   * @version $Revision$  */
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
name|Log
name|log
init|=
name|LogFactory
operator|.
name|getLog
argument_list|(
name|UdpTransport
operator|.
name|class
argument_list|)
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
name|DatagramReplayStrategy
name|replayStrategy
init|=
operator|new
name|ExceptionIfDroppedPacketStrategy
argument_list|()
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
name|long
name|maxInactivityDuration
init|=
literal|0
decl_stmt|;
comment|//30000;
specifier|private
name|InetSocketAddress
name|socketAddress
decl_stmt|;
specifier|private
name|DatagramChannel
name|channel
decl_stmt|;
specifier|private
name|boolean
name|trace
init|=
literal|false
decl_stmt|;
specifier|private
name|boolean
name|useLocalHost
init|=
literal|true
decl_stmt|;
specifier|protected
name|UdpTransport
parameter_list|(
name|OpenWireFormat
name|wireFormat
parameter_list|)
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
name|socketAddress
operator|=
name|createAddress
argument_list|(
name|remoteLocation
argument_list|)
expr_stmt|;
block|}
specifier|public
name|UdpTransport
parameter_list|(
name|OpenWireFormat
name|wireFormat
parameter_list|,
name|InetSocketAddress
name|socketAddress
parameter_list|)
block|{
name|this
argument_list|(
name|wireFormat
argument_list|)
expr_stmt|;
name|this
operator|.
name|socketAddress
operator|=
name|socketAddress
expr_stmt|;
block|}
comment|/**      * A one way asynchronous send      */
specifier|public
name|void
name|oneway
parameter_list|(
name|Command
name|command
parameter_list|)
throws|throws
name|IOException
block|{
name|checkStarted
argument_list|(
name|command
argument_list|)
expr_stmt|;
name|commandChannel
operator|.
name|write
argument_list|(
name|command
argument_list|)
expr_stmt|;
block|}
comment|/**      * @return pretty print of 'this'      */
specifier|public
name|String
name|toString
parameter_list|()
block|{
return|return
literal|"udp://"
operator|+
name|socketAddress
return|;
block|}
comment|/**      * reads packets from a Socket      */
specifier|public
name|void
name|run
parameter_list|()
block|{
name|log
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
name|isClosed
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
name|SocketTimeoutException
name|e
parameter_list|)
block|{             }
catch|catch
parameter_list|(
name|InterruptedIOException
name|e
parameter_list|)
block|{             }
catch|catch
parameter_list|(
name|IOException
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
name|log
operator|.
name|warn
argument_list|(
literal|"Caught while closing: "
operator|+
name|e2
operator|+
literal|". Now Closed"
argument_list|,
name|e2
argument_list|)
expr_stmt|;
block|}
name|onException
argument_list|(
name|e
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
name|long
name|getMaxInactivityDuration
parameter_list|()
block|{
return|return
name|maxInactivityDuration
return|;
block|}
comment|/**      * Sets the maximum inactivity duration      */
specifier|public
name|void
name|setMaxInactivityDuration
parameter_list|(
name|long
name|maxInactivityDuration
parameter_list|)
block|{
name|this
operator|.
name|maxInactivityDuration
operator|=
name|maxInactivityDuration
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
block|{
return|return
name|commandChannel
return|;
block|}
comment|/**      * Sets the implementation of the command channel to use.      */
specifier|public
name|void
name|setCommandChannel
parameter_list|(
name|CommandChannel
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
name|DatagramReplayStrategy
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
name|DatagramReplayStrategy
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
name|InetAddress
operator|.
name|getLocalHost
argument_list|()
operator|.
name|getHostName
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
if|if
condition|(
name|socketAddress
operator|!=
literal|null
condition|)
block|{
name|channel
operator|=
name|DatagramChannel
operator|.
name|open
argument_list|()
expr_stmt|;
name|channel
operator|.
name|connect
argument_list|(
name|socketAddress
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|channel
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"No channel configured"
argument_list|)
throw|;
block|}
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
name|commandChannel
operator|=
operator|new
name|CommandChannel
argument_list|(
name|channel
argument_list|,
name|wireFormat
argument_list|,
name|bufferPool
argument_list|,
name|datagramSize
argument_list|,
name|replayStrategy
argument_list|)
expr_stmt|;
name|commandChannel
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
block|}
end_class

end_unit

