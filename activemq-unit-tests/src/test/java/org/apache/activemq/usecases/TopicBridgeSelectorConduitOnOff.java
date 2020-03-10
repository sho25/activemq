begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  * http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
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
name|Topic
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
name|javax
operator|.
name|jms
operator|.
name|*
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
name|Collection
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
name|LinkedList
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

begin_comment
comment|// demonstrate the use of conduit=true/false on a network bridge for topics with selectors
end_comment

begin_comment
comment|// note selectors are ignored when conduit=true
end_comment

begin_class
specifier|public
class|class
name|TopicBridgeSelectorConduitOnOff
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
name|TopicBridgeSelectorConduitOnOff
operator|.
name|class
argument_list|)
decl_stmt|;
name|BrokerService
name|brokerA
decl_stmt|,
name|brokerB
decl_stmt|;
specifier|final
name|int
name|numProducers
init|=
literal|20
decl_stmt|;
specifier|final
name|int
name|numConsumers
init|=
literal|20
decl_stmt|;
specifier|final
name|int
name|numberOfMessagesToSendPerProducer
init|=
literal|5000
decl_stmt|;
specifier|final
name|ActiveMQTopic
name|destination
init|=
operator|new
name|ActiveMQTopic
argument_list|(
literal|"TOPIC"
argument_list|)
decl_stmt|;
annotation|@
name|After
specifier|public
name|void
name|stopBrokers
parameter_list|()
throws|throws
name|Exception
block|{
name|brokerA
operator|.
name|stop
argument_list|()
expr_stmt|;
name|brokerB
operator|.
name|stop
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testForwardsWithConduitSubsTrue
parameter_list|()
throws|throws
name|Exception
block|{
name|doTestWithConduit
argument_list|(
literal|true
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testForwardsWithConduitSubsFalse
parameter_list|()
throws|throws
name|Exception
block|{
name|doTestWithConduit
argument_list|(
literal|false
argument_list|)
expr_stmt|;
block|}
specifier|private
name|void
name|doTestWithConduit
parameter_list|(
name|boolean
name|conduitPlease
parameter_list|)
throws|throws
name|Exception
block|{
name|brokerA
operator|=
name|newBroker
argument_list|(
literal|"A"
argument_list|)
expr_stmt|;
name|brokerB
operator|=
name|newBroker
argument_list|(
literal|"B"
argument_list|)
expr_stmt|;
name|brokerB
operator|.
name|start
argument_list|()
expr_stmt|;
comment|// bridge
name|NetworkConnector
name|networkConnector
init|=
name|bridgeBrokers
argument_list|(
name|brokerA
argument_list|,
name|brokerB
argument_list|,
name|conduitPlease
argument_list|)
decl_stmt|;
name|brokerA
operator|.
name|start
argument_list|()
expr_stmt|;
comment|// wait for bridge creation
while|while
condition|(
name|networkConnector
operator|.
name|activeBridges
argument_list|()
operator|.
name|size
argument_list|()
operator|==
literal|0
condition|)
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"num bridges: "
operator|+
name|networkConnector
operator|.
name|activeBridges
argument_list|()
argument_list|)
expr_stmt|;
name|TimeUnit
operator|.
name|SECONDS
operator|.
name|sleep
argument_list|(
literal|1
argument_list|)
expr_stmt|;
block|}
comment|// a given consumer selects half the messages
name|CountDownLatch
name|allReceived
init|=
operator|new
name|CountDownLatch
argument_list|(
name|numConsumers
operator|/
literal|2
operator|*
operator|(
name|numberOfMessagesToSendPerProducer
operator|*
name|numProducers
operator|)
argument_list|)
decl_stmt|;
specifier|final
name|ActiveMQConnectionFactory
name|localConnectionFactoryForProducers
init|=
operator|new
name|ActiveMQConnectionFactory
argument_list|(
name|brokerA
operator|.
name|getTransportConnectorByScheme
argument_list|(
literal|"tcp"
argument_list|)
operator|.
name|getPublishableConnectString
argument_list|()
operator|+
literal|"?jms.watchTopicAdvisories=false"
argument_list|)
decl_stmt|;
specifier|final
name|ActiveMQConnectionFactory
name|remoteConnectionFactoryForConsumers
init|=
operator|new
name|ActiveMQConnectionFactory
argument_list|(
name|brokerB
operator|.
name|getTransportConnectorByScheme
argument_list|(
literal|"tcp"
argument_list|)
operator|.
name|getPublishableConnectString
argument_list|()
operator|+
literal|"?jms.watchTopicAdvisories=false"
argument_list|)
decl_stmt|;
name|ExecutorService
name|consumersExecutor
init|=
name|Executors
operator|.
name|newFixedThreadPool
argument_list|(
name|numConsumers
argument_list|)
decl_stmt|;
specifier|final
name|AtomicInteger
name|receivedCount
init|=
operator|new
name|AtomicInteger
argument_list|(
literal|0
argument_list|)
decl_stmt|;
specifier|final
name|Collection
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
name|LinkedList
argument_list|<
name|Connection
argument_list|>
argument_list|()
argument_list|)
decl_stmt|;
specifier|final
name|CountDownLatch
name|consumersRegistered
init|=
operator|new
name|CountDownLatch
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
specifier|final
name|int
name|id
init|=
name|i
decl_stmt|;
name|consumersExecutor
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
name|Connection
name|connection
init|=
name|remoteConnectionFactoryForConsumers
operator|.
name|createConnection
argument_list|()
decl_stmt|;
name|connection
operator|.
name|start
argument_list|()
expr_stmt|;
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
name|MessageConsumer
name|consumerWithSelector
init|=
name|session
operator|.
name|createConsumer
argument_list|(
name|destination
argument_list|,
operator|(
name|id
operator|%
literal|2
operator|==
literal|0
condition|?
literal|"COLOUR = 'RED'"
else|:
literal|"COLOUR = 'BLUE'"
operator|)
argument_list|)
decl_stmt|;
name|consumerWithSelector
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
name|int
name|messageCount
init|=
name|receivedCount
operator|.
name|incrementAndGet
argument_list|()
decl_stmt|;
name|allReceived
operator|.
name|countDown
argument_list|()
expr_stmt|;
if|if
condition|(
name|messageCount
operator|%
literal|20000
operator|==
literal|0
condition|)
block|{
try|try
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"Consumer id: "
operator|+
name|id
operator|+
literal|", message COLOUR:"
operator|+
name|message
operator|.
name|getStringProperty
argument_list|(
literal|"COLOUR"
argument_list|)
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
block|}
argument_list|)
expr_stmt|;
name|connections
operator|.
name|add
argument_list|(
name|connection
argument_list|)
expr_stmt|;
name|consumersRegistered
operator|.
name|countDown
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
argument_list|)
expr_stmt|;
block|}
comment|// need to be sure all subs are active before we publish to have some guaentee of stats
name|consumersRegistered
operator|.
name|await
argument_list|(
literal|20
argument_list|,
name|TimeUnit
operator|.
name|SECONDS
argument_list|)
expr_stmt|;
comment|// would really need to verify on the local broker...
comment|// lets do that
name|Topic
name|topic
init|=
operator|(
name|Topic
operator|)
name|brokerA
operator|.
name|getDestination
argument_list|(
name|destination
argument_list|)
decl_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"Num consumers: "
operator|+
name|topic
operator|.
name|getConsumers
argument_list|()
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
while|while
condition|(
name|topic
operator|.
name|getConsumers
argument_list|()
operator|.
name|size
argument_list|()
operator|!=
operator|(
name|conduitPlease
condition|?
literal|1
else|:
name|numConsumers
operator|)
condition|)
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"Num consumers: "
operator|+
name|topic
operator|.
name|getConsumers
argument_list|()
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|TimeUnit
operator|.
name|SECONDS
operator|.
name|sleep
argument_list|(
literal|1
argument_list|)
expr_stmt|;
block|}
name|ExecutorService
name|producersExecutor
init|=
name|Executors
operator|.
name|newFixedThreadPool
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
name|producersExecutor
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
name|Connection
name|connection
init|=
name|localConnectionFactoryForProducers
operator|.
name|createConnection
argument_list|()
decl_stmt|;
name|connection
operator|.
name|start
argument_list|()
expr_stmt|;
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
name|NON_PERSISTENT
argument_list|)
expr_stmt|;
for|for
control|(
name|int
name|id
init|=
literal|0
init|;
name|id
operator|<
name|numberOfMessagesToSendPerProducer
condition|;
name|id
operator|++
control|)
block|{
specifier|final
name|BytesMessage
name|message
init|=
name|session
operator|.
name|createBytesMessage
argument_list|()
decl_stmt|;
name|message
operator|.
name|setStringProperty
argument_list|(
literal|"COLOUR"
argument_list|,
name|id
operator|%
literal|2
operator|==
literal|0
condition|?
literal|"RED"
else|:
literal|"BLUE"
argument_list|)
expr_stmt|;
name|producer
operator|.
name|send
argument_list|(
name|message
argument_list|)
expr_stmt|;
block|}
name|connection
operator|.
name|close
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
argument_list|)
expr_stmt|;
block|}
name|producersExecutor
operator|.
name|shutdown
argument_list|()
expr_stmt|;
name|producersExecutor
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
specifier|final
name|long
name|start
init|=
name|System
operator|.
name|currentTimeMillis
argument_list|()
decl_stmt|;
comment|// wait for all messages to get delivered to the consumers
comment|// a given consumer selects half the messages
name|allReceived
operator|.
name|await
argument_list|(
literal|5
argument_list|,
name|TimeUnit
operator|.
name|MINUTES
argument_list|)
expr_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"Duration to Receive after producers complete: "
operator|+
operator|(
name|System
operator|.
name|currentTimeMillis
argument_list|()
operator|-
name|start
operator|)
operator|+
literal|"ms"
argument_list|)
expr_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"Topic enqueues: "
operator|+
name|topic
operator|.
name|getDestinationStatistics
argument_list|()
operator|.
name|getEnqueues
argument_list|()
operator|.
name|getCount
argument_list|()
operator|+
literal|", Total received: "
operator|+
name|receivedCount
operator|.
name|get
argument_list|()
operator|+
literal|", forwards: "
operator|+
name|topic
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
name|TimeUnit
operator|.
name|SECONDS
operator|.
name|sleep
argument_list|(
literal|10
argument_list|)
expr_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"Topic enqueues: "
operator|+
name|topic
operator|.
name|getDestinationStatistics
argument_list|()
operator|.
name|getEnqueues
argument_list|()
operator|.
name|getCount
argument_list|()
operator|+
literal|", Total received: "
operator|+
name|receivedCount
operator|.
name|get
argument_list|()
operator|+
literal|", forwards: "
operator|+
name|topic
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
for|for
control|(
name|Connection
name|connection
range|:
name|connections
control|)
block|{
try|try
block|{
name|connection
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
block|{}
block|}
name|consumersExecutor
operator|.
name|shutdown
argument_list|()
expr_stmt|;
name|consumersExecutor
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
block|}
specifier|private
name|BrokerService
name|newBroker
parameter_list|(
name|String
name|name
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
literal|false
argument_list|)
expr_stmt|;
name|brokerService
operator|.
name|setUseJmx
argument_list|(
literal|false
argument_list|)
expr_stmt|;
name|brokerService
operator|.
name|setBrokerName
argument_list|(
name|name
argument_list|)
expr_stmt|;
name|brokerService
operator|.
name|addConnector
argument_list|(
literal|"tcp://0.0.0.0:0"
argument_list|)
expr_stmt|;
name|PolicyMap
name|map
init|=
operator|new
name|PolicyMap
argument_list|()
decl_stmt|;
name|PolicyEntry
name|defaultEntry
init|=
operator|new
name|PolicyEntry
argument_list|()
decl_stmt|;
name|defaultEntry
operator|.
name|setExpireMessagesPeriod
argument_list|(
literal|0
argument_list|)
expr_stmt|;
comment|// the audit defaults to 1k producers and lk messages, but those casn be configured via policy
name|defaultEntry
operator|.
name|setEnableAudit
argument_list|(
literal|true
argument_list|)
expr_stmt|;
comment|// default is true only if there is a policy entry in force, otherwise the sub accepts the duplicates
name|map
operator|.
name|setDefaultEntry
argument_list|(
name|defaultEntry
argument_list|)
expr_stmt|;
name|brokerService
operator|.
name|setDestinationPolicy
argument_list|(
name|map
argument_list|)
expr_stmt|;
return|return
name|brokerService
return|;
block|}
specifier|protected
name|NetworkConnector
name|bridgeBrokers
parameter_list|(
name|BrokerService
name|localBroker
parameter_list|,
name|BrokerService
name|remoteBroker
parameter_list|,
name|boolean
name|conduitPlease
parameter_list|)
throws|throws
name|Exception
block|{
name|String
name|uri
init|=
literal|"static:("
operator|+
name|remoteBroker
operator|.
name|getTransportConnectorByScheme
argument_list|(
literal|"tcp"
argument_list|)
operator|.
name|getPublishableConnectString
argument_list|()
operator|+
literal|")"
decl_stmt|;
name|NetworkConnector
name|connector
init|=
operator|new
name|DiscoveryNetworkConnector
argument_list|(
operator|new
name|URI
argument_list|(
name|uri
argument_list|)
argument_list|)
decl_stmt|;
name|connector
operator|.
name|setName
argument_list|(
name|localBroker
operator|.
name|getBrokerName
argument_list|()
operator|+
literal|"-to-"
operator|+
name|remoteBroker
operator|.
name|getBrokerName
argument_list|()
argument_list|)
expr_stmt|;
name|connector
operator|.
name|setConduitSubscriptions
argument_list|(
name|conduitPlease
argument_list|)
expr_stmt|;
name|localBroker
operator|.
name|addNetworkConnector
argument_list|(
name|connector
argument_list|)
expr_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"Bridging with conduit subs:"
operator|+
name|conduitPlease
argument_list|)
expr_stmt|;
return|return
name|connector
return|;
block|}
block|}
end_class

end_unit
