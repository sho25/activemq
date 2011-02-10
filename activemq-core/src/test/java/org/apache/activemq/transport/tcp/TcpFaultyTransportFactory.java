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
name|ServerSocket
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
name|ServerSocketTstFactory
import|;
end_import

begin_comment
comment|/**  * Automatically generated socket.close() calls to simulate network faults  */
end_comment

begin_class
specifier|public
class|class
name|TcpFaultyTransportFactory
extends|extends
name|TcpTransportFactory
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
name|TcpFaultyTransportFactory
operator|.
name|class
argument_list|)
decl_stmt|;
specifier|protected
name|TcpFaultyTransport
name|createTcpFaultyTransport
parameter_list|(
name|WireFormat
name|wf
parameter_list|,
name|SocketFactory
name|socketFactory
parameter_list|,
name|URI
name|location
parameter_list|,
name|URI
name|localLocation
parameter_list|)
throws|throws
name|UnknownHostException
throws|,
name|IOException
block|{
return|return
operator|new
name|TcpFaultyTransport
argument_list|(
name|wf
argument_list|,
name|socketFactory
argument_list|,
name|location
argument_list|,
name|localLocation
argument_list|)
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
name|localPortIndex
operator|+
literal|1
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
name|LOG
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
name|SocketFactory
name|socketFactory
init|=
name|createSocketFactory
argument_list|()
decl_stmt|;
return|return
name|createTcpFaultyTransport
argument_list|(
name|wf
argument_list|,
name|socketFactory
argument_list|,
name|location
argument_list|,
name|localLocation
argument_list|)
return|;
block|}
specifier|protected
name|TcpFaultyTransportServer
name|createTcpFaultyTransportServer
parameter_list|(
specifier|final
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
return|return
operator|new
name|TcpFaultyTransportServer
argument_list|(
name|this
argument_list|,
name|location
argument_list|,
name|serverSocketFactory
argument_list|)
return|;
block|}
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
name|ServerSocketFactory
name|serverSocketFactory
init|=
name|createServerSocketFactory
argument_list|()
decl_stmt|;
name|TcpFaultyTransportServer
name|server
init|=
name|createTcpFaultyTransportServer
argument_list|(
name|location
argument_list|,
name|serverSocketFactory
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
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|transportOptions
init|=
name|IntrospectionSupport
operator|.
name|extractProperties
argument_list|(
name|options
argument_list|,
literal|"transport."
argument_list|)
decl_stmt|;
name|server
operator|.
name|setTransportOption
argument_list|(
name|transportOptions
argument_list|)
expr_stmt|;
name|server
operator|.
name|bind
argument_list|()
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
specifier|protected
name|SocketFactory
name|createSocketFactory
parameter_list|()
throws|throws
name|IOException
block|{
return|return
name|SocketTstFactory
operator|.
name|getDefault
argument_list|()
return|;
block|}
specifier|protected
name|ServerSocketFactory
name|createServerSocketFactory
parameter_list|()
throws|throws
name|IOException
block|{
return|return
name|ServerSocketTstFactory
operator|.
name|getDefault
argument_list|()
return|;
block|}
block|}
end_class

end_unit

