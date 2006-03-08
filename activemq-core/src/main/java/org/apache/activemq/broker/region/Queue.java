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
name|ArrayList
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
name|java
operator|.
name|util
operator|.
name|List
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
name|group
operator|.
name|MessageGroupHashBucket
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
name|group
operator|.
name|MessageGroupMap
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
name|group
operator|.
name|MessageGroupSet
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
name|broker
operator|.
name|region
operator|.
name|policy
operator|.
name|DispatchPolicy
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
name|RoundRobinDispatchPolicy
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
name|SharedDeadLetterStrategy
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
name|ConsumerId
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
name|filter
operator|.
name|MessageEvaluationContext
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
name|store
operator|.
name|MessageRecoveryListener
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
name|store
operator|.
name|MessageStore
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
name|TaskRunnerFactory
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
name|Valve
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

begin_import
import|import
name|edu
operator|.
name|emory
operator|.
name|mathcs
operator|.
name|backport
operator|.
name|java
operator|.
name|util
operator|.
name|concurrent
operator|.
name|CopyOnWriteArrayList
import|;
end_import

begin_comment
comment|/**  * The Queue is a List of MessageEntry objects that are dispatched to matching  * subscriptions.  *   * @version $Revision: 1.28 $  */
end_comment

begin_class
specifier|public
class|class
name|Queue
implements|implements
name|Destination
block|{
specifier|private
specifier|final
name|Log
name|log
decl_stmt|;
specifier|protected
specifier|final
name|ActiveMQDestination
name|destination
decl_stmt|;
specifier|protected
specifier|final
name|List
name|consumers
init|=
operator|new
name|CopyOnWriteArrayList
argument_list|()
decl_stmt|;
specifier|protected
specifier|final
name|LinkedList
name|messages
init|=
operator|new
name|LinkedList
argument_list|()
decl_stmt|;
specifier|protected
specifier|final
name|Valve
name|dispatchValve
init|=
operator|new
name|Valve
argument_list|(
literal|true
argument_list|)
decl_stmt|;
specifier|protected
specifier|final
name|UsageManager
name|usageManager
decl_stmt|;
specifier|protected
specifier|final
name|DestinationStatistics
name|destinationStatistics
init|=
operator|new
name|DestinationStatistics
argument_list|()
decl_stmt|;
specifier|private
name|LockOwner
name|exclusiveOwner
decl_stmt|;
specifier|private
name|MessageGroupMap
name|messageGroupOwners
decl_stmt|;
specifier|private
name|int
name|messageGroupHashBucketCount
init|=
literal|1024
decl_stmt|;
specifier|protected
name|long
name|garbageSize
init|=
literal|0
decl_stmt|;
specifier|protected
name|long
name|garbageSizeBeforeCollection
init|=
literal|1000
decl_stmt|;
specifier|private
name|DispatchPolicy
name|dispatchPolicy
init|=
operator|new
name|RoundRobinDispatchPolicy
argument_list|()
decl_stmt|;
specifier|protected
specifier|final
name|MessageStore
name|store
decl_stmt|;
specifier|protected
name|int
name|highestSubscriptionPriority
decl_stmt|;
specifier|private
name|DeadLetterStrategy
name|deadLetterStrategy
init|=
operator|new
name|SharedDeadLetterStrategy
argument_list|()
decl_stmt|;
specifier|public
name|Queue
parameter_list|(
name|ActiveMQDestination
name|destination
parameter_list|,
specifier|final
name|UsageManager
name|memoryManager
parameter_list|,
name|MessageStore
name|store
parameter_list|,
name|DestinationStatistics
name|parentStats
parameter_list|,
name|TaskRunnerFactory
name|taskFactory
parameter_list|)
throws|throws
name|Exception
block|{
name|this
operator|.
name|destination
operator|=
name|destination
expr_stmt|;
name|this
operator|.
name|usageManager
operator|=
name|memoryManager
expr_stmt|;
name|this
operator|.
name|store
operator|=
name|store
expr_stmt|;
name|destinationStatistics
operator|.
name|setParent
argument_list|(
name|parentStats
argument_list|)
expr_stmt|;
name|this
operator|.
name|log
operator|=
name|LogFactory
operator|.
name|getLog
argument_list|(
name|getClass
argument_list|()
operator|.
name|getName
argument_list|()
operator|+
literal|"."
operator|+
name|destination
operator|.
name|getPhysicalName
argument_list|()
argument_list|)
expr_stmt|;
if|if
condition|(
name|store
operator|!=
literal|null
condition|)
block|{
comment|// Restore the persistent messages.
name|store
operator|.
name|recover
argument_list|(
operator|new
name|MessageRecoveryListener
argument_list|()
block|{
specifier|public
name|void
name|recoverMessage
parameter_list|(
name|Message
name|message
parameter_list|)
block|{
name|message
operator|.
name|setRegionDestination
argument_list|(
name|Queue
operator|.
name|this
argument_list|)
expr_stmt|;
name|MessageReference
name|reference
init|=
name|createMessageReference
argument_list|(
name|message
argument_list|)
decl_stmt|;
name|messages
operator|.
name|add
argument_list|(
name|reference
argument_list|)
expr_stmt|;
name|reference
operator|.
name|decrementReferenceCount
argument_list|()
expr_stmt|;
name|destinationStatistics
operator|.
name|getMessages
argument_list|()
operator|.
name|increment
argument_list|()
expr_stmt|;
block|}
specifier|public
name|void
name|recoverMessageReference
parameter_list|(
name|String
name|messageReference
parameter_list|)
throws|throws
name|Exception
block|{
throw|throw
operator|new
name|RuntimeException
argument_list|(
literal|"Should not be called."
argument_list|)
throw|;
block|}
specifier|public
name|void
name|finished
parameter_list|()
block|{                 }
block|}
argument_list|)
expr_stmt|;
block|}
block|}
specifier|public
specifier|synchronized
name|boolean
name|lock
parameter_list|(
name|MessageReference
name|node
parameter_list|,
name|LockOwner
name|lockOwner
parameter_list|)
block|{
if|if
condition|(
name|exclusiveOwner
operator|==
name|lockOwner
condition|)
return|return
literal|true
return|;
if|if
condition|(
name|exclusiveOwner
operator|!=
literal|null
condition|)
return|return
literal|false
return|;
if|if
condition|(
name|lockOwner
operator|.
name|getLockPriority
argument_list|()
operator|<
name|highestSubscriptionPriority
condition|)
return|return
literal|false
return|;
if|if
condition|(
name|lockOwner
operator|.
name|isLockExclusive
argument_list|()
condition|)
block|{
name|exclusiveOwner
operator|=
name|lockOwner
expr_stmt|;
block|}
return|return
literal|true
return|;
block|}
specifier|public
name|void
name|addSubscription
parameter_list|(
name|ConnectionContext
name|context
parameter_list|,
name|Subscription
name|sub
parameter_list|)
throws|throws
name|Exception
block|{
name|sub
operator|.
name|add
argument_list|(
name|context
argument_list|,
name|this
argument_list|)
expr_stmt|;
name|destinationStatistics
operator|.
name|getConsumers
argument_list|()
operator|.
name|increment
argument_list|()
expr_stmt|;
comment|// synchronize with dispatch method so that no new messages are sent
comment|// while
comment|// setting up a subscription. avoid out of order messages, duplicates
comment|// etc.
name|dispatchValve
operator|.
name|turnOff
argument_list|()
expr_stmt|;
name|MessageEvaluationContext
name|msgContext
init|=
name|context
operator|.
name|getMessageEvaluationContext
argument_list|()
decl_stmt|;
try|try
block|{
synchronized|synchronized
init|(
name|consumers
init|)
block|{
name|consumers
operator|.
name|add
argument_list|(
name|sub
argument_list|)
expr_stmt|;
block|}
name|highestSubscriptionPriority
operator|=
name|calcHighestSubscriptionPriority
argument_list|()
expr_stmt|;
name|msgContext
operator|.
name|setDestination
argument_list|(
name|destination
argument_list|)
expr_stmt|;
synchronized|synchronized
init|(
name|messages
init|)
block|{
comment|// Add all the matching messages in the queue to the
comment|// subscription.
for|for
control|(
name|Iterator
name|iter
init|=
name|messages
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
name|IndirectMessageReference
name|node
init|=
operator|(
name|IndirectMessageReference
operator|)
name|iter
operator|.
name|next
argument_list|()
decl_stmt|;
if|if
condition|(
name|node
operator|.
name|isDropped
argument_list|()
condition|)
block|{
continue|continue;
block|}
try|try
block|{
name|msgContext
operator|.
name|setMessageReference
argument_list|(
name|node
argument_list|)
expr_stmt|;
if|if
condition|(
name|sub
operator|.
name|matches
argument_list|(
name|node
argument_list|,
name|msgContext
argument_list|)
condition|)
block|{
name|sub
operator|.
name|add
argument_list|(
name|node
argument_list|)
expr_stmt|;
block|}
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
name|log
operator|.
name|warn
argument_list|(
literal|"Could not load message: "
operator|+
name|e
argument_list|,
name|e
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|}
finally|finally
block|{
name|msgContext
operator|.
name|clear
argument_list|()
expr_stmt|;
name|dispatchValve
operator|.
name|turnOn
argument_list|()
expr_stmt|;
block|}
block|}
specifier|public
name|void
name|removeSubscription
parameter_list|(
name|ConnectionContext
name|context
parameter_list|,
name|Subscription
name|sub
parameter_list|)
throws|throws
name|Exception
block|{
name|destinationStatistics
operator|.
name|getConsumers
argument_list|()
operator|.
name|decrement
argument_list|()
expr_stmt|;
comment|// synchronize with dispatch method so that no new messages are sent
comment|// while
comment|// removing up a subscription.
name|dispatchValve
operator|.
name|turnOff
argument_list|()
expr_stmt|;
try|try
block|{
synchronized|synchronized
init|(
name|consumers
init|)
block|{
name|consumers
operator|.
name|remove
argument_list|(
name|sub
argument_list|)
expr_stmt|;
block|}
name|sub
operator|.
name|remove
argument_list|(
name|context
argument_list|,
name|this
argument_list|)
expr_stmt|;
name|highestSubscriptionPriority
operator|=
name|calcHighestSubscriptionPriority
argument_list|()
expr_stmt|;
name|boolean
name|wasExclusiveOwner
init|=
literal|false
decl_stmt|;
if|if
condition|(
name|exclusiveOwner
operator|==
name|sub
condition|)
block|{
name|exclusiveOwner
operator|=
literal|null
expr_stmt|;
name|wasExclusiveOwner
operator|=
literal|true
expr_stmt|;
block|}
name|ConsumerId
name|consumerId
init|=
name|sub
operator|.
name|getConsumerInfo
argument_list|()
operator|.
name|getConsumerId
argument_list|()
decl_stmt|;
name|MessageGroupSet
name|ownedGroups
init|=
name|getMessageGroupOwners
argument_list|()
operator|.
name|removeConsumer
argument_list|(
name|consumerId
argument_list|)
decl_stmt|;
if|if
condition|(
operator|!
name|sub
operator|.
name|getConsumerInfo
argument_list|()
operator|.
name|isBrowser
argument_list|()
condition|)
block|{
name|MessageEvaluationContext
name|msgContext
init|=
name|context
operator|.
name|getMessageEvaluationContext
argument_list|()
decl_stmt|;
try|try
block|{
name|msgContext
operator|.
name|setDestination
argument_list|(
name|destination
argument_list|)
expr_stmt|;
comment|// lets copy the messages to dispatch to avoid deadlock
name|List
name|messagesToDispatch
init|=
operator|new
name|ArrayList
argument_list|()
decl_stmt|;
synchronized|synchronized
init|(
name|messages
init|)
block|{
for|for
control|(
name|Iterator
name|iter
init|=
name|messages
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
name|IndirectMessageReference
name|node
init|=
operator|(
name|IndirectMessageReference
operator|)
name|iter
operator|.
name|next
argument_list|()
decl_stmt|;
if|if
condition|(
name|node
operator|.
name|isDropped
argument_list|()
condition|)
block|{
continue|continue;
block|}
name|String
name|groupID
init|=
name|node
operator|.
name|getGroupID
argument_list|()
decl_stmt|;
comment|// Re-deliver all messages that the sub locked
if|if
condition|(
name|node
operator|.
name|getLockOwner
argument_list|()
operator|==
name|sub
operator|||
name|wasExclusiveOwner
operator|||
operator|(
name|groupID
operator|!=
literal|null
operator|&&
name|ownedGroups
operator|.
name|contains
argument_list|(
name|groupID
argument_list|)
operator|)
condition|)
block|{
name|messagesToDispatch
operator|.
name|add
argument_list|(
name|node
argument_list|)
expr_stmt|;
block|}
block|}
block|}
comment|// now lets dispatch from the copy of the collection to avoid deadlocks
for|for
control|(
name|Iterator
name|iter
init|=
name|messagesToDispatch
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
name|IndirectMessageReference
name|node
init|=
operator|(
name|IndirectMessageReference
operator|)
name|iter
operator|.
name|next
argument_list|()
decl_stmt|;
name|node
operator|.
name|incrementRedeliveryCounter
argument_list|()
expr_stmt|;
name|node
operator|.
name|unlock
argument_list|()
expr_stmt|;
name|msgContext
operator|.
name|setMessageReference
argument_list|(
name|node
argument_list|)
expr_stmt|;
name|dispatchPolicy
operator|.
name|dispatch
argument_list|(
name|context
argument_list|,
name|node
argument_list|,
name|msgContext
argument_list|,
name|consumers
argument_list|)
expr_stmt|;
block|}
block|}
finally|finally
block|{
name|msgContext
operator|.
name|clear
argument_list|()
expr_stmt|;
block|}
block|}
block|}
finally|finally
block|{
name|dispatchValve
operator|.
name|turnOn
argument_list|()
expr_stmt|;
block|}
block|}
specifier|public
name|void
name|send
parameter_list|(
specifier|final
name|ConnectionContext
name|context
parameter_list|,
specifier|final
name|Message
name|message
parameter_list|)
throws|throws
name|Exception
block|{
if|if
condition|(
name|context
operator|.
name|isProducerFlowControl
argument_list|()
condition|)
name|usageManager
operator|.
name|waitForSpace
argument_list|()
expr_stmt|;
name|message
operator|.
name|setRegionDestination
argument_list|(
name|this
argument_list|)
expr_stmt|;
if|if
condition|(
name|store
operator|!=
literal|null
operator|&&
name|message
operator|.
name|isPersistent
argument_list|()
condition|)
name|store
operator|.
name|addMessage
argument_list|(
name|context
argument_list|,
name|message
argument_list|)
expr_stmt|;
specifier|final
name|MessageReference
name|node
init|=
name|createMessageReference
argument_list|(
name|message
argument_list|)
decl_stmt|;
try|try
block|{
if|if
condition|(
name|context
operator|.
name|isInTransaction
argument_list|()
condition|)
block|{
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
name|dispatch
argument_list|(
name|context
argument_list|,
name|node
argument_list|,
name|message
argument_list|)
expr_stmt|;
block|}
block|}
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|dispatch
argument_list|(
name|context
argument_list|,
name|node
argument_list|,
name|message
argument_list|)
expr_stmt|;
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
block|}
specifier|public
name|void
name|dispose
parameter_list|(
name|ConnectionContext
name|context
parameter_list|)
throws|throws
name|IOException
block|{
if|if
condition|(
name|store
operator|!=
literal|null
condition|)
block|{
name|store
operator|.
name|removeAllMessages
argument_list|(
name|context
argument_list|)
expr_stmt|;
block|}
name|destinationStatistics
operator|.
name|setParent
argument_list|(
literal|null
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|dropEvent
parameter_list|()
block|{
comment|// TODO: need to also decrement when messages expire.
name|destinationStatistics
operator|.
name|getMessages
argument_list|()
operator|.
name|decrement
argument_list|()
expr_stmt|;
synchronized|synchronized
init|(
name|messages
init|)
block|{
name|garbageSize
operator|++
expr_stmt|;
if|if
condition|(
name|garbageSize
operator|>
name|garbageSizeBeforeCollection
condition|)
block|{
name|gc
argument_list|()
expr_stmt|;
block|}
block|}
block|}
specifier|public
name|void
name|gc
parameter_list|()
block|{
synchronized|synchronized
init|(
name|messages
init|)
block|{
for|for
control|(
name|Iterator
name|iter
init|=
name|messages
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
comment|// Remove dropped messages from the queue.
name|IndirectMessageReference
name|node
init|=
operator|(
name|IndirectMessageReference
operator|)
name|iter
operator|.
name|next
argument_list|()
decl_stmt|;
if|if
condition|(
name|node
operator|.
name|isDropped
argument_list|()
condition|)
block|{
name|garbageSize
operator|--
expr_stmt|;
name|iter
operator|.
name|remove
argument_list|()
expr_stmt|;
continue|continue;
block|}
block|}
block|}
block|}
specifier|public
name|void
name|acknowledge
parameter_list|(
name|ConnectionContext
name|context
parameter_list|,
name|Subscription
name|sub
parameter_list|,
name|MessageAck
name|ack
parameter_list|,
name|MessageReference
name|node
parameter_list|)
throws|throws
name|IOException
block|{
if|if
condition|(
name|store
operator|!=
literal|null
operator|&&
name|node
operator|.
name|isPersistent
argument_list|()
condition|)
block|{
comment|// the original ack may be a ranged ack, but we are trying to delete a specific
comment|// message store here so we need to convert to a non ranged ack.
if|if
condition|(
name|ack
operator|.
name|getMessageCount
argument_list|()
operator|>
literal|0
condition|)
block|{
comment|// Dup the ack
name|MessageAck
name|a
init|=
operator|new
name|MessageAck
argument_list|()
decl_stmt|;
name|ack
operator|.
name|copy
argument_list|(
name|a
argument_list|)
expr_stmt|;
name|ack
operator|=
name|a
expr_stmt|;
comment|// Convert to non-ranged.
name|ack
operator|.
name|setFirstMessageId
argument_list|(
name|node
operator|.
name|getMessageId
argument_list|()
argument_list|)
expr_stmt|;
name|ack
operator|.
name|setLastMessageId
argument_list|(
name|node
operator|.
name|getMessageId
argument_list|()
argument_list|)
expr_stmt|;
name|ack
operator|.
name|setMessageCount
argument_list|(
literal|1
argument_list|)
expr_stmt|;
block|}
name|store
operator|.
name|removeMessage
argument_list|(
name|context
argument_list|,
name|ack
argument_list|)
expr_stmt|;
block|}
block|}
specifier|public
name|Message
name|loadMessage
parameter_list|(
name|MessageId
name|messageId
parameter_list|)
throws|throws
name|IOException
block|{
name|Message
name|msg
init|=
name|store
operator|.
name|getMessage
argument_list|(
name|messageId
argument_list|)
decl_stmt|;
if|if
condition|(
name|msg
operator|!=
literal|null
condition|)
block|{
name|msg
operator|.
name|setRegionDestination
argument_list|(
name|this
argument_list|)
expr_stmt|;
block|}
return|return
name|msg
return|;
block|}
specifier|public
name|String
name|toString
parameter_list|()
block|{
return|return
literal|"Queue: destination="
operator|+
name|destination
operator|.
name|getPhysicalName
argument_list|()
operator|+
literal|", subscriptions="
operator|+
name|consumers
operator|.
name|size
argument_list|()
operator|+
literal|", memory="
operator|+
name|usageManager
operator|.
name|getPercentUsage
argument_list|()
operator|+
literal|"%, size="
operator|+
name|messages
operator|.
name|size
argument_list|()
operator|+
literal|", in flight groups="
operator|+
name|messageGroupOwners
return|;
block|}
specifier|public
name|void
name|start
parameter_list|()
throws|throws
name|Exception
block|{     }
specifier|public
name|void
name|stop
parameter_list|()
throws|throws
name|Exception
block|{     }
comment|// Properties
comment|// -------------------------------------------------------------------------
specifier|public
name|ActiveMQDestination
name|getActiveMQDestination
parameter_list|()
block|{
return|return
name|destination
return|;
block|}
specifier|public
name|UsageManager
name|getUsageManager
parameter_list|()
block|{
return|return
name|usageManager
return|;
block|}
specifier|public
name|DestinationStatistics
name|getDestinationStatistics
parameter_list|()
block|{
return|return
name|destinationStatistics
return|;
block|}
specifier|public
name|MessageGroupMap
name|getMessageGroupOwners
parameter_list|()
block|{
if|if
condition|(
name|messageGroupOwners
operator|==
literal|null
condition|)
block|{
name|messageGroupOwners
operator|=
operator|new
name|MessageGroupHashBucket
argument_list|(
name|messageGroupHashBucketCount
argument_list|)
expr_stmt|;
block|}
return|return
name|messageGroupOwners
return|;
block|}
specifier|public
name|DispatchPolicy
name|getDispatchPolicy
parameter_list|()
block|{
return|return
name|dispatchPolicy
return|;
block|}
specifier|public
name|void
name|setDispatchPolicy
parameter_list|(
name|DispatchPolicy
name|dispatchPolicy
parameter_list|)
block|{
name|this
operator|.
name|dispatchPolicy
operator|=
name|dispatchPolicy
expr_stmt|;
block|}
specifier|public
name|DeadLetterStrategy
name|getDeadLetterStrategy
parameter_list|()
block|{
return|return
name|deadLetterStrategy
return|;
block|}
specifier|public
name|void
name|setDeadLetterStrategy
parameter_list|(
name|DeadLetterStrategy
name|deadLetterStrategy
parameter_list|)
block|{
name|this
operator|.
name|deadLetterStrategy
operator|=
name|deadLetterStrategy
expr_stmt|;
block|}
specifier|public
name|int
name|getMessageGroupHashBucketCount
parameter_list|()
block|{
return|return
name|messageGroupHashBucketCount
return|;
block|}
specifier|public
name|void
name|setMessageGroupHashBucketCount
parameter_list|(
name|int
name|messageGroupHashBucketCount
parameter_list|)
block|{
name|this
operator|.
name|messageGroupHashBucketCount
operator|=
name|messageGroupHashBucketCount
expr_stmt|;
block|}
comment|// Implementation methods
comment|// -------------------------------------------------------------------------
specifier|private
name|MessageReference
name|createMessageReference
parameter_list|(
name|Message
name|message
parameter_list|)
block|{
return|return
operator|new
name|IndirectMessageReference
argument_list|(
name|this
argument_list|,
name|message
argument_list|)
return|;
block|}
specifier|private
name|void
name|dispatch
parameter_list|(
name|ConnectionContext
name|context
parameter_list|,
name|MessageReference
name|node
parameter_list|,
name|Message
name|message
parameter_list|)
throws|throws
name|Exception
block|{
name|dispatchValve
operator|.
name|increment
argument_list|()
expr_stmt|;
name|MessageEvaluationContext
name|msgContext
init|=
name|context
operator|.
name|getMessageEvaluationContext
argument_list|()
decl_stmt|;
try|try
block|{
name|destinationStatistics
operator|.
name|onMessageEnqueue
argument_list|(
name|message
argument_list|)
expr_stmt|;
synchronized|synchronized
init|(
name|messages
init|)
block|{
name|messages
operator|.
name|add
argument_list|(
name|node
argument_list|)
expr_stmt|;
block|}
synchronized|synchronized
init|(
name|consumers
init|)
block|{
if|if
condition|(
name|consumers
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
name|log
operator|.
name|debug
argument_list|(
literal|"No subscriptions registered, will not dispatch message at this time."
argument_list|)
expr_stmt|;
return|return;
block|}
block|}
name|msgContext
operator|.
name|setDestination
argument_list|(
name|destination
argument_list|)
expr_stmt|;
name|msgContext
operator|.
name|setMessageReference
argument_list|(
name|node
argument_list|)
expr_stmt|;
name|dispatchPolicy
operator|.
name|dispatch
argument_list|(
name|context
argument_list|,
name|node
argument_list|,
name|msgContext
argument_list|,
name|consumers
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|msgContext
operator|.
name|clear
argument_list|()
expr_stmt|;
name|dispatchValve
operator|.
name|decrement
argument_list|()
expr_stmt|;
block|}
block|}
specifier|private
name|int
name|calcHighestSubscriptionPriority
parameter_list|()
block|{
name|int
name|rc
init|=
name|Integer
operator|.
name|MIN_VALUE
decl_stmt|;
synchronized|synchronized
init|(
name|consumers
init|)
block|{
for|for
control|(
name|Iterator
name|iter
init|=
name|consumers
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
name|Subscription
name|sub
init|=
operator|(
name|Subscription
operator|)
name|iter
operator|.
name|next
argument_list|()
decl_stmt|;
if|if
condition|(
name|sub
operator|.
name|getConsumerInfo
argument_list|()
operator|.
name|getPriority
argument_list|()
operator|>
name|rc
condition|)
block|{
name|rc
operator|=
name|sub
operator|.
name|getConsumerInfo
argument_list|()
operator|.
name|getPriority
argument_list|()
expr_stmt|;
block|}
block|}
block|}
return|return
name|rc
return|;
block|}
specifier|public
name|MessageStore
name|getMessageStore
parameter_list|()
block|{
return|return
name|store
return|;
block|}
specifier|public
name|Message
index|[]
name|browse
parameter_list|()
block|{
name|ArrayList
name|l
init|=
operator|new
name|ArrayList
argument_list|()
decl_stmt|;
synchronized|synchronized
init|(
name|messages
init|)
block|{
for|for
control|(
name|Iterator
name|iter
init|=
name|messages
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
try|try
block|{
name|MessageReference
name|r
init|=
operator|(
name|MessageReference
operator|)
name|iter
operator|.
name|next
argument_list|()
decl_stmt|;
name|r
operator|.
name|incrementReferenceCount
argument_list|()
expr_stmt|;
try|try
block|{
name|Message
name|m
init|=
name|r
operator|.
name|getMessage
argument_list|()
decl_stmt|;
if|if
condition|(
name|m
operator|!=
literal|null
condition|)
block|{
name|l
operator|.
name|add
argument_list|(
name|m
argument_list|)
expr_stmt|;
block|}
block|}
finally|finally
block|{
name|r
operator|.
name|decrementReferenceCount
argument_list|()
expr_stmt|;
block|}
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{                 }
block|}
block|}
return|return
operator|(
name|Message
index|[]
operator|)
name|l
operator|.
name|toArray
argument_list|(
operator|new
name|Message
index|[
name|l
operator|.
name|size
argument_list|()
index|]
argument_list|)
return|;
block|}
specifier|public
name|boolean
name|removeMessage
parameter_list|(
name|String
name|messageId
parameter_list|)
block|{
synchronized|synchronized
init|(
name|messages
init|)
block|{
name|ConnectionContext
name|c
init|=
operator|new
name|ConnectionContext
argument_list|()
decl_stmt|;
for|for
control|(
name|Iterator
name|iter
init|=
name|messages
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
try|try
block|{
name|IndirectMessageReference
name|r
init|=
operator|(
name|IndirectMessageReference
operator|)
name|iter
operator|.
name|next
argument_list|()
decl_stmt|;
if|if
condition|(
name|messageId
operator|.
name|equals
argument_list|(
name|r
operator|.
name|getMessageId
argument_list|()
operator|.
name|toString
argument_list|()
argument_list|)
condition|)
block|{
comment|// We should only delete messages that can be locked.
if|if
condition|(
name|r
operator|.
name|lock
argument_list|(
name|LockOwner
operator|.
name|HIGH_PRIORITY_LOCK_OWNER
argument_list|)
condition|)
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
name|setAckType
argument_list|(
name|MessageAck
operator|.
name|STANDARD_ACK_TYPE
argument_list|)
expr_stmt|;
name|ack
operator|.
name|setDestination
argument_list|(
name|destination
argument_list|)
expr_stmt|;
name|ack
operator|.
name|setMessageID
argument_list|(
name|r
operator|.
name|getMessageId
argument_list|()
argument_list|)
expr_stmt|;
name|acknowledge
argument_list|(
name|c
argument_list|,
literal|null
argument_list|,
name|ack
argument_list|,
name|r
argument_list|)
expr_stmt|;
name|r
operator|.
name|drop
argument_list|()
expr_stmt|;
name|dropEvent
argument_list|()
expr_stmt|;
name|iter
operator|.
name|remove
argument_list|()
expr_stmt|;
return|return
literal|true
return|;
block|}
block|}
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{                 }
block|}
block|}
return|return
literal|false
return|;
block|}
specifier|public
name|Message
name|getMessage
parameter_list|(
name|String
name|messageId
parameter_list|)
block|{
synchronized|synchronized
init|(
name|messages
init|)
block|{
for|for
control|(
name|Iterator
name|iter
init|=
name|messages
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
try|try
block|{
name|MessageReference
name|r
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
name|messageId
operator|.
name|equals
argument_list|(
name|r
operator|.
name|getMessageId
argument_list|()
operator|.
name|toString
argument_list|()
argument_list|)
condition|)
block|{
name|r
operator|.
name|incrementReferenceCount
argument_list|()
expr_stmt|;
try|try
block|{
name|Message
name|m
init|=
name|r
operator|.
name|getMessage
argument_list|()
decl_stmt|;
if|if
condition|(
name|m
operator|!=
literal|null
condition|)
block|{
return|return
name|m
return|;
block|}
block|}
finally|finally
block|{
name|r
operator|.
name|decrementReferenceCount
argument_list|()
expr_stmt|;
block|}
break|break;
block|}
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{                 }
block|}
block|}
return|return
literal|null
return|;
block|}
specifier|public
name|void
name|purge
parameter_list|()
block|{
synchronized|synchronized
init|(
name|messages
init|)
block|{
name|ConnectionContext
name|c
init|=
operator|new
name|ConnectionContext
argument_list|()
decl_stmt|;
for|for
control|(
name|Iterator
name|iter
init|=
name|messages
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
try|try
block|{
name|IndirectMessageReference
name|r
init|=
operator|(
name|IndirectMessageReference
operator|)
name|iter
operator|.
name|next
argument_list|()
decl_stmt|;
comment|// We should only delete messages that can be locked.
if|if
condition|(
name|r
operator|.
name|lock
argument_list|(
name|LockOwner
operator|.
name|HIGH_PRIORITY_LOCK_OWNER
argument_list|)
condition|)
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
name|setAckType
argument_list|(
name|MessageAck
operator|.
name|STANDARD_ACK_TYPE
argument_list|)
expr_stmt|;
name|ack
operator|.
name|setDestination
argument_list|(
name|destination
argument_list|)
expr_stmt|;
name|ack
operator|.
name|setMessageID
argument_list|(
name|r
operator|.
name|getMessageId
argument_list|()
argument_list|)
expr_stmt|;
name|acknowledge
argument_list|(
name|c
argument_list|,
literal|null
argument_list|,
name|ack
argument_list|,
name|r
argument_list|)
expr_stmt|;
name|r
operator|.
name|drop
argument_list|()
expr_stmt|;
name|dropEvent
argument_list|()
expr_stmt|;
name|iter
operator|.
name|remove
argument_list|()
expr_stmt|;
block|}
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{                 }
block|}
block|}
block|}
specifier|public
name|boolean
name|copyMessageTo
parameter_list|(
name|ConnectionContext
name|context
parameter_list|,
name|String
name|messageId
parameter_list|,
name|ActiveMQDestination
name|dest
parameter_list|)
throws|throws
name|Exception
block|{
synchronized|synchronized
init|(
name|messages
init|)
block|{
for|for
control|(
name|Iterator
name|iter
init|=
name|messages
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
try|try
block|{
name|MessageReference
name|r
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
name|messageId
operator|.
name|equals
argument_list|(
name|r
operator|.
name|getMessageId
argument_list|()
operator|.
name|toString
argument_list|()
argument_list|)
condition|)
block|{
name|r
operator|.
name|incrementReferenceCount
argument_list|()
expr_stmt|;
try|try
block|{
name|Message
name|m
init|=
name|r
operator|.
name|getMessage
argument_list|()
decl_stmt|;
name|BrokerSupport
operator|.
name|resend
argument_list|(
name|context
argument_list|,
name|m
argument_list|,
name|dest
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|r
operator|.
name|decrementReferenceCount
argument_list|()
expr_stmt|;
block|}
return|return
literal|true
return|;
block|}
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{                 }
block|}
block|}
return|return
literal|false
return|;
block|}
block|}
end_class

end_unit

