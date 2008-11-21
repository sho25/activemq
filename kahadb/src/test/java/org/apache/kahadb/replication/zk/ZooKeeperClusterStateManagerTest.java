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
name|kahadb
operator|.
name|replication
operator|.
name|zk
package|;
end_package

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|BufferedReader
import|;
end_import

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
name|io
operator|.
name|IOException
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|InputStreamReader
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|OutputStream
import|;
end_import

begin_import
import|import
name|java
operator|.
name|net
operator|.
name|Socket
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
name|concurrent
operator|.
name|LinkedBlockingQueue
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
name|util
operator|.
name|Callback
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|kahadb
operator|.
name|replication
operator|.
name|ClusterListener
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|kahadb
operator|.
name|replication
operator|.
name|ClusterState
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|kahadb
operator|.
name|replication
operator|.
name|pb
operator|.
name|PBClusterNodeStatus
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|kahadb
operator|.
name|replication
operator|.
name|pb
operator|.
name|PBJournalLocation
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|kahadb
operator|.
name|replication
operator|.
name|pb
operator|.
name|PBClusterNodeStatus
operator|.
name|State
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
name|KeeperException
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
name|ZooKeeper
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
name|apache
operator|.
name|zookeeper
operator|.
name|server
operator|.
name|NIOServerCnxn
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
name|server
operator|.
name|ServerStats
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
name|server
operator|.
name|ZooKeeperServer
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
name|server
operator|.
name|NIOServerCnxn
operator|.
name|Factory
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
name|server
operator|.
name|persistence
operator|.
name|FileTxnLog
import|;
end_import

begin_class
specifier|public
class|class
name|ZooKeeperClusterStateManagerTest
extends|extends
name|TestCase
block|{
specifier|private
specifier|static
specifier|final
name|int
name|PORT
init|=
literal|2181
decl_stmt|;
specifier|private
name|ZooKeeperClusterStateManager
name|zkcsm1
decl_stmt|;
specifier|private
name|ZooKeeper
name|zk
decl_stmt|;
specifier|private
name|Factory
name|serverFactory
decl_stmt|;
annotation|@
name|Override
specifier|protected
name|void
name|setUp
parameter_list|()
throws|throws
name|Exception
block|{
name|ServerStats
operator|.
name|registerAsConcrete
argument_list|()
expr_stmt|;
name|File
name|tmpDir
init|=
operator|new
name|File
argument_list|(
literal|"target/test-data/zookeeper"
argument_list|)
decl_stmt|;
name|tmpDir
operator|.
name|mkdirs
argument_list|()
expr_stmt|;
comment|// Reduces startup time..
name|System
operator|.
name|setProperty
argument_list|(
literal|"zookeeper.preAllocSize"
argument_list|,
literal|"100"
argument_list|)
expr_stmt|;
name|FileTxnLog
operator|.
name|setPreallocSize
argument_list|(
literal|100
argument_list|)
expr_stmt|;
name|ZooKeeperServer
name|zs
init|=
operator|new
name|ZooKeeperServer
argument_list|(
name|tmpDir
argument_list|,
name|tmpDir
argument_list|,
literal|3000
argument_list|)
decl_stmt|;
name|serverFactory
operator|=
operator|new
name|NIOServerCnxn
operator|.
name|Factory
argument_list|(
name|PORT
argument_list|)
expr_stmt|;
name|serverFactory
operator|.
name|startup
argument_list|(
name|zs
argument_list|)
expr_stmt|;
name|zkcsm1
operator|=
operator|new
name|ZooKeeperClusterStateManager
argument_list|()
expr_stmt|;
name|zk
operator|=
name|zkcsm1
operator|.
name|createZooKeeperConnection
argument_list|()
expr_stmt|;
comment|// Cleanup after previous run...
name|zkRecusiveDelete
argument_list|(
name|zkcsm1
operator|.
name|getPath
argument_list|()
argument_list|)
expr_stmt|;
block|}
specifier|private
name|void
name|zkRecusiveDelete
parameter_list|(
name|String
name|path
parameter_list|)
throws|throws
name|KeeperException
throws|,
name|InterruptedException
block|{
name|Stat
name|stat
init|=
name|zk
operator|.
name|exists
argument_list|(
name|path
argument_list|,
literal|false
argument_list|)
decl_stmt|;
if|if
condition|(
name|stat
operator|!=
literal|null
condition|)
block|{
if|if
condition|(
name|stat
operator|.
name|getNumChildren
argument_list|()
operator|>
literal|0
condition|)
block|{
name|List
argument_list|<
name|String
argument_list|>
name|children
init|=
name|zk
operator|.
name|getChildren
argument_list|(
name|path
argument_list|,
literal|false
argument_list|)
decl_stmt|;
for|for
control|(
name|String
name|node
range|:
name|children
control|)
block|{
name|zkRecusiveDelete
argument_list|(
name|path
operator|+
literal|"/"
operator|+
name|node
argument_list|)
expr_stmt|;
block|}
block|}
name|zk
operator|.
name|delete
argument_list|(
name|path
argument_list|,
name|stat
operator|.
name|getVersion
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Override
specifier|protected
name|void
name|tearDown
parameter_list|()
throws|throws
name|Exception
block|{
name|zk
operator|.
name|close
argument_list|()
expr_stmt|;
name|serverFactory
operator|.
name|shutdown
argument_list|()
expr_stmt|;
name|ServerStats
operator|.
name|unregister
argument_list|()
expr_stmt|;
block|}
specifier|public
name|void
name|testTwoNodesGoingOnline
parameter_list|()
throws|throws
name|Exception
block|{
specifier|final
name|LinkedBlockingQueue
argument_list|<
name|ClusterState
argument_list|>
name|stateEvents1
init|=
operator|new
name|LinkedBlockingQueue
argument_list|<
name|ClusterState
argument_list|>
argument_list|()
decl_stmt|;
specifier|final
name|LinkedBlockingQueue
argument_list|<
name|ClusterState
argument_list|>
name|stateEvents2
init|=
operator|new
name|LinkedBlockingQueue
argument_list|<
name|ClusterState
argument_list|>
argument_list|()
decl_stmt|;
name|zkcsm1
operator|.
name|addListener
argument_list|(
operator|new
name|ClusterListener
argument_list|()
block|{
specifier|public
name|void
name|onClusterChange
parameter_list|(
name|ClusterState
name|config
parameter_list|)
block|{
name|stateEvents1
operator|.
name|add
argument_list|(
name|config
argument_list|)
expr_stmt|;
block|}
block|}
argument_list|)
expr_stmt|;
name|zkcsm1
operator|.
name|start
argument_list|()
expr_stmt|;
name|zkcsm1
operator|.
name|addMember
argument_list|(
literal|"kdbr://localhost:60001"
argument_list|)
expr_stmt|;
specifier|final
name|ZooKeeperClusterStateManager
name|zkcsm2
init|=
operator|new
name|ZooKeeperClusterStateManager
argument_list|()
decl_stmt|;
name|zkcsm2
operator|.
name|addListener
argument_list|(
operator|new
name|ClusterListener
argument_list|()
block|{
specifier|public
name|void
name|onClusterChange
parameter_list|(
name|ClusterState
name|config
parameter_list|)
block|{
name|stateEvents2
operator|.
name|add
argument_list|(
name|config
argument_list|)
expr_stmt|;
block|}
block|}
argument_list|)
expr_stmt|;
name|zkcsm2
operator|.
name|start
argument_list|()
expr_stmt|;
name|zkcsm2
operator|.
name|addMember
argument_list|(
literal|"kdbr://localhost:60002"
argument_list|)
expr_stmt|;
comment|// Drain the events..
while|while
condition|(
name|stateEvents1
operator|.
name|poll
argument_list|(
literal|100
argument_list|,
name|TimeUnit
operator|.
name|MILLISECONDS
argument_list|)
operator|!=
literal|null
condition|)
block|{         }
while|while
condition|(
name|stateEvents2
operator|.
name|poll
argument_list|(
literal|100
argument_list|,
name|TimeUnit
operator|.
name|MILLISECONDS
argument_list|)
operator|!=
literal|null
condition|)
block|{         }
comment|// Bring node 1 online
specifier|final
name|PBClusterNodeStatus
name|status1
init|=
operator|new
name|PBClusterNodeStatus
argument_list|()
decl_stmt|;
name|status1
operator|.
name|setConnectUri
argument_list|(
literal|"kdbr://localhost:60001"
argument_list|)
expr_stmt|;
name|status1
operator|.
name|setLastUpdate
argument_list|(
operator|new
name|PBJournalLocation
argument_list|()
operator|.
name|setFileId
argument_list|(
literal|1
argument_list|)
operator|.
name|setOffset
argument_list|(
literal|50
argument_list|)
argument_list|)
expr_stmt|;
name|status1
operator|.
name|setState
argument_list|(
name|State
operator|.
name|SLAVE_UNCONNECTED
argument_list|)
expr_stmt|;
name|executeAsync
argument_list|(
operator|new
name|Callback
argument_list|()
block|{
specifier|public
name|void
name|execute
parameter_list|()
throws|throws
name|Exception
block|{
name|zkcsm1
operator|.
name|setMemberStatus
argument_list|(
name|status1
argument_list|)
expr_stmt|;
block|}
block|}
argument_list|)
expr_stmt|;
comment|// Bring node 2 online
specifier|final
name|PBClusterNodeStatus
name|status2
init|=
operator|new
name|PBClusterNodeStatus
argument_list|()
decl_stmt|;
name|status2
operator|.
name|setConnectUri
argument_list|(
literal|"kdbr://localhost:60002"
argument_list|)
expr_stmt|;
name|status2
operator|.
name|setLastUpdate
argument_list|(
operator|new
name|PBJournalLocation
argument_list|()
operator|.
name|setFileId
argument_list|(
literal|2
argument_list|)
operator|.
name|setOffset
argument_list|(
literal|20
argument_list|)
argument_list|)
expr_stmt|;
name|status2
operator|.
name|setState
argument_list|(
name|State
operator|.
name|SLAVE_UNCONNECTED
argument_list|)
expr_stmt|;
name|executeAsync
argument_list|(
operator|new
name|Callback
argument_list|()
block|{
specifier|public
name|void
name|execute
parameter_list|()
throws|throws
name|Exception
block|{
name|Thread
operator|.
name|sleep
argument_list|(
literal|1000
argument_list|)
expr_stmt|;
name|zkcsm2
operator|.
name|setMemberStatus
argument_list|(
name|status2
argument_list|)
expr_stmt|;
block|}
block|}
argument_list|)
expr_stmt|;
name|ClusterState
name|state
init|=
name|stateEvents1
operator|.
name|poll
argument_list|(
literal|10
argument_list|,
name|TimeUnit
operator|.
name|SECONDS
argument_list|)
decl_stmt|;
name|assertNotNull
argument_list|(
name|state
argument_list|)
expr_stmt|;
name|assertNotNull
argument_list|(
name|state
operator|.
name|getMaster
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"kdbr://localhost:60002"
argument_list|,
name|state
operator|.
name|getMaster
argument_list|()
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|state
operator|.
name|getSlaves
argument_list|()
operator|.
name|size
argument_list|()
operator|==
literal|1
argument_list|)
expr_stmt|;
name|state
operator|=
name|stateEvents2
operator|.
name|poll
argument_list|(
literal|1
argument_list|,
name|TimeUnit
operator|.
name|SECONDS
argument_list|)
expr_stmt|;
name|assertNotNull
argument_list|(
name|state
argument_list|)
expr_stmt|;
name|assertNotNull
argument_list|(
name|state
operator|.
name|getMaster
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"kdbr://localhost:60002"
argument_list|,
name|state
operator|.
name|getMaster
argument_list|()
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|state
operator|.
name|getSlaves
argument_list|()
operator|.
name|size
argument_list|()
operator|==
literal|1
argument_list|)
expr_stmt|;
name|zkcsm2
operator|.
name|stop
argument_list|()
expr_stmt|;
name|zkcsm1
operator|.
name|stop
argument_list|()
expr_stmt|;
block|}
specifier|private
name|void
name|executeAsync
parameter_list|(
specifier|final
name|Callback
name|callback
parameter_list|)
block|{
operator|new
name|Thread
argument_list|(
literal|"Async Test Task"
argument_list|)
block|{
annotation|@
name|Override
specifier|public
name|void
name|run
parameter_list|()
block|{
try|try
block|{
name|callback
operator|.
name|execute
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
name|e
operator|.
name|printStackTrace
argument_list|()
expr_stmt|;
block|}
block|}
block|}
operator|.
name|start
argument_list|()
expr_stmt|;
block|}
specifier|public
name|void
name|testOneNodeGoingOnline
parameter_list|()
throws|throws
name|Exception
block|{
specifier|final
name|LinkedBlockingQueue
argument_list|<
name|ClusterState
argument_list|>
name|stateEvents1
init|=
operator|new
name|LinkedBlockingQueue
argument_list|<
name|ClusterState
argument_list|>
argument_list|()
decl_stmt|;
name|zkcsm1
operator|.
name|addListener
argument_list|(
operator|new
name|ClusterListener
argument_list|()
block|{
specifier|public
name|void
name|onClusterChange
parameter_list|(
name|ClusterState
name|config
parameter_list|)
block|{
name|stateEvents1
operator|.
name|add
argument_list|(
name|config
argument_list|)
expr_stmt|;
block|}
block|}
argument_list|)
expr_stmt|;
name|zkcsm1
operator|.
name|start
argument_list|()
expr_stmt|;
comment|// Drain the events..
while|while
condition|(
name|stateEvents1
operator|.
name|poll
argument_list|(
literal|100
argument_list|,
name|TimeUnit
operator|.
name|MILLISECONDS
argument_list|)
operator|!=
literal|null
condition|)
block|{         }
comment|// Let node1 join the cluster.
name|zkcsm1
operator|.
name|addMember
argument_list|(
literal|"kdbr://localhost:60001"
argument_list|)
expr_stmt|;
name|ClusterState
name|state
init|=
name|stateEvents1
operator|.
name|poll
argument_list|(
literal|1
argument_list|,
name|TimeUnit
operator|.
name|SECONDS
argument_list|)
decl_stmt|;
name|assertNotNull
argument_list|(
name|state
argument_list|)
expr_stmt|;
name|assertNull
argument_list|(
name|state
operator|.
name|getMaster
argument_list|()
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|state
operator|.
name|getSlaves
argument_list|()
operator|.
name|size
argument_list|()
operator|==
literal|1
argument_list|)
expr_stmt|;
comment|// Let the cluster know that node1 is online..
name|PBClusterNodeStatus
name|status
init|=
operator|new
name|PBClusterNodeStatus
argument_list|()
decl_stmt|;
name|status
operator|.
name|setConnectUri
argument_list|(
literal|"kdbr://localhost:60001"
argument_list|)
expr_stmt|;
name|status
operator|.
name|setLastUpdate
argument_list|(
operator|new
name|PBJournalLocation
argument_list|()
operator|.
name|setFileId
argument_list|(
literal|0
argument_list|)
operator|.
name|setOffset
argument_list|(
literal|0
argument_list|)
argument_list|)
expr_stmt|;
name|status
operator|.
name|setState
argument_list|(
name|State
operator|.
name|SLAVE_UNCONNECTED
argument_list|)
expr_stmt|;
name|zkcsm1
operator|.
name|setMemberStatus
argument_list|(
name|status
argument_list|)
expr_stmt|;
name|state
operator|=
name|stateEvents1
operator|.
name|poll
argument_list|(
literal|10
argument_list|,
name|TimeUnit
operator|.
name|SECONDS
argument_list|)
expr_stmt|;
name|assertNotNull
argument_list|(
name|state
argument_list|)
expr_stmt|;
name|assertNotNull
argument_list|(
name|state
operator|.
name|getMaster
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"kdbr://localhost:60001"
argument_list|,
name|state
operator|.
name|getMaster
argument_list|()
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|state
operator|.
name|getSlaves
argument_list|()
operator|.
name|isEmpty
argument_list|()
argument_list|)
expr_stmt|;
name|zkcsm1
operator|.
name|stop
argument_list|()
expr_stmt|;
block|}
block|}
end_class

end_unit

