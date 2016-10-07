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
name|LIFETIME_POLICY
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
name|TEMP_QUEUE_CAPABILITY
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
name|TEMP_TOPIC_CAPABILITY
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
name|fail
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
name|DeleteOnClose
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
name|Target
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
name|junit
operator|.
name|Test
import|;
end_import

begin_comment
comment|/**  * Tests for JMS temporary destination mappings to AMQP  */
end_comment

begin_class
specifier|public
class|class
name|AmqpTempDestinationTest
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
name|testCannotCreateSenderWithNamedTempQueue
parameter_list|()
throws|throws
name|Exception
block|{
name|doTestCannotCreateSenderWithNamedTempDestination
argument_list|(
literal|false
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
name|testCannotCreateSenderWithNamedTempTopic
parameter_list|()
throws|throws
name|Exception
block|{
name|doTestCannotCreateSenderWithNamedTempDestination
argument_list|(
literal|true
argument_list|)
expr_stmt|;
block|}
specifier|protected
name|void
name|doTestCannotCreateSenderWithNamedTempDestination
parameter_list|(
name|boolean
name|topic
parameter_list|)
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
name|String
name|address
init|=
literal|null
decl_stmt|;
if|if
condition|(
name|topic
condition|)
block|{
name|address
operator|=
literal|"temp-topic://"
operator|+
name|getTestName
argument_list|()
expr_stmt|;
block|}
else|else
block|{
name|address
operator|=
literal|"temp-queue://"
operator|+
name|getTestName
argument_list|()
expr_stmt|;
block|}
try|try
block|{
name|session
operator|.
name|createSender
argument_list|(
name|address
argument_list|)
expr_stmt|;
name|fail
argument_list|(
literal|"Should not be able to create sender to a temp destination that doesn't exist."
argument_list|)
expr_stmt|;
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
literal|"Error creating sender: {}"
argument_list|,
name|ex
operator|.
name|getMessage
argument_list|()
argument_list|)
expr_stmt|;
block|}
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
name|testCanntCreateReceverWithNamedTempQueue
parameter_list|()
throws|throws
name|Exception
block|{
name|doTestCannotCreateReceiverWithNamedTempDestination
argument_list|(
literal|false
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
name|testCannotCreateReceiverWithNamedTempTopic
parameter_list|()
throws|throws
name|Exception
block|{
name|doTestCannotCreateReceiverWithNamedTempDestination
argument_list|(
literal|true
argument_list|)
expr_stmt|;
block|}
specifier|protected
name|void
name|doTestCannotCreateReceiverWithNamedTempDestination
parameter_list|(
name|boolean
name|topic
parameter_list|)
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
name|String
name|address
init|=
literal|null
decl_stmt|;
if|if
condition|(
name|topic
condition|)
block|{
name|address
operator|=
literal|"temp-topic://"
operator|+
name|getTestName
argument_list|()
expr_stmt|;
block|}
else|else
block|{
name|address
operator|=
literal|"temp-queue://"
operator|+
name|getTestName
argument_list|()
expr_stmt|;
block|}
try|try
block|{
name|session
operator|.
name|createReceiver
argument_list|(
name|address
argument_list|)
expr_stmt|;
name|fail
argument_list|(
literal|"Should not be able to create sender to a temp destination that doesn't exist."
argument_list|)
expr_stmt|;
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
literal|"Error creating sender: {}"
argument_list|,
name|ex
operator|.
name|getMessage
argument_list|()
argument_list|)
expr_stmt|;
block|}
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
name|testCreateDynamicSenderToTopic
parameter_list|()
throws|throws
name|Exception
block|{
name|doTestCreateDynamicSender
argument_list|(
literal|true
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
name|testCreateDynamicSenderToQueue
parameter_list|()
throws|throws
name|Exception
block|{
name|doTestCreateDynamicSender
argument_list|(
literal|false
argument_list|)
expr_stmt|;
block|}
specifier|protected
name|void
name|doTestCreateDynamicSender
parameter_list|(
name|boolean
name|topic
parameter_list|)
throws|throws
name|Exception
block|{
name|Target
name|target
init|=
name|createDynamicTarget
argument_list|(
name|topic
argument_list|)
decl_stmt|;
specifier|final
name|BrokerViewMBean
name|brokerView
init|=
name|getProxyToBroker
argument_list|()
decl_stmt|;
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
name|target
argument_list|)
decl_stmt|;
name|assertNotNull
argument_list|(
name|sender
argument_list|)
expr_stmt|;
if|if
condition|(
name|topic
condition|)
block|{
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|brokerView
operator|.
name|getTemporaryTopics
argument_list|()
operator|.
name|length
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|brokerView
operator|.
name|getTemporaryQueues
argument_list|()
operator|.
name|length
argument_list|)
expr_stmt|;
block|}
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
name|testDynamicSenderLifetimeBoundToLinkTopic
parameter_list|()
throws|throws
name|Exception
block|{
name|doTestDynamicSenderLifetimeBoundToLinkQueue
argument_list|(
literal|true
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
name|testDynamicSenderLifetimeBoundToLinkQueue
parameter_list|()
throws|throws
name|Exception
block|{
name|doTestDynamicSenderLifetimeBoundToLinkQueue
argument_list|(
literal|false
argument_list|)
expr_stmt|;
block|}
specifier|protected
name|void
name|doTestDynamicSenderLifetimeBoundToLinkQueue
parameter_list|(
name|boolean
name|topic
parameter_list|)
throws|throws
name|Exception
block|{
name|Target
name|target
init|=
name|createDynamicTarget
argument_list|(
name|topic
argument_list|)
decl_stmt|;
specifier|final
name|BrokerViewMBean
name|brokerView
init|=
name|getProxyToBroker
argument_list|()
decl_stmt|;
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
name|target
argument_list|)
decl_stmt|;
name|assertNotNull
argument_list|(
name|sender
argument_list|)
expr_stmt|;
if|if
condition|(
name|topic
condition|)
block|{
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|brokerView
operator|.
name|getTemporaryTopics
argument_list|()
operator|.
name|length
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|brokerView
operator|.
name|getTemporaryQueues
argument_list|()
operator|.
name|length
argument_list|)
expr_stmt|;
block|}
name|sender
operator|.
name|close
argument_list|()
expr_stmt|;
if|if
condition|(
name|topic
condition|)
block|{
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|brokerView
operator|.
name|getTemporaryTopics
argument_list|()
operator|.
name|length
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|brokerView
operator|.
name|getTemporaryQueues
argument_list|()
operator|.
name|length
argument_list|)
expr_stmt|;
block|}
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
name|testCreateDynamicReceiverToTopic
parameter_list|()
throws|throws
name|Exception
block|{
name|doTestCreateDynamicSender
argument_list|(
literal|true
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
name|testCreateDynamicReceiverToQueue
parameter_list|()
throws|throws
name|Exception
block|{
name|doTestCreateDynamicSender
argument_list|(
literal|false
argument_list|)
expr_stmt|;
block|}
specifier|protected
name|void
name|doTestCreateDynamicReceiver
parameter_list|(
name|boolean
name|topic
parameter_list|)
throws|throws
name|Exception
block|{
name|Source
name|source
init|=
name|createDynamicSource
argument_list|(
name|topic
argument_list|)
decl_stmt|;
specifier|final
name|BrokerViewMBean
name|brokerView
init|=
name|getProxyToBroker
argument_list|()
decl_stmt|;
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
name|AmqpReceiver
name|receiver
init|=
name|session
operator|.
name|createReceiver
argument_list|(
name|source
argument_list|)
decl_stmt|;
name|assertNotNull
argument_list|(
name|receiver
argument_list|)
expr_stmt|;
if|if
condition|(
name|topic
condition|)
block|{
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|brokerView
operator|.
name|getTemporaryTopics
argument_list|()
operator|.
name|length
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|brokerView
operator|.
name|getTemporaryQueues
argument_list|()
operator|.
name|length
argument_list|)
expr_stmt|;
block|}
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
name|testDynamicReceiverLifetimeBoundToLinkTopic
parameter_list|()
throws|throws
name|Exception
block|{
name|doTestDynamicReceiverLifetimeBoundToLinkQueue
argument_list|(
literal|true
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
name|testDynamicReceiverLifetimeBoundToLinkQueue
parameter_list|()
throws|throws
name|Exception
block|{
name|doTestDynamicReceiverLifetimeBoundToLinkQueue
argument_list|(
literal|false
argument_list|)
expr_stmt|;
block|}
specifier|protected
name|void
name|doTestDynamicReceiverLifetimeBoundToLinkQueue
parameter_list|(
name|boolean
name|topic
parameter_list|)
throws|throws
name|Exception
block|{
name|Source
name|source
init|=
name|createDynamicSource
argument_list|(
name|topic
argument_list|)
decl_stmt|;
specifier|final
name|BrokerViewMBean
name|brokerView
init|=
name|getProxyToBroker
argument_list|()
decl_stmt|;
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
name|AmqpReceiver
name|receiver
init|=
name|session
operator|.
name|createReceiver
argument_list|(
name|source
argument_list|)
decl_stmt|;
name|assertNotNull
argument_list|(
name|receiver
argument_list|)
expr_stmt|;
if|if
condition|(
name|topic
condition|)
block|{
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|brokerView
operator|.
name|getTemporaryTopics
argument_list|()
operator|.
name|length
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|brokerView
operator|.
name|getTemporaryQueues
argument_list|()
operator|.
name|length
argument_list|)
expr_stmt|;
block|}
name|receiver
operator|.
name|close
argument_list|()
expr_stmt|;
if|if
condition|(
name|topic
condition|)
block|{
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|brokerView
operator|.
name|getTemporaryTopics
argument_list|()
operator|.
name|length
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|brokerView
operator|.
name|getTemporaryQueues
argument_list|()
operator|.
name|length
argument_list|)
expr_stmt|;
block|}
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
name|TestCreateDynamicQueueSenderAndPublish
parameter_list|()
throws|throws
name|Exception
block|{
name|doTestCreateDynamicSenderAndPublish
argument_list|(
literal|false
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
name|TestCreateDynamicTopicSenderAndPublish
parameter_list|()
throws|throws
name|Exception
block|{
name|doTestCreateDynamicSenderAndPublish
argument_list|(
literal|true
argument_list|)
expr_stmt|;
block|}
specifier|protected
name|void
name|doTestCreateDynamicSenderAndPublish
parameter_list|(
name|boolean
name|topic
parameter_list|)
throws|throws
name|Exception
block|{
name|Target
name|target
init|=
name|createDynamicTarget
argument_list|(
name|topic
argument_list|)
decl_stmt|;
specifier|final
name|BrokerViewMBean
name|brokerView
init|=
name|getProxyToBroker
argument_list|()
decl_stmt|;
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
name|target
argument_list|)
decl_stmt|;
name|assertNotNull
argument_list|(
name|sender
argument_list|)
expr_stmt|;
if|if
condition|(
name|topic
condition|)
block|{
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|brokerView
operator|.
name|getTemporaryTopics
argument_list|()
operator|.
name|length
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|brokerView
operator|.
name|getTemporaryQueues
argument_list|()
operator|.
name|length
argument_list|)
expr_stmt|;
block|}
comment|// Get the new address
name|String
name|address
init|=
name|sender
operator|.
name|getSender
argument_list|()
operator|.
name|getRemoteTarget
argument_list|()
operator|.
name|getAddress
argument_list|()
decl_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"New dynamic sender address -> {}"
argument_list|,
name|address
argument_list|)
expr_stmt|;
comment|// Create a message and send to a receive that is listening on the newly
comment|// created dynamic link address.
name|AmqpMessage
name|message
init|=
operator|new
name|AmqpMessage
argument_list|()
decl_stmt|;
name|message
operator|.
name|setMessageId
argument_list|(
literal|"msg-1"
argument_list|)
expr_stmt|;
name|message
operator|.
name|setText
argument_list|(
literal|"Test-Message"
argument_list|)
expr_stmt|;
name|AmqpReceiver
name|receiver
init|=
name|session
operator|.
name|createReceiver
argument_list|(
name|address
argument_list|)
decl_stmt|;
name|receiver
operator|.
name|flow
argument_list|(
literal|1
argument_list|)
expr_stmt|;
name|sender
operator|.
name|send
argument_list|(
name|message
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
literal|"Should have read a message"
argument_list|,
name|received
argument_list|)
expr_stmt|;
name|received
operator|.
name|accept
argument_list|()
expr_stmt|;
name|receiver
operator|.
name|close
argument_list|()
expr_stmt|;
name|sender
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
annotation|@
name|Test
argument_list|(
name|timeout
operator|=
literal|60000
argument_list|)
specifier|public
name|void
name|testCreateDynamicReceiverToTopicAndSend
parameter_list|()
throws|throws
name|Exception
block|{
name|doTestCreateDynamicSender
argument_list|(
literal|true
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
name|testCreateDynamicReceiverToQueueAndSend
parameter_list|()
throws|throws
name|Exception
block|{
name|doTestCreateDynamicSender
argument_list|(
literal|false
argument_list|)
expr_stmt|;
block|}
specifier|protected
name|void
name|doTestCreateDynamicReceiverAndSend
parameter_list|(
name|boolean
name|topic
parameter_list|)
throws|throws
name|Exception
block|{
name|Source
name|source
init|=
name|createDynamicSource
argument_list|(
name|topic
argument_list|)
decl_stmt|;
specifier|final
name|BrokerViewMBean
name|brokerView
init|=
name|getProxyToBroker
argument_list|()
decl_stmt|;
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
name|AmqpReceiver
name|receiver
init|=
name|session
operator|.
name|createReceiver
argument_list|(
name|source
argument_list|)
decl_stmt|;
name|assertNotNull
argument_list|(
name|receiver
argument_list|)
expr_stmt|;
if|if
condition|(
name|topic
condition|)
block|{
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|brokerView
operator|.
name|getTemporaryTopics
argument_list|()
operator|.
name|length
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|brokerView
operator|.
name|getTemporaryQueues
argument_list|()
operator|.
name|length
argument_list|)
expr_stmt|;
block|}
comment|// Get the new address
name|String
name|address
init|=
name|receiver
operator|.
name|getReceiver
argument_list|()
operator|.
name|getRemoteSource
argument_list|()
operator|.
name|getAddress
argument_list|()
decl_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"New dynamic receiver address -> {}"
argument_list|,
name|address
argument_list|)
expr_stmt|;
comment|// Create a message and send to a receive that is listening on the newly
comment|// created dynamic link address.
name|AmqpMessage
name|message
init|=
operator|new
name|AmqpMessage
argument_list|()
decl_stmt|;
name|message
operator|.
name|setMessageId
argument_list|(
literal|"msg-1"
argument_list|)
expr_stmt|;
name|message
operator|.
name|setText
argument_list|(
literal|"Test-Message"
argument_list|)
expr_stmt|;
name|AmqpSender
name|sender
init|=
name|session
operator|.
name|createSender
argument_list|(
name|address
argument_list|)
decl_stmt|;
name|sender
operator|.
name|send
argument_list|(
name|message
argument_list|)
expr_stmt|;
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
literal|"Should have read a message"
argument_list|,
name|received
argument_list|)
expr_stmt|;
name|received
operator|.
name|accept
argument_list|()
expr_stmt|;
name|sender
operator|.
name|close
argument_list|()
expr_stmt|;
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
specifier|protected
name|Source
name|createDynamicSource
parameter_list|(
name|boolean
name|topic
parameter_list|)
block|{
name|Source
name|source
init|=
operator|new
name|Source
argument_list|()
decl_stmt|;
name|source
operator|.
name|setDynamic
argument_list|(
literal|true
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
comment|// Set the dynamic node lifetime-policy
name|Map
argument_list|<
name|Symbol
argument_list|,
name|Object
argument_list|>
name|dynamicNodeProperties
init|=
operator|new
name|HashMap
argument_list|<
name|Symbol
argument_list|,
name|Object
argument_list|>
argument_list|()
decl_stmt|;
name|dynamicNodeProperties
operator|.
name|put
argument_list|(
name|LIFETIME_POLICY
argument_list|,
name|DeleteOnClose
operator|.
name|getInstance
argument_list|()
argument_list|)
expr_stmt|;
name|source
operator|.
name|setDynamicNodeProperties
argument_list|(
name|dynamicNodeProperties
argument_list|)
expr_stmt|;
comment|// Set the capability to indicate the node type being created
if|if
condition|(
operator|!
name|topic
condition|)
block|{
name|source
operator|.
name|setCapabilities
argument_list|(
name|TEMP_QUEUE_CAPABILITY
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|source
operator|.
name|setCapabilities
argument_list|(
name|TEMP_TOPIC_CAPABILITY
argument_list|)
expr_stmt|;
block|}
return|return
name|source
return|;
block|}
specifier|protected
name|Target
name|createDynamicTarget
parameter_list|(
name|boolean
name|topic
parameter_list|)
block|{
name|Target
name|target
init|=
operator|new
name|Target
argument_list|()
decl_stmt|;
name|target
operator|.
name|setDynamic
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|target
operator|.
name|setDurable
argument_list|(
name|TerminusDurability
operator|.
name|NONE
argument_list|)
expr_stmt|;
name|target
operator|.
name|setExpiryPolicy
argument_list|(
name|TerminusExpiryPolicy
operator|.
name|LINK_DETACH
argument_list|)
expr_stmt|;
comment|// Set the dynamic node lifetime-policy
name|Map
argument_list|<
name|Symbol
argument_list|,
name|Object
argument_list|>
name|dynamicNodeProperties
init|=
operator|new
name|HashMap
argument_list|<
name|Symbol
argument_list|,
name|Object
argument_list|>
argument_list|()
decl_stmt|;
name|dynamicNodeProperties
operator|.
name|put
argument_list|(
name|LIFETIME_POLICY
argument_list|,
name|DeleteOnClose
operator|.
name|getInstance
argument_list|()
argument_list|)
expr_stmt|;
name|target
operator|.
name|setDynamicNodeProperties
argument_list|(
name|dynamicNodeProperties
argument_list|)
expr_stmt|;
comment|// Set the capability to indicate the node type being created
if|if
condition|(
operator|!
name|topic
condition|)
block|{
name|target
operator|.
name|setCapabilities
argument_list|(
name|TEMP_QUEUE_CAPABILITY
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|target
operator|.
name|setCapabilities
argument_list|(
name|TEMP_TOPIC_CAPABILITY
argument_list|)
expr_stmt|;
block|}
return|return
name|target
return|;
block|}
block|}
end_class

end_unit

