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
name|test
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
name|Collections
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Date
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Iterator
import|;
end_import

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
name|junit
operator|.
name|framework
operator|.
name|AssertionFailedError
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
comment|/**  * @version $Revision: 1.2 $  */
end_comment

begin_class
specifier|public
specifier|abstract
class|class
name|JmsSendReceiveTestSupport
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
name|JmsSendReceiveTestSupport
operator|.
name|class
argument_list|)
decl_stmt|;
specifier|protected
name|int
name|messageCount
init|=
literal|100
decl_stmt|;
specifier|protected
name|String
index|[]
name|data
decl_stmt|;
specifier|protected
name|Session
name|session
decl_stmt|;
specifier|protected
name|Session
name|consumeSession
decl_stmt|;
specifier|protected
name|MessageConsumer
name|consumer
decl_stmt|;
specifier|protected
name|MessageProducer
name|producer
decl_stmt|;
specifier|protected
name|Destination
name|consumerDestination
decl_stmt|;
specifier|protected
name|Destination
name|producerDestination
decl_stmt|;
specifier|protected
name|List
argument_list|<
name|Message
argument_list|>
name|messages
init|=
name|createConcurrentList
argument_list|()
decl_stmt|;
specifier|protected
name|boolean
name|topic
init|=
literal|true
decl_stmt|;
specifier|protected
name|boolean
name|durable
decl_stmt|;
specifier|protected
name|int
name|deliveryMode
init|=
name|DeliveryMode
operator|.
name|PERSISTENT
decl_stmt|;
specifier|protected
specifier|final
name|Object
name|lock
init|=
operator|new
name|Object
argument_list|()
decl_stmt|;
specifier|protected
name|boolean
name|verbose
decl_stmt|;
specifier|protected
name|boolean
name|useSeparateSession
decl_stmt|;
specifier|protected
name|boolean
name|largeMessages
decl_stmt|;
specifier|protected
name|int
name|largeMessageLoopSize
init|=
literal|4
operator|*
literal|1024
decl_stmt|;
comment|/*      * @see junit.framework.TestCase#setUp()      */
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
name|String
name|temp
init|=
name|System
operator|.
name|getProperty
argument_list|(
literal|"messageCount"
argument_list|)
decl_stmt|;
if|if
condition|(
name|temp
operator|!=
literal|null
condition|)
block|{
name|int
name|i
init|=
name|Integer
operator|.
name|parseInt
argument_list|(
name|temp
argument_list|)
decl_stmt|;
if|if
condition|(
name|i
operator|>
literal|0
condition|)
block|{
name|messageCount
operator|=
name|i
expr_stmt|;
block|}
block|}
name|LOG
operator|.
name|info
argument_list|(
literal|"Message count for test case is: "
operator|+
name|messageCount
argument_list|)
expr_stmt|;
name|data
operator|=
operator|new
name|String
index|[
name|messageCount
index|]
expr_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|messageCount
condition|;
name|i
operator|++
control|)
block|{
name|data
index|[
name|i
index|]
operator|=
name|createMessageText
argument_list|(
name|i
argument_list|)
expr_stmt|;
block|}
block|}
specifier|protected
name|String
name|createMessageText
parameter_list|(
name|int
name|i
parameter_list|)
block|{
if|if
condition|(
name|largeMessages
condition|)
block|{
return|return
name|createMessageBodyText
argument_list|()
return|;
block|}
else|else
block|{
return|return
literal|"Text for message: "
operator|+
name|i
operator|+
literal|" at "
operator|+
operator|new
name|Date
argument_list|()
return|;
block|}
block|}
specifier|protected
name|String
name|createMessageBodyText
parameter_list|()
block|{
name|StringBuffer
name|buffer
init|=
operator|new
name|StringBuffer
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
name|largeMessageLoopSize
condition|;
name|i
operator|++
control|)
block|{
name|buffer
operator|.
name|append
argument_list|(
literal|"0123456789"
argument_list|)
expr_stmt|;
block|}
return|return
name|buffer
operator|.
name|toString
argument_list|()
return|;
block|}
comment|/**      * Test if all the messages sent are being received.      *       * @throws Exception      */
specifier|public
name|void
name|testSendReceive
parameter_list|()
throws|throws
name|Exception
block|{
name|Thread
operator|.
name|sleep
argument_list|(
literal|1000
argument_list|)
expr_stmt|;
name|messages
operator|.
name|clear
argument_list|()
expr_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|data
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|Message
name|message
init|=
name|createMessage
argument_list|(
name|i
argument_list|)
decl_stmt|;
name|configureMessage
argument_list|(
name|message
argument_list|)
expr_stmt|;
if|if
condition|(
name|verbose
condition|)
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"About to send a message: "
operator|+
name|message
operator|+
literal|" with text: "
operator|+
name|data
index|[
name|i
index|]
argument_list|)
expr_stmt|;
block|}
name|producer
operator|.
name|send
argument_list|(
name|producerDestination
argument_list|,
name|message
argument_list|)
expr_stmt|;
block|}
name|assertMessagesAreReceived
argument_list|()
expr_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|""
operator|+
name|data
operator|.
name|length
operator|+
literal|" messages(s) received, closing down connections"
argument_list|)
expr_stmt|;
block|}
specifier|protected
name|Message
name|createMessage
parameter_list|(
name|int
name|index
parameter_list|)
throws|throws
name|JMSException
block|{
name|Message
name|message
init|=
name|session
operator|.
name|createTextMessage
argument_list|(
name|data
index|[
name|index
index|]
argument_list|)
decl_stmt|;
return|return
name|message
return|;
block|}
comment|/**      * A hook to allow the message to be configured such as adding extra headers      *       * @throws JMSException      */
specifier|protected
name|void
name|configureMessage
parameter_list|(
name|Message
name|message
parameter_list|)
throws|throws
name|JMSException
block|{     }
comment|/**      * Waits to receive the messages and performs the test if all messages have      * been received and are in sequential order.      *       * @throws JMSException      */
specifier|protected
name|void
name|assertMessagesAreReceived
parameter_list|()
throws|throws
name|JMSException
block|{
name|waitForMessagesToBeDelivered
argument_list|()
expr_stmt|;
name|assertMessagesReceivedAreValid
argument_list|(
name|messages
argument_list|)
expr_stmt|;
block|}
comment|/**      * Tests if the messages have all been received and are in sequential order.      *       * @param receivedMessages      * @throws JMSException      */
specifier|protected
name|void
name|assertMessagesReceivedAreValid
parameter_list|(
name|List
argument_list|<
name|Message
argument_list|>
name|receivedMessages
parameter_list|)
throws|throws
name|JMSException
block|{
name|List
argument_list|<
name|Object
argument_list|>
name|copyOfMessages
init|=
name|Arrays
operator|.
name|asList
argument_list|(
name|receivedMessages
operator|.
name|toArray
argument_list|()
argument_list|)
decl_stmt|;
name|int
name|counter
init|=
literal|0
decl_stmt|;
if|if
condition|(
name|data
operator|.
name|length
operator|!=
name|copyOfMessages
operator|.
name|size
argument_list|()
condition|)
block|{
for|for
control|(
name|Iterator
argument_list|<
name|Object
argument_list|>
name|iter
init|=
name|copyOfMessages
operator|.
name|iterator
argument_list|()
init|;
name|iter
operator|.
name|hasNext
argument_list|()
condition|;
control|)
block|{
name|Object
name|message
init|=
name|iter
operator|.
name|next
argument_list|()
decl_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"<== "
operator|+
name|counter
operator|++
operator|+
literal|" = "
operator|+
name|message
argument_list|)
expr_stmt|;
block|}
block|}
name|assertEquals
argument_list|(
literal|"Not enough messages received"
argument_list|,
name|data
operator|.
name|length
argument_list|,
name|receivedMessages
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|data
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|Message
name|received
init|=
name|receivedMessages
operator|.
name|get
argument_list|(
name|i
argument_list|)
decl_stmt|;
try|try
block|{
name|assertMessageValid
argument_list|(
name|i
argument_list|,
name|received
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|AssertionFailedError
name|e
parameter_list|)
block|{
for|for
control|(
name|int
name|j
init|=
literal|0
init|;
name|j
operator|<
name|data
operator|.
name|length
condition|;
name|j
operator|++
control|)
block|{
name|Message
name|m
init|=
name|receivedMessages
operator|.
name|get
argument_list|(
name|j
argument_list|)
decl_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
name|j
operator|+
literal|" => "
operator|+
name|m
operator|.
name|getJMSMessageID
argument_list|()
argument_list|)
expr_stmt|;
block|}
throw|throw
name|e
throw|;
block|}
block|}
block|}
specifier|protected
name|void
name|assertMessageValid
parameter_list|(
name|int
name|index
parameter_list|,
name|Message
name|message
parameter_list|)
throws|throws
name|JMSException
block|{
name|TextMessage
name|textMessage
init|=
operator|(
name|TextMessage
operator|)
name|message
decl_stmt|;
name|String
name|text
init|=
name|textMessage
operator|.
name|getText
argument_list|()
decl_stmt|;
if|if
condition|(
name|verbose
condition|)
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"Received Text: "
operator|+
name|text
argument_list|)
expr_stmt|;
block|}
name|assertEquals
argument_list|(
literal|"Message: "
operator|+
name|index
argument_list|,
name|data
index|[
name|index
index|]
argument_list|,
name|text
argument_list|)
expr_stmt|;
block|}
comment|/**      * Waits for the messages to be delivered or when the wait time has been      * reached.      */
specifier|protected
name|void
name|waitForMessagesToBeDelivered
parameter_list|()
block|{
name|long
name|maxWaitTime
init|=
literal|60000
decl_stmt|;
name|long
name|waitTime
init|=
name|maxWaitTime
decl_stmt|;
name|long
name|start
init|=
operator|(
name|maxWaitTime
operator|<=
literal|0
operator|)
condition|?
literal|0
else|:
name|System
operator|.
name|currentTimeMillis
argument_list|()
decl_stmt|;
synchronized|synchronized
init|(
name|lock
init|)
block|{
while|while
condition|(
name|messages
operator|.
name|size
argument_list|()
operator|<
name|data
operator|.
name|length
operator|&&
name|waitTime
operator|>=
literal|0
condition|)
block|{
try|try
block|{
name|lock
operator|.
name|wait
argument_list|(
literal|200
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|InterruptedException
name|e
parameter_list|)
block|{
name|e
operator|.
name|printStackTrace
argument_list|()
expr_stmt|;
block|}
name|waitTime
operator|=
name|maxWaitTime
operator|-
operator|(
name|System
operator|.
name|currentTimeMillis
argument_list|()
operator|-
name|start
operator|)
expr_stmt|;
block|}
block|}
block|}
comment|/**      * @see javax.jms.MessageListener#onMessage(javax.jms.Message)      */
specifier|public
specifier|synchronized
name|void
name|onMessage
parameter_list|(
name|Message
name|message
parameter_list|)
block|{
name|consumeMessage
argument_list|(
name|message
argument_list|,
name|messages
argument_list|)
expr_stmt|;
block|}
comment|/**      * Consumes a received message.      *       * @param message - a newly received message.      * @param messageList - list containing the received messages.      */
specifier|protected
name|void
name|consumeMessage
parameter_list|(
name|Message
name|message
parameter_list|,
name|List
argument_list|<
name|Message
argument_list|>
name|messageList
parameter_list|)
block|{
if|if
condition|(
name|verbose
condition|)
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"Received message: "
operator|+
name|message
argument_list|)
expr_stmt|;
block|}
name|messageList
operator|.
name|add
argument_list|(
name|message
argument_list|)
expr_stmt|;
if|if
condition|(
name|messageList
operator|.
name|size
argument_list|()
operator|>=
name|data
operator|.
name|length
condition|)
block|{
synchronized|synchronized
init|(
name|lock
init|)
block|{
name|lock
operator|.
name|notifyAll
argument_list|()
expr_stmt|;
block|}
block|}
block|}
comment|/**      * Creates a synchronized list.      *       * @return a synchronized view of the specified list.      */
specifier|protected
name|List
argument_list|<
name|Message
argument_list|>
name|createConcurrentList
parameter_list|()
block|{
return|return
name|Collections
operator|.
name|synchronizedList
argument_list|(
operator|new
name|ArrayList
argument_list|<
name|Message
argument_list|>
argument_list|()
argument_list|)
return|;
block|}
block|}
end_class

end_unit

