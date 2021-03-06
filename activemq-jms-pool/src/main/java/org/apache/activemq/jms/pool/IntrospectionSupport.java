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
name|jms
operator|.
name|pool
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
name|Method
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
operator|&&
name|value
operator|instanceof
name|String
condition|)
block|{
return|return
name|Boolean
operator|.
name|valueOf
argument_list|(
operator|(
name|String
operator|)
name|value
argument_list|)
return|;
block|}
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
block|}
end_class

end_unit

