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
name|File
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
name|ConnectionFactory
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
name|ExceptionListener
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
name|Topic
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|jms
operator|.
name|TopicConnection
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|jms
operator|.
name|TopicConnectionFactory
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|jms
operator|.
name|TopicPublisher
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
name|junit
operator|.
name|framework
operator|.
name|Test
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
name|CombinationTestSupport
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
name|BrokerView
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
name|store
operator|.
name|kahadb
operator|.
name|KahaDBStore
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
name|IOHelper
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

begin_comment
comment|/**  *  A Test case for AMQ-1479  */
end_comment

begin_class
specifier|public
class|class
name|DurableConsumerTest
extends|extends
name|CombinationTestSupport
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
name|DurableConsumerTest
operator|.
name|class
argument_list|)
decl_stmt|;
specifier|private
specifier|static
name|int
name|COUNT
init|=
literal|1024
decl_stmt|;
specifier|private
specifier|static
name|String
name|CONSUMER_NAME
init|=
literal|"DURABLE_TEST"
decl_stmt|;
specifier|protected
name|BrokerService
name|broker
decl_stmt|;
specifier|protected
name|String
name|bindAddress
init|=
literal|"tcp://localhost:61616"
decl_stmt|;
specifier|protected
name|byte
index|[]
name|payload
init|=
operator|new
name|byte
index|[
literal|1024
operator|*
literal|32
index|]
decl_stmt|;
specifier|protected
name|ConnectionFactory
name|factory
decl_stmt|;
specifier|protected
name|Vector
argument_list|<
name|Exception
argument_list|>
name|exceptions
init|=
operator|new
name|Vector
argument_list|<
name|Exception
argument_list|>
argument_list|()
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|String
name|TOPIC_NAME
init|=
literal|"failoverTopic"
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|String
name|CONNECTION_URL
init|=
literal|"failover:(tcp://localhost:61616,tcp://localhost:61617)"
decl_stmt|;
specifier|public
name|boolean
name|useDedicatedTaskRunner
init|=
literal|false
decl_stmt|;
specifier|private
class|class
name|SimpleTopicSubscriber
implements|implements
name|MessageListener
implements|,
name|ExceptionListener
block|{
specifier|private
name|TopicConnection
name|topicConnection
init|=
literal|null
decl_stmt|;
specifier|public
name|SimpleTopicSubscriber
parameter_list|(
name|String
name|connectionURL
parameter_list|,
name|String
name|clientId
parameter_list|,
name|String
name|topicName
parameter_list|)
block|{
name|ActiveMQConnectionFactory
name|topicConnectionFactory
init|=
literal|null
decl_stmt|;
name|TopicSession
name|topicSession
init|=
literal|null
decl_stmt|;
name|Topic
name|topic
init|=
literal|null
decl_stmt|;
name|TopicSubscriber
name|topicSubscriber
init|=
literal|null
decl_stmt|;
name|topicConnectionFactory
operator|=
operator|new
name|ActiveMQConnectionFactory
argument_list|(
name|connectionURL
argument_list|)
expr_stmt|;
try|try
block|{
name|topic
operator|=
operator|new
name|ActiveMQTopic
argument_list|(
name|topicName
argument_list|)
expr_stmt|;
name|topicConnection
operator|=
name|topicConnectionFactory
operator|.
name|createTopicConnection
argument_list|()
expr_stmt|;
name|topicConnection
operator|.
name|setClientID
argument_list|(
operator|(
name|clientId
operator|)
argument_list|)
expr_stmt|;
name|topicConnection
operator|.
name|start
argument_list|()
expr_stmt|;
name|topicSession
operator|=
name|topicConnection
operator|.
name|createTopicSession
argument_list|(
literal|false
argument_list|,
name|Session
operator|.
name|AUTO_ACKNOWLEDGE
argument_list|)
expr_stmt|;
name|topicSubscriber
operator|=
name|topicSession
operator|.
name|createDurableSubscriber
argument_list|(
name|topic
argument_list|,
operator|(
name|clientId
operator|)
argument_list|)
expr_stmt|;
name|topicSubscriber
operator|.
name|setMessageListener
argument_list|(
name|this
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
specifier|public
name|void
name|onMessage
parameter_list|(
name|Message
name|arg0
parameter_list|)
block|{         }
specifier|public
name|void
name|closeConnection
parameter_list|()
block|{
if|if
condition|(
name|topicConnection
operator|!=
literal|null
condition|)
block|{
try|try
block|{
name|topicConnection
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
specifier|public
name|void
name|onException
parameter_list|(
name|JMSException
name|exception
parameter_list|)
block|{
name|exceptions
operator|.
name|add
argument_list|(
name|exception
argument_list|)
expr_stmt|;
block|}
block|}
specifier|private
class|class
name|MessagePublisher
implements|implements
name|Runnable
block|{
specifier|private
specifier|final
name|boolean
name|shouldPublish
init|=
literal|true
decl_stmt|;
specifier|public
name|void
name|run
parameter_list|()
block|{
name|TopicConnectionFactory
name|topicConnectionFactory
init|=
literal|null
decl_stmt|;
name|TopicConnection
name|topicConnection
init|=
literal|null
decl_stmt|;
name|TopicSession
name|topicSession
init|=
literal|null
decl_stmt|;
name|Topic
name|topic
init|=
literal|null
decl_stmt|;
name|TopicPublisher
name|topicPublisher
init|=
literal|null
decl_stmt|;
name|Message
name|message
init|=
literal|null
decl_stmt|;
name|topicConnectionFactory
operator|=
operator|new
name|ActiveMQConnectionFactory
argument_list|(
name|CONNECTION_URL
argument_list|)
expr_stmt|;
try|try
block|{
name|topic
operator|=
operator|new
name|ActiveMQTopic
argument_list|(
name|TOPIC_NAME
argument_list|)
expr_stmt|;
name|topicConnection
operator|=
name|topicConnectionFactory
operator|.
name|createTopicConnection
argument_list|()
expr_stmt|;
name|topicSession
operator|=
name|topicConnection
operator|.
name|createTopicSession
argument_list|(
literal|false
argument_list|,
name|Session
operator|.
name|AUTO_ACKNOWLEDGE
argument_list|)
expr_stmt|;
name|topicPublisher
operator|=
name|topicSession
operator|.
name|createPublisher
argument_list|(
name|topic
argument_list|)
expr_stmt|;
name|message
operator|=
name|topicSession
operator|.
name|createMessage
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|ex
parameter_list|)
block|{
name|exceptions
operator|.
name|add
argument_list|(
name|ex
argument_list|)
expr_stmt|;
block|}
while|while
condition|(
name|shouldPublish
condition|)
block|{
try|try
block|{
name|topicPublisher
operator|.
name|publish
argument_list|(
name|message
argument_list|,
name|DeliveryMode
operator|.
name|PERSISTENT
argument_list|,
literal|1
argument_list|,
literal|2
operator|*
literal|60
operator|*
literal|60
operator|*
literal|1000
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|JMSException
name|ex
parameter_list|)
block|{
name|exceptions
operator|.
name|add
argument_list|(
name|ex
argument_list|)
expr_stmt|;
block|}
try|try
block|{
name|Thread
operator|.
name|sleep
argument_list|(
literal|1
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|ex
parameter_list|)
block|{                 }
block|}
block|}
block|}
specifier|private
name|void
name|configurePersistence
parameter_list|(
name|BrokerService
name|broker
parameter_list|)
throws|throws
name|Exception
block|{
name|File
name|dataDirFile
init|=
operator|new
name|File
argument_list|(
literal|"target/"
operator|+
name|getName
argument_list|()
argument_list|)
decl_stmt|;
name|KahaDBPersistenceAdapter
name|kahaDBAdapter
init|=
operator|new
name|KahaDBPersistenceAdapter
argument_list|()
decl_stmt|;
name|kahaDBAdapter
operator|.
name|setDirectory
argument_list|(
name|dataDirFile
argument_list|)
expr_stmt|;
name|broker
operator|.
name|setPersistenceAdapter
argument_list|(
name|kahaDBAdapter
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|testFailover
parameter_list|()
throws|throws
name|Exception
block|{
name|configurePersistence
argument_list|(
name|broker
argument_list|)
expr_stmt|;
name|broker
operator|.
name|start
argument_list|()
expr_stmt|;
name|Thread
name|publisherThread
init|=
operator|new
name|Thread
argument_list|(
operator|new
name|MessagePublisher
argument_list|()
argument_list|)
decl_stmt|;
name|publisherThread
operator|.
name|start
argument_list|()
expr_stmt|;
specifier|final
name|int
name|numSubs
init|=
literal|100
decl_stmt|;
specifier|final
name|List
argument_list|<
name|SimpleTopicSubscriber
argument_list|>
name|list
init|=
operator|new
name|ArrayList
argument_list|<
name|SimpleTopicSubscriber
argument_list|>
argument_list|(
name|numSubs
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
name|numSubs
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
name|Thread
name|thread
init|=
operator|new
name|Thread
argument_list|(
operator|new
name|Runnable
argument_list|()
block|{
specifier|public
name|void
name|run
parameter_list|()
block|{
name|SimpleTopicSubscriber
name|s
init|=
operator|new
name|SimpleTopicSubscriber
argument_list|(
name|CONNECTION_URL
argument_list|,
name|System
operator|.
name|currentTimeMillis
argument_list|()
operator|+
literal|"-"
operator|+
name|id
argument_list|,
name|TOPIC_NAME
argument_list|)
decl_stmt|;
name|list
operator|.
name|add
argument_list|(
name|s
argument_list|)
expr_stmt|;
block|}
block|}
argument_list|)
decl_stmt|;
name|thread
operator|.
name|start
argument_list|()
expr_stmt|;
block|}
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
name|numSubs
operator|==
name|list
operator|.
name|size
argument_list|()
return|;
block|}
block|}
argument_list|)
expr_stmt|;
name|broker
operator|.
name|stop
argument_list|()
expr_stmt|;
name|broker
operator|=
name|createBroker
argument_list|(
literal|false
argument_list|)
expr_stmt|;
name|configurePersistence
argument_list|(
name|broker
argument_list|)
expr_stmt|;
name|broker
operator|.
name|start
argument_list|()
expr_stmt|;
name|Thread
operator|.
name|sleep
argument_list|(
literal|10000
argument_list|)
expr_stmt|;
for|for
control|(
name|SimpleTopicSubscriber
name|s
range|:
name|list
control|)
block|{
name|s
operator|.
name|closeConnection
argument_list|()
expr_stmt|;
block|}
name|assertTrue
argument_list|(
literal|"no exceptions: "
operator|+
name|exceptions
argument_list|,
name|exceptions
operator|.
name|isEmpty
argument_list|()
argument_list|)
expr_stmt|;
block|}
comment|// makes heavy use of threads and can demonstrate https://issues.apache.org/activemq/browse/AMQ-2028
comment|// with use dedicatedTaskRunner=true and produce OOM
specifier|public
name|void
name|initCombosForTestConcurrentDurableConsumer
parameter_list|()
block|{
name|addCombinationValues
argument_list|(
literal|"useDedicatedTaskRunner"
argument_list|,
operator|new
name|Object
index|[]
block|{
name|Boolean
operator|.
name|TRUE
block|,
name|Boolean
operator|.
name|FALSE
block|}
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|testConcurrentDurableConsumer
parameter_list|()
throws|throws
name|Exception
block|{
name|broker
operator|.
name|start
argument_list|()
expr_stmt|;
name|factory
operator|=
name|createConnectionFactory
argument_list|()
expr_stmt|;
specifier|final
name|String
name|topicName
init|=
name|getName
argument_list|()
decl_stmt|;
specifier|final
name|int
name|numMessages
init|=
literal|500
decl_stmt|;
name|int
name|numConsumers
init|=
literal|1
decl_stmt|;
specifier|final
name|CountDownLatch
name|counsumerStarted
init|=
operator|new
name|CountDownLatch
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
argument_list|()
decl_stmt|;
name|Runnable
name|consumer
init|=
operator|new
name|Runnable
argument_list|()
block|{
specifier|public
name|void
name|run
parameter_list|()
block|{
specifier|final
name|String
name|consumerName
init|=
name|Thread
operator|.
name|currentThread
argument_list|()
operator|.
name|getName
argument_list|()
decl_stmt|;
name|int
name|acked
init|=
literal|0
decl_stmt|;
name|int
name|received
init|=
literal|0
decl_stmt|;
try|try
block|{
while|while
condition|(
name|acked
operator|<
name|numMessages
operator|/
literal|2
condition|)
block|{
comment|// take one message and close, ack on occasion
name|Connection
name|consumerConnection
init|=
name|factory
operator|.
name|createConnection
argument_list|()
decl_stmt|;
operator|(
operator|(
name|ActiveMQConnection
operator|)
name|consumerConnection
operator|)
operator|.
name|setWatchTopicAdvisories
argument_list|(
literal|false
argument_list|)
expr_stmt|;
name|consumerConnection
operator|.
name|setClientID
argument_list|(
name|consumerName
argument_list|)
expr_stmt|;
name|Session
name|consumerSession
init|=
name|consumerConnection
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
name|Topic
name|topic
init|=
name|consumerSession
operator|.
name|createTopic
argument_list|(
name|topicName
argument_list|)
decl_stmt|;
name|consumerConnection
operator|.
name|start
argument_list|()
expr_stmt|;
name|MessageConsumer
name|consumer
init|=
name|consumerSession
operator|.
name|createDurableSubscriber
argument_list|(
name|topic
argument_list|,
name|consumerName
argument_list|)
decl_stmt|;
name|counsumerStarted
operator|.
name|countDown
argument_list|()
expr_stmt|;
name|Message
name|msg
init|=
literal|null
decl_stmt|;
do|do
block|{
name|msg
operator|=
name|consumer
operator|.
name|receive
argument_list|(
literal|5000
argument_list|)
expr_stmt|;
if|if
condition|(
name|msg
operator|!=
literal|null
condition|)
block|{
name|receivedCount
operator|.
name|incrementAndGet
argument_list|()
expr_stmt|;
if|if
condition|(
name|received
operator|!=
literal|0
operator|&&
name|received
operator|%
literal|100
operator|==
literal|0
condition|)
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"Received msg: "
operator|+
name|msg
operator|.
name|getJMSMessageID
argument_list|()
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
operator|++
name|received
operator|%
literal|2
operator|==
literal|0
condition|)
block|{
name|msg
operator|.
name|acknowledge
argument_list|()
expr_stmt|;
name|acked
operator|++
expr_stmt|;
block|}
block|}
block|}
do|while
condition|(
name|msg
operator|==
literal|null
condition|)
do|;
name|consumerConnection
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
name|assertTrue
argument_list|(
name|received
operator|>=
name|acked
argument_list|)
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
name|exceptions
operator|.
name|add
argument_list|(
name|e
argument_list|)
expr_stmt|;
block|}
block|}
block|}
decl_stmt|;
name|ExecutorService
name|executor
init|=
name|Executors
operator|.
name|newFixedThreadPool
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
name|executor
operator|.
name|execute
argument_list|(
name|consumer
argument_list|)
expr_stmt|;
block|}
name|assertTrue
argument_list|(
name|counsumerStarted
operator|.
name|await
argument_list|(
literal|30
argument_list|,
name|TimeUnit
operator|.
name|SECONDS
argument_list|)
argument_list|)
expr_stmt|;
name|Connection
name|producerConnection
init|=
name|factory
operator|.
name|createConnection
argument_list|()
decl_stmt|;
operator|(
operator|(
name|ActiveMQConnection
operator|)
name|producerConnection
operator|)
operator|.
name|setWatchTopicAdvisories
argument_list|(
literal|false
argument_list|)
expr_stmt|;
name|Session
name|producerSession
init|=
name|producerConnection
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
name|Topic
name|topic
init|=
name|producerSession
operator|.
name|createTopic
argument_list|(
name|topicName
argument_list|)
decl_stmt|;
name|MessageProducer
name|producer
init|=
name|producerSession
operator|.
name|createProducer
argument_list|(
name|topic
argument_list|)
decl_stmt|;
name|producerConnection
operator|.
name|start
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
name|numMessages
condition|;
name|i
operator|++
control|)
block|{
name|BytesMessage
name|msg
init|=
name|producerSession
operator|.
name|createBytesMessage
argument_list|()
decl_stmt|;
name|msg
operator|.
name|writeBytes
argument_list|(
name|payload
argument_list|)
expr_stmt|;
name|producer
operator|.
name|send
argument_list|(
name|msg
argument_list|)
expr_stmt|;
if|if
condition|(
name|i
operator|!=
literal|0
operator|&&
name|i
operator|%
literal|100
operator|==
literal|0
condition|)
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"Sent msg "
operator|+
name|i
argument_list|)
expr_stmt|;
block|}
block|}
name|executor
operator|.
name|shutdown
argument_list|()
expr_stmt|;
name|executor
operator|.
name|awaitTermination
argument_list|(
literal|30
argument_list|,
name|TimeUnit
operator|.
name|SECONDS
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
literal|"receivedCount: "
operator|+
name|receivedCount
operator|.
name|get
argument_list|()
argument_list|)
expr_stmt|;
return|return
name|receivedCount
operator|.
name|get
argument_list|()
operator|==
name|numMessages
return|;
block|}
block|}
argument_list|,
literal|360
operator|*
literal|1000
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"got required some messages"
argument_list|,
name|numMessages
argument_list|,
name|receivedCount
operator|.
name|get
argument_list|()
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
literal|"no exceptions, but: "
operator|+
name|exceptions
argument_list|,
name|exceptions
operator|.
name|isEmpty
argument_list|()
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|testConsumerRecover
parameter_list|()
throws|throws
name|Exception
block|{
name|doTestConsumer
argument_list|(
literal|true
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|testConsumer
parameter_list|()
throws|throws
name|Exception
block|{
name|doTestConsumer
argument_list|(
literal|false
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|testPrefetchViaBrokerConfig
parameter_list|()
throws|throws
name|Exception
block|{
name|Integer
name|prefetchVal
init|=
operator|new
name|Integer
argument_list|(
literal|150
argument_list|)
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
name|setDurableTopicPrefetch
argument_list|(
name|prefetchVal
operator|.
name|intValue
argument_list|()
argument_list|)
expr_stmt|;
name|policyEntry
operator|.
name|setPrioritizedMessages
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
name|policyMap
operator|.
name|setDefaultEntry
argument_list|(
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
name|broker
operator|.
name|start
argument_list|()
expr_stmt|;
name|factory
operator|=
name|createConnectionFactory
argument_list|()
expr_stmt|;
name|Connection
name|consumerConnection
init|=
name|factory
operator|.
name|createConnection
argument_list|()
decl_stmt|;
name|consumerConnection
operator|.
name|setClientID
argument_list|(
name|CONSUMER_NAME
argument_list|)
expr_stmt|;
name|Session
name|consumerSession
init|=
name|consumerConnection
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
name|Topic
name|topic
init|=
name|consumerSession
operator|.
name|createTopic
argument_list|(
name|getClass
argument_list|()
operator|.
name|getName
argument_list|()
argument_list|)
decl_stmt|;
name|MessageConsumer
name|consumer
init|=
name|consumerSession
operator|.
name|createDurableSubscriber
argument_list|(
name|topic
argument_list|,
name|CONSUMER_NAME
argument_list|)
decl_stmt|;
name|consumerConnection
operator|.
name|start
argument_list|()
expr_stmt|;
name|ObjectName
name|activeSubscriptionObjectName
init|=
name|broker
operator|.
name|getAdminView
argument_list|()
operator|.
name|getDurableTopicSubscribers
argument_list|()
index|[
literal|0
index|]
decl_stmt|;
name|Object
name|prefetchFromSubView
init|=
name|broker
operator|.
name|getManagementContext
argument_list|()
operator|.
name|getAttribute
argument_list|(
name|activeSubscriptionObjectName
argument_list|,
literal|"PrefetchSize"
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
name|prefetchVal
argument_list|,
name|prefetchFromSubView
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|doTestConsumer
parameter_list|(
name|boolean
name|forceRecover
parameter_list|)
throws|throws
name|Exception
block|{
if|if
condition|(
name|forceRecover
condition|)
block|{
name|configurePersistence
argument_list|(
name|broker
argument_list|)
expr_stmt|;
block|}
name|broker
operator|.
name|start
argument_list|()
expr_stmt|;
name|factory
operator|=
name|createConnectionFactory
argument_list|()
expr_stmt|;
name|Connection
name|consumerConnection
init|=
name|factory
operator|.
name|createConnection
argument_list|()
decl_stmt|;
name|consumerConnection
operator|.
name|setClientID
argument_list|(
name|CONSUMER_NAME
argument_list|)
expr_stmt|;
name|Session
name|consumerSession
init|=
name|consumerConnection
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
name|Topic
name|topic
init|=
name|consumerSession
operator|.
name|createTopic
argument_list|(
name|getClass
argument_list|()
operator|.
name|getName
argument_list|()
argument_list|)
decl_stmt|;
name|MessageConsumer
name|consumer
init|=
name|consumerSession
operator|.
name|createDurableSubscriber
argument_list|(
name|topic
argument_list|,
name|CONSUMER_NAME
argument_list|)
decl_stmt|;
name|consumerConnection
operator|.
name|start
argument_list|()
expr_stmt|;
name|consumerConnection
operator|.
name|close
argument_list|()
expr_stmt|;
name|broker
operator|.
name|stop
argument_list|()
expr_stmt|;
name|broker
operator|=
name|createBroker
argument_list|(
literal|false
argument_list|)
expr_stmt|;
if|if
condition|(
name|forceRecover
condition|)
block|{
name|configurePersistence
argument_list|(
name|broker
argument_list|)
expr_stmt|;
block|}
name|broker
operator|.
name|start
argument_list|()
expr_stmt|;
name|Connection
name|producerConnection
init|=
name|factory
operator|.
name|createConnection
argument_list|()
decl_stmt|;
name|Session
name|producerSession
init|=
name|producerConnection
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
name|producerSession
operator|.
name|createProducer
argument_list|(
name|topic
argument_list|)
decl_stmt|;
name|producerConnection
operator|.
name|start
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
name|COUNT
condition|;
name|i
operator|++
control|)
block|{
name|BytesMessage
name|msg
init|=
name|producerSession
operator|.
name|createBytesMessage
argument_list|()
decl_stmt|;
name|msg
operator|.
name|writeBytes
argument_list|(
name|payload
argument_list|)
expr_stmt|;
name|producer
operator|.
name|send
argument_list|(
name|msg
argument_list|)
expr_stmt|;
if|if
condition|(
name|i
operator|!=
literal|0
operator|&&
name|i
operator|%
literal|1000
operator|==
literal|0
condition|)
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"Sent msg "
operator|+
name|i
argument_list|)
expr_stmt|;
block|}
block|}
name|producerConnection
operator|.
name|close
argument_list|()
expr_stmt|;
name|broker
operator|.
name|stop
argument_list|()
expr_stmt|;
name|broker
operator|=
name|createBroker
argument_list|(
literal|false
argument_list|)
expr_stmt|;
if|if
condition|(
name|forceRecover
condition|)
block|{
name|configurePersistence
argument_list|(
name|broker
argument_list|)
expr_stmt|;
block|}
name|broker
operator|.
name|start
argument_list|()
expr_stmt|;
name|consumerConnection
operator|=
name|factory
operator|.
name|createConnection
argument_list|()
expr_stmt|;
name|consumerConnection
operator|.
name|setClientID
argument_list|(
name|CONSUMER_NAME
argument_list|)
expr_stmt|;
name|consumerConnection
operator|.
name|start
argument_list|()
expr_stmt|;
name|consumerSession
operator|=
name|consumerConnection
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
name|consumer
operator|=
name|consumerSession
operator|.
name|createDurableSubscriber
argument_list|(
name|topic
argument_list|,
name|CONSUMER_NAME
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
name|COUNT
condition|;
name|i
operator|++
control|)
block|{
name|Message
name|msg
init|=
name|consumer
operator|.
name|receive
argument_list|(
literal|10000
argument_list|)
decl_stmt|;
name|assertNotNull
argument_list|(
literal|"Missing message: "
operator|+
name|i
argument_list|,
name|msg
argument_list|)
expr_stmt|;
if|if
condition|(
name|i
operator|!=
literal|0
operator|&&
name|i
operator|%
literal|1000
operator|==
literal|0
condition|)
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"Received msg "
operator|+
name|i
argument_list|)
expr_stmt|;
block|}
block|}
name|consumerConnection
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Override
specifier|protected
name|void
name|setUp
parameter_list|()
throws|throws
name|Exception
block|{
if|if
condition|(
name|broker
operator|==
literal|null
condition|)
block|{
name|broker
operator|=
name|createBroker
argument_list|(
literal|true
argument_list|)
expr_stmt|;
block|}
name|super
operator|.
name|setUp
argument_list|()
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
name|super
operator|.
name|tearDown
argument_list|()
expr_stmt|;
if|if
condition|(
name|broker
operator|!=
literal|null
condition|)
block|{
name|broker
operator|.
name|stop
argument_list|()
expr_stmt|;
name|broker
operator|=
literal|null
expr_stmt|;
block|}
block|}
specifier|protected
name|Topic
name|creatTopic
parameter_list|(
name|Session
name|s
parameter_list|,
name|String
name|destinationName
parameter_list|)
throws|throws
name|JMSException
block|{
return|return
name|s
operator|.
name|createTopic
argument_list|(
name|destinationName
argument_list|)
return|;
block|}
comment|/**      * Factory method to create a new broker      *       * @throws Exception      */
specifier|protected
name|BrokerService
name|createBroker
parameter_list|(
name|boolean
name|deleteStore
parameter_list|)
throws|throws
name|Exception
block|{
name|BrokerService
name|answer
init|=
operator|new
name|BrokerService
argument_list|()
decl_stmt|;
name|configureBroker
argument_list|(
name|answer
argument_list|,
name|deleteStore
argument_list|)
expr_stmt|;
return|return
name|answer
return|;
block|}
specifier|protected
name|void
name|configureBroker
parameter_list|(
name|BrokerService
name|answer
parameter_list|,
name|boolean
name|deleteStore
parameter_list|)
throws|throws
name|Exception
block|{
name|answer
operator|.
name|setDeleteAllMessagesOnStartup
argument_list|(
name|deleteStore
argument_list|)
expr_stmt|;
name|KahaDBStore
name|kaha
init|=
operator|new
name|KahaDBStore
argument_list|()
decl_stmt|;
comment|//kaha.setConcurrentStoreAndDispatchTopics(false);
name|File
name|directory
init|=
operator|new
name|File
argument_list|(
literal|"target/activemq-data/kahadb"
argument_list|)
decl_stmt|;
if|if
condition|(
name|deleteStore
condition|)
block|{
name|IOHelper
operator|.
name|deleteChildren
argument_list|(
name|directory
argument_list|)
expr_stmt|;
block|}
name|kaha
operator|.
name|setDirectory
argument_list|(
name|directory
argument_list|)
expr_stmt|;
comment|//kaha.setMaxAsyncJobs(10);
name|answer
operator|.
name|setPersistenceAdapter
argument_list|(
name|kaha
argument_list|)
expr_stmt|;
name|answer
operator|.
name|addConnector
argument_list|(
name|bindAddress
argument_list|)
expr_stmt|;
name|answer
operator|.
name|setUseShutdownHook
argument_list|(
literal|false
argument_list|)
expr_stmt|;
name|answer
operator|.
name|setAdvisorySupport
argument_list|(
literal|false
argument_list|)
expr_stmt|;
name|answer
operator|.
name|setDedicatedTaskRunner
argument_list|(
name|useDedicatedTaskRunner
argument_list|)
expr_stmt|;
block|}
specifier|protected
name|ActiveMQConnectionFactory
name|createConnectionFactory
parameter_list|()
throws|throws
name|Exception
block|{
name|ActiveMQConnectionFactory
name|factory
init|=
operator|new
name|ActiveMQConnectionFactory
argument_list|(
name|bindAddress
argument_list|)
decl_stmt|;
name|factory
operator|.
name|setUseDedicatedTaskRunner
argument_list|(
name|useDedicatedTaskRunner
argument_list|)
expr_stmt|;
return|return
name|factory
return|;
block|}
specifier|public
specifier|static
name|Test
name|suite
parameter_list|()
block|{
return|return
name|suite
argument_list|(
name|DurableConsumerTest
operator|.
name|class
argument_list|)
return|;
block|}
specifier|public
specifier|static
name|void
name|main
parameter_list|(
name|String
index|[]
name|args
parameter_list|)
block|{
name|junit
operator|.
name|textui
operator|.
name|TestRunner
operator|.
name|run
argument_list|(
name|suite
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

