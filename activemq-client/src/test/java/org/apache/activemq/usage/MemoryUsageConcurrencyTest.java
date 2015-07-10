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
name|usage
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
name|assertNotNull
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
name|java
operator|.
name|util
operator|.
name|Random
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
name|ArrayBlockingQueue
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
name|BlockingQueue
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
name|MemoryUsageConcurrencyTest
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
name|MemoryUsageConcurrencyTest
operator|.
name|class
argument_list|)
decl_stmt|;
annotation|@
name|Test
specifier|public
name|void
name|testCycle
parameter_list|()
throws|throws
name|Exception
block|{
name|Random
name|r
init|=
operator|new
name|Random
argument_list|(
literal|0xb4a14
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
literal|30000
condition|;
name|i
operator|++
control|)
block|{
name|checkPercentage
argument_list|(
name|i
argument_list|,
name|i
argument_list|,
name|r
operator|.
name|nextInt
argument_list|(
literal|100
argument_list|)
operator|+
literal|10
argument_list|,
name|i
operator|%
literal|2
operator|==
literal|0
argument_list|,
name|i
operator|%
literal|5
operator|==
literal|0
argument_list|)
expr_stmt|;
block|}
block|}
specifier|private
name|void
name|checkPercentage
parameter_list|(
name|int
name|attempt
parameter_list|,
name|int
name|seed
parameter_list|,
name|int
name|operations
parameter_list|,
name|boolean
name|useArrayBlocking
parameter_list|,
name|boolean
name|useWaitForSpaceThread
parameter_list|)
throws|throws
name|InterruptedException
block|{
specifier|final
name|BlockingQueue
argument_list|<
name|Integer
argument_list|>
name|toAdd
decl_stmt|;
specifier|final
name|BlockingQueue
argument_list|<
name|Integer
argument_list|>
name|toRemove
decl_stmt|;
specifier|final
name|BlockingQueue
argument_list|<
name|Integer
argument_list|>
name|removed
decl_stmt|;
if|if
condition|(
name|useArrayBlocking
condition|)
block|{
name|toAdd
operator|=
operator|new
name|ArrayBlockingQueue
argument_list|<
name|Integer
argument_list|>
argument_list|(
name|operations
argument_list|)
expr_stmt|;
name|toRemove
operator|=
operator|new
name|ArrayBlockingQueue
argument_list|<
name|Integer
argument_list|>
argument_list|(
name|operations
argument_list|)
expr_stmt|;
name|removed
operator|=
operator|new
name|ArrayBlockingQueue
argument_list|<
name|Integer
argument_list|>
argument_list|(
name|operations
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|toAdd
operator|=
operator|new
name|LinkedBlockingQueue
argument_list|<
name|Integer
argument_list|>
argument_list|()
expr_stmt|;
name|toRemove
operator|=
operator|new
name|LinkedBlockingQueue
argument_list|<
name|Integer
argument_list|>
argument_list|()
expr_stmt|;
name|removed
operator|=
operator|new
name|LinkedBlockingQueue
argument_list|<
name|Integer
argument_list|>
argument_list|()
expr_stmt|;
block|}
specifier|final
name|AtomicBoolean
name|running
init|=
operator|new
name|AtomicBoolean
argument_list|(
literal|true
argument_list|)
decl_stmt|;
specifier|final
name|CountDownLatch
name|startLatch
init|=
operator|new
name|CountDownLatch
argument_list|(
literal|1
argument_list|)
decl_stmt|;
specifier|final
name|MemoryUsage
name|memUsage
init|=
operator|new
name|MemoryUsage
argument_list|()
decl_stmt|;
name|memUsage
operator|.
name|setLimit
argument_list|(
literal|1000
argument_list|)
expr_stmt|;
name|memUsage
operator|.
name|start
argument_list|()
expr_stmt|;
name|Thread
name|addThread
init|=
operator|new
name|Thread
argument_list|(
operator|new
name|Runnable
argument_list|()
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
name|startLatch
operator|.
name|await
argument_list|()
expr_stmt|;
while|while
condition|(
literal|true
condition|)
block|{
name|Integer
name|add
init|=
name|toAdd
operator|.
name|poll
argument_list|(
literal|1
argument_list|,
name|TimeUnit
operator|.
name|MILLISECONDS
argument_list|)
decl_stmt|;
if|if
condition|(
name|add
operator|==
literal|null
condition|)
block|{
if|if
condition|(
operator|!
name|running
operator|.
name|get
argument_list|()
condition|)
block|{
break|break;
block|}
block|}
else|else
block|{
comment|// add to other queue before removing
name|toRemove
operator|.
name|add
argument_list|(
name|add
argument_list|)
expr_stmt|;
name|memUsage
operator|.
name|increaseUsage
argument_list|(
name|add
argument_list|)
expr_stmt|;
block|}
block|}
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
argument_list|)
decl_stmt|;
name|Thread
name|removeThread
init|=
operator|new
name|Thread
argument_list|(
operator|new
name|Runnable
argument_list|()
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
name|startLatch
operator|.
name|await
argument_list|()
expr_stmt|;
while|while
condition|(
literal|true
condition|)
block|{
name|Integer
name|remove
init|=
name|toRemove
operator|.
name|poll
argument_list|(
literal|1
argument_list|,
name|TimeUnit
operator|.
name|MILLISECONDS
argument_list|)
decl_stmt|;
if|if
condition|(
name|remove
operator|==
literal|null
condition|)
block|{
if|if
condition|(
operator|!
name|running
operator|.
name|get
argument_list|()
condition|)
block|{
break|break;
block|}
block|}
else|else
block|{
name|memUsage
operator|.
name|decreaseUsage
argument_list|(
name|remove
argument_list|)
expr_stmt|;
name|removed
operator|.
name|add
argument_list|(
name|remove
argument_list|)
expr_stmt|;
block|}
block|}
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
argument_list|)
decl_stmt|;
name|Thread
name|waitForSpaceThread
init|=
operator|new
name|Thread
argument_list|(
operator|new
name|Runnable
argument_list|()
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
name|startLatch
operator|.
name|await
argument_list|()
expr_stmt|;
while|while
condition|(
name|running
operator|.
name|get
argument_list|()
condition|)
block|{
name|memUsage
operator|.
name|waitForSpace
argument_list|()
expr_stmt|;
block|}
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
argument_list|)
decl_stmt|;
name|removeThread
operator|.
name|start
argument_list|()
expr_stmt|;
name|addThread
operator|.
name|start
argument_list|()
expr_stmt|;
if|if
condition|(
name|useWaitForSpaceThread
condition|)
block|{
name|waitForSpaceThread
operator|.
name|start
argument_list|()
expr_stmt|;
block|}
name|Random
name|r
init|=
operator|new
name|Random
argument_list|(
name|seed
argument_list|)
decl_stmt|;
name|startLatch
operator|.
name|countDown
argument_list|()
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
name|operations
condition|;
name|i
operator|++
control|)
block|{
name|toAdd
operator|.
name|add
argument_list|(
name|r
operator|.
name|nextInt
argument_list|(
literal|100
argument_list|)
operator|+
literal|1
argument_list|)
expr_stmt|;
block|}
comment|// we expect the failure percentage to be related to the last operation
name|List
argument_list|<
name|Integer
argument_list|>
name|ops
init|=
operator|new
name|ArrayList
argument_list|<
name|Integer
argument_list|>
argument_list|(
name|operations
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
name|operations
condition|;
name|i
operator|++
control|)
block|{
name|Integer
name|op
init|=
name|removed
operator|.
name|poll
argument_list|(
literal|1000
argument_list|,
name|TimeUnit
operator|.
name|MILLISECONDS
argument_list|)
decl_stmt|;
name|assertNotNull
argument_list|(
name|op
argument_list|)
expr_stmt|;
name|ops
operator|.
name|add
argument_list|(
name|op
argument_list|)
expr_stmt|;
block|}
name|running
operator|.
name|set
argument_list|(
literal|false
argument_list|)
expr_stmt|;
if|if
condition|(
name|useWaitForSpaceThread
condition|)
block|{
try|try
block|{
name|waitForSpaceThread
operator|.
name|join
argument_list|(
literal|1000
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|InterruptedException
name|e
parameter_list|)
block|{
name|LOG
operator|.
name|debug
argument_list|(
literal|"Attempt: {} : {} waitForSpace never returned"
argument_list|,
name|attempt
argument_list|,
name|memUsage
argument_list|)
expr_stmt|;
name|waitForSpaceThread
operator|.
name|interrupt
argument_list|()
expr_stmt|;
name|waitForSpaceThread
operator|.
name|join
argument_list|()
expr_stmt|;
block|}
block|}
name|removeThread
operator|.
name|join
argument_list|()
expr_stmt|;
name|addThread
operator|.
name|join
argument_list|()
expr_stmt|;
if|if
condition|(
name|memUsage
operator|.
name|getPercentUsage
argument_list|()
operator|!=
literal|0
operator|||
name|memUsage
operator|.
name|getUsage
argument_list|()
operator|!=
name|memUsage
operator|.
name|getPercentUsage
argument_list|()
condition|)
block|{
name|LOG
operator|.
name|debug
argument_list|(
literal|"Attempt: {} : {}"
argument_list|,
name|attempt
argument_list|,
name|memUsage
argument_list|)
expr_stmt|;
name|LOG
operator|.
name|debug
argument_list|(
literal|"Operations: {}"
argument_list|,
name|ops
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|memUsage
operator|.
name|getPercentUsage
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

