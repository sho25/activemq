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
name|protocol
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
name|AmqpSupport
operator|.
name|toLong
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
name|javax
operator|.
name|jms
operator|.
name|Destination
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
name|ActiveMQDestination
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
name|ExceptionResponse
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
name|LocalTransactionId
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
name|MessageId
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
name|ProducerId
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
name|ProducerInfo
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
name|RemoveInfo
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
name|Response
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
name|AmqpProtocolConverter
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
name|ResponseHandler
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
name|message
operator|.
name|AMQPNativeInboundTransformer
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
name|message
operator|.
name|AMQPRawInboundTransformer
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
name|message
operator|.
name|ActiveMQJMSVendor
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
name|message
operator|.
name|EncodedMessage
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
name|message
operator|.
name|InboundTransformer
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
name|message
operator|.
name|JMSMappingInboundTransformer
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
name|LongSequenceGenerator
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
name|Accepted
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
name|Rejected
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
name|transaction
operator|.
name|TransactionalState
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
name|transport
operator|.
name|DeliveryState
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
name|transport
operator|.
name|ErrorCondition
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
name|engine
operator|.
name|Receiver
import|;
end_import

begin_import
import|import
name|org
operator|.
name|fusesource
operator|.
name|hawtbuf
operator|.
name|Buffer
import|;
end_import

begin_import
import|import
name|org
operator|.
name|slf4j
operator|.
name|Logger
import|;
end_import

begin_import
import|import
name|org
operator|.
name|slf4j
operator|.
name|LoggerFactory
import|;
end_import

begin_comment
comment|/**  * An AmqpReceiver wraps the AMQP Receiver end of a link from the remote peer  * which holds the corresponding Sender which transfers message accross the  * link.  The AmqpReceiver handles all incoming deliveries by converting them  * or wrapping them into an ActiveMQ message object and forwarding that message  * on to the appropriate ActiveMQ Destination.  */
end_comment

begin_class
specifier|public
class|class
name|AmqpReceiver
extends|extends
name|AmqpAbstractReceiver
block|{
specifier|private
specifier|static
specifier|final
name|Logger
name|LOG
init|=
name|LoggerFactory
operator|.
name|getLogger
argument_list|(
name|AmqpReceiver
operator|.
name|class
argument_list|)
decl_stmt|;
specifier|private
specifier|final
name|ProducerInfo
name|producerInfo
decl_stmt|;
specifier|private
specifier|final
name|LongSequenceGenerator
name|messageIdGenerator
init|=
operator|new
name|LongSequenceGenerator
argument_list|()
decl_stmt|;
specifier|private
name|InboundTransformer
name|inboundTransformer
decl_stmt|;
comment|/**      * Create a new instance of an AmqpReceiver      *      * @param session      *        the Session that is the parent of this AmqpReceiver instance.      * @param endpoint      *        the AMQP receiver endpoint that the class manages.      * @param producerInfo      *        the ProducerInfo instance that contains this sender's configuration.      */
specifier|public
name|AmqpReceiver
parameter_list|(
name|AmqpSession
name|session
parameter_list|,
name|Receiver
name|endpoint
parameter_list|,
name|ProducerInfo
name|producerInfo
parameter_list|)
block|{
name|super
argument_list|(
name|session
argument_list|,
name|endpoint
argument_list|)
expr_stmt|;
name|this
operator|.
name|producerInfo
operator|=
name|producerInfo
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|close
parameter_list|()
block|{
if|if
condition|(
operator|!
name|isClosed
argument_list|()
operator|&&
name|isOpened
argument_list|()
condition|)
block|{
name|sendToActiveMQ
argument_list|(
operator|new
name|RemoveInfo
argument_list|(
name|getProducerId
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|super
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
comment|//----- Configuration accessors ------------------------------------------//
comment|/**      * @return the ActiveMQ ProducerId used to register this Receiver on the Broker.      */
specifier|public
name|ProducerId
name|getProducerId
parameter_list|()
block|{
return|return
name|producerInfo
operator|.
name|getProducerId
argument_list|()
return|;
block|}
annotation|@
name|Override
specifier|public
name|ActiveMQDestination
name|getDestination
parameter_list|()
block|{
return|return
name|producerInfo
operator|.
name|getDestination
argument_list|()
return|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|setDestination
parameter_list|(
name|ActiveMQDestination
name|destination
parameter_list|)
block|{
name|producerInfo
operator|.
name|setDestination
argument_list|(
name|destination
argument_list|)
expr_stmt|;
block|}
comment|/**      * If the Sender that initiated this Receiver endpoint did not define an address      * then it is using anonymous mode and message are to be routed to the address      * that is defined in the AMQP message 'To' field.      *      * @return true if this Receiver should operate in anonymous mode.      */
specifier|public
name|boolean
name|isAnonymous
parameter_list|()
block|{
return|return
name|producerInfo
operator|.
name|getDestination
argument_list|()
operator|==
literal|null
return|;
block|}
comment|//----- Internal Implementation ------------------------------------------//
specifier|protected
name|InboundTransformer
name|getTransformer
parameter_list|()
block|{
if|if
condition|(
name|inboundTransformer
operator|==
literal|null
condition|)
block|{
name|String
name|transformer
init|=
name|session
operator|.
name|getConnection
argument_list|()
operator|.
name|getConfiguredTransformer
argument_list|()
decl_stmt|;
if|if
condition|(
name|transformer
operator|.
name|equalsIgnoreCase
argument_list|(
name|InboundTransformer
operator|.
name|TRANSFORMER_JMS
argument_list|)
condition|)
block|{
name|inboundTransformer
operator|=
operator|new
name|JMSMappingInboundTransformer
argument_list|(
name|ActiveMQJMSVendor
operator|.
name|INSTANCE
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|transformer
operator|.
name|equalsIgnoreCase
argument_list|(
name|InboundTransformer
operator|.
name|TRANSFORMER_NATIVE
argument_list|)
condition|)
block|{
name|inboundTransformer
operator|=
operator|new
name|AMQPNativeInboundTransformer
argument_list|(
name|ActiveMQJMSVendor
operator|.
name|INSTANCE
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|transformer
operator|.
name|equalsIgnoreCase
argument_list|(
name|InboundTransformer
operator|.
name|TRANSFORMER_RAW
argument_list|)
condition|)
block|{
name|inboundTransformer
operator|=
operator|new
name|AMQPRawInboundTransformer
argument_list|(
name|ActiveMQJMSVendor
operator|.
name|INSTANCE
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|LOG
operator|.
name|warn
argument_list|(
literal|"Unknown transformer type {} using native one instead"
argument_list|,
name|transformer
argument_list|)
expr_stmt|;
name|inboundTransformer
operator|=
operator|new
name|AMQPNativeInboundTransformer
argument_list|(
name|ActiveMQJMSVendor
operator|.
name|INSTANCE
argument_list|)
expr_stmt|;
block|}
block|}
return|return
name|inboundTransformer
return|;
block|}
annotation|@
name|Override
specifier|protected
name|void
name|processDelivery
parameter_list|(
specifier|final
name|Delivery
name|delivery
parameter_list|,
name|Buffer
name|deliveryBytes
parameter_list|)
throws|throws
name|Exception
block|{
if|if
condition|(
operator|!
name|isClosed
argument_list|()
condition|)
block|{
name|EncodedMessage
name|em
init|=
operator|new
name|EncodedMessage
argument_list|(
name|delivery
operator|.
name|getMessageFormat
argument_list|()
argument_list|,
name|deliveryBytes
operator|.
name|data
argument_list|,
name|deliveryBytes
operator|.
name|offset
argument_list|,
name|deliveryBytes
operator|.
name|length
argument_list|)
decl_stmt|;
name|InboundTransformer
name|transformer
init|=
name|getTransformer
argument_list|()
decl_stmt|;
name|ActiveMQMessage
name|message
init|=
literal|null
decl_stmt|;
while|while
condition|(
name|transformer
operator|!=
literal|null
condition|)
block|{
try|try
block|{
name|message
operator|=
operator|(
name|ActiveMQMessage
operator|)
name|transformer
operator|.
name|transform
argument_list|(
name|em
argument_list|)
expr_stmt|;
break|break;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
name|LOG
operator|.
name|debug
argument_list|(
literal|"Transform of message using [{}] transformer, failed"
argument_list|,
name|getTransformer
argument_list|()
operator|.
name|getTransformerName
argument_list|()
argument_list|)
expr_stmt|;
name|LOG
operator|.
name|trace
argument_list|(
literal|"Transformation error:"
argument_list|,
name|e
argument_list|)
expr_stmt|;
name|transformer
operator|=
name|transformer
operator|.
name|getFallbackTransformer
argument_list|()
expr_stmt|;
block|}
block|}
if|if
condition|(
name|message
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|IOException
argument_list|(
literal|"Failed to transform incoming delivery, skipping."
argument_list|)
throw|;
block|}
name|current
operator|=
literal|null
expr_stmt|;
if|if
condition|(
name|isAnonymous
argument_list|()
condition|)
block|{
name|Destination
name|toDestination
init|=
name|message
operator|.
name|getJMSDestination
argument_list|()
decl_stmt|;
if|if
condition|(
name|toDestination
operator|==
literal|null
operator|||
operator|!
operator|(
name|toDestination
operator|instanceof
name|ActiveMQDestination
operator|)
condition|)
block|{
name|Rejected
name|rejected
init|=
operator|new
name|Rejected
argument_list|()
decl_stmt|;
name|ErrorCondition
name|condition
init|=
operator|new
name|ErrorCondition
argument_list|()
decl_stmt|;
name|condition
operator|.
name|setCondition
argument_list|(
name|Symbol
operator|.
name|valueOf
argument_list|(
literal|"failed"
argument_list|)
argument_list|)
expr_stmt|;
name|condition
operator|.
name|setDescription
argument_list|(
literal|"Missing to field for message sent to an anonymous producer"
argument_list|)
expr_stmt|;
name|rejected
operator|.
name|setError
argument_list|(
name|condition
argument_list|)
expr_stmt|;
name|delivery
operator|.
name|disposition
argument_list|(
name|rejected
argument_list|)
expr_stmt|;
return|return;
block|}
block|}
else|else
block|{
name|message
operator|.
name|setJMSDestination
argument_list|(
name|getDestination
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|message
operator|.
name|setProducerId
argument_list|(
name|getProducerId
argument_list|()
argument_list|)
expr_stmt|;
comment|// Always override the AMQP client's MessageId with our own.  Preserve
comment|// the original in the TextView property for later Ack.
name|MessageId
name|messageId
init|=
operator|new
name|MessageId
argument_list|(
name|getProducerId
argument_list|()
argument_list|,
name|messageIdGenerator
operator|.
name|getNextSequenceId
argument_list|()
argument_list|)
decl_stmt|;
name|MessageId
name|amqpMessageId
init|=
name|message
operator|.
name|getMessageId
argument_list|()
decl_stmt|;
if|if
condition|(
name|amqpMessageId
operator|!=
literal|null
condition|)
block|{
if|if
condition|(
name|amqpMessageId
operator|.
name|getTextView
argument_list|()
operator|!=
literal|null
condition|)
block|{
name|messageId
operator|.
name|setTextView
argument_list|(
name|amqpMessageId
operator|.
name|getTextView
argument_list|()
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|messageId
operator|.
name|setTextView
argument_list|(
name|amqpMessageId
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
name|message
operator|.
name|setMessageId
argument_list|(
name|messageId
argument_list|)
expr_stmt|;
name|LOG
operator|.
name|trace
argument_list|(
literal|"Inbound Message:{} from Producer:{}"
argument_list|,
name|message
operator|.
name|getMessageId
argument_list|()
argument_list|,
name|getProducerId
argument_list|()
operator|+
literal|":"
operator|+
name|messageId
operator|.
name|getProducerSequenceId
argument_list|()
argument_list|)
expr_stmt|;
specifier|final
name|DeliveryState
name|remoteState
init|=
name|delivery
operator|.
name|getRemoteState
argument_list|()
decl_stmt|;
if|if
condition|(
name|remoteState
operator|!=
literal|null
operator|&&
name|remoteState
operator|instanceof
name|TransactionalState
condition|)
block|{
name|TransactionalState
name|s
init|=
operator|(
name|TransactionalState
operator|)
name|remoteState
decl_stmt|;
name|long
name|txid
init|=
name|toLong
argument_list|(
name|s
operator|.
name|getTxnId
argument_list|()
argument_list|)
decl_stmt|;
name|message
operator|.
name|setTransactionId
argument_list|(
operator|new
name|LocalTransactionId
argument_list|(
name|session
operator|.
name|getConnection
argument_list|()
operator|.
name|getConnectionId
argument_list|()
argument_list|,
name|txid
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|message
operator|.
name|onSend
argument_list|()
expr_stmt|;
if|if
condition|(
operator|!
name|delivery
operator|.
name|remotelySettled
argument_list|()
condition|)
block|{
name|sendToActiveMQ
argument_list|(
name|message
argument_list|,
operator|new
name|ResponseHandler
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|void
name|onResponse
parameter_list|(
name|AmqpProtocolConverter
name|converter
parameter_list|,
name|Response
name|response
parameter_list|)
throws|throws
name|IOException
block|{
if|if
condition|(
name|response
operator|.
name|isException
argument_list|()
condition|)
block|{
name|ExceptionResponse
name|er
init|=
operator|(
name|ExceptionResponse
operator|)
name|response
decl_stmt|;
name|Rejected
name|rejected
init|=
operator|new
name|Rejected
argument_list|()
decl_stmt|;
name|ErrorCondition
name|condition
init|=
operator|new
name|ErrorCondition
argument_list|()
decl_stmt|;
name|condition
operator|.
name|setCondition
argument_list|(
name|Symbol
operator|.
name|valueOf
argument_list|(
literal|"failed"
argument_list|)
argument_list|)
expr_stmt|;
name|condition
operator|.
name|setDescription
argument_list|(
name|er
operator|.
name|getException
argument_list|()
operator|.
name|getMessage
argument_list|()
argument_list|)
expr_stmt|;
name|rejected
operator|.
name|setError
argument_list|(
name|condition
argument_list|)
expr_stmt|;
name|delivery
operator|.
name|disposition
argument_list|(
name|rejected
argument_list|)
expr_stmt|;
block|}
else|else
block|{
if|if
condition|(
name|getEndpoint
argument_list|()
operator|.
name|getCredit
argument_list|()
operator|<=
operator|(
name|getConfiguredReceiverCredit
argument_list|()
operator|*
literal|.2
operator|)
condition|)
block|{
name|LOG
operator|.
name|debug
argument_list|(
literal|"Sending more credit ({}) to producer: {}"
argument_list|,
name|getConfiguredReceiverCredit
argument_list|()
operator|-
name|getEndpoint
argument_list|()
operator|.
name|getCredit
argument_list|()
argument_list|,
name|getProducerId
argument_list|()
argument_list|)
expr_stmt|;
name|getEndpoint
argument_list|()
operator|.
name|flow
argument_list|(
name|getConfiguredReceiverCredit
argument_list|()
operator|-
name|getEndpoint
argument_list|()
operator|.
name|getCredit
argument_list|()
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|remoteState
operator|!=
literal|null
operator|&&
name|remoteState
operator|instanceof
name|TransactionalState
condition|)
block|{
name|TransactionalState
name|txAccepted
init|=
operator|new
name|TransactionalState
argument_list|()
decl_stmt|;
name|txAccepted
operator|.
name|setOutcome
argument_list|(
name|Accepted
operator|.
name|getInstance
argument_list|()
argument_list|)
expr_stmt|;
name|txAccepted
operator|.
name|setTxnId
argument_list|(
operator|(
operator|(
name|TransactionalState
operator|)
name|remoteState
operator|)
operator|.
name|getTxnId
argument_list|()
argument_list|)
expr_stmt|;
name|delivery
operator|.
name|disposition
argument_list|(
name|txAccepted
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|delivery
operator|.
name|disposition
argument_list|(
name|Accepted
operator|.
name|getInstance
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
name|delivery
operator|.
name|settle
argument_list|()
expr_stmt|;
name|session
operator|.
name|pumpProtonToSocket
argument_list|()
expr_stmt|;
block|}
block|}
argument_list|)
expr_stmt|;
block|}
else|else
block|{
if|if
condition|(
name|getEndpoint
argument_list|()
operator|.
name|getCredit
argument_list|()
operator|<=
operator|(
name|getConfiguredReceiverCredit
argument_list|()
operator|*
literal|.2
operator|)
condition|)
block|{
name|LOG
operator|.
name|debug
argument_list|(
literal|"Sending more credit ({}) to producer: {}"
argument_list|,
name|getConfiguredReceiverCredit
argument_list|()
operator|-
name|getEndpoint
argument_list|()
operator|.
name|getCredit
argument_list|()
argument_list|,
name|getProducerId
argument_list|()
argument_list|)
expr_stmt|;
name|getEndpoint
argument_list|()
operator|.
name|flow
argument_list|(
name|getConfiguredReceiverCredit
argument_list|()
operator|-
name|getEndpoint
argument_list|()
operator|.
name|getCredit
argument_list|()
argument_list|)
expr_stmt|;
name|session
operator|.
name|pumpProtonToSocket
argument_list|()
expr_stmt|;
block|}
name|delivery
operator|.
name|settle
argument_list|()
expr_stmt|;
name|sendToActiveMQ
argument_list|(
name|message
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|}
end_class

end_unit

