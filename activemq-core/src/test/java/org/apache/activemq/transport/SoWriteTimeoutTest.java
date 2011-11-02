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
package|;
end_package

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
name|SocketException
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
name|JMSException
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
name|junit
operator|.
name|framework
operator|.
name|Test
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
name|JmsTestSupport
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
name|apache
operator|.
name|activemq
operator|.
name|store
operator|.
name|kahadb
operator|.
name|KahaDBPersistenceAdapter
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
name|transport
operator|.
name|stomp
operator|.
name|Stomp
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
name|transport
operator|.
name|stomp
operator|.
name|StompConnection
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
name|SocketProxy
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
name|URISupport
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
name|SoWriteTimeoutTest
extends|extends
name|JmsTestSupport
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
name|SoWriteTimeoutTest
operator|.
name|class
argument_list|)
decl_stmt|;
specifier|final
name|int
name|receiveBufferSize
init|=
literal|16
operator|*
literal|1024
decl_stmt|;
specifier|public
name|String
name|brokerTransportScheme
init|=
literal|"nio"
decl_stmt|;
specifier|protected
name|BrokerService
name|createBroker
parameter_list|()
throws|throws
name|Exception
block|{
name|BrokerService
name|broker
init|=
name|super
operator|.
name|createBroker
argument_list|()
decl_stmt|;
name|broker
operator|.
name|setPersistent
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|broker
operator|.
name|setDeleteAllMessagesOnStartup
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|KahaDBPersistenceAdapter
name|adapter
init|=
operator|new
name|KahaDBPersistenceAdapter
argument_list|()
decl_stmt|;
name|adapter
operator|.
name|setConcurrentStoreAndDispatchQueues
argument_list|(
literal|false
argument_list|)
expr_stmt|;
name|broker
operator|.
name|setPersistenceAdapter
argument_list|(
name|adapter
argument_list|)
expr_stmt|;
name|broker
operator|.
name|addConnector
argument_list|(
name|brokerTransportScheme
operator|+
literal|"://localhost:0?wireFormat.maxInactivityDuration=0&transport.soWriteTimeout=1000&transport.sleep=1000"
argument_list|)
expr_stmt|;
if|if
condition|(
literal|"nio"
operator|.
name|equals
argument_list|(
name|brokerTransportScheme
argument_list|)
condition|)
block|{
name|broker
operator|.
name|addConnector
argument_list|(
literal|"stomp+"
operator|+
name|brokerTransportScheme
operator|+
literal|"://localhost:0?transport.soWriteTimeout=1000&transport.sleep=1000&socketBufferSize="
operator|+
name|receiveBufferSize
operator|+
literal|"&trace=true"
argument_list|)
expr_stmt|;
block|}
return|return
name|broker
return|;
block|}
specifier|public
name|void
name|initCombosForTestWriteTimeout
parameter_list|()
block|{
name|addCombinationValues
argument_list|(
literal|"brokerTransportScheme"
argument_list|,
operator|new
name|Object
index|[]
block|{
literal|"tcp"
block|,
literal|"nio"
block|}
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|testWriteTimeout
parameter_list|()
throws|throws
name|Exception
block|{
name|Destination
name|dest
init|=
operator|new
name|ActiveMQQueue
argument_list|(
literal|"testWriteTimeout"
argument_list|)
decl_stmt|;
name|messageTextPrefix
operator|=
name|initMessagePrefix
argument_list|(
literal|8
operator|*
literal|1024
argument_list|)
expr_stmt|;
name|sendMessages
argument_list|(
name|dest
argument_list|,
literal|500
argument_list|)
expr_stmt|;
name|URI
name|tcpBrokerUri
init|=
name|URISupport
operator|.
name|removeQuery
argument_list|(
name|broker
operator|.
name|getTransportConnectors
argument_list|()
operator|.
name|get
argument_list|(
literal|0
argument_list|)
operator|.
name|getConnectUri
argument_list|()
argument_list|)
decl_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"consuming using uri: "
operator|+
name|tcpBrokerUri
argument_list|)
expr_stmt|;
name|SocketProxy
name|proxy
init|=
operator|new
name|SocketProxy
argument_list|()
decl_stmt|;
name|proxy
operator|.
name|setTarget
argument_list|(
name|tcpBrokerUri
argument_list|)
expr_stmt|;
name|proxy
operator|.
name|setReceiveBufferSize
argument_list|(
name|receiveBufferSize
argument_list|)
expr_stmt|;
name|proxy
operator|.
name|open
argument_list|()
expr_stmt|;
name|ActiveMQConnectionFactory
name|factory
init|=
operator|new
name|ActiveMQConnectionFactory
argument_list|(
name|proxy
operator|.
name|getUrl
argument_list|()
argument_list|)
decl_stmt|;
name|Connection
name|c
init|=
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
name|session
init|=
name|c
operator|.
name|createSession
argument_list|(
literal|true
argument_list|,
name|Session
operator|.
name|SESSION_TRANSACTED
argument_list|)
decl_stmt|;
name|MessageConsumer
name|consumer
init|=
name|session
operator|.
name|createConsumer
argument_list|(
name|dest
argument_list|)
decl_stmt|;
name|proxy
operator|.
name|pause
argument_list|()
expr_stmt|;
comment|// writes should back up... writeTimeout will kick in a abort the connection
name|TimeUnit
operator|.
name|SECONDS
operator|.
name|sleep
argument_list|(
literal|10
argument_list|)
expr_stmt|;
name|proxy
operator|.
name|goOn
argument_list|()
expr_stmt|;
name|assertNotNull
argument_list|(
literal|"can receive buffered messages"
argument_list|,
name|consumer
operator|.
name|receive
argument_list|(
literal|500
argument_list|)
argument_list|)
expr_stmt|;
try|try
block|{
name|session
operator|.
name|commit
argument_list|()
expr_stmt|;
name|fail
argument_list|(
literal|"expect commit to fail as server has aborted writeTimeout connection"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|JMSException
name|expected
parameter_list|)
block|{         }
block|}
specifier|public
name|void
name|testWriteTimeoutStompNio
parameter_list|()
throws|throws
name|Exception
block|{
name|ActiveMQQueue
name|dest
init|=
operator|new
name|ActiveMQQueue
argument_list|(
literal|"testWriteTimeout"
argument_list|)
decl_stmt|;
name|messageTextPrefix
operator|=
name|initMessagePrefix
argument_list|(
literal|8
operator|*
literal|1024
argument_list|)
expr_stmt|;
name|sendMessages
argument_list|(
name|dest
argument_list|,
literal|500
argument_list|)
expr_stmt|;
name|URI
name|stompBrokerUri
init|=
name|URISupport
operator|.
name|removeQuery
argument_list|(
name|broker
operator|.
name|getTransportConnectors
argument_list|()
operator|.
name|get
argument_list|(
literal|1
argument_list|)
operator|.
name|getConnectUri
argument_list|()
argument_list|)
decl_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"consuming using uri: "
operator|+
name|stompBrokerUri
argument_list|)
expr_stmt|;
name|SocketProxy
name|proxy
init|=
operator|new
name|SocketProxy
argument_list|()
decl_stmt|;
name|proxy
operator|.
name|setTarget
argument_list|(
operator|new
name|URI
argument_list|(
literal|"tcp://localhost:"
operator|+
name|stompBrokerUri
operator|.
name|getPort
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|proxy
operator|.
name|setReceiveBufferSize
argument_list|(
name|receiveBufferSize
argument_list|)
expr_stmt|;
name|proxy
operator|.
name|open
argument_list|()
expr_stmt|;
name|StompConnection
name|stompConnection
init|=
operator|new
name|StompConnection
argument_list|()
decl_stmt|;
name|stompConnection
operator|.
name|open
argument_list|(
operator|new
name|Socket
argument_list|(
literal|"localhost"
argument_list|,
name|proxy
operator|.
name|getUrl
argument_list|()
operator|.
name|getPort
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|stompConnection
operator|.
name|getStompSocket
argument_list|()
operator|.
name|setTcpNoDelay
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|String
name|frame
init|=
literal|"CONNECT\n"
operator|+
literal|"login: system\n"
operator|+
literal|"passcode: manager\n\n"
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
name|stompConnection
operator|.
name|receiveFrame
argument_list|()
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
name|frame
operator|=
literal|"SUBSCRIBE\n"
operator|+
literal|"destination:/queue/"
operator|+
name|dest
operator|.
name|getQueueName
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
comment|// ensure dispatch has started before pause
name|frame
operator|=
name|stompConnection
operator|.
name|receiveFrame
argument_list|()
expr_stmt|;
name|assertTrue
argument_list|(
name|frame
operator|.
name|startsWith
argument_list|(
literal|"MESSAGE"
argument_list|)
argument_list|)
expr_stmt|;
name|proxy
operator|.
name|pause
argument_list|()
expr_stmt|;
comment|// writes should back up... writeTimeout will kick in a abort the connection
name|TimeUnit
operator|.
name|SECONDS
operator|.
name|sleep
argument_list|(
literal|1
argument_list|)
expr_stmt|;
comment|// see the blocked threads
comment|//dumpAllThreads("blocked on write");
comment|// abort should be done after this
name|TimeUnit
operator|.
name|SECONDS
operator|.
name|sleep
argument_list|(
literal|10
argument_list|)
expr_stmt|;
name|proxy
operator|.
name|goOn
argument_list|()
expr_stmt|;
comment|// get a buffered message
name|frame
operator|=
name|stompConnection
operator|.
name|receiveFrame
argument_list|()
expr_stmt|;
name|assertTrue
argument_list|(
name|frame
operator|.
name|startsWith
argument_list|(
literal|"MESSAGE"
argument_list|)
argument_list|)
expr_stmt|;
comment|// verify connection is dead
try|try
block|{
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
literal|200
condition|;
name|i
operator|++
control|)
block|{
name|stompConnection
operator|.
name|send
argument_list|(
literal|"/queue/"
operator|+
name|dest
operator|.
name|getPhysicalName
argument_list|()
argument_list|,
literal|"ShouldBeDeadConnectionText"
operator|+
name|i
argument_list|)
expr_stmt|;
block|}
name|fail
argument_list|(
literal|"expected send to fail with timeout out connection"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|SocketException
name|expected
parameter_list|)
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"got exception on send after timeout: "
operator|+
name|expected
argument_list|)
expr_stmt|;
block|}
block|}
specifier|private
name|String
name|initMessagePrefix
parameter_list|(
name|int
name|i
parameter_list|)
block|{
name|byte
index|[]
name|content
init|=
operator|new
name|byte
index|[
name|i
index|]
decl_stmt|;
return|return
operator|new
name|String
argument_list|(
name|content
argument_list|)
return|;
block|}
specifier|public
specifier|static
name|Test
name|suite
parameter_list|()
block|{
return|return
name|suite
argument_list|(
name|SoWriteTimeoutTest
operator|.
name|class
argument_list|)
return|;
block|}
block|}
end_class

end_unit

