begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/** *<a href="http://activemq.org">ActiveMQ: The Open Source Message Fabric</a> * * Copyright 2005 (C) LogicBlaze, Inc. http://www.logicblaze.com * * Licensed under the Apache License, Version 2.0 (the "License"); * you may not use this file except in compliance with the License. * You may obtain a copy of the License at * * http://www.apache.org/licenses/LICENSE-2.0 * * Unless required by applicable law or agreed to in writing, software * distributed under the License is distributed on an "AS IS" BASIS, * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. * See the License for the specific language governing permissions and * limitations under the License. * **/
end_comment

begin_package
package|package
name|org
operator|.
name|activemq
operator|.
name|command
package|;
end_package

begin_import
import|import
name|org
operator|.
name|activemq
operator|.
name|state
operator|.
name|CommandVisitor
import|;
end_import

begin_comment
comment|/**  *   * @openwire:marshaller  * @version $Revision: 1.13 $  */
end_comment

begin_class
specifier|public
class|class
name|ProducerInfo
extends|extends
name|BaseCommand
block|{
specifier|public
specifier|static
specifier|final
name|byte
name|DATA_STRUCTURE_TYPE
init|=
name|CommandTypes
operator|.
name|PRODUCER_INFO
decl_stmt|;
specifier|protected
name|ProducerId
name|producerId
decl_stmt|;
specifier|protected
name|ActiveMQDestination
name|destination
decl_stmt|;
specifier|protected
name|BrokerId
index|[]
name|brokerPath
decl_stmt|;
specifier|public
name|ProducerInfo
parameter_list|()
block|{     }
specifier|public
name|ProducerInfo
parameter_list|(
name|ProducerId
name|producerId
parameter_list|)
block|{
name|this
operator|.
name|producerId
operator|=
name|producerId
expr_stmt|;
block|}
specifier|public
name|ProducerInfo
parameter_list|(
name|SessionInfo
name|sessionInfo
parameter_list|,
name|long
name|producerId
parameter_list|)
block|{
name|this
operator|.
name|producerId
operator|=
operator|new
name|ProducerId
argument_list|(
name|sessionInfo
operator|.
name|getSessionId
argument_list|()
argument_list|,
name|producerId
argument_list|)
expr_stmt|;
block|}
specifier|public
name|ProducerInfo
name|copy
parameter_list|()
block|{
name|ProducerInfo
name|info
init|=
operator|new
name|ProducerInfo
argument_list|()
decl_stmt|;
name|copy
argument_list|(
name|info
argument_list|)
expr_stmt|;
return|return
name|info
return|;
block|}
specifier|public
name|void
name|copy
parameter_list|(
name|ProducerInfo
name|info
parameter_list|)
block|{
name|super
operator|.
name|copy
argument_list|(
name|info
argument_list|)
expr_stmt|;
name|info
operator|.
name|producerId
operator|=
name|producerId
expr_stmt|;
name|info
operator|.
name|destination
operator|=
name|destination
expr_stmt|;
block|}
specifier|public
name|byte
name|getDataStructureType
parameter_list|()
block|{
return|return
name|DATA_STRUCTURE_TYPE
return|;
block|}
comment|/**      * @openwire:property version=1 cache=true      */
specifier|public
name|ProducerId
name|getProducerId
parameter_list|()
block|{
return|return
name|producerId
return|;
block|}
specifier|public
name|void
name|setProducerId
parameter_list|(
name|ProducerId
name|producerId
parameter_list|)
block|{
name|this
operator|.
name|producerId
operator|=
name|producerId
expr_stmt|;
block|}
comment|/**      * @openwire:property version=1 cache=true      */
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
name|RemoveInfo
name|createRemoveCommand
parameter_list|()
block|{
name|RemoveInfo
name|command
init|=
operator|new
name|RemoveInfo
argument_list|(
name|getProducerId
argument_list|()
argument_list|)
decl_stmt|;
name|command
operator|.
name|setResponseRequired
argument_list|(
name|isResponseRequired
argument_list|()
argument_list|)
expr_stmt|;
return|return
name|command
return|;
block|}
comment|/**      * The route of brokers the command has moved through.       *       * @openwire:property version=1 cache=true      */
specifier|public
name|BrokerId
index|[]
name|getBrokerPath
parameter_list|()
block|{
return|return
name|brokerPath
return|;
block|}
specifier|public
name|void
name|setBrokerPath
parameter_list|(
name|BrokerId
index|[]
name|brokerPath
parameter_list|)
block|{
name|this
operator|.
name|brokerPath
operator|=
name|brokerPath
expr_stmt|;
block|}
specifier|public
name|Response
name|visit
parameter_list|(
name|CommandVisitor
name|visitor
parameter_list|)
throws|throws
name|Throwable
block|{
return|return
name|visitor
operator|.
name|processAddProducer
argument_list|(
name|this
argument_list|)
return|;
block|}
block|}
end_class

end_unit

