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
name|memory
package|;
end_package

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
name|LinkedList
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
name|thread
operator|.
name|Task
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
name|thread
operator|.
name|TaskRunner
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
name|thread
operator|.
name|TaskRunnerFactory
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|commons
operator|.
name|logging
operator|.
name|Log
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|commons
operator|.
name|logging
operator|.
name|LogFactory
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
name|CopyOnWriteArrayList
import|;
end_import

begin_class
specifier|public
class|class
name|CacheEvictionUsageListener
implements|implements
name|UsageListener
block|{
specifier|private
specifier|final
specifier|static
name|Log
name|log
init|=
name|LogFactory
operator|.
name|getLog
argument_list|(
name|CacheEvictionUsageListener
operator|.
name|class
argument_list|)
decl_stmt|;
specifier|private
specifier|final
name|CopyOnWriteArrayList
name|evictors
init|=
operator|new
name|CopyOnWriteArrayList
argument_list|()
decl_stmt|;
specifier|private
specifier|final
name|int
name|usageHighMark
decl_stmt|;
specifier|private
specifier|final
name|int
name|usageLowMark
decl_stmt|;
specifier|private
specifier|final
name|TaskRunner
name|evictionTask
decl_stmt|;
specifier|private
specifier|final
name|UsageManager
name|usageManager
decl_stmt|;
specifier|public
name|CacheEvictionUsageListener
parameter_list|(
name|UsageManager
name|usageManager
parameter_list|,
name|int
name|usageHighMark
parameter_list|,
name|int
name|usageLowMark
parameter_list|,
name|TaskRunnerFactory
name|taskRunnerFactory
parameter_list|)
block|{
name|this
operator|.
name|usageManager
operator|=
name|usageManager
expr_stmt|;
name|this
operator|.
name|usageHighMark
operator|=
name|usageHighMark
expr_stmt|;
name|this
operator|.
name|usageLowMark
operator|=
name|usageLowMark
expr_stmt|;
name|evictionTask
operator|=
name|taskRunnerFactory
operator|.
name|createTaskRunner
argument_list|(
operator|new
name|Task
argument_list|()
block|{
specifier|public
name|boolean
name|iterate
parameter_list|()
block|{
return|return
name|evictMessages
argument_list|()
return|;
block|}
block|}
argument_list|,
literal|"Cache Evictor: "
operator|+
name|System
operator|.
name|identityHashCode
argument_list|(
name|this
argument_list|)
argument_list|)
expr_stmt|;
block|}
specifier|private
name|boolean
name|evictMessages
parameter_list|()
block|{
comment|// Try to take the memory usage down below the low mark.
try|try
block|{
name|log
operator|.
name|debug
argument_list|(
literal|"Evicting cache memory usage: "
operator|+
name|usageManager
operator|.
name|getPercentUsage
argument_list|()
argument_list|)
expr_stmt|;
name|LinkedList
name|list
init|=
operator|new
name|LinkedList
argument_list|(
name|evictors
argument_list|)
decl_stmt|;
while|while
condition|(
name|list
operator|.
name|size
argument_list|()
operator|>
literal|0
operator|&&
name|usageManager
operator|.
name|getPercentUsage
argument_list|()
operator|>
name|usageLowMark
condition|)
block|{
comment|// Evenly evict messages from all evictors
for|for
control|(
name|Iterator
name|iter
init|=
name|list
operator|.
name|iterator
argument_list|()
init|;
name|iter
operator|.
name|hasNext
argument_list|()
condition|;
control|)
block|{
name|CacheEvictor
name|evictor
init|=
operator|(
name|CacheEvictor
operator|)
name|iter
operator|.
name|next
argument_list|()
decl_stmt|;
if|if
condition|(
name|evictor
operator|.
name|evictCacheEntry
argument_list|()
operator|==
literal|null
condition|)
name|iter
operator|.
name|remove
argument_list|()
expr_stmt|;
block|}
block|}
block|}
finally|finally
block|{         }
return|return
literal|false
return|;
block|}
specifier|public
name|void
name|onMemoryUseChanged
parameter_list|(
name|UsageManager
name|memoryManager
parameter_list|,
name|int
name|oldPercentUsage
parameter_list|,
name|int
name|newPercentUsage
parameter_list|)
block|{
comment|// Do we need to start evicting cache entries? Usage> than the
comment|// high mark
if|if
condition|(
name|oldPercentUsage
operator|<
name|newPercentUsage
operator|&&
name|memoryManager
operator|.
name|getPercentUsage
argument_list|()
operator|>=
name|usageHighMark
condition|)
block|{
try|try
block|{
name|evictionTask
operator|.
name|wakeup
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|InterruptedException
name|e
parameter_list|)
block|{
name|Thread
operator|.
name|currentThread
argument_list|()
operator|.
name|interrupt
argument_list|()
expr_stmt|;
block|}
block|}
block|}
specifier|public
name|void
name|add
parameter_list|(
name|CacheEvictor
name|evictor
parameter_list|)
block|{
name|evictors
operator|.
name|add
argument_list|(
name|evictor
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|remove
parameter_list|(
name|CacheEvictor
name|evictor
parameter_list|)
block|{
name|evictors
operator|.
name|remove
argument_list|(
name|evictor
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

