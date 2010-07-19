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
name|Map
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|jms
operator|.
name|Destination
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
name|com
operator|.
name|thoughtworks
operator|.
name|xstream
operator|.
name|XStream
import|;
end_import

begin_import
import|import
name|com
operator|.
name|thoughtworks
operator|.
name|xstream
operator|.
name|io
operator|.
name|json
operator|.
name|JsonHierarchicalStreamDriver
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
name|advisory
operator|.
name|AdvisorySupport
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
name|*
import|;
end_import

begin_comment
comment|/**  * Implements ActiveMQ 4.0 translations  */
end_comment

begin_class
specifier|public
class|class
name|LegacyFrameTranslator
implements|implements
name|FrameTranslator
block|{
specifier|public
name|ActiveMQMessage
name|convertFrame
parameter_list|(
name|ProtocolConverter
name|converter
parameter_list|,
name|StompFrame
name|command
parameter_list|)
throws|throws
name|JMSException
throws|,
name|ProtocolException
block|{
specifier|final
name|Map
name|headers
init|=
name|command
operator|.
name|getHeaders
argument_list|()
decl_stmt|;
specifier|final
name|ActiveMQMessage
name|msg
decl_stmt|;
comment|/*          * To reduce the complexity of this method perhaps a Chain of Responsibility          * would be a better implementation          */
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
name|AMQ_MESSAGE_TYPE
argument_list|)
condition|)
block|{
name|String
name|intendedType
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
name|AMQ_MESSAGE_TYPE
argument_list|)
decl_stmt|;
if|if
condition|(
name|intendedType
operator|.
name|equalsIgnoreCase
argument_list|(
literal|"text"
argument_list|)
condition|)
block|{
name|ActiveMQTextMessage
name|text
init|=
operator|new
name|ActiveMQTextMessage
argument_list|()
decl_stmt|;
try|try
block|{
name|text
operator|.
name|setText
argument_list|(
operator|new
name|String
argument_list|(
name|command
operator|.
name|getContent
argument_list|()
argument_list|,
literal|"UTF-8"
argument_list|)
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Throwable
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|ProtocolException
argument_list|(
literal|"Text could not bet set: "
operator|+
name|e
argument_list|,
literal|false
argument_list|,
name|e
argument_list|)
throw|;
block|}
name|msg
operator|=
name|text
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|intendedType
operator|.
name|equalsIgnoreCase
argument_list|(
literal|"bytes"
argument_list|)
condition|)
block|{
name|ActiveMQBytesMessage
name|byteMessage
init|=
operator|new
name|ActiveMQBytesMessage
argument_list|()
decl_stmt|;
name|byteMessage
operator|.
name|writeBytes
argument_list|(
name|command
operator|.
name|getContent
argument_list|()
argument_list|)
expr_stmt|;
name|msg
operator|=
name|byteMessage
expr_stmt|;
block|}
else|else
block|{
throw|throw
operator|new
name|ProtocolException
argument_list|(
literal|"Unsupported message type '"
operator|+
name|intendedType
operator|+
literal|"'"
argument_list|,
literal|false
argument_list|)
throw|;
block|}
block|}
elseif|else
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
name|headers
operator|.
name|remove
argument_list|(
name|Stomp
operator|.
name|Headers
operator|.
name|CONTENT_LENGTH
argument_list|)
expr_stmt|;
name|ActiveMQBytesMessage
name|bm
init|=
operator|new
name|ActiveMQBytesMessage
argument_list|()
decl_stmt|;
name|bm
operator|.
name|writeBytes
argument_list|(
name|command
operator|.
name|getContent
argument_list|()
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
try|try
block|{
name|text
operator|.
name|setText
argument_list|(
operator|new
name|String
argument_list|(
name|command
operator|.
name|getContent
argument_list|()
argument_list|,
literal|"UTF-8"
argument_list|)
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Throwable
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|ProtocolException
argument_list|(
literal|"Text could not bet set: "
operator|+
name|e
argument_list|,
literal|false
argument_list|,
name|e
argument_list|)
throw|;
block|}
name|msg
operator|=
name|text
expr_stmt|;
block|}
name|FrameTranslator
operator|.
name|Helper
operator|.
name|copyStandardHeadersFromFrameToMessage
argument_list|(
name|converter
argument_list|,
name|command
argument_list|,
name|msg
argument_list|,
name|this
argument_list|)
expr_stmt|;
return|return
name|msg
return|;
block|}
specifier|public
name|StompFrame
name|convertMessage
parameter_list|(
name|ProtocolConverter
name|converter
parameter_list|,
name|ActiveMQMessage
name|message
parameter_list|)
throws|throws
name|IOException
throws|,
name|JMSException
block|{
name|StompFrame
name|command
init|=
operator|new
name|StompFrame
argument_list|()
decl_stmt|;
name|command
operator|.
name|setAction
argument_list|(
name|Stomp
operator|.
name|Responses
operator|.
name|MESSAGE
argument_list|)
expr_stmt|;
name|Map
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
argument_list|(
literal|25
argument_list|)
decl_stmt|;
name|command
operator|.
name|setHeaders
argument_list|(
name|headers
argument_list|)
expr_stmt|;
name|FrameTranslator
operator|.
name|Helper
operator|.
name|copyStandardHeadersFromMessageToFrame
argument_list|(
name|converter
argument_list|,
name|message
argument_list|,
name|command
argument_list|,
name|this
argument_list|)
expr_stmt|;
if|if
condition|(
name|message
operator|.
name|getDataStructureType
argument_list|()
operator|==
name|ActiveMQTextMessage
operator|.
name|DATA_STRUCTURE_TYPE
condition|)
block|{
name|ActiveMQTextMessage
name|msg
init|=
operator|(
name|ActiveMQTextMessage
operator|)
name|message
operator|.
name|copy
argument_list|()
decl_stmt|;
name|command
operator|.
name|setContent
argument_list|(
name|msg
operator|.
name|getText
argument_list|()
operator|.
name|getBytes
argument_list|(
literal|"UTF-8"
argument_list|)
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|message
operator|.
name|getDataStructureType
argument_list|()
operator|==
name|ActiveMQBytesMessage
operator|.
name|DATA_STRUCTURE_TYPE
condition|)
block|{
name|ActiveMQBytesMessage
name|msg
init|=
operator|(
name|ActiveMQBytesMessage
operator|)
name|message
operator|.
name|copy
argument_list|()
decl_stmt|;
name|msg
operator|.
name|setReadOnlyBody
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|byte
index|[]
name|data
init|=
operator|new
name|byte
index|[
operator|(
name|int
operator|)
name|msg
operator|.
name|getBodyLength
argument_list|()
index|]
decl_stmt|;
name|msg
operator|.
name|readBytes
argument_list|(
name|data
argument_list|)
expr_stmt|;
name|headers
operator|.
name|put
argument_list|(
name|Stomp
operator|.
name|Headers
operator|.
name|CONTENT_LENGTH
argument_list|,
literal|""
operator|+
name|data
operator|.
name|length
argument_list|)
expr_stmt|;
name|command
operator|.
name|setContent
argument_list|(
name|data
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|message
operator|.
name|getDataStructureType
argument_list|()
operator|==
name|ActiveMQMessage
operator|.
name|DATA_STRUCTURE_TYPE
operator|&&
name|AdvisorySupport
operator|.
name|ADIVSORY_MESSAGE_TYPE
operator|.
name|equals
argument_list|(
name|message
operator|.
name|getType
argument_list|()
argument_list|)
condition|)
block|{
name|FrameTranslator
operator|.
name|Helper
operator|.
name|copyStandardHeadersFromMessageToFrame
argument_list|(
name|converter
argument_list|,
name|message
argument_list|,
name|command
argument_list|,
name|this
argument_list|)
expr_stmt|;
name|String
name|body
init|=
name|marshallAdvisory
argument_list|(
name|message
operator|.
name|getDataStructure
argument_list|()
argument_list|)
decl_stmt|;
name|command
operator|.
name|setContent
argument_list|(
name|body
operator|.
name|getBytes
argument_list|(
literal|"UTF-8"
argument_list|)
argument_list|)
expr_stmt|;
block|}
return|return
name|command
return|;
block|}
specifier|public
name|String
name|convertDestination
parameter_list|(
name|ProtocolConverter
name|converter
parameter_list|,
name|Destination
name|d
parameter_list|)
block|{
if|if
condition|(
name|d
operator|==
literal|null
condition|)
block|{
return|return
literal|null
return|;
block|}
name|ActiveMQDestination
name|activeMQDestination
init|=
operator|(
name|ActiveMQDestination
operator|)
name|d
decl_stmt|;
name|String
name|physicalName
init|=
name|activeMQDestination
operator|.
name|getPhysicalName
argument_list|()
decl_stmt|;
name|String
name|rc
init|=
name|converter
operator|.
name|getCreatedTempDestinationName
argument_list|(
name|activeMQDestination
argument_list|)
decl_stmt|;
if|if
condition|(
name|rc
operator|!=
literal|null
condition|)
block|{
return|return
name|rc
return|;
block|}
name|StringBuffer
name|buffer
init|=
operator|new
name|StringBuffer
argument_list|()
decl_stmt|;
if|if
condition|(
name|activeMQDestination
operator|.
name|isQueue
argument_list|()
condition|)
block|{
if|if
condition|(
name|activeMQDestination
operator|.
name|isTemporary
argument_list|()
condition|)
block|{
name|buffer
operator|.
name|append
argument_list|(
literal|"/remote-temp-queue/"
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|buffer
operator|.
name|append
argument_list|(
literal|"/queue/"
argument_list|)
expr_stmt|;
block|}
block|}
else|else
block|{
if|if
condition|(
name|activeMQDestination
operator|.
name|isTemporary
argument_list|()
condition|)
block|{
name|buffer
operator|.
name|append
argument_list|(
literal|"/remote-temp-topic/"
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|buffer
operator|.
name|append
argument_list|(
literal|"/topic/"
argument_list|)
expr_stmt|;
block|}
block|}
name|buffer
operator|.
name|append
argument_list|(
name|physicalName
argument_list|)
expr_stmt|;
return|return
name|buffer
operator|.
name|toString
argument_list|()
return|;
block|}
specifier|public
name|ActiveMQDestination
name|convertDestination
parameter_list|(
name|ProtocolConverter
name|converter
parameter_list|,
name|String
name|name
parameter_list|)
throws|throws
name|ProtocolException
block|{
if|if
condition|(
name|name
operator|==
literal|null
condition|)
block|{
return|return
literal|null
return|;
block|}
elseif|else
if|if
condition|(
name|name
operator|.
name|startsWith
argument_list|(
literal|"/queue/"
argument_list|)
condition|)
block|{
name|String
name|qName
init|=
name|name
operator|.
name|substring
argument_list|(
literal|"/queue/"
operator|.
name|length
argument_list|()
argument_list|,
name|name
operator|.
name|length
argument_list|()
argument_list|)
decl_stmt|;
return|return
name|ActiveMQDestination
operator|.
name|createDestination
argument_list|(
name|qName
argument_list|,
name|ActiveMQDestination
operator|.
name|QUEUE_TYPE
argument_list|)
return|;
block|}
elseif|else
if|if
condition|(
name|name
operator|.
name|startsWith
argument_list|(
literal|"/topic/"
argument_list|)
condition|)
block|{
name|String
name|tName
init|=
name|name
operator|.
name|substring
argument_list|(
literal|"/topic/"
operator|.
name|length
argument_list|()
argument_list|,
name|name
operator|.
name|length
argument_list|()
argument_list|)
decl_stmt|;
return|return
name|ActiveMQDestination
operator|.
name|createDestination
argument_list|(
name|tName
argument_list|,
name|ActiveMQDestination
operator|.
name|TOPIC_TYPE
argument_list|)
return|;
block|}
elseif|else
if|if
condition|(
name|name
operator|.
name|startsWith
argument_list|(
literal|"/remote-temp-queue/"
argument_list|)
condition|)
block|{
name|String
name|tName
init|=
name|name
operator|.
name|substring
argument_list|(
literal|"/remote-temp-queue/"
operator|.
name|length
argument_list|()
argument_list|,
name|name
operator|.
name|length
argument_list|()
argument_list|)
decl_stmt|;
return|return
name|ActiveMQDestination
operator|.
name|createDestination
argument_list|(
name|tName
argument_list|,
name|ActiveMQDestination
operator|.
name|TEMP_QUEUE_TYPE
argument_list|)
return|;
block|}
elseif|else
if|if
condition|(
name|name
operator|.
name|startsWith
argument_list|(
literal|"/remote-temp-topic/"
argument_list|)
condition|)
block|{
name|String
name|tName
init|=
name|name
operator|.
name|substring
argument_list|(
literal|"/remote-temp-topic/"
operator|.
name|length
argument_list|()
argument_list|,
name|name
operator|.
name|length
argument_list|()
argument_list|)
decl_stmt|;
return|return
name|ActiveMQDestination
operator|.
name|createDestination
argument_list|(
name|tName
argument_list|,
name|ActiveMQDestination
operator|.
name|TEMP_TOPIC_TYPE
argument_list|)
return|;
block|}
elseif|else
if|if
condition|(
name|name
operator|.
name|startsWith
argument_list|(
literal|"/temp-queue/"
argument_list|)
condition|)
block|{
return|return
name|converter
operator|.
name|createTempQueue
argument_list|(
name|name
argument_list|)
return|;
block|}
elseif|else
if|if
condition|(
name|name
operator|.
name|startsWith
argument_list|(
literal|"/temp-topic/"
argument_list|)
condition|)
block|{
return|return
name|converter
operator|.
name|createTempTopic
argument_list|(
name|name
argument_list|)
return|;
block|}
else|else
block|{
throw|throw
operator|new
name|ProtocolException
argument_list|(
literal|"Illegal destination name: ["
operator|+
name|name
operator|+
literal|"] -- ActiveMQ STOMP destinations "
operator|+
literal|"must begine with one of: /queue/ /topic/ /temp-queue/ /temp-topic/"
argument_list|)
throw|;
block|}
block|}
comment|/**      * Return an Advisory message as a JSON formatted string      * @param ds      * @return      */
specifier|protected
name|String
name|marshallAdvisory
parameter_list|(
specifier|final
name|DataStructure
name|ds
parameter_list|)
block|{
name|XStream
name|xstream
init|=
operator|new
name|XStream
argument_list|(
operator|new
name|JsonHierarchicalStreamDriver
argument_list|()
argument_list|)
decl_stmt|;
name|xstream
operator|.
name|setMode
argument_list|(
name|XStream
operator|.
name|NO_REFERENCES
argument_list|)
expr_stmt|;
name|xstream
operator|.
name|aliasPackage
argument_list|(
literal|""
argument_list|,
literal|"org.apache.activemq.command"
argument_list|)
expr_stmt|;
return|return
name|xstream
operator|.
name|toXML
argument_list|(
name|ds
argument_list|)
return|;
block|}
block|}
end_class

end_unit

