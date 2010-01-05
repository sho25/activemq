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
name|io
operator|.
name|IOException
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
name|AtomicBoolean
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
name|activemq
operator|.
name|broker
operator|.
name|TransportConnector
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
name|region
operator|.
name|policy
operator|.
name|PolicyEntry
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
name|region
operator|.
name|policy
operator|.
name|PolicyMap
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
name|region
operator|.
name|policy
operator|.
name|VMPendingQueueMessageStoragePolicy
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
name|region
operator|.
name|policy
operator|.
name|VMPendingSubscriberMessageStoragePolicy
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
name|tcp
operator|.
name|TcpTransport
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
name|ProducerFlowControlTest
extends|extends
name|JmsTestSupport
block|{
specifier|static
specifier|final
name|Log
name|LOG
init|=
name|LogFactory
operator|.
name|getLog
argument_list|(
name|ProducerFlowControlTest
operator|.
name|class
argument_list|)
decl_stmt|;
name|ActiveMQQueue
name|queueA
init|=
operator|new
name|ActiveMQQueue
argument_list|(
literal|"QUEUE.A"
argument_list|)
decl_stmt|;
name|ActiveMQQueue
name|queueB
init|=
operator|new
name|ActiveMQQueue
argument_list|(
literal|"QUEUE.B"
argument_list|)
decl_stmt|;
specifier|protected
name|TransportConnector
name|connector
decl_stmt|;
specifier|protected
name|ActiveMQConnection
name|connection
decl_stmt|;
comment|// used to test sendFailIfNoSpace on SystemUsage
specifier|protected
specifier|final
name|AtomicBoolean
name|gotResourceException
init|=
operator|new
name|AtomicBoolean
argument_list|(
literal|false
argument_list|)
decl_stmt|;
specifier|public
name|void
name|test2ndPubisherWithProducerWindowSendConnectionThatIsBlocked
parameter_list|()
throws|throws
name|Exception
block|{
name|ActiveMQConnectionFactory
name|factory
init|=
operator|(
name|ActiveMQConnectionFactory
operator|)
name|createConnectionFactory
argument_list|()
decl_stmt|;
name|factory
operator|.
name|setProducerWindowSize
argument_list|(
literal|1024
operator|*
literal|64
argument_list|)
expr_stmt|;
name|connection
operator|=
operator|(
name|ActiveMQConnection
operator|)
name|factory
operator|.
name|createConnection
argument_list|()
expr_stmt|;
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
name|CLIENT_ACKNOWLEDGE
argument_list|)
decl_stmt|;
name|MessageConsumer
name|consumer
init|=
name|session
operator|.
name|createConsumer
argument_list|(
name|queueB
argument_list|)
decl_stmt|;
comment|// Test sending to Queue A
comment|// 1 few sends should not block until the producer window is used up.
name|fillQueue
argument_list|(
name|queueA
argument_list|)
expr_stmt|;
comment|// Test sending to Queue B it should not block since the connection
comment|// should not be blocked.
name|CountDownLatch
name|pubishDoneToQeueuB
init|=
name|asyncSendTo
argument_list|(
name|queueB
argument_list|,
literal|"Message 1"
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
name|pubishDoneToQeueuB
operator|.
name|await
argument_list|(
literal|2
argument_list|,
name|TimeUnit
operator|.
name|SECONDS
argument_list|)
argument_list|)
expr_stmt|;
name|TextMessage
name|msg
init|=
operator|(
name|TextMessage
operator|)
name|consumer
operator|.
name|receive
argument_list|()
decl_stmt|;
name|assertEquals
argument_list|(
literal|"Message 1"
argument_list|,
name|msg
operator|.
name|getText
argument_list|()
argument_list|)
expr_stmt|;
name|msg
operator|.
name|acknowledge
argument_list|()
expr_stmt|;
name|pubishDoneToQeueuB
operator|=
name|asyncSendTo
argument_list|(
name|queueB
argument_list|,
literal|"Message 2"
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|pubishDoneToQeueuB
operator|.
name|await
argument_list|(
literal|2
argument_list|,
name|TimeUnit
operator|.
name|SECONDS
argument_list|)
argument_list|)
expr_stmt|;
name|msg
operator|=
operator|(
name|TextMessage
operator|)
name|consumer
operator|.
name|receive
argument_list|()
expr_stmt|;
name|assertEquals
argument_list|(
literal|"Message 2"
argument_list|,
name|msg
operator|.
name|getText
argument_list|()
argument_list|)
expr_stmt|;
name|msg
operator|.
name|acknowledge
argument_list|()
expr_stmt|;
block|}
specifier|public
name|void
name|testPubisherRecoverAfterBlock
parameter_list|()
throws|throws
name|Exception
block|{
name|ActiveMQConnectionFactory
name|factory
init|=
operator|(
name|ActiveMQConnectionFactory
operator|)
name|createConnectionFactory
argument_list|()
decl_stmt|;
name|connection
operator|=
operator|(
name|ActiveMQConnection
operator|)
name|factory
operator|.
name|createConnection
argument_list|()
expr_stmt|;
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
specifier|final
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
name|CLIENT_ACKNOWLEDGE
argument_list|)
decl_stmt|;
specifier|final
name|MessageProducer
name|producer
init|=
name|session
operator|.
name|createProducer
argument_list|(
name|queueA
argument_list|)
decl_stmt|;
specifier|final
name|AtomicBoolean
name|done
init|=
operator|new
name|AtomicBoolean
argument_list|(
literal|true
argument_list|)
decl_stmt|;
specifier|final
name|AtomicBoolean
name|keepGoing
init|=
operator|new
name|AtomicBoolean
argument_list|(
literal|true
argument_list|)
decl_stmt|;
name|Thread
name|thread
init|=
operator|new
name|Thread
argument_list|(
literal|"Filler"
argument_list|)
block|{
name|int
name|i
decl_stmt|;
annotation|@
name|Override
specifier|public
name|void
name|run
parameter_list|()
block|{
while|while
condition|(
name|keepGoing
operator|.
name|get
argument_list|()
condition|)
block|{
name|done
operator|.
name|set
argument_list|(
literal|false
argument_list|)
expr_stmt|;
try|try
block|{
name|producer
operator|.
name|send
argument_list|(
name|session
operator|.
name|createTextMessage
argument_list|(
literal|"Test message "
operator|+
operator|++
name|i
argument_list|)
argument_list|)
expr_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"sent: "
operator|+
name|i
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|JMSException
name|e
parameter_list|)
block|{ 					}
block|}
block|}
block|}
decl_stmt|;
name|thread
operator|.
name|start
argument_list|()
expr_stmt|;
name|waitForBlockedOrResourceLimit
argument_list|(
name|done
argument_list|)
expr_stmt|;
comment|// after receiveing messges, producer should continue sending messages
comment|// (done == false)
name|MessageConsumer
name|consumer
init|=
name|session
operator|.
name|createConsumer
argument_list|(
name|queueA
argument_list|)
decl_stmt|;
name|TextMessage
name|msg
decl_stmt|;
for|for
control|(
name|int
name|idx
init|=
literal|0
init|;
name|idx
operator|<
literal|5
condition|;
operator|++
name|idx
control|)
block|{
name|msg
operator|=
operator|(
name|TextMessage
operator|)
name|consumer
operator|.
name|receive
argument_list|(
literal|1000
argument_list|)
expr_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"received: "
operator|+
name|idx
operator|+
literal|", msg: "
operator|+
name|msg
operator|.
name|getJMSMessageID
argument_list|()
argument_list|)
expr_stmt|;
name|msg
operator|.
name|acknowledge
argument_list|()
expr_stmt|;
block|}
name|Thread
operator|.
name|sleep
argument_list|(
literal|1000
argument_list|)
expr_stmt|;
name|keepGoing
operator|.
name|set
argument_list|(
literal|false
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
literal|"producer has resumed"
argument_list|,
name|done
operator|.
name|get
argument_list|()
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|testAsyncPubisherRecoverAfterBlock
parameter_list|()
throws|throws
name|Exception
block|{
name|ActiveMQConnectionFactory
name|factory
init|=
operator|(
name|ActiveMQConnectionFactory
operator|)
name|createConnectionFactory
argument_list|()
decl_stmt|;
name|factory
operator|.
name|setProducerWindowSize
argument_list|(
literal|1024
operator|*
literal|5
argument_list|)
expr_stmt|;
name|factory
operator|.
name|setUseAsyncSend
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|connection
operator|=
operator|(
name|ActiveMQConnection
operator|)
name|factory
operator|.
name|createConnection
argument_list|()
expr_stmt|;
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
specifier|final
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
name|CLIENT_ACKNOWLEDGE
argument_list|)
decl_stmt|;
specifier|final
name|MessageProducer
name|producer
init|=
name|session
operator|.
name|createProducer
argument_list|(
name|queueA
argument_list|)
decl_stmt|;
specifier|final
name|AtomicBoolean
name|done
init|=
operator|new
name|AtomicBoolean
argument_list|(
literal|true
argument_list|)
decl_stmt|;
specifier|final
name|AtomicBoolean
name|keepGoing
init|=
operator|new
name|AtomicBoolean
argument_list|(
literal|true
argument_list|)
decl_stmt|;
name|Thread
name|thread
init|=
operator|new
name|Thread
argument_list|(
literal|"Filler"
argument_list|)
block|{
name|int
name|i
decl_stmt|;
annotation|@
name|Override
specifier|public
name|void
name|run
parameter_list|()
block|{
while|while
condition|(
name|keepGoing
operator|.
name|get
argument_list|()
condition|)
block|{
name|done
operator|.
name|set
argument_list|(
literal|false
argument_list|)
expr_stmt|;
try|try
block|{
name|producer
operator|.
name|send
argument_list|(
name|session
operator|.
name|createTextMessage
argument_list|(
literal|"Test message "
operator|+
operator|++
name|i
argument_list|)
argument_list|)
expr_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"sent: "
operator|+
name|i
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|JMSException
name|e
parameter_list|)
block|{                     }
block|}
block|}
block|}
decl_stmt|;
name|thread
operator|.
name|start
argument_list|()
expr_stmt|;
name|waitForBlockedOrResourceLimit
argument_list|(
name|done
argument_list|)
expr_stmt|;
comment|// after receiveing messges, producer should continue sending messages
comment|// (done == false)
name|MessageConsumer
name|consumer
init|=
name|session
operator|.
name|createConsumer
argument_list|(
name|queueA
argument_list|)
decl_stmt|;
name|TextMessage
name|msg
decl_stmt|;
for|for
control|(
name|int
name|idx
init|=
literal|0
init|;
name|idx
operator|<
literal|5
condition|;
operator|++
name|idx
control|)
block|{
name|msg
operator|=
operator|(
name|TextMessage
operator|)
name|consumer
operator|.
name|receive
argument_list|(
literal|1000
argument_list|)
expr_stmt|;
name|assertNotNull
argument_list|(
literal|"Got a message"
argument_list|,
name|msg
argument_list|)
expr_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"received: "
operator|+
name|idx
operator|+
literal|", msg: "
operator|+
name|msg
operator|.
name|getJMSMessageID
argument_list|()
argument_list|)
expr_stmt|;
name|msg
operator|.
name|acknowledge
argument_list|()
expr_stmt|;
block|}
name|Thread
operator|.
name|sleep
argument_list|(
literal|1000
argument_list|)
expr_stmt|;
name|keepGoing
operator|.
name|set
argument_list|(
literal|false
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
literal|"producer has resumed"
argument_list|,
name|done
operator|.
name|get
argument_list|()
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|test2ndPubisherWithSyncSendConnectionThatIsBlocked
parameter_list|()
throws|throws
name|Exception
block|{
name|ActiveMQConnectionFactory
name|factory
init|=
operator|(
name|ActiveMQConnectionFactory
operator|)
name|createConnectionFactory
argument_list|()
decl_stmt|;
name|factory
operator|.
name|setAlwaysSyncSend
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|connection
operator|=
operator|(
name|ActiveMQConnection
operator|)
name|factory
operator|.
name|createConnection
argument_list|()
expr_stmt|;
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
name|CLIENT_ACKNOWLEDGE
argument_list|)
decl_stmt|;
name|MessageConsumer
name|consumer
init|=
name|session
operator|.
name|createConsumer
argument_list|(
name|queueB
argument_list|)
decl_stmt|;
comment|// Test sending to Queue A
comment|// 1st send should not block. But the rest will.
name|fillQueue
argument_list|(
name|queueA
argument_list|)
expr_stmt|;
comment|// Test sending to Queue B it should not block.
name|CountDownLatch
name|pubishDoneToQeueuB
init|=
name|asyncSendTo
argument_list|(
name|queueB
argument_list|,
literal|"Message 1"
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
name|pubishDoneToQeueuB
operator|.
name|await
argument_list|(
literal|2
argument_list|,
name|TimeUnit
operator|.
name|SECONDS
argument_list|)
argument_list|)
expr_stmt|;
name|TextMessage
name|msg
init|=
operator|(
name|TextMessage
operator|)
name|consumer
operator|.
name|receive
argument_list|()
decl_stmt|;
name|assertEquals
argument_list|(
literal|"Message 1"
argument_list|,
name|msg
operator|.
name|getText
argument_list|()
argument_list|)
expr_stmt|;
name|msg
operator|.
name|acknowledge
argument_list|()
expr_stmt|;
name|pubishDoneToQeueuB
operator|=
name|asyncSendTo
argument_list|(
name|queueB
argument_list|,
literal|"Message 2"
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|pubishDoneToQeueuB
operator|.
name|await
argument_list|(
literal|2
argument_list|,
name|TimeUnit
operator|.
name|SECONDS
argument_list|)
argument_list|)
expr_stmt|;
name|msg
operator|=
operator|(
name|TextMessage
operator|)
name|consumer
operator|.
name|receive
argument_list|()
expr_stmt|;
name|assertEquals
argument_list|(
literal|"Message 2"
argument_list|,
name|msg
operator|.
name|getText
argument_list|()
argument_list|)
expr_stmt|;
name|msg
operator|.
name|acknowledge
argument_list|()
expr_stmt|;
block|}
specifier|public
name|void
name|testSimpleSendReceive
parameter_list|()
throws|throws
name|Exception
block|{
name|ActiveMQConnectionFactory
name|factory
init|=
operator|(
name|ActiveMQConnectionFactory
operator|)
name|createConnectionFactory
argument_list|()
decl_stmt|;
name|factory
operator|.
name|setAlwaysSyncSend
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|connection
operator|=
operator|(
name|ActiveMQConnection
operator|)
name|factory
operator|.
name|createConnection
argument_list|()
expr_stmt|;
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
name|CLIENT_ACKNOWLEDGE
argument_list|)
decl_stmt|;
name|MessageConsumer
name|consumer
init|=
name|session
operator|.
name|createConsumer
argument_list|(
name|queueA
argument_list|)
decl_stmt|;
comment|// Test sending to Queue B it should not block.
name|CountDownLatch
name|pubishDoneToQeueuA
init|=
name|asyncSendTo
argument_list|(
name|queueA
argument_list|,
literal|"Message 1"
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
name|pubishDoneToQeueuA
operator|.
name|await
argument_list|(
literal|2
argument_list|,
name|TimeUnit
operator|.
name|SECONDS
argument_list|)
argument_list|)
expr_stmt|;
name|TextMessage
name|msg
init|=
operator|(
name|TextMessage
operator|)
name|consumer
operator|.
name|receive
argument_list|()
decl_stmt|;
name|assertEquals
argument_list|(
literal|"Message 1"
argument_list|,
name|msg
operator|.
name|getText
argument_list|()
argument_list|)
expr_stmt|;
name|msg
operator|.
name|acknowledge
argument_list|()
expr_stmt|;
name|pubishDoneToQeueuA
operator|=
name|asyncSendTo
argument_list|(
name|queueA
argument_list|,
literal|"Message 2"
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|pubishDoneToQeueuA
operator|.
name|await
argument_list|(
literal|2
argument_list|,
name|TimeUnit
operator|.
name|SECONDS
argument_list|)
argument_list|)
expr_stmt|;
name|msg
operator|=
operator|(
name|TextMessage
operator|)
name|consumer
operator|.
name|receive
argument_list|()
expr_stmt|;
name|assertEquals
argument_list|(
literal|"Message 2"
argument_list|,
name|msg
operator|.
name|getText
argument_list|()
argument_list|)
expr_stmt|;
name|msg
operator|.
name|acknowledge
argument_list|()
expr_stmt|;
block|}
specifier|public
name|void
name|test2ndPubisherWithStandardConnectionThatIsBlocked
parameter_list|()
throws|throws
name|Exception
block|{
name|ConnectionFactory
name|factory
init|=
name|createConnectionFactory
argument_list|()
decl_stmt|;
name|connection
operator|=
operator|(
name|ActiveMQConnection
operator|)
name|factory
operator|.
name|createConnection
argument_list|()
expr_stmt|;
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
comment|// Test sending to Queue A
comment|// 1st send should not block.
name|fillQueue
argument_list|(
name|queueA
argument_list|)
expr_stmt|;
comment|// Test sending to Queue B it should block.
comment|// Since even though the it's queue limits have not been reached, the
comment|// connection
comment|// is blocked.
name|CountDownLatch
name|pubishDoneToQeueuB
init|=
name|asyncSendTo
argument_list|(
name|queueB
argument_list|,
literal|"Message 1"
argument_list|)
decl_stmt|;
name|assertFalse
argument_list|(
name|pubishDoneToQeueuB
operator|.
name|await
argument_list|(
literal|2
argument_list|,
name|TimeUnit
operator|.
name|SECONDS
argument_list|)
argument_list|)
expr_stmt|;
block|}
specifier|private
name|void
name|fillQueue
parameter_list|(
specifier|final
name|ActiveMQQueue
name|queue
parameter_list|)
throws|throws
name|JMSException
throws|,
name|InterruptedException
block|{
specifier|final
name|AtomicBoolean
name|done
init|=
operator|new
name|AtomicBoolean
argument_list|(
literal|true
argument_list|)
decl_stmt|;
specifier|final
name|AtomicBoolean
name|keepGoing
init|=
operator|new
name|AtomicBoolean
argument_list|(
literal|true
argument_list|)
decl_stmt|;
comment|// Starts an async thread that every time it publishes it sets the done
comment|// flag to false.
comment|// Once the send starts to block it will not reset the done flag
comment|// anymore.
operator|new
name|Thread
argument_list|(
literal|"Fill thread."
argument_list|)
block|{
specifier|public
name|void
name|run
parameter_list|()
block|{
name|Session
name|session
init|=
literal|null
decl_stmt|;
try|try
block|{
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
name|AUTO_ACKNOWLEDGE
argument_list|)
expr_stmt|;
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
name|DeliveryMode
operator|.
name|NON_PERSISTENT
argument_list|)
expr_stmt|;
while|while
condition|(
name|keepGoing
operator|.
name|get
argument_list|()
condition|)
block|{
name|done
operator|.
name|set
argument_list|(
literal|false
argument_list|)
expr_stmt|;
name|producer
operator|.
name|send
argument_list|(
name|session
operator|.
name|createTextMessage
argument_list|(
literal|"Hello World"
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
catch|catch
parameter_list|(
name|JMSException
name|e
parameter_list|)
block|{                 }
finally|finally
block|{
name|safeClose
argument_list|(
name|session
argument_list|)
expr_stmt|;
block|}
block|}
block|}
operator|.
name|start
argument_list|()
expr_stmt|;
name|waitForBlockedOrResourceLimit
argument_list|(
name|done
argument_list|)
expr_stmt|;
name|keepGoing
operator|.
name|set
argument_list|(
literal|false
argument_list|)
expr_stmt|;
block|}
specifier|protected
name|void
name|waitForBlockedOrResourceLimit
parameter_list|(
specifier|final
name|AtomicBoolean
name|done
parameter_list|)
throws|throws
name|InterruptedException
block|{
while|while
condition|(
literal|true
condition|)
block|{
name|Thread
operator|.
name|sleep
argument_list|(
literal|1000
argument_list|)
expr_stmt|;
comment|// the producer is blocked once the done flag stays true or there is a resource exception
if|if
condition|(
name|done
operator|.
name|get
argument_list|()
operator|||
name|gotResourceException
operator|.
name|get
argument_list|()
condition|)
block|{
break|break;
block|}
name|done
operator|.
name|set
argument_list|(
literal|true
argument_list|)
expr_stmt|;
block|}
block|}
specifier|private
name|CountDownLatch
name|asyncSendTo
parameter_list|(
specifier|final
name|ActiveMQQueue
name|queue
parameter_list|,
specifier|final
name|String
name|message
parameter_list|)
throws|throws
name|JMSException
block|{
specifier|final
name|CountDownLatch
name|done
init|=
operator|new
name|CountDownLatch
argument_list|(
literal|1
argument_list|)
decl_stmt|;
operator|new
name|Thread
argument_list|(
literal|"Send thread."
argument_list|)
block|{
specifier|public
name|void
name|run
parameter_list|()
block|{
name|Session
name|session
init|=
literal|null
decl_stmt|;
try|try
block|{
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
name|AUTO_ACKNOWLEDGE
argument_list|)
expr_stmt|;
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
name|DeliveryMode
operator|.
name|NON_PERSISTENT
argument_list|)
expr_stmt|;
name|producer
operator|.
name|send
argument_list|(
name|session
operator|.
name|createTextMessage
argument_list|(
name|message
argument_list|)
argument_list|)
expr_stmt|;
name|done
operator|.
name|countDown
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|JMSException
name|e
parameter_list|)
block|{                 }
finally|finally
block|{
name|safeClose
argument_list|(
name|session
argument_list|)
expr_stmt|;
block|}
block|}
block|}
operator|.
name|start
argument_list|()
expr_stmt|;
return|return
name|done
return|;
block|}
specifier|protected
name|BrokerService
name|createBroker
parameter_list|()
throws|throws
name|Exception
block|{
name|BrokerService
name|service
init|=
operator|new
name|BrokerService
argument_list|()
decl_stmt|;
name|service
operator|.
name|setPersistent
argument_list|(
literal|false
argument_list|)
expr_stmt|;
name|service
operator|.
name|setUseJmx
argument_list|(
literal|false
argument_list|)
expr_stmt|;
comment|// Setup a destination policy where it takes only 1 message at a time.
name|PolicyMap
name|policyMap
init|=
operator|new
name|PolicyMap
argument_list|()
decl_stmt|;
name|PolicyEntry
name|policy
init|=
operator|new
name|PolicyEntry
argument_list|()
decl_stmt|;
name|policy
operator|.
name|setMemoryLimit
argument_list|(
literal|1
argument_list|)
expr_stmt|;
name|policy
operator|.
name|setPendingSubscriberPolicy
argument_list|(
operator|new
name|VMPendingSubscriberMessageStoragePolicy
argument_list|()
argument_list|)
expr_stmt|;
name|policy
operator|.
name|setPendingQueuePolicy
argument_list|(
operator|new
name|VMPendingQueueMessageStoragePolicy
argument_list|()
argument_list|)
expr_stmt|;
name|policy
operator|.
name|setProducerFlowControl
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|policyMap
operator|.
name|setDefaultEntry
argument_list|(
name|policy
argument_list|)
expr_stmt|;
name|service
operator|.
name|setDestinationPolicy
argument_list|(
name|policyMap
argument_list|)
expr_stmt|;
name|connector
operator|=
name|service
operator|.
name|addConnector
argument_list|(
literal|"tcp://localhost:0"
argument_list|)
expr_stmt|;
return|return
name|service
return|;
block|}
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
block|}
specifier|protected
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
name|TcpTransport
name|t
init|=
operator|(
name|TcpTransport
operator|)
name|connection
operator|.
name|getTransport
argument_list|()
operator|.
name|narrow
argument_list|(
name|TcpTransport
operator|.
name|class
argument_list|)
decl_stmt|;
name|t
operator|.
name|getTransportListener
argument_list|()
operator|.
name|onException
argument_list|(
operator|new
name|IOException
argument_list|(
literal|"Disposed."
argument_list|)
argument_list|)
expr_stmt|;
name|connection
operator|.
name|getTransport
argument_list|()
operator|.
name|stop
argument_list|()
expr_stmt|;
name|super
operator|.
name|tearDown
argument_list|()
expr_stmt|;
block|}
block|}
specifier|protected
name|ConnectionFactory
name|createConnectionFactory
parameter_list|()
throws|throws
name|Exception
block|{
return|return
operator|new
name|ActiveMQConnectionFactory
argument_list|(
name|connector
operator|.
name|getConnectUri
argument_list|()
argument_list|)
return|;
block|}
block|}
end_class

end_unit

