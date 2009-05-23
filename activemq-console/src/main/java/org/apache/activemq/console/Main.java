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
name|console
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
name|PrintStream
import|;
end_import

begin_import
import|import
name|java
operator|.
name|lang
operator|.
name|management
operator|.
name|ManagementFactory
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
name|Arrays
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Comparator
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|HashSet
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
name|LinkedList
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
name|Set
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

begin_comment
comment|/**  * Main class that can bootstrap an ActiveMQ broker console. Handles command  * line argument parsing to set up and run broker tasks.  *   * @version $Revision$  */
end_comment

begin_class
specifier|public
class|class
name|Main
block|{
specifier|public
specifier|static
specifier|final
name|String
name|TASK_DEFAULT_CLASS
init|=
literal|"org.apache.activemq.console.command.ShellCommand"
decl_stmt|;
specifier|private
specifier|static
name|boolean
name|useDefExt
init|=
literal|true
decl_stmt|;
specifier|private
name|File
name|activeMQHome
decl_stmt|;
specifier|private
name|File
name|activeMQBase
decl_stmt|;
specifier|private
name|ClassLoader
name|classLoader
decl_stmt|;
specifier|private
name|Set
argument_list|<
name|File
argument_list|>
name|extensions
init|=
operator|new
name|HashSet
argument_list|<
name|File
argument_list|>
argument_list|(
literal|5
argument_list|)
decl_stmt|;
specifier|private
name|Set
argument_list|<
name|File
argument_list|>
name|activeMQClassPath
init|=
operator|new
name|HashSet
argument_list|<
name|File
argument_list|>
argument_list|(
literal|5
argument_list|)
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
argument_list|<
name|String
argument_list|>
name|tokens
init|=
operator|new
name|LinkedList
argument_list|<
name|String
argument_list|>
argument_list|(
name|Arrays
operator|.
name|asList
argument_list|(
name|args
argument_list|)
argument_list|)
decl_stmt|;
comment|// Parse for extension directory option
name|app
operator|.
name|parseExtensions
argument_list|(
name|tokens
argument_list|)
expr_stmt|;
comment|// lets add the conf directory first, to find the log4j.properties just in case its not
comment|// in the activemq.classpath system property or some jar incorrectly includes one
name|File
name|confDir
init|=
operator|new
name|File
argument_list|(
name|app
operator|.
name|getActiveMQBase
argument_list|()
argument_list|,
literal|"conf"
argument_list|)
decl_stmt|;
name|app
operator|.
name|addClassPath
argument_list|(
name|confDir
argument_list|)
expr_stmt|;
comment|// Add the following to the classpath:
comment|//
comment|// ${activemq.base}/conf
comment|// ${activemq.base}/lib/* (only if activemq.base != activemq.home)
comment|// ${activemq.home}/lib/*
comment|// ${activemq.base}/lib/optional/* (only if activemq.base !=
comment|// activemq.home)
comment|// ${activemq.home}/lib/optional/*
comment|// ${activemq.base}/lib/web/* (only if activemq.base != activemq.home)
comment|// ${activemq.home}/lib/web/*
comment|//
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
name|boolean
name|baseIsHome
init|=
name|app
operator|.
name|getActiveMQBase
argument_list|()
operator|.
name|equals
argument_list|(
name|app
operator|.
name|getActiveMQHome
argument_list|()
argument_list|)
decl_stmt|;
name|File
name|baseLibDir
init|=
operator|new
name|File
argument_list|(
name|app
operator|.
name|getActiveMQBase
argument_list|()
argument_list|,
literal|"lib"
argument_list|)
decl_stmt|;
name|File
name|homeLibDir
init|=
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
decl_stmt|;
if|if
condition|(
operator|!
name|baseIsHome
condition|)
block|{
name|app
operator|.
name|addExtensionDirectory
argument_list|(
name|baseLibDir
argument_list|)
expr_stmt|;
block|}
name|app
operator|.
name|addExtensionDirectory
argument_list|(
name|homeLibDir
argument_list|)
expr_stmt|;
if|if
condition|(
operator|!
name|baseIsHome
condition|)
block|{
name|app
operator|.
name|addExtensionDirectory
argument_list|(
operator|new
name|File
argument_list|(
name|baseLibDir
argument_list|,
literal|"optional"
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
name|baseLibDir
argument_list|,
literal|"web"
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|app
operator|.
name|addExtensionDirectory
argument_list|(
operator|new
name|File
argument_list|(
name|homeLibDir
argument_list|,
literal|"optional"
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
name|homeLibDir
argument_list|,
literal|"web"
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|// Add any custom classpath specified from the system property
comment|// activemq.classpath
name|app
operator|.
name|addClassPathList
argument_list|(
name|System
operator|.
name|getProperty
argument_list|(
literal|"activemq.classpath"
argument_list|)
argument_list|)
expr_stmt|;
try|try
block|{
name|app
operator|.
name|runTaskClass
argument_list|(
name|tokens
argument_list|)
expr_stmt|;
name|System
operator|.
name|exit
argument_list|(
literal|0
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|ClassNotFoundException
name|e
parameter_list|)
block|{
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"Could not load class: "
operator|+
name|e
operator|.
name|getMessage
argument_list|()
argument_list|)
expr_stmt|;
try|try
block|{
name|ClassLoader
name|cl
init|=
name|app
operator|.
name|getClassLoader
argument_list|()
decl_stmt|;
if|if
condition|(
name|cl
operator|!=
literal|null
condition|)
block|{
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"Class loader setup: "
argument_list|)
expr_stmt|;
name|printClassLoaderTree
argument_list|(
name|cl
argument_list|)
expr_stmt|;
block|}
block|}
catch|catch
parameter_list|(
name|MalformedURLException
name|e1
parameter_list|)
block|{             }
name|System
operator|.
name|exit
argument_list|(
literal|1
argument_list|)
expr_stmt|;
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
name|System
operator|.
name|exit
argument_list|(
literal|1
argument_list|)
expr_stmt|;
block|}
block|}
comment|/**      * Print out what's in the classloader tree being used.      *       * @param cl      * @return depth      */
specifier|private
specifier|static
name|int
name|printClassLoaderTree
parameter_list|(
name|ClassLoader
name|cl
parameter_list|)
block|{
name|int
name|depth
init|=
literal|0
decl_stmt|;
if|if
condition|(
name|cl
operator|.
name|getParent
argument_list|()
operator|!=
literal|null
condition|)
block|{
name|depth
operator|=
name|printClassLoaderTree
argument_list|(
name|cl
operator|.
name|getParent
argument_list|()
argument_list|)
operator|+
literal|1
expr_stmt|;
block|}
name|StringBuffer
name|indent
init|=
operator|new
name|StringBuffer
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
name|depth
condition|;
name|i
operator|++
control|)
block|{
name|indent
operator|.
name|append
argument_list|(
literal|"  "
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|cl
operator|instanceof
name|URLClassLoader
condition|)
block|{
name|URLClassLoader
name|ucl
init|=
operator|(
name|URLClassLoader
operator|)
name|cl
decl_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
name|indent
operator|+
name|cl
operator|.
name|getClass
argument_list|()
operator|.
name|getName
argument_list|()
operator|+
literal|" {"
argument_list|)
expr_stmt|;
name|URL
index|[]
name|urls
init|=
name|ucl
operator|.
name|getURLs
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
name|urls
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
name|indent
operator|+
literal|"  "
operator|+
name|urls
index|[
name|i
index|]
argument_list|)
expr_stmt|;
block|}
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
name|indent
operator|+
literal|"}"
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
name|indent
operator|+
name|cl
operator|.
name|getClass
argument_list|()
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
block|}
return|return
name|depth
return|;
block|}
specifier|public
name|void
name|parseExtensions
parameter_list|(
name|List
argument_list|<
name|String
argument_list|>
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
name|int
name|count
init|=
name|tokens
operator|.
name|size
argument_list|()
decl_stmt|;
name|int
name|i
init|=
literal|0
decl_stmt|;
comment|// Parse for all --extdir and --noDefExt options
while|while
condition|(
name|i
operator|<
name|count
condition|)
block|{
name|String
name|token
init|=
name|tokens
operator|.
name|get
argument_list|(
name|i
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
name|count
operator|--
expr_stmt|;
name|tokens
operator|.
name|remove
argument_list|(
name|i
argument_list|)
expr_stmt|;
comment|// If no extension directory is specified, or next token is
comment|// another option
if|if
condition|(
name|i
operator|>=
name|count
operator|||
name|tokens
operator|.
name|get
argument_list|(
name|i
argument_list|)
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
continue|continue;
block|}
comment|// Process extension dir token
name|count
operator|--
expr_stmt|;
name|File
name|extDir
init|=
operator|new
name|File
argument_list|(
name|tokens
operator|.
name|remove
argument_list|(
name|i
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
continue|continue;
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
continue|continue;
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
comment|// If token is
comment|// --noDefExt option
name|count
operator|--
expr_stmt|;
name|tokens
operator|.
name|remove
argument_list|(
name|i
argument_list|)
expr_stmt|;
name|useDefExt
operator|=
literal|false
expr_stmt|;
block|}
else|else
block|{
name|i
operator|++
expr_stmt|;
block|}
block|}
block|}
specifier|public
name|void
name|runTaskClass
parameter_list|(
name|List
argument_list|<
name|String
argument_list|>
name|tokens
parameter_list|)
throws|throws
name|Throwable
block|{
name|StringBuilder
name|buffer
init|=
operator|new
name|StringBuilder
argument_list|()
decl_stmt|;
name|buffer
operator|.
name|append
argument_list|(
name|System
operator|.
name|getProperty
argument_list|(
literal|"java.vendor"
argument_list|)
argument_list|)
expr_stmt|;
name|buffer
operator|.
name|append
argument_list|(
literal|" "
argument_list|)
expr_stmt|;
name|buffer
operator|.
name|append
argument_list|(
name|System
operator|.
name|getProperty
argument_list|(
literal|"java.version"
argument_list|)
argument_list|)
expr_stmt|;
name|buffer
operator|.
name|append
argument_list|(
literal|" "
argument_list|)
expr_stmt|;
name|buffer
operator|.
name|append
argument_list|(
name|System
operator|.
name|getProperty
argument_list|(
literal|"java.home"
argument_list|)
argument_list|)
expr_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"Java Runtime: "
operator|+
name|buffer
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|buffer
operator|=
operator|new
name|StringBuilder
argument_list|()
expr_stmt|;
name|buffer
operator|.
name|append
argument_list|(
literal|"current="
argument_list|)
expr_stmt|;
name|buffer
operator|.
name|append
argument_list|(
name|Runtime
operator|.
name|getRuntime
argument_list|()
operator|.
name|totalMemory
argument_list|()
operator|/
literal|1024L
argument_list|)
expr_stmt|;
name|buffer
operator|.
name|append
argument_list|(
literal|"k  free="
argument_list|)
expr_stmt|;
name|buffer
operator|.
name|append
argument_list|(
name|Runtime
operator|.
name|getRuntime
argument_list|()
operator|.
name|freeMemory
argument_list|()
operator|/
literal|1024L
argument_list|)
expr_stmt|;
name|buffer
operator|.
name|append
argument_list|(
literal|"k  max="
argument_list|)
expr_stmt|;
name|buffer
operator|.
name|append
argument_list|(
name|Runtime
operator|.
name|getRuntime
argument_list|()
operator|.
name|maxMemory
argument_list|()
operator|/
literal|1024L
argument_list|)
expr_stmt|;
name|buffer
operator|.
name|append
argument_list|(
literal|"k"
argument_list|)
expr_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"  Heap sizes: "
operator|+
name|buffer
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|List
name|jvmArgs
init|=
name|ManagementFactory
operator|.
name|getRuntimeMXBean
argument_list|()
operator|.
name|getInputArguments
argument_list|()
decl_stmt|;
name|buffer
operator|=
operator|new
name|StringBuilder
argument_list|()
expr_stmt|;
for|for
control|(
name|Object
name|arg
range|:
name|jvmArgs
control|)
block|{
name|buffer
operator|.
name|append
argument_list|(
literal|" "
argument_list|)
operator|.
name|append
argument_list|(
name|arg
argument_list|)
expr_stmt|;
block|}
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"    JVM args:"
operator|+
name|buffer
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
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
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"ACTIVEMQ_BASE: "
operator|+
name|getActiveMQBase
argument_list|()
argument_list|)
expr_stmt|;
name|ClassLoader
name|cl
init|=
name|getClassLoader
argument_list|()
decl_stmt|;
name|Thread
operator|.
name|currentThread
argument_list|()
operator|.
name|setContextClassLoader
argument_list|(
name|cl
argument_list|)
expr_stmt|;
comment|// Use reflection to run the task.
try|try
block|{
name|String
index|[]
name|args
init|=
name|tokens
operator|.
name|toArray
argument_list|(
operator|new
name|String
index|[
name|tokens
operator|.
name|size
argument_list|()
index|]
argument_list|)
decl_stmt|;
name|Class
name|task
init|=
name|cl
operator|.
name|loadClass
argument_list|(
name|TASK_DEFAULT_CLASS
argument_list|)
decl_stmt|;
name|Method
name|runTask
init|=
name|task
operator|.
name|getMethod
argument_list|(
literal|"main"
argument_list|,
operator|new
name|Class
index|[]
block|{
name|String
index|[]
operator|.
expr|class
block|,
name|InputStream
operator|.
name|class
block|,
name|PrintStream
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
name|args
block|,
name|System
operator|.
name|in
block|,
name|System
operator|.
name|out
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
specifier|public
name|void
name|addClassPathList
parameter_list|(
name|String
name|fileList
parameter_list|)
block|{
if|if
condition|(
name|fileList
operator|!=
literal|null
operator|&&
name|fileList
operator|.
name|length
argument_list|()
operator|>
literal|0
condition|)
block|{
name|StringTokenizer
name|tokenizer
init|=
operator|new
name|StringTokenizer
argument_list|(
name|fileList
argument_list|,
literal|";"
argument_list|)
decl_stmt|;
while|while
condition|(
name|tokenizer
operator|.
name|hasMoreTokens
argument_list|()
condition|)
block|{
name|addClassPath
argument_list|(
operator|new
name|File
argument_list|(
name|tokenizer
operator|.
name|nextToken
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
block|}
specifier|public
name|void
name|addClassPath
parameter_list|(
name|File
name|classpath
parameter_list|)
block|{
name|activeMQClassPath
operator|.
name|add
argument_list|(
name|classpath
argument_list|)
expr_stmt|;
block|}
comment|/**      * The extension directory feature will not work if the broker factory is      * already in the classpath since we have to load him from a child      * ClassLoader we build for it to work correctly.      *       * @return true, if extension dir can be used. false otherwise.      */
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
operator|||
operator|!
name|activeMQClassPath
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
name|ArrayList
argument_list|<
name|URL
argument_list|>
name|urls
init|=
operator|new
name|ArrayList
argument_list|<
name|URL
argument_list|>
argument_list|()
decl_stmt|;
for|for
control|(
name|Iterator
argument_list|<
name|File
argument_list|>
name|iter
init|=
name|activeMQClassPath
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
name|iter
operator|.
name|next
argument_list|()
decl_stmt|;
comment|// try{ System.out.println("Adding to classpath: " +
comment|// dir.getCanonicalPath()); }catch(Exception e){}
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
block|}
for|for
control|(
name|Iterator
argument_list|<
name|File
argument_list|>
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
name|iter
operator|.
name|next
argument_list|()
decl_stmt|;
if|if
condition|(
name|dir
operator|.
name|isDirectory
argument_list|()
condition|)
block|{
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
comment|// Sort the jars so that classpath built is
comment|// consistently
comment|// in the same order. Also allows us to use jar
comment|// names to control
comment|// classpath order.
name|Arrays
operator|.
name|sort
argument_list|(
name|files
argument_list|,
operator|new
name|Comparator
argument_list|()
block|{
specifier|public
name|int
name|compare
parameter_list|(
name|Object
name|o1
parameter_list|,
name|Object
name|o2
parameter_list|)
block|{
name|File
name|f1
init|=
operator|(
name|File
operator|)
name|o1
decl_stmt|;
name|File
name|f2
init|=
operator|(
name|File
operator|)
name|o2
decl_stmt|;
return|return
name|f1
operator|.
name|getName
argument_list|()
operator|.
name|compareTo
argument_list|(
name|f2
operator|.
name|getName
argument_list|()
argument_list|)
return|;
block|}
block|}
argument_list|)
expr_stmt|;
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
comment|// try{ System.out.println("Adding to
comment|// classpath: " +
comment|// files[j].getCanonicalPath());
comment|// }catch(Exception e){}
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
literal|"org/apache/activemq/console/Main.class"
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
name|System
operator|.
name|setProperty
argument_list|(
literal|"activemq.home"
argument_list|,
name|activeMQHome
operator|.
name|getAbsolutePath
argument_list|()
argument_list|)
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
literal|"../."
argument_list|)
expr_stmt|;
name|System
operator|.
name|setProperty
argument_list|(
literal|"activemq.home"
argument_list|,
name|activeMQHome
operator|.
name|getAbsolutePath
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
return|return
name|activeMQHome
return|;
block|}
specifier|public
name|File
name|getActiveMQBase
parameter_list|()
block|{
if|if
condition|(
name|activeMQBase
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
literal|"activemq.base"
argument_list|)
operator|!=
literal|null
condition|)
block|{
name|activeMQBase
operator|=
operator|new
name|File
argument_list|(
name|System
operator|.
name|getProperty
argument_list|(
literal|"activemq.base"
argument_list|)
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|activeMQBase
operator|==
literal|null
condition|)
block|{
name|activeMQBase
operator|=
name|getActiveMQHome
argument_list|()
expr_stmt|;
name|System
operator|.
name|setProperty
argument_list|(
literal|"activemq.base"
argument_list|,
name|activeMQBase
operator|.
name|getAbsolutePath
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
return|return
name|activeMQBase
return|;
block|}
block|}
end_class

end_unit

