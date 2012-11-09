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
name|http
package|;
end_package

begin_import
import|import
name|java
operator|.
name|net
operator|.
name|URISyntaxException
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
name|atomic
operator|.
name|AtomicInteger
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|jms
operator|.
name|BytesMessage
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
name|ConnectionFactory
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
name|MapMessage
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
name|ObjectMessage
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
name|StreamMessage
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
name|HttpJMSMessagesWithCompressionTest
block|{
specifier|private
specifier|static
specifier|final
name|AtomicInteger
name|counter
init|=
operator|new
name|AtomicInteger
argument_list|(
literal|1
argument_list|)
decl_stmt|;
enum|enum
name|DESTINATION_TYPE
block|{
name|TOPIC
block|,
name|QUEUE
block|}
empty_stmt|;
specifier|protected
name|BrokerService
name|broker
decl_stmt|;
specifier|protected
name|Connection
name|connection
decl_stmt|;
specifier|protected
name|DESTINATION_TYPE
name|destinationType
init|=
name|DESTINATION_TYPE
operator|.
name|QUEUE
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
name|broker
operator|=
name|createBroker
argument_list|()
expr_stmt|;
name|broker
operator|.
name|start
argument_list|()
expr_stmt|;
name|WaitForJettyListener
operator|.
name|waitForJettySocketToAccept
argument_list|(
name|getBrokerURL
argument_list|()
argument_list|)
expr_stmt|;
name|ConnectionFactory
name|factory
init|=
name|createConnectionFactory
argument_list|()
decl_stmt|;
name|connection
operator|=
name|factory
operator|.
name|createConnection
argument_list|()
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
if|if
condition|(
name|broker
operator|!=
literal|null
condition|)
block|{
name|broker
operator|.
name|stop
argument_list|()
expr_stmt|;
block|}
block|}
specifier|protected
name|ConnectionFactory
name|createConnectionFactory
parameter_list|()
throws|throws
name|URISyntaxException
block|{
name|ActiveMQConnectionFactory
name|factory
init|=
operator|new
name|ActiveMQConnectionFactory
argument_list|(
name|getBrokerURL
argument_list|()
argument_list|)
decl_stmt|;
return|return
name|factory
return|;
block|}
specifier|protected
name|String
name|getBrokerURL
parameter_list|()
block|{
return|return
literal|"http://localhost:8161?useCompression=true"
return|;
block|}
specifier|protected
name|BrokerService
name|createBroker
parameter_list|()
throws|throws
name|Exception
block|{
name|BrokerService
name|answer
init|=
operator|new
name|BrokerService
argument_list|()
decl_stmt|;
name|answer
operator|.
name|setPersistent
argument_list|(
literal|false
argument_list|)
expr_stmt|;
name|answer
operator|.
name|setUseJmx
argument_list|(
literal|false
argument_list|)
expr_stmt|;
name|answer
operator|.
name|setManagementContext
argument_list|(
literal|null
argument_list|)
expr_stmt|;
name|answer
operator|.
name|addConnector
argument_list|(
name|getBrokerURL
argument_list|()
argument_list|)
expr_stmt|;
return|return
name|answer
return|;
block|}
specifier|protected
name|Destination
name|createDestination
parameter_list|(
name|Session
name|session
parameter_list|,
name|DESTINATION_TYPE
name|destinationType
parameter_list|)
throws|throws
name|JMSException
block|{
switch|switch
condition|(
name|destinationType
condition|)
block|{
case|case
name|TOPIC
case|:
return|return
name|session
operator|.
name|createTopic
argument_list|(
literal|"TOPIC."
operator|+
name|counter
operator|.
name|getAndIncrement
argument_list|()
argument_list|)
return|;
case|case
name|QUEUE
case|:
return|return
name|session
operator|.
name|createQueue
argument_list|(
literal|"QUEUE."
operator|+
name|counter
operator|.
name|getAndIncrement
argument_list|()
argument_list|)
return|;
block|}
name|Assert
operator|.
name|fail
argument_list|(
literal|"Invalid destination type: "
operator|+
name|destinationType
argument_list|)
expr_stmt|;
return|return
literal|null
return|;
block|}
specifier|abstract
class|class
name|MessageCommand
parameter_list|<
name|M
extends|extends
name|Message
parameter_list|>
block|{
specifier|public
specifier|final
name|void
name|assertMessage
parameter_list|(
name|M
name|message
parameter_list|)
throws|throws
name|JMSException
block|{
name|Assert
operator|.
name|assertNotNull
argument_list|(
name|message
argument_list|)
expr_stmt|;
name|completeCheck
argument_list|(
name|message
argument_list|)
expr_stmt|;
block|}
specifier|public
specifier|abstract
name|void
name|completeCheck
parameter_list|(
name|M
name|message
parameter_list|)
throws|throws
name|JMSException
function_decl|;
specifier|public
specifier|abstract
name|M
name|createMessage
parameter_list|(
name|Session
name|session
parameter_list|)
throws|throws
name|JMSException
function_decl|;
block|}
annotation|@
name|SuppressWarnings
argument_list|(
literal|"unchecked"
argument_list|)
specifier|private
parameter_list|<
name|E
extends|extends
name|Message
parameter_list|>
name|void
name|executeTest
parameter_list|(
name|MessageCommand
argument_list|<
name|E
argument_list|>
name|messageCommand
parameter_list|)
throws|throws
name|JMSException
block|{
comment|// Receive a message with the JMS API
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
name|Destination
name|destination
init|=
name|createDestination
argument_list|(
name|session
argument_list|,
name|destinationType
argument_list|)
decl_stmt|;
name|MessageConsumer
name|consumer
init|=
name|session
operator|.
name|createConsumer
argument_list|(
name|destination
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
block|{
name|E
name|message
init|=
name|messageCommand
operator|.
name|createMessage
argument_list|(
name|session
argument_list|)
decl_stmt|;
name|producer
operator|.
name|send
argument_list|(
name|message
argument_list|)
expr_stmt|;
block|}
block|{
name|E
name|message
init|=
operator|(
name|E
operator|)
name|consumer
operator|.
name|receive
argument_list|(
literal|1000
argument_list|)
decl_stmt|;
name|messageCommand
operator|.
name|assertMessage
argument_list|(
name|message
argument_list|)
expr_stmt|;
block|}
name|Assert
operator|.
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
name|testTextMessage
parameter_list|()
throws|throws
name|Exception
block|{
name|executeTest
argument_list|(
operator|new
name|MessageCommand
argument_list|<
name|TextMessage
argument_list|>
argument_list|()
block|{
specifier|private
name|String
name|textString
init|=
literal|"This is a simple text string"
decl_stmt|;
specifier|public
name|TextMessage
name|createMessage
parameter_list|(
name|Session
name|session
parameter_list|)
throws|throws
name|JMSException
block|{
return|return
name|session
operator|.
name|createTextMessage
argument_list|(
name|textString
argument_list|)
return|;
block|}
specifier|public
name|void
name|completeCheck
parameter_list|(
name|TextMessage
name|message
parameter_list|)
throws|throws
name|JMSException
block|{
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|"The returned text string was different"
argument_list|,
name|textString
argument_list|,
name|message
operator|.
name|getText
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testBytesMessage
parameter_list|()
throws|throws
name|Exception
block|{
name|executeTest
argument_list|(
operator|new
name|MessageCommand
argument_list|<
name|BytesMessage
argument_list|>
argument_list|()
block|{
specifier|private
name|byte
index|[]
name|bytes
init|=
literal|"This is a simple text string"
operator|.
name|getBytes
argument_list|()
decl_stmt|;
specifier|public
name|BytesMessage
name|createMessage
parameter_list|(
name|Session
name|session
parameter_list|)
throws|throws
name|JMSException
block|{
name|BytesMessage
name|message
init|=
name|session
operator|.
name|createBytesMessage
argument_list|()
decl_stmt|;
name|message
operator|.
name|writeBytes
argument_list|(
name|bytes
argument_list|)
expr_stmt|;
return|return
name|message
return|;
block|}
specifier|public
name|void
name|completeCheck
parameter_list|(
name|BytesMessage
name|message
parameter_list|)
throws|throws
name|JMSException
block|{
name|byte
index|[]
name|result
init|=
operator|new
name|byte
index|[
name|bytes
operator|.
name|length
index|]
decl_stmt|;
name|message
operator|.
name|readBytes
argument_list|(
name|result
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertArrayEquals
argument_list|(
literal|"The returned byte array was different"
argument_list|,
name|bytes
argument_list|,
name|result
argument_list|)
expr_stmt|;
block|}
block|}
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testMapMessage
parameter_list|()
throws|throws
name|Exception
block|{
name|executeTest
argument_list|(
operator|new
name|MessageCommand
argument_list|<
name|MapMessage
argument_list|>
argument_list|()
block|{
specifier|public
name|MapMessage
name|createMessage
parameter_list|(
name|Session
name|session
parameter_list|)
throws|throws
name|JMSException
block|{
name|MapMessage
name|message
init|=
name|session
operator|.
name|createMapMessage
argument_list|()
decl_stmt|;
name|message
operator|.
name|setInt
argument_list|(
literal|"value"
argument_list|,
literal|13
argument_list|)
expr_stmt|;
return|return
name|message
return|;
block|}
specifier|public
name|void
name|completeCheck
parameter_list|(
name|MapMessage
name|message
parameter_list|)
throws|throws
name|JMSException
block|{
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|"The returned mapped value was different"
argument_list|,
literal|13
argument_list|,
name|message
operator|.
name|getInt
argument_list|(
literal|"value"
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testObjectMessage
parameter_list|()
throws|throws
name|Exception
block|{
name|executeTest
argument_list|(
operator|new
name|MessageCommand
argument_list|<
name|ObjectMessage
argument_list|>
argument_list|()
block|{
specifier|private
name|Long
name|value
init|=
operator|new
name|Long
argument_list|(
literal|101
argument_list|)
decl_stmt|;
specifier|public
name|ObjectMessage
name|createMessage
parameter_list|(
name|Session
name|session
parameter_list|)
throws|throws
name|JMSException
block|{
return|return
name|session
operator|.
name|createObjectMessage
argument_list|(
name|value
argument_list|)
return|;
block|}
specifier|public
name|void
name|completeCheck
parameter_list|(
name|ObjectMessage
name|message
parameter_list|)
throws|throws
name|JMSException
block|{
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|"The returned object was different"
argument_list|,
name|value
argument_list|,
name|message
operator|.
name|getObject
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testStreamMessage
parameter_list|()
throws|throws
name|Exception
block|{
name|executeTest
argument_list|(
operator|new
name|MessageCommand
argument_list|<
name|StreamMessage
argument_list|>
argument_list|()
block|{
specifier|private
name|Long
name|value
init|=
operator|new
name|Long
argument_list|(
literal|1013
argument_list|)
decl_stmt|;
specifier|public
name|StreamMessage
name|createMessage
parameter_list|(
name|Session
name|session
parameter_list|)
throws|throws
name|JMSException
block|{
name|StreamMessage
name|message
init|=
name|session
operator|.
name|createStreamMessage
argument_list|()
decl_stmt|;
name|message
operator|.
name|writeObject
argument_list|(
name|value
argument_list|)
expr_stmt|;
return|return
name|message
return|;
block|}
specifier|public
name|void
name|completeCheck
parameter_list|(
name|StreamMessage
name|message
parameter_list|)
throws|throws
name|JMSException
block|{
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|"The returned stream object was different"
argument_list|,
name|value
argument_list|,
name|message
operator|.
name|readObject
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit
