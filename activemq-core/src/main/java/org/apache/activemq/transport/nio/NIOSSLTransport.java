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
name|nio
package|;
end_package

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
name|openwire
operator|.
name|OpenWireFormat
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
name|wireformat
operator|.
name|WireFormat
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|net
operator|.
name|SocketFactory
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|net
operator|.
name|ssl
operator|.
name|*
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|DataInputStream
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|DataOutputStream
import|;
end_import

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
name|net
operator|.
name|URI
import|;
end_import

begin_import
import|import
name|java
operator|.
name|net
operator|.
name|UnknownHostException
import|;
end_import

begin_import
import|import
name|java
operator|.
name|nio
operator|.
name|ByteBuffer
import|;
end_import

begin_class
specifier|public
class|class
name|NIOSSLTransport
extends|extends
name|NIOTransport
block|{
specifier|protected
name|SSLContext
name|sslContext
decl_stmt|;
specifier|protected
name|SSLEngine
name|sslEngine
decl_stmt|;
specifier|protected
name|SSLSession
name|sslSession
decl_stmt|;
name|boolean
name|handshakeInProgress
init|=
literal|false
decl_stmt|;
name|SSLEngineResult
operator|.
name|Status
name|status
init|=
literal|null
decl_stmt|;
name|SSLEngineResult
operator|.
name|HandshakeStatus
name|handshakeStatus
init|=
literal|null
decl_stmt|;
specifier|public
name|NIOSSLTransport
parameter_list|(
name|WireFormat
name|wireFormat
parameter_list|,
name|SocketFactory
name|socketFactory
parameter_list|,
name|URI
name|remoteLocation
parameter_list|,
name|URI
name|localLocation
parameter_list|)
throws|throws
name|UnknownHostException
throws|,
name|IOException
block|{
name|super
argument_list|(
name|wireFormat
argument_list|,
name|socketFactory
argument_list|,
name|remoteLocation
argument_list|,
name|localLocation
argument_list|)
expr_stmt|;
block|}
specifier|public
name|NIOSSLTransport
parameter_list|(
name|WireFormat
name|wireFormat
parameter_list|,
name|Socket
name|socket
parameter_list|)
throws|throws
name|IOException
block|{
name|super
argument_list|(
name|wireFormat
argument_list|,
name|socket
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|setSslContext
parameter_list|(
name|SSLContext
name|sslContext
parameter_list|)
block|{
name|this
operator|.
name|sslContext
operator|=
name|sslContext
expr_stmt|;
block|}
annotation|@
name|Override
specifier|protected
name|void
name|initializeStreams
parameter_list|()
throws|throws
name|IOException
block|{
try|try
block|{
name|channel
operator|=
name|socket
operator|.
name|getChannel
argument_list|()
expr_stmt|;
name|channel
operator|.
name|configureBlocking
argument_list|(
literal|false
argument_list|)
expr_stmt|;
if|if
condition|(
name|sslContext
operator|==
literal|null
condition|)
block|{
name|sslContext
operator|=
name|SSLContext
operator|.
name|getDefault
argument_list|()
expr_stmt|;
block|}
name|sslEngine
operator|=
name|sslContext
operator|.
name|createSSLEngine
argument_list|()
expr_stmt|;
name|sslEngine
operator|.
name|setUseClientMode
argument_list|(
literal|false
argument_list|)
expr_stmt|;
name|sslSession
operator|=
name|sslEngine
operator|.
name|getSession
argument_list|()
expr_stmt|;
name|inputBuffer
operator|=
name|ByteBuffer
operator|.
name|allocate
argument_list|(
name|sslSession
operator|.
name|getPacketBufferSize
argument_list|()
argument_list|)
expr_stmt|;
name|inputBuffer
operator|.
name|clear
argument_list|()
expr_stmt|;
name|currentBuffer
operator|=
name|ByteBuffer
operator|.
name|allocate
argument_list|(
name|sslSession
operator|.
name|getApplicationBufferSize
argument_list|()
argument_list|)
expr_stmt|;
name|NIOOutputStream
name|outputStream
init|=
operator|new
name|NIOOutputStream
argument_list|(
name|channel
argument_list|)
decl_stmt|;
name|outputStream
operator|.
name|setEngine
argument_list|(
name|sslEngine
argument_list|)
expr_stmt|;
name|this
operator|.
name|dataOut
operator|=
operator|new
name|DataOutputStream
argument_list|(
name|outputStream
argument_list|)
expr_stmt|;
name|this
operator|.
name|buffOut
operator|=
name|outputStream
expr_stmt|;
name|sslEngine
operator|.
name|beginHandshake
argument_list|()
expr_stmt|;
name|handshakeStatus
operator|=
name|sslEngine
operator|.
name|getHandshakeStatus
argument_list|()
expr_stmt|;
name|doHandshake
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|IOException
argument_list|(
name|e
argument_list|)
throw|;
block|}
block|}
specifier|protected
name|void
name|finishHandshake
parameter_list|()
throws|throws
name|Exception
block|{
if|if
condition|(
name|handshakeInProgress
condition|)
block|{
name|handshakeInProgress
operator|=
literal|false
expr_stmt|;
name|nextFrameSize
operator|=
operator|-
literal|1
expr_stmt|;
comment|// listen for events telling us when the socket is readable.
name|selection
operator|=
name|SelectorManager
operator|.
name|getInstance
argument_list|()
operator|.
name|register
argument_list|(
name|channel
argument_list|,
operator|new
name|SelectorManager
operator|.
name|Listener
argument_list|()
block|{
specifier|public
name|void
name|onSelect
parameter_list|(
name|SelectorSelection
name|selection
parameter_list|)
block|{
name|serviceRead
argument_list|()
expr_stmt|;
block|}
specifier|public
name|void
name|onError
parameter_list|(
name|SelectorSelection
name|selection
parameter_list|,
name|Throwable
name|error
parameter_list|)
block|{
if|if
condition|(
name|error
operator|instanceof
name|IOException
condition|)
block|{
name|onException
argument_list|(
operator|(
name|IOException
operator|)
name|error
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|onException
argument_list|(
name|IOExceptionSupport
operator|.
name|create
argument_list|(
name|error
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
block|}
argument_list|)
expr_stmt|;
block|}
block|}
specifier|protected
name|void
name|serviceRead
parameter_list|()
block|{
try|try
block|{
if|if
condition|(
name|handshakeInProgress
condition|)
block|{
name|doHandshake
argument_list|()
expr_stmt|;
block|}
name|ByteBuffer
name|plain
init|=
name|ByteBuffer
operator|.
name|allocate
argument_list|(
name|sslSession
operator|.
name|getApplicationBufferSize
argument_list|()
argument_list|)
decl_stmt|;
name|plain
operator|.
name|position
argument_list|(
name|plain
operator|.
name|limit
argument_list|()
argument_list|)
expr_stmt|;
while|while
condition|(
literal|true
condition|)
block|{
if|if
condition|(
name|nextFrameSize
operator|==
operator|-
literal|1
condition|)
block|{
if|if
condition|(
operator|!
name|plain
operator|.
name|hasRemaining
argument_list|()
condition|)
block|{
name|plain
operator|.
name|clear
argument_list|()
expr_stmt|;
name|int
name|readCount
init|=
name|secureRead
argument_list|(
name|plain
argument_list|)
decl_stmt|;
if|if
condition|(
name|readCount
operator|==
literal|0
condition|)
break|break;
block|}
name|nextFrameSize
operator|=
name|plain
operator|.
name|getInt
argument_list|()
expr_stmt|;
if|if
condition|(
name|wireFormat
operator|instanceof
name|OpenWireFormat
condition|)
block|{
name|long
name|maxFrameSize
init|=
operator|(
operator|(
name|OpenWireFormat
operator|)
name|wireFormat
operator|)
operator|.
name|getMaxFrameSize
argument_list|()
decl_stmt|;
if|if
condition|(
name|nextFrameSize
operator|>
name|maxFrameSize
condition|)
block|{
throw|throw
operator|new
name|IOException
argument_list|(
literal|"Frame size of "
operator|+
operator|(
name|nextFrameSize
operator|/
operator|(
literal|1024
operator|*
literal|1024
operator|)
operator|)
operator|+
literal|" MB larger than max allowed "
operator|+
operator|(
name|maxFrameSize
operator|/
operator|(
literal|1024
operator|*
literal|1024
operator|)
operator|)
operator|+
literal|" MB"
argument_list|)
throw|;
block|}
block|}
name|currentBuffer
operator|=
name|ByteBuffer
operator|.
name|allocate
argument_list|(
name|nextFrameSize
operator|+
literal|4
argument_list|)
expr_stmt|;
name|currentBuffer
operator|.
name|putInt
argument_list|(
name|nextFrameSize
argument_list|)
expr_stmt|;
if|if
condition|(
name|currentBuffer
operator|.
name|hasRemaining
argument_list|()
condition|)
block|{
if|if
condition|(
name|currentBuffer
operator|.
name|remaining
argument_list|()
operator|>=
name|plain
operator|.
name|remaining
argument_list|()
condition|)
block|{
name|currentBuffer
operator|.
name|put
argument_list|(
name|plain
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|byte
index|[]
name|fill
init|=
operator|new
name|byte
index|[
name|currentBuffer
operator|.
name|remaining
argument_list|()
index|]
decl_stmt|;
name|plain
operator|.
name|get
argument_list|(
name|fill
argument_list|)
expr_stmt|;
name|currentBuffer
operator|.
name|put
argument_list|(
name|fill
argument_list|)
expr_stmt|;
block|}
block|}
if|if
condition|(
name|currentBuffer
operator|.
name|hasRemaining
argument_list|()
condition|)
block|{
continue|continue;
block|}
else|else
block|{
name|currentBuffer
operator|.
name|flip
argument_list|()
expr_stmt|;
name|Object
name|command
init|=
name|wireFormat
operator|.
name|unmarshal
argument_list|(
operator|new
name|DataInputStream
argument_list|(
operator|new
name|NIOInputStream
argument_list|(
name|currentBuffer
argument_list|)
argument_list|)
argument_list|)
decl_stmt|;
name|doConsume
argument_list|(
operator|(
name|Command
operator|)
name|command
argument_list|)
expr_stmt|;
name|nextFrameSize
operator|=
operator|-
literal|1
expr_stmt|;
block|}
block|}
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
catch|catch
parameter_list|(
name|Throwable
name|e
parameter_list|)
block|{
name|onException
argument_list|(
name|IOExceptionSupport
operator|.
name|create
argument_list|(
name|e
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
specifier|private
name|int
name|secureRead
parameter_list|(
name|ByteBuffer
name|plain
parameter_list|)
throws|throws
name|Exception
block|{
name|int
name|bytesRead
init|=
name|channel
operator|.
name|read
argument_list|(
name|inputBuffer
argument_list|)
decl_stmt|;
if|if
condition|(
name|bytesRead
operator|==
operator|-
literal|1
condition|)
block|{
name|sslEngine
operator|.
name|closeInbound
argument_list|()
expr_stmt|;
if|if
condition|(
name|inputBuffer
operator|.
name|position
argument_list|()
operator|==
literal|0
operator|||
name|status
operator|==
name|SSLEngineResult
operator|.
name|Status
operator|.
name|BUFFER_UNDERFLOW
condition|)
block|{
return|return
operator|-
literal|1
return|;
block|}
block|}
name|plain
operator|.
name|clear
argument_list|()
expr_stmt|;
name|inputBuffer
operator|.
name|flip
argument_list|()
expr_stmt|;
name|SSLEngineResult
name|res
decl_stmt|;
do|do
block|{
name|res
operator|=
name|sslEngine
operator|.
name|unwrap
argument_list|(
name|inputBuffer
argument_list|,
name|plain
argument_list|)
expr_stmt|;
block|}
do|while
condition|(
name|res
operator|.
name|getStatus
argument_list|()
operator|==
name|SSLEngineResult
operator|.
name|Status
operator|.
name|OK
operator|&&
name|res
operator|.
name|getHandshakeStatus
argument_list|()
operator|==
name|SSLEngineResult
operator|.
name|HandshakeStatus
operator|.
name|NEED_UNWRAP
operator|&&
name|res
operator|.
name|bytesProduced
argument_list|()
operator|==
literal|0
condition|)
do|;
if|if
condition|(
name|res
operator|.
name|getHandshakeStatus
argument_list|()
operator|==
name|SSLEngineResult
operator|.
name|HandshakeStatus
operator|.
name|FINISHED
condition|)
block|{
name|finishHandshake
argument_list|()
expr_stmt|;
block|}
name|status
operator|=
name|res
operator|.
name|getStatus
argument_list|()
expr_stmt|;
name|handshakeStatus
operator|=
name|res
operator|.
name|getHandshakeStatus
argument_list|()
expr_stmt|;
comment|//TODO deal with BUFFER_OVERFLOW
if|if
condition|(
name|status
operator|==
name|SSLEngineResult
operator|.
name|Status
operator|.
name|CLOSED
condition|)
block|{
comment|//TODO do shutdown
name|sslEngine
operator|.
name|closeInbound
argument_list|()
expr_stmt|;
return|return
operator|-
literal|1
return|;
block|}
name|inputBuffer
operator|.
name|compact
argument_list|()
expr_stmt|;
name|plain
operator|.
name|flip
argument_list|()
expr_stmt|;
return|return
name|plain
operator|.
name|remaining
argument_list|()
return|;
block|}
specifier|protected
name|void
name|doHandshake
parameter_list|()
throws|throws
name|Exception
block|{
name|handshakeInProgress
operator|=
literal|true
expr_stmt|;
while|while
condition|(
literal|true
condition|)
block|{
switch|switch
condition|(
name|sslEngine
operator|.
name|getHandshakeStatus
argument_list|()
condition|)
block|{
case|case
name|NEED_UNWRAP
case|:
name|secureRead
argument_list|(
name|ByteBuffer
operator|.
name|allocate
argument_list|(
name|sslSession
operator|.
name|getApplicationBufferSize
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
break|break;
case|case
name|NEED_TASK
case|:
comment|//TODO use the pool
name|Runnable
name|task
decl_stmt|;
while|while
condition|(
operator|(
name|task
operator|=
name|sslEngine
operator|.
name|getDelegatedTask
argument_list|()
operator|)
operator|!=
literal|null
condition|)
block|{
name|task
operator|.
name|run
argument_list|()
expr_stmt|;
block|}
break|break;
case|case
name|NEED_WRAP
case|:
operator|(
operator|(
name|NIOOutputStream
operator|)
name|buffOut
operator|)
operator|.
name|write
argument_list|(
name|ByteBuffer
operator|.
name|allocate
argument_list|(
literal|0
argument_list|)
argument_list|)
expr_stmt|;
break|break;
case|case
name|FINISHED
case|:
case|case
name|NOT_HANDSHAKING
case|:
name|finishHandshake
argument_list|()
expr_stmt|;
return|return;
block|}
block|}
block|}
annotation|@
name|Override
specifier|protected
name|void
name|doStop
parameter_list|(
name|ServiceStopper
name|stopper
parameter_list|)
throws|throws
name|Exception
block|{
if|if
condition|(
name|channel
operator|!=
literal|null
condition|)
block|{
name|channel
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
name|super
operator|.
name|doStop
argument_list|(
name|stopper
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

