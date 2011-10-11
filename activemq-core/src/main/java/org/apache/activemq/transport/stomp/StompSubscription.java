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
name|stomp
package|;
end_package

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
name|Iterator
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|LinkedHashMap
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|LinkedList
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
name|Map
operator|.
name|Entry
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
name|ConsumerInfo
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
name|MessageAck
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
name|MessageDispatch
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
name|TransactionId
import|;
end_import

begin_comment
comment|/**  * Keeps track of the STOMP subscription so that acking is correctly done.  *  * @author<a href="http://hiramchirino.com">chirino</a>  */
end_comment

begin_class
specifier|public
class|class
name|StompSubscription
block|{
specifier|public
specifier|static
specifier|final
name|String
name|AUTO_ACK
init|=
name|Stomp
operator|.
name|Headers
operator|.
name|Subscribe
operator|.
name|AckModeValues
operator|.
name|AUTO
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|String
name|CLIENT_ACK
init|=
name|Stomp
operator|.
name|Headers
operator|.
name|Subscribe
operator|.
name|AckModeValues
operator|.
name|CLIENT
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|String
name|INDIVIDUAL_ACK
init|=
name|Stomp
operator|.
name|Headers
operator|.
name|Subscribe
operator|.
name|AckModeValues
operator|.
name|INDIVIDUAL
decl_stmt|;
specifier|protected
specifier|final
name|ProtocolConverter
name|protocolConverter
decl_stmt|;
specifier|protected
specifier|final
name|String
name|subscriptionId
decl_stmt|;
specifier|protected
specifier|final
name|ConsumerInfo
name|consumerInfo
decl_stmt|;
specifier|protected
specifier|final
name|LinkedHashMap
argument_list|<
name|MessageId
argument_list|,
name|MessageDispatch
argument_list|>
name|dispatchedMessage
init|=
operator|new
name|LinkedHashMap
argument_list|<
name|MessageId
argument_list|,
name|MessageDispatch
argument_list|>
argument_list|()
decl_stmt|;
specifier|protected
specifier|final
name|LinkedList
argument_list|<
name|MessageDispatch
argument_list|>
name|unconsumedMessage
init|=
operator|new
name|LinkedList
argument_list|<
name|MessageDispatch
argument_list|>
argument_list|()
decl_stmt|;
specifier|protected
name|String
name|ackMode
init|=
name|AUTO_ACK
decl_stmt|;
specifier|protected
name|ActiveMQDestination
name|destination
decl_stmt|;
specifier|protected
name|String
name|transformation
decl_stmt|;
specifier|public
name|StompSubscription
parameter_list|(
name|ProtocolConverter
name|stompTransport
parameter_list|,
name|String
name|subscriptionId
parameter_list|,
name|ConsumerInfo
name|consumerInfo
parameter_list|,
name|String
name|transformation
parameter_list|)
block|{
name|this
operator|.
name|protocolConverter
operator|=
name|stompTransport
expr_stmt|;
name|this
operator|.
name|subscriptionId
operator|=
name|subscriptionId
expr_stmt|;
name|this
operator|.
name|consumerInfo
operator|=
name|consumerInfo
expr_stmt|;
name|this
operator|.
name|transformation
operator|=
name|transformation
expr_stmt|;
block|}
name|void
name|onMessageDispatch
parameter_list|(
name|MessageDispatch
name|md
parameter_list|)
throws|throws
name|IOException
throws|,
name|JMSException
block|{
name|ActiveMQMessage
name|message
init|=
operator|(
name|ActiveMQMessage
operator|)
name|md
operator|.
name|getMessage
argument_list|()
decl_stmt|;
if|if
condition|(
name|ackMode
operator|==
name|CLIENT_ACK
condition|)
block|{
synchronized|synchronized
init|(
name|this
init|)
block|{
name|dispatchedMessage
operator|.
name|put
argument_list|(
name|message
operator|.
name|getMessageId
argument_list|()
argument_list|,
name|md
argument_list|)
expr_stmt|;
block|}
block|}
elseif|else
if|if
condition|(
name|ackMode
operator|==
name|INDIVIDUAL_ACK
condition|)
block|{
synchronized|synchronized
init|(
name|this
init|)
block|{
name|dispatchedMessage
operator|.
name|put
argument_list|(
name|message
operator|.
name|getMessageId
argument_list|()
argument_list|,
name|md
argument_list|)
expr_stmt|;
block|}
block|}
elseif|else
if|if
condition|(
name|ackMode
operator|==
name|AUTO_ACK
condition|)
block|{
name|MessageAck
name|ack
init|=
operator|new
name|MessageAck
argument_list|(
name|md
argument_list|,
name|MessageAck
operator|.
name|STANDARD_ACK_TYPE
argument_list|,
literal|1
argument_list|)
decl_stmt|;
name|protocolConverter
operator|.
name|getStompTransport
argument_list|()
operator|.
name|asyncSendToActiveMQ
argument_list|(
name|ack
argument_list|)
expr_stmt|;
block|}
name|boolean
name|ignoreTransformation
init|=
literal|false
decl_stmt|;
if|if
condition|(
name|transformation
operator|!=
literal|null
operator|&&
operator|!
operator|(
name|message
operator|instanceof
name|ActiveMQBytesMessage
operator|)
condition|)
block|{
name|message
operator|.
name|setReadOnlyProperties
argument_list|(
literal|false
argument_list|)
expr_stmt|;
name|message
operator|.
name|setStringProperty
argument_list|(
name|Stomp
operator|.
name|Headers
operator|.
name|TRANSFORMATION
argument_list|,
name|transformation
argument_list|)
expr_stmt|;
block|}
else|else
block|{
if|if
condition|(
name|message
operator|.
name|getStringProperty
argument_list|(
name|Stomp
operator|.
name|Headers
operator|.
name|TRANSFORMATION
argument_list|)
operator|!=
literal|null
condition|)
block|{
name|ignoreTransformation
operator|=
literal|true
expr_stmt|;
block|}
block|}
name|StompFrame
name|command
init|=
name|protocolConverter
operator|.
name|convertMessage
argument_list|(
name|message
argument_list|,
name|ignoreTransformation
argument_list|)
decl_stmt|;
name|command
operator|.
name|setAction
argument_list|(
name|Stomp
operator|.
name|Responses
operator|.
name|MESSAGE
argument_list|)
expr_stmt|;
if|if
condition|(
name|subscriptionId
operator|!=
literal|null
condition|)
block|{
name|command
operator|.
name|getHeaders
argument_list|()
operator|.
name|put
argument_list|(
name|Stomp
operator|.
name|Headers
operator|.
name|Message
operator|.
name|SUBSCRIPTION
argument_list|,
name|subscriptionId
argument_list|)
expr_stmt|;
block|}
name|protocolConverter
operator|.
name|getStompTransport
argument_list|()
operator|.
name|sendToStomp
argument_list|(
name|command
argument_list|)
expr_stmt|;
block|}
specifier|synchronized
name|void
name|onStompAbort
parameter_list|(
name|TransactionId
name|transactionId
parameter_list|)
block|{
name|unconsumedMessage
operator|.
name|clear
argument_list|()
expr_stmt|;
block|}
specifier|synchronized
name|void
name|onStompCommit
parameter_list|(
name|TransactionId
name|transactionId
parameter_list|)
block|{
for|for
control|(
name|Iterator
argument_list|<
name|?
argument_list|>
name|iter
init|=
name|dispatchedMessage
operator|.
name|entrySet
argument_list|()
operator|.
name|iterator
argument_list|()
init|;
name|iter
operator|.
name|hasNext
argument_list|()
condition|;
control|)
block|{
annotation|@
name|SuppressWarnings
argument_list|(
literal|"rawtypes"
argument_list|)
name|Map
operator|.
name|Entry
name|entry
init|=
operator|(
name|Entry
operator|)
name|iter
operator|.
name|next
argument_list|()
decl_stmt|;
name|MessageDispatch
name|msg
init|=
operator|(
name|MessageDispatch
operator|)
name|entry
operator|.
name|getValue
argument_list|()
decl_stmt|;
if|if
condition|(
name|unconsumedMessage
operator|.
name|contains
argument_list|(
name|msg
argument_list|)
condition|)
block|{
name|iter
operator|.
name|remove
argument_list|()
expr_stmt|;
block|}
block|}
if|if
condition|(
operator|!
name|unconsumedMessage
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
name|MessageAck
name|ack
init|=
operator|new
name|MessageAck
argument_list|(
name|unconsumedMessage
operator|.
name|getLast
argument_list|()
argument_list|,
name|MessageAck
operator|.
name|STANDARD_ACK_TYPE
argument_list|,
name|unconsumedMessage
operator|.
name|size
argument_list|()
argument_list|)
decl_stmt|;
name|protocolConverter
operator|.
name|getStompTransport
argument_list|()
operator|.
name|asyncSendToActiveMQ
argument_list|(
name|ack
argument_list|)
expr_stmt|;
name|unconsumedMessage
operator|.
name|clear
argument_list|()
expr_stmt|;
block|}
block|}
specifier|synchronized
name|MessageAck
name|onStompMessageAck
parameter_list|(
name|String
name|messageId
parameter_list|,
name|TransactionId
name|transactionId
parameter_list|)
block|{
name|MessageId
name|msgId
init|=
operator|new
name|MessageId
argument_list|(
name|messageId
argument_list|)
decl_stmt|;
if|if
condition|(
operator|!
name|dispatchedMessage
operator|.
name|containsKey
argument_list|(
name|msgId
argument_list|)
condition|)
block|{
return|return
literal|null
return|;
block|}
name|MessageAck
name|ack
init|=
operator|new
name|MessageAck
argument_list|()
decl_stmt|;
name|ack
operator|.
name|setDestination
argument_list|(
name|consumerInfo
operator|.
name|getDestination
argument_list|()
argument_list|)
expr_stmt|;
name|ack
operator|.
name|setConsumerId
argument_list|(
name|consumerInfo
operator|.
name|getConsumerId
argument_list|()
argument_list|)
expr_stmt|;
if|if
condition|(
name|ackMode
operator|==
name|CLIENT_ACK
condition|)
block|{
if|if
condition|(
name|transactionId
operator|==
literal|null
condition|)
block|{
name|ack
operator|.
name|setAckType
argument_list|(
name|MessageAck
operator|.
name|STANDARD_ACK_TYPE
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|ack
operator|.
name|setAckType
argument_list|(
name|MessageAck
operator|.
name|DELIVERED_ACK_TYPE
argument_list|)
expr_stmt|;
block|}
name|int
name|count
init|=
literal|0
decl_stmt|;
for|for
control|(
name|Iterator
argument_list|<
name|?
argument_list|>
name|iter
init|=
name|dispatchedMessage
operator|.
name|entrySet
argument_list|()
operator|.
name|iterator
argument_list|()
init|;
name|iter
operator|.
name|hasNext
argument_list|()
condition|;
control|)
block|{
annotation|@
name|SuppressWarnings
argument_list|(
literal|"rawtypes"
argument_list|)
name|Map
operator|.
name|Entry
name|entry
init|=
operator|(
name|Entry
operator|)
name|iter
operator|.
name|next
argument_list|()
decl_stmt|;
name|MessageId
name|id
init|=
operator|(
name|MessageId
operator|)
name|entry
operator|.
name|getKey
argument_list|()
decl_stmt|;
name|MessageDispatch
name|msg
init|=
operator|(
name|MessageDispatch
operator|)
name|entry
operator|.
name|getValue
argument_list|()
decl_stmt|;
if|if
condition|(
name|transactionId
operator|!=
literal|null
condition|)
block|{
if|if
condition|(
operator|!
name|unconsumedMessage
operator|.
name|contains
argument_list|(
name|msg
argument_list|)
condition|)
block|{
name|unconsumedMessage
operator|.
name|add
argument_list|(
name|msg
argument_list|)
expr_stmt|;
name|count
operator|++
expr_stmt|;
block|}
block|}
else|else
block|{
name|iter
operator|.
name|remove
argument_list|()
expr_stmt|;
name|count
operator|++
expr_stmt|;
block|}
if|if
condition|(
name|id
operator|.
name|equals
argument_list|(
name|msgId
argument_list|)
condition|)
block|{
name|ack
operator|.
name|setLastMessageId
argument_list|(
name|id
argument_list|)
expr_stmt|;
break|break;
block|}
block|}
name|ack
operator|.
name|setMessageCount
argument_list|(
name|count
argument_list|)
expr_stmt|;
if|if
condition|(
name|transactionId
operator|!=
literal|null
condition|)
block|{
name|ack
operator|.
name|setTransactionId
argument_list|(
name|transactionId
argument_list|)
expr_stmt|;
block|}
block|}
elseif|else
if|if
condition|(
name|ackMode
operator|==
name|INDIVIDUAL_ACK
condition|)
block|{
name|ack
operator|.
name|setAckType
argument_list|(
name|MessageAck
operator|.
name|INDIVIDUAL_ACK_TYPE
argument_list|)
expr_stmt|;
name|ack
operator|.
name|setMessageID
argument_list|(
name|msgId
argument_list|)
expr_stmt|;
if|if
condition|(
name|transactionId
operator|!=
literal|null
condition|)
block|{
name|unconsumedMessage
operator|.
name|add
argument_list|(
name|dispatchedMessage
operator|.
name|get
argument_list|(
name|msgId
argument_list|)
argument_list|)
expr_stmt|;
name|ack
operator|.
name|setTransactionId
argument_list|(
name|transactionId
argument_list|)
expr_stmt|;
block|}
name|dispatchedMessage
operator|.
name|remove
argument_list|(
name|msgId
argument_list|)
expr_stmt|;
block|}
return|return
name|ack
return|;
block|}
specifier|public
name|MessageAck
name|onStompMessageNack
parameter_list|(
name|String
name|messageId
parameter_list|,
name|TransactionId
name|transactionId
parameter_list|)
throws|throws
name|ProtocolException
block|{
name|MessageId
name|msgId
init|=
operator|new
name|MessageId
argument_list|(
name|messageId
argument_list|)
decl_stmt|;
if|if
condition|(
operator|!
name|dispatchedMessage
operator|.
name|containsKey
argument_list|(
name|msgId
argument_list|)
condition|)
block|{
return|return
literal|null
return|;
block|}
name|MessageAck
name|ack
init|=
operator|new
name|MessageAck
argument_list|()
decl_stmt|;
name|ack
operator|.
name|setDestination
argument_list|(
name|consumerInfo
operator|.
name|getDestination
argument_list|()
argument_list|)
expr_stmt|;
name|ack
operator|.
name|setConsumerId
argument_list|(
name|consumerInfo
operator|.
name|getConsumerId
argument_list|()
argument_list|)
expr_stmt|;
name|ack
operator|.
name|setAckType
argument_list|(
name|MessageAck
operator|.
name|POSION_ACK_TYPE
argument_list|)
expr_stmt|;
name|ack
operator|.
name|setMessageID
argument_list|(
name|msgId
argument_list|)
expr_stmt|;
if|if
condition|(
name|transactionId
operator|!=
literal|null
condition|)
block|{
name|unconsumedMessage
operator|.
name|add
argument_list|(
name|dispatchedMessage
operator|.
name|get
argument_list|(
name|msgId
argument_list|)
argument_list|)
expr_stmt|;
name|ack
operator|.
name|setTransactionId
argument_list|(
name|transactionId
argument_list|)
expr_stmt|;
block|}
name|dispatchedMessage
operator|.
name|remove
argument_list|(
name|msgId
argument_list|)
expr_stmt|;
return|return
literal|null
return|;
block|}
specifier|public
name|String
name|getAckMode
parameter_list|()
block|{
return|return
name|ackMode
return|;
block|}
specifier|public
name|void
name|setAckMode
parameter_list|(
name|String
name|ackMode
parameter_list|)
block|{
name|this
operator|.
name|ackMode
operator|=
name|ackMode
expr_stmt|;
block|}
specifier|public
name|String
name|getSubscriptionId
parameter_list|()
block|{
return|return
name|subscriptionId
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
name|ConsumerInfo
name|getConsumerInfo
parameter_list|()
block|{
return|return
name|consumerInfo
return|;
block|}
block|}
end_class

end_unit

