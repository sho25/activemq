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

begin_import
import|import
name|org
operator|.
name|codehaus
operator|.
name|jam
operator|.
name|JamClassIterator
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
name|JamService
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
specifier|private
name|String
name|openwireVersion
decl_stmt|;
specifier|protected
name|String
name|filePostFix
init|=
literal|".java"
decl_stmt|;
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
if|if
condition|(
name|filePostFix
operator|.
name|endsWith
argument_list|(
literal|"java"
argument_list|)
condition|)
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
block|}
else|else
block|{
name|String
name|simpleName
init|=
name|j
operator|.
name|getSimpleName
argument_list|()
decl_stmt|;
return|return
name|simpleName
operator|.
name|equals
argument_list|(
literal|"ActiveMQMessage"
argument_list|)
operator|||
name|simpleName
operator|.
name|equals
argument_list|(
literal|"WireFormatInfo"
argument_list|)
return|;
block|}
comment|/*          * else { // is it a message type String simpleName = j.getSimpleName();          * JClass superclass = j.getSuperclass(); return          * simpleName.equals("ActiveMQMessage") || (superclass != null&&          * superclass.getSimpleName().equals("ActiveMQMessage")); }          */
block|}
specifier|public
name|JamService
name|getJam
parameter_list|()
block|{
return|return
operator|(
name|JamService
operator|)
name|getBinding
argument_list|()
operator|.
name|getVariable
argument_list|(
literal|"jam"
argument_list|)
return|;
block|}
specifier|public
name|JamClassIterator
name|getClasses
parameter_list|()
block|{
return|return
name|getJam
argument_list|()
operator|.
name|getClasses
argument_list|()
return|;
block|}
specifier|public
name|String
name|getOpenwireVersion
parameter_list|()
block|{
if|if
condition|(
name|openwireVersion
operator|==
literal|null
condition|)
block|{
name|openwireVersion
operator|=
operator|(
name|String
operator|)
name|getProperty
argument_list|(
literal|"version"
argument_list|)
expr_stmt|;
block|}
return|return
name|openwireVersion
return|;
block|}
specifier|public
name|void
name|setOpenwireVersion
parameter_list|(
name|String
name|openwireVersion
parameter_list|)
block|{
name|this
operator|.
name|openwireVersion
operator|=
name|openwireVersion
expr_stmt|;
block|}
comment|/**      * Converts the Java type to a C# type name      */
specifier|public
name|String
name|toCSharpType
parameter_list|(
name|JClass
name|type
parameter_list|)
block|{
name|String
name|name
init|=
name|type
operator|.
name|getSimpleName
argument_list|()
decl_stmt|;
if|if
condition|(
name|name
operator|.
name|equals
argument_list|(
literal|"String"
argument_list|)
condition|)
block|{
return|return
literal|"string"
return|;
block|}
elseif|else
if|if
condition|(
name|name
operator|.
name|equals
argument_list|(
literal|"Throwable"
argument_list|)
operator|||
name|name
operator|.
name|equals
argument_list|(
literal|"Exception"
argument_list|)
condition|)
block|{
return|return
literal|"BrokerError"
return|;
block|}
elseif|else
if|if
condition|(
name|name
operator|.
name|equals
argument_list|(
literal|"ByteSequence"
argument_list|)
condition|)
block|{
return|return
literal|"byte[]"
return|;
block|}
elseif|else
if|if
condition|(
name|name
operator|.
name|equals
argument_list|(
literal|"boolean"
argument_list|)
condition|)
block|{
return|return
literal|"bool"
return|;
block|}
else|else
block|{
return|return
name|name
return|;
block|}
block|}
specifier|public
name|String
name|getOpenWireOpCode
parameter_list|(
name|JClass
name|aClass
parameter_list|)
block|{
return|return
name|annotationValue
argument_list|(
name|aClass
argument_list|,
literal|"openwire:marshaller"
argument_list|,
literal|"code"
argument_list|,
literal|"0"
argument_list|)
return|;
block|}
block|}
end_class

end_unit

