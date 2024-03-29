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
name|Enumeration
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|HashMap
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Vector
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
name|ConnectionFactory
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
name|MapMessage
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
name|MessageEOFException
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
name|command
operator|.
name|ActiveMQDestination
import|;
end_import

begin_comment
comment|/**  * Test cases used to test the JMS message consumer.  *  *  */
end_comment

begin_class
specifier|public
class|class
name|JMSMessageTest
extends|extends
name|JmsTestSupport
block|{
specifier|public
name|ActiveMQDestination
name|destination
decl_stmt|;
specifier|public
name|int
name|deliveryMode
init|=
name|DeliveryMode
operator|.
name|NON_PERSISTENT
decl_stmt|;
specifier|public
name|int
name|prefetch
decl_stmt|;
specifier|public
name|int
name|ackMode
decl_stmt|;
specifier|public
name|byte
name|destinationType
init|=
name|ActiveMQDestination
operator|.
name|QUEUE_TYPE
decl_stmt|;
specifier|public
name|boolean
name|durableConsumer
decl_stmt|;
specifier|public
name|String
name|connectURL
init|=
literal|"vm://localhost?marshal=false"
decl_stmt|;
comment|/**      * Run all these tests in both marshaling and non-marshaling mode.      */
specifier|public
name|void
name|initCombos
parameter_list|()
block|{
name|addCombinationValues
argument_list|(
literal|"connectURL"
argument_list|,
operator|new
name|Object
index|[]
block|{
literal|"vm://localhost?marshal=false"
block|,
literal|"vm://localhost?marshal=true"
block|}
argument_list|)
expr_stmt|;
name|addCombinationValues
argument_list|(
literal|"deliveryMode"
argument_list|,
operator|new
name|Object
index|[]
block|{
name|Integer
operator|.
name|valueOf
argument_list|(
name|DeliveryMode
operator|.
name|NON_PERSISTENT
argument_list|)
block|,
name|Integer
operator|.
name|valueOf
argument_list|(
name|DeliveryMode
operator|.
name|PERSISTENT
argument_list|)
block|}
argument_list|)
expr_stmt|;
name|addCombinationValues
argument_list|(
literal|"destinationType"
argument_list|,
operator|new
name|Object
index|[]
block|{
name|Byte
operator|.
name|valueOf
argument_list|(
name|ActiveMQDestination
operator|.
name|QUEUE_TYPE
argument_list|)
block|}
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|testTextMessage
parameter_list|()
throws|throws
name|Exception
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
name|destination
operator|=
name|createDestination
argument_list|(
name|session
argument_list|,
name|destinationType
argument_list|)
expr_stmt|;
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
comment|// Send the message.
block|{
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
literal|"Hi"
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
comment|// Check the Message
block|{
name|TextMessage
name|message
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
name|message
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"Hi"
argument_list|,
name|message
operator|.
name|getText
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|assertNull
argument_list|(
name|consumer
operator|.
name|receiveNoWait
argument_list|()
argument_list|)
expr_stmt|;
block|}
specifier|public
specifier|static
name|Test
name|suite
parameter_list|()
block|{
return|return
name|suite
argument_list|(
name|JMSMessageTest
operator|.
name|class
argument_list|)
return|;
block|}
specifier|public
specifier|static
name|void
name|main
parameter_list|(
name|String
index|[]
name|args
parameter_list|)
block|{
name|junit
operator|.
name|textui
operator|.
name|TestRunner
operator|.
name|run
argument_list|(
name|suite
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
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
name|connectURL
argument_list|)
decl_stmt|;
return|return
name|factory
return|;
block|}
specifier|public
name|void
name|testBytesMessageLength
parameter_list|()
throws|throws
name|Exception
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
name|destination
operator|=
name|createDestination
argument_list|(
name|session
argument_list|,
name|destinationType
argument_list|)
expr_stmt|;
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
comment|// Send the message
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
name|writeInt
argument_list|(
literal|1
argument_list|)
expr_stmt|;
name|message
operator|.
name|writeInt
argument_list|(
literal|2
argument_list|)
expr_stmt|;
name|message
operator|.
name|writeInt
argument_list|(
literal|3
argument_list|)
expr_stmt|;
name|message
operator|.
name|writeInt
argument_list|(
literal|4
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
comment|// Check the message.
block|{
name|BytesMessage
name|message
init|=
operator|(
name|BytesMessage
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
name|message
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|16
argument_list|,
name|message
operator|.
name|getBodyLength
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|assertNull
argument_list|(
name|consumer
operator|.
name|receiveNoWait
argument_list|()
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|testObjectMessage
parameter_list|()
throws|throws
name|Exception
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
name|destination
operator|=
name|createDestination
argument_list|(
name|session
argument_list|,
name|destinationType
argument_list|)
expr_stmt|;
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
comment|// send the message.
block|{
name|ObjectMessage
name|message
init|=
name|session
operator|.
name|createObjectMessage
argument_list|()
decl_stmt|;
name|message
operator|.
name|setObject
argument_list|(
literal|"Hi"
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
comment|// Check the message
block|{
name|ObjectMessage
name|message
init|=
operator|(
name|ObjectMessage
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
name|message
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"Hi"
argument_list|,
name|message
operator|.
name|getObject
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|assertNull
argument_list|(
name|consumer
operator|.
name|receiveNoWait
argument_list|()
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|testBytesMessage
parameter_list|()
throws|throws
name|Exception
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
name|destination
operator|=
name|createDestination
argument_list|(
name|session
argument_list|,
name|destinationType
argument_list|)
expr_stmt|;
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
comment|// Send the message
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
name|writeBoolean
argument_list|(
literal|true
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
comment|// Check the message
block|{
name|BytesMessage
name|message
init|=
operator|(
name|BytesMessage
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
name|message
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|message
operator|.
name|readBoolean
argument_list|()
argument_list|)
expr_stmt|;
try|try
block|{
name|message
operator|.
name|readByte
argument_list|()
expr_stmt|;
name|fail
argument_list|(
literal|"Expected exception not thrown."
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|MessageEOFException
name|e
parameter_list|)
block|{             }
block|}
name|assertNull
argument_list|(
name|consumer
operator|.
name|receiveNoWait
argument_list|()
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|testStreamMessage
parameter_list|()
throws|throws
name|Exception
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
name|destination
operator|=
name|createDestination
argument_list|(
name|session
argument_list|,
name|destinationType
argument_list|)
expr_stmt|;
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
comment|// Send the message.
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
name|writeString
argument_list|(
literal|"This is a test to see how it works."
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
comment|// Check the message.
block|{
name|StreamMessage
name|message
init|=
operator|(
name|StreamMessage
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
name|message
argument_list|)
expr_stmt|;
comment|// Invalid conversion should throw exception and not move the stream
comment|// position.
try|try
block|{
name|message
operator|.
name|readByte
argument_list|()
expr_stmt|;
name|fail
argument_list|(
literal|"Should have received NumberFormatException"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|NumberFormatException
name|e
parameter_list|)
block|{             }
name|assertEquals
argument_list|(
literal|"This is a test to see how it works."
argument_list|,
name|message
operator|.
name|readString
argument_list|()
argument_list|)
expr_stmt|;
comment|// Invalid conversion should throw exception and not move the stream
comment|// position.
try|try
block|{
name|message
operator|.
name|readByte
argument_list|()
expr_stmt|;
name|fail
argument_list|(
literal|"Should have received MessageEOFException"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|MessageEOFException
name|e
parameter_list|)
block|{             }
block|}
name|assertNull
argument_list|(
name|consumer
operator|.
name|receiveNoWait
argument_list|()
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|testMapMessage
parameter_list|()
throws|throws
name|Exception
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
name|destination
operator|=
name|createDestination
argument_list|(
name|session
argument_list|,
name|destinationType
argument_list|)
expr_stmt|;
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
comment|// send the message.
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
name|setBoolean
argument_list|(
literal|"boolKey"
argument_list|,
literal|true
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
comment|// get the message.
block|{
name|MapMessage
name|message
init|=
operator|(
name|MapMessage
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
name|message
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|message
operator|.
name|getBoolean
argument_list|(
literal|"boolKey"
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|assertNull
argument_list|(
name|consumer
operator|.
name|receiveNoWait
argument_list|()
argument_list|)
expr_stmt|;
block|}
specifier|static
class|class
name|ForeignMessage
implements|implements
name|TextMessage
block|{
specifier|public
name|int
name|deliveryMode
decl_stmt|;
specifier|private
name|String
name|messageId
decl_stmt|;
specifier|private
name|long
name|timestamp
decl_stmt|;
specifier|private
name|String
name|correlationId
decl_stmt|;
specifier|private
name|Destination
name|replyTo
decl_stmt|;
specifier|private
name|Destination
name|destination
decl_stmt|;
specifier|private
name|boolean
name|redelivered
decl_stmt|;
specifier|private
name|String
name|type
decl_stmt|;
specifier|private
name|long
name|expiration
decl_stmt|;
specifier|private
name|int
name|priority
decl_stmt|;
specifier|private
name|String
name|text
decl_stmt|;
specifier|private
specifier|final
name|HashMap
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|props
init|=
operator|new
name|HashMap
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
argument_list|()
decl_stmt|;
annotation|@
name|Override
specifier|public
name|String
name|getJMSMessageID
parameter_list|()
throws|throws
name|JMSException
block|{
return|return
name|messageId
return|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|setJMSMessageID
parameter_list|(
name|String
name|arg0
parameter_list|)
throws|throws
name|JMSException
block|{
name|messageId
operator|=
name|arg0
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|long
name|getJMSTimestamp
parameter_list|()
throws|throws
name|JMSException
block|{
return|return
name|timestamp
return|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|setJMSTimestamp
parameter_list|(
name|long
name|arg0
parameter_list|)
throws|throws
name|JMSException
block|{
name|timestamp
operator|=
name|arg0
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|byte
index|[]
name|getJMSCorrelationIDAsBytes
parameter_list|()
throws|throws
name|JMSException
block|{
return|return
literal|null
return|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|setJMSCorrelationIDAsBytes
parameter_list|(
name|byte
index|[]
name|arg0
parameter_list|)
throws|throws
name|JMSException
block|{         }
annotation|@
name|Override
specifier|public
name|void
name|setJMSCorrelationID
parameter_list|(
name|String
name|arg0
parameter_list|)
throws|throws
name|JMSException
block|{
name|correlationId
operator|=
name|arg0
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|String
name|getJMSCorrelationID
parameter_list|()
throws|throws
name|JMSException
block|{
return|return
name|correlationId
return|;
block|}
annotation|@
name|Override
specifier|public
name|Destination
name|getJMSReplyTo
parameter_list|()
throws|throws
name|JMSException
block|{
return|return
name|replyTo
return|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|setJMSReplyTo
parameter_list|(
name|Destination
name|arg0
parameter_list|)
throws|throws
name|JMSException
block|{
name|replyTo
operator|=
name|arg0
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|Destination
name|getJMSDestination
parameter_list|()
throws|throws
name|JMSException
block|{
return|return
name|destination
return|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|setJMSDestination
parameter_list|(
name|Destination
name|arg0
parameter_list|)
throws|throws
name|JMSException
block|{
name|destination
operator|=
name|arg0
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|int
name|getJMSDeliveryMode
parameter_list|()
throws|throws
name|JMSException
block|{
return|return
name|deliveryMode
return|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|setJMSDeliveryMode
parameter_list|(
name|int
name|arg0
parameter_list|)
throws|throws
name|JMSException
block|{
name|deliveryMode
operator|=
name|arg0
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|getJMSRedelivered
parameter_list|()
throws|throws
name|JMSException
block|{
return|return
name|redelivered
return|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|setJMSRedelivered
parameter_list|(
name|boolean
name|arg0
parameter_list|)
throws|throws
name|JMSException
block|{
name|redelivered
operator|=
name|arg0
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|String
name|getJMSType
parameter_list|()
throws|throws
name|JMSException
block|{
return|return
name|type
return|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|setJMSType
parameter_list|(
name|String
name|arg0
parameter_list|)
throws|throws
name|JMSException
block|{
name|type
operator|=
name|arg0
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|long
name|getJMSExpiration
parameter_list|()
throws|throws
name|JMSException
block|{
return|return
name|expiration
return|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|setJMSExpiration
parameter_list|(
name|long
name|arg0
parameter_list|)
throws|throws
name|JMSException
block|{
name|expiration
operator|=
name|arg0
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|int
name|getJMSPriority
parameter_list|()
throws|throws
name|JMSException
block|{
return|return
name|priority
return|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|setJMSPriority
parameter_list|(
name|int
name|arg0
parameter_list|)
throws|throws
name|JMSException
block|{
name|priority
operator|=
name|arg0
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|clearProperties
parameter_list|()
throws|throws
name|JMSException
block|{         }
annotation|@
name|Override
specifier|public
name|boolean
name|propertyExists
parameter_list|(
name|String
name|arg0
parameter_list|)
throws|throws
name|JMSException
block|{
return|return
literal|false
return|;
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|getBooleanProperty
parameter_list|(
name|String
name|arg0
parameter_list|)
throws|throws
name|JMSException
block|{
return|return
literal|false
return|;
block|}
annotation|@
name|Override
specifier|public
name|byte
name|getByteProperty
parameter_list|(
name|String
name|arg0
parameter_list|)
throws|throws
name|JMSException
block|{
return|return
literal|0
return|;
block|}
annotation|@
name|Override
specifier|public
name|short
name|getShortProperty
parameter_list|(
name|String
name|arg0
parameter_list|)
throws|throws
name|JMSException
block|{
return|return
literal|0
return|;
block|}
annotation|@
name|Override
specifier|public
name|int
name|getIntProperty
parameter_list|(
name|String
name|arg0
parameter_list|)
throws|throws
name|JMSException
block|{
return|return
literal|0
return|;
block|}
annotation|@
name|Override
specifier|public
name|long
name|getLongProperty
parameter_list|(
name|String
name|arg0
parameter_list|)
throws|throws
name|JMSException
block|{
return|return
literal|0
return|;
block|}
annotation|@
name|Override
specifier|public
name|float
name|getFloatProperty
parameter_list|(
name|String
name|arg0
parameter_list|)
throws|throws
name|JMSException
block|{
return|return
literal|0
return|;
block|}
annotation|@
name|Override
specifier|public
name|double
name|getDoubleProperty
parameter_list|(
name|String
name|arg0
parameter_list|)
throws|throws
name|JMSException
block|{
return|return
literal|0
return|;
block|}
annotation|@
name|Override
specifier|public
name|String
name|getStringProperty
parameter_list|(
name|String
name|arg0
parameter_list|)
throws|throws
name|JMSException
block|{
return|return
operator|(
name|String
operator|)
name|props
operator|.
name|get
argument_list|(
name|arg0
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|Object
name|getObjectProperty
parameter_list|(
name|String
name|arg0
parameter_list|)
throws|throws
name|JMSException
block|{
return|return
name|props
operator|.
name|get
argument_list|(
name|arg0
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|Enumeration
argument_list|<
name|?
argument_list|>
name|getPropertyNames
parameter_list|()
throws|throws
name|JMSException
block|{
return|return
operator|new
name|Vector
argument_list|<
name|String
argument_list|>
argument_list|(
name|props
operator|.
name|keySet
argument_list|()
argument_list|)
operator|.
name|elements
argument_list|()
return|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|setBooleanProperty
parameter_list|(
name|String
name|arg0
parameter_list|,
name|boolean
name|arg1
parameter_list|)
throws|throws
name|JMSException
block|{         }
annotation|@
name|Override
specifier|public
name|void
name|setByteProperty
parameter_list|(
name|String
name|arg0
parameter_list|,
name|byte
name|arg1
parameter_list|)
throws|throws
name|JMSException
block|{         }
annotation|@
name|Override
specifier|public
name|void
name|setShortProperty
parameter_list|(
name|String
name|arg0
parameter_list|,
name|short
name|arg1
parameter_list|)
throws|throws
name|JMSException
block|{         }
annotation|@
name|Override
specifier|public
name|void
name|setIntProperty
parameter_list|(
name|String
name|arg0
parameter_list|,
name|int
name|arg1
parameter_list|)
throws|throws
name|JMSException
block|{         }
annotation|@
name|Override
specifier|public
name|void
name|setLongProperty
parameter_list|(
name|String
name|arg0
parameter_list|,
name|long
name|arg1
parameter_list|)
throws|throws
name|JMSException
block|{         }
annotation|@
name|Override
specifier|public
name|void
name|setFloatProperty
parameter_list|(
name|String
name|arg0
parameter_list|,
name|float
name|arg1
parameter_list|)
throws|throws
name|JMSException
block|{         }
annotation|@
name|Override
specifier|public
name|void
name|setDoubleProperty
parameter_list|(
name|String
name|arg0
parameter_list|,
name|double
name|arg1
parameter_list|)
throws|throws
name|JMSException
block|{         }
annotation|@
name|Override
specifier|public
name|void
name|setStringProperty
parameter_list|(
name|String
name|arg0
parameter_list|,
name|String
name|arg1
parameter_list|)
throws|throws
name|JMSException
block|{
name|props
operator|.
name|put
argument_list|(
name|arg0
argument_list|,
name|arg1
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|setObjectProperty
parameter_list|(
name|String
name|arg0
parameter_list|,
name|Object
name|arg1
parameter_list|)
throws|throws
name|JMSException
block|{
name|props
operator|.
name|put
argument_list|(
name|arg0
argument_list|,
name|arg1
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|acknowledge
parameter_list|()
throws|throws
name|JMSException
block|{         }
annotation|@
name|Override
specifier|public
name|void
name|clearBody
parameter_list|()
throws|throws
name|JMSException
block|{         }
annotation|@
name|Override
specifier|public
name|void
name|setText
parameter_list|(
name|String
name|arg0
parameter_list|)
throws|throws
name|JMSException
block|{
name|text
operator|=
name|arg0
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|String
name|getText
parameter_list|()
throws|throws
name|JMSException
block|{
return|return
name|text
return|;
block|}
block|}
specifier|public
name|void
name|testForeignMessage
parameter_list|()
throws|throws
name|Exception
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
name|destination
operator|=
name|createDestination
argument_list|(
name|session
argument_list|,
name|destinationType
argument_list|)
expr_stmt|;
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
comment|// Send the message.
block|{
name|ForeignMessage
name|message
init|=
operator|new
name|ForeignMessage
argument_list|()
decl_stmt|;
name|message
operator|.
name|text
operator|=
literal|"Hello"
expr_stmt|;
name|message
operator|.
name|setStringProperty
argument_list|(
literal|"test"
argument_list|,
literal|"value"
argument_list|)
expr_stmt|;
name|long
name|timeToLive
init|=
literal|10000L
decl_stmt|;
name|long
name|start
init|=
name|System
operator|.
name|currentTimeMillis
argument_list|()
decl_stmt|;
name|producer
operator|.
name|send
argument_list|(
name|message
argument_list|,
name|deliveryMode
argument_list|,
literal|7
argument_list|,
name|timeToLive
argument_list|)
expr_stmt|;
name|long
name|end
init|=
name|System
operator|.
name|currentTimeMillis
argument_list|()
decl_stmt|;
comment|//validate jms spec 1.1 section 3.4.11 table 3.1
comment|// JMSDestination, JMSDeliveryMode,  JMSExpiration, JMSPriority, JMSMessageID, and JMSTimestamp
comment|//must be set by sending a message.
name|assertNotNull
argument_list|(
name|message
operator|.
name|getJMSDestination
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|deliveryMode
argument_list|,
name|message
operator|.
name|getJMSDeliveryMode
argument_list|()
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|start
operator|+
name|timeToLive
operator|<=
name|message
operator|.
name|getJMSExpiration
argument_list|()
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|end
operator|+
name|timeToLive
operator|>=
name|message
operator|.
name|getJMSExpiration
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|7
argument_list|,
name|message
operator|.
name|getJMSPriority
argument_list|()
argument_list|)
expr_stmt|;
name|assertNotNull
argument_list|(
name|message
operator|.
name|getJMSMessageID
argument_list|()
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|start
operator|<=
name|message
operator|.
name|getJMSTimestamp
argument_list|()
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|end
operator|>=
name|message
operator|.
name|getJMSTimestamp
argument_list|()
argument_list|)
expr_stmt|;
block|}
comment|// Validate message is OK.
block|{
name|TextMessage
name|message
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
name|message
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"Hello"
argument_list|,
name|message
operator|.
name|getText
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"value"
argument_list|,
name|message
operator|.
name|getStringProperty
argument_list|(
literal|"test"
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|assertNull
argument_list|(
name|consumer
operator|.
name|receiveNoWait
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

