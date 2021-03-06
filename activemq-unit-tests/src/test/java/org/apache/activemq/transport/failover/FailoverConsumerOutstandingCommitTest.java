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
name|failover
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
name|BrokerPlugin
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
name|BrokerPluginSupport
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
name|ConnectionContext
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
name|TransactionId
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
name|Test
import|;
end_import

begin_class
specifier|public
class|class
name|FailoverConsumerOutstandingCommitTest
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
name|FailoverConsumerOutstandingCommitTest
operator|.
name|class
argument_list|)
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|String
name|QUEUE_NAME
init|=
literal|"FailoverWithOutstandingCommit"
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|String
name|MESSAGE_TEXT
init|=
literal|"Test message "
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|String
name|TRANSPORT_URI
init|=
literal|"tcp://localhost:0"
decl_stmt|;
specifier|private
name|String
name|url
decl_stmt|;
specifier|final
name|int
name|prefetch
init|=
literal|10
decl_stmt|;
name|BrokerService
name|broker
decl_stmt|;
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
specifier|public
name|void
name|startBroker
parameter_list|(
name|boolean
name|deleteAllMessagesOnStartup
parameter_list|)
throws|throws
name|Exception
block|{
name|broker
operator|=
name|createBroker
argument_list|(
name|deleteAllMessagesOnStartup
argument_list|)
expr_stmt|;
name|broker
operator|.
name|start
argument_list|()
expr_stmt|;
block|}
specifier|public
name|BrokerService
name|createBroker
parameter_list|(
name|boolean
name|deleteAllMessagesOnStartup
parameter_list|)
throws|throws
name|Exception
block|{
return|return
name|createBroker
argument_list|(
name|deleteAllMessagesOnStartup
argument_list|,
name|TRANSPORT_URI
argument_list|)
return|;
block|}
specifier|public
name|BrokerService
name|createBroker
parameter_list|(
name|boolean
name|deleteAllMessagesOnStartup
parameter_list|,
name|String
name|bindAddress
parameter_list|)
throws|throws
name|Exception
block|{
name|broker
operator|=
operator|new
name|BrokerService
argument_list|()
expr_stmt|;
name|broker
operator|.
name|addConnector
argument_list|(
name|bindAddress
argument_list|)
expr_stmt|;
name|broker
operator|.
name|setDeleteAllMessagesOnStartup
argument_list|(
name|deleteAllMessagesOnStartup
argument_list|)
expr_stmt|;
name|PolicyMap
name|policyMap
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
comment|// optimizedDispatche and sync dispatch ensure that the dispatch happens
comment|// before the commit reply that the consumer.clearDispatchList is waiting for.
name|defaultEntry
operator|.
name|setOptimizedDispatch
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|policyMap
operator|.
name|setDefaultEntry
argument_list|(
name|defaultEntry
argument_list|)
expr_stmt|;
name|broker
operator|.
name|setDestinationPolicy
argument_list|(
name|policyMap
argument_list|)
expr_stmt|;
name|url
operator|=
name|broker
operator|.
name|getTransportConnectors
argument_list|()
operator|.
name|get
argument_list|(
literal|0
argument_list|)
operator|.
name|getConnectUri
argument_list|()
operator|.
name|toString
argument_list|()
expr_stmt|;
return|return
name|broker
return|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testFailoverConsumerDups
parameter_list|()
throws|throws
name|Exception
block|{
name|doTestFailoverConsumerDups
argument_list|(
literal|true
argument_list|)
expr_stmt|;
block|}
annotation|@
name|SuppressWarnings
argument_list|(
literal|"unchecked"
argument_list|)
specifier|public
name|void
name|doTestFailoverConsumerDups
parameter_list|(
specifier|final
name|boolean
name|watchTopicAdvisories
parameter_list|)
throws|throws
name|Exception
block|{
name|broker
operator|=
name|createBroker
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|broker
operator|.
name|setPlugins
argument_list|(
operator|new
name|BrokerPlugin
index|[]
block|{
operator|new
name|BrokerPluginSupport
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|void
name|commitTransaction
parameter_list|(
name|ConnectionContext
name|context
parameter_list|,
name|TransactionId
name|xid
parameter_list|,
name|boolean
name|onePhase
parameter_list|)
throws|throws
name|Exception
block|{
comment|// so commit will hang as if reply is lost
name|context
operator|.
name|setDontSendReponse
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|Executors
operator|.
name|newSingleThreadExecutor
argument_list|()
operator|.
name|execute
argument_list|(
operator|new
name|Runnable
argument_list|()
block|{
specifier|public
name|void
name|run
parameter_list|()
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"Stopping broker before commit..."
argument_list|)
expr_stmt|;
try|try
block|{
name|broker
operator|.
name|stop
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
block|}
block|}
block|}
end_class

begin_empty_stmt
unit|)
empty_stmt|;
end_empty_stmt

begin_empty_stmt
unit|}                 }         })
empty_stmt|;
end_empty_stmt

begin_expr_stmt
name|broker
operator|.
name|start
argument_list|()
expr_stmt|;
end_expr_stmt

begin_decl_stmt
name|ActiveMQConnectionFactory
name|cf
init|=
operator|new
name|ActiveMQConnectionFactory
argument_list|(
literal|"failover:("
operator|+
name|url
operator|+
literal|")"
argument_list|)
decl_stmt|;
end_decl_stmt

begin_expr_stmt
name|cf
operator|.
name|setWatchTopicAdvisories
argument_list|(
name|watchTopicAdvisories
argument_list|)
expr_stmt|;
end_expr_stmt

begin_expr_stmt
name|cf
operator|.
name|setDispatchAsync
argument_list|(
literal|false
argument_list|)
expr_stmt|;
end_expr_stmt

begin_decl_stmt
specifier|final
name|ActiveMQConnection
name|connection
init|=
operator|(
name|ActiveMQConnection
operator|)
name|cf
operator|.
name|createConnection
argument_list|()
decl_stmt|;
end_decl_stmt

begin_expr_stmt
name|connection
operator|.
name|start
argument_list|()
expr_stmt|;
end_expr_stmt

begin_decl_stmt
specifier|final
name|Session
name|producerSession
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
end_decl_stmt

begin_decl_stmt
specifier|final
name|Queue
name|destination
init|=
name|producerSession
operator|.
name|createQueue
argument_list|(
name|QUEUE_NAME
operator|+
literal|"?consumer.prefetchSize="
operator|+
name|prefetch
argument_list|)
decl_stmt|;
end_decl_stmt

begin_decl_stmt
specifier|final
name|Session
name|consumerSession
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
end_decl_stmt

begin_decl_stmt
specifier|final
name|CountDownLatch
name|commitDoneLatch
init|=
operator|new
name|CountDownLatch
argument_list|(
literal|1
argument_list|)
decl_stmt|;
end_decl_stmt

begin_decl_stmt
specifier|final
name|CountDownLatch
name|messagesReceived
init|=
operator|new
name|CountDownLatch
argument_list|(
literal|2
argument_list|)
decl_stmt|;
end_decl_stmt

begin_decl_stmt
specifier|final
name|MessageConsumer
name|testConsumer
init|=
name|consumerSession
operator|.
name|createConsumer
argument_list|(
name|destination
argument_list|)
decl_stmt|;
end_decl_stmt

begin_expr_stmt
name|testConsumer
operator|.
name|setMessageListener
argument_list|(
operator|new
name|MessageListener
argument_list|()
block|{
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
literal|"consume one and commit"
argument_list|)
expr_stmt|;
name|assertNotNull
argument_list|(
literal|"got message"
argument_list|,
name|message
argument_list|)
expr_stmt|;
try|try
block|{
name|consumerSession
operator|.
name|commit
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
name|commitDoneLatch
operator|.
name|countDown
argument_list|()
expr_stmt|;
name|messagesReceived
operator|.
name|countDown
argument_list|()
expr_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"done commit"
argument_list|)
expr_stmt|;
block|}
block|}
argument_list|)
expr_stmt|;
end_expr_stmt

begin_comment
comment|// may block if broker shutodwn happens quickly
end_comment

begin_expr_stmt
name|Executors
operator|.
name|newSingleThreadExecutor
argument_list|()
operator|.
name|execute
argument_list|(
operator|new
name|Runnable
argument_list|()
block|{
specifier|public
name|void
name|run
parameter_list|()
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"producer started"
argument_list|)
expr_stmt|;
try|try
block|{
name|produceMessage
argument_list|(
name|producerSession
argument_list|,
name|destination
argument_list|,
name|prefetch
operator|*
literal|2
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|javax
operator|.
name|jms
operator|.
name|IllegalStateException
name|SessionClosedExpectedOnShutdown
parameter_list|)
block|{                 }
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
literal|"unexpceted ex on producer: "
operator|+
name|e
argument_list|)
expr_stmt|;
block|}
name|LOG
operator|.
name|info
argument_list|(
literal|"producer done"
argument_list|)
expr_stmt|;
block|}
block|}
argument_list|)
expr_stmt|;
end_expr_stmt

begin_comment
comment|// will be stopped by the plugin
end_comment

begin_expr_stmt
name|broker
operator|.
name|waitUntilStopped
argument_list|()
expr_stmt|;
end_expr_stmt

begin_expr_stmt
name|broker
operator|=
name|createBroker
argument_list|(
literal|false
argument_list|,
name|url
argument_list|)
expr_stmt|;
end_expr_stmt

begin_expr_stmt
name|broker
operator|.
name|start
argument_list|()
expr_stmt|;
end_expr_stmt

begin_expr_stmt
name|assertTrue
argument_list|(
literal|"consumer added through failover"
argument_list|,
name|commitDoneLatch
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
end_expr_stmt

begin_expr_stmt
name|assertTrue
argument_list|(
literal|"another message was recieved after failover"
argument_list|,
name|messagesReceived
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
end_expr_stmt

begin_expr_stmt
name|connection
operator|.
name|close
argument_list|()
expr_stmt|;
end_expr_stmt

begin_function
unit|}      @
name|Test
specifier|public
name|void
name|TestFailoverConsumerOutstandingSendTxIncomplete
parameter_list|()
throws|throws
name|Exception
block|{
name|doTestFailoverConsumerOutstandingSendTx
argument_list|(
literal|false
argument_list|)
expr_stmt|;
block|}
end_function

begin_function
annotation|@
name|Test
specifier|public
name|void
name|TestFailoverConsumerOutstandingSendTxComplete
parameter_list|()
throws|throws
name|Exception
block|{
name|doTestFailoverConsumerOutstandingSendTx
argument_list|(
literal|true
argument_list|)
expr_stmt|;
block|}
end_function

begin_function
annotation|@
name|SuppressWarnings
argument_list|(
literal|"unchecked"
argument_list|)
specifier|public
name|void
name|doTestFailoverConsumerOutstandingSendTx
parameter_list|(
specifier|final
name|boolean
name|doActualBrokerCommit
parameter_list|)
throws|throws
name|Exception
block|{
specifier|final
name|boolean
name|watchTopicAdvisories
init|=
literal|true
decl_stmt|;
name|broker
operator|=
name|createBroker
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|broker
operator|.
name|setPlugins
argument_list|(
operator|new
name|BrokerPlugin
index|[]
block|{
operator|new
name|BrokerPluginSupport
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|void
name|commitTransaction
parameter_list|(
name|ConnectionContext
name|context
parameter_list|,
name|TransactionId
name|xid
parameter_list|,
name|boolean
name|onePhase
parameter_list|)
throws|throws
name|Exception
block|{
comment|// from the consumer perspective whether the commit completed on the broker or
comment|// not is irrelevant, the transaction is still in doubt in the absence of a reply
if|if
condition|(
name|doActualBrokerCommit
condition|)
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"doing actual broker commit..."
argument_list|)
expr_stmt|;
name|super
operator|.
name|commitTransaction
argument_list|(
name|context
argument_list|,
name|xid
argument_list|,
name|onePhase
argument_list|)
expr_stmt|;
block|}
comment|// so commit will hang as if reply is lost
name|context
operator|.
name|setDontSendReponse
argument_list|(
literal|true
argument_list|)
argument_list|;
name|Executors
operator|.
name|newSingleThreadExecutor
argument_list|()
operator|.
name|execute
argument_list|(
operator|new
name|Runnable
argument_list|()
block|{
specifier|public
name|void
name|run
parameter_list|()
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"Stopping broker before commit..."
argument_list|)
expr_stmt|;
try|try
block|{
name|broker
operator|.
name|stop
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
block|}
block|}
block|}
argument_list|)
argument_list|;
block|}
end_function

begin_empty_stmt
unit|} })
empty_stmt|;
end_empty_stmt

begin_expr_stmt
name|broker
operator|.
name|start
argument_list|()
expr_stmt|;
end_expr_stmt

begin_decl_stmt
name|ActiveMQConnectionFactory
name|cf
init|=
operator|new
name|ActiveMQConnectionFactory
argument_list|(
literal|"failover:("
operator|+
name|url
operator|+
literal|")"
argument_list|)
decl_stmt|;
end_decl_stmt

begin_expr_stmt
name|cf
operator|.
name|setWatchTopicAdvisories
argument_list|(
name|watchTopicAdvisories
argument_list|)
expr_stmt|;
end_expr_stmt

begin_expr_stmt
name|cf
operator|.
name|setDispatchAsync
argument_list|(
literal|false
argument_list|)
expr_stmt|;
end_expr_stmt

begin_decl_stmt
specifier|final
name|ActiveMQConnection
name|connection
init|=
operator|(
name|ActiveMQConnection
operator|)
name|cf
operator|.
name|createConnection
argument_list|()
decl_stmt|;
end_decl_stmt

begin_expr_stmt
name|connection
operator|.
name|start
argument_list|()
expr_stmt|;
end_expr_stmt

begin_decl_stmt
specifier|final
name|Session
name|producerSession
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
end_decl_stmt

begin_decl_stmt
specifier|final
name|Queue
name|destination
init|=
name|producerSession
operator|.
name|createQueue
argument_list|(
name|QUEUE_NAME
operator|+
literal|"?consumer.prefetchSize="
operator|+
name|prefetch
argument_list|)
decl_stmt|;
end_decl_stmt

begin_decl_stmt
specifier|final
name|Queue
name|signalDestination
init|=
name|producerSession
operator|.
name|createQueue
argument_list|(
name|QUEUE_NAME
operator|+
literal|".signal"
operator|+
literal|"?consumer.prefetchSize="
operator|+
name|prefetch
argument_list|)
decl_stmt|;
end_decl_stmt

begin_decl_stmt
specifier|final
name|Session
name|consumerSession
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
end_decl_stmt

begin_decl_stmt
specifier|final
name|CountDownLatch
name|commitDoneLatch
init|=
operator|new
name|CountDownLatch
argument_list|(
literal|1
argument_list|)
decl_stmt|;
end_decl_stmt

begin_decl_stmt
specifier|final
name|CountDownLatch
name|messagesReceived
init|=
operator|new
name|CountDownLatch
argument_list|(
literal|3
argument_list|)
decl_stmt|;
end_decl_stmt

begin_decl_stmt
specifier|final
name|AtomicBoolean
name|gotCommitException
init|=
operator|new
name|AtomicBoolean
argument_list|(
literal|false
argument_list|)
decl_stmt|;
end_decl_stmt

begin_decl_stmt
specifier|final
name|ArrayList
argument_list|<
name|TextMessage
argument_list|>
name|receivedMessages
init|=
operator|new
name|ArrayList
argument_list|<
name|TextMessage
argument_list|>
argument_list|()
decl_stmt|;
end_decl_stmt

begin_decl_stmt
specifier|final
name|MessageConsumer
name|testConsumer
init|=
name|consumerSession
operator|.
name|createConsumer
argument_list|(
name|destination
argument_list|)
decl_stmt|;
end_decl_stmt

begin_expr_stmt
name|testConsumer
operator|.
name|setMessageListener
argument_list|(
operator|new
name|MessageListener
argument_list|()
block|{
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
literal|"consume one and commit: "
operator|+
name|message
argument_list|)
expr_stmt|;
name|assertNotNull
argument_list|(
literal|"got message"
argument_list|,
name|message
argument_list|)
expr_stmt|;
name|receivedMessages
operator|.
name|add
argument_list|(
operator|(
name|TextMessage
operator|)
name|message
argument_list|)
expr_stmt|;
try|try
block|{
name|produceMessage
argument_list|(
name|consumerSession
argument_list|,
name|signalDestination
argument_list|,
literal|1
argument_list|)
expr_stmt|;
name|consumerSession
operator|.
name|commit
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|JMSException
name|e
parameter_list|)
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"commit exception"
argument_list|,
name|e
argument_list|)
expr_stmt|;
name|gotCommitException
operator|.
name|set
argument_list|(
literal|true
argument_list|)
expr_stmt|;
block|}
name|commitDoneLatch
operator|.
name|countDown
argument_list|()
expr_stmt|;
name|messagesReceived
operator|.
name|countDown
argument_list|()
expr_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"done commit"
argument_list|)
expr_stmt|;
block|}
block|}
argument_list|)
expr_stmt|;
end_expr_stmt

begin_comment
comment|// may block if broker shutdown happens quickly
end_comment

begin_expr_stmt
name|Executors
operator|.
name|newSingleThreadExecutor
argument_list|()
operator|.
name|execute
argument_list|(
operator|new
name|Runnable
argument_list|()
block|{
specifier|public
name|void
name|run
parameter_list|()
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"producer started"
argument_list|)
expr_stmt|;
try|try
block|{
name|produceMessage
argument_list|(
name|producerSession
argument_list|,
name|destination
argument_list|,
name|prefetch
operator|*
literal|2
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|javax
operator|.
name|jms
operator|.
name|IllegalStateException
name|SessionClosedExpectedOnShutdown
parameter_list|)
block|{                 }
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
literal|"unexpceted ex on producer: "
operator|+
name|e
argument_list|)
expr_stmt|;
block|}
name|LOG
operator|.
name|info
argument_list|(
literal|"producer done"
argument_list|)
expr_stmt|;
block|}
block|}
argument_list|)
expr_stmt|;
end_expr_stmt

begin_comment
comment|// will be stopped by the plugin
end_comment

begin_expr_stmt
name|broker
operator|.
name|waitUntilStopped
argument_list|()
expr_stmt|;
end_expr_stmt

begin_expr_stmt
name|broker
operator|=
name|createBroker
argument_list|(
literal|false
argument_list|,
name|url
argument_list|)
expr_stmt|;
end_expr_stmt

begin_expr_stmt
name|broker
operator|.
name|start
argument_list|()
expr_stmt|;
end_expr_stmt

begin_expr_stmt
name|assertTrue
argument_list|(
literal|"commit done through failover"
argument_list|,
name|commitDoneLatch
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
end_expr_stmt

begin_expr_stmt
name|assertTrue
argument_list|(
literal|"commit failed"
argument_list|,
name|gotCommitException
operator|.
name|get
argument_list|()
argument_list|)
expr_stmt|;
end_expr_stmt

begin_expr_stmt
name|assertTrue
argument_list|(
literal|"another message was received after failover"
argument_list|,
name|messagesReceived
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
end_expr_stmt

begin_decl_stmt
name|int
name|receivedIndex
init|=
literal|0
decl_stmt|;
end_decl_stmt

begin_expr_stmt
name|assertEquals
argument_list|(
literal|"get message 0 first"
argument_list|,
name|MESSAGE_TEXT
operator|+
literal|"0"
argument_list|,
name|receivedMessages
operator|.
name|get
argument_list|(
name|receivedIndex
operator|++
argument_list|)
operator|.
name|getText
argument_list|()
argument_list|)
expr_stmt|;
end_expr_stmt

begin_if
if|if
condition|(
operator|!
name|doActualBrokerCommit
condition|)
block|{
comment|// it will be redelivered and not tracked as a duplicate
name|assertEquals
argument_list|(
literal|"get message 0 second"
argument_list|,
name|MESSAGE_TEXT
operator|+
literal|"0"
argument_list|,
name|receivedMessages
operator|.
name|get
argument_list|(
name|receivedIndex
operator|++
argument_list|)
operator|.
name|getText
argument_list|()
argument_list|)
expr_stmt|;
block|}
end_if

begin_expr_stmt
name|assertTrue
argument_list|(
literal|"another message was received"
argument_list|,
name|messagesReceived
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
end_expr_stmt

begin_expr_stmt
name|assertEquals
argument_list|(
literal|"get message 1 eventually"
argument_list|,
name|MESSAGE_TEXT
operator|+
literal|"1"
argument_list|,
name|receivedMessages
operator|.
name|get
argument_list|(
name|receivedIndex
operator|++
argument_list|)
operator|.
name|getText
argument_list|()
argument_list|)
expr_stmt|;
end_expr_stmt

begin_expr_stmt
name|connection
operator|.
name|close
argument_list|()
expr_stmt|;
end_expr_stmt

begin_function
unit|}      @
name|Test
specifier|public
name|void
name|testRollbackFailoverConsumerTx
parameter_list|()
throws|throws
name|Exception
block|{
name|broker
operator|=
name|createBroker
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|broker
operator|.
name|start
argument_list|()
expr_stmt|;
name|ActiveMQConnectionFactory
name|cf
init|=
operator|new
name|ActiveMQConnectionFactory
argument_list|(
literal|"failover:("
operator|+
name|url
operator|+
literal|")"
argument_list|)
decl_stmt|;
name|cf
operator|.
name|setConsumerFailoverRedeliveryWaitPeriod
argument_list|(
literal|10000
argument_list|)
expr_stmt|;
specifier|final
name|ActiveMQConnection
name|connection
init|=
operator|(
name|ActiveMQConnection
operator|)
name|cf
operator|.
name|createConnection
argument_list|()
decl_stmt|;
name|connection
operator|.
name|start
argument_list|()
expr_stmt|;
specifier|final
name|Session
name|producerSession
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
specifier|final
name|Queue
name|destination
init|=
name|producerSession
operator|.
name|createQueue
argument_list|(
name|QUEUE_NAME
argument_list|)
decl_stmt|;
specifier|final
name|Session
name|consumerSession
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
specifier|final
name|MessageConsumer
name|testConsumer
init|=
name|consumerSession
operator|.
name|createConsumer
argument_list|(
name|destination
argument_list|)
decl_stmt|;
name|assertNull
argument_list|(
literal|"no message yet"
argument_list|,
name|testConsumer
operator|.
name|receiveNoWait
argument_list|()
argument_list|)
expr_stmt|;
name|produceMessage
argument_list|(
name|producerSession
argument_list|,
name|destination
argument_list|,
literal|1
argument_list|)
expr_stmt|;
name|producerSession
operator|.
name|close
argument_list|()
expr_stmt|;
comment|// consume then rollback after restart
name|Message
name|msg
init|=
name|testConsumer
operator|.
name|receive
argument_list|(
literal|5000
argument_list|)
decl_stmt|;
name|assertNotNull
argument_list|(
name|msg
argument_list|)
expr_stmt|;
comment|// restart with outstanding delivered message
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
name|broker
operator|=
name|createBroker
argument_list|(
literal|false
argument_list|,
name|url
argument_list|)
expr_stmt|;
name|broker
operator|.
name|start
argument_list|()
expr_stmt|;
name|consumerSession
operator|.
name|rollback
argument_list|()
expr_stmt|;
comment|// receive again
name|msg
operator|=
name|testConsumer
operator|.
name|receive
argument_list|(
literal|10000
argument_list|)
expr_stmt|;
name|assertNotNull
argument_list|(
literal|"got message again after rollback"
argument_list|,
name|msg
argument_list|)
expr_stmt|;
name|consumerSession
operator|.
name|commit
argument_list|()
expr_stmt|;
comment|// close before sweep
name|consumerSession
operator|.
name|close
argument_list|()
expr_stmt|;
name|msg
operator|=
name|receiveMessage
argument_list|(
name|cf
argument_list|,
name|destination
argument_list|)
expr_stmt|;
name|assertNull
argument_list|(
literal|"should be nothing left after commit"
argument_list|,
name|msg
argument_list|)
expr_stmt|;
name|connection
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
end_function

begin_function
specifier|private
name|Message
name|receiveMessage
parameter_list|(
name|ActiveMQConnectionFactory
name|cf
parameter_list|,
name|Queue
name|destination
parameter_list|)
throws|throws
name|Exception
block|{
specifier|final
name|ActiveMQConnection
name|connection
init|=
operator|(
name|ActiveMQConnection
operator|)
name|cf
operator|.
name|createConnection
argument_list|()
decl_stmt|;
name|connection
operator|.
name|start
argument_list|()
expr_stmt|;
specifier|final
name|Session
name|consumerSession
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
specifier|final
name|MessageConsumer
name|consumer
init|=
name|consumerSession
operator|.
name|createConsumer
argument_list|(
name|destination
argument_list|)
decl_stmt|;
name|Message
name|msg
init|=
name|consumer
operator|.
name|receive
argument_list|(
literal|5000
argument_list|)
decl_stmt|;
name|consumerSession
operator|.
name|commit
argument_list|()
expr_stmt|;
name|connection
operator|.
name|close
argument_list|()
expr_stmt|;
return|return
name|msg
return|;
block|}
end_function

begin_function
specifier|private
name|void
name|produceMessage
parameter_list|(
specifier|final
name|Session
name|producerSession
parameter_list|,
name|Queue
name|destination
parameter_list|,
name|long
name|count
parameter_list|)
throws|throws
name|JMSException
block|{
name|MessageProducer
name|producer
init|=
name|producerSession
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
name|TextMessage
name|message
init|=
name|producerSession
operator|.
name|createTextMessage
argument_list|(
name|MESSAGE_TEXT
operator|+
name|i
argument_list|)
decl_stmt|;
name|producer
operator|.
name|send
argument_list|(
name|message
argument_list|)
expr_stmt|;
block|}
name|producer
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
end_function

unit|}
end_unit

