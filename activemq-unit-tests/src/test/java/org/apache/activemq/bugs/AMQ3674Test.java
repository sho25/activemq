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
name|*
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
name|TopicSubscriber
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

begin_class
specifier|public
class|class
name|AMQ3674Test
block|{
specifier|private
specifier|static
name|Logger
name|LOG
init|=
name|LoggerFactory
operator|.
name|getLogger
argument_list|(
name|AMQ3674Test
operator|.
name|class
argument_list|)
decl_stmt|;
specifier|private
specifier|final
specifier|static
name|int
name|deliveryMode
init|=
name|DeliveryMode
operator|.
name|NON_PERSISTENT
decl_stmt|;
specifier|private
specifier|final
specifier|static
name|ActiveMQTopic
name|destination
init|=
operator|new
name|ActiveMQTopic
argument_list|(
literal|"XYZ"
argument_list|)
decl_stmt|;
specifier|private
name|ActiveMQConnectionFactory
name|factory
decl_stmt|;
specifier|private
name|BrokerService
name|broker
decl_stmt|;
annotation|@
name|Test
specifier|public
name|void
name|removeSubscription
parameter_list|()
throws|throws
name|Exception
block|{
specifier|final
name|Connection
name|producerConnection
init|=
name|factory
operator|.
name|createConnection
argument_list|()
decl_stmt|;
name|producerConnection
operator|.
name|start
argument_list|()
expr_stmt|;
specifier|final
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
literal|"subscriber1"
argument_list|)
expr_stmt|;
name|Session
name|consumerMQSession
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
name|TopicSubscriber
name|activeConsumer
init|=
operator|(
name|TopicSubscriber
operator|)
name|consumerMQSession
operator|.
name|createDurableSubscriber
argument_list|(
name|destination
argument_list|,
literal|"myTopic"
argument_list|)
decl_stmt|;
name|consumerConnection
operator|.
name|start
argument_list|()
expr_stmt|;
name|Session
name|session
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
name|deliveryMode
argument_list|)
expr_stmt|;
specifier|final
name|BrokerView
name|brokerView
init|=
name|broker
operator|.
name|getAdminView
argument_list|()
decl_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|brokerView
operator|.
name|getDurableTopicSubscribers
argument_list|()
operator|.
name|length
argument_list|)
expr_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"Current Durable Topic Subscriptions: "
operator|+
name|brokerView
operator|.
name|getDurableTopicSubscribers
argument_list|()
operator|.
name|length
argument_list|)
expr_stmt|;
try|try
block|{
name|brokerView
operator|.
name|destroyDurableSubscriber
argument_list|(
literal|"subscriber1"
argument_list|,
literal|"myTopic"
argument_list|)
expr_stmt|;
name|fail
argument_list|(
literal|"Expected Exception for Durable consumer is in use"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"Recieved expected exception: "
operator|+
name|e
operator|.
name|getMessage
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|LOG
operator|.
name|info
argument_list|(
literal|"Current Durable Topic Subscriptions: "
operator|+
name|brokerView
operator|.
name|getDurableTopicSubscribers
argument_list|()
operator|.
name|length
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|brokerView
operator|.
name|getDurableTopicSubscribers
argument_list|()
operator|.
name|length
argument_list|)
expr_stmt|;
name|activeConsumer
operator|.
name|close
argument_list|()
expr_stmt|;
name|consumerConnection
operator|.
name|stop
argument_list|()
expr_stmt|;
name|assertTrue
argument_list|(
literal|"The subscription should be in the inactive state."
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
name|brokerView
operator|.
name|getInactiveDurableTopicSubscribers
argument_list|()
operator|.
name|length
operator|==
literal|1
return|;
block|}
block|}
argument_list|)
argument_list|)
expr_stmt|;
try|try
block|{
name|brokerView
operator|.
name|destroyDurableSubscriber
argument_list|(
literal|"subscriber1"
argument_list|,
literal|"myTopic"
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|producer
operator|.
name|close
argument_list|()
expr_stmt|;
name|producerConnection
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
annotation|@
name|Before
specifier|public
name|void
name|setUp
parameter_list|()
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
name|setDeleteAllMessagesOnStartup
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|TransportConnector
name|connector
init|=
name|broker
operator|.
name|addConnector
argument_list|(
literal|"tcp://localhost:0"
argument_list|)
decl_stmt|;
name|broker
operator|.
name|start
argument_list|()
expr_stmt|;
name|factory
operator|=
operator|new
name|ActiveMQConnectionFactory
argument_list|(
name|connector
operator|.
name|getPublishableConnectString
argument_list|()
argument_list|)
expr_stmt|;
name|factory
operator|.
name|setAlwaysSyncSend
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|factory
operator|.
name|setDispatchAsync
argument_list|(
literal|false
argument_list|)
expr_stmt|;
block|}
annotation|@
name|After
specifier|public
name|void
name|tearDown
parameter_list|()
throws|throws
name|Exception
block|{
name|broker
operator|.
name|stop
argument_list|()
expr_stmt|;
block|}
block|}
end_class

end_unit

