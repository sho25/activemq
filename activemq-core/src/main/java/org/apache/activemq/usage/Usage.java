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
name|usage
package|;
end_package

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|ArrayList
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
name|LinkedList
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
name|java
operator|.
name|util
operator|.
name|concurrent
operator|.
name|CopyOnWriteArrayList
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
name|atomic
operator|.
name|AtomicBoolean
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
name|Service
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
comment|/**  * Used to keep track of how much of something is being used so that a  * productive working set usage can be controlled. Main use case is manage  * memory usage.  *   * @org.apache.xbean.XBean  *   */
end_comment

begin_class
specifier|public
specifier|abstract
class|class
name|Usage
parameter_list|<
name|T
extends|extends
name|Usage
parameter_list|>
implements|implements
name|Service
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
name|Usage
operator|.
name|class
argument_list|)
decl_stmt|;
specifier|protected
specifier|final
name|Object
name|usageMutex
init|=
operator|new
name|Object
argument_list|()
decl_stmt|;
specifier|protected
name|int
name|percentUsage
decl_stmt|;
specifier|protected
name|T
name|parent
decl_stmt|;
specifier|private
name|UsageCapacity
name|limiter
init|=
operator|new
name|DefaultUsageCapacity
argument_list|()
decl_stmt|;
specifier|private
name|int
name|percentUsageMinDelta
init|=
literal|1
decl_stmt|;
specifier|private
specifier|final
name|List
argument_list|<
name|UsageListener
argument_list|>
name|listeners
init|=
operator|new
name|CopyOnWriteArrayList
argument_list|<
name|UsageListener
argument_list|>
argument_list|()
decl_stmt|;
specifier|private
specifier|final
name|boolean
name|debug
init|=
name|LOG
operator|.
name|isDebugEnabled
argument_list|()
decl_stmt|;
specifier|private
name|String
name|name
decl_stmt|;
specifier|private
name|float
name|usagePortion
init|=
literal|1.0f
decl_stmt|;
specifier|private
specifier|final
name|List
argument_list|<
name|T
argument_list|>
name|children
init|=
operator|new
name|CopyOnWriteArrayList
argument_list|<
name|T
argument_list|>
argument_list|()
decl_stmt|;
specifier|private
specifier|final
name|List
argument_list|<
name|Runnable
argument_list|>
name|callbacks
init|=
operator|new
name|LinkedList
argument_list|<
name|Runnable
argument_list|>
argument_list|()
decl_stmt|;
specifier|private
name|int
name|pollingTime
init|=
literal|100
decl_stmt|;
specifier|private
specifier|final
name|AtomicBoolean
name|started
init|=
operator|new
name|AtomicBoolean
argument_list|()
decl_stmt|;
specifier|private
name|ThreadPoolExecutor
name|executor
decl_stmt|;
specifier|public
name|Usage
parameter_list|(
name|T
name|parent
parameter_list|,
name|String
name|name
parameter_list|,
name|float
name|portion
parameter_list|)
block|{
name|this
operator|.
name|parent
operator|=
name|parent
expr_stmt|;
name|this
operator|.
name|usagePortion
operator|=
name|portion
expr_stmt|;
if|if
condition|(
name|parent
operator|!=
literal|null
condition|)
block|{
name|this
operator|.
name|limiter
operator|.
name|setLimit
argument_list|(
call|(
name|long
call|)
argument_list|(
name|parent
operator|.
name|getLimit
argument_list|()
operator|*
name|portion
argument_list|)
argument_list|)
expr_stmt|;
name|name
operator|=
name|parent
operator|.
name|name
operator|+
literal|":"
operator|+
name|name
expr_stmt|;
block|}
name|this
operator|.
name|name
operator|=
name|name
expr_stmt|;
block|}
specifier|protected
specifier|abstract
name|long
name|retrieveUsage
parameter_list|()
function_decl|;
comment|/**      * @throws InterruptedException      */
specifier|public
name|void
name|waitForSpace
parameter_list|()
throws|throws
name|InterruptedException
block|{
name|waitForSpace
argument_list|(
literal|0
argument_list|)
expr_stmt|;
block|}
specifier|public
name|boolean
name|waitForSpace
parameter_list|(
name|long
name|timeout
parameter_list|)
throws|throws
name|InterruptedException
block|{
return|return
name|waitForSpace
argument_list|(
name|timeout
argument_list|,
literal|100
argument_list|)
return|;
block|}
comment|/**      * @param timeout      * @throws InterruptedException      * @return true if space      */
specifier|public
name|boolean
name|waitForSpace
parameter_list|(
name|long
name|timeout
parameter_list|,
name|int
name|highWaterMark
parameter_list|)
throws|throws
name|InterruptedException
block|{
if|if
condition|(
name|parent
operator|!=
literal|null
condition|)
block|{
if|if
condition|(
operator|!
name|parent
operator|.
name|waitForSpace
argument_list|(
name|timeout
argument_list|,
name|highWaterMark
argument_list|)
condition|)
block|{
return|return
literal|false
return|;
block|}
block|}
synchronized|synchronized
init|(
name|usageMutex
init|)
block|{
name|percentUsage
operator|=
name|caclPercentUsage
argument_list|()
expr_stmt|;
if|if
condition|(
name|percentUsage
operator|>=
name|highWaterMark
condition|)
block|{
name|long
name|deadline
init|=
name|timeout
operator|>
literal|0
condition|?
name|System
operator|.
name|currentTimeMillis
argument_list|()
operator|+
name|timeout
else|:
name|Long
operator|.
name|MAX_VALUE
decl_stmt|;
name|long
name|timeleft
init|=
name|deadline
decl_stmt|;
while|while
condition|(
name|timeleft
operator|>
literal|0
condition|)
block|{
name|percentUsage
operator|=
name|caclPercentUsage
argument_list|()
expr_stmt|;
if|if
condition|(
name|percentUsage
operator|>=
name|highWaterMark
condition|)
block|{
name|usageMutex
operator|.
name|wait
argument_list|(
name|pollingTime
argument_list|)
expr_stmt|;
name|timeleft
operator|=
name|deadline
operator|-
name|System
operator|.
name|currentTimeMillis
argument_list|()
expr_stmt|;
block|}
else|else
block|{
break|break;
block|}
block|}
block|}
return|return
name|percentUsage
operator|<
name|highWaterMark
return|;
block|}
block|}
specifier|public
name|boolean
name|isFull
parameter_list|()
block|{
return|return
name|isFull
argument_list|(
literal|100
argument_list|)
return|;
block|}
specifier|public
name|boolean
name|isFull
parameter_list|(
name|int
name|highWaterMark
parameter_list|)
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
argument_list|(
name|highWaterMark
argument_list|)
condition|)
block|{
return|return
literal|true
return|;
block|}
synchronized|synchronized
init|(
name|usageMutex
init|)
block|{
name|percentUsage
operator|=
name|caclPercentUsage
argument_list|()
expr_stmt|;
return|return
name|percentUsage
operator|>=
name|highWaterMark
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
name|limiter
operator|.
name|getLimit
argument_list|()
return|;
block|}
block|}
comment|/**      * Sets the memory limit in bytes. Setting the limit in bytes will set the      * usagePortion to 0 since the UsageManager is not going to be portion based      * off the parent.      * When set using Xbean, values of the form "20 Mb", "1024kb", and "1g" can be used      *       * @org.apache.xbean.Property propertyEditor="org.apache.activemq.util.MemoryPropertyEditor"      */
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
synchronized|synchronized
init|(
name|usageMutex
init|)
block|{
name|this
operator|.
name|limiter
operator|.
name|setLimit
argument_list|(
name|limit
argument_list|)
expr_stmt|;
name|this
operator|.
name|usagePortion
operator|=
literal|0
expr_stmt|;
block|}
name|onLimitChange
argument_list|()
expr_stmt|;
block|}
specifier|protected
name|void
name|onLimitChange
parameter_list|()
block|{
comment|// We may need to calculate the limit
if|if
condition|(
name|usagePortion
operator|>
literal|0
operator|&&
name|parent
operator|!=
literal|null
condition|)
block|{
synchronized|synchronized
init|(
name|usageMutex
init|)
block|{
name|this
operator|.
name|limiter
operator|.
name|setLimit
argument_list|(
call|(
name|long
call|)
argument_list|(
name|parent
operator|.
name|getLimit
argument_list|()
operator|*
name|usagePortion
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
comment|// Reset the percent currently being used.
name|int
name|percentUsage
decl_stmt|;
synchronized|synchronized
init|(
name|usageMutex
init|)
block|{
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
comment|// Let the children know that the limit has changed. They may need to
comment|// set
comment|// their limits based on ours.
for|for
control|(
name|T
name|child
range|:
name|children
control|)
block|{
name|child
operator|.
name|onLimitChange
argument_list|()
expr_stmt|;
block|}
block|}
specifier|public
name|float
name|getUsagePortion
parameter_list|()
block|{
synchronized|synchronized
init|(
name|usageMutex
init|)
block|{
return|return
name|usagePortion
return|;
block|}
block|}
specifier|public
name|void
name|setUsagePortion
parameter_list|(
name|float
name|usagePortion
parameter_list|)
block|{
synchronized|synchronized
init|(
name|usageMutex
init|)
block|{
name|this
operator|.
name|usagePortion
operator|=
name|usagePortion
expr_stmt|;
block|}
name|onLimitChange
argument_list|()
expr_stmt|;
block|}
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
comment|/**      * Sets the minimum number of percentage points the usage has to change      * before a UsageListener event is fired by the manager.      *       * @param percentUsageMinDelta      * @org.apache.xbean.Property propertyEditor="org.apache.activemq.util.MemoryPropertyEditor"      */
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
name|retrieveUsage
argument_list|()
return|;
block|}
block|}
specifier|protected
name|void
name|setPercentUsage
parameter_list|(
name|int
name|value
parameter_list|)
block|{
synchronized|synchronized
init|(
name|usageMutex
init|)
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
block|}
specifier|protected
name|int
name|caclPercentUsage
parameter_list|()
block|{
if|if
condition|(
name|limiter
operator|.
name|getLimit
argument_list|()
operator|==
literal|0
condition|)
block|{
return|return
literal|0
return|;
block|}
return|return
call|(
name|int
call|)
argument_list|(
operator|(
operator|(
operator|(
name|retrieveUsage
argument_list|()
operator|*
literal|100
operator|)
operator|/
name|limiter
operator|.
name|getLimit
argument_list|()
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
specifier|final
name|int
name|oldPercentUsage
parameter_list|,
specifier|final
name|int
name|newPercentUsage
parameter_list|)
block|{
if|if
condition|(
name|debug
condition|)
block|{
name|LOG
operator|.
name|debug
argument_list|(
name|getName
argument_list|()
operator|+
literal|": usage change from: "
operator|+
name|oldPercentUsage
operator|+
literal|"% of available memory, to: "
operator|+
name|newPercentUsage
operator|+
literal|"% of available memory"
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|started
operator|.
name|get
argument_list|()
condition|)
block|{
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
if|if
condition|(
operator|!
name|callbacks
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
for|for
control|(
name|Iterator
argument_list|<
name|Runnable
argument_list|>
name|iter
init|=
operator|new
name|ArrayList
argument_list|<
name|Runnable
argument_list|>
argument_list|(
name|callbacks
argument_list|)
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
name|Runnable
name|callback
init|=
name|iter
operator|.
name|next
argument_list|()
decl_stmt|;
name|getExecutor
argument_list|()
operator|.
name|execute
argument_list|(
name|callback
argument_list|)
expr_stmt|;
block|}
name|callbacks
operator|.
name|clear
argument_list|()
expr_stmt|;
block|}
block|}
block|}
if|if
condition|(
operator|!
name|listeners
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
comment|// Let the listeners know on a separate thread
name|Runnable
name|listenerNotifier
init|=
operator|new
name|Runnable
argument_list|()
block|{
specifier|public
name|void
name|run
parameter_list|()
block|{
for|for
control|(
name|Iterator
argument_list|<
name|UsageListener
argument_list|>
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
name|iter
operator|.
name|next
argument_list|()
decl_stmt|;
name|l
operator|.
name|onUsageChanged
argument_list|(
name|Usage
operator|.
name|this
argument_list|,
name|oldPercentUsage
argument_list|,
name|newPercentUsage
argument_list|)
expr_stmt|;
block|}
block|}
block|}
decl_stmt|;
if|if
condition|(
name|started
operator|.
name|get
argument_list|()
condition|)
block|{
name|getExecutor
argument_list|()
operator|.
name|execute
argument_list|(
name|listenerNotifier
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|LOG
operator|.
name|warn
argument_list|(
literal|"Not notifying memory usage change to listeners on shutdown"
argument_list|)
expr_stmt|;
block|}
block|}
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
annotation|@
name|Override
specifier|public
name|String
name|toString
parameter_list|()
block|{
return|return
literal|"Usage("
operator|+
name|getName
argument_list|()
operator|+
literal|") percentUsage="
operator|+
name|percentUsage
operator|+
literal|"%, usage="
operator|+
name|retrieveUsage
argument_list|()
operator|+
literal|" limit="
operator|+
name|limiter
operator|.
name|getLimit
argument_list|()
operator|+
literal|" percentUsageMinDelta="
operator|+
name|percentUsageMinDelta
operator|+
literal|"%"
return|;
block|}
annotation|@
name|SuppressWarnings
argument_list|(
literal|"unchecked"
argument_list|)
specifier|public
name|void
name|start
parameter_list|()
block|{
if|if
condition|(
name|started
operator|.
name|compareAndSet
argument_list|(
literal|false
argument_list|,
literal|true
argument_list|)
condition|)
block|{
if|if
condition|(
name|parent
operator|!=
literal|null
condition|)
block|{
name|parent
operator|.
name|addChild
argument_list|(
name|this
argument_list|)
expr_stmt|;
block|}
for|for
control|(
name|T
name|t
range|:
name|children
control|)
block|{
name|t
operator|.
name|start
argument_list|()
expr_stmt|;
block|}
block|}
block|}
annotation|@
name|SuppressWarnings
argument_list|(
literal|"unchecked"
argument_list|)
specifier|public
name|void
name|stop
parameter_list|()
block|{
if|if
condition|(
name|started
operator|.
name|compareAndSet
argument_list|(
literal|true
argument_list|,
literal|false
argument_list|)
condition|)
block|{
if|if
condition|(
name|parent
operator|!=
literal|null
condition|)
block|{
name|parent
operator|.
name|removeChild
argument_list|(
name|this
argument_list|)
expr_stmt|;
block|}
comment|//clear down any callbacks
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
for|for
control|(
name|Iterator
argument_list|<
name|Runnable
argument_list|>
name|iter
init|=
operator|new
name|ArrayList
argument_list|<
name|Runnable
argument_list|>
argument_list|(
name|this
operator|.
name|callbacks
argument_list|)
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
name|Runnable
name|callback
init|=
name|iter
operator|.
name|next
argument_list|()
decl_stmt|;
name|callback
operator|.
name|run
argument_list|()
expr_stmt|;
block|}
name|this
operator|.
name|callbacks
operator|.
name|clear
argument_list|()
expr_stmt|;
block|}
for|for
control|(
name|T
name|t
range|:
name|children
control|)
block|{
name|t
operator|.
name|stop
argument_list|()
expr_stmt|;
block|}
block|}
block|}
specifier|private
name|void
name|addChild
parameter_list|(
name|T
name|child
parameter_list|)
block|{
name|children
operator|.
name|add
argument_list|(
name|child
argument_list|)
expr_stmt|;
if|if
condition|(
name|started
operator|.
name|get
argument_list|()
condition|)
block|{
name|child
operator|.
name|start
argument_list|()
expr_stmt|;
block|}
block|}
specifier|private
name|void
name|removeChild
parameter_list|(
name|T
name|child
parameter_list|)
block|{
name|children
operator|.
name|remove
argument_list|(
name|child
argument_list|)
expr_stmt|;
block|}
comment|/**      * @param callback      * @return true if the UsageManager was full. The callback will only be      *         called if this method returns true.      */
specifier|public
name|boolean
name|notifyCallbackWhenNotFull
parameter_list|(
specifier|final
name|Runnable
name|callback
parameter_list|)
block|{
if|if
condition|(
name|parent
operator|!=
literal|null
condition|)
block|{
name|Runnable
name|r
init|=
operator|new
name|Runnable
argument_list|()
block|{
specifier|public
name|void
name|run
parameter_list|()
block|{
synchronized|synchronized
init|(
name|usageMutex
init|)
block|{
if|if
condition|(
name|percentUsage
operator|>=
literal|100
condition|)
block|{
name|callbacks
operator|.
name|add
argument_list|(
name|callback
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|callback
operator|.
name|run
argument_list|()
expr_stmt|;
block|}
block|}
block|}
block|}
decl_stmt|;
if|if
condition|(
name|parent
operator|.
name|notifyCallbackWhenNotFull
argument_list|(
name|r
argument_list|)
condition|)
block|{
return|return
literal|true
return|;
block|}
block|}
synchronized|synchronized
init|(
name|usageMutex
init|)
block|{
if|if
condition|(
name|percentUsage
operator|>=
literal|100
condition|)
block|{
name|callbacks
operator|.
name|add
argument_list|(
name|callback
argument_list|)
expr_stmt|;
return|return
literal|true
return|;
block|}
else|else
block|{
return|return
literal|false
return|;
block|}
block|}
block|}
comment|/**      * @return the limiter      */
specifier|public
name|UsageCapacity
name|getLimiter
parameter_list|()
block|{
return|return
name|this
operator|.
name|limiter
return|;
block|}
comment|/**      * @param limiter the limiter to set      */
specifier|public
name|void
name|setLimiter
parameter_list|(
name|UsageCapacity
name|limiter
parameter_list|)
block|{
name|this
operator|.
name|limiter
operator|=
name|limiter
expr_stmt|;
block|}
comment|/**      * @return the pollingTime      */
specifier|public
name|int
name|getPollingTime
parameter_list|()
block|{
return|return
name|this
operator|.
name|pollingTime
return|;
block|}
comment|/**      * @param pollingTime the pollingTime to set      */
specifier|public
name|void
name|setPollingTime
parameter_list|(
name|int
name|pollingTime
parameter_list|)
block|{
name|this
operator|.
name|pollingTime
operator|=
name|pollingTime
expr_stmt|;
block|}
specifier|public
name|void
name|setName
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
name|T
name|getParent
parameter_list|()
block|{
return|return
name|parent
return|;
block|}
specifier|public
name|void
name|setParent
parameter_list|(
name|T
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
specifier|public
name|void
name|setExecutor
parameter_list|(
name|ThreadPoolExecutor
name|executor
parameter_list|)
block|{
name|this
operator|.
name|executor
operator|=
name|executor
expr_stmt|;
block|}
specifier|public
name|ThreadPoolExecutor
name|getExecutor
parameter_list|()
block|{
return|return
name|executor
return|;
block|}
block|}
end_class

end_unit

