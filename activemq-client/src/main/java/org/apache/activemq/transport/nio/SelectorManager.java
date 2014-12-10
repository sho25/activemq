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
name|transport
operator|.
name|nio
package|;
end_package

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|IOException
import|;
end_import

begin_import
import|import
name|java
operator|.
name|nio
operator|.
name|channels
operator|.
name|spi
operator|.
name|AbstractSelectableChannel
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|LinkedList
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

begin_comment
comment|/**  * The SelectorManager will manage one Selector and the thread that checks the  * selector.  *  * We may need to consider running more than one thread to check the selector if  * servicing the selector takes too long.  */
end_comment

begin_class
specifier|public
specifier|final
class|class
name|SelectorManager
block|{
specifier|public
specifier|static
specifier|final
name|SelectorManager
name|SINGLETON
init|=
operator|new
name|SelectorManager
argument_list|()
decl_stmt|;
specifier|private
name|Executor
name|selectorExecutor
init|=
name|createDefaultExecutor
argument_list|()
decl_stmt|;
specifier|private
name|Executor
name|channelExecutor
init|=
name|selectorExecutor
decl_stmt|;
specifier|private
specifier|final
name|LinkedList
argument_list|<
name|SelectorWorker
argument_list|>
name|freeWorkers
init|=
operator|new
name|LinkedList
argument_list|<
name|SelectorWorker
argument_list|>
argument_list|()
decl_stmt|;
specifier|private
name|int
name|maxChannelsPerWorker
init|=
literal|1024
decl_stmt|;
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
name|getDefaultCorePoolSize
argument_list|()
argument_list|,
name|getDefaultMaximumPoolSize
argument_list|()
argument_list|,
name|getDefaultKeepAliveTime
argument_list|()
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
specifier|private
name|long
name|i
init|=
literal|0
decl_stmt|;
annotation|@
name|Override
specifier|public
name|Thread
name|newThread
parameter_list|(
name|Runnable
name|runnable
parameter_list|)
block|{
name|this
operator|.
name|i
operator|++
expr_stmt|;
specifier|final
name|Thread
name|t
init|=
operator|new
name|Thread
argument_list|(
name|runnable
argument_list|,
literal|"ActiveMQ NIO Worker "
operator|+
name|this
operator|.
name|i
argument_list|)
decl_stmt|;
return|return
name|t
return|;
block|}
block|}
argument_list|)
decl_stmt|;
return|return
name|rc
return|;
block|}
specifier|private
specifier|static
name|int
name|getDefaultCorePoolSize
parameter_list|()
block|{
return|return
name|Integer
operator|.
name|getInteger
argument_list|(
literal|"org.apache.activemq.transport.nio.SelectorManager.corePoolSize"
argument_list|,
literal|0
argument_list|)
return|;
block|}
specifier|private
specifier|static
name|int
name|getDefaultMaximumPoolSize
parameter_list|()
block|{
return|return
name|Integer
operator|.
name|getInteger
argument_list|(
literal|"org.apache.activemq.transport.nio.SelectorManager.maximumPoolSize"
argument_list|,
name|Integer
operator|.
name|MAX_VALUE
argument_list|)
return|;
block|}
specifier|private
specifier|static
name|int
name|getDefaultKeepAliveTime
parameter_list|()
block|{
return|return
name|Integer
operator|.
name|getInteger
argument_list|(
literal|"org.apache.activemq.transport.nio.SelectorManager.keepAliveTime"
argument_list|,
literal|30
argument_list|)
return|;
block|}
specifier|public
specifier|static
name|SelectorManager
name|getInstance
parameter_list|()
block|{
return|return
name|SINGLETON
return|;
block|}
specifier|public
interface|interface
name|Listener
block|{
name|void
name|onSelect
parameter_list|(
name|SelectorSelection
name|selector
parameter_list|)
function_decl|;
name|void
name|onError
parameter_list|(
name|SelectorSelection
name|selection
parameter_list|,
name|Throwable
name|error
parameter_list|)
function_decl|;
block|}
specifier|public
specifier|synchronized
name|SelectorSelection
name|register
parameter_list|(
name|AbstractSelectableChannel
name|selectableChannel
parameter_list|,
name|Listener
name|listener
parameter_list|)
throws|throws
name|IOException
block|{
name|SelectorSelection
name|selection
init|=
literal|null
decl_stmt|;
while|while
condition|(
name|selection
operator|==
literal|null
condition|)
block|{
if|if
condition|(
name|freeWorkers
operator|.
name|size
argument_list|()
operator|>
literal|0
condition|)
block|{
name|SelectorWorker
name|worker
init|=
name|freeWorkers
operator|.
name|getFirst
argument_list|()
decl_stmt|;
if|if
condition|(
name|worker
operator|.
name|isReleased
argument_list|()
condition|)
block|{
name|freeWorkers
operator|.
name|remove
argument_list|(
name|worker
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|worker
operator|.
name|retain
argument_list|()
expr_stmt|;
name|selection
operator|=
operator|new
name|SelectorSelection
argument_list|(
name|worker
argument_list|,
name|selectableChannel
argument_list|,
name|listener
argument_list|)
expr_stmt|;
block|}
block|}
else|else
block|{
comment|// Worker starts /w retain count of 1
name|SelectorWorker
name|worker
init|=
operator|new
name|SelectorWorker
argument_list|(
name|this
argument_list|)
decl_stmt|;
name|freeWorkers
operator|.
name|addFirst
argument_list|(
name|worker
argument_list|)
expr_stmt|;
name|selection
operator|=
operator|new
name|SelectorSelection
argument_list|(
name|worker
argument_list|,
name|selectableChannel
argument_list|,
name|listener
argument_list|)
expr_stmt|;
block|}
block|}
return|return
name|selection
return|;
block|}
specifier|synchronized
name|void
name|onWorkerFullEvent
parameter_list|(
name|SelectorWorker
name|worker
parameter_list|)
block|{
name|freeWorkers
operator|.
name|remove
argument_list|(
name|worker
argument_list|)
expr_stmt|;
block|}
specifier|public
specifier|synchronized
name|void
name|onWorkerEmptyEvent
parameter_list|(
name|SelectorWorker
name|worker
parameter_list|)
block|{
name|freeWorkers
operator|.
name|remove
argument_list|(
name|worker
argument_list|)
expr_stmt|;
block|}
specifier|public
specifier|synchronized
name|void
name|onWorkerNotFullEvent
parameter_list|(
name|SelectorWorker
name|worker
parameter_list|)
block|{
name|freeWorkers
operator|.
name|addFirst
argument_list|(
name|worker
argument_list|)
expr_stmt|;
block|}
specifier|public
name|Executor
name|getChannelExecutor
parameter_list|()
block|{
return|return
name|channelExecutor
return|;
block|}
specifier|public
name|void
name|setChannelExecutor
parameter_list|(
name|Executor
name|channelExecutor
parameter_list|)
block|{
name|this
operator|.
name|channelExecutor
operator|=
name|channelExecutor
expr_stmt|;
block|}
specifier|public
name|int
name|getMaxChannelsPerWorker
parameter_list|()
block|{
return|return
name|maxChannelsPerWorker
return|;
block|}
specifier|public
name|void
name|setMaxChannelsPerWorker
parameter_list|(
name|int
name|maxChannelsPerWorker
parameter_list|)
block|{
name|this
operator|.
name|maxChannelsPerWorker
operator|=
name|maxChannelsPerWorker
expr_stmt|;
block|}
specifier|public
name|Executor
name|getSelectorExecutor
parameter_list|()
block|{
return|return
name|selectorExecutor
return|;
block|}
specifier|public
name|void
name|setSelectorExecutor
parameter_list|(
name|Executor
name|selectorExecutor
parameter_list|)
block|{
name|this
operator|.
name|selectorExecutor
operator|=
name|selectorExecutor
expr_stmt|;
block|}
block|}
end_class

end_unit

