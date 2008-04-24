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
name|thread
package|;
end_package

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|concurrent
operator|.
name|Callable
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
name|ExecutorService
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
name|Executors
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
name|Future
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
name|SynchronousQueue
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
name|ThreadFactory
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
name|ThreadPoolExecutor
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
name|TimeoutException
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
name|AtomicInteger
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

begin_class
specifier|public
class|class
name|PooledTaskRunnerTest
extends|extends
name|TestCase
block|{
specifier|private
name|ExecutorService
name|executor
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
name|super
operator|.
name|setUp
argument_list|()
expr_stmt|;
name|executor
operator|=
name|Executors
operator|.
name|newCachedThreadPool
argument_list|()
expr_stmt|;
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
name|executor
operator|.
name|shutdownNow
argument_list|()
expr_stmt|;
name|super
operator|.
name|tearDown
argument_list|()
expr_stmt|;
block|}
specifier|public
name|void
name|testNormalBehavior
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
name|PooledTaskRunner
name|runner
init|=
operator|new
name|PooledTaskRunner
argument_list|(
name|executor
argument_list|,
operator|new
name|Task
argument_list|()
block|{
specifier|public
name|boolean
name|iterate
parameter_list|()
block|{
name|latch
operator|.
name|countDown
argument_list|()
expr_stmt|;
return|return
literal|false
return|;
block|}
block|}
argument_list|,
literal|1
argument_list|)
decl_stmt|;
name|runner
operator|.
name|wakeup
argument_list|()
expr_stmt|;
name|assertTrue
argument_list|(
name|latch
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
name|runner
operator|.
name|shutdown
argument_list|()
expr_stmt|;
block|}
specifier|public
name|void
name|testWakeupResultsInThreadSafeCalls
parameter_list|()
throws|throws
name|Exception
block|{
name|ThreadPoolExecutor
name|executor
init|=
operator|new
name|ThreadPoolExecutor
argument_list|(
literal|0
argument_list|,
name|Integer
operator|.
name|MAX_VALUE
argument_list|,
literal|10
argument_list|,
name|TimeUnit
operator|.
name|SECONDS
argument_list|,
operator|new
name|SynchronousQueue
argument_list|<
name|Runnable
argument_list|>
argument_list|()
argument_list|,
operator|new
name|ThreadFactory
argument_list|()
block|{
specifier|public
name|Thread
name|newThread
parameter_list|(
name|Runnable
name|runnable
parameter_list|)
block|{
name|Thread
name|thread
init|=
operator|new
name|Thread
argument_list|(
name|runnable
argument_list|,
name|getName
argument_list|()
argument_list|)
decl_stmt|;
name|thread
operator|.
name|setDaemon
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|thread
operator|.
name|setPriority
argument_list|(
name|Thread
operator|.
name|NORM_PRIORITY
argument_list|)
expr_stmt|;
return|return
name|thread
return|;
block|}
block|}
argument_list|)
decl_stmt|;
specifier|final
name|CountDownLatch
name|doneLatch
init|=
operator|new
name|CountDownLatch
argument_list|(
literal|100
argument_list|)
decl_stmt|;
specifier|final
name|AtomicInteger
name|clashCount
init|=
operator|new
name|AtomicInteger
argument_list|()
decl_stmt|;
specifier|final
name|AtomicInteger
name|count
init|=
operator|new
name|AtomicInteger
argument_list|()
decl_stmt|;
specifier|final
name|PooledTaskRunner
name|runner
init|=
operator|new
name|PooledTaskRunner
argument_list|(
name|executor
argument_list|,
operator|new
name|Task
argument_list|()
block|{
name|String
name|threadUnSafeVal
init|=
literal|null
decl_stmt|;
specifier|public
name|boolean
name|iterate
parameter_list|()
block|{
if|if
condition|(
name|threadUnSafeVal
operator|!=
literal|null
condition|)
block|{
name|clashCount
operator|.
name|incrementAndGet
argument_list|()
expr_stmt|;
block|}
name|threadUnSafeVal
operator|=
name|Thread
operator|.
name|currentThread
argument_list|()
operator|.
name|getName
argument_list|()
expr_stmt|;
name|count
operator|.
name|incrementAndGet
argument_list|()
expr_stmt|;
name|doneLatch
operator|.
name|countDown
argument_list|()
expr_stmt|;
if|if
condition|(
operator|!
name|threadUnSafeVal
operator|.
name|equals
argument_list|(
name|Thread
operator|.
name|currentThread
argument_list|()
operator|.
name|getName
argument_list|()
argument_list|)
condition|)
block|{
name|clashCount
operator|.
name|incrementAndGet
argument_list|()
expr_stmt|;
block|}
name|threadUnSafeVal
operator|=
literal|null
expr_stmt|;
return|return
literal|false
return|;
block|}
block|}
argument_list|,
literal|1
argument_list|)
decl_stmt|;
name|Runnable
name|doWakeup
init|=
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
name|runner
operator|.
name|wakeup
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|InterruptedException
name|ignored
parameter_list|)
block|{                 }
block|}
block|}
decl_stmt|;
specifier|final
name|int
name|iterations
init|=
literal|1000
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
name|iterations
condition|;
name|i
operator|++
control|)
block|{
if|if
condition|(
name|i
operator|%
literal|100
operator|==
literal|0
condition|)
block|{
name|Thread
operator|.
name|sleep
argument_list|(
literal|10
argument_list|)
expr_stmt|;
block|}
name|executor
operator|.
name|execute
argument_list|(
name|doWakeup
argument_list|)
expr_stmt|;
block|}
name|doneLatch
operator|.
name|await
argument_list|(
literal|20
argument_list|,
name|TimeUnit
operator|.
name|SECONDS
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"thread safety clash"
argument_list|,
literal|0
argument_list|,
name|clashCount
operator|.
name|get
argument_list|()
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
literal|"called more than once"
argument_list|,
name|count
operator|.
name|get
argument_list|()
operator|>
literal|1
argument_list|)
expr_stmt|;
name|runner
operator|.
name|shutdown
argument_list|()
expr_stmt|;
block|}
specifier|public
name|void
name|testShutsDownAfterRunnerFailure
parameter_list|()
throws|throws
name|Exception
block|{
name|Future
argument_list|<
name|Object
argument_list|>
name|future
init|=
name|executor
operator|.
name|submit
argument_list|(
operator|new
name|Callable
argument_list|<
name|Object
argument_list|>
argument_list|()
block|{
specifier|public
name|Object
name|call
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
name|PooledTaskRunner
name|runner
init|=
operator|new
name|PooledTaskRunner
argument_list|(
name|executor
argument_list|,
operator|new
name|Task
argument_list|()
block|{
specifier|public
name|boolean
name|iterate
parameter_list|()
block|{
name|latch
operator|.
name|countDown
argument_list|()
expr_stmt|;
throw|throw
operator|new
name|RuntimeException
argument_list|()
throw|;
block|}
block|}
argument_list|,
literal|1
argument_list|)
decl_stmt|;
name|runner
operator|.
name|wakeup
argument_list|()
expr_stmt|;
name|assertTrue
argument_list|(
name|latch
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
name|runner
operator|.
name|shutdown
argument_list|()
expr_stmt|;
return|return
literal|null
return|;
block|}
block|}
argument_list|)
decl_stmt|;
try|try
block|{
name|future
operator|.
name|get
argument_list|(
literal|5
argument_list|,
name|TimeUnit
operator|.
name|SECONDS
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|TimeoutException
name|e
parameter_list|)
block|{
name|fail
argument_list|(
literal|"TaskRunner did not shut down cleanly"
argument_list|)
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

