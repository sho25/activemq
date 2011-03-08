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
name|ra
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
name|RedeliveryPolicy
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|resource
operator|.
name|spi
operator|.
name|ResourceAdapter
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|resource
operator|.
name|spi
operator|.
name|ActivationSpec
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

begin_comment
comment|/**  *   */
end_comment

begin_interface
specifier|public
interface|interface
name|MessageActivationSpec
extends|extends
name|ActivationSpec
block|{
name|boolean
name|isValidUseRAManagedTransaction
parameter_list|(
name|List
argument_list|<
name|String
argument_list|>
name|errorMessages
parameter_list|)
function_decl|;
name|boolean
name|isValidNoLocal
parameter_list|(
name|List
argument_list|<
name|String
argument_list|>
name|errorMessages
parameter_list|)
function_decl|;
name|boolean
name|isValidMessageSelector
parameter_list|(
name|List
argument_list|<
name|String
argument_list|>
name|errorMessages
parameter_list|)
function_decl|;
name|boolean
name|isValidMaxSessions
parameter_list|(
name|List
argument_list|<
name|String
argument_list|>
name|errorMessages
parameter_list|)
function_decl|;
name|boolean
name|isValidMaxMessagesPerSessions
parameter_list|(
name|List
argument_list|<
name|String
argument_list|>
name|errorMessages
parameter_list|)
function_decl|;
name|boolean
name|isValidMaxMessagesPerBatch
parameter_list|(
name|List
argument_list|<
name|String
argument_list|>
name|errorMessages
parameter_list|)
function_decl|;
name|boolean
name|isValidEnableBatch
parameter_list|(
name|List
argument_list|<
name|String
argument_list|>
name|errorMessages
parameter_list|)
function_decl|;
comment|/**      * @see javax.resource.spi.ResourceAdapterAssociation#getResourceAdapter()      */
name|ResourceAdapter
name|getResourceAdapter
parameter_list|()
function_decl|;
comment|/**      * @return Returns the destinationType.      */
name|String
name|getDestinationType
parameter_list|()
function_decl|;
name|String
name|getPassword
parameter_list|()
function_decl|;
name|String
name|getUserName
parameter_list|()
function_decl|;
comment|/**      * @return Returns the messageSelector.      */
name|String
name|getMessageSelector
parameter_list|()
function_decl|;
comment|/**      * @return Returns the noLocal.      */
name|String
name|getNoLocal
parameter_list|()
function_decl|;
name|String
name|getAcknowledgeMode
parameter_list|()
function_decl|;
name|String
name|getClientId
parameter_list|()
function_decl|;
name|String
name|getDestination
parameter_list|()
function_decl|;
name|String
name|getSubscriptionDurability
parameter_list|()
function_decl|;
name|String
name|getSubscriptionName
parameter_list|()
function_decl|;
name|boolean
name|isValidSubscriptionName
parameter_list|(
name|List
argument_list|<
name|String
argument_list|>
name|errorMessages
parameter_list|)
function_decl|;
name|boolean
name|isValidClientId
parameter_list|(
name|List
argument_list|<
name|String
argument_list|>
name|errorMessages
parameter_list|)
function_decl|;
name|boolean
name|isDurableSubscription
parameter_list|()
function_decl|;
name|boolean
name|isValidSubscriptionDurability
parameter_list|(
name|List
argument_list|<
name|String
argument_list|>
name|errorMessages
parameter_list|)
function_decl|;
name|boolean
name|isValidAcknowledgeMode
parameter_list|(
name|List
argument_list|<
name|String
argument_list|>
name|errorMessages
parameter_list|)
function_decl|;
name|boolean
name|isValidDestinationType
parameter_list|(
name|List
argument_list|<
name|String
argument_list|>
name|errorMessages
parameter_list|)
function_decl|;
name|boolean
name|isValidDestination
parameter_list|(
name|List
argument_list|<
name|String
argument_list|>
name|errorMessages
parameter_list|)
function_decl|;
name|boolean
name|isEmpty
parameter_list|(
name|String
name|value
parameter_list|)
function_decl|;
name|int
name|getAcknowledgeModeForSession
parameter_list|()
function_decl|;
name|String
name|getMaxMessagesPerSessions
parameter_list|()
function_decl|;
name|String
name|getMaxSessions
parameter_list|()
function_decl|;
name|String
name|getUseRAManagedTransaction
parameter_list|()
function_decl|;
name|int
name|getMaxMessagesPerSessionsIntValue
parameter_list|()
function_decl|;
name|int
name|getMaxSessionsIntValue
parameter_list|()
function_decl|;
name|boolean
name|isUseRAManagedTransactionEnabled
parameter_list|()
function_decl|;
name|boolean
name|getNoLocalBooleanValue
parameter_list|()
function_decl|;
name|String
name|getEnableBatch
parameter_list|()
function_decl|;
name|boolean
name|getEnableBatchBooleanValue
parameter_list|()
function_decl|;
name|int
name|getMaxMessagesPerBatchIntValue
parameter_list|()
function_decl|;
name|String
name|getMaxMessagesPerBatch
parameter_list|()
function_decl|;
name|double
name|getBackOffMultiplier
parameter_list|()
function_decl|;
name|long
name|getMaximumRedeliveryDelay
parameter_list|()
function_decl|;
name|long
name|getInitialRedeliveryDelay
parameter_list|()
function_decl|;
name|int
name|getMaximumRedeliveries
parameter_list|()
function_decl|;
name|boolean
name|isUseExponentialBackOff
parameter_list|()
function_decl|;
name|RedeliveryPolicy
name|redeliveryPolicy
parameter_list|()
function_decl|;
name|RedeliveryPolicy
name|lazyCreateRedeliveryPolicy
parameter_list|()
function_decl|;
block|}
end_interface

end_unit

