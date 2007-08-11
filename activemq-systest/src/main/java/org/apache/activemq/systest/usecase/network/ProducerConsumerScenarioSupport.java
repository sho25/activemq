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
name|systest
operator|.
name|usecase
operator|.
name|network
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
name|systest
operator|.
name|ConsumerAgent
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
name|MessageList
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
name|ProducerAgent
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
name|ScenarioSupport
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|jms
operator|.
name|Destination
import|;
end_import

begin_comment
comment|/**  *   * @version $Revision: 1.1 $  */
end_comment

begin_class
specifier|public
specifier|abstract
class|class
name|ProducerConsumerScenarioSupport
extends|extends
name|ScenarioSupport
block|{
specifier|protected
name|ProducerAgent
name|producer
decl_stmt|;
specifier|protected
name|ConsumerAgent
name|consumer
decl_stmt|;
specifier|protected
name|MessageList
name|messageList
decl_stmt|;
specifier|public
name|ProducerConsumerScenarioSupport
parameter_list|(
name|ProducerAgent
name|producer
parameter_list|,
name|ConsumerAgent
name|consumer
parameter_list|,
name|MessageList
name|messageList
parameter_list|)
block|{
name|this
operator|.
name|consumer
operator|=
name|consumer
expr_stmt|;
name|this
operator|.
name|producer
operator|=
name|producer
expr_stmt|;
name|this
operator|.
name|messageList
operator|=
name|messageList
expr_stmt|;
block|}
specifier|public
name|void
name|setDestination
parameter_list|(
name|Destination
name|destination
parameter_list|)
block|{
name|producer
operator|.
name|setDestination
argument_list|(
name|destination
argument_list|)
expr_stmt|;
name|consumer
operator|.
name|setDestination
argument_list|(
name|destination
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|start
parameter_list|()
throws|throws
name|Exception
block|{
name|start
argument_list|(
name|messageList
argument_list|)
expr_stmt|;
name|start
argument_list|(
name|consumer
argument_list|)
expr_stmt|;
name|start
argument_list|(
name|producer
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

