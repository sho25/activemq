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
name|JMS_SELECTOR_FILTER_IDS
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
name|NO_LOCAL_FILTER_IDS
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
name|findFilter
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
name|HashMap
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
name|AmqpSession
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
name|AmqpUnknownFilterType
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
name|amqp
operator|.
name|DescribedType
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
name|messaging
operator|.
name|Source
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
name|messaging
operator|.
name|TerminusDurability
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
name|messaging
operator|.
name|TerminusExpiryPolicy
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
name|Receiver
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
name|message
operator|.
name|Message
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
comment|/**  * Test various behaviors of AMQP receivers with the broker.  */
end_comment

begin_class
specifier|public
class|class
name|AmqpReceiverTest
extends|extends
name|AmqpClientTestSupport
block|{
annotation|@
name|Override
specifier|protected
name|boolean
name|isUseOpenWireConnector
parameter_list|()
block|{
return|return
literal|true
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
name|testCreateQueueReceiver
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
name|client
operator|.
name|connect
argument_list|()
decl_stmt|;
name|AmqpSession
name|session
init|=
name|connection
operator|.
name|createSession
argument_list|()
decl_stmt|;
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|brokerService
operator|.
name|getAdminView
argument_list|()
operator|.
name|getQueues
argument_list|()
operator|.
name|length
argument_list|)
expr_stmt|;
name|AmqpReceiver
name|receiver
init|=
name|session
operator|.
name|createReceiver
argument_list|(
literal|"queue://"
operator|+
name|getTestName
argument_list|()
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|brokerService
operator|.
name|getAdminView
argument_list|()
operator|.
name|getQueues
argument_list|()
operator|.
name|length
argument_list|)
expr_stmt|;
name|assertNotNull
argument_list|(
name|getProxyToQueue
argument_list|(
name|getTestName
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|brokerService
operator|.
name|getAdminView
argument_list|()
operator|.
name|getQueueSubscribers
argument_list|()
operator|.
name|length
argument_list|)
expr_stmt|;
name|receiver
operator|.
name|close
argument_list|()
expr_stmt|;
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|brokerService
operator|.
name|getAdminView
argument_list|()
operator|.
name|getQueueSubscribers
argument_list|()
operator|.
name|length
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
name|testCreateQueueReceiverWithJMSSelector
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
name|client
operator|.
name|setValidator
argument_list|(
operator|new
name|AmqpValidator
argument_list|()
block|{
annotation|@
name|SuppressWarnings
argument_list|(
literal|"unchecked"
argument_list|)
annotation|@
name|Override
specifier|public
name|void
name|inspectOpenedResource
parameter_list|(
name|Receiver
name|receiver
parameter_list|)
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"Receiver opened: {}"
argument_list|,
name|receiver
argument_list|)
expr_stmt|;
if|if
condition|(
name|receiver
operator|.
name|getRemoteSource
argument_list|()
operator|==
literal|null
condition|)
block|{
name|markAsInvalid
argument_list|(
literal|"Link opened with null source."
argument_list|)
expr_stmt|;
block|}
name|Source
name|source
init|=
operator|(
name|Source
operator|)
name|receiver
operator|.
name|getRemoteSource
argument_list|()
decl_stmt|;
name|Map
argument_list|<
name|Symbol
argument_list|,
name|Object
argument_list|>
name|filters
init|=
name|source
operator|.
name|getFilter
argument_list|()
decl_stmt|;
if|if
condition|(
name|findFilter
argument_list|(
name|filters
argument_list|,
name|JMS_SELECTOR_FILTER_IDS
argument_list|)
operator|==
literal|null
condition|)
block|{
name|markAsInvalid
argument_list|(
literal|"Broker did not return the JMS Filter on Attach"
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
name|AmqpSession
name|session
init|=
name|connection
operator|.
name|createSession
argument_list|()
decl_stmt|;
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|brokerService
operator|.
name|getAdminView
argument_list|()
operator|.
name|getQueues
argument_list|()
operator|.
name|length
argument_list|)
expr_stmt|;
name|session
operator|.
name|createReceiver
argument_list|(
literal|"queue://"
operator|+
name|getTestName
argument_list|()
argument_list|,
literal|"JMSPriority> 8"
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|brokerService
operator|.
name|getAdminView
argument_list|()
operator|.
name|getQueueSubscribers
argument_list|()
operator|.
name|length
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
name|testCreateQueueReceiverWithNoLocalSet
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
name|client
operator|.
name|setValidator
argument_list|(
operator|new
name|AmqpValidator
argument_list|()
block|{
annotation|@
name|SuppressWarnings
argument_list|(
literal|"unchecked"
argument_list|)
annotation|@
name|Override
specifier|public
name|void
name|inspectOpenedResource
parameter_list|(
name|Receiver
name|receiver
parameter_list|)
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"Receiver opened: {}"
argument_list|,
name|receiver
argument_list|)
expr_stmt|;
if|if
condition|(
name|receiver
operator|.
name|getRemoteSource
argument_list|()
operator|==
literal|null
condition|)
block|{
name|markAsInvalid
argument_list|(
literal|"Link opened with null source."
argument_list|)
expr_stmt|;
block|}
name|Source
name|source
init|=
operator|(
name|Source
operator|)
name|receiver
operator|.
name|getRemoteSource
argument_list|()
decl_stmt|;
name|Map
argument_list|<
name|Symbol
argument_list|,
name|Object
argument_list|>
name|filters
init|=
name|source
operator|.
name|getFilter
argument_list|()
decl_stmt|;
if|if
condition|(
name|findFilter
argument_list|(
name|filters
argument_list|,
name|NO_LOCAL_FILTER_IDS
argument_list|)
operator|==
literal|null
condition|)
block|{
name|markAsInvalid
argument_list|(
literal|"Broker did not return the NoLocal Filter on Attach"
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
name|AmqpSession
name|session
init|=
name|connection
operator|.
name|createSession
argument_list|()
decl_stmt|;
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|brokerService
operator|.
name|getAdminView
argument_list|()
operator|.
name|getQueues
argument_list|()
operator|.
name|length
argument_list|)
expr_stmt|;
name|session
operator|.
name|createReceiver
argument_list|(
literal|"queue://"
operator|+
name|getTestName
argument_list|()
argument_list|,
literal|null
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|brokerService
operator|.
name|getAdminView
argument_list|()
operator|.
name|getQueueSubscribers
argument_list|()
operator|.
name|length
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
name|testCreateTopicReceiver
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
name|client
operator|.
name|connect
argument_list|()
decl_stmt|;
name|AmqpSession
name|session
init|=
name|connection
operator|.
name|createSession
argument_list|()
decl_stmt|;
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|brokerService
operator|.
name|getAdminView
argument_list|()
operator|.
name|getTopics
argument_list|()
operator|.
name|length
argument_list|)
expr_stmt|;
name|AmqpReceiver
name|receiver
init|=
name|session
operator|.
name|createReceiver
argument_list|(
literal|"topic://"
operator|+
name|getTestName
argument_list|()
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|brokerService
operator|.
name|getAdminView
argument_list|()
operator|.
name|getTopics
argument_list|()
operator|.
name|length
argument_list|)
expr_stmt|;
name|assertNotNull
argument_list|(
name|getProxyToTopic
argument_list|(
name|getTestName
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|brokerService
operator|.
name|getAdminView
argument_list|()
operator|.
name|getTopicSubscribers
argument_list|()
operator|.
name|length
argument_list|)
expr_stmt|;
name|receiver
operator|.
name|close
argument_list|()
expr_stmt|;
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|brokerService
operator|.
name|getAdminView
argument_list|()
operator|.
name|getTopicSubscribers
argument_list|()
operator|.
name|length
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
name|testQueueReceiverReadMessage
parameter_list|()
throws|throws
name|Exception
block|{
name|sendMessages
argument_list|(
name|getTestName
argument_list|()
argument_list|,
literal|1
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|AmqpClient
name|client
init|=
name|createAmqpClient
argument_list|()
decl_stmt|;
name|AmqpConnection
name|connection
init|=
name|client
operator|.
name|connect
argument_list|()
decl_stmt|;
name|AmqpSession
name|session
init|=
name|connection
operator|.
name|createSession
argument_list|()
decl_stmt|;
name|AmqpReceiver
name|receiver
init|=
name|session
operator|.
name|createReceiver
argument_list|(
literal|"queue://"
operator|+
name|getTestName
argument_list|()
argument_list|)
decl_stmt|;
name|QueueViewMBean
name|queueView
init|=
name|getProxyToQueue
argument_list|(
name|getTestName
argument_list|()
argument_list|)
decl_stmt|;
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
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|queueView
operator|.
name|getDispatchCount
argument_list|()
argument_list|)
expr_stmt|;
name|receiver
operator|.
name|flow
argument_list|(
literal|1
argument_list|)
expr_stmt|;
name|assertNotNull
argument_list|(
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
argument_list|)
expr_stmt|;
name|receiver
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
name|testTwoQueueReceiversOnSameConnectionReadMessagesNoDispositions
parameter_list|()
throws|throws
name|Exception
block|{
name|int
name|MSG_COUNT
init|=
literal|4
decl_stmt|;
name|sendMessages
argument_list|(
name|getTestName
argument_list|()
argument_list|,
name|MSG_COUNT
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|AmqpClient
name|client
init|=
name|createAmqpClient
argument_list|()
decl_stmt|;
name|AmqpConnection
name|connection
init|=
name|client
operator|.
name|connect
argument_list|()
decl_stmt|;
name|AmqpSession
name|session
init|=
name|connection
operator|.
name|createSession
argument_list|()
decl_stmt|;
name|AmqpReceiver
name|receiver1
init|=
name|session
operator|.
name|createReceiver
argument_list|(
literal|"queue://"
operator|+
name|getTestName
argument_list|()
argument_list|)
decl_stmt|;
name|QueueViewMBean
name|queueView
init|=
name|getProxyToQueue
argument_list|(
name|getTestName
argument_list|()
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
name|MSG_COUNT
argument_list|,
name|queueView
operator|.
name|getQueueSize
argument_list|()
argument_list|)
expr_stmt|;
name|receiver1
operator|.
name|flow
argument_list|(
literal|2
argument_list|)
expr_stmt|;
name|assertNotNull
argument_list|(
name|receiver1
operator|.
name|receive
argument_list|(
literal|5
argument_list|,
name|TimeUnit
operator|.
name|SECONDS
argument_list|)
argument_list|)
expr_stmt|;
name|assertNotNull
argument_list|(
name|receiver1
operator|.
name|receive
argument_list|(
literal|5
argument_list|,
name|TimeUnit
operator|.
name|SECONDS
argument_list|)
argument_list|)
expr_stmt|;
name|AmqpReceiver
name|receiver2
init|=
name|session
operator|.
name|createReceiver
argument_list|(
literal|"queue://"
operator|+
name|getTestName
argument_list|()
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|2
argument_list|,
name|brokerService
operator|.
name|getAdminView
argument_list|()
operator|.
name|getQueueSubscribers
argument_list|()
operator|.
name|length
argument_list|)
expr_stmt|;
name|receiver2
operator|.
name|flow
argument_list|(
literal|2
argument_list|)
expr_stmt|;
name|assertNotNull
argument_list|(
name|receiver2
operator|.
name|receive
argument_list|(
literal|5
argument_list|,
name|TimeUnit
operator|.
name|SECONDS
argument_list|)
argument_list|)
expr_stmt|;
name|assertNotNull
argument_list|(
name|receiver2
operator|.
name|receive
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
name|MSG_COUNT
argument_list|,
name|queueView
operator|.
name|getDispatchCount
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|queueView
operator|.
name|getDequeueCount
argument_list|()
argument_list|)
expr_stmt|;
name|receiver1
operator|.
name|close
argument_list|()
expr_stmt|;
name|receiver2
operator|.
name|close
argument_list|()
expr_stmt|;
name|assertEquals
argument_list|(
name|MSG_COUNT
argument_list|,
name|queueView
operator|.
name|getQueueSize
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
name|testTwoQueueReceiversOnSameConnectionReadMessagesAcceptOnEach
parameter_list|()
throws|throws
name|Exception
block|{
name|int
name|MSG_COUNT
init|=
literal|4
decl_stmt|;
name|sendMessages
argument_list|(
name|getTestName
argument_list|()
argument_list|,
name|MSG_COUNT
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|AmqpClient
name|client
init|=
name|createAmqpClient
argument_list|()
decl_stmt|;
name|AmqpConnection
name|connection
init|=
name|client
operator|.
name|connect
argument_list|()
decl_stmt|;
name|AmqpSession
name|session
init|=
name|connection
operator|.
name|createSession
argument_list|()
decl_stmt|;
name|AmqpReceiver
name|receiver1
init|=
name|session
operator|.
name|createReceiver
argument_list|(
literal|"queue://"
operator|+
name|getTestName
argument_list|()
argument_list|)
decl_stmt|;
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
name|assertEquals
argument_list|(
name|MSG_COUNT
argument_list|,
name|queueView
operator|.
name|getQueueSize
argument_list|()
argument_list|)
expr_stmt|;
name|receiver1
operator|.
name|flow
argument_list|(
literal|2
argument_list|)
expr_stmt|;
name|AmqpMessage
name|message
init|=
name|receiver1
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
name|message
argument_list|)
expr_stmt|;
name|message
operator|.
name|accept
argument_list|()
expr_stmt|;
name|message
operator|=
name|receiver1
operator|.
name|receive
argument_list|(
literal|5
argument_list|,
name|TimeUnit
operator|.
name|SECONDS
argument_list|)
expr_stmt|;
name|assertNotNull
argument_list|(
name|message
argument_list|)
expr_stmt|;
name|message
operator|.
name|accept
argument_list|()
expr_stmt|;
name|assertTrue
argument_list|(
literal|"Should have ack'd two"
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
name|queueView
operator|.
name|getDequeueCount
argument_list|()
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
literal|5
argument_list|)
argument_list|,
name|TimeUnit
operator|.
name|MILLISECONDS
operator|.
name|toMillis
argument_list|(
literal|50
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|AmqpReceiver
name|receiver2
init|=
name|session
operator|.
name|createReceiver
argument_list|(
literal|"queue://"
operator|+
name|getTestName
argument_list|()
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|2
argument_list|,
name|brokerService
operator|.
name|getAdminView
argument_list|()
operator|.
name|getQueueSubscribers
argument_list|()
operator|.
name|length
argument_list|)
expr_stmt|;
name|receiver2
operator|.
name|flow
argument_list|(
literal|2
argument_list|)
expr_stmt|;
name|message
operator|=
name|receiver2
operator|.
name|receive
argument_list|(
literal|5
argument_list|,
name|TimeUnit
operator|.
name|SECONDS
argument_list|)
expr_stmt|;
name|assertNotNull
argument_list|(
name|message
argument_list|)
expr_stmt|;
name|message
operator|.
name|accept
argument_list|()
expr_stmt|;
name|message
operator|=
name|receiver2
operator|.
name|receive
argument_list|(
literal|5
argument_list|,
name|TimeUnit
operator|.
name|SECONDS
argument_list|)
expr_stmt|;
name|assertNotNull
argument_list|(
name|message
argument_list|)
expr_stmt|;
name|message
operator|.
name|accept
argument_list|()
expr_stmt|;
name|assertEquals
argument_list|(
name|MSG_COUNT
argument_list|,
name|queueView
operator|.
name|getDispatchCount
argument_list|()
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
literal|"Queue should be empty now"
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
name|queueView
operator|.
name|getDequeueCount
argument_list|()
operator|==
literal|4
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
literal|10
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|receiver1
operator|.
name|close
argument_list|()
expr_stmt|;
name|receiver2
operator|.
name|close
argument_list|()
expr_stmt|;
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
name|testSecondReceiverOnQueueGetsAllUnconsumedMessages
parameter_list|()
throws|throws
name|Exception
block|{
name|int
name|MSG_COUNT
init|=
literal|20
decl_stmt|;
name|sendMessages
argument_list|(
name|getTestName
argument_list|()
argument_list|,
name|MSG_COUNT
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|AmqpClient
name|client
init|=
name|createAmqpClient
argument_list|()
decl_stmt|;
name|AmqpConnection
name|connection
init|=
name|client
operator|.
name|connect
argument_list|()
decl_stmt|;
name|AmqpSession
name|session
init|=
name|connection
operator|.
name|createSession
argument_list|()
decl_stmt|;
name|AmqpReceiver
name|receiver1
init|=
name|session
operator|.
name|createReceiver
argument_list|(
literal|"queue://"
operator|+
name|getTestName
argument_list|()
argument_list|)
decl_stmt|;
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
name|assertEquals
argument_list|(
name|MSG_COUNT
argument_list|,
name|queueView
operator|.
name|getQueueSize
argument_list|()
argument_list|)
expr_stmt|;
name|receiver1
operator|.
name|flow
argument_list|(
literal|20
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
literal|"Should have dispatch to prefetch"
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
name|queueView
operator|.
name|getInFlightCount
argument_list|()
operator|>=
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
literal|5
argument_list|)
argument_list|,
name|TimeUnit
operator|.
name|MILLISECONDS
operator|.
name|toMillis
argument_list|(
literal|50
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|receiver1
operator|.
name|close
argument_list|()
expr_stmt|;
name|AmqpReceiver
name|receiver2
init|=
name|session
operator|.
name|createReceiver
argument_list|(
literal|"queue://"
operator|+
name|getTestName
argument_list|()
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|brokerService
operator|.
name|getAdminView
argument_list|()
operator|.
name|getQueueSubscribers
argument_list|()
operator|.
name|length
argument_list|)
expr_stmt|;
name|receiver2
operator|.
name|flow
argument_list|(
name|MSG_COUNT
operator|*
literal|2
argument_list|)
expr_stmt|;
name|AmqpMessage
name|message
init|=
name|receiver2
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
name|message
argument_list|)
expr_stmt|;
name|message
operator|.
name|accept
argument_list|()
expr_stmt|;
name|message
operator|=
name|receiver2
operator|.
name|receive
argument_list|(
literal|5
argument_list|,
name|TimeUnit
operator|.
name|SECONDS
argument_list|)
expr_stmt|;
name|assertNotNull
argument_list|(
name|message
argument_list|)
expr_stmt|;
name|message
operator|.
name|accept
argument_list|()
expr_stmt|;
name|assertTrue
argument_list|(
literal|"Should have ack'd two"
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
name|queueView
operator|.
name|getDequeueCount
argument_list|()
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
literal|5
argument_list|)
argument_list|,
name|TimeUnit
operator|.
name|MILLISECONDS
operator|.
name|toMillis
argument_list|(
literal|50
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|receiver2
operator|.
name|close
argument_list|()
expr_stmt|;
name|assertEquals
argument_list|(
name|MSG_COUNT
operator|-
literal|2
argument_list|,
name|queueView
operator|.
name|getQueueSize
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
name|testUnsupportedFiltersAreNotListedAsSupported
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
name|client
operator|.
name|setValidator
argument_list|(
operator|new
name|AmqpValidator
argument_list|()
block|{
annotation|@
name|SuppressWarnings
argument_list|(
literal|"unchecked"
argument_list|)
annotation|@
name|Override
specifier|public
name|void
name|inspectOpenedResource
parameter_list|(
name|Receiver
name|receiver
parameter_list|)
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"Receiver opened: {}"
argument_list|,
name|receiver
argument_list|)
expr_stmt|;
if|if
condition|(
name|receiver
operator|.
name|getRemoteSource
argument_list|()
operator|==
literal|null
condition|)
block|{
name|markAsInvalid
argument_list|(
literal|"Link opened with null source."
argument_list|)
expr_stmt|;
block|}
name|Source
name|source
init|=
operator|(
name|Source
operator|)
name|receiver
operator|.
name|getRemoteSource
argument_list|()
decl_stmt|;
name|Map
argument_list|<
name|Symbol
argument_list|,
name|Object
argument_list|>
name|filters
init|=
name|source
operator|.
name|getFilter
argument_list|()
decl_stmt|;
if|if
condition|(
name|findFilter
argument_list|(
name|filters
argument_list|,
name|AmqpUnknownFilterType
operator|.
name|UNKNOWN_FILTER_IDS
argument_list|)
operator|!=
literal|null
condition|)
block|{
name|markAsInvalid
argument_list|(
literal|"Broker should not return unsupported filter on attach."
argument_list|)
expr_stmt|;
block|}
block|}
block|}
argument_list|)
expr_stmt|;
name|Map
argument_list|<
name|Symbol
argument_list|,
name|DescribedType
argument_list|>
name|filters
init|=
operator|new
name|HashMap
argument_list|<
name|Symbol
argument_list|,
name|DescribedType
argument_list|>
argument_list|()
decl_stmt|;
name|filters
operator|.
name|put
argument_list|(
name|AmqpUnknownFilterType
operator|.
name|UNKNOWN_FILTER_NAME
argument_list|,
name|AmqpUnknownFilterType
operator|.
name|UNKOWN_FILTER
argument_list|)
expr_stmt|;
name|Source
name|source
init|=
operator|new
name|Source
argument_list|()
decl_stmt|;
name|source
operator|.
name|setAddress
argument_list|(
literal|"queue://"
operator|+
name|getTestName
argument_list|()
argument_list|)
expr_stmt|;
name|source
operator|.
name|setFilter
argument_list|(
name|filters
argument_list|)
expr_stmt|;
name|source
operator|.
name|setDurable
argument_list|(
name|TerminusDurability
operator|.
name|NONE
argument_list|)
expr_stmt|;
name|source
operator|.
name|setExpiryPolicy
argument_list|(
name|TerminusExpiryPolicy
operator|.
name|LINK_DETACH
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
name|AmqpSession
name|session
init|=
name|connection
operator|.
name|createSession
argument_list|()
decl_stmt|;
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|brokerService
operator|.
name|getAdminView
argument_list|()
operator|.
name|getQueues
argument_list|()
operator|.
name|length
argument_list|)
expr_stmt|;
name|session
operator|.
name|createReceiver
argument_list|(
name|source
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|brokerService
operator|.
name|getAdminView
argument_list|()
operator|.
name|getQueueSubscribers
argument_list|()
operator|.
name|length
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
literal|30000
argument_list|)
specifier|public
name|void
name|testModifiedDispositionWithDeliveryFailedWithoutUndeliverableHereFieldsSet
parameter_list|()
throws|throws
name|Exception
block|{
name|doModifiedDispositionTestImpl
argument_list|(
name|Boolean
operator|.
name|TRUE
argument_list|,
literal|null
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
argument_list|(
name|timeout
operator|=
literal|30000
argument_list|)
specifier|public
name|void
name|testModifiedDispositionWithoutDeliveryFailedWithoutUndeliverableHereFieldsSet
parameter_list|()
throws|throws
name|Exception
block|{
name|doModifiedDispositionTestImpl
argument_list|(
literal|null
argument_list|,
literal|null
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
argument_list|(
name|timeout
operator|=
literal|30000
argument_list|)
specifier|public
name|void
name|testModifiedDispositionWithoutDeliveryFailedWithUndeliverableHereFieldsSet
parameter_list|()
throws|throws
name|Exception
block|{
name|doModifiedDispositionTestImpl
argument_list|(
literal|null
argument_list|,
name|Boolean
operator|.
name|TRUE
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
argument_list|(
name|timeout
operator|=
literal|30000
argument_list|)
specifier|public
name|void
name|testModifiedDispositionWithDeliveryFailedWithUndeliverableHereFieldsSet
parameter_list|()
throws|throws
name|Exception
block|{
name|doModifiedDispositionTestImpl
argument_list|(
name|Boolean
operator|.
name|TRUE
argument_list|,
name|Boolean
operator|.
name|TRUE
argument_list|)
expr_stmt|;
block|}
specifier|private
name|void
name|doModifiedDispositionTestImpl
parameter_list|(
name|Boolean
name|deliveryFailed
parameter_list|,
name|Boolean
name|undeliverableHere
parameter_list|)
throws|throws
name|Exception
block|{
name|int
name|msgCount
init|=
literal|1
decl_stmt|;
name|sendMessages
argument_list|(
name|getTestName
argument_list|()
argument_list|,
name|msgCount
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|AmqpClient
name|client
init|=
name|createAmqpClient
argument_list|()
decl_stmt|;
name|AmqpConnection
name|connection
init|=
name|client
operator|.
name|connect
argument_list|()
decl_stmt|;
name|AmqpSession
name|session
init|=
name|connection
operator|.
name|createSession
argument_list|()
decl_stmt|;
name|AmqpReceiver
name|receiver
init|=
name|session
operator|.
name|createReceiver
argument_list|(
literal|"queue://"
operator|+
name|getTestName
argument_list|()
argument_list|)
decl_stmt|;
name|receiver
operator|.
name|flow
argument_list|(
literal|2
operator|*
name|msgCount
argument_list|)
expr_stmt|;
name|AmqpMessage
name|message
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
literal|"did not receive message first time"
argument_list|,
name|message
argument_list|)
expr_stmt|;
name|Message
name|protonMessage
init|=
name|message
operator|.
name|getWrappedMessage
argument_list|()
decl_stmt|;
name|assertNotNull
argument_list|(
name|protonMessage
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"Unexpected initial value for AMQP delivery-count"
argument_list|,
literal|0
argument_list|,
name|protonMessage
operator|.
name|getDeliveryCount
argument_list|()
argument_list|)
expr_stmt|;
name|message
operator|.
name|modified
argument_list|(
name|deliveryFailed
argument_list|,
name|undeliverableHere
argument_list|)
expr_stmt|;
if|if
condition|(
name|Boolean
operator|.
name|TRUE
operator|.
name|equals
argument_list|(
name|undeliverableHere
argument_list|)
condition|)
block|{
name|message
operator|=
name|receiver
operator|.
name|receive
argument_list|(
literal|250
argument_list|,
name|TimeUnit
operator|.
name|MILLISECONDS
argument_list|)
expr_stmt|;
name|assertNull
argument_list|(
literal|"Should not receive message again"
argument_list|,
name|message
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|message
operator|=
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
expr_stmt|;
name|assertNotNull
argument_list|(
literal|"did not receive message again"
argument_list|,
name|message
argument_list|)
expr_stmt|;
name|int
name|expectedDeliveryCount
init|=
literal|0
decl_stmt|;
if|if
condition|(
name|Boolean
operator|.
name|TRUE
operator|.
name|equals
argument_list|(
name|deliveryFailed
argument_list|)
condition|)
block|{
name|expectedDeliveryCount
operator|=
literal|1
expr_stmt|;
block|}
name|message
operator|.
name|accept
argument_list|()
expr_stmt|;
name|Message
name|protonMessage2
init|=
name|message
operator|.
name|getWrappedMessage
argument_list|()
decl_stmt|;
name|assertNotNull
argument_list|(
name|protonMessage2
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"Unexpected updated value for AMQP delivery-count"
argument_list|,
name|expectedDeliveryCount
argument_list|,
name|protonMessage2
operator|.
name|getDeliveryCount
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|receiver
operator|.
name|close
argument_list|()
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

