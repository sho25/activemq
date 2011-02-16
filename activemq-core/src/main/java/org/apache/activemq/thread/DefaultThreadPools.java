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
name|ScheduledThreadPoolExecutor
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

begin_comment
comment|/**  *   *   */
end_comment

begin_class
specifier|public
specifier|final
class|class
name|DefaultThreadPools
block|{
comment|//    private static final Executor DEFAULT_POOL;
comment|//    static {
comment|//        DEFAULT_POOL = new ScheduledThreadPoolExecutor(5, new ThreadFactory() {
comment|//            public Thread newThread(Runnable runnable) {
comment|//                Thread thread = new Thread(runnable, "ActiveMQ Default Thread Pool Thread");
comment|//                thread.setDaemon(true);
comment|//                return thread;
comment|//            }
comment|//        });
comment|//    }
specifier|private
specifier|static
specifier|final
name|TaskRunnerFactory
name|DEFAULT_TASK_RUNNER_FACTORY
init|=
operator|new
name|TaskRunnerFactory
argument_list|()
decl_stmt|;
specifier|private
name|DefaultThreadPools
parameter_list|()
block|{             }
comment|//    public static Executor getDefaultPool() {
comment|//        return DEFAULT_POOL;
comment|//    }
specifier|public
specifier|static
name|TaskRunnerFactory
name|getDefaultTaskRunnerFactory
parameter_list|()
block|{
return|return
name|DEFAULT_TASK_RUNNER_FACTORY
return|;
block|}
comment|/**      * Useful to cleanup when it is known that all brokers and connections are      * close and stopped, eg: when un deploying from web container.      */
specifier|public
specifier|static
name|void
name|shutdown
parameter_list|()
block|{
name|DEFAULT_TASK_RUNNER_FACTORY
operator|.
name|shutdown
argument_list|()
expr_stmt|;
block|}
block|}
end_class

end_unit

