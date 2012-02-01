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
name|failover
package|;
end_package

begin_comment
comment|/**  * Complex cluster test that will exercise the dynamic failover capabilities of  * a network of brokers. Using a networking of 3 brokers where the 3rd broker is  * removed and then added back in it is expected in each test that the number of  * connections on the client should start with 3, then have two after the 3rd  * broker is removed and then show 3 after the 3rd broker is reintroduced.  */
end_comment

begin_class
specifier|public
class|class
name|FailoverComplexClusterTest
extends|extends
name|FailoverClusterTestSupport
block|{
specifier|private
specifier|static
specifier|final
name|String
name|BROKER_A_CLIENT_TC_ADDRESS
init|=
literal|"tcp://localhost:61616"
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|String
name|BROKER_B_CLIENT_TC_ADDRESS
init|=
literal|"tcp://localhost:61617"
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|String
name|BROKER_C_CLIENT_TC_ADDRESS
init|=
literal|"tcp://localhost:61618"
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|String
name|BROKER_A_NOB_TC_ADDRESS
init|=
literal|"tcp://localhost:61626"
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|String
name|BROKER_B_NOB_TC_ADDRESS
init|=
literal|"tcp://localhost:61627"
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|String
name|BROKER_C_NOB_TC_ADDRESS
init|=
literal|"tcp://localhost:61628"
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|String
name|BROKER_A_NAME
init|=
literal|"BROKERA"
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|String
name|BROKER_B_NAME
init|=
literal|"BROKERB"
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|String
name|BROKER_C_NAME
init|=
literal|"BROKERC"
decl_stmt|;
specifier|public
name|void
name|testThreeBrokerClusterSingleConnectorBasic
parameter_list|()
throws|throws
name|Exception
block|{
name|initSingleTcBroker
argument_list|(
literal|""
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|Thread
operator|.
name|sleep
argument_list|(
literal|2000
argument_list|)
expr_stmt|;
name|setClientUrl
argument_list|(
literal|"failover://("
operator|+
name|BROKER_A_CLIENT_TC_ADDRESS
operator|+
literal|","
operator|+
name|BROKER_B_CLIENT_TC_ADDRESS
operator|+
literal|")"
argument_list|)
expr_stmt|;
name|createClients
argument_list|()
expr_stmt|;
name|Thread
operator|.
name|sleep
argument_list|(
literal|2000
argument_list|)
expr_stmt|;
name|runTests
argument_list|(
literal|false
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|testThreeBrokerClusterSingleConnectorBackup
parameter_list|()
throws|throws
name|Exception
block|{
name|initSingleTcBroker
argument_list|(
literal|""
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|Thread
operator|.
name|sleep
argument_list|(
literal|2000
argument_list|)
expr_stmt|;
name|setClientUrl
argument_list|(
literal|"failover://("
operator|+
name|BROKER_A_CLIENT_TC_ADDRESS
operator|+
literal|","
operator|+
name|BROKER_B_CLIENT_TC_ADDRESS
operator|+
literal|")?backup=true&backupPoolSize=2"
argument_list|)
expr_stmt|;
name|createClients
argument_list|()
expr_stmt|;
name|Thread
operator|.
name|sleep
argument_list|(
literal|2000
argument_list|)
expr_stmt|;
name|runTests
argument_list|(
literal|false
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|testThreeBrokerClusterSingleConnectorWithParams
parameter_list|()
throws|throws
name|Exception
block|{
name|initSingleTcBroker
argument_list|(
literal|"?transport.closeAsync=false"
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|Thread
operator|.
name|sleep
argument_list|(
literal|2000
argument_list|)
expr_stmt|;
name|setClientUrl
argument_list|(
literal|"failover://("
operator|+
name|BROKER_A_CLIENT_TC_ADDRESS
operator|+
literal|","
operator|+
name|BROKER_B_CLIENT_TC_ADDRESS
operator|+
literal|")"
argument_list|)
expr_stmt|;
name|createClients
argument_list|()
expr_stmt|;
name|runTests
argument_list|(
literal|false
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|testThreeBrokerClusterMultipleConnectorBasic
parameter_list|()
throws|throws
name|Exception
block|{
name|initMultiTcCluster
argument_list|(
literal|""
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|Thread
operator|.
name|sleep
argument_list|(
literal|2000
argument_list|)
expr_stmt|;
name|setClientUrl
argument_list|(
literal|"failover://("
operator|+
name|BROKER_A_CLIENT_TC_ADDRESS
operator|+
literal|","
operator|+
name|BROKER_B_CLIENT_TC_ADDRESS
operator|+
literal|")"
argument_list|)
expr_stmt|;
name|createClients
argument_list|()
expr_stmt|;
name|runTests
argument_list|(
literal|true
argument_list|)
expr_stmt|;
block|}
comment|/**      * Runs a 3 tests:<br/>      *<ul>      *<li>asserts clients are distributed across all 3 brokers</li>      *<li>asserts clients are distributed across 2 brokers after removing the 3rd</li>      *<li>asserts clients are distributed across all 3 brokers after reintroducing the 3rd broker</li>      *</ul>      * @throws Exception      * @throws InterruptedException      */
specifier|private
name|void
name|runTests
parameter_list|(
name|boolean
name|multi
parameter_list|)
throws|throws
name|Exception
throws|,
name|InterruptedException
block|{
name|assertClientsConnectedToThreeBrokers
argument_list|()
expr_stmt|;
name|getBroker
argument_list|(
name|BROKER_C_NAME
argument_list|)
operator|.
name|stop
argument_list|()
expr_stmt|;
name|getBroker
argument_list|(
name|BROKER_C_NAME
argument_list|)
operator|.
name|waitUntilStopped
argument_list|()
expr_stmt|;
name|removeBroker
argument_list|(
name|BROKER_C_NAME
argument_list|)
expr_stmt|;
name|Thread
operator|.
name|sleep
argument_list|(
literal|5000
argument_list|)
expr_stmt|;
name|assertClientsConnectedToTwoBrokers
argument_list|()
expr_stmt|;
name|createBrokerC
argument_list|(
name|multi
argument_list|,
literal|""
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|getBroker
argument_list|(
name|BROKER_C_NAME
argument_list|)
operator|.
name|waitUntilStarted
argument_list|()
expr_stmt|;
name|Thread
operator|.
name|sleep
argument_list|(
literal|5000
argument_list|)
expr_stmt|;
name|assertClientsConnectedToThreeBrokers
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Override
specifier|protected
name|void
name|setUp
parameter_list|()
throws|throws
name|Exception
block|{     }
annotation|@
name|Override
specifier|protected
name|void
name|tearDown
parameter_list|()
throws|throws
name|Exception
block|{
name|shutdownClients
argument_list|()
expr_stmt|;
name|destroyBrokerCluster
argument_list|()
expr_stmt|;
name|Thread
operator|.
name|sleep
argument_list|(
literal|2000
argument_list|)
expr_stmt|;
block|}
specifier|private
name|void
name|initSingleTcBroker
parameter_list|(
name|String
name|params
parameter_list|,
name|String
name|clusterFilter
parameter_list|)
throws|throws
name|Exception
block|{
name|createBrokerA
argument_list|(
literal|false
argument_list|,
name|params
argument_list|,
name|clusterFilter
argument_list|)
expr_stmt|;
name|createBrokerB
argument_list|(
literal|false
argument_list|,
name|params
argument_list|,
name|clusterFilter
argument_list|)
expr_stmt|;
name|createBrokerC
argument_list|(
literal|false
argument_list|,
name|params
argument_list|,
name|clusterFilter
argument_list|)
expr_stmt|;
name|getBroker
argument_list|(
name|BROKER_C_NAME
argument_list|)
operator|.
name|waitUntilStarted
argument_list|()
expr_stmt|;
block|}
specifier|private
name|void
name|initMultiTcCluster
parameter_list|(
name|String
name|params
parameter_list|,
name|String
name|clusterFilter
parameter_list|)
throws|throws
name|Exception
block|{
name|createBrokerA
argument_list|(
literal|true
argument_list|,
name|params
argument_list|,
name|clusterFilter
argument_list|)
expr_stmt|;
name|createBrokerB
argument_list|(
literal|true
argument_list|,
name|params
argument_list|,
name|clusterFilter
argument_list|)
expr_stmt|;
name|createBrokerC
argument_list|(
literal|true
argument_list|,
name|params
argument_list|,
name|clusterFilter
argument_list|)
expr_stmt|;
name|getBroker
argument_list|(
name|BROKER_C_NAME
argument_list|)
operator|.
name|waitUntilStarted
argument_list|()
expr_stmt|;
block|}
specifier|private
name|void
name|createBrokerA
parameter_list|(
name|boolean
name|multi
parameter_list|,
name|String
name|params
parameter_list|,
name|String
name|clusterFilter
parameter_list|)
throws|throws
name|Exception
block|{
if|if
condition|(
name|getBroker
argument_list|(
name|BROKER_A_NAME
argument_list|)
operator|==
literal|null
condition|)
block|{
name|addBroker
argument_list|(
name|BROKER_A_NAME
argument_list|,
name|createBroker
argument_list|(
name|BROKER_A_NAME
argument_list|)
argument_list|)
expr_stmt|;
name|addTransportConnector
argument_list|(
name|getBroker
argument_list|(
name|BROKER_A_NAME
argument_list|)
argument_list|,
literal|"openwire"
argument_list|,
name|BROKER_A_CLIENT_TC_ADDRESS
operator|+
name|params
argument_list|,
literal|true
argument_list|)
expr_stmt|;
if|if
condition|(
name|multi
condition|)
block|{
name|addTransportConnector
argument_list|(
name|getBroker
argument_list|(
name|BROKER_A_NAME
argument_list|)
argument_list|,
literal|"network"
argument_list|,
name|BROKER_A_NOB_TC_ADDRESS
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|addNetworkBridge
argument_list|(
name|getBroker
argument_list|(
name|BROKER_A_NAME
argument_list|)
argument_list|,
literal|"A_2_B_Bridge"
argument_list|,
literal|"static://("
operator|+
name|BROKER_B_NOB_TC_ADDRESS
operator|+
literal|")?useExponentialBackOff=false"
argument_list|,
literal|false
argument_list|,
name|clusterFilter
argument_list|)
expr_stmt|;
name|addNetworkBridge
argument_list|(
name|getBroker
argument_list|(
name|BROKER_A_NAME
argument_list|)
argument_list|,
literal|"A_2_C_Bridge"
argument_list|,
literal|"static://("
operator|+
name|BROKER_C_NOB_TC_ADDRESS
operator|+
literal|")?useExponentialBackOff=false"
argument_list|,
literal|false
argument_list|,
literal|null
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|addNetworkBridge
argument_list|(
name|getBroker
argument_list|(
name|BROKER_A_NAME
argument_list|)
argument_list|,
literal|"A_2_B_Bridge"
argument_list|,
literal|"static://("
operator|+
name|BROKER_B_CLIENT_TC_ADDRESS
operator|+
literal|")?useExponentialBackOff=false"
argument_list|,
literal|false
argument_list|,
name|clusterFilter
argument_list|)
expr_stmt|;
name|addNetworkBridge
argument_list|(
name|getBroker
argument_list|(
name|BROKER_A_NAME
argument_list|)
argument_list|,
literal|"A_2_C_Bridge"
argument_list|,
literal|"static://("
operator|+
name|BROKER_C_CLIENT_TC_ADDRESS
operator|+
literal|")?useExponentialBackOff=false"
argument_list|,
literal|false
argument_list|,
literal|null
argument_list|)
expr_stmt|;
block|}
name|getBroker
argument_list|(
name|BROKER_A_NAME
argument_list|)
operator|.
name|start
argument_list|()
expr_stmt|;
block|}
block|}
specifier|private
name|void
name|createBrokerB
parameter_list|(
name|boolean
name|multi
parameter_list|,
name|String
name|params
parameter_list|,
name|String
name|clusterFilter
parameter_list|)
throws|throws
name|Exception
block|{
if|if
condition|(
name|getBroker
argument_list|(
name|BROKER_B_NAME
argument_list|)
operator|==
literal|null
condition|)
block|{
name|addBroker
argument_list|(
name|BROKER_B_NAME
argument_list|,
name|createBroker
argument_list|(
name|BROKER_B_NAME
argument_list|)
argument_list|)
expr_stmt|;
name|addTransportConnector
argument_list|(
name|getBroker
argument_list|(
name|BROKER_B_NAME
argument_list|)
argument_list|,
literal|"openwire"
argument_list|,
name|BROKER_B_CLIENT_TC_ADDRESS
operator|+
name|params
argument_list|,
literal|true
argument_list|)
expr_stmt|;
if|if
condition|(
name|multi
condition|)
block|{
name|addTransportConnector
argument_list|(
name|getBroker
argument_list|(
name|BROKER_B_NAME
argument_list|)
argument_list|,
literal|"network"
argument_list|,
name|BROKER_B_NOB_TC_ADDRESS
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|addNetworkBridge
argument_list|(
name|getBroker
argument_list|(
name|BROKER_B_NAME
argument_list|)
argument_list|,
literal|"B_2_A_Bridge"
argument_list|,
literal|"static://("
operator|+
name|BROKER_A_NOB_TC_ADDRESS
operator|+
literal|")?useExponentialBackOff=false"
argument_list|,
literal|false
argument_list|,
name|clusterFilter
argument_list|)
expr_stmt|;
name|addNetworkBridge
argument_list|(
name|getBroker
argument_list|(
name|BROKER_B_NAME
argument_list|)
argument_list|,
literal|"B_2_C_Bridge"
argument_list|,
literal|"static://("
operator|+
name|BROKER_C_NOB_TC_ADDRESS
operator|+
literal|")?useExponentialBackOff=false"
argument_list|,
literal|false
argument_list|,
literal|null
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|addNetworkBridge
argument_list|(
name|getBroker
argument_list|(
name|BROKER_B_NAME
argument_list|)
argument_list|,
literal|"B_2_A_Bridge"
argument_list|,
literal|"static://("
operator|+
name|BROKER_A_CLIENT_TC_ADDRESS
operator|+
literal|")?useExponentialBackOff=false"
argument_list|,
literal|false
argument_list|,
name|clusterFilter
argument_list|)
expr_stmt|;
name|addNetworkBridge
argument_list|(
name|getBroker
argument_list|(
name|BROKER_B_NAME
argument_list|)
argument_list|,
literal|"B_2_C_Bridge"
argument_list|,
literal|"static://("
operator|+
name|BROKER_C_CLIENT_TC_ADDRESS
operator|+
literal|")?useExponentialBackOff=false"
argument_list|,
literal|false
argument_list|,
literal|null
argument_list|)
expr_stmt|;
block|}
name|getBroker
argument_list|(
name|BROKER_B_NAME
argument_list|)
operator|.
name|start
argument_list|()
expr_stmt|;
block|}
block|}
specifier|private
name|void
name|createBrokerC
parameter_list|(
name|boolean
name|multi
parameter_list|,
name|String
name|params
parameter_list|,
name|String
name|clusterFilter
parameter_list|)
throws|throws
name|Exception
block|{
if|if
condition|(
name|getBroker
argument_list|(
name|BROKER_C_NAME
argument_list|)
operator|==
literal|null
condition|)
block|{
name|addBroker
argument_list|(
name|BROKER_C_NAME
argument_list|,
name|createBroker
argument_list|(
name|BROKER_C_NAME
argument_list|)
argument_list|)
expr_stmt|;
name|addTransportConnector
argument_list|(
name|getBroker
argument_list|(
name|BROKER_C_NAME
argument_list|)
argument_list|,
literal|"openwire"
argument_list|,
name|BROKER_C_CLIENT_TC_ADDRESS
operator|+
name|params
argument_list|,
literal|true
argument_list|)
expr_stmt|;
if|if
condition|(
name|multi
condition|)
block|{
name|addTransportConnector
argument_list|(
name|getBroker
argument_list|(
name|BROKER_C_NAME
argument_list|)
argument_list|,
literal|"network"
argument_list|,
name|BROKER_C_NOB_TC_ADDRESS
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|addNetworkBridge
argument_list|(
name|getBroker
argument_list|(
name|BROKER_C_NAME
argument_list|)
argument_list|,
literal|"C_2_A_Bridge"
argument_list|,
literal|"static://("
operator|+
name|BROKER_A_NOB_TC_ADDRESS
operator|+
literal|")?useExponentialBackOff=false"
argument_list|,
literal|false
argument_list|,
name|clusterFilter
argument_list|)
expr_stmt|;
name|addNetworkBridge
argument_list|(
name|getBroker
argument_list|(
name|BROKER_C_NAME
argument_list|)
argument_list|,
literal|"C_2_B_Bridge"
argument_list|,
literal|"static://("
operator|+
name|BROKER_B_NOB_TC_ADDRESS
operator|+
literal|")?useExponentialBackOff=false"
argument_list|,
literal|false
argument_list|,
literal|null
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|addNetworkBridge
argument_list|(
name|getBroker
argument_list|(
name|BROKER_C_NAME
argument_list|)
argument_list|,
literal|"C_2_A_Bridge"
argument_list|,
literal|"static://("
operator|+
name|BROKER_A_CLIENT_TC_ADDRESS
operator|+
literal|")?useExponentialBackOff=false"
argument_list|,
literal|false
argument_list|,
name|clusterFilter
argument_list|)
expr_stmt|;
name|addNetworkBridge
argument_list|(
name|getBroker
argument_list|(
name|BROKER_C_NAME
argument_list|)
argument_list|,
literal|"C_2_B_Bridge"
argument_list|,
literal|"static://("
operator|+
name|BROKER_B_CLIENT_TC_ADDRESS
operator|+
literal|")?useExponentialBackOff=false"
argument_list|,
literal|false
argument_list|,
literal|null
argument_list|)
expr_stmt|;
block|}
name|getBroker
argument_list|(
name|BROKER_C_NAME
argument_list|)
operator|.
name|start
argument_list|()
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

