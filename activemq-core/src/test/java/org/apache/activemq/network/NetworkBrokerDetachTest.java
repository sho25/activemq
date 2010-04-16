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
name|assertTrue
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|junit
operator|.
name|Assume
operator|.
name|assumeNotNull
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|File
import|;
end_import

begin_import
import|import
name|java
operator|.
name|net
operator|.
name|MalformedURLException
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Set
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
name|MessageListener
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
name|TopicSubscriber
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|management
operator|.
name|InstanceNotFoundException
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|management
operator|.
name|MBeanServerConnection
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|management
operator|.
name|ObjectName
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|management
operator|.
name|remote
operator|.
name|JMXConnector
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|management
operator|.
name|remote
operator|.
name|JMXConnectorFactory
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|management
operator|.
name|remote
operator|.
name|JMXServiceURL
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
name|apache
operator|.
name|commons
operator|.
name|logging
operator|.
name|Log
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|commons
operator|.
name|logging
operator|.
name|LogFactory
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

begin_class
specifier|public
class|class
name|NetworkBrokerDetachTest
block|{
specifier|private
specifier|final
specifier|static
name|String
name|BROKER_NAME
init|=
literal|"broker"
decl_stmt|;
specifier|private
specifier|final
specifier|static
name|String
name|REM_BROKER_NAME
init|=
literal|"networkedBroker"
decl_stmt|;
specifier|private
specifier|final
specifier|static
name|String
name|DESTINATION_NAME
init|=
literal|"testQ"
decl_stmt|;
specifier|private
specifier|final
specifier|static
name|int
name|NUM_CONSUMERS
init|=
literal|1
decl_stmt|;
specifier|protected
specifier|static
specifier|final
name|Log
name|LOG
init|=
name|LogFactory
operator|.
name|getLog
argument_list|(
name|NetworkBrokerDetachTest
operator|.
name|class
argument_list|)
decl_stmt|;
specifier|protected
specifier|final
name|int
name|numRestarts
init|=
literal|3
decl_stmt|;
specifier|protected
specifier|final
name|int
name|networkTTL
init|=
literal|2
decl_stmt|;
specifier|protected
specifier|final
name|boolean
name|dynamicOnly
init|=
literal|false
decl_stmt|;
specifier|protected
name|BrokerService
name|broker
decl_stmt|;
specifier|protected
name|BrokerService
name|networkedBroker
decl_stmt|;
specifier|protected
name|BrokerService
name|createBroker
parameter_list|()
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
name|setBrokerName
argument_list|(
name|BROKER_NAME
argument_list|)
expr_stmt|;
name|configureBroker
argument_list|(
name|broker
argument_list|)
expr_stmt|;
name|broker
operator|.
name|addConnector
argument_list|(
literal|"tcp://localhost:61617"
argument_list|)
expr_stmt|;
name|NetworkConnector
name|networkConnector
init|=
name|broker
operator|.
name|addNetworkConnector
argument_list|(
literal|"static:(tcp://localhost:62617?wireFormat.maxInactivityDuration=500)?useExponentialBackOff=false"
argument_list|)
decl_stmt|;
name|configureNetworkConnector
argument_list|(
name|networkConnector
argument_list|)
expr_stmt|;
return|return
name|broker
return|;
block|}
specifier|protected
name|BrokerService
name|createNetworkedBroker
parameter_list|()
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
name|setBrokerName
argument_list|(
name|REM_BROKER_NAME
argument_list|)
expr_stmt|;
name|configureBroker
argument_list|(
name|broker
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
name|addConnector
argument_list|(
literal|"tcp://localhost:62617"
argument_list|)
expr_stmt|;
name|NetworkConnector
name|networkConnector
init|=
name|broker
operator|.
name|addNetworkConnector
argument_list|(
literal|"static:(tcp://localhost:61617?wireFormat.maxInactivityDuration=500)?useExponentialBackOff=false"
argument_list|)
decl_stmt|;
name|configureNetworkConnector
argument_list|(
name|networkConnector
argument_list|)
expr_stmt|;
return|return
name|broker
return|;
block|}
specifier|private
name|void
name|configureNetworkConnector
parameter_list|(
name|NetworkConnector
name|networkConnector
parameter_list|)
block|{
name|networkConnector
operator|.
name|setDuplex
argument_list|(
literal|false
argument_list|)
expr_stmt|;
name|networkConnector
operator|.
name|setNetworkTTL
argument_list|(
name|networkTTL
argument_list|)
expr_stmt|;
name|networkConnector
operator|.
name|setDynamicOnly
argument_list|(
name|dynamicOnly
argument_list|)
expr_stmt|;
block|}
comment|// variants for each store....
specifier|protected
name|void
name|configureBroker
parameter_list|(
name|BrokerService
name|broker
parameter_list|)
throws|throws
name|Exception
block|{
comment|//KahaPersistenceAdapter persistenceAdapter = new KahaPersistenceAdapter();
comment|//persistenceAdapter.setDirectory(new File("target/activemq-data/kaha/" + broker.getBrokerName() + "/NetworBrokerDetatchTest"));
comment|//broker.setPersistenceAdapter(persistenceAdapter);
name|KahaDBPersistenceAdapter
name|persistenceAdapter
init|=
operator|new
name|KahaDBPersistenceAdapter
argument_list|()
decl_stmt|;
name|persistenceAdapter
operator|.
name|setDirectory
argument_list|(
operator|new
name|File
argument_list|(
literal|"target/activemq-data/kahadb/"
operator|+
name|broker
operator|.
name|getBrokerName
argument_list|()
operator|+
literal|"NetworBrokerDetatchTest"
argument_list|)
argument_list|)
expr_stmt|;
name|broker
operator|.
name|setPersistenceAdapter
argument_list|(
name|persistenceAdapter
argument_list|)
expr_stmt|;
comment|// default AMQ
block|}
annotation|@
name|Before
specifier|public
name|void
name|init
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
name|networkedBroker
operator|=
name|createNetworkedBroker
argument_list|()
expr_stmt|;
name|networkedBroker
operator|.
name|setDeleteAllMessagesOnStartup
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|networkedBroker
operator|.
name|start
argument_list|()
expr_stmt|;
block|}
annotation|@
name|After
specifier|public
name|void
name|cleanup
parameter_list|()
throws|throws
name|Exception
block|{
name|networkedBroker
operator|.
name|stop
argument_list|()
expr_stmt|;
name|networkedBroker
operator|.
name|waitUntilStopped
argument_list|()
expr_stmt|;
name|broker
operator|.
name|stop
argument_list|()
expr_stmt|;
name|broker
operator|.
name|waitUntilStopped
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testNetworkedBrokerDetach
parameter_list|()
throws|throws
name|Exception
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"Creating Consumer on the networked broker ..."
argument_list|)
expr_stmt|;
comment|// Create a consumer on the networked broker
name|ConnectionFactory
name|consFactory
init|=
name|createConnectionFactory
argument_list|(
name|networkedBroker
argument_list|)
decl_stmt|;
name|Connection
name|consConn
init|=
name|consFactory
operator|.
name|createConnection
argument_list|()
decl_stmt|;
name|Session
name|consSession
init|=
name|consConn
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
name|ActiveMQDestination
name|destination
init|=
operator|(
name|ActiveMQDestination
operator|)
name|consSession
operator|.
name|createQueue
argument_list|(
name|DESTINATION_NAME
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
name|NUM_CONSUMERS
condition|;
name|i
operator|++
control|)
block|{
name|consSession
operator|.
name|createConsumer
argument_list|(
name|destination
argument_list|)
expr_stmt|;
block|}
name|assertTrue
argument_list|(
literal|"got expected consumer count from mbean within time limit"
argument_list|,
name|verifyConsumerCount
argument_list|(
literal|1
argument_list|,
name|destination
argument_list|,
name|BROKER_NAME
argument_list|)
argument_list|)
expr_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"Stopping Consumer on the networked broker ..."
argument_list|)
expr_stmt|;
comment|// Closing the connection will also close the consumer
name|consConn
operator|.
name|close
argument_list|()
expr_stmt|;
comment|// We should have 0 consumer for the queue on the local broker
name|assertTrue
argument_list|(
literal|"got expected 0 count from mbean within time limit"
argument_list|,
name|verifyConsumerCount
argument_list|(
literal|0
argument_list|,
name|destination
argument_list|,
name|BROKER_NAME
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testNetworkedBrokerDurableSubAfterRestart
parameter_list|()
throws|throws
name|Exception
block|{
specifier|final
name|AtomicInteger
name|count
init|=
operator|new
name|AtomicInteger
argument_list|(
literal|0
argument_list|)
decl_stmt|;
name|MessageListener
name|counter
init|=
operator|new
name|MessageListener
argument_list|()
block|{
specifier|public
name|void
name|onMessage
parameter_list|(
name|Message
name|message
parameter_list|)
block|{
name|count
operator|.
name|incrementAndGet
argument_list|()
expr_stmt|;
block|}
block|}
decl_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"Creating durable consumer on each broker ..."
argument_list|)
expr_stmt|;
name|ActiveMQTopic
name|destination
init|=
name|registerDurableConsumer
argument_list|(
name|networkedBroker
argument_list|,
name|counter
argument_list|)
decl_stmt|;
name|registerDurableConsumer
argument_list|(
name|broker
argument_list|,
name|counter
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
literal|"got expected consumer count from local broker mbean within time limit"
argument_list|,
name|verifyConsumerCount
argument_list|(
literal|2
argument_list|,
name|destination
argument_list|,
name|BROKER_NAME
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
literal|"got expected consumer count from network broker mbean within time limit"
argument_list|,
name|verifyConsumerCount
argument_list|(
literal|2
argument_list|,
name|destination
argument_list|,
name|REM_BROKER_NAME
argument_list|)
argument_list|)
expr_stmt|;
name|sendMessageTo
argument_list|(
name|destination
argument_list|,
name|broker
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
literal|"Got one message on each"
argument_list|,
name|verifyMessageCount
argument_list|(
literal|2
argument_list|,
name|count
argument_list|)
argument_list|)
expr_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"Stopping brokerTwo..."
argument_list|)
expr_stmt|;
name|networkedBroker
operator|.
name|stop
argument_list|()
expr_stmt|;
name|networkedBroker
operator|.
name|waitUntilStopped
argument_list|()
expr_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"restarting  broker Two..."
argument_list|)
expr_stmt|;
name|networkedBroker
operator|=
name|createNetworkedBroker
argument_list|()
expr_stmt|;
name|networkedBroker
operator|.
name|start
argument_list|()
expr_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"Recreating durable Consumer on the broker after restart..."
argument_list|)
expr_stmt|;
name|registerDurableConsumer
argument_list|(
name|networkedBroker
argument_list|,
name|counter
argument_list|)
expr_stmt|;
comment|// give advisories a chance to percolate
name|TimeUnit
operator|.
name|SECONDS
operator|.
name|sleep
argument_list|(
literal|5
argument_list|)
expr_stmt|;
name|sendMessageTo
argument_list|(
name|destination
argument_list|,
name|broker
argument_list|)
expr_stmt|;
comment|// expect similar after restart
name|assertTrue
argument_list|(
literal|"got expected consumer count from local broker mbean within time limit"
argument_list|,
name|verifyConsumerCount
argument_list|(
literal|2
argument_list|,
name|destination
argument_list|,
name|BROKER_NAME
argument_list|)
argument_list|)
expr_stmt|;
comment|// a durable sub is auto bridged on restart unless dynamicOnly=true
name|assertTrue
argument_list|(
literal|"got expected consumer count from network broker mbean within time limit"
argument_list|,
name|verifyConsumerCount
argument_list|(
literal|2
argument_list|,
name|destination
argument_list|,
name|REM_BROKER_NAME
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
literal|"got no inactive subs on broker"
argument_list|,
name|verifyDurableConsumerCount
argument_list|(
literal|0
argument_list|,
name|BROKER_NAME
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
literal|"got no inactive subs on other broker"
argument_list|,
name|verifyDurableConsumerCount
argument_list|(
literal|0
argument_list|,
name|REM_BROKER_NAME
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
literal|"Got two more messages after restart"
argument_list|,
name|verifyMessageCount
argument_list|(
literal|4
argument_list|,
name|count
argument_list|)
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
name|assertTrue
argument_list|(
literal|"still Got just two more messages"
argument_list|,
name|verifyMessageCount
argument_list|(
literal|4
argument_list|,
name|count
argument_list|)
argument_list|)
expr_stmt|;
block|}
specifier|private
name|boolean
name|verifyMessageCount
parameter_list|(
specifier|final
name|int
name|i
parameter_list|,
specifier|final
name|AtomicInteger
name|count
parameter_list|)
throws|throws
name|Exception
block|{
return|return
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
return|return
name|i
operator|==
name|count
operator|.
name|get
argument_list|()
return|;
block|}
block|}
argument_list|)
return|;
block|}
specifier|private
name|ActiveMQTopic
name|registerDurableConsumer
parameter_list|(
name|BrokerService
name|brokerService
parameter_list|,
name|MessageListener
name|listener
parameter_list|)
throws|throws
name|Exception
block|{
name|ConnectionFactory
name|factory
init|=
name|createConnectionFactory
argument_list|(
name|brokerService
argument_list|)
decl_stmt|;
name|Connection
name|connection
init|=
name|factory
operator|.
name|createConnection
argument_list|()
decl_stmt|;
name|connection
operator|.
name|setClientID
argument_list|(
literal|"DurableOne"
argument_list|)
expr_stmt|;
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
name|ActiveMQTopic
name|destination
init|=
operator|(
name|ActiveMQTopic
operator|)
name|session
operator|.
name|createTopic
argument_list|(
name|DESTINATION_NAME
argument_list|)
decl_stmt|;
comment|// unique to a broker
name|TopicSubscriber
name|sub
init|=
name|session
operator|.
name|createDurableSubscriber
argument_list|(
name|destination
argument_list|,
literal|"SubOne"
operator|+
name|brokerService
operator|.
name|getBrokerName
argument_list|()
argument_list|)
decl_stmt|;
name|sub
operator|.
name|setMessageListener
argument_list|(
name|listener
argument_list|)
expr_stmt|;
return|return
name|destination
return|;
block|}
specifier|private
name|void
name|sendMessageTo
parameter_list|(
name|ActiveMQTopic
name|destination
parameter_list|,
name|BrokerService
name|brokerService
parameter_list|)
throws|throws
name|Exception
block|{
name|ConnectionFactory
name|factory
init|=
name|createConnectionFactory
argument_list|(
name|brokerService
argument_list|)
decl_stmt|;
name|Connection
name|conn
init|=
name|factory
operator|.
name|createConnection
argument_list|()
decl_stmt|;
name|conn
operator|.
name|start
argument_list|()
expr_stmt|;
name|Session
name|session
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
name|session
operator|.
name|createProducer
argument_list|(
name|destination
argument_list|)
operator|.
name|send
argument_list|(
name|session
operator|.
name|createTextMessage
argument_list|(
literal|"Hi"
argument_list|)
argument_list|)
expr_stmt|;
name|conn
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
specifier|protected
name|ConnectionFactory
name|createConnectionFactory
parameter_list|(
specifier|final
name|BrokerService
name|broker
parameter_list|)
throws|throws
name|Exception
block|{
name|String
name|url
init|=
operator|(
operator|(
name|TransportConnector
operator|)
name|broker
operator|.
name|getTransportConnectors
argument_list|()
operator|.
name|get
argument_list|(
literal|0
argument_list|)
operator|)
operator|.
name|getServer
argument_list|()
operator|.
name|getConnectURI
argument_list|()
operator|.
name|toString
argument_list|()
decl_stmt|;
name|ActiveMQConnectionFactory
name|connectionFactory
init|=
operator|new
name|ActiveMQConnectionFactory
argument_list|(
name|url
argument_list|)
decl_stmt|;
name|connectionFactory
operator|.
name|setOptimizedMessageDispatch
argument_list|(
literal|true
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
name|setUseCompression
argument_list|(
literal|false
argument_list|)
expr_stmt|;
name|connectionFactory
operator|.
name|setDispatchAsync
argument_list|(
literal|false
argument_list|)
expr_stmt|;
name|connectionFactory
operator|.
name|setUseAsyncSend
argument_list|(
literal|false
argument_list|)
expr_stmt|;
name|connectionFactory
operator|.
name|setOptimizeAcknowledge
argument_list|(
literal|false
argument_list|)
expr_stmt|;
name|connectionFactory
operator|.
name|setWatchTopicAdvisories
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|ActiveMQPrefetchPolicy
name|qPrefetchPolicy
init|=
operator|new
name|ActiveMQPrefetchPolicy
argument_list|()
decl_stmt|;
name|qPrefetchPolicy
operator|.
name|setQueuePrefetch
argument_list|(
literal|100
argument_list|)
expr_stmt|;
name|qPrefetchPolicy
operator|.
name|setTopicPrefetch
argument_list|(
literal|1000
argument_list|)
expr_stmt|;
name|connectionFactory
operator|.
name|setPrefetchPolicy
argument_list|(
name|qPrefetchPolicy
argument_list|)
expr_stmt|;
name|connectionFactory
operator|.
name|setAlwaysSyncSend
argument_list|(
literal|true
argument_list|)
expr_stmt|;
return|return
name|connectionFactory
return|;
block|}
comment|// JMX Helper Methods
specifier|private
name|boolean
name|verifyConsumerCount
parameter_list|(
specifier|final
name|long
name|expectedCount
parameter_list|,
specifier|final
name|ActiveMQDestination
name|destination
parameter_list|,
specifier|final
name|String
name|brokerName
parameter_list|)
throws|throws
name|Exception
block|{
return|return
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
name|boolean
name|result
init|=
literal|false
decl_stmt|;
name|MBeanServerConnection
name|mbsc
init|=
name|getMBeanServerConnection
argument_list|()
decl_stmt|;
if|if
condition|(
name|mbsc
operator|!=
literal|null
condition|)
block|{
comment|// We should have 1 consumer for the queue on the local broker
name|Object
name|consumers
init|=
name|getAttribute
argument_list|(
name|mbsc
argument_list|,
name|brokerName
argument_list|,
name|destination
operator|.
name|isQueue
argument_list|()
condition|?
literal|"Queue"
else|:
literal|"Topic"
argument_list|,
literal|"Destination="
operator|+
name|destination
operator|.
name|getPhysicalName
argument_list|()
argument_list|,
literal|"ConsumerCount"
argument_list|)
decl_stmt|;
if|if
condition|(
name|consumers
operator|!=
literal|null
condition|)
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"Consumers for "
operator|+
name|destination
operator|.
name|getPhysicalName
argument_list|()
operator|+
literal|" on "
operator|+
name|brokerName
operator|+
literal|" : "
operator|+
name|consumers
argument_list|)
expr_stmt|;
if|if
condition|(
name|expectedCount
operator|==
operator|(
operator|(
name|Long
operator|)
name|consumers
operator|)
operator|.
name|longValue
argument_list|()
condition|)
block|{
name|result
operator|=
literal|true
expr_stmt|;
block|}
block|}
block|}
return|return
name|result
return|;
block|}
block|}
argument_list|)
return|;
block|}
specifier|private
name|boolean
name|verifyDurableConsumerCount
parameter_list|(
specifier|final
name|long
name|expectedCount
parameter_list|,
specifier|final
name|String
name|brokerName
parameter_list|)
throws|throws
name|Exception
block|{
return|return
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
name|boolean
name|result
init|=
literal|false
decl_stmt|;
name|MBeanServerConnection
name|mbsc
init|=
name|getMBeanServerConnection
argument_list|()
decl_stmt|;
if|if
condition|(
name|mbsc
operator|!=
literal|null
condition|)
block|{
name|Set
name|subs
init|=
name|getMbeans
argument_list|(
name|mbsc
argument_list|,
name|brokerName
argument_list|,
literal|"Subscription"
argument_list|,
literal|"active=false,*"
argument_list|)
decl_stmt|;
if|if
condition|(
name|subs
operator|!=
literal|null
condition|)
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"inactive durable subs on "
operator|+
name|brokerName
operator|+
literal|" : "
operator|+
name|subs
argument_list|)
expr_stmt|;
if|if
condition|(
name|expectedCount
operator|==
name|subs
operator|.
name|size
argument_list|()
condition|)
block|{
name|result
operator|=
literal|true
expr_stmt|;
block|}
block|}
block|}
return|return
name|result
return|;
block|}
block|}
argument_list|)
return|;
block|}
specifier|private
name|MBeanServerConnection
name|getMBeanServerConnection
parameter_list|()
throws|throws
name|MalformedURLException
block|{
specifier|final
name|JMXServiceURL
name|url
init|=
operator|new
name|JMXServiceURL
argument_list|(
literal|"service:jmx:rmi:///jndi/rmi://localhost:1099/jmxrmi"
argument_list|)
decl_stmt|;
name|MBeanServerConnection
name|mbsc
init|=
literal|null
decl_stmt|;
try|try
block|{
name|JMXConnector
name|jmxc
init|=
name|JMXConnectorFactory
operator|.
name|connect
argument_list|(
name|url
argument_list|,
literal|null
argument_list|)
decl_stmt|;
name|mbsc
operator|=
name|jmxc
operator|.
name|getMBeanServerConnection
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|ignored
parameter_list|)
block|{
name|LOG
operator|.
name|warn
argument_list|(
literal|"getMBeanServer ex: "
operator|+
name|ignored
argument_list|)
expr_stmt|;
block|}
comment|// If port 1099 is in use when the Broker starts, starting the jmx
comment|// connector will fail.  So, if we have no mbsc to query, skip the
comment|// test.
name|assumeNotNull
argument_list|(
name|mbsc
argument_list|)
expr_stmt|;
return|return
name|mbsc
return|;
block|}
specifier|private
name|Set
name|getMbeans
parameter_list|(
name|MBeanServerConnection
name|mbsc
parameter_list|,
name|String
name|brokerName
parameter_list|,
name|String
name|type
parameter_list|,
name|String
name|pattern
parameter_list|)
throws|throws
name|Exception
block|{
name|Set
name|obj
init|=
literal|null
decl_stmt|;
try|try
block|{
name|obj
operator|=
name|mbsc
operator|.
name|queryMBeans
argument_list|(
name|getObjectName
argument_list|(
name|brokerName
argument_list|,
name|type
argument_list|,
name|pattern
argument_list|)
argument_list|,
literal|null
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|InstanceNotFoundException
name|ignored
parameter_list|)
block|{
name|LOG
operator|.
name|warn
argument_list|(
literal|"getAttribute ex: "
operator|+
name|ignored
argument_list|)
expr_stmt|;
block|}
return|return
name|obj
return|;
block|}
specifier|private
name|Object
name|getAttribute
parameter_list|(
name|MBeanServerConnection
name|mbsc
parameter_list|,
name|String
name|brokerName
parameter_list|,
name|String
name|type
parameter_list|,
name|String
name|pattern
parameter_list|,
name|String
name|attrName
parameter_list|)
throws|throws
name|Exception
block|{
name|Object
name|obj
init|=
literal|null
decl_stmt|;
try|try
block|{
name|obj
operator|=
name|mbsc
operator|.
name|getAttribute
argument_list|(
name|getObjectName
argument_list|(
name|brokerName
argument_list|,
name|type
argument_list|,
name|pattern
argument_list|)
argument_list|,
name|attrName
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|InstanceNotFoundException
name|ignored
parameter_list|)
block|{
name|LOG
operator|.
name|warn
argument_list|(
literal|"getAttribute ex: "
operator|+
name|ignored
argument_list|)
expr_stmt|;
block|}
return|return
name|obj
return|;
block|}
specifier|private
name|ObjectName
name|getObjectName
parameter_list|(
name|String
name|brokerName
parameter_list|,
name|String
name|type
parameter_list|,
name|String
name|pattern
parameter_list|)
throws|throws
name|Exception
block|{
name|ObjectName
name|beanName
init|=
operator|new
name|ObjectName
argument_list|(
literal|"org.apache.activemq:BrokerName="
operator|+
name|brokerName
operator|+
literal|",Type="
operator|+
name|type
operator|+
literal|","
operator|+
name|pattern
argument_list|)
decl_stmt|;
return|return
name|beanName
return|;
block|}
block|}
end_class

end_unit

