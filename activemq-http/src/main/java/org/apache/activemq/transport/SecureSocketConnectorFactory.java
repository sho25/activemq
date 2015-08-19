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
package|;
end_package

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
name|util
operator|.
name|IntrospectionSupport
import|;
end_import

begin_import
import|import
name|org
operator|.
name|eclipse
operator|.
name|jetty
operator|.
name|server
operator|.
name|Connector
import|;
end_import

begin_import
import|import
name|org
operator|.
name|eclipse
operator|.
name|jetty
operator|.
name|server
operator|.
name|Server
import|;
end_import

begin_import
import|import
name|org
operator|.
name|eclipse
operator|.
name|jetty
operator|.
name|server
operator|.
name|ServerConnector
import|;
end_import

begin_import
import|import
name|org
operator|.
name|eclipse
operator|.
name|jetty
operator|.
name|util
operator|.
name|ssl
operator|.
name|SslContextFactory
import|;
end_import

begin_class
specifier|public
class|class
name|SecureSocketConnectorFactory
extends|extends
name|SocketConnectorFactory
block|{
specifier|private
name|String
name|keyPassword
init|=
name|System
operator|.
name|getProperty
argument_list|(
literal|"javax.net.ssl.keyPassword"
argument_list|)
decl_stmt|;
specifier|private
name|String
name|keyStorePassword
init|=
name|System
operator|.
name|getProperty
argument_list|(
literal|"javax.net.ssl.keyStorePassword"
argument_list|)
decl_stmt|;
specifier|private
name|String
name|keyStore
init|=
name|System
operator|.
name|getProperty
argument_list|(
literal|"javax.net.ssl.keyStore"
argument_list|)
decl_stmt|;
specifier|private
name|String
name|trustStorePassword
init|=
name|System
operator|.
name|getProperty
argument_list|(
literal|"javax.net.ssl.trustStorePassword"
argument_list|)
decl_stmt|;
specifier|private
name|String
name|trustStore
init|=
name|System
operator|.
name|getProperty
argument_list|(
literal|"javax.net.ssl.trustStore"
argument_list|)
decl_stmt|;
specifier|private
name|boolean
name|needClientAuth
decl_stmt|;
specifier|private
name|boolean
name|wantClientAuth
decl_stmt|;
specifier|private
name|String
name|keyStoreType
decl_stmt|;
specifier|private
name|String
name|secureRandomCertficateAlgorithm
decl_stmt|;
specifier|private
name|String
name|trustCertificateAlgorithm
decl_stmt|;
specifier|private
name|String
name|keyCertificateAlgorithm
decl_stmt|;
specifier|private
name|String
name|protocol
decl_stmt|;
specifier|private
name|String
name|auth
decl_stmt|;
specifier|private
name|SslContext
name|context
decl_stmt|;
specifier|private
name|SslContextFactory
name|contextFactory
decl_stmt|;
specifier|public
name|SecureSocketConnectorFactory
parameter_list|()
block|{      }
specifier|public
name|SecureSocketConnectorFactory
parameter_list|(
name|SslContext
name|context
parameter_list|)
block|{
name|this
operator|.
name|context
operator|=
name|context
expr_stmt|;
block|}
specifier|public
name|SecureSocketConnectorFactory
parameter_list|(
name|SslContextFactory
name|contextFactory
parameter_list|)
block|{
name|this
operator|.
name|contextFactory
operator|=
name|contextFactory
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|Connector
name|createConnector
parameter_list|(
name|Server
name|server
parameter_list|)
throws|throws
name|Exception
block|{
if|if
condition|(
name|getTransportOptions
argument_list|()
operator|!=
literal|null
condition|)
block|{
name|IntrospectionSupport
operator|.
name|setProperties
argument_list|(
name|this
argument_list|,
name|getTransportOptions
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|SSLContext
name|sslContext
init|=
name|context
operator|==
literal|null
condition|?
literal|null
else|:
name|context
operator|.
name|getSSLContext
argument_list|()
decl_stmt|;
comment|// Get a reference to the current ssl context factory...
name|SslContextFactory
name|factory
decl_stmt|;
if|if
condition|(
name|contextFactory
operator|==
literal|null
condition|)
block|{
name|factory
operator|=
operator|new
name|SslContextFactory
argument_list|()
expr_stmt|;
if|if
condition|(
name|context
operator|!=
literal|null
condition|)
block|{
comment|// Should not be using this method since it does not use all of the values
comment|// from the passed SslContext instance.....
name|factory
operator|.
name|setSslContext
argument_list|(
name|sslContext
argument_list|)
expr_stmt|;
block|}
else|else
block|{
if|if
condition|(
name|keyStore
operator|!=
literal|null
condition|)
block|{
name|factory
operator|.
name|setKeyStorePath
argument_list|(
name|keyStore
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|keyStorePassword
operator|!=
literal|null
condition|)
block|{
name|factory
operator|.
name|setKeyStorePassword
argument_list|(
name|keyStorePassword
argument_list|)
expr_stmt|;
block|}
comment|// if the keyPassword hasn't been set, default it to the
comment|// key store password
if|if
condition|(
name|keyPassword
operator|==
literal|null
operator|&&
name|keyStorePassword
operator|!=
literal|null
condition|)
block|{
name|factory
operator|.
name|setKeyStorePassword
argument_list|(
name|keyStorePassword
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|keyStoreType
operator|!=
literal|null
condition|)
block|{
name|factory
operator|.
name|setKeyStoreType
argument_list|(
name|keyStoreType
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|secureRandomCertficateAlgorithm
operator|!=
literal|null
condition|)
block|{
name|factory
operator|.
name|setSecureRandomAlgorithm
argument_list|(
name|secureRandomCertficateAlgorithm
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|keyCertificateAlgorithm
operator|!=
literal|null
condition|)
block|{
name|factory
operator|.
name|setSslKeyManagerFactoryAlgorithm
argument_list|(
name|keyCertificateAlgorithm
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|trustCertificateAlgorithm
operator|!=
literal|null
condition|)
block|{
name|factory
operator|.
name|setTrustManagerFactoryAlgorithm
argument_list|(
name|trustCertificateAlgorithm
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|protocol
operator|!=
literal|null
condition|)
block|{
name|factory
operator|.
name|setProtocol
argument_list|(
name|protocol
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|trustStore
operator|!=
literal|null
condition|)
block|{
name|setTrustStore
argument_list|(
name|factory
argument_list|,
name|trustStore
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|trustStorePassword
operator|!=
literal|null
condition|)
block|{
name|factory
operator|.
name|setTrustStorePassword
argument_list|(
name|trustStorePassword
argument_list|)
expr_stmt|;
block|}
block|}
name|factory
operator|.
name|setNeedClientAuth
argument_list|(
name|needClientAuth
argument_list|)
expr_stmt|;
name|factory
operator|.
name|setWantClientAuth
argument_list|(
name|wantClientAuth
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|factory
operator|=
name|contextFactory
expr_stmt|;
block|}
if|if
condition|(
literal|"KRB"
operator|.
name|equals
argument_list|(
name|auth
argument_list|)
operator|||
literal|"BOTH"
operator|.
name|equals
argument_list|(
name|auth
argument_list|)
operator|&&
name|Server
operator|.
name|getVersion
argument_list|()
operator|.
name|startsWith
argument_list|(
literal|"8"
argument_list|)
condition|)
block|{
comment|//return new Krb5AndCertsSslSocketConnector(factory, auth);
return|return
literal|null
return|;
block|}
else|else
block|{
name|ServerConnector
name|connector
init|=
operator|new
name|ServerConnector
argument_list|(
name|server
argument_list|,
name|factory
argument_list|)
decl_stmt|;
name|server
operator|.
name|setStopTimeout
argument_list|(
literal|500
argument_list|)
expr_stmt|;
name|connector
operator|.
name|setStopTimeout
argument_list|(
literal|500
argument_list|)
expr_stmt|;
return|return
name|connector
return|;
block|}
block|}
specifier|private
name|void
name|setTrustStore
parameter_list|(
name|SslContextFactory
name|factory
parameter_list|,
name|String
name|trustStore2
parameter_list|)
throws|throws
name|Exception
block|{
name|String
name|mname
init|=
name|Server
operator|.
name|getVersion
argument_list|()
operator|.
name|startsWith
argument_list|(
literal|"8"
argument_list|)
condition|?
literal|"setTrustStore"
else|:
literal|"setTrustStorePath"
decl_stmt|;
name|factory
operator|.
name|getClass
argument_list|()
operator|.
name|getMethod
argument_list|(
name|mname
argument_list|,
name|String
operator|.
name|class
argument_list|)
operator|.
name|invoke
argument_list|(
name|factory
argument_list|,
name|trustStore2
argument_list|)
expr_stmt|;
block|}
comment|// Properties
comment|// --------------------------------------------------------------------------------
specifier|public
name|String
name|getKeyStore
parameter_list|()
block|{
return|return
name|keyStore
return|;
block|}
specifier|public
name|void
name|setKeyStore
parameter_list|(
name|String
name|keyStore
parameter_list|)
block|{
name|this
operator|.
name|keyStore
operator|=
name|keyStore
expr_stmt|;
block|}
specifier|public
name|String
name|getKeyPassword
parameter_list|()
block|{
return|return
name|keyPassword
return|;
block|}
specifier|public
name|void
name|setKeyPassword
parameter_list|(
name|String
name|keyPassword
parameter_list|)
block|{
name|this
operator|.
name|keyPassword
operator|=
name|keyPassword
expr_stmt|;
block|}
specifier|public
name|String
name|getKeyStoreType
parameter_list|()
block|{
return|return
name|keyStoreType
return|;
block|}
specifier|public
name|void
name|setKeyStoreType
parameter_list|(
name|String
name|keyStoreType
parameter_list|)
block|{
name|this
operator|.
name|keyStoreType
operator|=
name|keyStoreType
expr_stmt|;
block|}
specifier|public
name|String
name|getKeyStorePassword
parameter_list|()
block|{
return|return
name|keyStorePassword
return|;
block|}
specifier|public
name|void
name|setKeyStorePassword
parameter_list|(
name|String
name|keyStorePassword
parameter_list|)
block|{
name|this
operator|.
name|keyStorePassword
operator|=
name|keyStorePassword
expr_stmt|;
block|}
specifier|public
name|String
name|getProtocol
parameter_list|()
block|{
return|return
name|protocol
return|;
block|}
specifier|public
name|void
name|setProtocol
parameter_list|(
name|String
name|protocol
parameter_list|)
block|{
name|this
operator|.
name|protocol
operator|=
name|protocol
expr_stmt|;
block|}
specifier|public
name|String
name|getSecureRandomCertficateAlgorithm
parameter_list|()
block|{
return|return
name|secureRandomCertficateAlgorithm
return|;
block|}
specifier|public
name|void
name|setSecureRandomCertficateAlgorithm
parameter_list|(
name|String
name|secureRandomCertficateAlgorithm
parameter_list|)
block|{
name|this
operator|.
name|secureRandomCertficateAlgorithm
operator|=
name|secureRandomCertficateAlgorithm
expr_stmt|;
block|}
specifier|public
name|String
name|getKeyCertificateAlgorithm
parameter_list|()
block|{
return|return
name|keyCertificateAlgorithm
return|;
block|}
specifier|public
name|void
name|setKeyCertificateAlgorithm
parameter_list|(
name|String
name|keyCertificateAlgorithm
parameter_list|)
block|{
name|this
operator|.
name|keyCertificateAlgorithm
operator|=
name|keyCertificateAlgorithm
expr_stmt|;
block|}
specifier|public
name|String
name|getTrustCertificateAlgorithm
parameter_list|()
block|{
return|return
name|trustCertificateAlgorithm
return|;
block|}
specifier|public
name|void
name|setTrustCertificateAlgorithm
parameter_list|(
name|String
name|trustCertificateAlgorithm
parameter_list|)
block|{
name|this
operator|.
name|trustCertificateAlgorithm
operator|=
name|trustCertificateAlgorithm
expr_stmt|;
block|}
comment|/**      * @return the auth      */
specifier|public
name|String
name|getAuth
parameter_list|()
block|{
return|return
name|auth
return|;
block|}
comment|/**      * @param auth the auth to set      */
specifier|public
name|void
name|setAuth
parameter_list|(
name|String
name|auth
parameter_list|)
block|{
name|this
operator|.
name|auth
operator|=
name|auth
expr_stmt|;
block|}
specifier|public
name|boolean
name|isWantClientAuth
parameter_list|()
block|{
return|return
name|wantClientAuth
return|;
block|}
specifier|public
name|void
name|setWantClientAuth
parameter_list|(
name|boolean
name|wantClientAuth
parameter_list|)
block|{
name|this
operator|.
name|wantClientAuth
operator|=
name|wantClientAuth
expr_stmt|;
block|}
specifier|public
name|boolean
name|isNeedClientAuth
parameter_list|()
block|{
return|return
name|needClientAuth
return|;
block|}
specifier|public
name|void
name|setNeedClientAuth
parameter_list|(
name|boolean
name|needClientAuth
parameter_list|)
block|{
name|this
operator|.
name|needClientAuth
operator|=
name|needClientAuth
expr_stmt|;
block|}
specifier|public
name|String
name|getTrustStore
parameter_list|()
block|{
return|return
name|trustStore
return|;
block|}
specifier|public
name|void
name|setTrustStore
parameter_list|(
name|String
name|trustStore
parameter_list|)
block|{
name|this
operator|.
name|trustStore
operator|=
name|trustStore
expr_stmt|;
block|}
specifier|public
name|String
name|getTrustStorePassword
parameter_list|()
block|{
return|return
name|trustStorePassword
return|;
block|}
specifier|public
name|void
name|setTrustStorePassword
parameter_list|(
name|String
name|trustStorePassword
parameter_list|)
block|{
name|this
operator|.
name|trustStorePassword
operator|=
name|trustStorePassword
expr_stmt|;
block|}
block|}
end_class

end_unit

