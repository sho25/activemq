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
name|Collection
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
name|QueueConnection
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|jms
operator|.
name|QueueReceiver
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|jms
operator|.
name|QueueSession
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
name|JmsMultipleBrokersTestSupport
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
name|RegionBroker
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
name|BrokerInfo
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
name|network
operator|.
name|DiscoveryNetworkConnector
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
name|network
operator|.
name|NetworkConnector
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
name|TimeUtils
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
name|AMQ4485NetworkOfXBrokersWithNDestsFanoutTransactionTest
extends|extends
name|JmsMultipleBrokersTestSupport
block|{
specifier|static
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
literal|10
operator|*
literal|1024
index|]
argument_list|)
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
name|AMQ4485NetworkOfXBrokersWithNDestsFanoutTransactionTest
operator|.
name|class
argument_list|)
decl_stmt|;
specifier|final
name|int
name|portBase
init|=
literal|61600
decl_stmt|;
specifier|final
name|int
name|numBrokers
init|=
literal|4
decl_stmt|;
specifier|final
name|int
name|numProducers
init|=
literal|10
decl_stmt|;
specifier|final
name|int
name|numMessages
init|=
literal|800
decl_stmt|;
specifier|final
name|int
name|consumerSleepTime
init|=
literal|20
decl_stmt|;
name|StringBuilder
name|brokersUrl
init|=
operator|new
name|StringBuilder
argument_list|()
decl_stmt|;
name|HashMap
argument_list|<
name|ActiveMQQueue
argument_list|,
name|AtomicInteger
argument_list|>
name|accumulators
init|=
operator|new
name|HashMap
argument_list|<
name|ActiveMQQueue
argument_list|,
name|AtomicInteger
argument_list|>
argument_list|()
decl_stmt|;
specifier|private
name|ArrayList
argument_list|<
name|Throwable
argument_list|>
name|exceptions
init|=
operator|new
name|ArrayList
argument_list|<
name|Throwable
argument_list|>
argument_list|()
decl_stmt|;
specifier|protected
name|void
name|buildUrlList
parameter_list|()
throws|throws
name|Exception
block|{
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|numBrokers
condition|;
name|i
operator|++
control|)
block|{
name|brokersUrl
operator|.
name|append
argument_list|(
literal|"tcp://localhost:"
operator|+
operator|(
name|portBase
operator|+
name|i
operator|)
argument_list|)
expr_stmt|;
if|if
condition|(
name|i
operator|!=
name|numBrokers
operator|-
literal|1
condition|)
block|{
name|brokersUrl
operator|.
name|append
argument_list|(
literal|','
argument_list|)
expr_stmt|;
block|}
block|}
block|}
specifier|protected
name|BrokerService
name|createBroker
parameter_list|(
name|int
name|brokerid
parameter_list|)
throws|throws
name|Exception
block|{
name|BrokerService
name|broker
init|=
operator|new
name|BrokerService
argument_list|()
decl_stmt|;
name|broker
operator|.
name|setPersistent
argument_list|(
literal|true
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
name|getManagementContext
argument_list|()
operator|.
name|setCreateConnector
argument_list|(
literal|false
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
name|setBrokerName
argument_list|(
literal|"B"
operator|+
name|brokerid
argument_list|)
expr_stmt|;
name|broker
operator|.
name|addConnector
argument_list|(
operator|new
name|URI
argument_list|(
literal|"tcp://localhost:"
operator|+
operator|(
name|portBase
operator|+
name|brokerid
operator|)
argument_list|)
argument_list|)
expr_stmt|;
name|addNetworkConnector
argument_list|(
name|broker
argument_list|)
expr_stmt|;
name|broker
operator|.
name|setSchedulePeriodForDestinationPurge
argument_list|(
literal|0
argument_list|)
expr_stmt|;
name|broker
operator|.
name|getSystemUsage
argument_list|()
operator|.
name|setSendFailIfNoSpace
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|broker
operator|.
name|getSystemUsage
argument_list|()
operator|.
name|getMemoryUsage
argument_list|()
operator|.
name|setLimit
argument_list|(
literal|512
operator|*
literal|1024
operator|*
literal|1024
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
name|policyEntry
init|=
operator|new
name|PolicyEntry
argument_list|()
decl_stmt|;
name|policyEntry
operator|.
name|setExpireMessagesPeriod
argument_list|(
literal|0
argument_list|)
expr_stmt|;
name|policyEntry
operator|.
name|setQueuePrefetch
argument_list|(
literal|1000
argument_list|)
expr_stmt|;
name|policyEntry
operator|.
name|setMemoryLimit
argument_list|(
literal|1024
operator|*
literal|1024l
argument_list|)
expr_stmt|;
name|policyEntry
operator|.
name|setOptimizedDispatch
argument_list|(
literal|false
argument_list|)
expr_stmt|;
name|policyEntry
operator|.
name|setProducerFlowControl
argument_list|(
literal|false
argument_list|)
expr_stmt|;
name|policyEntry
operator|.
name|setEnableAudit
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|policyEntry
operator|.
name|setUseCache
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|policyMap
operator|.
name|put
argument_list|(
operator|new
name|ActiveMQQueue
argument_list|(
literal|"GW.>"
argument_list|)
argument_list|,
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
name|KahaDBPersistenceAdapter
name|kahaDBPersistenceAdapter
init|=
operator|(
name|KahaDBPersistenceAdapter
operator|)
name|broker
operator|.
name|getPersistenceAdapter
argument_list|()
decl_stmt|;
name|kahaDBPersistenceAdapter
operator|.
name|setConcurrentStoreAndDispatchQueues
argument_list|(
literal|false
argument_list|)
expr_stmt|;
name|brokers
operator|.
name|put
argument_list|(
name|broker
operator|.
name|getBrokerName
argument_list|()
argument_list|,
operator|new
name|BrokerItem
argument_list|(
name|broker
argument_list|)
argument_list|)
expr_stmt|;
return|return
name|broker
return|;
block|}
specifier|private
name|void
name|addNetworkConnector
parameter_list|(
name|BrokerService
name|broker
parameter_list|)
throws|throws
name|Exception
block|{
name|StringBuilder
name|networkConnectorUrl
init|=
operator|new
name|StringBuilder
argument_list|(
literal|"static:("
argument_list|)
operator|.
name|append
argument_list|(
name|brokersUrl
operator|.
name|toString
argument_list|()
argument_list|)
decl_stmt|;
name|networkConnectorUrl
operator|.
name|append
argument_list|(
literal|')'
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
literal|2
condition|;
name|i
operator|++
control|)
block|{
name|NetworkConnector
name|nc
init|=
operator|new
name|DiscoveryNetworkConnector
argument_list|(
operator|new
name|URI
argument_list|(
name|networkConnectorUrl
operator|.
name|toString
argument_list|()
argument_list|)
argument_list|)
decl_stmt|;
name|nc
operator|.
name|setName
argument_list|(
literal|"Bridge-"
operator|+
name|i
argument_list|)
expr_stmt|;
name|nc
operator|.
name|setNetworkTTL
argument_list|(
literal|1
argument_list|)
expr_stmt|;
name|nc
operator|.
name|setDecreaseNetworkConsumerPriority
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|nc
operator|.
name|setDynamicOnly
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|nc
operator|.
name|setPrefetchSize
argument_list|(
literal|100
argument_list|)
expr_stmt|;
name|nc
operator|.
name|setDynamicallyIncludedDestinations
argument_list|(
name|Arrays
operator|.
name|asList
argument_list|(
operator|new
name|ActiveMQDestination
index|[]
block|{
operator|new
name|ActiveMQQueue
argument_list|(
literal|"GW.*"
argument_list|)
block|}
argument_list|)
argument_list|)
expr_stmt|;
name|broker
operator|.
name|addNetworkConnector
argument_list|(
name|nc
argument_list|)
expr_stmt|;
block|}
block|}
specifier|public
name|void
name|testBrokers
parameter_list|()
throws|throws
name|Exception
block|{
name|buildUrlList
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
name|numBrokers
condition|;
name|i
operator|++
control|)
block|{
name|createBroker
argument_list|(
name|i
argument_list|)
expr_stmt|;
block|}
name|startAllBrokers
argument_list|()
expr_stmt|;
name|waitForBridgeFormation
argument_list|(
name|numBrokers
operator|-
literal|1
argument_list|)
expr_stmt|;
name|verifyPeerBrokerInfos
argument_list|(
name|numBrokers
operator|-
literal|1
argument_list|)
expr_stmt|;
specifier|final
name|List
argument_list|<
name|ConsumerState
argument_list|>
name|consumerStates
init|=
name|startAllGWConsumers
argument_list|(
name|numBrokers
argument_list|)
decl_stmt|;
name|startAllGWFanoutConsumers
argument_list|(
name|numBrokers
argument_list|)
expr_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"Waiting for percolation of consumers.."
argument_list|)
expr_stmt|;
name|TimeUnit
operator|.
name|SECONDS
operator|.
name|sleep
argument_list|(
literal|5
argument_list|)
expr_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"Produce mesages.."
argument_list|)
expr_stmt|;
name|long
name|startTime
init|=
name|System
operator|.
name|currentTimeMillis
argument_list|()
decl_stmt|;
comment|// produce
name|produce
argument_list|(
name|numMessages
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
literal|"Got all sent"
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
for|for
control|(
name|ConsumerState
name|tally
range|:
name|consumerStates
control|)
block|{
specifier|final
name|int
name|expected
init|=
name|numMessages
operator|*
operator|(
name|tally
operator|.
name|destination
operator|.
name|isComposite
argument_list|()
condition|?
name|tally
operator|.
name|destination
operator|.
name|getCompositeDestinations
argument_list|()
operator|.
name|length
else|:
literal|1
operator|)
decl_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"Tally for: "
operator|+
name|tally
operator|.
name|brokerName
operator|+
literal|", dest: "
operator|+
name|tally
operator|.
name|destination
operator|+
literal|" - "
operator|+
name|tally
operator|.
name|accumulator
operator|.
name|get
argument_list|()
argument_list|)
expr_stmt|;
if|if
condition|(
name|tally
operator|.
name|accumulator
operator|.
name|get
argument_list|()
operator|!=
name|expected
condition|)
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"Tally for: "
operator|+
name|tally
operator|.
name|brokerName
operator|+
literal|", dest: "
operator|+
name|tally
operator|.
name|destination
operator|+
literal|" - "
operator|+
name|tally
operator|.
name|accumulator
operator|.
name|get
argument_list|()
operator|+
literal|" != "
operator|+
name|expected
operator|+
literal|", "
operator|+
name|tally
operator|.
name|expected
argument_list|)
expr_stmt|;
return|return
literal|false
return|;
block|}
name|LOG
operator|.
name|info
argument_list|(
literal|"got tally on "
operator|+
name|tally
operator|.
name|brokerName
argument_list|)
expr_stmt|;
block|}
return|return
literal|true
return|;
block|}
block|}
argument_list|,
literal|1000
operator|*
literal|60
operator|*
literal|1000l
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
literal|"No exceptions:"
operator|+
name|exceptions
argument_list|,
name|exceptions
operator|.
name|isEmpty
argument_list|()
argument_list|)
expr_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"done"
argument_list|)
expr_stmt|;
name|long
name|duration
init|=
name|System
operator|.
name|currentTimeMillis
argument_list|()
operator|-
name|startTime
decl_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"Duration:"
operator|+
name|TimeUtils
operator|.
name|printDuration
argument_list|(
name|duration
argument_list|)
argument_list|)
expr_stmt|;
block|}
specifier|private
name|void
name|startAllGWFanoutConsumers
parameter_list|(
name|int
name|nBrokers
parameter_list|)
throws|throws
name|Exception
block|{
name|StringBuffer
name|compositeDest
init|=
operator|new
name|StringBuffer
argument_list|()
decl_stmt|;
for|for
control|(
name|int
name|k
init|=
literal|0
init|;
name|k
operator|<
name|nBrokers
condition|;
name|k
operator|++
control|)
block|{
name|compositeDest
operator|.
name|append
argument_list|(
literal|"GW."
operator|+
name|k
argument_list|)
expr_stmt|;
if|if
condition|(
name|k
operator|+
literal|1
operator|!=
name|nBrokers
condition|)
block|{
name|compositeDest
operator|.
name|append
argument_list|(
literal|','
argument_list|)
expr_stmt|;
block|}
block|}
name|ActiveMQQueue
name|compositeQ
init|=
operator|new
name|ActiveMQQueue
argument_list|(
name|compositeDest
operator|.
name|toString
argument_list|()
argument_list|)
decl_stmt|;
for|for
control|(
name|int
name|id
init|=
literal|0
init|;
name|id
operator|<
name|nBrokers
condition|;
name|id
operator|++
control|)
block|{
name|ActiveMQConnectionFactory
name|connectionFactory
init|=
operator|new
name|ActiveMQConnectionFactory
argument_list|(
literal|"failover:(tcp://localhost:"
operator|+
operator|(
name|portBase
operator|+
name|id
operator|)
operator|+
literal|")"
argument_list|)
decl_stmt|;
name|connectionFactory
operator|.
name|setWatchTopicAdvisories
argument_list|(
literal|false
argument_list|)
expr_stmt|;
name|QueueConnection
name|queueConnection
init|=
name|connectionFactory
operator|.
name|createQueueConnection
argument_list|()
decl_stmt|;
name|queueConnection
operator|.
name|start
argument_list|()
expr_stmt|;
specifier|final
name|QueueSession
name|queueSession
init|=
name|queueConnection
operator|.
name|createQueueSession
argument_list|(
literal|true
argument_list|,
name|Session
operator|.
name|SESSION_TRANSACTED
argument_list|)
decl_stmt|;
specifier|final
name|MessageProducer
name|producer
init|=
name|queueSession
operator|.
name|createProducer
argument_list|(
name|compositeQ
argument_list|)
decl_stmt|;
name|queueSession
operator|.
name|createReceiver
argument_list|(
operator|new
name|ActiveMQQueue
argument_list|(
literal|"IN"
argument_list|)
argument_list|)
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
block|{
try|try
block|{
name|producer
operator|.
name|send
argument_list|(
name|message
argument_list|)
expr_stmt|;
name|queueSession
operator|.
name|commit
argument_list|()
expr_stmt|;
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
literal|"Failed to fanout to GW: "
operator|+
name|message
argument_list|,
name|e
argument_list|)
expr_stmt|;
block|}
block|}
block|}
argument_list|)
expr_stmt|;
block|}
block|}
specifier|private
name|List
argument_list|<
name|ConsumerState
argument_list|>
name|startAllGWConsumers
parameter_list|(
name|int
name|nBrokers
parameter_list|)
throws|throws
name|Exception
block|{
name|List
argument_list|<
name|ConsumerState
argument_list|>
name|consumerStates
init|=
operator|new
name|LinkedList
argument_list|<
name|ConsumerState
argument_list|>
argument_list|()
decl_stmt|;
for|for
control|(
name|int
name|id
init|=
literal|0
init|;
name|id
operator|<
name|nBrokers
condition|;
name|id
operator|++
control|)
block|{
name|ActiveMQConnectionFactory
name|connectionFactory
init|=
operator|new
name|ActiveMQConnectionFactory
argument_list|(
literal|"failover:(tcp://localhost:"
operator|+
operator|(
name|portBase
operator|+
name|id
operator|)
operator|+
literal|")"
argument_list|)
decl_stmt|;
name|connectionFactory
operator|.
name|setWatchTopicAdvisories
argument_list|(
literal|false
argument_list|)
expr_stmt|;
name|QueueConnection
name|queueConnection
init|=
name|connectionFactory
operator|.
name|createQueueConnection
argument_list|()
decl_stmt|;
name|queueConnection
operator|.
name|start
argument_list|()
expr_stmt|;
specifier|final
name|QueueSession
name|queueSession
init|=
name|queueConnection
operator|.
name|createQueueSession
argument_list|(
literal|false
argument_list|,
name|Session
operator|.
name|AUTO_ACKNOWLEDGE
argument_list|)
decl_stmt|;
name|ActiveMQQueue
name|destination
init|=
operator|new
name|ActiveMQQueue
argument_list|(
literal|"GW."
operator|+
name|id
argument_list|)
decl_stmt|;
name|QueueReceiver
name|queueReceiver
init|=
name|queueSession
operator|.
name|createReceiver
argument_list|(
name|destination
argument_list|)
decl_stmt|;
specifier|final
name|ConsumerState
name|consumerState
init|=
operator|new
name|ConsumerState
argument_list|()
decl_stmt|;
name|consumerState
operator|.
name|brokerName
operator|=
operator|(
operator|(
name|ActiveMQConnection
operator|)
name|queueConnection
operator|)
operator|.
name|getBrokerName
argument_list|()
expr_stmt|;
name|consumerState
operator|.
name|receiver
operator|=
name|queueReceiver
expr_stmt|;
name|consumerState
operator|.
name|destination
operator|=
name|destination
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
name|numMessages
operator|*
operator|(
name|consumerState
operator|.
name|destination
operator|.
name|isComposite
argument_list|()
condition|?
name|consumerState
operator|.
name|destination
operator|.
name|getCompositeDestinations
argument_list|()
operator|.
name|length
else|:
literal|1
operator|)
condition|;
name|j
operator|++
control|)
block|{
name|consumerState
operator|.
name|expected
operator|.
name|add
argument_list|(
name|j
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
operator|!
name|accumulators
operator|.
name|containsKey
argument_list|(
name|destination
argument_list|)
condition|)
block|{
name|accumulators
operator|.
name|put
argument_list|(
name|destination
argument_list|,
operator|new
name|AtomicInteger
argument_list|(
literal|0
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|consumerState
operator|.
name|accumulator
operator|=
name|accumulators
operator|.
name|get
argument_list|(
name|destination
argument_list|)
expr_stmt|;
name|queueReceiver
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
block|{
try|try
block|{
if|if
condition|(
name|consumerSleepTime
operator|>
literal|0
condition|)
block|{
name|TimeUnit
operator|.
name|MILLISECONDS
operator|.
name|sleep
argument_list|(
name|consumerSleepTime
argument_list|)
expr_stmt|;
block|}
block|}
catch|catch
parameter_list|(
name|InterruptedException
name|e
parameter_list|)
block|{
name|e
operator|.
name|printStackTrace
argument_list|()
expr_stmt|;
block|}
try|try
block|{
name|consumerState
operator|.
name|accumulator
operator|.
name|incrementAndGet
argument_list|()
expr_stmt|;
try|try
block|{
name|consumerState
operator|.
name|expected
operator|.
name|remove
argument_list|(
operator|(
operator|(
name|ActiveMQMessage
operator|)
name|message
operator|)
operator|.
name|getProperty
argument_list|(
literal|"NUM"
argument_list|)
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
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
literal|"Failed to commit slow receipt of "
operator|+
name|message
argument_list|,
name|e
argument_list|)
expr_stmt|;
block|}
block|}
block|}
argument_list|)
expr_stmt|;
name|consumerStates
operator|.
name|add
argument_list|(
name|consumerState
argument_list|)
expr_stmt|;
block|}
return|return
name|consumerStates
return|;
block|}
specifier|private
name|void
name|produce
parameter_list|(
name|int
name|numMessages
parameter_list|)
throws|throws
name|Exception
block|{
name|ExecutorService
name|executorService
init|=
name|Executors
operator|.
name|newFixedThreadPool
argument_list|(
name|numProducers
argument_list|)
decl_stmt|;
specifier|final
name|AtomicInteger
name|toSend
init|=
operator|new
name|AtomicInteger
argument_list|(
name|numMessages
argument_list|)
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|1
init|;
name|i
operator|<=
name|numProducers
condition|;
name|i
operator|++
control|)
block|{
specifier|final
name|int
name|id
init|=
name|i
operator|%
name|numBrokers
decl_stmt|;
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
name|ActiveMQConnectionFactory
name|connectionFactory
init|=
operator|new
name|ActiveMQConnectionFactory
argument_list|(
literal|"failover:(tcp://localhost:"
operator|+
operator|(
name|portBase
operator|+
name|id
operator|)
operator|+
literal|")"
argument_list|)
decl_stmt|;
name|connectionFactory
operator|.
name|setWatchTopicAdvisories
argument_list|(
literal|false
argument_list|)
expr_stmt|;
name|QueueConnection
name|queueConnection
init|=
name|connectionFactory
operator|.
name|createQueueConnection
argument_list|()
decl_stmt|;
name|queueConnection
operator|.
name|start
argument_list|()
expr_stmt|;
name|QueueSession
name|queueSession
init|=
name|queueConnection
operator|.
name|createQueueSession
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
name|queueSession
operator|.
name|createProducer
argument_list|(
literal|null
argument_list|)
decl_stmt|;
name|int
name|val
init|=
literal|0
decl_stmt|;
while|while
condition|(
operator|(
name|val
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
name|ActiveMQQueue
name|compositeQ
init|=
operator|new
name|ActiveMQQueue
argument_list|(
literal|"IN"
argument_list|)
decl_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"Send to: "
operator|+
operator|(
operator|(
name|ActiveMQConnection
operator|)
name|queueConnection
operator|)
operator|.
name|getBrokerName
argument_list|()
operator|+
literal|", "
operator|+
name|val
operator|+
literal|", dest:"
operator|+
name|compositeQ
argument_list|)
expr_stmt|;
name|Message
name|textMessage
init|=
name|queueSession
operator|.
name|createTextMessage
argument_list|(
operator|(
operator|(
name|ActiveMQConnection
operator|)
name|queueConnection
operator|)
operator|.
name|getBrokerName
argument_list|()
operator|+
literal|"->"
operator|+
name|val
operator|+
literal|" payload:"
operator|+
name|payload
argument_list|)
decl_stmt|;
name|textMessage
operator|.
name|setIntProperty
argument_list|(
literal|"NUM"
argument_list|,
name|val
argument_list|)
expr_stmt|;
name|producer
operator|.
name|send
argument_list|(
name|compositeQ
argument_list|,
name|textMessage
argument_list|)
expr_stmt|;
block|}
name|queueConnection
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Throwable
name|throwable
parameter_list|)
block|{
name|throwable
operator|.
name|printStackTrace
argument_list|()
expr_stmt|;
name|exceptions
operator|.
name|add
argument_list|(
name|throwable
argument_list|)
expr_stmt|;
block|}
block|}
block|}
argument_list|)
expr_stmt|;
block|}
block|}
specifier|private
name|void
name|verifyPeerBrokerInfo
parameter_list|(
name|BrokerItem
name|brokerItem
parameter_list|,
specifier|final
name|int
name|max
parameter_list|)
throws|throws
name|Exception
block|{
specifier|final
name|BrokerService
name|broker
init|=
name|brokerItem
operator|.
name|broker
decl_stmt|;
specifier|final
name|RegionBroker
name|regionBroker
init|=
operator|(
name|RegionBroker
operator|)
name|broker
operator|.
name|getRegionBroker
argument_list|()
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
annotation|@
name|Override
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
literal|"verify infos "
operator|+
name|broker
operator|.
name|getBrokerName
argument_list|()
operator|+
literal|", len: "
operator|+
name|regionBroker
operator|.
name|getPeerBrokerInfos
argument_list|()
operator|.
name|length
argument_list|)
expr_stmt|;
return|return
name|max
operator|==
name|regionBroker
operator|.
name|getPeerBrokerInfos
argument_list|()
operator|.
name|length
return|;
block|}
block|}
argument_list|)
expr_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"verify infos "
operator|+
name|broker
operator|.
name|getBrokerName
argument_list|()
operator|+
literal|", len: "
operator|+
name|regionBroker
operator|.
name|getPeerBrokerInfos
argument_list|()
operator|.
name|length
argument_list|)
expr_stmt|;
name|List
argument_list|<
name|String
argument_list|>
name|missing
init|=
operator|new
name|ArrayList
argument_list|<
name|String
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
name|max
condition|;
name|i
operator|++
control|)
block|{
name|missing
operator|.
name|add
argument_list|(
literal|"B"
operator|+
name|i
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|max
operator|!=
name|regionBroker
operator|.
name|getPeerBrokerInfos
argument_list|()
operator|.
name|length
condition|)
block|{
for|for
control|(
name|BrokerInfo
name|info
range|:
name|regionBroker
operator|.
name|getPeerBrokerInfos
argument_list|()
control|)
block|{
name|LOG
operator|.
name|info
argument_list|(
name|info
operator|.
name|getBrokerName
argument_list|()
argument_list|)
expr_stmt|;
name|missing
operator|.
name|remove
argument_list|(
name|info
operator|.
name|getBrokerName
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|LOG
operator|.
name|info
argument_list|(
literal|"Broker infos off.."
operator|+
name|missing
argument_list|)
expr_stmt|;
block|}
name|assertEquals
argument_list|(
name|broker
operator|.
name|getBrokerName
argument_list|()
argument_list|,
name|max
argument_list|,
name|regionBroker
operator|.
name|getPeerBrokerInfos
argument_list|()
operator|.
name|length
argument_list|)
expr_stmt|;
block|}
specifier|private
name|void
name|verifyPeerBrokerInfos
parameter_list|(
specifier|final
name|int
name|max
parameter_list|)
throws|throws
name|Exception
block|{
name|Collection
argument_list|<
name|BrokerItem
argument_list|>
name|brokerList
init|=
name|brokers
operator|.
name|values
argument_list|()
decl_stmt|;
for|for
control|(
name|Iterator
argument_list|<
name|BrokerItem
argument_list|>
name|i
init|=
name|brokerList
operator|.
name|iterator
argument_list|()
init|;
name|i
operator|.
name|hasNext
argument_list|()
condition|;
control|)
block|{
name|verifyPeerBrokerInfo
argument_list|(
name|i
operator|.
name|next
argument_list|()
argument_list|,
name|max
argument_list|)
expr_stmt|;
block|}
block|}
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
block|}
class|class
name|ConsumerState
block|{
name|AtomicInteger
name|accumulator
decl_stmt|;
name|String
name|brokerName
decl_stmt|;
name|QueueReceiver
name|receiver
decl_stmt|;
name|ActiveMQDestination
name|destination
decl_stmt|;
name|Vector
argument_list|<
name|Integer
argument_list|>
name|expected
init|=
operator|new
name|Vector
argument_list|<
name|Integer
argument_list|>
argument_list|()
decl_stmt|;
block|}
block|}
end_class

end_unit

