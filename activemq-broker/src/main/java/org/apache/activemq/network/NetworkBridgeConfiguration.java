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
name|network
package|;
end_package

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
name|CopyOnWriteArrayList
import|;
end_import

begin_comment
comment|/**  * Configuration for a NetworkBridge  */
end_comment

begin_class
specifier|public
class|class
name|NetworkBridgeConfiguration
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
name|NetworkBridgeConfiguration
operator|.
name|class
argument_list|)
decl_stmt|;
specifier|private
name|boolean
name|conduitSubscriptions
init|=
literal|true
decl_stmt|;
specifier|private
name|boolean
name|dynamicOnly
decl_stmt|;
specifier|private
name|boolean
name|dispatchAsync
init|=
literal|true
decl_stmt|;
specifier|private
name|boolean
name|decreaseNetworkConsumerPriority
decl_stmt|;
specifier|private
name|int
name|consumerPriorityBase
init|=
name|ConsumerInfo
operator|.
name|NETWORK_CONSUMER_PRIORITY
decl_stmt|;
specifier|private
name|boolean
name|duplex
decl_stmt|;
specifier|private
name|boolean
name|bridgeTempDestinations
init|=
literal|true
decl_stmt|;
specifier|private
name|int
name|prefetchSize
init|=
literal|1000
decl_stmt|;
specifier|private
name|int
name|networkTTL
init|=
literal|1
decl_stmt|;
specifier|private
name|String
name|brokerName
init|=
literal|"localhost"
decl_stmt|;
specifier|private
name|String
name|brokerURL
init|=
literal|""
decl_stmt|;
specifier|private
name|String
name|userName
decl_stmt|;
specifier|private
name|String
name|password
decl_stmt|;
specifier|private
name|String
name|destinationFilter
init|=
literal|null
decl_stmt|;
specifier|private
name|String
name|name
init|=
literal|"NC"
decl_stmt|;
specifier|protected
name|List
argument_list|<
name|ActiveMQDestination
argument_list|>
name|excludedDestinations
init|=
operator|new
name|CopyOnWriteArrayList
argument_list|<
name|ActiveMQDestination
argument_list|>
argument_list|()
decl_stmt|;
specifier|protected
name|List
argument_list|<
name|ActiveMQDestination
argument_list|>
name|dynamicallyIncludedDestinations
init|=
operator|new
name|CopyOnWriteArrayList
argument_list|<
name|ActiveMQDestination
argument_list|>
argument_list|()
decl_stmt|;
specifier|protected
name|List
argument_list|<
name|ActiveMQDestination
argument_list|>
name|staticallyIncludedDestinations
init|=
operator|new
name|CopyOnWriteArrayList
argument_list|<
name|ActiveMQDestination
argument_list|>
argument_list|()
decl_stmt|;
specifier|private
name|boolean
name|suppressDuplicateQueueSubscriptions
init|=
literal|false
decl_stmt|;
specifier|private
name|boolean
name|suppressDuplicateTopicSubscriptions
init|=
literal|true
decl_stmt|;
specifier|private
name|boolean
name|alwaysSyncSend
init|=
literal|false
decl_stmt|;
specifier|private
name|boolean
name|staticBridge
init|=
literal|false
decl_stmt|;
specifier|private
name|boolean
name|useCompression
init|=
literal|false
decl_stmt|;
comment|/**      * @return the conduitSubscriptions      */
specifier|public
name|boolean
name|isConduitSubscriptions
parameter_list|()
block|{
return|return
name|this
operator|.
name|conduitSubscriptions
return|;
block|}
comment|/**      * @param conduitSubscriptions the conduitSubscriptions to set      */
specifier|public
name|void
name|setConduitSubscriptions
parameter_list|(
name|boolean
name|conduitSubscriptions
parameter_list|)
block|{
name|this
operator|.
name|conduitSubscriptions
operator|=
name|conduitSubscriptions
expr_stmt|;
block|}
comment|/**      * @return the dynamicOnly      */
specifier|public
name|boolean
name|isDynamicOnly
parameter_list|()
block|{
return|return
name|this
operator|.
name|dynamicOnly
return|;
block|}
comment|/**      * @param dynamicOnly the dynamicOnly to set      */
specifier|public
name|void
name|setDynamicOnly
parameter_list|(
name|boolean
name|dynamicOnly
parameter_list|)
block|{
name|this
operator|.
name|dynamicOnly
operator|=
name|dynamicOnly
expr_stmt|;
block|}
comment|/**      * @return the bridgeTempDestinations      */
specifier|public
name|boolean
name|isBridgeTempDestinations
parameter_list|()
block|{
return|return
name|this
operator|.
name|bridgeTempDestinations
return|;
block|}
comment|/**      * @param bridgeTempDestinations the bridgeTempDestinations to set      */
specifier|public
name|void
name|setBridgeTempDestinations
parameter_list|(
name|boolean
name|bridgeTempDestinations
parameter_list|)
block|{
name|this
operator|.
name|bridgeTempDestinations
operator|=
name|bridgeTempDestinations
expr_stmt|;
block|}
comment|/**      * @return the decreaseNetworkConsumerPriority      */
specifier|public
name|boolean
name|isDecreaseNetworkConsumerPriority
parameter_list|()
block|{
return|return
name|this
operator|.
name|decreaseNetworkConsumerPriority
return|;
block|}
comment|/**      * @param decreaseNetworkConsumerPriority the      *                decreaseNetworkConsumerPriority to set      */
specifier|public
name|void
name|setDecreaseNetworkConsumerPriority
parameter_list|(
name|boolean
name|decreaseNetworkConsumerPriority
parameter_list|)
block|{
name|this
operator|.
name|decreaseNetworkConsumerPriority
operator|=
name|decreaseNetworkConsumerPriority
expr_stmt|;
block|}
comment|/**      * @return the dispatchAsync      */
specifier|public
name|boolean
name|isDispatchAsync
parameter_list|()
block|{
return|return
name|this
operator|.
name|dispatchAsync
return|;
block|}
comment|/**      * @param dispatchAsync the dispatchAsync to set      */
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
comment|/**      * @return the duplex      */
specifier|public
name|boolean
name|isDuplex
parameter_list|()
block|{
return|return
name|this
operator|.
name|duplex
return|;
block|}
comment|/**      * @param duplex the duplex to set      */
specifier|public
name|void
name|setDuplex
parameter_list|(
name|boolean
name|duplex
parameter_list|)
block|{
name|this
operator|.
name|duplex
operator|=
name|duplex
expr_stmt|;
block|}
comment|/**      * @return the brokerName      */
specifier|public
name|String
name|getBrokerName
parameter_list|()
block|{
return|return
name|this
operator|.
name|brokerName
return|;
block|}
comment|/**      * @param brokerName the localBrokerName to set      */
specifier|public
name|void
name|setBrokerName
parameter_list|(
name|String
name|brokerName
parameter_list|)
block|{
name|this
operator|.
name|brokerName
operator|=
name|brokerName
expr_stmt|;
block|}
comment|/**      * @return the networkTTL      */
specifier|public
name|int
name|getNetworkTTL
parameter_list|()
block|{
return|return
name|this
operator|.
name|networkTTL
return|;
block|}
comment|/**      * @param networkTTL the networkTTL to set      */
specifier|public
name|void
name|setNetworkTTL
parameter_list|(
name|int
name|networkTTL
parameter_list|)
block|{
name|this
operator|.
name|networkTTL
operator|=
name|networkTTL
expr_stmt|;
block|}
comment|/**      * @return the password      */
specifier|public
name|String
name|getPassword
parameter_list|()
block|{
return|return
name|this
operator|.
name|password
return|;
block|}
comment|/**      * @param password the password to set      */
specifier|public
name|void
name|setPassword
parameter_list|(
name|String
name|password
parameter_list|)
block|{
name|this
operator|.
name|password
operator|=
name|password
expr_stmt|;
block|}
comment|/**      * @return the prefetchSize      */
specifier|public
name|int
name|getPrefetchSize
parameter_list|()
block|{
return|return
name|this
operator|.
name|prefetchSize
return|;
block|}
comment|/**      * @param prefetchSize the prefetchSize to set      * @org.apache.xbean.Property propertyEditor="org.apache.activemq.util.MemoryIntPropertyEditor"      */
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
block|}
comment|/**      * @return the userName      */
specifier|public
name|String
name|getUserName
parameter_list|()
block|{
return|return
name|this
operator|.
name|userName
return|;
block|}
comment|/**      * @param userName the userName to set      */
specifier|public
name|void
name|setUserName
parameter_list|(
name|String
name|userName
parameter_list|)
block|{
name|this
operator|.
name|userName
operator|=
name|userName
expr_stmt|;
block|}
comment|/**      * @return the destinationFilter      */
specifier|public
name|String
name|getDestinationFilter
parameter_list|()
block|{
if|if
condition|(
name|this
operator|.
name|destinationFilter
operator|==
literal|null
condition|)
block|{
if|if
condition|(
name|dynamicallyIncludedDestinations
operator|!=
literal|null
operator|&&
operator|!
name|dynamicallyIncludedDestinations
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
name|StringBuffer
name|filter
init|=
operator|new
name|StringBuffer
argument_list|()
decl_stmt|;
name|String
name|delimiter
init|=
literal|""
decl_stmt|;
for|for
control|(
name|ActiveMQDestination
name|destination
range|:
name|dynamicallyIncludedDestinations
control|)
block|{
if|if
condition|(
operator|!
name|destination
operator|.
name|isTemporary
argument_list|()
condition|)
block|{
name|filter
operator|.
name|append
argument_list|(
name|delimiter
argument_list|)
expr_stmt|;
name|filter
operator|.
name|append
argument_list|(
name|AdvisorySupport
operator|.
name|CONSUMER_ADVISORY_TOPIC_PREFIX
argument_list|)
expr_stmt|;
name|filter
operator|.
name|append
argument_list|(
name|destination
operator|.
name|getDestinationTypeAsString
argument_list|()
argument_list|)
expr_stmt|;
name|filter
operator|.
name|append
argument_list|(
literal|"."
argument_list|)
expr_stmt|;
name|filter
operator|.
name|append
argument_list|(
name|destination
operator|.
name|getPhysicalName
argument_list|()
argument_list|)
expr_stmt|;
name|delimiter
operator|=
literal|","
expr_stmt|;
block|}
block|}
return|return
name|filter
operator|.
name|toString
argument_list|()
return|;
block|}
else|else
block|{
return|return
name|AdvisorySupport
operator|.
name|CONSUMER_ADVISORY_TOPIC_PREFIX
operator|+
literal|">"
return|;
block|}
block|}
else|else
block|{
comment|// prepend consumer advisory prefix
comment|// to keep backward compatibility
if|if
condition|(
operator|!
name|this
operator|.
name|destinationFilter
operator|.
name|startsWith
argument_list|(
name|AdvisorySupport
operator|.
name|CONSUMER_ADVISORY_TOPIC_PREFIX
argument_list|)
condition|)
block|{
return|return
name|AdvisorySupport
operator|.
name|CONSUMER_ADVISORY_TOPIC_PREFIX
operator|+
name|this
operator|.
name|destinationFilter
return|;
block|}
else|else
block|{
return|return
name|this
operator|.
name|destinationFilter
return|;
block|}
block|}
block|}
comment|/**      * @param destinationFilter the destinationFilter to set      */
specifier|public
name|void
name|setDestinationFilter
parameter_list|(
name|String
name|destinationFilter
parameter_list|)
block|{
name|this
operator|.
name|destinationFilter
operator|=
name|destinationFilter
expr_stmt|;
block|}
comment|/**      * @return the name      */
specifier|public
name|String
name|getName
parameter_list|()
block|{
return|return
name|this
operator|.
name|name
return|;
block|}
comment|/**      * @param name the name to set      */
specifier|public
name|void
name|setName
parameter_list|(
name|String
name|name
parameter_list|)
block|{
name|this
operator|.
name|name
operator|=
name|name
expr_stmt|;
block|}
specifier|public
name|List
argument_list|<
name|ActiveMQDestination
argument_list|>
name|getExcludedDestinations
parameter_list|()
block|{
return|return
name|excludedDestinations
return|;
block|}
specifier|public
name|void
name|setExcludedDestinations
parameter_list|(
name|List
argument_list|<
name|ActiveMQDestination
argument_list|>
name|excludedDestinations
parameter_list|)
block|{
name|this
operator|.
name|excludedDestinations
operator|=
name|excludedDestinations
expr_stmt|;
block|}
specifier|public
name|List
argument_list|<
name|ActiveMQDestination
argument_list|>
name|getDynamicallyIncludedDestinations
parameter_list|()
block|{
return|return
name|dynamicallyIncludedDestinations
return|;
block|}
specifier|public
name|void
name|setDynamicallyIncludedDestinations
parameter_list|(
name|List
argument_list|<
name|ActiveMQDestination
argument_list|>
name|dynamicallyIncludedDestinations
parameter_list|)
block|{
name|this
operator|.
name|dynamicallyIncludedDestinations
operator|=
name|dynamicallyIncludedDestinations
expr_stmt|;
block|}
specifier|public
name|List
argument_list|<
name|ActiveMQDestination
argument_list|>
name|getStaticallyIncludedDestinations
parameter_list|()
block|{
return|return
name|staticallyIncludedDestinations
return|;
block|}
specifier|public
name|void
name|setStaticallyIncludedDestinations
parameter_list|(
name|List
argument_list|<
name|ActiveMQDestination
argument_list|>
name|staticallyIncludedDestinations
parameter_list|)
block|{
name|this
operator|.
name|staticallyIncludedDestinations
operator|=
name|staticallyIncludedDestinations
expr_stmt|;
block|}
specifier|public
name|boolean
name|isSuppressDuplicateQueueSubscriptions
parameter_list|()
block|{
return|return
name|suppressDuplicateQueueSubscriptions
return|;
block|}
comment|/**      *      * @param val if true, duplicate network queue subscriptions (in a cyclic network) will be suppressed      */
specifier|public
name|void
name|setSuppressDuplicateQueueSubscriptions
parameter_list|(
name|boolean
name|val
parameter_list|)
block|{
name|suppressDuplicateQueueSubscriptions
operator|=
name|val
expr_stmt|;
block|}
specifier|public
name|boolean
name|isSuppressDuplicateTopicSubscriptions
parameter_list|()
block|{
return|return
name|suppressDuplicateTopicSubscriptions
return|;
block|}
comment|/**      *      * @param val if true, duplicate network topic subscriptions (in a cyclic network) will be suppressed      */
specifier|public
name|void
name|setSuppressDuplicateTopicSubscriptions
parameter_list|(
name|boolean
name|val
parameter_list|)
block|{
name|suppressDuplicateTopicSubscriptions
operator|=
name|val
expr_stmt|;
block|}
comment|/**      * @return the brokerURL      */
specifier|public
name|String
name|getBrokerURL
parameter_list|()
block|{
return|return
name|this
operator|.
name|brokerURL
return|;
block|}
comment|/**      * @param brokerURL the brokerURL to set      */
specifier|public
name|void
name|setBrokerURL
parameter_list|(
name|String
name|brokerURL
parameter_list|)
block|{
name|this
operator|.
name|brokerURL
operator|=
name|brokerURL
expr_stmt|;
block|}
specifier|public
name|boolean
name|isAlwaysSyncSend
parameter_list|()
block|{
return|return
name|alwaysSyncSend
return|;
block|}
comment|/**      * @param alwaysSyncSend  when true, both persistent and non persistent      * messages will be sent using a request. When false, non persistent messages      * are acked once the oneway send succeeds, which can potentially lead to      * message loss.      * Using an async request, allows multiple outstanding requests. This ensures      * that a bridge need not block all sending when the remote broker needs to      * flow control a single destination.      */
specifier|public
name|void
name|setAlwaysSyncSend
parameter_list|(
name|boolean
name|alwaysSyncSend
parameter_list|)
block|{
name|this
operator|.
name|alwaysSyncSend
operator|=
name|alwaysSyncSend
expr_stmt|;
block|}
specifier|public
name|int
name|getConsumerPriorityBase
parameter_list|()
block|{
return|return
name|consumerPriorityBase
return|;
block|}
comment|/**      * @param consumerPriorityBase , default -5. Sets the starting priority      * for consumers. This base value will be decremented by the length of the      * broker path when decreaseNetworkConsumerPriority is set.      */
specifier|public
name|void
name|setConsumerPriorityBase
parameter_list|(
name|int
name|consumerPriorityBase
parameter_list|)
block|{
name|this
operator|.
name|consumerPriorityBase
operator|=
name|consumerPriorityBase
expr_stmt|;
block|}
specifier|public
name|boolean
name|isStaticBridge
parameter_list|()
block|{
return|return
name|staticBridge
return|;
block|}
specifier|public
name|void
name|setStaticBridge
parameter_list|(
name|boolean
name|staticBridge
parameter_list|)
block|{
name|this
operator|.
name|staticBridge
operator|=
name|staticBridge
expr_stmt|;
block|}
comment|/**      * @param useCompression      *      True if the Network should enforce compression for messages sent.      */
specifier|public
name|void
name|setUseCompression
parameter_list|(
name|boolean
name|useCompression
parameter_list|)
block|{
name|this
operator|.
name|useCompression
operator|=
name|useCompression
expr_stmt|;
block|}
comment|/**      * @return the useCompression setting, true if message will be compressed on send.      */
specifier|public
name|boolean
name|isUseCompression
parameter_list|()
block|{
return|return
name|useCompression
return|;
block|}
block|}
end_class

end_unit

