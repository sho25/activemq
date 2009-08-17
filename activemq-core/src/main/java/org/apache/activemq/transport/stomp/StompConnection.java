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
name|stomp
package|;
end_package

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|ByteArrayOutputStream
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
name|IOException
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|InputStream
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|OutputStream
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
name|UnknownHostException
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|HashMap
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|regex
operator|.
name|Matcher
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|regex
operator|.
name|Pattern
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
operator|.
name|Headers
operator|.
name|Subscribe
import|;
end_import

begin_class
specifier|public
class|class
name|StompConnection
block|{
specifier|public
specifier|static
specifier|final
name|long
name|RECEIVE_TIMEOUT
init|=
literal|10000
decl_stmt|;
specifier|private
name|Socket
name|stompSocket
decl_stmt|;
specifier|private
name|ByteArrayOutputStream
name|inputBuffer
init|=
operator|new
name|ByteArrayOutputStream
argument_list|()
decl_stmt|;
specifier|public
name|void
name|open
parameter_list|(
name|String
name|host
parameter_list|,
name|int
name|port
parameter_list|)
throws|throws
name|IOException
throws|,
name|UnknownHostException
block|{
name|open
argument_list|(
operator|new
name|Socket
argument_list|(
name|host
argument_list|,
name|port
argument_list|)
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|open
parameter_list|(
name|Socket
name|socket
parameter_list|)
block|{
name|stompSocket
operator|=
name|socket
expr_stmt|;
block|}
specifier|public
name|void
name|close
parameter_list|()
throws|throws
name|IOException
block|{
if|if
condition|(
name|stompSocket
operator|!=
literal|null
condition|)
block|{
name|stompSocket
operator|.
name|close
argument_list|()
expr_stmt|;
name|stompSocket
operator|=
literal|null
expr_stmt|;
block|}
block|}
specifier|public
name|void
name|sendFrame
parameter_list|(
name|String
name|data
parameter_list|)
throws|throws
name|Exception
block|{
name|byte
index|[]
name|bytes
init|=
name|data
operator|.
name|getBytes
argument_list|(
literal|"UTF-8"
argument_list|)
decl_stmt|;
name|OutputStream
name|outputStream
init|=
name|stompSocket
operator|.
name|getOutputStream
argument_list|()
decl_stmt|;
name|outputStream
operator|.
name|write
argument_list|(
name|bytes
argument_list|)
expr_stmt|;
name|outputStream
operator|.
name|write
argument_list|(
literal|0
argument_list|)
expr_stmt|;
name|outputStream
operator|.
name|flush
argument_list|()
expr_stmt|;
block|}
specifier|public
name|StompFrame
name|receive
parameter_list|()
throws|throws
name|Exception
block|{
return|return
name|receive
argument_list|(
name|RECEIVE_TIMEOUT
argument_list|)
return|;
block|}
specifier|public
name|StompFrame
name|receive
parameter_list|(
name|long
name|timeOut
parameter_list|)
throws|throws
name|Exception
block|{
name|stompSocket
operator|.
name|setSoTimeout
argument_list|(
operator|(
name|int
operator|)
name|timeOut
argument_list|)
expr_stmt|;
name|InputStream
name|is
init|=
name|stompSocket
operator|.
name|getInputStream
argument_list|()
decl_stmt|;
name|StompWireFormat
name|wf
init|=
operator|new
name|StompWireFormat
argument_list|()
decl_stmt|;
name|DataInputStream
name|dis
init|=
operator|new
name|DataInputStream
argument_list|(
name|is
argument_list|)
decl_stmt|;
return|return
operator|(
name|StompFrame
operator|)
name|wf
operator|.
name|unmarshal
argument_list|(
name|dis
argument_list|)
return|;
block|}
specifier|public
name|String
name|receiveFrame
parameter_list|()
throws|throws
name|Exception
block|{
return|return
name|receiveFrame
argument_list|(
name|RECEIVE_TIMEOUT
argument_list|)
return|;
block|}
specifier|public
name|String
name|receiveFrame
parameter_list|(
name|long
name|timeOut
parameter_list|)
throws|throws
name|Exception
block|{
name|stompSocket
operator|.
name|setSoTimeout
argument_list|(
operator|(
name|int
operator|)
name|timeOut
argument_list|)
expr_stmt|;
name|InputStream
name|is
init|=
name|stompSocket
operator|.
name|getInputStream
argument_list|()
decl_stmt|;
name|int
name|c
init|=
literal|0
decl_stmt|;
for|for
control|(
init|;
condition|;
control|)
block|{
name|c
operator|=
name|is
operator|.
name|read
argument_list|()
expr_stmt|;
if|if
condition|(
name|c
operator|<
literal|0
condition|)
block|{
throw|throw
operator|new
name|IOException
argument_list|(
literal|"socket closed."
argument_list|)
throw|;
block|}
elseif|else
if|if
condition|(
name|c
operator|==
literal|0
condition|)
block|{
name|c
operator|=
name|is
operator|.
name|read
argument_list|()
expr_stmt|;
if|if
condition|(
name|c
operator|!=
literal|'\n'
condition|)
block|{
throw|throw
operator|new
name|IOException
argument_list|(
literal|"Expecting stomp frame to terminate with \0\n"
argument_list|)
throw|;
block|}
name|byte
index|[]
name|ba
init|=
name|inputBuffer
operator|.
name|toByteArray
argument_list|()
decl_stmt|;
name|inputBuffer
operator|.
name|reset
argument_list|()
expr_stmt|;
return|return
operator|new
name|String
argument_list|(
name|ba
argument_list|,
literal|"UTF-8"
argument_list|)
return|;
block|}
else|else
block|{
name|inputBuffer
operator|.
name|write
argument_list|(
name|c
argument_list|)
expr_stmt|;
block|}
block|}
block|}
specifier|public
name|Socket
name|getStompSocket
parameter_list|()
block|{
return|return
name|stompSocket
return|;
block|}
specifier|public
name|void
name|setStompSocket
parameter_list|(
name|Socket
name|stompSocket
parameter_list|)
block|{
name|this
operator|.
name|stompSocket
operator|=
name|stompSocket
expr_stmt|;
block|}
specifier|public
name|void
name|connect
parameter_list|(
name|String
name|username
parameter_list|,
name|String
name|password
parameter_list|)
throws|throws
name|Exception
block|{
name|connect
argument_list|(
name|username
argument_list|,
name|password
argument_list|,
literal|null
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|connect
parameter_list|(
name|String
name|username
parameter_list|,
name|String
name|password
parameter_list|,
name|String
name|client
parameter_list|)
throws|throws
name|Exception
block|{
name|HashMap
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|headers
init|=
operator|new
name|HashMap
argument_list|()
decl_stmt|;
name|headers
operator|.
name|put
argument_list|(
literal|"login"
argument_list|,
name|username
argument_list|)
expr_stmt|;
name|headers
operator|.
name|put
argument_list|(
literal|"passcode"
argument_list|,
name|password
argument_list|)
expr_stmt|;
if|if
condition|(
name|client
operator|!=
literal|null
condition|)
block|{
name|headers
operator|.
name|put
argument_list|(
literal|"client-id"
argument_list|,
name|client
argument_list|)
expr_stmt|;
block|}
name|StompFrame
name|frame
init|=
operator|new
name|StompFrame
argument_list|(
literal|"CONNECT"
argument_list|,
name|headers
argument_list|)
decl_stmt|;
name|sendFrame
argument_list|(
name|frame
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|StompFrame
name|connect
init|=
name|receive
argument_list|()
decl_stmt|;
if|if
condition|(
operator|!
name|connect
operator|.
name|getAction
argument_list|()
operator|.
name|equals
argument_list|(
name|Stomp
operator|.
name|Responses
operator|.
name|CONNECTED
argument_list|)
condition|)
block|{
throw|throw
operator|new
name|Exception
argument_list|(
literal|"Not connected: "
operator|+
name|connect
operator|.
name|getBody
argument_list|()
argument_list|)
throw|;
block|}
block|}
specifier|public
name|void
name|disconnect
parameter_list|()
throws|throws
name|Exception
block|{
name|StompFrame
name|frame
init|=
operator|new
name|StompFrame
argument_list|(
literal|"DISCONNECT"
argument_list|)
decl_stmt|;
name|sendFrame
argument_list|(
name|frame
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|send
parameter_list|(
name|String
name|destination
parameter_list|,
name|String
name|message
parameter_list|)
throws|throws
name|Exception
block|{
name|send
argument_list|(
name|destination
argument_list|,
name|message
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|send
parameter_list|(
name|String
name|destination
parameter_list|,
name|String
name|message
parameter_list|,
name|String
name|transaction
parameter_list|,
name|HashMap
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|headers
parameter_list|)
throws|throws
name|Exception
block|{
if|if
condition|(
name|headers
operator|==
literal|null
condition|)
block|{
name|headers
operator|=
operator|new
name|HashMap
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
argument_list|()
expr_stmt|;
block|}
name|headers
operator|.
name|put
argument_list|(
literal|"destination"
argument_list|,
name|destination
argument_list|)
expr_stmt|;
if|if
condition|(
name|transaction
operator|!=
literal|null
condition|)
block|{
name|headers
operator|.
name|put
argument_list|(
literal|"transaction"
argument_list|,
name|transaction
argument_list|)
expr_stmt|;
block|}
name|StompFrame
name|frame
init|=
operator|new
name|StompFrame
argument_list|(
literal|"SEND"
argument_list|,
name|headers
argument_list|,
name|message
operator|.
name|getBytes
argument_list|()
argument_list|)
decl_stmt|;
name|sendFrame
argument_list|(
name|frame
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|subscribe
parameter_list|(
name|String
name|destination
parameter_list|)
throws|throws
name|Exception
block|{
name|subscribe
argument_list|(
name|destination
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|subscribe
parameter_list|(
name|String
name|destination
parameter_list|,
name|String
name|ack
parameter_list|)
throws|throws
name|Exception
block|{
name|subscribe
argument_list|(
name|destination
argument_list|,
name|ack
argument_list|,
operator|new
name|HashMap
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
argument_list|()
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|subscribe
parameter_list|(
name|String
name|destination
parameter_list|,
name|String
name|ack
parameter_list|,
name|HashMap
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|headers
parameter_list|)
throws|throws
name|Exception
block|{
if|if
condition|(
name|headers
operator|==
literal|null
condition|)
block|{
name|headers
operator|=
operator|new
name|HashMap
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
argument_list|()
expr_stmt|;
block|}
name|headers
operator|.
name|put
argument_list|(
literal|"destination"
argument_list|,
name|destination
argument_list|)
expr_stmt|;
if|if
condition|(
name|ack
operator|!=
literal|null
condition|)
block|{
name|headers
operator|.
name|put
argument_list|(
literal|"ack"
argument_list|,
name|ack
argument_list|)
expr_stmt|;
block|}
name|StompFrame
name|frame
init|=
operator|new
name|StompFrame
argument_list|(
literal|"SUBSCRIBE"
argument_list|,
name|headers
argument_list|)
decl_stmt|;
name|sendFrame
argument_list|(
name|frame
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|unsubscribe
parameter_list|(
name|String
name|destination
parameter_list|)
throws|throws
name|Exception
block|{
name|unsubscribe
argument_list|(
name|destination
argument_list|,
literal|null
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|unsubscribe
parameter_list|(
name|String
name|destination
parameter_list|,
name|HashMap
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|headers
parameter_list|)
throws|throws
name|Exception
block|{
if|if
condition|(
name|headers
operator|==
literal|null
condition|)
block|{
name|headers
operator|=
operator|new
name|HashMap
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
argument_list|()
expr_stmt|;
block|}
name|headers
operator|.
name|put
argument_list|(
literal|"destination"
argument_list|,
name|destination
argument_list|)
expr_stmt|;
name|StompFrame
name|frame
init|=
operator|new
name|StompFrame
argument_list|(
literal|"UNSUBSCRIBE"
argument_list|,
name|headers
argument_list|)
decl_stmt|;
name|sendFrame
argument_list|(
name|frame
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|begin
parameter_list|(
name|String
name|transaction
parameter_list|)
throws|throws
name|Exception
block|{
name|HashMap
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|headers
init|=
operator|new
name|HashMap
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
argument_list|()
decl_stmt|;
name|headers
operator|.
name|put
argument_list|(
literal|"transaction"
argument_list|,
name|transaction
argument_list|)
expr_stmt|;
name|StompFrame
name|frame
init|=
operator|new
name|StompFrame
argument_list|(
literal|"BEGIN"
argument_list|,
name|headers
argument_list|)
decl_stmt|;
name|sendFrame
argument_list|(
name|frame
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|abort
parameter_list|(
name|String
name|transaction
parameter_list|)
throws|throws
name|Exception
block|{
name|HashMap
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|headers
init|=
operator|new
name|HashMap
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
argument_list|()
decl_stmt|;
name|headers
operator|.
name|put
argument_list|(
literal|"transaction"
argument_list|,
name|transaction
argument_list|)
expr_stmt|;
name|StompFrame
name|frame
init|=
operator|new
name|StompFrame
argument_list|(
literal|"ABORT"
argument_list|,
name|headers
argument_list|)
decl_stmt|;
name|sendFrame
argument_list|(
name|frame
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|commit
parameter_list|(
name|String
name|transaction
parameter_list|)
throws|throws
name|Exception
block|{
name|HashMap
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|headers
init|=
operator|new
name|HashMap
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
argument_list|()
decl_stmt|;
name|headers
operator|.
name|put
argument_list|(
literal|"transaction"
argument_list|,
name|transaction
argument_list|)
expr_stmt|;
name|StompFrame
name|frame
init|=
operator|new
name|StompFrame
argument_list|(
literal|"COMMIT"
argument_list|,
name|headers
argument_list|)
decl_stmt|;
name|sendFrame
argument_list|(
name|frame
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|ack
parameter_list|(
name|StompFrame
name|frame
parameter_list|)
throws|throws
name|Exception
block|{
name|ack
argument_list|(
name|frame
operator|.
name|getHeaders
argument_list|()
operator|.
name|get
argument_list|(
literal|"message-id"
argument_list|)
argument_list|,
literal|null
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|ack
parameter_list|(
name|StompFrame
name|frame
parameter_list|,
name|String
name|transaction
parameter_list|)
throws|throws
name|Exception
block|{
name|ack
argument_list|(
name|frame
operator|.
name|getHeaders
argument_list|()
operator|.
name|get
argument_list|(
literal|"message-id"
argument_list|)
argument_list|,
name|transaction
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|ack
parameter_list|(
name|String
name|messageId
parameter_list|)
throws|throws
name|Exception
block|{
name|ack
argument_list|(
name|messageId
argument_list|,
literal|null
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|ack
parameter_list|(
name|String
name|messageId
parameter_list|,
name|String
name|transaction
parameter_list|)
throws|throws
name|Exception
block|{
name|HashMap
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|headers
init|=
operator|new
name|HashMap
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
argument_list|()
decl_stmt|;
name|headers
operator|.
name|put
argument_list|(
literal|"message-id"
argument_list|,
name|messageId
argument_list|)
expr_stmt|;
if|if
condition|(
name|transaction
operator|!=
literal|null
condition|)
name|headers
operator|.
name|put
argument_list|(
literal|"transaction"
argument_list|,
name|transaction
argument_list|)
expr_stmt|;
name|StompFrame
name|frame
init|=
operator|new
name|StompFrame
argument_list|(
literal|"ACK"
argument_list|,
name|headers
argument_list|)
decl_stmt|;
name|sendFrame
argument_list|(
name|frame
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
block|}
specifier|protected
name|String
name|appendHeaders
parameter_list|(
name|HashMap
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|headers
parameter_list|)
block|{
name|StringBuffer
name|result
init|=
operator|new
name|StringBuffer
argument_list|()
decl_stmt|;
for|for
control|(
name|String
name|key
range|:
name|headers
operator|.
name|keySet
argument_list|()
control|)
block|{
name|result
operator|.
name|append
argument_list|(
name|key
operator|+
literal|":"
operator|+
name|headers
operator|.
name|get
argument_list|(
name|key
argument_list|)
operator|+
literal|"\n"
argument_list|)
expr_stmt|;
block|}
name|result
operator|.
name|append
argument_list|(
literal|"\n"
argument_list|)
expr_stmt|;
return|return
name|result
operator|.
name|toString
argument_list|()
return|;
block|}
block|}
end_class

end_unit

