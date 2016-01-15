begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *      http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
package|package
name|org
operator|.
name|apache
operator|.
name|activemq
operator|.
name|bugs
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
name|Deque
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|LinkedList
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

begin_comment
comment|/**  *  */
end_comment

begin_class
specifier|public
class|class
name|AMQ6117Test
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
name|AMQ6117Test
operator|.
name|class
argument_list|)
decl_stmt|;
specifier|private
name|BrokerService
name|broker
decl_stmt|;
annotation|@
name|Test
specifier|public
name|void
name|testViewIsStale
parameter_list|()
throws|throws
name|Exception
block|{
specifier|final
name|int
name|MSG_COUNT
init|=
literal|10
decl_stmt|;
name|ActiveMQConnectionFactory
name|cf
init|=
operator|new
name|ActiveMQConnectionFactory
argument_list|(
name|broker
operator|.
name|getVmConnectorURI
argument_list|()
argument_list|)
decl_stmt|;
name|Connection
name|connection
init|=
name|cf
operator|.
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
name|Queue
name|queue
init|=
name|session
operator|.
name|createQueue
argument_list|(
literal|"Test-Queue"
argument_list|)
decl_stmt|;
name|Queue
name|dlq
init|=
name|session
operator|.
name|createQueue
argument_list|(
literal|"ActiveMQ.DLQ"
argument_list|)
decl_stmt|;
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
comment|// Ensure there is a DLQ in existence to start.
name|session
operator|.
name|createProducer
argument_list|(
name|dlq
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
name|MSG_COUNT
condition|;
operator|++
name|i
control|)
block|{
name|producer
operator|.
name|send
argument_list|(
name|session
operator|.
name|createMessage
argument_list|()
argument_list|,
name|DeliveryMode
operator|.
name|PERSISTENT
argument_list|,
name|Message
operator|.
name|DEFAULT_PRIORITY
argument_list|,
literal|1000
argument_list|)
expr_stmt|;
block|}
specifier|final
name|QueueViewMBean
name|queueView
init|=
name|getProxyToQueue
argument_list|(
name|dlq
operator|.
name|getQueueName
argument_list|()
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
literal|"Message should be DLQ'd"
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
return|return
name|queueView
operator|.
name|getQueueSize
argument_list|()
operator|==
name|MSG_COUNT
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
literal|"DLQ has captured all expired messages"
argument_list|)
expr_stmt|;
name|Deque
argument_list|<
name|String
argument_list|>
name|browsed
init|=
operator|new
name|LinkedList
argument_list|<
name|String
argument_list|>
argument_list|()
decl_stmt|;
name|CompositeData
index|[]
name|elements
init|=
name|queueView
operator|.
name|browse
argument_list|()
decl_stmt|;
name|assertEquals
argument_list|(
name|MSG_COUNT
argument_list|,
name|elements
operator|.
name|length
argument_list|)
expr_stmt|;
for|for
control|(
name|CompositeData
name|element
range|:
name|elements
control|)
block|{
name|String
name|messageID
init|=
operator|(
name|String
operator|)
name|element
operator|.
name|get
argument_list|(
literal|"JMSMessageID"
argument_list|)
decl_stmt|;
name|LOG
operator|.
name|debug
argument_list|(
literal|"MessageID: {}"
argument_list|,
name|messageID
argument_list|)
expr_stmt|;
name|browsed
operator|.
name|add
argument_list|(
name|messageID
argument_list|)
expr_stmt|;
block|}
name|String
name|removedMsgId
init|=
name|browsed
operator|.
name|removeFirst
argument_list|()
decl_stmt|;
name|assertTrue
argument_list|(
name|queueView
operator|.
name|removeMessage
argument_list|(
name|removedMsgId
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|MSG_COUNT
operator|-
literal|1
argument_list|,
name|queueView
operator|.
name|getQueueSize
argument_list|()
argument_list|)
expr_stmt|;
name|elements
operator|=
name|queueView
operator|.
name|browse
argument_list|()
expr_stmt|;
name|assertEquals
argument_list|(
name|MSG_COUNT
operator|-
literal|1
argument_list|,
name|elements
operator|.
name|length
argument_list|)
expr_stmt|;
for|for
control|(
name|CompositeData
name|element
range|:
name|elements
control|)
block|{
name|String
name|messageID
init|=
operator|(
name|String
operator|)
name|element
operator|.
name|get
argument_list|(
literal|"JMSMessageID"
argument_list|)
decl_stmt|;
name|LOG
operator|.
name|debug
argument_list|(
literal|"MessageID: {}"
argument_list|,
name|messageID
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|messageID
operator|.
name|equals
argument_list|(
name|removedMsgId
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Before
specifier|public
name|void
name|setup
parameter_list|()
throws|throws
name|Exception
block|{
name|PolicyMap
name|policyMap
init|=
operator|new
name|PolicyMap
argument_list|()
decl_stmt|;
name|List
argument_list|<
name|PolicyEntry
argument_list|>
name|entries
init|=
operator|new
name|ArrayList
argument_list|<
name|PolicyEntry
argument_list|>
argument_list|()
decl_stmt|;
name|PolicyEntry
name|pe
init|=
operator|new
name|PolicyEntry
argument_list|()
decl_stmt|;
name|pe
operator|.
name|setExpireMessagesPeriod
argument_list|(
literal|1500
argument_list|)
expr_stmt|;
name|pe
operator|.
name|setQueue
argument_list|(
literal|">"
argument_list|)
expr_stmt|;
name|entries
operator|.
name|add
argument_list|(
name|pe
argument_list|)
expr_stmt|;
name|policyMap
operator|.
name|setPolicyEntries
argument_list|(
name|entries
argument_list|)
expr_stmt|;
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
literal|true
argument_list|)
expr_stmt|;
name|broker
operator|.
name|setPersistent
argument_list|(
literal|true
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
annotation|@
name|After
specifier|public
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
block|}
specifier|protected
name|QueueViewMBean
name|getProxyToQueue
parameter_list|(
name|String
name|name
parameter_list|)
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
name|name
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
block|}
end_class

end_unit
