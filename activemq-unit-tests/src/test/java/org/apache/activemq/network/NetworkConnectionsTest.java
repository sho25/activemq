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
name|network
package|;
end_package

begin_import
import|import
name|junit
operator|.
name|framework
operator|.
name|TestCase
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
name|ActiveMQQueue
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

begin_import
import|import
name|javax
operator|.
name|jms
operator|.
name|*
import|;
end_import

begin_class
specifier|public
class|class
name|NetworkConnectionsTest
extends|extends
name|TestCase
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
name|NetworkConnectionsTest
operator|.
name|class
argument_list|)
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|String
name|LOCAL_BROKER_TRANSPORT_URI
init|=
literal|"tcp://localhost:61616"
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|String
name|REMOTE_BROKER_TRANSPORT_URI
init|=
literal|"tcp://localhost:61617"
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|String
name|DESTINATION_NAME
init|=
literal|"TEST.RECONNECT"
decl_stmt|;
specifier|private
name|BrokerService
name|localBroker
decl_stmt|;
specifier|private
name|BrokerService
name|remoteBroker
decl_stmt|;
annotation|@
name|Test
specifier|public
name|void
name|testIsStarted
parameter_list|()
throws|throws
name|Exception
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"testIsStarted is starting..."
argument_list|)
expr_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"Adding network connector..."
argument_list|)
expr_stmt|;
name|NetworkConnector
name|nc
init|=
name|localBroker
operator|.
name|addNetworkConnector
argument_list|(
literal|"static:("
operator|+
name|REMOTE_BROKER_TRANSPORT_URI
operator|+
literal|")"
argument_list|)
decl_stmt|;
name|nc
operator|.
name|setName
argument_list|(
literal|"NC1"
argument_list|)
expr_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"Starting network connector..."
argument_list|)
expr_stmt|;
name|nc
operator|.
name|start
argument_list|()
expr_stmt|;
name|assertTrue
argument_list|(
name|nc
operator|.
name|isStarted
argument_list|()
argument_list|)
expr_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"Stopping network connector..."
argument_list|)
expr_stmt|;
name|nc
operator|.
name|stop
argument_list|()
expr_stmt|;
while|while
condition|(
name|nc
operator|.
name|isStopping
argument_list|()
condition|)
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"... still stopping ..."
argument_list|)
expr_stmt|;
name|Thread
operator|.
name|sleep
argument_list|(
literal|100
argument_list|)
expr_stmt|;
block|}
name|assertTrue
argument_list|(
name|nc
operator|.
name|isStopped
argument_list|()
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|nc
operator|.
name|isStarted
argument_list|()
argument_list|)
expr_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"Starting network connector..."
argument_list|)
expr_stmt|;
name|nc
operator|.
name|start
argument_list|()
expr_stmt|;
name|assertTrue
argument_list|(
name|nc
operator|.
name|isStarted
argument_list|()
argument_list|)
expr_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"Stopping network connector..."
argument_list|)
expr_stmt|;
name|nc
operator|.
name|stop
argument_list|()
expr_stmt|;
while|while
condition|(
name|nc
operator|.
name|isStopping
argument_list|()
condition|)
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"... still stopping ..."
argument_list|)
expr_stmt|;
name|Thread
operator|.
name|sleep
argument_list|(
literal|100
argument_list|)
expr_stmt|;
block|}
name|assertTrue
argument_list|(
name|nc
operator|.
name|isStopped
argument_list|()
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|nc
operator|.
name|isStarted
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testNetworkConnectionRestart
parameter_list|()
throws|throws
name|Exception
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"testNetworkConnectionRestart is starting..."
argument_list|)
expr_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"Adding network connector..."
argument_list|)
expr_stmt|;
name|NetworkConnector
name|nc
init|=
name|localBroker
operator|.
name|addNetworkConnector
argument_list|(
literal|"static:("
operator|+
name|REMOTE_BROKER_TRANSPORT_URI
operator|+
literal|")"
argument_list|)
decl_stmt|;
name|nc
operator|.
name|setName
argument_list|(
literal|"NC1"
argument_list|)
expr_stmt|;
name|nc
operator|.
name|start
argument_list|()
expr_stmt|;
name|assertTrue
argument_list|(
name|nc
operator|.
name|isStarted
argument_list|()
argument_list|)
expr_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"Setting up Message Producer and Consumer"
argument_list|)
expr_stmt|;
name|ActiveMQQueue
name|destination
init|=
operator|new
name|ActiveMQQueue
argument_list|(
name|DESTINATION_NAME
argument_list|)
decl_stmt|;
name|ActiveMQConnectionFactory
name|localFactory
init|=
operator|new
name|ActiveMQConnectionFactory
argument_list|(
name|LOCAL_BROKER_TRANSPORT_URI
argument_list|)
decl_stmt|;
name|Connection
name|localConnection
init|=
name|localFactory
operator|.
name|createConnection
argument_list|()
decl_stmt|;
name|localConnection
operator|.
name|start
argument_list|()
expr_stmt|;
name|Session
name|localSession
init|=
name|localConnection
operator|.
name|createSession
argument_list|(
literal|false
argument_list|,
name|Session
operator|.
name|AUTO_ACKNOWLEDGE
argument_list|)
decl_stmt|;
name|MessageProducer
name|localProducer
init|=
name|localSession
operator|.
name|createProducer
argument_list|(
name|destination
argument_list|)
decl_stmt|;
name|ActiveMQConnectionFactory
name|remoteFactory
init|=
operator|new
name|ActiveMQConnectionFactory
argument_list|(
name|REMOTE_BROKER_TRANSPORT_URI
argument_list|)
decl_stmt|;
name|Connection
name|remoteConnection
init|=
name|remoteFactory
operator|.
name|createConnection
argument_list|()
decl_stmt|;
name|remoteConnection
operator|.
name|start
argument_list|()
expr_stmt|;
name|Session
name|remoteSession
init|=
name|remoteConnection
operator|.
name|createSession
argument_list|(
literal|false
argument_list|,
name|Session
operator|.
name|AUTO_ACKNOWLEDGE
argument_list|)
decl_stmt|;
name|MessageConsumer
name|remoteConsumer
init|=
name|remoteSession
operator|.
name|createConsumer
argument_list|(
name|destination
argument_list|)
decl_stmt|;
name|Message
name|message
init|=
name|localSession
operator|.
name|createTextMessage
argument_list|(
literal|"test"
argument_list|)
decl_stmt|;
name|localProducer
operator|.
name|send
argument_list|(
name|message
argument_list|)
expr_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"Testing initial network connection..."
argument_list|)
expr_stmt|;
name|message
operator|=
name|remoteConsumer
operator|.
name|receive
argument_list|(
literal|10000
argument_list|)
expr_stmt|;
name|assertNotNull
argument_list|(
name|message
argument_list|)
expr_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"Stopping network connection..."
argument_list|)
expr_stmt|;
name|nc
operator|.
name|stop
argument_list|()
expr_stmt|;
name|assertFalse
argument_list|(
name|nc
operator|.
name|isStarted
argument_list|()
argument_list|)
expr_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"Sending 2nd message..."
argument_list|)
expr_stmt|;
name|message
operator|=
name|localSession
operator|.
name|createTextMessage
argument_list|(
literal|"test stop"
argument_list|)
expr_stmt|;
name|localProducer
operator|.
name|send
argument_list|(
name|message
argument_list|)
expr_stmt|;
name|message
operator|=
name|remoteConsumer
operator|.
name|receive
argument_list|(
literal|1000
argument_list|)
expr_stmt|;
name|assertNull
argument_list|(
literal|"Message should not have been delivered since NetworkConnector was stopped"
argument_list|,
name|message
argument_list|)
expr_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"(Re)starting network connection..."
argument_list|)
expr_stmt|;
name|nc
operator|.
name|start
argument_list|()
expr_stmt|;
name|assertTrue
argument_list|(
name|nc
operator|.
name|isStarted
argument_list|()
argument_list|)
expr_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"Wait for 2nd message to get forwarded and received..."
argument_list|)
expr_stmt|;
name|message
operator|=
name|remoteConsumer
operator|.
name|receive
argument_list|(
literal|10000
argument_list|)
expr_stmt|;
name|assertNotNull
argument_list|(
literal|"Should have received 2nd message"
argument_list|,
name|message
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testNetworkConnectionReAddURI
parameter_list|()
throws|throws
name|Exception
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"testNetworkConnectionReAddURI is starting..."
argument_list|)
expr_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"Adding network connector 'NC1'..."
argument_list|)
expr_stmt|;
name|NetworkConnector
name|nc
init|=
name|localBroker
operator|.
name|addNetworkConnector
argument_list|(
literal|"static:("
operator|+
name|REMOTE_BROKER_TRANSPORT_URI
operator|+
literal|")"
argument_list|)
decl_stmt|;
name|nc
operator|.
name|setName
argument_list|(
literal|"NC1"
argument_list|)
expr_stmt|;
name|nc
operator|.
name|start
argument_list|()
expr_stmt|;
name|assertTrue
argument_list|(
name|nc
operator|.
name|isStarted
argument_list|()
argument_list|)
expr_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"Looking up network connector by name..."
argument_list|)
expr_stmt|;
name|NetworkConnector
name|nc1
init|=
name|localBroker
operator|.
name|getNetworkConnectorByName
argument_list|(
literal|"NC1"
argument_list|)
decl_stmt|;
name|assertNotNull
argument_list|(
literal|"Should find network connector 'NC1'"
argument_list|,
name|nc1
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|nc1
operator|.
name|isStarted
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|nc
argument_list|,
name|nc1
argument_list|)
expr_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"Setting up producer and consumer..."
argument_list|)
expr_stmt|;
name|ActiveMQQueue
name|destination
init|=
operator|new
name|ActiveMQQueue
argument_list|(
name|DESTINATION_NAME
argument_list|)
decl_stmt|;
name|ActiveMQConnectionFactory
name|localFactory
init|=
operator|new
name|ActiveMQConnectionFactory
argument_list|(
name|LOCAL_BROKER_TRANSPORT_URI
argument_list|)
decl_stmt|;
name|Connection
name|localConnection
init|=
name|localFactory
operator|.
name|createConnection
argument_list|()
decl_stmt|;
name|localConnection
operator|.
name|start
argument_list|()
expr_stmt|;
name|Session
name|localSession
init|=
name|localConnection
operator|.
name|createSession
argument_list|(
literal|false
argument_list|,
name|Session
operator|.
name|AUTO_ACKNOWLEDGE
argument_list|)
decl_stmt|;
name|MessageProducer
name|localProducer
init|=
name|localSession
operator|.
name|createProducer
argument_list|(
name|destination
argument_list|)
decl_stmt|;
name|ActiveMQConnectionFactory
name|remoteFactory
init|=
operator|new
name|ActiveMQConnectionFactory
argument_list|(
name|REMOTE_BROKER_TRANSPORT_URI
argument_list|)
decl_stmt|;
name|Connection
name|remoteConnection
init|=
name|remoteFactory
operator|.
name|createConnection
argument_list|()
decl_stmt|;
name|remoteConnection
operator|.
name|start
argument_list|()
expr_stmt|;
name|Session
name|remoteSession
init|=
name|remoteConnection
operator|.
name|createSession
argument_list|(
literal|false
argument_list|,
name|Session
operator|.
name|AUTO_ACKNOWLEDGE
argument_list|)
decl_stmt|;
name|MessageConsumer
name|remoteConsumer
init|=
name|remoteSession
operator|.
name|createConsumer
argument_list|(
name|destination
argument_list|)
decl_stmt|;
name|Message
name|message
init|=
name|localSession
operator|.
name|createTextMessage
argument_list|(
literal|"test"
argument_list|)
decl_stmt|;
name|localProducer
operator|.
name|send
argument_list|(
name|message
argument_list|)
expr_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"Testing initial network connection..."
argument_list|)
expr_stmt|;
name|message
operator|=
name|remoteConsumer
operator|.
name|receive
argument_list|(
literal|10000
argument_list|)
expr_stmt|;
name|assertNotNull
argument_list|(
name|message
argument_list|)
expr_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"Stopping network connector 'NC1'..."
argument_list|)
expr_stmt|;
name|nc
operator|.
name|stop
argument_list|()
expr_stmt|;
name|assertFalse
argument_list|(
name|nc
operator|.
name|isStarted
argument_list|()
argument_list|)
expr_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"Removing network connector..."
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|localBroker
operator|.
name|removeNetworkConnector
argument_list|(
name|nc
argument_list|)
argument_list|)
expr_stmt|;
name|nc1
operator|=
name|localBroker
operator|.
name|getNetworkConnectorByName
argument_list|(
literal|"NC1"
argument_list|)
expr_stmt|;
name|assertNull
argument_list|(
literal|"Should not find network connector 'NC1'"
argument_list|,
name|nc1
argument_list|)
expr_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"Re-adding network connector 'NC2'..."
argument_list|)
expr_stmt|;
name|nc
operator|=
name|localBroker
operator|.
name|addNetworkConnector
argument_list|(
literal|"static:("
operator|+
name|REMOTE_BROKER_TRANSPORT_URI
operator|+
literal|")"
argument_list|)
expr_stmt|;
name|nc
operator|.
name|setName
argument_list|(
literal|"NC2"
argument_list|)
expr_stmt|;
name|nc
operator|.
name|start
argument_list|()
expr_stmt|;
name|assertTrue
argument_list|(
name|nc
operator|.
name|isStarted
argument_list|()
argument_list|)
expr_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"Looking up network connector by name..."
argument_list|)
expr_stmt|;
name|NetworkConnector
name|nc2
init|=
name|localBroker
operator|.
name|getNetworkConnectorByName
argument_list|(
literal|"NC2"
argument_list|)
decl_stmt|;
name|assertNotNull
argument_list|(
name|nc2
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|nc2
operator|.
name|isStarted
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|nc
argument_list|,
name|nc2
argument_list|)
expr_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"Testing re-added network connection..."
argument_list|)
expr_stmt|;
name|message
operator|=
name|localSession
operator|.
name|createTextMessage
argument_list|(
literal|"test"
argument_list|)
expr_stmt|;
name|localProducer
operator|.
name|send
argument_list|(
name|message
argument_list|)
expr_stmt|;
name|message
operator|=
name|remoteConsumer
operator|.
name|receive
argument_list|(
literal|10000
argument_list|)
expr_stmt|;
name|assertNotNull
argument_list|(
name|message
argument_list|)
expr_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"Stopping network connector..."
argument_list|)
expr_stmt|;
name|nc
operator|.
name|stop
argument_list|()
expr_stmt|;
name|assertFalse
argument_list|(
name|nc
operator|.
name|isStarted
argument_list|()
argument_list|)
expr_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"Removing network connection 'NC2'"
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|localBroker
operator|.
name|removeNetworkConnector
argument_list|(
name|nc
argument_list|)
argument_list|)
expr_stmt|;
name|nc2
operator|=
name|localBroker
operator|.
name|getNetworkConnectorByName
argument_list|(
literal|"NC2"
argument_list|)
expr_stmt|;
name|assertNull
argument_list|(
literal|"Should not find network connector 'NC2'"
argument_list|,
name|nc2
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|protected
name|void
name|setUp
parameter_list|()
throws|throws
name|Exception
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"Setting up LocalBroker"
argument_list|)
expr_stmt|;
name|localBroker
operator|=
operator|new
name|BrokerService
argument_list|()
expr_stmt|;
name|localBroker
operator|.
name|setBrokerName
argument_list|(
literal|"LocalBroker"
argument_list|)
expr_stmt|;
name|localBroker
operator|.
name|setUseJmx
argument_list|(
literal|false
argument_list|)
expr_stmt|;
name|localBroker
operator|.
name|setPersistent
argument_list|(
literal|false
argument_list|)
expr_stmt|;
name|localBroker
operator|.
name|setTransportConnectorURIs
argument_list|(
operator|new
name|String
index|[]
block|{
name|LOCAL_BROKER_TRANSPORT_URI
block|}
argument_list|)
expr_stmt|;
name|localBroker
operator|.
name|start
argument_list|()
expr_stmt|;
name|localBroker
operator|.
name|waitUntilStarted
argument_list|()
expr_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"Setting up RemoteBroker"
argument_list|)
expr_stmt|;
name|remoteBroker
operator|=
operator|new
name|BrokerService
argument_list|()
expr_stmt|;
name|remoteBroker
operator|.
name|setBrokerName
argument_list|(
literal|"RemoteBroker"
argument_list|)
expr_stmt|;
name|remoteBroker
operator|.
name|setUseJmx
argument_list|(
literal|false
argument_list|)
expr_stmt|;
name|remoteBroker
operator|.
name|setPersistent
argument_list|(
literal|false
argument_list|)
expr_stmt|;
name|remoteBroker
operator|.
name|setTransportConnectorURIs
argument_list|(
operator|new
name|String
index|[]
block|{
name|REMOTE_BROKER_TRANSPORT_URI
block|}
argument_list|)
expr_stmt|;
name|remoteBroker
operator|.
name|start
argument_list|()
expr_stmt|;
name|remoteBroker
operator|.
name|waitUntilStarted
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Override
specifier|protected
name|void
name|tearDown
parameter_list|()
throws|throws
name|Exception
block|{
if|if
condition|(
name|localBroker
operator|.
name|isStarted
argument_list|()
condition|)
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"Stopping LocalBroker"
argument_list|)
expr_stmt|;
name|localBroker
operator|.
name|stop
argument_list|()
expr_stmt|;
name|localBroker
operator|.
name|waitUntilStopped
argument_list|()
expr_stmt|;
name|localBroker
operator|=
literal|null
expr_stmt|;
block|}
if|if
condition|(
name|remoteBroker
operator|.
name|isStarted
argument_list|()
condition|)
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"Stopping RemoteBroker"
argument_list|)
expr_stmt|;
name|remoteBroker
operator|.
name|stop
argument_list|()
expr_stmt|;
name|remoteBroker
operator|.
name|waitUntilStopped
argument_list|()
expr_stmt|;
name|remoteBroker
operator|=
literal|null
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

