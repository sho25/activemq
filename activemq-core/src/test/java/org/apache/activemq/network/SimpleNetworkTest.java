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
name|TopicRequestor
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
name|xbean
operator|.
name|BrokerFactoryBean
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
name|springframework
operator|.
name|context
operator|.
name|support
operator|.
name|AbstractApplicationContext
import|;
end_import

begin_import
import|import
name|org
operator|.
name|springframework
operator|.
name|core
operator|.
name|io
operator|.
name|ClassPathResource
import|;
end_import

begin_import
import|import
name|org
operator|.
name|springframework
operator|.
name|core
operator|.
name|io
operator|.
name|Resource
import|;
end_import

begin_class
specifier|public
class|class
name|SimpleNetworkTest
extends|extends
name|TestCase
block|{
specifier|protected
specifier|static
specifier|final
name|int
name|MESSAGE_COUNT
init|=
literal|10
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|Log
name|LOG
init|=
name|LogFactory
operator|.
name|getLog
argument_list|(
name|SimpleNetworkTest
operator|.
name|class
argument_list|)
decl_stmt|;
specifier|protected
name|AbstractApplicationContext
name|context
decl_stmt|;
specifier|protected
name|Connection
name|localConnection
decl_stmt|;
specifier|protected
name|Connection
name|remoteConnection
decl_stmt|;
specifier|protected
name|BrokerService
name|localBroker
decl_stmt|;
specifier|protected
name|BrokerService
name|remoteBroker
decl_stmt|;
specifier|protected
name|Session
name|localSession
decl_stmt|;
specifier|protected
name|Session
name|remoteSession
decl_stmt|;
specifier|protected
name|ActiveMQTopic
name|included
decl_stmt|;
specifier|protected
name|ActiveMQTopic
name|excluded
decl_stmt|;
specifier|protected
name|String
name|consumerName
init|=
literal|"durableSubs"
decl_stmt|;
specifier|public
name|void
name|testRequestReply
parameter_list|()
throws|throws
name|Exception
block|{
specifier|final
name|MessageProducer
name|remoteProducer
init|=
name|remoteSession
operator|.
name|createProducer
argument_list|(
literal|null
argument_list|)
decl_stmt|;
name|MessageConsumer
name|remoteConsumer
init|=
name|remoteSession
operator|.
name|createConsumer
argument_list|(
name|included
argument_list|)
decl_stmt|;
name|remoteConsumer
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
name|TextMessage
name|textMsg
init|=
operator|(
name|TextMessage
operator|)
name|msg
decl_stmt|;
name|String
name|payload
init|=
literal|"REPLY: "
operator|+
name|textMsg
operator|.
name|getText
argument_list|()
decl_stmt|;
name|Destination
name|replyTo
decl_stmt|;
name|replyTo
operator|=
name|msg
operator|.
name|getJMSReplyTo
argument_list|()
expr_stmt|;
name|textMsg
operator|.
name|clearBody
argument_list|()
expr_stmt|;
name|textMsg
operator|.
name|setText
argument_list|(
name|payload
argument_list|)
expr_stmt|;
name|remoteProducer
operator|.
name|send
argument_list|(
name|replyTo
argument_list|,
name|textMsg
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|JMSException
name|e
parameter_list|)
block|{
comment|// TODO Auto-generated catch block
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
name|TopicRequestor
name|requestor
init|=
operator|new
name|TopicRequestor
argument_list|(
operator|(
name|TopicSession
operator|)
name|localSession
argument_list|,
name|included
argument_list|)
decl_stmt|;
comment|// allow for consumer infos to perculate arround
name|Thread
operator|.
name|sleep
argument_list|(
literal|2000
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
name|MESSAGE_COUNT
condition|;
name|i
operator|++
control|)
block|{
name|TextMessage
name|msg
init|=
name|localSession
operator|.
name|createTextMessage
argument_list|(
literal|"test msg: "
operator|+
name|i
argument_list|)
decl_stmt|;
name|TextMessage
name|result
init|=
operator|(
name|TextMessage
operator|)
name|requestor
operator|.
name|request
argument_list|(
name|msg
argument_list|)
decl_stmt|;
name|assertNotNull
argument_list|(
name|result
argument_list|)
expr_stmt|;
name|LOG
operator|.
name|info
argument_list|(
name|result
operator|.
name|getText
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
specifier|public
name|void
name|xtestFiltering
parameter_list|()
throws|throws
name|Exception
block|{
name|MessageConsumer
name|includedConsumer
init|=
name|remoteSession
operator|.
name|createConsumer
argument_list|(
name|included
argument_list|)
decl_stmt|;
name|MessageConsumer
name|excludedConsumer
init|=
name|remoteSession
operator|.
name|createConsumer
argument_list|(
name|excluded
argument_list|)
decl_stmt|;
name|MessageProducer
name|includedProducer
init|=
name|localSession
operator|.
name|createProducer
argument_list|(
name|included
argument_list|)
decl_stmt|;
name|MessageProducer
name|excludedProducer
init|=
name|localSession
operator|.
name|createProducer
argument_list|(
name|excluded
argument_list|)
decl_stmt|;
name|Thread
operator|.
name|sleep
argument_list|(
literal|1000
argument_list|)
expr_stmt|;
name|Message
name|test
init|=
name|localSession
operator|.
name|createTextMessage
argument_list|(
literal|"test"
argument_list|)
decl_stmt|;
name|includedProducer
operator|.
name|send
argument_list|(
name|test
argument_list|)
expr_stmt|;
name|excludedProducer
operator|.
name|send
argument_list|(
name|test
argument_list|)
expr_stmt|;
name|assertNull
argument_list|(
name|excludedConsumer
operator|.
name|receive
argument_list|(
literal|500
argument_list|)
argument_list|)
expr_stmt|;
name|assertNotNull
argument_list|(
name|includedConsumer
operator|.
name|receive
argument_list|(
literal|500
argument_list|)
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|xtestConduitBridge
parameter_list|()
throws|throws
name|Exception
block|{
name|MessageConsumer
name|consumer1
init|=
name|remoteSession
operator|.
name|createConsumer
argument_list|(
name|included
argument_list|)
decl_stmt|;
name|MessageConsumer
name|consumer2
init|=
name|remoteSession
operator|.
name|createConsumer
argument_list|(
name|included
argument_list|)
decl_stmt|;
name|MessageProducer
name|producer
init|=
name|localSession
operator|.
name|createProducer
argument_list|(
name|included
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
name|Thread
operator|.
name|sleep
argument_list|(
literal|1000
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
name|MESSAGE_COUNT
condition|;
name|i
operator|++
control|)
block|{
name|Message
name|test
init|=
name|localSession
operator|.
name|createTextMessage
argument_list|(
literal|"test-"
operator|+
name|i
argument_list|)
decl_stmt|;
name|producer
operator|.
name|send
argument_list|(
name|test
argument_list|)
expr_stmt|;
name|assertNotNull
argument_list|(
name|consumer1
operator|.
name|receive
argument_list|(
literal|500
argument_list|)
argument_list|)
expr_stmt|;
name|assertNotNull
argument_list|(
name|consumer2
operator|.
name|receive
argument_list|(
literal|500
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|// ensure no more messages received
name|assertNull
argument_list|(
name|consumer1
operator|.
name|receive
argument_list|(
literal|500
argument_list|)
argument_list|)
expr_stmt|;
name|assertNull
argument_list|(
name|consumer2
operator|.
name|receive
argument_list|(
literal|500
argument_list|)
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|xtestDurableStoreAndForward
parameter_list|()
throws|throws
name|Exception
block|{
comment|// create a remote durable consumer
name|MessageConsumer
name|remoteConsumer
init|=
name|remoteSession
operator|.
name|createDurableSubscriber
argument_list|(
name|included
argument_list|,
name|consumerName
argument_list|)
decl_stmt|;
name|Thread
operator|.
name|sleep
argument_list|(
literal|1000
argument_list|)
expr_stmt|;
comment|// now close everything down and restart
name|doTearDown
argument_list|()
expr_stmt|;
name|doSetUp
argument_list|()
expr_stmt|;
name|MessageProducer
name|producer
init|=
name|localSession
operator|.
name|createProducer
argument_list|(
name|included
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
name|MESSAGE_COUNT
condition|;
name|i
operator|++
control|)
block|{
name|Message
name|test
init|=
name|localSession
operator|.
name|createTextMessage
argument_list|(
literal|"test-"
operator|+
name|i
argument_list|)
decl_stmt|;
name|producer
operator|.
name|send
argument_list|(
name|test
argument_list|)
expr_stmt|;
block|}
name|Thread
operator|.
name|sleep
argument_list|(
literal|1000
argument_list|)
expr_stmt|;
comment|// close everything down and restart
name|doTearDown
argument_list|()
expr_stmt|;
name|doSetUp
argument_list|()
expr_stmt|;
name|remoteConsumer
operator|=
name|remoteSession
operator|.
name|createDurableSubscriber
argument_list|(
name|included
argument_list|,
name|consumerName
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
name|MESSAGE_COUNT
condition|;
name|i
operator|++
control|)
block|{
name|assertNotNull
argument_list|(
name|remoteConsumer
operator|.
name|receive
argument_list|(
literal|500
argument_list|)
argument_list|)
expr_stmt|;
block|}
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
name|doSetUp
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
name|localBroker
operator|.
name|deleteAllMessages
argument_list|()
expr_stmt|;
name|remoteBroker
operator|.
name|deleteAllMessages
argument_list|()
expr_stmt|;
name|doTearDown
argument_list|()
expr_stmt|;
name|super
operator|.
name|tearDown
argument_list|()
expr_stmt|;
block|}
specifier|protected
name|void
name|doTearDown
parameter_list|()
throws|throws
name|Exception
block|{
name|localConnection
operator|.
name|close
argument_list|()
expr_stmt|;
name|remoteConnection
operator|.
name|close
argument_list|()
expr_stmt|;
name|localBroker
operator|.
name|stop
argument_list|()
expr_stmt|;
name|remoteBroker
operator|.
name|stop
argument_list|()
expr_stmt|;
block|}
specifier|protected
name|void
name|doSetUp
parameter_list|()
throws|throws
name|Exception
block|{
name|remoteBroker
operator|=
name|createRemoteBroker
argument_list|()
expr_stmt|;
name|remoteBroker
operator|.
name|start
argument_list|()
expr_stmt|;
name|localBroker
operator|=
name|createLocalBroker
argument_list|()
expr_stmt|;
name|localBroker
operator|.
name|start
argument_list|()
expr_stmt|;
name|URI
name|localURI
init|=
name|localBroker
operator|.
name|getVmConnectorURI
argument_list|()
decl_stmt|;
name|ActiveMQConnectionFactory
name|fac
init|=
operator|new
name|ActiveMQConnectionFactory
argument_list|(
name|localURI
argument_list|)
decl_stmt|;
name|localConnection
operator|=
name|fac
operator|.
name|createConnection
argument_list|()
expr_stmt|;
name|localConnection
operator|.
name|setClientID
argument_list|(
literal|"local"
argument_list|)
expr_stmt|;
name|localConnection
operator|.
name|start
argument_list|()
expr_stmt|;
name|URI
name|remoteURI
init|=
name|remoteBroker
operator|.
name|getVmConnectorURI
argument_list|()
decl_stmt|;
name|fac
operator|=
operator|new
name|ActiveMQConnectionFactory
argument_list|(
name|remoteURI
argument_list|)
expr_stmt|;
name|remoteConnection
operator|=
name|fac
operator|.
name|createConnection
argument_list|()
expr_stmt|;
name|remoteConnection
operator|.
name|setClientID
argument_list|(
literal|"remote"
argument_list|)
expr_stmt|;
name|remoteConnection
operator|.
name|start
argument_list|()
expr_stmt|;
name|included
operator|=
operator|new
name|ActiveMQTopic
argument_list|(
literal|"include.test.bar"
argument_list|)
expr_stmt|;
name|excluded
operator|=
operator|new
name|ActiveMQTopic
argument_list|(
literal|"exclude.test.bar"
argument_list|)
expr_stmt|;
name|localSession
operator|=
name|localConnection
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
name|remoteSession
operator|=
name|remoteConnection
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
block|}
specifier|protected
name|String
name|getRemoteBrokerURI
parameter_list|()
block|{
return|return
literal|"org/apache/activemq/network/remoteBroker.xml"
return|;
block|}
specifier|protected
name|String
name|getLocalBrokerURI
parameter_list|()
block|{
return|return
literal|"org/apache/activemq/network/localBroker.xml"
return|;
block|}
specifier|protected
name|BrokerService
name|createBroker
parameter_list|(
name|String
name|uri
parameter_list|)
throws|throws
name|Exception
block|{
name|Resource
name|resource
init|=
operator|new
name|ClassPathResource
argument_list|(
name|uri
argument_list|)
decl_stmt|;
name|BrokerFactoryBean
name|factory
init|=
operator|new
name|BrokerFactoryBean
argument_list|(
name|resource
argument_list|)
decl_stmt|;
name|resource
operator|=
operator|new
name|ClassPathResource
argument_list|(
name|uri
argument_list|)
expr_stmt|;
name|factory
operator|=
operator|new
name|BrokerFactoryBean
argument_list|(
name|resource
argument_list|)
expr_stmt|;
name|factory
operator|.
name|afterPropertiesSet
argument_list|()
expr_stmt|;
name|BrokerService
name|result
init|=
name|factory
operator|.
name|getBroker
argument_list|()
decl_stmt|;
return|return
name|result
return|;
block|}
specifier|protected
name|BrokerService
name|createLocalBroker
parameter_list|()
throws|throws
name|Exception
block|{
return|return
name|createBroker
argument_list|(
name|getLocalBrokerURI
argument_list|()
argument_list|)
return|;
block|}
specifier|protected
name|BrokerService
name|createRemoteBroker
parameter_list|()
throws|throws
name|Exception
block|{
return|return
name|createBroker
argument_list|(
name|getRemoteBrokerURI
argument_list|()
argument_list|)
return|;
block|}
block|}
end_class

end_unit

