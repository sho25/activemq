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
comment|/**  * Represents a partial command; a large command that has been split up into  * pieces.  *  * @openwire:marshaller code="60"  *  */
end_comment

begin_class
specifier|public
class|class
name|PartialCommand
implements|implements
name|Command
block|{
specifier|public
specifier|static
specifier|final
name|byte
name|DATA_STRUCTURE_TYPE
init|=
name|CommandTypes
operator|.
name|PARTIAL_COMMAND
decl_stmt|;
specifier|private
name|int
name|commandId
decl_stmt|;
specifier|private
name|byte
index|[]
name|data
decl_stmt|;
specifier|private
specifier|transient
name|Endpoint
name|from
decl_stmt|;
specifier|private
specifier|transient
name|Endpoint
name|to
decl_stmt|;
specifier|public
name|PartialCommand
parameter_list|()
block|{     }
annotation|@
name|Override
specifier|public
name|byte
name|getDataStructureType
parameter_list|()
block|{
return|return
name|DATA_STRUCTURE_TYPE
return|;
block|}
comment|/**      * @openwire:property version=1      */
annotation|@
name|Override
specifier|public
name|int
name|getCommandId
parameter_list|()
block|{
return|return
name|commandId
return|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|setCommandId
parameter_list|(
name|int
name|commandId
parameter_list|)
block|{
name|this
operator|.
name|commandId
operator|=
name|commandId
expr_stmt|;
block|}
comment|/**      * The data for this part of the command      *      * @openwire:property version=1 mandatory=true      */
specifier|public
name|byte
index|[]
name|getData
parameter_list|()
block|{
return|return
name|data
return|;
block|}
specifier|public
name|void
name|setData
parameter_list|(
name|byte
index|[]
name|data
parameter_list|)
block|{
name|this
operator|.
name|data
operator|=
name|data
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|Endpoint
name|getFrom
parameter_list|()
block|{
return|return
name|from
return|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|setFrom
parameter_list|(
name|Endpoint
name|from
parameter_list|)
block|{
name|this
operator|.
name|from
operator|=
name|from
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|Endpoint
name|getTo
parameter_list|()
block|{
return|return
name|to
return|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|setTo
parameter_list|(
name|Endpoint
name|to
parameter_list|)
block|{
name|this
operator|.
name|to
operator|=
name|to
expr_stmt|;
block|}
annotation|@
name|Override
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
throw|throw
operator|new
name|IllegalStateException
argument_list|(
literal|"The transport layer should filter out PartialCommand instances but received: "
operator|+
name|this
argument_list|)
throw|;
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|isResponseRequired
parameter_list|()
block|{
return|return
literal|false
return|;
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|isResponse
parameter_list|()
block|{
return|return
literal|false
return|;
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|isBrokerInfo
parameter_list|()
block|{
return|return
literal|false
return|;
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|isMessageDispatch
parameter_list|()
block|{
return|return
literal|false
return|;
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|isMessage
parameter_list|()
block|{
return|return
literal|false
return|;
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|isMessageAck
parameter_list|()
block|{
return|return
literal|false
return|;
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|isMessageDispatchNotification
parameter_list|()
block|{
return|return
literal|false
return|;
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|isShutdownInfo
parameter_list|()
block|{
return|return
literal|false
return|;
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|isConnectionControl
parameter_list|()
block|{
return|return
literal|false
return|;
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|isConsumerControl
parameter_list|()
block|{
return|return
literal|false
return|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|setResponseRequired
parameter_list|(
name|boolean
name|responseRequired
parameter_list|)
block|{     }
annotation|@
name|Override
specifier|public
name|boolean
name|isWireFormatInfo
parameter_list|()
block|{
return|return
literal|false
return|;
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|isMarshallAware
parameter_list|()
block|{
return|return
literal|false
return|;
block|}
annotation|@
name|Override
specifier|public
name|String
name|toString
parameter_list|()
block|{
name|int
name|size
init|=
literal|0
decl_stmt|;
if|if
condition|(
name|data
operator|!=
literal|null
condition|)
block|{
name|size
operator|=
name|data
operator|.
name|length
expr_stmt|;
block|}
return|return
literal|"PartialCommand[id: "
operator|+
name|commandId
operator|+
literal|" data: "
operator|+
name|size
operator|+
literal|" byte(s)]"
return|;
block|}
block|}
end_class

end_unit

