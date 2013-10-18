begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  * http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
package|package
name|example
operator|.
name|wildcard
package|;
end_package

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
name|org
operator|.
name|apache
operator|.
name|activemq
operator|.
name|command
operator|.
name|ActiveMQTopic
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
name|java
operator|.
name|util
operator|.
name|Scanner
import|;
end_import

begin_comment
comment|/**  * @author<a href="http://www.christianposta.com/blog">Christian Posta</a>  */
end_comment

begin_class
specifier|public
class|class
name|Client
block|{
specifier|private
specifier|static
specifier|final
name|Boolean
name|NON_TRANSACTED
init|=
literal|false
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|String
name|BROKER_URL
init|=
literal|"tcp://localhost:61616"
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
name|url
init|=
name|BROKER_URL
decl_stmt|;
if|if
condition|(
name|args
operator|.
name|length
operator|>
literal|0
condition|)
block|{
name|url
operator|=
name|args
index|[
literal|0
index|]
operator|.
name|trim
argument_list|()
expr_stmt|;
block|}
name|ActiveMQConnectionFactory
name|connectionFactory
init|=
operator|new
name|ActiveMQConnectionFactory
argument_list|(
literal|"admin"
argument_list|,
literal|"password"
argument_list|,
name|url
argument_list|)
decl_stmt|;
name|Connection
name|connection
init|=
literal|null
decl_stmt|;
try|try
block|{
name|Topic
name|senderTopic
init|=
operator|new
name|ActiveMQTopic
argument_list|(
name|System
operator|.
name|getProperty
argument_list|(
literal|"topicName"
argument_list|)
argument_list|)
decl_stmt|;
name|connection
operator|=
name|connectionFactory
operator|.
name|createConnection
argument_list|(
literal|"admin"
argument_list|,
literal|"password"
argument_list|)
expr_stmt|;
name|Session
name|senderSession
init|=
name|connection
operator|.
name|createSession
argument_list|(
name|NON_TRANSACTED
argument_list|,
name|Session
operator|.
name|AUTO_ACKNOWLEDGE
argument_list|)
decl_stmt|;
name|MessageProducer
name|sender
init|=
name|senderSession
operator|.
name|createProducer
argument_list|(
name|senderTopic
argument_list|)
decl_stmt|;
name|Session
name|receiverSession
init|=
name|connection
operator|.
name|createSession
argument_list|(
name|NON_TRANSACTED
argument_list|,
name|Session
operator|.
name|AUTO_ACKNOWLEDGE
argument_list|)
decl_stmt|;
name|String
name|policyType
init|=
name|System
operator|.
name|getProperty
argument_list|(
literal|"wildcard"
argument_list|,
literal|".*"
argument_list|)
decl_stmt|;
name|String
name|receiverTopicName
init|=
name|senderTopic
operator|.
name|getTopicName
argument_list|()
operator|+
name|policyType
decl_stmt|;
name|Topic
name|receiverTopic
init|=
name|receiverSession
operator|.
name|createTopic
argument_list|(
name|receiverTopicName
argument_list|)
decl_stmt|;
name|MessageConsumer
name|receiver
init|=
name|receiverSession
operator|.
name|createConsumer
argument_list|(
name|receiverTopic
argument_list|)
decl_stmt|;
name|receiver
operator|.
name|setMessageListener
argument_list|(
operator|new
name|MessageListener
argument_list|()
block|{
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
if|if
condition|(
name|message
operator|instanceof
name|TextMessage
condition|)
block|{
name|String
name|text
init|=
operator|(
operator|(
name|TextMessage
operator|)
name|message
operator|)
operator|.
name|getText
argument_list|()
decl_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"We received a new message: "
operator|+
name|text
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
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"Could not read the receiver's topic because of a JMSException"
argument_list|)
expr_stmt|;
block|}
block|}
block|}
argument_list|)
expr_stmt|;
name|connection
operator|.
name|start
argument_list|()
expr_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"Listening on '"
operator|+
name|receiverTopicName
operator|+
literal|"'"
argument_list|)
expr_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"Enter a message to send: "
argument_list|)
expr_stmt|;
name|Scanner
name|inputReader
init|=
operator|new
name|Scanner
argument_list|(
name|System
operator|.
name|in
argument_list|)
decl_stmt|;
while|while
condition|(
literal|true
condition|)
block|{
name|String
name|line
init|=
name|inputReader
operator|.
name|nextLine
argument_list|()
decl_stmt|;
if|if
condition|(
name|line
operator|==
literal|null
condition|)
block|{
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"Done!"
argument_list|)
expr_stmt|;
break|break;
block|}
elseif|else
if|if
condition|(
name|line
operator|.
name|length
argument_list|()
operator|>
literal|0
condition|)
block|{
try|try
block|{
name|TextMessage
name|message
init|=
name|senderSession
operator|.
name|createTextMessage
argument_list|()
decl_stmt|;
name|message
operator|.
name|setText
argument_list|(
name|line
argument_list|)
expr_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"Sending a message: "
operator|+
name|message
operator|.
name|getText
argument_list|()
argument_list|)
expr_stmt|;
name|sender
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
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"Exception during publishing a message: "
argument_list|)
expr_stmt|;
block|}
block|}
block|}
name|receiver
operator|.
name|close
argument_list|()
expr_stmt|;
name|receiverSession
operator|.
name|close
argument_list|()
expr_stmt|;
name|sender
operator|.
name|close
argument_list|()
expr_stmt|;
name|senderSession
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"Caught exception!"
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
block|{
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"When trying to close connection: "
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|}
block|}
end_class

end_unit
