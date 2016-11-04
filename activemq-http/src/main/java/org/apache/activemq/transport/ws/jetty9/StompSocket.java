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
name|ws
operator|.
name|jetty9
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
name|util
operator|.
name|concurrent
operator|.
name|TimeUnit
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
name|stomp
operator|.
name|Stomp
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
name|stomp
operator|.
name|StompFrame
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
name|ws
operator|.
name|AbstractStompSocket
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
name|eclipse
operator|.
name|jetty
operator|.
name|websocket
operator|.
name|api
operator|.
name|Session
import|;
end_import

begin_import
import|import
name|org
operator|.
name|eclipse
operator|.
name|jetty
operator|.
name|websocket
operator|.
name|api
operator|.
name|WebSocketListener
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
comment|/**  * Implements web socket and mediates between servlet and the broker  */
end_comment

begin_class
specifier|public
class|class
name|StompSocket
extends|extends
name|AbstractStompSocket
implements|implements
name|WebSocketListener
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
name|StompSocket
operator|.
name|class
argument_list|)
decl_stmt|;
specifier|private
specifier|final
name|int
name|ORDERLY_CLOSE_TIMEOUT
init|=
literal|10
decl_stmt|;
specifier|private
name|Session
name|session
decl_stmt|;
specifier|public
name|StompSocket
parameter_list|(
name|String
name|remoteAddress
parameter_list|)
block|{
name|super
argument_list|(
name|remoteAddress
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|sendToStomp
parameter_list|(
name|StompFrame
name|command
parameter_list|)
throws|throws
name|IOException
block|{
try|try
block|{
comment|//timeout after a period of time so we don't wait forever and hold the protocol lock
name|session
operator|.
name|getRemote
argument_list|()
operator|.
name|sendStringByFuture
argument_list|(
name|command
operator|.
name|format
argument_list|()
argument_list|)
operator|.
name|get
argument_list|(
name|getDefaultSendTimeOut
argument_list|()
argument_list|,
name|TimeUnit
operator|.
name|SECONDS
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
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
block|}
annotation|@
name|Override
specifier|public
name|void
name|handleStopped
parameter_list|()
throws|throws
name|IOException
block|{
if|if
condition|(
name|session
operator|!=
literal|null
operator|&&
name|session
operator|.
name|isOpen
argument_list|()
condition|)
block|{
name|session
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
comment|//----- WebSocketListener event callbacks --------------------------------//
annotation|@
name|Override
specifier|public
name|void
name|onWebSocketBinary
parameter_list|(
name|byte
index|[]
name|arg0
parameter_list|,
name|int
name|arg1
parameter_list|,
name|int
name|arg2
parameter_list|)
block|{     }
annotation|@
name|Override
specifier|public
name|void
name|onWebSocketClose
parameter_list|(
name|int
name|arg0
parameter_list|,
name|String
name|arg1
parameter_list|)
block|{
try|try
block|{
if|if
condition|(
name|protocolLock
operator|.
name|tryLock
argument_list|()
operator|||
name|protocolLock
operator|.
name|tryLock
argument_list|(
name|ORDERLY_CLOSE_TIMEOUT
argument_list|,
name|TimeUnit
operator|.
name|SECONDS
argument_list|)
condition|)
block|{
name|LOG
operator|.
name|debug
argument_list|(
literal|"Stomp WebSocket closed: code[{}] message[{}]"
argument_list|,
name|arg0
argument_list|,
name|arg1
argument_list|)
expr_stmt|;
name|protocolConverter
operator|.
name|onStompCommand
argument_list|(
operator|new
name|StompFrame
argument_list|(
name|Stomp
operator|.
name|Commands
operator|.
name|DISCONNECT
argument_list|)
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
name|LOG
operator|.
name|debug
argument_list|(
literal|"Failed to close STOMP WebSocket cleanly"
argument_list|,
name|e
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
if|if
condition|(
name|protocolLock
operator|.
name|isHeldByCurrentThread
argument_list|()
condition|)
block|{
name|protocolLock
operator|.
name|unlock
argument_list|()
expr_stmt|;
block|}
block|}
block|}
annotation|@
name|Override
specifier|public
name|void
name|onWebSocketConnect
parameter_list|(
name|Session
name|session
parameter_list|)
block|{
name|this
operator|.
name|session
operator|=
name|session
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|onWebSocketError
parameter_list|(
name|Throwable
name|arg0
parameter_list|)
block|{     }
annotation|@
name|Override
specifier|public
name|void
name|onWebSocketText
parameter_list|(
name|String
name|data
parameter_list|)
block|{
name|processStompFrame
argument_list|(
name|data
argument_list|)
expr_stmt|;
block|}
specifier|private
specifier|static
name|int
name|getDefaultSendTimeOut
parameter_list|()
block|{
return|return
name|Integer
operator|.
name|getInteger
argument_list|(
literal|"org.apache.activemq.transport.ws.StompSocket.sendTimeout"
argument_list|,
literal|30
argument_list|)
return|;
block|}
block|}
end_class

end_unit

