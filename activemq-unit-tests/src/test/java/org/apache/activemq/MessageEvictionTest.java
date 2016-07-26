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
name|List
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
name|ExecutorService
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
name|Topic
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
name|AdvisorySupport
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
name|ConstantPendingMessageLimitStrategy
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
name|FilePendingSubscriberMessageStoragePolicy
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
name|OldestMessageEvictionStrategy
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
name|PendingSubscriberMessageStoragePolicy
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
name|ActiveMQTopic
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
name|MessageEvictionTest
block|{
specifier|static
specifier|final
name|Logger
name|LOG
init|=
name|LoggerFactory
operator|.
name|getLogger
argument_list|(
name|MessageEvictionTest
operator|.
name|class
argument_list|)
decl_stmt|;
specifier|private
name|BrokerService
name|broker
decl_stmt|;
specifier|private
name|ConnectionFactory
name|connectionFactory
decl_stmt|;
name|Connection
name|connection
decl_stmt|;
specifier|private
name|Session
name|session
decl_stmt|;
specifier|private
name|Topic
name|destination
decl_stmt|;
specifier|private
specifier|final
name|String
name|destinationName
init|=
literal|"verifyEvection"
decl_stmt|;
specifier|protected
name|int
name|numMessages
init|=
literal|2000
decl_stmt|;
specifier|protected
name|String
name|payload
init|=
operator|new
name|String
argument_list|(
operator|new
name|byte
index|[
literal|1024
operator|*
literal|2
index|]
argument_list|)
decl_stmt|;
specifier|public
name|void
name|setUp
parameter_list|(
name|PendingSubscriberMessageStoragePolicy
name|pendingSubscriberPolicy
parameter_list|)
throws|throws
name|Exception
block|{
name|broker
operator|=
name|createBroker
argument_list|(
name|pendingSubscriberPolicy
argument_list|)
expr_stmt|;
name|broker
operator|.
name|start
argument_list|()
expr_stmt|;
name|connectionFactory
operator|=
name|createConnectionFactory
argument_list|()
expr_stmt|;
name|connection
operator|=
name|connectionFactory
operator|.
name|createConnection
argument_list|()
expr_stmt|;
name|connection
operator|.
name|start
argument_list|()
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
name|destination
operator|=
name|session
operator|.
name|createTopic
argument_list|(
name|destinationName
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
name|connection
operator|.
name|stop
argument_list|()
expr_stmt|;
name|broker
operator|.
name|stop
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testMessageEvictionMemoryUsageFileCursor
parameter_list|()
throws|throws
name|Exception
block|{
name|setUp
argument_list|(
operator|new
name|FilePendingSubscriberMessageStoragePolicy
argument_list|()
argument_list|)
expr_stmt|;
name|doTestMessageEvictionMemoryUsage
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testMessageEvictionMemoryUsageVmCursor
parameter_list|()
throws|throws
name|Exception
block|{
name|setUp
argument_list|(
operator|new
name|VMPendingSubscriberMessageStoragePolicy
argument_list|()
argument_list|)
expr_stmt|;
name|doTestMessageEvictionMemoryUsage
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testMessageEvictionDiscardedAdvisory
parameter_list|()
throws|throws
name|Exception
block|{
name|setUp
argument_list|(
operator|new
name|VMPendingSubscriberMessageStoragePolicy
argument_list|()
argument_list|)
expr_stmt|;
name|ExecutorService
name|executor
init|=
name|Executors
operator|.
name|newSingleThreadExecutor
argument_list|()
decl_stmt|;
specifier|final
name|CountDownLatch
name|consumerRegistered
init|=
operator|new
name|CountDownLatch
argument_list|(
literal|1
argument_list|)
decl_stmt|;
specifier|final
name|CountDownLatch
name|gotAdvisory
init|=
operator|new
name|CountDownLatch
argument_list|(
literal|1
argument_list|)
decl_stmt|;
specifier|final
name|CountDownLatch
name|advisoryIsGood
init|=
operator|new
name|CountDownLatch
argument_list|(
literal|1
argument_list|)
decl_stmt|;
name|executor
operator|.
name|execute
argument_list|(
operator|new
name|Runnable
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
name|ActiveMQTopic
name|discardedAdvisoryDestination
init|=
name|AdvisorySupport
operator|.
name|getMessageDiscardedAdvisoryTopic
argument_list|(
name|destination
argument_list|)
decl_stmt|;
comment|// use separate session rather than asyncDispatch on consumer session
comment|// as we want consumer session to block
name|Session
name|advisorySession
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
name|MessageConsumer
name|consumer
init|=
name|advisorySession
operator|.
name|createConsumer
argument_list|(
name|discardedAdvisoryDestination
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
name|int
name|advisoriesReceived
init|=
literal|0
decl_stmt|;
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
literal|"advisory:"
operator|+
name|message
argument_list|)
expr_stmt|;
name|ActiveMQMessage
name|activeMQMessage
init|=
operator|(
name|ActiveMQMessage
operator|)
name|message
decl_stmt|;
name|assertNotNull
argument_list|(
name|activeMQMessage
operator|.
name|getStringProperty
argument_list|(
name|AdvisorySupport
operator|.
name|MSG_PROPERTY_CONSUMER_ID
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
operator|++
name|advisoriesReceived
argument_list|,
name|activeMQMessage
operator|.
name|getIntProperty
argument_list|(
name|AdvisorySupport
operator|.
name|MSG_PROPERTY_DISCARDED_COUNT
argument_list|)
argument_list|)
expr_stmt|;
name|message
operator|.
name|acknowledge
argument_list|()
expr_stmt|;
name|advisoryIsGood
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
finally|finally
block|{
name|gotAdvisory
operator|.
name|countDown
argument_list|()
expr_stmt|;
block|}
block|}
block|}
argument_list|)
expr_stmt|;
name|consumerRegistered
operator|.
name|countDown
argument_list|()
expr_stmt|;
name|gotAdvisory
operator|.
name|await
argument_list|(
literal|120
argument_list|,
name|TimeUnit
operator|.
name|SECONDS
argument_list|)
expr_stmt|;
name|consumer
operator|.
name|close
argument_list|()
expr_stmt|;
name|advisorySession
operator|.
name|close
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
name|e
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
block|}
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
literal|"we have an advisory consumer"
argument_list|,
name|consumerRegistered
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
name|doTestMessageEvictionMemoryUsage
argument_list|()
expr_stmt|;
name|assertTrue
argument_list|(
literal|"got an advisory for discarded"
argument_list|,
name|gotAdvisory
operator|.
name|await
argument_list|(
literal|0
argument_list|,
name|TimeUnit
operator|.
name|SECONDS
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
literal|"advisory is good"
argument_list|,
name|advisoryIsGood
operator|.
name|await
argument_list|(
literal|0
argument_list|,
name|TimeUnit
operator|.
name|SECONDS
argument_list|)
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|doTestMessageEvictionMemoryUsage
parameter_list|()
throws|throws
name|Exception
block|{
name|ExecutorService
name|executor
init|=
name|Executors
operator|.
name|newCachedThreadPool
argument_list|()
decl_stmt|;
specifier|final
name|CountDownLatch
name|doAck
init|=
operator|new
name|CountDownLatch
argument_list|(
literal|1
argument_list|)
decl_stmt|;
specifier|final
name|CountDownLatch
name|ackDone
init|=
operator|new
name|CountDownLatch
argument_list|(
literal|1
argument_list|)
decl_stmt|;
specifier|final
name|CountDownLatch
name|consumerRegistered
init|=
operator|new
name|CountDownLatch
argument_list|(
literal|1
argument_list|)
decl_stmt|;
name|executor
operator|.
name|execute
argument_list|(
operator|new
name|Runnable
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
specifier|final
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
try|try
block|{
comment|// very slow, only ack once
name|doAck
operator|.
name|await
argument_list|(
literal|60
argument_list|,
name|TimeUnit
operator|.
name|SECONDS
argument_list|)
expr_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"acking: "
operator|+
name|message
operator|.
name|getJMSMessageID
argument_list|()
argument_list|)
expr_stmt|;
name|message
operator|.
name|acknowledge
argument_list|()
expr_stmt|;
name|ackDone
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
name|e
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|consumerRegistered
operator|.
name|countDown
argument_list|()
expr_stmt|;
name|ackDone
operator|.
name|countDown
argument_list|()
expr_stmt|;
block|}
block|}
block|}
argument_list|)
expr_stmt|;
name|consumerRegistered
operator|.
name|countDown
argument_list|()
expr_stmt|;
name|ackDone
operator|.
name|await
argument_list|(
literal|60
argument_list|,
name|TimeUnit
operator|.
name|SECONDS
argument_list|)
expr_stmt|;
name|consumer
operator|.
name|close
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
name|e
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
block|}
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
literal|"we have a consumer"
argument_list|,
name|consumerRegistered
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
specifier|final
name|AtomicInteger
name|sent
init|=
operator|new
name|AtomicInteger
argument_list|(
literal|0
argument_list|)
decl_stmt|;
specifier|final
name|CountDownLatch
name|sendDone
init|=
operator|new
name|CountDownLatch
argument_list|(
literal|1
argument_list|)
decl_stmt|;
name|executor
operator|.
name|execute
argument_list|(
operator|new
name|Runnable
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|void
name|run
parameter_list|()
block|{
name|MessageProducer
name|producer
decl_stmt|;
try|try
block|{
name|producer
operator|=
name|session
operator|.
name|createProducer
argument_list|(
name|destination
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
name|numMessages
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
name|payload
argument_list|)
argument_list|)
expr_stmt|;
name|sent
operator|.
name|incrementAndGet
argument_list|()
expr_stmt|;
block|}
name|producer
operator|.
name|close
argument_list|()
expr_stmt|;
name|sendDone
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
name|sendDone
operator|.
name|countDown
argument_list|()
expr_stmt|;
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
block|}
block|}
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
literal|"messages sending done"
argument_list|,
name|sendDone
operator|.
name|await
argument_list|(
literal|180
argument_list|,
name|TimeUnit
operator|.
name|SECONDS
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"all message were sent"
argument_list|,
name|numMessages
argument_list|,
name|sent
operator|.
name|get
argument_list|()
argument_list|)
expr_stmt|;
name|doAck
operator|.
name|countDown
argument_list|()
expr_stmt|;
name|executor
operator|.
name|shutdown
argument_list|()
expr_stmt|;
name|executor
operator|.
name|awaitTermination
argument_list|(
literal|30
argument_list|,
name|TimeUnit
operator|.
name|SECONDS
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
literal|"usage goes to 0 once consumer goes away"
argument_list|,
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
return|return
literal|0
operator|==
name|TestSupport
operator|.
name|getDestination
argument_list|(
name|broker
argument_list|,
name|ActiveMQDestination
operator|.
name|transform
argument_list|(
name|destination
argument_list|)
argument_list|)
operator|.
name|getMemoryUsage
argument_list|()
operator|.
name|getPercentUsage
argument_list|()
return|;
block|}
block|}
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|BrokerService
name|createBroker
parameter_list|(
name|PendingSubscriberMessageStoragePolicy
name|pendingSubscriberPolicy
parameter_list|)
throws|throws
name|Exception
block|{
name|BrokerService
name|brokerService
init|=
operator|new
name|BrokerService
argument_list|()
decl_stmt|;
name|brokerService
operator|.
name|addConnector
argument_list|(
literal|"tcp://localhost:0"
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
name|setDeleteAllMessagesOnStartup
argument_list|(
literal|true
argument_list|)
expr_stmt|;
comment|// spooling to disk early so topic memory limit is not reached
name|brokerService
operator|.
name|getSystemUsage
argument_list|()
operator|.
name|getMemoryUsage
argument_list|()
operator|.
name|setLimit
argument_list|(
literal|500
operator|*
literal|1024
argument_list|)
expr_stmt|;
specifier|final
name|List
argument_list|<
name|PolicyEntry
argument_list|>
name|policyEntries
init|=
operator|new
name|ArrayList
argument_list|<
name|PolicyEntry
argument_list|>
argument_list|()
decl_stmt|;
specifier|final
name|PolicyEntry
name|entry
init|=
operator|new
name|PolicyEntry
argument_list|()
decl_stmt|;
name|entry
operator|.
name|setTopic
argument_list|(
literal|">"
argument_list|)
expr_stmt|;
name|entry
operator|.
name|setAdvisoryForDiscardingMessages
argument_list|(
literal|true
argument_list|)
expr_stmt|;
comment|// so consumer does not get over run while blocked limit the prefetch
name|entry
operator|.
name|setTopicPrefetch
argument_list|(
literal|50
argument_list|)
expr_stmt|;
name|entry
operator|.
name|setPendingSubscriberPolicy
argument_list|(
name|pendingSubscriberPolicy
argument_list|)
expr_stmt|;
comment|// limit the number of outstanding messages, large enough to use the file store
comment|// or small enough not to blow memory limit
name|int
name|pendingMessageLimit
init|=
literal|50
decl_stmt|;
if|if
condition|(
name|pendingSubscriberPolicy
operator|instanceof
name|FilePendingSubscriberMessageStoragePolicy
condition|)
block|{
name|pendingMessageLimit
operator|=
literal|500
expr_stmt|;
block|}
name|ConstantPendingMessageLimitStrategy
name|pendingMessageLimitStrategy
init|=
operator|new
name|ConstantPendingMessageLimitStrategy
argument_list|()
decl_stmt|;
name|pendingMessageLimitStrategy
operator|.
name|setLimit
argument_list|(
name|pendingMessageLimit
argument_list|)
expr_stmt|;
name|entry
operator|.
name|setPendingMessageLimitStrategy
argument_list|(
name|pendingMessageLimitStrategy
argument_list|)
expr_stmt|;
comment|// to keep the limit in check and up to date rather than just the first few, evict some
name|OldestMessageEvictionStrategy
name|messageEvictionStrategy
init|=
operator|new
name|OldestMessageEvictionStrategy
argument_list|()
decl_stmt|;
comment|// whether to check expiry before eviction, default limit 1000 is fine as no ttl set in this test
comment|//messageEvictionStrategy.setEvictExpiredMessagesHighWatermark(1000);
name|entry
operator|.
name|setMessageEvictionStrategy
argument_list|(
name|messageEvictionStrategy
argument_list|)
expr_stmt|;
comment|// let evicted messaged disappear
name|entry
operator|.
name|setDeadLetterStrategy
argument_list|(
literal|null
argument_list|)
expr_stmt|;
name|policyEntries
operator|.
name|add
argument_list|(
name|entry
argument_list|)
expr_stmt|;
specifier|final
name|PolicyMap
name|policyMap
init|=
operator|new
name|PolicyMap
argument_list|()
decl_stmt|;
name|policyMap
operator|.
name|setPolicyEntries
argument_list|(
name|policyEntries
argument_list|)
expr_stmt|;
name|brokerService
operator|.
name|setDestinationPolicy
argument_list|(
name|policyMap
argument_list|)
expr_stmt|;
return|return
name|brokerService
return|;
block|}
name|ConnectionFactory
name|createConnectionFactory
parameter_list|()
throws|throws
name|Exception
block|{
name|String
name|url
init|=
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
name|getServer
argument_list|()
operator|.
name|getConnectURI
argument_list|()
operator|.
name|toString
argument_list|()
decl_stmt|;
name|ActiveMQConnectionFactory
name|factory
init|=
operator|new
name|ActiveMQConnectionFactory
argument_list|(
name|url
argument_list|)
decl_stmt|;
name|factory
operator|.
name|setWatchTopicAdvisories
argument_list|(
literal|false
argument_list|)
expr_stmt|;
return|return
name|factory
return|;
block|}
block|}
end_class

end_unit

