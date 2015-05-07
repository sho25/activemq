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
name|stomp
operator|.
name|StompFrame
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
comment|/**  * STOMP over WS based Connection class  */
end_comment

begin_class
specifier|public
class|class
name|StompWSConnection
implements|implements
name|WebSocket
implements|,
name|WebSocket
operator|.
name|OnTextMessage
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
name|StompWSConnection
operator|.
name|class
argument_list|)
decl_stmt|;
specifier|private
name|Connection
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
name|BlockingQueue
argument_list|<
name|String
argument_list|>
name|prefetch
init|=
operator|new
name|LinkedBlockingDeque
argument_list|<
name|String
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
comment|//---- Send methods ------------------------------------------------------//
specifier|public
name|void
name|sendRawFrame
parameter_list|(
name|String
name|rawFrame
parameter_list|)
throws|throws
name|Exception
block|{
name|checkConnected
argument_list|()
expr_stmt|;
name|connection
operator|.
name|sendMessage
argument_list|(
name|rawFrame
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|sendFrame
parameter_list|(
name|StompFrame
name|frame
parameter_list|)
throws|throws
name|Exception
block|{
name|checkConnected
argument_list|()
expr_stmt|;
name|connection
operator|.
name|sendMessage
argument_list|(
name|frame
operator|.
name|format
argument_list|()
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
name|connection
operator|.
name|sendMessage
argument_list|(
literal|"\n"
argument_list|)
expr_stmt|;
block|}
comment|//----- Receive methods --------------------------------------------------//
specifier|public
name|String
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
name|String
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
name|String
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
name|onMessage
parameter_list|(
name|String
name|data
parameter_list|)
block|{
if|if
condition|(
name|data
operator|==
literal|null
condition|)
block|{
return|return;
block|}
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
name|LOG
operator|.
name|debug
argument_list|(
literal|"New incoming heartbeat read"
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|LOG
operator|.
name|trace
argument_list|(
literal|"New incoming STOMP Frame read: \n{}"
argument_list|,
name|data
argument_list|)
expr_stmt|;
name|prefetch
operator|.
name|add
argument_list|(
name|data
argument_list|)
expr_stmt|;
block|}
block|}
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
name|connection
operator|=
name|connection
expr_stmt|;
name|this
operator|.
name|connectLatch
operator|.
name|countDown
argument_list|()
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
block|{
name|LOG
operator|.
name|trace
argument_list|(
literal|"STOMP WS Connection closed, code:{} message:{}"
argument_list|,
name|closeCode
argument_list|,
name|message
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
name|closeCode
expr_stmt|;
name|this
operator|.
name|closeMessage
operator|=
name|message
expr_stmt|;
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
literal|"STOMP WS Connection is closed."
argument_list|)
throw|;
block|}
block|}
block|}
end_class

end_unit
