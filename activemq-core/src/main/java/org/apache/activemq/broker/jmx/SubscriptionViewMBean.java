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
name|javax
operator|.
name|jms
operator|.
name|InvalidSelectorException
import|;
end_import

begin_comment
comment|/**  * @version $Revision: 1.5 $  */
end_comment

begin_interface
specifier|public
interface|interface
name|SubscriptionViewMBean
block|{
comment|/**      * @return the clientId of the Connection the Subscription is on      */
annotation|@
name|MBeanInfo
argument_list|(
literal|"JMS Client id of the Connection the Subscription is on."
argument_list|)
name|String
name|getClientId
parameter_list|()
function_decl|;
comment|/**      * @return the id of the Connection the Subscription is on      */
annotation|@
name|MBeanInfo
argument_list|(
literal|"ID of the Connection the Subscription is on."
argument_list|)
name|String
name|getConnectionId
parameter_list|()
function_decl|;
comment|/**      * @return the id of the Session the subscription is on      */
annotation|@
name|MBeanInfo
argument_list|(
literal|"ID of the Session the Subscription is on."
argument_list|)
name|long
name|getSessionId
parameter_list|()
function_decl|;
comment|/**      * @return the id of the Subscription      */
annotation|@
name|MBeanInfo
argument_list|(
literal|"ID of the Subscription."
argument_list|)
name|long
name|getSubcriptionId
parameter_list|()
function_decl|;
comment|/**      * @return the destination name      */
annotation|@
name|MBeanInfo
argument_list|(
literal|"The name of the destionation the subscription is on."
argument_list|)
name|String
name|getDestinationName
parameter_list|()
function_decl|;
comment|/**      * @return the JMS selector on the current subscription      */
annotation|@
name|MBeanInfo
argument_list|(
literal|"The SQL-92 message header selector or XPATH body selector of the subscription."
argument_list|)
name|String
name|getSelector
parameter_list|()
function_decl|;
comment|/**      * Attempts to change the current active selector on the subscription. This      * operation is not supported for persistent topics.      */
name|void
name|setSelector
parameter_list|(
annotation|@
name|MBeanInfo
argument_list|(
literal|"selector"
argument_list|)
name|String
name|selector
parameter_list|)
throws|throws
name|InvalidSelectorException
throws|,
name|UnsupportedOperationException
function_decl|;
comment|/**      * @return true if the destination is a Queue      */
annotation|@
name|MBeanInfo
argument_list|(
literal|"Subscription is on a Queue"
argument_list|)
name|boolean
name|isDestinationQueue
parameter_list|()
function_decl|;
comment|/**      * @return true of the destination is a Topic      */
annotation|@
name|MBeanInfo
argument_list|(
literal|"Subscription is on a Topic"
argument_list|)
name|boolean
name|isDestinationTopic
parameter_list|()
function_decl|;
comment|/**      * @return true if the destination is temporary      */
annotation|@
name|MBeanInfo
argument_list|(
literal|"Subscription is on a temporary Queue/Topic"
argument_list|)
name|boolean
name|isDestinationTemporary
parameter_list|()
function_decl|;
comment|/**      * @return true if the subscriber is active      */
annotation|@
name|MBeanInfo
argument_list|(
literal|"Subscription is active (connected and receiving messages)."
argument_list|)
name|boolean
name|isActive
parameter_list|()
function_decl|;
comment|/**      * @return number of messages pending delivery      */
annotation|@
name|MBeanInfo
argument_list|(
literal|"Number of messages pending delivery."
argument_list|)
name|int
name|getPendingQueueSize
parameter_list|()
function_decl|;
comment|/**      * @return number of messages dispatched      */
annotation|@
name|MBeanInfo
argument_list|(
literal|"Number of messages dispatched awaiting acknowledgement."
argument_list|)
name|int
name|getDispatchedQueueSize
parameter_list|()
function_decl|;
comment|/**      * The same as the number of messages dispatched -       * making it explicit      * @return      */
annotation|@
name|MBeanInfo
argument_list|(
literal|"Number of messages dispatched awaiting acknowledgement."
argument_list|)
name|int
name|getMessageCountAwaitingAcknowledge
parameter_list|()
function_decl|;
comment|/**      * @return number of messages that matched the subscription      */
annotation|@
name|MBeanInfo
argument_list|(
literal|"Number of messages that sent to the client."
argument_list|)
name|long
name|getDispachedCounter
parameter_list|()
function_decl|;
comment|/**      * @return number of messages that matched the subscription      */
annotation|@
name|MBeanInfo
argument_list|(
literal|"Number of messages that matched the subscription."
argument_list|)
name|long
name|getEnqueueCounter
parameter_list|()
function_decl|;
comment|/**      * @return number of messages queued by the client      */
annotation|@
name|MBeanInfo
argument_list|(
literal|"Number of messages were sent to and acknowledge by the client."
argument_list|)
name|long
name|getDequeueCounter
parameter_list|()
function_decl|;
comment|/**      * @return the prefetch that has been configured for this subscriber      */
annotation|@
name|MBeanInfo
argument_list|(
literal|"Number of messages to pre-fetch and dispatch to the client."
argument_list|)
name|int
name|getPrefetchSize
parameter_list|()
function_decl|;
comment|/**      * @return whether or not the subscriber is retroactive or not      */
annotation|@
name|MBeanInfo
argument_list|(
literal|"The subscriber is retroactive (tries to receive broadcasted topic messages sent prior to connecting)"
argument_list|)
name|boolean
name|isRetroactive
parameter_list|()
function_decl|;
comment|/**      * @return whether or not the subscriber is an exclusive consumer      */
annotation|@
name|MBeanInfo
argument_list|(
literal|"The subscriber is exclusive (no other subscribers may receive messages from the destination as long as this one is)"
argument_list|)
name|boolean
name|isExclusive
parameter_list|()
function_decl|;
comment|/**      * @return whether or not the subscriber is durable (persistent)      */
annotation|@
name|MBeanInfo
argument_list|(
literal|"The subsription is persistent."
argument_list|)
name|boolean
name|isDurable
parameter_list|()
function_decl|;
comment|/**      * @return whether or not the subscriber ignores local messages      */
annotation|@
name|MBeanInfo
argument_list|(
literal|"The subsription ignores local messages."
argument_list|)
name|boolean
name|isNoLocal
parameter_list|()
function_decl|;
comment|/**      * @return the maximum number of pending messages allowed in addition to the      *         prefetch size. If enabled to a non-zero value then this will      *         perform eviction of messages for slow consumers on non-durable      *         topics.      */
annotation|@
name|MBeanInfo
argument_list|(
literal|"The maximum number of pending messages allowed (in addition to the prefetch size)."
argument_list|)
name|int
name|getMaximumPendingMessageLimit
parameter_list|()
function_decl|;
comment|/**      * @return the consumer priority      */
annotation|@
name|MBeanInfo
argument_list|(
literal|"The subscription priority"
argument_list|)
name|byte
name|getPriority
parameter_list|()
function_decl|;
comment|/**      * @return the name of the consumer which is only used for durable      *         consumers.      */
annotation|@
name|MBeanInfo
argument_list|(
literal|"The name of the subscription (durable subscriptions only)."
argument_list|)
name|String
name|getSubcriptionName
parameter_list|()
function_decl|;
comment|/**      * Returns true if this subscription (which may be using wildcards) matches the given queue name      *      * @param queueName the JMS queue name to match against      * @return true if this subscription matches the given queue or false if not      */
annotation|@
name|MBeanInfo
argument_list|(
literal|"Returns true if the subscription (which may be using wildcards) matches the given queue name"
argument_list|)
name|boolean
name|isMatchingQueue
parameter_list|(
name|String
name|queueName
parameter_list|)
function_decl|;
comment|/**      * Returns true if this subscription (which may be using wildcards) matches the given topic name      *      * @param topicName the JMS topic name to match against      * @return true if this subscription matches the given topic or false if not      */
annotation|@
name|MBeanInfo
argument_list|(
literal|"Returns true if the subscription (which may be using wildcards) matches the given topic name"
argument_list|)
name|boolean
name|isMatchingTopic
parameter_list|(
name|String
name|topicName
parameter_list|)
function_decl|;
block|}
end_interface

end_unit

