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

begin_comment
comment|/**  *  *   */
end_comment

begin_interface
specifier|public
interface|interface
name|TopicSubscriptionViewMBean
extends|extends
name|SubscriptionViewMBean
block|{
comment|/**      * @return the number of messages discarded due to being a slow consumer      */
annotation|@
name|MBeanInfo
argument_list|(
literal|"Number of messages discared due to being a slow consumer"
argument_list|)
name|int
name|getDiscardedCount
parameter_list|()
function_decl|;
comment|/**      * @return the maximun number of messages that can be pending.      */
annotation|@
name|MBeanInfo
argument_list|(
literal|"Maximum number of messages that can be pending"
argument_list|)
name|int
name|getMaximumPendingQueueSize
parameter_list|()
function_decl|;
name|void
name|setMaximumPendingQueueSize
parameter_list|(
name|int
name|max
parameter_list|)
function_decl|;
block|}
end_interface

end_unit

