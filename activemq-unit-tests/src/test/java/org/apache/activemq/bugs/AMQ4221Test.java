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
name|util
operator|.
name|HashSet
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
name|BytesMessage
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
name|junit
operator|.
name|framework
operator|.
name|Test
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
name|ActiveMQPrefetchPolicy
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
name|DestinationStatistics
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
name|FilePendingQueueMessageStoragePolicy
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
name|store
operator|.
name|kahadb
operator|.
name|plist
operator|.
name|PListStoreImpl
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
name|DefaultTestAppender
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
name|Level
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
name|LogManager
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
name|spi
operator|.
name|LoggingEvent
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
name|AMQ4221Test
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
name|AMQ4221Test
operator|.
name|class
argument_list|)
decl_stmt|;
specifier|public
name|int
name|PAYLOAD_SIZE_BYTES
init|=
literal|4
operator|*
literal|1024
decl_stmt|;
specifier|public
name|int
name|NUM_TO_SEND
init|=
literal|60000
decl_stmt|;
specifier|public
name|int
name|NUM_CONCURRENT_PRODUCERS
init|=
literal|20
decl_stmt|;
specifier|public
name|int
name|QUEUE_COUNT
init|=
literal|1
decl_stmt|;
specifier|public
name|int
name|TMP_JOURNAL_MAX_FILE_SIZE
init|=
literal|10
operator|*
literal|1024
operator|*
literal|1024
decl_stmt|;
specifier|public
name|int
name|DLQ_PURGE_INTERVAL
init|=
literal|30000
decl_stmt|;
specifier|public
name|int
name|MESSAGE_TIME_TO_LIVE
init|=
literal|20000
decl_stmt|;
specifier|public
name|int
name|EXPIRE_SWEEP_PERIOD
init|=
literal|200
decl_stmt|;
specifier|public
name|int
name|TMP_JOURNAL_GC_PERIOD
init|=
literal|50
decl_stmt|;
specifier|public
name|int
name|RECEIVE_POLL_PERIOD
init|=
literal|4000
decl_stmt|;
specifier|private
name|int
name|RECEIVE_BATCH
init|=
literal|5000
decl_stmt|;
specifier|final
name|byte
index|[]
name|payload
init|=
operator|new
name|byte
index|[
name|PAYLOAD_SIZE_BYTES
index|]
decl_stmt|;
specifier|final
name|AtomicInteger
name|counter
init|=
operator|new
name|AtomicInteger
argument_list|(
literal|0
argument_list|)
decl_stmt|;
specifier|final
name|HashSet
argument_list|<
name|Throwable
argument_list|>
name|exceptions
init|=
operator|new
name|HashSet
argument_list|<
name|Throwable
argument_list|>
argument_list|()
decl_stmt|;
name|BrokerService
name|brokerService
decl_stmt|;
specifier|private
name|String
name|brokerUrlString
decl_stmt|;
name|ExecutorService
name|executorService
init|=
name|Executors
operator|.
name|newCachedThreadPool
argument_list|()
decl_stmt|;
specifier|final
name|AtomicBoolean
name|done
init|=
operator|new
name|AtomicBoolean
argument_list|(
literal|false
argument_list|)
decl_stmt|;
specifier|public
specifier|static
name|Test
name|suite
parameter_list|()
block|{
return|return
name|suite
argument_list|(
name|AMQ4221Test
operator|.
name|class
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|setUp
parameter_list|()
throws|throws
name|Exception
block|{
name|LogManager
operator|.
name|getRootLogger
argument_list|()
operator|.
name|addAppender
argument_list|(
operator|new
name|DefaultTestAppender
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|void
name|doAppend
parameter_list|(
name|LoggingEvent
name|event
parameter_list|)
block|{
if|if
condition|(
name|event
operator|.
name|getLevel
argument_list|()
operator|.
name|isGreaterOrEqual
argument_list|(
name|Level
operator|.
name|ERROR
argument_list|)
condition|)
block|{
name|System
operator|.
name|err
operator|.
name|println
argument_list|(
literal|"exit on error: "
operator|+
name|event
operator|.
name|getMessage
argument_list|()
argument_list|)
expr_stmt|;
name|done
operator|.
name|set
argument_list|(
literal|true
argument_list|)
expr_stmt|;
operator|new
name|Thread
argument_list|()
block|{
specifier|public
name|void
name|run
parameter_list|()
block|{
name|System
operator|.
name|exit
argument_list|(
literal|787
argument_list|)
expr_stmt|;
block|}
block|}
operator|.
name|start
argument_list|()
expr_stmt|;
block|}
block|}
block|}
argument_list|)
expr_stmt|;
name|done
operator|.
name|set
argument_list|(
literal|false
argument_list|)
expr_stmt|;
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
name|setDestinations
argument_list|(
operator|new
name|ActiveMQDestination
index|[]
block|{
operator|new
name|ActiveMQQueue
argument_list|(
literal|"ActiveMQ.DLQ"
argument_list|)
block|}
argument_list|)
expr_stmt|;
name|PolicyEntry
name|defaultPolicy
init|=
operator|new
name|PolicyEntry
argument_list|()
decl_stmt|;
name|defaultPolicy
operator|.
name|setPendingQueuePolicy
argument_list|(
operator|new
name|FilePendingQueueMessageStoragePolicy
argument_list|()
argument_list|)
expr_stmt|;
name|defaultPolicy
operator|.
name|setExpireMessagesPeriod
argument_list|(
name|EXPIRE_SWEEP_PERIOD
argument_list|)
expr_stmt|;
name|defaultPolicy
operator|.
name|setProducerFlowControl
argument_list|(
literal|false
argument_list|)
expr_stmt|;
name|defaultPolicy
operator|.
name|setMemoryLimit
argument_list|(
literal|50
operator|*
literal|1024
operator|*
literal|1024
argument_list|)
expr_stmt|;
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
literal|50
operator|*
literal|1024
operator|*
literal|1024
argument_list|)
expr_stmt|;
name|PolicyMap
name|destinationPolicyMap
init|=
operator|new
name|PolicyMap
argument_list|()
decl_stmt|;
name|destinationPolicyMap
operator|.
name|setDefaultEntry
argument_list|(
name|defaultPolicy
argument_list|)
expr_stmt|;
name|brokerService
operator|.
name|setDestinationPolicy
argument_list|(
name|destinationPolicyMap
argument_list|)
expr_stmt|;
name|PListStoreImpl
name|tempDataStore
init|=
operator|new
name|PListStoreImpl
argument_list|()
decl_stmt|;
name|tempDataStore
operator|.
name|setDirectory
argument_list|(
name|brokerService
operator|.
name|getTmpDataDirectory
argument_list|()
argument_list|)
expr_stmt|;
name|tempDataStore
operator|.
name|setJournalMaxFileLength
argument_list|(
name|TMP_JOURNAL_MAX_FILE_SIZE
argument_list|)
expr_stmt|;
name|tempDataStore
operator|.
name|setCleanupInterval
argument_list|(
name|TMP_JOURNAL_GC_PERIOD
argument_list|)
expr_stmt|;
name|tempDataStore
operator|.
name|setIndexPageSize
argument_list|(
literal|200
argument_list|)
expr_stmt|;
name|tempDataStore
operator|.
name|setIndexEnablePageCaching
argument_list|(
literal|false
argument_list|)
expr_stmt|;
name|brokerService
operator|.
name|setTempDataStore
argument_list|(
name|tempDataStore
argument_list|)
expr_stmt|;
name|brokerService
operator|.
name|setAdvisorySupport
argument_list|(
literal|false
argument_list|)
expr_stmt|;
name|TransportConnector
name|tcp
init|=
name|brokerService
operator|.
name|addConnector
argument_list|(
literal|"tcp://localhost:0"
argument_list|)
decl_stmt|;
name|brokerService
operator|.
name|start
argument_list|()
expr_stmt|;
name|brokerUrlString
operator|=
name|tcp
operator|.
name|getPublishableConnectString
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|tearDown
parameter_list|()
throws|throws
name|Exception
block|{
name|brokerService
operator|.
name|stop
argument_list|()
expr_stmt|;
name|brokerService
operator|.
name|waitUntilStopped
argument_list|()
expr_stmt|;
name|executorService
operator|.
name|shutdownNow
argument_list|()
expr_stmt|;
block|}
specifier|public
name|void
name|testProduceConsumeExpireHalf
parameter_list|()
throws|throws
name|Exception
block|{
specifier|final
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
name|Queue
name|dlq
init|=
operator|(
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
name|Queue
operator|)
name|getDestination
argument_list|(
name|brokerService
argument_list|,
operator|new
name|ActiveMQQueue
argument_list|(
literal|"ActiveMQ.DLQ"
argument_list|)
argument_list|)
decl_stmt|;
if|if
condition|(
name|DLQ_PURGE_INTERVAL
operator|>
literal|0
condition|)
block|{
name|executorService
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
while|while
condition|(
operator|!
name|done
operator|.
name|get
argument_list|()
condition|)
block|{
try|try
block|{
name|Thread
operator|.
name|sleep
argument_list|(
name|DLQ_PURGE_INTERVAL
argument_list|)
expr_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"Purge DLQ, current size: "
operator|+
name|dlq
operator|.
name|getDestinationStatistics
argument_list|()
operator|.
name|getMessages
argument_list|()
operator|.
name|getCount
argument_list|()
argument_list|)
expr_stmt|;
name|dlq
operator|.
name|purge
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|InterruptedException
name|allDone
parameter_list|)
block|{                         }
catch|catch
parameter_list|(
name|Throwable
name|e
parameter_list|)
block|{
name|e
operator|.
name|printStackTrace
argument_list|()
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
block|}
argument_list|)
expr_stmt|;
block|}
specifier|final
name|CountDownLatch
name|latch
init|=
operator|new
name|CountDownLatch
argument_list|(
name|QUEUE_COUNT
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
name|QUEUE_COUNT
condition|;
name|i
operator|++
control|)
block|{
specifier|final
name|int
name|id
init|=
name|i
decl_stmt|;
name|executorService
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
name|doProduceConsumeExpireHalf
argument_list|(
name|id
argument_list|,
name|latch
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Throwable
name|e
parameter_list|)
block|{
name|e
operator|.
name|printStackTrace
argument_list|()
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
argument_list|)
expr_stmt|;
block|}
while|while
condition|(
operator|!
name|done
operator|.
name|get
argument_list|()
condition|)
block|{
name|done
operator|.
name|set
argument_list|(
name|latch
operator|.
name|await
argument_list|(
literal|5
argument_list|,
name|TimeUnit
operator|.
name|SECONDS
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|executorService
operator|.
name|shutdown
argument_list|()
expr_stmt|;
name|executorService
operator|.
name|awaitTermination
argument_list|(
literal|5
argument_list|,
name|TimeUnit
operator|.
name|MINUTES
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
literal|"no exceptions:"
operator|+
name|exceptions
argument_list|,
name|exceptions
operator|.
name|isEmpty
argument_list|()
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|doProduceConsumeExpireHalf
parameter_list|(
name|int
name|id
parameter_list|,
name|CountDownLatch
name|latch
parameter_list|)
throws|throws
name|Exception
block|{
specifier|final
name|ActiveMQQueue
name|queue
init|=
operator|new
name|ActiveMQQueue
argument_list|(
literal|"Q"
operator|+
name|id
argument_list|)
decl_stmt|;
specifier|final
name|ActiveMQConnectionFactory
name|factory
init|=
operator|new
name|ActiveMQConnectionFactory
argument_list|(
name|brokerUrlString
argument_list|)
decl_stmt|;
name|ActiveMQPrefetchPolicy
name|prefecthPolicy
init|=
operator|new
name|ActiveMQPrefetchPolicy
argument_list|()
decl_stmt|;
name|prefecthPolicy
operator|.
name|setAll
argument_list|(
literal|0
argument_list|)
expr_stmt|;
name|factory
operator|.
name|setPrefetchPolicy
argument_list|(
name|prefecthPolicy
argument_list|)
expr_stmt|;
name|Connection
name|connection
init|=
name|factory
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
name|MessageConsumer
name|consumer
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
operator|.
name|createConsumer
argument_list|(
name|queue
argument_list|,
literal|"on = 'true'"
argument_list|)
decl_stmt|;
name|executorService
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
while|while
condition|(
operator|!
name|done
operator|.
name|get
argument_list|()
condition|)
block|{
name|Thread
operator|.
name|sleep
argument_list|(
name|RECEIVE_POLL_PERIOD
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
name|RECEIVE_BATCH
operator|&&
operator|!
name|done
operator|.
name|get
argument_list|()
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
literal|1000
argument_list|)
decl_stmt|;
if|if
condition|(
name|message
operator|!=
literal|null
condition|)
block|{
name|counter
operator|.
name|incrementAndGet
argument_list|()
expr_stmt|;
if|if
condition|(
name|counter
operator|.
name|get
argument_list|()
operator|>
literal|0
operator|&&
name|counter
operator|.
name|get
argument_list|()
operator|%
literal|500
operator|==
literal|0
condition|)
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"received: "
operator|+
name|counter
operator|.
name|get
argument_list|()
operator|+
literal|", "
operator|+
name|message
operator|.
name|getJMSDestination
argument_list|()
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|}
block|}
catch|catch
parameter_list|(
name|JMSException
name|ignored
parameter_list|)
block|{                  }
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
argument_list|)
expr_stmt|;
specifier|final
name|AtomicInteger
name|accumulator
init|=
operator|new
name|AtomicInteger
argument_list|(
literal|0
argument_list|)
decl_stmt|;
specifier|final
name|CountDownLatch
name|producersDone
init|=
operator|new
name|CountDownLatch
argument_list|(
name|NUM_CONCURRENT_PRODUCERS
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
name|NUM_CONCURRENT_PRODUCERS
condition|;
name|i
operator|++
control|)
block|{
name|executorService
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
name|Connection
name|sendConnection
init|=
name|factory
operator|.
name|createConnection
argument_list|()
decl_stmt|;
name|sendConnection
operator|.
name|start
argument_list|()
expr_stmt|;
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
name|MessageProducer
name|producer
init|=
name|sendSession
operator|.
name|createProducer
argument_list|(
name|queue
argument_list|)
decl_stmt|;
name|producer
operator|.
name|setTimeToLive
argument_list|(
name|MESSAGE_TIME_TO_LIVE
argument_list|)
expr_stmt|;
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
name|accumulator
operator|.
name|incrementAndGet
argument_list|()
operator|<
name|NUM_TO_SEND
operator|&&
operator|!
name|done
operator|.
name|get
argument_list|()
condition|)
block|{
name|BytesMessage
name|message
init|=
name|sendSession
operator|.
name|createBytesMessage
argument_list|()
decl_stmt|;
name|message
operator|.
name|writeBytes
argument_list|(
name|payload
argument_list|)
expr_stmt|;
name|message
operator|.
name|setStringProperty
argument_list|(
literal|"on"
argument_list|,
name|String
operator|.
name|valueOf
argument_list|(
name|accumulator
operator|.
name|get
argument_list|()
operator|%
literal|2
operator|==
literal|0
argument_list|)
argument_list|)
expr_stmt|;
name|producer
operator|.
name|send
argument_list|(
name|message
argument_list|)
expr_stmt|;
block|}
name|producersDone
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
argument_list|)
expr_stmt|;
block|}
name|producersDone
operator|.
name|await
argument_list|(
literal|10
argument_list|,
name|TimeUnit
operator|.
name|MINUTES
argument_list|)
expr_stmt|;
specifier|final
name|DestinationStatistics
name|view
init|=
name|getDestinationStatistics
argument_list|(
name|brokerService
argument_list|,
name|queue
argument_list|)
decl_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"total expired so far "
operator|+
name|view
operator|.
name|getExpired
argument_list|()
operator|.
name|getCount
argument_list|()
operator|+
literal|", "
operator|+
name|queue
operator|.
name|getQueueName
argument_list|()
argument_list|)
expr_stmt|;
name|latch
operator|.
name|countDown
argument_list|()
expr_stmt|;
block|}
block|}
end_class

end_unit
