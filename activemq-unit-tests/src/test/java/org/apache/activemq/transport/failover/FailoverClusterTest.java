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
name|failover
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
name|ArrayList
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|HashSet
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
name|Set
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
name|MessageConsumer
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|jms
operator|.
name|Queue
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
name|NetworkConnector
import|;
end_import

begin_class
specifier|public
class|class
name|FailoverClusterTest
extends|extends
name|TestCase
block|{
specifier|private
specifier|static
specifier|final
name|int
name|NUMBER
init|=
literal|10
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|String
name|BROKER_BIND_ADDRESS
init|=
literal|"tcp://0.0.0.0:0"
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|String
name|BROKER_A_NAME
init|=
literal|"BROKERA"
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|String
name|BROKER_B_NAME
init|=
literal|"BROKERB"
decl_stmt|;
specifier|private
name|BrokerService
name|brokerA
decl_stmt|;
specifier|private
name|BrokerService
name|brokerB
decl_stmt|;
specifier|private
name|String
name|clientUrl
decl_stmt|;
specifier|private
specifier|final
name|List
argument_list|<
name|ActiveMQConnection
argument_list|>
name|connections
init|=
operator|new
name|ArrayList
argument_list|<
name|ActiveMQConnection
argument_list|>
argument_list|()
decl_stmt|;
specifier|protected
name|String
name|getBindAddress
parameter_list|()
block|{
return|return
name|BROKER_BIND_ADDRESS
return|;
block|}
specifier|public
name|void
name|testClusterConnectedAfterClients
parameter_list|()
throws|throws
name|Exception
block|{
name|createClients
argument_list|()
expr_stmt|;
if|if
condition|(
name|brokerB
operator|==
literal|null
condition|)
block|{
name|brokerB
operator|=
name|createBrokerB
argument_list|(
name|getBindAddress
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|Thread
operator|.
name|sleep
argument_list|(
literal|3000
argument_list|)
expr_stmt|;
name|Set
argument_list|<
name|String
argument_list|>
name|set
init|=
operator|new
name|HashSet
argument_list|<
name|String
argument_list|>
argument_list|()
decl_stmt|;
for|for
control|(
name|ActiveMQConnection
name|c
range|:
name|connections
control|)
block|{
name|set
operator|.
name|add
argument_list|(
name|c
operator|.
name|getTransportChannel
argument_list|()
operator|.
name|getRemoteAddress
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|assertTrue
argument_list|(
name|set
operator|.
name|size
argument_list|()
operator|>
literal|1
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|testClusterURIOptionsStrip
parameter_list|()
throws|throws
name|Exception
block|{
name|createClients
argument_list|()
expr_stmt|;
if|if
condition|(
name|brokerB
operator|==
literal|null
condition|)
block|{
comment|// add in server side only url param, should not be propagated
name|brokerB
operator|=
name|createBrokerB
argument_list|(
name|getBindAddress
argument_list|()
operator|+
literal|"?transport.closeAsync=false"
argument_list|)
expr_stmt|;
block|}
name|Thread
operator|.
name|sleep
argument_list|(
literal|3000
argument_list|)
expr_stmt|;
name|Set
argument_list|<
name|String
argument_list|>
name|set
init|=
operator|new
name|HashSet
argument_list|<
name|String
argument_list|>
argument_list|()
decl_stmt|;
for|for
control|(
name|ActiveMQConnection
name|c
range|:
name|connections
control|)
block|{
name|set
operator|.
name|add
argument_list|(
name|c
operator|.
name|getTransportChannel
argument_list|()
operator|.
name|getRemoteAddress
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|assertTrue
argument_list|(
name|set
operator|.
name|size
argument_list|()
operator|>
literal|1
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|testClusterConnectedBeforeClients
parameter_list|()
throws|throws
name|Exception
block|{
if|if
condition|(
name|brokerB
operator|==
literal|null
condition|)
block|{
name|brokerB
operator|=
name|createBrokerB
argument_list|(
name|getBindAddress
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|Thread
operator|.
name|sleep
argument_list|(
literal|5000
argument_list|)
expr_stmt|;
name|createClients
argument_list|()
expr_stmt|;
name|Thread
operator|.
name|sleep
argument_list|(
literal|2000
argument_list|)
expr_stmt|;
name|brokerA
operator|.
name|stop
argument_list|()
expr_stmt|;
name|Thread
operator|.
name|sleep
argument_list|(
literal|2000
argument_list|)
expr_stmt|;
name|URI
name|brokerBURI
init|=
operator|new
name|URI
argument_list|(
name|brokerB
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
for|for
control|(
name|ActiveMQConnection
name|c
range|:
name|connections
control|)
block|{
name|String
name|addr
init|=
name|c
operator|.
name|getTransportChannel
argument_list|()
operator|.
name|getRemoteAddress
argument_list|()
decl_stmt|;
name|assertTrue
argument_list|(
name|addr
operator|.
name|indexOf
argument_list|(
literal|""
operator|+
name|brokerBURI
operator|.
name|getPort
argument_list|()
argument_list|)
operator|>
literal|0
argument_list|)
expr_stmt|;
block|}
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
if|if
condition|(
name|brokerA
operator|==
literal|null
condition|)
block|{
name|brokerA
operator|=
name|createBrokerA
argument_list|(
name|getBindAddress
argument_list|()
operator|+
literal|"?transport.closeAsync=false"
argument_list|)
expr_stmt|;
name|clientUrl
operator|=
literal|"failover://("
operator|+
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
name|getPublishableConnectString
argument_list|()
operator|+
literal|")"
expr_stmt|;
block|}
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
for|for
control|(
name|Connection
name|c
range|:
name|connections
control|)
block|{
name|c
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
if|if
condition|(
name|brokerB
operator|!=
literal|null
condition|)
block|{
name|brokerB
operator|.
name|stop
argument_list|()
expr_stmt|;
name|brokerB
operator|=
literal|null
expr_stmt|;
block|}
if|if
condition|(
name|brokerA
operator|!=
literal|null
condition|)
block|{
name|brokerA
operator|.
name|stop
argument_list|()
expr_stmt|;
name|brokerA
operator|=
literal|null
expr_stmt|;
block|}
block|}
specifier|protected
name|BrokerService
name|createBrokerA
parameter_list|(
name|String
name|uri
parameter_list|)
throws|throws
name|Exception
block|{
name|BrokerService
name|answer
init|=
operator|new
name|BrokerService
argument_list|()
decl_stmt|;
name|answer
operator|.
name|setUseJmx
argument_list|(
literal|false
argument_list|)
expr_stmt|;
name|configureConsumerBroker
argument_list|(
name|answer
argument_list|,
name|uri
argument_list|)
expr_stmt|;
name|answer
operator|.
name|start
argument_list|()
expr_stmt|;
return|return
name|answer
return|;
block|}
specifier|protected
name|void
name|configureConsumerBroker
parameter_list|(
name|BrokerService
name|answer
parameter_list|,
name|String
name|uri
parameter_list|)
throws|throws
name|Exception
block|{
name|answer
operator|.
name|setBrokerName
argument_list|(
name|BROKER_A_NAME
argument_list|)
expr_stmt|;
name|answer
operator|.
name|setPersistent
argument_list|(
literal|false
argument_list|)
expr_stmt|;
name|TransportConnector
name|connector
init|=
name|answer
operator|.
name|addConnector
argument_list|(
name|uri
argument_list|)
decl_stmt|;
name|connector
operator|.
name|setRebalanceClusterClients
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|connector
operator|.
name|setUpdateClusterClients
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|answer
operator|.
name|setUseShutdownHook
argument_list|(
literal|false
argument_list|)
expr_stmt|;
block|}
specifier|protected
name|BrokerService
name|createBrokerB
parameter_list|(
name|String
name|uri
parameter_list|)
throws|throws
name|Exception
block|{
name|BrokerService
name|answer
init|=
operator|new
name|BrokerService
argument_list|()
decl_stmt|;
name|answer
operator|.
name|setUseJmx
argument_list|(
literal|false
argument_list|)
expr_stmt|;
name|configureNetwork
argument_list|(
name|answer
argument_list|,
name|uri
argument_list|)
expr_stmt|;
name|answer
operator|.
name|start
argument_list|()
expr_stmt|;
return|return
name|answer
return|;
block|}
specifier|protected
name|void
name|configureNetwork
parameter_list|(
name|BrokerService
name|answer
parameter_list|,
name|String
name|uri
parameter_list|)
throws|throws
name|Exception
block|{
name|answer
operator|.
name|setBrokerName
argument_list|(
name|BROKER_B_NAME
argument_list|)
expr_stmt|;
name|answer
operator|.
name|setPersistent
argument_list|(
literal|false
argument_list|)
expr_stmt|;
name|NetworkConnector
name|network
init|=
name|answer
operator|.
name|addNetworkConnector
argument_list|(
literal|"static://"
operator|+
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
name|getPublishableConnectString
argument_list|()
argument_list|)
decl_stmt|;
name|network
operator|.
name|setDuplex
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|TransportConnector
name|connector
init|=
name|answer
operator|.
name|addConnector
argument_list|(
name|uri
argument_list|)
decl_stmt|;
name|connector
operator|.
name|setRebalanceClusterClients
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|connector
operator|.
name|setUpdateClusterClients
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|answer
operator|.
name|setUseShutdownHook
argument_list|(
literal|false
argument_list|)
expr_stmt|;
block|}
annotation|@
name|SuppressWarnings
argument_list|(
literal|"unused"
argument_list|)
specifier|protected
name|void
name|createClients
parameter_list|()
throws|throws
name|Exception
block|{
name|ActiveMQConnectionFactory
name|factory
init|=
operator|new
name|ActiveMQConnectionFactory
argument_list|(
name|clientUrl
argument_list|)
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
name|NUMBER
condition|;
name|i
operator|++
control|)
block|{
name|ActiveMQConnection
name|c
init|=
operator|(
name|ActiveMQConnection
operator|)
name|factory
operator|.
name|createConnection
argument_list|()
decl_stmt|;
name|c
operator|.
name|start
argument_list|()
expr_stmt|;
name|Session
name|s
init|=
name|c
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
name|Queue
name|queue
init|=
name|s
operator|.
name|createQueue
argument_list|(
name|getClass
argument_list|()
operator|.
name|getName
argument_list|()
argument_list|)
decl_stmt|;
name|MessageConsumer
name|consumer
init|=
name|s
operator|.
name|createConsumer
argument_list|(
name|queue
argument_list|)
decl_stmt|;
name|connections
operator|.
name|add
argument_list|(
name|c
argument_list|)
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

