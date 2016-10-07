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
name|transport
operator|.
name|amqp
operator|.
name|client
operator|.
name|AmqpValidator
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
name|apache
operator|.
name|qpid
operator|.
name|proton
operator|.
name|engine
operator|.
name|Connection
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

begin_comment
comment|/**  * Tests that cover broker behavior when the client requests heartbeats  */
end_comment

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
name|AmqpClientRequestsHeartbeatsTest
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
annotation|@
name|Parameters
argument_list|(
name|name
operator|=
literal|"connector={0}"
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
literal|"amqp"
block|,
literal|false
block|}
block|,
block|{
literal|"amqp+ws"
block|,
literal|false
block|}
block|,         }
argument_list|)
return|;
block|}
specifier|public
name|AmqpClientRequestsHeartbeatsTest
parameter_list|(
name|String
name|connectorScheme
parameter_list|,
name|boolean
name|secure
parameter_list|)
block|{
name|super
argument_list|(
name|connectorScheme
argument_list|,
name|secure
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|protected
name|String
name|getAdditionalConfig
parameter_list|()
block|{
return|return
literal|"&transport.wireFormat.idleTimeout=0"
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
name|testBrokerWitZeroIdleTimeDoesNotAdvertise
parameter_list|()
throws|throws
name|Exception
block|{
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
name|client
operator|.
name|setValidator
argument_list|(
operator|new
name|AmqpValidator
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|void
name|inspectOpenedResource
parameter_list|(
name|Connection
name|connection
parameter_list|)
block|{
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|connection
operator|.
name|getTransport
argument_list|()
operator|.
name|getRemoteIdleTimeout
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
argument_list|)
expr_stmt|;
name|AmqpConnection
name|connection
init|=
name|trackConnection
argument_list|(
name|client
operator|.
name|connect
argument_list|()
argument_list|)
decl_stmt|;
name|assertNotNull
argument_list|(
name|connection
argument_list|)
expr_stmt|;
name|connection
operator|.
name|getStateInspector
argument_list|()
operator|.
name|assertValid
argument_list|()
expr_stmt|;
name|connection
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
literal|10
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

