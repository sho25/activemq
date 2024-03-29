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
operator|.
name|jms
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
name|assertTrue
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
name|Iterator
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

begin_class
specifier|public
class|class
name|TopicBridgeStandaloneReconnectTest
block|{
specifier|private
name|SimpleJmsTopicConnector
name|jmsTopicConnector
decl_stmt|;
specifier|private
name|BrokerService
name|localBroker
decl_stmt|;
specifier|private
name|BrokerService
name|foreignBroker
decl_stmt|;
specifier|private
name|ActiveMQConnectionFactory
name|localConnectionFactory
decl_stmt|;
specifier|private
name|ActiveMQConnectionFactory
name|foreignConnectionFactory
decl_stmt|;
specifier|private
name|Destination
name|outbound
decl_stmt|;
specifier|private
name|Destination
name|inbound
decl_stmt|;
specifier|private
specifier|final
name|ArrayList
argument_list|<
name|Connection
argument_list|>
name|connections
init|=
operator|new
name|ArrayList
argument_list|<
name|Connection
argument_list|>
argument_list|()
decl_stmt|;
annotation|@
name|Test
specifier|public
name|void
name|testSendAndReceiveOverConnectedBridges
parameter_list|()
throws|throws
name|Exception
block|{
name|startLocalBroker
argument_list|()
expr_stmt|;
name|startForeignBroker
argument_list|()
expr_stmt|;
name|jmsTopicConnector
operator|.
name|start
argument_list|()
expr_stmt|;
specifier|final
name|MessageConsumer
name|local
init|=
name|createConsumerForLocalBroker
argument_list|()
decl_stmt|;
specifier|final
name|MessageConsumer
name|foreign
init|=
name|createConsumerForForeignBroker
argument_list|()
decl_stmt|;
name|sendMessageToForeignBroker
argument_list|(
literal|"to.foreign.broker"
argument_list|)
expr_stmt|;
name|sendMessageToLocalBroker
argument_list|(
literal|"to.local.broker"
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
literal|"Should have received a Message."
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
name|Message
name|message
init|=
name|local
operator|.
name|receive
argument_list|(
literal|100
argument_list|)
decl_stmt|;
if|if
condition|(
name|message
operator|!=
literal|null
operator|&&
operator|(
operator|(
name|TextMessage
operator|)
name|message
operator|)
operator|.
name|getText
argument_list|()
operator|.
name|equals
argument_list|(
literal|"to.local.broker"
argument_list|)
condition|)
block|{
return|return
literal|true
return|;
block|}
return|return
literal|false
return|;
block|}
block|}
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
literal|"Should have received a Message."
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
name|Message
name|message
init|=
name|foreign
operator|.
name|receive
argument_list|(
literal|100
argument_list|)
decl_stmt|;
if|if
condition|(
name|message
operator|!=
literal|null
operator|&&
operator|(
operator|(
name|TextMessage
operator|)
name|message
operator|)
operator|.
name|getText
argument_list|()
operator|.
name|equals
argument_list|(
literal|"to.foreign.broker"
argument_list|)
condition|)
block|{
return|return
literal|true
return|;
block|}
return|return
literal|false
return|;
block|}
block|}
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testSendAndReceiveOverBridgeWhenStartedBeforeBrokers
parameter_list|()
throws|throws
name|Exception
block|{
name|jmsTopicConnector
operator|.
name|start
argument_list|()
expr_stmt|;
name|startLocalBroker
argument_list|()
expr_stmt|;
name|startForeignBroker
argument_list|()
expr_stmt|;
name|assertTrue
argument_list|(
literal|"Should have Connected."
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
name|jmsTopicConnector
operator|.
name|isConnected
argument_list|()
return|;
block|}
block|}
argument_list|)
argument_list|)
expr_stmt|;
specifier|final
name|MessageConsumer
name|local
init|=
name|createConsumerForLocalBroker
argument_list|()
decl_stmt|;
specifier|final
name|MessageConsumer
name|foreign
init|=
name|createConsumerForForeignBroker
argument_list|()
decl_stmt|;
name|sendMessageToForeignBroker
argument_list|(
literal|"to.foreign.broker"
argument_list|)
expr_stmt|;
name|sendMessageToLocalBroker
argument_list|(
literal|"to.local.broker"
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
literal|"Should have received a Message."
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
name|Message
name|message
init|=
name|local
operator|.
name|receive
argument_list|(
literal|100
argument_list|)
decl_stmt|;
if|if
condition|(
name|message
operator|!=
literal|null
operator|&&
operator|(
operator|(
name|TextMessage
operator|)
name|message
operator|)
operator|.
name|getText
argument_list|()
operator|.
name|equals
argument_list|(
literal|"to.local.broker"
argument_list|)
condition|)
block|{
return|return
literal|true
return|;
block|}
return|return
literal|false
return|;
block|}
block|}
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
literal|"Should have received a Message."
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
name|Message
name|message
init|=
name|foreign
operator|.
name|receive
argument_list|(
literal|100
argument_list|)
decl_stmt|;
if|if
condition|(
name|message
operator|!=
literal|null
operator|&&
operator|(
operator|(
name|TextMessage
operator|)
name|message
operator|)
operator|.
name|getText
argument_list|()
operator|.
name|equals
argument_list|(
literal|"to.foreign.broker"
argument_list|)
condition|)
block|{
return|return
literal|true
return|;
block|}
return|return
literal|false
return|;
block|}
block|}
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testSendAndReceiveOverBridgeWithRestart
parameter_list|()
throws|throws
name|Exception
block|{
name|startLocalBroker
argument_list|()
expr_stmt|;
name|startForeignBroker
argument_list|()
expr_stmt|;
name|jmsTopicConnector
operator|.
name|start
argument_list|()
expr_stmt|;
name|assertTrue
argument_list|(
literal|"Should have Connected."
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
name|jmsTopicConnector
operator|.
name|isConnected
argument_list|()
return|;
block|}
block|}
argument_list|)
argument_list|)
expr_stmt|;
name|stopLocalBroker
argument_list|()
expr_stmt|;
name|stopForeignBroker
argument_list|()
expr_stmt|;
name|assertTrue
argument_list|(
literal|"Should have detected connection drop."
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
operator|!
name|jmsTopicConnector
operator|.
name|isConnected
argument_list|()
return|;
block|}
block|}
argument_list|)
argument_list|)
expr_stmt|;
name|startLocalBroker
argument_list|()
expr_stmt|;
name|startForeignBroker
argument_list|()
expr_stmt|;
name|assertTrue
argument_list|(
literal|"Should have Re-Connected."
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
name|jmsTopicConnector
operator|.
name|isConnected
argument_list|()
return|;
block|}
block|}
argument_list|)
argument_list|)
expr_stmt|;
specifier|final
name|MessageConsumer
name|local
init|=
name|createConsumerForLocalBroker
argument_list|()
decl_stmt|;
specifier|final
name|MessageConsumer
name|foreign
init|=
name|createConsumerForForeignBroker
argument_list|()
decl_stmt|;
name|sendMessageToForeignBroker
argument_list|(
literal|"to.foreign.broker"
argument_list|)
expr_stmt|;
name|sendMessageToLocalBroker
argument_list|(
literal|"to.local.broker"
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
literal|"Should have received a Message."
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
name|Message
name|message
init|=
name|local
operator|.
name|receive
argument_list|(
literal|100
argument_list|)
decl_stmt|;
if|if
condition|(
name|message
operator|!=
literal|null
operator|&&
operator|(
operator|(
name|TextMessage
operator|)
name|message
operator|)
operator|.
name|getText
argument_list|()
operator|.
name|equals
argument_list|(
literal|"to.local.broker"
argument_list|)
condition|)
block|{
return|return
literal|true
return|;
block|}
return|return
literal|false
return|;
block|}
block|}
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
literal|"Should have received a Message."
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
name|Message
name|message
init|=
name|foreign
operator|.
name|receive
argument_list|(
literal|100
argument_list|)
decl_stmt|;
if|if
condition|(
name|message
operator|!=
literal|null
operator|&&
operator|(
operator|(
name|TextMessage
operator|)
name|message
operator|)
operator|.
name|getText
argument_list|()
operator|.
name|equals
argument_list|(
literal|"to.foreign.broker"
argument_list|)
condition|)
block|{
return|return
literal|true
return|;
block|}
return|return
literal|false
return|;
block|}
block|}
argument_list|)
argument_list|)
expr_stmt|;
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
name|localConnectionFactory
operator|=
name|createLocalConnectionFactory
argument_list|()
expr_stmt|;
name|foreignConnectionFactory
operator|=
name|createForeignConnectionFactory
argument_list|()
expr_stmt|;
name|outbound
operator|=
operator|new
name|ActiveMQTopic
argument_list|(
literal|"RECONNECT.TEST.OUT.TOPIC"
argument_list|)
expr_stmt|;
name|inbound
operator|=
operator|new
name|ActiveMQTopic
argument_list|(
literal|"RECONNECT.TEST.IN.TOPIC"
argument_list|)
expr_stmt|;
name|jmsTopicConnector
operator|=
operator|new
name|SimpleJmsTopicConnector
argument_list|()
expr_stmt|;
comment|// Wire the bridges.
name|jmsTopicConnector
operator|.
name|setOutboundTopicBridges
argument_list|(
operator|new
name|OutboundTopicBridge
index|[]
block|{
operator|new
name|OutboundTopicBridge
argument_list|(
literal|"RECONNECT.TEST.OUT.TOPIC"
argument_list|)
block|}
argument_list|)
expr_stmt|;
name|jmsTopicConnector
operator|.
name|setInboundTopicBridges
argument_list|(
operator|new
name|InboundTopicBridge
index|[]
block|{
operator|new
name|InboundTopicBridge
argument_list|(
literal|"RECONNECT.TEST.IN.TOPIC"
argument_list|)
block|}
argument_list|)
expr_stmt|;
comment|// Tell it how to reach the two brokers.
name|jmsTopicConnector
operator|.
name|setOutboundTopicConnectionFactory
argument_list|(
operator|new
name|ActiveMQConnectionFactory
argument_list|(
literal|"tcp://localhost:61617"
argument_list|)
argument_list|)
expr_stmt|;
name|jmsTopicConnector
operator|.
name|setLocalTopicConnectionFactory
argument_list|(
operator|new
name|ActiveMQConnectionFactory
argument_list|(
literal|"tcp://localhost:61616"
argument_list|)
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
name|disposeConsumerConnections
argument_list|()
expr_stmt|;
try|try
block|{
name|jmsTopicConnector
operator|.
name|stop
argument_list|()
expr_stmt|;
name|jmsTopicConnector
operator|=
literal|null
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{         }
try|try
block|{
name|stopLocalBroker
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Throwable
name|e
parameter_list|)
block|{         }
try|try
block|{
name|stopForeignBroker
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Throwable
name|e
parameter_list|)
block|{         }
block|}
specifier|protected
name|void
name|disposeConsumerConnections
parameter_list|()
block|{
for|for
control|(
name|Iterator
argument_list|<
name|Connection
argument_list|>
name|iter
init|=
name|connections
operator|.
name|iterator
argument_list|()
init|;
name|iter
operator|.
name|hasNext
argument_list|()
condition|;
control|)
block|{
name|Connection
name|connection
init|=
name|iter
operator|.
name|next
argument_list|()
decl_stmt|;
try|try
block|{
name|connection
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Throwable
name|ignore
parameter_list|)
block|{             }
block|}
block|}
specifier|protected
name|void
name|startLocalBroker
parameter_list|()
throws|throws
name|Exception
block|{
if|if
condition|(
name|localBroker
operator|==
literal|null
condition|)
block|{
name|localBroker
operator|=
name|createFirstBroker
argument_list|()
expr_stmt|;
name|localBroker
operator|.
name|start
argument_list|()
expr_stmt|;
name|localBroker
operator|.
name|waitUntilStarted
argument_list|()
expr_stmt|;
block|}
block|}
specifier|protected
name|void
name|stopLocalBroker
parameter_list|()
throws|throws
name|Exception
block|{
if|if
condition|(
name|localBroker
operator|!=
literal|null
condition|)
block|{
name|localBroker
operator|.
name|stop
argument_list|()
expr_stmt|;
name|localBroker
operator|.
name|waitUntilStopped
argument_list|()
expr_stmt|;
name|localBroker
operator|=
literal|null
expr_stmt|;
block|}
block|}
specifier|protected
name|void
name|startForeignBroker
parameter_list|()
throws|throws
name|Exception
block|{
if|if
condition|(
name|foreignBroker
operator|==
literal|null
condition|)
block|{
name|foreignBroker
operator|=
name|createSecondBroker
argument_list|()
expr_stmt|;
name|foreignBroker
operator|.
name|start
argument_list|()
expr_stmt|;
name|foreignBroker
operator|.
name|waitUntilStarted
argument_list|()
expr_stmt|;
block|}
block|}
specifier|protected
name|void
name|stopForeignBroker
parameter_list|()
throws|throws
name|Exception
block|{
if|if
condition|(
name|foreignBroker
operator|!=
literal|null
condition|)
block|{
name|foreignBroker
operator|.
name|stop
argument_list|()
expr_stmt|;
name|foreignBroker
operator|.
name|waitUntilStopped
argument_list|()
expr_stmt|;
name|foreignBroker
operator|=
literal|null
expr_stmt|;
block|}
block|}
specifier|protected
name|BrokerService
name|createFirstBroker
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
literal|"broker1"
argument_list|)
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
literal|false
argument_list|)
expr_stmt|;
name|broker
operator|.
name|addConnector
argument_list|(
literal|"tcp://localhost:61616"
argument_list|)
expr_stmt|;
return|return
name|broker
return|;
block|}
specifier|protected
name|BrokerService
name|createSecondBroker
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
literal|"broker2"
argument_list|)
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
literal|false
argument_list|)
expr_stmt|;
name|broker
operator|.
name|addConnector
argument_list|(
literal|"tcp://localhost:61617"
argument_list|)
expr_stmt|;
return|return
name|broker
return|;
block|}
specifier|protected
name|ActiveMQConnectionFactory
name|createLocalConnectionFactory
parameter_list|()
block|{
return|return
operator|new
name|ActiveMQConnectionFactory
argument_list|(
literal|"tcp://localhost:61616"
argument_list|)
return|;
block|}
specifier|protected
name|ActiveMQConnectionFactory
name|createForeignConnectionFactory
parameter_list|()
block|{
return|return
operator|new
name|ActiveMQConnectionFactory
argument_list|(
literal|"tcp://localhost:61617"
argument_list|)
return|;
block|}
specifier|protected
name|void
name|sendMessageToForeignBroker
parameter_list|(
name|String
name|text
parameter_list|)
throws|throws
name|JMSException
block|{
name|Connection
name|connection
init|=
literal|null
decl_stmt|;
try|try
block|{
name|connection
operator|=
name|localConnectionFactory
operator|.
name|createConnection
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
name|outbound
argument_list|)
decl_stmt|;
name|TextMessage
name|message
init|=
name|session
operator|.
name|createTextMessage
argument_list|()
decl_stmt|;
name|message
operator|.
name|setText
argument_list|(
name|text
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
finally|finally
block|{
try|try
block|{
name|connection
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Throwable
name|ignore
parameter_list|)
block|{             }
block|}
block|}
specifier|protected
name|void
name|sendMessageToLocalBroker
parameter_list|(
name|String
name|text
parameter_list|)
throws|throws
name|JMSException
block|{
name|Connection
name|connection
init|=
literal|null
decl_stmt|;
try|try
block|{
name|connection
operator|=
name|foreignConnectionFactory
operator|.
name|createConnection
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
name|inbound
argument_list|)
decl_stmt|;
name|TextMessage
name|message
init|=
name|session
operator|.
name|createTextMessage
argument_list|()
decl_stmt|;
name|message
operator|.
name|setText
argument_list|(
name|text
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
finally|finally
block|{
try|try
block|{
name|connection
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Throwable
name|ignore
parameter_list|)
block|{             }
block|}
block|}
specifier|protected
name|MessageConsumer
name|createConsumerForLocalBroker
parameter_list|()
throws|throws
name|JMSException
block|{
name|Connection
name|connection
init|=
name|localConnectionFactory
operator|.
name|createConnection
argument_list|()
decl_stmt|;
name|connections
operator|.
name|add
argument_list|(
name|connection
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
name|Session
operator|.
name|AUTO_ACKNOWLEDGE
argument_list|)
decl_stmt|;
return|return
name|session
operator|.
name|createConsumer
argument_list|(
name|inbound
argument_list|)
return|;
block|}
specifier|protected
name|MessageConsumer
name|createConsumerForForeignBroker
parameter_list|()
throws|throws
name|JMSException
block|{
name|Connection
name|connection
init|=
name|foreignConnectionFactory
operator|.
name|createConnection
argument_list|()
decl_stmt|;
name|connections
operator|.
name|add
argument_list|(
name|connection
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
name|Session
operator|.
name|AUTO_ACKNOWLEDGE
argument_list|)
decl_stmt|;
return|return
name|session
operator|.
name|createConsumer
argument_list|(
name|outbound
argument_list|)
return|;
block|}
block|}
end_class

end_unit

