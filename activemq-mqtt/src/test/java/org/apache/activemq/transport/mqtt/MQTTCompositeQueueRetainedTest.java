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
name|transport
operator|.
name|mqtt
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
name|RetainedMessageSubscriptionRecoveryPolicy
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

begin_comment
comment|/**  *  */
end_comment

begin_class
specifier|public
class|class
name|MQTTCompositeQueueRetainedTest
extends|extends
name|MQTTTestSupport
block|{
comment|// configure composite topic
specifier|private
specifier|static
specifier|final
name|String
name|COMPOSITE_TOPIC
init|=
literal|"Composite.TopicA"
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|String
name|FORWARD_QUEUE
init|=
literal|"Composite.Queue.A"
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|String
name|FORWARD_TOPIC
init|=
literal|"Composite.Topic.A"
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|int
name|NUM_MESSAGES
init|=
literal|25
decl_stmt|;
annotation|@
name|Override
specifier|protected
name|BrokerService
name|createBroker
parameter_list|(
name|boolean
name|deleteAllOnStart
parameter_list|)
throws|throws
name|Exception
block|{
name|BrokerService
name|brokerService
init|=
operator|new
name|BrokerService
argument_list|()
decl_stmt|;
name|brokerService
operator|.
name|setPersistent
argument_list|(
name|isPersistent
argument_list|()
argument_list|)
expr_stmt|;
name|brokerService
operator|.
name|setAdvisorySupport
argument_list|(
literal|false
argument_list|)
expr_stmt|;
name|brokerService
operator|.
name|setSchedulerSupport
argument_list|(
name|isSchedulerSupportEnabled
argument_list|()
argument_list|)
expr_stmt|;
name|brokerService
operator|.
name|setPopulateJMSXUserID
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|brokerService
operator|.
name|setUseJmx
argument_list|(
literal|false
argument_list|)
expr_stmt|;
specifier|final
name|CompositeTopic
name|compositeTopic
init|=
operator|new
name|CompositeTopic
argument_list|()
decl_stmt|;
name|compositeTopic
operator|.
name|setName
argument_list|(
name|COMPOSITE_TOPIC
argument_list|)
expr_stmt|;
specifier|final
name|ArrayList
argument_list|<
name|ActiveMQDestination
argument_list|>
name|forwardDestinations
init|=
operator|new
name|ArrayList
argument_list|<
name|ActiveMQDestination
argument_list|>
argument_list|()
decl_stmt|;
name|forwardDestinations
operator|.
name|add
argument_list|(
operator|new
name|ActiveMQQueue
argument_list|(
name|FORWARD_QUEUE
argument_list|)
argument_list|)
expr_stmt|;
name|forwardDestinations
operator|.
name|add
argument_list|(
operator|new
name|ActiveMQTopic
argument_list|(
name|FORWARD_TOPIC
argument_list|)
argument_list|)
expr_stmt|;
name|compositeTopic
operator|.
name|setForwardTo
argument_list|(
name|forwardDestinations
argument_list|)
expr_stmt|;
comment|// NOTE: allows retained messages to be set on the Composite
name|compositeTopic
operator|.
name|setForwardOnly
argument_list|(
literal|false
argument_list|)
expr_stmt|;
specifier|final
name|VirtualDestinationInterceptor
name|destinationInterceptor
init|=
operator|new
name|VirtualDestinationInterceptor
argument_list|()
decl_stmt|;
name|destinationInterceptor
operator|.
name|setVirtualDestinations
argument_list|(
operator|new
name|VirtualDestination
index|[]
block|{
name|compositeTopic
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
name|destinationInterceptor
block|}
argument_list|)
expr_stmt|;
return|return
name|brokerService
return|;
block|}
annotation|@
name|Test
argument_list|(
name|timeout
operator|=
literal|60
operator|*
literal|1000
argument_list|)
specifier|public
name|void
name|testSendMQTTReceiveJMSCompositeDestinations
parameter_list|()
throws|throws
name|Exception
block|{
specifier|final
name|MQTTClientProvider
name|provider
init|=
name|getMQTTClientProvider
argument_list|()
decl_stmt|;
name|initializeConnection
argument_list|(
name|provider
argument_list|)
expr_stmt|;
comment|// send retained message
specifier|final
name|String
name|MQTT_TOPIC
init|=
literal|"Composite/TopicA"
decl_stmt|;
specifier|final
name|String
name|RETAINED
init|=
literal|"RETAINED"
decl_stmt|;
name|provider
operator|.
name|publish
argument_list|(
name|MQTT_TOPIC
argument_list|,
name|RETAINED
operator|.
name|getBytes
argument_list|()
argument_list|,
name|AT_LEAST_ONCE
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|ActiveMQConnection
name|activeMQConnection
init|=
operator|(
name|ActiveMQConnection
operator|)
operator|new
name|ActiveMQConnectionFactory
argument_list|(
name|jmsUri
argument_list|)
operator|.
name|createConnection
argument_list|()
decl_stmt|;
comment|// MUST set to true to receive retained messages
name|activeMQConnection
operator|.
name|setUseRetroactiveConsumer
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|activeMQConnection
operator|.
name|setClientID
argument_list|(
literal|"jms-client"
argument_list|)
expr_stmt|;
name|activeMQConnection
operator|.
name|start
argument_list|()
expr_stmt|;
name|Session
name|s
init|=
name|activeMQConnection
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
name|javax
operator|.
name|jms
operator|.
name|Queue
name|jmsQueue
init|=
name|s
operator|.
name|createQueue
argument_list|(
name|FORWARD_QUEUE
argument_list|)
decl_stmt|;
name|javax
operator|.
name|jms
operator|.
name|Topic
name|jmsTopic
init|=
name|s
operator|.
name|createTopic
argument_list|(
name|FORWARD_TOPIC
argument_list|)
decl_stmt|;
name|MessageConsumer
name|queueConsumer
init|=
name|s
operator|.
name|createConsumer
argument_list|(
name|jmsQueue
argument_list|)
decl_stmt|;
name|MessageConsumer
name|topicConsumer
init|=
name|s
operator|.
name|createDurableSubscriber
argument_list|(
name|jmsTopic
argument_list|,
literal|"jms-subscription"
argument_list|)
decl_stmt|;
comment|// check whether we received retained message twice on mapped Queue, once marked as RETAINED
name|ActiveMQMessage
name|message
decl_stmt|;
name|ByteSequence
name|bs
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
literal|2
condition|;
name|i
operator|++
control|)
block|{
name|message
operator|=
operator|(
name|ActiveMQMessage
operator|)
name|queueConsumer
operator|.
name|receive
argument_list|(
literal|5000
argument_list|)
expr_stmt|;
name|assertNotNull
argument_list|(
literal|"Should get retained message from "
operator|+
name|FORWARD_QUEUE
argument_list|,
name|message
argument_list|)
expr_stmt|;
name|bs
operator|=
name|message
operator|.
name|getContent
argument_list|()
expr_stmt|;
name|assertEquals
argument_list|(
name|RETAINED
argument_list|,
operator|new
name|String
argument_list|(
name|bs
operator|.
name|data
argument_list|,
name|bs
operator|.
name|offset
argument_list|,
name|bs
operator|.
name|length
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|message
operator|.
name|getBooleanProperty
argument_list|(
name|RetainedMessageSubscriptionRecoveryPolicy
operator|.
name|RETAIN_PROPERTY
argument_list|)
operator|!=
name|message
operator|.
name|getBooleanProperty
argument_list|(
name|RetainedMessageSubscriptionRecoveryPolicy
operator|.
name|RETAINED_PROPERTY
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|// check whether we received retained message on mapped Topic
name|message
operator|=
operator|(
name|ActiveMQMessage
operator|)
name|topicConsumer
operator|.
name|receive
argument_list|(
literal|5000
argument_list|)
expr_stmt|;
name|assertNotNull
argument_list|(
literal|"Should get retained message from "
operator|+
name|FORWARD_TOPIC
argument_list|,
name|message
argument_list|)
expr_stmt|;
name|bs
operator|=
name|message
operator|.
name|getContent
argument_list|()
expr_stmt|;
name|assertEquals
argument_list|(
name|RETAINED
argument_list|,
operator|new
name|String
argument_list|(
name|bs
operator|.
name|data
argument_list|,
name|bs
operator|.
name|offset
argument_list|,
name|bs
operator|.
name|length
argument_list|)
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|message
operator|.
name|getBooleanProperty
argument_list|(
name|RetainedMessageSubscriptionRecoveryPolicy
operator|.
name|RETAIN_PROPERTY
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|message
operator|.
name|getBooleanProperty
argument_list|(
name|RetainedMessageSubscriptionRecoveryPolicy
operator|.
name|RETAINED_PROPERTY
argument_list|)
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
name|NUM_MESSAGES
condition|;
name|i
operator|++
control|)
block|{
name|String
name|payload
init|=
literal|"Test Message: "
operator|+
name|i
decl_stmt|;
name|provider
operator|.
name|publish
argument_list|(
name|MQTT_TOPIC
argument_list|,
name|payload
operator|.
name|getBytes
argument_list|()
argument_list|,
name|AT_LEAST_ONCE
argument_list|)
expr_stmt|;
name|message
operator|=
operator|(
name|ActiveMQMessage
operator|)
name|queueConsumer
operator|.
name|receive
argument_list|(
literal|5000
argument_list|)
expr_stmt|;
name|assertNotNull
argument_list|(
literal|"Should get a message from "
operator|+
name|FORWARD_QUEUE
argument_list|,
name|message
argument_list|)
expr_stmt|;
name|bs
operator|=
name|message
operator|.
name|getContent
argument_list|()
expr_stmt|;
name|assertEquals
argument_list|(
name|payload
argument_list|,
operator|new
name|String
argument_list|(
name|bs
operator|.
name|data
argument_list|,
name|bs
operator|.
name|offset
argument_list|,
name|bs
operator|.
name|length
argument_list|)
argument_list|)
expr_stmt|;
name|message
operator|=
operator|(
name|ActiveMQMessage
operator|)
name|topicConsumer
operator|.
name|receive
argument_list|(
literal|5000
argument_list|)
expr_stmt|;
name|assertNotNull
argument_list|(
literal|"Should get a message from "
operator|+
name|FORWARD_TOPIC
argument_list|,
name|message
argument_list|)
expr_stmt|;
name|bs
operator|=
name|message
operator|.
name|getContent
argument_list|()
expr_stmt|;
name|assertEquals
argument_list|(
name|payload
argument_list|,
operator|new
name|String
argument_list|(
name|bs
operator|.
name|data
argument_list|,
name|bs
operator|.
name|offset
argument_list|,
name|bs
operator|.
name|length
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|// close consumer and look for retained messages again
name|queueConsumer
operator|.
name|close
argument_list|()
expr_stmt|;
name|topicConsumer
operator|.
name|close
argument_list|()
expr_stmt|;
name|queueConsumer
operator|=
name|s
operator|.
name|createConsumer
argument_list|(
name|jmsQueue
argument_list|)
expr_stmt|;
name|topicConsumer
operator|=
name|s
operator|.
name|createDurableSubscriber
argument_list|(
name|jmsTopic
argument_list|,
literal|"jms-subscription"
argument_list|)
expr_stmt|;
comment|// check whether we received retained message on mapped Queue, again
name|message
operator|=
operator|(
name|ActiveMQMessage
operator|)
name|queueConsumer
operator|.
name|receive
argument_list|(
literal|5000
argument_list|)
expr_stmt|;
name|assertNotNull
argument_list|(
literal|"Should get recovered retained message from "
operator|+
name|FORWARD_QUEUE
argument_list|,
name|message
argument_list|)
expr_stmt|;
name|bs
operator|=
name|message
operator|.
name|getContent
argument_list|()
expr_stmt|;
name|assertEquals
argument_list|(
name|RETAINED
argument_list|,
operator|new
name|String
argument_list|(
name|bs
operator|.
name|data
argument_list|,
name|bs
operator|.
name|offset
argument_list|,
name|bs
operator|.
name|length
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|message
operator|.
name|getBooleanProperty
argument_list|(
name|RetainedMessageSubscriptionRecoveryPolicy
operator|.
name|RETAINED_PROPERTY
argument_list|)
argument_list|)
expr_stmt|;
name|assertNull
argument_list|(
literal|"Should not get second retained message from "
operator|+
name|FORWARD_QUEUE
argument_list|,
name|queueConsumer
operator|.
name|receive
argument_list|(
literal|2000
argument_list|)
argument_list|)
expr_stmt|;
comment|// check whether we received retained message on mapped Topic, again
name|message
operator|=
operator|(
name|ActiveMQMessage
operator|)
name|topicConsumer
operator|.
name|receive
argument_list|(
literal|5000
argument_list|)
expr_stmt|;
name|assertNotNull
argument_list|(
literal|"Should get recovered retained message from "
operator|+
name|FORWARD_TOPIC
argument_list|,
name|message
argument_list|)
expr_stmt|;
name|bs
operator|=
name|message
operator|.
name|getContent
argument_list|()
expr_stmt|;
name|assertEquals
argument_list|(
name|RETAINED
argument_list|,
operator|new
name|String
argument_list|(
name|bs
operator|.
name|data
argument_list|,
name|bs
operator|.
name|offset
argument_list|,
name|bs
operator|.
name|length
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|message
operator|.
name|getBooleanProperty
argument_list|(
name|RetainedMessageSubscriptionRecoveryPolicy
operator|.
name|RETAINED_PROPERTY
argument_list|)
argument_list|)
expr_stmt|;
name|assertNull
argument_list|(
literal|"Should not get second retained message from "
operator|+
name|FORWARD_TOPIC
argument_list|,
name|topicConsumer
operator|.
name|receive
argument_list|(
literal|2000
argument_list|)
argument_list|)
expr_stmt|;
comment|// create second queue consumer and verify that it doesn't trigger message recovery
specifier|final
name|MessageConsumer
name|queueConsumer2
init|=
name|s
operator|.
name|createConsumer
argument_list|(
name|jmsQueue
argument_list|)
decl_stmt|;
name|assertNull
argument_list|(
literal|"Second consumer MUST not receive retained message from "
operator|+
name|FORWARD_QUEUE
argument_list|,
name|queueConsumer2
operator|.
name|receive
argument_list|(
literal|2000
argument_list|)
argument_list|)
expr_stmt|;
name|activeMQConnection
operator|.
name|close
argument_list|()
expr_stmt|;
name|provider
operator|.
name|disconnect
argument_list|()
expr_stmt|;
block|}
block|}
end_class

end_unit

