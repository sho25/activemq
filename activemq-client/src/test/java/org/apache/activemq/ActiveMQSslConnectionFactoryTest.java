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
name|IOException
import|;
end_import

begin_import
import|import
name|java
operator|.
name|net
operator|.
name|ConnectException
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

begin_class
specifier|public
class|class
name|ActiveMQSslConnectionFactoryTest
block|{
specifier|final
name|String
name|TRUST_STORE_FILE_NAME
init|=
literal|"client.keystore"
decl_stmt|;
specifier|final
name|String
name|TRUST_STORE_PKCS12_FILE_NAME
init|=
literal|"client-pkcs12.keystore"
decl_stmt|;
specifier|final
name|String
name|TRUST_STORE_DIRECTORY_NAME
init|=
literal|"src/test/resources/ssl/"
decl_stmt|;
specifier|final
name|String
name|TRUST_STORE_RESOURCE_PREFIX
init|=
literal|"ssl/"
decl_stmt|;
specifier|final
name|String
name|TRUST_STORE_PASSWORD
init|=
literal|"password"
decl_stmt|;
specifier|final
name|String
name|SSL_TRANSPORT
init|=
literal|"ssl://localhost:0"
decl_stmt|;
specifier|final
name|String
name|FAILOVER_SSL_TRANSPORT
init|=
literal|"failover:("
operator|+
name|SSL_TRANSPORT
operator|+
literal|")?maxReconnectAttempts=1"
decl_stmt|;
annotation|@
name|Test
argument_list|(
name|expected
operator|=
name|ConnectException
operator|.
name|class
argument_list|)
specifier|public
name|void
name|validTrustStoreFileTest
parameter_list|()
throws|throws
name|Throwable
block|{
name|executeTest
argument_list|(
name|SSL_TRANSPORT
argument_list|,
name|TRUST_STORE_DIRECTORY_NAME
operator|+
name|TRUST_STORE_FILE_NAME
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
argument_list|(
name|expected
operator|=
name|ConnectException
operator|.
name|class
argument_list|)
specifier|public
name|void
name|validTrustStoreURLTest
parameter_list|()
throws|throws
name|Throwable
block|{
name|executeTest
argument_list|(
name|SSL_TRANSPORT
argument_list|,
operator|new
name|File
argument_list|(
name|TRUST_STORE_DIRECTORY_NAME
operator|+
name|TRUST_STORE_FILE_NAME
argument_list|)
operator|.
name|toURI
argument_list|()
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
argument_list|(
name|expected
operator|=
name|ConnectException
operator|.
name|class
argument_list|)
specifier|public
name|void
name|validTrustStoreResourceTest
parameter_list|()
throws|throws
name|Throwable
block|{
name|executeTest
argument_list|(
name|SSL_TRANSPORT
argument_list|,
name|TRUST_STORE_RESOURCE_PREFIX
operator|+
name|TRUST_STORE_FILE_NAME
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
argument_list|(
name|expected
operator|=
name|IOException
operator|.
name|class
argument_list|)
specifier|public
name|void
name|invalidTrustStoreFileTest
parameter_list|()
throws|throws
name|Throwable
block|{
name|executeTest
argument_list|(
name|SSL_TRANSPORT
argument_list|,
name|TRUST_STORE_DIRECTORY_NAME
operator|+
name|TRUST_STORE_FILE_NAME
operator|+
literal|".dummy"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
argument_list|(
name|expected
operator|=
name|IOException
operator|.
name|class
argument_list|)
specifier|public
name|void
name|invalidTrustStoreURLTest
parameter_list|()
throws|throws
name|Throwable
block|{
name|executeTest
argument_list|(
name|SSL_TRANSPORT
argument_list|,
operator|new
name|File
argument_list|(
name|TRUST_STORE_DIRECTORY_NAME
operator|+
name|TRUST_STORE_FILE_NAME
operator|+
literal|".dummy"
argument_list|)
operator|.
name|toURI
argument_list|()
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
argument_list|(
name|expected
operator|=
name|IOException
operator|.
name|class
argument_list|)
specifier|public
name|void
name|invalidTrustStoreResourceTest
parameter_list|()
throws|throws
name|Throwable
block|{
name|executeTest
argument_list|(
name|SSL_TRANSPORT
argument_list|,
name|TRUST_STORE_RESOURCE_PREFIX
operator|+
name|TRUST_STORE_FILE_NAME
operator|+
literal|".dummy"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
argument_list|(
name|expected
operator|=
name|IOException
operator|.
name|class
argument_list|)
specifier|public
name|void
name|validTrustStoreFileFailoverTest
parameter_list|()
throws|throws
name|Throwable
block|{
name|executeTest
argument_list|(
name|FAILOVER_SSL_TRANSPORT
argument_list|,
name|TRUST_STORE_DIRECTORY_NAME
operator|+
name|TRUST_STORE_FILE_NAME
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
argument_list|(
name|expected
operator|=
name|IOException
operator|.
name|class
argument_list|)
specifier|public
name|void
name|validTrustStoreURLFailoverTest
parameter_list|()
throws|throws
name|Throwable
block|{
name|executeTest
argument_list|(
name|FAILOVER_SSL_TRANSPORT
argument_list|,
operator|new
name|File
argument_list|(
name|TRUST_STORE_DIRECTORY_NAME
operator|+
name|TRUST_STORE_FILE_NAME
argument_list|)
operator|.
name|toURI
argument_list|()
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
argument_list|(
name|expected
operator|=
name|IOException
operator|.
name|class
argument_list|)
specifier|public
name|void
name|validTrustStoreResourceFailoverTest
parameter_list|()
throws|throws
name|Throwable
block|{
name|executeTest
argument_list|(
name|FAILOVER_SSL_TRANSPORT
argument_list|,
name|TRUST_STORE_RESOURCE_PREFIX
operator|+
name|TRUST_STORE_FILE_NAME
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
argument_list|(
name|expected
operator|=
name|IOException
operator|.
name|class
argument_list|)
specifier|public
name|void
name|invalidTrustStoreFileFailoverTest
parameter_list|()
throws|throws
name|Throwable
block|{
name|executeTest
argument_list|(
name|FAILOVER_SSL_TRANSPORT
argument_list|,
name|TRUST_STORE_DIRECTORY_NAME
operator|+
name|TRUST_STORE_FILE_NAME
operator|+
literal|".dummy"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
argument_list|(
name|expected
operator|=
name|IOException
operator|.
name|class
argument_list|)
specifier|public
name|void
name|invalidTrustStoreURLFailoverTest
parameter_list|()
throws|throws
name|Throwable
block|{
name|executeTest
argument_list|(
name|FAILOVER_SSL_TRANSPORT
argument_list|,
operator|new
name|File
argument_list|(
name|TRUST_STORE_DIRECTORY_NAME
operator|+
name|TRUST_STORE_FILE_NAME
operator|+
literal|".dummy"
argument_list|)
operator|.
name|toURI
argument_list|()
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
argument_list|(
name|expected
operator|=
name|IOException
operator|.
name|class
argument_list|)
specifier|public
name|void
name|invalidTrustStoreResourceFailoverTest
parameter_list|()
throws|throws
name|Throwable
block|{
name|executeTest
argument_list|(
name|FAILOVER_SSL_TRANSPORT
argument_list|,
name|TRUST_STORE_RESOURCE_PREFIX
operator|+
name|TRUST_STORE_FILE_NAME
operator|+
literal|".dummy"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
argument_list|(
name|expected
operator|=
name|ConnectException
operator|.
name|class
argument_list|)
specifier|public
name|void
name|validPkcs12TrustStoreFileTest
parameter_list|()
throws|throws
name|Throwable
block|{
name|executeTest
argument_list|(
name|SSL_TRANSPORT
argument_list|,
name|TRUST_STORE_DIRECTORY_NAME
operator|+
name|TRUST_STORE_PKCS12_FILE_NAME
argument_list|,
literal|"pkcs12"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
argument_list|(
name|expected
operator|=
name|ConnectException
operator|.
name|class
argument_list|)
specifier|public
name|void
name|validPkcs12TrustStoreURLTest
parameter_list|()
throws|throws
name|Throwable
block|{
name|executeTest
argument_list|(
name|SSL_TRANSPORT
argument_list|,
operator|new
name|File
argument_list|(
name|TRUST_STORE_DIRECTORY_NAME
operator|+
name|TRUST_STORE_PKCS12_FILE_NAME
argument_list|)
operator|.
name|toURI
argument_list|()
operator|.
name|toString
argument_list|()
argument_list|,
literal|"pkcs12"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
argument_list|(
name|expected
operator|=
name|ConnectException
operator|.
name|class
argument_list|)
specifier|public
name|void
name|validPkcs12TrustStoreResourceTest
parameter_list|()
throws|throws
name|Throwable
block|{
name|executeTest
argument_list|(
name|SSL_TRANSPORT
argument_list|,
name|TRUST_STORE_RESOURCE_PREFIX
operator|+
name|TRUST_STORE_PKCS12_FILE_NAME
argument_list|,
literal|"pkcs12"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
argument_list|(
name|expected
operator|=
name|IOException
operator|.
name|class
argument_list|)
comment|// Invalid keystore format
specifier|public
name|void
name|invalidTrustStoreTypeTest
parameter_list|()
throws|throws
name|Throwable
block|{
name|executeTest
argument_list|(
name|SSL_TRANSPORT
argument_list|,
name|TRUST_STORE_RESOURCE_PREFIX
operator|+
name|TRUST_STORE_PKCS12_FILE_NAME
argument_list|,
literal|"jks"
argument_list|)
expr_stmt|;
block|}
specifier|protected
name|void
name|executeTest
parameter_list|(
name|String
name|transport
parameter_list|,
name|String
name|name
parameter_list|)
throws|throws
name|Throwable
block|{
name|executeTest
argument_list|(
name|transport
argument_list|,
name|name
argument_list|,
literal|null
argument_list|)
expr_stmt|;
block|}
specifier|protected
name|ActiveMQSslConnectionFactory
name|getFactory
parameter_list|(
name|String
name|transport
parameter_list|)
block|{
return|return
operator|new
name|ActiveMQSslConnectionFactory
argument_list|(
name|transport
argument_list|)
return|;
block|}
specifier|protected
name|void
name|executeTest
parameter_list|(
name|String
name|transport
parameter_list|,
name|String
name|name
parameter_list|,
name|String
name|type
parameter_list|)
throws|throws
name|Throwable
block|{
try|try
block|{
name|ActiveMQSslConnectionFactory
name|activeMQSslConnectionFactory
init|=
name|getFactory
argument_list|(
name|transport
argument_list|)
decl_stmt|;
name|activeMQSslConnectionFactory
operator|.
name|setTrustStoreType
argument_list|(
name|type
operator|!=
literal|null
condition|?
name|type
else|:
name|activeMQSslConnectionFactory
operator|.
name|getTrustStoreType
argument_list|()
argument_list|)
expr_stmt|;
name|activeMQSslConnectionFactory
operator|.
name|setTrustStore
argument_list|(
name|name
argument_list|)
expr_stmt|;
name|activeMQSslConnectionFactory
operator|.
name|setTrustStorePassword
argument_list|(
name|TRUST_STORE_PASSWORD
argument_list|)
expr_stmt|;
name|javax
operator|.
name|jms
operator|.
name|Connection
name|connection
init|=
name|activeMQSslConnectionFactory
operator|.
name|createConnection
argument_list|()
decl_stmt|;
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
catch|catch
parameter_list|(
name|javax
operator|.
name|jms
operator|.
name|JMSException
name|e
parameter_list|)
block|{
name|e
operator|.
name|getCause
argument_list|()
operator|.
name|printStackTrace
argument_list|()
expr_stmt|;
throw|throw
name|e
operator|.
name|getCause
argument_list|()
throw|;
block|}
block|}
block|}
end_class

end_unit

