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
name|broker
operator|.
name|ft
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
name|concurrent
operator|.
name|Executors
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
name|atomic
operator|.
name|AtomicBoolean
import|;
end_import

begin_import
import|import
name|junit
operator|.
name|framework
operator|.
name|TestCase
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
name|BrokerPluginSupport
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
name|commons
operator|.
name|logging
operator|.
name|Log
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|commons
operator|.
name|logging
operator|.
name|LogFactory
import|;
end_import

begin_class
specifier|public
class|class
name|MasterSlaveSlaveDieTest
extends|extends
name|TestCase
block|{
specifier|private
specifier|static
specifier|final
name|Log
name|LOG
init|=
name|LogFactory
operator|.
name|getLog
argument_list|(
name|MasterSlaveSlaveDieTest
operator|.
name|class
argument_list|)
decl_stmt|;
specifier|private
specifier|final
name|AtomicBoolean
name|pluginStopped
init|=
operator|new
name|AtomicBoolean
argument_list|(
literal|false
argument_list|)
decl_stmt|;
class|class
name|Plugin
extends|extends
name|BrokerPluginSupport
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
name|LOG
operator|.
name|info
argument_list|(
literal|"plugin start"
argument_list|)
expr_stmt|;
name|super
operator|.
name|start
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
name|LOG
operator|.
name|info
argument_list|(
literal|"plugin stop"
argument_list|)
expr_stmt|;
name|pluginStopped
operator|.
name|set
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|super
operator|.
name|stop
argument_list|()
expr_stmt|;
block|}
block|}
specifier|public
name|void
name|testSlaveDieMasterStays
parameter_list|()
throws|throws
name|Exception
block|{
specifier|final
name|BrokerService
name|master
init|=
operator|new
name|BrokerService
argument_list|()
decl_stmt|;
name|master
operator|.
name|setBrokerName
argument_list|(
literal|"master"
argument_list|)
expr_stmt|;
name|master
operator|.
name|setPersistent
argument_list|(
literal|false
argument_list|)
expr_stmt|;
comment|// The wireformat negotiation timeout (defaults to same as
comment|// MaxInactivityDurationInitalDelay) needs to be a bit longer
comment|// on slow running machines - set it to 90 seconds.
name|master
operator|.
name|addConnector
argument_list|(
literal|"tcp://localhost:0?wireFormat.maxInactivityDurationInitalDelay=90000"
argument_list|)
expr_stmt|;
name|master
operator|.
name|setWaitForSlave
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|master
operator|.
name|setPlugins
argument_list|(
operator|new
name|BrokerPlugin
index|[]
block|{
operator|new
name|Plugin
argument_list|()
block|}
argument_list|)
expr_stmt|;
specifier|final
name|BrokerService
name|slave
init|=
operator|new
name|BrokerService
argument_list|()
decl_stmt|;
name|slave
operator|.
name|setBrokerName
argument_list|(
literal|"slave"
argument_list|)
expr_stmt|;
name|slave
operator|.
name|setPersistent
argument_list|(
literal|false
argument_list|)
expr_stmt|;
name|URI
name|masterUri
init|=
name|master
operator|.
name|getTransportConnectors
argument_list|()
operator|.
name|get
argument_list|(
literal|0
argument_list|)
operator|.
name|getConnectUri
argument_list|()
decl_stmt|;
comment|//SocketProxy masterProxy = new SocketProxy(masterUri);
name|slave
operator|.
name|setMasterConnectorURI
argument_list|(
name|masterUri
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|slave
operator|.
name|setUseJmx
argument_list|(
literal|false
argument_list|)
expr_stmt|;
name|slave
operator|.
name|getManagementContext
argument_list|()
operator|.
name|setCreateConnector
argument_list|(
literal|false
argument_list|)
expr_stmt|;
name|Executors
operator|.
name|newSingleThreadExecutor
argument_list|()
operator|.
name|execute
argument_list|(
operator|new
name|Runnable
argument_list|()
block|{
specifier|public
name|void
name|run
parameter_list|()
block|{
try|try
block|{
name|master
operator|.
name|start
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
name|LOG
operator|.
name|warn
argument_list|(
literal|"Exception starting master: "
operator|+
name|e
argument_list|)
expr_stmt|;
name|e
operator|.
name|printStackTrace
argument_list|()
expr_stmt|;
block|}
block|}
block|}
argument_list|)
expr_stmt|;
name|slave
operator|.
name|start
argument_list|()
expr_stmt|;
name|slave
operator|.
name|waitUntilStarted
argument_list|()
expr_stmt|;
name|master
operator|.
name|waitUntilStarted
argument_list|()
expr_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"killing slave.."
argument_list|)
expr_stmt|;
name|slave
operator|.
name|stop
argument_list|()
expr_stmt|;
name|slave
operator|.
name|waitUntilStopped
argument_list|()
expr_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"checking master still alive"
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
literal|"master is still alive"
argument_list|,
name|master
operator|.
name|isStarted
argument_list|()
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
literal|"plugin was not yet stopped"
argument_list|,
name|pluginStopped
operator|.
name|get
argument_list|()
argument_list|)
expr_stmt|;
name|master
operator|.
name|stop
argument_list|()
expr_stmt|;
name|master
operator|.
name|waitUntilStopped
argument_list|()
expr_stmt|;
block|}
block|}
end_class

end_unit

