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

begin_comment
comment|/**  * Used to keep track of how much of something is being used so that   * a productive working set usage can be controlled.  *   * Main use case is manage memory usage.  *   * @org.apache.xbean.XBean  *   * @version $Revision: 1.3 $  */
end_comment

begin_class
specifier|public
class|class
name|UsageManager
block|{
specifier|private
specifier|static
specifier|final
name|Log
name|log
init|=
name|LogFactory
operator|.
name|getLog
argument_list|(
name|UsageManager
operator|.
name|class
argument_list|)
decl_stmt|;
specifier|private
specifier|final
name|UsageManager
name|parent
decl_stmt|;
specifier|private
name|long
name|limit
decl_stmt|;
specifier|private
name|long
name|usage
decl_stmt|;
specifier|private
name|int
name|percentUsage
decl_stmt|;
specifier|private
name|int
name|percentUsageMinDelta
init|=
literal|1
decl_stmt|;
specifier|private
specifier|final
name|Object
name|usageMutex
init|=
operator|new
name|Object
argument_list|()
decl_stmt|;
specifier|private
specifier|final
name|CopyOnWriteArrayList
name|listeners
init|=
operator|new
name|CopyOnWriteArrayList
argument_list|()
decl_stmt|;
specifier|private
name|boolean
name|sendFailIfNoSpace
decl_stmt|;
comment|/** True if someone called setSendFailIfNoSpace() on this particular usage manager */
specifier|private
name|boolean
name|sendFailIfNoSpaceExplicitySet
decl_stmt|;
specifier|public
name|UsageManager
parameter_list|()
block|{
name|this
argument_list|(
literal|null
argument_list|)
expr_stmt|;
block|}
comment|/**      * Create the memory manager linked to a parent.  When the memory manager is linked to       * a parent then when usage increased or decreased, the parent's usage is also increased       * or decreased.      *       * @param parent      */
specifier|public
name|UsageManager
parameter_list|(
name|UsageManager
name|parent
parameter_list|)
block|{
name|this
operator|.
name|parent
operator|=
name|parent
expr_stmt|;
block|}
comment|/**      * Tries to increase the usage by value amount but blocks if this object      * is currently full.      * @throws InterruptedException       */
specifier|public
name|void
name|enqueueUsage
parameter_list|(
name|long
name|value
parameter_list|)
throws|throws
name|InterruptedException
block|{
name|waitForSpace
argument_list|()
expr_stmt|;
name|increaseUsage
argument_list|(
name|value
argument_list|)
expr_stmt|;
block|}
comment|/**      * @throws InterruptedException      */
specifier|public
name|void
name|waitForSpace
parameter_list|()
throws|throws
name|InterruptedException
block|{
if|if
condition|(
name|parent
operator|!=
literal|null
condition|)
name|parent
operator|.
name|waitForSpace
argument_list|()
expr_stmt|;
synchronized|synchronized
init|(
name|usageMutex
init|)
block|{
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|percentUsage
operator|>=
literal|100
condition|;
name|i
operator|++
control|)
block|{
name|usageMutex
operator|.
name|wait
argument_list|()
expr_stmt|;
block|}
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|percentUsage
operator|>
literal|90
condition|;
name|i
operator|++
control|)
block|{
name|usageMutex
operator|.
name|wait
argument_list|(
literal|100
argument_list|)
expr_stmt|;
block|}
block|}
block|}
comment|/**      * Increases the usage by the value amount.        *       * @param value      */
specifier|public
name|void
name|increaseUsage
parameter_list|(
name|long
name|value
parameter_list|)
block|{
if|if
condition|(
name|value
operator|==
literal|0
condition|)
return|return;
if|if
condition|(
name|parent
operator|!=
literal|null
condition|)
name|parent
operator|.
name|increaseUsage
argument_list|(
name|value
argument_list|)
expr_stmt|;
name|int
name|percentUsage
decl_stmt|;
synchronized|synchronized
init|(
name|usageMutex
init|)
block|{
name|usage
operator|+=
name|value
expr_stmt|;
name|percentUsage
operator|=
name|caclPercentUsage
argument_list|()
expr_stmt|;
block|}
name|setPercentUsage
argument_list|(
name|percentUsage
argument_list|)
expr_stmt|;
block|}
comment|/**      * Decreases the usage by the value amount.        *       * @param value      */
specifier|public
name|void
name|decreaseUsage
parameter_list|(
name|long
name|value
parameter_list|)
block|{
if|if
condition|(
name|value
operator|==
literal|0
condition|)
return|return;
if|if
condition|(
name|parent
operator|!=
literal|null
condition|)
name|parent
operator|.
name|decreaseUsage
argument_list|(
name|value
argument_list|)
expr_stmt|;
name|int
name|percentUsage
decl_stmt|;
synchronized|synchronized
init|(
name|usageMutex
init|)
block|{
name|usage
operator|-=
name|value
expr_stmt|;
name|percentUsage
operator|=
name|caclPercentUsage
argument_list|()
expr_stmt|;
block|}
name|setPercentUsage
argument_list|(
name|percentUsage
argument_list|)
expr_stmt|;
block|}
specifier|public
name|boolean
name|isFull
parameter_list|()
block|{
if|if
condition|(
name|parent
operator|!=
literal|null
operator|&&
name|parent
operator|.
name|isFull
argument_list|()
condition|)
return|return
literal|true
return|;
synchronized|synchronized
init|(
name|usageMutex
init|)
block|{
return|return
name|percentUsage
operator|>=
literal|100
return|;
block|}
block|}
specifier|public
name|void
name|addUsageListener
parameter_list|(
name|UsageListener
name|listener
parameter_list|)
block|{
name|listeners
operator|.
name|add
argument_list|(
name|listener
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|removeUsageListener
parameter_list|(
name|UsageListener
name|listener
parameter_list|)
block|{
name|listeners
operator|.
name|remove
argument_list|(
name|listener
argument_list|)
expr_stmt|;
block|}
specifier|public
name|long
name|getLimit
parameter_list|()
block|{
synchronized|synchronized
init|(
name|usageMutex
init|)
block|{
return|return
name|limit
return|;
block|}
block|}
comment|/**      * Sets the memory limit in bytes.      *       * When set using XBean, you can use values such as: "20 mb", "1024 kb", or "1 gb"      *       * @org.apache.xbean.Property propertyEditor="org.apache.activemq.util.MemoryPropertyEditor"      */
specifier|public
name|void
name|setLimit
parameter_list|(
name|long
name|limit
parameter_list|)
block|{
if|if
condition|(
name|percentUsageMinDelta
operator|<
literal|0
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"percentUsageMinDelta must be greater or equal to 0"
argument_list|)
throw|;
block|}
name|int
name|percentUsage
decl_stmt|;
synchronized|synchronized
init|(
name|usageMutex
init|)
block|{
name|this
operator|.
name|limit
operator|=
name|limit
expr_stmt|;
name|percentUsage
operator|=
name|caclPercentUsage
argument_list|()
expr_stmt|;
block|}
name|setPercentUsage
argument_list|(
name|percentUsage
argument_list|)
expr_stmt|;
block|}
comment|/*     * Sets the minimum number of percentage points the usage has to change before a UsageListener     * event is fired by the manager.     */
specifier|public
name|int
name|getPercentUsage
parameter_list|()
block|{
synchronized|synchronized
init|(
name|usageMutex
init|)
block|{
return|return
name|percentUsage
return|;
block|}
block|}
specifier|public
name|int
name|getPercentUsageMinDelta
parameter_list|()
block|{
synchronized|synchronized
init|(
name|usageMutex
init|)
block|{
return|return
name|percentUsageMinDelta
return|;
block|}
block|}
comment|/**      * Sets the minimum number of percentage points the usage has to change before a UsageListener      * event is fired by the manager.      *       * @param percentUsageMinDelta      */
specifier|public
name|void
name|setPercentUsageMinDelta
parameter_list|(
name|int
name|percentUsageMinDelta
parameter_list|)
block|{
if|if
condition|(
name|percentUsageMinDelta
operator|<
literal|1
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"percentUsageMinDelta must be greater than 0"
argument_list|)
throw|;
block|}
name|int
name|percentUsage
decl_stmt|;
synchronized|synchronized
init|(
name|usageMutex
init|)
block|{
name|this
operator|.
name|percentUsageMinDelta
operator|=
name|percentUsageMinDelta
expr_stmt|;
name|percentUsage
operator|=
name|caclPercentUsage
argument_list|()
expr_stmt|;
block|}
name|setPercentUsage
argument_list|(
name|percentUsage
argument_list|)
expr_stmt|;
block|}
specifier|public
name|long
name|getUsage
parameter_list|()
block|{
synchronized|synchronized
init|(
name|usageMutex
init|)
block|{
return|return
name|usage
return|;
block|}
block|}
comment|/**      * Sets whether or not a send() should fail if there is no space free. The default      * value is false which means to block the send() method until space becomes available      */
specifier|public
name|void
name|setSendFailIfNoSpace
parameter_list|(
name|boolean
name|failProducerIfNoSpace
parameter_list|)
block|{
name|sendFailIfNoSpaceExplicitySet
operator|=
literal|true
expr_stmt|;
name|this
operator|.
name|sendFailIfNoSpace
operator|=
name|failProducerIfNoSpace
expr_stmt|;
block|}
specifier|public
name|boolean
name|isSendFailIfNoSpace
parameter_list|()
block|{
if|if
condition|(
name|sendFailIfNoSpaceExplicitySet
operator|||
name|parent
operator|==
literal|null
condition|)
block|{
return|return
name|sendFailIfNoSpace
return|;
block|}
else|else
block|{
return|return
name|parent
operator|.
name|isSendFailIfNoSpace
argument_list|()
return|;
block|}
block|}
specifier|private
name|void
name|setPercentUsage
parameter_list|(
name|int
name|value
parameter_list|)
block|{
name|int
name|oldValue
init|=
name|percentUsage
decl_stmt|;
name|percentUsage
operator|=
name|value
expr_stmt|;
if|if
condition|(
name|oldValue
operator|!=
name|value
condition|)
block|{
name|fireEvent
argument_list|(
name|oldValue
argument_list|,
name|value
argument_list|)
expr_stmt|;
block|}
block|}
specifier|private
name|int
name|caclPercentUsage
parameter_list|()
block|{
if|if
condition|(
name|limit
operator|==
literal|0
condition|)
return|return
literal|0
return|;
return|return
call|(
name|int
call|)
argument_list|(
operator|(
operator|(
operator|(
name|usage
operator|*
literal|100
operator|)
operator|/
name|limit
operator|)
operator|/
name|percentUsageMinDelta
operator|)
operator|*
name|percentUsageMinDelta
argument_list|)
return|;
block|}
specifier|private
name|void
name|fireEvent
parameter_list|(
name|int
name|oldPercentUsage
parameter_list|,
name|int
name|newPercentUsage
parameter_list|)
block|{
name|log
operator|.
name|debug
argument_list|(
literal|"Memory usage change.  from: "
operator|+
name|oldPercentUsage
operator|+
literal|", to: "
operator|+
name|newPercentUsage
argument_list|)
expr_stmt|;
comment|// Switching from being full to not being full..
if|if
condition|(
name|oldPercentUsage
operator|>=
literal|100
operator|&&
name|newPercentUsage
operator|<
literal|100
condition|)
block|{
synchronized|synchronized
init|(
name|usageMutex
init|)
block|{
name|usageMutex
operator|.
name|notifyAll
argument_list|()
expr_stmt|;
block|}
block|}
comment|//      Let the listeners know
for|for
control|(
name|Iterator
name|iter
init|=
name|listeners
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
name|UsageListener
name|l
init|=
operator|(
name|UsageListener
operator|)
name|iter
operator|.
name|next
argument_list|()
decl_stmt|;
name|l
operator|.
name|onMemoryUseChanged
argument_list|(
name|this
argument_list|,
name|oldPercentUsage
argument_list|,
name|newPercentUsage
argument_list|)
expr_stmt|;
block|}
block|}
specifier|public
name|String
name|toString
parameter_list|()
block|{
return|return
literal|"UsageManager: percentUsage="
operator|+
name|percentUsage
operator|+
literal|"%, usage="
operator|+
name|usage
operator|+
literal|" limit="
operator|+
name|limit
operator|+
literal|" percentUsageMinDelta="
operator|+
name|percentUsageMinDelta
operator|+
literal|"%"
return|;
block|}
block|}
end_class

end_unit

