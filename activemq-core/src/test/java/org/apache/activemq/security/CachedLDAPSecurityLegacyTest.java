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
name|security
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
name|apache
operator|.
name|directory
operator|.
name|server
operator|.
name|annotations
operator|.
name|CreateLdapServer
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|directory
operator|.
name|server
operator|.
name|annotations
operator|.
name|CreateTransport
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|directory
operator|.
name|server
operator|.
name|core
operator|.
name|annotations
operator|.
name|ApplyLdifFiles
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|directory
operator|.
name|server
operator|.
name|core
operator|.
name|integ
operator|.
name|AbstractLdapTestUnit
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|directory
operator|.
name|server
operator|.
name|core
operator|.
name|integ
operator|.
name|FrameworkRunner
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|directory
operator|.
name|server
operator|.
name|ldap
operator|.
name|LdapServer
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
name|Test
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|runner
operator|.
name|RunWith
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
name|fail
import|;
end_import

begin_class
annotation|@
name|RunWith
argument_list|(
name|FrameworkRunner
operator|.
name|class
argument_list|)
annotation|@
name|CreateLdapServer
argument_list|(
name|transports
operator|=
block|{
annotation|@
name|CreateTransport
argument_list|(
name|protocol
operator|=
literal|"LDAP"
argument_list|)
block|}
argument_list|)
annotation|@
name|ApplyLdifFiles
argument_list|(
literal|"org/apache/activemq/security/activemq-apacheds-legacy.ldif"
argument_list|)
specifier|public
class|class
name|CachedLDAPSecurityLegacyTest
extends|extends
name|AbstractLdapTestUnit
block|{
specifier|public
name|BrokerService
name|broker
decl_stmt|;
specifier|public
specifier|static
name|LdapServer
name|ldapServer
decl_stmt|;
annotation|@
name|Before
specifier|public
name|void
name|setup
parameter_list|()
throws|throws
name|Exception
block|{
name|System
operator|.
name|setProperty
argument_list|(
literal|"ldapPort"
argument_list|,
name|String
operator|.
name|valueOf
argument_list|(
name|getLdapServer
argument_list|()
operator|.
name|getPort
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|broker
operator|=
name|BrokerFactory
operator|.
name|createBroker
argument_list|(
literal|"xbean:org/apache/activemq/security/activemq-apacheds-legacy.xml"
argument_list|)
expr_stmt|;
name|broker
operator|.
name|start
argument_list|()
expr_stmt|;
name|broker
operator|.
name|waitUntilStarted
argument_list|()
expr_stmt|;
block|}
annotation|@
name|After
specifier|public
name|void
name|shutdown
parameter_list|()
throws|throws
name|Exception
block|{
name|broker
operator|.
name|stop
argument_list|()
expr_stmt|;
name|broker
operator|.
name|waitUntilStopped
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testSendReceive
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
literal|"vm://localhost"
argument_list|)
decl_stmt|;
name|Connection
name|conn
init|=
name|factory
operator|.
name|createQueueConnection
argument_list|(
literal|"jdoe"
argument_list|,
literal|"sunflower"
argument_list|)
decl_stmt|;
name|Session
name|sess
init|=
name|conn
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
name|conn
operator|.
name|start
argument_list|()
expr_stmt|;
name|Queue
name|queue
init|=
name|sess
operator|.
name|createQueue
argument_list|(
literal|"TEST.FOO"
argument_list|)
decl_stmt|;
name|MessageProducer
name|producer
init|=
name|sess
operator|.
name|createProducer
argument_list|(
name|queue
argument_list|)
decl_stmt|;
name|MessageConsumer
name|consumer
init|=
name|sess
operator|.
name|createConsumer
argument_list|(
name|queue
argument_list|)
decl_stmt|;
name|producer
operator|.
name|send
argument_list|(
name|sess
operator|.
name|createTextMessage
argument_list|(
literal|"test"
argument_list|)
argument_list|)
expr_stmt|;
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
name|assertNotNull
argument_list|(
name|msg
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testSendDenied
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
literal|"vm://localhost"
argument_list|)
decl_stmt|;
name|Connection
name|conn
init|=
name|factory
operator|.
name|createQueueConnection
argument_list|(
literal|"jdoe"
argument_list|,
literal|"sunflower"
argument_list|)
decl_stmt|;
name|Session
name|sess
init|=
name|conn
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
name|conn
operator|.
name|start
argument_list|()
expr_stmt|;
name|Queue
name|queue
init|=
name|sess
operator|.
name|createQueue
argument_list|(
literal|"ADMIN.FOO"
argument_list|)
decl_stmt|;
name|MessageProducer
name|producer
init|=
name|sess
operator|.
name|createProducer
argument_list|(
name|queue
argument_list|)
decl_stmt|;
try|try
block|{
name|producer
operator|.
name|send
argument_list|(
name|sess
operator|.
name|createTextMessage
argument_list|(
literal|"test"
argument_list|)
argument_list|)
expr_stmt|;
name|fail
argument_list|(
literal|"expect auth exception"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|JMSException
name|expected
parameter_list|)
block|{         }
block|}
annotation|@
name|Test
specifier|public
name|void
name|testCompositeSendDenied
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
literal|"vm://localhost"
argument_list|)
decl_stmt|;
name|Connection
name|conn
init|=
name|factory
operator|.
name|createQueueConnection
argument_list|(
literal|"jdoe"
argument_list|,
literal|"sunflower"
argument_list|)
decl_stmt|;
name|Session
name|sess
init|=
name|conn
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
name|conn
operator|.
name|start
argument_list|()
expr_stmt|;
name|Queue
name|queue
init|=
name|sess
operator|.
name|createQueue
argument_list|(
literal|"TEST.FOO,ADMIN.FOO"
argument_list|)
decl_stmt|;
name|MessageProducer
name|producer
init|=
name|sess
operator|.
name|createProducer
argument_list|(
name|queue
argument_list|)
decl_stmt|;
try|try
block|{
name|producer
operator|.
name|send
argument_list|(
name|sess
operator|.
name|createTextMessage
argument_list|(
literal|"test"
argument_list|)
argument_list|)
expr_stmt|;
name|fail
argument_list|(
literal|"expect auth exception"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|JMSException
name|expected
parameter_list|)
block|{         }
block|}
annotation|@
name|Test
specifier|public
name|void
name|testTempDestinations
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
literal|"vm://localhost"
argument_list|)
decl_stmt|;
name|Connection
name|conn
init|=
name|factory
operator|.
name|createQueueConnection
argument_list|(
literal|"jdoe"
argument_list|,
literal|"sunflower"
argument_list|)
decl_stmt|;
name|Session
name|sess
init|=
name|conn
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
name|conn
operator|.
name|start
argument_list|()
expr_stmt|;
name|Queue
name|queue
init|=
name|sess
operator|.
name|createTemporaryQueue
argument_list|()
decl_stmt|;
name|MessageProducer
name|producer
init|=
name|sess
operator|.
name|createProducer
argument_list|(
name|queue
argument_list|)
decl_stmt|;
name|MessageConsumer
name|consumer
init|=
name|sess
operator|.
name|createConsumer
argument_list|(
name|queue
argument_list|)
decl_stmt|;
name|producer
operator|.
name|send
argument_list|(
name|sess
operator|.
name|createTextMessage
argument_list|(
literal|"test"
argument_list|)
argument_list|)
expr_stmt|;
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
name|assertNotNull
argument_list|(
name|msg
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

