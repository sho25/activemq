begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  *  * Copyright 2005-2006 The Apache Software Foundation  *  * Licensed under the Apache License, Version 2.0 (the "License");  * you may not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  * http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
package|package
name|org
operator|.
name|apache
operator|.
name|activemq
operator|.
name|command
package|;
end_package

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|DataInputStream
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|DataOutputStream
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
name|Collections
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
name|java
operator|.
name|util
operator|.
name|Map
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|activeio
operator|.
name|command
operator|.
name|WireFormat
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|activeio
operator|.
name|packet
operator|.
name|ByteSequence
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|activeio
operator|.
name|util
operator|.
name|ByteArrayInputStream
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|activeio
operator|.
name|util
operator|.
name|ByteArrayOutputStream
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
name|ActiveMQConnection
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
name|advisory
operator|.
name|AdvisorySupport
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
name|broker
operator|.
name|region
operator|.
name|MessageReference
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
name|util
operator|.
name|MarshallingSupport
import|;
end_import

begin_comment
comment|/**  * Represents an ActiveMQ message  *   * @openwire:marshaller  * @version $Revision$  */
end_comment

begin_class
specifier|abstract
specifier|public
class|class
name|Message
extends|extends
name|BaseCommand
implements|implements
name|MarshallAware
implements|,
name|MessageReference
block|{
specifier|public
specifier|static
specifier|final
name|int
name|AVERAGE_MESSAGE_SIZE_OVERHEAD
init|=
literal|300
decl_stmt|;
specifier|protected
name|MessageId
name|messageId
decl_stmt|;
specifier|protected
name|ActiveMQDestination
name|originalDestination
decl_stmt|;
specifier|protected
name|TransactionId
name|originalTransactionId
decl_stmt|;
specifier|protected
name|ProducerId
name|producerId
decl_stmt|;
specifier|protected
name|ActiveMQDestination
name|destination
decl_stmt|;
specifier|protected
name|TransactionId
name|transactionId
decl_stmt|;
specifier|protected
name|long
name|expiration
decl_stmt|;
specifier|protected
name|long
name|timestamp
decl_stmt|;
specifier|protected
name|long
name|arrival
decl_stmt|;
specifier|protected
name|String
name|correlationId
decl_stmt|;
specifier|protected
name|ActiveMQDestination
name|replyTo
decl_stmt|;
specifier|protected
name|boolean
name|persistent
decl_stmt|;
specifier|protected
name|String
name|type
decl_stmt|;
specifier|protected
name|byte
name|priority
decl_stmt|;
specifier|protected
name|String
name|groupID
decl_stmt|;
specifier|protected
name|int
name|groupSequence
decl_stmt|;
specifier|protected
name|ConsumerId
name|targetConsumerId
decl_stmt|;
specifier|protected
name|boolean
name|compressed
init|=
literal|false
decl_stmt|;
specifier|protected
name|String
name|userID
decl_stmt|;
specifier|protected
name|ByteSequence
name|content
decl_stmt|;
specifier|protected
name|ByteSequence
name|marshalledProperties
decl_stmt|;
specifier|protected
name|DataStructure
name|dataStructure
decl_stmt|;
specifier|protected
name|int
name|redeliveryCounter
decl_stmt|;
specifier|protected
name|int
name|size
decl_stmt|;
specifier|protected
name|Map
name|properties
decl_stmt|;
specifier|protected
name|boolean
name|readOnlyProperties
init|=
literal|false
decl_stmt|;
specifier|protected
name|boolean
name|readOnlyBody
init|=
literal|false
decl_stmt|;
specifier|protected
specifier|transient
name|boolean
name|recievedByDFBridge
init|=
literal|false
decl_stmt|;
specifier|private
specifier|transient
name|short
name|referenceCount
decl_stmt|;
specifier|private
specifier|transient
name|ActiveMQConnection
name|connection
decl_stmt|;
specifier|private
specifier|transient
name|org
operator|.
name|apache
operator|.
name|activemq
operator|.
name|broker
operator|.
name|region
operator|.
name|Destination
name|regionDestination
decl_stmt|;
specifier|private
specifier|transient
name|WireFormat
name|cachedWireFormat
decl_stmt|;
specifier|private
specifier|transient
name|ByteSequence
name|cachedWireFormatData
decl_stmt|;
specifier|private
name|BrokerId
index|[]
name|brokerPath
decl_stmt|;
specifier|abstract
specifier|public
name|Message
name|copy
parameter_list|()
function_decl|;
specifier|protected
name|void
name|copy
parameter_list|(
name|Message
name|copy
parameter_list|)
block|{
name|super
operator|.
name|copy
argument_list|(
name|copy
argument_list|)
expr_stmt|;
name|copy
operator|.
name|producerId
operator|=
name|producerId
expr_stmt|;
name|copy
operator|.
name|transactionId
operator|=
name|transactionId
expr_stmt|;
name|copy
operator|.
name|destination
operator|=
name|destination
expr_stmt|;
name|copy
operator|.
name|messageId
operator|=
name|messageId
expr_stmt|;
name|copy
operator|.
name|originalDestination
operator|=
name|originalDestination
expr_stmt|;
name|copy
operator|.
name|originalTransactionId
operator|=
name|originalTransactionId
expr_stmt|;
name|copy
operator|.
name|expiration
operator|=
name|expiration
expr_stmt|;
name|copy
operator|.
name|timestamp
operator|=
name|timestamp
expr_stmt|;
name|copy
operator|.
name|correlationId
operator|=
name|correlationId
expr_stmt|;
name|copy
operator|.
name|replyTo
operator|=
name|replyTo
expr_stmt|;
name|copy
operator|.
name|persistent
operator|=
name|persistent
expr_stmt|;
name|copy
operator|.
name|redeliveryCounter
operator|=
name|redeliveryCounter
expr_stmt|;
name|copy
operator|.
name|type
operator|=
name|type
expr_stmt|;
name|copy
operator|.
name|priority
operator|=
name|priority
expr_stmt|;
name|copy
operator|.
name|size
operator|=
name|size
expr_stmt|;
name|copy
operator|.
name|groupID
operator|=
name|groupID
expr_stmt|;
name|copy
operator|.
name|userID
operator|=
name|userID
expr_stmt|;
name|copy
operator|.
name|groupSequence
operator|=
name|groupSequence
expr_stmt|;
if|if
condition|(
name|properties
operator|!=
literal|null
condition|)
name|copy
operator|.
name|properties
operator|=
operator|new
name|HashMap
argument_list|(
name|properties
argument_list|)
expr_stmt|;
else|else
name|copy
operator|.
name|properties
operator|=
name|properties
expr_stmt|;
name|copy
operator|.
name|content
operator|=
name|content
expr_stmt|;
name|copy
operator|.
name|marshalledProperties
operator|=
name|marshalledProperties
expr_stmt|;
name|copy
operator|.
name|dataStructure
operator|=
name|dataStructure
expr_stmt|;
name|copy
operator|.
name|readOnlyProperties
operator|=
name|readOnlyProperties
expr_stmt|;
name|copy
operator|.
name|readOnlyBody
operator|=
name|readOnlyBody
expr_stmt|;
name|copy
operator|.
name|compressed
operator|=
name|compressed
expr_stmt|;
name|copy
operator|.
name|recievedByDFBridge
operator|=
name|recievedByDFBridge
expr_stmt|;
block|}
specifier|public
name|Object
name|getProperty
parameter_list|(
name|String
name|name
parameter_list|)
throws|throws
name|IOException
block|{
if|if
condition|(
name|properties
operator|==
literal|null
condition|)
block|{
if|if
condition|(
name|marshalledProperties
operator|==
literal|null
condition|)
return|return
literal|null
return|;
name|properties
operator|=
name|unmarsallProperties
argument_list|(
name|marshalledProperties
argument_list|)
expr_stmt|;
block|}
return|return
name|properties
operator|.
name|get
argument_list|(
name|name
argument_list|)
return|;
block|}
specifier|public
name|Map
name|getProperties
parameter_list|()
throws|throws
name|IOException
block|{
if|if
condition|(
name|properties
operator|==
literal|null
condition|)
block|{
if|if
condition|(
name|marshalledProperties
operator|==
literal|null
condition|)
return|return
name|Collections
operator|.
name|EMPTY_MAP
return|;
name|properties
operator|=
name|unmarsallProperties
argument_list|(
name|marshalledProperties
argument_list|)
expr_stmt|;
block|}
return|return
name|Collections
operator|.
name|unmodifiableMap
argument_list|(
name|properties
argument_list|)
return|;
block|}
specifier|public
name|void
name|clearProperties
parameter_list|()
block|{
name|marshalledProperties
operator|=
literal|null
expr_stmt|;
name|properties
operator|=
literal|null
expr_stmt|;
block|}
specifier|public
name|void
name|setProperty
parameter_list|(
name|String
name|name
parameter_list|,
name|Object
name|value
parameter_list|)
throws|throws
name|IOException
block|{
name|lazyCreateProperties
argument_list|()
expr_stmt|;
name|properties
operator|.
name|put
argument_list|(
name|name
argument_list|,
name|value
argument_list|)
expr_stmt|;
block|}
specifier|protected
name|void
name|lazyCreateProperties
parameter_list|()
throws|throws
name|IOException
block|{
if|if
condition|(
name|properties
operator|==
literal|null
condition|)
block|{
if|if
condition|(
name|marshalledProperties
operator|==
literal|null
condition|)
block|{
name|properties
operator|=
operator|new
name|HashMap
argument_list|()
expr_stmt|;
block|}
else|else
block|{
name|properties
operator|=
name|unmarsallProperties
argument_list|(
name|marshalledProperties
argument_list|)
expr_stmt|;
name|marshalledProperties
operator|=
literal|null
expr_stmt|;
block|}
block|}
block|}
specifier|private
name|Map
name|unmarsallProperties
parameter_list|(
name|ByteSequence
name|marshalledProperties
parameter_list|)
throws|throws
name|IOException
block|{
return|return
name|MarshallingSupport
operator|.
name|unmarshalPrimitiveMap
argument_list|(
operator|new
name|DataInputStream
argument_list|(
operator|new
name|ByteArrayInputStream
argument_list|(
name|marshalledProperties
argument_list|)
argument_list|)
argument_list|)
return|;
block|}
specifier|public
name|void
name|beforeMarshall
parameter_list|(
name|WireFormat
name|wireFormat
parameter_list|)
throws|throws
name|IOException
block|{
comment|// Need to marshal the properties.
if|if
condition|(
name|marshalledProperties
operator|==
literal|null
operator|&&
name|properties
operator|!=
literal|null
condition|)
block|{
name|ByteArrayOutputStream
name|baos
init|=
operator|new
name|ByteArrayOutputStream
argument_list|()
decl_stmt|;
name|DataOutputStream
name|os
init|=
operator|new
name|DataOutputStream
argument_list|(
name|baos
argument_list|)
decl_stmt|;
name|MarshallingSupport
operator|.
name|marshalPrimitiveMap
argument_list|(
name|properties
argument_list|,
name|os
argument_list|)
expr_stmt|;
name|os
operator|.
name|close
argument_list|()
expr_stmt|;
name|marshalledProperties
operator|=
name|baos
operator|.
name|toByteSequence
argument_list|()
expr_stmt|;
block|}
block|}
specifier|public
name|void
name|afterMarshall
parameter_list|(
name|WireFormat
name|wireFormat
parameter_list|)
throws|throws
name|IOException
block|{     }
specifier|public
name|void
name|beforeUnmarshall
parameter_list|(
name|WireFormat
name|wireFormat
parameter_list|)
throws|throws
name|IOException
block|{     }
specifier|public
name|void
name|afterUnmarshall
parameter_list|(
name|WireFormat
name|wireFormat
parameter_list|)
throws|throws
name|IOException
block|{     }
comment|///////////////////////////////////////////////////////////////////
comment|//
comment|// Simple Field accessors
comment|//
comment|///////////////////////////////////////////////////////////////////
comment|/**      * @openwire:property version=1 cache=true      */
specifier|public
name|ProducerId
name|getProducerId
parameter_list|()
block|{
return|return
name|producerId
return|;
block|}
specifier|public
name|void
name|setProducerId
parameter_list|(
name|ProducerId
name|producerId
parameter_list|)
block|{
name|this
operator|.
name|producerId
operator|=
name|producerId
expr_stmt|;
block|}
comment|/**      * @openwire:property version=1 cache=true      */
specifier|public
name|ActiveMQDestination
name|getDestination
parameter_list|()
block|{
return|return
name|destination
return|;
block|}
specifier|public
name|void
name|setDestination
parameter_list|(
name|ActiveMQDestination
name|destination
parameter_list|)
block|{
name|this
operator|.
name|destination
operator|=
name|destination
expr_stmt|;
block|}
comment|/**      * @openwire:property version=1 cache=true      */
specifier|public
name|TransactionId
name|getTransactionId
parameter_list|()
block|{
return|return
name|transactionId
return|;
block|}
specifier|public
name|void
name|setTransactionId
parameter_list|(
name|TransactionId
name|transactionId
parameter_list|)
block|{
name|this
operator|.
name|transactionId
operator|=
name|transactionId
expr_stmt|;
block|}
specifier|public
name|boolean
name|isInTransaction
parameter_list|()
block|{
return|return
name|transactionId
operator|!=
literal|null
return|;
block|}
comment|/**      * @openwire:property version=1 cache=true      */
specifier|public
name|ActiveMQDestination
name|getOriginalDestination
parameter_list|()
block|{
return|return
name|originalDestination
return|;
block|}
specifier|public
name|void
name|setOriginalDestination
parameter_list|(
name|ActiveMQDestination
name|destination
parameter_list|)
block|{
name|this
operator|.
name|originalDestination
operator|=
name|destination
expr_stmt|;
block|}
comment|/**      * @openwire:property version=1      */
specifier|public
name|MessageId
name|getMessageId
parameter_list|()
block|{
return|return
name|messageId
return|;
block|}
specifier|public
name|void
name|setMessageId
parameter_list|(
name|MessageId
name|messageId
parameter_list|)
block|{
name|this
operator|.
name|messageId
operator|=
name|messageId
expr_stmt|;
block|}
comment|/**      * @openwire:property version=1 cache=true      */
specifier|public
name|TransactionId
name|getOriginalTransactionId
parameter_list|()
block|{
return|return
name|originalTransactionId
return|;
block|}
specifier|public
name|void
name|setOriginalTransactionId
parameter_list|(
name|TransactionId
name|transactionId
parameter_list|)
block|{
name|this
operator|.
name|originalTransactionId
operator|=
name|transactionId
expr_stmt|;
block|}
comment|/**      * @openwire:property version=1      */
specifier|public
name|String
name|getGroupID
parameter_list|()
block|{
return|return
name|groupID
return|;
block|}
specifier|public
name|void
name|setGroupID
parameter_list|(
name|String
name|groupID
parameter_list|)
block|{
name|this
operator|.
name|groupID
operator|=
name|groupID
expr_stmt|;
block|}
comment|/**      * @openwire:property version=1      */
specifier|public
name|int
name|getGroupSequence
parameter_list|()
block|{
return|return
name|groupSequence
return|;
block|}
specifier|public
name|void
name|setGroupSequence
parameter_list|(
name|int
name|groupSequence
parameter_list|)
block|{
name|this
operator|.
name|groupSequence
operator|=
name|groupSequence
expr_stmt|;
block|}
comment|/**      * @openwire:property version=1      */
specifier|public
name|String
name|getCorrelationId
parameter_list|()
block|{
return|return
name|correlationId
return|;
block|}
specifier|public
name|void
name|setCorrelationId
parameter_list|(
name|String
name|correlationId
parameter_list|)
block|{
name|this
operator|.
name|correlationId
operator|=
name|correlationId
expr_stmt|;
block|}
comment|/**      * @openwire:property version=1      */
specifier|public
name|boolean
name|isPersistent
parameter_list|()
block|{
return|return
name|persistent
return|;
block|}
specifier|public
name|void
name|setPersistent
parameter_list|(
name|boolean
name|deliveryMode
parameter_list|)
block|{
name|this
operator|.
name|persistent
operator|=
name|deliveryMode
expr_stmt|;
block|}
comment|/**      * @openwire:property version=1      */
specifier|public
name|long
name|getExpiration
parameter_list|()
block|{
return|return
name|expiration
return|;
block|}
specifier|public
name|void
name|setExpiration
parameter_list|(
name|long
name|expiration
parameter_list|)
block|{
name|this
operator|.
name|expiration
operator|=
name|expiration
expr_stmt|;
block|}
comment|/**      * @openwire:property version=1      */
specifier|public
name|byte
name|getPriority
parameter_list|()
block|{
return|return
name|priority
return|;
block|}
specifier|public
name|void
name|setPriority
parameter_list|(
name|byte
name|priority
parameter_list|)
block|{
name|this
operator|.
name|priority
operator|=
name|priority
expr_stmt|;
block|}
comment|/**      * @openwire:property version=1      */
specifier|public
name|ActiveMQDestination
name|getReplyTo
parameter_list|()
block|{
return|return
name|replyTo
return|;
block|}
specifier|public
name|void
name|setReplyTo
parameter_list|(
name|ActiveMQDestination
name|replyTo
parameter_list|)
block|{
name|this
operator|.
name|replyTo
operator|=
name|replyTo
expr_stmt|;
block|}
comment|/**      * @openwire:property version=1      */
specifier|public
name|long
name|getTimestamp
parameter_list|()
block|{
return|return
name|timestamp
return|;
block|}
specifier|public
name|void
name|setTimestamp
parameter_list|(
name|long
name|timestamp
parameter_list|)
block|{
name|this
operator|.
name|timestamp
operator|=
name|timestamp
expr_stmt|;
block|}
comment|/**      * @openwire:property version=1      */
specifier|public
name|String
name|getType
parameter_list|()
block|{
return|return
name|type
return|;
block|}
specifier|public
name|void
name|setType
parameter_list|(
name|String
name|type
parameter_list|)
block|{
name|this
operator|.
name|type
operator|=
name|type
expr_stmt|;
block|}
comment|/**      * @openwire:property version=1      */
specifier|public
name|ByteSequence
name|getContent
parameter_list|()
block|{
return|return
name|content
return|;
block|}
specifier|public
name|void
name|setContent
parameter_list|(
name|ByteSequence
name|content
parameter_list|)
block|{
name|this
operator|.
name|content
operator|=
name|content
expr_stmt|;
block|}
comment|/**      * @openwire:property version=1      */
specifier|public
name|ByteSequence
name|getMarshalledProperties
parameter_list|()
block|{
return|return
name|marshalledProperties
return|;
block|}
specifier|public
name|void
name|setMarshalledProperties
parameter_list|(
name|ByteSequence
name|marshalledProperties
parameter_list|)
block|{
name|this
operator|.
name|marshalledProperties
operator|=
name|marshalledProperties
expr_stmt|;
block|}
comment|/**      * @openwire:property version=1      */
specifier|public
name|DataStructure
name|getDataStructure
parameter_list|()
block|{
return|return
name|dataStructure
return|;
block|}
specifier|public
name|void
name|setDataStructure
parameter_list|(
name|DataStructure
name|data
parameter_list|)
block|{
name|this
operator|.
name|dataStructure
operator|=
name|data
expr_stmt|;
block|}
comment|/**      * Can be used to route the message to a specific consumer.  Should      * be null to allow the broker use normal JMS routing semantics.  If       * the target consumer id is an active consumer on the broker, the message       * is dropped.  Used by the AdvisoryBroker to replay advisory messages      * to a specific consumer.       *       * @openwire:property version=1 cache=true      */
specifier|public
name|ConsumerId
name|getTargetConsumerId
parameter_list|()
block|{
return|return
name|targetConsumerId
return|;
block|}
specifier|public
name|void
name|setTargetConsumerId
parameter_list|(
name|ConsumerId
name|targetConsumerId
parameter_list|)
block|{
name|this
operator|.
name|targetConsumerId
operator|=
name|targetConsumerId
expr_stmt|;
block|}
specifier|public
name|boolean
name|isExpired
parameter_list|()
block|{
comment|// TODO: need to be implemented.
return|return
literal|false
return|;
block|}
specifier|public
name|boolean
name|isAdvisory
parameter_list|()
block|{
return|return
name|type
operator|!=
literal|null
operator|&&
name|type
operator|.
name|equals
argument_list|(
name|AdvisorySupport
operator|.
name|ADIVSORY_MESSAGE_TYPE
argument_list|)
return|;
block|}
comment|/**      * @openwire:property version=1      */
specifier|public
name|boolean
name|isCompressed
parameter_list|()
block|{
return|return
name|compressed
return|;
block|}
specifier|public
name|void
name|setCompressed
parameter_list|(
name|boolean
name|compressed
parameter_list|)
block|{
name|this
operator|.
name|compressed
operator|=
name|compressed
expr_stmt|;
block|}
specifier|public
name|boolean
name|isRedelivered
parameter_list|()
block|{
return|return
name|redeliveryCounter
operator|>
literal|0
return|;
block|}
specifier|public
name|void
name|setRedelivered
parameter_list|(
name|boolean
name|redelivered
parameter_list|)
block|{
if|if
condition|(
name|redelivered
condition|)
block|{
if|if
condition|(
operator|!
name|isRedelivered
argument_list|()
condition|)
block|{
name|setRedeliveryCounter
argument_list|(
literal|1
argument_list|)
expr_stmt|;
block|}
block|}
else|else
block|{
if|if
condition|(
name|isRedelivered
argument_list|()
condition|)
block|{
name|setRedeliveryCounter
argument_list|(
literal|0
argument_list|)
expr_stmt|;
block|}
block|}
block|}
specifier|public
name|void
name|incrementRedeliveryCounter
parameter_list|()
block|{
name|redeliveryCounter
operator|++
expr_stmt|;
block|}
comment|/**      * @openwire:property version=1      */
specifier|public
name|int
name|getRedeliveryCounter
parameter_list|()
block|{
return|return
name|redeliveryCounter
return|;
block|}
specifier|public
name|void
name|setRedeliveryCounter
parameter_list|(
name|int
name|deliveryCounter
parameter_list|)
block|{
name|this
operator|.
name|redeliveryCounter
operator|=
name|deliveryCounter
expr_stmt|;
block|}
comment|/**      * The route of brokers the command has moved through.       *       * @openwire:property version=1 cache=true      */
specifier|public
name|BrokerId
index|[]
name|getBrokerPath
parameter_list|()
block|{
return|return
name|brokerPath
return|;
block|}
specifier|public
name|void
name|setBrokerPath
parameter_list|(
name|BrokerId
index|[]
name|brokerPath
parameter_list|)
block|{
name|this
operator|.
name|brokerPath
operator|=
name|brokerPath
expr_stmt|;
block|}
specifier|public
name|boolean
name|isReadOnlyProperties
parameter_list|()
block|{
return|return
name|readOnlyProperties
return|;
block|}
specifier|public
name|void
name|setReadOnlyProperties
parameter_list|(
name|boolean
name|readOnlyProperties
parameter_list|)
block|{
name|this
operator|.
name|readOnlyProperties
operator|=
name|readOnlyProperties
expr_stmt|;
block|}
specifier|public
name|boolean
name|isReadOnlyBody
parameter_list|()
block|{
return|return
name|readOnlyBody
return|;
block|}
specifier|public
name|void
name|setReadOnlyBody
parameter_list|(
name|boolean
name|readOnlyBody
parameter_list|)
block|{
name|this
operator|.
name|readOnlyBody
operator|=
name|readOnlyBody
expr_stmt|;
block|}
specifier|public
name|ActiveMQConnection
name|getConnection
parameter_list|()
block|{
return|return
name|this
operator|.
name|connection
return|;
block|}
specifier|public
name|void
name|setConnection
parameter_list|(
name|ActiveMQConnection
name|connection
parameter_list|)
block|{
name|this
operator|.
name|connection
operator|=
name|connection
expr_stmt|;
block|}
comment|/**      * Used to schedule the arrival time of a message to a broker.  The broker will       * not dispatch a message to a consumer until it's arrival time has elapsed.       *        * @openwire:property version=1      */
specifier|public
name|long
name|getArrival
parameter_list|()
block|{
return|return
name|arrival
return|;
block|}
specifier|public
name|void
name|setArrival
parameter_list|(
name|long
name|arrival
parameter_list|)
block|{
name|this
operator|.
name|arrival
operator|=
name|arrival
expr_stmt|;
block|}
comment|/**      * Only set by the broker and defines the userID of the producer connection who      * sent this message. This is an optional field, it needs to be enabled on the      * broker to have this field populated.      *        * @openwire:property version=1      */
specifier|public
name|String
name|getUserID
parameter_list|()
block|{
return|return
name|userID
return|;
block|}
specifier|public
name|void
name|setUserID
parameter_list|(
name|String
name|jmsxUserID
parameter_list|)
block|{
name|this
operator|.
name|userID
operator|=
name|jmsxUserID
expr_stmt|;
block|}
specifier|public
name|int
name|getReferenceCount
parameter_list|()
block|{
return|return
name|referenceCount
return|;
block|}
specifier|public
name|Message
name|getMessageHardRef
parameter_list|()
block|{
return|return
name|this
return|;
block|}
specifier|public
name|Message
name|getMessage
parameter_list|()
throws|throws
name|IOException
block|{
return|return
name|this
return|;
block|}
specifier|public
name|org
operator|.
name|apache
operator|.
name|activemq
operator|.
name|broker
operator|.
name|region
operator|.
name|Destination
name|getRegionDestination
parameter_list|()
block|{
return|return
name|regionDestination
return|;
block|}
specifier|public
name|void
name|setRegionDestination
parameter_list|(
name|org
operator|.
name|apache
operator|.
name|activemq
operator|.
name|broker
operator|.
name|region
operator|.
name|Destination
name|destination
parameter_list|)
block|{
name|this
operator|.
name|regionDestination
operator|=
name|destination
expr_stmt|;
block|}
specifier|public
name|boolean
name|isMarshallAware
parameter_list|()
block|{
return|return
literal|true
return|;
block|}
specifier|synchronized
specifier|public
name|ByteSequence
name|getCachedMarshalledForm
parameter_list|(
name|WireFormat
name|wireFormat
parameter_list|)
block|{
if|if
condition|(
name|cachedWireFormat
operator|==
literal|null
operator|||
operator|!
name|cachedWireFormat
operator|.
name|equals
argument_list|(
name|wireFormat
argument_list|)
condition|)
block|{
return|return
literal|null
return|;
block|}
return|return
name|cachedWireFormatData
return|;
block|}
specifier|synchronized
specifier|public
name|void
name|evictMarshlledForm
parameter_list|()
block|{
name|cachedWireFormat
operator|=
literal|null
expr_stmt|;
name|cachedWireFormatData
operator|=
literal|null
expr_stmt|;
block|}
specifier|synchronized
specifier|public
name|void
name|setCachedMarshalledForm
parameter_list|(
name|WireFormat
name|wireFormat
parameter_list|,
name|ByteSequence
name|data
parameter_list|)
block|{
name|cachedWireFormat
operator|=
name|wireFormat
expr_stmt|;
name|cachedWireFormatData
operator|=
name|data
expr_stmt|;
name|int
name|sizeChange
init|=
literal|0
decl_stmt|;
synchronized|synchronized
init|(
name|this
init|)
block|{
if|if
condition|(
name|referenceCount
operator|>
literal|0
condition|)
block|{
name|sizeChange
operator|=
name|getSize
argument_list|()
expr_stmt|;
name|this
operator|.
name|size
operator|=
literal|0
expr_stmt|;
name|sizeChange
operator|-=
name|getSize
argument_list|()
expr_stmt|;
block|}
block|}
if|if
condition|(
name|sizeChange
operator|!=
literal|0
operator|&&
name|regionDestination
operator|!=
literal|null
condition|)
name|regionDestination
operator|.
name|getUsageManager
argument_list|()
operator|.
name|decreaseUsage
argument_list|(
name|sizeChange
argument_list|)
expr_stmt|;
block|}
specifier|public
name|int
name|incrementReferenceCount
parameter_list|()
block|{
name|int
name|rc
decl_stmt|;
name|int
name|size
decl_stmt|;
synchronized|synchronized
init|(
name|this
init|)
block|{
name|rc
operator|=
operator|++
name|referenceCount
expr_stmt|;
name|size
operator|=
name|getSize
argument_list|()
expr_stmt|;
block|}
if|if
condition|(
name|rc
operator|==
literal|1
operator|&&
name|regionDestination
operator|!=
literal|null
condition|)
name|regionDestination
operator|.
name|getUsageManager
argument_list|()
operator|.
name|increaseUsage
argument_list|(
name|size
argument_list|)
expr_stmt|;
return|return
name|rc
return|;
block|}
specifier|synchronized
specifier|public
name|int
name|decrementReferenceCount
parameter_list|()
block|{
name|int
name|rc
decl_stmt|;
name|int
name|size
decl_stmt|;
synchronized|synchronized
init|(
name|this
init|)
block|{
name|rc
operator|=
operator|--
name|referenceCount
expr_stmt|;
name|size
operator|=
name|getSize
argument_list|()
expr_stmt|;
block|}
if|if
condition|(
name|rc
operator|==
literal|0
operator|&&
name|regionDestination
operator|!=
literal|null
condition|)
name|regionDestination
operator|.
name|getUsageManager
argument_list|()
operator|.
name|decreaseUsage
argument_list|(
name|size
argument_list|)
expr_stmt|;
return|return
name|rc
return|;
block|}
specifier|public
name|int
name|getSize
parameter_list|()
block|{
if|if
condition|(
name|size
operator|==
literal|0
condition|)
block|{
name|size
operator|=
name|AVERAGE_MESSAGE_SIZE_OVERHEAD
expr_stmt|;
if|if
condition|(
name|marshalledProperties
operator|!=
literal|null
condition|)
name|size
operator|+=
name|marshalledProperties
operator|.
name|getLength
argument_list|()
expr_stmt|;
if|if
condition|(
name|content
operator|!=
literal|null
condition|)
name|size
operator|+=
name|content
operator|.
name|getLength
argument_list|()
expr_stmt|;
if|if
condition|(
name|cachedWireFormatData
operator|!=
literal|null
condition|)
name|size
operator|+=
name|cachedWireFormatData
operator|.
name|getLength
argument_list|()
operator|+
literal|12
expr_stmt|;
else|else
name|size
operator|*=
literal|2
expr_stmt|;
comment|// Estimate what the cached data will add.
block|}
return|return
name|size
return|;
block|}
comment|/**      * @openwire:property version=1      * @return Returns the recievedByDFBridge.      */
specifier|public
name|boolean
name|isRecievedByDFBridge
parameter_list|()
block|{
return|return
name|recievedByDFBridge
return|;
block|}
comment|/**      * @param recievedByDFBridge The recievedByDFBridge to set.      */
specifier|public
name|void
name|setRecievedByDFBridge
parameter_list|(
name|boolean
name|recievedByDFBridge
parameter_list|)
block|{
name|this
operator|.
name|recievedByDFBridge
operator|=
name|recievedByDFBridge
expr_stmt|;
block|}
specifier|public
name|void
name|onMessageRolledBack
parameter_list|()
block|{
name|incrementRedeliveryCounter
argument_list|()
expr_stmt|;
block|}
block|}
end_class

end_unit

