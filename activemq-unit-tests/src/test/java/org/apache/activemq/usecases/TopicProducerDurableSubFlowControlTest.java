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
name|Arrays
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
name|Message
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
name|TopicSubscriber
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
name|TestCase
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
name|broker
operator|.
name|jmx
operator|.
name|DurableSubscriptionViewMBean
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
name|TopicProducerDurableSubFlowControlTest
extends|extends
name|TestCase
implements|implements
name|MessageListener
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
name|TopicProducerDurableSubFlowControlTest
operator|.
name|class
argument_list|)
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|String
name|brokerName
init|=
literal|"testBroker"
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|String
name|brokerUrl
init|=
literal|"vm://"
operator|+
name|brokerName
decl_stmt|;
specifier|protected
specifier|static
specifier|final
name|int
name|destinationMemLimit
init|=
literal|2097152
decl_stmt|;
comment|// 2MB
specifier|private
specifier|static
specifier|final
name|AtomicLong
name|produced
init|=
operator|new
name|AtomicLong
argument_list|()
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|AtomicLong
name|consumed
init|=
operator|new
name|AtomicLong
argument_list|()
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|int
name|numMessagesToSend
init|=
literal|10000
decl_stmt|;
specifier|private
name|BrokerService
name|broker
decl_stmt|;
specifier|protected
name|void
name|setUp
parameter_list|()
throws|throws
name|Exception
block|{
name|doSetup
argument_list|(
literal|true
argument_list|)
expr_stmt|;
block|}
specifier|private
name|void
name|doSetup
parameter_list|(
name|boolean
name|deleteAll
parameter_list|)
throws|throws
name|Exception
block|{
comment|// Setup and start the broker
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
name|brokerName
argument_list|)
expr_stmt|;
name|broker
operator|.
name|setSchedulerSupport
argument_list|(
literal|false
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
name|setUseShutdownHook
argument_list|(
literal|false
argument_list|)
expr_stmt|;
name|broker
operator|.
name|addConnector
argument_list|(
name|brokerUrl
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
name|getSystemUsage
argument_list|()
operator|.
name|getMemoryUsage
argument_list|()
operator|.
name|setLimit
argument_list|(
name|destinationMemLimit
operator|*
literal|10
argument_list|)
expr_stmt|;
name|broker
operator|.
name|setDeleteAllMessagesOnStartup
argument_list|(
name|deleteAll
argument_list|)
expr_stmt|;
comment|// Setup the destination policy
name|PolicyMap
name|pm
init|=
operator|new
name|PolicyMap
argument_list|()
decl_stmt|;
comment|// Setup the topic destination policy
name|PolicyEntry
name|tpe
init|=
operator|new
name|PolicyEntry
argument_list|()
decl_stmt|;
name|tpe
operator|.
name|setTopic
argument_list|(
literal|">"
argument_list|)
expr_stmt|;
name|tpe
operator|.
name|setMemoryLimit
argument_list|(
name|destinationMemLimit
argument_list|)
expr_stmt|;
name|tpe
operator|.
name|setCursorMemoryHighWaterMark
argument_list|(
literal|10
argument_list|)
expr_stmt|;
name|tpe
operator|.
name|setProducerFlowControl
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|tpe
operator|.
name|setAdvisoryWhenFull
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|tpe
operator|.
name|setExpireMessagesPeriod
argument_list|(
literal|0
argument_list|)
expr_stmt|;
name|pm
operator|.
name|setPolicyEntries
argument_list|(
name|Arrays
operator|.
name|asList
argument_list|(
operator|new
name|PolicyEntry
index|[]
block|{
name|tpe
block|}
argument_list|)
argument_list|)
expr_stmt|;
name|setDestinationPolicy
argument_list|(
name|broker
argument_list|,
name|pm
argument_list|)
expr_stmt|;
comment|// Start the broker
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
specifier|protected
name|void
name|setDestinationPolicy
parameter_list|(
name|BrokerService
name|broker
parameter_list|,
name|PolicyMap
name|pm
parameter_list|)
block|{
name|broker
operator|.
name|setDestinationPolicy
argument_list|(
name|pm
argument_list|)
expr_stmt|;
block|}
specifier|protected
name|void
name|tearDown
parameter_list|()
throws|throws
name|Exception
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
specifier|public
name|void
name|testTopicProducerFlowControl
parameter_list|()
throws|throws
name|Exception
block|{
comment|// Create the connection factory
name|ActiveMQConnectionFactory
name|connectionFactory
init|=
operator|new
name|ActiveMQConnectionFactory
argument_list|(
name|brokerUrl
argument_list|)
decl_stmt|;
name|connectionFactory
operator|.
name|setAlwaysSyncSend
argument_list|(
literal|true
argument_list|)
expr_stmt|;
comment|// Start the test destination listener
name|Connection
name|c
init|=
name|connectionFactory
operator|.
name|createConnection
argument_list|()
decl_stmt|;
name|c
operator|.
name|setClientID
argument_list|(
literal|"cliId1"
argument_list|)
expr_stmt|;
name|c
operator|.
name|start
argument_list|()
expr_stmt|;
name|Session
name|listenerSession
init|=
name|c
operator|.
name|createSession
argument_list|(
literal|false
argument_list|,
literal|1
argument_list|)
decl_stmt|;
name|TopicSubscriber
name|durable
init|=
name|listenerSession
operator|.
name|createDurableSubscriber
argument_list|(
name|createDestination
argument_list|()
argument_list|,
literal|"DurableSub-0"
argument_list|)
decl_stmt|;
name|durable
operator|.
name|close
argument_list|()
expr_stmt|;
name|durable
operator|=
name|listenerSession
operator|.
name|createDurableSubscriber
argument_list|(
name|createDestination
argument_list|()
argument_list|,
literal|"DurableSub-1"
argument_list|)
expr_stmt|;
name|durable
operator|.
name|setMessageListener
argument_list|(
name|this
argument_list|)
expr_stmt|;
comment|// Start producing the test messages
specifier|final
name|Session
name|session
init|=
name|connectionFactory
operator|.
name|createConnection
argument_list|()
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
name|MessageProducer
name|producer
init|=
name|session
operator|.
name|createProducer
argument_list|(
name|createDestination
argument_list|()
argument_list|)
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
for|for
control|(
name|long
name|i
init|=
literal|0
init|;
name|i
operator|<
name|numMessagesToSend
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
literal|"test"
argument_list|)
argument_list|)
expr_stmt|;
name|long
name|count
init|=
name|produced
operator|.
name|incrementAndGet
argument_list|()
decl_stmt|;
if|if
condition|(
name|count
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
literal|"Produced "
operator|+
name|count
operator|+
literal|" messages"
argument_list|)
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
finally|finally
block|{
try|try
block|{
name|producer
operator|.
name|close
argument_list|()
expr_stmt|;
name|session
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
block|{                     }
block|}
block|}
block|}
decl_stmt|;
name|producingThread
operator|.
name|start
argument_list|()
expr_stmt|;
name|ArrayList
argument_list|<
name|ObjectName
argument_list|>
name|subON
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
specifier|final
name|ArrayList
argument_list|<
name|DurableSubscriptionViewMBean
argument_list|>
name|subViews
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
name|subON
operator|.
name|addAll
argument_list|(
name|Arrays
operator|.
name|asList
argument_list|(
name|broker
operator|.
name|getAdminView
argument_list|()
operator|.
name|getInactiveDurableTopicSubscribers
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|subON
operator|.
name|addAll
argument_list|(
name|Arrays
operator|.
name|asList
argument_list|(
name|broker
operator|.
name|getAdminView
argument_list|()
operator|.
name|getDurableTopicSubscribers
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
literal|"have a sub"
argument_list|,
operator|!
name|subON
operator|.
name|isEmpty
argument_list|()
argument_list|)
expr_stmt|;
for|for
control|(
name|ObjectName
name|subName
range|:
name|subON
control|)
block|{
name|subViews
operator|.
name|add
argument_list|(
operator|(
name|DurableSubscriptionViewMBean
operator|)
name|broker
operator|.
name|getManagementContext
argument_list|()
operator|.
name|newProxyInstance
argument_list|(
name|subName
argument_list|,
name|DurableSubscriptionViewMBean
operator|.
name|class
argument_list|,
literal|true
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|LOG
operator|.
name|info
argument_list|(
literal|"Wait for producer to stop"
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
literal|"producer thread is done"
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
for|for
control|(
name|DurableSubscriptionViewMBean
name|sub
range|:
name|subViews
control|)
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"name: "
operator|+
name|sub
operator|.
name|getSubscriptionName
argument_list|()
argument_list|)
expr_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"cursor size: "
operator|+
name|sub
operator|.
name|cursorSize
argument_list|()
argument_list|)
expr_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"mem usage: "
operator|+
name|sub
operator|.
name|getCursorMemoryUsage
argument_list|()
argument_list|)
expr_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"mem % usage: "
operator|+
name|sub
operator|.
name|getCursorPercentUsage
argument_list|()
argument_list|)
expr_stmt|;
block|}
return|return
operator|!
name|producingThread
operator|.
name|isAlive
argument_list|()
return|;
block|}
block|}
argument_list|,
literal|10
operator|*
literal|60
operator|*
literal|1000
argument_list|)
argument_list|)
expr_stmt|;
for|for
control|(
name|DurableSubscriptionViewMBean
name|sub
range|:
name|subViews
control|)
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"name: "
operator|+
name|sub
operator|.
name|getSubscriptionName
argument_list|()
argument_list|)
expr_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"cursor size: "
operator|+
name|sub
operator|.
name|cursorSize
argument_list|()
argument_list|)
expr_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"mem usage: "
operator|+
name|sub
operator|.
name|getCursorMemoryUsage
argument_list|()
argument_list|)
expr_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"mem % usage: "
operator|+
name|sub
operator|.
name|getCursorPercentUsage
argument_list|()
argument_list|)
expr_stmt|;
if|if
condition|(
name|sub
operator|.
name|cursorSize
argument_list|()
operator|>
literal|0
condition|)
block|{
name|assertTrue
argument_list|(
literal|"Has a decent usage"
argument_list|,
name|sub
operator|.
name|getCursorPercentUsage
argument_list|()
operator|>
literal|5
argument_list|)
expr_stmt|;
block|}
block|}
block|}
specifier|protected
name|ActiveMQTopic
name|createDestination
parameter_list|()
throws|throws
name|Exception
block|{
return|return
operator|new
name|ActiveMQTopic
argument_list|(
literal|"test"
argument_list|)
return|;
block|}
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
name|long
name|count
init|=
name|consumed
operator|.
name|incrementAndGet
argument_list|()
decl_stmt|;
if|if
condition|(
name|count
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
literal|"\tConsumed "
operator|+
name|count
operator|+
literal|" messages"
argument_list|)
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

