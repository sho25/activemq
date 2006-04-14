begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  *   * Copyright 2005-2006 The Apache Software Foundation  *   * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with  * the License. You may obtain a copy of the License at  *   * http://www.apache.org/licenses/LICENSE-2.0  *   * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on  * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the  * specific language governing permissions and limitations under the License.  */
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
name|broker
operator|.
name|region
operator|.
name|policy
operator|.
name|DeadLetterStrategy
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
name|ConsumerControl
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
name|activemq
operator|.
name|util
operator|.
name|BrokerSupport
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

begin_comment
comment|/**  * A subscription that honors the pre-fetch option of the ConsumerInfo.  *   * @version $Revision: 1.15 $  */
end_comment

begin_class
specifier|abstract
specifier|public
class|class
name|PrefetchSubscription
extends|extends
name|AbstractSubscription
block|{
specifier|static
specifier|private
specifier|final
name|Log
name|log
init|=
name|LogFactory
operator|.
name|getLog
argument_list|(
name|PrefetchSubscription
operator|.
name|class
argument_list|)
decl_stmt|;
specifier|final
specifier|protected
name|LinkedList
name|pending
init|=
operator|new
name|LinkedList
argument_list|()
decl_stmt|;
specifier|final
specifier|protected
name|LinkedList
name|dispatched
init|=
operator|new
name|LinkedList
argument_list|()
decl_stmt|;
specifier|protected
name|int
name|prefetchExtension
init|=
literal|0
decl_stmt|;
name|boolean
name|dispatching
init|=
literal|false
decl_stmt|;
name|long
name|enqueueCounter
decl_stmt|;
name|long
name|dispatchCounter
decl_stmt|;
name|long
name|dequeueCounter
decl_stmt|;
specifier|public
name|PrefetchSubscription
parameter_list|(
name|Broker
name|broker
parameter_list|,
name|ConnectionContext
name|context
parameter_list|,
name|ConsumerInfo
name|info
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
block|}
specifier|synchronized
specifier|public
name|void
name|add
parameter_list|(
name|MessageReference
name|node
parameter_list|)
throws|throws
name|Exception
block|{
name|enqueueCounter
operator|++
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
name|dispatch
argument_list|(
name|node
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|optimizePrefetch
argument_list|()
expr_stmt|;
synchronized|synchronized
init|(
name|pending
init|)
block|{
if|if
condition|(
name|pending
operator|.
name|isEmpty
argument_list|()
condition|)
if|if
condition|(
name|log
operator|.
name|isDebugEnabled
argument_list|()
condition|)
block|{
name|log
operator|.
name|debug
argument_list|(
literal|"Prefetch limit."
argument_list|)
expr_stmt|;
block|}
name|pending
operator|.
name|addLast
argument_list|(
name|node
argument_list|)
expr_stmt|;
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
name|pending
init|)
block|{
for|for
control|(
name|Iterator
name|i
init|=
name|pending
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
try|try
block|{
name|MessageDispatch
name|md
init|=
name|createMessageDispatch
argument_list|(
name|node
argument_list|,
name|node
operator|.
name|getMessage
argument_list|()
argument_list|)
decl_stmt|;
name|dispatched
operator|.
name|addLast
argument_list|(
name|node
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
name|log
operator|.
name|error
argument_list|(
literal|"Problem processing MessageDispatchNotification: "
operator|+
name|mdn
argument_list|,
name|e
argument_list|)
expr_stmt|;
block|}
break|break;
block|}
block|}
block|}
block|}
specifier|synchronized
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
name|Exception
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
condition|)
block|{
comment|// Acknowledge all dispatched messages up till the message id of the acknowledgment.
name|int
name|index
init|=
literal|0
decl_stmt|;
name|boolean
name|inAckRange
init|=
literal|false
decl_stmt|;
for|for
control|(
name|Iterator
name|iter
init|=
name|dispatched
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
specifier|final
name|MessageReference
name|node
init|=
operator|(
name|MessageReference
operator|)
name|iter
operator|.
name|next
argument_list|()
decl_stmt|;
name|MessageId
name|messageId
init|=
name|node
operator|.
name|getMessageId
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
operator|||
name|ack
operator|.
name|getFirstMessageId
argument_list|()
operator|.
name|equals
argument_list|(
name|messageId
argument_list|)
condition|)
block|{
name|inAckRange
operator|=
literal|true
expr_stmt|;
block|}
if|if
condition|(
name|inAckRange
condition|)
block|{
comment|// Don't remove the nodes until we are committed.
if|if
condition|(
operator|!
name|context
operator|.
name|isInTransaction
argument_list|()
condition|)
block|{
name|dequeueCounter
operator|++
expr_stmt|;
name|iter
operator|.
name|remove
argument_list|()
expr_stmt|;
block|}
else|else
block|{
comment|// setup a Synchronization to remove nodes from the dispatched list.
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
name|Exception
block|{
synchronized|synchronized
init|(
name|PrefetchSubscription
operator|.
name|this
init|)
block|{
name|dequeueCounter
operator|++
expr_stmt|;
name|dispatched
operator|.
name|remove
argument_list|(
name|node
argument_list|)
expr_stmt|;
name|prefetchExtension
operator|--
expr_stmt|;
block|}
block|}
block|}
argument_list|)
expr_stmt|;
block|}
name|index
operator|++
expr_stmt|;
name|acknowledge
argument_list|(
name|context
argument_list|,
name|ack
argument_list|,
name|node
argument_list|)
expr_stmt|;
if|if
condition|(
name|ack
operator|.
name|getLastMessageId
argument_list|()
operator|.
name|equals
argument_list|(
name|messageId
argument_list|)
condition|)
block|{
if|if
condition|(
name|context
operator|.
name|isInTransaction
argument_list|()
condition|)
name|prefetchExtension
operator|=
name|Math
operator|.
name|max
argument_list|(
name|prefetchExtension
argument_list|,
name|index
operator|+
literal|1
argument_list|)
expr_stmt|;
else|else
name|prefetchExtension
operator|=
name|Math
operator|.
name|max
argument_list|(
literal|0
argument_list|,
name|prefetchExtension
operator|-
operator|(
name|index
operator|+
literal|1
operator|)
argument_list|)
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
else|else
block|{
comment|// System.out.println("no match: "+ack.getLastMessageId()+","+messageId);
block|}
block|}
block|}
name|log
operator|.
name|info
argument_list|(
literal|"Could not correlate acknowledgment with dispatched message: "
operator|+
name|ack
argument_list|)
expr_stmt|;
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
comment|// Acknowledge all dispatched messages up till the message id of the acknowledgment.
name|int
name|index
init|=
literal|0
decl_stmt|;
for|for
control|(
name|Iterator
name|iter
init|=
name|dispatched
operator|.
name|iterator
argument_list|()
init|;
name|iter
operator|.
name|hasNext
argument_list|()
condition|;
name|index
operator|++
control|)
block|{
specifier|final
name|MessageReference
name|node
init|=
operator|(
name|MessageReference
operator|)
name|iter
operator|.
name|next
argument_list|()
decl_stmt|;
if|if
condition|(
name|ack
operator|.
name|getLastMessageId
argument_list|()
operator|.
name|equals
argument_list|(
name|node
operator|.
name|getMessageId
argument_list|()
argument_list|)
condition|)
block|{
name|prefetchExtension
operator|=
name|Math
operator|.
name|max
argument_list|(
name|prefetchExtension
argument_list|,
name|index
operator|+
literal|1
argument_list|)
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
block|}
throw|throw
operator|new
name|JMSException
argument_list|(
literal|"Could not correlate acknowledgment with dispatched message: "
operator|+
name|ack
argument_list|)
throw|;
block|}
elseif|else
if|if
condition|(
name|ack
operator|.
name|isPoisonAck
argument_list|()
condition|)
block|{
comment|// TODO: what if the message is already in a DLQ???
comment|// Handle the poison ACK case: we need to send the message to a DLQ
if|if
condition|(
name|ack
operator|.
name|isInTransaction
argument_list|()
condition|)
throw|throw
operator|new
name|JMSException
argument_list|(
literal|"Poison ack cannot be transacted: "
operator|+
name|ack
argument_list|)
throw|;
comment|// Acknowledge all dispatched messages up till the message id of the acknowledgment.
name|int
name|index
init|=
literal|0
decl_stmt|;
name|boolean
name|inAckRange
init|=
literal|false
decl_stmt|;
for|for
control|(
name|Iterator
name|iter
init|=
name|dispatched
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
specifier|final
name|MessageReference
name|node
init|=
operator|(
name|MessageReference
operator|)
name|iter
operator|.
name|next
argument_list|()
decl_stmt|;
name|MessageId
name|messageId
init|=
name|node
operator|.
name|getMessageId
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
operator|||
name|ack
operator|.
name|getFirstMessageId
argument_list|()
operator|.
name|equals
argument_list|(
name|messageId
argument_list|)
condition|)
block|{
name|inAckRange
operator|=
literal|true
expr_stmt|;
block|}
if|if
condition|(
name|inAckRange
condition|)
block|{
name|sendToDLQ
argument_list|(
name|context
argument_list|,
name|node
argument_list|)
expr_stmt|;
name|iter
operator|.
name|remove
argument_list|()
expr_stmt|;
name|dequeueCounter
operator|++
expr_stmt|;
name|index
operator|++
expr_stmt|;
name|acknowledge
argument_list|(
name|context
argument_list|,
name|ack
argument_list|,
name|node
argument_list|)
expr_stmt|;
if|if
condition|(
name|ack
operator|.
name|getLastMessageId
argument_list|()
operator|.
name|equals
argument_list|(
name|messageId
argument_list|)
condition|)
block|{
name|prefetchExtension
operator|=
name|Math
operator|.
name|max
argument_list|(
literal|0
argument_list|,
name|prefetchExtension
operator|-
operator|(
name|index
operator|+
literal|1
operator|)
argument_list|)
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
block|}
block|}
throw|throw
operator|new
name|JMSException
argument_list|(
literal|"Could not correlate acknowledgment with dispatched message: "
operator|+
name|ack
argument_list|)
throw|;
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
comment|/**      * @param context      * @param node      * @throws IOException      * @throws Exception      */
specifier|protected
name|void
name|sendToDLQ
parameter_list|(
specifier|final
name|ConnectionContext
name|context
parameter_list|,
specifier|final
name|MessageReference
name|node
parameter_list|)
throws|throws
name|IOException
throws|,
name|Exception
block|{
comment|// Send the message to the DLQ
name|Message
name|message
init|=
name|node
operator|.
name|getMessage
argument_list|()
decl_stmt|;
if|if
condition|(
name|message
operator|!=
literal|null
condition|)
block|{
comment|// The original destination and transaction id do not get filled when the message is first
comment|// sent,
comment|// it is only populated if the message is routed to another destination like the DLQ
name|DeadLetterStrategy
name|deadLetterStrategy
init|=
name|node
operator|.
name|getRegionDestination
argument_list|()
operator|.
name|getDeadLetterStrategy
argument_list|()
decl_stmt|;
name|ActiveMQDestination
name|deadLetterDestination
init|=
name|deadLetterStrategy
operator|.
name|getDeadLetterQueueFor
argument_list|(
name|message
operator|.
name|getDestination
argument_list|()
argument_list|)
decl_stmt|;
name|BrokerSupport
operator|.
name|resend
argument_list|(
name|context
argument_list|,
name|message
argument_list|,
name|deadLetterDestination
argument_list|)
expr_stmt|;
block|}
block|}
specifier|protected
name|boolean
name|isFull
parameter_list|()
block|{
return|return
name|dispatched
operator|.
name|size
argument_list|()
operator|-
name|prefetchExtension
operator|>=
name|info
operator|.
name|getPrefetchSize
argument_list|()
return|;
block|}
comment|/**      * @return true when 60% or more room is left for dispatching messages      */
specifier|public
name|boolean
name|isLowWaterMark
parameter_list|()
block|{
return|return
operator|(
name|dispatched
operator|.
name|size
argument_list|()
operator|-
name|prefetchExtension
operator|)
operator|<=
operator|(
name|info
operator|.
name|getPrefetchSize
argument_list|()
operator|*
literal|.4
operator|)
return|;
block|}
comment|/**      * @return true when 10% or less room is left for dispatching messages      */
specifier|public
name|boolean
name|isHighWaterMark
parameter_list|()
block|{
return|return
operator|(
name|dispatched
operator|.
name|size
argument_list|()
operator|-
name|prefetchExtension
operator|)
operator|>=
operator|(
name|info
operator|.
name|getPrefetchSize
argument_list|()
operator|*
literal|.9
operator|)
return|;
block|}
specifier|synchronized
specifier|public
name|int
name|getPendingQueueSize
parameter_list|()
block|{
return|return
name|pending
operator|.
name|size
argument_list|()
return|;
block|}
specifier|synchronized
specifier|public
name|int
name|getDispatchedQueueSize
parameter_list|()
block|{
return|return
name|dispatched
operator|.
name|size
argument_list|()
return|;
block|}
specifier|synchronized
specifier|public
name|long
name|getDequeueCounter
parameter_list|()
block|{
return|return
name|dequeueCounter
return|;
block|}
specifier|synchronized
specifier|public
name|long
name|getDispatchedCounter
parameter_list|()
block|{
return|return
name|dispatchCounter
return|;
block|}
specifier|synchronized
specifier|public
name|long
name|getEnqueueCounter
parameter_list|()
block|{
return|return
name|enqueueCounter
return|;
block|}
comment|/**      * optimize message consumer prefetch if the consumer supports it      *      */
specifier|public
name|void
name|optimizePrefetch
parameter_list|()
block|{
if|if
condition|(
name|info
operator|!=
literal|null
operator|&&
name|info
operator|.
name|isOptimizedAcknowledge
argument_list|()
operator|&&
name|context
operator|!=
literal|null
operator|&&
name|context
operator|.
name|getConnection
argument_list|()
operator|!=
literal|null
operator|&&
name|context
operator|.
name|getConnection
argument_list|()
operator|.
name|isManageable
argument_list|()
condition|)
block|{
if|if
condition|(
name|info
operator|.
name|getCurrentPrefetchSize
argument_list|()
operator|!=
name|info
operator|.
name|getPrefetchSize
argument_list|()
operator|&&
name|isLowWaterMark
argument_list|()
condition|)
block|{
name|info
operator|.
name|setCurrentPrefetchSize
argument_list|(
name|info
operator|.
name|getPrefetchSize
argument_list|()
argument_list|)
expr_stmt|;
name|updateConsumerPrefetch
argument_list|(
name|info
operator|.
name|getPrefetchSize
argument_list|()
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|info
operator|.
name|getCurrentPrefetchSize
argument_list|()
operator|==
name|info
operator|.
name|getPrefetchSize
argument_list|()
operator|&&
name|isHighWaterMark
argument_list|()
condition|)
block|{
comment|// want to purge any outstanding acks held by the consumer
name|info
operator|.
name|setCurrentPrefetchSize
argument_list|(
literal|1
argument_list|)
expr_stmt|;
name|updateConsumerPrefetch
argument_list|(
literal|1
argument_list|)
expr_stmt|;
block|}
block|}
block|}
specifier|protected
name|void
name|dispatchMatched
parameter_list|()
throws|throws
name|IOException
block|{
if|if
condition|(
operator|!
name|dispatching
condition|)
block|{
name|dispatching
operator|=
literal|true
expr_stmt|;
try|try
block|{
for|for
control|(
name|Iterator
name|iter
init|=
name|pending
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
name|node
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
name|node
argument_list|)
expr_stmt|;
block|}
block|}
finally|finally
block|{
name|dispatching
operator|=
literal|false
expr_stmt|;
block|}
block|}
block|}
specifier|protected
name|boolean
name|dispatch
parameter_list|(
specifier|final
name|MessageReference
name|node
parameter_list|)
throws|throws
name|IOException
block|{
specifier|final
name|Message
name|message
init|=
name|node
operator|.
name|getMessage
argument_list|()
decl_stmt|;
if|if
condition|(
name|message
operator|==
literal|null
condition|)
block|{
return|return
literal|false
return|;
block|}
comment|// Make sure we can dispatch a message.
if|if
condition|(
name|canDispatch
argument_list|(
name|node
argument_list|)
operator|&&
operator|!
name|isSlaveBroker
argument_list|()
condition|)
block|{
name|dispatchCounter
operator|++
expr_stmt|;
name|MessageDispatch
name|md
init|=
name|createMessageDispatch
argument_list|(
name|node
argument_list|,
name|message
argument_list|)
decl_stmt|;
name|dispatched
operator|.
name|addLast
argument_list|(
name|node
argument_list|)
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
comment|// Since the message gets queued up in async dispatch, we don't want to
comment|// decrease the reference count until it gets put on the wire.
name|onDispatch
argument_list|(
name|node
argument_list|,
name|message
argument_list|)
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
name|onDispatch
argument_list|(
name|node
argument_list|,
name|message
argument_list|)
expr_stmt|;
block|}
return|return
literal|true
return|;
block|}
else|else
block|{
return|return
literal|false
return|;
block|}
block|}
specifier|synchronized
specifier|protected
name|void
name|onDispatch
parameter_list|(
specifier|final
name|MessageReference
name|node
parameter_list|,
specifier|final
name|Message
name|message
parameter_list|)
block|{
name|boolean
name|wasFull
init|=
name|isFull
argument_list|()
decl_stmt|;
if|if
condition|(
name|node
operator|.
name|getRegionDestination
argument_list|()
operator|!=
literal|null
condition|)
block|{
name|node
operator|.
name|getRegionDestination
argument_list|()
operator|.
name|getDestinationStatistics
argument_list|()
operator|.
name|onMessageDequeue
argument_list|(
name|message
argument_list|)
expr_stmt|;
name|context
operator|.
name|getConnection
argument_list|()
operator|.
name|getStatistics
argument_list|()
operator|.
name|onMessageDequeue
argument_list|(
name|message
argument_list|)
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
try|try
block|{
name|dispatchMatched
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
name|context
operator|.
name|getConnection
argument_list|()
operator|.
name|serviceException
argument_list|(
name|e
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|}
comment|/**      * inform the MessageConsumer on the client to change it's prefetch      * @param newPrefetch      */
specifier|public
name|void
name|updateConsumerPrefetch
parameter_list|(
name|int
name|newPrefetch
parameter_list|)
block|{
if|if
condition|(
name|context
operator|!=
literal|null
operator|&&
name|context
operator|.
name|getConnection
argument_list|()
operator|!=
literal|null
operator|&&
name|context
operator|.
name|getConnection
argument_list|()
operator|.
name|isManageable
argument_list|()
condition|)
block|{
name|ConsumerControl
name|cc
init|=
operator|new
name|ConsumerControl
argument_list|()
decl_stmt|;
name|cc
operator|.
name|setConsumerId
argument_list|(
name|info
operator|.
name|getConsumerId
argument_list|()
argument_list|)
expr_stmt|;
name|cc
operator|.
name|setPrefetch
argument_list|(
name|newPrefetch
argument_list|)
expr_stmt|;
name|context
operator|.
name|getConnection
argument_list|()
operator|.
name|dispatchAsync
argument_list|(
name|cc
argument_list|)
expr_stmt|;
block|}
block|}
comment|/**      * @param node      * @param message      *            TODO      * @return      */
specifier|protected
name|MessageDispatch
name|createMessageDispatch
parameter_list|(
name|MessageReference
name|node
parameter_list|,
name|Message
name|message
parameter_list|)
block|{
name|MessageDispatch
name|md
init|=
operator|new
name|MessageDispatch
argument_list|()
decl_stmt|;
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
name|md
operator|.
name|setMessage
argument_list|(
name|message
argument_list|)
expr_stmt|;
name|md
operator|.
name|setRedeliveryCounter
argument_list|(
name|node
operator|.
name|getRedeliveryCounter
argument_list|()
argument_list|)
expr_stmt|;
return|return
name|md
return|;
block|}
comment|/**      * Use when a matched message is about to be dispatched to the client.      *       * @param node      * @return false if the message should not be dispatched to the client (another sub may have already dispatched it      *         for example).      * @throws IOException       */
specifier|abstract
specifier|protected
name|boolean
name|canDispatch
parameter_list|(
name|MessageReference
name|node
parameter_list|)
throws|throws
name|IOException
function_decl|;
comment|/**      * Used during acknowledgment to remove the message.      *       * @throws IOException      */
specifier|protected
name|void
name|acknowledge
parameter_list|(
name|ConnectionContext
name|context
parameter_list|,
specifier|final
name|MessageAck
name|ack
parameter_list|,
specifier|final
name|MessageReference
name|node
parameter_list|)
throws|throws
name|IOException
block|{}
block|}
end_class

end_unit

