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
name|java
operator|.
name|util
operator|.
name|Arrays
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
name|CombinationTestSupport
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
name|util
operator|.
name|MessageIdList
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
name|xbean
operator|.
name|XBeanBrokerFactory
import|;
end_import

begin_class
specifier|public
class|class
name|TwoBrokerMulticastQueueTest
extends|extends
name|CombinationTestSupport
block|{
specifier|public
specifier|static
specifier|final
name|int
name|MESSAGE_COUNT
init|=
literal|100
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|int
name|BROKER_COUNT
init|=
literal|2
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|int
name|CONSUMER_COUNT
init|=
literal|20
decl_stmt|;
specifier|public
name|String
name|sendUri
decl_stmt|;
specifier|public
name|String
name|recvUri
decl_stmt|;
specifier|private
name|BrokerService
index|[]
name|brokers
decl_stmt|;
specifier|private
name|String
name|groupId
decl_stmt|;
specifier|public
specifier|static
name|Test
name|suite
parameter_list|()
block|{
return|return
name|suite
argument_list|(
name|TwoBrokerMulticastQueueTest
operator|.
name|class
argument_list|)
return|;
block|}
specifier|public
specifier|static
name|void
name|main
parameter_list|(
name|String
index|[]
name|args
parameter_list|)
block|{
name|junit
operator|.
name|textui
operator|.
name|TestRunner
operator|.
name|run
argument_list|(
name|suite
argument_list|()
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|setUp
parameter_list|()
throws|throws
name|Exception
block|{
name|groupId
operator|=
name|getClass
argument_list|()
operator|.
name|getName
argument_list|()
operator|+
literal|"-"
operator|+
name|System
operator|.
name|currentTimeMillis
argument_list|()
expr_stmt|;
name|System
operator|.
name|setProperty
argument_list|(
literal|"groupId"
argument_list|,
name|groupId
argument_list|)
expr_stmt|;
name|super
operator|.
name|setAutoFail
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|super
operator|.
name|setUp
argument_list|()
expr_stmt|;
block|}
specifier|public
name|void
name|tearDown
parameter_list|()
throws|throws
name|Exception
block|{
if|if
condition|(
name|brokers
operator|!=
literal|null
condition|)
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
name|BROKER_COUNT
condition|;
name|i
operator|++
control|)
block|{
if|if
condition|(
name|brokers
index|[
name|i
index|]
operator|!=
literal|null
condition|)
block|{
name|brokers
index|[
name|i
index|]
operator|.
name|stop
argument_list|()
expr_stmt|;
block|}
block|}
name|super
operator|.
name|tearDown
argument_list|()
expr_stmt|;
block|}
block|}
specifier|private
name|void
name|doSendReceiveTest
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
literal|"TEST.FOO"
argument_list|)
decl_stmt|;
name|ConnectionFactory
name|sendFactory
init|=
name|createConnectionFactory
argument_list|(
name|sendUri
argument_list|)
decl_stmt|;
name|Connection
name|conn
init|=
name|createConnection
argument_list|(
name|sendFactory
argument_list|)
decl_stmt|;
name|sendMessages
argument_list|(
name|conn
argument_list|,
name|dest
argument_list|,
name|MESSAGE_COUNT
argument_list|)
expr_stmt|;
name|Thread
operator|.
name|sleep
argument_list|(
literal|500
argument_list|)
expr_stmt|;
name|ConnectionFactory
name|recvFactory
init|=
name|createConnectionFactory
argument_list|(
name|recvUri
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
name|MESSAGE_COUNT
argument_list|,
name|receiveMessages
argument_list|(
name|createConnection
argument_list|(
name|recvFactory
argument_list|)
argument_list|,
name|dest
argument_list|,
literal|0
argument_list|)
argument_list|)
expr_stmt|;
block|}
specifier|private
name|void
name|doMultipleConsumersConnectTest
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
literal|"TEST.FOO"
argument_list|)
decl_stmt|;
name|ConnectionFactory
name|sendFactory
init|=
name|createConnectionFactory
argument_list|(
name|sendUri
argument_list|)
decl_stmt|;
name|Connection
name|conn
init|=
name|createConnection
argument_list|(
name|sendFactory
argument_list|)
decl_stmt|;
name|sendMessages
argument_list|(
name|conn
argument_list|,
name|dest
argument_list|,
name|MESSAGE_COUNT
argument_list|)
expr_stmt|;
name|Thread
operator|.
name|sleep
argument_list|(
literal|500
argument_list|)
expr_stmt|;
name|ConnectionFactory
name|recvFactory
init|=
name|createConnectionFactory
argument_list|(
name|recvUri
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
name|MESSAGE_COUNT
argument_list|,
name|receiveMessages
argument_list|(
name|createConnection
argument_list|(
name|recvFactory
argument_list|)
argument_list|,
name|dest
argument_list|,
literal|0
argument_list|)
argument_list|)
expr_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
operator|(
name|CONSUMER_COUNT
operator|-
literal|1
operator|)
condition|;
name|i
operator|++
control|)
block|{
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|receiveMessages
argument_list|(
name|createConnection
argument_list|(
name|recvFactory
argument_list|)
argument_list|,
name|dest
argument_list|,
literal|200
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
specifier|public
name|void
name|initCombosForTestSendReceive
parameter_list|()
block|{
name|addCombinationValues
argument_list|(
literal|"sendUri"
argument_list|,
operator|new
name|Object
index|[]
block|{
literal|"tcp://localhost:61616"
block|,
literal|"tcp://localhost:61617"
block|}
argument_list|)
expr_stmt|;
name|addCombinationValues
argument_list|(
literal|"recvUri"
argument_list|,
operator|new
name|Object
index|[]
block|{
literal|"tcp://localhost:61616"
block|,
literal|"tcp://localhost:61617"
block|}
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|testSendReceive
parameter_list|()
throws|throws
name|Exception
block|{
name|createMulticastBrokerNetwork
argument_list|()
expr_stmt|;
name|doSendReceiveTest
argument_list|()
expr_stmt|;
block|}
specifier|public
name|void
name|initCombosForTestMultipleConsumersConnect
parameter_list|()
block|{
name|addCombinationValues
argument_list|(
literal|"sendUri"
argument_list|,
operator|new
name|Object
index|[]
block|{
literal|"tcp://localhost:61616"
block|,
literal|"tcp://localhost:61617"
block|}
argument_list|)
expr_stmt|;
name|addCombinationValues
argument_list|(
literal|"recvUri"
argument_list|,
operator|new
name|Object
index|[]
block|{
literal|"tcp://localhost:61616"
block|,
literal|"tcp://localhost:61617"
block|}
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|testMultipleConsumersConnect
parameter_list|()
throws|throws
name|Exception
block|{
name|createMulticastBrokerNetwork
argument_list|()
expr_stmt|;
name|doMultipleConsumersConnectTest
argument_list|()
expr_stmt|;
block|}
specifier|public
name|void
name|testSendReceiveUsingFailover
parameter_list|()
throws|throws
name|Exception
block|{
name|sendUri
operator|=
literal|"failover:(tcp://localhost:61616,tcp://localhost:61617)"
expr_stmt|;
name|recvUri
operator|=
literal|"failover:(tcp://localhost:61616,tcp://localhost:61617)"
expr_stmt|;
name|createMulticastBrokerNetwork
argument_list|()
expr_stmt|;
name|doSendReceiveTest
argument_list|()
expr_stmt|;
block|}
specifier|public
name|void
name|testMultipleConsumersConnectUsingFailover
parameter_list|()
throws|throws
name|Exception
block|{
name|sendUri
operator|=
literal|"failover:(tcp://localhost:61616,tcp://localhost:61617)"
expr_stmt|;
name|recvUri
operator|=
literal|"failover:(tcp://localhost:61616,tcp://localhost:61617)"
expr_stmt|;
name|createMulticastBrokerNetwork
argument_list|()
expr_stmt|;
name|doMultipleConsumersConnectTest
argument_list|()
expr_stmt|;
block|}
specifier|public
name|void
name|testSendReceiveUsingDiscovery
parameter_list|()
throws|throws
name|Exception
block|{
name|sendUri
operator|=
literal|"discovery:multicast://default?group="
operator|+
name|groupId
expr_stmt|;
name|recvUri
operator|=
literal|"discovery:multicast://default?group="
operator|+
name|groupId
expr_stmt|;
name|createMulticastBrokerNetwork
argument_list|()
expr_stmt|;
name|doSendReceiveTest
argument_list|()
expr_stmt|;
block|}
specifier|public
name|void
name|testMultipleConsumersConnectUsingDiscovery
parameter_list|()
throws|throws
name|Exception
block|{
name|sendUri
operator|=
literal|"discovery:multicast://default?group="
operator|+
name|groupId
expr_stmt|;
name|recvUri
operator|=
literal|"discovery:multicast://default?group="
operator|+
name|groupId
expr_stmt|;
name|createMulticastBrokerNetwork
argument_list|()
expr_stmt|;
name|doMultipleConsumersConnectTest
argument_list|()
expr_stmt|;
block|}
specifier|public
name|void
name|testSendReceiveUsingAutoAssignFailover
parameter_list|()
throws|throws
name|Exception
block|{
name|sendUri
operator|=
literal|"failover:(discovery:multicast:default?group=//"
operator|+
name|groupId
operator|+
literal|")"
expr_stmt|;
name|recvUri
operator|=
literal|"failover:(discovery:multicast:default?group=//"
operator|+
name|groupId
operator|+
literal|")"
expr_stmt|;
name|createAutoAssignMulticastBrokerNetwork
argument_list|()
expr_stmt|;
name|doSendReceiveTest
argument_list|()
expr_stmt|;
block|}
specifier|public
name|void
name|testMultipleConsumersConnectUsingAutoAssignFailover
parameter_list|()
throws|throws
name|Exception
block|{
name|sendUri
operator|=
literal|"failover:(discovery:multicast:default?group=//"
operator|+
name|groupId
operator|+
literal|")"
expr_stmt|;
name|recvUri
operator|=
literal|"failover:(discovery:multicast:default?group=//"
operator|+
name|groupId
operator|+
literal|")"
expr_stmt|;
name|createAutoAssignMulticastBrokerNetwork
argument_list|()
expr_stmt|;
name|doMultipleConsumersConnectTest
argument_list|()
expr_stmt|;
block|}
specifier|public
name|void
name|testSendReceiveUsingAutoAssignDiscovery
parameter_list|()
throws|throws
name|Exception
block|{
name|sendUri
operator|=
literal|"discovery:multicast://default?group="
operator|+
name|groupId
expr_stmt|;
name|recvUri
operator|=
literal|"discovery:multicast://default?group="
operator|+
name|groupId
expr_stmt|;
name|createAutoAssignMulticastBrokerNetwork
argument_list|()
expr_stmt|;
name|doSendReceiveTest
argument_list|()
expr_stmt|;
block|}
specifier|public
name|void
name|testMultipleConsumersConnectUsingAutoAssignDiscovery
parameter_list|()
throws|throws
name|Exception
block|{
name|sendUri
operator|=
literal|"discovery:multicast://default?group="
operator|+
name|groupId
expr_stmt|;
name|recvUri
operator|=
literal|"discovery:multicast://default?group="
operator|+
name|groupId
expr_stmt|;
name|createAutoAssignMulticastBrokerNetwork
argument_list|()
expr_stmt|;
name|doMultipleConsumersConnectTest
argument_list|()
expr_stmt|;
block|}
specifier|protected
name|void
name|createMulticastBrokerNetwork
parameter_list|()
throws|throws
name|Exception
block|{
name|brokers
operator|=
operator|new
name|BrokerService
index|[
name|BROKER_COUNT
index|]
expr_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|BROKER_COUNT
condition|;
name|i
operator|++
control|)
block|{
name|brokers
index|[
name|i
index|]
operator|=
name|createBroker
argument_list|(
literal|"org/apache/activemq/usecases/multicast-broker-"
operator|+
operator|(
name|i
operator|+
literal|1
operator|)
operator|+
literal|".xml"
argument_list|)
expr_stmt|;
name|brokers
index|[
name|i
index|]
operator|.
name|start
argument_list|()
expr_stmt|;
block|}
comment|// Let the brokers discover each other first
name|Thread
operator|.
name|sleep
argument_list|(
literal|1000
argument_list|)
expr_stmt|;
block|}
specifier|protected
name|void
name|createAutoAssignMulticastBrokerNetwork
parameter_list|()
throws|throws
name|Exception
block|{
name|brokers
operator|=
operator|new
name|BrokerService
index|[
name|BROKER_COUNT
index|]
expr_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|BROKER_COUNT
condition|;
name|i
operator|++
control|)
block|{
name|brokers
index|[
name|i
index|]
operator|=
name|createBroker
argument_list|(
literal|"org/apache/activemq/usecases/multicast-broker-auto.xml"
argument_list|)
expr_stmt|;
name|brokers
index|[
name|i
index|]
operator|.
name|start
argument_list|()
expr_stmt|;
block|}
comment|// Let the brokers discover each other first
name|Thread
operator|.
name|sleep
argument_list|(
literal|1000
argument_list|)
expr_stmt|;
block|}
specifier|protected
name|BrokerService
name|createBroker
parameter_list|(
name|String
name|uri
parameter_list|)
throws|throws
name|Exception
block|{
return|return
operator|(
operator|new
name|XBeanBrokerFactory
argument_list|()
operator|)
operator|.
name|createBroker
argument_list|(
operator|new
name|URI
argument_list|(
name|uri
argument_list|)
argument_list|)
return|;
block|}
specifier|protected
name|ConnectionFactory
name|createConnectionFactory
parameter_list|(
name|String
name|uri
parameter_list|)
block|{
return|return
operator|new
name|ActiveMQConnectionFactory
argument_list|(
name|uri
argument_list|)
return|;
block|}
specifier|protected
name|Connection
name|createConnection
parameter_list|(
name|ConnectionFactory
name|factory
parameter_list|)
throws|throws
name|JMSException
block|{
name|Connection
name|conn
init|=
name|factory
operator|.
name|createConnection
argument_list|()
decl_stmt|;
return|return
name|conn
return|;
block|}
specifier|protected
name|int
name|receiveMessages
parameter_list|(
name|Connection
name|conn
parameter_list|,
name|Destination
name|dest
parameter_list|,
name|int
name|waitTime
parameter_list|)
throws|throws
name|JMSException
throws|,
name|InterruptedException
block|{
name|conn
operator|.
name|start
argument_list|()
expr_stmt|;
name|MessageIdList
name|list
init|=
operator|new
name|MessageIdList
argument_list|()
decl_stmt|;
name|Session
name|sess
init|=
name|conn
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
name|consumer
init|=
name|sess
operator|.
name|createConsumer
argument_list|(
name|dest
argument_list|)
decl_stmt|;
name|consumer
operator|.
name|setMessageListener
argument_list|(
name|list
argument_list|)
expr_stmt|;
if|if
condition|(
name|waitTime
operator|>
literal|0
condition|)
block|{
name|Thread
operator|.
name|sleep
argument_list|(
name|waitTime
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|list
operator|.
name|waitForMessagesToArrive
argument_list|(
name|MESSAGE_COUNT
argument_list|)
expr_stmt|;
block|}
name|conn
operator|.
name|close
argument_list|()
expr_stmt|;
return|return
name|list
operator|.
name|getMessageCount
argument_list|()
return|;
block|}
specifier|protected
name|void
name|sendMessages
parameter_list|(
name|Connection
name|conn
parameter_list|,
name|Destination
name|dest
parameter_list|,
name|int
name|count
parameter_list|)
throws|throws
name|JMSException
block|{
name|conn
operator|.
name|start
argument_list|()
expr_stmt|;
name|Session
name|sess
init|=
name|conn
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
name|prod
init|=
name|sess
operator|.
name|createProducer
argument_list|(
name|dest
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
name|count
condition|;
name|i
operator|++
control|)
block|{
name|prod
operator|.
name|send
argument_list|(
name|createTextMessage
argument_list|(
name|sess
argument_list|,
literal|"Message "
operator|+
name|i
argument_list|,
literal|1024
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|conn
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
specifier|protected
name|TextMessage
name|createTextMessage
parameter_list|(
name|Session
name|session
parameter_list|,
name|String
name|initText
parameter_list|,
name|int
name|messageSize
parameter_list|)
throws|throws
name|JMSException
block|{
name|TextMessage
name|msg
init|=
name|session
operator|.
name|createTextMessage
argument_list|()
decl_stmt|;
comment|// Pad message text
if|if
condition|(
name|initText
operator|.
name|length
argument_list|()
operator|<
name|messageSize
condition|)
block|{
name|char
index|[]
name|data
init|=
operator|new
name|char
index|[
name|messageSize
operator|-
name|initText
operator|.
name|length
argument_list|()
index|]
decl_stmt|;
name|Arrays
operator|.
name|fill
argument_list|(
name|data
argument_list|,
literal|'*'
argument_list|)
expr_stmt|;
name|String
name|str
init|=
operator|new
name|String
argument_list|(
name|data
argument_list|)
decl_stmt|;
name|msg
operator|.
name|setText
argument_list|(
name|initText
operator|+
name|str
argument_list|)
expr_stmt|;
comment|// Do not pad message text
block|}
else|else
block|{
name|msg
operator|.
name|setText
argument_list|(
name|initText
argument_list|)
expr_stmt|;
block|}
return|return
name|msg
return|;
block|}
block|}
end_class

end_unit

