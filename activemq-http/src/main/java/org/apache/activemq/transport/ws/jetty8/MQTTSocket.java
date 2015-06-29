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
name|jetty8
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
name|AbstractMQTTSocket
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
name|fusesource
operator|.
name|mqtt
operator|.
name|codec
operator|.
name|DISCONNECT
import|;
end_import

begin_import
import|import
name|org
operator|.
name|fusesource
operator|.
name|mqtt
operator|.
name|codec
operator|.
name|MQTTFrame
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
name|MQTTSocket
extends|extends
name|AbstractMQTTSocket
implements|implements
name|WebSocket
operator|.
name|OnBinaryMessage
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
name|MQTTSocket
operator|.
name|class
argument_list|)
decl_stmt|;
specifier|private
name|Connection
name|outbound
decl_stmt|;
specifier|public
name|MQTTSocket
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
name|sendToMQTT
parameter_list|(
name|MQTTFrame
name|command
parameter_list|)
throws|throws
name|IOException
block|{
name|ByteSequence
name|bytes
init|=
name|wireFormat
operator|.
name|marshal
argument_list|(
name|command
argument_list|)
decl_stmt|;
name|outbound
operator|.
name|sendMessage
argument_list|(
name|bytes
operator|.
name|getData
argument_list|()
argument_list|,
literal|0
argument_list|,
name|bytes
operator|.
name|getLength
argument_list|()
argument_list|)
expr_stmt|;
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
name|outbound
operator|!=
literal|null
operator|&&
name|outbound
operator|.
name|isOpen
argument_list|()
condition|)
block|{
name|outbound
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
comment|//----- WebSocket.OnTextMessage callback handlers ------------------------//
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
name|onMessage
parameter_list|(
name|byte
index|[]
name|bytes
parameter_list|,
name|int
name|offset
parameter_list|,
name|int
name|length
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
literal|"Waiting for MQTTSocket to be properly started..."
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
literal|"While waiting for MQTTSocket to be properly started, we got interrupted!! Should be okay, but you could see race conditions..."
argument_list|)
expr_stmt|;
block|}
block|}
name|receiveCounter
operator|+=
name|length
expr_stmt|;
try|try
block|{
name|MQTTFrame
name|frame
init|=
operator|(
name|MQTTFrame
operator|)
name|wireFormat
operator|.
name|unmarshal
argument_list|(
operator|new
name|ByteSequence
argument_list|(
name|bytes
argument_list|,
name|offset
argument_list|,
name|length
argument_list|)
argument_list|)
decl_stmt|;
name|getProtocolConverter
argument_list|()
operator|.
name|onMQTTCommand
argument_list|(
name|frame
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
name|onClose
parameter_list|(
name|int
name|closeCode
parameter_list|,
name|String
name|message
parameter_list|)
block|{
try|try
block|{
name|getProtocolConverter
argument_list|()
operator|.
name|onMQTTCommand
argument_list|(
operator|new
name|DISCONNECT
argument_list|()
operator|.
name|encode
argument_list|()
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
name|LOG
operator|.
name|warn
argument_list|(
literal|"Failed to close WebSocket"
argument_list|,
name|e
argument_list|)
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

