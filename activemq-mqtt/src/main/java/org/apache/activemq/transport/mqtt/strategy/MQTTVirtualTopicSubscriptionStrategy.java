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
name|transport
operator|.
name|mqtt
operator|.
name|strategy
package|;
end_package

begin_import
import|import static
name|org
operator|.
name|apache
operator|.
name|activemq
operator|.
name|transport
operator|.
name|mqtt
operator|.
name|MQTTProtocolSupport
operator|.
name|convertActiveMQToMQTT
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|apache
operator|.
name|activemq
operator|.
name|transport
operator|.
name|mqtt
operator|.
name|MQTTProtocolSupport
operator|.
name|convertMQTTToActiveMQ
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
name|HashSet
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
name|Set
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|StringTokenizer
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
name|ActiveMQPrefetchPolicy
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
name|QueueRegion
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
name|RegionBroker
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
name|command
operator|.
name|DestinationInfo
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
name|transport
operator|.
name|mqtt
operator|.
name|MQTTProtocolConverter
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
name|transport
operator|.
name|mqtt
operator|.
name|MQTTProtocolException
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
name|transport
operator|.
name|mqtt
operator|.
name|MQTTProtocolSupport
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
name|transport
operator|.
name|mqtt
operator|.
name|MQTTSubscription
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
name|transport
operator|.
name|mqtt
operator|.
name|ResponseHandler
import|;
end_import

begin_import
import|import
name|org
operator|.
name|fusesource
operator|.
name|mqtt
operator|.
name|client
operator|.
name|QoS
import|;
end_import

begin_import
import|import
name|org
operator|.
name|fusesource
operator|.
name|mqtt
operator|.
name|codec
operator|.
name|CONNECT
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
comment|/**  * Subscription strategy that converts all MQTT subscribes that would be durable to  * Virtual Topic Queue subscriptions.  Also maps all publish requests to be prefixed  * with the VirtualTopic. prefix unless already present.  */
end_comment

begin_class
specifier|public
class|class
name|MQTTVirtualTopicSubscriptionStrategy
extends|extends
name|AbstractMQTTSubscriptionStrategy
block|{
specifier|private
specifier|static
specifier|final
name|String
name|VIRTUALTOPIC_PREFIX
init|=
literal|"VirtualTopic."
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|String
name|VIRTUALTOPIC_CONSUMER_PREFIX
init|=
literal|"Consumer."
decl_stmt|;
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
name|MQTTVirtualTopicSubscriptionStrategy
operator|.
name|class
argument_list|)
decl_stmt|;
specifier|private
specifier|final
name|Set
argument_list|<
name|ActiveMQQueue
argument_list|>
name|restoredQueues
init|=
name|Collections
operator|.
name|synchronizedSet
argument_list|(
operator|new
name|HashSet
argument_list|<
name|ActiveMQQueue
argument_list|>
argument_list|()
argument_list|)
decl_stmt|;
annotation|@
name|Override
specifier|public
name|void
name|onConnect
parameter_list|(
name|CONNECT
name|connect
parameter_list|)
throws|throws
name|MQTTProtocolException
block|{
name|List
argument_list|<
name|ActiveMQQueue
argument_list|>
name|queues
init|=
name|lookupQueues
argument_list|(
name|protocol
operator|.
name|getClientId
argument_list|()
argument_list|)
decl_stmt|;
name|List
argument_list|<
name|SubscriptionInfo
argument_list|>
name|subs
init|=
name|lookupSubscription
argument_list|(
name|protocol
operator|.
name|getClientId
argument_list|()
argument_list|)
decl_stmt|;
comment|// When clean session is true we must purge all of the client's old Queue subscriptions
comment|// and any durable subscriptions created on the VirtualTopic instance as well.
if|if
condition|(
name|connect
operator|.
name|cleanSession
argument_list|()
condition|)
block|{
name|deleteDurableQueues
argument_list|(
name|queues
argument_list|)
expr_stmt|;
name|deleteDurableSubs
argument_list|(
name|subs
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|restoreDurableQueue
argument_list|(
name|queues
argument_list|)
expr_stmt|;
name|restoreDurableSubs
argument_list|(
name|subs
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Override
specifier|public
name|byte
name|onSubscribe
parameter_list|(
name|String
name|topicName
parameter_list|,
name|QoS
name|requestedQoS
parameter_list|)
throws|throws
name|MQTTProtocolException
block|{
name|ActiveMQDestination
name|destination
init|=
literal|null
decl_stmt|;
name|int
name|prefetch
init|=
name|ActiveMQPrefetchPolicy
operator|.
name|DEFAULT_QUEUE_PREFETCH
decl_stmt|;
name|ConsumerInfo
name|consumerInfo
init|=
operator|new
name|ConsumerInfo
argument_list|(
name|getNextConsumerId
argument_list|()
argument_list|)
decl_stmt|;
name|String
name|converted
init|=
name|convertMQTTToActiveMQ
argument_list|(
name|topicName
argument_list|)
decl_stmt|;
if|if
condition|(
operator|!
name|protocol
operator|.
name|isCleanSession
argument_list|()
operator|&&
name|protocol
operator|.
name|getClientId
argument_list|()
operator|!=
literal|null
operator|&&
name|requestedQoS
operator|.
name|ordinal
argument_list|()
operator|>=
name|QoS
operator|.
name|AT_LEAST_ONCE
operator|.
name|ordinal
argument_list|()
condition|)
block|{
if|if
condition|(
name|converted
operator|.
name|startsWith
argument_list|(
name|VIRTUALTOPIC_PREFIX
argument_list|)
condition|)
block|{
name|destination
operator|=
operator|new
name|ActiveMQTopic
argument_list|(
name|converted
argument_list|)
expr_stmt|;
name|prefetch
operator|=
name|ActiveMQPrefetchPolicy
operator|.
name|DEFAULT_DURABLE_TOPIC_PREFETCH
expr_stmt|;
name|consumerInfo
operator|.
name|setSubscriptionName
argument_list|(
name|requestedQoS
operator|+
literal|":"
operator|+
name|topicName
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|converted
operator|=
name|VIRTUALTOPIC_CONSUMER_PREFIX
operator|+
name|convertMQTTToActiveMQ
argument_list|(
name|protocol
operator|.
name|getClientId
argument_list|()
argument_list|)
operator|+
literal|":"
operator|+
name|requestedQoS
operator|+
literal|"."
operator|+
name|VIRTUALTOPIC_PREFIX
operator|+
name|converted
expr_stmt|;
name|destination
operator|=
operator|new
name|ActiveMQQueue
argument_list|(
name|converted
argument_list|)
expr_stmt|;
name|prefetch
operator|=
name|ActiveMQPrefetchPolicy
operator|.
name|DEFAULT_QUEUE_PREFETCH
expr_stmt|;
block|}
block|}
else|else
block|{
if|if
condition|(
operator|!
name|converted
operator|.
name|startsWith
argument_list|(
name|VIRTUALTOPIC_PREFIX
argument_list|)
condition|)
block|{
name|converted
operator|=
name|VIRTUALTOPIC_PREFIX
operator|+
name|converted
expr_stmt|;
block|}
name|destination
operator|=
operator|new
name|ActiveMQTopic
argument_list|(
name|converted
argument_list|)
expr_stmt|;
name|prefetch
operator|=
name|ActiveMQPrefetchPolicy
operator|.
name|DEFAULT_TOPIC_PREFETCH
expr_stmt|;
block|}
name|consumerInfo
operator|.
name|setDestination
argument_list|(
name|destination
argument_list|)
expr_stmt|;
if|if
condition|(
name|protocol
operator|.
name|getActiveMQSubscriptionPrefetch
argument_list|()
operator|>
literal|0
condition|)
block|{
name|consumerInfo
operator|.
name|setPrefetchSize
argument_list|(
name|protocol
operator|.
name|getActiveMQSubscriptionPrefetch
argument_list|()
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|consumerInfo
operator|.
name|setPrefetchSize
argument_list|(
name|prefetch
argument_list|)
expr_stmt|;
block|}
name|consumerInfo
operator|.
name|setRetroactive
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|consumerInfo
operator|.
name|setDispatchAsync
argument_list|(
literal|true
argument_list|)
expr_stmt|;
return|return
name|doSubscribe
argument_list|(
name|consumerInfo
argument_list|,
name|topicName
argument_list|,
name|requestedQoS
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|onReSubscribe
parameter_list|(
name|MQTTSubscription
name|mqttSubscription
parameter_list|)
throws|throws
name|MQTTProtocolException
block|{
name|ActiveMQDestination
name|destination
init|=
name|mqttSubscription
operator|.
name|getDestination
argument_list|()
decl_stmt|;
comment|// check whether the Queue has been recovered in restoreDurableQueue
comment|// mark subscription available for recovery for duplicate subscription
if|if
condition|(
name|destination
operator|.
name|isQueue
argument_list|()
operator|&&
name|restoredQueues
operator|.
name|remove
argument_list|(
name|destination
argument_list|)
condition|)
block|{
return|return;
block|}
comment|// check whether the Topic has been recovered in restoreDurableSubs
comment|// mark subscription available for recovery for duplicate subscription
if|if
condition|(
name|destination
operator|.
name|isTopic
argument_list|()
operator|&&
name|restoredDurableSubs
operator|.
name|remove
argument_list|(
name|destination
operator|.
name|getPhysicalName
argument_list|()
argument_list|)
condition|)
block|{
return|return;
block|}
if|if
condition|(
name|mqttSubscription
operator|.
name|getDestination
argument_list|()
operator|.
name|isTopic
argument_list|()
condition|)
block|{
name|super
operator|.
name|onReSubscribe
argument_list|(
name|mqttSubscription
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|doUnSubscribe
argument_list|(
name|mqttSubscription
argument_list|)
expr_stmt|;
name|ConsumerInfo
name|consumerInfo
init|=
name|mqttSubscription
operator|.
name|getConsumerInfo
argument_list|()
decl_stmt|;
name|consumerInfo
operator|.
name|setConsumerId
argument_list|(
name|getNextConsumerId
argument_list|()
argument_list|)
expr_stmt|;
name|doSubscribe
argument_list|(
name|consumerInfo
argument_list|,
name|mqttSubscription
operator|.
name|getTopicName
argument_list|()
argument_list|,
name|mqttSubscription
operator|.
name|getQoS
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Override
specifier|public
name|void
name|onUnSubscribe
parameter_list|(
name|String
name|topicName
parameter_list|)
throws|throws
name|MQTTProtocolException
block|{
name|MQTTSubscription
name|subscription
init|=
name|mqttSubscriptionByTopic
operator|.
name|remove
argument_list|(
name|topicName
argument_list|)
decl_stmt|;
if|if
condition|(
name|subscription
operator|!=
literal|null
condition|)
block|{
name|doUnSubscribe
argument_list|(
name|subscription
argument_list|)
expr_stmt|;
if|if
condition|(
name|subscription
operator|.
name|getDestination
argument_list|()
operator|.
name|isQueue
argument_list|()
condition|)
block|{
name|DestinationInfo
name|remove
init|=
operator|new
name|DestinationInfo
argument_list|()
decl_stmt|;
name|remove
operator|.
name|setConnectionId
argument_list|(
name|protocol
operator|.
name|getConnectionId
argument_list|()
argument_list|)
expr_stmt|;
name|remove
operator|.
name|setDestination
argument_list|(
name|subscription
operator|.
name|getDestination
argument_list|()
argument_list|)
expr_stmt|;
name|remove
operator|.
name|setOperationType
argument_list|(
name|DestinationInfo
operator|.
name|REMOVE_OPERATION_TYPE
argument_list|)
expr_stmt|;
name|protocol
operator|.
name|sendToActiveMQ
argument_list|(
name|remove
argument_list|,
operator|new
name|ResponseHandler
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|void
name|onResponse
parameter_list|(
name|MQTTProtocolConverter
name|converter
parameter_list|,
name|Response
name|response
parameter_list|)
throws|throws
name|IOException
block|{
comment|// ignore failures..
block|}
block|}
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|subscription
operator|.
name|getConsumerInfo
argument_list|()
operator|.
name|getSubscriptionName
argument_list|()
operator|!=
literal|null
condition|)
block|{
comment|// also remove it from restored durable subscriptions set
name|restoredDurableSubs
operator|.
name|remove
argument_list|(
name|MQTTProtocolSupport
operator|.
name|convertMQTTToActiveMQ
argument_list|(
name|subscription
operator|.
name|getTopicName
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|RemoveSubscriptionInfo
name|rsi
init|=
operator|new
name|RemoveSubscriptionInfo
argument_list|()
decl_stmt|;
name|rsi
operator|.
name|setConnectionId
argument_list|(
name|protocol
operator|.
name|getConnectionId
argument_list|()
argument_list|)
expr_stmt|;
name|rsi
operator|.
name|setSubscriptionName
argument_list|(
name|subscription
operator|.
name|getConsumerInfo
argument_list|()
operator|.
name|getSubscriptionName
argument_list|()
argument_list|)
expr_stmt|;
name|rsi
operator|.
name|setClientId
argument_list|(
name|protocol
operator|.
name|getClientId
argument_list|()
argument_list|)
expr_stmt|;
name|protocol
operator|.
name|sendToActiveMQ
argument_list|(
name|rsi
argument_list|,
operator|new
name|ResponseHandler
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|void
name|onResponse
parameter_list|(
name|MQTTProtocolConverter
name|converter
parameter_list|,
name|Response
name|response
parameter_list|)
throws|throws
name|IOException
block|{
comment|// ignore failures..
block|}
block|}
argument_list|)
expr_stmt|;
block|}
block|}
block|}
annotation|@
name|Override
specifier|public
name|ActiveMQDestination
name|onSend
parameter_list|(
name|String
name|topicName
parameter_list|)
block|{
name|ActiveMQTopic
name|topic
init|=
operator|new
name|ActiveMQTopic
argument_list|(
name|topicName
argument_list|)
decl_stmt|;
if|if
condition|(
name|topic
operator|.
name|isComposite
argument_list|()
condition|)
block|{
name|ActiveMQDestination
index|[]
name|composites
init|=
name|topic
operator|.
name|getCompositeDestinations
argument_list|()
decl_stmt|;
for|for
control|(
name|ActiveMQDestination
name|composite
range|:
name|composites
control|)
block|{
name|composite
operator|.
name|setPhysicalName
argument_list|(
name|prefix
argument_list|(
name|composite
operator|.
name|getPhysicalName
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|ActiveMQTopic
name|result
init|=
operator|new
name|ActiveMQTopic
argument_list|()
decl_stmt|;
name|result
operator|.
name|setCompositeDestinations
argument_list|(
name|composites
argument_list|)
expr_stmt|;
return|return
name|result
return|;
block|}
else|else
block|{
return|return
operator|new
name|ActiveMQTopic
argument_list|(
name|prefix
argument_list|(
name|topicName
argument_list|)
argument_list|)
return|;
block|}
block|}
specifier|private
name|String
name|prefix
parameter_list|(
name|String
name|topicName
parameter_list|)
block|{
if|if
condition|(
operator|!
name|topicName
operator|.
name|startsWith
argument_list|(
name|VIRTUALTOPIC_PREFIX
argument_list|)
condition|)
block|{
return|return
name|VIRTUALTOPIC_PREFIX
operator|+
name|topicName
return|;
block|}
else|else
block|{
return|return
name|topicName
return|;
block|}
block|}
annotation|@
name|Override
specifier|public
name|String
name|onSend
parameter_list|(
name|ActiveMQDestination
name|destination
parameter_list|)
block|{
name|String
name|destinationName
init|=
name|destination
operator|.
name|getPhysicalName
argument_list|()
decl_stmt|;
name|int
name|position
init|=
name|destinationName
operator|.
name|indexOf
argument_list|(
name|VIRTUALTOPIC_PREFIX
argument_list|)
decl_stmt|;
if|if
condition|(
name|position
operator|>=
literal|0
condition|)
block|{
name|destinationName
operator|=
name|destinationName
operator|.
name|substring
argument_list|(
name|position
operator|+
name|VIRTUALTOPIC_PREFIX
operator|.
name|length
argument_list|()
argument_list|)
operator|.
name|substring
argument_list|(
literal|0
argument_list|)
expr_stmt|;
block|}
return|return
name|destinationName
return|;
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|isControlTopic
parameter_list|(
name|ActiveMQDestination
name|destination
parameter_list|)
block|{
name|String
name|destinationName
init|=
name|destination
operator|.
name|getPhysicalName
argument_list|()
decl_stmt|;
if|if
condition|(
name|destinationName
operator|.
name|startsWith
argument_list|(
literal|"$"
argument_list|)
operator|||
name|destinationName
operator|.
name|startsWith
argument_list|(
name|VIRTUALTOPIC_PREFIX
operator|+
literal|"$"
argument_list|)
condition|)
block|{
return|return
literal|true
return|;
block|}
return|return
literal|false
return|;
block|}
specifier|private
name|void
name|deleteDurableQueues
parameter_list|(
name|List
argument_list|<
name|ActiveMQQueue
argument_list|>
name|queues
parameter_list|)
block|{
try|try
block|{
for|for
control|(
name|ActiveMQQueue
name|queue
range|:
name|queues
control|)
block|{
name|LOG
operator|.
name|debug
argument_list|(
literal|"Removing queue subscription for {} "
argument_list|,
name|queue
operator|.
name|getPhysicalName
argument_list|()
argument_list|)
expr_stmt|;
name|DestinationInfo
name|removeAction
init|=
operator|new
name|DestinationInfo
argument_list|()
decl_stmt|;
name|removeAction
operator|.
name|setConnectionId
argument_list|(
name|protocol
operator|.
name|getConnectionId
argument_list|()
argument_list|)
expr_stmt|;
name|removeAction
operator|.
name|setDestination
argument_list|(
name|queue
argument_list|)
expr_stmt|;
name|removeAction
operator|.
name|setOperationType
argument_list|(
name|DestinationInfo
operator|.
name|REMOVE_OPERATION_TYPE
argument_list|)
expr_stmt|;
name|protocol
operator|.
name|sendToActiveMQ
argument_list|(
name|removeAction
argument_list|,
operator|new
name|ResponseHandler
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|void
name|onResponse
parameter_list|(
name|MQTTProtocolConverter
name|converter
parameter_list|,
name|Response
name|response
parameter_list|)
throws|throws
name|IOException
block|{
comment|// ignore failures..
block|}
block|}
argument_list|)
expr_stmt|;
block|}
block|}
catch|catch
parameter_list|(
name|Throwable
name|e
parameter_list|)
block|{
name|LOG
operator|.
name|warn
argument_list|(
literal|"Could not delete the MQTT queue subscriptions."
argument_list|,
name|e
argument_list|)
expr_stmt|;
block|}
block|}
specifier|private
name|void
name|restoreDurableQueue
parameter_list|(
name|List
argument_list|<
name|ActiveMQQueue
argument_list|>
name|queues
parameter_list|)
block|{
try|try
block|{
for|for
control|(
name|ActiveMQQueue
name|queue
range|:
name|queues
control|)
block|{
name|String
name|name
init|=
name|queue
operator|.
name|getPhysicalName
argument_list|()
operator|.
name|substring
argument_list|(
name|VIRTUALTOPIC_CONSUMER_PREFIX
operator|.
name|length
argument_list|()
argument_list|)
decl_stmt|;
name|StringTokenizer
name|tokenizer
init|=
operator|new
name|StringTokenizer
argument_list|(
name|name
argument_list|)
decl_stmt|;
name|tokenizer
operator|.
name|nextToken
argument_list|(
literal|":."
argument_list|)
expr_stmt|;
name|String
name|qosString
init|=
name|tokenizer
operator|.
name|nextToken
argument_list|()
decl_stmt|;
name|tokenizer
operator|.
name|nextToken
argument_list|()
expr_stmt|;
name|String
name|topicName
init|=
name|convertActiveMQToMQTT
argument_list|(
name|tokenizer
operator|.
name|nextToken
argument_list|(
literal|""
argument_list|)
operator|.
name|substring
argument_list|(
literal|1
argument_list|)
argument_list|)
decl_stmt|;
name|QoS
name|qoS
init|=
name|QoS
operator|.
name|valueOf
argument_list|(
name|qosString
argument_list|)
decl_stmt|;
name|LOG
operator|.
name|trace
argument_list|(
literal|"Restoring queue subscription: {}:{}"
argument_list|,
name|topicName
argument_list|,
name|qoS
argument_list|)
expr_stmt|;
name|ConsumerInfo
name|consumerInfo
init|=
operator|new
name|ConsumerInfo
argument_list|(
name|getNextConsumerId
argument_list|()
argument_list|)
decl_stmt|;
name|consumerInfo
operator|.
name|setDestination
argument_list|(
name|queue
argument_list|)
expr_stmt|;
name|consumerInfo
operator|.
name|setPrefetchSize
argument_list|(
name|ActiveMQPrefetchPolicy
operator|.
name|DEFAULT_QUEUE_PREFETCH
argument_list|)
expr_stmt|;
if|if
condition|(
name|protocol
operator|.
name|getActiveMQSubscriptionPrefetch
argument_list|()
operator|>
literal|0
condition|)
block|{
name|consumerInfo
operator|.
name|setPrefetchSize
argument_list|(
name|protocol
operator|.
name|getActiveMQSubscriptionPrefetch
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|consumerInfo
operator|.
name|setRetroactive
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|consumerInfo
operator|.
name|setDispatchAsync
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|doSubscribe
argument_list|(
name|consumerInfo
argument_list|,
name|topicName
argument_list|,
name|qoS
argument_list|)
expr_stmt|;
comment|// mark this durable subscription as restored by Broker
name|restoredQueues
operator|.
name|add
argument_list|(
name|queue
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
name|LOG
operator|.
name|warn
argument_list|(
literal|"Could not restore the MQTT queue subscriptions."
argument_list|,
name|e
argument_list|)
expr_stmt|;
block|}
block|}
name|List
argument_list|<
name|ActiveMQQueue
argument_list|>
name|lookupQueues
parameter_list|(
name|String
name|clientId
parameter_list|)
throws|throws
name|MQTTProtocolException
block|{
name|List
argument_list|<
name|ActiveMQQueue
argument_list|>
name|result
init|=
operator|new
name|ArrayList
argument_list|<
name|ActiveMQQueue
argument_list|>
argument_list|()
decl_stmt|;
name|RegionBroker
name|regionBroker
decl_stmt|;
try|try
block|{
name|regionBroker
operator|=
operator|(
name|RegionBroker
operator|)
name|brokerService
operator|.
name|getBroker
argument_list|()
operator|.
name|getAdaptor
argument_list|(
name|RegionBroker
operator|.
name|class
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|MQTTProtocolException
argument_list|(
literal|"Error recovering queues: "
operator|+
name|e
operator|.
name|getMessage
argument_list|()
argument_list|,
literal|false
argument_list|,
name|e
argument_list|)
throw|;
block|}
specifier|final
name|QueueRegion
name|queueRegion
init|=
operator|(
name|QueueRegion
operator|)
name|regionBroker
operator|.
name|getQueueRegion
argument_list|()
decl_stmt|;
for|for
control|(
name|ActiveMQDestination
name|destination
range|:
name|queueRegion
operator|.
name|getDestinationMap
argument_list|()
operator|.
name|keySet
argument_list|()
control|)
block|{
if|if
condition|(
name|destination
operator|.
name|isQueue
argument_list|()
operator|&&
operator|!
name|destination
operator|.
name|isTemporary
argument_list|()
condition|)
block|{
if|if
condition|(
name|destination
operator|.
name|getPhysicalName
argument_list|()
operator|.
name|startsWith
argument_list|(
literal|"Consumer."
operator|+
name|clientId
operator|+
literal|":"
argument_list|)
condition|)
block|{
name|LOG
operator|.
name|debug
argument_list|(
literal|"Recovered client sub: {} on connect"
argument_list|,
name|destination
operator|.
name|getPhysicalName
argument_list|()
argument_list|)
expr_stmt|;
name|result
operator|.
name|add
argument_list|(
operator|(
name|ActiveMQQueue
operator|)
name|destination
argument_list|)
expr_stmt|;
block|}
block|}
block|}
return|return
name|result
return|;
block|}
block|}
end_class

end_unit

