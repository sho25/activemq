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

begin_comment
comment|/**  *   * @openwire:marshaller  * @version $Revision$  */
end_comment

begin_class
specifier|public
class|class
name|SessionId
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
name|SESSION_ID
decl_stmt|;
specifier|protected
name|String
name|connectionId
decl_stmt|;
specifier|protected
name|long
name|sessionId
decl_stmt|;
specifier|protected
specifier|transient
name|int
name|hashCode
decl_stmt|;
specifier|protected
specifier|transient
name|String
name|key
decl_stmt|;
specifier|protected
specifier|transient
name|ConnectionId
name|parentId
decl_stmt|;
specifier|public
name|SessionId
parameter_list|()
block|{             }
specifier|public
name|SessionId
parameter_list|(
name|ConnectionId
name|connectionId
parameter_list|,
name|long
name|sessionId
parameter_list|)
block|{
name|this
operator|.
name|connectionId
operator|=
name|connectionId
operator|.
name|getConnectionId
argument_list|()
expr_stmt|;
name|this
operator|.
name|sessionId
operator|=
name|sessionId
expr_stmt|;
block|}
specifier|public
name|SessionId
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
name|this
operator|.
name|sessionId
operator|=
name|id
operator|.
name|getSessionId
argument_list|()
expr_stmt|;
block|}
specifier|public
name|SessionId
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
name|this
operator|.
name|sessionId
operator|=
name|id
operator|.
name|getSessionId
argument_list|()
expr_stmt|;
block|}
specifier|public
name|SessionId
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
name|this
operator|.
name|sessionId
operator|=
name|id
operator|.
name|getSessionId
argument_list|()
expr_stmt|;
block|}
specifier|public
name|ConnectionId
name|getParentId
parameter_list|()
block|{
if|if
condition|(
name|parentId
operator|==
literal|null
condition|)
block|{
name|parentId
operator|=
operator|new
name|ConnectionId
argument_list|(
name|this
argument_list|)
expr_stmt|;
block|}
return|return
name|parentId
return|;
block|}
specifier|public
name|int
name|hashCode
parameter_list|()
block|{
if|if
condition|(
name|hashCode
operator|==
literal|0
condition|)
block|{
name|hashCode
operator|=
name|connectionId
operator|.
name|hashCode
argument_list|()
operator|^
operator|(
name|int
operator|)
name|sessionId
expr_stmt|;
block|}
return|return
name|hashCode
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
name|SessionId
operator|.
name|class
condition|)
return|return
literal|false
return|;
name|SessionId
name|id
init|=
operator|(
name|SessionId
operator|)
name|o
decl_stmt|;
return|return
name|sessionId
operator|==
name|id
operator|.
name|sessionId
operator|&&
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
comment|/**      * @openwire:property version=1 cache=true      */
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
comment|/**      * @openwire:property version=1      */
specifier|public
name|long
name|getSessionId
parameter_list|()
block|{
return|return
name|sessionId
return|;
block|}
specifier|public
name|void
name|setSessionId
parameter_list|(
name|long
name|sessionId
parameter_list|)
block|{
name|this
operator|.
name|sessionId
operator|=
name|sessionId
expr_stmt|;
block|}
specifier|public
name|String
name|toString
parameter_list|()
block|{
if|if
condition|(
name|key
operator|==
literal|null
condition|)
block|{
name|key
operator|=
name|connectionId
operator|+
literal|":"
operator|+
name|sessionId
expr_stmt|;
block|}
return|return
name|key
return|;
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

