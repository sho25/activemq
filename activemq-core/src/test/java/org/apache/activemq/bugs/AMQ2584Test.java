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
name|util
operator|.
name|Properties
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
name|activemq
operator|.
name|util
operator|.
name|IntrospectionSupport
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
name|AMQ2584Test
extends|extends
name|org
operator|.
name|apache
operator|.
name|activemq
operator|.
name|TestSupport
block|{
specifier|static
specifier|final
name|Logger
name|LOG
init|=
name|LoggerFactory
operator|.
name|getLogger
argument_list|(
name|AMQ2584Test
operator|.
name|class
argument_list|)
decl_stmt|;
name|BrokerService
name|broker
init|=
literal|null
decl_stmt|;
name|ActiveMQTopic
name|topic
decl_stmt|;
name|ActiveMQConnection
name|consumerConnection
init|=
literal|null
decl_stmt|,
name|producerConnection
init|=
literal|null
decl_stmt|;
name|Session
name|producerSession
decl_stmt|;
name|MessageProducer
name|producer
decl_stmt|;
specifier|final
name|int
name|minPercentUsageForStore
init|=
literal|10
decl_stmt|;
name|String
name|data
decl_stmt|;
specifier|public
specifier|static
name|Test
name|suite
parameter_list|()
block|{
return|return
name|suite
argument_list|(
name|AMQ2584Test
operator|.
name|class
argument_list|)
return|;
block|}
specifier|public
name|void
name|initCombosForTestSize
parameter_list|()
throws|throws
name|Exception
block|{
name|this
operator|.
name|addCombinationValues
argument_list|(
literal|"defaultPersistenceAdapter"
argument_list|,
operator|new
name|Object
index|[]
block|{
name|PersistenceAdapterChoice
operator|.
name|AMQ
block|,
name|PersistenceAdapterChoice
operator|.
name|KahaDB
block|}
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|testSize
parameter_list|()
throws|throws
name|Exception
block|{
name|CountDownLatch
name|redeliveryConsumerLatch
init|=
operator|new
name|CountDownLatch
argument_list|(
literal|15000
operator|-
literal|1
argument_list|)
decl_stmt|;
name|openConsumer
argument_list|(
name|redeliveryConsumerLatch
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|broker
operator|.
name|getAdminView
argument_list|()
operator|.
name|getStorePercentUsage
argument_list|()
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
literal|5000
condition|;
name|i
operator|++
control|)
block|{
name|sendMessage
argument_list|(
literal|false
argument_list|)
expr_stmt|;
block|}
specifier|final
name|BrokerView
name|brokerView
init|=
name|broker
operator|.
name|getAdminView
argument_list|()
decl_stmt|;
name|broker
operator|.
name|getSystemUsage
argument_list|()
operator|.
name|getStoreUsage
argument_list|()
operator|.
name|isFull
argument_list|()
expr_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"store percent usage: "
operator|+
name|brokerView
operator|.
name|getStorePercentUsage
argument_list|()
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
literal|"some store in use"
argument_list|,
name|broker
operator|.
name|getAdminView
argument_list|()
operator|.
name|getStorePercentUsage
argument_list|()
operator|>
name|minPercentUsageForStore
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
literal|"redelivery consumer got all it needs"
argument_list|,
name|redeliveryConsumerLatch
operator|.
name|await
argument_list|(
literal|60
argument_list|,
name|TimeUnit
operator|.
name|SECONDS
argument_list|)
argument_list|)
expr_stmt|;
name|closeConsumer
argument_list|()
expr_stmt|;
comment|// consume from DLQ
specifier|final
name|CountDownLatch
name|received
init|=
operator|new
name|CountDownLatch
argument_list|(
literal|5000
operator|-
literal|1
argument_list|)
decl_stmt|;
name|consumerConnection
operator|=
operator|(
name|ActiveMQConnection
operator|)
name|createConnection
argument_list|()
expr_stmt|;
name|Session
name|dlqSession
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
name|MessageConsumer
name|dlqConsumer
init|=
name|dlqSession
operator|.
name|createConsumer
argument_list|(
operator|new
name|ActiveMQQueue
argument_list|(
literal|"ActiveMQ.DLQ"
argument_list|)
argument_list|)
decl_stmt|;
name|dlqConsumer
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
name|message
parameter_list|)
block|{
if|if
condition|(
name|received
operator|.
name|getCount
argument_list|()
operator|%
literal|500
operator|==
literal|0
condition|)
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"remaining on DLQ: "
operator|+
name|received
operator|.
name|getCount
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|received
operator|.
name|countDown
argument_list|()
expr_stmt|;
block|}
block|}
argument_list|)
expr_stmt|;
name|consumerConnection
operator|.
name|start
argument_list|()
expr_stmt|;
name|assertTrue
argument_list|(
literal|"Not all messages reached the DLQ"
argument_list|,
name|received
operator|.
name|await
argument_list|(
literal|60
argument_list|,
name|TimeUnit
operator|.
name|SECONDS
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
literal|"Store usage exceeds expected usage"
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
specifier|public
name|boolean
name|isSatisified
parameter_list|()
throws|throws
name|Exception
block|{
name|broker
operator|.
name|getSystemUsage
argument_list|()
operator|.
name|getStoreUsage
argument_list|()
operator|.
name|isFull
argument_list|()
expr_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"store precent usage: "
operator|+
name|brokerView
operator|.
name|getStorePercentUsage
argument_list|()
argument_list|)
expr_stmt|;
return|return
name|broker
operator|.
name|getAdminView
argument_list|()
operator|.
name|getStorePercentUsage
argument_list|()
operator|<
name|minPercentUsageForStore
return|;
block|}
block|}
argument_list|)
argument_list|)
expr_stmt|;
name|closeConsumer
argument_list|()
expr_stmt|;
block|}
specifier|private
name|void
name|openConsumer
parameter_list|(
specifier|final
name|CountDownLatch
name|latch
parameter_list|)
throws|throws
name|Exception
block|{
name|consumerConnection
operator|=
operator|(
name|ActiveMQConnection
operator|)
name|createConnection
argument_list|()
expr_stmt|;
name|consumerConnection
operator|.
name|setClientID
argument_list|(
literal|"cliID"
argument_list|)
expr_stmt|;
name|consumerConnection
operator|.
name|start
argument_list|()
expr_stmt|;
specifier|final
name|Session
name|session
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
name|MessageListener
name|listener
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
name|latch
operator|.
name|countDown
argument_list|()
expr_stmt|;
try|try
block|{
name|session
operator|.
name|recover
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|ignored
parameter_list|)
block|{
name|ignored
operator|.
name|printStackTrace
argument_list|()
expr_stmt|;
block|}
block|}
block|}
decl_stmt|;
name|session
operator|.
name|createDurableSubscriber
argument_list|(
name|topic
argument_list|,
literal|"subName1"
argument_list|)
operator|.
name|setMessageListener
argument_list|(
name|listener
argument_list|)
expr_stmt|;
name|session
operator|.
name|createDurableSubscriber
argument_list|(
name|topic
argument_list|,
literal|"subName2"
argument_list|)
operator|.
name|setMessageListener
argument_list|(
name|listener
argument_list|)
expr_stmt|;
name|session
operator|.
name|createDurableSubscriber
argument_list|(
name|topic
argument_list|,
literal|"subName3"
argument_list|)
operator|.
name|setMessageListener
argument_list|(
name|listener
argument_list|)
expr_stmt|;
block|}
specifier|private
name|void
name|closeConsumer
parameter_list|()
throws|throws
name|JMSException
block|{
if|if
condition|(
name|consumerConnection
operator|!=
literal|null
condition|)
name|consumerConnection
operator|.
name|close
argument_list|()
expr_stmt|;
name|consumerConnection
operator|=
literal|null
expr_stmt|;
block|}
specifier|private
name|void
name|sendMessage
parameter_list|(
name|boolean
name|filter
parameter_list|)
throws|throws
name|Exception
block|{
if|if
condition|(
name|producerConnection
operator|==
literal|null
condition|)
block|{
name|producerConnection
operator|=
operator|(
name|ActiveMQConnection
operator|)
name|createConnection
argument_list|()
expr_stmt|;
name|producerConnection
operator|.
name|start
argument_list|()
expr_stmt|;
name|producerSession
operator|=
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
expr_stmt|;
name|producer
operator|=
name|producerSession
operator|.
name|createProducer
argument_list|(
name|topic
argument_list|)
expr_stmt|;
block|}
name|Message
name|message
init|=
name|producerSession
operator|.
name|createMessage
argument_list|()
decl_stmt|;
name|message
operator|.
name|setStringProperty
argument_list|(
literal|"data"
argument_list|,
name|data
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
specifier|private
name|void
name|startBroker
parameter_list|(
name|boolean
name|deleteMessages
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
name|broker
operator|.
name|setAdvisorySupport
argument_list|(
literal|false
argument_list|)
expr_stmt|;
name|broker
operator|.
name|setBrokerName
argument_list|(
literal|"testStoreSize"
argument_list|)
expr_stmt|;
if|if
condition|(
name|deleteMessages
condition|)
block|{
name|broker
operator|.
name|setDeleteAllMessagesOnStartup
argument_list|(
literal|true
argument_list|)
expr_stmt|;
block|}
name|setDefaultPersistenceAdapter
argument_list|(
name|broker
argument_list|)
expr_stmt|;
name|configurePersistenceAdapter
argument_list|(
name|broker
operator|.
name|getPersistenceAdapter
argument_list|()
argument_list|)
expr_stmt|;
name|broker
operator|.
name|getSystemUsage
argument_list|()
operator|.
name|getStoreUsage
argument_list|()
operator|.
name|setLimit
argument_list|(
literal|200
operator|*
literal|1000
operator|*
literal|1000
argument_list|)
expr_stmt|;
name|broker
operator|.
name|start
argument_list|()
expr_stmt|;
block|}
specifier|private
name|void
name|configurePersistenceAdapter
parameter_list|(
name|PersistenceAdapter
name|persistenceAdapter
parameter_list|)
block|{
name|Properties
name|properties
init|=
operator|new
name|Properties
argument_list|()
decl_stmt|;
name|String
name|maxFileLengthVal
init|=
name|String
operator|.
name|valueOf
argument_list|(
literal|1
operator|*
literal|1024
operator|*
literal|1024
argument_list|)
decl_stmt|;
name|properties
operator|.
name|put
argument_list|(
literal|"journalMaxFileLength"
argument_list|,
name|maxFileLengthVal
argument_list|)
expr_stmt|;
name|properties
operator|.
name|put
argument_list|(
literal|"maxFileLength"
argument_list|,
name|maxFileLengthVal
argument_list|)
expr_stmt|;
name|properties
operator|.
name|put
argument_list|(
literal|"cleanupInterval"
argument_list|,
literal|"2000"
argument_list|)
expr_stmt|;
name|properties
operator|.
name|put
argument_list|(
literal|"checkpointInterval"
argument_list|,
literal|"2000"
argument_list|)
expr_stmt|;
name|IntrospectionSupport
operator|.
name|setProperties
argument_list|(
name|persistenceAdapter
argument_list|,
name|properties
argument_list|)
expr_stmt|;
block|}
specifier|private
name|void
name|stopBroker
parameter_list|()
throws|throws
name|Exception
block|{
if|if
condition|(
name|broker
operator|!=
literal|null
condition|)
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
specifier|protected
name|ActiveMQConnectionFactory
name|createConnectionFactory
parameter_list|()
throws|throws
name|Exception
block|{
return|return
operator|new
name|ActiveMQConnectionFactory
argument_list|(
literal|"vm://testStoreSize?jms.watchTopicAdvisories=false&jms.redeliveryPolicy.maximumRedeliveries=0&jms.closeTimeout=60000&waitForStart=5000&create=false"
argument_list|)
return|;
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
name|super
operator|.
name|setUp
argument_list|()
expr_stmt|;
name|StringBuilder
name|sb
init|=
operator|new
name|StringBuilder
argument_list|(
literal|5000
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
literal|5000
condition|;
name|i
operator|++
control|)
block|{
name|sb
operator|.
name|append
argument_list|(
literal|'a'
argument_list|)
expr_stmt|;
block|}
name|data
operator|=
name|sb
operator|.
name|toString
argument_list|()
expr_stmt|;
name|startBroker
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|topic
operator|=
operator|(
name|ActiveMQTopic
operator|)
name|createDestination
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
name|stopBroker
argument_list|()
expr_stmt|;
name|super
operator|.
name|tearDown
argument_list|()
expr_stmt|;
block|}
block|}
end_class

end_unit

