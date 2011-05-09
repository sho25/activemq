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
name|Timer
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|TimerTask
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
name|ServiceStopper
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
name|ServiceSupport
import|;
end_import

begin_comment
comment|/**  *   */
end_comment

begin_class
specifier|public
specifier|final
class|class
name|Scheduler
extends|extends
name|ServiceSupport
block|{
specifier|private
specifier|final
name|String
name|name
decl_stmt|;
specifier|private
name|Timer
name|timer
decl_stmt|;
specifier|private
specifier|final
name|HashMap
argument_list|<
name|Runnable
argument_list|,
name|TimerTask
argument_list|>
name|timerTasks
init|=
operator|new
name|HashMap
argument_list|<
name|Runnable
argument_list|,
name|TimerTask
argument_list|>
argument_list|()
decl_stmt|;
specifier|public
name|Scheduler
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
name|TimerTask
name|timerTask
init|=
operator|new
name|SchedulerTimerTask
argument_list|(
name|task
argument_list|)
decl_stmt|;
name|timer
operator|.
name|scheduleAtFixedRate
argument_list|(
name|timerTask
argument_list|,
name|period
argument_list|,
name|period
argument_list|)
expr_stmt|;
name|timerTasks
operator|.
name|put
argument_list|(
name|task
argument_list|,
name|timerTask
argument_list|)
expr_stmt|;
block|}
comment|/*      * execute on rough schedual based on termination of last execution. There is no      * compensation (two runs in quick succession) for delays      */
specifier|public
specifier|synchronized
name|void
name|schedualPeriodically
parameter_list|(
specifier|final
name|Runnable
name|task
parameter_list|,
name|long
name|period
parameter_list|)
block|{
name|TimerTask
name|timerTask
init|=
operator|new
name|SchedulerTimerTask
argument_list|(
name|task
argument_list|)
decl_stmt|;
name|timer
operator|.
name|schedule
argument_list|(
name|timerTask
argument_list|,
name|period
argument_list|,
name|period
argument_list|)
expr_stmt|;
name|timerTasks
operator|.
name|put
argument_list|(
name|task
argument_list|,
name|timerTask
argument_list|)
expr_stmt|;
block|}
specifier|public
specifier|synchronized
name|void
name|cancel
parameter_list|(
name|Runnable
name|task
parameter_list|)
block|{
name|TimerTask
name|ticket
init|=
name|timerTasks
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
argument_list|()
expr_stmt|;
name|timer
operator|.
name|purge
argument_list|()
expr_stmt|;
comment|//remove cancelled TimerTasks
block|}
block|}
specifier|public
specifier|synchronized
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
name|TimerTask
name|timerTask
init|=
operator|new
name|SchedulerTimerTask
argument_list|(
name|task
argument_list|)
decl_stmt|;
name|timer
operator|.
name|schedule
argument_list|(
name|timerTask
argument_list|,
name|redeliveryDelay
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|shutdown
parameter_list|()
block|{
name|timer
operator|.
name|cancel
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Override
specifier|protected
specifier|synchronized
name|void
name|doStart
parameter_list|()
throws|throws
name|Exception
block|{
name|this
operator|.
name|timer
operator|=
operator|new
name|Timer
argument_list|(
name|name
argument_list|,
literal|true
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|protected
specifier|synchronized
name|void
name|doStop
parameter_list|(
name|ServiceStopper
name|stopper
parameter_list|)
throws|throws
name|Exception
block|{
if|if
condition|(
name|this
operator|.
name|timer
operator|!=
literal|null
condition|)
block|{
name|this
operator|.
name|timer
operator|.
name|cancel
argument_list|()
expr_stmt|;
block|}
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
block|}
end_class

end_unit

