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
name|org
operator|.
name|apache
operator|.
name|activemq
operator|.
name|transport
operator|.
name|tcp
operator|.
name|TcpTransport
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
name|ByteArrayOutputStream
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
name|DataByteArrayInputStream
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|ByteArrayInputStream
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

begin_class
specifier|public
class|class
name|StompCodec
block|{
name|TcpTransport
name|transport
decl_stmt|;
name|ByteArrayOutputStream
name|currentCommand
init|=
operator|new
name|ByteArrayOutputStream
argument_list|()
decl_stmt|;
name|boolean
name|processedHeaders
init|=
literal|false
decl_stmt|;
name|String
name|action
decl_stmt|;
name|HashMap
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|headers
decl_stmt|;
name|int
name|contentLength
init|=
operator|-
literal|1
decl_stmt|;
name|int
name|readLength
init|=
literal|0
decl_stmt|;
name|int
name|previousByte
init|=
operator|-
literal|1
decl_stmt|;
specifier|public
name|StompCodec
parameter_list|(
name|TcpTransport
name|transport
parameter_list|)
block|{
name|this
operator|.
name|transport
operator|=
name|transport
expr_stmt|;
block|}
specifier|public
name|void
name|parse
parameter_list|(
name|ByteArrayInputStream
name|input
parameter_list|,
name|int
name|readSize
parameter_list|)
throws|throws
name|Exception
block|{
name|int
name|i
init|=
literal|0
decl_stmt|;
name|int
name|b
decl_stmt|;
while|while
condition|(
name|i
operator|++
operator|<
name|readSize
condition|)
block|{
name|b
operator|=
name|input
operator|.
name|read
argument_list|()
expr_stmt|;
comment|// skip repeating nulls
if|if
condition|(
operator|!
name|processedHeaders
operator|&&
name|previousByte
operator|==
literal|0
operator|&&
name|b
operator|==
literal|0
condition|)
block|{
continue|continue;
block|}
if|if
condition|(
operator|!
name|processedHeaders
condition|)
block|{
name|currentCommand
operator|.
name|write
argument_list|(
name|b
argument_list|)
expr_stmt|;
comment|// end of headers section, parse action and header
if|if
condition|(
name|previousByte
operator|==
literal|'\n'
operator|&&
name|b
operator|==
literal|'\n'
condition|)
block|{
if|if
condition|(
name|transport
operator|.
name|getWireFormat
argument_list|()
operator|instanceof
name|StompWireFormat
condition|)
block|{
name|DataByteArrayInputStream
name|data
init|=
operator|new
name|DataByteArrayInputStream
argument_list|(
name|currentCommand
operator|.
name|toByteArray
argument_list|()
argument_list|)
decl_stmt|;
name|action
operator|=
operator|(
operator|(
name|StompWireFormat
operator|)
name|transport
operator|.
name|getWireFormat
argument_list|()
operator|)
operator|.
name|parseAction
argument_list|(
name|data
argument_list|)
expr_stmt|;
name|headers
operator|=
operator|(
operator|(
name|StompWireFormat
operator|)
name|transport
operator|.
name|getWireFormat
argument_list|()
operator|)
operator|.
name|parseHeaders
argument_list|(
name|data
argument_list|)
expr_stmt|;
name|String
name|contentLengthHeader
init|=
name|headers
operator|.
name|get
argument_list|(
name|Stomp
operator|.
name|Headers
operator|.
name|CONTENT_LENGTH
argument_list|)
decl_stmt|;
if|if
condition|(
operator|(
name|action
operator|.
name|equals
argument_list|(
name|Stomp
operator|.
name|Commands
operator|.
name|SEND
argument_list|)
operator|||
name|action
operator|.
name|equals
argument_list|(
name|Stomp
operator|.
name|Responses
operator|.
name|MESSAGE
argument_list|)
operator|)
operator|&&
name|contentLengthHeader
operator|!=
literal|null
condition|)
block|{
name|contentLength
operator|=
operator|(
operator|(
name|StompWireFormat
operator|)
name|transport
operator|.
name|getWireFormat
argument_list|()
operator|)
operator|.
name|parseContentLength
argument_list|(
name|contentLengthHeader
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|contentLength
operator|=
operator|-
literal|1
expr_stmt|;
block|}
block|}
name|processedHeaders
operator|=
literal|true
expr_stmt|;
name|currentCommand
operator|.
name|reset
argument_list|()
expr_stmt|;
block|}
block|}
else|else
block|{
if|if
condition|(
name|contentLength
operator|==
operator|-
literal|1
condition|)
block|{
comment|// end of command reached, unmarshal
if|if
condition|(
name|b
operator|==
literal|0
condition|)
block|{
name|processCommand
argument_list|()
expr_stmt|;
block|}
else|else
block|{
name|currentCommand
operator|.
name|write
argument_list|(
name|b
argument_list|)
expr_stmt|;
block|}
block|}
else|else
block|{
comment|// read desired content length
if|if
condition|(
name|readLength
operator|++
operator|==
name|contentLength
condition|)
block|{
name|processCommand
argument_list|()
expr_stmt|;
name|readLength
operator|=
literal|0
expr_stmt|;
block|}
else|else
block|{
name|currentCommand
operator|.
name|write
argument_list|(
name|b
argument_list|)
expr_stmt|;
block|}
block|}
block|}
name|previousByte
operator|=
name|b
expr_stmt|;
block|}
block|}
specifier|protected
name|void
name|processCommand
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
name|action
argument_list|,
name|headers
argument_list|,
name|currentCommand
operator|.
name|toByteArray
argument_list|()
argument_list|)
decl_stmt|;
name|transport
operator|.
name|doConsume
argument_list|(
name|frame
argument_list|)
expr_stmt|;
name|processedHeaders
operator|=
literal|false
expr_stmt|;
name|currentCommand
operator|.
name|reset
argument_list|()
expr_stmt|;
name|contentLength
operator|=
operator|-
literal|1
expr_stmt|;
block|}
block|}
end_class

end_unit

