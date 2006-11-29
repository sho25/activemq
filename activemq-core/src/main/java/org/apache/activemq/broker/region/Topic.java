begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  *  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  * http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
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
name|Set
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
name|advisory
operator|.
name|AdvisorySupport
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
name|FixedCountSubscriptionRecoveryPolicy
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
name|broker
operator|.
name|region
operator|.
name|policy
operator|.
name|SimpleDispatchPolicy
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
name|SubscriptionRecoveryPolicy
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
name|ActiveMQTopic
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
name|command
operator|.
name|SubscriptionInfo
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
name|SubscriptionKey
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
name|CopyOnWriteArrayList
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
name|CopyOnWriteArraySet
import|;
end_import

begin_comment
comment|/**  * The Topic is a destination that sends a copy of a message to every active  * Subscription registered.  *   * @version $Revision: 1.21 $  */
end_comment

begin_class
specifier|public
class|class
name|Topic
implements|implements
name|Destination
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
name|Topic
operator|.
name|class
argument_list|)
decl_stmt|;
specifier|protected
specifier|final
name|ActiveMQDestination
name|destination
decl_stmt|;
specifier|protected
specifier|final
name|CopyOnWriteArrayList
name|consumers
init|=
operator|new
name|CopyOnWriteArrayList
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
name|TopicMessageStore
name|store
decl_stmt|;
comment|//this could be NULL! (If an advsiory)
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
name|DispatchPolicy
name|dispatchPolicy
init|=
operator|new
name|SimpleDispatchPolicy
argument_list|()
decl_stmt|;
specifier|private
name|SubscriptionRecoveryPolicy
name|subscriptionRecoveryPolicy
init|=
operator|new
name|FixedCountSubscriptionRecoveryPolicy
argument_list|()
decl_stmt|;
specifier|private
name|boolean
name|sendAdvisoryIfNoConsumers
decl_stmt|;
specifier|private
name|DeadLetterStrategy
name|deadLetterStrategy
init|=
operator|new
name|SharedDeadLetterStrategy
argument_list|()
decl_stmt|;
specifier|private
specifier|final
name|ConcurrentHashMap
name|durableSubcribers
init|=
operator|new
name|ConcurrentHashMap
argument_list|()
decl_stmt|;
specifier|public
name|Topic
parameter_list|(
name|ActiveMQDestination
name|destination
parameter_list|,
name|TopicMessageStore
name|store
parameter_list|,
name|UsageManager
name|memoryManager
parameter_list|,
name|DestinationStatistics
name|parentStats
parameter_list|,
name|TaskRunnerFactory
name|taskFactory
parameter_list|)
block|{
name|this
operator|.
name|destination
operator|=
name|destination
expr_stmt|;
name|this
operator|.
name|store
operator|=
name|store
expr_stmt|;
comment|//this could be NULL! (If an advsiory)
name|this
operator|.
name|usageManager
operator|=
operator|new
name|UsageManager
argument_list|(
name|memoryManager
argument_list|)
expr_stmt|;
name|this
operator|.
name|usageManager
operator|.
name|setLimit
argument_list|(
name|Long
operator|.
name|MAX_VALUE
argument_list|)
expr_stmt|;
comment|// Let the store know what usage manager we are using so that he can flush messages to disk
comment|// when usage gets high.
if|if
condition|(
name|store
operator|!=
literal|null
condition|)
block|{
name|store
operator|.
name|setUsageManager
argument_list|(
name|usageManager
argument_list|)
expr_stmt|;
block|}
name|this
operator|.
name|destinationStatistics
operator|.
name|setParent
argument_list|(
name|parentStats
argument_list|)
expr_stmt|;
block|}
specifier|public
name|boolean
name|lock
parameter_list|(
name|MessageReference
name|node
parameter_list|,
name|LockOwner
name|sub
parameter_list|)
block|{
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
specifier|final
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
if|if
condition|(
operator|!
name|sub
operator|.
name|getConsumerInfo
argument_list|()
operator|.
name|isDurable
argument_list|()
condition|)
block|{
comment|// Do a retroactive recovery if needed.
if|if
condition|(
name|sub
operator|.
name|getConsumerInfo
argument_list|()
operator|.
name|isRetroactive
argument_list|()
condition|)
block|{
comment|// synchronize with dispatch method so that no new messages are sent
comment|// while we are recovering a subscription to avoid out of order messages.
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
name|add
argument_list|(
name|sub
argument_list|)
expr_stmt|;
block|}
name|subscriptionRecoveryPolicy
operator|.
name|recover
argument_list|(
name|context
argument_list|,
name|this
argument_list|,
name|sub
argument_list|)
expr_stmt|;
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
else|else
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
block|}
block|}
else|else
block|{
name|DurableTopicSubscription
name|dsub
init|=
operator|(
name|DurableTopicSubscription
operator|)
name|sub
decl_stmt|;
name|durableSubcribers
operator|.
name|put
argument_list|(
name|dsub
operator|.
name|getSubscriptionKey
argument_list|()
argument_list|,
name|dsub
argument_list|)
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
if|if
condition|(
operator|!
name|sub
operator|.
name|getConsumerInfo
argument_list|()
operator|.
name|isDurable
argument_list|()
condition|)
block|{
name|destinationStatistics
operator|.
name|getConsumers
argument_list|()
operator|.
name|decrement
argument_list|()
expr_stmt|;
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
block|}
specifier|public
name|void
name|deleteSubscription
parameter_list|(
name|ConnectionContext
name|context
parameter_list|,
name|SubscriptionKey
name|key
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
name|deleteSubscription
argument_list|(
name|key
operator|.
name|clientId
argument_list|,
name|key
operator|.
name|subscriptionName
argument_list|)
expr_stmt|;
name|Object
name|removed
init|=
name|durableSubcribers
operator|.
name|remove
argument_list|(
name|key
argument_list|)
decl_stmt|;
if|if
condition|(
name|removed
operator|!=
literal|null
condition|)
block|{
name|destinationStatistics
operator|.
name|getConsumers
argument_list|()
operator|.
name|decrement
argument_list|()
expr_stmt|;
block|}
block|}
block|}
specifier|public
name|void
name|activate
parameter_list|(
name|ConnectionContext
name|context
parameter_list|,
specifier|final
name|DurableTopicSubscription
name|subscription
parameter_list|)
throws|throws
name|Exception
block|{
comment|// synchronize with dispatch method so that no new messages are sent
comment|// while
comment|// we are recovering a subscription to avoid out of order messages.
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
name|add
argument_list|(
name|subscription
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|store
operator|==
literal|null
condition|)
return|return;
comment|// Recover the durable subscription.
name|String
name|clientId
init|=
name|subscription
operator|.
name|getClientId
argument_list|()
decl_stmt|;
name|String
name|subscriptionName
init|=
name|subscription
operator|.
name|getSubscriptionName
argument_list|()
decl_stmt|;
name|String
name|selector
init|=
name|subscription
operator|.
name|getConsumerInfo
argument_list|()
operator|.
name|getSelector
argument_list|()
decl_stmt|;
name|SubscriptionInfo
name|info
init|=
name|store
operator|.
name|lookupSubscription
argument_list|(
name|clientId
argument_list|,
name|subscriptionName
argument_list|)
decl_stmt|;
if|if
condition|(
name|info
operator|!=
literal|null
condition|)
block|{
comment|// Check to see if selector changed.
name|String
name|s1
init|=
name|info
operator|.
name|getSelector
argument_list|()
decl_stmt|;
if|if
condition|(
name|s1
operator|==
literal|null
operator|^
name|selector
operator|==
literal|null
operator|||
operator|(
name|s1
operator|!=
literal|null
operator|&&
operator|!
name|s1
operator|.
name|equals
argument_list|(
name|selector
argument_list|)
operator|)
condition|)
block|{
comment|// Need to delete the subscription
name|store
operator|.
name|deleteSubscription
argument_list|(
name|clientId
argument_list|,
name|subscriptionName
argument_list|)
expr_stmt|;
name|info
operator|=
literal|null
expr_stmt|;
block|}
block|}
comment|// Do we need to create the subscription?
if|if
condition|(
name|info
operator|==
literal|null
condition|)
block|{
name|store
operator|.
name|addSubsciption
argument_list|(
name|clientId
argument_list|,
name|subscriptionName
argument_list|,
name|selector
argument_list|,
name|subscription
operator|.
name|getConsumerInfo
argument_list|()
operator|.
name|isRetroactive
argument_list|()
argument_list|)
expr_stmt|;
block|}
specifier|final
name|MessageEvaluationContext
name|msgContext
init|=
operator|new
name|MessageEvaluationContext
argument_list|()
decl_stmt|;
name|msgContext
operator|.
name|setDestination
argument_list|(
name|destination
argument_list|)
expr_stmt|;
if|if
condition|(
name|subscription
operator|.
name|isRecoveryRequired
argument_list|()
condition|)
block|{
name|store
operator|.
name|recoverSubscription
argument_list|(
name|clientId
argument_list|,
name|subscriptionName
argument_list|,
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
throws|throws
name|Exception
block|{
name|message
operator|.
name|setRegionDestination
argument_list|(
name|Topic
operator|.
name|this
argument_list|)
expr_stmt|;
try|try
block|{
name|msgContext
operator|.
name|setMessageReference
argument_list|(
name|message
argument_list|)
expr_stmt|;
if|if
condition|(
name|subscription
operator|.
name|matches
argument_list|(
name|message
argument_list|,
name|msgContext
argument_list|)
condition|)
block|{
name|subscription
operator|.
name|add
argument_list|(
name|message
argument_list|)
expr_stmt|;
block|}
block|}
catch|catch
parameter_list|(
name|InterruptedException
name|e
parameter_list|)
block|{
name|Thread
operator|.
name|currentThread
argument_list|()
operator|.
name|interrupt
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
comment|// TODO: Need to handle this better.
name|e
operator|.
name|printStackTrace
argument_list|()
expr_stmt|;
block|}
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
block|{}
specifier|public
name|boolean
name|hasSpace
parameter_list|()
block|{
return|return
literal|true
return|;
block|}
block|}
argument_list|)
expr_stmt|;
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
name|deactivate
parameter_list|(
name|ConnectionContext
name|context
parameter_list|,
name|DurableTopicSubscription
name|sub
parameter_list|)
throws|throws
name|Exception
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
block|}
specifier|protected
name|void
name|recoverRetroactiveMessages
parameter_list|(
name|ConnectionContext
name|context
parameter_list|,
name|Subscription
name|subscription
parameter_list|)
throws|throws
name|Exception
block|{
if|if
condition|(
name|subscription
operator|.
name|getConsumerInfo
argument_list|()
operator|.
name|isRetroactive
argument_list|()
condition|)
block|{
name|subscriptionRecoveryPolicy
operator|.
name|recover
argument_list|(
name|context
argument_list|,
name|this
argument_list|,
name|subscription
argument_list|)
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
comment|// There is delay between the client sending it and it arriving at the
comment|// destination.. it may have expired.
if|if
condition|(
name|message
operator|.
name|isExpired
argument_list|()
condition|)
block|{
return|return;
block|}
if|if
condition|(
name|context
operator|.
name|isProducerFlowControl
argument_list|()
condition|)
block|{
if|if
condition|(
name|usageManager
operator|.
name|isSendFailIfNoSpace
argument_list|()
operator|&&
name|usageManager
operator|.
name|isFull
argument_list|()
condition|)
block|{
throw|throw
operator|new
name|javax
operator|.
name|jms
operator|.
name|ResourceAllocationException
argument_list|(
literal|"Usage Manager memory limit reached"
argument_list|)
throw|;
block|}
else|else
block|{
name|usageManager
operator|.
name|waitForSpace
argument_list|()
expr_stmt|;
comment|// The usage manager could have delayed us by the time
comment|// we unblock the message could have expired..
if|if
condition|(
name|message
operator|.
name|isExpired
argument_list|()
condition|)
block|{
return|return;
block|}
block|}
block|}
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
operator|&&
operator|!
name|canOptimizeOutPersistence
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
name|message
operator|.
name|incrementReferenceCount
argument_list|()
expr_stmt|;
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
comment|// It could take while before we receive the commit
comment|// operration.. by that time the message could have expired..
if|if
condition|(
name|message
operator|.
name|isExpired
argument_list|()
condition|)
block|{
comment|// TODO: remove message from store.
return|return;
block|}
name|dispatch
argument_list|(
name|context
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
name|message
argument_list|)
expr_stmt|;
block|}
block|}
finally|finally
block|{
name|message
operator|.
name|decrementReferenceCount
argument_list|()
expr_stmt|;
block|}
block|}
specifier|private
name|boolean
name|canOptimizeOutPersistence
parameter_list|()
block|{
return|return
name|durableSubcribers
operator|.
name|size
argument_list|()
operator|==
literal|0
return|;
block|}
specifier|public
name|String
name|toString
parameter_list|()
block|{
return|return
literal|"Topic: destination="
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
return|;
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
name|DurableTopicSubscription
name|dsub
init|=
operator|(
name|DurableTopicSubscription
operator|)
name|sub
decl_stmt|;
name|store
operator|.
name|acknowledge
argument_list|(
name|context
argument_list|,
name|dsub
operator|.
name|getClientId
argument_list|()
argument_list|,
name|dsub
operator|.
name|getSubscriptionName
argument_list|()
argument_list|,
name|node
operator|.
name|getMessageId
argument_list|()
argument_list|)
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
name|gc
parameter_list|()
block|{     }
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
return|return
name|store
operator|!=
literal|null
condition|?
name|store
operator|.
name|getMessage
argument_list|(
name|messageId
argument_list|)
else|:
literal|null
return|;
block|}
specifier|public
name|void
name|start
parameter_list|()
throws|throws
name|Exception
block|{
name|this
operator|.
name|subscriptionRecoveryPolicy
operator|.
name|start
argument_list|()
expr_stmt|;
block|}
specifier|public
name|void
name|stop
parameter_list|()
throws|throws
name|Exception
block|{
name|this
operator|.
name|subscriptionRecoveryPolicy
operator|.
name|stop
argument_list|()
expr_stmt|;
block|}
specifier|public
name|Message
index|[]
name|browse
parameter_list|()
block|{
specifier|final
name|Set
name|result
init|=
operator|new
name|CopyOnWriteArraySet
argument_list|()
decl_stmt|;
try|try
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
throws|throws
name|Exception
block|{
name|result
operator|.
name|add
argument_list|(
name|message
argument_list|)
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
block|{}
specifier|public
name|void
name|finished
parameter_list|()
block|{}
specifier|public
name|boolean
name|hasSpace
parameter_list|()
block|{
return|return
literal|true
return|;
block|}
block|}
argument_list|)
expr_stmt|;
name|Message
index|[]
name|msgs
init|=
name|subscriptionRecoveryPolicy
operator|.
name|browse
argument_list|(
name|getActiveMQDestination
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
name|msgs
operator|!=
literal|null
condition|)
block|{
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|msgs
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|result
operator|.
name|add
argument_list|(
name|msgs
index|[
name|i
index|]
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|}
catch|catch
parameter_list|(
name|Throwable
name|e
parameter_list|)
block|{
name|log
operator|.
name|warn
argument_list|(
literal|"Failed to browse Topic: "
operator|+
name|getActiveMQDestination
argument_list|()
operator|.
name|getPhysicalName
argument_list|()
argument_list|,
name|e
argument_list|)
expr_stmt|;
block|}
return|return
operator|(
name|Message
index|[]
operator|)
name|result
operator|.
name|toArray
argument_list|(
operator|new
name|Message
index|[
name|result
operator|.
name|size
argument_list|()
index|]
argument_list|)
return|;
block|}
comment|// Properties
comment|// -------------------------------------------------------------------------
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
name|ActiveMQDestination
name|getActiveMQDestination
parameter_list|()
block|{
return|return
name|destination
return|;
block|}
specifier|public
name|String
name|getDestination
parameter_list|()
block|{
return|return
name|destination
operator|.
name|getPhysicalName
argument_list|()
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
name|SubscriptionRecoveryPolicy
name|getSubscriptionRecoveryPolicy
parameter_list|()
block|{
return|return
name|subscriptionRecoveryPolicy
return|;
block|}
specifier|public
name|void
name|setSubscriptionRecoveryPolicy
parameter_list|(
name|SubscriptionRecoveryPolicy
name|subscriptionRecoveryPolicy
parameter_list|)
block|{
name|this
operator|.
name|subscriptionRecoveryPolicy
operator|=
name|subscriptionRecoveryPolicy
expr_stmt|;
block|}
specifier|public
name|boolean
name|isSendAdvisoryIfNoConsumers
parameter_list|()
block|{
return|return
name|sendAdvisoryIfNoConsumers
return|;
block|}
specifier|public
name|void
name|setSendAdvisoryIfNoConsumers
parameter_list|(
name|boolean
name|sendAdvisoryIfNoConsumers
parameter_list|)
block|{
name|this
operator|.
name|sendAdvisoryIfNoConsumers
operator|=
name|sendAdvisoryIfNoConsumers
expr_stmt|;
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
name|String
name|getName
parameter_list|()
block|{
return|return
name|getActiveMQDestination
argument_list|()
operator|.
name|getPhysicalName
argument_list|()
return|;
block|}
comment|// Implementation methods
comment|// -------------------------------------------------------------------------
specifier|protected
name|void
name|dispatch
parameter_list|(
name|ConnectionContext
name|context
parameter_list|,
name|Message
name|message
parameter_list|)
throws|throws
name|Exception
block|{
name|destinationStatistics
operator|.
name|getEnqueues
argument_list|()
operator|.
name|increment
argument_list|()
expr_stmt|;
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
if|if
condition|(
operator|!
name|subscriptionRecoveryPolicy
operator|.
name|add
argument_list|(
name|context
argument_list|,
name|message
argument_list|)
condition|)
block|{
return|return;
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
name|onMessageWithNoConsumers
argument_list|(
name|context
argument_list|,
name|message
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
name|message
argument_list|)
expr_stmt|;
if|if
condition|(
operator|!
name|dispatchPolicy
operator|.
name|dispatch
argument_list|(
name|message
argument_list|,
name|msgContext
argument_list|,
name|consumers
argument_list|)
condition|)
block|{
name|onMessageWithNoConsumers
argument_list|(
name|context
argument_list|,
name|message
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
name|dispatchValve
operator|.
name|decrement
argument_list|()
expr_stmt|;
block|}
block|}
comment|/**      * Provides a hook to allow messages with no consumer to be processed in      * some way - such as to send to a dead letter queue or something..      */
specifier|protected
name|void
name|onMessageWithNoConsumers
parameter_list|(
name|ConnectionContext
name|context
parameter_list|,
name|Message
name|message
parameter_list|)
throws|throws
name|Exception
block|{
if|if
condition|(
operator|!
name|message
operator|.
name|isPersistent
argument_list|()
condition|)
block|{
if|if
condition|(
name|sendAdvisoryIfNoConsumers
condition|)
block|{
comment|// allow messages with no consumers to be dispatched to a dead
comment|// letter queue
if|if
condition|(
operator|!
name|AdvisorySupport
operator|.
name|isAdvisoryTopic
argument_list|(
name|destination
argument_list|)
condition|)
block|{
comment|// The original destination and transaction id do not get filled when the message is first sent,
comment|// it is only populated if the message is routed to another destination like the DLQ
if|if
condition|(
name|message
operator|.
name|getOriginalDestination
argument_list|()
operator|!=
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
name|ActiveMQTopic
name|advisoryTopic
init|=
name|AdvisorySupport
operator|.
name|getNoTopicConsumersAdvisoryTopic
argument_list|(
name|destination
argument_list|)
decl_stmt|;
name|message
operator|.
name|setDestination
argument_list|(
name|advisoryTopic
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
comment|// Disable flow control for this since since we don't want to block.
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
block|}
block|}
block|}
end_class

end_unit

