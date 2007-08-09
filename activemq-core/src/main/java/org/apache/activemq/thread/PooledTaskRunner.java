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
name|Executor
import|;
end_import

begin_comment
comment|/**  * @version $Revision: 1.1 $  */
end_comment

begin_class
class|class
name|PooledTaskRunner
implements|implements
name|TaskRunner
block|{
specifier|private
specifier|final
name|int
name|maxIterationsPerRun
decl_stmt|;
specifier|private
specifier|final
name|Executor
name|executor
decl_stmt|;
specifier|private
specifier|final
name|Task
name|task
decl_stmt|;
specifier|private
specifier|final
name|Runnable
name|runable
decl_stmt|;
specifier|private
name|boolean
name|queued
decl_stmt|;
specifier|private
name|boolean
name|shutdown
decl_stmt|;
specifier|private
name|boolean
name|iterating
decl_stmt|;
specifier|private
name|Thread
name|runningThread
decl_stmt|;
specifier|public
name|PooledTaskRunner
parameter_list|(
name|Executor
name|executor
parameter_list|,
name|Task
name|task
parameter_list|,
name|int
name|maxIterationsPerRun
parameter_list|)
block|{
name|this
operator|.
name|executor
operator|=
name|executor
expr_stmt|;
name|this
operator|.
name|maxIterationsPerRun
operator|=
name|maxIterationsPerRun
expr_stmt|;
name|this
operator|.
name|task
operator|=
name|task
expr_stmt|;
name|runable
operator|=
operator|new
name|Runnable
argument_list|()
block|{
specifier|public
name|void
name|run
parameter_list|()
block|{
name|runningThread
operator|=
name|Thread
operator|.
name|currentThread
argument_list|()
expr_stmt|;
name|runTask
argument_list|()
expr_stmt|;
name|runningThread
operator|=
literal|null
expr_stmt|;
block|}
block|}
expr_stmt|;
block|}
comment|/**      * We Expect MANY wakeup calls on the same TaskRunner.      */
specifier|public
name|void
name|wakeup
parameter_list|()
throws|throws
name|InterruptedException
block|{
synchronized|synchronized
init|(
name|runable
init|)
block|{
comment|// When we get in here, we make some assumptions of state:
comment|// queued=false, iterating=false: wakeup() has not be called and
comment|// therefore task is not executing.
comment|// queued=true, iterating=false: wakeup() was called but, task
comment|// execution has not started yet
comment|// queued=false, iterating=true : wakeup() was called, which caused
comment|// task execution to start.
comment|// queued=true, iterating=true : wakeup() called after task
comment|// execution was started.
if|if
condition|(
name|queued
operator|||
name|shutdown
condition|)
block|{
return|return;
block|}
name|queued
operator|=
literal|true
expr_stmt|;
comment|// The runTask() method will do this for me once we are done
comment|// iterating.
if|if
condition|(
operator|!
name|iterating
condition|)
block|{
name|executor
operator|.
name|execute
argument_list|(
name|runable
argument_list|)
expr_stmt|;
block|}
block|}
block|}
comment|/**      * shut down the task      *       * @throws InterruptedException      */
specifier|public
name|void
name|shutdown
parameter_list|(
name|long
name|timeout
parameter_list|)
throws|throws
name|InterruptedException
block|{
synchronized|synchronized
init|(
name|runable
init|)
block|{
name|shutdown
operator|=
literal|true
expr_stmt|;
comment|// the check on the thread is done
comment|// because a call to iterate can result in
comment|// shutDown() being called, which would wait forever
comment|// waiting for iterating to finish
if|if
condition|(
name|runningThread
operator|!=
name|Thread
operator|.
name|currentThread
argument_list|()
condition|)
block|{
if|if
condition|(
name|iterating
condition|)
block|{
name|runable
operator|.
name|wait
argument_list|(
name|timeout
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|}
specifier|public
name|void
name|shutdown
parameter_list|()
throws|throws
name|InterruptedException
block|{
name|shutdown
argument_list|(
literal|0
argument_list|)
expr_stmt|;
block|}
specifier|final
name|void
name|runTask
parameter_list|()
block|{
synchronized|synchronized
init|(
name|runable
init|)
block|{
name|queued
operator|=
literal|false
expr_stmt|;
if|if
condition|(
name|shutdown
condition|)
block|{
name|iterating
operator|=
literal|false
expr_stmt|;
name|runable
operator|.
name|notifyAll
argument_list|()
expr_stmt|;
return|return;
block|}
name|iterating
operator|=
literal|true
expr_stmt|;
block|}
comment|// Don't synchronize while we are iterating so that
comment|// multiple wakeup() calls can be executed concurrently.
name|boolean
name|done
init|=
literal|false
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
name|maxIterationsPerRun
condition|;
name|i
operator|++
control|)
block|{
if|if
condition|(
operator|!
name|task
operator|.
name|iterate
argument_list|()
condition|)
block|{
name|done
operator|=
literal|true
expr_stmt|;
break|break;
block|}
block|}
synchronized|synchronized
init|(
name|runable
init|)
block|{
name|iterating
operator|=
literal|false
expr_stmt|;
if|if
condition|(
name|shutdown
condition|)
block|{
name|queued
operator|=
literal|false
expr_stmt|;
name|runable
operator|.
name|notifyAll
argument_list|()
expr_stmt|;
return|return;
block|}
comment|// If we could not iterate all the items
comment|// then we need to re-queue.
if|if
condition|(
operator|!
name|done
condition|)
block|{
name|queued
operator|=
literal|true
expr_stmt|;
block|}
if|if
condition|(
name|queued
condition|)
block|{
name|executor
operator|.
name|execute
argument_list|(
name|runable
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|}
end_class

end_unit

