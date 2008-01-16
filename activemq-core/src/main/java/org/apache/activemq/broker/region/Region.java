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
name|org
operator|.
name|apache
operator|.
name|activemq
operator|.
name|Service
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
name|ConsumerBrokerExchange
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
name|ProducerBrokerExchange
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
name|MessageDispatchNotification
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
name|MessagePull
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
name|ProducerInfo
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

begin_comment
comment|/**  * A Region is used to implement the different QOS options available to   * a broker.  A Broker is composed of multiple message processing Regions that  * provide different QOS options.  *   * @version $Revision$  */
end_comment

begin_interface
specifier|public
interface|interface
name|Region
extends|extends
name|Service
block|{
comment|/**      * Used to create a destination.  Usually, this method is invoked as a side-effect of sending      * a message to a destination that does not exist yet.      *       * @param context      * @param destination the destination to create.      * @return TODO      * @throws Exception TODO      */
name|Destination
name|addDestination
parameter_list|(
name|ConnectionContext
name|context
parameter_list|,
name|ActiveMQDestination
name|destination
parameter_list|)
throws|throws
name|Exception
function_decl|;
comment|/**      * Used to destroy a destination.        * This should try to quiesce use of the destination up to the timeout allotted time before removing the destination.      * This will remove all persistent messages associated with the destination.      *       * @param context the environment the operation is being executed under.      * @param destination what is being removed from the broker.      * @param timeout the max amount of time to wait for the destination to quiesce      * @throws Exception TODO      */
name|void
name|removeDestination
parameter_list|(
name|ConnectionContext
name|context
parameter_list|,
name|ActiveMQDestination
name|destination
parameter_list|,
name|long
name|timeout
parameter_list|)
throws|throws
name|Exception
function_decl|;
comment|/**      * Returns a copy of the current destinations available in the region      *       * @return a copy of the regions currently active at the time of the call with the key the destination and the value the Destination.      */
name|Map
argument_list|<
name|ActiveMQDestination
argument_list|,
name|Destination
argument_list|>
name|getDestinationMap
parameter_list|()
function_decl|;
comment|/**      * Adds a consumer.      * @param context the environment the operation is being executed under.      * @return TODO      * @throws Exception TODO      */
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
function_decl|;
comment|/**      * Removes a consumer.      * @param context the environment the operation is being executed under.      * @throws Exception TODO      */
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
function_decl|;
comment|/**      * Adds a Producer.      * @param context the environment the operation is being executed under.      * @throws Exception TODO      */
name|void
name|addProducer
parameter_list|(
name|ConnectionContext
name|context
parameter_list|,
name|ProducerInfo
name|info
parameter_list|)
throws|throws
name|Exception
function_decl|;
comment|/**      * Removes a Producer.      * @param context the environment the operation is being executed under.      * @throws Exception TODO      */
name|void
name|removeProducer
parameter_list|(
name|ConnectionContext
name|context
parameter_list|,
name|ProducerInfo
name|info
parameter_list|)
throws|throws
name|Exception
function_decl|;
comment|/**      * Deletes a durable subscription.      * @param context the environment the operation is being executed under.      * @param info TODO      * @throws Exception TODO      */
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
function_decl|;
comment|/**      * Send a message to the broker to using the specified destination.  The destination specified      * in the message does not need to match the destination the message is sent to.  This is       * handy in case the message is being sent to a dead letter destination.      * @param producerExchange the environment the operation is being executed under.      * @param message       * @throws Exception TODO      */
name|void
name|send
parameter_list|(
name|ProducerBrokerExchange
name|producerExchange
parameter_list|,
name|Message
name|message
parameter_list|)
throws|throws
name|Exception
function_decl|;
comment|/**      * Used to acknowledge the receipt of a message by a client.      * @param consumerExchange the environment the operation is being executed under.      * @throws Exception TODO      */
name|void
name|acknowledge
parameter_list|(
name|ConsumerBrokerExchange
name|consumerExchange
parameter_list|,
name|MessageAck
name|ack
parameter_list|)
throws|throws
name|Exception
function_decl|;
comment|/**      * Allows a consumer to pull a message from a queue      */
name|Response
name|messagePull
parameter_list|(
name|ConnectionContext
name|context
parameter_list|,
name|MessagePull
name|pull
parameter_list|)
throws|throws
name|Exception
function_decl|;
comment|/**      * Process a notification of a dispatch - used by a Slave Broker      * @param messageDispatchNotification      * @throws Exception TODO      */
name|void
name|processDispatchNotification
parameter_list|(
name|MessageDispatchNotification
name|messageDispatchNotification
parameter_list|)
throws|throws
name|Exception
function_decl|;
name|void
name|gc
parameter_list|()
function_decl|;
comment|/**      * Provide an exact or wildcard lookup of destinations in the region      *       * @return a set of matching destination objects.      */
name|Set
argument_list|<
name|Destination
argument_list|>
name|getDestinations
parameter_list|(
name|ActiveMQDestination
name|destination
parameter_list|)
function_decl|;
block|}
end_interface

end_unit

