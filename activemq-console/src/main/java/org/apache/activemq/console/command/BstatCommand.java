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

begin_class
specifier|public
class|class
name|BstatCommand
extends|extends
name|QueryCommand
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
literal|"Task Usage: activemq-admin bstat [bstat-options] [broker-name]"
block|,
literal|"Description: Performs a predefined query that displays useful statistics regarding the specified broker."
block|,
literal|"             If no broker name is specified, it will try and select from all registered brokers."
block|,
literal|""
block|,
literal|"Bstat Options:"
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
literal|"    activemq-admin bstat localhost"
block|,
literal|"        - Display a summary of statistics for the broker 'localhost'"
block|}
decl_stmt|;
annotation|@
name|Override
specifier|public
name|String
name|getName
parameter_list|()
block|{
return|return
literal|"bstat"
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
literal|"Performs a predefined query that displays useful statistics regarding the specified broker"
return|;
block|}
comment|/**      * Performs a predefiend query option      * @param tokens - command arguments      * @throws Exception      */
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
name|List
argument_list|<
name|String
argument_list|>
name|queryTokens
init|=
operator|new
name|ArrayList
argument_list|<
name|String
argument_list|>
argument_list|()
decl_stmt|;
comment|// Find the first non-option token
name|String
name|brokerName
init|=
literal|"*"
decl_stmt|;
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
name|token
init|=
operator|(
name|String
operator|)
name|i
operator|.
name|next
argument_list|()
decl_stmt|;
if|if
condition|(
operator|!
name|token
operator|.
name|startsWith
argument_list|(
literal|"-"
argument_list|)
condition|)
block|{
name|brokerName
operator|=
name|token
expr_stmt|;
break|break;
block|}
else|else
block|{
comment|// Re-insert options
name|queryTokens
operator|.
name|add
argument_list|(
name|token
argument_list|)
expr_stmt|;
block|}
block|}
comment|// Build the predefined option
name|queryTokens
operator|.
name|add
argument_list|(
literal|"--objname"
argument_list|)
expr_stmt|;
name|queryTokens
operator|.
name|add
argument_list|(
literal|"Type=*,BrokerName="
operator|+
name|brokerName
argument_list|)
expr_stmt|;
name|queryTokens
operator|.
name|add
argument_list|(
literal|"-xQTopic=ActiveMQ.Advisory.*"
argument_list|)
expr_stmt|;
name|queryTokens
operator|.
name|add
argument_list|(
literal|"--vuew"
argument_list|)
expr_stmt|;
name|queryTokens
operator|.
name|add
argument_list|(
literal|"Type,BrokerName,Destination,ConnectorName,EnqueueCount,"
operator|+
literal|"DequeueCount,TotalEnqueueCount,TotalDequeueCount,Messages,"
operator|+
literal|"TotalMessages,ConsumerCount,TotalConsumerCount,DispatchQueueSize"
argument_list|)
expr_stmt|;
comment|// Call the query command
name|super
operator|.
name|runTask
argument_list|(
name|queryTokens
argument_list|)
expr_stmt|;
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

