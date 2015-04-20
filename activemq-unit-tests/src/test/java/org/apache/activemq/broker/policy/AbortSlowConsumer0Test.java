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
name|policy
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
name|lang
operator|.
name|reflect
operator|.
name|UndeclaredThrowableException
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
name|Collection
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Map
operator|.
name|Entry
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
name|JMSException
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
name|Session
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|management
operator|.
name|InstanceNotFoundException
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
name|javax
operator|.
name|management
operator|.
name|openmbean
operator|.
name|CompositeData
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|management
operator|.
name|openmbean
operator|.
name|TabularData
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
name|ActiveMQMessageConsumer
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
name|jmx
operator|.
name|AbortSlowConsumerStrategyViewMBean
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
name|MessageIdList
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
name|SocketProxy
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
name|Test
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
name|Parameterized
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
name|value
operator|=
name|Parameterized
operator|.
name|class
argument_list|)
specifier|public
class|class
name|AbortSlowConsumer0Test
extends|extends
name|AbortSlowConsumerBase
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
name|AbortSlowConsumer0Test
operator|.
name|class
argument_list|)
decl_stmt|;
annotation|@
name|Parameterized
operator|.
name|Parameters
argument_list|(
name|name
operator|=
literal|"isTopic({0})"
argument_list|)
specifier|public
specifier|static
name|Collection
argument_list|<
name|Object
index|[]
argument_list|>
name|getTestParameters
parameter_list|()
block|{
return|return
name|Arrays
operator|.
name|asList
argument_list|(
operator|new
name|Object
index|[]
index|[]
block|{
block|{
name|Boolean
operator|.
name|TRUE
block|}
block|,
block|{
name|Boolean
operator|.
name|FALSE
block|}
block|}
argument_list|)
return|;
block|}
specifier|public
name|AbortSlowConsumer0Test
parameter_list|(
name|Boolean
name|isTopic
parameter_list|)
block|{
name|this
operator|.
name|topic
operator|=
name|isTopic
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testRegularConsumerIsNotAborted
parameter_list|()
throws|throws
name|Exception
block|{
name|startConsumers
argument_list|(
name|destination
argument_list|)
expr_stmt|;
for|for
control|(
name|Connection
name|c
range|:
name|connections
control|)
block|{
name|c
operator|.
name|setExceptionListener
argument_list|(
name|this
argument_list|)
expr_stmt|;
block|}
name|startProducers
argument_list|(
name|destination
argument_list|,
literal|100
argument_list|)
expr_stmt|;
name|allMessagesList
operator|.
name|waitForMessagesToArrive
argument_list|(
literal|10
argument_list|)
expr_stmt|;
name|allMessagesList
operator|.
name|assertAtLeastMessagesReceived
argument_list|(
literal|10
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testSlowConsumerIsAbortedViaJmx
parameter_list|()
throws|throws
name|Exception
block|{
name|underTest
operator|.
name|setMaxSlowDuration
argument_list|(
literal|60
operator|*
literal|1000
argument_list|)
expr_stmt|;
comment|// so jmx does the abort
name|startConsumers
argument_list|(
name|withPrefetch
argument_list|(
literal|2
argument_list|,
name|destination
argument_list|)
argument_list|)
expr_stmt|;
name|Entry
argument_list|<
name|MessageConsumer
argument_list|,
name|MessageIdList
argument_list|>
name|consumertoAbort
init|=
name|consumers
operator|.
name|entrySet
argument_list|()
operator|.
name|iterator
argument_list|()
operator|.
name|next
argument_list|()
decl_stmt|;
name|consumertoAbort
operator|.
name|getValue
argument_list|()
operator|.
name|setProcessingDelay
argument_list|(
literal|8
operator|*
literal|1000
argument_list|)
expr_stmt|;
for|for
control|(
name|Connection
name|c
range|:
name|connections
control|)
block|{
name|c
operator|.
name|setExceptionListener
argument_list|(
name|this
argument_list|)
expr_stmt|;
block|}
name|startProducers
argument_list|(
name|destination
argument_list|,
literal|100
argument_list|)
expr_stmt|;
name|consumertoAbort
operator|.
name|getValue
argument_list|()
operator|.
name|assertMessagesReceived
argument_list|(
literal|1
argument_list|)
expr_stmt|;
name|ActiveMQDestination
name|amqDest
init|=
operator|(
name|ActiveMQDestination
operator|)
name|destination
decl_stmt|;
name|ObjectName
name|destinationViewMBean
init|=
operator|new
name|ObjectName
argument_list|(
literal|"org.apache.activemq:destinationType="
operator|+
operator|(
name|amqDest
operator|.
name|isTopic
argument_list|()
condition|?
literal|"Topic"
else|:
literal|"Queue"
operator|)
operator|+
literal|",destinationName="
operator|+
name|amqDest
operator|.
name|getPhysicalName
argument_list|()
operator|+
literal|",type=Broker,brokerName=localhost"
argument_list|)
decl_stmt|;
name|DestinationViewMBean
name|queue
init|=
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
name|destinationViewMBean
argument_list|,
name|DestinationViewMBean
operator|.
name|class
argument_list|,
literal|true
argument_list|)
decl_stmt|;
name|ObjectName
name|slowConsumerPolicyMBeanName
init|=
name|queue
operator|.
name|getSlowConsumerStrategy
argument_list|()
decl_stmt|;
name|assertNotNull
argument_list|(
name|slowConsumerPolicyMBeanName
argument_list|)
expr_stmt|;
name|AbortSlowConsumerStrategyViewMBean
name|abortPolicy
init|=
operator|(
name|AbortSlowConsumerStrategyViewMBean
operator|)
name|broker
operator|.
name|getManagementContext
argument_list|()
operator|.
name|newProxyInstance
argument_list|(
name|slowConsumerPolicyMBeanName
argument_list|,
name|AbortSlowConsumerStrategyViewMBean
operator|.
name|class
argument_list|,
literal|true
argument_list|)
decl_stmt|;
name|TimeUnit
operator|.
name|SECONDS
operator|.
name|sleep
argument_list|(
literal|3
argument_list|)
expr_stmt|;
name|TabularData
name|slowOnes
init|=
name|abortPolicy
operator|.
name|getSlowConsumers
argument_list|()
decl_stmt|;
name|assertEquals
argument_list|(
literal|"one slow consumers"
argument_list|,
literal|1
argument_list|,
name|slowOnes
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"slow ones:"
operator|+
name|slowOnes
argument_list|)
expr_stmt|;
name|CompositeData
name|slowOne
init|=
operator|(
name|CompositeData
operator|)
name|slowOnes
operator|.
name|values
argument_list|()
operator|.
name|iterator
argument_list|()
operator|.
name|next
argument_list|()
decl_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"Slow one: "
operator|+
name|slowOne
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
literal|"we have an object name"
argument_list|,
name|slowOne
operator|.
name|get
argument_list|(
literal|"subscription"
argument_list|)
operator|instanceof
name|ObjectName
argument_list|)
expr_stmt|;
name|abortPolicy
operator|.
name|abortConsumer
argument_list|(
operator|(
name|ObjectName
operator|)
name|slowOne
operator|.
name|get
argument_list|(
literal|"subscription"
argument_list|)
argument_list|)
expr_stmt|;
name|consumertoAbort
operator|.
name|getValue
argument_list|()
operator|.
name|assertAtMostMessagesReceived
argument_list|(
literal|1
argument_list|)
expr_stmt|;
name|slowOnes
operator|=
name|abortPolicy
operator|.
name|getSlowConsumers
argument_list|()
expr_stmt|;
name|assertEquals
argument_list|(
literal|"no slow consumers left"
argument_list|,
literal|0
argument_list|,
name|slowOnes
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
comment|// verify mbean gone with destination
name|broker
operator|.
name|getAdminView
argument_list|()
operator|.
name|removeTopic
argument_list|(
name|amqDest
operator|.
name|getPhysicalName
argument_list|()
argument_list|)
expr_stmt|;
try|try
block|{
name|abortPolicy
operator|.
name|getSlowConsumers
argument_list|()
expr_stmt|;
name|fail
argument_list|(
literal|"expect not found post destination removal"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|UndeclaredThrowableException
name|expected
parameter_list|)
block|{
name|assertTrue
argument_list|(
literal|"correct exception: "
operator|+
name|expected
operator|.
name|getCause
argument_list|()
argument_list|,
name|expected
operator|.
name|getCause
argument_list|()
operator|instanceof
name|InstanceNotFoundException
argument_list|)
expr_stmt|;
block|}
block|}
specifier|private
name|Destination
name|withPrefetch
parameter_list|(
name|int
name|i
parameter_list|,
name|Destination
name|destination
parameter_list|)
block|{
name|String
name|destWithPrefetch
init|=
operator|(
operator|(
name|ActiveMQDestination
operator|)
name|destination
operator|)
operator|.
name|getPhysicalName
argument_list|()
operator|+
literal|"?consumer.prefetchSize="
operator|+
name|i
decl_stmt|;
return|return
name|topic
condition|?
operator|new
name|ActiveMQTopic
argument_list|(
name|destWithPrefetch
argument_list|)
else|:
operator|new
name|ActiveMQQueue
argument_list|(
name|destWithPrefetch
argument_list|)
return|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testOnlyOneSlowConsumerIsAborted
parameter_list|()
throws|throws
name|Exception
block|{
name|consumerCount
operator|=
literal|10
expr_stmt|;
name|startConsumers
argument_list|(
name|destination
argument_list|)
expr_stmt|;
name|Entry
argument_list|<
name|MessageConsumer
argument_list|,
name|MessageIdList
argument_list|>
name|consumertoAbort
init|=
name|consumers
operator|.
name|entrySet
argument_list|()
operator|.
name|iterator
argument_list|()
operator|.
name|next
argument_list|()
decl_stmt|;
name|consumertoAbort
operator|.
name|getValue
argument_list|()
operator|.
name|setProcessingDelay
argument_list|(
literal|8
operator|*
literal|1000
argument_list|)
expr_stmt|;
for|for
control|(
name|Connection
name|c
range|:
name|connections
control|)
block|{
name|c
operator|.
name|setExceptionListener
argument_list|(
name|this
argument_list|)
expr_stmt|;
block|}
name|startProducers
argument_list|(
name|destination
argument_list|,
literal|100
argument_list|)
expr_stmt|;
name|allMessagesList
operator|.
name|waitForMessagesToArrive
argument_list|(
literal|99
argument_list|)
expr_stmt|;
name|allMessagesList
operator|.
name|assertAtLeastMessagesReceived
argument_list|(
literal|99
argument_list|)
expr_stmt|;
name|consumertoAbort
operator|.
name|getValue
argument_list|()
operator|.
name|assertMessagesReceived
argument_list|(
literal|1
argument_list|)
expr_stmt|;
name|TimeUnit
operator|.
name|SECONDS
operator|.
name|sleep
argument_list|(
literal|5
argument_list|)
expr_stmt|;
name|consumertoAbort
operator|.
name|getValue
argument_list|()
operator|.
name|assertAtMostMessagesReceived
argument_list|(
literal|1
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testAbortAlreadyClosingConsumers
parameter_list|()
throws|throws
name|Exception
block|{
name|consumerCount
operator|=
literal|1
expr_stmt|;
name|startConsumers
argument_list|(
name|withPrefetch
argument_list|(
literal|2
argument_list|,
name|destination
argument_list|)
argument_list|)
expr_stmt|;
for|for
control|(
name|MessageIdList
name|list
range|:
name|consumers
operator|.
name|values
argument_list|()
control|)
block|{
name|list
operator|.
name|setProcessingDelay
argument_list|(
literal|6
operator|*
literal|1000
argument_list|)
expr_stmt|;
block|}
for|for
control|(
name|Connection
name|c
range|:
name|connections
control|)
block|{
name|c
operator|.
name|setExceptionListener
argument_list|(
name|this
argument_list|)
expr_stmt|;
block|}
name|startProducers
argument_list|(
name|destination
argument_list|,
literal|100
argument_list|)
expr_stmt|;
name|allMessagesList
operator|.
name|waitForMessagesToArrive
argument_list|(
name|consumerCount
argument_list|)
expr_stmt|;
for|for
control|(
name|MessageConsumer
name|consumer
range|:
name|consumers
operator|.
name|keySet
argument_list|()
control|)
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"closing consumer: "
operator|+
name|consumer
argument_list|)
expr_stmt|;
comment|/// will block waiting for on message till 6secs expire
name|consumer
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
name|testAbortConsumerOnDeadConnection
parameter_list|()
throws|throws
name|Exception
block|{
name|TransportConnector
name|transportConnector
init|=
name|broker
operator|.
name|addConnector
argument_list|(
literal|"tcp://0.0.0.0:0"
argument_list|)
decl_stmt|;
name|transportConnector
operator|.
name|setBrokerService
argument_list|(
name|broker
argument_list|)
expr_stmt|;
name|transportConnector
operator|.
name|setTaskRunnerFactory
argument_list|(
name|broker
operator|.
name|getTaskRunnerFactory
argument_list|()
argument_list|)
expr_stmt|;
name|transportConnector
operator|.
name|start
argument_list|()
expr_stmt|;
name|SocketProxy
name|socketProxy
init|=
operator|new
name|SocketProxy
argument_list|(
name|transportConnector
operator|.
name|getPublishableConnectURI
argument_list|()
argument_list|)
decl_stmt|;
name|ActiveMQConnectionFactory
name|connectionFactory
init|=
operator|new
name|ActiveMQConnectionFactory
argument_list|(
name|socketProxy
operator|.
name|getUrl
argument_list|()
argument_list|)
decl_stmt|;
name|ActiveMQPrefetchPolicy
name|prefetchPolicy
init|=
operator|new
name|ActiveMQPrefetchPolicy
argument_list|()
decl_stmt|;
name|prefetchPolicy
operator|.
name|setAll
argument_list|(
literal|4
argument_list|)
expr_stmt|;
name|connectionFactory
operator|.
name|setPrefetchPolicy
argument_list|(
name|prefetchPolicy
argument_list|)
expr_stmt|;
name|Connection
name|c
init|=
name|connectionFactory
operator|.
name|createConnection
argument_list|()
decl_stmt|;
name|connections
operator|.
name|add
argument_list|(
name|c
argument_list|)
expr_stmt|;
name|c
operator|.
name|start
argument_list|()
expr_stmt|;
name|Session
name|session
init|=
name|c
operator|.
name|createSession
argument_list|(
literal|false
argument_list|,
name|Session
operator|.
name|CLIENT_ACKNOWLEDGE
argument_list|)
decl_stmt|;
specifier|final
name|ActiveMQMessageConsumer
name|messageconsumer
init|=
operator|(
name|ActiveMQMessageConsumer
operator|)
name|session
operator|.
name|createConsumer
argument_list|(
name|destination
argument_list|)
decl_stmt|;
name|startProducers
argument_list|(
name|destination
argument_list|,
literal|10
argument_list|)
expr_stmt|;
name|messageconsumer
operator|.
name|receive
argument_list|(
literal|4000
argument_list|)
operator|.
name|acknowledge
argument_list|()
expr_stmt|;
name|assertNotNull
argument_list|(
name|messageconsumer
operator|.
name|receive
argument_list|(
literal|4000
argument_list|)
argument_list|)
expr_stmt|;
name|assertNotNull
argument_list|(
name|messageconsumer
operator|.
name|receive
argument_list|(
literal|4000
argument_list|)
argument_list|)
expr_stmt|;
name|assertNotNull
argument_list|(
name|messageconsumer
operator|.
name|receive
argument_list|(
literal|4000
argument_list|)
argument_list|)
expr_stmt|;
comment|// close control command won't get through
name|socketProxy
operator|.
name|pause
argument_list|()
expr_stmt|;
name|ActiveMQDestination
name|amqDest
init|=
operator|(
name|ActiveMQDestination
operator|)
name|destination
decl_stmt|;
name|ObjectName
name|destinationViewMBean
init|=
operator|new
name|ObjectName
argument_list|(
literal|"org.apache.activemq:destinationType="
operator|+
operator|(
name|amqDest
operator|.
name|isTopic
argument_list|()
condition|?
literal|"Topic"
else|:
literal|"Queue"
operator|)
operator|+
literal|",destinationName="
operator|+
name|amqDest
operator|.
name|getPhysicalName
argument_list|()
operator|+
literal|",type=Broker,brokerName=localhost"
argument_list|)
decl_stmt|;
specifier|final
name|DestinationViewMBean
name|destView
init|=
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
name|destinationViewMBean
argument_list|,
name|DestinationViewMBean
operator|.
name|class
argument_list|,
literal|true
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
literal|"Consumer gone from broker view"
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
name|LOG
operator|.
name|info
argument_list|(
literal|"DestView {} comsumerCount {}"
argument_list|,
name|destView
argument_list|,
name|destView
operator|.
name|getConsumerCount
argument_list|()
argument_list|)
expr_stmt|;
return|return
literal|0
operator|==
name|destView
operator|.
name|getConsumerCount
argument_list|()
return|;
block|}
block|}
argument_list|)
argument_list|)
expr_stmt|;
name|socketProxy
operator|.
name|goOn
argument_list|()
expr_stmt|;
name|assertTrue
argument_list|(
literal|"consumer was closed"
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
name|boolean
name|closed
init|=
literal|false
decl_stmt|;
try|try
block|{
name|messageconsumer
operator|.
name|receive
argument_list|(
literal|400
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|javax
operator|.
name|jms
operator|.
name|IllegalStateException
name|expected
parameter_list|)
block|{
name|closed
operator|=
name|expected
operator|.
name|toString
argument_list|()
operator|.
name|contains
argument_list|(
literal|"closed"
argument_list|)
expr_stmt|;
block|}
return|return
name|closed
return|;
block|}
block|}
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|onException
parameter_list|(
name|JMSException
name|exception
parameter_list|)
block|{
name|exceptions
operator|.
name|add
argument_list|(
name|exception
argument_list|)
expr_stmt|;
name|exception
operator|.
name|printStackTrace
argument_list|()
expr_stmt|;
block|}
block|}
end_class

end_unit

