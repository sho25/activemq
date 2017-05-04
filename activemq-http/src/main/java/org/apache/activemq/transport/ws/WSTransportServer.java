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
package|;
end_package

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
name|URI
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
name|servlet
operator|.
name|Servlet
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
name|transport
operator|.
name|SocketConnectorFactory
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
name|WebTransportServerSupport
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
name|jetty9
operator|.
name|WSServlet
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
name|eclipse
operator|.
name|jetty
operator|.
name|security
operator|.
name|ConstraintSecurityHandler
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
name|server
operator|.
name|Connector
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
name|server
operator|.
name|Server
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
name|servlet
operator|.
name|ServletContextHandler
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
name|servlet
operator|.
name|ServletHolder
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
comment|/**  * Creates a web server and registers web socket server  *  */
end_comment

begin_class
specifier|public
class|class
name|WSTransportServer
extends|extends
name|WebTransportServerSupport
implements|implements
name|BrokerServiceAware
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
name|WSTransportServer
operator|.
name|class
argument_list|)
decl_stmt|;
specifier|private
name|BrokerService
name|brokerService
decl_stmt|;
specifier|private
name|WSServlet
name|servlet
decl_stmt|;
specifier|public
name|WSTransportServer
parameter_list|(
name|URI
name|location
parameter_list|)
block|{
name|super
argument_list|(
name|location
argument_list|)
expr_stmt|;
name|this
operator|.
name|bindAddress
operator|=
name|location
expr_stmt|;
name|socketConnectorFactory
operator|=
operator|new
name|SocketConnectorFactory
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Override
specifier|protected
name|void
name|doStart
parameter_list|()
throws|throws
name|Exception
block|{
name|createServer
argument_list|()
expr_stmt|;
if|if
condition|(
name|connector
operator|==
literal|null
condition|)
block|{
name|connector
operator|=
name|socketConnectorFactory
operator|.
name|createConnector
argument_list|(
name|server
argument_list|)
expr_stmt|;
block|}
name|URI
name|boundTo
init|=
name|bind
argument_list|()
decl_stmt|;
name|ServletContextHandler
name|contextHandler
init|=
operator|new
name|ServletContextHandler
argument_list|(
name|server
argument_list|,
literal|"/"
argument_list|,
name|ServletContextHandler
operator|.
name|SECURITY
argument_list|)
decl_stmt|;
name|ServletHolder
name|holder
init|=
operator|new
name|ServletHolder
argument_list|()
decl_stmt|;
comment|//AMQ-6182 - disabling trace by default
name|configureTraceMethod
argument_list|(
operator|(
name|ConstraintSecurityHandler
operator|)
name|contextHandler
operator|.
name|getSecurityHandler
argument_list|()
argument_list|,
name|httpOptions
operator|.
name|isEnableTrace
argument_list|()
argument_list|)
expr_stmt|;
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|webSocketOptions
init|=
name|IntrospectionSupport
operator|.
name|extractProperties
argument_list|(
name|transportOptions
argument_list|,
literal|"websocket."
argument_list|)
decl_stmt|;
for|for
control|(
name|Map
operator|.
name|Entry
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|webSocketEntry
range|:
name|webSocketOptions
operator|.
name|entrySet
argument_list|()
control|)
block|{
name|Object
name|value
init|=
name|webSocketEntry
operator|.
name|getValue
argument_list|()
decl_stmt|;
if|if
condition|(
name|value
operator|!=
literal|null
condition|)
block|{
name|holder
operator|.
name|setInitParameter
argument_list|(
name|webSocketEntry
operator|.
name|getKey
argument_list|()
argument_list|,
name|value
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
name|holder
operator|.
name|setServlet
argument_list|(
name|createWSServlet
argument_list|()
argument_list|)
expr_stmt|;
name|contextHandler
operator|.
name|addServlet
argument_list|(
name|holder
argument_list|,
literal|"/"
argument_list|)
expr_stmt|;
name|contextHandler
operator|.
name|setAttribute
argument_list|(
literal|"acceptListener"
argument_list|,
name|getAcceptListener
argument_list|()
argument_list|)
expr_stmt|;
name|server
operator|.
name|start
argument_list|()
expr_stmt|;
comment|// Update the Connect To URI with our actual location in case the configured port
comment|// was set to zero so that we report the actual port we are listening on.
name|int
name|port
init|=
name|getConnectorLocalPort
argument_list|()
decl_stmt|;
if|if
condition|(
name|port
operator|==
operator|-
literal|1
condition|)
block|{
name|port
operator|=
name|boundTo
operator|.
name|getPort
argument_list|()
expr_stmt|;
block|}
name|setConnectURI
argument_list|(
operator|new
name|URI
argument_list|(
name|boundTo
operator|.
name|getScheme
argument_list|()
argument_list|,
name|boundTo
operator|.
name|getUserInfo
argument_list|()
argument_list|,
name|boundTo
operator|.
name|getHost
argument_list|()
argument_list|,
name|port
argument_list|,
name|boundTo
operator|.
name|getPath
argument_list|()
argument_list|,
name|boundTo
operator|.
name|getQuery
argument_list|()
argument_list|,
name|boundTo
operator|.
name|getFragment
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"Listening for connections at {}"
argument_list|,
name|getConnectURI
argument_list|()
argument_list|)
expr_stmt|;
block|}
specifier|private
name|Servlet
name|createWSServlet
parameter_list|()
throws|throws
name|Exception
block|{
name|servlet
operator|=
operator|new
name|WSServlet
argument_list|()
expr_stmt|;
name|servlet
operator|.
name|setTransportOptions
argument_list|(
name|transportOptions
argument_list|)
expr_stmt|;
name|servlet
operator|.
name|setBrokerService
argument_list|(
name|brokerService
argument_list|)
expr_stmt|;
return|return
name|servlet
return|;
block|}
specifier|private
name|int
name|getConnectorLocalPort
parameter_list|()
throws|throws
name|Exception
block|{
return|return
operator|(
name|Integer
operator|)
name|connector
operator|.
name|getClass
argument_list|()
operator|.
name|getMethod
argument_list|(
literal|"getLocalPort"
argument_list|)
operator|.
name|invoke
argument_list|(
name|connector
argument_list|)
return|;
block|}
annotation|@
name|Override
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
name|Server
name|temp
init|=
name|server
decl_stmt|;
name|server
operator|=
literal|null
expr_stmt|;
if|if
condition|(
name|temp
operator|!=
literal|null
condition|)
block|{
name|temp
operator|.
name|stop
argument_list|()
expr_stmt|;
block|}
block|}
annotation|@
name|Override
specifier|public
name|InetSocketAddress
name|getSocketAddress
parameter_list|()
block|{
return|return
literal|null
return|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|setBrokerInfo
parameter_list|(
name|BrokerInfo
name|brokerInfo
parameter_list|)
block|{     }
specifier|protected
name|void
name|setConnector
parameter_list|(
name|Connector
name|connector
parameter_list|)
block|{
name|this
operator|.
name|connector
operator|=
name|connector
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|setTransportOption
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
comment|// String transport from options and
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|socketOptions
init|=
name|IntrospectionSupport
operator|.
name|extractProperties
argument_list|(
name|transportOptions
argument_list|,
literal|"transport."
argument_list|)
decl_stmt|;
name|socketConnectorFactory
operator|.
name|setTransportOptions
argument_list|(
name|socketOptions
argument_list|)
expr_stmt|;
name|transportOptions
operator|.
name|putAll
argument_list|(
name|socketOptions
argument_list|)
expr_stmt|;
name|super
operator|.
name|setTransportOption
argument_list|(
name|transportOptions
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|isSslServer
parameter_list|()
block|{
return|return
literal|false
return|;
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
if|if
condition|(
name|servlet
operator|!=
literal|null
condition|)
block|{
name|servlet
operator|.
name|setBrokerService
argument_list|(
name|brokerService
argument_list|)
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

