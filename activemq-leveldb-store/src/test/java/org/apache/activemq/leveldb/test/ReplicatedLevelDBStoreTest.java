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
name|leveldb
operator|.
name|replicated
operator|.
name|MasterLevelDBStore
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
name|SlaveLevelDBStore
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
name|util
operator|.
name|FileSupport
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
name|fusesource
operator|.
name|hawtdispatch
operator|.
name|transport
operator|.
name|TcpTransport
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
name|LinkedList
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
name|addMessage
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
name|createPlayload
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
name|getMessages
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
name|ReplicatedLevelDBStoreTest
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
name|ReplicatedLevelDBStoreTest
operator|.
name|class
argument_list|)
decl_stmt|;
name|ArrayList
argument_list|<
name|LevelDBStore
argument_list|>
name|stores
init|=
operator|new
name|ArrayList
argument_list|<
name|LevelDBStore
argument_list|>
argument_list|()
decl_stmt|;
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
name|testMinReplicaEnforced
parameter_list|()
throws|throws
name|Exception
block|{
name|File
name|masterDir
init|=
operator|new
name|File
argument_list|(
literal|"target/activemq-data/leveldb-node1"
argument_list|)
decl_stmt|;
name|File
name|slaveDir
init|=
operator|new
name|File
argument_list|(
literal|"target/activemq-data/leveldb-node2"
argument_list|)
decl_stmt|;
name|FileSupport
operator|.
name|toRichFile
argument_list|(
name|masterDir
argument_list|)
operator|.
name|recursiveDelete
argument_list|()
expr_stmt|;
name|FileSupport
operator|.
name|toRichFile
argument_list|(
name|slaveDir
argument_list|)
operator|.
name|recursiveDelete
argument_list|()
expr_stmt|;
specifier|final
name|MasterLevelDBStore
name|master
init|=
name|createMaster
argument_list|(
name|masterDir
argument_list|)
decl_stmt|;
name|master
operator|.
name|setReplicas
argument_list|(
literal|2
argument_list|)
expr_stmt|;
name|CountDownFuture
name|masterStartLatch
init|=
name|asyncStart
argument_list|(
name|master
argument_list|)
decl_stmt|;
name|stores
operator|.
name|add
argument_list|(
name|master
argument_list|)
expr_stmt|;
comment|// Start the store should not complete since we don't have enough
comment|// replicas.
name|assertFalse
argument_list|(
name|masterStartLatch
operator|.
name|await
argument_list|(
literal|2
argument_list|,
name|TimeUnit
operator|.
name|SECONDS
argument_list|)
argument_list|)
expr_stmt|;
comment|// Adding a slave should allow the master startup to complete.
name|SlaveLevelDBStore
name|slave
init|=
name|createSlave
argument_list|(
name|master
argument_list|,
name|slaveDir
argument_list|)
decl_stmt|;
name|slave
operator|.
name|start
argument_list|()
expr_stmt|;
name|stores
operator|.
name|add
argument_list|(
name|slave
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|masterStartLatch
operator|.
name|await
argument_list|(
literal|2
argument_list|,
name|TimeUnit
operator|.
name|SECONDS
argument_list|)
argument_list|)
expr_stmt|;
comment|// New updates should complete quickly now..
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
name|CountDownFuture
name|f
init|=
name|asyncAddMessage
argument_list|(
name|ms
argument_list|,
literal|"m1"
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
name|f
operator|.
name|await
argument_list|(
literal|1
argument_list|,
name|TimeUnit
operator|.
name|SECONDS
argument_list|)
argument_list|)
expr_stmt|;
comment|// If the slave goes offline, then updates should once again
comment|// not complete.
name|slave
operator|.
name|stop
argument_list|()
expr_stmt|;
name|f
operator|=
name|asyncAddMessage
argument_list|(
name|ms
argument_list|,
literal|"m2"
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|f
operator|.
name|await
argument_list|(
literal|2
argument_list|,
name|TimeUnit
operator|.
name|SECONDS
argument_list|)
argument_list|)
expr_stmt|;
comment|// Restart and the op should complete.
name|slave
operator|=
name|createSlave
argument_list|(
name|master
argument_list|,
name|slaveDir
argument_list|)
expr_stmt|;
name|slave
operator|.
name|start
argument_list|()
expr_stmt|;
name|assertTrue
argument_list|(
name|f
operator|.
name|await
argument_list|(
literal|2
argument_list|,
name|TimeUnit
operator|.
name|SECONDS
argument_list|)
argument_list|)
expr_stmt|;
name|master
operator|.
name|stop
argument_list|()
expr_stmt|;
name|slave
operator|.
name|stop
argument_list|()
expr_stmt|;
block|}
specifier|private
name|CountDownFuture
name|asyncAddMessage
parameter_list|(
specifier|final
name|MessageStore
name|ms
parameter_list|,
specifier|final
name|String
name|body
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
name|addMessage
argument_list|(
name|ms
argument_list|,
name|body
argument_list|)
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
name|testReplication
parameter_list|()
throws|throws
name|Exception
block|{
name|LinkedList
argument_list|<
name|File
argument_list|>
name|directories
init|=
operator|new
name|LinkedList
argument_list|<
name|File
argument_list|>
argument_list|()
decl_stmt|;
name|directories
operator|.
name|add
argument_list|(
operator|new
name|File
argument_list|(
literal|"target/activemq-data/leveldb-node1"
argument_list|)
argument_list|)
expr_stmt|;
name|directories
operator|.
name|add
argument_list|(
operator|new
name|File
argument_list|(
literal|"target/activemq-data/leveldb-node2"
argument_list|)
argument_list|)
expr_stmt|;
name|directories
operator|.
name|add
argument_list|(
operator|new
name|File
argument_list|(
literal|"target/activemq-data/leveldb-node3"
argument_list|)
argument_list|)
expr_stmt|;
name|resetDirectories
argument_list|(
name|directories
argument_list|)
expr_stmt|;
comment|// For some reason this had to be 64k to trigger a bug where
comment|// slave index snapshots were being done incorrectly.
name|String
name|playload
init|=
name|createPlayload
argument_list|(
literal|64
operator|*
literal|1024
argument_list|)
decl_stmt|;
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
comment|// We will rotate between 3 nodes the task of being the master.
for|for
control|(
name|int
name|j
init|=
literal|0
init|;
name|j
operator|<
literal|5
condition|;
name|j
operator|++
control|)
block|{
name|MasterLevelDBStore
name|master
init|=
name|createMaster
argument_list|(
name|directories
operator|.
name|get
argument_list|(
literal|0
argument_list|)
argument_list|)
decl_stmt|;
name|CountDownFuture
name|masterStart
init|=
name|asyncStart
argument_list|(
name|master
argument_list|)
decl_stmt|;
name|SlaveLevelDBStore
name|slave1
init|=
name|createSlave
argument_list|(
name|master
argument_list|,
name|directories
operator|.
name|get
argument_list|(
literal|1
argument_list|)
argument_list|)
decl_stmt|;
name|SlaveLevelDBStore
name|slave2
init|=
name|createSlave
argument_list|(
name|master
argument_list|,
name|directories
operator|.
name|get
argument_list|(
literal|2
argument_list|)
argument_list|)
decl_stmt|;
name|asyncStart
argument_list|(
name|slave2
argument_list|)
expr_stmt|;
name|masterStart
operator|.
name|await
argument_list|()
expr_stmt|;
if|if
condition|(
name|j
operator|==
literal|0
condition|)
block|{
name|stores
operator|.
name|add
argument_list|(
name|master
argument_list|)
expr_stmt|;
name|stores
operator|.
name|add
argument_list|(
name|slave1
argument_list|)
expr_stmt|;
name|stores
operator|.
name|add
argument_list|(
name|slave2
argument_list|)
expr_stmt|;
block|}
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
name|LOG
operator|.
name|info
argument_list|(
literal|"Checking: "
operator|+
name|master
operator|.
name|getDirectory
argument_list|()
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
name|LOG
operator|.
name|info
argument_list|(
literal|"Adding messages..."
argument_list|)
expr_stmt|;
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
name|slave1
operator|.
name|start
argument_list|()
expr_stmt|;
name|slave2
operator|.
name|stop
argument_list|()
expr_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"Checking: "
operator|+
name|master
operator|.
name|getDirectory
argument_list|()
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
block|}
name|String
name|msgid
init|=
literal|"m:"
operator|+
name|j
operator|+
literal|":"
operator|+
name|i
decl_stmt|;
name|addMessage
argument_list|(
name|ms
argument_list|,
name|msgid
argument_list|,
name|playload
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
literal|"Checking: "
operator|+
name|master
operator|.
name|getDirectory
argument_list|()
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
name|LOG
operator|.
name|info
argument_list|(
literal|"Stopping master: "
operator|+
name|master
operator|.
name|getDirectory
argument_list|()
argument_list|)
expr_stmt|;
name|master
operator|.
name|stop
argument_list|()
expr_stmt|;
name|Thread
operator|.
name|sleep
argument_list|(
literal|3
operator|*
literal|1000
argument_list|)
expr_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"Stopping slave: "
operator|+
name|slave1
operator|.
name|getDirectory
argument_list|()
argument_list|)
expr_stmt|;
name|slave1
operator|.
name|stop
argument_list|()
expr_stmt|;
comment|// Rotate the dir order so that slave1 becomes the master next.
name|directories
operator|.
name|addLast
argument_list|(
name|directories
operator|.
name|removeFirst
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
name|void
name|resetDirectories
parameter_list|(
name|LinkedList
argument_list|<
name|File
argument_list|>
name|directories
parameter_list|)
block|{
for|for
control|(
name|File
name|directory
range|:
name|directories
control|)
block|{
name|FileSupport
operator|.
name|toRichFile
argument_list|(
name|directory
argument_list|)
operator|.
name|recursiveDelete
argument_list|()
expr_stmt|;
name|directory
operator|.
name|mkdirs
argument_list|()
expr_stmt|;
name|FileSupport
operator|.
name|toRichFile
argument_list|(
operator|new
name|File
argument_list|(
name|directory
argument_list|,
literal|"nodeid.txt"
argument_list|)
argument_list|)
operator|.
name|writeText
argument_list|(
name|directory
operator|.
name|getName
argument_list|()
argument_list|,
literal|"UTF-8"
argument_list|)
expr_stmt|;
block|}
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
name|testSlowSlave
parameter_list|()
throws|throws
name|Exception
block|{
name|LinkedList
argument_list|<
name|File
argument_list|>
name|directories
init|=
operator|new
name|LinkedList
argument_list|<
name|File
argument_list|>
argument_list|()
decl_stmt|;
name|directories
operator|.
name|add
argument_list|(
operator|new
name|File
argument_list|(
literal|"target/activemq-data/leveldb-node1"
argument_list|)
argument_list|)
expr_stmt|;
name|directories
operator|.
name|add
argument_list|(
operator|new
name|File
argument_list|(
literal|"target/activemq-data/leveldb-node2"
argument_list|)
argument_list|)
expr_stmt|;
name|directories
operator|.
name|add
argument_list|(
operator|new
name|File
argument_list|(
literal|"target/activemq-data/leveldb-node3"
argument_list|)
argument_list|)
expr_stmt|;
name|resetDirectories
argument_list|(
name|directories
argument_list|)
expr_stmt|;
name|File
name|node1Dir
init|=
name|directories
operator|.
name|get
argument_list|(
literal|0
argument_list|)
decl_stmt|;
name|File
name|node2Dir
init|=
name|directories
operator|.
name|get
argument_list|(
literal|1
argument_list|)
decl_stmt|;
name|File
name|node3Dir
init|=
name|directories
operator|.
name|get
argument_list|(
literal|2
argument_list|)
decl_stmt|;
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
name|MasterLevelDBStore
name|node1
init|=
name|createMaster
argument_list|(
name|node1Dir
argument_list|)
decl_stmt|;
name|stores
operator|.
name|add
argument_list|(
name|node1
argument_list|)
expr_stmt|;
name|CountDownFuture
name|masterStart
init|=
name|asyncStart
argument_list|(
name|node1
argument_list|)
decl_stmt|;
comment|// Lets create a 1 slow slave...
name|SlaveLevelDBStore
name|node2
init|=
operator|new
name|SlaveLevelDBStore
argument_list|()
block|{
name|boolean
name|hitOnce
init|=
literal|false
decl_stmt|;
annotation|@
name|Override
specifier|public
name|TcpTransport
name|create_transport
parameter_list|()
block|{
if|if
condition|(
name|hitOnce
condition|)
block|{
return|return
name|super
operator|.
name|create_transport
argument_list|()
return|;
block|}
name|hitOnce
operator|=
literal|true
expr_stmt|;
name|TcpTransport
name|transport
init|=
name|super
operator|.
name|create_transport
argument_list|()
decl_stmt|;
name|transport
operator|.
name|setMaxReadRate
argument_list|(
literal|64
operator|*
literal|1024
argument_list|)
expr_stmt|;
return|return
name|transport
return|;
block|}
block|}
decl_stmt|;
name|stores
operator|.
name|add
argument_list|(
name|node2
argument_list|)
expr_stmt|;
name|configureSlave
argument_list|(
name|node2
argument_list|,
name|node1
argument_list|,
name|node2Dir
argument_list|)
expr_stmt|;
name|SlaveLevelDBStore
name|node3
init|=
name|createSlave
argument_list|(
name|node1
argument_list|,
name|node3Dir
argument_list|)
decl_stmt|;
name|stores
operator|.
name|add
argument_list|(
name|node3
argument_list|)
expr_stmt|;
name|asyncStart
argument_list|(
name|node2
argument_list|)
expr_stmt|;
name|asyncStart
argument_list|(
name|node3
argument_list|)
expr_stmt|;
name|masterStart
operator|.
name|await
argument_list|()
expr_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"Adding messages..."
argument_list|)
expr_stmt|;
name|String
name|playload
init|=
name|createPlayload
argument_list|(
literal|64
operator|*
literal|1024
argument_list|)
decl_stmt|;
name|MessageStore
name|ms
init|=
name|node1
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
literal|10
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
operator|==
literal|8
condition|)
block|{
comment|// Stop the fast slave so that we wait for the slow slave to
comment|// catch up..
name|node3
operator|.
name|stop
argument_list|()
expr_stmt|;
block|}
name|String
name|msgid
init|=
literal|"m:"
operator|+
literal|":"
operator|+
name|i
decl_stmt|;
name|addMessage
argument_list|(
name|ms
argument_list|,
name|msgid
argument_list|,
name|playload
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
literal|"Checking node1 state"
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
name|LOG
operator|.
name|info
argument_list|(
literal|"Stopping node1: "
operator|+
name|node1
operator|.
name|node_id
argument_list|()
argument_list|)
expr_stmt|;
name|node1
operator|.
name|stop
argument_list|()
expr_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"Stopping slave: "
operator|+
name|node2
operator|.
name|node_id
argument_list|()
argument_list|)
expr_stmt|;
name|node2
operator|.
name|stop
argument_list|()
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
for|for
control|(
name|LevelDBStore
name|store
range|:
name|stores
control|)
block|{
if|if
condition|(
name|store
operator|.
name|isStarted
argument_list|()
condition|)
block|{
name|store
operator|.
name|stop
argument_list|()
expr_stmt|;
block|}
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
name|SlaveLevelDBStore
name|createSlave
parameter_list|(
name|MasterLevelDBStore
name|master
parameter_list|,
name|File
name|directory
parameter_list|)
block|{
name|SlaveLevelDBStore
name|slave
init|=
operator|new
name|SlaveLevelDBStore
argument_list|()
decl_stmt|;
name|configureSlave
argument_list|(
name|slave
argument_list|,
name|master
argument_list|,
name|directory
argument_list|)
expr_stmt|;
return|return
name|slave
return|;
block|}
specifier|private
name|SlaveLevelDBStore
name|configureSlave
parameter_list|(
name|SlaveLevelDBStore
name|slave
parameter_list|,
name|MasterLevelDBStore
name|master
parameter_list|,
name|File
name|directory
parameter_list|)
block|{
name|slave
operator|.
name|setDirectory
argument_list|(
name|directory
argument_list|)
expr_stmt|;
name|slave
operator|.
name|setConnect
argument_list|(
literal|"tcp://127.0.0.1:"
operator|+
name|master
operator|.
name|getPort
argument_list|()
argument_list|)
expr_stmt|;
name|slave
operator|.
name|setSecurityToken
argument_list|(
literal|"foo"
argument_list|)
expr_stmt|;
name|slave
operator|.
name|setLogSize
argument_list|(
literal|1023
operator|*
literal|200
argument_list|)
expr_stmt|;
return|return
name|slave
return|;
block|}
specifier|private
name|MasterLevelDBStore
name|createMaster
parameter_list|(
name|File
name|directory
parameter_list|)
block|{
name|MasterLevelDBStore
name|master
init|=
operator|new
name|MasterLevelDBStore
argument_list|()
decl_stmt|;
name|master
operator|.
name|setDirectory
argument_list|(
name|directory
argument_list|)
expr_stmt|;
name|master
operator|.
name|setBind
argument_list|(
literal|"tcp://0.0.0.0:0"
argument_list|)
expr_stmt|;
name|master
operator|.
name|setSecurityToken
argument_list|(
literal|"foo"
argument_list|)
expr_stmt|;
name|master
operator|.
name|setReplicas
argument_list|(
literal|2
argument_list|)
expr_stmt|;
name|master
operator|.
name|setLogSize
argument_list|(
literal|1023
operator|*
literal|200
argument_list|)
expr_stmt|;
return|return
name|master
return|;
block|}
block|}
end_class

end_unit

