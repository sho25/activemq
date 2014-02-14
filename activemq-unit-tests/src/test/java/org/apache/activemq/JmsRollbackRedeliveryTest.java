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
package|;
end_package

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
name|ConcurrentHashMap
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
name|atomic
operator|.
name|AtomicInteger
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
name|ConnectionFactory
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
name|MessageConsumer
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
name|broker
operator|.
name|BrokerService
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

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|Rule
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|rules
operator|.
name|TestName
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

begin_import
import|import static
name|org
operator|.
name|junit
operator|.
name|Assert
operator|.
name|*
import|;
end_import

begin_class
specifier|public
class|class
name|JmsRollbackRedeliveryTest
block|{
annotation|@
name|Rule
specifier|public
name|TestName
name|testName
init|=
operator|new
name|TestName
argument_list|()
decl_stmt|;
specifier|protected
specifier|static
specifier|final
name|Logger
name|LOG
init|=
name|LoggerFactory
operator|.
name|getLogger
argument_list|(
name|JmsRollbackRedeliveryTest
operator|.
name|class
argument_list|)
decl_stmt|;
specifier|final
name|int
name|nbMessages
init|=
literal|10
decl_stmt|;
specifier|final
name|String
name|destinationName
init|=
literal|"Destination"
decl_stmt|;
specifier|final
name|String
name|brokerUrl
init|=
literal|"vm://localhost?create=false"
decl_stmt|;
name|boolean
name|consumerClose
init|=
literal|true
decl_stmt|;
name|boolean
name|rollback
init|=
literal|true
decl_stmt|;
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
name|LOG
operator|.
name|debug
argument_list|(
literal|"Starting "
operator|+
name|testName
operator|.
name|getMethodName
argument_list|()
argument_list|)
expr_stmt|;
name|broker
operator|=
operator|new
name|BrokerService
argument_list|()
expr_stmt|;
name|broker
operator|.
name|setPersistent
argument_list|(
literal|false
argument_list|)
expr_stmt|;
name|broker
operator|.
name|setUseJmx
argument_list|(
literal|false
argument_list|)
expr_stmt|;
name|broker
operator|.
name|start
argument_list|()
expr_stmt|;
name|broker
operator|.
name|waitUntilStarted
argument_list|()
expr_stmt|;
block|}
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
name|broker
operator|!=
literal|null
condition|)
block|{
name|broker
operator|.
name|stop
argument_list|()
expr_stmt|;
name|broker
operator|.
name|waitUntilStopped
argument_list|()
expr_stmt|;
block|}
name|LOG
operator|.
name|debug
argument_list|(
literal|"Finishing "
operator|+
name|testName
operator|.
name|getMethodName
argument_list|()
argument_list|)
expr_stmt|;
name|Thread
operator|.
name|sleep
argument_list|(
literal|100
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testRedelivery
parameter_list|()
throws|throws
name|Exception
block|{
name|doTestRedelivery
argument_list|(
name|brokerUrl
argument_list|,
literal|false
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testRedeliveryWithInterleavedProducer
parameter_list|()
throws|throws
name|Exception
block|{
name|doTestRedelivery
argument_list|(
name|brokerUrl
argument_list|,
literal|true
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testRedeliveryWithPrefetch0
parameter_list|()
throws|throws
name|Exception
block|{
name|doTestRedelivery
argument_list|(
name|brokerUrl
operator|+
literal|"?jms.prefetchPolicy.queuePrefetch=0"
argument_list|,
literal|true
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testRedeliveryWithPrefetch1
parameter_list|()
throws|throws
name|Exception
block|{
name|doTestRedelivery
argument_list|(
name|brokerUrl
operator|+
literal|"?jms.prefetchPolicy.queuePrefetch=1"
argument_list|,
literal|true
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|doTestRedelivery
parameter_list|(
name|String
name|brokerUrl
parameter_list|,
name|boolean
name|interleaveProducer
parameter_list|)
throws|throws
name|Exception
block|{
name|LOG
operator|.
name|debug
argument_list|(
literal|"entering doTestRedelivery interleaveProducer is "
operator|+
name|interleaveProducer
argument_list|)
expr_stmt|;
name|ConnectionFactory
name|connectionFactory
init|=
operator|new
name|ActiveMQConnectionFactory
argument_list|(
name|brokerUrl
argument_list|)
decl_stmt|;
name|Connection
name|connection
init|=
name|connectionFactory
operator|.
name|createConnection
argument_list|()
decl_stmt|;
name|connection
operator|.
name|start
argument_list|()
expr_stmt|;
if|if
condition|(
name|interleaveProducer
condition|)
block|{
name|populateDestinationWithInterleavedProducer
argument_list|(
name|nbMessages
argument_list|,
name|destinationName
argument_list|,
name|connection
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|populateDestination
argument_list|(
name|nbMessages
argument_list|,
name|destinationName
argument_list|,
name|connection
argument_list|)
expr_stmt|;
block|}
comment|// Consume messages and rollback transactions
block|{
name|AtomicInteger
name|received
init|=
operator|new
name|AtomicInteger
argument_list|()
decl_stmt|;
name|Map
argument_list|<
name|String
argument_list|,
name|Boolean
argument_list|>
name|rolledback
init|=
operator|new
name|ConcurrentHashMap
argument_list|<
name|String
argument_list|,
name|Boolean
argument_list|>
argument_list|()
decl_stmt|;
while|while
condition|(
name|received
operator|.
name|get
argument_list|()
operator|<
name|nbMessages
condition|)
block|{
name|Session
name|session
init|=
name|connection
operator|.
name|createSession
argument_list|(
literal|true
argument_list|,
name|Session
operator|.
name|AUTO_ACKNOWLEDGE
argument_list|)
decl_stmt|;
name|Destination
name|destination
init|=
name|session
operator|.
name|createQueue
argument_list|(
name|destinationName
argument_list|)
decl_stmt|;
name|MessageConsumer
name|consumer
init|=
name|session
operator|.
name|createConsumer
argument_list|(
name|destination
argument_list|)
decl_stmt|;
name|TextMessage
name|msg
init|=
operator|(
name|TextMessage
operator|)
name|consumer
operator|.
name|receive
argument_list|(
literal|6000000
argument_list|)
decl_stmt|;
if|if
condition|(
name|msg
operator|!=
literal|null
condition|)
block|{
if|if
condition|(
name|msg
operator|!=
literal|null
operator|&&
name|rolledback
operator|.
name|put
argument_list|(
name|msg
operator|.
name|getText
argument_list|()
argument_list|,
name|Boolean
operator|.
name|TRUE
argument_list|)
operator|!=
literal|null
condition|)
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"Received message "
operator|+
name|msg
operator|.
name|getText
argument_list|()
operator|+
literal|" ("
operator|+
name|received
operator|.
name|getAndIncrement
argument_list|()
operator|+
literal|")"
operator|+
name|msg
operator|.
name|getJMSMessageID
argument_list|()
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|msg
operator|.
name|getJMSRedelivered
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|2
argument_list|,
name|msg
operator|.
name|getLongProperty
argument_list|(
literal|"JMSXDeliveryCount"
argument_list|)
argument_list|)
expr_stmt|;
name|session
operator|.
name|commit
argument_list|()
expr_stmt|;
block|}
else|else
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"Rollback message "
operator|+
name|msg
operator|.
name|getText
argument_list|()
operator|+
literal|" id: "
operator|+
name|msg
operator|.
name|getJMSMessageID
argument_list|()
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
literal|"should not have redelivery flag set, id: "
operator|+
name|msg
operator|.
name|getJMSMessageID
argument_list|()
argument_list|,
name|msg
operator|.
name|getJMSRedelivered
argument_list|()
argument_list|)
expr_stmt|;
name|session
operator|.
name|rollback
argument_list|()
expr_stmt|;
block|}
block|}
name|consumer
operator|.
name|close
argument_list|()
expr_stmt|;
name|session
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
block|}
annotation|@
name|Test
specifier|public
name|void
name|testRedeliveryOnSingleConsumer
parameter_list|()
throws|throws
name|Exception
block|{
name|ConnectionFactory
name|connectionFactory
init|=
operator|new
name|ActiveMQConnectionFactory
argument_list|(
name|brokerUrl
argument_list|)
decl_stmt|;
name|Connection
name|connection
init|=
name|connectionFactory
operator|.
name|createConnection
argument_list|()
decl_stmt|;
name|connection
operator|.
name|start
argument_list|()
expr_stmt|;
name|populateDestinationWithInterleavedProducer
argument_list|(
name|nbMessages
argument_list|,
name|destinationName
argument_list|,
name|connection
argument_list|)
expr_stmt|;
comment|// Consume messages and rollback transactions
block|{
name|AtomicInteger
name|received
init|=
operator|new
name|AtomicInteger
argument_list|()
decl_stmt|;
name|Map
argument_list|<
name|String
argument_list|,
name|Boolean
argument_list|>
name|rolledback
init|=
operator|new
name|ConcurrentHashMap
argument_list|<
name|String
argument_list|,
name|Boolean
argument_list|>
argument_list|()
decl_stmt|;
name|Session
name|session
init|=
name|connection
operator|.
name|createSession
argument_list|(
literal|true
argument_list|,
name|Session
operator|.
name|AUTO_ACKNOWLEDGE
argument_list|)
decl_stmt|;
name|Destination
name|destination
init|=
name|session
operator|.
name|createQueue
argument_list|(
name|destinationName
argument_list|)
decl_stmt|;
name|MessageConsumer
name|consumer
init|=
name|session
operator|.
name|createConsumer
argument_list|(
name|destination
argument_list|)
decl_stmt|;
while|while
condition|(
name|received
operator|.
name|get
argument_list|()
operator|<
name|nbMessages
condition|)
block|{
name|TextMessage
name|msg
init|=
operator|(
name|TextMessage
operator|)
name|consumer
operator|.
name|receive
argument_list|(
literal|6000000
argument_list|)
decl_stmt|;
if|if
condition|(
name|msg
operator|!=
literal|null
condition|)
block|{
if|if
condition|(
name|msg
operator|!=
literal|null
operator|&&
name|rolledback
operator|.
name|put
argument_list|(
name|msg
operator|.
name|getText
argument_list|()
argument_list|,
name|Boolean
operator|.
name|TRUE
argument_list|)
operator|!=
literal|null
condition|)
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"Received message "
operator|+
name|msg
operator|.
name|getText
argument_list|()
operator|+
literal|" ("
operator|+
name|received
operator|.
name|getAndIncrement
argument_list|()
operator|+
literal|")"
operator|+
name|msg
operator|.
name|getJMSMessageID
argument_list|()
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|msg
operator|.
name|getJMSRedelivered
argument_list|()
argument_list|)
expr_stmt|;
name|session
operator|.
name|commit
argument_list|()
expr_stmt|;
block|}
else|else
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"Rollback message "
operator|+
name|msg
operator|.
name|getText
argument_list|()
operator|+
literal|" id: "
operator|+
name|msg
operator|.
name|getJMSMessageID
argument_list|()
argument_list|)
expr_stmt|;
name|session
operator|.
name|rollback
argument_list|()
expr_stmt|;
block|}
block|}
block|}
name|consumer
operator|.
name|close
argument_list|()
expr_stmt|;
name|session
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
annotation|@
name|Test
specifier|public
name|void
name|testRedeliveryOnSingleSession
parameter_list|()
throws|throws
name|Exception
block|{
name|ConnectionFactory
name|connectionFactory
init|=
operator|new
name|ActiveMQConnectionFactory
argument_list|(
name|brokerUrl
argument_list|)
decl_stmt|;
name|Connection
name|connection
init|=
name|connectionFactory
operator|.
name|createConnection
argument_list|()
decl_stmt|;
name|connection
operator|.
name|start
argument_list|()
expr_stmt|;
name|populateDestination
argument_list|(
name|nbMessages
argument_list|,
name|destinationName
argument_list|,
name|connection
argument_list|)
expr_stmt|;
comment|// Consume messages and rollback transactions
block|{
name|AtomicInteger
name|received
init|=
operator|new
name|AtomicInteger
argument_list|()
decl_stmt|;
name|Map
argument_list|<
name|String
argument_list|,
name|Boolean
argument_list|>
name|rolledback
init|=
operator|new
name|ConcurrentHashMap
argument_list|<
name|String
argument_list|,
name|Boolean
argument_list|>
argument_list|()
decl_stmt|;
name|Session
name|session
init|=
name|connection
operator|.
name|createSession
argument_list|(
literal|true
argument_list|,
name|Session
operator|.
name|AUTO_ACKNOWLEDGE
argument_list|)
decl_stmt|;
name|Destination
name|destination
init|=
name|session
operator|.
name|createQueue
argument_list|(
name|destinationName
argument_list|)
decl_stmt|;
while|while
condition|(
name|received
operator|.
name|get
argument_list|()
operator|<
name|nbMessages
condition|)
block|{
name|MessageConsumer
name|consumer
init|=
name|session
operator|.
name|createConsumer
argument_list|(
name|destination
argument_list|)
decl_stmt|;
name|TextMessage
name|msg
init|=
operator|(
name|TextMessage
operator|)
name|consumer
operator|.
name|receive
argument_list|(
literal|6000000
argument_list|)
decl_stmt|;
if|if
condition|(
name|msg
operator|!=
literal|null
condition|)
block|{
if|if
condition|(
name|msg
operator|!=
literal|null
operator|&&
name|rolledback
operator|.
name|put
argument_list|(
name|msg
operator|.
name|getText
argument_list|()
argument_list|,
name|Boolean
operator|.
name|TRUE
argument_list|)
operator|!=
literal|null
condition|)
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"Received message "
operator|+
name|msg
operator|.
name|getText
argument_list|()
operator|+
literal|" ("
operator|+
name|received
operator|.
name|getAndIncrement
argument_list|()
operator|+
literal|")"
operator|+
name|msg
operator|.
name|getJMSMessageID
argument_list|()
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|msg
operator|.
name|getJMSRedelivered
argument_list|()
argument_list|)
expr_stmt|;
name|session
operator|.
name|commit
argument_list|()
expr_stmt|;
block|}
else|else
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"Rollback message "
operator|+
name|msg
operator|.
name|getText
argument_list|()
operator|+
literal|" id: "
operator|+
name|msg
operator|.
name|getJMSMessageID
argument_list|()
argument_list|)
expr_stmt|;
name|session
operator|.
name|rollback
argument_list|()
expr_stmt|;
block|}
block|}
name|consumer
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
name|session
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
comment|// AMQ-1593
annotation|@
name|Test
specifier|public
name|void
name|testValidateRedeliveryCountOnRollback
parameter_list|()
throws|throws
name|Exception
block|{
specifier|final
name|int
name|numMessages
init|=
literal|1
decl_stmt|;
name|ConnectionFactory
name|connectionFactory
init|=
operator|new
name|ActiveMQConnectionFactory
argument_list|(
name|brokerUrl
argument_list|)
decl_stmt|;
name|Connection
name|connection
init|=
name|connectionFactory
operator|.
name|createConnection
argument_list|()
decl_stmt|;
name|connection
operator|.
name|start
argument_list|()
expr_stmt|;
name|populateDestination
argument_list|(
name|numMessages
argument_list|,
name|destinationName
argument_list|,
name|connection
argument_list|)
expr_stmt|;
block|{
name|AtomicInteger
name|received
init|=
operator|new
name|AtomicInteger
argument_list|()
decl_stmt|;
specifier|final
name|int
name|maxRetries
init|=
operator|new
name|RedeliveryPolicy
argument_list|()
operator|.
name|getMaximumRedeliveries
argument_list|()
decl_stmt|;
while|while
condition|(
name|received
operator|.
name|get
argument_list|()
operator|<
name|maxRetries
condition|)
block|{
name|Session
name|session
init|=
name|connection
operator|.
name|createSession
argument_list|(
literal|true
argument_list|,
name|Session
operator|.
name|SESSION_TRANSACTED
argument_list|)
decl_stmt|;
name|Destination
name|destination
init|=
name|session
operator|.
name|createQueue
argument_list|(
name|destinationName
argument_list|)
decl_stmt|;
name|MessageConsumer
name|consumer
init|=
name|session
operator|.
name|createConsumer
argument_list|(
name|destination
argument_list|)
decl_stmt|;
name|TextMessage
name|msg
init|=
operator|(
name|TextMessage
operator|)
name|consumer
operator|.
name|receive
argument_list|(
literal|1000
argument_list|)
decl_stmt|;
if|if
condition|(
name|msg
operator|!=
literal|null
condition|)
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"Received message "
operator|+
name|msg
operator|.
name|getText
argument_list|()
operator|+
literal|" ("
operator|+
name|received
operator|.
name|getAndIncrement
argument_list|()
operator|+
literal|")"
operator|+
name|msg
operator|.
name|getJMSMessageID
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"redelivery property matches deliveries"
argument_list|,
name|received
operator|.
name|get
argument_list|()
argument_list|,
name|msg
operator|.
name|getLongProperty
argument_list|(
literal|"JMSXDeliveryCount"
argument_list|)
argument_list|)
expr_stmt|;
name|session
operator|.
name|rollback
argument_list|()
expr_stmt|;
block|}
name|session
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
name|consumeMessage
argument_list|(
name|connection
argument_list|,
name|maxRetries
operator|+
literal|1
argument_list|)
expr_stmt|;
block|}
block|}
comment|// AMQ-1593
annotation|@
name|Test
specifier|public
name|void
name|testValidateRedeliveryCountOnRollbackWithPrefetch0
parameter_list|()
throws|throws
name|Exception
block|{
specifier|final
name|int
name|numMessages
init|=
literal|1
decl_stmt|;
name|ConnectionFactory
name|connectionFactory
init|=
operator|new
name|ActiveMQConnectionFactory
argument_list|(
name|brokerUrl
operator|+
literal|"?jms.prefetchPolicy.queuePrefetch=0"
argument_list|)
decl_stmt|;
name|Connection
name|connection
init|=
name|connectionFactory
operator|.
name|createConnection
argument_list|()
decl_stmt|;
name|connection
operator|.
name|start
argument_list|()
expr_stmt|;
name|populateDestination
argument_list|(
name|numMessages
argument_list|,
name|destinationName
argument_list|,
name|connection
argument_list|)
expr_stmt|;
block|{
name|AtomicInteger
name|received
init|=
operator|new
name|AtomicInteger
argument_list|()
decl_stmt|;
specifier|final
name|int
name|maxRetries
init|=
operator|new
name|RedeliveryPolicy
argument_list|()
operator|.
name|getMaximumRedeliveries
argument_list|()
decl_stmt|;
while|while
condition|(
name|received
operator|.
name|get
argument_list|()
operator|<
name|maxRetries
condition|)
block|{
name|Session
name|session
init|=
name|connection
operator|.
name|createSession
argument_list|(
literal|true
argument_list|,
name|Session
operator|.
name|SESSION_TRANSACTED
argument_list|)
decl_stmt|;
name|Destination
name|destination
init|=
name|session
operator|.
name|createQueue
argument_list|(
name|destinationName
argument_list|)
decl_stmt|;
name|MessageConsumer
name|consumer
init|=
name|session
operator|.
name|createConsumer
argument_list|(
name|destination
argument_list|)
decl_stmt|;
name|TextMessage
name|msg
init|=
operator|(
name|TextMessage
operator|)
name|consumer
operator|.
name|receive
argument_list|(
literal|1000
argument_list|)
decl_stmt|;
if|if
condition|(
name|msg
operator|!=
literal|null
condition|)
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"Received message "
operator|+
name|msg
operator|.
name|getText
argument_list|()
operator|+
literal|" ("
operator|+
name|received
operator|.
name|getAndIncrement
argument_list|()
operator|+
literal|")"
operator|+
name|msg
operator|.
name|getJMSMessageID
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"redelivery property matches deliveries"
argument_list|,
name|received
operator|.
name|get
argument_list|()
argument_list|,
name|msg
operator|.
name|getLongProperty
argument_list|(
literal|"JMSXDeliveryCount"
argument_list|)
argument_list|)
expr_stmt|;
name|session
operator|.
name|rollback
argument_list|()
expr_stmt|;
block|}
name|session
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
name|consumeMessage
argument_list|(
name|connection
argument_list|,
name|maxRetries
operator|+
literal|1
argument_list|)
expr_stmt|;
block|}
block|}
specifier|private
name|void
name|consumeMessage
parameter_list|(
name|Connection
name|connection
parameter_list|,
specifier|final
name|int
name|deliveryCount
parameter_list|)
throws|throws
name|JMSException
block|{
name|Session
name|session
init|=
name|connection
operator|.
name|createSession
argument_list|(
literal|true
argument_list|,
name|Session
operator|.
name|SESSION_TRANSACTED
argument_list|)
decl_stmt|;
name|Destination
name|destination
init|=
name|session
operator|.
name|createQueue
argument_list|(
name|destinationName
argument_list|)
decl_stmt|;
name|MessageConsumer
name|consumer
init|=
name|session
operator|.
name|createConsumer
argument_list|(
name|destination
argument_list|)
decl_stmt|;
name|TextMessage
name|msg
init|=
operator|(
name|TextMessage
operator|)
name|consumer
operator|.
name|receive
argument_list|(
literal|1000
argument_list|)
decl_stmt|;
name|assertNotNull
argument_list|(
name|msg
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"redelivery property matches deliveries"
argument_list|,
name|deliveryCount
argument_list|,
name|msg
operator|.
name|getLongProperty
argument_list|(
literal|"JMSXDeliveryCount"
argument_list|)
argument_list|)
expr_stmt|;
name|session
operator|.
name|commit
argument_list|()
expr_stmt|;
name|session
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testRedeliveryPropertyWithNoRollback
parameter_list|()
throws|throws
name|Exception
block|{
specifier|final
name|int
name|numMessages
init|=
literal|1
decl_stmt|;
name|ConnectionFactory
name|connectionFactory
init|=
operator|new
name|ActiveMQConnectionFactory
argument_list|(
name|brokerUrl
argument_list|)
decl_stmt|;
name|Connection
name|connection
init|=
name|connectionFactory
operator|.
name|createConnection
argument_list|()
decl_stmt|;
name|connection
operator|.
name|start
argument_list|()
expr_stmt|;
name|populateDestination
argument_list|(
name|numMessages
argument_list|,
name|destinationName
argument_list|,
name|connection
argument_list|)
expr_stmt|;
name|connection
operator|.
name|close
argument_list|()
expr_stmt|;
block|{
name|AtomicInteger
name|received
init|=
operator|new
name|AtomicInteger
argument_list|()
decl_stmt|;
specifier|final
name|int
name|maxRetries
init|=
operator|new
name|RedeliveryPolicy
argument_list|()
operator|.
name|getMaximumRedeliveries
argument_list|()
decl_stmt|;
while|while
condition|(
name|received
operator|.
name|get
argument_list|()
operator|<
name|maxRetries
condition|)
block|{
name|connection
operator|=
name|connectionFactory
operator|.
name|createConnection
argument_list|()
expr_stmt|;
name|connection
operator|.
name|start
argument_list|()
expr_stmt|;
name|Session
name|session
init|=
name|connection
operator|.
name|createSession
argument_list|(
literal|true
argument_list|,
name|Session
operator|.
name|SESSION_TRANSACTED
argument_list|)
decl_stmt|;
name|Destination
name|destination
init|=
name|session
operator|.
name|createQueue
argument_list|(
name|destinationName
argument_list|)
decl_stmt|;
name|MessageConsumer
name|consumer
init|=
name|session
operator|.
name|createConsumer
argument_list|(
name|destination
argument_list|)
decl_stmt|;
name|TextMessage
name|msg
init|=
operator|(
name|TextMessage
operator|)
name|consumer
operator|.
name|receive
argument_list|(
literal|2000
argument_list|)
decl_stmt|;
if|if
condition|(
name|msg
operator|!=
literal|null
condition|)
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"Received message "
operator|+
name|msg
operator|.
name|getText
argument_list|()
operator|+
literal|" ("
operator|+
name|received
operator|.
name|getAndIncrement
argument_list|()
operator|+
literal|")"
operator|+
name|msg
operator|.
name|getJMSMessageID
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"redelivery property matches deliveries"
argument_list|,
name|received
operator|.
name|get
argument_list|()
argument_list|,
name|msg
operator|.
name|getLongProperty
argument_list|(
literal|"JMSXDeliveryCount"
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|session
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
name|connection
operator|=
name|connectionFactory
operator|.
name|createConnection
argument_list|()
expr_stmt|;
name|connection
operator|.
name|start
argument_list|()
expr_stmt|;
name|consumeMessage
argument_list|(
name|connection
argument_list|,
name|maxRetries
operator|+
literal|1
argument_list|)
expr_stmt|;
block|}
block|}
specifier|private
name|void
name|populateDestination
parameter_list|(
specifier|final
name|int
name|nbMessages
parameter_list|,
specifier|final
name|String
name|destinationName
parameter_list|,
name|Connection
name|connection
parameter_list|)
throws|throws
name|JMSException
block|{
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
name|session
operator|.
name|createQueue
argument_list|(
name|destinationName
argument_list|)
decl_stmt|;
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
for|for
control|(
name|int
name|i
init|=
literal|1
init|;
name|i
operator|<=
name|nbMessages
condition|;
name|i
operator|++
control|)
block|{
name|producer
operator|.
name|send
argument_list|(
name|session
operator|.
name|createTextMessage
argument_list|(
literal|"<hello id='"
operator|+
name|i
operator|+
literal|"'/>"
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|producer
operator|.
name|close
argument_list|()
expr_stmt|;
name|session
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
specifier|private
name|void
name|populateDestinationWithInterleavedProducer
parameter_list|(
specifier|final
name|int
name|nbMessages
parameter_list|,
specifier|final
name|String
name|destinationName
parameter_list|,
name|Connection
name|connection
parameter_list|)
throws|throws
name|JMSException
block|{
name|Session
name|session1
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
name|destination1
init|=
name|session1
operator|.
name|createQueue
argument_list|(
name|destinationName
argument_list|)
decl_stmt|;
name|MessageProducer
name|producer1
init|=
name|session1
operator|.
name|createProducer
argument_list|(
name|destination1
argument_list|)
decl_stmt|;
name|Session
name|session2
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
name|destination2
init|=
name|session2
operator|.
name|createQueue
argument_list|(
name|destinationName
argument_list|)
decl_stmt|;
name|MessageProducer
name|producer2
init|=
name|session2
operator|.
name|createProducer
argument_list|(
name|destination2
argument_list|)
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|1
init|;
name|i
operator|<=
name|nbMessages
condition|;
name|i
operator|++
control|)
block|{
if|if
condition|(
name|i
operator|%
literal|2
operator|==
literal|0
condition|)
block|{
name|producer1
operator|.
name|send
argument_list|(
name|session1
operator|.
name|createTextMessage
argument_list|(
literal|"<hello id='"
operator|+
name|i
operator|+
literal|"'/>"
argument_list|)
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|producer2
operator|.
name|send
argument_list|(
name|session2
operator|.
name|createTextMessage
argument_list|(
literal|"<hello id='"
operator|+
name|i
operator|+
literal|"'/>"
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
name|producer1
operator|.
name|close
argument_list|()
expr_stmt|;
name|session1
operator|.
name|close
argument_list|()
expr_stmt|;
name|producer2
operator|.
name|close
argument_list|()
expr_stmt|;
name|session2
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
end_class

end_unit

