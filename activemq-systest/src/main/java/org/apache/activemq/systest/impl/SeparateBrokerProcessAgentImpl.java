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
name|systest
operator|.
name|impl
package|;
end_package

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|File
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
name|ActiveMQConnectionFactory
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
name|systest
operator|.
name|BrokerAgent
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|jms
operator|.
name|ConnectionFactory
import|;
end_import

begin_comment
comment|/**  * Runs a broker in a separate process  *   * @version $Revision: 1.1 $  */
end_comment

begin_class
specifier|public
class|class
name|SeparateBrokerProcessAgentImpl
extends|extends
name|SeparateProcessAgent
implements|implements
name|BrokerAgent
block|{
specifier|private
specifier|static
specifier|final
name|String
name|ENV_HOME
init|=
literal|"ACTIVEMQ_HOME"
decl_stmt|;
specifier|private
specifier|static
name|int
name|portCounter
init|=
literal|61616
decl_stmt|;
specifier|private
name|int
name|port
decl_stmt|;
specifier|private
name|String
name|connectionURI
decl_stmt|;
specifier|private
name|String
name|brokerScript
decl_stmt|;
specifier|private
name|File
name|workingDirectory
init|=
operator|new
name|File
argument_list|(
literal|"target/test-brokers"
argument_list|)
decl_stmt|;
specifier|private
name|String
name|defaultPrefix
init|=
literal|"~/activemq"
decl_stmt|;
specifier|private
name|String
name|coreURI
decl_stmt|;
specifier|public
name|SeparateBrokerProcessAgentImpl
parameter_list|(
name|String
name|host
parameter_list|)
throws|throws
name|Exception
block|{
name|port
operator|=
name|portCounter
operator|++
expr_stmt|;
name|coreURI
operator|=
literal|"tcp://"
operator|+
name|host
operator|+
literal|":"
operator|+
name|port
expr_stmt|;
name|connectionURI
operator|=
literal|"failover:("
operator|+
name|coreURI
operator|+
literal|")?useExponentialBackOff=false&initialReconnectDelay=500&&maxReconnectAttempts=20"
expr_stmt|;
block|}
specifier|public
name|void
name|kill
parameter_list|()
throws|throws
name|Exception
block|{
name|stop
argument_list|()
expr_stmt|;
block|}
specifier|public
name|ConnectionFactory
name|getConnectionFactory
parameter_list|()
block|{
return|return
operator|new
name|ActiveMQConnectionFactory
argument_list|(
name|getConnectionURI
argument_list|()
argument_list|)
return|;
block|}
specifier|public
name|String
name|getConnectionURI
parameter_list|()
block|{
return|return
name|connectionURI
return|;
block|}
specifier|public
name|void
name|connectTo
parameter_list|(
name|BrokerAgent
name|remoteBroker
parameter_list|)
throws|throws
name|Exception
block|{
comment|// lets assume discovery works! :)
block|}
specifier|public
name|String
name|getBrokerScript
parameter_list|()
block|{
if|if
condition|(
name|brokerScript
operator|==
literal|null
condition|)
block|{
name|brokerScript
operator|=
name|createBrokerScript
argument_list|()
expr_stmt|;
block|}
return|return
name|brokerScript
return|;
block|}
specifier|public
name|void
name|setBrokerScript
parameter_list|(
name|String
name|activemqScript
parameter_list|)
block|{
name|this
operator|.
name|brokerScript
operator|=
name|activemqScript
expr_stmt|;
block|}
specifier|public
name|String
name|getDefaultPrefix
parameter_list|()
block|{
return|return
name|defaultPrefix
return|;
block|}
specifier|public
name|void
name|setDefaultPrefix
parameter_list|(
name|String
name|defaultPrefix
parameter_list|)
block|{
name|this
operator|.
name|defaultPrefix
operator|=
name|defaultPrefix
expr_stmt|;
block|}
specifier|public
name|File
name|getWorkingDirectory
parameter_list|()
block|{
return|return
name|workingDirectory
return|;
block|}
specifier|public
name|void
name|setWorkingDirectory
parameter_list|(
name|File
name|workingDirectory
parameter_list|)
block|{
name|this
operator|.
name|workingDirectory
operator|=
name|workingDirectory
expr_stmt|;
block|}
comment|// Implementation methods
comment|// -------------------------------------------------------------------------
specifier|protected
name|Process
name|createProcess
parameter_list|()
throws|throws
name|Exception
block|{
name|String
name|commands
index|[]
init|=
name|getCommands
argument_list|()
decl_stmt|;
name|System
operator|.
name|out
operator|.
name|print
argument_list|(
literal|"About to execute command:"
argument_list|)
expr_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|commands
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|System
operator|.
name|out
operator|.
name|print
argument_list|(
literal|" "
argument_list|)
expr_stmt|;
name|System
operator|.
name|out
operator|.
name|print
argument_list|(
name|commands
index|[
name|i
index|]
argument_list|)
expr_stmt|;
block|}
name|System
operator|.
name|out
operator|.
name|println
argument_list|()
expr_stmt|;
name|File
name|workingDir
init|=
name|createBrokerWorkingDirectory
argument_list|()
decl_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"In directory: "
operator|+
name|workingDir
argument_list|)
expr_stmt|;
name|Process
name|answer
init|=
name|Runtime
operator|.
name|getRuntime
argument_list|()
operator|.
name|exec
argument_list|(
name|commands
argument_list|,
literal|null
argument_list|,
name|workingDir
argument_list|)
decl_stmt|;
return|return
name|answer
return|;
block|}
specifier|protected
name|File
name|createBrokerWorkingDirectory
parameter_list|()
block|{
name|workingDirectory
operator|.
name|mkdirs
argument_list|()
expr_stmt|;
comment|// now lets create a new temporary directory
name|File
name|brokerDir
init|=
operator|new
name|File
argument_list|(
name|workingDirectory
argument_list|,
literal|"broker_"
operator|+
name|port
argument_list|)
decl_stmt|;
name|brokerDir
operator|.
name|mkdirs
argument_list|()
expr_stmt|;
name|File
name|varDir
init|=
operator|new
name|File
argument_list|(
name|brokerDir
argument_list|,
literal|"data"
argument_list|)
decl_stmt|;
name|varDir
operator|.
name|mkdirs
argument_list|()
expr_stmt|;
name|File
name|workDir
init|=
operator|new
name|File
argument_list|(
name|brokerDir
argument_list|,
literal|"work"
argument_list|)
decl_stmt|;
name|workDir
operator|.
name|mkdirs
argument_list|()
expr_stmt|;
return|return
name|workDir
return|;
block|}
specifier|protected
name|String
name|createBrokerScript
parameter_list|()
block|{
name|String
name|version
init|=
literal|null
decl_stmt|;
name|Package
name|p
init|=
name|Package
operator|.
name|getPackage
argument_list|(
literal|"org.apache.activemq"
argument_list|)
decl_stmt|;
if|if
condition|(
name|p
operator|!=
literal|null
condition|)
block|{
name|version
operator|=
name|p
operator|.
name|getImplementationVersion
argument_list|()
expr_stmt|;
block|}
if|if
condition|(
name|version
operator|==
literal|null
condition|)
block|{
name|version
operator|=
literal|"activemq-4.0-SNAPSHOT"
expr_stmt|;
block|}
return|return
literal|"../../../../../assembly/target/"
operator|+
name|version
operator|+
literal|"/bin/"
operator|+
name|version
operator|+
literal|"/bin/activemq"
return|;
block|}
specifier|protected
name|String
index|[]
name|createCommand
parameter_list|()
block|{
comment|// lets try load the broker script from a system property
name|String
name|script
init|=
name|System
operator|.
name|getProperty
argument_list|(
literal|"brokerScript"
argument_list|)
decl_stmt|;
if|if
condition|(
name|script
operator|==
literal|null
condition|)
block|{
name|String
name|home
init|=
name|System
operator|.
name|getenv
argument_list|(
name|ENV_HOME
argument_list|)
decl_stmt|;
if|if
condition|(
name|home
operator|==
literal|null
condition|)
block|{
name|script
operator|=
name|getBrokerScript
argument_list|()
expr_stmt|;
block|}
else|else
block|{
name|script
operator|=
name|home
operator|+
literal|"/bin/"
operator|+
name|brokerScript
expr_stmt|;
block|}
block|}
name|String
index|[]
name|answer
init|=
block|{
literal|"/bin/bash"
block|,
name|script
block|,
literal|"broker:"
operator|+
name|coreURI
block|}
decl_stmt|;
return|return
name|answer
return|;
block|}
block|}
end_class

end_unit

