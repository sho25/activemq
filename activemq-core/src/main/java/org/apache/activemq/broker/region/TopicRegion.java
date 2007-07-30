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
name|Iterator
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

begin_comment
comment|/**  *   * @version $Revision: 1.12 $  */
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
name|Log
name|log
init|=
name|LogFactory
operator|.
name|getLog
argument_list|(
name|TopicRegion
operator|.
name|class
argument_list|)
decl_stmt|;
specifier|protected
specifier|final
name|ConcurrentHashMap
name|durableSubscriptions
init|=
operator|new
name|ConcurrentHashMap
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
init|=
literal|false
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
name|UsageManager
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
block|}
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
operator|(
name|DurableTopicSubscription
operator|)
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
for|for
control|(
name|Iterator
name|iter
init|=
name|destinations
operator|.
name|values
argument_list|()
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
name|Topic
name|topic
init|=
operator|(
name|Topic
operator|)
name|iter
operator|.
name|next
argument_list|()
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
operator|(
name|DurableTopicSubscription
operator|)
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
operator|(
name|DurableTopicSubscription
operator|)
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
name|memoryManager
argument_list|,
name|context
argument_list|,
name|info
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
operator|(
name|DurableTopicSubscription
operator|)
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
name|getSubcriptionName
argument_list|()
argument_list|)
decl_stmt|;
name|DurableTopicSubscription
name|sub
init|=
operator|(
name|DurableTopicSubscription
operator|)
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
name|getSubcriptionName
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
name|durableSubscriptions
operator|.
name|remove
argument_list|(
name|key
argument_list|)
expr_stmt|;
for|for
control|(
name|Iterator
name|iter
init|=
name|destinations
operator|.
name|values
argument_list|()
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
name|Topic
name|topic
init|=
operator|(
name|Topic
operator|)
name|iter
operator|.
name|next
argument_list|()
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
name|memoryManager
operator|.
name|getPercentUsage
argument_list|()
operator|+
literal|"%"
return|;
block|}
comment|// Implementation methods
comment|// -------------------------------------------------------------------------
specifier|protected
name|Destination
name|createDestination
parameter_list|(
name|ConnectionContext
name|context
parameter_list|,
name|ActiveMQDestination
name|destination
parameter_list|)
throws|throws
name|Exception
block|{
name|Topic
name|topic
init|=
operator|(
name|Topic
operator|)
name|super
operator|.
name|createDestination
argument_list|(
name|context
argument_list|,
name|destination
argument_list|)
decl_stmt|;
name|recoverDurableSubscriptions
argument_list|(
name|context
argument_list|,
name|topic
argument_list|)
expr_stmt|;
return|return
name|topic
return|;
block|}
specifier|private
name|void
name|recoverDurableSubscriptions
parameter_list|(
name|ConnectionContext
name|context
parameter_list|,
name|Topic
name|topic
parameter_list|)
throws|throws
name|IOException
throws|,
name|JMSException
throws|,
name|Exception
block|{
name|TopicMessageStore
name|store
init|=
operator|(
name|TopicMessageStore
operator|)
name|topic
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
name|log
operator|.
name|debug
argument_list|(
literal|"Restoring durable subscription: "
operator|+
name|infos
argument_list|)
expr_stmt|;
name|SubscriptionKey
name|key
init|=
operator|new
name|SubscriptionKey
argument_list|(
name|info
argument_list|)
decl_stmt|;
comment|// A single durable sub may be subscribing to multiple topics.  so it might exist already.
name|DurableTopicSubscription
name|sub
init|=
operator|(
name|DurableTopicSubscription
operator|)
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
block|}
name|topic
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
specifier|private
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
name|getDestination
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
name|topic
argument_list|)
expr_stmt|;
block|}
block|}
block|}
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
operator|(
name|DurableTopicSubscription
operator|)
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
name|memoryManager
argument_list|,
name|context
argument_list|,
name|info
argument_list|,
name|keepDurableSubsActive
argument_list|)
expr_stmt|;
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
name|memoryManager
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
name|memoryManager
argument_list|)
decl_stmt|;
comment|// lets configure the subscription depending on the destination
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
name|memoryManager
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
name|log
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
comment|/**      */
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
return|return
literal|true
return|;
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
return|return
literal|true
return|;
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
specifier|protected
name|Set
name|getInactiveDestinations
parameter_list|()
block|{
name|Set
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
operator|(
name|ActiveMQDestination
operator|)
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
name|iter
operator|.
name|remove
argument_list|()
expr_stmt|;
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
block|}
end_class

end_unit

