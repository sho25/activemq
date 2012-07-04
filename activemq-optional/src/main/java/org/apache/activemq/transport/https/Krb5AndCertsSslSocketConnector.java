begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements. See the NOTICE file distributed with this  * work for additional information regarding copyright ownership. The ASF  * licenses this file to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  * http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT  * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the  * License for the specific language governing permissions and limitations under  * the License.  */
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
name|ServerSocket
import|;
end_import

begin_import
import|import
name|java
operator|.
name|security
operator|.
name|KeyManagementException
import|;
end_import

begin_import
import|import
name|java
operator|.
name|security
operator|.
name|NoSuchAlgorithmException
import|;
end_import

begin_import
import|import
name|java
operator|.
name|security
operator|.
name|NoSuchProviderException
import|;
end_import

begin_import
import|import
name|java
operator|.
name|security
operator|.
name|Principal
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Collections
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|List
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Random
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
name|SSLServerSocket
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
name|org
operator|.
name|eclipse
operator|.
name|jetty
operator|.
name|http
operator|.
name|HttpSchemes
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
name|io
operator|.
name|EndPoint
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
name|Request
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
name|ServletSSL
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
comment|/**  * Extend Jetty's {@link SslSocketConnector} to optionally also provide  * Kerberos5ized SSL sockets. The only change in behavior from superclass is  * that we no longer honor requests to turn off NeedAuthentication when running  * with Kerberos support.  */
end_comment

begin_class
specifier|public
class|class
name|Krb5AndCertsSslSocketConnector
extends|extends
name|SslSocketConnector
block|{
specifier|public
specifier|static
specifier|final
name|List
argument_list|<
name|String
argument_list|>
name|KRB5_CIPHER_SUITES
init|=
name|Collections
operator|.
name|unmodifiableList
argument_list|(
name|Collections
operator|.
name|singletonList
argument_list|(
literal|"TLS_KRB5_WITH_3DES_EDE_CBC_SHA"
argument_list|)
argument_list|)
decl_stmt|;
static|static
block|{
name|System
operator|.
name|setProperty
argument_list|(
literal|"https.cipherSuites"
argument_list|,
name|KRB5_CIPHER_SUITES
operator|.
name|get
argument_list|(
literal|0
argument_list|)
argument_list|)
expr_stmt|;
block|}
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
name|Krb5AndCertsSslSocketConnector
operator|.
name|class
argument_list|)
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|String
name|REMOTE_PRINCIPAL
init|=
literal|"remote_principal"
decl_stmt|;
specifier|public
enum|enum
name|MODE
block|{
name|KRB
block|,
name|CERTS
block|,
name|BOTH
block|}
comment|// Support Kerberos, certificates or both?
specifier|private
name|boolean
name|useKrb
decl_stmt|;
specifier|private
name|boolean
name|useCerts
decl_stmt|;
specifier|public
name|Krb5AndCertsSslSocketConnector
parameter_list|()
block|{
comment|// By default, stick to cert based authentication
name|super
argument_list|()
expr_stmt|;
name|useKrb
operator|=
literal|false
expr_stmt|;
name|useCerts
operator|=
literal|true
expr_stmt|;
name|setPasswords
argument_list|()
expr_stmt|;
block|}
specifier|public
specifier|static
name|boolean
name|isKrb
parameter_list|(
name|String
name|mode
parameter_list|)
block|{
return|return
name|mode
operator|==
name|MODE
operator|.
name|KRB
operator|.
name|toString
argument_list|()
operator|||
name|mode
operator|==
name|MODE
operator|.
name|BOTH
operator|.
name|toString
argument_list|()
return|;
block|}
specifier|public
name|void
name|setMode
parameter_list|(
name|String
name|mode
parameter_list|)
block|{
name|useKrb
operator|=
name|mode
operator|==
name|MODE
operator|.
name|KRB
operator|.
name|toString
argument_list|()
operator|||
name|mode
operator|==
name|MODE
operator|.
name|BOTH
operator|.
name|toString
argument_list|()
expr_stmt|;
name|useCerts
operator|=
name|mode
operator|==
name|MODE
operator|.
name|CERTS
operator|.
name|toString
argument_list|()
operator|||
name|mode
operator|==
name|MODE
operator|.
name|BOTH
operator|.
name|toString
argument_list|()
expr_stmt|;
name|logIfDebug
argument_list|(
literal|"useKerb = "
operator|+
name|useKrb
operator|+
literal|", useCerts = "
operator|+
name|useCerts
argument_list|)
expr_stmt|;
block|}
comment|// If not using Certs, set passwords to random gibberish or else
comment|// Jetty will actually prompt the user for some.
specifier|private
name|void
name|setPasswords
parameter_list|()
block|{
if|if
condition|(
operator|!
name|useCerts
condition|)
block|{
name|Random
name|r
init|=
operator|new
name|Random
argument_list|()
decl_stmt|;
name|System
operator|.
name|setProperty
argument_list|(
literal|"jetty.ssl.password"
argument_list|,
name|String
operator|.
name|valueOf
argument_list|(
name|r
operator|.
name|nextLong
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|System
operator|.
name|setProperty
argument_list|(
literal|"jetty.ssl.keypassword"
argument_list|,
name|String
operator|.
name|valueOf
argument_list|(
name|r
operator|.
name|nextLong
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Override
specifier|public
name|SslContextFactory
name|getSslContextFactory
parameter_list|()
block|{
specifier|final
name|SslContextFactory
name|factory
init|=
name|super
operator|.
name|getSslContextFactory
argument_list|()
decl_stmt|;
if|if
condition|(
name|useCerts
condition|)
block|{
return|return
name|factory
return|;
block|}
try|try
block|{
name|SSLContext
name|context
init|=
name|factory
operator|.
name|getProvider
argument_list|()
operator|==
literal|null
condition|?
name|SSLContext
operator|.
name|getInstance
argument_list|(
name|factory
operator|.
name|getProtocol
argument_list|()
argument_list|)
else|:
name|SSLContext
operator|.
name|getInstance
argument_list|(
name|factory
operator|.
name|getProtocol
argument_list|()
argument_list|,
name|factory
operator|.
name|getProvider
argument_list|()
argument_list|)
decl_stmt|;
name|context
operator|.
name|init
argument_list|(
literal|null
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|factory
operator|.
name|setSslContext
argument_list|(
name|context
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|NoSuchAlgorithmException
name|e
parameter_list|)
block|{         }
catch|catch
parameter_list|(
name|NoSuchProviderException
name|e
parameter_list|)
block|{         }
catch|catch
parameter_list|(
name|KeyManagementException
name|e
parameter_list|)
block|{         }
return|return
name|factory
return|;
block|}
comment|/*      * (non-Javadoc)      *      * @see      * org.mortbay.jetty.security.SslSocketConnector#newServerSocket(java.lang      * .String, int, int)      */
annotation|@
name|Override
specifier|protected
name|ServerSocket
name|newServerSocket
parameter_list|(
name|String
name|host
parameter_list|,
name|int
name|port
parameter_list|,
name|int
name|backlog
parameter_list|)
throws|throws
name|IOException
block|{
name|logIfDebug
argument_list|(
literal|"Creating new KrbServerSocket for: "
operator|+
name|host
argument_list|)
expr_stmt|;
name|SSLServerSocket
name|ss
init|=
literal|null
decl_stmt|;
if|if
condition|(
name|useCerts
condition|)
comment|// Get the server socket from the SSL super impl
name|ss
operator|=
operator|(
name|SSLServerSocket
operator|)
name|super
operator|.
name|newServerSocket
argument_list|(
name|host
argument_list|,
name|port
argument_list|,
name|backlog
argument_list|)
expr_stmt|;
else|else
block|{
comment|// Create a default server socket
try|try
block|{
name|ss
operator|=
operator|(
name|SSLServerSocket
operator|)
name|super
operator|.
name|newServerSocket
argument_list|(
name|host
argument_list|,
name|port
argument_list|,
name|backlog
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
name|LOG
operator|.
name|warn
argument_list|(
literal|"Could not create KRB5 Listener"
argument_list|,
name|e
argument_list|)
expr_stmt|;
throw|throw
operator|new
name|IOException
argument_list|(
literal|"Could not create KRB5 Listener: "
operator|+
name|e
operator|.
name|toString
argument_list|()
argument_list|)
throw|;
block|}
block|}
comment|// Add Kerberos ciphers to this socket server if needed.
if|if
condition|(
name|useKrb
condition|)
block|{
name|ss
operator|.
name|setNeedClientAuth
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|String
index|[]
name|combined
decl_stmt|;
if|if
condition|(
name|useCerts
condition|)
block|{
comment|// combine the cipher suites
name|String
index|[]
name|certs
init|=
name|ss
operator|.
name|getEnabledCipherSuites
argument_list|()
decl_stmt|;
name|combined
operator|=
operator|new
name|String
index|[
name|certs
operator|.
name|length
operator|+
name|KRB5_CIPHER_SUITES
operator|.
name|size
argument_list|()
index|]
expr_stmt|;
name|System
operator|.
name|arraycopy
argument_list|(
name|certs
argument_list|,
literal|0
argument_list|,
name|combined
argument_list|,
literal|0
argument_list|,
name|certs
operator|.
name|length
argument_list|)
expr_stmt|;
name|System
operator|.
name|arraycopy
argument_list|(
name|KRB5_CIPHER_SUITES
operator|.
name|toArray
argument_list|(
operator|new
name|String
index|[
literal|0
index|]
argument_list|)
argument_list|,
literal|0
argument_list|,
name|combined
argument_list|,
name|certs
operator|.
name|length
argument_list|,
name|KRB5_CIPHER_SUITES
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
block|}
else|else
block|{
comment|// Just enable Kerberos auth
name|combined
operator|=
name|KRB5_CIPHER_SUITES
operator|.
name|toArray
argument_list|(
operator|new
name|String
index|[
literal|0
index|]
argument_list|)
expr_stmt|;
block|}
name|ss
operator|.
name|setEnabledCipherSuites
argument_list|(
name|combined
argument_list|)
expr_stmt|;
block|}
return|return
name|ss
return|;
block|}
empty_stmt|;
annotation|@
name|Override
specifier|public
name|void
name|customize
parameter_list|(
name|EndPoint
name|endpoint
parameter_list|,
name|Request
name|request
parameter_list|)
throws|throws
name|IOException
block|{
if|if
condition|(
name|useKrb
condition|)
block|{
comment|// Add Kerberos-specific info
name|SSLSocket
name|sslSocket
init|=
operator|(
name|SSLSocket
operator|)
name|endpoint
operator|.
name|getTransport
argument_list|()
decl_stmt|;
name|Principal
name|remotePrincipal
init|=
name|sslSocket
operator|.
name|getSession
argument_list|()
operator|.
name|getPeerPrincipal
argument_list|()
decl_stmt|;
name|logIfDebug
argument_list|(
literal|"Remote principal = "
operator|+
name|remotePrincipal
argument_list|)
expr_stmt|;
name|request
operator|.
name|setScheme
argument_list|(
name|HttpSchemes
operator|.
name|HTTPS
argument_list|)
expr_stmt|;
name|request
operator|.
name|setAttribute
argument_list|(
name|REMOTE_PRINCIPAL
argument_list|,
name|remotePrincipal
argument_list|)
expr_stmt|;
if|if
condition|(
operator|!
name|useCerts
condition|)
block|{
comment|// Add extra info that would have been added by
comment|// super
name|String
name|cipherSuite
init|=
name|sslSocket
operator|.
name|getSession
argument_list|()
operator|.
name|getCipherSuite
argument_list|()
decl_stmt|;
name|Integer
name|keySize
init|=
name|Integer
operator|.
name|valueOf
argument_list|(
name|ServletSSL
operator|.
name|deduceKeyLength
argument_list|(
name|cipherSuite
argument_list|)
argument_list|)
decl_stmt|;
empty_stmt|;
name|request
operator|.
name|setAttribute
argument_list|(
literal|"javax.servlet.request.cipher_suite"
argument_list|,
name|cipherSuite
argument_list|)
expr_stmt|;
name|request
operator|.
name|setAttribute
argument_list|(
literal|"javax.servlet.request.key_size"
argument_list|,
name|keySize
argument_list|)
expr_stmt|;
block|}
block|}
if|if
condition|(
name|useCerts
condition|)
name|super
operator|.
name|customize
argument_list|(
name|endpoint
argument_list|,
name|request
argument_list|)
expr_stmt|;
block|}
specifier|private
name|void
name|logIfDebug
parameter_list|(
name|String
name|s
parameter_list|)
block|{
if|if
condition|(
name|LOG
operator|.
name|isDebugEnabled
argument_list|()
condition|)
name|LOG
operator|.
name|debug
argument_list|(
name|s
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

