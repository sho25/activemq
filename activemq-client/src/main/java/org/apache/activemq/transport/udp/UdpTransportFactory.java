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
name|org
operator|.
name|apache
operator|.
name|activemq
operator|.
name|TransportLoggerSupport
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
name|TransportFactory
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
name|reliable
operator|.
name|DefaultReplayStrategy
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
name|transport
operator|.
name|tcp
operator|.
name|TcpTransportFactory
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
name|URISupport
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
comment|/**  * @author David Martin Clavo david(dot)martin(dot)clavo(at)gmail.com (logging improvement modifications)  *  * @deprecated  */
end_comment

begin_class
annotation|@
name|Deprecated
specifier|public
class|class
name|UdpTransportFactory
extends|extends
name|TransportFactory
block|{
specifier|private
specifier|static
specifier|final
name|Logger
name|log
init|=
name|LoggerFactory
operator|.
name|getLogger
argument_list|(
name|TcpTransportFactory
operator|.
name|class
argument_list|)
decl_stmt|;
annotation|@
name|Override
specifier|public
name|TransportServer
name|doBind
parameter_list|(
specifier|final
name|URI
name|location
parameter_list|)
throws|throws
name|IOException
block|{
try|try
block|{
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|options
init|=
operator|new
name|HashMap
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
argument_list|(
name|URISupport
operator|.
name|parseParameters
argument_list|(
name|location
argument_list|)
argument_list|)
decl_stmt|;
if|if
condition|(
name|options
operator|.
name|containsKey
argument_list|(
literal|"port"
argument_list|)
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"The port property cannot be specified on a UDP server transport - please use the port in the URI syntax"
argument_list|)
throw|;
block|}
name|WireFormat
name|wf
init|=
name|createWireFormat
argument_list|(
name|options
argument_list|)
decl_stmt|;
name|int
name|port
init|=
name|location
operator|.
name|getPort
argument_list|()
decl_stmt|;
name|OpenWireFormat
name|openWireFormat
init|=
name|asOpenWireFormat
argument_list|(
name|wf
argument_list|)
decl_stmt|;
name|UdpTransport
name|transport
init|=
operator|(
name|UdpTransport
operator|)
name|createTransport
argument_list|(
name|location
operator|.
name|getPort
argument_list|()
argument_list|,
name|wf
argument_list|)
decl_stmt|;
name|Transport
name|configuredTransport
init|=
name|configure
argument_list|(
name|transport
argument_list|,
name|wf
argument_list|,
name|options
argument_list|,
literal|true
argument_list|)
decl_stmt|;
name|UdpTransportServer
name|server
init|=
operator|new
name|UdpTransportServer
argument_list|(
name|location
argument_list|,
name|transport
argument_list|,
name|configuredTransport
argument_list|,
name|createReplayStrategy
argument_list|()
argument_list|)
decl_stmt|;
return|return
name|server
return|;
block|}
catch|catch
parameter_list|(
name|URISyntaxException
name|e
parameter_list|)
block|{
throw|throw
name|IOExceptionSupport
operator|.
name|create
argument_list|(
name|e
argument_list|)
throw|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
throw|throw
name|IOExceptionSupport
operator|.
name|create
argument_list|(
name|e
argument_list|)
throw|;
block|}
block|}
annotation|@
name|Override
specifier|public
name|Transport
name|configure
parameter_list|(
name|Transport
name|transport
parameter_list|,
name|WireFormat
name|format
parameter_list|,
name|Map
name|options
parameter_list|)
throws|throws
name|Exception
block|{
return|return
name|configure
argument_list|(
name|transport
argument_list|,
name|format
argument_list|,
name|options
argument_list|,
literal|false
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|Transport
name|compositeConfigure
parameter_list|(
name|Transport
name|transport
parameter_list|,
name|WireFormat
name|format
parameter_list|,
name|Map
name|options
parameter_list|)
block|{
name|IntrospectionSupport
operator|.
name|setProperties
argument_list|(
name|transport
argument_list|,
name|options
argument_list|)
expr_stmt|;
specifier|final
name|UdpTransport
name|udpTransport
init|=
operator|(
name|UdpTransport
operator|)
name|transport
decl_stmt|;
comment|// deal with fragmentation
name|transport
operator|=
operator|new
name|CommandJoiner
argument_list|(
name|transport
argument_list|,
name|asOpenWireFormat
argument_list|(
name|format
argument_list|)
argument_list|)
expr_stmt|;
if|if
condition|(
name|udpTransport
operator|.
name|isTrace
argument_list|()
condition|)
block|{
try|try
block|{
name|transport
operator|=
name|TransportLoggerSupport
operator|.
name|createTransportLogger
argument_list|(
name|transport
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Throwable
name|e
parameter_list|)
block|{
name|log
operator|.
name|error
argument_list|(
literal|"Could not create TransportLogger, reason: "
operator|+
name|e
argument_list|,
name|e
argument_list|)
expr_stmt|;
block|}
block|}
name|transport
operator|=
operator|new
name|InactivityMonitor
argument_list|(
name|transport
argument_list|,
name|format
argument_list|)
expr_stmt|;
if|if
condition|(
name|format
operator|instanceof
name|OpenWireFormat
condition|)
block|{
name|transport
operator|=
name|configureClientSideNegotiator
argument_list|(
name|transport
argument_list|,
name|format
argument_list|,
name|udpTransport
argument_list|)
expr_stmt|;
block|}
return|return
name|transport
return|;
block|}
annotation|@
name|Override
specifier|protected
name|Transport
name|createTransport
parameter_list|(
name|URI
name|location
parameter_list|,
name|WireFormat
name|wf
parameter_list|)
throws|throws
name|UnknownHostException
throws|,
name|IOException
block|{
name|OpenWireFormat
name|wireFormat
init|=
name|asOpenWireFormat
argument_list|(
name|wf
argument_list|)
decl_stmt|;
return|return
operator|new
name|UdpTransport
argument_list|(
name|wireFormat
argument_list|,
name|location
argument_list|)
return|;
block|}
specifier|protected
name|Transport
name|createTransport
parameter_list|(
name|int
name|port
parameter_list|,
name|WireFormat
name|wf
parameter_list|)
throws|throws
name|UnknownHostException
throws|,
name|IOException
block|{
name|OpenWireFormat
name|wireFormat
init|=
name|asOpenWireFormat
argument_list|(
name|wf
argument_list|)
decl_stmt|;
return|return
operator|new
name|UdpTransport
argument_list|(
name|wireFormat
argument_list|,
name|port
argument_list|)
return|;
block|}
comment|/**      * Configures the transport      *      * @param acceptServer true if this transport is used purely as an 'accept'      *                transport for new connections which work like TCP      *                SocketServers where new connections spin up a new separate      *                UDP transport      */
specifier|protected
name|Transport
name|configure
parameter_list|(
name|Transport
name|transport
parameter_list|,
name|WireFormat
name|format
parameter_list|,
name|Map
name|options
parameter_list|,
name|boolean
name|acceptServer
parameter_list|)
throws|throws
name|Exception
block|{
name|IntrospectionSupport
operator|.
name|setProperties
argument_list|(
name|transport
argument_list|,
name|options
argument_list|)
expr_stmt|;
name|UdpTransport
name|udpTransport
init|=
operator|(
name|UdpTransport
operator|)
name|transport
decl_stmt|;
name|OpenWireFormat
name|openWireFormat
init|=
name|asOpenWireFormat
argument_list|(
name|format
argument_list|)
decl_stmt|;
if|if
condition|(
name|udpTransport
operator|.
name|isTrace
argument_list|()
condition|)
block|{
name|transport
operator|=
name|TransportLoggerSupport
operator|.
name|createTransportLogger
argument_list|(
name|transport
argument_list|)
expr_stmt|;
block|}
name|transport
operator|=
operator|new
name|InactivityMonitor
argument_list|(
name|transport
argument_list|,
name|format
argument_list|)
expr_stmt|;
if|if
condition|(
operator|!
name|acceptServer
operator|&&
name|format
operator|instanceof
name|OpenWireFormat
condition|)
block|{
name|transport
operator|=
name|configureClientSideNegotiator
argument_list|(
name|transport
argument_list|,
name|format
argument_list|,
name|udpTransport
argument_list|)
expr_stmt|;
block|}
comment|// deal with fragmentation
if|if
condition|(
name|acceptServer
condition|)
block|{
comment|// lets not support a buffer of messages to enable reliable
comment|// messaging on the 'accept server' transport
name|udpTransport
operator|.
name|setReplayEnabled
argument_list|(
literal|false
argument_list|)
expr_stmt|;
comment|// we don't want to do reliable checks on this transport as we
comment|// delegate to one that does
name|transport
operator|=
operator|new
name|CommandJoiner
argument_list|(
name|transport
argument_list|,
name|openWireFormat
argument_list|)
expr_stmt|;
return|return
name|transport
return|;
block|}
else|else
block|{
name|ReliableTransport
name|reliableTransport
init|=
operator|new
name|ReliableTransport
argument_list|(
name|transport
argument_list|,
name|udpTransport
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
name|createReplayStrategy
argument_list|(
name|replayer
argument_list|)
argument_list|)
expr_stmt|;
comment|// Joiner must be on outside as the inbound messages must be
comment|// processed by the reliable transport first
return|return
operator|new
name|CommandJoiner
argument_list|(
name|reliableTransport
argument_list|,
name|openWireFormat
argument_list|)
return|;
block|}
block|}
specifier|protected
name|ReplayStrategy
name|createReplayStrategy
parameter_list|(
name|Replayer
name|replayer
parameter_list|)
block|{
if|if
condition|(
name|replayer
operator|!=
literal|null
condition|)
block|{
return|return
operator|new
name|DefaultReplayStrategy
argument_list|(
literal|5
argument_list|)
return|;
block|}
return|return
operator|new
name|ExceptionIfDroppedReplayStrategy
argument_list|(
literal|1
argument_list|)
return|;
block|}
specifier|protected
name|ReplayStrategy
name|createReplayStrategy
parameter_list|()
block|{
return|return
operator|new
name|DefaultReplayStrategy
argument_list|(
literal|5
argument_list|)
return|;
block|}
specifier|protected
name|Transport
name|configureClientSideNegotiator
parameter_list|(
name|Transport
name|transport
parameter_list|,
name|WireFormat
name|format
parameter_list|,
specifier|final
name|UdpTransport
name|udpTransport
parameter_list|)
block|{
return|return
operator|new
name|ResponseRedirectInterceptor
argument_list|(
name|transport
argument_list|,
name|udpTransport
argument_list|)
return|;
block|}
specifier|protected
name|OpenWireFormat
name|asOpenWireFormat
parameter_list|(
name|WireFormat
name|wf
parameter_list|)
block|{
name|OpenWireFormat
name|answer
init|=
operator|(
name|OpenWireFormat
operator|)
name|wf
decl_stmt|;
return|return
name|answer
return|;
block|}
block|}
end_class

end_unit

