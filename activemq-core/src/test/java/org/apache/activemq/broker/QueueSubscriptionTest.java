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
name|ActiveMQConnectionFactory
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
name|JmsMultipleClientsTestSupport
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
name|command
operator|.
name|ActiveMQDestination
import|;
end_import

begin_class
specifier|public
class|class
name|QueueSubscriptionTest
extends|extends
name|JmsMultipleClientsTestSupport
block|{
specifier|protected
name|int
name|messageCount
init|=
literal|1000
decl_stmt|;
comment|// 1000 Messages per producer
specifier|protected
name|int
name|prefetchCount
init|=
literal|10
decl_stmt|;
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
literal|false
expr_stmt|;
name|topic
operator|=
literal|false
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
literal|10
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
name|messageCount
operator|=
literal|1000
expr_stmt|;
name|messageSize
operator|=
literal|1024
expr_stmt|;
comment|// 1 Kb
name|configurePrefetchOfOne
argument_list|()
expr_stmt|;
name|doMultipleClientsTest
argument_list|()
expr_stmt|;
name|assertTotalMessagesReceived
argument_list|(
name|messageCount
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
name|prefetchCount
operator|=
name|messageCount
operator|*
literal|2
expr_stmt|;
name|messageSize
operator|=
literal|1024
expr_stmt|;
comment|// 1 Kb
name|doMultipleClientsTest
argument_list|()
expr_stmt|;
name|assertTotalMessagesReceived
argument_list|(
name|messageCount
operator|*
name|producerCount
argument_list|)
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
comment|// 2 MB
name|configurePrefetchOfOne
argument_list|()
expr_stmt|;
name|doMultipleClientsTest
argument_list|()
expr_stmt|;
name|assertTotalMessagesReceived
argument_list|(
name|messageCount
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
name|prefetchCount
operator|=
name|messageCount
operator|*
literal|2
expr_stmt|;
name|messageSize
operator|=
literal|1024
operator|*
literal|1024
operator|*
literal|1
expr_stmt|;
comment|// 2 MB
name|doMultipleClientsTest
argument_list|()
expr_stmt|;
name|assertTotalMessagesReceived
argument_list|(
name|messageCount
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
literal|1000
expr_stmt|;
name|messageSize
operator|=
literal|1
expr_stmt|;
comment|// 1 byte
name|prefetchCount
operator|=
name|messageCount
operator|/
name|consumerCount
expr_stmt|;
name|allMessagesList
operator|.
name|setMaximumDuration
argument_list|(
name|allMessagesList
operator|.
name|getMaximumDuration
argument_list|()
operator|*
literal|20
argument_list|)
expr_stmt|;
name|doMultipleClientsTest
argument_list|()
expr_stmt|;
name|assertTotalMessagesReceived
argument_list|(
name|messageCount
operator|*
name|producerCount
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
literal|50
expr_stmt|;
name|producerCount
operator|=
literal|50
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
literal|100
expr_stmt|;
name|allMessagesList
operator|.
name|setMaximumDuration
argument_list|(
name|allMessagesList
operator|.
name|getMaximumDuration
argument_list|()
operator|*
literal|20
argument_list|)
expr_stmt|;
name|doMultipleClientsTest
argument_list|()
expr_stmt|;
name|assertTotalMessagesReceived
argument_list|(
name|messageCount
operator|*
name|producerCount
argument_list|)
expr_stmt|;
block|}
specifier|protected
name|void
name|configurePrefetchOfOne
parameter_list|()
block|{
name|prefetchCount
operator|=
literal|1
expr_stmt|;
comment|// this is gonna be a bit slow what with the low prefetch so bump up the
comment|// wait time
name|allMessagesList
operator|.
name|setMaximumDuration
argument_list|(
name|allMessagesList
operator|.
name|getMaximumDuration
argument_list|()
operator|*
literal|20
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|doMultipleClientsTest
parameter_list|()
throws|throws
name|Exception
block|{
comment|// Create destination
specifier|final
name|ActiveMQDestination
name|dest
init|=
name|createDestination
argument_list|()
decl_stmt|;
comment|// Create consumers
name|ActiveMQConnectionFactory
name|consumerFactory
init|=
operator|(
name|ActiveMQConnectionFactory
operator|)
name|createConnectionFactory
argument_list|()
decl_stmt|;
name|consumerFactory
operator|.
name|getPrefetchPolicy
argument_list|()
operator|.
name|setAll
argument_list|(
name|prefetchCount
argument_list|)
expr_stmt|;
name|startConsumers
argument_list|(
name|consumerFactory
argument_list|,
name|dest
argument_list|)
expr_stmt|;
name|startProducers
argument_list|(
name|dest
argument_list|,
name|messageCount
argument_list|)
expr_stmt|;
comment|// Wait for messages to be received. Make it proportional to the
comment|// messages delivered.
name|int
name|totalMessageCount
init|=
name|messageCount
operator|*
name|producerCount
decl_stmt|;
if|if
condition|(
name|dest
operator|.
name|isTopic
argument_list|()
condition|)
block|{
name|totalMessageCount
operator|*=
name|consumerCount
expr_stmt|;
block|}
name|waitForAllMessagesToBeReceived
argument_list|(
name|totalMessageCount
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

