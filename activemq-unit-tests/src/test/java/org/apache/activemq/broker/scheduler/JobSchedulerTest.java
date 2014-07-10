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
name|util
operator|.
name|Calendar
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
name|JobSchedulerTest
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
name|JobSchedulerTest
operator|.
name|class
argument_list|)
decl_stmt|;
specifier|private
name|JobSchedulerStore
name|store
decl_stmt|;
specifier|private
name|JobScheduler
name|scheduler
decl_stmt|;
annotation|@
name|Test
specifier|public
name|void
name|testAddLongStringByteSequence
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
name|String
name|test
init|=
operator|new
name|String
argument_list|(
literal|"test"
operator|+
name|i
argument_list|)
decl_stmt|;
name|scheduler
operator|.
name|schedule
argument_list|(
literal|"id"
operator|+
name|i
argument_list|,
operator|new
name|ByteSequence
argument_list|(
name|test
operator|.
name|getBytes
argument_list|()
argument_list|)
argument_list|,
literal|1000
argument_list|)
expr_stmt|;
block|}
name|latch
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
block|}
annotation|@
name|Test
specifier|public
name|void
name|testAddCronAndByteSequence
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
name|Calendar
name|current
init|=
name|Calendar
operator|.
name|getInstance
argument_list|()
decl_stmt|;
name|current
operator|.
name|add
argument_list|(
name|Calendar
operator|.
name|MINUTE
argument_list|,
literal|1
argument_list|)
expr_stmt|;
name|int
name|minutes
init|=
name|current
operator|.
name|get
argument_list|(
name|Calendar
operator|.
name|MINUTE
argument_list|)
decl_stmt|;
name|int
name|hour
init|=
name|current
operator|.
name|get
argument_list|(
name|Calendar
operator|.
name|HOUR_OF_DAY
argument_list|)
decl_stmt|;
name|int
name|day
init|=
name|current
operator|.
name|get
argument_list|(
name|Calendar
operator|.
name|DAY_OF_WEEK
argument_list|)
operator|-
literal|1
decl_stmt|;
name|String
name|cronTab
init|=
name|String
operator|.
name|format
argument_list|(
literal|"%d %d * * %d"
argument_list|,
name|minutes
argument_list|,
name|hour
argument_list|,
name|day
argument_list|)
decl_stmt|;
name|String
name|str
init|=
operator|new
name|String
argument_list|(
literal|"test1"
argument_list|)
decl_stmt|;
name|scheduler
operator|.
name|schedule
argument_list|(
literal|"id:1"
argument_list|,
operator|new
name|ByteSequence
argument_list|(
name|str
operator|.
name|getBytes
argument_list|()
argument_list|)
argument_list|,
name|cronTab
argument_list|,
literal|0
argument_list|,
literal|0
argument_list|,
literal|0
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
block|}
annotation|@
name|Test
specifier|public
name|void
name|testAddLongLongIntStringByteSequence
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
literal|2000
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
name|String
name|test
init|=
operator|new
name|String
argument_list|(
literal|"test"
operator|+
name|i
argument_list|)
decl_stmt|;
name|scheduler
operator|.
name|schedule
argument_list|(
literal|"id"
operator|+
name|i
argument_list|,
operator|new
name|ByteSequence
argument_list|(
name|test
operator|.
name|getBytes
argument_list|()
argument_list|)
argument_list|,
literal|""
argument_list|,
name|time
argument_list|,
literal|10
argument_list|,
operator|-
literal|1
argument_list|)
expr_stmt|;
block|}
name|assertTrue
argument_list|(
name|latch
operator|.
name|getCount
argument_list|()
operator|==
name|COUNT
argument_list|)
expr_stmt|;
name|latch
operator|.
name|await
argument_list|(
literal|3000
argument_list|,
name|TimeUnit
operator|.
name|SECONDS
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|latch
operator|.
name|getCount
argument_list|()
operator|==
literal|0
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testAddStopThenDeliver
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
name|long
name|time
init|=
literal|2000
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
name|String
name|test
init|=
operator|new
name|String
argument_list|(
literal|"test"
operator|+
name|i
argument_list|)
decl_stmt|;
name|scheduler
operator|.
name|schedule
argument_list|(
literal|"id"
operator|+
name|i
argument_list|,
operator|new
name|ByteSequence
argument_list|(
name|test
operator|.
name|getBytes
argument_list|()
argument_list|)
argument_list|,
literal|""
argument_list|,
name|time
argument_list|,
literal|1000
argument_list|,
operator|-
literal|1
argument_list|)
expr_stmt|;
block|}
name|File
name|directory
init|=
name|store
operator|.
name|getDirectory
argument_list|()
decl_stmt|;
name|tearDown
argument_list|()
expr_stmt|;
name|startStore
argument_list|(
name|directory
argument_list|)
expr_stmt|;
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
name|assertTrue
argument_list|(
name|latch
operator|.
name|getCount
argument_list|()
operator|==
name|COUNT
argument_list|)
expr_stmt|;
name|latch
operator|.
name|await
argument_list|(
literal|3000
argument_list|,
name|TimeUnit
operator|.
name|SECONDS
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|latch
operator|.
name|getCount
argument_list|()
operator|==
literal|0
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testRemoveLong
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
name|long
name|time
init|=
literal|60000
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
name|String
name|str
init|=
operator|new
name|String
argument_list|(
literal|"test"
operator|+
name|i
argument_list|)
decl_stmt|;
name|scheduler
operator|.
name|schedule
argument_list|(
literal|"id"
operator|+
name|i
argument_list|,
operator|new
name|ByteSequence
argument_list|(
name|str
operator|.
name|getBytes
argument_list|()
argument_list|)
argument_list|,
literal|""
argument_list|,
name|time
argument_list|,
literal|1000
argument_list|,
operator|-
literal|1
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
name|long
name|removeTime
init|=
name|scheduler
operator|.
name|getNextScheduleTime
argument_list|()
decl_stmt|;
name|scheduler
operator|.
name|remove
argument_list|(
name|removeTime
argument_list|)
expr_stmt|;
comment|// If all jobs are not started within the same second we need to call remove again
if|if
condition|(
name|size
operator|!=
literal|0
condition|)
block|{
name|removeTime
operator|=
name|scheduler
operator|.
name|getNextScheduleTime
argument_list|()
expr_stmt|;
name|scheduler
operator|.
name|remove
argument_list|(
name|removeTime
argument_list|)
expr_stmt|;
block|}
name|size
operator|=
name|scheduler
operator|.
name|getAllJobs
argument_list|()
operator|.
name|size
argument_list|()
expr_stmt|;
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|size
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testRemoveString
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
name|String
name|test
init|=
literal|"TESTREMOVE"
decl_stmt|;
name|long
name|time
init|=
literal|20000
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
name|String
name|str
init|=
operator|new
name|String
argument_list|(
literal|"test"
operator|+
name|i
argument_list|)
decl_stmt|;
name|scheduler
operator|.
name|schedule
argument_list|(
literal|"id"
operator|+
name|i
argument_list|,
operator|new
name|ByteSequence
argument_list|(
name|str
operator|.
name|getBytes
argument_list|()
argument_list|)
argument_list|,
literal|""
argument_list|,
name|time
argument_list|,
literal|1000
argument_list|,
operator|-
literal|1
argument_list|)
expr_stmt|;
if|if
condition|(
name|i
operator|==
name|COUNT
operator|/
literal|2
condition|)
block|{
name|scheduler
operator|.
name|schedule
argument_list|(
name|test
argument_list|,
operator|new
name|ByteSequence
argument_list|(
name|test
operator|.
name|getBytes
argument_list|()
argument_list|)
argument_list|,
literal|""
argument_list|,
name|time
argument_list|,
literal|1000
argument_list|,
operator|-
literal|1
argument_list|)
expr_stmt|;
block|}
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
operator|+
literal|1
argument_list|)
expr_stmt|;
name|scheduler
operator|.
name|remove
argument_list|(
name|test
argument_list|)
expr_stmt|;
name|size
operator|=
name|scheduler
operator|.
name|getAllJobs
argument_list|()
operator|.
name|size
argument_list|()
expr_stmt|;
name|assertEquals
argument_list|(
name|size
argument_list|,
name|COUNT
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testGetExecutionCount
parameter_list|()
throws|throws
name|Exception
block|{
specifier|final
name|String
name|jobId
init|=
literal|"Job-1"
decl_stmt|;
name|long
name|time
init|=
literal|10000
decl_stmt|;
specifier|final
name|CountDownLatch
name|done
init|=
operator|new
name|CountDownLatch
argument_list|(
literal|10
argument_list|)
decl_stmt|;
name|String
name|str
init|=
operator|new
name|String
argument_list|(
literal|"test"
argument_list|)
decl_stmt|;
name|scheduler
operator|.
name|schedule
argument_list|(
name|jobId
argument_list|,
operator|new
name|ByteSequence
argument_list|(
name|str
operator|.
name|getBytes
argument_list|()
argument_list|)
argument_list|,
literal|""
argument_list|,
name|time
argument_list|,
literal|1000
argument_list|,
literal|10
argument_list|)
expr_stmt|;
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
literal|1
argument_list|)
expr_stmt|;
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
name|LOG
operator|.
name|info
argument_list|(
literal|"Job exectued: {}"
argument_list|,
literal|11
operator|-
name|done
operator|.
name|getCount
argument_list|()
argument_list|)
expr_stmt|;
name|done
operator|.
name|countDown
argument_list|()
expr_stmt|;
block|}
block|}
argument_list|)
expr_stmt|;
name|List
argument_list|<
name|Job
argument_list|>
name|jobs
init|=
name|scheduler
operator|.
name|getNextScheduleJobs
argument_list|()
decl_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|jobs
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|Job
name|job
init|=
name|jobs
operator|.
name|get
argument_list|(
literal|0
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
name|jobId
argument_list|,
name|job
operator|.
name|getJobId
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|job
operator|.
name|getExecutionCount
argument_list|()
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
literal|"Should have fired ten times."
argument_list|,
name|done
operator|.
name|await
argument_list|(
literal|60
argument_list|,
name|TimeUnit
operator|.
name|SECONDS
argument_list|)
argument_list|)
expr_stmt|;
comment|// The job is not updated on the last firing as it is removed from the store following
comment|// it's last execution so the count will always be one less than the max firings.
name|assertTrue
argument_list|(
name|job
operator|.
name|getExecutionCount
argument_list|()
operator|>=
literal|9
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testgetAllJobs
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
name|String
name|ID
init|=
literal|"id:"
decl_stmt|;
name|long
name|time
init|=
literal|20000
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
name|String
name|str
init|=
operator|new
name|String
argument_list|(
literal|"test"
operator|+
name|i
argument_list|)
decl_stmt|;
name|scheduler
operator|.
name|schedule
argument_list|(
name|ID
operator|+
name|i
argument_list|,
operator|new
name|ByteSequence
argument_list|(
name|str
operator|.
name|getBytes
argument_list|()
argument_list|)
argument_list|,
literal|""
argument_list|,
name|time
argument_list|,
literal|10
operator|+
name|i
argument_list|,
operator|-
literal|1
argument_list|)
expr_stmt|;
block|}
name|List
argument_list|<
name|Job
argument_list|>
name|list
init|=
name|scheduler
operator|.
name|getAllJobs
argument_list|()
decl_stmt|;
name|assertEquals
argument_list|(
name|list
operator|.
name|size
argument_list|()
argument_list|,
name|COUNT
argument_list|)
expr_stmt|;
name|int
name|count
init|=
literal|0
decl_stmt|;
for|for
control|(
name|Job
name|job
range|:
name|list
control|)
block|{
name|assertEquals
argument_list|(
name|job
operator|.
name|getJobId
argument_list|()
argument_list|,
name|ID
operator|+
name|count
argument_list|)
expr_stmt|;
name|count
operator|++
expr_stmt|;
block|}
block|}
annotation|@
name|Test
specifier|public
name|void
name|testgetAllJobsInRange
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
name|String
name|ID
init|=
literal|"id:"
decl_stmt|;
name|long
name|start
init|=
literal|10000
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
name|String
name|str
init|=
operator|new
name|String
argument_list|(
literal|"test"
operator|+
name|i
argument_list|)
decl_stmt|;
name|scheduler
operator|.
name|schedule
argument_list|(
name|ID
operator|+
name|i
argument_list|,
operator|new
name|ByteSequence
argument_list|(
name|str
operator|.
name|getBytes
argument_list|()
argument_list|)
argument_list|,
literal|""
argument_list|,
name|start
operator|+
operator|(
name|i
operator|*
literal|1000
operator|)
argument_list|,
literal|10000
operator|+
name|i
argument_list|,
literal|0
argument_list|)
expr_stmt|;
block|}
name|start
operator|=
name|System
operator|.
name|currentTimeMillis
argument_list|()
expr_stmt|;
name|long
name|finish
init|=
name|start
operator|+
literal|12000
operator|+
operator|(
name|COUNT
operator|*
literal|1000
operator|)
decl_stmt|;
name|List
argument_list|<
name|Job
argument_list|>
name|list
init|=
name|scheduler
operator|.
name|getAllJobs
argument_list|(
name|start
argument_list|,
name|finish
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
name|COUNT
argument_list|,
name|list
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|int
name|count
init|=
literal|0
decl_stmt|;
for|for
control|(
name|Job
name|job
range|:
name|list
control|)
block|{
name|assertEquals
argument_list|(
name|job
operator|.
name|getJobId
argument_list|()
argument_list|,
name|ID
operator|+
name|count
argument_list|)
expr_stmt|;
name|count
operator|++
expr_stmt|;
block|}
block|}
annotation|@
name|Test
specifier|public
name|void
name|testRemoveAllJobsInRange
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
name|String
name|ID
init|=
literal|"id:"
decl_stmt|;
name|long
name|start
init|=
literal|10000
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
name|String
name|str
init|=
operator|new
name|String
argument_list|(
literal|"test"
operator|+
name|i
argument_list|)
decl_stmt|;
name|scheduler
operator|.
name|schedule
argument_list|(
name|ID
operator|+
name|i
argument_list|,
operator|new
name|ByteSequence
argument_list|(
name|str
operator|.
name|getBytes
argument_list|()
argument_list|)
argument_list|,
literal|""
argument_list|,
name|start
operator|+
operator|(
name|i
operator|*
literal|1000
operator|)
argument_list|,
literal|10000
operator|+
name|i
argument_list|,
literal|0
argument_list|)
expr_stmt|;
block|}
name|start
operator|=
name|System
operator|.
name|currentTimeMillis
argument_list|()
expr_stmt|;
name|long
name|finish
init|=
name|start
operator|+
literal|12000
operator|+
operator|(
name|COUNT
operator|*
literal|1000
operator|)
decl_stmt|;
name|scheduler
operator|.
name|removeAllJobs
argument_list|(
name|start
argument_list|,
name|finish
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|scheduler
operator|.
name|getAllJobs
argument_list|()
operator|.
name|isEmpty
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Before
specifier|public
name|void
name|setUp
parameter_list|()
throws|throws
name|Exception
block|{
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
block|}
specifier|protected
name|JobSchedulerStore
name|createJobSchedulerStore
parameter_list|()
throws|throws
name|Exception
block|{
return|return
operator|new
name|JobSchedulerStoreImpl
argument_list|()
return|;
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
name|createJobSchedulerStore
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
block|}
end_class

end_unit

