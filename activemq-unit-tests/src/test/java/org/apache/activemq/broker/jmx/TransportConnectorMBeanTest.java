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
name|broker
operator|.
name|jmx
package|;
end_package

begin_import
import|import static
name|junit
operator|.
name|framework
operator|.
name|TestCase
operator|.
name|assertTrue
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
name|assertEquals
import|;
end_import

begin_import
import|import
name|java
operator|.
name|net
operator|.
name|Socket
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Set
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
name|ActiveMQConnection
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
name|JMXSupport
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
name|After
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

begin_class
specifier|public
class|class
name|TransportConnectorMBeanTest
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
name|TransportConnectorMBeanTest
operator|.
name|class
argument_list|)
decl_stmt|;
name|BrokerService
name|broker
decl_stmt|;
annotation|@
name|Test
specifier|public
name|void
name|verifyRemoteAddressInMbeanName
parameter_list|()
throws|throws
name|Exception
block|{
name|doVerifyRemoteAddressInMbeanName
argument_list|(
literal|true
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|verifyRemoteAddressNotInMbeanName
parameter_list|()
throws|throws
name|Exception
block|{
name|doVerifyRemoteAddressInMbeanName
argument_list|(
literal|false
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|verifyClientIdNetwork
parameter_list|()
throws|throws
name|Exception
block|{
name|doVerifyClientIdNetwork
argument_list|(
literal|false
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|verifyClientIdDuplexNetwork
parameter_list|()
throws|throws
name|Exception
block|{
name|doVerifyClientIdNetwork
argument_list|(
literal|true
argument_list|)
expr_stmt|;
block|}
specifier|private
name|void
name|doVerifyClientIdNetwork
parameter_list|(
name|boolean
name|duplex
parameter_list|)
throws|throws
name|Exception
block|{
name|createBroker
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|BrokerService
name|networked
init|=
operator|new
name|BrokerService
argument_list|()
decl_stmt|;
name|networked
operator|.
name|setBrokerName
argument_list|(
literal|"networked"
argument_list|)
expr_stmt|;
name|networked
operator|.
name|setPersistent
argument_list|(
literal|false
argument_list|)
expr_stmt|;
name|NetworkConnector
name|nc
init|=
name|networked
operator|.
name|addNetworkConnector
argument_list|(
literal|"static:"
operator|+
name|broker
operator|.
name|getTransportConnectors
argument_list|()
operator|.
name|get
argument_list|(
literal|0
argument_list|)
operator|.
name|getPublishableConnectString
argument_list|()
argument_list|)
decl_stmt|;
name|nc
operator|.
name|setDuplex
argument_list|(
name|duplex
argument_list|)
expr_stmt|;
name|networked
operator|.
name|start
argument_list|()
expr_stmt|;
try|try
block|{
name|assertTrue
argument_list|(
literal|"presence of mbean with clientId"
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
name|Set
argument_list|<
name|ObjectName
argument_list|>
name|registeredMbeans
init|=
name|getRegisteredMbeans
argument_list|()
decl_stmt|;
return|return
name|match
argument_list|(
literal|"_outbound"
argument_list|,
name|registeredMbeans
argument_list|)
return|;
block|}
block|}
argument_list|)
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|networked
operator|.
name|stop
argument_list|()
expr_stmt|;
block|}
block|}
specifier|private
name|void
name|doVerifyRemoteAddressInMbeanName
parameter_list|(
name|boolean
name|allowRemoteAddress
parameter_list|)
throws|throws
name|Exception
block|{
name|createBroker
argument_list|(
name|allowRemoteAddress
argument_list|)
expr_stmt|;
name|ActiveMQConnection
name|connection
init|=
name|createConnection
argument_list|()
decl_stmt|;
name|Set
argument_list|<
name|ObjectName
argument_list|>
name|registeredMbeans
init|=
name|getRegisteredMbeans
argument_list|()
decl_stmt|;
name|assertEquals
argument_list|(
literal|"presence of mbean with clientId"
argument_list|,
literal|true
argument_list|,
name|match
argument_list|(
name|connection
operator|.
name|getClientID
argument_list|()
argument_list|,
name|registeredMbeans
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"presence of mbean with local port"
argument_list|,
name|allowRemoteAddress
argument_list|,
name|match
argument_list|(
name|extractLocalPort
argument_list|(
name|connection
argument_list|)
argument_list|,
name|registeredMbeans
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|After
specifier|public
name|void
name|stopBroker
parameter_list|()
throws|throws
name|Exception
block|{
if|if
condition|(
name|broker
operator|!=
literal|null
condition|)
block|{
name|broker
operator|.
name|stop
argument_list|()
expr_stmt|;
block|}
block|}
specifier|private
name|boolean
name|match
parameter_list|(
name|String
name|s
parameter_list|,
name|Set
argument_list|<
name|ObjectName
argument_list|>
name|registeredMbeans
parameter_list|)
block|{
name|String
name|encodedName
init|=
name|JMXSupport
operator|.
name|encodeObjectNamePart
argument_list|(
name|s
argument_list|)
decl_stmt|;
for|for
control|(
name|ObjectName
name|name
range|:
name|registeredMbeans
control|)
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"checking for match:"
operator|+
name|encodedName
operator|+
literal|", with: "
operator|+
name|name
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
if|if
condition|(
name|name
operator|.
name|toString
argument_list|()
operator|.
name|contains
argument_list|(
name|encodedName
argument_list|)
condition|)
block|{
return|return
literal|true
return|;
block|}
block|}
return|return
literal|false
return|;
block|}
specifier|private
name|String
name|extractLocalPort
parameter_list|(
name|ActiveMQConnection
name|connection
parameter_list|)
throws|throws
name|Exception
block|{
name|Socket
name|socket
init|=
name|connection
operator|.
name|getTransport
argument_list|()
operator|.
name|narrow
argument_list|(
name|Socket
operator|.
name|class
argument_list|)
decl_stmt|;
return|return
name|String
operator|.
name|valueOf
argument_list|(
name|socket
operator|.
name|getLocalPort
argument_list|()
argument_list|)
return|;
block|}
specifier|private
name|Set
argument_list|<
name|ObjectName
argument_list|>
name|getRegisteredMbeans
parameter_list|()
throws|throws
name|Exception
block|{
comment|// need a little sleep to ensure JMX is up to date
name|Thread
operator|.
name|sleep
argument_list|(
literal|200
argument_list|)
expr_stmt|;
return|return
name|broker
operator|.
name|getManagementContext
argument_list|()
operator|.
name|queryNames
argument_list|(
literal|null
argument_list|,
literal|null
argument_list|)
return|;
block|}
specifier|private
name|ActiveMQConnection
name|createConnection
parameter_list|()
throws|throws
name|Exception
block|{
specifier|final
name|String
name|opts
init|=
literal|"?jms.watchTopicAdvisories=false"
decl_stmt|;
name|ActiveMQConnection
name|connection
init|=
operator|(
name|ActiveMQConnection
operator|)
operator|new
name|ActiveMQConnectionFactory
argument_list|(
name|broker
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
operator|+
name|opts
argument_list|)
operator|.
name|createConnection
argument_list|()
decl_stmt|;
name|connection
operator|.
name|start
argument_list|()
expr_stmt|;
return|return
name|connection
return|;
block|}
specifier|private
name|void
name|createBroker
parameter_list|(
name|boolean
name|allowRemoteAddressInMbeanNames
parameter_list|)
throws|throws
name|Exception
block|{
name|broker
operator|=
operator|new
name|BrokerService
argument_list|()
expr_stmt|;
name|broker
operator|.
name|setPersistent
argument_list|(
literal|false
argument_list|)
expr_stmt|;
name|broker
operator|.
name|addConnector
argument_list|(
literal|"tcp://localhost:0"
argument_list|)
expr_stmt|;
name|broker
operator|.
name|getManagementContext
argument_list|()
operator|.
name|setAllowRemoteAddressInMBeanNames
argument_list|(
name|allowRemoteAddressInMbeanNames
argument_list|)
expr_stmt|;
name|broker
operator|.
name|start
argument_list|()
expr_stmt|;
block|}
block|}
end_class

end_unit

