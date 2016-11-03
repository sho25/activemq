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
name|network
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
name|assertTrue
import|;
end_import

begin_import
import|import
name|java
operator|.
name|net
operator|.
name|URI
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
name|ConcurrentMap
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
name|javax
operator|.
name|jms
operator|.
name|TopicRequestor
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|jms
operator|.
name|TopicSession
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
name|command
operator|.
name|ConsumerId
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
name|activemq
operator|.
name|util
operator|.
name|Wait
operator|.
name|Condition
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
name|xbean
operator|.
name|BrokerFactoryBean
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
name|Ignore
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
name|org
operator|.
name|springframework
operator|.
name|context
operator|.
name|support
operator|.
name|AbstractApplicationContext
import|;
end_import

begin_import
import|import
name|org
operator|.
name|springframework
operator|.
name|core
operator|.
name|io
operator|.
name|ClassPathResource
import|;
end_import

begin_import
import|import
name|org
operator|.
name|springframework
operator|.
name|core
operator|.
name|io
operator|.
name|Resource
import|;
end_import

begin_class
specifier|public
class|class
name|SimpleNetworkTest
block|{
specifier|protected
specifier|static
specifier|final
name|int
name|MESSAGE_COUNT
init|=
literal|10
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
name|SimpleNetworkTest
operator|.
name|class
argument_list|)
decl_stmt|;
specifier|protected
name|AbstractApplicationContext
name|context
decl_stmt|;
specifier|protected
name|Connection
name|localConnection
decl_stmt|;
specifier|protected
name|Connection
name|remoteConnection
decl_stmt|;
specifier|protected
name|BrokerService
name|localBroker
decl_stmt|;
specifier|protected
name|BrokerService
name|remoteBroker
decl_stmt|;
specifier|protected
name|Session
name|localSession
decl_stmt|;
specifier|protected
name|Session
name|remoteSession
decl_stmt|;
specifier|protected
name|ActiveMQTopic
name|included
decl_stmt|;
specifier|protected
name|ActiveMQTopic
name|excluded
decl_stmt|;
specifier|protected
name|String
name|consumerName
init|=
literal|"durableSubs"
decl_stmt|;
comment|// works b/c of non marshaling vm transport, the connection
comment|// ref from the client is used during the forward
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
name|testMessageCompression
parameter_list|()
throws|throws
name|Exception
block|{
name|ActiveMQConnection
name|localAmqConnection
init|=
operator|(
name|ActiveMQConnection
operator|)
name|localConnection
decl_stmt|;
name|localAmqConnection
operator|.
name|setUseCompression
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|MessageConsumer
name|consumer1
init|=
name|remoteSession
operator|.
name|createConsumer
argument_list|(
name|included
argument_list|)
decl_stmt|;
name|MessageProducer
name|producer
init|=
name|localSession
operator|.
name|createProducer
argument_list|(
name|included
argument_list|)
decl_stmt|;
name|producer
operator|.
name|setDeliveryMode
argument_list|(
name|DeliveryMode
operator|.
name|NON_PERSISTENT
argument_list|)
expr_stmt|;
name|waitForConsumerRegistration
argument_list|(
name|localBroker
argument_list|,
literal|1
argument_list|,
name|included
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
name|MESSAGE_COUNT
condition|;
name|i
operator|++
control|)
block|{
name|Message
name|test
init|=
name|localSession
operator|.
name|createTextMessage
argument_list|(
literal|"test-"
operator|+
name|i
argument_list|)
decl_stmt|;
name|producer
operator|.
name|send
argument_list|(
name|test
argument_list|)
expr_stmt|;
name|Message
name|msg
init|=
name|consumer1
operator|.
name|receive
argument_list|(
literal|3000
argument_list|)
decl_stmt|;
name|assertNotNull
argument_list|(
literal|"not null? message: "
operator|+
name|i
argument_list|,
name|msg
argument_list|)
expr_stmt|;
name|ActiveMQMessage
name|amqMessage
init|=
operator|(
name|ActiveMQMessage
operator|)
name|msg
decl_stmt|;
name|assertTrue
argument_list|(
name|amqMessage
operator|.
name|isCompressed
argument_list|()
argument_list|)
expr_stmt|;
block|}
comment|// ensure no more messages received
name|assertNull
argument_list|(
name|consumer1
operator|.
name|receive
argument_list|(
literal|1000
argument_list|)
argument_list|)
expr_stmt|;
name|assertNetworkBridgeStatistics
argument_list|(
name|MESSAGE_COUNT
argument_list|,
literal|0
argument_list|)
expr_stmt|;
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
name|testRequestReply
parameter_list|()
throws|throws
name|Exception
block|{
specifier|final
name|MessageProducer
name|remoteProducer
init|=
name|remoteSession
operator|.
name|createProducer
argument_list|(
literal|null
argument_list|)
decl_stmt|;
name|MessageConsumer
name|remoteConsumer
init|=
name|remoteSession
operator|.
name|createConsumer
argument_list|(
name|included
argument_list|)
decl_stmt|;
name|remoteConsumer
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
name|msg
parameter_list|)
block|{
try|try
block|{
name|TextMessage
name|textMsg
init|=
operator|(
name|TextMessage
operator|)
name|msg
decl_stmt|;
name|String
name|payload
init|=
literal|"REPLY: "
operator|+
name|textMsg
operator|.
name|getText
argument_list|()
decl_stmt|;
name|Destination
name|replyTo
decl_stmt|;
name|replyTo
operator|=
name|msg
operator|.
name|getJMSReplyTo
argument_list|()
expr_stmt|;
name|textMsg
operator|.
name|clearBody
argument_list|()
expr_stmt|;
name|textMsg
operator|.
name|setText
argument_list|(
name|payload
argument_list|)
expr_stmt|;
name|remoteProducer
operator|.
name|send
argument_list|(
name|replyTo
argument_list|,
name|textMsg
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
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
block|}
argument_list|)
expr_stmt|;
name|TopicRequestor
name|requestor
init|=
operator|new
name|TopicRequestor
argument_list|(
operator|(
name|TopicSession
operator|)
name|localSession
argument_list|,
name|included
argument_list|)
decl_stmt|;
comment|// allow for consumer infos to perculate arround
name|Thread
operator|.
name|sleep
argument_list|(
literal|5000
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
name|MESSAGE_COUNT
condition|;
name|i
operator|++
control|)
block|{
name|TextMessage
name|msg
init|=
name|localSession
operator|.
name|createTextMessage
argument_list|(
literal|"test msg: "
operator|+
name|i
argument_list|)
decl_stmt|;
name|TextMessage
name|result
init|=
operator|(
name|TextMessage
operator|)
name|requestor
operator|.
name|request
argument_list|(
name|msg
argument_list|)
decl_stmt|;
name|assertNotNull
argument_list|(
name|result
argument_list|)
expr_stmt|;
name|LOG
operator|.
name|info
argument_list|(
name|result
operator|.
name|getText
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|assertNetworkBridgeStatistics
argument_list|(
name|MESSAGE_COUNT
argument_list|,
name|MESSAGE_COUNT
argument_list|)
expr_stmt|;
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
name|testFiltering
parameter_list|()
throws|throws
name|Exception
block|{
name|MessageConsumer
name|includedConsumer
init|=
name|remoteSession
operator|.
name|createConsumer
argument_list|(
name|included
argument_list|)
decl_stmt|;
name|MessageConsumer
name|excludedConsumer
init|=
name|remoteSession
operator|.
name|createConsumer
argument_list|(
name|excluded
argument_list|)
decl_stmt|;
name|MessageProducer
name|includedProducer
init|=
name|localSession
operator|.
name|createProducer
argument_list|(
name|included
argument_list|)
decl_stmt|;
name|MessageProducer
name|excludedProducer
init|=
name|localSession
operator|.
name|createProducer
argument_list|(
name|excluded
argument_list|)
decl_stmt|;
comment|// allow for consumer infos to perculate around
name|Thread
operator|.
name|sleep
argument_list|(
literal|2000
argument_list|)
expr_stmt|;
name|Message
name|test
init|=
name|localSession
operator|.
name|createTextMessage
argument_list|(
literal|"test"
argument_list|)
decl_stmt|;
name|includedProducer
operator|.
name|send
argument_list|(
name|test
argument_list|)
expr_stmt|;
name|excludedProducer
operator|.
name|send
argument_list|(
name|test
argument_list|)
expr_stmt|;
name|assertNull
argument_list|(
name|excludedConsumer
operator|.
name|receive
argument_list|(
literal|1000
argument_list|)
argument_list|)
expr_stmt|;
name|assertNotNull
argument_list|(
name|includedConsumer
operator|.
name|receive
argument_list|(
literal|1000
argument_list|)
argument_list|)
expr_stmt|;
name|assertNetworkBridgeStatistics
argument_list|(
literal|1
argument_list|,
literal|0
argument_list|)
expr_stmt|;
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
name|testConduitBridge
parameter_list|()
throws|throws
name|Exception
block|{
name|MessageConsumer
name|consumer1
init|=
name|remoteSession
operator|.
name|createConsumer
argument_list|(
name|included
argument_list|)
decl_stmt|;
name|MessageConsumer
name|consumer2
init|=
name|remoteSession
operator|.
name|createConsumer
argument_list|(
name|included
argument_list|)
decl_stmt|;
name|MessageProducer
name|producer
init|=
name|localSession
operator|.
name|createProducer
argument_list|(
name|included
argument_list|)
decl_stmt|;
name|producer
operator|.
name|setDeliveryMode
argument_list|(
name|DeliveryMode
operator|.
name|NON_PERSISTENT
argument_list|)
expr_stmt|;
name|waitForConsumerRegistration
argument_list|(
name|localBroker
argument_list|,
literal|2
argument_list|,
name|included
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
name|MESSAGE_COUNT
condition|;
name|i
operator|++
control|)
block|{
name|Message
name|test
init|=
name|localSession
operator|.
name|createTextMessage
argument_list|(
literal|"test-"
operator|+
name|i
argument_list|)
decl_stmt|;
name|producer
operator|.
name|send
argument_list|(
name|test
argument_list|)
expr_stmt|;
name|assertNotNull
argument_list|(
name|consumer1
operator|.
name|receive
argument_list|(
literal|1000
argument_list|)
argument_list|)
expr_stmt|;
name|assertNotNull
argument_list|(
name|consumer2
operator|.
name|receive
argument_list|(
literal|1000
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|// ensure no more messages received
name|assertNull
argument_list|(
name|consumer1
operator|.
name|receive
argument_list|(
literal|1000
argument_list|)
argument_list|)
expr_stmt|;
name|assertNull
argument_list|(
name|consumer2
operator|.
name|receive
argument_list|(
literal|1000
argument_list|)
argument_list|)
expr_stmt|;
name|assertNetworkBridgeStatistics
argument_list|(
name|MESSAGE_COUNT
argument_list|,
literal|0
argument_list|)
expr_stmt|;
block|}
specifier|private
name|void
name|waitForConsumerRegistration
parameter_list|(
specifier|final
name|BrokerService
name|brokerService
parameter_list|,
specifier|final
name|int
name|min
parameter_list|,
specifier|final
name|ActiveMQDestination
name|destination
parameter_list|)
throws|throws
name|Exception
block|{
name|assertTrue
argument_list|(
literal|"Internal bridge consumers registered in time"
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
name|Object
index|[]
name|bridges
init|=
name|brokerService
operator|.
name|getNetworkConnectors
argument_list|()
operator|.
name|get
argument_list|(
literal|0
argument_list|)
operator|.
name|bridges
operator|.
name|values
argument_list|()
operator|.
name|toArray
argument_list|()
decl_stmt|;
if|if
condition|(
name|bridges
operator|.
name|length
operator|>
literal|0
condition|)
block|{
name|LOG
operator|.
name|info
argument_list|(
name|brokerService
operator|+
literal|" bridges "
operator|+
name|Arrays
operator|.
name|toString
argument_list|(
name|bridges
argument_list|)
argument_list|)
expr_stmt|;
name|DemandForwardingBridgeSupport
name|demandForwardingBridgeSupport
init|=
operator|(
name|DemandForwardingBridgeSupport
operator|)
name|bridges
index|[
literal|0
index|]
decl_stmt|;
name|ConcurrentMap
argument_list|<
name|ConsumerId
argument_list|,
name|DemandSubscription
argument_list|>
name|forwardingBridges
init|=
name|demandForwardingBridgeSupport
operator|.
name|getLocalSubscriptionMap
argument_list|()
decl_stmt|;
name|LOG
operator|.
name|info
argument_list|(
name|brokerService
operator|+
literal|" bridge "
operator|+
name|demandForwardingBridgeSupport
operator|+
literal|", localSubs: "
operator|+
name|forwardingBridges
argument_list|)
expr_stmt|;
if|if
condition|(
operator|!
name|forwardingBridges
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
for|for
control|(
name|DemandSubscription
name|demandSubscription
range|:
name|forwardingBridges
operator|.
name|values
argument_list|()
control|)
block|{
if|if
condition|(
name|demandSubscription
operator|.
name|getLocalInfo
argument_list|()
operator|.
name|getDestination
argument_list|()
operator|.
name|equals
argument_list|(
name|destination
argument_list|)
condition|)
block|{
name|LOG
operator|.
name|info
argument_list|(
name|brokerService
operator|+
literal|" DemandSubscription "
operator|+
name|demandSubscription
operator|+
literal|", size: "
operator|+
name|demandSubscription
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
return|return
name|demandSubscription
operator|.
name|size
argument_list|()
operator|>=
name|min
return|;
block|}
block|}
block|}
block|}
return|return
literal|false
return|;
block|}
block|}
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|//Added for AMQ-6465 to make sure memory usage decreased back to 0 after messages are forwarded
comment|//to the other broker
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
name|testDurableTopicSubForwardMemoryUsage
parameter_list|()
throws|throws
name|Exception
block|{
comment|// create a remote durable consumer to create demand
name|MessageConsumer
name|remoteConsumer
init|=
name|remoteSession
operator|.
name|createDurableSubscriber
argument_list|(
name|included
argument_list|,
name|consumerName
argument_list|)
decl_stmt|;
name|Thread
operator|.
name|sleep
argument_list|(
literal|1000
argument_list|)
expr_stmt|;
name|MessageProducer
name|producer
init|=
name|localSession
operator|.
name|createProducer
argument_list|(
name|included
argument_list|)
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
name|MESSAGE_COUNT
condition|;
name|i
operator|++
control|)
block|{
name|Message
name|test
init|=
name|localSession
operator|.
name|createTextMessage
argument_list|(
literal|"test-"
operator|+
name|i
argument_list|)
decl_stmt|;
name|producer
operator|.
name|send
argument_list|(
name|test
argument_list|)
expr_stmt|;
block|}
name|Thread
operator|.
name|sleep
argument_list|(
literal|1000
argument_list|)
expr_stmt|;
comment|//Make sure stats are set
name|assertEquals
argument_list|(
name|MESSAGE_COUNT
argument_list|,
name|localBroker
operator|.
name|getDestination
argument_list|(
name|included
argument_list|)
operator|.
name|getDestinationStatistics
argument_list|()
operator|.
name|getForwards
argument_list|()
operator|.
name|getCount
argument_list|()
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|Wait
operator|.
name|waitFor
argument_list|(
operator|new
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
name|localBroker
operator|.
name|getSystemUsage
argument_list|()
operator|.
name|getMemoryUsage
argument_list|()
operator|.
name|getUsage
argument_list|()
operator|==
literal|0
return|;
block|}
block|}
argument_list|,
literal|10000
argument_list|,
literal|500
argument_list|)
argument_list|)
expr_stmt|;
name|remoteConsumer
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
comment|//Added for AMQ-6465 to make sure memory usage decreased back to 0 after messages are forwarded
comment|//to the other broker
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
name|testTopicSubForwardMemoryUsage
parameter_list|()
throws|throws
name|Exception
block|{
comment|// create a remote durable consumer to create demand
name|MessageConsumer
name|remoteConsumer
init|=
name|remoteSession
operator|.
name|createConsumer
argument_list|(
name|included
argument_list|)
decl_stmt|;
name|Thread
operator|.
name|sleep
argument_list|(
literal|1000
argument_list|)
expr_stmt|;
name|MessageProducer
name|producer
init|=
name|localSession
operator|.
name|createProducer
argument_list|(
name|included
argument_list|)
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
name|MESSAGE_COUNT
condition|;
name|i
operator|++
control|)
block|{
name|Message
name|test
init|=
name|localSession
operator|.
name|createTextMessage
argument_list|(
literal|"test-"
operator|+
name|i
argument_list|)
decl_stmt|;
name|producer
operator|.
name|send
argument_list|(
name|test
argument_list|)
expr_stmt|;
block|}
name|Thread
operator|.
name|sleep
argument_list|(
literal|1000
argument_list|)
expr_stmt|;
comment|//Make sure stats are set
name|assertEquals
argument_list|(
name|MESSAGE_COUNT
argument_list|,
name|localBroker
operator|.
name|getDestination
argument_list|(
name|included
argument_list|)
operator|.
name|getDestinationStatistics
argument_list|()
operator|.
name|getForwards
argument_list|()
operator|.
name|getCount
argument_list|()
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|Wait
operator|.
name|waitFor
argument_list|(
operator|new
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
name|localBroker
operator|.
name|getSystemUsage
argument_list|()
operator|.
name|getMemoryUsage
argument_list|()
operator|.
name|getUsage
argument_list|()
operator|==
literal|0
return|;
block|}
block|}
argument_list|,
literal|10000
argument_list|,
literal|500
argument_list|)
argument_list|)
expr_stmt|;
name|remoteConsumer
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
comment|//Added for AMQ-6465 to make sure memory usage decreased back to 0 after messages are forwarded
comment|//to the other broker
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
name|testQueueSubForwardMemoryUsage
parameter_list|()
throws|throws
name|Exception
block|{
name|ActiveMQQueue
name|queue
init|=
operator|new
name|ActiveMQQueue
argument_list|(
literal|"include.test.foo"
argument_list|)
decl_stmt|;
name|MessageConsumer
name|remoteConsumer
init|=
name|remoteSession
operator|.
name|createConsumer
argument_list|(
name|queue
argument_list|)
decl_stmt|;
name|Thread
operator|.
name|sleep
argument_list|(
literal|1000
argument_list|)
expr_stmt|;
name|MessageProducer
name|producer
init|=
name|localSession
operator|.
name|createProducer
argument_list|(
name|queue
argument_list|)
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
name|MESSAGE_COUNT
condition|;
name|i
operator|++
control|)
block|{
name|Message
name|test
init|=
name|localSession
operator|.
name|createTextMessage
argument_list|(
literal|"test-"
operator|+
name|i
argument_list|)
decl_stmt|;
name|producer
operator|.
name|send
argument_list|(
name|test
argument_list|)
expr_stmt|;
block|}
name|Thread
operator|.
name|sleep
argument_list|(
literal|1000
argument_list|)
expr_stmt|;
comment|//Make sure stats are set
name|assertEquals
argument_list|(
name|MESSAGE_COUNT
argument_list|,
name|localBroker
operator|.
name|getDestination
argument_list|(
name|queue
argument_list|)
operator|.
name|getDestinationStatistics
argument_list|()
operator|.
name|getForwards
argument_list|()
operator|.
name|getCount
argument_list|()
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|Wait
operator|.
name|waitFor
argument_list|(
operator|new
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
name|localBroker
operator|.
name|getSystemUsage
argument_list|()
operator|.
name|getMemoryUsage
argument_list|()
operator|.
name|getUsage
argument_list|()
operator|==
literal|0
return|;
block|}
block|}
argument_list|,
literal|10000
argument_list|,
literal|500
argument_list|)
argument_list|)
expr_stmt|;
name|remoteConsumer
operator|.
name|close
argument_list|()
expr_stmt|;
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
name|testDurableStoreAndForward
parameter_list|()
throws|throws
name|Exception
block|{
comment|// create a remote durable consumer
name|MessageConsumer
name|remoteConsumer
init|=
name|remoteSession
operator|.
name|createDurableSubscriber
argument_list|(
name|included
argument_list|,
name|consumerName
argument_list|)
decl_stmt|;
name|Thread
operator|.
name|sleep
argument_list|(
literal|1000
argument_list|)
expr_stmt|;
comment|// now close everything down and restart
name|doTearDown
argument_list|()
expr_stmt|;
name|doSetUp
argument_list|(
literal|false
argument_list|)
expr_stmt|;
name|MessageProducer
name|producer
init|=
name|localSession
operator|.
name|createProducer
argument_list|(
name|included
argument_list|)
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
name|MESSAGE_COUNT
condition|;
name|i
operator|++
control|)
block|{
name|Message
name|test
init|=
name|localSession
operator|.
name|createTextMessage
argument_list|(
literal|"test-"
operator|+
name|i
argument_list|)
decl_stmt|;
name|producer
operator|.
name|send
argument_list|(
name|test
argument_list|)
expr_stmt|;
block|}
name|Thread
operator|.
name|sleep
argument_list|(
literal|1000
argument_list|)
expr_stmt|;
comment|//Make sure stats are set
name|assertEquals
argument_list|(
name|MESSAGE_COUNT
argument_list|,
name|localBroker
operator|.
name|getDestination
argument_list|(
name|included
argument_list|)
operator|.
name|getDestinationStatistics
argument_list|()
operator|.
name|getForwards
argument_list|()
operator|.
name|getCount
argument_list|()
argument_list|)
expr_stmt|;
comment|// close everything down and restart
name|doTearDown
argument_list|()
expr_stmt|;
name|doSetUp
argument_list|(
literal|false
argument_list|)
expr_stmt|;
name|remoteConsumer
operator|=
name|remoteSession
operator|.
name|createDurableSubscriber
argument_list|(
name|included
argument_list|,
name|consumerName
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
name|MESSAGE_COUNT
condition|;
name|i
operator|++
control|)
block|{
name|assertNotNull
argument_list|(
literal|"message count: "
operator|+
name|i
argument_list|,
name|remoteConsumer
operator|.
name|receive
argument_list|(
literal|2500
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Ignore
argument_list|(
literal|"This seems like a simple use case, but it is problematic to consume an existing topic store, "
operator|+
literal|"it requires a connection per durable to match that connectionId"
argument_list|)
specifier|public
name|void
name|testDurableStoreAndForwardReconnect
parameter_list|()
throws|throws
name|Exception
block|{
comment|// create a local durable consumer
name|MessageConsumer
name|localConsumer
init|=
name|localSession
operator|.
name|createDurableSubscriber
argument_list|(
name|included
argument_list|,
name|consumerName
argument_list|)
decl_stmt|;
name|Thread
operator|.
name|sleep
argument_list|(
literal|5000
argument_list|)
expr_stmt|;
comment|// now close everything down and restart
name|doTearDown
argument_list|()
expr_stmt|;
name|doSetUp
argument_list|(
literal|false
argument_list|)
expr_stmt|;
comment|// send messages
name|MessageProducer
name|producer
init|=
name|localSession
operator|.
name|createProducer
argument_list|(
name|included
argument_list|)
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
name|MESSAGE_COUNT
condition|;
name|i
operator|++
control|)
block|{
name|Message
name|test
init|=
name|localSession
operator|.
name|createTextMessage
argument_list|(
literal|"test-"
operator|+
name|i
argument_list|)
decl_stmt|;
name|producer
operator|.
name|send
argument_list|(
name|test
argument_list|)
expr_stmt|;
block|}
name|Thread
operator|.
name|sleep
argument_list|(
literal|5000
argument_list|)
expr_stmt|;
comment|// consume some messages locally
name|localConsumer
operator|=
name|localSession
operator|.
name|createDurableSubscriber
argument_list|(
name|included
argument_list|,
name|consumerName
argument_list|)
expr_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"Consume from local consumer: "
operator|+
name|localConsumer
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
name|MESSAGE_COUNT
operator|/
literal|2
condition|;
name|i
operator|++
control|)
block|{
name|assertNotNull
argument_list|(
literal|"message count: "
operator|+
name|i
argument_list|,
name|localConsumer
operator|.
name|receive
argument_list|(
literal|2500
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|Thread
operator|.
name|sleep
argument_list|(
literal|5000
argument_list|)
expr_stmt|;
comment|// close everything down and restart
name|doTearDown
argument_list|()
expr_stmt|;
name|doSetUp
argument_list|(
literal|false
argument_list|)
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
literal|"Consume from remote"
argument_list|)
expr_stmt|;
comment|// consume the rest remotely
name|MessageConsumer
name|remoteConsumer
init|=
name|remoteSession
operator|.
name|createDurableSubscriber
argument_list|(
name|included
argument_list|,
name|consumerName
argument_list|)
decl_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"Remote consumer: "
operator|+
name|remoteConsumer
argument_list|)
expr_stmt|;
name|Thread
operator|.
name|sleep
argument_list|(
literal|5000
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
name|MESSAGE_COUNT
operator|/
literal|2
condition|;
name|i
operator|++
control|)
block|{
name|assertNotNull
argument_list|(
literal|"message count: "
operator|+
name|i
argument_list|,
name|remoteConsumer
operator|.
name|receive
argument_list|(
literal|10000
argument_list|)
argument_list|)
expr_stmt|;
block|}
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
name|doSetUp
argument_list|(
literal|true
argument_list|)
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
name|doTearDown
argument_list|()
expr_stmt|;
block|}
specifier|protected
name|void
name|doTearDown
parameter_list|()
throws|throws
name|Exception
block|{
name|localConnection
operator|.
name|close
argument_list|()
expr_stmt|;
name|remoteConnection
operator|.
name|close
argument_list|()
expr_stmt|;
name|localBroker
operator|.
name|stop
argument_list|()
expr_stmt|;
name|remoteBroker
operator|.
name|stop
argument_list|()
expr_stmt|;
block|}
specifier|protected
name|void
name|doSetUp
parameter_list|(
name|boolean
name|deleteAllMessages
parameter_list|)
throws|throws
name|Exception
block|{
name|remoteBroker
operator|=
name|createRemoteBroker
argument_list|()
expr_stmt|;
name|remoteBroker
operator|.
name|setDeleteAllMessagesOnStartup
argument_list|(
name|deleteAllMessages
argument_list|)
expr_stmt|;
name|remoteBroker
operator|.
name|start
argument_list|()
expr_stmt|;
name|remoteBroker
operator|.
name|waitUntilStarted
argument_list|()
expr_stmt|;
name|localBroker
operator|=
name|createLocalBroker
argument_list|()
expr_stmt|;
name|localBroker
operator|.
name|setDeleteAllMessagesOnStartup
argument_list|(
name|deleteAllMessages
argument_list|)
expr_stmt|;
name|localBroker
operator|.
name|start
argument_list|()
expr_stmt|;
name|localBroker
operator|.
name|waitUntilStarted
argument_list|()
expr_stmt|;
name|URI
name|localURI
init|=
name|localBroker
operator|.
name|getVmConnectorURI
argument_list|()
decl_stmt|;
name|ActiveMQConnectionFactory
name|fac
init|=
operator|new
name|ActiveMQConnectionFactory
argument_list|(
name|localURI
argument_list|)
decl_stmt|;
name|fac
operator|.
name|setAlwaysSyncSend
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|fac
operator|.
name|setDispatchAsync
argument_list|(
literal|false
argument_list|)
expr_stmt|;
name|localConnection
operator|=
name|fac
operator|.
name|createConnection
argument_list|()
expr_stmt|;
name|localConnection
operator|.
name|setClientID
argument_list|(
literal|"clientId"
argument_list|)
expr_stmt|;
name|localConnection
operator|.
name|start
argument_list|()
expr_stmt|;
name|URI
name|remoteURI
init|=
name|remoteBroker
operator|.
name|getVmConnectorURI
argument_list|()
decl_stmt|;
name|fac
operator|=
operator|new
name|ActiveMQConnectionFactory
argument_list|(
name|remoteURI
argument_list|)
expr_stmt|;
name|remoteConnection
operator|=
name|fac
operator|.
name|createConnection
argument_list|()
expr_stmt|;
name|remoteConnection
operator|.
name|setClientID
argument_list|(
literal|"clientId"
argument_list|)
expr_stmt|;
name|remoteConnection
operator|.
name|start
argument_list|()
expr_stmt|;
name|included
operator|=
operator|new
name|ActiveMQTopic
argument_list|(
literal|"include.test.bar"
argument_list|)
expr_stmt|;
name|excluded
operator|=
operator|new
name|ActiveMQTopic
argument_list|(
literal|"exclude.test.bar"
argument_list|)
expr_stmt|;
name|localSession
operator|=
name|localConnection
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
name|remoteSession
operator|=
name|remoteConnection
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
block|}
specifier|protected
name|String
name|getRemoteBrokerURI
parameter_list|()
block|{
return|return
literal|"org/apache/activemq/network/remoteBroker.xml"
return|;
block|}
specifier|protected
name|String
name|getLocalBrokerURI
parameter_list|()
block|{
return|return
literal|"org/apache/activemq/network/localBroker.xml"
return|;
block|}
specifier|protected
name|BrokerService
name|createBroker
parameter_list|(
name|String
name|uri
parameter_list|)
throws|throws
name|Exception
block|{
name|Resource
name|resource
init|=
operator|new
name|ClassPathResource
argument_list|(
name|uri
argument_list|)
decl_stmt|;
name|BrokerFactoryBean
name|factory
init|=
operator|new
name|BrokerFactoryBean
argument_list|(
name|resource
argument_list|)
decl_stmt|;
name|resource
operator|=
operator|new
name|ClassPathResource
argument_list|(
name|uri
argument_list|)
expr_stmt|;
name|factory
operator|=
operator|new
name|BrokerFactoryBean
argument_list|(
name|resource
argument_list|)
expr_stmt|;
name|factory
operator|.
name|afterPropertiesSet
argument_list|()
expr_stmt|;
name|BrokerService
name|result
init|=
name|factory
operator|.
name|getBroker
argument_list|()
decl_stmt|;
return|return
name|result
return|;
block|}
specifier|protected
name|BrokerService
name|createLocalBroker
parameter_list|()
throws|throws
name|Exception
block|{
return|return
name|createBroker
argument_list|(
name|getLocalBrokerURI
argument_list|()
argument_list|)
return|;
block|}
specifier|protected
name|BrokerService
name|createRemoteBroker
parameter_list|()
throws|throws
name|Exception
block|{
return|return
name|createBroker
argument_list|(
name|getRemoteBrokerURI
argument_list|()
argument_list|)
return|;
block|}
specifier|protected
name|void
name|assertNetworkBridgeStatistics
parameter_list|(
specifier|final
name|long
name|expectedLocalSent
parameter_list|,
specifier|final
name|long
name|expectedRemoteSent
parameter_list|)
throws|throws
name|Exception
block|{
specifier|final
name|NetworkBridge
name|localBridge
init|=
name|localBroker
operator|.
name|getNetworkConnectors
argument_list|()
operator|.
name|get
argument_list|(
literal|0
argument_list|)
operator|.
name|activeBridges
argument_list|()
operator|.
name|iterator
argument_list|()
operator|.
name|next
argument_list|()
decl_stmt|;
specifier|final
name|NetworkBridge
name|remoteBridge
init|=
name|remoteBroker
operator|.
name|getNetworkConnectors
argument_list|()
operator|.
name|get
argument_list|(
literal|0
argument_list|)
operator|.
name|activeBridges
argument_list|()
operator|.
name|iterator
argument_list|()
operator|.
name|next
argument_list|()
decl_stmt|;
name|assertTrue
argument_list|(
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
name|expectedLocalSent
operator|==
name|localBridge
operator|.
name|getNetworkBridgeStatistics
argument_list|()
operator|.
name|getDequeues
argument_list|()
operator|.
name|getCount
argument_list|()
operator|&&
literal|0
operator|==
name|localBridge
operator|.
name|getNetworkBridgeStatistics
argument_list|()
operator|.
name|getReceivedCount
argument_list|()
operator|.
name|getCount
argument_list|()
operator|&&
name|expectedRemoteSent
operator|==
name|remoteBridge
operator|.
name|getNetworkBridgeStatistics
argument_list|()
operator|.
name|getDequeues
argument_list|()
operator|.
name|getCount
argument_list|()
operator|&&
literal|0
operator|==
name|remoteBridge
operator|.
name|getNetworkBridgeStatistics
argument_list|()
operator|.
name|getReceivedCount
argument_list|()
operator|.
name|getCount
argument_list|()
return|;
block|}
block|}
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

