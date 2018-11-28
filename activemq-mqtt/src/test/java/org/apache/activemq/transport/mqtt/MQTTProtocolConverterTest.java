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
name|mqtt
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
name|assertTrue
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|mockito
operator|.
name|Mockito
operator|.
name|times
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
name|ExecutorService
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
name|command
operator|.
name|Command
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
name|ConnectionInfo
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
name|ProducerInfo
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
name|RemoveInfo
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
name|Response
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
name|mqtt
operator|.
name|strategy
operator|.
name|MQTTSubscriptionStrategy
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
name|mockito
operator|.
name|ArgumentCaptor
import|;
end_import

begin_import
import|import
name|org
operator|.
name|mockito
operator|.
name|Mockito
import|;
end_import

begin_comment
comment|/**  * Tests for various usage scenarios of the protocol converter  */
end_comment

begin_class
specifier|public
class|class
name|MQTTProtocolConverterTest
block|{
specifier|private
name|MQTTTransport
name|transport
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
name|transport
operator|=
name|Mockito
operator|.
name|mock
argument_list|(
name|MQTTTransport
operator|.
name|class
argument_list|)
expr_stmt|;
name|broker
operator|=
name|Mockito
operator|.
name|mock
argument_list|(
name|BrokerService
operator|.
name|class
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testConnectWithInvalidProtocolVersionToLow
parameter_list|()
throws|throws
name|IOException
block|{
name|doTestConnectWithInvalidProtocolVersion
argument_list|(
literal|2
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testConnectWithInvalidProtocolVersionToHigh
parameter_list|()
throws|throws
name|IOException
block|{
name|doTestConnectWithInvalidProtocolVersion
argument_list|(
literal|5
argument_list|)
expr_stmt|;
block|}
specifier|private
name|void
name|doTestConnectWithInvalidProtocolVersion
parameter_list|(
name|int
name|version
parameter_list|)
throws|throws
name|IOException
block|{
name|MQTTProtocolConverter
name|converter
init|=
operator|new
name|MQTTProtocolConverter
argument_list|(
name|transport
argument_list|,
name|broker
argument_list|)
decl_stmt|;
name|CONNECT
name|connect
init|=
name|Mockito
operator|.
name|mock
argument_list|(
name|CONNECT
operator|.
name|class
argument_list|)
decl_stmt|;
name|Mockito
operator|.
name|when
argument_list|(
name|connect
operator|.
name|version
argument_list|()
argument_list|)
operator|.
name|thenReturn
argument_list|(
name|version
argument_list|)
expr_stmt|;
name|converter
operator|.
name|onMQTTConnect
argument_list|(
name|connect
argument_list|)
expr_stmt|;
name|ArgumentCaptor
argument_list|<
name|IOException
argument_list|>
name|capturedException
init|=
name|ArgumentCaptor
operator|.
name|forClass
argument_list|(
name|IOException
operator|.
name|class
argument_list|)
decl_stmt|;
name|Mockito
operator|.
name|verify
argument_list|(
name|transport
argument_list|)
operator|.
name|onException
argument_list|(
name|capturedException
operator|.
name|capture
argument_list|()
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|capturedException
operator|.
name|getValue
argument_list|()
operator|.
name|getMessage
argument_list|()
operator|.
name|contains
argument_list|(
literal|"version"
argument_list|)
argument_list|)
expr_stmt|;
name|ArgumentCaptor
argument_list|<
name|MQTTFrame
argument_list|>
name|capturedFrame
init|=
name|ArgumentCaptor
operator|.
name|forClass
argument_list|(
name|MQTTFrame
operator|.
name|class
argument_list|)
decl_stmt|;
name|Mockito
operator|.
name|verify
argument_list|(
name|transport
argument_list|)
operator|.
name|sendToMQTT
argument_list|(
name|capturedFrame
operator|.
name|capture
argument_list|()
argument_list|)
expr_stmt|;
name|MQTTFrame
name|response
init|=
name|capturedFrame
operator|.
name|getValue
argument_list|()
decl_stmt|;
name|assertEquals
argument_list|(
name|CONNACK
operator|.
name|TYPE
argument_list|,
name|response
operator|.
name|messageType
argument_list|()
argument_list|)
expr_stmt|;
name|CONNACK
name|connAck
init|=
operator|new
name|CONNACK
argument_list|()
operator|.
name|decode
argument_list|(
name|response
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
name|CONNACK
operator|.
name|Code
operator|.
name|CONNECTION_REFUSED_UNACCEPTED_PROTOCOL_VERSION
argument_list|,
name|connAck
operator|.
name|code
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testConcurrentOnTransportError
parameter_list|()
throws|throws
name|Exception
block|{
name|MQTTProtocolConverter
name|converter
init|=
operator|new
name|MQTTProtocolConverter
argument_list|(
name|transport
argument_list|,
name|broker
argument_list|)
decl_stmt|;
name|converter
operator|.
name|setSubsciptionStrategy
argument_list|(
name|Mockito
operator|.
name|mock
argument_list|(
name|MQTTSubscriptionStrategy
operator|.
name|class
argument_list|)
argument_list|)
expr_stmt|;
name|CONNECT
name|connect
init|=
name|Mockito
operator|.
name|mock
argument_list|(
name|CONNECT
operator|.
name|class
argument_list|)
decl_stmt|;
name|Mockito
operator|.
name|when
argument_list|(
name|connect
operator|.
name|version
argument_list|()
argument_list|)
operator|.
name|thenReturn
argument_list|(
literal|3
argument_list|)
expr_stmt|;
name|Mockito
operator|.
name|when
argument_list|(
name|connect
operator|.
name|cleanSession
argument_list|()
argument_list|)
operator|.
name|thenReturn
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|converter
operator|.
name|onMQTTConnect
argument_list|(
name|connect
argument_list|)
expr_stmt|;
name|ArgumentCaptor
argument_list|<
name|ConnectionInfo
argument_list|>
name|connectionInfoArgumentCaptor
init|=
name|ArgumentCaptor
operator|.
name|forClass
argument_list|(
name|ConnectionInfo
operator|.
name|class
argument_list|)
decl_stmt|;
name|Mockito
operator|.
name|verify
argument_list|(
name|transport
argument_list|)
operator|.
name|sendToActiveMQ
argument_list|(
name|connectionInfoArgumentCaptor
operator|.
name|capture
argument_list|()
argument_list|)
expr_stmt|;
name|ConnectionInfo
name|connectInfo
init|=
name|connectionInfoArgumentCaptor
operator|.
name|getValue
argument_list|()
decl_stmt|;
name|Response
name|ok
init|=
operator|new
name|Response
argument_list|()
decl_stmt|;
name|ok
operator|.
name|setCorrelationId
argument_list|(
name|connectInfo
operator|.
name|getCommandId
argument_list|()
argument_list|)
expr_stmt|;
name|converter
operator|.
name|onActiveMQCommand
argument_list|(
name|ok
argument_list|)
expr_stmt|;
name|ArgumentCaptor
argument_list|<
name|Command
argument_list|>
name|producerInfoArgumentCaptor
init|=
name|ArgumentCaptor
operator|.
name|forClass
argument_list|(
name|Command
operator|.
name|class
argument_list|)
decl_stmt|;
name|Mockito
operator|.
name|verify
argument_list|(
name|transport
argument_list|,
name|times
argument_list|(
literal|3
argument_list|)
argument_list|)
operator|.
name|sendToActiveMQ
argument_list|(
name|producerInfoArgumentCaptor
operator|.
name|capture
argument_list|()
argument_list|)
expr_stmt|;
name|ProducerInfo
name|producerInfo
init|=
operator|(
name|ProducerInfo
operator|)
name|producerInfoArgumentCaptor
operator|.
name|getValue
argument_list|()
decl_stmt|;
name|ok
operator|=
operator|new
name|Response
argument_list|()
expr_stmt|;
name|ok
operator|.
name|setCorrelationId
argument_list|(
name|producerInfo
operator|.
name|getCommandId
argument_list|()
argument_list|)
expr_stmt|;
name|converter
operator|.
name|onActiveMQCommand
argument_list|(
name|ok
argument_list|)
expr_stmt|;
name|ExecutorService
name|executorService
init|=
name|Executors
operator|.
name|newCachedThreadPool
argument_list|()
decl_stmt|;
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
name|i
operator|++
control|)
block|{
name|executorService
operator|.
name|submit
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
name|converter
operator|.
name|onTransportError
argument_list|()
expr_stmt|;
block|}
block|}
argument_list|)
expr_stmt|;
block|}
name|executorService
operator|.
name|shutdown
argument_list|()
expr_stmt|;
name|executorService
operator|.
name|awaitTermination
argument_list|(
literal|10
argument_list|,
name|TimeUnit
operator|.
name|SECONDS
argument_list|)
expr_stmt|;
name|ArgumentCaptor
argument_list|<
name|RemoveInfo
argument_list|>
name|removeInfo
init|=
name|ArgumentCaptor
operator|.
name|forClass
argument_list|(
name|RemoveInfo
operator|.
name|class
argument_list|)
decl_stmt|;
name|Mockito
operator|.
name|verify
argument_list|(
name|transport
argument_list|,
name|times
argument_list|(
literal|4
argument_list|)
argument_list|)
operator|.
name|sendToActiveMQ
argument_list|(
name|removeInfo
operator|.
name|capture
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

