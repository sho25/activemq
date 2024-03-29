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
name|org
operator|.
name|apache
operator|.
name|activemq
operator|.
name|ActiveMQConnectionFactory
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
name|JmsTopicSendReceiveTest
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
name|spring
operator|.
name|SpringSslContext
import|;
end_import

begin_comment
comment|/**  * Here we are using a TLS cert which does not have "localhost" as the CN. However we configure the client not to enable  * hostname verification, and so the test passes  */
end_comment

begin_class
specifier|public
class|class
name|HttpsClientSettingsHostnameVerificationDisabledTest
extends|extends
name|JmsTopicSendReceiveTest
block|{
comment|/**      *       */
specifier|private
specifier|static
specifier|final
name|String
name|URI_LOCATION
init|=
literal|"https://localhost:8161"
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
name|TRUST_KEYSTORE
init|=
literal|"src/test/resources/server-somehost.keystore"
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|String
name|SERVER_KEYSTORE
init|=
literal|"src/test/resources/server-somehost.keystore"
decl_stmt|;
specifier|private
name|BrokerService
name|broker
decl_stmt|;
comment|/*      * (non-Javadoc)      *       * @see org.apache.activemq.JmsSendReceiveTestSupport#setUp()      */
annotation|@
name|Override
specifier|protected
name|void
name|setUp
parameter_list|()
throws|throws
name|Exception
block|{
comment|// Create the broker service from the configuration and wait until it
comment|// has been started...
name|broker
operator|=
operator|new
name|BrokerService
argument_list|()
expr_stmt|;
name|SpringSslContext
name|sslContext
init|=
operator|new
name|SpringSslContext
argument_list|()
decl_stmt|;
name|sslContext
operator|.
name|setKeyStorePassword
argument_list|(
name|PASSWORD
argument_list|)
expr_stmt|;
name|sslContext
operator|.
name|setKeyStore
argument_list|(
name|SERVER_KEYSTORE
argument_list|)
expr_stmt|;
name|sslContext
operator|.
name|setTrustStore
argument_list|(
name|TRUST_KEYSTORE
argument_list|)
expr_stmt|;
name|sslContext
operator|.
name|setTrustStorePassword
argument_list|(
name|PASSWORD
argument_list|)
expr_stmt|;
name|sslContext
operator|.
name|afterPropertiesSet
argument_list|()
expr_stmt|;
comment|// This is required so that the SSLContext instance is generated with the passed information.
name|broker
operator|.
name|setSslContext
argument_list|(
name|sslContext
argument_list|)
expr_stmt|;
name|broker
operator|.
name|addConnector
argument_list|(
name|URI_LOCATION
argument_list|)
expr_stmt|;
name|broker
operator|.
name|setPersistent
argument_list|(
literal|false
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
name|start
argument_list|()
expr_stmt|;
name|broker
operator|.
name|waitUntilStarted
argument_list|()
expr_stmt|;
name|super
operator|.
name|setUp
argument_list|()
expr_stmt|;
block|}
comment|/*      * (non-Javadoc)      *       * @see org.apache.activemq.AutoFailTestSupport#tearDown()      */
annotation|@
name|Override
specifier|protected
name|void
name|tearDown
parameter_list|()
throws|throws
name|Exception
block|{
name|super
operator|.
name|tearDown
argument_list|()
expr_stmt|;
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
comment|/*      * (non-Javadoc)      *       * @see org.apache.activemq.TestSupport#createConnectionFactory()      */
annotation|@
name|Override
specifier|protected
name|ActiveMQConnectionFactory
name|createConnectionFactory
parameter_list|()
throws|throws
name|Exception
block|{
name|ActiveMQSslConnectionFactory
name|factory
init|=
operator|new
name|ActiveMQSslConnectionFactory
argument_list|(
name|URI_LOCATION
operator|+
literal|"?transport.verifyHostName=false"
argument_list|)
decl_stmt|;
comment|// Configure TLS for the client
name|factory
operator|.
name|setTrustStore
argument_list|(
name|TRUST_KEYSTORE
argument_list|)
expr_stmt|;
name|factory
operator|.
name|setTrustStorePassword
argument_list|(
name|PASSWORD
argument_list|)
expr_stmt|;
return|return
name|factory
return|;
block|}
block|}
end_class

end_unit

