begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  *   * Licensed to the Apache Software Foundation (ASF) under one or more contributor license agreements. See the NOTICE  * file distributed with this work for additional information regarding copyright ownership. The ASF licenses this file  * to You under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the  * License. You may obtain a copy of the License at  *   * http://www.apache.org/licenses/LICENSE-2.0  *   * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on  * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the  * specific language governing permissions and limitations under the License.  */
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
name|Collections
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
name|LinkedBlockingQueue
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
name|AtomicLong
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
name|Log
name|log
init|=
name|LogFactory
operator|.
name|getLog
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
name|nextId
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
name|taskRunnerFactory
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
literal|false
decl_stmt|;
specifier|protected
name|boolean
name|started
init|=
literal|false
decl_stmt|;
specifier|protected
name|int
name|asyncQueueDepth
init|=
literal|2000
decl_stmt|;
specifier|protected
name|List
name|prePeerSetQueue
init|=
name|Collections
operator|.
name|synchronizedList
argument_list|(
operator|new
name|LinkedList
argument_list|()
argument_list|)
decl_stmt|;
specifier|protected
name|LinkedBlockingQueue
name|messageQueue
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
name|nextId
operator|.
name|getAndIncrement
argument_list|()
expr_stmt|;
block|}
specifier|synchronized
specifier|public
name|VMTransport
name|getPeer
parameter_list|()
block|{
return|return
name|peer
return|;
block|}
specifier|synchronized
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
name|Command
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
throw|throw
operator|new
name|IOException
argument_list|(
literal|"Peer not connected."
argument_list|)
throw|;
if|if
condition|(
operator|!
name|peer
operator|.
name|disposed
condition|)
block|{
specifier|final
name|TransportListener
name|tl
init|=
name|peer
operator|.
name|transportListener
decl_stmt|;
name|messageQueue
operator|=
name|getMessageQueue
argument_list|()
expr_stmt|;
name|prePeerSetQueue
operator|=
name|peer
operator|.
name|prePeerSetQueue
expr_stmt|;
if|if
condition|(
name|tl
operator|==
literal|null
condition|)
block|{
name|prePeerSetQueue
operator|.
name|add
argument_list|(
name|command
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
operator|!
name|async
condition|)
block|{
name|tl
operator|.
name|onCommand
argument_list|(
name|command
argument_list|)
expr_stmt|;
block|}
else|else
block|{
try|try
block|{
name|messageQueue
operator|.
name|put
argument_list|(
name|command
argument_list|)
expr_stmt|;
name|wakeup
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
specifier|final
name|InterruptedException
name|e
parameter_list|)
block|{
name|log
operator|.
name|error
argument_list|(
literal|"messageQueue interuppted"
argument_list|,
name|e
argument_list|)
expr_stmt|;
throw|throw
operator|new
name|IOException
argument_list|(
name|e
operator|.
name|getMessage
argument_list|()
argument_list|)
throw|;
block|}
block|}
block|}
else|else
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
specifier|synchronized
name|TransportListener
name|getTransportListener
parameter_list|()
block|{
return|return
name|transportListener
return|;
block|}
specifier|synchronized
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
name|wakeup
argument_list|()
expr_stmt|;
block|}
specifier|public
specifier|synchronized
name|void
name|start
parameter_list|()
throws|throws
name|Exception
block|{
name|started
operator|=
literal|true
expr_stmt|;
if|if
condition|(
name|transportListener
operator|==
literal|null
condition|)
throw|throw
operator|new
name|IOException
argument_list|(
literal|"TransportListener not set."
argument_list|)
throw|;
for|for
control|(
name|Iterator
name|iter
init|=
name|prePeerSetQueue
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
name|Command
name|command
init|=
operator|(
name|Command
operator|)
name|iter
operator|.
name|next
argument_list|()
decl_stmt|;
name|transportListener
operator|.
name|onCommand
argument_list|(
name|command
argument_list|)
expr_stmt|;
name|iter
operator|.
name|remove
argument_list|()
expr_stmt|;
block|}
name|wakeup
argument_list|()
expr_stmt|;
block|}
specifier|public
name|void
name|stop
parameter_list|()
throws|throws
name|Exception
block|{
name|started
operator|=
literal|false
expr_stmt|;
if|if
condition|(
operator|!
name|disposed
condition|)
block|{
name|disposed
operator|=
literal|true
expr_stmt|;
block|}
if|if
condition|(
name|taskRunner
operator|!=
literal|null
condition|)
block|{
name|taskRunner
operator|.
name|shutdown
argument_list|()
expr_stmt|;
name|taskRunner
operator|=
literal|null
expr_stmt|;
block|}
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
comment|// task implementation
specifier|public
name|boolean
name|iterate
parameter_list|()
block|{
name|TransportListener
name|tl
init|=
name|peer
operator|.
name|transportListener
decl_stmt|;
if|if
condition|(
operator|!
name|messageQueue
operator|.
name|isEmpty
argument_list|()
operator|&&
operator|!
name|peer
operator|.
name|disposed
operator|&&
name|tl
operator|!=
literal|null
condition|)
block|{
name|Command
name|command
init|=
operator|(
name|Command
operator|)
name|messageQueue
operator|.
name|poll
argument_list|()
decl_stmt|;
if|if
condition|(
name|tl
operator|!=
literal|null
condition|)
block|{
name|tl
operator|.
name|onCommand
argument_list|(
name|command
argument_list|)
expr_stmt|;
block|}
block|}
return|return
operator|!
name|messageQueue
operator|.
name|isEmpty
argument_list|()
operator|&&
operator|!
name|peer
operator|.
name|disposed
operator|&&
operator|!
operator|(
name|peer
operator|.
name|transportListener
operator|==
literal|null
operator|)
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
operator|&&
name|messageQueue
operator|!=
literal|null
operator|&&
operator|!
name|messageQueue
operator|.
name|isEmpty
argument_list|()
condition|)
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
name|taskRunnerFactory
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
specifier|protected
specifier|synchronized
name|LinkedBlockingQueue
name|getMessageQueue
parameter_list|()
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
end_class

end_unit

