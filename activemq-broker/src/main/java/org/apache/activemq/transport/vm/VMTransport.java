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
name|concurrent
operator|.
name|BlockingQueue
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
name|command
operator|.
name|ShutdownInfo
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
comment|/**  * A Transport implementation that uses direct method invocations.  */
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
specifier|protected
specifier|static
specifier|final
name|Logger
name|LOG
init|=
name|LoggerFactory
operator|.
name|getLogger
argument_list|(
name|VMTransport
operator|.
name|class
argument_list|)
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
comment|// Transport Configuration
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
name|marshal
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
specifier|final
name|URI
name|location
decl_stmt|;
specifier|protected
specifier|final
name|long
name|id
decl_stmt|;
comment|// Implementation
specifier|private
specifier|volatile
name|LinkedBlockingQueue
argument_list|<
name|Object
argument_list|>
name|messageQueue
decl_stmt|;
specifier|private
specifier|volatile
name|TaskRunnerFactory
name|taskRunnerFactory
decl_stmt|;
specifier|private
specifier|volatile
name|TaskRunner
name|taskRunner
decl_stmt|;
comment|// Transport State
specifier|protected
specifier|final
name|AtomicBoolean
name|started
init|=
operator|new
name|AtomicBoolean
argument_list|()
decl_stmt|;
specifier|protected
specifier|final
name|AtomicBoolean
name|disposed
init|=
operator|new
name|AtomicBoolean
argument_list|()
decl_stmt|;
specifier|private
specifier|volatile
name|int
name|receiveCounter
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
annotation|@
name|Override
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
operator|.
name|get
argument_list|()
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
try|try
block|{
if|if
condition|(
name|peer
operator|.
name|disposed
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
return|return;
block|}
if|if
condition|(
operator|!
name|peer
operator|.
name|started
operator|.
name|get
argument_list|()
condition|)
block|{
name|LinkedBlockingQueue
argument_list|<
name|Object
argument_list|>
name|pending
init|=
name|peer
operator|.
name|getMessageQueue
argument_list|()
decl_stmt|;
name|int
name|sleepTimeMillis
decl_stmt|;
name|boolean
name|accepted
init|=
literal|false
decl_stmt|;
do|do
block|{
name|sleepTimeMillis
operator|=
literal|0
expr_stmt|;
comment|// the pending queue is drained on start so we need to ensure we add before
comment|// the drain commences, otherwise we never get the command dispatched!
synchronized|synchronized
init|(
name|peer
operator|.
name|started
init|)
block|{
if|if
condition|(
operator|!
name|peer
operator|.
name|started
operator|.
name|get
argument_list|()
condition|)
block|{
name|accepted
operator|=
name|pending
operator|.
name|offer
argument_list|(
name|command
argument_list|)
expr_stmt|;
if|if
condition|(
operator|!
name|accepted
condition|)
block|{
name|sleepTimeMillis
operator|=
literal|500
expr_stmt|;
block|}
block|}
block|}
comment|// give start thread a chance if we will loop
name|TimeUnit
operator|.
name|MILLISECONDS
operator|.
name|sleep
argument_list|(
name|sleepTimeMillis
argument_list|)
expr_stmt|;
block|}
do|while
condition|(
operator|!
name|accepted
operator|&&
operator|!
name|peer
operator|.
name|started
operator|.
name|get
argument_list|()
condition|)
do|;
if|if
condition|(
name|accepted
condition|)
block|{
return|return;
block|}
block|}
block|}
catch|catch
parameter_list|(
name|InterruptedException
name|e
parameter_list|)
block|{
name|InterruptedIOException
name|iioe
init|=
operator|new
name|InterruptedIOException
argument_list|(
name|e
operator|.
name|getMessage
argument_list|()
argument_list|)
decl_stmt|;
name|iioe
operator|.
name|initCause
argument_list|(
name|e
argument_list|)
expr_stmt|;
throw|throw
name|iioe
throw|;
block|}
name|dispatch
argument_list|(
name|peer
argument_list|,
name|peer
operator|.
name|messageQueue
argument_list|,
name|command
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|dispatch
parameter_list|(
name|VMTransport
name|transport
parameter_list|,
name|BlockingQueue
argument_list|<
name|Object
argument_list|>
name|pending
parameter_list|,
name|Object
name|command
parameter_list|)
block|{
name|TransportListener
name|transportListener
init|=
name|transport
operator|.
name|getTransportListener
argument_list|()
decl_stmt|;
if|if
condition|(
name|transportListener
operator|!=
literal|null
condition|)
block|{
comment|// Lock here on the target transport's started since we want to wait for its start()
comment|// method to finish dispatching out of the queue before we do our own.
synchronized|synchronized
init|(
name|transport
operator|.
name|started
init|)
block|{
comment|// Ensure that no additional commands entered the queue in the small time window
comment|// before the start method locks the dispatch lock and the oneway method was in
comment|// an put operation.
while|while
condition|(
name|pending
operator|!=
literal|null
operator|&&
operator|!
name|pending
operator|.
name|isEmpty
argument_list|()
operator|&&
operator|!
name|transport
operator|.
name|isDisposed
argument_list|()
condition|)
block|{
name|doDispatch
argument_list|(
name|transport
argument_list|,
name|transportListener
argument_list|,
name|pending
operator|.
name|poll
argument_list|()
argument_list|)
expr_stmt|;
block|}
comment|// We are now in sync mode and won't enqueue any more commands to the target
comment|// transport so lets clean up its resources.
name|transport
operator|.
name|messageQueue
operator|=
literal|null
expr_stmt|;
comment|// Don't dispatch if either end was disposed already.
if|if
condition|(
name|command
operator|!=
literal|null
operator|&&
operator|!
name|this
operator|.
name|disposed
operator|.
name|get
argument_list|()
operator|&&
operator|!
name|transport
operator|.
name|isDisposed
argument_list|()
condition|)
block|{
name|doDispatch
argument_list|(
name|transport
argument_list|,
name|transportListener
argument_list|,
name|command
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|}
specifier|public
name|void
name|doDispatch
parameter_list|(
name|VMTransport
name|transport
parameter_list|,
name|TransportListener
name|transportListener
parameter_list|,
name|Object
name|command
parameter_list|)
block|{
name|transport
operator|.
name|receiveCounter
operator|++
expr_stmt|;
name|transportListener
operator|.
name|onCommand
argument_list|(
name|command
argument_list|)
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
comment|// If we are not in async mode we lock the dispatch lock here and then start to
comment|// prevent any sync dispatches from occurring until we dispatch the pending messages
comment|// to maintain delivery order.  When async this happens automatically so just set
comment|// started and wakeup the task runner.
if|if
condition|(
operator|!
name|async
condition|)
block|{
synchronized|synchronized
init|(
name|started
init|)
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
decl_stmt|;
while|while
condition|(
operator|(
name|command
operator|=
name|mq
operator|.
name|poll
argument_list|()
operator|)
operator|!=
literal|null
operator|&&
operator|!
name|disposed
operator|.
name|get
argument_list|()
condition|)
block|{
name|receiveCounter
operator|++
expr_stmt|;
name|doDispatch
argument_list|(
name|this
argument_list|,
name|transportListener
argument_list|,
name|command
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|}
else|else
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
name|wakeup
argument_list|()
expr_stmt|;
block|}
block|}
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
comment|// Only need to do this once, all future oneway calls will now
comment|// fail as will any asnyc jobs in the task runner.
if|if
condition|(
name|disposed
operator|.
name|compareAndSet
argument_list|(
literal|false
argument_list|,
literal|true
argument_list|)
condition|)
block|{
name|TaskRunner
name|tr
init|=
name|taskRunner
decl_stmt|;
name|LinkedBlockingQueue
argument_list|<
name|Object
argument_list|>
name|mq
init|=
name|this
operator|.
name|messageQueue
decl_stmt|;
name|taskRunner
operator|=
literal|null
expr_stmt|;
name|messageQueue
operator|=
literal|null
expr_stmt|;
if|if
condition|(
name|mq
operator|!=
literal|null
condition|)
block|{
name|mq
operator|.
name|clear
argument_list|()
expr_stmt|;
block|}
comment|// don't wait for completion
if|if
condition|(
name|tr
operator|!=
literal|null
condition|)
block|{
try|try
block|{
name|tr
operator|.
name|shutdown
argument_list|(
literal|1
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{                 }
name|tr
operator|=
literal|null
expr_stmt|;
block|}
if|if
condition|(
name|peer
operator|.
name|transportListener
operator|!=
literal|null
condition|)
block|{
comment|// let the peer know that we are disconnecting after attempting
comment|// to cleanly shutdown the async tasks so that this is the last
comment|// command it see's.
try|try
block|{
name|peer
operator|.
name|transportListener
operator|.
name|onCommand
argument_list|(
operator|new
name|ShutdownInfo
argument_list|()
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|ignore
parameter_list|)
block|{                 }
comment|// let any requests pending a response see an exception
try|try
block|{
name|peer
operator|.
name|transportListener
operator|.
name|onException
argument_list|(
operator|new
name|TransportDisposedIOException
argument_list|(
literal|"peer ("
operator|+
name|this
operator|+
literal|") stopped."
argument_list|)
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|ignore
parameter_list|)
block|{                 }
block|}
comment|// shutdown task runner factory
if|if
condition|(
name|taskRunnerFactory
operator|!=
literal|null
condition|)
block|{
name|taskRunnerFactory
operator|.
name|shutdownNow
argument_list|()
expr_stmt|;
name|taskRunnerFactory
operator|=
literal|null
expr_stmt|;
block|}
block|}
block|}
specifier|protected
name|void
name|wakeup
parameter_list|()
block|{
if|if
condition|(
name|async
operator|&&
name|started
operator|.
name|get
argument_list|()
condition|)
block|{
try|try
block|{
name|getTaskRunner
argument_list|()
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
catch|catch
parameter_list|(
name|TransportDisposedIOException
name|e
parameter_list|)
block|{             }
block|}
block|}
comment|/**      * @see org.apache.activemq.thread.Task#iterate()      */
annotation|@
name|Override
specifier|public
name|boolean
name|iterate
parameter_list|()
block|{
specifier|final
name|TransportListener
name|tl
init|=
name|transportListener
decl_stmt|;
name|LinkedBlockingQueue
argument_list|<
name|Object
argument_list|>
name|mq
decl_stmt|;
try|try
block|{
name|mq
operator|=
name|getMessageQueue
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|TransportDisposedIOException
name|e
parameter_list|)
block|{
return|return
literal|false
return|;
block|}
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
operator|&&
operator|!
name|disposed
operator|.
name|get
argument_list|()
condition|)
block|{
name|tl
operator|.
name|onCommand
argument_list|(
name|command
argument_list|)
expr_stmt|;
return|return
operator|!
name|mq
operator|.
name|isEmpty
argument_list|()
operator|&&
operator|!
name|disposed
operator|.
name|get
argument_list|()
return|;
block|}
else|else
block|{
if|if
condition|(
name|disposed
operator|.
name|get
argument_list|()
condition|)
block|{
name|mq
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
annotation|@
name|Override
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
name|LinkedBlockingQueue
argument_list|<
name|Object
argument_list|>
name|getMessageQueue
parameter_list|()
throws|throws
name|TransportDisposedIOException
block|{
name|LinkedBlockingQueue
argument_list|<
name|Object
argument_list|>
name|result
init|=
name|messageQueue
decl_stmt|;
if|if
condition|(
name|result
operator|==
literal|null
condition|)
block|{
synchronized|synchronized
init|(
name|this
init|)
block|{
name|result
operator|=
name|messageQueue
expr_stmt|;
if|if
condition|(
name|result
operator|==
literal|null
condition|)
block|{
if|if
condition|(
name|disposed
operator|.
name|get
argument_list|()
condition|)
block|{
throw|throw
operator|new
name|TransportDisposedIOException
argument_list|(
literal|"The Transport has been disposed"
argument_list|)
throw|;
block|}
name|messageQueue
operator|=
name|result
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
block|}
block|}
return|return
name|result
return|;
block|}
specifier|protected
name|TaskRunner
name|getTaskRunner
parameter_list|()
throws|throws
name|TransportDisposedIOException
block|{
name|TaskRunner
name|result
init|=
name|taskRunner
decl_stmt|;
if|if
condition|(
name|result
operator|==
literal|null
condition|)
block|{
synchronized|synchronized
init|(
name|this
init|)
block|{
name|result
operator|=
name|taskRunner
expr_stmt|;
if|if
condition|(
name|result
operator|==
literal|null
condition|)
block|{
if|if
condition|(
name|disposed
operator|.
name|get
argument_list|()
condition|)
block|{
throw|throw
operator|new
name|TransportDisposedIOException
argument_list|(
literal|"The Transport has been disposed"
argument_list|)
throw|;
block|}
name|String
name|name
init|=
literal|"ActiveMQ VMTransport: "
operator|+
name|toString
argument_list|()
decl_stmt|;
if|if
condition|(
name|taskRunnerFactory
operator|==
literal|null
condition|)
block|{
name|taskRunnerFactory
operator|=
operator|new
name|TaskRunnerFactory
argument_list|(
name|name
argument_list|)
expr_stmt|;
name|taskRunnerFactory
operator|.
name|init
argument_list|()
expr_stmt|;
block|}
name|taskRunner
operator|=
name|result
operator|=
name|taskRunnerFactory
operator|.
name|createTaskRunner
argument_list|(
name|this
argument_list|,
name|name
argument_list|)
expr_stmt|;
block|}
block|}
block|}
return|return
name|result
return|;
block|}
annotation|@
name|Override
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
annotation|@
name|Override
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
annotation|@
name|Override
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
annotation|@
name|Override
specifier|public
name|TransportListener
name|getTransportListener
parameter_list|()
block|{
return|return
name|transportListener
return|;
block|}
annotation|@
name|Override
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
annotation|@
name|Override
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
annotation|@
name|Override
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
annotation|@
name|Override
specifier|public
name|boolean
name|isFaultTolerant
parameter_list|()
block|{
return|return
literal|false
return|;
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|isDisposed
parameter_list|()
block|{
return|return
name|disposed
operator|.
name|get
argument_list|()
return|;
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|isConnected
parameter_list|()
block|{
return|return
operator|!
name|disposed
operator|.
name|get
argument_list|()
return|;
block|}
annotation|@
name|Override
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
literal|"Transport reconnect is not supported"
argument_list|)
throw|;
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|isReconnectSupported
parameter_list|()
block|{
return|return
literal|false
return|;
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|isUpdateURIsSupported
parameter_list|()
block|{
return|return
literal|false
return|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|updateURIs
parameter_list|(
name|boolean
name|reblance
parameter_list|,
name|URI
index|[]
name|uris
parameter_list|)
throws|throws
name|IOException
block|{
throw|throw
operator|new
name|IOException
argument_list|(
literal|"URI update feature not supported"
argument_list|)
throw|;
block|}
annotation|@
name|Override
specifier|public
name|int
name|getReceiveCounter
parameter_list|()
block|{
return|return
name|receiveCounter
return|;
block|}
block|}
end_class

end_unit

