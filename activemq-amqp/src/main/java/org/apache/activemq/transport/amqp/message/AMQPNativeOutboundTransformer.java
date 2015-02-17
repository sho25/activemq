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
name|nio
operator|.
name|ByteBuffer
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
name|JMSException
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
name|MessageFormatException
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
name|AMQPNativeOutboundTransformer
extends|extends
name|OutboundTransformer
block|{
specifier|public
name|AMQPNativeOutboundTransformer
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
return|return
literal|null
return|;
if|if
condition|(
operator|!
operator|(
name|msg
operator|instanceof
name|BytesMessage
operator|)
condition|)
return|return
literal|null
return|;
try|try
block|{
if|if
condition|(
operator|!
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
return|return
name|transform
argument_list|(
name|this
argument_list|,
operator|(
name|BytesMessage
operator|)
name|msg
argument_list|)
return|;
block|}
specifier|static
name|EncodedMessage
name|transform
parameter_list|(
name|OutboundTransformer
name|options
parameter_list|,
name|BytesMessage
name|msg
parameter_list|)
throws|throws
name|JMSException
block|{
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
name|options
operator|.
name|prefixVendor
operator|+
literal|"MESSAGE_FORMAT"
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
name|msg
operator|.
name|getBodyLength
argument_list|()
index|]
decl_stmt|;
name|int
name|dataSize
init|=
name|data
operator|.
name|length
decl_stmt|;
name|msg
operator|.
name|readBytes
argument_list|(
name|data
argument_list|)
expr_stmt|;
name|msg
operator|.
name|reset
argument_list|()
expr_stmt|;
try|try
block|{
name|int
name|count
init|=
name|msg
operator|.
name|getIntProperty
argument_list|(
literal|"JMSXDeliveryCount"
argument_list|)
decl_stmt|;
if|if
condition|(
name|count
operator|>
literal|1
condition|)
block|{
comment|// decode...
name|ProtonJMessage
name|amqp
init|=
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
argument_list|()
decl_stmt|;
name|int
name|offset
init|=
literal|0
decl_stmt|;
name|int
name|len
init|=
name|data
operator|.
name|length
decl_stmt|;
while|while
condition|(
name|len
operator|>
literal|0
condition|)
block|{
specifier|final
name|int
name|decoded
init|=
name|amqp
operator|.
name|decode
argument_list|(
name|data
argument_list|,
name|offset
argument_list|,
name|len
argument_list|)
decl_stmt|;
assert|assert
name|decoded
operator|>
literal|0
operator|:
literal|"Make progress decoding the message"
assert|;
name|offset
operator|+=
name|decoded
expr_stmt|;
name|len
operator|-=
name|decoded
expr_stmt|;
block|}
comment|// Update the DeliveryCount header...
comment|// The AMQP delivery-count field only includes prior failed delivery attempts,
comment|// whereas JMSXDeliveryCount includes the first/current delivery attempt. Subtract 1.
name|amqp
operator|.
name|getHeader
argument_list|()
operator|.
name|setDeliveryCount
argument_list|(
operator|new
name|UnsignedInteger
argument_list|(
name|count
operator|-
literal|1
argument_list|)
argument_list|)
expr_stmt|;
comment|// Re-encode...
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
name|data
operator|=
name|buffer
operator|.
name|array
argument_list|()
expr_stmt|;
name|dataSize
operator|=
name|c
expr_stmt|;
block|}
block|}
catch|catch
parameter_list|(
name|JMSException
name|e
parameter_list|)
block|{         }
return|return
operator|new
name|EncodedMessage
argument_list|(
name|messageFormat
argument_list|,
name|data
argument_list|,
literal|0
argument_list|,
name|dataSize
argument_list|)
return|;
block|}
block|}
end_class

end_unit

