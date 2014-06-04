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
name|advisory
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
name|List
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|jms
operator|.
name|BytesMessage
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
name|ConnectionFactory
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
name|jms
operator|.
name|TemporaryQueue
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|jms
operator|.
name|Topic
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
name|policy
operator|.
name|ConstantPendingMessageLimitStrategy
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
name|ActiveMQMessage
import|;
end_import

begin_class
specifier|public
class|class
name|AdvisoryTempDestinationTests
extends|extends
name|TestCase
block|{
specifier|protected
specifier|static
specifier|final
name|int
name|MESSAGE_COUNT
init|=
literal|2000
decl_stmt|;
specifier|protected
name|BrokerService
name|broker
decl_stmt|;
specifier|protected
name|Connection
name|connection
decl_stmt|;
specifier|protected
name|String
name|bindAddress
init|=
name|ActiveMQConnectionFactory
operator|.
name|DEFAULT_BROKER_BIND_URL
decl_stmt|;
specifier|protected
name|int
name|topicCount
decl_stmt|;
specifier|public
name|void
name|testNoSlowConsumerAdvisory
parameter_list|()
throws|throws
name|Exception
block|{
name|Session
name|s
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
name|TemporaryQueue
name|queue
init|=
name|s
operator|.
name|createTemporaryQueue
argument_list|()
decl_stmt|;
name|MessageConsumer
name|consumer
init|=
name|s
operator|.
name|createConsumer
argument_list|(
name|queue
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
block|{             }
block|}
argument_list|)
expr_stmt|;
name|Topic
name|advisoryTopic
init|=
name|AdvisorySupport
operator|.
name|getSlowConsumerAdvisoryTopic
argument_list|(
operator|(
name|ActiveMQDestination
operator|)
name|queue
argument_list|)
decl_stmt|;
name|s
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
name|MessageConsumer
name|advisoryConsumer
init|=
name|s
operator|.
name|createConsumer
argument_list|(
name|advisoryTopic
argument_list|)
decl_stmt|;
comment|// start throwing messages at the consumer
name|MessageProducer
name|producer
init|=
name|s
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
name|BytesMessage
name|m
init|=
name|s
operator|.
name|createBytesMessage
argument_list|()
decl_stmt|;
name|m
operator|.
name|writeBytes
argument_list|(
operator|new
name|byte
index|[
literal|1024
index|]
argument_list|)
expr_stmt|;
name|producer
operator|.
name|send
argument_list|(
name|m
argument_list|)
expr_stmt|;
block|}
name|Message
name|msg
init|=
name|advisoryConsumer
operator|.
name|receive
argument_list|(
literal|1000
argument_list|)
decl_stmt|;
name|assertNull
argument_list|(
name|msg
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|testSlowConsumerAdvisory
parameter_list|()
throws|throws
name|Exception
block|{
name|Session
name|s
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
name|TemporaryQueue
name|queue
init|=
name|s
operator|.
name|createTemporaryQueue
argument_list|()
decl_stmt|;
name|MessageConsumer
name|consumer
init|=
name|s
operator|.
name|createConsumer
argument_list|(
name|queue
argument_list|)
decl_stmt|;
name|assertNotNull
argument_list|(
name|consumer
argument_list|)
expr_stmt|;
name|Topic
name|advisoryTopic
init|=
name|AdvisorySupport
operator|.
name|getSlowConsumerAdvisoryTopic
argument_list|(
operator|(
name|ActiveMQDestination
operator|)
name|queue
argument_list|)
decl_stmt|;
name|s
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
name|MessageConsumer
name|advisoryConsumer
init|=
name|s
operator|.
name|createConsumer
argument_list|(
name|advisoryTopic
argument_list|)
decl_stmt|;
comment|// start throwing messages at the consumer
name|MessageProducer
name|producer
init|=
name|s
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
name|BytesMessage
name|m
init|=
name|s
operator|.
name|createBytesMessage
argument_list|()
decl_stmt|;
name|m
operator|.
name|writeBytes
argument_list|(
operator|new
name|byte
index|[
literal|1024
index|]
argument_list|)
expr_stmt|;
name|producer
operator|.
name|send
argument_list|(
name|m
argument_list|)
expr_stmt|;
block|}
name|Message
name|msg
init|=
name|advisoryConsumer
operator|.
name|receive
argument_list|(
literal|1000
argument_list|)
decl_stmt|;
name|assertNotNull
argument_list|(
name|msg
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|testMessageDeliveryAdvisory
parameter_list|()
throws|throws
name|Exception
block|{
name|Session
name|s
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
name|TemporaryQueue
name|queue
init|=
name|s
operator|.
name|createTemporaryQueue
argument_list|()
decl_stmt|;
name|MessageConsumer
name|consumer
init|=
name|s
operator|.
name|createConsumer
argument_list|(
name|queue
argument_list|)
decl_stmt|;
name|assertNotNull
argument_list|(
name|consumer
argument_list|)
expr_stmt|;
name|Topic
name|advisoryTopic
init|=
name|AdvisorySupport
operator|.
name|getMessageDeliveredAdvisoryTopic
argument_list|(
operator|(
name|ActiveMQDestination
operator|)
name|queue
argument_list|)
decl_stmt|;
name|MessageConsumer
name|advisoryConsumer
init|=
name|s
operator|.
name|createConsumer
argument_list|(
name|advisoryTopic
argument_list|)
decl_stmt|;
comment|//start throwing messages at the consumer
name|MessageProducer
name|producer
init|=
name|s
operator|.
name|createProducer
argument_list|(
name|queue
argument_list|)
decl_stmt|;
name|BytesMessage
name|m
init|=
name|s
operator|.
name|createBytesMessage
argument_list|()
decl_stmt|;
name|m
operator|.
name|writeBytes
argument_list|(
operator|new
name|byte
index|[
literal|1024
index|]
argument_list|)
expr_stmt|;
name|producer
operator|.
name|send
argument_list|(
name|m
argument_list|)
expr_stmt|;
name|Message
name|msg
init|=
name|advisoryConsumer
operator|.
name|receive
argument_list|(
literal|1000
argument_list|)
decl_stmt|;
name|assertNotNull
argument_list|(
name|msg
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|testTempMessageConsumedAdvisory
parameter_list|()
throws|throws
name|Exception
block|{
name|Session
name|s
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
name|TemporaryQueue
name|queue
init|=
name|s
operator|.
name|createTemporaryQueue
argument_list|()
decl_stmt|;
name|MessageConsumer
name|consumer
init|=
name|s
operator|.
name|createConsumer
argument_list|(
name|queue
argument_list|)
decl_stmt|;
name|Topic
name|advisoryTopic
init|=
name|AdvisorySupport
operator|.
name|getMessageConsumedAdvisoryTopic
argument_list|(
operator|(
name|ActiveMQDestination
operator|)
name|queue
argument_list|)
decl_stmt|;
name|MessageConsumer
name|advisoryConsumer
init|=
name|s
operator|.
name|createConsumer
argument_list|(
name|advisoryTopic
argument_list|)
decl_stmt|;
comment|//start throwing messages at the consumer
name|MessageProducer
name|producer
init|=
name|s
operator|.
name|createProducer
argument_list|(
name|queue
argument_list|)
decl_stmt|;
name|BytesMessage
name|m
init|=
name|s
operator|.
name|createBytesMessage
argument_list|()
decl_stmt|;
name|m
operator|.
name|writeBytes
argument_list|(
operator|new
name|byte
index|[
literal|1024
index|]
argument_list|)
expr_stmt|;
name|producer
operator|.
name|send
argument_list|(
name|m
argument_list|)
expr_stmt|;
name|String
name|id
init|=
name|m
operator|.
name|getJMSMessageID
argument_list|()
decl_stmt|;
name|Message
name|msg
init|=
name|consumer
operator|.
name|receive
argument_list|(
literal|1000
argument_list|)
decl_stmt|;
name|assertNotNull
argument_list|(
name|msg
argument_list|)
expr_stmt|;
name|msg
operator|=
name|advisoryConsumer
operator|.
name|receive
argument_list|(
literal|1000
argument_list|)
expr_stmt|;
name|assertNotNull
argument_list|(
name|msg
argument_list|)
expr_stmt|;
name|ActiveMQMessage
name|message
init|=
operator|(
name|ActiveMQMessage
operator|)
name|msg
decl_stmt|;
name|ActiveMQMessage
name|payload
init|=
operator|(
name|ActiveMQMessage
operator|)
name|message
operator|.
name|getDataStructure
argument_list|()
decl_stmt|;
name|String
name|originalId
init|=
name|payload
operator|.
name|getJMSMessageID
argument_list|()
decl_stmt|;
name|assertEquals
argument_list|(
name|originalId
argument_list|,
name|id
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|testMessageExpiredAdvisory
parameter_list|()
throws|throws
name|Exception
block|{
name|Session
name|s
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
name|s
operator|.
name|createQueue
argument_list|(
name|getClass
argument_list|()
operator|.
name|getName
argument_list|()
argument_list|)
decl_stmt|;
name|MessageConsumer
name|consumer
init|=
name|s
operator|.
name|createConsumer
argument_list|(
name|queue
argument_list|)
decl_stmt|;
name|assertNotNull
argument_list|(
name|consumer
argument_list|)
expr_stmt|;
name|Topic
name|advisoryTopic
init|=
name|AdvisorySupport
operator|.
name|getExpiredMessageTopic
argument_list|(
operator|(
name|ActiveMQDestination
operator|)
name|queue
argument_list|)
decl_stmt|;
name|MessageConsumer
name|advisoryConsumer
init|=
name|s
operator|.
name|createConsumer
argument_list|(
name|advisoryTopic
argument_list|)
decl_stmt|;
comment|//start throwing messages at the consumer
name|MessageProducer
name|producer
init|=
name|s
operator|.
name|createProducer
argument_list|(
name|queue
argument_list|)
decl_stmt|;
name|producer
operator|.
name|setTimeToLive
argument_list|(
literal|1
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
name|BytesMessage
name|m
init|=
name|s
operator|.
name|createBytesMessage
argument_list|()
decl_stmt|;
name|m
operator|.
name|writeBytes
argument_list|(
operator|new
name|byte
index|[
literal|1024
index|]
argument_list|)
expr_stmt|;
name|producer
operator|.
name|send
argument_list|(
name|m
argument_list|)
expr_stmt|;
block|}
name|Message
name|msg
init|=
name|advisoryConsumer
operator|.
name|receive
argument_list|(
literal|5000
argument_list|)
decl_stmt|;
name|assertNotNull
argument_list|(
name|msg
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|protected
name|void
name|setUp
parameter_list|()
throws|throws
name|Exception
block|{
if|if
condition|(
name|broker
operator|==
literal|null
condition|)
block|{
name|broker
operator|=
name|createBroker
argument_list|()
expr_stmt|;
block|}
name|ConnectionFactory
name|factory
init|=
name|createConnectionFactory
argument_list|()
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
name|super
operator|.
name|setUp
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Override
specifier|protected
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
name|connection
operator|.
name|close
argument_list|()
expr_stmt|;
if|if
condition|(
name|broker
operator|!=
literal|null
condition|)
block|{
name|broker
operator|.
name|stop
argument_list|()
expr_stmt|;
block|}
block|}
specifier|protected
name|ActiveMQConnectionFactory
name|createConnectionFactory
parameter_list|()
throws|throws
name|Exception
block|{
name|ActiveMQConnectionFactory
name|cf
init|=
operator|new
name|ActiveMQConnectionFactory
argument_list|(
name|ActiveMQConnection
operator|.
name|DEFAULT_BROKER_URL
argument_list|)
decl_stmt|;
return|return
name|cf
return|;
block|}
specifier|protected
name|BrokerService
name|createBroker
parameter_list|()
throws|throws
name|Exception
block|{
name|BrokerService
name|answer
init|=
operator|new
name|BrokerService
argument_list|()
decl_stmt|;
name|configureBroker
argument_list|(
name|answer
argument_list|)
expr_stmt|;
name|answer
operator|.
name|start
argument_list|()
expr_stmt|;
return|return
name|answer
return|;
block|}
specifier|protected
name|void
name|configureBroker
parameter_list|(
name|BrokerService
name|answer
parameter_list|)
throws|throws
name|Exception
block|{
name|answer
operator|.
name|setPersistent
argument_list|(
literal|false
argument_list|)
expr_stmt|;
name|ConstantPendingMessageLimitStrategy
name|strategy
init|=
operator|new
name|ConstantPendingMessageLimitStrategy
argument_list|()
decl_stmt|;
name|strategy
operator|.
name|setLimit
argument_list|(
literal|10
argument_list|)
expr_stmt|;
name|PolicyEntry
name|tempQueueEntry
init|=
name|createPolicyEntry
argument_list|(
name|strategy
argument_list|)
decl_stmt|;
name|tempQueueEntry
operator|.
name|setTempQueue
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|PolicyEntry
name|tempTopicEntry
init|=
name|createPolicyEntry
argument_list|(
name|strategy
argument_list|)
decl_stmt|;
name|tempTopicEntry
operator|.
name|setTempTopic
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|PolicyMap
name|pMap
init|=
operator|new
name|PolicyMap
argument_list|()
decl_stmt|;
specifier|final
name|List
argument_list|<
name|PolicyEntry
argument_list|>
name|policyEntries
init|=
operator|new
name|ArrayList
argument_list|<
name|PolicyEntry
argument_list|>
argument_list|()
decl_stmt|;
name|policyEntries
operator|.
name|add
argument_list|(
name|tempQueueEntry
argument_list|)
expr_stmt|;
name|policyEntries
operator|.
name|add
argument_list|(
name|tempTopicEntry
argument_list|)
expr_stmt|;
name|pMap
operator|.
name|setPolicyEntries
argument_list|(
name|policyEntries
argument_list|)
expr_stmt|;
name|answer
operator|.
name|setDestinationPolicy
argument_list|(
name|pMap
argument_list|)
expr_stmt|;
name|answer
operator|.
name|addConnector
argument_list|(
name|bindAddress
argument_list|)
expr_stmt|;
name|answer
operator|.
name|setDeleteAllMessagesOnStartup
argument_list|(
literal|true
argument_list|)
expr_stmt|;
block|}
specifier|private
name|PolicyEntry
name|createPolicyEntry
parameter_list|(
name|ConstantPendingMessageLimitStrategy
name|strategy
parameter_list|)
block|{
name|PolicyEntry
name|policy
init|=
operator|new
name|PolicyEntry
argument_list|()
decl_stmt|;
name|policy
operator|.
name|setAdvisoryForFastProducers
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|policy
operator|.
name|setAdvisoryForConsumed
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|policy
operator|.
name|setAdvisoryForDelivery
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|policy
operator|.
name|setAdvisoryForDiscardingMessages
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|policy
operator|.
name|setAdvisoryForSlowConsumers
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|policy
operator|.
name|setAdvisoryWhenFull
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|policy
operator|.
name|setProducerFlowControl
argument_list|(
literal|false
argument_list|)
expr_stmt|;
name|policy
operator|.
name|setPendingMessageLimitStrategy
argument_list|(
name|strategy
argument_list|)
expr_stmt|;
return|return
name|policy
return|;
block|}
block|}
end_class

end_unit

