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

begin_comment
comment|/**  * Keeps track of the MQTT client subscription so that acking is correctly done.  */
end_comment

begin_class
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
name|ActiveMQDestination
name|destination
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
block|}
name|MessageAck
name|createMessageAck
parameter_list|(
name|MessageDispatch
name|md
parameter_list|)
block|{
switch|switch
condition|(
name|qos
condition|)
block|{
case|case
name|AT_MOST_ONCE
case|:
block|{
return|return
literal|null
return|;
block|}
block|}
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
specifier|public
name|void
name|setDestination
parameter_list|(
name|ActiveMQDestination
name|destination
parameter_list|)
block|{
name|this
operator|.
name|destination
operator|=
name|destination
expr_stmt|;
block|}
specifier|public
name|ActiveMQDestination
name|getDestination
parameter_list|()
block|{
return|return
name|destination
return|;
block|}
specifier|public
name|ConsumerInfo
name|getConsumerInfo
parameter_list|()
block|{
return|return
name|consumerInfo
return|;
block|}
block|}
end_class

end_unit

