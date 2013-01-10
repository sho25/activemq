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
name|Enumeration
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
name|List
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Properties
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
name|QueryCommand
extends|extends
name|AbstractJmxCommand
block|{
comment|// Predefined type=identifier query
specifier|private
specifier|static
specifier|final
name|Properties
name|PREDEFINED_OBJNAME_QUERY
init|=
operator|new
name|Properties
argument_list|()
decl_stmt|;
static|static
block|{
name|PREDEFINED_OBJNAME_QUERY
operator|.
name|setProperty
argument_list|(
literal|"Broker"
argument_list|,
literal|"type=Broker,brokerName=%1"
argument_list|)
expr_stmt|;
name|PREDEFINED_OBJNAME_QUERY
operator|.
name|setProperty
argument_list|(
literal|"Connection"
argument_list|,
literal|"type=Broker,connector=clientConnectors,connectionName=%1,*"
argument_list|)
expr_stmt|;
name|PREDEFINED_OBJNAME_QUERY
operator|.
name|setProperty
argument_list|(
literal|"Connector"
argument_list|,
literal|"type=Broker,brokerName=*,connector=clientConnectors,connectorName=%1"
argument_list|)
expr_stmt|;
name|PREDEFINED_OBJNAME_QUERY
operator|.
name|setProperty
argument_list|(
literal|"NetworkConnector"
argument_list|,
literal|"type=Broker,brokerName=%1,connector=networkConnectors,networkConnectorName=*"
argument_list|)
expr_stmt|;
name|PREDEFINED_OBJNAME_QUERY
operator|.
name|setProperty
argument_list|(
literal|"Queue"
argument_list|,
literal|"type=Broker,brokerName=*,destinationType=Queue,destinationName=%1"
argument_list|)
expr_stmt|;
name|PREDEFINED_OBJNAME_QUERY
operator|.
name|setProperty
argument_list|(
literal|"Topic"
argument_list|,
literal|"type=Broker,brokerName=*,destinationType=Topic,destinationName=%1,*"
argument_list|)
expr_stmt|;
block|}
empty_stmt|;
specifier|protected
name|String
index|[]
name|helpFile
init|=
operator|new
name|String
index|[]
block|{
literal|"Task Usage: Main query [query-options]"
block|,
literal|"Description: Display selected broker component's attributes and statistics."
block|,
literal|""
block|,
literal|"Query Options:"
block|,
literal|"    -Q<type>=<name>               Add to the search list the specific object type matched"
block|,
literal|"                                  by the defined object identifier."
block|,
literal|"    -xQ<type>=<name>              Remove from the search list the specific object type"
block|,
literal|"                                  matched by the object identifier."
block|,
literal|"    --objname<query>             Add to the search list objects matched by the query similar"
block|,
literal|"                                  to the JMX object name format."
block|,
literal|"    --xobjname<query>            Remove from the search list objects matched by the query"
block|,
literal|"                                  similar to the JMX object name format."
block|,
literal|"    --view<attr1>,<attr2>,...    Select the specific attribute of the object to view."
block|,
literal|"                                  By default all attributes will be displayed."
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
literal|"    -h,-?,--help                  Display the query broker help information."
block|,
literal|""
block|,
literal|"Examples:"
block|,
literal|"    query"
block|,
literal|"        - Print all the attributes of all registered objects queues, topics, connections, etc)."
block|,
literal|""
block|,
literal|"    query -QQueue=TEST.FOO"
block|,
literal|"        - Print all the attributes of the queue with destination name TEST.FOO."
block|,
literal|""
block|,
literal|"    query -QTopic=*"
block|,
literal|"        - Print all the attributes of all registered topics."
block|,
literal|""
block|,
literal|"    query --view EnqueueCount,DequeueCount"
block|,
literal|"        - Print the attributes EnqueueCount and DequeueCount of all registered objects."
block|,
literal|""
block|,
literal|"    query -QTopic=* --view EnqueueCount,DequeueCount"
block|,
literal|"        - Print the attributes EnqueueCount and DequeueCount of all registered topics."
block|,
literal|""
block|,
literal|"    query -QTopic=* -QQueue=* --view EnqueueCount,DequeueCount"
block|,
literal|"        - Print the attributes EnqueueCount and DequeueCount of all registered topics and"
block|,
literal|"          queues."
block|,
literal|""
block|,
literal|"    query -QTopic=* -xQTopic=ActiveMQ.Advisory.*"
block|,
literal|"        - Print all attributes of all topics except those that has a name that begins"
block|,
literal|"          with \"ActiveMQ.Advisory\"."
block|,
literal|""
block|,
literal|"    query --objname Type=*Connect*,BrokerName=local* -xQNetworkConnector=*"
block|,
literal|"        - Print all attributes of all connectors, connections excluding network connectors"
block|,
literal|"          that belongs to the broker that begins with local."
block|,
literal|""
block|,
literal|"    query -QQueue=* -xQQueue=????"
block|,
literal|"        - Print all attributes of all queues except those that are 4 letters long."
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
name|queryViews
init|=
operator|new
name|HashSet
argument_list|(
literal|10
argument_list|)
decl_stmt|;
annotation|@
name|Override
specifier|public
name|String
name|getName
parameter_list|()
block|{
return|return
literal|"query"
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
literal|"Display selected broker component's attributes and statistics."
return|;
block|}
comment|/**      * Queries the mbeans registered in the specified JMX context      *       * @param tokens - command arguments      * @throws Exception      */
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
comment|// Query for the mbeans to add
name|List
name|addMBeans
init|=
name|JmxMBeansUtil
operator|.
name|queryMBeans
argument_list|(
name|createJmxConnection
argument_list|()
argument_list|,
name|queryAddObjects
argument_list|,
name|queryViews
argument_list|)
decl_stmt|;
comment|// Query for the mbeans to sub
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
name|subMBeans
init|=
name|JmxMBeansUtil
operator|.
name|queryMBeans
argument_list|(
name|createJmxConnection
argument_list|()
argument_list|,
name|querySubObjects
argument_list|,
name|queryViews
argument_list|)
decl_stmt|;
name|addMBeans
operator|.
name|removeAll
argument_list|(
name|subMBeans
argument_list|)
expr_stmt|;
block|}
name|context
operator|.
name|printMBean
argument_list|(
name|JmxMBeansUtil
operator|.
name|filterMBeansView
argument_list|(
name|addMBeans
argument_list|,
name|queryViews
argument_list|)
argument_list|)
expr_stmt|;
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
literal|"Failed to execute query task. Reason: "
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
comment|/**      * Handle the -Q, -xQ, --objname, --xobjname, --view options.      *       * @param token - option token to handle      * @param tokens - succeeding command arguments      * @throws Exception      */
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
comment|// If token is a additive predefined query define option
if|if
condition|(
name|token
operator|.
name|startsWith
argument_list|(
literal|"-Q"
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
comment|// If additive query
name|String
name|predefQuery
init|=
name|PREDEFINED_OBJNAME_QUERY
operator|.
name|getProperty
argument_list|(
name|key
argument_list|)
decl_stmt|;
if|if
condition|(
name|predefQuery
operator|==
literal|null
condition|)
block|{
name|context
operator|.
name|printException
argument_list|(
operator|new
name|IllegalArgumentException
argument_list|(
literal|"Unknown query object type: "
operator|+
name|key
argument_list|)
argument_list|)
expr_stmt|;
return|return;
block|}
name|String
name|queryStr
init|=
name|JmxMBeansUtil
operator|.
name|createQueryString
argument_list|(
name|predefQuery
argument_list|,
name|value
argument_list|)
decl_stmt|;
name|StringTokenizer
name|queryTokens
init|=
operator|new
name|StringTokenizer
argument_list|(
name|queryStr
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
literal|"-xQ"
argument_list|)
condition|)
block|{
comment|// If token is a substractive predefined query define option
name|String
name|key
init|=
name|token
operator|.
name|substring
argument_list|(
literal|3
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
comment|// If subtractive query
name|String
name|predefQuery
init|=
name|PREDEFINED_OBJNAME_QUERY
operator|.
name|getProperty
argument_list|(
name|key
argument_list|)
decl_stmt|;
if|if
condition|(
name|predefQuery
operator|==
literal|null
condition|)
block|{
name|context
operator|.
name|printException
argument_list|(
operator|new
name|IllegalArgumentException
argument_list|(
literal|"Unknown query object type: "
operator|+
name|key
argument_list|)
argument_list|)
expr_stmt|;
return|return;
block|}
name|String
name|queryStr
init|=
name|JmxMBeansUtil
operator|.
name|createQueryString
argument_list|(
name|predefQuery
argument_list|,
name|value
argument_list|)
decl_stmt|;
name|StringTokenizer
name|queryTokens
init|=
operator|new
name|StringTokenizer
argument_list|(
name|queryStr
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
literal|"--objname"
argument_list|)
condition|)
block|{
comment|// If token is an additive object name query option
comment|// If no object name query is specified, or next token is a new
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
literal|"Object name query not specified"
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
literal|"--xobjname"
argument_list|)
condition|)
block|{
comment|// If token is a substractive object name query option
comment|// If no object name query is specified, or next token is a new
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
literal|"Object name query not specified"
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
name|context
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
name|Enumeration
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
name|hasMoreElements
argument_list|()
condition|)
block|{
name|queryViews
operator|.
name|add
argument_list|(
name|viewTokens
operator|.
name|nextElement
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

