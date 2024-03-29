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
operator|.
name|kahadb
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
name|AtomicLong
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
name|ConnectionControl
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
name|disk
operator|.
name|journal
operator|.
name|FileAppender
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
name|disk
operator|.
name|journal
operator|.
name|Journal
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
name|KahaDBFastEnqueueTest
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
name|KahaDBFastEnqueueTest
operator|.
name|class
argument_list|)
decl_stmt|;
specifier|private
name|BrokerService
name|broker
decl_stmt|;
specifier|private
name|ActiveMQConnectionFactory
name|connectionFactory
decl_stmt|;
name|KahaDBPersistenceAdapter
name|kahaDBPersistenceAdapter
decl_stmt|;
specifier|private
specifier|final
name|Destination
name|destination
init|=
operator|new
name|ActiveMQQueue
argument_list|(
literal|"Test"
argument_list|)
decl_stmt|;
specifier|private
specifier|final
name|String
name|payloadString
init|=
operator|new
name|String
argument_list|(
operator|new
name|byte
index|[
literal|6
operator|*
literal|1024
index|]
argument_list|)
decl_stmt|;
specifier|private
specifier|final
name|boolean
name|useBytesMessage
init|=
literal|true
decl_stmt|;
specifier|private
specifier|final
name|int
name|parallelProducer
init|=
literal|20
decl_stmt|;
specifier|private
specifier|final
name|Vector
argument_list|<
name|Exception
argument_list|>
name|exceptions
init|=
operator|new
name|Vector
argument_list|<
name|Exception
argument_list|>
argument_list|()
decl_stmt|;
name|long
name|toSend
init|=
literal|10000
decl_stmt|;
comment|// use with:
comment|// -Xmx4g -Dorg.apache.kahadb.journal.appender.WRITE_STAT_WINDOW=10000 -Dorg.apache.kahadb.journal.CALLER_BUFFER_APPENDER=true
annotation|@
name|Test
specifier|public
name|void
name|testPublishNoConsumer
parameter_list|()
throws|throws
name|Exception
block|{
name|startBroker
argument_list|(
literal|true
argument_list|,
literal|10
argument_list|)
expr_stmt|;
specifier|final
name|AtomicLong
name|sharedCount
init|=
operator|new
name|AtomicLong
argument_list|(
name|toSend
argument_list|)
decl_stmt|;
name|long
name|start
init|=
name|System
operator|.
name|currentTimeMillis
argument_list|()
decl_stmt|;
name|ExecutorService
name|executorService
init|=
name|Executors
operator|.
name|newCachedThreadPool
argument_list|()
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
name|parallelProducer
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
name|publishMessages
argument_list|(
name|sharedCount
argument_list|,
literal|0
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
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
name|executorService
operator|.
name|shutdown
argument_list|()
expr_stmt|;
name|executorService
operator|.
name|awaitTermination
argument_list|(
literal|30
argument_list|,
name|TimeUnit
operator|.
name|MINUTES
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
literal|"Producers done in time"
argument_list|,
name|executorService
operator|.
name|isTerminated
argument_list|()
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
literal|"No exceptions: "
operator|+
name|exceptions
argument_list|,
name|exceptions
operator|.
name|isEmpty
argument_list|()
argument_list|)
expr_stmt|;
name|long
name|totalSent
init|=
name|toSend
operator|*
name|payloadString
operator|.
name|length
argument_list|()
decl_stmt|;
name|double
name|duration
init|=
name|System
operator|.
name|currentTimeMillis
argument_list|()
operator|-
name|start
decl_stmt|;
name|stopBroker
argument_list|()
expr_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"Duration:                "
operator|+
name|duration
operator|+
literal|"ms"
argument_list|)
expr_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"Rate:                       "
operator|+
operator|(
name|toSend
operator|*
literal|1000
operator|/
name|duration
operator|)
operator|+
literal|"m/s"
argument_list|)
expr_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"Total send:             "
operator|+
name|totalSent
argument_list|)
expr_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"Total journal write: "
operator|+
name|kahaDBPersistenceAdapter
operator|.
name|getStore
argument_list|()
operator|.
name|getJournal
argument_list|()
operator|.
name|length
argument_list|()
argument_list|)
expr_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"Total index size "
operator|+
name|kahaDBPersistenceAdapter
operator|.
name|getStore
argument_list|()
operator|.
name|getPageFile
argument_list|()
operator|.
name|getDiskSize
argument_list|()
argument_list|)
expr_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"Total store size: "
operator|+
name|kahaDBPersistenceAdapter
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"Journal writes %:    "
operator|+
name|kahaDBPersistenceAdapter
operator|.
name|getStore
argument_list|()
operator|.
name|getJournal
argument_list|()
operator|.
name|length
argument_list|()
operator|/
operator|(
name|double
operator|)
name|totalSent
operator|*
literal|100
operator|+
literal|"%"
argument_list|)
expr_stmt|;
name|restartBroker
argument_list|(
literal|0
argument_list|,
literal|1200000
argument_list|)
expr_stmt|;
name|consumeMessages
argument_list|(
name|toSend
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testPublishNoConsumerNoCheckpoint
parameter_list|()
throws|throws
name|Exception
block|{
name|toSend
operator|=
literal|100
expr_stmt|;
name|startBroker
argument_list|(
literal|true
argument_list|,
literal|0
argument_list|)
expr_stmt|;
specifier|final
name|AtomicLong
name|sharedCount
init|=
operator|new
name|AtomicLong
argument_list|(
name|toSend
argument_list|)
decl_stmt|;
name|long
name|start
init|=
name|System
operator|.
name|currentTimeMillis
argument_list|()
decl_stmt|;
name|ExecutorService
name|executorService
init|=
name|Executors
operator|.
name|newCachedThreadPool
argument_list|()
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
name|parallelProducer
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
name|publishMessages
argument_list|(
name|sharedCount
argument_list|,
literal|0
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
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
name|executorService
operator|.
name|shutdown
argument_list|()
expr_stmt|;
name|executorService
operator|.
name|awaitTermination
argument_list|(
literal|30
argument_list|,
name|TimeUnit
operator|.
name|MINUTES
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
literal|"Producers done in time"
argument_list|,
name|executorService
operator|.
name|isTerminated
argument_list|()
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
literal|"No exceptions: "
operator|+
name|exceptions
argument_list|,
name|exceptions
operator|.
name|isEmpty
argument_list|()
argument_list|)
expr_stmt|;
name|long
name|totalSent
init|=
name|toSend
operator|*
name|payloadString
operator|.
name|length
argument_list|()
decl_stmt|;
name|broker
operator|.
name|getAdminView
argument_list|()
operator|.
name|gc
argument_list|()
expr_stmt|;
name|double
name|duration
init|=
name|System
operator|.
name|currentTimeMillis
argument_list|()
operator|-
name|start
decl_stmt|;
name|stopBroker
argument_list|()
expr_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"Duration:                "
operator|+
name|duration
operator|+
literal|"ms"
argument_list|)
expr_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"Rate:                       "
operator|+
operator|(
name|toSend
operator|*
literal|1000
operator|/
name|duration
operator|)
operator|+
literal|"m/s"
argument_list|)
expr_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"Total send:             "
operator|+
name|totalSent
argument_list|)
expr_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"Total journal write: "
operator|+
name|kahaDBPersistenceAdapter
operator|.
name|getStore
argument_list|()
operator|.
name|getJournal
argument_list|()
operator|.
name|length
argument_list|()
argument_list|)
expr_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"Total index size "
operator|+
name|kahaDBPersistenceAdapter
operator|.
name|getStore
argument_list|()
operator|.
name|getPageFile
argument_list|()
operator|.
name|getDiskSize
argument_list|()
argument_list|)
expr_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"Total store size: "
operator|+
name|kahaDBPersistenceAdapter
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"Journal writes %:    "
operator|+
name|kahaDBPersistenceAdapter
operator|.
name|getStore
argument_list|()
operator|.
name|getJournal
argument_list|()
operator|.
name|length
argument_list|()
operator|/
operator|(
name|double
operator|)
name|totalSent
operator|*
literal|100
operator|+
literal|"%"
argument_list|)
expr_stmt|;
name|restartBroker
argument_list|(
literal|0
argument_list|,
literal|0
argument_list|)
expr_stmt|;
name|consumeMessages
argument_list|(
name|toSend
argument_list|)
expr_stmt|;
block|}
specifier|private
name|void
name|consumeMessages
parameter_list|(
name|long
name|count
parameter_list|)
throws|throws
name|Exception
block|{
name|ActiveMQConnection
name|connection
init|=
operator|(
name|ActiveMQConnection
operator|)
name|connectionFactory
operator|.
name|createConnection
argument_list|()
decl_stmt|;
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
name|consumer
init|=
name|session
operator|.
name|createConsumer
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
name|assertNotNull
argument_list|(
literal|"got message "
operator|+
name|i
argument_list|,
name|consumer
operator|.
name|receive
argument_list|(
literal|10000
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|assertNull
argument_list|(
literal|"none left over"
argument_list|,
name|consumer
operator|.
name|receive
argument_list|(
literal|2000
argument_list|)
argument_list|)
expr_stmt|;
block|}
specifier|private
name|void
name|restartBroker
parameter_list|(
name|int
name|restartDelay
parameter_list|,
name|int
name|checkpoint
parameter_list|)
throws|throws
name|Exception
block|{
name|stopBroker
argument_list|()
expr_stmt|;
name|TimeUnit
operator|.
name|MILLISECONDS
operator|.
name|sleep
argument_list|(
name|restartDelay
argument_list|)
expr_stmt|;
name|startBroker
argument_list|(
literal|false
argument_list|,
name|checkpoint
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Before
specifier|public
name|void
name|setProps
parameter_list|()
block|{
name|System
operator|.
name|setProperty
argument_list|(
name|Journal
operator|.
name|CALLER_BUFFER_APPENDER
argument_list|,
name|Boolean
operator|.
name|toString
argument_list|(
literal|true
argument_list|)
argument_list|)
expr_stmt|;
name|System
operator|.
name|setProperty
argument_list|(
name|FileAppender
operator|.
name|PROPERTY_LOG_WRITE_STAT_WINDOW
argument_list|,
literal|"10000"
argument_list|)
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
name|broker
operator|.
name|waitUntilStopped
argument_list|()
expr_stmt|;
block|}
name|System
operator|.
name|clearProperty
argument_list|(
name|Journal
operator|.
name|CALLER_BUFFER_APPENDER
argument_list|)
expr_stmt|;
name|System
operator|.
name|clearProperty
argument_list|(
name|FileAppender
operator|.
name|PROPERTY_LOG_WRITE_STAT_WINDOW
argument_list|)
expr_stmt|;
block|}
specifier|final
name|double
name|sampleRate
init|=
literal|100000
decl_stmt|;
specifier|private
name|void
name|publishMessages
parameter_list|(
name|AtomicLong
name|count
parameter_list|,
name|int
name|expiry
parameter_list|)
throws|throws
name|Exception
block|{
name|ActiveMQConnection
name|connection
init|=
operator|(
name|ActiveMQConnection
operator|)
name|connectionFactory
operator|.
name|createConnection
argument_list|()
decl_stmt|;
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
name|Long
name|start
init|=
name|System
operator|.
name|currentTimeMillis
argument_list|()
decl_stmt|;
name|long
name|i
init|=
literal|0l
decl_stmt|;
while|while
condition|(
operator|(
name|i
operator|=
name|count
operator|.
name|getAndDecrement
argument_list|()
operator|)
operator|>
literal|0
condition|)
block|{
name|Message
name|message
init|=
literal|null
decl_stmt|;
if|if
condition|(
name|useBytesMessage
condition|)
block|{
name|message
operator|=
name|session
operator|.
name|createBytesMessage
argument_list|()
expr_stmt|;
operator|(
operator|(
name|BytesMessage
operator|)
name|message
operator|)
operator|.
name|writeBytes
argument_list|(
name|payloadString
operator|.
name|getBytes
argument_list|()
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|message
operator|=
name|session
operator|.
name|createTextMessage
argument_list|(
name|payloadString
argument_list|)
expr_stmt|;
block|}
name|producer
operator|.
name|send
argument_list|(
name|message
argument_list|,
name|DeliveryMode
operator|.
name|PERSISTENT
argument_list|,
literal|5
argument_list|,
name|expiry
argument_list|)
expr_stmt|;
if|if
condition|(
name|i
operator|!=
name|toSend
operator|&&
name|i
operator|%
name|sampleRate
operator|==
literal|0
condition|)
block|{
name|long
name|now
init|=
name|System
operator|.
name|currentTimeMillis
argument_list|()
decl_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"Remainder: "
operator|+
name|i
operator|+
literal|", rate: "
operator|+
name|sampleRate
operator|*
literal|1000
operator|/
operator|(
name|now
operator|-
name|start
operator|)
operator|+
literal|"m/s"
argument_list|)
expr_stmt|;
name|start
operator|=
name|now
expr_stmt|;
block|}
block|}
name|connection
operator|.
name|syncSendPacket
argument_list|(
operator|new
name|ConnectionControl
argument_list|()
argument_list|)
expr_stmt|;
name|connection
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
specifier|public
name|void
name|startBroker
parameter_list|(
name|boolean
name|deleteAllMessages
parameter_list|,
name|int
name|checkPointPeriod
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
name|setDeleteAllMessagesOnStartup
argument_list|(
name|deleteAllMessages
argument_list|)
expr_stmt|;
name|kahaDBPersistenceAdapter
operator|=
operator|(
name|KahaDBPersistenceAdapter
operator|)
name|broker
operator|.
name|getPersistenceAdapter
argument_list|()
expr_stmt|;
name|kahaDBPersistenceAdapter
operator|.
name|setEnableJournalDiskSyncs
argument_list|(
literal|false
argument_list|)
expr_stmt|;
comment|// defer checkpoints which require a sync
name|kahaDBPersistenceAdapter
operator|.
name|setCleanupInterval
argument_list|(
name|checkPointPeriod
argument_list|)
expr_stmt|;
name|kahaDBPersistenceAdapter
operator|.
name|setCheckpointInterval
argument_list|(
name|checkPointPeriod
argument_list|)
expr_stmt|;
comment|// optimise for disk best batch rate
name|kahaDBPersistenceAdapter
operator|.
name|setJournalMaxWriteBatchSize
argument_list|(
literal|24
operator|*
literal|1024
operator|*
literal|1024
argument_list|)
expr_stmt|;
comment|//4mb default
name|kahaDBPersistenceAdapter
operator|.
name|setJournalMaxFileLength
argument_list|(
literal|128
operator|*
literal|1024
operator|*
literal|1024
argument_list|)
expr_stmt|;
comment|// 32mb default
comment|// keep index in memory
name|kahaDBPersistenceAdapter
operator|.
name|setIndexCacheSize
argument_list|(
literal|500000
argument_list|)
expr_stmt|;
name|kahaDBPersistenceAdapter
operator|.
name|setIndexWriteBatchSize
argument_list|(
literal|500000
argument_list|)
expr_stmt|;
name|kahaDBPersistenceAdapter
operator|.
name|setEnableIndexRecoveryFile
argument_list|(
literal|false
argument_list|)
expr_stmt|;
name|kahaDBPersistenceAdapter
operator|.
name|setEnableIndexDiskSyncs
argument_list|(
literal|false
argument_list|)
expr_stmt|;
name|broker
operator|.
name|addConnector
argument_list|(
literal|"tcp://0.0.0.0:0"
argument_list|)
expr_stmt|;
name|broker
operator|.
name|start
argument_list|()
expr_stmt|;
name|String
name|options
init|=
literal|"?jms.watchTopicAdvisories=false&jms.useAsyncSend=true&jms.alwaysSessionAsync=false&jms.dispatchAsync=false&socketBufferSize=131072&ioBufferSize=16384&wireFormat.tightEncodingEnabled=false&wireFormat.cacheSize=8192"
decl_stmt|;
name|connectionFactory
operator|=
operator|new
name|ActiveMQConnectionFactory
argument_list|(
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
operator|+
name|options
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testRollover
parameter_list|()
throws|throws
name|Exception
block|{
name|byte
name|flip
init|=
literal|0x1
decl_stmt|;
for|for
control|(
name|long
name|i
init|=
literal|0
init|;
name|i
operator|<
name|Short
operator|.
name|MAX_VALUE
condition|;
name|i
operator|++
control|)
block|{
name|assertEquals
argument_list|(
literal|"0 @:"
operator|+
name|i
argument_list|,
literal|0
argument_list|,
name|flip
operator|^=
literal|1
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"1 @:"
operator|+
name|i
argument_list|,
literal|1
argument_list|,
name|flip
operator|^=
literal|1
argument_list|)
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

