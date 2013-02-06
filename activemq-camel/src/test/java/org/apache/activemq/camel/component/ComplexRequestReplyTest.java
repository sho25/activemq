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
name|camel
operator|.
name|component
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
name|assertNotNull
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
name|pool
operator|.
name|PooledConnectionFactory
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|camel
operator|.
name|CamelContext
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|camel
operator|.
name|ProducerTemplate
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|camel
operator|.
name|builder
operator|.
name|RouteBuilder
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|camel
operator|.
name|impl
operator|.
name|DefaultCamelContext
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
name|ComplexRequestReplyTest
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
name|ComplexRequestReplyTest
operator|.
name|class
argument_list|)
decl_stmt|;
specifier|private
name|BrokerService
name|brokerA
init|=
literal|null
decl_stmt|;
specifier|private
name|BrokerService
name|brokerB
init|=
literal|null
decl_stmt|;
specifier|private
name|CamelContext
name|senderContext
init|=
literal|null
decl_stmt|;
specifier|private
name|CamelContext
name|brokerAContext
init|=
literal|null
decl_stmt|;
specifier|private
name|CamelContext
name|brokerBContext
init|=
literal|null
decl_stmt|;
specifier|private
specifier|final
name|String
name|fromEndpoint
init|=
literal|"direct:test"
decl_stmt|;
specifier|private
specifier|final
name|String
name|toEndpoint
init|=
literal|"activemq:queue:send"
decl_stmt|;
specifier|private
specifier|final
name|String
name|brokerEndpoint
init|=
literal|"activemq:send"
decl_stmt|;
specifier|private
name|String
name|brokerAUri
decl_stmt|;
specifier|private
name|String
name|brokerBUri
decl_stmt|;
specifier|private
name|String
name|connectionUri
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
name|createBrokerA
argument_list|()
expr_stmt|;
name|brokerAUri
operator|=
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
expr_stmt|;
name|createBrokerB
argument_list|()
expr_stmt|;
name|brokerBUri
operator|=
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
expr_stmt|;
name|connectionUri
operator|=
literal|"failover:("
operator|+
name|brokerAUri
operator|+
literal|","
operator|+
name|brokerBUri
operator|+
literal|")?randomize=false"
expr_stmt|;
name|senderContext
operator|=
name|createSenderContext
argument_list|()
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
name|shutdownBrokerA
argument_list|()
expr_stmt|;
name|shutdownBrokerB
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testSendThenFailoverThenSend
parameter_list|()
throws|throws
name|Exception
block|{
name|ProducerTemplate
name|requester
init|=
name|senderContext
operator|.
name|createProducerTemplate
argument_list|()
decl_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"*** Sending Request 1"
argument_list|)
expr_stmt|;
name|String
name|response
init|=
operator|(
name|String
operator|)
name|requester
operator|.
name|requestBody
argument_list|(
name|fromEndpoint
argument_list|,
literal|"This is a request"
argument_list|)
decl_stmt|;
name|assertNotNull
argument_list|(
name|response
operator|!=
literal|null
argument_list|)
expr_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"Got response: "
operator|+
name|response
argument_list|)
expr_stmt|;
comment|/**          * You actually don't need to restart the broker, just wait long enough and the next          * next send will take out a closed connection and reconnect, and if you happen to hit          * the broker you weren't on last time, then you will see the failure.          */
name|TimeUnit
operator|.
name|SECONDS
operator|.
name|sleep
argument_list|(
literal|20
argument_list|)
expr_stmt|;
comment|/**          * I restart the broker after the wait that exceeds the idle timeout value of the          * PooledConnectionFactory to show that it doesn't matter now as the older connection          * has already been closed.          */
name|LOG
operator|.
name|info
argument_list|(
literal|"Restarting Broker A now."
argument_list|)
expr_stmt|;
name|shutdownBrokerA
argument_list|()
expr_stmt|;
name|createBrokerA
argument_list|()
expr_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"*** Sending Request 2"
argument_list|)
expr_stmt|;
name|response
operator|=
operator|(
name|String
operator|)
name|requester
operator|.
name|requestBody
argument_list|(
name|fromEndpoint
argument_list|,
literal|"This is a request"
argument_list|)
expr_stmt|;
name|assertNotNull
argument_list|(
name|response
operator|!=
literal|null
argument_list|)
expr_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"Got response: "
operator|+
name|response
argument_list|)
expr_stmt|;
block|}
specifier|private
name|CamelContext
name|createSenderContext
parameter_list|()
throws|throws
name|Exception
block|{
name|ActiveMQConnectionFactory
name|amqFactory
init|=
operator|new
name|ActiveMQConnectionFactory
argument_list|(
name|connectionUri
argument_list|)
decl_stmt|;
name|amqFactory
operator|.
name|setWatchTopicAdvisories
argument_list|(
literal|false
argument_list|)
expr_stmt|;
name|PooledConnectionFactory
name|pooled
init|=
operator|new
name|PooledConnectionFactory
argument_list|(
name|amqFactory
argument_list|)
decl_stmt|;
name|pooled
operator|.
name|setMaxConnections
argument_list|(
literal|1
argument_list|)
expr_stmt|;
name|pooled
operator|.
name|setMaximumActiveSessionPerConnection
argument_list|(
literal|500
argument_list|)
expr_stmt|;
comment|// If this is not zero the connection could get closed and the request
comment|// reply can fail.
name|pooled
operator|.
name|setIdleTimeout
argument_list|(
literal|0
argument_list|)
expr_stmt|;
name|CamelContext
name|camelContext
init|=
operator|new
name|DefaultCamelContext
argument_list|()
decl_stmt|;
name|ActiveMQComponent
name|amqComponent
init|=
operator|new
name|ActiveMQComponent
argument_list|()
decl_stmt|;
name|amqComponent
operator|.
name|setConnectionFactory
argument_list|(
name|pooled
argument_list|)
expr_stmt|;
name|camelContext
operator|.
name|addComponent
argument_list|(
literal|"activemq"
argument_list|,
name|amqComponent
argument_list|)
expr_stmt|;
name|camelContext
operator|.
name|addRoutes
argument_list|(
operator|new
name|RouteBuilder
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|void
name|configure
parameter_list|()
throws|throws
name|Exception
block|{
name|from
argument_list|(
name|fromEndpoint
argument_list|)
operator|.
name|inOut
argument_list|(
name|toEndpoint
argument_list|)
expr_stmt|;
block|}
block|}
argument_list|)
expr_stmt|;
name|camelContext
operator|.
name|start
argument_list|()
expr_stmt|;
return|return
name|camelContext
return|;
block|}
specifier|private
name|void
name|createBrokerA
parameter_list|()
throws|throws
name|Exception
block|{
name|brokerA
operator|=
name|createBroker
argument_list|(
literal|"brokerA"
argument_list|)
expr_stmt|;
name|brokerAContext
operator|=
name|createBrokerCamelContext
argument_list|(
literal|"brokerA"
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
block|}
specifier|private
name|void
name|shutdownBrokerA
parameter_list|()
throws|throws
name|Exception
block|{
name|brokerAContext
operator|.
name|stop
argument_list|()
expr_stmt|;
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
name|brokerA
operator|=
literal|null
expr_stmt|;
block|}
specifier|private
name|void
name|createBrokerB
parameter_list|()
throws|throws
name|Exception
block|{
name|brokerB
operator|=
name|createBroker
argument_list|(
literal|"brokerB"
argument_list|)
expr_stmt|;
name|brokerBContext
operator|=
name|createBrokerCamelContext
argument_list|(
literal|"brokerB"
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
block|}
specifier|private
name|void
name|shutdownBrokerB
parameter_list|()
throws|throws
name|Exception
block|{
name|brokerBContext
operator|.
name|stop
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
name|brokerB
operator|=
literal|null
expr_stmt|;
block|}
specifier|private
name|BrokerService
name|createBroker
parameter_list|(
name|String
name|name
parameter_list|)
throws|throws
name|Exception
block|{
name|BrokerService
name|service
init|=
operator|new
name|BrokerService
argument_list|()
decl_stmt|;
name|service
operator|.
name|setPersistent
argument_list|(
literal|false
argument_list|)
expr_stmt|;
name|service
operator|.
name|setUseJmx
argument_list|(
literal|false
argument_list|)
expr_stmt|;
name|service
operator|.
name|setBrokerName
argument_list|(
name|name
argument_list|)
expr_stmt|;
name|service
operator|.
name|addConnector
argument_list|(
literal|"tcp://localhost:0"
argument_list|)
expr_stmt|;
return|return
name|service
return|;
block|}
specifier|private
name|CamelContext
name|createBrokerCamelContext
parameter_list|(
name|String
name|brokerName
parameter_list|)
throws|throws
name|Exception
block|{
name|CamelContext
name|camelContext
init|=
operator|new
name|DefaultCamelContext
argument_list|()
decl_stmt|;
name|camelContext
operator|.
name|addComponent
argument_list|(
literal|"activemq"
argument_list|,
name|ActiveMQComponent
operator|.
name|activeMQComponent
argument_list|(
literal|"vm://"
operator|+
name|brokerName
operator|+
literal|"?create=false&waitForStart=10000"
argument_list|)
argument_list|)
expr_stmt|;
name|camelContext
operator|.
name|addRoutes
argument_list|(
operator|new
name|RouteBuilder
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|void
name|configure
parameter_list|()
throws|throws
name|Exception
block|{
name|from
argument_list|(
name|brokerEndpoint
argument_list|)
operator|.
name|setBody
argument_list|()
operator|.
name|simple
argument_list|(
literal|"Returning ${body}"
argument_list|)
operator|.
name|log
argument_list|(
literal|"***Reply sent to ${header.JMSReplyTo} CoorId = ${header.JMSCorrelationID}"
argument_list|)
expr_stmt|;
block|}
block|}
argument_list|)
expr_stmt|;
name|camelContext
operator|.
name|start
argument_list|()
expr_stmt|;
return|return
name|camelContext
return|;
block|}
block|}
end_class

end_unit

