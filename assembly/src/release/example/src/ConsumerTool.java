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
name|ExceptionListener
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
name|jms
operator|.
name|Topic
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|IOException
import|;
end_import

begin_comment
comment|/**  * A simple tool for consuming messages  *  * @version $Revision: 1.1.1.1 $  */
end_comment

begin_class
specifier|public
class|class
name|ConsumerTool
extends|extends
name|ToolSupport
implements|implements
name|MessageListener
implements|,
name|ExceptionListener
block|{
specifier|protected
name|int
name|count
init|=
literal|0
decl_stmt|;
specifier|protected
name|int
name|dumpCount
init|=
literal|10
decl_stmt|;
specifier|protected
name|boolean
name|verbose
init|=
literal|true
decl_stmt|;
specifier|protected
name|int
name|maxiumMessages
init|=
literal|0
decl_stmt|;
specifier|private
name|boolean
name|pauseBeforeShutdown
decl_stmt|;
specifier|private
name|boolean
name|running
decl_stmt|;
specifier|private
name|Session
name|session
decl_stmt|;
specifier|private
name|long
name|sleepTime
init|=
literal|0
decl_stmt|;
specifier|private
name|long
name|receiveTimeOut
init|=
literal|0
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
name|ConsumerTool
name|tool
init|=
operator|new
name|ConsumerTool
argument_list|()
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
name|maxiumMessages
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
name|clientID
operator|=
name|args
index|[
literal|5
index|]
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
literal|6
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
literal|7
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
name|receiveTimeOut
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
name|running
operator|=
literal|true
expr_stmt|;
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
literal|"Consuming "
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
literal|" subscription"
argument_list|)
expr_stmt|;
name|Connection
name|connection
init|=
name|createConnection
argument_list|()
decl_stmt|;
name|connection
operator|.
name|setExceptionListener
argument_list|(
name|this
argument_list|)
expr_stmt|;
name|session
operator|=
name|createSession
argument_list|(
name|connection
argument_list|)
expr_stmt|;
name|MessageConsumer
name|consumer
init|=
literal|null
decl_stmt|;
if|if
condition|(
name|durable
operator|&&
name|topic
condition|)
block|{
name|consumer
operator|=
name|session
operator|.
name|createDurableSubscriber
argument_list|(
operator|(
name|Topic
operator|)
name|destination
argument_list|,
name|consumerName
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|consumer
operator|=
name|session
operator|.
name|createConsumer
argument_list|(
name|destination
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|maxiumMessages
operator|>
literal|0
condition|)
block|{
name|consumeMessagesAndClose
argument_list|(
name|connection
argument_list|,
name|session
argument_list|,
name|consumer
argument_list|)
expr_stmt|;
block|}
else|else
block|{
if|if
condition|(
name|receiveTimeOut
operator|==
literal|0
condition|)
block|{
name|consumer
operator|.
name|setMessageListener
argument_list|(
name|this
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|consumeMessagesAndClose
argument_list|(
name|connection
argument_list|,
name|session
argument_list|,
name|consumer
argument_list|,
name|receiveTimeOut
argument_list|)
expr_stmt|;
block|}
block|}
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
name|TextMessage
name|txtMsg
init|=
operator|(
name|TextMessage
operator|)
name|message
decl_stmt|;
if|if
condition|(
name|verbose
condition|)
block|{
name|String
name|msg
init|=
name|txtMsg
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
literal|"Received: "
operator|+
name|msg
argument_list|)
expr_stmt|;
block|}
block|}
else|else
block|{
if|if
condition|(
name|verbose
condition|)
block|{
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"Received: "
operator|+
name|message
argument_list|)
expr_stmt|;
block|}
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
comment|/*             if (++count % dumpCount == 0) {                 dumpStats(connection);             }             */
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
if|if
condition|(
name|sleepTime
operator|>
literal|0
condition|)
block|{
try|try
block|{
name|Thread
operator|.
name|sleep
argument_list|(
name|sleepTime
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|InterruptedException
name|e
parameter_list|)
block|{                 }
block|}
block|}
block|}
specifier|synchronized
specifier|public
name|void
name|onException
parameter_list|(
name|JMSException
name|ex
parameter_list|)
block|{
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"JMS Exception occured.  Shutting down client."
argument_list|)
expr_stmt|;
name|running
operator|=
literal|false
expr_stmt|;
block|}
specifier|synchronized
name|boolean
name|isRunning
parameter_list|()
block|{
return|return
name|running
return|;
block|}
specifier|protected
name|void
name|consumeMessagesAndClose
parameter_list|(
name|Connection
name|connection
parameter_list|,
name|Session
name|session
parameter_list|,
name|MessageConsumer
name|consumer
parameter_list|)
throws|throws
name|JMSException
throws|,
name|IOException
block|{
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"We are about to wait until we consume: "
operator|+
name|maxiumMessages
operator|+
literal|" message(s) then we will shutdown"
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
name|maxiumMessages
operator|&&
name|isRunning
argument_list|()
condition|;
control|)
block|{
name|Message
name|message
init|=
name|consumer
operator|.
name|receive
argument_list|(
literal|1000
argument_list|)
decl_stmt|;
if|if
condition|(
name|message
operator|!=
literal|null
condition|)
block|{
name|i
operator|++
expr_stmt|;
name|onMessage
argument_list|(
name|message
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
literal|"Closing connection"
argument_list|)
expr_stmt|;
name|consumer
operator|.
name|close
argument_list|()
expr_stmt|;
name|session
operator|.
name|close
argument_list|()
expr_stmt|;
name|connection
operator|.
name|close
argument_list|()
expr_stmt|;
if|if
condition|(
name|pauseBeforeShutdown
condition|)
block|{
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"Press return to shut down"
argument_list|)
expr_stmt|;
name|System
operator|.
name|in
operator|.
name|read
argument_list|()
expr_stmt|;
block|}
block|}
specifier|protected
name|void
name|consumeMessagesAndClose
parameter_list|(
name|Connection
name|connection
parameter_list|,
name|Session
name|session
parameter_list|,
name|MessageConsumer
name|consumer
parameter_list|,
name|long
name|timeout
parameter_list|)
throws|throws
name|JMSException
throws|,
name|IOException
block|{
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"We will consume messages while they continue to be delivered within: "
operator|+
name|timeout
operator|+
literal|" ms, and then we will shutdown"
argument_list|)
expr_stmt|;
name|Message
name|message
decl_stmt|;
while|while
condition|(
operator|(
name|message
operator|=
name|consumer
operator|.
name|receive
argument_list|(
name|timeout
argument_list|)
operator|)
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
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"Closing connection"
argument_list|)
expr_stmt|;
name|consumer
operator|.
name|close
argument_list|()
expr_stmt|;
name|session
operator|.
name|close
argument_list|()
expr_stmt|;
name|connection
operator|.
name|close
argument_list|()
expr_stmt|;
if|if
condition|(
name|pauseBeforeShutdown
condition|)
block|{
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"Press return to shut down"
argument_list|)
expr_stmt|;
name|System
operator|.
name|in
operator|.
name|read
argument_list|()
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

