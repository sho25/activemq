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
name|java
operator|.
name|io
operator|.
name|File
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
name|security
operator|.
name|ProtectionDomain
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|LinkedList
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
name|command
operator|.
name|ActiveMQMessage
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
name|ByteSequence
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
specifier|abstract
class|class
name|AbstractMQTTTest
block|{
specifier|protected
name|TransportConnector
name|mqttConnector
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|int
name|AT_MOST_ONCE
init|=
literal|0
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|int
name|AT_LEAST_ONCE
init|=
literal|1
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|int
name|EXACTLY_ONCE
init|=
literal|2
decl_stmt|;
specifier|public
name|File
name|basedir
parameter_list|()
throws|throws
name|IOException
block|{
name|ProtectionDomain
name|protectionDomain
init|=
name|getClass
argument_list|()
operator|.
name|getProtectionDomain
argument_list|()
decl_stmt|;
return|return
operator|new
name|File
argument_list|(
operator|new
name|File
argument_list|(
name|protectionDomain
operator|.
name|getCodeSource
argument_list|()
operator|.
name|getLocation
argument_list|()
operator|.
name|getPath
argument_list|()
argument_list|)
argument_list|,
literal|"../.."
argument_list|)
operator|.
name|getCanonicalFile
argument_list|()
return|;
block|}
specifier|protected
name|BrokerService
name|brokerService
decl_stmt|;
specifier|protected
name|LinkedList
argument_list|<
name|Throwable
argument_list|>
name|exceptions
init|=
operator|new
name|LinkedList
argument_list|<
name|Throwable
argument_list|>
argument_list|()
decl_stmt|;
specifier|protected
name|int
name|numberOfMessages
decl_stmt|;
annotation|@
name|Before
specifier|public
name|void
name|startBroker
parameter_list|()
throws|throws
name|Exception
block|{
name|exceptions
operator|.
name|clear
argument_list|()
expr_stmt|;
name|brokerService
operator|=
operator|new
name|BrokerService
argument_list|()
expr_stmt|;
name|brokerService
operator|.
name|setPersistent
argument_list|(
literal|false
argument_list|)
expr_stmt|;
name|brokerService
operator|.
name|setAdvisorySupport
argument_list|(
literal|false
argument_list|)
expr_stmt|;
name|brokerService
operator|.
name|setUseJmx
argument_list|(
literal|false
argument_list|)
expr_stmt|;
name|this
operator|.
name|numberOfMessages
operator|=
literal|3000
expr_stmt|;
block|}
annotation|@
name|After
specifier|public
name|void
name|stopBroker
parameter_list|()
throws|throws
name|Exception
block|{
if|if
condition|(
name|brokerService
operator|!=
literal|null
condition|)
block|{
name|brokerService
operator|.
name|stop
argument_list|()
expr_stmt|;
block|}
block|}
annotation|@
name|Test
argument_list|(
name|timeout
operator|=
literal|30000
argument_list|)
specifier|public
name|void
name|testSendAndReceiveMQTT
parameter_list|()
throws|throws
name|Exception
block|{
name|addMQTTConnector
argument_list|()
expr_stmt|;
name|brokerService
operator|.
name|start
argument_list|()
expr_stmt|;
specifier|final
name|MQTTClientProvider
name|subscriptionProvider
init|=
name|getMQTTClientProvider
argument_list|()
decl_stmt|;
name|initializeConnection
argument_list|(
name|subscriptionProvider
argument_list|)
expr_stmt|;
name|subscriptionProvider
operator|.
name|subscribe
argument_list|(
literal|"foo/bah"
argument_list|,
name|AT_MOST_ONCE
argument_list|)
expr_stmt|;
specifier|final
name|CountDownLatch
name|latch
init|=
operator|new
name|CountDownLatch
argument_list|(
name|numberOfMessages
argument_list|)
decl_stmt|;
name|Thread
name|thread
init|=
operator|new
name|Thread
argument_list|(
operator|new
name|Runnable
argument_list|()
block|{
specifier|public
name|void
name|run
parameter_list|()
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
name|numberOfMessages
condition|;
name|i
operator|++
control|)
block|{
try|try
block|{
name|byte
index|[]
name|payload
init|=
name|subscriptionProvider
operator|.
name|receive
argument_list|(
literal|10000
argument_list|)
decl_stmt|;
name|assertNotNull
argument_list|(
literal|"Should get a message"
argument_list|,
name|payload
argument_list|)
expr_stmt|;
name|latch
operator|.
name|countDown
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
name|e
operator|.
name|printStackTrace
argument_list|()
expr_stmt|;
break|break;
block|}
block|}
block|}
block|}
argument_list|)
decl_stmt|;
name|thread
operator|.
name|start
argument_list|()
expr_stmt|;
specifier|final
name|MQTTClientProvider
name|publishProvider
init|=
name|getMQTTClientProvider
argument_list|()
decl_stmt|;
name|initializeConnection
argument_list|(
name|publishProvider
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
name|numberOfMessages
condition|;
name|i
operator|++
control|)
block|{
name|String
name|payload
init|=
literal|"Message "
operator|+
name|i
decl_stmt|;
name|publishProvider
operator|.
name|publish
argument_list|(
literal|"foo/bah"
argument_list|,
name|payload
operator|.
name|getBytes
argument_list|()
argument_list|,
name|AT_LEAST_ONCE
argument_list|)
expr_stmt|;
block|}
name|latch
operator|.
name|await
argument_list|(
literal|10
argument_list|,
name|TimeUnit
operator|.
name|SECONDS
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|latch
operator|.
name|getCount
argument_list|()
argument_list|)
expr_stmt|;
name|subscriptionProvider
operator|.
name|disconnect
argument_list|()
expr_stmt|;
name|publishProvider
operator|.
name|disconnect
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
argument_list|(
name|timeout
operator|=
literal|30000
argument_list|)
specifier|public
name|void
name|testSendAtMostOnceReceiveExactlyOnce
parameter_list|()
throws|throws
name|Exception
block|{
comment|/**          * Although subscribing with EXACTLY ONCE, the message gets published          * with AT_MOST_ONCE - in MQTT the QoS is always determined by the message          * as published - not the wish of the subscriber          */
name|addMQTTConnector
argument_list|()
expr_stmt|;
name|brokerService
operator|.
name|start
argument_list|()
expr_stmt|;
specifier|final
name|MQTTClientProvider
name|provider
init|=
name|getMQTTClientProvider
argument_list|()
decl_stmt|;
name|initializeConnection
argument_list|(
name|provider
argument_list|)
expr_stmt|;
name|provider
operator|.
name|subscribe
argument_list|(
literal|"foo"
argument_list|,
name|EXACTLY_ONCE
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
name|numberOfMessages
condition|;
name|i
operator|++
control|)
block|{
name|String
name|payload
init|=
literal|"Test Message: "
operator|+
name|i
decl_stmt|;
name|provider
operator|.
name|publish
argument_list|(
literal|"foo"
argument_list|,
name|payload
operator|.
name|getBytes
argument_list|()
argument_list|,
name|AT_MOST_ONCE
argument_list|)
expr_stmt|;
name|byte
index|[]
name|message
init|=
name|provider
operator|.
name|receive
argument_list|(
literal|5000
argument_list|)
decl_stmt|;
name|assertNotNull
argument_list|(
literal|"Should get a message"
argument_list|,
name|message
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|payload
argument_list|,
operator|new
name|String
argument_list|(
name|message
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|provider
operator|.
name|disconnect
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
argument_list|(
name|timeout
operator|=
literal|30000
argument_list|)
specifier|public
name|void
name|testSendAtLeastOnceReceiveExactlyOnce
parameter_list|()
throws|throws
name|Exception
block|{
name|addMQTTConnector
argument_list|()
expr_stmt|;
name|brokerService
operator|.
name|start
argument_list|()
expr_stmt|;
specifier|final
name|MQTTClientProvider
name|provider
init|=
name|getMQTTClientProvider
argument_list|()
decl_stmt|;
name|initializeConnection
argument_list|(
name|provider
argument_list|)
expr_stmt|;
name|provider
operator|.
name|subscribe
argument_list|(
literal|"foo"
argument_list|,
name|EXACTLY_ONCE
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
name|numberOfMessages
condition|;
name|i
operator|++
control|)
block|{
name|String
name|payload
init|=
literal|"Test Message: "
operator|+
name|i
decl_stmt|;
name|provider
operator|.
name|publish
argument_list|(
literal|"foo"
argument_list|,
name|payload
operator|.
name|getBytes
argument_list|()
argument_list|,
name|AT_LEAST_ONCE
argument_list|)
expr_stmt|;
name|byte
index|[]
name|message
init|=
name|provider
operator|.
name|receive
argument_list|(
literal|5000
argument_list|)
decl_stmt|;
name|assertNotNull
argument_list|(
literal|"Should get a message"
argument_list|,
name|message
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|payload
argument_list|,
operator|new
name|String
argument_list|(
name|message
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|provider
operator|.
name|disconnect
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
argument_list|(
name|timeout
operator|=
literal|30000
argument_list|)
specifier|public
name|void
name|testSendAtLeastOnceReceiveAtMostOnce
parameter_list|()
throws|throws
name|Exception
block|{
name|addMQTTConnector
argument_list|()
expr_stmt|;
name|brokerService
operator|.
name|start
argument_list|()
expr_stmt|;
specifier|final
name|MQTTClientProvider
name|provider
init|=
name|getMQTTClientProvider
argument_list|()
decl_stmt|;
name|initializeConnection
argument_list|(
name|provider
argument_list|)
expr_stmt|;
name|provider
operator|.
name|subscribe
argument_list|(
literal|"foo"
argument_list|,
name|AT_MOST_ONCE
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
name|numberOfMessages
condition|;
name|i
operator|++
control|)
block|{
name|String
name|payload
init|=
literal|"Test Message: "
operator|+
name|i
decl_stmt|;
name|provider
operator|.
name|publish
argument_list|(
literal|"foo"
argument_list|,
name|payload
operator|.
name|getBytes
argument_list|()
argument_list|,
name|AT_LEAST_ONCE
argument_list|)
expr_stmt|;
name|byte
index|[]
name|message
init|=
name|provider
operator|.
name|receive
argument_list|(
literal|5000
argument_list|)
decl_stmt|;
name|assertNotNull
argument_list|(
literal|"Should get a message"
argument_list|,
name|message
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|payload
argument_list|,
operator|new
name|String
argument_list|(
name|message
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|provider
operator|.
name|disconnect
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
argument_list|(
name|timeout
operator|=
literal|30000
argument_list|)
specifier|public
name|void
name|testSendAndReceiveAtMostOnce
parameter_list|()
throws|throws
name|Exception
block|{
name|addMQTTConnector
argument_list|()
expr_stmt|;
name|brokerService
operator|.
name|start
argument_list|()
expr_stmt|;
specifier|final
name|MQTTClientProvider
name|provider
init|=
name|getMQTTClientProvider
argument_list|()
decl_stmt|;
name|initializeConnection
argument_list|(
name|provider
argument_list|)
expr_stmt|;
name|provider
operator|.
name|subscribe
argument_list|(
literal|"foo"
argument_list|,
name|AT_MOST_ONCE
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
name|numberOfMessages
condition|;
name|i
operator|++
control|)
block|{
name|String
name|payload
init|=
literal|"Test Message: "
operator|+
name|i
decl_stmt|;
name|provider
operator|.
name|publish
argument_list|(
literal|"foo"
argument_list|,
name|payload
operator|.
name|getBytes
argument_list|()
argument_list|,
name|AT_MOST_ONCE
argument_list|)
expr_stmt|;
name|byte
index|[]
name|message
init|=
name|provider
operator|.
name|receive
argument_list|(
literal|5000
argument_list|)
decl_stmt|;
name|assertNotNull
argument_list|(
literal|"Should get a message"
argument_list|,
name|message
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|payload
argument_list|,
operator|new
name|String
argument_list|(
name|message
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|provider
operator|.
name|disconnect
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
argument_list|(
name|timeout
operator|=
literal|30000
argument_list|)
specifier|public
name|void
name|testSendAndReceiveAtLeastOnce
parameter_list|()
throws|throws
name|Exception
block|{
name|addMQTTConnector
argument_list|()
expr_stmt|;
name|brokerService
operator|.
name|start
argument_list|()
expr_stmt|;
specifier|final
name|MQTTClientProvider
name|provider
init|=
name|getMQTTClientProvider
argument_list|()
decl_stmt|;
name|initializeConnection
argument_list|(
name|provider
argument_list|)
expr_stmt|;
name|provider
operator|.
name|subscribe
argument_list|(
literal|"foo"
argument_list|,
name|AT_LEAST_ONCE
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
name|numberOfMessages
condition|;
name|i
operator|++
control|)
block|{
name|String
name|payload
init|=
literal|"Test Message: "
operator|+
name|i
decl_stmt|;
name|provider
operator|.
name|publish
argument_list|(
literal|"foo"
argument_list|,
name|payload
operator|.
name|getBytes
argument_list|()
argument_list|,
name|AT_LEAST_ONCE
argument_list|)
expr_stmt|;
name|byte
index|[]
name|message
init|=
name|provider
operator|.
name|receive
argument_list|(
literal|5000
argument_list|)
decl_stmt|;
name|assertNotNull
argument_list|(
literal|"Should get a message"
argument_list|,
name|message
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|payload
argument_list|,
operator|new
name|String
argument_list|(
name|message
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|provider
operator|.
name|disconnect
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
argument_list|(
name|timeout
operator|=
literal|30000
argument_list|)
specifier|public
name|void
name|testSendAndReceiveExactlyOnce
parameter_list|()
throws|throws
name|Exception
block|{
name|addMQTTConnector
argument_list|()
expr_stmt|;
name|brokerService
operator|.
name|start
argument_list|()
expr_stmt|;
specifier|final
name|MQTTClientProvider
name|publisher
init|=
name|getMQTTClientProvider
argument_list|()
decl_stmt|;
name|initializeConnection
argument_list|(
name|publisher
argument_list|)
expr_stmt|;
specifier|final
name|MQTTClientProvider
name|subscriber
init|=
name|getMQTTClientProvider
argument_list|()
decl_stmt|;
name|initializeConnection
argument_list|(
name|subscriber
argument_list|)
expr_stmt|;
name|subscriber
operator|.
name|subscribe
argument_list|(
literal|"foo"
argument_list|,
name|EXACTLY_ONCE
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
name|numberOfMessages
condition|;
name|i
operator|++
control|)
block|{
name|String
name|payload
init|=
literal|"Test Message: "
operator|+
name|i
decl_stmt|;
name|publisher
operator|.
name|publish
argument_list|(
literal|"foo"
argument_list|,
name|payload
operator|.
name|getBytes
argument_list|()
argument_list|,
name|EXACTLY_ONCE
argument_list|)
expr_stmt|;
name|byte
index|[]
name|message
init|=
name|subscriber
operator|.
name|receive
argument_list|(
literal|5000
argument_list|)
decl_stmt|;
name|assertNotNull
argument_list|(
literal|"Should get a message + ["
operator|+
name|i
operator|+
literal|"]"
argument_list|,
name|message
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|payload
argument_list|,
operator|new
name|String
argument_list|(
name|message
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|subscriber
operator|.
name|disconnect
argument_list|()
expr_stmt|;
name|publisher
operator|.
name|disconnect
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
argument_list|(
name|timeout
operator|=
literal|30000
argument_list|)
specifier|public
name|void
name|testSendAndReceiveLargeMessages
parameter_list|()
throws|throws
name|Exception
block|{
name|byte
index|[]
name|payload
init|=
operator|new
name|byte
index|[
literal|1024
operator|*
literal|32
index|]
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
name|payload
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|payload
index|[
name|i
index|]
operator|=
literal|'2'
expr_stmt|;
block|}
name|addMQTTConnector
argument_list|()
expr_stmt|;
name|brokerService
operator|.
name|start
argument_list|()
expr_stmt|;
specifier|final
name|MQTTClientProvider
name|publisher
init|=
name|getMQTTClientProvider
argument_list|()
decl_stmt|;
name|initializeConnection
argument_list|(
name|publisher
argument_list|)
expr_stmt|;
specifier|final
name|MQTTClientProvider
name|subscriber
init|=
name|getMQTTClientProvider
argument_list|()
decl_stmt|;
name|initializeConnection
argument_list|(
name|subscriber
argument_list|)
expr_stmt|;
name|subscriber
operator|.
name|subscribe
argument_list|(
literal|"foo"
argument_list|,
name|AT_LEAST_ONCE
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
literal|10
condition|;
name|i
operator|++
control|)
block|{
name|publisher
operator|.
name|publish
argument_list|(
literal|"foo"
argument_list|,
name|payload
argument_list|,
name|AT_LEAST_ONCE
argument_list|)
expr_stmt|;
name|byte
index|[]
name|message
init|=
name|subscriber
operator|.
name|receive
argument_list|(
literal|5000
argument_list|)
decl_stmt|;
name|assertNotNull
argument_list|(
literal|"Should get a message"
argument_list|,
name|message
argument_list|)
expr_stmt|;
name|assertArrayEquals
argument_list|(
name|payload
argument_list|,
name|message
argument_list|)
expr_stmt|;
block|}
name|subscriber
operator|.
name|disconnect
argument_list|()
expr_stmt|;
name|publisher
operator|.
name|disconnect
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
argument_list|(
name|timeout
operator|=
literal|30000
argument_list|)
specifier|public
name|void
name|testSendMQTTReceiveJMS
parameter_list|()
throws|throws
name|Exception
block|{
name|addMQTTConnector
argument_list|()
expr_stmt|;
name|TransportConnector
name|openwireTransport
init|=
name|brokerService
operator|.
name|addConnector
argument_list|(
literal|"tcp://localhost:0"
argument_list|)
decl_stmt|;
name|brokerService
operator|.
name|start
argument_list|()
expr_stmt|;
specifier|final
name|MQTTClientProvider
name|provider
init|=
name|getMQTTClientProvider
argument_list|()
decl_stmt|;
name|initializeConnection
argument_list|(
name|provider
argument_list|)
expr_stmt|;
specifier|final
name|String
name|DESTINATION_NAME
init|=
literal|"foo.*"
decl_stmt|;
name|ActiveMQConnection
name|activeMQConnection
init|=
operator|(
name|ActiveMQConnection
operator|)
operator|new
name|ActiveMQConnectionFactory
argument_list|(
name|openwireTransport
operator|.
name|getConnectUri
argument_list|()
argument_list|)
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
name|javax
operator|.
name|jms
operator|.
name|Topic
name|jmsTopic
init|=
name|s
operator|.
name|createTopic
argument_list|(
name|DESTINATION_NAME
argument_list|)
decl_stmt|;
name|MessageConsumer
name|consumer
init|=
name|s
operator|.
name|createConsumer
argument_list|(
name|jmsTopic
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
name|numberOfMessages
condition|;
name|i
operator|++
control|)
block|{
name|String
name|payload
init|=
literal|"Test Message: "
operator|+
name|i
decl_stmt|;
name|provider
operator|.
name|publish
argument_list|(
literal|"foo/bah"
argument_list|,
name|payload
operator|.
name|getBytes
argument_list|()
argument_list|,
name|AT_LEAST_ONCE
argument_list|)
expr_stmt|;
name|ActiveMQMessage
name|message
init|=
operator|(
name|ActiveMQMessage
operator|)
name|consumer
operator|.
name|receive
argument_list|(
literal|5000
argument_list|)
decl_stmt|;
name|assertNotNull
argument_list|(
literal|"Should get a message"
argument_list|,
name|message
argument_list|)
expr_stmt|;
name|ByteSequence
name|bs
init|=
name|message
operator|.
name|getContent
argument_list|()
decl_stmt|;
name|assertEquals
argument_list|(
name|payload
argument_list|,
operator|new
name|String
argument_list|(
name|bs
operator|.
name|data
argument_list|,
name|bs
operator|.
name|offset
argument_list|,
name|bs
operator|.
name|length
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|activeMQConnection
operator|.
name|close
argument_list|()
expr_stmt|;
name|provider
operator|.
name|disconnect
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
argument_list|(
name|timeout
operator|=
literal|30000
argument_list|)
specifier|public
name|void
name|testSendJMSReceiveMQTT
parameter_list|()
throws|throws
name|Exception
block|{
name|addMQTTConnector
argument_list|()
expr_stmt|;
name|TransportConnector
name|openwireTransport
init|=
name|brokerService
operator|.
name|addConnector
argument_list|(
literal|"tcp://localhost:0"
argument_list|)
decl_stmt|;
name|brokerService
operator|.
name|start
argument_list|()
expr_stmt|;
specifier|final
name|MQTTClientProvider
name|provider
init|=
name|getMQTTClientProvider
argument_list|()
decl_stmt|;
name|initializeConnection
argument_list|(
name|provider
argument_list|)
expr_stmt|;
name|ActiveMQConnection
name|activeMQConnection
init|=
operator|(
name|ActiveMQConnection
operator|)
operator|new
name|ActiveMQConnectionFactory
argument_list|(
name|openwireTransport
operator|.
name|getConnectUri
argument_list|()
argument_list|)
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
name|javax
operator|.
name|jms
operator|.
name|Topic
name|jmsTopic
init|=
name|s
operator|.
name|createTopic
argument_list|(
literal|"foo.far"
argument_list|)
decl_stmt|;
name|MessageProducer
name|producer
init|=
name|s
operator|.
name|createProducer
argument_list|(
name|jmsTopic
argument_list|)
decl_stmt|;
name|provider
operator|.
name|subscribe
argument_list|(
literal|"foo/+"
argument_list|,
name|AT_MOST_ONCE
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
name|numberOfMessages
condition|;
name|i
operator|++
control|)
block|{
name|String
name|payload
init|=
literal|"This is Test Message: "
operator|+
name|i
decl_stmt|;
name|TextMessage
name|sendMessage
init|=
name|s
operator|.
name|createTextMessage
argument_list|(
name|payload
argument_list|)
decl_stmt|;
name|producer
operator|.
name|send
argument_list|(
name|sendMessage
argument_list|)
expr_stmt|;
name|byte
index|[]
name|message
init|=
name|provider
operator|.
name|receive
argument_list|(
literal|5000
argument_list|)
decl_stmt|;
name|assertNotNull
argument_list|(
literal|"Should get a message"
argument_list|,
name|message
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|payload
argument_list|,
operator|new
name|String
argument_list|(
name|message
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|provider
operator|.
name|disconnect
argument_list|()
expr_stmt|;
name|activeMQConnection
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
specifier|protected
name|String
name|getProtocolScheme
parameter_list|()
block|{
return|return
literal|"mqtt"
return|;
block|}
specifier|protected
name|void
name|addMQTTConnector
parameter_list|()
throws|throws
name|Exception
block|{
name|addMQTTConnector
argument_list|(
literal|""
argument_list|)
expr_stmt|;
block|}
specifier|protected
name|void
name|addMQTTConnector
parameter_list|(
name|String
name|config
parameter_list|)
throws|throws
name|Exception
block|{
name|mqttConnector
operator|=
name|brokerService
operator|.
name|addConnector
argument_list|(
name|getProtocolScheme
argument_list|()
operator|+
literal|"://localhost:0"
operator|+
name|config
argument_list|)
expr_stmt|;
block|}
specifier|protected
name|void
name|initializeConnection
parameter_list|(
name|MQTTClientProvider
name|provider
parameter_list|)
throws|throws
name|Exception
block|{
name|provider
operator|.
name|connect
argument_list|(
literal|"tcp://localhost:"
operator|+
name|mqttConnector
operator|.
name|getConnectUri
argument_list|()
operator|.
name|getPort
argument_list|()
argument_list|)
expr_stmt|;
block|}
specifier|protected
specifier|abstract
name|MQTTClientProvider
name|getMQTTClientProvider
parameter_list|()
function_decl|;
block|}
end_class

end_unit

