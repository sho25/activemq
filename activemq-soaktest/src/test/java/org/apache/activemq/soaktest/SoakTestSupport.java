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
name|soaktest
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
name|tool
operator|.
name|Producer
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
name|tool
operator|.
name|Consumer
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
name|junit
operator|.
name|framework
operator|.
name|TestCase
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

begin_class
specifier|public
class|class
name|SoakTestSupport
extends|extends
name|TestCase
block|{
specifier|private
specifier|static
specifier|final
name|Log
name|log
init|=
name|LogFactory
operator|.
name|getLog
argument_list|(
name|SoakTestSupport
operator|.
name|class
argument_list|)
decl_stmt|;
specifier|protected
name|BrokerService
name|broker
decl_stmt|;
specifier|protected
name|String
name|brokerURL
init|=
literal|"tcp://localhost:61616"
decl_stmt|;
specifier|protected
name|int
name|consumerCount
init|=
literal|1
decl_stmt|;
specifier|protected
name|int
name|producerCount
init|=
literal|1
decl_stmt|;
specifier|protected
name|int
name|messageSize
init|=
literal|1024
decl_stmt|;
specifier|protected
name|int
name|messageCount
init|=
literal|1000
decl_stmt|;
specifier|protected
name|Producer
index|[]
name|producers
decl_stmt|;
specifier|protected
name|Consumer
index|[]
name|consumers
decl_stmt|;
specifier|protected
name|String
name|destinationName
init|=
literal|"TOOL.DEFAULT"
decl_stmt|;
specifier|protected
name|Message
name|payload
decl_stmt|;
specifier|protected
name|ConnectionFactory
name|connectionFactory
decl_stmt|;
specifier|protected
name|Destination
name|destination
decl_stmt|;
specifier|protected
name|boolean
name|createConnectionPerClient
init|=
literal|true
decl_stmt|;
specifier|protected
name|boolean
name|topic
init|=
literal|false
decl_stmt|;
specifier|protected
name|boolean
name|transacted
init|=
literal|false
decl_stmt|;
specifier|protected
name|boolean
name|durable
init|=
literal|true
decl_stmt|;
specifier|protected
name|boolean
name|useEmbeddedBroker
init|=
literal|true
decl_stmt|;
specifier|protected
name|boolean
name|keepOnRunning
init|=
literal|true
decl_stmt|;
specifier|protected
name|int
name|duration
init|=
literal|0
decl_stmt|;
comment|//duration in minutes
specifier|protected
name|boolean
name|useConsumerListener
init|=
literal|true
decl_stmt|;
specifier|protected
name|Consumer
name|allMessagesList
init|=
operator|new
name|Consumer
argument_list|()
decl_stmt|;
specifier|private
name|String
name|dataFileRoot
init|=
literal|"activemq-data"
decl_stmt|;
specifier|protected
name|void
name|setUp
parameter_list|()
throws|throws
name|Exception
block|{
comment|//clean up db store
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
if|if
condition|(
name|useEmbeddedBroker
condition|)
block|{
if|if
condition|(
name|broker
operator|==
literal|null
condition|)
block|{
name|broker
operator|=
name|createBroker
argument_list|()
expr_stmt|;
block|}
block|}
name|connectionFactory
operator|=
name|createConnectionFactory
argument_list|()
expr_stmt|;
name|Connection
name|con
init|=
name|connectionFactory
operator|.
name|createConnection
argument_list|()
decl_stmt|;
name|Session
name|session
init|=
name|con
operator|.
name|createSession
argument_list|(
name|transacted
argument_list|,
name|Session
operator|.
name|AUTO_ACKNOWLEDGE
argument_list|)
decl_stmt|;
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
name|destinationName
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|destination
operator|=
name|session
operator|.
name|createQueue
argument_list|(
name|destinationName
argument_list|)
expr_stmt|;
block|}
name|createPayload
argument_list|(
name|session
argument_list|)
expr_stmt|;
name|con
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
specifier|protected
name|void
name|createPayload
parameter_list|(
name|Session
name|session
parameter_list|)
throws|throws
name|JMSException
block|{
name|byte
index|[]
name|array
init|=
operator|new
name|byte
index|[
name|messageSize
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
name|array
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|array
index|[
name|i
index|]
operator|=
operator|(
name|byte
operator|)
name|i
expr_stmt|;
block|}
name|BytesMessage
name|bystePayload
init|=
name|session
operator|.
name|createBytesMessage
argument_list|()
decl_stmt|;
name|bystePayload
operator|.
name|writeBytes
argument_list|(
name|array
argument_list|)
expr_stmt|;
name|payload
operator|=
operator|(
name|Message
operator|)
name|bystePayload
expr_stmt|;
block|}
specifier|protected
name|void
name|createProducers
parameter_list|()
throws|throws
name|JMSException
block|{
name|producers
operator|=
operator|new
name|Producer
index|[
name|producerCount
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
name|producerCount
condition|;
name|i
operator|++
control|)
block|{
name|producers
index|[
name|i
index|]
operator|=
operator|new
name|Producer
argument_list|(
name|connectionFactory
argument_list|,
name|destination
argument_list|)
expr_stmt|;
if|if
condition|(
name|durable
condition|)
block|{
name|producers
index|[
name|i
index|]
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
name|producers
index|[
name|i
index|]
operator|.
name|setDeliveryMode
argument_list|(
name|DeliveryMode
operator|.
name|NON_PERSISTENT
argument_list|)
expr_stmt|;
block|}
name|producers
index|[
name|i
index|]
operator|.
name|start
argument_list|()
expr_stmt|;
block|}
block|}
specifier|protected
name|void
name|createConsumers
parameter_list|()
throws|throws
name|JMSException
block|{
name|consumers
operator|=
operator|new
name|Consumer
index|[
name|consumerCount
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
name|consumerCount
condition|;
name|i
operator|++
control|)
block|{
name|consumers
index|[
name|i
index|]
operator|=
operator|new
name|Consumer
argument_list|(
name|connectionFactory
argument_list|,
name|destination
argument_list|)
expr_stmt|;
name|consumers
index|[
name|i
index|]
operator|.
name|setParent
argument_list|(
name|allMessagesList
argument_list|)
expr_stmt|;
if|if
condition|(
name|useConsumerListener
condition|)
block|{
name|consumers
index|[
name|i
index|]
operator|.
name|start
argument_list|()
expr_stmt|;
block|}
block|}
block|}
specifier|protected
name|ActiveMQConnectionFactory
name|createConnectionFactory
parameter_list|()
throws|throws
name|JMSException
block|{
if|if
condition|(
name|useEmbeddedBroker
condition|)
block|{
return|return
operator|new
name|ActiveMQConnectionFactory
argument_list|(
literal|"vm://localhost"
argument_list|)
return|;
block|}
else|else
block|{
return|return
operator|new
name|ActiveMQConnectionFactory
argument_list|(
name|brokerURL
argument_list|)
return|;
block|}
block|}
specifier|protected
name|BrokerService
name|createBroker
parameter_list|()
throws|throws
name|Exception
block|{
name|BrokerService
name|broker
init|=
operator|new
name|BrokerService
argument_list|()
decl_stmt|;
name|configureBroker
argument_list|(
name|broker
argument_list|)
expr_stmt|;
name|broker
operator|.
name|start
argument_list|()
expr_stmt|;
return|return
name|broker
return|;
block|}
specifier|protected
name|void
name|configureBroker
parameter_list|(
name|BrokerService
name|broker
parameter_list|)
throws|throws
name|Exception
block|{
name|broker
operator|.
name|addConnector
argument_list|(
literal|"vm://localhost"
argument_list|)
expr_stmt|;
name|broker
operator|.
name|setDeleteAllMessagesOnStartup
argument_list|(
literal|true
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|startTimer
parameter_list|()
block|{
name|Thread
name|timer
init|=
operator|new
name|Thread
argument_list|(
operator|new
name|Runnable
argument_list|()
block|{
specifier|public
name|void
name|run
parameter_list|()
block|{
try|try
block|{
name|Thread
operator|.
name|sleep
argument_list|(
name|duration
operator|*
literal|60
operator|*
literal|1000
argument_list|)
expr_stmt|;
name|keepOnRunning
operator|=
literal|true
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|InterruptedException
name|e
parameter_list|)
block|{                  }
finally|finally
block|{                  }
block|}
block|}
argument_list|,
literal|"TimerThread"
argument_list|)
decl_stmt|;
name|log
operator|.
name|info
argument_list|(
literal|"Starting timer thread... Duration :"
operator|+
name|duration
operator|+
literal|" minutes"
argument_list|)
expr_stmt|;
name|timer
operator|.
name|start
argument_list|()
expr_stmt|;
block|}
specifier|protected
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
block|}
end_class

end_unit

