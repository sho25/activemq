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
name|ws
operator|.
name|jetty9
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
name|util
operator|.
name|ArrayList
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Collections
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Comparator
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
name|List
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
name|ConcurrentHashMap
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|servlet
operator|.
name|ServletException
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|servlet
operator|.
name|http
operator|.
name|HttpServletRequest
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|servlet
operator|.
name|http
operator|.
name|HttpServletResponse
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
name|TransportAcceptListener
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
name|util
operator|.
name|HttpTransportUtils
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
name|ws
operator|.
name|WSTransportProxy
import|;
end_import

begin_import
import|import
name|org
operator|.
name|eclipse
operator|.
name|jetty
operator|.
name|websocket
operator|.
name|api
operator|.
name|WebSocketListener
import|;
end_import

begin_import
import|import
name|org
operator|.
name|eclipse
operator|.
name|jetty
operator|.
name|websocket
operator|.
name|servlet
operator|.
name|ServletUpgradeRequest
import|;
end_import

begin_import
import|import
name|org
operator|.
name|eclipse
operator|.
name|jetty
operator|.
name|websocket
operator|.
name|servlet
operator|.
name|ServletUpgradeResponse
import|;
end_import

begin_import
import|import
name|org
operator|.
name|eclipse
operator|.
name|jetty
operator|.
name|websocket
operator|.
name|servlet
operator|.
name|WebSocketCreator
import|;
end_import

begin_import
import|import
name|org
operator|.
name|eclipse
operator|.
name|jetty
operator|.
name|websocket
operator|.
name|servlet
operator|.
name|WebSocketServlet
import|;
end_import

begin_import
import|import
name|org
operator|.
name|eclipse
operator|.
name|jetty
operator|.
name|websocket
operator|.
name|servlet
operator|.
name|WebSocketServletFactory
import|;
end_import

begin_comment
comment|/**  * Handle connection upgrade requests and creates web sockets  */
end_comment

begin_class
specifier|public
class|class
name|WSServlet
extends|extends
name|WebSocketServlet
implements|implements
name|BrokerServiceAware
block|{
specifier|private
specifier|static
specifier|final
name|long
name|serialVersionUID
init|=
operator|-
literal|4716657876092884139L
decl_stmt|;
specifier|private
name|TransportAcceptListener
name|listener
decl_stmt|;
specifier|private
specifier|final
specifier|static
name|Map
argument_list|<
name|String
argument_list|,
name|Integer
argument_list|>
name|stompProtocols
init|=
operator|new
name|ConcurrentHashMap
argument_list|<>
argument_list|()
decl_stmt|;
specifier|private
specifier|final
specifier|static
name|Map
argument_list|<
name|String
argument_list|,
name|Integer
argument_list|>
name|mqttProtocols
init|=
operator|new
name|ConcurrentHashMap
argument_list|<>
argument_list|()
decl_stmt|;
specifier|private
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|transportOptions
decl_stmt|;
specifier|private
name|BrokerService
name|brokerService
decl_stmt|;
specifier|private
enum|enum
name|Protocol
block|{
name|MQTT
block|,
name|STOMP
block|,
name|UNKNOWN
block|}
static|static
block|{
name|stompProtocols
operator|.
name|put
argument_list|(
literal|"v12.stomp"
argument_list|,
literal|3
argument_list|)
expr_stmt|;
name|stompProtocols
operator|.
name|put
argument_list|(
literal|"v11.stomp"
argument_list|,
literal|2
argument_list|)
expr_stmt|;
name|stompProtocols
operator|.
name|put
argument_list|(
literal|"v10.stomp"
argument_list|,
literal|1
argument_list|)
expr_stmt|;
name|stompProtocols
operator|.
name|put
argument_list|(
literal|"stomp"
argument_list|,
literal|0
argument_list|)
expr_stmt|;
name|mqttProtocols
operator|.
name|put
argument_list|(
literal|"mqttv3.1"
argument_list|,
literal|1
argument_list|)
expr_stmt|;
name|mqttProtocols
operator|.
name|put
argument_list|(
literal|"mqtt"
argument_list|,
literal|0
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|init
parameter_list|()
throws|throws
name|ServletException
block|{
name|super
operator|.
name|init
argument_list|()
expr_stmt|;
name|listener
operator|=
operator|(
name|TransportAcceptListener
operator|)
name|getServletContext
argument_list|()
operator|.
name|getAttribute
argument_list|(
literal|"acceptListener"
argument_list|)
expr_stmt|;
if|if
condition|(
name|listener
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|ServletException
argument_list|(
literal|"No such attribute 'acceptListener' available in the ServletContext"
argument_list|)
throw|;
block|}
block|}
annotation|@
name|Override
specifier|protected
name|void
name|doGet
parameter_list|(
name|HttpServletRequest
name|request
parameter_list|,
name|HttpServletResponse
name|response
parameter_list|)
throws|throws
name|ServletException
throws|,
name|IOException
block|{
comment|//return empty response - AMQ-6491
block|}
annotation|@
name|Override
specifier|public
name|void
name|configure
parameter_list|(
name|WebSocketServletFactory
name|factory
parameter_list|)
block|{
name|factory
operator|.
name|setCreator
argument_list|(
operator|new
name|WebSocketCreator
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|Object
name|createWebSocket
parameter_list|(
name|ServletUpgradeRequest
name|req
parameter_list|,
name|ServletUpgradeResponse
name|resp
parameter_list|)
block|{
name|WebSocketListener
name|socket
decl_stmt|;
name|Protocol
name|requestedProtocol
init|=
name|Protocol
operator|.
name|UNKNOWN
decl_stmt|;
comment|// When no sub-protocol is requested we default to STOMP for legacy reasons.
if|if
condition|(
operator|!
name|req
operator|.
name|getSubProtocols
argument_list|()
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
for|for
control|(
name|String
name|subProtocol
range|:
name|req
operator|.
name|getSubProtocols
argument_list|()
control|)
block|{
if|if
condition|(
name|subProtocol
operator|.
name|startsWith
argument_list|(
literal|"mqtt"
argument_list|)
condition|)
block|{
name|requestedProtocol
operator|=
name|Protocol
operator|.
name|MQTT
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|subProtocol
operator|.
name|contains
argument_list|(
literal|"stomp"
argument_list|)
condition|)
block|{
name|requestedProtocol
operator|=
name|Protocol
operator|.
name|STOMP
expr_stmt|;
block|}
block|}
block|}
else|else
block|{
name|requestedProtocol
operator|=
name|Protocol
operator|.
name|STOMP
expr_stmt|;
block|}
switch|switch
condition|(
name|requestedProtocol
condition|)
block|{
case|case
name|MQTT
case|:
name|socket
operator|=
operator|new
name|MQTTSocket
argument_list|(
name|HttpTransportUtils
operator|.
name|generateWsRemoteAddress
argument_list|(
name|req
operator|.
name|getHttpServletRequest
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
operator|(
operator|(
name|MQTTSocket
operator|)
name|socket
operator|)
operator|.
name|setTransportOptions
argument_list|(
operator|new
name|HashMap
argument_list|<>
argument_list|(
name|transportOptions
argument_list|)
argument_list|)
expr_stmt|;
operator|(
operator|(
name|MQTTSocket
operator|)
name|socket
operator|)
operator|.
name|setPeerCertificates
argument_list|(
name|req
operator|.
name|getCertificates
argument_list|()
argument_list|)
expr_stmt|;
name|resp
operator|.
name|setAcceptedSubProtocol
argument_list|(
name|getAcceptedSubProtocol
argument_list|(
name|mqttProtocols
argument_list|,
name|req
operator|.
name|getSubProtocols
argument_list|()
argument_list|,
literal|"mqtt"
argument_list|)
argument_list|)
expr_stmt|;
break|break;
case|case
name|UNKNOWN
case|:
name|socket
operator|=
name|findWSTransport
argument_list|(
name|req
argument_list|,
name|resp
argument_list|)
expr_stmt|;
if|if
condition|(
name|socket
operator|!=
literal|null
condition|)
block|{
break|break;
block|}
case|case
name|STOMP
case|:
name|socket
operator|=
operator|new
name|StompSocket
argument_list|(
name|HttpTransportUtils
operator|.
name|generateWsRemoteAddress
argument_list|(
name|req
operator|.
name|getHttpServletRequest
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
operator|(
operator|(
name|StompSocket
operator|)
name|socket
operator|)
operator|.
name|setPeerCertificates
argument_list|(
name|req
operator|.
name|getCertificates
argument_list|()
argument_list|)
expr_stmt|;
name|resp
operator|.
name|setAcceptedSubProtocol
argument_list|(
name|getAcceptedSubProtocol
argument_list|(
name|stompProtocols
argument_list|,
name|req
operator|.
name|getSubProtocols
argument_list|()
argument_list|,
literal|"stomp"
argument_list|)
argument_list|)
expr_stmt|;
break|break;
default|default:
name|socket
operator|=
literal|null
expr_stmt|;
name|listener
operator|.
name|onAcceptError
argument_list|(
operator|new
name|IOException
argument_list|(
literal|"Unknown protocol requested"
argument_list|)
argument_list|)
expr_stmt|;
break|break;
block|}
if|if
condition|(
name|socket
operator|!=
literal|null
condition|)
block|{
name|listener
operator|.
name|onAccept
argument_list|(
operator|(
name|Transport
operator|)
name|socket
argument_list|)
expr_stmt|;
block|}
return|return
name|socket
return|;
block|}
block|}
argument_list|)
expr_stmt|;
block|}
specifier|private
name|WebSocketListener
name|findWSTransport
parameter_list|(
name|ServletUpgradeRequest
name|request
parameter_list|,
name|ServletUpgradeResponse
name|response
parameter_list|)
block|{
name|WSTransportProxy
name|proxy
init|=
literal|null
decl_stmt|;
for|for
control|(
name|String
name|subProtocol
range|:
name|request
operator|.
name|getSubProtocols
argument_list|()
control|)
block|{
try|try
block|{
name|String
name|remoteAddress
init|=
name|HttpTransportUtils
operator|.
name|generateWsRemoteAddress
argument_list|(
name|request
operator|.
name|getHttpServletRequest
argument_list|()
argument_list|,
name|subProtocol
argument_list|)
decl_stmt|;
name|URI
name|remoteURI
init|=
operator|new
name|URI
argument_list|(
name|remoteAddress
argument_list|)
decl_stmt|;
name|TransportFactory
name|factory
init|=
name|TransportFactory
operator|.
name|findTransportFactory
argument_list|(
name|remoteURI
argument_list|)
decl_stmt|;
if|if
condition|(
name|factory
operator|instanceof
name|BrokerServiceAware
condition|)
block|{
operator|(
operator|(
name|BrokerServiceAware
operator|)
name|factory
operator|)
operator|.
name|setBrokerService
argument_list|(
name|brokerService
argument_list|)
expr_stmt|;
block|}
name|Transport
name|transport
init|=
name|factory
operator|.
name|doConnect
argument_list|(
name|remoteURI
argument_list|)
decl_stmt|;
name|proxy
operator|=
operator|new
name|WSTransportProxy
argument_list|(
name|remoteAddress
argument_list|,
name|transport
argument_list|)
expr_stmt|;
name|proxy
operator|.
name|setPeerCertificates
argument_list|(
name|request
operator|.
name|getCertificates
argument_list|()
argument_list|)
expr_stmt|;
name|proxy
operator|.
name|setTransportOptions
argument_list|(
name|transportOptions
argument_list|)
expr_stmt|;
name|response
operator|.
name|setAcceptedSubProtocol
argument_list|(
name|proxy
operator|.
name|getSubProtocol
argument_list|()
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
name|proxy
operator|=
literal|null
expr_stmt|;
comment|// Keep going and try any other sub-protocols present.
continue|continue;
block|}
block|}
return|return
name|proxy
return|;
block|}
specifier|private
name|String
name|getAcceptedSubProtocol
parameter_list|(
specifier|final
name|Map
argument_list|<
name|String
argument_list|,
name|Integer
argument_list|>
name|protocols
parameter_list|,
name|List
argument_list|<
name|String
argument_list|>
name|subProtocols
parameter_list|,
name|String
name|defaultProtocol
parameter_list|)
block|{
name|List
argument_list|<
name|SubProtocol
argument_list|>
name|matchedProtocols
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
if|if
condition|(
name|subProtocols
operator|!=
literal|null
operator|&&
name|subProtocols
operator|.
name|size
argument_list|()
operator|>
literal|0
condition|)
block|{
comment|// detect which subprotocols match accepted protocols and add to the
comment|// list
for|for
control|(
name|String
name|subProtocol
range|:
name|subProtocols
control|)
block|{
name|Integer
name|priority
init|=
name|protocols
operator|.
name|get
argument_list|(
name|subProtocol
argument_list|)
decl_stmt|;
if|if
condition|(
name|subProtocol
operator|!=
literal|null
operator|&&
name|priority
operator|!=
literal|null
condition|)
block|{
comment|// only insert if both subProtocol and priority are not null
name|matchedProtocols
operator|.
name|add
argument_list|(
operator|new
name|SubProtocol
argument_list|(
name|subProtocol
argument_list|,
name|priority
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
comment|// sort the list by priority
if|if
condition|(
name|matchedProtocols
operator|.
name|size
argument_list|()
operator|>
literal|0
condition|)
block|{
name|Collections
operator|.
name|sort
argument_list|(
name|matchedProtocols
argument_list|,
operator|new
name|Comparator
argument_list|<
name|SubProtocol
argument_list|>
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|int
name|compare
parameter_list|(
name|SubProtocol
name|s1
parameter_list|,
name|SubProtocol
name|s2
parameter_list|)
block|{
return|return
name|s2
operator|.
name|priority
operator|.
name|compareTo
argument_list|(
name|s1
operator|.
name|priority
argument_list|)
return|;
block|}
block|}
argument_list|)
expr_stmt|;
return|return
name|matchedProtocols
operator|.
name|get
argument_list|(
literal|0
argument_list|)
operator|.
name|protocol
return|;
block|}
block|}
return|return
name|defaultProtocol
return|;
block|}
specifier|private
class|class
name|SubProtocol
block|{
specifier|private
name|String
name|protocol
decl_stmt|;
specifier|private
name|Integer
name|priority
decl_stmt|;
specifier|public
name|SubProtocol
parameter_list|(
name|String
name|protocol
parameter_list|,
name|Integer
name|priority
parameter_list|)
block|{
name|this
operator|.
name|protocol
operator|=
name|protocol
expr_stmt|;
name|this
operator|.
name|priority
operator|=
name|priority
expr_stmt|;
block|}
block|}
specifier|public
name|void
name|setTransportOptions
parameter_list|(
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|transportOptions
parameter_list|)
block|{
name|this
operator|.
name|transportOptions
operator|=
name|transportOptions
expr_stmt|;
block|}
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
block|}
end_class

end_unit

