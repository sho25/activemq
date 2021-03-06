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
name|broker
operator|.
name|scheduler
package|;
end_package

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Collections
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
name|org
operator|.
name|apache
operator|.
name|activemq
operator|.
name|util
operator|.
name|ByteSequence
import|;
end_import

begin_comment
comment|/**  * A wrapper for instances of the JobScheduler interface that ensures that methods  * provides safe and sane return values and can deal with null values being passed  * in etc.  Provides a measure of safety when using unknown implementations of the  * JobSchedulerStore which might not always do the right thing.  */
end_comment

begin_class
specifier|public
class|class
name|JobSchedulerFacade
implements|implements
name|JobScheduler
block|{
specifier|private
specifier|final
name|SchedulerBroker
name|broker
decl_stmt|;
name|JobSchedulerFacade
parameter_list|(
name|SchedulerBroker
name|broker
parameter_list|)
block|{
name|this
operator|.
name|broker
operator|=
name|broker
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|addListener
parameter_list|(
name|JobListener
name|l
parameter_list|)
throws|throws
name|Exception
block|{
name|JobScheduler
name|js
init|=
name|this
operator|.
name|broker
operator|.
name|getInternalScheduler
argument_list|()
decl_stmt|;
if|if
condition|(
name|js
operator|!=
literal|null
condition|)
block|{
name|js
operator|.
name|addListener
argument_list|(
name|l
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Override
specifier|public
name|List
argument_list|<
name|Job
argument_list|>
name|getAllJobs
parameter_list|()
throws|throws
name|Exception
block|{
name|JobScheduler
name|js
init|=
name|this
operator|.
name|broker
operator|.
name|getInternalScheduler
argument_list|()
decl_stmt|;
if|if
condition|(
name|js
operator|!=
literal|null
condition|)
block|{
return|return
name|js
operator|.
name|getAllJobs
argument_list|()
return|;
block|}
return|return
name|Collections
operator|.
name|emptyList
argument_list|()
return|;
block|}
annotation|@
name|Override
specifier|public
name|List
argument_list|<
name|Job
argument_list|>
name|getAllJobs
parameter_list|(
name|long
name|start
parameter_list|,
name|long
name|finish
parameter_list|)
throws|throws
name|Exception
block|{
name|JobScheduler
name|js
init|=
name|this
operator|.
name|broker
operator|.
name|getInternalScheduler
argument_list|()
decl_stmt|;
if|if
condition|(
name|js
operator|!=
literal|null
condition|)
block|{
return|return
name|js
operator|.
name|getAllJobs
argument_list|(
name|start
argument_list|,
name|finish
argument_list|)
return|;
block|}
return|return
name|Collections
operator|.
name|emptyList
argument_list|()
return|;
block|}
annotation|@
name|Override
specifier|public
name|String
name|getName
parameter_list|()
throws|throws
name|Exception
block|{
name|JobScheduler
name|js
init|=
name|this
operator|.
name|broker
operator|.
name|getInternalScheduler
argument_list|()
decl_stmt|;
if|if
condition|(
name|js
operator|!=
literal|null
condition|)
block|{
return|return
name|js
operator|.
name|getName
argument_list|()
return|;
block|}
return|return
literal|""
return|;
block|}
annotation|@
name|Override
specifier|public
name|List
argument_list|<
name|Job
argument_list|>
name|getNextScheduleJobs
parameter_list|()
throws|throws
name|Exception
block|{
name|JobScheduler
name|js
init|=
name|this
operator|.
name|broker
operator|.
name|getInternalScheduler
argument_list|()
decl_stmt|;
if|if
condition|(
name|js
operator|!=
literal|null
condition|)
block|{
return|return
name|js
operator|.
name|getNextScheduleJobs
argument_list|()
return|;
block|}
return|return
name|Collections
operator|.
name|emptyList
argument_list|()
return|;
block|}
annotation|@
name|Override
specifier|public
name|long
name|getNextScheduleTime
parameter_list|()
throws|throws
name|Exception
block|{
name|JobScheduler
name|js
init|=
name|this
operator|.
name|broker
operator|.
name|getInternalScheduler
argument_list|()
decl_stmt|;
if|if
condition|(
name|js
operator|!=
literal|null
condition|)
block|{
return|return
name|js
operator|.
name|getNextScheduleTime
argument_list|()
return|;
block|}
return|return
literal|0
return|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|remove
parameter_list|(
name|long
name|time
parameter_list|)
throws|throws
name|Exception
block|{
name|JobScheduler
name|js
init|=
name|this
operator|.
name|broker
operator|.
name|getInternalScheduler
argument_list|()
decl_stmt|;
if|if
condition|(
name|js
operator|!=
literal|null
condition|)
block|{
name|js
operator|.
name|remove
argument_list|(
name|time
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Override
specifier|public
name|void
name|remove
parameter_list|(
name|String
name|jobId
parameter_list|)
throws|throws
name|Exception
block|{
name|JobScheduler
name|js
init|=
name|this
operator|.
name|broker
operator|.
name|getInternalScheduler
argument_list|()
decl_stmt|;
if|if
condition|(
name|js
operator|!=
literal|null
condition|)
block|{
name|js
operator|.
name|remove
argument_list|(
name|jobId
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Override
specifier|public
name|void
name|removeAllJobs
parameter_list|()
throws|throws
name|Exception
block|{
name|JobScheduler
name|js
init|=
name|this
operator|.
name|broker
operator|.
name|getInternalScheduler
argument_list|()
decl_stmt|;
if|if
condition|(
name|js
operator|!=
literal|null
condition|)
block|{
name|js
operator|.
name|removeAllJobs
argument_list|()
expr_stmt|;
block|}
block|}
annotation|@
name|Override
specifier|public
name|void
name|removeAllJobs
parameter_list|(
name|long
name|start
parameter_list|,
name|long
name|finish
parameter_list|)
throws|throws
name|Exception
block|{
name|JobScheduler
name|js
init|=
name|this
operator|.
name|broker
operator|.
name|getInternalScheduler
argument_list|()
decl_stmt|;
if|if
condition|(
name|js
operator|!=
literal|null
condition|)
block|{
name|js
operator|.
name|removeAllJobs
argument_list|(
name|start
argument_list|,
name|finish
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Override
specifier|public
name|void
name|removeListener
parameter_list|(
name|JobListener
name|l
parameter_list|)
throws|throws
name|Exception
block|{
name|JobScheduler
name|js
init|=
name|this
operator|.
name|broker
operator|.
name|getInternalScheduler
argument_list|()
decl_stmt|;
if|if
condition|(
name|js
operator|!=
literal|null
condition|)
block|{
name|js
operator|.
name|removeListener
argument_list|(
name|l
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Override
specifier|public
name|void
name|schedule
parameter_list|(
name|String
name|jobId
parameter_list|,
name|ByteSequence
name|payload
parameter_list|,
name|long
name|delay
parameter_list|)
throws|throws
name|Exception
block|{
name|JobScheduler
name|js
init|=
name|this
operator|.
name|broker
operator|.
name|getInternalScheduler
argument_list|()
decl_stmt|;
if|if
condition|(
name|js
operator|!=
literal|null
condition|)
block|{
name|js
operator|.
name|schedule
argument_list|(
name|jobId
argument_list|,
name|payload
argument_list|,
name|delay
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Override
specifier|public
name|void
name|schedule
parameter_list|(
name|String
name|jobId
parameter_list|,
name|ByteSequence
name|payload
parameter_list|,
name|String
name|cronEntry
parameter_list|,
name|long
name|start
parameter_list|,
name|long
name|period
parameter_list|,
name|int
name|repeat
parameter_list|)
throws|throws
name|Exception
block|{
name|JobScheduler
name|js
init|=
name|this
operator|.
name|broker
operator|.
name|getInternalScheduler
argument_list|()
decl_stmt|;
if|if
condition|(
name|js
operator|!=
literal|null
condition|)
block|{
name|js
operator|.
name|schedule
argument_list|(
name|jobId
argument_list|,
name|payload
argument_list|,
name|cronEntry
argument_list|,
name|start
argument_list|,
name|period
argument_list|,
name|repeat
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Override
specifier|public
name|void
name|schedule
parameter_list|(
name|String
name|jobId
parameter_list|,
name|ByteSequence
name|payload
parameter_list|,
name|String
name|cronEntry
parameter_list|)
throws|throws
name|Exception
block|{
name|JobScheduler
name|js
init|=
name|this
operator|.
name|broker
operator|.
name|getInternalScheduler
argument_list|()
decl_stmt|;
if|if
condition|(
name|js
operator|!=
literal|null
condition|)
block|{
name|js
operator|.
name|schedule
argument_list|(
name|jobId
argument_list|,
name|payload
argument_list|,
name|cronEntry
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Override
specifier|public
name|void
name|startDispatching
parameter_list|()
throws|throws
name|Exception
block|{
name|JobScheduler
name|js
init|=
name|this
operator|.
name|broker
operator|.
name|getInternalScheduler
argument_list|()
decl_stmt|;
if|if
condition|(
name|js
operator|!=
literal|null
condition|)
block|{
name|js
operator|.
name|startDispatching
argument_list|()
expr_stmt|;
block|}
block|}
annotation|@
name|Override
specifier|public
name|void
name|stopDispatching
parameter_list|()
throws|throws
name|Exception
block|{
name|JobScheduler
name|js
init|=
name|this
operator|.
name|broker
operator|.
name|getInternalScheduler
argument_list|()
decl_stmt|;
if|if
condition|(
name|js
operator|!=
literal|null
condition|)
block|{
name|js
operator|.
name|stopDispatching
argument_list|()
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

