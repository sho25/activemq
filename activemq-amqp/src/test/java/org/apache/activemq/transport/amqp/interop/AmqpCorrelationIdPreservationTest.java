begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *      http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
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
operator|.
name|interop
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
name|java
operator|.
name|util
operator|.
name|UUID
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
name|QueueViewMBean
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
name|JMSInteroperabilityTest
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
name|AmqpClient
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
name|AmqpConnection
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
name|AmqpMessage
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
name|AmqpReceiver
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
name|AmqpSender
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
name|AmqpSession
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
name|proton
operator|.
name|amqp
operator|.
name|Binary
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
name|proton
operator|.
name|amqp
operator|.
name|UnsignedLong
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

begin_comment
comment|/**  * Tests that the AMQP CorrelationId value and type are preserved.  */
end_comment

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
name|AmqpCorrelationIdPreservationTest
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
name|JMSInteroperabilityTest
operator|.
name|class
argument_list|)
decl_stmt|;
specifier|private
specifier|final
name|String
name|transformer
decl_stmt|;
annotation|@
name|Parameters
argument_list|(
name|name
operator|=
literal|"Transformer->{0}"
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
literal|"jms"
block|}
block|,
block|{
literal|"native"
block|}
block|,
block|{
literal|"raw"
block|}
block|,             }
argument_list|)
return|;
block|}
specifier|public
name|AmqpCorrelationIdPreservationTest
parameter_list|(
name|String
name|transformer
parameter_list|)
block|{
name|this
operator|.
name|transformer
operator|=
name|transformer
expr_stmt|;
block|}
annotation|@
name|Override
specifier|protected
name|String
name|getAmqpTransformer
parameter_list|()
block|{
return|return
name|transformer
return|;
block|}
annotation|@
name|Override
specifier|protected
name|boolean
name|isPersistent
parameter_list|()
block|{
return|return
literal|true
return|;
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
name|testStringCorrelationIdIsPreserved
parameter_list|()
throws|throws
name|Exception
block|{
name|doTestCorrelationIdPreservation
argument_list|(
literal|"msg-id-string:1"
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
name|testStringCorrelationIdIsPreservedAfterRestart
parameter_list|()
throws|throws
name|Exception
block|{
name|doTestCorrelationIdPreservationOnBrokerRestart
argument_list|(
literal|"msg-id-string:1"
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
name|testUUIDCorrelationIdIsPreserved
parameter_list|()
throws|throws
name|Exception
block|{
name|doTestCorrelationIdPreservation
argument_list|(
name|UUID
operator|.
name|randomUUID
argument_list|()
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
name|testUUIDCorrelationIdIsPreservedAfterRestart
parameter_list|()
throws|throws
name|Exception
block|{
name|doTestCorrelationIdPreservationOnBrokerRestart
argument_list|(
name|UUID
operator|.
name|randomUUID
argument_list|()
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
name|testUnsignedLongCorrelationIdIsPreserved
parameter_list|()
throws|throws
name|Exception
block|{
name|doTestCorrelationIdPreservation
argument_list|(
operator|new
name|UnsignedLong
argument_list|(
literal|255l
argument_list|)
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
name|testUnsignedLongCorrelationIdIsPreservedAfterRestart
parameter_list|()
throws|throws
name|Exception
block|{
name|doTestCorrelationIdPreservationOnBrokerRestart
argument_list|(
operator|new
name|UnsignedLong
argument_list|(
literal|255l
argument_list|)
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
name|testBinaryLongCorrelationIdIsPreserved
parameter_list|()
throws|throws
name|Exception
block|{
name|byte
index|[]
name|payload
init|=
operator|new
name|byte
index|[
literal|32
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
literal|32
condition|;
operator|++
name|i
control|)
block|{
name|payload
index|[
name|i
index|]
operator|=
call|(
name|byte
call|)
argument_list|(
literal|'a'
operator|+
name|i
argument_list|)
expr_stmt|;
block|}
name|doTestCorrelationIdPreservation
argument_list|(
operator|new
name|Binary
argument_list|(
name|payload
argument_list|)
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
name|testBinaryLongCorrelationIdIsPreservedAfterRestart
parameter_list|()
throws|throws
name|Exception
block|{
name|byte
index|[]
name|payload
init|=
operator|new
name|byte
index|[
literal|32
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
literal|32
condition|;
operator|++
name|i
control|)
block|{
name|payload
index|[
name|i
index|]
operator|=
call|(
name|byte
call|)
argument_list|(
literal|'a'
operator|+
name|i
argument_list|)
expr_stmt|;
block|}
name|doTestCorrelationIdPreservationOnBrokerRestart
argument_list|(
operator|new
name|Binary
argument_list|(
name|payload
argument_list|)
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
name|testStringCorrelationIdPrefixIsPreserved
parameter_list|()
throws|throws
name|Exception
block|{
name|doTestCorrelationIdPreservation
argument_list|(
literal|"ID:msg-id-string:1"
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|doTestCorrelationIdPreservation
parameter_list|(
name|Object
name|messageId
parameter_list|)
throws|throws
name|Exception
block|{
name|AmqpClient
name|client
init|=
name|createAmqpClient
argument_list|()
decl_stmt|;
name|AmqpConnection
name|connection
init|=
name|trackConnection
argument_list|(
name|client
operator|.
name|connect
argument_list|()
argument_list|)
decl_stmt|;
name|AmqpSession
name|session
init|=
name|connection
operator|.
name|createSession
argument_list|()
decl_stmt|;
name|AmqpSender
name|sender
init|=
name|session
operator|.
name|createSender
argument_list|(
literal|"queue://"
operator|+
name|getTestName
argument_list|()
argument_list|)
decl_stmt|;
name|AmqpMessage
name|message
init|=
operator|new
name|AmqpMessage
argument_list|()
decl_stmt|;
name|message
operator|.
name|setRawCorrelationId
argument_list|(
name|messageId
argument_list|)
expr_stmt|;
name|message
operator|.
name|setText
argument_list|(
literal|"Test-Message"
argument_list|)
expr_stmt|;
name|sender
operator|.
name|send
argument_list|(
name|message
argument_list|)
expr_stmt|;
name|sender
operator|.
name|close
argument_list|()
expr_stmt|;
name|QueueViewMBean
name|queue
init|=
name|getProxyToQueue
argument_list|(
name|getTestName
argument_list|()
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|queue
operator|.
name|getQueueSize
argument_list|()
argument_list|)
expr_stmt|;
name|AmqpReceiver
name|receiver
init|=
name|session
operator|.
name|createReceiver
argument_list|(
literal|"queue://"
operator|+
name|getTestName
argument_list|()
argument_list|)
decl_stmt|;
name|receiver
operator|.
name|flow
argument_list|(
literal|1
argument_list|)
expr_stmt|;
name|AmqpMessage
name|received
init|=
name|receiver
operator|.
name|receive
argument_list|(
literal|5
argument_list|,
name|TimeUnit
operator|.
name|SECONDS
argument_list|)
decl_stmt|;
name|assertNotNull
argument_list|(
literal|"Should have got a message"
argument_list|,
name|received
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|received
operator|.
name|getRawCorrelationId
argument_list|()
operator|.
name|getClass
argument_list|()
argument_list|,
name|messageId
operator|.
name|getClass
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|messageId
argument_list|,
name|received
operator|.
name|getRawCorrelationId
argument_list|()
argument_list|)
expr_stmt|;
name|receiver
operator|.
name|close
argument_list|()
expr_stmt|;
name|connection
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
specifier|public
name|void
name|doTestCorrelationIdPreservationOnBrokerRestart
parameter_list|(
name|Object
name|messageId
parameter_list|)
throws|throws
name|Exception
block|{
name|AmqpClient
name|client
init|=
name|createAmqpClient
argument_list|()
decl_stmt|;
name|AmqpConnection
name|connection
init|=
name|trackConnection
argument_list|(
name|client
operator|.
name|connect
argument_list|()
argument_list|)
decl_stmt|;
name|AmqpSession
name|session
init|=
name|connection
operator|.
name|createSession
argument_list|()
decl_stmt|;
name|AmqpSender
name|sender
init|=
name|session
operator|.
name|createSender
argument_list|(
literal|"queue://"
operator|+
name|getTestName
argument_list|()
argument_list|)
decl_stmt|;
name|AmqpMessage
name|message
init|=
operator|new
name|AmqpMessage
argument_list|()
decl_stmt|;
name|message
operator|.
name|setRawCorrelationId
argument_list|(
name|messageId
argument_list|)
expr_stmt|;
name|message
operator|.
name|setText
argument_list|(
literal|"Test-Message"
argument_list|)
expr_stmt|;
name|message
operator|.
name|setDurable
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|sender
operator|.
name|send
argument_list|(
name|message
argument_list|)
expr_stmt|;
name|sender
operator|.
name|close
argument_list|()
expr_stmt|;
name|connection
operator|.
name|close
argument_list|()
expr_stmt|;
name|restartBroker
argument_list|()
expr_stmt|;
name|QueueViewMBean
name|queue
init|=
name|getProxyToQueue
argument_list|(
name|getTestName
argument_list|()
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|queue
operator|.
name|getQueueSize
argument_list|()
argument_list|)
expr_stmt|;
name|connection
operator|=
name|client
operator|.
name|connect
argument_list|()
expr_stmt|;
name|session
operator|=
name|connection
operator|.
name|createSession
argument_list|()
expr_stmt|;
name|AmqpReceiver
name|receiver
init|=
name|session
operator|.
name|createReceiver
argument_list|(
literal|"queue://"
operator|+
name|getTestName
argument_list|()
argument_list|)
decl_stmt|;
name|receiver
operator|.
name|flow
argument_list|(
literal|1
argument_list|)
expr_stmt|;
name|AmqpMessage
name|received
init|=
name|receiver
operator|.
name|receive
argument_list|(
literal|5
argument_list|,
name|TimeUnit
operator|.
name|SECONDS
argument_list|)
decl_stmt|;
name|assertNotNull
argument_list|(
literal|"Should have got a message"
argument_list|,
name|received
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|received
operator|.
name|getRawCorrelationId
argument_list|()
operator|.
name|getClass
argument_list|()
argument_list|,
name|messageId
operator|.
name|getClass
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|messageId
argument_list|,
name|received
operator|.
name|getRawCorrelationId
argument_list|()
argument_list|)
expr_stmt|;
name|receiver
operator|.
name|close
argument_list|()
expr_stmt|;
name|connection
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
end_class

end_unit

