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
name|BacklogNetworkCrossTalkTest
extends|extends
name|JmsMultipleBrokersTestSupport
block|{
specifier|private
specifier|static
specifier|final
name|Logger
name|LOG
init|=
name|LoggerFactory
operator|.
name|getLogger
argument_list|(
name|BacklogNetworkCrossTalkTest
operator|.
name|class
argument_list|)
decl_stmt|;
specifier|protected
name|BrokerService
name|createBroker
parameter_list|(
name|String
name|brokerName
parameter_list|)
throws|throws
name|Exception
block|{
name|BrokerService
name|broker
init|=
operator|new
name|BrokerService
argument_list|()
decl_stmt|;
name|broker
operator|.
name|setDeleteAllMessagesOnStartup
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|broker
operator|.
name|setPersistent
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|broker
operator|.
name|setUseJmx
argument_list|(
literal|false
argument_list|)
expr_stmt|;
name|broker
operator|.
name|setBrokerName
argument_list|(
name|brokerName
argument_list|)
expr_stmt|;
name|broker
operator|.
name|addConnector
argument_list|(
operator|new
name|URI
argument_list|(
name|AUTO_ASSIGN_TRANSPORT
argument_list|)
argument_list|)
expr_stmt|;
name|brokers
operator|.
name|put
argument_list|(
name|brokerName
argument_list|,
operator|new
name|BrokerItem
argument_list|(
name|broker
argument_list|)
argument_list|)
expr_stmt|;
return|return
name|broker
return|;
block|}
specifier|public
name|void
name|testProduceConsume
parameter_list|()
throws|throws
name|Exception
block|{
name|createBroker
argument_list|(
literal|"A"
argument_list|)
expr_stmt|;
name|createBroker
argument_list|(
literal|"B"
argument_list|)
expr_stmt|;
name|NetworkConnector
name|nc
init|=
name|bridgeBrokers
argument_list|(
literal|"A"
argument_list|,
literal|"B"
argument_list|)
decl_stmt|;
name|nc
operator|.
name|setDuplex
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|nc
operator|.
name|setDispatchAsync
argument_list|(
literal|false
argument_list|)
expr_stmt|;
name|startAllBrokers
argument_list|()
expr_stmt|;
name|waitForBridgeFormation
argument_list|()
expr_stmt|;
specifier|final
name|int
name|numMessages
init|=
literal|10000
decl_stmt|;
comment|// Create queue
name|ActiveMQDestination
name|destA
init|=
name|createDestination
argument_list|(
literal|"AAA"
argument_list|,
literal|false
argument_list|)
decl_stmt|;
name|sendMessages
argument_list|(
literal|"A"
argument_list|,
name|destA
argument_list|,
name|numMessages
argument_list|)
expr_stmt|;
name|ActiveMQDestination
name|destB
init|=
name|createDestination
argument_list|(
literal|"BBB"
argument_list|,
literal|false
argument_list|)
decl_stmt|;
name|sendMessages
argument_list|(
literal|"B"
argument_list|,
name|destB
argument_list|,
name|numMessages
argument_list|)
expr_stmt|;
comment|// consume across network
name|LOG
operator|.
name|info
argument_list|(
literal|"starting consumers.."
argument_list|)
expr_stmt|;
comment|// Setup consumers
name|MessageConsumer
name|clientA
init|=
name|createConsumer
argument_list|(
literal|"A"
argument_list|,
name|destB
argument_list|)
decl_stmt|;
comment|// Setup consumers
name|MessageConsumer
name|clientB
init|=
name|createConsumer
argument_list|(
literal|"B"
argument_list|,
name|destA
argument_list|)
decl_stmt|;
specifier|final
name|long
name|maxWait
init|=
literal|5
operator|*
literal|60
operator|*
literal|1000l
decl_stmt|;
name|MessageIdList
name|listA
init|=
name|getConsumerMessages
argument_list|(
literal|"A"
argument_list|,
name|clientA
argument_list|)
decl_stmt|;
name|listA
operator|.
name|setMaximumDuration
argument_list|(
name|maxWait
argument_list|)
expr_stmt|;
name|listA
operator|.
name|waitForMessagesToArrive
argument_list|(
name|numMessages
argument_list|)
expr_stmt|;
name|MessageIdList
name|listB
init|=
name|getConsumerMessages
argument_list|(
literal|"B"
argument_list|,
name|clientB
argument_list|)
decl_stmt|;
name|listB
operator|.
name|setMaximumDuration
argument_list|(
name|maxWait
argument_list|)
expr_stmt|;
name|listB
operator|.
name|waitForMessagesToArrive
argument_list|(
name|numMessages
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"got all on A"
operator|+
name|listA
operator|.
name|getMessageCount
argument_list|()
argument_list|,
name|numMessages
argument_list|,
name|listA
operator|.
name|getMessageCount
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"got all on B"
operator|+
name|listB
operator|.
name|getMessageCount
argument_list|()
argument_list|,
name|numMessages
argument_list|,
name|listB
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
name|messageSize
operator|=
literal|5000
expr_stmt|;
name|super
operator|.
name|setMaxTestTime
argument_list|(
literal|10
operator|*
literal|60
operator|*
literal|1000
argument_list|)
expr_stmt|;
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
block|}
block|}
end_class

end_unit

