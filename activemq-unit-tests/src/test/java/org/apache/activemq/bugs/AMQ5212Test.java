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
name|assertNotNull
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|IOException
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
name|ConcurrentLinkedQueue
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
name|ExecutorService
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
name|java
operator|.
name|util
operator|.
name|concurrent
operator|.
name|atomic
operator|.
name|AtomicInteger
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
name|ActiveMQMessageProducer
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
name|MutableBrokerFilter
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
name|ProducerBrokerExchange
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
name|ActiveMQTextMessage
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
name|store
operator|.
name|kahadb
operator|.
name|KahaDBPersistenceAdapter
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
name|AMQ5212Test
block|{
name|BrokerService
name|brokerService
decl_stmt|;
annotation|@
name|Parameterized
operator|.
name|Parameter
argument_list|(
literal|0
argument_list|)
specifier|public
name|boolean
name|concurrentStoreAndDispatchQ
init|=
literal|true
decl_stmt|;
annotation|@
name|Parameterized
operator|.
name|Parameters
argument_list|(
name|name
operator|=
literal|"concurrentStoreAndDispatch={0}"
argument_list|)
specifier|public
specifier|static
name|Iterable
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
annotation|@
name|Before
specifier|public
name|void
name|setUp
parameter_list|()
throws|throws
name|Exception
block|{
name|start
argument_list|(
literal|true
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|start
parameter_list|(
name|boolean
name|deleteAllMessages
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
if|if
condition|(
name|deleteAllMessages
condition|)
block|{
name|brokerService
operator|.
name|deleteAllMessages
argument_list|()
expr_stmt|;
block|}
operator|(
operator|(
name|KahaDBPersistenceAdapter
operator|)
name|brokerService
operator|.
name|getPersistenceAdapter
argument_list|()
operator|)
operator|.
name|setConcurrentStoreAndDispatchQueues
argument_list|(
name|concurrentStoreAndDispatchQ
argument_list|)
expr_stmt|;
name|brokerService
operator|.
name|addConnector
argument_list|(
literal|"tcp://localhost:0"
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
name|getManagementContext
argument_list|()
operator|.
name|setCreateConnector
argument_list|(
literal|false
argument_list|)
expr_stmt|;
name|brokerService
operator|.
name|start
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
name|brokerService
operator|.
name|stop
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|verifyDuplicateSuppressionWithConsumer
parameter_list|()
throws|throws
name|Exception
block|{
name|doVerifyDuplicateSuppression
argument_list|(
literal|100
argument_list|,
literal|100
argument_list|,
literal|true
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|verifyDuplicateSuppression
parameter_list|()
throws|throws
name|Exception
block|{
name|doVerifyDuplicateSuppression
argument_list|(
literal|100
argument_list|,
literal|100
argument_list|,
literal|false
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|doVerifyDuplicateSuppression
parameter_list|(
specifier|final
name|int
name|numToSend
parameter_list|,
specifier|final
name|int
name|expectedTotalEnqueue
parameter_list|,
specifier|final
name|boolean
name|demand
parameter_list|)
throws|throws
name|Exception
block|{
specifier|final
name|ActiveMQConnectionFactory
name|connectionFactory
init|=
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
decl_stmt|;
name|connectionFactory
operator|.
name|setCopyMessageOnSend
argument_list|(
literal|false
argument_list|)
expr_stmt|;
name|connectionFactory
operator|.
name|setWatchTopicAdvisories
argument_list|(
literal|false
argument_list|)
expr_stmt|;
specifier|final
name|int
name|concurrency
init|=
literal|40
decl_stmt|;
specifier|final
name|AtomicInteger
name|workCount
init|=
operator|new
name|AtomicInteger
argument_list|(
name|numToSend
argument_list|)
decl_stmt|;
name|ExecutorService
name|executorService
init|=
name|Executors
operator|.
name|newFixedThreadPool
argument_list|(
name|concurrency
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
name|concurrency
condition|;
name|i
operator|++
control|)
block|{
name|executorService
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
name|int
name|i
decl_stmt|;
while|while
condition|(
operator|(
name|i
operator|=
name|workCount
operator|.
name|getAndDecrement
argument_list|()
operator|)
operator|>
literal|0
condition|)
block|{
name|ActiveMQConnection
name|activeMQConnection
init|=
operator|(
name|ActiveMQConnection
operator|)
name|connectionFactory
operator|.
name|createConnection
argument_list|()
decl_stmt|;
name|activeMQConnection
operator|.
name|start
argument_list|()
expr_stmt|;
name|ActiveMQSession
name|activeMQSession
init|=
operator|(
name|ActiveMQSession
operator|)
name|activeMQConnection
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
name|ActiveMQQueue
name|dest
init|=
operator|new
name|ActiveMQQueue
argument_list|(
literal|"queue-"
operator|+
name|i
operator|+
literal|"-"
operator|+
name|AMQ5212Test
operator|.
name|class
operator|.
name|getSimpleName
argument_list|()
argument_list|)
decl_stmt|;
name|ActiveMQMessageProducer
name|activeMQMessageProducer
init|=
operator|(
name|ActiveMQMessageProducer
operator|)
name|activeMQSession
operator|.
name|createProducer
argument_list|(
name|dest
argument_list|)
decl_stmt|;
if|if
condition|(
name|demand
condition|)
block|{
comment|// create demand so page in will happen
name|activeMQSession
operator|.
name|createConsumer
argument_list|(
name|dest
argument_list|)
expr_stmt|;
block|}
name|ActiveMQTextMessage
name|message
init|=
operator|new
name|ActiveMQTextMessage
argument_list|()
decl_stmt|;
name|message
operator|.
name|setDestination
argument_list|(
name|dest
argument_list|)
expr_stmt|;
name|activeMQMessageProducer
operator|.
name|send
argument_list|(
name|message
argument_list|,
literal|null
argument_list|)
expr_stmt|;
comment|// send a duplicate
name|activeMQConnection
operator|.
name|syncSendPacket
argument_list|(
name|message
argument_list|)
expr_stmt|;
name|activeMQConnection
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
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
block|}
name|TimeUnit
operator|.
name|SECONDS
operator|.
name|sleep
argument_list|(
literal|1
argument_list|)
expr_stmt|;
name|executorService
operator|.
name|shutdown
argument_list|()
expr_stmt|;
name|executorService
operator|.
name|awaitTermination
argument_list|(
literal|5
argument_list|,
name|TimeUnit
operator|.
name|MINUTES
argument_list|)
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
return|return
name|expectedTotalEnqueue
operator|==
name|brokerService
operator|.
name|getAdminView
argument_list|()
operator|.
name|getTotalEnqueueCount
argument_list|()
return|;
block|}
block|}
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"total enqueue as expected"
argument_list|,
name|expectedTotalEnqueue
argument_list|,
name|brokerService
operator|.
name|getAdminView
argument_list|()
operator|.
name|getTotalEnqueueCount
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|verifyConsumptionOnDuplicate
parameter_list|()
throws|throws
name|Exception
block|{
name|ActiveMQConnectionFactory
name|connectionFactory
init|=
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
decl_stmt|;
name|connectionFactory
operator|.
name|setCopyMessageOnSend
argument_list|(
literal|false
argument_list|)
expr_stmt|;
name|connectionFactory
operator|.
name|setWatchTopicAdvisories
argument_list|(
literal|false
argument_list|)
expr_stmt|;
name|ActiveMQConnection
name|activeMQConnection
init|=
operator|(
name|ActiveMQConnection
operator|)
name|connectionFactory
operator|.
name|createConnection
argument_list|()
decl_stmt|;
name|activeMQConnection
operator|.
name|start
argument_list|()
expr_stmt|;
name|ActiveMQSession
name|activeMQSession
init|=
operator|(
name|ActiveMQSession
operator|)
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
name|ActiveMQQueue
name|dest
init|=
operator|new
name|ActiveMQQueue
argument_list|(
literal|"Q"
argument_list|)
decl_stmt|;
name|ActiveMQMessageProducer
name|activeMQMessageProducer
init|=
operator|(
name|ActiveMQMessageProducer
operator|)
name|activeMQSession
operator|.
name|createProducer
argument_list|(
name|dest
argument_list|)
decl_stmt|;
name|ActiveMQTextMessage
name|message
init|=
operator|new
name|ActiveMQTextMessage
argument_list|()
decl_stmt|;
name|message
operator|.
name|setDestination
argument_list|(
name|dest
argument_list|)
expr_stmt|;
name|activeMQMessageProducer
operator|.
name|send
argument_list|(
name|message
argument_list|,
literal|null
argument_list|)
expr_stmt|;
comment|// send a duplicate
name|activeMQConnection
operator|.
name|syncSendPacket
argument_list|(
name|message
argument_list|)
expr_stmt|;
name|activeMQConnection
operator|.
name|close
argument_list|()
expr_stmt|;
comment|// verify original can be consumed after restart
name|brokerService
operator|.
name|stop
argument_list|()
expr_stmt|;
name|brokerService
operator|.
name|start
argument_list|(
literal|false
argument_list|)
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
name|setCopyMessageOnSend
argument_list|(
literal|false
argument_list|)
expr_stmt|;
name|connectionFactory
operator|.
name|setWatchTopicAdvisories
argument_list|(
literal|false
argument_list|)
expr_stmt|;
name|activeMQConnection
operator|=
operator|(
name|ActiveMQConnection
operator|)
name|connectionFactory
operator|.
name|createConnection
argument_list|()
expr_stmt|;
name|activeMQConnection
operator|.
name|start
argument_list|()
expr_stmt|;
name|activeMQSession
operator|=
operator|(
name|ActiveMQSession
operator|)
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
expr_stmt|;
name|MessageConsumer
name|messageConsumer
init|=
name|activeMQSession
operator|.
name|createConsumer
argument_list|(
name|dest
argument_list|)
decl_stmt|;
name|Message
name|received
init|=
name|messageConsumer
operator|.
name|receive
argument_list|(
literal|4000
argument_list|)
decl_stmt|;
name|assertNotNull
argument_list|(
literal|"Got message"
argument_list|,
name|received
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"match"
argument_list|,
name|message
operator|.
name|getJMSMessageID
argument_list|()
argument_list|,
name|received
operator|.
name|getJMSMessageID
argument_list|()
argument_list|)
expr_stmt|;
name|activeMQConnection
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|verifyClientAckConsumptionOnDuplicate
parameter_list|()
throws|throws
name|Exception
block|{
name|ActiveMQConnectionFactory
name|connectionFactory
init|=
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
decl_stmt|;
name|connectionFactory
operator|.
name|setCopyMessageOnSend
argument_list|(
literal|false
argument_list|)
expr_stmt|;
name|connectionFactory
operator|.
name|setWatchTopicAdvisories
argument_list|(
literal|false
argument_list|)
expr_stmt|;
name|ActiveMQConnection
name|activeMQConnection
init|=
operator|(
name|ActiveMQConnection
operator|)
name|connectionFactory
operator|.
name|createConnection
argument_list|()
decl_stmt|;
name|activeMQConnection
operator|.
name|start
argument_list|()
expr_stmt|;
name|ActiveMQSession
name|activeMQSession
init|=
operator|(
name|ActiveMQSession
operator|)
name|activeMQConnection
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
name|ActiveMQQueue
name|dest
init|=
operator|new
name|ActiveMQQueue
argument_list|(
literal|"Q"
argument_list|)
decl_stmt|;
name|MessageConsumer
name|messageConsumer
init|=
name|activeMQSession
operator|.
name|createConsumer
argument_list|(
name|dest
argument_list|)
decl_stmt|;
name|ActiveMQMessageProducer
name|activeMQMessageProducer
init|=
operator|(
name|ActiveMQMessageProducer
operator|)
name|activeMQSession
operator|.
name|createProducer
argument_list|(
name|dest
argument_list|)
decl_stmt|;
name|ActiveMQTextMessage
name|message
init|=
operator|new
name|ActiveMQTextMessage
argument_list|()
decl_stmt|;
name|message
operator|.
name|setDestination
argument_list|(
name|dest
argument_list|)
expr_stmt|;
name|activeMQMessageProducer
operator|.
name|send
argument_list|(
name|message
argument_list|,
literal|null
argument_list|)
expr_stmt|;
comment|// send a duplicate
name|activeMQConnection
operator|.
name|syncSendPacket
argument_list|(
name|message
argument_list|)
expr_stmt|;
name|Message
name|received
init|=
name|messageConsumer
operator|.
name|receive
argument_list|(
literal|4000
argument_list|)
decl_stmt|;
name|assertNotNull
argument_list|(
literal|"Got message"
argument_list|,
name|received
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"match"
argument_list|,
name|message
operator|.
name|getJMSMessageID
argument_list|()
argument_list|,
name|received
operator|.
name|getJMSMessageID
argument_list|()
argument_list|)
expr_stmt|;
name|messageConsumer
operator|.
name|close
argument_list|()
expr_stmt|;
name|messageConsumer
operator|=
name|activeMQSession
operator|.
name|createConsumer
argument_list|(
name|dest
argument_list|)
expr_stmt|;
name|received
operator|=
name|messageConsumer
operator|.
name|receive
argument_list|(
literal|4000
argument_list|)
expr_stmt|;
name|assertNotNull
argument_list|(
literal|"Got message"
argument_list|,
name|received
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"match"
argument_list|,
name|message
operator|.
name|getJMSMessageID
argument_list|()
argument_list|,
name|received
operator|.
name|getJMSMessageID
argument_list|()
argument_list|)
expr_stmt|;
name|received
operator|.
name|acknowledge
argument_list|()
expr_stmt|;
name|activeMQConnection
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|verifyProducerAudit
parameter_list|()
throws|throws
name|Exception
block|{
name|MutableBrokerFilter
name|filter
init|=
operator|(
name|MutableBrokerFilter
operator|)
name|brokerService
operator|.
name|getBroker
argument_list|()
operator|.
name|getAdaptor
argument_list|(
name|MutableBrokerFilter
operator|.
name|class
argument_list|)
decl_stmt|;
name|filter
operator|.
name|setNext
argument_list|(
operator|new
name|MutableBrokerFilter
argument_list|(
name|filter
operator|.
name|getNext
argument_list|()
argument_list|)
block|{
annotation|@
name|Override
specifier|public
name|void
name|send
parameter_list|(
name|ProducerBrokerExchange
name|producerExchange
parameter_list|,
name|org
operator|.
name|apache
operator|.
name|activemq
operator|.
name|command
operator|.
name|Message
name|messageSend
parameter_list|)
throws|throws
name|Exception
block|{
name|super
operator|.
name|send
argument_list|(
name|producerExchange
argument_list|,
name|messageSend
argument_list|)
expr_stmt|;
name|Object
name|seq
init|=
name|messageSend
operator|.
name|getProperty
argument_list|(
literal|"seq"
argument_list|)
decl_stmt|;
if|if
condition|(
name|seq
operator|instanceof
name|Integer
condition|)
block|{
if|if
condition|(
operator|(
operator|(
name|Integer
operator|)
name|seq
operator|)
operator|.
name|intValue
argument_list|()
operator|%
literal|200
operator|==
literal|0
operator|&&
name|producerExchange
operator|.
name|getConnectionContext
argument_list|()
operator|.
name|getConnection
argument_list|()
operator|!=
literal|null
condition|)
block|{
name|producerExchange
operator|.
name|getConnectionContext
argument_list|()
operator|.
name|setDontSendReponse
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|producerExchange
operator|.
name|getConnectionContext
argument_list|()
operator|.
name|getConnection
argument_list|()
operator|.
name|serviceException
argument_list|(
operator|new
name|IOException
argument_list|(
literal|"force reconnect"
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|}
argument_list|)
expr_stmt|;
specifier|final
name|AtomicInteger
name|received
init|=
operator|new
name|AtomicInteger
argument_list|(
literal|0
argument_list|)
decl_stmt|;
specifier|final
name|ActiveMQQueue
name|dest
init|=
operator|new
name|ActiveMQQueue
argument_list|(
literal|"Q"
argument_list|)
decl_stmt|;
specifier|final
name|ActiveMQConnectionFactory
name|connectionFactory
init|=
operator|new
name|ActiveMQConnectionFactory
argument_list|(
literal|"failover://"
operator|+
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
decl_stmt|;
name|connectionFactory
operator|.
name|setCopyMessageOnSend
argument_list|(
literal|false
argument_list|)
expr_stmt|;
name|connectionFactory
operator|.
name|setWatchTopicAdvisories
argument_list|(
literal|false
argument_list|)
expr_stmt|;
specifier|final
name|int
name|numConsumers
init|=
literal|40
decl_stmt|;
name|ExecutorService
name|executorService
init|=
name|Executors
operator|.
name|newCachedThreadPool
argument_list|()
decl_stmt|;
specifier|final
name|CountDownLatch
name|consumerStarted
init|=
operator|new
name|CountDownLatch
argument_list|(
name|numConsumers
argument_list|)
decl_stmt|;
specifier|final
name|ConcurrentLinkedQueue
argument_list|<
name|ActiveMQConnection
argument_list|>
name|connectionList
init|=
operator|new
name|ConcurrentLinkedQueue
argument_list|<
name|ActiveMQConnection
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
name|numConsumers
condition|;
name|i
operator|++
control|)
block|{
name|executorService
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
name|ActiveMQConnection
name|activeMQConnection
init|=
operator|(
name|ActiveMQConnection
operator|)
name|connectionFactory
operator|.
name|createConnection
argument_list|()
decl_stmt|;
name|activeMQConnection
operator|.
name|getPrefetchPolicy
argument_list|()
operator|.
name|setAll
argument_list|(
literal|0
argument_list|)
expr_stmt|;
name|activeMQConnection
operator|.
name|start
argument_list|()
expr_stmt|;
name|connectionList
operator|.
name|add
argument_list|(
name|activeMQConnection
argument_list|)
expr_stmt|;
name|ActiveMQSession
name|activeMQSession
init|=
operator|(
name|ActiveMQSession
operator|)
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
name|MessageConsumer
name|messageConsumer
init|=
name|activeMQSession
operator|.
name|createConsumer
argument_list|(
name|dest
argument_list|)
decl_stmt|;
name|consumerStarted
operator|.
name|countDown
argument_list|()
expr_stmt|;
while|while
condition|(
literal|true
condition|)
block|{
if|if
condition|(
name|messageConsumer
operator|.
name|receive
argument_list|(
literal|500
argument_list|)
operator|!=
literal|null
condition|)
block|{
name|received
operator|.
name|incrementAndGet
argument_list|()
expr_stmt|;
block|}
block|}
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
block|{                     }
catch|catch
parameter_list|(
name|Exception
name|ignored
parameter_list|)
block|{
name|ignored
operator|.
name|printStackTrace
argument_list|()
expr_stmt|;
block|}
block|}
block|}
argument_list|)
expr_stmt|;
block|}
specifier|final
name|String
name|payload
init|=
operator|new
name|String
argument_list|(
operator|new
name|byte
index|[
literal|8
operator|*
literal|1024
index|]
argument_list|)
decl_stmt|;
specifier|final
name|int
name|totalToProduce
init|=
literal|5000
decl_stmt|;
specifier|final
name|AtomicInteger
name|toSend
init|=
operator|new
name|AtomicInteger
argument_list|(
name|totalToProduce
argument_list|)
decl_stmt|;
specifier|final
name|int
name|numProducers
init|=
literal|10
decl_stmt|;
specifier|final
name|CountDownLatch
name|producerDone
init|=
operator|new
name|CountDownLatch
argument_list|(
name|numProducers
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
name|numProducers
condition|;
name|i
operator|++
control|)
block|{
name|executorService
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
name|ActiveMQConnection
name|activeMQConnectionP
init|=
operator|(
name|ActiveMQConnection
operator|)
name|connectionFactory
operator|.
name|createConnection
argument_list|()
decl_stmt|;
name|activeMQConnectionP
operator|.
name|start
argument_list|()
expr_stmt|;
name|ActiveMQSession
name|activeMQSessionP
init|=
operator|(
name|ActiveMQSession
operator|)
name|activeMQConnectionP
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
name|ActiveMQMessageProducer
name|activeMQMessageProducer
init|=
operator|(
name|ActiveMQMessageProducer
operator|)
name|activeMQSessionP
operator|.
name|createProducer
argument_list|(
name|dest
argument_list|)
decl_stmt|;
name|int
name|seq
init|=
literal|0
decl_stmt|;
while|while
condition|(
operator|(
name|seq
operator|=
name|toSend
operator|.
name|decrementAndGet
argument_list|()
operator|)
operator|>=
literal|0
condition|)
block|{
name|ActiveMQTextMessage
name|message
init|=
operator|new
name|ActiveMQTextMessage
argument_list|()
decl_stmt|;
name|message
operator|.
name|setText
argument_list|(
name|payload
argument_list|)
expr_stmt|;
name|message
operator|.
name|setIntProperty
argument_list|(
literal|"seq"
argument_list|,
name|seq
argument_list|)
expr_stmt|;
name|activeMQMessageProducer
operator|.
name|send
argument_list|(
name|message
argument_list|)
expr_stmt|;
block|}
name|activeMQConnectionP
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|ignored
parameter_list|)
block|{
name|ignored
operator|.
name|printStackTrace
argument_list|()
expr_stmt|;
block|}
finally|finally
block|{
name|producerDone
operator|.
name|countDown
argument_list|()
expr_stmt|;
block|}
block|}
block|}
argument_list|)
expr_stmt|;
block|}
name|consumerStarted
operator|.
name|await
argument_list|(
literal|10
argument_list|,
name|TimeUnit
operator|.
name|MINUTES
argument_list|)
expr_stmt|;
name|producerDone
operator|.
name|await
argument_list|(
literal|10
argument_list|,
name|TimeUnit
operator|.
name|MINUTES
argument_list|)
expr_stmt|;
for|for
control|(
name|ActiveMQConnection
name|c
range|:
name|connectionList
control|)
block|{
name|c
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
name|executorService
operator|.
name|shutdown
argument_list|()
expr_stmt|;
name|executorService
operator|.
name|awaitTermination
argument_list|(
literal|10
argument_list|,
name|TimeUnit
operator|.
name|MINUTES
argument_list|)
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
return|return
name|brokerService
operator|.
name|getAdminView
argument_list|()
operator|.
name|getTotalEnqueueCount
argument_list|()
operator|>=
name|totalToProduce
return|;
block|}
block|}
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"total enqueue as expected, nothing added to dlq"
argument_list|,
name|totalToProduce
argument_list|,
name|brokerService
operator|.
name|getAdminView
argument_list|()
operator|.
name|getTotalEnqueueCount
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

