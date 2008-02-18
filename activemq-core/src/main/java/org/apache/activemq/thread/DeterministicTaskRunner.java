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
specifier|public
class|class
name|DeterministicTaskRunner
implements|implements
name|TaskRunner
block|{
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
name|shutdown
decl_stmt|;
comment|/**Constructor      * @param executor      * @param task      */
specifier|public
name|DeterministicTaskRunner
parameter_list|(
name|Executor
name|executor
parameter_list|,
name|Task
name|task
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
name|task
operator|=
name|task
expr_stmt|;
name|this
operator|.
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
name|Thread
operator|.
name|currentThread
argument_list|()
expr_stmt|;
name|runTask
argument_list|()
expr_stmt|;
block|}
block|}
expr_stmt|;
block|}
comment|/**      * We Expect MANY wakeup calls on the same TaskRunner - but each      * needs to run      */
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
if|if
condition|(
name|shutdown
condition|)
block|{
return|return;
block|}
name|executor
operator|.
name|execute
argument_list|(
name|runable
argument_list|)
expr_stmt|;
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
if|if
condition|(
name|shutdown
condition|)
block|{
name|runable
operator|.
name|notifyAll
argument_list|()
expr_stmt|;
return|return;
block|}
block|}
name|task
operator|.
name|iterate
argument_list|()
expr_stmt|;
block|}
block|}
end_class

end_unit

