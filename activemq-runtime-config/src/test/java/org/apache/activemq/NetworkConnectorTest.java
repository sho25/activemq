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
import|import static
name|org
operator|.
name|junit
operator|.
name|Assert
operator|.
name|assertEquals
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
name|assertFalse
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
name|assertNotNull
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
name|assertSame
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
name|assertTrue
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
name|fail
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|management
operator|.
name|InstanceNotFoundException
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
name|util
operator|.
name|Wait
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
name|NetworkConnectorTest
extends|extends
name|RuntimeConfigTestSupport
block|{
name|String
name|configurationSeed
init|=
literal|"networkConnectorTest"
decl_stmt|;
annotation|@
name|Test
specifier|public
name|void
name|testNew
parameter_list|()
throws|throws
name|Exception
block|{
specifier|final
name|String
name|brokerConfig
init|=
name|configurationSeed
operator|+
literal|"-no-nc-broker"
decl_stmt|;
name|applyNewConfig
argument_list|(
name|brokerConfig
argument_list|,
name|EMPTY_UPDATABLE_CONFIG
argument_list|)
expr_stmt|;
name|startBroker
argument_list|(
name|brokerConfig
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
literal|"broker alive"
argument_list|,
name|brokerService
operator|.
name|isStarted
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"no network connectors"
argument_list|,
literal|0
argument_list|,
name|brokerService
operator|.
name|getNetworkConnectors
argument_list|()
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|applyNewConfig
argument_list|(
name|brokerConfig
argument_list|,
name|configurationSeed
operator|+
literal|"-one-nc"
argument_list|,
name|SLEEP
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
literal|"new network connectors"
argument_list|,
name|Wait
operator|.
name|waitFor
argument_list|(
operator|new
name|Wait
operator|.
name|Condition
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|boolean
name|isSatisified
parameter_list|()
throws|throws
name|Exception
block|{
return|return
literal|1
operator|==
name|brokerService
operator|.
name|getNetworkConnectors
argument_list|()
operator|.
name|size
argument_list|()
return|;
block|}
block|}
argument_list|)
argument_list|)
expr_stmt|;
comment|// apply again - ensure no change
name|NetworkConnector
name|networkConnector
init|=
name|brokerService
operator|.
name|getNetworkConnectors
argument_list|()
operator|.
name|get
argument_list|(
literal|0
argument_list|)
decl_stmt|;
name|applyNewConfig
argument_list|(
name|brokerConfig
argument_list|,
name|configurationSeed
operator|+
literal|"-one-nc"
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"no new network connectors"
argument_list|,
literal|1
argument_list|,
name|brokerService
operator|.
name|getNetworkConnectors
argument_list|()
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|assertSame
argument_list|(
literal|"same instance"
argument_list|,
name|networkConnector
argument_list|,
name|brokerService
operator|.
name|getNetworkConnectors
argument_list|()
operator|.
name|get
argument_list|(
literal|0
argument_list|)
argument_list|)
expr_stmt|;
comment|// verify nested elements
name|assertEquals
argument_list|(
literal|"has exclusions"
argument_list|,
literal|2
argument_list|,
name|networkConnector
operator|.
name|getExcludedDestinations
argument_list|()
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"one statically included"
argument_list|,
literal|1
argument_list|,
name|networkConnector
operator|.
name|getStaticallyIncludedDestinations
argument_list|()
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"one dynamically included"
argument_list|,
literal|1
argument_list|,
name|networkConnector
operator|.
name|getDynamicallyIncludedDestinations
argument_list|()
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"one durable"
argument_list|,
literal|1
argument_list|,
name|networkConnector
operator|.
name|getDurableDestinations
argument_list|()
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|networkConnector
operator|.
name|getBrokerName
argument_list|()
operator|.
name|isEmpty
argument_list|()
argument_list|)
expr_stmt|;
name|assertNotNull
argument_list|(
name|brokerService
operator|.
name|getManagementContext
argument_list|()
operator|.
name|getObjectInstance
argument_list|(
name|brokerService
operator|.
name|createNetworkConnectorObjectName
argument_list|(
name|networkConnector
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testMod
parameter_list|()
throws|throws
name|Exception
block|{
specifier|final
name|String
name|brokerConfig
init|=
name|configurationSeed
operator|+
literal|"-one-nc-broker"
decl_stmt|;
name|applyNewConfig
argument_list|(
name|brokerConfig
argument_list|,
name|configurationSeed
operator|+
literal|"-one-nc"
argument_list|)
expr_stmt|;
name|startBroker
argument_list|(
name|brokerConfig
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
literal|"broker alive"
argument_list|,
name|brokerService
operator|.
name|isStarted
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"one network connectors"
argument_list|,
literal|1
argument_list|,
name|brokerService
operator|.
name|getNetworkConnectors
argument_list|()
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
comment|// track the original
name|NetworkConnector
name|networkConnector
init|=
name|brokerService
operator|.
name|getNetworkConnectors
argument_list|()
operator|.
name|get
argument_list|(
literal|0
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|"network ttl is default"
argument_list|,
literal|1
argument_list|,
name|networkConnector
operator|.
name|getNetworkTTL
argument_list|()
argument_list|)
expr_stmt|;
name|applyNewConfig
argument_list|(
name|brokerConfig
argument_list|,
name|configurationSeed
operator|+
literal|"-mod-one-nc"
argument_list|,
name|SLEEP
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"still one network connectors"
argument_list|,
literal|1
argument_list|,
name|brokerService
operator|.
name|getNetworkConnectors
argument_list|()
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|NetworkConnector
name|modNetworkConnector
init|=
name|brokerService
operator|.
name|getNetworkConnectors
argument_list|()
operator|.
name|get
argument_list|(
literal|0
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|"got ttl update"
argument_list|,
literal|2
argument_list|,
name|modNetworkConnector
operator|.
name|getNetworkTTL
argument_list|()
argument_list|)
expr_stmt|;
name|assertNotNull
argument_list|(
literal|"got ssl"
argument_list|,
name|modNetworkConnector
operator|.
name|getSslContext
argument_list|()
argument_list|)
expr_stmt|;
comment|// apply again - ensure no change
name|applyNewConfig
argument_list|(
name|brokerConfig
argument_list|,
name|configurationSeed
operator|+
literal|"-mod-one-nc"
argument_list|,
name|SLEEP
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"no new network connectors"
argument_list|,
literal|1
argument_list|,
name|brokerService
operator|.
name|getNetworkConnectors
argument_list|()
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|assertSame
argument_list|(
literal|"same instance"
argument_list|,
name|modNetworkConnector
argument_list|,
name|brokerService
operator|.
name|getNetworkConnectors
argument_list|()
operator|.
name|get
argument_list|(
literal|0
argument_list|)
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|modNetworkConnector
operator|.
name|getBrokerName
argument_list|()
operator|.
name|isEmpty
argument_list|()
argument_list|)
expr_stmt|;
name|assertNotNull
argument_list|(
name|brokerService
operator|.
name|getManagementContext
argument_list|()
operator|.
name|getObjectInstance
argument_list|(
name|brokerService
operator|.
name|createNetworkConnectorObjectName
argument_list|(
name|modNetworkConnector
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testRemove
parameter_list|()
throws|throws
name|Exception
block|{
specifier|final
name|String
name|brokerConfig
init|=
name|configurationSeed
operator|+
literal|"-two-nc-broker"
decl_stmt|;
name|applyNewConfig
argument_list|(
name|brokerConfig
argument_list|,
name|configurationSeed
operator|+
literal|"-two-nc"
argument_list|)
expr_stmt|;
name|startBroker
argument_list|(
name|brokerConfig
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
literal|"broker alive"
argument_list|,
name|brokerService
operator|.
name|isStarted
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"correct network connectors"
argument_list|,
literal|2
argument_list|,
name|brokerService
operator|.
name|getNetworkConnectors
argument_list|()
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|NetworkConnector
name|two
init|=
name|brokerService
operator|.
name|getNetworkConnectors
argument_list|()
operator|.
name|get
argument_list|(
literal|1
argument_list|)
decl_stmt|;
name|applyNewConfig
argument_list|(
name|brokerConfig
argument_list|,
name|configurationSeed
operator|+
literal|"-one-nc"
argument_list|,
name|SLEEP
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
literal|"expected mod on time"
argument_list|,
name|Wait
operator|.
name|waitFor
argument_list|(
operator|new
name|Wait
operator|.
name|Condition
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|boolean
name|isSatisified
parameter_list|()
throws|throws
name|Exception
block|{
return|return
literal|1
operator|==
name|brokerService
operator|.
name|getNetworkConnectors
argument_list|()
operator|.
name|size
argument_list|()
return|;
block|}
block|}
argument_list|)
argument_list|)
expr_stmt|;
name|NetworkConnector
name|remainingNetworkConnector
init|=
name|brokerService
operator|.
name|getNetworkConnectors
argument_list|()
operator|.
name|get
argument_list|(
literal|0
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|"name match"
argument_list|,
literal|"one"
argument_list|,
name|remainingNetworkConnector
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
try|try
block|{
name|brokerService
operator|.
name|getManagementContext
argument_list|()
operator|.
name|getObjectInstance
argument_list|(
name|brokerService
operator|.
name|createNetworkConnectorObjectName
argument_list|(
name|two
argument_list|)
argument_list|)
expr_stmt|;
name|fail
argument_list|(
literal|"mbean for nc2 should not exist"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|InstanceNotFoundException
name|e
parameter_list|)
block|{
comment|//should throw exception
block|}
name|assertNotNull
argument_list|(
name|brokerService
operator|.
name|getManagementContext
argument_list|()
operator|.
name|getObjectInstance
argument_list|(
name|brokerService
operator|.
name|createNetworkConnectorObjectName
argument_list|(
name|remainingNetworkConnector
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

