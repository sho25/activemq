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
package|;
end_package

begin_comment
comment|/**  * A helper class for working with agents  *    * @version $Revision: 1.1 $  */
end_comment

begin_class
specifier|public
specifier|abstract
class|class
name|AgentSupport
implements|implements
name|Agent
block|{
specifier|public
name|void
name|stop
parameter_list|()
throws|throws
name|Exception
block|{
name|AgentStopper
name|stopper
init|=
operator|new
name|AgentStopper
argument_list|()
decl_stmt|;
name|stop
argument_list|(
name|stopper
argument_list|)
expr_stmt|;
name|stopper
operator|.
name|throwFirstException
argument_list|()
expr_stmt|;
block|}
comment|/**      * Provides a way for derived classes to stop resources cleanly, handling exceptions      */
specifier|public
specifier|abstract
name|void
name|stop
parameter_list|(
name|AgentStopper
name|stopper
parameter_list|)
function_decl|;
block|}
end_class

end_unit

