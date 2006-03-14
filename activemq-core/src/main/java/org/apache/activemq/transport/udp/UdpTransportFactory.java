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
name|activeio
operator|.
name|command
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
name|command
operator|.
name|WireFormatInfo
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
name|ResponseCorrelator
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
name|TransportFilter
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
name|TransportLogger
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
name|WireFormatNegotiator
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

begin_class
specifier|public
class|class
name|UdpTransportFactory
extends|extends
name|TransportFactory
block|{
specifier|public
name|TransportServer
name|doBind
parameter_list|(
name|String
name|brokerId
parameter_list|,
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
name|options
init|=
operator|new
name|HashMap
argument_list|(
name|URISupport
operator|.
name|parseParamters
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
operator|new
name|UdpTransport
argument_list|(
name|openWireFormat
argument_list|,
name|port
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
name|transport
operator|=
operator|new
name|TransportLogger
argument_list|(
name|transport
argument_list|)
expr_stmt|;
block|}
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
if|if
condition|(
name|udpTransport
operator|.
name|getMaxInactivityDuration
argument_list|()
operator|>
literal|0
condition|)
block|{
name|transport
operator|=
operator|new
name|InactivityMonitor
argument_list|(
name|transport
argument_list|,
name|udpTransport
operator|.
name|getMaxInactivityDuration
argument_list|()
argument_list|)
expr_stmt|;
block|}
comment|// TODO should we have this?
comment|//transport = udpTransport.createFilter(transport);
return|return
name|transport
return|;
block|}
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
name|server
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
operator|new
name|TransportLogger
argument_list|(
name|transport
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
operator|!
name|server
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
if|if
condition|(
name|udpTransport
operator|.
name|getMaxInactivityDuration
argument_list|()
operator|>
literal|0
condition|)
block|{
name|transport
operator|=
operator|new
name|InactivityMonitor
argument_list|(
name|transport
argument_list|,
name|udpTransport
operator|.
name|getMaxInactivityDuration
argument_list|()
argument_list|)
expr_stmt|;
block|}
comment|// add reliabilty
comment|//transport = new ReliableTransport(transport, createReplayStrategy());
comment|// deal with fragmentation
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
name|transport
operator|=
name|udpTransport
operator|.
name|createFilter
argument_list|(
name|transport
argument_list|)
expr_stmt|;
return|return
name|transport
return|;
block|}
specifier|protected
name|ReplayStrategy
name|createReplayStrategy
parameter_list|()
block|{
return|return
operator|new
name|ExceptionIfDroppedReplayStrategy
argument_list|(
literal|1
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
name|TransportFilter
argument_list|(
name|transport
argument_list|)
block|{
specifier|public
name|void
name|onCommand
parameter_list|(
name|Command
name|command
parameter_list|)
block|{
comment|// redirect to the endpoint that the last response came from
name|Endpoint
name|from
init|=
name|command
operator|.
name|getFrom
argument_list|()
decl_stmt|;
name|udpTransport
operator|.
name|setTargetEndpoint
argument_list|(
name|from
argument_list|)
expr_stmt|;
name|super
operator|.
name|onCommand
argument_list|(
name|command
argument_list|)
expr_stmt|;
block|}
block|}
return|;
comment|/*         transport = new WireFormatNegotiator(transport, asOpenWireFormat(format), udpTransport.getMinmumWireFormatVersion()) {             protected void onWireFormatNegotiated(WireFormatInfo info) {                 // lets switch to the target endpoint                 // based on the last packet that was received                 // so that all future requests go to the newly created UDP channel                 Endpoint from = info.getFrom();                 System.out.println("####�setting the client side target to: " + from);                 udpTransport.setTargetEndpoint(from);             }         };         return transport;         */
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

