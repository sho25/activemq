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
name|LinkedList
name|dispatched
init|=
operator|new
name|LinkedList
argument_list|()
decl_stmt|;
specifier|protected
name|int
name|delivered
init|=
literal|0
decl_stmt|;
name|int
name|preLoadLimit
init|=
literal|1024
operator|*
literal|100
decl_stmt|;
name|int
name|preLoadSize
init|=
literal|0
decl_stmt|;
name|boolean
name|dispatching
init|=
literal|false
decl_stmt|;
specifier|public
name|PrefetchSubscription
parameter_list|(
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
name|Throwable
block|{
if|if
condition|(
operator|!
name|isFull
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
name|matched
operator|.
name|addLast
argument_list|(
name|node
argument_list|)
expr_stmt|;
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
name|Throwable
block|{
synchronized|synchronized
init|(
name|PrefetchSubscription
operator|.
name|this
init|)
block|{
comment|// Now that we are committed, we can remove the nodes.
name|boolean
name|inAckRange
init|=
literal|false
decl_stmt|;
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
name|index
operator|++
expr_stmt|;
name|iter
operator|.
name|remove
argument_list|()
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
operator|(
name|index
operator|+
literal|1
operator|)
argument_list|)
expr_stmt|;
return|return;
block|}
block|}
block|}
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
name|delivered
operator|=
name|Math
operator|.
name|max
argument_list|(
name|delivered
argument_list|,
name|index
operator|+
literal|1
argument_list|)
expr_stmt|;
else|else
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
comment|//                        System.out.println("no match: "+ack.getLastMessageId()+","+messageId);
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
name|delivered
operator|=
name|Math
operator|.
name|max
argument_list|(
name|delivered
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
comment|// Send the message to the DLQ
name|node
operator|.
name|incrementReferenceCount
argument_list|()
expr_stmt|;
try|try
block|{
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
comment|// TODO is this meant to be == null - it was != ?
if|if
condition|(
name|message
operator|.
name|getOriginalDestination
argument_list|()
operator|==
literal|null
condition|)
name|message
operator|.
name|setOriginalDestination
argument_list|(
name|message
operator|.
name|getDestination
argument_list|()
argument_list|)
expr_stmt|;
name|ActiveMQDestination
name|originalDestination
init|=
name|message
operator|.
name|getOriginalDestination
argument_list|()
decl_stmt|;
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
name|originalDestination
argument_list|)
decl_stmt|;
name|message
operator|.
name|setDestination
argument_list|(
name|deadLetterDestination
argument_list|)
expr_stmt|;
if|if
condition|(
name|message
operator|.
name|getOriginalTransactionId
argument_list|()
operator|!=
literal|null
condition|)
name|message
operator|.
name|setOriginalTransactionId
argument_list|(
name|message
operator|.
name|getTransactionId
argument_list|()
argument_list|)
expr_stmt|;
name|message
operator|.
name|setTransactionId
argument_list|(
literal|null
argument_list|)
expr_stmt|;
name|message
operator|.
name|evictMarshlledForm
argument_list|()
expr_stmt|;
name|boolean
name|originalFlowControl
init|=
name|context
operator|.
name|isProducerFlowControl
argument_list|()
decl_stmt|;
try|try
block|{
name|context
operator|.
name|setProducerFlowControl
argument_list|(
literal|false
argument_list|)
expr_stmt|;
name|context
operator|.
name|getBroker
argument_list|()
operator|.
name|send
argument_list|(
name|context
argument_list|,
name|message
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|context
operator|.
name|setProducerFlowControl
argument_list|(
name|originalFlowControl
argument_list|)
expr_stmt|;
block|}
block|}
block|}
finally|finally
block|{
name|node
operator|.
name|decrementReferenceCount
argument_list|()
expr_stmt|;
block|}
name|iter
operator|.
name|remove
argument_list|()
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
name|delivered
operator|>=
name|info
operator|.
name|getPrefetchSize
argument_list|()
operator|||
name|preLoadSize
operator|>
name|preLoadLimit
return|;
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
name|node
operator|.
name|incrementReferenceCount
argument_list|()
expr_stmt|;
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
return|return;
block|}
comment|// Make sure we can dispatch a message.
if|if
condition|(
name|canDispatch
argument_list|(
name|node
argument_list|)
condition|)
block|{
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
name|incrementPreloadSize
argument_list|(
name|node
operator|.
name|getMessage
argument_list|()
operator|.
name|getSize
argument_list|()
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
comment|// The onDispatch() does the node.decrementReferenceCount();
block|}
else|else
block|{
comment|// We were not allowed to dispatch that message (an other consumer grabbed it before we did)
name|node
operator|.
name|decrementReferenceCount
argument_list|()
expr_stmt|;
block|}
block|}
specifier|synchronized
specifier|private
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
name|decrementPreloadSize
argument_list|(
name|message
operator|.
name|getSize
argument_list|()
argument_list|)
expr_stmt|;
name|node
operator|.
name|decrementReferenceCount
argument_list|()
expr_stmt|;
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
specifier|private
name|int
name|incrementPreloadSize
parameter_list|(
name|int
name|size
parameter_list|)
block|{
name|preLoadSize
operator|+=
name|size
expr_stmt|;
return|return
name|preLoadSize
return|;
block|}
specifier|private
name|int
name|decrementPreloadSize
parameter_list|(
name|int
name|size
parameter_list|)
block|{
name|preLoadSize
operator|-=
name|size
expr_stmt|;
return|return
name|preLoadSize
return|;
block|}
comment|/**      * @param node      * @param message TODO      * @return      */
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
comment|/**      * Use when a matched message is about to be dispatched to the client.      *       * @param node      * @return false if the message should not be dispatched to the client (another sub may have already dispatched it for example).      */
specifier|abstract
specifier|protected
name|boolean
name|canDispatch
parameter_list|(
name|MessageReference
name|node
parameter_list|)
function_decl|;
comment|/**      * Used during acknowledgment to remove the message.      * @throws IOException       */
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
block|{             }
block|}
end_class

end_unit

