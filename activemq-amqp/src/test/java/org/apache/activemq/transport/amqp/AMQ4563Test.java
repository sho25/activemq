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
name|transport
operator|.
name|amqp
package|;
end_package

begin_import
import|import static
name|org
operator|.
name|junit
operator|.
name|Assert
operator|.
name|assertEquals
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|junit
operator|.
name|Assert
operator|.
name|assertNotNull
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|junit
operator|.
name|Assert
operator|.
name|assertTrue
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
name|ConnectionFactory
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
name|MessageProducer
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|jms
operator|.
name|Queue
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
name|jmx
operator|.
name|QueueViewMBean
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
name|transport
operator|.
name|amqp
operator|.
name|joram
operator|.
name|ActiveMQAdmin
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|Test
import|;
end_import

begin_class
specifier|public
class|class
name|AMQ4563Test
extends|extends
name|AmqpTestSupport
block|{
specifier|public
specifier|static
specifier|final
name|String
name|KAHADB_DIRECTORY
init|=
literal|"./target/activemq-data/kahadb-amq4563"
decl_stmt|;
annotation|@
name|Test
argument_list|(
name|timeout
operator|=
literal|60000
argument_list|)
specifier|public
name|void
name|testMessagesAreAckedAMQProducer
parameter_list|()
throws|throws
name|Exception
block|{
name|int
name|messagesSent
init|=
literal|3
decl_stmt|;
name|ActiveMQAdmin
operator|.
name|enableJMSFrameTracing
argument_list|()
expr_stmt|;
name|assertTrue
argument_list|(
name|brokerService
operator|.
name|isPersistent
argument_list|()
argument_list|)
expr_stmt|;
name|Connection
name|connection
init|=
name|createAMQConnection
argument_list|()
decl_stmt|;
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
name|Queue
name|queue
init|=
name|session
operator|.
name|createQueue
argument_list|(
name|name
operator|.
name|getMethodName
argument_list|()
argument_list|)
decl_stmt|;
name|MessageProducer
name|producer
init|=
name|session
operator|.
name|createProducer
argument_list|(
name|queue
argument_list|)
decl_stmt|;
name|TextMessage
name|message
init|=
literal|null
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
name|messagesSent
condition|;
name|i
operator|++
control|)
block|{
name|message
operator|=
name|session
operator|.
name|createTextMessage
argument_list|()
expr_stmt|;
name|String
name|messageText
init|=
literal|"Hello "
operator|+
name|i
operator|+
literal|" sent at "
operator|+
operator|new
name|java
operator|.
name|util
operator|.
name|Date
argument_list|()
operator|.
name|toString
argument_list|()
decl_stmt|;
name|message
operator|.
name|setText
argument_list|(
name|messageText
argument_list|)
expr_stmt|;
name|LOG
operator|.
name|debug
argument_list|(
literal|">>>> Sent [{}]"
argument_list|,
name|messageText
argument_list|)
expr_stmt|;
name|producer
operator|.
name|send
argument_list|(
name|message
argument_list|)
expr_stmt|;
block|}
comment|// After the first restart we should get all messages sent above
name|restartBroker
argument_list|(
name|connection
argument_list|,
name|session
argument_list|)
expr_stmt|;
name|int
name|messagesReceived
init|=
name|readAllMessages
argument_list|(
name|name
operator|.
name|getMethodName
argument_list|()
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
name|messagesSent
argument_list|,
name|messagesReceived
argument_list|)
expr_stmt|;
comment|// This time there should be no messages on this queue
name|restartBroker
argument_list|(
name|connection
argument_list|,
name|session
argument_list|)
expr_stmt|;
name|QueueViewMBean
name|queueView
init|=
name|getProxyToQueue
argument_list|(
name|name
operator|.
name|getMethodName
argument_list|()
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|queueView
operator|.
name|getQueueSize
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
argument_list|(
name|timeout
operator|=
literal|60000
argument_list|)
specifier|public
name|void
name|testSelectingOnAMQPMessageID
parameter_list|()
throws|throws
name|Exception
block|{
name|ActiveMQAdmin
operator|.
name|enableJMSFrameTracing
argument_list|()
expr_stmt|;
name|assertTrue
argument_list|(
name|brokerService
operator|.
name|isPersistent
argument_list|()
argument_list|)
expr_stmt|;
name|Connection
name|connection
init|=
name|JMSClientContext
operator|.
name|INSTANCE
operator|.
name|createConnection
argument_list|(
name|amqpURI
argument_list|)
decl_stmt|;
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
literal|false
argument_list|,
name|Session
operator|.
name|AUTO_ACKNOWLEDGE
argument_list|)
decl_stmt|;
name|Queue
name|queue
init|=
name|session
operator|.
name|createQueue
argument_list|(
name|name
operator|.
name|getMethodName
argument_list|()
argument_list|)
decl_stmt|;
name|MessageProducer
name|p
init|=
name|session
operator|.
name|createProducer
argument_list|(
name|queue
argument_list|)
decl_stmt|;
name|TextMessage
name|message
init|=
name|session
operator|.
name|createTextMessage
argument_list|()
decl_stmt|;
name|String
name|messageText
init|=
literal|"Hello sent at "
operator|+
operator|new
name|java
operator|.
name|util
operator|.
name|Date
argument_list|()
operator|.
name|toString
argument_list|()
decl_stmt|;
name|message
operator|.
name|setText
argument_list|(
name|messageText
argument_list|)
expr_stmt|;
name|p
operator|.
name|send
argument_list|(
name|message
argument_list|)
expr_stmt|;
comment|// Restart broker.
name|restartBroker
argument_list|(
name|connection
argument_list|,
name|session
argument_list|)
expr_stmt|;
name|String
name|selector
init|=
literal|"JMSMessageID = '"
operator|+
name|message
operator|.
name|getJMSMessageID
argument_list|()
operator|+
literal|"'"
decl_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"Using selector: {}"
argument_list|,
name|selector
argument_list|)
expr_stmt|;
name|int
name|messagesReceived
init|=
name|readAllMessages
argument_list|(
name|name
operator|.
name|getMethodName
argument_list|()
argument_list|,
name|selector
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|messagesReceived
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
argument_list|(
name|timeout
operator|=
literal|60000
argument_list|)
specifier|public
name|void
name|testSelectingOnActiveMQMessageID
parameter_list|()
throws|throws
name|Exception
block|{
name|ActiveMQAdmin
operator|.
name|enableJMSFrameTracing
argument_list|()
expr_stmt|;
name|assertTrue
argument_list|(
name|brokerService
operator|.
name|isPersistent
argument_list|()
argument_list|)
expr_stmt|;
name|Connection
name|connection
init|=
name|createAMQConnection
argument_list|()
decl_stmt|;
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
name|Destination
name|destination
init|=
name|session
operator|.
name|createQueue
argument_list|(
name|name
operator|.
name|getMethodName
argument_list|()
argument_list|)
decl_stmt|;
name|MessageProducer
name|p
init|=
name|session
operator|.
name|createProducer
argument_list|(
name|destination
argument_list|)
decl_stmt|;
name|TextMessage
name|message
init|=
name|session
operator|.
name|createTextMessage
argument_list|()
decl_stmt|;
name|String
name|messageText
init|=
literal|"Hello sent at "
operator|+
operator|new
name|java
operator|.
name|util
operator|.
name|Date
argument_list|()
operator|.
name|toString
argument_list|()
decl_stmt|;
name|message
operator|.
name|setText
argument_list|(
name|messageText
argument_list|)
expr_stmt|;
name|p
operator|.
name|send
argument_list|(
name|message
argument_list|)
expr_stmt|;
comment|// Restart broker.
name|restartBroker
argument_list|(
name|connection
argument_list|,
name|session
argument_list|)
expr_stmt|;
name|String
name|selector
init|=
literal|"JMSMessageID = '"
operator|+
name|message
operator|.
name|getJMSMessageID
argument_list|()
operator|+
literal|"'"
decl_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"Using selector: {}"
argument_list|,
name|selector
argument_list|)
expr_stmt|;
name|int
name|messagesReceived
init|=
name|readAllMessages
argument_list|(
name|name
operator|.
name|getMethodName
argument_list|()
argument_list|,
name|selector
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|messagesReceived
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
argument_list|(
name|timeout
operator|=
literal|60000
argument_list|)
specifier|public
name|void
name|testMessagesAreAckedAMQPProducer
parameter_list|()
throws|throws
name|Exception
block|{
name|int
name|messagesSent
init|=
literal|3
decl_stmt|;
name|ActiveMQAdmin
operator|.
name|enableJMSFrameTracing
argument_list|()
expr_stmt|;
name|assertTrue
argument_list|(
name|brokerService
operator|.
name|isPersistent
argument_list|()
argument_list|)
expr_stmt|;
name|Connection
name|connection
init|=
name|JMSClientContext
operator|.
name|INSTANCE
operator|.
name|createConnection
argument_list|(
name|amqpURI
argument_list|)
decl_stmt|;
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
literal|false
argument_list|,
name|Session
operator|.
name|AUTO_ACKNOWLEDGE
argument_list|)
decl_stmt|;
name|Queue
name|queue
init|=
name|session
operator|.
name|createQueue
argument_list|(
name|name
operator|.
name|getMethodName
argument_list|()
argument_list|)
decl_stmt|;
name|MessageProducer
name|p
init|=
name|session
operator|.
name|createProducer
argument_list|(
name|queue
argument_list|)
decl_stmt|;
name|TextMessage
name|message
init|=
literal|null
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
name|messagesSent
condition|;
name|i
operator|++
control|)
block|{
name|message
operator|=
name|session
operator|.
name|createTextMessage
argument_list|()
expr_stmt|;
name|String
name|messageText
init|=
literal|"Hello "
operator|+
name|i
operator|+
literal|" sent at "
operator|+
operator|new
name|java
operator|.
name|util
operator|.
name|Date
argument_list|()
operator|.
name|toString
argument_list|()
decl_stmt|;
name|message
operator|.
name|setText
argument_list|(
name|messageText
argument_list|)
expr_stmt|;
name|LOG
operator|.
name|debug
argument_list|(
literal|">>>> Sent [{}]"
argument_list|,
name|messageText
argument_list|)
expr_stmt|;
name|p
operator|.
name|send
argument_list|(
name|message
argument_list|)
expr_stmt|;
block|}
comment|// After the first restart we should get all messages sent above
name|restartBroker
argument_list|(
name|connection
argument_list|,
name|session
argument_list|)
expr_stmt|;
name|int
name|messagesReceived
init|=
name|readAllMessages
argument_list|(
name|name
operator|.
name|getMethodName
argument_list|()
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
name|messagesSent
argument_list|,
name|messagesReceived
argument_list|)
expr_stmt|;
comment|// This time there should be no messages on this queue
name|restartBroker
argument_list|(
name|connection
argument_list|,
name|session
argument_list|)
expr_stmt|;
name|QueueViewMBean
name|queueView
init|=
name|getProxyToQueue
argument_list|(
name|name
operator|.
name|getMethodName
argument_list|()
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|queueView
operator|.
name|getQueueSize
argument_list|()
argument_list|)
expr_stmt|;
block|}
specifier|private
name|int
name|readAllMessages
parameter_list|(
name|String
name|queueName
parameter_list|)
throws|throws
name|JMSException
block|{
return|return
name|readAllMessages
argument_list|(
name|queueName
argument_list|,
literal|null
argument_list|)
return|;
block|}
specifier|private
name|int
name|readAllMessages
parameter_list|(
name|String
name|queueName
parameter_list|,
name|String
name|selector
parameter_list|)
throws|throws
name|JMSException
block|{
name|Connection
name|connection
init|=
name|JMSClientContext
operator|.
name|INSTANCE
operator|.
name|createConnection
argument_list|(
name|amqpURI
argument_list|)
decl_stmt|;
name|connection
operator|.
name|start
argument_list|()
expr_stmt|;
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
name|Queue
name|queue
init|=
name|session
operator|.
name|createQueue
argument_list|(
name|queueName
argument_list|)
decl_stmt|;
name|int
name|messagesReceived
init|=
literal|0
decl_stmt|;
name|MessageConsumer
name|consumer
decl_stmt|;
if|if
condition|(
name|selector
operator|==
literal|null
condition|)
block|{
name|consumer
operator|=
name|session
operator|.
name|createConsumer
argument_list|(
name|queue
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
name|queue
argument_list|,
name|selector
argument_list|)
expr_stmt|;
block|}
try|try
block|{
comment|// Try to get out quickly if there are no messages on the broker side
name|QueueViewMBean
name|queueView
init|=
name|getProxyToQueue
argument_list|(
name|queue
operator|.
name|getQueueName
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
name|queueView
operator|.
name|getQueueSize
argument_list|()
operator|==
literal|0
condition|)
block|{
return|return
literal|0
return|;
block|}
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
name|LOG
operator|.
name|debug
argument_list|(
literal|"Error during destination check: {}"
argument_list|,
name|e
argument_list|)
expr_stmt|;
block|}
name|Message
name|msg
init|=
name|consumer
operator|.
name|receive
argument_list|(
literal|1000
argument_list|)
decl_stmt|;
while|while
condition|(
name|msg
operator|!=
literal|null
condition|)
block|{
name|assertNotNull
argument_list|(
name|msg
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|msg
operator|instanceof
name|TextMessage
argument_list|)
expr_stmt|;
name|TextMessage
name|textMessage
init|=
operator|(
name|TextMessage
operator|)
name|msg
decl_stmt|;
name|LOG
operator|.
name|debug
argument_list|(
literal|">>>> Received [{}]"
argument_list|,
name|textMessage
operator|.
name|getText
argument_list|()
argument_list|)
expr_stmt|;
name|messagesReceived
operator|++
expr_stmt|;
name|msg
operator|=
name|consumer
operator|.
name|receive
argument_list|(
literal|1000
argument_list|)
expr_stmt|;
block|}
name|consumer
operator|.
name|close
argument_list|()
expr_stmt|;
return|return
name|messagesReceived
return|;
block|}
finally|finally
block|{
name|connection
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
specifier|private
name|void
name|restartBroker
parameter_list|(
name|Connection
name|connection
parameter_list|,
name|Session
name|session
parameter_list|)
throws|throws
name|Exception
block|{
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
name|restartBroker
argument_list|()
expr_stmt|;
block|}
specifier|private
name|Connection
name|createAMQConnection
parameter_list|()
throws|throws
name|JMSException
block|{
name|LOG
operator|.
name|debug
argument_list|(
literal|">>> In createConnection using port {}"
argument_list|,
name|openwirePort
argument_list|)
expr_stmt|;
specifier|final
name|ConnectionFactory
name|factory
init|=
operator|new
name|ActiveMQConnectionFactory
argument_list|(
literal|"admin"
argument_list|,
literal|"password"
argument_list|,
name|openwireURI
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
name|setExceptionListener
argument_list|(
operator|new
name|ExceptionListener
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|void
name|onException
parameter_list|(
name|JMSException
name|exception
parameter_list|)
block|{
name|exception
operator|.
name|printStackTrace
argument_list|()
expr_stmt|;
block|}
block|}
argument_list|)
expr_stmt|;
name|connection
operator|.
name|start
argument_list|()
expr_stmt|;
return|return
name|connection
return|;
block|}
annotation|@
name|Override
specifier|protected
name|boolean
name|isUseOpenWireConnector
parameter_list|()
block|{
return|return
literal|true
return|;
block|}
annotation|@
name|Override
specifier|protected
name|boolean
name|isPersistent
parameter_list|()
block|{
return|return
literal|true
return|;
block|}
block|}
end_class

end_unit

