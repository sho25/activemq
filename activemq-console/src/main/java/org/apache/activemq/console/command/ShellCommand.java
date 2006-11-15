begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  *  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  * http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
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
name|formatter
operator|.
name|GlobalWriter
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
name|Arrays
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
block|}
comment|/**      * Main method to run a command shell client.      * @param args - command line arguments      * @param in - input stream to use      * @param out - output stream to use      * @return 0 for a successful run, -1 if there are any exception      */
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
name|GlobalWriter
operator|.
name|instantiate
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
name|tokens
init|=
operator|new
name|ArrayList
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
name|GlobalWriter
operator|.
name|printException
argument_list|(
name|e
argument_list|)
expr_stmt|;
return|return
operator|-
literal|1
return|;
block|}
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
comment|/**      * Parses for specific command task, default task is a start task.      * @param tokens - command arguments      * @throws Exception      */
specifier|protected
name|void
name|runTask
parameter_list|(
name|List
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
operator|new
name|StartCommand
argument_list|()
operator|.
name|execute
argument_list|(
name|tokens
argument_list|)
expr_stmt|;
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
operator|new
name|ShutdownCommand
argument_list|()
operator|.
name|execute
argument_list|(
name|tokens
argument_list|)
expr_stmt|;
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
operator|new
name|ListCommand
argument_list|()
operator|.
name|execute
argument_list|(
name|tokens
argument_list|)
expr_stmt|;
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
operator|new
name|QueryCommand
argument_list|()
operator|.
name|execute
argument_list|(
name|tokens
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|taskToken
operator|.
name|equals
argument_list|(
literal|"browse"
argument_list|)
condition|)
block|{
operator|new
name|AmqBrowseCommand
argument_list|()
operator|.
name|execute
argument_list|(
name|tokens
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|taskToken
operator|.
name|equals
argument_list|(
literal|"purge"
argument_list|)
condition|)
block|{
operator|new
name|PurgeCommand
argument_list|()
operator|.
name|execute
argument_list|(
name|tokens
argument_list|)
expr_stmt|;
block|}
elseif|else
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
operator|new
name|StartCommand
argument_list|()
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
operator|new
name|StartCommand
argument_list|()
operator|.
name|execute
argument_list|(
name|tokens
argument_list|)
expr_stmt|;
block|}
block|}
comment|/**      * Print the help messages for the browse command      */
specifier|protected
name|void
name|printHelp
parameter_list|()
block|{
name|GlobalWriter
operator|.
name|printHelp
argument_list|(
name|helpFile
argument_list|)
expr_stmt|;
block|}
specifier|protected
name|String
index|[]
name|helpFile
init|=
operator|new
name|String
index|[]
block|{
literal|"Usage: Main [--extdir<dir>] [task] [task-options] [task data]"
block|,
literal|""
block|,
literal|"Tasks (default task is start):"
block|,
literal|"    start           - Creates and starts a broker using a configuration file, or a broker URI."
block|,
literal|"    stop            - Stops a running broker specified by the broker name."
block|,
literal|"    list            - Lists all available brokers in the specified JMX context."
block|,
literal|"    query           - Display selected broker component's attributes and statistics."
block|,
literal|"    browse          - Display selected messages in a specified destination."
block|,
literal|""
block|,
literal|"Task Options (Options specific to each task):"
block|,
literal|"    --extdir<dir>  - Add the jar files in the directory to the classpath."
block|,
literal|"    --version       - Display the version information."
block|,
literal|"    -h,-?,--help    - Display this help information. To display task specific help, use Main [task] -h,-?,--help"
block|,
literal|""
block|,
literal|"Task Data:"
block|,
literal|"    - Information needed by each specific task."
block|,
literal|""
block|}
decl_stmt|;
block|}
end_class

end_unit

