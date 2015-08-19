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
name|IOException
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Vector
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
name|client
operator|.
name|WebSocketClient
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

begin_comment
comment|/**  * Test that a STOMP WS connection drops if not CONNECT or STOMP frame sent in time.  */
end_comment

begin_class
specifier|public
class|class
name|StompWSConnectionTimeoutTest
extends|extends
name|WSTransportTestSupport
block|{
specifier|protected
name|WebSocketClient
name|wsClient
decl_stmt|;
specifier|protected
name|StompWSConnection
name|wsStompConnection
decl_stmt|;
specifier|protected
name|Vector
argument_list|<
name|Throwable
argument_list|>
name|exceptions
init|=
operator|new
name|Vector
argument_list|<
name|Throwable
argument_list|>
argument_list|()
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
name|wsStompConnection
operator|=
operator|new
name|StompWSConnection
argument_list|()
expr_stmt|;
comment|//        WebSocketClientFactory clientFactory = new WebSocketClientFactory();
comment|//        clientFactory.start();
name|wsClient
operator|=
operator|new
name|WebSocketClient
argument_list|()
expr_stmt|;
name|wsClient
operator|.
name|start
argument_list|()
expr_stmt|;
name|wsClient
operator|.
name|connect
argument_list|(
name|wsStompConnection
argument_list|,
name|wsConnectUri
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
specifier|protected
name|String
name|getConnectorScheme
parameter_list|()
block|{
return|return
literal|"ws"
return|;
block|}
annotation|@
name|Test
argument_list|(
name|timeout
operator|=
literal|90000
argument_list|)
specifier|public
name|void
name|testInactivityMonitor
parameter_list|()
throws|throws
name|Exception
block|{
name|assertTrue
argument_list|(
literal|"one connection"
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
literal|1
operator|==
name|broker
operator|.
name|getTransportConnectorByScheme
argument_list|(
name|getConnectorScheme
argument_list|()
argument_list|)
operator|.
name|connectionCount
argument_list|()
return|;
block|}
block|}
argument_list|,
name|TimeUnit
operator|.
name|SECONDS
operator|.
name|toMillis
argument_list|(
literal|15
argument_list|)
argument_list|,
name|TimeUnit
operator|.
name|MILLISECONDS
operator|.
name|toMillis
argument_list|(
literal|250
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
comment|// and it should be closed due to inactivity
name|assertTrue
argument_list|(
literal|"no dangling connections"
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
literal|0
operator|==
name|broker
operator|.
name|getTransportConnectorByScheme
argument_list|(
name|getConnectorScheme
argument_list|()
argument_list|)
operator|.
name|connectionCount
argument_list|()
return|;
block|}
block|}
argument_list|,
name|TimeUnit
operator|.
name|SECONDS
operator|.
name|toMillis
argument_list|(
literal|60
argument_list|)
argument_list|,
name|TimeUnit
operator|.
name|MILLISECONDS
operator|.
name|toMillis
argument_list|(
literal|500
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
literal|"no exceptions"
argument_list|,
name|exceptions
operator|.
name|isEmpty
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

