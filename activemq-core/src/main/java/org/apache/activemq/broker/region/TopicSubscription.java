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
name|broker
operator|.
name|region
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
name|LinkedList
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|jms
operator|.
name|InvalidSelectorException
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
name|broker
operator|.
name|Broker
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
name|ConnectionContext
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
name|ActiveMQQueue
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
name|Message
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
name|MessageDispatchNotification
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
name|memory
operator|.
name|UsageManager
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
name|transaction
operator|.
name|Synchronization
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|commons
operator|.
name|logging
operator|.
name|Log
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|commons
operator|.
name|logging
operator|.
name|LogFactory
import|;
end_import

begin_class
specifier|public
class|class
name|TopicSubscription
extends|extends
name|AbstractSubscription
block|{
specifier|private
specifier|static
specifier|final
name|Log
name|log
init|=
name|LogFactory
operator|.
name|getLog
argument_list|(
name|TopicSubscription
operator|.
name|class
argument_list|)
decl_stmt|;
specifier|final
specifier|protected
name|LinkedList
name|matched
init|=
operator|new
name|LinkedList
argument_list|()
decl_stmt|;
specifier|final
specifier|protected
name|ActiveMQDestination
name|dlqDestination
init|=
operator|new
name|ActiveMQQueue
argument_list|(
literal|"ActiveMQ.DLQ"
argument_list|)
decl_stmt|;
specifier|final
specifier|protected
name|UsageManager
name|usageManager
decl_stmt|;
specifier|protected
name|int
name|dispatched
init|=
literal|0
decl_stmt|;
specifier|protected
name|int
name|delivered
init|=
literal|0
decl_stmt|;
specifier|private
name|int
name|maximumPendingMessages
init|=
operator|-
literal|1
decl_stmt|;
specifier|public
name|TopicSubscription
parameter_list|(
name|Broker
name|broker
parameter_list|,
name|ConnectionContext
name|context
parameter_list|,
name|ConsumerInfo
name|info
parameter_list|,
name|UsageManager
name|usageManager
parameter_list|)
throws|throws
name|InvalidSelectorException
block|{
name|super
argument_list|(
name|broker
argument_list|,
name|context
argument_list|,
name|info
argument_list|)
expr_stmt|;
name|this
operator|.
name|usageManager
operator|=
name|usageManager
expr_stmt|;
block|}
specifier|public
name|void
name|add
parameter_list|(
name|MessageReference
name|node
parameter_list|)
throws|throws
name|InterruptedException
throws|,
name|IOException
block|{
name|node
operator|.
name|incrementReferenceCount
argument_list|()
expr_stmt|;
if|if
condition|(
operator|!
name|isFull
argument_list|()
operator|&&
operator|!
name|isSlaveBroker
argument_list|()
condition|)
block|{
comment|// if maximumPendingMessages is set we will only discard messages which
comment|// have not been dispatched (i.e. we allow the prefetch buffer to be filled)
name|dispatch
argument_list|(
name|node
argument_list|)
expr_stmt|;
block|}
else|else
block|{
if|if
condition|(
name|maximumPendingMessages
operator|!=
literal|0
condition|)
block|{
synchronized|synchronized
init|(
name|matched
init|)
block|{
name|matched
operator|.
name|addLast
argument_list|(
name|node
argument_list|)
expr_stmt|;
comment|// NOTE - be careful about the slaveBroker!
if|if
condition|(
name|maximumPendingMessages
operator|>
literal|0
condition|)
block|{
name|log
operator|.
name|warn
argument_list|(
literal|"discarding "
operator|+
operator|(
name|matched
operator|.
name|size
argument_list|()
operator|-
name|maximumPendingMessages
operator|)
operator|+
literal|" messages for slow consumer"
argument_list|)
expr_stmt|;
comment|// lets discard old messages as we are a slow consumer
while|while
condition|(
operator|!
name|matched
operator|.
name|isEmpty
argument_list|()
operator|&&
name|matched
operator|.
name|size
argument_list|()
operator|>
name|maximumPendingMessages
condition|)
block|{
name|MessageReference
name|oldMessage
init|=
operator|(
name|MessageReference
operator|)
name|matched
operator|.
name|removeFirst
argument_list|()
decl_stmt|;
name|oldMessage
operator|.
name|decrementReferenceCount
argument_list|()
expr_stmt|;
block|}
block|}
block|}
block|}
block|}
block|}
specifier|public
name|void
name|processMessageDispatchNotification
parameter_list|(
name|MessageDispatchNotification
name|mdn
parameter_list|)
block|{
synchronized|synchronized
init|(
name|matched
init|)
block|{
for|for
control|(
name|Iterator
name|i
init|=
name|matched
operator|.
name|iterator
argument_list|()
init|;
name|i
operator|.
name|hasNext
argument_list|()
condition|;
control|)
block|{
name|MessageReference
name|node
init|=
operator|(
name|MessageReference
operator|)
name|i
operator|.
name|next
argument_list|()
decl_stmt|;
if|if
condition|(
name|node
operator|.
name|getMessageId
argument_list|()
operator|.
name|equals
argument_list|(
name|mdn
operator|.
name|getMessageId
argument_list|()
argument_list|)
condition|)
block|{
name|i
operator|.
name|remove
argument_list|()
expr_stmt|;
name|dispatched
operator|++
expr_stmt|;
name|node
operator|.
name|decrementReferenceCount
argument_list|()
expr_stmt|;
break|break;
block|}
block|}
block|}
block|}
specifier|public
name|void
name|acknowledge
parameter_list|(
specifier|final
name|ConnectionContext
name|context
parameter_list|,
specifier|final
name|MessageAck
name|ack
parameter_list|)
throws|throws
name|Throwable
block|{
comment|// Handle the standard acknowledgment case.
name|boolean
name|wasFull
init|=
name|isFull
argument_list|()
decl_stmt|;
if|if
condition|(
name|ack
operator|.
name|isStandardAck
argument_list|()
operator|||
name|ack
operator|.
name|isPoisonAck
argument_list|()
condition|)
block|{
if|if
condition|(
name|context
operator|.
name|isInTransaction
argument_list|()
condition|)
block|{
name|delivered
operator|+=
name|ack
operator|.
name|getMessageCount
argument_list|()
expr_stmt|;
name|context
operator|.
name|getTransaction
argument_list|()
operator|.
name|addSynchronization
argument_list|(
operator|new
name|Synchronization
argument_list|()
block|{
specifier|public
name|void
name|afterCommit
parameter_list|()
throws|throws
name|Throwable
block|{
synchronized|synchronized
init|(
name|TopicSubscription
operator|.
name|this
init|)
block|{
name|dispatched
operator|-=
name|ack
operator|.
name|getMessageCount
argument_list|()
expr_stmt|;
name|delivered
operator|=
name|Math
operator|.
name|max
argument_list|(
literal|0
argument_list|,
name|delivered
operator|-
name|ack
operator|.
name|getMessageCount
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
block|}
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|dispatched
operator|-=
name|ack
operator|.
name|getMessageCount
argument_list|()
expr_stmt|;
name|delivered
operator|=
name|Math
operator|.
name|max
argument_list|(
literal|0
argument_list|,
name|delivered
operator|-
name|ack
operator|.
name|getMessageCount
argument_list|()
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|wasFull
operator|&&
operator|!
name|isFull
argument_list|()
condition|)
block|{
name|dispatchMatched
argument_list|()
expr_stmt|;
block|}
return|return;
block|}
elseif|else
if|if
condition|(
name|ack
operator|.
name|isDeliveredAck
argument_list|()
condition|)
block|{
comment|// Message was delivered but not acknowledged: update pre-fetch counters.
name|delivered
operator|+=
name|ack
operator|.
name|getMessageCount
argument_list|()
expr_stmt|;
if|if
condition|(
name|wasFull
operator|&&
operator|!
name|isFull
argument_list|()
condition|)
block|{
name|dispatchMatched
argument_list|()
expr_stmt|;
block|}
return|return;
block|}
throw|throw
operator|new
name|JMSException
argument_list|(
literal|"Invalid acknowledgment: "
operator|+
name|ack
argument_list|)
throw|;
block|}
specifier|public
name|int
name|pending
parameter_list|()
block|{
return|return
name|matched
operator|.
name|size
argument_list|()
operator|-
name|dispatched
return|;
block|}
specifier|public
name|int
name|dispatched
parameter_list|()
block|{
return|return
name|dispatched
return|;
block|}
specifier|public
name|int
name|delivered
parameter_list|()
block|{
return|return
name|delivered
return|;
block|}
specifier|public
name|int
name|getMaximumPendingMessages
parameter_list|()
block|{
return|return
name|maximumPendingMessages
return|;
block|}
comment|/**      * Sets the maximum number of pending messages that can be matched against this consumer      * before old messages are discarded.      */
specifier|public
name|void
name|setMaximumPendingMessages
parameter_list|(
name|int
name|maximumPendingMessages
parameter_list|)
block|{
name|this
operator|.
name|maximumPendingMessages
operator|=
name|maximumPendingMessages
expr_stmt|;
block|}
specifier|private
name|boolean
name|isFull
parameter_list|()
block|{
return|return
name|dispatched
operator|-
name|delivered
operator|>=
name|info
operator|.
name|getPrefetchSize
argument_list|()
return|;
block|}
specifier|private
name|void
name|dispatchMatched
parameter_list|()
throws|throws
name|IOException
block|{
for|for
control|(
name|Iterator
name|iter
init|=
name|matched
operator|.
name|iterator
argument_list|()
init|;
name|iter
operator|.
name|hasNext
argument_list|()
operator|&&
operator|!
name|isFull
argument_list|()
condition|;
control|)
block|{
name|MessageReference
name|message
init|=
operator|(
name|MessageReference
operator|)
name|iter
operator|.
name|next
argument_list|()
decl_stmt|;
name|iter
operator|.
name|remove
argument_list|()
expr_stmt|;
name|dispatch
argument_list|(
name|message
argument_list|)
expr_stmt|;
block|}
block|}
specifier|private
name|void
name|dispatch
parameter_list|(
specifier|final
name|MessageReference
name|node
parameter_list|)
throws|throws
name|IOException
block|{
name|Message
name|message
init|=
operator|(
name|Message
operator|)
name|node
decl_stmt|;
comment|// Make sure we can dispatch a message.
name|MessageDispatch
name|md
init|=
operator|new
name|MessageDispatch
argument_list|()
decl_stmt|;
name|md
operator|.
name|setMessage
argument_list|(
name|message
argument_list|)
expr_stmt|;
name|md
operator|.
name|setConsumerId
argument_list|(
name|info
operator|.
name|getConsumerId
argument_list|()
argument_list|)
expr_stmt|;
name|md
operator|.
name|setDestination
argument_list|(
name|node
operator|.
name|getRegionDestination
argument_list|()
operator|.
name|getActiveMQDestination
argument_list|()
argument_list|)
expr_stmt|;
name|dispatched
operator|++
expr_stmt|;
if|if
condition|(
name|info
operator|.
name|isDispatchAsync
argument_list|()
condition|)
block|{
name|md
operator|.
name|setConsumer
argument_list|(
operator|new
name|Runnable
argument_list|()
block|{
specifier|public
name|void
name|run
parameter_list|()
block|{
name|node
operator|.
name|decrementReferenceCount
argument_list|()
expr_stmt|;
block|}
block|}
argument_list|)
expr_stmt|;
name|context
operator|.
name|getConnection
argument_list|()
operator|.
name|dispatchAsync
argument_list|(
name|md
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|context
operator|.
name|getConnection
argument_list|()
operator|.
name|dispatchSync
argument_list|(
name|md
argument_list|)
expr_stmt|;
name|node
operator|.
name|decrementReferenceCount
argument_list|()
expr_stmt|;
block|}
block|}
specifier|public
name|String
name|toString
parameter_list|()
block|{
return|return
literal|"TopicSubscription:"
operator|+
literal|" consumer="
operator|+
name|info
operator|.
name|getConsumerId
argument_list|()
operator|+
literal|", destinations="
operator|+
name|destinations
operator|.
name|size
argument_list|()
operator|+
literal|", dispatched="
operator|+
name|dispatched
operator|+
literal|", delivered="
operator|+
name|this
operator|.
name|delivered
operator|+
literal|", matched="
operator|+
name|this
operator|.
name|matched
operator|.
name|size
argument_list|()
return|;
block|}
block|}
end_class

end_unit

