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

begin_comment
comment|/**  *   * @openwire:marshaller  * @version $Revision$  */
end_comment

begin_class
specifier|public
class|class
name|ConnectionId
implements|implements
name|DataStructure
block|{
specifier|public
specifier|static
specifier|final
name|byte
name|DATA_STRUCTURE_TYPE
init|=
name|CommandTypes
operator|.
name|CONNECTION_ID
decl_stmt|;
specifier|protected
name|String
name|connectionId
decl_stmt|;
specifier|public
name|ConnectionId
parameter_list|()
block|{             }
specifier|public
name|ConnectionId
parameter_list|(
name|String
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
name|ConnectionId
parameter_list|(
name|ConnectionId
name|id
parameter_list|)
block|{
name|this
operator|.
name|connectionId
operator|=
name|id
operator|.
name|getConnectionId
argument_list|()
expr_stmt|;
block|}
specifier|public
name|ConnectionId
parameter_list|(
name|SessionId
name|id
parameter_list|)
block|{
name|this
operator|.
name|connectionId
operator|=
name|id
operator|.
name|getConnectionId
argument_list|()
expr_stmt|;
block|}
specifier|public
name|ConnectionId
parameter_list|(
name|ProducerId
name|id
parameter_list|)
block|{
name|this
operator|.
name|connectionId
operator|=
name|id
operator|.
name|getConnectionId
argument_list|()
expr_stmt|;
block|}
specifier|public
name|ConnectionId
parameter_list|(
name|ConsumerId
name|id
parameter_list|)
block|{
name|this
operator|.
name|connectionId
operator|=
name|id
operator|.
name|getConnectionId
argument_list|()
expr_stmt|;
block|}
specifier|public
name|int
name|hashCode
parameter_list|()
block|{
return|return
name|connectionId
operator|.
name|hashCode
argument_list|()
return|;
block|}
specifier|public
name|boolean
name|equals
parameter_list|(
name|Object
name|o
parameter_list|)
block|{
if|if
condition|(
name|this
operator|==
name|o
condition|)
return|return
literal|true
return|;
if|if
condition|(
name|o
operator|==
literal|null
operator|||
name|o
operator|.
name|getClass
argument_list|()
operator|!=
name|ConnectionId
operator|.
name|class
condition|)
return|return
literal|false
return|;
name|ConnectionId
name|id
init|=
operator|(
name|ConnectionId
operator|)
name|o
decl_stmt|;
return|return
name|connectionId
operator|.
name|equals
argument_list|(
name|id
operator|.
name|connectionId
argument_list|)
return|;
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
name|String
name|toString
parameter_list|()
block|{
return|return
name|connectionId
return|;
block|}
comment|/**      * @openwire:property version=1      */
specifier|public
name|String
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
name|String
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
name|boolean
name|isMarshallAware
parameter_list|()
block|{
return|return
literal|false
return|;
block|}
block|}
end_class

end_unit

