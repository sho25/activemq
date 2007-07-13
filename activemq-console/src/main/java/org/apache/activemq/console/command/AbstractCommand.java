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
name|ActiveMQConnectionMetaData
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
name|GlobalWriter
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

begin_class
specifier|public
specifier|abstract
class|class
name|AbstractCommand
implements|implements
name|Command
block|{
specifier|public
specifier|static
specifier|final
name|String
name|COMMAND_OPTION_DELIMETER
init|=
literal|","
decl_stmt|;
specifier|private
name|boolean
name|isPrintHelp
init|=
literal|false
decl_stmt|;
specifier|private
name|boolean
name|isPrintVersion
init|=
literal|false
decl_stmt|;
comment|/**      * Exceute a generic command, which includes parsing the options for the command and running the specific task.      * @param tokens - command arguments      * @throws Exception      */
specifier|public
name|void
name|execute
parameter_list|(
name|List
name|tokens
parameter_list|)
throws|throws
name|Exception
block|{
comment|// Parse the options specified by "-"
name|parseOptions
argument_list|(
name|tokens
argument_list|)
expr_stmt|;
comment|// Print the help file of the task
if|if
condition|(
name|isPrintHelp
condition|)
block|{
name|printHelp
argument_list|()
expr_stmt|;
comment|// Print the AMQ version
block|}
elseif|else
if|if
condition|(
name|isPrintVersion
condition|)
block|{
name|GlobalWriter
operator|.
name|printVersion
argument_list|(
name|ActiveMQConnectionMetaData
operator|.
name|PROVIDER_VERSION
argument_list|)
expr_stmt|;
comment|// Run the specified task
block|}
else|else
block|{
name|runTask
argument_list|(
name|tokens
argument_list|)
expr_stmt|;
block|}
block|}
comment|/**      * Parse any option parameters in the command arguments specified by a '-' as the first character of the token.      * @param tokens - command arguments      * @throws Exception      */
specifier|protected
name|void
name|parseOptions
parameter_list|(
name|List
name|tokens
parameter_list|)
throws|throws
name|Exception
block|{
while|while
condition|(
operator|!
name|tokens
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
name|String
name|token
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
name|token
operator|.
name|startsWith
argument_list|(
literal|"-"
argument_list|)
condition|)
block|{
comment|// Token is an option
name|handleOption
argument_list|(
name|token
argument_list|,
name|tokens
argument_list|)
expr_stmt|;
block|}
else|else
block|{
comment|// Push back to list of tokens
name|tokens
operator|.
name|add
argument_list|(
literal|0
argument_list|,
name|token
argument_list|)
expr_stmt|;
return|return;
block|}
block|}
block|}
comment|/**      * Handle the general options for each command, which includes -h, -?, --help, -D, --version.      * @param token - option token to handle      * @param tokens - succeeding command arguments      * @throws Exception      */
specifier|protected
name|void
name|handleOption
parameter_list|(
name|String
name|token
parameter_list|,
name|List
name|tokens
parameter_list|)
throws|throws
name|Exception
block|{
comment|// If token is a help option
if|if
condition|(
name|token
operator|.
name|equals
argument_list|(
literal|"-h"
argument_list|)
operator|||
name|token
operator|.
name|equals
argument_list|(
literal|"-?"
argument_list|)
operator|||
name|token
operator|.
name|equals
argument_list|(
literal|"--help"
argument_list|)
condition|)
block|{
name|isPrintHelp
operator|=
literal|true
expr_stmt|;
name|tokens
operator|.
name|clear
argument_list|()
expr_stmt|;
comment|// If token is a version option
block|}
elseif|else
if|if
condition|(
name|token
operator|.
name|equals
argument_list|(
literal|"--version"
argument_list|)
condition|)
block|{
name|isPrintVersion
operator|=
literal|true
expr_stmt|;
name|tokens
operator|.
name|clear
argument_list|()
expr_stmt|;
block|}
comment|// If token is a system property define option
elseif|else
if|if
condition|(
name|token
operator|.
name|startsWith
argument_list|(
literal|"-D"
argument_list|)
condition|)
block|{
name|String
name|key
init|=
name|token
operator|.
name|substring
argument_list|(
literal|2
argument_list|)
decl_stmt|;
name|String
name|value
init|=
literal|""
decl_stmt|;
name|int
name|pos
init|=
name|key
operator|.
name|indexOf
argument_list|(
literal|"="
argument_list|)
decl_stmt|;
if|if
condition|(
name|pos
operator|>=
literal|0
condition|)
block|{
name|value
operator|=
name|key
operator|.
name|substring
argument_list|(
name|pos
operator|+
literal|1
argument_list|)
expr_stmt|;
name|key
operator|=
name|key
operator|.
name|substring
argument_list|(
literal|0
argument_list|,
name|pos
argument_list|)
expr_stmt|;
block|}
name|System
operator|.
name|setProperty
argument_list|(
name|key
argument_list|,
name|value
argument_list|)
expr_stmt|;
block|}
comment|// Token is unrecognized
else|else
block|{
name|GlobalWriter
operator|.
name|printInfo
argument_list|(
literal|"Unrecognized option: "
operator|+
name|token
argument_list|)
expr_stmt|;
name|isPrintHelp
operator|=
literal|true
expr_stmt|;
block|}
block|}
comment|/**      * Run the specific task.      * @param tokens - command arguments      * @throws Exception      */
specifier|abstract
specifier|protected
name|void
name|runTask
parameter_list|(
name|List
name|tokens
parameter_list|)
throws|throws
name|Exception
function_decl|;
comment|/**      * Print the help messages for the specific task      */
specifier|abstract
specifier|protected
name|void
name|printHelp
parameter_list|()
function_decl|;
block|}
end_class

end_unit

