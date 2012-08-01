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
name|security
operator|.
name|cert
operator|.
name|X509Certificate
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
name|eclipse
operator|.
name|jetty
operator|.
name|websocket
operator|.
name|WebSocket
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
class|class
name|StompSocket
extends|extends
name|TransportSupport
implements|implements
name|WebSocket
operator|.
name|OnTextMessage
implements|,
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
name|StompSocket
operator|.
name|class
argument_list|)
decl_stmt|;
name|Connection
name|outbound
decl_stmt|;
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
name|StompWireFormat
name|wireFormat
init|=
operator|new
name|StompWireFormat
argument_list|()
decl_stmt|;
specifier|private
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
annotation|@
name|Override
specifier|public
name|void
name|onOpen
parameter_list|(
name|Connection
name|connection
parameter_list|)
block|{
name|this
operator|.
name|outbound
operator|=
name|connection
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|onClose
parameter_list|(
name|int
name|closeCode
parameter_list|,
name|String
name|message
parameter_list|)
block|{     }
annotation|@
name|Override
specifier|public
name|void
name|onMessage
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
block|{     }
annotation|@
name|Override
specifier|public
name|int
name|getReceiveCounter
parameter_list|()
block|{
return|return
literal|0
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
literal|"StompSocket_"
operator|+
name|this
operator|.
name|hashCode
argument_list|()
return|;
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
name|X509Certificate
index|[]
name|getPeerCertificates
parameter_list|()
block|{
return|return
literal|null
return|;
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
name|outbound
operator|.
name|sendMessage
argument_list|(
name|command
operator|.
name|format
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|StompInactivityMonitor
name|getInactivityMonitor
parameter_list|()
block|{
return|return
literal|null
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
name|this
operator|.
name|wireFormat
return|;
block|}
block|}
end_class

end_unit

