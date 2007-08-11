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
name|List
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
name|Connection
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
name|test
operator|.
name|TestSupport
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|commons
operator|.
name|logging
operator|.
name|Log
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|commons
operator|.
name|logging
operator|.
name|LogFactory
import|;
end_import

begin_comment
comment|/**  * @version $Revision: 1.3 $  */
end_comment

begin_class
specifier|public
class|class
name|JmsTopicRequestReplyTest
extends|extends
name|TestSupport
implements|implements
name|MessageListener
block|{
specifier|private
specifier|static
specifier|final
name|Log
name|LOG
init|=
name|LogFactory
operator|.
name|getLog
argument_list|(
name|JmsTopicRequestReplyTest
operator|.
name|class
argument_list|)
decl_stmt|;
specifier|protected
name|boolean
name|useAsyncConsume
decl_stmt|;
specifier|private
name|Connection
name|serverConnection
decl_stmt|;
specifier|private
name|Connection
name|clientConnection
decl_stmt|;
specifier|private
name|MessageProducer
name|replyProducer
decl_stmt|;
specifier|private
name|Session
name|serverSession
decl_stmt|;
specifier|private
name|Destination
name|requestDestination
decl_stmt|;
specifier|private
name|List
argument_list|<
name|JMSException
argument_list|>
name|failures
init|=
operator|new
name|Vector
argument_list|<
name|JMSException
argument_list|>
argument_list|()
decl_stmt|;
specifier|private
name|boolean
name|dynamicallyCreateProducer
decl_stmt|;
specifier|private
name|String
name|clientSideClientID
decl_stmt|;
specifier|public
name|void
name|testSendAndReceive
parameter_list|()
throws|throws
name|Exception
block|{
name|clientConnection
operator|=
name|createConnection
argument_list|()
expr_stmt|;
name|clientConnection
operator|.
name|setClientID
argument_list|(
literal|"ClientConnection:"
operator|+
name|getSubject
argument_list|()
argument_list|)
expr_stmt|;
name|Session
name|session
init|=
name|clientConnection
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
name|clientConnection
operator|.
name|start
argument_list|()
expr_stmt|;
name|Destination
name|replyDestination
init|=
name|createTemporaryDestination
argument_list|(
name|session
argument_list|)
decl_stmt|;
comment|// lets test the destination
name|clientSideClientID
operator|=
name|clientConnection
operator|.
name|getClientID
argument_list|()
expr_stmt|;
comment|// TODO
comment|// String value = ActiveMQDestination.getClientId((ActiveMQDestination)
comment|// replyDestination);
comment|// assertEquals("clientID from the temporary destination must be the
comment|// same", clientSideClientID, value);
name|LOG
operator|.
name|info
argument_list|(
literal|"Both the clientID and destination clientID match properly: "
operator|+
name|clientSideClientID
argument_list|)
expr_stmt|;
comment|/* build queues */
name|MessageProducer
name|requestProducer
init|=
name|session
operator|.
name|createProducer
argument_list|(
name|requestDestination
argument_list|)
decl_stmt|;
name|MessageConsumer
name|replyConsumer
init|=
name|session
operator|.
name|createConsumer
argument_list|(
name|replyDestination
argument_list|)
decl_stmt|;
comment|/* build requestmessage */
name|TextMessage
name|requestMessage
init|=
name|session
operator|.
name|createTextMessage
argument_list|(
literal|"Olivier"
argument_list|)
decl_stmt|;
name|requestMessage
operator|.
name|setJMSReplyTo
argument_list|(
name|replyDestination
argument_list|)
expr_stmt|;
name|requestProducer
operator|.
name|send
argument_list|(
name|requestMessage
argument_list|)
expr_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"Sent request."
argument_list|)
expr_stmt|;
name|LOG
operator|.
name|info
argument_list|(
name|requestMessage
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|Message
name|msg
init|=
name|replyConsumer
operator|.
name|receive
argument_list|(
literal|5000
argument_list|)
decl_stmt|;
if|if
condition|(
name|msg
operator|instanceof
name|TextMessage
condition|)
block|{
name|TextMessage
name|replyMessage
init|=
operator|(
name|TextMessage
operator|)
name|msg
decl_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"Received reply."
argument_list|)
expr_stmt|;
name|LOG
operator|.
name|info
argument_list|(
name|replyMessage
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"Wrong message content"
argument_list|,
literal|"Hello: Olivier"
argument_list|,
name|replyMessage
operator|.
name|getText
argument_list|()
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|fail
argument_list|(
literal|"Should have received a reply by now"
argument_list|)
expr_stmt|;
block|}
name|assertEquals
argument_list|(
literal|"Should not have had any failures: "
operator|+
name|failures
argument_list|,
literal|0
argument_list|,
name|failures
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|testSendAndReceiveWithDynamicallyCreatedProducer
parameter_list|()
throws|throws
name|Exception
block|{
name|dynamicallyCreateProducer
operator|=
literal|true
expr_stmt|;
name|testSendAndReceive
argument_list|()
expr_stmt|;
block|}
comment|/**      * Use the asynchronous subscription mechanism      */
specifier|public
name|void
name|onMessage
parameter_list|(
name|Message
name|message
parameter_list|)
block|{
try|try
block|{
name|TextMessage
name|requestMessage
init|=
operator|(
name|TextMessage
operator|)
name|message
decl_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"Received request."
argument_list|)
expr_stmt|;
name|LOG
operator|.
name|info
argument_list|(
name|requestMessage
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|Destination
name|replyDestination
init|=
name|requestMessage
operator|.
name|getJMSReplyTo
argument_list|()
decl_stmt|;
comment|// TODO
comment|// String value =
comment|// ActiveMQDestination.getClientId((ActiveMQDestination)
comment|// replyDestination);
comment|// assertEquals("clientID from the temporary destination must be the
comment|// same", clientSideClientID, value);
name|TextMessage
name|replyMessage
init|=
name|serverSession
operator|.
name|createTextMessage
argument_list|(
literal|"Hello: "
operator|+
name|requestMessage
operator|.
name|getText
argument_list|()
argument_list|)
decl_stmt|;
name|replyMessage
operator|.
name|setJMSCorrelationID
argument_list|(
name|requestMessage
operator|.
name|getJMSMessageID
argument_list|()
argument_list|)
expr_stmt|;
if|if
condition|(
name|dynamicallyCreateProducer
condition|)
block|{
name|replyProducer
operator|=
name|serverSession
operator|.
name|createProducer
argument_list|(
name|replyDestination
argument_list|)
expr_stmt|;
name|replyProducer
operator|.
name|send
argument_list|(
name|replyMessage
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|replyProducer
operator|.
name|send
argument_list|(
name|replyDestination
argument_list|,
name|replyMessage
argument_list|)
expr_stmt|;
block|}
name|LOG
operator|.
name|info
argument_list|(
literal|"Sent reply."
argument_list|)
expr_stmt|;
name|LOG
operator|.
name|info
argument_list|(
name|replyMessage
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|JMSException
name|e
parameter_list|)
block|{
name|onException
argument_list|(
name|e
argument_list|)
expr_stmt|;
block|}
block|}
comment|/**      * Use the synchronous subscription mechanism      */
specifier|protected
name|void
name|syncConsumeLoop
parameter_list|(
name|MessageConsumer
name|requestConsumer
parameter_list|)
block|{
try|try
block|{
name|Message
name|message
init|=
name|requestConsumer
operator|.
name|receive
argument_list|(
literal|5000
argument_list|)
decl_stmt|;
if|if
condition|(
name|message
operator|!=
literal|null
condition|)
block|{
name|onMessage
argument_list|(
name|message
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|LOG
operator|.
name|error
argument_list|(
literal|"No message received"
argument_list|)
expr_stmt|;
block|}
block|}
catch|catch
parameter_list|(
name|JMSException
name|e
parameter_list|)
block|{
name|onException
argument_list|(
name|e
argument_list|)
expr_stmt|;
block|}
block|}
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
name|serverConnection
operator|=
name|createConnection
argument_list|()
expr_stmt|;
name|serverConnection
operator|.
name|setClientID
argument_list|(
literal|"serverConnection:"
operator|+
name|getSubject
argument_list|()
argument_list|)
expr_stmt|;
name|serverSession
operator|=
name|serverConnection
operator|.
name|createSession
argument_list|(
literal|false
argument_list|,
name|Session
operator|.
name|AUTO_ACKNOWLEDGE
argument_list|)
expr_stmt|;
name|replyProducer
operator|=
name|serverSession
operator|.
name|createProducer
argument_list|(
literal|null
argument_list|)
expr_stmt|;
name|requestDestination
operator|=
name|createDestination
argument_list|(
name|serverSession
argument_list|)
expr_stmt|;
comment|/* build queues */
specifier|final
name|MessageConsumer
name|requestConsumer
init|=
name|serverSession
operator|.
name|createConsumer
argument_list|(
name|requestDestination
argument_list|)
decl_stmt|;
if|if
condition|(
name|useAsyncConsume
condition|)
block|{
name|requestConsumer
operator|.
name|setMessageListener
argument_list|(
name|this
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|Thread
name|thread
init|=
operator|new
name|Thread
argument_list|(
operator|new
name|Runnable
argument_list|()
block|{
specifier|public
name|void
name|run
parameter_list|()
block|{
name|syncConsumeLoop
argument_list|(
name|requestConsumer
argument_list|)
expr_stmt|;
block|}
block|}
argument_list|)
decl_stmt|;
name|thread
operator|.
name|start
argument_list|()
expr_stmt|;
block|}
name|serverConnection
operator|.
name|start
argument_list|()
expr_stmt|;
block|}
specifier|protected
name|void
name|tearDown
parameter_list|()
throws|throws
name|Exception
block|{
name|super
operator|.
name|tearDown
argument_list|()
expr_stmt|;
name|serverConnection
operator|.
name|close
argument_list|()
expr_stmt|;
name|clientConnection
operator|.
name|stop
argument_list|()
expr_stmt|;
name|clientConnection
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
specifier|protected
name|void
name|onException
parameter_list|(
name|JMSException
name|e
parameter_list|)
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"Caught: "
operator|+
name|e
argument_list|)
expr_stmt|;
name|e
operator|.
name|printStackTrace
argument_list|()
expr_stmt|;
name|failures
operator|.
name|add
argument_list|(
name|e
argument_list|)
expr_stmt|;
block|}
specifier|protected
name|Destination
name|createDestination
parameter_list|(
name|Session
name|session
parameter_list|)
throws|throws
name|JMSException
block|{
if|if
condition|(
name|topic
condition|)
block|{
return|return
name|session
operator|.
name|createTopic
argument_list|(
name|getSubject
argument_list|()
argument_list|)
return|;
block|}
return|return
name|session
operator|.
name|createQueue
argument_list|(
name|getSubject
argument_list|()
argument_list|)
return|;
block|}
specifier|protected
name|Destination
name|createTemporaryDestination
parameter_list|(
name|Session
name|session
parameter_list|)
throws|throws
name|JMSException
block|{
if|if
condition|(
name|topic
condition|)
block|{
return|return
name|session
operator|.
name|createTemporaryTopic
argument_list|()
return|;
block|}
return|return
name|session
operator|.
name|createTemporaryQueue
argument_list|()
return|;
block|}
block|}
end_class

end_unit

