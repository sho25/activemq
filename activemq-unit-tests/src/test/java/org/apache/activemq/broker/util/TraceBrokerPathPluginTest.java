begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *      http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  *   */
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
name|util
package|;
end_package

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
name|DeliveryMode
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|jms
operator|.
name|Message
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
name|broker
operator|.
name|TransportConnector
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

begin_comment
comment|/**  * Tests TraceBrokerPathPlugin by creating two brokers linked by a network connector, and checking to see if the consuming end receives the expected value in the trace property  * @author Raul Kripalani  *  */
end_comment

begin_class
specifier|public
class|class
name|TraceBrokerPathPluginTest
extends|extends
name|TestCase
block|{
name|BrokerService
name|brokerA
decl_stmt|;
name|BrokerService
name|brokerB
decl_stmt|;
name|TransportConnector
name|tcpConnectorA
decl_stmt|;
name|TransportConnector
name|tcpConnectorB
decl_stmt|;
name|MessageProducer
name|producer
decl_stmt|;
name|MessageConsumer
name|consumer
decl_stmt|;
name|Connection
name|connectionA
decl_stmt|;
name|Connection
name|connectionB
decl_stmt|;
name|Session
name|sessionA
decl_stmt|;
name|Session
name|sessionB
decl_stmt|;
name|String
name|queue
init|=
literal|"TEST.FOO"
decl_stmt|;
name|String
name|traceProperty
init|=
literal|"BROKER_PATH"
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
name|TraceBrokerPathPlugin
name|tbppA
init|=
operator|new
name|TraceBrokerPathPlugin
argument_list|()
decl_stmt|;
name|tbppA
operator|.
name|setStampProperty
argument_list|(
name|traceProperty
argument_list|)
expr_stmt|;
name|TraceBrokerPathPlugin
name|tbppB
init|=
operator|new
name|TraceBrokerPathPlugin
argument_list|()
decl_stmt|;
name|tbppB
operator|.
name|setStampProperty
argument_list|(
name|traceProperty
argument_list|)
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
literal|"brokerA"
argument_list|)
expr_stmt|;
name|brokerA
operator|.
name|setPersistent
argument_list|(
literal|false
argument_list|)
expr_stmt|;
name|brokerA
operator|.
name|setUseJmx
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|brokerA
operator|.
name|setPlugins
argument_list|(
operator|new
name|BrokerPlugin
index|[]
block|{
name|tbppA
block|}
argument_list|)
expr_stmt|;
name|tcpConnectorA
operator|=
name|brokerA
operator|.
name|addConnector
argument_list|(
literal|"tcp://localhost:0"
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
name|setBrokerName
argument_list|(
literal|"brokerB"
argument_list|)
expr_stmt|;
name|brokerB
operator|.
name|setPersistent
argument_list|(
literal|false
argument_list|)
expr_stmt|;
name|brokerB
operator|.
name|setUseJmx
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|brokerB
operator|.
name|setPlugins
argument_list|(
operator|new
name|BrokerPlugin
index|[]
block|{
name|tbppB
block|}
argument_list|)
expr_stmt|;
name|tcpConnectorB
operator|=
name|brokerB
operator|.
name|addConnector
argument_list|(
literal|"tcp://localhost:0"
argument_list|)
expr_stmt|;
name|brokerA
operator|.
name|addNetworkConnector
argument_list|(
literal|"static:("
operator|+
name|tcpConnectorB
operator|.
name|getConnectUri
argument_list|()
operator|.
name|toString
argument_list|()
operator|+
literal|")"
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
comment|// Initialise connection to A and MessageProducer
name|connectionA
operator|=
operator|new
name|ActiveMQConnectionFactory
argument_list|(
name|tcpConnectorA
operator|.
name|getConnectUri
argument_list|()
argument_list|)
operator|.
name|createConnection
argument_list|()
expr_stmt|;
name|connectionA
operator|.
name|start
argument_list|()
expr_stmt|;
name|sessionA
operator|=
name|connectionA
operator|.
name|createSession
argument_list|(
literal|false
argument_list|,
name|Session
operator|.
name|AUTO_ACKNOWLEDGE
argument_list|)
expr_stmt|;
name|producer
operator|=
name|sessionA
operator|.
name|createProducer
argument_list|(
name|sessionA
operator|.
name|createQueue
argument_list|(
name|queue
argument_list|)
argument_list|)
expr_stmt|;
name|producer
operator|.
name|setDeliveryMode
argument_list|(
name|DeliveryMode
operator|.
name|NON_PERSISTENT
argument_list|)
expr_stmt|;
comment|// Initialise connection to B and MessageConsumer
name|connectionB
operator|=
operator|new
name|ActiveMQConnectionFactory
argument_list|(
name|tcpConnectorB
operator|.
name|getConnectUri
argument_list|()
argument_list|)
operator|.
name|createConnection
argument_list|()
expr_stmt|;
name|connectionB
operator|.
name|start
argument_list|()
expr_stmt|;
name|sessionB
operator|=
name|connectionB
operator|.
name|createSession
argument_list|(
literal|false
argument_list|,
name|Session
operator|.
name|AUTO_ACKNOWLEDGE
argument_list|)
expr_stmt|;
name|consumer
operator|=
name|sessionB
operator|.
name|createConsumer
argument_list|(
name|sessionB
operator|.
name|createQueue
argument_list|(
name|queue
argument_list|)
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
comment|// Clean up
name|producer
operator|.
name|close
argument_list|()
expr_stmt|;
name|consumer
operator|.
name|close
argument_list|()
expr_stmt|;
name|sessionA
operator|.
name|close
argument_list|()
expr_stmt|;
name|sessionB
operator|.
name|close
argument_list|()
expr_stmt|;
name|connectionA
operator|.
name|close
argument_list|()
expr_stmt|;
name|connectionB
operator|.
name|close
argument_list|()
expr_stmt|;
name|brokerA
operator|.
name|stop
argument_list|()
expr_stmt|;
name|brokerB
operator|.
name|stop
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testTraceBrokerPathPlugin
parameter_list|()
throws|throws
name|Exception
block|{
name|Message
name|sentMessage
init|=
name|sessionA
operator|.
name|createMessage
argument_list|()
decl_stmt|;
name|producer
operator|.
name|send
argument_list|(
name|sentMessage
argument_list|)
expr_stmt|;
name|Message
name|receivedMessage
init|=
name|consumer
operator|.
name|receive
argument_list|(
literal|1000
argument_list|)
decl_stmt|;
comment|// assert we got the message
name|assertNotNull
argument_list|(
name|receivedMessage
argument_list|)
expr_stmt|;
comment|// assert we got the same message ID we sent
name|assertEquals
argument_list|(
name|sentMessage
operator|.
name|getJMSMessageID
argument_list|()
argument_list|,
name|receivedMessage
operator|.
name|getJMSMessageID
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"brokerA,brokerB"
argument_list|,
name|receivedMessage
operator|.
name|getStringProperty
argument_list|(
name|traceProperty
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

