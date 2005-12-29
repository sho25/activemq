begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  *  * Copyright 2005-2006 The Apache Software Foundation  *  * Licensed under the Apache License, Version 2.0 (the "License");  * you may not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  * http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
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
name|activeio
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
name|command
operator|.
name|ActiveMQBytesMessage
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
name|ActiveMQDestination
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
name|ActiveMQMessage
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
name|ActiveMQTextMessage
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
name|TransactionId
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|jms
operator|.
name|JMSException
import|;
end_import

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
name|DataInput
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
name|net
operator|.
name|ProtocolException
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Properties
import|;
end_import

begin_class
class|class
name|Send
implements|implements
name|StompCommand
block|{
specifier|private
specifier|final
name|HeaderParser
name|parser
init|=
operator|new
name|HeaderParser
argument_list|()
decl_stmt|;
specifier|private
specifier|final
name|StompWireFormat
name|format
decl_stmt|;
name|Send
parameter_list|(
name|StompWireFormat
name|format
parameter_list|)
block|{
name|this
operator|.
name|format
operator|=
name|format
expr_stmt|;
block|}
specifier|public
name|CommandEnvelope
name|build
parameter_list|(
name|String
name|commandLine
parameter_list|,
name|DataInput
name|in
parameter_list|)
throws|throws
name|IOException
throws|,
name|JMSException
block|{
name|Properties
name|headers
init|=
name|parser
operator|.
name|parse
argument_list|(
name|in
argument_list|)
decl_stmt|;
name|String
name|destination
init|=
operator|(
name|String
operator|)
name|headers
operator|.
name|remove
argument_list|(
name|Stomp
operator|.
name|Headers
operator|.
name|Send
operator|.
name|DESTINATION
argument_list|)
decl_stmt|;
comment|// now the body
name|ActiveMQMessage
name|msg
decl_stmt|;
if|if
condition|(
name|headers
operator|.
name|containsKey
argument_list|(
name|Stomp
operator|.
name|Headers
operator|.
name|CONTENT_LENGTH
argument_list|)
condition|)
block|{
name|ActiveMQBytesMessage
name|bm
init|=
operator|new
name|ActiveMQBytesMessage
argument_list|()
decl_stmt|;
name|String
name|content_length
init|=
name|headers
operator|.
name|getProperty
argument_list|(
name|Stomp
operator|.
name|Headers
operator|.
name|CONTENT_LENGTH
argument_list|)
operator|.
name|trim
argument_list|()
decl_stmt|;
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
name|content_length
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
argument_list|)
throw|;
block|}
name|byte
index|[]
name|bytes
init|=
operator|new
name|byte
index|[
name|length
index|]
decl_stmt|;
name|in
operator|.
name|readFully
argument_list|(
name|bytes
argument_list|)
expr_stmt|;
name|byte
name|nil
init|=
name|in
operator|.
name|readByte
argument_list|()
decl_stmt|;
if|if
condition|(
name|nil
operator|!=
literal|0
condition|)
throw|throw
operator|new
name|ProtocolException
argument_list|(
literal|"content-length bytes were read and "
operator|+
literal|"there was no trailing null byte"
argument_list|)
throw|;
name|ByteSequence
name|content
init|=
operator|new
name|ByteSequence
argument_list|(
name|bytes
argument_list|,
literal|0
argument_list|,
name|bytes
operator|.
name|length
argument_list|)
decl_stmt|;
name|bm
operator|.
name|setContent
argument_list|(
name|content
argument_list|)
expr_stmt|;
name|msg
operator|=
name|bm
expr_stmt|;
block|}
else|else
block|{
name|ActiveMQTextMessage
name|text
init|=
operator|new
name|ActiveMQTextMessage
argument_list|()
decl_stmt|;
name|ByteArrayOutputStream
name|bytes
init|=
operator|new
name|ByteArrayOutputStream
argument_list|()
decl_stmt|;
name|byte
name|b
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
name|bytes
operator|.
name|write
argument_list|(
name|b
argument_list|)
expr_stmt|;
block|}
name|bytes
operator|.
name|close
argument_list|()
expr_stmt|;
name|String
name|body
init|=
operator|new
name|String
argument_list|(
name|bytes
operator|.
name|toByteArray
argument_list|()
argument_list|)
decl_stmt|;
try|try
block|{
name|text
operator|.
name|setText
argument_list|(
name|body
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|JMSException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|RuntimeException
argument_list|(
literal|"Something is really wrong, we instantiated this thing!"
argument_list|)
throw|;
block|}
name|msg
operator|=
name|text
expr_stmt|;
block|}
name|msg
operator|.
name|setProducerId
argument_list|(
name|format
operator|.
name|getProducerId
argument_list|()
argument_list|)
expr_stmt|;
name|msg
operator|.
name|setMessageId
argument_list|(
name|format
operator|.
name|createMessageId
argument_list|()
argument_list|)
expr_stmt|;
name|ActiveMQDestination
name|d
init|=
name|DestinationNamer
operator|.
name|convert
argument_list|(
name|destination
argument_list|)
decl_stmt|;
name|msg
operator|.
name|setDestination
argument_list|(
name|d
argument_list|)
expr_stmt|;
comment|// msg.setJMSClientID(format.getClientId());
comment|// the standard JMS headers
name|msg
operator|.
name|setJMSCorrelationID
argument_list|(
operator|(
name|String
operator|)
name|headers
operator|.
name|remove
argument_list|(
name|Stomp
operator|.
name|Headers
operator|.
name|Send
operator|.
name|CORRELATION_ID
argument_list|)
argument_list|)
expr_stmt|;
name|Object
name|expiration
init|=
name|headers
operator|.
name|remove
argument_list|(
name|Stomp
operator|.
name|Headers
operator|.
name|Send
operator|.
name|EXPIRATION_TIME
argument_list|)
decl_stmt|;
if|if
condition|(
name|expiration
operator|!=
literal|null
condition|)
block|{
name|msg
operator|.
name|setJMSExpiration
argument_list|(
name|asLong
argument_list|(
name|expiration
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|Object
name|priority
init|=
name|headers
operator|.
name|remove
argument_list|(
name|Stomp
operator|.
name|Headers
operator|.
name|Send
operator|.
name|PRIORITY
argument_list|)
decl_stmt|;
if|if
condition|(
name|priority
operator|!=
literal|null
condition|)
block|{
name|msg
operator|.
name|setJMSPriority
argument_list|(
name|asInt
argument_list|(
name|priority
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|msg
operator|.
name|setJMSReplyTo
argument_list|(
name|DestinationNamer
operator|.
name|convert
argument_list|(
operator|(
name|String
operator|)
name|headers
operator|.
name|remove
argument_list|(
name|Stomp
operator|.
name|Headers
operator|.
name|Send
operator|.
name|REPLY_TO
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
comment|// now the general headers
name|msg
operator|.
name|setProperties
argument_list|(
name|headers
argument_list|)
expr_stmt|;
if|if
condition|(
name|headers
operator|.
name|containsKey
argument_list|(
name|Stomp
operator|.
name|Headers
operator|.
name|TRANSACTION
argument_list|)
condition|)
block|{
name|TransactionId
name|tx_id
init|=
name|format
operator|.
name|getTransactionId
argument_list|(
name|headers
operator|.
name|getProperty
argument_list|(
name|Stomp
operator|.
name|Headers
operator|.
name|TRANSACTION
argument_list|)
argument_list|)
decl_stmt|;
if|if
condition|(
name|tx_id
operator|==
literal|null
condition|)
throw|throw
operator|new
name|ProtocolException
argument_list|(
name|headers
operator|.
name|getProperty
argument_list|(
name|Stomp
operator|.
name|Headers
operator|.
name|TRANSACTION
argument_list|)
operator|+
literal|" is an invalid transaction id"
argument_list|)
throw|;
name|msg
operator|.
name|setTransactionId
argument_list|(
name|tx_id
argument_list|)
expr_stmt|;
block|}
return|return
operator|new
name|CommandEnvelope
argument_list|(
name|msg
argument_list|,
name|headers
argument_list|)
return|;
block|}
specifier|protected
name|boolean
name|asBool
parameter_list|(
name|Object
name|value
parameter_list|)
block|{
if|if
condition|(
name|value
operator|!=
literal|null
condition|)
block|{
return|return
name|String
operator|.
name|valueOf
argument_list|(
name|value
argument_list|)
operator|.
name|equals
argument_list|(
literal|"true"
argument_list|)
return|;
block|}
return|return
literal|false
return|;
block|}
specifier|protected
name|long
name|asLong
parameter_list|(
name|Object
name|value
parameter_list|)
block|{
if|if
condition|(
name|value
operator|instanceof
name|Number
condition|)
block|{
name|Number
name|n
init|=
operator|(
name|Number
operator|)
name|value
decl_stmt|;
return|return
name|n
operator|.
name|longValue
argument_list|()
return|;
block|}
return|return
name|Long
operator|.
name|parseLong
argument_list|(
name|value
operator|.
name|toString
argument_list|()
argument_list|)
return|;
block|}
specifier|protected
name|int
name|asInt
parameter_list|(
name|Object
name|value
parameter_list|)
block|{
if|if
condition|(
name|value
operator|instanceof
name|Number
condition|)
block|{
name|Number
name|n
init|=
operator|(
name|Number
operator|)
name|value
decl_stmt|;
return|return
name|n
operator|.
name|intValue
argument_list|()
return|;
block|}
return|return
name|Integer
operator|.
name|parseInt
argument_list|(
name|value
operator|.
name|toString
argument_list|()
argument_list|)
return|;
block|}
block|}
end_class

end_unit

