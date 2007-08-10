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
name|BrokenBarrierException
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
name|CyclicBarrier
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

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|commons
operator|.
name|logging
operator|.
name|Log
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
name|logging
operator|.
name|LogFactory
import|;
end_import

begin_class
specifier|public
class|class
name|TaskRunnerTest
extends|extends
name|TestCase
block|{
specifier|private
specifier|static
specifier|final
name|Log
name|LOG
init|=
name|LogFactory
operator|.
name|getLog
argument_list|(
name|TaskRunnerTest
operator|.
name|class
argument_list|)
decl_stmt|;
specifier|public
name|void
name|testWakeupPooled
parameter_list|()
throws|throws
name|InterruptedException
throws|,
name|BrokenBarrierException
block|{
name|System
operator|.
name|setProperty
argument_list|(
literal|"org.apache.activemq.UseDedicatedTaskRunner"
argument_list|,
literal|"false"
argument_list|)
expr_stmt|;
name|doTestWakeup
argument_list|()
expr_stmt|;
block|}
specifier|public
name|void
name|testWakeupDedicated
parameter_list|()
throws|throws
name|InterruptedException
throws|,
name|BrokenBarrierException
block|{
name|System
operator|.
name|setProperty
argument_list|(
literal|"org.apache.activemq.UseDedicatedTaskRunner"
argument_list|,
literal|"true"
argument_list|)
expr_stmt|;
name|doTestWakeup
argument_list|()
expr_stmt|;
block|}
comment|/**      * Simulate multiple threads queuing work for the TaskRunner. The Task      * Runner dequeues the work.      *       * @throws InterruptedException      * @throws BrokenBarrierException      */
specifier|public
name|void
name|doTestWakeup
parameter_list|()
throws|throws
name|InterruptedException
throws|,
name|BrokenBarrierException
block|{
specifier|final
name|int
name|enqueueCount
init|=
literal|100000
decl_stmt|;
specifier|final
name|AtomicInteger
name|iterations
init|=
operator|new
name|AtomicInteger
argument_list|(
literal|0
argument_list|)
decl_stmt|;
specifier|final
name|AtomicInteger
name|counter
init|=
operator|new
name|AtomicInteger
argument_list|(
literal|0
argument_list|)
decl_stmt|;
specifier|final
name|AtomicInteger
name|queue
init|=
operator|new
name|AtomicInteger
argument_list|(
literal|0
argument_list|)
decl_stmt|;
specifier|final
name|CountDownLatch
name|doneCountDownLatch
init|=
operator|new
name|CountDownLatch
argument_list|(
literal|1
argument_list|)
decl_stmt|;
name|TaskRunnerFactory
name|factory
init|=
operator|new
name|TaskRunnerFactory
argument_list|()
decl_stmt|;
specifier|final
name|TaskRunner
name|runner
init|=
name|factory
operator|.
name|createTaskRunner
argument_list|(
operator|new
name|Task
argument_list|()
block|{
specifier|public
name|boolean
name|iterate
parameter_list|()
block|{
if|if
condition|(
name|queue
operator|.
name|get
argument_list|()
operator|==
literal|0
condition|)
block|{
return|return
literal|false
return|;
block|}
else|else
block|{
while|while
condition|(
name|queue
operator|.
name|get
argument_list|()
operator|>
literal|0
condition|)
block|{
name|queue
operator|.
name|decrementAndGet
argument_list|()
expr_stmt|;
name|counter
operator|.
name|incrementAndGet
argument_list|()
expr_stmt|;
block|}
name|iterations
operator|.
name|incrementAndGet
argument_list|()
expr_stmt|;
if|if
condition|(
name|counter
operator|.
name|get
argument_list|()
operator|==
name|enqueueCount
condition|)
block|{
name|doneCountDownLatch
operator|.
name|countDown
argument_list|()
expr_stmt|;
block|}
return|return
literal|true
return|;
block|}
block|}
block|}
argument_list|,
literal|"Thread Name"
argument_list|)
decl_stmt|;
name|long
name|start
init|=
name|System
operator|.
name|currentTimeMillis
argument_list|()
decl_stmt|;
specifier|final
name|int
name|workerCount
init|=
literal|5
decl_stmt|;
specifier|final
name|CyclicBarrier
name|barrier
init|=
operator|new
name|CyclicBarrier
argument_list|(
name|workerCount
operator|+
literal|1
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
name|workerCount
condition|;
name|i
operator|++
control|)
block|{
operator|new
name|Thread
argument_list|()
block|{
specifier|public
name|void
name|run
parameter_list|()
block|{
try|try
block|{
name|barrier
operator|.
name|await
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
name|enqueueCount
operator|/
name|workerCount
condition|;
name|i
operator|++
control|)
block|{
name|queue
operator|.
name|incrementAndGet
argument_list|()
expr_stmt|;
name|runner
operator|.
name|wakeup
argument_list|()
expr_stmt|;
name|yield
argument_list|()
expr_stmt|;
block|}
block|}
catch|catch
parameter_list|(
name|BrokenBarrierException
name|e
parameter_list|)
block|{                     }
catch|catch
parameter_list|(
name|InterruptedException
name|e
parameter_list|)
block|{                     }
block|}
block|}
operator|.
name|start
argument_list|()
expr_stmt|;
block|}
name|barrier
operator|.
name|await
argument_list|()
expr_stmt|;
name|boolean
name|b
init|=
name|doneCountDownLatch
operator|.
name|await
argument_list|(
literal|30
argument_list|,
name|TimeUnit
operator|.
name|SECONDS
argument_list|)
decl_stmt|;
name|long
name|end
init|=
name|System
operator|.
name|currentTimeMillis
argument_list|()
decl_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"Iterations: "
operator|+
name|iterations
operator|.
name|get
argument_list|()
argument_list|)
expr_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"counter: "
operator|+
name|counter
operator|.
name|get
argument_list|()
argument_list|)
expr_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"Dequeues/s: "
operator|+
operator|(
literal|1000.0
operator|*
name|enqueueCount
operator|/
operator|(
name|end
operator|-
name|start
operator|)
operator|)
argument_list|)
expr_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"duration: "
operator|+
operator|(
operator|(
name|end
operator|-
name|start
operator|)
operator|/
literal|1000.0
operator|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|b
argument_list|)
expr_stmt|;
name|runner
operator|.
name|shutdown
argument_list|()
expr_stmt|;
block|}
specifier|public
specifier|static
name|void
name|main
parameter_list|(
name|String
index|[]
name|args
parameter_list|)
block|{
name|junit
operator|.
name|textui
operator|.
name|TestRunner
operator|.
name|run
argument_list|(
name|TaskRunnerTest
operator|.
name|class
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

