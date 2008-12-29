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
name|vm
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
name|concurrent
operator|.
name|LinkedBlockingQueue
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
name|AtomicLong
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
name|activemq
operator|.
name|thread
operator|.
name|Valve
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
name|TransportDisposedIOException
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

begin_comment
comment|/**  * A Transport implementation that uses direct method invocations.  *   * @version $Revision$  */
end_comment

begin_class
specifier|public
class|class
name|VMTransport
implements|implements
name|Transport
implements|,
name|Task
block|{
specifier|private
specifier|static
specifier|final
name|Object
name|DISCONNECT
init|=
operator|new
name|Object
argument_list|()
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|AtomicLong
name|NEXT_ID
init|=
operator|new
name|AtomicLong
argument_list|(
literal|0
argument_list|)
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|TaskRunnerFactory
name|TASK_RUNNER_FACTORY
init|=
operator|new
name|TaskRunnerFactory
argument_list|(
literal|"VMTransport"
argument_list|,
name|Thread
operator|.
name|NORM_PRIORITY
argument_list|,
literal|true
argument_list|,
literal|1000
argument_list|)
decl_stmt|;
specifier|protected
name|VMTransport
name|peer
decl_stmt|;
specifier|protected
name|TransportListener
name|transportListener
decl_stmt|;
specifier|protected
name|boolean
name|disposed
decl_stmt|;
specifier|protected
name|boolean
name|marshal
decl_stmt|;
specifier|protected
name|boolean
name|network
decl_stmt|;
specifier|protected
name|boolean
name|async
init|=
literal|true
decl_stmt|;
specifier|protected
name|int
name|asyncQueueDepth
init|=
literal|2000
decl_stmt|;
specifier|protected
name|LinkedBlockingQueue
argument_list|<
name|Object
argument_list|>
name|messageQueue
decl_stmt|;
specifier|protected
name|boolean
name|started
decl_stmt|;
specifier|protected
specifier|final
name|URI
name|location
decl_stmt|;
specifier|protected
specifier|final
name|long
name|id
decl_stmt|;
specifier|private
name|TaskRunner
name|taskRunner
decl_stmt|;
specifier|private
specifier|final
name|Object
name|lazyInitMutext
init|=
operator|new
name|Object
argument_list|()
decl_stmt|;
specifier|private
specifier|final
name|Valve
name|enqueueValve
init|=
operator|new
name|Valve
argument_list|(
literal|true
argument_list|)
decl_stmt|;
specifier|private
specifier|final
name|AtomicBoolean
name|stopping
init|=
operator|new
name|AtomicBoolean
argument_list|()
decl_stmt|;
specifier|public
name|VMTransport
parameter_list|(
name|URI
name|location
parameter_list|)
block|{
name|this
operator|.
name|location
operator|=
name|location
expr_stmt|;
name|this
operator|.
name|id
operator|=
name|NEXT_ID
operator|.
name|getAndIncrement
argument_list|()
expr_stmt|;
block|}
specifier|public
name|void
name|setPeer
parameter_list|(
name|VMTransport
name|peer
parameter_list|)
block|{
name|this
operator|.
name|peer
operator|=
name|peer
expr_stmt|;
block|}
specifier|public
name|void
name|oneway
parameter_list|(
name|Object
name|command
parameter_list|)
throws|throws
name|IOException
block|{
if|if
condition|(
name|disposed
condition|)
block|{
throw|throw
operator|new
name|TransportDisposedIOException
argument_list|(
literal|"Transport disposed."
argument_list|)
throw|;
block|}
if|if
condition|(
name|peer
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|IOException
argument_list|(
literal|"Peer not connected."
argument_list|)
throw|;
block|}
name|TransportListener
name|transportListener
init|=
literal|null
decl_stmt|;
try|try
block|{
comment|// Disable the peer from changing his state while we try to enqueue onto him.
name|peer
operator|.
name|enqueueValve
operator|.
name|increment
argument_list|()
expr_stmt|;
if|if
condition|(
name|peer
operator|.
name|disposed
operator|||
name|peer
operator|.
name|stopping
operator|.
name|get
argument_list|()
condition|)
block|{
throw|throw
operator|new
name|TransportDisposedIOException
argument_list|(
literal|"Peer ("
operator|+
name|peer
operator|.
name|toString
argument_list|()
operator|+
literal|") disposed."
argument_list|)
throw|;
block|}
if|if
condition|(
name|peer
operator|.
name|started
condition|)
block|{
if|if
condition|(
name|peer
operator|.
name|async
condition|)
block|{
name|peer
operator|.
name|getMessageQueue
argument_list|()
operator|.
name|put
argument_list|(
name|command
argument_list|)
expr_stmt|;
name|peer
operator|.
name|wakeup
argument_list|()
expr_stmt|;
block|}
else|else
block|{
name|transportListener
operator|=
name|peer
operator|.
name|transportListener
expr_stmt|;
block|}
block|}
else|else
block|{
name|peer
operator|.
name|getMessageQueue
argument_list|()
operator|.
name|put
argument_list|(
name|command
argument_list|)
expr_stmt|;
block|}
block|}
catch|catch
parameter_list|(
name|InterruptedException
name|e
parameter_list|)
block|{
throw|throw
name|IOExceptionSupport
operator|.
name|create
argument_list|(
name|e
argument_list|)
throw|;
block|}
finally|finally
block|{
comment|// Allow the peer to change state again...
name|peer
operator|.
name|enqueueValve
operator|.
name|decrement
argument_list|()
expr_stmt|;
block|}
if|if
condition|(
name|transportListener
operator|!=
literal|null
condition|)
block|{
if|if
condition|(
name|command
operator|==
name|DISCONNECT
condition|)
block|{
name|transportListener
operator|.
name|onException
argument_list|(
operator|new
name|TransportDisposedIOException
argument_list|(
literal|"Peer ("
operator|+
name|peer
operator|.
name|toString
argument_list|()
operator|+
literal|") disposed."
argument_list|)
argument_list|)
expr_stmt|;
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
block|}
specifier|public
name|void
name|start
parameter_list|()
throws|throws
name|Exception
block|{
if|if
condition|(
name|transportListener
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|IOException
argument_list|(
literal|"TransportListener not set."
argument_list|)
throw|;
block|}
try|try
block|{
name|enqueueValve
operator|.
name|turnOff
argument_list|()
expr_stmt|;
if|if
condition|(
name|messageQueue
operator|!=
literal|null
operator|&&
operator|!
name|async
condition|)
block|{
name|Object
name|command
decl_stmt|;
while|while
condition|(
operator|(
name|command
operator|=
name|messageQueue
operator|.
name|poll
argument_list|()
operator|)
operator|!=
literal|null
operator|&&
operator|!
name|stopping
operator|.
name|get
argument_list|()
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
name|started
operator|=
literal|true
expr_stmt|;
name|wakeup
argument_list|()
expr_stmt|;
block|}
finally|finally
block|{
name|enqueueValve
operator|.
name|turnOn
argument_list|()
expr_stmt|;
block|}
comment|// If we get stopped while starting up, then do the actual stop now
comment|// that the enqueueValve is back on.
if|if
condition|(
name|stopping
operator|.
name|get
argument_list|()
condition|)
block|{
name|stop
argument_list|()
expr_stmt|;
block|}
block|}
specifier|public
name|void
name|stop
parameter_list|()
throws|throws
name|Exception
block|{
name|stopping
operator|.
name|set
argument_list|(
literal|true
argument_list|)
expr_stmt|;
comment|// If stop() is called while being start()ed.. then we can't stop until we return to the start() method.
if|if
condition|(
name|enqueueValve
operator|.
name|isOn
argument_list|()
condition|)
block|{
comment|// let the peer know that we are disconnecting..
try|try
block|{
name|oneway
argument_list|(
name|DISCONNECT
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|ignore
parameter_list|)
block|{             }
name|TaskRunner
name|tr
init|=
literal|null
decl_stmt|;
try|try
block|{
name|enqueueValve
operator|.
name|turnOff
argument_list|()
expr_stmt|;
if|if
condition|(
operator|!
name|disposed
condition|)
block|{
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
name|taskRunner
operator|!=
literal|null
condition|)
block|{
name|tr
operator|=
name|taskRunner
expr_stmt|;
name|taskRunner
operator|=
literal|null
expr_stmt|;
block|}
block|}
block|}
finally|finally
block|{
name|stopping
operator|.
name|set
argument_list|(
literal|false
argument_list|)
expr_stmt|;
name|enqueueValve
operator|.
name|turnOn
argument_list|()
expr_stmt|;
block|}
if|if
condition|(
name|tr
operator|!=
literal|null
condition|)
block|{
name|tr
operator|.
name|shutdown
argument_list|(
literal|1000
argument_list|)
expr_stmt|;
block|}
block|}
block|}
comment|/**      * @see org.apache.activemq.thread.Task#iterate()      */
specifier|public
name|boolean
name|iterate
parameter_list|()
block|{
specifier|final
name|TransportListener
name|tl
decl_stmt|;
try|try
block|{
comment|// Disable changing the state variables while we are running...
name|enqueueValve
operator|.
name|increment
argument_list|()
expr_stmt|;
name|tl
operator|=
name|transportListener
expr_stmt|;
if|if
condition|(
operator|!
name|started
operator|||
name|disposed
operator|||
name|tl
operator|==
literal|null
operator|||
name|stopping
operator|.
name|get
argument_list|()
condition|)
block|{
if|if
condition|(
name|stopping
operator|.
name|get
argument_list|()
condition|)
block|{
comment|// drain the queue it since folks could be blocked putting on to
comment|// it and that would not allow the stop() method for finishing up.
name|getMessageQueue
argument_list|()
operator|.
name|clear
argument_list|()
expr_stmt|;
block|}
return|return
literal|false
return|;
block|}
block|}
catch|catch
parameter_list|(
name|InterruptedException
name|e
parameter_list|)
block|{
return|return
literal|false
return|;
block|}
finally|finally
block|{
name|enqueueValve
operator|.
name|decrement
argument_list|()
expr_stmt|;
block|}
name|LinkedBlockingQueue
argument_list|<
name|Object
argument_list|>
name|mq
init|=
name|getMessageQueue
argument_list|()
decl_stmt|;
name|Object
name|command
init|=
name|mq
operator|.
name|poll
argument_list|()
decl_stmt|;
if|if
condition|(
name|command
operator|!=
literal|null
condition|)
block|{
if|if
condition|(
name|command
operator|==
name|DISCONNECT
condition|)
block|{
name|tl
operator|.
name|onException
argument_list|(
operator|new
name|TransportDisposedIOException
argument_list|(
literal|"Peer ("
operator|+
name|peer
operator|.
name|toString
argument_list|()
operator|+
literal|") disposed."
argument_list|)
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|tl
operator|.
name|onCommand
argument_list|(
name|command
argument_list|)
expr_stmt|;
block|}
return|return
operator|!
name|mq
operator|.
name|isEmpty
argument_list|()
return|;
block|}
else|else
block|{
return|return
literal|false
return|;
block|}
block|}
specifier|public
name|void
name|setTransportListener
parameter_list|(
name|TransportListener
name|commandListener
parameter_list|)
block|{
try|try
block|{
try|try
block|{
name|enqueueValve
operator|.
name|turnOff
argument_list|()
expr_stmt|;
name|this
operator|.
name|transportListener
operator|=
name|commandListener
expr_stmt|;
name|wakeup
argument_list|()
expr_stmt|;
block|}
finally|finally
block|{
name|enqueueValve
operator|.
name|turnOn
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
throw|throw
operator|new
name|RuntimeException
argument_list|(
name|e
argument_list|)
throw|;
block|}
block|}
specifier|private
name|LinkedBlockingQueue
argument_list|<
name|Object
argument_list|>
name|getMessageQueue
parameter_list|()
block|{
synchronized|synchronized
init|(
name|lazyInitMutext
init|)
block|{
if|if
condition|(
name|messageQueue
operator|==
literal|null
condition|)
block|{
name|messageQueue
operator|=
operator|new
name|LinkedBlockingQueue
argument_list|<
name|Object
argument_list|>
argument_list|(
name|this
operator|.
name|asyncQueueDepth
argument_list|)
expr_stmt|;
block|}
return|return
name|messageQueue
return|;
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
name|TransportListener
name|getTransportListener
parameter_list|()
block|{
return|return
name|transportListener
return|;
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
return|return
literal|null
return|;
block|}
specifier|public
name|boolean
name|isMarshal
parameter_list|()
block|{
return|return
name|marshal
return|;
block|}
specifier|public
name|void
name|setMarshal
parameter_list|(
name|boolean
name|marshal
parameter_list|)
block|{
name|this
operator|.
name|marshal
operator|=
name|marshal
expr_stmt|;
block|}
specifier|public
name|boolean
name|isNetwork
parameter_list|()
block|{
return|return
name|network
return|;
block|}
specifier|public
name|void
name|setNetwork
parameter_list|(
name|boolean
name|network
parameter_list|)
block|{
name|this
operator|.
name|network
operator|=
name|network
expr_stmt|;
block|}
specifier|public
name|String
name|toString
parameter_list|()
block|{
return|return
name|location
operator|+
literal|"#"
operator|+
name|id
return|;
block|}
specifier|public
name|String
name|getRemoteAddress
parameter_list|()
block|{
if|if
condition|(
name|peer
operator|!=
literal|null
condition|)
block|{
return|return
name|peer
operator|.
name|toString
argument_list|()
return|;
block|}
return|return
literal|null
return|;
block|}
comment|/**      * @return the async      */
specifier|public
name|boolean
name|isAsync
parameter_list|()
block|{
return|return
name|async
return|;
block|}
comment|/**      * @param async the async to set      */
specifier|public
name|void
name|setAsync
parameter_list|(
name|boolean
name|async
parameter_list|)
block|{
name|this
operator|.
name|async
operator|=
name|async
expr_stmt|;
block|}
comment|/**      * @return the asyncQueueDepth      */
specifier|public
name|int
name|getAsyncQueueDepth
parameter_list|()
block|{
return|return
name|asyncQueueDepth
return|;
block|}
comment|/**      * @param asyncQueueDepth the asyncQueueDepth to set      */
specifier|public
name|void
name|setAsyncQueueDepth
parameter_list|(
name|int
name|asyncQueueDepth
parameter_list|)
block|{
name|this
operator|.
name|asyncQueueDepth
operator|=
name|asyncQueueDepth
expr_stmt|;
block|}
specifier|protected
name|void
name|wakeup
parameter_list|()
block|{
if|if
condition|(
name|async
condition|)
block|{
synchronized|synchronized
init|(
name|lazyInitMutext
init|)
block|{
if|if
condition|(
name|taskRunner
operator|==
literal|null
condition|)
block|{
name|taskRunner
operator|=
name|TASK_RUNNER_FACTORY
operator|.
name|createTaskRunner
argument_list|(
name|this
argument_list|,
literal|"VMTransport: "
operator|+
name|toString
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
try|try
block|{
name|taskRunner
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
name|boolean
name|isFaultTolerant
parameter_list|()
block|{
return|return
literal|false
return|;
block|}
specifier|public
name|boolean
name|isDisposed
parameter_list|()
block|{
return|return
name|disposed
return|;
block|}
specifier|public
name|boolean
name|isConnected
parameter_list|()
block|{
return|return
name|started
return|;
block|}
specifier|public
name|void
name|reconnect
parameter_list|(
name|URI
name|uri
parameter_list|)
throws|throws
name|IOException
block|{
throw|throw
operator|new
name|IOException
argument_list|(
literal|"Not supported"
argument_list|)
throw|;
block|}
block|}
end_class

end_unit

