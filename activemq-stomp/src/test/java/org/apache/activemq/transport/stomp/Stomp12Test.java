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
import|import static
name|org
operator|.
name|junit
operator|.
name|Assert
operator|.
name|assertTrue
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
name|javax
operator|.
name|jms
operator|.
name|Connection
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
name|Stomp12Test
extends|extends
name|StompTestSupport
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
name|Stomp12Test
operator|.
name|class
argument_list|)
decl_stmt|;
specifier|private
name|Connection
name|connection
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
name|super
operator|.
name|setUp
argument_list|()
expr_stmt|;
name|stompConnect
argument_list|()
expr_stmt|;
name|connection
operator|=
name|cf
operator|.
name|createConnection
argument_list|(
literal|"system"
argument_list|,
literal|"manager"
argument_list|)
expr_stmt|;
name|connection
operator|.
name|start
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Override
specifier|protected
name|void
name|addStompConnector
parameter_list|()
throws|throws
name|Exception
block|{
name|TransportConnector
name|connector
init|=
name|brokerService
operator|.
name|addConnector
argument_list|(
literal|"stomp://0.0.0.0:"
operator|+
name|port
argument_list|)
decl_stmt|;
name|port
operator|=
name|connector
operator|.
name|getConnectUri
argument_list|()
operator|.
name|getPort
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Override
specifier|protected
name|Socket
name|createSocket
parameter_list|()
throws|throws
name|IOException
block|{
return|return
operator|new
name|Socket
argument_list|(
literal|"127.0.0.1"
argument_list|,
name|this
operator|.
name|port
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|protected
name|String
name|getQueueName
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
annotation|@
name|Test
specifier|public
name|void
name|testTelnetStyleSends
parameter_list|()
throws|throws
name|Exception
block|{
name|stompConnection
operator|.
name|setVersion
argument_list|(
name|Stomp
operator|.
name|V1_2
argument_list|)
expr_stmt|;
name|String
name|connect
init|=
literal|"CONNECT\r\n"
operator|+
literal|"accept-version:1.2\r\n"
operator|+
literal|"login:system\r\n"
operator|+
literal|"passcode:manager\r\n"
operator|+
literal|"\r\n"
operator|+
literal|"\u0000\r\n"
decl_stmt|;
name|stompConnection
operator|.
name|sendFrame
argument_list|(
name|connect
argument_list|)
expr_stmt|;
name|String
name|f
init|=
name|stompConnection
operator|.
name|receiveFrame
argument_list|()
decl_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"Broker sent: "
operator|+
name|f
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|f
operator|.
name|startsWith
argument_list|(
literal|"CONNECTED"
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|f
operator|.
name|indexOf
argument_list|(
literal|"version:1.2"
argument_list|)
operator|>=
literal|0
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|f
operator|.
name|indexOf
argument_list|(
literal|"session:"
argument_list|)
operator|>=
literal|0
argument_list|)
expr_stmt|;
name|String
name|send
init|=
literal|"SUBSCRIBE\r\n"
operator|+
literal|"id:1\r\n"
operator|+
literal|"destination:/queue/"
operator|+
name|getQueueName
argument_list|()
operator|+
literal|"\r\n"
operator|+
literal|"receipt:1\r\n"
operator|+
literal|"\r\n"
operator|+
literal|"\u0000\r\n"
decl_stmt|;
name|stompConnection
operator|.
name|sendFrame
argument_list|(
name|send
argument_list|)
expr_stmt|;
name|StompFrame
name|receipt
init|=
name|stompConnection
operator|.
name|receive
argument_list|()
decl_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"Broker sent: "
operator|+
name|receipt
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|receipt
operator|.
name|getAction
argument_list|()
operator|.
name|startsWith
argument_list|(
literal|"RECEIPT"
argument_list|)
argument_list|)
expr_stmt|;
name|String
name|receiptId
init|=
name|receipt
operator|.
name|getHeaders
argument_list|()
operator|.
name|get
argument_list|(
literal|"receipt-id"
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|"1"
argument_list|,
name|receiptId
argument_list|)
expr_stmt|;
name|String
name|disconnect
init|=
literal|"DISCONNECT\n"
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
name|disconnect
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testClientAckWithoutAckId
parameter_list|()
throws|throws
name|Exception
block|{
name|stompConnection
operator|.
name|setVersion
argument_list|(
name|Stomp
operator|.
name|V1_2
argument_list|)
expr_stmt|;
name|String
name|connect
init|=
literal|"STOMP\r\n"
operator|+
literal|"accept-version:1.2\r\n"
operator|+
literal|"login:system\r\n"
operator|+
literal|"passcode:manager\r\n"
operator|+
literal|"\r\n"
operator|+
literal|"\u0000\r\n"
decl_stmt|;
name|stompConnection
operator|.
name|sendFrame
argument_list|(
name|connect
argument_list|)
expr_stmt|;
name|String
name|f
init|=
name|stompConnection
operator|.
name|receiveFrame
argument_list|()
decl_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"Broker sent: "
operator|+
name|f
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|f
operator|.
name|startsWith
argument_list|(
literal|"CONNECTED"
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|f
operator|.
name|indexOf
argument_list|(
literal|"version:1.2"
argument_list|)
operator|>=
literal|0
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|f
operator|.
name|indexOf
argument_list|(
literal|"session:"
argument_list|)
operator|>=
literal|0
argument_list|)
expr_stmt|;
name|String
name|subscribe
init|=
literal|"SUBSCRIBE\n"
operator|+
literal|"id:1\n"
operator|+
literal|"activemq.prefetchSize=1\n"
operator|+
literal|"ack:client\n"
operator|+
literal|"destination:/queue/"
operator|+
name|getQueueName
argument_list|()
operator|+
literal|"\n"
operator|+
literal|"receipt:1\n"
operator|+
literal|"\n"
operator|+
name|Stomp
operator|.
name|NULL
decl_stmt|;
name|stompConnection
operator|.
name|sendFrame
argument_list|(
name|subscribe
argument_list|)
expr_stmt|;
name|StompFrame
name|receipt
init|=
name|stompConnection
operator|.
name|receive
argument_list|()
decl_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"Broker sent: "
operator|+
name|receipt
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|receipt
operator|.
name|getAction
argument_list|()
operator|.
name|startsWith
argument_list|(
literal|"RECEIPT"
argument_list|)
argument_list|)
expr_stmt|;
name|String
name|receiptId
init|=
name|receipt
operator|.
name|getHeaders
argument_list|()
operator|.
name|get
argument_list|(
literal|"receipt-id"
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|"1"
argument_list|,
name|receiptId
argument_list|)
expr_stmt|;
name|String
name|message
init|=
literal|"SEND\n"
operator|+
literal|"destination:/queue/"
operator|+
name|getQueueName
argument_list|()
operator|+
literal|"\n\n"
operator|+
literal|"1"
operator|+
name|Stomp
operator|.
name|NULL
decl_stmt|;
name|stompConnection
operator|.
name|sendFrame
argument_list|(
name|message
argument_list|)
expr_stmt|;
name|StompFrame
name|received
init|=
name|stompConnection
operator|.
name|receive
argument_list|()
decl_stmt|;
name|assertTrue
argument_list|(
name|received
operator|.
name|getAction
argument_list|()
operator|.
name|equals
argument_list|(
literal|"MESSAGE"
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|received
operator|.
name|getHeaders
argument_list|()
operator|.
name|containsKey
argument_list|(
name|Stomp
operator|.
name|Headers
operator|.
name|Message
operator|.
name|ACK_ID
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"1"
argument_list|,
name|received
operator|.
name|getBody
argument_list|()
argument_list|)
expr_stmt|;
name|String
name|frame
init|=
literal|"ACK\n"
operator|+
literal|"message-id:"
operator|+
name|received
operator|.
name|getHeaders
argument_list|()
operator|.
name|get
argument_list|(
name|Stomp
operator|.
name|Headers
operator|.
name|Message
operator|.
name|ACK_ID
argument_list|)
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
name|frame
argument_list|)
expr_stmt|;
name|received
operator|=
name|stompConnection
operator|.
name|receive
argument_list|()
expr_stmt|;
name|assertTrue
argument_list|(
name|received
operator|.
name|getAction
argument_list|()
operator|.
name|equals
argument_list|(
literal|"ERROR"
argument_list|)
argument_list|)
expr_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"Broker sent: "
operator|+
name|received
argument_list|)
expr_stmt|;
name|String
name|disconnect
init|=
literal|"DISCONNECT\n"
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
name|disconnect
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testClientAck
parameter_list|()
throws|throws
name|Exception
block|{
name|stompConnection
operator|.
name|setVersion
argument_list|(
name|Stomp
operator|.
name|V1_2
argument_list|)
expr_stmt|;
name|String
name|connect
init|=
literal|"STOMP\r\n"
operator|+
literal|"accept-version:1.2\r\n"
operator|+
literal|"login:system\r\n"
operator|+
literal|"passcode:manager\r\n"
operator|+
literal|"\r\n"
operator|+
literal|"\u0000\r\n"
decl_stmt|;
name|stompConnection
operator|.
name|sendFrame
argument_list|(
name|connect
argument_list|)
expr_stmt|;
name|String
name|f
init|=
name|stompConnection
operator|.
name|receiveFrame
argument_list|()
decl_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"Broker sent: "
operator|+
name|f
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|f
operator|.
name|startsWith
argument_list|(
literal|"CONNECTED"
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|f
operator|.
name|indexOf
argument_list|(
literal|"version:1.2"
argument_list|)
operator|>=
literal|0
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|f
operator|.
name|indexOf
argument_list|(
literal|"session:"
argument_list|)
operator|>=
literal|0
argument_list|)
expr_stmt|;
name|String
name|subscribe
init|=
literal|"SUBSCRIBE\n"
operator|+
literal|"id:1\n"
operator|+
literal|"ack:client\n"
operator|+
literal|"destination:/queue/"
operator|+
name|getQueueName
argument_list|()
operator|+
literal|"\n"
operator|+
literal|"receipt:1\n"
operator|+
literal|"\n"
operator|+
name|Stomp
operator|.
name|NULL
decl_stmt|;
name|stompConnection
operator|.
name|sendFrame
argument_list|(
name|subscribe
argument_list|)
expr_stmt|;
name|StompFrame
name|receipt
init|=
name|stompConnection
operator|.
name|receive
argument_list|()
decl_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"Broker sent: "
operator|+
name|receipt
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|receipt
operator|.
name|getAction
argument_list|()
operator|.
name|startsWith
argument_list|(
literal|"RECEIPT"
argument_list|)
argument_list|)
expr_stmt|;
name|String
name|receiptId
init|=
name|receipt
operator|.
name|getHeaders
argument_list|()
operator|.
name|get
argument_list|(
literal|"receipt-id"
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|"1"
argument_list|,
name|receiptId
argument_list|)
expr_stmt|;
name|String
name|message
init|=
literal|"SEND\n"
operator|+
literal|"destination:/queue/"
operator|+
name|getQueueName
argument_list|()
operator|+
literal|"\n\n"
operator|+
literal|"1"
operator|+
name|Stomp
operator|.
name|NULL
decl_stmt|;
name|stompConnection
operator|.
name|sendFrame
argument_list|(
name|message
argument_list|)
expr_stmt|;
name|message
operator|=
literal|"SEND\n"
operator|+
literal|"destination:/queue/"
operator|+
name|getQueueName
argument_list|()
operator|+
literal|"\n\n"
operator|+
literal|"2"
operator|+
name|Stomp
operator|.
name|NULL
expr_stmt|;
name|stompConnection
operator|.
name|sendFrame
argument_list|(
name|message
argument_list|)
expr_stmt|;
name|StompFrame
name|received
init|=
name|stompConnection
operator|.
name|receive
argument_list|()
decl_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"Stomp Message: {}"
argument_list|,
name|received
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|received
operator|.
name|getAction
argument_list|()
operator|.
name|equals
argument_list|(
literal|"MESSAGE"
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|received
operator|.
name|getHeaders
argument_list|()
operator|.
name|containsKey
argument_list|(
name|Stomp
operator|.
name|Headers
operator|.
name|Message
operator|.
name|ACK_ID
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"1"
argument_list|,
name|received
operator|.
name|getBody
argument_list|()
argument_list|)
expr_stmt|;
name|received
operator|=
name|stompConnection
operator|.
name|receive
argument_list|()
expr_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"Stomp Message: {}"
argument_list|,
name|received
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|received
operator|.
name|getAction
argument_list|()
operator|.
name|equals
argument_list|(
literal|"MESSAGE"
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|received
operator|.
name|getHeaders
argument_list|()
operator|.
name|containsKey
argument_list|(
name|Stomp
operator|.
name|Headers
operator|.
name|Message
operator|.
name|ACK_ID
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"2"
argument_list|,
name|received
operator|.
name|getBody
argument_list|()
argument_list|)
expr_stmt|;
name|String
name|frame
init|=
literal|"ACK\n"
operator|+
literal|"id:"
operator|+
name|received
operator|.
name|getHeaders
argument_list|()
operator|.
name|get
argument_list|(
name|Stomp
operator|.
name|Headers
operator|.
name|Message
operator|.
name|ACK_ID
argument_list|)
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
name|frame
argument_list|)
expr_stmt|;
name|frame
operator|=
literal|"DISCONNECT\n\n"
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
name|Thread
operator|.
name|sleep
argument_list|(
literal|400
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|InterruptedException
name|e
parameter_list|)
block|{}
comment|// reconnect and send some messages to the offline subscribers and then try to get
comment|// them after subscribing again.
name|stompConnect
argument_list|()
expr_stmt|;
name|stompConnection
operator|.
name|sendFrame
argument_list|(
name|connect
argument_list|)
expr_stmt|;
name|frame
operator|=
name|stompConnection
operator|.
name|receiveFrame
argument_list|()
expr_stmt|;
name|LOG
operator|.
name|debug
argument_list|(
literal|"Broker sent: "
operator|+
name|frame
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|frame
operator|.
name|startsWith
argument_list|(
literal|"CONNECTED"
argument_list|)
argument_list|)
expr_stmt|;
name|stompConnection
operator|.
name|sendFrame
argument_list|(
name|subscribe
argument_list|)
expr_stmt|;
name|receipt
operator|=
name|stompConnection
operator|.
name|receive
argument_list|()
expr_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"Broker sent: "
operator|+
name|receipt
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|receipt
operator|.
name|getAction
argument_list|()
operator|.
name|startsWith
argument_list|(
literal|"RECEIPT"
argument_list|)
argument_list|)
expr_stmt|;
name|receiptId
operator|=
name|receipt
operator|.
name|getHeaders
argument_list|()
operator|.
name|get
argument_list|(
literal|"receipt-id"
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"1"
argument_list|,
name|receiptId
argument_list|)
expr_stmt|;
name|message
operator|=
literal|"SEND\n"
operator|+
literal|"destination:/queue/"
operator|+
name|getQueueName
argument_list|()
operator|+
literal|"\n\n"
operator|+
literal|"3"
operator|+
name|Stomp
operator|.
name|NULL
expr_stmt|;
name|stompConnection
operator|.
name|sendFrame
argument_list|(
name|message
argument_list|)
expr_stmt|;
name|received
operator|=
name|stompConnection
operator|.
name|receive
argument_list|()
expr_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"Stomp Message: {}"
argument_list|,
name|received
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|received
operator|.
name|getAction
argument_list|()
operator|.
name|equals
argument_list|(
literal|"MESSAGE"
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|received
operator|.
name|getHeaders
argument_list|()
operator|.
name|containsKey
argument_list|(
name|Stomp
operator|.
name|Headers
operator|.
name|Message
operator|.
name|ACK_ID
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"3"
argument_list|,
name|received
operator|.
name|getBody
argument_list|()
argument_list|)
expr_stmt|;
name|frame
operator|=
literal|"ACK\n"
operator|+
literal|"id:"
operator|+
name|received
operator|.
name|getHeaders
argument_list|()
operator|.
name|get
argument_list|(
name|Stomp
operator|.
name|Headers
operator|.
name|Message
operator|.
name|ACK_ID
argument_list|)
operator|+
literal|"\n\n"
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
name|String
name|disconnect
init|=
literal|"DISCONNECT\n"
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
name|disconnect
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testClientIndividualAck
parameter_list|()
throws|throws
name|Exception
block|{
name|stompConnection
operator|.
name|setVersion
argument_list|(
name|Stomp
operator|.
name|V1_2
argument_list|)
expr_stmt|;
name|String
name|connect
init|=
literal|"STOMP\r\n"
operator|+
literal|"accept-version:1.2\r\n"
operator|+
literal|"login:system\r\n"
operator|+
literal|"passcode:manager\r\n"
operator|+
literal|"\r\n"
operator|+
literal|"\u0000\r\n"
decl_stmt|;
name|stompConnection
operator|.
name|sendFrame
argument_list|(
name|connect
argument_list|)
expr_stmt|;
name|String
name|f
init|=
name|stompConnection
operator|.
name|receiveFrame
argument_list|()
decl_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"Broker sent: "
operator|+
name|f
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|f
operator|.
name|startsWith
argument_list|(
literal|"CONNECTED"
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|f
operator|.
name|indexOf
argument_list|(
literal|"version:1.2"
argument_list|)
operator|>=
literal|0
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|f
operator|.
name|indexOf
argument_list|(
literal|"session:"
argument_list|)
operator|>=
literal|0
argument_list|)
expr_stmt|;
name|String
name|subscribe
init|=
literal|"SUBSCRIBE\n"
operator|+
literal|"id:1\n"
operator|+
literal|"ack:client-individual\n"
operator|+
literal|"destination:/queue/"
operator|+
name|getQueueName
argument_list|()
operator|+
literal|"\n"
operator|+
literal|"receipt:1\n"
operator|+
literal|"\n"
operator|+
name|Stomp
operator|.
name|NULL
decl_stmt|;
name|stompConnection
operator|.
name|sendFrame
argument_list|(
name|subscribe
argument_list|)
expr_stmt|;
name|StompFrame
name|receipt
init|=
name|stompConnection
operator|.
name|receive
argument_list|()
decl_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"Broker sent: "
operator|+
name|receipt
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|receipt
operator|.
name|getAction
argument_list|()
operator|.
name|startsWith
argument_list|(
literal|"RECEIPT"
argument_list|)
argument_list|)
expr_stmt|;
name|String
name|receiptId
init|=
name|receipt
operator|.
name|getHeaders
argument_list|()
operator|.
name|get
argument_list|(
literal|"receipt-id"
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|"1"
argument_list|,
name|receiptId
argument_list|)
expr_stmt|;
name|String
name|message
init|=
literal|"SEND\n"
operator|+
literal|"destination:/queue/"
operator|+
name|getQueueName
argument_list|()
operator|+
literal|"\n\n"
operator|+
literal|"1"
operator|+
name|Stomp
operator|.
name|NULL
decl_stmt|;
name|stompConnection
operator|.
name|sendFrame
argument_list|(
name|message
argument_list|)
expr_stmt|;
name|message
operator|=
literal|"SEND\n"
operator|+
literal|"destination:/queue/"
operator|+
name|getQueueName
argument_list|()
operator|+
literal|"\n\n"
operator|+
literal|"2"
operator|+
name|Stomp
operator|.
name|NULL
expr_stmt|;
name|stompConnection
operator|.
name|sendFrame
argument_list|(
name|message
argument_list|)
expr_stmt|;
name|StompFrame
name|received
init|=
name|stompConnection
operator|.
name|receive
argument_list|()
decl_stmt|;
name|assertTrue
argument_list|(
name|received
operator|.
name|getAction
argument_list|()
operator|.
name|equals
argument_list|(
literal|"MESSAGE"
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|received
operator|.
name|getHeaders
argument_list|()
operator|.
name|containsKey
argument_list|(
name|Stomp
operator|.
name|Headers
operator|.
name|Message
operator|.
name|ACK_ID
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"1"
argument_list|,
name|received
operator|.
name|getBody
argument_list|()
argument_list|)
expr_stmt|;
name|received
operator|=
name|stompConnection
operator|.
name|receive
argument_list|()
expr_stmt|;
name|assertTrue
argument_list|(
name|received
operator|.
name|getAction
argument_list|()
operator|.
name|equals
argument_list|(
literal|"MESSAGE"
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|received
operator|.
name|getHeaders
argument_list|()
operator|.
name|containsKey
argument_list|(
name|Stomp
operator|.
name|Headers
operator|.
name|Message
operator|.
name|ACK_ID
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"2"
argument_list|,
name|received
operator|.
name|getBody
argument_list|()
argument_list|)
expr_stmt|;
name|String
name|frame
init|=
literal|"ACK\n"
operator|+
literal|"id:"
operator|+
name|received
operator|.
name|getHeaders
argument_list|()
operator|.
name|get
argument_list|(
name|Stomp
operator|.
name|Headers
operator|.
name|Message
operator|.
name|ACK_ID
argument_list|)
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
name|frame
argument_list|)
expr_stmt|;
name|frame
operator|=
literal|"DISCONNECT\n"
operator|+
literal|"\n\n"
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
name|Thread
operator|.
name|sleep
argument_list|(
literal|400
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|InterruptedException
name|e
parameter_list|)
block|{}
comment|// reconnect and send some messages to the offline subscribers and then try to get
comment|// them after subscribing again.
name|stompConnect
argument_list|()
expr_stmt|;
name|stompConnection
operator|.
name|sendFrame
argument_list|(
name|connect
argument_list|)
expr_stmt|;
name|frame
operator|=
name|stompConnection
operator|.
name|receiveFrame
argument_list|()
expr_stmt|;
name|LOG
operator|.
name|debug
argument_list|(
literal|"Broker sent: "
operator|+
name|frame
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|frame
operator|.
name|startsWith
argument_list|(
literal|"CONNECTED"
argument_list|)
argument_list|)
expr_stmt|;
name|stompConnection
operator|.
name|sendFrame
argument_list|(
name|subscribe
argument_list|)
expr_stmt|;
name|receipt
operator|=
name|stompConnection
operator|.
name|receive
argument_list|()
expr_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"Broker sent: "
operator|+
name|receipt
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|receipt
operator|.
name|getAction
argument_list|()
operator|.
name|startsWith
argument_list|(
literal|"RECEIPT"
argument_list|)
argument_list|)
expr_stmt|;
name|receiptId
operator|=
name|receipt
operator|.
name|getHeaders
argument_list|()
operator|.
name|get
argument_list|(
literal|"receipt-id"
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"1"
argument_list|,
name|receiptId
argument_list|)
expr_stmt|;
name|message
operator|=
literal|"SEND\n"
operator|+
literal|"destination:/queue/"
operator|+
name|getQueueName
argument_list|()
operator|+
literal|"\n\n"
operator|+
literal|"3"
operator|+
name|Stomp
operator|.
name|NULL
expr_stmt|;
name|stompConnection
operator|.
name|sendFrame
argument_list|(
name|message
argument_list|)
expr_stmt|;
name|received
operator|=
name|stompConnection
operator|.
name|receive
argument_list|()
expr_stmt|;
name|assertTrue
argument_list|(
name|received
operator|.
name|getAction
argument_list|()
operator|.
name|equals
argument_list|(
literal|"MESSAGE"
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|received
operator|.
name|getHeaders
argument_list|()
operator|.
name|containsKey
argument_list|(
name|Stomp
operator|.
name|Headers
operator|.
name|Message
operator|.
name|ACK_ID
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"1"
argument_list|,
name|received
operator|.
name|getBody
argument_list|()
argument_list|)
expr_stmt|;
name|frame
operator|=
literal|"ACK\n"
operator|+
literal|"id:"
operator|+
name|received
operator|.
name|getHeaders
argument_list|()
operator|.
name|get
argument_list|(
name|Stomp
operator|.
name|Headers
operator|.
name|Message
operator|.
name|ACK_ID
argument_list|)
operator|+
literal|"\n\n"
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
name|received
operator|=
name|stompConnection
operator|.
name|receive
argument_list|()
expr_stmt|;
name|assertTrue
argument_list|(
name|received
operator|.
name|getAction
argument_list|()
operator|.
name|equals
argument_list|(
literal|"MESSAGE"
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|received
operator|.
name|getHeaders
argument_list|()
operator|.
name|containsKey
argument_list|(
name|Stomp
operator|.
name|Headers
operator|.
name|Message
operator|.
name|ACK_ID
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"3"
argument_list|,
name|received
operator|.
name|getBody
argument_list|()
argument_list|)
expr_stmt|;
name|frame
operator|=
literal|"ACK\n"
operator|+
literal|"id:"
operator|+
name|received
operator|.
name|getHeaders
argument_list|()
operator|.
name|get
argument_list|(
name|Stomp
operator|.
name|Headers
operator|.
name|Message
operator|.
name|ACK_ID
argument_list|)
operator|+
literal|"\n\n"
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
name|String
name|disconnect
init|=
literal|"DISCONNECT\n"
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
name|disconnect
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

