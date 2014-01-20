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

begin_comment
comment|/**  * Goal which stops an activemq broker.  *  * @goal stop  * @phase process-sources  */
end_comment

begin_class
specifier|public
class|class
name|StopBrokerMojo
extends|extends
name|AbstractMojo
block|{
comment|/**      * Skip execution of the ActiveMQ Broker plugin if set to true      *      * @parameter property="skip"      */
specifier|private
name|boolean
name|skip
decl_stmt|;
specifier|public
name|void
name|execute
parameter_list|()
throws|throws
name|MojoExecutionException
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
name|Broker
operator|.
name|stop
argument_list|()
expr_stmt|;
name|getLog
argument_list|()
operator|.
name|info
argument_list|(
literal|"Stopped the ActiveMQ Broker"
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

