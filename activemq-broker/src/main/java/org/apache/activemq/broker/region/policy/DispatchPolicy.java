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
name|java
operator|.
name|util
operator|.
name|List
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
name|region
operator|.
name|MessageReference
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
name|filter
operator|.
name|MessageEvaluationContext
import|;
end_import

begin_comment
comment|/**  * Abstraction to allow different dispatching policies to be plugged  * into the  topic region implementations.  This is used by a topic to deliver  * messages to the matching subscriptions.  *   *   */
end_comment

begin_interface
specifier|public
interface|interface
name|DispatchPolicy
block|{
comment|/**      * Decides how to dispatch a selected message to a collection of consumers.  A safe      * approach is to dispatch to every subscription that matches.      *      * The list will be safe to iterate over when this method is called      *       * @return true if at least one consumer was dispatched or false if there are no active subscriptions that could be dispatched      */
name|boolean
name|dispatch
parameter_list|(
name|MessageReference
name|node
parameter_list|,
name|MessageEvaluationContext
name|msgContext
parameter_list|,
name|List
argument_list|<
name|Subscription
argument_list|>
name|consumers
parameter_list|)
throws|throws
name|Exception
function_decl|;
block|}
end_interface

end_unit

