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
name|broker
operator|.
name|jmx
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
name|ProducerInfo
import|;
end_import

begin_class
specifier|public
class|class
name|ProducerView
implements|implements
name|ProducerViewMBean
block|{
specifier|protected
specifier|final
name|ProducerInfo
name|info
decl_stmt|;
specifier|protected
specifier|final
name|String
name|clientId
decl_stmt|;
specifier|protected
specifier|final
name|String
name|userName
decl_stmt|;
specifier|protected
specifier|final
name|ManagedRegionBroker
name|broker
decl_stmt|;
specifier|protected
name|ActiveMQDestination
name|lastUsedDestination
decl_stmt|;
specifier|public
name|ProducerView
parameter_list|(
name|ProducerInfo
name|info
parameter_list|,
name|String
name|clientId
parameter_list|,
name|String
name|userName
parameter_list|,
name|ManagedRegionBroker
name|broker
parameter_list|)
block|{
name|this
operator|.
name|info
operator|=
name|info
expr_stmt|;
name|this
operator|.
name|clientId
operator|=
name|clientId
expr_stmt|;
name|this
operator|.
name|userName
operator|=
name|userName
expr_stmt|;
name|this
operator|.
name|broker
operator|=
name|broker
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|String
name|getClientId
parameter_list|()
block|{
return|return
name|this
operator|.
name|clientId
return|;
block|}
annotation|@
name|Override
specifier|public
name|String
name|getConnectionId
parameter_list|()
block|{
if|if
condition|(
name|info
operator|!=
literal|null
condition|)
block|{
return|return
name|info
operator|.
name|getProducerId
argument_list|()
operator|.
name|getConnectionId
argument_list|()
return|;
block|}
return|return
literal|"NOTSET"
return|;
block|}
annotation|@
name|Override
specifier|public
name|long
name|getSessionId
parameter_list|()
block|{
if|if
condition|(
name|info
operator|!=
literal|null
condition|)
block|{
return|return
name|info
operator|.
name|getProducerId
argument_list|()
operator|.
name|getSessionId
argument_list|()
return|;
block|}
return|return
literal|0
return|;
block|}
annotation|@
name|Override
specifier|public
name|String
name|getProducerId
parameter_list|()
block|{
if|if
condition|(
name|info
operator|!=
literal|null
condition|)
block|{
return|return
name|info
operator|.
name|getProducerId
argument_list|()
operator|.
name|toString
argument_list|()
return|;
block|}
return|return
literal|"NOTSET"
return|;
block|}
annotation|@
name|Override
specifier|public
name|String
name|getDestinationName
parameter_list|()
block|{
if|if
condition|(
name|info
operator|!=
literal|null
operator|&&
name|info
operator|.
name|getDestination
argument_list|()
operator|!=
literal|null
condition|)
block|{
name|ActiveMQDestination
name|dest
init|=
name|info
operator|.
name|getDestination
argument_list|()
decl_stmt|;
return|return
name|dest
operator|.
name|getPhysicalName
argument_list|()
return|;
block|}
elseif|else
if|if
condition|(
name|this
operator|.
name|lastUsedDestination
operator|!=
literal|null
condition|)
block|{
return|return
name|this
operator|.
name|lastUsedDestination
operator|.
name|getPhysicalName
argument_list|()
return|;
block|}
return|return
literal|"NOTSET"
return|;
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|isDestinationQueue
parameter_list|()
block|{
if|if
condition|(
name|info
operator|!=
literal|null
condition|)
block|{
if|if
condition|(
name|info
operator|.
name|getDestination
argument_list|()
operator|!=
literal|null
condition|)
block|{
name|ActiveMQDestination
name|dest
init|=
name|info
operator|.
name|getDestination
argument_list|()
decl_stmt|;
return|return
name|dest
operator|.
name|isQueue
argument_list|()
return|;
block|}
elseif|else
if|if
condition|(
name|lastUsedDestination
operator|!=
literal|null
condition|)
block|{
return|return
name|lastUsedDestination
operator|.
name|isQueue
argument_list|()
return|;
block|}
block|}
return|return
literal|false
return|;
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|isDestinationTopic
parameter_list|()
block|{
if|if
condition|(
name|info
operator|!=
literal|null
condition|)
block|{
if|if
condition|(
name|info
operator|.
name|getDestination
argument_list|()
operator|!=
literal|null
condition|)
block|{
name|ActiveMQDestination
name|dest
init|=
name|info
operator|.
name|getDestination
argument_list|()
decl_stmt|;
return|return
name|dest
operator|.
name|isTopic
argument_list|()
return|;
block|}
elseif|else
if|if
condition|(
name|lastUsedDestination
operator|!=
literal|null
condition|)
block|{
return|return
name|lastUsedDestination
operator|.
name|isTopic
argument_list|()
return|;
block|}
block|}
return|return
literal|false
return|;
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|isDestinationTemporary
parameter_list|()
block|{
if|if
condition|(
name|info
operator|!=
literal|null
condition|)
block|{
if|if
condition|(
name|info
operator|.
name|getDestination
argument_list|()
operator|!=
literal|null
condition|)
block|{
name|ActiveMQDestination
name|dest
init|=
name|info
operator|.
name|getDestination
argument_list|()
decl_stmt|;
return|return
name|dest
operator|.
name|isTemporary
argument_list|()
return|;
block|}
elseif|else
if|if
condition|(
name|lastUsedDestination
operator|!=
literal|null
condition|)
block|{
return|return
name|lastUsedDestination
operator|.
name|isTemporary
argument_list|()
return|;
block|}
block|}
return|return
literal|false
return|;
block|}
annotation|@
name|Override
specifier|public
name|int
name|getProducerWindowSize
parameter_list|()
block|{
if|if
condition|(
name|info
operator|!=
literal|null
condition|)
block|{
return|return
name|info
operator|.
name|getWindowSize
argument_list|()
return|;
block|}
return|return
literal|0
return|;
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|isDispatchAsync
parameter_list|()
block|{
if|if
condition|(
name|info
operator|!=
literal|null
condition|)
block|{
return|return
name|info
operator|.
name|isDispatchAsync
argument_list|()
return|;
block|}
return|return
literal|false
return|;
block|}
comment|/**      * @return pretty print      */
specifier|public
name|String
name|toString
parameter_list|()
block|{
return|return
literal|"ProducerView: "
operator|+
name|getClientId
argument_list|()
operator|+
literal|":"
operator|+
name|getConnectionId
argument_list|()
return|;
block|}
comment|/**      * Set the last used Destination name for a Dynamic Destination Producer.      */
name|void
name|setLastUsedDestinationName
parameter_list|(
name|ActiveMQDestination
name|destinationName
parameter_list|)
block|{
name|this
operator|.
name|lastUsedDestination
operator|=
name|destinationName
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|String
name|getUserName
parameter_list|()
block|{
return|return
name|userName
return|;
block|}
block|}
end_class

end_unit

