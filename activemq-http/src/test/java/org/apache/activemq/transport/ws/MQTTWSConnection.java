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
name|nio
operator|.
name|ByteBuffer
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|UUID
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
name|BlockingQueue
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
name|java
operator|.
name|util
operator|.
name|concurrent
operator|.
name|LinkedBlockingDeque
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
name|mqtt
operator|.
name|MQTTWireFormat
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
name|WebSocketAdapter
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
name|fusesource
operator|.
name|hawtbuf
operator|.
name|UTF8Buffer
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
name|CONNACK
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
name|CONNECT
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
name|fusesource
operator|.
name|mqtt
operator|.
name|codec
operator|.
name|PINGREQ
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
name|PINGRESP
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
name|PUBACK
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
name|PUBCOMP
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
name|PUBLISH
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
name|PUBREC
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
name|PUBREL
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
name|SUBACK
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
comment|/**  * Implements a simple WebSocket based MQTT Client that can be used for unit testing.  */
end_comment

begin_class
specifier|public
class|class
name|MQTTWSConnection
extends|extends
name|WebSocketAdapter
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
name|MQTTWSConnection
operator|.
name|class
argument_list|)
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|MQTTFrame
name|PING_RESP_FRAME
init|=
operator|new
name|PINGRESP
argument_list|()
operator|.
name|encode
argument_list|()
decl_stmt|;
specifier|private
name|Session
name|connection
decl_stmt|;
specifier|private
specifier|final
name|CountDownLatch
name|connectLatch
init|=
operator|new
name|CountDownLatch
argument_list|(
literal|1
argument_list|)
decl_stmt|;
specifier|private
specifier|final
name|MQTTWireFormat
name|wireFormat
init|=
operator|new
name|MQTTWireFormat
argument_list|()
decl_stmt|;
specifier|private
specifier|final
name|BlockingQueue
argument_list|<
name|MQTTFrame
argument_list|>
name|prefetch
init|=
operator|new
name|LinkedBlockingDeque
argument_list|<
name|MQTTFrame
argument_list|>
argument_list|()
decl_stmt|;
specifier|private
name|int
name|closeCode
init|=
operator|-
literal|1
decl_stmt|;
specifier|private
name|String
name|closeMessage
decl_stmt|;
annotation|@
name|Override
specifier|public
name|boolean
name|isConnected
parameter_list|()
block|{
return|return
name|connection
operator|!=
literal|null
condition|?
name|connection
operator|.
name|isOpen
argument_list|()
else|:
literal|false
return|;
block|}
specifier|public
name|void
name|close
parameter_list|()
block|{
if|if
condition|(
name|connection
operator|!=
literal|null
condition|)
block|{
name|connection
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
specifier|protected
name|Session
name|getConnection
parameter_list|()
block|{
return|return
name|connection
return|;
block|}
comment|//----- Connection and Disconnection methods -----------------------------//
specifier|public
name|void
name|connect
parameter_list|()
throws|throws
name|Exception
block|{
name|connect
argument_list|(
name|UUID
operator|.
name|randomUUID
argument_list|()
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|connect
parameter_list|(
name|String
name|clientId
parameter_list|)
throws|throws
name|Exception
block|{
name|checkConnected
argument_list|()
expr_stmt|;
name|CONNECT
name|command
init|=
operator|new
name|CONNECT
argument_list|()
decl_stmt|;
name|command
operator|.
name|clientId
argument_list|(
operator|new
name|UTF8Buffer
argument_list|(
name|clientId
argument_list|)
argument_list|)
expr_stmt|;
name|command
operator|.
name|cleanSession
argument_list|(
literal|false
argument_list|)
expr_stmt|;
name|command
operator|.
name|version
argument_list|(
literal|3
argument_list|)
expr_stmt|;
name|command
operator|.
name|keepAlive
argument_list|(
operator|(
name|short
operator|)
literal|0
argument_list|)
expr_stmt|;
name|ByteSequence
name|payload
init|=
name|wireFormat
operator|.
name|marshal
argument_list|(
name|command
operator|.
name|encode
argument_list|()
argument_list|)
decl_stmt|;
name|connection
operator|.
name|getRemote
argument_list|()
operator|.
name|sendBytes
argument_list|(
name|ByteBuffer
operator|.
name|wrap
argument_list|(
name|payload
operator|.
name|data
argument_list|)
argument_list|)
expr_stmt|;
name|MQTTFrame
name|incoming
init|=
name|receive
argument_list|(
literal|15
argument_list|,
name|TimeUnit
operator|.
name|SECONDS
argument_list|)
decl_stmt|;
if|if
condition|(
name|incoming
operator|==
literal|null
operator|||
name|incoming
operator|.
name|messageType
argument_list|()
operator|!=
name|CONNACK
operator|.
name|TYPE
condition|)
block|{
throw|throw
operator|new
name|IOException
argument_list|(
literal|"Failed to connect to remote service."
argument_list|)
throw|;
block|}
else|else
block|{
name|CONNACK
name|connack
init|=
operator|new
name|CONNACK
argument_list|()
operator|.
name|decode
argument_list|(
name|incoming
argument_list|)
decl_stmt|;
if|if
condition|(
operator|!
name|connack
operator|.
name|code
argument_list|()
operator|.
name|equals
argument_list|(
name|CONNACK
operator|.
name|Code
operator|.
name|CONNECTION_ACCEPTED
argument_list|)
condition|)
block|{
throw|throw
operator|new
name|IOException
argument_list|(
literal|"Failed to connect to remote service: "
operator|+
name|connack
operator|.
name|code
argument_list|()
argument_list|)
throw|;
block|}
block|}
block|}
specifier|public
name|void
name|disconnect
parameter_list|()
throws|throws
name|Exception
block|{
if|if
condition|(
operator|!
name|isConnected
argument_list|()
condition|)
block|{
return|return;
block|}
name|DISCONNECT
name|command
init|=
operator|new
name|DISCONNECT
argument_list|()
decl_stmt|;
name|ByteSequence
name|payload
init|=
name|wireFormat
operator|.
name|marshal
argument_list|(
name|command
operator|.
name|encode
argument_list|()
argument_list|)
decl_stmt|;
name|connection
operator|.
name|getRemote
argument_list|()
operator|.
name|sendBytes
argument_list|(
name|ByteBuffer
operator|.
name|wrap
argument_list|(
name|payload
operator|.
name|data
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|//---- Send methods ------------------------------------------------------//
specifier|public
name|void
name|sendFrame
parameter_list|(
name|MQTTFrame
name|frame
parameter_list|)
throws|throws
name|Exception
block|{
name|checkConnected
argument_list|()
expr_stmt|;
name|ByteSequence
name|payload
init|=
name|wireFormat
operator|.
name|marshal
argument_list|(
name|frame
argument_list|)
decl_stmt|;
name|connection
operator|.
name|getRemote
argument_list|()
operator|.
name|sendBytes
argument_list|(
name|ByteBuffer
operator|.
name|wrap
argument_list|(
name|payload
operator|.
name|data
argument_list|)
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|keepAlive
parameter_list|()
throws|throws
name|Exception
block|{
name|checkConnected
argument_list|()
expr_stmt|;
name|ByteSequence
name|payload
init|=
name|wireFormat
operator|.
name|marshal
argument_list|(
operator|new
name|PINGREQ
argument_list|()
operator|.
name|encode
argument_list|()
argument_list|)
decl_stmt|;
name|connection
operator|.
name|getRemote
argument_list|()
operator|.
name|sendBytes
argument_list|(
name|ByteBuffer
operator|.
name|wrap
argument_list|(
name|payload
operator|.
name|data
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|//----- Receive methods --------------------------------------------------//
specifier|public
name|MQTTFrame
name|receive
parameter_list|()
throws|throws
name|Exception
block|{
name|checkConnected
argument_list|()
expr_stmt|;
return|return
name|prefetch
operator|.
name|take
argument_list|()
return|;
block|}
specifier|public
name|MQTTFrame
name|receive
parameter_list|(
name|long
name|timeout
parameter_list|,
name|TimeUnit
name|unit
parameter_list|)
throws|throws
name|Exception
block|{
name|checkConnected
argument_list|()
expr_stmt|;
return|return
name|prefetch
operator|.
name|poll
argument_list|(
name|timeout
argument_list|,
name|unit
argument_list|)
return|;
block|}
specifier|public
name|MQTTFrame
name|receiveNoWait
parameter_list|()
throws|throws
name|Exception
block|{
name|checkConnected
argument_list|()
expr_stmt|;
return|return
name|prefetch
operator|.
name|poll
argument_list|()
return|;
block|}
comment|//---- Blocking state change calls ---------------------------------------//
specifier|public
name|void
name|awaitConnection
parameter_list|()
throws|throws
name|InterruptedException
block|{
name|connectLatch
operator|.
name|await
argument_list|()
expr_stmt|;
block|}
specifier|public
name|boolean
name|awaitConnection
parameter_list|(
name|long
name|time
parameter_list|,
name|TimeUnit
name|unit
parameter_list|)
throws|throws
name|InterruptedException
block|{
return|return
name|connectLatch
operator|.
name|await
argument_list|(
name|time
argument_list|,
name|unit
argument_list|)
return|;
block|}
comment|//----- Property Accessors -----------------------------------------------//
specifier|public
name|int
name|getCloseCode
parameter_list|()
block|{
return|return
name|closeCode
return|;
block|}
specifier|public
name|String
name|getCloseMessage
parameter_list|()
block|{
return|return
name|closeMessage
return|;
block|}
comment|//----- WebSocket callback handlers --------------------------------------//
annotation|@
name|Override
specifier|public
name|void
name|onWebSocketBinary
parameter_list|(
name|byte
index|[]
name|data
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
name|data
operator|==
literal|null
operator|||
name|length
operator|<=
literal|0
condition|)
block|{
return|return;
block|}
name|MQTTFrame
name|frame
init|=
literal|null
decl_stmt|;
try|try
block|{
name|frame
operator|=
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
name|data
argument_list|,
name|offset
argument_list|,
name|length
argument_list|)
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
name|LOG
operator|.
name|error
argument_list|(
literal|"Could not decode incoming MQTT Frame: "
argument_list|,
name|e
operator|.
name|getMessage
argument_list|()
argument_list|)
expr_stmt|;
name|connection
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
try|try
block|{
switch|switch
condition|(
name|frame
operator|.
name|messageType
argument_list|()
condition|)
block|{
case|case
name|PINGREQ
operator|.
name|TYPE
case|:
name|PINGREQ
name|ping
init|=
operator|new
name|PINGREQ
argument_list|()
operator|.
name|decode
argument_list|(
name|frame
argument_list|)
decl_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"WS-Client read frame: {}"
argument_list|,
name|ping
argument_list|)
expr_stmt|;
name|sendFrame
argument_list|(
name|PING_RESP_FRAME
argument_list|)
expr_stmt|;
break|break;
case|case
name|PINGRESP
operator|.
name|TYPE
case|:
name|LOG
operator|.
name|info
argument_list|(
literal|"WS-Client ping response received."
argument_list|)
expr_stmt|;
break|break;
case|case
name|CONNACK
operator|.
name|TYPE
case|:
name|CONNACK
name|connAck
init|=
operator|new
name|CONNACK
argument_list|()
operator|.
name|decode
argument_list|(
name|frame
argument_list|)
decl_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"WS-Client read frame: {}"
argument_list|,
name|connAck
argument_list|)
expr_stmt|;
name|prefetch
operator|.
name|put
argument_list|(
name|frame
argument_list|)
expr_stmt|;
break|break;
case|case
name|SUBACK
operator|.
name|TYPE
case|:
name|SUBACK
name|subAck
init|=
operator|new
name|SUBACK
argument_list|()
operator|.
name|decode
argument_list|(
name|frame
argument_list|)
decl_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"WS-Client read frame: {}"
argument_list|,
name|subAck
argument_list|)
expr_stmt|;
name|prefetch
operator|.
name|put
argument_list|(
name|frame
argument_list|)
expr_stmt|;
break|break;
case|case
name|PUBLISH
operator|.
name|TYPE
case|:
name|PUBLISH
name|publish
init|=
operator|new
name|PUBLISH
argument_list|()
operator|.
name|decode
argument_list|(
name|frame
argument_list|)
decl_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"WS-Client read frame: {}"
argument_list|,
name|publish
argument_list|)
expr_stmt|;
name|prefetch
operator|.
name|put
argument_list|(
name|frame
argument_list|)
expr_stmt|;
break|break;
case|case
name|PUBACK
operator|.
name|TYPE
case|:
name|PUBACK
name|pubAck
init|=
operator|new
name|PUBACK
argument_list|()
operator|.
name|decode
argument_list|(
name|frame
argument_list|)
decl_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"WS-Client read frame: {}"
argument_list|,
name|pubAck
argument_list|)
expr_stmt|;
name|prefetch
operator|.
name|put
argument_list|(
name|frame
argument_list|)
expr_stmt|;
break|break;
case|case
name|PUBREC
operator|.
name|TYPE
case|:
name|PUBREC
name|pubRec
init|=
operator|new
name|PUBREC
argument_list|()
operator|.
name|decode
argument_list|(
name|frame
argument_list|)
decl_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"WS-Client read frame: {}"
argument_list|,
name|pubRec
argument_list|)
expr_stmt|;
name|prefetch
operator|.
name|put
argument_list|(
name|frame
argument_list|)
expr_stmt|;
break|break;
case|case
name|PUBREL
operator|.
name|TYPE
case|:
name|PUBREL
name|pubRel
init|=
operator|new
name|PUBREL
argument_list|()
operator|.
name|decode
argument_list|(
name|frame
argument_list|)
decl_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"WS-Client read frame: {}"
argument_list|,
name|pubRel
argument_list|)
expr_stmt|;
name|prefetch
operator|.
name|put
argument_list|(
name|frame
argument_list|)
expr_stmt|;
break|break;
case|case
name|PUBCOMP
operator|.
name|TYPE
case|:
name|PUBCOMP
name|pubComp
init|=
operator|new
name|PUBCOMP
argument_list|()
operator|.
name|decode
argument_list|(
name|frame
argument_list|)
decl_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"WS-Client read frame: {}"
argument_list|,
name|pubComp
argument_list|)
expr_stmt|;
name|prefetch
operator|.
name|put
argument_list|(
name|frame
argument_list|)
expr_stmt|;
break|break;
default|default:
name|LOG
operator|.
name|error
argument_list|(
literal|"Unknown MQTT  Frame received."
argument_list|)
expr_stmt|;
name|connection
operator|.
name|close
argument_list|()
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
name|error
argument_list|(
literal|"Could not decode incoming MQTT Frame: "
argument_list|,
name|e
operator|.
name|getMessage
argument_list|()
argument_list|)
expr_stmt|;
name|connection
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
comment|//----- Internal implementation ------------------------------------------//
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
name|isConnected
argument_list|()
condition|)
block|{
throw|throw
operator|new
name|IOException
argument_list|(
literal|"MQTT WS Connection is closed."
argument_list|)
throw|;
block|}
block|}
comment|/* (non-Javadoc)      * @see org.eclipse.jetty.websocket.api.WebSocketListener#onWebSocketClose(int, java.lang.String)      */
annotation|@
name|Override
specifier|public
name|void
name|onWebSocketClose
parameter_list|(
name|int
name|statusCode
parameter_list|,
name|String
name|reason
parameter_list|)
block|{
name|LOG
operator|.
name|trace
argument_list|(
literal|"MQTT WS Connection closed, code:{} message:{}"
argument_list|,
name|statusCode
argument_list|,
name|reason
argument_list|)
expr_stmt|;
name|this
operator|.
name|connection
operator|=
literal|null
expr_stmt|;
name|this
operator|.
name|closeCode
operator|=
name|statusCode
expr_stmt|;
name|this
operator|.
name|closeMessage
operator|=
name|reason
expr_stmt|;
block|}
comment|/* (non-Javadoc)      * @see org.eclipse.jetty.websocket.api.WebSocketListener#onWebSocketConnect(org.eclipse.jetty.websocket.api.Session)      */
annotation|@
name|Override
specifier|public
name|void
name|onWebSocketConnect
parameter_list|(
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
name|session
parameter_list|)
block|{
name|this
operator|.
name|connection
operator|=
name|session
expr_stmt|;
name|this
operator|.
name|connectLatch
operator|.
name|countDown
argument_list|()
expr_stmt|;
block|}
block|}
end_class

end_unit

