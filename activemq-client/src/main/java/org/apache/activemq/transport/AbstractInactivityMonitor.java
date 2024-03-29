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
name|concurrent
operator|.
name|RejectedExecutionException
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
name|java
operator|.
name|util
operator|.
name|concurrent
operator|.
name|locks
operator|.
name|ReentrantReadWriteLock
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
name|command
operator|.
name|KeepAliveInfo
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
name|command
operator|.
name|WireFormatInfo
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
name|SchedulerTimerTask
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
name|ThreadPoolUtils
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
name|wireformat
operator|.
name|WireFormat
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
comment|/**  * Used to make sure that commands are arriving periodically from the peer of  * the transport.  */
end_comment

begin_class
specifier|public
specifier|abstract
class|class
name|AbstractInactivityMonitor
extends|extends
name|TransportFilter
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
name|AbstractInactivityMonitor
operator|.
name|class
argument_list|)
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|long
name|DEFAULT_CHECK_TIME_MILLS
init|=
literal|30000
decl_stmt|;
specifier|private
specifier|static
name|ThreadPoolExecutor
name|ASYNC_TASKS
decl_stmt|;
specifier|private
specifier|static
name|int
name|CHECKER_COUNTER
decl_stmt|;
specifier|private
specifier|static
name|Timer
name|READ_CHECK_TIMER
decl_stmt|;
specifier|private
specifier|static
name|Timer
name|WRITE_CHECK_TIMER
decl_stmt|;
specifier|private
specifier|final
name|AtomicBoolean
name|monitorStarted
init|=
operator|new
name|AtomicBoolean
argument_list|(
literal|false
argument_list|)
decl_stmt|;
specifier|private
specifier|final
name|AtomicBoolean
name|commandSent
init|=
operator|new
name|AtomicBoolean
argument_list|(
literal|false
argument_list|)
decl_stmt|;
specifier|private
specifier|final
name|AtomicBoolean
name|inSend
init|=
operator|new
name|AtomicBoolean
argument_list|(
literal|false
argument_list|)
decl_stmt|;
specifier|private
specifier|final
name|AtomicBoolean
name|failed
init|=
operator|new
name|AtomicBoolean
argument_list|(
literal|false
argument_list|)
decl_stmt|;
specifier|private
specifier|final
name|AtomicBoolean
name|commandReceived
init|=
operator|new
name|AtomicBoolean
argument_list|(
literal|true
argument_list|)
decl_stmt|;
specifier|private
specifier|final
name|AtomicBoolean
name|inReceive
init|=
operator|new
name|AtomicBoolean
argument_list|(
literal|false
argument_list|)
decl_stmt|;
specifier|private
specifier|final
name|AtomicInteger
name|lastReceiveCounter
init|=
operator|new
name|AtomicInteger
argument_list|(
literal|0
argument_list|)
decl_stmt|;
specifier|private
specifier|final
name|ReentrantReadWriteLock
name|sendLock
init|=
operator|new
name|ReentrantReadWriteLock
argument_list|()
decl_stmt|;
specifier|private
name|SchedulerTimerTask
name|connectCheckerTask
decl_stmt|;
specifier|private
name|SchedulerTimerTask
name|writeCheckerTask
decl_stmt|;
specifier|private
name|SchedulerTimerTask
name|readCheckerTask
decl_stmt|;
specifier|private
name|long
name|connectAttemptTimeout
init|=
name|DEFAULT_CHECK_TIME_MILLS
decl_stmt|;
specifier|private
name|long
name|readCheckTime
init|=
name|DEFAULT_CHECK_TIME_MILLS
decl_stmt|;
specifier|private
name|long
name|writeCheckTime
init|=
name|DEFAULT_CHECK_TIME_MILLS
decl_stmt|;
specifier|private
name|long
name|initialDelayTime
init|=
name|DEFAULT_CHECK_TIME_MILLS
decl_stmt|;
specifier|private
name|boolean
name|useKeepAlive
init|=
literal|true
decl_stmt|;
specifier|private
name|boolean
name|keepAliveResponseRequired
decl_stmt|;
specifier|protected
name|WireFormat
name|wireFormat
decl_stmt|;
specifier|private
specifier|final
name|Runnable
name|connectChecker
init|=
operator|new
name|Runnable
argument_list|()
block|{
specifier|private
specifier|final
name|long
name|startTime
init|=
name|System
operator|.
name|currentTimeMillis
argument_list|()
decl_stmt|;
annotation|@
name|Override
specifier|public
name|void
name|run
parameter_list|()
block|{
name|long
name|now
init|=
name|System
operator|.
name|currentTimeMillis
argument_list|()
decl_stmt|;
if|if
condition|(
operator|(
name|now
operator|-
name|startTime
operator|)
operator|>=
name|connectAttemptTimeout
operator|&&
name|connectCheckerTask
operator|!=
literal|null
operator|&&
operator|!
name|ASYNC_TASKS
operator|.
name|isShutdown
argument_list|()
condition|)
block|{
name|LOG
operator|.
name|debug
argument_list|(
literal|"No connection attempt made in time for {}! Throwing InactivityIOException."
argument_list|,
name|AbstractInactivityMonitor
operator|.
name|this
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
try|try
block|{
name|ASYNC_TASKS
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
name|onException
argument_list|(
operator|new
name|InactivityIOException
argument_list|(
literal|"Channel was inactive (no connection attempt made) for too (>"
operator|+
operator|(
name|connectAttemptTimeout
operator|)
operator|+
literal|") long: "
operator|+
name|next
operator|.
name|getRemoteAddress
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|RejectedExecutionException
name|ex
parameter_list|)
block|{
if|if
condition|(
operator|!
name|ASYNC_TASKS
operator|.
name|isShutdown
argument_list|()
condition|)
block|{
name|LOG
operator|.
name|error
argument_list|(
literal|"Async connection timeout task was rejected from the executor: "
argument_list|,
name|ex
argument_list|)
expr_stmt|;
throw|throw
name|ex
throw|;
block|}
block|}
block|}
block|}
block|}
decl_stmt|;
specifier|private
specifier|final
name|Runnable
name|readChecker
init|=
operator|new
name|Runnable
argument_list|()
block|{
name|long
name|lastRunTime
decl_stmt|;
annotation|@
name|Override
specifier|public
name|void
name|run
parameter_list|()
block|{
name|long
name|now
init|=
name|System
operator|.
name|currentTimeMillis
argument_list|()
decl_stmt|;
name|long
name|elapsed
init|=
operator|(
name|now
operator|-
name|lastRunTime
operator|)
decl_stmt|;
if|if
condition|(
name|lastRunTime
operator|!=
literal|0
condition|)
block|{
name|LOG
operator|.
name|debug
argument_list|(
literal|"{}ms elapsed since last read check."
argument_list|,
name|elapsed
argument_list|)
expr_stmt|;
block|}
comment|// Perhaps the timer executed a read check late.. and then executes
comment|// the next read check on time which causes the time elapsed between
comment|// read checks to be small..
comment|// If less than 90% of the read check Time elapsed then abort this
comment|// read check.
if|if
condition|(
operator|!
name|allowReadCheck
argument_list|(
name|elapsed
argument_list|)
condition|)
block|{
name|LOG
operator|.
name|debug
argument_list|(
literal|"Aborting read check...Not enough time elapsed since last read check."
argument_list|)
expr_stmt|;
return|return;
block|}
name|lastRunTime
operator|=
name|now
expr_stmt|;
name|readCheck
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|String
name|toString
parameter_list|()
block|{
return|return
literal|"ReadChecker"
return|;
block|}
block|}
decl_stmt|;
specifier|private
name|boolean
name|allowReadCheck
parameter_list|(
name|long
name|elapsed
parameter_list|)
block|{
return|return
name|elapsed
operator|>
operator|(
name|readCheckTime
operator|*
literal|9
operator|/
literal|10
operator|)
return|;
block|}
specifier|private
specifier|final
name|Runnable
name|writeChecker
init|=
operator|new
name|Runnable
argument_list|()
block|{
name|long
name|lastRunTime
decl_stmt|;
annotation|@
name|Override
specifier|public
name|void
name|run
parameter_list|()
block|{
name|long
name|now
init|=
name|System
operator|.
name|currentTimeMillis
argument_list|()
decl_stmt|;
if|if
condition|(
name|lastRunTime
operator|!=
literal|0
condition|)
block|{
name|LOG
operator|.
name|debug
argument_list|(
literal|"{}: {}ms elapsed since last write check."
argument_list|,
name|this
argument_list|,
operator|(
name|now
operator|-
name|lastRunTime
operator|)
argument_list|)
expr_stmt|;
block|}
name|lastRunTime
operator|=
name|now
expr_stmt|;
name|writeCheck
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|String
name|toString
parameter_list|()
block|{
return|return
literal|"WriteChecker"
return|;
block|}
block|}
decl_stmt|;
specifier|public
name|AbstractInactivityMonitor
parameter_list|(
name|Transport
name|next
parameter_list|,
name|WireFormat
name|wireFormat
parameter_list|)
block|{
name|super
argument_list|(
name|next
argument_list|)
expr_stmt|;
name|this
operator|.
name|wireFormat
operator|=
name|wireFormat
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|start
parameter_list|()
throws|throws
name|Exception
block|{
name|next
operator|.
name|start
argument_list|()
expr_stmt|;
name|startMonitorThreads
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|stop
parameter_list|()
throws|throws
name|Exception
block|{
name|stopMonitorThreads
argument_list|()
expr_stmt|;
name|next
operator|.
name|stop
argument_list|()
expr_stmt|;
block|}
specifier|final
name|void
name|writeCheck
parameter_list|()
block|{
if|if
condition|(
name|inSend
operator|.
name|get
argument_list|()
condition|)
block|{
name|LOG
operator|.
name|trace
argument_list|(
literal|"Send in progress. Skipping write check."
argument_list|)
expr_stmt|;
return|return;
block|}
if|if
condition|(
operator|!
name|commandSent
operator|.
name|get
argument_list|()
operator|&&
name|useKeepAlive
operator|&&
name|monitorStarted
operator|.
name|get
argument_list|()
operator|&&
operator|!
name|ASYNC_TASKS
operator|.
name|isShutdown
argument_list|()
condition|)
block|{
name|LOG
operator|.
name|trace
argument_list|(
literal|"{} no message sent since last write check, sending a KeepAliveInfo"
argument_list|,
name|this
argument_list|)
expr_stmt|;
try|try
block|{
name|ASYNC_TASKS
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
name|LOG
operator|.
name|debug
argument_list|(
literal|"Running {}"
argument_list|,
name|this
argument_list|)
expr_stmt|;
if|if
condition|(
name|monitorStarted
operator|.
name|get
argument_list|()
condition|)
block|{
try|try
block|{
comment|// If we can't get the lock it means another
comment|// write beat us into the
comment|// send and we don't need to heart beat now.
if|if
condition|(
name|sendLock
operator|.
name|writeLock
argument_list|()
operator|.
name|tryLock
argument_list|()
condition|)
block|{
name|KeepAliveInfo
name|info
init|=
operator|new
name|KeepAliveInfo
argument_list|()
decl_stmt|;
name|info
operator|.
name|setResponseRequired
argument_list|(
name|keepAliveResponseRequired
argument_list|)
expr_stmt|;
name|doOnewaySend
argument_list|(
name|info
argument_list|)
expr_stmt|;
block|}
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
name|onException
argument_list|(
name|e
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
if|if
condition|(
name|sendLock
operator|.
name|writeLock
argument_list|()
operator|.
name|isHeldByCurrentThread
argument_list|()
condition|)
block|{
name|sendLock
operator|.
name|writeLock
argument_list|()
operator|.
name|unlock
argument_list|()
expr_stmt|;
block|}
block|}
block|}
block|}
annotation|@
name|Override
specifier|public
name|String
name|toString
parameter_list|()
block|{
return|return
literal|"WriteCheck["
operator|+
name|getRemoteAddress
argument_list|()
operator|+
literal|"]"
return|;
block|}
empty_stmt|;
block|}
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|RejectedExecutionException
name|ex
parameter_list|)
block|{
if|if
condition|(
operator|!
name|ASYNC_TASKS
operator|.
name|isShutdown
argument_list|()
condition|)
block|{
name|LOG
operator|.
name|error
argument_list|(
literal|"Async write check was rejected from the executor: "
argument_list|,
name|ex
argument_list|)
expr_stmt|;
throw|throw
name|ex
throw|;
block|}
block|}
block|}
else|else
block|{
name|LOG
operator|.
name|trace
argument_list|(
literal|"{} message sent since last write check, resetting flag."
argument_list|,
name|this
argument_list|)
expr_stmt|;
block|}
name|commandSent
operator|.
name|set
argument_list|(
literal|false
argument_list|)
expr_stmt|;
block|}
specifier|final
name|void
name|readCheck
parameter_list|()
block|{
name|int
name|currentCounter
init|=
name|next
operator|.
name|getReceiveCounter
argument_list|()
decl_stmt|;
name|int
name|previousCounter
init|=
name|lastReceiveCounter
operator|.
name|getAndSet
argument_list|(
name|currentCounter
argument_list|)
decl_stmt|;
if|if
condition|(
name|inReceive
operator|.
name|get
argument_list|()
operator|||
name|currentCounter
operator|!=
name|previousCounter
condition|)
block|{
name|LOG
operator|.
name|trace
argument_list|(
literal|"A receive is in progress, skipping read check."
argument_list|)
expr_stmt|;
return|return;
block|}
if|if
condition|(
operator|!
name|commandReceived
operator|.
name|get
argument_list|()
operator|&&
name|monitorStarted
operator|.
name|get
argument_list|()
operator|&&
operator|!
name|ASYNC_TASKS
operator|.
name|isShutdown
argument_list|()
condition|)
block|{
name|LOG
operator|.
name|debug
argument_list|(
literal|"No message received since last read check for {}. Throwing InactivityIOException."
argument_list|,
name|this
argument_list|)
expr_stmt|;
try|try
block|{
name|ASYNC_TASKS
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
name|LOG
operator|.
name|debug
argument_list|(
literal|"Running {}"
argument_list|,
name|this
argument_list|)
expr_stmt|;
name|onException
argument_list|(
operator|new
name|InactivityIOException
argument_list|(
literal|"Channel was inactive for too (>"
operator|+
name|readCheckTime
operator|+
literal|") long: "
operator|+
name|next
operator|.
name|getRemoteAddress
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|String
name|toString
parameter_list|()
block|{
return|return
literal|"ReadCheck["
operator|+
name|getRemoteAddress
argument_list|()
operator|+
literal|"]"
return|;
block|}
empty_stmt|;
block|}
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|RejectedExecutionException
name|ex
parameter_list|)
block|{
if|if
condition|(
operator|!
name|ASYNC_TASKS
operator|.
name|isShutdown
argument_list|()
condition|)
block|{
name|LOG
operator|.
name|error
argument_list|(
literal|"Async read check was rejected from the executor: "
argument_list|,
name|ex
argument_list|)
expr_stmt|;
throw|throw
name|ex
throw|;
block|}
block|}
block|}
else|else
block|{
if|if
condition|(
name|LOG
operator|.
name|isTraceEnabled
argument_list|()
condition|)
block|{
name|LOG
operator|.
name|trace
argument_list|(
literal|"Message received since last read check, resetting flag: {}"
argument_list|,
name|this
argument_list|)
expr_stmt|;
block|}
block|}
name|commandReceived
operator|.
name|set
argument_list|(
literal|false
argument_list|)
expr_stmt|;
block|}
specifier|protected
specifier|abstract
name|void
name|processInboundWireFormatInfo
parameter_list|(
name|WireFormatInfo
name|info
parameter_list|)
throws|throws
name|IOException
function_decl|;
specifier|protected
specifier|abstract
name|void
name|processOutboundWireFormatInfo
parameter_list|(
name|WireFormatInfo
name|info
parameter_list|)
throws|throws
name|IOException
function_decl|;
annotation|@
name|Override
specifier|public
name|void
name|onCommand
parameter_list|(
name|Object
name|command
parameter_list|)
block|{
name|commandReceived
operator|.
name|set
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|inReceive
operator|.
name|set
argument_list|(
literal|true
argument_list|)
expr_stmt|;
try|try
block|{
if|if
condition|(
name|command
operator|.
name|getClass
argument_list|()
operator|==
name|KeepAliveInfo
operator|.
name|class
condition|)
block|{
name|KeepAliveInfo
name|info
init|=
operator|(
name|KeepAliveInfo
operator|)
name|command
decl_stmt|;
if|if
condition|(
name|info
operator|.
name|isResponseRequired
argument_list|()
condition|)
block|{
name|sendLock
operator|.
name|readLock
argument_list|()
operator|.
name|lock
argument_list|()
expr_stmt|;
try|try
block|{
name|info
operator|.
name|setResponseRequired
argument_list|(
literal|false
argument_list|)
expr_stmt|;
name|oneway
argument_list|(
name|info
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
name|onException
argument_list|(
name|e
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|sendLock
operator|.
name|readLock
argument_list|()
operator|.
name|unlock
argument_list|()
expr_stmt|;
block|}
block|}
block|}
else|else
block|{
if|if
condition|(
name|command
operator|.
name|getClass
argument_list|()
operator|==
name|WireFormatInfo
operator|.
name|class
condition|)
block|{
synchronized|synchronized
init|(
name|this
init|)
block|{
try|try
block|{
name|processInboundWireFormatInfo
argument_list|(
operator|(
name|WireFormatInfo
operator|)
name|command
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
name|onException
argument_list|(
name|e
argument_list|)
expr_stmt|;
block|}
block|}
block|}
name|transportListener
operator|.
name|onCommand
argument_list|(
name|command
argument_list|)
expr_stmt|;
block|}
block|}
finally|finally
block|{
name|inReceive
operator|.
name|set
argument_list|(
literal|false
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Override
specifier|public
name|void
name|oneway
parameter_list|(
name|Object
name|o
parameter_list|)
throws|throws
name|IOException
block|{
comment|// To prevent the inactivity monitor from sending a message while we
comment|// are performing a send we take a read lock. The inactivity monitor
comment|// sends its Heart-beat commands under a write lock. This means that
comment|// the MutexTransport is still responsible for synchronizing sends
name|sendLock
operator|.
name|readLock
argument_list|()
operator|.
name|lock
argument_list|()
expr_stmt|;
name|inSend
operator|.
name|set
argument_list|(
literal|true
argument_list|)
expr_stmt|;
try|try
block|{
name|doOnewaySend
argument_list|(
name|o
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|commandSent
operator|.
name|set
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|inSend
operator|.
name|set
argument_list|(
literal|false
argument_list|)
expr_stmt|;
name|sendLock
operator|.
name|readLock
argument_list|()
operator|.
name|unlock
argument_list|()
expr_stmt|;
block|}
block|}
comment|// Must be called under lock, either read or write on sendLock.
specifier|private
name|void
name|doOnewaySend
parameter_list|(
name|Object
name|command
parameter_list|)
throws|throws
name|IOException
block|{
if|if
condition|(
name|failed
operator|.
name|get
argument_list|()
condition|)
block|{
throw|throw
operator|new
name|InactivityIOException
argument_list|(
literal|"Cannot send, channel has already failed: "
operator|+
name|next
operator|.
name|getRemoteAddress
argument_list|()
argument_list|)
throw|;
block|}
if|if
condition|(
name|command
operator|.
name|getClass
argument_list|()
operator|==
name|WireFormatInfo
operator|.
name|class
condition|)
block|{
synchronized|synchronized
init|(
name|this
init|)
block|{
name|processOutboundWireFormatInfo
argument_list|(
operator|(
name|WireFormatInfo
operator|)
name|command
argument_list|)
expr_stmt|;
block|}
block|}
name|next
operator|.
name|oneway
argument_list|(
name|command
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|onException
parameter_list|(
name|IOException
name|error
parameter_list|)
block|{
if|if
condition|(
name|failed
operator|.
name|compareAndSet
argument_list|(
literal|false
argument_list|,
literal|true
argument_list|)
condition|)
block|{
name|stopMonitorThreads
argument_list|()
expr_stmt|;
if|if
condition|(
name|sendLock
operator|.
name|writeLock
argument_list|()
operator|.
name|isHeldByCurrentThread
argument_list|()
condition|)
block|{
name|sendLock
operator|.
name|writeLock
argument_list|()
operator|.
name|unlock
argument_list|()
expr_stmt|;
block|}
name|transportListener
operator|.
name|onException
argument_list|(
name|error
argument_list|)
expr_stmt|;
block|}
block|}
specifier|public
name|void
name|setUseKeepAlive
parameter_list|(
name|boolean
name|val
parameter_list|)
block|{
name|useKeepAlive
operator|=
name|val
expr_stmt|;
block|}
specifier|public
name|long
name|getConnectAttemptTimeout
parameter_list|()
block|{
return|return
name|connectAttemptTimeout
return|;
block|}
specifier|public
name|void
name|setConnectAttemptTimeout
parameter_list|(
name|long
name|connectionTimeout
parameter_list|)
block|{
name|this
operator|.
name|connectAttemptTimeout
operator|=
name|connectionTimeout
expr_stmt|;
block|}
specifier|public
name|long
name|getReadCheckTime
parameter_list|()
block|{
return|return
name|readCheckTime
return|;
block|}
specifier|public
name|void
name|setReadCheckTime
parameter_list|(
name|long
name|readCheckTime
parameter_list|)
block|{
name|this
operator|.
name|readCheckTime
operator|=
name|readCheckTime
expr_stmt|;
block|}
specifier|public
name|long
name|getWriteCheckTime
parameter_list|()
block|{
return|return
name|writeCheckTime
return|;
block|}
specifier|public
name|void
name|setWriteCheckTime
parameter_list|(
name|long
name|writeCheckTime
parameter_list|)
block|{
name|this
operator|.
name|writeCheckTime
operator|=
name|writeCheckTime
expr_stmt|;
block|}
specifier|public
name|long
name|getInitialDelayTime
parameter_list|()
block|{
return|return
name|initialDelayTime
return|;
block|}
specifier|public
name|void
name|setInitialDelayTime
parameter_list|(
name|long
name|initialDelayTime
parameter_list|)
block|{
name|this
operator|.
name|initialDelayTime
operator|=
name|initialDelayTime
expr_stmt|;
block|}
specifier|public
name|boolean
name|isKeepAliveResponseRequired
parameter_list|()
block|{
return|return
name|this
operator|.
name|keepAliveResponseRequired
return|;
block|}
specifier|public
name|void
name|setKeepAliveResponseRequired
parameter_list|(
name|boolean
name|value
parameter_list|)
block|{
name|this
operator|.
name|keepAliveResponseRequired
operator|=
name|value
expr_stmt|;
block|}
specifier|public
name|boolean
name|isMonitorStarted
parameter_list|()
block|{
return|return
name|this
operator|.
name|monitorStarted
operator|.
name|get
argument_list|()
return|;
block|}
specifier|abstract
specifier|protected
name|boolean
name|configuredOk
parameter_list|()
throws|throws
name|IOException
function_decl|;
specifier|public
specifier|synchronized
name|void
name|startConnectCheckTask
parameter_list|()
block|{
name|startConnectCheckTask
argument_list|(
name|getConnectAttemptTimeout
argument_list|()
argument_list|)
expr_stmt|;
block|}
specifier|public
specifier|synchronized
name|void
name|startConnectCheckTask
parameter_list|(
name|long
name|connectionTimeout
parameter_list|)
block|{
if|if
condition|(
name|connectionTimeout
operator|<=
literal|0
condition|)
block|{
return|return;
block|}
name|LOG
operator|.
name|trace
argument_list|(
literal|"Starting connection check task for: {}"
argument_list|,
name|this
argument_list|)
expr_stmt|;
name|this
operator|.
name|connectAttemptTimeout
operator|=
name|connectionTimeout
expr_stmt|;
if|if
condition|(
name|connectCheckerTask
operator|==
literal|null
condition|)
block|{
name|connectCheckerTask
operator|=
operator|new
name|SchedulerTimerTask
argument_list|(
name|connectChecker
argument_list|)
expr_stmt|;
synchronized|synchronized
init|(
name|AbstractInactivityMonitor
operator|.
name|class
init|)
block|{
if|if
condition|(
name|CHECKER_COUNTER
operator|==
literal|0
condition|)
block|{
if|if
condition|(
name|ASYNC_TASKS
operator|==
literal|null
operator|||
name|ASYNC_TASKS
operator|.
name|isShutdown
argument_list|()
condition|)
block|{
name|ASYNC_TASKS
operator|=
name|createExecutor
argument_list|()
expr_stmt|;
block|}
if|if
condition|(
name|READ_CHECK_TIMER
operator|==
literal|null
condition|)
block|{
name|READ_CHECK_TIMER
operator|=
operator|new
name|Timer
argument_list|(
literal|"ActiveMQ InactivityMonitor ReadCheckTimer"
argument_list|,
literal|true
argument_list|)
expr_stmt|;
block|}
block|}
name|CHECKER_COUNTER
operator|++
expr_stmt|;
name|READ_CHECK_TIMER
operator|.
name|schedule
argument_list|(
name|connectCheckerTask
argument_list|,
name|connectionTimeout
argument_list|)
expr_stmt|;
block|}
block|}
block|}
specifier|public
specifier|synchronized
name|void
name|stopConnectCheckTask
parameter_list|()
block|{
if|if
condition|(
name|connectCheckerTask
operator|!=
literal|null
condition|)
block|{
name|LOG
operator|.
name|trace
argument_list|(
literal|"Stopping connection check task for: {}"
argument_list|,
name|this
argument_list|)
expr_stmt|;
name|connectCheckerTask
operator|.
name|cancel
argument_list|()
expr_stmt|;
name|connectCheckerTask
operator|=
literal|null
expr_stmt|;
synchronized|synchronized
init|(
name|AbstractInactivityMonitor
operator|.
name|class
init|)
block|{
name|READ_CHECK_TIMER
operator|.
name|purge
argument_list|()
expr_stmt|;
name|CHECKER_COUNTER
operator|--
expr_stmt|;
block|}
block|}
block|}
specifier|protected
specifier|synchronized
name|void
name|startMonitorThreads
parameter_list|()
throws|throws
name|IOException
block|{
if|if
condition|(
name|monitorStarted
operator|.
name|get
argument_list|()
condition|)
block|{
return|return;
block|}
if|if
condition|(
operator|!
name|configuredOk
argument_list|()
condition|)
block|{
return|return;
block|}
if|if
condition|(
name|readCheckTime
operator|>
literal|0
condition|)
block|{
name|readCheckerTask
operator|=
operator|new
name|SchedulerTimerTask
argument_list|(
name|readChecker
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|writeCheckTime
operator|>
literal|0
condition|)
block|{
name|writeCheckerTask
operator|=
operator|new
name|SchedulerTimerTask
argument_list|(
name|writeChecker
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|writeCheckTime
operator|>
literal|0
operator|||
name|readCheckTime
operator|>
literal|0
condition|)
block|{
name|monitorStarted
operator|.
name|set
argument_list|(
literal|true
argument_list|)
expr_stmt|;
synchronized|synchronized
init|(
name|AbstractInactivityMonitor
operator|.
name|class
init|)
block|{
if|if
condition|(
name|ASYNC_TASKS
operator|==
literal|null
operator|||
name|ASYNC_TASKS
operator|.
name|isShutdown
argument_list|()
condition|)
block|{
name|ASYNC_TASKS
operator|=
name|createExecutor
argument_list|()
expr_stmt|;
block|}
if|if
condition|(
name|READ_CHECK_TIMER
operator|==
literal|null
condition|)
block|{
name|READ_CHECK_TIMER
operator|=
operator|new
name|Timer
argument_list|(
literal|"ActiveMQ InactivityMonitor ReadCheckTimer"
argument_list|,
literal|true
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|WRITE_CHECK_TIMER
operator|==
literal|null
condition|)
block|{
name|WRITE_CHECK_TIMER
operator|=
operator|new
name|Timer
argument_list|(
literal|"ActiveMQ InactivityMonitor WriteCheckTimer"
argument_list|,
literal|true
argument_list|)
expr_stmt|;
block|}
name|CHECKER_COUNTER
operator|++
expr_stmt|;
if|if
condition|(
name|readCheckTime
operator|>
literal|0
condition|)
block|{
name|READ_CHECK_TIMER
operator|.
name|schedule
argument_list|(
name|readCheckerTask
argument_list|,
name|initialDelayTime
argument_list|,
name|readCheckTime
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|writeCheckTime
operator|>
literal|0
condition|)
block|{
name|WRITE_CHECK_TIMER
operator|.
name|schedule
argument_list|(
name|writeCheckerTask
argument_list|,
name|initialDelayTime
argument_list|,
name|writeCheckTime
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|}
specifier|protected
specifier|synchronized
name|void
name|stopMonitorThreads
parameter_list|()
block|{
name|stopConnectCheckTask
argument_list|()
expr_stmt|;
if|if
condition|(
name|monitorStarted
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
name|readCheckerTask
operator|!=
literal|null
condition|)
block|{
name|readCheckerTask
operator|.
name|cancel
argument_list|()
expr_stmt|;
block|}
if|if
condition|(
name|writeCheckerTask
operator|!=
literal|null
condition|)
block|{
name|writeCheckerTask
operator|.
name|cancel
argument_list|()
expr_stmt|;
block|}
synchronized|synchronized
init|(
name|AbstractInactivityMonitor
operator|.
name|class
init|)
block|{
name|WRITE_CHECK_TIMER
operator|.
name|purge
argument_list|()
expr_stmt|;
name|READ_CHECK_TIMER
operator|.
name|purge
argument_list|()
expr_stmt|;
name|CHECKER_COUNTER
operator|--
expr_stmt|;
if|if
condition|(
name|CHECKER_COUNTER
operator|==
literal|0
condition|)
block|{
name|WRITE_CHECK_TIMER
operator|.
name|cancel
argument_list|()
expr_stmt|;
name|READ_CHECK_TIMER
operator|.
name|cancel
argument_list|()
expr_stmt|;
name|WRITE_CHECK_TIMER
operator|=
literal|null
expr_stmt|;
name|READ_CHECK_TIMER
operator|=
literal|null
expr_stmt|;
try|try
block|{
name|ThreadPoolUtils
operator|.
name|shutdownGraceful
argument_list|(
name|ASYNC_TASKS
argument_list|,
name|TimeUnit
operator|.
name|SECONDS
operator|.
name|toMillis
argument_list|(
literal|10
argument_list|)
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|ASYNC_TASKS
operator|=
literal|null
expr_stmt|;
block|}
block|}
block|}
block|}
block|}
specifier|private
specifier|final
name|ThreadFactory
name|factory
init|=
operator|new
name|ThreadFactory
argument_list|()
block|{
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
name|Thread
name|thread
init|=
operator|new
name|Thread
argument_list|(
name|runnable
argument_list|,
literal|"ActiveMQ InactivityMonitor Worker"
argument_list|)
decl_stmt|;
name|thread
operator|.
name|setDaemon
argument_list|(
literal|true
argument_list|)
expr_stmt|;
return|return
name|thread
return|;
block|}
block|}
decl_stmt|;
specifier|private
name|ThreadPoolExecutor
name|createExecutor
parameter_list|()
block|{
name|ThreadPoolExecutor
name|exec
init|=
operator|new
name|ThreadPoolExecutor
argument_list|(
literal|0
argument_list|,
name|Integer
operator|.
name|MAX_VALUE
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
name|factory
argument_list|)
decl_stmt|;
name|exec
operator|.
name|allowCoreThreadTimeOut
argument_list|(
literal|true
argument_list|)
expr_stmt|;
return|return
name|exec
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
literal|"org.apache.activemq.transport.AbstractInactivityMonitor.keepAliveTime"
argument_list|,
literal|30
argument_list|)
return|;
block|}
block|}
end_class

end_unit

