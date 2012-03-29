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
name|TestSupport
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
name|ConnectionFactory
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
name|TopicSession
import|;
end_import

begin_class
specifier|public
class|class
name|AMQ2580Test
extends|extends
name|TestSupport
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
name|AMQ2580Test
operator|.
name|class
argument_list|)
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|String
name|TOPIC_NAME
init|=
literal|"topicName"
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|String
name|CLIENT_ID
init|=
literal|"client_id"
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|String
name|textOfSelectedMsg
init|=
literal|"good_message"
decl_stmt|;
specifier|protected
name|TopicConnection
name|connection
decl_stmt|;
specifier|private
name|Topic
name|topic
decl_stmt|;
specifier|private
name|Session
name|session
decl_stmt|;
specifier|private
name|MessageProducer
name|producer
decl_stmt|;
specifier|private
name|ConnectionFactory
name|connectionFactory
decl_stmt|;
specifier|private
name|BrokerService
name|service
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
name|AMQ2580Test
operator|.
name|class
argument_list|)
return|;
block|}
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
name|initDurableBroker
argument_list|()
expr_stmt|;
name|initConnectionFactory
argument_list|()
expr_stmt|;
name|initTopic
argument_list|()
expr_stmt|;
block|}
specifier|protected
name|void
name|tearDown
parameter_list|()
throws|throws
name|Exception
block|{
name|shutdownClient
argument_list|()
expr_stmt|;
name|service
operator|.
name|stop
argument_list|()
expr_stmt|;
name|super
operator|.
name|tearDown
argument_list|()
expr_stmt|;
block|}
specifier|private
name|void
name|initConnection
parameter_list|()
throws|throws
name|JMSException
block|{
if|if
condition|(
name|connection
operator|==
literal|null
condition|)
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"Initializing connection"
argument_list|)
expr_stmt|;
name|connection
operator|=
operator|(
name|TopicConnection
operator|)
name|connectionFactory
operator|.
name|createConnection
argument_list|()
expr_stmt|;
name|connection
operator|.
name|start
argument_list|()
expr_stmt|;
block|}
block|}
specifier|public
name|void
name|initCombosForTestTopicIsDurableSmokeTest
parameter_list|()
throws|throws
name|Exception
block|{
name|addCombinationValues
argument_list|(
literal|"defaultPersistenceAdapter"
argument_list|,
name|PersistenceAdapterChoice
operator|.
name|values
argument_list|()
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|testTopicIsDurableSmokeTest
parameter_list|()
throws|throws
name|Exception
block|{
name|initClient
argument_list|()
expr_stmt|;
name|MessageConsumer
name|consumer
init|=
name|createMessageConsumer
argument_list|()
decl_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"Consuming message"
argument_list|)
expr_stmt|;
name|assertNull
argument_list|(
name|consumer
operator|.
name|receive
argument_list|(
literal|1
argument_list|)
argument_list|)
expr_stmt|;
name|shutdownClient
argument_list|()
expr_stmt|;
name|consumer
operator|.
name|close
argument_list|()
expr_stmt|;
name|sendMessages
argument_list|()
expr_stmt|;
name|shutdownClient
argument_list|()
expr_stmt|;
name|initClient
argument_list|()
expr_stmt|;
name|consumer
operator|=
name|createMessageConsumer
argument_list|()
expr_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"Consuming message"
argument_list|)
expr_stmt|;
name|TextMessage
name|answer1
init|=
operator|(
name|TextMessage
operator|)
name|consumer
operator|.
name|receive
argument_list|(
literal|1000
argument_list|)
decl_stmt|;
name|assertNotNull
argument_list|(
literal|"we got our message"
argument_list|,
name|answer1
argument_list|)
expr_stmt|;
name|consumer
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
specifier|private
name|MessageConsumer
name|createMessageConsumer
parameter_list|()
throws|throws
name|JMSException
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"creating durable subscriber"
argument_list|)
expr_stmt|;
return|return
name|session
operator|.
name|createDurableSubscriber
argument_list|(
name|topic
argument_list|,
name|TOPIC_NAME
argument_list|,
literal|"name='value'"
argument_list|,
literal|false
argument_list|)
return|;
block|}
specifier|private
name|void
name|initClient
parameter_list|()
throws|throws
name|JMSException
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"Initializing client"
argument_list|)
expr_stmt|;
name|initConnection
argument_list|()
expr_stmt|;
name|initSession
argument_list|()
expr_stmt|;
block|}
specifier|private
name|void
name|shutdownClient
parameter_list|()
throws|throws
name|JMSException
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"Closing session and connection"
argument_list|)
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
name|session
operator|=
literal|null
expr_stmt|;
name|connection
operator|=
literal|null
expr_stmt|;
block|}
specifier|private
name|void
name|sendMessages
parameter_list|()
throws|throws
name|JMSException
block|{
name|initConnection
argument_list|()
expr_stmt|;
name|initSession
argument_list|()
expr_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"Creating producer"
argument_list|)
expr_stmt|;
name|producer
operator|=
name|session
operator|.
name|createProducer
argument_list|(
name|topic
argument_list|)
expr_stmt|;
name|sendMessageThatFailsSelection
argument_list|()
expr_stmt|;
name|sendMessage
argument_list|(
name|textOfSelectedMsg
argument_list|,
literal|"value"
argument_list|)
expr_stmt|;
block|}
specifier|private
name|void
name|initSession
parameter_list|()
throws|throws
name|JMSException
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"Initializing session"
argument_list|)
expr_stmt|;
name|session
operator|=
name|connection
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
block|}
specifier|private
name|void
name|sendMessageThatFailsSelection
parameter_list|()
throws|throws
name|JMSException
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
literal|5
condition|;
name|i
operator|++
control|)
block|{
name|String
name|textOfNotSelectedMsg
init|=
literal|"Msg_"
operator|+
name|i
decl_stmt|;
name|sendMessage
argument_list|(
name|textOfNotSelectedMsg
argument_list|,
literal|"not_value"
argument_list|)
expr_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"#"
argument_list|)
expr_stmt|;
block|}
block|}
specifier|private
name|void
name|sendMessage
parameter_list|(
name|String
name|msgText
parameter_list|,
name|String
name|propertyValue
parameter_list|)
throws|throws
name|JMSException
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"Creating message: "
operator|+
name|msgText
argument_list|)
expr_stmt|;
name|TextMessage
name|messageToSelect
init|=
name|session
operator|.
name|createTextMessage
argument_list|(
name|msgText
argument_list|)
decl_stmt|;
name|messageToSelect
operator|.
name|setStringProperty
argument_list|(
literal|"name"
argument_list|,
name|propertyValue
argument_list|)
expr_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"Sending message"
argument_list|)
expr_stmt|;
name|producer
operator|.
name|send
argument_list|(
name|messageToSelect
argument_list|)
expr_stmt|;
block|}
specifier|protected
name|void
name|initConnectionFactory
parameter_list|()
throws|throws
name|Exception
block|{
name|ActiveMQConnectionFactory
name|activeMqConnectionFactory
init|=
name|createActiveMqConnectionFactory
argument_list|()
decl_stmt|;
name|connectionFactory
operator|=
name|activeMqConnectionFactory
expr_stmt|;
block|}
specifier|private
name|ActiveMQConnectionFactory
name|createActiveMqConnectionFactory
parameter_list|()
throws|throws
name|Exception
block|{
name|ActiveMQConnectionFactory
name|activeMqConnectionFactory
init|=
operator|new
name|ActiveMQConnectionFactory
argument_list|(
literal|"failover:"
operator|+
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
name|getConnectUri
argument_list|()
operator|.
name|toString
argument_list|()
argument_list|)
decl_stmt|;
name|activeMqConnectionFactory
operator|.
name|setWatchTopicAdvisories
argument_list|(
literal|false
argument_list|)
expr_stmt|;
name|ActiveMQPrefetchPolicy
name|prefetchPolicy
init|=
operator|new
name|ActiveMQPrefetchPolicy
argument_list|()
decl_stmt|;
name|prefetchPolicy
operator|.
name|setDurableTopicPrefetch
argument_list|(
literal|2
argument_list|)
expr_stmt|;
name|prefetchPolicy
operator|.
name|setOptimizeDurableTopicPrefetch
argument_list|(
literal|2
argument_list|)
expr_stmt|;
name|activeMqConnectionFactory
operator|.
name|setPrefetchPolicy
argument_list|(
name|prefetchPolicy
argument_list|)
expr_stmt|;
name|activeMqConnectionFactory
operator|.
name|setClientID
argument_list|(
name|CLIENT_ID
argument_list|)
expr_stmt|;
return|return
name|activeMqConnectionFactory
return|;
block|}
specifier|private
name|void
name|initDurableBroker
parameter_list|()
throws|throws
name|Exception
block|{
name|service
operator|=
operator|new
name|BrokerService
argument_list|()
expr_stmt|;
name|setDefaultPersistenceAdapter
argument_list|(
name|service
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
name|setAdvisorySupport
argument_list|(
literal|false
argument_list|)
expr_stmt|;
name|service
operator|.
name|setTransportConnectorURIs
argument_list|(
operator|new
name|String
index|[]
block|{
literal|"tcp://localhost:0"
block|}
argument_list|)
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
name|start
argument_list|()
expr_stmt|;
block|}
specifier|private
name|void
name|initTopic
parameter_list|()
throws|throws
name|JMSException
block|{
name|initConnection
argument_list|()
expr_stmt|;
name|TopicSession
name|topicSession
init|=
name|connection
operator|.
name|createTopicSession
argument_list|(
literal|false
argument_list|,
name|Session
operator|.
name|AUTO_ACKNOWLEDGE
argument_list|)
decl_stmt|;
name|topic
operator|=
name|topicSession
operator|.
name|createTopic
argument_list|(
name|TOPIC_NAME
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

