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
name|stomp
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
comment|/**  * Tracker object for Messages that carry STOMP v1.2 ACK IDs  */
end_comment

begin_class
specifier|public
class|class
name|StompAckEntry
block|{
specifier|private
specifier|final
name|String
name|ackId
decl_stmt|;
specifier|private
specifier|final
name|MessageId
name|messageId
decl_stmt|;
specifier|private
specifier|final
name|StompSubscription
name|subscription
decl_stmt|;
specifier|private
specifier|final
name|MessageDispatch
name|dispatch
decl_stmt|;
specifier|public
name|StompAckEntry
parameter_list|(
name|MessageDispatch
name|dispatch
parameter_list|,
name|String
name|ackId
parameter_list|,
name|StompSubscription
name|subscription
parameter_list|)
block|{
name|this
operator|.
name|messageId
operator|=
name|dispatch
operator|.
name|getMessage
argument_list|()
operator|.
name|getMessageId
argument_list|()
expr_stmt|;
name|this
operator|.
name|subscription
operator|=
name|subscription
expr_stmt|;
name|this
operator|.
name|ackId
operator|=
name|ackId
expr_stmt|;
name|this
operator|.
name|dispatch
operator|=
name|dispatch
expr_stmt|;
block|}
specifier|public
name|MessageAck
name|onMessageAck
parameter_list|(
name|TransactionId
name|transactionId
parameter_list|)
block|{
return|return
name|subscription
operator|.
name|onStompMessageAck
argument_list|(
name|messageId
operator|.
name|toString
argument_list|()
argument_list|,
name|transactionId
argument_list|)
return|;
block|}
specifier|public
name|MessageAck
name|onMessageNack
parameter_list|(
name|TransactionId
name|transactionId
parameter_list|)
throws|throws
name|ProtocolException
block|{
return|return
name|subscription
operator|.
name|onStompMessageNack
argument_list|(
name|messageId
operator|.
name|toString
argument_list|()
argument_list|,
name|transactionId
argument_list|)
return|;
block|}
specifier|public
name|MessageId
name|getMessageId
parameter_list|()
block|{
return|return
name|this
operator|.
name|messageId
return|;
block|}
specifier|public
name|MessageDispatch
name|getMessageDispatch
parameter_list|()
block|{
return|return
name|this
operator|.
name|dispatch
return|;
block|}
specifier|public
name|String
name|getAckId
parameter_list|()
block|{
return|return
name|this
operator|.
name|ackId
return|;
block|}
specifier|public
name|StompSubscription
name|getSubscription
parameter_list|()
block|{
return|return
name|this
operator|.
name|subscription
return|;
block|}
annotation|@
name|Override
specifier|public
name|String
name|toString
parameter_list|()
block|{
return|return
literal|"AckEntry[ msgId:"
operator|+
name|messageId
operator|+
literal|", ackId:"
operator|+
name|ackId
operator|+
literal|", sub:"
operator|+
name|subscription
operator|+
literal|" ]"
return|;
block|}
annotation|@
name|Override
specifier|public
name|int
name|hashCode
parameter_list|()
block|{
specifier|final
name|int
name|prime
init|=
literal|31
decl_stmt|;
name|int
name|result
init|=
literal|1
decl_stmt|;
name|result
operator|=
name|prime
operator|*
name|result
operator|+
operator|(
operator|(
name|messageId
operator|==
literal|null
operator|)
condition|?
literal|0
else|:
name|messageId
operator|.
name|hashCode
argument_list|()
operator|)
expr_stmt|;
return|return
name|result
return|;
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|equals
parameter_list|(
name|Object
name|obj
parameter_list|)
block|{
if|if
condition|(
name|this
operator|==
name|obj
condition|)
block|{
return|return
literal|true
return|;
block|}
if|if
condition|(
name|obj
operator|==
literal|null
condition|)
block|{
return|return
literal|false
return|;
block|}
if|if
condition|(
name|getClass
argument_list|()
operator|!=
name|obj
operator|.
name|getClass
argument_list|()
condition|)
block|{
return|return
literal|false
return|;
block|}
name|StompAckEntry
name|other
init|=
operator|(
name|StompAckEntry
operator|)
name|obj
decl_stmt|;
if|if
condition|(
name|messageId
operator|==
literal|null
condition|)
block|{
if|if
condition|(
name|other
operator|.
name|messageId
operator|!=
literal|null
condition|)
block|{
return|return
literal|false
return|;
block|}
block|}
elseif|else
if|if
condition|(
operator|!
name|messageId
operator|.
name|equals
argument_list|(
name|other
operator|.
name|messageId
argument_list|)
condition|)
block|{
return|return
literal|false
return|;
block|}
return|return
literal|true
return|;
block|}
block|}
end_class

end_unit

