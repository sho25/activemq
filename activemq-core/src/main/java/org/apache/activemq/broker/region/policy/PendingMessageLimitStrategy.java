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
name|TopicSubscription
import|;
end_import

begin_comment
comment|/**  * A pluggable strategy to calculate the maximum number of messages that are allowed to be pending on   * consumers (in addition to their prefetch sizes).  *   * Once the limit is reached, non-durable topics can then start discarding old messages.  * This allows us to keep dispatching messages to slow consumers while not blocking fast consumers  * and discarding the messages oldest first.  *    * @version $Revision$  */
end_comment

begin_interface
specifier|public
interface|interface
name|PendingMessageLimitStrategy
block|{
comment|/**      * Calculate the maximum number of pending messages (in excess of the prefetch size)      * for the given subscription      *       * @return the maximum or -1 if there is no maximum      */
name|int
name|getMaximumPendingMessageLimit
parameter_list|(
name|TopicSubscription
name|subscription
parameter_list|)
function_decl|;
block|}
end_interface

end_unit

