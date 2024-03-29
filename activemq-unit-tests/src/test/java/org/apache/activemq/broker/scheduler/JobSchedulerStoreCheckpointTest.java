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
name|scheduler
package|;
end_package

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
name|store
operator|.
name|kahadb
operator|.
name|scheduler
operator|.
name|JobSchedulerStoreImpl
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
name|IOHelper
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
name|apache
operator|.
name|log4j
operator|.
name|Level
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

begin_class
specifier|public
class|class
name|JobSchedulerStoreCheckpointTest
block|{
specifier|static
specifier|final
name|Logger
name|LOG
init|=
name|LoggerFactory
operator|.
name|getLogger
argument_list|(
name|JobSchedulerStoreCheckpointTest
operator|.
name|class
argument_list|)
decl_stmt|;
specifier|private
name|JobSchedulerStoreImpl
name|store
decl_stmt|;
specifier|private
name|JobScheduler
name|scheduler
decl_stmt|;
specifier|private
name|ByteSequence
name|payload
decl_stmt|;
annotation|@
name|Before
specifier|public
name|void
name|setUp
parameter_list|()
throws|throws
name|Exception
block|{
comment|// investigate gc issue - store usage not getting released
name|org
operator|.
name|apache
operator|.
name|log4j
operator|.
name|Logger
operator|.
name|getLogger
argument_list|(
name|JobSchedulerStoreImpl
operator|.
name|class
argument_list|)
operator|.
name|setLevel
argument_list|(
name|Level
operator|.
name|TRACE
argument_list|)
expr_stmt|;
name|File
name|directory
init|=
operator|new
name|File
argument_list|(
literal|"target/test/ScheduledJobsDB"
argument_list|)
decl_stmt|;
name|IOHelper
operator|.
name|mkdirs
argument_list|(
name|directory
argument_list|)
expr_stmt|;
name|IOHelper
operator|.
name|deleteChildren
argument_list|(
name|directory
argument_list|)
expr_stmt|;
name|startStore
argument_list|(
name|directory
argument_list|)
expr_stmt|;
name|byte
index|[]
name|data
init|=
operator|new
name|byte
index|[
literal|8192
index|]
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
name|data
operator|.
name|length
condition|;
operator|++
name|i
control|)
block|{
name|data
index|[
name|i
index|]
operator|=
call|(
name|byte
call|)
argument_list|(
name|i
operator|%
literal|256
argument_list|)
expr_stmt|;
block|}
name|payload
operator|=
operator|new
name|ByteSequence
argument_list|(
name|data
argument_list|)
expr_stmt|;
block|}
specifier|protected
name|void
name|startStore
parameter_list|(
name|File
name|directory
parameter_list|)
throws|throws
name|Exception
block|{
name|store
operator|=
operator|new
name|JobSchedulerStoreImpl
argument_list|()
expr_stmt|;
name|store
operator|.
name|setDirectory
argument_list|(
name|directory
argument_list|)
expr_stmt|;
name|store
operator|.
name|setCheckpointInterval
argument_list|(
literal|5000
argument_list|)
expr_stmt|;
name|store
operator|.
name|setCleanupInterval
argument_list|(
literal|10000
argument_list|)
expr_stmt|;
name|store
operator|.
name|setJournalMaxFileLength
argument_list|(
literal|10
operator|*
literal|1024
argument_list|)
expr_stmt|;
name|store
operator|.
name|start
argument_list|()
expr_stmt|;
name|scheduler
operator|=
name|store
operator|.
name|getJobScheduler
argument_list|(
literal|"test"
argument_list|)
expr_stmt|;
name|scheduler
operator|.
name|startDispatching
argument_list|()
expr_stmt|;
block|}
specifier|private
name|int
name|getNumJournalFiles
parameter_list|()
throws|throws
name|IOException
block|{
return|return
name|store
operator|.
name|getJournal
argument_list|()
operator|.
name|getFileMap
argument_list|()
operator|.
name|size
argument_list|()
return|;
block|}
annotation|@
name|After
specifier|public
name|void
name|tearDown
parameter_list|()
throws|throws
name|Exception
block|{
name|scheduler
operator|.
name|stopDispatching
argument_list|()
expr_stmt|;
name|store
operator|.
name|stop
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testStoreCleanupLinear
parameter_list|()
throws|throws
name|Exception
block|{
specifier|final
name|int
name|COUNT
init|=
literal|10
decl_stmt|;
specifier|final
name|CountDownLatch
name|latch
init|=
operator|new
name|CountDownLatch
argument_list|(
name|COUNT
argument_list|)
decl_stmt|;
name|scheduler
operator|.
name|addListener
argument_list|(
operator|new
name|JobListener
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|void
name|scheduledJob
parameter_list|(
name|String
name|id
parameter_list|,
name|ByteSequence
name|job
parameter_list|)
block|{
name|latch
operator|.
name|countDown
argument_list|()
expr_stmt|;
block|}
block|}
argument_list|)
expr_stmt|;
name|long
name|time
init|=
name|TimeUnit
operator|.
name|SECONDS
operator|.
name|toMillis
argument_list|(
literal|30
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
name|COUNT
condition|;
name|i
operator|++
control|)
block|{
name|scheduler
operator|.
name|schedule
argument_list|(
literal|"id"
operator|+
name|i
argument_list|,
name|payload
argument_list|,
literal|""
argument_list|,
name|time
argument_list|,
literal|0
argument_list|,
literal|0
argument_list|)
expr_stmt|;
block|}
name|int
name|size
init|=
name|scheduler
operator|.
name|getAllJobs
argument_list|()
operator|.
name|size
argument_list|()
decl_stmt|;
name|assertEquals
argument_list|(
name|size
argument_list|,
name|COUNT
argument_list|)
expr_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"Number of journal log files: {}"
argument_list|,
name|getNumJournalFiles
argument_list|()
argument_list|)
expr_stmt|;
comment|// need a little slack so go over 60 seconds
name|assertTrue
argument_list|(
name|latch
operator|.
name|await
argument_list|(
literal|70
argument_list|,
name|TimeUnit
operator|.
name|SECONDS
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|latch
operator|.
name|getCount
argument_list|()
argument_list|)
expr_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|COUNT
condition|;
name|i
operator|++
control|)
block|{
name|scheduler
operator|.
name|schedule
argument_list|(
literal|"id"
operator|+
name|i
argument_list|,
name|payload
argument_list|,
literal|""
argument_list|,
name|time
argument_list|,
literal|0
argument_list|,
literal|0
argument_list|)
expr_stmt|;
block|}
name|LOG
operator|.
name|info
argument_list|(
literal|"Number of journal log files: {}"
argument_list|,
name|getNumJournalFiles
argument_list|()
argument_list|)
expr_stmt|;
comment|// need a little slack so go over 60 seconds
name|assertTrue
argument_list|(
name|latch
operator|.
name|await
argument_list|(
literal|70
argument_list|,
name|TimeUnit
operator|.
name|SECONDS
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|latch
operator|.
name|getCount
argument_list|()
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
literal|"Should be only one log left: "
operator|+
name|getNumJournalFiles
argument_list|()
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
name|getNumJournalFiles
argument_list|()
operator|==
literal|1
return|;
block|}
block|}
argument_list|,
name|TimeUnit
operator|.
name|MINUTES
operator|.
name|toMillis
argument_list|(
literal|2
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"Number of journal log files: {}"
argument_list|,
name|getNumJournalFiles
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testColocatedAddRemoveCleanup
parameter_list|()
throws|throws
name|Exception
block|{
specifier|final
name|CountDownLatch
name|latch
init|=
operator|new
name|CountDownLatch
argument_list|(
literal|1
argument_list|)
decl_stmt|;
name|scheduler
operator|.
name|addListener
argument_list|(
operator|new
name|JobListener
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|void
name|scheduledJob
parameter_list|(
name|String
name|id
parameter_list|,
name|ByteSequence
name|job
parameter_list|)
block|{
name|latch
operator|.
name|countDown
argument_list|()
expr_stmt|;
block|}
block|}
argument_list|)
expr_stmt|;
name|byte
index|[]
name|data
init|=
operator|new
name|byte
index|[
literal|1024
index|]
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
name|data
operator|.
name|length
condition|;
operator|++
name|i
control|)
block|{
name|data
index|[
name|i
index|]
operator|=
call|(
name|byte
call|)
argument_list|(
name|i
operator|%
literal|256
argument_list|)
expr_stmt|;
block|}
name|long
name|time
init|=
name|TimeUnit
operator|.
name|SECONDS
operator|.
name|toMillis
argument_list|(
literal|2
argument_list|)
decl_stmt|;
name|scheduler
operator|.
name|schedule
argument_list|(
literal|"Message-1"
argument_list|,
operator|new
name|ByteSequence
argument_list|(
name|data
argument_list|)
argument_list|,
literal|""
argument_list|,
name|time
argument_list|,
literal|0
argument_list|,
literal|0
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|latch
operator|.
name|await
argument_list|(
literal|70
argument_list|,
name|TimeUnit
operator|.
name|SECONDS
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|latch
operator|.
name|getCount
argument_list|()
argument_list|)
expr_stmt|;
name|scheduler
operator|.
name|schedule
argument_list|(
literal|"Message-2"
argument_list|,
name|payload
argument_list|,
literal|""
argument_list|,
name|time
argument_list|,
literal|0
argument_list|,
literal|0
argument_list|)
expr_stmt|;
name|scheduler
operator|.
name|schedule
argument_list|(
literal|"Message-3"
argument_list|,
name|payload
argument_list|,
literal|""
argument_list|,
name|time
argument_list|,
literal|0
argument_list|,
literal|0
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
literal|"Should be only one log left: "
operator|+
name|getNumJournalFiles
argument_list|()
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
name|getNumJournalFiles
argument_list|()
operator|==
literal|1
return|;
block|}
block|}
argument_list|,
name|TimeUnit
operator|.
name|MINUTES
operator|.
name|toMillis
argument_list|(
literal|2
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"Number of journal log files: {}"
argument_list|,
name|getNumJournalFiles
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

