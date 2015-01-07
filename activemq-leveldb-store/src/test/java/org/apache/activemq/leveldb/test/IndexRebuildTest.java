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
name|FilenameFilter
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
name|util
operator|.
name|ArrayList
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
name|command
operator|.
name|MessageId
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
name|LevelDBStoreView
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
name|assertTrue
import|;
end_import

begin_class
specifier|public
class|class
name|IndexRebuildTest
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
name|IndexRebuildTest
operator|.
name|class
argument_list|)
decl_stmt|;
specifier|final
name|int
name|max
init|=
literal|30
decl_stmt|;
specifier|final
name|int
name|toLeave
init|=
literal|5
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
name|testRebuildIndex
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
literal|"target/activemq-data/leveldb-rebuild"
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
specifier|final
name|LevelDBStore
name|store
init|=
operator|new
name|LevelDBStore
argument_list|()
decl_stmt|;
name|store
operator|.
name|setDirectory
argument_list|(
name|masterDir
argument_list|)
expr_stmt|;
name|store
operator|.
name|setLogDirectory
argument_list|(
name|masterDir
argument_list|)
expr_stmt|;
name|store
operator|.
name|setLogSize
argument_list|(
literal|1024
operator|*
literal|10
argument_list|)
expr_stmt|;
name|store
operator|.
name|start
argument_list|()
expr_stmt|;
name|stores
operator|.
name|add
argument_list|(
name|store
argument_list|)
expr_stmt|;
name|ArrayList
argument_list|<
name|MessageId
argument_list|>
name|inserts
init|=
operator|new
name|ArrayList
argument_list|<
name|MessageId
argument_list|>
argument_list|()
decl_stmt|;
name|MessageStore
name|ms
init|=
name|store
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
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|max
condition|;
name|i
operator|++
control|)
block|{
name|inserts
operator|.
name|add
argument_list|(
name|addMessage
argument_list|(
name|ms
argument_list|,
literal|"m"
operator|+
name|i
argument_list|)
operator|.
name|getMessageId
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|int
name|logFileCount
init|=
name|countLogFiles
argument_list|(
name|store
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
literal|"more than one journal file"
argument_list|,
name|logFileCount
operator|>
literal|1
argument_list|)
expr_stmt|;
for|for
control|(
name|MessageId
name|id
range|:
name|inserts
operator|.
name|subList
argument_list|(
literal|0
argument_list|,
name|inserts
operator|.
name|size
argument_list|()
operator|-
name|toLeave
argument_list|)
control|)
block|{
name|removeMessage
argument_list|(
name|ms
argument_list|,
name|id
argument_list|)
expr_stmt|;
block|}
name|LevelDBStoreView
name|view
init|=
operator|new
name|LevelDBStoreView
argument_list|(
name|store
argument_list|)
decl_stmt|;
name|view
operator|.
name|compact
argument_list|()
expr_stmt|;
name|int
name|reducedLogFileCount
init|=
name|countLogFiles
argument_list|(
name|store
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
literal|"log files deleted"
argument_list|,
name|logFileCount
operator|>
name|reducedLogFileCount
argument_list|)
expr_stmt|;
name|store
operator|.
name|stop
argument_list|()
expr_stmt|;
name|deleteTheIndex
argument_list|(
name|store
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"log files remain"
argument_list|,
name|reducedLogFileCount
argument_list|,
name|countLogFiles
argument_list|(
name|store
argument_list|)
argument_list|)
expr_stmt|;
comment|// restart, recover and verify message read
name|store
operator|.
name|start
argument_list|()
expr_stmt|;
name|ms
operator|=
name|store
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
name|assertEquals
argument_list|(
name|toLeave
operator|+
literal|" messages remain"
argument_list|,
name|toLeave
argument_list|,
name|getMessages
argument_list|(
name|ms
argument_list|)
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
block|}
specifier|private
name|void
name|deleteTheIndex
parameter_list|(
name|LevelDBStore
name|store
parameter_list|)
throws|throws
name|IOException
block|{
for|for
control|(
name|String
name|index
range|:
name|store
operator|.
name|getLogDirectory
argument_list|()
operator|.
name|list
argument_list|(
operator|new
name|FilenameFilter
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|boolean
name|accept
parameter_list|(
name|File
name|dir
parameter_list|,
name|String
name|name
parameter_list|)
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"dir:"
operator|+
name|dir
operator|+
literal|", name: "
operator|+
name|name
argument_list|)
expr_stmt|;
return|return
operator|(
name|name
operator|!=
literal|null
operator|&&
name|name
operator|.
name|endsWith
argument_list|(
literal|".index"
argument_list|)
operator|)
return|;
block|}
block|}
argument_list|)
control|)
block|{
name|File
name|file
init|=
operator|new
name|File
argument_list|(
name|store
operator|.
name|getLogDirectory
argument_list|()
operator|.
name|getAbsoluteFile
argument_list|()
argument_list|,
name|index
argument_list|)
decl_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"Deleting index directory:"
operator|+
name|file
argument_list|)
expr_stmt|;
name|FileUtils
operator|.
name|deleteDirectory
argument_list|(
name|file
argument_list|)
expr_stmt|;
block|}
block|}
specifier|private
name|int
name|countLogFiles
parameter_list|(
name|LevelDBStore
name|store
parameter_list|)
block|{
return|return
name|store
operator|.
name|getLogDirectory
argument_list|()
operator|.
name|list
argument_list|(
operator|new
name|FilenameFilter
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|boolean
name|accept
parameter_list|(
name|File
name|dir
parameter_list|,
name|String
name|name
parameter_list|)
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"dir:"
operator|+
name|dir
operator|+
literal|", name: "
operator|+
name|name
argument_list|)
expr_stmt|;
return|return
operator|(
name|name
operator|!=
literal|null
operator|&&
name|name
operator|.
name|endsWith
argument_list|(
literal|".log"
argument_list|)
operator|)
return|;
block|}
block|}
argument_list|)
operator|.
name|length
return|;
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
block|}
end_class

end_unit

