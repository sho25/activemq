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
name|BrokerAgent
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

begin_comment
comment|/**  *   * @version $Revision: 1.1 $  */
end_comment

begin_class
specifier|public
class|class
name|SingleBrokerScenario
extends|extends
name|ProducerConsumerScenarioSupport
block|{
specifier|private
name|BrokerAgent
name|broker
decl_stmt|;
specifier|public
name|SingleBrokerScenario
parameter_list|(
name|BrokerAgent
name|broker
parameter_list|,
name|ProducerAgent
name|producer
parameter_list|,
name|ConsumerAgent
name|consumer
parameter_list|,
name|MessageList
name|list
parameter_list|)
block|{
name|super
argument_list|(
name|producer
argument_list|,
name|consumer
argument_list|,
name|list
argument_list|)
expr_stmt|;
name|this
operator|.
name|broker
operator|=
name|broker
expr_stmt|;
block|}
specifier|public
name|void
name|run
parameter_list|()
throws|throws
name|Exception
block|{
name|producer
operator|.
name|sendMessages
argument_list|(
name|messageList
argument_list|)
expr_stmt|;
name|consumer
operator|.
name|assertConsumed
argument_list|(
name|messageList
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
name|broker
argument_list|)
expr_stmt|;
name|consumer
operator|.
name|connectTo
argument_list|(
name|broker
argument_list|)
expr_stmt|;
name|producer
operator|.
name|connectTo
argument_list|(
name|broker
argument_list|)
expr_stmt|;
name|super
operator|.
name|start
argument_list|()
expr_stmt|;
block|}
block|}
end_class

end_unit

