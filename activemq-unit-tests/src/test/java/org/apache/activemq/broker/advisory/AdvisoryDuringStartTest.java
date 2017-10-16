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
name|advisory
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
name|ActiveMQMessage
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
name|command
operator|.
name|ConsumerInfo
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
name|util
operator|.
name|ServiceStopper
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
name|Session
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
name|CountDownLatch
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
name|Executors
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
import|import static
name|org
operator|.
name|junit
operator|.
name|Assert
operator|.
name|*
import|;
end_import

begin_class
specifier|public
class|class
name|AdvisoryDuringStartTest
block|{
name|BrokerService
name|brokerService
decl_stmt|;
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
annotation|@
name|Test
specifier|public
name|void
name|testConsumerAdvisoryDuringSlowStart
parameter_list|()
throws|throws
name|Exception
block|{
name|brokerService
operator|=
operator|new
name|BrokerService
argument_list|()
expr_stmt|;
name|brokerService
operator|.
name|setPersistent
argument_list|(
literal|false
argument_list|)
expr_stmt|;
name|brokerService
operator|.
name|setUseJmx
argument_list|(
literal|false
argument_list|)
expr_stmt|;
name|brokerService
operator|.
name|addConnector
argument_list|(
literal|"tcp://localhost:0"
argument_list|)
expr_stmt|;
specifier|final
name|CountDownLatch
name|resumeStart
init|=
operator|new
name|CountDownLatch
argument_list|(
literal|1
argument_list|)
decl_stmt|;
name|brokerService
operator|.
name|addNetworkConnector
argument_list|(
operator|new
name|DiscoveryNetworkConnector
argument_list|()
block|{
annotation|@
name|Override
specifier|protected
name|void
name|handleStart
parameter_list|()
throws|throws
name|Exception
block|{
comment|// delay broker started flag
name|resumeStart
operator|.
name|await
argument_list|(
literal|5
argument_list|,
name|TimeUnit
operator|.
name|SECONDS
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|protected
name|void
name|handleStop
parameter_list|(
name|ServiceStopper
name|s
parameter_list|)
throws|throws
name|Exception
block|{}
block|}
argument_list|)
expr_stmt|;
name|Executors
operator|.
name|newCachedThreadPool
argument_list|()
operator|.
name|submit
argument_list|(
operator|new
name|Runnable
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|void
name|run
parameter_list|()
block|{
try|try
block|{
name|brokerService
operator|.
name|start
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
name|e
operator|.
name|printStackTrace
argument_list|()
expr_stmt|;
name|fail
argument_list|(
literal|"error on start: "
operator|+
name|e
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
block|}
argument_list|)
expr_stmt|;
name|ActiveMQConnectionFactory
name|connectionFactory
init|=
operator|new
name|ActiveMQConnectionFactory
argument_list|(
literal|"failover:("
operator|+
name|brokerService
operator|.
name|getTransportConnectorByScheme
argument_list|(
literal|"tcp"
argument_list|)
operator|.
name|getPublishableConnectString
argument_list|()
operator|+
literal|")"
argument_list|)
decl_stmt|;
name|Connection
name|advisoryConnection
init|=
name|connectionFactory
operator|.
name|createConnection
argument_list|()
decl_stmt|;
name|advisoryConnection
operator|.
name|start
argument_list|()
expr_stmt|;
name|Session
name|advisorySession
init|=
name|advisoryConnection
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
name|advisoryConsumer
init|=
name|advisorySession
operator|.
name|createConsumer
argument_list|(
name|advisorySession
operator|.
name|createTopic
argument_list|(
literal|"ActiveMQ.Advisory.Consumer.>"
argument_list|)
argument_list|)
decl_stmt|;
name|Connection
name|consumerConnection
init|=
name|connectionFactory
operator|.
name|createConnection
argument_list|()
decl_stmt|;
name|Session
name|consumerSession
init|=
name|consumerConnection
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
name|consumerConnection
operator|.
name|start
argument_list|()
expr_stmt|;
name|ActiveMQTopic
name|dest
init|=
operator|new
name|ActiveMQTopic
argument_list|(
literal|"SomeTopic"
argument_list|)
decl_stmt|;
comment|// real consumer
name|consumerSession
operator|.
name|createConsumer
argument_list|(
name|dest
argument_list|)
expr_stmt|;
name|resumeStart
operator|.
name|countDown
argument_list|()
expr_stmt|;
name|ActiveMQMessage
name|advisory
init|=
operator|(
name|ActiveMQMessage
operator|)
name|advisoryConsumer
operator|.
name|receive
argument_list|(
literal|4000
argument_list|)
decl_stmt|;
name|assertNotNull
argument_list|(
name|advisory
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|advisory
operator|.
name|getDataStructure
argument_list|()
operator|instanceof
name|ConsumerInfo
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
operator|(
operator|(
name|ConsumerInfo
operator|)
name|advisory
operator|.
name|getDataStructure
argument_list|()
operator|)
operator|.
name|getDestination
argument_list|()
operator|.
name|equals
argument_list|(
name|dest
argument_list|)
argument_list|)
expr_stmt|;
name|advisoryConnection
operator|.
name|close
argument_list|()
expr_stmt|;
name|consumerConnection
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
end_class

end_unit
