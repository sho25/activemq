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
comment|/**  * Create an Inbound Topic Bridge.  By default this class uses the topic name for  * both the inbound and outbound topic.  This behavior can be overridden however  * by using the setter methods to configure both the inbound and outboud topic names  * separately.  *  * @org.apache.xbean.XBean  */
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
name|String
name|localTopicName
decl_stmt|;
comment|/**      * Constructor that takes a foreign destination as an argument      *      * @param inboundTopicName      */
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
name|this
operator|.
name|localTopicName
operator|=
name|inboundTopicName
expr_stmt|;
block|}
comment|/**      * Default Constructor      */
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
comment|/**      * Sets the topic name used for the inbound topic, if the outbound topic      * name has not been set, then this method uses the same name to configure      * the outbound topic name.      *      * @param inboundTopicName      */
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
name|inboundTopicName
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

