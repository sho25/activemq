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
name|network
operator|.
name|NetworkConnector
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
name|javax
operator|.
name|jms
operator|.
name|MessageConsumer
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

begin_class
specifier|public
class|class
name|StaticNetworkTest
extends|extends
name|JmsMultipleBrokersTestSupport
block|{
specifier|public
name|void
name|testStaticNetwork
parameter_list|()
throws|throws
name|Exception
block|{
comment|// Setup destination
name|ActiveMQDestination
name|dest
init|=
name|createDestination
argument_list|(
literal|"TEST"
argument_list|,
literal|false
argument_list|)
decl_stmt|;
name|ActiveMQDestination
name|dest1
init|=
name|createDestination
argument_list|(
literal|"TEST1"
argument_list|,
literal|false
argument_list|)
decl_stmt|;
name|NetworkConnector
name|bridgeAB
init|=
name|bridgeBrokers
argument_list|(
literal|"BrokerA"
argument_list|,
literal|"BrokerB"
argument_list|,
literal|true
argument_list|)
decl_stmt|;
name|bridgeAB
operator|.
name|addStaticallyIncludedDestination
argument_list|(
name|dest
argument_list|)
expr_stmt|;
name|bridgeAB
operator|.
name|setStaticBridge
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|startAllBrokers
argument_list|()
expr_stmt|;
name|waitForBridgeFormation
argument_list|()
expr_stmt|;
name|MessageConsumer
name|consumer1
init|=
name|createConsumer
argument_list|(
literal|"BrokerB"
argument_list|,
name|dest
argument_list|)
decl_stmt|;
name|MessageConsumer
name|consumer2
init|=
name|createConsumer
argument_list|(
literal|"BrokerB"
argument_list|,
name|dest1
argument_list|)
decl_stmt|;
name|Thread
operator|.
name|sleep
argument_list|(
literal|1000
argument_list|)
expr_stmt|;
name|sendMessages
argument_list|(
literal|"BrokerA"
argument_list|,
name|dest
argument_list|,
literal|1
argument_list|)
expr_stmt|;
name|sendMessages
argument_list|(
literal|"BrokerA"
argument_list|,
name|dest1
argument_list|,
literal|1
argument_list|)
expr_stmt|;
name|MessageIdList
name|msgs1
init|=
name|getConsumerMessages
argument_list|(
literal|"BrokerB"
argument_list|,
name|consumer1
argument_list|)
decl_stmt|;
name|MessageIdList
name|msgs2
init|=
name|getConsumerMessages
argument_list|(
literal|"BrokerB"
argument_list|,
name|consumer2
argument_list|)
decl_stmt|;
name|msgs1
operator|.
name|waitForMessagesToArrive
argument_list|(
literal|1
argument_list|)
expr_stmt|;
name|Thread
operator|.
name|sleep
argument_list|(
literal|1000
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|msgs1
operator|.
name|getMessageCount
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|msgs2
operator|.
name|getMessageCount
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
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
block|}
block|}
end_class

end_unit
