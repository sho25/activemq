begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  *  * Copyright 2004 Protique Ltd  *  * Licensed under the Apache License, Version 2.0 (the "License");  * you may not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  * http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  *  **/
end_comment

begin_package
package|package
name|org
operator|.
name|activemq
operator|.
name|benchmark
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
name|java
operator|.
name|io
operator|.
name|BufferedReader
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|File
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|FileReader
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
comment|/**  * @author James Strachan  * @version $Revision$  */
end_comment

begin_class
specifier|public
class|class
name|Producer
extends|extends
name|BenchmarkSupport
block|{
name|int
name|loops
init|=
operator|-
literal|1
decl_stmt|;
name|int
name|loopSize
init|=
literal|1000
decl_stmt|;
specifier|private
name|int
name|messageSize
init|=
literal|1000
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
name|Producer
name|tool
init|=
operator|new
name|Producer
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
name|setUrl
argument_list|(
name|args
index|[
literal|0
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
literal|1
condition|)
block|{
name|tool
operator|.
name|setTopic
argument_list|(
name|parseBoolean
argument_list|(
name|args
index|[
literal|1
index|]
argument_list|)
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
name|setSubject
argument_list|(
name|args
index|[
literal|2
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
literal|3
condition|)
block|{
name|tool
operator|.
name|setDurable
argument_list|(
name|parseBoolean
argument_list|(
name|args
index|[
literal|3
index|]
argument_list|)
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
name|setMessageSize
argument_list|(
name|Integer
operator|.
name|parseInt
argument_list|(
name|args
index|[
literal|4
index|]
argument_list|)
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
name|setConnectionCount
argument_list|(
name|Integer
operator|.
name|parseInt
argument_list|(
name|args
index|[
literal|5
index|]
argument_list|)
argument_list|)
expr_stmt|;
block|}
try|try
block|{
name|tool
operator|.
name|run
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
name|Producer
parameter_list|()
block|{     }
specifier|public
name|void
name|run
parameter_list|()
throws|throws
name|Exception
block|{
name|start
argument_list|()
expr_stmt|;
name|publish
argument_list|()
expr_stmt|;
block|}
comment|// Properties
comment|//-------------------------------------------------------------------------
specifier|public
name|int
name|getMessageSize
parameter_list|()
block|{
return|return
name|messageSize
return|;
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
name|int
name|getLoopSize
parameter_list|()
block|{
return|return
name|loopSize
return|;
block|}
specifier|public
name|void
name|setLoopSize
parameter_list|(
name|int
name|loopSize
parameter_list|)
block|{
name|this
operator|.
name|loopSize
operator|=
name|loopSize
expr_stmt|;
block|}
comment|// Implementation methods
comment|//-------------------------------------------------------------------------
specifier|protected
name|void
name|publish
parameter_list|()
throws|throws
name|Exception
block|{
specifier|final
name|String
name|text
init|=
name|getMessage
argument_list|()
decl_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"Publishing to: "
operator|+
name|subjects
operator|.
name|length
operator|+
literal|" subject(s)"
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
name|subjects
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
specifier|final
name|String
name|subject
init|=
name|subjects
index|[
name|i
index|]
decl_stmt|;
name|Thread
name|thread
init|=
operator|new
name|Thread
argument_list|()
block|{
specifier|public
name|void
name|run
parameter_list|()
block|{
try|try
block|{
name|publish
argument_list|(
name|text
argument_list|,
name|subject
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
block|}
decl_stmt|;
name|thread
operator|.
name|start
argument_list|()
expr_stmt|;
block|}
block|}
specifier|protected
name|String
name|getMessage
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
name|messageSize
condition|;
name|i
operator|++
control|)
block|{
name|char
name|ch
init|=
literal|'X'
decl_stmt|;
name|buffer
operator|.
name|append
argument_list|(
name|ch
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
specifier|protected
name|void
name|publish
parameter_list|(
name|String
name|text
parameter_list|,
name|String
name|subject
parameter_list|)
throws|throws
name|JMSException
block|{
name|Session
name|session
init|=
name|createSession
argument_list|()
decl_stmt|;
name|Destination
name|destination
init|=
name|createDestination
argument_list|(
name|session
argument_list|,
name|subject
argument_list|)
decl_stmt|;
name|MessageProducer
name|publisher
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
name|isDurable
argument_list|()
condition|)
block|{
name|publisher
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
name|publisher
operator|.
name|setDeliveryMode
argument_list|(
name|DeliveryMode
operator|.
name|NON_PERSISTENT
argument_list|)
expr_stmt|;
block|}
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"Starting publisher on : "
operator|+
name|destination
operator|+
literal|" of type: "
operator|+
name|destination
operator|.
name|getClass
argument_list|()
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"Message length: "
operator|+
name|text
operator|.
name|length
argument_list|()
argument_list|)
expr_stmt|;
if|if
condition|(
name|loops
operator|<=
literal|0
condition|)
block|{
while|while
condition|(
literal|true
condition|)
block|{
name|publishLoop
argument_list|(
name|session
argument_list|,
name|publisher
argument_list|,
name|text
argument_list|)
expr_stmt|;
block|}
block|}
else|else
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
name|loops
condition|;
name|i
operator|++
control|)
block|{
name|publishLoop
argument_list|(
name|session
argument_list|,
name|publisher
argument_list|,
name|text
argument_list|)
expr_stmt|;
block|}
block|}
block|}
specifier|protected
name|void
name|publishLoop
parameter_list|(
name|Session
name|session
parameter_list|,
name|MessageProducer
name|publisher
parameter_list|,
name|String
name|text
parameter_list|)
throws|throws
name|JMSException
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
name|loopSize
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
name|text
argument_list|)
decl_stmt|;
name|publisher
operator|.
name|send
argument_list|(
name|message
argument_list|)
expr_stmt|;
name|count
argument_list|(
literal|1
argument_list|)
expr_stmt|;
block|}
block|}
specifier|protected
name|String
name|loadFile
parameter_list|(
name|String
name|file
parameter_list|)
throws|throws
name|IOException
block|{
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"Loading file: "
operator|+
name|file
argument_list|)
expr_stmt|;
name|StringBuffer
name|buffer
init|=
operator|new
name|StringBuffer
argument_list|()
decl_stmt|;
name|BufferedReader
name|in
init|=
operator|new
name|BufferedReader
argument_list|(
operator|new
name|FileReader
argument_list|(
name|file
argument_list|)
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
name|in
operator|.
name|readLine
argument_list|()
decl_stmt|;
if|if
condition|(
name|line
operator|==
literal|null
condition|)
block|{
break|break;
block|}
name|buffer
operator|.
name|append
argument_list|(
name|line
argument_list|)
expr_stmt|;
name|buffer
operator|.
name|append
argument_list|(
name|File
operator|.
name|separator
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

