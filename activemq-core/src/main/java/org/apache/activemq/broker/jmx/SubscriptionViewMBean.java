begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  *   * Copyright 2005-2006 The Apache Software Foundation  *   * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with  * the License. You may obtain a copy of the License at  *   * http://www.apache.org/licenses/LICENSE-2.0  *   * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on  * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the  * specific language governing permissions and limitations under the License.  */
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
block|}
end_interface

end_unit

