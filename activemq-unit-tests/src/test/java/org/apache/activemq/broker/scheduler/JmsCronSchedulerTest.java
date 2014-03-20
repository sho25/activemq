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
name|scheduler
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
name|Date
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
name|EmbeddedBrokerTestSupport
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
name|ScheduledMessage
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
name|util
operator|.
name|IOHelper
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
name|junit
operator|.
name|runner
operator|.
name|RunWith
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|runners
operator|.
name|BlockJUnit4ClassRunner
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
annotation|@
name|RunWith
argument_list|(
name|BlockJUnit4ClassRunner
operator|.
name|class
argument_list|)
specifier|public
class|class
name|JmsCronSchedulerTest
extends|extends
name|EmbeddedBrokerTestSupport
block|{
annotation|@
name|Rule
specifier|public
name|TestName
name|testName
init|=
operator|new
name|TestName
argument_list|()
decl_stmt|;
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
name|JmsCronSchedulerTest
operator|.
name|class
argument_list|)
decl_stmt|;
annotation|@
name|Test
specifier|public
name|void
name|testSimulatenousCron
parameter_list|()
throws|throws
name|Exception
block|{
specifier|final
name|int
name|COUNT
init|=
literal|10
decl_stmt|;
specifier|final
name|AtomicInteger
name|count
init|=
operator|new
name|AtomicInteger
argument_list|()
decl_stmt|;
name|Connection
name|connection
init|=
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
specifier|final
name|CountDownLatch
name|latch
init|=
operator|new
name|CountDownLatch
argument_list|(
name|COUNT
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
name|count
operator|.
name|incrementAndGet
argument_list|()
expr_stmt|;
name|latch
operator|.
name|countDown
argument_list|()
expr_stmt|;
name|assertTrue
argument_list|(
name|message
operator|instanceof
name|TextMessage
argument_list|)
expr_stmt|;
name|TextMessage
name|tm
init|=
operator|(
name|TextMessage
operator|)
name|message
decl_stmt|;
try|try
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"Received [{}] count: {} "
argument_list|,
name|tm
operator|.
name|getText
argument_list|()
argument_list|,
name|count
operator|.
name|get
argument_list|()
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
literal|"Unexpected exception in onMessage"
argument_list|,
name|e
argument_list|)
expr_stmt|;
name|fail
argument_list|(
literal|"Unexpected exception in onMessage: "
operator|+
name|e
operator|.
name|getMessage
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
block|}
argument_list|)
expr_stmt|;
name|connection
operator|.
name|start
argument_list|()
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
name|COUNT
condition|;
name|i
operator|++
control|)
block|{
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
name|TextMessage
name|message
init|=
name|session
operator|.
name|createTextMessage
argument_list|(
literal|"test msg "
operator|+
name|i
argument_list|)
decl_stmt|;
name|message
operator|.
name|setStringProperty
argument_list|(
name|ScheduledMessage
operator|.
name|AMQ_SCHEDULED_CRON
argument_list|,
literal|"* * * * *"
argument_list|)
expr_stmt|;
name|producer
operator|.
name|send
argument_list|(
name|message
argument_list|)
expr_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"Message {} sent at {}"
argument_list|,
name|i
argument_list|,
operator|new
name|Date
argument_list|()
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|producer
operator|.
name|close
argument_list|()
expr_stmt|;
comment|//wait a couple sec so cron start time is different for next message
name|Thread
operator|.
name|sleep
argument_list|(
literal|2000
argument_list|)
expr_stmt|;
block|}
name|SchedulerBroker
name|sb
init|=
operator|(
name|SchedulerBroker
operator|)
name|this
operator|.
name|broker
operator|.
name|getBroker
argument_list|()
operator|.
name|getAdaptor
argument_list|(
name|SchedulerBroker
operator|.
name|class
argument_list|)
decl_stmt|;
name|JobScheduler
name|js
init|=
name|sb
operator|.
name|getJobScheduler
argument_list|()
decl_stmt|;
name|List
argument_list|<
name|Job
argument_list|>
name|list
init|=
name|js
operator|.
name|getAllJobs
argument_list|()
decl_stmt|;
name|assertEquals
argument_list|(
name|COUNT
argument_list|,
name|list
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|latch
operator|.
name|await
argument_list|(
literal|2
argument_list|,
name|TimeUnit
operator|.
name|MINUTES
argument_list|)
expr_stmt|;
comment|//All should messages should have been received by now
name|assertEquals
argument_list|(
name|COUNT
argument_list|,
name|count
operator|.
name|get
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testCronScheduleWithTtlSet
parameter_list|()
throws|throws
name|Exception
block|{
name|Connection
name|connection
init|=
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
name|connection
operator|.
name|start
argument_list|()
expr_stmt|;
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
name|setTimeToLive
argument_list|(
name|TimeUnit
operator|.
name|MINUTES
operator|.
name|toMillis
argument_list|(
literal|1
argument_list|)
argument_list|)
expr_stmt|;
name|TextMessage
name|message
init|=
name|session
operator|.
name|createTextMessage
argument_list|(
literal|"test msg "
argument_list|)
decl_stmt|;
name|message
operator|.
name|setStringProperty
argument_list|(
name|ScheduledMessage
operator|.
name|AMQ_SCHEDULED_CRON
argument_list|,
literal|"* * * * *"
argument_list|)
expr_stmt|;
name|producer
operator|.
name|send
argument_list|(
name|message
argument_list|)
expr_stmt|;
name|producer
operator|.
name|close
argument_list|()
expr_stmt|;
name|Thread
operator|.
name|sleep
argument_list|(
name|TimeUnit
operator|.
name|MINUTES
operator|.
name|toMillis
argument_list|(
literal|2
argument_list|)
argument_list|)
expr_stmt|;
name|assertNotNull
argument_list|(
name|consumer
operator|.
name|receiveNoWait
argument_list|()
argument_list|)
expr_stmt|;
name|assertNull
argument_list|(
name|consumer
operator|.
name|receiveNoWait
argument_list|()
argument_list|)
expr_stmt|;
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
literal|"Starting test {}"
argument_list|,
name|testName
operator|.
name|getMethodName
argument_list|()
argument_list|)
expr_stmt|;
name|bindAddress
operator|=
literal|"vm://localhost"
expr_stmt|;
name|super
operator|.
name|setUp
argument_list|()
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
name|super
operator|.
name|tearDown
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Override
specifier|protected
name|BrokerService
name|createBroker
parameter_list|()
throws|throws
name|Exception
block|{
return|return
name|createBroker
argument_list|(
literal|true
argument_list|)
return|;
block|}
specifier|protected
name|BrokerService
name|createBroker
parameter_list|(
name|boolean
name|delete
parameter_list|)
throws|throws
name|Exception
block|{
name|File
name|schedulerDirectory
init|=
operator|new
name|File
argument_list|(
literal|"target/scheduler"
argument_list|)
decl_stmt|;
if|if
condition|(
name|delete
condition|)
block|{
name|IOHelper
operator|.
name|mkdirs
argument_list|(
name|schedulerDirectory
argument_list|)
expr_stmt|;
name|IOHelper
operator|.
name|deleteChildren
argument_list|(
name|schedulerDirectory
argument_list|)
expr_stmt|;
block|}
name|BrokerService
name|answer
init|=
operator|new
name|BrokerService
argument_list|()
decl_stmt|;
name|answer
operator|.
name|setPersistent
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|answer
operator|.
name|getManagementContext
argument_list|()
operator|.
name|setCreateConnector
argument_list|(
literal|false
argument_list|)
expr_stmt|;
name|answer
operator|.
name|setDeleteAllMessagesOnStartup
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|answer
operator|.
name|setDataDirectory
argument_list|(
literal|"target"
argument_list|)
expr_stmt|;
name|answer
operator|.
name|setSchedulerDirectoryFile
argument_list|(
name|schedulerDirectory
argument_list|)
expr_stmt|;
name|answer
operator|.
name|setSchedulerSupport
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|answer
operator|.
name|setUseJmx
argument_list|(
literal|false
argument_list|)
expr_stmt|;
name|answer
operator|.
name|addConnector
argument_list|(
name|bindAddress
argument_list|)
expr_stmt|;
return|return
name|answer
return|;
block|}
block|}
end_class

end_unit

