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
name|stomp
package|;
end_package

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|ByteArrayInputStream
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|DataInput
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|DataInputStream
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|IOException
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
name|Message
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
name|ActiveMQQueue
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
comment|/**  *  *  */
end_comment

begin_class
specifier|public
class|class
name|StompSubscriptionRemoveTest
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
name|StompSubscriptionRemoveTest
operator|.
name|class
argument_list|)
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|String
name|COMMAND_MESSAGE
init|=
literal|"MESSAGE"
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|String
name|HEADER_MESSAGE_ID
init|=
literal|"message-id"
decl_stmt|;
specifier|private
name|StompConnection
name|stompConnection
init|=
operator|new
name|StompConnection
argument_list|()
decl_stmt|;
specifier|public
name|void
name|testRemoveSubscriber
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
name|addConnector
argument_list|(
literal|"stomp://localhost:0"
argument_list|)
operator|.
name|setName
argument_list|(
literal|"Stomp"
argument_list|)
expr_stmt|;
name|broker
operator|.
name|addConnector
argument_list|(
literal|"tcp://localhost:0"
argument_list|)
operator|.
name|setName
argument_list|(
literal|"Default"
argument_list|)
expr_stmt|;
name|broker
operator|.
name|start
argument_list|()
expr_stmt|;
specifier|final
name|String
name|stompUri
init|=
name|broker
operator|.
name|getConnectorByName
argument_list|(
literal|"Stomp"
argument_list|)
operator|.
name|getPublishableConnectString
argument_list|()
decl_stmt|;
specifier|final
name|int
name|stompPort
init|=
operator|new
name|URI
argument_list|(
name|stompUri
argument_list|)
operator|.
name|getPort
argument_list|()
decl_stmt|;
specifier|final
name|String
name|openwireUri
init|=
name|broker
operator|.
name|getConnectorByName
argument_list|(
literal|"Default"
argument_list|)
operator|.
name|getPublishableConnectString
argument_list|()
decl_stmt|;
name|ActiveMQConnectionFactory
name|factory
init|=
operator|new
name|ActiveMQConnectionFactory
argument_list|(
name|openwireUri
argument_list|)
decl_stmt|;
name|Connection
name|connection
init|=
name|factory
operator|.
name|createConnection
argument_list|()
decl_stmt|;
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
name|MessageProducer
name|producer
init|=
name|session
operator|.
name|createProducer
argument_list|(
operator|new
name|ActiveMQQueue
argument_list|(
name|getDestinationName
argument_list|()
argument_list|)
argument_list|)
decl_stmt|;
name|Message
name|message
init|=
name|session
operator|.
name|createTextMessage
argument_list|(
literal|"Testas"
argument_list|)
decl_stmt|;
for|for
control|(
name|int
name|idx
init|=
literal|0
init|;
name|idx
operator|<
literal|2000
condition|;
operator|++
name|idx
control|)
block|{
name|producer
operator|.
name|send
argument_list|(
name|message
argument_list|)
expr_stmt|;
name|LOG
operator|.
name|debug
argument_list|(
literal|"Sending: "
operator|+
name|idx
argument_list|)
expr_stmt|;
block|}
name|producer
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
name|stompConnection
operator|.
name|open
argument_list|(
operator|new
name|Socket
argument_list|(
literal|"localhost"
argument_list|,
name|stompPort
argument_list|)
argument_list|)
expr_stmt|;
name|String
name|connectFrame
init|=
literal|"CONNECT\n"
operator|+
literal|"login: brianm\n"
operator|+
literal|"passcode: wombats\n\n"
operator|+
name|Stomp
operator|.
name|NULL
decl_stmt|;
name|stompConnection
operator|.
name|sendFrame
argument_list|(
name|connectFrame
argument_list|)
expr_stmt|;
name|stompConnection
operator|.
name|receiveFrame
argument_list|()
expr_stmt|;
name|String
name|frame
init|=
literal|"SUBSCRIBE\n"
operator|+
literal|"destination:/queue/"
operator|+
name|getDestinationName
argument_list|()
operator|+
literal|"\n"
operator|+
literal|"ack:client\n\n"
operator|+
name|Stomp
operator|.
name|NULL
decl_stmt|;
name|stompConnection
operator|.
name|sendFrame
argument_list|(
name|frame
argument_list|)
expr_stmt|;
name|int
name|messagesCount
init|=
literal|0
decl_stmt|;
name|int
name|count
init|=
literal|0
decl_stmt|;
while|while
condition|(
name|count
operator|<
literal|2
condition|)
block|{
name|String
name|receiveFrame
init|=
name|stompConnection
operator|.
name|receiveFrame
argument_list|()
decl_stmt|;
name|LOG
operator|.
name|debug
argument_list|(
literal|"Received: "
operator|+
name|receiveFrame
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"Unexpected frame received"
argument_list|,
name|COMMAND_MESSAGE
argument_list|,
name|getCommand
argument_list|(
name|receiveFrame
argument_list|)
argument_list|)
expr_stmt|;
name|String
name|messageId
init|=
name|getHeaderValue
argument_list|(
name|receiveFrame
argument_list|,
name|HEADER_MESSAGE_ID
argument_list|)
decl_stmt|;
name|String
name|ackmessage
init|=
literal|"ACK\n"
operator|+
name|HEADER_MESSAGE_ID
operator|+
literal|":"
operator|+
name|messageId
operator|+
literal|"\n\n"
operator|+
name|Stomp
operator|.
name|NULL
decl_stmt|;
name|stompConnection
operator|.
name|sendFrame
argument_list|(
name|ackmessage
argument_list|)
expr_stmt|;
comment|// Thread.sleep(1000);
operator|++
name|messagesCount
expr_stmt|;
operator|++
name|count
expr_stmt|;
block|}
name|stompConnection
operator|.
name|sendFrame
argument_list|(
literal|"DISCONNECT\n\n"
argument_list|)
expr_stmt|;
name|Thread
operator|.
name|sleep
argument_list|(
literal|1000
argument_list|)
expr_stmt|;
name|stompConnection
operator|.
name|close
argument_list|()
expr_stmt|;
name|stompConnection
operator|.
name|open
argument_list|(
operator|new
name|Socket
argument_list|(
literal|"localhost"
argument_list|,
name|stompPort
argument_list|)
argument_list|)
expr_stmt|;
name|connectFrame
operator|=
literal|"CONNECT\n"
operator|+
literal|"login: brianm\n"
operator|+
literal|"passcode: wombats\n\n"
operator|+
name|Stomp
operator|.
name|NULL
expr_stmt|;
name|stompConnection
operator|.
name|sendFrame
argument_list|(
name|connectFrame
argument_list|)
expr_stmt|;
name|stompConnection
operator|.
name|receiveFrame
argument_list|()
expr_stmt|;
name|frame
operator|=
literal|"SUBSCRIBE\n"
operator|+
literal|"destination:/queue/"
operator|+
name|getDestinationName
argument_list|()
operator|+
literal|"\n"
operator|+
literal|"ack:client\n\n"
operator|+
name|Stomp
operator|.
name|NULL
expr_stmt|;
name|stompConnection
operator|.
name|sendFrame
argument_list|(
name|frame
argument_list|)
expr_stmt|;
try|try
block|{
while|while
condition|(
name|count
operator|!=
literal|2000
condition|)
block|{
name|String
name|receiveFrame
init|=
name|stompConnection
operator|.
name|receiveFrame
argument_list|()
decl_stmt|;
name|LOG
operator|.
name|debug
argument_list|(
literal|"Received: "
operator|+
name|receiveFrame
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"Unexpected frame received"
argument_list|,
name|COMMAND_MESSAGE
argument_list|,
name|getCommand
argument_list|(
name|receiveFrame
argument_list|)
argument_list|)
expr_stmt|;
name|String
name|messageId
init|=
name|getHeaderValue
argument_list|(
name|receiveFrame
argument_list|,
name|HEADER_MESSAGE_ID
argument_list|)
decl_stmt|;
name|String
name|ackmessage
init|=
literal|"ACK\n"
operator|+
name|HEADER_MESSAGE_ID
operator|+
literal|":"
operator|+
name|messageId
operator|.
name|trim
argument_list|()
operator|+
literal|"\n\n"
operator|+
name|Stomp
operator|.
name|NULL
decl_stmt|;
name|stompConnection
operator|.
name|sendFrame
argument_list|(
name|ackmessage
argument_list|)
expr_stmt|;
comment|// Thread.sleep(1000);
operator|++
name|messagesCount
expr_stmt|;
operator|++
name|count
expr_stmt|;
block|}
block|}
catch|catch
parameter_list|(
name|IOException
name|ex
parameter_list|)
block|{
name|ex
operator|.
name|printStackTrace
argument_list|()
expr_stmt|;
block|}
name|stompConnection
operator|.
name|sendFrame
argument_list|(
literal|"DISCONNECT\n\n"
argument_list|)
expr_stmt|;
name|stompConnection
operator|.
name|close
argument_list|()
expr_stmt|;
name|broker
operator|.
name|stop
argument_list|()
expr_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"Total messages received: "
operator|+
name|messagesCount
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
literal|"Messages received after connection loss: "
operator|+
name|messagesCount
argument_list|,
name|messagesCount
operator|>=
literal|2000
argument_list|)
expr_stmt|;
comment|// The first ack messages has no chance complete, so we receiving more
comment|// messages
comment|// Don't know how to list subscriptions for the broker. Currently you
comment|// can check using JMX console. You'll see
comment|// Subscription without any connections
block|}
specifier|protected
name|String
name|getDestinationName
parameter_list|()
block|{
return|return
name|getClass
argument_list|()
operator|.
name|getName
argument_list|()
operator|+
literal|"."
operator|+
name|getName
argument_list|()
return|;
block|}
comment|// These two methods could move to a utility class
specifier|protected
name|String
name|getCommand
parameter_list|(
name|String
name|frame
parameter_list|)
block|{
return|return
name|frame
operator|.
name|substring
argument_list|(
literal|0
argument_list|,
name|frame
operator|.
name|indexOf
argument_list|(
literal|'\n'
argument_list|)
operator|+
literal|1
argument_list|)
operator|.
name|trim
argument_list|()
return|;
block|}
specifier|protected
name|String
name|getHeaderValue
parameter_list|(
name|String
name|frame
parameter_list|,
name|String
name|header
parameter_list|)
throws|throws
name|IOException
block|{
name|DataInput
name|input
init|=
operator|new
name|DataInputStream
argument_list|(
operator|new
name|ByteArrayInputStream
argument_list|(
name|frame
operator|.
name|getBytes
argument_list|()
argument_list|)
argument_list|)
decl_stmt|;
name|String
name|line
decl_stmt|;
for|for
control|(
name|int
name|idx
init|=
literal|0
init|;
comment|/* forever, sort of */
condition|;
operator|++
name|idx
control|)
block|{
name|line
operator|=
name|input
operator|.
name|readLine
argument_list|()
expr_stmt|;
if|if
condition|(
name|line
operator|==
literal|null
condition|)
block|{
comment|// end of message, no headers
return|return
literal|null
return|;
block|}
name|line
operator|=
name|line
operator|.
name|trim
argument_list|()
expr_stmt|;
if|if
condition|(
name|line
operator|.
name|length
argument_list|()
operator|==
literal|0
condition|)
block|{
comment|// start body, no headers from here on
return|return
literal|null
return|;
block|}
if|if
condition|(
name|idx
operator|>
literal|0
condition|)
block|{
comment|// Ignore command line
name|int
name|pos
init|=
name|line
operator|.
name|indexOf
argument_list|(
literal|':'
argument_list|)
decl_stmt|;
if|if
condition|(
name|header
operator|.
name|equals
argument_list|(
name|line
operator|.
name|substring
argument_list|(
literal|0
argument_list|,
name|pos
argument_list|)
argument_list|)
condition|)
block|{
return|return
name|line
operator|.
name|substring
argument_list|(
name|pos
operator|+
literal|1
argument_list|)
operator|.
name|trim
argument_list|()
return|;
block|}
block|}
block|}
block|}
block|}
end_class

end_unit

