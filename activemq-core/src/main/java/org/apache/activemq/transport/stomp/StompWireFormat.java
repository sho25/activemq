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
name|DataInput
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
name|DataOutput
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
name|IOException
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
name|Iterator
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Map
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
name|ByteArrayInputStream
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
name|wireformat
operator|.
name|WireFormat
import|;
end_import

begin_comment
comment|/**  * Implements marshalling and unmarsalling the<a  * href="http://stomp.codehaus.org/">Stomp</a> protocol.  */
end_comment

begin_class
specifier|public
class|class
name|StompWireFormat
implements|implements
name|WireFormat
block|{
specifier|private
specifier|static
specifier|final
name|byte
index|[]
name|NO_DATA
init|=
operator|new
name|byte
index|[]
block|{}
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|byte
index|[]
name|END_OF_FRAME
init|=
operator|new
name|byte
index|[]
block|{
literal|0
block|,
literal|'\n'
block|}
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|int
name|MAX_COMMAND_LENGTH
init|=
literal|1024
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|int
name|MAX_HEADER_LENGTH
init|=
literal|1024
operator|*
literal|10
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|int
name|MAX_HEADERS
init|=
literal|1000
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|int
name|MAX_DATA_LENGTH
init|=
literal|1024
operator|*
literal|1024
operator|*
literal|100
decl_stmt|;
specifier|private
name|int
name|version
init|=
literal|1
decl_stmt|;
specifier|public
name|ByteSequence
name|marshal
parameter_list|(
name|Object
name|command
parameter_list|)
throws|throws
name|IOException
block|{
name|ByteArrayOutputStream
name|baos
init|=
operator|new
name|ByteArrayOutputStream
argument_list|()
decl_stmt|;
name|DataOutputStream
name|dos
init|=
operator|new
name|DataOutputStream
argument_list|(
name|baos
argument_list|)
decl_stmt|;
name|marshal
argument_list|(
name|command
argument_list|,
name|dos
argument_list|)
expr_stmt|;
name|dos
operator|.
name|close
argument_list|()
expr_stmt|;
return|return
name|baos
operator|.
name|toByteSequence
argument_list|()
return|;
block|}
specifier|public
name|Object
name|unmarshal
parameter_list|(
name|ByteSequence
name|packet
parameter_list|)
throws|throws
name|IOException
block|{
name|ByteArrayInputStream
name|stream
init|=
operator|new
name|ByteArrayInputStream
argument_list|(
name|packet
argument_list|)
decl_stmt|;
name|DataInputStream
name|dis
init|=
operator|new
name|DataInputStream
argument_list|(
name|stream
argument_list|)
decl_stmt|;
return|return
name|unmarshal
argument_list|(
name|dis
argument_list|)
return|;
block|}
specifier|public
name|void
name|marshal
parameter_list|(
name|Object
name|command
parameter_list|,
name|DataOutput
name|os
parameter_list|)
throws|throws
name|IOException
block|{
name|StompFrame
name|stomp
init|=
operator|(
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
operator|)
name|command
decl_stmt|;
name|StringBuffer
name|buffer
init|=
operator|new
name|StringBuffer
argument_list|()
decl_stmt|;
name|buffer
operator|.
name|append
argument_list|(
name|stomp
operator|.
name|getAction
argument_list|()
argument_list|)
expr_stmt|;
name|buffer
operator|.
name|append
argument_list|(
name|Stomp
operator|.
name|NEWLINE
argument_list|)
expr_stmt|;
comment|// Output the headers.
for|for
control|(
name|Iterator
name|iter
init|=
name|stomp
operator|.
name|getHeaders
argument_list|()
operator|.
name|entrySet
argument_list|()
operator|.
name|iterator
argument_list|()
init|;
name|iter
operator|.
name|hasNext
argument_list|()
condition|;
control|)
block|{
name|Map
operator|.
name|Entry
name|entry
init|=
operator|(
name|Map
operator|.
name|Entry
operator|)
name|iter
operator|.
name|next
argument_list|()
decl_stmt|;
name|buffer
operator|.
name|append
argument_list|(
name|entry
operator|.
name|getKey
argument_list|()
argument_list|)
expr_stmt|;
name|buffer
operator|.
name|append
argument_list|(
name|Stomp
operator|.
name|Headers
operator|.
name|SEPERATOR
argument_list|)
expr_stmt|;
name|buffer
operator|.
name|append
argument_list|(
name|entry
operator|.
name|getValue
argument_list|()
argument_list|)
expr_stmt|;
name|buffer
operator|.
name|append
argument_list|(
name|Stomp
operator|.
name|NEWLINE
argument_list|)
expr_stmt|;
block|}
comment|// Add a newline to seperate the headers from the content.
name|buffer
operator|.
name|append
argument_list|(
name|Stomp
operator|.
name|NEWLINE
argument_list|)
expr_stmt|;
name|os
operator|.
name|write
argument_list|(
name|buffer
operator|.
name|toString
argument_list|()
operator|.
name|getBytes
argument_list|(
literal|"UTF-8"
argument_list|)
argument_list|)
expr_stmt|;
name|os
operator|.
name|write
argument_list|(
name|stomp
operator|.
name|getContent
argument_list|()
argument_list|)
expr_stmt|;
name|os
operator|.
name|write
argument_list|(
name|END_OF_FRAME
argument_list|)
expr_stmt|;
block|}
specifier|public
name|Object
name|unmarshal
parameter_list|(
name|DataInput
name|in
parameter_list|)
throws|throws
name|IOException
block|{
try|try
block|{
name|String
name|action
init|=
literal|null
decl_stmt|;
comment|// skip white space to next real action line
while|while
condition|(
literal|true
condition|)
block|{
name|action
operator|=
name|readLine
argument_list|(
name|in
argument_list|,
name|MAX_COMMAND_LENGTH
argument_list|,
literal|"The maximum command length was exceeded"
argument_list|)
expr_stmt|;
if|if
condition|(
name|action
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|IOException
argument_list|(
literal|"connection was closed"
argument_list|)
throw|;
block|}
else|else
block|{
name|action
operator|=
name|action
operator|.
name|trim
argument_list|()
expr_stmt|;
if|if
condition|(
name|action
operator|.
name|length
argument_list|()
operator|>
literal|0
condition|)
block|{
break|break;
block|}
block|}
block|}
comment|// Parse the headers
name|HashMap
name|headers
init|=
operator|new
name|HashMap
argument_list|(
literal|25
argument_list|)
decl_stmt|;
while|while
condition|(
literal|true
condition|)
block|{
name|String
name|line
init|=
name|readLine
argument_list|(
name|in
argument_list|,
name|MAX_HEADER_LENGTH
argument_list|,
literal|"The maximum header length was exceeded"
argument_list|)
decl_stmt|;
if|if
condition|(
name|line
operator|!=
literal|null
operator|&&
name|line
operator|.
name|trim
argument_list|()
operator|.
name|length
argument_list|()
operator|>
literal|0
condition|)
block|{
if|if
condition|(
name|headers
operator|.
name|size
argument_list|()
operator|>
name|MAX_HEADERS
condition|)
block|{
throw|throw
operator|new
name|ProtocolException
argument_list|(
literal|"The maximum number of headers was exceeded"
argument_list|,
literal|true
argument_list|)
throw|;
block|}
try|try
block|{
name|int
name|seperatorIndex
init|=
name|line
operator|.
name|indexOf
argument_list|(
name|Stomp
operator|.
name|Headers
operator|.
name|SEPERATOR
argument_list|)
decl_stmt|;
name|String
name|name
init|=
name|line
operator|.
name|substring
argument_list|(
literal|0
argument_list|,
name|seperatorIndex
argument_list|)
operator|.
name|trim
argument_list|()
decl_stmt|;
name|String
name|value
init|=
name|line
operator|.
name|substring
argument_list|(
name|seperatorIndex
operator|+
literal|1
argument_list|,
name|line
operator|.
name|length
argument_list|()
argument_list|)
operator|.
name|trim
argument_list|()
decl_stmt|;
name|headers
operator|.
name|put
argument_list|(
name|name
argument_list|,
name|value
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
operator|new
name|ProtocolException
argument_list|(
literal|"Unable to parser header line ["
operator|+
name|line
operator|+
literal|"]"
argument_list|,
literal|true
argument_list|)
throw|;
block|}
block|}
else|else
block|{
break|break;
block|}
block|}
comment|// Read in the data part.
name|byte
index|[]
name|data
init|=
name|NO_DATA
decl_stmt|;
name|String
name|contentLength
init|=
operator|(
name|String
operator|)
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
name|contentLength
operator|!=
literal|null
condition|)
block|{
comment|// Bless the client, he's telling us how much data to read in.
name|int
name|length
decl_stmt|;
try|try
block|{
name|length
operator|=
name|Integer
operator|.
name|parseInt
argument_list|(
name|contentLength
operator|.
name|trim
argument_list|()
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|NumberFormatException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|ProtocolException
argument_list|(
literal|"Specified content-length is not a valid integer"
argument_list|,
literal|true
argument_list|)
throw|;
block|}
if|if
condition|(
name|length
operator|>
name|MAX_DATA_LENGTH
condition|)
block|{
throw|throw
operator|new
name|ProtocolException
argument_list|(
literal|"The maximum data length was exceeded"
argument_list|,
literal|true
argument_list|)
throw|;
block|}
name|data
operator|=
operator|new
name|byte
index|[
name|length
index|]
expr_stmt|;
name|in
operator|.
name|readFully
argument_list|(
name|data
argument_list|)
expr_stmt|;
if|if
condition|(
name|in
operator|.
name|readByte
argument_list|()
operator|!=
literal|0
condition|)
block|{
throw|throw
operator|new
name|ProtocolException
argument_list|(
name|Stomp
operator|.
name|Headers
operator|.
name|CONTENT_LENGTH
operator|+
literal|" bytes were read and "
operator|+
literal|"there was no trailing null byte"
argument_list|,
literal|true
argument_list|)
throw|;
block|}
block|}
else|else
block|{
comment|// We don't know how much to read.. data ends when we hit a 0
name|byte
name|b
decl_stmt|;
name|ByteArrayOutputStream
name|baos
init|=
literal|null
decl_stmt|;
while|while
condition|(
operator|(
name|b
operator|=
name|in
operator|.
name|readByte
argument_list|()
operator|)
operator|!=
literal|0
condition|)
block|{
if|if
condition|(
name|baos
operator|==
literal|null
condition|)
block|{
name|baos
operator|=
operator|new
name|ByteArrayOutputStream
argument_list|()
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|baos
operator|.
name|size
argument_list|()
operator|>
name|MAX_DATA_LENGTH
condition|)
block|{
throw|throw
operator|new
name|ProtocolException
argument_list|(
literal|"The maximum data length was exceeded"
argument_list|,
literal|true
argument_list|)
throw|;
block|}
name|baos
operator|.
name|write
argument_list|(
name|b
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|baos
operator|!=
literal|null
condition|)
block|{
name|baos
operator|.
name|close
argument_list|()
expr_stmt|;
name|data
operator|=
name|baos
operator|.
name|toByteArray
argument_list|()
expr_stmt|;
block|}
block|}
return|return
operator|new
name|StompFrame
argument_list|(
name|action
argument_list|,
name|headers
argument_list|,
name|data
argument_list|)
return|;
block|}
catch|catch
parameter_list|(
name|ProtocolException
name|e
parameter_list|)
block|{
return|return
operator|new
name|StompFrameError
argument_list|(
name|e
argument_list|)
return|;
block|}
block|}
specifier|private
name|String
name|readLine
parameter_list|(
name|DataInput
name|in
parameter_list|,
name|int
name|maxLength
parameter_list|,
name|String
name|errorMessage
parameter_list|)
throws|throws
name|IOException
block|{
name|byte
name|b
decl_stmt|;
name|ByteArrayOutputStream
name|baos
init|=
operator|new
name|ByteArrayOutputStream
argument_list|(
name|maxLength
argument_list|)
decl_stmt|;
while|while
condition|(
operator|(
name|b
operator|=
name|in
operator|.
name|readByte
argument_list|()
operator|)
operator|!=
literal|'\n'
condition|)
block|{
if|if
condition|(
name|baos
operator|.
name|size
argument_list|()
operator|>
name|maxLength
condition|)
block|{
throw|throw
operator|new
name|ProtocolException
argument_list|(
name|errorMessage
argument_list|,
literal|true
argument_list|)
throw|;
block|}
name|baos
operator|.
name|write
argument_list|(
name|b
argument_list|)
expr_stmt|;
block|}
name|baos
operator|.
name|close
argument_list|()
expr_stmt|;
name|ByteSequence
name|sequence
init|=
name|baos
operator|.
name|toByteSequence
argument_list|()
decl_stmt|;
return|return
operator|new
name|String
argument_list|(
name|sequence
operator|.
name|getData
argument_list|()
argument_list|,
name|sequence
operator|.
name|getOffset
argument_list|()
argument_list|,
name|sequence
operator|.
name|getLength
argument_list|()
argument_list|,
literal|"UTF-8"
argument_list|)
return|;
block|}
specifier|public
name|int
name|getVersion
parameter_list|()
block|{
return|return
name|version
return|;
block|}
specifier|public
name|void
name|setVersion
parameter_list|(
name|int
name|version
parameter_list|)
block|{
name|this
operator|.
name|version
operator|=
name|version
expr_stmt|;
block|}
block|}
end_class

end_unit

