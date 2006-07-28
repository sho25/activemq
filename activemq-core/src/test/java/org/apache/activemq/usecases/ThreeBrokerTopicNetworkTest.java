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
name|usecases
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
name|activemq
operator|.
name|JmsMultipleBrokersTestSupport
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
name|javax
operator|.
name|jms
operator|.
name|Destination
import|;
end_import

begin_import
import|import
name|java
operator|.
name|net
operator|.
name|URI
import|;
end_import

begin_comment
comment|/**  * @version $Revision: 1.1.1.1 $  */
end_comment

begin_class
specifier|public
class|class
name|ThreeBrokerTopicNetworkTest
extends|extends
name|JmsMultipleBrokersTestSupport
block|{
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
name|test_AB_BC_BrokerNetwork
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
literal|true
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
comment|//      let consumers propogate around the network
name|Thread
operator|.
name|sleep
argument_list|(
literal|2000
argument_list|)
expr_stmt|;
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
name|msgsA
operator|.
name|waitForMessagesToArrive
argument_list|(
name|MESSAGE_COUNT
argument_list|)
expr_stmt|;
name|msgsB
operator|.
name|waitForMessagesToArrive
argument_list|(
name|MESSAGE_COUNT
operator|*
literal|2
argument_list|)
expr_stmt|;
name|msgsC
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
operator|*
literal|2
argument_list|,
name|msgsB
operator|.
name|getMessageCount
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|MESSAGE_COUNT
operator|*
literal|2
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
name|test_BA_BC_BrokerNetwork
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
literal|true
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
comment|//      let consumers propogate around the network
name|Thread
operator|.
name|sleep
argument_list|(
literal|2000
argument_list|)
expr_stmt|;
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
name|msgsA
operator|.
name|waitForMessagesToArrive
argument_list|(
name|MESSAGE_COUNT
operator|*
literal|2
argument_list|)
expr_stmt|;
name|msgsB
operator|.
name|waitForMessagesToArrive
argument_list|(
name|MESSAGE_COUNT
argument_list|)
expr_stmt|;
name|msgsC
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
name|msgsB
operator|.
name|getMessageCount
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|MESSAGE_COUNT
operator|*
literal|2
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
name|test_AB_CB_BrokerNetwork
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
literal|true
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
comment|//      let consumers propogate around the network
name|Thread
operator|.
name|sleep
argument_list|(
literal|2000
argument_list|)
expr_stmt|;
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
name|msgsA
operator|.
name|waitForMessagesToArrive
argument_list|(
name|MESSAGE_COUNT
argument_list|)
expr_stmt|;
name|msgsB
operator|.
name|waitForMessagesToArrive
argument_list|(
name|MESSAGE_COUNT
operator|*
literal|3
argument_list|)
expr_stmt|;
name|msgsC
operator|.
name|waitForMessagesToArrive
argument_list|(
name|MESSAGE_COUNT
argument_list|)
expr_stmt|;
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
operator|*
literal|3
argument_list|,
name|msgsB
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
literal|true
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
comment|//let consumers propogate around the network
name|Thread
operator|.
name|sleep
argument_list|(
literal|2000
argument_list|)
expr_stmt|;
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
name|msgsA
operator|.
name|waitForMessagesToArrive
argument_list|(
name|MESSAGE_COUNT
operator|*
literal|3
argument_list|)
expr_stmt|;
name|msgsB
operator|.
name|waitForMessagesToArrive
argument_list|(
name|MESSAGE_COUNT
operator|*
literal|3
argument_list|)
expr_stmt|;
name|msgsC
operator|.
name|waitForMessagesToArrive
argument_list|(
name|MESSAGE_COUNT
operator|*
literal|3
argument_list|)
expr_stmt|;
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
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|MESSAGE_COUNT
operator|*
literal|3
argument_list|,
name|msgsB
operator|.
name|getMessageCount
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|MESSAGE_COUNT
operator|*
literal|3
argument_list|,
name|msgsC
operator|.
name|getMessageCount
argument_list|()
argument_list|)
expr_stmt|;
block|}
comment|/**      * BrokerA<-> BrokerB<-> BrokerC      */
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
literal|true
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
comment|//let consumers propogate around the network
name|Thread
operator|.
name|sleep
argument_list|(
literal|2000
argument_list|)
expr_stmt|;
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
name|msgsA
operator|.
name|waitForMessagesToArrive
argument_list|(
name|MESSAGE_COUNT
operator|*
literal|3
argument_list|)
expr_stmt|;
name|msgsB
operator|.
name|waitForMessagesToArrive
argument_list|(
name|MESSAGE_COUNT
operator|*
literal|3
argument_list|)
expr_stmt|;
name|msgsC
operator|.
name|waitForMessagesToArrive
argument_list|(
name|MESSAGE_COUNT
operator|*
literal|3
argument_list|)
expr_stmt|;
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
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|MESSAGE_COUNT
operator|*
literal|3
argument_list|,
name|msgsB
operator|.
name|getMessageCount
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|MESSAGE_COUNT
operator|*
literal|3
argument_list|,
name|msgsC
operator|.
name|getMessageCount
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

