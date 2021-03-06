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
name|JMSException
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|jms
operator|.
name|Message
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
name|ActiveMQQueue
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

begin_class
specifier|public
class|class
name|ExclusiveConsumerTest
block|{
specifier|private
specifier|static
specifier|final
name|String
name|VM_BROKER_URL
init|=
literal|"vm://localhost"
decl_stmt|;
specifier|private
name|BrokerService
name|brokerService
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
name|brokerService
operator|=
operator|new
name|BrokerService
argument_list|()
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
name|setSchedulerSupport
argument_list|(
literal|false
argument_list|)
expr_stmt|;
name|brokerService
operator|.
name|setAdvisorySupport
argument_list|(
literal|false
argument_list|)
expr_stmt|;
name|brokerService
operator|.
name|start
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
name|brokerService
operator|!=
literal|null
condition|)
block|{
name|brokerService
operator|.
name|stop
argument_list|()
expr_stmt|;
name|brokerService
operator|=
literal|null
expr_stmt|;
block|}
block|}
specifier|private
name|Connection
name|createConnection
parameter_list|(
specifier|final
name|boolean
name|start
parameter_list|)
throws|throws
name|JMSException
block|{
name|ConnectionFactory
name|cf
init|=
operator|new
name|ActiveMQConnectionFactory
argument_list|(
name|VM_BROKER_URL
argument_list|)
decl_stmt|;
name|Connection
name|conn
init|=
name|cf
operator|.
name|createConnection
argument_list|()
decl_stmt|;
if|if
condition|(
name|start
condition|)
block|{
name|conn
operator|.
name|start
argument_list|()
expr_stmt|;
block|}
return|return
name|conn
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
name|testExclusiveConsumerSelectedCreatedFirst
parameter_list|()
throws|throws
name|JMSException
throws|,
name|InterruptedException
block|{
name|Connection
name|conn
init|=
name|createConnection
argument_list|(
literal|true
argument_list|)
decl_stmt|;
name|Session
name|exclusiveSession
init|=
literal|null
decl_stmt|;
name|Session
name|fallbackSession
init|=
literal|null
decl_stmt|;
name|Session
name|senderSession
init|=
literal|null
decl_stmt|;
try|try
block|{
name|exclusiveSession
operator|=
name|conn
operator|.
name|createSession
argument_list|(
literal|false
argument_list|,
name|Session
operator|.
name|AUTO_ACKNOWLEDGE
argument_list|)
expr_stmt|;
name|fallbackSession
operator|=
name|conn
operator|.
name|createSession
argument_list|(
literal|false
argument_list|,
name|Session
operator|.
name|AUTO_ACKNOWLEDGE
argument_list|)
expr_stmt|;
name|senderSession
operator|=
name|conn
operator|.
name|createSession
argument_list|(
literal|false
argument_list|,
name|Session
operator|.
name|AUTO_ACKNOWLEDGE
argument_list|)
expr_stmt|;
name|ActiveMQQueue
name|exclusiveQueue
init|=
operator|new
name|ActiveMQQueue
argument_list|(
literal|"TEST.QUEUE1?consumer.exclusive=true"
argument_list|)
decl_stmt|;
name|MessageConsumer
name|exclusiveConsumer
init|=
name|exclusiveSession
operator|.
name|createConsumer
argument_list|(
name|exclusiveQueue
argument_list|)
decl_stmt|;
name|ActiveMQQueue
name|fallbackQueue
init|=
operator|new
name|ActiveMQQueue
argument_list|(
literal|"TEST.QUEUE1"
argument_list|)
decl_stmt|;
name|MessageConsumer
name|fallbackConsumer
init|=
name|fallbackSession
operator|.
name|createConsumer
argument_list|(
name|fallbackQueue
argument_list|)
decl_stmt|;
name|ActiveMQQueue
name|senderQueue
init|=
operator|new
name|ActiveMQQueue
argument_list|(
literal|"TEST.QUEUE1"
argument_list|)
decl_stmt|;
name|MessageProducer
name|producer
init|=
name|senderSession
operator|.
name|createProducer
argument_list|(
name|senderQueue
argument_list|)
decl_stmt|;
name|Message
name|msg
init|=
name|senderSession
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
name|msg
argument_list|)
expr_stmt|;
comment|// TODO need two send a 2nd message - bug AMQ-1024
comment|// producer.send(msg);
name|Thread
operator|.
name|sleep
argument_list|(
literal|100
argument_list|)
expr_stmt|;
comment|// Verify exclusive consumer receives the message.
name|assertNotNull
argument_list|(
name|exclusiveConsumer
operator|.
name|receive
argument_list|(
literal|100
argument_list|)
argument_list|)
expr_stmt|;
name|assertNull
argument_list|(
name|fallbackConsumer
operator|.
name|receive
argument_list|(
literal|100
argument_list|)
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|fallbackSession
operator|.
name|close
argument_list|()
expr_stmt|;
name|senderSession
operator|.
name|close
argument_list|()
expr_stmt|;
name|conn
operator|.
name|close
argument_list|()
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
name|testExclusiveConsumerSelectedCreatedAfter
parameter_list|()
throws|throws
name|JMSException
throws|,
name|InterruptedException
block|{
name|Connection
name|conn
init|=
name|createConnection
argument_list|(
literal|true
argument_list|)
decl_stmt|;
name|Session
name|exclusiveSession
init|=
literal|null
decl_stmt|;
name|Session
name|fallbackSession
init|=
literal|null
decl_stmt|;
name|Session
name|senderSession
init|=
literal|null
decl_stmt|;
try|try
block|{
name|exclusiveSession
operator|=
name|conn
operator|.
name|createSession
argument_list|(
literal|false
argument_list|,
name|Session
operator|.
name|AUTO_ACKNOWLEDGE
argument_list|)
expr_stmt|;
name|fallbackSession
operator|=
name|conn
operator|.
name|createSession
argument_list|(
literal|false
argument_list|,
name|Session
operator|.
name|AUTO_ACKNOWLEDGE
argument_list|)
expr_stmt|;
name|senderSession
operator|=
name|conn
operator|.
name|createSession
argument_list|(
literal|false
argument_list|,
name|Session
operator|.
name|AUTO_ACKNOWLEDGE
argument_list|)
expr_stmt|;
name|ActiveMQQueue
name|fallbackQueue
init|=
operator|new
name|ActiveMQQueue
argument_list|(
literal|"TEST.QUEUE5"
argument_list|)
decl_stmt|;
name|MessageConsumer
name|fallbackConsumer
init|=
name|fallbackSession
operator|.
name|createConsumer
argument_list|(
name|fallbackQueue
argument_list|)
decl_stmt|;
name|ActiveMQQueue
name|exclusiveQueue
init|=
operator|new
name|ActiveMQQueue
argument_list|(
literal|"TEST.QUEUE5?consumer.exclusive=true"
argument_list|)
decl_stmt|;
name|MessageConsumer
name|exclusiveConsumer
init|=
name|exclusiveSession
operator|.
name|createConsumer
argument_list|(
name|exclusiveQueue
argument_list|)
decl_stmt|;
name|ActiveMQQueue
name|senderQueue
init|=
operator|new
name|ActiveMQQueue
argument_list|(
literal|"TEST.QUEUE5"
argument_list|)
decl_stmt|;
name|MessageProducer
name|producer
init|=
name|senderSession
operator|.
name|createProducer
argument_list|(
name|senderQueue
argument_list|)
decl_stmt|;
name|Message
name|msg
init|=
name|senderSession
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
name|msg
argument_list|)
expr_stmt|;
name|Thread
operator|.
name|sleep
argument_list|(
literal|100
argument_list|)
expr_stmt|;
comment|// Verify exclusive consumer receives the message.
name|assertNotNull
argument_list|(
name|exclusiveConsumer
operator|.
name|receive
argument_list|(
literal|100
argument_list|)
argument_list|)
expr_stmt|;
name|assertNull
argument_list|(
name|fallbackConsumer
operator|.
name|receive
argument_list|(
literal|100
argument_list|)
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|fallbackSession
operator|.
name|close
argument_list|()
expr_stmt|;
name|senderSession
operator|.
name|close
argument_list|()
expr_stmt|;
name|conn
operator|.
name|close
argument_list|()
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
name|testFailoverToAnotherExclusiveConsumerCreatedFirst
parameter_list|()
throws|throws
name|JMSException
throws|,
name|InterruptedException
block|{
name|Connection
name|conn
init|=
name|createConnection
argument_list|(
literal|true
argument_list|)
decl_stmt|;
name|Session
name|exclusiveSession1
init|=
literal|null
decl_stmt|;
name|Session
name|exclusiveSession2
init|=
literal|null
decl_stmt|;
name|Session
name|fallbackSession
init|=
literal|null
decl_stmt|;
name|Session
name|senderSession
init|=
literal|null
decl_stmt|;
try|try
block|{
name|exclusiveSession1
operator|=
name|conn
operator|.
name|createSession
argument_list|(
literal|false
argument_list|,
name|Session
operator|.
name|AUTO_ACKNOWLEDGE
argument_list|)
expr_stmt|;
name|exclusiveSession2
operator|=
name|conn
operator|.
name|createSession
argument_list|(
literal|false
argument_list|,
name|Session
operator|.
name|AUTO_ACKNOWLEDGE
argument_list|)
expr_stmt|;
name|fallbackSession
operator|=
name|conn
operator|.
name|createSession
argument_list|(
literal|false
argument_list|,
name|Session
operator|.
name|AUTO_ACKNOWLEDGE
argument_list|)
expr_stmt|;
name|senderSession
operator|=
name|conn
operator|.
name|createSession
argument_list|(
literal|false
argument_list|,
name|Session
operator|.
name|AUTO_ACKNOWLEDGE
argument_list|)
expr_stmt|;
comment|// This creates the exclusive consumer first which avoids AMQ-1024 bug.
name|ActiveMQQueue
name|exclusiveQueue
init|=
operator|new
name|ActiveMQQueue
argument_list|(
literal|"TEST.QUEUE2?consumer.exclusive=true"
argument_list|)
decl_stmt|;
name|MessageConsumer
name|exclusiveConsumer1
init|=
name|exclusiveSession1
operator|.
name|createConsumer
argument_list|(
name|exclusiveQueue
argument_list|)
decl_stmt|;
name|MessageConsumer
name|exclusiveConsumer2
init|=
name|exclusiveSession2
operator|.
name|createConsumer
argument_list|(
name|exclusiveQueue
argument_list|)
decl_stmt|;
name|ActiveMQQueue
name|fallbackQueue
init|=
operator|new
name|ActiveMQQueue
argument_list|(
literal|"TEST.QUEUE2"
argument_list|)
decl_stmt|;
name|MessageConsumer
name|fallbackConsumer
init|=
name|fallbackSession
operator|.
name|createConsumer
argument_list|(
name|fallbackQueue
argument_list|)
decl_stmt|;
name|ActiveMQQueue
name|senderQueue
init|=
operator|new
name|ActiveMQQueue
argument_list|(
literal|"TEST.QUEUE2"
argument_list|)
decl_stmt|;
name|MessageProducer
name|producer
init|=
name|senderSession
operator|.
name|createProducer
argument_list|(
name|senderQueue
argument_list|)
decl_stmt|;
name|Message
name|msg
init|=
name|senderSession
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
name|msg
argument_list|)
expr_stmt|;
name|Thread
operator|.
name|sleep
argument_list|(
literal|100
argument_list|)
expr_stmt|;
comment|// Verify exclusive consumer receives the message.
name|assertNotNull
argument_list|(
name|exclusiveConsumer1
operator|.
name|receive
argument_list|(
literal|100
argument_list|)
argument_list|)
expr_stmt|;
name|assertNull
argument_list|(
name|exclusiveConsumer2
operator|.
name|receive
argument_list|(
literal|100
argument_list|)
argument_list|)
expr_stmt|;
name|assertNull
argument_list|(
name|fallbackConsumer
operator|.
name|receive
argument_list|(
literal|100
argument_list|)
argument_list|)
expr_stmt|;
comment|// Close the exclusive consumer to verify the non-exclusive consumer takes over
name|exclusiveConsumer1
operator|.
name|close
argument_list|()
expr_stmt|;
name|producer
operator|.
name|send
argument_list|(
name|msg
argument_list|)
expr_stmt|;
name|producer
operator|.
name|send
argument_list|(
name|msg
argument_list|)
expr_stmt|;
name|assertNotNull
argument_list|(
name|exclusiveConsumer2
operator|.
name|receive
argument_list|(
literal|100
argument_list|)
argument_list|)
expr_stmt|;
name|assertNull
argument_list|(
name|fallbackConsumer
operator|.
name|receive
argument_list|(
literal|100
argument_list|)
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|fallbackSession
operator|.
name|close
argument_list|()
expr_stmt|;
name|senderSession
operator|.
name|close
argument_list|()
expr_stmt|;
name|conn
operator|.
name|close
argument_list|()
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
name|testFailoverToAnotherExclusiveConsumerCreatedAfter
parameter_list|()
throws|throws
name|JMSException
throws|,
name|InterruptedException
block|{
name|Connection
name|conn
init|=
name|createConnection
argument_list|(
literal|true
argument_list|)
decl_stmt|;
name|Session
name|exclusiveSession1
init|=
literal|null
decl_stmt|;
name|Session
name|exclusiveSession2
init|=
literal|null
decl_stmt|;
name|Session
name|fallbackSession
init|=
literal|null
decl_stmt|;
name|Session
name|senderSession
init|=
literal|null
decl_stmt|;
try|try
block|{
name|exclusiveSession1
operator|=
name|conn
operator|.
name|createSession
argument_list|(
literal|false
argument_list|,
name|Session
operator|.
name|AUTO_ACKNOWLEDGE
argument_list|)
expr_stmt|;
name|exclusiveSession2
operator|=
name|conn
operator|.
name|createSession
argument_list|(
literal|false
argument_list|,
name|Session
operator|.
name|AUTO_ACKNOWLEDGE
argument_list|)
expr_stmt|;
name|fallbackSession
operator|=
name|conn
operator|.
name|createSession
argument_list|(
literal|false
argument_list|,
name|Session
operator|.
name|AUTO_ACKNOWLEDGE
argument_list|)
expr_stmt|;
name|senderSession
operator|=
name|conn
operator|.
name|createSession
argument_list|(
literal|false
argument_list|,
name|Session
operator|.
name|AUTO_ACKNOWLEDGE
argument_list|)
expr_stmt|;
comment|// This creates the exclusive consumer first which avoids AMQ-1024 bug.
name|ActiveMQQueue
name|exclusiveQueue
init|=
operator|new
name|ActiveMQQueue
argument_list|(
literal|"TEST.QUEUE6?consumer.exclusive=true"
argument_list|)
decl_stmt|;
name|MessageConsumer
name|exclusiveConsumer1
init|=
name|exclusiveSession1
operator|.
name|createConsumer
argument_list|(
name|exclusiveQueue
argument_list|)
decl_stmt|;
name|ActiveMQQueue
name|fallbackQueue
init|=
operator|new
name|ActiveMQQueue
argument_list|(
literal|"TEST.QUEUE6"
argument_list|)
decl_stmt|;
name|MessageConsumer
name|fallbackConsumer
init|=
name|fallbackSession
operator|.
name|createConsumer
argument_list|(
name|fallbackQueue
argument_list|)
decl_stmt|;
name|MessageConsumer
name|exclusiveConsumer2
init|=
name|exclusiveSession2
operator|.
name|createConsumer
argument_list|(
name|exclusiveQueue
argument_list|)
decl_stmt|;
name|ActiveMQQueue
name|senderQueue
init|=
operator|new
name|ActiveMQQueue
argument_list|(
literal|"TEST.QUEUE6"
argument_list|)
decl_stmt|;
name|MessageProducer
name|producer
init|=
name|senderSession
operator|.
name|createProducer
argument_list|(
name|senderQueue
argument_list|)
decl_stmt|;
name|Message
name|msg
init|=
name|senderSession
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
name|msg
argument_list|)
expr_stmt|;
name|Thread
operator|.
name|sleep
argument_list|(
literal|100
argument_list|)
expr_stmt|;
comment|// Verify exclusive consumer receives the message.
name|assertNotNull
argument_list|(
name|exclusiveConsumer1
operator|.
name|receive
argument_list|(
literal|100
argument_list|)
argument_list|)
expr_stmt|;
name|assertNull
argument_list|(
name|exclusiveConsumer2
operator|.
name|receive
argument_list|(
literal|100
argument_list|)
argument_list|)
expr_stmt|;
name|assertNull
argument_list|(
name|fallbackConsumer
operator|.
name|receive
argument_list|(
literal|100
argument_list|)
argument_list|)
expr_stmt|;
comment|// Close the exclusive consumer to verify the non-exclusive consumer takes over
name|exclusiveConsumer1
operator|.
name|close
argument_list|()
expr_stmt|;
name|producer
operator|.
name|send
argument_list|(
name|msg
argument_list|)
expr_stmt|;
name|producer
operator|.
name|send
argument_list|(
name|msg
argument_list|)
expr_stmt|;
name|assertNotNull
argument_list|(
name|exclusiveConsumer2
operator|.
name|receive
argument_list|(
literal|1000
argument_list|)
argument_list|)
expr_stmt|;
name|assertNull
argument_list|(
name|fallbackConsumer
operator|.
name|receive
argument_list|(
literal|100
argument_list|)
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|fallbackSession
operator|.
name|close
argument_list|()
expr_stmt|;
name|senderSession
operator|.
name|close
argument_list|()
expr_stmt|;
name|conn
operator|.
name|close
argument_list|()
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
name|testFailoverToNonExclusiveConsumer
parameter_list|()
throws|throws
name|JMSException
throws|,
name|InterruptedException
block|{
name|Connection
name|conn
init|=
name|createConnection
argument_list|(
literal|true
argument_list|)
decl_stmt|;
name|Session
name|exclusiveSession
init|=
literal|null
decl_stmt|;
name|Session
name|fallbackSession
init|=
literal|null
decl_stmt|;
name|Session
name|senderSession
init|=
literal|null
decl_stmt|;
try|try
block|{
name|exclusiveSession
operator|=
name|conn
operator|.
name|createSession
argument_list|(
literal|false
argument_list|,
name|Session
operator|.
name|AUTO_ACKNOWLEDGE
argument_list|)
expr_stmt|;
name|fallbackSession
operator|=
name|conn
operator|.
name|createSession
argument_list|(
literal|false
argument_list|,
name|Session
operator|.
name|AUTO_ACKNOWLEDGE
argument_list|)
expr_stmt|;
name|senderSession
operator|=
name|conn
operator|.
name|createSession
argument_list|(
literal|false
argument_list|,
name|Session
operator|.
name|AUTO_ACKNOWLEDGE
argument_list|)
expr_stmt|;
comment|// This creates the exclusive consumer first which avoids AMQ-1024 bug.
name|ActiveMQQueue
name|exclusiveQueue
init|=
operator|new
name|ActiveMQQueue
argument_list|(
literal|"TEST.QUEUE3?consumer.exclusive=true"
argument_list|)
decl_stmt|;
name|MessageConsumer
name|exclusiveConsumer
init|=
name|exclusiveSession
operator|.
name|createConsumer
argument_list|(
name|exclusiveQueue
argument_list|)
decl_stmt|;
name|ActiveMQQueue
name|fallbackQueue
init|=
operator|new
name|ActiveMQQueue
argument_list|(
literal|"TEST.QUEUE3"
argument_list|)
decl_stmt|;
name|MessageConsumer
name|fallbackConsumer
init|=
name|fallbackSession
operator|.
name|createConsumer
argument_list|(
name|fallbackQueue
argument_list|)
decl_stmt|;
name|ActiveMQQueue
name|senderQueue
init|=
operator|new
name|ActiveMQQueue
argument_list|(
literal|"TEST.QUEUE3"
argument_list|)
decl_stmt|;
name|MessageProducer
name|producer
init|=
name|senderSession
operator|.
name|createProducer
argument_list|(
name|senderQueue
argument_list|)
decl_stmt|;
name|Message
name|msg
init|=
name|senderSession
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
name|msg
argument_list|)
expr_stmt|;
name|Thread
operator|.
name|sleep
argument_list|(
literal|100
argument_list|)
expr_stmt|;
comment|// Verify exclusive consumer receives the message.
name|assertNotNull
argument_list|(
name|exclusiveConsumer
operator|.
name|receive
argument_list|(
literal|100
argument_list|)
argument_list|)
expr_stmt|;
name|assertNull
argument_list|(
name|fallbackConsumer
operator|.
name|receive
argument_list|(
literal|100
argument_list|)
argument_list|)
expr_stmt|;
comment|// Close the exclusive consumer to verify the non-exclusive consumer takes over
name|exclusiveConsumer
operator|.
name|close
argument_list|()
expr_stmt|;
name|producer
operator|.
name|send
argument_list|(
name|msg
argument_list|)
expr_stmt|;
name|assertNotNull
argument_list|(
name|fallbackConsumer
operator|.
name|receive
argument_list|(
literal|100
argument_list|)
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|fallbackSession
operator|.
name|close
argument_list|()
expr_stmt|;
name|senderSession
operator|.
name|close
argument_list|()
expr_stmt|;
name|conn
operator|.
name|close
argument_list|()
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
name|testFallbackToExclusiveConsumer
parameter_list|()
throws|throws
name|JMSException
throws|,
name|InterruptedException
block|{
name|Connection
name|conn
init|=
name|createConnection
argument_list|(
literal|true
argument_list|)
decl_stmt|;
name|Session
name|exclusiveSession
init|=
literal|null
decl_stmt|;
name|Session
name|fallbackSession
init|=
literal|null
decl_stmt|;
name|Session
name|senderSession
init|=
literal|null
decl_stmt|;
try|try
block|{
name|exclusiveSession
operator|=
name|conn
operator|.
name|createSession
argument_list|(
literal|false
argument_list|,
name|Session
operator|.
name|AUTO_ACKNOWLEDGE
argument_list|)
expr_stmt|;
name|fallbackSession
operator|=
name|conn
operator|.
name|createSession
argument_list|(
literal|false
argument_list|,
name|Session
operator|.
name|AUTO_ACKNOWLEDGE
argument_list|)
expr_stmt|;
name|senderSession
operator|=
name|conn
operator|.
name|createSession
argument_list|(
literal|false
argument_list|,
name|Session
operator|.
name|AUTO_ACKNOWLEDGE
argument_list|)
expr_stmt|;
comment|// This creates the exclusive consumer first which avoids AMQ-1024 bug.
name|ActiveMQQueue
name|exclusiveQueue
init|=
operator|new
name|ActiveMQQueue
argument_list|(
literal|"TEST.QUEUE4?consumer.exclusive=true"
argument_list|)
decl_stmt|;
name|MessageConsumer
name|exclusiveConsumer
init|=
name|exclusiveSession
operator|.
name|createConsumer
argument_list|(
name|exclusiveQueue
argument_list|)
decl_stmt|;
name|ActiveMQQueue
name|fallbackQueue
init|=
operator|new
name|ActiveMQQueue
argument_list|(
literal|"TEST.QUEUE4"
argument_list|)
decl_stmt|;
name|MessageConsumer
name|fallbackConsumer
init|=
name|fallbackSession
operator|.
name|createConsumer
argument_list|(
name|fallbackQueue
argument_list|)
decl_stmt|;
name|ActiveMQQueue
name|senderQueue
init|=
operator|new
name|ActiveMQQueue
argument_list|(
literal|"TEST.QUEUE4"
argument_list|)
decl_stmt|;
name|MessageProducer
name|producer
init|=
name|senderSession
operator|.
name|createProducer
argument_list|(
name|senderQueue
argument_list|)
decl_stmt|;
name|Message
name|msg
init|=
name|senderSession
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
name|msg
argument_list|)
expr_stmt|;
name|Thread
operator|.
name|sleep
argument_list|(
literal|100
argument_list|)
expr_stmt|;
comment|// Verify exclusive consumer receives the message.
name|assertNotNull
argument_list|(
name|exclusiveConsumer
operator|.
name|receive
argument_list|(
literal|100
argument_list|)
argument_list|)
expr_stmt|;
name|assertNull
argument_list|(
name|fallbackConsumer
operator|.
name|receive
argument_list|(
literal|100
argument_list|)
argument_list|)
expr_stmt|;
comment|// Close the exclusive consumer to verify the non-exclusive consumer takes over
name|exclusiveConsumer
operator|.
name|close
argument_list|()
expr_stmt|;
name|producer
operator|.
name|send
argument_list|(
name|msg
argument_list|)
expr_stmt|;
comment|// Verify other non-exclusive consumer receices the message.
name|assertNotNull
argument_list|(
name|fallbackConsumer
operator|.
name|receive
argument_list|(
literal|100
argument_list|)
argument_list|)
expr_stmt|;
comment|// Create exclusive consumer to determine if it will start receiving the messages.
name|exclusiveConsumer
operator|=
name|exclusiveSession
operator|.
name|createConsumer
argument_list|(
name|exclusiveQueue
argument_list|)
expr_stmt|;
name|producer
operator|.
name|send
argument_list|(
name|msg
argument_list|)
expr_stmt|;
name|assertNotNull
argument_list|(
name|exclusiveConsumer
operator|.
name|receive
argument_list|(
literal|100
argument_list|)
argument_list|)
expr_stmt|;
name|assertNull
argument_list|(
name|fallbackConsumer
operator|.
name|receive
argument_list|(
literal|100
argument_list|)
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|fallbackSession
operator|.
name|close
argument_list|()
expr_stmt|;
name|senderSession
operator|.
name|close
argument_list|()
expr_stmt|;
name|conn
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

