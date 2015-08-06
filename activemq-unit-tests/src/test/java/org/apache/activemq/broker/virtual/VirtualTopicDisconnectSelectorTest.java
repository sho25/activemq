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
name|broker
operator|.
name|virtual
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
name|org
operator|.
name|apache
operator|.
name|activemq
operator|.
name|EmbeddedBrokerTestSupport
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
name|plugin
operator|.
name|SubQueueSelectorCacheBroker
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
name|spring
operator|.
name|ConsumerBean
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
name|xbean
operator|.
name|XBeanBrokerFactory
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
comment|/**  * Test case for  https://issues.apache.org/jira/browse/AMQ-3004  */
end_comment

begin_class
specifier|public
class|class
name|VirtualTopicDisconnectSelectorTest
extends|extends
name|EmbeddedBrokerTestSupport
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
name|VirtualTopicDisconnectSelectorTest
operator|.
name|class
argument_list|)
decl_stmt|;
specifier|protected
name|Connection
name|connection
decl_stmt|;
specifier|public
name|void
name|testVirtualTopicSelectorDisconnect
parameter_list|()
throws|throws
name|Exception
block|{
name|testVirtualTopicDisconnect
argument_list|(
literal|"odd = 'no'"
argument_list|,
literal|3000
argument_list|,
literal|1500
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|testVirtualTopicNoSelectorDisconnect
parameter_list|()
throws|throws
name|Exception
block|{
name|testVirtualTopicDisconnect
argument_list|(
literal|null
argument_list|,
literal|3000
argument_list|,
literal|3000
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|testVirtualTopicDisconnect
parameter_list|(
name|String
name|messageSelector
parameter_list|,
name|int
name|total
parameter_list|,
name|int
name|expected
parameter_list|)
throws|throws
name|Exception
block|{
if|if
condition|(
name|connection
operator|==
literal|null
condition|)
block|{
name|connection
operator|=
name|createConnection
argument_list|()
expr_stmt|;
block|}
name|connection
operator|.
name|start
argument_list|()
expr_stmt|;
specifier|final
name|ConsumerBean
name|messageList
init|=
operator|new
name|ConsumerBean
argument_list|()
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
name|Session
operator|.
name|CLIENT_ACKNOWLEDGE
argument_list|)
decl_stmt|;
name|Destination
name|producerDestination
init|=
name|getProducerDestination
argument_list|()
decl_stmt|;
name|Destination
name|destination
init|=
name|getConsumerDsetination
argument_list|()
decl_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"Sending to: "
operator|+
name|producerDestination
argument_list|)
expr_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"Consuming from: "
operator|+
name|destination
argument_list|)
expr_stmt|;
name|MessageConsumer
name|consumer
init|=
name|createConsumer
argument_list|(
name|session
argument_list|,
name|destination
argument_list|,
name|messageSelector
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
name|messageList
operator|.
name|onMessage
argument_list|(
name|message
argument_list|)
expr_stmt|;
try|try
block|{
name|message
operator|.
name|acknowledge
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
decl_stmt|;
name|consumer
operator|.
name|setMessageListener
argument_list|(
name|listener
argument_list|)
expr_stmt|;
comment|// create topic producer
name|MessageProducer
name|producer
init|=
name|session
operator|.
name|createProducer
argument_list|(
name|producerDestination
argument_list|)
decl_stmt|;
name|assertNotNull
argument_list|(
name|producer
argument_list|)
expr_stmt|;
name|int
name|disconnectCount
init|=
name|total
operator|/
literal|3
decl_stmt|;
name|int
name|reconnectCount
init|=
operator|(
name|total
operator|*
literal|2
operator|)
operator|/
literal|3
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
name|total
condition|;
name|i
operator|++
control|)
block|{
name|producer
operator|.
name|send
argument_list|(
name|createMessage
argument_list|(
name|session
argument_list|,
name|i
argument_list|)
argument_list|)
expr_stmt|;
if|if
condition|(
name|i
operator|==
name|disconnectCount
condition|)
block|{
name|consumer
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
if|if
condition|(
name|i
operator|==
name|reconnectCount
condition|)
block|{
name|consumer
operator|=
name|createConsumer
argument_list|(
name|session
argument_list|,
name|destination
argument_list|,
name|messageSelector
argument_list|)
expr_stmt|;
name|consumer
operator|.
name|setMessageListener
argument_list|(
name|listener
argument_list|)
expr_stmt|;
block|}
block|}
name|assertMessagesArrived
argument_list|(
name|messageList
argument_list|,
name|expected
argument_list|,
literal|10000
argument_list|)
expr_stmt|;
block|}
specifier|protected
name|ActiveMQQueue
name|getConsumerDsetination
parameter_list|()
block|{
return|return
operator|new
name|ActiveMQQueue
argument_list|(
literal|"Consumer.VirtualTopic.TEST"
argument_list|)
return|;
block|}
specifier|protected
name|Destination
name|getProducerDestination
parameter_list|()
block|{
return|return
operator|new
name|ActiveMQTopic
argument_list|(
literal|"VirtualTopic.TEST"
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
block|}
specifier|protected
name|MessageConsumer
name|createConsumer
parameter_list|(
name|Session
name|session
parameter_list|,
name|Destination
name|destination
parameter_list|,
name|String
name|messageSelector
parameter_list|)
throws|throws
name|JMSException
block|{
if|if
condition|(
name|messageSelector
operator|!=
literal|null
condition|)
block|{
return|return
name|session
operator|.
name|createConsumer
argument_list|(
name|destination
argument_list|,
name|messageSelector
argument_list|)
return|;
block|}
else|else
block|{
return|return
name|session
operator|.
name|createConsumer
argument_list|(
name|destination
argument_list|)
return|;
block|}
block|}
specifier|protected
name|TextMessage
name|createMessage
parameter_list|(
name|Session
name|session
parameter_list|,
name|int
name|i
parameter_list|)
throws|throws
name|JMSException
block|{
name|TextMessage
name|textMessage
init|=
name|session
operator|.
name|createTextMessage
argument_list|(
literal|"message: "
operator|+
name|i
argument_list|)
decl_stmt|;
if|if
condition|(
name|i
operator|%
literal|2
operator|!=
literal|0
condition|)
block|{
name|textMessage
operator|.
name|setStringProperty
argument_list|(
literal|"odd"
argument_list|,
literal|"yes"
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|textMessage
operator|.
name|setStringProperty
argument_list|(
literal|"odd"
argument_list|,
literal|"no"
argument_list|)
expr_stmt|;
block|}
name|textMessage
operator|.
name|setIntProperty
argument_list|(
literal|"i"
argument_list|,
name|i
argument_list|)
expr_stmt|;
return|return
name|textMessage
return|;
block|}
specifier|protected
name|void
name|assertMessagesArrived
parameter_list|(
name|ConsumerBean
name|messageList
parameter_list|,
name|int
name|expected
parameter_list|,
name|long
name|timeout
parameter_list|)
block|{
name|messageList
operator|.
name|assertMessagesArrived
argument_list|(
name|expected
argument_list|,
name|timeout
argument_list|)
expr_stmt|;
name|messageList
operator|.
name|flushMessages
argument_list|()
expr_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"validate no other messages on queues"
argument_list|)
expr_stmt|;
try|try
block|{
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
name|destination1
init|=
name|getConsumerDsetination
argument_list|()
decl_stmt|;
name|MessageConsumer
name|c1
init|=
name|session
operator|.
name|createConsumer
argument_list|(
name|destination1
argument_list|,
literal|null
argument_list|)
decl_stmt|;
name|c1
operator|.
name|setMessageListener
argument_list|(
name|messageList
argument_list|)
expr_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"send one simple message that should go to both consumers"
argument_list|)
expr_stmt|;
name|MessageProducer
name|producer
init|=
name|session
operator|.
name|createProducer
argument_list|(
name|getProducerDestination
argument_list|()
argument_list|)
decl_stmt|;
name|assertNotNull
argument_list|(
name|producer
argument_list|)
expr_stmt|;
name|producer
operator|.
name|send
argument_list|(
name|session
operator|.
name|createTextMessage
argument_list|(
literal|"Last Message"
argument_list|)
argument_list|)
expr_stmt|;
name|messageList
operator|.
name|assertMessagesArrived
argument_list|(
literal|1
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
name|fail
argument_list|(
literal|"unexpeced ex while waiting for last messages: "
operator|+
name|e
argument_list|)
expr_stmt|;
block|}
block|}
specifier|protected
name|String
name|getBrokerConfigUri
parameter_list|()
block|{
return|return
literal|"org/apache/activemq/broker/virtual/disconnected-selector.xml"
return|;
block|}
specifier|protected
name|BrokerService
name|createBroker
parameter_list|()
throws|throws
name|Exception
block|{
name|XBeanBrokerFactory
name|factory
init|=
operator|new
name|XBeanBrokerFactory
argument_list|()
decl_stmt|;
name|BrokerService
name|answer
init|=
name|factory
operator|.
name|createBroker
argument_list|(
operator|new
name|URI
argument_list|(
name|getBrokerConfigUri
argument_list|()
argument_list|)
argument_list|)
decl_stmt|;
return|return
name|answer
return|;
block|}
specifier|protected
name|void
name|startBroker
parameter_list|()
throws|throws
name|Exception
block|{
name|super
operator|.
name|startBroker
argument_list|()
expr_stmt|;
comment|// start with a clean slate
name|SubQueueSelectorCacheBroker
name|selectorCacheBroker
init|=
operator|(
name|SubQueueSelectorCacheBroker
operator|)
name|broker
operator|.
name|getBroker
argument_list|()
operator|.
name|getAdaptor
argument_list|(
name|SubQueueSelectorCacheBroker
operator|.
name|class
argument_list|)
decl_stmt|;
name|selectorCacheBroker
operator|.
name|deleteAllSelectorsForDestination
argument_list|(
name|getConsumerDsetination
argument_list|()
operator|.
name|getQualifiedName
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

