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
name|util
operator|.
name|Wait
import|;
end_import

begin_import
import|import
name|org
operator|.
name|slf4j
operator|.
name|Logger
import|;
end_import

begin_import
import|import
name|org
operator|.
name|slf4j
operator|.
name|LoggerFactory
import|;
end_import

begin_class
specifier|public
class|class
name|TwoBrokerNetworkLoadBalanceTest
extends|extends
name|JmsMultipleBrokersTestSupport
block|{
specifier|protected
specifier|static
specifier|final
name|Logger
name|LOG
init|=
name|LoggerFactory
operator|.
name|getLogger
argument_list|(
name|TwoBrokerNetworkLoadBalanceTest
operator|.
name|class
argument_list|)
decl_stmt|;
specifier|public
name|void
name|testLoadBalancing
parameter_list|()
throws|throws
name|Exception
block|{
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
name|startAllBrokers
argument_list|()
expr_stmt|;
name|waitForBridgeFormation
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
literal|5000
argument_list|)
expr_stmt|;
comment|// Send messages
name|sendMessages
argument_list|(
literal|"BrokerB"
argument_list|,
name|dest
argument_list|,
literal|1000
argument_list|)
expr_stmt|;
comment|// Get message count
specifier|final
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
specifier|final
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
name|Wait
operator|.
name|waitFor
argument_list|(
operator|new
name|Wait
operator|.
name|Condition
argument_list|()
block|{
specifier|public
name|boolean
name|isSatisified
parameter_list|()
throws|throws
name|Exception
block|{
return|return
name|msgsA
operator|.
name|getMessageCount
argument_list|()
operator|+
name|msgsB
operator|.
name|getMessageCount
argument_list|()
operator|==
literal|6000
return|;
block|}
block|}
argument_list|)
expr_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"A got: "
operator|+
name|msgsA
operator|.
name|getMessageCount
argument_list|()
argument_list|)
expr_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"B got: "
operator|+
name|msgsB
operator|.
name|getMessageCount
argument_list|()
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
literal|"B got is fair share: "
operator|+
name|msgsB
operator|.
name|getMessageCount
argument_list|()
argument_list|,
name|msgsB
operator|.
name|getMessageCount
argument_list|()
operator|>
literal|2000
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
block|}
block|}
end_class

end_unit

