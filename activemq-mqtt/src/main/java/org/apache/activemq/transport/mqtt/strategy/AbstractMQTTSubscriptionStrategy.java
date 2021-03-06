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
name|ConcurrentMap
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
name|BrokerServiceAware
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
name|DurableTopicSubscription
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
name|PrefetchSubscription
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
name|broker
operator|.
name|region
operator|.
name|TopicRegion
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
name|virtual
operator|.
name|VirtualTopicInterceptor
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
name|ExceptionResponse
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
name|RemoveInfo
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
name|client
operator|.
name|Topic
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
comment|/**  * Abstract implementation of the {@link MQTTSubscriptionStrategy} interface providing  * the base functionality that is common to most implementations.  */
end_comment

begin_class
specifier|public
specifier|abstract
class|class
name|AbstractMQTTSubscriptionStrategy
implements|implements
name|MQTTSubscriptionStrategy
implements|,
name|BrokerServiceAware
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
name|AbstractMQTTSubscriptionStrategy
operator|.
name|class
argument_list|)
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|byte
name|SUBSCRIBE_ERROR
init|=
operator|(
name|byte
operator|)
literal|0x80
decl_stmt|;
specifier|protected
name|MQTTProtocolConverter
name|protocol
decl_stmt|;
specifier|protected
name|BrokerService
name|brokerService
decl_stmt|;
specifier|protected
specifier|final
name|ConcurrentMap
argument_list|<
name|ConsumerId
argument_list|,
name|MQTTSubscription
argument_list|>
name|subscriptionsByConsumerId
init|=
operator|new
name|ConcurrentHashMap
argument_list|<
name|ConsumerId
argument_list|,
name|MQTTSubscription
argument_list|>
argument_list|()
decl_stmt|;
specifier|protected
specifier|final
name|ConcurrentMap
argument_list|<
name|String
argument_list|,
name|MQTTSubscription
argument_list|>
name|mqttSubscriptionByTopic
init|=
operator|new
name|ConcurrentHashMap
argument_list|<
name|String
argument_list|,
name|MQTTSubscription
argument_list|>
argument_list|()
decl_stmt|;
specifier|protected
specifier|final
name|Set
argument_list|<
name|String
argument_list|>
name|restoredDurableSubs
init|=
name|Collections
operator|.
name|synchronizedSet
argument_list|(
operator|new
name|HashSet
argument_list|<
name|String
argument_list|>
argument_list|()
argument_list|)
decl_stmt|;
specifier|protected
specifier|final
name|LongSequenceGenerator
name|consumerIdGenerator
init|=
operator|new
name|LongSequenceGenerator
argument_list|()
decl_stmt|;
annotation|@
name|Override
specifier|public
name|void
name|initialize
parameter_list|(
name|MQTTProtocolConverter
name|protocol
parameter_list|)
throws|throws
name|MQTTProtocolException
block|{
name|setProtocolConverter
argument_list|(
name|protocol
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|setBrokerService
parameter_list|(
name|BrokerService
name|brokerService
parameter_list|)
block|{
name|this
operator|.
name|brokerService
operator|=
name|brokerService
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|setProtocolConverter
parameter_list|(
name|MQTTProtocolConverter
name|parent
parameter_list|)
block|{
name|this
operator|.
name|protocol
operator|=
name|parent
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|MQTTProtocolConverter
name|getProtocolConverter
parameter_list|()
block|{
return|return
name|protocol
return|;
block|}
annotation|@
name|Override
specifier|public
name|byte
name|onSubscribe
parameter_list|(
specifier|final
name|Topic
name|topic
parameter_list|)
throws|throws
name|MQTTProtocolException
block|{
specifier|final
name|String
name|destinationName
init|=
name|topic
operator|.
name|name
argument_list|()
operator|.
name|toString
argument_list|()
decl_stmt|;
specifier|final
name|QoS
name|requestedQoS
init|=
name|topic
operator|.
name|qos
argument_list|()
decl_stmt|;
specifier|final
name|MQTTSubscription
name|mqttSubscription
init|=
name|mqttSubscriptionByTopic
operator|.
name|get
argument_list|(
name|destinationName
argument_list|)
decl_stmt|;
if|if
condition|(
name|mqttSubscription
operator|!=
literal|null
condition|)
block|{
if|if
condition|(
name|requestedQoS
operator|!=
name|mqttSubscription
operator|.
name|getQoS
argument_list|()
condition|)
block|{
comment|// remove old subscription as the QoS has changed
name|onUnSubscribe
argument_list|(
name|destinationName
argument_list|)
expr_stmt|;
block|}
else|else
block|{
try|try
block|{
name|onReSubscribe
argument_list|(
name|mqttSubscription
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|MQTTProtocolException
argument_list|(
literal|"Failed to find subscription strategy"
argument_list|,
literal|true
argument_list|,
name|e
argument_list|)
throw|;
block|}
return|return
operator|(
name|byte
operator|)
name|requestedQoS
operator|.
name|ordinal
argument_list|()
return|;
block|}
block|}
try|try
block|{
return|return
name|onSubscribe
argument_list|(
name|destinationName
argument_list|,
name|requestedQoS
argument_list|)
return|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|MQTTProtocolException
argument_list|(
literal|"Failed while intercepting subscribe"
argument_list|,
literal|true
argument_list|,
name|e
argument_list|)
throw|;
block|}
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
name|String
name|topicName
init|=
name|mqttSubscription
operator|.
name|getTopicName
argument_list|()
decl_stmt|;
comment|// get TopicRegion
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
literal|"Error subscribing to "
operator|+
name|topicName
operator|+
literal|": "
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
name|TopicRegion
name|topicRegion
init|=
operator|(
name|TopicRegion
operator|)
name|regionBroker
operator|.
name|getTopicRegion
argument_list|()
decl_stmt|;
specifier|final
name|ConsumerInfo
name|consumerInfo
init|=
name|mqttSubscription
operator|.
name|getConsumerInfo
argument_list|()
decl_stmt|;
specifier|final
name|ConsumerId
name|consumerId
init|=
name|consumerInfo
operator|.
name|getConsumerId
argument_list|()
decl_stmt|;
comment|// use actual client id used to create connection to lookup connection
comment|// context
name|String
name|connectionInfoClientId
init|=
name|protocol
operator|.
name|getClientId
argument_list|()
decl_stmt|;
comment|// for zero-byte client ids we used connection id
if|if
condition|(
name|connectionInfoClientId
operator|==
literal|null
operator|||
name|connectionInfoClientId
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
name|connectionInfoClientId
operator|=
name|protocol
operator|.
name|getConnectionId
argument_list|()
operator|.
name|toString
argument_list|()
expr_stmt|;
block|}
specifier|final
name|ConnectionContext
name|connectionContext
init|=
name|regionBroker
operator|.
name|getConnectionContext
argument_list|(
name|connectionInfoClientId
argument_list|)
decl_stmt|;
comment|// get all matching Topics
specifier|final
name|Set
argument_list|<
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
name|Destination
argument_list|>
name|matchingDestinations
init|=
name|topicRegion
operator|.
name|getDestinations
argument_list|(
name|mqttSubscription
operator|.
name|getDestination
argument_list|()
argument_list|)
decl_stmt|;
for|for
control|(
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
name|Destination
name|dest
range|:
name|matchingDestinations
control|)
block|{
comment|// recover retroactive messages for matching subscription
for|for
control|(
name|Subscription
name|subscription
range|:
name|dest
operator|.
name|getConsumers
argument_list|()
control|)
block|{
if|if
condition|(
name|subscription
operator|.
name|getConsumerInfo
argument_list|()
operator|.
name|getConsumerId
argument_list|()
operator|.
name|equals
argument_list|(
name|consumerId
argument_list|)
condition|)
block|{
try|try
block|{
if|if
condition|(
name|dest
operator|instanceof
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
name|Topic
condition|)
block|{
operator|(
operator|(
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
name|Topic
operator|)
name|dest
operator|)
operator|.
name|recoverRetroactiveMessages
argument_list|(
name|connectionContext
argument_list|,
name|subscription
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|dest
operator|instanceof
name|VirtualTopicInterceptor
condition|)
block|{
operator|(
operator|(
name|VirtualTopicInterceptor
operator|)
name|dest
operator|)
operator|.
name|getTopic
argument_list|()
operator|.
name|recoverRetroactiveMessages
argument_list|(
name|connectionContext
argument_list|,
name|subscription
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|subscription
operator|instanceof
name|PrefetchSubscription
condition|)
block|{
comment|// request dispatch for prefetch subs
name|PrefetchSubscription
name|prefetchSubscription
init|=
operator|(
name|PrefetchSubscription
operator|)
name|subscription
decl_stmt|;
name|prefetchSubscription
operator|.
name|dispatchPending
argument_list|()
expr_stmt|;
block|}
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
literal|"Error recovering retained messages for "
operator|+
name|dest
operator|.
name|getName
argument_list|()
operator|+
literal|": "
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
break|break;
block|}
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
return|return
operator|new
name|ActiveMQTopic
argument_list|(
name|topicName
argument_list|)
return|;
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
return|return
name|destination
operator|.
name|getPhysicalName
argument_list|()
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
return|return
name|destination
operator|.
name|getPhysicalName
argument_list|()
operator|.
name|startsWith
argument_list|(
literal|"$"
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|MQTTSubscription
name|getSubscription
parameter_list|(
name|ConsumerId
name|consumerId
parameter_list|)
block|{
return|return
name|subscriptionsByConsumerId
operator|.
name|get
argument_list|(
name|consumerId
argument_list|)
return|;
block|}
specifier|protected
name|ConsumerId
name|getNextConsumerId
parameter_list|()
block|{
return|return
operator|new
name|ConsumerId
argument_list|(
name|protocol
operator|.
name|getSessionId
argument_list|()
argument_list|,
name|consumerIdGenerator
operator|.
name|getNextSequenceId
argument_list|()
argument_list|)
return|;
block|}
specifier|protected
name|byte
name|doSubscribe
parameter_list|(
name|ConsumerInfo
name|consumerInfo
parameter_list|,
specifier|final
name|String
name|topicName
parameter_list|,
specifier|final
name|QoS
name|qoS
parameter_list|)
throws|throws
name|MQTTProtocolException
block|{
name|MQTTSubscription
name|mqttSubscription
init|=
operator|new
name|MQTTSubscription
argument_list|(
name|protocol
argument_list|,
name|topicName
argument_list|,
name|qoS
argument_list|,
name|consumerInfo
argument_list|)
decl_stmt|;
comment|// optimistic add to local maps first to be able to handle commands in onActiveMQCommand
name|subscriptionsByConsumerId
operator|.
name|put
argument_list|(
name|consumerInfo
operator|.
name|getConsumerId
argument_list|()
argument_list|,
name|mqttSubscription
argument_list|)
expr_stmt|;
name|mqttSubscriptionByTopic
operator|.
name|put
argument_list|(
name|topicName
argument_list|,
name|mqttSubscription
argument_list|)
expr_stmt|;
specifier|final
name|byte
index|[]
name|qos
init|=
block|{
operator|-
literal|1
block|}
decl_stmt|;
name|protocol
operator|.
name|sendToActiveMQ
argument_list|(
name|consumerInfo
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
comment|// validate subscription request
if|if
condition|(
name|response
operator|.
name|isException
argument_list|()
condition|)
block|{
specifier|final
name|Throwable
name|throwable
init|=
operator|(
operator|(
name|ExceptionResponse
operator|)
name|response
operator|)
operator|.
name|getException
argument_list|()
decl_stmt|;
name|LOG
operator|.
name|warn
argument_list|(
literal|"Error subscribing to {}"
argument_list|,
name|topicName
argument_list|,
name|throwable
argument_list|)
expr_stmt|;
comment|// version 3.1 don't supports silent fail
comment|// version 3.1.1 send "error" qos
if|if
condition|(
name|protocol
operator|.
name|version
operator|==
name|MQTTProtocolConverter
operator|.
name|V3_1_1
condition|)
block|{
name|qos
index|[
literal|0
index|]
operator|=
name|SUBSCRIBE_ERROR
expr_stmt|;
block|}
else|else
block|{
name|qos
index|[
literal|0
index|]
operator|=
operator|(
name|byte
operator|)
name|qoS
operator|.
name|ordinal
argument_list|()
expr_stmt|;
block|}
block|}
else|else
block|{
name|qos
index|[
literal|0
index|]
operator|=
operator|(
name|byte
operator|)
name|qoS
operator|.
name|ordinal
argument_list|()
expr_stmt|;
block|}
block|}
block|}
argument_list|)
expr_stmt|;
if|if
condition|(
name|qos
index|[
literal|0
index|]
operator|==
name|SUBSCRIBE_ERROR
condition|)
block|{
comment|// remove from local maps if subscribe failed
name|subscriptionsByConsumerId
operator|.
name|remove
argument_list|(
name|consumerInfo
operator|.
name|getConsumerId
argument_list|()
argument_list|)
expr_stmt|;
name|mqttSubscriptionByTopic
operator|.
name|remove
argument_list|(
name|topicName
argument_list|)
expr_stmt|;
block|}
return|return
name|qos
index|[
literal|0
index|]
return|;
block|}
specifier|public
name|void
name|doUnSubscribe
parameter_list|(
name|MQTTSubscription
name|subscription
parameter_list|)
block|{
name|mqttSubscriptionByTopic
operator|.
name|remove
argument_list|(
name|subscription
operator|.
name|getTopicName
argument_list|()
argument_list|)
expr_stmt|;
name|ConsumerInfo
name|info
init|=
name|subscription
operator|.
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
name|subscriptionsByConsumerId
operator|.
name|remove
argument_list|(
name|info
operator|.
name|getConsumerId
argument_list|()
argument_list|)
expr_stmt|;
name|RemoveInfo
name|removeInfo
init|=
name|info
operator|.
name|createRemoveCommand
argument_list|()
decl_stmt|;
name|protocol
operator|.
name|sendToActiveMQ
argument_list|(
name|removeInfo
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
comment|//----- Durable Subscription management methods --------------------------//
specifier|protected
name|void
name|deleteDurableSubs
parameter_list|(
name|List
argument_list|<
name|SubscriptionInfo
argument_list|>
name|subs
parameter_list|)
block|{
try|try
block|{
for|for
control|(
name|SubscriptionInfo
name|sub
range|:
name|subs
control|)
block|{
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
name|sub
operator|.
name|getSubcriptionName
argument_list|()
argument_list|)
expr_stmt|;
name|rsi
operator|.
name|setClientId
argument_list|(
name|sub
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
literal|"Could not delete the MQTT durable subs."
argument_list|,
name|e
argument_list|)
expr_stmt|;
block|}
block|}
specifier|protected
name|void
name|restoreDurableSubs
parameter_list|(
name|List
argument_list|<
name|SubscriptionInfo
argument_list|>
name|subs
parameter_list|)
block|{
try|try
block|{
for|for
control|(
name|SubscriptionInfo
name|sub
range|:
name|subs
control|)
block|{
name|String
name|name
init|=
name|sub
operator|.
name|getSubcriptionName
argument_list|()
decl_stmt|;
name|String
index|[]
name|split
init|=
name|name
operator|.
name|split
argument_list|(
literal|":"
argument_list|,
literal|2
argument_list|)
decl_stmt|;
name|QoS
name|qoS
init|=
name|QoS
operator|.
name|valueOf
argument_list|(
name|split
index|[
literal|0
index|]
argument_list|)
decl_stmt|;
name|onSubscribe
argument_list|(
operator|new
name|Topic
argument_list|(
name|split
index|[
literal|1
index|]
argument_list|,
name|qoS
argument_list|)
argument_list|)
expr_stmt|;
comment|// mark this durable subscription as restored by Broker
name|restoredDurableSubs
operator|.
name|add
argument_list|(
name|MQTTProtocolSupport
operator|.
name|convertMQTTToActiveMQ
argument_list|(
name|split
index|[
literal|1
index|]
argument_list|)
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
literal|"Could not restore the MQTT durable subs."
argument_list|,
name|e
argument_list|)
expr_stmt|;
block|}
block|}
specifier|protected
name|List
argument_list|<
name|SubscriptionInfo
argument_list|>
name|lookupSubscription
parameter_list|(
name|String
name|clientId
parameter_list|)
throws|throws
name|MQTTProtocolException
block|{
name|List
argument_list|<
name|SubscriptionInfo
argument_list|>
name|result
init|=
operator|new
name|ArrayList
argument_list|<
name|SubscriptionInfo
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
literal|"Error recovering durable subscriptions: "
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
name|TopicRegion
name|topicRegion
init|=
operator|(
name|TopicRegion
operator|)
name|regionBroker
operator|.
name|getTopicRegion
argument_list|()
decl_stmt|;
name|List
argument_list|<
name|DurableTopicSubscription
argument_list|>
name|subscriptions
init|=
name|topicRegion
operator|.
name|lookupSubscriptions
argument_list|(
name|clientId
argument_list|)
decl_stmt|;
if|if
condition|(
name|subscriptions
operator|!=
literal|null
condition|)
block|{
for|for
control|(
name|DurableTopicSubscription
name|subscription
range|:
name|subscriptions
control|)
block|{
name|LOG
operator|.
name|debug
argument_list|(
literal|"Recovered durable sub:{} on connect"
argument_list|,
name|subscription
argument_list|)
expr_stmt|;
name|SubscriptionInfo
name|info
init|=
operator|new
name|SubscriptionInfo
argument_list|()
decl_stmt|;
name|info
operator|.
name|setDestination
argument_list|(
name|subscription
operator|.
name|getActiveMQDestination
argument_list|()
argument_list|)
expr_stmt|;
name|info
operator|.
name|setSubcriptionName
argument_list|(
name|subscription
operator|.
name|getSubscriptionKey
argument_list|()
operator|.
name|getSubscriptionName
argument_list|()
argument_list|)
expr_stmt|;
name|info
operator|.
name|setClientId
argument_list|(
name|clientId
argument_list|)
expr_stmt|;
name|result
operator|.
name|add
argument_list|(
name|info
argument_list|)
expr_stmt|;
block|}
block|}
return|return
name|result
return|;
block|}
block|}
end_class

end_unit

