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
name|io
operator|.
name|File
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

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|HashSet
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
name|Set
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|jms
operator|.
name|MessageConsumer
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
name|DurableTopicSubscription
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
name|Topic
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
name|ActiveMQDestination
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
name|ActiveMQTopic
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
name|util
operator|.
name|IOHelper
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
comment|/**  * Show that both directions of a duplex bridge will properly restart the  * network durable consumers if dynamicOnly is false.  */
end_comment

begin_class
specifier|public
class|class
name|AMQ6366Test
extends|extends
name|JmsMultipleBrokersTestSupport
block|{
specifier|protected
specifier|static
specifier|final
name|Logger
name|LOG
init|=
name|LoggerFactory
operator|.
name|getLogger
argument_list|(
name|AMQ6366Test
operator|.
name|class
argument_list|)
decl_stmt|;
specifier|final
name|ActiveMQTopic
name|dest
init|=
operator|new
name|ActiveMQTopic
argument_list|(
literal|"TEST.FOO"
argument_list|)
decl_stmt|;
comment|/**      * This test works even before AMQ6366      * @throws Exception      */
specifier|public
name|void
name|testDuplexDurableSubRestarted
parameter_list|()
throws|throws
name|Exception
block|{
name|testNonDurableReceiveThrougRestart
argument_list|(
literal|"BrokerA"
argument_list|,
literal|"BrokerB"
argument_list|)
expr_stmt|;
block|}
comment|/**      * This test failed before AMQ6366 because the NC durable consumer was      * never properly activated.      *      * @throws Exception      */
specifier|public
name|void
name|testDuplexDurableSubRestartedReverse
parameter_list|()
throws|throws
name|Exception
block|{
name|testNonDurableReceiveThrougRestart
argument_list|(
literal|"BrokerB"
argument_list|,
literal|"BrokerA"
argument_list|)
expr_stmt|;
block|}
specifier|protected
name|void
name|testNonDurableReceiveThrougRestart
parameter_list|(
name|String
name|pubBroker
parameter_list|,
name|String
name|conBroker
parameter_list|)
throws|throws
name|Exception
block|{
name|NetworkConnector
name|networkConnector
init|=
name|bridgeBrokerPair
argument_list|(
literal|"BrokerA"
argument_list|,
literal|"BrokerB"
argument_list|)
decl_stmt|;
name|startAllBrokers
argument_list|()
expr_stmt|;
name|waitForBridgeFormation
argument_list|()
expr_stmt|;
name|MessageConsumer
name|client
init|=
name|createDurableSubscriber
argument_list|(
name|conBroker
argument_list|,
name|dest
argument_list|,
literal|"sub1"
argument_list|)
decl_stmt|;
name|client
operator|.
name|close
argument_list|()
expr_stmt|;
name|Thread
operator|.
name|sleep
argument_list|(
literal|1000
argument_list|)
expr_stmt|;
name|networkConnector
operator|.
name|stop
argument_list|()
expr_stmt|;
name|Thread
operator|.
name|sleep
argument_list|(
literal|1000
argument_list|)
expr_stmt|;
name|Set
argument_list|<
name|ActiveMQDestination
argument_list|>
name|durableDests
init|=
operator|new
name|HashSet
argument_list|<>
argument_list|()
decl_stmt|;
name|durableDests
operator|.
name|add
argument_list|(
name|dest
argument_list|)
expr_stmt|;
comment|//Normally set on broker start from the persistence layer but
comment|//simulate here since we just stopped and started the network connector
comment|//without a restart
name|networkConnector
operator|.
name|setDurableDestinations
argument_list|(
name|durableDests
argument_list|)
expr_stmt|;
name|networkConnector
operator|.
name|start
argument_list|()
expr_stmt|;
name|waitForBridgeFormation
argument_list|()
expr_stmt|;
comment|// Send messages
name|sendMessages
argument_list|(
name|pubBroker
argument_list|,
name|dest
argument_list|,
literal|1
argument_list|)
expr_stmt|;
name|Thread
operator|.
name|sleep
argument_list|(
literal|1000
argument_list|)
expr_stmt|;
name|Topic
name|destination
init|=
operator|(
name|Topic
operator|)
name|brokers
operator|.
name|get
argument_list|(
name|conBroker
argument_list|)
operator|.
name|broker
operator|.
name|getDestination
argument_list|(
name|dest
argument_list|)
decl_stmt|;
name|DurableTopicSubscription
name|sub
init|=
name|destination
operator|.
name|getDurableTopicSubs
argument_list|()
operator|.
name|values
argument_list|()
operator|.
name|toArray
argument_list|(
operator|new
name|DurableTopicSubscription
index|[
literal|0
index|]
argument_list|)
index|[
literal|0
index|]
decl_stmt|;
comment|//Assert that the message made it to the other broker
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|sub
operator|.
name|getSubscriptionStatistics
argument_list|()
operator|.
name|getEnqueues
argument_list|()
operator|.
name|getCount
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|protected
name|void
name|configureBroker
parameter_list|(
name|BrokerService
name|broker
parameter_list|)
block|{
name|broker
operator|.
name|getManagementContext
argument_list|()
operator|.
name|setCreateConnector
argument_list|(
literal|false
argument_list|)
expr_stmt|;
name|broker
operator|.
name|setAdvisorySupport
argument_list|(
literal|true
argument_list|)
expr_stmt|;
block|}
specifier|protected
name|NetworkConnector
name|bridgeBrokerPair
parameter_list|(
name|String
name|localBrokerName
parameter_list|,
name|String
name|remoteBrokerName
parameter_list|)
throws|throws
name|Exception
block|{
name|BrokerService
name|localBroker
init|=
name|brokers
operator|.
name|get
argument_list|(
name|localBrokerName
argument_list|)
operator|.
name|broker
decl_stmt|;
name|BrokerService
name|remoteBroker
init|=
name|brokers
operator|.
name|get
argument_list|(
name|remoteBrokerName
argument_list|)
operator|.
name|broker
decl_stmt|;
name|List
argument_list|<
name|TransportConnector
argument_list|>
name|transportConnectors
init|=
name|remoteBroker
operator|.
name|getTransportConnectors
argument_list|()
decl_stmt|;
name|URI
name|remoteURI
decl_stmt|;
if|if
condition|(
operator|!
name|transportConnectors
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
name|remoteURI
operator|=
name|transportConnectors
operator|.
name|get
argument_list|(
literal|0
argument_list|)
operator|.
name|getConnectUri
argument_list|()
expr_stmt|;
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
name|setDynamicOnly
argument_list|(
literal|false
argument_list|)
expr_stmt|;
comment|// so matching durable subs are loaded on start
name|connector
operator|.
name|setStaticBridge
argument_list|(
literal|false
argument_list|)
expr_stmt|;
name|connector
operator|.
name|setDuplex
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|connector
operator|.
name|addDynamicallyIncludedDestination
argument_list|(
name|dest
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
else|else
block|{
throw|throw
operator|new
name|Exception
argument_list|(
literal|"Remote broker has no registered connectors."
argument_list|)
throw|;
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
name|File
name|dataDir
init|=
operator|new
name|File
argument_list|(
name|IOHelper
operator|.
name|getDefaultDataDirectory
argument_list|()
argument_list|)
decl_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"Delete dataDir.."
operator|+
name|dataDir
operator|.
name|getCanonicalPath
argument_list|()
argument_list|)
expr_stmt|;
name|org
operator|.
name|apache
operator|.
name|activemq
operator|.
name|TestSupport
operator|.
name|recursiveDelete
argument_list|(
name|dataDir
argument_list|)
expr_stmt|;
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
name|createBroker
argument_list|(
operator|new
name|URI
argument_list|(
literal|"broker:(tcp://0.0.0.0:0)/BrokerA"
argument_list|)
argument_list|)
expr_stmt|;
name|createBroker
argument_list|(
operator|new
name|URI
argument_list|(
literal|"broker:(tcp://0.0.0.0:0)/BrokerB"
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

