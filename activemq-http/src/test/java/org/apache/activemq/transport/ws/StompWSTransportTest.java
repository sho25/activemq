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
name|assertNotNull
import|;
end_import

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
name|IOException
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
name|Executors
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
name|ScheduledExecutorService
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
name|websocket
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
name|WebSocketClientFactory
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
comment|/**  * Test STOMP over WebSockets functionality.  */
end_comment

begin_class
specifier|public
class|class
name|StompWSTransportTest
extends|extends
name|WSTransportTestSupport
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
name|StompWSTransportTest
operator|.
name|class
argument_list|)
decl_stmt|;
specifier|protected
name|WebSocketClient
name|wsClient
decl_stmt|;
specifier|protected
name|StompWSConnection
name|wsStompConnection
decl_stmt|;
annotation|@
name|Override
annotation|@
name|Before
specifier|public
name|void
name|setUp
parameter_list|()
throws|throws
name|Exception
block|{
name|super
operator|.
name|setUp
argument_list|()
expr_stmt|;
name|WebSocketClientFactory
name|clientFactory
init|=
operator|new
name|WebSocketClientFactory
argument_list|()
decl_stmt|;
name|clientFactory
operator|.
name|start
argument_list|()
expr_stmt|;
name|wsClient
operator|=
name|clientFactory
operator|.
name|newWebSocketClient
argument_list|()
expr_stmt|;
name|wsStompConnection
operator|=
operator|new
name|StompWSConnection
argument_list|()
expr_stmt|;
name|wsClient
operator|.
name|open
argument_list|(
name|wsConnectUri
argument_list|,
name|wsStompConnection
argument_list|)
expr_stmt|;
if|if
condition|(
operator|!
name|wsStompConnection
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
literal|"Could not connect to STOMP WS endpoint"
argument_list|)
throw|;
block|}
block|}
annotation|@
name|Override
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
name|wsStompConnection
operator|!=
literal|null
condition|)
block|{
name|wsStompConnection
operator|.
name|close
argument_list|()
expr_stmt|;
name|wsStompConnection
operator|=
literal|null
expr_stmt|;
name|wsClient
operator|=
literal|null
expr_stmt|;
block|}
name|super
operator|.
name|tearDown
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
argument_list|(
name|timeout
operator|=
literal|60000
argument_list|)
specifier|public
name|void
name|testConnect
parameter_list|()
throws|throws
name|Exception
block|{
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
name|assertTrue
argument_list|(
literal|"Connection should close"
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
name|getProxyToBroker
argument_list|()
operator|.
name|getCurrentConnectionsCount
argument_list|()
operator|==
literal|1
return|;
block|}
block|}
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
name|assertTrue
argument_list|(
literal|"Connection should close"
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
name|getProxyToBroker
argument_list|()
operator|.
name|getCurrentConnectionsCount
argument_list|()
operator|==
literal|0
return|;
block|}
block|}
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
argument_list|(
name|timeout
operator|=
literal|60000
argument_list|)
specifier|public
name|void
name|testConnectWithVersionOptions
parameter_list|()
throws|throws
name|Exception
block|{
name|String
name|connectFrame
init|=
literal|"STOMP\n"
operator|+
literal|"login:system\n"
operator|+
literal|"passcode:manager\n"
operator|+
literal|"accept-version:1.0,1.1\n"
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
name|assertTrue
argument_list|(
name|incoming
operator|.
name|indexOf
argument_list|(
literal|"version:1.1"
argument_list|)
operator|>=
literal|0
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|incoming
operator|.
name|indexOf
argument_list|(
literal|"session:"
argument_list|)
operator|>=
literal|0
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
argument_list|(
name|timeout
operator|=
literal|60000
argument_list|)
specifier|public
name|void
name|testRejectInvalidHeartbeats1
parameter_list|()
throws|throws
name|Exception
block|{
name|String
name|connectFrame
init|=
literal|"STOMP\n"
operator|+
literal|"login:system\n"
operator|+
literal|"passcode:manager\n"
operator|+
literal|"accept-version:1.1\n"
operator|+
literal|"heart-beat:0\n"
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
name|assertTrue
argument_list|(
name|incoming
operator|.
name|startsWith
argument_list|(
literal|"ERROR"
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|incoming
operator|.
name|indexOf
argument_list|(
literal|"heart-beat"
argument_list|)
operator|>=
literal|0
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|incoming
operator|.
name|indexOf
argument_list|(
literal|"message:"
argument_list|)
operator|>=
literal|0
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
literal|"Connection should close"
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
name|getProxyToBroker
argument_list|()
operator|.
name|getCurrentConnectionsCount
argument_list|()
operator|==
literal|0
return|;
block|}
block|}
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
argument_list|(
name|timeout
operator|=
literal|60000
argument_list|)
specifier|public
name|void
name|testRejectInvalidHeartbeats2
parameter_list|()
throws|throws
name|Exception
block|{
name|String
name|connectFrame
init|=
literal|"STOMP\n"
operator|+
literal|"login:system\n"
operator|+
literal|"passcode:manager\n"
operator|+
literal|"accept-version:1.1\n"
operator|+
literal|"heart-beat:T,0\n"
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
name|assertTrue
argument_list|(
name|incoming
operator|.
name|startsWith
argument_list|(
literal|"ERROR"
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|incoming
operator|.
name|indexOf
argument_list|(
literal|"heart-beat"
argument_list|)
operator|>=
literal|0
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|incoming
operator|.
name|indexOf
argument_list|(
literal|"message:"
argument_list|)
operator|>=
literal|0
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
literal|"Connection should close"
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
name|getProxyToBroker
argument_list|()
operator|.
name|getCurrentConnectionsCount
argument_list|()
operator|==
literal|0
return|;
block|}
block|}
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
argument_list|(
name|timeout
operator|=
literal|60000
argument_list|)
specifier|public
name|void
name|testRejectInvalidHeartbeats3
parameter_list|()
throws|throws
name|Exception
block|{
name|String
name|connectFrame
init|=
literal|"STOMP\n"
operator|+
literal|"login:system\n"
operator|+
literal|"passcode:manager\n"
operator|+
literal|"accept-version:1.1\n"
operator|+
literal|"heart-beat:100,10,50\n"
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
name|assertTrue
argument_list|(
name|incoming
operator|.
name|startsWith
argument_list|(
literal|"ERROR"
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|incoming
operator|.
name|indexOf
argument_list|(
literal|"heart-beat"
argument_list|)
operator|>=
literal|0
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|incoming
operator|.
name|indexOf
argument_list|(
literal|"message:"
argument_list|)
operator|>=
literal|0
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
literal|"Connection should close"
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
name|getProxyToBroker
argument_list|()
operator|.
name|getCurrentConnectionsCount
argument_list|()
operator|==
literal|0
return|;
block|}
block|}
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
argument_list|(
name|timeout
operator|=
literal|60000
argument_list|)
specifier|public
name|void
name|testHeartbeatsDropsIdleConnection
parameter_list|()
throws|throws
name|Exception
block|{
name|String
name|connectFrame
init|=
literal|"STOMP\n"
operator|+
literal|"login:system\n"
operator|+
literal|"passcode:manager\n"
operator|+
literal|"accept-version:1.1\n"
operator|+
literal|"heart-beat:1000,0\n"
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
name|assertTrue
argument_list|(
name|incoming
operator|.
name|indexOf
argument_list|(
literal|"version:1.1"
argument_list|)
operator|>=
literal|0
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|incoming
operator|.
name|indexOf
argument_list|(
literal|"heart-beat:"
argument_list|)
operator|>=
literal|0
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|incoming
operator|.
name|indexOf
argument_list|(
literal|"session:"
argument_list|)
operator|>=
literal|0
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
literal|"Broker should have closed WS connection:"
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
operator|!
name|wsStompConnection
operator|.
name|isConnected
argument_list|()
return|;
block|}
block|}
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
argument_list|(
name|timeout
operator|=
literal|60000
argument_list|)
specifier|public
name|void
name|testHeartbeatsKeepsConnectionOpen
parameter_list|()
throws|throws
name|Exception
block|{
name|String
name|connectFrame
init|=
literal|"STOMP\n"
operator|+
literal|"login:system\n"
operator|+
literal|"passcode:manager\n"
operator|+
literal|"accept-version:1.1\n"
operator|+
literal|"heart-beat:2000,0\n"
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
name|assertTrue
argument_list|(
name|incoming
operator|.
name|indexOf
argument_list|(
literal|"version:1.1"
argument_list|)
operator|>=
literal|0
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|incoming
operator|.
name|indexOf
argument_list|(
literal|"heart-beat:"
argument_list|)
operator|>=
literal|0
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|incoming
operator|.
name|indexOf
argument_list|(
literal|"session:"
argument_list|)
operator|>=
literal|0
argument_list|)
expr_stmt|;
name|String
name|message
init|=
literal|"SEND\n"
operator|+
literal|"destination:/queue/"
operator|+
name|getTestName
argument_list|()
operator|+
literal|"\n\n"
operator|+
literal|"Hello World"
operator|+
name|Stomp
operator|.
name|NULL
decl_stmt|;
name|wsStompConnection
operator|.
name|sendRawFrame
argument_list|(
name|message
argument_list|)
expr_stmt|;
name|ScheduledExecutorService
name|service
init|=
name|Executors
operator|.
name|newSingleThreadScheduledExecutor
argument_list|()
decl_stmt|;
name|service
operator|.
name|scheduleAtFixedRate
argument_list|(
operator|new
name|Runnable
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|void
name|run
parameter_list|()
block|{
try|try
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"Sending next KeepAlive"
argument_list|)
expr_stmt|;
name|wsStompConnection
operator|.
name|keepAlive
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{                 }
block|}
block|}
argument_list|,
literal|1
argument_list|,
literal|1
argument_list|,
name|TimeUnit
operator|.
name|SECONDS
argument_list|)
expr_stmt|;
name|TimeUnit
operator|.
name|SECONDS
operator|.
name|sleep
argument_list|(
literal|15
argument_list|)
expr_stmt|;
name|String
name|frame
init|=
literal|"SUBSCRIBE\n"
operator|+
literal|"destination:/queue/"
operator|+
name|getTestName
argument_list|()
operator|+
literal|"\n"
operator|+
literal|"id:12345\n"
operator|+
literal|"ack:auto\n\n"
operator|+
name|Stomp
operator|.
name|NULL
decl_stmt|;
name|wsStompConnection
operator|.
name|sendRawFrame
argument_list|(
name|frame
argument_list|)
expr_stmt|;
name|incoming
operator|=
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
expr_stmt|;
name|assertTrue
argument_list|(
name|incoming
operator|.
name|startsWith
argument_list|(
literal|"MESSAGE"
argument_list|)
argument_list|)
expr_stmt|;
name|service
operator|.
name|shutdownNow
argument_list|()
expr_stmt|;
name|service
operator|.
name|awaitTermination
argument_list|(
literal|5
argument_list|,
name|TimeUnit
operator|.
name|SECONDS
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
block|}
block|}
end_class

end_unit

