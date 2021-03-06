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
operator|.
name|memory
package|;
end_package

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|activemq
operator|.
name|broker
operator|.
name|scheduler
operator|.
name|Job
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
name|broker
operator|.
name|scheduler
operator|.
name|JobSupport
import|;
end_import

begin_comment
comment|/**  * A simple in memory Job POJO.  */
end_comment

begin_class
specifier|public
class|class
name|InMemoryJob
implements|implements
name|Job
block|{
specifier|private
specifier|final
name|String
name|jobId
decl_stmt|;
specifier|private
name|int
name|repeat
decl_stmt|;
specifier|private
name|long
name|start
decl_stmt|;
specifier|private
name|long
name|nextTime
decl_stmt|;
specifier|private
name|long
name|delay
decl_stmt|;
specifier|private
name|long
name|period
decl_stmt|;
specifier|private
name|String
name|cronEntry
decl_stmt|;
specifier|private
name|int
name|executionCount
decl_stmt|;
specifier|private
name|byte
index|[]
name|payload
decl_stmt|;
specifier|public
name|InMemoryJob
parameter_list|(
name|String
name|jobId
parameter_list|)
block|{
name|this
operator|.
name|jobId
operator|=
name|jobId
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|String
name|getJobId
parameter_list|()
block|{
return|return
name|jobId
return|;
block|}
annotation|@
name|Override
specifier|public
name|int
name|getRepeat
parameter_list|()
block|{
return|return
name|repeat
return|;
block|}
specifier|public
name|void
name|setRepeat
parameter_list|(
name|int
name|repeat
parameter_list|)
block|{
name|this
operator|.
name|repeat
operator|=
name|repeat
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|long
name|getStart
parameter_list|()
block|{
return|return
name|start
return|;
block|}
specifier|public
name|void
name|setStart
parameter_list|(
name|long
name|start
parameter_list|)
block|{
name|this
operator|.
name|start
operator|=
name|start
expr_stmt|;
block|}
specifier|public
name|long
name|getNextTime
parameter_list|()
block|{
return|return
name|nextTime
return|;
block|}
specifier|public
name|void
name|setNextTime
parameter_list|(
name|long
name|nextTime
parameter_list|)
block|{
name|this
operator|.
name|nextTime
operator|=
name|nextTime
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|long
name|getDelay
parameter_list|()
block|{
return|return
name|delay
return|;
block|}
specifier|public
name|void
name|setDelay
parameter_list|(
name|long
name|delay
parameter_list|)
block|{
name|this
operator|.
name|delay
operator|=
name|delay
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|long
name|getPeriod
parameter_list|()
block|{
return|return
name|period
return|;
block|}
specifier|public
name|void
name|setPeriod
parameter_list|(
name|long
name|period
parameter_list|)
block|{
name|this
operator|.
name|period
operator|=
name|period
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|String
name|getCronEntry
parameter_list|()
block|{
return|return
name|cronEntry
return|;
block|}
specifier|public
name|void
name|setCronEntry
parameter_list|(
name|String
name|cronEntry
parameter_list|)
block|{
name|this
operator|.
name|cronEntry
operator|=
name|cronEntry
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|byte
index|[]
name|getPayload
parameter_list|()
block|{
return|return
name|payload
return|;
block|}
specifier|public
name|void
name|setPayload
parameter_list|(
name|byte
index|[]
name|payload
parameter_list|)
block|{
name|this
operator|.
name|payload
operator|=
name|payload
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|String
name|getStartTime
parameter_list|()
block|{
return|return
name|JobSupport
operator|.
name|getDateTime
argument_list|(
name|getStart
argument_list|()
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|String
name|getNextExecutionTime
parameter_list|()
block|{
return|return
name|JobSupport
operator|.
name|getDateTime
argument_list|(
name|getNextTime
argument_list|()
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|int
name|getExecutionCount
parameter_list|()
block|{
return|return
name|executionCount
return|;
block|}
specifier|public
name|void
name|incrementExecutionCount
parameter_list|()
block|{
name|this
operator|.
name|executionCount
operator|++
expr_stmt|;
block|}
specifier|public
name|void
name|decrementRepeatCount
parameter_list|()
block|{
if|if
condition|(
name|this
operator|.
name|repeat
operator|>
literal|0
condition|)
block|{
name|this
operator|.
name|repeat
operator|--
expr_stmt|;
block|}
block|}
comment|/**      * @return true if this Job represents a Cron entry.      */
specifier|public
name|boolean
name|isCron
parameter_list|()
block|{
return|return
name|getCronEntry
argument_list|()
operator|!=
literal|null
operator|&&
name|getCronEntry
argument_list|()
operator|.
name|length
argument_list|()
operator|>
literal|0
return|;
block|}
annotation|@
name|Override
specifier|public
name|int
name|hashCode
parameter_list|()
block|{
return|return
name|jobId
operator|.
name|hashCode
argument_list|()
return|;
block|}
annotation|@
name|Override
specifier|public
name|String
name|toString
parameter_list|()
block|{
return|return
literal|"Job: "
operator|+
name|getJobId
argument_list|()
return|;
block|}
block|}
end_class

end_unit

