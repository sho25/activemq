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
name|EOFException
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
name|SocketTimeoutException
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

begin_import
import|import
name|java
operator|.
name|nio
operator|.
name|channels
operator|.
name|SelectionKey
import|;
end_import

begin_import
import|import
name|java
operator|.
name|nio
operator|.
name|channels
operator|.
name|Selector
import|;
end_import

begin_import
import|import
name|java
operator|.
name|security
operator|.
name|cert
operator|.
name|X509Certificate
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
name|SSLContext
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
name|SSLEngine
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
name|SSLEngineResult
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
name|SSLEngineResult
operator|.
name|HandshakeStatus
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
name|SSLPeerUnverifiedException
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
name|SSLSession
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
name|ConnectionInfo
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

begin_class
specifier|public
class|class
name|NIOSSLTransport
extends|extends
name|NIOTransport
block|{
specifier|private
specifier|static
specifier|final
name|Logger
name|LOG
init|=
name|LoggerFactory
operator|.
name|getLogger
argument_list|(
name|NIOSSLTransport
operator|.
name|class
argument_list|)
decl_stmt|;
specifier|protected
name|boolean
name|needClientAuth
decl_stmt|;
specifier|protected
name|boolean
name|wantClientAuth
decl_stmt|;
specifier|protected
name|String
index|[]
name|enabledCipherSuites
decl_stmt|;
specifier|protected
name|String
index|[]
name|enabledProtocols
decl_stmt|;
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
specifier|protected
specifier|volatile
name|boolean
name|handshakeInProgress
init|=
literal|false
decl_stmt|;
specifier|protected
name|SSLEngineResult
operator|.
name|Status
name|status
init|=
literal|null
decl_stmt|;
specifier|protected
name|SSLEngineResult
operator|.
name|HandshakeStatus
name|handshakeStatus
init|=
literal|null
decl_stmt|;
specifier|protected
name|TaskRunnerFactory
name|taskRunnerFactory
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
name|NIOOutputStream
name|outputStream
init|=
literal|null
decl_stmt|;
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
name|String
name|remoteHost
init|=
literal|null
decl_stmt|;
name|int
name|remotePort
init|=
operator|-
literal|1
decl_stmt|;
try|try
block|{
name|URI
name|remoteAddress
init|=
operator|new
name|URI
argument_list|(
name|this
operator|.
name|getRemoteAddress
argument_list|()
argument_list|)
decl_stmt|;
name|remoteHost
operator|=
name|remoteAddress
operator|.
name|getHost
argument_list|()
expr_stmt|;
name|remotePort
operator|=
name|remoteAddress
operator|.
name|getPort
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{             }
comment|// initialize engine, the initial sslSession we get will need to be
comment|// updated once the ssl handshake process is completed.
if|if
condition|(
name|remoteHost
operator|!=
literal|null
operator|&&
name|remotePort
operator|!=
operator|-
literal|1
condition|)
block|{
name|sslEngine
operator|=
name|sslContext
operator|.
name|createSSLEngine
argument_list|(
name|remoteHost
argument_list|,
name|remotePort
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|sslEngine
operator|=
name|sslContext
operator|.
name|createSSLEngine
argument_list|()
expr_stmt|;
block|}
name|sslEngine
operator|.
name|setUseClientMode
argument_list|(
literal|false
argument_list|)
expr_stmt|;
if|if
condition|(
name|enabledCipherSuites
operator|!=
literal|null
condition|)
block|{
name|sslEngine
operator|.
name|setEnabledCipherSuites
argument_list|(
name|enabledCipherSuites
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|enabledProtocols
operator|!=
literal|null
condition|)
block|{
name|sslEngine
operator|.
name|setEnabledProtocols
argument_list|(
name|enabledProtocols
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|wantClientAuth
condition|)
block|{
name|sslEngine
operator|.
name|setWantClientAuth
argument_list|(
name|wantClientAuth
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|needClientAuth
condition|)
block|{
name|sslEngine
operator|.
name|setNeedClientAuth
argument_list|(
name|needClientAuth
argument_list|)
expr_stmt|;
block|}
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
name|outputStream
operator|=
operator|new
name|NIOOutputStream
argument_list|(
name|channel
argument_list|)
expr_stmt|;
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
try|try
block|{
if|if
condition|(
name|outputStream
operator|!=
literal|null
condition|)
block|{
name|outputStream
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
name|super
operator|.
name|closeStreams
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|ex
parameter_list|)
block|{}
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
comment|// Once handshake completes we need to ask for the now real sslSession
comment|// otherwise the session would return 'SSL_NULL_WITH_NULL_NULL' for the
comment|// cipher suite.
name|sslSession
operator|=
name|sslEngine
operator|.
name|getSession
argument_list|()
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
annotation|@
name|Override
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
annotation|@
name|Override
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
annotation|@
name|Override
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
operator|!
name|plain
operator|.
name|hasRemaining
argument_list|()
condition|)
block|{
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
block|{
break|break;
block|}
comment|// channel is closed, cleanup
if|if
condition|(
name|readCount
operator|==
operator|-
literal|1
condition|)
block|{
name|onException
argument_list|(
operator|new
name|EOFException
argument_list|()
argument_list|)
expr_stmt|;
name|selection
operator|.
name|close
argument_list|()
expr_stmt|;
break|break;
block|}
name|receiveCounter
operator|+=
name|readCount
expr_stmt|;
block|}
if|if
condition|(
name|status
operator|==
name|SSLEngineResult
operator|.
name|Status
operator|.
name|OK
operator|&&
name|handshakeStatus
operator|!=
name|SSLEngineResult
operator|.
name|HandshakeStatus
operator|.
name|NEED_UNWRAP
condition|)
block|{
name|processCommand
argument_list|(
name|plain
argument_list|)
expr_stmt|;
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
specifier|protected
name|void
name|processCommand
parameter_list|(
name|ByteBuffer
name|plain
parameter_list|)
throws|throws
name|Exception
block|{
comment|// Are we waiting for the next Command or are we building on the current one
if|if
condition|(
name|nextFrameSize
operator|==
operator|-
literal|1
condition|)
block|{
comment|// We can get small packets that don't give us enough for the frame size
comment|// so allocate enough for the initial size value and
if|if
condition|(
name|plain
operator|.
name|remaining
argument_list|()
operator|<
name|Integer
operator|.
name|SIZE
condition|)
block|{
if|if
condition|(
name|currentBuffer
operator|==
literal|null
condition|)
block|{
name|currentBuffer
operator|=
name|ByteBuffer
operator|.
name|allocate
argument_list|(
literal|4
argument_list|)
expr_stmt|;
block|}
comment|// Go until we fill the integer sized current buffer.
while|while
condition|(
name|currentBuffer
operator|.
name|hasRemaining
argument_list|()
operator|&&
name|plain
operator|.
name|hasRemaining
argument_list|()
condition|)
block|{
name|currentBuffer
operator|.
name|put
argument_list|(
name|plain
operator|.
name|get
argument_list|()
argument_list|)
expr_stmt|;
block|}
comment|// Didn't we get enough yet to figure out next frame size.
if|if
condition|(
name|currentBuffer
operator|.
name|hasRemaining
argument_list|()
condition|)
block|{
return|return;
block|}
else|else
block|{
name|currentBuffer
operator|.
name|flip
argument_list|()
expr_stmt|;
name|nextFrameSize
operator|=
name|currentBuffer
operator|.
name|getInt
argument_list|()
expr_stmt|;
block|}
block|}
else|else
block|{
comment|// Either we are completing a previous read of the next frame size or its
comment|// fully contained in plain already.
if|if
condition|(
name|currentBuffer
operator|!=
literal|null
condition|)
block|{
comment|// Finish the frame size integer read and get from the current buffer.
while|while
condition|(
name|currentBuffer
operator|.
name|hasRemaining
argument_list|()
condition|)
block|{
name|currentBuffer
operator|.
name|put
argument_list|(
name|plain
operator|.
name|get
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|currentBuffer
operator|.
name|flip
argument_list|()
expr_stmt|;
name|nextFrameSize
operator|=
name|currentBuffer
operator|.
name|getInt
argument_list|()
expr_stmt|;
block|}
else|else
block|{
name|nextFrameSize
operator|=
name|plain
operator|.
name|getInt
argument_list|()
expr_stmt|;
block|}
block|}
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
comment|// now we got the data, lets reallocate and store the size for the marshaler.
comment|// if there's more data in plain, then the next call will start processing it.
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
block|}
else|else
block|{
comment|// If its all in one read then we can just take it all, otherwise take only
comment|// the current frame size and the next iteration starts a new command.
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
comment|// Either we have enough data for a new command or we have to wait for some more.
if|if
condition|(
name|currentBuffer
operator|.
name|hasRemaining
argument_list|()
condition|)
block|{
return|return;
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
name|command
argument_list|)
expr_stmt|;
name|nextFrameSize
operator|=
operator|-
literal|1
expr_stmt|;
name|currentBuffer
operator|=
literal|null
expr_stmt|;
block|}
block|}
block|}
specifier|protected
name|int
name|secureRead
parameter_list|(
name|ByteBuffer
name|plain
parameter_list|)
throws|throws
name|Exception
block|{
if|if
condition|(
operator|!
operator|(
name|inputBuffer
operator|.
name|position
argument_list|()
operator|!=
literal|0
operator|&&
name|inputBuffer
operator|.
name|hasRemaining
argument_list|()
operator|)
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
literal|0
operator|&&
operator|!
operator|(
name|sslEngine
operator|.
name|getHandshakeStatus
argument_list|()
operator|.
name|equals
argument_list|(
name|SSLEngineResult
operator|.
name|HandshakeStatus
operator|.
name|NEED_UNWRAP
argument_list|)
operator|)
condition|)
block|{
return|return
literal|0
return|;
block|}
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
comment|// TODO deal with BUFFER_OVERFLOW
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
name|Selector
name|selector
init|=
literal|null
decl_stmt|;
name|SelectionKey
name|key
init|=
literal|null
decl_stmt|;
name|boolean
name|readable
init|=
literal|true
decl_stmt|;
name|int
name|timeout
init|=
literal|100
decl_stmt|;
try|try
block|{
while|while
condition|(
literal|true
condition|)
block|{
name|HandshakeStatus
name|handshakeStatus
init|=
name|sslEngine
operator|.
name|getHandshakeStatus
argument_list|()
decl_stmt|;
switch|switch
condition|(
name|handshakeStatus
condition|)
block|{
case|case
name|NEED_UNWRAP
case|:
if|if
condition|(
name|readable
condition|)
block|{
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
block|}
if|if
condition|(
name|this
operator|.
name|status
operator|==
name|SSLEngineResult
operator|.
name|Status
operator|.
name|BUFFER_UNDERFLOW
condition|)
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
name|selector
operator|==
literal|null
condition|)
block|{
name|selector
operator|=
name|Selector
operator|.
name|open
argument_list|()
expr_stmt|;
name|key
operator|=
name|channel
operator|.
name|register
argument_list|(
name|selector
argument_list|,
name|SelectionKey
operator|.
name|OP_READ
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|key
operator|.
name|interestOps
argument_list|(
name|SelectionKey
operator|.
name|OP_READ
argument_list|)
expr_stmt|;
block|}
name|int
name|keyCount
init|=
name|selector
operator|.
name|select
argument_list|(
name|timeout
argument_list|)
decl_stmt|;
if|if
condition|(
name|keyCount
operator|==
literal|0
operator|&&
operator|(
operator|(
name|System
operator|.
name|currentTimeMillis
argument_list|()
operator|-
name|now
operator|)
operator|>=
name|timeout
operator|)
condition|)
block|{
throw|throw
operator|new
name|SocketTimeoutException
argument_list|(
literal|"Timeout during handshake"
argument_list|)
throw|;
block|}
name|readable
operator|=
name|key
operator|.
name|isReadable
argument_list|()
expr_stmt|;
block|}
break|break;
case|case
name|NEED_TASK
case|:
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
finally|finally
block|{
if|if
condition|(
name|key
operator|!=
literal|null
condition|)
try|try
block|{
name|key
operator|.
name|cancel
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|ignore
parameter_list|)
block|{}
if|if
condition|(
name|selector
operator|!=
literal|null
condition|)
try|try
block|{
name|selector
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|ignore
parameter_list|)
block|{}
block|}
block|}
annotation|@
name|Override
specifier|protected
name|void
name|doStart
parameter_list|()
throws|throws
name|Exception
block|{
name|taskRunnerFactory
operator|=
operator|new
name|TaskRunnerFactory
argument_list|(
literal|"ActiveMQ NIOSSLTransport Task"
argument_list|)
expr_stmt|;
comment|// no need to init as we can delay that until demand (eg in doHandshake)
name|super
operator|.
name|doStart
argument_list|()
expr_stmt|;
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
name|channel
operator|=
literal|null
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
comment|/**      * Overriding in order to add the client's certificates to ConnectionInfo Commands.      *      * @param command      *            The Command coming in.      */
annotation|@
name|Override
specifier|public
name|void
name|doConsume
parameter_list|(
name|Object
name|command
parameter_list|)
block|{
if|if
condition|(
name|command
operator|instanceof
name|ConnectionInfo
condition|)
block|{
name|ConnectionInfo
name|connectionInfo
init|=
operator|(
name|ConnectionInfo
operator|)
name|command
decl_stmt|;
name|connectionInfo
operator|.
name|setTransportContext
argument_list|(
name|getPeerCertificates
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|super
operator|.
name|doConsume
argument_list|(
name|command
argument_list|)
expr_stmt|;
block|}
comment|/**      * @return peer certificate chain associated with the ssl socket      */
specifier|public
name|X509Certificate
index|[]
name|getPeerCertificates
parameter_list|()
block|{
name|X509Certificate
index|[]
name|clientCertChain
init|=
literal|null
decl_stmt|;
try|try
block|{
if|if
condition|(
name|sslEngine
operator|.
name|getSession
argument_list|()
operator|!=
literal|null
condition|)
block|{
name|clientCertChain
operator|=
operator|(
name|X509Certificate
index|[]
operator|)
name|sslEngine
operator|.
name|getSession
argument_list|()
operator|.
name|getPeerCertificates
argument_list|()
expr_stmt|;
block|}
block|}
catch|catch
parameter_list|(
name|SSLPeerUnverifiedException
name|e
parameter_list|)
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
literal|"Failed to get peer certificates."
argument_list|,
name|e
argument_list|)
expr_stmt|;
block|}
block|}
return|return
name|clientCertChain
return|;
block|}
specifier|public
name|boolean
name|isNeedClientAuth
parameter_list|()
block|{
return|return
name|needClientAuth
return|;
block|}
specifier|public
name|void
name|setNeedClientAuth
parameter_list|(
name|boolean
name|needClientAuth
parameter_list|)
block|{
name|this
operator|.
name|needClientAuth
operator|=
name|needClientAuth
expr_stmt|;
block|}
specifier|public
name|boolean
name|isWantClientAuth
parameter_list|()
block|{
return|return
name|wantClientAuth
return|;
block|}
specifier|public
name|void
name|setWantClientAuth
parameter_list|(
name|boolean
name|wantClientAuth
parameter_list|)
block|{
name|this
operator|.
name|wantClientAuth
operator|=
name|wantClientAuth
expr_stmt|;
block|}
specifier|public
name|String
index|[]
name|getEnabledCipherSuites
parameter_list|()
block|{
return|return
name|enabledCipherSuites
return|;
block|}
specifier|public
name|void
name|setEnabledCipherSuites
parameter_list|(
name|String
index|[]
name|enabledCipherSuites
parameter_list|)
block|{
name|this
operator|.
name|enabledCipherSuites
operator|=
name|enabledCipherSuites
expr_stmt|;
block|}
specifier|public
name|String
index|[]
name|getEnabledProtocols
parameter_list|()
block|{
return|return
name|enabledProtocols
return|;
block|}
specifier|public
name|void
name|setEnabledProtocols
parameter_list|(
name|String
index|[]
name|enabledProtocols
parameter_list|)
block|{
name|this
operator|.
name|enabledProtocols
operator|=
name|enabledProtocols
expr_stmt|;
block|}
block|}
end_class

end_unit

