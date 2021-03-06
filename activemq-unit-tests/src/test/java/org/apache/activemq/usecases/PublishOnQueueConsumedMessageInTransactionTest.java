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
name|usecases
package|;
end_package

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
name|util
operator|.
name|ArrayList
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Collections
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|List
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
name|ObjectMessage
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
name|activemq
operator|.
name|util
operator|.
name|IOHelper
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

begin_class
specifier|public
specifier|final
class|class
name|PublishOnQueueConsumedMessageInTransactionTest
extends|extends
name|TestCase
implements|implements
name|MessageListener
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
name|PublishOnQueueConsumedMessageInTransactionTest
operator|.
name|class
argument_list|)
decl_stmt|;
specifier|private
name|Session
name|producerSession
decl_stmt|;
specifier|private
name|Session
name|consumerSession
decl_stmt|;
specifier|private
name|Destination
name|queue
decl_stmt|;
specifier|private
name|ActiveMQConnectionFactory
name|factory
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
name|Connection
name|connection
decl_stmt|;
specifier|private
name|ObjectMessage
name|objectMessage
decl_stmt|;
specifier|private
name|List
argument_list|<
name|Message
argument_list|>
name|messages
init|=
name|createConcurrentList
argument_list|()
decl_stmt|;
specifier|private
specifier|final
name|Object
name|lock
init|=
operator|new
name|Object
argument_list|()
decl_stmt|;
specifier|private
name|String
index|[]
name|data
decl_stmt|;
specifier|private
name|String
name|dataFileRoot
init|=
name|IOHelper
operator|.
name|getDefaultDataDirectory
argument_list|()
decl_stmt|;
specifier|private
name|int
name|messageCount
init|=
literal|3
decl_stmt|;
specifier|private
name|String
name|url
init|=
literal|"vm://localhost"
decl_stmt|;
comment|// Invalid acknowledgment warning can be viewed on the console of a remote
comment|// broker
comment|// The warning message is not thrown back to the client
comment|// private String url = "tcp://localhost:61616";
specifier|protected
name|void
name|setUp
parameter_list|()
throws|throws
name|Exception
block|{
name|File
name|dataFile
init|=
operator|new
name|File
argument_list|(
name|dataFileRoot
argument_list|)
decl_stmt|;
name|recursiveDelete
argument_list|(
name|dataFile
argument_list|)
expr_stmt|;
try|try
block|{
name|factory
operator|=
operator|new
name|ActiveMQConnectionFactory
argument_list|(
name|url
argument_list|)
expr_stmt|;
name|connection
operator|=
name|factory
operator|.
name|createConnection
argument_list|()
expr_stmt|;
name|producerSession
operator|=
name|connection
operator|.
name|createSession
argument_list|(
literal|true
argument_list|,
name|Session
operator|.
name|SESSION_TRANSACTED
argument_list|)
expr_stmt|;
name|consumerSession
operator|=
name|connection
operator|.
name|createSession
argument_list|(
literal|true
argument_list|,
name|Session
operator|.
name|SESSION_TRANSACTED
argument_list|)
expr_stmt|;
name|queue
operator|=
operator|new
name|ActiveMQQueue
argument_list|(
literal|"FOO.BAR"
argument_list|)
expr_stmt|;
name|data
operator|=
operator|new
name|String
index|[
name|messageCount
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
name|messageCount
condition|;
name|i
operator|++
control|)
block|{
name|data
index|[
name|i
index|]
operator|=
literal|"Message : "
operator|+
name|i
expr_stmt|;
block|}
block|}
catch|catch
parameter_list|(
name|JMSException
name|je
parameter_list|)
block|{
name|fail
argument_list|(
literal|"Error setting up connection : "
operator|+
name|je
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
specifier|public
name|void
name|testSendReceive
parameter_list|()
throws|throws
name|Exception
block|{
name|sendMessage
argument_list|()
expr_stmt|;
name|connection
operator|.
name|start
argument_list|()
expr_stmt|;
name|consumer
operator|=
name|consumerSession
operator|.
name|createConsumer
argument_list|(
name|queue
argument_list|)
expr_stmt|;
name|consumer
operator|.
name|setMessageListener
argument_list|(
name|this
argument_list|)
expr_stmt|;
name|waitForMessagesToBeDelivered
argument_list|()
expr_stmt|;
name|assertEquals
argument_list|(
literal|"Messages received doesn't equal messages sent"
argument_list|,
name|messages
operator|.
name|size
argument_list|()
argument_list|,
name|data
operator|.
name|length
argument_list|)
expr_stmt|;
block|}
specifier|protected
name|void
name|sendMessage
parameter_list|()
throws|throws
name|JMSException
block|{
name|messages
operator|.
name|clear
argument_list|()
expr_stmt|;
try|try
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
name|data
operator|.
name|length
condition|;
operator|++
name|i
control|)
block|{
name|producer
operator|=
name|producerSession
operator|.
name|createProducer
argument_list|(
name|queue
argument_list|)
expr_stmt|;
name|objectMessage
operator|=
name|producerSession
operator|.
name|createObjectMessage
argument_list|(
name|data
index|[
name|i
index|]
argument_list|)
expr_stmt|;
name|producer
operator|.
name|send
argument_list|(
name|objectMessage
argument_list|)
expr_stmt|;
name|producerSession
operator|.
name|commit
argument_list|()
expr_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"sending message :"
operator|+
name|objectMessage
argument_list|)
expr_stmt|;
block|}
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
if|if
condition|(
name|producerSession
operator|!=
literal|null
condition|)
block|{
name|producerSession
operator|.
name|rollback
argument_list|()
expr_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"rollback"
argument_list|)
expr_stmt|;
name|producerSession
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
name|e
operator|.
name|printStackTrace
argument_list|()
expr_stmt|;
block|}
block|}
specifier|public
specifier|synchronized
name|void
name|onMessage
parameter_list|(
name|Message
name|m
parameter_list|)
block|{
try|try
block|{
name|objectMessage
operator|=
operator|(
name|ObjectMessage
operator|)
name|m
expr_stmt|;
name|consumeMessage
argument_list|(
name|objectMessage
argument_list|,
name|messages
argument_list|)
expr_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"consumer received message :"
operator|+
name|objectMessage
argument_list|)
expr_stmt|;
name|consumerSession
operator|.
name|commit
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
try|try
block|{
name|consumerSession
operator|.
name|rollback
argument_list|()
expr_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"rolled back transaction"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|JMSException
name|e1
parameter_list|)
block|{
name|LOG
operator|.
name|info
argument_list|(
name|e1
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|e1
operator|.
name|printStackTrace
argument_list|()
expr_stmt|;
block|}
name|LOG
operator|.
name|info
argument_list|(
name|e
operator|.
name|toString
argument_list|()
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
name|void
name|consumeMessage
parameter_list|(
name|Message
name|message
parameter_list|,
name|List
argument_list|<
name|Message
argument_list|>
name|messageList
parameter_list|)
block|{
name|messageList
operator|.
name|add
argument_list|(
name|message
argument_list|)
expr_stmt|;
if|if
condition|(
name|messageList
operator|.
name|size
argument_list|()
operator|>=
name|data
operator|.
name|length
condition|)
block|{
synchronized|synchronized
init|(
name|lock
init|)
block|{
name|lock
operator|.
name|notifyAll
argument_list|()
expr_stmt|;
block|}
block|}
block|}
specifier|protected
name|List
argument_list|<
name|Message
argument_list|>
name|createConcurrentList
parameter_list|()
block|{
return|return
name|Collections
operator|.
name|synchronizedList
argument_list|(
operator|new
name|ArrayList
argument_list|<
name|Message
argument_list|>
argument_list|()
argument_list|)
return|;
block|}
specifier|protected
name|void
name|waitForMessagesToBeDelivered
parameter_list|()
block|{
name|long
name|maxWaitTime
init|=
literal|5000
decl_stmt|;
name|long
name|waitTime
init|=
name|maxWaitTime
decl_stmt|;
name|long
name|start
init|=
operator|(
name|maxWaitTime
operator|<=
literal|0
operator|)
condition|?
literal|0
else|:
name|System
operator|.
name|currentTimeMillis
argument_list|()
decl_stmt|;
synchronized|synchronized
init|(
name|lock
init|)
block|{
while|while
condition|(
name|messages
operator|.
name|size
argument_list|()
operator|<=
name|data
operator|.
name|length
operator|&&
name|waitTime
operator|>=
literal|0
condition|)
block|{
try|try
block|{
name|lock
operator|.
name|wait
argument_list|(
literal|200
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|InterruptedException
name|e
parameter_list|)
block|{
name|e
operator|.
name|printStackTrace
argument_list|()
expr_stmt|;
block|}
name|waitTime
operator|=
name|maxWaitTime
operator|-
operator|(
name|System
operator|.
name|currentTimeMillis
argument_list|()
operator|-
name|start
operator|)
expr_stmt|;
block|}
block|}
block|}
specifier|protected
specifier|static
name|void
name|recursiveDelete
parameter_list|(
name|File
name|file
parameter_list|)
block|{
if|if
condition|(
name|file
operator|.
name|isDirectory
argument_list|()
condition|)
block|{
name|File
index|[]
name|files
init|=
name|file
operator|.
name|listFiles
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
name|files
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|recursiveDelete
argument_list|(
name|files
index|[
name|i
index|]
argument_list|)
expr_stmt|;
block|}
block|}
name|file
operator|.
name|delete
argument_list|()
expr_stmt|;
block|}
specifier|protected
name|void
name|tearDown
parameter_list|()
throws|throws
name|Exception
block|{
if|if
condition|(
name|connection
operator|!=
literal|null
condition|)
block|{
name|connection
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
name|super
operator|.
name|tearDown
argument_list|()
expr_stmt|;
block|}
block|}
end_class

end_unit

