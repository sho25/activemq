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
name|partition
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
name|leveldb
operator|.
name|replicated
operator|.
name|groups
operator|.
name|ZKClient
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
name|partition
operator|.
name|dto
operator|.
name|Partitioning
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|zookeeper
operator|.
name|WatchedEvent
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|zookeeper
operator|.
name|Watcher
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|zookeeper
operator|.
name|data
operator|.
name|Stat
import|;
end_import

begin_import
import|import
name|org
operator|.
name|linkedin
operator|.
name|util
operator|.
name|clock
operator|.
name|Timespan
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

begin_comment
comment|/**  */
end_comment

begin_class
specifier|public
class|class
name|ZooKeeperPartitionBroker
extends|extends
name|PartitionBroker
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
name|ZooKeeperPartitionBroker
operator|.
name|class
argument_list|)
decl_stmt|;
specifier|protected
specifier|volatile
name|ZKClient
name|zk_client
init|=
literal|null
decl_stmt|;
specifier|protected
specifier|volatile
name|Partitioning
name|config
decl_stmt|;
specifier|protected
specifier|final
name|CountDownLatch
name|configAcquired
init|=
operator|new
name|CountDownLatch
argument_list|(
literal|1
argument_list|)
decl_stmt|;
specifier|public
name|ZooKeeperPartitionBroker
parameter_list|(
name|Broker
name|broker
parameter_list|,
name|ZooKeeperPartitionBrokerPlugin
name|plugin
parameter_list|)
block|{
name|super
argument_list|(
name|broker
argument_list|,
name|plugin
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|start
parameter_list|()
throws|throws
name|Exception
block|{
name|super
operator|.
name|start
argument_list|()
expr_stmt|;
comment|// Lets block a bit until we get our config.. Otherwise just keep
comment|// on going.. not a big deal if we get our config later.  Perhaps
comment|// ZK service is not having a good day.
name|configAcquired
operator|.
name|await
argument_list|(
literal|5
argument_list|,
name|TimeUnit
operator|.
name|SECONDS
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|protected
name|void
name|onMonitorStop
parameter_list|()
block|{
name|zkDisconnect
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Override
specifier|protected
name|Partitioning
name|getConfig
parameter_list|()
block|{
return|return
name|config
return|;
block|}
specifier|protected
name|ZooKeeperPartitionBrokerPlugin
name|plugin
parameter_list|()
block|{
return|return
operator|(
name|ZooKeeperPartitionBrokerPlugin
operator|)
name|plugin
return|;
block|}
specifier|protected
name|void
name|zkConnect
parameter_list|()
throws|throws
name|Exception
block|{
name|zk_client
operator|=
operator|new
name|ZKClient
argument_list|(
name|plugin
argument_list|()
operator|.
name|getZkAddress
argument_list|()
argument_list|,
name|Timespan
operator|.
name|parse
argument_list|(
name|plugin
argument_list|()
operator|.
name|getZkSessionTmeout
argument_list|()
argument_list|)
argument_list|,
literal|null
argument_list|)
expr_stmt|;
if|if
condition|(
name|plugin
argument_list|()
operator|.
name|getZkPassword
argument_list|()
operator|!=
literal|null
condition|)
block|{
name|zk_client
operator|.
name|setPassword
argument_list|(
name|plugin
argument_list|()
operator|.
name|getZkPassword
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|zk_client
operator|.
name|start
argument_list|()
expr_stmt|;
name|zk_client
operator|.
name|waitForConnected
argument_list|(
name|Timespan
operator|.
name|parse
argument_list|(
literal|"30s"
argument_list|)
argument_list|)
expr_stmt|;
block|}
specifier|protected
name|void
name|zkDisconnect
parameter_list|()
block|{
if|if
condition|(
name|zk_client
operator|!=
literal|null
condition|)
block|{
name|zk_client
operator|.
name|close
argument_list|()
expr_stmt|;
name|zk_client
operator|=
literal|null
expr_stmt|;
block|}
block|}
specifier|protected
name|void
name|reloadConfiguration
parameter_list|()
throws|throws
name|Exception
block|{
if|if
condition|(
name|zk_client
operator|==
literal|null
condition|)
block|{
name|LOG
operator|.
name|debug
argument_list|(
literal|"Connecting to ZooKeeper"
argument_list|)
expr_stmt|;
try|try
block|{
name|zkConnect
argument_list|()
expr_stmt|;
name|LOG
operator|.
name|debug
argument_list|(
literal|"Connected to ZooKeeper"
argument_list|)
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
name|debug
argument_list|(
literal|"Connection to ZooKeeper failed: "
operator|+
name|e
argument_list|)
expr_stmt|;
name|zkDisconnect
argument_list|()
expr_stmt|;
throw|throw
name|e
throw|;
block|}
block|}
name|byte
index|[]
name|data
init|=
literal|null
decl_stmt|;
try|try
block|{
name|Stat
name|stat
init|=
operator|new
name|Stat
argument_list|()
decl_stmt|;
name|data
operator|=
name|zk_client
operator|.
name|getData
argument_list|(
name|plugin
argument_list|()
operator|.
name|getZkPath
argument_list|()
argument_list|,
operator|new
name|Watcher
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|void
name|process
parameter_list|(
name|WatchedEvent
name|watchedEvent
parameter_list|)
block|{
try|try
block|{
name|reloadConfiguration
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{                     }
name|monitorWakeup
argument_list|()
expr_stmt|;
block|}
block|}
argument_list|,
name|stat
argument_list|)
expr_stmt|;
name|configAcquired
operator|.
name|countDown
argument_list|()
expr_stmt|;
name|reloadConfigOnPoll
operator|=
literal|false
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
literal|"Could load partitioning configuration: "
operator|+
name|e
argument_list|,
name|e
argument_list|)
expr_stmt|;
name|reloadConfigOnPoll
operator|=
literal|true
expr_stmt|;
block|}
try|try
block|{
name|config
operator|=
name|Partitioning
operator|.
name|MAPPER
operator|.
name|readValue
argument_list|(
name|data
argument_list|,
name|Partitioning
operator|.
name|class
argument_list|)
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
literal|"Invalid partitioning configuration: "
operator|+
name|e
argument_list|,
name|e
argument_list|)
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit
