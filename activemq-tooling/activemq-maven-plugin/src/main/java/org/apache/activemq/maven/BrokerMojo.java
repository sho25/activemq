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
name|maven
package|;
end_package

begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *      http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

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
name|maven
operator|.
name|plugin
operator|.
name|AbstractMojo
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|maven
operator|.
name|plugin
operator|.
name|MojoExecutionException
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|maven
operator|.
name|project
operator|.
name|MavenProject
import|;
end_import

begin_comment
comment|/**  * Goal which starts an activemq broker.  *  * @goal run  * @phase process-sources  */
end_comment

begin_class
specifier|public
class|class
name|BrokerMojo
extends|extends
name|AbstractMojo
block|{
comment|/**      * The maven project.      *      * @parameter property="project"      * @required      * @readonly      */
specifier|protected
name|MavenProject
name|project
decl_stmt|;
comment|/**      * The broker configuration uri The list of currently supported URI syntaxes      * is described<a      * href="http://activemq.apache.org/how-do-i-embed-a-broker-inside-a-connection.html">here</a>      *      * @parameter property="configUri"      *            default-value="broker:(tcp://localhost:61616)?useJmx=false&persistent=false"      * @required      */
specifier|private
name|String
name|configUri
decl_stmt|;
comment|/**      * Indicates whether to fork the broker, useful for integration tests.      *      * @parameter property="fork" default-value="false"      */
specifier|private
name|boolean
name|fork
decl_stmt|;
comment|/**      * System properties to add      *      * @parameter property="systemProperties"      */
specifier|private
name|Properties
name|systemProperties
decl_stmt|;
comment|/**      * Skip execution of the ActiveMQ Broker plugin if set to true      *      * @parameter property="skip"      */
specifier|private
name|boolean
name|skip
decl_stmt|;
annotation|@
name|Override
specifier|public
name|void
name|execute
parameter_list|()
throws|throws
name|MojoExecutionException
block|{
try|try
block|{
if|if
condition|(
name|skip
condition|)
block|{
name|getLog
argument_list|()
operator|.
name|info
argument_list|(
literal|"Skipped execution of ActiveMQ Broker"
argument_list|)
expr_stmt|;
return|return;
block|}
name|setSystemProperties
argument_list|()
expr_stmt|;
name|getLog
argument_list|()
operator|.
name|info
argument_list|(
literal|"Loading broker configUri: "
operator|+
name|configUri
argument_list|)
expr_stmt|;
if|if
condition|(
name|XBeanFileResolver
operator|.
name|isXBeanFile
argument_list|(
name|configUri
argument_list|)
condition|)
block|{
name|getLog
argument_list|()
operator|.
name|debug
argument_list|(
literal|"configUri before transformation: "
operator|+
name|configUri
argument_list|)
expr_stmt|;
name|configUri
operator|=
name|XBeanFileResolver
operator|.
name|toUrlCompliantAbsolutePath
argument_list|(
name|configUri
argument_list|)
expr_stmt|;
name|getLog
argument_list|()
operator|.
name|debug
argument_list|(
literal|"configUri after transformation: "
operator|+
name|configUri
argument_list|)
expr_stmt|;
block|}
specifier|final
name|BrokerService
name|broker
init|=
name|BrokerFactory
operator|.
name|createBroker
argument_list|(
name|configUri
argument_list|)
decl_stmt|;
if|if
condition|(
name|fork
condition|)
block|{
operator|new
name|Thread
argument_list|(
operator|new
name|Runnable
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|void
name|run
parameter_list|()
block|{
try|try
block|{
name|broker
operator|.
name|start
argument_list|()
expr_stmt|;
name|waitForShutdown
argument_list|(
name|broker
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
name|e
operator|.
name|printStackTrace
argument_list|()
expr_stmt|;
block|}
block|}
block|}
argument_list|)
operator|.
name|start
argument_list|()
expr_stmt|;
block|}
else|else
block|{
name|broker
operator|.
name|start
argument_list|()
expr_stmt|;
name|waitForShutdown
argument_list|(
name|broker
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
throw|throw
operator|new
name|MojoExecutionException
argument_list|(
literal|"Failed to start ActiveMQ Broker"
argument_list|,
name|e
argument_list|)
throw|;
block|}
block|}
comment|/**      * Wait for a shutdown invocation elsewhere      *      * @throws Exception      */
specifier|protected
name|void
name|waitForShutdown
parameter_list|(
name|BrokerService
name|broker
parameter_list|)
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
annotation|@
name|Override
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
comment|// Stop broker
name|broker
operator|.
name|stop
argument_list|()
expr_stmt|;
block|}
comment|/**      * Set system properties      */
specifier|protected
name|void
name|setSystemProperties
parameter_list|()
block|{
comment|// Set the default properties
name|System
operator|.
name|setProperty
argument_list|(
literal|"activemq.base"
argument_list|,
name|project
operator|.
name|getBuild
argument_list|()
operator|.
name|getDirectory
argument_list|()
operator|+
literal|"/"
argument_list|)
expr_stmt|;
name|System
operator|.
name|setProperty
argument_list|(
literal|"activemq.home"
argument_list|,
name|project
operator|.
name|getBuild
argument_list|()
operator|.
name|getDirectory
argument_list|()
operator|+
literal|"/"
argument_list|)
expr_stmt|;
name|System
operator|.
name|setProperty
argument_list|(
literal|"org.apache.activemq.UseDedicatedTaskRunner"
argument_list|,
literal|"true"
argument_list|)
expr_stmt|;
name|System
operator|.
name|setProperty
argument_list|(
literal|"org.apache.activemq.default.directory.prefix"
argument_list|,
name|project
operator|.
name|getBuild
argument_list|()
operator|.
name|getDirectory
argument_list|()
operator|+
literal|"/"
argument_list|)
expr_stmt|;
name|System
operator|.
name|setProperty
argument_list|(
literal|"derby.system.home"
argument_list|,
name|project
operator|.
name|getBuild
argument_list|()
operator|.
name|getDirectory
argument_list|()
operator|+
literal|"/"
argument_list|)
expr_stmt|;
name|System
operator|.
name|setProperty
argument_list|(
literal|"derby.storage.fileSyncTransactionLog"
argument_list|,
literal|"true"
argument_list|)
expr_stmt|;
comment|// Overwrite any custom properties
name|System
operator|.
name|getProperties
argument_list|()
operator|.
name|putAll
argument_list|(
name|systemProperties
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

