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
name|CountDownLatch
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
name|KeepAliveInfo
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
name|TransportSupport
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
name|ProtocolConverter
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
name|stomp
operator|.
name|StompInactivityMonitor
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
name|StompTransport
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
name|StompWireFormat
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
name|ByteSequence
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
comment|/**  * Base implementation of a STOMP based WebSocket handler.  */
end_comment

begin_class
specifier|public
specifier|abstract
class|class
name|AbstractStompSocket
extends|extends
name|TransportSupport
implements|implements
name|StompTransport
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
name|AbstractStompSocket
operator|.
name|class
argument_list|)
decl_stmt|;
specifier|protected
name|ProtocolConverter
name|protocolConverter
init|=
operator|new
name|ProtocolConverter
argument_list|(
name|this
argument_list|,
literal|null
argument_list|)
decl_stmt|;
specifier|protected
name|StompWireFormat
name|wireFormat
init|=
operator|new
name|StompWireFormat
argument_list|()
decl_stmt|;
specifier|protected
specifier|final
name|CountDownLatch
name|socketTransportStarted
init|=
operator|new
name|CountDownLatch
argument_list|(
literal|1
argument_list|)
decl_stmt|;
specifier|protected
specifier|final
name|StompInactivityMonitor
name|stompInactivityMonitor
init|=
operator|new
name|StompInactivityMonitor
argument_list|(
name|this
argument_list|,
name|wireFormat
argument_list|)
decl_stmt|;
specifier|protected
specifier|volatile
name|int
name|receiveCounter
decl_stmt|;
specifier|protected
specifier|final
name|String
name|remoteAddress
decl_stmt|;
specifier|public
name|AbstractStompSocket
parameter_list|(
name|String
name|remoteAddress
parameter_list|)
block|{
name|super
argument_list|()
expr_stmt|;
name|this
operator|.
name|remoteAddress
operator|=
name|remoteAddress
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
name|protocolConverter
operator|.
name|onActiveMQCommand
argument_list|(
operator|(
name|Command
operator|)
name|command
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
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
annotation|@
name|Override
specifier|public
name|void
name|sendToActiveMQ
parameter_list|(
name|Command
name|command
parameter_list|)
block|{
name|doConsume
argument_list|(
name|command
argument_list|)
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
name|stompInactivityMonitor
operator|.
name|stop
argument_list|()
expr_stmt|;
name|handleStopped
argument_list|()
expr_stmt|;
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
name|socketTransportStarted
operator|.
name|countDown
argument_list|()
expr_stmt|;
name|stompInactivityMonitor
operator|.
name|setTransportListener
argument_list|(
name|getTransportListener
argument_list|()
argument_list|)
expr_stmt|;
name|stompInactivityMonitor
operator|.
name|startConnectCheckTask
argument_list|()
expr_stmt|;
block|}
comment|//----- Abstract methods for subclasses to implement ---------------------//
annotation|@
name|Override
specifier|public
specifier|abstract
name|void
name|sendToStomp
parameter_list|(
name|StompFrame
name|command
parameter_list|)
throws|throws
name|IOException
function_decl|;
comment|/**      * Called when the transport is stopping to allow the dervied classes      * a chance to close WebSocket resources.      *      * @throws IOException if an error occurs during the stop.      */
specifier|public
specifier|abstract
name|void
name|handleStopped
parameter_list|()
throws|throws
name|IOException
function_decl|;
comment|//----- Accessor methods -------------------------------------------------//
annotation|@
name|Override
specifier|public
name|StompInactivityMonitor
name|getInactivityMonitor
parameter_list|()
block|{
return|return
name|stompInactivityMonitor
return|;
block|}
annotation|@
name|Override
specifier|public
name|StompWireFormat
name|getWireFormat
parameter_list|()
block|{
return|return
name|wireFormat
return|;
block|}
annotation|@
name|Override
specifier|public
name|String
name|getRemoteAddress
parameter_list|()
block|{
return|return
name|remoteAddress
return|;
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
comment|//----- Internal implementation ------------------------------------------//
specifier|protected
name|void
name|processStompFrame
parameter_list|(
name|String
name|data
parameter_list|)
block|{
if|if
condition|(
operator|!
name|transportStartedAtLeastOnce
argument_list|()
condition|)
block|{
name|LOG
operator|.
name|debug
argument_list|(
literal|"Waiting for StompSocket to be properly started..."
argument_list|)
expr_stmt|;
try|try
block|{
name|socketTransportStarted
operator|.
name|await
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|InterruptedException
name|e
parameter_list|)
block|{
name|LOG
operator|.
name|warn
argument_list|(
literal|"While waiting for StompSocket to be properly started, we got interrupted!! Should be okay, but you could see race conditions..."
argument_list|)
expr_stmt|;
block|}
block|}
try|try
block|{
if|if
condition|(
name|data
operator|!=
literal|null
condition|)
block|{
name|receiveCounter
operator|+=
name|data
operator|.
name|length
argument_list|()
expr_stmt|;
if|if
condition|(
name|data
operator|.
name|equals
argument_list|(
literal|"\n"
argument_list|)
condition|)
block|{
name|stompInactivityMonitor
operator|.
name|onCommand
argument_list|(
operator|new
name|KeepAliveInfo
argument_list|()
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|protocolConverter
operator|.
name|onStompCommand
argument_list|(
operator|(
name|StompFrame
operator|)
name|wireFormat
operator|.
name|unmarshal
argument_list|(
operator|new
name|ByteSequence
argument_list|(
name|data
operator|.
name|getBytes
argument_list|(
literal|"UTF-8"
argument_list|)
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
block|}
catch|catch
parameter_list|(
name|Exception
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
name|boolean
name|transportStartedAtLeastOnce
parameter_list|()
block|{
return|return
name|socketTransportStarted
operator|.
name|getCount
argument_list|()
operator|==
literal|0
return|;
block|}
block|}
end_class

end_unit

