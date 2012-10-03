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

begin_import
import|import
name|javax
operator|.
name|jms
operator|.
name|MessageFormatException
import|;
end_import

begin_comment
comment|/** * @author<a href="http://hiramchirino.com">Hiram Chirino</a> */
end_comment

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
name|byte
index|[]
name|transform
parameter_list|(
name|Message
name|jms
parameter_list|)
throws|throws
name|Exception
block|{
if|if
condition|(
name|jms
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
name|jms
operator|instanceof
name|BytesMessage
operator|)
condition|)
return|return
literal|null
return|;
name|long
name|messageFormat
decl_stmt|;
try|try
block|{
if|if
condition|(
operator|!
name|jms
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
name|messageFormat
operator|=
name|jms
operator|.
name|getLongProperty
argument_list|(
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
comment|// TODO: Proton should probably expose a way to set the msg format
comment|// delivery.settMessageFormat(messageFormat);
name|BytesMessage
name|bytesMessage
init|=
operator|(
name|BytesMessage
operator|)
name|jms
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
name|bytesMessage
operator|.
name|getBodyLength
argument_list|()
index|]
decl_stmt|;
name|bytesMessage
operator|.
name|readBytes
argument_list|(
name|data
argument_list|)
expr_stmt|;
return|return
name|data
return|;
block|}
block|}
end_class

end_unit

