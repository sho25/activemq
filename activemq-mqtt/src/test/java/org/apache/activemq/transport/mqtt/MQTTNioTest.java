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
name|mqtt
package|;
end_package

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|LinkedList
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
name|BrokerPlugin
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
name|ActiveMQTopic
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
name|filter
operator|.
name|DestinationMapEntry
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
name|security
operator|.
name|AuthenticationUser
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
name|security
operator|.
name|AuthorizationEntry
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
name|security
operator|.
name|AuthorizationPlugin
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
name|security
operator|.
name|DefaultAuthorizationMap
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
name|security
operator|.
name|SimpleAuthenticationPlugin
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
name|fusesource
operator|.
name|mqtt
operator|.
name|client
operator|.
name|BlockingConnection
import|;
end_import

begin_import
import|import
name|org
operator|.
name|fusesource
operator|.
name|mqtt
operator|.
name|client
operator|.
name|MQTT
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
name|Rule
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
name|rules
operator|.
name|TestName
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
name|junit
operator|.
name|runners
operator|.
name|BlockJUnit4ClassRunner
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
name|BlockJUnit4ClassRunner
operator|.
name|class
argument_list|)
specifier|public
class|class
name|MQTTNioTest
extends|extends
name|MQTTTest
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
name|MQTTNioTest
operator|.
name|class
argument_list|)
decl_stmt|;
annotation|@
name|Rule
specifier|public
name|TestName
name|testname
init|=
operator|new
name|TestName
argument_list|()
decl_stmt|;
annotation|@
name|Before
specifier|public
name|void
name|setUp
parameter_list|()
throws|throws
name|Exception
block|{
name|super
operator|.
name|setUp
argument_list|()
expr_stmt|;
name|LOG
operator|.
name|debug
argument_list|(
literal|"Starting {}"
argument_list|,
name|testname
operator|.
name|getMethodName
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|protected
name|String
name|getProtocolScheme
parameter_list|()
block|{
return|return
literal|"mqtt+nio"
return|;
block|}
annotation|@
name|Test
argument_list|(
name|timeout
operator|=
literal|60
operator|*
literal|1000
argument_list|)
specifier|public
name|void
name|testPingOnMQTTNIO
parameter_list|()
throws|throws
name|Exception
block|{
name|addMQTTConnector
argument_list|(
literal|"maxInactivityDuration=-1"
argument_list|)
expr_stmt|;
name|brokerService
operator|.
name|start
argument_list|()
expr_stmt|;
name|MQTT
name|mqtt
init|=
name|createMQTTConnection
argument_list|()
decl_stmt|;
name|mqtt
operator|.
name|setClientId
argument_list|(
literal|"test-mqtt"
argument_list|)
expr_stmt|;
name|mqtt
operator|.
name|setKeepAlive
argument_list|(
operator|(
name|short
operator|)
literal|2
argument_list|)
expr_stmt|;
specifier|final
name|BlockingConnection
name|connection
init|=
name|mqtt
operator|.
name|blockingConnection
argument_list|()
decl_stmt|;
name|connection
operator|.
name|connect
argument_list|()
expr_stmt|;
name|assertTrue
argument_list|(
literal|"KeepAlive didn't work properly"
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
name|connection
operator|.
name|isConnected
argument_list|()
return|;
block|}
block|}
argument_list|)
argument_list|)
expr_stmt|;
name|connection
operator|.
name|disconnect
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
argument_list|(
name|timeout
operator|=
literal|60
operator|*
literal|1000
argument_list|)
specifier|public
name|void
name|testAnonymousUserConnect
parameter_list|()
throws|throws
name|Exception
block|{
name|addMQTTConnector
argument_list|()
expr_stmt|;
name|configureAuthentication
argument_list|(
name|brokerService
argument_list|)
expr_stmt|;
name|brokerService
operator|.
name|start
argument_list|()
expr_stmt|;
name|brokerService
operator|.
name|waitUntilStarted
argument_list|()
expr_stmt|;
name|MQTT
name|mqtt
init|=
name|createMQTTConnection
argument_list|()
decl_stmt|;
name|mqtt
operator|.
name|setCleanSession
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|mqtt
operator|.
name|setUserName
argument_list|(
operator|(
name|String
operator|)
literal|null
argument_list|)
expr_stmt|;
name|mqtt
operator|.
name|setPassword
argument_list|(
operator|(
name|String
operator|)
literal|null
argument_list|)
expr_stmt|;
specifier|final
name|BlockingConnection
name|connection
init|=
name|mqtt
operator|.
name|blockingConnection
argument_list|()
decl_stmt|;
name|connection
operator|.
name|connect
argument_list|()
expr_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"Connected!"
argument_list|)
expr_stmt|;
name|connection
operator|.
name|disconnect
argument_list|()
expr_stmt|;
block|}
specifier|private
name|void
name|configureAuthentication
parameter_list|(
name|BrokerService
name|brokerService
parameter_list|)
throws|throws
name|Exception
block|{
name|LinkedList
argument_list|<
name|AuthenticationUser
argument_list|>
name|users
init|=
operator|new
name|LinkedList
argument_list|<
name|AuthenticationUser
argument_list|>
argument_list|()
decl_stmt|;
name|users
operator|.
name|add
argument_list|(
operator|new
name|AuthenticationUser
argument_list|(
literal|"user1"
argument_list|,
literal|"user1"
argument_list|,
literal|"anonymous,user1group"
argument_list|)
argument_list|)
expr_stmt|;
specifier|final
name|SimpleAuthenticationPlugin
name|authenticationPlugin
init|=
operator|new
name|SimpleAuthenticationPlugin
argument_list|(
name|users
argument_list|)
decl_stmt|;
name|DefaultAuthorizationMap
name|map
init|=
operator|new
name|DefaultAuthorizationMap
argument_list|()
decl_stmt|;
name|LinkedList
argument_list|<
name|DestinationMapEntry
argument_list|>
name|authz
init|=
operator|new
name|LinkedList
argument_list|<
name|DestinationMapEntry
argument_list|>
argument_list|()
decl_stmt|;
name|AuthorizationEntry
name|entry
init|=
operator|new
name|AuthorizationEntry
argument_list|()
decl_stmt|;
name|entry
operator|.
name|setDestination
argument_list|(
operator|new
name|ActiveMQTopic
argument_list|(
literal|">"
argument_list|)
argument_list|)
expr_stmt|;
name|entry
operator|.
name|setAdmin
argument_list|(
literal|"admins"
argument_list|)
expr_stmt|;
name|entry
operator|.
name|setRead
argument_list|(
literal|"admins,anonymous"
argument_list|)
expr_stmt|;
name|entry
operator|.
name|setWrite
argument_list|(
literal|"admins"
argument_list|)
expr_stmt|;
name|authz
operator|.
name|add
argument_list|(
name|entry
argument_list|)
expr_stmt|;
name|map
operator|.
name|setAuthorizationEntries
argument_list|(
name|authz
argument_list|)
expr_stmt|;
name|AuthorizationPlugin
name|authorizationPlugin
init|=
operator|new
name|AuthorizationPlugin
argument_list|(
name|map
argument_list|)
decl_stmt|;
name|authenticationPlugin
operator|.
name|setAnonymousAccessAllowed
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|brokerService
operator|.
name|setPlugins
argument_list|(
operator|new
name|BrokerPlugin
index|[]
block|{
name|authenticationPlugin
block|,
name|authorizationPlugin
block|}
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

