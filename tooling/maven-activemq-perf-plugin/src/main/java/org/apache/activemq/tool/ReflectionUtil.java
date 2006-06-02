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
name|Map
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
name|lang
operator|.
name|reflect
operator|.
name|Method
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
name|Object
name|val
parameter_list|)
block|{
try|try
block|{
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
comment|//System.out.print("Invoking: " + targetClass);
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
comment|//System.out.print("." + getMethod + "()");
block|}
comment|// Invoke setter method of last class
name|String
name|name
init|=
name|tokenizer
operator|.
name|nextToken
argument_list|()
decl_stmt|;
name|String
name|methodName
init|=
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
decl_stmt|;
name|Method
name|method
init|=
name|getPrimitiveMethod
argument_list|(
name|targetClass
argument_list|,
name|methodName
argument_list|,
name|val
argument_list|)
decl_stmt|;
name|Object
index|[]
name|objVal
init|=
block|{
name|val
block|}
decl_stmt|;
name|method
operator|.
name|invoke
argument_list|(
name|target
argument_list|,
name|objVal
argument_list|)
expr_stmt|;
comment|//method.invoke(target, val);
comment|//System.out.println("." + methodName + "(" + val + ")");
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
literal|""
argument_list|,
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
name|Map
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
name|Object
name|val
init|=
name|props
operator|.
name|get
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
specifier|private
specifier|static
name|Method
name|getPrimitiveMethod
parameter_list|(
name|Class
name|objClass
parameter_list|,
name|String
name|methodName
parameter_list|,
name|Object
name|param
parameter_list|)
throws|throws
name|NoSuchMethodException
block|{
if|if
condition|(
name|param
operator|instanceof
name|Boolean
condition|)
block|{
try|try
block|{
comment|// Try using the primitive type first
return|return
name|objClass
operator|.
name|getMethod
argument_list|(
name|methodName
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
return|;
block|}
catch|catch
parameter_list|(
name|NoSuchMethodException
name|e
parameter_list|)
block|{
comment|// Try using the wrapper class next
return|return
name|objClass
operator|.
name|getMethod
argument_list|(
name|methodName
argument_list|,
operator|new
name|Class
index|[]
block|{
name|Boolean
operator|.
name|class
block|}
argument_list|)
return|;
block|}
block|}
elseif|else
if|if
condition|(
name|param
operator|instanceof
name|Integer
condition|)
block|{
try|try
block|{
comment|// Try using the primitive type first
return|return
name|objClass
operator|.
name|getMethod
argument_list|(
name|methodName
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
return|;
block|}
catch|catch
parameter_list|(
name|NoSuchMethodException
name|e
parameter_list|)
block|{
comment|// Try using the wrapper class next
return|return
name|objClass
operator|.
name|getMethod
argument_list|(
name|methodName
argument_list|,
operator|new
name|Class
index|[]
block|{
name|Integer
operator|.
name|class
block|}
argument_list|)
return|;
block|}
block|}
elseif|else
if|if
condition|(
name|param
operator|instanceof
name|Long
condition|)
block|{
try|try
block|{
comment|// Try using the primitive type first
return|return
name|objClass
operator|.
name|getMethod
argument_list|(
name|methodName
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
return|;
block|}
catch|catch
parameter_list|(
name|NoSuchMethodException
name|e
parameter_list|)
block|{
comment|// Try using the wrapper class next
return|return
name|objClass
operator|.
name|getMethod
argument_list|(
name|methodName
argument_list|,
operator|new
name|Class
index|[]
block|{
name|Long
operator|.
name|class
block|}
argument_list|)
return|;
block|}
block|}
elseif|else
if|if
condition|(
name|param
operator|instanceof
name|Short
condition|)
block|{
try|try
block|{
comment|// Try using the primitive type first
return|return
name|objClass
operator|.
name|getMethod
argument_list|(
name|methodName
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
return|;
block|}
catch|catch
parameter_list|(
name|NoSuchMethodException
name|e
parameter_list|)
block|{
comment|// Try using the wrapper class next
return|return
name|objClass
operator|.
name|getMethod
argument_list|(
name|methodName
argument_list|,
operator|new
name|Class
index|[]
block|{
name|Short
operator|.
name|class
block|}
argument_list|)
return|;
block|}
block|}
elseif|else
if|if
condition|(
name|param
operator|instanceof
name|Byte
condition|)
block|{
try|try
block|{
comment|// Try using the primitive type first
return|return
name|objClass
operator|.
name|getMethod
argument_list|(
name|methodName
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
return|;
block|}
catch|catch
parameter_list|(
name|NoSuchMethodException
name|e
parameter_list|)
block|{
comment|// Try using the wrapper class next
return|return
name|objClass
operator|.
name|getMethod
argument_list|(
name|methodName
argument_list|,
operator|new
name|Class
index|[]
block|{
name|Byte
operator|.
name|class
block|}
argument_list|)
return|;
block|}
block|}
elseif|else
if|if
condition|(
name|param
operator|instanceof
name|Character
condition|)
block|{
try|try
block|{
comment|// Try using the primitive type first
return|return
name|objClass
operator|.
name|getMethod
argument_list|(
name|methodName
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
return|;
block|}
catch|catch
parameter_list|(
name|NoSuchMethodException
name|e
parameter_list|)
block|{
comment|// Try using the wrapper class next
return|return
name|objClass
operator|.
name|getMethod
argument_list|(
name|methodName
argument_list|,
operator|new
name|Class
index|[]
block|{
name|Character
operator|.
name|class
block|}
argument_list|)
return|;
block|}
block|}
elseif|else
if|if
condition|(
name|param
operator|instanceof
name|Double
condition|)
block|{
try|try
block|{
comment|// Try using the primitive type first
return|return
name|objClass
operator|.
name|getMethod
argument_list|(
name|methodName
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
return|;
block|}
catch|catch
parameter_list|(
name|NoSuchMethodException
name|e
parameter_list|)
block|{
comment|// Try using the wrapper class next
return|return
name|objClass
operator|.
name|getMethod
argument_list|(
name|methodName
argument_list|,
operator|new
name|Class
index|[]
block|{
name|Double
operator|.
name|class
block|}
argument_list|)
return|;
block|}
block|}
elseif|else
if|if
condition|(
name|param
operator|instanceof
name|Float
condition|)
block|{
try|try
block|{
comment|// Try using the primitive type first
return|return
name|objClass
operator|.
name|getMethod
argument_list|(
name|methodName
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
return|;
block|}
catch|catch
parameter_list|(
name|NoSuchMethodException
name|e
parameter_list|)
block|{
comment|// Try using the wrapper class next
return|return
name|objClass
operator|.
name|getMethod
argument_list|(
name|methodName
argument_list|,
operator|new
name|Class
index|[]
block|{
name|Float
operator|.
name|class
block|}
argument_list|)
return|;
block|}
block|}
else|else
block|{
comment|// parameter is not a primitive
return|return
name|objClass
operator|.
name|getMethod
argument_list|(
name|methodName
argument_list|,
operator|new
name|Class
index|[]
block|{
name|param
operator|.
name|getClass
argument_list|()
block|}
argument_list|)
return|;
block|}
block|}
block|}
end_class

end_unit

