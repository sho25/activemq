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
name|transport
operator|.
name|mqtt
package|;
end_package

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|IOException
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|zip
operator|.
name|DataFormatException
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|jms
operator|.
name|JMSException
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|activemq
operator|.
name|command
operator|.
name|ActiveMQDestination
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|activemq
operator|.
name|command
operator|.
name|ActiveMQMessage
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|activemq
operator|.
name|command
operator|.
name|ConsumerInfo
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|activemq
operator|.
name|command
operator|.
name|MessageAck
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|activemq
operator|.
name|command
operator|.
name|MessageDispatch
import|;
end_import

begin_import
import|import
name|org
operator|.
name|fusesource
operator|.
name|mqtt
operator|.
name|client
operator|.
name|QoS
import|;
end_import

begin_import
import|import
name|org
operator|.
name|fusesource
operator|.
name|mqtt
operator|.
name|codec
operator|.
name|PUBLISH
import|;
end_import

begin_comment
comment|/**  * Keeps track of the MQTT client subscription so that acking is correctly done.  */
end_comment

begin_class
specifier|public
class|class
name|MQTTSubscription
block|{
specifier|private
specifier|final
name|MQTTProtocolConverter
name|protocolConverter
decl_stmt|;
specifier|private
specifier|final
name|ConsumerInfo
name|consumerInfo
decl_stmt|;
specifier|private
specifier|final
name|String
name|topicName
decl_stmt|;
specifier|private
specifier|final
name|QoS
name|qos
decl_stmt|;
specifier|public
name|MQTTSubscription
parameter_list|(
name|MQTTProtocolConverter
name|protocolConverter
parameter_list|,
name|String
name|topicName
parameter_list|,
name|QoS
name|qos
parameter_list|,
name|ConsumerInfo
name|consumerInfo
parameter_list|)
block|{
name|this
operator|.
name|protocolConverter
operator|=
name|protocolConverter
expr_stmt|;
name|this
operator|.
name|consumerInfo
operator|=
name|consumerInfo
expr_stmt|;
name|this
operator|.
name|qos
operator|=
name|qos
expr_stmt|;
name|this
operator|.
name|topicName
operator|=
name|topicName
expr_stmt|;
block|}
comment|/**      * Create a {@link MessageAck} that will acknowledge the given {@link MessageDispatch}.      *      * @param md      *        the {@link MessageDispatch} to acknowledge.      *      * @return a new {@link MessageAck} command to acknowledge the message.      */
specifier|public
name|MessageAck
name|createMessageAck
parameter_list|(
name|MessageDispatch
name|md
parameter_list|)
block|{
return|return
operator|new
name|MessageAck
argument_list|(
name|md
argument_list|,
name|MessageAck
operator|.
name|STANDARD_ACK_TYPE
argument_list|,
literal|1
argument_list|)
return|;
block|}
comment|/**      * Creates a PUBLISH command that can be sent to a remote client from an      * incoming {@link ActiveMQMessage} instance.      *      * @param message      *        the message to convert to a PUBLISH command.      *      * @return a new PUBLISH command that is populated from the {@link ActiveMQMessage}.      *      * @throws DataFormatException      * @throws IOException      * @throws JMSException      */
specifier|public
name|PUBLISH
name|createPublish
parameter_list|(
name|ActiveMQMessage
name|message
parameter_list|)
throws|throws
name|DataFormatException
throws|,
name|IOException
throws|,
name|JMSException
block|{
name|PUBLISH
name|publish
init|=
name|protocolConverter
operator|.
name|convertMessage
argument_list|(
name|message
argument_list|)
decl_stmt|;
if|if
condition|(
name|publish
operator|.
name|qos
argument_list|()
operator|.
name|ordinal
argument_list|()
operator|>
name|this
operator|.
name|qos
operator|.
name|ordinal
argument_list|()
condition|)
block|{
name|publish
operator|.
name|qos
argument_list|(
name|this
operator|.
name|qos
argument_list|)
expr_stmt|;
block|}
switch|switch
condition|(
name|publish
operator|.
name|qos
argument_list|()
condition|)
block|{
case|case
name|AT_LEAST_ONCE
case|:
case|case
name|EXACTLY_ONCE
case|:
comment|// set packet id, and optionally dup flag
name|protocolConverter
operator|.
name|getPacketIdGenerator
argument_list|()
operator|.
name|setPacketId
argument_list|(
name|protocolConverter
operator|.
name|getClientId
argument_list|()
argument_list|,
name|this
argument_list|,
name|message
argument_list|,
name|publish
argument_list|)
expr_stmt|;
case|case
name|AT_MOST_ONCE
case|:
block|}
return|return
name|publish
return|;
block|}
comment|/**      * Given a PUBLISH command determine if it will expect an ACK based on the      * QoS of the Publish command and the QoS of this subscription.      *      * @param publish      *        The publish command to inspect.      *      * @return true if the client will expect an PUBACK for this PUBLISH.      */
specifier|public
name|boolean
name|expectAck
parameter_list|(
name|PUBLISH
name|publish
parameter_list|)
block|{
name|QoS
name|publishQoS
init|=
name|publish
operator|.
name|qos
argument_list|()
decl_stmt|;
if|if
condition|(
name|publishQoS
operator|.
name|compareTo
argument_list|(
name|this
operator|.
name|qos
argument_list|)
operator|>
literal|0
condition|)
block|{
name|publishQoS
operator|=
name|this
operator|.
name|qos
expr_stmt|;
block|}
return|return
operator|!
name|publishQoS
operator|.
name|equals
argument_list|(
name|QoS
operator|.
name|AT_MOST_ONCE
argument_list|)
return|;
block|}
comment|/**      * @returns the original topic name value the client used when subscribing.      */
specifier|public
name|String
name|getTopicName
parameter_list|()
block|{
return|return
name|this
operator|.
name|topicName
return|;
block|}
comment|/**      * The real {@link ActiveMQDestination} that this subscription is assigned.      *      * @return the real {@link ActiveMQDestination} assigned to this subscription.      */
specifier|public
name|ActiveMQDestination
name|getDestination
parameter_list|()
block|{
return|return
name|consumerInfo
operator|.
name|getDestination
argument_list|()
return|;
block|}
comment|/**      * Gets the {@link ConsumerInfo} that describes the subscription sent to ActiveMQ.      *      * @return the {@link ConsumerInfo} used to create this subscription.      */
specifier|public
name|ConsumerInfo
name|getConsumerInfo
parameter_list|()
block|{
return|return
name|consumerInfo
return|;
block|}
comment|/**      * @return the assigned QoS value for this subscription.      */
specifier|public
name|QoS
name|getQoS
parameter_list|()
block|{
return|return
name|qos
return|;
block|}
annotation|@
name|Override
specifier|public
name|String
name|toString
parameter_list|()
block|{
return|return
literal|"MQTT Sub: topic["
operator|+
name|topicName
operator|+
literal|"] -> ["
operator|+
name|consumerInfo
operator|.
name|getDestination
argument_list|()
operator|+
literal|"]"
return|;
block|}
block|}
end_class

end_unit

