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

begin_comment
comment|/**  * @version $Revision: 1.7 $  */
end_comment

begin_class
specifier|public
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
name|org
operator|.
name|apache
operator|.
name|commons
operator|.
name|logging
operator|.
name|Log
name|LOG
init|=
name|org
operator|.
name|apache
operator|.
name|commons
operator|.
name|logging
operator|.
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
literal|"Text for message: "
operator|+
name|i
operator|+
literal|" at "
operator|+
operator|new
name|Date
argument_list|()
expr_stmt|;
block|}
block|}
comment|/**      * Sends and consumes the messages.      *       * @throws Exception      */
specifier|public
name|void
name|testSendReceive
parameter_list|()
throws|throws
name|Exception
block|{
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
name|session
operator|.
name|createTextMessage
argument_list|(
name|data
index|[
name|i
index|]
argument_list|)
decl_stmt|;
name|message
operator|.
name|setStringProperty
argument_list|(
literal|"stringProperty"
argument_list|,
name|data
index|[
name|i
index|]
argument_list|)
expr_stmt|;
name|message
operator|.
name|setIntProperty
argument_list|(
literal|"intProperty"
argument_list|,
name|i
argument_list|)
expr_stmt|;
if|if
condition|(
name|verbose
condition|)
block|{
if|if
condition|(
name|LOG
operator|.
name|isDebugEnabled
argument_list|()
condition|)
block|{
name|LOG
operator|.
name|debug
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
name|messageSent
argument_list|()
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
comment|/**      * Asserts messages are received.      *       * @throws JMSException      */
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
comment|/**      * Tests if the messages received are valid.      *       * @param receivedMessages - list of received messages.      * @throws JMSException      */
specifier|protected
name|void
name|assertMessagesReceivedAreValid
parameter_list|(
name|List
name|receivedMessages
parameter_list|)
throws|throws
name|JMSException
block|{
name|List
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
name|TextMessage
name|message
init|=
operator|(
name|TextMessage
operator|)
name|iter
operator|.
name|next
argument_list|()
decl_stmt|;
if|if
condition|(
name|LOG
operator|.
name|isInfoEnabled
argument_list|()
condition|)
block|{
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
operator|.
name|getText
argument_list|()
argument_list|)
expr_stmt|;
block|}
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
name|TextMessage
name|received
init|=
operator|(
name|TextMessage
operator|)
name|receivedMessages
operator|.
name|get
argument_list|(
name|i
argument_list|)
decl_stmt|;
name|String
name|text
init|=
name|received
operator|.
name|getText
argument_list|()
decl_stmt|;
name|String
name|stringProperty
init|=
name|received
operator|.
name|getStringProperty
argument_list|(
literal|"stringProperty"
argument_list|)
decl_stmt|;
name|int
name|intProperty
init|=
name|received
operator|.
name|getIntProperty
argument_list|(
literal|"intProperty"
argument_list|)
decl_stmt|;
if|if
condition|(
name|verbose
condition|)
block|{
if|if
condition|(
name|LOG
operator|.
name|isDebugEnabled
argument_list|()
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
block|}
name|assertEquals
argument_list|(
literal|"Message: "
operator|+
name|i
argument_list|,
name|data
index|[
name|i
index|]
argument_list|,
name|text
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|data
index|[
name|i
index|]
argument_list|,
name|stringProperty
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|i
argument_list|,
name|intProperty
argument_list|)
expr_stmt|;
block|}
block|}
comment|/**      * Waits for messages to be delivered.      */
specifier|protected
name|void
name|waitForMessagesToBeDelivered
parameter_list|()
block|{
name|long
name|maxWaitTime
init|=
literal|30000
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
comment|/*      * (non-Javadoc)      *       * @see javax.jms.MessageListener#onMessage(javax.jms.Message)      */
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
comment|/**      * Consumes messages.      *       * @param message - message to be consumed.      * @param messageList -list of consumed messages.      */
specifier|protected
name|void
name|consumeMessage
parameter_list|(
name|Message
name|message
parameter_list|,
name|List
name|messageList
parameter_list|)
block|{
if|if
condition|(
name|verbose
condition|)
block|{
if|if
condition|(
name|LOG
operator|.
name|isDebugEnabled
argument_list|()
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
comment|/**      * Returns the ArrayList as a synchronized list.      *       * @return List      */
specifier|protected
name|List
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
argument_list|()
argument_list|)
return|;
block|}
comment|/**      * Just a hook so can insert failure tests      *       * @throws Exception      */
specifier|protected
name|void
name|messageSent
parameter_list|()
throws|throws
name|Exception
block|{      }
block|}
end_class

end_unit

