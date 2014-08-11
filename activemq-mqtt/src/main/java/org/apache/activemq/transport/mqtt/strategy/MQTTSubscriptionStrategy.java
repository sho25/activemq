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
name|MQTTSubscription
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
name|fusesource
operator|.
name|mqtt
operator|.
name|codec
operator|.
name|CONNECT
import|;
end_import

begin_comment
comment|/**  * Subscription management strategy used to control how MQTT clients  * subscribe to destination and how messages are addressed in order to  * arrive on the appropriate destinations.  */
end_comment

begin_interface
specifier|public
interface|interface
name|MQTTSubscriptionStrategy
block|{
comment|/**      * Initialize the strategy before first use.      *      * @param protocol      *        the MQTTProtocolConverter that is initializing the strategy      *      * @throws MQTTProtocolException if an error occurs during initialization.      */
specifier|public
name|void
name|initialize
parameter_list|(
name|MQTTProtocolConverter
name|protocol
parameter_list|)
throws|throws
name|MQTTProtocolException
function_decl|;
comment|/**      * Allows the strategy to perform any needed actions on client connect      * prior to the CONNACK frame being sent back such as recovering old      * subscriptions and performing any clean session actions.      *      * @throws MQTTProtocolException if an error occurs while processing the connect actions.      */
specifier|public
name|void
name|onConnect
parameter_list|(
name|CONNECT
name|connect
parameter_list|)
throws|throws
name|MQTTProtocolException
function_decl|;
comment|/**      * Called for each Topic that a client requests to subscribe to.  The strategy needs      * check each Topic for duplicate subscription requests and change of QoS state.      *      * @param topic      *        the MQTT Topic instance being subscribed to.      *      * @return the assigned QoS value given to the new subscription.      *      * @throws MQTTProtocolException if an error occurs while processing the subscribe actions.      */
specifier|public
name|byte
name|onSubscribe
parameter_list|(
name|Topic
name|topic
parameter_list|)
throws|throws
name|MQTTProtocolException
function_decl|;
comment|/**      * Called when a new Subscription is being requested.  This method allows the      * strategy to create a specific type of subscription for the client such as      * mapping topic subscriptions to Queues etc.      *      * @param topicName      *        the requested Topic name to subscribe to.      * @param requestedQoS      *        the QoS level that the client has requested for this subscription.      *      * @return the assigned QoS value given to the new subscription      *      * @throws MQTTProtocolException if an error occurs while processing the subscribe actions.      */
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
function_decl|;
comment|/**      * Called when a client sends a duplicate subscribe request which should      * force any retained messages on that topic to be replayed again as though      * the client had just subscribed for the first time.  The method should      * not unsubscribe the client as it might miss messages sent while the      * subscription is being recreated.      *      * @param subscription      *        the MQTTSubscription that contains the subscription state.      */
specifier|public
name|void
name|onReSubscribe
parameter_list|(
name|MQTTSubscription
name|subscription
parameter_list|)
throws|throws
name|MQTTProtocolException
function_decl|;
comment|/**      * Called when a client requests an un-subscribe a previous subscription.      *      * @param topicName      *        the name of the Topic the client wishes to unsubscribe from.      *      * @throws MQTTProtocolException if an error occurs during the un-subscribe processing.      */
specifier|public
name|void
name|onUnSubscribe
parameter_list|(
name|String
name|topicName
parameter_list|)
throws|throws
name|MQTTProtocolException
function_decl|;
comment|/**      * Intercepts PUBLISH operations from the client and allows the strategy to map the      * target destination so that the send operation will land in the destinations that      * this strategy has mapped the incoming subscribe requests to.      *      * @param topicName      *        the targeted Topic that the client sent the message to.      *      * @return an ActiveMQ Topic instance that lands the send in the correct destinations.      */
specifier|public
name|ActiveMQDestination
name|onSend
parameter_list|(
name|String
name|topicName
parameter_list|)
function_decl|;
comment|/**      * Intercepts send operations from the broker and allows the strategy to map the      * target topic name so that the client sees a valid Topic name.      *      * @param destination      *        the destination that the message was dispatched from      *      * @return an Topic name that is valid for the receiving client.      */
specifier|public
name|String
name|onSend
parameter_list|(
name|ActiveMQDestination
name|destination
parameter_list|)
function_decl|;
comment|/**      * Allows the protocol handler to interrogate an destination name to determine if it      * is equivalent to the MQTT control topic (starts with $).  Since the mapped destinations      * that the strategy might alter the naming scheme the strategy must provide a way to      * reverse map and determine if the destination was originally an MQTT control topic.      *      * @param destination      *        the destination to query.      *      * @return true if the destination is an MQTT control topic.      */
specifier|public
name|boolean
name|isControlTopic
parameter_list|(
name|ActiveMQDestination
name|destination
parameter_list|)
function_decl|;
comment|/**      * Sets the {@link MQTTProtocolConverter} that is the parent of this strategy object.      *      * @param parent      *        the {@link MQTTProtocolConverter} that owns this strategy.      */
specifier|public
name|void
name|setProtocolConverter
parameter_list|(
name|MQTTProtocolConverter
name|parent
parameter_list|)
function_decl|;
comment|/**      * @return the {@link MQTTProtocolConverter} that owns this strategy.      */
specifier|public
name|MQTTProtocolConverter
name|getProtocolConverter
parameter_list|()
function_decl|;
comment|/**      * Lookup an {@link MQTTSubscription} instance based on known {@link ConsumerId} value.      *      * @param consumer      *        the consumer ID to lookup.      *      * @return the {@link MQTTSubscription} for the consumer or null if no subscription exists.      */
specifier|public
name|MQTTSubscription
name|getSubscription
parameter_list|(
name|ConsumerId
name|consumer
parameter_list|)
function_decl|;
block|}
end_interface

end_unit

