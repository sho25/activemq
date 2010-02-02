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
name|usecases
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
name|VMPendingQueueMessageStoragePolicy
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
name|amq
operator|.
name|AMQPersistenceAdapter
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
name|apache
operator|.
name|commons
operator|.
name|logging
operator|.
name|Log
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|commons
operator|.
name|logging
operator|.
name|LogFactory
import|;
end_import

begin_class
specifier|public
class|class
name|ExpiredMessagesTest
extends|extends
name|CombinationTestSupport
block|{
specifier|private
specifier|static
specifier|final
name|Log
name|LOG
init|=
name|LogFactory
operator|.
name|getLog
argument_list|(
name|ExpiredMessagesTest
operator|.
name|class
argument_list|)
decl_stmt|;
name|BrokerService
name|broker
decl_stmt|;
name|Connection
name|connection
decl_stmt|;
name|Session
name|session
decl_stmt|;
name|MessageProducer
name|producer
decl_stmt|;
name|MessageConsumer
name|consumer
decl_stmt|;
specifier|public
name|ActiveMQDestination
name|destination
init|=
operator|new
name|ActiveMQQueue
argument_list|(
literal|"test"
argument_list|)
decl_stmt|;
specifier|public
name|ActiveMQDestination
name|dlqDestination
init|=
operator|new
name|ActiveMQQueue
argument_list|(
literal|"ActiveMQ.DLQ"
argument_list|)
decl_stmt|;
specifier|public
name|boolean
name|useTextMessage
init|=
literal|true
decl_stmt|;
specifier|public
name|boolean
name|useVMCursor
init|=
literal|true
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
name|ExpiredMessagesTest
operator|.
name|class
argument_list|)
return|;
block|}
specifier|public
specifier|static
name|void
name|main
parameter_list|(
name|String
index|[]
name|args
parameter_list|)
block|{
name|junit
operator|.
name|textui
operator|.
name|TestRunner
operator|.
name|run
argument_list|(
name|suite
argument_list|()
argument_list|)
expr_stmt|;
block|}
specifier|protected
name|void
name|setUp
parameter_list|()
throws|throws
name|Exception
block|{
specifier|final
name|boolean
name|deleteAllMessages
init|=
literal|true
decl_stmt|;
name|broker
operator|=
name|createBroker
argument_list|(
name|deleteAllMessages
argument_list|,
literal|100
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|testExpiredMessages
parameter_list|()
throws|throws
name|Exception
block|{
name|ActiveMQConnectionFactory
name|factory
init|=
operator|new
name|ActiveMQConnectionFactory
argument_list|(
literal|"tcp://localhost:61616"
argument_list|)
decl_stmt|;
name|connection
operator|=
name|factory
operator|.
name|createConnection
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
name|AUTO_ACKNOWLEDGE
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
name|producer
operator|.
name|setTimeToLive
argument_list|(
literal|100
argument_list|)
expr_stmt|;
name|consumer
operator|=
name|session
operator|.
name|createConsumer
argument_list|(
name|destination
argument_list|)
expr_stmt|;
name|connection
operator|.
name|start
argument_list|()
expr_stmt|;
specifier|final
name|AtomicLong
name|received
init|=
operator|new
name|AtomicLong
argument_list|()
decl_stmt|;
name|Thread
name|consumerThread
init|=
operator|new
name|Thread
argument_list|(
literal|"Consumer Thread"
argument_list|)
block|{
specifier|public
name|void
name|run
parameter_list|()
block|{
name|long
name|start
init|=
name|System
operator|.
name|currentTimeMillis
argument_list|()
decl_stmt|;
try|try
block|{
name|long
name|end
init|=
name|System
operator|.
name|currentTimeMillis
argument_list|()
decl_stmt|;
while|while
condition|(
name|end
operator|-
name|start
operator|<
literal|3000
condition|)
block|{
if|if
condition|(
name|consumer
operator|.
name|receive
argument_list|(
literal|1000
argument_list|)
operator|!=
literal|null
condition|)
block|{
name|received
operator|.
name|incrementAndGet
argument_list|()
expr_stmt|;
block|}
name|Thread
operator|.
name|sleep
argument_list|(
literal|100
argument_list|)
expr_stmt|;
name|end
operator|=
name|System
operator|.
name|currentTimeMillis
argument_list|()
expr_stmt|;
block|}
name|consumer
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Throwable
name|ex
parameter_list|)
block|{
name|ex
operator|.
name|printStackTrace
argument_list|()
expr_stmt|;
block|}
block|}
block|}
decl_stmt|;
name|consumerThread
operator|.
name|start
argument_list|()
expr_stmt|;
specifier|final
name|int
name|numMessagesToSend
init|=
literal|10000
decl_stmt|;
name|Thread
name|producingThread
init|=
operator|new
name|Thread
argument_list|(
literal|"Producing Thread"
argument_list|)
block|{
specifier|public
name|void
name|run
parameter_list|()
block|{
try|try
block|{
name|int
name|i
init|=
literal|0
decl_stmt|;
while|while
condition|(
name|i
operator|++
operator|<
name|numMessagesToSend
condition|)
block|{
name|producer
operator|.
name|send
argument_list|(
name|session
operator|.
name|createTextMessage
argument_list|(
literal|"test"
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|producer
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Throwable
name|ex
parameter_list|)
block|{
name|ex
operator|.
name|printStackTrace
argument_list|()
expr_stmt|;
block|}
block|}
block|}
decl_stmt|;
name|producingThread
operator|.
name|start
argument_list|()
expr_stmt|;
name|consumerThread
operator|.
name|join
argument_list|()
expr_stmt|;
name|producingThread
operator|.
name|join
argument_list|()
expr_stmt|;
name|session
operator|.
name|close
argument_list|()
expr_stmt|;
specifier|final
name|DestinationStatistics
name|view
init|=
name|this
operator|.
name|getDestinationStatistics
argument_list|(
name|destination
argument_list|)
decl_stmt|;
comment|// wait for all to inflight to expire
name|assertTrue
argument_list|(
literal|"all inflight messages expired "
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
specifier|public
name|boolean
name|isSatisified
parameter_list|()
throws|throws
name|Exception
block|{
return|return
name|view
operator|.
name|getInflight
argument_list|()
operator|.
name|getCount
argument_list|()
operator|==
literal|0
return|;
block|}
block|}
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"Wrong inFlightCount: "
argument_list|,
literal|0
argument_list|,
name|view
operator|.
name|getInflight
argument_list|()
operator|.
name|getCount
argument_list|()
argument_list|)
expr_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"Stats: received: "
operator|+
name|received
operator|.
name|get
argument_list|()
operator|+
literal|", enqueues: "
operator|+
name|view
operator|.
name|getEnqueues
argument_list|()
operator|.
name|getCount
argument_list|()
operator|+
literal|", dequeues: "
operator|+
name|view
operator|.
name|getDequeues
argument_list|()
operator|.
name|getCount
argument_list|()
operator|+
literal|", dispatched: "
operator|+
name|view
operator|.
name|getDispatched
argument_list|()
operator|.
name|getCount
argument_list|()
operator|+
literal|", inflight: "
operator|+
name|view
operator|.
name|getInflight
argument_list|()
operator|.
name|getCount
argument_list|()
operator|+
literal|", expiries: "
operator|+
name|view
operator|.
name|getExpired
argument_list|()
operator|.
name|getCount
argument_list|()
argument_list|)
expr_stmt|;
comment|// wait for all sent to get delivered and expire
name|assertTrue
argument_list|(
literal|"all sent messages expired "
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
specifier|public
name|boolean
name|isSatisified
parameter_list|()
throws|throws
name|Exception
block|{
name|long
name|oldEnqueues
init|=
name|view
operator|.
name|getEnqueues
argument_list|()
operator|.
name|getCount
argument_list|()
decl_stmt|;
name|Thread
operator|.
name|sleep
argument_list|(
literal|200
argument_list|)
expr_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"Stats: received: "
operator|+
name|received
operator|.
name|get
argument_list|()
operator|+
literal|", size= "
operator|+
name|view
operator|.
name|getMessages
argument_list|()
operator|.
name|getCount
argument_list|()
operator|+
literal|", enqueues: "
operator|+
name|view
operator|.
name|getDequeues
argument_list|()
operator|.
name|getCount
argument_list|()
operator|+
literal|", dequeues: "
operator|+
name|view
operator|.
name|getDequeues
argument_list|()
operator|.
name|getCount
argument_list|()
operator|+
literal|", dispatched: "
operator|+
name|view
operator|.
name|getDispatched
argument_list|()
operator|.
name|getCount
argument_list|()
operator|+
literal|", inflight: "
operator|+
name|view
operator|.
name|getInflight
argument_list|()
operator|.
name|getCount
argument_list|()
operator|+
literal|", expiries: "
operator|+
name|view
operator|.
name|getExpired
argument_list|()
operator|.
name|getCount
argument_list|()
argument_list|)
expr_stmt|;
return|return
name|oldEnqueues
operator|==
name|view
operator|.
name|getEnqueues
argument_list|()
operator|.
name|getCount
argument_list|()
return|;
block|}
block|}
argument_list|,
literal|60
operator|*
literal|1000
argument_list|)
argument_list|)
expr_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"Stats: received: "
operator|+
name|received
operator|.
name|get
argument_list|()
operator|+
literal|", size= "
operator|+
name|view
operator|.
name|getMessages
argument_list|()
operator|.
name|getCount
argument_list|()
operator|+
literal|", enqueues: "
operator|+
name|view
operator|.
name|getEnqueues
argument_list|()
operator|.
name|getCount
argument_list|()
operator|+
literal|", dequeues: "
operator|+
name|view
operator|.
name|getDequeues
argument_list|()
operator|.
name|getCount
argument_list|()
operator|+
literal|", dispatched: "
operator|+
name|view
operator|.
name|getDispatched
argument_list|()
operator|.
name|getCount
argument_list|()
operator|+
literal|", inflight: "
operator|+
name|view
operator|.
name|getInflight
argument_list|()
operator|.
name|getCount
argument_list|()
operator|+
literal|", expiries: "
operator|+
name|view
operator|.
name|getExpired
argument_list|()
operator|.
name|getCount
argument_list|()
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
literal|"got at least what did not expire"
argument_list|,
name|received
operator|.
name|get
argument_list|()
operator|>=
name|view
operator|.
name|getDequeues
argument_list|()
operator|.
name|getCount
argument_list|()
operator|-
name|view
operator|.
name|getExpired
argument_list|()
operator|.
name|getCount
argument_list|()
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
literal|"all messages expired - queue size gone to zero "
operator|+
name|view
operator|.
name|getMessages
argument_list|()
operator|.
name|getCount
argument_list|()
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
literal|"Stats: received: "
operator|+
name|received
operator|.
name|get
argument_list|()
operator|+
literal|", size= "
operator|+
name|view
operator|.
name|getMessages
argument_list|()
operator|.
name|getCount
argument_list|()
operator|+
literal|", enqueues: "
operator|+
name|view
operator|.
name|getEnqueues
argument_list|()
operator|.
name|getCount
argument_list|()
operator|+
literal|", dequeues: "
operator|+
name|view
operator|.
name|getDequeues
argument_list|()
operator|.
name|getCount
argument_list|()
operator|+
literal|", dispatched: "
operator|+
name|view
operator|.
name|getDispatched
argument_list|()
operator|.
name|getCount
argument_list|()
operator|+
literal|", inflight: "
operator|+
name|view
operator|.
name|getInflight
argument_list|()
operator|.
name|getCount
argument_list|()
operator|+
literal|", expiries: "
operator|+
name|view
operator|.
name|getExpired
argument_list|()
operator|.
name|getCount
argument_list|()
argument_list|)
expr_stmt|;
return|return
name|view
operator|.
name|getMessages
argument_list|()
operator|.
name|getCount
argument_list|()
operator|==
literal|0
return|;
block|}
block|}
argument_list|)
argument_list|)
expr_stmt|;
specifier|final
name|long
name|expiredBeforeEnqueue
init|=
name|numMessagesToSend
operator|-
name|view
operator|.
name|getEnqueues
argument_list|()
operator|.
name|getCount
argument_list|()
decl_stmt|;
specifier|final
name|long
name|totalExpiredCount
init|=
name|view
operator|.
name|getExpired
argument_list|()
operator|.
name|getCount
argument_list|()
operator|+
name|expiredBeforeEnqueue
decl_stmt|;
specifier|final
name|DestinationStatistics
name|dlqView
init|=
name|getDestinationStatistics
argument_list|(
name|dlqDestination
argument_list|)
decl_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"DLQ stats: size= "
operator|+
name|dlqView
operator|.
name|getMessages
argument_list|()
operator|.
name|getCount
argument_list|()
operator|+
literal|", enqueues: "
operator|+
name|dlqView
operator|.
name|getDequeues
argument_list|()
operator|.
name|getCount
argument_list|()
operator|+
literal|", dequeues: "
operator|+
name|dlqView
operator|.
name|getDequeues
argument_list|()
operator|.
name|getCount
argument_list|()
operator|+
literal|", dispatched: "
operator|+
name|dlqView
operator|.
name|getDispatched
argument_list|()
operator|.
name|getCount
argument_list|()
operator|+
literal|", inflight: "
operator|+
name|dlqView
operator|.
name|getInflight
argument_list|()
operator|.
name|getCount
argument_list|()
operator|+
literal|", expiries: "
operator|+
name|dlqView
operator|.
name|getExpired
argument_list|()
operator|.
name|getCount
argument_list|()
argument_list|)
expr_stmt|;
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
specifier|public
name|boolean
name|isSatisified
parameter_list|()
throws|throws
name|Exception
block|{
return|return
name|totalExpiredCount
operator|==
name|dlqView
operator|.
name|getMessages
argument_list|()
operator|.
name|getCount
argument_list|()
return|;
block|}
block|}
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"dlq contains all expired"
argument_list|,
name|totalExpiredCount
argument_list|,
name|dlqView
operator|.
name|getMessages
argument_list|()
operator|.
name|getCount
argument_list|()
argument_list|)
expr_stmt|;
comment|// memory check
name|assertEquals
argument_list|(
literal|"memory usage is back to duck egg"
argument_list|,
literal|0
argument_list|,
name|this
operator|.
name|getDestination
argument_list|(
name|destination
argument_list|)
operator|.
name|getMemoryUsage
argument_list|()
operator|.
name|getPercentUsage
argument_list|()
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
literal|"memory usage is increased "
argument_list|,
literal|0
operator|<
name|this
operator|.
name|getDestination
argument_list|(
name|dlqDestination
argument_list|)
operator|.
name|getMemoryUsage
argument_list|()
operator|.
name|getPercentUsage
argument_list|()
argument_list|)
expr_stmt|;
comment|// verify DLQ
name|MessageConsumer
name|dlqConsumer
init|=
name|createDlqConsumer
argument_list|(
name|connection
argument_list|)
decl_stmt|;
specifier|final
name|DLQListener
name|dlqListener
init|=
operator|new
name|DLQListener
argument_list|()
decl_stmt|;
name|dlqConsumer
operator|.
name|setMessageListener
argument_list|(
name|dlqListener
argument_list|)
expr_stmt|;
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
specifier|public
name|boolean
name|isSatisified
parameter_list|()
throws|throws
name|Exception
block|{
return|return
name|totalExpiredCount
operator|==
name|dlqListener
operator|.
name|count
return|;
block|}
block|}
argument_list|,
literal|60
operator|*
literal|1000
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"dlq returned all expired"
argument_list|,
name|dlqListener
operator|.
name|count
argument_list|,
name|totalExpiredCount
argument_list|)
expr_stmt|;
block|}
class|class
name|DLQListener
implements|implements
name|MessageListener
block|{
name|int
name|count
init|=
literal|0
decl_stmt|;
specifier|public
name|void
name|onMessage
parameter_list|(
name|Message
name|message
parameter_list|)
block|{
name|count
operator|++
expr_stmt|;
block|}
block|}
empty_stmt|;
specifier|private
name|MessageConsumer
name|createDlqConsumer
parameter_list|(
name|Connection
name|connection
parameter_list|)
throws|throws
name|Exception
block|{
return|return
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
name|dlqDestination
argument_list|)
return|;
block|}
specifier|public
name|void
name|initCombosForTestRecoverExpiredMessages
parameter_list|()
block|{
name|addCombinationValues
argument_list|(
literal|"useVMCursor"
argument_list|,
operator|new
name|Object
index|[]
block|{
name|Boolean
operator|.
name|TRUE
block|,
name|Boolean
operator|.
name|FALSE
block|}
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|testRecoverExpiredMessages
parameter_list|()
throws|throws
name|Exception
block|{
name|ActiveMQConnectionFactory
name|factory
init|=
operator|new
name|ActiveMQConnectionFactory
argument_list|(
literal|"failover://tcp://localhost:61616"
argument_list|)
decl_stmt|;
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
name|AUTO_ACKNOWLEDGE
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
name|producer
operator|.
name|setTimeToLive
argument_list|(
literal|2000
argument_list|)
expr_stmt|;
name|producer
operator|.
name|setDeliveryMode
argument_list|(
name|DeliveryMode
operator|.
name|PERSISTENT
argument_list|)
expr_stmt|;
name|Thread
name|producingThread
init|=
operator|new
name|Thread
argument_list|(
literal|"Producing Thread"
argument_list|)
block|{
specifier|public
name|void
name|run
parameter_list|()
block|{
try|try
block|{
name|int
name|i
init|=
literal|0
decl_stmt|;
while|while
condition|(
name|i
operator|++
operator|<
literal|1000
condition|)
block|{
name|Message
name|message
init|=
name|useTextMessage
condition|?
name|session
operator|.
name|createTextMessage
argument_list|(
literal|"test"
argument_list|)
else|:
name|session
operator|.
name|createObjectMessage
argument_list|(
literal|"test"
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
catch|catch
parameter_list|(
name|Throwable
name|ex
parameter_list|)
block|{
name|ex
operator|.
name|printStackTrace
argument_list|()
expr_stmt|;
block|}
block|}
block|}
decl_stmt|;
name|producingThread
operator|.
name|start
argument_list|()
expr_stmt|;
name|producingThread
operator|.
name|join
argument_list|()
expr_stmt|;
name|DestinationStatistics
name|view
init|=
name|getDestinationStatistics
argument_list|(
name|destination
argument_list|)
decl_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"Stats: size: "
operator|+
name|view
operator|.
name|getMessages
argument_list|()
operator|.
name|getCount
argument_list|()
operator|+
literal|", enqueues: "
operator|+
name|view
operator|.
name|getEnqueues
argument_list|()
operator|.
name|getCount
argument_list|()
operator|+
literal|", dequeues: "
operator|+
name|view
operator|.
name|getDequeues
argument_list|()
operator|.
name|getCount
argument_list|()
operator|+
literal|", dispatched: "
operator|+
name|view
operator|.
name|getDispatched
argument_list|()
operator|.
name|getCount
argument_list|()
operator|+
literal|", inflight: "
operator|+
name|view
operator|.
name|getInflight
argument_list|()
operator|.
name|getCount
argument_list|()
operator|+
literal|", expiries: "
operator|+
name|view
operator|.
name|getExpired
argument_list|()
operator|.
name|getCount
argument_list|()
argument_list|)
expr_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"stopping broker"
argument_list|)
expr_stmt|;
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
name|Thread
operator|.
name|sleep
argument_list|(
literal|5000
argument_list|)
expr_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"recovering broker"
argument_list|)
expr_stmt|;
specifier|final
name|boolean
name|deleteAllMessages
init|=
literal|false
decl_stmt|;
name|broker
operator|=
name|createBroker
argument_list|(
name|deleteAllMessages
argument_list|,
literal|5000
argument_list|)
expr_stmt|;
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
specifier|public
name|boolean
name|isSatisified
parameter_list|()
throws|throws
name|Exception
block|{
name|DestinationStatistics
name|view
init|=
name|getDestinationStatistics
argument_list|(
name|destination
argument_list|)
decl_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"Stats: size: "
operator|+
name|view
operator|.
name|getMessages
argument_list|()
operator|.
name|getCount
argument_list|()
operator|+
literal|", enqueues: "
operator|+
name|view
operator|.
name|getEnqueues
argument_list|()
operator|.
name|getCount
argument_list|()
operator|+
literal|", dequeues: "
operator|+
name|view
operator|.
name|getDequeues
argument_list|()
operator|.
name|getCount
argument_list|()
operator|+
literal|", dispatched: "
operator|+
name|view
operator|.
name|getDispatched
argument_list|()
operator|.
name|getCount
argument_list|()
operator|+
literal|", inflight: "
operator|+
name|view
operator|.
name|getInflight
argument_list|()
operator|.
name|getCount
argument_list|()
operator|+
literal|", expiries: "
operator|+
name|view
operator|.
name|getExpired
argument_list|()
operator|.
name|getCount
argument_list|()
argument_list|)
expr_stmt|;
return|return
name|view
operator|.
name|getMessages
argument_list|()
operator|.
name|getCount
argument_list|()
operator|==
literal|0
return|;
block|}
block|}
argument_list|)
expr_stmt|;
name|view
operator|=
name|getDestinationStatistics
argument_list|(
name|destination
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"Expect empty queue, QueueSize: "
argument_list|,
literal|0
argument_list|,
name|view
operator|.
name|getMessages
argument_list|()
operator|.
name|getCount
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"all dequeues were expired"
argument_list|,
name|view
operator|.
name|getDequeues
argument_list|()
operator|.
name|getCount
argument_list|()
argument_list|,
name|view
operator|.
name|getExpired
argument_list|()
operator|.
name|getCount
argument_list|()
argument_list|)
expr_stmt|;
block|}
specifier|private
name|BrokerService
name|createBroker
parameter_list|(
name|boolean
name|deleteAllMessages
parameter_list|,
name|long
name|expireMessagesPeriod
parameter_list|)
throws|throws
name|Exception
block|{
name|BrokerService
name|broker
init|=
operator|new
name|BrokerService
argument_list|()
decl_stmt|;
name|broker
operator|.
name|setBrokerName
argument_list|(
literal|"localhost"
argument_list|)
expr_stmt|;
name|broker
operator|.
name|setDestinations
argument_list|(
operator|new
name|ActiveMQDestination
index|[]
block|{
name|destination
block|}
argument_list|)
expr_stmt|;
name|AMQPersistenceAdapter
name|adaptor
init|=
operator|new
name|AMQPersistenceAdapter
argument_list|()
decl_stmt|;
name|adaptor
operator|.
name|setDirectory
argument_list|(
operator|new
name|File
argument_list|(
literal|"target/expiredtest-data/"
argument_list|)
argument_list|)
expr_stmt|;
name|adaptor
operator|.
name|setForceRecoverReferenceStore
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|broker
operator|.
name|setPersistenceAdapter
argument_list|(
name|adaptor
argument_list|)
expr_stmt|;
name|PolicyEntry
name|defaultPolicy
init|=
operator|new
name|PolicyEntry
argument_list|()
decl_stmt|;
if|if
condition|(
name|useVMCursor
condition|)
block|{
name|defaultPolicy
operator|.
name|setPendingQueuePolicy
argument_list|(
operator|new
name|VMPendingQueueMessageStoragePolicy
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|defaultPolicy
operator|.
name|setExpireMessagesPeriod
argument_list|(
name|expireMessagesPeriod
argument_list|)
expr_stmt|;
name|defaultPolicy
operator|.
name|setMaxExpirePageSize
argument_list|(
literal|1200
argument_list|)
expr_stmt|;
name|PolicyMap
name|policyMap
init|=
operator|new
name|PolicyMap
argument_list|()
decl_stmt|;
name|policyMap
operator|.
name|setDefaultEntry
argument_list|(
name|defaultPolicy
argument_list|)
expr_stmt|;
name|broker
operator|.
name|setDestinationPolicy
argument_list|(
name|policyMap
argument_list|)
expr_stmt|;
name|broker
operator|.
name|setDeleteAllMessagesOnStartup
argument_list|(
name|deleteAllMessages
argument_list|)
expr_stmt|;
name|broker
operator|.
name|addConnector
argument_list|(
literal|"tcp://localhost:61616"
argument_list|)
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
return|return
name|broker
return|;
block|}
specifier|private
name|DestinationStatistics
name|getDestinationStatistics
parameter_list|(
name|ActiveMQDestination
name|destination
parameter_list|)
block|{
name|DestinationStatistics
name|result
init|=
literal|null
decl_stmt|;
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
name|Destination
name|dest
init|=
name|getDestination
argument_list|(
name|destination
argument_list|)
decl_stmt|;
if|if
condition|(
name|dest
operator|!=
literal|null
condition|)
block|{
name|result
operator|=
name|dest
operator|.
name|getDestinationStatistics
argument_list|()
expr_stmt|;
block|}
return|return
name|result
return|;
block|}
specifier|private
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
name|Destination
name|getDestination
parameter_list|(
name|ActiveMQDestination
name|destination
parameter_list|)
block|{
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
name|Destination
name|result
init|=
literal|null
decl_stmt|;
name|RegionBroker
name|regionBroker
init|=
operator|(
name|RegionBroker
operator|)
name|broker
operator|.
name|getRegionBroker
argument_list|()
decl_stmt|;
for|for
control|(
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
name|Destination
name|dest
range|:
name|regionBroker
operator|.
name|getQueueRegion
argument_list|()
operator|.
name|getDestinationMap
argument_list|()
operator|.
name|values
argument_list|()
control|)
block|{
if|if
condition|(
name|dest
operator|.
name|getName
argument_list|()
operator|.
name|equals
argument_list|(
name|destination
operator|.
name|getPhysicalName
argument_list|()
argument_list|)
condition|)
block|{
name|result
operator|=
name|dest
expr_stmt|;
break|break;
block|}
block|}
return|return
name|result
return|;
block|}
specifier|protected
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
name|broker
operator|.
name|waitUntilStopped
argument_list|()
expr_stmt|;
block|}
block|}
end_class

end_unit

