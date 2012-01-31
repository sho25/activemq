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
name|*
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
name|*
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
name|java
operator|.
name|util
operator|.
name|concurrent
operator|.
name|TimeUnit
import|;
end_import

begin_comment
comment|/**  * These test cases are used to verify that queue outbound bridge connections get  * re-established in all broker restart scenarios. This is possible when the  * outbound bridge is configured using the failover URI with a timeout.  */
end_comment

begin_class
specifier|public
class|class
name|TopicOutboundBridgeReconnectTest
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
name|TopicOutboundBridgeReconnectTest
operator|.
name|class
argument_list|)
decl_stmt|;
specifier|private
name|BrokerService
name|producerBroker
decl_stmt|;
specifier|private
name|BrokerService
name|consumerBroker
decl_stmt|;
specifier|private
name|ActiveMQConnectionFactory
name|producerConnectionFactory
decl_stmt|;
specifier|private
name|ActiveMQConnectionFactory
name|consumerConnectionFactory
decl_stmt|;
specifier|private
name|Destination
name|destination
decl_stmt|;
specifier|private
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
name|testMultipleProducerBrokerRestarts
parameter_list|()
throws|throws
name|Exception
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
literal|10
condition|;
name|i
operator|++
control|)
block|{
name|testWithProducerBrokerRestart
argument_list|()
expr_stmt|;
name|disposeConsumerConnections
argument_list|()
expr_stmt|;
block|}
block|}
annotation|@
name|Test
specifier|public
name|void
name|testWithoutRestartsConsumerFirst
parameter_list|()
throws|throws
name|Exception
block|{
name|startConsumerBroker
argument_list|()
expr_stmt|;
name|startProducerBroker
argument_list|()
expr_stmt|;
name|MessageConsumer
name|consumer
init|=
name|createConsumer
argument_list|()
decl_stmt|;
name|sendMessage
argument_list|(
literal|"test123"
argument_list|)
expr_stmt|;
name|sendMessage
argument_list|(
literal|"test456"
argument_list|)
expr_stmt|;
name|Message
name|message
init|=
name|consumer
operator|.
name|receive
argument_list|(
literal|2000
argument_list|)
decl_stmt|;
name|assertNotNull
argument_list|(
name|message
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"test123"
argument_list|,
operator|(
operator|(
name|TextMessage
operator|)
name|message
operator|)
operator|.
name|getText
argument_list|()
argument_list|)
expr_stmt|;
name|message
operator|=
name|consumer
operator|.
name|receive
argument_list|(
literal|5000
argument_list|)
expr_stmt|;
name|assertNotNull
argument_list|(
name|message
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"test456"
argument_list|,
operator|(
operator|(
name|TextMessage
operator|)
name|message
operator|)
operator|.
name|getText
argument_list|()
argument_list|)
expr_stmt|;
name|assertNull
argument_list|(
name|consumer
operator|.
name|receiveNoWait
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testWithoutRestartsProducerFirst
parameter_list|()
throws|throws
name|Exception
block|{
name|startProducerBroker
argument_list|()
expr_stmt|;
name|sendMessage
argument_list|(
literal|"test123"
argument_list|)
expr_stmt|;
name|startConsumerBroker
argument_list|()
expr_stmt|;
comment|// unless using a failover URI, the first attempt of this send will likely fail, so increase the timeout below
comment|// to give the bridge time to recover
name|sendMessage
argument_list|(
literal|"test456"
argument_list|)
expr_stmt|;
name|MessageConsumer
name|consumer
init|=
name|createConsumer
argument_list|()
decl_stmt|;
name|Message
name|message
init|=
name|consumer
operator|.
name|receive
argument_list|(
literal|5000
argument_list|)
decl_stmt|;
name|assertNotNull
argument_list|(
name|message
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"test123"
argument_list|,
operator|(
operator|(
name|TextMessage
operator|)
name|message
operator|)
operator|.
name|getText
argument_list|()
argument_list|)
expr_stmt|;
name|message
operator|=
name|consumer
operator|.
name|receive
argument_list|(
literal|5000
argument_list|)
expr_stmt|;
name|assertNotNull
argument_list|(
name|message
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"test456"
argument_list|,
operator|(
operator|(
name|TextMessage
operator|)
name|message
operator|)
operator|.
name|getText
argument_list|()
argument_list|)
expr_stmt|;
name|assertNull
argument_list|(
name|consumer
operator|.
name|receiveNoWait
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testWithProducerBrokerRestart
parameter_list|()
throws|throws
name|Exception
block|{
name|startProducerBroker
argument_list|()
expr_stmt|;
name|startConsumerBroker
argument_list|()
expr_stmt|;
name|MessageConsumer
name|consumer
init|=
name|createConsumer
argument_list|()
decl_stmt|;
name|sendMessage
argument_list|(
literal|"test123"
argument_list|)
expr_stmt|;
name|Message
name|message
init|=
name|consumer
operator|.
name|receive
argument_list|(
literal|5000
argument_list|)
decl_stmt|;
name|assertNotNull
argument_list|(
name|message
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"test123"
argument_list|,
operator|(
operator|(
name|TextMessage
operator|)
name|message
operator|)
operator|.
name|getText
argument_list|()
argument_list|)
expr_stmt|;
name|assertNull
argument_list|(
name|consumer
operator|.
name|receiveNoWait
argument_list|()
argument_list|)
expr_stmt|;
comment|// Restart the first broker...
name|stopProducerBroker
argument_list|()
expr_stmt|;
name|startProducerBroker
argument_list|()
expr_stmt|;
name|sendMessage
argument_list|(
literal|"test123"
argument_list|)
expr_stmt|;
name|message
operator|=
name|consumer
operator|.
name|receive
argument_list|(
literal|5000
argument_list|)
expr_stmt|;
name|assertNotNull
argument_list|(
name|message
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"test123"
argument_list|,
operator|(
operator|(
name|TextMessage
operator|)
name|message
operator|)
operator|.
name|getText
argument_list|()
argument_list|)
expr_stmt|;
name|assertNull
argument_list|(
name|consumer
operator|.
name|receiveNoWait
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testWithConsumerBrokerRestart
parameter_list|()
throws|throws
name|Exception
block|{
name|startProducerBroker
argument_list|()
expr_stmt|;
name|startConsumerBroker
argument_list|()
expr_stmt|;
specifier|final
name|MessageConsumer
name|consumer1
init|=
name|createConsumer
argument_list|()
decl_stmt|;
name|sendMessage
argument_list|(
literal|"test123"
argument_list|)
expr_stmt|;
name|Message
name|message
init|=
name|consumer1
operator|.
name|receive
argument_list|(
literal|5000
argument_list|)
decl_stmt|;
name|assertNotNull
argument_list|(
name|message
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"test123"
argument_list|,
operator|(
operator|(
name|TextMessage
operator|)
name|message
operator|)
operator|.
name|getText
argument_list|()
argument_list|)
expr_stmt|;
name|assertNull
argument_list|(
name|consumer1
operator|.
name|receiveNoWait
argument_list|()
argument_list|)
expr_stmt|;
name|consumer1
operator|.
name|close
argument_list|()
expr_stmt|;
comment|// Restart the first broker...
name|stopConsumerBroker
argument_list|()
expr_stmt|;
name|startConsumerBroker
argument_list|()
expr_stmt|;
comment|// unless using a failover URI, the first attempt of this send will likely fail, so increase the timeout below
comment|// to give the bridge time to recover
name|sendMessage
argument_list|(
literal|"test123"
argument_list|)
expr_stmt|;
specifier|final
name|MessageConsumer
name|consumer2
init|=
name|createConsumer
argument_list|()
decl_stmt|;
name|assertTrue
argument_list|(
literal|"Expected recover and delivery failed"
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
name|consumer2
operator|.
name|receiveNoWait
argument_list|()
decl_stmt|;
if|if
condition|(
name|message
operator|==
literal|null
operator|||
operator|!
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
literal|"test123"
argument_list|)
condition|)
block|{
return|return
literal|false
return|;
block|}
return|return
literal|true
return|;
block|}
block|}
argument_list|)
argument_list|)
expr_stmt|;
name|assertNull
argument_list|(
name|consumer2
operator|.
name|receiveNoWait
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testWithConsumerBrokerStartDelay
parameter_list|()
throws|throws
name|Exception
block|{
name|startConsumerBroker
argument_list|()
expr_stmt|;
specifier|final
name|MessageConsumer
name|consumer
init|=
name|createConsumer
argument_list|()
decl_stmt|;
name|TimeUnit
operator|.
name|SECONDS
operator|.
name|sleep
argument_list|(
literal|5
argument_list|)
expr_stmt|;
name|startProducerBroker
argument_list|()
expr_stmt|;
name|sendMessage
argument_list|(
literal|"test123"
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
literal|"Expected recover and delivery failed"
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
name|consumer
operator|.
name|receiveNoWait
argument_list|()
decl_stmt|;
if|if
condition|(
name|message
operator|==
literal|null
operator|||
operator|!
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
literal|"test123"
argument_list|)
condition|)
block|{
return|return
literal|false
return|;
block|}
return|return
literal|true
return|;
block|}
block|}
argument_list|)
argument_list|)
expr_stmt|;
name|assertNull
argument_list|(
name|consumer
operator|.
name|receiveNoWait
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testWithProducerBrokerStartDelay
parameter_list|()
throws|throws
name|Exception
block|{
name|startProducerBroker
argument_list|()
expr_stmt|;
name|TimeUnit
operator|.
name|SECONDS
operator|.
name|sleep
argument_list|(
literal|5
argument_list|)
expr_stmt|;
name|startConsumerBroker
argument_list|()
expr_stmt|;
name|MessageConsumer
name|consumer
init|=
name|createConsumer
argument_list|()
decl_stmt|;
name|sendMessage
argument_list|(
literal|"test123"
argument_list|)
expr_stmt|;
name|Message
name|message
init|=
name|consumer
operator|.
name|receive
argument_list|(
literal|2000
argument_list|)
decl_stmt|;
name|assertNotNull
argument_list|(
name|message
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"test123"
argument_list|,
operator|(
operator|(
name|TextMessage
operator|)
name|message
operator|)
operator|.
name|getText
argument_list|()
argument_list|)
expr_stmt|;
name|assertNull
argument_list|(
name|consumer
operator|.
name|receiveNoWait
argument_list|()
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
name|producerConnectionFactory
operator|=
name|createProducerConnectionFactory
argument_list|()
expr_stmt|;
name|consumerConnectionFactory
operator|=
name|createConsumerConnectionFactory
argument_list|()
expr_stmt|;
name|destination
operator|=
operator|new
name|ActiveMQTopic
argument_list|(
literal|"RECONNECT.TEST.TOPIC"
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
name|stopProducerBroker
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
name|stopConsumerBroker
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
name|startProducerBroker
parameter_list|()
throws|throws
name|Exception
block|{
if|if
condition|(
name|producerBroker
operator|==
literal|null
condition|)
block|{
name|producerBroker
operator|=
name|createFirstBroker
argument_list|()
expr_stmt|;
name|producerBroker
operator|.
name|start
argument_list|()
expr_stmt|;
block|}
block|}
specifier|protected
name|void
name|stopProducerBroker
parameter_list|()
throws|throws
name|Exception
block|{
if|if
condition|(
name|producerBroker
operator|!=
literal|null
condition|)
block|{
name|producerBroker
operator|.
name|stop
argument_list|()
expr_stmt|;
name|producerBroker
operator|=
literal|null
expr_stmt|;
block|}
block|}
specifier|protected
name|void
name|startConsumerBroker
parameter_list|()
throws|throws
name|Exception
block|{
if|if
condition|(
name|consumerBroker
operator|==
literal|null
condition|)
block|{
name|consumerBroker
operator|=
name|createSecondBroker
argument_list|()
expr_stmt|;
name|consumerBroker
operator|.
name|start
argument_list|()
expr_stmt|;
block|}
block|}
specifier|protected
name|void
name|stopConsumerBroker
parameter_list|()
throws|throws
name|Exception
block|{
if|if
condition|(
name|consumerBroker
operator|!=
literal|null
condition|)
block|{
name|consumerBroker
operator|.
name|stop
argument_list|()
expr_stmt|;
name|consumerBroker
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
name|broker
operator|.
name|addConnector
argument_list|(
literal|"vm://broker1"
argument_list|)
expr_stmt|;
name|JmsTopicConnector
name|jmsTopicConnector
init|=
operator|new
name|JmsTopicConnector
argument_list|()
decl_stmt|;
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
literal|"RECONNECT.TEST.TOPIC"
argument_list|)
block|}
argument_list|)
expr_stmt|;
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
name|broker
operator|.
name|setJmsBridgeConnectors
argument_list|(
operator|new
name|JmsConnector
index|[]
block|{
name|jmsTopicConnector
block|}
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
name|broker
operator|.
name|addConnector
argument_list|(
literal|"vm://broker2"
argument_list|)
expr_stmt|;
return|return
name|broker
return|;
block|}
specifier|protected
name|ActiveMQConnectionFactory
name|createProducerConnectionFactory
parameter_list|()
block|{
return|return
operator|new
name|ActiveMQConnectionFactory
argument_list|(
literal|"vm://broker1"
argument_list|)
return|;
block|}
specifier|protected
name|ActiveMQConnectionFactory
name|createConsumerConnectionFactory
parameter_list|()
block|{
return|return
operator|new
name|ActiveMQConnectionFactory
argument_list|(
literal|"vm://broker2"
argument_list|)
return|;
block|}
specifier|protected
name|void
name|sendMessage
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
name|producerConnectionFactory
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
name|destination
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
name|createConsumer
parameter_list|()
throws|throws
name|JMSException
block|{
name|Connection
name|connection
init|=
name|consumerConnectionFactory
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
name|destination
argument_list|)
return|;
block|}
block|}
end_class

end_unit

