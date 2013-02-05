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
name|io
operator|.
name|IOException
import|;
end_import

begin_import
import|import
name|java
operator|.
name|lang
operator|.
name|reflect
operator|.
name|Field
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
name|javax
operator|.
name|jms
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
name|region
operator|.
name|DurableTopicSubscription
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
name|broker
operator|.
name|region
operator|.
name|TopicRegion
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
name|SubscriptionKey
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
name|AMQ4062Test
block|{
specifier|private
name|BrokerService
name|service
decl_stmt|;
specifier|private
name|PolicyEntry
name|policy
decl_stmt|;
specifier|private
name|ConcurrentHashMap
argument_list|<
name|SubscriptionKey
argument_list|,
name|DurableTopicSubscription
argument_list|>
name|durableSubscriptions
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|int
name|PREFETCH_SIZE_5
init|=
literal|5
decl_stmt|;
specifier|private
name|String
name|connectionUri
decl_stmt|;
annotation|@
name|Before
specifier|public
name|void
name|startBroker
parameter_list|()
throws|throws
name|IOException
throws|,
name|Exception
block|{
name|service
operator|=
operator|new
name|BrokerService
argument_list|()
expr_stmt|;
name|service
operator|.
name|setPersistent
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|service
operator|.
name|setDeleteAllMessagesOnStartup
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|service
operator|.
name|setUseJmx
argument_list|(
literal|false
argument_list|)
expr_stmt|;
name|KahaDBPersistenceAdapter
name|pa
init|=
operator|new
name|KahaDBPersistenceAdapter
argument_list|()
decl_stmt|;
name|File
name|dataFile
init|=
operator|new
name|File
argument_list|(
literal|"createData"
argument_list|)
decl_stmt|;
name|pa
operator|.
name|setDirectory
argument_list|(
name|dataFile
argument_list|)
expr_stmt|;
name|pa
operator|.
name|setJournalMaxFileLength
argument_list|(
literal|1024
operator|*
literal|1024
operator|*
literal|32
argument_list|)
expr_stmt|;
name|service
operator|.
name|setPersistenceAdapter
argument_list|(
name|pa
argument_list|)
expr_stmt|;
name|policy
operator|=
operator|new
name|PolicyEntry
argument_list|()
expr_stmt|;
name|policy
operator|.
name|setTopic
argument_list|(
literal|">"
argument_list|)
expr_stmt|;
name|policy
operator|.
name|setDurableTopicPrefetch
argument_list|(
name|PREFETCH_SIZE_5
argument_list|)
expr_stmt|;
name|PolicyMap
name|pMap
init|=
operator|new
name|PolicyMap
argument_list|()
decl_stmt|;
name|pMap
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
name|pMap
argument_list|)
expr_stmt|;
name|service
operator|.
name|addConnector
argument_list|(
literal|"tcp://localhost:0"
argument_list|)
expr_stmt|;
name|service
operator|.
name|start
argument_list|()
expr_stmt|;
name|service
operator|.
name|waitUntilStarted
argument_list|()
expr_stmt|;
name|connectionUri
operator|=
name|service
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
expr_stmt|;
block|}
specifier|public
name|void
name|restartBroker
parameter_list|()
throws|throws
name|IOException
throws|,
name|Exception
block|{
name|service
operator|=
operator|new
name|BrokerService
argument_list|()
expr_stmt|;
name|service
operator|.
name|setPersistent
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|service
operator|.
name|setUseJmx
argument_list|(
literal|false
argument_list|)
expr_stmt|;
name|service
operator|.
name|setKeepDurableSubsActive
argument_list|(
literal|false
argument_list|)
expr_stmt|;
name|KahaDBPersistenceAdapter
name|pa
init|=
operator|new
name|KahaDBPersistenceAdapter
argument_list|()
decl_stmt|;
name|File
name|dataFile
init|=
operator|new
name|File
argument_list|(
literal|"createData"
argument_list|)
decl_stmt|;
name|pa
operator|.
name|setDirectory
argument_list|(
name|dataFile
argument_list|)
expr_stmt|;
name|pa
operator|.
name|setJournalMaxFileLength
argument_list|(
literal|1024
operator|*
literal|1024
operator|*
literal|32
argument_list|)
expr_stmt|;
name|service
operator|.
name|setPersistenceAdapter
argument_list|(
name|pa
argument_list|)
expr_stmt|;
name|policy
operator|=
operator|new
name|PolicyEntry
argument_list|()
expr_stmt|;
name|policy
operator|.
name|setTopic
argument_list|(
literal|">"
argument_list|)
expr_stmt|;
name|policy
operator|.
name|setDurableTopicPrefetch
argument_list|(
name|PREFETCH_SIZE_5
argument_list|)
expr_stmt|;
name|PolicyMap
name|pMap
init|=
operator|new
name|PolicyMap
argument_list|()
decl_stmt|;
name|pMap
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
name|pMap
argument_list|)
expr_stmt|;
name|service
operator|.
name|addConnector
argument_list|(
literal|"tcp://localhost:0"
argument_list|)
expr_stmt|;
name|service
operator|.
name|start
argument_list|()
expr_stmt|;
name|service
operator|.
name|waitUntilStarted
argument_list|()
expr_stmt|;
name|connectionUri
operator|=
name|service
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
expr_stmt|;
block|}
annotation|@
name|After
specifier|public
name|void
name|stopBroker
parameter_list|()
throws|throws
name|Exception
block|{
name|service
operator|.
name|stop
argument_list|()
expr_stmt|;
name|service
operator|.
name|waitUntilStopped
argument_list|()
expr_stmt|;
name|service
operator|=
literal|null
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testDirableSubPrefetchRecovered
parameter_list|()
throws|throws
name|Exception
block|{
name|PrefetchConsumer
name|consumer
init|=
operator|new
name|PrefetchConsumer
argument_list|(
literal|true
argument_list|,
name|connectionUri
argument_list|)
decl_stmt|;
name|consumer
operator|.
name|recieve
argument_list|()
expr_stmt|;
name|durableSubscriptions
operator|=
name|getDurableSubscriptions
argument_list|()
expr_stmt|;
name|ConsumerInfo
name|info
init|=
name|getConsumerInfo
argument_list|(
name|durableSubscriptions
argument_list|)
decl_stmt|;
comment|//check if the prefetchSize equals to the size we set in the PolicyEntry
name|assertEquals
argument_list|(
name|PREFETCH_SIZE_5
argument_list|,
name|info
operator|.
name|getPrefetchSize
argument_list|()
argument_list|)
expr_stmt|;
name|consumer
operator|.
name|a
operator|.
name|countDown
argument_list|()
expr_stmt|;
name|Producer
name|p
init|=
operator|new
name|Producer
argument_list|(
name|connectionUri
argument_list|)
decl_stmt|;
name|p
operator|.
name|send
argument_list|()
expr_stmt|;
name|p
operator|=
literal|null
expr_stmt|;
name|service
operator|.
name|stop
argument_list|()
expr_stmt|;
name|service
operator|.
name|waitUntilStopped
argument_list|()
expr_stmt|;
name|durableSubscriptions
operator|=
literal|null
expr_stmt|;
name|consumer
operator|=
literal|null
expr_stmt|;
name|stopBroker
argument_list|()
expr_stmt|;
name|restartBroker
argument_list|()
expr_stmt|;
name|getDurableSubscriptions
argument_list|()
expr_stmt|;
name|info
operator|=
literal|null
expr_stmt|;
name|info
operator|=
name|getConsumerInfo
argument_list|(
name|durableSubscriptions
argument_list|)
expr_stmt|;
comment|//check if the prefetchSize equals to 0 after persistent storage recovered
comment|//assertEquals(0, info.getPrefetchSize());
name|consumer
operator|=
operator|new
name|PrefetchConsumer
argument_list|(
literal|false
argument_list|,
name|connectionUri
argument_list|)
expr_stmt|;
name|consumer
operator|.
name|recieve
argument_list|()
expr_stmt|;
name|consumer
operator|.
name|a
operator|.
name|countDown
argument_list|()
expr_stmt|;
name|info
operator|=
literal|null
expr_stmt|;
name|info
operator|=
name|getConsumerInfo
argument_list|(
name|durableSubscriptions
argument_list|)
expr_stmt|;
comment|//check if the prefetchSize is the default size for durable consumer and the PolicyEntry
comment|//we set earlier take no effect
comment|//assertEquals(100, info.getPrefetchSize());
comment|//info.getPrefetchSize() is 100,it should be 5,because I set the PolicyEntry as follows,
comment|//policy.setDurableTopicPrefetch(PREFETCH_SIZE_5);
name|assertEquals
argument_list|(
literal|5
argument_list|,
name|info
operator|.
name|getPrefetchSize
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|SuppressWarnings
argument_list|(
literal|"unchecked"
argument_list|)
specifier|private
name|ConcurrentHashMap
argument_list|<
name|SubscriptionKey
argument_list|,
name|DurableTopicSubscription
argument_list|>
name|getDurableSubscriptions
parameter_list|()
throws|throws
name|NoSuchFieldException
throws|,
name|IllegalAccessException
block|{
if|if
condition|(
name|durableSubscriptions
operator|!=
literal|null
condition|)
return|return
name|durableSubscriptions
return|;
name|RegionBroker
name|regionBroker
init|=
operator|(
name|RegionBroker
operator|)
name|service
operator|.
name|getRegionBroker
argument_list|()
decl_stmt|;
name|TopicRegion
name|region
init|=
operator|(
name|TopicRegion
operator|)
name|regionBroker
operator|.
name|getTopicRegion
argument_list|()
decl_stmt|;
name|Field
name|field
init|=
name|TopicRegion
operator|.
name|class
operator|.
name|getDeclaredField
argument_list|(
literal|"durableSubscriptions"
argument_list|)
decl_stmt|;
name|field
operator|.
name|setAccessible
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|durableSubscriptions
operator|=
operator|(
name|ConcurrentHashMap
argument_list|<
name|SubscriptionKey
argument_list|,
name|DurableTopicSubscription
argument_list|>
operator|)
name|field
operator|.
name|get
argument_list|(
name|region
argument_list|)
expr_stmt|;
return|return
name|durableSubscriptions
return|;
block|}
specifier|private
name|ConsumerInfo
name|getConsumerInfo
parameter_list|(
name|ConcurrentHashMap
argument_list|<
name|SubscriptionKey
argument_list|,
name|DurableTopicSubscription
argument_list|>
name|durableSubscriptions
parameter_list|)
block|{
name|ConsumerInfo
name|info
init|=
literal|null
decl_stmt|;
for|for
control|(
name|Iterator
argument_list|<
name|DurableTopicSubscription
argument_list|>
name|it
init|=
name|durableSubscriptions
operator|.
name|values
argument_list|()
operator|.
name|iterator
argument_list|()
init|;
name|it
operator|.
name|hasNext
argument_list|()
condition|;
control|)
block|{
name|Subscription
name|sub
init|=
name|it
operator|.
name|next
argument_list|()
decl_stmt|;
name|info
operator|=
name|sub
operator|.
name|getConsumerInfo
argument_list|()
expr_stmt|;
if|if
condition|(
name|info
operator|.
name|getSubscriptionName
argument_list|()
operator|.
name|equals
argument_list|(
name|PrefetchConsumer
operator|.
name|SUBSCRIPTION_NAME
argument_list|)
condition|)
block|{
return|return
name|info
return|;
block|}
block|}
return|return
literal|null
return|;
block|}
specifier|public
class|class
name|PrefetchConsumer
implements|implements
name|MessageListener
block|{
specifier|public
specifier|static
specifier|final
name|String
name|SUBSCRIPTION_NAME
init|=
literal|"A_NAME_ABC_DEF"
decl_stmt|;
specifier|private
specifier|final
name|String
name|user
init|=
name|ActiveMQConnection
operator|.
name|DEFAULT_USER
decl_stmt|;
specifier|private
specifier|final
name|String
name|password
init|=
name|ActiveMQConnection
operator|.
name|DEFAULT_PASSWORD
decl_stmt|;
specifier|private
specifier|final
name|String
name|uri
decl_stmt|;
specifier|private
name|boolean
name|transacted
decl_stmt|;
name|ActiveMQConnection
name|connection
decl_stmt|;
name|Session
name|session
decl_stmt|;
name|MessageConsumer
name|consumer
decl_stmt|;
specifier|private
name|boolean
name|needAck
init|=
literal|false
decl_stmt|;
name|CountDownLatch
name|a
init|=
operator|new
name|CountDownLatch
argument_list|(
literal|1
argument_list|)
decl_stmt|;
specifier|public
name|PrefetchConsumer
parameter_list|(
name|boolean
name|needAck
parameter_list|,
name|String
name|uri
parameter_list|)
block|{
name|this
operator|.
name|needAck
operator|=
name|needAck
expr_stmt|;
name|this
operator|.
name|uri
operator|=
name|uri
expr_stmt|;
block|}
specifier|public
name|void
name|recieve
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
name|user
argument_list|,
name|password
argument_list|,
name|uri
argument_list|)
decl_stmt|;
name|connection
operator|=
operator|(
name|ActiveMQConnection
operator|)
name|connectionFactory
operator|.
name|createConnection
argument_list|()
expr_stmt|;
name|connection
operator|.
name|setClientID
argument_list|(
literal|"3"
argument_list|)
expr_stmt|;
name|connection
operator|.
name|start
argument_list|()
expr_stmt|;
name|session
operator|=
name|connection
operator|.
name|createSession
argument_list|(
name|transacted
argument_list|,
name|Session
operator|.
name|CLIENT_ACKNOWLEDGE
argument_list|)
expr_stmt|;
name|Destination
name|destination
init|=
name|session
operator|.
name|createTopic
argument_list|(
literal|"topic2"
argument_list|)
decl_stmt|;
name|consumer
operator|=
name|session
operator|.
name|createDurableSubscriber
argument_list|(
operator|(
name|Topic
operator|)
name|destination
argument_list|,
name|SUBSCRIPTION_NAME
argument_list|)
expr_stmt|;
name|consumer
operator|.
name|setMessageListener
argument_list|(
name|this
argument_list|)
expr_stmt|;
block|}
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
name|a
operator|.
name|await
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|InterruptedException
name|e1
parameter_list|)
block|{             }
if|if
condition|(
name|needAck
condition|)
block|{
try|try
block|{
name|message
operator|.
name|acknowledge
argument_list|()
expr_stmt|;
name|consumer
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
block|}
catch|catch
parameter_list|(
name|JMSException
name|e
parameter_list|)
block|{                 }
block|}
block|}
block|}
specifier|public
class|class
name|Producer
block|{
specifier|protected
specifier|final
name|String
name|user
init|=
name|ActiveMQConnection
operator|.
name|DEFAULT_USER
decl_stmt|;
specifier|private
specifier|final
name|String
name|password
init|=
name|ActiveMQConnection
operator|.
name|DEFAULT_PASSWORD
decl_stmt|;
specifier|private
specifier|final
name|String
name|uri
decl_stmt|;
specifier|private
name|boolean
name|transacted
decl_stmt|;
specifier|public
name|Producer
parameter_list|(
name|String
name|uri
parameter_list|)
block|{
name|this
operator|.
name|uri
operator|=
name|uri
expr_stmt|;
block|}
specifier|public
name|void
name|send
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
name|user
argument_list|,
name|password
argument_list|,
name|uri
argument_list|)
decl_stmt|;
name|ActiveMQConnection
name|connection
init|=
operator|(
name|ActiveMQConnection
operator|)
name|connectionFactory
operator|.
name|createConnection
argument_list|()
decl_stmt|;
name|connection
operator|.
name|start
argument_list|()
expr_stmt|;
name|ActiveMQSession
name|session
init|=
operator|(
name|ActiveMQSession
operator|)
name|connection
operator|.
name|createSession
argument_list|(
name|transacted
argument_list|,
name|Session
operator|.
name|AUTO_ACKNOWLEDGE
argument_list|)
decl_stmt|;
name|Destination
name|destination
init|=
name|session
operator|.
name|createTopic
argument_list|(
literal|"topic2"
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
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
literal|100
condition|;
name|i
operator|++
control|)
block|{
name|TextMessage
name|om
init|=
name|session
operator|.
name|createTextMessage
argument_list|(
literal|"hello from producer"
argument_list|)
decl_stmt|;
name|producer
operator|.
name|send
argument_list|(
name|om
argument_list|)
expr_stmt|;
block|}
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
block|}
block|}
block|}
end_class

end_unit

