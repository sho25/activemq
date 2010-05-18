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
name|net
operator|.
name|Socket
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
name|concurrent
operator|.
name|ConcurrentLinkedQueue
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
name|Condition
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
name|ReentrantLock
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
name|tcp
operator|.
name|TcpBufferedOutputStream
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
name|tcp
operator|.
name|TimeStampStream
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
comment|/**  * This filter implements write timeouts for socket write operations.  * When using blocking IO, the Java implementation doesn't have an explicit flag  * to set a timeout, and can cause operations to block forever (or until the TCP stack implementation times out the retransmissions,  * which is usually around 13-30 minutes).<br/>  * To enable this transport, in the transport URI, simpley add<br/>  *<code>transport.soWriteTimeout=<value in millis></code>.<br/>  * For example (15 second timeout on write operations to the socket):</br>  *<pre><code>  *&lt;transportConnector   *     name=&quot;tcp1&quot;   *     uri=&quot;tcp://127.0.0.1:61616?transport.soTimeout=10000&amp;transport.soWriteTimeout=15000"  * /&gt;  *</code></pre><br/>  * For example (enable default timeout on the socket):</br>  *<pre><code>  *&lt;transportConnector   *     name=&quot;tcp1&quot;   *     uri=&quot;tcp://127.0.0.1:61616?transport.soTimeout=10000&amp;transport.soWriteTimeout=15000"  * /&gt;  *</code></pre>  * @author Filip Hanik  *  */
end_comment

begin_class
specifier|public
class|class
name|WriteTimeoutFilter
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
name|WriteTimeoutFilter
operator|.
name|class
argument_list|)
decl_stmt|;
specifier|protected
specifier|static
name|ConcurrentLinkedQueue
argument_list|<
name|WriteTimeoutFilter
argument_list|>
name|writers
init|=
operator|new
name|ConcurrentLinkedQueue
argument_list|<
name|WriteTimeoutFilter
argument_list|>
argument_list|()
decl_stmt|;
specifier|protected
specifier|static
name|AtomicInteger
name|messageCounter
init|=
operator|new
name|AtomicInteger
argument_list|(
literal|0
argument_list|)
decl_stmt|;
specifier|protected
specifier|static
name|TimeoutThread
name|timeoutThread
init|=
operator|new
name|TimeoutThread
argument_list|()
decl_stmt|;
specifier|protected
specifier|static
name|long
name|sleep
init|=
literal|5000l
decl_stmt|;
specifier|protected
name|long
name|writeTimeout
init|=
operator|-
literal|1
decl_stmt|;
specifier|public
name|WriteTimeoutFilter
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
try|try
block|{
name|registerWrite
argument_list|(
name|this
argument_list|)
expr_stmt|;
name|super
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
name|x
parameter_list|)
block|{
throw|throw
name|x
throw|;
block|}
finally|finally
block|{
name|deRegisterWrite
argument_list|(
name|this
argument_list|,
literal|false
argument_list|,
literal|null
argument_list|)
expr_stmt|;
block|}
block|}
specifier|public
name|long
name|getWriteTimeout
parameter_list|()
block|{
return|return
name|writeTimeout
return|;
block|}
specifier|public
name|void
name|setWriteTimeout
parameter_list|(
name|long
name|writeTimeout
parameter_list|)
block|{
name|this
operator|.
name|writeTimeout
operator|=
name|writeTimeout
expr_stmt|;
block|}
specifier|public
specifier|static
name|long
name|getSleep
parameter_list|()
block|{
return|return
name|sleep
return|;
block|}
specifier|public
specifier|static
name|void
name|setSleep
parameter_list|(
name|long
name|sleep
parameter_list|)
block|{
name|WriteTimeoutFilter
operator|.
name|sleep
operator|=
name|sleep
expr_stmt|;
block|}
specifier|protected
name|TimeStampStream
name|getWriter
parameter_list|()
block|{
return|return
name|next
operator|.
name|narrow
argument_list|(
name|TimeStampStream
operator|.
name|class
argument_list|)
return|;
block|}
specifier|protected
name|Socket
name|getSocket
parameter_list|()
block|{
return|return
name|next
operator|.
name|narrow
argument_list|(
name|Socket
operator|.
name|class
argument_list|)
return|;
block|}
specifier|protected
specifier|static
name|void
name|registerWrite
parameter_list|(
name|WriteTimeoutFilter
name|filter
parameter_list|)
block|{
name|writers
operator|.
name|add
argument_list|(
name|filter
argument_list|)
expr_stmt|;
block|}
specifier|protected
specifier|static
name|boolean
name|deRegisterWrite
parameter_list|(
name|WriteTimeoutFilter
name|filter
parameter_list|,
name|boolean
name|fail
parameter_list|,
name|IOException
name|iox
parameter_list|)
block|{
name|boolean
name|result
init|=
name|writers
operator|.
name|remove
argument_list|(
name|filter
argument_list|)
decl_stmt|;
if|if
condition|(
name|result
condition|)
block|{
if|if
condition|(
name|fail
condition|)
block|{
name|String
name|message
init|=
literal|"Forced write timeout for:"
operator|+
name|filter
operator|.
name|getNext
argument_list|()
operator|.
name|getRemoteAddress
argument_list|()
decl_stmt|;
name|LOG
operator|.
name|warn
argument_list|(
name|message
argument_list|)
expr_stmt|;
name|Socket
name|sock
init|=
name|filter
operator|.
name|getSocket
argument_list|()
decl_stmt|;
if|if
condition|(
name|sock
operator|==
literal|null
condition|)
block|{
name|LOG
operator|.
name|error
argument_list|(
literal|"Destination socket is null, unable to close socket.("
operator|+
name|message
operator|+
literal|")"
argument_list|)
expr_stmt|;
block|}
else|else
block|{
try|try
block|{
name|sock
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|ignore
parameter_list|)
block|{                     }
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
name|void
name|start
parameter_list|()
throws|throws
name|Exception
block|{
name|super
operator|.
name|start
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
name|super
operator|.
name|stop
argument_list|()
expr_stmt|;
block|}
specifier|protected
specifier|static
class|class
name|TimeoutThread
extends|extends
name|Thread
block|{
specifier|static
name|AtomicInteger
name|instance
init|=
operator|new
name|AtomicInteger
argument_list|(
literal|0
argument_list|)
decl_stmt|;
name|boolean
name|run
init|=
literal|true
decl_stmt|;
specifier|public
name|TimeoutThread
parameter_list|()
block|{
name|setName
argument_list|(
literal|"WriteTimeoutFilter-Timeout-"
operator|+
name|instance
operator|.
name|incrementAndGet
argument_list|()
argument_list|)
expr_stmt|;
name|setDaemon
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|setPriority
argument_list|(
name|Thread
operator|.
name|MIN_PRIORITY
argument_list|)
expr_stmt|;
name|start
argument_list|()
expr_stmt|;
block|}
specifier|public
name|void
name|run
parameter_list|()
block|{
while|while
condition|(
name|run
condition|)
block|{
name|boolean
name|error
init|=
literal|false
decl_stmt|;
try|try
block|{
if|if
condition|(
operator|!
name|interrupted
argument_list|()
condition|)
block|{
name|Iterator
argument_list|<
name|WriteTimeoutFilter
argument_list|>
name|filters
init|=
name|writers
operator|.
name|iterator
argument_list|()
decl_stmt|;
while|while
condition|(
name|run
operator|&&
name|filters
operator|.
name|hasNext
argument_list|()
condition|)
block|{
name|WriteTimeoutFilter
name|filter
init|=
name|filters
operator|.
name|next
argument_list|()
decl_stmt|;
if|if
condition|(
name|filter
operator|.
name|getWriteTimeout
argument_list|()
operator|<=
literal|0
condition|)
continue|continue;
comment|//no timeout set
name|long
name|writeStart
init|=
name|filter
operator|.
name|getWriter
argument_list|()
operator|.
name|getWriteTimestamp
argument_list|()
decl_stmt|;
name|long
name|delta
init|=
operator|(
name|filter
operator|.
name|getWriter
argument_list|()
operator|.
name|isWriting
argument_list|()
operator|&&
name|writeStart
operator|>
literal|0
operator|)
condition|?
name|System
operator|.
name|currentTimeMillis
argument_list|()
operator|-
name|writeStart
else|:
operator|-
literal|1
decl_stmt|;
if|if
condition|(
name|delta
operator|>
name|filter
operator|.
name|getWriteTimeout
argument_list|()
condition|)
block|{
name|WriteTimeoutFilter
operator|.
name|deRegisterWrite
argument_list|(
name|filter
argument_list|,
literal|true
argument_list|,
literal|null
argument_list|)
expr_stmt|;
block|}
comment|//if timeout
block|}
comment|//while
block|}
comment|//if interrupted
try|try
block|{
name|Thread
operator|.
name|sleep
argument_list|(
name|getSleep
argument_list|()
argument_list|)
expr_stmt|;
name|error
operator|=
literal|false
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|InterruptedException
name|x
parameter_list|)
block|{
comment|//do nothing
block|}
block|}
catch|catch
parameter_list|(
name|Throwable
name|t
parameter_list|)
block|{
comment|//make sure this thread never dies
if|if
condition|(
operator|!
name|error
condition|)
block|{
comment|//use error flag to avoid filling up the logs
name|LOG
operator|.
name|error
argument_list|(
literal|"WriteTimeout thread unable validate existing sockets."
argument_list|,
name|t
argument_list|)
expr_stmt|;
name|error
operator|=
literal|true
expr_stmt|;
block|}
block|}
block|}
block|}
block|}
block|}
end_class

end_unit

