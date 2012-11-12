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
name|stomp
operator|.
name|util
package|;
end_package

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
name|InputStream
import|;
end_import

begin_import
import|import
name|java
operator|.
name|net
operator|.
name|MalformedURLException
import|;
end_import

begin_import
import|import
name|java
operator|.
name|security
operator|.
name|KeyStore
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
name|SecureRandom
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|ArrayList
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
name|javax
operator|.
name|annotation
operator|.
name|PostConstruct
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
name|KeyManagerFactory
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
name|TrustManagerFactory
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
name|springframework
operator|.
name|core
operator|.
name|io
operator|.
name|ClassPathResource
import|;
end_import

begin_import
import|import
name|org
operator|.
name|springframework
operator|.
name|core
operator|.
name|io
operator|.
name|FileSystemResource
import|;
end_import

begin_import
import|import
name|org
operator|.
name|springframework
operator|.
name|core
operator|.
name|io
operator|.
name|Resource
import|;
end_import

begin_import
import|import
name|org
operator|.
name|springframework
operator|.
name|core
operator|.
name|io
operator|.
name|UrlResource
import|;
end_import

begin_import
import|import
name|org
operator|.
name|springframework
operator|.
name|util
operator|.
name|ResourceUtils
import|;
end_import

begin_comment
comment|/**  * Extends the SslContext so that it's easier to configure from spring.  */
end_comment

begin_class
specifier|public
class|class
name|ResourceLoadingSslContext
extends|extends
name|SslContext
block|{
specifier|private
name|String
name|keyStoreType
init|=
literal|"jks"
decl_stmt|;
specifier|private
name|String
name|trustStoreType
init|=
literal|"jks"
decl_stmt|;
specifier|private
name|String
name|secureRandomAlgorithm
init|=
literal|"SHA1PRNG"
decl_stmt|;
specifier|private
name|String
name|keyStoreAlgorithm
init|=
name|KeyManagerFactory
operator|.
name|getDefaultAlgorithm
argument_list|()
decl_stmt|;
specifier|private
name|String
name|trustStoreAlgorithm
init|=
name|TrustManagerFactory
operator|.
name|getDefaultAlgorithm
argument_list|()
decl_stmt|;
specifier|private
name|String
name|keyStore
decl_stmt|;
specifier|private
name|String
name|trustStore
decl_stmt|;
specifier|private
name|String
name|keyStoreKeyPassword
decl_stmt|;
specifier|private
name|String
name|keyStorePassword
decl_stmt|;
specifier|private
name|String
name|trustStorePassword
decl_stmt|;
comment|/**      *      * @throws Exception      * @org.apache.xbean.InitMethod      */
annotation|@
name|PostConstruct
specifier|public
name|void
name|afterPropertiesSet
parameter_list|()
throws|throws
name|Exception
block|{
name|keyManagers
operator|.
name|addAll
argument_list|(
name|createKeyManagers
argument_list|()
argument_list|)
expr_stmt|;
name|trustManagers
operator|.
name|addAll
argument_list|(
name|createTrustManagers
argument_list|()
argument_list|)
expr_stmt|;
if|if
condition|(
name|secureRandom
operator|==
literal|null
condition|)
block|{
name|secureRandom
operator|=
name|createSecureRandom
argument_list|()
expr_stmt|;
block|}
block|}
specifier|private
name|SecureRandom
name|createSecureRandom
parameter_list|()
throws|throws
name|NoSuchAlgorithmException
block|{
return|return
name|SecureRandom
operator|.
name|getInstance
argument_list|(
name|secureRandomAlgorithm
argument_list|)
return|;
block|}
specifier|private
name|Collection
argument_list|<
name|TrustManager
argument_list|>
name|createTrustManagers
parameter_list|()
throws|throws
name|Exception
block|{
name|KeyStore
name|ks
init|=
name|createTrustManagerKeyStore
argument_list|()
decl_stmt|;
if|if
condition|(
name|ks
operator|==
literal|null
condition|)
block|{
return|return
operator|new
name|ArrayList
argument_list|<
name|TrustManager
argument_list|>
argument_list|(
literal|0
argument_list|)
return|;
block|}
name|TrustManagerFactory
name|tmf
init|=
name|TrustManagerFactory
operator|.
name|getInstance
argument_list|(
name|trustStoreAlgorithm
argument_list|)
decl_stmt|;
name|tmf
operator|.
name|init
argument_list|(
name|ks
argument_list|)
expr_stmt|;
return|return
name|Arrays
operator|.
name|asList
argument_list|(
name|tmf
operator|.
name|getTrustManagers
argument_list|()
argument_list|)
return|;
block|}
specifier|private
name|Collection
argument_list|<
name|KeyManager
argument_list|>
name|createKeyManagers
parameter_list|()
throws|throws
name|Exception
block|{
name|KeyStore
name|ks
init|=
name|createKeyManagerKeyStore
argument_list|()
decl_stmt|;
if|if
condition|(
name|ks
operator|==
literal|null
condition|)
block|{
return|return
operator|new
name|ArrayList
argument_list|<
name|KeyManager
argument_list|>
argument_list|(
literal|0
argument_list|)
return|;
block|}
name|KeyManagerFactory
name|tmf
init|=
name|KeyManagerFactory
operator|.
name|getInstance
argument_list|(
name|keyStoreAlgorithm
argument_list|)
decl_stmt|;
name|tmf
operator|.
name|init
argument_list|(
name|ks
argument_list|,
name|keyStoreKeyPassword
operator|==
literal|null
condition|?
operator|(
name|keyStorePassword
operator|==
literal|null
condition|?
literal|null
else|:
name|keyStorePassword
operator|.
name|toCharArray
argument_list|()
operator|)
else|:
name|keyStoreKeyPassword
operator|.
name|toCharArray
argument_list|()
argument_list|)
expr_stmt|;
return|return
name|Arrays
operator|.
name|asList
argument_list|(
name|tmf
operator|.
name|getKeyManagers
argument_list|()
argument_list|)
return|;
block|}
specifier|private
name|KeyStore
name|createTrustManagerKeyStore
parameter_list|()
throws|throws
name|Exception
block|{
if|if
condition|(
name|trustStore
operator|==
literal|null
condition|)
block|{
return|return
literal|null
return|;
block|}
name|KeyStore
name|ks
init|=
name|KeyStore
operator|.
name|getInstance
argument_list|(
name|trustStoreType
argument_list|)
decl_stmt|;
name|InputStream
name|is
init|=
name|resourceFromString
argument_list|(
name|trustStore
argument_list|)
operator|.
name|getInputStream
argument_list|()
decl_stmt|;
try|try
block|{
name|ks
operator|.
name|load
argument_list|(
name|is
argument_list|,
name|trustStorePassword
operator|==
literal|null
condition|?
literal|null
else|:
name|trustStorePassword
operator|.
name|toCharArray
argument_list|()
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|is
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
return|return
name|ks
return|;
block|}
specifier|private
name|KeyStore
name|createKeyManagerKeyStore
parameter_list|()
throws|throws
name|Exception
block|{
if|if
condition|(
name|keyStore
operator|==
literal|null
condition|)
block|{
return|return
literal|null
return|;
block|}
name|KeyStore
name|ks
init|=
name|KeyStore
operator|.
name|getInstance
argument_list|(
name|keyStoreType
argument_list|)
decl_stmt|;
name|InputStream
name|is
init|=
name|resourceFromString
argument_list|(
name|keyStore
argument_list|)
operator|.
name|getInputStream
argument_list|()
decl_stmt|;
try|try
block|{
name|ks
operator|.
name|load
argument_list|(
name|is
argument_list|,
name|keyStorePassword
operator|==
literal|null
condition|?
literal|null
else|:
name|keyStorePassword
operator|.
name|toCharArray
argument_list|()
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|is
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
return|return
name|ks
return|;
block|}
specifier|public
name|String
name|getTrustStoreType
parameter_list|()
block|{
return|return
name|trustStoreType
return|;
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
throws|throws
name|MalformedURLException
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
throws|throws
name|MalformedURLException
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
name|getKeyStoreAlgorithm
parameter_list|()
block|{
return|return
name|keyStoreAlgorithm
return|;
block|}
specifier|public
name|void
name|setKeyStoreAlgorithm
parameter_list|(
name|String
name|keyAlgorithm
parameter_list|)
block|{
name|this
operator|.
name|keyStoreAlgorithm
operator|=
name|keyAlgorithm
expr_stmt|;
block|}
specifier|public
name|String
name|getTrustStoreAlgorithm
parameter_list|()
block|{
return|return
name|trustStoreAlgorithm
return|;
block|}
specifier|public
name|void
name|setTrustStoreAlgorithm
parameter_list|(
name|String
name|trustAlgorithm
parameter_list|)
block|{
name|this
operator|.
name|trustStoreAlgorithm
operator|=
name|trustAlgorithm
expr_stmt|;
block|}
specifier|public
name|String
name|getKeyStoreKeyPassword
parameter_list|()
block|{
return|return
name|keyStoreKeyPassword
return|;
block|}
specifier|public
name|void
name|setKeyStoreKeyPassword
parameter_list|(
name|String
name|keyPassword
parameter_list|)
block|{
name|this
operator|.
name|keyStoreKeyPassword
operator|=
name|keyPassword
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
name|keyPassword
parameter_list|)
block|{
name|this
operator|.
name|keyStorePassword
operator|=
name|keyPassword
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
name|trustPassword
parameter_list|)
block|{
name|this
operator|.
name|trustStorePassword
operator|=
name|trustPassword
expr_stmt|;
block|}
specifier|public
name|void
name|setKeyStoreType
parameter_list|(
name|String
name|keyType
parameter_list|)
block|{
name|this
operator|.
name|keyStoreType
operator|=
name|keyType
expr_stmt|;
block|}
specifier|public
name|void
name|setTrustStoreType
parameter_list|(
name|String
name|trustType
parameter_list|)
block|{
name|this
operator|.
name|trustStoreType
operator|=
name|trustType
expr_stmt|;
block|}
specifier|public
name|String
name|getSecureRandomAlgorithm
parameter_list|()
block|{
return|return
name|secureRandomAlgorithm
return|;
block|}
specifier|public
name|void
name|setSecureRandomAlgorithm
parameter_list|(
name|String
name|secureRandomAlgorithm
parameter_list|)
block|{
name|this
operator|.
name|secureRandomAlgorithm
operator|=
name|secureRandomAlgorithm
expr_stmt|;
block|}
specifier|public
specifier|static
name|Resource
name|resourceFromString
parameter_list|(
name|String
name|uri
parameter_list|)
throws|throws
name|MalformedURLException
block|{
name|Resource
name|resource
decl_stmt|;
name|File
name|file
init|=
operator|new
name|File
argument_list|(
name|uri
argument_list|)
decl_stmt|;
if|if
condition|(
name|file
operator|.
name|exists
argument_list|()
condition|)
block|{
name|resource
operator|=
operator|new
name|FileSystemResource
argument_list|(
name|uri
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|ResourceUtils
operator|.
name|isUrl
argument_list|(
name|uri
argument_list|)
condition|)
block|{
name|resource
operator|=
operator|new
name|UrlResource
argument_list|(
name|uri
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|resource
operator|=
operator|new
name|ClassPathResource
argument_list|(
name|uri
argument_list|)
expr_stmt|;
block|}
return|return
name|resource
return|;
block|}
block|}
end_class

end_unit

