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
name|TimeUnit
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
name|management
operator|.
name|ObjectName
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
name|jmx
operator|.
name|BrokerViewMBean
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
name|jmx
operator|.
name|DurableSubscriptionViewMBean
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
name|PersistenceAdapter
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
name|lang
operator|.
name|ArrayUtils
import|;
end_import

begin_import
import|import
name|org
operator|.
name|fusesource
operator|.
name|hawtdispatch
operator|.
name|Dispatch
import|;
end_import

begin_import
import|import
name|org
operator|.
name|fusesource
operator|.
name|mqtt
operator|.
name|client
operator|.
name|BlockingConnection
import|;
end_import

begin_import
import|import
name|org
operator|.
name|fusesource
operator|.
name|mqtt
operator|.
name|client
operator|.
name|MQTT
import|;
end_import

begin_import
import|import
name|org
operator|.
name|fusesource
operator|.
name|mqtt
operator|.
name|client
operator|.
name|QoS
import|;
end_import

begin_import
import|import
name|org
operator|.
name|fusesource
operator|.
name|mqtt
operator|.
name|client
operator|.
name|Topic
import|;
end_import

begin_import
import|import
name|org
operator|.
name|fusesource
operator|.
name|mqtt
operator|.
name|client
operator|.
name|Tracer
import|;
end_import

begin_import
import|import
name|org
operator|.
name|fusesource
operator|.
name|mqtt
operator|.
name|codec
operator|.
name|MQTTFrame
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

begin_comment
comment|/**  * Created by ceposta  *<a href="http://christianposta.com/blog>http://christianposta.com/blog</a>.  */
end_comment

begin_class
specifier|public
class|class
name|MQTTNetworkOfBrokersFailoverTest
extends|extends
name|NetworkTestSupport
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
name|MQTTNetworkOfBrokersFailoverTest
operator|.
name|class
argument_list|)
decl_stmt|;
specifier|private
name|int
name|localBrokerMQTTPort
init|=
operator|-
literal|1
decl_stmt|;
specifier|private
name|int
name|remoteBrokerMQTTPort
init|=
operator|-
literal|1
decl_stmt|;
annotation|@
name|Override
specifier|protected
name|void
name|setUp
parameter_list|()
throws|throws
name|Exception
block|{
name|useJmx
operator|=
literal|true
expr_stmt|;
name|super
operator|.
name|setUp
argument_list|()
expr_stmt|;
name|URI
name|ncUri
init|=
operator|new
name|URI
argument_list|(
literal|"static:("
operator|+
name|connector
operator|.
name|getConnectUri
argument_list|()
operator|.
name|toString
argument_list|()
operator|+
literal|")"
argument_list|)
decl_stmt|;
name|NetworkConnector
name|nc
init|=
operator|new
name|DiscoveryNetworkConnector
argument_list|(
name|ncUri
argument_list|)
decl_stmt|;
name|nc
operator|.
name|setDuplex
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|remoteBroker
operator|.
name|addNetworkConnector
argument_list|(
name|nc
argument_list|)
expr_stmt|;
name|nc
operator|.
name|start
argument_list|()
expr_stmt|;
comment|// mqtt port should have been assigned by now
name|assertFalse
argument_list|(
name|localBrokerMQTTPort
operator|==
operator|-
literal|1
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|remoteBrokerMQTTPort
operator|==
operator|-
literal|1
argument_list|)
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
if|if
condition|(
name|remoteBroker
operator|.
name|isStarted
argument_list|()
condition|)
block|{
name|remoteBroker
operator|.
name|stop
argument_list|()
expr_stmt|;
name|remoteBroker
operator|.
name|waitUntilStopped
argument_list|()
expr_stmt|;
block|}
if|if
condition|(
name|broker
operator|.
name|isStarted
argument_list|()
condition|)
block|{
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
name|super
operator|.
name|tearDown
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testNoStaleSubscriptionAcrossNetwork
parameter_list|()
throws|throws
name|Exception
block|{
comment|// before we get started, we want an async way to be able to know when
comment|// the durable consumer has been networked so we can assert that it indeed
comment|// would have a durable subscriber. for example, when we subscribe on remote broker,
comment|// a network-sub would be created on local broker and we want to listen for when that
comment|// even happens. we do that with advisory messages and a latch:
name|CountDownLatch
name|consumerNetworked
init|=
name|listenForConsumersOn
argument_list|(
name|broker
argument_list|)
decl_stmt|;
comment|// create a subscription with Clean == 0 (durable sub for QoS==1&& QoS==2)
comment|// on the remote broker. this sub should still be there after we disconnect
name|MQTT
name|remoteMqtt
init|=
name|createMQTTTcpConnection
argument_list|(
literal|"foo"
argument_list|,
literal|false
argument_list|,
name|remoteBrokerMQTTPort
argument_list|)
decl_stmt|;
name|BlockingConnection
name|remoteConn
init|=
name|remoteMqtt
operator|.
name|blockingConnection
argument_list|()
decl_stmt|;
name|remoteConn
operator|.
name|connect
argument_list|()
expr_stmt|;
name|remoteConn
operator|.
name|subscribe
argument_list|(
operator|new
name|Topic
index|[]
block|{
operator|new
name|Topic
argument_list|(
literal|"foo/bar"
argument_list|,
name|QoS
operator|.
name|AT_LEAST_ONCE
argument_list|)
block|}
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
literal|"No destination detected!"
argument_list|,
name|consumerNetworked
operator|.
name|await
argument_list|(
literal|1
argument_list|,
name|TimeUnit
operator|.
name|SECONDS
argument_list|)
argument_list|)
expr_stmt|;
name|assertQueueExistsOn
argument_list|(
name|remoteBroker
argument_list|,
literal|"Consumer.foo_AT_LEAST_ONCE.VirtualTopic.foo.bar"
argument_list|)
expr_stmt|;
name|assertQueueExistsOn
argument_list|(
name|broker
argument_list|,
literal|"Consumer.foo_AT_LEAST_ONCE.VirtualTopic.foo.bar"
argument_list|)
expr_stmt|;
name|remoteConn
operator|.
name|disconnect
argument_list|()
expr_stmt|;
comment|// now we reconnect the same sub on the local broker, again with clean==0
name|MQTT
name|localMqtt
init|=
name|createMQTTTcpConnection
argument_list|(
literal|"foo"
argument_list|,
literal|false
argument_list|,
name|localBrokerMQTTPort
argument_list|)
decl_stmt|;
name|BlockingConnection
name|localConn
init|=
name|localMqtt
operator|.
name|blockingConnection
argument_list|()
decl_stmt|;
name|localConn
operator|.
name|connect
argument_list|()
expr_stmt|;
name|localConn
operator|.
name|subscribe
argument_list|(
operator|new
name|Topic
index|[]
block|{
operator|new
name|Topic
argument_list|(
literal|"foo/bar"
argument_list|,
name|QoS
operator|.
name|AT_LEAST_ONCE
argument_list|)
block|}
argument_list|)
expr_stmt|;
comment|// now let's connect back up to remote broker and send a message
name|remoteConn
operator|=
name|remoteMqtt
operator|.
name|blockingConnection
argument_list|()
expr_stmt|;
name|remoteConn
operator|.
name|connect
argument_list|()
expr_stmt|;
name|remoteConn
operator|.
name|publish
argument_list|(
literal|"foo/bar"
argument_list|,
literal|"Hello, World!"
operator|.
name|getBytes
argument_list|()
argument_list|,
name|QoS
operator|.
name|AT_LEAST_ONCE
argument_list|,
literal|false
argument_list|)
expr_stmt|;
comment|// now we should see that message on the local broker because the subscription
comment|// should have been properly networked... we'll give a sec of grace for the
comment|// networking and forwarding to have happened properly
name|org
operator|.
name|fusesource
operator|.
name|mqtt
operator|.
name|client
operator|.
name|Message
name|msg
init|=
name|localConn
operator|.
name|receive
argument_list|(
literal|100
argument_list|,
name|TimeUnit
operator|.
name|SECONDS
argument_list|)
decl_stmt|;
name|assertNotNull
argument_list|(
name|msg
argument_list|)
expr_stmt|;
name|msg
operator|.
name|ack
argument_list|()
expr_stmt|;
name|String
name|response
init|=
operator|new
name|String
argument_list|(
name|msg
operator|.
name|getPayload
argument_list|()
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|"Hello, World!"
argument_list|,
name|response
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"foo/bar"
argument_list|,
name|msg
operator|.
name|getTopic
argument_list|()
argument_list|)
expr_stmt|;
comment|// Now... we SHOULD NOT see a message on the remote broker because we already
comment|// consumed it on the local broker... having the same message on the remote broker
comment|// would effectively give us duplicates in a distributed topic scenario:
name|remoteConn
operator|.
name|subscribe
argument_list|(
operator|new
name|Topic
index|[]
block|{
operator|new
name|Topic
argument_list|(
literal|"foo/bar"
argument_list|,
name|QoS
operator|.
name|AT_LEAST_ONCE
argument_list|)
block|}
argument_list|)
expr_stmt|;
name|msg
operator|=
name|remoteConn
operator|.
name|receive
argument_list|(
literal|500
argument_list|,
name|TimeUnit
operator|.
name|MILLISECONDS
argument_list|)
expr_stmt|;
name|assertNull
argument_list|(
literal|"We have duplicate messages across the cluster for a distributed topic"
argument_list|,
name|msg
argument_list|)
expr_stmt|;
block|}
specifier|private
name|CountDownLatch
name|listenForConsumersOn
parameter_list|(
name|BrokerService
name|broker
parameter_list|)
throws|throws
name|Exception
block|{
specifier|final
name|CountDownLatch
name|latch
init|=
operator|new
name|CountDownLatch
argument_list|(
literal|1
argument_list|)
decl_stmt|;
name|URI
name|brokerUri
init|=
name|broker
operator|.
name|getVmConnectorURI
argument_list|()
decl_stmt|;
specifier|final
name|ActiveMQConnectionFactory
name|cf
init|=
operator|new
name|ActiveMQConnectionFactory
argument_list|(
name|brokerUri
operator|.
name|toASCIIString
argument_list|()
argument_list|)
decl_stmt|;
specifier|final
name|Connection
name|connection
init|=
name|cf
operator|.
name|createConnection
argument_list|()
decl_stmt|;
name|connection
operator|.
name|start
argument_list|()
expr_stmt|;
specifier|final
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
name|Destination
name|dest
init|=
name|session
operator|.
name|createTopic
argument_list|(
literal|"ActiveMQ.Advisory.Consumer.Queue.Consumer.foo:AT_LEAST_ONCE.VirtualTopic.foo.bar"
argument_list|)
decl_stmt|;
name|MessageConsumer
name|consumer
init|=
name|session
operator|.
name|createConsumer
argument_list|(
name|dest
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
block|{
name|latch
operator|.
name|countDown
argument_list|()
expr_stmt|;
comment|// shutdown this connection
name|Dispatch
operator|.
name|getGlobalQueue
argument_list|()
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
block|}
block|}
argument_list|)
expr_stmt|;
return|return
name|latch
return|;
block|}
specifier|private
name|void
name|assertQueueExistsOn
parameter_list|(
name|BrokerService
name|broker
parameter_list|,
name|String
name|queueName
parameter_list|)
throws|throws
name|Exception
block|{
name|BrokerViewMBean
name|brokerView
init|=
name|broker
operator|.
name|getAdminView
argument_list|()
decl_stmt|;
name|ObjectName
index|[]
name|queueNames
init|=
name|brokerView
operator|.
name|getQueues
argument_list|()
decl_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|queueNames
operator|.
name|length
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|queueNames
index|[
literal|0
index|]
operator|.
name|toString
argument_list|()
operator|.
name|contains
argument_list|(
name|queueName
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|SuppressWarnings
argument_list|(
literal|"unused"
argument_list|)
specifier|private
name|void
name|assertOneDurableSubOn
parameter_list|(
name|BrokerService
name|broker
parameter_list|,
name|String
name|subName
parameter_list|)
throws|throws
name|Exception
block|{
name|BrokerViewMBean
name|brokerView
init|=
name|broker
operator|.
name|getAdminView
argument_list|()
decl_stmt|;
name|ObjectName
index|[]
name|activeDurableSubs
init|=
name|brokerView
operator|.
name|getDurableTopicSubscribers
argument_list|()
decl_stmt|;
name|ObjectName
index|[]
name|inactiveDurableSubs
init|=
name|brokerView
operator|.
name|getInactiveDurableTopicSubscribers
argument_list|()
decl_stmt|;
name|ObjectName
index|[]
name|allDurables
init|=
operator|(
name|ObjectName
index|[]
operator|)
name|ArrayUtils
operator|.
name|addAll
argument_list|(
name|activeDurableSubs
argument_list|,
name|inactiveDurableSubs
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|allDurables
operator|.
name|length
argument_list|)
expr_stmt|;
comment|// at this point our assertions should prove that we have only on durable sub
name|DurableSubscriptionViewMBean
name|durableSubView
init|=
operator|(
name|DurableSubscriptionViewMBean
operator|)
name|broker
operator|.
name|getManagementContext
argument_list|()
operator|.
name|newProxyInstance
argument_list|(
name|allDurables
index|[
literal|0
index|]
argument_list|,
name|DurableSubscriptionViewMBean
operator|.
name|class
argument_list|,
literal|true
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
name|subName
argument_list|,
name|durableSubView
operator|.
name|getClientId
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
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
name|super
operator|.
name|createBroker
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
name|setBrokerName
argument_list|(
literal|"local"
argument_list|)
expr_stmt|;
name|broker
operator|.
name|setDataDirectory
argument_list|(
literal|"target/activemq-data"
argument_list|)
expr_stmt|;
name|broker
operator|.
name|setDeleteAllMessagesOnStartup
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|TransportConnector
name|tc
init|=
name|broker
operator|.
name|addConnector
argument_list|(
name|getDefaultMQTTTransportConnectorUri
argument_list|()
argument_list|)
decl_stmt|;
name|localBrokerMQTTPort
operator|=
name|tc
operator|.
name|getConnectUri
argument_list|()
operator|.
name|getPort
argument_list|()
expr_stmt|;
return|return
name|broker
return|;
block|}
annotation|@
name|Override
specifier|protected
name|BrokerService
name|createRemoteBroker
parameter_list|(
name|PersistenceAdapter
name|persistenceAdapter
parameter_list|)
throws|throws
name|Exception
block|{
name|BrokerService
name|broker
init|=
name|super
operator|.
name|createRemoteBroker
argument_list|(
name|persistenceAdapter
argument_list|)
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
name|setDataDirectory
argument_list|(
literal|"target/activemq-data"
argument_list|)
expr_stmt|;
name|TransportConnector
name|tc
init|=
name|broker
operator|.
name|addConnector
argument_list|(
name|getDefaultMQTTTransportConnectorUri
argument_list|()
argument_list|)
decl_stmt|;
name|remoteBrokerMQTTPort
operator|=
name|tc
operator|.
name|getConnectUri
argument_list|()
operator|.
name|getPort
argument_list|()
expr_stmt|;
return|return
name|broker
return|;
block|}
specifier|private
name|String
name|getDefaultMQTTTransportConnectorUri
parameter_list|()
block|{
return|return
literal|"mqtt://localhost:0?transport.subscriptionStrategy=mqtt-virtual-topic-subscriptions"
return|;
block|}
specifier|private
name|MQTT
name|createMQTTTcpConnection
parameter_list|(
name|String
name|clientId
parameter_list|,
name|boolean
name|clean
parameter_list|,
name|int
name|port
parameter_list|)
throws|throws
name|Exception
block|{
name|MQTT
name|mqtt
init|=
operator|new
name|MQTT
argument_list|()
decl_stmt|;
name|mqtt
operator|.
name|setConnectAttemptsMax
argument_list|(
literal|1
argument_list|)
expr_stmt|;
name|mqtt
operator|.
name|setReconnectAttemptsMax
argument_list|(
literal|0
argument_list|)
expr_stmt|;
name|mqtt
operator|.
name|setTracer
argument_list|(
name|createTracer
argument_list|()
argument_list|)
expr_stmt|;
if|if
condition|(
name|clientId
operator|!=
literal|null
condition|)
block|{
name|mqtt
operator|.
name|setClientId
argument_list|(
name|clientId
argument_list|)
expr_stmt|;
block|}
name|mqtt
operator|.
name|setCleanSession
argument_list|(
name|clean
argument_list|)
expr_stmt|;
name|mqtt
operator|.
name|setHost
argument_list|(
literal|"localhost"
argument_list|,
name|port
argument_list|)
expr_stmt|;
return|return
name|mqtt
return|;
block|}
specifier|protected
name|Tracer
name|createTracer
parameter_list|()
block|{
return|return
operator|new
name|Tracer
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|void
name|onReceive
parameter_list|(
name|MQTTFrame
name|frame
parameter_list|)
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"Client Received:\n"
operator|+
name|frame
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|onSend
parameter_list|(
name|MQTTFrame
name|frame
parameter_list|)
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"Client Sent:\n"
operator|+
name|frame
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|debug
parameter_list|(
name|String
name|message
parameter_list|,
name|Object
modifier|...
name|args
parameter_list|)
block|{
name|LOG
operator|.
name|info
argument_list|(
name|String
operator|.
name|format
argument_list|(
name|message
argument_list|,
name|args
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
return|;
block|}
block|}
end_class

end_unit

