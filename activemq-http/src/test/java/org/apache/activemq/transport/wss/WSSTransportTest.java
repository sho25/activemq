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
name|wss
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
name|transport
operator|.
name|SecureSocketConnectorFactory
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
name|ws
operator|.
name|WSTransportTest
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
name|WSSTransportTest
extends|extends
name|WSTransportTest
block|{
annotation|@
name|Override
specifier|protected
name|Connector
name|createJettyConnector
parameter_list|(
name|Server
name|server
parameter_list|)
throws|throws
name|Exception
block|{
name|SecureSocketConnectorFactory
name|sscf
init|=
operator|new
name|SecureSocketConnectorFactory
argument_list|()
decl_stmt|;
name|sscf
operator|.
name|setKeyStore
argument_list|(
literal|"src/test/resources/server.keystore"
argument_list|)
expr_stmt|;
name|sscf
operator|.
name|setKeyStorePassword
argument_list|(
literal|"password"
argument_list|)
expr_stmt|;
name|sscf
operator|.
name|setTrustStore
argument_list|(
literal|"src/test/resources/client.keystore"
argument_list|)
expr_stmt|;
name|sscf
operator|.
name|setTrustStorePassword
argument_list|(
literal|"password"
argument_list|)
expr_stmt|;
name|ServerConnector
name|c
init|=
operator|(
name|ServerConnector
operator|)
name|sscf
operator|.
name|createConnector
argument_list|(
name|server
argument_list|)
decl_stmt|;
name|c
operator|.
name|setPort
argument_list|(
name|getProxyPort
argument_list|()
argument_list|)
expr_stmt|;
return|return
name|c
return|;
block|}
annotation|@
name|Override
specifier|protected
name|String
name|getWSConnectorURI
parameter_list|()
block|{
return|return
literal|"wss://localhost:"
operator|+
name|port
return|;
block|}
annotation|@
name|Override
annotation|@
name|Test
argument_list|(
name|timeout
operator|=
literal|10000
argument_list|)
specifier|public
name|void
name|testGet
parameter_list|()
throws|throws
name|Exception
block|{
name|SslContextFactory
name|factory
init|=
operator|new
name|SslContextFactory
operator|.
name|Client
argument_list|()
decl_stmt|;
name|factory
operator|.
name|setEndpointIdentificationAlgorithm
argument_list|(
literal|null
argument_list|)
expr_stmt|;
comment|// service cert does not contain a SAN
name|factory
operator|.
name|setSslContext
argument_list|(
name|broker
operator|.
name|getSslContext
argument_list|()
operator|.
name|getSSLContext
argument_list|()
argument_list|)
expr_stmt|;
name|testGet
argument_list|(
literal|"https://127.0.0.1:"
operator|+
name|port
argument_list|,
name|factory
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|protected
name|String
name|getTestURI
parameter_list|()
block|{
name|int
name|proxyPort
init|=
name|getProxyPort
argument_list|()
decl_stmt|;
return|return
literal|"https://localhost:"
operator|+
name|proxyPort
operator|+
literal|"/websocket.html#wss://localhost:"
operator|+
name|port
return|;
block|}
block|}
end_class

end_unit

