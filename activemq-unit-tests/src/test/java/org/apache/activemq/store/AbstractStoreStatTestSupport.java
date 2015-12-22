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
name|store
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
name|Enumeration
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
name|BytesMessage
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
name|QueueBrowser
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
name|javax
operator|.
name|jms
operator|.
name|Topic
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|jms
operator|.
name|TopicSession
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
name|ObjectName
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|management
operator|.
name|openmbean
operator|.
name|CompositeData
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
name|broker
operator|.
name|region
operator|.
name|Destination
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
name|ActiveMQTopic
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
comment|/**  *  *  */
end_comment

begin_class
specifier|public
specifier|abstract
class|class
name|AbstractStoreStatTestSupport
block|{
specifier|protected
specifier|static
specifier|final
name|Logger
name|LOG
init|=
name|LoggerFactory
operator|.
name|getLogger
argument_list|(
name|AbstractStoreStatTestSupport
operator|.
name|class
argument_list|)
decl_stmt|;
specifier|protected
specifier|static
name|int
name|defaultMessageSize
init|=
literal|1000
decl_stmt|;
specifier|protected
specifier|abstract
name|BrokerService
name|getBroker
parameter_list|()
function_decl|;
specifier|protected
specifier|abstract
name|URI
name|getBrokerConnectURI
parameter_list|()
function_decl|;
specifier|protected
name|Destination
name|consumeTestQueueMessages
parameter_list|(
name|String
name|queueName
parameter_list|)
throws|throws
name|Exception
block|{
comment|// create a new queue
specifier|final
name|ActiveMQDestination
name|activeMqQueue
init|=
operator|new
name|ActiveMQQueue
argument_list|(
name|queueName
argument_list|)
decl_stmt|;
name|Destination
name|dest
init|=
name|getBroker
argument_list|()
operator|.
name|getDestination
argument_list|(
name|activeMqQueue
argument_list|)
decl_stmt|;
comment|// Start the connection
name|Connection
name|connection
init|=
operator|new
name|ActiveMQConnectionFactory
argument_list|(
name|getBrokerConnectURI
argument_list|()
argument_list|)
operator|.
name|createConnection
argument_list|()
decl_stmt|;
name|connection
operator|.
name|setClientID
argument_list|(
literal|"clientId2"
operator|+
name|queueName
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
name|QueueSession
operator|.
name|AUTO_ACKNOWLEDGE
argument_list|)
decl_stmt|;
name|Queue
name|queue
init|=
name|session
operator|.
name|createQueue
argument_list|(
name|queueName
argument_list|)
decl_stmt|;
try|try
block|{
name|MessageConsumer
name|consumer
init|=
name|session
operator|.
name|createConsumer
argument_list|(
name|queue
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
literal|200
condition|;
name|i
operator|++
control|)
block|{
name|consumer
operator|.
name|receive
argument_list|()
expr_stmt|;
block|}
block|}
finally|finally
block|{
name|connection
operator|.
name|stop
argument_list|()
expr_stmt|;
block|}
return|return
name|dest
return|;
block|}
specifier|protected
name|Destination
name|browseTestQueueMessages
parameter_list|(
name|String
name|queueName
parameter_list|)
throws|throws
name|Exception
block|{
comment|// create a new queue
specifier|final
name|ActiveMQDestination
name|activeMqQueue
init|=
operator|new
name|ActiveMQQueue
argument_list|(
name|queueName
argument_list|)
decl_stmt|;
name|Destination
name|dest
init|=
name|getBroker
argument_list|()
operator|.
name|getDestination
argument_list|(
name|activeMqQueue
argument_list|)
decl_stmt|;
comment|// Start the connection
name|Connection
name|connection
init|=
operator|new
name|ActiveMQConnectionFactory
argument_list|(
name|getBrokerConnectURI
argument_list|()
argument_list|)
operator|.
name|createConnection
argument_list|()
decl_stmt|;
name|connection
operator|.
name|setClientID
argument_list|(
literal|"clientId2"
operator|+
name|queueName
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
name|QueueSession
operator|.
name|AUTO_ACKNOWLEDGE
argument_list|)
decl_stmt|;
name|Queue
name|queue
init|=
name|session
operator|.
name|createQueue
argument_list|(
name|queueName
argument_list|)
decl_stmt|;
try|try
block|{
name|QueueBrowser
name|queueBrowser
init|=
name|session
operator|.
name|createBrowser
argument_list|(
name|queue
argument_list|)
decl_stmt|;
annotation|@
name|SuppressWarnings
argument_list|(
literal|"unchecked"
argument_list|)
name|Enumeration
argument_list|<
name|Message
argument_list|>
name|messages
init|=
name|queueBrowser
operator|.
name|getEnumeration
argument_list|()
decl_stmt|;
while|while
condition|(
name|messages
operator|.
name|hasMoreElements
argument_list|()
condition|)
block|{
name|messages
operator|.
name|nextElement
argument_list|()
expr_stmt|;
block|}
block|}
finally|finally
block|{
name|connection
operator|.
name|stop
argument_list|()
expr_stmt|;
block|}
return|return
name|dest
return|;
block|}
specifier|protected
name|Destination
name|consumeDurableTestMessages
parameter_list|(
name|Connection
name|connection
parameter_list|,
name|String
name|sub
parameter_list|,
name|int
name|size
parameter_list|,
name|String
name|topicName
parameter_list|,
name|AtomicLong
name|publishedMessageSize
parameter_list|)
throws|throws
name|Exception
block|{
comment|// create a new queue
specifier|final
name|ActiveMQDestination
name|activeMqTopic
init|=
operator|new
name|ActiveMQTopic
argument_list|(
name|topicName
argument_list|)
decl_stmt|;
name|Destination
name|dest
init|=
name|getBroker
argument_list|()
operator|.
name|getDestination
argument_list|(
name|activeMqTopic
argument_list|)
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
name|QueueSession
operator|.
name|AUTO_ACKNOWLEDGE
argument_list|)
decl_stmt|;
name|Topic
name|topic
init|=
name|session
operator|.
name|createTopic
argument_list|(
name|topicName
argument_list|)
decl_stmt|;
try|try
block|{
name|TopicSubscriber
name|consumer
init|=
name|session
operator|.
name|createDurableSubscriber
argument_list|(
name|topic
argument_list|,
name|sub
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
name|size
condition|;
name|i
operator|++
control|)
block|{
name|ActiveMQMessage
name|message
init|=
operator|(
name|ActiveMQMessage
operator|)
name|consumer
operator|.
name|receive
argument_list|()
decl_stmt|;
if|if
condition|(
name|publishedMessageSize
operator|!=
literal|null
condition|)
block|{
name|publishedMessageSize
operator|.
name|addAndGet
argument_list|(
operator|-
name|message
operator|.
name|getSize
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
block|}
finally|finally
block|{
name|session
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
return|return
name|dest
return|;
block|}
specifier|protected
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
name|Queue
name|publishTestQueueMessages
parameter_list|(
name|int
name|count
parameter_list|,
name|String
name|queueName
parameter_list|,
name|int
name|deliveryMode
parameter_list|,
name|int
name|messageSize
parameter_list|,
name|AtomicLong
name|publishedMessageSize
parameter_list|)
throws|throws
name|Exception
block|{
comment|// create a new queue
specifier|final
name|ActiveMQDestination
name|activeMqQueue
init|=
operator|new
name|ActiveMQQueue
argument_list|(
name|queueName
argument_list|)
decl_stmt|;
name|Destination
name|dest
init|=
name|getBroker
argument_list|()
operator|.
name|getDestination
argument_list|(
name|activeMqQueue
argument_list|)
decl_stmt|;
comment|// Start the connection
name|Connection
name|connection
init|=
operator|new
name|ActiveMQConnectionFactory
argument_list|(
name|getBrokerConnectURI
argument_list|()
argument_list|)
operator|.
name|createConnection
argument_list|()
decl_stmt|;
name|connection
operator|.
name|setClientID
argument_list|(
literal|"clientId"
operator|+
name|queueName
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
name|QueueSession
operator|.
name|AUTO_ACKNOWLEDGE
argument_list|)
decl_stmt|;
name|Queue
name|queue
init|=
name|session
operator|.
name|createQueue
argument_list|(
name|queueName
argument_list|)
decl_stmt|;
try|try
block|{
comment|// publish a bunch of non-persistent messages to fill up the temp
comment|// store
name|MessageProducer
name|prod
init|=
name|session
operator|.
name|createProducer
argument_list|(
name|queue
argument_list|)
decl_stmt|;
name|prod
operator|.
name|setDeliveryMode
argument_list|(
name|deliveryMode
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
name|count
condition|;
name|i
operator|++
control|)
block|{
name|prod
operator|.
name|send
argument_list|(
name|createMessage
argument_list|(
name|i
argument_list|,
name|session
argument_list|,
name|messageSize
argument_list|,
name|publishedMessageSize
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
finally|finally
block|{
name|connection
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
return|return
operator|(
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
name|Queue
operator|)
name|dest
return|;
block|}
specifier|protected
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
name|publishTestMessagesDurable
parameter_list|(
name|Connection
name|connection
parameter_list|,
name|String
index|[]
name|subNames
parameter_list|,
name|String
name|topicName
parameter_list|,
name|int
name|publishSize
parameter_list|,
name|int
name|expectedSize
parameter_list|,
name|int
name|messageSize
parameter_list|,
name|AtomicLong
name|publishedMessageSize
parameter_list|,
name|boolean
name|verifyBrowsing
parameter_list|)
throws|throws
name|Exception
block|{
return|return
name|this
operator|.
name|publishTestMessagesDurable
argument_list|(
name|connection
argument_list|,
name|subNames
argument_list|,
name|topicName
argument_list|,
name|publishSize
argument_list|,
name|expectedSize
argument_list|,
name|messageSize
argument_list|,
name|publishedMessageSize
argument_list|,
name|verifyBrowsing
argument_list|,
name|DeliveryMode
operator|.
name|PERSISTENT
argument_list|)
return|;
block|}
specifier|protected
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
name|publishTestMessagesDurable
parameter_list|(
name|Connection
name|connection
parameter_list|,
name|String
index|[]
name|subNames
parameter_list|,
name|String
name|topicName
parameter_list|,
name|int
name|publishSize
parameter_list|,
name|int
name|expectedSize
parameter_list|,
name|int
name|messageSize
parameter_list|,
name|AtomicLong
name|publishedMessageSize
parameter_list|,
name|boolean
name|verifyBrowsing
parameter_list|,
name|int
name|deliveryMode
parameter_list|)
throws|throws
name|Exception
block|{
comment|// create a new queue
specifier|final
name|ActiveMQDestination
name|activeMqTopic
init|=
operator|new
name|ActiveMQTopic
argument_list|(
name|topicName
argument_list|)
decl_stmt|;
name|Destination
name|dest
init|=
name|getBroker
argument_list|()
operator|.
name|getDestination
argument_list|(
name|activeMqTopic
argument_list|)
decl_stmt|;
comment|// Start the connection
name|Session
name|session
init|=
name|connection
operator|.
name|createSession
argument_list|(
literal|false
argument_list|,
name|TopicSession
operator|.
name|AUTO_ACKNOWLEDGE
argument_list|)
decl_stmt|;
name|Topic
name|topic
init|=
name|session
operator|.
name|createTopic
argument_list|(
name|topicName
argument_list|)
decl_stmt|;
for|for
control|(
name|String
name|subName
range|:
name|subNames
control|)
block|{
name|session
operator|.
name|createDurableSubscriber
argument_list|(
name|topic
argument_list|,
name|subName
argument_list|)
expr_stmt|;
block|}
name|ObjectName
index|[]
name|subs
init|=
literal|null
decl_stmt|;
if|if
condition|(
name|verifyBrowsing
condition|)
block|{
comment|// browse the durable sub - this test is to verify that browsing (which calls createTopicMessageStore)
comment|//in KahaDBStore will not create a brand new store (ie uses the cache) If the cache is not used,
comment|//then the statistics won't be updated properly because a new store would overwrite the old store
comment|//which is still in use
name|subs
operator|=
name|getBroker
argument_list|()
operator|.
name|getAdminView
argument_list|()
operator|.
name|getDurableTopicSubscribers
argument_list|()
expr_stmt|;
block|}
try|try
block|{
comment|// publish a bunch of non-persistent messages to fill up the temp
comment|// store
name|MessageProducer
name|prod
init|=
name|session
operator|.
name|createProducer
argument_list|(
name|topic
argument_list|)
decl_stmt|;
name|prod
operator|.
name|setDeliveryMode
argument_list|(
name|deliveryMode
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
name|publishSize
condition|;
name|i
operator|++
control|)
block|{
name|prod
operator|.
name|send
argument_list|(
name|createMessage
argument_list|(
name|i
argument_list|,
name|session
argument_list|,
name|messageSize
argument_list|,
name|publishedMessageSize
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|//verify the view has expected messages
if|if
condition|(
name|verifyBrowsing
condition|)
block|{
name|assertNotNull
argument_list|(
name|subs
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|subNames
operator|.
name|length
argument_list|,
name|subs
operator|.
name|length
argument_list|)
expr_stmt|;
name|ObjectName
name|subName
init|=
name|subs
index|[
literal|0
index|]
decl_stmt|;
name|DurableSubscriptionViewMBean
name|sub
init|=
operator|(
name|DurableSubscriptionViewMBean
operator|)
name|getBroker
argument_list|()
operator|.
name|getManagementContext
argument_list|()
operator|.
name|newProxyInstance
argument_list|(
name|subName
argument_list|,
name|DurableSubscriptionViewMBean
operator|.
name|class
argument_list|,
literal|true
argument_list|)
decl_stmt|;
name|CompositeData
index|[]
name|data
init|=
name|sub
operator|.
name|browse
argument_list|()
decl_stmt|;
name|assertNotNull
argument_list|(
name|data
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|expectedSize
argument_list|,
name|data
operator|.
name|length
argument_list|)
expr_stmt|;
block|}
block|}
finally|finally
block|{
name|session
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
return|return
operator|(
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
operator|)
name|dest
return|;
block|}
comment|/**      * Generate random messages between 100 bytes and maxMessageSize      * @param session      * @return      * @throws JMSException      */
specifier|protected
name|BytesMessage
name|createMessage
parameter_list|(
name|int
name|count
parameter_list|,
name|Session
name|session
parameter_list|,
name|int
name|maxMessageSize
parameter_list|,
name|AtomicLong
name|publishedMessageSize
parameter_list|)
throws|throws
name|JMSException
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
specifier|final
name|Random
name|randomSize
init|=
operator|new
name|Random
argument_list|()
decl_stmt|;
name|int
name|size
init|=
name|randomSize
operator|.
name|nextInt
argument_list|(
operator|(
name|maxMessageSize
operator|-
literal|100
operator|)
operator|+
literal|1
argument_list|)
operator|+
literal|100
decl_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"Creating message to publish: "
operator|+
name|count
operator|+
literal|", size: "
operator|+
name|size
argument_list|)
expr_stmt|;
if|if
condition|(
name|publishedMessageSize
operator|!=
literal|null
condition|)
block|{
name|publishedMessageSize
operator|.
name|addAndGet
argument_list|(
name|size
argument_list|)
expr_stmt|;
block|}
specifier|final
name|byte
index|[]
name|data
init|=
operator|new
name|byte
index|[
name|size
index|]
decl_stmt|;
specifier|final
name|Random
name|rng
init|=
operator|new
name|Random
argument_list|()
decl_stmt|;
name|rng
operator|.
name|nextBytes
argument_list|(
name|data
argument_list|)
expr_stmt|;
name|message
operator|.
name|writeBytes
argument_list|(
name|data
argument_list|)
expr_stmt|;
return|return
name|message
return|;
block|}
block|}
end_class

end_unit

