begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  *  * Copyright 2005-2006 The Apache Software Foundation  *  * Licensed under the Apache License, Version 2.0 (the "License");  * you may not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  * http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
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
name|DeliveryMode
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
name|java
operator|.
name|util
operator|.
name|Date
import|;
end_import

begin_comment
comment|/**  * A simple tool for publishing messages  *  * @version $Revision: 1.2 $  */
end_comment

begin_class
specifier|public
class|class
name|ProducerTool
extends|extends
name|ToolSupport
block|{
specifier|protected
name|int
name|messageCount
init|=
literal|10
decl_stmt|;
specifier|protected
name|long
name|sleepTime
init|=
literal|0L
decl_stmt|;
specifier|protected
name|boolean
name|verbose
init|=
literal|true
decl_stmt|;
specifier|protected
name|int
name|messageSize
init|=
literal|255
decl_stmt|;
specifier|private
name|long
name|timeToLive
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
name|runTool
argument_list|(
name|args
argument_list|,
operator|new
name|ProducerTool
argument_list|()
argument_list|)
expr_stmt|;
block|}
specifier|protected
specifier|static
name|void
name|runTool
parameter_list|(
name|String
index|[]
name|args
parameter_list|,
name|ProducerTool
name|tool
parameter_list|)
block|{
name|tool
operator|.
name|clientID
operator|=
literal|null
expr_stmt|;
if|if
condition|(
name|args
operator|.
name|length
operator|>
literal|0
condition|)
block|{
name|tool
operator|.
name|url
operator|=
name|args
index|[
literal|0
index|]
expr_stmt|;
block|}
if|if
condition|(
name|args
operator|.
name|length
operator|>
literal|1
condition|)
block|{
name|tool
operator|.
name|topic
operator|=
name|args
index|[
literal|1
index|]
operator|.
name|equalsIgnoreCase
argument_list|(
literal|"true"
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|args
operator|.
name|length
operator|>
literal|2
condition|)
block|{
name|tool
operator|.
name|subject
operator|=
name|args
index|[
literal|2
index|]
expr_stmt|;
block|}
if|if
condition|(
name|args
operator|.
name|length
operator|>
literal|3
condition|)
block|{
name|tool
operator|.
name|durable
operator|=
name|args
index|[
literal|3
index|]
operator|.
name|equalsIgnoreCase
argument_list|(
literal|"true"
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|args
operator|.
name|length
operator|>
literal|4
condition|)
block|{
name|tool
operator|.
name|messageCount
operator|=
name|Integer
operator|.
name|parseInt
argument_list|(
name|args
index|[
literal|4
index|]
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|args
operator|.
name|length
operator|>
literal|5
condition|)
block|{
name|tool
operator|.
name|messageSize
operator|=
name|Integer
operator|.
name|parseInt
argument_list|(
name|args
index|[
literal|5
index|]
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|args
operator|.
name|length
operator|>
literal|6
condition|)
block|{
if|if
condition|(
operator|!
literal|"null"
operator|.
name|equals
argument_list|(
name|args
index|[
literal|6
index|]
argument_list|)
condition|)
block|{
name|tool
operator|.
name|clientID
operator|=
name|args
index|[
literal|6
index|]
expr_stmt|;
block|}
block|}
if|if
condition|(
name|args
operator|.
name|length
operator|>
literal|7
condition|)
block|{
name|tool
operator|.
name|timeToLive
operator|=
name|Long
operator|.
name|parseLong
argument_list|(
name|args
index|[
literal|7
index|]
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|args
operator|.
name|length
operator|>
literal|8
condition|)
block|{
name|tool
operator|.
name|sleepTime
operator|=
name|Long
operator|.
name|parseLong
argument_list|(
name|args
index|[
literal|8
index|]
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|args
operator|.
name|length
operator|>
literal|9
condition|)
block|{
name|tool
operator|.
name|transacted
operator|=
literal|"true"
operator|.
name|equals
argument_list|(
name|args
index|[
literal|9
index|]
argument_list|)
expr_stmt|;
block|}
name|tool
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
name|durable
condition|?
literal|"durable"
else|:
literal|"non-durable"
operator|)
operator|+
literal|" publishing"
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
block|}
name|Connection
name|connection
init|=
name|createConnection
argument_list|()
decl_stmt|;
name|Session
name|session
init|=
name|createSession
argument_list|(
name|connection
argument_list|)
decl_stmt|;
name|MessageProducer
name|producer
init|=
name|createProducer
argument_list|(
name|session
argument_list|)
decl_stmt|;
name|sendLoop
argument_list|(
name|session
argument_list|,
name|producer
argument_list|)
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
name|close
argument_list|(
name|connection
argument_list|,
name|session
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
block|}
specifier|protected
name|MessageProducer
name|createProducer
parameter_list|(
name|Session
name|session
parameter_list|)
throws|throws
name|JMSException
block|{
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
if|if
condition|(
name|durable
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
name|producer
operator|.
name|setTimeToLive
argument_list|(
name|timeToLive
argument_list|)
expr_stmt|;
return|return
name|producer
return|;
block|}
specifier|protected
name|void
name|sendLoop
parameter_list|(
name|Session
name|session
parameter_list|,
name|MessageProducer
name|producer
parameter_list|)
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
block|}
end_class

end_unit

