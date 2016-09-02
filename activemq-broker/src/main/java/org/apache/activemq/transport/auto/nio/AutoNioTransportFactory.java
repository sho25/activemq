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
name|auto
operator|.
name|nio
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
name|Socket
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
name|Set
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
name|broker
operator|.
name|BrokerService
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
name|broker
operator|.
name|BrokerServiceAware
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
name|auto
operator|.
name|AutoTcpTransportServer
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
name|auto
operator|.
name|AutoTransportUtils
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
name|nio
operator|.
name|NIOTransportFactory
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
name|TcpTransport
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

begin_comment
comment|/**  *  *  */
end_comment

begin_class
specifier|public
class|class
name|AutoNioTransportFactory
extends|extends
name|NIOTransportFactory
implements|implements
name|BrokerServiceAware
block|{
specifier|protected
name|BrokerService
name|brokerService
decl_stmt|;
comment|/* (non-Javadoc)      * @see org.apache.activemq.broker.BrokerServiceAware#setBrokerService(org.apache.activemq.broker.BrokerService)      */
annotation|@
name|Override
specifier|public
name|void
name|setBrokerService
parameter_list|(
name|BrokerService
name|brokerService
parameter_list|)
block|{
name|this
operator|.
name|brokerService
operator|=
name|brokerService
expr_stmt|;
block|}
annotation|@
name|Override
specifier|protected
name|AutoTcpTransportServer
name|createTcpTransportServer
parameter_list|(
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
name|AutoTcpTransportServer
argument_list|(
name|this
argument_list|,
name|location
argument_list|,
name|serverSocketFactory
argument_list|,
name|brokerService
argument_list|,
name|enabledProtocols
argument_list|)
block|{
annotation|@
name|Override
specifier|protected
name|TcpTransport
name|createTransport
parameter_list|(
name|Socket
name|socket
parameter_list|,
name|WireFormat
name|format
parameter_list|,
name|TcpTransportFactory
name|detectedTransportFactory
parameter_list|)
throws|throws
name|IOException
block|{
name|TcpTransport
name|nioTransport
init|=
literal|null
decl_stmt|;
if|if
condition|(
name|detectedTransportFactory
operator|.
name|getClass
argument_list|()
operator|.
name|equals
argument_list|(
name|NIOTransportFactory
operator|.
name|class
argument_list|)
condition|)
block|{
name|nioTransport
operator|=
operator|new
name|AutoNIOTransport
argument_list|(
name|format
argument_list|,
name|socket
argument_list|,
name|this
operator|.
name|initBuffer
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|nioTransport
operator|=
name|detectedTransportFactory
operator|.
name|createTransport
argument_list|(
name|format
argument_list|,
name|socket
argument_list|,
name|this
operator|.
name|initBuffer
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|format
operator|.
name|getClass
argument_list|()
operator|.
name|toString
argument_list|()
operator|.
name|contains
argument_list|(
literal|"MQTT"
argument_list|)
condition|)
block|{
if|if
condition|(
operator|!
name|allowLinkStealingSet
condition|)
block|{
name|this
operator|.
name|setAllowLinkStealing
argument_list|(
literal|true
argument_list|)
expr_stmt|;
block|}
block|}
return|return
name|nioTransport
return|;
block|}
block|}
return|;
block|}
name|boolean
name|allowLinkStealingSet
init|=
literal|false
decl_stmt|;
specifier|private
name|Set
argument_list|<
name|String
argument_list|>
name|enabledProtocols
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
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|autoProperties
init|=
name|IntrospectionSupport
operator|.
name|extractProperties
argument_list|(
name|options
argument_list|,
literal|"auto."
argument_list|)
decl_stmt|;
name|this
operator|.
name|enabledProtocols
operator|=
name|AutoTransportUtils
operator|.
name|parseProtocols
argument_list|(
operator|(
name|String
operator|)
name|autoProperties
operator|.
name|get
argument_list|(
literal|"protocols"
argument_list|)
argument_list|)
expr_stmt|;
name|ServerSocketFactory
name|serverSocketFactory
init|=
name|createServerSocketFactory
argument_list|()
decl_stmt|;
name|AutoTcpTransportServer
name|server
init|=
name|createTcpTransportServer
argument_list|(
name|location
argument_list|,
name|serverSocketFactory
argument_list|)
decl_stmt|;
comment|//server.setWireFormatFactory(createWireFormatFactory(options));
name|server
operator|.
name|setWireFormatFactory
argument_list|(
operator|new
name|OpenWireFormatFactory
argument_list|()
argument_list|)
expr_stmt|;
if|if
condition|(
name|options
operator|.
name|get
argument_list|(
literal|"allowLinkStealing"
argument_list|)
operator|!=
literal|null
condition|)
block|{
name|allowLinkStealingSet
operator|=
literal|true
expr_stmt|;
block|}
name|IntrospectionSupport
operator|.
name|setProperties
argument_list|(
name|server
argument_list|,
name|options
argument_list|)
expr_stmt|;
name|server
operator|.
name|setTransportOption
argument_list|(
name|IntrospectionSupport
operator|.
name|extractProperties
argument_list|(
name|options
argument_list|,
literal|"transport."
argument_list|)
argument_list|)
expr_stmt|;
name|server
operator|.
name|setWireFormatOptions
argument_list|(
name|AutoTransportUtils
operator|.
name|extractWireFormatOptions
argument_list|(
name|options
argument_list|)
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
block|}
end_class

end_unit

