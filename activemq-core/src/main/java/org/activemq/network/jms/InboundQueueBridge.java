begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  *<a href="http://activemq.org">ActiveMQ: The Open Source Message Fabric</a>  *   * Copyright 2005 (C) Simula Labs Inc.  *   * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with  * the License. You may obtain a copy of the License at  *   * http://www.apache.org/licenses/LICENSE-2.0  *   * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on  * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the  * specific language governing permissions and limitations under the License.  *   */
end_comment

begin_package
package|package
name|org
operator|.
name|activemq
operator|.
name|network
operator|.
name|jms
package|;
end_package

begin_comment
comment|/**  * Create an Inbound Queue Bridge  *   * @org.xbean.XBean  *   * @version $Revision: 1.1.1.1 $  */
end_comment

begin_class
specifier|public
class|class
name|InboundQueueBridge
extends|extends
name|QueueBridge
block|{
name|String
name|inboundQueueName
decl_stmt|;
comment|/**      * Constructor that takes a foriegn destination as an argument      * @param inboundQueueName      */
specifier|public
name|InboundQueueBridge
parameter_list|(
name|String
name|inboundQueueName
parameter_list|)
block|{
name|this
operator|.
name|inboundQueueName
operator|=
name|inboundQueueName
expr_stmt|;
block|}
comment|/**      * Default Contructor      */
specifier|public
name|InboundQueueBridge
parameter_list|()
block|{     }
comment|/**      * @return Returns the inboundQueueName.      */
specifier|public
name|String
name|getInboundQueueName
parameter_list|()
block|{
return|return
name|inboundQueueName
return|;
block|}
comment|/**      * @param inboundQueueName The inboundQueueName to set.      */
specifier|public
name|void
name|setInboundQueueName
parameter_list|(
name|String
name|inboundQueueName
parameter_list|)
block|{
name|this
operator|.
name|inboundQueueName
operator|=
name|inboundQueueName
expr_stmt|;
block|}
block|}
end_class

end_unit

