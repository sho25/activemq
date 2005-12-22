begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  *  * Copyright 2004 The Apache Software Foundation  *  * Licensed under the Apache License, Version 2.0 (the "License");  * you may not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  * http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
package|package
name|org
operator|.
name|activeio
operator|.
name|adapter
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
name|org
operator|.
name|activeio
operator|.
name|AcceptListener
import|;
end_import

begin_import
import|import
name|org
operator|.
name|activeio
operator|.
name|Channel
import|;
end_import

begin_import
import|import
name|org
operator|.
name|activeio
operator|.
name|ChannelServer
import|;
end_import

begin_import
import|import
name|org
operator|.
name|activeio
operator|.
name|packet
operator|.
name|async
operator|.
name|AsyncChannelServer
import|;
end_import

begin_import
import|import
name|org
operator|.
name|activeio
operator|.
name|packet
operator|.
name|sync
operator|.
name|SyncChannelServer
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
name|BlockingQueue
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
name|TimeUnit
import|;
end_import

begin_comment
comment|/**  * Adapts a {@see org.activeio.AsyncChannelServer} so that it provides an   * {@see org.activeio.SynchChannelServer} interface.    *   * This object buffers asynchronous accepts from the {@see org.activeio.AsyncChannelServer}   * abs buffers them in a {@see edu.emory.mathcs.backport.java.util.concurrent.Channel} util the client accepts the   * connection.  *   * @version $Revision$  */
end_comment

begin_class
specifier|final
specifier|public
class|class
name|AsyncToSyncChannelServer
implements|implements
name|SyncChannelServer
implements|,
name|AcceptListener
block|{
specifier|private
specifier|final
name|AsyncChannelServer
name|asyncChannelServer
decl_stmt|;
specifier|private
specifier|final
name|BlockingQueue
name|acceptBuffer
decl_stmt|;
specifier|static
specifier|public
name|SyncChannelServer
name|adapt
parameter_list|(
name|ChannelServer
name|channel
parameter_list|)
block|{
return|return
name|adapt
argument_list|(
name|channel
argument_list|,
operator|new
name|LinkedBlockingQueue
argument_list|()
argument_list|)
return|;
block|}
specifier|static
specifier|public
name|SyncChannelServer
name|adapt
parameter_list|(
name|ChannelServer
name|channel
parameter_list|,
name|BlockingQueue
name|upPacketChannel
parameter_list|)
block|{
comment|// It might not need adapting
if|if
condition|(
name|channel
operator|instanceof
name|SyncChannelServer
condition|)
block|{
return|return
operator|(
name|SyncChannelServer
operator|)
name|channel
return|;
block|}
comment|// Can we just just undo the adaptor
if|if
condition|(
name|channel
operator|.
name|getClass
argument_list|()
operator|==
name|SyncToAsyncChannel
operator|.
name|class
condition|)
block|{
return|return
operator|(
operator|(
name|SyncToAsyncChannelServer
operator|)
name|channel
operator|)
operator|.
name|getSynchChannelServer
argument_list|()
return|;
block|}
return|return
operator|new
name|AsyncToSyncChannelServer
argument_list|(
operator|(
name|AsyncChannelServer
operator|)
name|channel
argument_list|,
name|upPacketChannel
argument_list|)
return|;
block|}
comment|/**      * @deprecated {@see #adapt(ChannelServer)}      */
specifier|public
name|AsyncToSyncChannelServer
parameter_list|(
name|AsyncChannelServer
name|asyncChannelServer
parameter_list|)
block|{
name|this
argument_list|(
name|asyncChannelServer
argument_list|,
operator|new
name|LinkedBlockingQueue
argument_list|()
argument_list|)
expr_stmt|;
block|}
comment|/**      * @deprecated {@see #adapt(ChannelServer, edu.emory.mathcs.backport.java.util.concurrent.Channel)}      */
specifier|public
name|AsyncToSyncChannelServer
parameter_list|(
name|AsyncChannelServer
name|asyncChannelServer
parameter_list|,
name|BlockingQueue
name|acceptBuffer
parameter_list|)
block|{
name|this
operator|.
name|asyncChannelServer
operator|=
name|asyncChannelServer
expr_stmt|;
name|this
operator|.
name|acceptBuffer
operator|=
name|acceptBuffer
expr_stmt|;
name|this
operator|.
name|asyncChannelServer
operator|.
name|setAcceptListener
argument_list|(
name|this
argument_list|)
expr_stmt|;
block|}
comment|/**      * @see org.activeio.packet.sync.SyncChannelServer#accept(long)      */
specifier|public
name|org
operator|.
name|activeio
operator|.
name|Channel
name|accept
parameter_list|(
name|long
name|timeout
parameter_list|)
throws|throws
name|IOException
block|{
try|try
block|{
name|Object
name|o
decl_stmt|;
if|if
condition|(
name|timeout
operator|==
name|NO_WAIT_TIMEOUT
condition|)
block|{
name|o
operator|=
name|acceptBuffer
operator|.
name|poll
argument_list|(
literal|0
argument_list|,
name|TimeUnit
operator|.
name|MILLISECONDS
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|timeout
operator|==
name|WAIT_FOREVER_TIMEOUT
condition|)
block|{
name|o
operator|=
name|acceptBuffer
operator|.
name|take
argument_list|()
expr_stmt|;
block|}
else|else
block|{
name|o
operator|=
name|acceptBuffer
operator|.
name|poll
argument_list|(
name|timeout
argument_list|,
name|TimeUnit
operator|.
name|MILLISECONDS
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|o
operator|==
literal|null
condition|)
return|return
literal|null
return|;
if|if
condition|(
name|o
operator|instanceof
name|Channel
condition|)
return|return
operator|(
name|Channel
operator|)
name|o
return|;
name|Throwable
name|e
init|=
operator|(
name|Throwable
operator|)
name|o
decl_stmt|;
throw|throw
operator|(
name|IOException
operator|)
operator|new
name|IOException
argument_list|(
literal|"Async error occurred: "
operator|+
name|e
argument_list|)
operator|.
name|initCause
argument_list|(
name|e
argument_list|)
throw|;
block|}
catch|catch
parameter_list|(
name|InterruptedException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|InterruptedIOException
argument_list|(
name|e
operator|.
name|getMessage
argument_list|()
argument_list|)
throw|;
block|}
block|}
comment|/**      * @see org.activeio.Disposable#dispose()      */
specifier|public
name|void
name|dispose
parameter_list|()
block|{
name|asyncChannelServer
operator|.
name|dispose
argument_list|()
expr_stmt|;
block|}
comment|/**      * @see org.activeio.Service#start()      */
specifier|public
name|void
name|start
parameter_list|()
throws|throws
name|IOException
block|{
name|asyncChannelServer
operator|.
name|start
argument_list|()
expr_stmt|;
block|}
comment|/**      * @see org.activeio.Service#stop()      */
specifier|public
name|void
name|stop
parameter_list|()
throws|throws
name|IOException
block|{
name|asyncChannelServer
operator|.
name|stop
argument_list|()
expr_stmt|;
block|}
specifier|public
name|URI
name|getBindURI
parameter_list|()
block|{
return|return
name|asyncChannelServer
operator|.
name|getBindURI
argument_list|()
return|;
block|}
specifier|public
name|URI
name|getConnectURI
parameter_list|()
block|{
return|return
name|asyncChannelServer
operator|.
name|getConnectURI
argument_list|()
return|;
block|}
comment|/**      * @see org.activeio.AcceptListener#onAccept(org.activeio.Channel)      */
specifier|public
name|void
name|onAccept
parameter_list|(
name|org
operator|.
name|activeio
operator|.
name|Channel
name|channel
parameter_list|)
block|{
try|try
block|{
name|acceptBuffer
operator|.
name|put
argument_list|(
name|channel
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
comment|/**      * @see org.activeio.AcceptListener#onAcceptError(java.io.IOException)      */
specifier|public
name|void
name|onAcceptError
parameter_list|(
name|IOException
name|error
parameter_list|)
block|{
try|try
block|{
name|acceptBuffer
operator|.
name|put
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
block|}
block|}
specifier|public
name|AsyncChannelServer
name|getAsyncChannelServer
parameter_list|()
block|{
return|return
name|asyncChannelServer
return|;
block|}
specifier|public
name|Object
name|getAdapter
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
name|asyncChannelServer
operator|.
name|getAdapter
argument_list|(
name|target
argument_list|)
return|;
block|}
specifier|public
name|String
name|toString
parameter_list|()
block|{
return|return
name|asyncChannelServer
operator|.
name|toString
argument_list|()
return|;
block|}
block|}
end_class

end_unit

