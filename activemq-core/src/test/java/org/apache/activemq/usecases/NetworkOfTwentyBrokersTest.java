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
name|java
operator|.
name|net
operator|.
name|URI
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
name|java
operator|.
name|util
operator|.
name|Iterator
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
name|broker
operator|.
name|TransportConnector
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
name|region
operator|.
name|RegionBroker
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
name|command
operator|.
name|BrokerInfo
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
name|ThreadTracker
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

begin_class
specifier|public
class|class
name|NetworkOfTwentyBrokersTest
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
name|NetworkOfTwentyBrokersTest
operator|.
name|class
argument_list|)
decl_stmt|;
comment|// This will interconnect all brokers using multicast
specifier|protected
name|void
name|bridgeAllBrokers
parameter_list|()
throws|throws
name|Exception
block|{
name|bridgeAllBrokers
argument_list|(
literal|"TwentyBrokersTest"
argument_list|,
literal|1
argument_list|,
literal|false
argument_list|,
literal|false
argument_list|)
expr_stmt|;
block|}
specifier|protected
name|void
name|bridgeAllBrokers
parameter_list|(
name|String
name|groupName
parameter_list|,
name|int
name|ttl
parameter_list|,
name|boolean
name|suppressduplicateQueueSubs
parameter_list|)
throws|throws
name|Exception
block|{
name|bridgeAllBrokers
argument_list|(
name|groupName
argument_list|,
name|ttl
argument_list|,
name|suppressduplicateQueueSubs
argument_list|,
literal|false
argument_list|)
expr_stmt|;
block|}
specifier|protected
name|void
name|bridgeAllBrokers
parameter_list|(
name|String
name|groupName
parameter_list|,
name|int
name|ttl
parameter_list|,
name|boolean
name|suppressduplicateQueueSubs
parameter_list|,
name|boolean
name|decreasePriority
parameter_list|)
throws|throws
name|Exception
block|{
name|Collection
argument_list|<
name|BrokerItem
argument_list|>
name|brokerList
init|=
name|brokers
operator|.
name|values
argument_list|()
decl_stmt|;
for|for
control|(
name|Iterator
argument_list|<
name|BrokerItem
argument_list|>
name|i
init|=
name|brokerList
operator|.
name|iterator
argument_list|()
init|;
name|i
operator|.
name|hasNext
argument_list|()
condition|;
control|)
block|{
name|BrokerService
name|broker
init|=
name|i
operator|.
name|next
argument_list|()
operator|.
name|broker
decl_stmt|;
name|List
argument_list|<
name|TransportConnector
argument_list|>
name|transportConnectors
init|=
name|broker
operator|.
name|getTransportConnectors
argument_list|()
decl_stmt|;
if|if
condition|(
name|transportConnectors
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
name|broker
operator|.
name|addConnector
argument_list|(
operator|new
name|URI
argument_list|(
name|AUTO_ASSIGN_TRANSPORT
argument_list|)
argument_list|)
expr_stmt|;
name|transportConnectors
operator|=
name|broker
operator|.
name|getTransportConnectors
argument_list|()
expr_stmt|;
block|}
name|TransportConnector
name|transport
init|=
name|transportConnectors
operator|.
name|get
argument_list|(
literal|0
argument_list|)
decl_stmt|;
if|if
condition|(
name|transport
operator|.
name|getDiscoveryUri
argument_list|()
operator|==
literal|null
condition|)
block|{
name|transport
operator|.
name|setDiscoveryUri
argument_list|(
operator|new
name|URI
argument_list|(
literal|"multicast://default?group="
operator|+
name|groupName
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|List
argument_list|<
name|NetworkConnector
argument_list|>
name|networkConnectors
init|=
name|broker
operator|.
name|getNetworkConnectors
argument_list|()
decl_stmt|;
if|if
condition|(
name|networkConnectors
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
name|broker
operator|.
name|addNetworkConnector
argument_list|(
literal|"multicast://default?group="
operator|+
name|groupName
argument_list|)
expr_stmt|;
name|networkConnectors
operator|=
name|broker
operator|.
name|getNetworkConnectors
argument_list|()
expr_stmt|;
block|}
name|NetworkConnector
name|nc
init|=
name|networkConnectors
operator|.
name|get
argument_list|(
literal|0
argument_list|)
decl_stmt|;
name|nc
operator|.
name|setNetworkTTL
argument_list|(
name|ttl
argument_list|)
expr_stmt|;
name|nc
operator|.
name|setSuppressDuplicateQueueSubscriptions
argument_list|(
name|suppressduplicateQueueSubs
argument_list|)
expr_stmt|;
name|nc
operator|.
name|setDecreaseNetworkConsumerPriority
argument_list|(
name|decreasePriority
argument_list|)
expr_stmt|;
block|}
comment|// Multicasting may take longer to setup
name|maxSetupTime
operator|=
literal|8000
expr_stmt|;
block|}
specifier|protected
name|BrokerService
name|createBroker
parameter_list|(
name|String
name|brokerName
parameter_list|)
throws|throws
name|Exception
block|{
name|BrokerService
name|broker
init|=
operator|new
name|BrokerService
argument_list|()
decl_stmt|;
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
name|setBrokerName
argument_list|(
name|brokerName
argument_list|)
expr_stmt|;
name|broker
operator|.
name|addConnector
argument_list|(
operator|new
name|URI
argument_list|(
name|AUTO_ASSIGN_TRANSPORT
argument_list|)
argument_list|)
expr_stmt|;
name|brokers
operator|.
name|put
argument_list|(
name|brokerName
argument_list|,
operator|new
name|BrokerItem
argument_list|(
name|broker
argument_list|)
argument_list|)
expr_stmt|;
return|return
name|broker
return|;
block|}
comment|/* AMQ-3077 Bug */
specifier|public
name|void
name|testBrokers
parameter_list|()
throws|throws
name|Exception
block|{
name|int
name|X
init|=
literal|20
decl_stmt|;
name|int
name|i
decl_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"Creating X Brokers"
argument_list|)
expr_stmt|;
for|for
control|(
name|i
operator|=
literal|0
init|;
name|i
operator|<
name|X
condition|;
name|i
operator|++
control|)
block|{
name|createBroker
argument_list|(
literal|"Broker"
operator|+
name|i
argument_list|)
expr_stmt|;
block|}
name|bridgeAllBrokers
argument_list|()
expr_stmt|;
name|startAllBrokers
argument_list|()
expr_stmt|;
name|waitForBridgeFormation
argument_list|(
name|X
operator|-
literal|1
argument_list|)
expr_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"Waiting for complete formation"
argument_list|)
expr_stmt|;
try|try
block|{
name|Thread
operator|.
name|sleep
argument_list|(
literal|10000
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{         }
name|verifyPeerBrokerInfos
argument_list|(
name|X
operator|-
literal|1
argument_list|)
expr_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"Stopping half the brokers"
argument_list|)
expr_stmt|;
for|for
control|(
name|i
operator|=
literal|0
init|;
name|i
operator|<
name|X
operator|/
literal|2
condition|;
name|i
operator|++
control|)
block|{
name|destroyBroker
argument_list|(
literal|"Broker"
operator|+
name|i
argument_list|)
expr_stmt|;
block|}
name|LOG
operator|.
name|info
argument_list|(
literal|"Waiting for complete stop"
argument_list|)
expr_stmt|;
try|try
block|{
name|Thread
operator|.
name|sleep
argument_list|(
literal|10000
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{         }
name|verifyPeerBrokerInfos
argument_list|(
operator|(
name|X
operator|/
literal|2
operator|)
operator|-
literal|1
argument_list|)
expr_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"Recreating first half"
argument_list|)
expr_stmt|;
for|for
control|(
name|i
operator|=
literal|0
init|;
name|i
operator|<
name|X
operator|/
literal|2
condition|;
name|i
operator|++
control|)
block|{
name|createBroker
argument_list|(
literal|"Broker"
operator|+
name|i
argument_list|)
expr_stmt|;
block|}
name|bridgeAllBrokers
argument_list|()
expr_stmt|;
name|startAllBrokers
argument_list|()
expr_stmt|;
name|waitForBridgeFormation
argument_list|(
name|X
operator|-
literal|1
argument_list|)
expr_stmt|;
name|verifyPeerBrokerInfos
argument_list|(
name|X
operator|-
literal|1
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|testPeerBrokerCountHalfPeer
parameter_list|()
throws|throws
name|Exception
block|{
name|createBroker
argument_list|(
literal|"A"
argument_list|)
expr_stmt|;
name|createBroker
argument_list|(
literal|"B"
argument_list|)
expr_stmt|;
name|bridgeBrokers
argument_list|(
literal|"A"
argument_list|,
literal|"B"
argument_list|)
expr_stmt|;
name|startAllBrokers
argument_list|()
expr_stmt|;
name|verifyPeerBrokerInfo
argument_list|(
name|brokers
operator|.
name|get
argument_list|(
literal|"A"
argument_list|)
argument_list|,
literal|1
argument_list|)
expr_stmt|;
name|verifyPeerBrokerInfo
argument_list|(
name|brokers
operator|.
name|get
argument_list|(
literal|"B"
argument_list|)
argument_list|,
literal|0
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|testPeerBrokerCountHalfPeerTwice
parameter_list|()
throws|throws
name|Exception
block|{
name|createBroker
argument_list|(
literal|"A"
argument_list|)
expr_stmt|;
name|createBroker
argument_list|(
literal|"B"
argument_list|)
expr_stmt|;
name|bridgeBrokers
argument_list|(
literal|"A"
argument_list|,
literal|"B"
argument_list|)
expr_stmt|;
name|bridgeBrokers
argument_list|(
literal|"A"
argument_list|,
literal|"B"
argument_list|)
expr_stmt|;
name|startAllBrokers
argument_list|()
expr_stmt|;
name|verifyPeerBrokerInfo
argument_list|(
name|brokers
operator|.
name|get
argument_list|(
literal|"A"
argument_list|)
argument_list|,
literal|1
argument_list|)
expr_stmt|;
name|verifyPeerBrokerInfo
argument_list|(
name|brokers
operator|.
name|get
argument_list|(
literal|"B"
argument_list|)
argument_list|,
literal|0
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|testPeerBrokerCountFullPeer
parameter_list|()
throws|throws
name|Exception
block|{
name|createBroker
argument_list|(
literal|"A"
argument_list|)
expr_stmt|;
name|createBroker
argument_list|(
literal|"B"
argument_list|)
expr_stmt|;
name|bridgeBrokers
argument_list|(
literal|"A"
argument_list|,
literal|"B"
argument_list|)
expr_stmt|;
name|bridgeBrokers
argument_list|(
literal|"B"
argument_list|,
literal|"A"
argument_list|)
expr_stmt|;
name|startAllBrokers
argument_list|()
expr_stmt|;
name|verifyPeerBrokerInfo
argument_list|(
name|brokers
operator|.
name|get
argument_list|(
literal|"A"
argument_list|)
argument_list|,
literal|1
argument_list|)
expr_stmt|;
name|verifyPeerBrokerInfo
argument_list|(
name|brokers
operator|.
name|get
argument_list|(
literal|"B"
argument_list|)
argument_list|,
literal|1
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|testPeerBrokerCountFullPeerDuplex
parameter_list|()
throws|throws
name|Exception
block|{
name|createBroker
argument_list|(
literal|"A"
argument_list|)
expr_stmt|;
name|createBroker
argument_list|(
literal|"B"
argument_list|)
expr_stmt|;
name|NetworkConnector
name|nc
init|=
name|bridgeBrokers
argument_list|(
literal|"A"
argument_list|,
literal|"B"
argument_list|)
decl_stmt|;
name|nc
operator|.
name|setDuplex
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|startAllBrokers
argument_list|()
expr_stmt|;
name|verifyPeerBrokerInfo
argument_list|(
name|brokers
operator|.
name|get
argument_list|(
literal|"A"
argument_list|)
argument_list|,
literal|1
argument_list|)
expr_stmt|;
name|verifyPeerBrokerInfo
argument_list|(
name|brokers
operator|.
name|get
argument_list|(
literal|"B"
argument_list|)
argument_list|,
literal|1
argument_list|)
expr_stmt|;
block|}
specifier|private
name|void
name|verifyPeerBrokerInfo
parameter_list|(
name|BrokerItem
name|brokerItem
parameter_list|,
specifier|final
name|int
name|max
parameter_list|)
block|{
name|BrokerService
name|broker
init|=
name|brokerItem
operator|.
name|broker
decl_stmt|;
name|RegionBroker
name|regionBroker
init|=
operator|(
name|RegionBroker
operator|)
name|broker
operator|.
name|getRegionBroker
argument_list|()
decl_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"verify infos "
operator|+
name|broker
operator|.
name|getBrokerName
argument_list|()
operator|+
literal|", len: "
operator|+
name|regionBroker
operator|.
name|getPeerBrokerInfos
argument_list|()
operator|.
name|length
argument_list|)
expr_stmt|;
for|for
control|(
name|BrokerInfo
name|info
range|:
name|regionBroker
operator|.
name|getPeerBrokerInfos
argument_list|()
control|)
block|{
name|LOG
operator|.
name|info
argument_list|(
name|info
operator|.
name|getBrokerName
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|assertEquals
argument_list|(
name|broker
operator|.
name|getBrokerName
argument_list|()
argument_list|,
name|max
argument_list|,
name|regionBroker
operator|.
name|getPeerBrokerInfos
argument_list|()
operator|.
name|length
argument_list|)
expr_stmt|;
block|}
specifier|private
name|void
name|verifyPeerBrokerInfos
parameter_list|(
specifier|final
name|int
name|max
parameter_list|)
block|{
name|Collection
argument_list|<
name|BrokerItem
argument_list|>
name|brokerList
init|=
name|brokers
operator|.
name|values
argument_list|()
decl_stmt|;
for|for
control|(
name|Iterator
argument_list|<
name|BrokerItem
argument_list|>
name|i
init|=
name|brokerList
operator|.
name|iterator
argument_list|()
init|;
name|i
operator|.
name|hasNext
argument_list|()
condition|;
control|)
block|{
name|verifyPeerBrokerInfo
argument_list|(
name|i
operator|.
name|next
argument_list|()
argument_list|,
name|max
argument_list|)
expr_stmt|;
block|}
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
block|}
annotation|@
name|Override
specifier|public
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
name|ThreadTracker
operator|.
name|result
argument_list|()
expr_stmt|;
block|}
block|}
end_class

end_unit

