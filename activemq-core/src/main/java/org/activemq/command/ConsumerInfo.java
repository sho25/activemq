begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/** *<a href="http://activemq.org">ActiveMQ: The Open Source Message Fabric</a> * * Copyright 2005 (C) LogicBlaze, Inc. http://www.logicblaze.com * * Licensed under the Apache License, Version 2.0 (the "License"); * you may not use this file except in compliance with the License. * You may obtain a copy of the License at * * http://www.apache.org/licenses/LICENSE-2.0 * * Unless required by applicable law or agreed to in writing, software * distributed under the License is distributed on an "AS IS" BASIS, * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. * See the License for the specific language governing permissions and * limitations under the License. * **/
end_comment

begin_package
package|package
name|org
operator|.
name|activemq
operator|.
name|command
package|;
end_package

begin_import
import|import
name|org
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
name|activemq
operator|.
name|state
operator|.
name|CommandVisitor
import|;
end_import

begin_comment
comment|/**  *   * @openwire:marshaller  * @version $Revision: 1.20 $  */
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
name|subcriptionName
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
specifier|transient
name|BooleanExpression
name|additionalPredicate
decl_stmt|;
specifier|protected
specifier|transient
name|boolean
name|networkSubscription
decl_stmt|;
comment|//this subscription orginated from a network connection
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
name|subcriptionName
operator|=
name|subcriptionName
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
block|}
specifier|public
name|boolean
name|isDurable
parameter_list|()
block|{
return|return
name|subcriptionName
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
comment|/**      * The destination that the consumer is interested in receiving messages from.      * This destination could be a composite destination.      *       * @openwire:property version=1 cache=true      */
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
comment|/**      * How many messages a broker will send to the client without receiving an ack before      * he stops dispatching messages to the client.      *       * @openwire:property version=1      */
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
block|}
comment|/**      * Should the broker dispatch a message to the consumer async?  If he does it async, then       * he uses a more SEDA style of processing while if it is not done async, then he broker       * use a STP style of processing.  STP is more appropriate in high bandwidth situations or when      * being used by and in vm transport.      *       * @openwire:property version=1      */
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
comment|/**      * The JMS selector used to filter out messages that this consumer      * is interested in.      *       * @openwire:property version=1      */
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
name|getSubcriptionName
parameter_list|()
block|{
return|return
name|subcriptionName
return|;
block|}
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
name|subcriptionName
operator|=
name|durableSubscriptionId
expr_stmt|;
block|}
comment|/**      * Set noLocal to true to avoid receiving messages that were published locally on the same connection.      *       * @openwire:property version=1      */
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
comment|/**      * An exclusive consumer locks out other consumers from being able to receive messages      * from the destination.  If there are multiple exclusive consumers for a destination, the first one      * created will be the exclusive consumer of the destination.      *       * @openwire:property version=1      */
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
comment|/**      * A retroactive consumer only has meaning for Topics.  It allows a consumer      * to retroactively see messages sent prior to the consumer being created.  If the       * consumer is not durable, it will be delivered the last message published to the topic.      * If the consumer is durable then it will receive all persistent messages that are       * still stored in persistent storage for that topic.      *       * @openwire:property version=1      */
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
comment|/**      * The broker will avoid dispatching to a lower priority consumer if there are other higher priority       * consumers available to dispatch to.  This allows letting the broker to have an affinity to       * higher priority consumers.  Default priority is 0.      *       * @openwire:property version=1      */
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
comment|/**      * The route of brokers the command has moved through.       *       * @openwire:property version=1 cache=true      */
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
comment|/**      * A transient additional predicate that can be used it inject additional predicates      * into the selector on the fly.  Handy if if say a Security Broker interceptor wants to       * filter out messages based on security level of the consumer.      *       * @return      */
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
name|Throwable
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
block|}
end_class

end_unit

