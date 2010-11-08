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
name|http
package|;
end_package

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
name|apache
operator|.
name|activemq
operator|.
name|transport
operator|.
name|http
operator|.
name|BlockingQueueTransport
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|After
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

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|Before
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

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|logging
operator|.
name|Logger
import|;
end_import

begin_comment
comment|/**  * This test demonstrates that HttpTunnelServlet leaks BlockingQueueTransport  * objects whenever a network bridge gets created and closed over HTTP.  *<p>  *<b>NOTE:</b> This test requires a modified version of  * BlockingQueueTransport; the modification is for the purpose of detecting when  * the object is removed from memory.  */
end_comment

begin_class
specifier|public
class|class
name|BlockingQueueTransportLeakTest
block|{
specifier|private
specifier|static
specifier|final
name|long
name|INACTIVITY_TIMEOUT
init|=
literal|5000
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|Logger
name|LOG
init|=
name|Logger
operator|.
name|getLogger
argument_list|(
name|BlockingQueueTransportLeakTest
operator|.
name|class
operator|.
name|getName
argument_list|()
argument_list|)
decl_stmt|;
comment|// Change this URL to be an unused port. The inactivity timeout is required
comment|// per AMQ-3016.
specifier|private
specifier|static
specifier|final
name|String
name|REMOTE_BROKER_HTTP_URL
init|=
literal|"http://localhost:50000?transport.useInactivityMonitor=true&transport.initialDelayTime=0&transport.readCheckTime="
operator|+
name|INACTIVITY_TIMEOUT
decl_stmt|;
specifier|private
name|BrokerService
name|localBroker
init|=
operator|new
name|BrokerService
argument_list|()
decl_stmt|;
specifier|private
name|BrokerService
name|remoteBroker
init|=
operator|new
name|BrokerService
argument_list|()
decl_stmt|;
annotation|@
name|Before
specifier|public
name|void
name|init
parameter_list|()
throws|throws
name|Exception
block|{
name|localBroker
operator|.
name|setBrokerName
argument_list|(
literal|"localBroker"
argument_list|)
expr_stmt|;
name|localBroker
operator|.
name|setPersistent
argument_list|(
literal|false
argument_list|)
expr_stmt|;
name|localBroker
operator|.
name|setUseJmx
argument_list|(
literal|false
argument_list|)
expr_stmt|;
name|localBroker
operator|.
name|setSchedulerSupport
argument_list|(
literal|false
argument_list|)
expr_stmt|;
name|remoteBroker
operator|.
name|setBrokerName
argument_list|(
literal|"remoteBroker"
argument_list|)
expr_stmt|;
name|remoteBroker
operator|.
name|setPersistent
argument_list|(
literal|false
argument_list|)
expr_stmt|;
name|remoteBroker
operator|.
name|setUseJmx
argument_list|(
literal|false
argument_list|)
expr_stmt|;
name|remoteBroker
operator|.
name|addConnector
argument_list|(
name|REMOTE_BROKER_HTTP_URL
argument_list|)
expr_stmt|;
name|remoteBroker
operator|.
name|setSchedulerSupport
argument_list|(
literal|false
argument_list|)
expr_stmt|;
block|}
annotation|@
name|After
specifier|public
name|void
name|cleanup
parameter_list|()
throws|throws
name|Exception
block|{
try|try
block|{
name|localBroker
operator|.
name|stop
argument_list|()
expr_stmt|;
block|}
finally|finally
block|{
name|remoteBroker
operator|.
name|stop
argument_list|()
expr_stmt|;
block|}
block|}
comment|/** 	 * This test involves a local broker which establishes a network bridge to a 	 * remote broker using the HTTP protocol. The local broker stops and the 	 * remote broker cleans up the bridge connection. 	 *<p> 	 * This test demonstrates how the BlockingQueueTransport, which is created 	 * by HttpTunnelServlet for each bridge, is held in memory indefinitely. 	 */
annotation|@
name|Test
specifier|public
name|void
name|httpTest
parameter_list|()
throws|throws
name|Exception
block|{
specifier|final
name|long
name|BRIDGE_TIMEOUT
init|=
literal|10000
decl_stmt|;
specifier|final
name|long
name|GC_TIMEOUT
init|=
literal|30000
decl_stmt|;
comment|// Add a network connector to the local broker that will create a bridge
comment|// to the remote broker.
name|DiscoveryNetworkConnector
name|dnc
init|=
operator|new
name|DiscoveryNetworkConnector
argument_list|()
decl_stmt|;
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
name|REMOTE_BROKER_HTTP_URL
argument_list|)
expr_stmt|;
name|dnc
operator|.
name|setDiscoveryAgent
argument_list|(
name|da
argument_list|)
expr_stmt|;
name|localBroker
operator|.
name|addNetworkConnector
argument_list|(
name|dnc
argument_list|)
expr_stmt|;
comment|// Add an interceptor to the remote broker that signals when the bridge
comment|// connection has been added and removed.
name|BrokerPlugin
index|[]
name|plugins
init|=
operator|new
name|BrokerPlugin
index|[
literal|1
index|]
decl_stmt|;
name|plugins
index|[
literal|0
index|]
operator|=
operator|new
name|BrokerPlugin
argument_list|()
block|{
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
name|super
operator|.
name|addConnection
argument_list|(
name|context
argument_list|,
name|info
argument_list|)
expr_stmt|;
synchronized|synchronized
init|(
name|remoteBroker
init|)
block|{
name|remoteBroker
operator|.
name|notifyAll
argument_list|()
expr_stmt|;
block|}
block|}
annotation|@
name|Override
specifier|public
name|void
name|removeConnection
parameter_list|(
name|ConnectionContext
name|context
parameter_list|,
name|ConnectionInfo
name|info
parameter_list|,
name|Throwable
name|error
parameter_list|)
throws|throws
name|Exception
block|{
name|super
operator|.
name|removeConnection
argument_list|(
name|context
argument_list|,
name|info
argument_list|,
name|error
argument_list|)
expr_stmt|;
synchronized|synchronized
init|(
name|remoteBroker
init|)
block|{
name|remoteBroker
operator|.
name|notifyAll
argument_list|()
expr_stmt|;
block|}
block|}
block|}
return|;
block|}
block|}
expr_stmt|;
name|remoteBroker
operator|.
name|setPlugins
argument_list|(
name|plugins
argument_list|)
expr_stmt|;
comment|// Start the remote broker so that it available for the local broker to
comment|// connect to.
name|remoteBroker
operator|.
name|start
argument_list|()
expr_stmt|;
comment|// Start and stop the local broker. Synchronization is used to ensure
comment|// that the bridge is created before the local broker stops,
comment|// and that the test waits for the remote broker to remove the bridge.
synchronized|synchronized
init|(
name|remoteBroker
init|)
block|{
name|localBroker
operator|.
name|start
argument_list|()
expr_stmt|;
name|remoteBroker
operator|.
name|wait
argument_list|(
name|BRIDGE_TIMEOUT
argument_list|)
expr_stmt|;
comment|// Verify that the remote bridge connection has been created by the
comment|// remote broker.
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|remoteBroker
operator|.
name|getRegionBroker
argument_list|()
operator|.
name|getClients
argument_list|()
operator|.
name|length
argument_list|)
expr_stmt|;
name|localBroker
operator|.
name|stop
argument_list|()
expr_stmt|;
name|remoteBroker
operator|.
name|wait
argument_list|(
name|BRIDGE_TIMEOUT
argument_list|)
expr_stmt|;
comment|// Verify that the remote bridge connection has been closed by the
comment|// remote broker.
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|remoteBroker
operator|.
name|getRegionBroker
argument_list|()
operator|.
name|getClients
argument_list|()
operator|.
name|length
argument_list|)
expr_stmt|;
block|}
comment|// Initialize the countdown latch with the expected number of remote
comment|// bridge connections that should be garbage collected.
name|BlockingQueueTransport
operator|.
name|finalizeLatch
operator|=
operator|new
name|CountDownLatch
argument_list|(
literal|1
argument_list|)
expr_stmt|;
comment|// Run the GC and verify that the remote bridge connections are no
comment|// longer in memory. Some GC's are slow to respond, so give a second
comment|// prod if necessary.
comment|// This assertion fails with finalizeLatch.getCount() returning 1.
name|LOG
operator|.
name|info
argument_list|(
literal|"Triggering first GC..."
argument_list|)
expr_stmt|;
name|System
operator|.
name|gc
argument_list|()
expr_stmt|;
name|BlockingQueueTransport
operator|.
name|finalizeLatch
operator|.
name|await
argument_list|(
name|GC_TIMEOUT
argument_list|,
name|TimeUnit
operator|.
name|MILLISECONDS
argument_list|)
expr_stmt|;
if|if
condition|(
name|BlockingQueueTransport
operator|.
name|finalizeLatch
operator|.
name|getCount
argument_list|()
operator|!=
literal|0
condition|)
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"Triggering second GC..."
argument_list|)
expr_stmt|;
name|System
operator|.
name|gc
argument_list|()
expr_stmt|;
name|BlockingQueueTransport
operator|.
name|finalizeLatch
operator|.
name|await
argument_list|(
name|GC_TIMEOUT
argument_list|,
name|TimeUnit
operator|.
name|MILLISECONDS
argument_list|)
expr_stmt|;
block|}
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|BlockingQueueTransport
operator|.
name|finalizeLatch
operator|.
name|getCount
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

