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
name|bugs
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
name|URI
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|concurrent
operator|.
name|CountDownLatch
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|concurrent
operator|.
name|TimeUnit
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|management
operator|.
name|ObjectName
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
name|Broker
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
name|BrokerFilter
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
name|BrokerPlugin
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
name|ConnectionContext
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
name|ConnectionInfo
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
name|DiscoveryEvent
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
name|NetworkBridge
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
name|NetworkBridgeListener
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
name|thread
operator|.
name|TaskRunnerFactory
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
name|Transport
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
name|discovery
operator|.
name|DiscoveryAgent
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
name|discovery
operator|.
name|DiscoveryListener
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
name|discovery
operator|.
name|simple
operator|.
name|SimpleDiscoveryAgent
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|Assert
import|;
end_import

begin_comment
comment|/**  * This test demonstrates a number of race conditions in  * {@link DiscoveryNetworkConnector} that can result in an active bridge no  * longer being reported as active and vice-versa, an inactive bridge still  * being reported as active.  */
end_comment

begin_class
specifier|public
class|class
name|AMQ4160Test
extends|extends
name|JmsMultipleBrokersTestSupport
block|{
comment|/**      * This test demonstrates how concurrent attempts to establish a bridge to      * the same remote broker are allowed to occur. Connection uniqueness will      * cause whichever bridge creation attempt is second to fail. However, this      * failure erases the entry in      * {@link DiscoveryNetworkConnector#activeBridges()} that represents the      * successful first bridge creation attempt.      */
specifier|public
name|void
name|testLostActiveBridge
parameter_list|()
throws|throws
name|Exception
block|{
comment|// Start two brokers with a bridge from broker1 to broker2.
name|BrokerService
name|broker1
init|=
name|createBroker
argument_list|(
operator|new
name|URI
argument_list|(
literal|"broker:(vm://broker1)/broker1?persistent=false"
argument_list|)
argument_list|)
decl_stmt|;
specifier|final
name|BrokerService
name|broker2
init|=
name|createBroker
argument_list|(
operator|new
name|URI
argument_list|(
literal|"broker:(vm://broker2)/broker2?persistent=false"
argument_list|)
argument_list|)
decl_stmt|;
comment|// Allow the concurrent local bridge connections to be made even though
comment|// they are duplicated; this prevents both of the bridge attempts from
comment|// failing in the case that the local and remote bridges are established
comment|// out-of-order.
name|BrokerPlugin
name|ignoreAddConnectionPlugin
init|=
operator|new
name|BrokerPlugin
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|Broker
name|installPlugin
parameter_list|(
name|Broker
name|broker
parameter_list|)
throws|throws
name|Exception
block|{
return|return
operator|new
name|BrokerFilter
argument_list|(
name|broker
argument_list|)
block|{
annotation|@
name|Override
specifier|public
name|void
name|addConnection
parameter_list|(
name|ConnectionContext
name|context
parameter_list|,
name|ConnectionInfo
name|info
parameter_list|)
throws|throws
name|Exception
block|{
comment|// ignore
block|}
block|}
return|;
block|}
block|}
decl_stmt|;
name|broker1
operator|.
name|setPlugins
argument_list|(
operator|new
name|BrokerPlugin
index|[]
block|{
name|ignoreAddConnectionPlugin
block|}
argument_list|)
expr_stmt|;
name|startAllBrokers
argument_list|()
expr_stmt|;
comment|// Start a bridge from broker1 to broker2. The discovery agent attempts
comment|// to create the bridge concurrently with two threads, and the
comment|// synchronization in createBridge ensures that both threads actually
comment|// attempt to start bridges.
specifier|final
name|CountDownLatch
name|createLatch
init|=
operator|new
name|CountDownLatch
argument_list|(
literal|2
argument_list|)
decl_stmt|;
name|DiscoveryNetworkConnector
name|nc
init|=
operator|new
name|DiscoveryNetworkConnector
argument_list|()
block|{
annotation|@
name|Override
specifier|protected
name|NetworkBridge
name|createBridge
parameter_list|(
name|Transport
name|localTransport
parameter_list|,
name|Transport
name|remoteTransport
parameter_list|,
specifier|final
name|DiscoveryEvent
name|event
parameter_list|)
block|{
name|createLatch
operator|.
name|countDown
argument_list|()
expr_stmt|;
try|try
block|{
name|createLatch
operator|.
name|await
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|InterruptedException
name|e
parameter_list|)
block|{                 }
return|return
name|super
operator|.
name|createBridge
argument_list|(
name|localTransport
argument_list|,
name|remoteTransport
argument_list|,
name|event
argument_list|)
return|;
block|}
block|}
decl_stmt|;
name|nc
operator|.
name|setDiscoveryAgent
argument_list|(
operator|new
name|DiscoveryAgent
argument_list|()
block|{
name|TaskRunnerFactory
name|taskRunner
init|=
operator|new
name|TaskRunnerFactory
argument_list|()
decl_stmt|;
name|DiscoveryListener
name|listener
decl_stmt|;
annotation|@
name|Override
specifier|public
name|void
name|start
parameter_list|()
throws|throws
name|Exception
block|{
name|taskRunner
operator|.
name|init
argument_list|()
expr_stmt|;
name|taskRunner
operator|.
name|execute
argument_list|(
operator|new
name|Runnable
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|void
name|run
parameter_list|()
block|{
name|listener
operator|.
name|onServiceAdd
argument_list|(
operator|new
name|DiscoveryEvent
argument_list|(
name|broker2
operator|.
name|getVmConnectorURI
argument_list|()
operator|.
name|toString
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
argument_list|)
expr_stmt|;
name|taskRunner
operator|.
name|execute
argument_list|(
operator|new
name|Runnable
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|void
name|run
parameter_list|()
block|{
name|listener
operator|.
name|onServiceAdd
argument_list|(
operator|new
name|DiscoveryEvent
argument_list|(
name|broker2
operator|.
name|getVmConnectorURI
argument_list|()
operator|.
name|toString
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|stop
parameter_list|()
throws|throws
name|Exception
block|{
name|taskRunner
operator|.
name|shutdown
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|setDiscoveryListener
parameter_list|(
name|DiscoveryListener
name|listener
parameter_list|)
block|{
name|this
operator|.
name|listener
operator|=
name|listener
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|registerService
parameter_list|(
name|String
name|name
parameter_list|)
throws|throws
name|IOException
block|{             }
annotation|@
name|Override
specifier|public
name|void
name|serviceFailed
parameter_list|(
name|DiscoveryEvent
name|event
parameter_list|)
throws|throws
name|IOException
block|{
name|listener
operator|.
name|onServiceRemove
argument_list|(
name|event
argument_list|)
expr_stmt|;
block|}
block|}
argument_list|)
expr_stmt|;
name|broker1
operator|.
name|addNetworkConnector
argument_list|(
name|nc
argument_list|)
expr_stmt|;
name|nc
operator|.
name|start
argument_list|()
expr_stmt|;
comment|// The bridge should be formed by the second creation attempt, but the
comment|// wait will time out because the active bridge entry from the second
comment|// (successful) bridge creation attempt is removed by the first
comment|// (unsuccessful) bridge creation attempt.
name|waitForBridgeFormation
argument_list|()
expr_stmt|;
name|Assert
operator|.
name|assertFalse
argument_list|(
name|nc
operator|.
name|activeBridges
argument_list|()
operator|.
name|isEmpty
argument_list|()
argument_list|)
expr_stmt|;
block|}
comment|/**      * This test demonstrates a race condition where a failed bridge can be      * removed from the list of active bridges in      * {@link DiscoveryNetworkConnector} before it has been added. Eventually,      * the failed bridge is added, but never removed, which prevents subsequent      * bridge creation attempts to be ignored. The result is a network connector      * that thinks it has an active bridge, when in fact it doesn't.      */
specifier|public
name|void
name|testInactiveBridgStillActive
parameter_list|()
throws|throws
name|Exception
block|{
comment|// Start two brokers with a bridge from broker1 to broker2.
name|BrokerService
name|broker1
init|=
name|createBroker
argument_list|(
operator|new
name|URI
argument_list|(
literal|"broker:(vm://broker1)/broker1?persistent=false"
argument_list|)
argument_list|)
decl_stmt|;
specifier|final
name|BrokerService
name|broker2
init|=
name|createBroker
argument_list|(
operator|new
name|URI
argument_list|(
literal|"broker:(vm://broker2)/broker2?persistent=false"
argument_list|)
argument_list|)
decl_stmt|;
comment|// Force bridge failure by having broker1 disallow connections.
name|BrokerPlugin
name|disallowAddConnectionPlugin
init|=
operator|new
name|BrokerPlugin
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|Broker
name|installPlugin
parameter_list|(
name|Broker
name|broker
parameter_list|)
throws|throws
name|Exception
block|{
return|return
operator|new
name|BrokerFilter
argument_list|(
name|broker
argument_list|)
block|{
annotation|@
name|Override
specifier|public
name|void
name|addConnection
parameter_list|(
name|ConnectionContext
name|context
parameter_list|,
name|ConnectionInfo
name|info
parameter_list|)
throws|throws
name|Exception
block|{
throw|throw
operator|new
name|Exception
argument_list|(
literal|"Test exception to force bridge failure"
argument_list|)
throw|;
block|}
block|}
return|;
block|}
block|}
decl_stmt|;
name|broker1
operator|.
name|setPlugins
argument_list|(
operator|new
name|BrokerPlugin
index|[]
block|{
name|disallowAddConnectionPlugin
block|}
argument_list|)
expr_stmt|;
name|startAllBrokers
argument_list|()
expr_stmt|;
comment|// Start a bridge from broker1 to broker2. The bridge delays returning
comment|// from start until after the bridge failure has been processed;
comment|// this leaves the first bridge creation attempt recorded as active,
comment|// even though it failed.
specifier|final
name|SimpleDiscoveryAgent
name|da
init|=
operator|new
name|SimpleDiscoveryAgent
argument_list|()
decl_stmt|;
name|da
operator|.
name|setServices
argument_list|(
operator|new
name|URI
index|[]
block|{
name|broker2
operator|.
name|getVmConnectorURI
argument_list|()
block|}
argument_list|)
expr_stmt|;
specifier|final
name|CountDownLatch
name|attemptLatch
init|=
operator|new
name|CountDownLatch
argument_list|(
literal|3
argument_list|)
decl_stmt|;
specifier|final
name|CountDownLatch
name|removedLatch
init|=
operator|new
name|CountDownLatch
argument_list|(
literal|1
argument_list|)
decl_stmt|;
name|DiscoveryNetworkConnector
name|nc
init|=
operator|new
name|DiscoveryNetworkConnector
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|void
name|onServiceAdd
parameter_list|(
name|DiscoveryEvent
name|event
parameter_list|)
block|{
name|attemptLatch
operator|.
name|countDown
argument_list|()
expr_stmt|;
name|super
operator|.
name|onServiceAdd
argument_list|(
name|event
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|onServiceRemove
parameter_list|(
name|DiscoveryEvent
name|event
parameter_list|)
block|{
name|super
operator|.
name|onServiceRemove
argument_list|(
name|event
argument_list|)
expr_stmt|;
name|removedLatch
operator|.
name|countDown
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Override
specifier|protected
name|NetworkBridge
name|createBridge
parameter_list|(
name|Transport
name|localTransport
parameter_list|,
name|Transport
name|remoteTransport
parameter_list|,
specifier|final
name|DiscoveryEvent
name|event
parameter_list|)
block|{
specifier|final
name|NetworkBridge
name|next
init|=
name|super
operator|.
name|createBridge
argument_list|(
name|localTransport
argument_list|,
name|remoteTransport
argument_list|,
name|event
argument_list|)
decl_stmt|;
return|return
operator|new
name|NetworkBridge
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|void
name|start
parameter_list|()
throws|throws
name|Exception
block|{
name|next
operator|.
name|start
argument_list|()
expr_stmt|;
comment|// Delay returning until the failed service has been
comment|// removed.
name|removedLatch
operator|.
name|await
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|stop
parameter_list|()
throws|throws
name|Exception
block|{
name|next
operator|.
name|stop
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|serviceRemoteException
parameter_list|(
name|Throwable
name|error
parameter_list|)
block|{
name|next
operator|.
name|serviceRemoteException
argument_list|(
name|error
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|serviceLocalException
parameter_list|(
name|Throwable
name|error
parameter_list|)
block|{
name|next
operator|.
name|serviceLocalException
argument_list|(
name|error
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|setNetworkBridgeListener
parameter_list|(
name|NetworkBridgeListener
name|listener
parameter_list|)
block|{
name|next
operator|.
name|setNetworkBridgeListener
argument_list|(
name|listener
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|String
name|getRemoteAddress
parameter_list|()
block|{
return|return
name|next
operator|.
name|getRemoteAddress
argument_list|()
return|;
block|}
annotation|@
name|Override
specifier|public
name|String
name|getRemoteBrokerName
parameter_list|()
block|{
return|return
name|next
operator|.
name|getRemoteBrokerName
argument_list|()
return|;
block|}
annotation|@
name|Override
specifier|public
name|String
name|getLocalAddress
parameter_list|()
block|{
return|return
name|next
operator|.
name|getLocalAddress
argument_list|()
return|;
block|}
annotation|@
name|Override
specifier|public
name|String
name|getLocalBrokerName
parameter_list|()
block|{
return|return
name|next
operator|.
name|getLocalBrokerName
argument_list|()
return|;
block|}
annotation|@
name|Override
specifier|public
name|long
name|getEnqueueCounter
parameter_list|()
block|{
return|return
name|next
operator|.
name|getEnqueueCounter
argument_list|()
return|;
block|}
annotation|@
name|Override
specifier|public
name|long
name|getDequeueCounter
parameter_list|()
block|{
return|return
name|next
operator|.
name|getDequeueCounter
argument_list|()
return|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|setMbeanObjectName
parameter_list|(
name|ObjectName
name|objectName
parameter_list|)
block|{
name|next
operator|.
name|setMbeanObjectName
argument_list|(
name|objectName
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|ObjectName
name|getMbeanObjectName
parameter_list|()
block|{
return|return
name|next
operator|.
name|getMbeanObjectName
argument_list|()
return|;
block|}
block|}
return|;
block|}
block|}
decl_stmt|;
name|nc
operator|.
name|setDiscoveryAgent
argument_list|(
name|da
argument_list|)
expr_stmt|;
name|broker1
operator|.
name|addNetworkConnector
argument_list|(
name|nc
argument_list|)
expr_stmt|;
name|nc
operator|.
name|start
argument_list|()
expr_stmt|;
comment|// All bridge attempts should fail, so the attempt latch should get
comment|// triggered. However, because of the race condition, the first attempt
comment|// is considered successful and causes further attempts to stop.
comment|// Therefore, this wait will time out and cause the test to fail.
name|Assert
operator|.
name|assertTrue
argument_list|(
name|attemptLatch
operator|.
name|await
argument_list|(
literal|30
argument_list|,
name|TimeUnit
operator|.
name|SECONDS
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit
