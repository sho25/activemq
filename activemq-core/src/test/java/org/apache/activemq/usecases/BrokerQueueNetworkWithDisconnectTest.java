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
name|List
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
name|javax
operator|.
name|jms
operator|.
name|TextMessage
import|;
end_import

begin_import
import|import
name|junit
operator|.
name|framework
operator|.
name|Test
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
name|JmsMultipleBrokersTestSupport
operator|.
name|BrokerItem
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
name|TransportConnector
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
name|DiscoveryNetworkConnector
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
name|apache
operator|.
name|activemq
operator|.
name|util
operator|.
name|SocketProxy
import|;
end_import

begin_class
specifier|public
class|class
name|BrokerQueueNetworkWithDisconnectTest
extends|extends
name|JmsMultipleBrokersTestSupport
block|{
specifier|private
specifier|static
specifier|final
name|int
name|NETWORK_DOWN_TIME
init|=
literal|5000
decl_stmt|;
specifier|protected
specifier|static
specifier|final
name|int
name|MESSAGE_COUNT
init|=
literal|200
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|String
name|HUB
init|=
literal|"HubBroker"
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|String
name|SPOKE
init|=
literal|"SpokeBroker"
decl_stmt|;
specifier|private
name|SocketProxy
name|socketProxy
decl_stmt|;
specifier|private
name|long
name|networkDownTimeStart
decl_stmt|;
specifier|public
name|boolean
name|useDuplexNetworkBridge
decl_stmt|;
specifier|public
name|boolean
name|sumulateStalledNetwork
decl_stmt|;
specifier|public
name|void
name|initCombosForTestSendOnAReceiveOnBWithTransportDisconnect
parameter_list|()
block|{
name|addCombinationValues
argument_list|(
literal|"useDuplexNetworkBridge"
argument_list|,
operator|new
name|Object
index|[]
block|{
name|Boolean
operator|.
name|TRUE
block|,
name|Boolean
operator|.
name|FALSE
block|}
argument_list|)
expr_stmt|;
name|addCombinationValues
argument_list|(
literal|"sumulateStalledNetwork"
argument_list|,
operator|new
name|Object
index|[]
block|{
name|Boolean
operator|.
name|TRUE
block|}
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|testSendOnAReceiveOnBWithTransportDisconnect
parameter_list|()
throws|throws
name|Exception
block|{
name|bridgeBrokers
argument_list|(
name|SPOKE
argument_list|,
name|HUB
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
name|client
init|=
name|createConsumer
argument_list|(
name|HUB
argument_list|,
name|dest
argument_list|)
decl_stmt|;
comment|// allow subscription information to flow back to Spoke
name|sleep
argument_list|(
literal|600
argument_list|)
expr_stmt|;
comment|// Send messages
name|sendMessages
argument_list|(
name|SPOKE
argument_list|,
name|dest
argument_list|,
name|MESSAGE_COUNT
argument_list|)
expr_stmt|;
name|MessageIdList
name|msgs
init|=
name|getConsumerMessages
argument_list|(
name|HUB
argument_list|,
name|client
argument_list|)
decl_stmt|;
name|msgs
operator|.
name|waitForMessagesToArrive
argument_list|(
name|MESSAGE_COUNT
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
literal|"At least message "
operator|+
name|MESSAGE_COUNT
operator|+
literal|" must be recieved, duplicates are expected, count="
operator|+
name|msgs
operator|.
name|getMessageCount
argument_list|()
argument_list|,
name|MESSAGE_COUNT
operator|<=
name|msgs
operator|.
name|getMessageCount
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|protected
name|void
name|startAllBrokers
parameter_list|()
throws|throws
name|Exception
block|{
comment|// Ensure HUB is started first so bridge will be active from the get go
name|BrokerItem
name|brokerItem
init|=
name|brokers
operator|.
name|get
argument_list|(
name|HUB
argument_list|)
decl_stmt|;
name|brokerItem
operator|.
name|broker
operator|.
name|start
argument_list|()
expr_stmt|;
name|brokerItem
operator|=
name|brokers
operator|.
name|get
argument_list|(
name|SPOKE
argument_list|)
expr_stmt|;
name|brokerItem
operator|.
name|broker
operator|.
name|start
argument_list|()
expr_stmt|;
name|sleep
argument_list|(
literal|600
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
name|networkDownTimeStart
operator|=
literal|0
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
specifier|final
name|String
name|options
init|=
literal|"?persistent=true&useJmx=false&deleteAllMessagesOnStartup=true"
decl_stmt|;
name|createBroker
argument_list|(
operator|new
name|URI
argument_list|(
literal|"broker:(tcp://localhost:61617)/"
operator|+
name|HUB
operator|+
name|options
argument_list|)
argument_list|)
expr_stmt|;
name|createBroker
argument_list|(
operator|new
name|URI
argument_list|(
literal|"broker:(tcp://localhost:61616)/"
operator|+
name|SPOKE
operator|+
name|options
argument_list|)
argument_list|)
expr_stmt|;
block|}
specifier|public
specifier|static
name|Test
name|suite
parameter_list|()
block|{
return|return
name|suite
argument_list|(
name|BrokerQueueNetworkWithDisconnectTest
operator|.
name|class
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|protected
name|void
name|onSend
parameter_list|(
name|int
name|i
parameter_list|,
name|TextMessage
name|msg
parameter_list|)
block|{
name|sleep
argument_list|(
literal|50
argument_list|)
expr_stmt|;
if|if
condition|(
name|i
operator|==
literal|50
operator|||
name|i
operator|==
literal|150
condition|)
block|{
if|if
condition|(
name|sumulateStalledNetwork
condition|)
block|{
name|socketProxy
operator|.
name|pause
argument_list|()
expr_stmt|;
block|}
else|else
block|{
name|socketProxy
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
name|networkDownTimeStart
operator|=
name|System
operator|.
name|currentTimeMillis
argument_list|()
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|networkDownTimeStart
operator|>
literal|0
condition|)
block|{
comment|// restart after NETWORK_DOWN_TIME seconds
if|if
condition|(
name|networkDownTimeStart
operator|+
name|NETWORK_DOWN_TIME
operator|<
name|System
operator|.
name|currentTimeMillis
argument_list|()
condition|)
block|{
if|if
condition|(
name|sumulateStalledNetwork
condition|)
block|{
name|socketProxy
operator|.
name|goOn
argument_list|()
expr_stmt|;
block|}
else|else
block|{
name|socketProxy
operator|.
name|reopen
argument_list|()
expr_stmt|;
block|}
name|networkDownTimeStart
operator|=
literal|0
expr_stmt|;
block|}
else|else
block|{
comment|// slow message production to allow bridge to recover and limit message duplication
name|sleep
argument_list|(
literal|500
argument_list|)
expr_stmt|;
block|}
block|}
name|super
operator|.
name|onSend
argument_list|(
name|i
argument_list|,
name|msg
argument_list|)
expr_stmt|;
block|}
specifier|private
name|void
name|sleep
parameter_list|(
name|int
name|milliSecondTime
parameter_list|)
block|{
try|try
block|{
name|Thread
operator|.
name|sleep
argument_list|(
name|milliSecondTime
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|InterruptedException
name|igonred
parameter_list|)
block|{         }
block|}
annotation|@
name|Override
specifier|protected
name|NetworkConnector
name|bridgeBrokers
parameter_list|(
name|BrokerService
name|localBroker
parameter_list|,
name|BrokerService
name|remoteBroker
parameter_list|,
name|boolean
name|dynamicOnly
parameter_list|,
name|int
name|networkTTL
parameter_list|)
throws|throws
name|Exception
block|{
name|List
argument_list|<
name|TransportConnector
argument_list|>
name|transportConnectors
init|=
name|remoteBroker
operator|.
name|getTransportConnectors
argument_list|()
decl_stmt|;
name|URI
name|remoteURI
decl_stmt|;
if|if
condition|(
operator|!
name|transportConnectors
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
name|remoteURI
operator|=
operator|(
operator|(
name|TransportConnector
operator|)
name|transportConnectors
operator|.
name|get
argument_list|(
literal|0
argument_list|)
operator|)
operator|.
name|getConnectUri
argument_list|()
expr_stmt|;
name|socketProxy
operator|=
operator|new
name|SocketProxy
argument_list|(
name|remoteURI
argument_list|)
expr_stmt|;
name|DiscoveryNetworkConnector
name|connector
init|=
operator|new
name|DiscoveryNetworkConnector
argument_list|(
operator|new
name|URI
argument_list|(
literal|"static:("
operator|+
name|socketProxy
operator|.
name|getUrl
argument_list|()
operator|+
literal|"?wireFormat.maxInactivityDuration=1000&wireFormat.maxInactivityDurationInitalDelay=1000)?useExponentialBackOff=false"
argument_list|)
argument_list|)
decl_stmt|;
name|connector
operator|.
name|setDynamicOnly
argument_list|(
name|dynamicOnly
argument_list|)
expr_stmt|;
name|connector
operator|.
name|setNetworkTTL
argument_list|(
name|networkTTL
argument_list|)
expr_stmt|;
name|localBroker
operator|.
name|addNetworkConnector
argument_list|(
name|connector
argument_list|)
expr_stmt|;
name|maxSetupTime
operator|=
literal|2000
expr_stmt|;
if|if
condition|(
name|useDuplexNetworkBridge
condition|)
block|{
name|connector
operator|.
name|setDuplex
argument_list|(
literal|true
argument_list|)
expr_stmt|;
block|}
return|return
name|connector
return|;
block|}
else|else
block|{
throw|throw
operator|new
name|Exception
argument_list|(
literal|"Remote broker has no registered connectors."
argument_list|)
throw|;
block|}
block|}
block|}
end_class

end_unit

