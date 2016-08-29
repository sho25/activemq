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
name|MessageListener
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
name|region
operator|.
name|Subscription
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
name|ActiveMQDestination
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
name|ActiveMQQueue
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
name|ConsumerControl
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
name|ExceptionResponse
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
name|spring
operator|.
name|SpringConsumer
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
comment|/**  *  */
end_comment

begin_class
specifier|public
class|class
name|ZeroPrefetchConsumerTest
extends|extends
name|EmbeddedBrokerTestSupport
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
name|ZeroPrefetchConsumerTest
operator|.
name|class
argument_list|)
decl_stmt|;
specifier|protected
name|Connection
name|connection
decl_stmt|;
specifier|protected
name|Queue
name|queue
decl_stmt|;
specifier|protected
name|Queue
name|brokerZeroQueue
init|=
operator|new
name|ActiveMQQueue
argument_list|(
literal|"brokerZeroConfig"
argument_list|)
decl_stmt|;
specifier|public
name|void
name|testCannotUseMessageListener
parameter_list|()
throws|throws
name|Exception
block|{
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
name|MessageListener
name|listener
init|=
operator|new
name|SpringConsumer
argument_list|()
decl_stmt|;
try|try
block|{
name|consumer
operator|.
name|setMessageListener
argument_list|(
name|listener
argument_list|)
expr_stmt|;
name|fail
argument_list|(
literal|"Should have thrown JMSException as we cannot use MessageListener with zero prefetch"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|JMSException
name|e
parameter_list|)
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"Received expected exception : "
operator|+
name|e
argument_list|)
expr_stmt|;
block|}
block|}
specifier|public
name|void
name|testPullConsumerWorks
parameter_list|()
throws|throws
name|Exception
block|{
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
name|queue
argument_list|)
decl_stmt|;
name|producer
operator|.
name|send
argument_list|(
name|session
operator|.
name|createTextMessage
argument_list|(
literal|"Hello World!"
argument_list|)
argument_list|)
expr_stmt|;
comment|// now lets receive it
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
name|Message
name|answer
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
literal|"Should have received a message!"
argument_list|,
name|answer
argument_list|)
expr_stmt|;
comment|// check if method will return at all and will return a null
name|answer
operator|=
name|consumer
operator|.
name|receive
argument_list|(
literal|1
argument_list|)
expr_stmt|;
name|assertNull
argument_list|(
literal|"Should have not received a message!"
argument_list|,
name|answer
argument_list|)
expr_stmt|;
name|answer
operator|=
name|consumer
operator|.
name|receiveNoWait
argument_list|()
expr_stmt|;
name|assertNull
argument_list|(
literal|"Should have not received a message!"
argument_list|,
name|answer
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|testIdleConsumer
parameter_list|()
throws|throws
name|Exception
block|{
name|doTestIdleConsumer
argument_list|(
literal|false
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|testIdleConsumerTranscated
parameter_list|()
throws|throws
name|Exception
block|{
name|doTestIdleConsumer
argument_list|(
literal|true
argument_list|)
expr_stmt|;
block|}
specifier|private
name|void
name|doTestIdleConsumer
parameter_list|(
name|boolean
name|transacted
parameter_list|)
throws|throws
name|Exception
block|{
name|Session
name|session
init|=
name|connection
operator|.
name|createSession
argument_list|(
name|transacted
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
name|queue
argument_list|)
decl_stmt|;
name|producer
operator|.
name|send
argument_list|(
name|session
operator|.
name|createTextMessage
argument_list|(
literal|"Msg1"
argument_list|)
argument_list|)
expr_stmt|;
name|producer
operator|.
name|send
argument_list|(
name|session
operator|.
name|createTextMessage
argument_list|(
literal|"Msg2"
argument_list|)
argument_list|)
expr_stmt|;
if|if
condition|(
name|transacted
condition|)
block|{
name|session
operator|.
name|commit
argument_list|()
expr_stmt|;
block|}
comment|// now lets receive it
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
name|session
operator|.
name|createConsumer
argument_list|(
name|queue
argument_list|)
expr_stmt|;
name|TextMessage
name|answer
init|=
operator|(
name|TextMessage
operator|)
name|consumer
operator|.
name|receive
argument_list|(
literal|5000
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|"Should have received a message!"
argument_list|,
name|answer
operator|.
name|getText
argument_list|()
argument_list|,
literal|"Msg1"
argument_list|)
expr_stmt|;
if|if
condition|(
name|transacted
condition|)
block|{
name|session
operator|.
name|commit
argument_list|()
expr_stmt|;
block|}
comment|// this call would return null if prefetchSize> 0
name|answer
operator|=
operator|(
name|TextMessage
operator|)
name|consumer
operator|.
name|receive
argument_list|(
literal|5000
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"Should have received a message!"
argument_list|,
name|answer
operator|.
name|getText
argument_list|()
argument_list|,
literal|"Msg2"
argument_list|)
expr_stmt|;
if|if
condition|(
name|transacted
condition|)
block|{
name|session
operator|.
name|commit
argument_list|()
expr_stmt|;
block|}
name|answer
operator|=
operator|(
name|TextMessage
operator|)
name|consumer
operator|.
name|receiveNoWait
argument_list|()
expr_stmt|;
name|assertNull
argument_list|(
literal|"Should have not received a message!"
argument_list|,
name|answer
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|testRecvRecvCommit
parameter_list|()
throws|throws
name|Exception
block|{
name|doTestRecvRecvCommit
argument_list|(
literal|false
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|testRecvRecvCommitTranscated
parameter_list|()
throws|throws
name|Exception
block|{
name|doTestRecvRecvCommit
argument_list|(
literal|true
argument_list|)
expr_stmt|;
block|}
specifier|private
name|void
name|doTestRecvRecvCommit
parameter_list|(
name|boolean
name|transacted
parameter_list|)
throws|throws
name|Exception
block|{
name|Session
name|session
init|=
name|connection
operator|.
name|createSession
argument_list|(
name|transacted
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
name|queue
argument_list|)
decl_stmt|;
name|producer
operator|.
name|send
argument_list|(
name|session
operator|.
name|createTextMessage
argument_list|(
literal|"Msg1"
argument_list|)
argument_list|)
expr_stmt|;
name|producer
operator|.
name|send
argument_list|(
name|session
operator|.
name|createTextMessage
argument_list|(
literal|"Msg2"
argument_list|)
argument_list|)
expr_stmt|;
if|if
condition|(
name|transacted
condition|)
block|{
name|session
operator|.
name|commit
argument_list|()
expr_stmt|;
block|}
comment|// now lets receive it
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
name|TextMessage
name|answer
init|=
operator|(
name|TextMessage
operator|)
name|consumer
operator|.
name|receiveNoWait
argument_list|()
decl_stmt|;
name|assertEquals
argument_list|(
literal|"Should have received a message!"
argument_list|,
name|answer
operator|.
name|getText
argument_list|()
argument_list|,
literal|"Msg1"
argument_list|)
expr_stmt|;
name|answer
operator|=
operator|(
name|TextMessage
operator|)
name|consumer
operator|.
name|receiveNoWait
argument_list|()
expr_stmt|;
name|assertEquals
argument_list|(
literal|"Should have received a message!"
argument_list|,
name|answer
operator|.
name|getText
argument_list|()
argument_list|,
literal|"Msg2"
argument_list|)
expr_stmt|;
if|if
condition|(
name|transacted
condition|)
block|{
name|session
operator|.
name|commit
argument_list|()
expr_stmt|;
block|}
name|answer
operator|=
operator|(
name|TextMessage
operator|)
name|consumer
operator|.
name|receiveNoWait
argument_list|()
expr_stmt|;
name|assertNull
argument_list|(
literal|"Should have not received a message!"
argument_list|,
name|answer
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|testTwoConsumers
parameter_list|()
throws|throws
name|Exception
block|{
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
name|queue
argument_list|)
decl_stmt|;
name|producer
operator|.
name|send
argument_list|(
name|session
operator|.
name|createTextMessage
argument_list|(
literal|"Msg1"
argument_list|)
argument_list|)
expr_stmt|;
name|producer
operator|.
name|send
argument_list|(
name|session
operator|.
name|createTextMessage
argument_list|(
literal|"Msg2"
argument_list|)
argument_list|)
expr_stmt|;
comment|// now lets receive it
name|MessageConsumer
name|consumer1
init|=
name|session
operator|.
name|createConsumer
argument_list|(
name|queue
argument_list|)
decl_stmt|;
name|MessageConsumer
name|consumer2
init|=
name|session
operator|.
name|createConsumer
argument_list|(
name|queue
argument_list|)
decl_stmt|;
name|TextMessage
name|answer
init|=
operator|(
name|TextMessage
operator|)
name|consumer1
operator|.
name|receiveNoWait
argument_list|()
decl_stmt|;
name|assertEquals
argument_list|(
literal|"Should have received a message!"
argument_list|,
name|answer
operator|.
name|getText
argument_list|()
argument_list|,
literal|"Msg1"
argument_list|)
expr_stmt|;
name|answer
operator|=
operator|(
name|TextMessage
operator|)
name|consumer2
operator|.
name|receiveNoWait
argument_list|()
expr_stmt|;
name|assertEquals
argument_list|(
literal|"Should have received a message!"
argument_list|,
name|answer
operator|.
name|getText
argument_list|()
argument_list|,
literal|"Msg2"
argument_list|)
expr_stmt|;
name|answer
operator|=
operator|(
name|TextMessage
operator|)
name|consumer2
operator|.
name|receiveNoWait
argument_list|()
expr_stmt|;
name|assertNull
argument_list|(
literal|"Should have not received a message!"
argument_list|,
name|answer
argument_list|)
expr_stmt|;
block|}
comment|// https://issues.apache.org/activemq/browse/AMQ-2567
specifier|public
name|void
name|testManyMessageConsumer
parameter_list|()
throws|throws
name|Exception
block|{
name|doTestManyMessageConsumer
argument_list|(
literal|true
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|testManyMessageConsumerNoTransaction
parameter_list|()
throws|throws
name|Exception
block|{
name|doTestManyMessageConsumer
argument_list|(
literal|false
argument_list|)
expr_stmt|;
block|}
specifier|private
name|void
name|doTestManyMessageConsumer
parameter_list|(
name|boolean
name|transacted
parameter_list|)
throws|throws
name|Exception
block|{
name|Session
name|session
init|=
name|connection
operator|.
name|createSession
argument_list|(
name|transacted
argument_list|,
name|transacted
condition|?
name|Session
operator|.
name|SESSION_TRANSACTED
else|:
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
name|queue
argument_list|)
decl_stmt|;
name|producer
operator|.
name|send
argument_list|(
name|session
operator|.
name|createTextMessage
argument_list|(
literal|"Msg1"
argument_list|)
argument_list|)
expr_stmt|;
name|producer
operator|.
name|send
argument_list|(
name|session
operator|.
name|createTextMessage
argument_list|(
literal|"Msg2"
argument_list|)
argument_list|)
expr_stmt|;
name|producer
operator|.
name|send
argument_list|(
name|session
operator|.
name|createTextMessage
argument_list|(
literal|"Msg3"
argument_list|)
argument_list|)
expr_stmt|;
name|producer
operator|.
name|send
argument_list|(
name|session
operator|.
name|createTextMessage
argument_list|(
literal|"Msg4"
argument_list|)
argument_list|)
expr_stmt|;
name|producer
operator|.
name|send
argument_list|(
name|session
operator|.
name|createTextMessage
argument_list|(
literal|"Msg5"
argument_list|)
argument_list|)
expr_stmt|;
name|producer
operator|.
name|send
argument_list|(
name|session
operator|.
name|createTextMessage
argument_list|(
literal|"Msg6"
argument_list|)
argument_list|)
expr_stmt|;
name|producer
operator|.
name|send
argument_list|(
name|session
operator|.
name|createTextMessage
argument_list|(
literal|"Msg7"
argument_list|)
argument_list|)
expr_stmt|;
name|producer
operator|.
name|send
argument_list|(
name|session
operator|.
name|createTextMessage
argument_list|(
literal|"Msg8"
argument_list|)
argument_list|)
expr_stmt|;
if|if
condition|(
name|transacted
condition|)
block|{
name|session
operator|.
name|commit
argument_list|()
expr_stmt|;
block|}
comment|// now lets receive it
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
name|MessageConsumer
name|consumer2
init|=
name|session
operator|.
name|createConsumer
argument_list|(
name|queue
argument_list|)
decl_stmt|;
name|TextMessage
name|answer
init|=
operator|(
name|TextMessage
operator|)
name|consumer
operator|.
name|receive
argument_list|(
literal|5000
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|"Should have received a message!"
argument_list|,
name|answer
operator|.
name|getText
argument_list|()
argument_list|,
literal|"Msg1"
argument_list|)
expr_stmt|;
if|if
condition|(
name|transacted
condition|)
block|{
name|session
operator|.
name|commit
argument_list|()
expr_stmt|;
block|}
name|answer
operator|=
operator|(
name|TextMessage
operator|)
name|consumer
operator|.
name|receive
argument_list|(
literal|5000
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"Should have received a message!"
argument_list|,
name|answer
operator|.
name|getText
argument_list|()
argument_list|,
literal|"Msg2"
argument_list|)
expr_stmt|;
if|if
condition|(
name|transacted
condition|)
block|{
name|session
operator|.
name|commit
argument_list|()
expr_stmt|;
block|}
name|answer
operator|=
operator|(
name|TextMessage
operator|)
name|consumer
operator|.
name|receive
argument_list|(
literal|5000
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"Should have received a message!"
argument_list|,
name|answer
operator|.
name|getText
argument_list|()
argument_list|,
literal|"Msg3"
argument_list|)
expr_stmt|;
if|if
condition|(
name|transacted
condition|)
block|{
name|session
operator|.
name|commit
argument_list|()
expr_stmt|;
block|}
comment|// this call would return null if prefetchSize> 0
name|answer
operator|=
operator|(
name|TextMessage
operator|)
name|consumer
operator|.
name|receive
argument_list|(
literal|5000
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"Should have received a message!"
argument_list|,
name|answer
operator|.
name|getText
argument_list|()
argument_list|,
literal|"Msg4"
argument_list|)
expr_stmt|;
if|if
condition|(
name|transacted
condition|)
block|{
name|session
operator|.
name|commit
argument_list|()
expr_stmt|;
block|}
comment|// Now using other consumer
comment|// this call should return the next message (Msg5) still left on the queue
name|answer
operator|=
operator|(
name|TextMessage
operator|)
name|consumer2
operator|.
name|receive
argument_list|(
literal|5000
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"Should have received a message!"
argument_list|,
name|answer
operator|.
name|getText
argument_list|()
argument_list|,
literal|"Msg5"
argument_list|)
expr_stmt|;
if|if
condition|(
name|transacted
condition|)
block|{
name|session
operator|.
name|commit
argument_list|()
expr_stmt|;
block|}
comment|// Now using other consumer
comment|// this call should return the next message still left on the queue
name|answer
operator|=
operator|(
name|TextMessage
operator|)
name|consumer
operator|.
name|receive
argument_list|(
literal|5000
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"Should have received a message!"
argument_list|,
name|answer
operator|.
name|getText
argument_list|()
argument_list|,
literal|"Msg6"
argument_list|)
expr_stmt|;
comment|// read one more message without commit
comment|// this call should return the next message still left on the queue
name|answer
operator|=
operator|(
name|TextMessage
operator|)
name|consumer
operator|.
name|receive
argument_list|(
literal|5000
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"Should have received a message!"
argument_list|,
name|answer
operator|.
name|getText
argument_list|()
argument_list|,
literal|"Msg7"
argument_list|)
expr_stmt|;
if|if
condition|(
name|transacted
condition|)
block|{
name|session
operator|.
name|commit
argument_list|()
expr_stmt|;
block|}
comment|// Now using other consumer
comment|// this call should return the next message (Msg5) still left on the queue
name|answer
operator|=
operator|(
name|TextMessage
operator|)
name|consumer2
operator|.
name|receive
argument_list|(
literal|5000
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"Should have received a message!"
argument_list|,
name|answer
operator|.
name|getText
argument_list|()
argument_list|,
literal|"Msg8"
argument_list|)
expr_stmt|;
if|if
condition|(
name|transacted
condition|)
block|{
name|session
operator|.
name|commit
argument_list|()
expr_stmt|;
block|}
name|answer
operator|=
operator|(
name|TextMessage
operator|)
name|consumer
operator|.
name|receiveNoWait
argument_list|()
expr_stmt|;
name|assertNull
argument_list|(
literal|"Should have not received a message!"
argument_list|,
name|answer
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|testManyMessageConsumerWithSend
parameter_list|()
throws|throws
name|Exception
block|{
name|doTestManyMessageConsumerWithSend
argument_list|(
literal|true
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|testManyMessageConsumerWithTxSendPrioritySupport
parameter_list|()
throws|throws
name|Exception
block|{
operator|(
operator|(
name|ActiveMQConnection
operator|)
name|connection
operator|)
operator|.
name|setMessagePrioritySupported
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|doTestManyMessageConsumerWithSend
argument_list|(
literal|true
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|testManyMessageConsumerWithSendNoTransaction
parameter_list|()
throws|throws
name|Exception
block|{
name|doTestManyMessageConsumerWithSend
argument_list|(
literal|false
argument_list|)
expr_stmt|;
block|}
specifier|private
name|void
name|doTestManyMessageConsumerWithSend
parameter_list|(
name|boolean
name|transacted
parameter_list|)
throws|throws
name|Exception
block|{
name|Session
name|session
init|=
name|connection
operator|.
name|createSession
argument_list|(
name|transacted
argument_list|,
name|transacted
condition|?
name|Session
operator|.
name|SESSION_TRANSACTED
else|:
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
name|queue
argument_list|)
decl_stmt|;
name|producer
operator|.
name|send
argument_list|(
name|session
operator|.
name|createTextMessage
argument_list|(
literal|"Msg1"
argument_list|)
argument_list|)
expr_stmt|;
name|producer
operator|.
name|send
argument_list|(
name|session
operator|.
name|createTextMessage
argument_list|(
literal|"Msg2"
argument_list|)
argument_list|)
expr_stmt|;
name|producer
operator|.
name|send
argument_list|(
name|session
operator|.
name|createTextMessage
argument_list|(
literal|"Msg3"
argument_list|)
argument_list|)
expr_stmt|;
name|producer
operator|.
name|send
argument_list|(
name|session
operator|.
name|createTextMessage
argument_list|(
literal|"Msg4"
argument_list|)
argument_list|)
expr_stmt|;
name|producer
operator|.
name|send
argument_list|(
name|session
operator|.
name|createTextMessage
argument_list|(
literal|"Msg5"
argument_list|)
argument_list|)
expr_stmt|;
name|producer
operator|.
name|send
argument_list|(
name|session
operator|.
name|createTextMessage
argument_list|(
literal|"Msg6"
argument_list|)
argument_list|)
expr_stmt|;
name|producer
operator|.
name|send
argument_list|(
name|session
operator|.
name|createTextMessage
argument_list|(
literal|"Msg7"
argument_list|)
argument_list|)
expr_stmt|;
name|producer
operator|.
name|send
argument_list|(
name|session
operator|.
name|createTextMessage
argument_list|(
literal|"Msg8"
argument_list|)
argument_list|)
expr_stmt|;
if|if
condition|(
name|transacted
condition|)
block|{
name|session
operator|.
name|commit
argument_list|()
expr_stmt|;
block|}
comment|// now lets receive it
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
name|MessageConsumer
name|consumer2
init|=
name|session
operator|.
name|createConsumer
argument_list|(
name|queue
argument_list|)
decl_stmt|;
name|TextMessage
name|answer
init|=
operator|(
name|TextMessage
operator|)
name|consumer
operator|.
name|receive
argument_list|(
literal|5000
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|"Should have received a message!"
argument_list|,
name|answer
operator|.
name|getText
argument_list|()
argument_list|,
literal|"Msg1"
argument_list|)
expr_stmt|;
if|if
condition|(
name|transacted
condition|)
block|{
name|session
operator|.
name|commit
argument_list|()
expr_stmt|;
block|}
name|answer
operator|=
operator|(
name|TextMessage
operator|)
name|consumer
operator|.
name|receive
argument_list|(
literal|5000
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"Should have received a message!"
argument_list|,
name|answer
operator|.
name|getText
argument_list|()
argument_list|,
literal|"Msg2"
argument_list|)
expr_stmt|;
if|if
condition|(
name|transacted
condition|)
block|{
name|session
operator|.
name|commit
argument_list|()
expr_stmt|;
block|}
name|answer
operator|=
operator|(
name|TextMessage
operator|)
name|consumer
operator|.
name|receive
argument_list|(
literal|5000
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"Should have received a message!"
argument_list|,
name|answer
operator|.
name|getText
argument_list|()
argument_list|,
literal|"Msg3"
argument_list|)
expr_stmt|;
if|if
condition|(
name|transacted
condition|)
block|{
name|session
operator|.
name|commit
argument_list|()
expr_stmt|;
block|}
comment|// Now using other consumer take 2
name|answer
operator|=
operator|(
name|TextMessage
operator|)
name|consumer2
operator|.
name|receive
argument_list|(
literal|5000
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"Should have received a message!"
argument_list|,
name|answer
operator|.
name|getText
argument_list|()
argument_list|,
literal|"Msg4"
argument_list|)
expr_stmt|;
name|answer
operator|=
operator|(
name|TextMessage
operator|)
name|consumer2
operator|.
name|receive
argument_list|(
literal|5000
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"Should have received a message!"
argument_list|,
name|answer
operator|.
name|getText
argument_list|()
argument_list|,
literal|"Msg5"
argument_list|)
expr_stmt|;
comment|// ensure prefetch extension ok by sending another that could get dispatched
name|producer
operator|.
name|send
argument_list|(
name|session
operator|.
name|createTextMessage
argument_list|(
literal|"Msg9"
argument_list|)
argument_list|)
expr_stmt|;
if|if
condition|(
name|transacted
condition|)
block|{
name|session
operator|.
name|commit
argument_list|()
expr_stmt|;
block|}
name|answer
operator|=
operator|(
name|TextMessage
operator|)
name|consumer
operator|.
name|receive
argument_list|(
literal|5000
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"Should have received a message!"
argument_list|,
name|answer
operator|.
name|getText
argument_list|()
argument_list|,
literal|"Msg6"
argument_list|)
expr_stmt|;
comment|// read one more message without commit
comment|// and using other consumer
name|answer
operator|=
operator|(
name|TextMessage
operator|)
name|consumer2
operator|.
name|receive
argument_list|(
literal|5000
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"Should have received a message!"
argument_list|,
name|answer
operator|.
name|getText
argument_list|()
argument_list|,
literal|"Msg7"
argument_list|)
expr_stmt|;
if|if
condition|(
name|transacted
condition|)
block|{
name|session
operator|.
name|commit
argument_list|()
expr_stmt|;
block|}
name|answer
operator|=
operator|(
name|TextMessage
operator|)
name|consumer2
operator|.
name|receive
argument_list|(
literal|5000
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"Should have received a message!"
argument_list|,
name|answer
operator|.
name|getText
argument_list|()
argument_list|,
literal|"Msg8"
argument_list|)
expr_stmt|;
if|if
condition|(
name|transacted
condition|)
block|{
name|session
operator|.
name|commit
argument_list|()
expr_stmt|;
block|}
name|answer
operator|=
operator|(
name|TextMessage
operator|)
name|consumer
operator|.
name|receive
argument_list|(
literal|5000
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"Should have received a message!"
argument_list|,
name|answer
operator|.
name|getText
argument_list|()
argument_list|,
literal|"Msg9"
argument_list|)
expr_stmt|;
if|if
condition|(
name|transacted
condition|)
block|{
name|session
operator|.
name|commit
argument_list|()
expr_stmt|;
block|}
name|answer
operator|=
operator|(
name|TextMessage
operator|)
name|consumer
operator|.
name|receiveNoWait
argument_list|()
expr_stmt|;
name|assertNull
argument_list|(
literal|"Should have not received a message!"
argument_list|,
name|answer
argument_list|)
expr_stmt|;
block|}
comment|// https://issues.apache.org/jira/browse/AMQ-4224
specifier|public
name|void
name|testBrokerZeroPrefetchConfig
parameter_list|()
throws|throws
name|Exception
block|{
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
name|brokerZeroQueue
argument_list|)
decl_stmt|;
name|producer
operator|.
name|send
argument_list|(
name|session
operator|.
name|createTextMessage
argument_list|(
literal|"Msg1"
argument_list|)
argument_list|)
expr_stmt|;
comment|// now lets receive it
name|MessageConsumer
name|consumer
init|=
name|session
operator|.
name|createConsumer
argument_list|(
name|brokerZeroQueue
argument_list|)
decl_stmt|;
name|TextMessage
name|answer
init|=
operator|(
name|TextMessage
operator|)
name|consumer
operator|.
name|receive
argument_list|(
literal|5000
argument_list|)
decl_stmt|;
name|assertNotNull
argument_list|(
literal|"Consumer should have read a message"
argument_list|,
name|answer
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"Should have received a message!"
argument_list|,
name|answer
operator|.
name|getText
argument_list|()
argument_list|,
literal|"Msg1"
argument_list|)
expr_stmt|;
block|}
comment|// https://issues.apache.org/jira/browse/AMQ-4234
comment|// https://issues.apache.org/jira/browse/AMQ-4235
specifier|public
name|void
name|testBrokerZeroPrefetchConfigWithConsumerControl
parameter_list|()
throws|throws
name|Exception
block|{
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
name|ActiveMQMessageConsumer
name|consumer
init|=
operator|(
name|ActiveMQMessageConsumer
operator|)
name|session
operator|.
name|createConsumer
argument_list|(
name|brokerZeroQueue
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|"broker config prefetch in effect"
argument_list|,
literal|0
argument_list|,
name|consumer
operator|.
name|info
operator|.
name|getCurrentPrefetchSize
argument_list|()
argument_list|)
expr_stmt|;
comment|// verify sub view broker
name|Subscription
name|sub
init|=
name|broker
operator|.
name|getRegionBroker
argument_list|()
operator|.
name|getDestinationMap
argument_list|()
operator|.
name|get
argument_list|(
name|ActiveMQDestination
operator|.
name|transform
argument_list|(
name|brokerZeroQueue
argument_list|)
argument_list|)
operator|.
name|getConsumers
argument_list|()
operator|.
name|get
argument_list|(
literal|0
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|"broker sub prefetch is correct"
argument_list|,
literal|0
argument_list|,
name|sub
operator|.
name|getConsumerInfo
argument_list|()
operator|.
name|getCurrentPrefetchSize
argument_list|()
argument_list|)
expr_stmt|;
comment|// manipulate Prefetch (like failover and stomp)
name|ConsumerControl
name|consumerControl
init|=
operator|new
name|ConsumerControl
argument_list|()
decl_stmt|;
name|consumerControl
operator|.
name|setConsumerId
argument_list|(
name|consumer
operator|.
name|info
operator|.
name|getConsumerId
argument_list|()
argument_list|)
expr_stmt|;
name|consumerControl
operator|.
name|setDestination
argument_list|(
name|ActiveMQDestination
operator|.
name|transform
argument_list|(
name|brokerZeroQueue
argument_list|)
argument_list|)
expr_stmt|;
name|consumerControl
operator|.
name|setPrefetch
argument_list|(
literal|1000
argument_list|)
expr_stmt|;
comment|// default for a q
name|Object
name|reply
init|=
operator|(
operator|(
name|ActiveMQConnection
operator|)
name|connection
operator|)
operator|.
name|getTransport
argument_list|()
operator|.
name|request
argument_list|(
name|consumerControl
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
literal|"good request"
argument_list|,
operator|!
operator|(
name|reply
operator|instanceof
name|ExceptionResponse
operator|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"broker config prefetch in effect"
argument_list|,
literal|0
argument_list|,
name|consumer
operator|.
name|info
operator|.
name|getCurrentPrefetchSize
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"broker sub prefetch is correct"
argument_list|,
literal|0
argument_list|,
name|sub
operator|.
name|getConsumerInfo
argument_list|()
operator|.
name|getCurrentPrefetchSize
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|protected
name|BrokerService
name|createBroker
parameter_list|()
throws|throws
name|Exception
block|{
name|BrokerService
name|brokerService
init|=
name|super
operator|.
name|createBroker
argument_list|()
decl_stmt|;
name|PolicyMap
name|policyMap
init|=
operator|new
name|PolicyMap
argument_list|()
decl_stmt|;
name|PolicyEntry
name|zeroPrefetchPolicy
init|=
operator|new
name|PolicyEntry
argument_list|()
decl_stmt|;
name|zeroPrefetchPolicy
operator|.
name|setQueuePrefetch
argument_list|(
literal|0
argument_list|)
expr_stmt|;
name|policyMap
operator|.
name|put
argument_list|(
name|ActiveMQDestination
operator|.
name|transform
argument_list|(
name|brokerZeroQueue
argument_list|)
argument_list|,
name|zeroPrefetchPolicy
argument_list|)
expr_stmt|;
name|brokerService
operator|.
name|setDestinationPolicy
argument_list|(
name|policyMap
argument_list|)
expr_stmt|;
return|return
name|brokerService
return|;
block|}
annotation|@
name|Override
specifier|protected
name|void
name|setUp
parameter_list|()
throws|throws
name|Exception
block|{
name|bindAddress
operator|=
literal|"tcp://localhost:0"
expr_stmt|;
name|super
operator|.
name|setUp
argument_list|()
expr_stmt|;
name|connection
operator|=
name|createConnection
argument_list|()
expr_stmt|;
name|connection
operator|.
name|start
argument_list|()
expr_stmt|;
name|queue
operator|=
name|createQueue
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Override
specifier|protected
name|void
name|startBroker
parameter_list|()
throws|throws
name|Exception
block|{
name|super
operator|.
name|startBroker
argument_list|()
expr_stmt|;
name|bindAddress
operator|=
name|broker
operator|.
name|getTransportConnectors
argument_list|()
operator|.
name|get
argument_list|(
literal|0
argument_list|)
operator|.
name|getConnectUri
argument_list|()
operator|.
name|toString
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Override
specifier|protected
name|void
name|tearDown
parameter_list|()
throws|throws
name|Exception
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
name|Exception
name|ex
parameter_list|)
block|{}
name|super
operator|.
name|tearDown
argument_list|()
expr_stmt|;
block|}
specifier|protected
name|Queue
name|createQueue
parameter_list|()
block|{
return|return
operator|new
name|ActiveMQQueue
argument_list|(
name|getDestinationString
argument_list|()
operator|+
literal|"?consumer.prefetchSize=0"
argument_list|)
return|;
block|}
block|}
end_class

end_unit

