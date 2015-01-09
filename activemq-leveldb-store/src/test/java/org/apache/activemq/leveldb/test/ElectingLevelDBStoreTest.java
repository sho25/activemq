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
name|leveldb
operator|.
name|test
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
name|Service
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
name|ActiveMQQueue
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
name|CountDownFuture
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
name|LevelDBStore
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
name|ElectingLevelDBStore
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
name|store
operator|.
name|MessageStore
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
name|io
operator|.
name|FileUtils
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
name|Ignore
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
name|io
operator|.
name|File
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|ArrayList
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
name|concurrent
operator|.
name|TimeUnit
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|apache
operator|.
name|activemq
operator|.
name|leveldb
operator|.
name|test
operator|.
name|ReplicationTestSupport
operator|.
name|*
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
name|*
import|;
end_import

begin_comment
comment|/**  */
end_comment

begin_class
specifier|public
class|class
name|ElectingLevelDBStoreTest
extends|extends
name|ZooKeeperTestSupport
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
name|ElectingLevelDBStoreTest
operator|.
name|class
argument_list|)
decl_stmt|;
name|ArrayList
argument_list|<
name|ElectingLevelDBStore
argument_list|>
name|stores
init|=
operator|new
name|ArrayList
argument_list|<
name|ElectingLevelDBStore
argument_list|>
argument_list|()
decl_stmt|;
name|ElectingLevelDBStore
name|master
init|=
literal|null
decl_stmt|;
annotation|@
name|Ignore
argument_list|(
literal|"https://issues.apache.org/jira/browse/AMQ-5512"
argument_list|)
annotation|@
name|Test
argument_list|(
name|timeout
operator|=
literal|1000
operator|*
literal|60
operator|*
literal|10
argument_list|)
specifier|public
name|void
name|testElection
parameter_list|()
throws|throws
name|Exception
block|{
name|deleteDirectory
argument_list|(
literal|"leveldb-node1"
argument_list|)
expr_stmt|;
name|deleteDirectory
argument_list|(
literal|"leveldb-node2"
argument_list|)
expr_stmt|;
name|deleteDirectory
argument_list|(
literal|"leveldb-node3"
argument_list|)
expr_stmt|;
name|ArrayList
argument_list|<
name|CountDownFuture
argument_list|>
name|pending_starts
init|=
operator|new
name|ArrayList
argument_list|<
name|CountDownFuture
argument_list|>
argument_list|()
decl_stmt|;
for|for
control|(
name|String
name|dir
range|:
operator|new
name|String
index|[]
block|{
literal|"leveldb-node1"
block|,
literal|"leveldb-node2"
block|,
literal|"leveldb-node3"
block|}
control|)
block|{
name|ElectingLevelDBStore
name|store
init|=
name|createStoreNode
argument_list|()
decl_stmt|;
name|store
operator|.
name|setDirectory
argument_list|(
operator|new
name|File
argument_list|(
name|data_dir
argument_list|()
argument_list|,
name|dir
argument_list|)
argument_list|)
expr_stmt|;
name|stores
operator|.
name|add
argument_list|(
name|store
argument_list|)
expr_stmt|;
name|pending_starts
operator|.
name|add
argument_list|(
name|asyncStart
argument_list|(
name|store
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|// At least one of the stores should have started.
name|CountDownFuture
name|f
init|=
name|waitFor
argument_list|(
literal|30
operator|*
literal|1000
argument_list|,
name|pending_starts
operator|.
name|toArray
argument_list|(
operator|new
name|CountDownFuture
index|[
name|pending_starts
operator|.
name|size
argument_list|()
index|]
argument_list|)
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
name|f
operator|!=
literal|null
argument_list|)
expr_stmt|;
name|pending_starts
operator|.
name|remove
argument_list|(
name|f
argument_list|)
expr_stmt|;
comment|// The other stores should not start..
name|LOG
operator|.
name|info
argument_list|(
literal|"Making sure the other stores don't start"
argument_list|)
expr_stmt|;
name|Thread
operator|.
name|sleep
argument_list|(
literal|5000
argument_list|)
expr_stmt|;
for|for
control|(
name|CountDownFuture
name|start
range|:
name|pending_starts
control|)
block|{
name|assertFalse
argument_list|(
name|start
operator|.
name|completed
argument_list|()
argument_list|)
expr_stmt|;
block|}
comment|// Make sure only of the stores is reporting to be the master.
for|for
control|(
name|ElectingLevelDBStore
name|store
range|:
name|stores
control|)
block|{
if|if
condition|(
name|store
operator|.
name|isMaster
argument_list|()
condition|)
block|{
name|assertNull
argument_list|(
name|master
argument_list|)
expr_stmt|;
name|master
operator|=
name|store
expr_stmt|;
block|}
block|}
name|assertNotNull
argument_list|(
name|master
argument_list|)
expr_stmt|;
comment|// We can work out who the slaves are...
name|HashSet
argument_list|<
name|ElectingLevelDBStore
argument_list|>
name|slaves
init|=
operator|new
name|HashSet
argument_list|<
name|ElectingLevelDBStore
argument_list|>
argument_list|(
name|stores
argument_list|)
decl_stmt|;
name|slaves
operator|.
name|remove
argument_list|(
name|master
argument_list|)
expr_stmt|;
comment|// Start sending messages to the master.
name|ArrayList
argument_list|<
name|String
argument_list|>
name|expected_list
init|=
operator|new
name|ArrayList
argument_list|<
name|String
argument_list|>
argument_list|()
decl_stmt|;
name|MessageStore
name|ms
init|=
name|master
operator|.
name|createQueueMessageStore
argument_list|(
operator|new
name|ActiveMQQueue
argument_list|(
literal|"TEST"
argument_list|)
argument_list|)
decl_stmt|;
specifier|final
name|int
name|TOTAL
init|=
literal|500
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|TOTAL
condition|;
name|i
operator|++
control|)
block|{
if|if
condition|(
name|i
operator|%
operator|(
call|(
name|int
call|)
argument_list|(
name|TOTAL
operator|*
literal|0.10
argument_list|)
operator|)
operator|==
literal|0
condition|)
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|""
operator|+
operator|(
literal|100
operator|*
name|i
operator|/
name|TOTAL
operator|)
operator|+
literal|"% done"
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|i
operator|==
literal|250
condition|)
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"Checking master state"
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|expected_list
argument_list|,
name|getMessages
argument_list|(
name|ms
argument_list|)
argument_list|)
expr_stmt|;
comment|// mid way, lets kill the master..
name|LOG
operator|.
name|info
argument_list|(
literal|"Killing Master."
argument_list|)
expr_stmt|;
name|master
operator|.
name|stop
argument_list|()
expr_stmt|;
comment|// At least one of the remaining stores should complete starting.
name|LOG
operator|.
name|info
argument_list|(
literal|"Waiting for slave takeover..."
argument_list|)
expr_stmt|;
name|f
operator|=
name|waitFor
argument_list|(
literal|60
operator|*
literal|1000
argument_list|,
name|pending_starts
operator|.
name|toArray
argument_list|(
operator|new
name|CountDownFuture
index|[
name|pending_starts
operator|.
name|size
argument_list|()
index|]
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|f
operator|!=
literal|null
argument_list|)
expr_stmt|;
name|pending_starts
operator|.
name|remove
argument_list|(
name|f
argument_list|)
expr_stmt|;
comment|// Make sure one and only one of the slaves becomes the master..
name|master
operator|=
literal|null
expr_stmt|;
for|for
control|(
name|ElectingLevelDBStore
name|store
range|:
name|slaves
control|)
block|{
if|if
condition|(
name|store
operator|.
name|isMaster
argument_list|()
condition|)
block|{
name|assertNull
argument_list|(
name|master
argument_list|)
expr_stmt|;
name|master
operator|=
name|store
expr_stmt|;
block|}
block|}
name|assertNotNull
argument_list|(
name|master
argument_list|)
expr_stmt|;
name|slaves
operator|.
name|remove
argument_list|(
name|master
argument_list|)
expr_stmt|;
name|ms
operator|=
name|master
operator|.
name|createQueueMessageStore
argument_list|(
operator|new
name|ActiveMQQueue
argument_list|(
literal|"TEST"
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|String
name|msgid
init|=
literal|"m:"
operator|+
name|i
decl_stmt|;
name|addMessage
argument_list|(
name|ms
argument_list|,
name|msgid
argument_list|)
expr_stmt|;
name|expected_list
operator|.
name|add
argument_list|(
name|msgid
argument_list|)
expr_stmt|;
block|}
name|LOG
operator|.
name|info
argument_list|(
literal|"Checking master state"
argument_list|)
expr_stmt|;
name|ArrayList
argument_list|<
name|String
argument_list|>
name|messagesInStore
init|=
name|getMessages
argument_list|(
name|ms
argument_list|)
decl_stmt|;
name|int
name|index
init|=
literal|0
decl_stmt|;
for|for
control|(
name|String
name|id
range|:
name|expected_list
control|)
block|{
if|if
condition|(
operator|!
name|id
operator|.
name|equals
argument_list|(
name|messagesInStore
operator|.
name|get
argument_list|(
name|index
argument_list|)
argument_list|)
condition|)
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"Mismatch for expected:"
operator|+
name|id
operator|+
literal|", got:"
operator|+
name|messagesInStore
operator|.
name|get
argument_list|(
name|index
argument_list|)
argument_list|)
expr_stmt|;
break|break;
block|}
name|index
operator|++
expr_stmt|;
block|}
name|assertEquals
argument_list|(
name|expected_list
argument_list|,
name|messagesInStore
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
argument_list|(
name|timeout
operator|=
literal|1000
operator|*
literal|60
operator|*
literal|10
argument_list|)
specifier|public
name|void
name|testZooKeeperServerFailure
parameter_list|()
throws|throws
name|Exception
block|{
specifier|final
name|ArrayList
argument_list|<
name|ElectingLevelDBStore
argument_list|>
name|stores
init|=
operator|new
name|ArrayList
argument_list|<
name|ElectingLevelDBStore
argument_list|>
argument_list|()
decl_stmt|;
name|ArrayList
argument_list|<
name|CountDownFuture
argument_list|>
name|pending_starts
init|=
operator|new
name|ArrayList
argument_list|<
name|CountDownFuture
argument_list|>
argument_list|()
decl_stmt|;
for|for
control|(
name|String
name|dir
range|:
operator|new
name|String
index|[]
block|{
literal|"leveldb-node1"
block|,
literal|"leveldb-node2"
block|,
literal|"leveldb-node3"
block|}
control|)
block|{
name|ElectingLevelDBStore
name|store
init|=
name|createStoreNode
argument_list|()
decl_stmt|;
name|store
operator|.
name|setDirectory
argument_list|(
operator|new
name|File
argument_list|(
name|data_dir
argument_list|()
argument_list|,
name|dir
argument_list|)
argument_list|)
expr_stmt|;
name|stores
operator|.
name|add
argument_list|(
name|store
argument_list|)
expr_stmt|;
name|pending_starts
operator|.
name|add
argument_list|(
name|asyncStart
argument_list|(
name|store
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|// At least one of the stores should have started.
name|CountDownFuture
name|f
init|=
name|waitFor
argument_list|(
literal|30
operator|*
literal|1000
argument_list|,
name|pending_starts
operator|.
name|toArray
argument_list|(
operator|new
name|CountDownFuture
index|[
name|pending_starts
operator|.
name|size
argument_list|()
index|]
argument_list|)
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
name|f
operator|!=
literal|null
argument_list|)
expr_stmt|;
name|pending_starts
operator|.
name|remove
argument_list|(
name|f
argument_list|)
expr_stmt|;
comment|// The other stores should not start..
name|LOG
operator|.
name|info
argument_list|(
literal|"Making sure the other stores don't start"
argument_list|)
expr_stmt|;
name|Thread
operator|.
name|sleep
argument_list|(
literal|5000
argument_list|)
expr_stmt|;
for|for
control|(
name|CountDownFuture
name|start
range|:
name|pending_starts
control|)
block|{
name|assertFalse
argument_list|(
name|start
operator|.
name|completed
argument_list|()
argument_list|)
expr_stmt|;
block|}
comment|// Stop ZooKeeper..
name|LOG
operator|.
name|info
argument_list|(
literal|"SHUTTING DOWN ZooKeeper!"
argument_list|)
expr_stmt|;
name|connector
operator|.
name|shutdown
argument_list|()
expr_stmt|;
comment|// None of the store should be slaves...
name|within
argument_list|(
literal|30
argument_list|,
name|TimeUnit
operator|.
name|SECONDS
argument_list|,
operator|new
name|Task
argument_list|()
block|{
specifier|public
name|void
name|run
parameter_list|()
throws|throws
name|Exception
block|{
for|for
control|(
name|ElectingLevelDBStore
name|store
range|:
name|stores
control|)
block|{
name|assertFalse
argument_list|(
name|store
operator|.
name|isMaster
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
block|}
argument_list|)
expr_stmt|;
block|}
annotation|@
name|After
specifier|public
name|void
name|stop
parameter_list|()
throws|throws
name|Exception
block|{
if|if
condition|(
name|master
operator|!=
literal|null
condition|)
block|{
name|master
operator|.
name|stop
argument_list|()
expr_stmt|;
name|FileUtils
operator|.
name|deleteDirectory
argument_list|(
name|master
operator|.
name|directory
argument_list|()
argument_list|)
expr_stmt|;
block|}
for|for
control|(
name|ElectingLevelDBStore
name|store
range|:
name|stores
control|)
block|{
name|store
operator|.
name|stop
argument_list|()
expr_stmt|;
name|FileUtils
operator|.
name|deleteDirectory
argument_list|(
name|store
operator|.
name|directory
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|stores
operator|.
name|clear
argument_list|()
expr_stmt|;
block|}
specifier|private
name|CountDownFuture
name|asyncStart
parameter_list|(
specifier|final
name|Service
name|service
parameter_list|)
block|{
specifier|final
name|CountDownFuture
argument_list|<
name|Throwable
argument_list|>
name|f
init|=
operator|new
name|CountDownFuture
argument_list|<
name|Throwable
argument_list|>
argument_list|()
decl_stmt|;
name|LevelDBStore
operator|.
name|BLOCKING_EXECUTOR
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
name|service
operator|.
name|start
argument_list|()
expr_stmt|;
name|f
operator|.
name|set
argument_list|(
literal|null
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Throwable
name|e
parameter_list|)
block|{
name|e
operator|.
name|printStackTrace
argument_list|()
expr_stmt|;
name|f
operator|.
name|set
argument_list|(
name|e
argument_list|)
expr_stmt|;
block|}
block|}
block|}
argument_list|)
expr_stmt|;
return|return
name|f
return|;
block|}
specifier|private
name|CountDownFuture
name|asyncStop
parameter_list|(
specifier|final
name|Service
name|service
parameter_list|)
block|{
specifier|final
name|CountDownFuture
argument_list|<
name|Throwable
argument_list|>
name|f
init|=
operator|new
name|CountDownFuture
argument_list|<
name|Throwable
argument_list|>
argument_list|()
decl_stmt|;
name|LevelDBStore
operator|.
name|BLOCKING_EXECUTOR
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
name|service
operator|.
name|stop
argument_list|()
expr_stmt|;
name|f
operator|.
name|set
argument_list|(
literal|null
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Throwable
name|e
parameter_list|)
block|{
name|e
operator|.
name|printStackTrace
argument_list|()
expr_stmt|;
name|f
operator|.
name|set
argument_list|(
name|e
argument_list|)
expr_stmt|;
block|}
block|}
block|}
argument_list|)
expr_stmt|;
return|return
name|f
return|;
block|}
specifier|private
name|ElectingLevelDBStore
name|createStoreNode
parameter_list|()
block|{
name|ElectingLevelDBStore
name|store
init|=
operator|new
name|ElectingLevelDBStore
argument_list|()
decl_stmt|;
name|store
operator|.
name|setSecurityToken
argument_list|(
literal|"foo"
argument_list|)
expr_stmt|;
name|store
operator|.
name|setLogSize
argument_list|(
literal|1024
operator|*
literal|200
argument_list|)
expr_stmt|;
name|store
operator|.
name|setReplicas
argument_list|(
literal|2
argument_list|)
expr_stmt|;
name|store
operator|.
name|setSync
argument_list|(
literal|"quorum_disk"
argument_list|)
expr_stmt|;
name|store
operator|.
name|setZkSessionTimeout
argument_list|(
literal|"15s"
argument_list|)
expr_stmt|;
name|store
operator|.
name|setZkAddress
argument_list|(
literal|"localhost:"
operator|+
name|connector
operator|.
name|getLocalPort
argument_list|()
argument_list|)
expr_stmt|;
name|store
operator|.
name|setZkPath
argument_list|(
literal|"/broker-stores"
argument_list|)
expr_stmt|;
name|store
operator|.
name|setBrokerName
argument_list|(
literal|"foo"
argument_list|)
expr_stmt|;
name|store
operator|.
name|setHostname
argument_list|(
literal|"localhost"
argument_list|)
expr_stmt|;
name|store
operator|.
name|setBind
argument_list|(
literal|"tcp://0.0.0.0:0"
argument_list|)
expr_stmt|;
return|return
name|store
return|;
block|}
block|}
end_class

end_unit

