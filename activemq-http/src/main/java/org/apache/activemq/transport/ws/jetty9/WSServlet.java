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
name|util
operator|.
name|HttpTransportUtils
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
name|getServletContext
argument_list|()
operator|.
name|getNamedDispatcher
argument_list|(
literal|"default"
argument_list|)
operator|.
name|forward
argument_list|(
name|request
argument_list|,
name|response
argument_list|)
expr_stmt|;
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
name|boolean
name|isMqtt
init|=
literal|false
decl_stmt|;
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
name|subProtocol
operator|.
name|startsWith
argument_list|(
literal|"mqtt"
argument_list|)
expr_stmt|;
name|isMqtt
operator|=
literal|true
expr_stmt|;
block|}
if|if
condition|(
name|isMqtt
condition|)
block|{
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
name|resp
operator|.
name|setAcceptedSubProtocol
argument_list|(
literal|"mqtt"
argument_list|)
expr_stmt|;
block|}
else|else
block|{
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
name|resp
operator|.
name|setAcceptedSubProtocol
argument_list|(
literal|"stomp"
argument_list|)
expr_stmt|;
block|}
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
return|return
name|socket
return|;
block|}
block|}
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

