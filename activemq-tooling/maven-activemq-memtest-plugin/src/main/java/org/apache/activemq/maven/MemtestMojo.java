begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
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
name|activemq
operator|.
name|tool
operator|.
name|JMSMemtest
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|jms
operator|.
name|JMSException
import|;
end_import

begin_comment
comment|/**  * Goal which does a memory usage test  to check for any memory leak  *  * @goal memtest  * @phase process-sources  */
end_comment

begin_class
specifier|public
class|class
name|MemtestMojo
extends|extends
name|AbstractMojo
block|{
comment|/**      * @parameter expression="${url}       *      */
specifier|private
name|String
name|url
decl_stmt|;
comment|/**      * @parameter expression="${topic}" default-value="true"      * @required      */
specifier|private
name|String
name|topic
decl_stmt|;
comment|/**      * @parameter expression="${connectionCheckpointSize}"  default-value="-1"      * @required      */
specifier|private
name|String
name|connectionCheckpointSize
decl_stmt|;
comment|/**      * @parameter expression="${durable}" default-value="false"      * @required      */
specifier|private
name|String
name|durable
decl_stmt|;
comment|/**      * @parameter expression="${producerCount}" default-value="1"      * @required      */
specifier|private
name|String
name|producerCount
decl_stmt|;
comment|/**      * @parameter expression="${prefetchSize}" default-value="-1"      * @required      */
specifier|private
name|String
name|prefetchSize
decl_stmt|;
comment|/**      * @parameter expression="${consumerCount}" default-value="1"      * @required      */
specifier|private
name|String
name|consumerCount
decl_stmt|;
comment|/**      * @parameter expression="${messageCount}" default-value="100000"      * @required      */
specifier|private
name|String
name|messageCount
decl_stmt|;
comment|/**      * @parameter expression="${messageSize}" default-value="10240"      * @required      */
specifier|private
name|String
name|messageSize
decl_stmt|;
comment|/**      * @parameter expression="${checkpointInterval}" default-value="2"      * @required      */
specifier|private
name|String
name|checkpointInterval
decl_stmt|;
comment|/**      * @parameter expression="${destinationName}" default-value="FOO.BAR"      * @required      */
specifier|private
name|String
name|destinationName
decl_stmt|;
comment|/**      * @parameter expression="${reportName}" default-value="activemq-memory-usage-report"      * @required      */
specifier|private
name|String
name|reportName
decl_stmt|;
comment|/**      * @parameter expression="${reportDirectory}" default-value="${project.build.directory}/test-memtest"      * @required      */
specifier|private
name|String
name|reportDirectory
decl_stmt|;
specifier|public
name|void
name|execute
parameter_list|()
throws|throws
name|MojoExecutionException
block|{
name|JMSMemtest
operator|.
name|main
argument_list|(
name|createArgument
argument_list|()
argument_list|)
expr_stmt|;
block|}
specifier|public
name|String
index|[]
name|createArgument
parameter_list|()
block|{
name|String
index|[]
name|options
init|=
block|{
literal|"url="
operator|+
name|url
block|,
literal|"topic="
operator|+
name|topic
block|,
literal|"durable="
operator|+
name|durable
block|,
literal|"connectionCheckpointSize="
operator|+
name|connectionCheckpointSize
block|,
literal|"producerCount="
operator|+
name|producerCount
block|,
literal|"consumerCount="
operator|+
name|consumerCount
block|,
literal|"messageCount="
operator|+
name|messageCount
block|,
literal|"messageSize="
operator|+
name|messageSize
block|,
literal|"checkpointInterval="
operator|+
name|checkpointInterval
block|,
literal|"destinationName="
operator|+
name|destinationName
block|,
literal|"reportName="
operator|+
name|reportName
block|,
literal|"prefetchSize="
operator|+
name|prefetchSize
block|,
literal|"reportDirectory="
operator|+
name|reportDirectory
block|,         }
decl_stmt|;
return|return
name|options
return|;
block|}
block|}
end_class

end_unit

