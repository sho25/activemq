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
name|io
operator|.
name|IOException
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|InputStream
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|ObjectInputStream
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|ObjectStreamClass
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
name|Proxy
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|*
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
class|class
name|ClassLoadingAwareObjectInputStream
extends|extends
name|ObjectInputStream
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
name|ClassLoadingAwareObjectInputStream
operator|.
name|class
argument_list|)
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|ClassLoader
name|FALLBACK_CLASS_LOADER
init|=
name|ClassLoadingAwareObjectInputStream
operator|.
name|class
operator|.
name|getClassLoader
argument_list|()
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|String
index|[]
name|serializablePackages
decl_stmt|;
specifier|private
name|List
argument_list|<
name|String
argument_list|>
name|trustedPackages
init|=
operator|new
name|ArrayList
argument_list|<
name|String
argument_list|>
argument_list|()
decl_stmt|;
specifier|private
name|boolean
name|trustAllPackages
init|=
literal|false
decl_stmt|;
specifier|private
specifier|final
name|ClassLoader
name|inLoader
decl_stmt|;
static|static
block|{
name|serializablePackages
operator|=
name|System
operator|.
name|getProperty
argument_list|(
literal|"org.apache.activemq.SERIALIZABLE_PACKAGES"
argument_list|,
literal|"org.apache.activemq,org.fusesource.hawtbuf,com.thoughtworks.xstream.mapper"
argument_list|)
operator|.
name|split
argument_list|(
literal|","
argument_list|)
expr_stmt|;
block|}
specifier|public
name|ClassLoadingAwareObjectInputStream
parameter_list|(
name|InputStream
name|in
parameter_list|)
throws|throws
name|IOException
block|{
name|super
argument_list|(
name|in
argument_list|)
expr_stmt|;
name|inLoader
operator|=
name|in
operator|.
name|getClass
argument_list|()
operator|.
name|getClassLoader
argument_list|()
expr_stmt|;
name|trustedPackages
operator|.
name|addAll
argument_list|(
name|Arrays
operator|.
name|asList
argument_list|(
name|serializablePackages
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|protected
name|Class
argument_list|<
name|?
argument_list|>
name|resolveClass
parameter_list|(
name|ObjectStreamClass
name|classDesc
parameter_list|)
throws|throws
name|IOException
throws|,
name|ClassNotFoundException
block|{
name|ClassLoader
name|cl
init|=
name|Thread
operator|.
name|currentThread
argument_list|()
operator|.
name|getContextClassLoader
argument_list|()
decl_stmt|;
name|Class
name|clazz
init|=
name|load
argument_list|(
name|classDesc
operator|.
name|getName
argument_list|()
argument_list|,
name|cl
argument_list|,
name|inLoader
argument_list|)
decl_stmt|;
name|checkSecurity
argument_list|(
name|clazz
argument_list|)
expr_stmt|;
return|return
name|clazz
return|;
block|}
annotation|@
name|Override
specifier|protected
name|Class
argument_list|<
name|?
argument_list|>
name|resolveProxyClass
parameter_list|(
name|String
index|[]
name|interfaces
parameter_list|)
throws|throws
name|IOException
throws|,
name|ClassNotFoundException
block|{
name|ClassLoader
name|cl
init|=
name|Thread
operator|.
name|currentThread
argument_list|()
operator|.
name|getContextClassLoader
argument_list|()
decl_stmt|;
name|Class
index|[]
name|cinterfaces
init|=
operator|new
name|Class
index|[
name|interfaces
operator|.
name|length
index|]
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
name|cinterfaces
index|[
name|i
index|]
operator|=
name|load
argument_list|(
name|interfaces
index|[
name|i
index|]
argument_list|,
name|cl
argument_list|)
expr_stmt|;
block|}
name|Class
name|clazz
init|=
literal|null
decl_stmt|;
try|try
block|{
name|clazz
operator|=
name|Proxy
operator|.
name|getProxyClass
argument_list|(
name|cl
argument_list|,
name|cinterfaces
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IllegalArgumentException
name|e
parameter_list|)
block|{
try|try
block|{
name|clazz
operator|=
name|Proxy
operator|.
name|getProxyClass
argument_list|(
name|inLoader
argument_list|,
name|cinterfaces
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IllegalArgumentException
name|e1
parameter_list|)
block|{
comment|// ignore
block|}
try|try
block|{
name|clazz
operator|=
name|Proxy
operator|.
name|getProxyClass
argument_list|(
name|FALLBACK_CLASS_LOADER
argument_list|,
name|cinterfaces
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IllegalArgumentException
name|e2
parameter_list|)
block|{
comment|// ignore
block|}
block|}
if|if
condition|(
name|clazz
operator|!=
literal|null
condition|)
block|{
name|checkSecurity
argument_list|(
name|clazz
argument_list|)
expr_stmt|;
return|return
name|clazz
return|;
block|}
else|else
block|{
throw|throw
operator|new
name|ClassNotFoundException
argument_list|(
literal|null
argument_list|)
throw|;
block|}
block|}
specifier|public
specifier|static
name|boolean
name|isAllAllowed
parameter_list|()
block|{
return|return
name|serializablePackages
operator|.
name|length
operator|==
literal|1
operator|&&
name|serializablePackages
index|[
literal|0
index|]
operator|.
name|equals
argument_list|(
literal|"*"
argument_list|)
return|;
block|}
specifier|private
name|boolean
name|trustAllPackages
parameter_list|()
block|{
return|return
name|trustAllPackages
operator|||
operator|(
name|trustedPackages
operator|.
name|size
argument_list|()
operator|==
literal|1
operator|&&
name|trustedPackages
operator|.
name|get
argument_list|(
literal|0
argument_list|)
operator|.
name|equals
argument_list|(
literal|"*"
argument_list|)
operator|)
return|;
block|}
specifier|private
name|void
name|checkSecurity
parameter_list|(
name|Class
name|clazz
parameter_list|)
throws|throws
name|ClassNotFoundException
block|{
if|if
condition|(
operator|!
name|clazz
operator|.
name|isPrimitive
argument_list|()
condition|)
block|{
if|if
condition|(
name|clazz
operator|.
name|getPackage
argument_list|()
operator|!=
literal|null
operator|&&
operator|!
name|trustAllPackages
argument_list|()
condition|)
block|{
name|boolean
name|found
init|=
literal|false
decl_stmt|;
for|for
control|(
name|String
name|packageName
range|:
name|getTrustedPackages
argument_list|()
control|)
block|{
if|if
condition|(
name|clazz
operator|.
name|getPackage
argument_list|()
operator|.
name|getName
argument_list|()
operator|.
name|equals
argument_list|(
name|packageName
argument_list|)
operator|||
name|clazz
operator|.
name|getPackage
argument_list|()
operator|.
name|getName
argument_list|()
operator|.
name|startsWith
argument_list|(
name|packageName
operator|+
literal|"."
argument_list|)
condition|)
block|{
name|found
operator|=
literal|true
expr_stmt|;
break|break;
block|}
block|}
if|if
condition|(
operator|!
name|found
condition|)
block|{
throw|throw
operator|new
name|ClassNotFoundException
argument_list|(
literal|"Forbidden "
operator|+
name|clazz
operator|+
literal|"! This class is not trusted to be serialized as ObjectMessage payload. Please take a look at http://activemq.apache.org/objectmessage.html for more information on how to configure trusted classes."
argument_list|)
throw|;
block|}
block|}
block|}
block|}
specifier|private
name|Class
argument_list|<
name|?
argument_list|>
name|load
parameter_list|(
name|String
name|className
parameter_list|,
name|ClassLoader
modifier|...
name|cl
parameter_list|)
throws|throws
name|ClassNotFoundException
block|{
comment|// check for simple types first
specifier|final
name|Class
argument_list|<
name|?
argument_list|>
name|clazz
init|=
name|loadSimpleType
argument_list|(
name|className
argument_list|)
decl_stmt|;
if|if
condition|(
name|clazz
operator|!=
literal|null
condition|)
block|{
name|LOG
operator|.
name|trace
argument_list|(
literal|"Loaded class: {} as simple type -> "
argument_list|,
name|className
argument_list|,
name|clazz
argument_list|)
expr_stmt|;
return|return
name|clazz
return|;
block|}
comment|// try the different class loaders
for|for
control|(
name|ClassLoader
name|loader
range|:
name|cl
control|)
block|{
name|LOG
operator|.
name|trace
argument_list|(
literal|"Attempting to load class: {} using classloader: {}"
argument_list|,
name|className
argument_list|,
name|cl
argument_list|)
expr_stmt|;
try|try
block|{
name|Class
argument_list|<
name|?
argument_list|>
name|answer
init|=
name|Class
operator|.
name|forName
argument_list|(
name|className
argument_list|,
literal|false
argument_list|,
name|loader
argument_list|)
decl_stmt|;
if|if
condition|(
name|LOG
operator|.
name|isTraceEnabled
argument_list|()
condition|)
block|{
name|LOG
operator|.
name|trace
argument_list|(
literal|"Loaded class: {} using classloader: {} -> "
argument_list|,
operator|new
name|Object
index|[]
block|{
name|className
block|,
name|cl
block|,
name|answer
block|}
argument_list|)
expr_stmt|;
block|}
return|return
name|answer
return|;
block|}
catch|catch
parameter_list|(
name|ClassNotFoundException
name|e
parameter_list|)
block|{
name|LOG
operator|.
name|trace
argument_list|(
literal|"Class not found: {} using classloader: {}"
argument_list|,
name|className
argument_list|,
name|cl
argument_list|)
expr_stmt|;
comment|// ignore
block|}
block|}
comment|// and then the fallback class loader
return|return
name|Class
operator|.
name|forName
argument_list|(
name|className
argument_list|,
literal|false
argument_list|,
name|FALLBACK_CLASS_LOADER
argument_list|)
return|;
block|}
comment|/**      * Load a simple type      *      * @param name the name of the class to load      * @return the class or<tt>null</tt> if it could not be loaded      */
specifier|public
specifier|static
name|Class
argument_list|<
name|?
argument_list|>
name|loadSimpleType
parameter_list|(
name|String
name|name
parameter_list|)
block|{
comment|// code from ObjectHelper.loadSimpleType in Apache Camel
comment|// special for byte[] or Object[] as its common to use
if|if
condition|(
literal|"java.lang.byte[]"
operator|.
name|equals
argument_list|(
name|name
argument_list|)
operator|||
literal|"byte[]"
operator|.
name|equals
argument_list|(
name|name
argument_list|)
condition|)
block|{
return|return
name|byte
index|[]
operator|.
name|class
return|;
block|}
elseif|else
if|if
condition|(
literal|"java.lang.Byte[]"
operator|.
name|equals
argument_list|(
name|name
argument_list|)
operator|||
literal|"Byte[]"
operator|.
name|equals
argument_list|(
name|name
argument_list|)
condition|)
block|{
return|return
name|Byte
index|[]
operator|.
name|class
return|;
block|}
elseif|else
if|if
condition|(
literal|"java.lang.Object[]"
operator|.
name|equals
argument_list|(
name|name
argument_list|)
operator|||
literal|"Object[]"
operator|.
name|equals
argument_list|(
name|name
argument_list|)
condition|)
block|{
return|return
name|Object
index|[]
operator|.
name|class
return|;
block|}
elseif|else
if|if
condition|(
literal|"java.lang.String[]"
operator|.
name|equals
argument_list|(
name|name
argument_list|)
operator|||
literal|"String[]"
operator|.
name|equals
argument_list|(
name|name
argument_list|)
condition|)
block|{
return|return
name|String
index|[]
operator|.
name|class
return|;
comment|// and these is common as well
block|}
elseif|else
if|if
condition|(
literal|"java.lang.String"
operator|.
name|equals
argument_list|(
name|name
argument_list|)
operator|||
literal|"String"
operator|.
name|equals
argument_list|(
name|name
argument_list|)
condition|)
block|{
return|return
name|String
operator|.
name|class
return|;
block|}
elseif|else
if|if
condition|(
literal|"java.lang.Boolean"
operator|.
name|equals
argument_list|(
name|name
argument_list|)
operator|||
literal|"Boolean"
operator|.
name|equals
argument_list|(
name|name
argument_list|)
condition|)
block|{
return|return
name|Boolean
operator|.
name|class
return|;
block|}
elseif|else
if|if
condition|(
literal|"boolean"
operator|.
name|equals
argument_list|(
name|name
argument_list|)
condition|)
block|{
return|return
name|boolean
operator|.
name|class
return|;
block|}
elseif|else
if|if
condition|(
literal|"java.lang.Integer"
operator|.
name|equals
argument_list|(
name|name
argument_list|)
operator|||
literal|"Integer"
operator|.
name|equals
argument_list|(
name|name
argument_list|)
condition|)
block|{
return|return
name|Integer
operator|.
name|class
return|;
block|}
elseif|else
if|if
condition|(
literal|"int"
operator|.
name|equals
argument_list|(
name|name
argument_list|)
condition|)
block|{
return|return
name|int
operator|.
name|class
return|;
block|}
elseif|else
if|if
condition|(
literal|"java.lang.Long"
operator|.
name|equals
argument_list|(
name|name
argument_list|)
operator|||
literal|"Long"
operator|.
name|equals
argument_list|(
name|name
argument_list|)
condition|)
block|{
return|return
name|Long
operator|.
name|class
return|;
block|}
elseif|else
if|if
condition|(
literal|"long"
operator|.
name|equals
argument_list|(
name|name
argument_list|)
condition|)
block|{
return|return
name|long
operator|.
name|class
return|;
block|}
elseif|else
if|if
condition|(
literal|"java.lang.Short"
operator|.
name|equals
argument_list|(
name|name
argument_list|)
operator|||
literal|"Short"
operator|.
name|equals
argument_list|(
name|name
argument_list|)
condition|)
block|{
return|return
name|Short
operator|.
name|class
return|;
block|}
elseif|else
if|if
condition|(
literal|"short"
operator|.
name|equals
argument_list|(
name|name
argument_list|)
condition|)
block|{
return|return
name|short
operator|.
name|class
return|;
block|}
elseif|else
if|if
condition|(
literal|"java.lang.Byte"
operator|.
name|equals
argument_list|(
name|name
argument_list|)
operator|||
literal|"Byte"
operator|.
name|equals
argument_list|(
name|name
argument_list|)
condition|)
block|{
return|return
name|Byte
operator|.
name|class
return|;
block|}
elseif|else
if|if
condition|(
literal|"byte"
operator|.
name|equals
argument_list|(
name|name
argument_list|)
condition|)
block|{
return|return
name|byte
operator|.
name|class
return|;
block|}
elseif|else
if|if
condition|(
literal|"java.lang.Float"
operator|.
name|equals
argument_list|(
name|name
argument_list|)
operator|||
literal|"Float"
operator|.
name|equals
argument_list|(
name|name
argument_list|)
condition|)
block|{
return|return
name|Float
operator|.
name|class
return|;
block|}
elseif|else
if|if
condition|(
literal|"float"
operator|.
name|equals
argument_list|(
name|name
argument_list|)
condition|)
block|{
return|return
name|float
operator|.
name|class
return|;
block|}
elseif|else
if|if
condition|(
literal|"java.lang.Double"
operator|.
name|equals
argument_list|(
name|name
argument_list|)
operator|||
literal|"Double"
operator|.
name|equals
argument_list|(
name|name
argument_list|)
condition|)
block|{
return|return
name|Double
operator|.
name|class
return|;
block|}
elseif|else
if|if
condition|(
literal|"double"
operator|.
name|equals
argument_list|(
name|name
argument_list|)
condition|)
block|{
return|return
name|double
operator|.
name|class
return|;
block|}
elseif|else
if|if
condition|(
literal|"void"
operator|.
name|equals
argument_list|(
name|name
argument_list|)
condition|)
block|{
return|return
name|void
operator|.
name|class
return|;
block|}
return|return
literal|null
return|;
block|}
specifier|public
name|List
argument_list|<
name|String
argument_list|>
name|getTrustedPackages
parameter_list|()
block|{
return|return
name|trustedPackages
return|;
block|}
specifier|public
name|void
name|setTrustedPackages
parameter_list|(
name|List
argument_list|<
name|String
argument_list|>
name|trustedPackages
parameter_list|)
block|{
name|this
operator|.
name|trustedPackages
operator|=
name|trustedPackages
expr_stmt|;
block|}
specifier|public
name|void
name|addTrustedPackage
parameter_list|(
name|String
name|trustedPackage
parameter_list|)
block|{
name|this
operator|.
name|trustedPackages
operator|.
name|add
argument_list|(
name|trustedPackage
argument_list|)
expr_stmt|;
block|}
specifier|public
name|boolean
name|isTrustAllPackages
parameter_list|()
block|{
return|return
name|trustAllPackages
return|;
block|}
specifier|public
name|void
name|setTrustAllPackages
parameter_list|(
name|boolean
name|trustAllPackages
parameter_list|)
block|{
name|this
operator|.
name|trustAllPackages
operator|=
name|trustAllPackages
expr_stmt|;
block|}
block|}
end_class

end_unit

