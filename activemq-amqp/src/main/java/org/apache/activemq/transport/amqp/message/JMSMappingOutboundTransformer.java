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
name|amqp
operator|.
name|message
package|;
end_package

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|UnsupportedEncodingException
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
name|ArrayList
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Date
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Enumeration
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
name|javax
operator|.
name|jms
operator|.
name|BytesMessage
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|jms
operator|.
name|DeliveryMode
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
name|javax
operator|.
name|jms
operator|.
name|MapMessage
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|jms
operator|.
name|Message
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|jms
operator|.
name|MessageEOFException
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|jms
operator|.
name|MessageFormatException
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|jms
operator|.
name|ObjectMessage
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|jms
operator|.
name|Queue
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|jms
operator|.
name|StreamMessage
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|jms
operator|.
name|TemporaryQueue
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|jms
operator|.
name|TemporaryTopic
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|jms
operator|.
name|TextMessage
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|jms
operator|.
name|Topic
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|qpid
operator|.
name|proton
operator|.
name|amqp
operator|.
name|Binary
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|qpid
operator|.
name|proton
operator|.
name|amqp
operator|.
name|Symbol
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|qpid
operator|.
name|proton
operator|.
name|amqp
operator|.
name|UnsignedByte
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|qpid
operator|.
name|proton
operator|.
name|amqp
operator|.
name|UnsignedInteger
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|qpid
operator|.
name|proton
operator|.
name|amqp
operator|.
name|messaging
operator|.
name|AmqpSequence
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|qpid
operator|.
name|proton
operator|.
name|amqp
operator|.
name|messaging
operator|.
name|AmqpValue
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|qpid
operator|.
name|proton
operator|.
name|amqp
operator|.
name|messaging
operator|.
name|ApplicationProperties
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|qpid
operator|.
name|proton
operator|.
name|amqp
operator|.
name|messaging
operator|.
name|Data
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|qpid
operator|.
name|proton
operator|.
name|amqp
operator|.
name|messaging
operator|.
name|DeliveryAnnotations
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|qpid
operator|.
name|proton
operator|.
name|amqp
operator|.
name|messaging
operator|.
name|Footer
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|qpid
operator|.
name|proton
operator|.
name|amqp
operator|.
name|messaging
operator|.
name|Header
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|qpid
operator|.
name|proton
operator|.
name|amqp
operator|.
name|messaging
operator|.
name|MessageAnnotations
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|qpid
operator|.
name|proton
operator|.
name|amqp
operator|.
name|messaging
operator|.
name|Properties
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|qpid
operator|.
name|proton
operator|.
name|amqp
operator|.
name|messaging
operator|.
name|Section
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|qpid
operator|.
name|proton
operator|.
name|codec
operator|.
name|CompositeWritableBuffer
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|qpid
operator|.
name|proton
operator|.
name|codec
operator|.
name|DroppingWritableBuffer
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|qpid
operator|.
name|proton
operator|.
name|codec
operator|.
name|WritableBuffer
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|qpid
operator|.
name|proton
operator|.
name|message
operator|.
name|ProtonJMessage
import|;
end_import

begin_class
specifier|public
class|class
name|JMSMappingOutboundTransformer
extends|extends
name|OutboundTransformer
block|{
specifier|public
name|JMSMappingOutboundTransformer
parameter_list|(
name|JMSVendor
name|vendor
parameter_list|)
block|{
name|super
argument_list|(
name|vendor
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|EncodedMessage
name|transform
parameter_list|(
name|Message
name|msg
parameter_list|)
throws|throws
name|Exception
block|{
if|if
condition|(
name|msg
operator|==
literal|null
condition|)
block|{
return|return
literal|null
return|;
block|}
try|try
block|{
if|if
condition|(
name|msg
operator|.
name|getBooleanProperty
argument_list|(
name|prefixVendor
operator|+
literal|"NATIVE"
argument_list|)
condition|)
block|{
return|return
literal|null
return|;
block|}
block|}
catch|catch
parameter_list|(
name|MessageFormatException
name|e
parameter_list|)
block|{
return|return
literal|null
return|;
block|}
name|ProtonJMessage
name|amqp
init|=
name|convert
argument_list|(
name|msg
argument_list|)
decl_stmt|;
name|long
name|messageFormat
decl_stmt|;
try|try
block|{
name|messageFormat
operator|=
name|msg
operator|.
name|getLongProperty
argument_list|(
name|this
operator|.
name|messageFormatKey
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|MessageFormatException
name|e
parameter_list|)
block|{
return|return
literal|null
return|;
block|}
name|ByteBuffer
name|buffer
init|=
name|ByteBuffer
operator|.
name|wrap
argument_list|(
operator|new
name|byte
index|[
literal|1024
operator|*
literal|4
index|]
argument_list|)
decl_stmt|;
specifier|final
name|DroppingWritableBuffer
name|overflow
init|=
operator|new
name|DroppingWritableBuffer
argument_list|()
decl_stmt|;
name|int
name|c
init|=
name|amqp
operator|.
name|encode
argument_list|(
operator|new
name|CompositeWritableBuffer
argument_list|(
operator|new
name|WritableBuffer
operator|.
name|ByteBufferWrapper
argument_list|(
name|buffer
argument_list|)
argument_list|,
name|overflow
argument_list|)
argument_list|)
decl_stmt|;
if|if
condition|(
name|overflow
operator|.
name|position
argument_list|()
operator|>
literal|0
condition|)
block|{
name|buffer
operator|=
name|ByteBuffer
operator|.
name|wrap
argument_list|(
operator|new
name|byte
index|[
literal|1024
operator|*
literal|4
operator|+
name|overflow
operator|.
name|position
argument_list|()
index|]
argument_list|)
expr_stmt|;
name|c
operator|=
name|amqp
operator|.
name|encode
argument_list|(
operator|new
name|WritableBuffer
operator|.
name|ByteBufferWrapper
argument_list|(
name|buffer
argument_list|)
argument_list|)
expr_stmt|;
block|}
return|return
operator|new
name|EncodedMessage
argument_list|(
name|messageFormat
argument_list|,
name|buffer
operator|.
name|array
argument_list|()
argument_list|,
literal|0
argument_list|,
name|c
argument_list|)
return|;
block|}
comment|/**      * Perform the conversion between JMS Message and Proton Message without      * re-encoding it to array. This is needed because some frameworks may elect      * to do this on their own way (Netty for instance using Nettybuffers)      *      * @param msg      * @return      * @throws Exception      */
specifier|public
name|ProtonJMessage
name|convert
parameter_list|(
name|Message
name|msg
parameter_list|)
throws|throws
name|JMSException
throws|,
name|UnsupportedEncodingException
block|{
name|Header
name|header
init|=
operator|new
name|Header
argument_list|()
decl_stmt|;
name|Properties
name|props
init|=
operator|new
name|Properties
argument_list|()
decl_stmt|;
name|HashMap
argument_list|<
name|Symbol
argument_list|,
name|Object
argument_list|>
name|daMap
init|=
literal|null
decl_stmt|;
name|HashMap
argument_list|<
name|Symbol
argument_list|,
name|Object
argument_list|>
name|maMap
init|=
literal|null
decl_stmt|;
name|HashMap
name|apMap
init|=
literal|null
decl_stmt|;
name|Section
name|body
init|=
literal|null
decl_stmt|;
name|HashMap
name|footerMap
init|=
literal|null
decl_stmt|;
if|if
condition|(
name|msg
operator|instanceof
name|BytesMessage
condition|)
block|{
name|BytesMessage
name|m
init|=
operator|(
name|BytesMessage
operator|)
name|msg
decl_stmt|;
name|byte
name|data
index|[]
init|=
operator|new
name|byte
index|[
operator|(
name|int
operator|)
name|m
operator|.
name|getBodyLength
argument_list|()
index|]
decl_stmt|;
name|m
operator|.
name|readBytes
argument_list|(
name|data
argument_list|)
expr_stmt|;
name|m
operator|.
name|reset
argument_list|()
expr_stmt|;
comment|// Need to reset after readBytes or future readBytes
comment|// calls (ex: redeliveries) will fail and return -1
name|body
operator|=
operator|new
name|Data
argument_list|(
operator|new
name|Binary
argument_list|(
name|data
argument_list|)
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|msg
operator|instanceof
name|TextMessage
condition|)
block|{
name|body
operator|=
operator|new
name|AmqpValue
argument_list|(
operator|(
operator|(
name|TextMessage
operator|)
name|msg
operator|)
operator|.
name|getText
argument_list|()
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|msg
operator|instanceof
name|MapMessage
condition|)
block|{
specifier|final
name|HashMap
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|map
init|=
operator|new
name|HashMap
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
argument_list|()
decl_stmt|;
specifier|final
name|MapMessage
name|m
init|=
operator|(
name|MapMessage
operator|)
name|msg
decl_stmt|;
specifier|final
name|Enumeration
argument_list|<
name|String
argument_list|>
name|names
init|=
name|m
operator|.
name|getMapNames
argument_list|()
decl_stmt|;
while|while
condition|(
name|names
operator|.
name|hasMoreElements
argument_list|()
condition|)
block|{
name|String
name|key
init|=
name|names
operator|.
name|nextElement
argument_list|()
decl_stmt|;
name|map
operator|.
name|put
argument_list|(
name|key
argument_list|,
name|m
operator|.
name|getObject
argument_list|(
name|key
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|body
operator|=
operator|new
name|AmqpValue
argument_list|(
name|map
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|msg
operator|instanceof
name|StreamMessage
condition|)
block|{
name|ArrayList
argument_list|<
name|Object
argument_list|>
name|list
init|=
operator|new
name|ArrayList
argument_list|<
name|Object
argument_list|>
argument_list|()
decl_stmt|;
specifier|final
name|StreamMessage
name|m
init|=
operator|(
name|StreamMessage
operator|)
name|msg
decl_stmt|;
try|try
block|{
while|while
condition|(
literal|true
condition|)
block|{
name|list
operator|.
name|add
argument_list|(
name|m
operator|.
name|readObject
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
catch|catch
parameter_list|(
name|MessageEOFException
name|e
parameter_list|)
block|{             }
name|body
operator|=
operator|new
name|AmqpSequence
argument_list|(
name|list
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|msg
operator|instanceof
name|ObjectMessage
condition|)
block|{
name|body
operator|=
operator|new
name|AmqpValue
argument_list|(
operator|(
operator|(
name|ObjectMessage
operator|)
name|msg
operator|)
operator|.
name|getObject
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|header
operator|.
name|setDurable
argument_list|(
name|msg
operator|.
name|getJMSDeliveryMode
argument_list|()
operator|==
name|DeliveryMode
operator|.
name|PERSISTENT
condition|?
literal|true
else|:
literal|false
argument_list|)
expr_stmt|;
name|header
operator|.
name|setPriority
argument_list|(
operator|new
name|UnsignedByte
argument_list|(
operator|(
name|byte
operator|)
name|msg
operator|.
name|getJMSPriority
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
if|if
condition|(
name|msg
operator|.
name|getJMSType
argument_list|()
operator|!=
literal|null
condition|)
block|{
if|if
condition|(
name|maMap
operator|==
literal|null
condition|)
block|{
name|maMap
operator|=
operator|new
name|HashMap
argument_list|<
name|Symbol
argument_list|,
name|Object
argument_list|>
argument_list|()
expr_stmt|;
block|}
name|maMap
operator|.
name|put
argument_list|(
name|Symbol
operator|.
name|valueOf
argument_list|(
literal|"x-opt-jms-type"
argument_list|)
argument_list|,
name|msg
operator|.
name|getJMSType
argument_list|()
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|msg
operator|.
name|getJMSMessageID
argument_list|()
operator|!=
literal|null
condition|)
block|{
name|props
operator|.
name|setMessageId
argument_list|(
name|msg
operator|.
name|getJMSMessageID
argument_list|()
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|msg
operator|.
name|getJMSDestination
argument_list|()
operator|!=
literal|null
condition|)
block|{
name|props
operator|.
name|setTo
argument_list|(
name|vendor
operator|.
name|toAddress
argument_list|(
name|msg
operator|.
name|getJMSDestination
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
if|if
condition|(
name|maMap
operator|==
literal|null
condition|)
block|{
name|maMap
operator|=
operator|new
name|HashMap
argument_list|<
name|Symbol
argument_list|,
name|Object
argument_list|>
argument_list|()
expr_stmt|;
block|}
name|maMap
operator|.
name|put
argument_list|(
name|Symbol
operator|.
name|valueOf
argument_list|(
literal|"x-opt-to-type"
argument_list|)
argument_list|,
name|destinationAttributes
argument_list|(
name|msg
operator|.
name|getJMSDestination
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|msg
operator|.
name|getJMSReplyTo
argument_list|()
operator|!=
literal|null
condition|)
block|{
name|props
operator|.
name|setReplyTo
argument_list|(
name|vendor
operator|.
name|toAddress
argument_list|(
name|msg
operator|.
name|getJMSReplyTo
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
if|if
condition|(
name|maMap
operator|==
literal|null
condition|)
block|{
name|maMap
operator|=
operator|new
name|HashMap
argument_list|<
name|Symbol
argument_list|,
name|Object
argument_list|>
argument_list|()
expr_stmt|;
block|}
name|maMap
operator|.
name|put
argument_list|(
name|Symbol
operator|.
name|valueOf
argument_list|(
literal|"x-opt-reply-type"
argument_list|)
argument_list|,
name|destinationAttributes
argument_list|(
name|msg
operator|.
name|getJMSReplyTo
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|msg
operator|.
name|getJMSCorrelationID
argument_list|()
operator|!=
literal|null
condition|)
block|{
name|props
operator|.
name|setCorrelationId
argument_list|(
name|msg
operator|.
name|getJMSCorrelationID
argument_list|()
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|msg
operator|.
name|getJMSExpiration
argument_list|()
operator|!=
literal|0
condition|)
block|{
name|long
name|ttl
init|=
name|msg
operator|.
name|getJMSExpiration
argument_list|()
operator|-
name|System
operator|.
name|currentTimeMillis
argument_list|()
decl_stmt|;
if|if
condition|(
name|ttl
operator|<
literal|0
condition|)
block|{
name|ttl
operator|=
literal|1
expr_stmt|;
block|}
name|header
operator|.
name|setTtl
argument_list|(
operator|new
name|UnsignedInteger
argument_list|(
operator|(
name|int
operator|)
name|ttl
argument_list|)
argument_list|)
expr_stmt|;
name|props
operator|.
name|setAbsoluteExpiryTime
argument_list|(
operator|new
name|Date
argument_list|(
name|msg
operator|.
name|getJMSExpiration
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|msg
operator|.
name|getJMSTimestamp
argument_list|()
operator|!=
literal|0
condition|)
block|{
name|props
operator|.
name|setCreationTime
argument_list|(
operator|new
name|Date
argument_list|(
name|msg
operator|.
name|getJMSTimestamp
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
specifier|final
name|Enumeration
argument_list|<
name|String
argument_list|>
name|keys
init|=
name|msg
operator|.
name|getPropertyNames
argument_list|()
decl_stmt|;
while|while
condition|(
name|keys
operator|.
name|hasMoreElements
argument_list|()
condition|)
block|{
name|String
name|key
init|=
name|keys
operator|.
name|nextElement
argument_list|()
decl_stmt|;
if|if
condition|(
name|key
operator|.
name|equals
argument_list|(
name|messageFormatKey
argument_list|)
operator|||
name|key
operator|.
name|equals
argument_list|(
name|nativeKey
argument_list|)
condition|)
block|{
comment|// skip..
block|}
elseif|else
if|if
condition|(
name|key
operator|.
name|equals
argument_list|(
name|firstAcquirerKey
argument_list|)
condition|)
block|{
name|header
operator|.
name|setFirstAcquirer
argument_list|(
name|msg
operator|.
name|getBooleanProperty
argument_list|(
name|key
argument_list|)
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|key
operator|.
name|startsWith
argument_list|(
literal|"JMSXDeliveryCount"
argument_list|)
condition|)
block|{
comment|// The AMQP delivery-count field only includes prior failed delivery attempts,
comment|// whereas JMSXDeliveryCount includes the first/current delivery attempt.
name|int
name|amqpDeliveryCount
init|=
name|msg
operator|.
name|getIntProperty
argument_list|(
name|key
argument_list|)
operator|-
literal|1
decl_stmt|;
if|if
condition|(
name|amqpDeliveryCount
operator|>
literal|0
condition|)
block|{
name|header
operator|.
name|setDeliveryCount
argument_list|(
operator|new
name|UnsignedInteger
argument_list|(
name|amqpDeliveryCount
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
elseif|else
if|if
condition|(
name|key
operator|.
name|startsWith
argument_list|(
literal|"JMSXUserID"
argument_list|)
condition|)
block|{
name|String
name|value
init|=
name|msg
operator|.
name|getStringProperty
argument_list|(
name|key
argument_list|)
decl_stmt|;
name|props
operator|.
name|setUserId
argument_list|(
operator|new
name|Binary
argument_list|(
name|value
operator|.
name|getBytes
argument_list|(
literal|"UTF-8"
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|key
operator|.
name|startsWith
argument_list|(
literal|"JMSXGroupID"
argument_list|)
condition|)
block|{
name|String
name|value
init|=
name|msg
operator|.
name|getStringProperty
argument_list|(
name|key
argument_list|)
decl_stmt|;
name|props
operator|.
name|setGroupId
argument_list|(
name|value
argument_list|)
expr_stmt|;
if|if
condition|(
name|apMap
operator|==
literal|null
condition|)
block|{
name|apMap
operator|=
operator|new
name|HashMap
argument_list|()
expr_stmt|;
block|}
name|apMap
operator|.
name|put
argument_list|(
name|key
argument_list|,
name|value
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|key
operator|.
name|startsWith
argument_list|(
literal|"JMSXGroupSeq"
argument_list|)
condition|)
block|{
name|UnsignedInteger
name|value
init|=
operator|new
name|UnsignedInteger
argument_list|(
name|msg
operator|.
name|getIntProperty
argument_list|(
name|key
argument_list|)
argument_list|)
decl_stmt|;
name|props
operator|.
name|setGroupSequence
argument_list|(
name|value
argument_list|)
expr_stmt|;
if|if
condition|(
name|apMap
operator|==
literal|null
condition|)
block|{
name|apMap
operator|=
operator|new
name|HashMap
argument_list|()
expr_stmt|;
block|}
name|apMap
operator|.
name|put
argument_list|(
name|key
argument_list|,
name|value
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|key
operator|.
name|startsWith
argument_list|(
name|prefixDeliveryAnnotationsKey
argument_list|)
condition|)
block|{
if|if
condition|(
name|daMap
operator|==
literal|null
condition|)
block|{
name|daMap
operator|=
operator|new
name|HashMap
argument_list|<
name|Symbol
argument_list|,
name|Object
argument_list|>
argument_list|()
expr_stmt|;
block|}
name|String
name|name
init|=
name|key
operator|.
name|substring
argument_list|(
name|prefixDeliveryAnnotationsKey
operator|.
name|length
argument_list|()
argument_list|)
decl_stmt|;
name|daMap
operator|.
name|put
argument_list|(
name|Symbol
operator|.
name|valueOf
argument_list|(
name|name
argument_list|)
argument_list|,
name|msg
operator|.
name|getObjectProperty
argument_list|(
name|key
argument_list|)
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|key
operator|.
name|startsWith
argument_list|(
name|prefixMessageAnnotationsKey
argument_list|)
condition|)
block|{
if|if
condition|(
name|maMap
operator|==
literal|null
condition|)
block|{
name|maMap
operator|=
operator|new
name|HashMap
argument_list|<
name|Symbol
argument_list|,
name|Object
argument_list|>
argument_list|()
expr_stmt|;
block|}
name|String
name|name
init|=
name|key
operator|.
name|substring
argument_list|(
name|prefixMessageAnnotationsKey
operator|.
name|length
argument_list|()
argument_list|)
decl_stmt|;
name|maMap
operator|.
name|put
argument_list|(
name|Symbol
operator|.
name|valueOf
argument_list|(
name|name
argument_list|)
argument_list|,
name|msg
operator|.
name|getObjectProperty
argument_list|(
name|key
argument_list|)
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|key
operator|.
name|equals
argument_list|(
name|subjectKey
argument_list|)
condition|)
block|{
name|props
operator|.
name|setSubject
argument_list|(
name|msg
operator|.
name|getStringProperty
argument_list|(
name|key
argument_list|)
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|key
operator|.
name|equals
argument_list|(
name|contentTypeKey
argument_list|)
condition|)
block|{
name|props
operator|.
name|setContentType
argument_list|(
name|Symbol
operator|.
name|getSymbol
argument_list|(
name|msg
operator|.
name|getStringProperty
argument_list|(
name|key
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|key
operator|.
name|equals
argument_list|(
name|contentEncodingKey
argument_list|)
condition|)
block|{
name|props
operator|.
name|setContentEncoding
argument_list|(
name|Symbol
operator|.
name|getSymbol
argument_list|(
name|msg
operator|.
name|getStringProperty
argument_list|(
name|key
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|key
operator|.
name|equals
argument_list|(
name|replyToGroupIDKey
argument_list|)
condition|)
block|{
name|props
operator|.
name|setReplyToGroupId
argument_list|(
name|msg
operator|.
name|getStringProperty
argument_list|(
name|key
argument_list|)
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|key
operator|.
name|startsWith
argument_list|(
name|prefixFooterKey
argument_list|)
condition|)
block|{
if|if
condition|(
name|footerMap
operator|==
literal|null
condition|)
block|{
name|footerMap
operator|=
operator|new
name|HashMap
argument_list|()
expr_stmt|;
block|}
name|String
name|name
init|=
name|key
operator|.
name|substring
argument_list|(
name|prefixFooterKey
operator|.
name|length
argument_list|()
argument_list|)
decl_stmt|;
name|footerMap
operator|.
name|put
argument_list|(
name|name
argument_list|,
name|msg
operator|.
name|getObjectProperty
argument_list|(
name|key
argument_list|)
argument_list|)
expr_stmt|;
block|}
else|else
block|{
if|if
condition|(
name|apMap
operator|==
literal|null
condition|)
block|{
name|apMap
operator|=
operator|new
name|HashMap
argument_list|()
expr_stmt|;
block|}
name|apMap
operator|.
name|put
argument_list|(
name|key
argument_list|,
name|msg
operator|.
name|getObjectProperty
argument_list|(
name|key
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
name|MessageAnnotations
name|ma
init|=
literal|null
decl_stmt|;
if|if
condition|(
name|maMap
operator|!=
literal|null
condition|)
block|{
name|ma
operator|=
operator|new
name|MessageAnnotations
argument_list|(
name|maMap
argument_list|)
expr_stmt|;
block|}
name|DeliveryAnnotations
name|da
init|=
literal|null
decl_stmt|;
if|if
condition|(
name|daMap
operator|!=
literal|null
condition|)
block|{
name|da
operator|=
operator|new
name|DeliveryAnnotations
argument_list|(
name|daMap
argument_list|)
expr_stmt|;
block|}
name|ApplicationProperties
name|ap
init|=
literal|null
decl_stmt|;
if|if
condition|(
name|apMap
operator|!=
literal|null
condition|)
block|{
name|ap
operator|=
operator|new
name|ApplicationProperties
argument_list|(
name|apMap
argument_list|)
expr_stmt|;
block|}
name|Footer
name|footer
init|=
literal|null
decl_stmt|;
if|if
condition|(
name|footerMap
operator|!=
literal|null
condition|)
block|{
name|footer
operator|=
operator|new
name|Footer
argument_list|(
name|footerMap
argument_list|)
expr_stmt|;
block|}
return|return
operator|(
name|ProtonJMessage
operator|)
name|org
operator|.
name|apache
operator|.
name|qpid
operator|.
name|proton
operator|.
name|message
operator|.
name|Message
operator|.
name|Factory
operator|.
name|create
argument_list|(
name|header
argument_list|,
name|da
argument_list|,
name|ma
argument_list|,
name|props
argument_list|,
name|ap
argument_list|,
name|body
argument_list|,
name|footer
argument_list|)
return|;
block|}
specifier|private
specifier|static
name|String
name|destinationAttributes
parameter_list|(
name|Destination
name|destination
parameter_list|)
block|{
if|if
condition|(
name|destination
operator|instanceof
name|Queue
condition|)
block|{
if|if
condition|(
name|destination
operator|instanceof
name|TemporaryQueue
condition|)
block|{
return|return
literal|"temporary,queue"
return|;
block|}
else|else
block|{
return|return
literal|"queue"
return|;
block|}
block|}
if|if
condition|(
name|destination
operator|instanceof
name|Topic
condition|)
block|{
if|if
condition|(
name|destination
operator|instanceof
name|TemporaryTopic
condition|)
block|{
return|return
literal|"temporary,topic"
return|;
block|}
else|else
block|{
return|return
literal|"topic"
return|;
block|}
block|}
return|return
literal|""
return|;
block|}
block|}
end_class

end_unit
