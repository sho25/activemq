begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  *  * Copyright 2005-2006 The Apache Software Foundation  *  * Licensed under the Apache License, Version 2.0 (the "License");  * you may not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  * http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
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
comment|/**  * Removes a consumer, producer, session or connection.  *    * @openwire:marshaller code="12"  * @version $Revision$  */
end_comment

begin_class
specifier|public
class|class
name|RemoveInfo
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
name|REMOVE_INFO
decl_stmt|;
specifier|protected
name|DataStructure
name|objectId
decl_stmt|;
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
name|RemoveInfo
parameter_list|()
block|{             }
specifier|public
name|RemoveInfo
parameter_list|(
name|DataStructure
name|objectId
parameter_list|)
block|{
name|this
operator|.
name|objectId
operator|=
name|objectId
expr_stmt|;
block|}
comment|/**      * @openwire:property version=1 cache=true      */
specifier|public
name|DataStructure
name|getObjectId
parameter_list|()
block|{
return|return
name|objectId
return|;
block|}
specifier|public
name|void
name|setObjectId
parameter_list|(
name|DataStructure
name|objectId
parameter_list|)
block|{
name|this
operator|.
name|objectId
operator|=
name|objectId
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
switch|switch
condition|(
name|objectId
operator|.
name|getDataStructureType
argument_list|()
condition|)
block|{
case|case
name|ConnectionId
operator|.
name|DATA_STRUCTURE_TYPE
case|:
return|return
name|visitor
operator|.
name|processRemoveConnection
argument_list|(
operator|(
name|ConnectionId
operator|)
name|objectId
argument_list|)
return|;
case|case
name|SessionId
operator|.
name|DATA_STRUCTURE_TYPE
case|:
return|return
name|visitor
operator|.
name|processRemoveSession
argument_list|(
operator|(
name|SessionId
operator|)
name|objectId
argument_list|)
return|;
case|case
name|ConsumerId
operator|.
name|DATA_STRUCTURE_TYPE
case|:
return|return
name|visitor
operator|.
name|processRemoveConsumer
argument_list|(
operator|(
name|ConsumerId
operator|)
name|objectId
argument_list|)
return|;
case|case
name|ProducerId
operator|.
name|DATA_STRUCTURE_TYPE
case|:
return|return
name|visitor
operator|.
name|processRemoveProducer
argument_list|(
operator|(
name|ProducerId
operator|)
name|objectId
argument_list|)
return|;
default|default:
throw|throw
operator|new
name|IOException
argument_list|(
literal|"Unknown remove command type: "
operator|+
name|objectId
operator|.
name|getDataStructureType
argument_list|()
argument_list|)
throw|;
block|}
block|}
comment|/**      * Returns true if this event is for a removed connection      */
specifier|public
name|boolean
name|isConnectionRemove
parameter_list|()
block|{
return|return
name|objectId
operator|.
name|getDataStructureType
argument_list|()
operator|==
name|ConnectionId
operator|.
name|DATA_STRUCTURE_TYPE
return|;
block|}
comment|/**      * Returns true if this event is for a removed session      */
specifier|public
name|boolean
name|isSessionRemove
parameter_list|()
block|{
return|return
name|objectId
operator|.
name|getDataStructureType
argument_list|()
operator|==
name|SessionId
operator|.
name|DATA_STRUCTURE_TYPE
return|;
block|}
comment|/**      * Returns true if this event is for a removed consumer      */
specifier|public
name|boolean
name|isConsumerRemove
parameter_list|()
block|{
return|return
name|objectId
operator|.
name|getDataStructureType
argument_list|()
operator|==
name|ConsumerId
operator|.
name|DATA_STRUCTURE_TYPE
return|;
block|}
comment|/**      * Returns true if this event is for a removed producer      */
specifier|public
name|boolean
name|isProducerRemove
parameter_list|()
block|{
return|return
name|objectId
operator|.
name|getDataStructureType
argument_list|()
operator|==
name|ProducerId
operator|.
name|DATA_STRUCTURE_TYPE
return|;
block|}
block|}
end_class

end_unit

