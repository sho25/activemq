begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  *  * Copyright 2005 The Apache Software Foundation  *  * Licensed under the Apache License, Version 2.0 (the "License");  * you may not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  * http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
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
name|javax
operator|.
name|jms
operator|.
name|*
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

begin_comment
comment|/**  * A simple queue sender which does not use JNDI  *   * @version $Revision: 1.1 $  */
end_comment

begin_class
specifier|public
class|class
name|DefaultQueueSender
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
name|DefaultQueueSender
operator|.
name|class
argument_list|)
decl_stmt|;
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
name|uri
init|=
literal|"tcp://localhost:61616"
decl_stmt|;
name|String
name|text
init|=
literal|"Hello World!"
decl_stmt|;
name|Connection
name|connection
init|=
literal|null
decl_stmt|;
name|QueueSession
name|queueSession
init|=
literal|null
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
condition|)
block|{
name|printUsage
argument_list|()
expr_stmt|;
name|System
operator|.
name|exit
argument_list|(
literal|1
argument_list|)
expr_stmt|;
block|}
name|int
name|idx
init|=
literal|0
decl_stmt|;
name|String
name|arg
init|=
name|args
index|[
literal|0
index|]
decl_stmt|;
if|if
condition|(
name|arg
operator|.
name|equals
argument_list|(
literal|"-uri"
argument_list|)
condition|)
block|{
if|if
condition|(
name|args
operator|.
name|length
operator|==
literal|1
condition|)
block|{
name|printUsage
argument_list|()
expr_stmt|;
name|System
operator|.
name|exit
argument_list|(
literal|1
argument_list|)
expr_stmt|;
block|}
name|uri
operator|=
name|args
index|[
literal|1
index|]
expr_stmt|;
name|idx
operator|+=
literal|2
expr_stmt|;
block|}
name|String
name|queueName
init|=
name|args
index|[
name|idx
index|]
decl_stmt|;
name|log
operator|.
name|info
argument_list|(
literal|"Connecting to: "
operator|+
name|uri
argument_list|)
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
operator|++
name|idx
operator|<
name|args
operator|.
name|length
condition|)
block|{
name|text
operator|=
name|args
index|[
name|idx
index|]
expr_stmt|;
block|}
try|try
block|{
name|ConnectionFactory
name|factory
init|=
operator|new
name|ActiveMQConnectionFactory
argument_list|(
name|uri
argument_list|)
decl_stmt|;
name|connection
operator|=
name|factory
operator|.
name|createConnection
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
name|AUTO_ACKNOWLEDGE
argument_list|)
decl_stmt|;
name|Destination
name|destination
init|=
name|session
operator|.
name|createQueue
argument_list|(
name|queueName
argument_list|)
decl_stmt|;
name|MessageProducer
name|producer
init|=
name|session
operator|.
name|createProducer
argument_list|(
name|destination
argument_list|)
decl_stmt|;
name|Message
name|message
init|=
name|session
operator|.
name|createTextMessage
argument_list|(
name|text
argument_list|)
decl_stmt|;
name|producer
operator|.
name|send
argument_list|(
name|message
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
specifier|protected
specifier|static
name|void
name|printUsage
parameter_list|()
block|{
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"Usage: java DefaultQueueSender [-uri<connection-uri>] "
operator|+
literal|"<queue-name> [<message-body>]"
argument_list|)
expr_stmt|;
block|}
block|}
end_class

begin_comment
comment|// END SNIPPET: demo
end_comment

end_unit

