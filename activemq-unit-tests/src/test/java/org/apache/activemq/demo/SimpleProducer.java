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
name|Connection
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|jms
operator|.
name|ConnectionFactory
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
comment|/**  * A simple polymorphic JMS producer which can work with Queues or Topics which  * uses JNDI to lookup the JMS connection factory and destination  *   *   */
end_comment

begin_class
specifier|public
specifier|final
class|class
name|SimpleProducer
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
name|SimpleProducer
operator|.
name|class
argument_list|)
decl_stmt|;
specifier|private
name|SimpleProducer
parameter_list|()
block|{     }
comment|/**      * @param args the destination name to send to and optionally, the number of      *                messages to send      */
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
name|Context
name|jndiContext
init|=
literal|null
decl_stmt|;
name|ConnectionFactory
name|connectionFactory
init|=
literal|null
decl_stmt|;
name|Connection
name|connection
init|=
literal|null
decl_stmt|;
name|Session
name|session
init|=
literal|null
decl_stmt|;
name|Destination
name|destination
init|=
literal|null
decl_stmt|;
name|MessageProducer
name|producer
init|=
literal|null
decl_stmt|;
name|String
name|destinationName
init|=
literal|null
decl_stmt|;
specifier|final
name|int
name|numMsgs
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
name|LOG
operator|.
name|info
argument_list|(
literal|"Usage: java SimpleProducer<destination-name> [<number-of-messages>]"
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
name|destinationName
operator|=
name|args
index|[
literal|0
index|]
expr_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"Destination name is "
operator|+
name|destinationName
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
name|numMsgs
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
name|numMsgs
operator|=
literal|1
expr_stmt|;
block|}
comment|/*          * Create a JNDI API InitialContext object          */
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
name|LOG
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
comment|/*          * Look up connection factory and destination.          */
try|try
block|{
name|connectionFactory
operator|=
operator|(
name|ConnectionFactory
operator|)
name|jndiContext
operator|.
name|lookup
argument_list|(
literal|"ConnectionFactory"
argument_list|)
expr_stmt|;
name|destination
operator|=
operator|(
name|Destination
operator|)
name|jndiContext
operator|.
name|lookup
argument_list|(
name|destinationName
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|NamingException
name|e
parameter_list|)
block|{
name|LOG
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
name|connection
operator|=
name|connectionFactory
operator|.
name|createConnection
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
name|AUTO_ACKNOWLEDGE
argument_list|)
expr_stmt|;
name|producer
operator|=
name|session
operator|.
name|createProducer
argument_list|(
name|destination
argument_list|)
expr_stmt|;
name|TextMessage
name|message
init|=
name|session
operator|.
name|createTextMessage
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
name|numMsgs
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
name|LOG
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
name|producer
operator|.
name|send
argument_list|(
name|message
argument_list|)
expr_stmt|;
block|}
comment|/*              * Send a non-text control message indicating end of messages.              */
name|producer
operator|.
name|send
argument_list|(
name|session
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
name|LOG
operator|.
name|info
argument_list|(
literal|"Exception occurred: "
operator|+
name|e
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
if|if
condition|(
name|connection
operator|!=
literal|null
condition|)
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
