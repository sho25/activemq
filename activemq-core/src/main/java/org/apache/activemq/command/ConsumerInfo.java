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
name|command
package|;
end_package

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
name|filter
operator|.
name|BooleanExpression
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
name|state
operator|.
name|CommandVisitor
import|;
end_import

begin_comment
comment|/**  * @openwire:marshaller code="5"  * @version $Revision: 1.20 $  */
end_comment

begin_class
specifier|public
class|class
name|ConsumerInfo
extends|extends
name|BaseCommand
block|{
specifier|public
specifier|static
specifier|final
name|byte
name|DATA_STRUCTURE_TYPE
init|=
name|CommandTypes
operator|.
name|CONSUMER_INFO
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|byte
name|HIGH_PRIORITY
init|=
literal|10
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|byte
name|NORMAL_PRIORITY
init|=
literal|0
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|byte
name|NETWORK_CONSUMER_PRIORITY
init|=
operator|-
literal|5
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|byte
name|LOW_PRIORITY
init|=
operator|-
literal|10
decl_stmt|;
specifier|protected
name|ConsumerId
name|consumerId
decl_stmt|;
specifier|protected
name|ActiveMQDestination
name|destination
decl_stmt|;
specifier|protected
name|int
name|prefetchSize
decl_stmt|;
specifier|protected
name|int
name|maximumPendingMessageLimit
decl_stmt|;
specifier|protected
name|boolean
name|browser
decl_stmt|;
specifier|protected
name|boolean
name|dispatchAsync
decl_stmt|;
specifier|protected
name|String
name|selector
decl_stmt|;
specifier|protected
name|String
name|subscriptionName
decl_stmt|;
specifier|protected
name|boolean
name|noLocal
decl_stmt|;
specifier|protected
name|boolean
name|exclusive
decl_stmt|;
specifier|protected
name|boolean
name|retroactive
decl_stmt|;
specifier|protected
name|byte
name|priority
decl_stmt|;
specifier|protected
name|BrokerId
index|[]
name|brokerPath
decl_stmt|;
specifier|protected
name|boolean
name|optimizedAcknowledge
decl_stmt|;
comment|// used by the broker
specifier|protected
specifier|transient
name|int
name|currentPrefetchSize
decl_stmt|;
comment|// if true, the consumer will not send range
specifier|protected
name|boolean
name|noRangeAcks
decl_stmt|;
comment|// acks.
specifier|protected
name|BooleanExpression
name|additionalPredicate
decl_stmt|;
specifier|protected
specifier|transient
name|boolean
name|networkSubscription
decl_stmt|;
comment|// this subscription
specifier|protected
specifier|transient
name|List
argument_list|<
name|ConsumerId
argument_list|>
name|networkConsumerIds
decl_stmt|;
comment|// the original consumerId
comment|// not marshalled, populated from RemoveInfo, the last message delivered, used
comment|// to suppress redelivery on prefetched messages after close
specifier|private
specifier|transient
name|long
name|lastDeliveredSequenceId
decl_stmt|;
comment|// originated from a
comment|// network connection
specifier|public
name|ConsumerInfo
parameter_list|()
block|{     }
specifier|public
name|ConsumerInfo
parameter_list|(
name|ConsumerId
name|consumerId
parameter_list|)
block|{
name|this
operator|.
name|consumerId
operator|=
name|consumerId
expr_stmt|;
block|}
specifier|public
name|ConsumerInfo
parameter_list|(
name|SessionInfo
name|sessionInfo
parameter_list|,
name|long
name|consumerId
parameter_list|)
block|{
name|this
operator|.
name|consumerId
operator|=
operator|new
name|ConsumerId
argument_list|(
name|sessionInfo
operator|.
name|getSessionId
argument_list|()
argument_list|,
name|consumerId
argument_list|)
expr_stmt|;
block|}
specifier|public
name|ConsumerInfo
name|copy
parameter_list|()
block|{
name|ConsumerInfo
name|info
init|=
operator|new
name|ConsumerInfo
argument_list|()
decl_stmt|;
name|copy
argument_list|(
name|info
argument_list|)
expr_stmt|;
return|return
name|info
return|;
block|}
specifier|public
name|void
name|copy
parameter_list|(
name|ConsumerInfo
name|info
parameter_list|)
block|{
name|super
operator|.
name|copy
argument_list|(
name|info
argument_list|)
expr_stmt|;
name|info
operator|.
name|consumerId
operator|=
name|consumerId
expr_stmt|;
name|info
operator|.
name|destination
operator|=
name|destination
expr_stmt|;
name|info
operator|.
name|prefetchSize
operator|=
name|prefetchSize
expr_stmt|;
name|info
operator|.
name|maximumPendingMessageLimit
operator|=
name|maximumPendingMessageLimit
expr_stmt|;
name|info
operator|.
name|browser
operator|=
name|browser
expr_stmt|;
name|info
operator|.
name|dispatchAsync
operator|=
name|dispatchAsync
expr_stmt|;
name|info
operator|.
name|selector
operator|=
name|selector
expr_stmt|;
name|info
operator|.
name|subscriptionName
operator|=
name|subscriptionName
expr_stmt|;
name|info
operator|.
name|noLocal
operator|=
name|noLocal
expr_stmt|;
name|info
operator|.
name|exclusive
operator|=
name|exclusive
expr_stmt|;
name|info
operator|.
name|retroactive
operator|=
name|retroactive
expr_stmt|;
name|info
operator|.
name|priority
operator|=
name|priority
expr_stmt|;
name|info
operator|.
name|brokerPath
operator|=
name|brokerPath
expr_stmt|;
name|info
operator|.
name|networkSubscription
operator|=
name|networkSubscription
expr_stmt|;
if|if
condition|(
name|networkConsumerIds
operator|!=
literal|null
condition|)
block|{
if|if
condition|(
name|info
operator|.
name|networkConsumerIds
operator|==
literal|null
condition|)
block|{
name|info
operator|.
name|networkConsumerIds
operator|=
operator|new
name|ArrayList
argument_list|<
name|ConsumerId
argument_list|>
argument_list|()
expr_stmt|;
block|}
name|info
operator|.
name|networkConsumerIds
operator|.
name|addAll
argument_list|(
name|networkConsumerIds
argument_list|)
expr_stmt|;
block|}
block|}
specifier|public
name|boolean
name|isDurable
parameter_list|()
block|{
return|return
name|subscriptionName
operator|!=
literal|null
return|;
block|}
specifier|public
name|byte
name|getDataStructureType
parameter_list|()
block|{
return|return
name|DATA_STRUCTURE_TYPE
return|;
block|}
comment|/**      * Is used to uniquely identify the consumer to the broker.      *       * @openwire:property version=1 cache=true      */
specifier|public
name|ConsumerId
name|getConsumerId
parameter_list|()
block|{
return|return
name|consumerId
return|;
block|}
specifier|public
name|void
name|setConsumerId
parameter_list|(
name|ConsumerId
name|consumerId
parameter_list|)
block|{
name|this
operator|.
name|consumerId
operator|=
name|consumerId
expr_stmt|;
block|}
comment|/**      * Is this consumer a queue browser?      *       * @openwire:property version=1      */
specifier|public
name|boolean
name|isBrowser
parameter_list|()
block|{
return|return
name|browser
return|;
block|}
specifier|public
name|void
name|setBrowser
parameter_list|(
name|boolean
name|browser
parameter_list|)
block|{
name|this
operator|.
name|browser
operator|=
name|browser
expr_stmt|;
block|}
comment|/**      * The destination that the consumer is interested in receiving messages      * from. This destination could be a composite destination.      *       * @openwire:property version=1 cache=true      */
specifier|public
name|ActiveMQDestination
name|getDestination
parameter_list|()
block|{
return|return
name|destination
return|;
block|}
specifier|public
name|void
name|setDestination
parameter_list|(
name|ActiveMQDestination
name|destination
parameter_list|)
block|{
name|this
operator|.
name|destination
operator|=
name|destination
expr_stmt|;
block|}
comment|/**      * How many messages a broker will send to the client without receiving an      * ack before he stops dispatching messages to the client.      *       * @openwire:property version=1      */
specifier|public
name|int
name|getPrefetchSize
parameter_list|()
block|{
return|return
name|prefetchSize
return|;
block|}
specifier|public
name|void
name|setPrefetchSize
parameter_list|(
name|int
name|prefetchSize
parameter_list|)
block|{
name|this
operator|.
name|prefetchSize
operator|=
name|prefetchSize
expr_stmt|;
name|this
operator|.
name|currentPrefetchSize
operator|=
name|prefetchSize
expr_stmt|;
block|}
comment|/**      * How many messages a broker will keep around, above the prefetch limit,      * for non-durable topics before starting to discard older messages.      *       * @openwire:property version=1      */
specifier|public
name|int
name|getMaximumPendingMessageLimit
parameter_list|()
block|{
return|return
name|maximumPendingMessageLimit
return|;
block|}
specifier|public
name|void
name|setMaximumPendingMessageLimit
parameter_list|(
name|int
name|maximumPendingMessageLimit
parameter_list|)
block|{
name|this
operator|.
name|maximumPendingMessageLimit
operator|=
name|maximumPendingMessageLimit
expr_stmt|;
block|}
comment|/**      * Should the broker dispatch a message to the consumer async? If he does it      * async, then he uses a more SEDA style of processing while if it is not      * done async, then he broker use a STP style of processing. STP is more      * appropriate in high bandwidth situations or when being used by and in vm      * transport.      *       * @openwire:property version=1      */
specifier|public
name|boolean
name|isDispatchAsync
parameter_list|()
block|{
return|return
name|dispatchAsync
return|;
block|}
specifier|public
name|void
name|setDispatchAsync
parameter_list|(
name|boolean
name|dispatchAsync
parameter_list|)
block|{
name|this
operator|.
name|dispatchAsync
operator|=
name|dispatchAsync
expr_stmt|;
block|}
comment|/**      * The JMS selector used to filter out messages that this consumer is      * interested in.      *       * @openwire:property version=1      */
specifier|public
name|String
name|getSelector
parameter_list|()
block|{
return|return
name|selector
return|;
block|}
specifier|public
name|void
name|setSelector
parameter_list|(
name|String
name|selector
parameter_list|)
block|{
name|this
operator|.
name|selector
operator|=
name|selector
expr_stmt|;
block|}
comment|/**      * Used to identify the name of a durable subscription.      *       * @openwire:property version=1      */
specifier|public
name|String
name|getSubscriptionName
parameter_list|()
block|{
return|return
name|subscriptionName
return|;
block|}
specifier|public
name|void
name|setSubscriptionName
parameter_list|(
name|String
name|durableSubscriptionId
parameter_list|)
block|{
name|this
operator|.
name|subscriptionName
operator|=
name|durableSubscriptionId
expr_stmt|;
block|}
comment|/**      * @deprecated      * @return      * @see getSubscriptionName      */
specifier|public
name|String
name|getSubcriptionName
parameter_list|()
block|{
return|return
name|subscriptionName
return|;
block|}
comment|/**      * @deprecated      * @see setSubscriptionName      * @param durableSubscriptionId      */
specifier|public
name|void
name|setSubcriptionName
parameter_list|(
name|String
name|durableSubscriptionId
parameter_list|)
block|{
name|this
operator|.
name|subscriptionName
operator|=
name|durableSubscriptionId
expr_stmt|;
block|}
comment|/**      * Set noLocal to true to avoid receiving messages that were published      * locally on the same connection.      *       * @openwire:property version=1      */
specifier|public
name|boolean
name|isNoLocal
parameter_list|()
block|{
return|return
name|noLocal
return|;
block|}
specifier|public
name|void
name|setNoLocal
parameter_list|(
name|boolean
name|noLocal
parameter_list|)
block|{
name|this
operator|.
name|noLocal
operator|=
name|noLocal
expr_stmt|;
block|}
comment|/**      * An exclusive consumer locks out other consumers from being able to      * receive messages from the destination. If there are multiple exclusive      * consumers for a destination, the first one created will be the exclusive      * consumer of the destination.      *       * @openwire:property version=1      */
specifier|public
name|boolean
name|isExclusive
parameter_list|()
block|{
return|return
name|exclusive
return|;
block|}
specifier|public
name|void
name|setExclusive
parameter_list|(
name|boolean
name|exclusive
parameter_list|)
block|{
name|this
operator|.
name|exclusive
operator|=
name|exclusive
expr_stmt|;
block|}
comment|/**      * A retroactive consumer only has meaning for Topics. It allows a consumer      * to retroactively see messages sent prior to the consumer being created.      * If the consumer is not durable, it will be delivered the last message      * published to the topic. If the consumer is durable then it will receive      * all persistent messages that are still stored in persistent storage for      * that topic.      *       * @openwire:property version=1      */
specifier|public
name|boolean
name|isRetroactive
parameter_list|()
block|{
return|return
name|retroactive
return|;
block|}
specifier|public
name|void
name|setRetroactive
parameter_list|(
name|boolean
name|retroactive
parameter_list|)
block|{
name|this
operator|.
name|retroactive
operator|=
name|retroactive
expr_stmt|;
block|}
specifier|public
name|RemoveInfo
name|createRemoveCommand
parameter_list|()
block|{
name|RemoveInfo
name|command
init|=
operator|new
name|RemoveInfo
argument_list|(
name|getConsumerId
argument_list|()
argument_list|)
decl_stmt|;
name|command
operator|.
name|setResponseRequired
argument_list|(
name|isResponseRequired
argument_list|()
argument_list|)
expr_stmt|;
return|return
name|command
return|;
block|}
comment|/**      * The broker will avoid dispatching to a lower priority consumer if there      * are other higher priority consumers available to dispatch to. This allows      * letting the broker to have an affinity to higher priority consumers.      * Default priority is 0.      *       * @openwire:property version=1      */
specifier|public
name|byte
name|getPriority
parameter_list|()
block|{
return|return
name|priority
return|;
block|}
specifier|public
name|void
name|setPriority
parameter_list|(
name|byte
name|priority
parameter_list|)
block|{
name|this
operator|.
name|priority
operator|=
name|priority
expr_stmt|;
block|}
comment|/**      * The route of brokers the command has moved through.      *       * @openwire:property version=1 cache=true      */
specifier|public
name|BrokerId
index|[]
name|getBrokerPath
parameter_list|()
block|{
return|return
name|brokerPath
return|;
block|}
specifier|public
name|void
name|setBrokerPath
parameter_list|(
name|BrokerId
index|[]
name|brokerPath
parameter_list|)
block|{
name|this
operator|.
name|brokerPath
operator|=
name|brokerPath
expr_stmt|;
block|}
comment|/**      * A transient additional predicate that can be used it inject additional      * predicates into the selector on the fly. Handy if if say a Security      * Broker interceptor wants to filter out messages based on security level      * of the consumer.      *       * @openwire:property version=1      */
specifier|public
name|BooleanExpression
name|getAdditionalPredicate
parameter_list|()
block|{
return|return
name|additionalPredicate
return|;
block|}
specifier|public
name|void
name|setAdditionalPredicate
parameter_list|(
name|BooleanExpression
name|additionalPredicate
parameter_list|)
block|{
name|this
operator|.
name|additionalPredicate
operator|=
name|additionalPredicate
expr_stmt|;
block|}
specifier|public
name|Response
name|visit
parameter_list|(
name|CommandVisitor
name|visitor
parameter_list|)
throws|throws
name|Exception
block|{
return|return
name|visitor
operator|.
name|processAddConsumer
argument_list|(
name|this
argument_list|)
return|;
block|}
comment|/**      * @openwire:property version=1      * @return Returns the networkSubscription.      */
specifier|public
name|boolean
name|isNetworkSubscription
parameter_list|()
block|{
return|return
name|networkSubscription
return|;
block|}
comment|/**      * @param networkSubscription The networkSubscription to set.      */
specifier|public
name|void
name|setNetworkSubscription
parameter_list|(
name|boolean
name|networkSubscription
parameter_list|)
block|{
name|this
operator|.
name|networkSubscription
operator|=
name|networkSubscription
expr_stmt|;
block|}
comment|/**      * @openwire:property version=1      * @return Returns the optimizedAcknowledge.      */
specifier|public
name|boolean
name|isOptimizedAcknowledge
parameter_list|()
block|{
return|return
name|optimizedAcknowledge
return|;
block|}
comment|/**      * @param optimizedAcknowledge The optimizedAcknowledge to set.      */
specifier|public
name|void
name|setOptimizedAcknowledge
parameter_list|(
name|boolean
name|optimizedAcknowledge
parameter_list|)
block|{
name|this
operator|.
name|optimizedAcknowledge
operator|=
name|optimizedAcknowledge
expr_stmt|;
block|}
comment|/**      * @return Returns the currentPrefetchSize.      */
specifier|public
name|int
name|getCurrentPrefetchSize
parameter_list|()
block|{
return|return
name|currentPrefetchSize
return|;
block|}
comment|/**      * @param currentPrefetchSize The currentPrefetchSize to set.      */
specifier|public
name|void
name|setCurrentPrefetchSize
parameter_list|(
name|int
name|currentPrefetchSize
parameter_list|)
block|{
name|this
operator|.
name|currentPrefetchSize
operator|=
name|currentPrefetchSize
expr_stmt|;
block|}
comment|/**      * The broker may be able to optimize it's processing or provides better QOS      * if it knows the consumer will not be sending ranged acks.      *       * @return true if the consumer will not send range acks.      * @openwire:property version=1      */
specifier|public
name|boolean
name|isNoRangeAcks
parameter_list|()
block|{
return|return
name|noRangeAcks
return|;
block|}
specifier|public
name|void
name|setNoRangeAcks
parameter_list|(
name|boolean
name|noRangeAcks
parameter_list|)
block|{
name|this
operator|.
name|noRangeAcks
operator|=
name|noRangeAcks
expr_stmt|;
block|}
specifier|public
specifier|synchronized
name|void
name|addNetworkConsumerId
parameter_list|(
name|ConsumerId
name|networkConsumerId
parameter_list|)
block|{
if|if
condition|(
name|networkConsumerIds
operator|==
literal|null
condition|)
block|{
name|networkConsumerIds
operator|=
operator|new
name|ArrayList
argument_list|<
name|ConsumerId
argument_list|>
argument_list|()
expr_stmt|;
block|}
name|networkConsumerIds
operator|.
name|add
argument_list|(
name|networkConsumerId
argument_list|)
expr_stmt|;
block|}
specifier|public
specifier|synchronized
name|void
name|removeNetworkConsumerId
parameter_list|(
name|ConsumerId
name|networkConsumerId
parameter_list|)
block|{
if|if
condition|(
name|networkConsumerIds
operator|!=
literal|null
condition|)
block|{
name|networkConsumerIds
operator|.
name|remove
argument_list|(
name|networkConsumerId
argument_list|)
expr_stmt|;
if|if
condition|(
name|networkConsumerIds
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
name|networkConsumerIds
operator|=
literal|null
expr_stmt|;
block|}
block|}
block|}
specifier|public
specifier|synchronized
name|boolean
name|isNetworkConsumersEmpty
parameter_list|()
block|{
return|return
name|networkConsumerIds
operator|==
literal|null
operator|||
name|networkConsumerIds
operator|.
name|isEmpty
argument_list|()
return|;
block|}
specifier|public
specifier|synchronized
name|List
argument_list|<
name|ConsumerId
argument_list|>
name|getNetworkConsumerIds
parameter_list|()
block|{
name|List
argument_list|<
name|ConsumerId
argument_list|>
name|result
init|=
operator|new
name|ArrayList
argument_list|<
name|ConsumerId
argument_list|>
argument_list|()
decl_stmt|;
if|if
condition|(
name|networkConsumerIds
operator|!=
literal|null
condition|)
block|{
name|result
operator|.
name|addAll
argument_list|(
name|networkConsumerIds
argument_list|)
expr_stmt|;
block|}
return|return
name|result
return|;
block|}
comment|/**      * Tracks the original subscription id that causes a subscription to       * percolate through a network when networkTTL> 1. Tracking the original      * subscription allows duplicate suppression.      *       * @return array of the current subscription path      * @openwire:property version=4      */
specifier|public
name|ConsumerId
index|[]
name|getNetworkConsumerPath
parameter_list|()
block|{
name|ConsumerId
index|[]
name|result
init|=
literal|null
decl_stmt|;
if|if
condition|(
name|networkConsumerIds
operator|!=
literal|null
condition|)
block|{
name|result
operator|=
name|networkConsumerIds
operator|.
name|toArray
argument_list|(
operator|new
name|ConsumerId
index|[
literal|0
index|]
argument_list|)
expr_stmt|;
block|}
return|return
name|result
return|;
block|}
specifier|public
name|void
name|setNetworkConsumerPath
parameter_list|(
name|ConsumerId
index|[]
name|consumerPath
parameter_list|)
block|{
if|if
condition|(
name|consumerPath
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
name|consumerPath
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|addNetworkConsumerId
argument_list|(
name|consumerPath
index|[
name|i
index|]
argument_list|)
expr_stmt|;
block|}
block|}
block|}
specifier|public
name|void
name|setLastDeliveredSequenceId
parameter_list|(
name|long
name|lastDeliveredSequenceId
parameter_list|)
block|{
name|this
operator|.
name|lastDeliveredSequenceId
operator|=
name|lastDeliveredSequenceId
expr_stmt|;
block|}
specifier|public
name|long
name|getLastDeliveredSequenceId
parameter_list|()
block|{
return|return
name|lastDeliveredSequenceId
return|;
block|}
block|}
end_class

end_unit

