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
name|ArrayList
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Collections
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
name|java
operator|.
name|util
operator|.
name|concurrent
operator|.
name|ConcurrentHashMap
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
name|AtomicBoolean
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
name|AbstractPendingMessageCursor
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
name|StoreDurableSubscriberCursor
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
name|PolicyEntry
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
name|store
operator|.
name|TopicMessageStore
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
name|usage
operator|.
name|SystemUsage
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
name|usage
operator|.
name|Usage
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
name|usage
operator|.
name|UsageListener
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
name|SubscriptionKey
import|;
end_import

begin_import
import|import
name|org
operator|.
name|slf4j
operator|.
name|Logger
import|;
end_import

begin_import
import|import
name|org
operator|.
name|slf4j
operator|.
name|LoggerFactory
import|;
end_import

begin_class
specifier|public
class|class
name|DurableTopicSubscription
extends|extends
name|PrefetchSubscription
implements|implements
name|UsageListener
block|{
specifier|private
specifier|static
specifier|final
name|Logger
name|LOG
init|=
name|LoggerFactory
operator|.
name|getLogger
argument_list|(
name|DurableTopicSubscription
operator|.
name|class
argument_list|)
decl_stmt|;
specifier|private
specifier|final
name|ConcurrentHashMap
argument_list|<
name|MessageId
argument_list|,
name|Integer
argument_list|>
name|redeliveredMessages
init|=
operator|new
name|ConcurrentHashMap
argument_list|<
name|MessageId
argument_list|,
name|Integer
argument_list|>
argument_list|()
decl_stmt|;
specifier|private
specifier|final
name|ConcurrentHashMap
argument_list|<
name|ActiveMQDestination
argument_list|,
name|Destination
argument_list|>
name|durableDestinations
init|=
operator|new
name|ConcurrentHashMap
argument_list|<
name|ActiveMQDestination
argument_list|,
name|Destination
argument_list|>
argument_list|()
decl_stmt|;
specifier|private
specifier|final
name|SubscriptionKey
name|subscriptionKey
decl_stmt|;
specifier|private
specifier|final
name|boolean
name|keepDurableSubsActive
decl_stmt|;
specifier|private
specifier|final
name|AtomicBoolean
name|active
init|=
operator|new
name|AtomicBoolean
argument_list|()
decl_stmt|;
specifier|private
specifier|final
name|AtomicLong
name|offlineTimestamp
init|=
operator|new
name|AtomicLong
argument_list|(
operator|-
literal|1
argument_list|)
decl_stmt|;
specifier|public
name|DurableTopicSubscription
parameter_list|(
name|Broker
name|broker
parameter_list|,
name|SystemUsage
name|usageManager
parameter_list|,
name|ConnectionContext
name|context
parameter_list|,
name|ConsumerInfo
name|info
parameter_list|,
name|boolean
name|keepDurableSubsActive
parameter_list|)
throws|throws
name|JMSException
block|{
name|super
argument_list|(
name|broker
argument_list|,
name|usageManager
argument_list|,
name|context
argument_list|,
name|info
argument_list|)
expr_stmt|;
name|this
operator|.
name|pending
operator|=
operator|new
name|StoreDurableSubscriberCursor
argument_list|(
name|broker
argument_list|,
name|context
operator|.
name|getClientId
argument_list|()
argument_list|,
name|info
operator|.
name|getSubscriptionName
argument_list|()
argument_list|,
name|info
operator|.
name|getPrefetchSize
argument_list|()
argument_list|,
name|this
argument_list|)
expr_stmt|;
name|this
operator|.
name|pending
operator|.
name|setSystemUsage
argument_list|(
name|usageManager
argument_list|)
expr_stmt|;
name|this
operator|.
name|pending
operator|.
name|setMemoryUsageHighWaterMark
argument_list|(
name|getCursorMemoryHighWaterMark
argument_list|()
argument_list|)
expr_stmt|;
name|this
operator|.
name|keepDurableSubsActive
operator|=
name|keepDurableSubsActive
expr_stmt|;
name|subscriptionKey
operator|=
operator|new
name|SubscriptionKey
argument_list|(
name|context
operator|.
name|getClientId
argument_list|()
argument_list|,
name|info
operator|.
name|getSubscriptionName
argument_list|()
argument_list|)
expr_stmt|;
block|}
specifier|public
specifier|final
name|boolean
name|isActive
parameter_list|()
block|{
return|return
name|active
operator|.
name|get
argument_list|()
return|;
block|}
specifier|public
specifier|final
name|long
name|getOfflineTimestamp
parameter_list|()
block|{
return|return
name|offlineTimestamp
operator|.
name|get
argument_list|()
return|;
block|}
specifier|public
name|void
name|setOfflineTimestamp
parameter_list|(
name|long
name|timestamp
parameter_list|)
block|{
name|offlineTimestamp
operator|.
name|set
argument_list|(
name|timestamp
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|isFull
parameter_list|()
block|{
return|return
operator|!
name|active
operator|.
name|get
argument_list|()
operator|||
name|super
operator|.
name|isFull
argument_list|()
return|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|gc
parameter_list|()
block|{     }
comment|/**      * store will have a pending ack for all durables, irrespective of the      * selector so we need to ack if node is un-matched      */
annotation|@
name|Override
specifier|public
name|void
name|unmatched
parameter_list|(
name|MessageReference
name|node
parameter_list|)
throws|throws
name|IOException
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
name|UNMATCHED_ACK_TYPE
argument_list|)
expr_stmt|;
name|ack
operator|.
name|setMessageID
argument_list|(
name|node
operator|.
name|getMessageId
argument_list|()
argument_list|)
expr_stmt|;
name|Destination
name|regionDestination
init|=
operator|(
name|Destination
operator|)
name|node
operator|.
name|getRegionDestination
argument_list|()
decl_stmt|;
name|regionDestination
operator|.
name|acknowledge
argument_list|(
name|this
operator|.
name|getContext
argument_list|()
argument_list|,
name|this
argument_list|,
name|ack
argument_list|,
name|node
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|protected
name|void
name|setPendingBatchSize
parameter_list|(
name|PendingMessageCursor
name|pending
parameter_list|,
name|int
name|numberToDispatch
parameter_list|)
block|{
comment|// statically configured via maxPageSize
block|}
annotation|@
name|Override
specifier|public
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
if|if
condition|(
operator|!
name|destinations
operator|.
name|contains
argument_list|(
name|destination
argument_list|)
condition|)
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
block|}
comment|// do it just once per destination
if|if
condition|(
name|durableDestinations
operator|.
name|containsKey
argument_list|(
name|destination
operator|.
name|getActiveMQDestination
argument_list|()
argument_list|)
condition|)
block|{
return|return;
block|}
name|durableDestinations
operator|.
name|put
argument_list|(
name|destination
operator|.
name|getActiveMQDestination
argument_list|()
argument_list|,
name|destination
argument_list|)
expr_stmt|;
if|if
condition|(
name|active
operator|.
name|get
argument_list|()
operator|||
name|keepDurableSubsActive
condition|)
block|{
name|Topic
name|topic
init|=
operator|(
name|Topic
operator|)
name|destination
decl_stmt|;
name|topic
operator|.
name|activate
argument_list|(
name|context
argument_list|,
name|this
argument_list|)
expr_stmt|;
name|this
operator|.
name|enqueueCounter
operator|+=
name|pending
operator|.
name|size
argument_list|()
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|destination
operator|.
name|getMessageStore
argument_list|()
operator|!=
literal|null
condition|)
block|{
name|TopicMessageStore
name|store
init|=
operator|(
name|TopicMessageStore
operator|)
name|destination
operator|.
name|getMessageStore
argument_list|()
decl_stmt|;
try|try
block|{
name|this
operator|.
name|enqueueCounter
operator|+=
name|store
operator|.
name|getMessageCount
argument_list|(
name|subscriptionKey
operator|.
name|getClientId
argument_list|()
argument_list|,
name|subscriptionKey
operator|.
name|getSubscriptionName
argument_list|()
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
name|JMSException
name|jmsEx
init|=
operator|new
name|JMSException
argument_list|(
literal|"Failed to retrieve enqueueCount from store "
operator|+
name|e
argument_list|)
decl_stmt|;
name|jmsEx
operator|.
name|setLinkedException
argument_list|(
name|e
argument_list|)
expr_stmt|;
throw|throw
name|jmsEx
throw|;
block|}
block|}
name|dispatchPending
argument_list|()
expr_stmt|;
block|}
comment|// used by RetaineMessageSubscriptionRecoveryPolicy
specifier|public
name|boolean
name|isEmpty
parameter_list|(
name|Topic
name|topic
parameter_list|)
block|{
return|return
name|pending
operator|.
name|isEmpty
argument_list|(
name|topic
argument_list|)
return|;
block|}
specifier|public
name|void
name|activate
parameter_list|(
name|SystemUsage
name|memoryManager
parameter_list|,
name|ConnectionContext
name|context
parameter_list|,
name|ConsumerInfo
name|info
parameter_list|,
name|RegionBroker
name|regionBroker
parameter_list|)
throws|throws
name|Exception
block|{
if|if
condition|(
operator|!
name|active
operator|.
name|get
argument_list|()
condition|)
block|{
name|this
operator|.
name|context
operator|=
name|context
expr_stmt|;
name|this
operator|.
name|info
operator|=
name|info
expr_stmt|;
name|LOG
operator|.
name|debug
argument_list|(
literal|"Activating {}"
argument_list|,
name|this
argument_list|)
expr_stmt|;
if|if
condition|(
operator|!
name|keepDurableSubsActive
condition|)
block|{
for|for
control|(
name|Destination
name|destination
range|:
name|durableDestinations
operator|.
name|values
argument_list|()
control|)
block|{
name|Topic
name|topic
init|=
operator|(
name|Topic
operator|)
name|destination
decl_stmt|;
name|add
argument_list|(
name|context
argument_list|,
name|topic
argument_list|)
expr_stmt|;
name|topic
operator|.
name|activate
argument_list|(
name|context
argument_list|,
name|this
argument_list|)
expr_stmt|;
block|}
comment|// On Activation we should update the configuration based on our new consumer info.
name|ActiveMQDestination
name|dest
init|=
name|this
operator|.
name|info
operator|.
name|getDestination
argument_list|()
decl_stmt|;
if|if
condition|(
name|dest
operator|!=
literal|null
operator|&&
name|regionBroker
operator|.
name|getDestinationPolicy
argument_list|()
operator|!=
literal|null
condition|)
block|{
name|PolicyEntry
name|entry
init|=
name|regionBroker
operator|.
name|getDestinationPolicy
argument_list|()
operator|.
name|getEntryFor
argument_list|(
name|dest
argument_list|)
decl_stmt|;
if|if
condition|(
name|entry
operator|!=
literal|null
condition|)
block|{
name|entry
operator|.
name|configure
argument_list|(
name|broker
argument_list|,
name|usageManager
argument_list|,
name|this
argument_list|)
expr_stmt|;
block|}
block|}
block|}
synchronized|synchronized
init|(
name|pendingLock
init|)
block|{
if|if
condition|(
operator|!
operator|(
operator|(
name|AbstractPendingMessageCursor
operator|)
name|pending
operator|)
operator|.
name|isStarted
argument_list|()
operator|||
operator|!
name|keepDurableSubsActive
condition|)
block|{
name|pending
operator|.
name|setSystemUsage
argument_list|(
name|memoryManager
argument_list|)
expr_stmt|;
name|pending
operator|.
name|setMemoryUsageHighWaterMark
argument_list|(
name|getCursorMemoryHighWaterMark
argument_list|()
argument_list|)
expr_stmt|;
name|pending
operator|.
name|setMaxAuditDepth
argument_list|(
name|getMaxAuditDepth
argument_list|()
argument_list|)
expr_stmt|;
name|pending
operator|.
name|setMaxProducersToAudit
argument_list|(
name|getMaxProducersToAudit
argument_list|()
argument_list|)
expr_stmt|;
name|pending
operator|.
name|start
argument_list|()
expr_stmt|;
block|}
comment|// use recovery policy every time sub is activated for retroactive topics and consumers
for|for
control|(
name|Destination
name|destination
range|:
name|durableDestinations
operator|.
name|values
argument_list|()
control|)
block|{
name|Topic
name|topic
init|=
operator|(
name|Topic
operator|)
name|destination
decl_stmt|;
if|if
condition|(
name|topic
operator|.
name|isAlwaysRetroactive
argument_list|()
operator|||
name|info
operator|.
name|isRetroactive
argument_list|()
condition|)
block|{
name|topic
operator|.
name|recoverRetroactiveMessages
argument_list|(
name|context
argument_list|,
name|this
argument_list|)
expr_stmt|;
block|}
block|}
block|}
name|this
operator|.
name|active
operator|.
name|set
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|this
operator|.
name|offlineTimestamp
operator|.
name|set
argument_list|(
operator|-
literal|1
argument_list|)
expr_stmt|;
name|dispatchPending
argument_list|()
expr_stmt|;
name|this
operator|.
name|usageManager
operator|.
name|getMemoryUsage
argument_list|()
operator|.
name|addUsageListener
argument_list|(
name|this
argument_list|)
expr_stmt|;
block|}
block|}
specifier|public
name|void
name|deactivate
parameter_list|(
name|boolean
name|keepDurableSubsActive
parameter_list|)
throws|throws
name|Exception
block|{
name|LOG
operator|.
name|debug
argument_list|(
literal|"Deactivating keepActive={}, {}"
argument_list|,
name|keepDurableSubsActive
argument_list|,
name|this
argument_list|)
expr_stmt|;
name|active
operator|.
name|set
argument_list|(
literal|false
argument_list|)
expr_stmt|;
name|offlineTimestamp
operator|.
name|set
argument_list|(
name|System
operator|.
name|currentTimeMillis
argument_list|()
argument_list|)
expr_stmt|;
name|this
operator|.
name|usageManager
operator|.
name|getMemoryUsage
argument_list|()
operator|.
name|removeUsageListener
argument_list|(
name|this
argument_list|)
expr_stmt|;
name|ArrayList
argument_list|<
name|Topic
argument_list|>
name|topicsToDeactivate
init|=
operator|new
name|ArrayList
argument_list|<
name|Topic
argument_list|>
argument_list|()
decl_stmt|;
name|List
argument_list|<
name|MessageReference
argument_list|>
name|savedDispateched
init|=
literal|null
decl_stmt|;
synchronized|synchronized
init|(
name|pendingLock
init|)
block|{
if|if
condition|(
operator|!
name|keepDurableSubsActive
condition|)
block|{
name|pending
operator|.
name|stop
argument_list|()
expr_stmt|;
block|}
synchronized|synchronized
init|(
name|dispatchLock
init|)
block|{
for|for
control|(
name|Destination
name|destination
range|:
name|durableDestinations
operator|.
name|values
argument_list|()
control|)
block|{
name|Topic
name|topic
init|=
operator|(
name|Topic
operator|)
name|destination
decl_stmt|;
if|if
condition|(
operator|!
name|keepDurableSubsActive
condition|)
block|{
name|topicsToDeactivate
operator|.
name|add
argument_list|(
name|topic
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|topic
operator|.
name|getDestinationStatistics
argument_list|()
operator|.
name|getInflight
argument_list|()
operator|.
name|subtract
argument_list|(
name|dispatched
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
comment|// Before we add these back to pending they need to be in producer order not
comment|// dispatch order so we can add them to the front of the pending list.
name|Collections
operator|.
name|reverse
argument_list|(
name|dispatched
argument_list|)
expr_stmt|;
for|for
control|(
specifier|final
name|MessageReference
name|node
range|:
name|dispatched
control|)
block|{
comment|// Mark the dispatched messages as redelivered for next time.
name|Integer
name|count
init|=
name|redeliveredMessages
operator|.
name|get
argument_list|(
name|node
operator|.
name|getMessageId
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
name|count
operator|!=
literal|null
condition|)
block|{
name|redeliveredMessages
operator|.
name|put
argument_list|(
name|node
operator|.
name|getMessageId
argument_list|()
argument_list|,
name|Integer
operator|.
name|valueOf
argument_list|(
name|count
operator|.
name|intValue
argument_list|()
operator|+
literal|1
argument_list|)
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|redeliveredMessages
operator|.
name|put
argument_list|(
name|node
operator|.
name|getMessageId
argument_list|()
argument_list|,
name|Integer
operator|.
name|valueOf
argument_list|(
literal|1
argument_list|)
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|keepDurableSubsActive
operator|&&
name|pending
operator|.
name|isTransient
argument_list|()
condition|)
block|{
name|pending
operator|.
name|addMessageFirst
argument_list|(
name|node
argument_list|)
expr_stmt|;
name|pending
operator|.
name|rollback
argument_list|(
name|node
operator|.
name|getMessageId
argument_list|()
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|node
operator|.
name|decrementReferenceCount
argument_list|()
expr_stmt|;
block|}
block|}
if|if
condition|(
operator|!
name|topicsToDeactivate
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
name|savedDispateched
operator|=
operator|new
name|ArrayList
argument_list|<
name|MessageReference
argument_list|>
argument_list|(
name|dispatched
argument_list|)
expr_stmt|;
block|}
name|dispatched
operator|.
name|clear
argument_list|()
expr_stmt|;
block|}
if|if
condition|(
operator|!
name|keepDurableSubsActive
operator|&&
name|pending
operator|.
name|isTransient
argument_list|()
condition|)
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
name|node
operator|.
name|decrementReferenceCount
argument_list|()
expr_stmt|;
name|pending
operator|.
name|remove
argument_list|()
expr_stmt|;
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
for|for
control|(
name|Topic
name|topic
range|:
name|topicsToDeactivate
control|)
block|{
name|topic
operator|.
name|deactivate
argument_list|(
name|context
argument_list|,
name|this
argument_list|,
name|savedDispateched
argument_list|)
expr_stmt|;
block|}
name|prefetchExtension
operator|.
name|set
argument_list|(
literal|0
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
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
name|super
operator|.
name|createMessageDispatch
argument_list|(
name|node
argument_list|,
name|message
argument_list|)
decl_stmt|;
if|if
condition|(
name|node
operator|!=
name|QueueMessageReference
operator|.
name|NULL_MESSAGE
condition|)
block|{
name|Integer
name|count
init|=
name|redeliveredMessages
operator|.
name|get
argument_list|(
name|node
operator|.
name|getMessageId
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
name|count
operator|!=
literal|null
condition|)
block|{
name|md
operator|.
name|setRedeliveryCounter
argument_list|(
name|count
operator|.
name|intValue
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
return|return
name|md
return|;
block|}
annotation|@
name|Override
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
if|if
condition|(
operator|!
name|active
operator|.
name|get
argument_list|()
operator|&&
operator|!
name|keepDurableSubsActive
condition|)
block|{
return|return;
block|}
name|super
operator|.
name|add
argument_list|(
name|node
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|dispatchPending
parameter_list|()
throws|throws
name|IOException
block|{
if|if
condition|(
name|isActive
argument_list|()
condition|)
block|{
name|super
operator|.
name|dispatchPending
argument_list|()
expr_stmt|;
block|}
block|}
specifier|public
name|void
name|removePending
parameter_list|(
name|MessageReference
name|node
parameter_list|)
throws|throws
name|IOException
block|{
name|pending
operator|.
name|remove
argument_list|(
name|node
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|protected
name|void
name|doAddRecoveredMessage
parameter_list|(
name|MessageReference
name|message
parameter_list|)
throws|throws
name|Exception
block|{
synchronized|synchronized
init|(
name|pending
init|)
block|{
name|pending
operator|.
name|addRecoveredMessage
argument_list|(
name|message
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Override
specifier|public
name|int
name|getPendingQueueSize
parameter_list|()
block|{
if|if
condition|(
name|active
operator|.
name|get
argument_list|()
operator|||
name|keepDurableSubsActive
condition|)
block|{
return|return
name|super
operator|.
name|getPendingQueueSize
argument_list|()
return|;
block|}
comment|// TODO: need to get from store
return|return
literal|0
return|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|setSelector
parameter_list|(
name|String
name|selector
parameter_list|)
throws|throws
name|InvalidSelectorException
block|{
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|(
literal|"You cannot dynamically change the selector for durable topic subscriptions"
argument_list|)
throw|;
block|}
annotation|@
name|Override
specifier|protected
name|boolean
name|canDispatch
parameter_list|(
name|MessageReference
name|node
parameter_list|)
block|{
return|return
literal|true
return|;
comment|// let them go, our dispatchPending gates the active / inactive state.
block|}
annotation|@
name|Override
specifier|protected
name|void
name|acknowledge
parameter_list|(
name|ConnectionContext
name|context
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
name|this
operator|.
name|setTimeOfLastMessageAck
argument_list|(
name|System
operator|.
name|currentTimeMillis
argument_list|()
argument_list|)
expr_stmt|;
name|Destination
name|regionDestination
init|=
operator|(
name|Destination
operator|)
name|node
operator|.
name|getRegionDestination
argument_list|()
decl_stmt|;
name|regionDestination
operator|.
name|acknowledge
argument_list|(
name|context
argument_list|,
name|this
argument_list|,
name|ack
argument_list|,
name|node
argument_list|)
expr_stmt|;
name|redeliveredMessages
operator|.
name|remove
argument_list|(
name|node
operator|.
name|getMessageId
argument_list|()
argument_list|)
expr_stmt|;
name|node
operator|.
name|decrementReferenceCount
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
specifier|synchronized
name|String
name|toString
parameter_list|()
block|{
return|return
literal|"DurableTopicSubscription-"
operator|+
name|getSubscriptionKey
argument_list|()
operator|+
literal|", id="
operator|+
name|info
operator|.
name|getConsumerId
argument_list|()
operator|+
literal|", active="
operator|+
name|isActive
argument_list|()
operator|+
literal|", destinations="
operator|+
name|durableDestinations
operator|.
name|size
argument_list|()
operator|+
literal|", total="
operator|+
name|enqueueCounter
operator|+
literal|", pending="
operator|+
name|getPendingQueueSize
argument_list|()
operator|+
literal|", dispatched="
operator|+
name|dispatchCounter
operator|+
literal|", inflight="
operator|+
name|dispatched
operator|.
name|size
argument_list|()
operator|+
literal|", prefetchExtension="
operator|+
name|getPrefetchExtension
argument_list|()
return|;
block|}
specifier|public
name|SubscriptionKey
name|getSubscriptionKey
parameter_list|()
block|{
return|return
name|subscriptionKey
return|;
block|}
comment|/**      * Release any references that we are holding.      */
annotation|@
name|Override
specifier|public
name|void
name|destroy
parameter_list|()
block|{
synchronized|synchronized
init|(
name|pendingLock
init|)
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
name|node
operator|.
name|decrementReferenceCount
argument_list|()
expr_stmt|;
block|}
block|}
finally|finally
block|{
name|pending
operator|.
name|release
argument_list|()
expr_stmt|;
name|pending
operator|.
name|clear
argument_list|()
expr_stmt|;
block|}
block|}
synchronized|synchronized
init|(
name|dispatchLock
init|)
block|{
for|for
control|(
name|MessageReference
name|node
range|:
name|dispatched
control|)
block|{
name|node
operator|.
name|decrementReferenceCount
argument_list|()
expr_stmt|;
block|}
name|dispatched
operator|.
name|clear
argument_list|()
expr_stmt|;
block|}
name|setSlowConsumer
argument_list|(
literal|false
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|onUsageChanged
parameter_list|(
name|Usage
name|usage
parameter_list|,
name|int
name|oldPercentUsage
parameter_list|,
name|int
name|newPercentUsage
parameter_list|)
block|{
if|if
condition|(
name|oldPercentUsage
operator|>
name|newPercentUsage
operator|&&
name|oldPercentUsage
operator|>=
literal|90
condition|)
block|{
try|try
block|{
name|dispatchPending
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
name|LOG
operator|.
name|warn
argument_list|(
literal|"problem calling dispatchMatched"
argument_list|,
name|e
argument_list|)
expr_stmt|;
block|}
block|}
block|}
annotation|@
name|Override
specifier|protected
name|boolean
name|isDropped
parameter_list|(
name|MessageReference
name|node
parameter_list|)
block|{
return|return
literal|false
return|;
block|}
specifier|public
name|boolean
name|isKeepDurableSubsActive
parameter_list|()
block|{
return|return
name|keepDurableSubsActive
return|;
block|}
block|}
end_class

end_unit

