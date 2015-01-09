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
name|util
operator|.
name|concurrent
operator|.
name|TimeUnit
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
name|javax
operator|.
name|jms
operator|.
name|Topic
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
name|junit
operator|.
name|framework
operator|.
name|TestCase
import|;
end_import

begin_import
import|import
name|junit
operator|.
name|framework
operator|.
name|TestSuite
import|;
end_import

begin_comment
comment|/**  *   */
end_comment

begin_class
specifier|public
class|class
name|JmsRedeliveredTest
extends|extends
name|TestCase
block|{
specifier|private
name|Connection
name|connection
decl_stmt|;
comment|/*      * (non-Javadoc)      *       * @see junit.framework.TestCase#setUp()      */
specifier|protected
name|void
name|setUp
parameter_list|()
throws|throws
name|Exception
block|{
name|connection
operator|=
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
comment|/**      * Creates a connection.      *       * @return connection      * @throws Exception      */
specifier|protected
name|Connection
name|createConnection
parameter_list|()
throws|throws
name|Exception
block|{
name|ActiveMQConnectionFactory
name|factory
init|=
operator|new
name|ActiveMQConnectionFactory
argument_list|(
literal|"vm://localhost?broker.persistent=false"
argument_list|)
decl_stmt|;
return|return
name|factory
operator|.
name|createConnection
argument_list|()
return|;
block|}
comment|/**      * Tests if a message unacknowledged message gets to be resent when the      * session is closed and then a new consumer session is created.      *       */
specifier|public
name|void
name|testQueueSessionCloseMarksMessageRedelivered
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
name|CLIENT_ACKNOWLEDGE
argument_list|)
decl_stmt|;
name|Queue
name|queue
init|=
name|session
operator|.
name|createQueue
argument_list|(
literal|"queue-"
operator|+
name|getName
argument_list|()
argument_list|)
decl_stmt|;
name|MessageProducer
name|producer
init|=
name|createProducer
argument_list|(
name|session
argument_list|,
name|queue
argument_list|)
decl_stmt|;
name|producer
operator|.
name|send
argument_list|(
name|createTextMessage
argument_list|(
name|session
argument_list|)
argument_list|)
expr_stmt|;
comment|// Consume the message...
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
name|msg
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
name|msg
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
literal|"Message should not be redelivered."
argument_list|,
name|msg
operator|.
name|getJMSRedelivered
argument_list|()
argument_list|)
expr_stmt|;
comment|// Don't ack the message.
comment|// Reset the session. This should cause the Unacked message to be
comment|// redelivered.
name|session
operator|.
name|close
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
name|Session
operator|.
name|CLIENT_ACKNOWLEDGE
argument_list|)
expr_stmt|;
comment|// Attempt to Consume the message...
name|consumer
operator|=
name|session
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
literal|2000
argument_list|)
expr_stmt|;
name|assertNotNull
argument_list|(
name|msg
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
literal|"Message should be redelivered."
argument_list|,
name|msg
operator|.
name|getJMSRedelivered
argument_list|()
argument_list|)
expr_stmt|;
name|msg
operator|.
name|acknowledge
argument_list|()
expr_stmt|;
name|session
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
specifier|public
name|void
name|testQueueSessionCloseMarksUnAckedMessageRedelivered
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
name|CLIENT_ACKNOWLEDGE
argument_list|)
decl_stmt|;
name|Queue
name|queue
init|=
name|session
operator|.
name|createQueue
argument_list|(
literal|"queue-"
operator|+
name|getName
argument_list|()
argument_list|)
decl_stmt|;
name|MessageProducer
name|producer
init|=
name|createProducer
argument_list|(
name|session
argument_list|,
name|queue
argument_list|)
decl_stmt|;
name|producer
operator|.
name|send
argument_list|(
name|createTextMessage
argument_list|(
name|session
argument_list|,
literal|"1"
argument_list|)
argument_list|)
expr_stmt|;
name|producer
operator|.
name|send
argument_list|(
name|createTextMessage
argument_list|(
name|session
argument_list|,
literal|"2"
argument_list|)
argument_list|)
expr_stmt|;
comment|// Consume the message...
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
name|msg
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
name|msg
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
literal|"Message should not be redelivered."
argument_list|,
name|msg
operator|.
name|getJMSRedelivered
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"1"
argument_list|,
operator|(
operator|(
name|TextMessage
operator|)
name|msg
operator|)
operator|.
name|getText
argument_list|()
argument_list|)
expr_stmt|;
name|msg
operator|.
name|acknowledge
argument_list|()
expr_stmt|;
comment|// Don't ack the message.
name|msg
operator|=
name|consumer
operator|.
name|receive
argument_list|(
literal|1000
argument_list|)
expr_stmt|;
name|assertNotNull
argument_list|(
name|msg
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
literal|"Message should not be redelivered."
argument_list|,
name|msg
operator|.
name|getJMSRedelivered
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"2"
argument_list|,
operator|(
operator|(
name|TextMessage
operator|)
name|msg
operator|)
operator|.
name|getText
argument_list|()
argument_list|)
expr_stmt|;
comment|// Reset the session. This should cause the Unacked message to be
comment|// redelivered.
name|session
operator|.
name|close
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
name|Session
operator|.
name|CLIENT_ACKNOWLEDGE
argument_list|)
expr_stmt|;
comment|// Attempt to Consume the message...
name|consumer
operator|=
name|session
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
literal|2000
argument_list|)
expr_stmt|;
name|assertNotNull
argument_list|(
name|msg
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"2"
argument_list|,
operator|(
operator|(
name|TextMessage
operator|)
name|msg
operator|)
operator|.
name|getText
argument_list|()
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
literal|"Message should be redelivered."
argument_list|,
name|msg
operator|.
name|getJMSRedelivered
argument_list|()
argument_list|)
expr_stmt|;
name|msg
operator|.
name|acknowledge
argument_list|()
expr_stmt|;
name|session
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
comment|/**      * Tests session recovery and that the redelivered message is marked as      * such. Session uses client acknowledgement, the destination is a queue.      *       * @throws JMSException      */
specifier|public
name|void
name|testQueueRecoverMarksMessageRedelivered
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
name|CLIENT_ACKNOWLEDGE
argument_list|)
decl_stmt|;
name|Queue
name|queue
init|=
name|session
operator|.
name|createQueue
argument_list|(
literal|"queue-"
operator|+
name|getName
argument_list|()
argument_list|)
decl_stmt|;
name|MessageProducer
name|producer
init|=
name|createProducer
argument_list|(
name|session
argument_list|,
name|queue
argument_list|)
decl_stmt|;
name|producer
operator|.
name|send
argument_list|(
name|createTextMessage
argument_list|(
name|session
argument_list|)
argument_list|)
expr_stmt|;
comment|// Consume the message...
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
name|msg
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
name|msg
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
literal|"Message should not be redelivered."
argument_list|,
name|msg
operator|.
name|getJMSRedelivered
argument_list|()
argument_list|)
expr_stmt|;
comment|// Don't ack the message.
comment|// Reset the session. This should cause the Unacked message to be
comment|// redelivered.
name|session
operator|.
name|recover
argument_list|()
expr_stmt|;
comment|// Attempt to Consume the message...
name|msg
operator|=
name|consumer
operator|.
name|receive
argument_list|(
literal|2000
argument_list|)
expr_stmt|;
name|assertNotNull
argument_list|(
name|msg
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
literal|"Message should be redelivered."
argument_list|,
name|msg
operator|.
name|getJMSRedelivered
argument_list|()
argument_list|)
expr_stmt|;
name|msg
operator|.
name|acknowledge
argument_list|()
expr_stmt|;
name|session
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
comment|/**      * Tests rollback message to be marked as redelivered. Session uses client      * acknowledgement and the destination is a queue.      *       * @throws JMSException      */
specifier|public
name|void
name|testQueueRollbackMarksMessageRedelivered
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
name|session
init|=
name|connection
operator|.
name|createSession
argument_list|(
literal|true
argument_list|,
name|Session
operator|.
name|CLIENT_ACKNOWLEDGE
argument_list|)
decl_stmt|;
name|Queue
name|queue
init|=
name|session
operator|.
name|createQueue
argument_list|(
literal|"queue-"
operator|+
name|getName
argument_list|()
argument_list|)
decl_stmt|;
name|MessageProducer
name|producer
init|=
name|createProducer
argument_list|(
name|session
argument_list|,
name|queue
argument_list|)
decl_stmt|;
name|producer
operator|.
name|send
argument_list|(
name|createTextMessage
argument_list|(
name|session
argument_list|)
argument_list|)
expr_stmt|;
name|session
operator|.
name|commit
argument_list|()
expr_stmt|;
comment|// Get the message... Should not be redelivered.
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
name|msg
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
name|msg
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
literal|"Message should not be redelivered."
argument_list|,
name|msg
operator|.
name|getJMSRedelivered
argument_list|()
argument_list|)
expr_stmt|;
comment|// Rollback.. should cause redelivery.
name|session
operator|.
name|rollback
argument_list|()
expr_stmt|;
comment|// Attempt to Consume the message...
name|msg
operator|=
name|consumer
operator|.
name|receive
argument_list|(
literal|2000
argument_list|)
expr_stmt|;
name|assertNotNull
argument_list|(
name|msg
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
literal|"Message should be redelivered."
argument_list|,
name|msg
operator|.
name|getJMSRedelivered
argument_list|()
argument_list|)
expr_stmt|;
name|session
operator|.
name|commit
argument_list|()
expr_stmt|;
name|session
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
comment|/**      * Tests if the message gets to be re-delivered when the session closes and      * that the re-delivered message is marked as such. Session uses client      * acknowledgment, the destination is a topic and the consumer is a durable      * subscriber.      *       * @throws JMSException      */
specifier|public
name|void
name|testDurableTopicSessionCloseMarksMessageRedelivered
parameter_list|()
throws|throws
name|JMSException
block|{
name|connection
operator|.
name|setClientID
argument_list|(
name|getName
argument_list|()
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
name|CLIENT_ACKNOWLEDGE
argument_list|)
decl_stmt|;
name|Topic
name|topic
init|=
name|session
operator|.
name|createTopic
argument_list|(
literal|"topic-"
operator|+
name|getName
argument_list|()
argument_list|)
decl_stmt|;
name|MessageConsumer
name|consumer
init|=
name|session
operator|.
name|createDurableSubscriber
argument_list|(
name|topic
argument_list|,
literal|"sub1"
argument_list|)
decl_stmt|;
comment|// This case only works with persistent messages since transient
comment|// messages
comment|// are dropped when the consumer goes offline.
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
name|setDeliveryMode
argument_list|(
name|DeliveryMode
operator|.
name|PERSISTENT
argument_list|)
expr_stmt|;
name|producer
operator|.
name|send
argument_list|(
name|createTextMessage
argument_list|(
name|session
argument_list|)
argument_list|)
expr_stmt|;
comment|// Consume the message...
name|Message
name|msg
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
name|msg
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
literal|"Message should not be re-delivered."
argument_list|,
name|msg
operator|.
name|getJMSRedelivered
argument_list|()
argument_list|)
expr_stmt|;
comment|// Don't ack the message.
comment|// Reset the session. This should cause the Unacked message to be
comment|// re-delivered.
name|session
operator|.
name|close
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
name|Session
operator|.
name|CLIENT_ACKNOWLEDGE
argument_list|)
expr_stmt|;
comment|// Attempt to Consume the message...
name|consumer
operator|=
name|session
operator|.
name|createDurableSubscriber
argument_list|(
name|topic
argument_list|,
literal|"sub1"
argument_list|)
expr_stmt|;
name|msg
operator|=
name|consumer
operator|.
name|receive
argument_list|(
literal|2000
argument_list|)
expr_stmt|;
name|assertNotNull
argument_list|(
name|msg
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
literal|"Message should be redelivered."
argument_list|,
name|msg
operator|.
name|getJMSRedelivered
argument_list|()
argument_list|)
expr_stmt|;
name|msg
operator|.
name|acknowledge
argument_list|()
expr_stmt|;
name|session
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
comment|/**      * Tests session recovery and that the redelivered message is marked as      * such. Session uses client acknowledgement, the destination is a topic and      * the consumer is a durable suscriber.      *       * @throws JMSException      */
specifier|public
name|void
name|testDurableTopicRecoverMarksMessageRedelivered
parameter_list|()
throws|throws
name|JMSException
block|{
name|connection
operator|.
name|setClientID
argument_list|(
name|getName
argument_list|()
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
name|CLIENT_ACKNOWLEDGE
argument_list|)
decl_stmt|;
name|Topic
name|topic
init|=
name|session
operator|.
name|createTopic
argument_list|(
literal|"topic-"
operator|+
name|getName
argument_list|()
argument_list|)
decl_stmt|;
name|MessageConsumer
name|consumer
init|=
name|session
operator|.
name|createDurableSubscriber
argument_list|(
name|topic
argument_list|,
literal|"sub1"
argument_list|)
decl_stmt|;
name|MessageProducer
name|producer
init|=
name|createProducer
argument_list|(
name|session
argument_list|,
name|topic
argument_list|)
decl_stmt|;
name|producer
operator|.
name|send
argument_list|(
name|createTextMessage
argument_list|(
name|session
argument_list|)
argument_list|)
expr_stmt|;
comment|// Consume the message...
name|Message
name|msg
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
name|msg
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
literal|"Message should not be redelivered."
argument_list|,
name|msg
operator|.
name|getJMSRedelivered
argument_list|()
argument_list|)
expr_stmt|;
comment|// Don't ack the message.
comment|// Reset the session. This should cause the Unacked message to be
comment|// redelivered.
name|session
operator|.
name|recover
argument_list|()
expr_stmt|;
comment|// Attempt to Consume the message...
name|msg
operator|=
name|consumer
operator|.
name|receive
argument_list|(
literal|2000
argument_list|)
expr_stmt|;
name|assertNotNull
argument_list|(
name|msg
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
literal|"Message should be redelivered."
argument_list|,
name|msg
operator|.
name|getJMSRedelivered
argument_list|()
argument_list|)
expr_stmt|;
name|msg
operator|.
name|acknowledge
argument_list|()
expr_stmt|;
name|session
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
comment|/**      * Tests rollback message to be marked as redelivered. Session uses client      * acknowledgement and the destination is a topic.      *       * @throws JMSException      */
specifier|public
name|void
name|testDurableTopicRollbackMarksMessageRedelivered
parameter_list|()
throws|throws
name|JMSException
block|{
name|connection
operator|.
name|setClientID
argument_list|(
name|getName
argument_list|()
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
literal|true
argument_list|,
name|Session
operator|.
name|CLIENT_ACKNOWLEDGE
argument_list|)
decl_stmt|;
name|Topic
name|topic
init|=
name|session
operator|.
name|createTopic
argument_list|(
literal|"topic-"
operator|+
name|getName
argument_list|()
argument_list|)
decl_stmt|;
name|MessageConsumer
name|consumer
init|=
name|session
operator|.
name|createDurableSubscriber
argument_list|(
name|topic
argument_list|,
literal|"sub1"
argument_list|)
decl_stmt|;
name|MessageProducer
name|producer
init|=
name|createProducer
argument_list|(
name|session
argument_list|,
name|topic
argument_list|)
decl_stmt|;
name|producer
operator|.
name|send
argument_list|(
name|createTextMessage
argument_list|(
name|session
argument_list|)
argument_list|)
expr_stmt|;
name|session
operator|.
name|commit
argument_list|()
expr_stmt|;
comment|// Get the message... Should not be redelivered.
name|Message
name|msg
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
name|msg
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
literal|"Message should not be redelivered."
argument_list|,
name|msg
operator|.
name|getJMSRedelivered
argument_list|()
argument_list|)
expr_stmt|;
comment|// Rollback.. should cause redelivery.
name|session
operator|.
name|rollback
argument_list|()
expr_stmt|;
comment|// Attempt to Consume the message...
name|msg
operator|=
name|consumer
operator|.
name|receive
argument_list|(
literal|2000
argument_list|)
expr_stmt|;
name|assertNotNull
argument_list|(
name|msg
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
literal|"Message should be redelivered."
argument_list|,
name|msg
operator|.
name|getJMSRedelivered
argument_list|()
argument_list|)
expr_stmt|;
name|session
operator|.
name|commit
argument_list|()
expr_stmt|;
name|session
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
comment|/**      *       *       * @throws JMSException      */
specifier|public
name|void
name|testTopicRecoverMarksMessageRedelivered
parameter_list|()
throws|throws
name|JMSException
block|{
name|connection
operator|.
name|setClientID
argument_list|(
name|getName
argument_list|()
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
name|CLIENT_ACKNOWLEDGE
argument_list|)
decl_stmt|;
name|Topic
name|topic
init|=
name|session
operator|.
name|createTopic
argument_list|(
literal|"topic-"
operator|+
name|getName
argument_list|()
argument_list|)
decl_stmt|;
name|MessageConsumer
name|consumer
init|=
name|session
operator|.
name|createConsumer
argument_list|(
name|topic
argument_list|)
decl_stmt|;
name|MessageProducer
name|producer
init|=
name|createProducer
argument_list|(
name|session
argument_list|,
name|topic
argument_list|)
decl_stmt|;
name|producer
operator|.
name|send
argument_list|(
name|createTextMessage
argument_list|(
name|session
argument_list|)
argument_list|)
expr_stmt|;
comment|// Consume the message...
name|Message
name|msg
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
name|msg
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
literal|"Message should not be redelivered."
argument_list|,
name|msg
operator|.
name|getJMSRedelivered
argument_list|()
argument_list|)
expr_stmt|;
comment|// Don't ack the message.
comment|// Reset the session. This should cause the Unacked message to be
comment|// redelivered.
name|session
operator|.
name|recover
argument_list|()
expr_stmt|;
comment|// Attempt to Consume the message...
name|msg
operator|=
name|consumer
operator|.
name|receive
argument_list|(
literal|2000
argument_list|)
expr_stmt|;
name|assertNotNull
argument_list|(
name|msg
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
literal|"Message should be redelivered."
argument_list|,
name|msg
operator|.
name|getJMSRedelivered
argument_list|()
argument_list|)
expr_stmt|;
name|msg
operator|.
name|acknowledge
argument_list|()
expr_stmt|;
name|session
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
comment|/**      * Tests rollback message to be marked as redelivered. Session uses client      * acknowledgement and the destination is a topic.      *       * @throws JMSException      */
specifier|public
name|void
name|testTopicRollbackMarksMessageRedelivered
parameter_list|()
throws|throws
name|JMSException
block|{
name|connection
operator|.
name|setClientID
argument_list|(
name|getName
argument_list|()
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
literal|true
argument_list|,
name|Session
operator|.
name|CLIENT_ACKNOWLEDGE
argument_list|)
decl_stmt|;
name|Topic
name|topic
init|=
name|session
operator|.
name|createTopic
argument_list|(
literal|"topic-"
operator|+
name|getName
argument_list|()
argument_list|)
decl_stmt|;
name|MessageConsumer
name|consumer
init|=
name|session
operator|.
name|createConsumer
argument_list|(
name|topic
argument_list|)
decl_stmt|;
name|MessageProducer
name|producer
init|=
name|createProducer
argument_list|(
name|session
argument_list|,
name|topic
argument_list|)
decl_stmt|;
name|producer
operator|.
name|send
argument_list|(
name|createTextMessage
argument_list|(
name|session
argument_list|)
argument_list|)
expr_stmt|;
name|session
operator|.
name|commit
argument_list|()
expr_stmt|;
comment|// Get the message... Should not be redelivered.
name|Message
name|msg
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
name|msg
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
literal|"Message should not be redelivered."
argument_list|,
name|msg
operator|.
name|getJMSRedelivered
argument_list|()
argument_list|)
expr_stmt|;
comment|// Rollback.. should cause redelivery.
name|session
operator|.
name|rollback
argument_list|()
expr_stmt|;
comment|// Attempt to Consume the message...
name|msg
operator|=
name|consumer
operator|.
name|receive
argument_list|(
literal|2000
argument_list|)
expr_stmt|;
name|assertNotNull
argument_list|(
name|msg
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
literal|"Message should be redelivered."
argument_list|,
name|msg
operator|.
name|getJMSRedelivered
argument_list|()
argument_list|)
expr_stmt|;
name|session
operator|.
name|commit
argument_list|()
expr_stmt|;
name|session
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
specifier|public
name|void
name|testNoReceiveConsumerDoesNotIncrementRedelivery
parameter_list|()
throws|throws
name|Exception
block|{
name|connection
operator|.
name|setClientID
argument_list|(
name|getName
argument_list|()
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
literal|true
argument_list|,
name|Session
operator|.
name|CLIENT_ACKNOWLEDGE
argument_list|)
decl_stmt|;
name|Queue
name|queue
init|=
name|session
operator|.
name|createQueue
argument_list|(
literal|"queue-"
operator|+
name|getName
argument_list|()
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
name|MessageProducer
name|producer
init|=
name|createProducer
argument_list|(
name|session
argument_list|,
name|queue
argument_list|)
decl_stmt|;
name|producer
operator|.
name|send
argument_list|(
name|createTextMessage
argument_list|(
name|session
argument_list|)
argument_list|)
expr_stmt|;
name|session
operator|.
name|commit
argument_list|()
expr_stmt|;
name|TimeUnit
operator|.
name|SECONDS
operator|.
name|sleep
argument_list|(
literal|1
argument_list|)
expr_stmt|;
name|consumer
operator|.
name|close
argument_list|()
expr_stmt|;
name|consumer
operator|=
name|session
operator|.
name|createConsumer
argument_list|(
name|queue
argument_list|)
expr_stmt|;
name|Message
name|msg
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
name|msg
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
literal|"Message should not be redelivered."
argument_list|,
name|msg
operator|.
name|getJMSRedelivered
argument_list|()
argument_list|)
expr_stmt|;
name|session
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
specifier|public
name|void
name|testNoReceiveDurableConsumerDoesNotIncrementRedelivery
parameter_list|()
throws|throws
name|Exception
block|{
name|connection
operator|.
name|setClientID
argument_list|(
name|getName
argument_list|()
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
literal|true
argument_list|,
name|Session
operator|.
name|CLIENT_ACKNOWLEDGE
argument_list|)
decl_stmt|;
name|Topic
name|topic
init|=
name|session
operator|.
name|createTopic
argument_list|(
literal|"topic-"
operator|+
name|getName
argument_list|()
argument_list|)
decl_stmt|;
name|MessageConsumer
name|consumer
init|=
name|session
operator|.
name|createDurableSubscriber
argument_list|(
name|topic
argument_list|,
literal|"sub"
argument_list|)
decl_stmt|;
name|MessageProducer
name|producer
init|=
name|createProducer
argument_list|(
name|session
argument_list|,
name|topic
argument_list|)
decl_stmt|;
name|producer
operator|.
name|send
argument_list|(
name|createTextMessage
argument_list|(
name|session
argument_list|)
argument_list|)
expr_stmt|;
name|session
operator|.
name|commit
argument_list|()
expr_stmt|;
name|TimeUnit
operator|.
name|SECONDS
operator|.
name|sleep
argument_list|(
literal|1
argument_list|)
expr_stmt|;
name|consumer
operator|.
name|close
argument_list|()
expr_stmt|;
name|consumer
operator|=
name|session
operator|.
name|createDurableSubscriber
argument_list|(
name|topic
argument_list|,
literal|"sub"
argument_list|)
expr_stmt|;
name|Message
name|msg
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
name|msg
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
literal|"Message should not be redelivered."
argument_list|,
name|msg
operator|.
name|getJMSRedelivered
argument_list|()
argument_list|)
expr_stmt|;
name|session
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
comment|/**      * Creates a text message.      *       * @param session      * @return TextMessage.      * @throws JMSException      */
specifier|private
name|TextMessage
name|createTextMessage
parameter_list|(
name|Session
name|session
parameter_list|)
throws|throws
name|JMSException
block|{
return|return
name|createTextMessage
argument_list|(
name|session
argument_list|,
literal|"Hello"
argument_list|)
return|;
block|}
specifier|private
name|TextMessage
name|createTextMessage
parameter_list|(
name|Session
name|session
parameter_list|,
name|String
name|txt
parameter_list|)
throws|throws
name|JMSException
block|{
return|return
name|session
operator|.
name|createTextMessage
argument_list|(
name|txt
argument_list|)
return|;
block|}
comment|/**      * Creates a producer.      *       * @param session      * @param queue - destination.      * @return MessageProducer      * @throws JMSException      */
specifier|private
name|MessageProducer
name|createProducer
parameter_list|(
name|Session
name|session
parameter_list|,
name|Destination
name|queue
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
name|queue
argument_list|)
decl_stmt|;
name|producer
operator|.
name|setDeliveryMode
argument_list|(
name|getDeliveryMode
argument_list|()
argument_list|)
expr_stmt|;
return|return
name|producer
return|;
block|}
comment|/**      * Returns delivery mode.      *       * @return int - persistent delivery mode.      */
specifier|protected
name|int
name|getDeliveryMode
parameter_list|()
block|{
return|return
name|DeliveryMode
operator|.
name|PERSISTENT
return|;
block|}
comment|/**      * Run the JmsRedeliverTest with the delivery mode set as persistent.      */
specifier|public
specifier|static
specifier|final
class|class
name|PersistentCase
extends|extends
name|JmsRedeliveredTest
block|{
comment|/**          * Returns delivery mode.          *           * @return int - persistent delivery mode.          */
specifier|protected
name|int
name|getDeliveryMode
parameter_list|()
block|{
return|return
name|DeliveryMode
operator|.
name|PERSISTENT
return|;
block|}
block|}
comment|/**      * Run the JmsRedeliverTest with the delivery mode set as non-persistent.      */
specifier|public
specifier|static
specifier|final
class|class
name|TransientCase
extends|extends
name|JmsRedeliveredTest
block|{
comment|/**          * Returns delivery mode.          *           * @return int - non-persistent delivery mode.          */
specifier|protected
name|int
name|getDeliveryMode
parameter_list|()
block|{
return|return
name|DeliveryMode
operator|.
name|NON_PERSISTENT
return|;
block|}
block|}
specifier|public
specifier|static
name|Test
name|suite
parameter_list|()
block|{
name|TestSuite
name|suite
init|=
operator|new
name|TestSuite
argument_list|()
decl_stmt|;
name|suite
operator|.
name|addTestSuite
argument_list|(
name|PersistentCase
operator|.
name|class
argument_list|)
expr_stmt|;
name|suite
operator|.
name|addTestSuite
argument_list|(
name|TransientCase
operator|.
name|class
argument_list|)
expr_stmt|;
return|return
name|suite
return|;
block|}
block|}
end_class

end_unit

