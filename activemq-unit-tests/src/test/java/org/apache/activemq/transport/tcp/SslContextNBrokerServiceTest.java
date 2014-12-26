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
name|tcp
package|;
end_package

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
name|java
operator|.
name|util
operator|.
name|Iterator
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Map
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
name|SSLSocket
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

begin_import
import|import
name|org
operator|.
name|springframework
operator|.
name|context
operator|.
name|support
operator|.
name|ClassPathXmlApplicationContext
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
name|*
import|;
end_import

begin_class
specifier|public
class|class
name|SslContextNBrokerServiceTest
block|{
specifier|private
specifier|static
specifier|final
specifier|transient
name|Logger
name|LOG
init|=
name|LoggerFactory
operator|.
name|getLogger
argument_list|(
name|SslContextNBrokerServiceTest
operator|.
name|class
argument_list|)
decl_stmt|;
specifier|private
name|ClassPathXmlApplicationContext
name|context
decl_stmt|;
name|Map
argument_list|<
name|String
argument_list|,
name|BrokerService
argument_list|>
name|beansOfType
decl_stmt|;
annotation|@
name|Test
argument_list|(
name|timeout
operator|=
literal|3
operator|*
literal|60
operator|*
literal|1000
argument_list|)
specifier|public
name|void
name|testDummyConfigurationIsolation
parameter_list|()
throws|throws
name|Exception
block|{
name|assertTrue
argument_list|(
literal|"dummy bean has dummy cert"
argument_list|,
name|verifyCredentials
argument_list|(
literal|"dummy"
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
argument_list|(
name|timeout
operator|=
literal|3
operator|*
literal|60
operator|*
literal|1000
argument_list|)
specifier|public
name|void
name|testActiveMQDotOrgConfigurationIsolation
parameter_list|()
throws|throws
name|Exception
block|{
name|assertTrue
argument_list|(
literal|"good bean has amq cert"
argument_list|,
name|verifyCredentials
argument_list|(
literal|"activemq.org"
argument_list|)
argument_list|)
expr_stmt|;
block|}
specifier|private
name|boolean
name|verifyCredentials
parameter_list|(
name|String
name|name
parameter_list|)
throws|throws
name|Exception
block|{
name|boolean
name|result
init|=
literal|false
decl_stmt|;
name|BrokerService
name|broker
init|=
name|getBroker
argument_list|(
name|name
argument_list|)
decl_stmt|;
name|assertNotNull
argument_list|(
name|name
argument_list|,
name|broker
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
try|try
block|{
name|result
operator|=
name|verifySslCredentials
argument_list|(
name|broker
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|broker
operator|.
name|stop
argument_list|()
expr_stmt|;
block|}
return|return
name|result
return|;
block|}
specifier|private
name|boolean
name|verifySslCredentials
parameter_list|(
name|BrokerService
name|broker
parameter_list|)
throws|throws
name|Exception
block|{
name|TransportConnector
name|connector
init|=
name|broker
operator|.
name|getTransportConnectors
argument_list|()
operator|.
name|get
argument_list|(
literal|0
argument_list|)
decl_stmt|;
name|URI
name|brokerUri
init|=
name|connector
operator|.
name|getConnectUri
argument_list|()
decl_stmt|;
name|SSLContext
name|context
init|=
name|SSLContext
operator|.
name|getInstance
argument_list|(
literal|"TLS"
argument_list|)
decl_stmt|;
name|CertChainCatcher
name|catcher
init|=
operator|new
name|CertChainCatcher
argument_list|()
decl_stmt|;
name|context
operator|.
name|init
argument_list|(
literal|null
argument_list|,
operator|new
name|TrustManager
index|[]
block|{
name|catcher
block|}
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|SSLSocketFactory
name|factory
init|=
name|context
operator|.
name|getSocketFactory
argument_list|()
decl_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"Connecting to broker: "
operator|+
name|broker
operator|.
name|getBrokerName
argument_list|()
operator|+
literal|" on: "
operator|+
name|brokerUri
operator|.
name|getHost
argument_list|()
operator|+
literal|":"
operator|+
name|brokerUri
operator|.
name|getPort
argument_list|()
argument_list|)
expr_stmt|;
name|SSLSocket
name|socket
init|=
operator|(
name|SSLSocket
operator|)
name|factory
operator|.
name|createSocket
argument_list|(
name|brokerUri
operator|.
name|getHost
argument_list|()
argument_list|,
name|brokerUri
operator|.
name|getPort
argument_list|()
argument_list|)
decl_stmt|;
name|socket
operator|.
name|setSoTimeout
argument_list|(
literal|2
operator|*
literal|60
operator|*
literal|1000
argument_list|)
expr_stmt|;
name|socket
operator|.
name|startHandshake
argument_list|()
expr_stmt|;
name|socket
operator|.
name|close
argument_list|()
expr_stmt|;
name|boolean
name|matches
init|=
literal|false
decl_stmt|;
if|if
condition|(
name|catcher
operator|.
name|serverCerts
operator|!=
literal|null
condition|)
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
name|catcher
operator|.
name|serverCerts
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|X509Certificate
name|cert
init|=
name|catcher
operator|.
name|serverCerts
index|[
name|i
index|]
decl_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|" "
operator|+
operator|(
name|i
operator|+
literal|1
operator|)
operator|+
literal|" Issuer "
operator|+
name|cert
operator|.
name|getIssuerDN
argument_list|()
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|catcher
operator|.
name|serverCerts
operator|.
name|length
operator|>
literal|0
condition|)
block|{
name|String
name|issuer
init|=
name|catcher
operator|.
name|serverCerts
index|[
literal|0
index|]
operator|.
name|getIssuerDN
argument_list|()
operator|.
name|toString
argument_list|()
decl_stmt|;
if|if
condition|(
name|issuer
operator|.
name|indexOf
argument_list|(
name|broker
operator|.
name|getBrokerName
argument_list|()
argument_list|)
operator|!=
operator|-
literal|1
condition|)
block|{
name|matches
operator|=
literal|true
expr_stmt|;
block|}
block|}
block|}
return|return
name|matches
return|;
block|}
specifier|private
name|BrokerService
name|getBroker
parameter_list|(
name|String
name|name
parameter_list|)
block|{
name|BrokerService
name|result
init|=
literal|null
decl_stmt|;
name|Iterator
argument_list|<
name|BrokerService
argument_list|>
name|iterator
init|=
name|beansOfType
operator|.
name|values
argument_list|()
operator|.
name|iterator
argument_list|()
decl_stmt|;
while|while
condition|(
name|iterator
operator|.
name|hasNext
argument_list|()
condition|)
block|{
name|BrokerService
name|candidate
init|=
name|iterator
operator|.
name|next
argument_list|()
decl_stmt|;
if|if
condition|(
name|candidate
operator|.
name|getBrokerName
argument_list|()
operator|.
name|equals
argument_list|(
name|name
argument_list|)
condition|)
block|{
name|result
operator|=
name|candidate
expr_stmt|;
break|break;
block|}
block|}
return|return
name|result
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
comment|// System.setProperty("javax.net.debug", "ssl");
name|Thread
operator|.
name|currentThread
argument_list|()
operator|.
name|setContextClassLoader
argument_list|(
name|SslContextNBrokerServiceTest
operator|.
name|class
operator|.
name|getClassLoader
argument_list|()
argument_list|)
expr_stmt|;
name|context
operator|=
operator|new
name|ClassPathXmlApplicationContext
argument_list|(
literal|"org/apache/activemq/transport/tcp/n-brokers-ssl.xml"
argument_list|)
expr_stmt|;
name|beansOfType
operator|=
name|context
operator|.
name|getBeansOfType
argument_list|(
name|BrokerService
operator|.
name|class
argument_list|)
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
name|context
operator|.
name|destroy
argument_list|()
expr_stmt|;
block|}
class|class
name|CertChainCatcher
implements|implements
name|X509TrustManager
block|{
name|X509Certificate
index|[]
name|serverCerts
decl_stmt|;
annotation|@
name|Override
specifier|public
name|void
name|checkClientTrusted
parameter_list|(
name|X509Certificate
index|[]
name|arg0
parameter_list|,
name|String
name|arg1
parameter_list|)
throws|throws
name|CertificateException
block|{         }
annotation|@
name|Override
specifier|public
name|void
name|checkServerTrusted
parameter_list|(
name|X509Certificate
index|[]
name|arg0
parameter_list|,
name|String
name|arg1
parameter_list|)
throws|throws
name|CertificateException
block|{
name|serverCerts
operator|=
name|arg0
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|X509Certificate
index|[]
name|getAcceptedIssuers
parameter_list|()
block|{
return|return
literal|null
return|;
block|}
block|}
block|}
end_class

end_unit

