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
name|command
package|;
end_package

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|activemq
operator|.
name|state
operator|.
name|CommandVisitor
import|;
end_import

begin_comment
comment|/**  * @openwire:marshaller code="22"  *   */
end_comment

begin_class
specifier|public
class|class
name|MessageAck
extends|extends
name|BaseCommand
block|{
specifier|public
specifier|static
specifier|final
name|byte
name|DATA_STRUCTURE_TYPE
init|=
name|CommandTypes
operator|.
name|MESSAGE_ACK
decl_stmt|;
comment|/**      * Used to let the broker know that the message has been delivered to the      * client. Message will still be retained until an standard ack is received.      * This is used get the broker to send more messages past prefetch limits      * when an standard ack has not been sent.      */
specifier|public
specifier|static
specifier|final
name|byte
name|DELIVERED_ACK_TYPE
init|=
literal|0
decl_stmt|;
comment|/**      * The standard ack case where a client wants the message to be discarded.      */
specifier|public
specifier|static
specifier|final
name|byte
name|STANDARD_ACK_TYPE
init|=
literal|2
decl_stmt|;
comment|/**      * In case the client want's to explicitly let the broker know that a      * message was not processed and the message was considered a poison      * message.      */
specifier|public
specifier|static
specifier|final
name|byte
name|POSION_ACK_TYPE
init|=
literal|1
decl_stmt|;
comment|/**      * In case the client want's to explicitly let the broker know that a      * message was not processed and it was re-delivered to the consumer      * but it was not yet considered to be a poison message.  The messageCount       * field will hold the number of times the message was re-delivered.       */
specifier|public
specifier|static
specifier|final
name|byte
name|REDELIVERED_ACK_TYPE
init|=
literal|3
decl_stmt|;
comment|/**      * The  ack case where a client wants only an individual message to be discarded.      */
specifier|public
specifier|static
specifier|final
name|byte
name|INDIVIDUAL_ACK_TYPE
init|=
literal|4
decl_stmt|;
comment|/**      * The ack case where a durable topic subscription does not match a selector.      */
specifier|public
specifier|static
specifier|final
name|byte
name|UNMATCHED_ACK_TYPE
init|=
literal|5
decl_stmt|;
specifier|protected
name|byte
name|ackType
decl_stmt|;
specifier|protected
name|ConsumerId
name|consumerId
decl_stmt|;
specifier|protected
name|MessageId
name|firstMessageId
decl_stmt|;
specifier|protected
name|MessageId
name|lastMessageId
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
name|int
name|messageCount
decl_stmt|;
specifier|protected
specifier|transient
name|String
name|consumerKey
decl_stmt|;
specifier|public
name|MessageAck
parameter_list|()
block|{     }
specifier|public
name|MessageAck
parameter_list|(
name|MessageDispatch
name|md
parameter_list|,
name|byte
name|ackType
parameter_list|,
name|int
name|messageCount
parameter_list|)
block|{
name|this
operator|.
name|ackType
operator|=
name|ackType
expr_stmt|;
name|this
operator|.
name|consumerId
operator|=
name|md
operator|.
name|getConsumerId
argument_list|()
expr_stmt|;
name|this
operator|.
name|destination
operator|=
name|md
operator|.
name|getDestination
argument_list|()
expr_stmt|;
name|this
operator|.
name|lastMessageId
operator|=
name|md
operator|.
name|getMessage
argument_list|()
operator|.
name|getMessageId
argument_list|()
expr_stmt|;
name|this
operator|.
name|messageCount
operator|=
name|messageCount
expr_stmt|;
block|}
specifier|public
name|void
name|copy
parameter_list|(
name|MessageAck
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
name|firstMessageId
operator|=
name|firstMessageId
expr_stmt|;
name|copy
operator|.
name|lastMessageId
operator|=
name|lastMessageId
expr_stmt|;
name|copy
operator|.
name|destination
operator|=
name|destination
expr_stmt|;
name|copy
operator|.
name|transactionId
operator|=
name|transactionId
expr_stmt|;
name|copy
operator|.
name|ackType
operator|=
name|ackType
expr_stmt|;
name|copy
operator|.
name|consumerId
operator|=
name|consumerId
expr_stmt|;
block|}
specifier|public
name|byte
name|getDataStructureType
parameter_list|()
block|{
return|return
name|DATA_STRUCTURE_TYPE
return|;
block|}
specifier|public
name|boolean
name|isMessageAck
parameter_list|()
block|{
return|return
literal|true
return|;
block|}
specifier|public
name|boolean
name|isPoisonAck
parameter_list|()
block|{
return|return
name|ackType
operator|==
name|POSION_ACK_TYPE
return|;
block|}
specifier|public
name|boolean
name|isStandardAck
parameter_list|()
block|{
return|return
name|ackType
operator|==
name|STANDARD_ACK_TYPE
return|;
block|}
specifier|public
name|boolean
name|isDeliveredAck
parameter_list|()
block|{
return|return
name|ackType
operator|==
name|DELIVERED_ACK_TYPE
return|;
block|}
specifier|public
name|boolean
name|isRedeliveredAck
parameter_list|()
block|{
return|return
name|ackType
operator|==
name|REDELIVERED_ACK_TYPE
return|;
block|}
specifier|public
name|boolean
name|isIndividualAck
parameter_list|()
block|{
return|return
name|ackType
operator|==
name|INDIVIDUAL_ACK_TYPE
return|;
block|}
specifier|public
name|boolean
name|isUnmatchedAck
parameter_list|()
block|{
return|return
name|ackType
operator|==
name|UNMATCHED_ACK_TYPE
return|;
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
name|ConsumerId
name|getConsumerId
parameter_list|()
block|{
return|return
name|consumerId
return|;
block|}
specifier|public
name|void
name|setConsumerId
parameter_list|(
name|ConsumerId
name|consumerId
parameter_list|)
block|{
name|this
operator|.
name|consumerId
operator|=
name|consumerId
expr_stmt|;
block|}
comment|/**      * @openwire:property version=1      */
specifier|public
name|byte
name|getAckType
parameter_list|()
block|{
return|return
name|ackType
return|;
block|}
specifier|public
name|void
name|setAckType
parameter_list|(
name|byte
name|ackType
parameter_list|)
block|{
name|this
operator|.
name|ackType
operator|=
name|ackType
expr_stmt|;
block|}
comment|/**      * @openwire:property version=1      */
specifier|public
name|MessageId
name|getFirstMessageId
parameter_list|()
block|{
return|return
name|firstMessageId
return|;
block|}
specifier|public
name|void
name|setFirstMessageId
parameter_list|(
name|MessageId
name|firstMessageId
parameter_list|)
block|{
name|this
operator|.
name|firstMessageId
operator|=
name|firstMessageId
expr_stmt|;
block|}
comment|/**      * @openwire:property version=1      */
specifier|public
name|MessageId
name|getLastMessageId
parameter_list|()
block|{
return|return
name|lastMessageId
return|;
block|}
specifier|public
name|void
name|setLastMessageId
parameter_list|(
name|MessageId
name|lastMessageId
parameter_list|)
block|{
name|this
operator|.
name|lastMessageId
operator|=
name|lastMessageId
expr_stmt|;
block|}
comment|/**      * The number of messages being acknowledged in the range.      *       * @openwire:property version=1      */
specifier|public
name|int
name|getMessageCount
parameter_list|()
block|{
return|return
name|messageCount
return|;
block|}
specifier|public
name|void
name|setMessageCount
parameter_list|(
name|int
name|messageCount
parameter_list|)
block|{
name|this
operator|.
name|messageCount
operator|=
name|messageCount
expr_stmt|;
block|}
specifier|public
name|Response
name|visit
parameter_list|(
name|CommandVisitor
name|visitor
parameter_list|)
throws|throws
name|Exception
block|{
return|return
name|visitor
operator|.
name|processMessageAck
argument_list|(
name|this
argument_list|)
return|;
block|}
comment|/**      * A helper method to allow a single message ID to be acknowledged      */
specifier|public
name|void
name|setMessageID
parameter_list|(
name|MessageId
name|messageID
parameter_list|)
block|{
name|setFirstMessageId
argument_list|(
name|messageID
argument_list|)
expr_stmt|;
name|setLastMessageId
argument_list|(
name|messageID
argument_list|)
expr_stmt|;
name|setMessageCount
argument_list|(
literal|1
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

