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
name|ArrayList
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
name|DeliveryMode
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
name|MessageListener
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
name|Queue
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
name|Rule
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

begin_class
specifier|public
class|class
name|MessageListenerRedeliveryTest
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
name|MessageListenerRedeliveryTest
operator|.
name|class
argument_list|)
decl_stmt|;
annotation|@
name|Rule
specifier|public
name|TestName
name|name
init|=
operator|new
name|TestName
argument_list|()
decl_stmt|;
specifier|private
name|Connection
name|connection
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
name|connection
operator|=
name|createConnection
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
name|connection
operator|!=
literal|null
condition|)
block|{
name|connection
operator|.
name|close
argument_list|()
expr_stmt|;
name|connection
operator|=
literal|null
expr_stmt|;
block|}
block|}
specifier|protected
name|String
name|getTestName
parameter_list|()
block|{
return|return
name|name
operator|.
name|getMethodName
argument_list|()
return|;
block|}
specifier|protected
name|RedeliveryPolicy
name|getRedeliveryPolicy
parameter_list|()
block|{
name|RedeliveryPolicy
name|redeliveryPolicy
init|=
operator|new
name|RedeliveryPolicy
argument_list|()
decl_stmt|;
name|redeliveryPolicy
operator|.
name|setInitialRedeliveryDelay
argument_list|(
literal|0
argument_list|)
expr_stmt|;
name|redeliveryPolicy
operator|.
name|setRedeliveryDelay
argument_list|(
literal|1000
argument_list|)
expr_stmt|;
name|redeliveryPolicy
operator|.
name|setMaximumRedeliveries
argument_list|(
literal|3
argument_list|)
expr_stmt|;
name|redeliveryPolicy
operator|.
name|setBackOffMultiplier
argument_list|(
operator|(
name|short
operator|)
literal|2
argument_list|)
expr_stmt|;
name|redeliveryPolicy
operator|.
name|setUseExponentialBackOff
argument_list|(
literal|true
argument_list|)
expr_stmt|;
return|return
name|redeliveryPolicy
return|;
block|}
specifier|protected
name|Connection
name|createConnection
parameter_list|()
throws|throws
name|Exception
block|{
name|ActiveMQConnectionFactory
name|factory
init|=
operator|new
name|ActiveMQConnectionFactory
argument_list|(
literal|"vm://localhost?broker.persistent=false&marshal=true"
argument_list|)
decl_stmt|;
name|factory
operator|.
name|setRedeliveryPolicy
argument_list|(
name|getRedeliveryPolicy
argument_list|()
argument_list|)
expr_stmt|;
return|return
name|factory
operator|.
name|createConnection
argument_list|()
return|;
block|}
specifier|private
class|class
name|TestMessageListener
implements|implements
name|MessageListener
block|{
specifier|public
name|int
name|counter
decl_stmt|;
specifier|private
specifier|final
name|Session
name|session
decl_stmt|;
specifier|public
name|TestMessageListener
parameter_list|(
name|Session
name|session
parameter_list|)
block|{
name|this
operator|.
name|session
operator|=
name|session
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|onMessage
parameter_list|(
name|Message
name|message
parameter_list|)
block|{
try|try
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"Message Received: "
operator|+
name|message
argument_list|)
expr_stmt|;
name|counter
operator|++
expr_stmt|;
if|if
condition|(
name|counter
operator|<=
literal|4
condition|)
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"Message Rollback."
argument_list|)
expr_stmt|;
name|session
operator|.
name|rollback
argument_list|()
expr_stmt|;
block|}
else|else
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"Message Commit."
argument_list|)
expr_stmt|;
name|message
operator|.
name|acknowledge
argument_list|()
expr_stmt|;
name|session
operator|.
name|commit
argument_list|()
expr_stmt|;
block|}
block|}
catch|catch
parameter_list|(
name|JMSException
name|e
parameter_list|)
block|{
name|LOG
operator|.
name|error
argument_list|(
literal|"Error when rolling back transaction"
argument_list|)
expr_stmt|;
block|}
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
name|testQueueRollbackConsumerListener
parameter_list|()
throws|throws
name|JMSException
block|{
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
name|CLIENT_ACKNOWLEDGE
argument_list|)
decl_stmt|;
name|Queue
name|queue
init|=
name|session
operator|.
name|createQueue
argument_list|(
literal|"queue-"
operator|+
name|getTestName
argument_list|()
argument_list|)
decl_stmt|;
name|MessageProducer
name|producer
init|=
name|createProducer
argument_list|(
name|session
argument_list|,
name|queue
argument_list|)
decl_stmt|;
name|Message
name|message
init|=
name|createTextMessage
argument_list|(
name|session
argument_list|)
decl_stmt|;
name|producer
operator|.
name|send
argument_list|(
name|message
argument_list|)
expr_stmt|;
name|session
operator|.
name|commit
argument_list|()
expr_stmt|;
name|MessageConsumer
name|consumer
init|=
name|session
operator|.
name|createConsumer
argument_list|(
name|queue
argument_list|)
decl_stmt|;
name|ActiveMQMessageConsumer
name|mc
init|=
operator|(
name|ActiveMQMessageConsumer
operator|)
name|consumer
decl_stmt|;
name|mc
operator|.
name|setRedeliveryPolicy
argument_list|(
name|getRedeliveryPolicy
argument_list|()
argument_list|)
expr_stmt|;
name|TestMessageListener
name|listener
init|=
operator|new
name|TestMessageListener
argument_list|(
name|session
argument_list|)
decl_stmt|;
name|consumer
operator|.
name|setMessageListener
argument_list|(
name|listener
argument_list|)
expr_stmt|;
try|try
block|{
name|Thread
operator|.
name|sleep
argument_list|(
literal|500
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|InterruptedException
name|e
parameter_list|)
block|{         }
comment|// first try.. should get 2 since there is no delay on the
comment|// first redeliver..
name|assertEquals
argument_list|(
literal|2
argument_list|,
name|listener
operator|.
name|counter
argument_list|)
expr_stmt|;
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
name|e
parameter_list|)
block|{         }
comment|// 2nd redeliver (redelivery after 1 sec)
name|assertEquals
argument_list|(
literal|3
argument_list|,
name|listener
operator|.
name|counter
argument_list|)
expr_stmt|;
try|try
block|{
name|Thread
operator|.
name|sleep
argument_list|(
literal|2000
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|InterruptedException
name|e
parameter_list|)
block|{         }
comment|// 3rd redeliver (redelivery after 2 seconds) - it should give up after
comment|// that
name|assertEquals
argument_list|(
literal|4
argument_list|,
name|listener
operator|.
name|counter
argument_list|)
expr_stmt|;
comment|// create new message
name|producer
operator|.
name|send
argument_list|(
name|createTextMessage
argument_list|(
name|session
argument_list|)
argument_list|)
expr_stmt|;
name|session
operator|.
name|commit
argument_list|()
expr_stmt|;
try|try
block|{
name|Thread
operator|.
name|sleep
argument_list|(
literal|500
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|InterruptedException
name|e
parameter_list|)
block|{         }
comment|// it should be committed, so no redelivery
name|assertEquals
argument_list|(
literal|5
argument_list|,
name|listener
operator|.
name|counter
argument_list|)
expr_stmt|;
try|try
block|{
name|Thread
operator|.
name|sleep
argument_list|(
literal|1500
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|InterruptedException
name|e
parameter_list|)
block|{         }
comment|// no redelivery, counter should still be 4
name|assertEquals
argument_list|(
literal|5
argument_list|,
name|listener
operator|.
name|counter
argument_list|)
expr_stmt|;
name|session
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
name|testQueueRollbackSessionListener
parameter_list|()
throws|throws
name|JMSException
block|{
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
name|CLIENT_ACKNOWLEDGE
argument_list|)
decl_stmt|;
name|Queue
name|queue
init|=
name|session
operator|.
name|createQueue
argument_list|(
literal|"queue-"
operator|+
name|getTestName
argument_list|()
argument_list|)
decl_stmt|;
name|MessageProducer
name|producer
init|=
name|createProducer
argument_list|(
name|session
argument_list|,
name|queue
argument_list|)
decl_stmt|;
name|Message
name|message
init|=
name|createTextMessage
argument_list|(
name|session
argument_list|)
decl_stmt|;
name|producer
operator|.
name|send
argument_list|(
name|message
argument_list|)
expr_stmt|;
name|session
operator|.
name|commit
argument_list|()
expr_stmt|;
name|MessageConsumer
name|consumer
init|=
name|session
operator|.
name|createConsumer
argument_list|(
name|queue
argument_list|)
decl_stmt|;
name|ActiveMQMessageConsumer
name|mc
init|=
operator|(
name|ActiveMQMessageConsumer
operator|)
name|consumer
decl_stmt|;
name|mc
operator|.
name|setRedeliveryPolicy
argument_list|(
name|getRedeliveryPolicy
argument_list|()
argument_list|)
expr_stmt|;
name|TestMessageListener
name|listener
init|=
operator|new
name|TestMessageListener
argument_list|(
name|session
argument_list|)
decl_stmt|;
name|consumer
operator|.
name|setMessageListener
argument_list|(
name|listener
argument_list|)
expr_stmt|;
try|try
block|{
name|Thread
operator|.
name|sleep
argument_list|(
literal|500
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|InterruptedException
name|e
parameter_list|)
block|{          }
comment|// first try
name|assertEquals
argument_list|(
literal|2
argument_list|,
name|listener
operator|.
name|counter
argument_list|)
expr_stmt|;
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
name|e
parameter_list|)
block|{          }
comment|// second try (redelivery after 1 sec)
name|assertEquals
argument_list|(
literal|3
argument_list|,
name|listener
operator|.
name|counter
argument_list|)
expr_stmt|;
try|try
block|{
name|Thread
operator|.
name|sleep
argument_list|(
literal|2000
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|InterruptedException
name|e
parameter_list|)
block|{          }
comment|// third try (redelivery after 2 seconds) - it should give up after that
name|assertEquals
argument_list|(
literal|4
argument_list|,
name|listener
operator|.
name|counter
argument_list|)
expr_stmt|;
comment|// create new message
name|producer
operator|.
name|send
argument_list|(
name|createTextMessage
argument_list|(
name|session
argument_list|)
argument_list|)
expr_stmt|;
name|session
operator|.
name|commit
argument_list|()
expr_stmt|;
try|try
block|{
name|Thread
operator|.
name|sleep
argument_list|(
literal|500
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|InterruptedException
name|e
parameter_list|)
block|{
comment|// ignore
block|}
comment|// it should be committed, so no redelivery
name|assertEquals
argument_list|(
literal|5
argument_list|,
name|listener
operator|.
name|counter
argument_list|)
expr_stmt|;
try|try
block|{
name|Thread
operator|.
name|sleep
argument_list|(
literal|1500
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|InterruptedException
name|e
parameter_list|)
block|{
comment|// ignore
block|}
comment|// no redelivery, counter should still be 4
name|assertEquals
argument_list|(
literal|5
argument_list|,
name|listener
operator|.
name|counter
argument_list|)
expr_stmt|;
name|session
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
name|testQueueSessionListenerExceptionRetry
parameter_list|()
throws|throws
name|Exception
block|{
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
literal|false
argument_list|,
name|Session
operator|.
name|AUTO_ACKNOWLEDGE
argument_list|)
decl_stmt|;
name|Queue
name|queue
init|=
name|session
operator|.
name|createQueue
argument_list|(
literal|"queue-"
operator|+
name|getTestName
argument_list|()
argument_list|)
decl_stmt|;
name|MessageProducer
name|producer
init|=
name|createProducer
argument_list|(
name|session
argument_list|,
name|queue
argument_list|)
decl_stmt|;
name|Message
name|message
init|=
name|createTextMessage
argument_list|(
name|session
argument_list|,
literal|"1"
argument_list|)
decl_stmt|;
name|producer
operator|.
name|send
argument_list|(
name|message
argument_list|)
expr_stmt|;
name|message
operator|=
name|createTextMessage
argument_list|(
name|session
argument_list|,
literal|"2"
argument_list|)
expr_stmt|;
name|producer
operator|.
name|send
argument_list|(
name|message
argument_list|)
expr_stmt|;
name|MessageConsumer
name|consumer
init|=
name|session
operator|.
name|createConsumer
argument_list|(
name|queue
argument_list|)
decl_stmt|;
specifier|final
name|CountDownLatch
name|gotMessage
init|=
operator|new
name|CountDownLatch
argument_list|(
literal|2
argument_list|)
decl_stmt|;
specifier|final
name|AtomicInteger
name|count
init|=
operator|new
name|AtomicInteger
argument_list|(
literal|0
argument_list|)
decl_stmt|;
specifier|final
name|int
name|maxDeliveries
init|=
name|getRedeliveryPolicy
argument_list|()
operator|.
name|getMaximumRedeliveries
argument_list|()
decl_stmt|;
specifier|final
name|ArrayList
argument_list|<
name|String
argument_list|>
name|received
init|=
operator|new
name|ArrayList
argument_list|<
name|String
argument_list|>
argument_list|()
decl_stmt|;
name|consumer
operator|.
name|setMessageListener
argument_list|(
operator|new
name|MessageListener
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|void
name|onMessage
parameter_list|(
name|Message
name|message
parameter_list|)
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"Message Received: "
operator|+
name|message
argument_list|)
expr_stmt|;
try|try
block|{
name|received
operator|.
name|add
argument_list|(
operator|(
operator|(
name|TextMessage
operator|)
name|message
operator|)
operator|.
name|getText
argument_list|()
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|JMSException
name|e
parameter_list|)
block|{
name|e
operator|.
name|printStackTrace
argument_list|()
expr_stmt|;
name|fail
argument_list|(
name|e
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|count
operator|.
name|incrementAndGet
argument_list|()
operator|<
name|maxDeliveries
condition|)
block|{
throw|throw
operator|new
name|RuntimeException
argument_list|(
name|getTestName
argument_list|()
operator|+
literal|" force a redelivery"
argument_list|)
throw|;
block|}
comment|// new blood
name|count
operator|.
name|set
argument_list|(
literal|0
argument_list|)
expr_stmt|;
name|gotMessage
operator|.
name|countDown
argument_list|()
expr_stmt|;
block|}
block|}
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
literal|"got message before retry expiry"
argument_list|,
name|gotMessage
operator|.
name|await
argument_list|(
literal|20
argument_list|,
name|TimeUnit
operator|.
name|SECONDS
argument_list|)
argument_list|)
expr_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|maxDeliveries
condition|;
name|i
operator|++
control|)
block|{
name|assertEquals
argument_list|(
literal|"got first redelivered: "
operator|+
name|i
argument_list|,
literal|"1"
argument_list|,
name|received
operator|.
name|get
argument_list|(
name|i
argument_list|)
argument_list|)
expr_stmt|;
block|}
for|for
control|(
name|int
name|i
init|=
name|maxDeliveries
init|;
name|i
operator|<
name|maxDeliveries
operator|*
literal|2
condition|;
name|i
operator|++
control|)
block|{
name|assertEquals
argument_list|(
literal|"got first redelivered: "
operator|+
name|i
argument_list|,
literal|"2"
argument_list|,
name|received
operator|.
name|get
argument_list|(
name|i
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|session
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
name|testQueueSessionListenerExceptionDlq
parameter_list|()
throws|throws
name|Exception
block|{
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
literal|false
argument_list|,
name|Session
operator|.
name|AUTO_ACKNOWLEDGE
argument_list|)
decl_stmt|;
name|Queue
name|queue
init|=
name|session
operator|.
name|createQueue
argument_list|(
literal|"queue-"
operator|+
name|getTestName
argument_list|()
argument_list|)
decl_stmt|;
name|MessageProducer
name|producer
init|=
name|createProducer
argument_list|(
name|session
argument_list|,
name|queue
argument_list|)
decl_stmt|;
name|Message
name|message
init|=
name|createTextMessage
argument_list|(
name|session
argument_list|)
decl_stmt|;
name|producer
operator|.
name|send
argument_list|(
name|message
argument_list|)
expr_stmt|;
specifier|final
name|Message
index|[]
name|dlqMessage
init|=
operator|new
name|Message
index|[
literal|1
index|]
decl_stmt|;
name|ActiveMQDestination
name|dlqDestination
init|=
operator|new
name|ActiveMQQueue
argument_list|(
literal|"ActiveMQ.DLQ"
argument_list|)
decl_stmt|;
name|MessageConsumer
name|dlqConsumer
init|=
name|session
operator|.
name|createConsumer
argument_list|(
name|dlqDestination
argument_list|)
decl_stmt|;
specifier|final
name|CountDownLatch
name|gotDlqMessage
init|=
operator|new
name|CountDownLatch
argument_list|(
literal|1
argument_list|)
decl_stmt|;
name|dlqConsumer
operator|.
name|setMessageListener
argument_list|(
operator|new
name|MessageListener
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|void
name|onMessage
parameter_list|(
name|Message
name|message
parameter_list|)
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"DLQ Message Received: "
operator|+
name|message
argument_list|)
expr_stmt|;
name|dlqMessage
index|[
literal|0
index|]
operator|=
name|message
expr_stmt|;
name|gotDlqMessage
operator|.
name|countDown
argument_list|()
expr_stmt|;
block|}
block|}
argument_list|)
expr_stmt|;
name|MessageConsumer
name|consumer
init|=
name|session
operator|.
name|createConsumer
argument_list|(
name|queue
argument_list|)
decl_stmt|;
specifier|final
name|int
name|maxDeliveries
init|=
name|getRedeliveryPolicy
argument_list|()
operator|.
name|getMaximumRedeliveries
argument_list|()
decl_stmt|;
specifier|final
name|CountDownLatch
name|gotMessage
init|=
operator|new
name|CountDownLatch
argument_list|(
name|maxDeliveries
argument_list|)
decl_stmt|;
name|consumer
operator|.
name|setMessageListener
argument_list|(
operator|new
name|MessageListener
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|void
name|onMessage
parameter_list|(
name|Message
name|message
parameter_list|)
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"Message Received: "
operator|+
name|message
argument_list|)
expr_stmt|;
name|gotMessage
operator|.
name|countDown
argument_list|()
expr_stmt|;
throw|throw
operator|new
name|RuntimeException
argument_list|(
name|getTestName
argument_list|()
operator|+
literal|" force a redelivery"
argument_list|)
throw|;
block|}
block|}
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
literal|"got message before retry expiry"
argument_list|,
name|gotMessage
operator|.
name|await
argument_list|(
literal|20
argument_list|,
name|TimeUnit
operator|.
name|SECONDS
argument_list|)
argument_list|)
expr_stmt|;
comment|// check DLQ
name|assertTrue
argument_list|(
literal|"got dlq message"
argument_list|,
name|gotDlqMessage
operator|.
name|await
argument_list|(
literal|20
argument_list|,
name|TimeUnit
operator|.
name|SECONDS
argument_list|)
argument_list|)
expr_stmt|;
comment|// check DLQ message cause is captured
name|message
operator|=
name|dlqMessage
index|[
literal|0
index|]
expr_stmt|;
name|assertNotNull
argument_list|(
literal|"dlq message captured"
argument_list|,
name|message
argument_list|)
expr_stmt|;
name|String
name|cause
init|=
name|message
operator|.
name|getStringProperty
argument_list|(
name|ActiveMQMessage
operator|.
name|DLQ_DELIVERY_FAILURE_CAUSE_PROPERTY
argument_list|)
decl_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"DLQ'd message cause reported as: {}"
argument_list|,
name|cause
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
literal|"cause 'cause' exception is remembered"
argument_list|,
name|cause
operator|.
name|contains
argument_list|(
literal|"RuntimeException"
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
literal|"is correct exception"
argument_list|,
name|cause
operator|.
name|contains
argument_list|(
name|getTestName
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
literal|"cause exception is remembered"
argument_list|,
name|cause
operator|.
name|contains
argument_list|(
literal|"Throwable"
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
literal|"cause policy is remembered"
argument_list|,
name|cause
operator|.
name|contains
argument_list|(
literal|"RedeliveryPolicy"
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
literal|"cause redelivered count is remembered"
argument_list|,
name|cause
operator|.
name|contains
argument_list|(
literal|"["
operator|+
operator|(
name|maxDeliveries
operator|+
literal|1
operator|)
operator|+
literal|"]"
argument_list|)
argument_list|)
expr_stmt|;
name|session
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
name|testTransactedQueueSessionListenerExceptionDlq
parameter_list|()
throws|throws
name|Exception
block|{
name|connection
operator|.
name|start
argument_list|()
expr_stmt|;
specifier|final
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
name|Queue
name|queue
init|=
name|session
operator|.
name|createQueue
argument_list|(
literal|"queue-"
operator|+
name|getTestName
argument_list|()
argument_list|)
decl_stmt|;
name|MessageProducer
name|producer
init|=
name|createProducer
argument_list|(
name|session
argument_list|,
name|queue
argument_list|)
decl_stmt|;
name|Message
name|message
init|=
name|createTextMessage
argument_list|(
name|session
argument_list|)
decl_stmt|;
name|producer
operator|.
name|send
argument_list|(
name|message
argument_list|)
expr_stmt|;
name|session
operator|.
name|commit
argument_list|()
expr_stmt|;
specifier|final
name|Message
index|[]
name|dlqMessage
init|=
operator|new
name|Message
index|[
literal|1
index|]
decl_stmt|;
name|ActiveMQDestination
name|dlqDestination
init|=
operator|new
name|ActiveMQQueue
argument_list|(
literal|"ActiveMQ.DLQ"
argument_list|)
decl_stmt|;
name|MessageConsumer
name|dlqConsumer
init|=
name|session
operator|.
name|createConsumer
argument_list|(
name|dlqDestination
argument_list|)
decl_stmt|;
specifier|final
name|CountDownLatch
name|gotDlqMessage
init|=
operator|new
name|CountDownLatch
argument_list|(
literal|1
argument_list|)
decl_stmt|;
name|dlqConsumer
operator|.
name|setMessageListener
argument_list|(
operator|new
name|MessageListener
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|void
name|onMessage
parameter_list|(
name|Message
name|message
parameter_list|)
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"DLQ Message Received: "
operator|+
name|message
argument_list|)
expr_stmt|;
name|dlqMessage
index|[
literal|0
index|]
operator|=
name|message
expr_stmt|;
name|gotDlqMessage
operator|.
name|countDown
argument_list|()
expr_stmt|;
block|}
block|}
argument_list|)
expr_stmt|;
name|MessageConsumer
name|consumer
init|=
name|session
operator|.
name|createConsumer
argument_list|(
name|queue
argument_list|)
decl_stmt|;
specifier|final
name|int
name|maxDeliveries
init|=
name|getRedeliveryPolicy
argument_list|()
operator|.
name|getMaximumRedeliveries
argument_list|()
decl_stmt|;
specifier|final
name|CountDownLatch
name|gotMessage
init|=
operator|new
name|CountDownLatch
argument_list|(
name|maxDeliveries
argument_list|)
decl_stmt|;
name|consumer
operator|.
name|setMessageListener
argument_list|(
operator|new
name|MessageListener
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|void
name|onMessage
parameter_list|(
name|Message
name|message
parameter_list|)
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"Message Received: "
operator|+
name|message
argument_list|)
expr_stmt|;
name|gotMessage
operator|.
name|countDown
argument_list|()
expr_stmt|;
try|try
block|{
name|session
operator|.
name|rollback
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|JMSException
name|e
parameter_list|)
block|{
name|e
operator|.
name|printStackTrace
argument_list|()
expr_stmt|;
block|}
throw|throw
operator|new
name|RuntimeException
argument_list|(
name|getTestName
argument_list|()
operator|+
literal|" force a redelivery"
argument_list|)
throw|;
block|}
block|}
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
literal|"got message before retry expiry"
argument_list|,
name|gotMessage
operator|.
name|await
argument_list|(
literal|20
argument_list|,
name|TimeUnit
operator|.
name|SECONDS
argument_list|)
argument_list|)
expr_stmt|;
comment|// check DLQ
name|assertTrue
argument_list|(
literal|"got dlq message"
argument_list|,
name|gotDlqMessage
operator|.
name|await
argument_list|(
literal|20
argument_list|,
name|TimeUnit
operator|.
name|SECONDS
argument_list|)
argument_list|)
expr_stmt|;
comment|// check DLQ message cause is captured
name|message
operator|=
name|dlqMessage
index|[
literal|0
index|]
expr_stmt|;
name|assertNotNull
argument_list|(
literal|"dlq message captured"
argument_list|,
name|message
argument_list|)
expr_stmt|;
name|String
name|cause
init|=
name|message
operator|.
name|getStringProperty
argument_list|(
name|ActiveMQMessage
operator|.
name|DLQ_DELIVERY_FAILURE_CAUSE_PROPERTY
argument_list|)
decl_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"DLQ'd message cause reported as: {}"
argument_list|,
name|cause
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
literal|"cause 'cause' exception is remembered"
argument_list|,
name|cause
operator|.
name|contains
argument_list|(
literal|"RuntimeException"
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
literal|"is correct exception"
argument_list|,
name|cause
operator|.
name|contains
argument_list|(
name|getTestName
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
literal|"cause exception is remembered"
argument_list|,
name|cause
operator|.
name|contains
argument_list|(
literal|"Throwable"
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
literal|"cause policy is remembered"
argument_list|,
name|cause
operator|.
name|contains
argument_list|(
literal|"RedeliveryPolicy"
argument_list|)
argument_list|)
expr_stmt|;
name|session
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
specifier|private
name|TextMessage
name|createTextMessage
parameter_list|(
name|Session
name|session
parameter_list|,
name|String
name|text
parameter_list|)
throws|throws
name|JMSException
block|{
return|return
name|session
operator|.
name|createTextMessage
argument_list|(
name|text
argument_list|)
return|;
block|}
specifier|private
name|TextMessage
name|createTextMessage
parameter_list|(
name|Session
name|session
parameter_list|)
throws|throws
name|JMSException
block|{
return|return
name|session
operator|.
name|createTextMessage
argument_list|(
literal|"Hello"
argument_list|)
return|;
block|}
specifier|private
name|MessageProducer
name|createProducer
parameter_list|(
name|Session
name|session
parameter_list|,
name|Destination
name|queue
parameter_list|)
throws|throws
name|JMSException
block|{
name|MessageProducer
name|producer
init|=
name|session
operator|.
name|createProducer
argument_list|(
name|queue
argument_list|)
decl_stmt|;
name|producer
operator|.
name|setDeliveryMode
argument_list|(
name|getDeliveryMode
argument_list|()
argument_list|)
expr_stmt|;
return|return
name|producer
return|;
block|}
specifier|protected
name|int
name|getDeliveryMode
parameter_list|()
block|{
return|return
name|DeliveryMode
operator|.
name|PERSISTENT
return|;
block|}
block|}
end_class

end_unit

