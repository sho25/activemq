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
name|Destination
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
name|apache
operator|.
name|activemq
operator|.
name|broker
operator|.
name|region
operator|.
name|policy
operator|.
name|DeadLetterStrategy
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
name|region
operator|.
name|policy
operator|.
name|IndividualDeadLetterStrategy
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
name|region
operator|.
name|policy
operator|.
name|PolicyEntry
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
name|region
operator|.
name|policy
operator|.
name|PolicyMap
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

begin_class
specifier|public
class|class
name|TimeStampingBrokerPluginTest
extends|extends
name|TestCase
block|{
name|BrokerService
name|broker
decl_stmt|;
name|TransportConnector
name|tcpConnector
decl_stmt|;
name|MessageProducer
name|producer
decl_stmt|;
name|MessageConsumer
name|consumer
decl_stmt|;
name|Connection
name|connection
decl_stmt|;
name|Session
name|session
decl_stmt|;
name|Destination
name|destination
decl_stmt|;
name|String
name|queue
init|=
literal|"TEST.FOO"
decl_stmt|;
name|long
name|expiry
init|=
literal|500
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
name|TimeStampingBrokerPlugin
name|tsbp
init|=
operator|new
name|TimeStampingBrokerPlugin
argument_list|()
decl_stmt|;
name|tsbp
operator|.
name|setZeroExpirationOverride
argument_list|(
name|expiry
argument_list|)
expr_stmt|;
name|tsbp
operator|.
name|setTtlCeiling
argument_list|(
name|expiry
argument_list|)
expr_stmt|;
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
name|setUseJmx
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|broker
operator|.
name|setPlugins
argument_list|(
operator|new
name|BrokerPlugin
index|[]
block|{
name|tsbp
block|}
argument_list|)
expr_stmt|;
name|tcpConnector
operator|=
name|broker
operator|.
name|addConnector
argument_list|(
literal|"tcp://localhost:0"
argument_list|)
expr_stmt|;
comment|// Add policy and individual DLQ strategy
name|PolicyEntry
name|policy
init|=
operator|new
name|PolicyEntry
argument_list|()
decl_stmt|;
name|DeadLetterStrategy
name|strategy
init|=
operator|new
name|IndividualDeadLetterStrategy
argument_list|()
decl_stmt|;
name|strategy
operator|.
name|setProcessExpired
argument_list|(
literal|true
argument_list|)
expr_stmt|;
operator|(
operator|(
name|IndividualDeadLetterStrategy
operator|)
name|strategy
operator|)
operator|.
name|setUseQueueForQueueMessages
argument_list|(
literal|true
argument_list|)
expr_stmt|;
operator|(
operator|(
name|IndividualDeadLetterStrategy
operator|)
name|strategy
operator|)
operator|.
name|setQueuePrefix
argument_list|(
literal|"DLQ."
argument_list|)
expr_stmt|;
name|strategy
operator|.
name|setProcessNonPersistent
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|policy
operator|.
name|setDeadLetterStrategy
argument_list|(
name|strategy
argument_list|)
expr_stmt|;
name|PolicyMap
name|pMap
init|=
operator|new
name|PolicyMap
argument_list|()
decl_stmt|;
name|pMap
operator|.
name|setDefaultEntry
argument_list|(
name|policy
argument_list|)
expr_stmt|;
name|broker
operator|.
name|setDestinationPolicy
argument_list|(
name|pMap
argument_list|)
expr_stmt|;
name|broker
operator|.
name|start
argument_list|()
expr_stmt|;
comment|// Create a ConnectionFactory
name|ActiveMQConnectionFactory
name|connectionFactory
init|=
operator|new
name|ActiveMQConnectionFactory
argument_list|(
name|tcpConnector
operator|.
name|getConnectUri
argument_list|()
argument_list|)
decl_stmt|;
comment|// Create a Connection
name|connection
operator|=
name|connectionFactory
operator|.
name|createConnection
argument_list|()
expr_stmt|;
name|connection
operator|.
name|start
argument_list|()
expr_stmt|;
comment|// Create a Session
name|session
operator|=
name|connection
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
comment|// Create the destination Queue
name|destination
operator|=
name|session
operator|.
name|createQueue
argument_list|(
name|queue
argument_list|)
expr_stmt|;
comment|// Create a MessageProducer from the Session to the Topic or Queue
name|producer
operator|=
name|session
operator|.
name|createProducer
argument_list|(
name|destination
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
name|session
operator|.
name|close
argument_list|()
expr_stmt|;
name|connection
operator|.
name|close
argument_list|()
expr_stmt|;
name|broker
operator|.
name|stop
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testExpirationSet
parameter_list|()
throws|throws
name|Exception
block|{
comment|// Create a messages
name|Message
name|sentMessage
init|=
name|session
operator|.
name|createMessage
argument_list|()
decl_stmt|;
comment|// Tell the producer to send the message
name|long
name|beforeSend
init|=
name|System
operator|.
name|currentTimeMillis
argument_list|()
decl_stmt|;
name|producer
operator|.
name|send
argument_list|(
name|sentMessage
argument_list|)
expr_stmt|;
comment|// Create a MessageConsumer from the Session to the Topic or Queue
name|consumer
operator|=
name|session
operator|.
name|createConsumer
argument_list|(
name|destination
argument_list|)
expr_stmt|;
comment|// Wait for a message
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
comment|// assert message timestamp is in window
name|assertTrue
argument_list|(
literal|"Expiration should be not null"
operator|+
name|receivedMessage
operator|.
name|getJMSExpiration
argument_list|()
operator|+
literal|"\n"
argument_list|,
name|Long
operator|.
name|valueOf
argument_list|(
name|receivedMessage
operator|.
name|getJMSExpiration
argument_list|()
argument_list|)
operator|!=
literal|null
argument_list|)
expr_stmt|;
comment|// assert message expiration is in window
name|assertTrue
argument_list|(
literal|"Before send: "
operator|+
name|beforeSend
operator|+
literal|" Msg ts: "
operator|+
name|receivedMessage
operator|.
name|getJMSTimestamp
argument_list|()
operator|+
literal|" Msg Expiry: "
operator|+
name|receivedMessage
operator|.
name|getJMSExpiration
argument_list|()
argument_list|,
name|beforeSend
operator|<=
name|receivedMessage
operator|.
name|getJMSExpiration
argument_list|()
operator|&&
name|receivedMessage
operator|.
name|getJMSExpiration
argument_list|()
operator|<=
operator|(
name|receivedMessage
operator|.
name|getJMSTimestamp
argument_list|()
operator|+
name|expiry
operator|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testExpirationCelingSet
parameter_list|()
throws|throws
name|Exception
block|{
comment|// Create a messages
name|Message
name|sentMessage
init|=
name|session
operator|.
name|createMessage
argument_list|()
decl_stmt|;
comment|// Tell the producer to send the message
name|long
name|beforeSend
init|=
name|System
operator|.
name|currentTimeMillis
argument_list|()
decl_stmt|;
name|long
name|sendExpiry
init|=
name|beforeSend
operator|+
operator|(
name|expiry
operator|*
literal|22
operator|)
decl_stmt|;
name|sentMessage
operator|.
name|setJMSExpiration
argument_list|(
name|sendExpiry
argument_list|)
expr_stmt|;
name|producer
operator|.
name|send
argument_list|(
name|sentMessage
argument_list|)
expr_stmt|;
comment|// Create a MessageConsumer from the Session to the Topic or Queue
name|consumer
operator|=
name|session
operator|.
name|createConsumer
argument_list|(
name|destination
argument_list|)
expr_stmt|;
comment|// Wait for a message
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
comment|// assert message timestamp is in window
name|assertTrue
argument_list|(
literal|"Expiration should be not null"
operator|+
name|receivedMessage
operator|.
name|getJMSExpiration
argument_list|()
operator|+
literal|"\n"
argument_list|,
name|Long
operator|.
name|valueOf
argument_list|(
name|receivedMessage
operator|.
name|getJMSExpiration
argument_list|()
argument_list|)
operator|!=
literal|null
argument_list|)
expr_stmt|;
comment|// assert message expiration is in window
name|assertTrue
argument_list|(
literal|"Sent expiry: "
operator|+
name|sendExpiry
operator|+
literal|" Recv ts: "
operator|+
name|receivedMessage
operator|.
name|getJMSTimestamp
argument_list|()
operator|+
literal|" Recv expiry: "
operator|+
name|receivedMessage
operator|.
name|getJMSExpiration
argument_list|()
argument_list|,
name|beforeSend
operator|<=
name|receivedMessage
operator|.
name|getJMSExpiration
argument_list|()
operator|&&
name|receivedMessage
operator|.
name|getJMSExpiration
argument_list|()
operator|<=
operator|(
name|receivedMessage
operator|.
name|getJMSTimestamp
argument_list|()
operator|+
name|expiry
operator|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testExpirationDLQ
parameter_list|()
throws|throws
name|Exception
block|{
comment|// Create a messages
name|Message
name|sentMessage
init|=
name|session
operator|.
name|createMessage
argument_list|()
decl_stmt|;
comment|// Tell the producer to send the message
name|long
name|beforeSend
init|=
name|System
operator|.
name|currentTimeMillis
argument_list|()
decl_stmt|;
name|long
name|sendExpiry
init|=
name|beforeSend
operator|+
name|expiry
decl_stmt|;
name|sentMessage
operator|.
name|setJMSExpiration
argument_list|(
name|sendExpiry
argument_list|)
expr_stmt|;
name|producer
operator|.
name|send
argument_list|(
name|sentMessage
argument_list|)
expr_stmt|;
comment|// Create a MessageConsumer from the Session to the Topic or Queue
name|consumer
operator|=
name|session
operator|.
name|createConsumer
argument_list|(
name|destination
argument_list|)
expr_stmt|;
name|Thread
operator|.
name|sleep
argument_list|(
name|expiry
operator|+
literal|250
argument_list|)
expr_stmt|;
comment|// Wait for a message
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
comment|// Message should roll to DLQ
name|assertNull
argument_list|(
name|receivedMessage
argument_list|)
expr_stmt|;
comment|// Close old consumer, setup DLQ listener
name|consumer
operator|.
name|close
argument_list|()
expr_stmt|;
name|consumer
operator|=
name|session
operator|.
name|createConsumer
argument_list|(
name|session
operator|.
name|createQueue
argument_list|(
literal|"DLQ."
operator|+
name|queue
argument_list|)
argument_list|)
expr_stmt|;
comment|// Get mesage from DLQ
name|receivedMessage
operator|=
name|consumer
operator|.
name|receive
argument_list|(
literal|1000
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
comment|// assert message timestamp is in window
comment|//System.out.println("Recv: " + receivedMessage.getJMSExpiration());
name|assertEquals
argument_list|(
literal|"Expiration should be zero"
operator|+
name|receivedMessage
operator|.
name|getJMSExpiration
argument_list|()
operator|+
literal|"\n"
argument_list|,
name|receivedMessage
operator|.
name|getJMSExpiration
argument_list|()
argument_list|,
literal|0
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

