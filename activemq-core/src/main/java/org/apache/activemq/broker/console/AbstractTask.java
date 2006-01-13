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
operator|.
name|console
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
name|AbstractTask
implements|implements
name|Task
block|{
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
specifier|public
name|void
name|runTask
parameter_list|(
name|List
name|tokens
parameter_list|)
throws|throws
name|Exception
block|{
name|parseOptions
argument_list|(
name|tokens
argument_list|)
expr_stmt|;
if|if
condition|(
name|isPrintHelp
condition|)
block|{
name|printHelp
argument_list|()
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|isPrintVersion
condition|)
block|{
name|printVersion
argument_list|()
expr_stmt|;
block|}
else|else
block|{
name|startTask
argument_list|(
name|tokens
argument_list|)
expr_stmt|;
block|}
block|}
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
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"Ignoring unrecognized option: "
operator|+
name|token
argument_list|)
expr_stmt|;
block|}
block|}
specifier|protected
name|void
name|printVersion
parameter_list|()
block|{
name|System
operator|.
name|out
operator|.
name|println
argument_list|()
expr_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"ActiveMQ "
operator|+
name|ActiveMQConnectionMetaData
operator|.
name|PROVIDER_VERSION
argument_list|)
expr_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"For help or more information please see: http://www.logicblaze.com"
argument_list|)
expr_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|()
expr_stmt|;
block|}
specifier|protected
name|void
name|printError
parameter_list|(
name|String
name|message
parameter_list|)
block|{
name|isPrintHelp
operator|=
literal|true
expr_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
name|message
argument_list|)
expr_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|()
expr_stmt|;
block|}
specifier|abstract
specifier|protected
name|void
name|startTask
parameter_list|(
name|List
name|tokens
parameter_list|)
throws|throws
name|Exception
function_decl|;
specifier|abstract
specifier|protected
name|void
name|printHelp
parameter_list|()
function_decl|;
block|}
end_class

end_unit

