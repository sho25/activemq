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
name|TimeUnit
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
name|util
operator|.
name|IdGenerator
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
name|JobSchedulerManagementTest
extends|extends
name|JobSchedulerTestSupport
block|{
specifier|private
specifier|static
specifier|final
specifier|transient
name|Logger
name|LOG
init|=
name|LoggerFactory
operator|.
name|getLogger
argument_list|(
name|JobSchedulerManagementTest
operator|.
name|class
argument_list|)
decl_stmt|;
annotation|@
name|Test
specifier|public
name|void
name|testRemoveAllScheduled
parameter_list|()
throws|throws
name|Exception
block|{
specifier|final
name|int
name|COUNT
init|=
literal|5
decl_stmt|;
name|Connection
name|connection
init|=
name|createConnection
argument_list|()
decl_stmt|;
comment|// Setup the scheduled Message
name|scheduleMessage
argument_list|(
name|connection
argument_list|,
name|TimeUnit
operator|.
name|SECONDS
operator|.
name|toMillis
argument_list|(
literal|6
argument_list|)
argument_list|,
name|COUNT
argument_list|)
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
comment|// Create the Browse Destination and the Reply To location
name|Destination
name|management
init|=
name|session
operator|.
name|createTopic
argument_list|(
name|ScheduledMessage
operator|.
name|AMQ_SCHEDULER_MANAGEMENT_DESTINATION
argument_list|)
decl_stmt|;
comment|// Create the eventual Consumer to receive the scheduled message
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
name|latch
operator|.
name|countDown
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
comment|// Send the remove request
name|MessageProducer
name|producer
init|=
name|session
operator|.
name|createProducer
argument_list|(
name|management
argument_list|)
decl_stmt|;
name|Message
name|request
init|=
name|session
operator|.
name|createMessage
argument_list|()
decl_stmt|;
name|request
operator|.
name|setStringProperty
argument_list|(
name|ScheduledMessage
operator|.
name|AMQ_SCHEDULER_ACTION
argument_list|,
name|ScheduledMessage
operator|.
name|AMQ_SCHEDULER_ACTION_REMOVEALL
argument_list|)
expr_stmt|;
name|producer
operator|.
name|send
argument_list|(
name|request
argument_list|)
expr_stmt|;
comment|// Now wait and see if any get delivered, none should.
name|latch
operator|.
name|await
argument_list|(
literal|10
argument_list|,
name|TimeUnit
operator|.
name|SECONDS
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|latch
operator|.
name|getCount
argument_list|()
argument_list|,
name|COUNT
argument_list|)
expr_stmt|;
name|connection
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testRemoveAllScheduledAtTime
parameter_list|()
throws|throws
name|Exception
block|{
specifier|final
name|int
name|COUNT
init|=
literal|3
decl_stmt|;
name|Connection
name|connection
init|=
name|createConnection
argument_list|()
decl_stmt|;
comment|// Setup the scheduled Message
name|scheduleMessage
argument_list|(
name|connection
argument_list|,
name|TimeUnit
operator|.
name|SECONDS
operator|.
name|toMillis
argument_list|(
literal|6
argument_list|)
argument_list|)
expr_stmt|;
name|scheduleMessage
argument_list|(
name|connection
argument_list|,
name|TimeUnit
operator|.
name|SECONDS
operator|.
name|toMillis
argument_list|(
literal|15
argument_list|)
argument_list|)
expr_stmt|;
name|scheduleMessage
argument_list|(
name|connection
argument_list|,
name|TimeUnit
operator|.
name|SECONDS
operator|.
name|toMillis
argument_list|(
literal|20
argument_list|)
argument_list|)
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
comment|// Create the Browse Destination and the Reply To location
name|Destination
name|management
init|=
name|session
operator|.
name|createTopic
argument_list|(
name|ScheduledMessage
operator|.
name|AMQ_SCHEDULER_MANAGEMENT_DESTINATION
argument_list|)
decl_stmt|;
name|Destination
name|browseDest
init|=
name|session
operator|.
name|createTemporaryQueue
argument_list|()
decl_stmt|;
comment|// Create the eventual Consumer to receive the scheduled message
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
name|latch
operator|.
name|countDown
argument_list|()
expr_stmt|;
block|}
block|}
argument_list|)
expr_stmt|;
comment|// Create the "Browser"
name|MessageConsumer
name|browser
init|=
name|session
operator|.
name|createConsumer
argument_list|(
name|browseDest
argument_list|)
decl_stmt|;
specifier|final
name|CountDownLatch
name|browsedLatch
init|=
operator|new
name|CountDownLatch
argument_list|(
name|COUNT
argument_list|)
decl_stmt|;
name|browser
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
name|browsedLatch
operator|.
name|countDown
argument_list|()
expr_stmt|;
name|LOG
operator|.
name|debug
argument_list|(
literal|"Scheduled Message Browser got Message: "
operator|+
name|message
argument_list|)
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
name|long
name|start
init|=
name|System
operator|.
name|currentTimeMillis
argument_list|()
operator|+
name|TimeUnit
operator|.
name|SECONDS
operator|.
name|toMillis
argument_list|(
literal|10
argument_list|)
decl_stmt|;
name|long
name|end
init|=
name|System
operator|.
name|currentTimeMillis
argument_list|()
operator|+
name|TimeUnit
operator|.
name|SECONDS
operator|.
name|toMillis
argument_list|(
literal|30
argument_list|)
decl_stmt|;
comment|// Send the remove request
name|MessageProducer
name|producer
init|=
name|session
operator|.
name|createProducer
argument_list|(
name|management
argument_list|)
decl_stmt|;
name|Message
name|request
init|=
name|session
operator|.
name|createMessage
argument_list|()
decl_stmt|;
name|request
operator|.
name|setStringProperty
argument_list|(
name|ScheduledMessage
operator|.
name|AMQ_SCHEDULER_ACTION
argument_list|,
name|ScheduledMessage
operator|.
name|AMQ_SCHEDULER_ACTION_REMOVEALL
argument_list|)
expr_stmt|;
name|request
operator|.
name|setStringProperty
argument_list|(
name|ScheduledMessage
operator|.
name|AMQ_SCHEDULER_ACTION_START_TIME
argument_list|,
name|Long
operator|.
name|toString
argument_list|(
name|start
argument_list|)
argument_list|)
expr_stmt|;
name|request
operator|.
name|setStringProperty
argument_list|(
name|ScheduledMessage
operator|.
name|AMQ_SCHEDULER_ACTION_END_TIME
argument_list|,
name|Long
operator|.
name|toString
argument_list|(
name|end
argument_list|)
argument_list|)
expr_stmt|;
name|producer
operator|.
name|send
argument_list|(
name|request
argument_list|)
expr_stmt|;
comment|// Send the browse request
name|request
operator|=
name|session
operator|.
name|createMessage
argument_list|()
expr_stmt|;
name|request
operator|.
name|setStringProperty
argument_list|(
name|ScheduledMessage
operator|.
name|AMQ_SCHEDULER_ACTION
argument_list|,
name|ScheduledMessage
operator|.
name|AMQ_SCHEDULER_ACTION_BROWSE
argument_list|)
expr_stmt|;
name|request
operator|.
name|setJMSReplyTo
argument_list|(
name|browseDest
argument_list|)
expr_stmt|;
name|producer
operator|.
name|send
argument_list|(
name|request
argument_list|)
expr_stmt|;
comment|// now see if we got back only the one remaining message.
name|latch
operator|.
name|await
argument_list|(
literal|10
argument_list|,
name|TimeUnit
operator|.
name|SECONDS
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|2
argument_list|,
name|browsedLatch
operator|.
name|getCount
argument_list|()
argument_list|)
expr_stmt|;
comment|// Now wait and see if any get delivered, none should.
name|latch
operator|.
name|await
argument_list|(
literal|10
argument_list|,
name|TimeUnit
operator|.
name|SECONDS
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|2
argument_list|,
name|latch
operator|.
name|getCount
argument_list|()
argument_list|)
expr_stmt|;
name|connection
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testBrowseAllScheduled
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
name|Connection
name|connection
init|=
name|createConnection
argument_list|()
decl_stmt|;
comment|// Setup the scheduled Message
name|scheduleMessage
argument_list|(
name|connection
argument_list|,
name|TimeUnit
operator|.
name|SECONDS
operator|.
name|toMillis
argument_list|(
literal|9
argument_list|)
argument_list|,
name|COUNT
argument_list|)
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
comment|// Create the Browse Destination and the Reply To location
name|Destination
name|requestBrowse
init|=
name|session
operator|.
name|createTopic
argument_list|(
name|ScheduledMessage
operator|.
name|AMQ_SCHEDULER_MANAGEMENT_DESTINATION
argument_list|)
decl_stmt|;
name|Destination
name|browseDest
init|=
name|session
operator|.
name|createTemporaryQueue
argument_list|()
decl_stmt|;
comment|// Create the eventual Consumer to receive the scheduled message
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
name|latch
operator|.
name|countDown
argument_list|()
expr_stmt|;
block|}
block|}
argument_list|)
expr_stmt|;
comment|// Create the "Browser"
name|MessageConsumer
name|browser
init|=
name|session
operator|.
name|createConsumer
argument_list|(
name|browseDest
argument_list|)
decl_stmt|;
specifier|final
name|CountDownLatch
name|browsedLatch
init|=
operator|new
name|CountDownLatch
argument_list|(
name|COUNT
argument_list|)
decl_stmt|;
name|browser
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
name|browsedLatch
operator|.
name|countDown
argument_list|()
expr_stmt|;
name|LOG
operator|.
name|debug
argument_list|(
literal|"Scheduled Message Browser got Message: "
operator|+
name|message
argument_list|)
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
comment|// Send the browse request
name|MessageProducer
name|producer
init|=
name|session
operator|.
name|createProducer
argument_list|(
name|requestBrowse
argument_list|)
decl_stmt|;
name|Message
name|request
init|=
name|session
operator|.
name|createMessage
argument_list|()
decl_stmt|;
name|request
operator|.
name|setStringProperty
argument_list|(
name|ScheduledMessage
operator|.
name|AMQ_SCHEDULER_ACTION
argument_list|,
name|ScheduledMessage
operator|.
name|AMQ_SCHEDULER_ACTION_BROWSE
argument_list|)
expr_stmt|;
name|request
operator|.
name|setJMSReplyTo
argument_list|(
name|browseDest
argument_list|)
expr_stmt|;
name|producer
operator|.
name|send
argument_list|(
name|request
argument_list|)
expr_stmt|;
comment|// make sure the message isn't delivered early because we browsed it
name|Thread
operator|.
name|sleep
argument_list|(
literal|2000
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|latch
operator|.
name|getCount
argument_list|()
argument_list|,
name|COUNT
argument_list|)
expr_stmt|;
comment|// now see if we got all the scheduled messages on the browse
comment|// destination.
name|latch
operator|.
name|await
argument_list|(
literal|10
argument_list|,
name|TimeUnit
operator|.
name|SECONDS
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|browsedLatch
operator|.
name|getCount
argument_list|()
argument_list|,
literal|0
argument_list|)
expr_stmt|;
comment|// now check that they all got delivered
name|latch
operator|.
name|await
argument_list|(
literal|10
argument_list|,
name|TimeUnit
operator|.
name|SECONDS
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|latch
operator|.
name|getCount
argument_list|()
argument_list|,
literal|0
argument_list|)
expr_stmt|;
name|connection
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testBrowseWindowlScheduled
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
name|Connection
name|connection
init|=
name|createConnection
argument_list|()
decl_stmt|;
comment|// Setup the scheduled Message
name|scheduleMessage
argument_list|(
name|connection
argument_list|,
name|TimeUnit
operator|.
name|SECONDS
operator|.
name|toMillis
argument_list|(
literal|5
argument_list|)
argument_list|)
expr_stmt|;
name|scheduleMessage
argument_list|(
name|connection
argument_list|,
name|TimeUnit
operator|.
name|SECONDS
operator|.
name|toMillis
argument_list|(
literal|10
argument_list|)
argument_list|,
name|COUNT
argument_list|)
expr_stmt|;
name|scheduleMessage
argument_list|(
name|connection
argument_list|,
name|TimeUnit
operator|.
name|SECONDS
operator|.
name|toMillis
argument_list|(
literal|20
argument_list|)
argument_list|)
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
comment|// Create the Browse Destination and the Reply To location
name|Destination
name|requestBrowse
init|=
name|session
operator|.
name|createTopic
argument_list|(
name|ScheduledMessage
operator|.
name|AMQ_SCHEDULER_MANAGEMENT_DESTINATION
argument_list|)
decl_stmt|;
name|Destination
name|browseDest
init|=
name|session
operator|.
name|createTemporaryQueue
argument_list|()
decl_stmt|;
comment|// Create the eventual Consumer to receive the scheduled message
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
operator|+
literal|2
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
name|latch
operator|.
name|countDown
argument_list|()
expr_stmt|;
block|}
block|}
argument_list|)
expr_stmt|;
comment|// Create the "Browser"
name|MessageConsumer
name|browser
init|=
name|session
operator|.
name|createConsumer
argument_list|(
name|browseDest
argument_list|)
decl_stmt|;
specifier|final
name|CountDownLatch
name|browsedLatch
init|=
operator|new
name|CountDownLatch
argument_list|(
name|COUNT
argument_list|)
decl_stmt|;
name|browser
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
name|browsedLatch
operator|.
name|countDown
argument_list|()
expr_stmt|;
name|LOG
operator|.
name|debug
argument_list|(
literal|"Scheduled Message Browser got Message: "
operator|+
name|message
argument_list|)
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
name|long
name|start
init|=
name|System
operator|.
name|currentTimeMillis
argument_list|()
operator|+
name|TimeUnit
operator|.
name|SECONDS
operator|.
name|toMillis
argument_list|(
literal|6
argument_list|)
decl_stmt|;
name|long
name|end
init|=
name|System
operator|.
name|currentTimeMillis
argument_list|()
operator|+
name|TimeUnit
operator|.
name|SECONDS
operator|.
name|toMillis
argument_list|(
literal|15
argument_list|)
decl_stmt|;
comment|// Send the browse request
name|MessageProducer
name|producer
init|=
name|session
operator|.
name|createProducer
argument_list|(
name|requestBrowse
argument_list|)
decl_stmt|;
name|Message
name|request
init|=
name|session
operator|.
name|createMessage
argument_list|()
decl_stmt|;
name|request
operator|.
name|setStringProperty
argument_list|(
name|ScheduledMessage
operator|.
name|AMQ_SCHEDULER_ACTION
argument_list|,
name|ScheduledMessage
operator|.
name|AMQ_SCHEDULER_ACTION_BROWSE
argument_list|)
expr_stmt|;
name|request
operator|.
name|setStringProperty
argument_list|(
name|ScheduledMessage
operator|.
name|AMQ_SCHEDULER_ACTION_START_TIME
argument_list|,
name|Long
operator|.
name|toString
argument_list|(
name|start
argument_list|)
argument_list|)
expr_stmt|;
name|request
operator|.
name|setStringProperty
argument_list|(
name|ScheduledMessage
operator|.
name|AMQ_SCHEDULER_ACTION_END_TIME
argument_list|,
name|Long
operator|.
name|toString
argument_list|(
name|end
argument_list|)
argument_list|)
expr_stmt|;
name|request
operator|.
name|setJMSReplyTo
argument_list|(
name|browseDest
argument_list|)
expr_stmt|;
name|producer
operator|.
name|send
argument_list|(
name|request
argument_list|)
expr_stmt|;
comment|// make sure the message isn't delivered early because we browsed it
name|Thread
operator|.
name|sleep
argument_list|(
literal|2000
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|COUNT
operator|+
literal|2
argument_list|,
name|latch
operator|.
name|getCount
argument_list|()
argument_list|)
expr_stmt|;
comment|// now see if we got all the scheduled messages on the browse
comment|// destination.
name|latch
operator|.
name|await
argument_list|(
literal|15
argument_list|,
name|TimeUnit
operator|.
name|SECONDS
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|browsedLatch
operator|.
name|getCount
argument_list|()
argument_list|)
expr_stmt|;
comment|// now see if we got all the scheduled messages on the browse
comment|// destination.
name|latch
operator|.
name|await
argument_list|(
literal|20
argument_list|,
name|TimeUnit
operator|.
name|SECONDS
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|latch
operator|.
name|getCount
argument_list|()
argument_list|)
expr_stmt|;
name|connection
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testRemoveScheduled
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
name|Connection
name|connection
init|=
name|createConnection
argument_list|()
decl_stmt|;
comment|// Setup the scheduled Message
name|scheduleMessage
argument_list|(
name|connection
argument_list|,
name|TimeUnit
operator|.
name|SECONDS
operator|.
name|toMillis
argument_list|(
literal|9
argument_list|)
argument_list|,
name|COUNT
argument_list|)
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
comment|// Create the Browse Destination and the Reply To location
name|Destination
name|management
init|=
name|session
operator|.
name|createTopic
argument_list|(
name|ScheduledMessage
operator|.
name|AMQ_SCHEDULER_MANAGEMENT_DESTINATION
argument_list|)
decl_stmt|;
name|Destination
name|browseDest
init|=
name|session
operator|.
name|createTemporaryQueue
argument_list|()
decl_stmt|;
comment|// Create the eventual Consumer to receive the scheduled message
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
name|MessageProducer
name|producer
init|=
name|session
operator|.
name|createProducer
argument_list|(
name|management
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
name|latch
operator|.
name|countDown
argument_list|()
expr_stmt|;
block|}
block|}
argument_list|)
expr_stmt|;
comment|// Create the "Browser"
name|Session
name|browseSession
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
name|browser
init|=
name|browseSession
operator|.
name|createConsumer
argument_list|(
name|browseDest
argument_list|)
decl_stmt|;
name|connection
operator|.
name|start
argument_list|()
expr_stmt|;
comment|// Send the browse request
name|Message
name|request
init|=
name|session
operator|.
name|createMessage
argument_list|()
decl_stmt|;
name|request
operator|.
name|setStringProperty
argument_list|(
name|ScheduledMessage
operator|.
name|AMQ_SCHEDULER_ACTION
argument_list|,
name|ScheduledMessage
operator|.
name|AMQ_SCHEDULER_ACTION_BROWSE
argument_list|)
expr_stmt|;
name|request
operator|.
name|setJMSReplyTo
argument_list|(
name|browseDest
argument_list|)
expr_stmt|;
name|producer
operator|.
name|send
argument_list|(
name|request
argument_list|)
expr_stmt|;
comment|// Browse all the Scheduled Messages.
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
operator|++
name|i
control|)
block|{
name|Message
name|message
init|=
name|browser
operator|.
name|receive
argument_list|(
literal|2000
argument_list|)
decl_stmt|;
name|assertNotNull
argument_list|(
name|message
argument_list|)
expr_stmt|;
try|try
block|{
name|Message
name|remove
init|=
name|session
operator|.
name|createMessage
argument_list|()
decl_stmt|;
name|remove
operator|.
name|setStringProperty
argument_list|(
name|ScheduledMessage
operator|.
name|AMQ_SCHEDULER_ACTION
argument_list|,
name|ScheduledMessage
operator|.
name|AMQ_SCHEDULER_ACTION_REMOVE
argument_list|)
expr_stmt|;
name|remove
operator|.
name|setStringProperty
argument_list|(
name|ScheduledMessage
operator|.
name|AMQ_SCHEDULED_ID
argument_list|,
name|message
operator|.
name|getStringProperty
argument_list|(
name|ScheduledMessage
operator|.
name|AMQ_SCHEDULED_ID
argument_list|)
argument_list|)
expr_stmt|;
name|producer
operator|.
name|send
argument_list|(
name|remove
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{             }
block|}
comment|// now check that they all got removed and are not delivered.
name|latch
operator|.
name|await
argument_list|(
literal|11
argument_list|,
name|TimeUnit
operator|.
name|SECONDS
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|COUNT
argument_list|,
name|latch
operator|.
name|getCount
argument_list|()
argument_list|)
expr_stmt|;
name|connection
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testRemoveNotScheduled
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
comment|// Create the Browse Destination and the Reply To location
name|Destination
name|management
init|=
name|session
operator|.
name|createTopic
argument_list|(
name|ScheduledMessage
operator|.
name|AMQ_SCHEDULER_MANAGEMENT_DESTINATION
argument_list|)
decl_stmt|;
name|MessageProducer
name|producer
init|=
name|session
operator|.
name|createProducer
argument_list|(
name|management
argument_list|)
decl_stmt|;
try|try
block|{
comment|// Send the remove request
name|Message
name|remove
init|=
name|session
operator|.
name|createMessage
argument_list|()
decl_stmt|;
name|remove
operator|.
name|setStringProperty
argument_list|(
name|ScheduledMessage
operator|.
name|AMQ_SCHEDULER_ACTION
argument_list|,
name|ScheduledMessage
operator|.
name|AMQ_SCHEDULER_ACTION_REMOVEALL
argument_list|)
expr_stmt|;
name|remove
operator|.
name|setStringProperty
argument_list|(
name|ScheduledMessage
operator|.
name|AMQ_SCHEDULED_ID
argument_list|,
operator|new
name|IdGenerator
argument_list|()
operator|.
name|generateId
argument_list|()
argument_list|)
expr_stmt|;
name|producer
operator|.
name|send
argument_list|(
name|remove
argument_list|)
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
literal|"Caught unexpected exception during remove of unscheduled message."
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|connection
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
annotation|@
name|Test
specifier|public
name|void
name|testBrowseWithSelector
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
comment|// Setup the scheduled Message
name|scheduleMessage
argument_list|(
name|connection
argument_list|,
name|TimeUnit
operator|.
name|SECONDS
operator|.
name|toMillis
argument_list|(
literal|9
argument_list|)
argument_list|)
expr_stmt|;
name|scheduleMessage
argument_list|(
name|connection
argument_list|,
name|TimeUnit
operator|.
name|SECONDS
operator|.
name|toMillis
argument_list|(
literal|10
argument_list|)
argument_list|)
expr_stmt|;
name|scheduleMessage
argument_list|(
name|connection
argument_list|,
name|TimeUnit
operator|.
name|SECONDS
operator|.
name|toMillis
argument_list|(
literal|5
argument_list|)
argument_list|)
expr_stmt|;
name|scheduleMessage
argument_list|(
name|connection
argument_list|,
name|TimeUnit
operator|.
name|SECONDS
operator|.
name|toMillis
argument_list|(
literal|45
argument_list|)
argument_list|)
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
comment|// Create the Browse Destination and the Reply To location
name|Destination
name|requestBrowse
init|=
name|session
operator|.
name|createTopic
argument_list|(
name|ScheduledMessage
operator|.
name|AMQ_SCHEDULER_MANAGEMENT_DESTINATION
argument_list|)
decl_stmt|;
name|Destination
name|browseDest
init|=
name|session
operator|.
name|createTemporaryTopic
argument_list|()
decl_stmt|;
comment|// Create the "Browser"
name|MessageConsumer
name|browser
init|=
name|session
operator|.
name|createConsumer
argument_list|(
name|browseDest
argument_list|,
name|ScheduledMessage
operator|.
name|AMQ_SCHEDULED_DELAY
operator|+
literal|" = 45000"
argument_list|)
decl_stmt|;
name|connection
operator|.
name|start
argument_list|()
expr_stmt|;
comment|// Send the browse request
name|MessageProducer
name|producer
init|=
name|session
operator|.
name|createProducer
argument_list|(
name|requestBrowse
argument_list|)
decl_stmt|;
name|Message
name|request
init|=
name|session
operator|.
name|createMessage
argument_list|()
decl_stmt|;
name|request
operator|.
name|setStringProperty
argument_list|(
name|ScheduledMessage
operator|.
name|AMQ_SCHEDULER_ACTION
argument_list|,
name|ScheduledMessage
operator|.
name|AMQ_SCHEDULER_ACTION_BROWSE
argument_list|)
expr_stmt|;
name|request
operator|.
name|setJMSReplyTo
argument_list|(
name|browseDest
argument_list|)
expr_stmt|;
name|producer
operator|.
name|send
argument_list|(
name|request
argument_list|)
expr_stmt|;
comment|// Now try and receive the one we selected
name|Message
name|message
init|=
name|browser
operator|.
name|receive
argument_list|(
literal|5000
argument_list|)
decl_stmt|;
name|assertNotNull
argument_list|(
name|message
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|45000
argument_list|,
name|message
operator|.
name|getLongProperty
argument_list|(
name|ScheduledMessage
operator|.
name|AMQ_SCHEDULED_DELAY
argument_list|)
argument_list|)
expr_stmt|;
comment|// Verify that original destination was preserved
name|assertEquals
argument_list|(
name|destination
argument_list|,
operator|(
operator|(
name|ActiveMQMessage
operator|)
name|message
operator|)
operator|.
name|getOriginalDestination
argument_list|()
argument_list|)
expr_stmt|;
comment|// Now check if there are anymore, there shouldn't be
name|message
operator|=
name|browser
operator|.
name|receive
argument_list|(
literal|5000
argument_list|)
expr_stmt|;
name|assertNull
argument_list|(
name|message
argument_list|)
expr_stmt|;
name|connection
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
specifier|protected
name|void
name|scheduleMessage
parameter_list|(
name|Connection
name|connection
parameter_list|,
name|long
name|delay
parameter_list|)
throws|throws
name|Exception
block|{
name|scheduleMessage
argument_list|(
name|connection
argument_list|,
name|delay
argument_list|,
literal|1
argument_list|)
expr_stmt|;
block|}
specifier|protected
name|void
name|scheduleMessage
parameter_list|(
name|Connection
name|connection
parameter_list|,
name|long
name|delay
parameter_list|,
name|int
name|count
parameter_list|)
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
name|TextMessage
name|message
init|=
name|session
operator|.
name|createTextMessage
argument_list|(
literal|"test msg"
argument_list|)
decl_stmt|;
name|message
operator|.
name|setLongProperty
argument_list|(
name|ScheduledMessage
operator|.
name|AMQ_SCHEDULED_DELAY
argument_list|,
name|delay
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
name|count
condition|;
operator|++
name|i
control|)
block|{
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
block|}
end_class

end_unit

