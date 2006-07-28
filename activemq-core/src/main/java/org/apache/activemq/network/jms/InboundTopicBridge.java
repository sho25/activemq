begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  *  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  * http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
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
comment|/**  * Create an Inbound Topic Bridge  *   * @org.apache.xbean.XBean  *   * @version $Revision: 1.1.1.1 $  */
end_comment

begin_class
specifier|public
class|class
name|InboundTopicBridge
extends|extends
name|TopicBridge
block|{
name|String
name|inboundTopicName
decl_stmt|;
comment|/**      * Constructor that takes a foriegn destination as an argument      * @param inboundTopicName      */
specifier|public
name|InboundTopicBridge
parameter_list|(
name|String
name|inboundTopicName
parameter_list|)
block|{
name|this
operator|.
name|inboundTopicName
operator|=
name|inboundTopicName
expr_stmt|;
block|}
comment|/**      * Default Contructor      */
specifier|public
name|InboundTopicBridge
parameter_list|()
block|{     }
comment|/**      * @return Returns the outboundTopicName.      */
specifier|public
name|String
name|getInboundTopicName
parameter_list|()
block|{
return|return
name|inboundTopicName
return|;
block|}
comment|/**      * @param outboundTopicName The outboundTopicName to set.      */
specifier|public
name|void
name|setInboundTopicName
parameter_list|(
name|String
name|inboundTopicName
parameter_list|)
block|{
name|this
operator|.
name|inboundTopicName
operator|=
name|inboundTopicName
expr_stmt|;
block|}
block|}
end_class

end_unit

