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
name|Collections
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|HashMap
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Iterator
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
name|java
operator|.
name|util
operator|.
name|Map
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
name|ConcurrentHashMap
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
name|ThreadFactory
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
name|ConnectionFactory
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
name|junit
operator|.
name|framework
operator|.
name|Test
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
name|TestSupport
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
name|ConcurrentProducerQueueConsumerTest
extends|extends
name|TestSupport
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
name|ConcurrentProducerQueueConsumerTest
operator|.
name|class
argument_list|)
decl_stmt|;
specifier|protected
name|List
argument_list|<
name|Connection
argument_list|>
name|connections
init|=
name|Collections
operator|.
name|synchronizedList
argument_list|(
operator|new
name|ArrayList
argument_list|<
name|Connection
argument_list|>
argument_list|()
argument_list|)
decl_stmt|;
specifier|protected
name|Map
argument_list|<
name|MessageConsumer
argument_list|,
name|TimedMessageListener
argument_list|>
name|consumers
init|=
operator|new
name|HashMap
argument_list|<
name|MessageConsumer
argument_list|,
name|TimedMessageListener
argument_list|>
argument_list|()
decl_stmt|;
specifier|protected
name|MessageIdList
name|allMessagesList
init|=
operator|new
name|MessageIdList
argument_list|()
decl_stmt|;
specifier|private
name|BrokerService
name|broker
decl_stmt|;
specifier|private
specifier|final
name|int
name|consumerCount
init|=
literal|5
decl_stmt|;
specifier|private
specifier|final
name|int
name|messageSize
init|=
literal|1024
decl_stmt|;
specifier|private
specifier|final
name|int
name|NUM_MESSAGES
init|=
literal|500
decl_stmt|;
specifier|private
specifier|final
name|int
name|ITERATIONS
init|=
literal|10
decl_stmt|;
specifier|private
name|int
name|expectedQueueDeliveries
init|=
literal|0
decl_stmt|;
specifier|public
name|void
name|initCombosForTestSendRateWithActivatingConsumers
parameter_list|()
throws|throws
name|Exception
block|{
name|addCombinationValues
argument_list|(
literal|"defaultPersistenceAdapter"
argument_list|,
operator|new
name|Object
index|[]
block|{
name|PersistenceAdapterChoice
operator|.
name|KahaDB
block|,
name|PersistenceAdapterChoice
operator|.
name|LevelDB
block|,
comment|/* too slow for hudson - PersistenceAdapterChoice.JDBC,*/
name|PersistenceAdapterChoice
operator|.
name|MEM
block|}
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|testSendRateWithActivatingConsumers
parameter_list|()
throws|throws
name|Exception
block|{
specifier|final
name|Destination
name|destination
init|=
name|createDestination
argument_list|()
decl_stmt|;
specifier|final
name|ConnectionFactory
name|factory
init|=
name|createConnectionFactory
argument_list|()
decl_stmt|;
name|Connection
name|connection
init|=
name|factory
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
name|MessageProducer
name|producer
init|=
name|createMessageProducer
argument_list|(
name|session
argument_list|,
name|destination
argument_list|)
decl_stmt|;
comment|// preload the queue before adding any consumers
name|double
index|[]
name|noConsumerStats
init|=
name|produceMessages
argument_list|(
name|destination
argument_list|,
name|NUM_MESSAGES
argument_list|,
name|ITERATIONS
argument_list|,
name|session
argument_list|,
name|producer
argument_list|,
literal|null
argument_list|)
decl_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"With no consumers: ave: "
operator|+
name|noConsumerStats
index|[
literal|1
index|]
operator|+
literal|", max: "
operator|+
name|noConsumerStats
index|[
literal|0
index|]
operator|+
literal|", multiplier: "
operator|+
operator|(
name|noConsumerStats
index|[
literal|0
index|]
operator|/
name|noConsumerStats
index|[
literal|1
index|]
operator|)
argument_list|)
expr_stmt|;
name|expectedQueueDeliveries
operator|=
name|NUM_MESSAGES
operator|*
name|ITERATIONS
expr_stmt|;
comment|// periodically start a queue consumer
specifier|final
name|int
name|consumersToActivate
init|=
literal|5
decl_stmt|;
specifier|final
name|Object
name|addConsumerSignal
init|=
operator|new
name|Object
argument_list|()
decl_stmt|;
name|Executors
operator|.
name|newCachedThreadPool
argument_list|(
operator|new
name|ThreadFactory
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|Thread
name|newThread
parameter_list|(
name|Runnable
name|r
parameter_list|)
block|{
return|return
operator|new
name|Thread
argument_list|(
name|r
argument_list|,
literal|"ActivateConsumer"
operator|+
name|this
argument_list|)
return|;
block|}
block|}
argument_list|)
operator|.
name|execute
argument_list|(
operator|new
name|Runnable
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|void
name|run
parameter_list|()
block|{
try|try
block|{
name|MessageConsumer
name|consumer
init|=
literal|null
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
name|consumersToActivate
condition|;
name|i
operator|++
control|)
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"Waiting for add signal from producer..."
argument_list|)
expr_stmt|;
synchronized|synchronized
init|(
name|addConsumerSignal
init|)
block|{
name|addConsumerSignal
operator|.
name|wait
argument_list|(
literal|30
operator|*
literal|60
operator|*
literal|1000
argument_list|)
expr_stmt|;
block|}
name|TimedMessageListener
name|listener
init|=
operator|new
name|TimedMessageListener
argument_list|()
decl_stmt|;
name|consumer
operator|=
name|createConsumer
argument_list|(
name|factory
operator|.
name|createConnection
argument_list|()
argument_list|,
name|destination
argument_list|)
expr_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"Created consumer "
operator|+
name|consumer
argument_list|)
expr_stmt|;
name|consumer
operator|.
name|setMessageListener
argument_list|(
name|listener
argument_list|)
expr_stmt|;
name|consumers
operator|.
name|put
argument_list|(
name|consumer
argument_list|,
name|listener
argument_list|)
expr_stmt|;
block|}
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
name|LOG
operator|.
name|error
argument_list|(
literal|"failed to start consumer"
argument_list|,
name|e
argument_list|)
expr_stmt|;
block|}
block|}
block|}
argument_list|)
expr_stmt|;
comment|// Collect statistics when there are active consumers.
name|double
index|[]
name|statsWithActive
init|=
name|produceMessages
argument_list|(
name|destination
argument_list|,
name|NUM_MESSAGES
argument_list|,
name|ITERATIONS
argument_list|,
name|session
argument_list|,
name|producer
argument_list|,
name|addConsumerSignal
argument_list|)
decl_stmt|;
name|expectedQueueDeliveries
operator|+=
name|NUM_MESSAGES
operator|*
name|ITERATIONS
expr_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|" with concurrent activate, ave: "
operator|+
name|statsWithActive
index|[
literal|1
index|]
operator|+
literal|", max: "
operator|+
name|statsWithActive
index|[
literal|0
index|]
operator|+
literal|", multiplier: "
operator|+
operator|(
name|statsWithActive
index|[
literal|0
index|]
operator|/
name|statsWithActive
index|[
literal|1
index|]
operator|)
argument_list|)
expr_stmt|;
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
name|consumers
operator|.
name|size
argument_list|()
operator|==
name|consumersToActivate
return|;
block|}
block|}
argument_list|)
argument_list|)
expr_stmt|;
name|long
name|timeToFirstAccumulator
init|=
literal|0
decl_stmt|;
for|for
control|(
name|TimedMessageListener
name|listener
range|:
name|consumers
operator|.
name|values
argument_list|()
control|)
block|{
name|long
name|time
init|=
name|listener
operator|.
name|getFirstReceipt
argument_list|()
decl_stmt|;
name|timeToFirstAccumulator
operator|+=
name|time
expr_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"Time to first "
operator|+
name|time
argument_list|)
expr_stmt|;
block|}
name|LOG
operator|.
name|info
argument_list|(
literal|"Ave time to first message ="
operator|+
name|timeToFirstAccumulator
operator|/
name|consumers
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
for|for
control|(
name|TimedMessageListener
name|listener
range|:
name|consumers
operator|.
name|values
argument_list|()
control|)
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"Ave batch receipt time: "
operator|+
name|listener
operator|.
name|waitForReceivedLimit
argument_list|(
name|expectedQueueDeliveries
argument_list|)
operator|+
literal|" max receipt: "
operator|+
name|listener
operator|.
name|maxReceiptTime
argument_list|)
expr_stmt|;
block|}
comment|// compare no active to active
name|LOG
operator|.
name|info
argument_list|(
literal|"Ave send time with active: "
operator|+
name|statsWithActive
index|[
literal|1
index|]
operator|+
literal|" as multiplier of ave with none active: "
operator|+
name|noConsumerStats
index|[
literal|1
index|]
operator|+
literal|", multiplier="
operator|+
operator|(
name|statsWithActive
index|[
literal|1
index|]
operator|/
name|noConsumerStats
index|[
literal|1
index|]
operator|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
literal|"Ave send time with active: "
operator|+
name|statsWithActive
index|[
literal|1
index|]
operator|+
literal|" within reasonable multpler of ave with none active: "
operator|+
name|noConsumerStats
index|[
literal|1
index|]
operator|+
literal|", multiplier "
operator|+
operator|(
name|statsWithActive
index|[
literal|1
index|]
operator|/
name|noConsumerStats
index|[
literal|1
index|]
operator|)
argument_list|,
name|statsWithActive
index|[
literal|1
index|]
operator|<
literal|15
operator|*
name|noConsumerStats
index|[
literal|1
index|]
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|x_initCombosForTestSendWithInactiveAndActiveConsumers
parameter_list|()
throws|throws
name|Exception
block|{
name|addCombinationValues
argument_list|(
literal|"defaultPersistenceAdapter"
argument_list|,
operator|new
name|Object
index|[]
block|{
name|PersistenceAdapterChoice
operator|.
name|KahaDB
block|,
name|PersistenceAdapterChoice
operator|.
name|LevelDB
block|,
comment|/* too slow for hudson - PersistenceAdapterChoice.JDBC,*/
name|PersistenceAdapterChoice
operator|.
name|MEM
block|}
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|x_testSendWithInactiveAndActiveConsumers
parameter_list|()
throws|throws
name|Exception
block|{
name|Destination
name|destination
init|=
name|createDestination
argument_list|()
decl_stmt|;
name|ConnectionFactory
name|factory
init|=
name|createConnectionFactory
argument_list|()
decl_stmt|;
name|Connection
name|connection
init|=
name|factory
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
name|MessageProducer
name|producer
init|=
name|session
operator|.
name|createProducer
argument_list|(
name|destination
argument_list|)
decl_stmt|;
name|producer
operator|.
name|setDeliveryMode
argument_list|(
name|DeliveryMode
operator|.
name|PERSISTENT
argument_list|)
expr_stmt|;
specifier|final
name|int
name|toSend
init|=
literal|100
decl_stmt|;
specifier|final
name|int
name|numIterations
init|=
literal|5
decl_stmt|;
name|double
index|[]
name|noConsumerStats
init|=
name|produceMessages
argument_list|(
name|destination
argument_list|,
name|toSend
argument_list|,
name|numIterations
argument_list|,
name|session
argument_list|,
name|producer
argument_list|,
literal|null
argument_list|)
decl_stmt|;
name|startConsumers
argument_list|(
name|factory
argument_list|,
name|destination
argument_list|)
expr_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"Activated consumer"
argument_list|)
expr_stmt|;
name|double
index|[]
name|withConsumerStats
init|=
name|produceMessages
argument_list|(
name|destination
argument_list|,
name|toSend
argument_list|,
name|numIterations
argument_list|,
name|session
argument_list|,
name|producer
argument_list|,
literal|null
argument_list|)
decl_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"With consumer: "
operator|+
name|withConsumerStats
index|[
literal|1
index|]
operator|+
literal|" , with noConsumer: "
operator|+
name|noConsumerStats
index|[
literal|1
index|]
operator|+
literal|", multiplier: "
operator|+
operator|(
name|withConsumerStats
index|[
literal|1
index|]
operator|/
name|noConsumerStats
index|[
literal|1
index|]
operator|)
argument_list|)
expr_stmt|;
specifier|final
name|int
name|reasonableMultiplier
init|=
literal|15
decl_stmt|;
comment|// not so reasonable but improving
name|assertTrue
argument_list|(
literal|"max X times as slow with consumer: "
operator|+
name|withConsumerStats
index|[
literal|1
index|]
operator|+
literal|", with no Consumer: "
operator|+
name|noConsumerStats
index|[
literal|1
index|]
operator|+
literal|", multiplier: "
operator|+
operator|(
name|withConsumerStats
index|[
literal|1
index|]
operator|/
name|noConsumerStats
index|[
literal|1
index|]
operator|)
argument_list|,
name|withConsumerStats
index|[
literal|1
index|]
operator|<
name|noConsumerStats
index|[
literal|1
index|]
operator|*
name|reasonableMultiplier
argument_list|)
expr_stmt|;
specifier|final
name|int
name|toReceive
init|=
name|toSend
operator|*
name|numIterations
operator|*
name|consumerCount
operator|*
literal|2
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
name|LOG
operator|.
name|info
argument_list|(
literal|"count: "
operator|+
name|allMessagesList
operator|.
name|getMessageCount
argument_list|()
argument_list|)
expr_stmt|;
return|return
name|toReceive
operator|==
name|allMessagesList
operator|.
name|getMessageCount
argument_list|()
return|;
block|}
block|}
argument_list|,
literal|60
operator|*
literal|1000
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"got all messages"
argument_list|,
name|toReceive
argument_list|,
name|allMessagesList
operator|.
name|getMessageCount
argument_list|()
argument_list|)
expr_stmt|;
block|}
specifier|private
name|MessageProducer
name|createMessageProducer
parameter_list|(
name|Session
name|session
parameter_list|,
name|Destination
name|destination
parameter_list|)
throws|throws
name|JMSException
block|{
name|MessageProducer
name|producer
init|=
name|session
operator|.
name|createProducer
argument_list|(
name|destination
argument_list|)
decl_stmt|;
name|producer
operator|.
name|setDeliveryMode
argument_list|(
name|DeliveryMode
operator|.
name|PERSISTENT
argument_list|)
expr_stmt|;
return|return
name|producer
return|;
block|}
specifier|protected
name|void
name|startConsumers
parameter_list|(
name|ConnectionFactory
name|factory
parameter_list|,
name|Destination
name|dest
parameter_list|)
throws|throws
name|Exception
block|{
name|MessageConsumer
name|consumer
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
name|consumerCount
condition|;
name|i
operator|++
control|)
block|{
name|TimedMessageListener
name|list
init|=
operator|new
name|TimedMessageListener
argument_list|()
decl_stmt|;
name|consumer
operator|=
name|createConsumer
argument_list|(
name|factory
operator|.
name|createConnection
argument_list|()
argument_list|,
name|dest
argument_list|)
expr_stmt|;
name|consumer
operator|.
name|setMessageListener
argument_list|(
name|list
argument_list|)
expr_stmt|;
name|consumers
operator|.
name|put
argument_list|(
name|consumer
argument_list|,
name|list
argument_list|)
expr_stmt|;
block|}
block|}
specifier|protected
name|MessageConsumer
name|createConsumer
parameter_list|(
name|Connection
name|conn
parameter_list|,
name|Destination
name|dest
parameter_list|)
throws|throws
name|Exception
block|{
name|connections
operator|.
name|add
argument_list|(
name|conn
argument_list|)
expr_stmt|;
name|conn
operator|.
name|start
argument_list|()
expr_stmt|;
name|Session
name|sess
init|=
name|conn
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
name|MessageConsumer
name|consumer
init|=
name|sess
operator|.
name|createConsumer
argument_list|(
name|dest
argument_list|)
decl_stmt|;
return|return
name|consumer
return|;
block|}
comment|/**      * @return max and average send time      * @throws Exception      */
specifier|private
name|double
index|[]
name|produceMessages
parameter_list|(
name|Destination
name|destination
parameter_list|,
specifier|final
name|int
name|toSend
parameter_list|,
specifier|final
name|int
name|numIterations
parameter_list|,
name|Session
name|session
parameter_list|,
name|MessageProducer
name|producer
parameter_list|,
name|Object
name|addConsumerSignal
parameter_list|)
throws|throws
name|Exception
block|{
name|long
name|start
decl_stmt|;
name|long
name|count
init|=
literal|0
decl_stmt|;
name|double
name|batchMax
init|=
literal|0
decl_stmt|,
name|max
init|=
literal|0
decl_stmt|,
name|sum
init|=
literal|0
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
name|numIterations
condition|;
name|i
operator|++
control|)
block|{
name|start
operator|=
name|System
operator|.
name|currentTimeMillis
argument_list|()
expr_stmt|;
for|for
control|(
name|int
name|j
init|=
literal|0
init|;
name|j
operator|<
name|toSend
condition|;
name|j
operator|++
control|)
block|{
name|long
name|singleSendstart
init|=
name|System
operator|.
name|currentTimeMillis
argument_list|()
decl_stmt|;
name|TextMessage
name|msg
init|=
name|createTextMessage
argument_list|(
name|session
argument_list|,
literal|""
operator|+
name|j
argument_list|)
decl_stmt|;
comment|// rotate
name|int
name|priority
init|=
operator|(
operator|(
name|int
operator|)
name|count
operator|%
literal|10
operator|)
decl_stmt|;
name|producer
operator|.
name|send
argument_list|(
name|msg
argument_list|,
name|DeliveryMode
operator|.
name|PERSISTENT
argument_list|,
name|priority
argument_list|,
literal|0
argument_list|)
expr_stmt|;
name|max
operator|=
name|Math
operator|.
name|max
argument_list|(
name|max
argument_list|,
operator|(
name|System
operator|.
name|currentTimeMillis
argument_list|()
operator|-
name|singleSendstart
operator|)
argument_list|)
expr_stmt|;
if|if
condition|(
operator|++
name|count
operator|%
literal|500
operator|==
literal|0
condition|)
block|{
if|if
condition|(
name|addConsumerSignal
operator|!=
literal|null
condition|)
block|{
synchronized|synchronized
init|(
name|addConsumerSignal
init|)
block|{
name|addConsumerSignal
operator|.
name|notifyAll
argument_list|()
expr_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"Signalled add consumer"
argument_list|)
expr_stmt|;
block|}
block|}
block|}
empty_stmt|;
if|if
condition|(
name|count
operator|%
literal|5000
operator|==
literal|0
condition|)
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"Sent "
operator|+
name|count
operator|+
literal|", singleSendMax:"
operator|+
name|max
argument_list|)
expr_stmt|;
block|}
block|}
name|long
name|duration
init|=
name|System
operator|.
name|currentTimeMillis
argument_list|()
operator|-
name|start
decl_stmt|;
name|batchMax
operator|=
name|Math
operator|.
name|max
argument_list|(
name|batchMax
argument_list|,
name|duration
argument_list|)
expr_stmt|;
name|sum
operator|+=
name|duration
expr_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"Iteration "
operator|+
name|i
operator|+
literal|", sent "
operator|+
name|toSend
operator|+
literal|", time: "
operator|+
name|duration
operator|+
literal|", batchMax:"
operator|+
name|batchMax
operator|+
literal|", singleSendMax:"
operator|+
name|max
argument_list|)
expr_stmt|;
block|}
name|LOG
operator|.
name|info
argument_list|(
literal|"Sent: "
operator|+
name|toSend
operator|*
name|numIterations
operator|+
literal|", batchMax: "
operator|+
name|batchMax
operator|+
literal|" singleSendMax: "
operator|+
name|max
argument_list|)
expr_stmt|;
return|return
operator|new
name|double
index|[]
block|{
name|batchMax
block|,
name|sum
operator|/
name|numIterations
block|}
return|;
block|}
specifier|protected
name|TextMessage
name|createTextMessage
parameter_list|(
name|Session
name|session
parameter_list|,
name|String
name|initText
parameter_list|)
throws|throws
name|Exception
block|{
name|TextMessage
name|msg
init|=
name|session
operator|.
name|createTextMessage
argument_list|()
decl_stmt|;
comment|// Pad message text
if|if
condition|(
name|initText
operator|.
name|length
argument_list|()
operator|<
name|messageSize
condition|)
block|{
name|char
index|[]
name|data
init|=
operator|new
name|char
index|[
name|messageSize
operator|-
name|initText
operator|.
name|length
argument_list|()
index|]
decl_stmt|;
name|Arrays
operator|.
name|fill
argument_list|(
name|data
argument_list|,
literal|'*'
argument_list|)
expr_stmt|;
name|String
name|str
init|=
operator|new
name|String
argument_list|(
name|data
argument_list|)
decl_stmt|;
name|msg
operator|.
name|setText
argument_list|(
name|initText
operator|+
name|str
argument_list|)
expr_stmt|;
comment|// Do not pad message text
block|}
else|else
block|{
name|msg
operator|.
name|setText
argument_list|(
name|initText
argument_list|)
expr_stmt|;
block|}
return|return
name|msg
return|;
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
name|topic
operator|=
literal|false
expr_stmt|;
name|super
operator|.
name|setUp
argument_list|()
expr_stmt|;
name|broker
operator|=
name|createBroker
argument_list|()
expr_stmt|;
name|broker
operator|.
name|start
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
for|for
control|(
name|Iterator
argument_list|<
name|Connection
argument_list|>
name|iter
init|=
name|connections
operator|.
name|iterator
argument_list|()
init|;
name|iter
operator|.
name|hasNext
argument_list|()
condition|;
control|)
block|{
name|Connection
name|conn
init|=
name|iter
operator|.
name|next
argument_list|()
decl_stmt|;
try|try
block|{
name|conn
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Throwable
name|e
parameter_list|)
block|{             }
block|}
name|broker
operator|.
name|stop
argument_list|()
expr_stmt|;
name|allMessagesList
operator|.
name|flushMessages
argument_list|()
expr_stmt|;
name|consumers
operator|.
name|clear
argument_list|()
expr_stmt|;
name|super
operator|.
name|tearDown
argument_list|()
expr_stmt|;
block|}
specifier|protected
name|BrokerService
name|createBroker
parameter_list|()
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
name|setEnableStatistics
argument_list|(
literal|false
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
name|setDeleteAllMessagesOnStartup
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|PolicyEntry
name|policy
init|=
operator|new
name|PolicyEntry
argument_list|()
decl_stmt|;
name|policy
operator|.
name|setPrioritizedMessages
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|policy
operator|.
name|setMaxPageSize
argument_list|(
literal|500
argument_list|)
expr_stmt|;
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
name|policy
argument_list|)
expr_stmt|;
name|brokerService
operator|.
name|setDestinationPolicy
argument_list|(
name|policyMap
argument_list|)
expr_stmt|;
name|setDefaultPersistenceAdapter
argument_list|(
name|brokerService
argument_list|)
expr_stmt|;
return|return
name|brokerService
return|;
block|}
specifier|protected
name|ActiveMQConnectionFactory
name|createConnectionFactory
parameter_list|()
throws|throws
name|Exception
block|{
name|ActiveMQConnectionFactory
name|factory
init|=
operator|new
name|ActiveMQConnectionFactory
argument_list|(
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
name|getPublishableConnectString
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
literal|1
argument_list|)
expr_stmt|;
name|factory
operator|.
name|setPrefetchPolicy
argument_list|(
name|prefetchPolicy
argument_list|)
expr_stmt|;
name|factory
operator|.
name|setDispatchAsync
argument_list|(
literal|true
argument_list|)
expr_stmt|;
return|return
name|factory
return|;
block|}
specifier|public
specifier|static
name|Test
name|suite
parameter_list|()
block|{
return|return
name|suite
argument_list|(
name|ConcurrentProducerQueueConsumerTest
operator|.
name|class
argument_list|)
return|;
block|}
specifier|static
class|class
name|TimedMessageListener
implements|implements
name|MessageListener
block|{
specifier|static
specifier|final
name|AtomicLong
name|count
init|=
operator|new
name|AtomicLong
argument_list|(
literal|0
argument_list|)
decl_stmt|;
specifier|final
name|int
name|batchSize
init|=
literal|1000
decl_stmt|;
specifier|final
name|CountDownLatch
name|firstReceiptLatch
init|=
operator|new
name|CountDownLatch
argument_list|(
literal|1
argument_list|)
decl_stmt|;
name|long
name|mark
init|=
name|System
operator|.
name|currentTimeMillis
argument_list|()
decl_stmt|;
name|long
name|firstReceipt
init|=
literal|0l
decl_stmt|;
name|long
name|receiptAccumulator
init|=
literal|0
decl_stmt|;
name|long
name|batchReceiptAccumulator
init|=
literal|0
decl_stmt|;
name|long
name|maxReceiptTime
init|=
literal|0
decl_stmt|;
specifier|final
name|Map
argument_list|<
name|Integer
argument_list|,
name|MessageIdList
argument_list|>
name|messageLists
init|=
operator|new
name|ConcurrentHashMap
argument_list|<
name|Integer
argument_list|,
name|MessageIdList
argument_list|>
argument_list|(
operator|new
name|HashMap
argument_list|<
name|Integer
argument_list|,
name|MessageIdList
argument_list|>
argument_list|()
argument_list|)
decl_stmt|;
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
specifier|final
name|long
name|current
init|=
name|System
operator|.
name|currentTimeMillis
argument_list|()
decl_stmt|;
specifier|final
name|long
name|duration
init|=
name|current
operator|-
name|mark
decl_stmt|;
name|receiptAccumulator
operator|+=
name|duration
expr_stmt|;
name|int
name|priority
init|=
literal|0
decl_stmt|;
try|try
block|{
name|priority
operator|=
name|message
operator|.
name|getJMSPriority
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|JMSException
name|ignored
parameter_list|)
block|{}
if|if
condition|(
operator|!
name|messageLists
operator|.
name|containsKey
argument_list|(
name|priority
argument_list|)
condition|)
block|{
name|messageLists
operator|.
name|put
argument_list|(
name|priority
argument_list|,
operator|new
name|MessageIdList
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|messageLists
operator|.
name|get
argument_list|(
name|priority
argument_list|)
operator|.
name|onMessage
argument_list|(
name|message
argument_list|)
expr_stmt|;
if|if
condition|(
name|count
operator|.
name|incrementAndGet
argument_list|()
operator|==
literal|1
condition|)
block|{
name|firstReceipt
operator|=
name|duration
expr_stmt|;
name|firstReceiptLatch
operator|.
name|countDown
argument_list|()
expr_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"First receipt in "
operator|+
name|firstReceipt
operator|+
literal|"ms"
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|count
operator|.
name|get
argument_list|()
operator|%
name|batchSize
operator|==
literal|0
condition|)
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"Consumed "
operator|+
name|count
operator|.
name|get
argument_list|()
operator|+
literal|" in "
operator|+
name|batchReceiptAccumulator
operator|+
literal|"ms"
operator|+
literal|", priority:"
operator|+
name|priority
argument_list|)
expr_stmt|;
name|batchReceiptAccumulator
operator|=
literal|0
expr_stmt|;
block|}
name|maxReceiptTime
operator|=
name|Math
operator|.
name|max
argument_list|(
name|maxReceiptTime
argument_list|,
name|duration
argument_list|)
expr_stmt|;
name|receiptAccumulator
operator|+=
name|duration
expr_stmt|;
name|batchReceiptAccumulator
operator|+=
name|duration
expr_stmt|;
name|mark
operator|=
name|current
expr_stmt|;
block|}
name|long
name|getMessageCount
parameter_list|()
block|{
return|return
name|count
operator|.
name|get
argument_list|()
return|;
block|}
name|long
name|getFirstReceipt
parameter_list|()
throws|throws
name|Exception
block|{
name|firstReceiptLatch
operator|.
name|await
argument_list|(
literal|30
argument_list|,
name|TimeUnit
operator|.
name|SECONDS
argument_list|)
expr_stmt|;
return|return
name|firstReceipt
return|;
block|}
specifier|public
name|long
name|waitForReceivedLimit
parameter_list|(
name|long
name|limit
parameter_list|)
throws|throws
name|Exception
block|{
specifier|final
name|long
name|expiry
init|=
name|System
operator|.
name|currentTimeMillis
argument_list|()
operator|+
literal|30
operator|*
literal|60
operator|*
literal|1000
decl_stmt|;
while|while
condition|(
name|count
operator|.
name|get
argument_list|()
operator|<
name|limit
condition|)
block|{
if|if
condition|(
name|System
operator|.
name|currentTimeMillis
argument_list|()
operator|>
name|expiry
condition|)
block|{
throw|throw
operator|new
name|RuntimeException
argument_list|(
literal|"Expired waiting for X messages, "
operator|+
name|limit
argument_list|)
throw|;
block|}
name|TimeUnit
operator|.
name|SECONDS
operator|.
name|sleep
argument_list|(
literal|2
argument_list|)
expr_stmt|;
name|String
name|missing
init|=
name|findFirstMissingMessage
argument_list|()
decl_stmt|;
if|if
condition|(
name|missing
operator|!=
literal|null
condition|)
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"first missing = "
operator|+
name|missing
argument_list|)
expr_stmt|;
throw|throw
operator|new
name|RuntimeException
argument_list|(
literal|"We have a missing message. "
operator|+
name|missing
argument_list|)
throw|;
block|}
block|}
return|return
name|receiptAccumulator
operator|/
operator|(
name|limit
operator|/
name|batchSize
operator|)
return|;
block|}
specifier|private
name|String
name|findFirstMissingMessage
parameter_list|()
block|{
return|return
literal|null
return|;
block|}
block|}
block|}
end_class

end_unit

