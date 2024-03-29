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
name|usecases
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
name|JmsMultipleBrokersTestSupport
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
name|network
operator|.
name|DiscoveryNetworkConnector
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
name|network
operator|.
name|NetworkConnector
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
name|java
operator|.
name|net
operator|.
name|URI
import|;
end_import

begin_class
specifier|public
class|class
name|NetworkAsyncStartSslTest
extends|extends
name|JmsMultipleBrokersTestSupport
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
name|NetworkAsyncStartSslTest
operator|.
name|class
argument_list|)
decl_stmt|;
specifier|private
name|String
name|brokerBDomain
init|=
literal|"localhost:61617"
decl_stmt|;
specifier|private
name|String
name|brokerCDomain
init|=
literal|"localhost:61618"
decl_stmt|;
name|int
name|bridgeCount
init|=
literal|0
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
specifier|public
name|void
name|testSslPerConnectorConfig
parameter_list|()
throws|throws
name|Exception
block|{
name|String
name|transport
init|=
literal|"ssl"
decl_stmt|;
name|String
name|brokerBUri
init|=
name|transport
operator|+
literal|"://"
operator|+
name|brokerBDomain
decl_stmt|;
name|String
name|brokerCUri
init|=
name|transport
operator|+
literal|"://"
operator|+
name|brokerCDomain
decl_stmt|;
name|SpringSslContext
name|brokerSslContext
init|=
operator|new
name|SpringSslContext
argument_list|()
decl_stmt|;
name|brokerSslContext
operator|.
name|setKeyStore
argument_list|(
name|SERVER_KEYSTORE
argument_list|)
expr_stmt|;
name|brokerSslContext
operator|.
name|setKeyStorePassword
argument_list|(
name|PASSWORD
argument_list|)
expr_stmt|;
name|brokerSslContext
operator|.
name|setKeyStoreType
argument_list|(
name|KEYSTORE_TYPE
argument_list|)
expr_stmt|;
name|brokerSslContext
operator|.
name|setTrustStore
argument_list|(
name|TRUST_KEYSTORE
argument_list|)
expr_stmt|;
name|brokerSslContext
operator|.
name|setTrustStorePassword
argument_list|(
name|PASSWORD
argument_list|)
expr_stmt|;
name|brokerSslContext
operator|.
name|afterPropertiesSet
argument_list|()
expr_stmt|;
name|BrokerService
name|brokerC
init|=
name|brokers
operator|.
name|get
argument_list|(
literal|"BrokerC"
argument_list|)
operator|.
name|broker
decl_stmt|;
name|brokerC
operator|.
name|setSslContext
argument_list|(
name|brokerSslContext
argument_list|)
expr_stmt|;
name|brokerC
operator|.
name|addConnector
argument_list|(
name|brokerCUri
argument_list|)
expr_stmt|;
name|brokerC
operator|.
name|start
argument_list|()
expr_stmt|;
name|BrokerService
name|brokerB
init|=
name|brokers
operator|.
name|get
argument_list|(
literal|"BrokerB"
argument_list|)
operator|.
name|broker
decl_stmt|;
name|brokerB
operator|.
name|setSslContext
argument_list|(
name|brokerSslContext
argument_list|)
expr_stmt|;
name|brokerB
operator|.
name|addConnector
argument_list|(
name|brokerBUri
argument_list|)
expr_stmt|;
name|brokerB
operator|.
name|start
argument_list|()
expr_stmt|;
name|BrokerService
name|brokerA
init|=
name|brokers
operator|.
name|get
argument_list|(
literal|"BrokerA"
argument_list|)
operator|.
name|broker
decl_stmt|;
name|brokerA
operator|.
name|setNetworkConnectorStartAsync
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|NetworkConnector
name|networkConnector
init|=
name|bridgeBroker
argument_list|(
name|brokerA
argument_list|,
name|brokerBUri
argument_list|)
decl_stmt|;
name|networkConnector
operator|.
name|setSslContext
argument_list|(
name|brokerSslContext
argument_list|)
expr_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"Added bridge to: "
operator|+
name|brokerBUri
argument_list|)
expr_stmt|;
comment|// no ssl context, will fail
name|bridgeBroker
argument_list|(
name|brokerA
argument_list|,
name|brokerCUri
argument_list|)
expr_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"Added bridge to: "
operator|+
name|brokerCUri
argument_list|)
expr_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"starting A.."
argument_list|)
expr_stmt|;
name|brokerA
operator|.
name|start
argument_list|()
expr_stmt|;
comment|// wait for A to get bridge to B
name|waitForBridgeFormation
argument_list|(
name|brokerA
argument_list|,
literal|1
argument_list|,
literal|0
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
literal|"one worked"
argument_list|,
name|hasBridge
argument_list|(
literal|"BrokerA"
argument_list|,
literal|"BrokerB"
argument_list|)
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
literal|"one failed"
argument_list|,
name|hasBridge
argument_list|(
literal|"BrokerA"
argument_list|,
literal|"BrokerC"
argument_list|)
argument_list|)
expr_stmt|;
block|}
specifier|private
name|NetworkConnector
name|bridgeBroker
parameter_list|(
name|BrokerService
name|localBroker
parameter_list|,
name|String
name|remoteURI
parameter_list|)
throws|throws
name|Exception
block|{
name|String
name|uri
init|=
literal|"static:("
operator|+
name|remoteURI
operator|+
literal|")"
decl_stmt|;
name|NetworkConnector
name|connector
init|=
operator|new
name|DiscoveryNetworkConnector
argument_list|(
operator|new
name|URI
argument_list|(
name|uri
argument_list|)
argument_list|)
decl_stmt|;
name|connector
operator|.
name|setName
argument_list|(
literal|"bridge-"
operator|+
name|bridgeCount
operator|++
argument_list|)
expr_stmt|;
name|localBroker
operator|.
name|addNetworkConnector
argument_list|(
name|connector
argument_list|)
expr_stmt|;
return|return
name|connector
return|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|setUp
parameter_list|()
throws|throws
name|Exception
block|{
name|super
operator|.
name|setAutoFail
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|super
operator|.
name|setUp
argument_list|()
expr_stmt|;
comment|// initially with no tcp transport connector
name|createBroker
argument_list|(
operator|new
name|URI
argument_list|(
literal|"broker:()BrokerA?persistent=false&useJmx=false"
argument_list|)
argument_list|)
expr_stmt|;
name|createBroker
argument_list|(
operator|new
name|URI
argument_list|(
literal|"broker:()BrokerB?persistent=false&useJmx=false"
argument_list|)
argument_list|)
expr_stmt|;
name|createBroker
argument_list|(
operator|new
name|URI
argument_list|(
literal|"broker:()BrokerC?persistent=false&useJmx=false"
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

