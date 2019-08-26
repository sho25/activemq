begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *      http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
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
import|import static
name|org
operator|.
name|junit
operator|.
name|Assert
operator|.
name|fail
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
name|Arrays
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Collection
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|UUID
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
name|java
operator|.
name|util
operator|.
name|concurrent
operator|.
name|atomic
operator|.
name|AtomicBoolean
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
name|fusesource
operator|.
name|hawtbuf
operator|.
name|UTF8Buffer
import|;
end_import

begin_import
import|import
name|org
operator|.
name|fusesource
operator|.
name|mqtt
operator|.
name|codec
operator|.
name|CONNACK
import|;
end_import

begin_import
import|import
name|org
operator|.
name|fusesource
operator|.
name|mqtt
operator|.
name|codec
operator|.
name|CONNECT
import|;
end_import

begin_import
import|import
name|org
operator|.
name|fusesource
operator|.
name|mqtt
operator|.
name|codec
operator|.
name|MQTTFrame
import|;
end_import

begin_import
import|import
name|org
operator|.
name|fusesource
operator|.
name|mqtt
operator|.
name|codec
operator|.
name|PINGREQ
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
name|junit
operator|.
name|runner
operator|.
name|RunWith
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|runners
operator|.
name|Parameterized
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|runners
operator|.
name|Parameterized
operator|.
name|Parameters
import|;
end_import

begin_class
annotation|@
name|RunWith
argument_list|(
name|Parameterized
operator|.
name|class
argument_list|)
specifier|public
class|class
name|MQTTWSTransportTest
extends|extends
name|WSTransportTestSupport
block|{
specifier|protected
name|WebSocketClient
name|wsClient
decl_stmt|;
specifier|protected
name|MQTTWSConnection
name|wsMQTTConnection
decl_stmt|;
specifier|protected
name|ClientUpgradeRequest
name|request
decl_stmt|;
specifier|protected
name|boolean
name|partialFrames
decl_stmt|;
annotation|@
name|Parameters
argument_list|(
name|name
operator|=
literal|"{0}"
argument_list|)
specifier|public
specifier|static
name|Collection
argument_list|<
name|Object
index|[]
argument_list|>
name|data
parameter_list|()
block|{
return|return
name|Arrays
operator|.
name|asList
argument_list|(
operator|new
name|Object
index|[]
index|[]
block|{
block|{
literal|"complete-frames"
block|,
literal|false
block|}
block|,
block|{
literal|"partial-frames"
block|,
literal|true
block|}
block|}
argument_list|)
return|;
block|}
specifier|public
name|MQTTWSTransportTest
parameter_list|(
name|String
name|testName
parameter_list|,
name|boolean
name|partialFrames
parameter_list|)
block|{
name|this
operator|.
name|partialFrames
operator|=
name|partialFrames
expr_stmt|;
block|}
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
name|wsClient
operator|=
operator|new
name|WebSocketClient
argument_list|(
operator|new
name|SslContextFactory
operator|.
name|Client
argument_list|(
literal|true
argument_list|)
argument_list|)
expr_stmt|;
name|wsClient
operator|.
name|start
argument_list|()
expr_stmt|;
name|request
operator|=
operator|new
name|ClientUpgradeRequest
argument_list|()
expr_stmt|;
name|request
operator|.
name|setSubProtocols
argument_list|(
literal|"mqttv3.1"
argument_list|)
expr_stmt|;
name|wsMQTTConnection
operator|=
operator|new
name|MQTTWSConnection
argument_list|()
operator|.
name|setWritePartialFrames
argument_list|(
name|partialFrames
argument_list|)
expr_stmt|;
name|wsClient
operator|.
name|connect
argument_list|(
name|wsMQTTConnection
argument_list|,
name|wsConnectUri
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
name|wsMQTTConnection
operator|!=
literal|null
condition|)
block|{
name|wsMQTTConnection
operator|.
name|close
argument_list|()
expr_stmt|;
name|wsMQTTConnection
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
name|testConnectCycles
parameter_list|()
throws|throws
name|Exception
block|{
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
literal|10
condition|;
operator|++
name|i
control|)
block|{
name|testConnect
argument_list|()
expr_stmt|;
name|wsMQTTConnection
operator|=
operator|new
name|MQTTWSConnection
argument_list|()
operator|.
name|setWritePartialFrames
argument_list|(
name|partialFrames
argument_list|)
expr_stmt|;
name|wsClient
operator|.
name|connect
argument_list|(
name|wsMQTTConnection
argument_list|,
name|wsConnectUri
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
block|}
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
name|wsMQTTConnection
operator|.
name|connect
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
literal|1
return|;
block|}
block|}
argument_list|)
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
name|testConnectWithNoHeartbeatsClosesConnection
parameter_list|()
throws|throws
name|Exception
block|{
name|CONNECT
name|command
init|=
operator|new
name|CONNECT
argument_list|()
decl_stmt|;
name|command
operator|.
name|clientId
argument_list|(
operator|new
name|UTF8Buffer
argument_list|(
name|UUID
operator|.
name|randomUUID
argument_list|()
operator|.
name|toString
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|command
operator|.
name|cleanSession
argument_list|(
literal|false
argument_list|)
expr_stmt|;
name|command
operator|.
name|version
argument_list|(
literal|3
argument_list|)
expr_stmt|;
name|command
operator|.
name|keepAlive
argument_list|(
operator|(
name|short
operator|)
literal|2
argument_list|)
expr_stmt|;
name|wsMQTTConnection
operator|.
name|sendFrame
argument_list|(
name|command
operator|.
name|encode
argument_list|()
argument_list|)
expr_stmt|;
name|MQTTFrame
name|received
init|=
name|wsMQTTConnection
operator|.
name|receive
argument_list|(
literal|15
argument_list|,
name|TimeUnit
operator|.
name|SECONDS
argument_list|)
decl_stmt|;
if|if
condition|(
name|received
operator|==
literal|null
operator|||
name|received
operator|.
name|messageType
argument_list|()
operator|!=
name|CONNACK
operator|.
name|TYPE
condition|)
block|{
name|fail
argument_list|(
literal|"Client did not get expected CONNACK"
argument_list|)
expr_stmt|;
block|}
name|assertTrue
argument_list|(
literal|"Connection should open"
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
name|assertTrue
argument_list|(
literal|"Client Connection should close"
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
name|wsMQTTConnection
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
name|testConnectWithHeartbeatsKeepsConnectionAlive
parameter_list|()
throws|throws
name|Exception
block|{
specifier|final
name|AtomicBoolean
name|done
init|=
operator|new
name|AtomicBoolean
argument_list|()
decl_stmt|;
name|CONNECT
name|command
init|=
operator|new
name|CONNECT
argument_list|()
decl_stmt|;
name|command
operator|.
name|clientId
argument_list|(
operator|new
name|UTF8Buffer
argument_list|(
name|UUID
operator|.
name|randomUUID
argument_list|()
operator|.
name|toString
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|command
operator|.
name|cleanSession
argument_list|(
literal|false
argument_list|)
expr_stmt|;
name|command
operator|.
name|version
argument_list|(
literal|3
argument_list|)
expr_stmt|;
name|command
operator|.
name|keepAlive
argument_list|(
operator|(
name|short
operator|)
literal|2
argument_list|)
expr_stmt|;
name|wsMQTTConnection
operator|.
name|sendFrame
argument_list|(
name|command
operator|.
name|encode
argument_list|()
argument_list|)
expr_stmt|;
name|MQTTFrame
name|received
init|=
name|wsMQTTConnection
operator|.
name|receive
argument_list|(
literal|15
argument_list|,
name|TimeUnit
operator|.
name|SECONDS
argument_list|)
decl_stmt|;
if|if
condition|(
name|received
operator|==
literal|null
operator|||
name|received
operator|.
name|messageType
argument_list|()
operator|!=
name|CONNACK
operator|.
name|TYPE
condition|)
block|{
name|fail
argument_list|(
literal|"Client did not get expected CONNACK"
argument_list|)
expr_stmt|;
block|}
name|Thread
name|pinger
init|=
operator|new
name|Thread
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
while|while
condition|(
operator|!
name|done
operator|.
name|get
argument_list|()
condition|)
block|{
name|TimeUnit
operator|.
name|SECONDS
operator|.
name|sleep
argument_list|(
literal|1
argument_list|)
expr_stmt|;
name|wsMQTTConnection
operator|.
name|sendFrame
argument_list|(
operator|new
name|PINGREQ
argument_list|()
operator|.
name|encode
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{                 }
block|}
block|}
argument_list|)
decl_stmt|;
name|pinger
operator|.
name|start
argument_list|()
expr_stmt|;
name|assertTrue
argument_list|(
literal|"Connection should open"
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
name|TimeUnit
operator|.
name|SECONDS
operator|.
name|sleep
argument_list|(
literal|10
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
literal|"Connection should still open"
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
name|done
operator|.
name|set
argument_list|(
literal|true
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
name|assertTrue
argument_list|(
literal|"Client Connection should close"
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
name|wsMQTTConnection
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
block|}
end_class

end_unit

