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
name|Message
import|;
end_import

begin_class
specifier|public
specifier|abstract
class|class
name|OutboundTransformer
block|{
specifier|protected
specifier|final
name|ActiveMQJMSVendor
name|vendor
decl_stmt|;
specifier|protected
name|String
name|prefixVendor
decl_stmt|;
specifier|protected
name|String
name|prefixDeliveryAnnotations
init|=
literal|"DA_"
decl_stmt|;
specifier|protected
name|String
name|prefixMessageAnnotations
init|=
literal|"MA_"
decl_stmt|;
specifier|protected
name|String
name|prefixFooter
init|=
literal|"FT_"
decl_stmt|;
specifier|protected
name|String
name|messageFormatKey
decl_stmt|;
specifier|protected
name|String
name|nativeKey
decl_stmt|;
specifier|protected
name|String
name|firstAcquirerKey
decl_stmt|;
specifier|protected
name|String
name|prefixDeliveryAnnotationsKey
decl_stmt|;
specifier|protected
name|String
name|prefixMessageAnnotationsKey
decl_stmt|;
specifier|protected
name|String
name|contentTypeKey
decl_stmt|;
specifier|protected
name|String
name|contentEncodingKey
decl_stmt|;
specifier|protected
name|String
name|replyToGroupIDKey
decl_stmt|;
specifier|protected
name|String
name|prefixFooterKey
decl_stmt|;
specifier|public
name|OutboundTransformer
parameter_list|(
name|ActiveMQJMSVendor
name|vendor
parameter_list|)
block|{
name|this
operator|.
name|vendor
operator|=
name|vendor
expr_stmt|;
name|this
operator|.
name|setPrefixVendor
argument_list|(
literal|"JMS_AMQP_"
argument_list|)
expr_stmt|;
block|}
specifier|public
specifier|abstract
name|EncodedMessage
name|transform
parameter_list|(
name|Message
name|jms
parameter_list|)
throws|throws
name|Exception
function_decl|;
specifier|public
name|String
name|getPrefixVendor
parameter_list|()
block|{
return|return
name|prefixVendor
return|;
block|}
specifier|public
name|void
name|setPrefixVendor
parameter_list|(
name|String
name|prefixVendor
parameter_list|)
block|{
name|this
operator|.
name|prefixVendor
operator|=
name|prefixVendor
expr_stmt|;
name|messageFormatKey
operator|=
name|prefixVendor
operator|+
literal|"MESSAGE_FORMAT"
expr_stmt|;
name|nativeKey
operator|=
name|prefixVendor
operator|+
literal|"NATIVE"
expr_stmt|;
name|firstAcquirerKey
operator|=
name|prefixVendor
operator|+
literal|"FirstAcquirer"
expr_stmt|;
name|prefixDeliveryAnnotationsKey
operator|=
name|prefixVendor
operator|+
name|prefixDeliveryAnnotations
expr_stmt|;
name|prefixMessageAnnotationsKey
operator|=
name|prefixVendor
operator|+
name|prefixMessageAnnotations
expr_stmt|;
name|contentTypeKey
operator|=
name|prefixVendor
operator|+
literal|"ContentType"
expr_stmt|;
name|contentEncodingKey
operator|=
name|prefixVendor
operator|+
literal|"ContentEncoding"
expr_stmt|;
name|replyToGroupIDKey
operator|=
name|prefixVendor
operator|+
literal|"ReplyToGroupID"
expr_stmt|;
name|prefixFooterKey
operator|=
name|prefixVendor
operator|+
name|prefixFooter
expr_stmt|;
block|}
specifier|public
name|ActiveMQJMSVendor
name|getVendor
parameter_list|()
block|{
return|return
name|vendor
return|;
block|}
block|}
end_class

end_unit

