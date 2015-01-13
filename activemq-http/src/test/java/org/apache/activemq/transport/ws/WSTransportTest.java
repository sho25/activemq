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
import|import static
name|org
operator|.
name|junit
operator|.
name|Assert
operator|.
name|assertTrue
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|File
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
name|spring
operator|.
name|SpringSslContext
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
name|StompConnection
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
name|server
operator|.
name|nio
operator|.
name|SelectChannelConnector
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
name|webapp
operator|.
name|WebAppContext
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
name|Ignore
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
name|org
operator|.
name|openqa
operator|.
name|selenium
operator|.
name|By
import|;
end_import

begin_import
import|import
name|org
operator|.
name|openqa
operator|.
name|selenium
operator|.
name|WebDriver
import|;
end_import

begin_import
import|import
name|org
operator|.
name|openqa
operator|.
name|selenium
operator|.
name|WebElement
import|;
end_import

begin_import
import|import
name|org
operator|.
name|openqa
operator|.
name|selenium
operator|.
name|chrome
operator|.
name|ChromeDriver
import|;
end_import

begin_import
import|import
name|org
operator|.
name|openqa
operator|.
name|selenium
operator|.
name|chrome
operator|.
name|ChromeOptions
import|;
end_import

begin_import
import|import
name|org
operator|.
name|openqa
operator|.
name|selenium
operator|.
name|firefox
operator|.
name|FirefoxDriver
import|;
end_import

begin_import
import|import
name|org
operator|.
name|openqa
operator|.
name|selenium
operator|.
name|firefox
operator|.
name|FirefoxProfile
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

begin_class
specifier|public
class|class
name|WSTransportTest
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
name|WSTransportTest
operator|.
name|class
argument_list|)
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|int
name|MESSAGE_COUNT
init|=
literal|1000
decl_stmt|;
specifier|private
name|BrokerService
name|broker
decl_stmt|;
specifier|private
name|Server
name|server
decl_stmt|;
specifier|private
name|WebDriver
name|driver
decl_stmt|;
specifier|private
name|File
name|profileDir
decl_stmt|;
specifier|private
name|String
name|stompUri
decl_stmt|;
specifier|private
name|int
name|proxyPort
init|=
literal|0
decl_stmt|;
specifier|protected
name|String
name|wsUri
decl_stmt|;
specifier|private
name|StompConnection
name|stompConnection
init|=
operator|new
name|StompConnection
argument_list|()
decl_stmt|;
specifier|protected
name|BrokerService
name|createBroker
parameter_list|(
name|boolean
name|deleteMessages
parameter_list|)
throws|throws
name|Exception
block|{
name|BrokerService
name|broker
init|=
name|BrokerFactory
operator|.
name|createBroker
argument_list|(
operator|new
name|URI
argument_list|(
literal|"broker:()/localhost?persistent=false&useJmx=false"
argument_list|)
argument_list|)
decl_stmt|;
name|SpringSslContext
name|context
init|=
operator|new
name|SpringSslContext
argument_list|()
decl_stmt|;
name|context
operator|.
name|setKeyStore
argument_list|(
literal|"src/test/resources/server.keystore"
argument_list|)
expr_stmt|;
name|context
operator|.
name|setKeyStoreKeyPassword
argument_list|(
literal|"password"
argument_list|)
expr_stmt|;
name|context
operator|.
name|setTrustStore
argument_list|(
literal|"src/test/resources/client.keystore"
argument_list|)
expr_stmt|;
name|context
operator|.
name|setTrustStorePassword
argument_list|(
literal|"password"
argument_list|)
expr_stmt|;
name|context
operator|.
name|afterPropertiesSet
argument_list|()
expr_stmt|;
name|broker
operator|.
name|setSslContext
argument_list|(
name|context
argument_list|)
expr_stmt|;
name|stompUri
operator|=
name|broker
operator|.
name|addConnector
argument_list|(
literal|"stomp://localhost:0"
argument_list|)
operator|.
name|getPublishableConnectString
argument_list|()
expr_stmt|;
name|wsUri
operator|=
name|broker
operator|.
name|addConnector
argument_list|(
name|getWSConnectorURI
argument_list|()
argument_list|)
operator|.
name|getPublishableConnectString
argument_list|()
expr_stmt|;
name|broker
operator|.
name|setDeleteAllMessagesOnStartup
argument_list|(
name|deleteMessages
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
return|return
name|broker
return|;
block|}
specifier|protected
name|String
name|getWSConnectorURI
parameter_list|()
block|{
return|return
literal|"ws://127.0.0.1:61623?websocket.maxTextMessageSize=99999&transport.maxIdleTime=1001"
return|;
block|}
specifier|protected
name|Server
name|createWebServer
parameter_list|()
throws|throws
name|Exception
block|{
name|Server
name|server
init|=
operator|new
name|Server
argument_list|()
decl_stmt|;
name|Connector
name|connector
init|=
name|createJettyConnector
argument_list|(
name|server
argument_list|)
decl_stmt|;
name|connector
operator|.
name|setServer
argument_list|(
name|server
argument_list|)
expr_stmt|;
name|WebAppContext
name|context
init|=
operator|new
name|WebAppContext
argument_list|()
decl_stmt|;
name|context
operator|.
name|setResourceBase
argument_list|(
literal|"src/test/webapp"
argument_list|)
expr_stmt|;
name|context
operator|.
name|setContextPath
argument_list|(
literal|"/"
argument_list|)
expr_stmt|;
name|context
operator|.
name|setServer
argument_list|(
name|server
argument_list|)
expr_stmt|;
name|server
operator|.
name|setHandler
argument_list|(
name|context
argument_list|)
expr_stmt|;
name|server
operator|.
name|setConnectors
argument_list|(
operator|new
name|Connector
index|[]
block|{
name|connector
block|}
argument_list|)
expr_stmt|;
name|server
operator|.
name|start
argument_list|()
expr_stmt|;
return|return
name|server
return|;
block|}
specifier|protected
name|int
name|getProxyPort
parameter_list|()
block|{
if|if
condition|(
name|proxyPort
operator|==
literal|0
condition|)
block|{
name|ServerSocket
name|ss
init|=
literal|null
decl_stmt|;
try|try
block|{
name|ss
operator|=
name|ServerSocketFactory
operator|.
name|getDefault
argument_list|()
operator|.
name|createServerSocket
argument_list|(
literal|0
argument_list|)
expr_stmt|;
name|proxyPort
operator|=
name|ss
operator|.
name|getLocalPort
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
comment|// ignore
block|}
finally|finally
block|{
try|try
block|{
if|if
condition|(
name|ss
operator|!=
literal|null
condition|)
block|{
name|ss
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
comment|// ignore
block|}
block|}
block|}
return|return
name|proxyPort
return|;
block|}
specifier|protected
name|Connector
name|createJettyConnector
parameter_list|(
name|Server
name|server
parameter_list|)
block|{
name|SelectChannelConnector
name|connector
init|=
operator|new
name|SelectChannelConnector
argument_list|()
decl_stmt|;
name|connector
operator|.
name|setPort
argument_list|(
name|getProxyPort
argument_list|()
argument_list|)
expr_stmt|;
return|return
name|connector
return|;
block|}
specifier|protected
name|void
name|stopBroker
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
name|broker
operator|=
literal|null
expr_stmt|;
block|}
block|}
annotation|@
name|Before
specifier|public
name|void
name|setUp
parameter_list|()
throws|throws
name|Exception
block|{
name|profileDir
operator|=
operator|new
name|File
argument_list|(
literal|"activemq-data/profiles"
argument_list|)
expr_stmt|;
name|broker
operator|=
name|createBroker
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|stompConnect
argument_list|()
expr_stmt|;
name|server
operator|=
name|createWebServer
argument_list|()
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
try|try
block|{
name|stompDisconnect
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
comment|// Some tests explicitly disconnect from stomp so can ignore
block|}
finally|finally
block|{
try|try
block|{
name|stopBroker
argument_list|()
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
literal|"Error on Broker stop."
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|driver
operator|!=
literal|null
condition|)
block|{
try|try
block|{
name|driver
operator|.
name|quit
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{}
name|driver
operator|=
literal|null
expr_stmt|;
block|}
if|if
condition|(
name|server
operator|!=
literal|null
condition|)
block|{
try|try
block|{
name|server
operator|.
name|stop
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{}
block|}
block|}
block|}
annotation|@
name|Test
specifier|public
name|void
name|testBrokerStart
parameter_list|()
throws|throws
name|Exception
block|{
name|assertTrue
argument_list|(
name|broker
operator|.
name|isStarted
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Ignore
annotation|@
name|Test
specifier|public
name|void
name|testFireFoxWebSockets
parameter_list|()
throws|throws
name|Exception
block|{
name|driver
operator|=
name|createFireFoxWebDriver
argument_list|()
expr_stmt|;
name|doTestWebSockets
argument_list|(
name|driver
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Ignore
annotation|@
name|Test
specifier|public
name|void
name|testChromeWebSockets
parameter_list|()
throws|throws
name|Exception
block|{
name|driver
operator|=
name|createChromeWebDriver
argument_list|()
expr_stmt|;
name|doTestWebSockets
argument_list|(
name|driver
argument_list|)
expr_stmt|;
block|}
specifier|protected
name|WebDriver
name|createChromeWebDriver
parameter_list|()
throws|throws
name|Exception
block|{
name|File
name|profile
init|=
operator|new
name|File
argument_list|(
name|profileDir
argument_list|,
literal|"chrome"
argument_list|)
decl_stmt|;
name|profile
operator|.
name|mkdirs
argument_list|()
expr_stmt|;
name|ChromeOptions
name|options
init|=
operator|new
name|ChromeOptions
argument_list|()
decl_stmt|;
name|options
operator|.
name|addArguments
argument_list|(
literal|"--enable-udd-profiles"
argument_list|,
literal|"--user-data-dir="
operator|+
name|profile
argument_list|,
literal|"--allow-file-access-from-files"
argument_list|)
expr_stmt|;
return|return
operator|new
name|ChromeDriver
argument_list|(
name|options
argument_list|)
return|;
block|}
specifier|protected
name|WebDriver
name|createFireFoxWebDriver
parameter_list|()
throws|throws
name|Exception
block|{
name|File
name|profile
init|=
operator|new
name|File
argument_list|(
name|profileDir
argument_list|,
literal|"firefox"
argument_list|)
decl_stmt|;
name|profile
operator|.
name|mkdirs
argument_list|()
expr_stmt|;
return|return
operator|new
name|FirefoxDriver
argument_list|(
operator|new
name|FirefoxProfile
argument_list|(
name|profile
argument_list|)
argument_list|)
return|;
block|}
specifier|private
name|void
name|stompConnect
parameter_list|()
throws|throws
name|IOException
throws|,
name|URISyntaxException
throws|,
name|UnknownHostException
block|{
name|URI
name|connectUri
init|=
operator|new
name|URI
argument_list|(
name|stompUri
argument_list|)
decl_stmt|;
name|stompConnection
operator|.
name|open
argument_list|(
name|createSocket
argument_list|(
name|connectUri
argument_list|)
argument_list|)
expr_stmt|;
block|}
specifier|private
name|Socket
name|createSocket
parameter_list|(
name|URI
name|connectUri
parameter_list|)
throws|throws
name|IOException
block|{
return|return
operator|new
name|Socket
argument_list|(
literal|"127.0.0.1"
argument_list|,
name|connectUri
operator|.
name|getPort
argument_list|()
argument_list|)
return|;
block|}
specifier|private
name|void
name|stompDisconnect
parameter_list|()
throws|throws
name|IOException
block|{
if|if
condition|(
name|stompConnection
operator|!=
literal|null
condition|)
block|{
name|stompConnection
operator|.
name|close
argument_list|()
expr_stmt|;
name|stompConnection
operator|=
literal|null
expr_stmt|;
block|}
block|}
specifier|protected
name|String
name|getTestURI
parameter_list|()
block|{
name|int
name|port
init|=
name|getProxyPort
argument_list|()
decl_stmt|;
return|return
literal|"http://localhost:"
operator|+
name|port
operator|+
literal|"/websocket.html#"
operator|+
name|wsUri
return|;
block|}
specifier|public
name|void
name|doTestWebSockets
parameter_list|(
name|WebDriver
name|driver
parameter_list|)
throws|throws
name|Exception
block|{
name|driver
operator|.
name|get
argument_list|(
name|getTestURI
argument_list|()
argument_list|)
expr_stmt|;
specifier|final
name|WebElement
name|webStatus
init|=
name|driver
operator|.
name|findElement
argument_list|(
name|By
operator|.
name|id
argument_list|(
literal|"status"
argument_list|)
argument_list|)
decl_stmt|;
specifier|final
name|WebElement
name|webReceived
init|=
name|driver
operator|.
name|findElement
argument_list|(
name|By
operator|.
name|id
argument_list|(
literal|"received"
argument_list|)
argument_list|)
decl_stmt|;
while|while
condition|(
literal|"Loading"
operator|==
name|webStatus
operator|.
name|getText
argument_list|()
condition|)
block|{
name|Thread
operator|.
name|sleep
argument_list|(
literal|100
argument_list|)
expr_stmt|;
block|}
comment|// Skip test if browser does not support websockets..
if|if
condition|(
name|webStatus
operator|.
name|getText
argument_list|()
operator|!=
literal|"No WebSockets"
condition|)
block|{
name|assertTrue
argument_list|(
literal|"Should have connected"
argument_list|,
name|Wait
operator|.
name|waitFor
argument_list|(
operator|new
name|Wait
operator|.
name|Condition
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|boolean
name|isSatisified
parameter_list|()
throws|throws
name|Exception
block|{
return|return
name|webStatus
operator|.
name|getText
argument_list|()
operator|.
name|equals
argument_list|(
literal|"Connected"
argument_list|)
return|;
block|}
block|}
argument_list|)
argument_list|)
expr_stmt|;
name|stompConnection
operator|.
name|connect
argument_list|(
literal|"system"
argument_list|,
literal|"manager"
argument_list|)
expr_stmt|;
name|stompConnection
operator|.
name|send
argument_list|(
literal|"/queue/websocket"
argument_list|,
literal|"Hello"
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
literal|"Should have received message by now."
argument_list|,
name|Wait
operator|.
name|waitFor
argument_list|(
operator|new
name|Wait
operator|.
name|Condition
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|boolean
name|isSatisified
parameter_list|()
throws|throws
name|Exception
block|{
return|return
name|webReceived
operator|.
name|getText
argument_list|()
operator|.
name|equals
argument_list|(
literal|"Hello"
argument_list|)
return|;
block|}
block|}
argument_list|)
argument_list|)
expr_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|1
init|;
name|i
operator|<=
name|MESSAGE_COUNT
condition|;
operator|++
name|i
control|)
block|{
name|stompConnection
operator|.
name|send
argument_list|(
literal|"/queue/websocket"
argument_list|,
literal|"messages #"
operator|+
name|i
argument_list|)
expr_stmt|;
block|}
name|assertTrue
argument_list|(
literal|"Should have received messages by now."
argument_list|,
name|Wait
operator|.
name|waitFor
argument_list|(
operator|new
name|Wait
operator|.
name|Condition
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|boolean
name|isSatisified
parameter_list|()
throws|throws
name|Exception
block|{
return|return
name|webReceived
operator|.
name|getText
argument_list|()
operator|.
name|equals
argument_list|(
literal|"messages #"
operator|+
name|MESSAGE_COUNT
argument_list|)
return|;
block|}
block|}
argument_list|)
argument_list|)
expr_stmt|;
name|Thread
operator|.
name|sleep
argument_list|(
literal|1000
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
literal|"Should have disconnected"
argument_list|,
name|Wait
operator|.
name|waitFor
argument_list|(
operator|new
name|Wait
operator|.
name|Condition
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|boolean
name|isSatisified
parameter_list|()
throws|throws
name|Exception
block|{
return|return
name|webStatus
operator|.
name|getText
argument_list|()
operator|.
name|equals
argument_list|(
literal|"Disconnected"
argument_list|)
return|;
block|}
block|}
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

