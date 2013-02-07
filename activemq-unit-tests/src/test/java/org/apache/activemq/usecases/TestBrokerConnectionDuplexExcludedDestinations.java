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
name|usecases
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
name|ConnectionFactory
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

begin_class
specifier|public
class|class
name|TestBrokerConnectionDuplexExcludedDestinations
extends|extends
name|TestCase
block|{
name|BrokerService
name|receiverBroker
decl_stmt|;
name|BrokerService
name|senderBroker
decl_stmt|;
name|Connection
name|hubConnection
decl_stmt|;
name|Session
name|hubSession
decl_stmt|;
name|Connection
name|spokeConnection
decl_stmt|;
name|Session
name|spokeSession
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
comment|// Hub broker
name|String
name|configFileName
init|=
literal|"org/apache/activemq/usecases/receiver-duplex.xml"
decl_stmt|;
name|URI
name|uri
init|=
operator|new
name|URI
argument_list|(
literal|"xbean:"
operator|+
name|configFileName
argument_list|)
decl_stmt|;
name|receiverBroker
operator|=
name|BrokerFactory
operator|.
name|createBroker
argument_list|(
name|uri
argument_list|)
expr_stmt|;
name|receiverBroker
operator|.
name|setPersistent
argument_list|(
literal|false
argument_list|)
expr_stmt|;
name|receiverBroker
operator|.
name|setBrokerName
argument_list|(
literal|"Hub"
argument_list|)
expr_stmt|;
comment|// Spoke broker
name|configFileName
operator|=
literal|"org/apache/activemq/usecases/sender-duplex.xml"
expr_stmt|;
name|uri
operator|=
operator|new
name|URI
argument_list|(
literal|"xbean:"
operator|+
name|configFileName
argument_list|)
expr_stmt|;
name|senderBroker
operator|=
name|BrokerFactory
operator|.
name|createBroker
argument_list|(
name|uri
argument_list|)
expr_stmt|;
name|senderBroker
operator|.
name|setPersistent
argument_list|(
literal|false
argument_list|)
expr_stmt|;
name|senderBroker
operator|.
name|setBrokerName
argument_list|(
literal|"Spoke"
argument_list|)
expr_stmt|;
comment|// Start both Hub and Spoke broker
name|receiverBroker
operator|.
name|start
argument_list|()
expr_stmt|;
name|senderBroker
operator|.
name|start
argument_list|()
expr_stmt|;
comment|// create hub session
name|ConnectionFactory
name|cfHub
init|=
operator|new
name|ActiveMQConnectionFactory
argument_list|(
literal|"tcp://localhost:62002"
argument_list|)
decl_stmt|;
name|hubConnection
operator|=
name|cfHub
operator|.
name|createConnection
argument_list|()
expr_stmt|;
name|hubConnection
operator|.
name|start
argument_list|()
expr_stmt|;
name|hubSession
operator|=
name|hubConnection
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
comment|// create spoke session
name|ConnectionFactory
name|cfSpoke
init|=
operator|new
name|ActiveMQConnectionFactory
argument_list|(
literal|"tcp://localhost:62001"
argument_list|)
decl_stmt|;
name|spokeConnection
operator|=
name|cfSpoke
operator|.
name|createConnection
argument_list|()
expr_stmt|;
name|spokeConnection
operator|.
name|start
argument_list|()
expr_stmt|;
name|spokeSession
operator|=
name|spokeConnection
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
block|}
annotation|@
name|Override
specifier|public
name|void
name|tearDown
parameter_list|()
throws|throws
name|Exception
block|{
name|hubSession
operator|.
name|close
argument_list|()
expr_stmt|;
name|hubConnection
operator|.
name|stop
argument_list|()
expr_stmt|;
name|hubConnection
operator|.
name|close
argument_list|()
expr_stmt|;
name|spokeSession
operator|.
name|close
argument_list|()
expr_stmt|;
name|spokeConnection
operator|.
name|stop
argument_list|()
expr_stmt|;
name|spokeConnection
operator|.
name|close
argument_list|()
expr_stmt|;
name|senderBroker
operator|.
name|stop
argument_list|()
expr_stmt|;
name|receiverBroker
operator|.
name|stop
argument_list|()
expr_stmt|;
block|}
specifier|public
name|void
name|testDuplexSendFromHubToSpoke
parameter_list|()
throws|throws
name|Exception
block|{
comment|//create hub producer
name|MessageProducer
name|hubProducer
init|=
name|hubSession
operator|.
name|createProducer
argument_list|(
literal|null
argument_list|)
decl_stmt|;
name|hubProducer
operator|.
name|setDeliveryMode
argument_list|(
name|DeliveryMode
operator|.
name|NON_PERSISTENT
argument_list|)
expr_stmt|;
name|hubProducer
operator|.
name|setDisableMessageID
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|hubProducer
operator|.
name|setDisableMessageTimestamp
argument_list|(
literal|true
argument_list|)
expr_stmt|;
comment|//create spoke producer
name|MessageProducer
name|spokeProducer
init|=
name|hubSession
operator|.
name|createProducer
argument_list|(
literal|null
argument_list|)
decl_stmt|;
name|spokeProducer
operator|.
name|setDeliveryMode
argument_list|(
name|DeliveryMode
operator|.
name|NON_PERSISTENT
argument_list|)
expr_stmt|;
name|spokeProducer
operator|.
name|setDisableMessageID
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|spokeProducer
operator|.
name|setDisableMessageTimestamp
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|Queue
name|excludedQueueHub
init|=
name|hubSession
operator|.
name|createQueue
argument_list|(
literal|"exclude.test.foo"
argument_list|)
decl_stmt|;
name|TextMessage
name|excludedMsgHub
init|=
name|hubSession
operator|.
name|createTextMessage
argument_list|()
decl_stmt|;
name|excludedMsgHub
operator|.
name|setText
argument_list|(
name|excludedQueueHub
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|Queue
name|includedQueueHub
init|=
name|hubSession
operator|.
name|createQueue
argument_list|(
literal|"include.test.foo"
argument_list|)
decl_stmt|;
name|TextMessage
name|includedMsgHub
init|=
name|hubSession
operator|.
name|createTextMessage
argument_list|()
decl_stmt|;
name|includedMsgHub
operator|.
name|setText
argument_list|(
name|includedQueueHub
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|Queue
name|alwaysIncludedQueueHub
init|=
name|hubSession
operator|.
name|createQueue
argument_list|(
literal|"always.include.test.foo"
argument_list|)
decl_stmt|;
name|TextMessage
name|alwaysIncludedMsgHub
init|=
name|hubSession
operator|.
name|createTextMessage
argument_list|()
decl_stmt|;
name|alwaysIncludedMsgHub
operator|.
name|setText
argument_list|(
name|alwaysIncludedQueueHub
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
comment|// Sending from Hub queue
name|hubProducer
operator|.
name|send
argument_list|(
name|excludedQueueHub
argument_list|,
name|excludedMsgHub
argument_list|)
expr_stmt|;
name|hubProducer
operator|.
name|send
argument_list|(
name|includedQueueHub
argument_list|,
name|includedMsgHub
argument_list|)
expr_stmt|;
name|hubProducer
operator|.
name|send
argument_list|(
name|alwaysIncludedQueueHub
argument_list|,
name|alwaysIncludedMsgHub
argument_list|)
expr_stmt|;
name|Queue
name|excludedQueueSpoke
init|=
name|spokeSession
operator|.
name|createQueue
argument_list|(
literal|"exclude.test.foo"
argument_list|)
decl_stmt|;
name|MessageConsumer
name|excludedConsumerSpoke
init|=
name|spokeSession
operator|.
name|createConsumer
argument_list|(
name|excludedQueueSpoke
argument_list|)
decl_stmt|;
name|Thread
operator|.
name|sleep
argument_list|(
literal|100
argument_list|)
expr_stmt|;
name|Queue
name|includedQueueSpoke
init|=
name|spokeSession
operator|.
name|createQueue
argument_list|(
literal|"include.test.foo"
argument_list|)
decl_stmt|;
name|MessageConsumer
name|includedConsumerSpoke
init|=
name|spokeSession
operator|.
name|createConsumer
argument_list|(
name|includedQueueSpoke
argument_list|)
decl_stmt|;
name|Thread
operator|.
name|sleep
argument_list|(
literal|100
argument_list|)
expr_stmt|;
name|Queue
name|alwaysIncludedQueueSpoke
init|=
name|spokeSession
operator|.
name|createQueue
argument_list|(
literal|"always.include.test.foo"
argument_list|)
decl_stmt|;
name|MessageConsumer
name|alwaysIncludedConsumerSpoke
init|=
name|spokeSession
operator|.
name|createConsumer
argument_list|(
name|alwaysIncludedQueueHub
argument_list|)
decl_stmt|;
name|Thread
operator|.
name|sleep
argument_list|(
literal|100
argument_list|)
expr_stmt|;
name|TextMessage
name|alwaysIncludedMsgSpoke
init|=
name|spokeSession
operator|.
name|createTextMessage
argument_list|()
decl_stmt|;
name|alwaysIncludedMsgSpoke
operator|.
name|setText
argument_list|(
name|alwaysIncludedQueueSpoke
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|spokeProducer
operator|.
name|send
argument_list|(
name|alwaysIncludedQueueSpoke
argument_list|,
name|alwaysIncludedMsgSpoke
argument_list|)
expr_stmt|;
name|MessageConsumer
name|alwaysIncludedConsumerHub
init|=
name|spokeSession
operator|.
name|createConsumer
argument_list|(
name|alwaysIncludedQueueHub
argument_list|)
decl_stmt|;
name|assertNotNull
argument_list|(
name|alwaysIncludedConsumerHub
argument_list|)
expr_stmt|;
comment|// Receiving from excluded Spoke queue
name|Message
name|msg
init|=
name|excludedConsumerSpoke
operator|.
name|receive
argument_list|(
literal|200
argument_list|)
decl_stmt|;
name|assertNull
argument_list|(
name|msg
argument_list|)
expr_stmt|;
comment|// Receiving from included Spoke queue
name|msg
operator|=
name|includedConsumerSpoke
operator|.
name|receive
argument_list|(
literal|200
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|includedMsgHub
argument_list|,
name|msg
argument_list|)
expr_stmt|;
comment|// Receiving from included Spoke queue
name|msg
operator|=
name|alwaysIncludedConsumerSpoke
operator|.
name|receive
argument_list|(
literal|200
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|alwaysIncludedMsgHub
argument_list|,
name|msg
argument_list|)
expr_stmt|;
comment|// we should be able to receive excluded queue message on Hub
name|MessageConsumer
name|excludedConsumerHub
init|=
name|hubSession
operator|.
name|createConsumer
argument_list|(
name|excludedQueueHub
argument_list|)
decl_stmt|;
name|msg
operator|=
name|excludedConsumerHub
operator|.
name|receive
argument_list|(
literal|200
argument_list|)
expr_stmt|;
empty_stmt|;
name|assertEquals
argument_list|(
name|excludedMsgHub
argument_list|,
name|msg
argument_list|)
expr_stmt|;
name|hubProducer
operator|.
name|close
argument_list|()
expr_stmt|;
name|excludedConsumerSpoke
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
end_class

end_unit

