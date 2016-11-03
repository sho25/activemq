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
name|List
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Locale
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
name|Map
operator|.
name|Entry
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
name|javax
operator|.
name|net
operator|.
name|ssl
operator|.
name|SSLServerSocket
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
name|command
operator|.
name|ActiveMQDestination
import|;
end_import

begin_import
import|import
name|org
operator|.
name|slf4j
operator|.
name|Logger
import|;
end_import

begin_import
import|import
name|org
operator|.
name|slf4j
operator|.
name|LoggerFactory
import|;
end_import

begin_class
specifier|public
specifier|final
class|class
name|IntrospectionSupport
block|{
specifier|private
specifier|static
specifier|final
name|Logger
name|LOG
init|=
name|LoggerFactory
operator|.
name|getLogger
argument_list|(
name|IntrospectionSupport
operator|.
name|class
argument_list|)
decl_stmt|;
specifier|private
name|IntrospectionSupport
parameter_list|()
block|{     }
specifier|public
specifier|static
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
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"target was null."
argument_list|)
throw|;
block|}
if|if
condition|(
name|props
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"props was null."
argument_list|)
throw|;
block|}
if|if
condition|(
name|optionPrefix
operator|==
literal|null
condition|)
block|{
name|optionPrefix
operator|=
literal|""
expr_stmt|;
block|}
name|Class
argument_list|<
name|?
argument_list|>
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
name|Method
name|method
range|:
name|methods
control|)
block|{
name|String
name|name
init|=
name|method
operator|.
name|getName
argument_list|()
decl_stmt|;
name|Class
argument_list|<
name|?
argument_list|>
name|type
init|=
name|method
operator|.
name|getReturnType
argument_list|()
decl_stmt|;
name|Class
argument_list|<
name|?
argument_list|>
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
operator|(
name|name
operator|.
name|startsWith
argument_list|(
literal|"is"
argument_list|)
operator|||
name|name
operator|.
name|startsWith
argument_list|(
literal|"get"
argument_list|)
operator|)
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
argument_list|)
decl_stmt|;
if|if
condition|(
name|value
operator|==
literal|null
condition|)
block|{
continue|continue;
block|}
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
block|{
continue|continue;
block|}
if|if
condition|(
name|name
operator|.
name|startsWith
argument_list|(
literal|"get"
argument_list|)
condition|)
block|{
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
argument_list|(
name|Locale
operator|.
name|ENGLISH
argument_list|)
operator|+
name|name
operator|.
name|substring
argument_list|(
literal|4
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|name
operator|=
name|name
operator|.
name|substring
argument_list|(
literal|2
argument_list|,
literal|3
argument_list|)
operator|.
name|toLowerCase
argument_list|(
name|Locale
operator|.
name|ENGLISH
argument_list|)
operator|+
name|name
operator|.
name|substring
argument_list|(
literal|3
argument_list|)
expr_stmt|;
block|}
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
name|Exception
name|ignore
parameter_list|)
block|{                 }
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
argument_list|<
name|String
argument_list|,
name|?
argument_list|>
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
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"target was null."
argument_list|)
throw|;
block|}
if|if
condition|(
name|props
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"props was null."
argument_list|)
throw|;
block|}
for|for
control|(
name|Iterator
argument_list|<
name|String
argument_list|>
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
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
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
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"props was null."
argument_list|)
throw|;
block|}
name|HashMap
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|rc
init|=
operator|new
name|HashMap
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
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
argument_list|<
name|?
argument_list|>
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
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"target was null."
argument_list|)
throw|;
block|}
if|if
condition|(
name|props
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"props was null."
argument_list|)
throw|;
block|}
for|for
control|(
name|Iterator
argument_list|<
name|?
argument_list|>
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
argument_list|<
name|?
argument_list|,
name|?
argument_list|>
name|entry
init|=
operator|(
name|Entry
argument_list|<
name|?
argument_list|,
name|?
argument_list|>
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
argument_list|<
name|?
argument_list|>
name|clazz
init|=
name|target
operator|.
name|getClass
argument_list|()
decl_stmt|;
if|if
condition|(
name|target
operator|instanceof
name|SSLServerSocket
condition|)
block|{
comment|// overcome illegal access issues with internal implementation class
name|clazz
operator|=
name|SSLServerSocket
operator|.
name|class
expr_stmt|;
block|}
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
block|{
return|return
literal|false
return|;
block|}
comment|// If the type is null or it matches the needed type, just use the
comment|// value directly
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
name|value
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
argument_list|)
expr_stmt|;
block|}
return|return
literal|true
return|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
name|LOG
operator|.
name|error
argument_list|(
name|String
operator|.
name|format
argument_list|(
literal|"Could not set property %s on %s"
argument_list|,
name|name
argument_list|,
name|target
argument_list|)
argument_list|,
name|e
argument_list|)
expr_stmt|;
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
name|to
parameter_list|)
block|{
if|if
condition|(
name|value
operator|==
literal|null
condition|)
block|{
comment|// lets avoid NullPointerException when converting to boolean for null values
if|if
condition|(
name|boolean
operator|.
name|class
operator|.
name|isAssignableFrom
argument_list|(
name|to
argument_list|)
condition|)
block|{
return|return
name|Boolean
operator|.
name|FALSE
return|;
block|}
return|return
literal|null
return|;
block|}
comment|// eager same instance type test to avoid the overhead of invoking the type converter
comment|// if already same type
if|if
condition|(
name|to
operator|.
name|isAssignableFrom
argument_list|(
name|value
operator|.
name|getClass
argument_list|()
argument_list|)
condition|)
block|{
return|return
name|to
operator|.
name|cast
argument_list|(
name|value
argument_list|)
return|;
block|}
comment|// special for String[] as we do not want to use a PropertyEditor for that
if|if
condition|(
name|to
operator|.
name|isAssignableFrom
argument_list|(
name|String
index|[]
operator|.
expr|class
argument_list|)
condition|)
block|{
return|return
name|StringArrayConverter
operator|.
name|convertToStringArray
argument_list|(
name|value
argument_list|)
return|;
block|}
comment|// special for String to List<ActiveMQDestination> as we do not want to use a PropertyEditor for that
if|if
condition|(
name|value
operator|.
name|getClass
argument_list|()
operator|.
name|equals
argument_list|(
name|String
operator|.
name|class
argument_list|)
operator|&&
name|to
operator|.
name|equals
argument_list|(
name|List
operator|.
name|class
argument_list|)
condition|)
block|{
name|Object
name|answer
init|=
name|StringToListOfActiveMQDestinationConverter
operator|.
name|convertToActiveMQDestination
argument_list|(
name|value
argument_list|)
decl_stmt|;
if|if
condition|(
name|answer
operator|!=
literal|null
condition|)
block|{
return|return
name|answer
return|;
block|}
block|}
name|TypeConversionSupport
operator|.
name|Converter
name|converter
init|=
name|TypeConversionSupport
operator|.
name|lookupConverter
argument_list|(
name|value
operator|.
name|getClass
argument_list|()
argument_list|,
name|to
argument_list|)
decl_stmt|;
if|if
condition|(
name|converter
operator|!=
literal|null
condition|)
block|{
return|return
name|converter
operator|.
name|convert
argument_list|(
name|value
argument_list|)
return|;
block|}
else|else
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"Cannot convert from "
operator|+
name|value
operator|.
name|getClass
argument_list|()
operator|+
literal|" to "
operator|+
name|to
operator|+
literal|" with value "
operator|+
name|value
argument_list|)
throw|;
block|}
block|}
specifier|public
specifier|static
name|String
name|convertToString
parameter_list|(
name|Object
name|value
parameter_list|,
name|Class
name|to
parameter_list|)
block|{
if|if
condition|(
name|value
operator|==
literal|null
condition|)
block|{
return|return
literal|null
return|;
block|}
comment|// already a String
if|if
condition|(
name|value
operator|instanceof
name|String
condition|)
block|{
return|return
operator|(
name|String
operator|)
name|value
return|;
block|}
comment|// special for String[] as we do not want to use a PropertyEditor for that
if|if
condition|(
name|String
index|[]
operator|.
name|class
operator|.
name|isInstance
argument_list|(
name|value
argument_list|)
condition|)
block|{
name|String
index|[]
name|array
init|=
operator|(
name|String
index|[]
operator|)
name|value
decl_stmt|;
return|return
name|StringArrayConverter
operator|.
name|convertToString
argument_list|(
name|array
argument_list|)
return|;
block|}
comment|// special for String to List<ActiveMQDestination> as we do not want to use a PropertyEditor for that
if|if
condition|(
name|List
operator|.
name|class
operator|.
name|isInstance
argument_list|(
name|value
argument_list|)
condition|)
block|{
comment|// if the list is a ActiveMQDestination, then return a comma list
name|String
name|answer
init|=
name|StringToListOfActiveMQDestinationConverter
operator|.
name|convertFromActiveMQDestination
argument_list|(
name|value
argument_list|)
decl_stmt|;
if|if
condition|(
name|answer
operator|!=
literal|null
condition|)
block|{
return|return
name|answer
return|;
block|}
block|}
name|TypeConversionSupport
operator|.
name|Converter
name|converter
init|=
name|TypeConversionSupport
operator|.
name|lookupConverter
argument_list|(
name|value
operator|.
name|getClass
argument_list|()
argument_list|,
name|String
operator|.
name|class
argument_list|)
decl_stmt|;
if|if
condition|(
name|converter
operator|!=
literal|null
condition|)
block|{
return|return
operator|(
name|String
operator|)
name|converter
operator|.
name|convert
argument_list|(
name|value
argument_list|)
return|;
block|}
else|else
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"Cannot convert from "
operator|+
name|value
operator|.
name|getClass
argument_list|()
operator|+
literal|" to "
operator|+
name|to
operator|+
literal|" with value "
operator|+
name|value
argument_list|)
throw|;
block|}
block|}
specifier|public
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
name|Character
operator|.
name|toUpperCase
argument_list|(
name|name
operator|.
name|charAt
argument_list|(
literal|0
argument_list|)
argument_list|)
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
name|Method
name|method
range|:
name|methods
control|)
block|{
name|Class
argument_list|<
name|?
argument_list|>
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
specifier|public
specifier|static
name|Method
name|findGetterMethod
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
literal|"get"
operator|+
name|Character
operator|.
name|toUpperCase
argument_list|(
name|name
operator|.
name|charAt
argument_list|(
literal|0
argument_list|)
argument_list|)
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
name|Method
name|method
range|:
name|methods
control|)
block|{
name|Class
argument_list|<
name|?
argument_list|>
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
literal|0
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
specifier|public
specifier|static
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
argument_list|,
literal|null
argument_list|)
return|;
block|}
specifier|public
specifier|static
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
return|return
name|toString
argument_list|(
name|target
argument_list|,
name|stopClass
argument_list|,
literal|null
argument_list|)
return|;
block|}
specifier|public
specifier|static
name|String
name|toString
parameter_list|(
name|Object
name|target
parameter_list|,
name|Class
name|stopClass
parameter_list|,
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|overrideFields
parameter_list|)
block|{
name|LinkedHashMap
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|map
init|=
operator|new
name|LinkedHashMap
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
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
if|if
condition|(
name|overrideFields
operator|!=
literal|null
condition|)
block|{
for|for
control|(
name|String
name|key
range|:
name|overrideFields
operator|.
name|keySet
argument_list|()
control|)
block|{
name|Object
name|value
init|=
name|overrideFields
operator|.
name|get
argument_list|(
name|key
argument_list|)
decl_stmt|;
name|map
operator|.
name|put
argument_list|(
name|key
argument_list|,
name|value
argument_list|)
expr_stmt|;
block|}
block|}
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
argument_list|<
name|Entry
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
argument_list|>
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
name|Map
operator|.
name|Entry
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|entry
range|:
name|entrySet
control|)
block|{
name|Object
name|value
init|=
name|entry
operator|.
name|getValue
argument_list|()
decl_stmt|;
name|Object
name|key
init|=
name|entry
operator|.
name|getKey
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
name|key
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
name|key
argument_list|,
name|value
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
name|key
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
elseif|else
if|if
condition|(
name|key
operator|.
name|toString
argument_list|()
operator|.
name|toLowerCase
argument_list|(
name|Locale
operator|.
name|ENGLISH
argument_list|)
operator|.
name|contains
argument_list|(
literal|"password"
argument_list|)
condition|)
block|{
name|buffer
operator|.
name|append
argument_list|(
literal|"*****"
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
specifier|static
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
argument_list|<
name|Object
argument_list|>
name|stopClass
parameter_list|,
name|LinkedHashMap
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|map
parameter_list|)
block|{
if|if
condition|(
name|startClass
operator|!=
name|stopClass
condition|)
block|{
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
block|}
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
name|Field
name|field
range|:
name|fields
control|)
block|{
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
name|Exception
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
name|Exception
name|e
parameter_list|)
block|{
name|LOG
operator|.
name|debug
argument_list|(
literal|"Error getting field "
operator|+
name|field
operator|+
literal|" on class "
operator|+
name|startClass
operator|+
literal|". This exception is ignored."
argument_list|,
name|e
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|}
end_class

end_unit

