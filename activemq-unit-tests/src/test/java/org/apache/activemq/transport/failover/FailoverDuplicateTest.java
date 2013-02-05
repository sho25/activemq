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
name|TestSupport
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
name|ProducerBrokerExchange
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
name|RegionBroker
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
name|FailoverDuplicateTest
extends|extends
name|TestSupport
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
name|FailoverDuplicateTest
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
literal|"TestQueue"
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
name|BrokerService
name|broker
decl_stmt|;
specifier|public
name|void
name|tearDown
parameter_list|()
throws|throws
name|Exception
block|{
name|stopBroker
argument_list|()
expr_stmt|;
block|}
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
specifier|private
name|void
name|startCleanBroker
parameter_list|()
throws|throws
name|Exception
block|{
name|startBroker
argument_list|(
literal|true
argument_list|)
expr_stmt|;
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
name|void
name|startBroker
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
name|createBroker
argument_list|(
name|deleteAllMessagesOnStartup
argument_list|,
name|bindAddress
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
name|setUseJmx
argument_list|(
literal|false
argument_list|)
expr_stmt|;
name|broker
operator|.
name|setAdvisorySupport
argument_list|(
literal|false
argument_list|)
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
specifier|public
name|void
name|configureConnectionFactory
parameter_list|(
name|ActiveMQConnectionFactory
name|factory
parameter_list|)
block|{
name|factory
operator|.
name|setAuditMaximumProducerNumber
argument_list|(
literal|2048
argument_list|)
expr_stmt|;
name|factory
operator|.
name|setOptimizeAcknowledge
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
name|testFailoverSendReplyLost
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
name|setDefaultPersistenceAdapter
argument_list|(
name|broker
argument_list|)
expr_stmt|;
specifier|final
name|CountDownLatch
name|gotMessageLatch
init|=
operator|new
name|CountDownLatch
argument_list|(
literal|1
argument_list|)
decl_stmt|;
specifier|final
name|CountDownLatch
name|producersDone
init|=
operator|new
name|CountDownLatch
argument_list|(
literal|1
argument_list|)
decl_stmt|;
specifier|final
name|AtomicBoolean
name|first
init|=
operator|new
name|AtomicBoolean
argument_list|(
literal|false
argument_list|)
decl_stmt|;
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
name|send
parameter_list|(
specifier|final
name|ProducerBrokerExchange
name|producerExchange
parameter_list|,
name|org
operator|.
name|apache
operator|.
name|activemq
operator|.
name|command
operator|.
name|Message
name|messageSend
parameter_list|)
throws|throws
name|Exception
block|{
comment|// so send will hang as if reply is lost
name|super
operator|.
name|send
argument_list|(
name|producerExchange
argument_list|,
name|messageSend
argument_list|)
expr_stmt|;
if|if
condition|(
name|first
operator|.
name|compareAndSet
argument_list|(
literal|false
argument_list|,
literal|true
argument_list|)
condition|)
block|{
name|producerExchange
operator|.
name|getConnectionContext
argument_list|()
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
try|try
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"Waiting for recepit"
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
literal|"message received on time"
argument_list|,
name|gotMessageLatch
operator|.
name|await
argument_list|(
literal|60
argument_list|,
name|TimeUnit
operator|.
name|SECONDS
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
literal|"new producers done on time"
argument_list|,
name|producersDone
operator|.
name|await
argument_list|(
literal|120
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
literal|"Stopping connection post send and receive and multiple producers"
argument_list|)
expr_stmt|;
name|producerExchange
operator|.
name|getConnectionContext
argument_list|()
operator|.
name|getConnection
argument_list|()
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
unit|}                     }                 }         })
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
literal|")?jms.watchTopicAdvisories=false"
argument_list|)
decl_stmt|;
end_decl_stmt

begin_expr_stmt
name|configureConnectionFactory
argument_list|(
name|cf
argument_list|)
expr_stmt|;
end_expr_stmt

begin_decl_stmt
name|Connection
name|sendConnection
init|=
name|cf
operator|.
name|createConnection
argument_list|()
decl_stmt|;
end_decl_stmt

begin_expr_stmt
name|sendConnection
operator|.
name|start
argument_list|()
expr_stmt|;
end_expr_stmt

begin_decl_stmt
specifier|final
name|Session
name|sendSession
init|=
name|sendConnection
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
name|sendSession
operator|.
name|createQueue
argument_list|(
name|QUEUE_NAME
argument_list|)
decl_stmt|;
end_decl_stmt

begin_decl_stmt
specifier|final
name|AtomicInteger
name|receivedCount
init|=
operator|new
name|AtomicInteger
argument_list|()
decl_stmt|;
end_decl_stmt

begin_decl_stmt
name|MessageListener
name|listener
init|=
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
name|gotMessageLatch
operator|.
name|countDown
argument_list|()
expr_stmt|;
name|receivedCount
operator|.
name|incrementAndGet
argument_list|()
expr_stmt|;
block|}
block|}
decl_stmt|;
end_decl_stmt

begin_decl_stmt
name|Connection
name|receiveConnection
decl_stmt|;
end_decl_stmt

begin_decl_stmt
name|Session
name|receiveSession
init|=
literal|null
decl_stmt|;
end_decl_stmt

begin_expr_stmt
name|receiveConnection
operator|=
name|cf
operator|.
name|createConnection
argument_list|()
expr_stmt|;
end_expr_stmt

begin_expr_stmt
name|receiveConnection
operator|.
name|start
argument_list|()
expr_stmt|;
end_expr_stmt

begin_expr_stmt
name|receiveSession
operator|=
name|receiveConnection
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
end_expr_stmt

begin_expr_stmt
name|receiveSession
operator|.
name|createConsumer
argument_list|(
name|destination
argument_list|)
operator|.
name|setMessageListener
argument_list|(
name|listener
argument_list|)
expr_stmt|;
end_expr_stmt

begin_decl_stmt
specifier|final
name|CountDownLatch
name|sendDoneLatch
init|=
operator|new
name|CountDownLatch
argument_list|(
literal|1
argument_list|)
decl_stmt|;
end_decl_stmt

begin_comment
comment|// broker will die on send reply so this will hang till restart
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
literal|"doing async send..."
argument_list|)
expr_stmt|;
try|try
block|{
name|produceMessage
argument_list|(
name|sendSession
argument_list|,
name|destination
argument_list|,
literal|"will resend"
argument_list|,
literal|1
argument_list|)
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
name|error
argument_list|(
literal|"got send exception: "
argument_list|,
name|e
argument_list|)
expr_stmt|;
name|fail
argument_list|(
literal|"got unexpected send exception"
operator|+
name|e
argument_list|)
expr_stmt|;
block|}
name|sendDoneLatch
operator|.
name|countDown
argument_list|()
expr_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"done async send"
argument_list|)
expr_stmt|;
block|}
block|}
argument_list|)
expr_stmt|;
end_expr_stmt

begin_expr_stmt
name|assertTrue
argument_list|(
literal|"one message got through on time"
argument_list|,
name|gotMessageLatch
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

begin_comment
comment|// send more messages, blow producer audit
end_comment

begin_decl_stmt
specifier|final
name|int
name|numProducers
init|=
literal|1050
decl_stmt|;
end_decl_stmt

begin_decl_stmt
specifier|final
name|int
name|numPerProducer
init|=
literal|2
decl_stmt|;
end_decl_stmt

begin_decl_stmt
specifier|final
name|int
name|totalSent
init|=
name|numPerProducer
operator|*
name|numProducers
operator|+
literal|1
decl_stmt|;
end_decl_stmt

begin_for
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|numProducers
condition|;
name|i
operator|++
control|)
block|{
name|produceMessage
argument_list|(
name|receiveSession
argument_list|,
name|destination
argument_list|,
literal|"new producer "
operator|+
name|i
argument_list|,
name|numPerProducer
argument_list|)
expr_stmt|;
comment|// release resend when we half done, cursor audit exhausted
comment|// and concurrent dispatch with the resend
if|if
condition|(
name|i
operator|==
literal|1025
condition|)
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"count down producers done"
argument_list|)
expr_stmt|;
name|producersDone
operator|.
name|countDown
argument_list|()
expr_stmt|;
block|}
block|}
end_for

begin_expr_stmt
name|assertTrue
argument_list|(
literal|"message sent complete through failover"
argument_list|,
name|sendDoneLatch
operator|.
name|await
argument_list|(
literal|30
argument_list|,
name|TimeUnit
operator|.
name|SECONDS
argument_list|)
argument_list|)
expr_stmt|;
end_expr_stmt

begin_expr_stmt
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
annotation|@
name|Override
specifier|public
name|boolean
name|isSatisified
parameter_list|()
throws|throws
name|Exception
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"received count:"
operator|+
name|receivedCount
operator|.
name|get
argument_list|()
argument_list|)
expr_stmt|;
return|return
name|totalSent
operator|<=
name|receivedCount
operator|.
name|get
argument_list|()
return|;
block|}
block|}
argument_list|)
expr_stmt|;
end_expr_stmt

begin_expr_stmt
name|assertEquals
argument_list|(
literal|"we got all produced messages"
argument_list|,
name|totalSent
argument_list|,
name|receivedCount
operator|.
name|get
argument_list|()
argument_list|)
expr_stmt|;
end_expr_stmt

begin_expr_stmt
name|sendConnection
operator|.
name|close
argument_list|()
expr_stmt|;
end_expr_stmt

begin_expr_stmt
name|receiveConnection
operator|.
name|close
argument_list|()
expr_stmt|;
end_expr_stmt

begin_comment
comment|// verify stats
end_comment

begin_expr_stmt
name|assertEquals
argument_list|(
literal|"expect all messages are dequeued with one duplicate"
argument_list|,
name|totalSent
operator|+
literal|1
argument_list|,
operator|(
operator|(
name|RegionBroker
operator|)
name|broker
operator|.
name|getRegionBroker
argument_list|()
operator|)
operator|.
name|getDestinationStatistics
argument_list|()
operator|.
name|getEnqueues
argument_list|()
operator|.
name|getCount
argument_list|()
argument_list|)
expr_stmt|;
end_expr_stmt

begin_expr_stmt
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
annotation|@
name|Override
specifier|public
name|boolean
name|isSatisified
parameter_list|()
throws|throws
name|Exception
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"dequeues : "
operator|+
operator|(
operator|(
name|RegionBroker
operator|)
name|broker
operator|.
name|getRegionBroker
argument_list|()
operator|)
operator|.
name|getDestinationStatistics
argument_list|()
operator|.
name|getDequeues
argument_list|()
operator|.
name|getCount
argument_list|()
argument_list|)
expr_stmt|;
return|return
name|totalSent
operator|+
literal|1
operator|<=
operator|(
operator|(
name|RegionBroker
operator|)
name|broker
operator|.
name|getRegionBroker
argument_list|()
operator|)
operator|.
name|getDestinationStatistics
argument_list|()
operator|.
name|getDequeues
argument_list|()
operator|.
name|getCount
argument_list|()
return|;
block|}
block|}
argument_list|)
expr_stmt|;
end_expr_stmt

begin_expr_stmt
name|assertEquals
argument_list|(
literal|"dequeue correct, including duplicate dispatch auto acked"
argument_list|,
name|totalSent
operator|+
literal|1
argument_list|,
operator|(
operator|(
name|RegionBroker
operator|)
name|broker
operator|.
name|getRegionBroker
argument_list|()
operator|)
operator|.
name|getDestinationStatistics
argument_list|()
operator|.
name|getDequeues
argument_list|()
operator|.
name|getCount
argument_list|()
argument_list|)
expr_stmt|;
end_expr_stmt

begin_comment
comment|// ensure no dangling messages with fresh broker etc
end_comment

begin_expr_stmt
name|broker
operator|.
name|stop
argument_list|()
expr_stmt|;
end_expr_stmt

begin_expr_stmt
name|broker
operator|.
name|waitUntilStopped
argument_list|()
expr_stmt|;
end_expr_stmt

begin_expr_stmt
name|LOG
operator|.
name|info
argument_list|(
literal|"Checking for remaining/hung messages with second restart.."
argument_list|)
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
name|setDefaultPersistenceAdapter
argument_list|(
name|broker
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

begin_comment
comment|// after restart, ensure no dangling messages
end_comment

begin_expr_stmt
name|cf
operator|=
operator|new
name|ActiveMQConnectionFactory
argument_list|(
literal|"failover:("
operator|+
name|url
operator|+
literal|")"
argument_list|)
expr_stmt|;
end_expr_stmt

begin_expr_stmt
name|configureConnectionFactory
argument_list|(
name|cf
argument_list|)
expr_stmt|;
end_expr_stmt

begin_expr_stmt
name|sendConnection
operator|=
name|cf
operator|.
name|createConnection
argument_list|()
expr_stmt|;
end_expr_stmt

begin_expr_stmt
name|sendConnection
operator|.
name|start
argument_list|()
expr_stmt|;
end_expr_stmt

begin_decl_stmt
name|Session
name|session2
init|=
name|sendConnection
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
name|MessageConsumer
name|consumer
init|=
name|session2
operator|.
name|createConsumer
argument_list|(
name|destination
argument_list|)
decl_stmt|;
end_decl_stmt

begin_decl_stmt
name|Message
name|msg
init|=
name|consumer
operator|.
name|receive
argument_list|(
literal|1000
argument_list|)
decl_stmt|;
end_decl_stmt

begin_if
if|if
condition|(
name|msg
operator|==
literal|null
condition|)
block|{
name|msg
operator|=
name|consumer
operator|.
name|receive
argument_list|(
literal|5000
argument_list|)
expr_stmt|;
block|}
end_if

begin_expr_stmt
name|assertNull
argument_list|(
literal|"no messges left dangling but got: "
operator|+
name|msg
argument_list|,
name|msg
argument_list|)
expr_stmt|;
end_expr_stmt

begin_expr_stmt
name|sendConnection
operator|.
name|close
argument_list|()
expr_stmt|;
end_expr_stmt

begin_function
unit|}       private
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
specifier|final
name|String
name|text
parameter_list|,
specifier|final
name|int
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
name|text
operator|+
literal|", count:"
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
