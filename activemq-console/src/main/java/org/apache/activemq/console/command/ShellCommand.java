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
operator|.
name|command
package|;
end_package

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|activemq
operator|.
name|console
operator|.
name|CommandContext
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
name|console
operator|.
name|formatter
operator|.
name|CommandShellOutputFormatter
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
name|util
operator|.
name|*
import|;
end_import

begin_class
specifier|public
class|class
name|ShellCommand
extends|extends
name|AbstractCommand
block|{
specifier|private
name|boolean
name|interactive
decl_stmt|;
specifier|private
name|String
index|[]
name|helpFile
decl_stmt|;
specifier|public
name|ShellCommand
parameter_list|()
block|{
name|this
argument_list|(
literal|false
argument_list|)
expr_stmt|;
block|}
specifier|public
name|ShellCommand
parameter_list|(
name|boolean
name|interactive
parameter_list|)
block|{
name|this
operator|.
name|interactive
operator|=
name|interactive
expr_stmt|;
name|ArrayList
argument_list|<
name|String
argument_list|>
name|help
init|=
operator|new
name|ArrayList
argument_list|<
name|String
argument_list|>
argument_list|()
decl_stmt|;
name|help
operator|.
name|addAll
argument_list|(
name|Arrays
operator|.
name|asList
argument_list|(
operator|new
name|String
index|[]
block|{
name|interactive
condition|?
literal|"Usage: [task] [task-options] [task data]"
else|:
literal|"Usage: Main [--extdir<dir>] [task] [task-options] [task data]"
block|,
literal|""
block|,
literal|"Tasks:"
block|}
argument_list|)
argument_list|)
expr_stmt|;
name|ArrayList
argument_list|<
name|Command
argument_list|>
name|commands
init|=
name|getCommands
argument_list|()
decl_stmt|;
name|Collections
operator|.
name|sort
argument_list|(
name|commands
argument_list|,
operator|new
name|Comparator
argument_list|<
name|Command
argument_list|>
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|int
name|compare
parameter_list|(
name|Command
name|command
parameter_list|,
name|Command
name|command1
parameter_list|)
block|{
return|return
name|command
operator|.
name|getName
argument_list|()
operator|.
name|compareTo
argument_list|(
name|command1
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
name|Command
name|command
range|:
name|commands
control|)
block|{
name|help
operator|.
name|add
argument_list|(
name|String
operator|.
name|format
argument_list|(
literal|"    %-24s - %s"
argument_list|,
name|command
operator|.
name|getName
argument_list|()
argument_list|,
name|command
operator|.
name|getOneLineDescription
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|help
operator|.
name|addAll
argument_list|(
name|Arrays
operator|.
name|asList
argument_list|(
operator|new
name|String
index|[]
block|{
literal|""
block|,
literal|"Task Options (Options specific to each task):"
block|,
literal|"    --extdir<dir>  - Add the jar files in the directory to the classpath."
block|,
literal|"    --version       - Display the version information."
block|,
literal|"    -h,-?,--help    - Display this help information. To display task specific help, use "
operator|+
operator|(
name|interactive
condition|?
literal|""
else|:
literal|"Main "
operator|)
operator|+
literal|"[task] -h,-?,--help"
block|,
literal|""
block|,
literal|"Task Data:"
block|,
literal|"    - Information needed by each specific task."
block|,
literal|""
block|,
literal|"JMX system property options:"
block|,
literal|"    -Dactivemq.jmx.url=<jmx service uri> (default is: 'service:jmx:rmi:///jndi/rmi://localhost:1099/jmxrmi')"
block|,
literal|"    -Dactivemq.jmx.user=<user name>"
block|,
literal|"    -Dactivemq.jmx.password=<password>"
block|,
literal|""
block|}
argument_list|)
argument_list|)
expr_stmt|;
name|this
operator|.
name|helpFile
operator|=
name|help
operator|.
name|toArray
argument_list|(
operator|new
name|String
index|[
name|help
operator|.
name|size
argument_list|()
index|]
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|String
name|getName
parameter_list|()
block|{
return|return
literal|"shell"
return|;
block|}
annotation|@
name|Override
specifier|public
name|String
name|getOneLineDescription
parameter_list|()
block|{
return|return
literal|"Runs the activemq sub shell"
return|;
block|}
comment|/**      * Main method to run a command shell client.      *       * @param args - command line arguments      * @param in - input stream to use      * @param out - output stream to use      * @return 0 for a successful run, -1 if there are any exception      */
specifier|public
specifier|static
name|int
name|main
parameter_list|(
name|String
index|[]
name|args
parameter_list|,
name|InputStream
name|in
parameter_list|,
name|PrintStream
name|out
parameter_list|)
block|{
name|CommandContext
name|context
init|=
operator|new
name|CommandContext
argument_list|()
decl_stmt|;
name|context
operator|.
name|setFormatter
argument_list|(
operator|new
name|CommandShellOutputFormatter
argument_list|(
name|out
argument_list|)
argument_list|)
expr_stmt|;
comment|// Convert arguments to list for easier management
name|List
argument_list|<
name|String
argument_list|>
name|tokens
init|=
operator|new
name|ArrayList
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
name|ShellCommand
name|main
init|=
operator|new
name|ShellCommand
argument_list|()
decl_stmt|;
try|try
block|{
name|main
operator|.
name|setCommandContext
argument_list|(
name|context
argument_list|)
expr_stmt|;
name|main
operator|.
name|execute
argument_list|(
name|tokens
argument_list|)
expr_stmt|;
return|return
literal|0
return|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
name|context
operator|.
name|printException
argument_list|(
name|e
argument_list|)
expr_stmt|;
return|return
literal|1
return|;
block|}
block|}
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
name|main
argument_list|(
name|args
argument_list|,
name|System
operator|.
name|in
argument_list|,
name|System
operator|.
name|out
argument_list|)
expr_stmt|;
block|}
specifier|public
name|boolean
name|isInteractive
parameter_list|()
block|{
return|return
name|interactive
return|;
block|}
specifier|public
name|void
name|setInteractive
parameter_list|(
name|boolean
name|interactive
parameter_list|)
block|{
name|this
operator|.
name|interactive
operator|=
name|interactive
expr_stmt|;
block|}
comment|/**      * Parses for specific command task.      *       * @param tokens - command arguments      * @throws Exception      */
specifier|protected
name|void
name|runTask
parameter_list|(
name|List
argument_list|<
name|String
argument_list|>
name|tokens
parameter_list|)
throws|throws
name|Exception
block|{
comment|// Process task token
if|if
condition|(
name|tokens
operator|.
name|size
argument_list|()
operator|>
literal|0
condition|)
block|{
name|Command
name|command
init|=
literal|null
decl_stmt|;
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
for|for
control|(
name|Command
name|c
range|:
name|getCommands
argument_list|()
control|)
block|{
if|if
condition|(
name|taskToken
operator|.
name|equals
argument_list|(
name|c
operator|.
name|getName
argument_list|()
argument_list|)
condition|)
block|{
name|command
operator|=
name|c
expr_stmt|;
break|break;
block|}
block|}
if|if
condition|(
name|command
operator|==
literal|null
condition|)
block|{
if|if
condition|(
name|taskToken
operator|.
name|equals
argument_list|(
literal|"help"
argument_list|)
condition|)
block|{
name|printHelp
argument_list|()
expr_stmt|;
block|}
else|else
block|{
name|printHelp
argument_list|()
expr_stmt|;
block|}
block|}
if|if
condition|(
name|command
operator|!=
literal|null
condition|)
block|{
name|command
operator|.
name|setCommandContext
argument_list|(
name|context
argument_list|)
expr_stmt|;
name|command
operator|.
name|execute
argument_list|(
name|tokens
argument_list|)
expr_stmt|;
block|}
block|}
else|else
block|{
name|printHelp
argument_list|()
expr_stmt|;
block|}
block|}
name|ArrayList
argument_list|<
name|Command
argument_list|>
name|getCommands
parameter_list|()
block|{
name|ServiceLoader
argument_list|<
name|Command
argument_list|>
name|loader
init|=
name|ServiceLoader
operator|.
name|load
argument_list|(
name|Command
operator|.
name|class
argument_list|)
decl_stmt|;
name|Iterator
argument_list|<
name|Command
argument_list|>
name|iterator
init|=
name|loader
operator|.
name|iterator
argument_list|()
decl_stmt|;
name|ArrayList
argument_list|<
name|Command
argument_list|>
name|rc
init|=
operator|new
name|ArrayList
argument_list|<
name|Command
argument_list|>
argument_list|()
decl_stmt|;
name|boolean
name|done
init|=
literal|false
decl_stmt|;
while|while
condition|(
operator|!
name|done
condition|)
block|{
try|try
block|{
if|if
condition|(
name|iterator
operator|.
name|hasNext
argument_list|()
condition|)
block|{
name|rc
operator|.
name|add
argument_list|(
name|iterator
operator|.
name|next
argument_list|()
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|done
operator|=
literal|true
expr_stmt|;
block|}
block|}
catch|catch
parameter_list|(
name|ServiceConfigurationError
name|e
parameter_list|)
block|{
comment|// it's ok, some commands may not load if their dependencies
comment|// are not available.
block|}
block|}
return|return
name|rc
return|;
block|}
comment|/**      * Print the help messages for the browse command      */
specifier|protected
name|void
name|printHelp
parameter_list|()
block|{
name|context
operator|.
name|printHelp
argument_list|(
name|helpFile
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

