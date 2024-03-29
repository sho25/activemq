begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *      http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_comment
comment|/**  * The SimpleQueueReceiver class consists only of a main method,  * which fetches one or more messages from a queue using  * synchronous message delivery.  Run this program in conjunction  * with SimpleQueueSender.  Specify a queue name on the command  * line when you run the program.  */
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

begin_comment
comment|/**  * A simple polymorphic JMS consumer which can work with Queues or Topics which  * uses JNDI to lookup the JMS connection factory and destination  *   *   */
end_comment

begin_class
specifier|public
specifier|final
class|class
name|SimpleConsumer
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
name|SimpleConsumer
operator|.
name|class
argument_list|)
decl_stmt|;
specifier|private
name|SimpleConsumer
parameter_list|()
block|{     }
comment|/**      * @param args the queue used by the example      */
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
name|destinationName
init|=
literal|null
decl_stmt|;
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
name|MessageConsumer
name|consumer
init|=
literal|null
decl_stmt|;
comment|/*          * Read destination name from command line and display it.          */
if|if
condition|(
name|args
operator|.
name|length
operator|!=
literal|1
condition|)
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"Usage: java SimpleConsumer<destination-name>"
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
literal|"Could not create JNDI API "
operator|+
literal|"context: "
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
comment|/*          * Create connection. Create session from connection; false means          * session is not transacted. Create receiver, then start message          * delivery. Receive all text messages from destination until a non-text          * message is received indicating end of message stream. Close          * connection.          */
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
name|consumer
operator|=
name|session
operator|.
name|createConsumer
argument_list|(
name|destination
argument_list|)
expr_stmt|;
name|connection
operator|.
name|start
argument_list|()
expr_stmt|;
while|while
condition|(
literal|true
condition|)
block|{
name|Message
name|m
init|=
name|consumer
operator|.
name|receive
argument_list|(
literal|1
argument_list|)
decl_stmt|;
if|if
condition|(
name|m
operator|!=
literal|null
condition|)
block|{
if|if
condition|(
name|m
operator|instanceof
name|TextMessage
condition|)
block|{
name|TextMessage
name|message
init|=
operator|(
name|TextMessage
operator|)
name|m
decl_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"Reading message: "
operator|+
name|message
operator|.
name|getText
argument_list|()
argument_list|)
expr_stmt|;
block|}
else|else
block|{
break|break;
block|}
block|}
block|}
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

end_unit

