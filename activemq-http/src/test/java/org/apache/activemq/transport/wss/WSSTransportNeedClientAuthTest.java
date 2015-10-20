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
name|wss
package|;
end_package

begin_import
import|import
name|junit
operator|.
name|framework
operator|.
name|Assert
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
name|BrokerFactory
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
name|transport
operator|.
name|stomp
operator|.
name|Stomp
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
name|stomp
operator|.
name|StompFrame
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
name|MQTTWSConnection
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
name|StompWSConnection
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
name|Wait
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
name|util
operator|.
name|ssl
operator|.
name|SslContextFactory
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
name|Session
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
name|client
operator|.
name|ClientUpgradeRequest
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
name|client
operator|.
name|WebSocketClient
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
name|client
operator|.
name|io
operator|.
name|ConnectPromise
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|After
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|Before
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|Test
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
name|util
operator|.
name|concurrent
operator|.
name|Future
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
import|import static
name|junit
operator|.
name|framework
operator|.
name|TestCase
operator|.
name|assertNotNull
import|;
end_import

begin_import
import|import static
name|junit
operator|.
name|framework
operator|.
name|TestCase
operator|.
name|assertTrue
import|;
end_import

begin_class
specifier|public
class|class
name|WSSTransportNeedClientAuthTest
block|{
specifier|public
specifier|static
specifier|final
name|String
name|KEYSTORE_TYPE
init|=
literal|"jks"
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|String
name|PASSWORD
init|=
literal|"password"
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|String
name|TRUST_KEYSTORE
init|=
literal|"src/test/resources/client.keystore"
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|String
name|KEYSTORE
init|=
literal|"src/test/resources/server.keystore"
decl_stmt|;
specifier|private
name|BrokerService
name|broker
decl_stmt|;
annotation|@
name|Before
specifier|public
name|void
name|setUp
parameter_list|()
throws|throws
name|Exception
block|{
name|broker
operator|=
name|BrokerFactory
operator|.
name|createBroker
argument_list|(
literal|"xbean:activemq-https-need-client-auth.xml"
argument_list|)
expr_stmt|;
name|broker
operator|.
name|setPersistent
argument_list|(
literal|false
argument_list|)
expr_stmt|;
name|broker
operator|.
name|start
argument_list|()
expr_stmt|;
name|broker
operator|.
name|waitUntilStarted
argument_list|()
expr_stmt|;
comment|// these are used for the client side... for the server side, the SSL context
comment|// will be configured through the<sslContext> spring beans
name|System
operator|.
name|setProperty
argument_list|(
literal|"javax.net.ssl.trustStore"
argument_list|,
name|TRUST_KEYSTORE
argument_list|)
expr_stmt|;
name|System
operator|.
name|setProperty
argument_list|(
literal|"javax.net.ssl.trustStorePassword"
argument_list|,
name|PASSWORD
argument_list|)
expr_stmt|;
name|System
operator|.
name|setProperty
argument_list|(
literal|"javax.net.ssl.trustStoreType"
argument_list|,
name|KEYSTORE_TYPE
argument_list|)
expr_stmt|;
name|System
operator|.
name|setProperty
argument_list|(
literal|"javax.net.ssl.keyStore"
argument_list|,
name|KEYSTORE
argument_list|)
expr_stmt|;
name|System
operator|.
name|setProperty
argument_list|(
literal|"javax.net.ssl.keyStorePassword"
argument_list|,
name|PASSWORD
argument_list|)
expr_stmt|;
name|System
operator|.
name|setProperty
argument_list|(
literal|"javax.net.ssl.keyStoreType"
argument_list|,
name|KEYSTORE_TYPE
argument_list|)
expr_stmt|;
block|}
annotation|@
name|After
specifier|public
name|void
name|tearDown
parameter_list|()
throws|throws
name|Exception
block|{
if|if
condition|(
name|broker
operator|!=
literal|null
condition|)
block|{
name|broker
operator|.
name|stop
argument_list|()
expr_stmt|;
name|broker
operator|.
name|waitUntilStopped
argument_list|()
expr_stmt|;
block|}
block|}
annotation|@
name|Test
specifier|public
name|void
name|testStompNeedClientAuth
parameter_list|()
throws|throws
name|Exception
block|{
name|StompWSConnection
name|wsStompConnection
init|=
operator|new
name|StompWSConnection
argument_list|()
decl_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"starting connection"
argument_list|)
expr_stmt|;
name|SslContextFactory
name|factory
init|=
operator|new
name|SslContextFactory
argument_list|()
decl_stmt|;
name|factory
operator|.
name|setKeyStorePath
argument_list|(
name|KEYSTORE
argument_list|)
expr_stmt|;
name|factory
operator|.
name|setKeyStorePassword
argument_list|(
name|PASSWORD
argument_list|)
expr_stmt|;
name|factory
operator|.
name|setKeyStoreType
argument_list|(
name|KEYSTORE_TYPE
argument_list|)
expr_stmt|;
name|factory
operator|.
name|setTrustStorePath
argument_list|(
name|TRUST_KEYSTORE
argument_list|)
expr_stmt|;
name|factory
operator|.
name|setTrustStorePassword
argument_list|(
name|PASSWORD
argument_list|)
expr_stmt|;
name|factory
operator|.
name|setTrustStoreType
argument_list|(
name|KEYSTORE_TYPE
argument_list|)
expr_stmt|;
name|WebSocketClient
name|wsClient
init|=
operator|new
name|WebSocketClient
argument_list|(
name|factory
argument_list|)
decl_stmt|;
name|wsClient
operator|.
name|start
argument_list|()
expr_stmt|;
name|Future
argument_list|<
name|Session
argument_list|>
name|connected
init|=
name|wsClient
operator|.
name|connect
argument_list|(
name|wsStompConnection
argument_list|,
operator|new
name|URI
argument_list|(
literal|"wss://localhost:61618"
argument_list|)
argument_list|)
decl_stmt|;
name|Session
name|sess
init|=
name|connected
operator|.
name|get
argument_list|(
literal|30
argument_list|,
name|TimeUnit
operator|.
name|SECONDS
argument_list|)
decl_stmt|;
name|String
name|connectFrame
init|=
literal|"STOMP\n"
operator|+
literal|"login:system\n"
operator|+
literal|"passcode:manager\n"
operator|+
literal|"accept-version:1.2\n"
operator|+
literal|"host:localhost\n"
operator|+
literal|"\n"
operator|+
name|Stomp
operator|.
name|NULL
decl_stmt|;
name|wsStompConnection
operator|.
name|sendRawFrame
argument_list|(
name|connectFrame
argument_list|)
expr_stmt|;
name|String
name|incoming
init|=
name|wsStompConnection
operator|.
name|receive
argument_list|(
literal|30
argument_list|,
name|TimeUnit
operator|.
name|SECONDS
argument_list|)
decl_stmt|;
name|assertNotNull
argument_list|(
name|incoming
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|incoming
operator|.
name|startsWith
argument_list|(
literal|"CONNECTED"
argument_list|)
argument_list|)
expr_stmt|;
name|wsStompConnection
operator|.
name|sendFrame
argument_list|(
operator|new
name|StompFrame
argument_list|(
name|Stomp
operator|.
name|Commands
operator|.
name|DISCONNECT
argument_list|)
argument_list|)
expr_stmt|;
name|wsStompConnection
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testMQTTNeedClientAuth
parameter_list|()
throws|throws
name|Exception
block|{
name|SslContextFactory
name|factory
init|=
operator|new
name|SslContextFactory
argument_list|()
decl_stmt|;
name|factory
operator|.
name|setKeyStorePath
argument_list|(
name|KEYSTORE
argument_list|)
expr_stmt|;
name|factory
operator|.
name|setKeyStorePassword
argument_list|(
name|PASSWORD
argument_list|)
expr_stmt|;
name|factory
operator|.
name|setKeyStoreType
argument_list|(
name|KEYSTORE_TYPE
argument_list|)
expr_stmt|;
name|factory
operator|.
name|setTrustStorePath
argument_list|(
name|TRUST_KEYSTORE
argument_list|)
expr_stmt|;
name|factory
operator|.
name|setTrustStorePassword
argument_list|(
name|PASSWORD
argument_list|)
expr_stmt|;
name|factory
operator|.
name|setTrustStoreType
argument_list|(
name|KEYSTORE_TYPE
argument_list|)
expr_stmt|;
name|WebSocketClient
name|wsClient
init|=
operator|new
name|WebSocketClient
argument_list|(
name|factory
argument_list|)
decl_stmt|;
name|wsClient
operator|.
name|start
argument_list|()
expr_stmt|;
name|ClientUpgradeRequest
name|request
init|=
operator|new
name|ClientUpgradeRequest
argument_list|()
decl_stmt|;
name|request
operator|.
name|setSubProtocols
argument_list|(
literal|"mqttv3.1"
argument_list|)
expr_stmt|;
name|MQTTWSConnection
name|wsMQTTConnection
init|=
operator|new
name|MQTTWSConnection
argument_list|()
decl_stmt|;
name|wsClient
operator|.
name|connect
argument_list|(
name|wsMQTTConnection
argument_list|,
operator|new
name|URI
argument_list|(
literal|"wss://localhost:61618"
argument_list|)
argument_list|,
name|request
argument_list|)
expr_stmt|;
if|if
condition|(
operator|!
name|wsMQTTConnection
operator|.
name|awaitConnection
argument_list|(
literal|30
argument_list|,
name|TimeUnit
operator|.
name|SECONDS
argument_list|)
condition|)
block|{
throw|throw
operator|new
name|IOException
argument_list|(
literal|"Could not connect to MQTT WS endpoint"
argument_list|)
throw|;
block|}
name|wsMQTTConnection
operator|.
name|connect
argument_list|()
expr_stmt|;
name|assertTrue
argument_list|(
literal|"Client not connected"
argument_list|,
name|wsMQTTConnection
operator|.
name|isConnected
argument_list|()
argument_list|)
expr_stmt|;
name|wsMQTTConnection
operator|.
name|disconnect
argument_list|()
expr_stmt|;
name|wsMQTTConnection
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
end_class

end_unit
