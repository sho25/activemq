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
name|xbean
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
name|Connection
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
name|MessageProducer
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|jms
operator|.
name|Session
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
name|BrokerFactory
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
name|ActiveMQQueue
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
name|network
operator|.
name|NetworkConnector
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

begin_comment
comment|/**  * @version $Revision: 1.1 $  */
end_comment

begin_class
specifier|public
class|class
name|ConnectorXBeanConfigTest
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
name|ConnectorXBeanConfigTest
operator|.
name|class
argument_list|)
decl_stmt|;
specifier|protected
name|BrokerService
name|brokerService
decl_stmt|;
specifier|public
name|void
name|testConnectorConfiguredCorrectly
parameter_list|()
throws|throws
name|Exception
block|{
name|TransportConnector
name|connector
init|=
operator|(
name|TransportConnector
operator|)
name|brokerService
operator|.
name|getTransportConnectors
argument_list|()
operator|.
name|get
argument_list|(
literal|0
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
operator|new
name|URI
argument_list|(
literal|"tcp://localhost:61636"
argument_list|)
argument_list|,
name|connector
operator|.
name|getUri
argument_list|()
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|connector
operator|.
name|getTaskRunnerFactory
argument_list|()
operator|==
name|brokerService
operator|.
name|getTaskRunnerFactory
argument_list|()
argument_list|)
expr_stmt|;
name|NetworkConnector
name|netConnector
init|=
operator|(
name|NetworkConnector
operator|)
name|brokerService
operator|.
name|getNetworkConnectors
argument_list|()
operator|.
name|get
argument_list|(
literal|0
argument_list|)
decl_stmt|;
name|List
name|excludedDestinations
init|=
name|netConnector
operator|.
name|getExcludedDestinations
argument_list|()
decl_stmt|;
name|assertEquals
argument_list|(
operator|new
name|ActiveMQQueue
argument_list|(
literal|"exclude.test.foo"
argument_list|)
argument_list|,
name|excludedDestinations
operator|.
name|get
argument_list|(
literal|0
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
operator|new
name|ActiveMQTopic
argument_list|(
literal|"exclude.test.bar"
argument_list|)
argument_list|,
name|excludedDestinations
operator|.
name|get
argument_list|(
literal|1
argument_list|)
argument_list|)
expr_stmt|;
name|List
name|dynamicallyIncludedDestinations
init|=
name|netConnector
operator|.
name|getDynamicallyIncludedDestinations
argument_list|()
decl_stmt|;
name|assertEquals
argument_list|(
operator|new
name|ActiveMQQueue
argument_list|(
literal|"include.test.foo"
argument_list|)
argument_list|,
name|dynamicallyIncludedDestinations
operator|.
name|get
argument_list|(
literal|0
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
operator|new
name|ActiveMQTopic
argument_list|(
literal|"include.test.bar"
argument_list|)
argument_list|,
name|dynamicallyIncludedDestinations
operator|.
name|get
argument_list|(
literal|1
argument_list|)
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|testBrokerRestartIsAllowed
parameter_list|()
throws|throws
name|Exception
block|{
name|brokerService
operator|.
name|stop
argument_list|()
expr_stmt|;
name|brokerService
operator|.
name|waitUntilStopped
argument_list|()
expr_stmt|;
comment|// redundant start is now ignored
name|brokerService
operator|.
name|start
argument_list|()
expr_stmt|;
block|}
specifier|public
name|void
name|testForceBrokerRestart
parameter_list|()
throws|throws
name|Exception
block|{
name|brokerService
operator|.
name|stop
argument_list|()
expr_stmt|;
name|brokerService
operator|.
name|waitUntilStopped
argument_list|()
expr_stmt|;
name|brokerService
operator|.
name|start
argument_list|(
literal|true
argument_list|)
expr_stmt|;
comment|// force restart
name|brokerService
operator|.
name|waitUntilStarted
argument_list|()
expr_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"try and connect to restarted broker"
argument_list|)
expr_stmt|;
comment|//send and receive a message from a restarted broker
name|ActiveMQConnectionFactory
name|factory
init|=
operator|new
name|ActiveMQConnectionFactory
argument_list|(
literal|"tcp://localhost:61636"
argument_list|)
decl_stmt|;
name|Connection
name|conn
init|=
name|factory
operator|.
name|createConnection
argument_list|()
decl_stmt|;
name|Session
name|sess
init|=
name|conn
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
name|conn
operator|.
name|start
argument_list|()
expr_stmt|;
name|Destination
name|dest
init|=
operator|new
name|ActiveMQQueue
argument_list|(
literal|"test"
argument_list|)
decl_stmt|;
name|MessageProducer
name|producer
init|=
name|sess
operator|.
name|createProducer
argument_list|(
name|dest
argument_list|)
decl_stmt|;
name|MessageConsumer
name|consumer
init|=
name|sess
operator|.
name|createConsumer
argument_list|(
name|dest
argument_list|)
decl_stmt|;
name|producer
operator|.
name|send
argument_list|(
name|sess
operator|.
name|createTextMessage
argument_list|(
literal|"test"
argument_list|)
argument_list|)
expr_stmt|;
name|TextMessage
name|msg
init|=
operator|(
name|TextMessage
operator|)
name|consumer
operator|.
name|receive
argument_list|(
literal|1000
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|"test"
argument_list|,
name|msg
operator|.
name|getText
argument_list|()
argument_list|)
expr_stmt|;
block|}
specifier|protected
name|void
name|setUp
parameter_list|()
throws|throws
name|Exception
block|{
name|brokerService
operator|=
name|createBroker
argument_list|()
expr_stmt|;
name|brokerService
operator|.
name|start
argument_list|()
expr_stmt|;
block|}
specifier|protected
name|void
name|tearDown
parameter_list|()
throws|throws
name|Exception
block|{
if|if
condition|(
name|brokerService
operator|!=
literal|null
condition|)
block|{
name|brokerService
operator|.
name|stop
argument_list|()
expr_stmt|;
block|}
block|}
specifier|protected
name|BrokerService
name|createBroker
parameter_list|()
throws|throws
name|Exception
block|{
name|String
name|uri
init|=
literal|"org/apache/activemq/xbean/connector-test.xml"
decl_stmt|;
return|return
name|BrokerFactory
operator|.
name|createBroker
argument_list|(
operator|new
name|URI
argument_list|(
literal|"xbean:"
operator|+
name|uri
argument_list|)
argument_list|)
return|;
block|}
block|}
end_class

end_unit

