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
import|import static
name|org
operator|.
name|junit
operator|.
name|Assert
operator|.
name|fail
import|;
end_import

begin_import
import|import
name|java
operator|.
name|net
operator|.
name|URI
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
name|JMSException
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|jms
operator|.
name|JMSSecurityException
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
name|broker
operator|.
name|BrokerFactory
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
name|Before
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|Rule
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

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|rules
operator|.
name|TestName
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
class|class
name|JMSClientSimpleAuthTest
block|{
annotation|@
name|Rule
specifier|public
name|TestName
name|name
init|=
operator|new
name|TestName
argument_list|()
decl_stmt|;
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
name|JMSClientSimpleAuthTest
operator|.
name|class
argument_list|)
decl_stmt|;
specifier|private
specifier|final
name|String
name|SIMPLE_AUTH_AMQP_BROKER_XML
init|=
literal|"org/apache/activemq/transport/amqp/simple-auth-amqp-broker.xml"
decl_stmt|;
specifier|private
name|BrokerService
name|brokerService
decl_stmt|;
specifier|private
name|Connection
name|connection
decl_stmt|;
specifier|private
name|URI
name|amqpURI
decl_stmt|;
annotation|@
name|Before
specifier|public
name|void
name|setUp
parameter_list|()
throws|throws
name|Exception
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"========== starting: "
operator|+
name|getTestName
argument_list|()
operator|+
literal|" =========="
argument_list|)
expr_stmt|;
name|startBroker
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
name|Exception
name|ex
parameter_list|)
block|{}
name|connection
operator|=
literal|null
expr_stmt|;
block|}
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
name|LOG
operator|.
name|info
argument_list|(
literal|"========== finished: "
operator|+
name|getTestName
argument_list|()
operator|+
literal|" =========="
argument_list|)
expr_stmt|;
block|}
specifier|public
name|String
name|getTestName
parameter_list|()
block|{
return|return
name|name
operator|.
name|getMethodName
argument_list|()
return|;
block|}
annotation|@
name|Test
argument_list|(
name|timeout
operator|=
literal|10000
argument_list|)
specifier|public
name|void
name|testNoUserOrPassword
parameter_list|()
throws|throws
name|Exception
block|{
try|try
block|{
name|connection
operator|=
name|JMSClientContext
operator|.
name|INSTANCE
operator|.
name|createConnection
argument_list|(
name|amqpURI
argument_list|,
literal|""
argument_list|,
literal|""
argument_list|)
expr_stmt|;
name|connection
operator|.
name|start
argument_list|()
expr_stmt|;
name|fail
argument_list|(
literal|"Expected JMSException"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|JMSSecurityException
name|ex
parameter_list|)
block|{
name|LOG
operator|.
name|debug
argument_list|(
literal|"Failed to authenticate connection with no user / password."
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Test
argument_list|(
name|timeout
operator|=
literal|10000
argument_list|)
specifier|public
name|void
name|testUnknownUser
parameter_list|()
throws|throws
name|Exception
block|{
try|try
block|{
name|connection
operator|=
name|JMSClientContext
operator|.
name|INSTANCE
operator|.
name|createConnection
argument_list|(
name|amqpURI
argument_list|,
literal|"nosuchuser"
argument_list|,
literal|"blah"
argument_list|)
expr_stmt|;
name|connection
operator|.
name|start
argument_list|()
expr_stmt|;
name|fail
argument_list|(
literal|"Expected JMSException"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|JMSSecurityException
name|ex
parameter_list|)
block|{
name|LOG
operator|.
name|debug
argument_list|(
literal|"Failed to authenticate connection with unknown user ID"
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Test
argument_list|(
name|timeout
operator|=
literal|10000
argument_list|)
specifier|public
name|void
name|testKnownUserWrongPassword
parameter_list|()
throws|throws
name|Exception
block|{
try|try
block|{
name|connection
operator|=
name|JMSClientContext
operator|.
name|INSTANCE
operator|.
name|createConnection
argument_list|(
name|amqpURI
argument_list|,
literal|"user"
argument_list|,
literal|"wrongPassword"
argument_list|)
expr_stmt|;
name|connection
operator|.
name|start
argument_list|()
expr_stmt|;
name|fail
argument_list|(
literal|"Expected JMSException"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|JMSSecurityException
name|ex
parameter_list|)
block|{
name|LOG
operator|.
name|debug
argument_list|(
literal|"Failed to authenticate connection with incorrect password."
argument_list|)
expr_stmt|;
block|}
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
name|testRepeatedWrongPasswordAttempts
parameter_list|()
throws|throws
name|Exception
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
literal|25
condition|;
operator|++
name|i
control|)
block|{
name|Connection
name|connection
init|=
literal|null
decl_stmt|;
try|try
block|{
name|connection
operator|=
name|JMSClientContext
operator|.
name|INSTANCE
operator|.
name|createConnection
argument_list|(
name|amqpURI
argument_list|,
literal|"user"
argument_list|,
literal|"wrongPassword"
argument_list|)
expr_stmt|;
name|connection
operator|.
name|start
argument_list|()
expr_stmt|;
name|fail
argument_list|(
literal|"Expected JMSException"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|JMSSecurityException
name|ex
parameter_list|)
block|{
name|LOG
operator|.
name|debug
argument_list|(
literal|"Failed to authenticate connection with incorrect password."
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
name|connection
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
block|}
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
name|testSendReceive
parameter_list|()
throws|throws
name|Exception
block|{
name|connection
operator|=
name|JMSClientContext
operator|.
name|INSTANCE
operator|.
name|createConnection
argument_list|(
name|amqpURI
argument_list|,
literal|"user"
argument_list|,
literal|"userPassword"
argument_list|)
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
literal|"USERS.txQueue"
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
literal|"hello  sent at "
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
comment|// Get the message we just sent
name|MessageConsumer
name|consumer
init|=
name|session
operator|.
name|createConsumer
argument_list|(
name|queue
argument_list|)
decl_stmt|;
name|connection
operator|.
name|start
argument_list|()
expr_stmt|;
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
name|assertEquals
argument_list|(
name|messageText
argument_list|,
name|textMessage
operator|.
name|getText
argument_list|()
argument_list|)
expr_stmt|;
name|connection
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
name|testProducerNotAuthorized
parameter_list|()
throws|throws
name|Exception
block|{
name|connection
operator|=
name|JMSClientContext
operator|.
name|INSTANCE
operator|.
name|createConnection
argument_list|(
name|amqpURI
argument_list|,
literal|"guest"
argument_list|,
literal|"guestPassword"
argument_list|)
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
literal|"USERS.txQueue"
argument_list|)
decl_stmt|;
try|try
block|{
name|session
operator|.
name|createProducer
argument_list|(
name|queue
argument_list|)
expr_stmt|;
name|fail
argument_list|(
literal|"Should not be able to produce here."
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|JMSSecurityException
name|jmsSE
parameter_list|)
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"Caught expected exception"
argument_list|)
expr_stmt|;
block|}
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
name|testAnonymousProducerNotAuthorized
parameter_list|()
throws|throws
name|Exception
block|{
name|connection
operator|=
name|JMSClientContext
operator|.
name|INSTANCE
operator|.
name|createConnection
argument_list|(
name|amqpURI
argument_list|,
literal|"guest"
argument_list|,
literal|"guestPassword"
argument_list|)
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
literal|"USERS.txQueue"
argument_list|)
decl_stmt|;
name|MessageProducer
name|producer
init|=
name|session
operator|.
name|createProducer
argument_list|(
literal|null
argument_list|)
decl_stmt|;
try|try
block|{
name|producer
operator|.
name|send
argument_list|(
name|queue
argument_list|,
name|session
operator|.
name|createTextMessage
argument_list|()
argument_list|)
expr_stmt|;
name|fail
argument_list|(
literal|"Should not be able to produce here."
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|JMSSecurityException
name|jmsSE
parameter_list|)
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"Caught expected exception"
argument_list|)
expr_stmt|;
block|}
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
name|testCreateTemporaryQueueNotAuthorized
parameter_list|()
throws|throws
name|JMSException
block|{
name|connection
operator|=
name|JMSClientContext
operator|.
name|INSTANCE
operator|.
name|createConnection
argument_list|(
name|amqpURI
argument_list|,
literal|"user"
argument_list|,
literal|"userPassword"
argument_list|)
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
try|try
block|{
name|session
operator|.
name|createTemporaryQueue
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|JMSSecurityException
name|jmsse
parameter_list|)
block|{         }
catch|catch
parameter_list|(
name|JMSException
name|jmse
parameter_list|)
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"Client should have thrown a JMSSecurityException but only threw JMSException"
argument_list|)
expr_stmt|;
block|}
comment|// Should not be fatal
name|assertNotNull
argument_list|(
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
argument_list|)
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
name|testCreateTemporaryTopicNotAuthorized
parameter_list|()
throws|throws
name|JMSException
block|{
name|connection
operator|=
name|JMSClientContext
operator|.
name|INSTANCE
operator|.
name|createConnection
argument_list|(
name|amqpURI
argument_list|,
literal|"user"
argument_list|,
literal|"userPassword"
argument_list|)
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
try|try
block|{
name|session
operator|.
name|createTemporaryTopic
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|JMSSecurityException
name|jmsse
parameter_list|)
block|{         }
catch|catch
parameter_list|(
name|JMSException
name|jmse
parameter_list|)
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"Client should have thrown a JMSSecurityException but only threw JMSException"
argument_list|)
expr_stmt|;
block|}
comment|// Should not be fatal
name|assertNotNull
argument_list|(
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
argument_list|)
expr_stmt|;
block|}
specifier|protected
name|BrokerService
name|createBroker
parameter_list|()
throws|throws
name|Exception
block|{
return|return
name|createBroker
argument_list|(
name|SIMPLE_AUTH_AMQP_BROKER_XML
argument_list|)
return|;
block|}
specifier|protected
name|BrokerService
name|createBroker
parameter_list|(
name|String
name|uri
parameter_list|)
throws|throws
name|Exception
block|{
name|LOG
operator|.
name|debug
argument_list|(
literal|">>>>> Loading broker configuration from the classpath with URI: {}"
argument_list|,
name|uri
argument_list|)
expr_stmt|;
return|return
name|BrokerFactory
operator|.
name|createBroker
argument_list|(
operator|new
name|URI
argument_list|(
literal|"xbean:"
operator|+
name|uri
argument_list|)
argument_list|)
return|;
block|}
specifier|public
name|void
name|startBroker
parameter_list|()
throws|throws
name|Exception
block|{
name|brokerService
operator|=
name|createBroker
argument_list|()
expr_stmt|;
name|brokerService
operator|.
name|start
argument_list|()
expr_stmt|;
name|amqpURI
operator|=
name|brokerService
operator|.
name|getTransportConnectorByName
argument_list|(
literal|"amqp"
argument_list|)
operator|.
name|getPublishableConnectURI
argument_list|()
expr_stmt|;
name|brokerService
operator|.
name|waitUntilStarted
argument_list|()
expr_stmt|;
block|}
block|}
end_class

end_unit

