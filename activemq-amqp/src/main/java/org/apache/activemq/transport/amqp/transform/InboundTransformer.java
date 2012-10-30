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
name|org
operator|.
name|apache
operator|.
name|qpid
operator|.
name|proton
operator|.
name|type
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
name|type
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
name|type
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
name|type
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
name|type
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
name|type
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
name|type
operator|.
name|messaging
operator|.
name|Properties
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
name|Map
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Set
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
specifier|public
specifier|static
specifier|final
name|String
name|TRANSFORMER_NATIVE
init|=
literal|"native"
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|String
name|TRANSFORMER_RAW
init|=
literal|"raw"
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|String
name|TRANSFORMER_JMS
init|=
literal|"jms"
decl_stmt|;
name|String
name|prefixVendor
init|=
literal|"JMS_AMQP_"
decl_stmt|;
name|String
name|prefixDeliveryAnnotations
init|=
literal|"DA_"
decl_stmt|;
name|String
name|prefixMessageAnnotations
init|=
literal|"MA_"
decl_stmt|;
name|String
name|prefixFooter
init|=
literal|"FT_"
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
name|EncodedMessage
name|amqpMessage
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
specifier|protected
name|void
name|populateMessage
parameter_list|(
name|Message
name|jms
parameter_list|,
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
name|amqp
parameter_list|)
throws|throws
name|Exception
block|{
name|Header
name|header
init|=
name|amqp
operator|.
name|getHeader
argument_list|()
decl_stmt|;
if|if
condition|(
name|header
operator|==
literal|null
condition|)
block|{
name|header
operator|=
operator|new
name|Header
argument_list|()
expr_stmt|;
block|}
if|if
condition|(
name|header
operator|.
name|getDurable
argument_list|()
operator|!=
literal|null
condition|)
block|{
name|jms
operator|.
name|setJMSDeliveryMode
argument_list|(
name|header
operator|.
name|getDurable
argument_list|()
operator|.
name|booleanValue
argument_list|()
condition|?
name|DeliveryMode
operator|.
name|PERSISTENT
else|:
name|DeliveryMode
operator|.
name|NON_PERSISTENT
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|jms
operator|.
name|setJMSDeliveryMode
argument_list|(
name|defaultDeliveryMode
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|header
operator|.
name|getPriority
argument_list|()
operator|!=
literal|null
condition|)
block|{
name|jms
operator|.
name|setJMSPriority
argument_list|(
name|header
operator|.
name|getPriority
argument_list|()
operator|.
name|intValue
argument_list|()
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|jms
operator|.
name|setJMSPriority
argument_list|(
name|defaultPriority
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|header
operator|.
name|getTtl
argument_list|()
operator|!=
literal|null
condition|)
block|{
name|jms
operator|.
name|setJMSExpiration
argument_list|(
name|header
operator|.
name|getTtl
argument_list|()
operator|.
name|longValue
argument_list|()
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|jms
operator|.
name|setJMSExpiration
argument_list|(
name|defaultTtl
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|header
operator|.
name|getFirstAcquirer
argument_list|()
operator|!=
literal|null
condition|)
block|{
name|jms
operator|.
name|setBooleanProperty
argument_list|(
name|prefixVendor
operator|+
literal|"FirstAcquirer"
argument_list|,
name|header
operator|.
name|getFirstAcquirer
argument_list|()
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|header
operator|.
name|getDeliveryCount
argument_list|()
operator|!=
literal|null
condition|)
block|{
name|vendor
operator|.
name|setJMSXDeliveryCount
argument_list|(
name|jms
argument_list|,
name|header
operator|.
name|getDeliveryCount
argument_list|()
operator|.
name|longValue
argument_list|()
argument_list|)
expr_stmt|;
block|}
specifier|final
name|DeliveryAnnotations
name|da
init|=
name|amqp
operator|.
name|getDeliveryAnnotations
argument_list|()
decl_stmt|;
if|if
condition|(
name|da
operator|!=
literal|null
condition|)
block|{
for|for
control|(
name|Map
operator|.
name|Entry
name|entry
range|:
operator|(
name|Set
argument_list|<
name|Map
operator|.
name|Entry
argument_list|>
operator|)
name|da
operator|.
name|getValue
argument_list|()
operator|.
name|entrySet
argument_list|()
control|)
block|{
name|String
name|key
init|=
name|entry
operator|.
name|getKey
argument_list|()
operator|.
name|toString
argument_list|()
decl_stmt|;
name|setProperty
argument_list|(
name|jms
argument_list|,
name|prefixVendor
operator|+
name|prefixDeliveryAnnotations
operator|+
name|key
argument_list|,
name|entry
operator|.
name|getValue
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
specifier|final
name|MessageAnnotations
name|ma
init|=
name|amqp
operator|.
name|getMessageAnnotations
argument_list|()
decl_stmt|;
if|if
condition|(
name|ma
operator|!=
literal|null
condition|)
block|{
for|for
control|(
name|Map
operator|.
name|Entry
name|entry
range|:
operator|(
name|Set
argument_list|<
name|Map
operator|.
name|Entry
argument_list|>
operator|)
name|ma
operator|.
name|getValue
argument_list|()
operator|.
name|entrySet
argument_list|()
control|)
block|{
name|String
name|key
init|=
name|entry
operator|.
name|getKey
argument_list|()
operator|.
name|toString
argument_list|()
decl_stmt|;
name|setProperty
argument_list|(
name|jms
argument_list|,
name|prefixVendor
operator|+
name|prefixMessageAnnotations
operator|+
name|key
argument_list|,
name|entry
operator|.
name|getValue
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
specifier|final
name|Properties
name|properties
init|=
name|amqp
operator|.
name|getProperties
argument_list|()
decl_stmt|;
if|if
condition|(
name|properties
operator|!=
literal|null
condition|)
block|{
if|if
condition|(
name|properties
operator|.
name|getMessageId
argument_list|()
operator|!=
literal|null
condition|)
block|{
name|jms
operator|.
name|setJMSMessageID
argument_list|(
name|properties
operator|.
name|getMessageId
argument_list|()
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|Binary
name|userId
init|=
name|properties
operator|.
name|getUserId
argument_list|()
decl_stmt|;
if|if
condition|(
name|userId
operator|!=
literal|null
condition|)
block|{
name|vendor
operator|.
name|setJMSXUserID
argument_list|(
name|jms
argument_list|,
operator|new
name|String
argument_list|(
name|userId
operator|.
name|getArray
argument_list|()
argument_list|,
name|userId
operator|.
name|getArrayOffset
argument_list|()
argument_list|,
name|userId
operator|.
name|getLength
argument_list|()
argument_list|,
literal|"UTF-8"
argument_list|)
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|properties
operator|.
name|getTo
argument_list|()
operator|!=
literal|null
condition|)
block|{
name|jms
operator|.
name|setJMSDestination
argument_list|(
name|vendor
operator|.
name|createDestination
argument_list|(
name|properties
operator|.
name|getTo
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|properties
operator|.
name|getSubject
argument_list|()
operator|!=
literal|null
condition|)
block|{
name|jms
operator|.
name|setStringProperty
argument_list|(
name|prefixVendor
operator|+
literal|"Subject"
argument_list|,
name|properties
operator|.
name|getSubject
argument_list|()
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|properties
operator|.
name|getReplyTo
argument_list|()
operator|!=
literal|null
condition|)
block|{
name|jms
operator|.
name|setJMSReplyTo
argument_list|(
name|vendor
operator|.
name|createDestination
argument_list|(
name|properties
operator|.
name|getReplyTo
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|properties
operator|.
name|getCorrelationId
argument_list|()
operator|!=
literal|null
condition|)
block|{
name|jms
operator|.
name|setJMSCorrelationID
argument_list|(
name|properties
operator|.
name|getCorrelationId
argument_list|()
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|properties
operator|.
name|getContentType
argument_list|()
operator|!=
literal|null
condition|)
block|{
name|jms
operator|.
name|setStringProperty
argument_list|(
name|prefixVendor
operator|+
literal|"ContentType"
argument_list|,
name|properties
operator|.
name|getContentType
argument_list|()
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|properties
operator|.
name|getContentEncoding
argument_list|()
operator|!=
literal|null
condition|)
block|{
name|jms
operator|.
name|setStringProperty
argument_list|(
name|prefixVendor
operator|+
literal|"ContentEncoding"
argument_list|,
name|properties
operator|.
name|getContentEncoding
argument_list|()
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|properties
operator|.
name|getCreationTime
argument_list|()
operator|!=
literal|null
condition|)
block|{
name|jms
operator|.
name|setJMSTimestamp
argument_list|(
name|properties
operator|.
name|getCreationTime
argument_list|()
operator|.
name|getTime
argument_list|()
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|properties
operator|.
name|getGroupId
argument_list|()
operator|!=
literal|null
condition|)
block|{
name|vendor
operator|.
name|setJMSXGroupID
argument_list|(
name|jms
argument_list|,
name|properties
operator|.
name|getGroupId
argument_list|()
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|properties
operator|.
name|getGroupSequence
argument_list|()
operator|!=
literal|null
condition|)
block|{
name|vendor
operator|.
name|setJMSXGroupSequence
argument_list|(
name|jms
argument_list|,
name|properties
operator|.
name|getGroupSequence
argument_list|()
operator|.
name|intValue
argument_list|()
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|properties
operator|.
name|getReplyToGroupId
argument_list|()
operator|!=
literal|null
condition|)
block|{
name|jms
operator|.
name|setStringProperty
argument_list|(
name|prefixVendor
operator|+
literal|"ReplyToGroupID"
argument_list|,
name|properties
operator|.
name|getReplyToGroupId
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
specifier|final
name|ApplicationProperties
name|ap
init|=
name|amqp
operator|.
name|getApplicationProperties
argument_list|()
decl_stmt|;
if|if
condition|(
name|ap
operator|!=
literal|null
condition|)
block|{
for|for
control|(
name|Map
operator|.
name|Entry
name|entry
range|:
operator|(
name|Set
argument_list|<
name|Map
operator|.
name|Entry
argument_list|>
operator|)
name|ap
operator|.
name|getValue
argument_list|()
operator|.
name|entrySet
argument_list|()
control|)
block|{
name|String
name|key
init|=
name|entry
operator|.
name|getKey
argument_list|()
operator|.
name|toString
argument_list|()
decl_stmt|;
name|setProperty
argument_list|(
name|jms
argument_list|,
name|key
argument_list|,
name|entry
operator|.
name|getValue
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
specifier|final
name|Footer
name|fp
init|=
name|amqp
operator|.
name|getFooter
argument_list|()
decl_stmt|;
if|if
condition|(
name|fp
operator|!=
literal|null
condition|)
block|{
for|for
control|(
name|Map
operator|.
name|Entry
name|entry
range|:
operator|(
name|Set
argument_list|<
name|Map
operator|.
name|Entry
argument_list|>
operator|)
name|fp
operator|.
name|getValue
argument_list|()
operator|.
name|entrySet
argument_list|()
control|)
block|{
name|String
name|key
init|=
name|entry
operator|.
name|getKey
argument_list|()
operator|.
name|toString
argument_list|()
decl_stmt|;
name|setProperty
argument_list|(
name|jms
argument_list|,
name|prefixVendor
operator|+
name|prefixFooter
operator|+
name|key
argument_list|,
name|entry
operator|.
name|getValue
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
block|}
specifier|private
name|void
name|setProperty
parameter_list|(
name|Message
name|msg
parameter_list|,
name|String
name|key
parameter_list|,
name|Object
name|value
parameter_list|)
throws|throws
name|JMSException
block|{
comment|//TODO support all types
name|msg
operator|.
name|setObjectProperty
argument_list|(
name|key
argument_list|,
name|value
argument_list|)
expr_stmt|;
comment|//        if( value instanceof String ) {
comment|//            msg.setStringProperty(key, (String) value);
comment|//        } else if( value instanceof Double ) {
comment|//            msg.setDoubleProperty(key, ((Double) value).doubleValue());
comment|//        } else if( value instanceof Integer ) {
comment|//            msg.setIntProperty(key, ((Integer) value).intValue());
comment|//        } else if( value instanceof Long ) {
comment|//            msg.setLongProperty(key, ((Long) value).longValue());
comment|//        } else {
comment|//            throw new RuntimeException("Unexpected value type: "+value.getClass());
comment|//        }
block|}
block|}
end_class

end_unit

