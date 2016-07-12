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
name|client
package|;
end_package

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
name|java
operator|.
name|util
operator|.
name|NoSuchElementException
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
name|transport
operator|.
name|amqp
operator|.
name|client
operator|.
name|util
operator|.
name|UnmodifiableDelivery
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
name|Proton
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
name|DescribedType
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
name|message
operator|.
name|Message
import|;
end_import

begin_class
specifier|public
class|class
name|AmqpMessage
block|{
specifier|private
specifier|final
name|AmqpReceiver
name|receiver
decl_stmt|;
specifier|private
specifier|final
name|Message
name|message
decl_stmt|;
specifier|private
specifier|final
name|Delivery
name|delivery
decl_stmt|;
specifier|private
name|Map
argument_list|<
name|Symbol
argument_list|,
name|Object
argument_list|>
name|deliveryAnnotationsMap
decl_stmt|;
specifier|private
name|Map
argument_list|<
name|Symbol
argument_list|,
name|Object
argument_list|>
name|messageAnnotationsMap
decl_stmt|;
specifier|private
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|applicationPropertiesMap
decl_stmt|;
comment|/**      * Creates a new AmqpMessage that wraps the information necessary to handle      * an outgoing message.      */
specifier|public
name|AmqpMessage
parameter_list|()
block|{
name|receiver
operator|=
literal|null
expr_stmt|;
name|delivery
operator|=
literal|null
expr_stmt|;
name|message
operator|=
name|Proton
operator|.
name|message
argument_list|()
expr_stmt|;
block|}
comment|/**      * Creates a new AmqpMessage that wraps the information necessary to handle      * an outgoing message.      *      * @param message      *        the Proton message that is to be sent.      */
specifier|public
name|AmqpMessage
parameter_list|(
name|Message
name|message
parameter_list|)
block|{
name|this
argument_list|(
literal|null
argument_list|,
name|message
argument_list|,
literal|null
argument_list|)
expr_stmt|;
block|}
comment|/**      * Creates a new AmqpMessage that wraps the information necessary to handle      * an incoming delivery.      *      * @param receiver      *        the AmqpReceiver that received this message.      * @param message      *        the Proton message that was received.      * @param delivery      *        the Delivery instance that produced this message.      */
annotation|@
name|SuppressWarnings
argument_list|(
literal|"unchecked"
argument_list|)
specifier|public
name|AmqpMessage
parameter_list|(
name|AmqpReceiver
name|receiver
parameter_list|,
name|Message
name|message
parameter_list|,
name|Delivery
name|delivery
parameter_list|)
block|{
name|this
operator|.
name|receiver
operator|=
name|receiver
expr_stmt|;
name|this
operator|.
name|message
operator|=
name|message
expr_stmt|;
name|this
operator|.
name|delivery
operator|=
name|delivery
expr_stmt|;
if|if
condition|(
name|message
operator|.
name|getMessageAnnotations
argument_list|()
operator|!=
literal|null
condition|)
block|{
name|messageAnnotationsMap
operator|=
name|message
operator|.
name|getMessageAnnotations
argument_list|()
operator|.
name|getValue
argument_list|()
expr_stmt|;
block|}
if|if
condition|(
name|message
operator|.
name|getApplicationProperties
argument_list|()
operator|!=
literal|null
condition|)
block|{
name|applicationPropertiesMap
operator|=
name|message
operator|.
name|getApplicationProperties
argument_list|()
operator|.
name|getValue
argument_list|()
expr_stmt|;
block|}
if|if
condition|(
name|message
operator|.
name|getDeliveryAnnotations
argument_list|()
operator|!=
literal|null
condition|)
block|{
name|deliveryAnnotationsMap
operator|=
name|message
operator|.
name|getDeliveryAnnotations
argument_list|()
operator|.
name|getValue
argument_list|()
expr_stmt|;
block|}
block|}
comment|//----- Access to interal client resources -------------------------------//
comment|/**      * @return the AMQP Delivery object linked to a received message.      */
specifier|public
name|Delivery
name|getWrappedDelivery
parameter_list|()
block|{
if|if
condition|(
name|delivery
operator|!=
literal|null
condition|)
block|{
return|return
operator|new
name|UnmodifiableDelivery
argument_list|(
name|delivery
argument_list|)
return|;
block|}
return|return
literal|null
return|;
block|}
comment|/**      * @return the AMQP Message that is wrapped by this object.      */
specifier|public
name|Message
name|getWrappedMessage
parameter_list|()
block|{
return|return
name|message
return|;
block|}
comment|/**      * @return the AmqpReceiver that consumed this message.      */
specifier|public
name|AmqpReceiver
name|getAmqpReceiver
parameter_list|()
block|{
return|return
name|receiver
return|;
block|}
comment|//----- Message disposition control --------------------------------------//
comment|/**      * Accepts the message marking it as consumed on the remote peer.      *      * @throws Exception if an error occurs during the accept.      */
specifier|public
name|void
name|accept
parameter_list|()
throws|throws
name|Exception
block|{
if|if
condition|(
name|receiver
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|IllegalStateException
argument_list|(
literal|"Can't accept non-received message."
argument_list|)
throw|;
block|}
name|receiver
operator|.
name|accept
argument_list|(
name|delivery
argument_list|)
expr_stmt|;
block|}
comment|/**      * Marks the message as Modified, indicating whether it failed to deliver and is not deliverable here.      *      * @param deliveryFailed      *        indicates that the delivery failed for some reason.      * @param undeliverableHere      *        marks the delivery as not being able to be process by link it was sent to.      *      * @throws Exception if an error occurs during the process.      */
specifier|public
name|void
name|modified
parameter_list|(
name|Boolean
name|deliveryFailed
parameter_list|,
name|Boolean
name|undeliverableHere
parameter_list|)
throws|throws
name|Exception
block|{
if|if
condition|(
name|receiver
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|IllegalStateException
argument_list|(
literal|"Can't modify non-received message."
argument_list|)
throw|;
block|}
name|receiver
operator|.
name|modified
argument_list|(
name|delivery
argument_list|,
name|deliveryFailed
argument_list|,
name|undeliverableHere
argument_list|)
expr_stmt|;
block|}
comment|/**      * Release the message, remote can redeliver it elsewhere.      *      * @throws Exception if an error occurs during the reject.      */
specifier|public
name|void
name|release
parameter_list|()
throws|throws
name|Exception
block|{
if|if
condition|(
name|receiver
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|IllegalStateException
argument_list|(
literal|"Can't release non-received message."
argument_list|)
throw|;
block|}
name|receiver
operator|.
name|release
argument_list|(
name|delivery
argument_list|)
expr_stmt|;
block|}
comment|//----- Convenience methods for constructing outbound messages -----------//
comment|/**      * Sets the MessageId property on an outbound message using the provided String      *      * @param messageId      *        the String message ID value to set.      */
specifier|public
name|void
name|setMessageId
parameter_list|(
name|String
name|messageId
parameter_list|)
block|{
name|checkReadOnly
argument_list|()
expr_stmt|;
name|lazyCreateProperties
argument_list|()
expr_stmt|;
name|getWrappedMessage
argument_list|()
operator|.
name|setMessageId
argument_list|(
name|messageId
argument_list|)
expr_stmt|;
block|}
comment|/**      * Return the set MessageId value in String form, if there are no properties      * in the given message return null.      *      * @return the set message ID in String form or null if not set.      */
specifier|public
name|String
name|getMessageId
parameter_list|()
block|{
if|if
condition|(
name|message
operator|.
name|getProperties
argument_list|()
operator|==
literal|null
condition|)
block|{
return|return
literal|null
return|;
block|}
return|return
name|message
operator|.
name|getProperties
argument_list|()
operator|.
name|getMessageId
argument_list|()
operator|.
name|toString
argument_list|()
return|;
block|}
comment|/**      * Return the set MessageId value in the original form, if there are no properties      * in the given message return null.      *      * @return the set message ID in its original form or null if not set.      */
specifier|public
name|Object
name|getRawMessageId
parameter_list|()
block|{
if|if
condition|(
name|message
operator|.
name|getProperties
argument_list|()
operator|==
literal|null
condition|)
block|{
return|return
literal|null
return|;
block|}
return|return
name|message
operator|.
name|getProperties
argument_list|()
operator|.
name|getMessageId
argument_list|()
return|;
block|}
comment|/**      * Sets the MessageId property on an outbound message using the provided value      *      * @param messageId      *        the message ID value to set.      */
specifier|public
name|void
name|setRawMessageId
parameter_list|(
name|Object
name|messageId
parameter_list|)
block|{
name|checkReadOnly
argument_list|()
expr_stmt|;
name|lazyCreateProperties
argument_list|()
expr_stmt|;
name|getWrappedMessage
argument_list|()
operator|.
name|setMessageId
argument_list|(
name|messageId
argument_list|)
expr_stmt|;
block|}
comment|/**      * Sets the CorrelationId property on an outbound message using the provided String      *      * @param correlationId      *        the String Correlation ID value to set.      */
specifier|public
name|void
name|setCorrelationId
parameter_list|(
name|String
name|correlationId
parameter_list|)
block|{
name|checkReadOnly
argument_list|()
expr_stmt|;
name|lazyCreateProperties
argument_list|()
expr_stmt|;
name|getWrappedMessage
argument_list|()
operator|.
name|setCorrelationId
argument_list|(
name|correlationId
argument_list|)
expr_stmt|;
block|}
comment|/**      * Return the set CorrelationId value in String form, if there are no properties      * in the given message return null.      *      * @return the set correlation ID in String form or null if not set.      */
specifier|public
name|String
name|getCorrelationId
parameter_list|()
block|{
if|if
condition|(
name|message
operator|.
name|getProperties
argument_list|()
operator|==
literal|null
condition|)
block|{
return|return
literal|null
return|;
block|}
return|return
name|message
operator|.
name|getProperties
argument_list|()
operator|.
name|getCorrelationId
argument_list|()
operator|.
name|toString
argument_list|()
return|;
block|}
comment|/**      * Return the set CorrelationId value in the original form, if there are no properties      * in the given message return null.      *      * @return the set message ID in its original form or null if not set.      */
specifier|public
name|Object
name|getRawCorrelationId
parameter_list|()
block|{
if|if
condition|(
name|message
operator|.
name|getProperties
argument_list|()
operator|==
literal|null
condition|)
block|{
return|return
literal|null
return|;
block|}
return|return
name|message
operator|.
name|getProperties
argument_list|()
operator|.
name|getCorrelationId
argument_list|()
return|;
block|}
comment|/**      * Sets the CorrelationId property on an outbound message using the provided value      *      * @param correlationId      *        the correlation ID value to set.      */
specifier|public
name|void
name|setRawCorrelationId
parameter_list|(
name|Object
name|correlationId
parameter_list|)
block|{
name|checkReadOnly
argument_list|()
expr_stmt|;
name|lazyCreateProperties
argument_list|()
expr_stmt|;
name|getWrappedMessage
argument_list|()
operator|.
name|setCorrelationId
argument_list|(
name|correlationId
argument_list|)
expr_stmt|;
block|}
comment|/**      * Sets the GroupId property on an outbound message using the provided String      *      * @param messageId      *        the String Group ID value to set.      */
specifier|public
name|void
name|setGroupId
parameter_list|(
name|String
name|groupId
parameter_list|)
block|{
name|checkReadOnly
argument_list|()
expr_stmt|;
name|lazyCreateProperties
argument_list|()
expr_stmt|;
name|getWrappedMessage
argument_list|()
operator|.
name|setGroupId
argument_list|(
name|groupId
argument_list|)
expr_stmt|;
block|}
comment|/**      * Return the set GroupId value in String form, if there are no properties      * in the given message return null.      *      * @return the set GroupID in String form or null if not set.      */
specifier|public
name|String
name|getGroupId
parameter_list|()
block|{
if|if
condition|(
name|message
operator|.
name|getProperties
argument_list|()
operator|==
literal|null
condition|)
block|{
return|return
literal|null
return|;
block|}
return|return
name|message
operator|.
name|getProperties
argument_list|()
operator|.
name|getGroupId
argument_list|()
return|;
block|}
comment|/**      * Sets the durable header on the outgoing message.      *      * @param durable      *        the boolean durable value to set.      */
specifier|public
name|void
name|setDurable
parameter_list|(
name|boolean
name|durable
parameter_list|)
block|{
name|checkReadOnly
argument_list|()
expr_stmt|;
name|lazyCreateHeader
argument_list|()
expr_stmt|;
name|getWrappedMessage
argument_list|()
operator|.
name|setDurable
argument_list|(
name|durable
argument_list|)
expr_stmt|;
block|}
comment|/**      * Checks the durable value in the Message Headers to determine if      * the message was sent as a durable Message.      *      * @return true if the message is marked as being durable.      */
specifier|public
name|boolean
name|isDurable
parameter_list|()
block|{
if|if
condition|(
name|message
operator|.
name|getHeader
argument_list|()
operator|==
literal|null
condition|)
block|{
return|return
literal|false
return|;
block|}
return|return
name|message
operator|.
name|getHeader
argument_list|()
operator|.
name|getDurable
argument_list|()
return|;
block|}
comment|/**      * Sets a given application property on an outbound message.      *      * @param key      *        the name to assign the new property.      * @param value      *        the value to set for the named property.      */
specifier|public
name|void
name|setApplicationProperty
parameter_list|(
name|String
name|key
parameter_list|,
name|Object
name|value
parameter_list|)
block|{
name|checkReadOnly
argument_list|()
expr_stmt|;
name|lazyCreateApplicationProperties
argument_list|()
expr_stmt|;
name|applicationPropertiesMap
operator|.
name|put
argument_list|(
name|key
argument_list|,
name|value
argument_list|)
expr_stmt|;
block|}
comment|/**      * Gets the application property that is mapped to the given name or null      * if no property has been set with that name.      *      * @param key      *        the name used to lookup the property in the application properties.      *      * @return the propety value or null if not set.      */
specifier|public
name|Object
name|getApplicationProperty
parameter_list|(
name|String
name|key
parameter_list|)
block|{
if|if
condition|(
name|applicationPropertiesMap
operator|==
literal|null
condition|)
block|{
return|return
literal|null
return|;
block|}
return|return
name|applicationPropertiesMap
operator|.
name|get
argument_list|(
name|key
argument_list|)
return|;
block|}
comment|/**      * Perform a proper annotation set on the AMQP Message based on a Symbol key and      * the target value to append to the current annotations.      *      * @param key      *        The name of the Symbol whose value is being set.      * @param value      *        The new value to set in the annotations of this message.      */
specifier|public
name|void
name|setMessageAnnotation
parameter_list|(
name|String
name|key
parameter_list|,
name|Object
name|value
parameter_list|)
block|{
name|checkReadOnly
argument_list|()
expr_stmt|;
name|lazyCreateMessageAnnotations
argument_list|()
expr_stmt|;
name|messageAnnotationsMap
operator|.
name|put
argument_list|(
name|Symbol
operator|.
name|valueOf
argument_list|(
name|key
argument_list|)
argument_list|,
name|value
argument_list|)
expr_stmt|;
block|}
comment|/**      * Given a message annotation name, lookup and return the value associated with      * that annotation name.  If the message annotations have not been created yet      * then this method will always return null.      *      * @param key      *        the Symbol name that should be looked up in the message annotations.      *      * @return the value of the annotation if it exists, or null if not set or not accessible.      */
specifier|public
name|Object
name|getMessageAnnotation
parameter_list|(
name|String
name|key
parameter_list|)
block|{
if|if
condition|(
name|messageAnnotationsMap
operator|==
literal|null
condition|)
block|{
return|return
literal|null
return|;
block|}
return|return
name|messageAnnotationsMap
operator|.
name|get
argument_list|(
name|Symbol
operator|.
name|valueOf
argument_list|(
name|key
argument_list|)
argument_list|)
return|;
block|}
comment|/**      * Perform a proper delivery annotation set on the AMQP Message based on a Symbol      * key and the target value to append to the current delivery annotations.      *      * @param key      *        The name of the Symbol whose value is being set.      * @param value      *        The new value to set in the delivery annotations of this message.      */
specifier|public
name|void
name|setDeliveryAnnotation
parameter_list|(
name|String
name|key
parameter_list|,
name|Object
name|value
parameter_list|)
block|{
name|checkReadOnly
argument_list|()
expr_stmt|;
name|lazyCreateDeliveryAnnotations
argument_list|()
expr_stmt|;
name|deliveryAnnotationsMap
operator|.
name|put
argument_list|(
name|Symbol
operator|.
name|valueOf
argument_list|(
name|key
argument_list|)
argument_list|,
name|value
argument_list|)
expr_stmt|;
block|}
comment|/**      * Given a message annotation name, lookup and return the value associated with      * that annotation name.  If the message annotations have not been created yet      * then this method will always return null.      *      * @param key      *        the Symbol name that should be looked up in the message annotations.      *      * @return the value of the annotation if it exists, or null if not set or not accessible.      */
specifier|public
name|Object
name|getDeliveryAnnotation
parameter_list|(
name|String
name|key
parameter_list|)
block|{
if|if
condition|(
name|deliveryAnnotationsMap
operator|==
literal|null
condition|)
block|{
return|return
literal|null
return|;
block|}
return|return
name|deliveryAnnotationsMap
operator|.
name|get
argument_list|(
name|Symbol
operator|.
name|valueOf
argument_list|(
name|key
argument_list|)
argument_list|)
return|;
block|}
comment|//----- Methods for manipulating the Message body ------------------------//
comment|/**      * Sets a String value into the body of an outgoing Message, throws      * an exception if this is an incoming message instance.      *      * @param value      *        the String value to store in the Message body.      *      * @throws IllegalStateException if the message is read only.      */
specifier|public
name|void
name|setText
parameter_list|(
name|String
name|value
parameter_list|)
throws|throws
name|IllegalStateException
block|{
name|checkReadOnly
argument_list|()
expr_stmt|;
name|AmqpValue
name|body
init|=
operator|new
name|AmqpValue
argument_list|(
name|value
argument_list|)
decl_stmt|;
name|getWrappedMessage
argument_list|()
operator|.
name|setBody
argument_list|(
name|body
argument_list|)
expr_stmt|;
block|}
comment|/**      * Sets a byte array value into the body of an outgoing Message, throws      * an exception if this is an incoming message instance.      *      * @param value      *        the byte array value to store in the Message body.      *      * @throws IllegalStateException if the message is read only.      */
specifier|public
name|void
name|setBytes
parameter_list|(
name|byte
index|[]
name|bytes
parameter_list|)
throws|throws
name|IllegalStateException
block|{
name|checkReadOnly
argument_list|()
expr_stmt|;
name|Data
name|body
init|=
operator|new
name|Data
argument_list|(
operator|new
name|Binary
argument_list|(
name|bytes
argument_list|)
argument_list|)
decl_stmt|;
name|getWrappedMessage
argument_list|()
operator|.
name|setBody
argument_list|(
name|body
argument_list|)
expr_stmt|;
block|}
comment|/**      * Sets a byte array value into the body of an outgoing Message, throws      * an exception if this is an incoming message instance.      *      * @param value      *        the byte array value to store in the Message body.      *      * @throws IllegalStateException if the message is read only.      */
specifier|public
name|void
name|setDescribedType
parameter_list|(
name|DescribedType
name|described
parameter_list|)
throws|throws
name|IllegalStateException
block|{
name|checkReadOnly
argument_list|()
expr_stmt|;
name|AmqpValue
name|body
init|=
operator|new
name|AmqpValue
argument_list|(
name|described
argument_list|)
decl_stmt|;
name|getWrappedMessage
argument_list|()
operator|.
name|setBody
argument_list|(
name|body
argument_list|)
expr_stmt|;
block|}
comment|/**      * Attempts to retrieve the message body as an DescribedType instance.      *      * @return an DescribedType instance if one is stored in the message body.      *      * @throws NoSuchElementException if the body does not contain a DescribedType.      */
specifier|public
name|DescribedType
name|getDescribedType
parameter_list|()
throws|throws
name|NoSuchElementException
block|{
name|DescribedType
name|result
init|=
literal|null
decl_stmt|;
if|if
condition|(
name|getWrappedMessage
argument_list|()
operator|.
name|getBody
argument_list|()
operator|==
literal|null
condition|)
block|{
return|return
literal|null
return|;
block|}
else|else
block|{
if|if
condition|(
name|getWrappedMessage
argument_list|()
operator|.
name|getBody
argument_list|()
operator|instanceof
name|AmqpValue
condition|)
block|{
name|AmqpValue
name|value
init|=
operator|(
name|AmqpValue
operator|)
name|getWrappedMessage
argument_list|()
operator|.
name|getBody
argument_list|()
decl_stmt|;
if|if
condition|(
name|value
operator|.
name|getValue
argument_list|()
operator|==
literal|null
condition|)
block|{
name|result
operator|=
literal|null
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|value
operator|.
name|getValue
argument_list|()
operator|instanceof
name|DescribedType
condition|)
block|{
name|result
operator|=
operator|(
name|DescribedType
operator|)
name|value
operator|.
name|getValue
argument_list|()
expr_stmt|;
block|}
else|else
block|{
throw|throw
operator|new
name|NoSuchElementException
argument_list|(
literal|"Message does not contain a DescribedType body"
argument_list|)
throw|;
block|}
block|}
block|}
return|return
name|result
return|;
block|}
comment|//----- Internal implementation ------------------------------------------//
specifier|private
name|void
name|checkReadOnly
parameter_list|()
throws|throws
name|IllegalStateException
block|{
if|if
condition|(
name|delivery
operator|!=
literal|null
condition|)
block|{
throw|throw
operator|new
name|IllegalStateException
argument_list|(
literal|"Message is read only."
argument_list|)
throw|;
block|}
block|}
specifier|private
name|void
name|lazyCreateMessageAnnotations
parameter_list|()
block|{
if|if
condition|(
name|messageAnnotationsMap
operator|==
literal|null
condition|)
block|{
name|messageAnnotationsMap
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
name|message
operator|.
name|setMessageAnnotations
argument_list|(
operator|new
name|MessageAnnotations
argument_list|(
name|messageAnnotationsMap
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
specifier|private
name|void
name|lazyCreateDeliveryAnnotations
parameter_list|()
block|{
if|if
condition|(
name|deliveryAnnotationsMap
operator|==
literal|null
condition|)
block|{
name|deliveryAnnotationsMap
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
name|message
operator|.
name|setDeliveryAnnotations
argument_list|(
operator|new
name|DeliveryAnnotations
argument_list|(
name|deliveryAnnotationsMap
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
specifier|private
name|void
name|lazyCreateApplicationProperties
parameter_list|()
block|{
if|if
condition|(
name|applicationPropertiesMap
operator|==
literal|null
condition|)
block|{
name|applicationPropertiesMap
operator|=
operator|new
name|HashMap
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
argument_list|()
expr_stmt|;
name|message
operator|.
name|setApplicationProperties
argument_list|(
operator|new
name|ApplicationProperties
argument_list|(
name|applicationPropertiesMap
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
specifier|private
name|void
name|lazyCreateHeader
parameter_list|()
block|{
if|if
condition|(
name|message
operator|.
name|getHeader
argument_list|()
operator|==
literal|null
condition|)
block|{
name|message
operator|.
name|setHeader
argument_list|(
operator|new
name|Header
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
specifier|private
name|void
name|lazyCreateProperties
parameter_list|()
block|{
if|if
condition|(
name|message
operator|.
name|getProperties
argument_list|()
operator|==
literal|null
condition|)
block|{
name|message
operator|.
name|setProperties
argument_list|(
operator|new
name|Properties
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

