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
name|tool
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
name|Field
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
block|{      }
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
comment|// NOTE: Skip the first token, it is assume that this is an indicator for the object itself
name|tokenizer
operator|.
name|nextToken
argument_list|()
expr_stmt|;
comment|// For nested settings, get the object first
for|for
control|(
name|int
name|j
init|=
literal|0
init|;
name|j
operator|<
name|tokenizer
operator|.
name|countTokens
argument_list|()
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
operator|(
name|Object
index|[]
operator|)
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
comment|// Determine data type of property
name|Class
name|propertyType
init|=
name|getField
argument_list|(
name|targetClass
argument_list|,
name|property
argument_list|)
operator|.
name|getType
argument_list|()
decl_stmt|;
comment|// Get setter method
name|String
name|setterMethod
init|=
literal|"set"
operator|+
name|property
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
name|property
operator|.
name|substring
argument_list|(
literal|1
argument_list|)
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
name|propertyType
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
name|propertyType
operator|.
name|isPrimitive
argument_list|()
condition|)
block|{
if|if
condition|(
name|propertyType
operator|==
name|Boolean
operator|.
name|TYPE
condition|)
block|{
name|targetClass
operator|.
name|getMethod
argument_list|(
name|setterMethod
argument_list|,
operator|new
name|Class
index|[]
block|{
name|boolean
operator|.
name|class
block|}
argument_list|)
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
name|propertyType
operator|==
name|Integer
operator|.
name|TYPE
condition|)
block|{
name|targetClass
operator|.
name|getMethod
argument_list|(
name|setterMethod
argument_list|,
operator|new
name|Class
index|[]
block|{
name|int
operator|.
name|class
block|}
argument_list|)
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
name|propertyType
operator|==
name|Long
operator|.
name|TYPE
condition|)
block|{
name|targetClass
operator|.
name|getMethod
argument_list|(
name|setterMethod
argument_list|,
operator|new
name|Class
index|[]
block|{
name|long
operator|.
name|class
block|}
argument_list|)
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
name|propertyType
operator|==
name|Double
operator|.
name|TYPE
condition|)
block|{
name|targetClass
operator|.
name|getMethod
argument_list|(
name|setterMethod
argument_list|,
operator|new
name|Class
index|[]
block|{
name|double
operator|.
name|class
block|}
argument_list|)
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
name|propertyType
operator|==
name|Float
operator|.
name|TYPE
condition|)
block|{
name|targetClass
operator|.
name|getMethod
argument_list|(
name|setterMethod
argument_list|,
operator|new
name|Class
index|[]
block|{
name|float
operator|.
name|class
block|}
argument_list|)
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
name|propertyType
operator|==
name|Short
operator|.
name|TYPE
condition|)
block|{
name|targetClass
operator|.
name|getMethod
argument_list|(
name|setterMethod
argument_list|,
operator|new
name|Class
index|[]
block|{
name|short
operator|.
name|class
block|}
argument_list|)
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
name|propertyType
operator|==
name|Byte
operator|.
name|TYPE
condition|)
block|{
name|targetClass
operator|.
name|getMethod
argument_list|(
name|setterMethod
argument_list|,
operator|new
name|Class
index|[]
block|{
name|byte
operator|.
name|class
block|}
argument_list|)
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
name|propertyType
operator|==
name|Character
operator|.
name|TYPE
condition|)
block|{
name|targetClass
operator|.
name|getMethod
argument_list|(
name|setterMethod
argument_list|,
operator|new
name|Class
index|[]
block|{
name|char
operator|.
name|class
block|}
argument_list|)
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
operator|.
name|charAt
argument_list|(
literal|0
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
name|propertyType
operator|==
name|String
operator|.
name|class
condition|)
block|{
name|targetClass
operator|.
name|getMethod
argument_list|(
name|setterMethod
argument_list|,
operator|new
name|Class
index|[]
block|{
name|String
operator|.
name|class
block|}
argument_list|)
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
comment|// For unknown object type, try to call the valueOf method of the object
comment|// to convert the string to the target object type
block|}
else|else
block|{
name|Object
name|param
init|=
name|propertyType
operator|.
name|getMethod
argument_list|(
literal|"valueOf"
argument_list|,
operator|new
name|Class
index|[]
block|{
name|String
operator|.
name|class
block|}
argument_list|)
operator|.
name|invoke
argument_list|(
literal|null
argument_list|,
name|val
argument_list|)
decl_stmt|;
name|targetClass
operator|.
name|getMethod
argument_list|(
name|setterMethod
argument_list|,
operator|new
name|Class
index|[]
block|{
name|propertyType
block|}
argument_list|)
operator|.
name|invoke
argument_list|(
name|target
argument_list|,
operator|new
name|Object
index|[]
block|{
name|param
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
block|}
specifier|public
specifier|static
name|Field
name|getField
parameter_list|(
name|Class
name|targetClass
parameter_list|,
name|String
name|fieldName
parameter_list|)
throws|throws
name|NoSuchFieldException
block|{
while|while
condition|(
name|targetClass
operator|!=
literal|null
condition|)
block|{
try|try
block|{
return|return
name|targetClass
operator|.
name|getDeclaredField
argument_list|(
name|fieldName
argument_list|)
return|;
block|}
catch|catch
parameter_list|(
name|NoSuchFieldException
name|e
parameter_list|)
block|{
name|targetClass
operator|=
name|targetClass
operator|.
name|getSuperclass
argument_list|()
expr_stmt|;
block|}
block|}
throw|throw
operator|new
name|NoSuchFieldException
argument_list|(
name|fieldName
argument_list|)
throw|;
block|}
block|}
end_class

end_unit

