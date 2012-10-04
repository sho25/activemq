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
name|transform
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
name|Message
import|;
end_import

begin_comment
comment|/** * @author<a href="http://hiramchirino.com">Hiram Chirino</a> */
end_comment

begin_class
specifier|public
class|class
name|AMQPNativeInboundTransformer
extends|extends
name|InboundTransformer
block|{
specifier|public
name|AMQPNativeInboundTransformer
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
name|Message
name|transform
parameter_list|(
name|EncodedMessage
name|amqpMessage
parameter_list|)
throws|throws
name|Exception
block|{
name|BytesMessage
name|rc
init|=
name|vendor
operator|.
name|createBytesMessage
argument_list|()
decl_stmt|;
name|rc
operator|.
name|writeBytes
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
expr_stmt|;
name|rc
operator|.
name|setJMSDeliveryMode
argument_list|(
name|defaultDeliveryMode
argument_list|)
expr_stmt|;
name|rc
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
name|rc
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
name|rc
operator|.
name|setJMSExpiration
argument_list|(
name|now
operator|+
name|defaultTtl
argument_list|)
expr_stmt|;
block|}
name|rc
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
name|rc
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
name|rc
return|;
block|}
block|}
end_class

end_unit

