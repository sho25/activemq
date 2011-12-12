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
name|command
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
name|org
operator|.
name|apache
operator|.
name|activemq
operator|.
name|state
operator|.
name|CommandVisitor
import|;
end_import

begin_comment
comment|/**  * Used to create and destroy destinations on the broker.  *   * @openwire:marshaller code="8"  *   */
end_comment

begin_class
specifier|public
class|class
name|DestinationInfo
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
name|DESTINATION_INFO
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|byte
name|ADD_OPERATION_TYPE
init|=
literal|0
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|byte
name|REMOVE_OPERATION_TYPE
init|=
literal|1
decl_stmt|;
specifier|protected
name|ConnectionId
name|connectionId
decl_stmt|;
specifier|protected
name|ActiveMQDestination
name|destination
decl_stmt|;
specifier|protected
name|byte
name|operationType
decl_stmt|;
specifier|protected
name|long
name|timeout
decl_stmt|;
specifier|protected
name|BrokerId
index|[]
name|brokerPath
decl_stmt|;
specifier|public
name|DestinationInfo
parameter_list|()
block|{     }
specifier|public
name|DestinationInfo
parameter_list|(
name|ConnectionId
name|connectionId
parameter_list|,
name|byte
name|operationType
parameter_list|,
name|ActiveMQDestination
name|destination
parameter_list|)
block|{
name|this
operator|.
name|connectionId
operator|=
name|connectionId
expr_stmt|;
name|this
operator|.
name|operationType
operator|=
name|operationType
expr_stmt|;
name|this
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
specifier|public
name|boolean
name|isAddOperation
parameter_list|()
block|{
return|return
name|operationType
operator|==
name|ADD_OPERATION_TYPE
return|;
block|}
specifier|public
name|boolean
name|isRemoveOperation
parameter_list|()
block|{
return|return
name|operationType
operator|==
name|REMOVE_OPERATION_TYPE
return|;
block|}
comment|/**      * @openwire:property version=1 cache=true      */
specifier|public
name|ConnectionId
name|getConnectionId
parameter_list|()
block|{
return|return
name|connectionId
return|;
block|}
specifier|public
name|void
name|setConnectionId
parameter_list|(
name|ConnectionId
name|connectionId
parameter_list|)
block|{
name|this
operator|.
name|connectionId
operator|=
name|connectionId
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
comment|/**      * @openwire:property version=1      */
specifier|public
name|byte
name|getOperationType
parameter_list|()
block|{
return|return
name|operationType
return|;
block|}
specifier|public
name|void
name|setOperationType
parameter_list|(
name|byte
name|operationType
parameter_list|)
block|{
name|this
operator|.
name|operationType
operator|=
name|operationType
expr_stmt|;
block|}
comment|/**      * @openwire:property version=1      */
specifier|public
name|long
name|getTimeout
parameter_list|()
block|{
return|return
name|timeout
return|;
block|}
specifier|public
name|void
name|setTimeout
parameter_list|(
name|long
name|timeout
parameter_list|)
block|{
name|this
operator|.
name|timeout
operator|=
name|timeout
expr_stmt|;
block|}
comment|/**      * The route of brokers the command has moved through.      *       * @openwire:property version=1 cache=true      */
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
name|Exception
block|{
if|if
condition|(
name|isAddOperation
argument_list|()
condition|)
block|{
return|return
name|visitor
operator|.
name|processAddDestination
argument_list|(
name|this
argument_list|)
return|;
block|}
elseif|else
if|if
condition|(
name|isRemoveOperation
argument_list|()
condition|)
block|{
return|return
name|visitor
operator|.
name|processRemoveDestination
argument_list|(
name|this
argument_list|)
return|;
block|}
throw|throw
operator|new
name|IOException
argument_list|(
literal|"Unknown operation type: "
operator|+
name|getOperationType
argument_list|()
argument_list|)
throw|;
block|}
specifier|public
name|DestinationInfo
name|copy
parameter_list|()
block|{
name|DestinationInfo
name|result
init|=
operator|new
name|DestinationInfo
argument_list|()
decl_stmt|;
name|super
operator|.
name|copy
argument_list|(
name|result
argument_list|)
expr_stmt|;
name|result
operator|.
name|connectionId
operator|=
name|connectionId
expr_stmt|;
name|result
operator|.
name|destination
operator|=
name|destination
expr_stmt|;
name|result
operator|.
name|operationType
operator|=
name|operationType
expr_stmt|;
name|result
operator|.
name|brokerPath
operator|=
name|brokerPath
expr_stmt|;
return|return
name|result
return|;
block|}
block|}
end_class

end_unit

