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
package|;
end_package

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
name|RedeliveryPolicy
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
name|RedeliveryPolicyMap
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
name|SharedDeadLetterStrategy
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
name|util
operator|.
name|RedeliveryPlugin
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
name|BrokerRedeliveryTest
extends|extends
name|org
operator|.
name|apache
operator|.
name|activemq
operator|.
name|TestSupport
block|{
specifier|static
specifier|final
name|Logger
name|LOG
init|=
name|LoggerFactory
operator|.
name|getLogger
argument_list|(
name|BrokerRedeliveryTest
operator|.
name|class
argument_list|)
decl_stmt|;
name|BrokerService
name|broker
init|=
literal|null
decl_stmt|;
specifier|final
name|ActiveMQQueue
name|destination
init|=
operator|new
name|ActiveMQQueue
argument_list|(
literal|"Redelivery"
argument_list|)
decl_stmt|;
specifier|final
name|String
name|data
init|=
literal|"hi"
decl_stmt|;
specifier|final
name|long
name|redeliveryDelayMillis
init|=
literal|2000
decl_stmt|;
name|long
name|initialRedeliveryDelayMillis
init|=
literal|4000
decl_stmt|;
name|int
name|maxBrokerRedeliveries
init|=
literal|2
decl_stmt|;
specifier|public
name|void
name|testScheduledRedelivery
parameter_list|()
throws|throws
name|Exception
block|{
name|doTestScheduledRedelivery
argument_list|(
name|maxBrokerRedeliveries
argument_list|,
literal|true
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|testInfiniteRedelivery
parameter_list|()
throws|throws
name|Exception
block|{
name|initialRedeliveryDelayMillis
operator|=
name|redeliveryDelayMillis
expr_stmt|;
name|maxBrokerRedeliveries
operator|=
name|RedeliveryPolicy
operator|.
name|NO_MAXIMUM_REDELIVERIES
expr_stmt|;
name|doTestScheduledRedelivery
argument_list|(
name|RedeliveryPolicy
operator|.
name|DEFAULT_MAXIMUM_REDELIVERIES
operator|+
literal|1
argument_list|,
literal|false
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|doTestScheduledRedelivery
parameter_list|(
name|int
name|maxBrokerRedeliveriesToValidate
parameter_list|,
name|boolean
name|validateDLQ
parameter_list|)
throws|throws
name|Exception
block|{
name|startBroker
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|sendMessage
argument_list|(
literal|0
argument_list|)
expr_stmt|;
name|ActiveMQConnection
name|consumerConnection
init|=
operator|(
name|ActiveMQConnection
operator|)
name|createConnection
argument_list|()
decl_stmt|;
name|RedeliveryPolicy
name|redeliveryPolicy
init|=
operator|new
name|RedeliveryPolicy
argument_list|()
decl_stmt|;
name|redeliveryPolicy
operator|.
name|setInitialRedeliveryDelay
argument_list|(
literal|0
argument_list|)
expr_stmt|;
name|redeliveryPolicy
operator|.
name|setMaximumRedeliveries
argument_list|(
literal|0
argument_list|)
expr_stmt|;
name|consumerConnection
operator|.
name|setRedeliveryPolicy
argument_list|(
name|redeliveryPolicy
argument_list|)
expr_stmt|;
name|consumerConnection
operator|.
name|start
argument_list|()
expr_stmt|;
name|Session
name|consumerSession
init|=
name|consumerConnection
operator|.
name|createSession
argument_list|(
literal|true
argument_list|,
name|Session
operator|.
name|SESSION_TRANSACTED
argument_list|)
decl_stmt|;
name|MessageConsumer
name|consumer
init|=
name|consumerSession
operator|.
name|createConsumer
argument_list|(
name|destination
argument_list|)
decl_stmt|;
name|Message
name|message
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
literal|"got message"
argument_list|,
name|message
argument_list|)
expr_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"got: "
operator|+
name|message
argument_list|)
expr_stmt|;
name|consumerSession
operator|.
name|rollback
argument_list|()
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
name|maxBrokerRedeliveriesToValidate
condition|;
name|i
operator|++
control|)
block|{
name|Message
name|shouldBeNull
init|=
name|consumer
operator|.
name|receive
argument_list|(
literal|500
argument_list|)
decl_stmt|;
name|assertNull
argument_list|(
literal|"did not get message early: "
operator|+
name|shouldBeNull
argument_list|,
name|shouldBeNull
argument_list|)
expr_stmt|;
name|TimeUnit
operator|.
name|SECONDS
operator|.
name|sleep
argument_list|(
literal|4
argument_list|)
expr_stmt|;
name|Message
name|brokerRedeliveryMessage
init|=
name|consumer
operator|.
name|receive
argument_list|(
literal|1500
argument_list|)
decl_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"got: "
operator|+
name|brokerRedeliveryMessage
argument_list|)
expr_stmt|;
name|assertNotNull
argument_list|(
literal|"got message via broker redelivery after delay"
argument_list|,
name|brokerRedeliveryMessage
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"message matches"
argument_list|,
name|message
operator|.
name|getStringProperty
argument_list|(
literal|"data"
argument_list|)
argument_list|,
name|brokerRedeliveryMessage
operator|.
name|getStringProperty
argument_list|(
literal|"data"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"has expiryDelay specified - iteration:"
operator|+
name|i
argument_list|,
name|i
operator|==
literal|0
condition|?
name|initialRedeliveryDelayMillis
else|:
name|redeliveryDelayMillis
argument_list|,
name|brokerRedeliveryMessage
operator|.
name|getLongProperty
argument_list|(
name|RedeliveryPlugin
operator|.
name|REDELIVERY_DELAY
argument_list|)
argument_list|)
expr_stmt|;
name|consumerSession
operator|.
name|rollback
argument_list|()
expr_stmt|;
block|}
if|if
condition|(
name|validateDLQ
condition|)
block|{
name|MessageConsumer
name|dlqConsumer
init|=
name|consumerSession
operator|.
name|createConsumer
argument_list|(
operator|new
name|ActiveMQQueue
argument_list|(
name|SharedDeadLetterStrategy
operator|.
name|DEFAULT_DEAD_LETTER_QUEUE_NAME
argument_list|)
argument_list|)
decl_stmt|;
name|Message
name|dlqMessage
init|=
name|dlqConsumer
operator|.
name|receive
argument_list|(
literal|2000
argument_list|)
decl_stmt|;
name|assertNotNull
argument_list|(
literal|"Got message from dql"
argument_list|,
name|dlqMessage
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"message matches"
argument_list|,
name|message
operator|.
name|getStringProperty
argument_list|(
literal|"data"
argument_list|)
argument_list|,
name|dlqMessage
operator|.
name|getStringProperty
argument_list|(
literal|"data"
argument_list|)
argument_list|)
expr_stmt|;
name|consumerSession
operator|.
name|commit
argument_list|()
expr_stmt|;
block|}
else|else
block|{
comment|// consume/commit ok
name|message
operator|=
name|consumer
operator|.
name|receive
argument_list|(
literal|3000
argument_list|)
expr_stmt|;
name|assertNotNull
argument_list|(
literal|"got message"
argument_list|,
name|message
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"redeliveries accounted for"
argument_list|,
name|maxBrokerRedeliveriesToValidate
operator|+
literal|2
argument_list|,
name|message
operator|.
name|getLongProperty
argument_list|(
literal|"JMSXDeliveryCount"
argument_list|)
argument_list|)
expr_stmt|;
name|consumerSession
operator|.
name|commit
argument_list|()
expr_stmt|;
block|}
name|consumerConnection
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
specifier|public
name|void
name|testNoScheduledRedeliveryOfExpired
parameter_list|()
throws|throws
name|Exception
block|{
name|startBroker
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|ActiveMQConnection
name|consumerConnection
init|=
operator|(
name|ActiveMQConnection
operator|)
name|createConnection
argument_list|()
decl_stmt|;
name|consumerConnection
operator|.
name|start
argument_list|()
expr_stmt|;
name|Session
name|consumerSession
init|=
name|consumerConnection
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
name|MessageConsumer
name|consumer
init|=
name|consumerSession
operator|.
name|createConsumer
argument_list|(
name|destination
argument_list|)
decl_stmt|;
name|sendMessage
argument_list|(
literal|1500
argument_list|)
expr_stmt|;
name|Message
name|message
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
literal|"got message"
argument_list|,
name|message
argument_list|)
expr_stmt|;
comment|// ensure there is another consumer to redispatch to
name|MessageConsumer
name|redeliverConsumer
init|=
name|consumerSession
operator|.
name|createConsumer
argument_list|(
name|destination
argument_list|)
decl_stmt|;
comment|// allow consumed to expire so it gets redelivered
name|TimeUnit
operator|.
name|SECONDS
operator|.
name|sleep
argument_list|(
literal|2
argument_list|)
expr_stmt|;
name|consumer
operator|.
name|close
argument_list|()
expr_stmt|;
comment|// should go to dlq as it has expired
comment|// validate DLQ
name|MessageConsumer
name|dlqConsumer
init|=
name|consumerSession
operator|.
name|createConsumer
argument_list|(
operator|new
name|ActiveMQQueue
argument_list|(
name|SharedDeadLetterStrategy
operator|.
name|DEFAULT_DEAD_LETTER_QUEUE_NAME
argument_list|)
argument_list|)
decl_stmt|;
name|Message
name|dlqMessage
init|=
name|dlqConsumer
operator|.
name|receive
argument_list|(
literal|2000
argument_list|)
decl_stmt|;
name|assertNotNull
argument_list|(
literal|"Got message from dql"
argument_list|,
name|dlqMessage
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"message matches"
argument_list|,
name|message
operator|.
name|getStringProperty
argument_list|(
literal|"data"
argument_list|)
argument_list|,
name|dlqMessage
operator|.
name|getStringProperty
argument_list|(
literal|"data"
argument_list|)
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|testNoScheduledRedeliveryOfDuplicates
parameter_list|()
throws|throws
name|Exception
block|{
name|broker
operator|=
name|createBroker
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|PolicyEntry
name|policyEntry
init|=
operator|new
name|PolicyEntry
argument_list|()
decl_stmt|;
name|policyEntry
operator|.
name|setUseCache
argument_list|(
literal|false
argument_list|)
expr_stmt|;
comment|// disable the cache such that duplicates are not suppressed on send
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
name|policyEntry
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
literal|true
argument_list|)
expr_stmt|;
name|broker
operator|.
name|start
argument_list|()
expr_stmt|;
name|ActiveMQConnection
name|consumerConnection
init|=
operator|(
name|ActiveMQConnection
operator|)
name|createConnection
argument_list|()
decl_stmt|;
name|consumerConnection
operator|.
name|start
argument_list|()
expr_stmt|;
name|Session
name|consumerSession
init|=
name|consumerConnection
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
name|MessageConsumer
name|consumer
init|=
name|consumerSession
operator|.
name|createConsumer
argument_list|(
name|destination
argument_list|)
decl_stmt|;
name|ActiveMQConnection
name|producerConnection
init|=
operator|(
name|ActiveMQConnection
operator|)
name|createConnection
argument_list|()
decl_stmt|;
name|producerConnection
operator|.
name|start
argument_list|()
expr_stmt|;
name|Session
name|producerSession
init|=
name|producerConnection
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
name|producerSession
operator|.
name|createProducer
argument_list|(
name|destination
argument_list|)
decl_stmt|;
name|Message
name|message
init|=
name|producerSession
operator|.
name|createMessage
argument_list|()
decl_stmt|;
name|message
operator|.
name|setStringProperty
argument_list|(
literal|"data"
argument_list|,
name|data
argument_list|)
expr_stmt|;
name|producer
operator|.
name|send
argument_list|(
name|message
argument_list|)
expr_stmt|;
name|message
operator|=
name|consumer
operator|.
name|receive
argument_list|(
literal|1000
argument_list|)
expr_stmt|;
name|assertNotNull
argument_list|(
literal|"got message"
argument_list|,
name|message
argument_list|)
expr_stmt|;
name|message
operator|.
name|acknowledge
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
annotation|@
name|Override
specifier|public
name|boolean
name|isSatisified
parameter_list|()
throws|throws
name|Exception
block|{
comment|// wait for ack to be processes
name|LOG
operator|.
name|info
argument_list|(
literal|"Total message count: "
operator|+
name|broker
operator|.
name|getAdminView
argument_list|()
operator|.
name|getTotalMessageCount
argument_list|()
argument_list|)
expr_stmt|;
return|return
name|broker
operator|.
name|getAdminView
argument_list|()
operator|.
name|getTotalMessageCount
argument_list|()
operator|==
literal|0
return|;
block|}
block|}
argument_list|)
expr_stmt|;
comment|// send it again
comment|// should go to dlq as a duplicate from the store
name|producerConnection
operator|.
name|getTransport
argument_list|()
operator|.
name|request
argument_list|(
name|message
argument_list|)
expr_stmt|;
comment|// validate DLQ
name|MessageConsumer
name|dlqConsumer
init|=
name|consumerSession
operator|.
name|createConsumer
argument_list|(
operator|new
name|ActiveMQQueue
argument_list|(
name|SharedDeadLetterStrategy
operator|.
name|DEFAULT_DEAD_LETTER_QUEUE_NAME
argument_list|)
argument_list|)
decl_stmt|;
name|Message
name|dlqMessage
init|=
name|dlqConsumer
operator|.
name|receive
argument_list|(
literal|4000
argument_list|)
decl_stmt|;
name|assertNotNull
argument_list|(
literal|"Got message from dql"
argument_list|,
name|dlqMessage
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"message matches"
argument_list|,
name|message
operator|.
name|getStringProperty
argument_list|(
literal|"data"
argument_list|)
argument_list|,
name|dlqMessage
operator|.
name|getStringProperty
argument_list|(
literal|"data"
argument_list|)
argument_list|)
expr_stmt|;
block|}
specifier|private
name|void
name|sendMessage
parameter_list|(
name|int
name|timeToLive
parameter_list|)
throws|throws
name|Exception
block|{
name|ActiveMQConnection
name|producerConnection
init|=
operator|(
name|ActiveMQConnection
operator|)
name|createConnection
argument_list|()
decl_stmt|;
name|producerConnection
operator|.
name|start
argument_list|()
expr_stmt|;
name|Session
name|producerSession
init|=
name|producerConnection
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
name|producerSession
operator|.
name|createProducer
argument_list|(
name|destination
argument_list|)
decl_stmt|;
if|if
condition|(
name|timeToLive
operator|>
literal|0
condition|)
block|{
name|producer
operator|.
name|setTimeToLive
argument_list|(
name|timeToLive
argument_list|)
expr_stmt|;
block|}
name|Message
name|message
init|=
name|producerSession
operator|.
name|createMessage
argument_list|()
decl_stmt|;
name|message
operator|.
name|setStringProperty
argument_list|(
literal|"data"
argument_list|,
name|data
argument_list|)
expr_stmt|;
name|producer
operator|.
name|send
argument_list|(
name|message
argument_list|)
expr_stmt|;
name|producerConnection
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
specifier|private
name|void
name|startBroker
parameter_list|(
name|boolean
name|deleteMessages
parameter_list|)
throws|throws
name|Exception
block|{
name|broker
operator|=
name|createBroker
argument_list|(
literal|false
argument_list|)
expr_stmt|;
if|if
condition|(
name|deleteMessages
condition|)
block|{
name|broker
operator|.
name|setDeleteAllMessagesOnStartup
argument_list|(
literal|true
argument_list|)
expr_stmt|;
block|}
name|broker
operator|.
name|start
argument_list|()
expr_stmt|;
block|}
specifier|private
name|BrokerService
name|createBroker
parameter_list|(
name|boolean
name|persistent
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
name|setPersistent
argument_list|(
name|persistent
argument_list|)
expr_stmt|;
name|broker
operator|.
name|setSchedulerSupport
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|RedeliveryPlugin
name|redeliveryPlugin
init|=
operator|new
name|RedeliveryPlugin
argument_list|()
decl_stmt|;
name|RedeliveryPolicy
name|brokerRedeliveryPolicy
init|=
operator|new
name|RedeliveryPolicy
argument_list|()
decl_stmt|;
name|brokerRedeliveryPolicy
operator|.
name|setRedeliveryDelay
argument_list|(
name|redeliveryDelayMillis
argument_list|)
expr_stmt|;
name|brokerRedeliveryPolicy
operator|.
name|setInitialRedeliveryDelay
argument_list|(
name|initialRedeliveryDelayMillis
argument_list|)
expr_stmt|;
name|brokerRedeliveryPolicy
operator|.
name|setMaximumRedeliveries
argument_list|(
name|maxBrokerRedeliveries
argument_list|)
expr_stmt|;
name|RedeliveryPolicyMap
name|redeliveryPolicyMap
init|=
operator|new
name|RedeliveryPolicyMap
argument_list|()
decl_stmt|;
name|redeliveryPolicyMap
operator|.
name|setDefaultEntry
argument_list|(
name|brokerRedeliveryPolicy
argument_list|)
expr_stmt|;
name|redeliveryPlugin
operator|.
name|setRedeliveryPolicyMap
argument_list|(
name|redeliveryPolicyMap
argument_list|)
expr_stmt|;
name|broker
operator|.
name|setPlugins
argument_list|(
operator|new
name|BrokerPlugin
index|[]
block|{
name|redeliveryPlugin
block|}
argument_list|)
expr_stmt|;
return|return
name|broker
return|;
block|}
specifier|private
name|void
name|stopBroker
parameter_list|()
throws|throws
name|Exception
block|{
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
name|broker
operator|=
literal|null
expr_stmt|;
block|}
block|}
annotation|@
name|Override
specifier|protected
name|ActiveMQConnectionFactory
name|createConnectionFactory
parameter_list|()
throws|throws
name|Exception
block|{
return|return
operator|new
name|ActiveMQConnectionFactory
argument_list|(
literal|"vm://localhost"
argument_list|)
return|;
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
name|stopBroker
argument_list|()
expr_stmt|;
name|super
operator|.
name|tearDown
argument_list|()
expr_stmt|;
block|}
block|}
end_class

end_unit

