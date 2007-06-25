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
name|util
operator|.
name|JmxMBeansUtil
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
name|ObjectName
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
name|remote
operator|.
name|JMXServiceURL
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
name|Iterator
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Collection
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

begin_class
specifier|public
class|class
name|ShutdownCommand
extends|extends
name|AbstractJmxCommand
block|{
specifier|private
name|boolean
name|isStopAllBrokers
init|=
literal|false
decl_stmt|;
comment|/**      * Shuts down the specified broker or brokers      * @param brokerNames - names of brokers to shutdown      * @throws Exception      */
specifier|protected
name|void
name|runTask
parameter_list|(
name|List
name|brokerNames
parameter_list|)
throws|throws
name|Exception
block|{
try|try
block|{
name|Collection
name|mbeans
decl_stmt|;
comment|// Stop all brokers
if|if
condition|(
name|isStopAllBrokers
condition|)
block|{
name|mbeans
operator|=
name|JmxMBeansUtil
operator|.
name|getAllBrokers
argument_list|(
name|useJmxServiceUrl
argument_list|()
argument_list|)
expr_stmt|;
name|brokerNames
operator|.
name|clear
argument_list|()
expr_stmt|;
block|}
comment|// Stop the default broker
elseif|else
if|if
condition|(
name|brokerNames
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
name|mbeans
operator|=
name|JmxMBeansUtil
operator|.
name|getAllBrokers
argument_list|(
name|useJmxServiceUrl
argument_list|()
argument_list|)
expr_stmt|;
comment|// If there is no broker to stop
if|if
condition|(
name|mbeans
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
name|GlobalWriter
operator|.
name|printInfo
argument_list|(
literal|"There are no brokers to stop."
argument_list|)
expr_stmt|;
return|return;
comment|// There should only be one broker to stop
block|}
elseif|else
if|if
condition|(
name|mbeans
operator|.
name|size
argument_list|()
operator|>
literal|1
condition|)
block|{
name|GlobalWriter
operator|.
name|printInfo
argument_list|(
literal|"There are multiple brokers to stop. Please select the broker(s) to stop or use --all to stop all brokers."
argument_list|)
expr_stmt|;
return|return;
comment|// Get the first broker only
block|}
else|else
block|{
name|Object
name|firstBroker
init|=
name|mbeans
operator|.
name|iterator
argument_list|()
operator|.
name|next
argument_list|()
decl_stmt|;
name|mbeans
operator|.
name|clear
argument_list|()
expr_stmt|;
name|mbeans
operator|.
name|add
argument_list|(
name|firstBroker
argument_list|)
expr_stmt|;
block|}
block|}
comment|// Stop each specified broker
else|else
block|{
name|String
name|brokerName
decl_stmt|;
name|mbeans
operator|=
operator|new
name|HashSet
argument_list|()
expr_stmt|;
while|while
condition|(
operator|!
name|brokerNames
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
name|brokerName
operator|=
operator|(
name|String
operator|)
name|brokerNames
operator|.
name|remove
argument_list|(
literal|0
argument_list|)
expr_stmt|;
name|Collection
name|matchedBrokers
init|=
name|JmxMBeansUtil
operator|.
name|getBrokersByName
argument_list|(
name|useJmxServiceUrl
argument_list|()
argument_list|,
name|brokerName
argument_list|)
decl_stmt|;
if|if
condition|(
name|matchedBrokers
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
name|GlobalWriter
operator|.
name|printInfo
argument_list|(
name|brokerName
operator|+
literal|" did not match any running brokers."
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|mbeans
operator|.
name|addAll
argument_list|(
name|matchedBrokers
argument_list|)
expr_stmt|;
block|}
block|}
block|}
comment|// Stop all brokers in set
name|stopBrokers
argument_list|(
name|useJmxServiceUrl
argument_list|()
argument_list|,
name|mbeans
argument_list|)
expr_stmt|;
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
literal|"Failed to execute stop task. Reason: "
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
comment|/**      * Stops the list of brokers.      * @param jmxServiceUrl - JMX service url to connect to      * @param brokerBeans - broker mbeans to stop      * @throws Exception      */
specifier|protected
name|void
name|stopBrokers
parameter_list|(
name|JMXServiceURL
name|jmxServiceUrl
parameter_list|,
name|Collection
name|brokerBeans
parameter_list|)
throws|throws
name|Exception
block|{
name|MBeanServerConnection
name|server
init|=
name|createJmxConnector
argument_list|()
operator|.
name|getMBeanServerConnection
argument_list|()
decl_stmt|;
name|ObjectName
name|brokerObjName
decl_stmt|;
for|for
control|(
name|Iterator
name|i
init|=
name|brokerBeans
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
name|brokerObjName
operator|=
operator|(
operator|(
name|ObjectInstance
operator|)
name|i
operator|.
name|next
argument_list|()
operator|)
operator|.
name|getObjectName
argument_list|()
expr_stmt|;
name|String
name|brokerName
init|=
name|brokerObjName
operator|.
name|getKeyProperty
argument_list|(
literal|"BrokerName"
argument_list|)
decl_stmt|;
name|GlobalWriter
operator|.
name|print
argument_list|(
literal|"Stopping broker: "
operator|+
name|brokerName
argument_list|)
expr_stmt|;
try|try
block|{
name|server
operator|.
name|invoke
argument_list|(
name|brokerObjName
argument_list|,
literal|"terminateJVM"
argument_list|,
operator|new
name|Object
index|[]
block|{
name|Integer
operator|.
name|valueOf
argument_list|(
literal|0
argument_list|)
block|}
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"int"
block|}
argument_list|)
expr_stmt|;
name|GlobalWriter
operator|.
name|print
argument_list|(
literal|"Succesfully stopped broker: "
operator|+
name|brokerName
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
comment|// TODO: Check exceptions throwned
comment|//System.out.println("Failed to stop broker: [ " + brokerName + " ]. Reason: " + e.getMessage());
block|}
block|}
name|closeJmxConnector
argument_list|()
expr_stmt|;
block|}
comment|/**      * Handle the --all option.      * @param token - option token to handle      * @param tokens - succeeding command arguments      * @throws Exception      */
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
comment|// Try to handle the options first
if|if
condition|(
name|token
operator|.
name|equals
argument_list|(
literal|"--all"
argument_list|)
condition|)
block|{
name|isStopAllBrokers
operator|=
literal|true
expr_stmt|;
block|}
else|else
block|{
comment|// Let the super class handle the option
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
specifier|protected
name|String
index|[]
name|helpFile
init|=
operator|new
name|String
index|[]
block|{
literal|"Task Usage: Main stop [stop-options] [broker-name1] [broker-name2] ..."
block|,
literal|"Description: Stops a running broker."
block|,
literal|""
block|,
literal|"Stop Options:"
block|,
literal|"    --jmxurl<url>      Set the JMX URL to connect to."
block|,
literal|"    --all               Stop all brokers."
block|,
literal|"    --version           Display the version information."
block|,
literal|"    -h,-?,--help        Display the stop broker help information."
block|,
literal|""
block|,
literal|"Broker Names:"
block|,
literal|"    Name of the brokers that will be stopped."
block|,
literal|"    If omitted, it is assumed that there is only one broker running, and it will be stopped."
block|,
literal|"    Use -all to stop all running brokers."
block|,
literal|""
block|}
decl_stmt|;
block|}
end_class

end_unit

