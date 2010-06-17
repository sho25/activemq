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
name|io
operator|.
name|File
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|IOException
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
name|java
operator|.
name|util
operator|.
name|List
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
name|store
operator|.
name|kahadb
operator|.
name|KahaDBStore
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
name|stomp
operator|.
name|Stomp
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
name|stomp
operator|.
name|StompConnection
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
name|stomp
operator|.
name|StompFrame
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

begin_class
specifier|public
class|class
name|ThreeBrokerStompTemporaryQueueTest
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
name|ThreeBrokerStompTemporaryQueueTest
operator|.
name|class
argument_list|)
decl_stmt|;
specifier|private
name|StompConnection
name|stompConnection
decl_stmt|;
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
parameter_list|,
name|boolean
name|conduit
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
name|transportConnectors
operator|.
name|get
argument_list|(
literal|0
argument_list|)
operator|.
name|getConnectUri
argument_list|()
expr_stmt|;
name|NetworkConnector
name|connector
init|=
operator|new
name|DiscoveryNetworkConnector
argument_list|(
operator|new
name|URI
argument_list|(
literal|"static:"
operator|+
name|remoteURI
argument_list|)
argument_list|)
decl_stmt|;
name|connector
operator|.
name|setName
argument_list|(
name|localBroker
operator|.
name|getBrokerName
argument_list|()
operator|+
name|remoteBroker
operator|.
name|getBrokerName
argument_list|()
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
specifier|public
name|void
name|testStompTemporaryQueue
parameter_list|()
throws|throws
name|Exception
block|{
comment|// Setup broker networks
name|bridgeAndConfigureBrokers
argument_list|(
literal|"BrokerA"
argument_list|,
literal|"BrokerB"
argument_list|)
expr_stmt|;
name|bridgeAndConfigureBrokers
argument_list|(
literal|"BrokerA"
argument_list|,
literal|"BrokerC"
argument_list|)
expr_stmt|;
name|bridgeAndConfigureBrokers
argument_list|(
literal|"BrokerB"
argument_list|,
literal|"BrokerA"
argument_list|)
expr_stmt|;
name|bridgeAndConfigureBrokers
argument_list|(
literal|"BrokerB"
argument_list|,
literal|"BrokerC"
argument_list|)
expr_stmt|;
name|bridgeAndConfigureBrokers
argument_list|(
literal|"BrokerC"
argument_list|,
literal|"BrokerA"
argument_list|)
expr_stmt|;
name|bridgeAndConfigureBrokers
argument_list|(
literal|"BrokerC"
argument_list|,
literal|"BrokerB"
argument_list|)
expr_stmt|;
name|startAllBrokers
argument_list|()
expr_stmt|;
name|waitForBridgeFormation
argument_list|()
expr_stmt|;
name|Thread
operator|.
name|sleep
argument_list|(
literal|1000
argument_list|)
expr_stmt|;
name|stompConnection
operator|=
operator|new
name|StompConnection
argument_list|()
expr_stmt|;
name|stompConnection
operator|.
name|open
argument_list|(
literal|"localhost"
argument_list|,
literal|61614
argument_list|)
expr_stmt|;
comment|// Creating a temp queue
name|stompConnection
operator|.
name|sendFrame
argument_list|(
literal|"CONNECT\n"
operator|+
literal|"login: system\n"
operator|+
literal|"passcode: manager\n\n"
operator|+
name|Stomp
operator|.
name|NULL
argument_list|)
expr_stmt|;
name|StompFrame
name|frame
init|=
name|stompConnection
operator|.
name|receive
argument_list|()
decl_stmt|;
name|assertTrue
argument_list|(
name|frame
operator|.
name|toString
argument_list|()
operator|.
name|startsWith
argument_list|(
literal|"CONNECTED"
argument_list|)
argument_list|)
expr_stmt|;
name|stompConnection
operator|.
name|subscribe
argument_list|(
literal|"/temp-queue/meaningless"
argument_list|,
literal|"auto"
argument_list|)
expr_stmt|;
name|stompConnection
operator|.
name|send
argument_list|(
literal|"/temp-queue/meaningless"
argument_list|,
literal|"Hello World"
argument_list|)
expr_stmt|;
name|frame
operator|=
name|stompConnection
operator|.
name|receive
argument_list|(
literal|3000
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"Hello World"
argument_list|,
name|frame
operator|.
name|getBody
argument_list|()
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
literal|"Destination"
argument_list|,
literal|1
argument_list|,
name|brokers
operator|.
name|get
argument_list|(
literal|"BrokerA"
argument_list|)
operator|.
name|broker
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
name|assertEquals
argument_list|(
literal|"Destination"
argument_list|,
literal|1
argument_list|,
name|brokers
operator|.
name|get
argument_list|(
literal|"BrokerB"
argument_list|)
operator|.
name|broker
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
name|assertEquals
argument_list|(
literal|"Destination"
argument_list|,
literal|1
argument_list|,
name|brokers
operator|.
name|get
argument_list|(
literal|"BrokerC"
argument_list|)
operator|.
name|broker
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
name|int
name|advisoryTopicsForTempQueues
decl_stmt|;
name|advisoryTopicsForTempQueues
operator|=
name|countTopicsByName
argument_list|(
literal|"BrokerA"
argument_list|,
literal|"ActiveMQ.Advisory.Consumer.Queue.ID"
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"Advisory topic should be present"
argument_list|,
literal|1
argument_list|,
name|advisoryTopicsForTempQueues
argument_list|)
expr_stmt|;
name|advisoryTopicsForTempQueues
operator|=
name|countTopicsByName
argument_list|(
literal|"BrokerB"
argument_list|,
literal|"ActiveMQ.Advisory.Consumer.Queue.ID"
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"Advisory topic should be present"
argument_list|,
literal|1
argument_list|,
name|advisoryTopicsForTempQueues
argument_list|)
expr_stmt|;
name|advisoryTopicsForTempQueues
operator|=
name|countTopicsByName
argument_list|(
literal|"BrokerC"
argument_list|,
literal|"ActiveMQ.Advisory.Consumer.Queue.ID"
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"Advisory topic should be present"
argument_list|,
literal|1
argument_list|,
name|advisoryTopicsForTempQueues
argument_list|)
expr_stmt|;
name|stompConnection
operator|.
name|disconnect
argument_list|()
expr_stmt|;
name|Thread
operator|.
name|sleep
argument_list|(
literal|1000
argument_list|)
expr_stmt|;
name|advisoryTopicsForTempQueues
operator|=
name|countTopicsByName
argument_list|(
literal|"BrokerA"
argument_list|,
literal|"ActiveMQ.Advisory.Consumer.Queue.ID"
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"Advisory topic should have been deleted"
argument_list|,
literal|0
argument_list|,
name|advisoryTopicsForTempQueues
argument_list|)
expr_stmt|;
name|advisoryTopicsForTempQueues
operator|=
name|countTopicsByName
argument_list|(
literal|"BrokerB"
argument_list|,
literal|"ActiveMQ.Advisory.Consumer.Queue.ID"
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"Advisory topic should have been deleted"
argument_list|,
literal|0
argument_list|,
name|advisoryTopicsForTempQueues
argument_list|)
expr_stmt|;
name|advisoryTopicsForTempQueues
operator|=
name|countTopicsByName
argument_list|(
literal|"BrokerC"
argument_list|,
literal|"ActiveMQ.Advisory.Consumer.Queue.ID"
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"Advisory topic should have been deleted"
argument_list|,
literal|0
argument_list|,
name|advisoryTopicsForTempQueues
argument_list|)
expr_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"Restarting brokerA"
argument_list|)
expr_stmt|;
name|BrokerItem
name|brokerItem
init|=
name|brokers
operator|.
name|remove
argument_list|(
literal|"BrokerA"
argument_list|)
decl_stmt|;
if|if
condition|(
name|brokerItem
operator|!=
literal|null
condition|)
block|{
name|brokerItem
operator|.
name|destroy
argument_list|()
expr_stmt|;
block|}
name|BrokerService
name|restartedBroker
init|=
name|createAndConfigureBroker
argument_list|(
operator|new
name|URI
argument_list|(
literal|"broker:(tcp://localhost:61616,stomp://localhost:61613)/BrokerA"
argument_list|)
argument_list|)
decl_stmt|;
name|bridgeAndConfigureBrokers
argument_list|(
literal|"BrokerA"
argument_list|,
literal|"BrokerB"
argument_list|)
expr_stmt|;
name|bridgeAndConfigureBrokers
argument_list|(
literal|"BrokerA"
argument_list|,
literal|"BrokerC"
argument_list|)
expr_stmt|;
name|restartedBroker
operator|.
name|start
argument_list|()
expr_stmt|;
name|waitForBridgeFormation
argument_list|()
expr_stmt|;
name|Thread
operator|.
name|sleep
argument_list|(
literal|3000
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"Destination"
argument_list|,
literal|0
argument_list|,
name|brokers
operator|.
name|get
argument_list|(
literal|"BrokerA"
argument_list|)
operator|.
name|broker
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
name|assertEquals
argument_list|(
literal|"Destination"
argument_list|,
literal|0
argument_list|,
name|brokers
operator|.
name|get
argument_list|(
literal|"BrokerB"
argument_list|)
operator|.
name|broker
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
name|assertEquals
argument_list|(
literal|"Destination"
argument_list|,
literal|0
argument_list|,
name|brokers
operator|.
name|get
argument_list|(
literal|"BrokerC"
argument_list|)
operator|.
name|broker
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
name|advisoryTopicsForTempQueues
operator|=
name|countTopicsByName
argument_list|(
literal|"BrokerA"
argument_list|,
literal|"ActiveMQ.Advisory.Consumer.Queue.ID"
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"Advisory topic should have been deleted"
argument_list|,
literal|0
argument_list|,
name|advisoryTopicsForTempQueues
argument_list|)
expr_stmt|;
name|advisoryTopicsForTempQueues
operator|=
name|countTopicsByName
argument_list|(
literal|"BrokerB"
argument_list|,
literal|"ActiveMQ.Advisory.Consumer.Queue.ID"
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"Advisory topic should have been deleted"
argument_list|,
literal|0
argument_list|,
name|advisoryTopicsForTempQueues
argument_list|)
expr_stmt|;
name|advisoryTopicsForTempQueues
operator|=
name|countTopicsByName
argument_list|(
literal|"BrokerC"
argument_list|,
literal|"ActiveMQ.Advisory.Consumer.Queue.ID"
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"Advisory topic should have been deleted"
argument_list|,
literal|0
argument_list|,
name|advisoryTopicsForTempQueues
argument_list|)
expr_stmt|;
block|}
specifier|private
name|int
name|countTopicsByName
parameter_list|(
name|String
name|broker
parameter_list|,
name|String
name|name
parameter_list|)
throws|throws
name|Exception
block|{
name|int
name|advisoryTopicsForTempQueues
init|=
literal|0
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|brokers
operator|.
name|get
argument_list|(
name|broker
argument_list|)
operator|.
name|broker
operator|.
name|getAdminView
argument_list|()
operator|.
name|getTopics
argument_list|()
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
if|if
condition|(
name|brokers
operator|.
name|get
argument_list|(
name|broker
argument_list|)
operator|.
name|broker
operator|.
name|getAdminView
argument_list|()
operator|.
name|getTopics
argument_list|()
index|[
name|i
index|]
operator|.
name|toString
argument_list|()
operator|.
name|contains
argument_list|(
name|name
argument_list|)
condition|)
block|{
name|advisoryTopicsForTempQueues
operator|++
expr_stmt|;
block|}
block|}
return|return
name|advisoryTopicsForTempQueues
return|;
block|}
specifier|private
name|void
name|bridgeAndConfigureBrokers
parameter_list|(
name|String
name|local
parameter_list|,
name|String
name|remote
parameter_list|)
throws|throws
name|Exception
block|{
name|NetworkConnector
name|bridge
init|=
name|bridgeBrokers
argument_list|(
name|local
argument_list|,
name|remote
argument_list|)
decl_stmt|;
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
name|String
name|options
init|=
operator|new
name|String
argument_list|(
literal|"?deleteAllMessagesOnStartup=true"
argument_list|)
decl_stmt|;
name|createAndConfigureBroker
argument_list|(
operator|new
name|URI
argument_list|(
literal|"broker:(tcp://localhost:61616,stomp://localhost:61613)/BrokerA"
operator|+
name|options
argument_list|)
argument_list|)
expr_stmt|;
name|createAndConfigureBroker
argument_list|(
operator|new
name|URI
argument_list|(
literal|"broker:(tcp://localhost:61617,stomp://localhost:61614)/BrokerB"
operator|+
name|options
argument_list|)
argument_list|)
expr_stmt|;
name|createAndConfigureBroker
argument_list|(
operator|new
name|URI
argument_list|(
literal|"broker:(tcp://localhost:61618,stomp://localhost:61615)/BrokerC"
operator|+
name|options
argument_list|)
argument_list|)
expr_stmt|;
block|}
specifier|private
name|BrokerService
name|createAndConfigureBroker
parameter_list|(
name|URI
name|uri
parameter_list|)
throws|throws
name|Exception
block|{
name|BrokerService
name|broker
init|=
name|createBroker
argument_list|(
name|uri
argument_list|)
decl_stmt|;
name|configurePersistenceAdapter
argument_list|(
name|broker
argument_list|)
expr_stmt|;
return|return
name|broker
return|;
block|}
specifier|protected
name|void
name|configurePersistenceAdapter
parameter_list|(
name|BrokerService
name|broker
parameter_list|)
throws|throws
name|IOException
block|{
name|File
name|dataFileDir
init|=
operator|new
name|File
argument_list|(
literal|"target/test-amq-data/kahadb/"
operator|+
name|broker
operator|.
name|getBrokerName
argument_list|()
argument_list|)
decl_stmt|;
name|KahaDBStore
name|kaha
init|=
operator|new
name|KahaDBStore
argument_list|()
decl_stmt|;
name|kaha
operator|.
name|setDirectory
argument_list|(
name|dataFileDir
argument_list|)
expr_stmt|;
name|broker
operator|.
name|setPersistenceAdapter
argument_list|(
name|kaha
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

