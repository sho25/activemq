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
name|java
operator|.
name|io
operator|.
name|File
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
name|spring
operator|.
name|SpringSslContext
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
name|store
operator|.
name|kahadb
operator|.
name|KahaDBStore
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
name|apache
operator|.
name|qpid
operator|.
name|amqp_1_0
operator|.
name|jms
operator|.
name|impl
operator|.
name|ConnectionFactoryImpl
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|qpid
operator|.
name|amqp_1_0
operator|.
name|jms
operator|.
name|impl
operator|.
name|QueueImpl
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
literal|"target/activemq-data/kahadb-amq4563"
decl_stmt|;
specifier|private
name|String
name|openwireUri
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
name|QueueImpl
name|queue
init|=
operator|new
name|QueueImpl
argument_list|(
literal|"queue://txqueue"
argument_list|)
decl_stmt|;
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
literal|"txqueue"
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
name|queue
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
name|messagesReceived
operator|=
name|readAllMessages
argument_list|(
name|queue
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|0
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
name|QueueImpl
name|queue
init|=
operator|new
name|QueueImpl
argument_list|(
literal|"queue://txqueue"
argument_list|)
decl_stmt|;
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
name|createAMQPConnection
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
literal|"txqueue"
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
name|queue
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
name|QueueImpl
name|queue
init|=
operator|new
name|QueueImpl
argument_list|(
literal|"queue://txqueue"
argument_list|)
decl_stmt|;
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
literal|"txqueue"
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
name|queue
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
name|QueueImpl
name|queue
init|=
operator|new
name|QueueImpl
argument_list|(
literal|"queue://txqueue"
argument_list|)
decl_stmt|;
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
name|createAMQPConnection
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
name|queue
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
name|messagesReceived
operator|=
name|readAllMessages
argument_list|(
name|queue
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|messagesReceived
argument_list|)
expr_stmt|;
block|}
specifier|private
name|int
name|readAllMessages
parameter_list|(
name|QueueImpl
name|queue
parameter_list|)
throws|throws
name|JMSException
block|{
return|return
name|readAllMessages
argument_list|(
name|queue
argument_list|,
literal|null
argument_list|)
return|;
block|}
specifier|private
name|int
name|readAllMessages
parameter_list|(
name|QueueImpl
name|queue
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
name|createAMQPConnection
argument_list|()
decl_stmt|;
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
name|Message
name|msg
init|=
name|consumer
operator|.
name|receive
argument_list|(
literal|5000
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
literal|5000
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
name|stopBroker
argument_list|()
expr_stmt|;
name|createBroker
argument_list|(
literal|false
argument_list|)
expr_stmt|;
block|}
specifier|private
name|Connection
name|createAMQPConnection
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
name|port
argument_list|)
expr_stmt|;
specifier|final
name|ConnectionFactoryImpl
name|factory
init|=
operator|new
name|ConnectionFactoryImpl
argument_list|(
literal|"localhost"
argument_list|,
name|port
argument_list|,
literal|"admin"
argument_list|,
literal|"password"
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
name|port
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
name|openwireUri
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
specifier|public
name|void
name|startBroker
parameter_list|()
throws|throws
name|Exception
block|{
name|createBroker
argument_list|(
literal|true
argument_list|)
expr_stmt|;
block|}
comment|/**      * Copied from AmqpTestSupport, modified to use persistence      */
specifier|public
name|void
name|createBroker
parameter_list|(
name|boolean
name|deleteAllMessages
parameter_list|)
throws|throws
name|Exception
block|{
name|KahaDBStore
name|kaha
init|=
operator|new
name|KahaDBStore
argument_list|()
decl_stmt|;
name|kaha
operator|.
name|setDirectory
argument_list|(
operator|new
name|File
argument_list|(
name|KAHADB_DIRECTORY
argument_list|)
argument_list|)
expr_stmt|;
name|brokerService
operator|=
operator|new
name|BrokerService
argument_list|()
expr_stmt|;
name|brokerService
operator|.
name|setDeleteAllMessagesOnStartup
argument_list|(
name|deleteAllMessages
argument_list|)
expr_stmt|;
name|brokerService
operator|.
name|setPersistent
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|brokerService
operator|.
name|setPersistenceAdapter
argument_list|(
name|kaha
argument_list|)
expr_stmt|;
name|brokerService
operator|.
name|setAdvisorySupport
argument_list|(
literal|false
argument_list|)
expr_stmt|;
name|brokerService
operator|.
name|setUseJmx
argument_list|(
literal|false
argument_list|)
expr_stmt|;
name|brokerService
operator|.
name|setStoreOpenWireVersion
argument_list|(
literal|10
argument_list|)
expr_stmt|;
name|openwireUri
operator|=
name|brokerService
operator|.
name|addConnector
argument_list|(
literal|"tcp://0.0.0.0:0"
argument_list|)
operator|.
name|getPublishableConnectString
argument_list|()
expr_stmt|;
comment|// Setup SSL context...
specifier|final
name|File
name|classesDir
init|=
operator|new
name|File
argument_list|(
name|AmqpProtocolConverter
operator|.
name|class
operator|.
name|getProtectionDomain
argument_list|()
operator|.
name|getCodeSource
argument_list|()
operator|.
name|getLocation
argument_list|()
operator|.
name|getFile
argument_list|()
argument_list|)
decl_stmt|;
name|File
name|keystore
init|=
operator|new
name|File
argument_list|(
name|classesDir
argument_list|,
literal|"../../src/test/resources/keystore"
argument_list|)
decl_stmt|;
specifier|final
name|SpringSslContext
name|sslContext
init|=
operator|new
name|SpringSslContext
argument_list|()
decl_stmt|;
name|sslContext
operator|.
name|setKeyStore
argument_list|(
name|keystore
operator|.
name|getCanonicalPath
argument_list|()
argument_list|)
expr_stmt|;
name|sslContext
operator|.
name|setKeyStorePassword
argument_list|(
literal|"password"
argument_list|)
expr_stmt|;
name|sslContext
operator|.
name|setTrustStore
argument_list|(
name|keystore
operator|.
name|getCanonicalPath
argument_list|()
argument_list|)
expr_stmt|;
name|sslContext
operator|.
name|setTrustStorePassword
argument_list|(
literal|"password"
argument_list|)
expr_stmt|;
name|sslContext
operator|.
name|afterPropertiesSet
argument_list|()
expr_stmt|;
name|brokerService
operator|.
name|setSslContext
argument_list|(
name|sslContext
argument_list|)
expr_stmt|;
name|addAMQPConnector
argument_list|()
expr_stmt|;
name|brokerService
operator|.
name|start
argument_list|()
expr_stmt|;
name|this
operator|.
name|numberOfMessages
operator|=
literal|2000
expr_stmt|;
block|}
block|}
end_class

end_unit

