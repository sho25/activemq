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
name|apache
operator|.
name|commons
operator|.
name|logging
operator|.
name|Log
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|commons
operator|.
name|logging
operator|.
name|LogFactory
import|;
end_import

begin_class
specifier|public
class|class
name|JmsRollbackRedeliveryTest
extends|extends
name|AutoFailTestSupport
block|{
specifier|protected
specifier|static
specifier|final
name|Log
name|LOG
init|=
name|LogFactory
operator|.
name|getLog
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
specifier|public
name|void
name|setUp
parameter_list|()
throws|throws
name|Exception
block|{
name|setAutoFail
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|super
operator|.
name|setUp
argument_list|()
expr_stmt|;
name|BrokerService
name|broker
init|=
operator|new
name|BrokerService
argument_list|()
decl_stmt|;
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
block|}
specifier|public
name|void
name|testRedelivery
parameter_list|()
throws|throws
name|Exception
block|{
name|doTestRedelivery
argument_list|(
literal|"vm://localhost"
argument_list|,
literal|false
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|testRedeliveryWithInterleavedProducer
parameter_list|()
throws|throws
name|Exception
block|{
name|doTestRedelivery
argument_list|(
literal|"vm://localhost"
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
name|session
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
block|}
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
literal|"vm://localhost"
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
literal|"vm://localhost"
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
specifier|public
name|void
name|testRedeliveryOnSessionCloseWithNoRollback
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
literal|"vm://localhost"
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
block|}
name|session
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
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

