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
name|List
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
name|filter
operator|.
name|MessageEvaluationContext
import|;
end_import

begin_comment
comment|/**  * @version $Revision: 1.5 $  */
end_comment

begin_interface
specifier|public
interface|interface
name|Subscription
extends|extends
name|SubscriptionRecovery
block|{
comment|/**      * Used to add messages that match the subscription.      * @param node      * @throws Exception       * @throws InterruptedException       * @throws IOException       */
name|void
name|add
parameter_list|(
name|MessageReference
name|node
parameter_list|)
throws|throws
name|Exception
function_decl|;
comment|/**      * Used when client acknowledge receipt of dispatched message.       * @param node      * @throws IOException       * @throws Exception       */
name|void
name|acknowledge
parameter_list|(
name|ConnectionContext
name|context
parameter_list|,
specifier|final
name|MessageAck
name|ack
parameter_list|)
throws|throws
name|Exception
function_decl|;
comment|/**      * Allows a consumer to pull a message on demand      */
name|Response
name|pullMessage
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
comment|/**      * Is the subscription interested in the message?      * @param node       * @param context      * @return      * @throws IOException       */
name|boolean
name|matches
parameter_list|(
name|MessageReference
name|node
parameter_list|,
name|MessageEvaluationContext
name|context
parameter_list|)
throws|throws
name|IOException
function_decl|;
comment|/**      * Is the subscription interested in messages in the destination?      * @param context      * @return      */
name|boolean
name|matches
parameter_list|(
name|ActiveMQDestination
name|destination
parameter_list|)
function_decl|;
comment|/**      * The subscription will be receiving messages from the destination.      * @param context       * @param destination      * @throws Exception       */
name|void
name|add
parameter_list|(
name|ConnectionContext
name|context
parameter_list|,
name|Destination
name|destination
parameter_list|)
throws|throws
name|Exception
function_decl|;
comment|/**      * The subscription will be no longer be receiving messages from the destination.      * @param context       * @param destination      * @return a list of un-acked messages that were added to the subscription.      */
name|List
argument_list|<
name|MessageReference
argument_list|>
name|remove
parameter_list|(
name|ConnectionContext
name|context
parameter_list|,
name|Destination
name|destination
parameter_list|)
throws|throws
name|Exception
function_decl|;
comment|/**      * The ConsumerInfo object that created the subscription.      * @param destination      */
name|ConsumerInfo
name|getConsumerInfo
parameter_list|()
function_decl|;
comment|/**      * The subscription should release as may references as it can to help the garbage collector      * reclaim memory.      */
name|void
name|gc
parameter_list|()
function_decl|;
comment|/**      * Used by a Slave Broker to update dispatch infomation      * @param mdn      * @throws Exception       */
name|void
name|processMessageDispatchNotification
parameter_list|(
name|MessageDispatchNotification
name|mdn
parameter_list|)
throws|throws
name|Exception
function_decl|;
comment|/**      * @return true if the broker is currently in slave mode      */
name|boolean
name|isSlave
parameter_list|()
function_decl|;
comment|/**      * @return number of messages pending delivery      */
name|int
name|getPendingQueueSize
parameter_list|()
function_decl|;
comment|/**      * @return number of messages dispatched to the client      */
name|int
name|getDispatchedQueueSize
parameter_list|()
function_decl|;
comment|/**      * @return number of messages dispatched to the client      */
name|long
name|getDispatchedCounter
parameter_list|()
function_decl|;
comment|/**      * @return number of messages that matched the subscription      */
name|long
name|getEnqueueCounter
parameter_list|()
function_decl|;
comment|/**      * @return number of messages queued by the client      */
name|long
name|getDequeueCounter
parameter_list|()
function_decl|;
comment|/**      * @return the JMS selector on the current subscription      */
name|String
name|getSelector
parameter_list|()
function_decl|;
comment|/**      * Attempts to change the current active selector on the subscription.      * This operation is not supported for persistent topics.      */
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
function_decl|;
comment|/**      * @return the JMX object name that this subscription was registered as if applicable      */
name|ObjectName
name|getObjectName
parameter_list|()
function_decl|;
comment|/**      * Set when the subscription is registered in JMX      */
name|void
name|setObjectName
parameter_list|(
name|ObjectName
name|objectName
parameter_list|)
function_decl|;
comment|/**      * @return true when 60% or more room is left for dispatching messages      */
name|boolean
name|isLowWaterMark
parameter_list|()
function_decl|;
comment|/**      * @return true when 10% or less room is left for dispatching messages      */
name|boolean
name|isHighWaterMark
parameter_list|()
function_decl|;
comment|/**      * @return true if there is no space to dispatch messages      */
name|boolean
name|isFull
parameter_list|()
function_decl|;
comment|/**      * inform the MessageConsumer on the client to change it's prefetch      * @param newPrefetch      */
name|void
name|updateConsumerPrefetch
parameter_list|(
name|int
name|newPrefetch
parameter_list|)
function_decl|;
comment|/**      * Called when the subscription is destroyed.      */
name|void
name|destroy
parameter_list|()
function_decl|;
comment|/**      * @return the prefetch size that is configured for the subscription      */
name|int
name|getPrefetchSize
parameter_list|()
function_decl|;
comment|/**      * @return the number of messages awaiting acknowledgement      */
name|int
name|getInFlightSize
parameter_list|()
function_decl|;
comment|/**      * @return the in flight messages as a percentage of the prefetch size      */
name|int
name|getInFlightUsage
parameter_list|()
function_decl|;
comment|/**      * Informs the Broker if the subscription needs to intervention to recover it's state      * e.g. DurableTopicSubscriber may do      * @see org.apache.activemq.region.cursors.PendingMessageCursor      * @return true if recovery required      */
name|boolean
name|isRecoveryRequired
parameter_list|()
function_decl|;
comment|/**      * @return true if a browser      */
name|boolean
name|isBrowser
parameter_list|()
function_decl|;
comment|/**      * @return the number of messages this subscription can accept before its full      */
name|int
name|countBeforeFull
parameter_list|()
function_decl|;
name|ConnectionContext
name|getContext
parameter_list|()
function_decl|;
block|}
end_interface

end_unit

