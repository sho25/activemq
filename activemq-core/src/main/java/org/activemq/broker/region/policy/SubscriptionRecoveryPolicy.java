begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  *  * Copyright 2004 The Apache Software Foundation  *  * Licensed under the Apache License, Version 2.0 (the "License");  * you may not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  * http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
package|package
name|org
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
name|activemq
operator|.
name|Service
import|;
end_import

begin_import
import|import
name|org
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
name|activemq
operator|.
name|broker
operator|.
name|region
operator|.
name|MessageReference
import|;
end_import

begin_import
import|import
name|org
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
name|activemq
operator|.
name|broker
operator|.
name|region
operator|.
name|Topic
import|;
end_import

begin_comment
comment|/**  * Abstraction to allow different recovery policies to be plugged  * into the region implementations.  This is used by a topic to retroactively recover  * messages that the subscription missed.  *   * @version $Revision$  */
end_comment

begin_interface
specifier|public
interface|interface
name|SubscriptionRecoveryPolicy
extends|extends
name|Service
block|{
comment|/**      * A message was sent to the destination.      *       * @param context      * @param node      * @return TODO      * @throws Throwable      */
name|boolean
name|add
parameter_list|(
name|ConnectionContext
name|context
parameter_list|,
name|MessageReference
name|message
parameter_list|)
throws|throws
name|Throwable
function_decl|;
comment|/**      * Let a subscription recover message held by the policy.      *       * @param context      * @param topic TODO      * @param topic       * @param node      * @throws Throwable      */
name|void
name|recover
parameter_list|(
name|ConnectionContext
name|context
parameter_list|,
name|Topic
name|topic
parameter_list|,
name|Subscription
name|sub
parameter_list|)
throws|throws
name|Throwable
function_decl|;
block|}
end_interface

end_unit

