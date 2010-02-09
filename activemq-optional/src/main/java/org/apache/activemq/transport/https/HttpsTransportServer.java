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
name|https
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
name|org
operator|.
name|apache
operator|.
name|activemq
operator|.
name|transport
operator|.
name|http
operator|.
name|HttpTransportServer
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
name|ssl
operator|.
name|SslSocketConnector
import|;
end_import

begin_class
specifier|public
class|class
name|HttpsTransportServer
extends|extends
name|HttpTransportServer
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
specifier|public
name|HttpsTransportServer
parameter_list|(
name|URI
name|uri
parameter_list|)
block|{
name|super
argument_list|(
name|uri
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|doStart
parameter_list|()
throws|throws
name|Exception
block|{
name|SslSocketConnector
name|sslConnector
init|=
operator|new
name|SslSocketConnector
argument_list|()
decl_stmt|;
name|sslConnector
operator|.
name|setKeystore
argument_list|(
name|keyStore
argument_list|)
expr_stmt|;
name|sslConnector
operator|.
name|setPassword
argument_list|(
name|keyStorePassword
argument_list|)
expr_stmt|;
comment|// if the keyPassword hasn't been set, default it to the
comment|// key store password
if|if
condition|(
name|keyPassword
operator|==
literal|null
condition|)
block|{
name|sslConnector
operator|.
name|setKeyPassword
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
name|sslConnector
operator|.
name|setKeystoreType
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
name|sslConnector
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
name|sslConnector
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
name|sslConnector
operator|.
name|setSslTrustManagerFactoryAlgorithm
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
name|sslConnector
operator|.
name|setProtocol
argument_list|(
name|protocol
argument_list|)
expr_stmt|;
block|}
name|setConnector
argument_list|(
name|sslConnector
argument_list|)
expr_stmt|;
name|super
operator|.
name|doStart
argument_list|()
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
block|}
end_class

end_unit

