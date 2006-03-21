begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  *  * Copyright 2005-2006 The Apache Software Foundation  *  * Licensed under the Apache License, Version 2.0 (the "License");  * you may not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  * http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
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
name|org
operator|.
name|apache
operator|.
name|activemq
operator|.
name|command
operator|.
name|Command
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
name|Scheduler
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
name|edu
operator|.
name|emory
operator|.
name|mathcs
operator|.
name|backport
operator|.
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

begin_comment
comment|/**  * Used to make sure that commands are arriving periodically from the peer of the transport.    *   * @version $Revision$  */
end_comment

begin_class
specifier|public
class|class
name|InactivityMonitor
extends|extends
name|TransportFilter
block|{
specifier|private
specifier|final
name|Log
name|log
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
name|WireFormatInfo
name|localWireFormatInfo
decl_stmt|;
specifier|private
name|WireFormatInfo
name|remoteWireFormatInfo
decl_stmt|;
specifier|private
name|boolean
name|monitorStarted
init|=
literal|false
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
specifier|final
name|Runnable
name|readChecker
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
specifier|public
name|void
name|run
parameter_list|()
block|{
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
specifier|private
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
name|log
operator|.
name|debug
argument_list|(
literal|"A send is in progress"
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
condition|)
block|{
name|log
operator|.
name|debug
argument_list|(
literal|"No message sent since last write check, sending a KeepAliveInfo"
argument_list|)
expr_stmt|;
try|try
block|{
name|next
operator|.
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
else|else
block|{
name|log
operator|.
name|debug
argument_list|(
literal|"Message sent since last write check, resetting flag"
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
specifier|private
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
name|log
operator|.
name|debug
argument_list|(
literal|"A receive is in progress"
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
condition|)
block|{
name|log
operator|.
name|debug
argument_list|(
literal|"No message received since last read check! "
argument_list|)
expr_stmt|;
name|onException
argument_list|(
operator|new
name|InactivityIOException
argument_list|(
literal|"Channel was inactive for too long."
argument_list|)
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|log
operator|.
name|debug
argument_list|(
literal|"Message received since last read check, resetting flag: "
argument_list|)
expr_stmt|;
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
name|Command
name|command
parameter_list|)
block|{
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
name|isWireFormatInfo
argument_list|()
condition|)
block|{
synchronized|synchronized
init|(
name|this
init|)
block|{
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
name|onException
argument_list|(
name|e
argument_list|)
expr_stmt|;
block|}
block|}
block|}
name|getTransportListener
argument_list|()
operator|.
name|onCommand
argument_list|(
name|command
argument_list|)
expr_stmt|;
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
name|commandReceived
operator|.
name|set
argument_list|(
literal|true
argument_list|)
expr_stmt|;
block|}
block|}
specifier|public
name|void
name|oneway
parameter_list|(
name|Command
name|command
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
name|commandSent
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
name|isWireFormatInfo
argument_list|()
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
name|command
expr_stmt|;
name|startMonitorThreads
argument_list|()
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
finally|finally
block|{
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
name|stopMonitorThreads
argument_list|()
expr_stmt|;
name|getTransportListener
argument_list|()
operator|.
name|onException
argument_list|(
name|error
argument_list|)
expr_stmt|;
block|}
specifier|synchronized
specifier|private
name|void
name|startMonitorThreads
parameter_list|()
throws|throws
name|IOException
block|{
if|if
condition|(
name|monitorStarted
condition|)
return|return;
if|if
condition|(
name|localWireFormatInfo
operator|==
literal|null
condition|)
return|return;
if|if
condition|(
name|remoteWireFormatInfo
operator|==
literal|null
condition|)
return|return;
name|long
name|l
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
name|l
operator|>
literal|0
condition|)
block|{
name|Scheduler
operator|.
name|executePeriodically
argument_list|(
name|writeChecker
argument_list|,
name|l
operator|/
literal|2
argument_list|)
expr_stmt|;
name|Scheduler
operator|.
name|executePeriodically
argument_list|(
name|readChecker
argument_list|,
name|l
argument_list|)
expr_stmt|;
name|monitorStarted
operator|=
literal|true
expr_stmt|;
block|}
block|}
comment|/**      *       */
specifier|synchronized
specifier|private
name|void
name|stopMonitorThreads
parameter_list|()
block|{
if|if
condition|(
name|monitorStarted
condition|)
block|{
name|Scheduler
operator|.
name|cancel
argument_list|(
name|readChecker
argument_list|)
expr_stmt|;
name|Scheduler
operator|.
name|cancel
argument_list|(
name|writeChecker
argument_list|)
expr_stmt|;
name|monitorStarted
operator|=
literal|false
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

