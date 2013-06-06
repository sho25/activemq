begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  * http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
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
name|assertFalse
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
name|broker
operator|.
name|TransportConnector
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
name|After
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

begin_comment
comment|/**  * @author<a href="http://www.christianposta.com/blog">Christian Posta</a>  */
end_comment

begin_class
specifier|public
class|class
name|AmqpTransformerTest
block|{
specifier|private
specifier|static
specifier|final
name|String
name|AMQP_URL
init|=
literal|"amqp://0.0.0.0:0%s"
decl_stmt|;
specifier|private
name|BrokerService
name|brokerService
decl_stmt|;
specifier|private
name|int
name|amqpPort
decl_stmt|;
specifier|private
name|int
name|openwirePort
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|String
name|TEST_QUEUE
init|=
literal|"txqueue"
decl_stmt|;
annotation|@
name|Test
specifier|public
name|void
name|testNativeTransformation
parameter_list|()
throws|throws
name|Exception
block|{
comment|// default is native
name|startBrokerWithAmqpTransport
argument_list|(
name|String
operator|.
name|format
argument_list|(
name|AMQP_URL
argument_list|,
literal|"?transport.transformer=native"
argument_list|)
argument_list|)
expr_stmt|;
comment|// send "text message" with AMQP JMS API
name|Connection
name|amqpConnection
init|=
name|createAmqpConnection
argument_list|()
decl_stmt|;
name|QueueImpl
name|queue
init|=
operator|new
name|QueueImpl
argument_list|(
literal|"queue://"
operator|+
name|TEST_QUEUE
argument_list|)
decl_stmt|;
name|Session
name|amqpSession
init|=
name|amqpConnection
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
name|amqpSession
operator|.
name|createProducer
argument_list|(
name|queue
argument_list|)
decl_stmt|;
name|p
operator|.
name|setPriority
argument_list|(
literal|7
argument_list|)
expr_stmt|;
name|TextMessage
name|amqpMessage
init|=
name|amqpSession
operator|.
name|createTextMessage
argument_list|()
decl_stmt|;
name|amqpMessage
operator|.
name|setText
argument_list|(
literal|"hello"
argument_list|)
expr_stmt|;
name|p
operator|.
name|send
argument_list|(
name|amqpMessage
argument_list|)
expr_stmt|;
name|p
operator|.
name|close
argument_list|()
expr_stmt|;
name|amqpSession
operator|.
name|close
argument_list|()
expr_stmt|;
name|amqpConnection
operator|.
name|close
argument_list|()
expr_stmt|;
comment|// receive with openwire JMS
name|ActiveMQConnectionFactory
name|factory
init|=
operator|new
name|ActiveMQConnectionFactory
argument_list|(
literal|"tcp://0.0.0.0:"
operator|+
name|openwirePort
argument_list|)
decl_stmt|;
name|Connection
name|openwireConn
init|=
name|factory
operator|.
name|createConnection
argument_list|()
decl_stmt|;
name|openwireConn
operator|.
name|start
argument_list|()
expr_stmt|;
name|Session
name|session
init|=
name|openwireConn
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
name|jmsDest
init|=
name|session
operator|.
name|createQueue
argument_list|(
name|TEST_QUEUE
argument_list|)
decl_stmt|;
name|MessageConsumer
name|c
init|=
name|session
operator|.
name|createConsumer
argument_list|(
name|jmsDest
argument_list|)
decl_stmt|;
name|Message
name|message
init|=
name|c
operator|.
name|receive
argument_list|(
literal|1000
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
name|message
operator|instanceof
name|BytesMessage
argument_list|)
expr_stmt|;
name|Boolean
name|nativeTransformationUsed
init|=
name|message
operator|.
name|getBooleanProperty
argument_list|(
literal|"JMS_AMQP_NATIVE"
argument_list|)
decl_stmt|;
name|Long
name|messageFormat
init|=
name|message
operator|.
name|getLongProperty
argument_list|(
literal|"JMS_AMQP_MESSAGE_FORMAT"
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|0L
argument_list|,
name|messageFormat
operator|.
name|longValue
argument_list|()
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
literal|"Didn't use the correct transformation, expected NATIVE"
argument_list|,
name|nativeTransformationUsed
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|2
argument_list|,
name|message
operator|.
name|getJMSDeliveryMode
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|7
argument_list|,
name|message
operator|.
name|getJMSPriority
argument_list|()
argument_list|)
expr_stmt|;
name|c
operator|.
name|close
argument_list|()
expr_stmt|;
name|session
operator|.
name|close
argument_list|()
expr_stmt|;
name|openwireConn
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
argument_list|(
name|timeout
operator|=
literal|30000
argument_list|)
specifier|public
name|void
name|testRawTransformation
parameter_list|()
throws|throws
name|Exception
block|{
comment|// default is native
name|startBrokerWithAmqpTransport
argument_list|(
name|String
operator|.
name|format
argument_list|(
name|AMQP_URL
argument_list|,
literal|"?transport.transformer=raw"
argument_list|)
argument_list|)
expr_stmt|;
comment|// send "text message" with AMQP JMS API
name|Connection
name|amqpConnection
init|=
name|createAmqpConnection
argument_list|()
decl_stmt|;
name|QueueImpl
name|queue
init|=
operator|new
name|QueueImpl
argument_list|(
literal|"queue://"
operator|+
name|TEST_QUEUE
argument_list|)
decl_stmt|;
name|Session
name|amqpSession
init|=
name|amqpConnection
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
name|amqpSession
operator|.
name|createProducer
argument_list|(
name|queue
argument_list|)
decl_stmt|;
name|p
operator|.
name|setPriority
argument_list|(
literal|7
argument_list|)
expr_stmt|;
name|TextMessage
name|amqpMessage
init|=
name|amqpSession
operator|.
name|createTextMessage
argument_list|()
decl_stmt|;
name|amqpMessage
operator|.
name|setText
argument_list|(
literal|"hello"
argument_list|)
expr_stmt|;
name|p
operator|.
name|send
argument_list|(
name|amqpMessage
argument_list|)
expr_stmt|;
name|p
operator|.
name|close
argument_list|()
expr_stmt|;
name|amqpSession
operator|.
name|close
argument_list|()
expr_stmt|;
name|amqpConnection
operator|.
name|close
argument_list|()
expr_stmt|;
comment|// receive with openwire JMS
name|ActiveMQConnectionFactory
name|factory
init|=
operator|new
name|ActiveMQConnectionFactory
argument_list|(
literal|"tcp://0.0.0.0:"
operator|+
name|openwirePort
argument_list|)
decl_stmt|;
name|Connection
name|openwireConn
init|=
name|factory
operator|.
name|createConnection
argument_list|()
decl_stmt|;
name|openwireConn
operator|.
name|start
argument_list|()
expr_stmt|;
name|Session
name|session
init|=
name|openwireConn
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
name|jmsDest
init|=
name|session
operator|.
name|createQueue
argument_list|(
name|TEST_QUEUE
argument_list|)
decl_stmt|;
name|MessageConsumer
name|c
init|=
name|session
operator|.
name|createConsumer
argument_list|(
name|jmsDest
argument_list|)
decl_stmt|;
name|Message
name|message
init|=
name|c
operator|.
name|receive
argument_list|(
literal|1000
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
name|message
operator|instanceof
name|BytesMessage
argument_list|)
expr_stmt|;
name|Boolean
name|nativeTransformationUsed
init|=
name|message
operator|.
name|getBooleanProperty
argument_list|(
literal|"JMS_AMQP_NATIVE"
argument_list|)
decl_stmt|;
name|Long
name|messageFormat
init|=
name|message
operator|.
name|getLongProperty
argument_list|(
literal|"JMS_AMQP_MESSAGE_FORMAT"
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|0L
argument_list|,
name|messageFormat
operator|.
name|longValue
argument_list|()
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
literal|"Didn't use the correct transformation, expected NATIVE"
argument_list|,
name|nativeTransformationUsed
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|2
argument_list|,
name|message
operator|.
name|getJMSDeliveryMode
argument_list|()
argument_list|)
expr_stmt|;
comment|// should not equal 7 (should equal the default) because "raw" does not map
comment|// headers
name|assertEquals
argument_list|(
literal|4
argument_list|,
name|message
operator|.
name|getJMSPriority
argument_list|()
argument_list|)
expr_stmt|;
name|c
operator|.
name|close
argument_list|()
expr_stmt|;
name|session
operator|.
name|close
argument_list|()
expr_stmt|;
name|openwireConn
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testJmsTransformation
parameter_list|()
throws|throws
name|Exception
block|{
comment|// default is native
name|startBrokerWithAmqpTransport
argument_list|(
name|String
operator|.
name|format
argument_list|(
name|AMQP_URL
argument_list|,
literal|"?transport.transformer=jms"
argument_list|)
argument_list|)
expr_stmt|;
comment|// send "text message" with AMQP JMS API
name|Connection
name|amqpConnection
init|=
name|createAmqpConnection
argument_list|()
decl_stmt|;
name|QueueImpl
name|queue
init|=
operator|new
name|QueueImpl
argument_list|(
literal|"queue://"
operator|+
name|TEST_QUEUE
argument_list|)
decl_stmt|;
name|Session
name|amqpSession
init|=
name|amqpConnection
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
name|amqpSession
operator|.
name|createProducer
argument_list|(
name|queue
argument_list|)
decl_stmt|;
name|TextMessage
name|amqpMessage
init|=
name|amqpSession
operator|.
name|createTextMessage
argument_list|()
decl_stmt|;
name|amqpMessage
operator|.
name|setText
argument_list|(
literal|"hello"
argument_list|)
expr_stmt|;
name|p
operator|.
name|send
argument_list|(
name|amqpMessage
argument_list|)
expr_stmt|;
name|p
operator|.
name|close
argument_list|()
expr_stmt|;
name|amqpSession
operator|.
name|close
argument_list|()
expr_stmt|;
name|amqpConnection
operator|.
name|close
argument_list|()
expr_stmt|;
comment|// receive with openwire JMS
name|ActiveMQConnectionFactory
name|factory
init|=
operator|new
name|ActiveMQConnectionFactory
argument_list|(
literal|"tcp://0.0.0.0:"
operator|+
name|openwirePort
argument_list|)
decl_stmt|;
name|Connection
name|openwireConn
init|=
name|factory
operator|.
name|createConnection
argument_list|()
decl_stmt|;
name|openwireConn
operator|.
name|start
argument_list|()
expr_stmt|;
name|Session
name|session
init|=
name|openwireConn
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
name|jmsDest
init|=
name|session
operator|.
name|createQueue
argument_list|(
name|TEST_QUEUE
argument_list|)
decl_stmt|;
name|MessageConsumer
name|c
init|=
name|session
operator|.
name|createConsumer
argument_list|(
name|jmsDest
argument_list|)
decl_stmt|;
name|Message
name|message
init|=
name|c
operator|.
name|receive
argument_list|(
literal|1000
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
name|message
operator|instanceof
name|TextMessage
argument_list|)
expr_stmt|;
name|Boolean
name|nativeTransformationUsed
init|=
name|message
operator|.
name|getBooleanProperty
argument_list|(
literal|"JMS_AMQP_NATIVE"
argument_list|)
decl_stmt|;
name|Long
name|messageFormat
init|=
name|message
operator|.
name|getLongProperty
argument_list|(
literal|"JMS_AMQP_MESSAGE_FORMAT"
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|0L
argument_list|,
name|messageFormat
operator|.
name|longValue
argument_list|()
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
literal|"Didn't use the correct transformation, expected NOT to be NATIVE"
argument_list|,
name|nativeTransformationUsed
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|2
argument_list|,
name|message
operator|.
name|getJMSDeliveryMode
argument_list|()
argument_list|)
expr_stmt|;
name|c
operator|.
name|close
argument_list|()
expr_stmt|;
name|session
operator|.
name|close
argument_list|()
expr_stmt|;
name|openwireConn
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
specifier|public
name|Connection
name|createAmqpConnection
parameter_list|()
throws|throws
name|JMSException
block|{
specifier|final
name|ConnectionFactoryImpl
name|factory
init|=
operator|new
name|ConnectionFactoryImpl
argument_list|(
literal|"localhost"
argument_list|,
name|amqpPort
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
specifier|public
name|void
name|startBrokerWithAmqpTransport
parameter_list|(
name|String
name|amqpUrl
parameter_list|)
throws|throws
name|Exception
block|{
name|brokerService
operator|=
operator|new
name|BrokerService
argument_list|()
expr_stmt|;
name|brokerService
operator|.
name|setPersistent
argument_list|(
literal|false
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
name|setDeleteAllMessagesOnStartup
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|TransportConnector
name|connector
init|=
name|brokerService
operator|.
name|addConnector
argument_list|(
name|amqpUrl
argument_list|)
decl_stmt|;
name|amqpPort
operator|=
name|connector
operator|.
name|getConnectUri
argument_list|()
operator|.
name|getPort
argument_list|()
expr_stmt|;
name|connector
operator|=
name|brokerService
operator|.
name|addConnector
argument_list|(
literal|"tcp://0.0.0.0:0"
argument_list|)
expr_stmt|;
name|openwirePort
operator|=
name|connector
operator|.
name|getConnectUri
argument_list|()
operator|.
name|getPort
argument_list|()
expr_stmt|;
name|brokerService
operator|.
name|start
argument_list|()
expr_stmt|;
name|brokerService
operator|.
name|waitUntilStarted
argument_list|()
expr_stmt|;
block|}
annotation|@
name|After
specifier|public
name|void
name|stopBroker
parameter_list|()
throws|throws
name|Exception
block|{
if|if
condition|(
name|brokerService
operator|!=
literal|null
condition|)
block|{
name|brokerService
operator|.
name|stop
argument_list|()
expr_stmt|;
name|brokerService
operator|=
literal|null
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

