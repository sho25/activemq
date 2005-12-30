begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  *  * Copyright 2005-2006 The Apache Software Foundation  *  * Licensed under the Apache License, Version 2.0 (the "License");  * you may not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  * http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
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
package|;
end_package

begin_class
specifier|public
class|class
name|TopicSubscriptionTest
extends|extends
name|QueueSubscriptionTest
block|{
specifier|protected
name|void
name|setUp
parameter_list|()
throws|throws
name|Exception
block|{
name|super
operator|.
name|setUp
argument_list|()
expr_stmt|;
name|durable
operator|=
literal|true
expr_stmt|;
name|topic
operator|=
literal|true
expr_stmt|;
block|}
specifier|public
name|void
name|testOneProducerTwoConsumersLargeMessagesOnePrefetch
parameter_list|()
throws|throws
name|Exception
block|{
name|consumerCount
operator|=
literal|2
expr_stmt|;
name|producerCount
operator|=
literal|1
expr_stmt|;
name|messageCount
operator|=
literal|100
expr_stmt|;
name|messageSize
operator|=
literal|1024
operator|*
literal|1024
operator|*
literal|1
expr_stmt|;
comment|// 1 MB
name|prefetchCount
operator|=
literal|1
expr_stmt|;
name|doMultipleClientsTest
argument_list|()
expr_stmt|;
name|assertTotalMessagesReceived
argument_list|(
name|messageCount
operator|*
name|consumerCount
operator|*
name|producerCount
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|testOneProducerTwoConsumersSmallMessagesOnePrefetch
parameter_list|()
throws|throws
name|Exception
block|{
name|consumerCount
operator|=
literal|2
expr_stmt|;
name|producerCount
operator|=
literal|1
expr_stmt|;
name|prefetchCount
operator|=
literal|1
expr_stmt|;
name|messageSize
operator|=
literal|1024
expr_stmt|;
name|messageCount
operator|=
literal|1000
expr_stmt|;
name|doMultipleClientsTest
argument_list|()
expr_stmt|;
name|assertTotalMessagesReceived
argument_list|(
name|messageCount
operator|*
name|consumerCount
operator|*
name|producerCount
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
name|consumerCount
operator|=
literal|2
expr_stmt|;
name|producerCount
operator|=
literal|1
expr_stmt|;
name|messageCount
operator|=
literal|1000
expr_stmt|;
name|messageSize
operator|=
literal|1024
expr_stmt|;
name|prefetchCount
operator|=
name|messageCount
operator|*
literal|2
expr_stmt|;
name|doMultipleClientsTest
argument_list|()
expr_stmt|;
name|assertTotalMessagesReceived
argument_list|(
name|messageCount
operator|*
name|consumerCount
operator|*
name|producerCount
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
name|consumerCount
operator|=
literal|2
expr_stmt|;
name|producerCount
operator|=
literal|1
expr_stmt|;
name|messageCount
operator|=
literal|10
expr_stmt|;
name|messageSize
operator|=
literal|1024
operator|*
literal|1024
operator|*
literal|1
expr_stmt|;
comment|// 1 MB
name|prefetchCount
operator|=
name|messageCount
operator|*
literal|2
expr_stmt|;
name|doMultipleClientsTest
argument_list|()
expr_stmt|;
name|assertTotalMessagesReceived
argument_list|(
name|messageCount
operator|*
name|consumerCount
operator|*
name|producerCount
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|testOneProducerManyConsumersFewMessages
parameter_list|()
throws|throws
name|Exception
block|{
name|consumerCount
operator|=
literal|50
expr_stmt|;
name|producerCount
operator|=
literal|1
expr_stmt|;
name|messageCount
operator|=
literal|10
expr_stmt|;
name|messageSize
operator|=
literal|1
expr_stmt|;
comment|// 1 byte
name|prefetchCount
operator|=
literal|10
expr_stmt|;
name|doMultipleClientsTest
argument_list|()
expr_stmt|;
name|assertTotalMessagesReceived
argument_list|(
name|messageCount
operator|*
name|consumerCount
operator|*
name|producerCount
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|testOneProducerManyConsumersManyMessages
parameter_list|()
throws|throws
name|Exception
block|{
name|consumerCount
operator|=
literal|50
expr_stmt|;
name|producerCount
operator|=
literal|1
expr_stmt|;
name|messageCount
operator|=
literal|100
expr_stmt|;
name|messageSize
operator|=
literal|1
expr_stmt|;
comment|// 1 byte
name|prefetchCount
operator|=
literal|10
expr_stmt|;
name|doMultipleClientsTest
argument_list|()
expr_stmt|;
name|assertTotalMessagesReceived
argument_list|(
name|messageCount
operator|*
name|consumerCount
operator|*
name|producerCount
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|testManyProducersOneConsumer
parameter_list|()
throws|throws
name|Exception
block|{
name|consumerCount
operator|=
literal|1
expr_stmt|;
name|producerCount
operator|=
literal|20
expr_stmt|;
name|messageCount
operator|=
literal|100
expr_stmt|;
name|messageSize
operator|=
literal|1
expr_stmt|;
comment|// 1 byte
name|prefetchCount
operator|=
literal|10
expr_stmt|;
name|doMultipleClientsTest
argument_list|()
expr_stmt|;
name|assertTotalMessagesReceived
argument_list|(
name|messageCount
operator|*
name|producerCount
operator|*
name|consumerCount
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|testManyProducersManyConsumers
parameter_list|()
throws|throws
name|Exception
block|{
name|consumerCount
operator|=
literal|20
expr_stmt|;
name|producerCount
operator|=
literal|20
expr_stmt|;
name|messageCount
operator|=
literal|20
expr_stmt|;
name|messageSize
operator|=
literal|1
expr_stmt|;
comment|// 1 byte
name|prefetchCount
operator|=
literal|10
expr_stmt|;
name|doMultipleClientsTest
argument_list|()
expr_stmt|;
name|assertTotalMessagesReceived
argument_list|(
name|messageCount
operator|*
name|producerCount
operator|*
name|consumerCount
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

