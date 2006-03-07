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

begin_interface
specifier|public
interface|interface
name|TopicViewMBean
extends|extends
name|DestinationViewMBean
block|{
comment|/** 	 * Creates a durable subscription that is subscribed to this topic. 	 *  	 * @param clientId 	 * @param subscriberName 	 * @throws Exception 	 */
specifier|public
name|void
name|createDurableSubscriber
parameter_list|(
name|String
name|clientId
parameter_list|,
name|String
name|subscriberName
parameter_list|)
throws|throws
name|Exception
function_decl|;
comment|/** 	 * Destroys a durable subscription that had previously subscribed to this topic. 	 *  	 * @param clientId 	 * @param subscriberName 	 * @throws Exception 	 */
specifier|public
name|void
name|destroyDurableSubscriber
parameter_list|(
name|String
name|clientId
parameter_list|,
name|String
name|subscriberName
parameter_list|)
throws|throws
name|Exception
function_decl|;
block|}
end_interface

end_unit

