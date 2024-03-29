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
name|broker
operator|.
name|jmx
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
name|Method
import|;
end_import

begin_import
import|import
name|java
operator|.
name|net
operator|.
name|MalformedURLException
import|;
end_import

begin_import
import|import
name|java
operator|.
name|net
operator|.
name|URL
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
name|util
operator|.
name|Collections
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Enumeration
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
name|Log4JConfigView
implements|implements
name|Log4JConfigViewMBean
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
name|Log4JConfigView
operator|.
name|class
argument_list|)
decl_stmt|;
annotation|@
name|Override
specifier|public
name|String
name|getRootLogLevel
parameter_list|()
throws|throws
name|Exception
block|{
name|ClassLoader
name|cl
init|=
name|getClassLoader
argument_list|()
decl_stmt|;
if|if
condition|(
operator|!
name|isLog4JAvailable
argument_list|(
name|cl
argument_list|)
condition|)
block|{
return|return
literal|null
return|;
block|}
name|Class
argument_list|<
name|?
argument_list|>
name|loggerClass
init|=
name|getLoggerClass
argument_list|(
name|cl
argument_list|)
decl_stmt|;
if|if
condition|(
name|loggerClass
operator|==
literal|null
condition|)
block|{
return|return
literal|null
return|;
block|}
name|Method
name|getRootLogger
init|=
name|loggerClass
operator|.
name|getMethod
argument_list|(
literal|"getRootLogger"
argument_list|,
operator|new
name|Class
index|[]
block|{}
argument_list|)
decl_stmt|;
name|Method
name|getLevel
init|=
name|loggerClass
operator|.
name|getMethod
argument_list|(
literal|"getLevel"
argument_list|,
operator|new
name|Class
index|[]
block|{}
argument_list|)
decl_stmt|;
name|Object
name|rootLogger
init|=
name|getRootLogger
operator|.
name|invoke
argument_list|(
literal|null
argument_list|,
operator|(
name|Object
index|[]
operator|)
literal|null
argument_list|)
decl_stmt|;
return|return
name|getLevel
operator|.
name|invoke
argument_list|(
name|rootLogger
argument_list|,
operator|(
name|Object
index|[]
operator|)
literal|null
argument_list|)
operator|.
name|toString
argument_list|()
return|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|setRootLogLevel
parameter_list|(
name|String
name|level
parameter_list|)
throws|throws
name|Exception
block|{
name|ClassLoader
name|cl
init|=
name|getClassLoader
argument_list|()
decl_stmt|;
if|if
condition|(
operator|!
name|isLog4JAvailable
argument_list|(
name|cl
argument_list|)
condition|)
block|{
return|return;
block|}
name|Class
argument_list|<
name|?
argument_list|>
name|loggerClass
init|=
name|getLoggerClass
argument_list|(
name|cl
argument_list|)
decl_stmt|;
name|Class
argument_list|<
name|?
argument_list|>
name|levelClass
init|=
name|getLevelClass
argument_list|(
name|cl
argument_list|)
decl_stmt|;
if|if
condition|(
name|levelClass
operator|==
literal|null
operator|||
name|loggerClass
operator|==
literal|null
condition|)
block|{
return|return;
block|}
name|String
name|targetLevel
init|=
name|level
operator|.
name|toUpperCase
argument_list|(
name|Locale
operator|.
name|US
argument_list|)
decl_stmt|;
name|Method
name|getRootLogger
init|=
name|loggerClass
operator|.
name|getMethod
argument_list|(
literal|"getRootLogger"
argument_list|,
operator|new
name|Class
index|[]
block|{}
argument_list|)
decl_stmt|;
name|Method
name|setLevel
init|=
name|loggerClass
operator|.
name|getMethod
argument_list|(
literal|"setLevel"
argument_list|,
name|levelClass
argument_list|)
decl_stmt|;
name|Object
name|rootLogger
init|=
name|getRootLogger
operator|.
name|invoke
argument_list|(
literal|null
argument_list|,
operator|(
name|Object
index|[]
operator|)
literal|null
argument_list|)
decl_stmt|;
name|Method
name|toLevel
init|=
name|levelClass
operator|.
name|getMethod
argument_list|(
literal|"toLevel"
argument_list|,
name|String
operator|.
name|class
argument_list|)
decl_stmt|;
name|Object
name|newLevel
init|=
name|toLevel
operator|.
name|invoke
argument_list|(
literal|null
argument_list|,
name|targetLevel
argument_list|)
decl_stmt|;
comment|// Check that the level conversion worked and that we got a level
comment|// that matches what was asked for.  A bad level name will result
comment|// in the lowest level value and we don't want to change unless we
comment|// matched what the user asked for.
if|if
condition|(
name|newLevel
operator|!=
literal|null
operator|&&
name|newLevel
operator|.
name|toString
argument_list|()
operator|.
name|equals
argument_list|(
name|targetLevel
argument_list|)
condition|)
block|{
name|LOG
operator|.
name|debug
argument_list|(
literal|"Set level {} for root logger."
argument_list|,
name|level
argument_list|)
expr_stmt|;
name|setLevel
operator|.
name|invoke
argument_list|(
name|rootLogger
argument_list|,
name|newLevel
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Override
specifier|public
name|List
argument_list|<
name|String
argument_list|>
name|getLoggers
parameter_list|()
throws|throws
name|Exception
block|{
name|ClassLoader
name|cl
init|=
name|getClassLoader
argument_list|()
decl_stmt|;
if|if
condition|(
operator|!
name|isLog4JAvailable
argument_list|(
name|cl
argument_list|)
condition|)
block|{
return|return
name|Collections
operator|.
name|emptyList
argument_list|()
return|;
block|}
name|Class
argument_list|<
name|?
argument_list|>
name|logManagerClass
init|=
name|getLogManagerClass
argument_list|(
name|cl
argument_list|)
decl_stmt|;
name|Class
argument_list|<
name|?
argument_list|>
name|loggerClass
init|=
name|getLoggerClass
argument_list|(
name|cl
argument_list|)
decl_stmt|;
if|if
condition|(
name|logManagerClass
operator|==
literal|null
operator|||
name|loggerClass
operator|==
literal|null
condition|)
block|{
return|return
name|Collections
operator|.
name|emptyList
argument_list|()
return|;
block|}
name|Method
name|getCurrentLoggers
init|=
name|logManagerClass
operator|.
name|getMethod
argument_list|(
literal|"getCurrentLoggers"
argument_list|,
operator|new
name|Class
index|[]
block|{}
argument_list|)
decl_stmt|;
name|Method
name|getName
init|=
name|loggerClass
operator|.
name|getMethod
argument_list|(
literal|"getName"
argument_list|,
operator|new
name|Class
index|[]
block|{}
argument_list|)
decl_stmt|;
name|List
argument_list|<
name|String
argument_list|>
name|list
init|=
operator|new
name|ArrayList
argument_list|<
name|String
argument_list|>
argument_list|()
decl_stmt|;
name|Enumeration
argument_list|<
name|?
argument_list|>
name|loggers
init|=
operator|(
name|Enumeration
argument_list|<
name|?
argument_list|>
operator|)
name|getCurrentLoggers
operator|.
name|invoke
argument_list|(
literal|null
argument_list|,
operator|(
name|Object
index|[]
operator|)
literal|null
argument_list|)
decl_stmt|;
while|while
condition|(
name|loggers
operator|.
name|hasMoreElements
argument_list|()
condition|)
block|{
name|Object
name|logger
init|=
name|loggers
operator|.
name|nextElement
argument_list|()
decl_stmt|;
if|if
condition|(
name|logger
operator|!=
literal|null
condition|)
block|{
name|list
operator|.
name|add
argument_list|(
operator|(
name|String
operator|)
name|getName
operator|.
name|invoke
argument_list|(
name|logger
argument_list|,
operator|(
name|Object
index|[]
operator|)
literal|null
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
name|LOG
operator|.
name|debug
argument_list|(
literal|"Found {} loggers"
argument_list|,
name|list
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
return|return
name|list
return|;
block|}
annotation|@
name|Override
specifier|public
name|String
name|getLogLevel
parameter_list|(
name|String
name|loggerName
parameter_list|)
throws|throws
name|Exception
block|{
name|ClassLoader
name|cl
init|=
name|getClassLoader
argument_list|()
decl_stmt|;
if|if
condition|(
operator|!
name|isLog4JAvailable
argument_list|(
name|cl
argument_list|)
condition|)
block|{
return|return
literal|null
return|;
block|}
name|Class
argument_list|<
name|?
argument_list|>
name|loggerClass
init|=
name|getLoggerClass
argument_list|(
name|cl
argument_list|)
decl_stmt|;
if|if
condition|(
name|loggerClass
operator|==
literal|null
condition|)
block|{
return|return
literal|null
return|;
block|}
name|Method
name|getLogger
init|=
name|loggerClass
operator|.
name|getMethod
argument_list|(
literal|"getLogger"
argument_list|,
name|String
operator|.
name|class
argument_list|)
decl_stmt|;
name|String
name|logLevel
init|=
literal|null
decl_stmt|;
if|if
condition|(
name|loggerName
operator|!=
literal|null
operator|&&
operator|!
name|loggerName
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
name|Object
name|logger
init|=
name|getLogger
operator|.
name|invoke
argument_list|(
literal|null
argument_list|,
name|loggerName
argument_list|)
decl_stmt|;
if|if
condition|(
name|logger
operator|!=
literal|null
condition|)
block|{
name|LOG
operator|.
name|debug
argument_list|(
literal|"Found level {} for logger: {}"
argument_list|,
name|logLevel
argument_list|,
name|loggerName
argument_list|)
expr_stmt|;
name|Method
name|getLevel
init|=
name|loggerClass
operator|.
name|getMethod
argument_list|(
literal|"getLevel"
argument_list|,
operator|new
name|Class
index|[]
block|{}
argument_list|)
decl_stmt|;
name|Object
name|level
init|=
name|getLevel
operator|.
name|invoke
argument_list|(
name|logger
argument_list|,
operator|(
name|Object
index|[]
operator|)
literal|null
argument_list|)
decl_stmt|;
if|if
condition|(
name|level
operator|!=
literal|null
condition|)
block|{
name|logLevel
operator|=
name|level
operator|.
name|toString
argument_list|()
expr_stmt|;
block|}
else|else
block|{
name|Method
name|getRootLogger
init|=
name|loggerClass
operator|.
name|getMethod
argument_list|(
literal|"getRootLogger"
argument_list|,
operator|new
name|Class
index|[]
block|{}
argument_list|)
decl_stmt|;
name|Object
name|rootLogger
init|=
name|getRootLogger
operator|.
name|invoke
argument_list|(
literal|null
argument_list|,
operator|(
name|Object
index|[]
operator|)
literal|null
argument_list|)
decl_stmt|;
name|logLevel
operator|=
name|getLevel
operator|.
name|invoke
argument_list|(
name|rootLogger
argument_list|,
operator|(
name|Object
index|[]
operator|)
literal|null
argument_list|)
operator|.
name|toString
argument_list|()
expr_stmt|;
block|}
block|}
block|}
else|else
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"Logger names cannot be null or empty strings"
argument_list|)
throw|;
block|}
return|return
name|logLevel
return|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|setLogLevel
parameter_list|(
name|String
name|loggerName
parameter_list|,
name|String
name|level
parameter_list|)
throws|throws
name|Exception
block|{
if|if
condition|(
name|loggerName
operator|==
literal|null
operator|||
name|loggerName
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"Logger names cannot be null or empty strings"
argument_list|)
throw|;
block|}
if|if
condition|(
name|level
operator|==
literal|null
operator|||
name|level
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"Level name cannot be null or empty strings"
argument_list|)
throw|;
block|}
name|ClassLoader
name|cl
init|=
name|getClassLoader
argument_list|()
decl_stmt|;
if|if
condition|(
operator|!
name|isLog4JAvailable
argument_list|(
name|cl
argument_list|)
condition|)
block|{
return|return;
block|}
name|Class
argument_list|<
name|?
argument_list|>
name|loggerClass
init|=
name|getLoggerClass
argument_list|(
name|cl
argument_list|)
decl_stmt|;
name|Class
argument_list|<
name|?
argument_list|>
name|levelClass
init|=
name|getLevelClass
argument_list|(
name|cl
argument_list|)
decl_stmt|;
if|if
condition|(
name|loggerClass
operator|==
literal|null
operator|||
name|levelClass
operator|==
literal|null
condition|)
block|{
return|return;
block|}
name|String
name|targetLevel
init|=
name|level
operator|.
name|toUpperCase
argument_list|(
name|Locale
operator|.
name|US
argument_list|)
decl_stmt|;
name|Method
name|getLogger
init|=
name|loggerClass
operator|.
name|getMethod
argument_list|(
literal|"getLogger"
argument_list|,
name|String
operator|.
name|class
argument_list|)
decl_stmt|;
name|Method
name|setLevel
init|=
name|loggerClass
operator|.
name|getMethod
argument_list|(
literal|"setLevel"
argument_list|,
name|levelClass
argument_list|)
decl_stmt|;
name|Method
name|toLevel
init|=
name|levelClass
operator|.
name|getMethod
argument_list|(
literal|"toLevel"
argument_list|,
name|String
operator|.
name|class
argument_list|)
decl_stmt|;
name|Object
name|logger
init|=
name|getLogger
operator|.
name|invoke
argument_list|(
literal|null
argument_list|,
name|loggerName
argument_list|)
decl_stmt|;
if|if
condition|(
name|logger
operator|!=
literal|null
condition|)
block|{
name|Object
name|newLevel
init|=
name|toLevel
operator|.
name|invoke
argument_list|(
literal|null
argument_list|,
name|targetLevel
argument_list|)
decl_stmt|;
comment|// Check that the level conversion worked and that we got a level
comment|// that matches what was asked for.  A bad level name will result
comment|// in the lowest level value and we don't want to change unless we
comment|// matched what the user asked for.
if|if
condition|(
name|newLevel
operator|!=
literal|null
operator|&&
name|newLevel
operator|.
name|toString
argument_list|()
operator|.
name|equals
argument_list|(
name|targetLevel
argument_list|)
condition|)
block|{
name|LOG
operator|.
name|debug
argument_list|(
literal|"Set level {} for logger: {}"
argument_list|,
name|level
argument_list|,
name|loggerName
argument_list|)
expr_stmt|;
name|setLevel
operator|.
name|invoke
argument_list|(
name|logger
argument_list|,
name|newLevel
argument_list|)
expr_stmt|;
block|}
block|}
block|}
annotation|@
name|Override
specifier|public
name|void
name|reloadLog4jProperties
parameter_list|()
throws|throws
name|Throwable
block|{
name|doReloadLog4jProperties
argument_list|()
expr_stmt|;
block|}
comment|//---------- Static Helper Methods ---------------------------------------//
specifier|public
specifier|static
name|void
name|doReloadLog4jProperties
parameter_list|()
throws|throws
name|Throwable
block|{
try|try
block|{
name|ClassLoader
name|cl
init|=
name|Log4JConfigView
operator|.
name|class
operator|.
name|getClassLoader
argument_list|()
decl_stmt|;
name|Class
argument_list|<
name|?
argument_list|>
name|logManagerClass
init|=
name|getLogManagerClass
argument_list|(
name|cl
argument_list|)
decl_stmt|;
if|if
condition|(
name|logManagerClass
operator|==
literal|null
condition|)
block|{
name|LOG
operator|.
name|debug
argument_list|(
literal|"Could not locate log4j classes on classpath."
argument_list|)
expr_stmt|;
return|return;
block|}
name|Method
name|resetConfiguration
init|=
name|logManagerClass
operator|.
name|getMethod
argument_list|(
literal|"resetConfiguration"
argument_list|,
operator|new
name|Class
index|[]
block|{}
argument_list|)
decl_stmt|;
name|resetConfiguration
operator|.
name|invoke
argument_list|(
literal|null
argument_list|,
operator|new
name|Object
index|[]
block|{}
argument_list|)
expr_stmt|;
name|String
name|configurationOptionStr
init|=
name|System
operator|.
name|getProperty
argument_list|(
literal|"log4j.configuration"
argument_list|)
decl_stmt|;
name|URL
name|log4jprops
init|=
literal|null
decl_stmt|;
if|if
condition|(
name|configurationOptionStr
operator|!=
literal|null
condition|)
block|{
try|try
block|{
name|log4jprops
operator|=
operator|new
name|URL
argument_list|(
name|configurationOptionStr
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|MalformedURLException
name|ex
parameter_list|)
block|{
name|log4jprops
operator|=
name|cl
operator|.
name|getResource
argument_list|(
literal|"log4j.properties"
argument_list|)
expr_stmt|;
block|}
block|}
else|else
block|{
name|log4jprops
operator|=
name|cl
operator|.
name|getResource
argument_list|(
literal|"log4j.properties"
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|log4jprops
operator|!=
literal|null
condition|)
block|{
name|Class
argument_list|<
name|?
argument_list|>
name|propertyConfiguratorClass
init|=
name|cl
operator|.
name|loadClass
argument_list|(
literal|"org.apache.log4j.PropertyConfigurator"
argument_list|)
decl_stmt|;
name|Method
name|configure
init|=
name|propertyConfiguratorClass
operator|.
name|getMethod
argument_list|(
literal|"configure"
argument_list|,
operator|new
name|Class
index|[]
block|{
name|URL
operator|.
name|class
block|}
argument_list|)
decl_stmt|;
name|configure
operator|.
name|invoke
argument_list|(
literal|null
argument_list|,
operator|new
name|Object
index|[]
block|{
name|log4jprops
block|}
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
throw|throw
name|e
operator|.
name|getTargetException
argument_list|()
throw|;
block|}
block|}
specifier|public
specifier|static
name|boolean
name|isLog4JAvailable
parameter_list|()
block|{
return|return
name|isLog4JAvailable
argument_list|(
name|getClassLoader
argument_list|()
argument_list|)
return|;
block|}
specifier|private
specifier|static
name|ClassLoader
name|getClassLoader
parameter_list|()
block|{
return|return
name|Log4JConfigView
operator|.
name|class
operator|.
name|getClassLoader
argument_list|()
return|;
block|}
specifier|private
specifier|static
name|boolean
name|isLog4JAvailable
parameter_list|(
name|ClassLoader
name|cl
parameter_list|)
block|{
if|if
condition|(
name|getLogManagerClass
argument_list|(
name|cl
argument_list|)
operator|!=
literal|null
condition|)
block|{
return|return
literal|true
return|;
block|}
name|LOG
operator|.
name|debug
argument_list|(
literal|"Could not locate log4j classes on classpath."
argument_list|)
expr_stmt|;
return|return
literal|false
return|;
block|}
specifier|private
specifier|static
name|Class
argument_list|<
name|?
argument_list|>
name|getLogManagerClass
parameter_list|(
name|ClassLoader
name|cl
parameter_list|)
block|{
name|Class
argument_list|<
name|?
argument_list|>
name|logManagerClass
init|=
literal|null
decl_stmt|;
try|try
block|{
name|logManagerClass
operator|=
name|cl
operator|.
name|loadClass
argument_list|(
literal|"org.apache.log4j.LogManager"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|ClassNotFoundException
name|e
parameter_list|)
block|{         }
return|return
name|logManagerClass
return|;
block|}
specifier|private
specifier|static
name|Class
argument_list|<
name|?
argument_list|>
name|getLoggerClass
parameter_list|(
name|ClassLoader
name|cl
parameter_list|)
block|{
name|Class
argument_list|<
name|?
argument_list|>
name|loggerClass
init|=
literal|null
decl_stmt|;
try|try
block|{
name|loggerClass
operator|=
name|cl
operator|.
name|loadClass
argument_list|(
literal|"org.apache.log4j.Logger"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|ClassNotFoundException
name|e
parameter_list|)
block|{         }
return|return
name|loggerClass
return|;
block|}
specifier|private
specifier|static
name|Class
argument_list|<
name|?
argument_list|>
name|getLevelClass
parameter_list|(
name|ClassLoader
name|cl
parameter_list|)
block|{
name|Class
argument_list|<
name|?
argument_list|>
name|levelClass
init|=
literal|null
decl_stmt|;
try|try
block|{
name|levelClass
operator|=
name|cl
operator|.
name|loadClass
argument_list|(
literal|"org.apache.log4j.Level"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|ClassNotFoundException
name|e
parameter_list|)
block|{         }
return|return
name|levelClass
return|;
block|}
block|}
end_class

end_unit

