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
name|mqtt
package|;
end_package

begin_import
import|import
name|java
operator|.
name|security
operator|.
name|SecureRandom
import|;
end_import

begin_import
import|import
name|java
operator|.
name|security
operator|.
name|cert
operator|.
name|CertificateException
import|;
end_import

begin_import
import|import
name|java
operator|.
name|security
operator|.
name|cert
operator|.
name|X509Certificate
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
name|SSLContext
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

begin_import
import|import
name|javax
operator|.
name|net
operator|.
name|ssl
operator|.
name|X509TrustManager
import|;
end_import

begin_import
import|import
name|org
operator|.
name|fusesource
operator|.
name|mqtt
operator|.
name|client
operator|.
name|MQTT
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
name|BlockJUnit4ClassRunner
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
annotation|@
name|RunWith
argument_list|(
name|BlockJUnit4ClassRunner
operator|.
name|class
argument_list|)
specifier|public
class|class
name|MQTTSSLTest
extends|extends
name|MQTTTest
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
name|MQTTSSLTest
operator|.
name|class
argument_list|)
decl_stmt|;
specifier|public
name|void
name|setUp
parameter_list|()
throws|throws
name|Exception
block|{
name|String
name|basedir
init|=
name|basedir
argument_list|()
operator|.
name|getPath
argument_list|()
decl_stmt|;
name|System
operator|.
name|setProperty
argument_list|(
literal|"javax.net.ssl.trustStore"
argument_list|,
name|basedir
operator|+
literal|"/src/test/resources/client.keystore"
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
name|basedir
operator|+
literal|"/src/test/resources/server.keystore"
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
name|super
operator|.
name|setUp
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Override
specifier|protected
name|String
name|getProtocolScheme
parameter_list|()
block|{
return|return
literal|"mqtt+ssl"
return|;
block|}
specifier|protected
name|MQTT
name|createMQTTConnection
parameter_list|()
throws|throws
name|Exception
block|{
name|MQTT
name|mqtt
init|=
operator|new
name|MQTT
argument_list|()
decl_stmt|;
name|mqtt
operator|.
name|setConnectAttemptsMax
argument_list|(
literal|1
argument_list|)
expr_stmt|;
name|mqtt
operator|.
name|setReconnectAttemptsMax
argument_list|(
literal|0
argument_list|)
expr_stmt|;
name|mqtt
operator|.
name|setTracer
argument_list|(
name|createTracer
argument_list|()
argument_list|)
expr_stmt|;
name|mqtt
operator|.
name|setHost
argument_list|(
literal|"ssl://localhost:"
operator|+
name|mqttConnector
operator|.
name|getConnectUri
argument_list|()
operator|.
name|getPort
argument_list|()
argument_list|)
expr_stmt|;
name|SSLContext
name|ctx
init|=
name|SSLContext
operator|.
name|getInstance
argument_list|(
literal|"TLS"
argument_list|)
decl_stmt|;
name|ctx
operator|.
name|init
argument_list|(
operator|new
name|KeyManager
index|[
literal|0
index|]
argument_list|,
operator|new
name|TrustManager
index|[]
block|{
operator|new
name|DefaultTrustManager
argument_list|()
block|}
argument_list|,
operator|new
name|SecureRandom
argument_list|()
argument_list|)
expr_stmt|;
name|mqtt
operator|.
name|setSslContext
argument_list|(
name|ctx
argument_list|)
expr_stmt|;
return|return
name|mqtt
return|;
block|}
specifier|protected
name|MQTT
name|createMQTTConnection
parameter_list|(
name|String
name|clientId
parameter_list|,
name|boolean
name|clean
parameter_list|)
throws|throws
name|Exception
block|{
name|MQTT
name|mqtt
init|=
name|createMQTTConnection
argument_list|()
decl_stmt|;
if|if
condition|(
name|clientId
operator|!=
literal|null
condition|)
block|{
name|mqtt
operator|.
name|setClientId
argument_list|(
name|clientId
argument_list|)
expr_stmt|;
block|}
name|mqtt
operator|.
name|setCleanSession
argument_list|(
name|clean
argument_list|)
expr_stmt|;
return|return
name|mqtt
return|;
block|}
specifier|protected
name|void
name|initializeConnection
parameter_list|(
name|MQTTClientProvider
name|provider
parameter_list|)
throws|throws
name|Exception
block|{
name|SSLContext
name|ctx
init|=
name|SSLContext
operator|.
name|getInstance
argument_list|(
literal|"TLS"
argument_list|)
decl_stmt|;
name|ctx
operator|.
name|init
argument_list|(
operator|new
name|KeyManager
index|[
literal|0
index|]
argument_list|,
operator|new
name|TrustManager
index|[]
block|{
operator|new
name|DefaultTrustManager
argument_list|()
block|}
argument_list|,
operator|new
name|SecureRandom
argument_list|()
argument_list|)
expr_stmt|;
name|provider
operator|.
name|setSslContext
argument_list|(
name|ctx
argument_list|)
expr_stmt|;
name|provider
operator|.
name|connect
argument_list|(
literal|"ssl://localhost:"
operator|+
name|mqttConnector
operator|.
name|getConnectUri
argument_list|()
operator|.
name|getPort
argument_list|()
argument_list|)
expr_stmt|;
block|}
specifier|static
class|class
name|DefaultTrustManager
implements|implements
name|X509TrustManager
block|{
specifier|public
name|void
name|checkClientTrusted
parameter_list|(
name|X509Certificate
index|[]
name|x509Certificates
parameter_list|,
name|String
name|s
parameter_list|)
throws|throws
name|CertificateException
block|{         }
specifier|public
name|void
name|checkServerTrusted
parameter_list|(
name|X509Certificate
index|[]
name|x509Certificates
parameter_list|,
name|String
name|s
parameter_list|)
throws|throws
name|CertificateException
block|{         }
specifier|public
name|X509Certificate
index|[]
name|getAcceptedIssuers
parameter_list|()
block|{
return|return
operator|new
name|X509Certificate
index|[
literal|0
index|]
return|;
block|}
block|}
block|}
end_class

end_unit

