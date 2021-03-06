begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  * http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
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
name|assertArrayEquals
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
name|Arrays
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Collection
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
name|Queue
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
name|transport
operator|.
name|amqp
operator|.
name|client
operator|.
name|AmqpClientTestSupport
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|Rule
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
name|junit
operator|.
name|rules
operator|.
name|TestName
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|runner
operator|.
name|RunWith
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|runners
operator|.
name|Parameterized
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|runners
operator|.
name|Parameterized
operator|.
name|Parameters
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
annotation|@
name|RunWith
argument_list|(
name|Parameterized
operator|.
name|class
argument_list|)
specifier|public
class|class
name|JMSLargeMessageSendRecvTest
extends|extends
name|AmqpClientTestSupport
block|{
specifier|protected
specifier|static
specifier|final
name|Logger
name|LOG
init|=
name|LoggerFactory
operator|.
name|getLogger
argument_list|(
name|JMSLargeMessageSendRecvTest
operator|.
name|class
argument_list|)
decl_stmt|;
annotation|@
name|Parameters
argument_list|(
name|name
operator|=
literal|"{0}"
argument_list|)
specifier|public
specifier|static
name|Collection
argument_list|<
name|Object
index|[]
argument_list|>
name|data
parameter_list|()
block|{
return|return
name|Arrays
operator|.
name|asList
argument_list|(
operator|new
name|Object
index|[]
index|[]
block|{
block|{
literal|"amqp"
block|,
literal|false
block|}
block|,
block|{
literal|"amqp+ws"
block|,
literal|false
block|}
block|,
block|{
literal|"amqp+ssl"
block|,
literal|true
block|}
block|,
block|{
literal|"amqp+wss"
block|,
literal|true
block|}
block|}
argument_list|)
return|;
block|}
annotation|@
name|Rule
specifier|public
name|TestName
name|testName
init|=
operator|new
name|TestName
argument_list|()
decl_stmt|;
specifier|public
name|JMSLargeMessageSendRecvTest
parameter_list|(
name|String
name|connectorScheme
parameter_list|,
name|boolean
name|secure
parameter_list|)
block|{
name|super
argument_list|(
name|connectorScheme
argument_list|,
name|secure
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
argument_list|(
name|timeout
operator|=
literal|60
operator|*
literal|1000
argument_list|)
specifier|public
name|void
name|testSendSmallerTextMessage
parameter_list|()
throws|throws
name|JMSException
block|{
name|doTestSendTextMessageOfGivenSize
argument_list|(
literal|1024
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
argument_list|(
name|timeout
operator|=
literal|60
operator|*
literal|1000
argument_list|)
specifier|public
name|void
name|testSendSeriesOfSmallerTextMessages
parameter_list|()
throws|throws
name|JMSException
block|{
for|for
control|(
name|int
name|i
init|=
literal|512
init|;
name|i
operator|<=
operator|(
literal|8
operator|*
literal|1024
operator|)
condition|;
name|i
operator|+=
literal|512
control|)
block|{
name|doTestSendTextMessageOfGivenSize
argument_list|(
name|i
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Test
argument_list|(
name|timeout
operator|=
literal|60
operator|*
literal|1000
argument_list|)
specifier|public
name|void
name|testSendFixedSizedTextMessages
parameter_list|()
throws|throws
name|JMSException
block|{
name|doTestSendTextMessageOfGivenSize
argument_list|(
literal|65536
argument_list|)
expr_stmt|;
name|doTestSendTextMessageOfGivenSize
argument_list|(
literal|65536
operator|*
literal|2
argument_list|)
expr_stmt|;
name|doTestSendTextMessageOfGivenSize
argument_list|(
literal|65536
operator|*
literal|4
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
argument_list|(
name|timeout
operator|=
literal|60
operator|*
literal|1000
argument_list|)
specifier|public
name|void
name|testSendHugeTextMessage
parameter_list|()
throws|throws
name|JMSException
block|{
name|doTestSendTextMessageOfGivenSize
argument_list|(
literal|1024
operator|*
literal|1024
operator|*
literal|5
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|doTestSendTextMessageOfGivenSize
parameter_list|(
name|int
name|expectedSize
parameter_list|)
throws|throws
name|JMSException
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"doTestSendLargeMessage called with expectedSize "
operator|+
name|expectedSize
argument_list|)
expr_stmt|;
name|String
name|payload
init|=
name|createLargeString
argument_list|(
name|expectedSize
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
name|expectedSize
argument_list|,
name|payload
operator|.
name|getBytes
argument_list|()
operator|.
name|length
argument_list|)
expr_stmt|;
name|Connection
name|connection
init|=
name|JMSClientContext
operator|.
name|INSTANCE
operator|.
name|createConnection
argument_list|(
name|getBrokerAmqpConnectionURI
argument_list|()
argument_list|)
decl_stmt|;
name|long
name|startTime
init|=
name|System
operator|.
name|currentTimeMillis
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
name|AUTO_ACKNOWLEDGE
argument_list|)
decl_stmt|;
name|Queue
name|queue
init|=
name|session
operator|.
name|createQueue
argument_list|(
name|testName
operator|.
name|getMethodName
argument_list|()
argument_list|)
decl_stmt|;
name|MessageProducer
name|producer
init|=
name|session
operator|.
name|createProducer
argument_list|(
name|queue
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
name|payload
argument_list|)
expr_stmt|;
name|producer
operator|.
name|send
argument_list|(
name|message
argument_list|)
expr_stmt|;
name|long
name|endTime
init|=
name|System
operator|.
name|currentTimeMillis
argument_list|()
decl_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"Returned from send after {} ms"
argument_list|,
name|endTime
operator|-
name|startTime
argument_list|)
expr_stmt|;
name|startTime
operator|=
name|System
operator|.
name|currentTimeMillis
argument_list|()
expr_stmt|;
name|MessageConsumer
name|consumer
init|=
name|session
operator|.
name|createConsumer
argument_list|(
name|queue
argument_list|)
decl_stmt|;
name|connection
operator|.
name|start
argument_list|()
expr_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"Calling receive"
argument_list|)
expr_stmt|;
name|Message
name|receivedMessage
init|=
name|consumer
operator|.
name|receive
argument_list|()
decl_stmt|;
name|assertNotNull
argument_list|(
name|receivedMessage
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|receivedMessage
operator|instanceof
name|TextMessage
argument_list|)
expr_stmt|;
name|TextMessage
name|receivedTextMessage
init|=
operator|(
name|TextMessage
operator|)
name|receivedMessage
decl_stmt|;
name|assertNotNull
argument_list|(
name|receivedMessage
argument_list|)
expr_stmt|;
name|endTime
operator|=
name|System
operator|.
name|currentTimeMillis
argument_list|()
expr_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"Returned from receive after {} ms"
argument_list|,
name|endTime
operator|-
name|startTime
argument_list|)
expr_stmt|;
name|String
name|receivedText
init|=
name|receivedTextMessage
operator|.
name|getText
argument_list|()
decl_stmt|;
name|assertEquals
argument_list|(
name|expectedSize
argument_list|,
name|receivedText
operator|.
name|getBytes
argument_list|()
operator|.
name|length
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|payload
argument_list|,
name|receivedText
argument_list|)
expr_stmt|;
name|connection
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
argument_list|(
name|timeout
operator|=
literal|60
operator|*
literal|1000
argument_list|)
specifier|public
name|void
name|testSendSmallerBytesMessage
parameter_list|()
throws|throws
name|JMSException
block|{
name|doTestSendBytesMessageOfGivenSize
argument_list|(
literal|1024
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
argument_list|(
name|timeout
operator|=
literal|60
operator|*
literal|1000
argument_list|)
specifier|public
name|void
name|testSendSeriesOfSmallerBytesMessages
parameter_list|()
throws|throws
name|JMSException
block|{
for|for
control|(
name|int
name|i
init|=
literal|512
init|;
name|i
operator|<=
operator|(
literal|8
operator|*
literal|1024
operator|)
condition|;
name|i
operator|+=
literal|512
control|)
block|{
name|doTestSendBytesMessageOfGivenSize
argument_list|(
name|i
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Test
argument_list|(
name|timeout
operator|=
literal|60
operator|*
literal|1000
argument_list|)
specifier|public
name|void
name|testSendFixedSizedBytesMessages
parameter_list|()
throws|throws
name|JMSException
block|{
name|doTestSendBytesMessageOfGivenSize
argument_list|(
literal|65536
argument_list|)
expr_stmt|;
name|doTestSendBytesMessageOfGivenSize
argument_list|(
literal|65536
operator|*
literal|2
argument_list|)
expr_stmt|;
name|doTestSendBytesMessageOfGivenSize
argument_list|(
literal|65536
operator|*
literal|4
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
argument_list|(
name|timeout
operator|=
literal|60
operator|*
literal|1000
argument_list|)
specifier|public
name|void
name|testSendHugeBytesMessage
parameter_list|()
throws|throws
name|JMSException
block|{
name|doTestSendBytesMessageOfGivenSize
argument_list|(
literal|1024
operator|*
literal|1024
operator|*
literal|5
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|doTestSendBytesMessageOfGivenSize
parameter_list|(
name|int
name|expectedSize
parameter_list|)
throws|throws
name|JMSException
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"doTestSendLargeMessage called with expectedSize "
operator|+
name|expectedSize
argument_list|)
expr_stmt|;
name|byte
index|[]
name|payload
init|=
name|createLargeByteArray
argument_list|(
name|expectedSize
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
name|expectedSize
argument_list|,
name|payload
operator|.
name|length
argument_list|)
expr_stmt|;
name|Connection
name|connection
init|=
name|JMSClientContext
operator|.
name|INSTANCE
operator|.
name|createConnection
argument_list|(
name|getBrokerAmqpConnectionURI
argument_list|()
argument_list|)
decl_stmt|;
name|long
name|startTime
init|=
name|System
operator|.
name|currentTimeMillis
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
name|AUTO_ACKNOWLEDGE
argument_list|)
decl_stmt|;
name|Queue
name|queue
init|=
name|session
operator|.
name|createQueue
argument_list|(
name|testName
operator|.
name|getMethodName
argument_list|()
argument_list|)
decl_stmt|;
name|MessageProducer
name|producer
init|=
name|session
operator|.
name|createProducer
argument_list|(
name|queue
argument_list|)
decl_stmt|;
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
name|payload
argument_list|)
expr_stmt|;
name|producer
operator|.
name|send
argument_list|(
name|message
argument_list|)
expr_stmt|;
name|long
name|endTime
init|=
name|System
operator|.
name|currentTimeMillis
argument_list|()
decl_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"Returned from send after {} ms"
argument_list|,
name|endTime
operator|-
name|startTime
argument_list|)
expr_stmt|;
name|startTime
operator|=
name|System
operator|.
name|currentTimeMillis
argument_list|()
expr_stmt|;
name|MessageConsumer
name|consumer
init|=
name|session
operator|.
name|createConsumer
argument_list|(
name|queue
argument_list|)
decl_stmt|;
name|connection
operator|.
name|start
argument_list|()
expr_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"Calling receive"
argument_list|)
expr_stmt|;
name|Message
name|receivedMessage
init|=
name|consumer
operator|.
name|receive
argument_list|()
decl_stmt|;
name|assertNotNull
argument_list|(
name|receivedMessage
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|receivedMessage
operator|instanceof
name|BytesMessage
argument_list|)
expr_stmt|;
name|BytesMessage
name|receivedBytesMessage
init|=
operator|(
name|BytesMessage
operator|)
name|receivedMessage
decl_stmt|;
name|assertNotNull
argument_list|(
name|receivedMessage
argument_list|)
expr_stmt|;
name|endTime
operator|=
name|System
operator|.
name|currentTimeMillis
argument_list|()
expr_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"Returned from receive after {} ms"
argument_list|,
name|endTime
operator|-
name|startTime
argument_list|)
expr_stmt|;
name|byte
index|[]
name|receivedBytes
init|=
operator|new
name|byte
index|[
operator|(
name|int
operator|)
name|receivedBytesMessage
operator|.
name|getBodyLength
argument_list|()
index|]
decl_stmt|;
name|receivedBytesMessage
operator|.
name|readBytes
argument_list|(
name|receivedBytes
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|expectedSize
argument_list|,
name|receivedBytes
operator|.
name|length
argument_list|)
expr_stmt|;
name|assertArrayEquals
argument_list|(
name|payload
argument_list|,
name|receivedBytes
argument_list|)
expr_stmt|;
name|connection
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
specifier|private
name|String
name|createLargeString
parameter_list|(
name|int
name|sizeInBytes
parameter_list|)
block|{
name|byte
index|[]
name|base
init|=
block|{
literal|1
block|,
literal|2
block|,
literal|3
block|,
literal|4
block|,
literal|5
block|,
literal|6
block|,
literal|7
block|,
literal|8
block|,
literal|9
block|,
literal|0
block|}
decl_stmt|;
name|StringBuilder
name|builder
init|=
operator|new
name|StringBuilder
argument_list|()
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
name|sizeInBytes
condition|;
name|i
operator|++
control|)
block|{
name|builder
operator|.
name|append
argument_list|(
name|base
index|[
name|i
operator|%
name|base
operator|.
name|length
index|]
argument_list|)
expr_stmt|;
block|}
name|LOG
operator|.
name|debug
argument_list|(
literal|"Created string with size : "
operator|+
name|builder
operator|.
name|toString
argument_list|()
operator|.
name|getBytes
argument_list|()
operator|.
name|length
operator|+
literal|" bytes"
argument_list|)
expr_stmt|;
return|return
name|builder
operator|.
name|toString
argument_list|()
return|;
block|}
specifier|private
name|byte
index|[]
name|createLargeByteArray
parameter_list|(
name|int
name|sizeInBytes
parameter_list|)
block|{
name|byte
index|[]
name|base
init|=
block|{
literal|1
block|,
literal|2
block|,
literal|3
block|,
literal|4
block|,
literal|5
block|,
literal|6
block|,
literal|7
block|,
literal|8
block|,
literal|9
block|,
literal|0
block|}
decl_stmt|;
name|byte
index|[]
name|payload
init|=
operator|new
name|byte
index|[
name|sizeInBytes
index|]
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
name|sizeInBytes
condition|;
name|i
operator|++
control|)
block|{
name|payload
index|[
name|i
index|]
operator|=
operator|(
name|base
index|[
name|i
operator|%
name|base
operator|.
name|length
index|]
operator|)
expr_stmt|;
block|}
name|LOG
operator|.
name|debug
argument_list|(
literal|"Created byte array with size : "
operator|+
name|payload
operator|.
name|length
operator|+
literal|" bytes"
argument_list|)
expr_stmt|;
return|return
name|payload
return|;
block|}
block|}
end_class

end_unit

