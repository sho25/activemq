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
name|apache
operator|.
name|activemq
operator|.
name|transport
operator|.
name|amqp
operator|.
name|AmqpSupport
operator|.
name|CONNECTION_OPEN_FAILED
import|;
end_import

begin_import
import|import static
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
name|AmqpSupport
operator|.
name|contains
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
name|assertNotNull
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
name|AmqpStateInspector
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
name|amqp
operator|.
name|Symbol
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
name|amqp
operator|.
name|transport
operator|.
name|AmqpError
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
name|amqp
operator|.
name|transport
operator|.
name|ErrorCondition
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

begin_comment
comment|/**  * Test broker handling of AMQP connections with various configurations.  */
end_comment

begin_class
specifier|public
class|class
name|AmqpConnectionsTest
extends|extends
name|AmqpClientTestSupport
block|{
specifier|private
specifier|static
specifier|final
name|Symbol
name|QUEUE_PREFIX
init|=
name|Symbol
operator|.
name|valueOf
argument_list|(
literal|"queue-prefix"
argument_list|)
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|Symbol
name|TOPIC_PREFIX
init|=
name|Symbol
operator|.
name|valueOf
argument_list|(
literal|"topic-prefix"
argument_list|)
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|Symbol
name|ANONYMOUS_RELAY
init|=
name|Symbol
operator|.
name|valueOf
argument_list|(
literal|"ANONYMOUS-RELAY"
argument_list|)
decl_stmt|;
annotation|@
name|Test
argument_list|(
name|timeout
operator|=
literal|60000
argument_list|)
specifier|public
name|void
name|testCanConnect
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
name|AmqpConnection
name|connection
init|=
name|client
operator|.
name|connect
argument_list|()
decl_stmt|;
name|assertNotNull
argument_list|(
name|connection
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
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|getProxyToBroker
argument_list|()
operator|.
name|getCurrentConnectionsCount
argument_list|()
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
name|testConnectionCarriesExpectedCapabilities
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
name|setStateInspector
argument_list|(
operator|new
name|AmqpStateInspector
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
name|Symbol
index|[]
name|offered
init|=
name|connection
operator|.
name|getRemoteOfferedCapabilities
argument_list|()
decl_stmt|;
if|if
condition|(
operator|!
name|contains
argument_list|(
name|offered
argument_list|,
name|ANONYMOUS_RELAY
argument_list|)
condition|)
block|{
name|markAsInvalid
argument_list|(
literal|"Broker did not indicate it support anonymous relay"
argument_list|)
expr_stmt|;
block|}
name|Map
argument_list|<
name|Symbol
argument_list|,
name|Object
argument_list|>
name|properties
init|=
name|connection
operator|.
name|getRemoteProperties
argument_list|()
decl_stmt|;
if|if
condition|(
operator|!
name|properties
operator|.
name|containsKey
argument_list|(
name|QUEUE_PREFIX
argument_list|)
condition|)
block|{
name|markAsInvalid
argument_list|(
literal|"Broker did not send a queue prefix value"
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
operator|!
name|properties
operator|.
name|containsKey
argument_list|(
name|TOPIC_PREFIX
argument_list|)
condition|)
block|{
name|markAsInvalid
argument_list|(
literal|"Broker did not send a queue prefix value"
argument_list|)
expr_stmt|;
block|}
block|}
block|}
argument_list|)
expr_stmt|;
name|AmqpConnection
name|connection
init|=
name|client
operator|.
name|connect
argument_list|()
decl_stmt|;
name|assertNotNull
argument_list|(
name|connection
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
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|getProxyToBroker
argument_list|()
operator|.
name|getCurrentConnectionsCount
argument_list|()
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
name|testCanConnectWithDifferentContainerIds
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
name|AmqpConnection
name|connection1
init|=
name|client
operator|.
name|createConnection
argument_list|()
decl_stmt|;
name|AmqpConnection
name|connection2
init|=
name|client
operator|.
name|createConnection
argument_list|()
decl_stmt|;
name|connection1
operator|.
name|setContainerId
argument_list|(
name|getTestName
argument_list|()
operator|+
literal|"-Client:1"
argument_list|)
expr_stmt|;
name|connection2
operator|.
name|setContainerId
argument_list|(
name|getTestName
argument_list|()
operator|+
literal|"-Client:2"
argument_list|)
expr_stmt|;
name|connection1
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
name|connection2
operator|.
name|connect
argument_list|()
expr_stmt|;
name|assertEquals
argument_list|(
literal|2
argument_list|,
name|getProxyToBroker
argument_list|()
operator|.
name|getCurrentConnectionsCount
argument_list|()
argument_list|)
expr_stmt|;
name|connection1
operator|.
name|close
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
name|connection2
operator|.
name|close
argument_list|()
expr_stmt|;
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|getProxyToBroker
argument_list|()
operator|.
name|getCurrentConnectionsCount
argument_list|()
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
name|testCannotConnectWithSameContainerId
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
name|AmqpConnection
name|connection1
init|=
name|client
operator|.
name|createConnection
argument_list|()
decl_stmt|;
name|AmqpConnection
name|connection2
init|=
name|client
operator|.
name|createConnection
argument_list|()
decl_stmt|;
name|connection1
operator|.
name|setContainerId
argument_list|(
name|getTestName
argument_list|()
argument_list|)
expr_stmt|;
name|connection2
operator|.
name|setContainerId
argument_list|(
name|getTestName
argument_list|()
argument_list|)
expr_stmt|;
name|connection1
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
name|connection2
operator|.
name|setStateInspector
argument_list|(
operator|new
name|AmqpStateInspector
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
if|if
condition|(
operator|!
name|connection
operator|.
name|getRemoteProperties
argument_list|()
operator|.
name|containsKey
argument_list|(
name|CONNECTION_OPEN_FAILED
argument_list|)
condition|)
block|{
name|markAsInvalid
argument_list|(
literal|"Broker did not set connection establishment failed property"
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Override
specifier|public
name|void
name|inspectClosedResource
parameter_list|(
name|Connection
name|connection
parameter_list|)
block|{
name|ErrorCondition
name|remoteError
init|=
name|connection
operator|.
name|getRemoteCondition
argument_list|()
decl_stmt|;
if|if
condition|(
name|remoteError
operator|==
literal|null
condition|)
block|{
name|markAsInvalid
argument_list|(
literal|"Broker dd not add error condition for duplicate client ID"
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
operator|!
name|remoteError
operator|.
name|getCondition
argument_list|()
operator|.
name|equals
argument_list|(
name|AmqpError
operator|.
name|INVALID_FIELD
argument_list|)
condition|)
block|{
name|markAsInvalid
argument_list|(
literal|"Broker dd not set condition to "
operator|+
name|AmqpError
operator|.
name|INVALID_FIELD
argument_list|)
expr_stmt|;
block|}
block|}
block|}
argument_list|)
expr_stmt|;
try|try
block|{
name|connection2
operator|.
name|connect
argument_list|()
expr_stmt|;
comment|//fail("Should not be able to connect with same container Id.");
block|}
catch|catch
parameter_list|(
name|Exception
name|ex
parameter_list|)
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"Second connection with same container Id failed as expected."
argument_list|)
expr_stmt|;
block|}
name|connection2
operator|.
name|getStateInspector
argument_list|()
operator|.
name|assertIfStateChecksFailed
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
name|connection1
operator|.
name|close
argument_list|()
expr_stmt|;
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|getProxyToBroker
argument_list|()
operator|.
name|getCurrentConnectionsCount
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

