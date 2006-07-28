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
name|broker
operator|.
name|BrokerFactory
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
name|BrokerService
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
name|net
operator|.
name|URI
import|;
end_import

begin_import
import|import
name|java
operator|.
name|net
operator|.
name|URISyntaxException
import|;
end_import

begin_class
specifier|public
class|class
name|StartCommand
extends|extends
name|AbstractCommand
block|{
specifier|public
specifier|static
specifier|final
name|String
name|DEFAULT_CONFIG_URI
init|=
literal|"xbean:activemq.xml"
decl_stmt|;
specifier|private
name|URI
name|configURI
decl_stmt|;
specifier|private
name|List
name|brokers
init|=
operator|new
name|ArrayList
argument_list|(
literal|5
argument_list|)
decl_stmt|;
comment|/**      * The default task to start a broker or a group of brokers      * @param brokerURIs      */
specifier|protected
name|void
name|runTask
parameter_list|(
name|List
name|brokerURIs
parameter_list|)
throws|throws
name|Exception
block|{
try|try
block|{
comment|// If no config uri, use default setting
if|if
condition|(
name|brokerURIs
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
name|setConfigUri
argument_list|(
operator|new
name|URI
argument_list|(
name|DEFAULT_CONFIG_URI
argument_list|)
argument_list|)
expr_stmt|;
name|startBroker
argument_list|(
name|getConfigUri
argument_list|()
argument_list|)
expr_stmt|;
comment|// Set configuration data, if available, which in this case would be the config URI
block|}
else|else
block|{
name|String
name|strConfigURI
decl_stmt|;
while|while
condition|(
operator|!
name|brokerURIs
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
name|strConfigURI
operator|=
operator|(
name|String
operator|)
name|brokerURIs
operator|.
name|remove
argument_list|(
literal|0
argument_list|)
expr_stmt|;
try|try
block|{
name|setConfigUri
argument_list|(
operator|new
name|URI
argument_list|(
name|strConfigURI
argument_list|)
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|URISyntaxException
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
return|return;
block|}
name|startBroker
argument_list|(
name|getConfigUri
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
comment|// Prevent the main thread from exiting unless it is terminated elsewhere
name|waitForShutdown
argument_list|()
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
literal|"Failed to execute start task. Reason: "
operator|+
name|e
argument_list|,
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
comment|/**      * Create and run a broker specified by the given configuration URI      * @param configURI      * @throws Exception      */
specifier|public
name|void
name|startBroker
parameter_list|(
name|URI
name|configURI
parameter_list|)
throws|throws
name|Exception
block|{
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"Loading message broker from: "
operator|+
name|configURI
argument_list|)
expr_stmt|;
name|BrokerService
name|broker
init|=
name|BrokerFactory
operator|.
name|createBroker
argument_list|(
name|configURI
argument_list|)
decl_stmt|;
name|brokers
operator|.
name|add
argument_list|(
name|broker
argument_list|)
expr_stmt|;
name|broker
operator|.
name|start
argument_list|()
expr_stmt|;
block|}
comment|/**      * Wait for a shutdown invocation elsewhere      * @throws Exception      */
specifier|protected
name|void
name|waitForShutdown
parameter_list|()
throws|throws
name|Exception
block|{
specifier|final
name|boolean
index|[]
name|shutdown
init|=
operator|new
name|boolean
index|[]
block|{
literal|false
block|}
decl_stmt|;
name|Runtime
operator|.
name|getRuntime
argument_list|()
operator|.
name|addShutdownHook
argument_list|(
operator|new
name|Thread
argument_list|()
block|{
specifier|public
name|void
name|run
parameter_list|()
block|{
synchronized|synchronized
init|(
name|shutdown
init|)
block|{
name|shutdown
index|[
literal|0
index|]
operator|=
literal|true
expr_stmt|;
name|shutdown
operator|.
name|notify
argument_list|()
expr_stmt|;
block|}
block|}
block|}
argument_list|)
expr_stmt|;
comment|// Wait for any shutdown event
synchronized|synchronized
init|(
name|shutdown
init|)
block|{
while|while
condition|(
operator|!
name|shutdown
index|[
literal|0
index|]
condition|)
block|{
try|try
block|{
name|shutdown
operator|.
name|wait
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|InterruptedException
name|e
parameter_list|)
block|{                 }
block|}
block|}
comment|// Stop each broker
for|for
control|(
name|Iterator
name|i
init|=
name|brokers
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
name|BrokerService
name|broker
init|=
operator|(
name|BrokerService
operator|)
name|i
operator|.
name|next
argument_list|()
decl_stmt|;
name|broker
operator|.
name|stop
argument_list|()
expr_stmt|;
block|}
block|}
comment|/**      * Sets the current configuration URI used by the start task      * @param uri      */
specifier|public
name|void
name|setConfigUri
parameter_list|(
name|URI
name|uri
parameter_list|)
block|{
name|configURI
operator|=
name|uri
expr_stmt|;
block|}
comment|/**      * Gets the current configuration URI used by the start task      * @return current configuration URI      */
specifier|public
name|URI
name|getConfigUri
parameter_list|()
block|{
return|return
name|configURI
return|;
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
literal|"Task Usage: Main start [start-options] [uri]"
block|,
literal|"Description: Creates and starts a broker using a configuration file, or a broker URI."
block|,
literal|""
block|,
literal|"Start Options:"
block|,
literal|"    -D<name>=<value>      Define a system property."
block|,
literal|"    --version             Display the version information."
block|,
literal|"    -h,-?,--help          Display the start broker help information."
block|,
literal|""
block|,
literal|"URI:"
block|,
literal|""
block|,
literal|"    XBean based broker configuration:"
block|,
literal|""
block|,
literal|"        Example: Main xbean:file:activemq.xml"
block|,
literal|"            Loads the xbean configuration file from the current working directory"
block|,
literal|"        Example: Main xbean:activemq.xml"
block|,
literal|"            Loads the xbean configuration file from the classpath"
block|,
literal|""
block|,
literal|"    URI Parameter based broker configuration:"
block|,
literal|""
block|,
literal|"        Example: Main broker:(tcp://localhost:61616, tcp://localhost:5000)?useJmx=true"
block|,
literal|"            Configures the broker with 2 transport connectors and jmx enabled"
block|,
literal|"        Example: Main broker:(tcp://localhost:61616, network:tcp://localhost:5000)?persistent=false"
block|,
literal|"            Configures the broker with 1 transport connector, and 1 network connector and persistence disabled"
block|,
literal|""
block|}
decl_stmt|;
block|}
end_class

end_unit

