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
name|java
operator|.
name|lang
operator|.
name|reflect
operator|.
name|Field
import|;
end_import

begin_import
import|import
name|java
operator|.
name|lang
operator|.
name|reflect
operator|.
name|Modifier
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|LinkedHashMap
import|;
end_import

begin_comment
comment|/**  *   * @openwire:marshaller  * @version $Revision: 1.11 $  */
end_comment

begin_class
specifier|abstract
specifier|public
class|class
name|BaseCommand
implements|implements
name|Command
block|{
specifier|protected
name|short
name|commandId
decl_stmt|;
specifier|protected
name|boolean
name|responseRequired
decl_stmt|;
specifier|public
name|void
name|copy
parameter_list|(
name|BaseCommand
name|copy
parameter_list|)
block|{
name|copy
operator|.
name|commandId
operator|=
name|commandId
expr_stmt|;
name|copy
operator|.
name|responseRequired
operator|=
name|responseRequired
expr_stmt|;
block|}
specifier|public
name|boolean
name|isWireFormatInfo
parameter_list|()
block|{
return|return
literal|false
return|;
block|}
specifier|public
name|boolean
name|isBrokerInfo
parameter_list|()
block|{
return|return
literal|false
return|;
block|}
specifier|public
name|boolean
name|isResponse
parameter_list|()
block|{
return|return
literal|false
return|;
block|}
specifier|public
name|boolean
name|isMessageDispatch
parameter_list|()
block|{
return|return
literal|false
return|;
block|}
specifier|public
name|boolean
name|isMessage
parameter_list|()
block|{
return|return
literal|false
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
specifier|public
name|boolean
name|isMessageAck
parameter_list|()
block|{
return|return
literal|false
return|;
block|}
comment|/**      * @openwire:property version=1      */
specifier|public
name|short
name|getCommandId
parameter_list|()
block|{
return|return
name|commandId
return|;
block|}
specifier|public
name|void
name|setCommandId
parameter_list|(
name|short
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
comment|/**      * @openwire:property version=1      */
specifier|public
name|boolean
name|isResponseRequired
parameter_list|()
block|{
return|return
name|responseRequired
return|;
block|}
specifier|public
name|void
name|setResponseRequired
parameter_list|(
name|boolean
name|responseRequired
parameter_list|)
block|{
name|this
operator|.
name|responseRequired
operator|=
name|responseRequired
expr_stmt|;
block|}
specifier|public
name|String
name|toString
parameter_list|()
block|{
name|LinkedHashMap
name|map
init|=
operator|new
name|LinkedHashMap
argument_list|()
decl_stmt|;
name|addFields
argument_list|(
name|map
argument_list|,
name|getClass
argument_list|()
argument_list|)
expr_stmt|;
return|return
name|simpleName
argument_list|(
name|getClass
argument_list|()
argument_list|)
operator|+
literal|" "
operator|+
name|map
return|;
block|}
specifier|public
specifier|static
name|String
name|simpleName
parameter_list|(
name|Class
name|clazz
parameter_list|)
block|{
name|String
name|name
init|=
name|clazz
operator|.
name|getName
argument_list|()
decl_stmt|;
name|int
name|p
init|=
name|name
operator|.
name|lastIndexOf
argument_list|(
literal|"."
argument_list|)
decl_stmt|;
if|if
condition|(
name|p
operator|>=
literal|0
condition|)
block|{
name|name
operator|=
name|name
operator|.
name|substring
argument_list|(
name|p
operator|+
literal|1
argument_list|)
expr_stmt|;
block|}
return|return
name|name
return|;
block|}
specifier|private
name|void
name|addFields
parameter_list|(
name|LinkedHashMap
name|map
parameter_list|,
name|Class
name|clazz
parameter_list|)
block|{
if|if
condition|(
name|clazz
operator|!=
name|BaseCommand
operator|.
name|class
condition|)
name|addFields
argument_list|(
name|map
argument_list|,
name|clazz
operator|.
name|getSuperclass
argument_list|()
argument_list|)
expr_stmt|;
name|Field
index|[]
name|fields
init|=
name|clazz
operator|.
name|getDeclaredFields
argument_list|()
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|fields
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|Field
name|field
init|=
name|fields
index|[
name|i
index|]
decl_stmt|;
if|if
condition|(
name|Modifier
operator|.
name|isStatic
argument_list|(
name|field
operator|.
name|getModifiers
argument_list|()
argument_list|)
operator|||
name|Modifier
operator|.
name|isTransient
argument_list|(
name|field
operator|.
name|getModifiers
argument_list|()
argument_list|)
operator|||
name|Modifier
operator|.
name|isPrivate
argument_list|(
name|field
operator|.
name|getModifiers
argument_list|()
argument_list|)
condition|)
block|{
continue|continue;
block|}
try|try
block|{
name|map
operator|.
name|put
argument_list|(
name|field
operator|.
name|getName
argument_list|()
argument_list|,
name|field
operator|.
name|get
argument_list|(
name|this
argument_list|)
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Throwable
name|e
parameter_list|)
block|{
name|e
operator|.
name|printStackTrace
argument_list|()
expr_stmt|;
block|}
block|}
block|}
block|}
end_class

end_unit

