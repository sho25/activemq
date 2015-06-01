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
name|fail
import|;
end_import

begin_import
import|import
name|java
operator|.
name|net
operator|.
name|URI
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
name|Iterator
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
name|advisory
operator|.
name|ConsumerEvent
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
name|advisory
operator|.
name|ConsumerEventSource
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
name|advisory
operator|.
name|ConsumerListener
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
name|BrokerFactory
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
name|transport
operator|.
name|http
operator|.
name|WaitForJettyListener
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
name|AMQ2764Test
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
name|AMQ2764Test
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
name|BrokerService
name|brokerOne
decl_stmt|;
specifier|private
name|BrokerService
name|brokerTwo
decl_stmt|;
specifier|private
name|Destination
name|destination
decl_stmt|;
specifier|private
specifier|final
name|ArrayList
argument_list|<
name|Connection
argument_list|>
name|connections
init|=
operator|new
name|ArrayList
argument_list|<
name|Connection
argument_list|>
argument_list|()
decl_stmt|;
annotation|@
name|Test
argument_list|(
name|timeout
operator|=
literal|60000
argument_list|)
specifier|public
name|void
name|testInactivityMonitor
parameter_list|()
throws|throws
name|Exception
block|{
name|startBrokerTwo
argument_list|()
expr_stmt|;
name|brokerTwo
operator|.
name|waitUntilStarted
argument_list|()
expr_stmt|;
name|startBrokerOne
argument_list|()
expr_stmt|;
name|brokerOne
operator|.
name|waitUntilStarted
argument_list|()
expr_stmt|;
name|ActiveMQConnectionFactory
name|secondProducerConnectionFactory
init|=
name|createBrokerTwoHttpConnectionFactory
argument_list|()
decl_stmt|;
name|ActiveMQConnectionFactory
name|consumerConnectionFactory
init|=
name|createBrokerOneHttpConnectionFactory
argument_list|()
decl_stmt|;
name|MessageConsumer
name|consumer
init|=
name|createConsumer
argument_list|(
name|consumerConnectionFactory
argument_list|)
decl_stmt|;
name|AtomicInteger
name|counter
init|=
name|createConsumerCounter
argument_list|(
name|consumerConnectionFactory
argument_list|)
decl_stmt|;
name|waitForConsumerToArrive
argument_list|(
name|counter
argument_list|)
expr_stmt|;
name|Connection
name|connection
init|=
name|secondProducerConnectionFactory
operator|.
name|createConnection
argument_list|()
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
name|setDeliveryMode
argument_list|(
name|DeliveryMode
operator|.
name|NON_PERSISTENT
argument_list|)
expr_stmt|;
specifier|final
name|int
name|expectedMessagesReceived
init|=
literal|1000
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
name|expectedMessagesReceived
condition|;
name|i
operator|++
control|)
block|{
name|Message
name|message
init|=
name|session
operator|.
name|createMessage
argument_list|()
decl_stmt|;
name|producer
operator|.
name|send
argument_list|(
name|message
argument_list|)
expr_stmt|;
if|if
condition|(
name|i
operator|%
literal|200
operator|==
literal|0
condition|)
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"sent message "
operator|+
name|i
argument_list|)
expr_stmt|;
block|}
block|}
for|for
control|(
name|int
name|i
init|=
literal|1
init|;
name|i
operator|<=
name|expectedMessagesReceived
condition|;
name|i
operator|++
control|)
block|{
name|Message
name|message
init|=
name|consumer
operator|.
name|receive
argument_list|(
literal|2000
argument_list|)
decl_stmt|;
if|if
condition|(
name|message
operator|==
literal|null
condition|)
block|{
name|fail
argument_list|(
literal|"Didn't receive a message"
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|i
operator|%
literal|200
operator|==
literal|0
condition|)
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"received message "
operator|+
name|i
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
name|testBrokerRestart
parameter_list|()
throws|throws
name|Exception
block|{
name|startBrokerTwo
argument_list|()
expr_stmt|;
name|brokerTwo
operator|.
name|waitUntilStarted
argument_list|()
expr_stmt|;
name|startBrokerOne
argument_list|()
expr_stmt|;
name|brokerOne
operator|.
name|waitUntilStarted
argument_list|()
expr_stmt|;
name|ActiveMQConnectionFactory
name|producerConnectionFactory
init|=
name|createBrokerOneConnectionFactory
argument_list|()
decl_stmt|;
name|ActiveMQConnectionFactory
name|secondProducerConnectionFactory
init|=
name|createBrokerTwoConnectionFactory
argument_list|()
decl_stmt|;
name|ActiveMQConnectionFactory
name|consumerConnectionFactory
init|=
name|createBrokerOneConnectionFactory
argument_list|()
decl_stmt|;
name|MessageConsumer
name|consumer
init|=
name|createConsumer
argument_list|(
name|consumerConnectionFactory
argument_list|)
decl_stmt|;
name|AtomicInteger
name|counter
init|=
name|createConsumerCounter
argument_list|(
name|consumerConnectionFactory
argument_list|)
decl_stmt|;
name|waitForConsumerToArrive
argument_list|(
name|counter
argument_list|)
expr_stmt|;
specifier|final
name|int
name|expectedMessagesReceived
init|=
literal|25
decl_stmt|;
name|int
name|actualMessagesReceived
init|=
name|doSendMessage
argument_list|(
name|expectedMessagesReceived
argument_list|,
name|consumer
argument_list|,
name|producerConnectionFactory
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|"Didn't receive the right amount of messages directly connected"
argument_list|,
name|expectedMessagesReceived
argument_list|,
name|actualMessagesReceived
argument_list|)
expr_stmt|;
name|assertNull
argument_list|(
literal|"Had extra messages"
argument_list|,
name|consumer
operator|.
name|receiveNoWait
argument_list|()
argument_list|)
expr_stmt|;
name|actualMessagesReceived
operator|=
name|doSendMessage
argument_list|(
name|expectedMessagesReceived
argument_list|,
name|consumer
argument_list|,
name|secondProducerConnectionFactory
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"Didn't receive the right amount of messages via network"
argument_list|,
name|expectedMessagesReceived
argument_list|,
name|actualMessagesReceived
argument_list|)
expr_stmt|;
name|assertNull
argument_list|(
literal|"Had extra messages"
argument_list|,
name|consumer
operator|.
name|receiveNoWait
argument_list|()
argument_list|)
expr_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"Stopping broker one"
argument_list|)
expr_stmt|;
name|stopBrokerOne
argument_list|()
expr_stmt|;
name|TimeUnit
operator|.
name|SECONDS
operator|.
name|sleep
argument_list|(
literal|1
argument_list|)
expr_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"Restarting broker"
argument_list|)
expr_stmt|;
name|startBrokerOne
argument_list|()
expr_stmt|;
name|consumer
operator|=
name|createConsumer
argument_list|(
name|consumerConnectionFactory
argument_list|)
expr_stmt|;
name|counter
operator|=
name|createConsumerCounter
argument_list|(
name|consumerConnectionFactory
argument_list|)
expr_stmt|;
name|waitForConsumerToArrive
argument_list|(
name|counter
argument_list|)
expr_stmt|;
name|actualMessagesReceived
operator|=
name|doSendMessage
argument_list|(
name|expectedMessagesReceived
argument_list|,
name|consumer
argument_list|,
name|secondProducerConnectionFactory
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"Didn't receive the right amount of messages via network after restart"
argument_list|,
name|expectedMessagesReceived
argument_list|,
name|actualMessagesReceived
argument_list|)
expr_stmt|;
name|assertNull
argument_list|(
literal|"Had extra messages"
argument_list|,
name|consumer
operator|.
name|receiveNoWait
argument_list|()
argument_list|)
expr_stmt|;
name|stopBrokerOne
argument_list|()
expr_stmt|;
name|stopBrokerTwo
argument_list|()
expr_stmt|;
block|}
specifier|protected
name|int
name|doSendMessage
parameter_list|(
name|int
name|expectedMessagesReceived
parameter_list|,
name|MessageConsumer
name|consumer
parameter_list|,
name|ActiveMQConnectionFactory
name|connectionFactory
parameter_list|)
throws|throws
name|Exception
block|{
name|int
name|messagesReceived
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
name|expectedMessagesReceived
condition|;
name|i
operator|++
control|)
block|{
name|sendMessage
argument_list|(
name|connectionFactory
argument_list|)
expr_stmt|;
name|Message
name|message
init|=
name|consumer
operator|.
name|receive
argument_list|(
literal|5000
argument_list|)
decl_stmt|;
if|if
condition|(
name|message
operator|!=
literal|null
condition|)
block|{
name|messagesReceived
operator|++
expr_stmt|;
block|}
block|}
return|return
name|messagesReceived
return|;
block|}
specifier|protected
name|String
name|sendMessage
parameter_list|(
name|ActiveMQConnectionFactory
name|connectionFactory
parameter_list|)
throws|throws
name|JMSException
block|{
name|Connection
name|connection
init|=
literal|null
decl_stmt|;
try|try
block|{
name|connection
operator|=
name|connectionFactory
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
name|Message
name|message
init|=
name|session
operator|.
name|createMessage
argument_list|()
decl_stmt|;
name|producer
operator|.
name|send
argument_list|(
name|message
argument_list|)
expr_stmt|;
return|return
name|message
operator|.
name|getJMSMessageID
argument_list|()
return|;
block|}
finally|finally
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
name|Throwable
name|ignore
parameter_list|)
block|{             }
block|}
block|}
specifier|protected
name|BrokerService
name|createFirstBroker
parameter_list|()
throws|throws
name|Exception
block|{
return|return
name|BrokerFactory
operator|.
name|createBroker
argument_list|(
operator|new
name|URI
argument_list|(
literal|"xbean:org/apache/activemq/bugs/amq2764/reconnect-broker1.xml"
argument_list|)
argument_list|)
return|;
block|}
specifier|protected
name|BrokerService
name|createSecondBroker
parameter_list|()
throws|throws
name|Exception
block|{
return|return
name|BrokerFactory
operator|.
name|createBroker
argument_list|(
operator|new
name|URI
argument_list|(
literal|"xbean:org/apache/activemq/bugs/amq2764/reconnect-broker2.xml"
argument_list|)
argument_list|)
return|;
block|}
specifier|protected
name|ActiveMQConnectionFactory
name|createBrokerOneConnectionFactory
parameter_list|()
block|{
return|return
operator|new
name|ActiveMQConnectionFactory
argument_list|(
literal|"vm://broker1?create=false"
argument_list|)
return|;
block|}
specifier|protected
name|ActiveMQConnectionFactory
name|createBrokerTwoConnectionFactory
parameter_list|()
block|{
return|return
operator|new
name|ActiveMQConnectionFactory
argument_list|(
literal|"vm://broker2?create=false"
argument_list|)
return|;
block|}
specifier|protected
name|ActiveMQConnectionFactory
name|createBrokerOneHttpConnectionFactory
parameter_list|()
block|{
return|return
operator|new
name|ActiveMQConnectionFactory
argument_list|(
literal|"http://localhost:61616"
argument_list|)
return|;
block|}
specifier|protected
name|ActiveMQConnectionFactory
name|createBrokerTwoHttpConnectionFactory
parameter_list|()
block|{
return|return
operator|new
name|ActiveMQConnectionFactory
argument_list|(
literal|"http://localhost:61617"
argument_list|)
return|;
block|}
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
name|info
argument_list|(
literal|"===== Starting test {} ================"
argument_list|,
name|getTestName
argument_list|()
argument_list|)
expr_stmt|;
name|destination
operator|=
operator|new
name|ActiveMQQueue
argument_list|(
literal|"RECONNECT.TEST.QUEUE"
argument_list|)
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
name|disposeConsumerConnections
argument_list|()
expr_stmt|;
name|Thread
operator|.
name|sleep
argument_list|(
literal|10
argument_list|)
expr_stmt|;
try|try
block|{
name|stopBrokerOne
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Throwable
name|e
parameter_list|)
block|{         }
try|try
block|{
name|stopBrokerTwo
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Throwable
name|e
parameter_list|)
block|{         }
name|LOG
operator|.
name|info
argument_list|(
literal|"===== Finished test {} ================"
argument_list|,
name|getTestName
argument_list|()
argument_list|)
expr_stmt|;
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
name|void
name|disposeConsumerConnections
parameter_list|()
block|{
for|for
control|(
name|Iterator
argument_list|<
name|Connection
argument_list|>
name|iter
init|=
name|connections
operator|.
name|iterator
argument_list|()
init|;
name|iter
operator|.
name|hasNext
argument_list|()
condition|;
control|)
block|{
name|Connection
name|connection
init|=
name|iter
operator|.
name|next
argument_list|()
decl_stmt|;
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
name|Throwable
name|ignore
parameter_list|)
block|{             }
block|}
block|}
specifier|protected
name|void
name|startBrokerOne
parameter_list|()
throws|throws
name|Exception
block|{
if|if
condition|(
name|brokerOne
operator|==
literal|null
condition|)
block|{
name|brokerOne
operator|=
name|createFirstBroker
argument_list|()
expr_stmt|;
name|brokerOne
operator|.
name|start
argument_list|()
expr_stmt|;
name|brokerOne
operator|.
name|waitUntilStarted
argument_list|()
expr_stmt|;
name|WaitForJettyListener
operator|.
name|waitForJettySocketToAccept
argument_list|(
literal|"http://localhost:61616"
argument_list|)
expr_stmt|;
block|}
block|}
specifier|protected
name|void
name|stopBrokerOne
parameter_list|()
throws|throws
name|Exception
block|{
if|if
condition|(
name|brokerOne
operator|!=
literal|null
condition|)
block|{
name|brokerOne
operator|.
name|stop
argument_list|()
expr_stmt|;
name|brokerOne
operator|=
literal|null
expr_stmt|;
block|}
block|}
specifier|protected
name|void
name|startBrokerTwo
parameter_list|()
throws|throws
name|Exception
block|{
if|if
condition|(
name|brokerTwo
operator|==
literal|null
condition|)
block|{
name|brokerTwo
operator|=
name|createSecondBroker
argument_list|()
expr_stmt|;
name|brokerTwo
operator|.
name|start
argument_list|()
expr_stmt|;
name|brokerTwo
operator|.
name|waitUntilStarted
argument_list|()
expr_stmt|;
name|WaitForJettyListener
operator|.
name|waitForJettySocketToAccept
argument_list|(
literal|"http://localhost:61617"
argument_list|)
expr_stmt|;
block|}
block|}
specifier|protected
name|void
name|stopBrokerTwo
parameter_list|()
throws|throws
name|Exception
block|{
if|if
condition|(
name|brokerTwo
operator|!=
literal|null
condition|)
block|{
name|brokerTwo
operator|.
name|stop
argument_list|()
expr_stmt|;
name|brokerTwo
operator|=
literal|null
expr_stmt|;
block|}
block|}
specifier|protected
name|MessageConsumer
name|createConsumer
parameter_list|(
name|ActiveMQConnectionFactory
name|consumerConnectionFactory
parameter_list|)
throws|throws
name|JMSException
block|{
name|Connection
name|connection
init|=
name|consumerConnectionFactory
operator|.
name|createConnection
argument_list|()
decl_stmt|;
name|connections
operator|.
name|add
argument_list|(
name|connection
argument_list|)
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
literal|false
argument_list|,
name|Session
operator|.
name|AUTO_ACKNOWLEDGE
argument_list|)
decl_stmt|;
return|return
name|session
operator|.
name|createConsumer
argument_list|(
name|destination
argument_list|)
return|;
block|}
specifier|protected
name|AtomicInteger
name|createConsumerCounter
parameter_list|(
name|ActiveMQConnectionFactory
name|cf
parameter_list|)
throws|throws
name|Exception
block|{
specifier|final
name|AtomicInteger
name|rc
init|=
operator|new
name|AtomicInteger
argument_list|(
literal|0
argument_list|)
decl_stmt|;
name|Connection
name|connection
init|=
name|cf
operator|.
name|createConnection
argument_list|()
decl_stmt|;
name|connections
operator|.
name|add
argument_list|(
name|connection
argument_list|)
expr_stmt|;
name|connection
operator|.
name|start
argument_list|()
expr_stmt|;
name|ConsumerEventSource
name|source
init|=
operator|new
name|ConsumerEventSource
argument_list|(
name|connection
argument_list|,
name|destination
argument_list|)
decl_stmt|;
name|source
operator|.
name|setConsumerListener
argument_list|(
operator|new
name|ConsumerListener
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|void
name|onConsumerEvent
parameter_list|(
name|ConsumerEvent
name|event
parameter_list|)
block|{
name|rc
operator|.
name|set
argument_list|(
name|event
operator|.
name|getConsumerCount
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
argument_list|)
expr_stmt|;
name|source
operator|.
name|start
argument_list|()
expr_stmt|;
return|return
name|rc
return|;
block|}
specifier|protected
name|void
name|waitForConsumerToArrive
parameter_list|(
name|AtomicInteger
name|consumerCounter
parameter_list|)
throws|throws
name|InterruptedException
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
literal|200
condition|;
name|i
operator|++
control|)
block|{
if|if
condition|(
name|consumerCounter
operator|.
name|get
argument_list|()
operator|>
literal|0
condition|)
block|{
return|return;
block|}
name|Thread
operator|.
name|sleep
argument_list|(
literal|50
argument_list|)
expr_stmt|;
block|}
name|fail
argument_list|(
literal|"The consumer did not arrive."
argument_list|)
expr_stmt|;
block|}
specifier|protected
name|void
name|waitForConsumerToLeave
parameter_list|(
name|AtomicInteger
name|consumerCounter
parameter_list|)
throws|throws
name|InterruptedException
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
literal|200
condition|;
name|i
operator|++
control|)
block|{
if|if
condition|(
name|consumerCounter
operator|.
name|get
argument_list|()
operator|==
literal|0
condition|)
block|{
return|return;
block|}
name|Thread
operator|.
name|sleep
argument_list|(
literal|50
argument_list|)
expr_stmt|;
block|}
name|fail
argument_list|(
literal|"The consumer did not leave."
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

