begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements. See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License. You may obtain a copy of the License at  *<p/>  * http://www.apache.org/licenses/LICENSE-2.0  *<p/>  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
package|package
name|org
operator|.
name|apache
operator|.
name|activemq
operator|.
name|ra
package|;
end_package

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
name|broker
operator|.
name|SslBrokerService
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
name|SslContext
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
name|activemq
operator|.
name|transport
operator|.
name|TransportFactory
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
name|tcp
operator|.
name|SslTransportFactory
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
name|net
operator|.
name|ssl
operator|.
name|KeyManager
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|net
operator|.
name|ssl
operator|.
name|TrustManager
import|;
end_import

begin_comment
comment|/**  * @author<a href="mailto:andy.taylor@jboss.org">Andy Taylor</a>  */
end_comment

begin_class
specifier|public
class|class
name|SSLMAnagedConnectionFactoryTest
extends|extends
name|TestCase
block|{
specifier|private
specifier|static
specifier|final
name|String
name|DEFAULT_HOST
init|=
literal|"ssl://0.0.0.0:61616"
decl_stmt|;
specifier|private
name|ConnectionManagerAdapter
name|connectionManager
init|=
operator|new
name|ConnectionManagerAdapter
argument_list|()
decl_stmt|;
specifier|private
name|ActiveMQManagedConnectionFactory
name|managedConnectionFactory
decl_stmt|;
specifier|private
name|ConnectionFactory
name|connectionFactory
decl_stmt|;
specifier|private
name|ManagedConnectionProxy
name|connection
decl_stmt|;
specifier|private
name|ActiveMQManagedConnection
name|managedConnection
decl_stmt|;
specifier|private
name|SslBrokerService
name|broker
decl_stmt|;
specifier|private
name|TransportConnector
name|connector
decl_stmt|;
comment|/**      * @see junit.framework.TestCase#setUp()      */
specifier|protected
name|void
name|setUp
parameter_list|()
throws|throws
name|Exception
block|{
name|managedConnectionFactory
operator|=
operator|new
name|ActiveMQManagedConnectionFactory
argument_list|()
expr_stmt|;
name|managedConnectionFactory
operator|.
name|setServerUrl
argument_list|(
name|DEFAULT_HOST
argument_list|)
expr_stmt|;
name|managedConnectionFactory
operator|.
name|setTrustStore
argument_list|(
literal|"server.keystore"
argument_list|)
expr_stmt|;
name|managedConnectionFactory
operator|.
name|setTrustStorePassword
argument_list|(
literal|"password"
argument_list|)
expr_stmt|;
name|managedConnectionFactory
operator|.
name|setKeyStore
argument_list|(
literal|"client.keystore"
argument_list|)
expr_stmt|;
name|managedConnectionFactory
operator|.
name|setKeyStorePassword
argument_list|(
literal|"password"
argument_list|)
expr_stmt|;
name|connectionFactory
operator|=
operator|(
name|ConnectionFactory
operator|)
name|managedConnectionFactory
operator|.
name|createConnectionFactory
argument_list|(
name|connectionManager
argument_list|)
expr_stmt|;
name|createAndStartBroker
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Override
specifier|protected
name|void
name|tearDown
parameter_list|()
throws|throws
name|Exception
block|{
if|if
condition|(
name|broker
operator|!=
literal|null
condition|)
block|{
name|broker
operator|.
name|stop
argument_list|()
expr_stmt|;
block|}
block|}
specifier|public
name|void
name|testSSLManagedConnection
parameter_list|()
throws|throws
name|Exception
block|{
name|connection
operator|=
operator|(
name|ManagedConnectionProxy
operator|)
name|connectionFactory
operator|.
name|createConnection
argument_list|()
expr_stmt|;
name|managedConnection
operator|=
name|connection
operator|.
name|getManagedConnection
argument_list|()
expr_stmt|;
comment|//do some stuff
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
name|t
init|=
name|session
operator|.
name|createQueue
argument_list|(
literal|"TEST"
argument_list|)
decl_stmt|;
name|MessageProducer
name|producer
init|=
name|session
operator|.
name|createProducer
argument_list|(
name|t
argument_list|)
decl_stmt|;
name|producer
operator|.
name|send
argument_list|(
name|session
operator|.
name|createTextMessage
argument_list|(
literal|"test message."
argument_list|)
argument_list|)
expr_stmt|;
name|managedConnection
operator|.
name|destroy
argument_list|()
expr_stmt|;
name|connection
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
specifier|private
name|void
name|createAndStartBroker
parameter_list|()
throws|throws
name|Exception
block|{
name|broker
operator|=
operator|new
name|SslBrokerService
argument_list|()
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
name|setUseJmx
argument_list|(
literal|false
argument_list|)
expr_stmt|;
name|broker
operator|.
name|setBrokerName
argument_list|(
literal|"BROKER"
argument_list|)
expr_stmt|;
name|KeyManager
index|[]
name|km
init|=
name|SSLTest
operator|.
name|getKeyManager
argument_list|()
decl_stmt|;
name|TrustManager
index|[]
name|tm
init|=
name|SSLTest
operator|.
name|getTrustManager
argument_list|()
decl_stmt|;
name|connector
operator|=
name|broker
operator|.
name|addSslConnector
argument_list|(
name|DEFAULT_HOST
argument_list|,
name|km
argument_list|,
name|tm
argument_list|,
literal|null
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
comment|// for client side
name|SslTransportFactory
name|sslFactory
init|=
operator|new
name|SslTransportFactory
argument_list|()
decl_stmt|;
name|SslContext
name|ctx
init|=
operator|new
name|SslContext
argument_list|(
name|km
argument_list|,
name|tm
argument_list|,
literal|null
argument_list|)
decl_stmt|;
name|SslContext
operator|.
name|setCurrentSslContext
argument_list|(
name|ctx
argument_list|)
expr_stmt|;
name|TransportFactory
operator|.
name|registerTransportFactory
argument_list|(
literal|"ssl"
argument_list|,
name|sslFactory
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

