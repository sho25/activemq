begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  *  * Copyright 2005-2006 The Apache Software Foundation  *  * Licensed under the Apache License, Version 2.0 (the "License");  * you may not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  * http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
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
name|javax
operator|.
name|jms
operator|.
name|DeliveryMode
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
name|StubConnection
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
name|ActiveMQDestination
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
name|Message
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
name|SessionInfo
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
name|network
operator|.
name|ForwardingBridge
import|;
end_import

begin_import
import|import
name|junit
operator|.
name|framework
operator|.
name|Test
import|;
end_import

begin_class
specifier|public
class|class
name|ForwardingBridgeTest
extends|extends
name|NetworkTestSupport
block|{
specifier|public
name|ActiveMQDestination
name|destination
decl_stmt|;
specifier|public
name|byte
name|destinationType
decl_stmt|;
specifier|public
name|int
name|deliveryMode
decl_stmt|;
specifier|private
name|ForwardingBridge
name|bridge
decl_stmt|;
specifier|public
name|void
name|initCombosForTestAddConsumerThenSend
parameter_list|()
block|{
name|addCombinationValues
argument_list|(
literal|"deliveryMode"
argument_list|,
operator|new
name|Object
index|[]
block|{
operator|new
name|Integer
argument_list|(
name|DeliveryMode
operator|.
name|NON_PERSISTENT
argument_list|)
block|,
operator|new
name|Integer
argument_list|(
name|DeliveryMode
operator|.
name|PERSISTENT
argument_list|)
block|}
argument_list|)
expr_stmt|;
name|addCombinationValues
argument_list|(
literal|"destinationType"
argument_list|,
operator|new
name|Object
index|[]
block|{
operator|new
name|Byte
argument_list|(
name|ActiveMQDestination
operator|.
name|QUEUE_TYPE
argument_list|)
block|,
operator|new
name|Byte
argument_list|(
name|ActiveMQDestination
operator|.
name|TOPIC_TYPE
argument_list|)
block|,                  }
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|testAddConsumerThenSend
parameter_list|()
throws|throws
name|Exception
block|{
comment|// Start a producer on local broker
name|StubConnection
name|connection1
init|=
name|createConnection
argument_list|()
decl_stmt|;
name|ConnectionInfo
name|connectionInfo1
init|=
name|createConnectionInfo
argument_list|()
decl_stmt|;
name|SessionInfo
name|sessionInfo1
init|=
name|createSessionInfo
argument_list|(
name|connectionInfo1
argument_list|)
decl_stmt|;
name|ProducerInfo
name|producerInfo
init|=
name|createProducerInfo
argument_list|(
name|sessionInfo1
argument_list|)
decl_stmt|;
name|connection1
operator|.
name|send
argument_list|(
name|connectionInfo1
argument_list|)
expr_stmt|;
name|connection1
operator|.
name|send
argument_list|(
name|sessionInfo1
argument_list|)
expr_stmt|;
name|connection1
operator|.
name|send
argument_list|(
name|producerInfo
argument_list|)
expr_stmt|;
name|destination
operator|=
name|createDestinationInfo
argument_list|(
name|connection1
argument_list|,
name|connectionInfo1
argument_list|,
name|destinationType
argument_list|)
expr_stmt|;
comment|// Start a consumer on a remote broker
name|StubConnection
name|connection2
init|=
name|createRemoteConnection
argument_list|()
decl_stmt|;
name|ConnectionInfo
name|connectionInfo2
init|=
name|createConnectionInfo
argument_list|()
decl_stmt|;
name|SessionInfo
name|sessionInfo2
init|=
name|createSessionInfo
argument_list|(
name|connectionInfo2
argument_list|)
decl_stmt|;
name|connection2
operator|.
name|send
argument_list|(
name|connectionInfo2
argument_list|)
expr_stmt|;
name|connection2
operator|.
name|send
argument_list|(
name|sessionInfo2
argument_list|)
expr_stmt|;
name|ConsumerInfo
name|consumerInfo
init|=
name|createConsumerInfo
argument_list|(
name|sessionInfo2
argument_list|,
name|destination
argument_list|)
decl_stmt|;
name|connection2
operator|.
name|send
argument_list|(
name|consumerInfo
argument_list|)
expr_stmt|;
comment|// Send the message to the local boker.
name|connection1
operator|.
name|send
argument_list|(
name|createMessage
argument_list|(
name|producerInfo
argument_list|,
name|destination
argument_list|,
name|deliveryMode
argument_list|)
argument_list|)
expr_stmt|;
comment|// Make sure the message was delivered via the remote.
name|Message
name|m
init|=
name|receiveMessage
argument_list|(
name|connection2
argument_list|)
decl_stmt|;
name|assertNotNull
argument_list|(
name|m
argument_list|)
expr_stmt|;
block|}
specifier|protected
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
name|bridge
operator|=
operator|new
name|ForwardingBridge
argument_list|(
name|createTransport
argument_list|()
argument_list|,
name|createRemoteTransport
argument_list|()
argument_list|)
expr_stmt|;
name|bridge
operator|.
name|setClientId
argument_list|(
literal|"local-remote-bridge"
argument_list|)
expr_stmt|;
name|bridge
operator|.
name|setDispatchAsync
argument_list|(
literal|false
argument_list|)
expr_stmt|;
name|bridge
operator|.
name|start
argument_list|()
expr_stmt|;
comment|// PATCH: Give forwarding bridge a chance to finish setting up
try|try
block|{
name|Thread
operator|.
name|sleep
argument_list|(
literal|1000
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|InterruptedException
name|ie
parameter_list|)
block|{
name|ie
operator|.
name|printStackTrace
argument_list|()
expr_stmt|;
block|}
block|}
specifier|protected
name|void
name|tearDown
parameter_list|()
throws|throws
name|Exception
block|{
name|bridge
operator|.
name|stop
argument_list|()
expr_stmt|;
name|super
operator|.
name|tearDown
argument_list|()
expr_stmt|;
block|}
specifier|public
specifier|static
name|Test
name|suite
parameter_list|()
block|{
return|return
name|suite
argument_list|(
name|ForwardingBridgeTest
operator|.
name|class
argument_list|)
return|;
block|}
specifier|public
specifier|static
name|void
name|main
parameter_list|(
name|String
index|[]
name|args
parameter_list|)
block|{
name|junit
operator|.
name|textui
operator|.
name|TestRunner
operator|.
name|run
argument_list|(
name|suite
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

