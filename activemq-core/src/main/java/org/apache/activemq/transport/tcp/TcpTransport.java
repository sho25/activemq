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
name|tcp
package|;
end_package

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|DataInputStream
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|DataOutputStream
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
name|Socket
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
name|java
operator|.
name|util
operator|.
name|concurrent
operator|.
name|CountDownLatch
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|concurrent
operator|.
name|atomic
operator|.
name|AtomicReference
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
name|util
operator|.
name|IntrospectionSupport
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
name|activemq
operator|.
name|wireformat
operator|.
name|WireFormat
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
comment|/**  * An implementation of the {@link Transport} interface using raw tcp/ip  *   * @version $Revision$  */
end_comment

begin_class
specifier|public
class|class
name|TcpTransport
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
name|LOG
init|=
name|LogFactory
operator|.
name|getLog
argument_list|(
name|TcpTransport
operator|.
name|class
argument_list|)
decl_stmt|;
specifier|protected
specifier|final
name|URI
name|remoteLocation
decl_stmt|;
specifier|protected
specifier|final
name|URI
name|localLocation
decl_stmt|;
specifier|protected
specifier|final
name|WireFormat
name|wireFormat
decl_stmt|;
specifier|protected
name|int
name|connectionTimeout
init|=
literal|30000
decl_stmt|;
specifier|protected
name|int
name|soTimeout
init|=
literal|0
decl_stmt|;
specifier|protected
name|int
name|socketBufferSize
init|=
literal|64
operator|*
literal|1024
decl_stmt|;
specifier|protected
name|int
name|ioBufferSize
init|=
literal|8
operator|*
literal|1024
decl_stmt|;
specifier|protected
name|Socket
name|socket
decl_stmt|;
specifier|protected
name|DataOutputStream
name|dataOut
decl_stmt|;
specifier|protected
name|DataInputStream
name|dataIn
decl_stmt|;
specifier|protected
name|boolean
name|trace
decl_stmt|;
specifier|protected
name|boolean
name|useLocalHost
init|=
literal|true
decl_stmt|;
specifier|protected
name|int
name|minmumWireFormatVersion
decl_stmt|;
specifier|protected
name|SocketFactory
name|socketFactory
decl_stmt|;
specifier|protected
specifier|final
name|AtomicReference
argument_list|<
name|CountDownLatch
argument_list|>
name|stoppedLatch
init|=
operator|new
name|AtomicReference
argument_list|<
name|CountDownLatch
argument_list|>
argument_list|()
decl_stmt|;
specifier|private
name|Map
name|socketOptions
decl_stmt|;
specifier|private
name|Boolean
name|keepAlive
decl_stmt|;
specifier|private
name|Boolean
name|tcpNoDelay
decl_stmt|;
comment|/**      * Connect to a remote Node - e.g. a Broker      *       * @param wireFormat      * @param socketFactory      * @param remoteLocation      * @param localLocation - e.g. local InetAddress and local port      * @throws IOException      * @throws UnknownHostException      */
specifier|public
name|TcpTransport
parameter_list|(
name|WireFormat
name|wireFormat
parameter_list|,
name|SocketFactory
name|socketFactory
parameter_list|,
name|URI
name|remoteLocation
parameter_list|,
name|URI
name|localLocation
parameter_list|)
throws|throws
name|UnknownHostException
throws|,
name|IOException
block|{
name|this
operator|.
name|wireFormat
operator|=
name|wireFormat
expr_stmt|;
name|this
operator|.
name|socketFactory
operator|=
name|socketFactory
expr_stmt|;
try|try
block|{
name|this
operator|.
name|socket
operator|=
name|socketFactory
operator|.
name|createSocket
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|SocketException
name|e
parameter_list|)
block|{
name|this
operator|.
name|socket
operator|=
literal|null
expr_stmt|;
block|}
name|this
operator|.
name|remoteLocation
operator|=
name|remoteLocation
expr_stmt|;
name|this
operator|.
name|localLocation
operator|=
name|localLocation
expr_stmt|;
name|setDaemon
argument_list|(
literal|false
argument_list|)
expr_stmt|;
block|}
comment|/**      * Initialize from a server Socket      *       * @param wireFormat      * @param socket      * @throws IOException      */
specifier|public
name|TcpTransport
parameter_list|(
name|WireFormat
name|wireFormat
parameter_list|,
name|Socket
name|socket
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
name|this
operator|.
name|socket
operator|=
name|socket
expr_stmt|;
name|this
operator|.
name|remoteLocation
operator|=
literal|null
expr_stmt|;
name|this
operator|.
name|localLocation
operator|=
literal|null
expr_stmt|;
name|setDaemon
argument_list|(
literal|true
argument_list|)
expr_stmt|;
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
name|checkStarted
argument_list|()
expr_stmt|;
name|wireFormat
operator|.
name|marshal
argument_list|(
name|command
argument_list|,
name|dataOut
argument_list|)
expr_stmt|;
name|dataOut
operator|.
name|flush
argument_list|()
expr_stmt|;
block|}
comment|/**      * @return pretty print of 'this'      */
specifier|public
name|String
name|toString
parameter_list|()
block|{
return|return
literal|"tcp://"
operator|+
name|socket
operator|.
name|getInetAddress
argument_list|()
operator|+
literal|":"
operator|+
name|socket
operator|.
name|getPort
argument_list|()
return|;
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
literal|"TCP consumer thread starting"
argument_list|)
expr_stmt|;
try|try
block|{
while|while
condition|(
operator|!
name|isStopped
argument_list|()
condition|)
block|{
name|doRun
argument_list|()
expr_stmt|;
block|}
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
name|stoppedLatch
operator|.
name|get
argument_list|()
operator|.
name|countDown
argument_list|()
expr_stmt|;
name|onException
argument_list|(
name|e
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|stoppedLatch
operator|.
name|get
argument_list|()
operator|.
name|countDown
argument_list|()
expr_stmt|;
block|}
block|}
specifier|protected
name|void
name|doRun
parameter_list|()
throws|throws
name|IOException
block|{
try|try
block|{
name|Object
name|command
init|=
name|readCommand
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
block|{         }
catch|catch
parameter_list|(
name|InterruptedIOException
name|e
parameter_list|)
block|{         }
block|}
specifier|protected
name|Object
name|readCommand
parameter_list|()
throws|throws
name|IOException
block|{
return|return
name|wireFormat
operator|.
name|unmarshal
argument_list|(
name|dataIn
argument_list|)
return|;
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
name|int
name|getSocketBufferSize
parameter_list|()
block|{
return|return
name|socketBufferSize
return|;
block|}
comment|/**      * Sets the buffer size to use on the socket      */
specifier|public
name|void
name|setSocketBufferSize
parameter_list|(
name|int
name|socketBufferSize
parameter_list|)
block|{
name|this
operator|.
name|socketBufferSize
operator|=
name|socketBufferSize
expr_stmt|;
block|}
specifier|public
name|int
name|getSoTimeout
parameter_list|()
block|{
return|return
name|soTimeout
return|;
block|}
comment|/**      * Sets the socket timeout      */
specifier|public
name|void
name|setSoTimeout
parameter_list|(
name|int
name|soTimeout
parameter_list|)
block|{
name|this
operator|.
name|soTimeout
operator|=
name|soTimeout
expr_stmt|;
block|}
specifier|public
name|int
name|getConnectionTimeout
parameter_list|()
block|{
return|return
name|connectionTimeout
return|;
block|}
comment|/**      * Sets the timeout used to connect to the socket      */
specifier|public
name|void
name|setConnectionTimeout
parameter_list|(
name|int
name|connectionTimeout
parameter_list|)
block|{
name|this
operator|.
name|connectionTimeout
operator|=
name|connectionTimeout
expr_stmt|;
block|}
specifier|public
name|Boolean
name|getKeepAlive
parameter_list|()
block|{
return|return
name|keepAlive
return|;
block|}
comment|/**      * Enable/disable TCP KEEP_ALIVE mode      */
specifier|public
name|void
name|setKeepAlive
parameter_list|(
name|Boolean
name|keepAlive
parameter_list|)
block|{
name|this
operator|.
name|keepAlive
operator|=
name|keepAlive
expr_stmt|;
block|}
specifier|public
name|Boolean
name|getTcpNoDelay
parameter_list|()
block|{
return|return
name|tcpNoDelay
return|;
block|}
comment|/**      * Enable/disable the TCP_NODELAY option on the socket      */
specifier|public
name|void
name|setTcpNoDelay
parameter_list|(
name|Boolean
name|tcpNoDelay
parameter_list|)
block|{
name|this
operator|.
name|tcpNoDelay
operator|=
name|tcpNoDelay
expr_stmt|;
block|}
comment|/**      * @return the ioBufferSize      */
specifier|public
name|int
name|getIoBufferSize
parameter_list|()
block|{
return|return
name|this
operator|.
name|ioBufferSize
return|;
block|}
comment|/**      * @param ioBufferSize the ioBufferSize to set      */
specifier|public
name|void
name|setIoBufferSize
parameter_list|(
name|int
name|ioBufferSize
parameter_list|)
block|{
name|this
operator|.
name|ioBufferSize
operator|=
name|ioBufferSize
expr_stmt|;
block|}
comment|// Implementation methods
comment|// -------------------------------------------------------------------------
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
comment|/**      * Configures the socket for use      *       * @param sock      * @throws SocketException      */
specifier|protected
name|void
name|initialiseSocket
parameter_list|(
name|Socket
name|sock
parameter_list|)
throws|throws
name|SocketException
block|{
if|if
condition|(
name|socketOptions
operator|!=
literal|null
condition|)
block|{
name|IntrospectionSupport
operator|.
name|setProperties
argument_list|(
name|socket
argument_list|,
name|socketOptions
argument_list|)
expr_stmt|;
block|}
try|try
block|{
name|sock
operator|.
name|setReceiveBufferSize
argument_list|(
name|socketBufferSize
argument_list|)
expr_stmt|;
name|sock
operator|.
name|setSendBufferSize
argument_list|(
name|socketBufferSize
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|SocketException
name|se
parameter_list|)
block|{
name|LOG
operator|.
name|warn
argument_list|(
literal|"Cannot set socket buffer size = "
operator|+
name|socketBufferSize
argument_list|)
expr_stmt|;
name|LOG
operator|.
name|debug
argument_list|(
literal|"Cannot set socket buffer size. Reason: "
operator|+
name|se
argument_list|,
name|se
argument_list|)
expr_stmt|;
block|}
name|sock
operator|.
name|setSoTimeout
argument_list|(
name|soTimeout
argument_list|)
expr_stmt|;
if|if
condition|(
name|keepAlive
operator|!=
literal|null
condition|)
block|{
name|sock
operator|.
name|setKeepAlive
argument_list|(
name|keepAlive
operator|.
name|booleanValue
argument_list|()
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|tcpNoDelay
operator|!=
literal|null
condition|)
block|{
name|sock
operator|.
name|setTcpNoDelay
argument_list|(
name|tcpNoDelay
operator|.
name|booleanValue
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
specifier|protected
name|void
name|doStart
parameter_list|()
throws|throws
name|Exception
block|{
name|connect
argument_list|()
expr_stmt|;
name|stoppedLatch
operator|.
name|set
argument_list|(
operator|new
name|CountDownLatch
argument_list|(
literal|1
argument_list|)
argument_list|)
expr_stmt|;
name|super
operator|.
name|doStart
argument_list|()
expr_stmt|;
block|}
specifier|protected
name|void
name|connect
parameter_list|()
throws|throws
name|Exception
block|{
if|if
condition|(
name|socket
operator|==
literal|null
operator|&&
name|socketFactory
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|IllegalStateException
argument_list|(
literal|"Cannot connect if the socket or socketFactory have not been set"
argument_list|)
throw|;
block|}
name|InetSocketAddress
name|localAddress
init|=
literal|null
decl_stmt|;
name|InetSocketAddress
name|remoteAddress
init|=
literal|null
decl_stmt|;
if|if
condition|(
name|localLocation
operator|!=
literal|null
condition|)
block|{
name|localAddress
operator|=
operator|new
name|InetSocketAddress
argument_list|(
name|InetAddress
operator|.
name|getByName
argument_list|(
name|localLocation
operator|.
name|getHost
argument_list|()
argument_list|)
argument_list|,
name|localLocation
operator|.
name|getPort
argument_list|()
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|remoteLocation
operator|!=
literal|null
condition|)
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
name|remoteAddress
operator|=
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
expr_stmt|;
block|}
if|if
condition|(
name|socket
operator|!=
literal|null
condition|)
block|{
if|if
condition|(
name|localAddress
operator|!=
literal|null
condition|)
block|{
name|socket
operator|.
name|bind
argument_list|(
name|localAddress
argument_list|)
expr_stmt|;
block|}
comment|// If it's a server accepted socket.. we don't need to connect it
comment|// to a remote address.
if|if
condition|(
name|remoteAddress
operator|!=
literal|null
condition|)
block|{
if|if
condition|(
name|connectionTimeout
operator|>=
literal|0
condition|)
block|{
name|socket
operator|.
name|connect
argument_list|(
name|remoteAddress
argument_list|,
name|connectionTimeout
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|socket
operator|.
name|connect
argument_list|(
name|remoteAddress
argument_list|)
expr_stmt|;
block|}
block|}
block|}
else|else
block|{
comment|// For SSL sockets.. you can't create an unconnected socket :(
comment|// This means the timout option are not supported either.
if|if
condition|(
name|localAddress
operator|!=
literal|null
condition|)
block|{
name|socket
operator|=
name|socketFactory
operator|.
name|createSocket
argument_list|(
name|remoteAddress
operator|.
name|getAddress
argument_list|()
argument_list|,
name|remoteAddress
operator|.
name|getPort
argument_list|()
argument_list|,
name|localAddress
operator|.
name|getAddress
argument_list|()
argument_list|,
name|localAddress
operator|.
name|getPort
argument_list|()
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|socket
operator|=
name|socketFactory
operator|.
name|createSocket
argument_list|(
name|remoteAddress
operator|.
name|getAddress
argument_list|()
argument_list|,
name|remoteAddress
operator|.
name|getPort
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
name|initialiseSocket
argument_list|(
name|socket
argument_list|)
expr_stmt|;
name|initializeStreams
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
literal|"Stopping transport "
operator|+
name|this
argument_list|)
expr_stmt|;
block|}
comment|// Closing the streams flush the sockets before closing.. if the socket
comment|// is hung.. then this hangs the close.
comment|// closeStreams();
if|if
condition|(
name|socket
operator|!=
literal|null
condition|)
block|{
name|socket
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
comment|/**      * Override so that stop() blocks until the run thread is no longer running.      */
annotation|@
name|Override
specifier|public
name|void
name|stop
parameter_list|()
throws|throws
name|Exception
block|{
name|super
operator|.
name|stop
argument_list|()
expr_stmt|;
name|CountDownLatch
name|countDownLatch
init|=
name|stoppedLatch
operator|.
name|get
argument_list|()
decl_stmt|;
if|if
condition|(
name|countDownLatch
operator|!=
literal|null
condition|)
block|{
name|countDownLatch
operator|.
name|await
argument_list|()
expr_stmt|;
block|}
block|}
specifier|protected
name|void
name|initializeStreams
parameter_list|()
throws|throws
name|Exception
block|{
name|TcpBufferedInputStream
name|buffIn
init|=
operator|new
name|TcpBufferedInputStream
argument_list|(
name|socket
operator|.
name|getInputStream
argument_list|()
argument_list|,
name|ioBufferSize
argument_list|)
decl_stmt|;
name|this
operator|.
name|dataIn
operator|=
operator|new
name|DataInputStream
argument_list|(
name|buffIn
argument_list|)
expr_stmt|;
name|TcpBufferedOutputStream
name|buffOut
init|=
operator|new
name|TcpBufferedOutputStream
argument_list|(
name|socket
operator|.
name|getOutputStream
argument_list|()
argument_list|,
name|ioBufferSize
argument_list|)
decl_stmt|;
name|this
operator|.
name|dataOut
operator|=
operator|new
name|DataOutputStream
argument_list|(
name|buffOut
argument_list|)
expr_stmt|;
block|}
specifier|protected
name|void
name|closeStreams
parameter_list|()
throws|throws
name|IOException
block|{
if|if
condition|(
name|dataOut
operator|!=
literal|null
condition|)
block|{
name|dataOut
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
if|if
condition|(
name|dataIn
operator|!=
literal|null
condition|)
block|{
name|dataIn
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
specifier|public
name|void
name|setSocketOptions
parameter_list|(
name|Map
name|socketOptions
parameter_list|)
block|{
name|this
operator|.
name|socketOptions
operator|=
operator|new
name|HashMap
argument_list|(
name|socketOptions
argument_list|)
expr_stmt|;
block|}
specifier|public
name|String
name|getRemoteAddress
parameter_list|()
block|{
if|if
condition|(
name|socket
operator|!=
literal|null
condition|)
block|{
return|return
literal|""
operator|+
name|socket
operator|.
name|getRemoteSocketAddress
argument_list|()
return|;
block|}
return|return
literal|null
return|;
block|}
block|}
end_class

end_unit

