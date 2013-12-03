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
name|jmx
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
name|management
operator|.
name|ObjectName
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
name|BrokerService
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
name|Subscription
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
name|filter
operator|.
name|DestinationFilter
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
name|IOExceptionSupport
import|;
end_import

begin_comment
comment|/**  *  */
end_comment

begin_class
specifier|public
class|class
name|SubscriptionView
implements|implements
name|SubscriptionViewMBean
block|{
specifier|protected
specifier|final
name|Subscription
name|subscription
decl_stmt|;
specifier|protected
specifier|final
name|String
name|clientId
decl_stmt|;
specifier|protected
specifier|final
name|String
name|userName
decl_stmt|;
comment|/**      * Constructor      *      * @param subs      */
specifier|public
name|SubscriptionView
parameter_list|(
name|String
name|clientId
parameter_list|,
name|String
name|userName
parameter_list|,
name|Subscription
name|subs
parameter_list|)
block|{
name|this
operator|.
name|clientId
operator|=
name|clientId
expr_stmt|;
name|this
operator|.
name|subscription
operator|=
name|subs
expr_stmt|;
name|this
operator|.
name|userName
operator|=
name|userName
expr_stmt|;
block|}
comment|/**      * @return the clientId      */
annotation|@
name|Override
specifier|public
name|String
name|getClientId
parameter_list|()
block|{
return|return
name|clientId
return|;
block|}
comment|/**      * @returns the ObjectName of the Connection that created this subscription      */
annotation|@
name|Override
specifier|public
name|ObjectName
name|getConnection
parameter_list|()
block|{
name|ObjectName
name|result
init|=
literal|null
decl_stmt|;
if|if
condition|(
name|clientId
operator|!=
literal|null
operator|&&
name|subscription
operator|!=
literal|null
condition|)
block|{
name|ConnectionContext
name|ctx
init|=
name|subscription
operator|.
name|getContext
argument_list|()
decl_stmt|;
if|if
condition|(
name|ctx
operator|!=
literal|null
operator|&&
name|ctx
operator|.
name|getBroker
argument_list|()
operator|!=
literal|null
operator|&&
name|ctx
operator|.
name|getBroker
argument_list|()
operator|.
name|getBrokerService
argument_list|()
operator|!=
literal|null
condition|)
block|{
name|BrokerService
name|service
init|=
name|ctx
operator|.
name|getBroker
argument_list|()
operator|.
name|getBrokerService
argument_list|()
decl_stmt|;
name|ManagementContext
name|managementCtx
init|=
name|service
operator|.
name|getManagementContext
argument_list|()
decl_stmt|;
if|if
condition|(
name|managementCtx
operator|!=
literal|null
condition|)
block|{
try|try
block|{
name|ObjectName
name|query
init|=
name|createConnectionQuery
argument_list|(
name|managementCtx
argument_list|,
name|service
operator|.
name|getBrokerName
argument_list|()
argument_list|)
decl_stmt|;
name|Set
argument_list|<
name|ObjectName
argument_list|>
name|names
init|=
name|managementCtx
operator|.
name|queryNames
argument_list|(
name|query
argument_list|,
literal|null
argument_list|)
decl_stmt|;
if|if
condition|(
name|names
operator|.
name|size
argument_list|()
operator|==
literal|1
condition|)
block|{
name|result
operator|=
name|names
operator|.
name|iterator
argument_list|()
operator|.
name|next
argument_list|()
expr_stmt|;
block|}
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{                     }
block|}
block|}
block|}
return|return
name|result
return|;
block|}
specifier|private
name|ObjectName
name|createConnectionQuery
parameter_list|(
name|ManagementContext
name|ctx
parameter_list|,
name|String
name|brokerName
parameter_list|)
throws|throws
name|IOException
block|{
try|try
block|{
return|return
name|BrokerMBeanSupport
operator|.
name|createConnectionQuery
argument_list|(
name|ctx
operator|.
name|getJmxDomainName
argument_list|()
argument_list|,
name|brokerName
argument_list|,
name|clientId
argument_list|)
return|;
block|}
catch|catch
parameter_list|(
name|Throwable
name|e
parameter_list|)
block|{
throw|throw
name|IOExceptionSupport
operator|.
name|create
argument_list|(
name|e
argument_list|)
throw|;
block|}
block|}
comment|/**      * @return the id of the Connection the Subscription is on      */
annotation|@
name|Override
specifier|public
name|String
name|getConnectionId
parameter_list|()
block|{
name|ConsumerInfo
name|info
init|=
name|getConsumerInfo
argument_list|()
decl_stmt|;
if|if
condition|(
name|info
operator|!=
literal|null
condition|)
block|{
return|return
name|info
operator|.
name|getConsumerId
argument_list|()
operator|.
name|getConnectionId
argument_list|()
return|;
block|}
return|return
literal|"NOTSET"
return|;
block|}
comment|/**      * @return the id of the Session the subscription is on      */
annotation|@
name|Override
specifier|public
name|long
name|getSessionId
parameter_list|()
block|{
name|ConsumerInfo
name|info
init|=
name|getConsumerInfo
argument_list|()
decl_stmt|;
if|if
condition|(
name|info
operator|!=
literal|null
condition|)
block|{
return|return
name|info
operator|.
name|getConsumerId
argument_list|()
operator|.
name|getSessionId
argument_list|()
return|;
block|}
return|return
literal|0
return|;
block|}
comment|/**      * @return the id of the Subscription      */
annotation|@
name|Deprecated
annotation|@
name|Override
specifier|public
name|long
name|getSubcriptionId
parameter_list|()
block|{
return|return
name|getSubscriptionId
argument_list|()
return|;
block|}
comment|/**      * @return the id of the Subscription      */
annotation|@
name|Override
specifier|public
name|long
name|getSubscriptionId
parameter_list|()
block|{
name|ConsumerInfo
name|info
init|=
name|getConsumerInfo
argument_list|()
decl_stmt|;
if|if
condition|(
name|info
operator|!=
literal|null
condition|)
block|{
return|return
name|info
operator|.
name|getConsumerId
argument_list|()
operator|.
name|getValue
argument_list|()
return|;
block|}
return|return
literal|0
return|;
block|}
comment|/**      * @return the destination name      */
annotation|@
name|Override
specifier|public
name|String
name|getDestinationName
parameter_list|()
block|{
name|ConsumerInfo
name|info
init|=
name|getConsumerInfo
argument_list|()
decl_stmt|;
if|if
condition|(
name|info
operator|!=
literal|null
condition|)
block|{
name|ActiveMQDestination
name|dest
init|=
name|info
operator|.
name|getDestination
argument_list|()
decl_stmt|;
return|return
name|dest
operator|.
name|getPhysicalName
argument_list|()
return|;
block|}
return|return
literal|"NOTSET"
return|;
block|}
annotation|@
name|Override
specifier|public
name|String
name|getSelector
parameter_list|()
block|{
if|if
condition|(
name|subscription
operator|!=
literal|null
condition|)
block|{
return|return
name|subscription
operator|.
name|getSelector
argument_list|()
return|;
block|}
return|return
literal|null
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
throws|,
name|UnsupportedOperationException
block|{
if|if
condition|(
name|subscription
operator|!=
literal|null
condition|)
block|{
name|subscription
operator|.
name|setSelector
argument_list|(
name|selector
argument_list|)
expr_stmt|;
block|}
else|else
block|{
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|(
literal|"No subscription object"
argument_list|)
throw|;
block|}
block|}
comment|/**      * @return true if the destination is a Queue      */
annotation|@
name|Override
specifier|public
name|boolean
name|isDestinationQueue
parameter_list|()
block|{
name|ConsumerInfo
name|info
init|=
name|getConsumerInfo
argument_list|()
decl_stmt|;
if|if
condition|(
name|info
operator|!=
literal|null
condition|)
block|{
name|ActiveMQDestination
name|dest
init|=
name|info
operator|.
name|getDestination
argument_list|()
decl_stmt|;
return|return
name|dest
operator|.
name|isQueue
argument_list|()
return|;
block|}
return|return
literal|false
return|;
block|}
comment|/**      * @return true of the destination is a Topic      */
annotation|@
name|Override
specifier|public
name|boolean
name|isDestinationTopic
parameter_list|()
block|{
name|ConsumerInfo
name|info
init|=
name|getConsumerInfo
argument_list|()
decl_stmt|;
if|if
condition|(
name|info
operator|!=
literal|null
condition|)
block|{
name|ActiveMQDestination
name|dest
init|=
name|info
operator|.
name|getDestination
argument_list|()
decl_stmt|;
return|return
name|dest
operator|.
name|isTopic
argument_list|()
return|;
block|}
return|return
literal|false
return|;
block|}
comment|/**      * @return true if the destination is temporary      */
annotation|@
name|Override
specifier|public
name|boolean
name|isDestinationTemporary
parameter_list|()
block|{
name|ConsumerInfo
name|info
init|=
name|getConsumerInfo
argument_list|()
decl_stmt|;
if|if
condition|(
name|info
operator|!=
literal|null
condition|)
block|{
name|ActiveMQDestination
name|dest
init|=
name|info
operator|.
name|getDestination
argument_list|()
decl_stmt|;
return|return
name|dest
operator|.
name|isTemporary
argument_list|()
return|;
block|}
return|return
literal|false
return|;
block|}
comment|/**      * @return true if the subscriber is active      */
annotation|@
name|Override
specifier|public
name|boolean
name|isActive
parameter_list|()
block|{
return|return
literal|true
return|;
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|isNetwork
parameter_list|()
block|{
name|ConsumerInfo
name|info
init|=
name|getConsumerInfo
argument_list|()
decl_stmt|;
if|if
condition|(
name|info
operator|!=
literal|null
condition|)
block|{
return|return
name|info
operator|.
name|isNetworkSubscription
argument_list|()
return|;
block|}
return|return
literal|false
return|;
block|}
comment|/**      * The subscription should release as may references as it can to help the      * garbage collector reclaim memory.      */
specifier|public
name|void
name|gc
parameter_list|()
block|{
if|if
condition|(
name|subscription
operator|!=
literal|null
condition|)
block|{
name|subscription
operator|.
name|gc
argument_list|()
expr_stmt|;
block|}
block|}
comment|/**      * @return whether or not the subscriber is retroactive or not      */
annotation|@
name|Override
specifier|public
name|boolean
name|isRetroactive
parameter_list|()
block|{
name|ConsumerInfo
name|info
init|=
name|getConsumerInfo
argument_list|()
decl_stmt|;
return|return
name|info
operator|!=
literal|null
condition|?
name|info
operator|.
name|isRetroactive
argument_list|()
else|:
literal|false
return|;
block|}
comment|/**      * @return whether or not the subscriber is an exclusive consumer      */
annotation|@
name|Override
specifier|public
name|boolean
name|isExclusive
parameter_list|()
block|{
name|ConsumerInfo
name|info
init|=
name|getConsumerInfo
argument_list|()
decl_stmt|;
return|return
name|info
operator|!=
literal|null
condition|?
name|info
operator|.
name|isExclusive
argument_list|()
else|:
literal|false
return|;
block|}
comment|/**      * @return whether or not the subscriber is durable (persistent)      */
annotation|@
name|Override
specifier|public
name|boolean
name|isDurable
parameter_list|()
block|{
name|ConsumerInfo
name|info
init|=
name|getConsumerInfo
argument_list|()
decl_stmt|;
return|return
name|info
operator|!=
literal|null
condition|?
name|info
operator|.
name|isDurable
argument_list|()
else|:
literal|false
return|;
block|}
comment|/**      * @return whether or not the subscriber ignores local messages      */
annotation|@
name|Override
specifier|public
name|boolean
name|isNoLocal
parameter_list|()
block|{
name|ConsumerInfo
name|info
init|=
name|getConsumerInfo
argument_list|()
decl_stmt|;
return|return
name|info
operator|!=
literal|null
condition|?
name|info
operator|.
name|isNoLocal
argument_list|()
else|:
literal|false
return|;
block|}
comment|/**      * @return the maximum number of pending messages allowed in addition to the      *         prefetch size. If enabled to a non-zero value then this will      *         perform eviction of messages for slow consumers on non-durable      *         topics.      */
annotation|@
name|Override
specifier|public
name|int
name|getMaximumPendingMessageLimit
parameter_list|()
block|{
name|ConsumerInfo
name|info
init|=
name|getConsumerInfo
argument_list|()
decl_stmt|;
return|return
name|info
operator|!=
literal|null
condition|?
name|info
operator|.
name|getMaximumPendingMessageLimit
argument_list|()
else|:
literal|0
return|;
block|}
comment|/**      * @return the consumer priority      */
annotation|@
name|Override
specifier|public
name|byte
name|getPriority
parameter_list|()
block|{
name|ConsumerInfo
name|info
init|=
name|getConsumerInfo
argument_list|()
decl_stmt|;
return|return
name|info
operator|!=
literal|null
condition|?
name|info
operator|.
name|getPriority
argument_list|()
else|:
literal|0
return|;
block|}
comment|/**      * @return the name of the consumer which is only used for durable      *         consumers.      */
annotation|@
name|Deprecated
annotation|@
name|Override
specifier|public
name|String
name|getSubcriptionName
parameter_list|()
block|{
return|return
name|getSubscriptionName
argument_list|()
return|;
block|}
comment|/**      * @return the name of the consumer which is only used for durable      *         consumers.      */
annotation|@
name|Override
specifier|public
name|String
name|getSubscriptionName
parameter_list|()
block|{
name|ConsumerInfo
name|info
init|=
name|getConsumerInfo
argument_list|()
decl_stmt|;
return|return
name|info
operator|!=
literal|null
condition|?
name|info
operator|.
name|getSubscriptionName
argument_list|()
else|:
literal|null
return|;
block|}
comment|/**      * @return number of messages pending delivery      */
annotation|@
name|Override
specifier|public
name|int
name|getPendingQueueSize
parameter_list|()
block|{
return|return
name|subscription
operator|!=
literal|null
condition|?
name|subscription
operator|.
name|getPendingQueueSize
argument_list|()
else|:
literal|0
return|;
block|}
comment|/**      * @return number of messages dispatched      */
annotation|@
name|Override
specifier|public
name|int
name|getDispatchedQueueSize
parameter_list|()
block|{
return|return
name|subscription
operator|!=
literal|null
condition|?
name|subscription
operator|.
name|getDispatchedQueueSize
argument_list|()
else|:
literal|0
return|;
block|}
annotation|@
name|Override
specifier|public
name|int
name|getMessageCountAwaitingAcknowledge
parameter_list|()
block|{
return|return
name|getDispatchedQueueSize
argument_list|()
return|;
block|}
comment|/**      * @return number of messages that matched the subscription      */
annotation|@
name|Override
specifier|public
name|long
name|getDispatchedCounter
parameter_list|()
block|{
return|return
name|subscription
operator|!=
literal|null
condition|?
name|subscription
operator|.
name|getDispatchedCounter
argument_list|()
else|:
literal|0
return|;
block|}
comment|/**      * @return number of messages that matched the subscription      */
annotation|@
name|Override
specifier|public
name|long
name|getEnqueueCounter
parameter_list|()
block|{
return|return
name|subscription
operator|!=
literal|null
condition|?
name|subscription
operator|.
name|getEnqueueCounter
argument_list|()
else|:
literal|0
return|;
block|}
comment|/**      * @return number of messages queued by the client      */
annotation|@
name|Override
specifier|public
name|long
name|getDequeueCounter
parameter_list|()
block|{
return|return
name|subscription
operator|!=
literal|null
condition|?
name|subscription
operator|.
name|getDequeueCounter
argument_list|()
else|:
literal|0
return|;
block|}
specifier|protected
name|ConsumerInfo
name|getConsumerInfo
parameter_list|()
block|{
return|return
name|subscription
operator|!=
literal|null
condition|?
name|subscription
operator|.
name|getConsumerInfo
argument_list|()
else|:
literal|null
return|;
block|}
comment|/**      * @return pretty print      */
annotation|@
name|Override
specifier|public
name|String
name|toString
parameter_list|()
block|{
return|return
literal|"SubscriptionView: "
operator|+
name|getClientId
argument_list|()
operator|+
literal|":"
operator|+
name|getConnectionId
argument_list|()
return|;
block|}
comment|/**      */
annotation|@
name|Override
specifier|public
name|int
name|getPrefetchSize
parameter_list|()
block|{
return|return
name|subscription
operator|!=
literal|null
condition|?
name|subscription
operator|.
name|getPrefetchSize
argument_list|()
else|:
literal|0
return|;
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|isMatchingQueue
parameter_list|(
name|String
name|queueName
parameter_list|)
block|{
if|if
condition|(
name|isDestinationQueue
argument_list|()
condition|)
block|{
return|return
name|matchesDestination
argument_list|(
operator|new
name|ActiveMQQueue
argument_list|(
name|queueName
argument_list|)
argument_list|)
return|;
block|}
return|return
literal|false
return|;
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|isMatchingTopic
parameter_list|(
name|String
name|topicName
parameter_list|)
block|{
if|if
condition|(
name|isDestinationTopic
argument_list|()
condition|)
block|{
return|return
name|matchesDestination
argument_list|(
operator|new
name|ActiveMQTopic
argument_list|(
name|topicName
argument_list|)
argument_list|)
return|;
block|}
return|return
literal|false
return|;
block|}
comment|/**      * Return true if this subscription matches the given destination      *      * @param destination the destination to compare against      * @return true if this subscription matches the given destination      */
specifier|public
name|boolean
name|matchesDestination
parameter_list|(
name|ActiveMQDestination
name|destination
parameter_list|)
block|{
name|ActiveMQDestination
name|subscriptionDestination
init|=
name|subscription
operator|.
name|getActiveMQDestination
argument_list|()
decl_stmt|;
name|DestinationFilter
name|filter
init|=
name|DestinationFilter
operator|.
name|parseFilter
argument_list|(
name|subscriptionDestination
argument_list|)
decl_stmt|;
return|return
name|filter
operator|.
name|matches
argument_list|(
name|destination
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|isSlowConsumer
parameter_list|()
block|{
return|return
name|subscription
operator|.
name|isSlowConsumer
argument_list|()
return|;
block|}
annotation|@
name|Override
specifier|public
name|String
name|getUserName
parameter_list|()
block|{
return|return
name|userName
return|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|resetStatistics
parameter_list|()
block|{
if|if
condition|(
name|subscription
operator|!=
literal|null
condition|)
block|{
name|subscription
operator|.
name|getConsumedCount
argument_list|()
operator|.
name|reset
argument_list|()
expr_stmt|;
block|}
block|}
annotation|@
name|Override
specifier|public
name|long
name|getConsumedCount
parameter_list|()
block|{
return|return
name|subscription
operator|!=
literal|null
condition|?
name|subscription
operator|.
name|getConsumedCount
argument_list|()
operator|.
name|getCount
argument_list|()
else|:
literal|0
return|;
block|}
block|}
end_class

end_unit

