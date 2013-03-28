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
name|net
operator|.
name|Socket
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
name|management
operator|.
name|ObjectName
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|net
operator|.
name|SocketFactory
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
name|SSLSocketFactory
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
name|ActiveMQConnection
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
name|ActiveMQSslConnectionFactory
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
name|activemq
operator|.
name|transport
operator|.
name|stomp
operator|.
name|Stomp
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
name|stomp
operator|.
name|StompConnection
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
name|stomp
operator|.
name|StompFrame
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

begin_comment
comment|/**  *  */
end_comment

begin_class
specifier|public
class|class
name|AMQ4126Test
block|{
specifier|protected
name|BrokerService
name|broker
decl_stmt|;
specifier|protected
name|String
name|java_security_auth_login_config
init|=
literal|"java.security.auth.login.config"
decl_stmt|;
specifier|protected
name|String
name|xbean
init|=
literal|"xbean:"
decl_stmt|;
specifier|protected
name|String
name|confBase
init|=
literal|"src/test/resources/org/apache/activemq/bugs/amq4126"
decl_stmt|;
specifier|protected
name|String
name|certBase
init|=
literal|"src/test/resources/org/apache/activemq/security"
decl_stmt|;
specifier|protected
name|String
name|JaasStompSSLBroker_xml
init|=
literal|"JaasStompSSLBroker.xml"
decl_stmt|;
specifier|protected
name|StompConnection
name|stompConnection
init|=
operator|new
name|StompConnection
argument_list|()
decl_stmt|;
specifier|private
specifier|final
specifier|static
name|String
name|destinationName
init|=
literal|"TEST.QUEUE"
decl_stmt|;
specifier|protected
name|String
name|oldLoginConf
init|=
literal|null
decl_stmt|;
annotation|@
name|Before
specifier|public
name|void
name|before
parameter_list|()
throws|throws
name|Exception
block|{
if|if
condition|(
name|System
operator|.
name|getProperty
argument_list|(
name|java_security_auth_login_config
argument_list|)
operator|!=
literal|null
condition|)
block|{
name|oldLoginConf
operator|=
name|System
operator|.
name|getProperty
argument_list|(
name|java_security_auth_login_config
argument_list|)
expr_stmt|;
block|}
name|System
operator|.
name|setProperty
argument_list|(
name|java_security_auth_login_config
argument_list|,
name|confBase
operator|+
literal|"/login.config"
argument_list|)
expr_stmt|;
name|broker
operator|=
name|BrokerFactory
operator|.
name|createBroker
argument_list|(
name|xbean
operator|+
name|confBase
operator|+
literal|"/"
operator|+
name|JaasStompSSLBroker_xml
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
name|setUseJmx
argument_list|(
literal|true
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
name|after
parameter_list|()
throws|throws
name|Exception
block|{
name|broker
operator|.
name|stop
argument_list|()
expr_stmt|;
if|if
condition|(
name|oldLoginConf
operator|!=
literal|null
condition|)
block|{
name|System
operator|.
name|setProperty
argument_list|(
name|java_security_auth_login_config
argument_list|,
name|oldLoginConf
argument_list|)
expr_stmt|;
block|}
block|}
specifier|public
name|Socket
name|createSocket
parameter_list|(
name|String
name|host
parameter_list|,
name|int
name|port
parameter_list|)
throws|throws
name|Exception
block|{
name|System
operator|.
name|setProperty
argument_list|(
literal|"javax.net.ssl.trustStore"
argument_list|,
name|certBase
operator|+
literal|"/broker1.ks"
argument_list|)
expr_stmt|;
name|System
operator|.
name|setProperty
argument_list|(
literal|"javax.net.ssl.trustStorePassword"
argument_list|,
literal|"password"
argument_list|)
expr_stmt|;
name|System
operator|.
name|setProperty
argument_list|(
literal|"javax.net.ssl.trustStoreType"
argument_list|,
literal|"jks"
argument_list|)
expr_stmt|;
name|System
operator|.
name|setProperty
argument_list|(
literal|"javax.net.ssl.keyStore"
argument_list|,
name|certBase
operator|+
literal|"/client.ks"
argument_list|)
expr_stmt|;
name|System
operator|.
name|setProperty
argument_list|(
literal|"javax.net.ssl.keyStorePassword"
argument_list|,
literal|"password"
argument_list|)
expr_stmt|;
name|System
operator|.
name|setProperty
argument_list|(
literal|"javax.net.ssl.keyStoreType"
argument_list|,
literal|"jks"
argument_list|)
expr_stmt|;
name|SocketFactory
name|factory
init|=
name|SSLSocketFactory
operator|.
name|getDefault
argument_list|()
decl_stmt|;
return|return
name|factory
operator|.
name|createSocket
argument_list|(
name|host
argument_list|,
name|port
argument_list|)
return|;
block|}
specifier|public
name|void
name|stompConnectTo
parameter_list|(
name|String
name|connectorName
parameter_list|,
name|String
name|extraHeaders
parameter_list|)
throws|throws
name|Exception
block|{
name|String
name|host
init|=
name|broker
operator|.
name|getConnectorByName
argument_list|(
name|connectorName
argument_list|)
operator|.
name|getConnectUri
argument_list|()
operator|.
name|getHost
argument_list|()
decl_stmt|;
name|int
name|port
init|=
name|broker
operator|.
name|getConnectorByName
argument_list|(
name|connectorName
argument_list|)
operator|.
name|getConnectUri
argument_list|()
operator|.
name|getPort
argument_list|()
decl_stmt|;
name|stompConnection
operator|.
name|open
argument_list|(
name|createSocket
argument_list|(
name|host
argument_list|,
name|port
argument_list|)
argument_list|)
expr_stmt|;
name|String
name|extra
init|=
name|extraHeaders
operator|!=
literal|null
condition|?
name|extraHeaders
else|:
literal|"\n"
decl_stmt|;
name|stompConnection
operator|.
name|sendFrame
argument_list|(
literal|"CONNECT\n"
operator|+
name|extra
operator|+
literal|"\n"
operator|+
name|Stomp
operator|.
name|NULL
argument_list|)
expr_stmt|;
name|StompFrame
name|f
init|=
name|stompConnection
operator|.
name|receive
argument_list|()
decl_stmt|;
name|TestCase
operator|.
name|assertEquals
argument_list|(
name|f
operator|.
name|getBody
argument_list|()
argument_list|,
literal|"CONNECTED"
argument_list|,
name|f
operator|.
name|getAction
argument_list|()
argument_list|)
expr_stmt|;
name|stompConnection
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testStompSSLWithUsernameAndPassword
parameter_list|()
throws|throws
name|Exception
block|{
name|stompConnectTo
argument_list|(
literal|"stomp+ssl"
argument_list|,
literal|"login:system\n"
operator|+
literal|"passcode:manager\n"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testStompSSLWithCertificate
parameter_list|()
throws|throws
name|Exception
block|{
name|stompConnectTo
argument_list|(
literal|"stomp+ssl"
argument_list|,
literal|null
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testStompNIOSSLWithUsernameAndPassword
parameter_list|()
throws|throws
name|Exception
block|{
name|stompConnectTo
argument_list|(
literal|"stomp+nio+ssl"
argument_list|,
literal|"login:system\n"
operator|+
literal|"passcode:manager\n"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testStompNIOSSLWithCertificate
parameter_list|()
throws|throws
name|Exception
block|{
name|stompConnectTo
argument_list|(
literal|"stomp+nio+ssl"
argument_list|,
literal|null
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|openwireConnectTo
parameter_list|(
name|String
name|connectorName
parameter_list|,
name|String
name|username
parameter_list|,
name|String
name|password
parameter_list|)
throws|throws
name|Exception
block|{
name|URI
name|brokerURI
init|=
name|broker
operator|.
name|getConnectorByName
argument_list|(
name|connectorName
argument_list|)
operator|.
name|getConnectUri
argument_list|()
decl_stmt|;
name|String
name|uri
init|=
literal|"ssl://"
operator|+
name|brokerURI
operator|.
name|getHost
argument_list|()
operator|+
literal|":"
operator|+
name|brokerURI
operator|.
name|getPort
argument_list|()
decl_stmt|;
name|ActiveMQSslConnectionFactory
name|cf
init|=
operator|new
name|ActiveMQSslConnectionFactory
argument_list|(
name|uri
argument_list|)
decl_stmt|;
name|cf
operator|.
name|setTrustStore
argument_list|(
literal|"org/apache/activemq/security/broker1.ks"
argument_list|)
expr_stmt|;
name|cf
operator|.
name|setTrustStorePassword
argument_list|(
literal|"password"
argument_list|)
expr_stmt|;
name|cf
operator|.
name|setKeyStore
argument_list|(
literal|"org/apache/activemq/security/client.ks"
argument_list|)
expr_stmt|;
name|cf
operator|.
name|setKeyStorePassword
argument_list|(
literal|"password"
argument_list|)
expr_stmt|;
name|ActiveMQConnection
name|connection
init|=
literal|null
decl_stmt|;
if|if
condition|(
name|username
operator|!=
literal|null
operator|||
name|password
operator|!=
literal|null
condition|)
block|{
name|connection
operator|=
operator|(
name|ActiveMQConnection
operator|)
name|cf
operator|.
name|createConnection
argument_list|(
name|username
argument_list|,
name|password
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|connection
operator|=
operator|(
name|ActiveMQConnection
operator|)
name|cf
operator|.
name|createConnection
argument_list|()
expr_stmt|;
block|}
name|TestCase
operator|.
name|assertNotNull
argument_list|(
name|connection
argument_list|)
expr_stmt|;
name|connection
operator|.
name|start
argument_list|()
expr_stmt|;
name|connection
operator|.
name|stop
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testOpenwireSSLWithUsernameAndPassword
parameter_list|()
throws|throws
name|Exception
block|{
name|openwireConnectTo
argument_list|(
literal|"openwire+ssl"
argument_list|,
literal|"system"
argument_list|,
literal|"manager"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testOpenwireSSLWithCertificate
parameter_list|()
throws|throws
name|Exception
block|{
name|openwireConnectTo
argument_list|(
literal|"openwire+ssl"
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testOpenwireNIOSSLWithUsernameAndPassword
parameter_list|()
throws|throws
name|Exception
block|{
name|openwireConnectTo
argument_list|(
literal|"openwire+nio+ssl"
argument_list|,
literal|"system"
argument_list|,
literal|"mmanager"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testOpenwireNIOSSLWithCertificate
parameter_list|()
throws|throws
name|Exception
block|{
name|openwireConnectTo
argument_list|(
literal|"openwire+nio+ssl"
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testJmx
parameter_list|()
throws|throws
name|Exception
block|{
name|TestCase
operator|.
name|assertFalse
argument_list|(
name|findDestination
argument_list|(
name|destinationName
argument_list|)
argument_list|)
expr_stmt|;
name|broker
operator|.
name|getAdminView
argument_list|()
operator|.
name|addQueue
argument_list|(
name|destinationName
argument_list|)
expr_stmt|;
name|TestCase
operator|.
name|assertTrue
argument_list|(
name|findDestination
argument_list|(
name|destinationName
argument_list|)
argument_list|)
expr_stmt|;
name|broker
operator|.
name|getAdminView
argument_list|()
operator|.
name|removeQueue
argument_list|(
name|destinationName
argument_list|)
expr_stmt|;
name|TestCase
operator|.
name|assertFalse
argument_list|(
name|findDestination
argument_list|(
name|destinationName
argument_list|)
argument_list|)
expr_stmt|;
block|}
specifier|private
name|boolean
name|findDestination
parameter_list|(
name|String
name|name
parameter_list|)
throws|throws
name|Exception
block|{
name|ObjectName
index|[]
name|destinations
init|=
name|broker
operator|.
name|getAdminView
argument_list|()
operator|.
name|getQueues
argument_list|()
decl_stmt|;
for|for
control|(
name|ObjectName
name|destination
range|:
name|destinations
control|)
block|{
if|if
condition|(
name|destination
operator|.
name|toString
argument_list|()
operator|.
name|contains
argument_list|(
name|name
argument_list|)
condition|)
block|{
return|return
literal|true
return|;
block|}
block|}
return|return
literal|false
return|;
block|}
block|}
end_class

end_unit

