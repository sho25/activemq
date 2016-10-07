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
name|amqp
operator|.
name|interop
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
name|assertEquals
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
name|assertFalse
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
name|CountDownLatch
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
name|amqp
operator|.
name|client
operator|.
name|AmqpClient
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
name|amqp
operator|.
name|client
operator|.
name|AmqpClientTestSupport
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
name|amqp
operator|.
name|client
operator|.
name|AmqpConnection
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
name|amqp
operator|.
name|client
operator|.
name|AmqpConnectionListener
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
name|SocketProxy
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

begin_comment
comment|/**  * Test for idle timeout processing using SocketProxy to interrupt coms.  */
end_comment

begin_class
specifier|public
class|class
name|AmqpSocketProxyIdleTimeoutTests
extends|extends
name|AmqpClientTestSupport
block|{
specifier|private
specifier|final
name|int
name|TEST_IDLE_TIMEOUT
init|=
literal|3000
decl_stmt|;
specifier|private
name|SocketProxy
name|socketProxy
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
name|socketProxy
operator|=
operator|new
name|SocketProxy
argument_list|(
name|super
operator|.
name|getBrokerAmqpConnectionURI
argument_list|()
argument_list|)
expr_stmt|;
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
name|socketProxy
operator|!=
literal|null
condition|)
block|{
name|socketProxy
operator|.
name|close
argument_list|()
expr_stmt|;
name|socketProxy
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
name|Override
specifier|public
name|URI
name|getBrokerAmqpConnectionURI
parameter_list|()
block|{
return|return
name|socketProxy
operator|.
name|getUrl
argument_list|()
return|;
block|}
annotation|@
name|Override
specifier|protected
name|String
name|getAdditionalConfig
parameter_list|()
block|{
return|return
literal|"&transport.wireFormat.idleTimeout="
operator|+
name|TEST_IDLE_TIMEOUT
return|;
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
name|testBrokerSendsRequestedHeartbeats
parameter_list|()
throws|throws
name|Exception
block|{
specifier|final
name|CountDownLatch
name|disconnected
init|=
operator|new
name|CountDownLatch
argument_list|(
literal|1
argument_list|)
decl_stmt|;
name|AmqpClient
name|client
init|=
name|createAmqpClient
argument_list|()
decl_stmt|;
name|assertNotNull
argument_list|(
name|client
argument_list|)
expr_stmt|;
name|AmqpConnection
name|connection
init|=
name|trackConnection
argument_list|(
name|client
operator|.
name|createConnection
argument_list|()
argument_list|)
decl_stmt|;
name|connection
operator|.
name|setIdleTimeout
argument_list|(
name|TEST_IDLE_TIMEOUT
argument_list|)
expr_stmt|;
name|assertNotNull
argument_list|(
name|connection
argument_list|)
expr_stmt|;
name|connection
operator|.
name|setListener
argument_list|(
operator|new
name|AmqpConnectionListener
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|void
name|onException
parameter_list|(
name|Throwable
name|ex
parameter_list|)
block|{
name|disconnected
operator|.
name|countDown
argument_list|()
expr_stmt|;
block|}
block|}
argument_list|)
expr_stmt|;
name|connection
operator|.
name|connect
argument_list|()
expr_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|getProxyToBroker
argument_list|()
operator|.
name|getCurrentConnectionsCount
argument_list|()
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|disconnected
operator|.
name|await
argument_list|(
literal|5
argument_list|,
name|TimeUnit
operator|.
name|SECONDS
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|getProxyToBroker
argument_list|()
operator|.
name|getCurrentConnectionsCount
argument_list|()
argument_list|)
expr_stmt|;
name|socketProxy
operator|.
name|pause
argument_list|()
expr_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|getProxyToBroker
argument_list|()
operator|.
name|getCurrentConnectionsCount
argument_list|()
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|disconnected
operator|.
name|await
argument_list|(
literal|10
argument_list|,
name|TimeUnit
operator|.
name|SECONDS
argument_list|)
argument_list|)
expr_stmt|;
name|socketProxy
operator|.
name|goOn
argument_list|()
expr_stmt|;
name|connection
operator|.
name|close
argument_list|()
expr_stmt|;
name|assertTrue
argument_list|(
literal|"Connection should get cleaned up."
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
name|testClientWithoutHeartbeatsGetsDropped
parameter_list|()
throws|throws
name|Exception
block|{
specifier|final
name|CountDownLatch
name|disconnected
init|=
operator|new
name|CountDownLatch
argument_list|(
literal|1
argument_list|)
decl_stmt|;
name|AmqpClient
name|client
init|=
name|createAmqpClient
argument_list|()
decl_stmt|;
name|assertNotNull
argument_list|(
name|client
argument_list|)
expr_stmt|;
name|AmqpConnection
name|connection
init|=
name|trackConnection
argument_list|(
name|client
operator|.
name|createConnection
argument_list|()
argument_list|)
decl_stmt|;
name|connection
operator|.
name|setCloseTimeout
argument_list|(
literal|1000
argument_list|)
expr_stmt|;
comment|// Socket will have silently gone away, don't wait to long.
name|assertNotNull
argument_list|(
name|connection
argument_list|)
expr_stmt|;
name|connection
operator|.
name|setListener
argument_list|(
operator|new
name|AmqpConnectionListener
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|void
name|onException
parameter_list|(
name|Throwable
name|ex
parameter_list|)
block|{
name|disconnected
operator|.
name|countDown
argument_list|()
expr_stmt|;
block|}
block|}
argument_list|)
expr_stmt|;
name|connection
operator|.
name|connect
argument_list|()
expr_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|getProxyToBroker
argument_list|()
operator|.
name|getCurrentConnectionsCount
argument_list|()
argument_list|)
expr_stmt|;
name|socketProxy
operator|.
name|pause
argument_list|()
expr_stmt|;
comment|// Client still sends ok but broker doesn't see them.
name|assertFalse
argument_list|(
name|disconnected
operator|.
name|await
argument_list|(
literal|5
argument_list|,
name|TimeUnit
operator|.
name|SECONDS
argument_list|)
argument_list|)
expr_stmt|;
name|socketProxy
operator|.
name|halfClose
argument_list|()
expr_stmt|;
name|assertTrue
argument_list|(
name|disconnected
operator|.
name|await
argument_list|(
literal|15
argument_list|,
name|TimeUnit
operator|.
name|SECONDS
argument_list|)
argument_list|)
expr_stmt|;
name|socketProxy
operator|.
name|close
argument_list|()
expr_stmt|;
name|connection
operator|.
name|close
argument_list|()
expr_stmt|;
name|assertTrue
argument_list|(
literal|"Connection should get cleaned up."
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
block|}
end_class

end_unit

