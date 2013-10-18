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
name|javax
operator|.
name|jms
operator|.
name|BytesMessage
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
name|javax
operator|.
name|jms
operator|.
name|Topic
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

begin_comment
comment|/**  * Use in conjunction with TopicListener to test the performance of ActiveMQ  * Topics.  */
end_comment

begin_class
specifier|public
class|class
name|TopicPublisher
implements|implements
name|MessageListener
block|{
specifier|private
specifier|static
specifier|final
name|char
index|[]
name|DATA
init|=
literal|"abcdefghijklmnopqrstuvwxyz"
operator|.
name|toCharArray
argument_list|()
decl_stmt|;
specifier|private
specifier|final
name|Object
name|mutex
init|=
operator|new
name|Object
argument_list|()
decl_stmt|;
specifier|private
name|Connection
name|connection
decl_stmt|;
specifier|private
name|Session
name|session
decl_stmt|;
specifier|private
name|MessageProducer
name|publisher
decl_stmt|;
specifier|private
name|Topic
name|topic
decl_stmt|;
specifier|private
name|Topic
name|control
decl_stmt|;
specifier|private
name|String
name|url
init|=
literal|"tcp://localhost:61616"
decl_stmt|;
specifier|private
name|int
name|size
init|=
literal|256
decl_stmt|;
specifier|private
name|int
name|subscribers
init|=
literal|1
decl_stmt|;
specifier|private
name|int
name|remaining
decl_stmt|;
specifier|private
name|int
name|messages
init|=
literal|10000
decl_stmt|;
specifier|private
name|long
name|delay
decl_stmt|;
specifier|private
name|int
name|batch
init|=
literal|2000
decl_stmt|;
specifier|private
name|byte
index|[]
name|payload
decl_stmt|;
specifier|public
specifier|static
name|void
name|main
parameter_list|(
name|String
index|[]
name|argv
parameter_list|)
throws|throws
name|Exception
block|{
name|TopicPublisher
name|p
init|=
operator|new
name|TopicPublisher
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
name|p
argument_list|,
name|argv
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
name|p
operator|.
name|run
argument_list|()
expr_stmt|;
block|}
specifier|private
name|void
name|run
parameter_list|()
throws|throws
name|Exception
block|{
name|ActiveMQConnectionFactory
name|factory
init|=
operator|new
name|ActiveMQConnectionFactory
argument_list|(
name|url
argument_list|)
decl_stmt|;
name|connection
operator|=
name|factory
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
name|topic
operator|=
name|session
operator|.
name|createTopic
argument_list|(
literal|"topictest.messages"
argument_list|)
expr_stmt|;
name|control
operator|=
name|session
operator|.
name|createTopic
argument_list|(
literal|"topictest.control"
argument_list|)
expr_stmt|;
name|publisher
operator|=
name|session
operator|.
name|createProducer
argument_list|(
name|topic
argument_list|)
expr_stmt|;
name|publisher
operator|.
name|setDeliveryMode
argument_list|(
name|DeliveryMode
operator|.
name|NON_PERSISTENT
argument_list|)
expr_stmt|;
name|payload
operator|=
operator|new
name|byte
index|[
name|size
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
name|size
condition|;
name|i
operator|++
control|)
block|{
name|payload
index|[
name|i
index|]
operator|=
operator|(
name|byte
operator|)
name|DATA
index|[
name|i
operator|%
name|DATA
operator|.
name|length
index|]
expr_stmt|;
block|}
name|session
operator|.
name|createConsumer
argument_list|(
name|control
argument_list|)
operator|.
name|setMessageListener
argument_list|(
name|this
argument_list|)
expr_stmt|;
name|connection
operator|.
name|start
argument_list|()
expr_stmt|;
name|long
index|[]
name|times
init|=
operator|new
name|long
index|[
name|batch
index|]
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
name|batch
condition|;
name|i
operator|++
control|)
block|{
if|if
condition|(
name|i
operator|>
literal|0
condition|)
block|{
name|Thread
operator|.
name|sleep
argument_list|(
name|delay
operator|*
literal|1000
argument_list|)
expr_stmt|;
block|}
name|times
index|[
name|i
index|]
operator|=
name|batch
argument_list|(
name|messages
argument_list|)
expr_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"Batch "
operator|+
operator|(
name|i
operator|+
literal|1
operator|)
operator|+
literal|" of "
operator|+
name|batch
operator|+
literal|" completed in "
operator|+
name|times
index|[
name|i
index|]
operator|+
literal|" ms."
argument_list|)
expr_stmt|;
block|}
name|long
name|min
init|=
name|min
argument_list|(
name|times
argument_list|)
decl_stmt|;
name|long
name|max
init|=
name|max
argument_list|(
name|times
argument_list|)
decl_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"min: "
operator|+
name|min
operator|+
literal|", max: "
operator|+
name|max
operator|+
literal|" avg: "
operator|+
name|avg
argument_list|(
name|times
argument_list|,
name|min
argument_list|,
name|max
argument_list|)
argument_list|)
expr_stmt|;
comment|// request shutdown
name|publisher
operator|.
name|send
argument_list|(
name|session
operator|.
name|createTextMessage
argument_list|(
literal|"SHUTDOWN"
argument_list|)
argument_list|)
expr_stmt|;
name|connection
operator|.
name|stop
argument_list|()
expr_stmt|;
name|connection
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
specifier|private
name|long
name|batch
parameter_list|(
name|int
name|msgCount
parameter_list|)
throws|throws
name|Exception
block|{
name|long
name|start
init|=
name|System
operator|.
name|currentTimeMillis
argument_list|()
decl_stmt|;
name|remaining
operator|=
name|subscribers
expr_stmt|;
name|publish
argument_list|()
expr_stmt|;
name|waitForCompletion
argument_list|()
expr_stmt|;
return|return
name|System
operator|.
name|currentTimeMillis
argument_list|()
operator|-
name|start
return|;
block|}
specifier|private
name|void
name|publish
parameter_list|()
throws|throws
name|Exception
block|{
comment|// send events
name|BytesMessage
name|msg
init|=
name|session
operator|.
name|createBytesMessage
argument_list|()
decl_stmt|;
name|msg
operator|.
name|writeBytes
argument_list|(
name|payload
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
name|messages
condition|;
name|i
operator|++
control|)
block|{
name|publisher
operator|.
name|send
argument_list|(
name|msg
argument_list|)
expr_stmt|;
if|if
condition|(
operator|(
name|i
operator|+
literal|1
operator|)
operator|%
literal|1000
operator|==
literal|0
condition|)
block|{
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"Sent "
operator|+
operator|(
name|i
operator|+
literal|1
operator|)
operator|+
literal|" messages"
argument_list|)
expr_stmt|;
block|}
block|}
comment|// request report
name|publisher
operator|.
name|send
argument_list|(
name|session
operator|.
name|createTextMessage
argument_list|(
literal|"REPORT"
argument_list|)
argument_list|)
expr_stmt|;
block|}
specifier|private
name|void
name|waitForCompletion
parameter_list|()
throws|throws
name|Exception
block|{
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"Waiting for completion..."
argument_list|)
expr_stmt|;
synchronized|synchronized
init|(
name|mutex
init|)
block|{
while|while
condition|(
name|remaining
operator|>
literal|0
condition|)
block|{
name|mutex
operator|.
name|wait
argument_list|()
expr_stmt|;
block|}
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
synchronized|synchronized
init|(
name|mutex
init|)
block|{
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"Received report "
operator|+
name|getReport
argument_list|(
name|message
argument_list|)
operator|+
literal|" "
operator|+
operator|--
name|remaining
operator|+
literal|" remaining"
argument_list|)
expr_stmt|;
if|if
condition|(
name|remaining
operator|==
literal|0
condition|)
block|{
name|mutex
operator|.
name|notify
argument_list|()
expr_stmt|;
block|}
block|}
block|}
name|Object
name|getReport
parameter_list|(
name|Message
name|m
parameter_list|)
block|{
try|try
block|{
return|return
operator|(
operator|(
name|TextMessage
operator|)
name|m
operator|)
operator|.
name|getText
argument_list|()
return|;
block|}
catch|catch
parameter_list|(
name|JMSException
name|e
parameter_list|)
block|{
name|e
operator|.
name|printStackTrace
argument_list|(
name|System
operator|.
name|out
argument_list|)
expr_stmt|;
return|return
name|e
operator|.
name|toString
argument_list|()
return|;
block|}
block|}
specifier|static
name|long
name|min
parameter_list|(
name|long
index|[]
name|times
parameter_list|)
block|{
name|long
name|min
init|=
name|times
operator|.
name|length
operator|>
literal|0
condition|?
name|times
index|[
literal|0
index|]
else|:
literal|0
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
name|times
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|min
operator|=
name|Math
operator|.
name|min
argument_list|(
name|min
argument_list|,
name|times
index|[
name|i
index|]
argument_list|)
expr_stmt|;
block|}
return|return
name|min
return|;
block|}
specifier|static
name|long
name|max
parameter_list|(
name|long
index|[]
name|times
parameter_list|)
block|{
name|long
name|max
init|=
name|times
operator|.
name|length
operator|>
literal|0
condition|?
name|times
index|[
literal|0
index|]
else|:
literal|0
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
name|times
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|max
operator|=
name|Math
operator|.
name|max
argument_list|(
name|max
argument_list|,
name|times
index|[
name|i
index|]
argument_list|)
expr_stmt|;
block|}
return|return
name|max
return|;
block|}
specifier|static
name|long
name|avg
parameter_list|(
name|long
index|[]
name|times
parameter_list|,
name|long
name|min
parameter_list|,
name|long
name|max
parameter_list|)
block|{
name|long
name|sum
init|=
literal|0
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
name|times
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|sum
operator|+=
name|times
index|[
name|i
index|]
expr_stmt|;
block|}
name|sum
operator|-=
name|min
expr_stmt|;
name|sum
operator|-=
name|max
expr_stmt|;
return|return
name|sum
operator|/
name|times
operator|.
name|length
operator|-
literal|2
return|;
block|}
specifier|public
name|void
name|setBatch
parameter_list|(
name|int
name|batch
parameter_list|)
block|{
name|this
operator|.
name|batch
operator|=
name|batch
expr_stmt|;
block|}
specifier|public
name|void
name|setDelay
parameter_list|(
name|long
name|delay
parameter_list|)
block|{
name|this
operator|.
name|delay
operator|=
name|delay
expr_stmt|;
block|}
specifier|public
name|void
name|setMessages
parameter_list|(
name|int
name|messages
parameter_list|)
block|{
name|this
operator|.
name|messages
operator|=
name|messages
expr_stmt|;
block|}
specifier|public
name|void
name|setSize
parameter_list|(
name|int
name|size
parameter_list|)
block|{
name|this
operator|.
name|size
operator|=
name|size
expr_stmt|;
block|}
specifier|public
name|void
name|setSubscribers
parameter_list|(
name|int
name|subscribers
parameter_list|)
block|{
name|this
operator|.
name|subscribers
operator|=
name|subscribers
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
block|}
end_class

end_unit
