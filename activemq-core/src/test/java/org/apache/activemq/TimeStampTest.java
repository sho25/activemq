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
name|util
operator|.
name|UDPTraceBrokerPlugin
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
name|view
operator|.
name|ConnectionDotFilePlugin
import|;
end_import

begin_class
specifier|public
class|class
name|TimeStampTest
extends|extends
name|TestCase
block|{
specifier|public
name|void
name|test
parameter_list|()
throws|throws
name|Exception
block|{
name|BrokerService
name|broker
init|=
operator|new
name|BrokerService
argument_list|()
decl_stmt|;
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
operator|new
name|ConnectionDotFilePlugin
argument_list|()
block|,
operator|new
name|UDPTraceBrokerPlugin
argument_list|()
block|}
argument_list|)
expr_stmt|;
name|TransportConnector
name|tcpConnector
init|=
name|broker
operator|.
name|addConnector
argument_list|(
literal|"tcp://localhost:0"
argument_list|)
decl_stmt|;
name|broker
operator|.
name|addConnector
argument_list|(
literal|"stomp://localhost:0"
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
name|Connection
name|connection
init|=
name|connectionFactory
operator|.
name|createConnection
argument_list|()
decl_stmt|;
name|connection
operator|.
name|start
argument_list|()
expr_stmt|;
comment|// Create a Session
name|Session
name|session
init|=
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
decl_stmt|;
comment|// Create the destination Queue
name|Destination
name|destination
init|=
name|session
operator|.
name|createQueue
argument_list|(
literal|"TEST.FOO"
argument_list|)
decl_stmt|;
comment|// Create a MessageProducer from the Session to the Topic or Queue
name|MessageProducer
name|producer
init|=
name|session
operator|.
name|createProducer
argument_list|(
name|destination
argument_list|)
decl_stmt|;
name|producer
operator|.
name|setDeliveryMode
argument_list|(
name|DeliveryMode
operator|.
name|NON_PERSISTENT
argument_list|)
expr_stmt|;
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
name|long
name|afterSend
init|=
name|System
operator|.
name|currentTimeMillis
argument_list|()
decl_stmt|;
comment|// assert message timestamp is in window
name|assertTrue
argument_list|(
name|beforeSend
operator|<=
name|sentMessage
operator|.
name|getJMSTimestamp
argument_list|()
operator|&&
name|sentMessage
operator|.
name|getJMSTimestamp
argument_list|()
operator|<=
name|afterSend
argument_list|)
expr_stmt|;
comment|// Create a MessageConsumer from the Session to the Topic or Queue
name|MessageConsumer
name|consumer
init|=
name|session
operator|.
name|createConsumer
argument_list|(
name|destination
argument_list|)
decl_stmt|;
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
literal|"JMS Message Timestamp should be set during the send method: \n"
operator|+
literal|"        beforeSend = "
operator|+
name|beforeSend
operator|+
literal|"\n"
operator|+
literal|"   getJMSTimestamp = "
operator|+
name|receivedMessage
operator|.
name|getJMSTimestamp
argument_list|()
operator|+
literal|"\n"
operator|+
literal|"         afterSend = "
operator|+
name|afterSend
operator|+
literal|"\n"
argument_list|,
name|beforeSend
operator|<=
name|receivedMessage
operator|.
name|getJMSTimestamp
argument_list|()
operator|&&
name|receivedMessage
operator|.
name|getJMSTimestamp
argument_list|()
operator|<=
name|afterSend
argument_list|)
expr_stmt|;
comment|// assert message timestamp is unchanged
name|assertEquals
argument_list|(
literal|"JMS Message Timestamp of recieved message should be the same as the sent message\n        "
argument_list|,
name|sentMessage
operator|.
name|getJMSTimestamp
argument_list|()
argument_list|,
name|receivedMessage
operator|.
name|getJMSTimestamp
argument_list|()
argument_list|)
expr_stmt|;
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
block|}
end_class

end_unit

