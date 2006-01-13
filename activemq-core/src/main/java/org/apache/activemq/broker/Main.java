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
name|broker
package|;
end_package

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|File
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
name|Method
import|;
end_import

begin_import
import|import
name|java
operator|.
name|net
operator|.
name|JarURLConnection
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
name|URI
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
name|net
operator|.
name|URLClassLoader
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
name|List
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|LinkedList
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
name|Iterator
import|;
end_import

begin_comment
comment|/**  * Main class that can bootstrap an ActiveMQ broker console. Handles command line  * argument parsing to set up and run broker tasks.  *  * @version $Revision$  */
end_comment

begin_class
specifier|public
class|class
name|Main
block|{
specifier|public
specifier|static
specifier|final
name|int
name|TASK_DEFAULT
init|=
literal|0
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|int
name|TASK_START
init|=
literal|1
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|int
name|TASK_STOP
init|=
literal|2
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|int
name|TASK_LIST
init|=
literal|3
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|int
name|TASK_QUERY
init|=
literal|4
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|String
name|TASK_DEFAULT_CLASS
init|=
literal|"org.apache.activemq.broker.console.DefaultTask"
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|String
name|TASK_START_CLASS
init|=
literal|"org.apache.activemq.broker.console.StartTask"
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|String
name|TASK_SHUTDOWN_CLASS
init|=
literal|"org.apache.activemq.broker.console.ShutdownTask"
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|String
name|TASK_LIST_CLASS
init|=
literal|"org.apache.activemq.broker.console.ListTask"
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|String
name|TASK_QUERY_CLASS
init|=
literal|"org.apache.activemq.broker.console.QueryTask"
decl_stmt|;
specifier|private
name|int
name|taskType
decl_stmt|;
specifier|private
name|File
name|activeMQHome
decl_stmt|;
specifier|private
name|ClassLoader
name|classLoader
decl_stmt|;
specifier|private
name|List
name|extensions
init|=
operator|new
name|ArrayList
argument_list|(
literal|5
argument_list|)
decl_stmt|;
specifier|private
specifier|static
name|boolean
name|useDefExt
init|=
literal|true
decl_stmt|;
specifier|public
specifier|static
name|void
name|main
parameter_list|(
name|String
index|[]
name|args
parameter_list|)
block|{
name|Main
name|app
init|=
operator|new
name|Main
argument_list|()
decl_stmt|;
comment|// Convert arguments to collection for easier management
name|List
name|tokens
init|=
operator|new
name|LinkedList
argument_list|(
name|Arrays
operator|.
name|asList
argument_list|(
name|args
argument_list|)
argument_list|)
decl_stmt|;
comment|// First token should be task type (start|stop|list|query)
name|app
operator|.
name|setTaskType
argument_list|(
name|app
operator|.
name|parseTask
argument_list|(
name|tokens
argument_list|)
argument_list|)
expr_stmt|;
comment|// Parse for extension directory option
name|app
operator|.
name|parseExtensions
argument_list|(
name|tokens
argument_list|)
expr_stmt|;
comment|// Add default extension directories
if|if
condition|(
name|useDefExt
operator|&&
name|app
operator|.
name|canUseExtdir
argument_list|()
condition|)
block|{
name|app
operator|.
name|addExtensionDirectory
argument_list|(
operator|new
name|File
argument_list|(
name|app
operator|.
name|getActiveMQHome
argument_list|()
argument_list|,
literal|"conf"
argument_list|)
argument_list|)
expr_stmt|;
name|app
operator|.
name|addExtensionDirectory
argument_list|(
operator|new
name|File
argument_list|(
name|app
operator|.
name|getActiveMQHome
argument_list|()
argument_list|,
literal|"lib"
argument_list|)
argument_list|)
expr_stmt|;
name|app
operator|.
name|addExtensionDirectory
argument_list|(
operator|new
name|File
argument_list|(
operator|new
name|File
argument_list|(
name|app
operator|.
name|getActiveMQHome
argument_list|()
argument_list|,
literal|"lib"
argument_list|)
argument_list|,
literal|"optional"
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|// Succeeding tokens should be the task data
try|try
block|{
switch|switch
condition|(
name|app
operator|.
name|getTaskType
argument_list|()
condition|)
block|{
case|case
name|TASK_START
case|:
name|app
operator|.
name|runTaskClass
argument_list|(
name|TASK_START_CLASS
argument_list|,
name|tokens
argument_list|)
expr_stmt|;
break|break;
case|case
name|TASK_STOP
case|:
name|app
operator|.
name|runTaskClass
argument_list|(
name|TASK_SHUTDOWN_CLASS
argument_list|,
name|tokens
argument_list|)
expr_stmt|;
break|break;
case|case
name|TASK_LIST
case|:
name|app
operator|.
name|runTaskClass
argument_list|(
name|TASK_LIST_CLASS
argument_list|,
name|tokens
argument_list|)
expr_stmt|;
break|break;
case|case
name|TASK_QUERY
case|:
name|app
operator|.
name|runTaskClass
argument_list|(
name|TASK_QUERY_CLASS
argument_list|,
name|tokens
argument_list|)
expr_stmt|;
break|break;
case|case
name|TASK_DEFAULT
case|:
name|app
operator|.
name|runTaskClass
argument_list|(
name|TASK_DEFAULT_CLASS
argument_list|,
name|tokens
argument_list|)
expr_stmt|;
break|break;
default|default:
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"Encountered unknown task type: "
operator|+
name|app
operator|.
name|getTaskType
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
catch|catch
parameter_list|(
name|Throwable
name|e
parameter_list|)
block|{
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"Failed to execute main task. Reason: "
operator|+
name|e
argument_list|)
expr_stmt|;
block|}
block|}
specifier|public
name|int
name|parseTask
parameter_list|(
name|List
name|tokens
parameter_list|)
block|{
if|if
condition|(
name|tokens
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
comment|// If no task, run the default task
return|return
name|TASK_DEFAULT
return|;
block|}
comment|// Process task token
name|String
name|taskToken
init|=
operator|(
name|String
operator|)
name|tokens
operator|.
name|remove
argument_list|(
literal|0
argument_list|)
decl_stmt|;
if|if
condition|(
name|taskToken
operator|.
name|equals
argument_list|(
literal|"start"
argument_list|)
condition|)
block|{
return|return
name|TASK_START
return|;
block|}
elseif|else
if|if
condition|(
name|taskToken
operator|.
name|equals
argument_list|(
literal|"stop"
argument_list|)
condition|)
block|{
return|return
name|TASK_STOP
return|;
block|}
elseif|else
if|if
condition|(
name|taskToken
operator|.
name|equals
argument_list|(
literal|"list"
argument_list|)
condition|)
block|{
return|return
name|TASK_LIST
return|;
block|}
elseif|else
if|if
condition|(
name|taskToken
operator|.
name|equals
argument_list|(
literal|"query"
argument_list|)
condition|)
block|{
return|return
name|TASK_QUERY
return|;
block|}
else|else
block|{
comment|// If not valid task, push back to list
name|tokens
operator|.
name|add
argument_list|(
literal|0
argument_list|,
name|taskToken
argument_list|)
expr_stmt|;
return|return
name|TASK_DEFAULT
return|;
block|}
block|}
specifier|public
name|void
name|parseExtensions
parameter_list|(
name|List
name|tokens
parameter_list|)
block|{
if|if
condition|(
name|tokens
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
return|return;
block|}
name|String
name|token
init|=
operator|(
name|String
operator|)
name|tokens
operator|.
name|get
argument_list|(
literal|0
argument_list|)
decl_stmt|;
comment|// If token is an extension dir option
if|if
condition|(
name|token
operator|.
name|equals
argument_list|(
literal|"--extdir"
argument_list|)
condition|)
block|{
comment|// Process token
name|tokens
operator|.
name|remove
argument_list|(
literal|0
argument_list|)
expr_stmt|;
comment|// If no extension directory is specified, or next token is another option
if|if
condition|(
name|tokens
operator|.
name|isEmpty
argument_list|()
operator|||
operator|(
operator|(
name|String
operator|)
name|tokens
operator|.
name|get
argument_list|(
literal|0
argument_list|)
operator|)
operator|.
name|startsWith
argument_list|(
literal|"-"
argument_list|)
condition|)
block|{
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"Extension directory not specified."
argument_list|)
expr_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"Ignoring extension directory option."
argument_list|)
expr_stmt|;
return|return;
block|}
comment|// Process extension dir token
name|File
name|extDir
init|=
operator|new
name|File
argument_list|(
operator|(
name|String
operator|)
name|tokens
operator|.
name|remove
argument_list|(
literal|0
argument_list|)
argument_list|)
decl_stmt|;
if|if
condition|(
operator|!
name|canUseExtdir
argument_list|()
condition|)
block|{
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"Extension directory feature not available due to the system classpath being able to load: "
operator|+
name|TASK_DEFAULT_CLASS
argument_list|)
expr_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"Ignoring extension directory option."
argument_list|)
expr_stmt|;
return|return;
block|}
if|if
condition|(
operator|!
name|extDir
operator|.
name|isDirectory
argument_list|()
condition|)
block|{
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"Extension directory specified is not valid directory: "
operator|+
name|extDir
argument_list|)
expr_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"Ignoring extension directory option."
argument_list|)
expr_stmt|;
return|return;
block|}
name|addExtensionDirectory
argument_list|(
name|extDir
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|token
operator|.
name|equals
argument_list|(
literal|"--noDefExt"
argument_list|)
condition|)
block|{
comment|// If token is --noDefExt option
name|useDefExt
operator|=
literal|false
expr_stmt|;
block|}
block|}
specifier|public
name|void
name|runTaskClass
parameter_list|(
name|String
name|taskClass
parameter_list|,
name|List
name|tokens
parameter_list|)
throws|throws
name|Throwable
block|{
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"ACTIVEMQ_HOME: "
operator|+
name|getActiveMQHome
argument_list|()
argument_list|)
expr_stmt|;
name|ClassLoader
name|cl
init|=
name|getClassLoader
argument_list|()
decl_stmt|;
comment|// Use reflection to run the task.
try|try
block|{
name|Class
name|task
init|=
name|cl
operator|.
name|loadClass
argument_list|(
name|taskClass
argument_list|)
decl_stmt|;
name|Method
name|runTask
init|=
name|task
operator|.
name|getMethod
argument_list|(
literal|"runTask"
argument_list|,
operator|new
name|Class
index|[]
block|{
name|List
operator|.
name|class
block|}
argument_list|)
decl_stmt|;
name|runTask
operator|.
name|invoke
argument_list|(
name|task
operator|.
name|newInstance
argument_list|()
argument_list|,
operator|new
name|Object
index|[]
block|{
name|tokens
block|}
argument_list|)
expr_stmt|;
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
name|getCause
argument_list|()
throw|;
block|}
catch|catch
parameter_list|(
name|Throwable
name|e
parameter_list|)
block|{
throw|throw
name|e
throw|;
block|}
block|}
specifier|public
name|void
name|addExtensionDirectory
parameter_list|(
name|File
name|directory
parameter_list|)
block|{
name|extensions
operator|.
name|add
argument_list|(
name|directory
argument_list|)
expr_stmt|;
block|}
comment|/**      * The extension directory feature will not work if the broker factory is already in the classpath      * since we have to load him from a child ClassLoader we build for it to work correctly.      *      * @return      */
specifier|public
name|boolean
name|canUseExtdir
parameter_list|()
block|{
try|try
block|{
name|Main
operator|.
name|class
operator|.
name|getClassLoader
argument_list|()
operator|.
name|loadClass
argument_list|(
name|TASK_DEFAULT_CLASS
argument_list|)
expr_stmt|;
return|return
literal|false
return|;
block|}
catch|catch
parameter_list|(
name|ClassNotFoundException
name|e
parameter_list|)
block|{
return|return
literal|true
return|;
block|}
block|}
specifier|public
name|ClassLoader
name|getClassLoader
parameter_list|()
throws|throws
name|MalformedURLException
block|{
if|if
condition|(
name|classLoader
operator|==
literal|null
condition|)
block|{
comment|// Setup the ClassLoader
name|classLoader
operator|=
name|Main
operator|.
name|class
operator|.
name|getClassLoader
argument_list|()
expr_stmt|;
if|if
condition|(
operator|!
name|extensions
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
name|ArrayList
name|urls
init|=
operator|new
name|ArrayList
argument_list|()
decl_stmt|;
for|for
control|(
name|Iterator
name|iter
init|=
name|extensions
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
name|File
name|dir
init|=
operator|(
name|File
operator|)
name|iter
operator|.
name|next
argument_list|()
decl_stmt|;
name|urls
operator|.
name|add
argument_list|(
name|dir
operator|.
name|toURL
argument_list|()
argument_list|)
expr_stmt|;
name|File
index|[]
name|files
init|=
name|dir
operator|.
name|listFiles
argument_list|()
decl_stmt|;
if|if
condition|(
name|files
operator|!=
literal|null
condition|)
block|{
for|for
control|(
name|int
name|j
init|=
literal|0
init|;
name|j
operator|<
name|files
operator|.
name|length
condition|;
name|j
operator|++
control|)
block|{
if|if
condition|(
name|files
index|[
name|j
index|]
operator|.
name|getName
argument_list|()
operator|.
name|endsWith
argument_list|(
literal|".zip"
argument_list|)
operator|||
name|files
index|[
name|j
index|]
operator|.
name|getName
argument_list|()
operator|.
name|endsWith
argument_list|(
literal|".jar"
argument_list|)
condition|)
block|{
name|urls
operator|.
name|add
argument_list|(
name|files
index|[
name|j
index|]
operator|.
name|toURL
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|}
name|URL
name|u
index|[]
init|=
operator|new
name|URL
index|[
name|urls
operator|.
name|size
argument_list|()
index|]
decl_stmt|;
name|urls
operator|.
name|toArray
argument_list|(
name|u
argument_list|)
expr_stmt|;
name|classLoader
operator|=
operator|new
name|URLClassLoader
argument_list|(
name|u
argument_list|,
name|classLoader
argument_list|)
expr_stmt|;
block|}
name|Thread
operator|.
name|currentThread
argument_list|()
operator|.
name|setContextClassLoader
argument_list|(
name|classLoader
argument_list|)
expr_stmt|;
block|}
return|return
name|classLoader
return|;
block|}
specifier|public
name|int
name|getTaskType
parameter_list|()
block|{
return|return
name|taskType
return|;
block|}
specifier|public
name|void
name|setTaskType
parameter_list|(
name|int
name|taskType
parameter_list|)
block|{
name|this
operator|.
name|taskType
operator|=
name|taskType
expr_stmt|;
block|}
specifier|public
name|void
name|setActiveMQHome
parameter_list|(
name|File
name|activeMQHome
parameter_list|)
block|{
name|this
operator|.
name|activeMQHome
operator|=
name|activeMQHome
expr_stmt|;
block|}
specifier|public
name|File
name|getActiveMQHome
parameter_list|()
block|{
if|if
condition|(
name|activeMQHome
operator|==
literal|null
condition|)
block|{
if|if
condition|(
name|System
operator|.
name|getProperty
argument_list|(
literal|"activemq.home"
argument_list|)
operator|!=
literal|null
condition|)
block|{
name|activeMQHome
operator|=
operator|new
name|File
argument_list|(
name|System
operator|.
name|getProperty
argument_list|(
literal|"activemq.home"
argument_list|)
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|activeMQHome
operator|==
literal|null
condition|)
block|{
comment|// guess from the location of the jar
name|URL
name|url
init|=
name|Main
operator|.
name|class
operator|.
name|getClassLoader
argument_list|()
operator|.
name|getResource
argument_list|(
literal|"org/apache/activemq/broker/Main.class"
argument_list|)
decl_stmt|;
if|if
condition|(
name|url
operator|!=
literal|null
condition|)
block|{
try|try
block|{
name|JarURLConnection
name|jarConnection
init|=
operator|(
name|JarURLConnection
operator|)
name|url
operator|.
name|openConnection
argument_list|()
decl_stmt|;
name|url
operator|=
name|jarConnection
operator|.
name|getJarFileURL
argument_list|()
expr_stmt|;
name|URI
name|baseURI
init|=
operator|new
name|URI
argument_list|(
name|url
operator|.
name|toString
argument_list|()
argument_list|)
operator|.
name|resolve
argument_list|(
literal|".."
argument_list|)
decl_stmt|;
name|activeMQHome
operator|=
operator|new
name|File
argument_list|(
name|baseURI
argument_list|)
operator|.
name|getCanonicalFile
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|ignored
parameter_list|)
block|{                     }
block|}
block|}
if|if
condition|(
name|activeMQHome
operator|==
literal|null
condition|)
block|{
name|activeMQHome
operator|=
operator|new
name|File
argument_list|(
literal|"."
argument_list|)
expr_stmt|;
block|}
block|}
return|return
name|activeMQHome
return|;
block|}
block|}
end_class

end_unit

