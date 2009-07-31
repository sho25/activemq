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
name|DestinationViewMBean
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
name|management
operator|.
name|ObjectName
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

begin_class
specifier|public
class|class
name|ExpiredMessagesWithNoConsumerTest
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
name|ExpiredMessagesWithNoConsumerTest
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
specifier|static
name|Test
name|suite
parameter_list|()
block|{
return|return
name|suite
argument_list|(
name|ExpiredMessagesWithNoConsumerTest
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
name|createBrokerWithMemoryLimit
parameter_list|()
throws|throws
name|Exception
block|{
name|doCreateBroker
argument_list|(
literal|true
argument_list|)
expr_stmt|;
block|}
specifier|protected
name|void
name|createBroker
parameter_list|()
throws|throws
name|Exception
block|{
name|doCreateBroker
argument_list|(
literal|false
argument_list|)
expr_stmt|;
block|}
specifier|private
name|void
name|doCreateBroker
parameter_list|(
name|boolean
name|memoryLimit
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
name|setBrokerName
argument_list|(
literal|"localhost"
argument_list|)
expr_stmt|;
name|broker
operator|.
name|setDataDirectory
argument_list|(
literal|"data/"
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
name|addConnector
argument_list|(
literal|"tcp://localhost:61616"
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
name|defaultEntry
operator|.
name|setExpireMessagesPeriod
argument_list|(
literal|100
argument_list|)
expr_stmt|;
name|defaultEntry
operator|.
name|setMaxExpirePageSize
argument_list|(
literal|800
argument_list|)
expr_stmt|;
if|if
condition|(
name|memoryLimit
condition|)
block|{
comment|// so memory is not consumed by DLQ turn if off
name|defaultEntry
operator|.
name|setDeadLetterStrategy
argument_list|(
literal|null
argument_list|)
expr_stmt|;
name|defaultEntry
operator|.
name|setMemoryLimit
argument_list|(
literal|200
operator|*
literal|1000
argument_list|)
expr_stmt|;
block|}
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
block|}
specifier|public
name|void
name|testExpiredMessagesWithNoConsumer
parameter_list|()
throws|throws
name|Exception
block|{
name|createBrokerWithMemoryLimit
argument_list|()
expr_stmt|;
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
literal|1000
argument_list|)
expr_stmt|;
name|connection
operator|.
name|start
argument_list|()
expr_stmt|;
specifier|final
name|long
name|sendCount
init|=
literal|2000
decl_stmt|;
specifier|final
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
name|long
name|tStamp
init|=
name|System
operator|.
name|currentTimeMillis
argument_list|()
decl_stmt|;
while|while
condition|(
name|i
operator|++
operator|<
name|sendCount
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
if|if
condition|(
name|i
operator|%
literal|100
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
operator|+
literal|" @ "
operator|+
operator|(
operator|(
name|System
operator|.
name|currentTimeMillis
argument_list|()
operator|-
name|tStamp
operator|)
operator|/
literal|100
operator|)
operator|+
literal|"m/ms"
argument_list|)
expr_stmt|;
name|tStamp
operator|=
name|System
operator|.
name|currentTimeMillis
argument_list|()
expr_stmt|;
block|}
block|}
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
name|assertTrue
argument_list|(
literal|"producer completed within time"
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
name|producingThread
operator|.
name|join
argument_list|(
literal|1000
argument_list|)
expr_stmt|;
return|return
operator|!
name|producingThread
operator|.
name|isAlive
argument_list|()
return|;
block|}
block|}
argument_list|)
argument_list|)
expr_stmt|;
specifier|final
name|DestinationViewMBean
name|view
init|=
name|createView
argument_list|(
name|destination
argument_list|)
decl_stmt|;
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
name|sendCount
operator|==
name|view
operator|.
name|getExpiredCount
argument_list|()
return|;
block|}
block|}
argument_list|)
expr_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"enqueue="
operator|+
name|view
operator|.
name|getEnqueueCount
argument_list|()
operator|+
literal|", dequeue="
operator|+
name|view
operator|.
name|getDequeueCount
argument_list|()
operator|+
literal|", inflight="
operator|+
name|view
operator|.
name|getInFlightCount
argument_list|()
operator|+
literal|", expired= "
operator|+
name|view
operator|.
name|getExpiredCount
argument_list|()
operator|+
literal|", size= "
operator|+
name|view
operator|.
name|getQueueSize
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"All sent have expired"
argument_list|,
name|sendCount
argument_list|,
name|view
operator|.
name|getExpiredCount
argument_list|()
argument_list|)
expr_stmt|;
block|}
comment|// first ack delivered after expiry
specifier|public
name|void
name|testExpiredMessagesWithVerySlowConsumer
parameter_list|()
throws|throws
name|Exception
block|{
name|createBroker
argument_list|()
expr_stmt|;
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
name|CLIENT_ACKNOWLEDGE
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
specifier|final
name|int
name|ttl
init|=
literal|4000
decl_stmt|;
name|producer
operator|.
name|setTimeToLive
argument_list|(
name|ttl
argument_list|)
expr_stmt|;
specifier|final
name|long
name|sendCount
init|=
literal|1500
decl_stmt|;
specifier|final
name|CountDownLatch
name|receivedOneCondition
init|=
operator|new
name|CountDownLatch
argument_list|(
literal|1
argument_list|)
decl_stmt|;
specifier|final
name|CountDownLatch
name|waitCondition
init|=
operator|new
name|CountDownLatch
argument_list|(
literal|1
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
name|consumer
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
try|try
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"Got my message: "
operator|+
name|message
argument_list|)
expr_stmt|;
name|receivedOneCondition
operator|.
name|countDown
argument_list|()
expr_stmt|;
name|waitCondition
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
literal|"acking message: "
operator|+
name|message
argument_list|)
expr_stmt|;
name|message
operator|.
name|acknowledge
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
name|connection
operator|.
name|start
argument_list|()
expr_stmt|;
specifier|final
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
name|long
name|tStamp
init|=
name|System
operator|.
name|currentTimeMillis
argument_list|()
decl_stmt|;
while|while
condition|(
name|i
operator|++
operator|<
name|sendCount
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
if|if
condition|(
name|i
operator|%
literal|100
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
operator|+
literal|" @ "
operator|+
operator|(
operator|(
name|System
operator|.
name|currentTimeMillis
argument_list|()
operator|-
name|tStamp
operator|)
operator|/
literal|100
operator|)
operator|+
literal|"m/ms"
argument_list|)
expr_stmt|;
name|tStamp
operator|=
name|System
operator|.
name|currentTimeMillis
argument_list|()
expr_stmt|;
block|}
block|}
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
name|assertTrue
argument_list|(
literal|"got one message"
argument_list|,
name|receivedOneCondition
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
name|assertTrue
argument_list|(
literal|"producer completed within time "
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
name|producingThread
operator|.
name|join
argument_list|(
literal|1000
argument_list|)
expr_stmt|;
return|return
operator|!
name|producingThread
operator|.
name|isAlive
argument_list|()
return|;
block|}
block|}
argument_list|)
argument_list|)
expr_stmt|;
specifier|final
name|DestinationViewMBean
name|view
init|=
name|createView
argument_list|(
name|destination
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
literal|"all dispatched up to default prefetch "
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
literal|1000
operator|==
name|view
operator|.
name|getDispatchCount
argument_list|()
return|;
block|}
block|}
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
literal|"All sent have expired "
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
name|sendCount
operator|==
name|view
operator|.
name|getExpiredCount
argument_list|()
return|;
block|}
block|}
argument_list|)
argument_list|)
expr_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"enqueue="
operator|+
name|view
operator|.
name|getEnqueueCount
argument_list|()
operator|+
literal|", dequeue="
operator|+
name|view
operator|.
name|getDequeueCount
argument_list|()
operator|+
literal|", inflight="
operator|+
name|view
operator|.
name|getInFlightCount
argument_list|()
operator|+
literal|", expired= "
operator|+
name|view
operator|.
name|getExpiredCount
argument_list|()
operator|+
literal|", size= "
operator|+
name|view
operator|.
name|getQueueSize
argument_list|()
argument_list|)
expr_stmt|;
comment|// let the ack happen
name|waitCondition
operator|.
name|countDown
argument_list|()
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
literal|0
operator|==
name|view
operator|.
name|getInFlightCount
argument_list|()
return|;
block|}
block|}
argument_list|)
expr_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"enqueue="
operator|+
name|view
operator|.
name|getEnqueueCount
argument_list|()
operator|+
literal|", dequeue="
operator|+
name|view
operator|.
name|getDequeueCount
argument_list|()
operator|+
literal|", inflight="
operator|+
name|view
operator|.
name|getInFlightCount
argument_list|()
operator|+
literal|", expired= "
operator|+
name|view
operator|.
name|getExpiredCount
argument_list|()
operator|+
literal|", size= "
operator|+
name|view
operator|.
name|getQueueSize
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"prefetch gets back to 0 "
argument_list|,
literal|0
argument_list|,
name|view
operator|.
name|getInFlightCount
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"size gets back to 0 "
argument_list|,
literal|0
argument_list|,
name|view
operator|.
name|getQueueSize
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"dequeues match sent/expired "
argument_list|,
name|sendCount
argument_list|,
name|view
operator|.
name|getDequeueCount
argument_list|()
argument_list|)
expr_stmt|;
name|consumer
operator|.
name|close
argument_list|()
expr_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"done: "
operator|+
name|getName
argument_list|()
argument_list|)
expr_stmt|;
block|}
specifier|protected
name|DestinationViewMBean
name|createView
parameter_list|(
name|ActiveMQDestination
name|destination
parameter_list|)
throws|throws
name|Exception
block|{
name|String
name|domain
init|=
literal|"org.apache.activemq"
decl_stmt|;
name|ObjectName
name|name
decl_stmt|;
if|if
condition|(
name|destination
operator|.
name|isQueue
argument_list|()
condition|)
block|{
name|name
operator|=
operator|new
name|ObjectName
argument_list|(
name|domain
operator|+
literal|":BrokerName=localhost,Type=Queue,Destination=test"
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|name
operator|=
operator|new
name|ObjectName
argument_list|(
name|domain
operator|+
literal|":BrokerName=localhost,Type=Topic,Destination=test"
argument_list|)
expr_stmt|;
block|}
return|return
operator|(
name|DestinationViewMBean
operator|)
name|broker
operator|.
name|getManagementContext
argument_list|()
operator|.
name|newProxyInstance
argument_list|(
name|name
argument_list|,
name|DestinationViewMBean
operator|.
name|class
argument_list|,
literal|true
argument_list|)
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

