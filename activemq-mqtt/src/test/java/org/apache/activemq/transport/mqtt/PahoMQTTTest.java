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
import|import static
name|org
operator|.
name|junit
operator|.
name|Assert
operator|.
name|assertNull
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
name|eclipse
operator|.
name|paho
operator|.
name|client
operator|.
name|mqttv3
operator|.
name|MqttClient
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
block|}
end_class

end_unit

