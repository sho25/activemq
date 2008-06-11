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
name|Set
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
name|ListCommand
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
literal|"Task Usage: Main list [list-options]"
block|,
literal|"Description:  Lists all available broker in the specified JMX context."
block|,
literal|""
block|,
literal|"List Options:"
block|,
literal|"    --jmxurl<url>             Set the JMX URL to connect to."
block|,
literal|"    --jmxuser<user>           Set the JMX user used for authenticating."
block|,
literal|"    --jmxpassword<password>   Set the JMX password used for authenticating."
block|,
literal|"    --jmxlocal                 Use the local JMX server instead of a remote one."
block|,
literal|"    --version                  Display the version information."
block|,
literal|"    -h,-?,--help               Display the stop broker help information."
block|,
literal|""
block|}
decl_stmt|;
comment|/**      * List all running brokers registered in the specified JMX context      * @param tokens - command arguments      * @throws Exception      */
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
name|Set
argument_list|<
name|String
argument_list|>
name|propsView
init|=
operator|new
name|HashSet
argument_list|<
name|String
argument_list|>
argument_list|()
decl_stmt|;
name|propsView
operator|.
name|add
argument_list|(
literal|"BrokerName"
argument_list|)
expr_stmt|;
name|context
operator|.
name|printMBean
argument_list|(
name|JmxMBeansUtil
operator|.
name|filterMBeansView
argument_list|(
name|JmxMBeansUtil
operator|.
name|getAllBrokers
argument_list|(
name|createJmxConnection
argument_list|()
argument_list|)
argument_list|,
name|propsView
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
literal|"Failed to execute list task. Reason: "
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

