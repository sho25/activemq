begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *<p>  * http://www.apache.org/licenses/LICENSE-2.0  *<p>  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
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
name|jdbc
package|;
end_package

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
name|RedeliveryPolicy
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
name|util
operator|.
name|DefaultIOExceptionHandler
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

begin_import
import|import
name|javax
operator|.
name|jms
operator|.
name|*
import|;
end_import

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
name|assertFalse
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

begin_class
specifier|public
class|class
name|JDBCConcurrentDLQTest
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
name|JDBCConcurrentDLQTest
operator|.
name|class
argument_list|)
decl_stmt|;
name|BrokerService
name|broker
decl_stmt|;
name|JDBCPersistenceAdapter
name|jdbcPersistenceAdapter
decl_stmt|;
name|Appender
name|appender
init|=
literal|null
decl_stmt|;
specifier|final
name|AtomicBoolean
name|gotError
init|=
operator|new
name|AtomicBoolean
argument_list|(
literal|false
argument_list|)
decl_stmt|;
annotation|@
name|Before
specifier|public
name|void
name|setUp
parameter_list|()
throws|throws
name|Exception
block|{
name|gotError
operator|.
name|set
argument_list|(
literal|false
argument_list|)
expr_stmt|;
name|broker
operator|=
name|createBroker
argument_list|()
expr_stmt|;
name|broker
operator|.
name|start
argument_list|()
expr_stmt|;
name|broker
operator|.
name|waitUntilStarted
argument_list|()
expr_stmt|;
name|appender
operator|=
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
name|org
operator|.
name|apache
operator|.
name|log4j
operator|.
name|spi
operator|.
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
name|toInt
argument_list|()
operator|>
name|Level
operator|.
name|INFO_INT
condition|)
block|{
name|LOG
operator|.
name|error
argument_list|(
literal|"Got error from log:"
operator|+
name|event
operator|.
name|getRenderedMessage
argument_list|()
argument_list|)
expr_stmt|;
name|gotError
operator|.
name|set
argument_list|(
literal|true
argument_list|)
expr_stmt|;
block|}
block|}
block|}
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
name|RegionBroker
operator|.
name|class
argument_list|)
operator|.
name|removeAppender
argument_list|(
name|appender
argument_list|)
expr_stmt|;
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
name|JDBCPersistenceAdapter
operator|.
name|class
argument_list|)
operator|.
name|removeAppender
argument_list|(
name|appender
argument_list|)
expr_stmt|;
name|broker
operator|.
name|stop
argument_list|()
expr_stmt|;
block|}
specifier|protected
name|BrokerService
name|createBroker
parameter_list|()
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
literal|true
argument_list|)
expr_stmt|;
name|broker
operator|.
name|setAdvisorySupport
argument_list|(
literal|false
argument_list|)
expr_stmt|;
name|jdbcPersistenceAdapter
operator|=
operator|new
name|JDBCPersistenceAdapter
argument_list|()
expr_stmt|;
name|jdbcPersistenceAdapter
operator|.
name|setUseLock
argument_list|(
literal|false
argument_list|)
expr_stmt|;
name|broker
operator|.
name|setPersistenceAdapter
argument_list|(
name|jdbcPersistenceAdapter
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
name|addConnector
argument_list|(
literal|"tcp://0.0.0.0:0"
argument_list|)
expr_stmt|;
return|return
name|broker
return|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testConcurrentDlqOk
parameter_list|()
throws|throws
name|Exception
block|{
specifier|final
name|Destination
name|dest
init|=
operator|new
name|ActiveMQQueue
argument_list|(
literal|"DD"
argument_list|)
decl_stmt|;
specifier|final
name|ActiveMQConnectionFactory
name|amq
init|=
operator|new
name|ActiveMQConnectionFactory
argument_list|(
name|broker
operator|.
name|getTransportConnectorByScheme
argument_list|(
literal|"tcp"
argument_list|)
operator|.
name|getPublishableConnectString
argument_list|()
argument_list|)
decl_stmt|;
name|amq
operator|.
name|setWatchTopicAdvisories
argument_list|(
literal|false
argument_list|)
expr_stmt|;
name|broker
operator|.
name|setIoExceptionHandler
argument_list|(
operator|new
name|DefaultIOExceptionHandler
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|void
name|handle
parameter_list|(
name|IOException
name|exception
parameter_list|)
block|{
name|LOG
operator|.
name|error
argument_list|(
literal|"handle IOException from store"
argument_list|,
name|exception
argument_list|)
expr_stmt|;
name|gotError
operator|.
name|set
argument_list|(
literal|true
argument_list|)
expr_stmt|;
block|}
block|}
argument_list|)
expr_stmt|;
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
name|RegionBroker
operator|.
name|class
argument_list|)
operator|.
name|addAppender
argument_list|(
name|appender
argument_list|)
expr_stmt|;
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
name|JDBCPersistenceAdapter
operator|.
name|class
argument_list|)
operator|.
name|addAppender
argument_list|(
name|appender
argument_list|)
expr_stmt|;
specifier|final
name|int
name|numMessages
init|=
literal|100
decl_stmt|;
specifier|final
name|AtomicInteger
name|consumed
init|=
operator|new
name|AtomicInteger
argument_list|(
name|numMessages
argument_list|)
decl_stmt|;
name|produceMessages
argument_list|(
name|amq
argument_list|,
name|dest
argument_list|,
name|numMessages
argument_list|)
expr_stmt|;
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
literal|50
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
name|Connection
name|connection
init|=
literal|null
decl_stmt|;
name|Session
name|session
init|=
literal|null
decl_stmt|;
name|MessageConsumer
name|consumer
init|=
literal|null
decl_stmt|;
try|try
block|{
name|connection
operator|=
name|amq
operator|.
name|createConnection
argument_list|()
expr_stmt|;
name|connection
operator|.
name|setExceptionListener
argument_list|(
operator|new
name|javax
operator|.
name|jms
operator|.
name|ExceptionListener
argument_list|()
block|{
specifier|public
name|void
name|onException
parameter_list|(
name|javax
operator|.
name|jms
operator|.
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
block|}
argument_list|)
expr_stmt|;
comment|//set custom redelivery policy with 0 retries to force move to DLQ
name|RedeliveryPolicy
name|queuePolicy
init|=
operator|new
name|RedeliveryPolicy
argument_list|()
decl_stmt|;
name|queuePolicy
operator|.
name|setMaximumRedeliveries
argument_list|(
literal|0
argument_list|)
expr_stmt|;
operator|(
operator|(
name|ActiveMQConnection
operator|)
name|connection
operator|)
operator|.
name|setRedeliveryPolicy
argument_list|(
name|queuePolicy
argument_list|)
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
literal|true
argument_list|,
name|Session
operator|.
name|SESSION_TRANSACTED
argument_list|)
expr_stmt|;
name|consumer
operator|=
name|session
operator|.
name|createConsumer
argument_list|(
name|dest
argument_list|)
expr_stmt|;
while|while
condition|(
name|consumed
operator|.
name|get
argument_list|()
operator|>
literal|0
operator|&&
operator|!
name|gotError
operator|.
name|get
argument_list|()
condition|)
block|{
name|Message
name|message
init|=
name|consumer
operator|.
name|receive
argument_list|(
literal|4000
argument_list|)
decl_stmt|;
if|if
condition|(
name|message
operator|!=
literal|null
condition|)
block|{
name|consumed
operator|.
name|decrementAndGet
argument_list|()
expr_stmt|;
name|session
operator|.
name|rollback
argument_list|()
expr_stmt|;
block|}
block|}
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
name|LOG
operator|.
name|error
argument_list|(
literal|"Error on consumption"
argument_list|,
name|e
argument_list|)
expr_stmt|;
name|gotError
operator|.
name|set
argument_list|(
literal|true
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
name|Exception
name|ignored
parameter_list|)
block|{}
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
name|boolean
name|allComplete
init|=
name|executorService
operator|.
name|awaitTermination
argument_list|(
literal|60
argument_list|,
name|TimeUnit
operator|.
name|SECONDS
argument_list|)
decl_stmt|;
name|executorService
operator|.
name|shutdownNow
argument_list|()
expr_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"Total messages: "
operator|+
name|broker
operator|.
name|getAdminView
argument_list|()
operator|.
name|getTotalMessageCount
argument_list|()
argument_list|)
expr_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"Total enqueues: "
operator|+
name|broker
operator|.
name|getAdminView
argument_list|()
operator|.
name|getTotalEnqueueCount
argument_list|()
argument_list|)
expr_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"Total deueues: "
operator|+
name|broker
operator|.
name|getAdminView
argument_list|()
operator|.
name|getTotalDequeueCount
argument_list|()
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|allComplete
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"all consumed"
argument_list|,
literal|0l
argument_list|,
name|consumed
operator|.
name|get
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"all messages get to the dlq"
argument_list|,
name|numMessages
operator|*
literal|2
argument_list|,
name|broker
operator|.
name|getAdminView
argument_list|()
operator|.
name|getTotalEnqueueCount
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"all messages acked"
argument_list|,
name|numMessages
argument_list|,
name|broker
operator|.
name|getAdminView
argument_list|()
operator|.
name|getTotalDequeueCount
argument_list|()
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
literal|"no error"
argument_list|,
name|gotError
operator|.
name|get
argument_list|()
argument_list|)
expr_stmt|;
block|}
specifier|private
name|void
name|produceMessages
parameter_list|(
name|ActiveMQConnectionFactory
name|amq
parameter_list|,
name|Destination
name|dest
parameter_list|,
name|int
name|numMessages
parameter_list|)
throws|throws
name|JMSException
block|{
name|Connection
name|connection
init|=
name|amq
operator|.
name|createConnection
argument_list|()
decl_stmt|;
name|connection
operator|.
name|setExceptionListener
argument_list|(
operator|new
name|javax
operator|.
name|jms
operator|.
name|ExceptionListener
argument_list|()
block|{
specifier|public
name|void
name|onException
parameter_list|(
name|javax
operator|.
name|jms
operator|.
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
block|}
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
name|dest
argument_list|)
decl_stmt|;
name|long
name|counter
init|=
literal|0
decl_stmt|;
name|TextMessage
name|message
init|=
name|session
operator|.
name|createTextMessage
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
name|message
argument_list|)
expr_stmt|;
name|counter
operator|++
expr_stmt|;
if|if
condition|(
operator|(
name|counter
operator|%
literal|50
operator|)
operator|==
literal|0
condition|)
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"sent "
operator|+
name|counter
operator|+
literal|" messages"
argument_list|)
expr_stmt|;
block|}
block|}
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
block|}
end_class

end_unit
