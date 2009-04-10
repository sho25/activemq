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
name|usecases
package|;
end_package

begin_import
import|import
name|java
operator|.
name|net
operator|.
name|URI
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Collection
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|HashMap
import|;
end_import

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
name|java
operator|.
name|util
operator|.
name|concurrent
operator|.
name|CountDownLatch
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|concurrent
operator|.
name|TimeUnit
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
name|JmsMultipleBrokersTestSupport
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
name|Queue
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
name|RegionBroker
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
name|apache
operator|.
name|commons
operator|.
name|logging
operator|.
name|Log
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|commons
operator|.
name|logging
operator|.
name|LogFactory
import|;
end_import

begin_comment
comment|/**  * @version $Revision: 1.1.1.1 $  */
end_comment

begin_class
specifier|public
class|class
name|ThreeBrokerQueueNetworkTest
extends|extends
name|JmsMultipleBrokersTestSupport
block|{
specifier|private
specifier|static
specifier|final
name|Log
name|LOG
init|=
name|LogFactory
operator|.
name|getLog
argument_list|(
name|ThreeBrokerQueueNetworkTest
operator|.
name|class
argument_list|)
decl_stmt|;
specifier|protected
specifier|static
specifier|final
name|int
name|MESSAGE_COUNT
init|=
literal|100
decl_stmt|;
comment|/**      * BrokerA -> BrokerB -> BrokerC      */
specifier|public
name|void
name|testABandBCbrokerNetwork
parameter_list|()
throws|throws
name|Exception
block|{
comment|// Setup broker networks
name|bridgeBrokers
argument_list|(
literal|"BrokerA"
argument_list|,
literal|"BrokerB"
argument_list|)
expr_stmt|;
name|bridgeBrokers
argument_list|(
literal|"BrokerB"
argument_list|,
literal|"BrokerC"
argument_list|)
expr_stmt|;
name|startAllBrokers
argument_list|()
expr_stmt|;
comment|// Setup destination
name|Destination
name|dest
init|=
name|createDestination
argument_list|(
literal|"TEST.FOO"
argument_list|,
literal|false
argument_list|)
decl_stmt|;
comment|// Setup consumers
name|MessageConsumer
name|clientC
init|=
name|createConsumer
argument_list|(
literal|"BrokerC"
argument_list|,
name|dest
argument_list|)
decl_stmt|;
comment|// Send messages
name|sendMessages
argument_list|(
literal|"BrokerA"
argument_list|,
name|dest
argument_list|,
name|MESSAGE_COUNT
argument_list|)
expr_stmt|;
comment|// Let's try to wait for any messages. Should be none.
name|Thread
operator|.
name|sleep
argument_list|(
literal|1000
argument_list|)
expr_stmt|;
comment|// Get message count
name|MessageIdList
name|msgsC
init|=
name|getConsumerMessages
argument_list|(
literal|"BrokerC"
argument_list|,
name|clientC
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|msgsC
operator|.
name|getMessageCount
argument_list|()
argument_list|)
expr_stmt|;
block|}
comment|/**      * BrokerA<- BrokerB -> BrokerC      */
specifier|public
name|void
name|testBAandBCbrokerNetwork
parameter_list|()
throws|throws
name|Exception
block|{
comment|// Setup broker networks
name|bridgeBrokers
argument_list|(
literal|"BrokerB"
argument_list|,
literal|"BrokerA"
argument_list|)
expr_stmt|;
name|bridgeBrokers
argument_list|(
literal|"BrokerB"
argument_list|,
literal|"BrokerC"
argument_list|)
expr_stmt|;
name|startAllBrokers
argument_list|()
expr_stmt|;
comment|// Setup destination
name|Destination
name|dest
init|=
name|createDestination
argument_list|(
literal|"TEST.FOO"
argument_list|,
literal|false
argument_list|)
decl_stmt|;
comment|// Setup consumers
name|MessageConsumer
name|clientA
init|=
name|createConsumer
argument_list|(
literal|"BrokerA"
argument_list|,
name|dest
argument_list|)
decl_stmt|;
name|MessageConsumer
name|clientC
init|=
name|createConsumer
argument_list|(
literal|"BrokerC"
argument_list|,
name|dest
argument_list|)
decl_stmt|;
name|Thread
operator|.
name|sleep
argument_list|(
literal|2000
argument_list|)
expr_stmt|;
comment|//et subscriptions get propagated
comment|// Send messages
name|sendMessages
argument_list|(
literal|"BrokerB"
argument_list|,
name|dest
argument_list|,
name|MESSAGE_COUNT
argument_list|)
expr_stmt|;
comment|// Let's try to wait for any messages.
name|Thread
operator|.
name|sleep
argument_list|(
literal|1000
argument_list|)
expr_stmt|;
comment|// Get message count
name|MessageIdList
name|msgsA
init|=
name|getConsumerMessages
argument_list|(
literal|"BrokerA"
argument_list|,
name|clientA
argument_list|)
decl_stmt|;
name|MessageIdList
name|msgsC
init|=
name|getConsumerMessages
argument_list|(
literal|"BrokerC"
argument_list|,
name|clientC
argument_list|)
decl_stmt|;
comment|// Total received should be 100
name|assertEquals
argument_list|(
name|MESSAGE_COUNT
argument_list|,
name|msgsA
operator|.
name|getMessageCount
argument_list|()
operator|+
name|msgsC
operator|.
name|getMessageCount
argument_list|()
argument_list|)
expr_stmt|;
block|}
comment|/**      * BrokerA<- BrokerB -> BrokerC      */
specifier|public
name|void
name|testBAandBCbrokerNetworkWithSelectorsSendFirst
parameter_list|()
throws|throws
name|Exception
block|{
comment|// Setup broker networks
name|bridgeBrokers
argument_list|(
literal|"BrokerB"
argument_list|,
literal|"BrokerA"
argument_list|,
literal|true
argument_list|,
literal|1
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|bridgeBrokers
argument_list|(
literal|"BrokerB"
argument_list|,
literal|"BrokerC"
argument_list|,
literal|true
argument_list|,
literal|1
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|startAllBrokers
argument_list|()
expr_stmt|;
comment|// Setup destination
name|Destination
name|dest
init|=
name|createDestination
argument_list|(
literal|"TEST.FOO"
argument_list|,
literal|false
argument_list|)
decl_stmt|;
comment|// Send messages for broker A
name|HashMap
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|props
init|=
operator|new
name|HashMap
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
argument_list|()
decl_stmt|;
name|props
operator|.
name|put
argument_list|(
literal|"broker"
argument_list|,
literal|"BROKER_A"
argument_list|)
expr_stmt|;
name|sendMessages
argument_list|(
literal|"BrokerB"
argument_list|,
name|dest
argument_list|,
name|MESSAGE_COUNT
argument_list|,
name|props
argument_list|)
expr_stmt|;
comment|//Send messages for broker C
name|props
operator|.
name|clear
argument_list|()
expr_stmt|;
name|props
operator|.
name|put
argument_list|(
literal|"broker"
argument_list|,
literal|"BROKER_C"
argument_list|)
expr_stmt|;
name|sendMessages
argument_list|(
literal|"BrokerB"
argument_list|,
name|dest
argument_list|,
name|MESSAGE_COUNT
argument_list|,
name|props
argument_list|)
expr_stmt|;
comment|// Setup consumers
name|MessageConsumer
name|clientA
init|=
name|createConsumer
argument_list|(
literal|"BrokerA"
argument_list|,
name|dest
argument_list|,
literal|"broker = 'BROKER_A'"
argument_list|)
decl_stmt|;
name|MessageConsumer
name|clientC
init|=
name|createConsumer
argument_list|(
literal|"BrokerC"
argument_list|,
name|dest
argument_list|,
literal|"broker = 'BROKER_C'"
argument_list|)
decl_stmt|;
name|Thread
operator|.
name|sleep
argument_list|(
literal|2000
argument_list|)
expr_stmt|;
comment|//et subscriptions get propagated
comment|// Let's try to wait for any messages.
comment|//Thread.sleep(1000);
comment|// Get message count
name|MessageIdList
name|msgsA
init|=
name|getConsumerMessages
argument_list|(
literal|"BrokerA"
argument_list|,
name|clientA
argument_list|)
decl_stmt|;
name|MessageIdList
name|msgsC
init|=
name|getConsumerMessages
argument_list|(
literal|"BrokerC"
argument_list|,
name|clientC
argument_list|)
decl_stmt|;
comment|// Total received should be 100
name|assertEquals
argument_list|(
name|MESSAGE_COUNT
argument_list|,
name|msgsA
operator|.
name|getMessageCount
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|MESSAGE_COUNT
argument_list|,
name|msgsC
operator|.
name|getMessageCount
argument_list|()
argument_list|)
expr_stmt|;
block|}
comment|/**      * BrokerA<- BrokerB -> BrokerC      */
specifier|public
name|void
name|testBAandBCbrokerNetworkWithSelectorsSubscribeFirst
parameter_list|()
throws|throws
name|Exception
block|{
comment|// Setup broker networks
name|bridgeBrokers
argument_list|(
literal|"BrokerB"
argument_list|,
literal|"BrokerA"
argument_list|,
literal|true
argument_list|,
literal|1
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|bridgeBrokers
argument_list|(
literal|"BrokerB"
argument_list|,
literal|"BrokerC"
argument_list|,
literal|true
argument_list|,
literal|1
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|startAllBrokers
argument_list|()
expr_stmt|;
comment|// Setup destination
name|Destination
name|dest
init|=
name|createDestination
argument_list|(
literal|"TEST.FOO"
argument_list|,
literal|false
argument_list|)
decl_stmt|;
comment|// Setup consumers
name|MessageConsumer
name|clientA
init|=
name|createConsumer
argument_list|(
literal|"BrokerA"
argument_list|,
name|dest
argument_list|,
literal|"broker = 'BROKER_A'"
argument_list|)
decl_stmt|;
name|MessageConsumer
name|clientC
init|=
name|createConsumer
argument_list|(
literal|"BrokerC"
argument_list|,
name|dest
argument_list|,
literal|"broker = 'BROKER_C'"
argument_list|)
decl_stmt|;
name|Thread
operator|.
name|sleep
argument_list|(
literal|2000
argument_list|)
expr_stmt|;
comment|//et subscriptions get propagated
comment|// Send messages for broker A
name|HashMap
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|props
init|=
operator|new
name|HashMap
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
argument_list|()
decl_stmt|;
name|props
operator|.
name|put
argument_list|(
literal|"broker"
argument_list|,
literal|"BROKER_A"
argument_list|)
expr_stmt|;
name|sendMessages
argument_list|(
literal|"BrokerB"
argument_list|,
name|dest
argument_list|,
name|MESSAGE_COUNT
argument_list|,
name|props
argument_list|)
expr_stmt|;
comment|//Send messages for broker C
name|props
operator|.
name|clear
argument_list|()
expr_stmt|;
name|props
operator|.
name|put
argument_list|(
literal|"broker"
argument_list|,
literal|"BROKER_C"
argument_list|)
expr_stmt|;
name|sendMessages
argument_list|(
literal|"BrokerB"
argument_list|,
name|dest
argument_list|,
name|MESSAGE_COUNT
argument_list|,
name|props
argument_list|)
expr_stmt|;
comment|// Let's try to wait for any messages.
name|Thread
operator|.
name|sleep
argument_list|(
literal|1000
argument_list|)
expr_stmt|;
comment|// Get message count
name|MessageIdList
name|msgsA
init|=
name|getConsumerMessages
argument_list|(
literal|"BrokerA"
argument_list|,
name|clientA
argument_list|)
decl_stmt|;
name|MessageIdList
name|msgsC
init|=
name|getConsumerMessages
argument_list|(
literal|"BrokerC"
argument_list|,
name|clientC
argument_list|)
decl_stmt|;
comment|// Total received should be 100
name|assertEquals
argument_list|(
name|MESSAGE_COUNT
argument_list|,
name|msgsA
operator|.
name|getMessageCount
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|MESSAGE_COUNT
argument_list|,
name|msgsC
operator|.
name|getMessageCount
argument_list|()
argument_list|)
expr_stmt|;
block|}
comment|/**      * BrokerA -> BrokerB<- BrokerC      */
specifier|public
name|void
name|testABandCBbrokerNetwork
parameter_list|()
throws|throws
name|Exception
block|{
comment|// Setup broker networks
name|bridgeBrokers
argument_list|(
literal|"BrokerA"
argument_list|,
literal|"BrokerB"
argument_list|)
expr_stmt|;
name|bridgeBrokers
argument_list|(
literal|"BrokerC"
argument_list|,
literal|"BrokerB"
argument_list|)
expr_stmt|;
name|startAllBrokers
argument_list|()
expr_stmt|;
comment|// Setup destination
name|Destination
name|dest
init|=
name|createDestination
argument_list|(
literal|"TEST.FOO"
argument_list|,
literal|false
argument_list|)
decl_stmt|;
comment|// Setup consumers
name|MessageConsumer
name|clientB
init|=
name|createConsumer
argument_list|(
literal|"BrokerB"
argument_list|,
name|dest
argument_list|)
decl_stmt|;
comment|// Send messages
name|sendMessages
argument_list|(
literal|"BrokerA"
argument_list|,
name|dest
argument_list|,
name|MESSAGE_COUNT
argument_list|)
expr_stmt|;
name|sendMessages
argument_list|(
literal|"BrokerC"
argument_list|,
name|dest
argument_list|,
name|MESSAGE_COUNT
argument_list|)
expr_stmt|;
comment|// Get message count
name|MessageIdList
name|msgsB
init|=
name|getConsumerMessages
argument_list|(
literal|"BrokerB"
argument_list|,
name|clientB
argument_list|)
decl_stmt|;
name|msgsB
operator|.
name|waitForMessagesToArrive
argument_list|(
name|MESSAGE_COUNT
operator|*
literal|2
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|MESSAGE_COUNT
operator|*
literal|2
argument_list|,
name|msgsB
operator|.
name|getMessageCount
argument_list|()
argument_list|)
expr_stmt|;
block|}
comment|/**      * BrokerA<-> BrokerB<-> BrokerC      */
specifier|public
name|void
name|testAllConnectedBrokerNetwork
parameter_list|()
throws|throws
name|Exception
block|{
comment|// Setup broker networks
name|bridgeBrokers
argument_list|(
literal|"BrokerA"
argument_list|,
literal|"BrokerB"
argument_list|)
expr_stmt|;
name|bridgeBrokers
argument_list|(
literal|"BrokerB"
argument_list|,
literal|"BrokerA"
argument_list|)
expr_stmt|;
name|bridgeBrokers
argument_list|(
literal|"BrokerB"
argument_list|,
literal|"BrokerC"
argument_list|)
expr_stmt|;
name|bridgeBrokers
argument_list|(
literal|"BrokerC"
argument_list|,
literal|"BrokerB"
argument_list|)
expr_stmt|;
name|bridgeBrokers
argument_list|(
literal|"BrokerA"
argument_list|,
literal|"BrokerC"
argument_list|)
expr_stmt|;
name|bridgeBrokers
argument_list|(
literal|"BrokerC"
argument_list|,
literal|"BrokerA"
argument_list|)
expr_stmt|;
name|startAllBrokers
argument_list|()
expr_stmt|;
comment|// Setup destination
name|Destination
name|dest
init|=
name|createDestination
argument_list|(
literal|"TEST.FOO"
argument_list|,
literal|false
argument_list|)
decl_stmt|;
comment|// Setup consumers
name|MessageConsumer
name|clientA
init|=
name|createConsumer
argument_list|(
literal|"BrokerA"
argument_list|,
name|dest
argument_list|)
decl_stmt|;
name|MessageConsumer
name|clientB
init|=
name|createConsumer
argument_list|(
literal|"BrokerB"
argument_list|,
name|dest
argument_list|)
decl_stmt|;
name|MessageConsumer
name|clientC
init|=
name|createConsumer
argument_list|(
literal|"BrokerC"
argument_list|,
name|dest
argument_list|)
decl_stmt|;
comment|// Send messages
name|sendMessages
argument_list|(
literal|"BrokerA"
argument_list|,
name|dest
argument_list|,
name|MESSAGE_COUNT
argument_list|)
expr_stmt|;
name|sendMessages
argument_list|(
literal|"BrokerB"
argument_list|,
name|dest
argument_list|,
name|MESSAGE_COUNT
argument_list|)
expr_stmt|;
name|sendMessages
argument_list|(
literal|"BrokerC"
argument_list|,
name|dest
argument_list|,
name|MESSAGE_COUNT
argument_list|)
expr_stmt|;
comment|// Let's try to wait for any messages.
name|Thread
operator|.
name|sleep
argument_list|(
literal|1000
argument_list|)
expr_stmt|;
comment|// Get message count
name|MessageIdList
name|msgsA
init|=
name|getConsumerMessages
argument_list|(
literal|"BrokerA"
argument_list|,
name|clientA
argument_list|)
decl_stmt|;
name|MessageIdList
name|msgsB
init|=
name|getConsumerMessages
argument_list|(
literal|"BrokerB"
argument_list|,
name|clientB
argument_list|)
decl_stmt|;
name|MessageIdList
name|msgsC
init|=
name|getConsumerMessages
argument_list|(
literal|"BrokerC"
argument_list|,
name|clientC
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
name|MESSAGE_COUNT
operator|*
literal|3
argument_list|,
name|msgsA
operator|.
name|getMessageCount
argument_list|()
operator|+
name|msgsB
operator|.
name|getMessageCount
argument_list|()
operator|+
name|msgsC
operator|.
name|getMessageCount
argument_list|()
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|testAllConnectedUsingMulticast
parameter_list|()
throws|throws
name|Exception
block|{
comment|// Setup broker networks
name|bridgeAllBrokers
argument_list|()
expr_stmt|;
name|startAllBrokers
argument_list|()
expr_stmt|;
comment|// Setup destination
name|Destination
name|dest
init|=
name|createDestination
argument_list|(
literal|"TEST.FOO"
argument_list|,
literal|false
argument_list|)
decl_stmt|;
comment|// Setup consumers
name|MessageConsumer
name|clientA
init|=
name|createConsumer
argument_list|(
literal|"BrokerA"
argument_list|,
name|dest
argument_list|)
decl_stmt|;
name|MessageConsumer
name|clientB
init|=
name|createConsumer
argument_list|(
literal|"BrokerB"
argument_list|,
name|dest
argument_list|)
decl_stmt|;
name|MessageConsumer
name|clientC
init|=
name|createConsumer
argument_list|(
literal|"BrokerC"
argument_list|,
name|dest
argument_list|)
decl_stmt|;
comment|// Send messages
name|sendMessages
argument_list|(
literal|"BrokerA"
argument_list|,
name|dest
argument_list|,
name|MESSAGE_COUNT
argument_list|)
expr_stmt|;
name|sendMessages
argument_list|(
literal|"BrokerB"
argument_list|,
name|dest
argument_list|,
name|MESSAGE_COUNT
argument_list|)
expr_stmt|;
name|sendMessages
argument_list|(
literal|"BrokerC"
argument_list|,
name|dest
argument_list|,
name|MESSAGE_COUNT
argument_list|)
expr_stmt|;
comment|// Let's try to wait for any messages.
name|Thread
operator|.
name|sleep
argument_list|(
literal|1000
argument_list|)
expr_stmt|;
comment|// Get message count
name|MessageIdList
name|msgsA
init|=
name|getConsumerMessages
argument_list|(
literal|"BrokerA"
argument_list|,
name|clientA
argument_list|)
decl_stmt|;
name|MessageIdList
name|msgsB
init|=
name|getConsumerMessages
argument_list|(
literal|"BrokerB"
argument_list|,
name|clientB
argument_list|)
decl_stmt|;
name|MessageIdList
name|msgsC
init|=
name|getConsumerMessages
argument_list|(
literal|"BrokerC"
argument_list|,
name|clientC
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
name|MESSAGE_COUNT
operator|*
literal|3
argument_list|,
name|msgsA
operator|.
name|getMessageCount
argument_list|()
operator|+
name|msgsB
operator|.
name|getMessageCount
argument_list|()
operator|+
name|msgsC
operator|.
name|getMessageCount
argument_list|()
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|testAllConnectedUsingMulticastProducerConsumerOnA
parameter_list|()
throws|throws
name|Exception
block|{
name|bridgeAllBrokers
argument_list|(
literal|"default"
argument_list|,
literal|3
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|startAllBrokers
argument_list|()
expr_stmt|;
comment|// Setup destination
name|Destination
name|dest
init|=
name|createDestination
argument_list|(
literal|"TEST.FOO"
argument_list|,
literal|false
argument_list|)
decl_stmt|;
comment|// Setup consumers
name|int
name|messageCount
init|=
literal|2000
decl_stmt|;
name|CountDownLatch
name|messagesReceived
init|=
operator|new
name|CountDownLatch
argument_list|(
name|messageCount
argument_list|)
decl_stmt|;
name|MessageConsumer
name|clientA
init|=
name|createConsumer
argument_list|(
literal|"BrokerA"
argument_list|,
name|dest
argument_list|,
name|messagesReceived
argument_list|)
decl_stmt|;
comment|// Let's try to wait for advisory percolation.
name|Thread
operator|.
name|sleep
argument_list|(
literal|1000
argument_list|)
expr_stmt|;
comment|// Send messages
name|sendMessages
argument_list|(
literal|"BrokerA"
argument_list|,
name|dest
argument_list|,
name|messageCount
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|messagesReceived
operator|.
name|await
argument_list|(
literal|30
argument_list|,
name|TimeUnit
operator|.
name|SECONDS
argument_list|)
argument_list|)
expr_stmt|;
comment|// Get message count
name|MessageIdList
name|msgsA
init|=
name|getConsumerMessages
argument_list|(
literal|"BrokerA"
argument_list|,
name|clientA
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
name|messageCount
argument_list|,
name|msgsA
operator|.
name|getMessageCount
argument_list|()
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|testAllConnectedWithSpare
parameter_list|()
throws|throws
name|Exception
block|{
name|bridgeAllBrokers
argument_list|(
literal|"default"
argument_list|,
literal|3
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|startAllBrokers
argument_list|()
expr_stmt|;
comment|// Setup destination
name|Destination
name|dest
init|=
name|createDestination
argument_list|(
literal|"TEST.FOO"
argument_list|,
literal|false
argument_list|)
decl_stmt|;
comment|// Setup consumers
name|int
name|messageCount
init|=
literal|2000
decl_stmt|;
name|CountDownLatch
name|messagesReceived
init|=
operator|new
name|CountDownLatch
argument_list|(
name|messageCount
argument_list|)
decl_stmt|;
name|MessageConsumer
name|clientA
init|=
name|createConsumer
argument_list|(
literal|"BrokerA"
argument_list|,
name|dest
argument_list|,
name|messagesReceived
argument_list|)
decl_stmt|;
comment|// ensure advisory percolation.
name|Thread
operator|.
name|sleep
argument_list|(
literal|1000
argument_list|)
expr_stmt|;
comment|// Send messages
name|sendMessages
argument_list|(
literal|"BrokerB"
argument_list|,
name|dest
argument_list|,
name|messageCount
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|messagesReceived
operator|.
name|await
argument_list|(
literal|30
argument_list|,
name|TimeUnit
operator|.
name|SECONDS
argument_list|)
argument_list|)
expr_stmt|;
comment|// Get message count
name|MessageIdList
name|msgsA
init|=
name|getConsumerMessages
argument_list|(
literal|"BrokerA"
argument_list|,
name|clientA
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
name|messageCount
argument_list|,
name|msgsA
operator|.
name|getMessageCount
argument_list|()
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|testMigrateConsumerStuckMessages
parameter_list|()
throws|throws
name|Exception
block|{
name|boolean
name|suppressQueueDuplicateSubscriptions
init|=
literal|false
decl_stmt|;
name|bridgeAllBrokers
argument_list|(
literal|"default"
argument_list|,
literal|3
argument_list|,
name|suppressQueueDuplicateSubscriptions
argument_list|)
expr_stmt|;
name|startAllBrokers
argument_list|()
expr_stmt|;
comment|// Setup destination
name|Destination
name|dest
init|=
name|createDestination
argument_list|(
literal|"TEST.FOO"
argument_list|,
literal|false
argument_list|)
decl_stmt|;
comment|// Setup consumers
name|LOG
operator|.
name|info
argument_list|(
literal|"Consumer on A"
argument_list|)
expr_stmt|;
name|MessageConsumer
name|clientA
init|=
name|createConsumer
argument_list|(
literal|"BrokerA"
argument_list|,
name|dest
argument_list|)
decl_stmt|;
comment|// ensure advisors have percolated
name|Thread
operator|.
name|sleep
argument_list|(
literal|500
argument_list|)
expr_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"Consumer on B"
argument_list|)
expr_stmt|;
name|int
name|messageCount
init|=
literal|2000
decl_stmt|;
comment|// will only get half of the messages
name|CountDownLatch
name|messagesReceived
init|=
operator|new
name|CountDownLatch
argument_list|(
name|messageCount
operator|/
literal|2
argument_list|)
decl_stmt|;
name|MessageConsumer
name|clientB
init|=
name|createConsumer
argument_list|(
literal|"BrokerB"
argument_list|,
name|dest
argument_list|,
name|messagesReceived
argument_list|)
decl_stmt|;
comment|// ensure advisors have percolated
name|Thread
operator|.
name|sleep
argument_list|(
literal|500
argument_list|)
expr_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"Close consumer on A"
argument_list|)
expr_stmt|;
name|clientA
operator|.
name|close
argument_list|()
expr_stmt|;
comment|// ensure advisors have percolated
name|Thread
operator|.
name|sleep
argument_list|(
literal|500
argument_list|)
expr_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"Send to B"
argument_list|)
expr_stmt|;
name|sendMessages
argument_list|(
literal|"BrokerB"
argument_list|,
name|dest
argument_list|,
name|messageCount
argument_list|)
expr_stmt|;
comment|// Let's try to wait for any messages.
name|assertTrue
argument_list|(
name|messagesReceived
operator|.
name|await
argument_list|(
literal|30
argument_list|,
name|TimeUnit
operator|.
name|SECONDS
argument_list|)
argument_list|)
expr_stmt|;
comment|// Get message count
name|MessageIdList
name|msgs
init|=
name|getConsumerMessages
argument_list|(
literal|"BrokerB"
argument_list|,
name|clientB
argument_list|)
decl_stmt|;
comment|// see will any more arrive
name|Thread
operator|.
name|sleep
argument_list|(
literal|500
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|messageCount
operator|/
literal|2
argument_list|,
name|msgs
operator|.
name|getMessageCount
argument_list|()
argument_list|)
expr_stmt|;
comment|// pick up the stuck messages
name|messagesReceived
operator|=
operator|new
name|CountDownLatch
argument_list|(
name|messageCount
operator|/
literal|2
argument_list|)
expr_stmt|;
name|clientA
operator|=
name|createConsumer
argument_list|(
literal|"BrokerA"
argument_list|,
name|dest
argument_list|,
name|messagesReceived
argument_list|)
expr_stmt|;
comment|// Let's try to wait for any messages.
name|assertTrue
argument_list|(
name|messagesReceived
operator|.
name|await
argument_list|(
literal|30
argument_list|,
name|TimeUnit
operator|.
name|SECONDS
argument_list|)
argument_list|)
expr_stmt|;
name|msgs
operator|=
name|getConsumerMessages
argument_list|(
literal|"BrokerA"
argument_list|,
name|clientA
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|messageCount
operator|/
literal|2
argument_list|,
name|msgs
operator|.
name|getMessageCount
argument_list|()
argument_list|)
expr_stmt|;
block|}
comment|// use case: for maintenance, migrate consumers and producers from one
comment|// node in the network to another so node can be replaced/updated
specifier|public
name|void
name|testMigrateConsumer
parameter_list|()
throws|throws
name|Exception
block|{
name|boolean
name|suppressQueueDuplicateSubscriptions
init|=
literal|true
decl_stmt|;
name|boolean
name|decreaseNetworkConsumerPriority
init|=
literal|true
decl_stmt|;
name|bridgeAllBrokers
argument_list|(
literal|"default"
argument_list|,
literal|3
argument_list|,
name|suppressQueueDuplicateSubscriptions
argument_list|,
name|decreaseNetworkConsumerPriority
argument_list|)
expr_stmt|;
name|startAllBrokers
argument_list|()
expr_stmt|;
comment|// Setup destination
name|Destination
name|dest
init|=
name|createDestination
argument_list|(
literal|"TEST.FOO"
argument_list|,
literal|false
argument_list|)
decl_stmt|;
comment|// Setup consumers
name|LOG
operator|.
name|info
argument_list|(
literal|"Consumer on A"
argument_list|)
expr_stmt|;
name|MessageConsumer
name|clientA
init|=
name|createConsumer
argument_list|(
literal|"BrokerA"
argument_list|,
name|dest
argument_list|)
decl_stmt|;
comment|// ensure advisors have percolated
name|Thread
operator|.
name|sleep
argument_list|(
literal|500
argument_list|)
expr_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"Consumer on B"
argument_list|)
expr_stmt|;
name|int
name|messageCount
init|=
literal|2000
decl_stmt|;
name|CountDownLatch
name|messagesReceived
init|=
operator|new
name|CountDownLatch
argument_list|(
name|messageCount
argument_list|)
decl_stmt|;
name|MessageConsumer
name|clientB
init|=
name|createConsumer
argument_list|(
literal|"BrokerB"
argument_list|,
name|dest
argument_list|,
name|messagesReceived
argument_list|)
decl_stmt|;
comment|// make the consumer slow so that any network consumer has a chance, even
comment|// if it has a lower priority
name|MessageIdList
name|msgs
init|=
name|getConsumerMessages
argument_list|(
literal|"BrokerB"
argument_list|,
name|clientB
argument_list|)
decl_stmt|;
name|msgs
operator|.
name|setProcessingDelay
argument_list|(
literal|10
argument_list|)
expr_stmt|;
comment|// ensure advisors have percolated
name|Thread
operator|.
name|sleep
argument_list|(
literal|500
argument_list|)
expr_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"Close consumer on A"
argument_list|)
expr_stmt|;
name|clientA
operator|.
name|close
argument_list|()
expr_stmt|;
comment|// ensure advisors have percolated
name|Thread
operator|.
name|sleep
argument_list|(
literal|500
argument_list|)
expr_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"Send to B"
argument_list|)
expr_stmt|;
name|sendMessages
argument_list|(
literal|"BrokerB"
argument_list|,
name|dest
argument_list|,
name|messageCount
argument_list|)
expr_stmt|;
comment|// Let's try to wait for any messages.
name|assertTrue
argument_list|(
name|messagesReceived
operator|.
name|await
argument_list|(
literal|30
argument_list|,
name|TimeUnit
operator|.
name|SECONDS
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|messageCount
argument_list|,
name|msgs
operator|.
name|getMessageCount
argument_list|()
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|testNoDuplicateQueueSubs
parameter_list|()
throws|throws
name|Exception
block|{
name|bridgeAllBrokers
argument_list|(
literal|"default"
argument_list|,
literal|3
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|startAllBrokers
argument_list|()
expr_stmt|;
comment|// Setup destination
name|Destination
name|dest
init|=
name|createDestination
argument_list|(
literal|"TEST.FOO"
argument_list|,
literal|false
argument_list|)
decl_stmt|;
comment|// Setup consumers
name|String
name|brokerName
init|=
literal|"BrokerA"
decl_stmt|;
name|createConsumer
argument_list|(
name|brokerName
argument_list|,
name|dest
argument_list|)
expr_stmt|;
comment|// wait for advisories
name|Thread
operator|.
name|sleep
argument_list|(
literal|2000
argument_list|)
expr_stmt|;
comment|// verify there is one consumer on each broker, no cycles
name|Collection
argument_list|<
name|BrokerItem
argument_list|>
name|brokerList
init|=
name|brokers
operator|.
name|values
argument_list|()
decl_stmt|;
for|for
control|(
name|Iterator
argument_list|<
name|BrokerItem
argument_list|>
name|i
init|=
name|brokerList
operator|.
name|iterator
argument_list|()
init|;
name|i
operator|.
name|hasNext
argument_list|()
condition|;
control|)
block|{
name|BrokerService
name|broker
init|=
name|i
operator|.
name|next
argument_list|()
operator|.
name|broker
decl_stmt|;
name|verifyConsumerCount
argument_list|(
name|broker
argument_list|,
literal|1
argument_list|,
name|dest
argument_list|)
expr_stmt|;
block|}
block|}
specifier|public
name|void
name|testDuplicateQueueSubs
parameter_list|()
throws|throws
name|Exception
block|{
name|bridgeAllBrokers
argument_list|(
literal|"default"
argument_list|,
literal|3
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|startAllBrokers
argument_list|()
expr_stmt|;
comment|// Setup destination
name|Destination
name|dest
init|=
name|createDestination
argument_list|(
literal|"TEST.FOO"
argument_list|,
literal|false
argument_list|)
decl_stmt|;
comment|// Setup consumers
name|String
name|brokerName
init|=
literal|"BrokerA"
decl_stmt|;
name|createConsumer
argument_list|(
name|brokerName
argument_list|,
name|dest
argument_list|)
expr_stmt|;
comment|// wait for advisories
name|Thread
operator|.
name|sleep
argument_list|(
literal|2000
argument_list|)
expr_stmt|;
name|verifyConsumerCount
argument_list|(
name|brokers
operator|.
name|get
argument_list|(
name|brokerName
argument_list|)
operator|.
name|broker
argument_list|,
literal|1
argument_list|,
name|dest
argument_list|)
expr_stmt|;
comment|// in a cyclic network, other brokers will get second order consumer
comment|// an alternative route to A via each other
name|Collection
argument_list|<
name|BrokerItem
argument_list|>
name|brokerList
init|=
name|brokers
operator|.
name|values
argument_list|()
decl_stmt|;
for|for
control|(
name|Iterator
argument_list|<
name|BrokerItem
argument_list|>
name|i
init|=
name|brokerList
operator|.
name|iterator
argument_list|()
init|;
name|i
operator|.
name|hasNext
argument_list|()
condition|;
control|)
block|{
name|BrokerService
name|broker
init|=
name|i
operator|.
name|next
argument_list|()
operator|.
name|broker
decl_stmt|;
if|if
condition|(
operator|!
name|brokerName
operator|.
name|equals
argument_list|(
name|broker
operator|.
name|getBrokerName
argument_list|()
argument_list|)
condition|)
block|{
name|verifyConsumerCount
argument_list|(
name|broker
argument_list|,
literal|2
argument_list|,
name|dest
argument_list|)
expr_stmt|;
block|}
block|}
block|}
specifier|private
name|void
name|verifyConsumerCount
parameter_list|(
name|BrokerService
name|broker
parameter_list|,
name|int
name|count
parameter_list|,
name|Destination
name|dest
parameter_list|)
throws|throws
name|Exception
block|{
name|RegionBroker
name|regionBroker
init|=
operator|(
name|RegionBroker
operator|)
name|broker
operator|.
name|getRegionBroker
argument_list|()
decl_stmt|;
name|Queue
name|internalQueue
init|=
operator|(
name|Queue
operator|)
name|regionBroker
operator|.
name|getDestinations
argument_list|(
name|ActiveMQDestination
operator|.
name|transform
argument_list|(
name|dest
argument_list|)
argument_list|)
operator|.
name|iterator
argument_list|()
operator|.
name|next
argument_list|()
decl_stmt|;
name|assertEquals
argument_list|(
literal|"consumer count on "
operator|+
name|broker
operator|.
name|getBrokerName
argument_list|()
operator|+
literal|" matches for q: "
operator|+
name|internalQueue
argument_list|,
name|count
argument_list|,
name|internalQueue
operator|.
name|getConsumers
argument_list|()
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|setUp
parameter_list|()
throws|throws
name|Exception
block|{
name|super
operator|.
name|setAutoFail
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|super
operator|.
name|setUp
argument_list|()
expr_stmt|;
name|createBroker
argument_list|(
operator|new
name|URI
argument_list|(
literal|"broker:(tcp://localhost:61616)/BrokerA?persistent=false&useJmx=false"
argument_list|)
argument_list|)
expr_stmt|;
name|createBroker
argument_list|(
operator|new
name|URI
argument_list|(
literal|"broker:(tcp://localhost:61617)/BrokerB?persistent=false&useJmx=false"
argument_list|)
argument_list|)
expr_stmt|;
name|createBroker
argument_list|(
operator|new
name|URI
argument_list|(
literal|"broker:(tcp://localhost:61618)/BrokerC?persistent=false&useJmx=false"
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

