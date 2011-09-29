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
name|failover
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
name|assertTrue
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Vector
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
name|Executors
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
name|TextMessage
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
name|ActiveMQMessageTransformation
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
name|ActiveMQSession
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
name|BrokerPlugin
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
name|BrokerPluginSupport
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
name|ConnectionContext
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
name|Subscription
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
name|command
operator|.
name|ConsumerInfo
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
name|SessionId
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
name|Test
import|;
end_import

begin_comment
comment|// see https://issues.apache.org/activemq/browse/AMQ-2573
end_comment

begin_class
specifier|public
class|class
name|FailoverConsumerUnconsumedTest
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
name|FailoverConsumerUnconsumedTest
operator|.
name|class
argument_list|)
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|String
name|QUEUE_NAME
init|=
literal|"FailoverWithUnconsumed"
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|String
name|TRANSPORT_URI
init|=
literal|"tcp://localhost:0"
decl_stmt|;
specifier|private
name|String
name|url
decl_stmt|;
specifier|final
name|int
name|prefetch
init|=
literal|10
decl_stmt|;
name|BrokerService
name|broker
decl_stmt|;
annotation|@
name|After
specifier|public
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
block|}
block|}
specifier|public
name|void
name|startBroker
parameter_list|(
name|boolean
name|deleteAllMessagesOnStartup
parameter_list|)
throws|throws
name|Exception
block|{
name|broker
operator|=
name|createBroker
argument_list|(
name|deleteAllMessagesOnStartup
argument_list|)
expr_stmt|;
name|broker
operator|.
name|start
argument_list|()
expr_stmt|;
block|}
specifier|public
name|BrokerService
name|createBroker
parameter_list|(
name|boolean
name|deleteAllMessagesOnStartup
parameter_list|)
throws|throws
name|Exception
block|{
return|return
name|createBroker
argument_list|(
name|deleteAllMessagesOnStartup
argument_list|,
name|TRANSPORT_URI
argument_list|)
return|;
block|}
specifier|public
name|BrokerService
name|createBroker
parameter_list|(
name|boolean
name|deleteAllMessagesOnStartup
parameter_list|,
name|String
name|bindAddress
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
name|addConnector
argument_list|(
name|bindAddress
argument_list|)
expr_stmt|;
name|broker
operator|.
name|setDeleteAllMessagesOnStartup
argument_list|(
name|deleteAllMessagesOnStartup
argument_list|)
expr_stmt|;
name|this
operator|.
name|url
operator|=
name|broker
operator|.
name|getTransportConnectors
argument_list|()
operator|.
name|get
argument_list|(
literal|0
argument_list|)
operator|.
name|getConnectUri
argument_list|()
operator|.
name|toString
argument_list|()
expr_stmt|;
return|return
name|broker
return|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testFailoverConsumerDups
parameter_list|()
throws|throws
name|Exception
block|{
name|doTestFailoverConsumerDups
argument_list|(
literal|true
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testFailoverConsumerDupsNoAdvisoryWatch
parameter_list|()
throws|throws
name|Exception
block|{
name|doTestFailoverConsumerDups
argument_list|(
literal|false
argument_list|)
expr_stmt|;
block|}
annotation|@
name|SuppressWarnings
argument_list|(
literal|"unchecked"
argument_list|)
specifier|public
name|void
name|doTestFailoverConsumerDups
parameter_list|(
specifier|final
name|boolean
name|watchTopicAdvisories
parameter_list|)
throws|throws
name|Exception
block|{
specifier|final
name|int
name|maxConsumers
init|=
literal|4
decl_stmt|;
name|broker
operator|=
name|createBroker
argument_list|(
literal|true
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
operator|new
name|BrokerPluginSupport
argument_list|()
block|{
name|int
name|consumerCount
decl_stmt|;
comment|// broker is killed on x create consumer
annotation|@
name|Override
specifier|public
name|Subscription
name|addConsumer
parameter_list|(
name|ConnectionContext
name|context
parameter_list|,
specifier|final
name|ConsumerInfo
name|info
parameter_list|)
throws|throws
name|Exception
block|{
if|if
condition|(
operator|++
name|consumerCount
operator|==
name|maxConsumers
operator|+
operator|(
name|watchTopicAdvisories
condition|?
literal|1
else|:
literal|0
operator|)
condition|)
block|{
name|context
operator|.
name|setDontSendReponse
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|Executors
operator|.
name|newSingleThreadExecutor
argument_list|()
operator|.
name|execute
argument_list|(
operator|new
name|Runnable
argument_list|()
block|{
specifier|public
name|void
name|run
parameter_list|()
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"Stopping broker on consumer: "
operator|+
name|info
operator|.
name|getConsumerId
argument_list|()
argument_list|)
expr_stmt|;
try|try
block|{
name|broker
operator|.
name|stop
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
block|}
block|}
block|}
end_class

begin_empty_stmt
unit|)
empty_stmt|;
end_empty_stmt

begin_expr_stmt
unit|}                         return
name|super
operator|.
name|addConsumer
argument_list|(
name|context
argument_list|,
name|info
argument_list|)
expr_stmt|;
end_expr_stmt

begin_empty_stmt
unit|}                 }         })
empty_stmt|;
end_empty_stmt

begin_expr_stmt
name|broker
operator|.
name|start
argument_list|()
expr_stmt|;
end_expr_stmt

begin_decl_stmt
name|ActiveMQConnectionFactory
name|cf
init|=
operator|new
name|ActiveMQConnectionFactory
argument_list|(
literal|"failover:("
operator|+
name|url
operator|+
literal|")"
argument_list|)
decl_stmt|;
end_decl_stmt

begin_expr_stmt
name|cf
operator|.
name|setWatchTopicAdvisories
argument_list|(
name|watchTopicAdvisories
argument_list|)
expr_stmt|;
end_expr_stmt

begin_decl_stmt
specifier|final
name|ActiveMQConnection
name|connection
init|=
operator|(
name|ActiveMQConnection
operator|)
name|cf
operator|.
name|createConnection
argument_list|()
decl_stmt|;
end_decl_stmt

begin_expr_stmt
name|connection
operator|.
name|start
argument_list|()
expr_stmt|;
end_expr_stmt

begin_decl_stmt
specifier|final
name|Session
name|consumerSession
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
end_decl_stmt

begin_decl_stmt
specifier|final
name|Queue
name|destination
init|=
name|consumerSession
operator|.
name|createQueue
argument_list|(
name|QUEUE_NAME
operator|+
literal|"?jms.consumer.prefetch="
operator|+
name|prefetch
argument_list|)
decl_stmt|;
end_decl_stmt

begin_decl_stmt
specifier|final
name|Vector
argument_list|<
name|TestConsumer
argument_list|>
name|testConsumers
init|=
operator|new
name|Vector
argument_list|<
name|TestConsumer
argument_list|>
argument_list|()
decl_stmt|;
end_decl_stmt

begin_for
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|maxConsumers
operator|-
literal|1
condition|;
name|i
operator|++
control|)
block|{
name|testConsumers
operator|.
name|add
argument_list|(
operator|new
name|TestConsumer
argument_list|(
name|consumerSession
argument_list|,
name|destination
argument_list|,
name|connection
argument_list|)
argument_list|)
expr_stmt|;
block|}
end_for

begin_expr_stmt
name|produceMessage
argument_list|(
name|consumerSession
argument_list|,
name|destination
argument_list|,
name|maxConsumers
operator|*
name|prefetch
argument_list|)
expr_stmt|;
end_expr_stmt

begin_expr_stmt
name|assertTrue
argument_list|(
literal|"add messages are dispatched"
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
name|int
name|totalUnconsumed
init|=
literal|0
decl_stmt|;
for|for
control|(
name|TestConsumer
name|testConsumer
range|:
name|testConsumers
control|)
block|{
name|long
name|unconsumed
init|=
name|testConsumer
operator|.
name|unconsumedSize
argument_list|()
decl_stmt|;
name|LOG
operator|.
name|info
argument_list|(
name|testConsumer
operator|.
name|getConsumerId
argument_list|()
operator|+
literal|" unconsumed: "
operator|+
name|unconsumed
argument_list|)
expr_stmt|;
name|totalUnconsumed
operator|+=
name|unconsumed
expr_stmt|;
block|}
return|return
name|totalUnconsumed
operator|==
operator|(
name|maxConsumers
operator|-
literal|1
operator|)
operator|*
name|prefetch
return|;
block|}
block|}
argument_list|)
argument_list|)
expr_stmt|;
end_expr_stmt

begin_decl_stmt
specifier|final
name|CountDownLatch
name|commitDoneLatch
init|=
operator|new
name|CountDownLatch
argument_list|(
literal|1
argument_list|)
decl_stmt|;
end_decl_stmt

begin_expr_stmt
name|Executors
operator|.
name|newSingleThreadExecutor
argument_list|()
operator|.
name|execute
argument_list|(
operator|new
name|Runnable
argument_list|()
block|{
specifier|public
name|void
name|run
parameter_list|()
block|{
try|try
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"add last consumer..."
argument_list|)
expr_stmt|;
name|testConsumers
operator|.
name|add
argument_list|(
operator|new
name|TestConsumer
argument_list|(
name|consumerSession
argument_list|,
name|destination
argument_list|,
name|connection
argument_list|)
argument_list|)
expr_stmt|;
name|commitDoneLatch
operator|.
name|countDown
argument_list|()
expr_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"done add last consumer"
argument_list|)
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
block|}
block|}
block|}
argument_list|)
expr_stmt|;
end_expr_stmt

begin_comment
comment|// will be stopped by the plugin
end_comment

begin_expr_stmt
name|broker
operator|.
name|waitUntilStopped
argument_list|()
expr_stmt|;
end_expr_stmt

begin_comment
comment|// verify interrupt
end_comment

begin_expr_stmt
name|assertTrue
argument_list|(
literal|"add messages dispatched and unconsumed are cleaned up"
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
name|int
name|totalUnconsumed
init|=
literal|0
decl_stmt|;
for|for
control|(
name|TestConsumer
name|testConsumer
range|:
name|testConsumers
control|)
block|{
name|long
name|unconsumed
init|=
name|testConsumer
operator|.
name|unconsumedSize
argument_list|()
decl_stmt|;
name|LOG
operator|.
name|info
argument_list|(
name|testConsumer
operator|.
name|getConsumerId
argument_list|()
operator|+
literal|" unconsumed: "
operator|+
name|unconsumed
argument_list|)
expr_stmt|;
name|totalUnconsumed
operator|+=
name|unconsumed
expr_stmt|;
block|}
return|return
name|totalUnconsumed
operator|==
literal|0
return|;
block|}
block|}
argument_list|)
argument_list|)
expr_stmt|;
end_expr_stmt

begin_expr_stmt
name|broker
operator|=
name|createBroker
argument_list|(
literal|false
argument_list|,
name|this
operator|.
name|url
argument_list|)
expr_stmt|;
end_expr_stmt

begin_expr_stmt
name|broker
operator|.
name|start
argument_list|()
expr_stmt|;
end_expr_stmt

begin_expr_stmt
name|assertTrue
argument_list|(
literal|"consumer added through failover"
argument_list|,
name|commitDoneLatch
operator|.
name|await
argument_list|(
literal|30
argument_list|,
name|TimeUnit
operator|.
name|SECONDS
argument_list|)
argument_list|)
expr_stmt|;
end_expr_stmt

begin_comment
comment|// each should again get prefetch messages - all unconsumed deliveries should be rolledback
end_comment

begin_expr_stmt
name|assertTrue
argument_list|(
literal|"after start all messages are re dispatched"
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
name|int
name|totalUnconsumed
init|=
literal|0
decl_stmt|;
for|for
control|(
name|TestConsumer
name|testConsumer
range|:
name|testConsumers
control|)
block|{
name|long
name|unconsumed
init|=
name|testConsumer
operator|.
name|unconsumedSize
argument_list|()
decl_stmt|;
name|LOG
operator|.
name|info
argument_list|(
name|testConsumer
operator|.
name|getConsumerId
argument_list|()
operator|+
literal|" after restart: unconsumed: "
operator|+
name|unconsumed
argument_list|)
expr_stmt|;
name|totalUnconsumed
operator|+=
name|unconsumed
expr_stmt|;
block|}
return|return
name|totalUnconsumed
operator|==
operator|(
name|maxConsumers
operator|)
operator|*
name|prefetch
return|;
block|}
block|}
argument_list|)
argument_list|)
expr_stmt|;
end_expr_stmt

begin_expr_stmt
name|connection
operator|.
name|close
argument_list|()
expr_stmt|;
end_expr_stmt

begin_function
unit|}      private
name|void
name|produceMessage
parameter_list|(
specifier|final
name|Session
name|producerSession
parameter_list|,
name|Queue
name|destination
parameter_list|,
name|long
name|count
parameter_list|)
throws|throws
name|JMSException
block|{
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
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|count
condition|;
name|i
operator|++
control|)
block|{
name|TextMessage
name|message
init|=
name|producerSession
operator|.
name|createTextMessage
argument_list|(
literal|"Test message "
operator|+
name|i
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
end_function

begin_comment
comment|// allow access to unconsumedMessages
end_comment

begin_class
class|class
name|TestConsumer
extends|extends
name|ActiveMQMessageConsumer
block|{
name|TestConsumer
parameter_list|(
name|Session
name|consumerSession
parameter_list|,
name|Destination
name|destination
parameter_list|,
name|ActiveMQConnection
name|connection
parameter_list|)
throws|throws
name|Exception
block|{
name|super
argument_list|(
operator|(
name|ActiveMQSession
operator|)
name|consumerSession
argument_list|,
operator|new
name|ConsumerId
argument_list|(
operator|new
name|SessionId
argument_list|(
name|connection
operator|.
name|getConnectionInfo
argument_list|()
operator|.
name|getConnectionId
argument_list|()
argument_list|,
literal|1
argument_list|)
argument_list|,
name|nextGen
argument_list|()
argument_list|)
argument_list|,
name|ActiveMQMessageTransformation
operator|.
name|transformDestination
argument_list|(
name|destination
argument_list|)
argument_list|,
literal|null
argument_list|,
literal|""
argument_list|,
name|prefetch
argument_list|,
operator|-
literal|1
argument_list|,
literal|false
argument_list|,
literal|false
argument_list|,
literal|true
argument_list|,
literal|null
argument_list|)
expr_stmt|;
block|}
specifier|public
name|int
name|unconsumedSize
parameter_list|()
block|{
return|return
name|unconsumedMessages
operator|.
name|size
argument_list|()
return|;
block|}
block|}
end_class

begin_decl_stmt
specifier|static
name|long
name|idGen
init|=
literal|100
decl_stmt|;
end_decl_stmt

begin_function
specifier|private
specifier|static
name|long
name|nextGen
parameter_list|()
block|{
name|idGen
operator|-=
literal|5
expr_stmt|;
return|return
name|idGen
return|;
block|}
end_function

unit|}
end_unit

