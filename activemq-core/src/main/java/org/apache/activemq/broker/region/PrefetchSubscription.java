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
name|cursors
operator|.
name|PendingMessageCursor
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
name|cursors
operator|.
name|VMPendingMessageCursor
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
name|command
operator|.
name|MessagePull
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
name|thread
operator|.
name|Scheduler
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

begin_comment
comment|/**  * A subscription that honors the pre-fetch option of the ConsumerInfo.  *   * @version $Revision: 1.15 $  */
end_comment

begin_class
specifier|public
specifier|abstract
class|class
name|PrefetchSubscription
extends|extends
name|AbstractSubscription
block|{
specifier|private
specifier|static
specifier|final
name|Log
name|LOG
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
specifier|protected
name|PendingMessageCursor
name|pending
decl_stmt|;
specifier|protected
specifier|final
name|LinkedList
argument_list|<
name|MessageReference
argument_list|>
name|dispatched
init|=
operator|new
name|LinkedList
argument_list|<
name|MessageReference
argument_list|>
argument_list|()
decl_stmt|;
specifier|protected
name|int
name|prefetchExtension
decl_stmt|;
specifier|protected
name|long
name|enqueueCounter
decl_stmt|;
specifier|protected
name|long
name|dispatchCounter
decl_stmt|;
specifier|protected
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
parameter_list|,
name|PendingMessageCursor
name|cursor
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
name|pending
operator|=
name|cursor
expr_stmt|;
block|}
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
name|this
argument_list|(
name|broker
argument_list|,
name|context
argument_list|,
name|info
argument_list|,
operator|new
name|VMPendingMessageCursor
argument_list|()
argument_list|)
expr_stmt|;
block|}
comment|/**      * Allows a message to be pulled on demand by a client      */
specifier|public
specifier|synchronized
name|Response
name|pullMessage
parameter_list|(
name|ConnectionContext
name|context
parameter_list|,
name|MessagePull
name|pull
parameter_list|)
throws|throws
name|Exception
block|{
comment|// The slave should not deliver pull messages. TODO: when the slave
comment|// becomes a master,
comment|// He should send a NULL message to all the consumers to 'wake them up'
comment|// in case
comment|// they were waiting for a message.
if|if
condition|(
name|getPrefetchSize
argument_list|()
operator|==
literal|0
operator|&&
operator|!
name|isSlave
argument_list|()
condition|)
block|{
name|prefetchExtension
operator|++
expr_stmt|;
specifier|final
name|long
name|dispatchCounterBeforePull
init|=
name|dispatchCounter
decl_stmt|;
name|dispatchMatched
argument_list|()
expr_stmt|;
comment|// If there was nothing dispatched.. we may need to setup a timeout.
if|if
condition|(
name|dispatchCounterBeforePull
operator|==
name|dispatchCounter
condition|)
block|{
comment|// imediate timeout used by receiveNoWait()
if|if
condition|(
name|pull
operator|.
name|getTimeout
argument_list|()
operator|==
operator|-
literal|1
condition|)
block|{
comment|// Send a NULL message.
name|add
argument_list|(
name|QueueMessageReference
operator|.
name|NULL_MESSAGE
argument_list|)
expr_stmt|;
name|dispatchMatched
argument_list|()
expr_stmt|;
block|}
if|if
condition|(
name|pull
operator|.
name|getTimeout
argument_list|()
operator|>
literal|0
condition|)
block|{
name|Scheduler
operator|.
name|executeAfterDelay
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
name|pullTimeout
argument_list|(
name|dispatchCounterBeforePull
argument_list|)
expr_stmt|;
block|}
block|}
argument_list|,
name|pull
operator|.
name|getTimeout
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
block|}
return|return
literal|null
return|;
block|}
comment|/**      * Occurs when a pull times out. If nothing has been dispatched since the      * timeout was setup, then send the NULL message.      */
specifier|final
specifier|synchronized
name|void
name|pullTimeout
parameter_list|(
name|long
name|dispatchCounterBeforePull
parameter_list|)
block|{
if|if
condition|(
name|dispatchCounterBeforePull
operator|==
name|dispatchCounter
condition|)
block|{
try|try
block|{
name|add
argument_list|(
name|QueueMessageReference
operator|.
name|NULL_MESSAGE
argument_list|)
expr_stmt|;
name|dispatchMatched
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
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
specifier|public
specifier|synchronized
name|void
name|add
parameter_list|(
name|MessageReference
name|node
parameter_list|)
throws|throws
name|Exception
block|{
name|boolean
name|pendingEmpty
init|=
literal|false
decl_stmt|;
name|pendingEmpty
operator|=
name|pending
operator|.
name|isEmpty
argument_list|()
expr_stmt|;
name|enqueueCounter
operator|++
expr_stmt|;
if|if
condition|(
operator|!
name|isFull
argument_list|()
operator|&&
name|pendingEmpty
operator|&&
operator|!
name|isSlave
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
operator|&&
name|LOG
operator|.
name|isDebugEnabled
argument_list|()
condition|)
block|{
name|LOG
operator|.
name|debug
argument_list|(
literal|"Prefetch limit."
argument_list|)
expr_stmt|;
block|}
name|pending
operator|.
name|addMessageLast
argument_list|(
name|node
argument_list|)
expr_stmt|;
block|}
block|}
block|}
specifier|public
specifier|synchronized
name|void
name|processMessageDispatchNotification
parameter_list|(
name|MessageDispatchNotification
name|mdn
parameter_list|)
throws|throws
name|Exception
block|{
try|try
block|{
name|pending
operator|.
name|reset
argument_list|()
expr_stmt|;
while|while
condition|(
name|pending
operator|.
name|hasNext
argument_list|()
condition|)
block|{
name|MessageReference
name|node
init|=
name|pending
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
name|pending
operator|.
name|remove
argument_list|()
expr_stmt|;
name|createMessageDispatch
argument_list|(
name|node
argument_list|,
name|node
operator|.
name|getMessage
argument_list|()
argument_list|)
expr_stmt|;
name|dispatched
operator|.
name|addLast
argument_list|(
name|node
argument_list|)
expr_stmt|;
return|return;
block|}
block|}
block|}
finally|finally
block|{
name|pending
operator|.
name|release
argument_list|()
expr_stmt|;
block|}
throw|throw
operator|new
name|JMSException
argument_list|(
literal|"Slave broker out of sync with master: Dispatched message ("
operator|+
name|mdn
operator|.
name|getMessageId
argument_list|()
operator|+
literal|") was not in the pending list"
argument_list|)
throw|;
block|}
specifier|public
specifier|synchronized
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
name|callDispatchMatched
init|=
literal|false
decl_stmt|;
if|if
condition|(
name|ack
operator|.
name|isStandardAck
argument_list|()
condition|)
block|{
comment|// Acknowledge all dispatched messages up till the message id of the
comment|// acknowledgment.
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
argument_list|<
name|MessageReference
argument_list|>
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
name|node
operator|.
name|getRegionDestination
argument_list|()
operator|.
name|getDestinationStatistics
argument_list|()
operator|.
name|getDequeues
argument_list|()
operator|.
name|increment
argument_list|()
expr_stmt|;
name|iter
operator|.
name|remove
argument_list|()
expr_stmt|;
block|}
else|else
block|{
comment|// setup a Synchronization to remove nodes from the
comment|// dispatched list.
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
name|node
operator|.
name|getRegionDestination
argument_list|()
operator|.
name|getDestinationStatistics
argument_list|()
operator|.
name|getDequeues
argument_list|()
operator|.
name|increment
argument_list|()
expr_stmt|;
name|prefetchExtension
operator|--
expr_stmt|;
block|}
block|}
specifier|public
name|void
name|afterRollback
parameter_list|()
throws|throws
name|Exception
block|{
name|super
operator|.
name|afterRollback
argument_list|()
expr_stmt|;
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
block|{
comment|// extend prefetch window only if not a pulling
comment|// consumer
if|if
condition|(
name|getPrefetchSize
argument_list|()
operator|!=
literal|0
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
block|}
block|}
else|else
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
block|}
name|callDispatchMatched
operator|=
literal|true
expr_stmt|;
break|break;
block|}
block|}
block|}
comment|// this only happens after a reconnect - get an ack which is not
comment|// valid
if|if
condition|(
operator|!
name|callDispatchMatched
condition|)
block|{
if|if
condition|(
name|LOG
operator|.
name|isDebugEnabled
argument_list|()
condition|)
block|{
name|LOG
operator|.
name|debug
argument_list|(
literal|"Could not correlate acknowledgment with dispatched message: "
operator|+
name|ack
argument_list|)
expr_stmt|;
block|}
block|}
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
comment|// Message was delivered but not acknowledged: update pre-fetch
comment|// counters.
comment|// Acknowledge all dispatched messages up till the message id of the
comment|// acknowledgment.
name|int
name|index
init|=
literal|0
decl_stmt|;
for|for
control|(
name|Iterator
argument_list|<
name|MessageReference
argument_list|>
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
name|callDispatchMatched
operator|=
literal|true
expr_stmt|;
break|break;
block|}
block|}
if|if
condition|(
operator|!
name|callDispatchMatched
condition|)
block|{
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
block|}
elseif|else
if|if
condition|(
name|ack
operator|.
name|isRedeliveredAck
argument_list|()
condition|)
block|{
comment|// Message was re-delivered but it was not yet considered to be a DLQ message.
comment|// Acknowledge all dispatched messages up till the message id of the
comment|// acknowledgment.
name|boolean
name|inAckRange
init|=
literal|false
decl_stmt|;
for|for
control|(
name|Iterator
argument_list|<
name|MessageReference
argument_list|>
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
name|node
operator|.
name|incrementRedeliveryCounter
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
name|callDispatchMatched
operator|=
literal|true
expr_stmt|;
break|break;
block|}
block|}
block|}
if|if
condition|(
operator|!
name|callDispatchMatched
condition|)
block|{
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
block|{
throw|throw
operator|new
name|JMSException
argument_list|(
literal|"Poison ack cannot be transacted: "
operator|+
name|ack
argument_list|)
throw|;
block|}
comment|// Acknowledge all dispatched messages up till the message id of the
comment|// acknowledgment.
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
argument_list|<
name|MessageReference
argument_list|>
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
name|node
operator|.
name|getRegionDestination
argument_list|()
operator|.
name|getDestinationStatistics
argument_list|()
operator|.
name|getDequeues
argument_list|()
operator|.
name|increment
argument_list|()
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
name|callDispatchMatched
operator|=
literal|true
expr_stmt|;
break|break;
block|}
block|}
block|}
if|if
condition|(
operator|!
name|callDispatchMatched
condition|)
block|{
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
block|}
if|if
condition|(
name|callDispatchMatched
condition|)
block|{
name|dispatchMatched
argument_list|()
expr_stmt|;
block|}
else|else
block|{
if|if
condition|(
name|isSlave
argument_list|()
condition|)
block|{
throw|throw
operator|new
name|JMSException
argument_list|(
literal|"Slave broker out of sync with master: Acknowledgment ("
operator|+
name|ack
operator|+
literal|") was not in the dispatch list: "
operator|+
name|dispatched
argument_list|)
throw|;
block|}
else|else
block|{
name|LOG
operator|.
name|debug
argument_list|(
literal|"Acknowledgment out of sync (Normally occurs when failover connection reconnects): "
operator|+
name|ack
argument_list|)
expr_stmt|;
block|}
block|}
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
name|broker
operator|.
name|sendToDeadLetterQueue
argument_list|(
name|context
argument_list|,
name|node
argument_list|)
expr_stmt|;
block|}
comment|/**      * Used to determine if the broker can dispatch to the consumer.      *       * @return      */
specifier|protected
specifier|synchronized
name|boolean
name|isFull
parameter_list|()
block|{
return|return
name|isSlave
argument_list|()
operator|||
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
specifier|synchronized
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
specifier|synchronized
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
specifier|public
specifier|synchronized
name|int
name|countBeforeFull
parameter_list|()
block|{
return|return
name|info
operator|.
name|getPrefetchSize
argument_list|()
operator|+
name|prefetchExtension
operator|-
name|dispatched
operator|.
name|size
argument_list|()
return|;
block|}
specifier|public
specifier|synchronized
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
specifier|public
specifier|synchronized
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
specifier|public
specifier|synchronized
name|long
name|getDequeueCounter
parameter_list|()
block|{
return|return
name|dequeueCounter
return|;
block|}
specifier|public
specifier|synchronized
name|long
name|getDispatchedCounter
parameter_list|()
block|{
return|return
name|dispatchCounter
return|;
block|}
specifier|public
specifier|synchronized
name|long
name|getEnqueueCounter
parameter_list|()
block|{
return|return
name|enqueueCounter
return|;
block|}
specifier|public
name|boolean
name|isRecoveryRequired
parameter_list|()
block|{
return|return
name|pending
operator|.
name|isRecoveryRequired
argument_list|()
return|;
block|}
specifier|public
specifier|synchronized
name|PendingMessageCursor
name|getPending
parameter_list|()
block|{
return|return
name|this
operator|.
name|pending
return|;
block|}
specifier|public
specifier|synchronized
name|void
name|setPending
parameter_list|(
name|PendingMessageCursor
name|pending
parameter_list|)
block|{
name|this
operator|.
name|pending
operator|=
name|pending
expr_stmt|;
block|}
comment|/**      * optimize message consumer prefetch if the consumer supports it      */
specifier|public
name|void
name|optimizePrefetch
parameter_list|()
block|{
comment|/*          * if(info!=null&&info.isOptimizedAcknowledge()&&context!=null&&context.getConnection()!=null          *&&context.getConnection().isManageable()){          * if(info.getCurrentPrefetchSize()!=info.getPrefetchSize()&&          * isLowWaterMark()){          * info.setCurrentPrefetchSize(info.getPrefetchSize());          * updateConsumerPrefetch(info.getPrefetchSize()); }else          * if(info.getCurrentPrefetchSize()==info.getPrefetchSize()&&          * isHighWaterMark()){ // want to purge any outstanding acks held by the          * consumer info.setCurrentPrefetchSize(1); updateConsumerPrefetch(1); } }          */
block|}
specifier|public
specifier|synchronized
name|void
name|add
parameter_list|(
name|ConnectionContext
name|context
parameter_list|,
name|Destination
name|destination
parameter_list|)
throws|throws
name|Exception
block|{
name|super
operator|.
name|add
argument_list|(
name|context
argument_list|,
name|destination
argument_list|)
expr_stmt|;
name|pending
operator|.
name|add
argument_list|(
name|context
argument_list|,
name|destination
argument_list|)
expr_stmt|;
block|}
specifier|public
specifier|synchronized
name|void
name|remove
parameter_list|(
name|ConnectionContext
name|context
parameter_list|,
name|Destination
name|destination
parameter_list|)
throws|throws
name|Exception
block|{
name|super
operator|.
name|remove
argument_list|(
name|context
argument_list|,
name|destination
argument_list|)
expr_stmt|;
name|pending
operator|.
name|remove
argument_list|(
name|context
argument_list|,
name|destination
argument_list|)
expr_stmt|;
block|}
specifier|protected
specifier|synchronized
name|void
name|dispatchMatched
parameter_list|()
throws|throws
name|IOException
block|{
if|if
condition|(
operator|!
name|isSlave
argument_list|()
condition|)
block|{
try|try
block|{
name|int
name|numberToDispatch
init|=
name|countBeforeFull
argument_list|()
decl_stmt|;
if|if
condition|(
name|numberToDispatch
operator|>
literal|0
condition|)
block|{
name|pending
operator|.
name|setMaxBatchSize
argument_list|(
name|numberToDispatch
argument_list|)
expr_stmt|;
name|int
name|count
init|=
literal|0
decl_stmt|;
name|pending
operator|.
name|reset
argument_list|()
expr_stmt|;
while|while
condition|(
name|pending
operator|.
name|hasNext
argument_list|()
operator|&&
operator|!
name|isFull
argument_list|()
operator|&&
name|count
operator|<
name|numberToDispatch
condition|)
block|{
name|MessageReference
name|node
init|=
name|pending
operator|.
name|next
argument_list|()
decl_stmt|;
if|if
condition|(
name|node
operator|==
literal|null
condition|)
block|{
break|break;
block|}
if|if
condition|(
name|canDispatch
argument_list|(
name|node
argument_list|)
condition|)
block|{
name|pending
operator|.
name|remove
argument_list|()
expr_stmt|;
comment|// Message may have been sitting in the pending list
comment|// a while
comment|// waiting for the consumer to ak the message.
if|if
condition|(
name|node
operator|!=
name|QueueMessageReference
operator|.
name|NULL_MESSAGE
operator|&&
name|broker
operator|.
name|isExpired
argument_list|(
name|node
argument_list|)
condition|)
block|{
name|broker
operator|.
name|messageExpired
argument_list|(
name|getContext
argument_list|()
argument_list|,
name|node
argument_list|)
expr_stmt|;
name|dequeueCounter
operator|++
expr_stmt|;
continue|continue;
block|}
name|dispatch
argument_list|(
name|node
argument_list|)
expr_stmt|;
name|count
operator|++
expr_stmt|;
block|}
block|}
block|}
block|}
finally|finally
block|{
name|pending
operator|.
name|release
argument_list|()
expr_stmt|;
block|}
block|}
block|}
specifier|protected
specifier|synchronized
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
name|isSlave
argument_list|()
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
comment|// NULL messages don't count... they don't get Acked.
if|if
condition|(
name|node
operator|!=
name|QueueMessageReference
operator|.
name|NULL_MESSAGE
condition|)
block|{
name|dispatchCounter
operator|++
expr_stmt|;
name|dispatched
operator|.
name|addLast
argument_list|(
name|node
argument_list|)
expr_stmt|;
block|}
else|else
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
literal|1
argument_list|)
expr_stmt|;
block|}
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
name|setTransmitCallback
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
comment|// Since the message gets queued up in async dispatch,
comment|// we don't want to
comment|// decrease the reference count until it gets put on the
comment|// wire.
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
comment|// System.err.println(broker.getBrokerName() + " " + this + " (" +
comment|// enqueueCounter + ", " + dispatchCounter +") " + node);
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
if|if
condition|(
name|node
operator|!=
name|QueueMessageReference
operator|.
name|NULL_MESSAGE
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
name|getDispatched
argument_list|()
operator|.
name|increment
argument_list|()
expr_stmt|;
block|}
block|}
if|if
condition|(
name|info
operator|.
name|isDispatchAsync
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
name|serviceExceptionAsync
argument_list|(
name|e
argument_list|)
expr_stmt|;
block|}
block|}
block|}
comment|/**      * inform the MessageConsumer on the client to change it's prefetch      *       * @param newPrefetch      */
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
comment|/**      * @param node      * @param message      * @return MessageDispatch      */
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
if|if
condition|(
name|node
operator|==
name|QueueMessageReference
operator|.
name|NULL_MESSAGE
condition|)
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
name|setMessage
argument_list|(
literal|null
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
literal|null
argument_list|)
expr_stmt|;
return|return
name|md
return|;
block|}
else|else
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
block|}
comment|/**      * Use when a matched message is about to be dispatched to the client.      *       * @param node      * @return false if the message should not be dispatched to the client      *         (another sub may have already dispatched it for example).      * @throws IOException      */
specifier|protected
specifier|abstract
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
block|{     }
block|}
end_class

end_unit

