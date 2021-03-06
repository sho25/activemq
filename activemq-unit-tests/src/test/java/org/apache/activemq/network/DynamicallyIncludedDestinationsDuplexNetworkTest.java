begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  * http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
package|package
name|org
operator|.
name|apache
operator|.
name|activemq
operator|.
name|network
package|;
end_package

begin_import
import|import static
name|org
operator|.
name|junit
operator|.
name|Assert
operator|.
name|assertEquals
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
name|assertFalse
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
name|assertNotNull
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
name|assertTrue
import|;
end_import

begin_import
import|import
name|java
operator|.
name|lang
operator|.
name|reflect
operator|.
name|Field
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
name|CopyOnWriteArrayList
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|jms
operator|.
name|MessageProducer
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|jms
operator|.
name|TemporaryQueue
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
name|advisory
operator|.
name|AdvisorySupport
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
name|TransportConnection
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
name|util
operator|.
name|Wait
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

begin_comment
comment|/**  * @author<a href="http://www.christianposta.com/blog">Christian Posta</a>  */
end_comment

begin_class
specifier|public
class|class
name|DynamicallyIncludedDestinationsDuplexNetworkTest
extends|extends
name|SimpleNetworkTest
block|{
specifier|private
specifier|static
specifier|final
name|int
name|REMOTE_BROKER_TCP_PORT
init|=
literal|61617
decl_stmt|;
annotation|@
name|Override
specifier|protected
name|String
name|getLocalBrokerURI
parameter_list|()
block|{
return|return
literal|"org/apache/activemq/network/duplexDynamicIncludedDestLocalBroker.xml"
return|;
block|}
annotation|@
name|Override
specifier|protected
name|BrokerService
name|createRemoteBroker
parameter_list|()
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
name|setBrokerName
argument_list|(
literal|"remoteBroker"
argument_list|)
expr_stmt|;
name|broker
operator|.
name|addConnector
argument_list|(
literal|"tcp://localhost:"
operator|+
name|REMOTE_BROKER_TCP_PORT
argument_list|)
expr_stmt|;
return|return
name|broker
return|;
block|}
comment|// we have to override this, because with dynamicallyIncludedDestinations working properly
comment|// (see https://issues.apache.org/jira/browse/AMQ-4209) you can't get request/response
comment|// with temps working (there is no wild card like there is for staticallyIncludedDest)
comment|//
annotation|@
name|Override
specifier|public
name|void
name|testRequestReply
parameter_list|()
throws|throws
name|Exception
block|{      }
annotation|@
name|Test
specifier|public
name|void
name|testTempQueues
parameter_list|()
throws|throws
name|Exception
block|{
name|TemporaryQueue
name|temp
init|=
name|localSession
operator|.
name|createTemporaryQueue
argument_list|()
decl_stmt|;
name|MessageProducer
name|producer
init|=
name|localSession
operator|.
name|createProducer
argument_list|(
name|temp
argument_list|)
decl_stmt|;
name|producer
operator|.
name|send
argument_list|(
name|localSession
operator|.
name|createTextMessage
argument_list|(
literal|"test"
argument_list|)
argument_list|)
expr_stmt|;
name|Thread
operator|.
name|sleep
argument_list|(
literal|100
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"Destination not created"
argument_list|,
literal|1
argument_list|,
name|remoteBroker
operator|.
name|getAdminView
argument_list|()
operator|.
name|getTemporaryQueues
argument_list|()
operator|.
name|length
argument_list|)
expr_stmt|;
name|temp
operator|.
name|delete
argument_list|()
expr_stmt|;
name|Thread
operator|.
name|sleep
argument_list|(
literal|100
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"Destination not deleted"
argument_list|,
literal|0
argument_list|,
name|remoteBroker
operator|.
name|getAdminView
argument_list|()
operator|.
name|getTemporaryQueues
argument_list|()
operator|.
name|length
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testDynamicallyIncludedDestinationsForDuplex
parameter_list|()
throws|throws
name|Exception
block|{
comment|// Once the bridge is set up, we should see the filter used for the duplex end of the bridge
comment|// only subscribe to the specific destinations included in the<dynamicallyIncludedDestinations> list
comment|// so let's test that the filter is correct, let's also test the subscription on the localbroker
comment|// is correct
comment|// the bridge on the remote broker has the correct filter
name|TransportConnection
name|bridgeConnection
init|=
name|getDuplexBridgeConnectionFromRemote
argument_list|()
decl_stmt|;
name|assertNotNull
argument_list|(
name|bridgeConnection
argument_list|)
expr_stmt|;
name|DemandForwardingBridge
name|duplexBridge
init|=
name|getDuplexBridgeFromConnection
argument_list|(
name|bridgeConnection
argument_list|)
decl_stmt|;
name|assertNotNull
argument_list|(
name|duplexBridge
argument_list|)
expr_stmt|;
name|NetworkBridgeConfiguration
name|configuration
init|=
name|getConfigurationFromNetworkBridge
argument_list|(
name|duplexBridge
argument_list|)
decl_stmt|;
name|assertNotNull
argument_list|(
name|configuration
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
literal|"This destinationFilter does not include ONLY the destinations specified in dynamicallyIncludedDestinations"
argument_list|,
name|configuration
operator|.
name|getDestinationFilter
argument_list|()
operator|.
name|equals
argument_list|(
name|AdvisorySupport
operator|.
name|CONSUMER_ADVISORY_TOPIC_PREFIX
operator|+
literal|">"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"There are other patterns in the destinationFilter that shouldn't be there"
argument_list|,
literal|"ActiveMQ.Advisory.Consumer.Queue.include.test.foo,ActiveMQ.Advisory.Consumer.Topic.include.test.bar"
argument_list|,
name|configuration
operator|.
name|getDestinationFilter
argument_list|()
argument_list|)
expr_stmt|;
block|}
specifier|private
name|NetworkBridgeConfiguration
name|getConfigurationFromNetworkBridge
parameter_list|(
name|DemandForwardingBridgeSupport
name|duplexBridge
parameter_list|)
throws|throws
name|NoSuchFieldException
throws|,
name|IllegalAccessException
block|{
name|Field
name|f
init|=
name|DemandForwardingBridgeSupport
operator|.
name|class
operator|.
name|getDeclaredField
argument_list|(
literal|"configuration"
argument_list|)
decl_stmt|;
name|f
operator|.
name|setAccessible
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|NetworkBridgeConfiguration
name|configuration
init|=
operator|(
name|NetworkBridgeConfiguration
operator|)
name|f
operator|.
name|get
argument_list|(
name|duplexBridge
argument_list|)
decl_stmt|;
return|return
name|configuration
return|;
block|}
specifier|private
name|DemandForwardingBridge
name|getDuplexBridgeFromConnection
parameter_list|(
name|TransportConnection
name|bridgeConnection
parameter_list|)
throws|throws
name|NoSuchFieldException
throws|,
name|IllegalAccessException
block|{
name|Field
name|f
init|=
name|TransportConnection
operator|.
name|class
operator|.
name|getDeclaredField
argument_list|(
literal|"duplexBridge"
argument_list|)
decl_stmt|;
name|f
operator|.
name|setAccessible
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|DemandForwardingBridge
name|bridge
init|=
operator|(
name|DemandForwardingBridge
operator|)
name|f
operator|.
name|get
argument_list|(
name|bridgeConnection
argument_list|)
decl_stmt|;
return|return
name|bridge
return|;
block|}
specifier|public
name|TransportConnection
name|getDuplexBridgeConnectionFromRemote
parameter_list|()
block|{
name|TransportConnector
name|transportConnector
init|=
name|remoteBroker
operator|.
name|getTransportConnectorByScheme
argument_list|(
literal|"tcp"
argument_list|)
decl_stmt|;
name|CopyOnWriteArrayList
argument_list|<
name|TransportConnection
argument_list|>
name|transportConnections
init|=
name|transportConnector
operator|.
name|getConnections
argument_list|()
decl_stmt|;
name|TransportConnection
name|duplexBridgeConnectionFromRemote
init|=
name|transportConnections
operator|.
name|get
argument_list|(
literal|0
argument_list|)
decl_stmt|;
return|return
name|duplexBridgeConnectionFromRemote
return|;
block|}
annotation|@
name|Override
specifier|protected
name|void
name|assertNetworkBridgeStatistics
parameter_list|(
specifier|final
name|long
name|expectedLocalSent
parameter_list|,
specifier|final
name|long
name|expectedRemoteSent
parameter_list|)
throws|throws
name|Exception
block|{
specifier|final
name|NetworkBridge
name|localBridge
init|=
name|localBroker
operator|.
name|getNetworkConnectors
argument_list|()
operator|.
name|get
argument_list|(
literal|0
argument_list|)
operator|.
name|activeBridges
argument_list|()
operator|.
name|iterator
argument_list|()
operator|.
name|next
argument_list|()
decl_stmt|;
name|assertTrue
argument_list|(
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
annotation|@
name|Override
specifier|public
name|boolean
name|isSatisified
parameter_list|()
throws|throws
name|Exception
block|{
return|return
name|expectedLocalSent
operator|==
name|localBridge
operator|.
name|getNetworkBridgeStatistics
argument_list|()
operator|.
name|getDequeues
argument_list|()
operator|.
name|getCount
argument_list|()
operator|&&
name|expectedRemoteSent
operator|==
name|localBridge
operator|.
name|getNetworkBridgeStatistics
argument_list|()
operator|.
name|getReceivedCount
argument_list|()
operator|.
name|getCount
argument_list|()
return|;
block|}
block|}
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

