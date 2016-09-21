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
name|usecases
package|;
end_package

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
name|Session
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
name|command
operator|.
name|ActiveMQTopic
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
name|Assert
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
name|TopicSubscriptionZeroPrefetchTest
block|{
specifier|private
specifier|static
specifier|final
name|String
name|TOPIC_NAME
init|=
literal|"slow.consumer"
decl_stmt|;
specifier|private
name|Connection
name|connection
decl_stmt|;
specifier|private
name|Session
name|session
decl_stmt|;
specifier|private
name|ActiveMQTopic
name|destination
decl_stmt|;
specifier|private
name|MessageProducer
name|producer
decl_stmt|;
specifier|private
name|MessageConsumer
name|consumer
decl_stmt|;
specifier|private
name|BrokerService
name|brokerService
decl_stmt|;
annotation|@
name|Before
specifier|public
name|void
name|setUp
parameter_list|()
throws|throws
name|Exception
block|{
name|brokerService
operator|=
name|createBroker
argument_list|()
expr_stmt|;
name|ActiveMQConnectionFactory
name|activeMQConnectionFactory
init|=
operator|new
name|ActiveMQConnectionFactory
argument_list|(
literal|"vm://localhost"
argument_list|)
decl_stmt|;
name|activeMQConnectionFactory
operator|.
name|setWatchTopicAdvisories
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|connection
operator|=
name|activeMQConnectionFactory
operator|.
name|createConnection
argument_list|()
expr_stmt|;
name|connection
operator|.
name|setClientID
argument_list|(
literal|"ClientID-1"
argument_list|)
expr_stmt|;
name|session
operator|=
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
expr_stmt|;
name|destination
operator|=
operator|new
name|ActiveMQTopic
argument_list|(
name|TOPIC_NAME
argument_list|)
expr_stmt|;
name|producer
operator|=
name|session
operator|.
name|createProducer
argument_list|(
name|destination
argument_list|)
expr_stmt|;
name|connection
operator|.
name|start
argument_list|()
expr_stmt|;
block|}
comment|/*      * test non durable topic subscription with prefetch set to zero      */
annotation|@
name|Test
argument_list|(
name|timeout
operator|=
literal|60000
argument_list|)
specifier|public
name|void
name|testTopicConsumerPrefetchZero
parameter_list|()
throws|throws
name|Exception
block|{
name|ActiveMQTopic
name|consumerDestination
init|=
operator|new
name|ActiveMQTopic
argument_list|(
name|TOPIC_NAME
operator|+
literal|"?consumer.retroactive=true&consumer.prefetchSize=0"
argument_list|)
decl_stmt|;
name|consumer
operator|=
name|session
operator|.
name|createConsumer
argument_list|(
name|consumerDestination
argument_list|)
expr_stmt|;
comment|// publish messages
name|Message
name|txtMessage
init|=
name|session
operator|.
name|createTextMessage
argument_list|(
literal|"M"
argument_list|)
decl_stmt|;
name|producer
operator|.
name|send
argument_list|(
name|txtMessage
argument_list|)
expr_stmt|;
name|Message
name|consumedMessage
init|=
name|consumer
operator|.
name|receiveNoWait
argument_list|()
decl_stmt|;
name|Assert
operator|.
name|assertNotNull
argument_list|(
literal|"should have received a message the published message"
argument_list|,
name|consumedMessage
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
argument_list|(
name|timeout
operator|=
literal|60000
argument_list|)
specifier|public
name|void
name|testTopicConsumerPrefetchZeroClientAckLoop
parameter_list|()
throws|throws
name|Exception
block|{
name|ActiveMQTopic
name|consumerDestination
init|=
operator|new
name|ActiveMQTopic
argument_list|(
name|TOPIC_NAME
operator|+
literal|"?consumer.retroactive=true&consumer.prefetchSize=0"
argument_list|)
decl_stmt|;
name|Session
name|consumerClientAckSession
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
name|consumer
operator|=
name|consumerClientAckSession
operator|.
name|createConsumer
argument_list|(
name|consumerDestination
argument_list|)
expr_stmt|;
specifier|final
name|int
name|count
init|=
literal|10
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
name|count
condition|;
name|i
operator|++
control|)
block|{
name|Message
name|txtMessage
init|=
name|session
operator|.
name|createTextMessage
argument_list|(
literal|"M:"
operator|+
name|i
argument_list|)
decl_stmt|;
name|producer
operator|.
name|send
argument_list|(
name|txtMessage
argument_list|)
expr_stmt|;
block|}
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
name|Message
name|consumedMessage
init|=
name|consumer
operator|.
name|receive
argument_list|(
literal|2000
argument_list|)
decl_stmt|;
name|Assert
operator|.
name|assertNotNull
argument_list|(
literal|"should have received message["
operator|+
name|i
operator|+
literal|"]"
argument_list|,
name|consumedMessage
argument_list|)
expr_stmt|;
block|}
block|}
comment|/*      * test durable topic subscription with prefetch zero      */
annotation|@
name|Test
argument_list|(
name|timeout
operator|=
literal|60000
argument_list|)
specifier|public
name|void
name|testDurableTopicConsumerPrefetchZero
parameter_list|()
throws|throws
name|Exception
block|{
name|ActiveMQTopic
name|consumerDestination
init|=
operator|new
name|ActiveMQTopic
argument_list|(
name|TOPIC_NAME
operator|+
literal|"?consumer.prefetchSize=0"
argument_list|)
decl_stmt|;
name|consumer
operator|=
name|session
operator|.
name|createDurableSubscriber
argument_list|(
name|consumerDestination
argument_list|,
literal|"mysub1"
argument_list|)
expr_stmt|;
comment|// publish messages
name|Message
name|txtMessage
init|=
name|session
operator|.
name|createTextMessage
argument_list|(
literal|"M"
argument_list|)
decl_stmt|;
name|producer
operator|.
name|send
argument_list|(
name|txtMessage
argument_list|)
expr_stmt|;
name|Message
name|consumedMessage
init|=
name|consumer
operator|.
name|receive
argument_list|(
literal|100
argument_list|)
decl_stmt|;
name|Assert
operator|.
name|assertNotNull
argument_list|(
literal|"should have received a message the published message"
argument_list|,
name|consumedMessage
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
name|consumer
operator|.
name|close
argument_list|()
expr_stmt|;
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
name|brokerService
operator|.
name|stop
argument_list|()
expr_stmt|;
block|}
comment|// helper method to create a broker with slow consumer advisory turned on
specifier|private
name|BrokerService
name|createBroker
parameter_list|()
throws|throws
name|Exception
block|{
name|BrokerService
name|broker
init|=
operator|new
name|BrokerService
argument_list|()
decl_stmt|;
name|broker
operator|.
name|setBrokerName
argument_list|(
literal|"localhost"
argument_list|)
expr_stmt|;
name|broker
operator|.
name|setUseJmx
argument_list|(
literal|false
argument_list|)
expr_stmt|;
name|broker
operator|.
name|setDeleteAllMessagesOnStartup
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|broker
operator|.
name|addConnector
argument_list|(
literal|"vm://localhost"
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
return|return
name|broker
return|;
block|}
block|}
end_class

end_unit

