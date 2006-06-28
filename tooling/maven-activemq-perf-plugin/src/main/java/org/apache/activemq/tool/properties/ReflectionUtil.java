begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Copyright 2005-2006 The Apache Software Foundation  *<p/>  * Licensed under the Apache License, Version 2.0 (the "License");  * you may not use this file except in compliance with the License.  * You may obtain a copy of the License at  *<p/>  * http://www.apache.org/licenses/LICENSE-2.0  *<p/>  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
package|package
name|org
operator|.
name|apache
operator|.
name|activemq
operator|.
name|tool
operator|.
name|properties
package|;
end_package

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|commons
operator|.
name|logging
operator|.
name|Log
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|commons
operator|.
name|logging
operator|.
name|LogFactory
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
name|StringTokenizer
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Properties
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
name|ArrayList
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
name|InvocationTargetException
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
name|Constructor
import|;
end_import

begin_class
specifier|public
specifier|final
class|class
name|ReflectionUtil
block|{
specifier|private
specifier|static
specifier|final
name|Log
name|log
init|=
name|LogFactory
operator|.
name|getLog
argument_list|(
name|ReflectionUtil
operator|.
name|class
argument_list|)
decl_stmt|;
specifier|private
name|ReflectionUtil
parameter_list|()
block|{     }
specifier|public
specifier|static
name|void
name|configureClass
parameter_list|(
name|Object
name|obj
parameter_list|,
name|String
name|key
parameter_list|,
name|String
name|val
parameter_list|)
block|{
try|try
block|{
name|String
name|debugInfo
decl_stmt|;
name|Object
name|target
init|=
name|obj
decl_stmt|;
name|Class
name|targetClass
init|=
name|obj
operator|.
name|getClass
argument_list|()
decl_stmt|;
comment|// DEBUG: Debugging Info
name|debugInfo
operator|=
literal|"Invoking: "
operator|+
name|targetClass
operator|.
name|getName
argument_list|()
expr_stmt|;
name|StringTokenizer
name|tokenizer
init|=
operator|new
name|StringTokenizer
argument_list|(
name|key
argument_list|,
literal|"."
argument_list|)
decl_stmt|;
name|int
name|tokenCount
init|=
name|tokenizer
operator|.
name|countTokens
argument_list|()
decl_stmt|;
comment|// For nested settings, get the object first. -1, do not count the last token
for|for
control|(
name|int
name|j
init|=
literal|0
init|;
name|j
operator|<
name|tokenCount
operator|-
literal|1
condition|;
name|j
operator|++
control|)
block|{
comment|// Find getter method first
name|String
name|name
init|=
name|tokenizer
operator|.
name|nextToken
argument_list|()
decl_stmt|;
name|String
name|getMethod
init|=
literal|"get"
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
decl_stmt|;
name|Method
name|method
init|=
name|targetClass
operator|.
name|getMethod
argument_list|(
name|getMethod
argument_list|,
operator|new
name|Class
index|[]
block|{}
argument_list|)
decl_stmt|;
name|target
operator|=
name|method
operator|.
name|invoke
argument_list|(
name|target
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|targetClass
operator|=
name|target
operator|.
name|getClass
argument_list|()
expr_stmt|;
name|debugInfo
operator|+=
operator|(
literal|"."
operator|+
name|getMethod
operator|+
literal|"()"
operator|)
expr_stmt|;
block|}
comment|// Property name
name|String
name|property
init|=
name|tokenizer
operator|.
name|nextToken
argument_list|()
decl_stmt|;
comment|// Check if the target object will accept the settings
if|if
condition|(
name|obj
operator|instanceof
name|ReflectionConfigurable
operator|&&
operator|!
operator|(
operator|(
name|ReflectionConfigurable
operator|)
name|target
operator|)
operator|.
name|acceptConfig
argument_list|(
name|property
argument_list|,
name|val
argument_list|)
condition|)
block|{
return|return;
block|}
comment|// Find setter method
name|Method
name|setterMethod
init|=
name|findSetterMethod
argument_list|(
name|targetClass
argument_list|,
name|property
argument_list|)
decl_stmt|;
comment|// Get the first parameter type. This assumes that there is only one parameter.
if|if
condition|(
name|setterMethod
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|IllegalAccessException
argument_list|(
literal|"Unable to find appropriate setter method signature for property: "
operator|+
name|property
argument_list|)
throw|;
block|}
name|Class
name|paramType
init|=
name|setterMethod
operator|.
name|getParameterTypes
argument_list|()
index|[
literal|0
index|]
decl_stmt|;
comment|// Set primitive type
name|debugInfo
operator|+=
operator|(
literal|"."
operator|+
name|setterMethod
operator|+
literal|"("
operator|+
name|paramType
operator|.
name|getName
argument_list|()
operator|+
literal|": "
operator|+
name|val
operator|+
literal|")"
operator|)
expr_stmt|;
if|if
condition|(
name|paramType
operator|.
name|isPrimitive
argument_list|()
condition|)
block|{
if|if
condition|(
name|paramType
operator|==
name|Boolean
operator|.
name|TYPE
condition|)
block|{
name|setterMethod
operator|.
name|invoke
argument_list|(
name|target
argument_list|,
operator|new
name|Object
index|[]
block|{
name|Boolean
operator|.
name|valueOf
argument_list|(
name|val
argument_list|)
block|}
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|paramType
operator|==
name|Integer
operator|.
name|TYPE
condition|)
block|{
name|setterMethod
operator|.
name|invoke
argument_list|(
name|target
argument_list|,
operator|new
name|Object
index|[]
block|{
name|Integer
operator|.
name|valueOf
argument_list|(
name|val
argument_list|)
block|}
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|paramType
operator|==
name|Long
operator|.
name|TYPE
condition|)
block|{
name|setterMethod
operator|.
name|invoke
argument_list|(
name|target
argument_list|,
operator|new
name|Object
index|[]
block|{
name|Long
operator|.
name|valueOf
argument_list|(
name|val
argument_list|)
block|}
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|paramType
operator|==
name|Double
operator|.
name|TYPE
condition|)
block|{
name|setterMethod
operator|.
name|invoke
argument_list|(
name|target
argument_list|,
operator|new
name|Object
index|[]
block|{
name|Double
operator|.
name|valueOf
argument_list|(
name|val
argument_list|)
block|}
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|paramType
operator|==
name|Float
operator|.
name|TYPE
condition|)
block|{
name|setterMethod
operator|.
name|invoke
argument_list|(
name|target
argument_list|,
operator|new
name|Object
index|[]
block|{
name|Float
operator|.
name|valueOf
argument_list|(
name|val
argument_list|)
block|}
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|paramType
operator|==
name|Short
operator|.
name|TYPE
condition|)
block|{
name|setterMethod
operator|.
name|invoke
argument_list|(
name|target
argument_list|,
operator|new
name|Object
index|[]
block|{
name|Short
operator|.
name|valueOf
argument_list|(
name|val
argument_list|)
block|}
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|paramType
operator|==
name|Byte
operator|.
name|TYPE
condition|)
block|{
name|setterMethod
operator|.
name|invoke
argument_list|(
name|target
argument_list|,
operator|new
name|Object
index|[]
block|{
name|Byte
operator|.
name|valueOf
argument_list|(
name|val
argument_list|)
block|}
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|paramType
operator|==
name|Character
operator|.
name|TYPE
condition|)
block|{
name|setterMethod
operator|.
name|invoke
argument_list|(
name|target
argument_list|,
operator|new
name|Object
index|[]
block|{
operator|new
name|Character
argument_list|(
name|val
operator|.
name|charAt
argument_list|(
literal|0
argument_list|)
argument_list|)
block|}
argument_list|)
expr_stmt|;
block|}
block|}
else|else
block|{
comment|// Set String type
if|if
condition|(
name|paramType
operator|==
name|String
operator|.
name|class
condition|)
block|{
name|setterMethod
operator|.
name|invoke
argument_list|(
name|target
argument_list|,
operator|new
name|Object
index|[]
block|{
name|val
block|}
argument_list|)
expr_stmt|;
comment|// For unknown object type, try to create an instance of the object using a String constructor
block|}
else|else
block|{
name|Constructor
name|c
init|=
name|paramType
operator|.
name|getConstructor
argument_list|(
operator|new
name|Class
index|[]
block|{
name|String
operator|.
name|class
block|}
argument_list|)
decl_stmt|;
name|Object
name|paramObject
init|=
name|c
operator|.
name|newInstance
argument_list|(
operator|new
name|Object
index|[]
block|{
name|val
block|}
argument_list|)
decl_stmt|;
name|setterMethod
operator|.
name|invoke
argument_list|(
name|target
argument_list|,
operator|new
name|Object
index|[]
block|{
name|paramObject
block|}
argument_list|)
expr_stmt|;
block|}
block|}
name|log
operator|.
name|debug
argument_list|(
name|debugInfo
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
name|log
operator|.
name|warn
argument_list|(
name|e
argument_list|)
expr_stmt|;
block|}
block|}
specifier|public
specifier|static
name|void
name|configureClass
parameter_list|(
name|Object
name|obj
parameter_list|,
name|Properties
name|props
parameter_list|)
block|{
for|for
control|(
name|Iterator
name|i
init|=
name|props
operator|.
name|keySet
argument_list|()
operator|.
name|iterator
argument_list|()
init|;
name|i
operator|.
name|hasNext
argument_list|()
condition|;
control|)
block|{
try|try
block|{
name|String
name|key
init|=
operator|(
name|String
operator|)
name|i
operator|.
name|next
argument_list|()
decl_stmt|;
name|String
name|val
init|=
name|props
operator|.
name|getProperty
argument_list|(
name|key
argument_list|)
decl_stmt|;
name|configureClass
argument_list|(
name|obj
argument_list|,
name|key
argument_list|,
name|val
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Throwable
name|t
parameter_list|)
block|{
comment|// Let's catch any exception as this could be cause by the foreign class
name|t
operator|.
name|printStackTrace
argument_list|()
expr_stmt|;
block|}
block|}
block|}
specifier|public
specifier|static
name|Properties
name|retrieveObjectProperties
parameter_list|(
name|Object
name|obj
parameter_list|)
block|{
name|Properties
name|props
init|=
operator|new
name|Properties
argument_list|()
decl_stmt|;
try|try
block|{
name|props
operator|.
name|putAll
argument_list|(
name|retrieveClassProperties
argument_list|(
literal|""
argument_list|,
name|obj
operator|.
name|getClass
argument_list|()
argument_list|,
name|obj
argument_list|)
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
name|log
operator|.
name|warn
argument_list|(
name|e
argument_list|)
expr_stmt|;
block|}
return|return
name|props
return|;
block|}
specifier|protected
specifier|static
name|Properties
name|retrieveClassProperties
parameter_list|(
name|String
name|prefix
parameter_list|,
name|Class
name|targetClass
parameter_list|,
name|Object
name|targetObject
parameter_list|)
block|{
if|if
condition|(
name|targetClass
operator|==
literal|null
operator|||
name|targetObject
operator|==
literal|null
condition|)
block|{
return|return
operator|new
name|Properties
argument_list|()
return|;
block|}
else|else
block|{
name|Properties
name|props
init|=
operator|new
name|Properties
argument_list|()
decl_stmt|;
name|Method
index|[]
name|getterMethods
init|=
name|findAllGetterMethods
argument_list|(
name|targetClass
argument_list|)
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
name|getterMethods
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
try|try
block|{
name|String
name|propertyName
init|=
name|getPropertyName
argument_list|(
name|getterMethods
index|[
name|i
index|]
operator|.
name|getName
argument_list|()
argument_list|)
decl_stmt|;
name|Class
name|retType
init|=
name|getterMethods
index|[
name|i
index|]
operator|.
name|getReturnType
argument_list|()
decl_stmt|;
comment|// If primitive or string type, return it
if|if
condition|(
name|retType
operator|.
name|isPrimitive
argument_list|()
operator|||
name|retType
operator|==
name|String
operator|.
name|class
condition|)
block|{
comment|// Check for an appropriate setter method to consider it as a property
if|if
condition|(
name|findSetterMethod
argument_list|(
name|targetClass
argument_list|,
name|propertyName
argument_list|)
operator|!=
literal|null
condition|)
block|{
name|Object
name|val
init|=
literal|null
decl_stmt|;
try|try
block|{
name|val
operator|=
name|getterMethods
index|[
name|i
index|]
operator|.
name|invoke
argument_list|(
name|targetObject
argument_list|,
literal|null
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|InvocationTargetException
name|e
parameter_list|)
block|{
name|e
operator|.
name|printStackTrace
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IllegalAccessException
name|e
parameter_list|)
block|{
name|e
operator|.
name|printStackTrace
argument_list|()
expr_stmt|;
block|}
name|props
operator|.
name|setProperty
argument_list|(
name|prefix
operator|+
name|propertyName
argument_list|,
name|val
operator|+
literal|""
argument_list|)
expr_stmt|;
block|}
block|}
else|else
block|{
try|try
block|{
name|Object
name|val
init|=
name|getterMethods
index|[
name|i
index|]
operator|.
name|invoke
argument_list|(
name|targetObject
argument_list|,
literal|null
argument_list|)
decl_stmt|;
if|if
condition|(
name|val
operator|!=
literal|null
condition|)
block|{
name|props
operator|.
name|putAll
argument_list|(
name|retrieveClassProperties
argument_list|(
name|propertyName
operator|+
literal|"."
argument_list|,
name|val
operator|.
name|getClass
argument_list|()
argument_list|,
name|val
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
catch|catch
parameter_list|(
name|InvocationTargetException
name|e
parameter_list|)
block|{
name|e
operator|.
name|printStackTrace
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IllegalAccessException
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
catch|catch
parameter_list|(
name|Throwable
name|t
parameter_list|)
block|{
comment|// Let's catch any exception, cause this could be cause by the foreign class
name|t
operator|.
name|printStackTrace
argument_list|()
expr_stmt|;
block|}
block|}
return|return
name|props
return|;
block|}
block|}
specifier|private
specifier|static
name|Method
name|findSetterMethod
parameter_list|(
name|Class
name|targetClass
parameter_list|,
name|String
name|propertyName
parameter_list|)
block|{
name|String
name|methodName
init|=
literal|"set"
operator|+
name|propertyName
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
name|propertyName
operator|.
name|substring
argument_list|(
literal|1
argument_list|)
decl_stmt|;
name|Method
index|[]
name|methods
init|=
name|targetClass
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
if|if
condition|(
name|methods
index|[
name|i
index|]
operator|.
name|getName
argument_list|()
operator|.
name|equals
argument_list|(
name|methodName
argument_list|)
operator|&&
name|isSetterMethod
argument_list|(
name|methods
index|[
name|i
index|]
argument_list|)
condition|)
block|{
return|return
name|methods
index|[
name|i
index|]
return|;
block|}
block|}
return|return
literal|null
return|;
block|}
specifier|private
specifier|static
name|Method
name|findGetterMethod
parameter_list|(
name|Class
name|targetClass
parameter_list|,
name|String
name|propertyName
parameter_list|)
block|{
name|String
name|methodName1
init|=
literal|"get"
operator|+
name|propertyName
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
name|propertyName
operator|.
name|substring
argument_list|(
literal|1
argument_list|)
decl_stmt|;
name|String
name|methodName2
init|=
literal|"is"
operator|+
name|propertyName
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
name|propertyName
operator|.
name|substring
argument_list|(
literal|1
argument_list|)
decl_stmt|;
name|Method
index|[]
name|methods
init|=
name|targetClass
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
if|if
condition|(
operator|(
name|methods
index|[
name|i
index|]
operator|.
name|getName
argument_list|()
operator|.
name|equals
argument_list|(
name|methodName1
argument_list|)
operator|||
name|methods
index|[
name|i
index|]
operator|.
name|getName
argument_list|()
operator|.
name|equals
argument_list|(
name|methodName2
argument_list|)
operator|)
operator|&&
name|isGetterMethod
argument_list|(
name|methods
index|[
name|i
index|]
argument_list|)
condition|)
block|{
return|return
name|methods
index|[
name|i
index|]
return|;
block|}
block|}
return|return
literal|null
return|;
block|}
specifier|private
specifier|static
name|Method
index|[]
name|findAllGetterMethods
parameter_list|(
name|Class
name|targetClass
parameter_list|)
block|{
name|List
name|getterMethods
init|=
operator|new
name|ArrayList
argument_list|()
decl_stmt|;
name|Method
index|[]
name|methods
init|=
name|targetClass
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
if|if
condition|(
name|isGetterMethod
argument_list|(
name|methods
index|[
name|i
index|]
argument_list|)
condition|)
block|{
name|getterMethods
operator|.
name|add
argument_list|(
name|methods
index|[
name|i
index|]
argument_list|)
expr_stmt|;
block|}
block|}
return|return
operator|(
name|Method
index|[]
operator|)
name|getterMethods
operator|.
name|toArray
argument_list|(
operator|new
name|Method
index|[]
block|{}
argument_list|)
return|;
block|}
specifier|private
specifier|static
name|boolean
name|isGetterMethod
parameter_list|(
name|Method
name|method
parameter_list|)
block|{
comment|// Check method signature first
comment|// If 'get' method, must return a non-void value
comment|// If 'is' method, must return a boolean value
comment|// Both must have no parameters
comment|// Method must not belong to the Object class to prevent infinite loop
return|return
operator|(
operator|(
operator|(
name|method
operator|.
name|getName
argument_list|()
operator|.
name|startsWith
argument_list|(
literal|"is"
argument_list|)
operator|&&
name|method
operator|.
name|getReturnType
argument_list|()
operator|==
name|Boolean
operator|.
name|TYPE
operator|)
operator|||
operator|(
name|method
operator|.
name|getName
argument_list|()
operator|.
name|startsWith
argument_list|(
literal|"get"
argument_list|)
operator|&&
name|method
operator|.
name|getReturnType
argument_list|()
operator|!=
name|Void
operator|.
name|TYPE
operator|)
operator|)
operator|&&
operator|(
name|method
operator|.
name|getParameterTypes
argument_list|()
operator|.
name|length
operator|==
literal|0
operator|)
operator|&&
name|method
operator|.
name|getDeclaringClass
argument_list|()
operator|!=
name|Object
operator|.
name|class
operator|)
return|;
block|}
specifier|private
specifier|static
name|boolean
name|isSetterMethod
parameter_list|(
name|Method
name|method
parameter_list|)
block|{
comment|// Check method signature first
if|if
condition|(
name|method
operator|.
name|getName
argument_list|()
operator|.
name|startsWith
argument_list|(
literal|"set"
argument_list|)
operator|&&
name|method
operator|.
name|getReturnType
argument_list|()
operator|==
name|Void
operator|.
name|TYPE
condition|)
block|{
name|Class
index|[]
name|paramType
init|=
name|method
operator|.
name|getParameterTypes
argument_list|()
decl_stmt|;
comment|// Check that it can only accept one parameter
if|if
condition|(
name|paramType
operator|.
name|length
operator|==
literal|1
condition|)
block|{
comment|// Check if parameter is a primitive or can accept a String parameter
if|if
condition|(
name|paramType
index|[
literal|0
index|]
operator|.
name|isPrimitive
argument_list|()
operator|||
name|paramType
index|[
literal|0
index|]
operator|==
name|String
operator|.
name|class
condition|)
block|{
return|return
literal|true
return|;
block|}
else|else
block|{
comment|// Check if object can accept a string as a constructor
try|try
block|{
if|if
condition|(
name|paramType
index|[
literal|0
index|]
operator|.
name|getConstructor
argument_list|(
operator|new
name|Class
index|[]
block|{
name|String
operator|.
name|class
block|}
block|)
operator|!=
literal|null
block|)
block|{
return|return
literal|true
return|;
block|}
block|}
catch|catch
parameter_list|(
name|NoSuchMethodException
name|e
parameter_list|)
block|{
comment|// Do nothing
block|}
block|}
block|}
block|}
end_class

begin_return
return|return
literal|false
return|;
end_return

begin_function
unit|}      private
specifier|static
name|String
name|getPropertyName
parameter_list|(
name|String
name|methodName
parameter_list|)
block|{
name|String
name|name
decl_stmt|;
if|if
condition|(
name|methodName
operator|.
name|startsWith
argument_list|(
literal|"get"
argument_list|)
condition|)
block|{
name|name
operator|=
name|methodName
operator|.
name|substring
argument_list|(
literal|3
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|methodName
operator|.
name|startsWith
argument_list|(
literal|"set"
argument_list|)
condition|)
block|{
name|name
operator|=
name|methodName
operator|.
name|substring
argument_list|(
literal|3
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|methodName
operator|.
name|startsWith
argument_list|(
literal|"is"
argument_list|)
condition|)
block|{
name|name
operator|=
name|methodName
operator|.
name|substring
argument_list|(
literal|2
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|name
operator|=
literal|""
expr_stmt|;
block|}
return|return
name|name
operator|.
name|substring
argument_list|(
literal|0
argument_list|,
literal|1
argument_list|)
operator|.
name|toLowerCase
argument_list|()
operator|+
name|name
operator|.
name|substring
argument_list|(
literal|1
argument_list|)
return|;
block|}
end_function

unit|}
end_unit

