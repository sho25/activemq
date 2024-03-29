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
name|store
operator|.
name|kahadb
operator|.
name|disk
operator|.
name|journal
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
name|management
operator|.
name|TimeStatisticImpl
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
name|kahadb
operator|.
name|KahaDBStore
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
name|ByteSequence
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
name|Wait
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
name|Random
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
name|PreallocationJournalLatencyTest
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
name|PreallocationJournalLatencyTest
operator|.
name|class
argument_list|)
decl_stmt|;
specifier|final
name|Random
name|rand
init|=
operator|new
name|Random
argument_list|()
decl_stmt|;
annotation|@
name|Test
specifier|public
name|void
name|preallocationLatency
parameter_list|()
throws|throws
name|Exception
block|{
name|TimeStatisticImpl
name|sparse
init|=
name|executeTest
argument_list|(
name|Journal
operator|.
name|PreallocationStrategy
operator|.
name|SPARSE_FILE
operator|.
name|name
argument_list|()
argument_list|)
decl_stmt|;
name|TimeStatisticImpl
name|chunked_zeros
init|=
name|executeTest
argument_list|(
name|Journal
operator|.
name|PreallocationStrategy
operator|.
name|CHUNKED_ZEROS
operator|.
name|name
argument_list|()
argument_list|)
decl_stmt|;
comment|//TimeStatisticImpl zeros = executeTest(Journal.PreallocationStrategy.ZEROS.name());
name|TimeStatisticImpl
name|kernel
init|=
name|executeTest
argument_list|(
name|Journal
operator|.
name|PreallocationStrategy
operator|.
name|OS_KERNEL_COPY
operator|.
name|name
argument_list|()
argument_list|)
decl_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"  sparse: "
operator|+
name|sparse
argument_list|)
expr_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|" chunked: "
operator|+
name|chunked_zeros
argument_list|)
expr_stmt|;
comment|//LOG.info("   zeros: " + zeros);
name|LOG
operator|.
name|info
argument_list|(
literal|"  kernel: "
operator|+
name|kernel
argument_list|)
expr_stmt|;
block|}
specifier|private
name|TimeStatisticImpl
name|executeTest
parameter_list|(
name|String
name|preallocationStrategy
parameter_list|)
throws|throws
name|Exception
block|{
name|int
name|randInt
init|=
name|rand
operator|.
name|nextInt
argument_list|(
literal|100
argument_list|)
decl_stmt|;
name|File
name|dataDirectory
init|=
operator|new
name|File
argument_list|(
literal|"./target/activemq-data/kahadb"
operator|+
name|randInt
argument_list|)
decl_stmt|;
specifier|final
name|KahaDBStore
name|store
init|=
operator|new
name|KahaDBStore
argument_list|()
decl_stmt|;
name|store
operator|.
name|setCheckpointInterval
argument_list|(
literal|5000
argument_list|)
expr_stmt|;
name|store
operator|.
name|setJournalMaxFileLength
argument_list|(
literal|32
operator|*
literal|1204
operator|*
literal|1024
argument_list|)
expr_stmt|;
name|store
operator|.
name|deleteAllMessages
argument_list|()
expr_stmt|;
name|store
operator|.
name|setDirectory
argument_list|(
name|dataDirectory
argument_list|)
expr_stmt|;
name|store
operator|.
name|setPreallocationStrategy
argument_list|(
name|preallocationStrategy
argument_list|)
expr_stmt|;
name|store
operator|.
name|setPreallocationScope
argument_list|(
name|Journal
operator|.
name|PreallocationScope
operator|.
name|ENTIRE_JOURNAL_ASYNC
operator|.
name|name
argument_list|()
argument_list|)
expr_stmt|;
name|store
operator|.
name|start
argument_list|()
expr_stmt|;
specifier|final
name|File
name|journalLog
init|=
operator|new
name|File
argument_list|(
name|dataDirectory
argument_list|,
literal|"db-1.log"
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
literal|"file exists"
argument_list|,
name|Wait
operator|.
name|waitFor
argument_list|(
operator|new
name|Wait
operator|.
name|Condition
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|boolean
name|isSatisified
parameter_list|()
throws|throws
name|Exception
block|{
return|return
name|journalLog
operator|.
name|exists
argument_list|()
return|;
block|}
block|}
argument_list|)
argument_list|)
expr_stmt|;
specifier|final
name|Journal
name|journal
init|=
name|store
operator|.
name|getJournal
argument_list|()
decl_stmt|;
name|ByteSequence
name|byteSequence
init|=
operator|new
name|ByteSequence
argument_list|(
operator|new
name|byte
index|[
literal|16
operator|*
literal|1024
index|]
argument_list|)
decl_stmt|;
name|TimeStatisticImpl
name|timeStatistic
init|=
operator|new
name|TimeStatisticImpl
argument_list|(
literal|"append"
argument_list|,
literal|"duration"
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
literal|5000
condition|;
name|i
operator|++
control|)
block|{
specifier|final
name|long
name|start
init|=
name|System
operator|.
name|currentTimeMillis
argument_list|()
decl_stmt|;
name|journal
operator|.
name|write
argument_list|(
name|byteSequence
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|timeStatistic
operator|.
name|addTime
argument_list|(
name|System
operator|.
name|currentTimeMillis
argument_list|()
operator|-
name|start
argument_list|)
expr_stmt|;
block|}
name|LOG
operator|.
name|info
argument_list|(
literal|"current journal dataFile id: "
operator|+
name|journal
operator|.
name|getCurrentDataFileId
argument_list|()
argument_list|)
expr_stmt|;
name|store
operator|.
name|stop
argument_list|()
expr_stmt|;
return|return
name|timeStatistic
return|;
block|}
block|}
end_class

end_unit

