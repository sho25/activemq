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
name|javax
operator|.
name|jms
operator|.
name|ConnectionFactory
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
name|lang
operator|.
name|reflect
operator|.
name|Constructor
import|;
end_import

begin_class
specifier|public
class|class
name|JmsBasicClientSupport
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
name|JmsBasicClientSupport
operator|.
name|class
argument_list|)
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|String
name|DEFAULT_CONNECTION_FACTORY_CLASS
init|=
literal|"org.apache.activemq.ActiveMQConnectionFactory"
decl_stmt|;
specifier|public
name|ConnectionFactory
name|createConnectionFactory
parameter_list|(
name|String
name|url
parameter_list|)
block|{
return|return
name|createConnectionFactory
argument_list|(
name|DEFAULT_CONNECTION_FACTORY_CLASS
argument_list|,
name|url
argument_list|,
literal|null
argument_list|)
return|;
block|}
specifier|public
name|ConnectionFactory
name|createConnectionFactory
parameter_list|(
name|String
name|url
parameter_list|,
name|Map
name|props
parameter_list|)
block|{
return|return
name|createConnectionFactory
argument_list|(
name|DEFAULT_CONNECTION_FACTORY_CLASS
argument_list|,
name|url
argument_list|,
name|props
argument_list|)
return|;
block|}
specifier|public
name|ConnectionFactory
name|createConnectionFactory
parameter_list|(
name|String
name|clazz
parameter_list|,
name|String
name|url
parameter_list|)
block|{
return|return
name|createConnectionFactory
argument_list|(
name|clazz
argument_list|,
name|url
argument_list|,
literal|null
argument_list|)
return|;
block|}
specifier|public
name|ConnectionFactory
name|createConnectionFactory
parameter_list|(
name|String
name|clazz
parameter_list|,
name|String
name|url
parameter_list|,
name|Map
name|props
parameter_list|)
block|{
if|if
condition|(
name|clazz
operator|==
literal|null
operator|||
name|clazz
operator|==
literal|""
condition|)
block|{
throw|throw
operator|new
name|RuntimeException
argument_list|(
literal|"No class definition specified to create connection factory."
argument_list|)
throw|;
block|}
name|ConnectionFactory
name|f
init|=
name|instantiateConnectionFactory
argument_list|(
name|clazz
argument_list|,
name|url
argument_list|)
decl_stmt|;
if|if
condition|(
name|props
operator|!=
literal|null
condition|)
block|{
name|ReflectionUtil
operator|.
name|configureClass
argument_list|(
name|f
argument_list|,
name|props
argument_list|)
expr_stmt|;
block|}
return|return
name|f
return|;
block|}
specifier|protected
name|ConnectionFactory
name|instantiateConnectionFactory
parameter_list|(
name|String
name|clazz
parameter_list|,
name|String
name|url
parameter_list|)
block|{
try|try
block|{
name|Class
name|factoryClass
init|=
name|Class
operator|.
name|forName
argument_list|(
name|clazz
argument_list|)
decl_stmt|;
name|Constructor
name|c
init|=
name|factoryClass
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
name|ConnectionFactory
name|factoryObj
init|=
operator|(
name|ConnectionFactory
operator|)
name|c
operator|.
name|newInstance
argument_list|(
operator|new
name|Object
index|[]
block|{
name|url
block|}
argument_list|)
decl_stmt|;
return|return
name|factoryObj
return|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|RuntimeException
argument_list|(
name|e
argument_list|)
throw|;
block|}
block|}
block|}
end_class

end_unit

