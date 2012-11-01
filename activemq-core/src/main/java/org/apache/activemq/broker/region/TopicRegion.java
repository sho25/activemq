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
name|util
operator|.
name|HashSet
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
name|List
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Map
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
name|java
operator|.
name|util
operator|.
name|Timer
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|TimerTask
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
name|javax
operator|.
name|jms
operator|.
name|InvalidDestinationException
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
name|ConnectionId
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
name|RemoveSubscriptionInfo
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
name|SessionId
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
name|util
operator|.
name|LongSequenceGenerator
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

begin_comment
comment|/**  *  */
end_comment

begin_class
specifier|public
class|class
name|TopicRegion
extends|extends
name|AbstractRegion
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
name|TopicRegion
operator|.
name|class
argument_list|)
decl_stmt|;
specifier|protected
specifier|final
name|ConcurrentHashMap
argument_list|<
name|SubscriptionKey
argument_list|,
name|DurableTopicSubscription
argument_list|>
name|durableSubscriptions
init|=
operator|new
name|ConcurrentHashMap
argument_list|<
name|SubscriptionKey
argument_list|,
name|DurableTopicSubscription
argument_list|>
argument_list|()
decl_stmt|;
specifier|private
specifier|final
name|LongSequenceGenerator
name|recoveredDurableSubIdGenerator
init|=
operator|new
name|LongSequenceGenerator
argument_list|()
decl_stmt|;
specifier|private
specifier|final
name|SessionId
name|recoveredDurableSubSessionId
init|=
operator|new
name|SessionId
argument_list|(
operator|new
name|ConnectionId
argument_list|(
literal|"OFFLINE"
argument_list|)
argument_list|,
name|recoveredDurableSubIdGenerator
operator|.
name|getNextSequenceId
argument_list|()
argument_list|)
decl_stmt|;
specifier|private
name|boolean
name|keepDurableSubsActive
decl_stmt|;
specifier|private
name|Timer
name|cleanupTimer
decl_stmt|;
specifier|private
name|TimerTask
name|cleanupTask
decl_stmt|;
specifier|public
name|TopicRegion
parameter_list|(
name|RegionBroker
name|broker
parameter_list|,
name|DestinationStatistics
name|destinationStatistics
parameter_list|,
name|SystemUsage
name|memoryManager
parameter_list|,
name|TaskRunnerFactory
name|taskRunnerFactory
parameter_list|,
name|DestinationFactory
name|destinationFactory
parameter_list|)
block|{
name|super
argument_list|(
name|broker
argument_list|,
name|destinationStatistics
argument_list|,
name|memoryManager
argument_list|,
name|taskRunnerFactory
argument_list|,
name|destinationFactory
argument_list|)
expr_stmt|;
if|if
condition|(
name|broker
operator|.
name|getBrokerService
argument_list|()
operator|.
name|getOfflineDurableSubscriberTaskSchedule
argument_list|()
operator|!=
operator|-
literal|1
operator|&&
name|broker
operator|.
name|getBrokerService
argument_list|()
operator|.
name|getOfflineDurableSubscriberTimeout
argument_list|()
operator|!=
operator|-
literal|1
condition|)
block|{
name|this
operator|.
name|cleanupTimer
operator|=
operator|new
name|Timer
argument_list|(
literal|"ActiveMQ Durable Subscriber Cleanup Timer"
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|this
operator|.
name|cleanupTask
operator|=
operator|new
name|TimerTask
argument_list|()
block|{
specifier|public
name|void
name|run
parameter_list|()
block|{
name|doCleanup
argument_list|()
expr_stmt|;
block|}
block|}
expr_stmt|;
name|this
operator|.
name|cleanupTimer
operator|.
name|schedule
argument_list|(
name|cleanupTask
argument_list|,
name|broker
operator|.
name|getBrokerService
argument_list|()
operator|.
name|getOfflineDurableSubscriberTaskSchedule
argument_list|()
argument_list|,
name|broker
operator|.
name|getBrokerService
argument_list|()
operator|.
name|getOfflineDurableSubscriberTaskSchedule
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Override
specifier|public
name|void
name|stop
parameter_list|()
throws|throws
name|Exception
block|{
name|super
operator|.
name|stop
argument_list|()
expr_stmt|;
if|if
condition|(
name|cleanupTimer
operator|!=
literal|null
condition|)
block|{
name|cleanupTimer
operator|.
name|cancel
argument_list|()
expr_stmt|;
block|}
block|}
specifier|public
name|void
name|doCleanup
parameter_list|()
block|{
name|long
name|now
init|=
name|System
operator|.
name|currentTimeMillis
argument_list|()
decl_stmt|;
for|for
control|(
name|Map
operator|.
name|Entry
argument_list|<
name|SubscriptionKey
argument_list|,
name|DurableTopicSubscription
argument_list|>
name|entry
range|:
name|durableSubscriptions
operator|.
name|entrySet
argument_list|()
control|)
block|{
name|DurableTopicSubscription
name|sub
init|=
name|entry
operator|.
name|getValue
argument_list|()
decl_stmt|;
if|if
condition|(
operator|!
name|sub
operator|.
name|isActive
argument_list|()
condition|)
block|{
name|long
name|offline
init|=
name|sub
operator|.
name|getOfflineTimestamp
argument_list|()
decl_stmt|;
if|if
condition|(
name|offline
operator|!=
operator|-
literal|1
operator|&&
name|now
operator|-
name|offline
operator|>=
name|broker
operator|.
name|getBrokerService
argument_list|()
operator|.
name|getOfflineDurableSubscriberTimeout
argument_list|()
condition|)
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"Destroying durable subscriber due to inactivity: "
operator|+
name|sub
argument_list|)
expr_stmt|;
try|try
block|{
name|RemoveSubscriptionInfo
name|info
init|=
operator|new
name|RemoveSubscriptionInfo
argument_list|()
decl_stmt|;
name|info
operator|.
name|setClientId
argument_list|(
name|entry
operator|.
name|getKey
argument_list|()
operator|.
name|getClientId
argument_list|()
argument_list|)
expr_stmt|;
name|info
operator|.
name|setSubscriptionName
argument_list|(
name|entry
operator|.
name|getKey
argument_list|()
operator|.
name|getSubscriptionName
argument_list|()
argument_list|)
expr_stmt|;
name|ConnectionContext
name|context
init|=
operator|new
name|ConnectionContext
argument_list|()
decl_stmt|;
name|context
operator|.
name|setBroker
argument_list|(
name|broker
argument_list|)
expr_stmt|;
name|context
operator|.
name|setClientId
argument_list|(
name|entry
operator|.
name|getKey
argument_list|()
operator|.
name|getClientId
argument_list|()
argument_list|)
expr_stmt|;
name|removeSubscription
argument_list|(
name|context
argument_list|,
name|info
argument_list|)
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
name|error
argument_list|(
literal|"Failed to remove inactive durable subscriber"
argument_list|,
name|e
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|}
block|}
annotation|@
name|Override
specifier|public
name|Subscription
name|addConsumer
parameter_list|(
name|ConnectionContext
name|context
parameter_list|,
name|ConsumerInfo
name|info
parameter_list|)
throws|throws
name|Exception
block|{
if|if
condition|(
name|info
operator|.
name|isDurable
argument_list|()
condition|)
block|{
name|ActiveMQDestination
name|destination
init|=
name|info
operator|.
name|getDestination
argument_list|()
decl_stmt|;
if|if
condition|(
operator|!
name|destination
operator|.
name|isPattern
argument_list|()
condition|)
block|{
comment|// Make sure the destination is created.
name|lookup
argument_list|(
name|context
argument_list|,
name|destination
argument_list|,
literal|true
argument_list|)
expr_stmt|;
block|}
name|String
name|clientId
init|=
name|context
operator|.
name|getClientId
argument_list|()
decl_stmt|;
name|String
name|subscriptionName
init|=
name|info
operator|.
name|getSubscriptionName
argument_list|()
decl_stmt|;
name|SubscriptionKey
name|key
init|=
operator|new
name|SubscriptionKey
argument_list|(
name|clientId
argument_list|,
name|subscriptionName
argument_list|)
decl_stmt|;
name|DurableTopicSubscription
name|sub
init|=
name|durableSubscriptions
operator|.
name|get
argument_list|(
name|key
argument_list|)
decl_stmt|;
if|if
condition|(
name|sub
operator|!=
literal|null
condition|)
block|{
if|if
condition|(
name|sub
operator|.
name|isActive
argument_list|()
condition|)
block|{
throw|throw
operator|new
name|JMSException
argument_list|(
literal|"Durable consumer is in use for client: "
operator|+
name|clientId
operator|+
literal|" and subscriptionName: "
operator|+
name|subscriptionName
argument_list|)
throw|;
block|}
comment|// Has the selector changed??
if|if
condition|(
name|hasDurableSubChanged
argument_list|(
name|info
argument_list|,
name|sub
operator|.
name|getConsumerInfo
argument_list|()
argument_list|)
condition|)
block|{
comment|// Remove the consumer first then add it.
name|durableSubscriptions
operator|.
name|remove
argument_list|(
name|key
argument_list|)
expr_stmt|;
name|destinationsLock
operator|.
name|readLock
argument_list|()
operator|.
name|lock
argument_list|()
expr_stmt|;
try|try
block|{
for|for
control|(
name|Destination
name|dest
range|:
name|destinations
operator|.
name|values
argument_list|()
control|)
block|{
comment|//Account for virtual destinations
if|if
condition|(
name|dest
operator|instanceof
name|Topic
condition|)
block|{
name|Topic
name|topic
init|=
operator|(
name|Topic
operator|)
name|dest
decl_stmt|;
name|topic
operator|.
name|deleteSubscription
argument_list|(
name|context
argument_list|,
name|key
argument_list|)
expr_stmt|;
block|}
block|}
block|}
finally|finally
block|{
name|destinationsLock
operator|.
name|readLock
argument_list|()
operator|.
name|unlock
argument_list|()
expr_stmt|;
block|}
name|super
operator|.
name|removeConsumer
argument_list|(
name|context
argument_list|,
name|sub
operator|.
name|getConsumerInfo
argument_list|()
argument_list|)
expr_stmt|;
name|super
operator|.
name|addConsumer
argument_list|(
name|context
argument_list|,
name|info
argument_list|)
expr_stmt|;
name|sub
operator|=
name|durableSubscriptions
operator|.
name|get
argument_list|(
name|key
argument_list|)
expr_stmt|;
block|}
else|else
block|{
comment|// Change the consumer id key of the durable sub.
if|if
condition|(
name|sub
operator|.
name|getConsumerInfo
argument_list|()
operator|.
name|getConsumerId
argument_list|()
operator|!=
literal|null
condition|)
block|{
name|subscriptions
operator|.
name|remove
argument_list|(
name|sub
operator|.
name|getConsumerInfo
argument_list|()
operator|.
name|getConsumerId
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|subscriptions
operator|.
name|put
argument_list|(
name|info
operator|.
name|getConsumerId
argument_list|()
argument_list|,
name|sub
argument_list|)
expr_stmt|;
block|}
block|}
else|else
block|{
name|super
operator|.
name|addConsumer
argument_list|(
name|context
argument_list|,
name|info
argument_list|)
expr_stmt|;
name|sub
operator|=
name|durableSubscriptions
operator|.
name|get
argument_list|(
name|key
argument_list|)
expr_stmt|;
if|if
condition|(
name|sub
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|JMSException
argument_list|(
literal|"Cannot use the same consumerId: "
operator|+
name|info
operator|.
name|getConsumerId
argument_list|()
operator|+
literal|" for two different durable subscriptions clientID: "
operator|+
name|key
operator|.
name|getClientId
argument_list|()
operator|+
literal|" subscriberName: "
operator|+
name|key
operator|.
name|getSubscriptionName
argument_list|()
argument_list|)
throw|;
block|}
block|}
name|sub
operator|.
name|activate
argument_list|(
name|usageManager
argument_list|,
name|context
argument_list|,
name|info
argument_list|,
name|broker
argument_list|)
expr_stmt|;
return|return
name|sub
return|;
block|}
else|else
block|{
return|return
name|super
operator|.
name|addConsumer
argument_list|(
name|context
argument_list|,
name|info
argument_list|)
return|;
block|}
block|}
annotation|@
name|Override
specifier|public
name|void
name|removeConsumer
parameter_list|(
name|ConnectionContext
name|context
parameter_list|,
name|ConsumerInfo
name|info
parameter_list|)
throws|throws
name|Exception
block|{
if|if
condition|(
name|info
operator|.
name|isDurable
argument_list|()
condition|)
block|{
name|SubscriptionKey
name|key
init|=
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
decl_stmt|;
name|DurableTopicSubscription
name|sub
init|=
name|durableSubscriptions
operator|.
name|get
argument_list|(
name|key
argument_list|)
decl_stmt|;
if|if
condition|(
name|sub
operator|!=
literal|null
condition|)
block|{
name|sub
operator|.
name|deactivate
argument_list|(
name|keepDurableSubsActive
argument_list|)
expr_stmt|;
block|}
block|}
else|else
block|{
name|super
operator|.
name|removeConsumer
argument_list|(
name|context
argument_list|,
name|info
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Override
specifier|public
name|void
name|removeSubscription
parameter_list|(
name|ConnectionContext
name|context
parameter_list|,
name|RemoveSubscriptionInfo
name|info
parameter_list|)
throws|throws
name|Exception
block|{
name|SubscriptionKey
name|key
init|=
operator|new
name|SubscriptionKey
argument_list|(
name|info
operator|.
name|getClientId
argument_list|()
argument_list|,
name|info
operator|.
name|getSubscriptionName
argument_list|()
argument_list|)
decl_stmt|;
name|DurableTopicSubscription
name|sub
init|=
name|durableSubscriptions
operator|.
name|get
argument_list|(
name|key
argument_list|)
decl_stmt|;
if|if
condition|(
name|sub
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|InvalidDestinationException
argument_list|(
literal|"No durable subscription exists for: "
operator|+
name|info
operator|.
name|getSubscriptionName
argument_list|()
argument_list|)
throw|;
block|}
if|if
condition|(
name|sub
operator|.
name|isActive
argument_list|()
condition|)
block|{
throw|throw
operator|new
name|JMSException
argument_list|(
literal|"Durable consumer is in use"
argument_list|)
throw|;
block|}
else|else
block|{
name|durableSubscriptions
operator|.
name|remove
argument_list|(
name|key
argument_list|)
expr_stmt|;
block|}
name|destinationsLock
operator|.
name|readLock
argument_list|()
operator|.
name|lock
argument_list|()
expr_stmt|;
try|try
block|{
for|for
control|(
name|Destination
name|dest
range|:
name|destinations
operator|.
name|values
argument_list|()
control|)
block|{
comment|//Account for virtual destinations
if|if
condition|(
name|dest
operator|instanceof
name|Topic
condition|)
block|{
name|Topic
name|topic
init|=
operator|(
name|Topic
operator|)
name|dest
decl_stmt|;
name|topic
operator|.
name|deleteSubscription
argument_list|(
name|context
argument_list|,
name|key
argument_list|)
expr_stmt|;
block|}
block|}
block|}
finally|finally
block|{
name|destinationsLock
operator|.
name|readLock
argument_list|()
operator|.
name|unlock
argument_list|()
expr_stmt|;
block|}
if|if
condition|(
name|subscriptions
operator|.
name|get
argument_list|(
name|sub
operator|.
name|getConsumerInfo
argument_list|()
operator|.
name|getConsumerId
argument_list|()
argument_list|)
operator|!=
literal|null
condition|)
block|{
name|super
operator|.
name|removeConsumer
argument_list|(
name|context
argument_list|,
name|sub
operator|.
name|getConsumerInfo
argument_list|()
argument_list|)
expr_stmt|;
block|}
else|else
block|{
comment|// try destroying inactive subscriptions
name|destroySubscription
argument_list|(
name|sub
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Override
specifier|public
name|String
name|toString
parameter_list|()
block|{
return|return
literal|"TopicRegion: destinations="
operator|+
name|destinations
operator|.
name|size
argument_list|()
operator|+
literal|", subscriptions="
operator|+
name|subscriptions
operator|.
name|size
argument_list|()
operator|+
literal|", memory="
operator|+
name|usageManager
operator|.
name|getMemoryUsage
argument_list|()
operator|.
name|getPercentUsage
argument_list|()
operator|+
literal|"%"
return|;
block|}
annotation|@
name|Override
specifier|protected
name|List
argument_list|<
name|Subscription
argument_list|>
name|addSubscriptionsForDestination
parameter_list|(
name|ConnectionContext
name|context
parameter_list|,
name|Destination
name|dest
parameter_list|)
throws|throws
name|Exception
block|{
name|List
argument_list|<
name|Subscription
argument_list|>
name|rc
init|=
name|super
operator|.
name|addSubscriptionsForDestination
argument_list|(
name|context
argument_list|,
name|dest
argument_list|)
decl_stmt|;
name|Set
argument_list|<
name|Subscription
argument_list|>
name|dupChecker
init|=
operator|new
name|HashSet
argument_list|<
name|Subscription
argument_list|>
argument_list|(
name|rc
argument_list|)
decl_stmt|;
name|TopicMessageStore
name|store
init|=
operator|(
name|TopicMessageStore
operator|)
name|dest
operator|.
name|getMessageStore
argument_list|()
decl_stmt|;
comment|// Eagerly recover the durable subscriptions
if|if
condition|(
name|store
operator|!=
literal|null
condition|)
block|{
name|SubscriptionInfo
index|[]
name|infos
init|=
name|store
operator|.
name|getAllSubscriptions
argument_list|()
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
name|infos
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|SubscriptionInfo
name|info
init|=
name|infos
index|[
name|i
index|]
decl_stmt|;
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
literal|"Restoring durable subscription: "
operator|+
name|info
argument_list|)
expr_stmt|;
block|}
name|SubscriptionKey
name|key
init|=
operator|new
name|SubscriptionKey
argument_list|(
name|info
argument_list|)
decl_stmt|;
comment|// A single durable sub may be subscribing to multiple topics.
comment|// so it might exist already.
name|DurableTopicSubscription
name|sub
init|=
name|durableSubscriptions
operator|.
name|get
argument_list|(
name|key
argument_list|)
decl_stmt|;
name|ConsumerInfo
name|consumerInfo
init|=
name|createInactiveConsumerInfo
argument_list|(
name|info
argument_list|)
decl_stmt|;
if|if
condition|(
name|sub
operator|==
literal|null
condition|)
block|{
name|ConnectionContext
name|c
init|=
operator|new
name|ConnectionContext
argument_list|()
decl_stmt|;
name|c
operator|.
name|setBroker
argument_list|(
name|context
operator|.
name|getBroker
argument_list|()
argument_list|)
expr_stmt|;
name|c
operator|.
name|setClientId
argument_list|(
name|key
operator|.
name|getClientId
argument_list|()
argument_list|)
expr_stmt|;
name|c
operator|.
name|setConnectionId
argument_list|(
name|consumerInfo
operator|.
name|getConsumerId
argument_list|()
operator|.
name|getParentId
argument_list|()
operator|.
name|getParentId
argument_list|()
argument_list|)
expr_stmt|;
name|sub
operator|=
operator|(
name|DurableTopicSubscription
operator|)
name|createSubscription
argument_list|(
name|c
argument_list|,
name|consumerInfo
argument_list|)
expr_stmt|;
name|sub
operator|.
name|setOfflineTimestamp
argument_list|(
name|System
operator|.
name|currentTimeMillis
argument_list|()
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|dupChecker
operator|.
name|contains
argument_list|(
name|sub
argument_list|)
condition|)
block|{
continue|continue;
block|}
name|dupChecker
operator|.
name|add
argument_list|(
name|sub
argument_list|)
expr_stmt|;
name|rc
operator|.
name|add
argument_list|(
name|sub
argument_list|)
expr_stmt|;
name|dest
operator|.
name|addSubscription
argument_list|(
name|context
argument_list|,
name|sub
argument_list|)
expr_stmt|;
block|}
comment|// Now perhaps there other durable subscriptions (via wild card)
comment|// that would match this destination..
name|durableSubscriptions
operator|.
name|values
argument_list|()
expr_stmt|;
for|for
control|(
name|DurableTopicSubscription
name|sub
range|:
name|durableSubscriptions
operator|.
name|values
argument_list|()
control|)
block|{
comment|// Skip over subscriptions that we already added..
if|if
condition|(
name|dupChecker
operator|.
name|contains
argument_list|(
name|sub
argument_list|)
condition|)
block|{
continue|continue;
block|}
if|if
condition|(
name|sub
operator|.
name|matches
argument_list|(
name|dest
operator|.
name|getActiveMQDestination
argument_list|()
argument_list|)
condition|)
block|{
name|rc
operator|.
name|add
argument_list|(
name|sub
argument_list|)
expr_stmt|;
name|dest
operator|.
name|addSubscription
argument_list|(
name|context
argument_list|,
name|sub
argument_list|)
expr_stmt|;
block|}
block|}
block|}
return|return
name|rc
return|;
block|}
specifier|public
name|ConsumerInfo
name|createInactiveConsumerInfo
parameter_list|(
name|SubscriptionInfo
name|info
parameter_list|)
block|{
name|ConsumerInfo
name|rc
init|=
operator|new
name|ConsumerInfo
argument_list|()
decl_stmt|;
name|rc
operator|.
name|setSelector
argument_list|(
name|info
operator|.
name|getSelector
argument_list|()
argument_list|)
expr_stmt|;
name|rc
operator|.
name|setSubscriptionName
argument_list|(
name|info
operator|.
name|getSubscriptionName
argument_list|()
argument_list|)
expr_stmt|;
name|rc
operator|.
name|setDestination
argument_list|(
name|info
operator|.
name|getSubscribedDestination
argument_list|()
argument_list|)
expr_stmt|;
name|rc
operator|.
name|setConsumerId
argument_list|(
name|createConsumerId
argument_list|()
argument_list|)
expr_stmt|;
return|return
name|rc
return|;
block|}
specifier|private
name|ConsumerId
name|createConsumerId
parameter_list|()
block|{
return|return
operator|new
name|ConsumerId
argument_list|(
name|recoveredDurableSubSessionId
argument_list|,
name|recoveredDurableSubIdGenerator
operator|.
name|getNextSequenceId
argument_list|()
argument_list|)
return|;
block|}
specifier|protected
name|void
name|configureTopic
parameter_list|(
name|Topic
name|topic
parameter_list|,
name|ActiveMQDestination
name|destination
parameter_list|)
block|{
if|if
condition|(
name|broker
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
name|broker
operator|.
name|getDestinationPolicy
argument_list|()
operator|.
name|getEntryFor
argument_list|(
name|destination
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
name|topic
argument_list|)
expr_stmt|;
block|}
block|}
block|}
annotation|@
name|Override
specifier|protected
name|Subscription
name|createSubscription
parameter_list|(
name|ConnectionContext
name|context
parameter_list|,
name|ConsumerInfo
name|info
parameter_list|)
throws|throws
name|JMSException
block|{
name|ActiveMQDestination
name|destination
init|=
name|info
operator|.
name|getDestination
argument_list|()
decl_stmt|;
if|if
condition|(
name|info
operator|.
name|isDurable
argument_list|()
condition|)
block|{
if|if
condition|(
name|AdvisorySupport
operator|.
name|isAdvisoryTopic
argument_list|(
name|info
operator|.
name|getDestination
argument_list|()
argument_list|)
condition|)
block|{
throw|throw
operator|new
name|JMSException
argument_list|(
literal|"Cannot create a durable subscription for an advisory Topic"
argument_list|)
throw|;
block|}
name|SubscriptionKey
name|key
init|=
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
decl_stmt|;
name|DurableTopicSubscription
name|sub
init|=
name|durableSubscriptions
operator|.
name|get
argument_list|(
name|key
argument_list|)
decl_stmt|;
if|if
condition|(
name|sub
operator|==
literal|null
condition|)
block|{
name|sub
operator|=
operator|new
name|DurableTopicSubscription
argument_list|(
name|broker
argument_list|,
name|usageManager
argument_list|,
name|context
argument_list|,
name|info
argument_list|,
name|keepDurableSubsActive
argument_list|)
expr_stmt|;
if|if
condition|(
name|destination
operator|!=
literal|null
operator|&&
name|broker
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
name|broker
operator|.
name|getDestinationPolicy
argument_list|()
operator|.
name|getEntryFor
argument_list|(
name|destination
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
name|sub
argument_list|)
expr_stmt|;
block|}
block|}
name|durableSubscriptions
operator|.
name|put
argument_list|(
name|key
argument_list|,
name|sub
argument_list|)
expr_stmt|;
block|}
else|else
block|{
throw|throw
operator|new
name|JMSException
argument_list|(
literal|"That durable subscription is already active."
argument_list|)
throw|;
block|}
return|return
name|sub
return|;
block|}
try|try
block|{
name|TopicSubscription
name|answer
init|=
operator|new
name|TopicSubscription
argument_list|(
name|broker
argument_list|,
name|context
argument_list|,
name|info
argument_list|,
name|usageManager
argument_list|)
decl_stmt|;
comment|// lets configure the subscription depending on the destination
if|if
condition|(
name|destination
operator|!=
literal|null
operator|&&
name|broker
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
name|broker
operator|.
name|getDestinationPolicy
argument_list|()
operator|.
name|getEntryFor
argument_list|(
name|destination
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
name|answer
argument_list|)
expr_stmt|;
block|}
block|}
name|answer
operator|.
name|init
argument_list|()
expr_stmt|;
return|return
name|answer
return|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
name|LOG
operator|.
name|error
argument_list|(
literal|"Failed to create TopicSubscription "
argument_list|,
name|e
argument_list|)
expr_stmt|;
name|JMSException
name|jmsEx
init|=
operator|new
name|JMSException
argument_list|(
literal|"Couldn't create TopicSubscription"
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
specifier|private
name|boolean
name|hasDurableSubChanged
parameter_list|(
name|ConsumerInfo
name|info1
parameter_list|,
name|ConsumerInfo
name|info2
parameter_list|)
block|{
if|if
condition|(
name|info1
operator|.
name|getSelector
argument_list|()
operator|!=
literal|null
operator|^
name|info2
operator|.
name|getSelector
argument_list|()
operator|!=
literal|null
condition|)
block|{
return|return
literal|true
return|;
block|}
if|if
condition|(
name|info1
operator|.
name|getSelector
argument_list|()
operator|!=
literal|null
operator|&&
operator|!
name|info1
operator|.
name|getSelector
argument_list|()
operator|.
name|equals
argument_list|(
name|info2
operator|.
name|getSelector
argument_list|()
argument_list|)
condition|)
block|{
return|return
literal|true
return|;
block|}
return|return
operator|!
name|info1
operator|.
name|getDestination
argument_list|()
operator|.
name|equals
argument_list|(
name|info2
operator|.
name|getDestination
argument_list|()
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|protected
name|Set
argument_list|<
name|ActiveMQDestination
argument_list|>
name|getInactiveDestinations
parameter_list|()
block|{
name|Set
argument_list|<
name|ActiveMQDestination
argument_list|>
name|inactiveDestinations
init|=
name|super
operator|.
name|getInactiveDestinations
argument_list|()
decl_stmt|;
for|for
control|(
name|Iterator
argument_list|<
name|ActiveMQDestination
argument_list|>
name|iter
init|=
name|inactiveDestinations
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
name|ActiveMQDestination
name|dest
init|=
name|iter
operator|.
name|next
argument_list|()
decl_stmt|;
if|if
condition|(
operator|!
name|dest
operator|.
name|isTopic
argument_list|()
condition|)
block|{
name|iter
operator|.
name|remove
argument_list|()
expr_stmt|;
block|}
block|}
return|return
name|inactiveDestinations
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
specifier|public
name|void
name|setKeepDurableSubsActive
parameter_list|(
name|boolean
name|keepDurableSubsActive
parameter_list|)
block|{
name|this
operator|.
name|keepDurableSubsActive
operator|=
name|keepDurableSubsActive
expr_stmt|;
block|}
specifier|public
name|boolean
name|durableSubscriptionExists
parameter_list|(
name|SubscriptionKey
name|key
parameter_list|)
block|{
return|return
name|this
operator|.
name|durableSubscriptions
operator|.
name|containsKey
argument_list|(
name|key
argument_list|)
return|;
block|}
block|}
end_class

end_unit

