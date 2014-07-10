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
name|java
operator|.
name|io
operator|.
name|File
import|;
end_import

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
name|Map
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
name|locks
operator|.
name|ReentrantLock
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
name|JobScheduler
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
name|JobSchedulerStore
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

begin_import
import|import
name|org
operator|.
name|slf4j
operator|.
name|Logger
import|;
end_import

begin_import
import|import
name|org
operator|.
name|slf4j
operator|.
name|LoggerFactory
import|;
end_import

begin_comment
comment|/**  * An in-memory JobSchedulerStore implementation used for Brokers that have persistence  * disabled or when the JobSchedulerStore usage doesn't require a file or DB based store  * implementation allowing for better performance.  */
end_comment

begin_class
specifier|public
class|class
name|InMemoryJobSchedulerStore
extends|extends
name|ServiceSupport
implements|implements
name|JobSchedulerStore
block|{
specifier|private
specifier|static
specifier|final
name|Logger
name|LOG
init|=
name|LoggerFactory
operator|.
name|getLogger
argument_list|(
name|InMemoryJobSchedulerStore
operator|.
name|class
argument_list|)
decl_stmt|;
specifier|private
specifier|final
name|ReentrantLock
name|lock
init|=
operator|new
name|ReentrantLock
argument_list|()
decl_stmt|;
specifier|private
specifier|final
name|Map
argument_list|<
name|String
argument_list|,
name|InMemoryJobScheduler
argument_list|>
name|schedulers
init|=
operator|new
name|HashMap
argument_list|<
name|String
argument_list|,
name|InMemoryJobScheduler
argument_list|>
argument_list|()
decl_stmt|;
annotation|@
name|Override
specifier|protected
name|void
name|doStop
parameter_list|(
name|ServiceStopper
name|stopper
parameter_list|)
throws|throws
name|Exception
block|{
for|for
control|(
name|InMemoryJobScheduler
name|scheduler
range|:
name|schedulers
operator|.
name|values
argument_list|()
control|)
block|{
try|try
block|{
name|scheduler
operator|.
name|stop
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
name|LOG
operator|.
name|error
argument_list|(
literal|"Failed to stop scheduler: {}"
argument_list|,
name|scheduler
operator|.
name|getName
argument_list|()
argument_list|,
name|e
argument_list|)
expr_stmt|;
block|}
block|}
block|}
annotation|@
name|Override
specifier|protected
name|void
name|doStart
parameter_list|()
throws|throws
name|Exception
block|{
for|for
control|(
name|InMemoryJobScheduler
name|scheduler
range|:
name|schedulers
operator|.
name|values
argument_list|()
control|)
block|{
try|try
block|{
name|scheduler
operator|.
name|start
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
name|LOG
operator|.
name|error
argument_list|(
literal|"Failed to start scheduler: {}"
argument_list|,
name|scheduler
operator|.
name|getName
argument_list|()
argument_list|,
name|e
argument_list|)
expr_stmt|;
block|}
block|}
block|}
annotation|@
name|Override
specifier|public
name|JobScheduler
name|getJobScheduler
parameter_list|(
name|String
name|name
parameter_list|)
throws|throws
name|Exception
block|{
name|this
operator|.
name|lock
operator|.
name|lock
argument_list|()
expr_stmt|;
try|try
block|{
name|InMemoryJobScheduler
name|result
init|=
name|this
operator|.
name|schedulers
operator|.
name|get
argument_list|(
name|name
argument_list|)
decl_stmt|;
if|if
condition|(
name|result
operator|==
literal|null
condition|)
block|{
name|LOG
operator|.
name|debug
argument_list|(
literal|"Creating new in-memory scheduler: {}"
argument_list|,
name|name
argument_list|)
expr_stmt|;
name|result
operator|=
operator|new
name|InMemoryJobScheduler
argument_list|(
name|name
argument_list|)
expr_stmt|;
name|this
operator|.
name|schedulers
operator|.
name|put
argument_list|(
name|name
argument_list|,
name|result
argument_list|)
expr_stmt|;
if|if
condition|(
name|isStarted
argument_list|()
condition|)
block|{
name|result
operator|.
name|start
argument_list|()
expr_stmt|;
block|}
block|}
return|return
name|result
return|;
block|}
finally|finally
block|{
name|this
operator|.
name|lock
operator|.
name|unlock
argument_list|()
expr_stmt|;
block|}
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|removeJobScheduler
parameter_list|(
name|String
name|name
parameter_list|)
throws|throws
name|Exception
block|{
name|boolean
name|result
init|=
literal|false
decl_stmt|;
name|this
operator|.
name|lock
operator|.
name|lock
argument_list|()
expr_stmt|;
try|try
block|{
name|InMemoryJobScheduler
name|scheduler
init|=
name|this
operator|.
name|schedulers
operator|.
name|remove
argument_list|(
name|name
argument_list|)
decl_stmt|;
name|result
operator|=
name|scheduler
operator|!=
literal|null
expr_stmt|;
if|if
condition|(
name|result
condition|)
block|{
name|LOG
operator|.
name|debug
argument_list|(
literal|"Removing in-memory Job Scheduler: {}"
argument_list|,
name|name
argument_list|)
expr_stmt|;
name|scheduler
operator|.
name|stop
argument_list|()
expr_stmt|;
name|this
operator|.
name|schedulers
operator|.
name|remove
argument_list|(
name|name
argument_list|)
expr_stmt|;
block|}
block|}
finally|finally
block|{
name|this
operator|.
name|lock
operator|.
name|unlock
argument_list|()
expr_stmt|;
block|}
return|return
name|result
return|;
block|}
comment|//---------- Methods that don't really apply to this implementation ------//
annotation|@
name|Override
specifier|public
name|long
name|size
parameter_list|()
block|{
return|return
literal|0
return|;
block|}
annotation|@
name|Override
specifier|public
name|File
name|getDirectory
parameter_list|()
block|{
return|return
literal|null
return|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|setDirectory
parameter_list|(
name|File
name|directory
parameter_list|)
block|{     }
block|}
end_class

end_unit

