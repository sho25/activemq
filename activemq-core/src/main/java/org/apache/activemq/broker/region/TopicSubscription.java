begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  *   * Licensed to the Apache Software Foundation (ASF) under one or more contributor license agreements. See the NOTICE  * file distributed with this work for additional information regarding copyright ownership. The ASF licenses this file  * to You under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the  * License. You may obtain a copy of the License at  *   * http://www.apache.org/licenses/LICENSE-2.0  *   * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on  * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the  * specific language governing permissions and limitations under the License.  */
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
name|LinkedList
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|concurrent
operator|.
name|atomic
operator|.
name|AtomicLong
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
name|FilePendingMessageCursor
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
name|policy
operator|.
name|MessageEvictionStrategy
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
name|OldestMessageEvictionStrategy
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
name|LOG
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
specifier|private
specifier|static
specifier|final
name|AtomicLong
name|cursorNameCounter
init|=
operator|new
name|AtomicLong
argument_list|(
literal|0
argument_list|)
decl_stmt|;
specifier|protected
name|PendingMessageCursor
name|matched
decl_stmt|;
specifier|final
specifier|protected
name|UsageManager
name|usageManager
decl_stmt|;
specifier|protected
name|AtomicLong
name|dispatchedCounter
init|=
operator|new
name|AtomicLong
argument_list|()
decl_stmt|;
specifier|protected
name|AtomicLong
name|prefetchExtension
init|=
operator|new
name|AtomicLong
argument_list|()
decl_stmt|;
specifier|private
name|int
name|maximumPendingMessages
init|=
operator|-
literal|1
decl_stmt|;
specifier|private
name|MessageEvictionStrategy
name|messageEvictionStrategy
init|=
operator|new
name|OldestMessageEvictionStrategy
argument_list|()
decl_stmt|;
specifier|private
name|int
name|discarded
decl_stmt|;
specifier|private
specifier|final
name|Object
name|matchedListMutex
init|=
operator|new
name|Object
argument_list|()
decl_stmt|;
specifier|private
specifier|final
name|AtomicLong
name|enqueueCounter
init|=
operator|new
name|AtomicLong
argument_list|(
literal|0
argument_list|)
decl_stmt|;
specifier|private
specifier|final
name|AtomicLong
name|dequeueCounter
init|=
operator|new
name|AtomicLong
argument_list|(
literal|0
argument_list|)
decl_stmt|;
name|boolean
name|singleDestination
init|=
literal|true
decl_stmt|;
name|Destination
name|destination
decl_stmt|;
specifier|private
name|int
name|memoryUsageHighWaterMark
init|=
literal|95
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
name|Exception
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
name|String
name|matchedName
init|=
literal|"TopicSubscription:"
operator|+
name|cursorNameCounter
operator|.
name|getAndIncrement
argument_list|()
operator|+
literal|"["
operator|+
name|info
operator|.
name|getConsumerId
argument_list|()
operator|.
name|toString
argument_list|()
operator|+
literal|"]"
decl_stmt|;
name|this
operator|.
name|matched
operator|=
operator|new
name|FilePendingMessageCursor
argument_list|(
name|matchedName
argument_list|,
name|broker
operator|.
name|getTempDataStore
argument_list|()
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|init
parameter_list|()
throws|throws
name|Exception
block|{
name|this
operator|.
name|matched
operator|.
name|setUsageManager
argument_list|(
name|usageManager
argument_list|)
expr_stmt|;
name|this
operator|.
name|matched
operator|.
name|start
argument_list|()
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
name|Exception
block|{
name|enqueueCounter
operator|.
name|incrementAndGet
argument_list|()
expr_stmt|;
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
name|isSlave
argument_list|()
condition|)
block|{
name|optimizePrefetch
argument_list|()
expr_stmt|;
comment|// if maximumPendingMessages is set we will only discard messages
comment|// which
comment|// have not been dispatched (i.e. we allow the prefetch buffer to be
comment|// filled)
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
name|matchedListMutex
init|)
block|{
name|matched
operator|.
name|addMessageLast
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
comment|// calculate the high water mark from which point we
comment|// will eagerly evict expired messages
name|int
name|max
init|=
name|messageEvictionStrategy
operator|.
name|getEvictExpiredMessagesHighWatermark
argument_list|()
decl_stmt|;
if|if
condition|(
name|maximumPendingMessages
operator|>
literal|0
operator|&&
name|maximumPendingMessages
operator|<
name|max
condition|)
block|{
name|max
operator|=
name|maximumPendingMessages
expr_stmt|;
block|}
if|if
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
name|max
condition|)
block|{
name|removeExpiredMessages
argument_list|()
expr_stmt|;
block|}
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
name|int
name|pageInSize
init|=
name|matched
operator|.
name|size
argument_list|()
operator|-
name|maximumPendingMessages
decl_stmt|;
comment|// only page in a 1000 at a time - else we could
comment|// blow da memory
name|pageInSize
operator|=
name|Math
operator|.
name|max
argument_list|(
literal|1000
argument_list|,
name|pageInSize
argument_list|)
expr_stmt|;
name|LinkedList
name|list
init|=
name|matched
operator|.
name|pageInList
argument_list|(
name|pageInSize
argument_list|)
decl_stmt|;
name|MessageReference
index|[]
name|oldMessages
init|=
name|messageEvictionStrategy
operator|.
name|evictMessages
argument_list|(
name|list
argument_list|)
decl_stmt|;
name|int
name|messagesToEvict
init|=
name|oldMessages
operator|.
name|length
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|messagesToEvict
condition|;
name|i
operator|++
control|)
block|{
name|MessageReference
name|oldMessage
init|=
name|oldMessages
index|[
name|i
index|]
decl_stmt|;
name|discard
argument_list|(
name|oldMessage
argument_list|)
expr_stmt|;
block|}
comment|// lets avoid an infinite loop if we are given a bad
comment|// eviction strategy
comment|// for a bad strategy lets just not evict
if|if
condition|(
name|messagesToEvict
operator|==
literal|0
condition|)
block|{
name|LOG
operator|.
name|warn
argument_list|(
literal|"No messages to evict returned from eviction strategy: "
operator|+
name|messageEvictionStrategy
argument_list|)
expr_stmt|;
break|break;
block|}
block|}
block|}
block|}
block|}
block|}
block|}
comment|/**      * Discard any expired messages from the matched list. Called from a      * synchronized block.      *       * @throws IOException      */
specifier|protected
name|void
name|removeExpiredMessages
parameter_list|()
throws|throws
name|IOException
block|{
try|try
block|{
name|matched
operator|.
name|reset
argument_list|()
expr_stmt|;
while|while
condition|(
name|matched
operator|.
name|hasNext
argument_list|()
condition|)
block|{
name|MessageReference
name|node
init|=
name|matched
operator|.
name|next
argument_list|()
decl_stmt|;
if|if
condition|(
name|broker
operator|.
name|isExpired
argument_list|(
name|node
argument_list|)
condition|)
block|{
name|matched
operator|.
name|remove
argument_list|()
expr_stmt|;
name|dispatchedCounter
operator|.
name|incrementAndGet
argument_list|()
expr_stmt|;
name|node
operator|.
name|decrementReferenceCount
argument_list|()
expr_stmt|;
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
break|break;
block|}
block|}
block|}
finally|finally
block|{
name|matched
operator|.
name|release
argument_list|()
expr_stmt|;
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
name|matchedListMutex
init|)
block|{
try|try
block|{
name|matched
operator|.
name|reset
argument_list|()
expr_stmt|;
while|while
condition|(
name|matched
operator|.
name|hasNext
argument_list|()
condition|)
block|{
name|MessageReference
name|node
init|=
name|matched
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
name|matched
operator|.
name|remove
argument_list|()
expr_stmt|;
name|dispatchedCounter
operator|.
name|incrementAndGet
argument_list|()
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
finally|finally
block|{
name|matched
operator|.
name|release
argument_list|()
expr_stmt|;
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
name|prefetchExtension
operator|.
name|addAndGet
argument_list|(
name|ack
operator|.
name|getMessageCount
argument_list|()
argument_list|)
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
name|Exception
block|{
synchronized|synchronized
init|(
name|TopicSubscription
operator|.
name|this
init|)
block|{
if|if
condition|(
name|singleDestination
operator|&&
name|destination
operator|!=
literal|null
condition|)
block|{
name|destination
operator|.
name|getDestinationStatistics
argument_list|()
operator|.
name|getDequeues
argument_list|()
operator|.
name|add
argument_list|(
name|ack
operator|.
name|getMessageCount
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
name|dequeueCounter
operator|.
name|addAndGet
argument_list|(
name|ack
operator|.
name|getMessageCount
argument_list|()
argument_list|)
expr_stmt|;
name|prefetchExtension
operator|.
name|addAndGet
argument_list|(
name|ack
operator|.
name|getMessageCount
argument_list|()
argument_list|)
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
name|singleDestination
operator|&&
name|destination
operator|!=
literal|null
condition|)
block|{
name|destination
operator|.
name|getDestinationStatistics
argument_list|()
operator|.
name|getDequeues
argument_list|()
operator|.
name|add
argument_list|(
name|ack
operator|.
name|getMessageCount
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|dequeueCounter
operator|.
name|addAndGet
argument_list|(
name|ack
operator|.
name|getMessageCount
argument_list|()
argument_list|)
expr_stmt|;
name|prefetchExtension
operator|.
name|addAndGet
argument_list|(
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
comment|// Message was delivered but not acknowledged: update pre-fetch
comment|// counters.
name|prefetchExtension
operator|.
name|addAndGet
argument_list|(
name|ack
operator|.
name|getMessageCount
argument_list|()
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
comment|// not supported for topics
return|return
literal|null
return|;
block|}
specifier|public
name|int
name|getPendingQueueSize
parameter_list|()
block|{
return|return
name|matched
argument_list|()
return|;
block|}
specifier|public
name|int
name|getDispatchedQueueSize
parameter_list|()
block|{
return|return
call|(
name|int
call|)
argument_list|(
name|dispatchedCounter
operator|.
name|get
argument_list|()
operator|-
name|dequeueCounter
operator|.
name|get
argument_list|()
argument_list|)
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
specifier|public
name|long
name|getDispatchedCounter
parameter_list|()
block|{
return|return
name|dispatchedCounter
operator|.
name|get
argument_list|()
return|;
block|}
specifier|public
name|long
name|getEnqueueCounter
parameter_list|()
block|{
return|return
name|enqueueCounter
operator|.
name|get
argument_list|()
return|;
block|}
specifier|public
name|long
name|getDequeueCounter
parameter_list|()
block|{
return|return
name|dequeueCounter
operator|.
name|get
argument_list|()
return|;
block|}
comment|/**      * @return the number of messages discarded due to being a slow consumer      */
specifier|public
name|int
name|discarded
parameter_list|()
block|{
synchronized|synchronized
init|(
name|matchedListMutex
init|)
block|{
return|return
name|discarded
return|;
block|}
block|}
comment|/**      * @return the number of matched messages (messages targeted for the      *         subscription but not yet able to be dispatched due to the      *         prefetch buffer being full).      */
specifier|public
name|int
name|matched
parameter_list|()
block|{
synchronized|synchronized
init|(
name|matchedListMutex
init|)
block|{
return|return
name|matched
operator|.
name|size
argument_list|()
return|;
block|}
block|}
comment|/**      * Sets the maximum number of pending messages that can be matched against      * this consumer before old messages are discarded.      */
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
specifier|public
name|MessageEvictionStrategy
name|getMessageEvictionStrategy
parameter_list|()
block|{
return|return
name|messageEvictionStrategy
return|;
block|}
comment|/**      * Sets the eviction strategy used to decide which message to evict when the      * slow consumer needs to discard messages      */
specifier|public
name|void
name|setMessageEvictionStrategy
parameter_list|(
name|MessageEvictionStrategy
name|messageEvictionStrategy
parameter_list|)
block|{
name|this
operator|.
name|messageEvictionStrategy
operator|=
name|messageEvictionStrategy
expr_stmt|;
block|}
comment|// Implementation methods
comment|// -------------------------------------------------------------------------
specifier|private
name|boolean
name|isFull
parameter_list|()
block|{
return|return
name|getDispatchedQueueSize
argument_list|()
operator|-
name|prefetchExtension
operator|.
name|get
argument_list|()
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
name|getDispatchedQueueSize
argument_list|()
operator|-
name|prefetchExtension
operator|.
name|get
argument_list|()
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
name|getDispatchedQueueSize
argument_list|()
operator|-
name|prefetchExtension
operator|.
name|get
argument_list|()
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
comment|/**      * @param memoryUsageHighWaterMark the memoryUsageHighWaterMark to set      */
specifier|public
name|void
name|setMemoryUsageHighWaterMark
parameter_list|(
name|int
name|memoryUsageHighWaterMark
parameter_list|)
block|{
name|this
operator|.
name|memoryUsageHighWaterMark
operator|=
name|memoryUsageHighWaterMark
expr_stmt|;
block|}
comment|/**      * @return the memoryUsageHighWaterMark      */
specifier|public
name|int
name|getMemoryUsageHighWaterMark
parameter_list|()
block|{
return|return
name|this
operator|.
name|memoryUsageHighWaterMark
return|;
block|}
comment|/**      * @return the usageManager      */
specifier|public
name|UsageManager
name|getUsageManager
parameter_list|()
block|{
return|return
name|this
operator|.
name|usageManager
return|;
block|}
comment|/**      * @return the matched      */
specifier|public
name|PendingMessageCursor
name|getMatched
parameter_list|()
block|{
return|return
name|this
operator|.
name|matched
return|;
block|}
comment|/**      * @param matched the matched to set      */
specifier|public
name|void
name|setMatched
parameter_list|(
name|PendingMessageCursor
name|matched
parameter_list|)
block|{
name|this
operator|.
name|matched
operator|=
name|matched
expr_stmt|;
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
comment|/**      * optimize message consumer prefetch if the consumer supports it      */
specifier|public
name|void
name|optimizePrefetch
parameter_list|()
block|{
comment|/*          * if(info!=null&&info.isOptimizedAcknowledge()&&context!=null&&context.getConnection()!=null          *&&context.getConnection().isManageable()){          * if(info.getCurrentPrefetchSize()!=info.getPrefetchSize()&&          * isLowWaterMark()){          * info.setCurrentPrefetchSize(info.getPrefetchSize());          * updateConsumerPrefetch(info.getPrefetchSize()); }else          * if(info.getCurrentPrefetchSize()==info.getPrefetchSize()&&          * isHighWaterMark()){ // want to purge any outstanding acks held by the          * consumer info.setCurrentPrefetchSize(1); updateConsumerPrefetch(1); } }          */
block|}
specifier|private
name|void
name|dispatchMatched
parameter_list|()
throws|throws
name|IOException
block|{
synchronized|synchronized
init|(
name|matchedListMutex
init|)
block|{
try|try
block|{
name|matched
operator|.
name|reset
argument_list|()
expr_stmt|;
while|while
condition|(
name|matched
operator|.
name|hasNext
argument_list|()
operator|&&
operator|!
name|isFull
argument_list|()
condition|)
block|{
name|MessageReference
name|message
init|=
operator|(
name|MessageReference
operator|)
name|matched
operator|.
name|next
argument_list|()
decl_stmt|;
name|matched
operator|.
name|remove
argument_list|()
expr_stmt|;
comment|// Message may have been sitting in the matched list a while
comment|// waiting for the consumer to ak the message.
if|if
condition|(
name|broker
operator|.
name|isExpired
argument_list|(
name|message
argument_list|)
condition|)
block|{
name|message
operator|.
name|decrementReferenceCount
argument_list|()
expr_stmt|;
name|broker
operator|.
name|messageExpired
argument_list|(
name|getContext
argument_list|()
argument_list|,
name|message
argument_list|)
expr_stmt|;
name|dequeueCounter
operator|.
name|incrementAndGet
argument_list|()
expr_stmt|;
continue|continue;
comment|// just drop it.
block|}
name|dispatch
argument_list|(
name|message
argument_list|)
expr_stmt|;
block|}
block|}
finally|finally
block|{
name|matched
operator|.
name|release
argument_list|()
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
name|dispatchedCounter
operator|.
name|incrementAndGet
argument_list|()
expr_stmt|;
comment|// Keep track if this subscription is receiving messages from a single
comment|// destination.
if|if
condition|(
name|singleDestination
condition|)
block|{
if|if
condition|(
name|destination
operator|==
literal|null
condition|)
block|{
name|destination
operator|=
name|node
operator|.
name|getRegionDestination
argument_list|()
expr_stmt|;
block|}
else|else
block|{
if|if
condition|(
name|destination
operator|!=
name|node
operator|.
name|getRegionDestination
argument_list|()
condition|)
block|{
name|singleDestination
operator|=
literal|false
expr_stmt|;
block|}
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
name|node
operator|.
name|decrementReferenceCount
argument_list|()
expr_stmt|;
block|}
block|}
specifier|private
name|void
name|discard
parameter_list|(
name|MessageReference
name|message
parameter_list|)
block|{
name|message
operator|.
name|decrementReferenceCount
argument_list|()
expr_stmt|;
name|matched
operator|.
name|remove
argument_list|(
name|message
argument_list|)
expr_stmt|;
name|discarded
operator|++
expr_stmt|;
name|dequeueCounter
operator|.
name|incrementAndGet
argument_list|()
expr_stmt|;
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
literal|"Discarding message "
operator|+
name|message
argument_list|)
expr_stmt|;
block|}
name|broker
operator|.
name|getRoot
argument_list|()
operator|.
name|sendToDeadLetterQueue
argument_list|(
name|getContext
argument_list|()
argument_list|,
name|message
argument_list|)
expr_stmt|;
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
name|getDispatchedQueueSize
argument_list|()
operator|+
literal|", delivered="
operator|+
name|getDequeueCounter
argument_list|()
operator|+
literal|", matched="
operator|+
name|matched
argument_list|()
operator|+
literal|", discarded="
operator|+
name|discarded
argument_list|()
return|;
block|}
specifier|public
name|void
name|destroy
parameter_list|()
block|{
synchronized|synchronized
init|(
name|matchedListMutex
init|)
block|{
try|try
block|{
name|matched
operator|.
name|destroy
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
name|LOG
operator|.
name|warn
argument_list|(
literal|"Failed to destroy cursor"
argument_list|,
name|e
argument_list|)
expr_stmt|;
block|}
block|}
block|}
specifier|public
name|int
name|getPrefetchSize
parameter_list|()
block|{
return|return
call|(
name|int
call|)
argument_list|(
name|info
operator|.
name|getPrefetchSize
argument_list|()
operator|+
name|prefetchExtension
operator|.
name|get
argument_list|()
argument_list|)
return|;
block|}
block|}
end_class

end_unit

