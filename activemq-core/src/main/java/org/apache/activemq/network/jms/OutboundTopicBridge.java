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
comment|/**  * Create an Outbound Topic Bridge  *   * @org.apache.xbean.XBean  *   * @version $Revision: 1.1.1.1 $  */
end_comment

begin_class
specifier|public
class|class
name|OutboundTopicBridge
extends|extends
name|TopicBridge
block|{
name|String
name|outboundTopicName
decl_stmt|;
name|String
name|localTopicName
decl_stmt|;
comment|/**      * Constructor that takes a foreign destination as an argument      *       * @param outboundTopicName      */
specifier|public
name|OutboundTopicBridge
parameter_list|(
name|String
name|outboundTopicName
parameter_list|)
block|{
name|this
operator|.
name|outboundTopicName
operator|=
name|outboundTopicName
expr_stmt|;
name|this
operator|.
name|localTopicName
operator|=
name|outboundTopicName
expr_stmt|;
block|}
comment|/**      * Default Contructor      */
specifier|public
name|OutboundTopicBridge
parameter_list|()
block|{     }
comment|/**      * @return Returns the outboundTopicName.      */
specifier|public
name|String
name|getOutboundTopicName
parameter_list|()
block|{
return|return
name|outboundTopicName
return|;
block|}
comment|/**      * @param outboundTopicName The outboundTopicName to set.      */
specifier|public
name|void
name|setOutboundTopicName
parameter_list|(
name|String
name|outboundTopicName
parameter_list|)
block|{
name|this
operator|.
name|outboundTopicName
operator|=
name|outboundTopicName
expr_stmt|;
if|if
condition|(
name|this
operator|.
name|localTopicName
operator|==
literal|null
condition|)
block|{
name|this
operator|.
name|localTopicName
operator|=
name|outboundTopicName
expr_stmt|;
block|}
block|}
comment|/**      * @return the localTopicName      */
specifier|public
name|String
name|getLocalTopicName
parameter_list|()
block|{
return|return
name|localTopicName
return|;
block|}
comment|/**      * @param localTopicName the localTopicName to set      */
specifier|public
name|void
name|setLocalTopicName
parameter_list|(
name|String
name|localTopicName
parameter_list|)
block|{
name|this
operator|.
name|localTopicName
operator|=
name|localTopicName
expr_stmt|;
block|}
block|}
end_class

end_unit

