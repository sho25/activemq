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
name|DataOutput
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
name|Iterator
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
name|ActiveMQTextMessage
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
name|RemoveInfo
import|;
end_import

begin_class
specifier|public
class|class
name|Subscription
block|{
specifier|public
specifier|static
specifier|final
name|int
name|AUTO_ACK
init|=
literal|1
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|int
name|CLIENT_ACK
init|=
literal|2
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|String
name|NO_ID
init|=
literal|"~~ NO SUCH THING ~~%%@#!Q"
decl_stmt|;
specifier|private
name|ActiveMQDestination
name|destination
decl_stmt|;
specifier|private
name|int
name|ackMode
init|=
name|AUTO_ACK
decl_stmt|;
specifier|private
name|StompWireFormat
name|format
decl_stmt|;
specifier|private
specifier|final
name|String
name|subscriptionId
decl_stmt|;
specifier|private
specifier|final
name|ConsumerInfo
name|consumerInfo
decl_stmt|;
specifier|private
specifier|final
name|LinkedList
name|dispatchedMessages
init|=
operator|new
name|LinkedList
argument_list|()
decl_stmt|;
specifier|public
name|Subscription
parameter_list|(
name|StompWireFormat
name|format
parameter_list|,
name|String
name|subscriptionId
parameter_list|,
name|ConsumerInfo
name|consumerInfo
parameter_list|)
block|{
name|this
operator|.
name|format
operator|=
name|format
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
block|}
name|void
name|setDestination
parameter_list|(
name|ActiveMQDestination
name|actual_dest
parameter_list|)
block|{
name|this
operator|.
name|destination
operator|=
name|actual_dest
expr_stmt|;
block|}
name|void
name|receive
parameter_list|(
name|MessageDispatch
name|md
parameter_list|,
name|DataOutput
name|out
parameter_list|)
throws|throws
name|IOException
throws|,
name|JMSException
block|{
name|ActiveMQMessage
name|m
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
name|Subscription
name|sub
init|=
name|format
operator|.
name|getSubcription
argument_list|(
name|md
operator|.
name|getConsumerId
argument_list|()
argument_list|)
decl_stmt|;
name|sub
operator|.
name|addMessageDispatch
argument_list|(
name|md
argument_list|)
expr_stmt|;
name|format
operator|.
name|getDispachedMap
argument_list|()
operator|.
name|put
argument_list|(
name|m
operator|.
name|getJMSMessageID
argument_list|()
argument_list|,
name|sub
argument_list|)
expr_stmt|;
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
name|format
operator|.
name|enqueueCommand
argument_list|(
name|ack
argument_list|)
expr_stmt|;
block|}
name|FrameBuilder
name|builder
init|=
operator|new
name|FrameBuilder
argument_list|(
name|Stomp
operator|.
name|Responses
operator|.
name|MESSAGE
argument_list|)
decl_stmt|;
name|builder
operator|.
name|addHeaders
argument_list|(
name|m
argument_list|)
expr_stmt|;
if|if
condition|(
name|m
operator|.
name|getDataStructureType
argument_list|()
operator|==
name|ActiveMQTextMessage
operator|.
name|DATA_STRUCTURE_TYPE
condition|)
block|{
name|builder
operator|.
name|setBody
argument_list|(
operator|(
operator|(
name|ActiveMQTextMessage
operator|)
name|m
operator|)
operator|.
name|getText
argument_list|()
operator|.
name|getBytes
argument_list|(
literal|"UTF-8"
argument_list|)
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|m
operator|.
name|getDataStructureType
argument_list|()
operator|==
name|ActiveMQBytesMessage
operator|.
name|DATA_STRUCTURE_TYPE
condition|)
block|{
name|ActiveMQBytesMessage
name|msg
init|=
operator|(
name|ActiveMQBytesMessage
operator|)
name|m
decl_stmt|;
name|byte
index|[]
name|data
init|=
operator|new
name|byte
index|[
operator|(
name|int
operator|)
name|msg
operator|.
name|getBodyLength
argument_list|()
index|]
decl_stmt|;
name|msg
operator|.
name|readBytes
argument_list|(
name|data
argument_list|)
expr_stmt|;
name|builder
operator|.
name|setBody
argument_list|(
name|data
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|subscriptionId
operator|!=
literal|null
condition|)
block|{
name|builder
operator|.
name|addHeader
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
name|out
operator|.
name|write
argument_list|(
name|builder
operator|.
name|toFrame
argument_list|()
argument_list|)
expr_stmt|;
block|}
specifier|private
name|void
name|addMessageDispatch
parameter_list|(
name|MessageDispatch
name|md
parameter_list|)
block|{
name|dispatchedMessages
operator|.
name|addLast
argument_list|(
name|md
argument_list|)
expr_stmt|;
block|}
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
name|setAckMode
parameter_list|(
name|int
name|clientAck
parameter_list|)
block|{
name|this
operator|.
name|ackMode
operator|=
name|clientAck
expr_stmt|;
block|}
specifier|public
name|RemoveInfo
name|close
parameter_list|()
block|{
return|return
operator|new
name|RemoveInfo
argument_list|(
name|consumerInfo
operator|.
name|getConsumerId
argument_list|()
argument_list|)
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
name|MessageAck
name|createMessageAck
parameter_list|(
name|String
name|message_id
parameter_list|)
block|{
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
name|setAckType
argument_list|(
name|MessageAck
operator|.
name|STANDARD_ACK_TYPE
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
name|int
name|count
init|=
literal|0
decl_stmt|;
for|for
control|(
name|Iterator
name|iter
init|=
name|dispatchedMessages
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
name|MessageDispatch
name|md
init|=
operator|(
name|MessageDispatch
operator|)
name|iter
operator|.
name|next
argument_list|()
decl_stmt|;
name|String
name|id
init|=
operator|(
operator|(
name|ActiveMQMessage
operator|)
name|md
operator|.
name|getMessage
argument_list|()
operator|)
operator|.
name|getJMSMessageID
argument_list|()
decl_stmt|;
if|if
condition|(
name|ack
operator|.
name|getFirstMessageId
argument_list|()
operator|==
literal|null
condition|)
name|ack
operator|.
name|setFirstMessageId
argument_list|(
name|md
operator|.
name|getMessage
argument_list|()
operator|.
name|getMessageId
argument_list|()
argument_list|)
expr_stmt|;
name|format
operator|.
name|getDispachedMap
argument_list|()
operator|.
name|remove
argument_list|(
name|id
argument_list|)
expr_stmt|;
name|iter
operator|.
name|remove
argument_list|()
expr_stmt|;
name|count
operator|++
expr_stmt|;
if|if
condition|(
name|id
operator|.
name|equals
argument_list|(
name|message_id
argument_list|)
condition|)
block|{
name|ack
operator|.
name|setLastMessageId
argument_list|(
name|md
operator|.
name|getMessage
argument_list|()
operator|.
name|getMessageId
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
name|ack
operator|.
name|setMessageCount
argument_list|(
name|count
argument_list|)
expr_stmt|;
return|return
name|ack
return|;
block|}
block|}
end_class

end_unit

