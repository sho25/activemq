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
class|class
name|Scheduler
block|{
specifier|static
specifier|public
name|ScheduledThreadPoolExecutor
name|clockDaemon
init|=
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
argument_list|)
decl_stmt|;
static|static
block|{
name|clockDaemon
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
specifier|static
name|HashMap
name|clockTickets
init|=
operator|new
name|HashMap
argument_list|()
decl_stmt|;
specifier|synchronized
specifier|static
specifier|public
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
name|clockDaemon
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
name|clockTickets
operator|.
name|put
argument_list|(
name|task
argument_list|,
name|ticket
argument_list|)
expr_stmt|;
block|}
specifier|synchronized
specifier|static
specifier|public
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
operator|(
name|ScheduledFuture
operator|)
name|clockTickets
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
name|clockDaemon
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
name|clockDaemon
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

