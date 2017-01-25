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
name|assertEquals
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
name|ActiveMQSession
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
name|broker
operator|.
name|region
operator|.
name|policy
operator|.
name|ConstantPendingMessageLimitStrategy
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

begin_class
specifier|public
class|class
name|AMQ6579Test
block|{
specifier|protected
name|BrokerService
name|brokerService
decl_stmt|;
specifier|protected
name|Connection
name|connection
decl_stmt|;
specifier|protected
name|Session
name|session
decl_stmt|;
specifier|protected
name|ActiveMQTopic
name|topic
decl_stmt|;
specifier|protected
name|Destination
name|amqDestination
decl_stmt|;
specifier|protected
name|MessageConsumer
name|consumer
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
operator|new
name|BrokerService
argument_list|()
expr_stmt|;
name|brokerService
operator|.
name|setPersistent
argument_list|(
literal|false
argument_list|)
expr_stmt|;
name|PolicyEntry
name|policy
init|=
operator|new
name|PolicyEntry
argument_list|()
decl_stmt|;
name|policy
operator|.
name|setTopicPrefetch
argument_list|(
literal|5
argument_list|)
expr_stmt|;
name|ConstantPendingMessageLimitStrategy
name|pendingMessageLimitStrategy
init|=
operator|new
name|ConstantPendingMessageLimitStrategy
argument_list|()
decl_stmt|;
name|pendingMessageLimitStrategy
operator|.
name|setLimit
argument_list|(
literal|5
argument_list|)
expr_stmt|;
name|policy
operator|.
name|setPendingMessageLimitStrategy
argument_list|(
name|pendingMessageLimitStrategy
argument_list|)
expr_stmt|;
name|PolicyMap
name|pMap
init|=
operator|new
name|PolicyMap
argument_list|()
decl_stmt|;
name|pMap
operator|.
name|setDefaultEntry
argument_list|(
name|policy
argument_list|)
expr_stmt|;
name|brokerService
operator|.
name|setDestinationPolicy
argument_list|(
name|pMap
argument_list|)
expr_stmt|;
name|TransportConnector
name|tcp
init|=
name|brokerService
operator|.
name|addConnector
argument_list|(
literal|"tcp://localhost:0"
argument_list|)
decl_stmt|;
name|brokerService
operator|.
name|start
argument_list|()
expr_stmt|;
name|connection
operator|=
operator|new
name|ActiveMQConnectionFactory
argument_list|(
name|tcp
operator|.
name|getPublishableConnectString
argument_list|()
argument_list|)
operator|.
name|createConnection
argument_list|()
expr_stmt|;
name|connection
operator|.
name|start
argument_list|()
expr_stmt|;
name|session
operator|=
name|connection
operator|.
name|createSession
argument_list|(
literal|false
argument_list|,
name|ActiveMQSession
operator|.
name|AUTO_ACKNOWLEDGE
argument_list|)
expr_stmt|;
name|topic
operator|=
operator|new
name|ActiveMQTopic
argument_list|(
literal|"test.topic"
argument_list|)
expr_stmt|;
name|consumer
operator|=
name|session
operator|.
name|createConsumer
argument_list|(
name|topic
argument_list|)
expr_stmt|;
name|amqDestination
operator|=
name|TestSupport
operator|.
name|getDestination
argument_list|(
name|brokerService
argument_list|,
name|topic
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
if|if
condition|(
name|connection
operator|!=
literal|null
condition|)
block|{
name|connection
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
name|brokerService
operator|.
name|stop
argument_list|()
expr_stmt|;
block|}
comment|/**      * Test that messages are expired properly on a topic subscription when a      * constant pending limit strategy is set and that future messages are      * dispatched properly so that the consumer isn't blocked      *      * @throws Exception      */
annotation|@
name|Test
argument_list|(
name|timeout
operator|=
literal|10000
argument_list|)
specifier|public
name|void
name|testExpireWithPendingLimitStrategy
parameter_list|()
throws|throws
name|Exception
block|{
comment|//Send 5 messages that are not expired to fill up prefetch
comment|//followed by 5 messages that can be expired
comment|//then another 5 messages that won't expire
comment|//Make sure 10 messages are received
name|sendMessages
argument_list|(
literal|5
argument_list|,
literal|0
argument_list|)
expr_stmt|;
name|sendMessages
argument_list|(
literal|5
argument_list|,
literal|1
argument_list|)
expr_stmt|;
name|sendMessages
argument_list|(
literal|5
argument_list|,
literal|0
argument_list|)
expr_stmt|;
comment|//should get 10 messages as the middle 5 should expire
name|assertEquals
argument_list|(
literal|10
argument_list|,
name|receiveMessages
argument_list|()
argument_list|)
expr_stmt|;
block|}
comment|/**      * This method will generate random sized messages up to 150000 bytes.      *      * @param count      * @throws JMSException      */
specifier|protected
name|void
name|sendMessages
parameter_list|(
name|int
name|count
parameter_list|,
name|int
name|expire
parameter_list|)
throws|throws
name|JMSException
block|{
name|MessageProducer
name|producer
init|=
name|session
operator|.
name|createProducer
argument_list|(
name|topic
argument_list|)
decl_stmt|;
name|producer
operator|.
name|setTimeToLive
argument_list|(
name|expire
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
name|TextMessage
name|textMessage
init|=
name|session
operator|.
name|createTextMessage
argument_list|(
literal|"test"
argument_list|)
decl_stmt|;
name|producer
operator|.
name|send
argument_list|(
name|textMessage
argument_list|)
expr_stmt|;
block|}
block|}
specifier|protected
name|int
name|receiveMessages
parameter_list|()
throws|throws
name|JMSException
block|{
name|int
name|count
init|=
literal|0
decl_stmt|;
while|while
condition|(
name|consumer
operator|.
name|receive
argument_list|(
literal|500
argument_list|)
operator|!=
literal|null
condition|)
block|{
name|count
operator|++
expr_stmt|;
block|}
return|return
name|count
return|;
block|}
block|}
end_class

end_unit

