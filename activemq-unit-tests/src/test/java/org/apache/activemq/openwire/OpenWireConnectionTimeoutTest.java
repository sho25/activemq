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
name|openwire
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
name|java
operator|.
name|io
operator|.
name|IOException
import|;
end_import

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
name|util
operator|.
name|Arrays
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Collection
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Vector
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|concurrent
operator|.
name|TimeUnit
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
name|transport
operator|.
name|tcp
operator|.
name|TcpTransportServer
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
name|Wait
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
name|junit
operator|.
name|runner
operator|.
name|RunWith
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|runners
operator|.
name|Parameterized
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|runners
operator|.
name|Parameterized
operator|.
name|Parameters
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

begin_comment
comment|/**  * Test that connection attempts that don't send the WireFormatInfo performative  * get cleaned up by the inactivity monitor.  */
end_comment

begin_class
annotation|@
name|RunWith
argument_list|(
name|Parameterized
operator|.
name|class
argument_list|)
specifier|public
class|class
name|OpenWireConnectionTimeoutTest
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
name|OpenWireConnectionTimeoutTest
operator|.
name|class
argument_list|)
decl_stmt|;
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
specifier|public
specifier|static
specifier|final
name|String
name|KEYSTORE_TYPE
init|=
literal|"jks"
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|String
name|PASSWORD
init|=
literal|"password"
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|String
name|SERVER_KEYSTORE
init|=
literal|"src/test/resources/server.keystore"
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|String
name|TRUST_KEYSTORE
init|=
literal|"src/test/resources/client.keystore"
decl_stmt|;
specifier|private
name|Socket
name|connection
decl_stmt|;
specifier|protected
name|String
name|connectorScheme
decl_stmt|;
specifier|protected
name|int
name|port
decl_stmt|;
specifier|protected
name|BrokerService
name|brokerService
decl_stmt|;
specifier|protected
name|Vector
argument_list|<
name|Throwable
argument_list|>
name|exceptions
init|=
operator|new
name|Vector
argument_list|<
name|Throwable
argument_list|>
argument_list|()
decl_stmt|;
static|static
block|{
name|System
operator|.
name|setProperty
argument_list|(
literal|"javax.net.ssl.trustStore"
argument_list|,
name|TRUST_KEYSTORE
argument_list|)
expr_stmt|;
name|System
operator|.
name|setProperty
argument_list|(
literal|"javax.net.ssl.trustStorePassword"
argument_list|,
name|PASSWORD
argument_list|)
expr_stmt|;
name|System
operator|.
name|setProperty
argument_list|(
literal|"javax.net.ssl.trustStoreType"
argument_list|,
name|KEYSTORE_TYPE
argument_list|)
expr_stmt|;
name|System
operator|.
name|setProperty
argument_list|(
literal|"javax.net.ssl.keyStore"
argument_list|,
name|SERVER_KEYSTORE
argument_list|)
expr_stmt|;
name|System
operator|.
name|setProperty
argument_list|(
literal|"javax.net.ssl.keyStoreType"
argument_list|,
name|KEYSTORE_TYPE
argument_list|)
expr_stmt|;
name|System
operator|.
name|setProperty
argument_list|(
literal|"javax.net.ssl.keyStorePassword"
argument_list|,
name|PASSWORD
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Parameters
argument_list|(
name|name
operator|=
literal|"{0}"
argument_list|)
specifier|public
specifier|static
name|Collection
argument_list|<
name|Object
index|[]
argument_list|>
name|data
parameter_list|()
block|{
return|return
name|Arrays
operator|.
name|asList
argument_list|(
operator|new
name|Object
index|[]
index|[]
block|{
block|{
literal|"tcp"
block|}
block|,
block|{
literal|"ssl"
block|}
block|,
block|{
literal|"nio"
block|}
block|,
block|{
literal|"nio+ssl"
block|}
block|,
block|{
literal|"auto"
block|}
block|,
block|{
literal|"auto+ssl"
block|}
block|,
block|{
literal|"auto+nio"
block|}
block|,
block|{
literal|"auto+nio+ssl"
block|}
block|}
argument_list|)
return|;
block|}
specifier|public
name|OpenWireConnectionTimeoutTest
parameter_list|(
name|String
name|connectorScheme
parameter_list|)
block|{
name|this
operator|.
name|connectorScheme
operator|=
name|connectorScheme
expr_stmt|;
block|}
specifier|protected
name|String
name|getConnectorScheme
parameter_list|()
block|{
return|return
name|connectorScheme
return|;
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
literal|"========== start "
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
name|Throwable
name|e
parameter_list|)
block|{}
name|connection
operator|=
literal|null
expr_stmt|;
block|}
name|stopBroker
argument_list|()
expr_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"========== start "
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
name|getAdditionalConfig
parameter_list|()
block|{
return|return
literal|"?transport.connectAttemptTimeout=1200&protocolDetectionTimeOut=1200"
return|;
block|}
annotation|@
name|Test
argument_list|(
name|timeout
operator|=
literal|90000
argument_list|)
specifier|public
name|void
name|testInactivityMonitor
parameter_list|()
throws|throws
name|Exception
block|{
name|Thread
name|t1
init|=
operator|new
name|Thread
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|void
name|run
parameter_list|()
block|{
try|try
block|{
name|connection
operator|=
name|createConnection
argument_list|()
expr_stmt|;
name|connection
operator|.
name|getOutputStream
argument_list|()
operator|.
name|write
argument_list|(
literal|'A'
argument_list|)
expr_stmt|;
name|connection
operator|.
name|getOutputStream
argument_list|()
operator|.
name|flush
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|ex
parameter_list|)
block|{
name|LOG
operator|.
name|error
argument_list|(
literal|"unexpected exception on connect/disconnect"
argument_list|,
name|ex
argument_list|)
expr_stmt|;
name|exceptions
operator|.
name|add
argument_list|(
name|ex
argument_list|)
expr_stmt|;
block|}
block|}
block|}
decl_stmt|;
name|t1
operator|.
name|start
argument_list|()
expr_stmt|;
name|assertTrue
argument_list|(
literal|"one connection"
argument_list|,
name|Wait
operator|.
name|waitFor
argument_list|(
operator|new
name|Wait
operator|.
name|Condition
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|boolean
name|isSatisified
parameter_list|()
throws|throws
name|Exception
block|{
name|TcpTransportServer
name|server
init|=
operator|(
name|TcpTransportServer
operator|)
name|brokerService
operator|.
name|getTransportConnectorByScheme
argument_list|(
name|getConnectorScheme
argument_list|()
argument_list|)
operator|.
name|getServer
argument_list|()
decl_stmt|;
return|return
literal|1
operator|==
name|server
operator|.
name|getCurrentTransportCount
argument_list|()
operator|.
name|get
argument_list|()
return|;
block|}
block|}
argument_list|,
name|TimeUnit
operator|.
name|SECONDS
operator|.
name|toMillis
argument_list|(
literal|15
argument_list|)
argument_list|,
name|TimeUnit
operator|.
name|MILLISECONDS
operator|.
name|toMillis
argument_list|(
literal|250
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
comment|// and it should be closed due to inactivity
name|assertTrue
argument_list|(
literal|"no dangling connections"
argument_list|,
name|Wait
operator|.
name|waitFor
argument_list|(
operator|new
name|Wait
operator|.
name|Condition
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|boolean
name|isSatisified
parameter_list|()
throws|throws
name|Exception
block|{
name|TcpTransportServer
name|server
init|=
operator|(
name|TcpTransportServer
operator|)
name|brokerService
operator|.
name|getTransportConnectorByScheme
argument_list|(
name|getConnectorScheme
argument_list|()
argument_list|)
operator|.
name|getServer
argument_list|()
decl_stmt|;
return|return
literal|0
operator|==
name|server
operator|.
name|getCurrentTransportCount
argument_list|()
operator|.
name|get
argument_list|()
return|;
block|}
block|}
argument_list|,
name|TimeUnit
operator|.
name|SECONDS
operator|.
name|toMillis
argument_list|(
literal|15
argument_list|)
argument_list|,
name|TimeUnit
operator|.
name|MILLISECONDS
operator|.
name|toMillis
argument_list|(
literal|500
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
literal|"no exceptions"
argument_list|,
name|exceptions
operator|.
name|isEmpty
argument_list|()
argument_list|)
expr_stmt|;
block|}
specifier|protected
name|Socket
name|createConnection
parameter_list|()
throws|throws
name|IOException
block|{
name|boolean
name|useSsl
init|=
literal|false
decl_stmt|;
switch|switch
condition|(
name|connectorScheme
condition|)
block|{
case|case
literal|"tcp"
case|:
case|case
literal|"auto"
case|:
case|case
literal|"nio"
case|:
case|case
literal|"auto+nio"
case|:
break|break;
case|case
literal|"ssl"
case|:
case|case
literal|"auto+ssl"
case|:
case|case
literal|"nio+ssl"
case|:
case|case
literal|"auto+nio+ssl"
case|:
name|useSsl
operator|=
literal|true
expr_stmt|;
break|break;
default|default:
throw|throw
operator|new
name|IOException
argument_list|(
literal|"Invalid OpenWire connector scheme passed to test."
argument_list|)
throw|;
block|}
if|if
condition|(
name|useSsl
condition|)
block|{
return|return
name|SSLSocketFactory
operator|.
name|getDefault
argument_list|()
operator|.
name|createSocket
argument_list|(
literal|"localhost"
argument_list|,
name|port
argument_list|)
return|;
block|}
else|else
block|{
return|return
operator|new
name|Socket
argument_list|(
literal|"localhost"
argument_list|,
name|port
argument_list|)
return|;
block|}
block|}
specifier|protected
name|void
name|startBroker
parameter_list|()
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
name|setSchedulerSupport
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
name|setUseJmx
argument_list|(
literal|false
argument_list|)
expr_stmt|;
name|brokerService
operator|.
name|getManagementContext
argument_list|()
operator|.
name|setCreateConnector
argument_list|(
literal|false
argument_list|)
expr_stmt|;
comment|// Setup SSL context...
specifier|final
name|File
name|classesDir
init|=
operator|new
name|File
argument_list|(
name|OpenWireConnectionTimeoutTest
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
literal|"../../src/test/resources/server.keystore"
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
name|System
operator|.
name|setProperty
argument_list|(
literal|"javax.net.ssl.trustStore"
argument_list|,
name|keystore
operator|.
name|getCanonicalPath
argument_list|()
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
name|keystore
operator|.
name|getCanonicalPath
argument_list|()
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
name|TransportConnector
name|connector
init|=
literal|null
decl_stmt|;
switch|switch
condition|(
name|connectorScheme
condition|)
block|{
case|case
literal|"tcp"
case|:
name|connector
operator|=
name|brokerService
operator|.
name|addConnector
argument_list|(
literal|"tcp://0.0.0.0:0"
operator|+
name|getAdditionalConfig
argument_list|()
argument_list|)
expr_stmt|;
break|break;
case|case
literal|"nio"
case|:
name|connector
operator|=
name|brokerService
operator|.
name|addConnector
argument_list|(
literal|"nio://0.0.0.0:0"
operator|+
name|getAdditionalConfig
argument_list|()
argument_list|)
expr_stmt|;
break|break;
case|case
literal|"ssl"
case|:
name|connector
operator|=
name|brokerService
operator|.
name|addConnector
argument_list|(
literal|"ssl://0.0.0.0:0"
operator|+
name|getAdditionalConfig
argument_list|()
argument_list|)
expr_stmt|;
break|break;
case|case
literal|"nio+ssl"
case|:
name|connector
operator|=
name|brokerService
operator|.
name|addConnector
argument_list|(
literal|"nio+ssl://0.0.0.0:0"
operator|+
name|getAdditionalConfig
argument_list|()
argument_list|)
expr_stmt|;
break|break;
case|case
literal|"auto"
case|:
name|connector
operator|=
name|brokerService
operator|.
name|addConnector
argument_list|(
literal|"auto://0.0.0.0:0"
operator|+
name|getAdditionalConfig
argument_list|()
argument_list|)
expr_stmt|;
break|break;
case|case
literal|"auto+nio"
case|:
name|connector
operator|=
name|brokerService
operator|.
name|addConnector
argument_list|(
literal|"auto+nio://0.0.0.0:0"
operator|+
name|getAdditionalConfig
argument_list|()
argument_list|)
expr_stmt|;
break|break;
case|case
literal|"auto+ssl"
case|:
name|connector
operator|=
name|brokerService
operator|.
name|addConnector
argument_list|(
literal|"auto+ssl://0.0.0.0:0"
operator|+
name|getAdditionalConfig
argument_list|()
argument_list|)
expr_stmt|;
break|break;
case|case
literal|"auto+nio+ssl"
case|:
name|connector
operator|=
name|brokerService
operator|.
name|addConnector
argument_list|(
literal|"auto+nio+ssl://0.0.0.0:0"
operator|+
name|getAdditionalConfig
argument_list|()
argument_list|)
expr_stmt|;
break|break;
default|default:
throw|throw
operator|new
name|IOException
argument_list|(
literal|"Invalid OpenWire connector scheme passed to test."
argument_list|)
throw|;
block|}
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
name|port
operator|=
name|connector
operator|.
name|getPublishableConnectURI
argument_list|()
operator|.
name|getPort
argument_list|()
expr_stmt|;
block|}
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
operator|.
name|waitUntilStopped
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

