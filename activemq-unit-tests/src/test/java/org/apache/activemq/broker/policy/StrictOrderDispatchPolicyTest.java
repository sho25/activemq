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
name|broker
operator|.
name|policy
package|;
end_package

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Iterator
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|jms
operator|.
name|MessageConsumer
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
name|TopicSubscriptionTest
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
name|PolicyMap
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
name|StrictOrderDispatchPolicy
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
name|util
operator|.
name|MessageIdList
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|Test
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|runner
operator|.
name|RunWith
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|runners
operator|.
name|BlockJUnit4ClassRunner
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|junit
operator|.
name|Assert
operator|.
name|*
import|;
end_import

begin_class
annotation|@
name|RunWith
argument_list|(
name|BlockJUnit4ClassRunner
operator|.
name|class
argument_list|)
specifier|public
class|class
name|StrictOrderDispatchPolicyTest
extends|extends
name|TopicSubscriptionTest
block|{
annotation|@
name|Override
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
name|StrictOrderDispatchPolicy
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
annotation|@
name|Test
annotation|@
name|Override
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
name|assertReceivedMessagesAreOrdered
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
annotation|@
name|Override
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
name|assertReceivedMessagesAreOrdered
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
annotation|@
name|Override
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
name|assertReceivedMessagesAreOrdered
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
annotation|@
name|Override
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
name|assertReceivedMessagesAreOrdered
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
annotation|@
name|Override
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
name|assertReceivedMessagesAreOrdered
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
annotation|@
name|Override
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
name|assertReceivedMessagesAreOrdered
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
annotation|@
name|Override
specifier|public
name|void
name|testManyProducersOneConsumer
parameter_list|()
throws|throws
name|Exception
block|{
name|super
operator|.
name|testManyProducersOneConsumer
argument_list|()
expr_stmt|;
name|assertReceivedMessagesAreOrdered
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
annotation|@
name|Override
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
name|assertReceivedMessagesAreOrdered
argument_list|()
expr_stmt|;
block|}
specifier|public
name|void
name|assertReceivedMessagesAreOrdered
parameter_list|()
throws|throws
name|Exception
block|{
comment|// If there is only one consumer, messages is definitely ordered
if|if
condition|(
name|consumers
operator|.
name|size
argument_list|()
operator|<=
literal|1
condition|)
block|{
return|return;
block|}
comment|// Get basis of order
name|Iterator
argument_list|<
name|MessageConsumer
argument_list|>
name|i
init|=
name|consumers
operator|.
name|keySet
argument_list|()
operator|.
name|iterator
argument_list|()
decl_stmt|;
name|MessageIdList
name|messageOrder
init|=
operator|(
name|MessageIdList
operator|)
name|consumers
operator|.
name|get
argument_list|(
name|i
operator|.
name|next
argument_list|()
argument_list|)
decl_stmt|;
for|for
control|(
init|;
name|i
operator|.
name|hasNext
argument_list|()
condition|;
control|)
block|{
name|MessageIdList
name|messageIdList
init|=
operator|(
name|MessageIdList
operator|)
name|consumers
operator|.
name|get
argument_list|(
name|i
operator|.
name|next
argument_list|()
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
literal|"Messages are not ordered."
argument_list|,
name|messageOrder
operator|.
name|equals
argument_list|(
name|messageIdList
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

