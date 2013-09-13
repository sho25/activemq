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
name|transport
operator|.
name|amqp
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
name|qpid
operator|.
name|amqp_1_0
operator|.
name|jms
operator|.
name|impl
operator|.
name|ConnectionFactoryImpl
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|qpid
operator|.
name|amqp_1_0
operator|.
name|jms
operator|.
name|impl
operator|.
name|TopicImpl
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
name|AMQ4696Test
extends|extends
name|AmqpTestSupport
block|{
annotation|@
name|Test
specifier|public
name|void
name|simpleDurableTopicTest
parameter_list|()
throws|throws
name|Exception
block|{
name|String
name|TOPIC_NAME
init|=
literal|"topic://AMQ4696Test"
operator|+
name|System
operator|.
name|currentTimeMillis
argument_list|()
decl_stmt|;
name|String
name|durableClientId
init|=
literal|"AMQPDurableTopicTestClient"
decl_stmt|;
name|String
name|durableSubscriberName
init|=
literal|"durableSubscriberName"
decl_stmt|;
name|BrokerView
name|adminView
init|=
name|this
operator|.
name|brokerService
operator|.
name|getAdminView
argument_list|()
decl_stmt|;
name|int
name|durableSubscribersAtStart
init|=
name|adminView
operator|.
name|getDurableTopicSubscribers
argument_list|()
operator|.
name|length
decl_stmt|;
name|int
name|inactiveSubscribersAtStart
init|=
name|adminView
operator|.
name|getInactiveDurableTopicSubscribers
argument_list|()
operator|.
name|length
decl_stmt|;
name|LOG
operator|.
name|debug
argument_list|(
literal|">>>> At Start, durable Subscribers {} inactiveDurableSubscribers {}"
argument_list|,
name|durableSubscribersAtStart
argument_list|,
name|inactiveSubscribersAtStart
argument_list|)
expr_stmt|;
name|TopicConnectionFactory
name|factory
init|=
operator|new
name|ConnectionFactoryImpl
argument_list|(
literal|"localhost"
argument_list|,
name|port
argument_list|,
literal|"admin"
argument_list|,
literal|"password"
argument_list|)
decl_stmt|;
name|Topic
name|topic
init|=
operator|new
name|TopicImpl
argument_list|(
literal|"topic://"
operator|+
name|TOPIC_NAME
argument_list|)
decl_stmt|;
name|TopicConnection
name|subscriberConnection
init|=
name|factory
operator|.
name|createTopicConnection
argument_list|()
decl_stmt|;
name|subscriberConnection
operator|.
name|setClientID
argument_list|(
name|durableClientId
argument_list|)
expr_stmt|;
name|TopicSession
name|subscriberSession
init|=
name|subscriberConnection
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
name|TopicSubscriber
name|messageConsumer
init|=
name|subscriberSession
operator|.
name|createDurableSubscriber
argument_list|(
name|topic
argument_list|,
name|durableSubscriberName
argument_list|)
decl_stmt|;
name|assertNotNull
argument_list|(
name|messageConsumer
argument_list|)
expr_stmt|;
name|int
name|durableSubscribers
init|=
name|adminView
operator|.
name|getDurableTopicSubscribers
argument_list|()
operator|.
name|length
decl_stmt|;
name|int
name|inactiveSubscribers
init|=
name|adminView
operator|.
name|getInactiveDurableTopicSubscribers
argument_list|()
operator|.
name|length
decl_stmt|;
name|LOG
operator|.
name|debug
argument_list|(
literal|">>>> durable Subscribers after creation {} inactiveDurableSubscribers {}"
argument_list|,
name|durableSubscribers
argument_list|,
name|inactiveSubscribers
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"Wrong number of durable subscribers after first subscription"
argument_list|,
literal|1
argument_list|,
operator|(
name|durableSubscribers
operator|-
name|durableSubscribersAtStart
operator|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"Wrong number of inactive durable subscribers after first subscription"
argument_list|,
literal|0
argument_list|,
operator|(
name|inactiveSubscribers
operator|-
name|inactiveSubscribersAtStart
operator|)
argument_list|)
expr_stmt|;
name|subscriberConnection
operator|.
name|close
argument_list|()
expr_stmt|;
name|subscriberConnection
operator|=
literal|null
expr_stmt|;
name|durableSubscribers
operator|=
name|adminView
operator|.
name|getDurableTopicSubscribers
argument_list|()
operator|.
name|length
expr_stmt|;
name|inactiveSubscribers
operator|=
name|adminView
operator|.
name|getInactiveDurableTopicSubscribers
argument_list|()
operator|.
name|length
expr_stmt|;
name|LOG
operator|.
name|debug
argument_list|(
literal|">>>> durable Subscribers after close {} inactiveDurableSubscribers {}"
argument_list|,
name|durableSubscribers
argument_list|,
name|inactiveSubscribers
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"Wrong number of durable subscribers after close"
argument_list|,
literal|0
argument_list|,
operator|(
name|durableSubscribersAtStart
operator|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"Wrong number of inactive durable subscribers after close"
argument_list|,
literal|1
argument_list|,
operator|(
name|inactiveSubscribers
operator|-
name|inactiveSubscribersAtStart
operator|)
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit
