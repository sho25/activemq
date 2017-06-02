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
name|virtual
package|;
end_package

begin_import
import|import
name|junit
operator|.
name|framework
operator|.
name|Assert
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
name|TopicViewMBean
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
name|DestinationInterceptor
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
name|virtual
operator|.
name|CompositeTopic
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
name|virtual
operator|.
name|VirtualDestination
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
name|virtual
operator|.
name|VirtualDestinationInterceptor
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
name|ActiveMQBytesMessage
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
name|ByteSequence
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
name|Collection
import|;
end_import

begin_comment
comment|/**  * Test to ensure the CompositeTopic Memory Usage returns to zero after messages forwarded to underlying queues  */
end_comment

begin_class
specifier|public
class|class
name|CompositeTopicMemoryUsageTest
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
name|CompositeTopicMemoryUsageTest
operator|.
name|class
argument_list|)
decl_stmt|;
specifier|public
name|int
name|messageSize
init|=
literal|5
operator|*
literal|1024
decl_stmt|;
specifier|public
name|int
name|messageCount
init|=
literal|1000
decl_stmt|;
name|ActiveMQTopic
name|target
init|=
operator|new
name|ActiveMQTopic
argument_list|(
literal|"target"
argument_list|)
decl_stmt|;
name|BrokerService
name|brokerService
decl_stmt|;
name|ActiveMQConnectionFactory
name|connectionFactory
decl_stmt|;
annotation|@
name|Test
specifier|public
name|void
name|testMemoryUsage
parameter_list|()
throws|throws
name|Exception
block|{
name|startBroker
argument_list|(
literal|4
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|messageSize
operator|=
literal|20
operator|*
literal|1024
expr_stmt|;
name|produceMessages
argument_list|(
literal|20
argument_list|,
name|target
argument_list|)
expr_stmt|;
name|long
name|memoryUsage
init|=
name|getMemoryUsageForTopic
argument_list|(
name|target
operator|.
name|getPhysicalName
argument_list|()
argument_list|)
decl_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|"MemoryUsage should be zero"
argument_list|,
literal|0l
argument_list|,
name|memoryUsage
argument_list|)
expr_stmt|;
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
block|}
specifier|private
name|long
name|getMemoryUsageForTopic
parameter_list|(
name|String
name|topicName
parameter_list|)
throws|throws
name|Exception
block|{
name|ObjectName
index|[]
name|topics
init|=
name|brokerService
operator|.
name|getAdminView
argument_list|()
operator|.
name|getTopics
argument_list|()
decl_stmt|;
for|for
control|(
name|ObjectName
name|objectName
range|:
name|topics
control|)
block|{
if|if
condition|(
name|objectName
operator|.
name|getCanonicalName
argument_list|()
operator|.
name|contains
argument_list|(
name|topicName
argument_list|)
condition|)
block|{
name|TopicViewMBean
name|topicViewMBean
init|=
operator|(
name|TopicViewMBean
operator|)
name|brokerService
operator|.
name|getManagementContext
argument_list|()
operator|.
name|newProxyInstance
argument_list|(
name|objectName
argument_list|,
name|TopicViewMBean
operator|.
name|class
argument_list|,
literal|false
argument_list|)
decl_stmt|;
return|return
name|topicViewMBean
operator|.
name|getMemoryUsageByteCount
argument_list|()
return|;
block|}
block|}
throw|throw
operator|new
name|Exception
argument_list|(
literal|"NO TOPIC FOUND"
argument_list|)
throw|;
block|}
specifier|protected
name|void
name|produceMessages
parameter_list|(
name|int
name|messageCount
parameter_list|,
name|ActiveMQDestination
name|destination
parameter_list|)
throws|throws
name|Exception
block|{
specifier|final
name|ByteSequence
name|payLoad
init|=
operator|new
name|ByteSequence
argument_list|(
operator|new
name|byte
index|[
name|messageSize
index|]
argument_list|)
decl_stmt|;
name|Connection
name|connection
init|=
name|connectionFactory
operator|.
name|createConnection
argument_list|()
decl_stmt|;
name|MessageProducer
name|messageProducer
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
name|createProducer
argument_list|(
name|destination
argument_list|)
decl_stmt|;
name|messageProducer
operator|.
name|setDeliveryMode
argument_list|(
name|DeliveryMode
operator|.
name|PERSISTENT
argument_list|)
expr_stmt|;
name|ActiveMQBytesMessage
name|message
init|=
operator|new
name|ActiveMQBytesMessage
argument_list|()
decl_stmt|;
name|message
operator|.
name|setContent
argument_list|(
name|payLoad
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
name|messageCount
condition|;
name|i
operator|++
control|)
block|{
name|messageProducer
operator|.
name|send
argument_list|(
name|message
argument_list|)
expr_stmt|;
block|}
name|connection
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
specifier|private
name|void
name|startBroker
parameter_list|(
name|int
name|fanoutCount
parameter_list|,
name|boolean
name|concurrentSend
parameter_list|)
throws|throws
name|Exception
block|{
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
name|setUseVirtualTopics
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|brokerService
operator|.
name|addConnector
argument_list|(
literal|"tcp://0.0.0.0:0"
argument_list|)
expr_stmt|;
name|brokerService
operator|.
name|setAdvisorySupport
argument_list|(
literal|false
argument_list|)
expr_stmt|;
name|PolicyMap
name|destPolicyMap
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
literal|0
argument_list|)
expr_stmt|;
name|defaultEntry
operator|.
name|setOptimizedDispatch
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|defaultEntry
operator|.
name|setCursorMemoryHighWaterMark
argument_list|(
literal|110
argument_list|)
expr_stmt|;
name|destPolicyMap
operator|.
name|setDefaultEntry
argument_list|(
name|defaultEntry
argument_list|)
expr_stmt|;
name|brokerService
operator|.
name|setDestinationPolicy
argument_list|(
name|destPolicyMap
argument_list|)
expr_stmt|;
name|CompositeTopic
name|route
init|=
operator|new
name|CompositeTopic
argument_list|()
decl_stmt|;
name|route
operator|.
name|setName
argument_list|(
literal|"target"
argument_list|)
expr_stmt|;
name|route
operator|.
name|setForwardOnly
argument_list|(
literal|false
argument_list|)
expr_stmt|;
name|route
operator|.
name|setConcurrentSend
argument_list|(
name|concurrentSend
argument_list|)
expr_stmt|;
name|Collection
argument_list|<
name|ActiveMQQueue
argument_list|>
name|routes
init|=
operator|new
name|ArrayList
argument_list|<
name|ActiveMQQueue
argument_list|>
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
name|fanoutCount
condition|;
name|i
operator|++
control|)
block|{
name|routes
operator|.
name|add
argument_list|(
operator|new
name|ActiveMQQueue
argument_list|(
literal|"route."
operator|+
name|i
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|route
operator|.
name|setForwardTo
argument_list|(
name|routes
argument_list|)
expr_stmt|;
name|VirtualDestinationInterceptor
name|interceptor
init|=
operator|new
name|VirtualDestinationInterceptor
argument_list|()
decl_stmt|;
name|interceptor
operator|.
name|setVirtualDestinations
argument_list|(
operator|new
name|VirtualDestination
index|[]
block|{
name|route
block|}
argument_list|)
expr_stmt|;
name|brokerService
operator|.
name|setDestinationInterceptors
argument_list|(
operator|new
name|DestinationInterceptor
index|[]
block|{
name|interceptor
block|}
argument_list|)
expr_stmt|;
name|brokerService
operator|.
name|start
argument_list|()
expr_stmt|;
name|connectionFactory
operator|=
operator|new
name|ActiveMQConnectionFactory
argument_list|(
name|brokerService
operator|.
name|getTransportConnectors
argument_list|()
operator|.
name|get
argument_list|(
literal|0
argument_list|)
operator|.
name|getPublishableConnectString
argument_list|()
argument_list|)
expr_stmt|;
name|connectionFactory
operator|.
name|setWatchTopicAdvisories
argument_list|(
literal|false
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

