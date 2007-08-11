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
name|HashMap
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
name|ScheduledFuture
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

begin_comment
comment|/**  * @version $Revision$  */
end_comment

begin_class
specifier|public
specifier|final
class|class
name|Scheduler
block|{
specifier|public
specifier|static
specifier|final
name|ScheduledThreadPoolExecutor
name|CLOCK_DAEMON
init|=
operator|new
name|ScheduledThreadPoolExecutor
argument_list|(
literal|5
argument_list|,
name|createThreadFactory
argument_list|()
argument_list|)
decl_stmt|;
static|static
block|{
name|CLOCK_DAEMON
operator|.
name|setKeepAliveTime
argument_list|(
literal|5
argument_list|,
name|TimeUnit
operator|.
name|SECONDS
argument_list|)
expr_stmt|;
block|}
specifier|private
specifier|static
specifier|final
name|HashMap
argument_list|<
name|Runnable
argument_list|,
name|ScheduledFuture
argument_list|>
name|CLOCK_TICKETS
init|=
operator|new
name|HashMap
argument_list|<
name|Runnable
argument_list|,
name|ScheduledFuture
argument_list|>
argument_list|()
decl_stmt|;
specifier|private
name|Scheduler
parameter_list|()
block|{     }
specifier|private
specifier|static
name|ThreadFactory
name|createThreadFactory
parameter_list|()
block|{
return|return
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
literal|"ActiveMQ Scheduler"
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
return|;
block|}
specifier|public
specifier|static
specifier|synchronized
name|void
name|executePeriodically
parameter_list|(
specifier|final
name|Runnable
name|task
parameter_list|,
name|long
name|period
parameter_list|)
block|{
name|ScheduledFuture
name|ticket
init|=
name|CLOCK_DAEMON
operator|.
name|scheduleAtFixedRate
argument_list|(
name|task
argument_list|,
name|period
argument_list|,
name|period
argument_list|,
name|TimeUnit
operator|.
name|MILLISECONDS
argument_list|)
decl_stmt|;
name|CLOCK_TICKETS
operator|.
name|put
argument_list|(
name|task
argument_list|,
name|ticket
argument_list|)
expr_stmt|;
block|}
specifier|public
specifier|static
specifier|synchronized
name|void
name|cancel
parameter_list|(
name|Runnable
name|task
parameter_list|)
block|{
name|ScheduledFuture
name|ticket
init|=
name|CLOCK_TICKETS
operator|.
name|remove
argument_list|(
name|task
argument_list|)
decl_stmt|;
if|if
condition|(
name|ticket
operator|!=
literal|null
condition|)
block|{
name|ticket
operator|.
name|cancel
argument_list|(
literal|false
argument_list|)
expr_stmt|;
if|if
condition|(
name|ticket
operator|instanceof
name|Runnable
condition|)
block|{
name|CLOCK_DAEMON
operator|.
name|remove
argument_list|(
operator|(
name|Runnable
operator|)
name|ticket
argument_list|)
expr_stmt|;
block|}
block|}
block|}
specifier|public
specifier|static
name|void
name|executeAfterDelay
parameter_list|(
specifier|final
name|Runnable
name|task
parameter_list|,
name|long
name|redeliveryDelay
parameter_list|)
block|{
name|CLOCK_DAEMON
operator|.
name|schedule
argument_list|(
name|task
argument_list|,
name|redeliveryDelay
argument_list|,
name|TimeUnit
operator|.
name|MILLISECONDS
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

