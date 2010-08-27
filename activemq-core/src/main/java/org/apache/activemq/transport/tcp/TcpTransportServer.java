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
name|InetSocketAddress
import|;
end_import

begin_import
import|import
name|java
operator|.
name|net
operator|.
name|ServerSocket
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
name|URISyntaxException
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
name|BlockingQueue
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
name|LinkedBlockingQueue
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
name|TimeUnit
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|net
operator|.
name|ServerSocketFactory
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
name|ThreadPriorities
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
name|openwire
operator|.
name|OpenWireFormatFactory
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
name|TransportLoggerFactory
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
name|TransportServerThreadSupport
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
name|IOExceptionSupport
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
name|ServiceListener
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
name|util
operator|.
name|ServiceSupport
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
name|activemq
operator|.
name|wireformat
operator|.
name|WireFormatFactory
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
comment|/**  * A TCP based implementation of {@link TransportServer}  *   * @author David Martin Clavo david(dot)martin(dot)clavo(at)gmail.com (logging improvement modifications)  * @version $Revision: 1.1 $  */
end_comment

begin_class
specifier|public
class|class
name|TcpTransportServer
extends|extends
name|TransportServerThreadSupport
implements|implements
name|ServiceListener
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
name|TcpTransportServer
operator|.
name|class
argument_list|)
decl_stmt|;
specifier|protected
name|ServerSocket
name|serverSocket
decl_stmt|;
specifier|protected
name|int
name|backlog
init|=
literal|5000
decl_stmt|;
specifier|protected
name|WireFormatFactory
name|wireFormatFactory
init|=
operator|new
name|OpenWireFormatFactory
argument_list|()
decl_stmt|;
specifier|protected
specifier|final
name|TcpTransportFactory
name|transportFactory
decl_stmt|;
specifier|protected
name|long
name|maxInactivityDuration
init|=
literal|30000
decl_stmt|;
specifier|protected
name|long
name|maxInactivityDurationInitalDelay
init|=
literal|10000
decl_stmt|;
specifier|protected
name|int
name|minmumWireFormatVersion
decl_stmt|;
specifier|protected
name|boolean
name|useQueueForAccept
init|=
literal|true
decl_stmt|;
comment|/**      * trace=true -> the Transport stack where this TcpTransport      * object will be, will have a TransportLogger layer      * trace=false -> the Transport stack where this TcpTransport      * object will be, will NOT have a TransportLogger layer, and therefore      * will never be able to print logging messages.      * This parameter is most probably set in Connection or TransportConnector URIs.      */
specifier|protected
name|boolean
name|trace
init|=
literal|false
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
name|connectionTimeout
init|=
literal|30000
decl_stmt|;
comment|/**      * Name of the LogWriter implementation to use.      * Names are mapped to classes in the resources/META-INF/services/org/apache/activemq/transport/logwriters directory.      * This parameter is most probably set in Connection or TransportConnector URIs.      */
specifier|protected
name|String
name|logWriterName
init|=
name|TransportLoggerFactory
operator|.
name|defaultLogWriterName
decl_stmt|;
comment|/**      * Specifies if the TransportLogger will be manageable by JMX or not.      * Also, as long as there is at least 1 TransportLogger which is manageable,      * a TransportLoggerControl MBean will me created.      */
specifier|protected
name|boolean
name|dynamicManagement
init|=
literal|false
decl_stmt|;
comment|/**      * startLogging=true -> the TransportLogger object of the Transport stack      * will initially write messages to the log.      * startLogging=false -> the TransportLogger object of the Transport stack      * will initially NOT write messages to the log.      * This parameter only has an effect if trace == true.      * This parameter is most probably set in Connection or TransportConnector URIs.      */
specifier|protected
name|boolean
name|startLogging
init|=
literal|true
decl_stmt|;
specifier|protected
specifier|final
name|ServerSocketFactory
name|serverSocketFactory
decl_stmt|;
specifier|protected
name|BlockingQueue
argument_list|<
name|Socket
argument_list|>
name|socketQueue
init|=
operator|new
name|LinkedBlockingQueue
argument_list|<
name|Socket
argument_list|>
argument_list|()
decl_stmt|;
specifier|protected
name|Thread
name|socketHandlerThread
decl_stmt|;
comment|/**      * The maximum number of sockets allowed for this server      */
specifier|protected
name|int
name|maximumConnections
init|=
name|Integer
operator|.
name|MAX_VALUE
decl_stmt|;
specifier|protected
name|int
name|currentTransportCount
init|=
literal|0
decl_stmt|;
specifier|public
name|TcpTransportServer
parameter_list|(
name|TcpTransportFactory
name|transportFactory
parameter_list|,
name|URI
name|location
parameter_list|,
name|ServerSocketFactory
name|serverSocketFactory
parameter_list|)
throws|throws
name|IOException
throws|,
name|URISyntaxException
block|{
name|super
argument_list|(
name|location
argument_list|)
expr_stmt|;
name|this
operator|.
name|transportFactory
operator|=
name|transportFactory
expr_stmt|;
name|this
operator|.
name|serverSocketFactory
operator|=
name|serverSocketFactory
expr_stmt|;
block|}
specifier|public
name|void
name|bind
parameter_list|()
throws|throws
name|IOException
block|{
name|URI
name|bind
init|=
name|getBindLocation
argument_list|()
decl_stmt|;
name|String
name|host
init|=
name|bind
operator|.
name|getHost
argument_list|()
decl_stmt|;
name|host
operator|=
operator|(
name|host
operator|==
literal|null
operator|||
name|host
operator|.
name|length
argument_list|()
operator|==
literal|0
operator|)
condition|?
literal|"localhost"
else|:
name|host
expr_stmt|;
name|InetAddress
name|addr
init|=
name|InetAddress
operator|.
name|getByName
argument_list|(
name|host
argument_list|)
decl_stmt|;
try|try
block|{
name|this
operator|.
name|serverSocket
operator|=
name|serverSocketFactory
operator|.
name|createServerSocket
argument_list|(
name|bind
operator|.
name|getPort
argument_list|()
argument_list|,
name|backlog
argument_list|,
name|addr
argument_list|)
expr_stmt|;
name|configureServerSocket
argument_list|(
name|this
operator|.
name|serverSocket
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
throw|throw
name|IOExceptionSupport
operator|.
name|create
argument_list|(
literal|"Failed to bind to server socket: "
operator|+
name|bind
operator|+
literal|" due to: "
operator|+
name|e
argument_list|,
name|e
argument_list|)
throw|;
block|}
try|try
block|{
name|setConnectURI
argument_list|(
operator|new
name|URI
argument_list|(
name|bind
operator|.
name|getScheme
argument_list|()
argument_list|,
name|bind
operator|.
name|getUserInfo
argument_list|()
argument_list|,
name|resolveHostName
argument_list|(
name|serverSocket
argument_list|,
name|addr
argument_list|)
argument_list|,
name|serverSocket
operator|.
name|getLocalPort
argument_list|()
argument_list|,
name|bind
operator|.
name|getPath
argument_list|()
argument_list|,
name|bind
operator|.
name|getQuery
argument_list|()
argument_list|,
name|bind
operator|.
name|getFragment
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|URISyntaxException
name|e
parameter_list|)
block|{
comment|// it could be that the host name contains invalid characters such
comment|// as _ on unix platforms
comment|// so lets try use the IP address instead
try|try
block|{
name|setConnectURI
argument_list|(
operator|new
name|URI
argument_list|(
name|bind
operator|.
name|getScheme
argument_list|()
argument_list|,
name|bind
operator|.
name|getUserInfo
argument_list|()
argument_list|,
name|addr
operator|.
name|getHostAddress
argument_list|()
argument_list|,
name|serverSocket
operator|.
name|getLocalPort
argument_list|()
argument_list|,
name|bind
operator|.
name|getPath
argument_list|()
argument_list|,
name|bind
operator|.
name|getQuery
argument_list|()
argument_list|,
name|bind
operator|.
name|getFragment
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|URISyntaxException
name|e2
parameter_list|)
block|{
throw|throw
name|IOExceptionSupport
operator|.
name|create
argument_list|(
name|e2
argument_list|)
throw|;
block|}
block|}
block|}
specifier|private
name|void
name|configureServerSocket
parameter_list|(
name|ServerSocket
name|socket
parameter_list|)
throws|throws
name|SocketException
block|{
name|socket
operator|.
name|setSoTimeout
argument_list|(
literal|2000
argument_list|)
expr_stmt|;
if|if
condition|(
name|transportOptions
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
name|transportOptions
argument_list|)
expr_stmt|;
block|}
block|}
comment|/**      * @return Returns the wireFormatFactory.      */
specifier|public
name|WireFormatFactory
name|getWireFormatFactory
parameter_list|()
block|{
return|return
name|wireFormatFactory
return|;
block|}
comment|/**      * @param wireFormatFactory The wireFormatFactory to set.      */
specifier|public
name|void
name|setWireFormatFactory
parameter_list|(
name|WireFormatFactory
name|wireFormatFactory
parameter_list|)
block|{
name|this
operator|.
name|wireFormatFactory
operator|=
name|wireFormatFactory
expr_stmt|;
block|}
comment|/**      * Associates a broker info with the transport server so that the transport      * can do discovery advertisements of the broker.      *       * @param brokerInfo      */
specifier|public
name|void
name|setBrokerInfo
parameter_list|(
name|BrokerInfo
name|brokerInfo
parameter_list|)
block|{     }
specifier|public
name|long
name|getMaxInactivityDuration
parameter_list|()
block|{
return|return
name|maxInactivityDuration
return|;
block|}
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
name|long
name|getMaxInactivityDurationInitalDelay
parameter_list|()
block|{
return|return
name|this
operator|.
name|maxInactivityDurationInitalDelay
return|;
block|}
specifier|public
name|void
name|setMaxInactivityDurationInitalDelay
parameter_list|(
name|long
name|maxInactivityDurationInitalDelay
parameter_list|)
block|{
name|this
operator|.
name|maxInactivityDurationInitalDelay
operator|=
name|maxInactivityDurationInitalDelay
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
name|String
name|getLogWriterName
parameter_list|()
block|{
return|return
name|logWriterName
return|;
block|}
specifier|public
name|void
name|setLogWriterName
parameter_list|(
name|String
name|logFormat
parameter_list|)
block|{
name|this
operator|.
name|logWriterName
operator|=
name|logFormat
expr_stmt|;
block|}
specifier|public
name|boolean
name|isDynamicManagement
parameter_list|()
block|{
return|return
name|dynamicManagement
return|;
block|}
specifier|public
name|void
name|setDynamicManagement
parameter_list|(
name|boolean
name|useJmx
parameter_list|)
block|{
name|this
operator|.
name|dynamicManagement
operator|=
name|useJmx
expr_stmt|;
block|}
specifier|public
name|boolean
name|isStartLogging
parameter_list|()
block|{
return|return
name|startLogging
return|;
block|}
specifier|public
name|void
name|setStartLogging
parameter_list|(
name|boolean
name|startLogging
parameter_list|)
block|{
name|this
operator|.
name|startLogging
operator|=
name|startLogging
expr_stmt|;
block|}
comment|/**      * @return the backlog      */
specifier|public
name|int
name|getBacklog
parameter_list|()
block|{
return|return
name|backlog
return|;
block|}
comment|/**      * @param backlog the backlog to set      */
specifier|public
name|void
name|setBacklog
parameter_list|(
name|int
name|backlog
parameter_list|)
block|{
name|this
operator|.
name|backlog
operator|=
name|backlog
expr_stmt|;
block|}
comment|/**      * @return the useQueueForAccept      */
specifier|public
name|boolean
name|isUseQueueForAccept
parameter_list|()
block|{
return|return
name|useQueueForAccept
return|;
block|}
comment|/**      * @param useQueueForAccept the useQueueForAccept to set      */
specifier|public
name|void
name|setUseQueueForAccept
parameter_list|(
name|boolean
name|useQueueForAccept
parameter_list|)
block|{
name|this
operator|.
name|useQueueForAccept
operator|=
name|useQueueForAccept
expr_stmt|;
block|}
comment|/**      * pull Sockets from the ServerSocket      */
specifier|public
name|void
name|run
parameter_list|()
block|{
while|while
condition|(
operator|!
name|isStopped
argument_list|()
condition|)
block|{
name|Socket
name|socket
init|=
literal|null
decl_stmt|;
try|try
block|{
name|socket
operator|=
name|serverSocket
operator|.
name|accept
argument_list|()
expr_stmt|;
if|if
condition|(
name|socket
operator|!=
literal|null
condition|)
block|{
if|if
condition|(
name|isStopped
argument_list|()
operator|||
name|getAcceptListener
argument_list|()
operator|==
literal|null
condition|)
block|{
name|socket
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
else|else
block|{
if|if
condition|(
name|useQueueForAccept
condition|)
block|{
name|socketQueue
operator|.
name|put
argument_list|(
name|socket
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|handleSocket
argument_list|(
name|socket
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|}
catch|catch
parameter_list|(
name|SocketTimeoutException
name|ste
parameter_list|)
block|{
comment|// expect this to happen
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
if|if
condition|(
operator|!
name|isStopping
argument_list|()
condition|)
block|{
name|onAcceptError
argument_list|(
name|e
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
operator|!
name|isStopped
argument_list|()
condition|)
block|{
name|LOG
operator|.
name|warn
argument_list|(
literal|"run()"
argument_list|,
name|e
argument_list|)
expr_stmt|;
name|onAcceptError
argument_list|(
name|e
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|}
comment|/**      * Allow derived classes to override the Transport implementation that this      * transport server creates.      *       * @param socket      * @param format      * @return      * @throws IOException      */
specifier|protected
name|Transport
name|createTransport
parameter_list|(
name|Socket
name|socket
parameter_list|,
name|WireFormat
name|format
parameter_list|)
throws|throws
name|IOException
block|{
return|return
operator|new
name|TcpTransport
argument_list|(
name|format
argument_list|,
name|socket
argument_list|)
return|;
block|}
comment|/**      * @return pretty print of this      */
specifier|public
name|String
name|toString
parameter_list|()
block|{
return|return
literal|""
operator|+
name|getBindLocation
argument_list|()
return|;
block|}
comment|/**      * @param socket       * @param inetAddress      * @return real hostName      * @throws UnknownHostException      */
specifier|protected
name|String
name|resolveHostName
parameter_list|(
name|ServerSocket
name|socket
parameter_list|,
name|InetAddress
name|bindAddress
parameter_list|)
throws|throws
name|UnknownHostException
block|{
name|String
name|result
init|=
literal|null
decl_stmt|;
if|if
condition|(
name|socket
operator|.
name|isBound
argument_list|()
condition|)
block|{
if|if
condition|(
name|socket
operator|.
name|getInetAddress
argument_list|()
operator|.
name|isAnyLocalAddress
argument_list|()
condition|)
block|{
comment|// make it more human readable and useful, an alternative to 0.0.0.0
name|result
operator|=
name|InetAddress
operator|.
name|getLocalHost
argument_list|()
operator|.
name|getHostName
argument_list|()
expr_stmt|;
block|}
else|else
block|{
name|result
operator|=
name|socket
operator|.
name|getInetAddress
argument_list|()
operator|.
name|getCanonicalHostName
argument_list|()
expr_stmt|;
block|}
block|}
else|else
block|{
name|result
operator|=
name|bindAddress
operator|.
name|getCanonicalHostName
argument_list|()
expr_stmt|;
block|}
return|return
name|result
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
name|useQueueForAccept
condition|)
block|{
name|Runnable
name|run
init|=
operator|new
name|Runnable
argument_list|()
block|{
specifier|public
name|void
name|run
parameter_list|()
block|{
try|try
block|{
while|while
condition|(
operator|!
name|isStopped
argument_list|()
operator|&&
operator|!
name|isStopping
argument_list|()
condition|)
block|{
name|Socket
name|sock
init|=
name|socketQueue
operator|.
name|poll
argument_list|(
literal|1
argument_list|,
name|TimeUnit
operator|.
name|SECONDS
argument_list|)
decl_stmt|;
if|if
condition|(
name|sock
operator|!=
literal|null
condition|)
block|{
name|handleSocket
argument_list|(
name|sock
argument_list|)
expr_stmt|;
block|}
block|}
block|}
catch|catch
parameter_list|(
name|InterruptedException
name|e
parameter_list|)
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"socketQueue interuppted - stopping"
argument_list|)
expr_stmt|;
if|if
condition|(
operator|!
name|isStopping
argument_list|()
condition|)
block|{
name|onAcceptError
argument_list|(
name|e
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|}
decl_stmt|;
name|socketHandlerThread
operator|=
operator|new
name|Thread
argument_list|(
literal|null
argument_list|,
name|run
argument_list|,
literal|"ActiveMQ Transport Server Thread Handler: "
operator|+
name|toString
argument_list|()
argument_list|,
name|getStackSize
argument_list|()
argument_list|)
expr_stmt|;
name|socketHandlerThread
operator|.
name|setDaemon
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|socketHandlerThread
operator|.
name|setPriority
argument_list|(
name|ThreadPriorities
operator|.
name|BROKER_MANAGEMENT
operator|-
literal|1
argument_list|)
expr_stmt|;
name|socketHandlerThread
operator|.
name|start
argument_list|()
expr_stmt|;
block|}
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
name|super
operator|.
name|doStop
argument_list|(
name|stopper
argument_list|)
expr_stmt|;
if|if
condition|(
name|serverSocket
operator|!=
literal|null
condition|)
block|{
name|serverSocket
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
specifier|public
name|InetSocketAddress
name|getSocketAddress
parameter_list|()
block|{
return|return
operator|(
name|InetSocketAddress
operator|)
name|serverSocket
operator|.
name|getLocalSocketAddress
argument_list|()
return|;
block|}
specifier|protected
specifier|final
name|void
name|handleSocket
parameter_list|(
name|Socket
name|socket
parameter_list|)
block|{
try|try
block|{
if|if
condition|(
name|this
operator|.
name|currentTransportCount
operator|>=
name|this
operator|.
name|maximumConnections
condition|)
block|{
throw|throw
operator|new
name|ExceededMaximumConnectionsException
argument_list|(
literal|"Exceeded the maximum "
operator|+
literal|"number of allowed client connections. See the 'maximumConnections' "
operator|+
literal|"property on the TCP transport configuration URI in the ActiveMQ "
operator|+
literal|"configuration file (e.g., activemq.xml)"
argument_list|)
throw|;
block|}
else|else
block|{
name|HashMap
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|options
init|=
operator|new
name|HashMap
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
argument_list|()
decl_stmt|;
name|options
operator|.
name|put
argument_list|(
literal|"maxInactivityDuration"
argument_list|,
name|Long
operator|.
name|valueOf
argument_list|(
name|maxInactivityDuration
argument_list|)
argument_list|)
expr_stmt|;
name|options
operator|.
name|put
argument_list|(
literal|"maxInactivityDurationInitalDelay"
argument_list|,
name|Long
operator|.
name|valueOf
argument_list|(
name|maxInactivityDurationInitalDelay
argument_list|)
argument_list|)
expr_stmt|;
name|options
operator|.
name|put
argument_list|(
literal|"minmumWireFormatVersion"
argument_list|,
name|Integer
operator|.
name|valueOf
argument_list|(
name|minmumWireFormatVersion
argument_list|)
argument_list|)
expr_stmt|;
name|options
operator|.
name|put
argument_list|(
literal|"trace"
argument_list|,
name|Boolean
operator|.
name|valueOf
argument_list|(
name|trace
argument_list|)
argument_list|)
expr_stmt|;
name|options
operator|.
name|put
argument_list|(
literal|"soTimeout"
argument_list|,
name|Integer
operator|.
name|valueOf
argument_list|(
name|soTimeout
argument_list|)
argument_list|)
expr_stmt|;
name|options
operator|.
name|put
argument_list|(
literal|"socketBufferSize"
argument_list|,
name|Integer
operator|.
name|valueOf
argument_list|(
name|socketBufferSize
argument_list|)
argument_list|)
expr_stmt|;
name|options
operator|.
name|put
argument_list|(
literal|"connectionTimeout"
argument_list|,
name|Integer
operator|.
name|valueOf
argument_list|(
name|connectionTimeout
argument_list|)
argument_list|)
expr_stmt|;
name|options
operator|.
name|put
argument_list|(
literal|"logWriterName"
argument_list|,
name|logWriterName
argument_list|)
expr_stmt|;
name|options
operator|.
name|put
argument_list|(
literal|"dynamicManagement"
argument_list|,
name|Boolean
operator|.
name|valueOf
argument_list|(
name|dynamicManagement
argument_list|)
argument_list|)
expr_stmt|;
name|options
operator|.
name|put
argument_list|(
literal|"startLogging"
argument_list|,
name|Boolean
operator|.
name|valueOf
argument_list|(
name|startLogging
argument_list|)
argument_list|)
expr_stmt|;
name|options
operator|.
name|putAll
argument_list|(
name|transportOptions
argument_list|)
expr_stmt|;
name|WireFormat
name|format
init|=
name|wireFormatFactory
operator|.
name|createWireFormat
argument_list|()
decl_stmt|;
name|Transport
name|transport
init|=
name|createTransport
argument_list|(
name|socket
argument_list|,
name|format
argument_list|)
decl_stmt|;
if|if
condition|(
name|transport
operator|instanceof
name|ServiceSupport
condition|)
block|{
operator|(
operator|(
name|ServiceSupport
operator|)
name|transport
operator|)
operator|.
name|addServiceListener
argument_list|(
name|this
argument_list|)
expr_stmt|;
block|}
name|Transport
name|configuredTransport
init|=
name|transportFactory
operator|.
name|serverConfigure
argument_list|(
name|transport
argument_list|,
name|format
argument_list|,
name|options
argument_list|)
decl_stmt|;
name|getAcceptListener
argument_list|()
operator|.
name|onAccept
argument_list|(
name|configuredTransport
argument_list|)
expr_stmt|;
block|}
block|}
catch|catch
parameter_list|(
name|SocketTimeoutException
name|ste
parameter_list|)
block|{
comment|// expect this to happen
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
if|if
condition|(
operator|!
name|isStopping
argument_list|()
condition|)
block|{
name|onAcceptError
argument_list|(
name|e
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
operator|!
name|isStopped
argument_list|()
condition|)
block|{
name|LOG
operator|.
name|warn
argument_list|(
literal|"run()"
argument_list|,
name|e
argument_list|)
expr_stmt|;
name|onAcceptError
argument_list|(
name|e
argument_list|)
expr_stmt|;
block|}
block|}
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
name|getSocketBufferSize
parameter_list|()
block|{
return|return
name|socketBufferSize
return|;
block|}
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
name|getConnectionTimeout
parameter_list|()
block|{
return|return
name|connectionTimeout
return|;
block|}
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
comment|/**      * @return the maximumConnections      */
specifier|public
name|int
name|getMaximumConnections
parameter_list|()
block|{
return|return
name|maximumConnections
return|;
block|}
comment|/**      * @param maximumConnections the maximumConnections to set      */
specifier|public
name|void
name|setMaximumConnections
parameter_list|(
name|int
name|maximumConnections
parameter_list|)
block|{
name|this
operator|.
name|maximumConnections
operator|=
name|maximumConnections
expr_stmt|;
block|}
specifier|public
name|void
name|started
parameter_list|(
name|Service
name|service
parameter_list|)
block|{
name|this
operator|.
name|currentTransportCount
operator|++
expr_stmt|;
block|}
specifier|public
name|void
name|stopped
parameter_list|(
name|Service
name|service
parameter_list|)
block|{
name|this
operator|.
name|currentTransportCount
operator|--
expr_stmt|;
block|}
block|}
end_class

end_unit

