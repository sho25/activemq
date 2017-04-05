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
name|assertNull
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
name|jmx
operator|.
name|QueueViewMBean
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
name|AmqpMessage
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
name|AmqpReceiver
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
name|AmqpSender
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
name|AmqpSession
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
name|AmqpExpiredMessageTest
extends|extends
name|AmqpClientTestSupport
block|{
annotation|@
name|Test
argument_list|(
name|timeout
operator|=
literal|60000
argument_list|)
specifier|public
name|void
name|testSendMessageThatIsAlreadyExpiredUsingAbsoluteTime
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
name|AmqpSession
name|session
init|=
name|connection
operator|.
name|createSession
argument_list|()
decl_stmt|;
name|AmqpSender
name|sender
init|=
name|session
operator|.
name|createSender
argument_list|(
name|getTestName
argument_list|()
argument_list|)
decl_stmt|;
comment|// Get the Queue View early to avoid racing the delivery.
specifier|final
name|QueueViewMBean
name|queueView
init|=
name|getProxyToQueue
argument_list|(
name|getTestName
argument_list|()
argument_list|)
decl_stmt|;
name|assertNotNull
argument_list|(
name|queueView
argument_list|)
expr_stmt|;
name|AmqpMessage
name|message
init|=
operator|new
name|AmqpMessage
argument_list|()
decl_stmt|;
name|message
operator|.
name|setAbsoluteExpiryTime
argument_list|(
name|System
operator|.
name|currentTimeMillis
argument_list|()
operator|-
literal|5000
argument_list|)
expr_stmt|;
name|message
operator|.
name|setText
argument_list|(
literal|"Test-Message"
argument_list|)
expr_stmt|;
name|sender
operator|.
name|send
argument_list|(
name|message
argument_list|)
expr_stmt|;
name|sender
operator|.
name|close
argument_list|()
expr_stmt|;
comment|// Broker doesn't track messages that arrived already expired.
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|queueView
operator|.
name|getQueueSize
argument_list|()
argument_list|)
expr_stmt|;
comment|// Now try and get the message
name|AmqpReceiver
name|receiver
init|=
name|session
operator|.
name|createReceiver
argument_list|(
name|getTestName
argument_list|()
argument_list|)
decl_stmt|;
name|receiver
operator|.
name|flow
argument_list|(
literal|1
argument_list|)
expr_stmt|;
name|AmqpMessage
name|received
init|=
name|receiver
operator|.
name|receive
argument_list|(
literal|1
argument_list|,
name|TimeUnit
operator|.
name|SECONDS
argument_list|)
decl_stmt|;
name|assertNull
argument_list|(
name|received
argument_list|)
expr_stmt|;
comment|// Broker doesn't track messages that arrived already expired.
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|queueView
operator|.
name|getExpiredCount
argument_list|()
argument_list|)
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
name|testSendMessageThatIsNotExpiredUsingAbsoluteTime
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
name|AmqpSession
name|session
init|=
name|connection
operator|.
name|createSession
argument_list|()
decl_stmt|;
name|AmqpSender
name|sender
init|=
name|session
operator|.
name|createSender
argument_list|(
name|getTestName
argument_list|()
argument_list|)
decl_stmt|;
comment|// Get the Queue View early to avoid racing the delivery.
specifier|final
name|QueueViewMBean
name|queueView
init|=
name|getProxyToQueue
argument_list|(
name|getTestName
argument_list|()
argument_list|)
decl_stmt|;
name|assertNotNull
argument_list|(
name|queueView
argument_list|)
expr_stmt|;
name|AmqpMessage
name|message
init|=
operator|new
name|AmqpMessage
argument_list|()
decl_stmt|;
name|message
operator|.
name|setAbsoluteExpiryTime
argument_list|(
name|System
operator|.
name|currentTimeMillis
argument_list|()
operator|+
literal|5000
argument_list|)
expr_stmt|;
name|message
operator|.
name|setText
argument_list|(
literal|"Test-Message"
argument_list|)
expr_stmt|;
name|sender
operator|.
name|send
argument_list|(
name|message
argument_list|)
expr_stmt|;
name|sender
operator|.
name|close
argument_list|()
expr_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|queueView
operator|.
name|getQueueSize
argument_list|()
argument_list|)
expr_stmt|;
comment|// Now try and get the message
name|AmqpReceiver
name|receiver
init|=
name|session
operator|.
name|createReceiver
argument_list|(
name|getTestName
argument_list|()
argument_list|)
decl_stmt|;
name|receiver
operator|.
name|flow
argument_list|(
literal|1
argument_list|)
expr_stmt|;
name|AmqpMessage
name|received
init|=
name|receiver
operator|.
name|receive
argument_list|(
literal|5
argument_list|,
name|TimeUnit
operator|.
name|SECONDS
argument_list|)
decl_stmt|;
name|assertNotNull
argument_list|(
name|received
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|queueView
operator|.
name|getExpiredCount
argument_list|()
argument_list|)
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
name|testSendMessageThatIsExiredUsingAbsoluteTimeWithLongTTL
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
name|AmqpSession
name|session
init|=
name|connection
operator|.
name|createSession
argument_list|()
decl_stmt|;
name|AmqpSender
name|sender
init|=
name|session
operator|.
name|createSender
argument_list|(
name|getTestName
argument_list|()
argument_list|)
decl_stmt|;
comment|// Get the Queue View early to avoid racing the delivery.
specifier|final
name|QueueViewMBean
name|queueView
init|=
name|getProxyToQueue
argument_list|(
name|getTestName
argument_list|()
argument_list|)
decl_stmt|;
name|assertNotNull
argument_list|(
name|queueView
argument_list|)
expr_stmt|;
name|AmqpMessage
name|message
init|=
operator|new
name|AmqpMessage
argument_list|()
decl_stmt|;
name|message
operator|.
name|setAbsoluteExpiryTime
argument_list|(
name|System
operator|.
name|currentTimeMillis
argument_list|()
operator|-
literal|5000
argument_list|)
expr_stmt|;
comment|// AET should override any TTL set
name|message
operator|.
name|setTimeToLive
argument_list|(
literal|60000
argument_list|)
expr_stmt|;
name|message
operator|.
name|setText
argument_list|(
literal|"Test-Message"
argument_list|)
expr_stmt|;
name|sender
operator|.
name|send
argument_list|(
name|message
argument_list|)
expr_stmt|;
name|sender
operator|.
name|close
argument_list|()
expr_stmt|;
comment|// Broker doesn't track messages that arrived already expired.
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|queueView
operator|.
name|getQueueSize
argument_list|()
argument_list|)
expr_stmt|;
comment|// Now try and get the message
name|AmqpReceiver
name|receiver
init|=
name|session
operator|.
name|createReceiver
argument_list|(
name|getTestName
argument_list|()
argument_list|)
decl_stmt|;
name|receiver
operator|.
name|flow
argument_list|(
literal|1
argument_list|)
expr_stmt|;
name|AmqpMessage
name|received
init|=
name|receiver
operator|.
name|receive
argument_list|(
literal|1
argument_list|,
name|TimeUnit
operator|.
name|SECONDS
argument_list|)
decl_stmt|;
name|assertNull
argument_list|(
name|received
argument_list|)
expr_stmt|;
comment|// Broker doesn't track messages that arrived already expired.
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|queueView
operator|.
name|getExpiredCount
argument_list|()
argument_list|)
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
name|testSendMessageThatIsExpiredUsingTTLWhenAbsoluteIsZero
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
name|AmqpSession
name|session
init|=
name|connection
operator|.
name|createSession
argument_list|()
decl_stmt|;
name|AmqpSender
name|sender
init|=
name|session
operator|.
name|createSender
argument_list|(
name|getTestName
argument_list|()
argument_list|)
decl_stmt|;
comment|// Get the Queue View early to avoid racing the delivery.
specifier|final
name|QueueViewMBean
name|queueView
init|=
name|getProxyToQueue
argument_list|(
name|getTestName
argument_list|()
argument_list|)
decl_stmt|;
name|assertNotNull
argument_list|(
name|queueView
argument_list|)
expr_stmt|;
name|AmqpMessage
name|message
init|=
operator|new
name|AmqpMessage
argument_list|()
decl_stmt|;
name|message
operator|.
name|setAbsoluteExpiryTime
argument_list|(
literal|0
argument_list|)
expr_stmt|;
comment|// AET should override any TTL set unless it is zero
name|message
operator|.
name|setTimeToLive
argument_list|(
literal|1000
argument_list|)
expr_stmt|;
name|message
operator|.
name|setText
argument_list|(
literal|"Test-Message"
argument_list|)
expr_stmt|;
name|sender
operator|.
name|send
argument_list|(
name|message
argument_list|)
expr_stmt|;
name|sender
operator|.
name|close
argument_list|()
expr_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|queueView
operator|.
name|getQueueSize
argument_list|()
argument_list|)
expr_stmt|;
name|Thread
operator|.
name|sleep
argument_list|(
literal|1000
argument_list|)
expr_stmt|;
comment|// Now try and get the message
name|AmqpReceiver
name|receiver
init|=
name|session
operator|.
name|createReceiver
argument_list|(
name|getTestName
argument_list|()
argument_list|)
decl_stmt|;
name|receiver
operator|.
name|flow
argument_list|(
literal|1
argument_list|)
expr_stmt|;
name|AmqpMessage
name|received
init|=
name|receiver
operator|.
name|receive
argument_list|(
literal|1
argument_list|,
name|TimeUnit
operator|.
name|SECONDS
argument_list|)
decl_stmt|;
name|assertNull
argument_list|(
name|received
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|queueView
operator|.
name|getExpiredCount
argument_list|()
argument_list|)
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
name|testSendMessageThatIsNotExpiredUsingAbsoluteTimeWithElspsedTTL
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
name|AmqpSession
name|session
init|=
name|connection
operator|.
name|createSession
argument_list|()
decl_stmt|;
name|AmqpSender
name|sender
init|=
name|session
operator|.
name|createSender
argument_list|(
name|getTestName
argument_list|()
argument_list|)
decl_stmt|;
comment|// Get the Queue View early to avoid racing the delivery.
specifier|final
name|QueueViewMBean
name|queueView
init|=
name|getProxyToQueue
argument_list|(
name|getTestName
argument_list|()
argument_list|)
decl_stmt|;
name|assertNotNull
argument_list|(
name|queueView
argument_list|)
expr_stmt|;
name|AmqpMessage
name|message
init|=
operator|new
name|AmqpMessage
argument_list|()
decl_stmt|;
name|message
operator|.
name|setAbsoluteExpiryTime
argument_list|(
name|System
operator|.
name|currentTimeMillis
argument_list|()
operator|+
literal|5000
argument_list|)
expr_stmt|;
comment|// AET should override any TTL set
name|message
operator|.
name|setTimeToLive
argument_list|(
literal|10
argument_list|)
expr_stmt|;
name|message
operator|.
name|setText
argument_list|(
literal|"Test-Message"
argument_list|)
expr_stmt|;
name|sender
operator|.
name|send
argument_list|(
name|message
argument_list|)
expr_stmt|;
name|sender
operator|.
name|close
argument_list|()
expr_stmt|;
name|Thread
operator|.
name|sleep
argument_list|(
literal|50
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|queueView
operator|.
name|getQueueSize
argument_list|()
argument_list|)
expr_stmt|;
comment|// Now try and get the message
name|AmqpReceiver
name|receiver
init|=
name|session
operator|.
name|createReceiver
argument_list|(
name|getTestName
argument_list|()
argument_list|)
decl_stmt|;
name|receiver
operator|.
name|flow
argument_list|(
literal|1
argument_list|)
expr_stmt|;
name|AmqpMessage
name|received
init|=
name|receiver
operator|.
name|receive
argument_list|(
literal|5
argument_list|,
name|TimeUnit
operator|.
name|SECONDS
argument_list|)
decl_stmt|;
name|assertNotNull
argument_list|(
name|received
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|queueView
operator|.
name|getExpiredCount
argument_list|()
argument_list|)
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
name|testSendMessageThatIsNotExpiredUsingTimeToLive
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
name|AmqpSession
name|session
init|=
name|connection
operator|.
name|createSession
argument_list|()
decl_stmt|;
name|AmqpSender
name|sender
init|=
name|session
operator|.
name|createSender
argument_list|(
name|getTestName
argument_list|()
argument_list|)
decl_stmt|;
comment|// Get the Queue View early to avoid racing the delivery.
specifier|final
name|QueueViewMBean
name|queueView
init|=
name|getProxyToQueue
argument_list|(
name|getTestName
argument_list|()
argument_list|)
decl_stmt|;
name|assertNotNull
argument_list|(
name|queueView
argument_list|)
expr_stmt|;
name|AmqpMessage
name|message
init|=
operator|new
name|AmqpMessage
argument_list|()
decl_stmt|;
name|message
operator|.
name|setTimeToLive
argument_list|(
literal|5000
argument_list|)
expr_stmt|;
name|message
operator|.
name|setText
argument_list|(
literal|"Test-Message"
argument_list|)
expr_stmt|;
name|sender
operator|.
name|send
argument_list|(
name|message
argument_list|)
expr_stmt|;
name|sender
operator|.
name|close
argument_list|()
expr_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|queueView
operator|.
name|getQueueSize
argument_list|()
argument_list|)
expr_stmt|;
comment|// Now try and get the message
name|AmqpReceiver
name|receiver
init|=
name|session
operator|.
name|createReceiver
argument_list|(
name|getTestName
argument_list|()
argument_list|)
decl_stmt|;
name|receiver
operator|.
name|flow
argument_list|(
literal|1
argument_list|)
expr_stmt|;
name|AmqpMessage
name|received
init|=
name|receiver
operator|.
name|receive
argument_list|(
literal|5
argument_list|,
name|TimeUnit
operator|.
name|SECONDS
argument_list|)
decl_stmt|;
name|assertNotNull
argument_list|(
name|received
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|queueView
operator|.
name|getExpiredCount
argument_list|()
argument_list|)
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
name|testSendMessageThenAllowToExpiredUsingTimeToLive
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
name|AmqpSession
name|session
init|=
name|connection
operator|.
name|createSession
argument_list|()
decl_stmt|;
name|AmqpSender
name|sender
init|=
name|session
operator|.
name|createSender
argument_list|(
name|getTestName
argument_list|()
argument_list|)
decl_stmt|;
comment|// Get the Queue View early to avoid racing the delivery.
specifier|final
name|QueueViewMBean
name|queueView
init|=
name|getProxyToQueue
argument_list|(
name|getTestName
argument_list|()
argument_list|)
decl_stmt|;
name|assertNotNull
argument_list|(
name|queueView
argument_list|)
expr_stmt|;
name|AmqpMessage
name|message
init|=
operator|new
name|AmqpMessage
argument_list|()
decl_stmt|;
name|message
operator|.
name|setTimeToLive
argument_list|(
literal|10
argument_list|)
expr_stmt|;
name|message
operator|.
name|setText
argument_list|(
literal|"Test-Message"
argument_list|)
expr_stmt|;
name|sender
operator|.
name|send
argument_list|(
name|message
argument_list|)
expr_stmt|;
name|sender
operator|.
name|close
argument_list|()
expr_stmt|;
name|Thread
operator|.
name|sleep
argument_list|(
literal|50
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|queueView
operator|.
name|getQueueSize
argument_list|()
argument_list|)
expr_stmt|;
comment|// Now try and get the message
name|AmqpReceiver
name|receiver
init|=
name|session
operator|.
name|createReceiver
argument_list|(
name|getTestName
argument_list|()
argument_list|)
decl_stmt|;
name|receiver
operator|.
name|flow
argument_list|(
literal|1
argument_list|)
expr_stmt|;
name|AmqpMessage
name|received
init|=
name|receiver
operator|.
name|receive
argument_list|(
literal|1
argument_list|,
name|TimeUnit
operator|.
name|SECONDS
argument_list|)
decl_stmt|;
name|assertNull
argument_list|(
name|received
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|queueView
operator|.
name|getExpiredCount
argument_list|()
argument_list|)
expr_stmt|;
name|connection
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
end_class

end_unit

