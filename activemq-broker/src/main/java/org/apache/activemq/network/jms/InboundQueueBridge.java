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
name|network
operator|.
name|jms
package|;
end_package

begin_comment
comment|/**  * Create an Inbound Queue Bridge.  By default this class uses the sname name for  * both the inbound and outbound queue.  This behavior can be overridden however  * by using the setter methods to configure both the inbound and outboud queue names  * separately.  *  * @org.apache.xbean.XBean  */
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
name|String
name|localQueueName
decl_stmt|;
comment|/**      * Constructor that takes a foreign destination as an argument      *      * @param inboundQueueName      */
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
name|this
operator|.
name|localQueueName
operator|=
name|inboundQueueName
expr_stmt|;
block|}
comment|/**      * Default Constructor      */
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
comment|/**      * Sets the queue name used for the inbound queue, if the outbound queue      * name has not been set, then this method uses the same name to configure      * the outbound queue name.      *      * @param inboundQueueName The inboundQueueName to set.      */
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
if|if
condition|(
name|this
operator|.
name|localQueueName
operator|==
literal|null
condition|)
block|{
name|this
operator|.
name|localQueueName
operator|=
name|inboundQueueName
expr_stmt|;
block|}
block|}
comment|/**      * @return the localQueueName      */
specifier|public
name|String
name|getLocalQueueName
parameter_list|()
block|{
return|return
name|localQueueName
return|;
block|}
comment|/**      * @param localQueueName the localQueueName to set      */
specifier|public
name|void
name|setLocalQueueName
parameter_list|(
name|String
name|localQueueName
parameter_list|)
block|{
name|this
operator|.
name|localQueueName
operator|=
name|localQueueName
expr_stmt|;
block|}
block|}
end_class

end_unit

