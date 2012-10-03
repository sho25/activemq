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
name|org
operator|.
name|apache
operator|.
name|qpid
operator|.
name|proton
operator|.
name|engine
operator|.
name|Delivery
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
name|java
operator|.
name|io
operator|.
name|IOException
import|;
end_import

begin_comment
comment|/** * @author<a href="http://hiramchirino.com">Hiram Chirino</a> */
end_comment

begin_class
specifier|public
specifier|abstract
class|class
name|InboundTransformer
block|{
name|JMSVendor
name|vendor
decl_stmt|;
name|String
name|prefixVendor
init|=
literal|"JMS_AMQP_"
decl_stmt|;
name|int
name|defaultDeliveryMode
init|=
name|javax
operator|.
name|jms
operator|.
name|Message
operator|.
name|DEFAULT_DELIVERY_MODE
decl_stmt|;
name|int
name|defaultPriority
init|=
name|javax
operator|.
name|jms
operator|.
name|Message
operator|.
name|DEFAULT_PRIORITY
decl_stmt|;
name|long
name|defaultTtl
init|=
name|javax
operator|.
name|jms
operator|.
name|Message
operator|.
name|DEFAULT_TIME_TO_LIVE
decl_stmt|;
specifier|public
name|InboundTransformer
parameter_list|(
name|JMSVendor
name|vendor
parameter_list|)
block|{
name|this
operator|.
name|vendor
operator|=
name|vendor
expr_stmt|;
block|}
specifier|abstract
specifier|public
name|Message
name|transform
parameter_list|(
name|long
name|messageFormat
parameter_list|,
name|byte
index|[]
name|data
parameter_list|,
name|int
name|offset
parameter_list|,
name|int
name|len
parameter_list|)
throws|throws
name|Exception
function_decl|;
specifier|public
name|int
name|getDefaultDeliveryMode
parameter_list|()
block|{
return|return
name|defaultDeliveryMode
return|;
block|}
specifier|public
name|void
name|setDefaultDeliveryMode
parameter_list|(
name|int
name|defaultDeliveryMode
parameter_list|)
block|{
name|this
operator|.
name|defaultDeliveryMode
operator|=
name|defaultDeliveryMode
expr_stmt|;
block|}
specifier|public
name|int
name|getDefaultPriority
parameter_list|()
block|{
return|return
name|defaultPriority
return|;
block|}
specifier|public
name|void
name|setDefaultPriority
parameter_list|(
name|int
name|defaultPriority
parameter_list|)
block|{
name|this
operator|.
name|defaultPriority
operator|=
name|defaultPriority
expr_stmt|;
block|}
specifier|public
name|long
name|getDefaultTtl
parameter_list|()
block|{
return|return
name|defaultTtl
return|;
block|}
specifier|public
name|void
name|setDefaultTtl
parameter_list|(
name|long
name|defaultTtl
parameter_list|)
block|{
name|this
operator|.
name|defaultTtl
operator|=
name|defaultTtl
expr_stmt|;
block|}
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
block|}
specifier|public
name|JMSVendor
name|getVendor
parameter_list|()
block|{
return|return
name|vendor
return|;
block|}
specifier|public
name|void
name|setVendor
parameter_list|(
name|JMSVendor
name|vendor
parameter_list|)
block|{
name|this
operator|.
name|vendor
operator|=
name|vendor
expr_stmt|;
block|}
block|}
end_class

end_unit

