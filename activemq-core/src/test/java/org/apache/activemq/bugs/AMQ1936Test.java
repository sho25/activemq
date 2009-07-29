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
name|bugs
package|;
end_package

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|activemq
operator|.
name|ActiveMQConnectionFactory
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
name|log4j
operator|.
name|Logger
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
name|LinkedBlockingQueue
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
name|ThreadPoolExecutor
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
name|AtomicBoolean
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
name|DeliveryMode
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
name|Queue
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|jms
operator|.
name|QueueConnection
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|jms
operator|.
name|QueueConnectionFactory
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|jms
operator|.
name|QueueReceiver
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|jms
operator|.
name|QueueSender
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|jms
operator|.
name|QueueSession
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
name|javax
operator|.
name|naming
operator|.
name|NamingException
import|;
end_import

begin_import
import|import
name|junit
operator|.
name|framework
operator|.
name|TestCase
import|;
end_import

begin_comment
comment|/**  * A AMQ1936Test  *  */
end_comment

begin_class
specifier|public
class|class
name|AMQ1936Test
extends|extends
name|TestCase
block|{
specifier|private
specifier|final
specifier|static
name|Logger
name|logger
init|=
name|Logger
operator|.
name|getLogger
argument_list|(
name|AMQ1936Test
operator|.
name|class
argument_list|)
decl_stmt|;
specifier|private
specifier|final
specifier|static
name|String
name|TEST_QUEUE_NAME
init|=
literal|"dynamicQueues/duplicate.message.test.queue"
decl_stmt|;
comment|////--
comment|//
specifier|private
specifier|final
specifier|static
name|long
name|TEST_MESSAGE_COUNT
init|=
literal|60000
decl_stmt|;
comment|// The number of test messages to use
comment|//
comment|////--
specifier|private
specifier|final
specifier|static
name|int
name|CONSUMER_COUNT
init|=
literal|2
decl_stmt|;
comment|// The number of message receiver instances
specifier|private
specifier|final
specifier|static
name|boolean
name|TRANSACTED_RECEIVE
init|=
literal|true
decl_stmt|;
comment|// Flag used by receiver which indicates messages should be processed within a JMS transaction
specifier|private
name|ThreadPoolExecutor
name|threadPool
init|=
operator|new
name|ThreadPoolExecutor
argument_list|(
name|CONSUMER_COUNT
argument_list|,
name|CONSUMER_COUNT
argument_list|,
name|Long
operator|.
name|MAX_VALUE
argument_list|,
name|TimeUnit
operator|.
name|SECONDS
argument_list|,
operator|new
name|LinkedBlockingQueue
argument_list|<
name|Runnable
argument_list|>
argument_list|()
argument_list|)
decl_stmt|;
specifier|private
name|ThreadedMessageReceiver
index|[]
name|receivers
init|=
operator|new
name|ThreadedMessageReceiver
index|[
name|CONSUMER_COUNT
index|]
decl_stmt|;
specifier|private
name|BrokerService
name|broker
init|=
literal|null
decl_stmt|;
specifier|static
name|QueueConnectionFactory
name|connectionFactory
init|=
literal|null
decl_stmt|;
annotation|@
name|Override
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
name|broker
operator|=
operator|new
name|BrokerService
argument_list|()
expr_stmt|;
name|broker
operator|.
name|getSystemUsage
argument_list|()
operator|.
name|getMemoryUsage
argument_list|()
operator|.
name|setLimit
argument_list|(
literal|5
operator|*
literal|1024
operator|*
literal|1024
argument_list|)
expr_stmt|;
name|broker
operator|.
name|setBrokerName
argument_list|(
literal|"test"
argument_list|)
expr_stmt|;
name|broker
operator|.
name|setDeleteAllMessagesOnStartup
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|broker
operator|.
name|start
argument_list|()
expr_stmt|;
name|connectionFactory
operator|=
operator|new
name|ActiveMQConnectionFactory
argument_list|(
literal|"vm://test"
argument_list|)
expr_stmt|;
empty_stmt|;
block|}
annotation|@
name|Override
specifier|protected
name|void
name|tearDown
parameter_list|()
throws|throws
name|Exception
block|{
name|super
operator|.
name|tearDown
argument_list|()
expr_stmt|;
if|if
condition|(
name|threadPool
operator|!=
literal|null
condition|)
block|{
comment|// signal receivers to stop
for|for
control|(
name|ThreadedMessageReceiver
name|receiver
range|:
name|receivers
control|)
block|{
name|receiver
operator|.
name|setShouldStop
argument_list|(
literal|true
argument_list|)
expr_stmt|;
block|}
name|logger
operator|.
name|info
argument_list|(
literal|"Waiting for receivers to shutdown.."
argument_list|)
expr_stmt|;
if|if
condition|(
operator|!
name|threadPool
operator|.
name|awaitTermination
argument_list|(
literal|10
argument_list|,
name|TimeUnit
operator|.
name|SECONDS
argument_list|)
condition|)
block|{
name|logger
operator|.
name|warn
argument_list|(
literal|"Not all receivers completed shutdown."
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|logger
operator|.
name|info
argument_list|(
literal|"All receivers shutdown successfully.."
argument_list|)
expr_stmt|;
block|}
block|}
name|logger
operator|.
name|debug
argument_list|(
literal|"Stoping the broker."
argument_list|)
expr_stmt|;
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
block|}
block|}
specifier|private
name|void
name|sendTextMessage
parameter_list|(
name|String
name|queueName
parameter_list|,
name|String
name|msg
parameter_list|)
throws|throws
name|JMSException
throws|,
name|NamingException
block|{
name|QueueConnectionFactory
name|connectionFactory
init|=
operator|new
name|ActiveMQConnectionFactory
argument_list|(
literal|"vm://test"
argument_list|)
decl_stmt|;
name|QueueConnection
name|queueConnection
init|=
literal|null
decl_stmt|;
name|QueueSession
name|session
init|=
literal|null
decl_stmt|;
name|QueueSender
name|sender
init|=
literal|null
decl_stmt|;
name|Queue
name|queue
init|=
literal|null
decl_stmt|;
name|TextMessage
name|message
init|=
literal|null
decl_stmt|;
try|try
block|{
comment|// Create the queue connection
name|queueConnection
operator|=
name|connectionFactory
operator|.
name|createQueueConnection
argument_list|()
expr_stmt|;
name|session
operator|=
name|queueConnection
operator|.
name|createQueueSession
argument_list|(
literal|false
argument_list|,
name|QueueSession
operator|.
name|AUTO_ACKNOWLEDGE
argument_list|)
expr_stmt|;
name|queue
operator|=
name|session
operator|.
name|createQueue
argument_list|(
name|TEST_QUEUE_NAME
argument_list|)
expr_stmt|;
name|sender
operator|=
name|session
operator|.
name|createSender
argument_list|(
name|queue
argument_list|)
expr_stmt|;
name|sender
operator|.
name|setDeliveryMode
argument_list|(
name|DeliveryMode
operator|.
name|PERSISTENT
argument_list|)
expr_stmt|;
name|message
operator|=
name|session
operator|.
name|createTextMessage
argument_list|(
name|msg
argument_list|)
expr_stmt|;
comment|// send the message
name|sender
operator|.
name|send
argument_list|(
name|message
argument_list|)
expr_stmt|;
if|if
condition|(
name|session
operator|.
name|getTransacted
argument_list|()
condition|)
block|{
name|session
operator|.
name|commit
argument_list|()
expr_stmt|;
block|}
name|logger
operator|.
name|info
argument_list|(
literal|"Message successfully sent to : "
operator|+
name|queue
operator|.
name|getQueueName
argument_list|( )
operator|+
literal|" messageid: "
operator|+
name|message
operator|.
name|getJMSMessageID
argument_list|( )
operator|+
literal|" content:"
operator|+
name|message
operator|.
name|getText
argument_list|()
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
if|if
condition|(
name|sender
operator|!=
literal|null
condition|)
block|{
name|sender
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
if|if
condition|(
name|session
operator|!=
literal|null
condition|)
block|{
name|session
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
if|if
condition|(
name|queueConnection
operator|!=
literal|null
condition|)
block|{
name|queueConnection
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
block|}
specifier|public
name|void
name|testForDuplicateMessages
parameter_list|( )
throws|throws
name|Exception
block|{
specifier|final
name|ConcurrentHashMap
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|messages
init|=
operator|new
name|ConcurrentHashMap
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
argument_list|( )
decl_stmt|;
specifier|final
name|Object
name|lock
init|=
operator|new
name|Object
argument_list|( )
decl_stmt|;
specifier|final
name|CountDownLatch
name|duplicateSignal
init|=
operator|new
name|CountDownLatch
argument_list|(
literal|1
argument_list|)
decl_stmt|;
specifier|final
name|AtomicInteger
name|messageCount
init|=
operator|new
name|AtomicInteger
argument_list|(
literal|0
argument_list|)
decl_stmt|;
comment|// add 1/2 the number of our total messages
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|TEST_MESSAGE_COUNT
operator|/
literal|2
condition|;
name|i
operator|++
control|)
block|{
if|if
condition|(
name|duplicateSignal
operator|.
name|getCount
argument_list|()
operator|==
literal|0
condition|)
block|{
name|fail
argument_list|(
literal|"Duplicate message id detected"
argument_list|)
expr_stmt|;
block|}
name|sendTextMessage
argument_list|(
name|TEST_QUEUE_NAME
argument_list|,
name|String
operator|.
name|valueOf
argument_list|(
name|i
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|// create a number of consumers to read of the messages and start them with a handler which simply stores the message ids
comment|// in a Map and checks for a duplicate
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|CONSUMER_COUNT
condition|;
name|i
operator|++
control|)
block|{
name|receivers
index|[
name|i
index|]
operator|=
operator|new
name|ThreadedMessageReceiver
argument_list|(
name|TEST_QUEUE_NAME
argument_list|,
operator|new
name|IMessageHandler
argument_list|( )
block|{
specifier|public
name|void
name|onMessage
parameter_list|(
name|Message
name|message
parameter_list|)
throws|throws
name|Exception
block|{
synchronized|synchronized
init|(
name|lock
init|)
block|{
name|logger
operator|.
name|info
argument_list|(
literal|"Received message:"
operator|+
name|message
operator|.
name|getJMSMessageID
argument_list|()
operator|+
literal|" with content: "
operator|+
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
name|messageCount
operator|.
name|incrementAndGet
argument_list|()
expr_stmt|;
if|if
condition|(
name|messages
operator|.
name|containsKey
argument_list|(
name|message
operator|.
name|getJMSMessageID
argument_list|()
argument_list|)
condition|)
block|{
name|duplicateSignal
operator|.
name|countDown
argument_list|( )
expr_stmt|;
name|logger
operator|.
name|fatal
argument_list|(
literal|"duplicate message id detected:"
operator|+
name|message
operator|.
name|getJMSMessageID
argument_list|()
argument_list|)
expr_stmt|;
name|fail
argument_list|(
literal|"Duplicate message id detected:"
operator|+
name|message
operator|.
name|getJMSMessageID
argument_list|()
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|messages
operator|.
name|put
argument_list|(
name|message
operator|.
name|getJMSMessageID
argument_list|()
argument_list|,
name|message
operator|.
name|getJMSMessageID
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|}
argument_list|)
expr_stmt|;
name|threadPool
operator|.
name|submit
argument_list|(
name|receivers
index|[
name|i
index|]
argument_list|)
expr_stmt|;
block|}
comment|// starting adding the remaining messages
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|TEST_MESSAGE_COUNT
operator|/
literal|2
condition|;
name|i
operator|++
control|)
block|{
if|if
condition|(
name|duplicateSignal
operator|.
name|getCount
argument_list|()
operator|==
literal|0
condition|)
block|{
name|fail
argument_list|(
literal|"Duplicate message id detected"
argument_list|)
expr_stmt|;
block|}
name|sendTextMessage
argument_list|(
name|TEST_QUEUE_NAME
argument_list|,
name|String
operator|.
name|valueOf
argument_list|(
name|i
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|// allow some time for messages to be delivered to receivers.
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
specifier|public
name|boolean
name|isSatisified
parameter_list|()
throws|throws
name|Exception
block|{
return|return
name|TEST_MESSAGE_COUNT
operator|==
name|messages
operator|.
name|size
argument_list|()
return|;
block|}
block|}
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"Number of messages received does not match the number sent"
argument_list|,
name|TEST_MESSAGE_COUNT
argument_list|,
name|messages
operator|.
name|size
argument_list|( )
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|TEST_MESSAGE_COUNT
argument_list|,
name|messageCount
operator|.
name|get
argument_list|()
argument_list|)
expr_stmt|;
block|}
specifier|private
specifier|final
specifier|static
class|class
name|ThreadedMessageReceiver
implements|implements
name|Runnable
block|{
specifier|private
name|String
name|queueName
init|=
literal|null
decl_stmt|;
specifier|private
name|IMessageHandler
name|handler
init|=
literal|null
decl_stmt|;
specifier|private
name|AtomicBoolean
name|shouldStop
init|=
operator|new
name|AtomicBoolean
argument_list|(
literal|false
argument_list|)
decl_stmt|;
specifier|public
name|ThreadedMessageReceiver
parameter_list|(
name|String
name|queueName
parameter_list|,
name|IMessageHandler
name|handler
parameter_list|)
block|{
name|this
operator|.
name|queueName
operator|=
name|queueName
expr_stmt|;
name|this
operator|.
name|handler
operator|=
name|handler
expr_stmt|;
block|}
specifier|public
name|void
name|run
parameter_list|( )
block|{
name|QueueConnection
name|queueConnection
init|=
literal|null
decl_stmt|;
name|QueueSession
name|session
init|=
literal|null
decl_stmt|;
name|QueueReceiver
name|receiver
init|=
literal|null
decl_stmt|;
name|Queue
name|queue
init|=
literal|null
decl_stmt|;
name|Message
name|message
init|=
literal|null
decl_stmt|;
try|try
block|{
try|try
block|{
name|queueConnection
operator|=
name|connectionFactory
operator|.
name|createQueueConnection
argument_list|( )
expr_stmt|;
comment|// create a transacted session
name|session
operator|=
name|queueConnection
operator|.
name|createQueueSession
argument_list|(
name|TRANSACTED_RECEIVE
argument_list|,
name|QueueSession
operator|.
name|AUTO_ACKNOWLEDGE
argument_list|)
expr_stmt|;
name|queue
operator|=
name|session
operator|.
name|createQueue
argument_list|(
name|TEST_QUEUE_NAME
argument_list|)
expr_stmt|;
name|receiver
operator|=
name|session
operator|.
name|createReceiver
argument_list|(
name|queue
argument_list|)
expr_stmt|;
comment|// start the connection
name|queueConnection
operator|.
name|start
argument_list|( )
expr_stmt|;
name|logger
operator|.
name|info
argument_list|(
literal|"Receiver "
operator|+
name|Thread
operator|.
name|currentThread
argument_list|()
operator|.
name|getName
argument_list|()
operator|+
literal|" connected."
argument_list|)
expr_stmt|;
comment|// start receive loop
while|while
condition|(
operator|!
operator|(
name|shouldStop
operator|.
name|get
argument_list|()
operator|||
name|Thread
operator|.
name|currentThread
argument_list|()
operator|.
name|isInterrupted
argument_list|()
operator|)
condition|)
block|{
try|try
block|{
name|message
operator|=
name|receiver
operator|.
name|receive
argument_list|(
literal|200
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
comment|//
comment|// ignore interrupted exceptions
comment|//
if|if
condition|(
name|e
operator|instanceof
name|InterruptedException
operator|||
name|e
operator|.
name|getCause
argument_list|()
operator|instanceof
name|InterruptedException
condition|)
block|{
comment|/* ignore */
block|}
else|else
block|{
throw|throw
name|e
throw|;
block|}
block|}
if|if
condition|(
name|message
operator|!=
literal|null
operator|&&
name|this
operator|.
name|handler
operator|!=
literal|null
condition|)
block|{
name|this
operator|.
name|handler
operator|.
name|onMessage
argument_list|(
name|message
argument_list|)
expr_stmt|;
block|}
comment|// commit session on successful handling of message
if|if
condition|(
name|session
operator|.
name|getTransacted
argument_list|()
condition|)
block|{
name|session
operator|.
name|commit
argument_list|()
expr_stmt|;
block|}
block|}
name|logger
operator|.
name|info
argument_list|(
literal|"Receiver "
operator|+
name|Thread
operator|.
name|currentThread
argument_list|()
operator|.
name|getName
argument_list|()
operator|+
literal|" shutting down."
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
if|if
condition|(
name|receiver
operator|!=
literal|null
condition|)
block|{
try|try
block|{
name|receiver
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|JMSException
name|e
parameter_list|)
block|{
name|logger
operator|.
name|warn
argument_list|(
name|e
argument_list|)
expr_stmt|;
block|}
block|}
if|if
condition|(
name|session
operator|!=
literal|null
condition|)
block|{
try|try
block|{
name|session
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|JMSException
name|e
parameter_list|)
block|{
name|logger
operator|.
name|warn
argument_list|(
name|e
argument_list|)
expr_stmt|;
block|}
block|}
if|if
condition|(
name|queueConnection
operator|!=
literal|null
condition|)
block|{
name|queueConnection
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
block|}
catch|catch
parameter_list|(
name|JMSException
name|e
parameter_list|)
block|{
name|logger
operator|.
name|error
argument_list|(
name|e
argument_list|)
expr_stmt|;
name|e
operator|.
name|printStackTrace
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|NamingException
name|e
parameter_list|)
block|{
name|logger
operator|.
name|error
argument_list|(
name|e
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
name|logger
operator|.
name|error
argument_list|(
name|e
argument_list|)
expr_stmt|;
name|e
operator|.
name|printStackTrace
argument_list|()
expr_stmt|;
block|}
block|}
specifier|public
name|Boolean
name|getShouldStop
parameter_list|()
block|{
return|return
name|shouldStop
operator|.
name|get
argument_list|()
return|;
block|}
specifier|public
name|void
name|setShouldStop
parameter_list|(
name|Boolean
name|shouldStop
parameter_list|)
block|{
name|this
operator|.
name|shouldStop
operator|.
name|set
argument_list|(
name|shouldStop
argument_list|)
expr_stmt|;
block|}
block|}
specifier|public
interface|interface
name|IMessageHandler
block|{
name|void
name|onMessage
parameter_list|(
name|Message
name|message
parameter_list|)
throws|throws
name|Exception
function_decl|;
block|}
block|}
end_class

end_unit

