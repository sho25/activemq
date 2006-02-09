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
name|network
operator|.
name|jms
package|;
end_package

begin_comment
comment|/**  * Create an Outbound Queue Bridge  *   * @org.apache.xbean.XBean  *   * @version $Revision: 1.1.1.1 $  */
end_comment

begin_class
specifier|public
class|class
name|OutboundQueueBridge
extends|extends
name|QueueBridge
block|{
name|String
name|outboundQueueName
decl_stmt|;
comment|/**      * Constructor that takes a foreign destination as an argument      * @param outboundQueueName      */
specifier|public
name|OutboundQueueBridge
parameter_list|(
name|String
name|outboundQueueName
parameter_list|)
block|{
name|this
operator|.
name|outboundQueueName
operator|=
name|outboundQueueName
expr_stmt|;
block|}
comment|/**      * Default Contructor      */
specifier|public
name|OutboundQueueBridge
parameter_list|()
block|{     }
comment|/**      * @return Returns the outboundQueueName.      */
specifier|public
name|String
name|getOutboundQueueName
parameter_list|()
block|{
return|return
name|outboundQueueName
return|;
block|}
comment|/**      * @param outboundQueueName The outboundQueueName to set.      */
specifier|public
name|void
name|setOutboundQueueName
parameter_list|(
name|String
name|outboundQueueName
parameter_list|)
block|{
name|this
operator|.
name|outboundQueueName
operator|=
name|outboundQueueName
expr_stmt|;
block|}
block|}
end_class

end_unit

