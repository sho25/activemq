begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *      http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
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
name|amqp
operator|.
name|client
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
name|URI
import|;
end_import

begin_import
import|import
name|java
operator|.
name|security
operator|.
name|Principal
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
name|org
operator|.
name|apache
operator|.
name|activemq
operator|.
name|transport
operator|.
name|amqp
operator|.
name|client
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

begin_import
import|import
name|io
operator|.
name|netty
operator|.
name|bootstrap
operator|.
name|Bootstrap
import|;
end_import

begin_import
import|import
name|io
operator|.
name|netty
operator|.
name|buffer
operator|.
name|ByteBuf
import|;
end_import

begin_import
import|import
name|io
operator|.
name|netty
operator|.
name|channel
operator|.
name|Channel
import|;
end_import

begin_import
import|import
name|io
operator|.
name|netty
operator|.
name|channel
operator|.
name|ChannelFuture
import|;
end_import

begin_import
import|import
name|io
operator|.
name|netty
operator|.
name|channel
operator|.
name|ChannelFutureListener
import|;
end_import

begin_import
import|import
name|io
operator|.
name|netty
operator|.
name|channel
operator|.
name|ChannelHandlerContext
import|;
end_import

begin_import
import|import
name|io
operator|.
name|netty
operator|.
name|channel
operator|.
name|ChannelInitializer
import|;
end_import

begin_import
import|import
name|io
operator|.
name|netty
operator|.
name|channel
operator|.
name|ChannelOption
import|;
end_import

begin_import
import|import
name|io
operator|.
name|netty
operator|.
name|channel
operator|.
name|ChannelPromise
import|;
end_import

begin_import
import|import
name|io
operator|.
name|netty
operator|.
name|channel
operator|.
name|EventLoopGroup
import|;
end_import

begin_import
import|import
name|io
operator|.
name|netty
operator|.
name|channel
operator|.
name|FixedRecvByteBufAllocator
import|;
end_import

begin_import
import|import
name|io
operator|.
name|netty
operator|.
name|channel
operator|.
name|SimpleChannelInboundHandler
import|;
end_import

begin_import
import|import
name|io
operator|.
name|netty
operator|.
name|channel
operator|.
name|nio
operator|.
name|NioEventLoopGroup
import|;
end_import

begin_import
import|import
name|io
operator|.
name|netty
operator|.
name|channel
operator|.
name|socket
operator|.
name|nio
operator|.
name|NioSocketChannel
import|;
end_import

begin_import
import|import
name|io
operator|.
name|netty
operator|.
name|handler
operator|.
name|codec
operator|.
name|http
operator|.
name|DefaultHttpHeaders
import|;
end_import

begin_import
import|import
name|io
operator|.
name|netty
operator|.
name|handler
operator|.
name|codec
operator|.
name|http
operator|.
name|FullHttpResponse
import|;
end_import

begin_import
import|import
name|io
operator|.
name|netty
operator|.
name|handler
operator|.
name|codec
operator|.
name|http
operator|.
name|HttpClientCodec
import|;
end_import

begin_import
import|import
name|io
operator|.
name|netty
operator|.
name|handler
operator|.
name|codec
operator|.
name|http
operator|.
name|HttpObjectAggregator
import|;
end_import

begin_import
import|import
name|io
operator|.
name|netty
operator|.
name|handler
operator|.
name|codec
operator|.
name|http
operator|.
name|websocketx
operator|.
name|BinaryWebSocketFrame
import|;
end_import

begin_import
import|import
name|io
operator|.
name|netty
operator|.
name|handler
operator|.
name|codec
operator|.
name|http
operator|.
name|websocketx
operator|.
name|CloseWebSocketFrame
import|;
end_import

begin_import
import|import
name|io
operator|.
name|netty
operator|.
name|handler
operator|.
name|codec
operator|.
name|http
operator|.
name|websocketx
operator|.
name|PongWebSocketFrame
import|;
end_import

begin_import
import|import
name|io
operator|.
name|netty
operator|.
name|handler
operator|.
name|codec
operator|.
name|http
operator|.
name|websocketx
operator|.
name|TextWebSocketFrame
import|;
end_import

begin_import
import|import
name|io
operator|.
name|netty
operator|.
name|handler
operator|.
name|codec
operator|.
name|http
operator|.
name|websocketx
operator|.
name|WebSocketClientHandshaker
import|;
end_import

begin_import
import|import
name|io
operator|.
name|netty
operator|.
name|handler
operator|.
name|codec
operator|.
name|http
operator|.
name|websocketx
operator|.
name|WebSocketClientHandshakerFactory
import|;
end_import

begin_import
import|import
name|io
operator|.
name|netty
operator|.
name|handler
operator|.
name|codec
operator|.
name|http
operator|.
name|websocketx
operator|.
name|WebSocketFrame
import|;
end_import

begin_import
import|import
name|io
operator|.
name|netty
operator|.
name|handler
operator|.
name|codec
operator|.
name|http
operator|.
name|websocketx
operator|.
name|WebSocketVersion
import|;
end_import

begin_import
import|import
name|io
operator|.
name|netty
operator|.
name|handler
operator|.
name|ssl
operator|.
name|SslHandler
import|;
end_import

begin_import
import|import
name|io
operator|.
name|netty
operator|.
name|util
operator|.
name|CharsetUtil
import|;
end_import

begin_import
import|import
name|io
operator|.
name|netty
operator|.
name|util
operator|.
name|concurrent
operator|.
name|Future
import|;
end_import

begin_import
import|import
name|io
operator|.
name|netty
operator|.
name|util
operator|.
name|concurrent
operator|.
name|GenericFutureListener
import|;
end_import

begin_comment
comment|/**  * Transport for communicating over WebSockets  */
end_comment

begin_class
specifier|public
class|class
name|NettyWSTransport
implements|implements
name|NettyTransport
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
name|NettyWSTransport
operator|.
name|class
argument_list|)
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|int
name|QUIET_PERIOD
init|=
literal|20
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|int
name|SHUTDOWN_TIMEOUT
init|=
literal|100
decl_stmt|;
specifier|protected
name|Bootstrap
name|bootstrap
decl_stmt|;
specifier|protected
name|EventLoopGroup
name|group
decl_stmt|;
specifier|protected
name|Channel
name|channel
decl_stmt|;
specifier|protected
name|NettyTransportListener
name|listener
decl_stmt|;
specifier|protected
name|NettyTransportOptions
name|options
decl_stmt|;
specifier|protected
specifier|final
name|URI
name|remote
decl_stmt|;
specifier|protected
name|boolean
name|secure
decl_stmt|;
specifier|private
specifier|final
name|AtomicBoolean
name|connected
init|=
operator|new
name|AtomicBoolean
argument_list|()
decl_stmt|;
specifier|private
specifier|final
name|AtomicBoolean
name|closed
init|=
operator|new
name|AtomicBoolean
argument_list|()
decl_stmt|;
specifier|private
name|ChannelPromise
name|handshakeFuture
decl_stmt|;
specifier|private
name|IOException
name|failureCause
decl_stmt|;
specifier|private
name|Throwable
name|pendingFailure
decl_stmt|;
comment|/**      * Create a new transport instance      *      * @param remoteLocation      *        the URI that defines the remote resource to connect to.      * @param options      *        the transport options used to configure the socket connection.      */
specifier|public
name|NettyWSTransport
parameter_list|(
name|URI
name|remoteLocation
parameter_list|,
name|NettyTransportOptions
name|options
parameter_list|)
block|{
name|this
argument_list|(
literal|null
argument_list|,
name|remoteLocation
argument_list|,
name|options
argument_list|)
expr_stmt|;
block|}
comment|/**      * Create a new transport instance      *      * @param listener      *        the TransportListener that will receive events from this Transport.      * @param remoteLocation      *        the URI that defines the remote resource to connect to.      * @param options      *        the transport options used to configure the socket connection.      */
specifier|public
name|NettyWSTransport
parameter_list|(
name|NettyTransportListener
name|listener
parameter_list|,
name|URI
name|remoteLocation
parameter_list|,
name|NettyTransportOptions
name|options
parameter_list|)
block|{
name|this
operator|.
name|options
operator|=
name|options
expr_stmt|;
name|this
operator|.
name|listener
operator|=
name|listener
expr_stmt|;
name|this
operator|.
name|remote
operator|=
name|remoteLocation
expr_stmt|;
name|this
operator|.
name|secure
operator|=
name|remoteLocation
operator|.
name|getScheme
argument_list|()
operator|.
name|equalsIgnoreCase
argument_list|(
literal|"wss"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|connect
parameter_list|()
throws|throws
name|IOException
block|{
if|if
condition|(
name|listener
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|IllegalStateException
argument_list|(
literal|"A transport listener must be set before connection attempts."
argument_list|)
throw|;
block|}
name|group
operator|=
operator|new
name|NioEventLoopGroup
argument_list|(
literal|1
argument_list|)
expr_stmt|;
name|bootstrap
operator|=
operator|new
name|Bootstrap
argument_list|()
expr_stmt|;
name|bootstrap
operator|.
name|group
argument_list|(
name|group
argument_list|)
expr_stmt|;
name|bootstrap
operator|.
name|channel
argument_list|(
name|NioSocketChannel
operator|.
name|class
argument_list|)
expr_stmt|;
name|bootstrap
operator|.
name|handler
argument_list|(
operator|new
name|ChannelInitializer
argument_list|<
name|Channel
argument_list|>
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|void
name|initChannel
parameter_list|(
name|Channel
name|connectedChannel
parameter_list|)
throws|throws
name|Exception
block|{
name|configureChannel
argument_list|(
name|connectedChannel
argument_list|)
expr_stmt|;
block|}
block|}
argument_list|)
expr_stmt|;
name|configureNetty
argument_list|(
name|bootstrap
argument_list|,
name|getTransportOptions
argument_list|()
argument_list|)
expr_stmt|;
name|ChannelFuture
name|future
decl_stmt|;
try|try
block|{
name|future
operator|=
name|bootstrap
operator|.
name|connect
argument_list|(
name|getRemoteHost
argument_list|()
argument_list|,
name|getRemotePort
argument_list|()
argument_list|)
expr_stmt|;
name|future
operator|.
name|addListener
argument_list|(
operator|new
name|ChannelFutureListener
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|void
name|operationComplete
parameter_list|(
name|ChannelFuture
name|future
parameter_list|)
throws|throws
name|Exception
block|{
if|if
condition|(
name|future
operator|.
name|isSuccess
argument_list|()
condition|)
block|{
name|handleConnected
argument_list|(
name|future
operator|.
name|channel
argument_list|()
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|future
operator|.
name|isCancelled
argument_list|()
condition|)
block|{
name|connectionFailed
argument_list|(
name|future
operator|.
name|channel
argument_list|()
argument_list|,
operator|new
name|IOException
argument_list|(
literal|"Connection attempt was cancelled"
argument_list|)
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|connectionFailed
argument_list|(
name|future
operator|.
name|channel
argument_list|()
argument_list|,
name|IOExceptionSupport
operator|.
name|create
argument_list|(
name|future
operator|.
name|cause
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
block|}
argument_list|)
expr_stmt|;
name|future
operator|.
name|sync
argument_list|()
expr_stmt|;
comment|// Now wait for WS protocol level handshake completion
name|handshakeFuture
operator|.
name|await
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|InterruptedException
name|ex
parameter_list|)
block|{
name|LOG
operator|.
name|debug
argument_list|(
literal|"Transport connection attempt was interrupted."
argument_list|)
expr_stmt|;
name|Thread
operator|.
name|interrupted
argument_list|()
expr_stmt|;
name|failureCause
operator|=
name|IOExceptionSupport
operator|.
name|create
argument_list|(
name|ex
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|failureCause
operator|!=
literal|null
condition|)
block|{
comment|// Close out any Netty resources now as they are no longer needed.
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
operator|.
name|syncUninterruptibly
argument_list|()
expr_stmt|;
name|channel
operator|=
literal|null
expr_stmt|;
block|}
if|if
condition|(
name|group
operator|!=
literal|null
condition|)
block|{
name|group
operator|.
name|shutdownGracefully
argument_list|(
name|QUIET_PERIOD
argument_list|,
name|SHUTDOWN_TIMEOUT
argument_list|,
name|TimeUnit
operator|.
name|MILLISECONDS
argument_list|)
expr_stmt|;
name|group
operator|=
literal|null
expr_stmt|;
block|}
throw|throw
name|failureCause
throw|;
block|}
else|else
block|{
comment|// Connected, allow any held async error to fire now and close the transport.
name|channel
operator|.
name|eventLoop
argument_list|()
operator|.
name|execute
argument_list|(
operator|new
name|Runnable
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|void
name|run
parameter_list|()
block|{
if|if
condition|(
name|pendingFailure
operator|!=
literal|null
condition|)
block|{
name|channel
operator|.
name|pipeline
argument_list|()
operator|.
name|fireExceptionCaught
argument_list|(
name|pendingFailure
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
specifier|public
name|boolean
name|isConnected
parameter_list|()
block|{
return|return
name|connected
operator|.
name|get
argument_list|()
return|;
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|isSSL
parameter_list|()
block|{
return|return
name|secure
return|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|close
parameter_list|()
throws|throws
name|IOException
block|{
if|if
condition|(
name|closed
operator|.
name|compareAndSet
argument_list|(
literal|false
argument_list|,
literal|true
argument_list|)
condition|)
block|{
name|connected
operator|.
name|set
argument_list|(
literal|false
argument_list|)
expr_stmt|;
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
operator|.
name|syncUninterruptibly
argument_list|()
expr_stmt|;
block|}
if|if
condition|(
name|group
operator|!=
literal|null
condition|)
block|{
name|group
operator|.
name|shutdownGracefully
argument_list|(
name|QUIET_PERIOD
argument_list|,
name|SHUTDOWN_TIMEOUT
argument_list|,
name|TimeUnit
operator|.
name|MILLISECONDS
argument_list|)
expr_stmt|;
block|}
block|}
block|}
annotation|@
name|Override
specifier|public
name|ByteBuf
name|allocateSendBuffer
parameter_list|(
name|int
name|size
parameter_list|)
throws|throws
name|IOException
block|{
name|checkConnected
argument_list|()
expr_stmt|;
return|return
name|channel
operator|.
name|alloc
argument_list|()
operator|.
name|ioBuffer
argument_list|(
name|size
argument_list|,
name|size
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|send
parameter_list|(
name|ByteBuf
name|output
parameter_list|)
throws|throws
name|IOException
block|{
name|checkConnected
argument_list|()
expr_stmt|;
name|int
name|length
init|=
name|output
operator|.
name|readableBytes
argument_list|()
decl_stmt|;
if|if
condition|(
name|length
operator|==
literal|0
condition|)
block|{
return|return;
block|}
name|LOG
operator|.
name|trace
argument_list|(
literal|"Attempted write of: {} bytes"
argument_list|,
name|length
argument_list|)
expr_stmt|;
name|channel
operator|.
name|writeAndFlush
argument_list|(
operator|new
name|BinaryWebSocketFrame
argument_list|(
name|output
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|NettyTransportListener
name|getTransportListener
parameter_list|()
block|{
return|return
name|listener
return|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|setTransportListener
parameter_list|(
name|NettyTransportListener
name|listener
parameter_list|)
block|{
name|this
operator|.
name|listener
operator|=
name|listener
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|NettyTransportOptions
name|getTransportOptions
parameter_list|()
block|{
if|if
condition|(
name|options
operator|==
literal|null
condition|)
block|{
if|if
condition|(
name|isSSL
argument_list|()
condition|)
block|{
name|options
operator|=
name|NettyTransportSslOptions
operator|.
name|INSTANCE
expr_stmt|;
block|}
else|else
block|{
name|options
operator|=
name|NettyTransportOptions
operator|.
name|INSTANCE
expr_stmt|;
block|}
block|}
return|return
name|options
return|;
block|}
annotation|@
name|Override
specifier|public
name|URI
name|getRemoteLocation
parameter_list|()
block|{
return|return
name|remote
return|;
block|}
annotation|@
name|Override
specifier|public
name|Principal
name|getLocalPrincipal
parameter_list|()
block|{
if|if
condition|(
operator|!
name|isSSL
argument_list|()
condition|)
block|{
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|(
literal|"Not connected to a secure channel"
argument_list|)
throw|;
block|}
name|SslHandler
name|sslHandler
init|=
name|channel
operator|.
name|pipeline
argument_list|()
operator|.
name|get
argument_list|(
name|SslHandler
operator|.
name|class
argument_list|)
decl_stmt|;
return|return
name|sslHandler
operator|.
name|engine
argument_list|()
operator|.
name|getSession
argument_list|()
operator|.
name|getLocalPrincipal
argument_list|()
return|;
block|}
comment|//----- Internal implementation details, can be overridden as needed --//
specifier|protected
name|String
name|getRemoteHost
parameter_list|()
block|{
return|return
name|remote
operator|.
name|getHost
argument_list|()
return|;
block|}
specifier|protected
name|int
name|getRemotePort
parameter_list|()
block|{
name|int
name|port
init|=
name|remote
operator|.
name|getPort
argument_list|()
decl_stmt|;
if|if
condition|(
name|port
operator|<=
literal|0
condition|)
block|{
if|if
condition|(
name|isSSL
argument_list|()
condition|)
block|{
name|port
operator|=
name|getSslOptions
argument_list|()
operator|.
name|getDefaultSslPort
argument_list|()
expr_stmt|;
block|}
else|else
block|{
name|port
operator|=
name|getTransportOptions
argument_list|()
operator|.
name|getDefaultTcpPort
argument_list|()
expr_stmt|;
block|}
block|}
return|return
name|port
return|;
block|}
specifier|protected
name|void
name|configureNetty
parameter_list|(
name|Bootstrap
name|bootstrap
parameter_list|,
name|NettyTransportOptions
name|options
parameter_list|)
block|{
name|bootstrap
operator|.
name|option
argument_list|(
name|ChannelOption
operator|.
name|TCP_NODELAY
argument_list|,
name|options
operator|.
name|isTcpNoDelay
argument_list|()
argument_list|)
expr_stmt|;
name|bootstrap
operator|.
name|option
argument_list|(
name|ChannelOption
operator|.
name|CONNECT_TIMEOUT_MILLIS
argument_list|,
name|options
operator|.
name|getConnectTimeout
argument_list|()
argument_list|)
expr_stmt|;
name|bootstrap
operator|.
name|option
argument_list|(
name|ChannelOption
operator|.
name|SO_KEEPALIVE
argument_list|,
name|options
operator|.
name|isTcpKeepAlive
argument_list|()
argument_list|)
expr_stmt|;
name|bootstrap
operator|.
name|option
argument_list|(
name|ChannelOption
operator|.
name|SO_LINGER
argument_list|,
name|options
operator|.
name|getSoLinger
argument_list|()
argument_list|)
expr_stmt|;
name|bootstrap
operator|.
name|option
argument_list|(
name|ChannelOption
operator|.
name|ALLOCATOR
argument_list|,
name|PartialPooledByteBufAllocator
operator|.
name|INSTANCE
argument_list|)
expr_stmt|;
if|if
condition|(
name|options
operator|.
name|getSendBufferSize
argument_list|()
operator|!=
operator|-
literal|1
condition|)
block|{
name|bootstrap
operator|.
name|option
argument_list|(
name|ChannelOption
operator|.
name|SO_SNDBUF
argument_list|,
name|options
operator|.
name|getSendBufferSize
argument_list|()
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|options
operator|.
name|getReceiveBufferSize
argument_list|()
operator|!=
operator|-
literal|1
condition|)
block|{
name|bootstrap
operator|.
name|option
argument_list|(
name|ChannelOption
operator|.
name|SO_RCVBUF
argument_list|,
name|options
operator|.
name|getReceiveBufferSize
argument_list|()
argument_list|)
expr_stmt|;
name|bootstrap
operator|.
name|option
argument_list|(
name|ChannelOption
operator|.
name|RCVBUF_ALLOCATOR
argument_list|,
operator|new
name|FixedRecvByteBufAllocator
argument_list|(
name|options
operator|.
name|getReceiveBufferSize
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|options
operator|.
name|getTrafficClass
argument_list|()
operator|!=
operator|-
literal|1
condition|)
block|{
name|bootstrap
operator|.
name|option
argument_list|(
name|ChannelOption
operator|.
name|IP_TOS
argument_list|,
name|options
operator|.
name|getTrafficClass
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
specifier|protected
name|void
name|configureChannel
parameter_list|(
specifier|final
name|Channel
name|channel
parameter_list|)
throws|throws
name|Exception
block|{
if|if
condition|(
name|isSSL
argument_list|()
condition|)
block|{
name|SslHandler
name|sslHandler
init|=
name|NettyTransportSupport
operator|.
name|createSslHandler
argument_list|(
name|getRemoteLocation
argument_list|()
argument_list|,
name|getSslOptions
argument_list|()
argument_list|)
decl_stmt|;
name|sslHandler
operator|.
name|handshakeFuture
argument_list|()
operator|.
name|addListener
argument_list|(
operator|new
name|GenericFutureListener
argument_list|<
name|Future
argument_list|<
name|Channel
argument_list|>
argument_list|>
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|void
name|operationComplete
parameter_list|(
name|Future
argument_list|<
name|Channel
argument_list|>
name|future
parameter_list|)
throws|throws
name|Exception
block|{
if|if
condition|(
name|future
operator|.
name|isSuccess
argument_list|()
condition|)
block|{
name|LOG
operator|.
name|trace
argument_list|(
literal|"SSL Handshake has completed: {}"
argument_list|,
name|channel
argument_list|)
expr_stmt|;
name|connectionEstablished
argument_list|(
name|channel
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|LOG
operator|.
name|trace
argument_list|(
literal|"SSL Handshake has failed: {}"
argument_list|,
name|channel
argument_list|)
expr_stmt|;
name|connectionFailed
argument_list|(
name|channel
argument_list|,
name|IOExceptionSupport
operator|.
name|create
argument_list|(
name|future
operator|.
name|cause
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
block|}
argument_list|)
expr_stmt|;
name|channel
operator|.
name|pipeline
argument_list|()
operator|.
name|addLast
argument_list|(
name|sslHandler
argument_list|)
expr_stmt|;
block|}
name|channel
operator|.
name|pipeline
argument_list|()
operator|.
name|addLast
argument_list|(
operator|new
name|HttpClientCodec
argument_list|()
argument_list|)
expr_stmt|;
name|channel
operator|.
name|pipeline
argument_list|()
operator|.
name|addLast
argument_list|(
operator|new
name|HttpObjectAggregator
argument_list|(
literal|8192
argument_list|)
argument_list|)
expr_stmt|;
name|channel
operator|.
name|pipeline
argument_list|()
operator|.
name|addLast
argument_list|(
operator|new
name|NettyTcpTransportHandler
argument_list|()
argument_list|)
expr_stmt|;
block|}
specifier|protected
name|void
name|handleConnected
parameter_list|(
specifier|final
name|Channel
name|channel
parameter_list|)
throws|throws
name|Exception
block|{
if|if
condition|(
operator|!
name|isSSL
argument_list|()
condition|)
block|{
name|connectionEstablished
argument_list|(
name|channel
argument_list|)
expr_stmt|;
block|}
block|}
comment|//----- State change handlers and checks ---------------------------------//
comment|/**      * Called when the transport has successfully connected and is ready for use.      */
specifier|protected
name|void
name|connectionEstablished
parameter_list|(
name|Channel
name|connectedChannel
parameter_list|)
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"WebSocket connectionEstablished! {}"
argument_list|,
name|connectedChannel
argument_list|)
expr_stmt|;
name|channel
operator|=
name|connectedChannel
expr_stmt|;
name|connected
operator|.
name|set
argument_list|(
literal|true
argument_list|)
expr_stmt|;
block|}
comment|/**      * Called when the transport connection failed and an error should be returned.      *      * @param failedChannel      *      The Channel instance that failed.      * @param cause      *      An IOException that describes the cause of the failed connection.      */
specifier|protected
name|void
name|connectionFailed
parameter_list|(
name|Channel
name|failedChannel
parameter_list|,
name|IOException
name|cause
parameter_list|)
block|{
name|failureCause
operator|=
name|IOExceptionSupport
operator|.
name|create
argument_list|(
name|cause
argument_list|)
expr_stmt|;
name|channel
operator|=
name|failedChannel
expr_stmt|;
name|connected
operator|.
name|set
argument_list|(
literal|false
argument_list|)
expr_stmt|;
name|handshakeFuture
operator|.
name|setFailure
argument_list|(
name|cause
argument_list|)
expr_stmt|;
block|}
specifier|private
name|NettyTransportSslOptions
name|getSslOptions
parameter_list|()
block|{
return|return
operator|(
name|NettyTransportSslOptions
operator|)
name|getTransportOptions
argument_list|()
return|;
block|}
specifier|private
name|void
name|checkConnected
parameter_list|()
throws|throws
name|IOException
block|{
if|if
condition|(
operator|!
name|connected
operator|.
name|get
argument_list|()
condition|)
block|{
throw|throw
operator|new
name|IOException
argument_list|(
literal|"Cannot send to a non-connected transport."
argument_list|)
throw|;
block|}
block|}
comment|//----- Handle connection events -----------------------------------------//
specifier|private
class|class
name|NettyTcpTransportHandler
extends|extends
name|SimpleChannelInboundHandler
argument_list|<
name|Object
argument_list|>
block|{
specifier|private
specifier|final
name|WebSocketClientHandshaker
name|handshaker
decl_stmt|;
specifier|public
name|NettyTcpTransportHandler
parameter_list|()
block|{
name|handshaker
operator|=
name|WebSocketClientHandshakerFactory
operator|.
name|newHandshaker
argument_list|(
name|remote
argument_list|,
name|WebSocketVersion
operator|.
name|V13
argument_list|,
literal|"amqp"
argument_list|,
literal|false
argument_list|,
operator|new
name|DefaultHttpHeaders
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|handlerAdded
parameter_list|(
name|ChannelHandlerContext
name|context
parameter_list|)
block|{
name|LOG
operator|.
name|trace
argument_list|(
literal|"Handler has become added! Channel is {}"
argument_list|,
name|context
operator|.
name|channel
argument_list|()
argument_list|)
expr_stmt|;
name|handshakeFuture
operator|=
name|context
operator|.
name|newPromise
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|channelActive
parameter_list|(
name|ChannelHandlerContext
name|context
parameter_list|)
throws|throws
name|Exception
block|{
name|LOG
operator|.
name|trace
argument_list|(
literal|"Channel has become active! Channel is {}"
argument_list|,
name|context
operator|.
name|channel
argument_list|()
argument_list|)
expr_stmt|;
name|handshaker
operator|.
name|handshake
argument_list|(
name|context
operator|.
name|channel
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|channelInactive
parameter_list|(
name|ChannelHandlerContext
name|context
parameter_list|)
throws|throws
name|Exception
block|{
name|LOG
operator|.
name|trace
argument_list|(
literal|"Channel has gone inactive! Channel is {}"
argument_list|,
name|context
operator|.
name|channel
argument_list|()
argument_list|)
expr_stmt|;
if|if
condition|(
name|connected
operator|.
name|compareAndSet
argument_list|(
literal|true
argument_list|,
literal|false
argument_list|)
operator|&&
operator|!
name|closed
operator|.
name|get
argument_list|()
condition|)
block|{
name|LOG
operator|.
name|trace
argument_list|(
literal|"Firing onTransportClosed listener"
argument_list|)
expr_stmt|;
name|listener
operator|.
name|onTransportClosed
argument_list|()
expr_stmt|;
block|}
block|}
annotation|@
name|Override
specifier|public
name|void
name|exceptionCaught
parameter_list|(
name|ChannelHandlerContext
name|context
parameter_list|,
name|Throwable
name|cause
parameter_list|)
throws|throws
name|Exception
block|{
name|LOG
operator|.
name|trace
argument_list|(
literal|"Exception on channel! Channel is {} -> {}"
argument_list|,
name|context
operator|.
name|channel
argument_list|()
argument_list|,
name|cause
operator|.
name|getMessage
argument_list|()
argument_list|)
expr_stmt|;
name|LOG
operator|.
name|trace
argument_list|(
literal|"Error Stack: "
argument_list|,
name|cause
argument_list|)
expr_stmt|;
if|if
condition|(
name|connected
operator|.
name|compareAndSet
argument_list|(
literal|true
argument_list|,
literal|false
argument_list|)
operator|&&
operator|!
name|closed
operator|.
name|get
argument_list|()
condition|)
block|{
name|LOG
operator|.
name|trace
argument_list|(
literal|"Firing onTransportError listener"
argument_list|)
expr_stmt|;
if|if
condition|(
name|pendingFailure
operator|!=
literal|null
condition|)
block|{
name|listener
operator|.
name|onTransportError
argument_list|(
name|pendingFailure
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|listener
operator|.
name|onTransportError
argument_list|(
name|cause
argument_list|)
expr_stmt|;
block|}
block|}
else|else
block|{
comment|// Hold the first failure for later dispatch if connect succeeds.
comment|// This will then trigger disconnect using the first error reported.
if|if
condition|(
name|pendingFailure
operator|!=
literal|null
condition|)
block|{
name|LOG
operator|.
name|trace
argument_list|(
literal|"Holding error until connect succeeds: {}"
argument_list|,
name|cause
operator|.
name|getMessage
argument_list|()
argument_list|)
expr_stmt|;
name|pendingFailure
operator|=
name|cause
expr_stmt|;
block|}
if|if
condition|(
operator|!
name|handshakeFuture
operator|.
name|isDone
argument_list|()
condition|)
block|{
name|handshakeFuture
operator|.
name|setFailure
argument_list|(
name|cause
argument_list|)
expr_stmt|;
block|}
block|}
block|}
annotation|@
name|Override
specifier|protected
name|void
name|channelRead0
parameter_list|(
name|ChannelHandlerContext
name|ctx
parameter_list|,
name|Object
name|message
parameter_list|)
throws|throws
name|Exception
block|{
name|LOG
operator|.
name|trace
argument_list|(
literal|"New data read: incoming: {}"
argument_list|,
name|message
argument_list|)
expr_stmt|;
name|Channel
name|ch
init|=
name|ctx
operator|.
name|channel
argument_list|()
decl_stmt|;
if|if
condition|(
operator|!
name|handshaker
operator|.
name|isHandshakeComplete
argument_list|()
condition|)
block|{
name|handshaker
operator|.
name|finishHandshake
argument_list|(
name|ch
argument_list|,
operator|(
name|FullHttpResponse
operator|)
name|message
argument_list|)
expr_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"WebSocket Client connected! {}"
argument_list|,
name|ctx
operator|.
name|channel
argument_list|()
argument_list|)
expr_stmt|;
name|handshakeFuture
operator|.
name|setSuccess
argument_list|()
expr_stmt|;
return|return;
block|}
comment|// We shouldn't get this since we handle the handshake previously.
if|if
condition|(
name|message
operator|instanceof
name|FullHttpResponse
condition|)
block|{
name|FullHttpResponse
name|response
init|=
operator|(
name|FullHttpResponse
operator|)
name|message
decl_stmt|;
throw|throw
operator|new
name|IllegalStateException
argument_list|(
literal|"Unexpected FullHttpResponse (getStatus="
operator|+
name|response
operator|.
name|getStatus
argument_list|()
operator|+
literal|", content="
operator|+
name|response
operator|.
name|content
argument_list|()
operator|.
name|toString
argument_list|(
name|CharsetUtil
operator|.
name|UTF_8
argument_list|)
operator|+
literal|')'
argument_list|)
throw|;
block|}
name|WebSocketFrame
name|frame
init|=
operator|(
name|WebSocketFrame
operator|)
name|message
decl_stmt|;
if|if
condition|(
name|frame
operator|instanceof
name|TextWebSocketFrame
condition|)
block|{
name|TextWebSocketFrame
name|textFrame
init|=
operator|(
name|TextWebSocketFrame
operator|)
name|frame
decl_stmt|;
name|LOG
operator|.
name|warn
argument_list|(
literal|"WebSocket Client received message: "
operator|+
name|textFrame
operator|.
name|text
argument_list|()
argument_list|)
expr_stmt|;
name|ctx
operator|.
name|fireExceptionCaught
argument_list|(
operator|new
name|IOException
argument_list|(
literal|"Received invalid frame over WebSocket."
argument_list|)
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|frame
operator|instanceof
name|BinaryWebSocketFrame
condition|)
block|{
name|BinaryWebSocketFrame
name|binaryFrame
init|=
operator|(
name|BinaryWebSocketFrame
operator|)
name|frame
decl_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"WebSocket Client received data: {} bytes"
argument_list|,
name|binaryFrame
operator|.
name|content
argument_list|()
operator|.
name|readableBytes
argument_list|()
argument_list|)
expr_stmt|;
name|listener
operator|.
name|onData
argument_list|(
name|binaryFrame
operator|.
name|content
argument_list|()
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|frame
operator|instanceof
name|PongWebSocketFrame
condition|)
block|{
name|LOG
operator|.
name|trace
argument_list|(
literal|"WebSocket Client received pong"
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|frame
operator|instanceof
name|CloseWebSocketFrame
condition|)
block|{
name|LOG
operator|.
name|trace
argument_list|(
literal|"WebSocket Client received closing"
argument_list|)
expr_stmt|;
name|ch
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
block|}
block|}
end_class

end_unit
