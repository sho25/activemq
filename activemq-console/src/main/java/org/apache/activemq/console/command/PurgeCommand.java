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
name|Iterator
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
name|StringTokenizer
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|management
operator|.
name|MBeanServerConnection
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|management
operator|.
name|ObjectInstance
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|management
operator|.
name|ObjectName
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|management
operator|.
name|openmbean
operator|.
name|CompositeData
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|management
operator|.
name|remote
operator|.
name|JMXConnector
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
name|util
operator|.
name|JmxMBeansUtil
import|;
end_import

begin_class
specifier|public
class|class
name|PurgeCommand
extends|extends
name|AbstractJmxCommand
block|{
specifier|protected
name|String
index|[]
name|helpFile
init|=
operator|new
name|String
index|[]
block|{
literal|"Task Usage: Main purge [browse-options]<destinations>"
block|,
literal|"Description: Delete selected destination's messages that matches the message selector."
block|,
literal|""
block|,
literal|"Browse Options:"
block|,
literal|"    --msgsel<msgsel1,msglsel2>   Add to the search list messages matched by the query similar to"
block|,
literal|"                                  the messages selector format."
block|,
literal|"    --jmxurl<url>                Set the JMX URL to connect to."
block|,
literal|"    --jmxuser<user>              Set the JMX user used for authenticating."
block|,
literal|"    --jmxpassword<password>      Set the JMX password used for authenticating."
block|,
literal|"    --jmxlocal                    Use the local JMX server instead of a remote one."
block|,
literal|"    --version                     Display the version information."
block|,
literal|"    -h,-?,--help                  Display the browse broker help information."
block|,
literal|""
block|,
literal|"Examples:"
block|,
literal|"    Main purge FOO.BAR"
block|,
literal|"        - Delete all the messages in queue FOO.BAR"
block|,
literal|"    Main purge --msgsel JMSMessageID='*:10',JMSPriority>5 FOO.*"
block|,
literal|"        - Delete all the messages in the destinations that matches FOO.* and has a JMSMessageID in"
block|,
literal|"          the header field that matches the wildcard *:10, and has a JMSPriority field> 5 in the"
block|,
literal|"          queue FOO.BAR"
block|,
literal|"        * To use wildcard queries, the field must be a string and the query enclosed in ''"
block|,
literal|""
block|,     }
decl_stmt|;
specifier|private
specifier|final
name|List
argument_list|<
name|String
argument_list|>
name|queryAddObjects
init|=
operator|new
name|ArrayList
argument_list|<
name|String
argument_list|>
argument_list|(
literal|10
argument_list|)
decl_stmt|;
specifier|private
specifier|final
name|List
argument_list|<
name|String
argument_list|>
name|querySubObjects
init|=
operator|new
name|ArrayList
argument_list|<
name|String
argument_list|>
argument_list|(
literal|10
argument_list|)
decl_stmt|;
comment|/**      * Execute the purge command, which allows you to purge the messages in a      * given JMS destination      *       * @param tokens - command arguments      * @throws Exception      */
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
try|try
block|{
comment|// If there is no queue name specified, let's select all
if|if
condition|(
name|tokens
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
name|tokens
operator|.
name|add
argument_list|(
literal|"*"
argument_list|)
expr_stmt|;
block|}
comment|// Iterate through the queue names
for|for
control|(
name|Iterator
argument_list|<
name|String
argument_list|>
name|i
init|=
name|tokens
operator|.
name|iterator
argument_list|()
init|;
name|i
operator|.
name|hasNext
argument_list|()
condition|;
control|)
block|{
name|List
name|queueList
init|=
name|JmxMBeansUtil
operator|.
name|queryMBeans
argument_list|(
name|createJmxConnection
argument_list|()
argument_list|,
literal|"Type=Queue,Destination="
operator|+
name|i
operator|.
name|next
argument_list|()
operator|+
literal|",*"
argument_list|)
decl_stmt|;
for|for
control|(
name|Iterator
name|j
init|=
name|queueList
operator|.
name|iterator
argument_list|()
init|;
name|j
operator|.
name|hasNext
argument_list|()
condition|;
control|)
block|{
name|ObjectName
name|queueName
init|=
operator|(
operator|(
name|ObjectInstance
operator|)
name|j
operator|.
name|next
argument_list|()
operator|)
operator|.
name|getObjectName
argument_list|()
decl_stmt|;
if|if
condition|(
name|queryAddObjects
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
name|purgeQueue
argument_list|(
name|queueName
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|List
name|messages
init|=
name|JmxMBeansUtil
operator|.
name|createMessageQueryFilter
argument_list|(
name|createJmxConnection
argument_list|()
argument_list|,
name|queueName
argument_list|)
operator|.
name|query
argument_list|(
name|queryAddObjects
argument_list|)
decl_stmt|;
name|purgeMessages
argument_list|(
name|queueName
argument_list|,
name|messages
argument_list|)
expr_stmt|;
block|}
block|}
block|}
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
operator|new
name|RuntimeException
argument_list|(
literal|"Failed to execute purge task. Reason: "
operator|+
name|e
argument_list|)
argument_list|)
expr_stmt|;
throw|throw
operator|new
name|Exception
argument_list|(
name|e
argument_list|)
throw|;
block|}
block|}
comment|/**      * Purge all the messages in the queue      *       * @param queue - ObjectName of the queue to purge      * @throws Exception      */
specifier|public
name|void
name|purgeQueue
parameter_list|(
name|ObjectName
name|queue
parameter_list|)
throws|throws
name|Exception
block|{
name|context
operator|.
name|printInfo
argument_list|(
literal|"Purging all messages in queue: "
operator|+
name|queue
operator|.
name|getKeyProperty
argument_list|(
literal|"Destination"
argument_list|)
argument_list|)
expr_stmt|;
name|createJmxConnection
argument_list|()
operator|.
name|invoke
argument_list|(
name|queue
argument_list|,
literal|"purge"
argument_list|,
operator|new
name|Object
index|[]
block|{}
argument_list|,
operator|new
name|String
index|[]
block|{}
argument_list|)
expr_stmt|;
block|}
comment|/**      * Purge selected messages in the queue      *       * @param queue - ObjectName of the queue to purge the messages from      * @param messages - List of messages to purge      * @throws Exception      */
specifier|public
name|void
name|purgeMessages
parameter_list|(
name|ObjectName
name|queue
parameter_list|,
name|List
name|messages
parameter_list|)
throws|throws
name|Exception
block|{
name|Object
index|[]
name|param
init|=
operator|new
name|Object
index|[
literal|1
index|]
decl_stmt|;
for|for
control|(
name|Iterator
name|i
init|=
name|messages
operator|.
name|iterator
argument_list|()
init|;
name|i
operator|.
name|hasNext
argument_list|()
condition|;
control|)
block|{
name|CompositeData
name|msg
init|=
operator|(
name|CompositeData
operator|)
name|i
operator|.
name|next
argument_list|()
decl_stmt|;
name|param
index|[
literal|0
index|]
operator|=
literal|""
operator|+
name|msg
operator|.
name|get
argument_list|(
literal|"JMSMessageID"
argument_list|)
expr_stmt|;
name|context
operator|.
name|printInfo
argument_list|(
literal|"Removing message: "
operator|+
name|param
index|[
literal|0
index|]
operator|+
literal|" from queue: "
operator|+
name|queue
operator|.
name|getKeyProperty
argument_list|(
literal|"Destination"
argument_list|)
argument_list|)
expr_stmt|;
name|createJmxConnection
argument_list|()
operator|.
name|invoke
argument_list|(
name|queue
argument_list|,
literal|"removeMessage"
argument_list|,
name|param
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"java.lang.String"
block|}
argument_list|)
expr_stmt|;
block|}
block|}
comment|/**      * Handle the --msgsel, --xmsgsel.      *       * @param token - option token to handle      * @param tokens - succeeding command arguments      * @throws Exception      */
specifier|protected
name|void
name|handleOption
parameter_list|(
name|String
name|token
parameter_list|,
name|List
argument_list|<
name|String
argument_list|>
name|tokens
parameter_list|)
throws|throws
name|Exception
block|{
comment|// If token is an additive message selector option
if|if
condition|(
name|token
operator|.
name|startsWith
argument_list|(
literal|"--msgsel"
argument_list|)
condition|)
block|{
comment|// If no message selector is specified, or next token is a new
comment|// option
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
name|context
operator|.
name|printException
argument_list|(
operator|new
name|IllegalArgumentException
argument_list|(
literal|"Message selector not specified"
argument_list|)
argument_list|)
expr_stmt|;
return|return;
block|}
name|StringTokenizer
name|queryTokens
init|=
operator|new
name|StringTokenizer
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
argument_list|,
name|COMMAND_OPTION_DELIMETER
argument_list|)
decl_stmt|;
while|while
condition|(
name|queryTokens
operator|.
name|hasMoreTokens
argument_list|()
condition|)
block|{
name|queryAddObjects
operator|.
name|add
argument_list|(
name|queryTokens
operator|.
name|nextToken
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
elseif|else
if|if
condition|(
name|token
operator|.
name|startsWith
argument_list|(
literal|"--xmsgsel"
argument_list|)
condition|)
block|{
comment|// If token is a substractive message selector option
comment|// If no message selector is specified, or next token is a new
comment|// option
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
name|context
operator|.
name|printException
argument_list|(
operator|new
name|IllegalArgumentException
argument_list|(
literal|"Message selector not specified"
argument_list|)
argument_list|)
expr_stmt|;
return|return;
block|}
name|StringTokenizer
name|queryTokens
init|=
operator|new
name|StringTokenizer
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
argument_list|,
name|COMMAND_OPTION_DELIMETER
argument_list|)
decl_stmt|;
while|while
condition|(
name|queryTokens
operator|.
name|hasMoreTokens
argument_list|()
condition|)
block|{
name|querySubObjects
operator|.
name|add
argument_list|(
name|queryTokens
operator|.
name|nextToken
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
else|else
block|{
comment|// Let super class handle unknown option
name|super
operator|.
name|handleOption
argument_list|(
name|token
argument_list|,
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

