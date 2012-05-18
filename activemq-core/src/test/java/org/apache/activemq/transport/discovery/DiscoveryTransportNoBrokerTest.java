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
name|transport
operator|.
name|discovery
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
name|Map
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Vector
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|jms
operator|.
name|Connection
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|jms
operator|.
name|JMSException
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
name|CombinationTestSupport
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
name|command
operator|.
name|DiscoveryEvent
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
name|StubCompositeTransport
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
name|URISupport
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
name|URISupport
operator|.
name|CompositeData
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
name|DiscoveryTransportNoBrokerTest
extends|extends
name|CombinationTestSupport
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
name|DiscoveryTransportNoBrokerTest
operator|.
name|class
argument_list|)
decl_stmt|;
annotation|@
name|Override
specifier|public
name|void
name|setUp
parameter_list|()
throws|throws
name|Exception
block|{
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
specifier|public
name|void
name|testNoExtraThreads
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
name|TransportConnector
name|tcp
init|=
name|broker
operator|.
name|addConnector
argument_list|(
literal|"tcp://localhost:0?transport.closeAsync=false"
argument_list|)
decl_stmt|;
name|String
name|group
init|=
literal|"GR-"
operator|+
name|System
operator|.
name|currentTimeMillis
argument_list|()
decl_stmt|;
name|URI
name|discoveryUri
init|=
operator|new
name|URI
argument_list|(
literal|"multicast://default?group="
operator|+
name|group
argument_list|)
decl_stmt|;
name|tcp
operator|.
name|setDiscoveryUri
argument_list|(
name|discoveryUri
argument_list|)
expr_stmt|;
name|broker
operator|.
name|start
argument_list|()
expr_stmt|;
name|broker
operator|.
name|waitUntilStarted
argument_list|()
expr_stmt|;
name|Vector
argument_list|<
name|String
argument_list|>
name|existingNames
init|=
operator|new
name|Vector
argument_list|<
name|String
argument_list|>
argument_list|()
decl_stmt|;
name|Thread
index|[]
name|threads
init|=
name|getThreads
argument_list|()
decl_stmt|;
for|for
control|(
name|Thread
name|t
range|:
name|threads
control|)
block|{
name|existingNames
operator|.
name|add
argument_list|(
name|t
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
block|}
specifier|final
name|int
name|idleThreadCount
init|=
name|threads
operator|.
name|length
decl_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"Broker started - thread Count:"
operator|+
name|idleThreadCount
argument_list|)
expr_stmt|;
specifier|final
name|int
name|noConnectionToCreate
init|=
literal|10
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
literal|10
condition|;
name|i
operator|++
control|)
block|{
name|ActiveMQConnectionFactory
name|factory
init|=
operator|new
name|ActiveMQConnectionFactory
argument_list|(
literal|"discovery:(multicast://239.255.2.3:6155?group="
operator|+
name|group
operator|+
literal|")?closeAsync=false"
argument_list|)
decl_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"Connecting."
argument_list|)
expr_stmt|;
name|Connection
name|connection
init|=
name|factory
operator|.
name|createConnection
argument_list|()
decl_stmt|;
name|connection
operator|.
name|setClientID
argument_list|(
literal|"test"
argument_list|)
expr_stmt|;
name|connection
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
name|Thread
operator|.
name|sleep
argument_list|(
literal|2000
argument_list|)
expr_stmt|;
name|threads
operator|=
name|getThreads
argument_list|()
expr_stmt|;
for|for
control|(
name|Thread
name|t
range|:
name|threads
control|)
block|{
if|if
condition|(
operator|!
name|existingNames
operator|.
name|contains
argument_list|(
name|t
operator|.
name|getName
argument_list|()
argument_list|)
condition|)
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"Remaining thread:"
operator|+
name|t
argument_list|)
expr_stmt|;
block|}
block|}
name|assertTrue
argument_list|(
literal|"no extra threads per connection"
argument_list|,
name|Thread
operator|.
name|activeCount
argument_list|()
operator|-
name|idleThreadCount
operator|<
name|noConnectionToCreate
argument_list|)
expr_stmt|;
block|}
specifier|private
name|Thread
index|[]
name|getThreads
parameter_list|()
block|{
name|Thread
index|[]
name|threads
init|=
operator|new
name|Thread
index|[
name|Thread
operator|.
name|activeCount
argument_list|()
index|]
decl_stmt|;
name|Thread
operator|.
name|enumerate
argument_list|(
name|threads
argument_list|)
expr_stmt|;
return|return
name|threads
return|;
block|}
specifier|public
name|void
name|testMaxReconnectAttempts
parameter_list|()
throws|throws
name|JMSException
block|{
try|try
block|{
name|ActiveMQConnectionFactory
name|factory
init|=
operator|new
name|ActiveMQConnectionFactory
argument_list|(
literal|"discovery:(multicast://doesNOTexist)"
argument_list|)
decl_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"Connecting."
argument_list|)
expr_stmt|;
name|Connection
name|connection
init|=
name|factory
operator|.
name|createConnection
argument_list|()
decl_stmt|;
name|connection
operator|.
name|setClientID
argument_list|(
literal|"test"
argument_list|)
expr_stmt|;
name|fail
argument_list|(
literal|"Did not fail to connect as expected."
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|JMSException
name|expected
parameter_list|)
block|{
name|assertTrue
argument_list|(
literal|"reason is java.io.IOException, was: "
operator|+
name|expected
operator|.
name|getCause
argument_list|()
argument_list|,
name|expected
operator|.
name|getCause
argument_list|()
operator|instanceof
name|java
operator|.
name|io
operator|.
name|IOException
argument_list|)
expr_stmt|;
block|}
block|}
specifier|public
name|void
name|testInitialConnectDelayWithNoBroker
parameter_list|()
throws|throws
name|Exception
block|{
comment|// the initialReconnectDelay only kicks in once a set of connect URL have
comment|// been returned from the discovery agent.
comment|// Up to that point the reconnectDelay is used which has a default value of 10
comment|//
name|long
name|initialReconnectDelay
init|=
literal|4000
decl_stmt|;
name|long
name|startT
init|=
name|System
operator|.
name|currentTimeMillis
argument_list|()
decl_stmt|;
name|String
name|groupId
init|=
literal|"WillNotMatch"
operator|+
name|startT
decl_stmt|;
try|try
block|{
name|String
name|urlStr
init|=
literal|"discovery:(multicast://default?group="
operator|+
name|groupId
operator|+
literal|")?useExponentialBackOff=false&maxReconnectAttempts=2&reconnectDelay="
operator|+
name|initialReconnectDelay
decl_stmt|;
name|ActiveMQConnectionFactory
name|factory
init|=
operator|new
name|ActiveMQConnectionFactory
argument_list|(
name|urlStr
argument_list|)
decl_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"Connecting."
argument_list|)
expr_stmt|;
name|Connection
name|connection
init|=
name|factory
operator|.
name|createConnection
argument_list|()
decl_stmt|;
name|connection
operator|.
name|setClientID
argument_list|(
literal|"test"
argument_list|)
expr_stmt|;
name|fail
argument_list|(
literal|"Did not fail to connect as expected."
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|JMSException
name|expected
parameter_list|)
block|{
name|assertTrue
argument_list|(
literal|"reason is java.io.IOException, was: "
operator|+
name|expected
operator|.
name|getCause
argument_list|()
argument_list|,
name|expected
operator|.
name|getCause
argument_list|()
operator|instanceof
name|java
operator|.
name|io
operator|.
name|IOException
argument_list|)
expr_stmt|;
name|long
name|duration
init|=
name|System
operator|.
name|currentTimeMillis
argument_list|()
operator|-
name|startT
decl_stmt|;
name|assertTrue
argument_list|(
literal|"took at least initialReconnectDelay time: "
operator|+
name|duration
operator|+
literal|" e:"
operator|+
name|expected
argument_list|,
name|duration
operator|>=
name|initialReconnectDelay
argument_list|)
expr_stmt|;
block|}
block|}
specifier|public
name|void
name|testSetDiscoveredBrokerProperties
parameter_list|()
throws|throws
name|Exception
block|{
specifier|final
name|String
name|extraParameterName
init|=
literal|"connectionTimeout"
decl_stmt|;
specifier|final
name|String
name|extraParameterValue
init|=
literal|"3000"
decl_stmt|;
specifier|final
name|URI
name|uri
init|=
operator|new
name|URI
argument_list|(
literal|"discovery:(multicast://default)?initialReconnectDelay=100&"
operator|+
name|DiscoveryListener
operator|.
name|DISCOVERED_OPTION_PREFIX
operator|+
name|extraParameterName
operator|+
literal|"="
operator|+
name|extraParameterValue
argument_list|)
decl_stmt|;
name|CompositeData
name|compositeData
init|=
name|URISupport
operator|.
name|parseComposite
argument_list|(
name|uri
argument_list|)
decl_stmt|;
name|StubCompositeTransport
name|compositeTransport
init|=
operator|new
name|StubCompositeTransport
argument_list|()
decl_stmt|;
name|DiscoveryTransport
name|discoveryTransport
init|=
name|DiscoveryTransportFactory
operator|.
name|createTransport
argument_list|(
name|compositeTransport
argument_list|,
name|compositeData
argument_list|,
name|compositeData
operator|.
name|getParameters
argument_list|()
argument_list|)
decl_stmt|;
name|discoveryTransport
operator|.
name|onServiceAdd
argument_list|(
operator|new
name|DiscoveryEvent
argument_list|(
literal|"tcp://localhost:61616"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"expected added URI after discovery event"
argument_list|,
name|compositeTransport
operator|.
name|getTransportURIs
argument_list|()
operator|.
name|length
argument_list|,
literal|1
argument_list|)
expr_stmt|;
name|URI
name|discoveredServiceURI
init|=
name|compositeTransport
operator|.
name|getTransportURIs
argument_list|()
index|[
literal|0
index|]
decl_stmt|;
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|parameters
init|=
name|URISupport
operator|.
name|parseParameters
argument_list|(
name|discoveredServiceURI
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
literal|"unable to add parameter to discovered service"
argument_list|,
name|parameters
operator|.
name|containsKey
argument_list|(
name|extraParameterName
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"incorrect value for parameter added to discovered service"
argument_list|,
name|parameters
operator|.
name|get
argument_list|(
name|extraParameterName
argument_list|)
argument_list|,
name|extraParameterValue
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|testSetDiscoveredStaticBrokerProperties
parameter_list|()
throws|throws
name|Exception
block|{
specifier|final
name|String
name|extraParameterName
init|=
literal|"connectionTimeout"
decl_stmt|;
specifier|final
name|String
name|extraParameterValue
init|=
literal|"3000"
decl_stmt|;
specifier|final
name|URI
name|uri
init|=
operator|new
name|URI
argument_list|(
literal|"discovery:(static:tcp://localhost:61616)?initialReconnectDelay=100&"
operator|+
name|DiscoveryListener
operator|.
name|DISCOVERED_OPTION_PREFIX
operator|+
name|extraParameterName
operator|+
literal|"="
operator|+
name|extraParameterValue
argument_list|)
decl_stmt|;
name|CompositeData
name|compositeData
init|=
name|URISupport
operator|.
name|parseComposite
argument_list|(
name|uri
argument_list|)
decl_stmt|;
name|StubCompositeTransport
name|compositeTransport
init|=
operator|new
name|StubCompositeTransport
argument_list|()
decl_stmt|;
name|DiscoveryTransport
name|discoveryTransport
init|=
name|DiscoveryTransportFactory
operator|.
name|createTransport
argument_list|(
name|compositeTransport
argument_list|,
name|compositeData
argument_list|,
name|compositeData
operator|.
name|getParameters
argument_list|()
argument_list|)
decl_stmt|;
name|discoveryTransport
operator|.
name|start
argument_list|()
expr_stmt|;
name|assertEquals
argument_list|(
literal|"expected added URI after discovery event"
argument_list|,
literal|1
argument_list|,
name|compositeTransport
operator|.
name|getTransportURIs
argument_list|()
operator|.
name|length
argument_list|)
expr_stmt|;
name|URI
name|discoveredServiceURI
init|=
name|compositeTransport
operator|.
name|getTransportURIs
argument_list|()
index|[
literal|0
index|]
decl_stmt|;
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|parameters
init|=
name|URISupport
operator|.
name|parseParameters
argument_list|(
name|discoveredServiceURI
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
literal|"unable to add parameter to discovered service"
argument_list|,
name|parameters
operator|.
name|containsKey
argument_list|(
name|extraParameterName
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"incorrect value for parameter added to discovered service"
argument_list|,
name|parameters
operator|.
name|get
argument_list|(
name|extraParameterName
argument_list|)
argument_list|,
name|extraParameterValue
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|testAddRemoveDiscoveredBroker
parameter_list|()
throws|throws
name|Exception
block|{
specifier|final
name|URI
name|uri
init|=
operator|new
name|URI
argument_list|(
literal|"discovery:(multicast://default)?initialReconnectDelay=100&connectionTimeout=3000"
argument_list|)
decl_stmt|;
name|CompositeData
name|compositeData
init|=
name|URISupport
operator|.
name|parseComposite
argument_list|(
name|uri
argument_list|)
decl_stmt|;
name|StubCompositeTransport
name|compositeTransport
init|=
operator|new
name|StubCompositeTransport
argument_list|()
decl_stmt|;
name|DiscoveryTransport
name|discoveryTransport
init|=
name|DiscoveryTransportFactory
operator|.
name|createTransport
argument_list|(
name|compositeTransport
argument_list|,
name|compositeData
argument_list|,
name|compositeData
operator|.
name|getParameters
argument_list|()
argument_list|)
decl_stmt|;
specifier|final
name|String
name|serviceName
init|=
literal|"tcp://localhost:61616"
decl_stmt|;
name|discoveryTransport
operator|.
name|onServiceAdd
argument_list|(
operator|new
name|DiscoveryEvent
argument_list|(
name|serviceName
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"expected added URI after discovery event"
argument_list|,
literal|1
argument_list|,
name|compositeTransport
operator|.
name|getTransportURIs
argument_list|()
operator|.
name|length
argument_list|)
expr_stmt|;
name|discoveryTransport
operator|.
name|onServiceRemove
argument_list|(
operator|new
name|DiscoveryEvent
argument_list|(
name|serviceName
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"expected URI removed after discovery event"
argument_list|,
literal|0
argument_list|,
name|compositeTransport
operator|.
name|getTransportURIs
argument_list|()
operator|.
name|length
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

