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
name|command
operator|.
name|ActiveMQTopic
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

begin_class
specifier|public
class|class
name|AmqBrowseCommand
extends|extends
name|AbstractAmqCommand
block|{
specifier|public
specifier|static
specifier|final
name|String
name|QUEUE_PREFIX
init|=
literal|"queue:"
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|String
name|TOPIC_PREFIX
init|=
literal|"topic:"
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|String
name|VIEW_GROUP_HEADER
init|=
literal|"header:"
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|String
name|VIEW_GROUP_CUSTOM
init|=
literal|"custom:"
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|String
name|VIEW_GROUP_BODY
init|=
literal|"body:"
decl_stmt|;
specifier|protected
name|String
index|[]
name|helpFile
init|=
operator|new
name|String
index|[]
block|{
literal|"Task Usage: Main browse --amqurl<broker url> [browse-options]<destinations>"
block|,
literal|"Description: Display selected destination's messages."
block|,
literal|""
block|,
literal|"Browse Options:"
block|,
literal|"    --amqurl<url>                Set the broker URL to connect to."
block|,
literal|"    --msgsel<msgsel1,msglsel2>   Add to the search list messages matched by the query similar to"
block|,
literal|"                                  the messages selector format."
block|,
literal|"    -V<header|custom|body>        Predefined view that allows you to view the message header, custom"
block|,
literal|"                                  message header, or the message body."
block|,
literal|"    --view<attr1>,<attr2>,...    Select the specific attribute of the message to view."
block|,
literal|"    --version                     Display the version information."
block|,
literal|"    -h,-?,--help                  Display the browse broker help information."
block|,
literal|""
block|,
literal|"Examples:"
block|,
literal|"    Main browse --amqurl tcp://localhost:61616 FOO.BAR"
block|,
literal|"        - Print the message header, custom message header, and message body of all messages in the"
block|,
literal|"          queue FOO.BAR"
block|,
literal|""
block|,
literal|"    Main browse --amqurl tcp://localhost:61616 -Vheader,body queue:FOO.BAR"
block|,
literal|"        - Print only the message header and message body of all messages in the queue FOO.BAR"
block|,
literal|""
block|,
literal|"    Main browse --amqurl tcp://localhost:61616 -Vheader --view custom:MyField queue:FOO.BAR"
block|,
literal|"        - Print the message header and the custom field 'MyField' of all messages in the queue FOO.BAR"
block|,
literal|""
block|,
literal|"    Main browse --amqurl tcp://localhost:61616 --msgsel JMSMessageID='*:10',JMSPriority>5 FOO.BAR"
block|,
literal|"        - Print all the message fields that has a JMSMessageID in the header field that matches the"
block|,
literal|"          wildcard *:10, and has a JMSPriority field> 5 in the queue FOO.BAR"
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
specifier|private
specifier|final
name|Set
argument_list|<
name|String
argument_list|>
name|groupViews
init|=
operator|new
name|HashSet
argument_list|<
name|String
argument_list|>
argument_list|(
literal|10
argument_list|)
decl_stmt|;
specifier|private
specifier|final
name|Set
name|queryViews
init|=
operator|new
name|HashSet
argument_list|(
literal|10
argument_list|)
decl_stmt|;
comment|/**      * Execute the browse command, which allows you to browse the messages in a      * given JMS destination      *       * @param tokens - command arguments      * @throws Exception      */
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
try|try
block|{
comment|// If no destination specified
if|if
condition|(
name|tokens
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
name|GlobalWriter
operator|.
name|printException
argument_list|(
operator|new
name|IllegalArgumentException
argument_list|(
literal|"No JMS destination specified."
argument_list|)
argument_list|)
expr_stmt|;
return|return;
block|}
comment|// If no broker url specified
if|if
condition|(
name|getBrokerUrl
argument_list|()
operator|==
literal|null
condition|)
block|{
name|GlobalWriter
operator|.
name|printException
argument_list|(
operator|new
name|IllegalStateException
argument_list|(
literal|"No broker url specified. Use the --amqurl option to specify a broker url."
argument_list|)
argument_list|)
expr_stmt|;
return|return;
block|}
comment|// Display the messages for each destination
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
name|String
name|destName
init|=
operator|(
name|String
operator|)
name|i
operator|.
name|next
argument_list|()
decl_stmt|;
name|Destination
name|dest
decl_stmt|;
comment|// If destination has been explicitly specified as a queue
if|if
condition|(
name|destName
operator|.
name|startsWith
argument_list|(
name|QUEUE_PREFIX
argument_list|)
condition|)
block|{
name|dest
operator|=
operator|new
name|ActiveMQQueue
argument_list|(
name|destName
operator|.
name|substring
argument_list|(
name|QUEUE_PREFIX
operator|.
name|length
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
comment|// If destination has been explicitly specified as a topic
block|}
elseif|else
if|if
condition|(
name|destName
operator|.
name|startsWith
argument_list|(
name|TOPIC_PREFIX
argument_list|)
condition|)
block|{
name|dest
operator|=
operator|new
name|ActiveMQTopic
argument_list|(
name|destName
operator|.
name|substring
argument_list|(
name|TOPIC_PREFIX
operator|.
name|length
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
comment|// By default destination is assumed to be a queue
block|}
else|else
block|{
name|dest
operator|=
operator|new
name|ActiveMQQueue
argument_list|(
name|destName
argument_list|)
expr_stmt|;
block|}
comment|// Query for the messages to view
name|List
name|addMsgs
init|=
name|AmqMessagesUtil
operator|.
name|getMessages
argument_list|(
name|getBrokerUrl
argument_list|()
argument_list|,
name|dest
argument_list|,
name|queryAddObjects
argument_list|)
decl_stmt|;
comment|// Query for the messages to remove from view
if|if
condition|(
name|querySubObjects
operator|.
name|size
argument_list|()
operator|>
literal|0
condition|)
block|{
name|List
name|subMsgs
init|=
name|AmqMessagesUtil
operator|.
name|getMessages
argument_list|(
name|getBrokerUrl
argument_list|()
argument_list|,
name|dest
argument_list|,
name|querySubObjects
argument_list|)
decl_stmt|;
name|addMsgs
operator|.
name|removeAll
argument_list|(
name|subMsgs
argument_list|)
expr_stmt|;
block|}
comment|// Display the messages
name|GlobalWriter
operator|.
name|printMessage
argument_list|(
name|AmqMessagesUtil
operator|.
name|filterMessagesView
argument_list|(
name|addMsgs
argument_list|,
name|groupViews
argument_list|,
name|queryViews
argument_list|)
argument_list|)
expr_stmt|;
block|}
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
operator|new
name|RuntimeException
argument_list|(
literal|"Failed to execute browse task. Reason: "
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
comment|/**      * Handle the --msgsel, --xmsgsel, --view, -V options.      *       * @param token - option token to handle      * @param tokens - succeeding command arguments      * @throws Exception      */
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
name|GlobalWriter
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
name|GlobalWriter
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
elseif|else
if|if
condition|(
name|token
operator|.
name|startsWith
argument_list|(
literal|"--view"
argument_list|)
condition|)
block|{
comment|// If token is a view option
comment|// If no view specified, or next token is a new option
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
name|GlobalWriter
operator|.
name|printException
argument_list|(
operator|new
name|IllegalArgumentException
argument_list|(
literal|"Attributes to view not specified"
argument_list|)
argument_list|)
expr_stmt|;
return|return;
block|}
comment|// Add the attributes to view
name|StringTokenizer
name|viewTokens
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
name|viewTokens
operator|.
name|hasMoreTokens
argument_list|()
condition|)
block|{
name|String
name|viewToken
init|=
name|viewTokens
operator|.
name|nextToken
argument_list|()
decl_stmt|;
comment|// If view is explicitly specified to belong to the JMS header
if|if
condition|(
name|viewToken
operator|.
name|equals
argument_list|(
name|VIEW_GROUP_HEADER
argument_list|)
condition|)
block|{
name|queryViews
operator|.
name|add
argument_list|(
name|AmqMessagesUtil
operator|.
name|JMS_MESSAGE_HEADER_PREFIX
operator|+
name|viewToken
operator|.
name|substring
argument_list|(
name|VIEW_GROUP_HEADER
operator|.
name|length
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
comment|// If view is explicitly specified to belong to the JMS
comment|// custom header
block|}
elseif|else
if|if
condition|(
name|viewToken
operator|.
name|equals
argument_list|(
name|VIEW_GROUP_CUSTOM
argument_list|)
condition|)
block|{
name|queryViews
operator|.
name|add
argument_list|(
name|AmqMessagesUtil
operator|.
name|JMS_MESSAGE_CUSTOM_PREFIX
operator|+
name|viewToken
operator|.
name|substring
argument_list|(
name|VIEW_GROUP_CUSTOM
operator|.
name|length
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
comment|// If view is explicitly specified to belong to the JMS body
block|}
elseif|else
if|if
condition|(
name|viewToken
operator|.
name|equals
argument_list|(
name|VIEW_GROUP_BODY
argument_list|)
condition|)
block|{
name|queryViews
operator|.
name|add
argument_list|(
name|AmqMessagesUtil
operator|.
name|JMS_MESSAGE_BODY_PREFIX
operator|+
name|viewToken
operator|.
name|substring
argument_list|(
name|VIEW_GROUP_BODY
operator|.
name|length
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
comment|// If no view explicitly specified, let's check the view for
comment|// each group
block|}
else|else
block|{
name|queryViews
operator|.
name|add
argument_list|(
name|AmqMessagesUtil
operator|.
name|JMS_MESSAGE_HEADER_PREFIX
operator|+
name|viewToken
argument_list|)
expr_stmt|;
name|queryViews
operator|.
name|add
argument_list|(
name|AmqMessagesUtil
operator|.
name|JMS_MESSAGE_CUSTOM_PREFIX
operator|+
name|viewToken
argument_list|)
expr_stmt|;
name|queryViews
operator|.
name|add
argument_list|(
name|AmqMessagesUtil
operator|.
name|JMS_MESSAGE_BODY_PREFIX
operator|+
name|viewToken
argument_list|)
expr_stmt|;
block|}
block|}
block|}
elseif|else
if|if
condition|(
name|token
operator|.
name|startsWith
argument_list|(
literal|"-V"
argument_list|)
condition|)
block|{
comment|// If token is a predefined group view option
name|String
name|viewGroup
init|=
name|token
operator|.
name|substring
argument_list|(
literal|2
argument_list|)
decl_stmt|;
comment|// If option is a header group view
if|if
condition|(
name|viewGroup
operator|.
name|equals
argument_list|(
literal|"header"
argument_list|)
condition|)
block|{
name|groupViews
operator|.
name|add
argument_list|(
name|AmqMessagesUtil
operator|.
name|JMS_MESSAGE_HEADER_PREFIX
argument_list|)
expr_stmt|;
comment|// If option is a custom header group view
block|}
elseif|else
if|if
condition|(
name|viewGroup
operator|.
name|equals
argument_list|(
literal|"custom"
argument_list|)
condition|)
block|{
name|groupViews
operator|.
name|add
argument_list|(
name|AmqMessagesUtil
operator|.
name|JMS_MESSAGE_CUSTOM_PREFIX
argument_list|)
expr_stmt|;
comment|// If option is a body group view
block|}
elseif|else
if|if
condition|(
name|viewGroup
operator|.
name|equals
argument_list|(
literal|"body"
argument_list|)
condition|)
block|{
name|groupViews
operator|.
name|add
argument_list|(
name|AmqMessagesUtil
operator|.
name|JMS_MESSAGE_BODY_PREFIX
argument_list|)
expr_stmt|;
comment|// Unknown group view
block|}
else|else
block|{
name|GlobalWriter
operator|.
name|printInfo
argument_list|(
literal|"Unknown group view: "
operator|+
name|viewGroup
operator|+
literal|". Ignoring group view option."
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
name|GlobalWriter
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

