begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  *<a href="http://activemq.org">ActiveMQ: The Open Source Message Fabric</a>  *  * Copyright 2005 (C) LogicBlaze, Inc. http://www.logicblaze.com  *  * Licensed under the Apache License, Version 2.0 (the "License");  * you may not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  * http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  *  **/
end_comment

begin_package
package|package
name|org
operator|.
name|apache
operator|.
name|activemq
operator|.
name|openwire
operator|.
name|tool
package|;
end_package

begin_import
import|import
name|org
operator|.
name|codehaus
operator|.
name|gram
operator|.
name|GramSupport
import|;
end_import

begin_import
import|import
name|org
operator|.
name|codehaus
operator|.
name|jam
operator|.
name|JAnnotationValue
import|;
end_import

begin_import
import|import
name|org
operator|.
name|codehaus
operator|.
name|jam
operator|.
name|JClass
import|;
end_import

begin_import
import|import
name|org
operator|.
name|codehaus
operator|.
name|jam
operator|.
name|JField
import|;
end_import

begin_import
import|import
name|org
operator|.
name|codehaus
operator|.
name|jam
operator|.
name|JMethod
import|;
end_import

begin_import
import|import
name|org
operator|.
name|codehaus
operator|.
name|jam
operator|.
name|JProperty
import|;
end_import

begin_comment
comment|/**  * @version $Revision$  */
end_comment

begin_class
specifier|public
specifier|abstract
class|class
name|OpenWireScript
extends|extends
name|GramSupport
block|{
specifier|public
name|boolean
name|isValidProperty
parameter_list|(
name|JProperty
name|it
parameter_list|)
block|{
name|JMethod
name|getter
init|=
name|it
operator|.
name|getGetter
argument_list|()
decl_stmt|;
return|return
name|getter
operator|!=
literal|null
operator|&&
name|it
operator|.
name|getSetter
argument_list|()
operator|!=
literal|null
operator|&&
name|getter
operator|.
name|isStatic
argument_list|()
operator|==
literal|false
operator|&&
name|getter
operator|.
name|getAnnotation
argument_list|(
literal|"openwire:property"
argument_list|)
operator|!=
literal|null
return|;
block|}
specifier|public
name|boolean
name|isCachedProperty
parameter_list|(
name|JProperty
name|it
parameter_list|)
block|{
name|JMethod
name|getter
init|=
name|it
operator|.
name|getGetter
argument_list|()
decl_stmt|;
if|if
condition|(
operator|!
name|isValidProperty
argument_list|(
name|it
argument_list|)
condition|)
return|return
literal|false
return|;
name|JAnnotationValue
name|value
init|=
name|getter
operator|.
name|getAnnotation
argument_list|(
literal|"openwire:property"
argument_list|)
operator|.
name|getValue
argument_list|(
literal|"cache"
argument_list|)
decl_stmt|;
return|return
name|value
operator|!=
literal|null
operator|&&
name|value
operator|.
name|asBoolean
argument_list|()
return|;
block|}
specifier|public
name|boolean
name|isAbstract
parameter_list|(
name|JClass
name|j
parameter_list|)
block|{
name|JField
index|[]
name|fields
init|=
name|j
operator|.
name|getFields
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
name|JField
name|field
init|=
name|fields
index|[
name|i
index|]
decl_stmt|;
if|if
condition|(
name|field
operator|.
name|isStatic
argument_list|()
operator|&&
name|field
operator|.
name|isPublic
argument_list|()
operator|&&
name|field
operator|.
name|isFinal
argument_list|()
operator|&&
name|field
operator|.
name|getSimpleName
argument_list|()
operator|.
name|equals
argument_list|(
literal|"DATA_STRUCTURE_TYPE"
argument_list|)
condition|)
block|{
return|return
literal|false
return|;
block|}
block|}
return|return
literal|true
return|;
block|}
specifier|public
name|boolean
name|isThrowable
parameter_list|(
name|JClass
name|j
parameter_list|)
block|{
if|if
condition|(
name|j
operator|.
name|getQualifiedName
argument_list|()
operator|.
name|equals
argument_list|(
name|Throwable
operator|.
name|class
operator|.
name|getName
argument_list|()
argument_list|)
condition|)
block|{
return|return
literal|true
return|;
block|}
return|return
name|j
operator|.
name|getSuperclass
argument_list|()
operator|!=
literal|null
operator|&&
name|isThrowable
argument_list|(
name|j
operator|.
name|getSuperclass
argument_list|()
argument_list|)
return|;
block|}
specifier|public
name|boolean
name|isMarshallAware
parameter_list|(
name|JClass
name|j
parameter_list|)
block|{
name|JClass
index|[]
name|interfaces
init|=
name|j
operator|.
name|getInterfaces
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
name|interfaces
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
if|if
condition|(
name|interfaces
index|[
name|i
index|]
operator|.
name|getQualifiedName
argument_list|()
operator|.
name|equals
argument_list|(
literal|"org.apache.activemq.command.MarshallAware"
argument_list|)
condition|)
block|{
return|return
literal|true
return|;
block|}
block|}
return|return
literal|false
return|;
comment|//j.getSuperclass()!=null&& isMarshallAware(j.getSuperclass());
block|}
block|}
end_class

end_unit

