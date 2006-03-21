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
name|javax
operator|.
name|net
operator|.
name|ServerSocketFactory
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
name|MutexTransport
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

begin_class
specifier|public
class|class
name|TcpTransportFactory
extends|extends
name|TransportFactory
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
name|TcpTransportFactory
operator|.
name|class
argument_list|)
decl_stmt|;
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
name|TcpTransportServer
name|server
init|=
operator|new
name|TcpTransportServer
argument_list|(
name|location
argument_list|)
decl_stmt|;
name|server
operator|.
name|setWireFormatFactory
argument_list|(
name|createWireFormatFactory
argument_list|(
name|options
argument_list|)
argument_list|)
expr_stmt|;
name|IntrospectionSupport
operator|.
name|setProperties
argument_list|(
name|server
argument_list|,
name|options
argument_list|)
expr_stmt|;
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
name|IntrospectionSupport
operator|.
name|setProperties
argument_list|(
name|transport
argument_list|,
name|options
argument_list|)
expr_stmt|;
name|TcpTransport
name|tcpTransport
init|=
operator|(
name|TcpTransport
operator|)
name|transport
decl_stmt|;
if|if
condition|(
name|tcpTransport
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
name|transport
operator|=
operator|new
name|InactivityMonitor
argument_list|(
name|transport
argument_list|)
expr_stmt|;
comment|// Only need the OpenWireFormat if using openwire
if|if
condition|(
name|format
operator|instanceof
name|OpenWireFormat
condition|)
block|{
name|transport
operator|=
operator|new
name|WireFormatNegotiator
argument_list|(
name|transport
argument_list|,
operator|(
name|OpenWireFormat
operator|)
name|format
argument_list|,
name|tcpTransport
operator|.
name|getMinmumWireFormatVersion
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|transport
operator|=
operator|new
name|MutexTransport
argument_list|(
name|transport
argument_list|)
expr_stmt|;
name|transport
operator|=
operator|new
name|ResponseCorrelator
argument_list|(
name|transport
argument_list|)
expr_stmt|;
return|return
name|transport
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
name|TcpTransport
name|tcpTransport
init|=
operator|(
name|TcpTransport
operator|)
name|transport
decl_stmt|;
if|if
condition|(
name|tcpTransport
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
name|transport
operator|=
operator|new
name|InactivityMonitor
argument_list|(
name|transport
argument_list|)
expr_stmt|;
comment|// Only need the OpenWireFormat if using openwire
if|if
condition|(
name|format
operator|instanceof
name|OpenWireFormat
condition|)
block|{
name|transport
operator|=
operator|new
name|WireFormatNegotiator
argument_list|(
name|transport
argument_list|,
operator|(
name|OpenWireFormat
operator|)
name|format
argument_list|,
name|tcpTransport
operator|.
name|getMinmumWireFormatVersion
argument_list|()
argument_list|)
expr_stmt|;
block|}
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
name|URI
name|localLocation
init|=
literal|null
decl_stmt|;
name|String
name|path
init|=
name|location
operator|.
name|getPath
argument_list|()
decl_stmt|;
comment|// see if the path is a local URI location
if|if
condition|(
name|path
operator|!=
literal|null
operator|&&
name|path
operator|.
name|length
argument_list|()
operator|>
literal|0
condition|)
block|{
name|int
name|localPortIndex
init|=
name|path
operator|.
name|indexOf
argument_list|(
literal|':'
argument_list|)
decl_stmt|;
try|try
block|{
name|Integer
operator|.
name|parseInt
argument_list|(
name|path
operator|.
name|substring
argument_list|(
operator|(
name|localPortIndex
operator|+
literal|1
operator|)
argument_list|,
name|path
operator|.
name|length
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|String
name|localString
init|=
name|location
operator|.
name|getScheme
argument_list|()
operator|+
literal|":/"
operator|+
name|path
decl_stmt|;
name|localLocation
operator|=
operator|new
name|URI
argument_list|(
name|localString
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
name|log
operator|.
name|warn
argument_list|(
literal|"path isn't a valid local location for TcpTransport to use"
argument_list|,
name|e
argument_list|)
expr_stmt|;
block|}
block|}
if|if
condition|(
name|localLocation
operator|!=
literal|null
condition|)
block|{
return|return
operator|new
name|TcpTransport
argument_list|(
name|wf
argument_list|,
name|location
argument_list|,
name|localLocation
argument_list|)
return|;
block|}
return|return
operator|new
name|TcpTransport
argument_list|(
name|wf
argument_list|,
name|location
argument_list|)
return|;
block|}
specifier|protected
name|ServerSocketFactory
name|createServerSocketFactory
parameter_list|()
block|{
return|return
name|ServerSocketFactory
operator|.
name|getDefault
argument_list|()
return|;
block|}
specifier|protected
name|SocketFactory
name|createSocketFactory
parameter_list|()
block|{
return|return
name|SocketFactory
operator|.
name|getDefault
argument_list|()
return|;
block|}
block|}
end_class

end_unit

