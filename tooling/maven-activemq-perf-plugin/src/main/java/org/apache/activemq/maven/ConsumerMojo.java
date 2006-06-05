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
comment|/*  * Copyright 2001-2005 The Apache Software Foundation.  *  * Licensed under the Apache License, Version 2.0 (the "License");  * you may not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *      http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

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
name|JmsConsumerClient
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
name|javax
operator|.
name|jms
operator|.
name|JMSException
import|;
end_import

begin_comment
comment|/**  * Goal which touches a timestamp file.  *  * @goal consumer  * @phase process-sources  */
end_comment

begin_class
specifier|public
class|class
name|ConsumerMojo
extends|extends
name|AbstractMojo
block|{
comment|/**      * @parameter expression="${url}" default-value="tcp://localhost:61616"      * @required      */
specifier|private
name|String
name|url
decl_stmt|;
comment|/**      * @parameter expression="${topic}" default-value="true"      * @required      */
specifier|private
name|String
name|topic
decl_stmt|;
comment|/**      * @parameter expression="${subject}"  default-value="FOO.BAR"      * @required      */
specifier|private
name|String
name|subject
decl_stmt|;
comment|/**      * @parameter expression="${durable}" default-value="false"      * @required      */
specifier|private
name|String
name|durable
decl_stmt|;
comment|/**      * @parameter expression="${maximumMessage}" default-value="10"      * @required      */
specifier|private
name|String
name|maximumMessage
decl_stmt|;
specifier|public
name|void
name|execute
parameter_list|()
throws|throws
name|MojoExecutionException
block|{
name|String
index|[]
name|args
init|=
block|{
name|url
block|,
name|topic
block|,
name|subject
block|,
name|durable
block|,
name|maximumMessage
block|}
decl_stmt|;
try|try
block|{
name|JmsConsumerClient
operator|.
name|main
argument_list|(
name|args
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|JMSException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|MojoExecutionException
argument_list|(
literal|"Error Executing Consumer: "
operator|+
name|e
operator|.
name|getMessage
argument_list|()
argument_list|)
throw|;
block|}
block|}
block|}
end_class

end_unit

