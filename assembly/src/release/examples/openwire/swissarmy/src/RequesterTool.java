begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *      http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

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
name|Date
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
name|ActiveMQConnection
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
name|util
operator|.
name|IndentPrinter
import|;
end_import

begin_comment
comment|/**  * A simple tool for publishing messages  *   *   */
end_comment

begin_class
specifier|public
class|class
name|RequesterTool
block|{
specifier|private
name|int
name|messageCount
init|=
literal|10
decl_stmt|;
specifier|private
name|long
name|sleepTime
decl_stmt|;
specifier|private
name|boolean
name|verbose
init|=
literal|true
decl_stmt|;
specifier|private
name|int
name|messageSize
init|=
literal|255
decl_stmt|;
specifier|private
name|long
name|timeToLive
decl_stmt|;
specifier|private
name|String
name|subject
init|=
literal|"TOOL.DEFAULT"
decl_stmt|;
specifier|private
name|String
name|replySubject
decl_stmt|;
specifier|private
name|boolean
name|topic
decl_stmt|;
specifier|private
name|String
name|user
init|=
name|ActiveMQConnection
operator|.
name|DEFAULT_USER
decl_stmt|;
specifier|private
name|String
name|password
init|=
name|ActiveMQConnection
operator|.
name|DEFAULT_PASSWORD
decl_stmt|;
specifier|private
name|String
name|url
init|=
name|ActiveMQConnection
operator|.
name|DEFAULT_BROKER_URL
decl_stmt|;
specifier|private
name|boolean
name|transacted
decl_stmt|;
specifier|private
name|boolean
name|persistent
decl_stmt|;
specifier|private
name|String
name|clientId
decl_stmt|;
specifier|private
name|Destination
name|destination
decl_stmt|;
specifier|private
name|Destination
name|replyDest
decl_stmt|;
specifier|private
name|MessageProducer
name|producer
decl_stmt|;
specifier|private
name|MessageConsumer
name|consumer
decl_stmt|;
specifier|private
name|Session
name|session
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
name|RequesterTool
name|requesterTool
init|=
operator|new
name|RequesterTool
argument_list|()
decl_stmt|;
name|String
index|[]
name|unknown
init|=
name|CommandLineSupport
operator|.
name|setOptions
argument_list|(
name|requesterTool
argument_list|,
name|args
argument_list|)
decl_stmt|;
if|if
condition|(
name|unknown
operator|.
name|length
operator|>
literal|0
condition|)
block|{
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"Unknown options: "
operator|+
name|Arrays
operator|.
name|toString
argument_list|(
name|unknown
argument_list|)
argument_list|)
expr_stmt|;
name|System
operator|.
name|exit
argument_list|(
operator|-
literal|1
argument_list|)
expr_stmt|;
block|}
name|requesterTool
operator|.
name|run
argument_list|()
expr_stmt|;
block|}
specifier|public
name|void
name|run
parameter_list|()
block|{
name|Connection
name|connection
init|=
literal|null
decl_stmt|;
try|try
block|{
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"Connecting to URL: "
operator|+
name|url
argument_list|)
expr_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"Publishing a Message with size "
operator|+
name|messageSize
operator|+
literal|" to "
operator|+
operator|(
name|topic
condition|?
literal|"topic"
else|:
literal|"queue"
operator|)
operator|+
literal|": "
operator|+
name|subject
argument_list|)
expr_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"Using "
operator|+
operator|(
name|persistent
condition|?
literal|"persistent"
else|:
literal|"non-persistent"
operator|)
operator|+
literal|" messages"
argument_list|)
expr_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"Sleeping between publish "
operator|+
name|sleepTime
operator|+
literal|" ms"
argument_list|)
expr_stmt|;
comment|// Create the connection
name|ActiveMQConnectionFactory
name|connectionFactory
init|=
operator|new
name|ActiveMQConnectionFactory
argument_list|(
name|user
argument_list|,
name|password
argument_list|,
name|url
argument_list|)
decl_stmt|;
name|connection
operator|=
name|connectionFactory
operator|.
name|createConnection
argument_list|()
expr_stmt|;
if|if
condition|(
name|persistent
operator|&&
name|clientId
operator|!=
literal|null
operator|&&
name|clientId
operator|.
name|length
argument_list|()
operator|>
literal|0
operator|&&
operator|!
literal|"null"
operator|.
name|equals
argument_list|(
name|clientId
argument_list|)
condition|)
block|{
name|connection
operator|.
name|setClientID
argument_list|(
name|clientId
argument_list|)
expr_stmt|;
block|}
name|connection
operator|.
name|start
argument_list|()
expr_stmt|;
comment|// Create the Session
name|session
operator|=
name|connection
operator|.
name|createSession
argument_list|(
name|transacted
argument_list|,
name|Session
operator|.
name|AUTO_ACKNOWLEDGE
argument_list|)
expr_stmt|;
comment|// And the Destinations..
if|if
condition|(
name|topic
condition|)
block|{
name|destination
operator|=
name|session
operator|.
name|createTopic
argument_list|(
name|subject
argument_list|)
expr_stmt|;
if|if
condition|(
name|replySubject
operator|==
literal|null
operator|||
name|replySubject
operator|.
name|equals
argument_list|(
literal|""
argument_list|)
condition|)
block|{
name|replyDest
operator|=
name|session
operator|.
name|createTemporaryTopic
argument_list|()
expr_stmt|;
block|}
else|else
block|{
name|replyDest
operator|=
name|session
operator|.
name|createTopic
argument_list|(
name|replySubject
argument_list|)
expr_stmt|;
block|}
block|}
else|else
block|{
name|destination
operator|=
name|session
operator|.
name|createQueue
argument_list|(
name|subject
argument_list|)
expr_stmt|;
if|if
condition|(
name|replySubject
operator|==
literal|null
operator|||
name|replySubject
operator|.
name|equals
argument_list|(
literal|""
argument_list|)
condition|)
block|{
name|replyDest
operator|=
name|session
operator|.
name|createTemporaryQueue
argument_list|()
expr_stmt|;
block|}
else|else
block|{
name|replyDest
operator|=
name|session
operator|.
name|createQueue
argument_list|(
name|replySubject
argument_list|)
expr_stmt|;
block|}
block|}
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"Reply Destination: "
operator|+
name|replyDest
argument_list|)
expr_stmt|;
comment|// Create the producer
name|producer
operator|=
name|session
operator|.
name|createProducer
argument_list|(
name|destination
argument_list|)
expr_stmt|;
if|if
condition|(
name|persistent
condition|)
block|{
name|producer
operator|.
name|setDeliveryMode
argument_list|(
name|DeliveryMode
operator|.
name|PERSISTENT
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|producer
operator|.
name|setDeliveryMode
argument_list|(
name|DeliveryMode
operator|.
name|NON_PERSISTENT
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|timeToLive
operator|!=
literal|0
condition|)
block|{
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"Messages time to live "
operator|+
name|timeToLive
operator|+
literal|" ms"
argument_list|)
expr_stmt|;
name|producer
operator|.
name|setTimeToLive
argument_list|(
name|timeToLive
argument_list|)
expr_stmt|;
block|}
comment|// Create the reply consumer
name|consumer
operator|=
name|session
operator|.
name|createConsumer
argument_list|(
name|replyDest
argument_list|)
expr_stmt|;
comment|// Start sending reqests.
name|requestLoop
argument_list|()
expr_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"Done."
argument_list|)
expr_stmt|;
comment|// Use the ActiveMQConnection interface to dump the connection
comment|// stats.
name|ActiveMQConnection
name|c
init|=
operator|(
name|ActiveMQConnection
operator|)
name|connection
decl_stmt|;
name|c
operator|.
name|getConnectionStats
argument_list|()
operator|.
name|dump
argument_list|(
operator|new
name|IndentPrinter
argument_list|()
argument_list|)
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
block|}
finally|finally
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
name|Throwable
name|ignore
parameter_list|)
block|{             }
block|}
block|}
specifier|protected
name|void
name|requestLoop
parameter_list|()
throws|throws
name|Exception
block|{
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
operator|||
name|messageCount
operator|==
literal|0
condition|;
name|i
operator|++
control|)
block|{
name|TextMessage
name|message
init|=
name|session
operator|.
name|createTextMessage
argument_list|(
name|createMessageText
argument_list|(
name|i
argument_list|)
argument_list|)
decl_stmt|;
name|message
operator|.
name|setJMSReplyTo
argument_list|(
name|replyDest
argument_list|)
expr_stmt|;
if|if
condition|(
name|verbose
condition|)
block|{
name|String
name|msg
init|=
name|message
operator|.
name|getText
argument_list|()
decl_stmt|;
if|if
condition|(
name|msg
operator|.
name|length
argument_list|()
operator|>
literal|50
condition|)
block|{
name|msg
operator|=
name|msg
operator|.
name|substring
argument_list|(
literal|0
argument_list|,
literal|50
argument_list|)
operator|+
literal|"..."
expr_stmt|;
block|}
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"Sending message: "
operator|+
name|msg
argument_list|)
expr_stmt|;
block|}
name|producer
operator|.
name|send
argument_list|(
name|message
argument_list|)
expr_stmt|;
if|if
condition|(
name|transacted
condition|)
block|{
name|session
operator|.
name|commit
argument_list|()
expr_stmt|;
block|}
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"Waiting for reponse message..."
argument_list|)
expr_stmt|;
name|Message
name|message2
init|=
name|consumer
operator|.
name|receive
argument_list|()
decl_stmt|;
if|if
condition|(
name|message2
operator|instanceof
name|TextMessage
condition|)
block|{
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"Reponse message: "
operator|+
operator|(
operator|(
name|TextMessage
operator|)
name|message2
operator|)
operator|.
name|getText
argument_list|()
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"Reponse message: "
operator|+
name|message2
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|transacted
condition|)
block|{
name|session
operator|.
name|commit
argument_list|()
expr_stmt|;
block|}
name|Thread
operator|.
name|sleep
argument_list|(
name|sleepTime
argument_list|)
expr_stmt|;
block|}
block|}
comment|/**      * @param i      * @return      */
specifier|private
name|String
name|createMessageText
parameter_list|(
name|int
name|index
parameter_list|)
block|{
name|StringBuffer
name|buffer
init|=
operator|new
name|StringBuffer
argument_list|(
name|messageSize
argument_list|)
decl_stmt|;
name|buffer
operator|.
name|append
argument_list|(
literal|"Message: "
operator|+
name|index
operator|+
literal|" sent at: "
operator|+
operator|new
name|Date
argument_list|()
argument_list|)
expr_stmt|;
if|if
condition|(
name|buffer
operator|.
name|length
argument_list|()
operator|>
name|messageSize
condition|)
block|{
return|return
name|buffer
operator|.
name|substring
argument_list|(
literal|0
argument_list|,
name|messageSize
argument_list|)
return|;
block|}
for|for
control|(
name|int
name|i
init|=
name|buffer
operator|.
name|length
argument_list|()
init|;
name|i
operator|<
name|messageSize
condition|;
name|i
operator|++
control|)
block|{
name|buffer
operator|.
name|append
argument_list|(
literal|' '
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
specifier|public
name|void
name|setClientId
parameter_list|(
name|String
name|clientId
parameter_list|)
block|{
name|this
operator|.
name|clientId
operator|=
name|clientId
expr_stmt|;
block|}
specifier|public
name|void
name|setPersistent
parameter_list|(
name|boolean
name|durable
parameter_list|)
block|{
name|this
operator|.
name|persistent
operator|=
name|durable
expr_stmt|;
block|}
specifier|public
name|void
name|setMessageCount
parameter_list|(
name|int
name|messageCount
parameter_list|)
block|{
name|this
operator|.
name|messageCount
operator|=
name|messageCount
expr_stmt|;
block|}
specifier|public
name|void
name|setMessageSize
parameter_list|(
name|int
name|messageSize
parameter_list|)
block|{
name|this
operator|.
name|messageSize
operator|=
name|messageSize
expr_stmt|;
block|}
specifier|public
name|void
name|setPassword
parameter_list|(
name|String
name|password
parameter_list|)
block|{
name|this
operator|.
name|password
operator|=
name|password
expr_stmt|;
block|}
specifier|public
name|void
name|setSleepTime
parameter_list|(
name|long
name|sleepTime
parameter_list|)
block|{
name|this
operator|.
name|sleepTime
operator|=
name|sleepTime
expr_stmt|;
block|}
specifier|public
name|void
name|setSubject
parameter_list|(
name|String
name|subject
parameter_list|)
block|{
name|this
operator|.
name|subject
operator|=
name|subject
expr_stmt|;
block|}
specifier|public
name|void
name|setTimeToLive
parameter_list|(
name|long
name|timeToLive
parameter_list|)
block|{
name|this
operator|.
name|timeToLive
operator|=
name|timeToLive
expr_stmt|;
block|}
specifier|public
name|void
name|setTopic
parameter_list|(
name|boolean
name|topic
parameter_list|)
block|{
name|this
operator|.
name|topic
operator|=
name|topic
expr_stmt|;
block|}
specifier|public
name|void
name|setQueue
parameter_list|(
name|boolean
name|queue
parameter_list|)
block|{
name|this
operator|.
name|topic
operator|=
operator|!
name|queue
expr_stmt|;
block|}
specifier|public
name|void
name|setTransacted
parameter_list|(
name|boolean
name|transacted
parameter_list|)
block|{
name|this
operator|.
name|transacted
operator|=
name|transacted
expr_stmt|;
block|}
specifier|public
name|void
name|setUrl
parameter_list|(
name|String
name|url
parameter_list|)
block|{
name|this
operator|.
name|url
operator|=
name|url
expr_stmt|;
block|}
specifier|public
name|void
name|setUser
parameter_list|(
name|String
name|user
parameter_list|)
block|{
name|this
operator|.
name|user
operator|=
name|user
expr_stmt|;
block|}
specifier|public
name|void
name|setVerbose
parameter_list|(
name|boolean
name|verbose
parameter_list|)
block|{
name|this
operator|.
name|verbose
operator|=
name|verbose
expr_stmt|;
block|}
specifier|public
name|void
name|setReplySubject
parameter_list|(
name|String
name|replySubject
parameter_list|)
block|{
name|this
operator|.
name|replySubject
operator|=
name|replySubject
expr_stmt|;
block|}
block|}
end_class

end_unit
