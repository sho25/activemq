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
name|SelectionKey
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
name|Selector
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Iterator
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Set
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
name|ConcurrentLinkedQueue
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
name|atomic
operator|.
name|AtomicInteger
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

begin_class
specifier|public
class|class
name|SelectorWorker
implements|implements
name|Runnable
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
name|SelectorWorker
operator|.
name|class
argument_list|)
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|AtomicInteger
name|NEXT_ID
init|=
operator|new
name|AtomicInteger
argument_list|()
decl_stmt|;
specifier|final
name|SelectorManager
name|manager
decl_stmt|;
specifier|final
name|Selector
name|selector
decl_stmt|;
specifier|final
name|int
name|id
init|=
name|NEXT_ID
operator|.
name|getAndIncrement
argument_list|()
decl_stmt|;
specifier|private
specifier|final
name|int
name|maxChannelsPerWorker
decl_stmt|;
specifier|final
name|AtomicInteger
name|retainCounter
init|=
operator|new
name|AtomicInteger
argument_list|(
literal|1
argument_list|)
decl_stmt|;
specifier|private
specifier|final
name|ConcurrentLinkedQueue
argument_list|<
name|Runnable
argument_list|>
name|ioTasks
init|=
operator|new
name|ConcurrentLinkedQueue
argument_list|<
name|Runnable
argument_list|>
argument_list|()
decl_stmt|;
specifier|public
name|SelectorWorker
parameter_list|(
name|SelectorManager
name|manager
parameter_list|)
throws|throws
name|IOException
block|{
name|this
operator|.
name|manager
operator|=
name|manager
expr_stmt|;
name|selector
operator|=
name|Selector
operator|.
name|open
argument_list|()
expr_stmt|;
name|maxChannelsPerWorker
operator|=
name|manager
operator|.
name|getMaxChannelsPerWorker
argument_list|()
expr_stmt|;
name|manager
operator|.
name|getSelectorExecutor
argument_list|()
operator|.
name|execute
argument_list|(
name|this
argument_list|)
expr_stmt|;
block|}
name|void
name|retain
parameter_list|()
block|{
if|if
condition|(
name|retainCounter
operator|.
name|incrementAndGet
argument_list|()
operator|==
name|maxChannelsPerWorker
condition|)
block|{
name|manager
operator|.
name|onWorkerFullEvent
argument_list|(
name|this
argument_list|)
expr_stmt|;
block|}
block|}
name|void
name|release
parameter_list|()
block|{
name|int
name|use
init|=
name|retainCounter
operator|.
name|decrementAndGet
argument_list|()
decl_stmt|;
if|if
condition|(
name|use
operator|==
literal|0
condition|)
block|{
name|manager
operator|.
name|onWorkerEmptyEvent
argument_list|(
name|this
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|use
operator|==
name|maxChannelsPerWorker
operator|-
literal|1
condition|)
block|{
name|manager
operator|.
name|onWorkerNotFullEvent
argument_list|(
name|this
argument_list|)
expr_stmt|;
block|}
block|}
name|boolean
name|isReleased
parameter_list|()
block|{
return|return
name|retainCounter
operator|.
name|get
argument_list|()
operator|==
literal|0
return|;
block|}
specifier|public
name|void
name|addIoTask
parameter_list|(
name|Runnable
name|work
parameter_list|)
block|{
name|ioTasks
operator|.
name|add
argument_list|(
name|work
argument_list|)
expr_stmt|;
name|selector
operator|.
name|wakeup
argument_list|()
expr_stmt|;
block|}
specifier|private
name|void
name|processIoTasks
parameter_list|()
block|{
name|Runnable
name|task
decl_stmt|;
while|while
condition|(
operator|(
name|task
operator|=
name|ioTasks
operator|.
name|poll
argument_list|()
operator|)
operator|!=
literal|null
condition|)
block|{
try|try
block|{
name|task
operator|.
name|run
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Throwable
name|e
parameter_list|)
block|{
name|LOG
operator|.
name|debug
argument_list|(
name|e
operator|.
name|getMessage
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
name|void
name|run
parameter_list|()
block|{
name|String
name|origName
init|=
name|Thread
operator|.
name|currentThread
argument_list|()
operator|.
name|getName
argument_list|()
decl_stmt|;
try|try
block|{
name|Thread
operator|.
name|currentThread
argument_list|()
operator|.
name|setName
argument_list|(
literal|"Selector Worker: "
operator|+
name|id
argument_list|)
expr_stmt|;
while|while
condition|(
operator|!
name|isReleased
argument_list|()
condition|)
block|{
name|processIoTasks
argument_list|()
expr_stmt|;
name|int
name|count
init|=
name|selector
operator|.
name|select
argument_list|(
literal|10
argument_list|)
decl_stmt|;
if|if
condition|(
name|count
operator|==
literal|0
condition|)
block|{
continue|continue;
block|}
comment|// Get a java.util.Set containing the SelectionKey objects
comment|// for all channels that are ready for I/O.
name|Set
argument_list|<
name|SelectionKey
argument_list|>
name|keys
init|=
name|selector
operator|.
name|selectedKeys
argument_list|()
decl_stmt|;
for|for
control|(
name|Iterator
argument_list|<
name|SelectionKey
argument_list|>
name|i
init|=
name|keys
operator|.
name|iterator
argument_list|()
init|;
name|i
operator|.
name|hasNext
argument_list|()
condition|;
control|)
block|{
specifier|final
name|SelectionKey
name|key
init|=
name|i
operator|.
name|next
argument_list|()
decl_stmt|;
name|i
operator|.
name|remove
argument_list|()
expr_stmt|;
specifier|final
name|SelectorSelection
name|s
init|=
operator|(
name|SelectorSelection
operator|)
name|key
operator|.
name|attachment
argument_list|()
decl_stmt|;
try|try
block|{
if|if
condition|(
name|key
operator|.
name|isValid
argument_list|()
condition|)
block|{
name|key
operator|.
name|interestOps
argument_list|(
literal|0
argument_list|)
expr_stmt|;
block|}
comment|// Kick off another thread to find newly selected keys
comment|// while we process the
comment|// currently selected keys
name|manager
operator|.
name|getChannelExecutor
argument_list|()
operator|.
name|execute
argument_list|(
operator|new
name|Runnable
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|void
name|run
parameter_list|()
block|{
try|try
block|{
name|s
operator|.
name|onSelect
argument_list|()
expr_stmt|;
name|s
operator|.
name|enable
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Throwable
name|e
parameter_list|)
block|{
name|s
operator|.
name|onError
argument_list|(
name|e
argument_list|)
expr_stmt|;
block|}
block|}
block|}
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Throwable
name|e
parameter_list|)
block|{
name|s
operator|.
name|onError
argument_list|(
name|e
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|}
catch|catch
parameter_list|(
name|Throwable
name|e
parameter_list|)
block|{
comment|// Notify all the selections that the error occurred.
name|Set
argument_list|<
name|SelectionKey
argument_list|>
name|keys
init|=
name|selector
operator|.
name|keys
argument_list|()
decl_stmt|;
for|for
control|(
name|Iterator
argument_list|<
name|SelectionKey
argument_list|>
name|i
init|=
name|keys
operator|.
name|iterator
argument_list|()
init|;
name|i
operator|.
name|hasNext
argument_list|()
condition|;
control|)
block|{
name|SelectionKey
name|key
init|=
name|i
operator|.
name|next
argument_list|()
decl_stmt|;
name|SelectorSelection
name|s
init|=
operator|(
name|SelectorSelection
operator|)
name|key
operator|.
name|attachment
argument_list|()
decl_stmt|;
name|s
operator|.
name|onError
argument_list|(
name|e
argument_list|)
expr_stmt|;
block|}
block|}
finally|finally
block|{
try|try
block|{
name|manager
operator|.
name|onWorkerEmptyEvent
argument_list|(
name|this
argument_list|)
expr_stmt|;
name|selector
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|ignore
parameter_list|)
block|{
name|LOG
operator|.
name|debug
argument_list|(
name|ignore
operator|.
name|getMessage
argument_list|()
argument_list|,
name|ignore
argument_list|)
expr_stmt|;
block|}
name|Thread
operator|.
name|currentThread
argument_list|()
operator|.
name|setName
argument_list|(
name|origName
argument_list|)
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

