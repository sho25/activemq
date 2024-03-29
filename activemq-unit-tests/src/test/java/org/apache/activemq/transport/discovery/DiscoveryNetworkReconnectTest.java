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
name|concurrent
operator|.
name|Semaphore
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
name|TimeUnit
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|management
operator|.
name|ObjectInstance
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|management
operator|.
name|ObjectName
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
name|jmx
operator|.
name|ManagementContext
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
name|discovery
operator|.
name|multicast
operator|.
name|MulticastDiscoveryAgentFactory
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
name|hamcrest
operator|.
name|BaseMatcher
import|;
end_import

begin_import
import|import
name|org
operator|.
name|hamcrest
operator|.
name|Description
import|;
end_import

begin_import
import|import
name|org
operator|.
name|jmock
operator|.
name|Expectations
import|;
end_import

begin_import
import|import
name|org
operator|.
name|jmock
operator|.
name|Mockery
import|;
end_import

begin_import
import|import
name|org
operator|.
name|jmock
operator|.
name|api
operator|.
name|Invocation
import|;
end_import

begin_import
import|import
name|org
operator|.
name|jmock
operator|.
name|integration
operator|.
name|junit4
operator|.
name|JMock
import|;
end_import

begin_import
import|import
name|org
operator|.
name|jmock
operator|.
name|integration
operator|.
name|junit4
operator|.
name|JUnit4Mockery
import|;
end_import

begin_import
import|import
name|org
operator|.
name|jmock
operator|.
name|lib
operator|.
name|action
operator|.
name|CustomAction
import|;
end_import

begin_import
import|import
name|org
operator|.
name|jmock
operator|.
name|lib
operator|.
name|legacy
operator|.
name|ClassImposteriser
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|After
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|Before
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
name|junit
operator|.
name|runner
operator|.
name|RunWith
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
annotation|@
name|RunWith
argument_list|(
name|JMock
operator|.
name|class
argument_list|)
specifier|public
class|class
name|DiscoveryNetworkReconnectTest
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
name|DiscoveryNetworkReconnectTest
operator|.
name|class
argument_list|)
decl_stmt|;
specifier|final
name|int
name|maxReconnects
init|=
literal|2
decl_stmt|;
specifier|final
name|String
name|groupName
init|=
literal|"GroupID-"
operator|+
literal|"DiscoveryNetworkReconnectTest"
decl_stmt|;
specifier|final
name|String
name|discoveryAddress
init|=
literal|"multicast://default?group="
operator|+
name|groupName
operator|+
literal|"&initialReconnectDelay=1000"
decl_stmt|;
specifier|final
name|Semaphore
name|mbeanRegistered
init|=
operator|new
name|Semaphore
argument_list|(
literal|0
argument_list|)
decl_stmt|;
specifier|final
name|Semaphore
name|mbeanUnregistered
init|=
operator|new
name|Semaphore
argument_list|(
literal|0
argument_list|)
decl_stmt|;
name|BrokerService
name|brokerA
decl_stmt|,
name|brokerB
decl_stmt|;
name|Mockery
name|context
decl_stmt|;
name|ManagementContext
name|managementContext
decl_stmt|;
name|DiscoveryAgent
name|agent
decl_stmt|;
name|SocketProxy
name|proxy
decl_stmt|;
comment|// ignore the hostname resolution component as this is machine dependent
class|class
name|NetworkBridgeObjectNameMatcher
parameter_list|<
name|T
parameter_list|>
extends|extends
name|BaseMatcher
argument_list|<
name|T
argument_list|>
block|{
name|T
name|name
decl_stmt|;
name|NetworkBridgeObjectNameMatcher
parameter_list|(
name|T
name|o
parameter_list|)
block|{
name|name
operator|=
name|o
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|matches
parameter_list|(
name|Object
name|arg0
parameter_list|)
block|{
name|ObjectName
name|other
init|=
operator|(
name|ObjectName
operator|)
name|arg0
decl_stmt|;
name|ObjectName
name|mine
init|=
operator|(
name|ObjectName
operator|)
name|name
decl_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"Match: "
operator|+
name|mine
operator|+
literal|" vs: "
operator|+
name|other
argument_list|)
expr_stmt|;
if|if
condition|(
operator|!
literal|"networkConnectors"
operator|.
name|equals
argument_list|(
name|other
operator|.
name|getKeyProperty
argument_list|(
literal|"connector"
argument_list|)
argument_list|)
condition|)
block|{
return|return
literal|false
return|;
block|}
return|return
name|other
operator|.
name|getKeyProperty
argument_list|(
literal|"connector"
argument_list|)
operator|.
name|equals
argument_list|(
name|mine
operator|.
name|getKeyProperty
argument_list|(
literal|"connector"
argument_list|)
argument_list|)
operator|&&
name|other
operator|.
name|getKeyProperty
argument_list|(
literal|"networkBridge"
argument_list|)
operator|!=
literal|null
operator|&&
name|mine
operator|.
name|getKeyProperty
argument_list|(
literal|"networkBridge"
argument_list|)
operator|!=
literal|null
return|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|describeTo
parameter_list|(
name|Description
name|arg0
parameter_list|)
block|{
name|arg0
operator|.
name|appendText
argument_list|(
name|this
operator|.
name|getClass
argument_list|()
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Before
specifier|public
name|void
name|setUp
parameter_list|()
throws|throws
name|Exception
block|{
name|context
operator|=
operator|new
name|JUnit4Mockery
argument_list|()
block|{
block|{
name|setImposteriser
parameter_list|(
name|ClassImposteriser
operator|.
name|INSTANCE
parameter_list|)
constructor_decl|;
block|}
block|}
expr_stmt|;
name|brokerA
operator|=
operator|new
name|BrokerService
argument_list|()
expr_stmt|;
name|brokerA
operator|.
name|setBrokerName
argument_list|(
literal|"BrokerA"
argument_list|)
expr_stmt|;
name|configure
argument_list|(
name|brokerA
argument_list|)
expr_stmt|;
name|brokerA
operator|.
name|addConnector
argument_list|(
literal|"tcp://localhost:0"
argument_list|)
expr_stmt|;
name|brokerA
operator|.
name|start
argument_list|()
expr_stmt|;
name|brokerA
operator|.
name|waitUntilStarted
argument_list|()
expr_stmt|;
name|proxy
operator|=
operator|new
name|SocketProxy
argument_list|(
name|brokerA
operator|.
name|getTransportConnectors
argument_list|()
operator|.
name|get
argument_list|(
literal|0
argument_list|)
operator|.
name|getConnectUri
argument_list|()
argument_list|)
expr_stmt|;
name|managementContext
operator|=
name|context
operator|.
name|mock
argument_list|(
name|ManagementContext
operator|.
name|class
argument_list|)
expr_stmt|;
name|context
operator|.
name|checking
argument_list|(
operator|new
name|Expectations
argument_list|()
block|{
block|{
name|allowing
argument_list|(
name|managementContext
argument_list|)
operator|.
name|getJmxDomainName
argument_list|()
expr_stmt|;
name|will
argument_list|(
name|returnValue
argument_list|(
literal|"Test"
argument_list|)
argument_list|)
expr_stmt|;
name|allowing
argument_list|(
name|managementContext
argument_list|)
operator|.
name|setBrokerName
argument_list|(
literal|"BrokerNC"
argument_list|)
expr_stmt|;
name|allowing
argument_list|(
name|managementContext
argument_list|)
operator|.
name|start
argument_list|()
expr_stmt|;
name|allowing
argument_list|(
name|managementContext
argument_list|)
operator|.
name|isCreateConnector
argument_list|()
expr_stmt|;
name|allowing
argument_list|(
name|managementContext
argument_list|)
operator|.
name|stop
argument_list|()
expr_stmt|;
name|allowing
argument_list|(
name|managementContext
argument_list|)
operator|.
name|isConnectorStarted
argument_list|()
expr_stmt|;
comment|// expected MBeans
name|allowing
argument_list|(
name|managementContext
argument_list|)
operator|.
name|registerMBean
argument_list|(
name|with
argument_list|(
name|any
argument_list|(
name|Object
operator|.
name|class
argument_list|)
argument_list|)
argument_list|,
name|with
argument_list|(
name|equal
argument_list|(
operator|new
name|ObjectName
argument_list|(
literal|"Test:type=Broker,brokerName=BrokerNC"
argument_list|)
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|allowing
argument_list|(
name|managementContext
argument_list|)
operator|.
name|registerMBean
argument_list|(
name|with
argument_list|(
name|any
argument_list|(
name|Object
operator|.
name|class
argument_list|)
argument_list|)
argument_list|,
name|with
argument_list|(
name|equal
argument_list|(
operator|new
name|ObjectName
argument_list|(
literal|"Test:type=Broker,brokerName=BrokerNC,service=Health"
argument_list|)
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|allowing
argument_list|(
name|managementContext
argument_list|)
operator|.
name|registerMBean
argument_list|(
name|with
argument_list|(
name|any
argument_list|(
name|Object
operator|.
name|class
argument_list|)
argument_list|)
argument_list|,
name|with
argument_list|(
name|equal
argument_list|(
operator|new
name|ObjectName
argument_list|(
literal|"Test:type=Broker,brokerName=BrokerNC,connector=networkConnectors,networkConnectorName=NC"
argument_list|)
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|allowing
argument_list|(
name|managementContext
argument_list|)
operator|.
name|registerMBean
argument_list|(
name|with
argument_list|(
name|any
argument_list|(
name|Object
operator|.
name|class
argument_list|)
argument_list|)
argument_list|,
name|with
argument_list|(
name|equal
argument_list|(
operator|new
name|ObjectName
argument_list|(
literal|"Test:type=Broker,brokerName=BrokerNC,service=Log4JConfiguration"
argument_list|)
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|allowing
argument_list|(
name|managementContext
argument_list|)
operator|.
name|registerMBean
argument_list|(
name|with
argument_list|(
name|any
argument_list|(
name|Object
operator|.
name|class
argument_list|)
argument_list|)
argument_list|,
name|with
argument_list|(
name|equal
argument_list|(
operator|new
name|ObjectName
argument_list|(
literal|"Test:type=Broker,brokerName=BrokerNC,destinationType=Topic,destinationName=ActiveMQ.Advisory.Connection"
argument_list|)
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|allowing
argument_list|(
name|managementContext
argument_list|)
operator|.
name|registerMBean
argument_list|(
name|with
argument_list|(
name|any
argument_list|(
name|Object
operator|.
name|class
argument_list|)
argument_list|)
argument_list|,
name|with
argument_list|(
name|equal
argument_list|(
operator|new
name|ObjectName
argument_list|(
literal|"Test:type=Broker,brokerName=BrokerNC,destinationType=Topic,destinationName=ActiveMQ.Advisory.NetworkBridge"
argument_list|)
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|allowing
argument_list|(
name|managementContext
argument_list|)
operator|.
name|registerMBean
argument_list|(
name|with
argument_list|(
name|any
argument_list|(
name|Object
operator|.
name|class
argument_list|)
argument_list|)
argument_list|,
name|with
argument_list|(
name|equal
argument_list|(
operator|new
name|ObjectName
argument_list|(
literal|"Test:type=Broker,brokerName=BrokerNC,destinationType=Topic,destinationName=ActiveMQ.Advisory.MasterBroker"
argument_list|)
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|allowing
argument_list|(
name|managementContext
argument_list|)
operator|.
name|registerMBean
argument_list|(
name|with
argument_list|(
name|any
argument_list|(
name|Object
operator|.
name|class
argument_list|)
argument_list|)
argument_list|,
name|with
argument_list|(
name|equal
argument_list|(
operator|new
name|ObjectName
argument_list|(
literal|"Test:type=Broker,brokerName=BrokerNC,service=jobScheduler,jobSchedulerName=JMS"
argument_list|)
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|allowing
argument_list|(
name|managementContext
argument_list|)
operator|.
name|getObjectInstance
argument_list|(
name|with
argument_list|(
name|equal
argument_list|(
operator|new
name|ObjectName
argument_list|(
literal|"Test:type=Broker,brokerName=BrokerNC,connector=networkConnectors,networkConnectorName=NC"
argument_list|)
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|atLeast
argument_list|(
name|maxReconnects
operator|-
literal|1
argument_list|)
operator|.
name|of
argument_list|(
name|managementContext
argument_list|)
operator|.
name|registerMBean
argument_list|(
name|with
argument_list|(
name|any
argument_list|(
name|Object
operator|.
name|class
argument_list|)
argument_list|)
argument_list|,
name|with
argument_list|(
operator|new
name|NetworkBridgeObjectNameMatcher
argument_list|<
name|ObjectName
argument_list|>
argument_list|(
operator|new
name|ObjectName
argument_list|(
literal|"Test:type=Broker,brokerName=BrokerNC,connector=networkConnectors,networkConnectorName=NC,networkBridge=localhost/127.0.0.1_"
operator|+
name|proxy
operator|.
name|getUrl
argument_list|()
operator|.
name|getPort
argument_list|()
argument_list|)
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|will
argument_list|(
operator|new
name|CustomAction
argument_list|(
literal|"signal register network mbean"
argument_list|)
block|{
annotation|@
name|Override
specifier|public
name|Object
name|invoke
parameter_list|(
name|Invocation
name|invocation
parameter_list|)
throws|throws
name|Throwable
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"Mbean Registered: "
operator|+
name|invocation
operator|.
name|getParameter
argument_list|(
literal|0
argument_list|)
argument_list|)
expr_stmt|;
name|mbeanRegistered
operator|.
name|release
argument_list|()
expr_stmt|;
return|return
operator|new
name|ObjectInstance
argument_list|(
operator|(
name|ObjectName
operator|)
name|invocation
operator|.
name|getParameter
argument_list|(
literal|1
argument_list|)
argument_list|,
literal|"discription"
argument_list|)
return|;
block|}
block|}
argument_list|)
expr_stmt|;
name|atLeast
argument_list|(
name|maxReconnects
operator|-
literal|1
argument_list|)
operator|.
name|of
argument_list|(
name|managementContext
argument_list|)
operator|.
name|unregisterMBean
argument_list|(
name|with
argument_list|(
operator|new
name|NetworkBridgeObjectNameMatcher
argument_list|<
name|ObjectName
argument_list|>
argument_list|(
operator|new
name|ObjectName
argument_list|(
literal|"Test:type=Broker,brokerName=BrokerNC,connector=networkConnectors,networkConnectorName=NC,networkBridge=localhost/127.0.0.1_"
operator|+
name|proxy
operator|.
name|getUrl
argument_list|()
operator|.
name|getPort
argument_list|()
argument_list|)
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|will
argument_list|(
operator|new
name|CustomAction
argument_list|(
literal|"signal unregister network mbean"
argument_list|)
block|{
annotation|@
name|Override
specifier|public
name|Object
name|invoke
parameter_list|(
name|Invocation
name|invocation
parameter_list|)
throws|throws
name|Throwable
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"Mbean Unregistered: "
operator|+
name|invocation
operator|.
name|getParameter
argument_list|(
literal|0
argument_list|)
argument_list|)
expr_stmt|;
name|mbeanUnregistered
operator|.
name|release
argument_list|()
expr_stmt|;
return|return
literal|null
return|;
block|}
block|}
argument_list|)
expr_stmt|;
name|allowing
argument_list|(
name|managementContext
argument_list|)
operator|.
name|unregisterMBean
argument_list|(
name|with
argument_list|(
name|equal
argument_list|(
operator|new
name|ObjectName
argument_list|(
literal|"Test:type=Broker,brokerName=BrokerNC"
argument_list|)
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|allowing
argument_list|(
name|managementContext
argument_list|)
operator|.
name|unregisterMBean
argument_list|(
name|with
argument_list|(
name|equal
argument_list|(
operator|new
name|ObjectName
argument_list|(
literal|"Test:type=Broker,brokerName=BrokerNC,service=Health"
argument_list|)
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|allowing
argument_list|(
name|managementContext
argument_list|)
operator|.
name|unregisterMBean
argument_list|(
name|with
argument_list|(
name|equal
argument_list|(
operator|new
name|ObjectName
argument_list|(
literal|"Test:type=Broker,brokerName=BrokerNC,connector=networkConnectors,networkConnectorName=NC"
argument_list|)
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|allowing
argument_list|(
name|managementContext
argument_list|)
operator|.
name|unregisterMBean
argument_list|(
name|with
argument_list|(
name|equal
argument_list|(
operator|new
name|ObjectName
argument_list|(
literal|"Test:type=Broker,brokerName=BrokerNC,service=Log4JConfiguration"
argument_list|)
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|allowing
argument_list|(
name|managementContext
argument_list|)
operator|.
name|unregisterMBean
argument_list|(
name|with
argument_list|(
name|equal
argument_list|(
operator|new
name|ObjectName
argument_list|(
literal|"Test:type=Broker,brokerName=BrokerNC,destinationType=Topic,destinationName=ActiveMQ.Advisory.Connection"
argument_list|)
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|allowing
argument_list|(
name|managementContext
argument_list|)
operator|.
name|unregisterMBean
argument_list|(
name|with
argument_list|(
name|equal
argument_list|(
operator|new
name|ObjectName
argument_list|(
literal|"Test:type=Broker,brokerName=BrokerNC,destinationType=Topic,destinationName=ActiveMQ.Advisory.NetworkBridge"
argument_list|)
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|allowing
argument_list|(
name|managementContext
argument_list|)
operator|.
name|unregisterMBean
argument_list|(
name|with
argument_list|(
name|equal
argument_list|(
operator|new
name|ObjectName
argument_list|(
literal|"Test:type=Broker,brokerName=BrokerNC,destinationType=Topic,destinationName=ActiveMQ.Advisory.MasterBroker"
argument_list|)
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
argument_list|)
expr_stmt|;
name|brokerB
operator|=
operator|new
name|BrokerService
argument_list|()
expr_stmt|;
name|brokerB
operator|.
name|setManagementContext
argument_list|(
name|managementContext
argument_list|)
expr_stmt|;
name|brokerB
operator|.
name|setBrokerName
argument_list|(
literal|"BrokerNC"
argument_list|)
expr_stmt|;
name|configure
argument_list|(
name|brokerB
argument_list|)
expr_stmt|;
block|}
annotation|@
name|After
specifier|public
name|void
name|tearDown
parameter_list|()
throws|throws
name|Exception
block|{
name|brokerA
operator|.
name|stop
argument_list|()
expr_stmt|;
name|brokerA
operator|.
name|waitUntilStopped
argument_list|()
expr_stmt|;
name|brokerB
operator|.
name|stop
argument_list|()
expr_stmt|;
name|brokerB
operator|.
name|waitUntilStopped
argument_list|()
expr_stmt|;
name|proxy
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
specifier|private
name|void
name|configure
parameter_list|(
name|BrokerService
name|broker
parameter_list|)
block|{
name|broker
operator|.
name|setPersistent
argument_list|(
literal|false
argument_list|)
expr_stmt|;
name|broker
operator|.
name|setUseJmx
argument_list|(
literal|true
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testMulicastReconnect
parameter_list|()
throws|throws
name|Exception
block|{
name|brokerB
operator|.
name|addNetworkConnector
argument_list|(
name|discoveryAddress
operator|+
literal|"&discovered.trace=true&discovered.wireFormat.maxInactivityDuration=1000&discovered.wireFormat.maxInactivityDurationInitalDelay=1000"
argument_list|)
expr_stmt|;
name|brokerB
operator|.
name|start
argument_list|()
expr_stmt|;
name|brokerB
operator|.
name|waitUntilStarted
argument_list|()
expr_stmt|;
comment|// control multicast advertise agent to inject proxy
name|agent
operator|=
name|MulticastDiscoveryAgentFactory
operator|.
name|createDiscoveryAgent
argument_list|(
operator|new
name|URI
argument_list|(
name|discoveryAddress
argument_list|)
argument_list|)
expr_stmt|;
name|agent
operator|.
name|registerService
argument_list|(
name|proxy
operator|.
name|getUrl
argument_list|()
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|agent
operator|.
name|start
argument_list|()
expr_stmt|;
name|doReconnect
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testSimpleReconnect
parameter_list|()
throws|throws
name|Exception
block|{
name|brokerB
operator|.
name|addNetworkConnector
argument_list|(
literal|"simple://("
operator|+
name|proxy
operator|.
name|getUrl
argument_list|()
operator|+
literal|")?useExponentialBackOff=false&initialReconnectDelay=500&discovered.wireFormat.maxInactivityDuration=1000&discovered.wireFormat.maxInactivityDurationInitalDelay=1000"
argument_list|)
expr_stmt|;
name|brokerB
operator|.
name|start
argument_list|()
expr_stmt|;
name|brokerB
operator|.
name|waitUntilStarted
argument_list|()
expr_stmt|;
name|doReconnect
argument_list|()
expr_stmt|;
block|}
specifier|private
name|void
name|doReconnect
parameter_list|()
throws|throws
name|Exception
block|{
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|maxReconnects
condition|;
name|i
operator|++
control|)
block|{
comment|// Wait for connection
name|assertTrue
argument_list|(
literal|"we got a network connection in a timely manner"
argument_list|,
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
name|proxy
operator|.
name|connections
operator|.
name|size
argument_list|()
operator|>=
literal|1
return|;
block|}
block|}
argument_list|)
argument_list|)
expr_stmt|;
comment|// wait for network connector
name|assertTrue
argument_list|(
literal|"network connector mbean registered within 1 minute"
argument_list|,
name|mbeanRegistered
operator|.
name|tryAcquire
argument_list|(
literal|60
argument_list|,
name|TimeUnit
operator|.
name|SECONDS
argument_list|)
argument_list|)
expr_stmt|;
comment|// force an inactivity timeout via the proxy
name|proxy
operator|.
name|pause
argument_list|()
expr_stmt|;
comment|// wait for the inactivity timeout and network shutdown
name|assertTrue
argument_list|(
literal|"network connector mbean unregistered within 1 minute"
argument_list|,
name|mbeanUnregistered
operator|.
name|tryAcquire
argument_list|(
literal|60
argument_list|,
name|TimeUnit
operator|.
name|SECONDS
argument_list|)
argument_list|)
expr_stmt|;
comment|// whack all connections
name|proxy
operator|.
name|close
argument_list|()
expr_stmt|;
comment|// let a reconnect succeed
name|proxy
operator|.
name|reopen
argument_list|()
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

