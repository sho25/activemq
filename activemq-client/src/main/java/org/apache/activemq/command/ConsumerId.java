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

begin_comment
comment|/**  * @openwire:marshaller code="122"  *   */
end_comment

begin_class
specifier|public
class|class
name|ConsumerId
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
name|CONSUMER_ID
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
name|long
name|value
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
name|SessionId
name|parentId
decl_stmt|;
specifier|public
name|ConsumerId
parameter_list|()
block|{     }
specifier|public
name|ConsumerId
parameter_list|(
name|String
name|str
parameter_list|)
block|{
if|if
condition|(
name|str
operator|!=
literal|null
condition|)
block|{
name|String
index|[]
name|splits
init|=
name|str
operator|.
name|split
argument_list|(
literal|":"
argument_list|)
decl_stmt|;
if|if
condition|(
name|splits
operator|!=
literal|null
operator|&&
name|splits
operator|.
name|length
operator|>=
literal|3
condition|)
block|{
name|this
operator|.
name|connectionId
operator|=
name|splits
index|[
literal|0
index|]
expr_stmt|;
name|this
operator|.
name|sessionId
operator|=
name|Long
operator|.
name|parseLong
argument_list|(
name|splits
index|[
literal|1
index|]
argument_list|)
expr_stmt|;
name|this
operator|.
name|value
operator|=
name|Long
operator|.
name|parseLong
argument_list|(
name|splits
index|[
literal|2
index|]
argument_list|)
expr_stmt|;
block|}
block|}
block|}
specifier|public
name|ConsumerId
parameter_list|(
name|SessionId
name|sessionId
parameter_list|,
name|long
name|consumerId
parameter_list|)
block|{
name|this
operator|.
name|connectionId
operator|=
name|sessionId
operator|.
name|getConnectionId
argument_list|()
expr_stmt|;
name|this
operator|.
name|sessionId
operator|=
name|sessionId
operator|.
name|getValue
argument_list|()
expr_stmt|;
name|this
operator|.
name|value
operator|=
name|consumerId
expr_stmt|;
block|}
specifier|public
name|ConsumerId
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
name|this
operator|.
name|value
operator|=
name|id
operator|.
name|getValue
argument_list|()
expr_stmt|;
block|}
specifier|public
name|SessionId
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
name|SessionId
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
operator|^
operator|(
name|int
operator|)
name|value
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
block|{
return|return
literal|true
return|;
block|}
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
name|ConsumerId
operator|.
name|class
condition|)
block|{
return|return
literal|false
return|;
block|}
name|ConsumerId
name|id
init|=
operator|(
name|ConsumerId
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
name|value
operator|==
name|id
operator|.
name|value
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
operator|+
literal|":"
operator|+
name|value
expr_stmt|;
block|}
return|return
name|key
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
comment|/**      * @openwire:property version=1      */
specifier|public
name|long
name|getValue
parameter_list|()
block|{
return|return
name|value
return|;
block|}
specifier|public
name|void
name|setValue
parameter_list|(
name|long
name|consumerId
parameter_list|)
block|{
name|this
operator|.
name|value
operator|=
name|consumerId
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

