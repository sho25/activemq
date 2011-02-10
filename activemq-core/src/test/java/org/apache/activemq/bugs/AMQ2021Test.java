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
name|java
operator|.
name|lang
operator|.
name|Thread
operator|.
name|UncaughtExceptionHandler
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
name|Vector
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
name|ExceptionListener
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
name|junit
operator|.
name|framework
operator|.
name|TestCase
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
name|command
operator|.
name|ActiveMQQueue
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
name|ActiveMQTopic
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
comment|/**  * This is a test case for the issue reported at:  * https://issues.apache.org/activemq/browse/AMQ-2021   * Bug is modification of inflight message properties so the failure can manifest itself in a bunch  * or ways, from message receipt with null properties to marshall errors  */
end_comment

begin_class
specifier|public
class|class
name|AMQ2021Test
extends|extends
name|TestCase
implements|implements
name|ExceptionListener
implements|,
name|UncaughtExceptionHandler
block|{
specifier|private
specifier|static
specifier|final
name|Logger
name|log
init|=
name|LoggerFactory
operator|.
name|getLogger
argument_list|(
name|AMQ2021Test
operator|.
name|class
argument_list|)
decl_stmt|;
name|BrokerService
name|brokerService
decl_stmt|;
name|ArrayList
argument_list|<
name|Thread
argument_list|>
name|threads
init|=
operator|new
name|ArrayList
argument_list|<
name|Thread
argument_list|>
argument_list|()
decl_stmt|;
name|Vector
argument_list|<
name|Throwable
argument_list|>
name|exceptions
decl_stmt|;
name|AMQ2021Test
name|testCase
decl_stmt|;
name|String
name|ACTIVEMQ_BROKER_BIND
init|=
literal|"tcp://localhost:61616"
decl_stmt|;
name|String
name|ACTIVEMQ_BROKER_URL
init|=
name|ACTIVEMQ_BROKER_BIND
operator|+
literal|"?jms.redeliveryPolicy.maximumRedeliveries=1&jms.redeliveryPolicy.initialRedeliveryDelay=0"
decl_stmt|;
specifier|private
name|int
name|numMessages
init|=
literal|1000
decl_stmt|;
specifier|private
name|int
name|numConsumers
init|=
literal|2
decl_stmt|;
specifier|private
name|int
name|dlqMessages
init|=
name|numMessages
operator|/
literal|2
decl_stmt|;
name|CountDownLatch
name|receivedLatch
decl_stmt|;
specifier|private
name|ActiveMQTopic
name|destination
decl_stmt|;
specifier|public
name|CountDownLatch
name|started
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
name|Thread
operator|.
name|setDefaultUncaughtExceptionHandler
argument_list|(
name|this
argument_list|)
expr_stmt|;
name|testCase
operator|=
name|this
expr_stmt|;
comment|// Start an embedded broker up.
name|brokerService
operator|=
operator|new
name|BrokerService
argument_list|()
expr_stmt|;
name|brokerService
operator|.
name|setDeleteAllMessagesOnStartup
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|brokerService
operator|.
name|addConnector
argument_list|(
name|ACTIVEMQ_BROKER_BIND
argument_list|)
expr_stmt|;
name|brokerService
operator|.
name|start
argument_list|()
expr_stmt|;
name|destination
operator|=
operator|new
name|ActiveMQTopic
argument_list|(
name|getName
argument_list|()
argument_list|)
expr_stmt|;
name|exceptions
operator|=
operator|new
name|Vector
argument_list|<
name|Throwable
argument_list|>
argument_list|()
expr_stmt|;
name|receivedLatch
operator|=
operator|new
name|CountDownLatch
argument_list|(
name|numConsumers
operator|*
operator|(
name|numMessages
operator|+
name|dlqMessages
operator|)
argument_list|)
expr_stmt|;
name|started
operator|=
operator|new
name|CountDownLatch
argument_list|(
literal|1
argument_list|)
expr_stmt|;
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
for|for
control|(
name|Thread
name|t
range|:
name|threads
control|)
block|{
name|t
operator|.
name|interrupt
argument_list|()
expr_stmt|;
name|t
operator|.
name|join
argument_list|()
expr_stmt|;
block|}
name|brokerService
operator|.
name|stop
argument_list|()
expr_stmt|;
block|}
specifier|public
name|void
name|testConcurrentTopicResendToDLQ
parameter_list|()
throws|throws
name|Exception
block|{
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|numConsumers
condition|;
name|i
operator|++
control|)
block|{
name|ConsumerThread
name|c1
init|=
operator|new
name|ConsumerThread
argument_list|(
literal|"Consumer-"
operator|+
name|i
argument_list|)
decl_stmt|;
name|threads
operator|.
name|add
argument_list|(
name|c1
argument_list|)
expr_stmt|;
name|c1
operator|.
name|start
argument_list|()
expr_stmt|;
block|}
name|assertTrue
argument_list|(
name|started
operator|.
name|await
argument_list|(
literal|10
argument_list|,
name|TimeUnit
operator|.
name|SECONDS
argument_list|)
argument_list|)
expr_stmt|;
name|Thread
name|producer
init|=
operator|new
name|Thread
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|void
name|run
parameter_list|()
block|{
try|try
block|{
name|produce
argument_list|(
name|numMessages
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{                 }
block|}
block|}
decl_stmt|;
name|threads
operator|.
name|add
argument_list|(
name|producer
argument_list|)
expr_stmt|;
name|producer
operator|.
name|start
argument_list|()
expr_stmt|;
name|boolean
name|allGood
init|=
name|receivedLatch
operator|.
name|await
argument_list|(
literal|30
argument_list|,
name|TimeUnit
operator|.
name|SECONDS
argument_list|)
decl_stmt|;
for|for
control|(
name|Throwable
name|t
range|:
name|exceptions
control|)
block|{
name|log
operator|.
name|error
argument_list|(
literal|"failing test with first exception"
argument_list|,
name|t
argument_list|)
expr_stmt|;
name|fail
argument_list|(
literal|"exception during test : "
operator|+
name|t
argument_list|)
expr_stmt|;
block|}
name|assertTrue
argument_list|(
literal|"excepted messages received within time limit"
argument_list|,
name|allGood
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|exceptions
operator|.
name|size
argument_list|()
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
name|numConsumers
condition|;
name|i
operator|++
control|)
block|{
comment|// last recovery sends message to deq so is not received again
name|assertEquals
argument_list|(
name|dlqMessages
operator|*
literal|2
argument_list|,
operator|(
operator|(
name|ConsumerThread
operator|)
name|threads
operator|.
name|get
argument_list|(
name|i
argument_list|)
operator|)
operator|.
name|recoveries
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|numMessages
operator|+
name|dlqMessages
argument_list|,
operator|(
operator|(
name|ConsumerThread
operator|)
name|threads
operator|.
name|get
argument_list|(
name|i
argument_list|)
operator|)
operator|.
name|counter
argument_list|)
expr_stmt|;
block|}
comment|// half of the messages for each consumer should go to the dlq but duplicates will
comment|// be suppressed
name|consumeFromDLQ
argument_list|(
name|dlqMessages
argument_list|)
expr_stmt|;
block|}
specifier|private
name|void
name|consumeFromDLQ
parameter_list|(
name|int
name|messageCount
parameter_list|)
throws|throws
name|Exception
block|{
name|ActiveMQConnectionFactory
name|connectionFactory
init|=
operator|new
name|ActiveMQConnectionFactory
argument_list|(
name|ACTIVEMQ_BROKER_URL
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
name|MessageConsumer
name|dlqConsumer
init|=
name|session
operator|.
name|createConsumer
argument_list|(
operator|new
name|ActiveMQQueue
argument_list|(
literal|"ActiveMQ.DLQ"
argument_list|)
argument_list|)
decl_stmt|;
name|int
name|count
init|=
literal|0
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|messageCount
condition|;
name|i
operator|++
control|)
block|{
if|if
condition|(
name|dlqConsumer
operator|.
name|receive
argument_list|(
literal|1000
argument_list|)
operator|==
literal|null
condition|)
block|{
break|break;
block|}
name|count
operator|++
expr_stmt|;
block|}
name|assertEquals
argument_list|(
name|messageCount
argument_list|,
name|count
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|produce
parameter_list|(
name|int
name|count
parameter_list|)
throws|throws
name|Exception
block|{
name|Connection
name|connection
init|=
literal|null
decl_stmt|;
try|try
block|{
name|ActiveMQConnectionFactory
name|factory
init|=
operator|new
name|ActiveMQConnectionFactory
argument_list|(
name|ACTIVEMQ_BROKER_BIND
argument_list|)
decl_stmt|;
name|connection
operator|=
name|factory
operator|.
name|createConnection
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
name|producer
operator|.
name|setTimeToLive
argument_list|(
literal|0
argument_list|)
expr_stmt|;
name|connection
operator|.
name|start
argument_list|()
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
name|count
condition|;
name|i
operator|++
control|)
block|{
name|int
name|id
init|=
name|i
operator|+
literal|1
decl_stmt|;
name|TextMessage
name|message
init|=
name|session
operator|.
name|createTextMessage
argument_list|(
name|getName
argument_list|()
operator|+
literal|" Message "
operator|+
name|id
argument_list|)
decl_stmt|;
name|message
operator|.
name|setIntProperty
argument_list|(
literal|"MsgNumber"
argument_list|,
name|id
argument_list|)
expr_stmt|;
name|producer
operator|.
name|send
argument_list|(
name|message
argument_list|)
expr_stmt|;
if|if
condition|(
name|id
operator|%
literal|500
operator|==
literal|0
condition|)
block|{
name|log
operator|.
name|info
argument_list|(
literal|"sent "
operator|+
name|id
operator|+
literal|", ith "
operator|+
name|message
argument_list|)
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
name|log
operator|.
name|error
argument_list|(
literal|"unexpected ex on produce"
argument_list|,
name|e
argument_list|)
expr_stmt|;
name|exceptions
operator|.
name|add
argument_list|(
name|e
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
try|try
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
block|}
block|}
catch|catch
parameter_list|(
name|Throwable
name|e
parameter_list|)
block|{             }
block|}
block|}
specifier|public
class|class
name|ConsumerThread
extends|extends
name|Thread
implements|implements
name|MessageListener
block|{
specifier|public
name|long
name|counter
init|=
literal|0
decl_stmt|;
specifier|public
name|long
name|recoveries
init|=
literal|0
decl_stmt|;
specifier|private
name|Session
name|session
decl_stmt|;
specifier|public
name|ConsumerThread
parameter_list|(
name|String
name|threadId
parameter_list|)
block|{
name|super
argument_list|(
name|threadId
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|run
parameter_list|()
block|{
try|try
block|{
name|ActiveMQConnectionFactory
name|connectionFactory
init|=
operator|new
name|ActiveMQConnectionFactory
argument_list|(
name|ACTIVEMQ_BROKER_URL
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
name|setExceptionListener
argument_list|(
name|testCase
argument_list|)
expr_stmt|;
name|connection
operator|.
name|setClientID
argument_list|(
name|getName
argument_list|()
argument_list|)
expr_stmt|;
name|session
operator|=
name|connection
operator|.
name|createSession
argument_list|(
literal|false
argument_list|,
name|Session
operator|.
name|CLIENT_ACKNOWLEDGE
argument_list|)
expr_stmt|;
name|MessageConsumer
name|consumer
init|=
name|session
operator|.
name|createDurableSubscriber
argument_list|(
name|destination
argument_list|,
name|getName
argument_list|()
argument_list|)
decl_stmt|;
name|consumer
operator|.
name|setMessageListener
argument_list|(
name|this
argument_list|)
expr_stmt|;
name|connection
operator|.
name|start
argument_list|()
expr_stmt|;
name|started
operator|.
name|countDown
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|JMSException
name|exception
parameter_list|)
block|{
name|log
operator|.
name|error
argument_list|(
literal|"unexpected ex in consumer run"
argument_list|,
name|exception
argument_list|)
expr_stmt|;
name|exceptions
operator|.
name|add
argument_list|(
name|exception
argument_list|)
expr_stmt|;
block|}
block|}
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
name|counter
operator|++
expr_stmt|;
name|int
name|messageNumber
init|=
name|message
operator|.
name|getIntProperty
argument_list|(
literal|"MsgNumber"
argument_list|)
decl_stmt|;
if|if
condition|(
name|messageNumber
operator|%
literal|2
operator|==
literal|0
condition|)
block|{
name|session
operator|.
name|recover
argument_list|()
expr_stmt|;
name|recoveries
operator|++
expr_stmt|;
block|}
else|else
block|{
name|message
operator|.
name|acknowledge
argument_list|()
expr_stmt|;
block|}
if|if
condition|(
name|counter
operator|%
literal|200
operator|==
literal|0
condition|)
block|{
name|log
operator|.
name|info
argument_list|(
literal|"recoveries:"
operator|+
name|recoveries
operator|+
literal|", Received "
operator|+
name|counter
operator|+
literal|", counter'th "
operator|+
name|message
argument_list|)
expr_stmt|;
block|}
name|receivedLatch
operator|.
name|countDown
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
name|log
operator|.
name|error
argument_list|(
literal|"unexpected ex on onMessage"
argument_list|,
name|e
argument_list|)
expr_stmt|;
name|exceptions
operator|.
name|add
argument_list|(
name|e
argument_list|)
expr_stmt|;
block|}
block|}
block|}
specifier|public
name|void
name|onException
parameter_list|(
name|JMSException
name|exception
parameter_list|)
block|{
name|log
operator|.
name|info
argument_list|(
literal|"Unexpected JMSException"
argument_list|,
name|exception
argument_list|)
expr_stmt|;
name|exceptions
operator|.
name|add
argument_list|(
name|exception
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|uncaughtException
parameter_list|(
name|Thread
name|thread
parameter_list|,
name|Throwable
name|exception
parameter_list|)
block|{
name|log
operator|.
name|info
argument_list|(
literal|"Unexpected exception from thread "
operator|+
name|thread
operator|+
literal|", ex: "
operator|+
name|exception
argument_list|)
expr_stmt|;
name|exceptions
operator|.
name|add
argument_list|(
name|exception
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

