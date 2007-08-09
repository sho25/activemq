begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *      http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_comment
comment|/**  * The SimpleQueueSender class consists only of a main method,  * which sends several messages to a queue.  *  * Run this program in conjunction with SimpleQueueReceiver.  * Specify a queue name on the command line when you run the  * program.  By default, the program sends one message.  Specify  * a number after the queue name to send that number of messages.  */
end_comment

begin_package
package|package
name|org
operator|.
name|apache
operator|.
name|activemq
operator|.
name|demo
package|;
end_package

begin_comment
comment|// START SNIPPET: demo
end_comment

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
name|Queue
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|jms
operator|.
name|QueueConnection
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|jms
operator|.
name|QueueConnectionFactory
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|jms
operator|.
name|QueueSender
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|jms
operator|.
name|QueueSession
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
name|naming
operator|.
name|Context
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|naming
operator|.
name|InitialContext
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|naming
operator|.
name|NamingException
import|;
end_import

begin_class
specifier|public
class|class
name|SimpleQueueSender
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
name|log
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
name|SimpleQueueSender
operator|.
name|class
argument_list|)
decl_stmt|;
comment|/**      * Main method.      *       * @param args the queue used by the example and, optionally, the number of      *                messages to send      */
specifier|public
specifier|static
name|void
name|main
parameter_list|(
name|String
index|[]
name|args
parameter_list|)
block|{
name|String
name|queueName
init|=
literal|null
decl_stmt|;
name|Context
name|jndiContext
init|=
literal|null
decl_stmt|;
name|QueueConnectionFactory
name|queueConnectionFactory
init|=
literal|null
decl_stmt|;
name|QueueConnection
name|queueConnection
init|=
literal|null
decl_stmt|;
name|QueueSession
name|queueSession
init|=
literal|null
decl_stmt|;
name|Queue
name|queue
init|=
literal|null
decl_stmt|;
name|QueueSender
name|queueSender
init|=
literal|null
decl_stmt|;
name|TextMessage
name|message
init|=
literal|null
decl_stmt|;
specifier|final
name|int
name|NUM_MSGS
decl_stmt|;
if|if
condition|(
operator|(
name|args
operator|.
name|length
operator|<
literal|1
operator|)
operator|||
operator|(
name|args
operator|.
name|length
operator|>
literal|2
operator|)
condition|)
block|{
name|log
operator|.
name|info
argument_list|(
literal|"Usage: java SimpleQueueSender "
operator|+
literal|"<queue-name> [<number-of-messages>]"
argument_list|)
expr_stmt|;
name|System
operator|.
name|exit
argument_list|(
literal|1
argument_list|)
expr_stmt|;
block|}
name|queueName
operator|=
name|args
index|[
literal|0
index|]
expr_stmt|;
name|log
operator|.
name|info
argument_list|(
literal|"Queue name is "
operator|+
name|queueName
argument_list|)
expr_stmt|;
if|if
condition|(
name|args
operator|.
name|length
operator|==
literal|2
condition|)
block|{
name|NUM_MSGS
operator|=
operator|(
operator|new
name|Integer
argument_list|(
name|args
index|[
literal|1
index|]
argument_list|)
operator|)
operator|.
name|intValue
argument_list|()
expr_stmt|;
block|}
else|else
block|{
name|NUM_MSGS
operator|=
literal|1
expr_stmt|;
block|}
comment|/*          * Create a JNDI API InitialContext object if none exists yet.          */
try|try
block|{
name|jndiContext
operator|=
operator|new
name|InitialContext
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|NamingException
name|e
parameter_list|)
block|{
name|log
operator|.
name|info
argument_list|(
literal|"Could not create JNDI API context: "
operator|+
name|e
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|System
operator|.
name|exit
argument_list|(
literal|1
argument_list|)
expr_stmt|;
block|}
comment|/*          * Look up connection factory and queue. If either does not exist, exit.          */
try|try
block|{
name|queueConnectionFactory
operator|=
operator|(
name|QueueConnectionFactory
operator|)
name|jndiContext
operator|.
name|lookup
argument_list|(
literal|"QueueConnectionFactory"
argument_list|)
expr_stmt|;
name|queue
operator|=
operator|(
name|Queue
operator|)
name|jndiContext
operator|.
name|lookup
argument_list|(
name|queueName
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|NamingException
name|e
parameter_list|)
block|{
name|log
operator|.
name|info
argument_list|(
literal|"JNDI API lookup failed: "
operator|+
name|e
argument_list|)
expr_stmt|;
name|System
operator|.
name|exit
argument_list|(
literal|1
argument_list|)
expr_stmt|;
block|}
comment|/*          * Create connection. Create session from connection; false means          * session is not transacted. Create sender and text message. Send          * messages, varying text slightly. Send end-of-messages message.          * Finally, close connection.          */
try|try
block|{
name|queueConnection
operator|=
name|queueConnectionFactory
operator|.
name|createQueueConnection
argument_list|()
expr_stmt|;
name|queueSession
operator|=
name|queueConnection
operator|.
name|createQueueSession
argument_list|(
literal|false
argument_list|,
name|Session
operator|.
name|AUTO_ACKNOWLEDGE
argument_list|)
expr_stmt|;
name|queueSender
operator|=
name|queueSession
operator|.
name|createSender
argument_list|(
name|queue
argument_list|)
expr_stmt|;
name|message
operator|=
name|queueSession
operator|.
name|createTextMessage
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
name|NUM_MSGS
condition|;
name|i
operator|++
control|)
block|{
name|message
operator|.
name|setText
argument_list|(
literal|"This is message "
operator|+
operator|(
name|i
operator|+
literal|1
operator|)
argument_list|)
expr_stmt|;
name|log
operator|.
name|info
argument_list|(
literal|"Sending message: "
operator|+
name|message
operator|.
name|getText
argument_list|()
argument_list|)
expr_stmt|;
name|queueSender
operator|.
name|send
argument_list|(
name|message
argument_list|)
expr_stmt|;
block|}
comment|/*              * Send a non-text control message indicating end of messages.              */
name|queueSender
operator|.
name|send
argument_list|(
name|queueSession
operator|.
name|createMessage
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
name|log
operator|.
name|info
argument_list|(
literal|"Exception occurred: "
operator|+
name|e
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
if|if
condition|(
name|queueConnection
operator|!=
literal|null
condition|)
block|{
try|try
block|{
name|queueConnection
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|JMSException
name|e
parameter_list|)
block|{                 }
block|}
block|}
block|}
block|}
end_class

begin_comment
comment|// END SNIPPET: demo
end_comment

end_unit

