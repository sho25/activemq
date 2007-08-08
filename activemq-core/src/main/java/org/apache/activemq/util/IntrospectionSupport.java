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
name|util
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
name|command
operator|.
name|ActiveMQDestination
import|;
end_import

begin_import
import|import
name|java
operator|.
name|beans
operator|.
name|PropertyEditor
import|;
end_import

begin_import
import|import
name|java
operator|.
name|beans
operator|.
name|PropertyEditorManager
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
name|Method
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
name|net
operator|.
name|URI
import|;
end_import

begin_import
import|import
name|java
operator|.
name|net
operator|.
name|URISyntaxException
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Arrays
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|HashMap
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Iterator
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

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Map
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Set
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Map
operator|.
name|Entry
import|;
end_import

begin_class
specifier|public
class|class
name|IntrospectionSupport
block|{
specifier|static
specifier|public
name|boolean
name|getProperties
parameter_list|(
name|Object
name|target
parameter_list|,
name|Map
name|props
parameter_list|,
name|String
name|optionPrefix
parameter_list|)
block|{
name|boolean
name|rc
init|=
literal|false
decl_stmt|;
if|if
condition|(
name|target
operator|==
literal|null
condition|)
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"target was null."
argument_list|)
throw|;
if|if
condition|(
name|props
operator|==
literal|null
condition|)
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"props was null."
argument_list|)
throw|;
if|if
condition|(
name|optionPrefix
operator|==
literal|null
condition|)
name|optionPrefix
operator|=
literal|""
expr_stmt|;
name|Class
name|clazz
init|=
name|target
operator|.
name|getClass
argument_list|()
decl_stmt|;
name|Method
index|[]
name|methods
init|=
name|clazz
operator|.
name|getMethods
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
name|methods
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|Method
name|method
init|=
name|methods
index|[
name|i
index|]
decl_stmt|;
name|String
name|name
init|=
name|method
operator|.
name|getName
argument_list|()
decl_stmt|;
name|Class
name|type
init|=
name|method
operator|.
name|getReturnType
argument_list|()
decl_stmt|;
name|Class
name|params
index|[]
init|=
name|method
operator|.
name|getParameterTypes
argument_list|()
decl_stmt|;
if|if
condition|(
name|name
operator|.
name|startsWith
argument_list|(
literal|"get"
argument_list|)
operator|&&
name|params
operator|.
name|length
operator|==
literal|0
operator|&&
name|type
operator|!=
literal|null
operator|&&
name|isSettableType
argument_list|(
name|type
argument_list|)
condition|)
block|{
try|try
block|{
name|Object
name|value
init|=
name|method
operator|.
name|invoke
argument_list|(
name|target
argument_list|,
operator|new
name|Object
index|[]
block|{}
argument_list|)
decl_stmt|;
if|if
condition|(
name|value
operator|==
literal|null
condition|)
continue|continue;
name|String
name|strValue
init|=
name|convertToString
argument_list|(
name|value
argument_list|,
name|type
argument_list|)
decl_stmt|;
if|if
condition|(
name|strValue
operator|==
literal|null
condition|)
continue|continue;
name|name
operator|=
name|name
operator|.
name|substring
argument_list|(
literal|3
argument_list|,
literal|4
argument_list|)
operator|.
name|toLowerCase
argument_list|()
operator|+
name|name
operator|.
name|substring
argument_list|(
literal|4
argument_list|)
expr_stmt|;
name|props
operator|.
name|put
argument_list|(
name|optionPrefix
operator|+
name|name
argument_list|,
name|strValue
argument_list|)
expr_stmt|;
name|rc
operator|=
literal|true
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Throwable
name|ignore
parameter_list|)
block|{             	}
block|}
block|}
return|return
name|rc
return|;
block|}
specifier|static
specifier|public
name|boolean
name|setProperties
parameter_list|(
name|Object
name|target
parameter_list|,
name|Map
name|props
parameter_list|,
name|String
name|optionPrefix
parameter_list|)
block|{
name|boolean
name|rc
init|=
literal|false
decl_stmt|;
if|if
condition|(
name|target
operator|==
literal|null
condition|)
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"target was null."
argument_list|)
throw|;
if|if
condition|(
name|props
operator|==
literal|null
condition|)
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"props was null."
argument_list|)
throw|;
for|for
control|(
name|Iterator
name|iter
init|=
name|props
operator|.
name|keySet
argument_list|()
operator|.
name|iterator
argument_list|()
init|;
name|iter
operator|.
name|hasNext
argument_list|()
condition|;
control|)
block|{
name|String
name|name
init|=
operator|(
name|String
operator|)
name|iter
operator|.
name|next
argument_list|()
decl_stmt|;
if|if
condition|(
name|name
operator|.
name|startsWith
argument_list|(
name|optionPrefix
argument_list|)
condition|)
block|{
name|Object
name|value
init|=
name|props
operator|.
name|get
argument_list|(
name|name
argument_list|)
decl_stmt|;
name|name
operator|=
name|name
operator|.
name|substring
argument_list|(
name|optionPrefix
operator|.
name|length
argument_list|()
argument_list|)
expr_stmt|;
if|if
condition|(
name|setProperty
argument_list|(
name|target
argument_list|,
name|name
argument_list|,
name|value
argument_list|)
condition|)
block|{
name|iter
operator|.
name|remove
argument_list|()
expr_stmt|;
name|rc
operator|=
literal|true
expr_stmt|;
block|}
block|}
block|}
return|return
name|rc
return|;
block|}
specifier|public
specifier|static
name|Map
name|extractProperties
parameter_list|(
name|Map
name|props
parameter_list|,
name|String
name|optionPrefix
parameter_list|)
block|{
if|if
condition|(
name|props
operator|==
literal|null
condition|)
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"props was null."
argument_list|)
throw|;
name|HashMap
name|rc
init|=
operator|new
name|HashMap
argument_list|(
name|props
operator|.
name|size
argument_list|()
argument_list|)
decl_stmt|;
for|for
control|(
name|Iterator
name|iter
init|=
name|props
operator|.
name|keySet
argument_list|()
operator|.
name|iterator
argument_list|()
init|;
name|iter
operator|.
name|hasNext
argument_list|()
condition|;
control|)
block|{
name|String
name|name
init|=
operator|(
name|String
operator|)
name|iter
operator|.
name|next
argument_list|()
decl_stmt|;
if|if
condition|(
name|name
operator|.
name|startsWith
argument_list|(
name|optionPrefix
argument_list|)
condition|)
block|{
name|Object
name|value
init|=
name|props
operator|.
name|get
argument_list|(
name|name
argument_list|)
decl_stmt|;
name|name
operator|=
name|name
operator|.
name|substring
argument_list|(
name|optionPrefix
operator|.
name|length
argument_list|()
argument_list|)
expr_stmt|;
name|rc
operator|.
name|put
argument_list|(
name|name
argument_list|,
name|value
argument_list|)
expr_stmt|;
name|iter
operator|.
name|remove
argument_list|()
expr_stmt|;
block|}
block|}
return|return
name|rc
return|;
block|}
specifier|public
specifier|static
name|boolean
name|setProperties
parameter_list|(
name|Object
name|target
parameter_list|,
name|Map
name|props
parameter_list|)
block|{
name|boolean
name|rc
init|=
literal|false
decl_stmt|;
if|if
condition|(
name|target
operator|==
literal|null
condition|)
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"target was null."
argument_list|)
throw|;
if|if
condition|(
name|props
operator|==
literal|null
condition|)
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"props was null."
argument_list|)
throw|;
for|for
control|(
name|Iterator
name|iter
init|=
name|props
operator|.
name|entrySet
argument_list|()
operator|.
name|iterator
argument_list|()
init|;
name|iter
operator|.
name|hasNext
argument_list|()
condition|;
control|)
block|{
name|Map
operator|.
name|Entry
name|entry
init|=
operator|(
name|Entry
operator|)
name|iter
operator|.
name|next
argument_list|()
decl_stmt|;
if|if
condition|(
name|setProperty
argument_list|(
name|target
argument_list|,
operator|(
name|String
operator|)
name|entry
operator|.
name|getKey
argument_list|()
argument_list|,
name|entry
operator|.
name|getValue
argument_list|()
argument_list|)
condition|)
block|{
name|iter
operator|.
name|remove
argument_list|()
expr_stmt|;
name|rc
operator|=
literal|true
expr_stmt|;
block|}
block|}
return|return
name|rc
return|;
block|}
specifier|public
specifier|static
name|boolean
name|setProperty
parameter_list|(
name|Object
name|target
parameter_list|,
name|String
name|name
parameter_list|,
name|Object
name|value
parameter_list|)
block|{
try|try
block|{
name|Class
name|clazz
init|=
name|target
operator|.
name|getClass
argument_list|()
decl_stmt|;
name|Method
name|setter
init|=
name|findSetterMethod
argument_list|(
name|clazz
argument_list|,
name|name
argument_list|)
decl_stmt|;
if|if
condition|(
name|setter
operator|==
literal|null
condition|)
return|return
literal|false
return|;
comment|// If the type is null or it matches the needed type, just use the value directly
if|if
condition|(
name|value
operator|==
literal|null
operator|||
name|value
operator|.
name|getClass
argument_list|()
operator|==
name|setter
operator|.
name|getParameterTypes
argument_list|()
index|[
literal|0
index|]
condition|)
block|{
name|setter
operator|.
name|invoke
argument_list|(
name|target
argument_list|,
operator|new
name|Object
index|[]
block|{
name|value
block|}
argument_list|)
expr_stmt|;
block|}
else|else
block|{
comment|// We need to convert it
name|setter
operator|.
name|invoke
argument_list|(
name|target
argument_list|,
operator|new
name|Object
index|[]
block|{
name|convert
argument_list|(
name|value
argument_list|,
name|setter
operator|.
name|getParameterTypes
argument_list|()
index|[
literal|0
index|]
argument_list|)
block|}
argument_list|)
expr_stmt|;
block|}
return|return
literal|true
return|;
block|}
catch|catch
parameter_list|(
name|Throwable
name|ignore
parameter_list|)
block|{
return|return
literal|false
return|;
block|}
block|}
specifier|private
specifier|static
name|Object
name|convert
parameter_list|(
name|Object
name|value
parameter_list|,
name|Class
name|type
parameter_list|)
throws|throws
name|URISyntaxException
block|{
name|PropertyEditor
name|editor
init|=
name|PropertyEditorManager
operator|.
name|findEditor
argument_list|(
name|type
argument_list|)
decl_stmt|;
if|if
condition|(
name|editor
operator|!=
literal|null
condition|)
block|{
name|editor
operator|.
name|setAsText
argument_list|(
name|value
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
return|return
name|editor
operator|.
name|getValue
argument_list|()
return|;
block|}
if|if
condition|(
name|type
operator|==
name|URI
operator|.
name|class
condition|)
block|{
return|return
operator|new
name|URI
argument_list|(
name|value
operator|.
name|toString
argument_list|()
argument_list|)
return|;
block|}
return|return
literal|null
return|;
block|}
specifier|private
specifier|static
name|String
name|convertToString
parameter_list|(
name|Object
name|value
parameter_list|,
name|Class
name|type
parameter_list|)
throws|throws
name|URISyntaxException
block|{
name|PropertyEditor
name|editor
init|=
name|PropertyEditorManager
operator|.
name|findEditor
argument_list|(
name|type
argument_list|)
decl_stmt|;
if|if
condition|(
name|editor
operator|!=
literal|null
condition|)
block|{
name|editor
operator|.
name|setValue
argument_list|(
name|value
argument_list|)
expr_stmt|;
return|return
name|editor
operator|.
name|getAsText
argument_list|()
return|;
block|}
if|if
condition|(
name|type
operator|==
name|URI
operator|.
name|class
condition|)
block|{
return|return
operator|(
operator|(
name|URI
operator|)
name|value
operator|)
operator|.
name|toString
argument_list|()
return|;
block|}
return|return
literal|null
return|;
block|}
specifier|private
specifier|static
name|Method
name|findSetterMethod
parameter_list|(
name|Class
name|clazz
parameter_list|,
name|String
name|name
parameter_list|)
block|{
comment|// Build the method name.
name|name
operator|=
literal|"set"
operator|+
name|name
operator|.
name|substring
argument_list|(
literal|0
argument_list|,
literal|1
argument_list|)
operator|.
name|toUpperCase
argument_list|()
operator|+
name|name
operator|.
name|substring
argument_list|(
literal|1
argument_list|)
expr_stmt|;
name|Method
index|[]
name|methods
init|=
name|clazz
operator|.
name|getMethods
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
name|methods
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|Method
name|method
init|=
name|methods
index|[
name|i
index|]
decl_stmt|;
name|Class
name|params
index|[]
init|=
name|method
operator|.
name|getParameterTypes
argument_list|()
decl_stmt|;
if|if
condition|(
name|method
operator|.
name|getName
argument_list|()
operator|.
name|equals
argument_list|(
name|name
argument_list|)
operator|&&
name|params
operator|.
name|length
operator|==
literal|1
operator|&&
name|isSettableType
argument_list|(
name|params
index|[
literal|0
index|]
argument_list|)
condition|)
block|{
return|return
name|method
return|;
block|}
block|}
return|return
literal|null
return|;
block|}
specifier|private
specifier|static
name|boolean
name|isSettableType
parameter_list|(
name|Class
name|clazz
parameter_list|)
block|{
if|if
condition|(
name|PropertyEditorManager
operator|.
name|findEditor
argument_list|(
name|clazz
argument_list|)
operator|!=
literal|null
condition|)
return|return
literal|true
return|;
if|if
condition|(
name|clazz
operator|==
name|URI
operator|.
name|class
condition|)
return|return
literal|true
return|;
if|if
condition|(
name|clazz
operator|==
name|Boolean
operator|.
name|class
condition|)
return|return
literal|true
return|;
return|return
literal|false
return|;
block|}
specifier|static
specifier|public
name|String
name|toString
parameter_list|(
name|Object
name|target
parameter_list|)
block|{
return|return
name|toString
argument_list|(
name|target
argument_list|,
name|Object
operator|.
name|class
argument_list|)
return|;
block|}
specifier|static
specifier|public
name|String
name|toString
parameter_list|(
name|Object
name|target
parameter_list|,
name|Class
name|stopClass
parameter_list|)
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
name|target
argument_list|,
name|target
operator|.
name|getClass
argument_list|()
argument_list|,
name|stopClass
argument_list|,
name|map
argument_list|)
expr_stmt|;
name|StringBuffer
name|buffer
init|=
operator|new
name|StringBuffer
argument_list|(
name|simpleName
argument_list|(
name|target
operator|.
name|getClass
argument_list|()
argument_list|)
argument_list|)
decl_stmt|;
name|buffer
operator|.
name|append
argument_list|(
literal|" {"
argument_list|)
expr_stmt|;
name|Set
name|entrySet
init|=
name|map
operator|.
name|entrySet
argument_list|()
decl_stmt|;
name|boolean
name|first
init|=
literal|true
decl_stmt|;
for|for
control|(
name|Iterator
name|iter
init|=
name|entrySet
operator|.
name|iterator
argument_list|()
init|;
name|iter
operator|.
name|hasNext
argument_list|()
condition|;
control|)
block|{
name|Map
operator|.
name|Entry
name|entry
init|=
operator|(
name|Map
operator|.
name|Entry
operator|)
name|iter
operator|.
name|next
argument_list|()
decl_stmt|;
if|if
condition|(
name|first
condition|)
block|{
name|first
operator|=
literal|false
expr_stmt|;
block|}
else|else
block|{
name|buffer
operator|.
name|append
argument_list|(
literal|", "
argument_list|)
expr_stmt|;
block|}
name|buffer
operator|.
name|append
argument_list|(
name|entry
operator|.
name|getKey
argument_list|()
argument_list|)
expr_stmt|;
name|buffer
operator|.
name|append
argument_list|(
literal|" = "
argument_list|)
expr_stmt|;
name|appendToString
argument_list|(
name|buffer
argument_list|,
name|entry
operator|.
name|getValue
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|buffer
operator|.
name|append
argument_list|(
literal|"}"
argument_list|)
expr_stmt|;
return|return
name|buffer
operator|.
name|toString
argument_list|()
return|;
block|}
specifier|protected
specifier|static
name|void
name|appendToString
parameter_list|(
name|StringBuffer
name|buffer
parameter_list|,
name|Object
name|value
parameter_list|)
block|{
if|if
condition|(
name|value
operator|instanceof
name|ActiveMQDestination
condition|)
block|{
name|ActiveMQDestination
name|destination
init|=
operator|(
name|ActiveMQDestination
operator|)
name|value
decl_stmt|;
name|buffer
operator|.
name|append
argument_list|(
name|destination
operator|.
name|getQualifiedName
argument_list|()
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|buffer
operator|.
name|append
argument_list|(
name|value
argument_list|)
expr_stmt|;
block|}
block|}
specifier|static
specifier|public
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
specifier|static
specifier|private
name|void
name|addFields
parameter_list|(
name|Object
name|target
parameter_list|,
name|Class
name|startClass
parameter_list|,
name|Class
name|stopClass
parameter_list|,
name|LinkedHashMap
name|map
parameter_list|)
block|{
if|if
condition|(
name|startClass
operator|!=
name|stopClass
condition|)
name|addFields
argument_list|(
name|target
argument_list|,
name|startClass
operator|.
name|getSuperclass
argument_list|()
argument_list|,
name|stopClass
argument_list|,
name|map
argument_list|)
expr_stmt|;
name|Field
index|[]
name|fields
init|=
name|startClass
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
name|field
operator|.
name|setAccessible
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|Object
name|o
init|=
name|field
operator|.
name|get
argument_list|(
name|target
argument_list|)
decl_stmt|;
if|if
condition|(
name|o
operator|!=
literal|null
operator|&&
name|o
operator|.
name|getClass
argument_list|()
operator|.
name|isArray
argument_list|()
condition|)
block|{
try|try
block|{
name|o
operator|=
name|Arrays
operator|.
name|asList
argument_list|(
operator|(
name|Object
index|[]
operator|)
name|o
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Throwable
name|e
parameter_list|)
block|{                     }
block|}
name|map
operator|.
name|put
argument_list|(
name|field
operator|.
name|getName
argument_list|()
argument_list|,
name|o
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

