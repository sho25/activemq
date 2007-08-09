begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *      http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
package|package
name|org
operator|.
name|apache
operator|.
name|activemq
operator|.
name|bugs
package|;
end_package

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|ByteArrayOutputStream
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

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|InputStream
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|OutputStream
import|;
end_import

begin_import
import|import
name|java
operator|.
name|net
operator|.
name|Socket
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
name|junit
operator|.
name|framework
operator|.
name|TestCase
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
name|broker
operator|.
name|BrokerService
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
name|ActiveMQQueue
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|commons
operator|.
name|logging
operator|.
name|Log
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|commons
operator|.
name|logging
operator|.
name|LogFactory
import|;
end_import

begin_class
specifier|public
class|class
name|SlowConsumerTest
extends|extends
name|TestCase
block|{
specifier|private
specifier|static
specifier|final
name|Log
name|LOG
init|=
name|LogFactory
operator|.
name|getLog
argument_list|(
name|SlowConsumerTest
operator|.
name|class
argument_list|)
decl_stmt|;
specifier|private
name|Socket
name|stompSocket
decl_stmt|;
specifier|private
name|ByteArrayOutputStream
name|inputBuffer
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|int
name|MESSAGES_COUNT
init|=
literal|10000
decl_stmt|;
specifier|private
name|int
name|messagesCount
decl_stmt|;
specifier|protected
name|int
name|messageLogFrequency
init|=
literal|2500
decl_stmt|;
specifier|protected
name|long
name|messageReceiveTimeout
init|=
literal|10000L
decl_stmt|;
comment|/**      * @param args      * @throws Exception      */
specifier|public
name|void
name|testRemoveSubscriber
parameter_list|()
throws|throws
name|Exception
block|{
specifier|final
name|BrokerService
name|broker
init|=
operator|new
name|BrokerService
argument_list|()
decl_stmt|;
name|broker
operator|.
name|setPersistent
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|broker
operator|.
name|setUseJmx
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|broker
operator|.
name|setDeleteAllMessagesOnStartup
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|broker
operator|.
name|addConnector
argument_list|(
literal|"tcp://localhost:61616"
argument_list|)
operator|.
name|setName
argument_list|(
literal|"Default"
argument_list|)
expr_stmt|;
name|broker
operator|.
name|start
argument_list|()
expr_stmt|;
name|ActiveMQConnectionFactory
name|factory
init|=
operator|new
name|ActiveMQConnectionFactory
argument_list|(
literal|"tcp://localhost:61616"
argument_list|)
decl_stmt|;
specifier|final
name|Connection
name|connection
init|=
name|factory
operator|.
name|createConnection
argument_list|()
decl_stmt|;
name|connection
operator|.
name|start
argument_list|()
expr_stmt|;
name|Thread
name|producingThread
init|=
operator|new
name|Thread
argument_list|(
literal|"Producing thread"
argument_list|)
block|{
specifier|public
name|void
name|run
parameter_list|()
block|{
try|try
block|{
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
name|MessageProducer
name|producer
init|=
name|session
operator|.
name|createProducer
argument_list|(
operator|new
name|ActiveMQQueue
argument_list|(
name|getDestinationName
argument_list|()
argument_list|)
argument_list|)
decl_stmt|;
for|for
control|(
name|int
name|idx
init|=
literal|0
init|;
name|idx
operator|<
name|MESSAGES_COUNT
condition|;
operator|++
name|idx
control|)
block|{
name|Message
name|message
init|=
name|session
operator|.
name|createTextMessage
argument_list|(
literal|""
operator|+
name|idx
argument_list|)
decl_stmt|;
name|producer
operator|.
name|send
argument_list|(
name|message
argument_list|)
expr_stmt|;
name|LOG
operator|.
name|debug
argument_list|(
literal|"Sending: "
operator|+
name|idx
argument_list|)
expr_stmt|;
block|}
name|producer
operator|.
name|close
argument_list|()
expr_stmt|;
name|session
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Throwable
name|ex
parameter_list|)
block|{
name|ex
operator|.
name|printStackTrace
argument_list|()
expr_stmt|;
block|}
block|}
block|}
decl_stmt|;
name|producingThread
operator|.
name|setPriority
argument_list|(
name|Thread
operator|.
name|MAX_PRIORITY
argument_list|)
expr_stmt|;
name|producingThread
operator|.
name|start
argument_list|()
expr_stmt|;
name|Thread
operator|.
name|sleep
argument_list|(
literal|1000
argument_list|)
expr_stmt|;
name|Thread
name|consumingThread
init|=
operator|new
name|Thread
argument_list|(
literal|"Consuming thread"
argument_list|)
block|{
specifier|public
name|void
name|run
parameter_list|()
block|{
try|try
block|{
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
name|CLIENT_ACKNOWLEDGE
argument_list|)
decl_stmt|;
name|MessageConsumer
name|consumer
init|=
name|session
operator|.
name|createConsumer
argument_list|(
operator|new
name|ActiveMQQueue
argument_list|(
name|getDestinationName
argument_list|()
argument_list|)
argument_list|)
decl_stmt|;
name|int
name|diff
init|=
literal|0
decl_stmt|;
while|while
condition|(
name|messagesCount
operator|!=
name|MESSAGES_COUNT
condition|)
block|{
name|Message
name|msg
init|=
name|consumer
operator|.
name|receive
argument_list|(
name|messageReceiveTimeout
argument_list|)
decl_stmt|;
if|if
condition|(
name|msg
operator|==
literal|null
condition|)
block|{
name|LOG
operator|.
name|warn
argument_list|(
literal|"Got null message at count: "
operator|+
name|messagesCount
operator|+
literal|". Continuing..."
argument_list|)
expr_stmt|;
break|break;
block|}
name|String
name|text
init|=
operator|(
operator|(
name|TextMessage
operator|)
name|msg
operator|)
operator|.
name|getText
argument_list|()
decl_stmt|;
name|int
name|currentMsgIdx
init|=
name|Integer
operator|.
name|parseInt
argument_list|(
name|text
argument_list|)
decl_stmt|;
name|LOG
operator|.
name|debug
argument_list|(
literal|"Received: "
operator|+
name|text
operator|+
literal|" messageCount: "
operator|+
name|messagesCount
argument_list|)
expr_stmt|;
name|msg
operator|.
name|acknowledge
argument_list|()
expr_stmt|;
if|if
condition|(
operator|(
name|messagesCount
operator|+
name|diff
operator|)
operator|!=
name|currentMsgIdx
condition|)
block|{
name|LOG
operator|.
name|debug
argument_list|(
literal|"Message(s) skipped!! Should be message no.: "
operator|+
name|messagesCount
operator|+
literal|" but got: "
operator|+
name|currentMsgIdx
argument_list|)
expr_stmt|;
name|diff
operator|=
name|currentMsgIdx
operator|-
name|messagesCount
expr_stmt|;
block|}
operator|++
name|messagesCount
expr_stmt|;
if|if
condition|(
name|messagesCount
operator|%
name|messageLogFrequency
operator|==
literal|0
condition|)
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"Received: "
operator|+
name|messagesCount
operator|+
literal|" messages so far"
argument_list|)
expr_stmt|;
block|}
comment|// Thread.sleep(70);
block|}
block|}
catch|catch
parameter_list|(
name|Throwable
name|ex
parameter_list|)
block|{
name|ex
operator|.
name|printStackTrace
argument_list|()
expr_stmt|;
block|}
block|}
block|}
decl_stmt|;
name|consumingThread
operator|.
name|start
argument_list|()
expr_stmt|;
name|consumingThread
operator|.
name|join
argument_list|()
expr_stmt|;
name|assertEquals
argument_list|(
name|MESSAGES_COUNT
argument_list|,
name|messagesCount
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|sendFrame
parameter_list|(
name|String
name|data
parameter_list|)
throws|throws
name|Exception
block|{
name|byte
index|[]
name|bytes
init|=
name|data
operator|.
name|getBytes
argument_list|(
literal|"UTF-8"
argument_list|)
decl_stmt|;
name|OutputStream
name|outputStream
init|=
name|stompSocket
operator|.
name|getOutputStream
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
name|bytes
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|outputStream
operator|.
name|write
argument_list|(
name|bytes
index|[
name|i
index|]
argument_list|)
expr_stmt|;
block|}
name|outputStream
operator|.
name|flush
argument_list|()
expr_stmt|;
block|}
specifier|public
name|String
name|receiveFrame
parameter_list|(
name|long
name|timeOut
parameter_list|)
throws|throws
name|Exception
block|{
name|stompSocket
operator|.
name|setSoTimeout
argument_list|(
operator|(
name|int
operator|)
name|timeOut
argument_list|)
expr_stmt|;
name|InputStream
name|is
init|=
name|stompSocket
operator|.
name|getInputStream
argument_list|()
decl_stmt|;
name|int
name|c
init|=
literal|0
decl_stmt|;
for|for
control|(
init|;
condition|;
control|)
block|{
name|c
operator|=
name|is
operator|.
name|read
argument_list|()
expr_stmt|;
if|if
condition|(
name|c
operator|<
literal|0
condition|)
block|{
throw|throw
operator|new
name|IOException
argument_list|(
literal|"socket closed."
argument_list|)
throw|;
block|}
elseif|else
if|if
condition|(
name|c
operator|==
literal|0
condition|)
block|{
name|c
operator|=
name|is
operator|.
name|read
argument_list|()
expr_stmt|;
name|byte
index|[]
name|ba
init|=
name|inputBuffer
operator|.
name|toByteArray
argument_list|()
decl_stmt|;
name|inputBuffer
operator|.
name|reset
argument_list|()
expr_stmt|;
return|return
operator|new
name|String
argument_list|(
name|ba
argument_list|,
literal|"UTF-8"
argument_list|)
return|;
block|}
else|else
block|{
name|inputBuffer
operator|.
name|write
argument_list|(
name|c
argument_list|)
expr_stmt|;
block|}
block|}
block|}
specifier|protected
name|String
name|getDestinationName
parameter_list|()
block|{
return|return
name|getClass
argument_list|()
operator|.
name|getName
argument_list|()
operator|+
literal|"."
operator|+
name|getName
argument_list|()
return|;
block|}
block|}
end_class

end_unit

