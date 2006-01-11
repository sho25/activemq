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
name|DemandForwardingBridge
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
name|transport
operator|.
name|TransportFactory
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
name|command
operator|.
name|Command
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
name|java
operator|.
name|util
operator|.
name|List
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|ArrayList
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

begin_import
import|import
name|edu
operator|.
name|emory
operator|.
name|mathcs
operator|.
name|backport
operator|.
name|java
operator|.
name|util
operator|.
name|concurrent
operator|.
name|atomic
operator|.
name|AtomicInteger
import|;
end_import

begin_comment
comment|/**  * @version $Revision: 1.1.1.1 $  */
end_comment

begin_class
specifier|public
class|class
name|TwoBrokerMessageNotSentToRemoteWhenNoConsumerTest
extends|extends
name|JmsMultipleBrokersTestSupport
block|{
specifier|protected
specifier|static
specifier|final
name|int
name|MESSAGE_COUNT
init|=
literal|10
decl_stmt|;
specifier|protected
name|List
name|bridges
decl_stmt|;
specifier|protected
name|AtomicInteger
name|msgDispatchCount
decl_stmt|;
comment|/**      * BrokerA -> BrokerB      */
specifier|public
name|void
name|testRemoteBrokerHasConsumer
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
argument_list|,
name|msgsB
operator|.
name|getMessageCount
argument_list|()
argument_list|)
expr_stmt|;
comment|// Check that 10 message dispatch commands are send over the network
name|assertEquals
argument_list|(
name|MESSAGE_COUNT
argument_list|,
name|msgDispatchCount
operator|.
name|get
argument_list|()
argument_list|)
expr_stmt|;
block|}
comment|/**      * BrokerA -> BrokerB      */
specifier|public
name|void
name|testRemoteBrokerHasNoConsumer
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
name|msgsA
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
comment|// Check that no message dispatch commands are send over the network
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|msgDispatchCount
operator|.
name|get
argument_list|()
argument_list|)
expr_stmt|;
block|}
specifier|protected
name|void
name|bridgeBrokers
parameter_list|(
name|BrokerService
name|localBroker
parameter_list|,
name|BrokerService
name|remoteBroker
parameter_list|)
throws|throws
name|Exception
block|{
name|List
name|remoteTransports
init|=
name|remoteBroker
operator|.
name|getTransportConnectors
argument_list|()
decl_stmt|;
name|List
name|localTransports
init|=
name|localBroker
operator|.
name|getTransportConnectors
argument_list|()
decl_stmt|;
name|URI
name|remoteURI
decl_stmt|,
name|localURI
decl_stmt|;
if|if
condition|(
operator|!
name|remoteTransports
operator|.
name|isEmpty
argument_list|()
operator|&&
operator|!
name|localTransports
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
name|remoteTransports
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
name|localURI
operator|=
operator|(
operator|(
name|TransportConnector
operator|)
name|localTransports
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
comment|// Ensure that we are connecting using tcp
if|if
condition|(
name|remoteURI
operator|.
name|toString
argument_list|()
operator|.
name|startsWith
argument_list|(
literal|"tcp:"
argument_list|)
operator|&&
name|localURI
operator|.
name|toString
argument_list|()
operator|.
name|startsWith
argument_list|(
literal|"tcp:"
argument_list|)
condition|)
block|{
name|DemandForwardingBridge
name|bridge
init|=
operator|new
name|DemandForwardingBridge
argument_list|(
name|TransportFactory
operator|.
name|connect
argument_list|(
name|localURI
argument_list|)
argument_list|,
name|TransportFactory
operator|.
name|connect
argument_list|(
name|remoteURI
argument_list|)
argument_list|)
block|{
specifier|protected
name|void
name|serviceLocalCommand
parameter_list|(
name|Command
name|command
parameter_list|)
block|{
if|if
condition|(
name|command
operator|.
name|isMessageDispatch
argument_list|()
condition|)
block|{
comment|// Keep track of the number of message dispatches through the bridge
name|msgDispatchCount
operator|.
name|incrementAndGet
argument_list|()
expr_stmt|;
block|}
name|super
operator|.
name|serviceLocalCommand
argument_list|(
name|command
argument_list|)
expr_stmt|;
block|}
block|}
decl_stmt|;
name|bridge
operator|.
name|setClientId
argument_list|(
name|localBroker
operator|.
name|getBrokerName
argument_list|()
operator|+
literal|"_to_"
operator|+
name|remoteBroker
operator|.
name|getBrokerName
argument_list|()
argument_list|)
expr_stmt|;
name|bridges
operator|.
name|add
argument_list|(
name|bridge
argument_list|)
expr_stmt|;
name|bridge
operator|.
name|start
argument_list|()
expr_stmt|;
block|}
else|else
block|{
throw|throw
operator|new
name|Exception
argument_list|(
literal|"Remote broker or local broker is not using tcp connectors"
argument_list|)
throw|;
block|}
block|}
else|else
block|{
throw|throw
operator|new
name|Exception
argument_list|(
literal|"Remote broker or local broker has no registered connectors."
argument_list|)
throw|;
block|}
name|MAX_SETUP_TIME
operator|=
literal|2000
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
name|bridges
operator|=
operator|new
name|ArrayList
argument_list|()
expr_stmt|;
name|msgDispatchCount
operator|=
operator|new
name|AtomicInteger
argument_list|(
literal|0
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

