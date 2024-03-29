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
name|Random
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
name|ActiveMQQueue
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
name|assertTrue
import|;
end_import

begin_class
annotation|@
name|RunWith
argument_list|(
name|Parameterized
operator|.
name|class
argument_list|)
specifier|public
class|class
name|MessageGroupReconnectDistributionTest
block|{
specifier|public
specifier|static
specifier|final
name|Logger
name|LOG
init|=
name|LoggerFactory
operator|.
name|getLogger
argument_list|(
name|MessageGroupReconnectDistributionTest
operator|.
name|class
argument_list|)
decl_stmt|;
specifier|final
name|Random
name|random
init|=
operator|new
name|Random
argument_list|()
decl_stmt|;
specifier|protected
name|Connection
name|connection
decl_stmt|;
specifier|protected
name|Session
name|session
decl_stmt|;
specifier|protected
name|MessageProducer
name|producer
decl_stmt|;
specifier|protected
name|ActiveMQQueue
name|destination
init|=
operator|new
name|ActiveMQQueue
argument_list|(
literal|"GroupQ"
argument_list|)
decl_stmt|;
specifier|protected
name|TransportConnector
name|connector
decl_stmt|;
name|ActiveMQConnectionFactory
name|connFactory
decl_stmt|;
name|BrokerService
name|broker
decl_stmt|;
name|int
name|numMessages
init|=
literal|10000
decl_stmt|;
name|int
name|groupSize
init|=
literal|10
decl_stmt|;
name|int
name|batchSize
init|=
literal|20
decl_stmt|;
annotation|@
name|Parameterized
operator|.
name|Parameter
argument_list|(
literal|0
argument_list|)
specifier|public
name|int
name|numConsumers
init|=
literal|4
decl_stmt|;
annotation|@
name|Parameterized
operator|.
name|Parameter
argument_list|(
literal|1
argument_list|)
specifier|public
name|boolean
name|consumerPriority
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
literal|"numConsumers={0},consumerPriority={1}"
argument_list|)
specifier|public
specifier|static
name|Iterable
argument_list|<
name|Object
index|[]
argument_list|>
name|combinations
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
literal|4
block|,
literal|true
block|}
block|,
block|{
literal|10
block|,
literal|true
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
name|connFactory
operator|=
operator|new
name|ActiveMQConnectionFactory
argument_list|(
name|connector
operator|.
name|getConnectUri
argument_list|()
operator|+
literal|"?jms.prefetchPolicy.all=200"
argument_list|)
expr_stmt|;
name|connFactory
operator|.
name|setWatchTopicAdvisories
argument_list|(
literal|false
argument_list|)
expr_stmt|;
name|connection
operator|=
name|connFactory
operator|.
name|createConnection
argument_list|()
expr_stmt|;
name|session
operator|=
name|connection
operator|.
name|createSession
argument_list|(
literal|false
argument_list|,
name|Session
operator|.
name|CLIENT_ACKNOWLEDGE
argument_list|)
expr_stmt|;
name|producer
operator|=
name|session
operator|.
name|createProducer
argument_list|(
name|destination
argument_list|)
expr_stmt|;
name|connection
operator|.
name|start
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
name|service
init|=
operator|new
name|BrokerService
argument_list|()
decl_stmt|;
name|service
operator|.
name|setAdvisorySupport
argument_list|(
literal|false
argument_list|)
expr_stmt|;
name|service
operator|.
name|setPersistent
argument_list|(
literal|false
argument_list|)
expr_stmt|;
name|service
operator|.
name|setUseJmx
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|PolicyMap
name|policyMap
init|=
operator|new
name|PolicyMap
argument_list|()
decl_stmt|;
name|PolicyEntry
name|policy
init|=
operator|new
name|PolicyEntry
argument_list|()
decl_stmt|;
name|policy
operator|.
name|setUseConsumerPriority
argument_list|(
name|consumerPriority
argument_list|)
expr_stmt|;
name|policy
operator|.
name|setMessageGroupMapFactoryType
argument_list|(
literal|"cached?cacheSize="
operator|+
operator|(
name|numConsumers
operator|-
literal|1
operator|)
argument_list|)
expr_stmt|;
name|policyMap
operator|.
name|setDefaultEntry
argument_list|(
name|policy
argument_list|)
expr_stmt|;
name|service
operator|.
name|setDestinationPolicy
argument_list|(
name|policyMap
argument_list|)
expr_stmt|;
name|connector
operator|=
name|service
operator|.
name|addConnector
argument_list|(
literal|"tcp://localhost:0"
argument_list|)
expr_stmt|;
return|return
name|service
return|;
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
name|producer
operator|.
name|close
argument_list|()
expr_stmt|;
name|session
operator|.
name|close
argument_list|()
expr_stmt|;
name|connection
operator|.
name|close
argument_list|()
expr_stmt|;
name|broker
operator|.
name|stop
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
argument_list|(
name|timeout
operator|=
literal|5
operator|*
literal|60
operator|*
literal|1000
argument_list|)
specifier|public
name|void
name|testReconnect
parameter_list|()
throws|throws
name|Exception
block|{
specifier|final
name|AtomicLong
name|totalConsumed
init|=
operator|new
name|AtomicLong
argument_list|(
literal|0
argument_list|)
decl_stmt|;
name|ExecutorService
name|executorService
init|=
name|Executors
operator|.
name|newFixedThreadPool
argument_list|(
name|numConsumers
argument_list|)
decl_stmt|;
specifier|final
name|ArrayList
argument_list|<
name|AtomicLong
argument_list|>
name|consumedCounters
init|=
operator|new
name|ArrayList
argument_list|<
name|AtomicLong
argument_list|>
argument_list|(
name|numConsumers
argument_list|)
decl_stmt|;
specifier|final
name|ArrayList
argument_list|<
name|AtomicLong
argument_list|>
name|batchCounters
init|=
operator|new
name|ArrayList
argument_list|<
name|AtomicLong
argument_list|>
argument_list|(
name|numConsumers
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
name|numConsumers
condition|;
name|i
operator|++
control|)
block|{
name|consumedCounters
operator|.
name|add
argument_list|(
operator|new
name|AtomicLong
argument_list|(
literal|0l
argument_list|)
argument_list|)
expr_stmt|;
name|batchCounters
operator|.
name|add
argument_list|(
operator|new
name|AtomicLong
argument_list|(
literal|0l
argument_list|)
argument_list|)
expr_stmt|;
specifier|final
name|int
name|id
init|=
name|i
decl_stmt|;
name|executorService
operator|.
name|submit
argument_list|(
operator|new
name|Runnable
argument_list|()
block|{
name|int
name|getBatchSize
parameter_list|()
block|{
return|return
operator|(
name|id
operator|+
literal|1
operator|)
operator|*
name|batchSize
return|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|run
parameter_list|()
block|{
try|try
block|{
name|Session
name|connectionSession
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
name|int
name|batchSize
init|=
name|getBatchSize
argument_list|()
decl_stmt|;
name|MessageConsumer
name|messageConsumer
init|=
name|connectionSession
operator|.
name|createConsumer
argument_list|(
name|destWithPrefetch
argument_list|(
name|destination
argument_list|)
argument_list|)
decl_stmt|;
name|Message
name|message
decl_stmt|;
name|AtomicLong
name|consumed
init|=
name|consumedCounters
operator|.
name|get
argument_list|(
name|id
argument_list|)
decl_stmt|;
name|AtomicLong
name|batches
init|=
name|batchCounters
operator|.
name|get
argument_list|(
name|id
argument_list|)
decl_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"Consumer: "
operator|+
name|id
operator|+
literal|", batchSize:"
operator|+
name|batchSize
operator|+
literal|", totalConsumed:"
operator|+
name|totalConsumed
operator|.
name|get
argument_list|()
operator|+
literal|", consumed:"
operator|+
name|consumed
operator|.
name|get
argument_list|()
argument_list|)
expr_stmt|;
while|while
condition|(
name|totalConsumed
operator|.
name|get
argument_list|()
operator|<
name|numMessages
condition|)
block|{
name|message
operator|=
name|messageConsumer
operator|.
name|receive
argument_list|(
literal|10000
argument_list|)
expr_stmt|;
if|if
condition|(
name|message
operator|==
literal|null
condition|)
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"Consumer: "
operator|+
name|id
operator|+
literal|", batchSize:"
operator|+
name|batchSize
operator|+
literal|", null message (totalConsumed:"
operator|+
name|totalConsumed
operator|.
name|get
argument_list|()
operator|+
literal|") consumed:"
operator|+
name|consumed
operator|.
name|get
argument_list|()
argument_list|)
expr_stmt|;
name|messageConsumer
operator|.
name|close
argument_list|()
expr_stmt|;
if|if
condition|(
name|totalConsumed
operator|.
name|get
argument_list|()
operator|==
name|numMessages
condition|)
block|{
break|break;
block|}
else|else
block|{
name|batchSize
operator|=
name|getBatchSize
argument_list|()
expr_stmt|;
name|messageConsumer
operator|=
name|connectionSession
operator|.
name|createConsumer
argument_list|(
name|destWithPrefetch
argument_list|(
name|destination
argument_list|)
argument_list|)
expr_stmt|;
name|batches
operator|.
name|incrementAndGet
argument_list|()
expr_stmt|;
continue|continue;
block|}
block|}
name|consumed
operator|.
name|incrementAndGet
argument_list|()
expr_stmt|;
name|totalConsumed
operator|.
name|incrementAndGet
argument_list|()
expr_stmt|;
if|if
condition|(
name|consumed
operator|.
name|get
argument_list|()
operator|>
literal|0
operator|&&
name|consumed
operator|.
name|intValue
argument_list|()
operator|%
name|batchSize
operator|==
literal|0
condition|)
block|{
name|messageConsumer
operator|.
name|close
argument_list|()
expr_stmt|;
name|batchSize
operator|=
name|getBatchSize
argument_list|()
expr_stmt|;
name|messageConsumer
operator|=
name|connectionSession
operator|.
name|createConsumer
argument_list|(
name|destWithPrefetch
argument_list|(
name|destination
argument_list|)
argument_list|)
expr_stmt|;
name|batches
operator|.
name|incrementAndGet
argument_list|()
expr_stmt|;
block|}
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
name|TimeUnit
operator|.
name|MILLISECONDS
operator|.
name|sleep
argument_list|(
literal|200
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
name|produceMessages
argument_list|(
name|numMessages
argument_list|)
expr_stmt|;
name|executorService
operator|.
name|shutdown
argument_list|()
expr_stmt|;
name|assertTrue
argument_list|(
literal|"threads done on time"
argument_list|,
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
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"All consumed"
argument_list|,
name|numMessages
argument_list|,
name|totalConsumed
operator|.
name|intValue
argument_list|()
argument_list|)
expr_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"Distribution: "
operator|+
name|consumedCounters
argument_list|)
expr_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"Batches: "
operator|+
name|batchCounters
argument_list|)
expr_stmt|;
name|double
name|max
init|=
name|consumedCounters
operator|.
name|get
argument_list|(
literal|0
argument_list|)
operator|.
name|longValue
argument_list|()
operator|*
literal|1.5
decl_stmt|;
name|double
name|min
init|=
name|consumedCounters
operator|.
name|get
argument_list|(
literal|0
argument_list|)
operator|.
name|longValue
argument_list|()
operator|*
literal|0.5
decl_stmt|;
for|for
control|(
name|AtomicLong
name|l
range|:
name|consumedCounters
control|)
block|{
name|assertTrue
argument_list|(
literal|"Even +/- 50% distribution on consumed:"
operator|+
name|consumedCounters
operator|+
literal|", outlier:"
operator|+
name|l
operator|.
name|get
argument_list|()
argument_list|,
name|l
operator|.
name|longValue
argument_list|()
operator|<
name|max
operator|&&
name|l
operator|.
name|longValue
argument_list|()
operator|>
name|min
argument_list|)
expr_stmt|;
block|}
block|}
specifier|private
name|Destination
name|destWithPrefetch
parameter_list|(
name|ActiveMQQueue
name|destination
parameter_list|)
throws|throws
name|Exception
block|{
return|return
name|destination
return|;
block|}
specifier|private
name|void
name|produceMessages
parameter_list|(
name|int
name|numMessages
parameter_list|)
throws|throws
name|JMSException
block|{
name|int
name|groupID
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
name|numMessages
condition|;
name|i
operator|++
control|)
block|{
if|if
condition|(
name|i
operator|>
literal|0
operator|&&
name|i
operator|%
name|groupSize
operator|==
literal|0
condition|)
block|{
name|groupID
operator|++
expr_stmt|;
block|}
name|TextMessage
name|msga
init|=
name|session
operator|.
name|createTextMessage
argument_list|(
literal|"hello "
operator|+
name|i
argument_list|)
decl_stmt|;
name|msga
operator|.
name|setStringProperty
argument_list|(
literal|"JMSXGroupID"
argument_list|,
literal|"Group-"
operator|+
name|groupID
argument_list|)
expr_stmt|;
name|producer
operator|.
name|send
argument_list|(
name|msga
argument_list|)
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

