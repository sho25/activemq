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

begin_comment
comment|/**  * Used to make sure that commands are arriving periodically from the peer of  * the transport.  *   * @version $Revision$  */
end_comment

begin_class
specifier|public
class|class
name|InactivityMonitor
extends|extends
name|TransportFilter
block|{
specifier|private
specifier|static
specifier|final
name|Log
name|LOG
init|=
name|LogFactory
operator|.
name|getLog
argument_list|(
name|InactivityMonitor
operator|.
name|class
argument_list|)
decl_stmt|;
specifier|private
specifier|static
specifier|final
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
name|WireFormatInfo
name|localWireFormatInfo
decl_stmt|;
specifier|private
name|WireFormatInfo
name|remoteWireFormatInfo
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
name|SchedulerTimerTask
name|writeCheckerTask
decl_stmt|;
specifier|private
name|SchedulerTimerTask
name|readCheckerTask
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
operator|&&
name|LOG
operator|.
name|isDebugEnabled
argument_list|()
condition|)
block|{
name|LOG
operator|.
name|debug
argument_list|(
literal|""
operator|+
operator|(
name|now
operator|-
name|lastRunTime
operator|)
operator|+
literal|" ms elapsed since last read check."
argument_list|)
expr_stmt|;
block|}
name|lastRunTime
operator|=
name|now
expr_stmt|;
name|readCheck
argument_list|()
expr_stmt|;
block|}
block|}
decl_stmt|;
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
operator|&&
name|LOG
operator|.
name|isDebugEnabled
argument_list|()
condition|)
block|{
name|LOG
operator|.
name|debug
argument_list|(
literal|""
operator|+
operator|(
name|now
operator|-
name|lastRunTime
operator|)
operator|+
literal|" ms elapsed since last write check."
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
block|}
decl_stmt|;
specifier|public
name|InactivityMonitor
parameter_list|(
name|Transport
name|next
parameter_list|)
block|{
name|super
argument_list|(
name|next
argument_list|)
expr_stmt|;
block|}
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
literal|"A send is in progress"
argument_list|)
expr_stmt|;
block|}
return|return;
block|}
if|if
condition|(
operator|!
name|commandSent
operator|.
name|get
argument_list|()
condition|)
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
literal|"No message sent since last write check, sending a KeepAliveInfo"
argument_list|)
expr_stmt|;
block|}
name|ASYNC_TASKS
operator|.
name|execute
argument_list|(
operator|new
name|Runnable
argument_list|()
block|{
specifier|public
name|void
name|run
parameter_list|()
block|{
try|try
block|{
name|oneway
argument_list|(
operator|new
name|KeepAliveInfo
argument_list|()
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
empty_stmt|;
block|}
argument_list|)
expr_stmt|;
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
literal|"Message sent since last write check, resetting flag"
argument_list|)
expr_stmt|;
block|}
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
if|if
condition|(
name|inReceive
operator|.
name|get
argument_list|()
condition|)
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
literal|"A receive is in progress"
argument_list|)
expr_stmt|;
block|}
return|return;
block|}
if|if
condition|(
operator|!
name|commandReceived
operator|.
name|get
argument_list|()
condition|)
block|{
if|if
condition|(
name|LOG
operator|.
name|isDebugEnabled
argument_list|()
condition|)
block|{
name|LOG
operator|.
name|debug
argument_list|(
literal|"No message received since last read check for "
operator|+
name|toString
argument_list|()
operator|+
literal|"! Throwing InactivityIOException."
argument_list|)
expr_stmt|;
block|}
comment|// TODO: use a thread pool for this..
name|ASYNC_TASKS
operator|.
name|execute
argument_list|(
operator|new
name|Runnable
argument_list|()
block|{
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
literal|"Channel was inactive for too long: "
operator|+
name|next
operator|.
name|getRemoteAddress
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
empty_stmt|;
block|}
argument_list|)
expr_stmt|;
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
literal|"Message received since last read check, resetting flag: "
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
name|IOException
name|error
init|=
literal|null
decl_stmt|;
name|remoteWireFormatInfo
operator|=
operator|(
name|WireFormatInfo
operator|)
name|command
expr_stmt|;
try|try
block|{
name|startMonitorThreads
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
name|error
operator|=
name|e
expr_stmt|;
block|}
if|if
condition|(
name|error
operator|!=
literal|null
condition|)
block|{
name|onException
argument_list|(
name|error
argument_list|)
expr_stmt|;
block|}
block|}
block|}
synchronized|synchronized
init|(
name|readChecker
init|)
block|{
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
comment|// Disable inactivity monitoring while processing a command.
name|inSend
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
name|o
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
name|localWireFormatInfo
operator|=
operator|(
name|WireFormatInfo
operator|)
name|o
expr_stmt|;
name|startMonitorThreads
argument_list|()
expr_stmt|;
block|}
block|}
synchronized|synchronized
init|(
name|writeChecker
init|)
block|{
name|next
operator|.
name|oneway
argument_list|(
name|o
argument_list|)
expr_stmt|;
block|}
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
block|}
block|}
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
name|monitorStarted
operator|.
name|get
argument_list|()
condition|)
block|{
name|stopMonitorThreads
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
specifier|private
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
name|localWireFormatInfo
operator|==
literal|null
condition|)
block|{
return|return;
block|}
if|if
condition|(
name|remoteWireFormatInfo
operator|==
literal|null
condition|)
block|{
return|return;
block|}
name|long
name|checkTime
init|=
name|Math
operator|.
name|min
argument_list|(
name|localWireFormatInfo
operator|.
name|getMaxInactivityDuration
argument_list|()
argument_list|,
name|remoteWireFormatInfo
operator|.
name|getMaxInactivityDuration
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
name|checkTime
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
name|writeCheckerTask
operator|=
operator|new
name|SchedulerTimerTask
argument_list|(
name|writeChecker
argument_list|)
expr_stmt|;
name|readCheckerTask
operator|=
operator|new
name|SchedulerTimerTask
argument_list|(
name|readChecker
argument_list|)
expr_stmt|;
name|long
name|writeCheckTime
init|=
name|checkTime
operator|/
literal|3
decl_stmt|;
synchronized|synchronized
init|(
name|InactivityMonitor
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
name|READ_CHECK_TIMER
operator|=
operator|new
name|Timer
argument_list|(
literal|"InactivityMonitor ReadCheck"
argument_list|)
expr_stmt|;
name|WRITE_CHECK_TIMER
operator|=
operator|new
name|Timer
argument_list|(
literal|"InactivityMonitor WriteCheck"
argument_list|)
expr_stmt|;
block|}
name|CHECKER_COUNTER
operator|++
expr_stmt|;
name|WRITE_CHECK_TIMER
operator|.
name|scheduleAtFixedRate
argument_list|(
name|writeCheckerTask
argument_list|,
name|writeCheckTime
argument_list|,
name|writeCheckTime
argument_list|)
expr_stmt|;
name|READ_CHECK_TIMER
operator|.
name|scheduleAtFixedRate
argument_list|(
name|readCheckerTask
argument_list|,
name|checkTime
argument_list|,
name|checkTime
argument_list|)
expr_stmt|;
block|}
block|}
block|}
comment|/**      *      */
specifier|private
specifier|synchronized
name|void
name|stopMonitorThreads
parameter_list|()
block|{
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
name|readCheckerTask
operator|.
name|cancel
argument_list|()
expr_stmt|;
name|writeCheckerTask
operator|.
name|cancel
argument_list|()
expr_stmt|;
synchronized|synchronized
init|(
name|InactivityMonitor
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
block|}
block|}
block|}
block|}
static|static
block|{
name|ASYNC_TASKS
operator|=
operator|new
name|ThreadPoolExecutor
argument_list|(
literal|0
argument_list|,
name|Integer
operator|.
name|MAX_VALUE
argument_list|,
literal|10
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
literal|"InactivityMonitor Async Task: "
operator|+
name|runnable
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
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

