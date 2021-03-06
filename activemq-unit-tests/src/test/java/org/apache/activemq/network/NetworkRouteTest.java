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
name|network
package|;
end_package

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
name|advisory
operator|.
name|AdvisorySupport
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
name|ActiveMQMessage
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
name|ActiveMQTopic
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
name|BrokerId
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
name|command
operator|.
name|ConnectionId
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
name|ConsumerId
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
name|ConsumerInfo
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
name|MessageAck
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
name|MessageDispatch
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
name|command
operator|.
name|SessionId
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
name|FutureResponse
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
name|ResponseCallback
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
name|Transport
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
name|TransportListener
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
name|tcp
operator|.
name|TcpTransport
import|;
end_import

begin_import
import|import
name|org
operator|.
name|easymock
operator|.
name|EasyMock
import|;
end_import

begin_import
import|import
name|org
operator|.
name|easymock
operator|.
name|IAnswer
import|;
end_import

begin_import
import|import
name|org
operator|.
name|easymock
operator|.
name|IMocksControl
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
name|Assert
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

begin_class
specifier|public
class|class
name|NetworkRouteTest
block|{
specifier|private
name|IMocksControl
name|control
decl_stmt|;
specifier|private
name|BrokerService
name|brokerService
decl_stmt|;
specifier|private
name|Transport
name|localBroker
decl_stmt|;
specifier|private
name|Transport
name|remoteBroker
decl_stmt|;
specifier|private
name|TransportListener
name|localListener
decl_stmt|;
specifier|private
name|TransportListener
name|remoteListener
decl_stmt|;
specifier|private
name|MessageDispatch
name|msgDispatch
decl_stmt|;
specifier|private
name|ActiveMQMessage
name|path1Msg
decl_stmt|;
specifier|private
name|ActiveMQMessage
name|path2Msg
decl_stmt|;
specifier|private
name|ActiveMQMessage
name|removePath1Msg
decl_stmt|;
specifier|private
name|ActiveMQMessage
name|removePath2Msg
decl_stmt|;
comment|// this sort of mockery is very brittle but it is fast!
annotation|@
name|Test
specifier|public
name|void
name|verifyNoRemoveOnOneConduitRemove
parameter_list|()
throws|throws
name|Exception
block|{
name|localBroker
operator|.
name|oneway
argument_list|(
name|EasyMock
operator|.
name|isA
argument_list|(
name|ConsumerInfo
operator|.
name|class
argument_list|)
argument_list|)
expr_stmt|;
name|control
operator|.
name|replay
argument_list|()
expr_stmt|;
name|remoteListener
operator|.
name|onCommand
argument_list|(
name|path2Msg
argument_list|)
expr_stmt|;
name|remoteListener
operator|.
name|onCommand
argument_list|(
name|path1Msg
argument_list|)
expr_stmt|;
name|remoteListener
operator|.
name|onCommand
argument_list|(
name|removePath2Msg
argument_list|)
expr_stmt|;
name|control
operator|.
name|verify
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|addAndRemoveOppositeOrder
parameter_list|()
throws|throws
name|Exception
block|{
comment|// from (1)
name|localBroker
operator|.
name|oneway
argument_list|(
name|EasyMock
operator|.
name|isA
argument_list|(
name|ConsumerInfo
operator|.
name|class
argument_list|)
argument_list|)
expr_stmt|;
name|ArgHolder
name|localConsumer
init|=
name|ArgHolder
operator|.
name|holdArgsForLastObjectCall
argument_list|()
decl_stmt|;
comment|// from (2a)
name|remoteBroker
operator|.
name|asyncRequest
argument_list|(
name|EasyMock
operator|.
name|isA
argument_list|(
name|ActiveMQMessage
operator|.
name|class
argument_list|)
argument_list|,
name|EasyMock
operator|.
name|isA
argument_list|(
name|ResponseCallback
operator|.
name|class
argument_list|)
argument_list|)
expr_stmt|;
name|ArgHolder
name|firstMessageFuture
init|=
name|ArgHolder
operator|.
name|holdArgsForLastFutureRequestCall
argument_list|()
decl_stmt|;
name|localBroker
operator|.
name|oneway
argument_list|(
name|EasyMock
operator|.
name|isA
argument_list|(
name|MessageAck
operator|.
name|class
argument_list|)
argument_list|)
expr_stmt|;
comment|// from (2b)
name|remoteBroker
operator|.
name|asyncRequest
argument_list|(
name|EasyMock
operator|.
name|isA
argument_list|(
name|ActiveMQMessage
operator|.
name|class
argument_list|)
argument_list|,
name|EasyMock
operator|.
name|isA
argument_list|(
name|ResponseCallback
operator|.
name|class
argument_list|)
argument_list|)
expr_stmt|;
name|ArgHolder
name|secondMessageFuture
init|=
name|ArgHolder
operator|.
name|holdArgsForLastFutureRequestCall
argument_list|()
decl_stmt|;
name|localBroker
operator|.
name|oneway
argument_list|(
name|EasyMock
operator|.
name|isA
argument_list|(
name|MessageAck
operator|.
name|class
argument_list|)
argument_list|)
expr_stmt|;
comment|// from (3)
name|localBroker
operator|.
name|oneway
argument_list|(
name|EasyMock
operator|.
name|isA
argument_list|(
name|RemoveInfo
operator|.
name|class
argument_list|)
argument_list|)
expr_stmt|;
name|ExpectationWaiter
name|waitForRemove
init|=
name|ExpectationWaiter
operator|.
name|waiterForLastVoidCall
argument_list|()
decl_stmt|;
name|control
operator|.
name|replay
argument_list|()
expr_stmt|;
comment|// (1) send advisory of path 1
name|remoteListener
operator|.
name|onCommand
argument_list|(
name|path1Msg
argument_list|)
expr_stmt|;
name|msgDispatch
operator|.
name|setConsumerId
argument_list|(
operator|(
operator|(
name|ConsumerInfo
operator|)
name|localConsumer
operator|.
name|arguments
index|[
literal|0
index|]
operator|)
operator|.
name|getConsumerId
argument_list|()
argument_list|)
expr_stmt|;
comment|// send advisory of path 2, doesn't send a ConsumerInfo to localBroker
name|remoteListener
operator|.
name|onCommand
argument_list|(
name|path2Msg
argument_list|)
expr_stmt|;
comment|// (2a) send a message
name|localListener
operator|.
name|onCommand
argument_list|(
name|msgDispatch
argument_list|)
expr_stmt|;
name|ResponseCallback
name|callback
init|=
operator|(
name|ResponseCallback
operator|)
name|firstMessageFuture
operator|.
name|arguments
index|[
literal|1
index|]
decl_stmt|;
name|FutureResponse
name|response
init|=
operator|new
name|FutureResponse
argument_list|(
name|callback
argument_list|)
decl_stmt|;
name|response
operator|.
name|set
argument_list|(
operator|new
name|Response
argument_list|()
argument_list|)
expr_stmt|;
comment|// send advisory of path 2 remove, doesn't send a RemoveInfo to localBroker
name|remoteListener
operator|.
name|onCommand
argument_list|(
name|removePath2Msg
argument_list|)
expr_stmt|;
comment|// (2b) send a message
name|localListener
operator|.
name|onCommand
argument_list|(
name|msgDispatch
argument_list|)
expr_stmt|;
name|callback
operator|=
operator|(
name|ResponseCallback
operator|)
name|secondMessageFuture
operator|.
name|arguments
index|[
literal|1
index|]
expr_stmt|;
name|response
operator|=
operator|new
name|FutureResponse
argument_list|(
name|callback
argument_list|)
expr_stmt|;
name|response
operator|.
name|set
argument_list|(
operator|new
name|Response
argument_list|()
argument_list|)
expr_stmt|;
comment|// (3) send advisory of path 1 remove, sends a RemoveInfo to localBroker
name|remoteListener
operator|.
name|onCommand
argument_list|(
name|removePath1Msg
argument_list|)
expr_stmt|;
name|waitForRemove
operator|.
name|assertHappens
argument_list|(
literal|5
argument_list|,
name|TimeUnit
operator|.
name|SECONDS
argument_list|)
expr_stmt|;
comment|// send a message, does not send message as in 2a and 2b
name|localListener
operator|.
name|onCommand
argument_list|(
name|msgDispatch
argument_list|)
expr_stmt|;
name|control
operator|.
name|verify
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|addAndRemoveSameOrder
parameter_list|()
throws|throws
name|Exception
block|{
comment|// from (1)
name|localBroker
operator|.
name|oneway
argument_list|(
name|EasyMock
operator|.
name|isA
argument_list|(
name|ConsumerInfo
operator|.
name|class
argument_list|)
argument_list|)
expr_stmt|;
name|ArgHolder
name|localConsumer
init|=
name|ArgHolder
operator|.
name|holdArgsForLastObjectCall
argument_list|()
decl_stmt|;
comment|// from (2a)
name|remoteBroker
operator|.
name|asyncRequest
argument_list|(
name|EasyMock
operator|.
name|isA
argument_list|(
name|ActiveMQMessage
operator|.
name|class
argument_list|)
argument_list|,
name|EasyMock
operator|.
name|isA
argument_list|(
name|ResponseCallback
operator|.
name|class
argument_list|)
argument_list|)
expr_stmt|;
name|ArgHolder
name|firstMessageFuture
init|=
name|ArgHolder
operator|.
name|holdArgsForLastFutureRequestCall
argument_list|()
decl_stmt|;
name|localBroker
operator|.
name|oneway
argument_list|(
name|EasyMock
operator|.
name|isA
argument_list|(
name|MessageAck
operator|.
name|class
argument_list|)
argument_list|)
expr_stmt|;
comment|// from (2b)
name|remoteBroker
operator|.
name|asyncRequest
argument_list|(
name|EasyMock
operator|.
name|isA
argument_list|(
name|ActiveMQMessage
operator|.
name|class
argument_list|)
argument_list|,
name|EasyMock
operator|.
name|isA
argument_list|(
name|ResponseCallback
operator|.
name|class
argument_list|)
argument_list|)
expr_stmt|;
name|ArgHolder
name|secondMessageFuture
init|=
name|ArgHolder
operator|.
name|holdArgsForLastFutureRequestCall
argument_list|()
decl_stmt|;
name|localBroker
operator|.
name|oneway
argument_list|(
name|EasyMock
operator|.
name|isA
argument_list|(
name|MessageAck
operator|.
name|class
argument_list|)
argument_list|)
expr_stmt|;
comment|// from (3)
name|localBroker
operator|.
name|oneway
argument_list|(
name|EasyMock
operator|.
name|isA
argument_list|(
name|RemoveInfo
operator|.
name|class
argument_list|)
argument_list|)
expr_stmt|;
name|ExpectationWaiter
name|waitForRemove
init|=
name|ExpectationWaiter
operator|.
name|waiterForLastVoidCall
argument_list|()
decl_stmt|;
name|control
operator|.
name|replay
argument_list|()
expr_stmt|;
comment|// (1) send advisory of path 1
name|remoteListener
operator|.
name|onCommand
argument_list|(
name|path1Msg
argument_list|)
expr_stmt|;
name|msgDispatch
operator|.
name|setConsumerId
argument_list|(
operator|(
operator|(
name|ConsumerInfo
operator|)
name|localConsumer
operator|.
name|arguments
index|[
literal|0
index|]
operator|)
operator|.
name|getConsumerId
argument_list|()
argument_list|)
expr_stmt|;
comment|// send advisory of path 2, doesn't send a ConsumerInfo to localBroker
name|remoteListener
operator|.
name|onCommand
argument_list|(
name|path2Msg
argument_list|)
expr_stmt|;
comment|// (2a) send a message
name|localListener
operator|.
name|onCommand
argument_list|(
name|msgDispatch
argument_list|)
expr_stmt|;
name|ResponseCallback
name|callback
init|=
operator|(
name|ResponseCallback
operator|)
name|firstMessageFuture
operator|.
name|arguments
index|[
literal|1
index|]
decl_stmt|;
name|FutureResponse
name|response
init|=
operator|new
name|FutureResponse
argument_list|(
name|callback
argument_list|)
decl_stmt|;
name|response
operator|.
name|set
argument_list|(
operator|new
name|Response
argument_list|()
argument_list|)
expr_stmt|;
comment|// send advisory of path 1 remove, shouldn't send a RemoveInfo to localBroker
name|remoteListener
operator|.
name|onCommand
argument_list|(
name|removePath1Msg
argument_list|)
expr_stmt|;
comment|// (2b) send a message, should send the message as in 2a
name|localListener
operator|.
name|onCommand
argument_list|(
name|msgDispatch
argument_list|)
expr_stmt|;
name|callback
operator|=
operator|(
name|ResponseCallback
operator|)
name|secondMessageFuture
operator|.
name|arguments
index|[
literal|1
index|]
expr_stmt|;
name|response
operator|=
operator|new
name|FutureResponse
argument_list|(
name|callback
argument_list|)
expr_stmt|;
name|response
operator|.
name|set
argument_list|(
operator|new
name|Response
argument_list|()
argument_list|)
expr_stmt|;
comment|// (3) send advisory of path 1 remove, should send a RemoveInfo to localBroker
name|remoteListener
operator|.
name|onCommand
argument_list|(
name|removePath2Msg
argument_list|)
expr_stmt|;
name|waitForRemove
operator|.
name|assertHappens
argument_list|(
literal|5
argument_list|,
name|TimeUnit
operator|.
name|SECONDS
argument_list|)
expr_stmt|;
comment|// send a message, does not send message as in 2a
name|localListener
operator|.
name|onCommand
argument_list|(
name|msgDispatch
argument_list|)
expr_stmt|;
name|control
operator|.
name|verify
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Before
specifier|public
name|void
name|before
parameter_list|()
throws|throws
name|Exception
block|{
name|control
operator|=
name|EasyMock
operator|.
name|createControl
argument_list|()
expr_stmt|;
name|localBroker
operator|=
name|control
operator|.
name|createMock
argument_list|(
name|Transport
operator|.
name|class
argument_list|)
expr_stmt|;
name|remoteBroker
operator|=
name|control
operator|.
name|createMock
argument_list|(
name|Transport
operator|.
name|class
argument_list|)
expr_stmt|;
name|NetworkBridgeConfiguration
name|configuration
init|=
operator|new
name|NetworkBridgeConfiguration
argument_list|()
decl_stmt|;
name|brokerService
operator|=
operator|new
name|BrokerService
argument_list|()
expr_stmt|;
name|BrokerInfo
name|remoteBrokerInfo
init|=
operator|new
name|BrokerInfo
argument_list|()
decl_stmt|;
name|configuration
operator|.
name|setDuplex
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|configuration
operator|.
name|setNetworkTTL
argument_list|(
literal|5
argument_list|)
expr_stmt|;
name|brokerService
operator|.
name|setBrokerId
argument_list|(
literal|"broker-1"
argument_list|)
expr_stmt|;
name|brokerService
operator|.
name|setPersistent
argument_list|(
literal|false
argument_list|)
expr_stmt|;
name|brokerService
operator|.
name|setUseJmx
argument_list|(
literal|false
argument_list|)
expr_stmt|;
name|brokerService
operator|.
name|start
argument_list|()
expr_stmt|;
name|brokerService
operator|.
name|waitUntilStarted
argument_list|()
expr_stmt|;
name|remoteBrokerInfo
operator|.
name|setBrokerId
argument_list|(
operator|new
name|BrokerId
argument_list|(
literal|"remote-broker-id"
argument_list|)
argument_list|)
expr_stmt|;
name|remoteBrokerInfo
operator|.
name|setBrokerName
argument_list|(
literal|"remote-broker-name"
argument_list|)
expr_stmt|;
name|localBroker
operator|.
name|setTransportListener
argument_list|(
name|EasyMock
operator|.
name|isA
argument_list|(
name|TransportListener
operator|.
name|class
argument_list|)
argument_list|)
expr_stmt|;
name|ArgHolder
name|localListenerRef
init|=
name|ArgHolder
operator|.
name|holdArgsForLastVoidCall
argument_list|()
decl_stmt|;
name|remoteBroker
operator|.
name|setTransportListener
argument_list|(
name|EasyMock
operator|.
name|isA
argument_list|(
name|TransportListener
operator|.
name|class
argument_list|)
argument_list|)
expr_stmt|;
name|ArgHolder
name|remoteListenerRef
init|=
name|ArgHolder
operator|.
name|holdArgsForLastVoidCall
argument_list|()
decl_stmt|;
name|localBroker
operator|.
name|start
argument_list|()
expr_stmt|;
name|remoteBroker
operator|.
name|start
argument_list|()
expr_stmt|;
name|remoteBroker
operator|.
name|oneway
argument_list|(
name|EasyMock
operator|.
name|isA
argument_list|(
name|Object
operator|.
name|class
argument_list|)
argument_list|)
expr_stmt|;
name|EasyMock
operator|.
name|expectLastCall
argument_list|()
operator|.
name|times
argument_list|(
literal|4
argument_list|)
expr_stmt|;
name|remoteBroker
operator|.
name|oneway
argument_list|(
name|EasyMock
operator|.
name|isA
argument_list|(
name|Object
operator|.
name|class
argument_list|)
argument_list|)
expr_stmt|;
name|ExpectationWaiter
name|remoteInitWaiter
init|=
name|ExpectationWaiter
operator|.
name|waiterForLastVoidCall
argument_list|()
decl_stmt|;
name|localBroker
operator|.
name|oneway
argument_list|(
name|remoteBrokerInfo
argument_list|)
expr_stmt|;
name|EasyMock
operator|.
name|expect
argument_list|(
name|localBroker
operator|.
name|request
argument_list|(
name|EasyMock
operator|.
name|isA
argument_list|(
name|Object
operator|.
name|class
argument_list|)
argument_list|)
argument_list|)
operator|.
name|andReturn
argument_list|(
literal|null
argument_list|)
expr_stmt|;
name|EasyMock
operator|.
name|expect
argument_list|(
name|remoteBroker
operator|.
name|narrow
argument_list|(
name|TcpTransport
operator|.
name|class
argument_list|)
argument_list|)
operator|.
name|andReturn
argument_list|(
literal|null
argument_list|)
expr_stmt|;
name|localBroker
operator|.
name|oneway
argument_list|(
name|EasyMock
operator|.
name|isA
argument_list|(
name|Object
operator|.
name|class
argument_list|)
argument_list|)
expr_stmt|;
name|ExpectationWaiter
name|localInitWaiter
init|=
name|ExpectationWaiter
operator|.
name|waiterForLastVoidCall
argument_list|()
decl_stmt|;
name|control
operator|.
name|replay
argument_list|()
expr_stmt|;
name|DurableConduitBridge
name|bridge
init|=
operator|new
name|DurableConduitBridge
argument_list|(
name|configuration
argument_list|,
name|localBroker
argument_list|,
name|remoteBroker
argument_list|)
decl_stmt|;
name|bridge
operator|.
name|setBrokerService
argument_list|(
name|brokerService
argument_list|)
expr_stmt|;
name|bridge
operator|.
name|start
argument_list|()
expr_stmt|;
name|localListener
operator|=
operator|(
name|TransportListener
operator|)
name|localListenerRef
operator|.
name|getArguments
argument_list|()
index|[
literal|0
index|]
expr_stmt|;
name|Assert
operator|.
name|assertNotNull
argument_list|(
name|localListener
argument_list|)
expr_stmt|;
name|remoteListener
operator|=
operator|(
name|TransportListener
operator|)
name|remoteListenerRef
operator|.
name|getArguments
argument_list|()
index|[
literal|0
index|]
expr_stmt|;
name|Assert
operator|.
name|assertNotNull
argument_list|(
name|remoteListener
argument_list|)
expr_stmt|;
name|remoteListener
operator|.
name|onCommand
argument_list|(
name|remoteBrokerInfo
argument_list|)
expr_stmt|;
name|remoteInitWaiter
operator|.
name|assertHappens
argument_list|(
literal|5
argument_list|,
name|TimeUnit
operator|.
name|SECONDS
argument_list|)
expr_stmt|;
name|localInitWaiter
operator|.
name|assertHappens
argument_list|(
literal|5
argument_list|,
name|TimeUnit
operator|.
name|SECONDS
argument_list|)
expr_stmt|;
name|control
operator|.
name|verify
argument_list|()
expr_stmt|;
name|control
operator|.
name|reset
argument_list|()
expr_stmt|;
name|ActiveMQMessage
name|msg
init|=
operator|new
name|ActiveMQMessage
argument_list|()
decl_stmt|;
name|msg
operator|.
name|setDestination
argument_list|(
operator|new
name|ActiveMQTopic
argument_list|(
literal|"test"
argument_list|)
argument_list|)
expr_stmt|;
name|msgDispatch
operator|=
operator|new
name|MessageDispatch
argument_list|()
expr_stmt|;
name|msgDispatch
operator|.
name|setMessage
argument_list|(
name|msg
argument_list|)
expr_stmt|;
name|msgDispatch
operator|.
name|setDestination
argument_list|(
name|msg
operator|.
name|getDestination
argument_list|()
argument_list|)
expr_stmt|;
name|ConsumerInfo
name|path1
init|=
operator|new
name|ConsumerInfo
argument_list|()
decl_stmt|;
name|path1
operator|.
name|setDestination
argument_list|(
name|msg
operator|.
name|getDestination
argument_list|()
argument_list|)
expr_stmt|;
name|path1
operator|.
name|setConsumerId
argument_list|(
operator|new
name|ConsumerId
argument_list|(
operator|new
name|SessionId
argument_list|(
operator|new
name|ConnectionId
argument_list|(
literal|"conn-id-1"
argument_list|)
argument_list|,
literal|1
argument_list|)
argument_list|,
literal|3
argument_list|)
argument_list|)
expr_stmt|;
name|path1
operator|.
name|setBrokerPath
argument_list|(
operator|new
name|BrokerId
index|[]
block|{
operator|new
name|BrokerId
argument_list|(
literal|"remote-broker-id"
argument_list|)
block|,
operator|new
name|BrokerId
argument_list|(
literal|"server(1)-broker-id"
argument_list|)
block|,         }
argument_list|)
expr_stmt|;
name|path1Msg
operator|=
operator|new
name|ActiveMQMessage
argument_list|()
expr_stmt|;
name|path1Msg
operator|.
name|setDestination
argument_list|(
name|AdvisorySupport
operator|.
name|getConsumerAdvisoryTopic
argument_list|(
name|path1
operator|.
name|getDestination
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|path1Msg
operator|.
name|setDataStructure
argument_list|(
name|path1
argument_list|)
expr_stmt|;
name|ConsumerInfo
name|path2
init|=
operator|new
name|ConsumerInfo
argument_list|()
decl_stmt|;
name|path2
operator|.
name|setDestination
argument_list|(
name|path1
operator|.
name|getDestination
argument_list|()
argument_list|)
expr_stmt|;
name|path2
operator|.
name|setConsumerId
argument_list|(
operator|new
name|ConsumerId
argument_list|(
operator|new
name|SessionId
argument_list|(
operator|new
name|ConnectionId
argument_list|(
literal|"conn-id-2"
argument_list|)
argument_list|,
literal|2
argument_list|)
argument_list|,
literal|4
argument_list|)
argument_list|)
expr_stmt|;
name|path2
operator|.
name|setBrokerPath
argument_list|(
operator|new
name|BrokerId
index|[]
block|{
operator|new
name|BrokerId
argument_list|(
literal|"remote-broker-id"
argument_list|)
block|,
operator|new
name|BrokerId
argument_list|(
literal|"server(2)-broker-id"
argument_list|)
block|,
operator|new
name|BrokerId
argument_list|(
literal|"server(1)-broker-id"
argument_list|)
block|,         }
argument_list|)
expr_stmt|;
name|path2Msg
operator|=
operator|new
name|ActiveMQMessage
argument_list|()
expr_stmt|;
name|path2Msg
operator|.
name|setDestination
argument_list|(
name|path1Msg
operator|.
name|getDestination
argument_list|()
argument_list|)
expr_stmt|;
name|path2Msg
operator|.
name|setDataStructure
argument_list|(
name|path2
argument_list|)
expr_stmt|;
name|RemoveInfo
name|removePath1
init|=
operator|new
name|RemoveInfo
argument_list|(
name|path1
operator|.
name|getConsumerId
argument_list|()
argument_list|)
decl_stmt|;
name|RemoveInfo
name|removePath2
init|=
operator|new
name|RemoveInfo
argument_list|(
name|path2
operator|.
name|getConsumerId
argument_list|()
argument_list|)
decl_stmt|;
name|removePath1Msg
operator|=
operator|new
name|ActiveMQMessage
argument_list|()
expr_stmt|;
name|removePath1Msg
operator|.
name|setDestination
argument_list|(
name|path1Msg
operator|.
name|getDestination
argument_list|()
argument_list|)
expr_stmt|;
name|removePath1Msg
operator|.
name|setDataStructure
argument_list|(
name|removePath1
argument_list|)
expr_stmt|;
name|removePath2Msg
operator|=
operator|new
name|ActiveMQMessage
argument_list|()
expr_stmt|;
name|removePath2Msg
operator|.
name|setDestination
argument_list|(
name|path1Msg
operator|.
name|getDestination
argument_list|()
argument_list|)
expr_stmt|;
name|removePath2Msg
operator|.
name|setDataStructure
argument_list|(
name|removePath2
argument_list|)
expr_stmt|;
block|}
annotation|@
name|After
specifier|public
name|void
name|after
parameter_list|()
throws|throws
name|Exception
block|{
name|control
operator|.
name|reset
argument_list|()
expr_stmt|;
name|brokerService
operator|.
name|stop
argument_list|()
expr_stmt|;
name|brokerService
operator|.
name|waitUntilStopped
argument_list|()
expr_stmt|;
block|}
specifier|private
specifier|static
class|class
name|ArgHolder
block|{
specifier|public
name|Object
index|[]
name|arguments
decl_stmt|;
specifier|public
specifier|static
name|ArgHolder
name|holdArgsForLastVoidCall
parameter_list|()
block|{
specifier|final
name|ArgHolder
name|holder
init|=
operator|new
name|ArgHolder
argument_list|()
decl_stmt|;
name|EasyMock
operator|.
name|expectLastCall
argument_list|()
operator|.
name|andAnswer
argument_list|(
operator|new
name|IAnswer
argument_list|<
name|Object
argument_list|>
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|Object
name|answer
parameter_list|()
throws|throws
name|Throwable
block|{
name|Object
index|[]
name|args
init|=
name|EasyMock
operator|.
name|getCurrentArguments
argument_list|()
decl_stmt|;
name|holder
operator|.
name|arguments
operator|=
name|Arrays
operator|.
name|copyOf
argument_list|(
name|args
argument_list|,
name|args
operator|.
name|length
argument_list|)
expr_stmt|;
return|return
literal|null
return|;
block|}
block|}
argument_list|)
expr_stmt|;
return|return
name|holder
return|;
block|}
specifier|public
specifier|static
name|ArgHolder
name|holdArgsForLastObjectCall
parameter_list|()
block|{
specifier|final
name|ArgHolder
name|holder
init|=
operator|new
name|ArgHolder
argument_list|()
decl_stmt|;
name|EasyMock
operator|.
name|expect
argument_list|(
operator|new
name|Object
argument_list|()
argument_list|)
operator|.
name|andAnswer
argument_list|(
operator|new
name|IAnswer
argument_list|<
name|Object
argument_list|>
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|Object
name|answer
parameter_list|()
throws|throws
name|Throwable
block|{
name|Object
index|[]
name|args
init|=
name|EasyMock
operator|.
name|getCurrentArguments
argument_list|()
decl_stmt|;
name|holder
operator|.
name|arguments
operator|=
name|Arrays
operator|.
name|copyOf
argument_list|(
name|args
argument_list|,
name|args
operator|.
name|length
argument_list|)
expr_stmt|;
return|return
literal|null
return|;
block|}
block|}
argument_list|)
expr_stmt|;
return|return
name|holder
return|;
block|}
specifier|public
specifier|static
name|ArgHolder
name|holdArgsForLastFutureRequestCall
parameter_list|()
block|{
specifier|final
name|ArgHolder
name|holder
init|=
operator|new
name|ArgHolder
argument_list|()
decl_stmt|;
name|EasyMock
operator|.
name|expect
argument_list|(
operator|new
name|FutureResponse
argument_list|(
literal|null
argument_list|)
argument_list|)
operator|.
name|andAnswer
argument_list|(
operator|new
name|IAnswer
argument_list|<
name|FutureResponse
argument_list|>
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|FutureResponse
name|answer
parameter_list|()
throws|throws
name|Throwable
block|{
name|Object
index|[]
name|args
init|=
name|EasyMock
operator|.
name|getCurrentArguments
argument_list|()
decl_stmt|;
name|holder
operator|.
name|arguments
operator|=
name|Arrays
operator|.
name|copyOf
argument_list|(
name|args
argument_list|,
name|args
operator|.
name|length
argument_list|)
expr_stmt|;
return|return
literal|null
return|;
block|}
block|}
argument_list|)
expr_stmt|;
return|return
name|holder
return|;
block|}
specifier|public
name|Object
index|[]
name|getArguments
parameter_list|()
block|{
name|Assert
operator|.
name|assertNotNull
argument_list|(
name|arguments
argument_list|)
expr_stmt|;
return|return
name|arguments
return|;
block|}
block|}
specifier|private
specifier|static
class|class
name|ExpectationWaiter
block|{
specifier|private
name|CountDownLatch
name|latch
init|=
operator|new
name|CountDownLatch
argument_list|(
literal|1
argument_list|)
decl_stmt|;
specifier|public
specifier|static
name|ExpectationWaiter
name|waiterForLastVoidCall
parameter_list|()
block|{
specifier|final
name|ExpectationWaiter
name|waiter
init|=
operator|new
name|ExpectationWaiter
argument_list|()
decl_stmt|;
name|EasyMock
operator|.
name|expectLastCall
argument_list|()
operator|.
name|andAnswer
argument_list|(
operator|new
name|IAnswer
argument_list|<
name|Object
argument_list|>
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|Object
name|answer
parameter_list|()
throws|throws
name|Throwable
block|{
name|waiter
operator|.
name|latch
operator|.
name|countDown
argument_list|()
expr_stmt|;
return|return
literal|null
return|;
block|}
block|}
argument_list|)
expr_stmt|;
return|return
name|waiter
return|;
block|}
specifier|public
name|void
name|assertHappens
parameter_list|(
name|long
name|timeout
parameter_list|,
name|TimeUnit
name|unit
parameter_list|)
throws|throws
name|InterruptedException
block|{
name|Assert
operator|.
name|assertTrue
argument_list|(
name|latch
operator|.
name|await
argument_list|(
name|timeout
argument_list|,
name|unit
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

