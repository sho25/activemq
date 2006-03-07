begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  *  * Copyright 2005-2006 The Apache Software Foundation  *  * Licensed under the Apache License, Version 2.0 (the "License");  * you may not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  * http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
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
comment|/**  *  * @version $Revision$  */
end_comment

begin_interface
specifier|public
interface|interface
name|TopicSubscriptionViewMBean
extends|extends
name|SubscriptionViewMBean
block|{
comment|/**      * @return the number of messages discarded due to being a slow consumer      */
specifier|public
name|int
name|getDiscardedCount
parameter_list|()
function_decl|;
comment|/**      * @return the maximun number of messages that can be pending.      */
specifier|public
name|int
name|getMaximumPendingQueueSize
parameter_list|()
function_decl|;
specifier|public
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

