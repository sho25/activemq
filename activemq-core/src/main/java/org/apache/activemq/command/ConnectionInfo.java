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
comment|/**  *   * @openwire:marshaller code="3"  * @version $Revision: 1.11 $  */
end_comment

begin_class
specifier|public
class|class
name|ConnectionInfo
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
name|CONNECTION_INFO
decl_stmt|;
specifier|protected
name|ConnectionId
name|connectionId
decl_stmt|;
specifier|protected
name|String
name|clientId
decl_stmt|;
specifier|protected
name|String
name|userName
decl_stmt|;
specifier|protected
name|String
name|password
decl_stmt|;
specifier|protected
name|BrokerId
index|[]
name|brokerPath
decl_stmt|;
specifier|protected
name|boolean
name|brokerMasterConnector
decl_stmt|;
specifier|protected
name|boolean
name|manageable
decl_stmt|;
specifier|public
name|ConnectionInfo
parameter_list|()
block|{             }
specifier|public
name|ConnectionInfo
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
name|void
name|copy
parameter_list|(
name|ConnectionInfo
name|copy
parameter_list|)
block|{
name|super
operator|.
name|copy
argument_list|(
name|copy
argument_list|)
expr_stmt|;
name|copy
operator|.
name|clientId
operator|=
name|clientId
expr_stmt|;
name|copy
operator|.
name|userName
operator|=
name|userName
expr_stmt|;
name|copy
operator|.
name|password
operator|=
name|password
expr_stmt|;
name|copy
operator|.
name|brokerPath
operator|=
name|brokerPath
expr_stmt|;
name|copy
operator|.
name|brokerMasterConnector
operator|=
name|brokerMasterConnector
expr_stmt|;
name|copy
operator|.
name|manageable
operator|=
name|manageable
expr_stmt|;
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
comment|/**      * @openwire:property version=1      */
specifier|public
name|String
name|getClientId
parameter_list|()
block|{
return|return
name|clientId
return|;
block|}
specifier|public
name|void
name|setClientId
parameter_list|(
name|String
name|clientId
parameter_list|)
block|{
name|this
operator|.
name|clientId
operator|=
name|clientId
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
name|getConnectionId
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
comment|/**      * @openwire:property version=1      */
specifier|public
name|String
name|getPassword
parameter_list|()
block|{
return|return
name|password
return|;
block|}
specifier|public
name|void
name|setPassword
parameter_list|(
name|String
name|password
parameter_list|)
block|{
name|this
operator|.
name|password
operator|=
name|password
expr_stmt|;
block|}
comment|/**      * @openwire:property version=1      */
specifier|public
name|String
name|getUserName
parameter_list|()
block|{
return|return
name|userName
return|;
block|}
specifier|public
name|void
name|setUserName
parameter_list|(
name|String
name|userName
parameter_list|)
block|{
name|this
operator|.
name|userName
operator|=
name|userName
expr_stmt|;
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
name|Exception
block|{
return|return
name|visitor
operator|.
name|processAddConnection
argument_list|(
name|this
argument_list|)
return|;
block|}
comment|/**      * @openwire:property version=1      */
specifier|public
name|boolean
name|isBrokerMasterConnector
parameter_list|()
block|{
return|return
name|brokerMasterConnector
return|;
block|}
comment|/**      * @param brokerMasterConnector The brokerMasterConnector to set.      */
specifier|public
name|void
name|setBrokerMasterConnector
parameter_list|(
name|boolean
name|slaveBroker
parameter_list|)
block|{
name|this
operator|.
name|brokerMasterConnector
operator|=
name|slaveBroker
expr_stmt|;
block|}
comment|/**      * @openwire:property version=1      */
specifier|public
name|boolean
name|isManageable
parameter_list|()
block|{
return|return
name|manageable
return|;
block|}
comment|/**      * @param manageable The manageable to set.      */
specifier|public
name|void
name|setManageable
parameter_list|(
name|boolean
name|manageable
parameter_list|)
block|{
name|this
operator|.
name|manageable
operator|=
name|manageable
expr_stmt|;
block|}
block|}
end_class

end_unit

