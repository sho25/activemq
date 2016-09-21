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
import|import static
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
operator|.
name|AmqpMessageSupport
operator|.
name|JMS_AMQP_NATIVE
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
name|ActiveMQMessage
import|;
end_import

begin_class
specifier|public
class|class
name|AutoOutboundTransformer
extends|extends
name|JMSMappingOutboundTransformer
block|{
specifier|private
specifier|final
name|JMSMappingOutboundTransformer
name|transformer
init|=
operator|new
name|JMSMappingOutboundTransformer
argument_list|()
decl_stmt|;
annotation|@
name|Override
specifier|public
name|EncodedMessage
name|transform
parameter_list|(
name|ActiveMQMessage
name|message
parameter_list|)
throws|throws
name|Exception
block|{
if|if
condition|(
name|message
operator|==
literal|null
condition|)
block|{
return|return
literal|null
return|;
block|}
if|if
condition|(
name|message
operator|.
name|getBooleanProperty
argument_list|(
name|JMS_AMQP_NATIVE
argument_list|)
condition|)
block|{
if|if
condition|(
name|message
operator|instanceof
name|BytesMessage
condition|)
block|{
return|return
name|AMQPNativeOutboundTransformer
operator|.
name|transform
argument_list|(
name|this
argument_list|,
operator|(
name|ActiveMQBytesMessage
operator|)
name|message
argument_list|)
return|;
block|}
else|else
block|{
return|return
literal|null
return|;
block|}
block|}
else|else
block|{
return|return
name|transformer
operator|.
name|transform
argument_list|(
name|message
argument_list|)
return|;
block|}
block|}
block|}
end_class

end_unit

