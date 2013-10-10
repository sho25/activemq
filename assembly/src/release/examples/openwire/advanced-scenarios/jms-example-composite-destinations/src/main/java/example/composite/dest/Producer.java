begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  * http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
package|package
name|example
operator|.
name|composite
operator|.
name|dest
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
name|javax
operator|.
name|jms
operator|.
name|*
import|;
end_import

begin_comment
comment|/**  * @author<a href="http://www.christianposta.com/blog">Christian Posta</a>  */
end_comment

begin_class
specifier|public
class|class
name|Producer
block|{
specifier|private
specifier|static
specifier|final
name|String
name|BROKER_URL
init|=
literal|"tcp://localhost:61616"
decl_stmt|;
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
name|int
name|NUM_MESSAGES_TO_SEND
init|=
literal|100
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|long
name|DELAY
init|=
literal|100
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
name|connection
operator|=
name|connectionFactory
operator|.
name|createConnection
argument_list|()
expr_stmt|;
name|connection
operator|.
name|start
argument_list|()
expr_stmt|;
name|Session
name|session
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
name|Destination
name|destination
init|=
name|session
operator|.
name|createQueue
argument_list|(
literal|"test-queue,test-queue-foo,test-queue-bar,topic://test-topic-foo"
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
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|NUM_MESSAGES_TO_SEND
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
literal|"Message #"
operator|+
name|i
argument_list|)
decl_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"Sending message #"
operator|+
name|i
argument_list|)
expr_stmt|;
name|producer
operator|.
name|send
argument_list|(
name|message
argument_list|)
expr_stmt|;
name|Thread
operator|.
name|sleep
argument_list|(
name|DELAY
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
literal|"Could not close an open connection..."
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|}
block|}
end_class

end_unit

