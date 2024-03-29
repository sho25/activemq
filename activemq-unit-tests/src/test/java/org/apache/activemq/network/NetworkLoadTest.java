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
name|ArrayList
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Date
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
name|concurrent
operator|.
name|atomic
operator|.
name|AtomicBoolean
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
name|java
operator|.
name|util
operator|.
name|concurrent
operator|.
name|atomic
operator|.
name|AtomicReference
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
name|DeliveryMode
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
name|broker
operator|.
name|region
operator|.
name|policy
operator|.
name|NoSubscriptionRecoveryPolicy
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
name|VMPendingQueueMessageStoragePolicy
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
name|VMPendingSubscriberMessageStoragePolicy
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
name|usage
operator|.
name|SystemUsage
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
comment|/**  * This test case is used to load test store and forwarding between brokers.  It sets up  * n brokers to which have a chain of queues which this test consumes and produces to.   *   * If the network bridges gets stuck at any point subsequent queues will not get messages.  This test   * samples the production and consumption stats every second and if the flow of messages  * get stuck then this tast fails.  The test monitors the flow of messages for 1 min.  *    * @author chirino  */
end_comment

begin_class
specifier|public
class|class
name|NetworkLoadTest
extends|extends
name|TestCase
block|{
specifier|private
specifier|static
specifier|final
specifier|transient
name|Logger
name|LOG
init|=
name|LoggerFactory
operator|.
name|getLogger
argument_list|(
name|NetworkLoadTest
operator|.
name|class
argument_list|)
decl_stmt|;
comment|// How many times do we sample?
specifier|private
specifier|static
specifier|final
name|long
name|SAMPLES
init|=
name|Integer
operator|.
name|parseInt
argument_list|(
name|System
operator|.
name|getProperty
argument_list|(
literal|"SAMPLES"
argument_list|,
literal|""
operator|+
literal|60
operator|*
literal|1
operator|/
literal|5
argument_list|)
argument_list|)
decl_stmt|;
comment|// Slower machines might need to make this bigger.
specifier|private
specifier|static
specifier|final
name|long
name|SAMPLE_DURATION
init|=
name|Integer
operator|.
name|parseInt
argument_list|(
name|System
operator|.
name|getProperty
argument_list|(
literal|"SAMPLES_DURATION"
argument_list|,
literal|""
operator|+
literal|1000
operator|*
literal|5
argument_list|)
argument_list|)
decl_stmt|;
specifier|protected
specifier|static
specifier|final
name|int
name|BROKER_COUNT
init|=
literal|4
decl_stmt|;
specifier|protected
specifier|static
specifier|final
name|int
name|MESSAGE_SIZE
init|=
literal|2000
decl_stmt|;
name|String
name|groupId
decl_stmt|;
class|class
name|ForwardingClient
block|{
specifier|private
specifier|final
name|AtomicLong
name|forwardCounter
init|=
operator|new
name|AtomicLong
argument_list|()
decl_stmt|;
specifier|private
specifier|final
name|Connection
name|toConnection
decl_stmt|;
specifier|private
specifier|final
name|Connection
name|fromConnection
decl_stmt|;
specifier|public
name|ForwardingClient
parameter_list|(
name|int
name|from
parameter_list|,
name|int
name|to
parameter_list|)
throws|throws
name|JMSException
block|{
name|toConnection
operator|=
name|createConnection
argument_list|(
name|from
argument_list|)
expr_stmt|;
name|Session
name|toSession
init|=
name|toConnection
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
name|MessageProducer
name|producer
init|=
name|toSession
operator|.
name|createProducer
argument_list|(
operator|new
name|ActiveMQQueue
argument_list|(
literal|"Q"
operator|+
name|to
argument_list|)
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
name|producer
operator|.
name|setDisableMessageID
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|fromConnection
operator|=
name|createConnection
argument_list|(
name|from
argument_list|)
expr_stmt|;
name|Session
name|fromSession
init|=
name|fromConnection
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
name|consumer
init|=
name|fromSession
operator|.
name|createConsumer
argument_list|(
operator|new
name|ActiveMQQueue
argument_list|(
literal|"Q"
operator|+
name|from
argument_list|)
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
specifier|public
name|void
name|onMessage
parameter_list|(
name|Message
name|msg
parameter_list|)
block|{
try|try
block|{
name|producer
operator|.
name|send
argument_list|(
name|msg
argument_list|)
expr_stmt|;
name|forwardCounter
operator|.
name|incrementAndGet
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|JMSException
name|e
parameter_list|)
block|{
comment|// this is caused by the connection getting closed.
block|}
block|}
block|}
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|start
parameter_list|()
throws|throws
name|JMSException
block|{
name|toConnection
operator|.
name|start
argument_list|()
expr_stmt|;
name|fromConnection
operator|.
name|start
argument_list|()
expr_stmt|;
block|}
specifier|public
name|void
name|stop
parameter_list|()
throws|throws
name|JMSException
block|{
name|toConnection
operator|.
name|stop
argument_list|()
expr_stmt|;
name|fromConnection
operator|.
name|stop
argument_list|()
expr_stmt|;
block|}
specifier|public
name|void
name|close
parameter_list|()
throws|throws
name|JMSException
block|{
name|toConnection
operator|.
name|close
argument_list|()
expr_stmt|;
name|fromConnection
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
specifier|private
name|BrokerService
index|[]
name|brokers
decl_stmt|;
specifier|private
name|ForwardingClient
index|[]
name|forwardingClients
decl_stmt|;
specifier|protected
name|void
name|setUp
parameter_list|()
throws|throws
name|Exception
block|{
name|groupId
operator|=
literal|"network-load-test-"
operator|+
name|System
operator|.
name|currentTimeMillis
argument_list|()
expr_stmt|;
name|brokers
operator|=
operator|new
name|BrokerService
index|[
name|BROKER_COUNT
index|]
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
name|brokers
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"Starting broker: "
operator|+
name|i
argument_list|)
expr_stmt|;
name|brokers
index|[
name|i
index|]
operator|=
name|createBroker
argument_list|(
name|i
argument_list|)
expr_stmt|;
name|brokers
index|[
name|i
index|]
operator|.
name|start
argument_list|()
expr_stmt|;
block|}
comment|// Wait for the network connection to get setup.
comment|// The wait is exponential since every broker has to connect to every other broker.
name|Thread
operator|.
name|sleep
argument_list|(
name|BROKER_COUNT
operator|*
name|BROKER_COUNT
operator|*
literal|50
argument_list|)
expr_stmt|;
name|forwardingClients
operator|=
operator|new
name|ForwardingClient
index|[
name|BROKER_COUNT
operator|-
literal|1
index|]
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
name|forwardingClients
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"Starting fowarding client "
operator|+
name|i
argument_list|)
expr_stmt|;
name|forwardingClients
index|[
name|i
index|]
operator|=
operator|new
name|ForwardingClient
argument_list|(
name|i
argument_list|,
name|i
operator|+
literal|1
argument_list|)
expr_stmt|;
name|forwardingClients
index|[
name|i
index|]
operator|.
name|start
argument_list|()
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
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|forwardingClients
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"Stoping fowarding client "
operator|+
name|i
argument_list|)
expr_stmt|;
name|forwardingClients
index|[
name|i
index|]
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|brokers
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"Stoping broker "
operator|+
name|i
argument_list|)
expr_stmt|;
name|brokers
index|[
name|i
index|]
operator|.
name|stop
argument_list|()
expr_stmt|;
block|}
block|}
specifier|protected
name|Connection
name|createConnection
parameter_list|(
name|int
name|brokerId
parameter_list|)
throws|throws
name|JMSException
block|{
name|ActiveMQConnectionFactory
name|connectionFactory
init|=
operator|new
name|ActiveMQConnectionFactory
argument_list|(
literal|"tcp://localhost:"
operator|+
operator|(
literal|60000
operator|+
name|brokerId
operator|)
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
literal|true
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
literal|false
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
operator|.
name|createConnection
argument_list|()
return|;
block|}
specifier|protected
name|BrokerService
name|createBroker
parameter_list|(
name|int
name|brokerId
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
name|setBrokerName
argument_list|(
literal|"broker-"
operator|+
name|brokerId
argument_list|)
expr_stmt|;
name|broker
operator|.
name|setPersistent
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
name|getManagementContext
argument_list|()
operator|.
name|setCreateConnector
argument_list|(
literal|false
argument_list|)
expr_stmt|;
specifier|final
name|SystemUsage
name|memoryManager
init|=
operator|new
name|SystemUsage
argument_list|()
decl_stmt|;
name|memoryManager
operator|.
name|getMemoryUsage
argument_list|()
operator|.
name|setLimit
argument_list|(
literal|1024
operator|*
literal|1024
operator|*
literal|50
argument_list|)
expr_stmt|;
comment|// 50 MB
name|broker
operator|.
name|setSystemUsage
argument_list|(
name|memoryManager
argument_list|)
expr_stmt|;
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
specifier|final
name|PolicyEntry
name|entry
init|=
operator|new
name|PolicyEntry
argument_list|()
decl_stmt|;
name|entry
operator|.
name|setQueue
argument_list|(
literal|">"
argument_list|)
expr_stmt|;
name|entry
operator|.
name|setMemoryLimit
argument_list|(
literal|1024
operator|*
literal|1024
operator|*
literal|1
argument_list|)
expr_stmt|;
comment|// Set to 1 MB
name|entry
operator|.
name|setPendingSubscriberPolicy
argument_list|(
operator|new
name|VMPendingSubscriberMessageStoragePolicy
argument_list|()
argument_list|)
expr_stmt|;
name|entry
operator|.
name|setPendingQueuePolicy
argument_list|(
operator|new
name|VMPendingQueueMessageStoragePolicy
argument_list|()
argument_list|)
expr_stmt|;
name|policyEntries
operator|.
name|add
argument_list|(
name|entry
argument_list|)
expr_stmt|;
comment|// This is to turn of the default behavior of storing topic messages for retroactive consumption
specifier|final
name|PolicyEntry
name|topicPolicyEntry
init|=
operator|new
name|PolicyEntry
argument_list|()
decl_stmt|;
name|topicPolicyEntry
operator|.
name|setTopic
argument_list|(
literal|">"
argument_list|)
expr_stmt|;
specifier|final
name|NoSubscriptionRecoveryPolicy
name|noSubscriptionRecoveryPolicy
init|=
operator|new
name|NoSubscriptionRecoveryPolicy
argument_list|()
decl_stmt|;
name|topicPolicyEntry
operator|.
name|setSubscriptionRecoveryPolicy
argument_list|(
name|noSubscriptionRecoveryPolicy
argument_list|)
expr_stmt|;
specifier|final
name|PolicyMap
name|policyMap
init|=
operator|new
name|PolicyMap
argument_list|()
decl_stmt|;
name|policyMap
operator|.
name|setPolicyEntries
argument_list|(
name|policyEntries
argument_list|)
expr_stmt|;
name|broker
operator|.
name|setDestinationPolicy
argument_list|(
name|policyMap
argument_list|)
expr_stmt|;
name|TransportConnector
name|transportConnector
init|=
operator|new
name|TransportConnector
argument_list|()
decl_stmt|;
name|transportConnector
operator|.
name|setUri
argument_list|(
operator|new
name|URI
argument_list|(
literal|"tcp://localhost:"
operator|+
operator|(
literal|60000
operator|+
name|brokerId
operator|)
argument_list|)
argument_list|)
expr_stmt|;
name|transportConnector
operator|.
name|setDiscoveryUri
argument_list|(
operator|new
name|URI
argument_list|(
literal|"multicast://default?group="
operator|+
name|groupId
argument_list|)
argument_list|)
expr_stmt|;
name|broker
operator|.
name|addConnector
argument_list|(
name|transportConnector
argument_list|)
expr_stmt|;
name|DiscoveryNetworkConnector
name|networkConnector
init|=
operator|new
name|DiscoveryNetworkConnector
argument_list|()
decl_stmt|;
name|networkConnector
operator|.
name|setUri
argument_list|(
operator|new
name|URI
argument_list|(
literal|"multicast://default?group="
operator|+
name|groupId
argument_list|)
argument_list|)
expr_stmt|;
name|networkConnector
operator|.
name|setBridgeTempDestinations
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|networkConnector
operator|.
name|setPrefetchSize
argument_list|(
literal|1
argument_list|)
expr_stmt|;
name|broker
operator|.
name|addNetworkConnector
argument_list|(
name|networkConnector
argument_list|)
expr_stmt|;
return|return
name|broker
return|;
block|}
specifier|public
name|void
name|testRequestReply
parameter_list|()
throws|throws
name|Exception
block|{
specifier|final
name|int
name|to
init|=
literal|0
decl_stmt|;
comment|// Send to the first broker
name|int
name|from
init|=
name|brokers
operator|.
name|length
operator|-
literal|1
decl_stmt|;
comment|// consume from the last broker..
name|LOG
operator|.
name|info
argument_list|(
literal|"Staring Final Consumer"
argument_list|)
expr_stmt|;
name|Connection
name|fromConnection
init|=
name|createConnection
argument_list|(
name|from
argument_list|)
decl_stmt|;
name|fromConnection
operator|.
name|start
argument_list|()
expr_stmt|;
name|Session
name|fromSession
init|=
name|fromConnection
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
name|consumer
init|=
name|fromSession
operator|.
name|createConsumer
argument_list|(
operator|new
name|ActiveMQQueue
argument_list|(
literal|"Q"
operator|+
name|from
argument_list|)
argument_list|)
decl_stmt|;
specifier|final
name|AtomicReference
argument_list|<
name|ActiveMQTextMessage
argument_list|>
name|lastMessageReceived
init|=
operator|new
name|AtomicReference
argument_list|<
name|ActiveMQTextMessage
argument_list|>
argument_list|()
decl_stmt|;
specifier|final
name|AtomicLong
name|producedMessages
init|=
operator|new
name|AtomicLong
argument_list|()
decl_stmt|;
specifier|final
name|AtomicLong
name|receivedMessages
init|=
operator|new
name|AtomicLong
argument_list|()
decl_stmt|;
specifier|final
name|AtomicBoolean
name|done
init|=
operator|new
name|AtomicBoolean
argument_list|()
decl_stmt|;
comment|// Setup the consumer..
name|consumer
operator|.
name|setMessageListener
argument_list|(
operator|new
name|MessageListener
argument_list|()
block|{
specifier|public
name|void
name|onMessage
parameter_list|(
name|Message
name|msg
parameter_list|)
block|{
name|ActiveMQTextMessage
name|m
init|=
operator|(
name|ActiveMQTextMessage
operator|)
name|msg
decl_stmt|;
name|ActiveMQTextMessage
name|last
init|=
name|lastMessageReceived
operator|.
name|get
argument_list|()
decl_stmt|;
if|if
condition|(
name|last
operator|!=
literal|null
condition|)
block|{
comment|// Some order checking...
if|if
condition|(
name|last
operator|.
name|getMessageId
argument_list|()
operator|.
name|getProducerSequenceId
argument_list|()
operator|>
name|m
operator|.
name|getMessageId
argument_list|()
operator|.
name|getProducerSequenceId
argument_list|()
condition|)
block|{
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"Received an out of order message. Got "
operator|+
name|m
operator|.
name|getMessageId
argument_list|()
operator|+
literal|", expected something after "
operator|+
name|last
operator|.
name|getMessageId
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
name|lastMessageReceived
operator|.
name|set
argument_list|(
name|m
argument_list|)
expr_stmt|;
name|receivedMessages
operator|.
name|incrementAndGet
argument_list|()
expr_stmt|;
block|}
block|}
argument_list|)
expr_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"Staring Initial Producer"
argument_list|)
expr_stmt|;
specifier|final
name|Connection
name|toConnection
init|=
name|createConnection
argument_list|(
name|to
argument_list|)
decl_stmt|;
name|Thread
name|producer
init|=
operator|new
name|Thread
argument_list|(
literal|"Producer"
argument_list|)
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
name|toConnection
operator|.
name|start
argument_list|()
expr_stmt|;
name|Session
name|toSession
init|=
name|toConnection
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
name|MessageProducer
name|producer
init|=
name|toSession
operator|.
name|createProducer
argument_list|(
operator|new
name|ActiveMQQueue
argument_list|(
literal|"Q"
operator|+
name|to
argument_list|)
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
name|producer
operator|.
name|setDisableMessageID
argument_list|(
literal|true
argument_list|)
expr_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
operator|!
name|done
operator|.
name|get
argument_list|()
condition|;
name|i
operator|++
control|)
block|{
name|TextMessage
name|msg
init|=
name|toSession
operator|.
name|createTextMessage
argument_list|(
name|createMessageText
argument_list|(
name|i
argument_list|)
argument_list|)
decl_stmt|;
name|producer
operator|.
name|send
argument_list|(
name|msg
argument_list|)
expr_stmt|;
name|producedMessages
operator|.
name|incrementAndGet
argument_list|()
expr_stmt|;
block|}
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
specifier|private
name|String
name|createMessageText
parameter_list|(
name|int
name|index
parameter_list|)
block|{
name|StringBuffer
name|buffer
init|=
operator|new
name|StringBuffer
argument_list|(
name|MESSAGE_SIZE
argument_list|)
decl_stmt|;
name|buffer
operator|.
name|append
argument_list|(
name|index
operator|+
literal|" on "
operator|+
operator|new
name|Date
argument_list|()
operator|+
literal|" ..."
argument_list|)
expr_stmt|;
if|if
condition|(
name|buffer
operator|.
name|length
argument_list|()
operator|>
name|MESSAGE_SIZE
condition|)
block|{
return|return
name|buffer
operator|.
name|substring
argument_list|(
literal|0
argument_list|,
name|MESSAGE_SIZE
argument_list|)
return|;
block|}
for|for
control|(
name|int
name|i
init|=
name|buffer
operator|.
name|length
argument_list|()
init|;
name|i
operator|<
name|MESSAGE_SIZE
condition|;
name|i
operator|++
control|)
block|{
name|buffer
operator|.
name|append
argument_list|(
literal|' '
argument_list|)
expr_stmt|;
block|}
return|return
name|buffer
operator|.
name|toString
argument_list|()
return|;
block|}
block|}
decl_stmt|;
name|producer
operator|.
name|start
argument_list|()
expr_stmt|;
comment|// Give the forwarding clients a chance to get going and fill the down
comment|// stream broker queues..
name|Thread
operator|.
name|sleep
argument_list|(
name|BROKER_COUNT
operator|*
literal|200
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
name|SAMPLES
condition|;
name|i
operator|++
control|)
block|{
name|long
name|start
init|=
name|System
operator|.
name|currentTimeMillis
argument_list|()
decl_stmt|;
name|producedMessages
operator|.
name|set
argument_list|(
literal|0
argument_list|)
expr_stmt|;
name|receivedMessages
operator|.
name|set
argument_list|(
literal|0
argument_list|)
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
name|forwardingClients
operator|.
name|length
condition|;
name|j
operator|++
control|)
block|{
name|forwardingClients
index|[
name|j
index|]
operator|.
name|forwardCounter
operator|.
name|set
argument_list|(
literal|0
argument_list|)
expr_stmt|;
block|}
name|Thread
operator|.
name|sleep
argument_list|(
name|SAMPLE_DURATION
argument_list|)
expr_stmt|;
name|long
name|end
init|=
name|System
operator|.
name|currentTimeMillis
argument_list|()
decl_stmt|;
name|long
name|r
init|=
name|receivedMessages
operator|.
name|get
argument_list|()
decl_stmt|;
name|long
name|p
init|=
name|producedMessages
operator|.
name|get
argument_list|()
decl_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"published: "
operator|+
name|p
operator|+
literal|" msgs at "
operator|+
operator|(
name|p
operator|*
literal|1000f
operator|/
operator|(
name|end
operator|-
name|start
operator|)
operator|)
operator|+
literal|" msgs/sec, "
operator|+
literal|"consumed: "
operator|+
name|r
operator|+
literal|" msgs at "
operator|+
operator|(
name|r
operator|*
literal|1000f
operator|/
operator|(
name|end
operator|-
name|start
operator|)
operator|)
operator|+
literal|" msgs/sec"
argument_list|)
expr_stmt|;
name|StringBuffer
name|fwdingmsg
init|=
operator|new
name|StringBuffer
argument_list|(
literal|500
argument_list|)
decl_stmt|;
name|fwdingmsg
operator|.
name|append
argument_list|(
literal|"  forwarding counters: "
argument_list|)
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
name|forwardingClients
operator|.
name|length
condition|;
name|j
operator|++
control|)
block|{
if|if
condition|(
name|j
operator|!=
literal|0
condition|)
block|{
name|fwdingmsg
operator|.
name|append
argument_list|(
literal|", "
argument_list|)
expr_stmt|;
block|}
name|fwdingmsg
operator|.
name|append
argument_list|(
name|forwardingClients
index|[
name|j
index|]
operator|.
name|forwardCounter
operator|.
name|get
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|LOG
operator|.
name|info
argument_list|(
name|fwdingmsg
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
comment|// The test is just checking to make sure thaat the producer and consumer does not hang
comment|// due to the network hops take to route the message form the producer to the consumer.
name|assertTrue
argument_list|(
literal|"Recieved some messages since last sample"
argument_list|,
name|r
operator|>
literal|0
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
literal|"Produced some messages since last sample"
argument_list|,
name|p
operator|>
literal|0
argument_list|)
expr_stmt|;
block|}
name|LOG
operator|.
name|info
argument_list|(
literal|"Sample done."
argument_list|)
expr_stmt|;
name|done
operator|.
name|set
argument_list|(
literal|true
argument_list|)
expr_stmt|;
comment|// Wait for the producer to finish.
name|producer
operator|.
name|join
argument_list|(
literal|1000
operator|*
literal|5
argument_list|)
expr_stmt|;
name|toConnection
operator|.
name|close
argument_list|()
expr_stmt|;
name|fromConnection
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
end_class

end_unit

