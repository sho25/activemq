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
name|net
operator|.
name|URI
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
name|jms
operator|.
name|Destination
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|jms
operator|.
name|Message
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
name|MBeanServerInvocationHandler
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
name|broker
operator|.
name|jmx
operator|.
name|QueueViewMBean
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
name|command
operator|.
name|ActiveMQQueue
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
name|AmqMessagesUtil
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
literal|"Purge Options:"
block|,
literal|"    --msgsel<msgsel1,msglsel2>   Add to the search list messages matched by the query similar to"
block|,
literal|"                                  the messages selector format."
block|,
literal|"    --jmxurl<url>                Set the JMX URL to connect to."
block|,
literal|"    --pid<pid>                   Set the pid to connect to (only on Sun JVM)."
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
literal|"    Main purge --msgsel \"JMSMessageID='*:10',JMSPriority>5\" FOO.*"
block|,
literal|"        - Delete all the messages in the destinations that matches FOO.* and has a JMSMessageID in"
block|,
literal|"          the header field that matches the wildcard *:10, and has a JMSPriority field> 5 in the"
block|,
literal|"          queue FOO.BAR."
block|,
literal|"          SLQ92 syntax is also supported."
block|,
literal|"        * To use wildcard queries, the field must be a string and the query enclosed in ''"
block|,
literal|"          Use double quotes \"\" around the entire message selector string."
block|,
literal|""
block|}
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
name|QueueViewMBean
name|proxy
init|=
operator|(
name|QueueViewMBean
operator|)
name|MBeanServerInvocationHandler
operator|.
name|newProxyInstance
argument_list|(
name|createJmxConnection
argument_list|()
argument_list|,
name|queueName
argument_list|,
name|QueueViewMBean
operator|.
name|class
argument_list|,
literal|true
argument_list|)
decl_stmt|;
name|int
name|removed
init|=
literal|0
decl_stmt|;
comment|// AMQ-3404: We support two syntaxes for the message
comment|// selector query:
comment|// 1) AMQ specific:
comment|//    "JMSPriority>2,MyHeader='Foo'"
comment|//
comment|// 2) SQL-92 syntax:
comment|//    "(JMSPriority>2) AND (MyHeader='Foo')"
comment|//
comment|// If syntax style 1) is used, the comma separated
comment|// criterias are broken into List<String> elements.
comment|// We then need to construct the SQL-92 query out of
comment|// this list.
name|String
name|sqlQuery
init|=
literal|null
decl_stmt|;
if|if
condition|(
name|queryAddObjects
operator|.
name|size
argument_list|()
operator|>
literal|1
condition|)
block|{
name|sqlQuery
operator|=
name|convertToSQL92
argument_list|(
name|queryAddObjects
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|sqlQuery
operator|=
name|queryAddObjects
operator|.
name|get
argument_list|(
literal|0
argument_list|)
expr_stmt|;
block|}
name|removed
operator|=
name|proxy
operator|.
name|removeMatchingMessages
argument_list|(
name|sqlQuery
argument_list|)
expr_stmt|;
name|context
operator|.
name|printInfo
argument_list|(
literal|"Removed: "
operator|+
name|removed
operator|+
literal|" messages for message selector "
operator|+
name|sqlQuery
operator|.
name|toString
argument_list|()
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
comment|/**      * Converts the message selector as provided on command line      * argument to activem-admin into an SQL-92 conform string.       * E.g.      *   "JMSMessageID='*:10',JMSPriority>5"      * gets converted into       *   "(JMSMessageID='%:10') AND (JMSPriority>5)"      *       * @param tokens - List of message selector query parameters       * @return SQL-92 string of that query.       */
specifier|public
name|String
name|convertToSQL92
parameter_list|(
name|List
argument_list|<
name|String
argument_list|>
name|tokens
parameter_list|)
block|{
name|String
name|selector
init|=
literal|""
decl_stmt|;
comment|// Convert to message selector
for|for
control|(
name|Iterator
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
name|selector
operator|=
name|selector
operator|+
literal|"("
operator|+
name|i
operator|.
name|next
argument_list|()
operator|.
name|toString
argument_list|()
operator|+
literal|") AND "
expr_stmt|;
block|}
comment|// Remove last AND and replace '*' with '%'
if|if
condition|(
operator|!
name|selector
operator|.
name|equals
argument_list|(
literal|""
argument_list|)
condition|)
block|{
name|selector
operator|=
name|selector
operator|.
name|substring
argument_list|(
literal|0
argument_list|,
name|selector
operator|.
name|length
argument_list|()
operator|-
literal|5
argument_list|)
expr_stmt|;
name|selector
operator|=
name|selector
operator|.
name|replace
argument_list|(
literal|'*'
argument_list|,
literal|'%'
argument_list|)
expr_stmt|;
block|}
return|return
name|selector
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

