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
name|atomic
operator|.
name|AtomicBoolean
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
name|AtomicLong
import|;
end_import

begin_comment
comment|/**  * Manages the thread pool for long running tasks. Long running tasks are not  * always active but when they are active, they may need a few iterations of  * processing for them to become idle. The manager ensures that each task is  * processes but that no one task overtakes the system. This is kinda like  * cooperative multitasking.  *  * @org.apache.xbean.XBean  */
end_comment

begin_class
specifier|public
class|class
name|TaskRunnerFactory
implements|implements
name|Executor
block|{
specifier|private
name|ExecutorService
name|executor
decl_stmt|;
specifier|private
name|int
name|maxIterationsPerRun
decl_stmt|;
specifier|private
name|String
name|name
decl_stmt|;
specifier|private
name|int
name|priority
decl_stmt|;
specifier|private
name|boolean
name|daemon
decl_stmt|;
specifier|private
name|AtomicLong
name|id
init|=
operator|new
name|AtomicLong
argument_list|(
literal|0
argument_list|)
decl_stmt|;
specifier|private
name|boolean
name|dedicatedTaskRunner
decl_stmt|;
specifier|private
name|AtomicBoolean
name|initDone
init|=
operator|new
name|AtomicBoolean
argument_list|(
literal|false
argument_list|)
decl_stmt|;
specifier|public
name|TaskRunnerFactory
parameter_list|()
block|{
name|this
argument_list|(
literal|"ActiveMQ Task"
argument_list|,
name|Thread
operator|.
name|NORM_PRIORITY
argument_list|,
literal|true
argument_list|,
literal|1000
argument_list|)
expr_stmt|;
block|}
specifier|private
name|TaskRunnerFactory
parameter_list|(
name|String
name|name
parameter_list|,
name|int
name|priority
parameter_list|,
name|boolean
name|daemon
parameter_list|,
name|int
name|maxIterationsPerRun
parameter_list|)
block|{
name|this
argument_list|(
name|name
argument_list|,
name|priority
argument_list|,
name|daemon
argument_list|,
name|maxIterationsPerRun
argument_list|,
literal|false
argument_list|)
expr_stmt|;
block|}
specifier|public
name|TaskRunnerFactory
parameter_list|(
name|String
name|name
parameter_list|,
name|int
name|priority
parameter_list|,
name|boolean
name|daemon
parameter_list|,
name|int
name|maxIterationsPerRun
parameter_list|,
name|boolean
name|dedicatedTaskRunner
parameter_list|)
block|{
name|this
operator|.
name|name
operator|=
name|name
expr_stmt|;
name|this
operator|.
name|priority
operator|=
name|priority
expr_stmt|;
name|this
operator|.
name|daemon
operator|=
name|daemon
expr_stmt|;
name|this
operator|.
name|maxIterationsPerRun
operator|=
name|maxIterationsPerRun
expr_stmt|;
name|this
operator|.
name|dedicatedTaskRunner
operator|=
name|dedicatedTaskRunner
expr_stmt|;
block|}
specifier|public
name|void
name|init
parameter_list|()
block|{
if|if
condition|(
name|initDone
operator|.
name|compareAndSet
argument_list|(
literal|false
argument_list|,
literal|true
argument_list|)
condition|)
block|{
comment|// If your OS/JVM combination has a good thread model, you may want to
comment|// avoid using a thread pool to run tasks and use a DedicatedTaskRunner instead.
if|if
condition|(
name|dedicatedTaskRunner
operator|||
literal|"true"
operator|.
name|equalsIgnoreCase
argument_list|(
name|System
operator|.
name|getProperty
argument_list|(
literal|"org.apache.activemq.UseDedicatedTaskRunner"
argument_list|)
argument_list|)
condition|)
block|{
name|executor
operator|=
literal|null
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|executor
operator|==
literal|null
condition|)
block|{
name|executor
operator|=
name|createDefaultExecutor
argument_list|()
expr_stmt|;
block|}
block|}
block|}
specifier|public
name|void
name|shutdown
parameter_list|()
block|{
if|if
condition|(
name|executor
operator|!=
literal|null
condition|)
block|{
name|executor
operator|.
name|shutdownNow
argument_list|()
expr_stmt|;
block|}
name|initDone
operator|.
name|set
argument_list|(
literal|false
argument_list|)
expr_stmt|;
block|}
specifier|public
name|TaskRunner
name|createTaskRunner
parameter_list|(
name|Task
name|task
parameter_list|,
name|String
name|name
parameter_list|)
block|{
name|init
argument_list|()
expr_stmt|;
if|if
condition|(
name|executor
operator|!=
literal|null
condition|)
block|{
return|return
operator|new
name|PooledTaskRunner
argument_list|(
name|executor
argument_list|,
name|task
argument_list|,
name|maxIterationsPerRun
argument_list|)
return|;
block|}
else|else
block|{
return|return
operator|new
name|DedicatedTaskRunner
argument_list|(
name|task
argument_list|,
name|name
argument_list|,
name|priority
argument_list|,
name|daemon
argument_list|)
return|;
block|}
block|}
specifier|public
name|void
name|execute
parameter_list|(
name|Runnable
name|runnable
parameter_list|)
block|{
name|execute
argument_list|(
name|runnable
argument_list|,
literal|"ActiveMQ Task"
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|execute
parameter_list|(
name|Runnable
name|runnable
parameter_list|,
name|String
name|name
parameter_list|)
block|{
name|init
argument_list|()
expr_stmt|;
if|if
condition|(
name|executor
operator|!=
literal|null
condition|)
block|{
name|executor
operator|.
name|execute
argument_list|(
name|runnable
argument_list|)
expr_stmt|;
block|}
else|else
block|{
operator|new
name|Thread
argument_list|(
name|runnable
argument_list|,
name|name
operator|+
literal|"-"
operator|+
name|id
operator|.
name|incrementAndGet
argument_list|()
argument_list|)
operator|.
name|start
argument_list|()
expr_stmt|;
block|}
block|}
specifier|protected
name|ExecutorService
name|createDefaultExecutor
parameter_list|()
block|{
name|ThreadPoolExecutor
name|rc
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
literal|30
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
name|name
operator|+
literal|"-"
operator|+
name|id
operator|.
name|incrementAndGet
argument_list|()
argument_list|)
decl_stmt|;
name|thread
operator|.
name|setDaemon
argument_list|(
name|daemon
argument_list|)
expr_stmt|;
name|thread
operator|.
name|setPriority
argument_list|(
name|priority
argument_list|)
expr_stmt|;
return|return
name|thread
return|;
block|}
block|}
argument_list|)
decl_stmt|;
return|return
name|rc
return|;
block|}
specifier|public
name|ExecutorService
name|getExecutor
parameter_list|()
block|{
return|return
name|executor
return|;
block|}
specifier|public
name|void
name|setExecutor
parameter_list|(
name|ExecutorService
name|executor
parameter_list|)
block|{
name|this
operator|.
name|executor
operator|=
name|executor
expr_stmt|;
block|}
specifier|public
name|int
name|getMaxIterationsPerRun
parameter_list|()
block|{
return|return
name|maxIterationsPerRun
return|;
block|}
specifier|public
name|void
name|setMaxIterationsPerRun
parameter_list|(
name|int
name|maxIterationsPerRun
parameter_list|)
block|{
name|this
operator|.
name|maxIterationsPerRun
operator|=
name|maxIterationsPerRun
expr_stmt|;
block|}
specifier|public
name|String
name|getName
parameter_list|()
block|{
return|return
name|name
return|;
block|}
specifier|public
name|void
name|setName
parameter_list|(
name|String
name|name
parameter_list|)
block|{
name|this
operator|.
name|name
operator|=
name|name
expr_stmt|;
block|}
specifier|public
name|int
name|getPriority
parameter_list|()
block|{
return|return
name|priority
return|;
block|}
specifier|public
name|void
name|setPriority
parameter_list|(
name|int
name|priority
parameter_list|)
block|{
name|this
operator|.
name|priority
operator|=
name|priority
expr_stmt|;
block|}
specifier|public
name|boolean
name|isDaemon
parameter_list|()
block|{
return|return
name|daemon
return|;
block|}
specifier|public
name|void
name|setDaemon
parameter_list|(
name|boolean
name|daemon
parameter_list|)
block|{
name|this
operator|.
name|daemon
operator|=
name|daemon
expr_stmt|;
block|}
specifier|public
name|boolean
name|isDedicatedTaskRunner
parameter_list|()
block|{
return|return
name|dedicatedTaskRunner
return|;
block|}
specifier|public
name|void
name|setDedicatedTaskRunner
parameter_list|(
name|boolean
name|dedicatedTaskRunner
parameter_list|)
block|{
name|this
operator|.
name|dedicatedTaskRunner
operator|=
name|dedicatedTaskRunner
expr_stmt|;
block|}
block|}
end_class

end_unit

