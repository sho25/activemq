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
name|broker
operator|.
name|policy
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
name|QueueSubscriptionTest
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
name|broker
operator|.
name|region
operator|.
name|policy
operator|.
name|PolicyEntry
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
name|region
operator|.
name|policy
operator|.
name|RoundRobinDispatchPolicy
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
name|region
operator|.
name|policy
operator|.
name|PolicyMap
import|;
end_import

begin_class
specifier|public
class|class
name|RoundRobinDispatchPolicyTest
extends|extends
name|QueueSubscriptionTest
block|{
specifier|protected
name|BrokerService
name|createBroker
parameter_list|()
throws|throws
name|Exception
block|{
name|BrokerService
name|broker
init|=
name|super
operator|.
name|createBroker
argument_list|()
decl_stmt|;
name|PolicyEntry
name|policy
init|=
operator|new
name|PolicyEntry
argument_list|()
decl_stmt|;
name|policy
operator|.
name|setDispatchPolicy
argument_list|(
operator|new
name|RoundRobinDispatchPolicy
argument_list|()
argument_list|)
expr_stmt|;
name|PolicyMap
name|pMap
init|=
operator|new
name|PolicyMap
argument_list|()
decl_stmt|;
name|pMap
operator|.
name|setDefaultEntry
argument_list|(
name|policy
argument_list|)
expr_stmt|;
name|broker
operator|.
name|setDestinationPolicy
argument_list|(
name|pMap
argument_list|)
expr_stmt|;
return|return
name|broker
return|;
block|}
specifier|public
name|void
name|testOneProducerTwoConsumersSmallMessagesOnePrefetch
parameter_list|()
throws|throws
name|Exception
block|{
name|super
operator|.
name|testOneProducerTwoConsumersSmallMessagesOnePrefetch
argument_list|()
expr_stmt|;
comment|// Ensure that each consumer should have received at least one message
comment|// We cannot guarantee that messages will be equally divided, since prefetch is one
name|assertEachConsumerReceivedAtLeastXMessages
argument_list|(
literal|1
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|testOneProducerTwoConsumersSmallMessagesLargePrefetch
parameter_list|()
throws|throws
name|Exception
block|{
name|super
operator|.
name|testOneProducerTwoConsumersSmallMessagesLargePrefetch
argument_list|()
expr_stmt|;
name|assertMessagesDividedAmongConsumers
argument_list|()
expr_stmt|;
block|}
specifier|public
name|void
name|testOneProducerTwoConsumersLargeMessagesOnePrefetch
parameter_list|()
throws|throws
name|Exception
block|{
name|super
operator|.
name|testOneProducerTwoConsumersLargeMessagesOnePrefetch
argument_list|()
expr_stmt|;
comment|// Ensure that each consumer should have received at least one message
comment|// We cannot guarantee that messages will be equally divided, since prefetch is one
name|assertEachConsumerReceivedAtLeastXMessages
argument_list|(
literal|1
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|testOneProducerTwoConsumersLargeMessagesLargePrefetch
parameter_list|()
throws|throws
name|Exception
block|{
name|super
operator|.
name|testOneProducerTwoConsumersLargeMessagesLargePrefetch
argument_list|()
expr_stmt|;
name|assertMessagesDividedAmongConsumers
argument_list|()
expr_stmt|;
block|}
specifier|public
name|void
name|testOneProducerManyConsumersFewMessages
parameter_list|()
throws|throws
name|Exception
block|{
name|super
operator|.
name|testOneProducerManyConsumersFewMessages
argument_list|()
expr_stmt|;
comment|// Since there are more consumers, each consumer should have received at most one message only
name|assertMessagesDividedAmongConsumers
argument_list|()
expr_stmt|;
block|}
specifier|public
name|void
name|testOneProducerManyConsumersManyMessages
parameter_list|()
throws|throws
name|Exception
block|{
name|super
operator|.
name|testOneProducerManyConsumersManyMessages
argument_list|()
expr_stmt|;
name|assertMessagesDividedAmongConsumers
argument_list|()
expr_stmt|;
block|}
specifier|public
name|void
name|testManyProducersManyConsumers
parameter_list|()
throws|throws
name|Exception
block|{
name|super
operator|.
name|testManyProducersManyConsumers
argument_list|()
expr_stmt|;
name|assertMessagesDividedAmongConsumers
argument_list|()
expr_stmt|;
block|}
specifier|public
name|void
name|assertMessagesDividedAmongConsumers
parameter_list|()
block|{
name|assertEachConsumerReceivedAtLeastXMessages
argument_list|(
operator|(
name|messageCount
operator|*
name|producerCount
operator|)
operator|/
name|consumerCount
argument_list|)
expr_stmt|;
name|assertEachConsumerReceivedAtMostXMessages
argument_list|(
operator|(
operator|(
name|messageCount
operator|*
name|producerCount
operator|)
operator|/
name|consumerCount
operator|)
operator|+
literal|1
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

