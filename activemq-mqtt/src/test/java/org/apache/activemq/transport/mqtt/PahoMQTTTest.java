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
name|mqtt
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
name|ActiveMQConnection
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
name|Wait
import|;
end_import

begin_import
import|import
name|org
operator|.
name|eclipse
operator|.
name|paho
operator|.
name|client
operator|.
name|mqttv3
operator|.
name|*
import|;
end_import

begin_import
import|import
name|org
operator|.
name|eclipse
operator|.
name|paho
operator|.
name|client
operator|.
name|mqttv3
operator|.
name|persist
operator|.
name|MemoryPersistence
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
name|MessageListener
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
name|TimeUnit
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
name|atomic
operator|.
name|AtomicInteger
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
name|atomic
operator|.
name|AtomicReference
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
name|PahoMQTTTest
extends|extends
name|MQTTTestSupport
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
name|PahoMQTTTest
operator|.
name|class
argument_list|)
decl_stmt|;
annotation|@
name|Test
argument_list|(
name|timeout
operator|=
literal|300000
argument_list|)
specifier|public
name|void
name|testLotsOfClients
parameter_list|()
throws|throws
name|Exception
block|{
specifier|final
name|int
name|CLIENTS
init|=
name|Integer
operator|.
name|getInteger
argument_list|(
literal|"PahoMQTTTest.CLIENTS"
argument_list|,
literal|100
argument_list|)
decl_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"Using: {} clients"
argument_list|,
name|CLIENTS
argument_list|)
expr_stmt|;
name|ActiveMQConnection
name|activeMQConnection
init|=
operator|(
name|ActiveMQConnection
operator|)
name|cf
operator|.
name|createConnection
argument_list|()
decl_stmt|;
name|activeMQConnection
operator|.
name|start
argument_list|()
expr_stmt|;
name|Session
name|s
init|=
name|activeMQConnection
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
name|s
operator|.
name|createConsumer
argument_list|(
name|s
operator|.
name|createTopic
argument_list|(
literal|"test"
argument_list|)
argument_list|)
decl_stmt|;
specifier|final
name|AtomicInteger
name|receiveCounter
init|=
operator|new
name|AtomicInteger
argument_list|()
decl_stmt|;
name|consumer
operator|.
name|setMessageListener
argument_list|(
operator|new
name|MessageListener
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|void
name|onMessage
parameter_list|(
name|Message
name|message
parameter_list|)
block|{
name|receiveCounter
operator|.
name|incrementAndGet
argument_list|()
expr_stmt|;
block|}
block|}
argument_list|)
expr_stmt|;
specifier|final
name|AtomicReference
argument_list|<
name|Throwable
argument_list|>
name|asyncError
init|=
operator|new
name|AtomicReference
argument_list|<
name|Throwable
argument_list|>
argument_list|()
decl_stmt|;
specifier|final
name|CountDownLatch
name|connectedDoneLatch
init|=
operator|new
name|CountDownLatch
argument_list|(
name|CLIENTS
argument_list|)
decl_stmt|;
specifier|final
name|CountDownLatch
name|disconnectDoneLatch
init|=
operator|new
name|CountDownLatch
argument_list|(
name|CLIENTS
argument_list|)
decl_stmt|;
specifier|final
name|CountDownLatch
name|sendBarrier
init|=
operator|new
name|CountDownLatch
argument_list|(
literal|1
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
name|CLIENTS
condition|;
name|i
operator|++
control|)
block|{
name|Thread
operator|.
name|sleep
argument_list|(
literal|10
argument_list|)
expr_stmt|;
operator|new
name|Thread
argument_list|(
literal|null
argument_list|,
literal|null
argument_list|,
literal|"client:"
operator|+
name|i
argument_list|)
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
name|MqttClient
name|client
init|=
operator|new
name|MqttClient
argument_list|(
literal|"tcp://localhost:"
operator|+
name|getPort
argument_list|()
argument_list|,
name|Thread
operator|.
name|currentThread
argument_list|()
operator|.
name|getName
argument_list|()
argument_list|,
operator|new
name|MemoryPersistence
argument_list|()
argument_list|)
decl_stmt|;
name|client
operator|.
name|connect
argument_list|()
expr_stmt|;
name|connectedDoneLatch
operator|.
name|countDown
argument_list|()
expr_stmt|;
name|sendBarrier
operator|.
name|await
argument_list|()
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
literal|10
condition|;
name|i
operator|++
control|)
block|{
name|Thread
operator|.
name|sleep
argument_list|(
literal|1000
argument_list|)
expr_stmt|;
name|client
operator|.
name|publish
argument_list|(
literal|"test"
argument_list|,
literal|"hello"
operator|.
name|getBytes
argument_list|()
argument_list|,
literal|1
argument_list|,
literal|false
argument_list|)
expr_stmt|;
block|}
name|client
operator|.
name|disconnect
argument_list|()
expr_stmt|;
name|client
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Throwable
name|e
parameter_list|)
block|{
name|e
operator|.
name|printStackTrace
argument_list|()
expr_stmt|;
name|asyncError
operator|.
name|set
argument_list|(
name|e
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|disconnectDoneLatch
operator|.
name|countDown
argument_list|()
expr_stmt|;
block|}
block|}
block|}
operator|.
name|start
argument_list|()
expr_stmt|;
block|}
name|connectedDoneLatch
operator|.
name|await
argument_list|()
expr_stmt|;
name|assertNull
argument_list|(
literal|"Async error: "
operator|+
name|asyncError
operator|.
name|get
argument_list|()
argument_list|,
name|asyncError
operator|.
name|get
argument_list|()
argument_list|)
expr_stmt|;
name|sendBarrier
operator|.
name|countDown
argument_list|()
expr_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"All clients connected... waiting to receive sent messages..."
argument_list|)
expr_stmt|;
comment|// We should eventually get all the messages.
name|within
argument_list|(
literal|30
argument_list|,
name|TimeUnit
operator|.
name|SECONDS
argument_list|,
operator|new
name|Task
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|void
name|run
parameter_list|()
throws|throws
name|Exception
block|{
name|assertTrue
argument_list|(
name|receiveCounter
operator|.
name|get
argument_list|()
operator|==
name|CLIENTS
operator|*
literal|10
argument_list|)
expr_stmt|;
block|}
block|}
argument_list|)
expr_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"All messages received."
argument_list|)
expr_stmt|;
name|disconnectDoneLatch
operator|.
name|await
argument_list|()
expr_stmt|;
name|assertNull
argument_list|(
literal|"Async error: "
operator|+
name|asyncError
operator|.
name|get
argument_list|()
argument_list|,
name|asyncError
operator|.
name|get
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
argument_list|(
name|timeout
operator|=
literal|300000
argument_list|)
specifier|public
name|void
name|testSendAndReceiveMQTT
parameter_list|()
throws|throws
name|Exception
block|{
name|ActiveMQConnection
name|activeMQConnection
init|=
operator|(
name|ActiveMQConnection
operator|)
name|cf
operator|.
name|createConnection
argument_list|()
decl_stmt|;
name|activeMQConnection
operator|.
name|start
argument_list|()
expr_stmt|;
name|Session
name|s
init|=
name|activeMQConnection
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
name|s
operator|.
name|createConsumer
argument_list|(
name|s
operator|.
name|createTopic
argument_list|(
literal|"test"
argument_list|)
argument_list|)
decl_stmt|;
name|MqttClient
name|client
init|=
operator|new
name|MqttClient
argument_list|(
literal|"tcp://localhost:"
operator|+
name|getPort
argument_list|()
argument_list|,
literal|"clientid"
argument_list|,
operator|new
name|MemoryPersistence
argument_list|()
argument_list|)
decl_stmt|;
name|client
operator|.
name|connect
argument_list|()
expr_stmt|;
name|client
operator|.
name|publish
argument_list|(
literal|"test"
argument_list|,
literal|"hello"
operator|.
name|getBytes
argument_list|()
argument_list|,
literal|1
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|Message
name|msg
init|=
name|consumer
operator|.
name|receive
argument_list|(
literal|100
operator|*
literal|5
argument_list|)
decl_stmt|;
name|assertNotNull
argument_list|(
name|msg
argument_list|)
expr_stmt|;
name|client
operator|.
name|disconnect
argument_list|()
expr_stmt|;
name|client
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
argument_list|(
name|timeout
operator|=
literal|300000
argument_list|)
specifier|public
name|void
name|testSubs
parameter_list|()
throws|throws
name|Exception
block|{
name|stopBroker
argument_list|()
expr_stmt|;
name|protocolConfig
operator|=
literal|"transport.subscriptionStrategy=mqtt-virtual-topic-subscriptions"
expr_stmt|;
name|startBroker
argument_list|()
expr_stmt|;
specifier|final
name|DefaultListener
name|listener
init|=
operator|new
name|DefaultListener
argument_list|()
decl_stmt|;
comment|// subscriber connects and creates durable sub
name|MqttClient
name|client
init|=
name|createClient
argument_list|(
literal|false
argument_list|,
literal|"receive"
argument_list|,
name|listener
argument_list|)
decl_stmt|;
specifier|final
name|String
name|ACCOUNT_PREFIX
init|=
literal|"test/"
decl_stmt|;
name|client
operator|.
name|subscribe
argument_list|(
name|ACCOUNT_PREFIX
operator|+
literal|"1/2/3"
argument_list|)
expr_stmt|;
name|client
operator|.
name|subscribe
argument_list|(
name|ACCOUNT_PREFIX
operator|+
literal|"a/+/#"
argument_list|)
expr_stmt|;
name|client
operator|.
name|subscribe
argument_list|(
name|ACCOUNT_PREFIX
operator|+
literal|"#"
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|client
operator|.
name|getPendingDeliveryTokens
argument_list|()
operator|.
name|length
operator|==
literal|0
argument_list|)
expr_stmt|;
name|String
name|expectedResult
init|=
literal|"should get everything"
decl_stmt|;
name|client
operator|.
name|publish
argument_list|(
name|ACCOUNT_PREFIX
operator|+
literal|"1/2/3/4"
argument_list|,
name|expectedResult
operator|.
name|getBytes
argument_list|()
argument_list|,
literal|0
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|Wait
operator|.
name|waitFor
argument_list|(
operator|new
name|Wait
operator|.
name|Condition
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|boolean
name|isSatisified
parameter_list|()
throws|throws
name|Exception
block|{
return|return
name|listener
operator|.
name|result
operator|!=
literal|null
return|;
block|}
block|}
argument_list|,
name|TimeUnit
operator|.
name|SECONDS
operator|.
name|toMillis
argument_list|(
literal|45
argument_list|)
argument_list|,
name|TimeUnit
operator|.
name|MILLISECONDS
operator|.
name|toMillis
argument_list|(
literal|200
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|client
operator|.
name|getPendingDeliveryTokens
argument_list|()
operator|.
name|length
operator|==
literal|0
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|expectedResult
argument_list|,
name|listener
operator|.
name|result
argument_list|)
expr_stmt|;
name|expectedResult
operator|=
literal|"should get everything"
expr_stmt|;
name|listener
operator|.
name|result
operator|=
literal|null
expr_stmt|;
name|client
operator|.
name|publish
argument_list|(
name|ACCOUNT_PREFIX
operator|+
literal|"a/1/2"
argument_list|,
name|expectedResult
operator|.
name|getBytes
argument_list|()
argument_list|,
literal|0
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|Wait
operator|.
name|waitFor
argument_list|(
operator|new
name|Wait
operator|.
name|Condition
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|boolean
name|isSatisified
parameter_list|()
throws|throws
name|Exception
block|{
return|return
name|listener
operator|.
name|result
operator|!=
literal|null
return|;
block|}
block|}
argument_list|,
name|TimeUnit
operator|.
name|SECONDS
operator|.
name|toMillis
argument_list|(
literal|45
argument_list|)
argument_list|,
name|TimeUnit
operator|.
name|MILLISECONDS
operator|.
name|toMillis
argument_list|(
literal|200
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|expectedResult
argument_list|,
name|listener
operator|.
name|result
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|client
operator|.
name|getPendingDeliveryTokens
argument_list|()
operator|.
name|length
operator|==
literal|0
argument_list|)
expr_stmt|;
name|client
operator|.
name|unsubscribe
argument_list|(
name|ACCOUNT_PREFIX
operator|+
literal|"a/+/#"
argument_list|)
expr_stmt|;
name|client
operator|.
name|unsubscribe
argument_list|(
name|ACCOUNT_PREFIX
operator|+
literal|"#"
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|client
operator|.
name|getPendingDeliveryTokens
argument_list|()
operator|.
name|length
operator|==
literal|0
argument_list|)
expr_stmt|;
name|expectedResult
operator|=
literal|"should still get 1/2/3"
expr_stmt|;
name|listener
operator|.
name|result
operator|=
literal|null
expr_stmt|;
name|client
operator|.
name|publish
argument_list|(
name|ACCOUNT_PREFIX
operator|+
literal|"1/2/3"
argument_list|,
name|expectedResult
operator|.
name|getBytes
argument_list|()
argument_list|,
literal|0
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|Wait
operator|.
name|waitFor
argument_list|(
operator|new
name|Wait
operator|.
name|Condition
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|boolean
name|isSatisified
parameter_list|()
throws|throws
name|Exception
block|{
return|return
name|listener
operator|.
name|result
operator|!=
literal|null
return|;
block|}
block|}
argument_list|,
name|TimeUnit
operator|.
name|SECONDS
operator|.
name|toMillis
argument_list|(
literal|45
argument_list|)
argument_list|,
name|TimeUnit
operator|.
name|MILLISECONDS
operator|.
name|toMillis
argument_list|(
literal|200
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|expectedResult
argument_list|,
name|listener
operator|.
name|result
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|client
operator|.
name|getPendingDeliveryTokens
argument_list|()
operator|.
name|length
operator|==
literal|0
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
argument_list|(
name|timeout
operator|=
literal|300000
argument_list|)
specifier|public
name|void
name|testOverlappingTopics
parameter_list|()
throws|throws
name|Exception
block|{
name|stopBroker
argument_list|()
expr_stmt|;
name|protocolConfig
operator|=
literal|"transport.subscriptionStrategy=mqtt-virtual-topic-subscriptions"
expr_stmt|;
name|startBroker
argument_list|()
expr_stmt|;
specifier|final
name|DefaultListener
name|listener
init|=
operator|new
name|DefaultListener
argument_list|()
decl_stmt|;
comment|// subscriber connects and creates durable sub
name|MqttClient
name|client
init|=
name|createClient
argument_list|(
literal|false
argument_list|,
literal|"receive"
argument_list|,
name|listener
argument_list|)
decl_stmt|;
specifier|final
name|String
name|ACCOUNT_PREFIX
init|=
literal|"test/"
decl_stmt|;
comment|// *****************************************
comment|// check a simple # subscribe works
comment|// *****************************************
name|client
operator|.
name|subscribe
argument_list|(
name|ACCOUNT_PREFIX
operator|+
literal|"#"
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|client
operator|.
name|getPendingDeliveryTokens
argument_list|()
operator|.
name|length
operator|==
literal|0
argument_list|)
expr_stmt|;
name|String
name|expectedResult
init|=
literal|"hello mqtt broker on hash"
decl_stmt|;
name|client
operator|.
name|publish
argument_list|(
name|ACCOUNT_PREFIX
operator|+
literal|"a/b/c"
argument_list|,
name|expectedResult
operator|.
name|getBytes
argument_list|()
argument_list|,
literal|0
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|Wait
operator|.
name|waitFor
argument_list|(
operator|new
name|Wait
operator|.
name|Condition
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|boolean
name|isSatisified
parameter_list|()
throws|throws
name|Exception
block|{
return|return
name|listener
operator|.
name|result
operator|!=
literal|null
return|;
block|}
block|}
argument_list|,
name|TimeUnit
operator|.
name|SECONDS
operator|.
name|toMillis
argument_list|(
literal|45
argument_list|)
argument_list|,
name|TimeUnit
operator|.
name|MILLISECONDS
operator|.
name|toMillis
argument_list|(
literal|200
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|expectedResult
argument_list|,
name|listener
operator|.
name|result
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|client
operator|.
name|getPendingDeliveryTokens
argument_list|()
operator|.
name|length
operator|==
literal|0
argument_list|)
expr_stmt|;
name|expectedResult
operator|=
literal|"hello mqtt broker on a different topic"
expr_stmt|;
name|listener
operator|.
name|result
operator|=
literal|null
expr_stmt|;
name|client
operator|.
name|publish
argument_list|(
name|ACCOUNT_PREFIX
operator|+
literal|"1/2/3/4/5/6"
argument_list|,
name|expectedResult
operator|.
name|getBytes
argument_list|()
argument_list|,
literal|0
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|Wait
operator|.
name|waitFor
argument_list|(
operator|new
name|Wait
operator|.
name|Condition
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|boolean
name|isSatisified
parameter_list|()
throws|throws
name|Exception
block|{
return|return
name|listener
operator|.
name|result
operator|!=
literal|null
return|;
block|}
block|}
argument_list|,
name|TimeUnit
operator|.
name|SECONDS
operator|.
name|toMillis
argument_list|(
literal|45
argument_list|)
argument_list|,
name|TimeUnit
operator|.
name|MILLISECONDS
operator|.
name|toMillis
argument_list|(
literal|200
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|expectedResult
argument_list|,
name|listener
operator|.
name|result
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|client
operator|.
name|getPendingDeliveryTokens
argument_list|()
operator|.
name|length
operator|==
literal|0
argument_list|)
expr_stmt|;
comment|// *****************************************
comment|// now subscribe on a topic that overlaps the root # wildcard - we
comment|// should still get everything
comment|// *****************************************
name|client
operator|.
name|subscribe
argument_list|(
name|ACCOUNT_PREFIX
operator|+
literal|"1/2/3"
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|client
operator|.
name|getPendingDeliveryTokens
argument_list|()
operator|.
name|length
operator|==
literal|0
argument_list|)
expr_stmt|;
name|expectedResult
operator|=
literal|"hello mqtt broker on explicit topic"
expr_stmt|;
name|listener
operator|.
name|result
operator|=
literal|null
expr_stmt|;
name|client
operator|.
name|publish
argument_list|(
name|ACCOUNT_PREFIX
operator|+
literal|"1/2/3"
argument_list|,
name|expectedResult
operator|.
name|getBytes
argument_list|()
argument_list|,
literal|0
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|Wait
operator|.
name|waitFor
argument_list|(
operator|new
name|Wait
operator|.
name|Condition
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|boolean
name|isSatisified
parameter_list|()
throws|throws
name|Exception
block|{
return|return
name|listener
operator|.
name|result
operator|!=
literal|null
return|;
block|}
block|}
argument_list|,
name|TimeUnit
operator|.
name|SECONDS
operator|.
name|toMillis
argument_list|(
literal|45
argument_list|)
argument_list|,
name|TimeUnit
operator|.
name|MILLISECONDS
operator|.
name|toMillis
argument_list|(
literal|200
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|expectedResult
argument_list|,
name|listener
operator|.
name|result
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|client
operator|.
name|getPendingDeliveryTokens
argument_list|()
operator|.
name|length
operator|==
literal|0
argument_list|)
expr_stmt|;
name|expectedResult
operator|=
literal|"hello mqtt broker on some other topic"
expr_stmt|;
name|listener
operator|.
name|result
operator|=
literal|null
expr_stmt|;
name|client
operator|.
name|publish
argument_list|(
name|ACCOUNT_PREFIX
operator|+
literal|"a/b/c/d/e"
argument_list|,
name|expectedResult
operator|.
name|getBytes
argument_list|()
argument_list|,
literal|0
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|Wait
operator|.
name|waitFor
argument_list|(
operator|new
name|Wait
operator|.
name|Condition
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|boolean
name|isSatisified
parameter_list|()
throws|throws
name|Exception
block|{
return|return
name|listener
operator|.
name|result
operator|!=
literal|null
return|;
block|}
block|}
argument_list|,
name|TimeUnit
operator|.
name|SECONDS
operator|.
name|toMillis
argument_list|(
literal|45
argument_list|)
argument_list|,
name|TimeUnit
operator|.
name|MILLISECONDS
operator|.
name|toMillis
argument_list|(
literal|200
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|expectedResult
argument_list|,
name|listener
operator|.
name|result
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|client
operator|.
name|getPendingDeliveryTokens
argument_list|()
operator|.
name|length
operator|==
literal|0
argument_list|)
expr_stmt|;
comment|// *****************************************
comment|// now unsub hash - we should only get called back on 1/2/3
comment|// *****************************************
name|client
operator|.
name|unsubscribe
argument_list|(
name|ACCOUNT_PREFIX
operator|+
literal|"#"
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|client
operator|.
name|getPendingDeliveryTokens
argument_list|()
operator|.
name|length
operator|==
literal|0
argument_list|)
expr_stmt|;
name|expectedResult
operator|=
literal|"this should not come back..."
expr_stmt|;
name|listener
operator|.
name|result
operator|=
literal|null
expr_stmt|;
name|client
operator|.
name|publish
argument_list|(
name|ACCOUNT_PREFIX
operator|+
literal|"1/2/3/4"
argument_list|,
name|expectedResult
operator|.
name|getBytes
argument_list|()
argument_list|,
literal|0
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|Wait
operator|.
name|waitFor
argument_list|(
operator|new
name|Wait
operator|.
name|Condition
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|boolean
name|isSatisified
parameter_list|()
throws|throws
name|Exception
block|{
return|return
name|listener
operator|.
name|result
operator|!=
literal|null
return|;
block|}
block|}
argument_list|,
name|TimeUnit
operator|.
name|SECONDS
operator|.
name|toMillis
argument_list|(
literal|20
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|assertNull
argument_list|(
name|listener
operator|.
name|result
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|client
operator|.
name|getPendingDeliveryTokens
argument_list|()
operator|.
name|length
operator|==
literal|0
argument_list|)
expr_stmt|;
name|expectedResult
operator|=
literal|"this should not come back either..."
expr_stmt|;
name|listener
operator|.
name|result
operator|=
literal|null
expr_stmt|;
name|client
operator|.
name|publish
argument_list|(
name|ACCOUNT_PREFIX
operator|+
literal|"a/b/c"
argument_list|,
name|expectedResult
operator|.
name|getBytes
argument_list|()
argument_list|,
literal|0
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|Wait
operator|.
name|waitFor
argument_list|(
operator|new
name|Wait
operator|.
name|Condition
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|boolean
name|isSatisified
parameter_list|()
throws|throws
name|Exception
block|{
return|return
name|listener
operator|.
name|result
operator|!=
literal|null
return|;
block|}
block|}
argument_list|,
name|TimeUnit
operator|.
name|SECONDS
operator|.
name|toMillis
argument_list|(
literal|20
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|assertNull
argument_list|(
name|listener
operator|.
name|result
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|client
operator|.
name|getPendingDeliveryTokens
argument_list|()
operator|.
name|length
operator|==
literal|0
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
argument_list|(
name|timeout
operator|=
literal|300000
argument_list|)
specifier|public
name|void
name|testCleanSession
parameter_list|()
throws|throws
name|Exception
block|{
name|String
name|topic
init|=
literal|"test"
decl_stmt|;
specifier|final
name|DefaultListener
name|listener
init|=
operator|new
name|DefaultListener
argument_list|()
decl_stmt|;
comment|// subscriber connects and creates durable sub
name|LOG
operator|.
name|info
argument_list|(
literal|"Connecting durable subscriber..."
argument_list|)
expr_stmt|;
name|MqttClient
name|client
init|=
name|createClient
argument_list|(
literal|false
argument_list|,
literal|"receive"
argument_list|,
name|listener
argument_list|)
decl_stmt|;
comment|// subscribe and wait for the retain message to arrive
name|LOG
operator|.
name|info
argument_list|(
literal|"Subscribing durable subscriber..."
argument_list|)
expr_stmt|;
name|client
operator|.
name|subscribe
argument_list|(
name|topic
argument_list|,
literal|1
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|client
operator|.
name|getPendingDeliveryTokens
argument_list|()
operator|.
name|length
operator|==
literal|0
argument_list|)
expr_stmt|;
name|disconnect
argument_list|(
name|client
argument_list|)
expr_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"Disconnected durable subscriber."
argument_list|)
expr_stmt|;
comment|// Publish message with QoS 1
name|MqttClient
name|client2
init|=
name|createClient
argument_list|(
literal|true
argument_list|,
literal|"publish"
argument_list|,
name|listener
argument_list|)
decl_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"Publish message with QoS 1..."
argument_list|)
expr_stmt|;
name|String
name|expectedResult
init|=
literal|"QOS 1 message"
decl_stmt|;
name|client2
operator|.
name|publish
argument_list|(
name|topic
argument_list|,
name|expectedResult
operator|.
name|getBytes
argument_list|()
argument_list|,
literal|1
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|waitForDelivery
argument_list|(
name|client2
argument_list|)
expr_stmt|;
comment|// Publish message with QoS 0
name|LOG
operator|.
name|info
argument_list|(
literal|"Publish message with QoS 0..."
argument_list|)
expr_stmt|;
name|expectedResult
operator|=
literal|"QOS 0 message"
expr_stmt|;
name|client2
operator|.
name|publish
argument_list|(
name|topic
argument_list|,
name|expectedResult
operator|.
name|getBytes
argument_list|()
argument_list|,
literal|0
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|waitForDelivery
argument_list|(
name|client2
argument_list|)
expr_stmt|;
comment|// subscriber reconnects
name|LOG
operator|.
name|info
argument_list|(
literal|"Reconnecting durable subscriber..."
argument_list|)
expr_stmt|;
name|MqttClient
name|client3
init|=
name|createClient
argument_list|(
literal|false
argument_list|,
literal|"receive"
argument_list|,
name|listener
argument_list|)
decl_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"Subscribing durable subscriber..."
argument_list|)
expr_stmt|;
name|client3
operator|.
name|subscribe
argument_list|(
name|topic
argument_list|,
literal|1
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|Wait
operator|.
name|waitFor
argument_list|(
operator|new
name|Wait
operator|.
name|Condition
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|boolean
name|isSatisified
parameter_list|()
throws|throws
name|Exception
block|{
return|return
name|listener
operator|.
name|received
operator|==
literal|2
return|;
block|}
block|}
argument_list|,
name|TimeUnit
operator|.
name|SECONDS
operator|.
name|toMillis
argument_list|(
literal|15
argument_list|)
argument_list|,
name|TimeUnit
operator|.
name|MILLISECONDS
operator|.
name|toMillis
argument_list|(
literal|100
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|2
argument_list|,
name|listener
operator|.
name|received
argument_list|)
expr_stmt|;
name|disconnect
argument_list|(
name|client3
argument_list|)
expr_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"Disconnected durable subscriber."
argument_list|)
expr_stmt|;
comment|// make sure we consumed everything
name|listener
operator|.
name|received
operator|=
literal|0
expr_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"Reconnecting durable subscriber..."
argument_list|)
expr_stmt|;
name|MqttClient
name|client4
init|=
name|createClient
argument_list|(
literal|false
argument_list|,
literal|"receive"
argument_list|,
name|listener
argument_list|)
decl_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"Subscribing durable subscriber..."
argument_list|)
expr_stmt|;
name|client4
operator|.
name|subscribe
argument_list|(
name|topic
argument_list|,
literal|1
argument_list|)
expr_stmt|;
name|Thread
operator|.
name|sleep
argument_list|(
literal|3
operator|*
literal|1000
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|listener
operator|.
name|received
argument_list|)
expr_stmt|;
block|}
specifier|protected
name|MqttClient
name|createClient
parameter_list|(
name|boolean
name|cleanSession
parameter_list|,
name|String
name|clientId
parameter_list|,
name|MqttCallback
name|listener
parameter_list|)
throws|throws
name|Exception
block|{
name|MqttConnectOptions
name|options
init|=
operator|new
name|MqttConnectOptions
argument_list|()
decl_stmt|;
name|options
operator|.
name|setCleanSession
argument_list|(
name|cleanSession
argument_list|)
expr_stmt|;
specifier|final
name|MqttClient
name|client
init|=
operator|new
name|MqttClient
argument_list|(
literal|"tcp://localhost:"
operator|+
name|getPort
argument_list|()
argument_list|,
name|clientId
argument_list|,
operator|new
name|MemoryPersistence
argument_list|()
argument_list|)
decl_stmt|;
name|client
operator|.
name|setCallback
argument_list|(
name|listener
argument_list|)
expr_stmt|;
name|client
operator|.
name|connect
argument_list|(
name|options
argument_list|)
expr_stmt|;
name|Wait
operator|.
name|waitFor
argument_list|(
operator|new
name|Wait
operator|.
name|Condition
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|boolean
name|isSatisified
parameter_list|()
throws|throws
name|Exception
block|{
return|return
name|client
operator|.
name|isConnected
argument_list|()
return|;
block|}
block|}
argument_list|,
name|TimeUnit
operator|.
name|SECONDS
operator|.
name|toMillis
argument_list|(
literal|15
argument_list|)
argument_list|,
name|TimeUnit
operator|.
name|MILLISECONDS
operator|.
name|toMillis
argument_list|(
literal|100
argument_list|)
argument_list|)
expr_stmt|;
return|return
name|client
return|;
block|}
specifier|protected
name|void
name|disconnect
parameter_list|(
specifier|final
name|MqttClient
name|client
parameter_list|)
throws|throws
name|Exception
block|{
name|client
operator|.
name|disconnect
argument_list|()
expr_stmt|;
name|client
operator|.
name|close
argument_list|()
expr_stmt|;
name|Wait
operator|.
name|waitFor
argument_list|(
operator|new
name|Wait
operator|.
name|Condition
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|boolean
name|isSatisified
parameter_list|()
throws|throws
name|Exception
block|{
return|return
operator|!
name|client
operator|.
name|isConnected
argument_list|()
return|;
block|}
block|}
argument_list|,
name|TimeUnit
operator|.
name|SECONDS
operator|.
name|toMillis
argument_list|(
literal|15
argument_list|)
argument_list|,
name|TimeUnit
operator|.
name|MILLISECONDS
operator|.
name|toMillis
argument_list|(
literal|100
argument_list|)
argument_list|)
expr_stmt|;
block|}
specifier|protected
name|void
name|waitForDelivery
parameter_list|(
specifier|final
name|MqttClient
name|client
parameter_list|)
throws|throws
name|Exception
block|{
name|Wait
operator|.
name|waitFor
argument_list|(
operator|new
name|Wait
operator|.
name|Condition
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|boolean
name|isSatisified
parameter_list|()
throws|throws
name|Exception
block|{
return|return
name|client
operator|.
name|getPendingDeliveryTokens
argument_list|()
operator|.
name|length
operator|==
literal|0
return|;
block|}
block|}
argument_list|,
name|TimeUnit
operator|.
name|SECONDS
operator|.
name|toMillis
argument_list|(
literal|30
argument_list|)
argument_list|,
name|TimeUnit
operator|.
name|MILLISECONDS
operator|.
name|toMillis
argument_list|(
literal|200
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|client
operator|.
name|getPendingDeliveryTokens
argument_list|()
operator|.
name|length
operator|==
literal|0
argument_list|)
expr_stmt|;
block|}
specifier|static
class|class
name|DefaultListener
implements|implements
name|MqttCallback
block|{
name|int
name|received
init|=
literal|0
decl_stmt|;
name|String
name|result
decl_stmt|;
annotation|@
name|Override
specifier|public
name|void
name|connectionLost
parameter_list|(
name|Throwable
name|cause
parameter_list|)
block|{          }
annotation|@
name|Override
specifier|public
name|void
name|messageArrived
parameter_list|(
name|String
name|topic
parameter_list|,
name|MqttMessage
name|message
parameter_list|)
throws|throws
name|Exception
block|{
name|LOG
operator|.
name|debug
argument_list|(
literal|"Received: "
operator|+
name|message
argument_list|)
expr_stmt|;
name|received
operator|++
expr_stmt|;
name|result
operator|=
operator|new
name|String
argument_list|(
name|message
operator|.
name|getPayload
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|deliveryComplete
parameter_list|(
name|IMqttDeliveryToken
name|token
parameter_list|)
block|{          }
block|}
block|}
end_class

end_unit

