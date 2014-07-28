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
operator|.
name|memory
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
name|List
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
name|scheduler
operator|.
name|Job
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
name|scheduler
operator|.
name|JobScheduler
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

begin_comment
comment|/**  *  */
end_comment

begin_class
specifier|public
class|class
name|InMemoryJobSchedulerStoreTest
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
name|InMemoryJobSchedulerStoreTest
operator|.
name|class
argument_list|)
decl_stmt|;
annotation|@
name|Test
argument_list|(
name|timeout
operator|=
literal|120
operator|*
literal|1000
argument_list|)
specifier|public
name|void
name|testRestart
parameter_list|()
throws|throws
name|Exception
block|{
name|InMemoryJobSchedulerStore
name|store
init|=
operator|new
name|InMemoryJobSchedulerStore
argument_list|()
decl_stmt|;
name|File
name|directory
init|=
operator|new
name|File
argument_list|(
literal|"target/test/ScheduledDB"
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
name|store
operator|.
name|setDirectory
argument_list|(
name|directory
argument_list|)
expr_stmt|;
specifier|final
name|int
name|NUMBER
init|=
literal|1000
decl_stmt|;
name|store
operator|.
name|start
argument_list|()
expr_stmt|;
name|List
argument_list|<
name|ByteSequence
argument_list|>
name|list
init|=
operator|new
name|ArrayList
argument_list|<
name|ByteSequence
argument_list|>
argument_list|()
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
name|NUMBER
condition|;
name|i
operator|++
control|)
block|{
name|ByteSequence
name|buff
init|=
operator|new
name|ByteSequence
argument_list|(
operator|new
name|String
argument_list|(
literal|"testjob"
operator|+
name|i
argument_list|)
operator|.
name|getBytes
argument_list|()
argument_list|)
decl_stmt|;
name|list
operator|.
name|add
argument_list|(
name|buff
argument_list|)
expr_stmt|;
block|}
name|JobScheduler
name|js
init|=
name|store
operator|.
name|getJobScheduler
argument_list|(
literal|"test"
argument_list|)
decl_stmt|;
name|js
operator|.
name|startDispatching
argument_list|()
expr_stmt|;
name|int
name|count
init|=
literal|0
decl_stmt|;
name|long
name|startTime
init|=
literal|10
operator|*
literal|60
operator|*
literal|1000
decl_stmt|;
name|long
name|period
init|=
name|startTime
decl_stmt|;
for|for
control|(
name|ByteSequence
name|job
range|:
name|list
control|)
block|{
name|js
operator|.
name|schedule
argument_list|(
literal|"id:"
operator|+
operator|(
name|count
operator|++
operator|)
argument_list|,
name|job
argument_list|,
literal|""
argument_list|,
name|startTime
argument_list|,
name|period
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
name|test
init|=
name|js
operator|.
name|getAllJobs
argument_list|()
decl_stmt|;
name|LOG
operator|.
name|debug
argument_list|(
literal|"Found {} jobs in the store before restart"
argument_list|,
name|test
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|list
operator|.
name|size
argument_list|()
argument_list|,
name|test
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|store
operator|.
name|stop
argument_list|()
expr_stmt|;
name|store
operator|.
name|start
argument_list|()
expr_stmt|;
name|js
operator|=
name|store
operator|.
name|getJobScheduler
argument_list|(
literal|"test"
argument_list|)
expr_stmt|;
name|test
operator|=
name|js
operator|.
name|getAllJobs
argument_list|()
expr_stmt|;
name|LOG
operator|.
name|debug
argument_list|(
literal|"Found {} jobs in the store after restart"
argument_list|,
name|test
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|test
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit
