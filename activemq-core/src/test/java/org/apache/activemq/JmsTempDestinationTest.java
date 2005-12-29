begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  *  * Copyright 2005-2006 The Apache Software Foundation  *  * Licensed under the Apache License, Version 2.0 (the "License");  * you may not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  * http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
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
name|util
operator|.
name|ArrayList
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
name|junit
operator|.
name|framework
operator|.
name|TestCase
import|;
end_import

begin_comment
comment|/**  * @version  */
end_comment

begin_class
specifier|public
class|class
name|JmsTempDestinationTest
extends|extends
name|TestCase
block|{
specifier|private
name|Connection
name|connection
decl_stmt|;
specifier|private
name|ActiveMQConnectionFactory
name|factory
decl_stmt|;
specifier|protected
name|void
name|setUp
parameter_list|()
throws|throws
name|Exception
block|{
name|factory
operator|=
operator|new
name|ActiveMQConnectionFactory
argument_list|(
literal|"vm://localhost?broker.persistent=false"
argument_list|)
expr_stmt|;
name|factory
operator|.
name|setUseAsyncSend
argument_list|(
literal|false
argument_list|)
expr_stmt|;
name|connection
operator|=
name|factory
operator|.
name|createConnection
argument_list|()
expr_stmt|;
block|}
comment|/**      * @see junit.framework.TestCase#tearDown()      */
specifier|protected
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
name|connection
operator|=
literal|null
expr_stmt|;
block|}
block|}
comment|/**      * Make sure Temp destination can only be consumed by local connection      *      * @throws JMSException      */
specifier|public
name|void
name|testTempDestOnlyConsumedByLocalConn
parameter_list|()
throws|throws
name|JMSException
block|{
name|connection
operator|.
name|start
argument_list|()
expr_stmt|;
name|Session
name|tempSession
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
name|TemporaryQueue
name|queue
init|=
name|tempSession
operator|.
name|createTemporaryQueue
argument_list|()
decl_stmt|;
name|MessageProducer
name|producer
init|=
name|tempSession
operator|.
name|createProducer
argument_list|(
name|queue
argument_list|)
decl_stmt|;
name|producer
operator|.
name|setDeliveryMode
argument_list|(
name|DeliveryMode
operator|.
name|NON_PERSISTENT
argument_list|)
expr_stmt|;
name|TextMessage
name|message
init|=
name|tempSession
operator|.
name|createTextMessage
argument_list|(
literal|"First"
argument_list|)
decl_stmt|;
name|producer
operator|.
name|send
argument_list|(
name|message
argument_list|)
expr_stmt|;
comment|//temp destination should not be consume  when using another connection
name|Connection
name|otherConnection
init|=
name|factory
operator|.
name|createConnection
argument_list|()
decl_stmt|;
name|Session
name|otherSession
init|=
name|otherConnection
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
name|TemporaryQueue
name|otherQueue
init|=
name|otherSession
operator|.
name|createTemporaryQueue
argument_list|()
decl_stmt|;
name|MessageConsumer
name|consumer
init|=
name|otherSession
operator|.
name|createConsumer
argument_list|(
name|otherQueue
argument_list|)
decl_stmt|;
name|Message
name|msg
init|=
name|consumer
operator|.
name|receive
argument_list|(
literal|3000
argument_list|)
decl_stmt|;
name|assertNull
argument_list|(
name|msg
argument_list|)
expr_stmt|;
comment|//should throw InvalidDestinationException when consuming a temp destination from another connection
try|try
block|{
name|consumer
operator|=
name|otherSession
operator|.
name|createConsumer
argument_list|(
name|queue
argument_list|)
expr_stmt|;
name|fail
argument_list|(
literal|"Send should fail since temp destination should be used from another connection"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|InvalidDestinationException
name|e
parameter_list|)
block|{
name|assertTrue
argument_list|(
literal|"failed to throw an exception"
argument_list|,
literal|true
argument_list|)
expr_stmt|;
block|}
comment|//should be able to consume temp destination from the same connection
name|consumer
operator|=
name|tempSession
operator|.
name|createConsumer
argument_list|(
name|queue
argument_list|)
expr_stmt|;
name|msg
operator|=
name|consumer
operator|.
name|receive
argument_list|(
literal|3000
argument_list|)
expr_stmt|;
name|assertNotNull
argument_list|(
name|msg
argument_list|)
expr_stmt|;
block|}
comment|/**      * Make sure that a temp queue does not drop message if there is an active      * consumers.      *       * @throws JMSException      */
specifier|public
name|void
name|testTempQueueHoldsMessagesWithConsumers
parameter_list|()
throws|throws
name|JMSException
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
name|Queue
name|queue
init|=
name|session
operator|.
name|createTemporaryQueue
argument_list|()
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
name|connection
operator|.
name|start
argument_list|()
expr_stmt|;
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
name|setDeliveryMode
argument_list|(
name|DeliveryMode
operator|.
name|NON_PERSISTENT
argument_list|)
expr_stmt|;
name|TextMessage
name|message
init|=
name|session
operator|.
name|createTextMessage
argument_list|(
literal|"Hello"
argument_list|)
decl_stmt|;
name|producer
operator|.
name|send
argument_list|(
name|message
argument_list|)
expr_stmt|;
name|Message
name|message2
init|=
name|consumer
operator|.
name|receive
argument_list|(
literal|1000
argument_list|)
decl_stmt|;
name|assertNotNull
argument_list|(
name|message2
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
literal|"Expected message to be a TextMessage"
argument_list|,
name|message2
operator|instanceof
name|TextMessage
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
literal|"Expected message to be a '"
operator|+
name|message
operator|.
name|getText
argument_list|()
operator|+
literal|"'"
argument_list|,
operator|(
operator|(
name|TextMessage
operator|)
name|message2
operator|)
operator|.
name|getText
argument_list|()
operator|.
name|equals
argument_list|(
name|message
operator|.
name|getText
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|/**      * Make sure that a temp queue does not drop message if there are no active      * consumers.      *       * @throws JMSException      */
specifier|public
name|void
name|testTempQueueHoldsMessagesWithoutConsumers
parameter_list|()
throws|throws
name|JMSException
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
name|Queue
name|queue
init|=
name|session
operator|.
name|createTemporaryQueue
argument_list|()
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
name|setDeliveryMode
argument_list|(
name|DeliveryMode
operator|.
name|NON_PERSISTENT
argument_list|)
expr_stmt|;
name|TextMessage
name|message
init|=
name|session
operator|.
name|createTextMessage
argument_list|(
literal|"Hello"
argument_list|)
decl_stmt|;
name|producer
operator|.
name|send
argument_list|(
name|message
argument_list|)
expr_stmt|;
name|connection
operator|.
name|start
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
name|Message
name|message2
init|=
name|consumer
operator|.
name|receive
argument_list|(
literal|3000
argument_list|)
decl_stmt|;
name|assertNotNull
argument_list|(
name|message2
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
literal|"Expected message to be a TextMessage"
argument_list|,
name|message2
operator|instanceof
name|TextMessage
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
literal|"Expected message to be a '"
operator|+
name|message
operator|.
name|getText
argument_list|()
operator|+
literal|"'"
argument_list|,
operator|(
operator|(
name|TextMessage
operator|)
name|message2
operator|)
operator|.
name|getText
argument_list|()
operator|.
name|equals
argument_list|(
name|message
operator|.
name|getText
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|/**      * Test temp queue works under load      * @throws JMSException      */
specifier|public
name|void
name|testTmpQueueWorksUnderLoad
parameter_list|()
throws|throws
name|JMSException
block|{
name|int
name|count
init|=
literal|500
decl_stmt|;
name|int
name|dataSize
init|=
literal|1024
decl_stmt|;
name|ArrayList
name|list
init|=
operator|new
name|ArrayList
argument_list|(
name|count
argument_list|)
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
name|createTemporaryQueue
argument_list|()
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
name|setDeliveryMode
argument_list|(
name|DeliveryMode
operator|.
name|NON_PERSISTENT
argument_list|)
expr_stmt|;
name|byte
index|[]
name|data
init|=
operator|new
name|byte
index|[
name|dataSize
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
name|count
condition|;
name|i
operator|++
control|)
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
name|data
argument_list|)
expr_stmt|;
name|producer
operator|.
name|send
argument_list|(
name|message
argument_list|)
expr_stmt|;
name|list
operator|.
name|add
argument_list|(
name|message
argument_list|)
expr_stmt|;
block|}
name|connection
operator|.
name|start
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
name|message2
init|=
name|consumer
operator|.
name|receive
argument_list|(
literal|2000
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
name|message2
operator|!=
literal|null
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|message2
operator|.
name|equals
argument_list|(
name|list
operator|.
name|get
argument_list|(
name|i
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
comment|/**      * Make sure you cannot publish to a temp destination that does not exist anymore.      *       * @throws JMSException      */
specifier|public
name|void
name|testPublishFailsForClosedConnection
parameter_list|()
throws|throws
name|JMSException
block|{
name|Connection
name|tempConnection
init|=
name|factory
operator|.
name|createConnection
argument_list|()
decl_stmt|;
name|Session
name|tempSession
init|=
name|tempConnection
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
name|TemporaryQueue
name|queue
init|=
name|tempSession
operator|.
name|createTemporaryQueue
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
name|connection
operator|.
name|start
argument_list|()
expr_stmt|;
comment|// This message delivery should work since the temp connection is still open.
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
name|setDeliveryMode
argument_list|(
name|DeliveryMode
operator|.
name|NON_PERSISTENT
argument_list|)
expr_stmt|;
name|TextMessage
name|message
init|=
name|session
operator|.
name|createTextMessage
argument_list|(
literal|"First"
argument_list|)
decl_stmt|;
name|producer
operator|.
name|send
argument_list|(
name|message
argument_list|)
expr_stmt|;
comment|// Closing the connection should destroy the temp queue that was created.
name|tempConnection
operator|.
name|close
argument_list|()
expr_stmt|;
comment|// This message delivery NOT should work since the temp connection is now closed.
try|try
block|{
name|message
operator|=
name|session
operator|.
name|createTextMessage
argument_list|(
literal|"Hello"
argument_list|)
expr_stmt|;
name|producer
operator|.
name|send
argument_list|(
name|message
argument_list|)
expr_stmt|;
name|fail
argument_list|(
literal|"Send should fail since temp destination should not exist anymore."
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|JMSException
name|e
parameter_list|)
block|{
name|assertTrue
argument_list|(
literal|"failed to throw an exception"
argument_list|,
literal|true
argument_list|)
expr_stmt|;
block|}
block|}
comment|/**      * Make sure you cannot publish to a temp destination that does not exist anymore.      *       * @throws JMSException      */
specifier|public
name|void
name|testPublishFailsForDestoryedTempDestination
parameter_list|()
throws|throws
name|JMSException
block|{
name|Connection
name|tempConnection
init|=
name|factory
operator|.
name|createConnection
argument_list|()
decl_stmt|;
name|Session
name|tempSession
init|=
name|tempConnection
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
name|TemporaryQueue
name|queue
init|=
name|tempSession
operator|.
name|createTemporaryQueue
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
name|connection
operator|.
name|start
argument_list|()
expr_stmt|;
comment|// This message delivery should work since the temp connection is still open.
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
name|setDeliveryMode
argument_list|(
name|DeliveryMode
operator|.
name|NON_PERSISTENT
argument_list|)
expr_stmt|;
name|TextMessage
name|message
init|=
name|session
operator|.
name|createTextMessage
argument_list|(
literal|"First"
argument_list|)
decl_stmt|;
name|producer
operator|.
name|send
argument_list|(
name|message
argument_list|)
expr_stmt|;
comment|// deleting the Queue will cause sends to fail
name|queue
operator|.
name|delete
argument_list|()
expr_stmt|;
comment|// This message delivery NOT should work since the temp connection is now closed.
try|try
block|{
name|message
operator|=
name|session
operator|.
name|createTextMessage
argument_list|(
literal|"Hello"
argument_list|)
expr_stmt|;
name|producer
operator|.
name|send
argument_list|(
name|message
argument_list|)
expr_stmt|;
name|fail
argument_list|(
literal|"Send should fail since temp destination should not exist anymore."
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|JMSException
name|e
parameter_list|)
block|{
name|assertTrue
argument_list|(
literal|"failed to throw an exception"
argument_list|,
literal|true
argument_list|)
expr_stmt|;
block|}
block|}
comment|/**      * Test you can't delete a Destination with Active Subscribers      * @throws JMSException      */
specifier|public
name|void
name|testDeleteDestinationWithSubscribersFails
parameter_list|()
throws|throws
name|JMSException
block|{
name|Connection
name|connection
init|=
name|factory
operator|.
name|createConnection
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
name|TemporaryQueue
name|queue
init|=
name|session
operator|.
name|createTemporaryQueue
argument_list|()
decl_stmt|;
name|connection
operator|.
name|start
argument_list|()
expr_stmt|;
name|session
operator|.
name|createConsumer
argument_list|(
name|queue
argument_list|)
expr_stmt|;
comment|// This message delivery should NOT work since the temp connection is now closed.
try|try
block|{
name|queue
operator|.
name|delete
argument_list|()
expr_stmt|;
name|fail
argument_list|(
literal|"Should fail as Subscribers are active"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|JMSException
name|e
parameter_list|)
block|{
name|assertTrue
argument_list|(
literal|"failed to throw an exception"
argument_list|,
literal|true
argument_list|)
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

