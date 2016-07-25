begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *      http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
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
name|Message
import|;
end_import

begin_class
specifier|public
class|class
name|AMQPRawInboundTransformer
extends|extends
name|InboundTransformer
block|{
specifier|public
name|AMQPRawInboundTransformer
parameter_list|(
name|ActiveMQJMSVendor
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
name|String
name|getTransformerName
parameter_list|()
block|{
return|return
name|TRANSFORMER_RAW
return|;
block|}
annotation|@
name|Override
specifier|public
name|InboundTransformer
name|getFallbackTransformer
parameter_list|()
block|{
return|return
literal|null
return|;
comment|// No fallback from full raw transform, message likely dropped.
block|}
annotation|@
name|Override
specifier|protected
name|Message
name|doTransform
parameter_list|(
name|EncodedMessage
name|amqpMessage
parameter_list|)
throws|throws
name|Exception
block|{
name|BytesMessage
name|result
init|=
name|vendor
operator|.
name|createBytesMessage
argument_list|(
name|amqpMessage
operator|.
name|getArray
argument_list|()
argument_list|,
name|amqpMessage
operator|.
name|getArrayOffset
argument_list|()
argument_list|,
name|amqpMessage
operator|.
name|getLength
argument_list|()
argument_list|)
decl_stmt|;
comment|// We cannot decode the message headers to check so err on the side of caution
comment|// and mark all messages as persistent.
name|result
operator|.
name|setJMSDeliveryMode
argument_list|(
name|DeliveryMode
operator|.
name|PERSISTENT
argument_list|)
expr_stmt|;
name|result
operator|.
name|setJMSPriority
argument_list|(
name|defaultPriority
argument_list|)
expr_stmt|;
specifier|final
name|long
name|now
init|=
name|System
operator|.
name|currentTimeMillis
argument_list|()
decl_stmt|;
name|result
operator|.
name|setJMSTimestamp
argument_list|(
name|now
argument_list|)
expr_stmt|;
if|if
condition|(
name|defaultTtl
operator|>
literal|0
condition|)
block|{
name|result
operator|.
name|setJMSExpiration
argument_list|(
name|now
operator|+
name|defaultTtl
argument_list|)
expr_stmt|;
block|}
name|result
operator|.
name|setLongProperty
argument_list|(
name|prefixVendor
operator|+
literal|"MESSAGE_FORMAT"
argument_list|,
name|amqpMessage
operator|.
name|getMessageFormat
argument_list|()
argument_list|)
expr_stmt|;
name|result
operator|.
name|setBooleanProperty
argument_list|(
name|prefixVendor
operator|+
literal|"NATIVE"
argument_list|,
literal|true
argument_list|)
expr_stmt|;
return|return
name|result
return|;
block|}
block|}
end_class

end_unit

