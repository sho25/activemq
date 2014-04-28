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

begin_comment
comment|/**  *  */
end_comment

begin_class
specifier|public
class|class
name|JMSIndividualAckTest
extends|extends
name|TestSupport
block|{
specifier|private
name|Connection
name|connection
decl_stmt|;
annotation|@
name|Override
specifier|protected
name|void
name|setUp
parameter_list|()
throws|throws
name|Exception
block|{
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
block|}
comment|/**      * @see junit.framework.TestCase#tearDown()      */
annotation|@
name|Override
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
name|super
operator|.
name|tearDown
argument_list|()
expr_stmt|;
block|}
comment|/**      * Tests if acknowledged messages are being consumed.      *      * @throws JMSException      */
specifier|public
name|void
name|testAckedMessageAreConsumed
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
name|ActiveMQSession
operator|.
name|INDIVIDUAL_ACKNOWLEDGE
argument_list|)
decl_stmt|;
name|Queue
name|queue
init|=
name|session
operator|.
name|createQueue
argument_list|(
name|getQueueName
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
name|producer
operator|.
name|send
argument_list|(
name|session
operator|.
name|createTextMessage
argument_list|(
literal|"Hello"
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
name|msg
operator|.
name|acknowledge
argument_list|()
expr_stmt|;
comment|// Reset the session.
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
name|ActiveMQSession
operator|.
name|INDIVIDUAL_ACKNOWLEDGE
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
literal|1000
argument_list|)
expr_stmt|;
name|assertNull
argument_list|(
name|msg
argument_list|)
expr_stmt|;
name|session
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
comment|/**      * Tests if acknowledged messages are being consumed.      *      * @throws JMSException      */
specifier|public
name|void
name|testLastMessageAcked
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
name|ActiveMQSession
operator|.
name|INDIVIDUAL_ACKNOWLEDGE
argument_list|)
decl_stmt|;
name|Queue
name|queue
init|=
name|session
operator|.
name|createQueue
argument_list|(
name|getQueueName
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
name|msg1
init|=
name|session
operator|.
name|createTextMessage
argument_list|(
literal|"msg1"
argument_list|)
decl_stmt|;
name|TextMessage
name|msg2
init|=
name|session
operator|.
name|createTextMessage
argument_list|(
literal|"msg2"
argument_list|)
decl_stmt|;
name|TextMessage
name|msg3
init|=
name|session
operator|.
name|createTextMessage
argument_list|(
literal|"msg3"
argument_list|)
decl_stmt|;
name|producer
operator|.
name|send
argument_list|(
name|msg1
argument_list|)
expr_stmt|;
name|producer
operator|.
name|send
argument_list|(
name|msg2
argument_list|)
expr_stmt|;
name|producer
operator|.
name|send
argument_list|(
name|msg3
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
name|msg
operator|.
name|acknowledge
argument_list|()
expr_stmt|;
comment|// Reset the session.
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
name|ActiveMQSession
operator|.
name|INDIVIDUAL_ACKNOWLEDGE
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
literal|1000
argument_list|)
expr_stmt|;
name|assertNotNull
argument_list|(
name|msg
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|msg1
argument_list|,
name|msg
argument_list|)
expr_stmt|;
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
name|assertEquals
argument_list|(
name|msg2
argument_list|,
name|msg
argument_list|)
expr_stmt|;
name|msg
operator|=
name|consumer
operator|.
name|receive
argument_list|(
literal|1000
argument_list|)
expr_stmt|;
name|assertNull
argument_list|(
name|msg
argument_list|)
expr_stmt|;
name|session
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
comment|/**      * Tests if unacknowledged messages are being re-delivered when the consumer connects again.      *      * @throws JMSException      */
specifier|public
name|void
name|testUnAckedMessageAreNotConsumedOnSessionClose
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
name|ActiveMQSession
operator|.
name|INDIVIDUAL_ACKNOWLEDGE
argument_list|)
decl_stmt|;
name|Queue
name|queue
init|=
name|session
operator|.
name|createQueue
argument_list|(
name|getQueueName
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
name|producer
operator|.
name|send
argument_list|(
name|session
operator|.
name|createTextMessage
argument_list|(
literal|"Hello"
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
comment|// Don't ack the message.
comment|// Reset the session.  This should cause the unacknowledged message to be re-delivered.
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
name|ActiveMQSession
operator|.
name|INDIVIDUAL_ACKNOWLEDGE
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
specifier|protected
name|String
name|getQueueName
parameter_list|()
block|{
return|return
name|getClass
argument_list|()
operator|.
name|getName
argument_list|()
operator|+
literal|"."
operator|+
name|getName
argument_list|()
return|;
block|}
block|}
end_class

end_unit

