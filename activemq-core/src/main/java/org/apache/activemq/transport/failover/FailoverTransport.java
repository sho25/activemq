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
name|failover
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
name|java
operator|.
name|util
operator|.
name|Random
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
name|ConcurrentHashMap
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
name|org
operator|.
name|apache
operator|.
name|activemq
operator|.
name|command
operator|.
name|BrokerInfo
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
name|state
operator|.
name|Tracked
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

begin_comment
comment|/**  * A Transport that is made reliable by being able to fail over to another  * transport when a transport failure is detected.  *   * @version $Revision$  */
end_comment

begin_class
specifier|public
class|class
name|FailoverTransport
implements|implements
name|CompositeTransport
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
name|FailoverTransport
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
name|CopyOnWriteArrayList
argument_list|<
name|URI
argument_list|>
name|uris
init|=
operator|new
name|CopyOnWriteArrayList
argument_list|<
name|URI
argument_list|>
argument_list|()
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
name|Object
name|sleepMutex
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
argument_list|<
name|Integer
argument_list|,
name|Command
argument_list|>
name|requestMap
init|=
operator|new
name|ConcurrentHashMap
argument_list|<
name|Integer
argument_list|,
name|Command
argument_list|>
argument_list|()
decl_stmt|;
specifier|private
name|URI
name|connectedTransportURI
decl_stmt|;
specifier|private
name|Transport
name|connectedTransport
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
name|boolean
name|randomize
init|=
literal|true
decl_stmt|;
specifier|private
name|boolean
name|initialized
decl_stmt|;
specifier|private
name|int
name|maxReconnectAttempts
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
name|Exception
name|connectionFailure
decl_stmt|;
specifier|private
specifier|final
name|TransportListener
name|myTransportListener
init|=
name|createTransportListener
argument_list|()
decl_stmt|;
specifier|public
name|FailoverTransport
parameter_list|()
throws|throws
name|InterruptedIOException
block|{
name|stateTracker
operator|.
name|setTrackTransactions
argument_list|(
literal|true
argument_list|)
expr_stmt|;
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
name|Exception
name|failure
init|=
literal|null
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
name|connectedTransport
operator|!=
literal|null
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
name|ArrayList
argument_list|<
name|Object
argument_list|>
name|connectList
init|=
name|getConnectList
argument_list|()
decl_stmt|;
if|if
condition|(
name|connectList
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
name|failure
operator|=
operator|new
name|IOException
argument_list|(
literal|"No uris available to connect to."
argument_list|)
expr_stmt|;
block|}
else|else
block|{
if|if
condition|(
operator|!
name|useExponentialBackOff
condition|)
block|{
name|reconnectDelay
operator|=
name|initialReconnectDelay
expr_stmt|;
block|}
name|Iterator
argument_list|<
name|Object
argument_list|>
name|iter
init|=
name|connectList
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
name|connectedTransport
operator|==
literal|null
operator|&&
operator|!
name|disposed
condition|;
name|i
operator|++
control|)
block|{
name|URI
name|uri
init|=
operator|(
name|URI
operator|)
name|iter
operator|.
name|next
argument_list|()
decl_stmt|;
try|try
block|{
name|LOG
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
name|t
operator|.
name|setTransportListener
argument_list|(
name|myTransportListener
argument_list|)
expr_stmt|;
name|t
operator|.
name|start
argument_list|()
expr_stmt|;
if|if
condition|(
name|started
condition|)
block|{
name|restoreTransport
argument_list|(
name|t
argument_list|)
expr_stmt|;
block|}
name|LOG
operator|.
name|debug
argument_list|(
literal|"Connection established"
argument_list|)
expr_stmt|;
name|reconnectDelay
operator|=
name|initialReconnectDelay
expr_stmt|;
name|connectedTransportURI
operator|=
name|uri
expr_stmt|;
name|connectedTransport
operator|=
name|t
expr_stmt|;
name|reconnectMutex
operator|.
name|notifyAll
argument_list|()
expr_stmt|;
name|connectFailures
operator|=
literal|0
expr_stmt|;
if|if
condition|(
name|transportListener
operator|!=
literal|null
condition|)
block|{
name|transportListener
operator|.
name|transportResumed
argument_list|()
expr_stmt|;
block|}
name|LOG
operator|.
name|info
argument_list|(
literal|"Successfully reconnected to "
operator|+
name|uri
argument_list|)
expr_stmt|;
return|return
literal|false
return|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
name|failure
operator|=
name|e
expr_stmt|;
name|LOG
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
block|}
block|}
block|}
block|}
if|if
condition|(
name|maxReconnectAttempts
operator|>
literal|0
operator|&&
operator|++
name|connectFailures
operator|>=
name|maxReconnectAttempts
condition|)
block|{
name|LOG
operator|.
name|error
argument_list|(
literal|"Failed to connect to transport after: "
operator|+
name|connectFailures
operator|+
literal|" attempt(s)"
argument_list|)
expr_stmt|;
name|connectionFailure
operator|=
name|failure
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
block|}
if|if
condition|(
operator|!
name|disposed
condition|)
block|{
name|LOG
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
synchronized|synchronized
init|(
name|sleepMutex
init|)
block|{
try|try
block|{
name|sleepMutex
operator|.
name|wait
argument_list|(
name|reconnectDelay
argument_list|)
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
if|if
condition|(
name|useExponentialBackOff
condition|)
block|{
comment|// Exponential increment of reconnect delay.
name|reconnectDelay
operator|*=
name|backOffMultiplier
expr_stmt|;
if|if
condition|(
name|reconnectDelay
operator|>
name|maxReconnectDelay
condition|)
block|{
name|reconnectDelay
operator|=
name|maxReconnectDelay
expr_stmt|;
block|}
block|}
block|}
return|return
operator|!
name|disposed
return|;
block|}
block|}
argument_list|,
literal|"ActiveMQ Failover Worker: "
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
name|TransportListener
name|createTransportListener
parameter_list|()
block|{
return|return
operator|new
name|TransportListener
argument_list|()
block|{
specifier|public
name|void
name|onCommand
parameter_list|(
name|Object
name|o
parameter_list|)
block|{
name|Command
name|command
init|=
operator|(
name|Command
operator|)
name|o
decl_stmt|;
if|if
condition|(
name|command
operator|==
literal|null
condition|)
block|{
return|return;
block|}
if|if
condition|(
name|command
operator|.
name|isResponse
argument_list|()
condition|)
block|{
name|Object
name|object
init|=
name|requestMap
operator|.
name|remove
argument_list|(
name|Integer
operator|.
name|valueOf
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
argument_list|)
decl_stmt|;
if|if
condition|(
name|object
operator|!=
literal|null
operator|&&
name|object
operator|.
name|getClass
argument_list|()
operator|==
name|Tracked
operator|.
name|class
condition|)
block|{
operator|(
operator|(
name|Tracked
operator|)
name|object
operator|)
operator|.
name|onResponses
argument_list|()
expr_stmt|;
block|}
block|}
if|if
condition|(
operator|!
name|initialized
condition|)
block|{
if|if
condition|(
name|command
operator|.
name|isBrokerInfo
argument_list|()
condition|)
block|{
name|BrokerInfo
name|info
init|=
operator|(
name|BrokerInfo
operator|)
name|command
decl_stmt|;
name|BrokerInfo
index|[]
name|peers
init|=
name|info
operator|.
name|getPeerBrokerInfos
argument_list|()
decl_stmt|;
if|if
condition|(
name|peers
operator|!=
literal|null
condition|)
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
name|peers
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|String
name|brokerString
init|=
name|peers
index|[
name|i
index|]
operator|.
name|getBrokerURL
argument_list|()
decl_stmt|;
name|add
argument_list|(
name|brokerString
argument_list|)
expr_stmt|;
block|}
block|}
name|initialized
operator|=
literal|true
expr_stmt|;
block|}
block|}
if|if
condition|(
name|transportListener
operator|!=
literal|null
condition|)
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
name|handleTransportFailure
argument_list|(
name|error
argument_list|)
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
specifier|public
name|void
name|transportInterupted
parameter_list|()
block|{
if|if
condition|(
name|transportListener
operator|!=
literal|null
condition|)
block|{
name|transportListener
operator|.
name|transportInterupted
argument_list|()
expr_stmt|;
block|}
block|}
specifier|public
name|void
name|transportResumed
parameter_list|()
block|{
if|if
condition|(
name|transportListener
operator|!=
literal|null
condition|)
block|{
name|transportListener
operator|.
name|transportResumed
argument_list|()
expr_stmt|;
block|}
block|}
block|}
return|;
block|}
specifier|final
name|void
name|handleTransportFailure
parameter_list|(
name|IOException
name|e
parameter_list|)
throws|throws
name|InterruptedException
block|{
if|if
condition|(
name|transportListener
operator|!=
literal|null
condition|)
block|{
name|transportListener
operator|.
name|transportInterupted
argument_list|()
expr_stmt|;
block|}
synchronized|synchronized
init|(
name|reconnectMutex
init|)
block|{
name|LOG
operator|.
name|warn
argument_list|(
literal|"Transport failed, attempting to automatically reconnect due to: "
operator|+
name|e
argument_list|,
name|e
argument_list|)
expr_stmt|;
if|if
condition|(
name|connectedTransport
operator|!=
literal|null
condition|)
block|{
name|initialized
operator|=
literal|false
expr_stmt|;
name|ServiceSupport
operator|.
name|dispose
argument_list|(
name|connectedTransport
argument_list|)
expr_stmt|;
name|connectedTransport
operator|=
literal|null
expr_stmt|;
name|connectedTransportURI
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
name|LOG
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
block|{
return|return;
block|}
name|started
operator|=
literal|true
expr_stmt|;
if|if
condition|(
name|connectedTransport
operator|!=
literal|null
condition|)
block|{
name|stateTracker
operator|.
name|restore
argument_list|(
name|connectedTransport
argument_list|)
expr_stmt|;
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
name|LOG
operator|.
name|debug
argument_list|(
literal|"Stopped."
argument_list|)
expr_stmt|;
if|if
condition|(
operator|!
name|started
condition|)
block|{
return|return;
block|}
name|started
operator|=
literal|false
expr_stmt|;
name|disposed
operator|=
literal|true
expr_stmt|;
if|if
condition|(
name|connectedTransport
operator|!=
literal|null
condition|)
block|{
name|connectedTransport
operator|.
name|stop
argument_list|()
expr_stmt|;
name|connectedTransport
operator|=
literal|null
expr_stmt|;
block|}
name|reconnectMutex
operator|.
name|notifyAll
argument_list|()
expr_stmt|;
block|}
synchronized|synchronized
init|(
name|sleepMutex
init|)
block|{
name|sleepMutex
operator|.
name|notifyAll
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
name|getReconnectDelay
parameter_list|()
block|{
return|return
name|reconnectDelay
return|;
block|}
specifier|public
name|void
name|setReconnectDelay
parameter_list|(
name|long
name|reconnectDelay
parameter_list|)
block|{
name|this
operator|.
name|reconnectDelay
operator|=
name|reconnectDelay
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
name|Transport
name|getConnectedTransport
parameter_list|()
block|{
return|return
name|connectedTransport
return|;
block|}
specifier|public
name|URI
name|getConnectedTransportURI
parameter_list|()
block|{
return|return
name|connectedTransportURI
return|;
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
comment|/**      * @return Returns the randomize.      */
specifier|public
name|boolean
name|isRandomize
parameter_list|()
block|{
return|return
name|randomize
return|;
block|}
comment|/**      * @param randomize The randomize to set.      */
specifier|public
name|void
name|setRandomize
parameter_list|(
name|boolean
name|randomize
parameter_list|)
block|{
name|this
operator|.
name|randomize
operator|=
name|randomize
expr_stmt|;
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
name|Command
name|command
init|=
operator|(
name|Command
operator|)
name|o
decl_stmt|;
name|Exception
name|error
init|=
literal|null
decl_stmt|;
try|try
block|{
synchronized|synchronized
init|(
name|reconnectMutex
init|)
block|{
comment|// Keep trying until the message is sent.
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
operator|!
name|disposed
condition|;
name|i
operator|++
control|)
block|{
try|try
block|{
comment|// Wait for transport to be connected.
while|while
condition|(
name|connectedTransport
operator|==
literal|null
operator|&&
operator|!
name|disposed
operator|&&
name|connectionFailure
operator|==
literal|null
condition|)
block|{
name|LOG
operator|.
name|trace
argument_list|(
literal|"Waiting for transport to reconnect."
argument_list|)
expr_stmt|;
try|try
block|{
name|reconnectMutex
operator|.
name|wait
argument_list|(
literal|1000
argument_list|)
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
name|LOG
operator|.
name|debug
argument_list|(
literal|"Interupted: "
operator|+
name|e
argument_list|,
name|e
argument_list|)
expr_stmt|;
block|}
block|}
if|if
condition|(
name|connectedTransport
operator|==
literal|null
condition|)
block|{
comment|// Previous loop may have exited due to use being
comment|// disposed.
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
break|break;
block|}
comment|// If it was a request and it was not being tracked by
comment|// the state tracker,
comment|// then hold it in the requestMap so that we can replay
comment|// it later.
name|Tracked
name|tracked
init|=
name|stateTracker
operator|.
name|track
argument_list|(
name|command
argument_list|)
decl_stmt|;
if|if
condition|(
name|tracked
operator|!=
literal|null
operator|&&
name|tracked
operator|.
name|isWaitingForResponse
argument_list|()
condition|)
block|{
name|requestMap
operator|.
name|put
argument_list|(
name|Integer
operator|.
name|valueOf
argument_list|(
name|command
operator|.
name|getCommandId
argument_list|()
argument_list|)
argument_list|,
name|tracked
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|tracked
operator|==
literal|null
operator|&&
name|command
operator|.
name|isResponseRequired
argument_list|()
condition|)
block|{
name|requestMap
operator|.
name|put
argument_list|(
name|Integer
operator|.
name|valueOf
argument_list|(
name|command
operator|.
name|getCommandId
argument_list|()
argument_list|)
argument_list|,
name|command
argument_list|)
expr_stmt|;
block|}
comment|// Send the message.
try|try
block|{
name|connectedTransport
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
comment|// If the command was not tracked.. we will retry in
comment|// this method
if|if
condition|(
name|tracked
operator|==
literal|null
condition|)
block|{
comment|// since we will retry in this method.. take it
comment|// out of the request
comment|// map so that it is not sent 2 times on
comment|// recovery
if|if
condition|(
name|command
operator|.
name|isResponseRequired
argument_list|()
condition|)
block|{
name|requestMap
operator|.
name|remove
argument_list|(
name|Integer
operator|.
name|valueOf
argument_list|(
name|command
operator|.
name|getCommandId
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|// Rethrow the exception so it will handled by
comment|// the outer catch
throw|throw
name|e
throw|;
block|}
block|}
return|return;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
name|LOG
operator|.
name|debug
argument_list|(
literal|"Send oneway attempt: "
operator|+
name|i
operator|+
literal|" failed."
argument_list|)
expr_stmt|;
name|handleTransportFailure
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
name|Thread
operator|.
name|currentThread
argument_list|()
operator|.
name|interrupt
argument_list|()
expr_stmt|;
throw|throw
operator|new
name|InterruptedIOException
argument_list|()
throw|;
block|}
if|if
condition|(
operator|!
name|disposed
condition|)
block|{
if|if
condition|(
name|error
operator|!=
literal|null
condition|)
block|{
if|if
condition|(
name|error
operator|instanceof
name|IOException
condition|)
block|{
throw|throw
operator|(
name|IOException
operator|)
name|error
throw|;
block|}
throw|throw
name|IOExceptionSupport
operator|.
name|create
argument_list|(
name|error
argument_list|)
throw|;
block|}
block|}
block|}
specifier|public
name|FutureResponse
name|asyncRequest
parameter_list|(
name|Object
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
name|Object
name|request
parameter_list|(
name|Object
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
name|Object
name|request
parameter_list|(
name|Object
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
name|add
parameter_list|(
name|URI
name|u
index|[]
parameter_list|)
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
name|u
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
if|if
condition|(
operator|!
name|uris
operator|.
name|contains
argument_list|(
name|u
index|[
name|i
index|]
argument_list|)
condition|)
block|{
name|uris
operator|.
name|add
argument_list|(
name|u
index|[
name|i
index|]
argument_list|)
expr_stmt|;
block|}
block|}
name|reconnect
argument_list|()
expr_stmt|;
block|}
specifier|public
name|void
name|remove
parameter_list|(
name|URI
name|u
index|[]
parameter_list|)
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
name|u
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|uris
operator|.
name|remove
argument_list|(
name|u
index|[
name|i
index|]
argument_list|)
expr_stmt|;
block|}
name|reconnect
argument_list|()
expr_stmt|;
block|}
specifier|public
name|void
name|add
parameter_list|(
name|String
name|u
parameter_list|)
block|{
try|try
block|{
name|URI
name|uri
init|=
operator|new
name|URI
argument_list|(
name|u
argument_list|)
decl_stmt|;
if|if
condition|(
operator|!
name|uris
operator|.
name|contains
argument_list|(
name|uri
argument_list|)
condition|)
block|{
name|uris
operator|.
name|add
argument_list|(
name|uri
argument_list|)
expr_stmt|;
block|}
name|reconnect
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
name|LOG
operator|.
name|error
argument_list|(
literal|"Failed to parse URI: "
operator|+
name|u
argument_list|)
expr_stmt|;
block|}
block|}
specifier|public
name|void
name|reconnect
parameter_list|()
block|{
name|LOG
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
specifier|private
name|ArrayList
argument_list|<
name|Object
argument_list|>
name|getConnectList
parameter_list|()
block|{
name|ArrayList
argument_list|<
name|Object
argument_list|>
name|l
init|=
operator|new
name|ArrayList
argument_list|<
name|Object
argument_list|>
argument_list|(
name|uris
argument_list|)
decl_stmt|;
if|if
condition|(
name|randomize
condition|)
block|{
comment|// Randomly, reorder the list by random swapping
name|Random
name|r
init|=
operator|new
name|Random
argument_list|()
decl_stmt|;
name|r
operator|.
name|setSeed
argument_list|(
name|System
operator|.
name|currentTimeMillis
argument_list|()
argument_list|)
expr_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|l
operator|.
name|size
argument_list|()
condition|;
name|i
operator|++
control|)
block|{
name|int
name|p
init|=
name|r
operator|.
name|nextInt
argument_list|(
name|l
operator|.
name|size
argument_list|()
argument_list|)
decl_stmt|;
name|Object
name|t
init|=
name|l
operator|.
name|get
argument_list|(
name|p
argument_list|)
decl_stmt|;
name|l
operator|.
name|set
argument_list|(
name|p
argument_list|,
name|l
operator|.
name|get
argument_list|(
name|i
argument_list|)
argument_list|)
expr_stmt|;
name|l
operator|.
name|set
argument_list|(
name|i
argument_list|,
name|t
argument_list|)
expr_stmt|;
block|}
block|}
return|return
name|l
return|;
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
parameter_list|<
name|T
parameter_list|>
name|T
name|narrow
parameter_list|(
name|Class
argument_list|<
name|T
argument_list|>
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
name|target
operator|.
name|cast
argument_list|(
name|this
argument_list|)
return|;
block|}
synchronized|synchronized
init|(
name|reconnectMutex
init|)
block|{
if|if
condition|(
name|connectedTransport
operator|!=
literal|null
condition|)
block|{
return|return
name|connectedTransport
operator|.
name|narrow
argument_list|(
name|target
argument_list|)
return|;
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
name|Transport
name|t
parameter_list|)
throws|throws
name|Exception
throws|,
name|IOException
block|{
name|t
operator|.
name|start
argument_list|()
expr_stmt|;
name|stateTracker
operator|.
name|restore
argument_list|(
name|t
argument_list|)
expr_stmt|;
for|for
control|(
name|Iterator
argument_list|<
name|Command
argument_list|>
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
name|Command
name|command
init|=
name|iter2
operator|.
name|next
argument_list|()
decl_stmt|;
name|t
operator|.
name|oneway
argument_list|(
name|command
argument_list|)
expr_stmt|;
block|}
block|}
specifier|public
name|boolean
name|isUseExponentialBackOff
parameter_list|()
block|{
return|return
name|useExponentialBackOff
return|;
block|}
specifier|public
name|void
name|setUseExponentialBackOff
parameter_list|(
name|boolean
name|useExponentialBackOff
parameter_list|)
block|{
name|this
operator|.
name|useExponentialBackOff
operator|=
name|useExponentialBackOff
expr_stmt|;
block|}
specifier|public
name|String
name|toString
parameter_list|()
block|{
return|return
name|connectedTransportURI
operator|==
literal|null
condition|?
literal|"unconnected"
else|:
name|connectedTransportURI
operator|.
name|toString
argument_list|()
return|;
block|}
specifier|public
name|String
name|getRemoteAddress
parameter_list|()
block|{
if|if
condition|(
name|connectedTransport
operator|!=
literal|null
condition|)
block|{
return|return
name|connectedTransport
operator|.
name|getRemoteAddress
argument_list|()
return|;
block|}
return|return
literal|null
return|;
block|}
specifier|public
name|boolean
name|isFaultTolerant
parameter_list|()
block|{
return|return
literal|true
return|;
block|}
block|}
end_class

end_unit

