begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/** *<a href="http://activemq.org">ActiveMQ: The Open Source Message Fabric</a> * * Copyright 2005 (C) LogicBlaze, Inc. http://www.logicblaze.com * * Licensed under the Apache License, Version 2.0 (the "License"); * you may not use this file except in compliance with the License. * You may obtain a copy of the License at * * http://www.apache.org/licenses/LICENSE-2.0 * * Unless required by applicable law or agreed to in writing, software * distributed under the License is distributed on an "AS IS" BASIS, * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. * See the License for the specific language governing permissions and * limitations under the License. * **/
end_comment

begin_package
package|package
name|org
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

begin_comment
comment|/**  * @version $Revision: 1.3 $  */
end_comment

begin_class
specifier|public
class|class
name|JmsClientAckListenerTest
extends|extends
name|TestSupport
implements|implements
name|MessageListener
block|{
specifier|private
name|Connection
name|connection
decl_stmt|;
specifier|private
name|boolean
name|dontAck
init|=
literal|false
decl_stmt|;
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
comment|/**      * Tests if acknowleged messages are being consumed.      *       * @throws javax.jms.JMSException      */
specifier|public
name|void
name|testAckedMessageAreConsumed
parameter_list|()
throws|throws
name|Exception
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
literal|"test"
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
name|consumer
operator|.
name|setMessageListener
argument_list|(
name|this
argument_list|)
expr_stmt|;
name|Thread
operator|.
name|sleep
argument_list|(
literal|10000
argument_list|)
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
comment|/**      * Tests if unacknowleged messages are being redelivered when the consumer connects again.      *       * @throws javax.jms.JMSException      */
specifier|public
name|void
name|testUnAckedMessageAreNotConsumedOnSessionClose
parameter_list|()
throws|throws
name|Exception
block|{
name|connection
operator|.
name|start
argument_list|()
expr_stmt|;
comment|//don't aknowledge message on onMessage() call
name|dontAck
operator|=
literal|true
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
literal|"test"
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
name|consumer
operator|.
name|setMessageListener
argument_list|(
name|this
argument_list|)
expr_stmt|;
comment|// Don't ack the message.
comment|// Reset the session.  This should cause the Unacked message to be redelivered.
name|session
operator|.
name|close
argument_list|()
expr_stmt|;
name|Thread
operator|.
name|sleep
argument_list|(
literal|10000
argument_list|)
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
name|Message
name|msg
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
specifier|public
name|void
name|onMessage
parameter_list|(
name|Message
name|message
parameter_list|)
block|{
name|assertNotNull
argument_list|(
name|message
argument_list|)
expr_stmt|;
if|if
condition|(
operator|!
name|dontAck
condition|)
block|{
try|try
block|{
name|message
operator|.
name|acknowledge
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
name|e
operator|.
name|printStackTrace
argument_list|()
expr_stmt|;
block|}
block|}
block|}
block|}
end_class

end_unit

