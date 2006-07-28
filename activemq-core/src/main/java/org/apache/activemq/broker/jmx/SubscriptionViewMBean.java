begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  *  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  * http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
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
specifier|public
name|String
name|getClientId
parameter_list|()
function_decl|;
comment|/**      * @return the id of the Connection the Subscription is on      */
specifier|public
name|String
name|getConnectionId
parameter_list|()
function_decl|;
comment|/**      * @return the id of the Session the subscription is on      */
specifier|public
name|long
name|getSessionId
parameter_list|()
function_decl|;
comment|/**      * @return the id of the Subscription      */
specifier|public
name|long
name|getSubcriptionId
parameter_list|()
function_decl|;
comment|/**      * @return the destination name      */
specifier|public
name|String
name|getDestinationName
parameter_list|()
function_decl|;
comment|/**      * @return the JMS selector on the current subscription      */
specifier|public
name|String
name|getSelector
parameter_list|()
function_decl|;
comment|/**      * Attempts to change the current active selector on the subscription. This      * operation is not supported for persistent topics.      */
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
function_decl|;
comment|/**      * @return true if the destination is a Queue      */
specifier|public
name|boolean
name|isDestinationQueue
parameter_list|()
function_decl|;
comment|/**      * @return true of the destination is a Topic      */
specifier|public
name|boolean
name|isDestinationTopic
parameter_list|()
function_decl|;
comment|/**      * @return true if the destination is temporary      */
specifier|public
name|boolean
name|isDestinationTemporary
parameter_list|()
function_decl|;
comment|/**      * @return true if the subscriber is active      */
specifier|public
name|boolean
name|isActive
parameter_list|()
function_decl|;
comment|/**      * @return number of messages pending delivery      */
specifier|public
name|int
name|getPendingQueueSize
parameter_list|()
function_decl|;
comment|/**      * @return number of messages dispatched      */
specifier|public
name|int
name|getDispatchedQueueSize
parameter_list|()
function_decl|;
comment|/**      * @return number of messages that matched the subscription      */
name|long
name|getDispachedCounter
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
comment|/**      * @return the prefetch that has been configured for this subscriber      */
specifier|public
name|int
name|getPrefetchSize
parameter_list|()
function_decl|;
comment|/**      * @return whether or not the subscriber is retroactive or not      */
specifier|public
name|boolean
name|isRetroactive
parameter_list|()
function_decl|;
comment|/**      * @return whether or not the subscriber is an exclusive consumer      */
specifier|public
name|boolean
name|isExclusive
parameter_list|()
function_decl|;
comment|/**      * @return whether or not the subscriber is durable (persistent)      */
specifier|public
name|boolean
name|isDurable
parameter_list|()
function_decl|;
comment|/**      * @return whether or not the subscriber ignores local messages      */
specifier|public
name|boolean
name|isNoLocal
parameter_list|()
function_decl|;
comment|/**      * @return the maximum number of pending messages allowed in addition to the      *         prefetch size. If enabled to a non-zero value then this will      *         perform eviction of messages for slow consumers on non-durable      *         topics.      */
specifier|public
name|int
name|getMaximumPendingMessageLimit
parameter_list|()
function_decl|;
comment|/**      * @return the consumer priority      */
specifier|public
name|byte
name|getPriority
parameter_list|()
function_decl|;
comment|/**      * @return the name of the consumer which is only used for durable      *         consumers.      */
specifier|public
name|String
name|getSubcriptionName
parameter_list|()
function_decl|;
block|}
end_interface

end_unit

