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
name|store
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
name|Executor
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
name|Executors
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
name|ActiveMQConnection
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
name|command
operator|.
name|ActiveMQQueue
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

begin_comment
comment|//  https://issues.apache.org/activemq/browse/AMQ-2594
end_comment

begin_class
specifier|public
specifier|abstract
class|class
name|StoreOrderTest
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
name|StoreOrderTest
operator|.
name|class
argument_list|)
decl_stmt|;
specifier|protected
name|BrokerService
name|broker
decl_stmt|;
specifier|private
name|ActiveMQConnection
name|connection
decl_stmt|;
specifier|public
name|Destination
name|destination
init|=
operator|new
name|ActiveMQQueue
argument_list|(
literal|"StoreOrderTest?consumer.prefetchSize=0"
argument_list|)
decl_stmt|;
specifier|protected
specifier|abstract
name|void
name|setPersistentAdapter
parameter_list|(
name|BrokerService
name|brokerService
parameter_list|)
throws|throws
name|Exception
function_decl|;
specifier|protected
name|void
name|dumpMessages
parameter_list|()
throws|throws
name|Exception
block|{}
specifier|public
class|class
name|TransactedSend
implements|implements
name|Runnable
block|{
specifier|private
name|CountDownLatch
name|readyForCommit
decl_stmt|;
specifier|private
name|CountDownLatch
name|firstDone
decl_stmt|;
specifier|private
name|boolean
name|first
decl_stmt|;
specifier|private
name|Session
name|session
decl_stmt|;
specifier|private
name|MessageProducer
name|producer
decl_stmt|;
specifier|public
name|TransactedSend
parameter_list|(
name|CountDownLatch
name|readyForCommit
parameter_list|,
name|CountDownLatch
name|firstDone
parameter_list|,
name|boolean
name|b
parameter_list|)
throws|throws
name|Exception
block|{
name|this
operator|.
name|readyForCommit
operator|=
name|readyForCommit
expr_stmt|;
name|this
operator|.
name|firstDone
operator|=
name|firstDone
expr_stmt|;
name|this
operator|.
name|first
operator|=
name|b
expr_stmt|;
name|session
operator|=
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
expr_stmt|;
name|producer
operator|=
name|session
operator|.
name|createProducer
argument_list|(
name|destination
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
if|if
condition|(
operator|!
name|first
condition|)
block|{
name|firstDone
operator|.
name|await
argument_list|(
literal|30
argument_list|,
name|TimeUnit
operator|.
name|SECONDS
argument_list|)
expr_stmt|;
block|}
name|producer
operator|.
name|send
argument_list|(
name|session
operator|.
name|createTextMessage
argument_list|(
name|first
condition|?
literal|"first"
else|:
literal|"second"
argument_list|)
argument_list|)
expr_stmt|;
if|if
condition|(
name|first
condition|)
block|{
name|firstDone
operator|.
name|countDown
argument_list|()
expr_stmt|;
block|}
name|readyForCommit
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
name|e
operator|.
name|printStackTrace
argument_list|()
expr_stmt|;
name|fail
argument_list|(
literal|"unexpected ex on run "
operator|+
name|e
argument_list|)
expr_stmt|;
block|}
block|}
specifier|public
name|void
name|commit
parameter_list|()
throws|throws
name|Exception
block|{
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
block|}
annotation|@
name|Before
specifier|public
name|void
name|setup
parameter_list|()
throws|throws
name|Exception
block|{
name|broker
operator|=
name|createBroker
argument_list|()
expr_stmt|;
name|initConnection
argument_list|()
expr_stmt|;
block|}
specifier|public
name|void
name|initConnection
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
literal|"vm://localhost?create=false"
argument_list|)
decl_stmt|;
name|connection
operator|=
operator|(
name|ActiveMQConnection
operator|)
name|connectionFactory
operator|.
name|createConnection
argument_list|()
expr_stmt|;
name|connection
operator|.
name|setWatchTopicAdvisories
argument_list|(
literal|false
argument_list|)
expr_stmt|;
name|connection
operator|.
name|start
argument_list|()
expr_stmt|;
block|}
annotation|@
name|After
specifier|public
name|void
name|stopBroker
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
block|}
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
annotation|@
name|Test
specifier|public
name|void
name|testCompositeSendReceiveAfterRestart
parameter_list|()
throws|throws
name|Exception
block|{
name|destination
operator|=
operator|new
name|ActiveMQQueue
argument_list|(
literal|"StoreOrderTest,SecondStoreOrderTest"
argument_list|)
expr_stmt|;
name|enqueueOneMessage
argument_list|()
expr_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"restart broker"
argument_list|)
expr_stmt|;
name|stopBroker
argument_list|()
expr_stmt|;
name|broker
operator|=
name|createRestartedBroker
argument_list|()
expr_stmt|;
name|dumpMessages
argument_list|()
expr_stmt|;
name|initConnection
argument_list|()
expr_stmt|;
name|destination
operator|=
operator|new
name|ActiveMQQueue
argument_list|(
literal|"StoreOrderTest"
argument_list|)
expr_stmt|;
name|assertNotNull
argument_list|(
literal|"got one message from first dest"
argument_list|,
name|receiveOne
argument_list|()
argument_list|)
expr_stmt|;
name|dumpMessages
argument_list|()
expr_stmt|;
name|destination
operator|=
operator|new
name|ActiveMQQueue
argument_list|(
literal|"SecondStoreOrderTest"
argument_list|)
expr_stmt|;
name|assertNotNull
argument_list|(
literal|"got one message from second dest"
argument_list|,
name|receiveOne
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|validateUnorderedTxCommit
parameter_list|()
throws|throws
name|Exception
block|{
name|Executor
name|executor
init|=
name|Executors
operator|.
name|newCachedThreadPool
argument_list|()
decl_stmt|;
name|CountDownLatch
name|readyForCommit
init|=
operator|new
name|CountDownLatch
argument_list|(
literal|2
argument_list|)
decl_stmt|;
name|CountDownLatch
name|firstDone
init|=
operator|new
name|CountDownLatch
argument_list|(
literal|1
argument_list|)
decl_stmt|;
name|TransactedSend
name|first
init|=
operator|new
name|TransactedSend
argument_list|(
name|readyForCommit
argument_list|,
name|firstDone
argument_list|,
literal|true
argument_list|)
decl_stmt|;
name|TransactedSend
name|second
init|=
operator|new
name|TransactedSend
argument_list|(
name|readyForCommit
argument_list|,
name|firstDone
argument_list|,
literal|false
argument_list|)
decl_stmt|;
name|executor
operator|.
name|execute
argument_list|(
name|first
argument_list|)
expr_stmt|;
name|executor
operator|.
name|execute
argument_list|(
name|second
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
literal|"both started"
argument_list|,
name|readyForCommit
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
name|LOG
operator|.
name|info
argument_list|(
literal|"commit out of order"
argument_list|)
expr_stmt|;
comment|// send interleaved so sequence id at time of commit could be reversed
name|second
operator|.
name|commit
argument_list|()
expr_stmt|;
comment|// force usage over the limit before second commit to flush cache
name|enqueueOneMessage
argument_list|()
expr_stmt|;
comment|// can get lost in the cursor as it is behind the last sequenceId that was cached
name|first
operator|.
name|commit
argument_list|()
expr_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"send/commit done.."
argument_list|)
expr_stmt|;
name|dumpMessages
argument_list|()
expr_stmt|;
name|String
name|received1
decl_stmt|,
name|received2
decl_stmt|,
name|received3
init|=
literal|null
decl_stmt|;
if|if
condition|(
literal|true
condition|)
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"receive and rollback..."
argument_list|)
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
name|received1
operator|=
name|receive
argument_list|(
name|session
argument_list|)
expr_stmt|;
name|received2
operator|=
name|receive
argument_list|(
name|session
argument_list|)
expr_stmt|;
name|received3
operator|=
name|receive
argument_list|(
name|session
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"second"
argument_list|,
name|received1
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"middle"
argument_list|,
name|received2
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"first"
argument_list|,
name|received3
argument_list|)
expr_stmt|;
name|session
operator|.
name|rollback
argument_list|()
expr_stmt|;
name|session
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
name|LOG
operator|.
name|info
argument_list|(
literal|"restart broker"
argument_list|)
expr_stmt|;
name|stopBroker
argument_list|()
expr_stmt|;
name|broker
operator|=
name|createRestartedBroker
argument_list|()
expr_stmt|;
name|initConnection
argument_list|()
expr_stmt|;
if|if
condition|(
literal|true
condition|)
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"receive and rollback after restart..."
argument_list|)
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
name|received1
operator|=
name|receive
argument_list|(
name|session
argument_list|)
expr_stmt|;
name|received2
operator|=
name|receive
argument_list|(
name|session
argument_list|)
expr_stmt|;
name|received3
operator|=
name|receive
argument_list|(
name|session
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"second"
argument_list|,
name|received1
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"middle"
argument_list|,
name|received2
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"first"
argument_list|,
name|received3
argument_list|)
expr_stmt|;
name|session
operator|.
name|rollback
argument_list|()
expr_stmt|;
name|session
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
name|LOG
operator|.
name|info
argument_list|(
literal|"receive and ack each message"
argument_list|)
expr_stmt|;
name|received1
operator|=
name|receiveOne
argument_list|()
expr_stmt|;
name|received2
operator|=
name|receiveOne
argument_list|()
expr_stmt|;
name|received3
operator|=
name|receiveOne
argument_list|()
expr_stmt|;
name|assertEquals
argument_list|(
literal|"second"
argument_list|,
name|received1
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"middle"
argument_list|,
name|received2
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"first"
argument_list|,
name|received3
argument_list|)
expr_stmt|;
block|}
specifier|private
name|void
name|enqueueOneMessage
parameter_list|()
throws|throws
name|Exception
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
name|send
argument_list|(
name|session
operator|.
name|createTextMessage
argument_list|(
literal|"middle"
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
specifier|private
name|String
name|receiveOne
parameter_list|()
throws|throws
name|Exception
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
name|String
name|received
init|=
name|receive
argument_list|(
name|session
argument_list|)
decl_stmt|;
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
return|return
name|received
return|;
block|}
specifier|private
name|String
name|receive
parameter_list|(
name|Session
name|session
parameter_list|)
throws|throws
name|Exception
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
name|String
name|result
init|=
literal|null
decl_stmt|;
name|TextMessage
name|message
init|=
operator|(
name|TextMessage
operator|)
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
name|LOG
operator|.
name|info
argument_list|(
literal|"got message: "
operator|+
name|message
argument_list|)
expr_stmt|;
name|result
operator|=
name|message
operator|.
name|getText
argument_list|()
expr_stmt|;
block|}
name|consumer
operator|.
name|close
argument_list|()
expr_stmt|;
return|return
name|result
return|;
block|}
specifier|protected
name|BrokerService
name|createBroker
parameter_list|()
throws|throws
name|Exception
block|{
name|boolean
name|deleteMessagesOnStartup
init|=
literal|true
decl_stmt|;
return|return
name|startBroker
argument_list|(
name|deleteMessagesOnStartup
argument_list|)
return|;
block|}
specifier|protected
name|BrokerService
name|createRestartedBroker
parameter_list|()
throws|throws
name|Exception
block|{
name|boolean
name|deleteMessagesOnStartup
init|=
literal|false
decl_stmt|;
return|return
name|startBroker
argument_list|(
name|deleteMessagesOnStartup
argument_list|)
return|;
block|}
specifier|protected
name|BrokerService
name|startBroker
parameter_list|(
name|boolean
name|deleteMessagesOnStartup
parameter_list|)
throws|throws
name|Exception
block|{
name|BrokerService
name|newBroker
init|=
operator|new
name|BrokerService
argument_list|()
decl_stmt|;
name|configureBroker
argument_list|(
name|newBroker
argument_list|)
expr_stmt|;
name|newBroker
operator|.
name|setDeleteAllMessagesOnStartup
argument_list|(
name|deleteMessagesOnStartup
argument_list|)
expr_stmt|;
name|newBroker
operator|.
name|start
argument_list|()
expr_stmt|;
return|return
name|newBroker
return|;
block|}
specifier|protected
name|void
name|configureBroker
parameter_list|(
name|BrokerService
name|brokerService
parameter_list|)
throws|throws
name|Exception
block|{
name|setPersistentAdapter
argument_list|(
name|brokerService
argument_list|)
expr_stmt|;
name|brokerService
operator|.
name|setAdvisorySupport
argument_list|(
literal|false
argument_list|)
expr_stmt|;
name|PolicyMap
name|map
init|=
operator|new
name|PolicyMap
argument_list|()
decl_stmt|;
name|PolicyEntry
name|defaultEntry
init|=
operator|new
name|PolicyEntry
argument_list|()
decl_stmt|;
name|defaultEntry
operator|.
name|setMemoryLimit
argument_list|(
literal|1024
operator|*
literal|3
argument_list|)
expr_stmt|;
name|defaultEntry
operator|.
name|setCursorMemoryHighWaterMark
argument_list|(
literal|68
argument_list|)
expr_stmt|;
name|defaultEntry
operator|.
name|setExpireMessagesPeriod
argument_list|(
literal|0
argument_list|)
expr_stmt|;
name|map
operator|.
name|setDefaultEntry
argument_list|(
name|defaultEntry
argument_list|)
expr_stmt|;
name|brokerService
operator|.
name|setDestinationPolicy
argument_list|(
name|map
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

