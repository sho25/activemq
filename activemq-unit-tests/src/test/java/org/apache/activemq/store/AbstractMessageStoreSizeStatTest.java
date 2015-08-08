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
name|Random
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
name|javax
operator|.
name|management
operator|.
name|openmbean
operator|.
name|TabularData
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
name|activemq
operator|.
name|util
operator|.
name|Wait
operator|.
name|Condition
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
comment|/**  * This test checks that KahaDB properly sets the new storeMessageSize statistic.  *  * AMQ-5748  *  */
end_comment

begin_class
specifier|public
specifier|abstract
class|class
name|AbstractMessageStoreSizeStatTest
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
name|AbstractMessageStoreSizeStatTest
operator|.
name|class
argument_list|)
decl_stmt|;
specifier|protected
name|BrokerService
name|broker
decl_stmt|;
specifier|protected
name|URI
name|brokerConnectURI
decl_stmt|;
specifier|protected
name|String
name|defaultQueueName
init|=
literal|"test.queue"
decl_stmt|;
specifier|protected
name|String
name|defaultTopicName
init|=
literal|"test.topic"
decl_stmt|;
specifier|protected
specifier|static
name|int
name|messageSize
init|=
literal|1000
decl_stmt|;
annotation|@
name|Before
specifier|public
name|void
name|startBroker
parameter_list|()
throws|throws
name|Exception
block|{
name|setUpBroker
argument_list|(
literal|true
argument_list|)
expr_stmt|;
block|}
specifier|protected
name|void
name|setUpBroker
parameter_list|(
name|boolean
name|clearDataDir
parameter_list|)
throws|throws
name|Exception
block|{
name|broker
operator|=
operator|new
name|BrokerService
argument_list|()
expr_stmt|;
name|this
operator|.
name|initPersistence
argument_list|(
name|broker
argument_list|)
expr_stmt|;
comment|//set up a transport
name|TransportConnector
name|connector
init|=
name|broker
operator|.
name|addConnector
argument_list|(
operator|new
name|TransportConnector
argument_list|()
argument_list|)
decl_stmt|;
name|connector
operator|.
name|setUri
argument_list|(
operator|new
name|URI
argument_list|(
literal|"tcp://0.0.0.0:0"
argument_list|)
argument_list|)
expr_stmt|;
name|connector
operator|.
name|setName
argument_list|(
literal|"tcp"
argument_list|)
expr_stmt|;
name|broker
operator|.
name|start
argument_list|()
expr_stmt|;
name|broker
operator|.
name|waitUntilStarted
argument_list|()
expr_stmt|;
name|brokerConnectURI
operator|=
name|broker
operator|.
name|getConnectorByName
argument_list|(
literal|"tcp"
argument_list|)
operator|.
name|getConnectUri
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
specifier|protected
specifier|abstract
name|void
name|initPersistence
parameter_list|(
name|BrokerService
name|brokerService
parameter_list|)
throws|throws
name|IOException
function_decl|;
annotation|@
name|Test
specifier|public
name|void
name|testMessageSize
parameter_list|()
throws|throws
name|Exception
block|{
name|Destination
name|dest
init|=
name|publishTestQueueMessages
argument_list|(
literal|200
argument_list|)
decl_stmt|;
name|verifyStats
argument_list|(
name|dest
argument_list|,
literal|200
argument_list|,
literal|200
operator|*
name|messageSize
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testMessageSizeAfterConsumption
parameter_list|()
throws|throws
name|Exception
block|{
name|Destination
name|dest
init|=
name|publishTestQueueMessages
argument_list|(
literal|200
argument_list|)
decl_stmt|;
name|verifyStats
argument_list|(
name|dest
argument_list|,
literal|200
argument_list|,
literal|200
operator|*
name|messageSize
argument_list|)
expr_stmt|;
name|consumeTestQueueMessages
argument_list|()
expr_stmt|;
name|verifyStats
argument_list|(
name|dest
argument_list|,
literal|0
argument_list|,
literal|0
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testMessageSizeOneDurable
parameter_list|()
throws|throws
name|Exception
block|{
name|Connection
name|connection
init|=
operator|new
name|ActiveMQConnectionFactory
argument_list|(
name|brokerConnectURI
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
argument_list|)
expr_stmt|;
name|connection
operator|.
name|start
argument_list|()
expr_stmt|;
name|Destination
name|dest
init|=
name|publishTestMessagesDurable
argument_list|(
name|connection
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"sub1"
block|}
argument_list|,
literal|200
argument_list|,
literal|200
argument_list|)
decl_stmt|;
comment|//verify the count and size
name|verifyStats
argument_list|(
name|dest
argument_list|,
literal|200
argument_list|,
literal|200
operator|*
name|messageSize
argument_list|)
expr_stmt|;
comment|//consume all messages
name|consumeDurableTestMessages
argument_list|(
name|connection
argument_list|,
literal|"sub1"
argument_list|,
literal|200
argument_list|)
expr_stmt|;
comment|//All messages should now be gone
name|verifyStats
argument_list|(
name|dest
argument_list|,
literal|0
argument_list|,
literal|0
argument_list|)
expr_stmt|;
name|connection
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
argument_list|(
name|timeout
operator|=
literal|10000
argument_list|)
specifier|public
name|void
name|testMessageSizeTwoDurables
parameter_list|()
throws|throws
name|Exception
block|{
name|Connection
name|connection
init|=
operator|new
name|ActiveMQConnectionFactory
argument_list|(
name|brokerConnectURI
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
argument_list|)
expr_stmt|;
name|connection
operator|.
name|start
argument_list|()
expr_stmt|;
name|Destination
name|dest
init|=
name|publishTestMessagesDurable
argument_list|(
name|connection
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"sub1"
block|,
literal|"sub2"
block|}
argument_list|,
literal|200
argument_list|,
literal|200
argument_list|)
decl_stmt|;
comment|//verify the count and size
name|verifyStats
argument_list|(
name|dest
argument_list|,
literal|200
argument_list|,
literal|200
operator|*
name|messageSize
argument_list|)
expr_stmt|;
comment|//consume messages just for sub1
name|consumeDurableTestMessages
argument_list|(
name|connection
argument_list|,
literal|"sub1"
argument_list|,
literal|200
argument_list|)
expr_stmt|;
comment|//There is still a durable that hasn't consumed so the messages should exist
name|verifyStats
argument_list|(
name|dest
argument_list|,
literal|200
argument_list|,
literal|200
operator|*
name|messageSize
argument_list|)
expr_stmt|;
name|connection
operator|.
name|stop
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testMessageSizeAfterDestinationDeletion
parameter_list|()
throws|throws
name|Exception
block|{
name|Destination
name|dest
init|=
name|publishTestQueueMessages
argument_list|(
literal|200
argument_list|)
decl_stmt|;
name|verifyStats
argument_list|(
name|dest
argument_list|,
literal|200
argument_list|,
literal|200
operator|*
name|messageSize
argument_list|)
expr_stmt|;
comment|//check that the size is 0 after deletion
name|broker
operator|.
name|removeDestination
argument_list|(
name|dest
operator|.
name|getActiveMQDestination
argument_list|()
argument_list|)
expr_stmt|;
name|verifyStats
argument_list|(
name|dest
argument_list|,
literal|0
argument_list|,
literal|0
argument_list|)
expr_stmt|;
block|}
specifier|protected
name|void
name|verifyStats
parameter_list|(
name|Destination
name|dest
parameter_list|,
specifier|final
name|int
name|count
parameter_list|,
specifier|final
name|long
name|minimumSize
parameter_list|)
throws|throws
name|Exception
block|{
specifier|final
name|MessageStore
name|messageStore
init|=
name|dest
operator|.
name|getMessageStore
argument_list|()
decl_stmt|;
specifier|final
name|MessageStoreStatistics
name|storeStats
init|=
name|dest
operator|.
name|getMessageStore
argument_list|()
operator|.
name|getMessageStoreStatistics
argument_list|()
decl_stmt|;
name|Wait
operator|.
name|waitFor
argument_list|(
operator|new
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
operator|(
name|count
operator|==
name|messageStore
operator|.
name|getMessageCount
argument_list|()
operator|)
operator|&&
operator|(
name|messageStore
operator|.
name|getMessageCount
argument_list|()
operator|==
name|storeStats
operator|.
name|getMessageCount
argument_list|()
operator|.
name|getCount
argument_list|()
operator|)
operator|&&
operator|(
name|messageStore
operator|.
name|getMessageSize
argument_list|()
operator|==
name|messageStore
operator|.
name|getMessageStoreStatistics
argument_list|()
operator|.
name|getMessageSize
argument_list|()
operator|.
name|getTotalSize
argument_list|()
operator|)
return|;
block|}
block|}
argument_list|)
expr_stmt|;
if|if
condition|(
name|count
operator|>
literal|0
condition|)
block|{
name|assertTrue
argument_list|(
name|storeStats
operator|.
name|getMessageSize
argument_list|()
operator|.
name|getTotalSize
argument_list|()
operator|>
name|minimumSize
argument_list|)
expr_stmt|;
name|Wait
operator|.
name|waitFor
argument_list|(
operator|new
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
name|storeStats
operator|.
name|getMessageSize
argument_list|()
operator|.
name|getTotalSize
argument_list|()
operator|>
name|minimumSize
return|;
block|}
block|}
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|Wait
operator|.
name|waitFor
argument_list|(
operator|new
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
name|storeStats
operator|.
name|getMessageSize
argument_list|()
operator|.
name|getTotalSize
argument_list|()
operator|==
literal|0
return|;
block|}
block|}
argument_list|)
expr_stmt|;
block|}
block|}
comment|/**      * Generate random 1 megabyte messages      * @param session      * @return      * @throws JMSException      */
specifier|protected
name|BytesMessage
name|createMessage
parameter_list|(
name|Session
name|session
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
name|byte
index|[]
name|data
init|=
operator|new
name|byte
index|[
name|messageSize
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
specifier|protected
name|Destination
name|publishTestQueueMessages
parameter_list|(
name|int
name|count
parameter_list|)
throws|throws
name|Exception
block|{
return|return
name|publishTestQueueMessages
argument_list|(
name|count
argument_list|,
name|defaultQueueName
argument_list|)
return|;
block|}
specifier|protected
name|Destination
name|publishTestQueueMessages
parameter_list|(
name|int
name|count
parameter_list|,
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
name|broker
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
name|brokerConnectURI
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
name|session
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
name|dest
return|;
block|}
specifier|protected
name|Destination
name|consumeTestQueueMessages
parameter_list|()
throws|throws
name|Exception
block|{
return|return
name|consumeTestQueueMessages
argument_list|(
name|defaultQueueName
argument_list|)
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
parameter_list|)
throws|throws
name|Exception
block|{
return|return
name|consumeDurableTestMessages
argument_list|(
name|connection
argument_list|,
name|sub
argument_list|,
name|size
argument_list|,
name|defaultTopicName
argument_list|)
return|;
block|}
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
name|broker
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
name|brokerConnectURI
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
name|broker
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
name|consumer
operator|.
name|receive
argument_list|()
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
name|dest
return|;
block|}
specifier|protected
name|Destination
name|publishTestMessagesDurable
parameter_list|(
name|Connection
name|connection
parameter_list|,
name|String
index|[]
name|subNames
parameter_list|,
name|int
name|publishSize
parameter_list|,
name|int
name|expectedSize
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
name|defaultTopicName
argument_list|)
decl_stmt|;
name|Destination
name|dest
init|=
name|broker
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
name|defaultTopicName
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
comment|// browse the durable sub - this test is to verify that browsing (which calls createTopicMessageStore)
comment|//in KahaDBStore will not create a brand new store (ie uses the cache) If the cache is not used,
comment|//then the statistics won't be updated properly because a new store would overwrite the old store
comment|//which is still in use
name|ObjectName
index|[]
name|subs
init|=
name|broker
operator|.
name|getAdminView
argument_list|()
operator|.
name|getDurableTopicSubscribers
argument_list|()
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
name|topic
argument_list|)
decl_stmt|;
name|prod
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
name|session
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|//verify the view has expected messages
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
name|broker
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
block|}
end_class

end_unit

