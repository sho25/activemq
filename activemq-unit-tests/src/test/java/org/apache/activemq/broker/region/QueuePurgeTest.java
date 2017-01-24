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
name|broker
operator|.
name|region
package|;
end_package

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|File
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
name|javax
operator|.
name|management
operator|.
name|MalformedObjectNameException
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|management
operator|.
name|ObjectName
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
name|CombinationTestSupport
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
name|jmx
operator|.
name|QueueViewMBean
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
name|PendingQueueMessageStoragePolicy
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
name|store
operator|.
name|kahadb
operator|.
name|KahaDBPersistenceAdapter
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
name|Appender
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
name|QueuePurgeTest
extends|extends
name|CombinationTestSupport
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
name|QueuePurgeTest
operator|.
name|class
argument_list|)
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|int
name|NUM_TO_SEND
init|=
literal|20000
decl_stmt|;
specifier|private
specifier|final
name|String
name|MESSAGE_TEXT
init|=
operator|new
name|String
argument_list|(
operator|new
name|byte
index|[
literal|1024
index|]
argument_list|)
decl_stmt|;
name|BrokerService
name|broker
decl_stmt|;
name|ConnectionFactory
name|factory
decl_stmt|;
name|Connection
name|connection
decl_stmt|;
name|Session
name|session
decl_stmt|;
name|Queue
name|queue
decl_stmt|;
name|MessageConsumer
name|consumer
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
name|setMaxTestTime
argument_list|(
literal|10
operator|*
literal|60
operator|*
literal|1000
argument_list|)
expr_stmt|;
comment|// 10 mins
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
name|broker
operator|=
operator|new
name|BrokerService
argument_list|()
expr_stmt|;
name|File
name|testDataDir
init|=
operator|new
name|File
argument_list|(
literal|"target/activemq-data/QueuePurgeTest"
argument_list|)
decl_stmt|;
name|broker
operator|.
name|setDataDirectoryFile
argument_list|(
name|testDataDir
argument_list|)
expr_stmt|;
name|broker
operator|.
name|setUseJmx
argument_list|(
literal|true
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
name|getSystemUsage
argument_list|()
operator|.
name|getMemoryUsage
argument_list|()
operator|.
name|setLimit
argument_list|(
literal|1024l
operator|*
literal|1024
operator|*
literal|64
argument_list|)
expr_stmt|;
name|KahaDBPersistenceAdapter
name|persistenceAdapter
init|=
operator|new
name|KahaDBPersistenceAdapter
argument_list|()
decl_stmt|;
name|persistenceAdapter
operator|.
name|setDirectory
argument_list|(
operator|new
name|File
argument_list|(
name|testDataDir
argument_list|,
literal|"kahadb"
argument_list|)
argument_list|)
expr_stmt|;
name|broker
operator|.
name|setPersistenceAdapter
argument_list|(
name|persistenceAdapter
argument_list|)
expr_stmt|;
name|broker
operator|.
name|addConnector
argument_list|(
literal|"tcp://localhost:0"
argument_list|)
expr_stmt|;
name|broker
operator|.
name|start
argument_list|()
expr_stmt|;
name|factory
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
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|connection
operator|=
name|factory
operator|.
name|createConnection
argument_list|()
expr_stmt|;
name|connection
operator|.
name|start
argument_list|()
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
name|super
operator|.
name|tearDown
argument_list|()
expr_stmt|;
if|if
condition|(
name|consumer
operator|!=
literal|null
condition|)
block|{
name|consumer
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
name|session
operator|.
name|close
argument_list|()
expr_stmt|;
name|connection
operator|.
name|stop
argument_list|()
expr_stmt|;
name|connection
operator|.
name|close
argument_list|()
expr_stmt|;
name|broker
operator|.
name|stop
argument_list|()
expr_stmt|;
block|}
specifier|public
name|void
name|testPurgeLargeQueue
parameter_list|()
throws|throws
name|Exception
block|{
name|testPurgeLargeQueue
argument_list|(
literal|false
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|testPurgeLargeQueuePrioritizedMessages
parameter_list|()
throws|throws
name|Exception
block|{
name|testPurgeLargeQueue
argument_list|(
literal|true
argument_list|)
expr_stmt|;
block|}
specifier|private
name|void
name|testPurgeLargeQueue
parameter_list|(
name|boolean
name|prioritizedMessages
parameter_list|)
throws|throws
name|Exception
block|{
name|applyBrokerSpoolingPolicy
argument_list|(
name|prioritizedMessages
argument_list|)
expr_stmt|;
name|createProducerAndSendMessages
argument_list|(
name|NUM_TO_SEND
argument_list|)
expr_stmt|;
name|QueueViewMBean
name|proxy
init|=
name|getProxyToQueueViewMBean
argument_list|()
decl_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"purging.."
argument_list|)
expr_stmt|;
name|org
operator|.
name|apache
operator|.
name|log4j
operator|.
name|Logger
name|log4jLogger
init|=
name|org
operator|.
name|apache
operator|.
name|log4j
operator|.
name|Logger
operator|.
name|getLogger
argument_list|(
name|org
operator|.
name|apache
operator|.
name|activemq
operator|.
name|broker
operator|.
name|jmx
operator|.
name|QueueView
operator|.
name|class
argument_list|)
decl_stmt|;
specifier|final
name|AtomicBoolean
name|gotPurgeLogMessage
init|=
operator|new
name|AtomicBoolean
argument_list|(
literal|false
argument_list|)
decl_stmt|;
name|Appender
name|appender
init|=
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
name|getMessage
argument_list|()
operator|instanceof
name|String
condition|)
block|{
name|String
name|message
init|=
operator|(
name|String
operator|)
name|event
operator|.
name|getMessage
argument_list|()
decl_stmt|;
if|if
condition|(
name|message
operator|.
name|contains
argument_list|(
literal|"purge of "
operator|+
name|NUM_TO_SEND
operator|+
literal|" messages"
argument_list|)
condition|)
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"Received a log message: {} "
argument_list|,
name|event
operator|.
name|getMessage
argument_list|()
argument_list|)
expr_stmt|;
name|gotPurgeLogMessage
operator|.
name|set
argument_list|(
literal|true
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|}
decl_stmt|;
name|Level
name|level
init|=
name|log4jLogger
operator|.
name|getLevel
argument_list|()
decl_stmt|;
name|log4jLogger
operator|.
name|setLevel
argument_list|(
name|Level
operator|.
name|INFO
argument_list|)
expr_stmt|;
name|log4jLogger
operator|.
name|addAppender
argument_list|(
name|appender
argument_list|)
expr_stmt|;
try|try
block|{
name|proxy
operator|.
name|purge
argument_list|()
expr_stmt|;
block|}
finally|finally
block|{
name|log4jLogger
operator|.
name|setLevel
argument_list|(
name|level
argument_list|)
expr_stmt|;
name|log4jLogger
operator|.
name|removeAppender
argument_list|(
name|appender
argument_list|)
expr_stmt|;
block|}
name|assertEquals
argument_list|(
literal|"Queue size is not zero, it's "
operator|+
name|proxy
operator|.
name|getQueueSize
argument_list|()
argument_list|,
literal|0
argument_list|,
name|proxy
operator|.
name|getQueueSize
argument_list|()
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
literal|"cache is disabled, temp store being used"
argument_list|,
operator|!
name|proxy
operator|.
name|isCacheEnabled
argument_list|()
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
literal|"got expected info purge log message"
argument_list|,
name|gotPurgeLogMessage
operator|.
name|get
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"Found messages when browsing"
argument_list|,
literal|0
argument_list|,
name|proxy
operator|.
name|browseMessages
argument_list|()
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|testRepeatedExpiryProcessingOfLargeQueue
parameter_list|()
throws|throws
name|Exception
block|{
name|applyBrokerSpoolingPolicy
argument_list|(
literal|false
argument_list|)
expr_stmt|;
specifier|final
name|int
name|expiryPeriod
init|=
literal|500
decl_stmt|;
name|applyExpiryDuration
argument_list|(
name|expiryPeriod
argument_list|)
expr_stmt|;
name|createProducerAndSendMessages
argument_list|(
name|NUM_TO_SEND
argument_list|)
expr_stmt|;
name|QueueViewMBean
name|proxy
init|=
name|getProxyToQueueViewMBean
argument_list|()
decl_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"waiting for expiry to kick in a bunch of times to verify it does not blow mem"
argument_list|)
expr_stmt|;
name|Thread
operator|.
name|sleep
argument_list|(
literal|5000
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"Queue size is has not changed "
operator|+
name|proxy
operator|.
name|getQueueSize
argument_list|()
argument_list|,
name|NUM_TO_SEND
argument_list|,
name|proxy
operator|.
name|getQueueSize
argument_list|()
argument_list|)
expr_stmt|;
block|}
specifier|private
name|void
name|applyExpiryDuration
parameter_list|(
name|int
name|i
parameter_list|)
block|{
name|broker
operator|.
name|getDestinationPolicy
argument_list|()
operator|.
name|getDefaultEntry
argument_list|()
operator|.
name|setExpireMessagesPeriod
argument_list|(
name|i
argument_list|)
expr_stmt|;
block|}
specifier|private
name|void
name|applyBrokerSpoolingPolicy
parameter_list|(
name|boolean
name|prioritizedMessages
parameter_list|)
block|{
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
name|defaultEntry
operator|.
name|setPrioritizedMessages
argument_list|(
name|prioritizedMessages
argument_list|)
expr_stmt|;
name|defaultEntry
operator|.
name|setProducerFlowControl
argument_list|(
literal|false
argument_list|)
expr_stmt|;
name|PendingQueueMessageStoragePolicy
name|pendingQueuePolicy
init|=
operator|new
name|FilePendingQueueMessageStoragePolicy
argument_list|()
decl_stmt|;
name|defaultEntry
operator|.
name|setPendingQueuePolicy
argument_list|(
name|pendingQueuePolicy
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
block|}
specifier|public
name|void
name|testPurgeLargeQueueWithConsumer
parameter_list|()
throws|throws
name|Exception
block|{
name|testPurgeLargeQueueWithConsumer
argument_list|(
literal|false
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|testPurgeLargeQueueWithConsumerPrioritizedMessages
parameter_list|()
throws|throws
name|Exception
block|{
name|testPurgeLargeQueueWithConsumer
argument_list|(
literal|true
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|testConcurrentPurgeAndSend
parameter_list|()
throws|throws
name|Exception
block|{
name|testConcurrentPurgeAndSend
argument_list|(
literal|false
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|testConcurrentPurgeAndSendPrioritizedMessages
parameter_list|()
throws|throws
name|Exception
block|{
name|testConcurrentPurgeAndSend
argument_list|(
literal|true
argument_list|)
expr_stmt|;
block|}
specifier|private
name|void
name|testConcurrentPurgeAndSend
parameter_list|(
name|boolean
name|prioritizedMessages
parameter_list|)
throws|throws
name|Exception
block|{
name|applyBrokerSpoolingPolicy
argument_list|(
literal|false
argument_list|)
expr_stmt|;
name|createProducerAndSendMessages
argument_list|(
name|NUM_TO_SEND
operator|/
literal|2
argument_list|)
expr_stmt|;
name|QueueViewMBean
name|proxy
init|=
name|getProxyToQueueViewMBean
argument_list|()
decl_stmt|;
name|createConsumer
argument_list|()
expr_stmt|;
name|long
name|start
init|=
name|System
operator|.
name|currentTimeMillis
argument_list|()
decl_stmt|;
name|ExecutorService
name|service
init|=
name|Executors
operator|.
name|newFixedThreadPool
argument_list|(
literal|1
argument_list|)
decl_stmt|;
try|try
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"purging.."
argument_list|)
expr_stmt|;
name|service
operator|.
name|submit
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
name|proxy
operator|.
name|purge
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
name|fail
argument_list|(
name|e
operator|.
name|getMessage
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|LOG
operator|.
name|info
argument_list|(
literal|"purge done: "
operator|+
operator|(
name|System
operator|.
name|currentTimeMillis
argument_list|()
operator|-
name|start
operator|)
operator|+
literal|"ms"
argument_list|)
expr_stmt|;
block|}
block|}
argument_list|)
expr_stmt|;
comment|//send should get blocked while purge is running
comment|//which should ensure the metrics are correct
name|createProducerAndSendMessages
argument_list|(
name|NUM_TO_SEND
operator|/
literal|2
argument_list|)
expr_stmt|;
name|Message
name|msg
decl_stmt|;
do|do
block|{
name|msg
operator|=
name|consumer
operator|.
name|receive
argument_list|(
literal|1000
argument_list|)
expr_stmt|;
if|if
condition|(
name|msg
operator|!=
literal|null
condition|)
block|{
name|msg
operator|.
name|acknowledge
argument_list|()
expr_stmt|;
block|}
block|}
do|while
condition|(
name|msg
operator|!=
literal|null
condition|)
do|;
name|assertEquals
argument_list|(
literal|"Queue size not valid"
argument_list|,
literal|0
argument_list|,
name|proxy
operator|.
name|getQueueSize
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"Found messages when browsing"
argument_list|,
literal|0
argument_list|,
name|proxy
operator|.
name|browseMessages
argument_list|()
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|service
operator|.
name|shutdownNow
argument_list|()
expr_stmt|;
block|}
block|}
specifier|private
name|void
name|testPurgeLargeQueueWithConsumer
parameter_list|(
name|boolean
name|prioritizedMessages
parameter_list|)
throws|throws
name|Exception
block|{
name|applyBrokerSpoolingPolicy
argument_list|(
name|prioritizedMessages
argument_list|)
expr_stmt|;
name|createProducerAndSendMessages
argument_list|(
name|NUM_TO_SEND
argument_list|)
expr_stmt|;
name|QueueViewMBean
name|proxy
init|=
name|getProxyToQueueViewMBean
argument_list|()
decl_stmt|;
name|createConsumer
argument_list|()
expr_stmt|;
name|long
name|start
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
literal|"purging.."
argument_list|)
expr_stmt|;
name|proxy
operator|.
name|purge
argument_list|()
expr_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"purge done: "
operator|+
operator|(
name|System
operator|.
name|currentTimeMillis
argument_list|()
operator|-
name|start
operator|)
operator|+
literal|"ms"
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"Queue size is not zero, it's "
operator|+
name|proxy
operator|.
name|getQueueSize
argument_list|()
argument_list|,
literal|0
argument_list|,
name|proxy
operator|.
name|getQueueSize
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"usage goes to duck"
argument_list|,
literal|0
argument_list|,
name|proxy
operator|.
name|getMemoryPercentUsage
argument_list|()
argument_list|)
expr_stmt|;
name|Message
name|msg
decl_stmt|;
do|do
block|{
name|msg
operator|=
name|consumer
operator|.
name|receive
argument_list|(
literal|1000
argument_list|)
expr_stmt|;
if|if
condition|(
name|msg
operator|!=
literal|null
condition|)
block|{
name|msg
operator|.
name|acknowledge
argument_list|()
expr_stmt|;
block|}
block|}
do|while
condition|(
name|msg
operator|!=
literal|null
condition|)
do|;
name|assertEquals
argument_list|(
literal|"Queue size not valid"
argument_list|,
literal|0
argument_list|,
name|proxy
operator|.
name|getQueueSize
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"Found messages when browsing"
argument_list|,
literal|0
argument_list|,
name|proxy
operator|.
name|browseMessages
argument_list|()
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
block|}
specifier|private
name|QueueViewMBean
name|getProxyToQueueViewMBean
parameter_list|()
throws|throws
name|MalformedObjectNameException
throws|,
name|JMSException
block|{
name|ObjectName
name|queueViewMBeanName
init|=
operator|new
name|ObjectName
argument_list|(
literal|"org.apache.activemq:type=Broker,brokerName=localhost,destinationType=Queue,destinationName="
operator|+
name|queue
operator|.
name|getQueueName
argument_list|()
argument_list|)
decl_stmt|;
name|QueueViewMBean
name|proxy
init|=
operator|(
name|QueueViewMBean
operator|)
name|broker
operator|.
name|getManagementContext
argument_list|()
operator|.
name|newProxyInstance
argument_list|(
name|queueViewMBeanName
argument_list|,
name|QueueViewMBean
operator|.
name|class
argument_list|,
literal|true
argument_list|)
decl_stmt|;
return|return
name|proxy
return|;
block|}
specifier|private
name|void
name|createProducerAndSendMessages
parameter_list|(
name|int
name|numToSend
parameter_list|)
throws|throws
name|Exception
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
name|CLIENT_ACKNOWLEDGE
argument_list|)
expr_stmt|;
name|queue
operator|=
name|session
operator|.
name|createQueue
argument_list|(
literal|"test1"
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
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|numToSend
condition|;
name|i
operator|++
control|)
block|{
name|TextMessage
name|message
init|=
name|session
operator|.
name|createTextMessage
argument_list|(
name|MESSAGE_TEXT
operator|+
name|i
argument_list|)
decl_stmt|;
if|if
condition|(
name|i
operator|!=
literal|0
operator|&&
name|i
operator|%
literal|10000
operator|==
literal|0
condition|)
block|{
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
specifier|private
name|void
name|createConsumer
parameter_list|()
throws|throws
name|Exception
block|{
name|consumer
operator|=
name|session
operator|.
name|createConsumer
argument_list|(
name|queue
argument_list|)
expr_stmt|;
comment|// wait for buffer fill out
name|Thread
operator|.
name|sleep
argument_list|(
literal|5
operator|*
literal|1000
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
literal|500
condition|;
operator|++
name|i
control|)
block|{
name|Message
name|message
init|=
name|consumer
operator|.
name|receive
argument_list|()
decl_stmt|;
name|message
operator|.
name|acknowledge
argument_list|()
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

