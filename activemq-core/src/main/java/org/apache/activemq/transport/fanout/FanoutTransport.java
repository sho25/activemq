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
operator|.
name|fanout
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
name|io
operator|.
name|InterruptedIOException
import|;
end_import

begin_import
import|import
name|java
operator|.
name|net
operator|.
name|URI
import|;
end_import

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
name|ConsumerInfo
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
name|Message
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
name|Response
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
name|state
operator|.
name|ConnectionStateTracker
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
name|DefaultThreadPools
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
name|transport
operator|.
name|CompositeTransport
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
name|transport
operator|.
name|DefaultTransportListener
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
name|transport
operator|.
name|FutureResponse
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
name|transport
operator|.
name|ResponseCallback
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
name|transport
operator|.
name|Transport
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
name|transport
operator|.
name|TransportFactory
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
name|transport
operator|.
name|TransportListener
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
name|IOExceptionSupport
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
name|ConcurrentHashMap
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
name|AtomicInteger
import|;
end_import

begin_comment
comment|/**  * A Transport that fans out a connection to multiple brokers.  *   * @version $Revision$  */
end_comment

begin_class
specifier|public
class|class
name|FanoutTransport
implements|implements
name|CompositeTransport
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
name|FanoutTransport
operator|.
name|class
argument_list|)
decl_stmt|;
specifier|private
name|TransportListener
name|transportListener
decl_stmt|;
specifier|private
name|boolean
name|disposed
decl_stmt|;
specifier|private
specifier|final
name|Object
name|reconnectMutex
init|=
operator|new
name|Object
argument_list|()
decl_stmt|;
specifier|private
specifier|final
name|ConnectionStateTracker
name|stateTracker
init|=
operator|new
name|ConnectionStateTracker
argument_list|()
decl_stmt|;
specifier|private
specifier|final
name|ConcurrentHashMap
name|requestMap
init|=
operator|new
name|ConcurrentHashMap
argument_list|()
decl_stmt|;
specifier|private
specifier|final
name|TaskRunner
name|reconnectTask
decl_stmt|;
specifier|private
name|boolean
name|started
decl_stmt|;
specifier|private
name|ArrayList
name|transports
init|=
operator|new
name|ArrayList
argument_list|()
decl_stmt|;
specifier|private
name|int
name|connectedCount
init|=
literal|0
decl_stmt|;
specifier|private
name|int
name|minAckCount
init|=
literal|2
decl_stmt|;
specifier|private
name|long
name|initialReconnectDelay
init|=
literal|10
decl_stmt|;
specifier|private
name|long
name|maxReconnectDelay
init|=
literal|1000
operator|*
literal|30
decl_stmt|;
specifier|private
name|long
name|backOffMultiplier
init|=
literal|2
decl_stmt|;
specifier|private
name|boolean
name|useExponentialBackOff
init|=
literal|true
decl_stmt|;
specifier|private
name|int
name|maxReconnectAttempts
decl_stmt|;
specifier|private
name|Exception
name|connectionFailure
decl_stmt|;
specifier|private
name|FanoutTransportHandler
name|primary
decl_stmt|;
specifier|static
class|class
name|RequestCounter
block|{
specifier|final
name|Command
name|command
decl_stmt|;
specifier|final
name|AtomicInteger
name|ackCount
decl_stmt|;
name|RequestCounter
parameter_list|(
name|Command
name|command
parameter_list|,
name|int
name|count
parameter_list|)
block|{
name|this
operator|.
name|command
operator|=
name|command
expr_stmt|;
name|this
operator|.
name|ackCount
operator|=
operator|new
name|AtomicInteger
argument_list|(
name|count
argument_list|)
expr_stmt|;
block|}
specifier|public
name|String
name|toString
parameter_list|()
block|{
return|return
name|command
operator|.
name|getCommandId
argument_list|()
operator|+
literal|"="
operator|+
name|ackCount
operator|.
name|get
argument_list|()
return|;
block|}
block|}
class|class
name|FanoutTransportHandler
extends|extends
name|DefaultTransportListener
block|{
specifier|private
specifier|final
name|URI
name|uri
decl_stmt|;
specifier|private
name|Transport
name|transport
decl_stmt|;
specifier|private
name|int
name|connectFailures
decl_stmt|;
specifier|private
name|long
name|reconnectDelay
init|=
name|initialReconnectDelay
decl_stmt|;
specifier|private
name|long
name|reconnectDate
decl_stmt|;
specifier|public
name|FanoutTransportHandler
parameter_list|(
name|URI
name|uri
parameter_list|)
block|{
name|this
operator|.
name|uri
operator|=
name|uri
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
if|if
condition|(
name|command
operator|.
name|isResponse
argument_list|()
condition|)
block|{
name|Integer
name|id
init|=
operator|new
name|Integer
argument_list|(
operator|(
operator|(
name|Response
operator|)
name|command
operator|)
operator|.
name|getCorrelationId
argument_list|()
argument_list|)
decl_stmt|;
name|RequestCounter
name|rc
init|=
operator|(
name|RequestCounter
operator|)
name|requestMap
operator|.
name|get
argument_list|(
name|id
argument_list|)
decl_stmt|;
if|if
condition|(
name|rc
operator|!=
literal|null
condition|)
block|{
if|if
condition|(
name|rc
operator|.
name|ackCount
operator|.
name|decrementAndGet
argument_list|()
operator|<=
literal|0
condition|)
block|{
name|requestMap
operator|.
name|remove
argument_list|(
name|id
argument_list|)
expr_stmt|;
name|transportListener
operator|.
name|onCommand
argument_list|(
name|command
argument_list|)
expr_stmt|;
block|}
block|}
else|else
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
else|else
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
specifier|public
name|void
name|onException
parameter_list|(
name|IOException
name|error
parameter_list|)
block|{
try|try
block|{
synchronized|synchronized
init|(
name|reconnectMutex
init|)
block|{
if|if
condition|(
name|transport
operator|==
literal|null
condition|)
return|return;
name|log
operator|.
name|debug
argument_list|(
literal|"Transport failed, starting up reconnect task"
argument_list|,
name|error
argument_list|)
expr_stmt|;
name|ServiceSupport
operator|.
name|dispose
argument_list|(
name|transport
argument_list|)
expr_stmt|;
name|transport
operator|=
literal|null
expr_stmt|;
name|connectedCount
operator|--
expr_stmt|;
if|if
condition|(
name|primary
operator|==
name|this
condition|)
block|{
name|primary
operator|=
literal|null
expr_stmt|;
block|}
name|reconnectTask
operator|.
name|wakeup
argument_list|()
expr_stmt|;
block|}
block|}
catch|catch
parameter_list|(
name|InterruptedException
name|e
parameter_list|)
block|{
name|transportListener
operator|.
name|onException
argument_list|(
operator|new
name|InterruptedIOException
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
block|}
specifier|public
name|FanoutTransport
parameter_list|()
throws|throws
name|InterruptedIOException
block|{
comment|// Setup a task that is used to reconnect the a connection async.
name|reconnectTask
operator|=
name|DefaultThreadPools
operator|.
name|getDefaultTaskRunnerFactory
argument_list|()
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
name|doConnect
argument_list|()
return|;
block|}
block|}
argument_list|)
expr_stmt|;
block|}
comment|/**      * @return      */
specifier|private
name|boolean
name|doConnect
parameter_list|()
block|{
name|long
name|closestReconnectDate
init|=
literal|0
decl_stmt|;
synchronized|synchronized
init|(
name|reconnectMutex
init|)
block|{
if|if
condition|(
name|disposed
operator|||
name|connectionFailure
operator|!=
literal|null
condition|)
block|{
name|reconnectMutex
operator|.
name|notifyAll
argument_list|()
expr_stmt|;
block|}
if|if
condition|(
name|transports
operator|.
name|size
argument_list|()
operator|==
name|connectedCount
operator|||
name|disposed
operator|||
name|connectionFailure
operator|!=
literal|null
condition|)
block|{
return|return
literal|false
return|;
block|}
else|else
block|{
if|if
condition|(
name|transports
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
comment|//                    connectionFailure = new IOException("No uris available to connect to.");
block|}
else|else
block|{
comment|// Try to connect them up.
name|Iterator
name|iter
init|=
name|transports
operator|.
name|iterator
argument_list|()
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|iter
operator|.
name|hasNext
argument_list|()
operator|&&
operator|!
name|disposed
condition|;
name|i
operator|++
control|)
block|{
name|long
name|now
init|=
name|System
operator|.
name|currentTimeMillis
argument_list|()
decl_stmt|;
name|FanoutTransportHandler
name|fanoutHandler
init|=
operator|(
name|FanoutTransportHandler
operator|)
name|iter
operator|.
name|next
argument_list|()
decl_stmt|;
if|if
condition|(
name|fanoutHandler
operator|.
name|transport
operator|!=
literal|null
condition|)
block|{
continue|continue;
block|}
comment|// Are we waiting a little to try to reconnect this one?
if|if
condition|(
name|fanoutHandler
operator|.
name|reconnectDate
operator|!=
literal|0
operator|&&
name|fanoutHandler
operator|.
name|reconnectDate
operator|>
name|now
condition|)
block|{
if|if
condition|(
name|closestReconnectDate
operator|==
literal|0
operator|||
name|fanoutHandler
operator|.
name|reconnectDate
operator|<
name|closestReconnectDate
condition|)
block|{
name|closestReconnectDate
operator|=
name|fanoutHandler
operator|.
name|reconnectDate
expr_stmt|;
block|}
continue|continue;
block|}
name|URI
name|uri
init|=
name|fanoutHandler
operator|.
name|uri
decl_stmt|;
try|try
block|{
name|log
operator|.
name|debug
argument_list|(
literal|"Stopped: "
operator|+
name|this
argument_list|)
expr_stmt|;
name|log
operator|.
name|debug
argument_list|(
literal|"Attempting connect to: "
operator|+
name|uri
argument_list|)
expr_stmt|;
name|Transport
name|t
init|=
name|TransportFactory
operator|.
name|compositeConnect
argument_list|(
name|uri
argument_list|)
decl_stmt|;
name|log
operator|.
name|debug
argument_list|(
literal|"Connection established"
argument_list|)
expr_stmt|;
name|fanoutHandler
operator|.
name|transport
operator|=
name|t
expr_stmt|;
name|fanoutHandler
operator|.
name|reconnectDelay
operator|=
literal|10
expr_stmt|;
name|fanoutHandler
operator|.
name|connectFailures
operator|=
literal|0
expr_stmt|;
if|if
condition|(
name|primary
operator|==
literal|null
condition|)
block|{
name|primary
operator|=
name|fanoutHandler
expr_stmt|;
block|}
name|t
operator|.
name|setTransportListener
argument_list|(
name|fanoutHandler
argument_list|)
expr_stmt|;
name|connectedCount
operator|++
expr_stmt|;
if|if
condition|(
name|started
condition|)
block|{
name|restoreTransport
argument_list|(
name|fanoutHandler
argument_list|)
expr_stmt|;
block|}
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
name|log
operator|.
name|debug
argument_list|(
literal|"Connect fail to: "
operator|+
name|uri
operator|+
literal|", reason: "
operator|+
name|e
argument_list|)
expr_stmt|;
if|if
condition|(
name|maxReconnectAttempts
operator|>
literal|0
operator|&&
operator|++
name|fanoutHandler
operator|.
name|connectFailures
operator|>=
name|maxReconnectAttempts
condition|)
block|{
name|log
operator|.
name|error
argument_list|(
literal|"Failed to connect to transport after: "
operator|+
name|fanoutHandler
operator|.
name|connectFailures
operator|+
literal|" attempt(s)"
argument_list|)
expr_stmt|;
name|connectionFailure
operator|=
name|e
expr_stmt|;
name|reconnectMutex
operator|.
name|notifyAll
argument_list|()
expr_stmt|;
return|return
literal|false
return|;
block|}
else|else
block|{
if|if
condition|(
name|useExponentialBackOff
condition|)
block|{
comment|// Exponential increment of reconnect delay.
name|fanoutHandler
operator|.
name|reconnectDelay
operator|*=
name|backOffMultiplier
expr_stmt|;
if|if
condition|(
name|fanoutHandler
operator|.
name|reconnectDelay
operator|>
name|maxReconnectDelay
condition|)
name|fanoutHandler
operator|.
name|reconnectDelay
operator|=
name|maxReconnectDelay
expr_stmt|;
block|}
name|fanoutHandler
operator|.
name|reconnectDate
operator|=
name|now
operator|+
name|fanoutHandler
operator|.
name|reconnectDelay
expr_stmt|;
if|if
condition|(
name|closestReconnectDate
operator|==
literal|0
operator|||
name|fanoutHandler
operator|.
name|reconnectDate
operator|<
name|closestReconnectDate
condition|)
block|{
name|closestReconnectDate
operator|=
name|fanoutHandler
operator|.
name|reconnectDate
expr_stmt|;
block|}
block|}
block|}
block|}
if|if
condition|(
name|transports
operator|.
name|size
argument_list|()
operator|==
name|connectedCount
operator|||
name|disposed
condition|)
block|{
name|reconnectMutex
operator|.
name|notifyAll
argument_list|()
expr_stmt|;
return|return
literal|false
return|;
block|}
block|}
block|}
block|}
try|try
block|{
name|long
name|reconnectDelay
init|=
name|closestReconnectDate
operator|-
name|System
operator|.
name|currentTimeMillis
argument_list|()
decl_stmt|;
if|if
condition|(
name|reconnectDelay
operator|>
literal|0
condition|)
block|{
name|log
operator|.
name|debug
argument_list|(
literal|"Waiting "
operator|+
name|reconnectDelay
operator|+
literal|" ms before attempting connection. "
argument_list|)
expr_stmt|;
name|Thread
operator|.
name|sleep
argument_list|(
name|reconnectDelay
argument_list|)
expr_stmt|;
block|}
block|}
catch|catch
parameter_list|(
name|InterruptedException
name|e1
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
return|return
literal|true
return|;
block|}
specifier|public
name|void
name|start
parameter_list|()
throws|throws
name|Exception
block|{
synchronized|synchronized
init|(
name|reconnectMutex
init|)
block|{
name|log
operator|.
name|debug
argument_list|(
literal|"Started."
argument_list|)
expr_stmt|;
if|if
condition|(
name|started
condition|)
return|return;
name|started
operator|=
literal|true
expr_stmt|;
for|for
control|(
name|Iterator
name|iter
init|=
name|transports
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
name|FanoutTransportHandler
name|th
init|=
operator|(
name|FanoutTransportHandler
operator|)
name|iter
operator|.
name|next
argument_list|()
decl_stmt|;
if|if
condition|(
name|th
operator|.
name|transport
operator|!=
literal|null
condition|)
block|{
name|restoreTransport
argument_list|(
name|th
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|}
specifier|public
name|void
name|stop
parameter_list|()
throws|throws
name|Exception
block|{
synchronized|synchronized
init|(
name|reconnectMutex
init|)
block|{
name|ServiceStopper
name|ss
init|=
operator|new
name|ServiceStopper
argument_list|()
decl_stmt|;
if|if
condition|(
operator|!
name|started
condition|)
return|return;
name|started
operator|=
literal|false
expr_stmt|;
name|disposed
operator|=
literal|true
expr_stmt|;
for|for
control|(
name|Iterator
name|iter
init|=
name|transports
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
name|FanoutTransportHandler
name|th
init|=
operator|(
name|FanoutTransportHandler
operator|)
name|iter
operator|.
name|next
argument_list|()
decl_stmt|;
if|if
condition|(
name|th
operator|.
name|transport
operator|!=
literal|null
condition|)
block|{
name|ss
operator|.
name|stop
argument_list|(
name|th
operator|.
name|transport
argument_list|)
expr_stmt|;
block|}
block|}
name|log
operator|.
name|debug
argument_list|(
literal|"Stopped: "
operator|+
name|this
argument_list|)
expr_stmt|;
name|ss
operator|.
name|throwFirstException
argument_list|()
expr_stmt|;
block|}
name|reconnectTask
operator|.
name|shutdown
argument_list|()
expr_stmt|;
block|}
specifier|public
name|long
name|getInitialReconnectDelay
parameter_list|()
block|{
return|return
name|initialReconnectDelay
return|;
block|}
specifier|public
name|void
name|setInitialReconnectDelay
parameter_list|(
name|long
name|initialReconnectDelay
parameter_list|)
block|{
name|this
operator|.
name|initialReconnectDelay
operator|=
name|initialReconnectDelay
expr_stmt|;
block|}
specifier|public
name|long
name|getMaxReconnectDelay
parameter_list|()
block|{
return|return
name|maxReconnectDelay
return|;
block|}
specifier|public
name|void
name|setMaxReconnectDelay
parameter_list|(
name|long
name|maxReconnectDelay
parameter_list|)
block|{
name|this
operator|.
name|maxReconnectDelay
operator|=
name|maxReconnectDelay
expr_stmt|;
block|}
specifier|public
name|long
name|getReconnectDelayExponent
parameter_list|()
block|{
return|return
name|backOffMultiplier
return|;
block|}
specifier|public
name|void
name|setReconnectDelayExponent
parameter_list|(
name|long
name|reconnectDelayExponent
parameter_list|)
block|{
name|this
operator|.
name|backOffMultiplier
operator|=
name|reconnectDelayExponent
expr_stmt|;
block|}
specifier|public
name|int
name|getMaxReconnectAttempts
parameter_list|()
block|{
return|return
name|maxReconnectAttempts
return|;
block|}
specifier|public
name|void
name|setMaxReconnectAttempts
parameter_list|(
name|int
name|maxReconnectAttempts
parameter_list|)
block|{
name|this
operator|.
name|maxReconnectAttempts
operator|=
name|maxReconnectAttempts
expr_stmt|;
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
try|try
block|{
synchronized|synchronized
init|(
name|reconnectMutex
init|)
block|{
comment|// If it was a request and it was not being tracked by
comment|// the state tracker,
comment|// then hold it in the requestMap so that we can replay
comment|// it later.
name|boolean
name|fanout
init|=
name|isFanoutCommand
argument_list|(
name|command
argument_list|)
decl_stmt|;
if|if
condition|(
operator|!
name|stateTracker
operator|.
name|track
argument_list|(
name|command
argument_list|)
operator|&&
name|command
operator|.
name|isResponseRequired
argument_list|()
condition|)
block|{
name|int
name|size
init|=
name|fanout
condition|?
name|minAckCount
else|:
literal|1
decl_stmt|;
name|requestMap
operator|.
name|put
argument_list|(
operator|new
name|Integer
argument_list|(
name|command
operator|.
name|getCommandId
argument_list|()
argument_list|)
argument_list|,
operator|new
name|RequestCounter
argument_list|(
name|command
argument_list|,
name|size
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|// Wait for transport to be connected.
while|while
condition|(
name|connectedCount
operator|!=
name|minAckCount
operator|&&
operator|!
name|disposed
operator|&&
name|connectionFailure
operator|==
literal|null
condition|)
block|{
name|log
operator|.
name|debug
argument_list|(
literal|"Waiting for at least "
operator|+
name|minAckCount
operator|+
literal|" transports to be connected."
argument_list|)
expr_stmt|;
name|reconnectMutex
operator|.
name|wait
argument_list|(
literal|1000
argument_list|)
expr_stmt|;
block|}
comment|// Still not fully connected.
if|if
condition|(
name|connectedCount
operator|!=
name|minAckCount
condition|)
block|{
name|Exception
name|error
decl_stmt|;
comment|// Throw the right kind of error..
if|if
condition|(
name|disposed
condition|)
block|{
name|error
operator|=
operator|new
name|IOException
argument_list|(
literal|"Transport disposed."
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|connectionFailure
operator|!=
literal|null
condition|)
block|{
name|error
operator|=
name|connectionFailure
expr_stmt|;
block|}
else|else
block|{
name|error
operator|=
operator|new
name|IOException
argument_list|(
literal|"Unexpected failure."
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|error
operator|instanceof
name|IOException
condition|)
throw|throw
operator|(
name|IOException
operator|)
name|error
throw|;
throw|throw
name|IOExceptionSupport
operator|.
name|create
argument_list|(
name|error
argument_list|)
throw|;
block|}
comment|// Send the message.
if|if
condition|(
name|fanout
condition|)
block|{
for|for
control|(
name|Iterator
name|iter
init|=
name|transports
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
name|FanoutTransportHandler
name|th
init|=
operator|(
name|FanoutTransportHandler
operator|)
name|iter
operator|.
name|next
argument_list|()
decl_stmt|;
if|if
condition|(
name|th
operator|.
name|transport
operator|!=
literal|null
condition|)
block|{
try|try
block|{
name|th
operator|.
name|transport
operator|.
name|oneway
argument_list|(
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
name|log
operator|.
name|debug
argument_list|(
literal|"Send attempt: failed."
argument_list|)
expr_stmt|;
name|th
operator|.
name|onException
argument_list|(
name|e
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|}
else|else
block|{
try|try
block|{
name|primary
operator|.
name|transport
operator|.
name|oneway
argument_list|(
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
name|log
operator|.
name|debug
argument_list|(
literal|"Send attempt: failed."
argument_list|)
expr_stmt|;
name|primary
operator|.
name|onException
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
name|InterruptedException
name|e
parameter_list|)
block|{
comment|// Some one may be trying to stop our thread.
throw|throw
operator|new
name|InterruptedIOException
argument_list|()
throw|;
block|}
block|}
comment|/**      * @param command      * @return      */
specifier|private
name|boolean
name|isFanoutCommand
parameter_list|(
name|Command
name|command
parameter_list|)
block|{
if|if
condition|(
name|command
operator|.
name|isMessage
argument_list|()
condition|)
block|{
return|return
operator|(
operator|(
name|Message
operator|)
name|command
operator|)
operator|.
name|getDestination
argument_list|()
operator|.
name|isTopic
argument_list|()
return|;
block|}
if|if
condition|(
name|command
operator|.
name|getDataStructureType
argument_list|()
operator|==
name|ConsumerInfo
operator|.
name|DATA_STRUCTURE_TYPE
condition|)
block|{
return|return
literal|false
return|;
block|}
return|return
literal|true
return|;
block|}
specifier|public
name|FutureResponse
name|asyncRequest
parameter_list|(
name|Command
name|command
parameter_list|,
name|ResponseCallback
name|responseCallback
parameter_list|)
throws|throws
name|IOException
block|{
throw|throw
operator|new
name|AssertionError
argument_list|(
literal|"Unsupported Method"
argument_list|)
throw|;
block|}
specifier|public
name|Response
name|request
parameter_list|(
name|Command
name|command
parameter_list|)
throws|throws
name|IOException
block|{
throw|throw
operator|new
name|AssertionError
argument_list|(
literal|"Unsupported Method"
argument_list|)
throw|;
block|}
specifier|public
name|Response
name|request
parameter_list|(
name|Command
name|command
parameter_list|,
name|int
name|timeout
parameter_list|)
throws|throws
name|IOException
block|{
throw|throw
operator|new
name|AssertionError
argument_list|(
literal|"Unsupported Method"
argument_list|)
throw|;
block|}
specifier|public
name|void
name|reconnect
parameter_list|()
block|{
name|log
operator|.
name|debug
argument_list|(
literal|"Waking up reconnect task"
argument_list|)
expr_stmt|;
try|try
block|{
name|reconnectTask
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
specifier|public
name|TransportListener
name|getTransportListener
parameter_list|()
block|{
return|return
name|transportListener
return|;
block|}
specifier|public
name|void
name|setTransportListener
parameter_list|(
name|TransportListener
name|commandListener
parameter_list|)
block|{
name|this
operator|.
name|transportListener
operator|=
name|commandListener
expr_stmt|;
block|}
specifier|public
name|Object
name|narrow
parameter_list|(
name|Class
name|target
parameter_list|)
block|{
if|if
condition|(
name|target
operator|.
name|isAssignableFrom
argument_list|(
name|getClass
argument_list|()
argument_list|)
condition|)
block|{
return|return
name|this
return|;
block|}
synchronized|synchronized
init|(
name|reconnectMutex
init|)
block|{
for|for
control|(
name|Iterator
name|iter
init|=
name|transports
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
name|FanoutTransportHandler
name|th
init|=
operator|(
name|FanoutTransportHandler
operator|)
name|iter
operator|.
name|next
argument_list|()
decl_stmt|;
if|if
condition|(
name|th
operator|.
name|transport
operator|!=
literal|null
condition|)
block|{
name|Object
name|rc
init|=
name|th
operator|.
name|transport
operator|.
name|narrow
argument_list|(
name|target
argument_list|)
decl_stmt|;
if|if
condition|(
name|rc
operator|!=
literal|null
condition|)
return|return
name|rc
return|;
block|}
block|}
block|}
return|return
literal|null
return|;
block|}
specifier|protected
name|void
name|restoreTransport
parameter_list|(
name|FanoutTransportHandler
name|th
parameter_list|)
throws|throws
name|Exception
throws|,
name|IOException
block|{
name|th
operator|.
name|transport
operator|.
name|start
argument_list|()
expr_stmt|;
name|stateTracker
operator|.
name|setRestoreConsumers
argument_list|(
name|th
operator|.
name|transport
operator|==
name|primary
argument_list|)
expr_stmt|;
name|stateTracker
operator|.
name|restore
argument_list|(
name|th
operator|.
name|transport
argument_list|)
expr_stmt|;
for|for
control|(
name|Iterator
name|iter2
init|=
name|requestMap
operator|.
name|values
argument_list|()
operator|.
name|iterator
argument_list|()
init|;
name|iter2
operator|.
name|hasNext
argument_list|()
condition|;
control|)
block|{
name|RequestCounter
name|rc
init|=
operator|(
name|RequestCounter
operator|)
name|iter2
operator|.
name|next
argument_list|()
decl_stmt|;
name|th
operator|.
name|transport
operator|.
name|oneway
argument_list|(
name|rc
operator|.
name|command
argument_list|)
expr_stmt|;
block|}
block|}
specifier|public
name|void
name|add
parameter_list|(
name|URI
name|uris
index|[]
parameter_list|)
block|{
synchronized|synchronized
init|(
name|reconnectMutex
init|)
block|{
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|uris
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|URI
name|uri
init|=
name|uris
index|[
name|i
index|]
decl_stmt|;
name|boolean
name|match
init|=
literal|false
decl_stmt|;
for|for
control|(
name|Iterator
name|iter
init|=
name|transports
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
name|FanoutTransportHandler
name|th
init|=
operator|(
name|FanoutTransportHandler
operator|)
name|iter
operator|.
name|next
argument_list|()
decl_stmt|;
if|if
condition|(
name|th
operator|.
name|uri
operator|.
name|equals
argument_list|(
name|uri
argument_list|)
condition|)
block|{
name|match
operator|=
literal|true
expr_stmt|;
break|break;
block|}
block|}
if|if
condition|(
operator|!
name|match
condition|)
block|{
name|FanoutTransportHandler
name|th
init|=
operator|new
name|FanoutTransportHandler
argument_list|(
name|uri
argument_list|)
decl_stmt|;
name|transports
operator|.
name|add
argument_list|(
name|th
argument_list|)
expr_stmt|;
name|reconnect
argument_list|()
expr_stmt|;
block|}
block|}
block|}
block|}
specifier|public
name|void
name|remove
parameter_list|(
name|URI
name|uris
index|[]
parameter_list|)
block|{
synchronized|synchronized
init|(
name|reconnectMutex
init|)
block|{
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|uris
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|URI
name|uri
init|=
name|uris
index|[
name|i
index|]
decl_stmt|;
name|boolean
name|match
init|=
literal|false
decl_stmt|;
for|for
control|(
name|Iterator
name|iter
init|=
name|transports
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
name|FanoutTransportHandler
name|th
init|=
operator|(
name|FanoutTransportHandler
operator|)
name|iter
operator|.
name|next
argument_list|()
decl_stmt|;
if|if
condition|(
name|th
operator|.
name|uri
operator|.
name|equals
argument_list|(
name|uri
argument_list|)
condition|)
block|{
if|if
condition|(
name|th
operator|.
name|transport
operator|!=
literal|null
condition|)
block|{
name|ServiceSupport
operator|.
name|dispose
argument_list|(
name|th
operator|.
name|transport
argument_list|)
expr_stmt|;
name|connectedCount
operator|--
expr_stmt|;
block|}
name|iter
operator|.
name|remove
argument_list|()
expr_stmt|;
break|break;
block|}
block|}
block|}
block|}
block|}
block|}
end_class

end_unit

