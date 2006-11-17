begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  *  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  * http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
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
comment|/**  *   * @version $Revision$  */
end_comment

begin_class
specifier|public
class|class
name|DefaultThreadPools
block|{
specifier|private
specifier|static
specifier|final
name|Executor
name|defaultPool
decl_stmt|;
static|static
block|{
name|defaultPool
operator|=
operator|new
name|ScheduledThreadPoolExecutor
argument_list|(
literal|5
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
literal|"ActiveMQ Default Thread Pool Thread"
argument_list|)
decl_stmt|;
name|thread
operator|.
name|setDaemon
argument_list|(
literal|true
argument_list|)
expr_stmt|;
return|return
name|thread
return|;
block|}
block|}
argument_list|)
expr_stmt|;
block|}
specifier|private
specifier|static
specifier|final
name|TaskRunnerFactory
name|defaultTaskRunnerFactory
init|=
operator|new
name|TaskRunnerFactory
argument_list|()
decl_stmt|;
specifier|public
specifier|static
name|Executor
name|getDefaultPool
parameter_list|()
block|{
return|return
name|defaultPool
return|;
block|}
specifier|public
specifier|static
name|TaskRunnerFactory
name|getDefaultTaskRunnerFactory
parameter_list|()
block|{
return|return
name|defaultTaskRunnerFactory
return|;
block|}
block|}
end_class

end_unit

