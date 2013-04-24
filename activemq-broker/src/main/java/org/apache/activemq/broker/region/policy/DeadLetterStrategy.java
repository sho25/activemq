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
operator|.
name|policy
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
name|Message
import|;
end_import

begin_comment
comment|/**  * A strategy for choosing which destination is used for dead letter queue messages.  *   *   */
end_comment

begin_interface
specifier|public
interface|interface
name|DeadLetterStrategy
block|{
comment|/**      * Allow pluggable strategy for deciding if message should be sent to a dead letter queue      * for example, you might not want to ignore expired or non-persistent messages      * @param message      * @return true if message should be sent to a dead letter queue      */
name|boolean
name|isSendToDeadLetterQueue
parameter_list|(
name|Message
name|message
parameter_list|)
function_decl|;
comment|/**      * Returns the dead letter queue for the given message and subscription.      */
name|ActiveMQDestination
name|getDeadLetterQueueFor
parameter_list|(
name|Message
name|message
parameter_list|,
name|Subscription
name|subscription
parameter_list|)
function_decl|;
comment|/**      * @return true if processes expired messages      */
specifier|public
name|boolean
name|isProcessExpired
parameter_list|()
function_decl|;
comment|/**      * @param processExpired the processExpired to set      */
specifier|public
name|void
name|setProcessExpired
parameter_list|(
name|boolean
name|processExpired
parameter_list|)
function_decl|;
comment|/**      * @return the processNonPersistent      */
specifier|public
name|boolean
name|isProcessNonPersistent
parameter_list|()
function_decl|;
comment|/**      * @param processNonPersistent the processNonPersistent to set      */
specifier|public
name|void
name|setProcessNonPersistent
parameter_list|(
name|boolean
name|processNonPersistent
parameter_list|)
function_decl|;
specifier|public
name|boolean
name|isDLQ
parameter_list|(
name|ActiveMQDestination
name|destination
parameter_list|)
function_decl|;
block|}
end_interface

end_unit

