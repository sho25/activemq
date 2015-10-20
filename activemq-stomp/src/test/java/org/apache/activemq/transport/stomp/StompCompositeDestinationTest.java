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
name|stomp
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
name|assertTrue
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
name|javax
operator|.
name|jms
operator|.
name|Connection
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|jms
operator|.
name|Destination
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|jms
operator|.
name|JMSException
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|jms
operator|.
name|MessageProducer
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|jms
operator|.
name|Session
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|jms
operator|.
name|TextMessage
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
name|ActiveMQConnection
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
name|BrokerViewMBean
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
name|broker
operator|.
name|jmx
operator|.
name|TopicViewMBean
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
comment|/**  * Tests for support of composite destination support over STOMP  */
end_comment

begin_class
specifier|public
class|class
name|StompCompositeDestinationTest
extends|extends
name|StompTestSupport
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
name|StompCompositeDestinationTest
operator|.
name|class
argument_list|)
decl_stmt|;
specifier|protected
name|ActiveMQConnection
name|connection
decl_stmt|;
annotation|@
name|Override
specifier|public
name|void
name|tearDown
parameter_list|()
throws|throws
name|Exception
block|{
try|try
block|{
name|connection
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|ex
parameter_list|)
block|{}
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
literal|20000
argument_list|)
specifier|public
name|void
name|testSubscribeToCompositeQueue
parameter_list|()
throws|throws
name|Exception
block|{
name|stompConnect
argument_list|()
expr_stmt|;
name|String
name|destinationA
init|=
literal|"StompA"
decl_stmt|;
name|String
name|destinationB
init|=
literal|"StompB"
decl_stmt|;
name|String
name|frame
init|=
literal|"CONNECT\n"
operator|+
literal|"login:system\n"
operator|+
literal|"passcode:manager\n\n"
operator|+
name|Stomp
operator|.
name|NULL
decl_stmt|;
name|stompConnection
operator|.
name|sendFrame
argument_list|(
name|frame
argument_list|)
expr_stmt|;
name|frame
operator|=
name|stompConnection
operator|.
name|receiveFrame
argument_list|()
expr_stmt|;
name|assertTrue
argument_list|(
name|frame
operator|.
name|startsWith
argument_list|(
literal|"CONNECTED"
argument_list|)
argument_list|)
expr_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"Subscribing to destination: {},{}"
argument_list|,
name|destinationA
argument_list|,
name|destinationB
argument_list|)
expr_stmt|;
name|frame
operator|=
literal|"SUBSCRIBE\n"
operator|+
literal|"destination:/queue/"
operator|+
name|destinationA
operator|+
literal|",/queue/"
operator|+
name|destinationB
operator|+
literal|"\n"
operator|+
literal|"ack:auto\n\n"
operator|+
name|Stomp
operator|.
name|NULL
expr_stmt|;
name|stompConnection
operator|.
name|sendFrame
argument_list|(
name|frame
argument_list|)
expr_stmt|;
comment|// Test in same order as the subscribe command
name|sendMessage
argument_list|(
name|destinationA
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|sendMessage
argument_list|(
name|destinationB
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|frame
operator|=
name|stompConnection
operator|.
name|receiveFrame
argument_list|()
expr_stmt|;
name|assertTrue
argument_list|(
name|frame
operator|.
name|startsWith
argument_list|(
literal|"MESSAGE"
argument_list|)
argument_list|)
expr_stmt|;
name|frame
operator|=
name|stompConnection
operator|.
name|receiveFrame
argument_list|()
expr_stmt|;
name|assertTrue
argument_list|(
name|frame
operator|.
name|startsWith
argument_list|(
literal|"MESSAGE"
argument_list|)
argument_list|)
expr_stmt|;
comment|// Test the reverse ordering
name|sendMessage
argument_list|(
name|destinationB
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|sendMessage
argument_list|(
name|destinationA
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|frame
operator|=
name|stompConnection
operator|.
name|receiveFrame
argument_list|()
expr_stmt|;
name|assertTrue
argument_list|(
name|frame
operator|.
name|startsWith
argument_list|(
literal|"MESSAGE"
argument_list|)
argument_list|)
expr_stmt|;
name|frame
operator|=
name|stompConnection
operator|.
name|receiveFrame
argument_list|()
expr_stmt|;
name|assertTrue
argument_list|(
name|frame
operator|.
name|startsWith
argument_list|(
literal|"MESSAGE"
argument_list|)
argument_list|)
expr_stmt|;
name|stompConnection
operator|.
name|disconnect
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
argument_list|(
name|timeout
operator|=
literal|20000
argument_list|)
specifier|public
name|void
name|testSubscribeToCompositeQueueTrailersDefault
parameter_list|()
throws|throws
name|Exception
block|{
name|stompConnect
argument_list|()
expr_stmt|;
name|String
name|destinationA
init|=
literal|"StompA"
decl_stmt|;
name|String
name|destinationB
init|=
literal|"StompB"
decl_stmt|;
name|String
name|frame
init|=
literal|"CONNECT\n"
operator|+
literal|"login:system\n"
operator|+
literal|"passcode:manager\n\n"
operator|+
name|Stomp
operator|.
name|NULL
decl_stmt|;
name|stompConnection
operator|.
name|sendFrame
argument_list|(
name|frame
argument_list|)
expr_stmt|;
name|frame
operator|=
name|stompConnection
operator|.
name|receiveFrame
argument_list|()
expr_stmt|;
name|assertTrue
argument_list|(
name|frame
operator|.
name|startsWith
argument_list|(
literal|"CONNECTED"
argument_list|)
argument_list|)
expr_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"Subscribing to destination: {},{}"
argument_list|,
name|destinationA
argument_list|,
name|destinationB
argument_list|)
expr_stmt|;
name|frame
operator|=
literal|"SUBSCRIBE\n"
operator|+
literal|"destination:/queue/"
operator|+
name|destinationA
operator|+
literal|","
operator|+
name|destinationB
operator|+
literal|"\n"
operator|+
literal|"ack:auto\n\n"
operator|+
name|Stomp
operator|.
name|NULL
expr_stmt|;
name|stompConnection
operator|.
name|sendFrame
argument_list|(
name|frame
argument_list|)
expr_stmt|;
comment|// Test in same order as the subscribe command
name|sendMessage
argument_list|(
name|destinationA
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|sendMessage
argument_list|(
name|destinationB
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|frame
operator|=
name|stompConnection
operator|.
name|receiveFrame
argument_list|()
expr_stmt|;
name|assertTrue
argument_list|(
name|frame
operator|.
name|startsWith
argument_list|(
literal|"MESSAGE"
argument_list|)
argument_list|)
expr_stmt|;
name|frame
operator|=
name|stompConnection
operator|.
name|receiveFrame
argument_list|()
expr_stmt|;
name|assertTrue
argument_list|(
name|frame
operator|.
name|startsWith
argument_list|(
literal|"MESSAGE"
argument_list|)
argument_list|)
expr_stmt|;
comment|// Test the reverse ordering
name|sendMessage
argument_list|(
name|destinationB
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|sendMessage
argument_list|(
name|destinationA
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|frame
operator|=
name|stompConnection
operator|.
name|receiveFrame
argument_list|()
expr_stmt|;
name|assertTrue
argument_list|(
name|frame
operator|.
name|startsWith
argument_list|(
literal|"MESSAGE"
argument_list|)
argument_list|)
expr_stmt|;
name|frame
operator|=
name|stompConnection
operator|.
name|receiveFrame
argument_list|()
expr_stmt|;
name|assertTrue
argument_list|(
name|frame
operator|.
name|startsWith
argument_list|(
literal|"MESSAGE"
argument_list|)
argument_list|)
expr_stmt|;
name|stompConnection
operator|.
name|disconnect
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
argument_list|(
name|timeout
operator|=
literal|20000
argument_list|)
specifier|public
name|void
name|testSubscribeToCompositeTopics
parameter_list|()
throws|throws
name|Exception
block|{
name|stompConnect
argument_list|()
expr_stmt|;
name|String
name|destinationA
init|=
literal|"StompA"
decl_stmt|;
name|String
name|destinationB
init|=
literal|"StompB"
decl_stmt|;
name|String
name|frame
init|=
literal|"CONNECT\n"
operator|+
literal|"login:system\n"
operator|+
literal|"passcode:manager\n\n"
operator|+
name|Stomp
operator|.
name|NULL
decl_stmt|;
name|stompConnection
operator|.
name|sendFrame
argument_list|(
name|frame
argument_list|)
expr_stmt|;
name|frame
operator|=
name|stompConnection
operator|.
name|receiveFrame
argument_list|()
expr_stmt|;
name|assertTrue
argument_list|(
name|frame
operator|.
name|startsWith
argument_list|(
literal|"CONNECTED"
argument_list|)
argument_list|)
expr_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"Subscribing to destination: {},{}"
argument_list|,
name|destinationA
argument_list|,
name|destinationB
argument_list|)
expr_stmt|;
name|frame
operator|=
literal|"SUBSCRIBE\n"
operator|+
literal|"destination:/topic/"
operator|+
name|destinationA
operator|+
literal|",/topic/"
operator|+
name|destinationB
operator|+
literal|"\n"
operator|+
literal|"ack:auto\n\n"
operator|+
name|Stomp
operator|.
name|NULL
expr_stmt|;
name|stompConnection
operator|.
name|sendFrame
argument_list|(
name|frame
argument_list|)
expr_stmt|;
comment|// Test in same order as the subscribe command
name|sendMessage
argument_list|(
name|destinationA
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|sendMessage
argument_list|(
name|destinationB
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|frame
operator|=
name|stompConnection
operator|.
name|receiveFrame
argument_list|()
expr_stmt|;
name|assertTrue
argument_list|(
name|frame
operator|.
name|startsWith
argument_list|(
literal|"MESSAGE"
argument_list|)
argument_list|)
expr_stmt|;
name|frame
operator|=
name|stompConnection
operator|.
name|receiveFrame
argument_list|()
expr_stmt|;
name|assertTrue
argument_list|(
name|frame
operator|.
name|startsWith
argument_list|(
literal|"MESSAGE"
argument_list|)
argument_list|)
expr_stmt|;
comment|// Test the reverse ordering
name|sendMessage
argument_list|(
name|destinationB
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|sendMessage
argument_list|(
name|destinationA
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|frame
operator|=
name|stompConnection
operator|.
name|receiveFrame
argument_list|()
expr_stmt|;
name|assertTrue
argument_list|(
name|frame
operator|.
name|startsWith
argument_list|(
literal|"MESSAGE"
argument_list|)
argument_list|)
expr_stmt|;
name|frame
operator|=
name|stompConnection
operator|.
name|receiveFrame
argument_list|()
expr_stmt|;
name|assertTrue
argument_list|(
name|frame
operator|.
name|startsWith
argument_list|(
literal|"MESSAGE"
argument_list|)
argument_list|)
expr_stmt|;
name|stompConnection
operator|.
name|disconnect
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
name|testSendMessageToCompositeQueue
parameter_list|()
throws|throws
name|Exception
block|{
name|stompConnect
argument_list|()
expr_stmt|;
name|String
name|destinationA
init|=
literal|"StompA"
decl_stmt|;
name|String
name|destinationB
init|=
literal|"StompB"
decl_stmt|;
name|String
name|frame
init|=
literal|"CONNECT\n"
operator|+
literal|"login:system\n"
operator|+
literal|"passcode:manager\n\n"
operator|+
name|Stomp
operator|.
name|NULL
decl_stmt|;
name|stompConnection
operator|.
name|sendFrame
argument_list|(
name|frame
argument_list|)
expr_stmt|;
name|frame
operator|=
name|stompConnection
operator|.
name|receiveFrame
argument_list|()
expr_stmt|;
name|assertTrue
argument_list|(
name|frame
operator|.
name|startsWith
argument_list|(
literal|"CONNECTED"
argument_list|)
argument_list|)
expr_stmt|;
name|frame
operator|=
literal|"SEND\n"
operator|+
literal|"destination:/queue/"
operator|+
name|destinationA
operator|+
literal|",/queue/"
operator|+
name|destinationB
operator|+
literal|"\n\n"
operator|+
literal|"Hello World"
operator|+
name|Stomp
operator|.
name|NULL
expr_stmt|;
name|stompConnection
operator|.
name|sendFrame
argument_list|(
name|frame
argument_list|)
expr_stmt|;
specifier|final
name|BrokerViewMBean
name|brokerView
init|=
name|getProxyToBroker
argument_list|()
decl_stmt|;
name|assertTrue
argument_list|(
literal|"Should be two destinations for the dispatch"
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
name|brokerView
operator|.
name|getQueues
argument_list|()
operator|.
name|length
operator|==
literal|2
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
literal|30
argument_list|)
argument_list|,
name|TimeUnit
operator|.
name|MILLISECONDS
operator|.
name|toMillis
argument_list|(
literal|150
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|QueueViewMBean
name|viewOfA
init|=
name|getProxyToQueue
argument_list|(
name|destinationA
argument_list|)
decl_stmt|;
name|QueueViewMBean
name|viewOfB
init|=
name|getProxyToQueue
argument_list|(
name|destinationB
argument_list|)
decl_stmt|;
name|assertNotNull
argument_list|(
name|viewOfA
argument_list|)
expr_stmt|;
name|assertNotNull
argument_list|(
name|viewOfB
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|viewOfA
operator|.
name|getQueueSize
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|viewOfB
operator|.
name|getQueueSize
argument_list|()
argument_list|)
expr_stmt|;
name|stompConnection
operator|.
name|disconnect
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
name|testSendMessageToCompositeTopic
parameter_list|()
throws|throws
name|Exception
block|{
name|stompConnect
argument_list|()
expr_stmt|;
name|String
name|destinationA
init|=
literal|"StompA"
decl_stmt|;
name|String
name|destinationB
init|=
literal|"StompB"
decl_stmt|;
name|String
name|frame
init|=
literal|"CONNECT\n"
operator|+
literal|"login:system\n"
operator|+
literal|"passcode:manager\n\n"
operator|+
name|Stomp
operator|.
name|NULL
decl_stmt|;
name|stompConnection
operator|.
name|sendFrame
argument_list|(
name|frame
argument_list|)
expr_stmt|;
name|frame
operator|=
name|stompConnection
operator|.
name|receiveFrame
argument_list|()
expr_stmt|;
name|assertTrue
argument_list|(
name|frame
operator|.
name|startsWith
argument_list|(
literal|"CONNECTED"
argument_list|)
argument_list|)
expr_stmt|;
name|frame
operator|=
literal|"SEND\n"
operator|+
literal|"destination:/topic/"
operator|+
name|destinationA
operator|+
literal|",/topic/"
operator|+
name|destinationB
operator|+
literal|"\n\n"
operator|+
literal|"Hello World"
operator|+
name|Stomp
operator|.
name|NULL
expr_stmt|;
name|stompConnection
operator|.
name|sendFrame
argument_list|(
name|frame
argument_list|)
expr_stmt|;
specifier|final
name|BrokerViewMBean
name|brokerView
init|=
name|getProxyToBroker
argument_list|()
decl_stmt|;
name|assertTrue
argument_list|(
literal|"Should be two destinations for the dispatch"
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
name|brokerView
operator|.
name|getTopics
argument_list|()
operator|.
name|length
operator|==
literal|2
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
literal|30
argument_list|)
argument_list|,
name|TimeUnit
operator|.
name|MILLISECONDS
operator|.
name|toMillis
argument_list|(
literal|150
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|TopicViewMBean
name|viewOfA
init|=
name|getProxyToTopic
argument_list|(
name|destinationA
argument_list|)
decl_stmt|;
name|TopicViewMBean
name|viewOfB
init|=
name|getProxyToTopic
argument_list|(
name|destinationB
argument_list|)
decl_stmt|;
name|assertNotNull
argument_list|(
name|viewOfA
argument_list|)
expr_stmt|;
name|assertNotNull
argument_list|(
name|viewOfB
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|viewOfA
operator|.
name|getEnqueueCount
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|viewOfB
operator|.
name|getEnqueueCount
argument_list|()
argument_list|)
expr_stmt|;
name|stompConnection
operator|.
name|disconnect
argument_list|()
expr_stmt|;
block|}
specifier|private
name|void
name|sendMessage
parameter_list|(
name|String
name|destinationName
parameter_list|,
name|boolean
name|topic
parameter_list|)
throws|throws
name|JMSException
block|{
name|Connection
name|connection
init|=
name|cf
operator|.
name|createConnection
argument_list|(
literal|"system"
argument_list|,
literal|"manager"
argument_list|)
decl_stmt|;
name|Session
name|session
init|=
name|connection
operator|.
name|createSession
argument_list|(
literal|false
argument_list|,
name|Session
operator|.
name|AUTO_ACKNOWLEDGE
argument_list|)
decl_stmt|;
name|Destination
name|destination
init|=
literal|null
decl_stmt|;
if|if
condition|(
name|topic
condition|)
block|{
name|destination
operator|=
name|session
operator|.
name|createTopic
argument_list|(
name|destinationName
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|destination
operator|=
name|session
operator|.
name|createQueue
argument_list|(
name|destinationName
argument_list|)
expr_stmt|;
block|}
name|MessageProducer
name|producer
init|=
name|session
operator|.
name|createProducer
argument_list|(
name|destination
argument_list|)
decl_stmt|;
name|TextMessage
name|message
init|=
name|session
operator|.
name|createTextMessage
argument_list|(
literal|"test"
argument_list|)
decl_stmt|;
name|producer
operator|.
name|send
argument_list|(
name|message
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
