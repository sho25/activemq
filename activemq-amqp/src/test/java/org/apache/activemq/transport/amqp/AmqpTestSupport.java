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
name|amqp
package|;
end_package

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
name|AutoFailTestSupport
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
name|SslContext
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
name|spring
operator|.
name|SpringSslContext
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
name|util
operator|.
name|Vector
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|fusesource
operator|.
name|hawtbuf
operator|.
name|UTF8Buffer
operator|.
name|utf8
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
name|assertArrayEquals
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
name|assertEquals
import|;
end_import

begin_class
specifier|public
class|class
name|AmqpTestSupport
block|{
specifier|protected
specifier|static
specifier|final
name|Logger
name|LOG
init|=
name|LoggerFactory
operator|.
name|getLogger
argument_list|(
name|AmqpTestSupport
operator|.
name|class
argument_list|)
decl_stmt|;
specifier|protected
name|BrokerService
name|brokerService
decl_stmt|;
specifier|protected
name|Vector
argument_list|<
name|Throwable
argument_list|>
name|exceptions
init|=
operator|new
name|Vector
argument_list|<
name|Throwable
argument_list|>
argument_list|()
decl_stmt|;
specifier|protected
name|int
name|numberOfMessages
decl_stmt|;
name|AutoFailTestSupport
name|autoFailTestSupport
init|=
operator|new
name|AutoFailTestSupport
argument_list|()
block|{}
decl_stmt|;
specifier|protected
name|int
name|port
decl_stmt|;
specifier|protected
name|int
name|sslPort
decl_stmt|;
specifier|public
specifier|static
name|void
name|main
parameter_list|(
name|String
index|[]
name|args
parameter_list|)
throws|throws
name|Exception
block|{
specifier|final
name|AmqpTestSupport
name|s
init|=
operator|new
name|AmqpTestSupport
argument_list|()
decl_stmt|;
name|s
operator|.
name|sslPort
operator|=
literal|5671
expr_stmt|;
name|s
operator|.
name|port
operator|=
literal|5672
expr_stmt|;
name|s
operator|.
name|startBroker
argument_list|()
expr_stmt|;
while|while
condition|(
literal|true
condition|)
block|{
name|Thread
operator|.
name|sleep
argument_list|(
literal|100000
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Before
specifier|public
name|void
name|setUp
parameter_list|()
throws|throws
name|Exception
block|{
name|autoFailTestSupport
operator|.
name|startAutoFailThread
argument_list|()
expr_stmt|;
name|exceptions
operator|.
name|clear
argument_list|()
expr_stmt|;
name|startBroker
argument_list|()
expr_stmt|;
block|}
specifier|public
name|void
name|startBroker
parameter_list|()
throws|throws
name|Exception
block|{
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
comment|// Setup SSL context...
specifier|final
name|File
name|classesDir
init|=
operator|new
name|File
argument_list|(
name|AmqpProtocolConverter
operator|.
name|class
operator|.
name|getProtectionDomain
argument_list|()
operator|.
name|getCodeSource
argument_list|()
operator|.
name|getLocation
argument_list|()
operator|.
name|getFile
argument_list|()
argument_list|)
decl_stmt|;
name|File
name|keystore
init|=
operator|new
name|File
argument_list|(
name|classesDir
argument_list|,
literal|"../../src/test/resources/keystore"
argument_list|)
decl_stmt|;
specifier|final
name|SpringSslContext
name|sslContext
init|=
operator|new
name|SpringSslContext
argument_list|()
decl_stmt|;
name|sslContext
operator|.
name|setKeyStore
argument_list|(
name|keystore
operator|.
name|getCanonicalPath
argument_list|()
argument_list|)
expr_stmt|;
name|sslContext
operator|.
name|setKeyStorePassword
argument_list|(
literal|"password"
argument_list|)
expr_stmt|;
name|sslContext
operator|.
name|setTrustStore
argument_list|(
name|keystore
operator|.
name|getCanonicalPath
argument_list|()
argument_list|)
expr_stmt|;
name|sslContext
operator|.
name|setTrustStorePassword
argument_list|(
literal|"password"
argument_list|)
expr_stmt|;
name|sslContext
operator|.
name|afterPropertiesSet
argument_list|()
expr_stmt|;
name|brokerService
operator|.
name|setSslContext
argument_list|(
name|sslContext
argument_list|)
expr_stmt|;
name|addAMQPConnector
argument_list|()
expr_stmt|;
name|brokerService
operator|.
name|start
argument_list|()
expr_stmt|;
name|this
operator|.
name|numberOfMessages
operator|=
literal|2000
expr_stmt|;
block|}
specifier|protected
name|void
name|addAMQPConnector
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
literal|"amqp+ssl://0.0.0.0:"
operator|+
name|sslPort
argument_list|)
decl_stmt|;
name|sslPort
operator|=
name|connector
operator|.
name|getConnectUri
argument_list|()
operator|.
name|getPort
argument_list|()
expr_stmt|;
name|connector
operator|=
name|brokerService
operator|.
name|addConnector
argument_list|(
literal|"amqp://0.0.0.0:"
operator|+
name|port
argument_list|)
expr_stmt|;
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
name|brokerService
operator|=
literal|null
expr_stmt|;
block|}
name|autoFailTestSupport
operator|.
name|stopAutoFailThread
argument_list|()
expr_stmt|;
block|}
comment|//    @Test
comment|//    public void testSendAndReceiveAMQP() throws Exception {
comment|//        addAMQPConnector(brokerService);
comment|//        brokerService.start();
comment|//        AMQP amqp = new AMQP();
comment|//        final BlockingConnection subscribeConnection = amqp.blockingConnection();
comment|//        subscribeConnection.connect();
comment|//        Topic topic = new Topic("foo/bah",QoS.AT_MOST_ONCE);
comment|//        Topic[] topics = {topic};
comment|//        subscribeConnection.subscribe(topics);
comment|//        final CountDownLatch latch = new CountDownLatch(numberOfMessages);
comment|//
comment|//        Thread thread = new Thread(new Runnable() {
comment|//            public void run() {
comment|//                for (int i = 0; i< numberOfMessages; i++){
comment|//                    try {
comment|//                        Message message = subscribeConnection.receive();
comment|//                        message.ack();
comment|//                        latch.countDown();
comment|//                    } catch (Exception e) {
comment|//                        e.printStackTrace();
comment|//                        break;
comment|//                    }
comment|//
comment|//                }
comment|//            }
comment|//        });
comment|//        thread.start();
comment|//
comment|//        BlockingConnection publisherConnection = amqp.blockingConnection();
comment|//        publisherConnection.connect();
comment|//        for (int i = 0; i< numberOfMessages; i++){
comment|//            String payload = "Message " + i;
comment|//            publisherConnection.publish(topic.name().toString(),payload.getBytes(),QoS.AT_LEAST_ONCE,false);
comment|//        }
comment|//
comment|//        latch.await(10, TimeUnit.SECONDS);
comment|//        assertEquals(0, latch.getCount());
comment|//    }
comment|//
comment|//    @Test
comment|//    public void testSendAndReceiveAtMostOnce() throws Exception {
comment|//        addAMQPConnector(brokerService);
comment|//        brokerService.start();
comment|//        AMQP amqp = createAMQPConnection();
comment|//        amqp.setKeepAlive(Short.MAX_VALUE);
comment|//        BlockingConnection connection = amqp.blockingConnection();
comment|//
comment|//        connection.connect();
comment|//
comment|//
comment|//        Topic[] topics = {new Topic(utf8("foo"), QoS.AT_MOST_ONCE)};
comment|//        connection.subscribe(topics);
comment|//        for (int i = 0; i< numberOfMessages; i++) {
comment|//            String payload = "Test Message: " + i;
comment|//            connection.publish("foo", payload.getBytes(), QoS.AT_MOST_ONCE, false);
comment|//            Message message = connection.receive();
comment|//            assertEquals(payload, new String(message.getPayload()));
comment|//        }
comment|//        connection.disconnect();
comment|//    }
comment|//
comment|//    @Test
comment|//    public void testSendAndReceiveAtLeastOnce() throws Exception {
comment|//        addAMQPConnector(brokerService);
comment|//        brokerService.start();
comment|//        AMQP amqp = createAMQPConnection();
comment|//        amqp.setKeepAlive(Short.MAX_VALUE);
comment|//        BlockingConnection connection = amqp.blockingConnection();
comment|//
comment|//        connection.connect();
comment|//
comment|//        Topic[] topics = {new Topic(utf8("foo"), QoS.AT_LEAST_ONCE)};
comment|//        connection.subscribe(topics);
comment|//        for (int i = 0; i< numberOfMessages; i++) {
comment|//            String payload = "Test Message: " + i;
comment|//            connection.publish("foo", payload.getBytes(), QoS.AT_LEAST_ONCE, false);
comment|//            Message message = connection.receive();
comment|//            message.ack();
comment|//            assertEquals(payload, new String(message.getPayload()));
comment|//        }
comment|//        connection.disconnect();
comment|//    }
comment|//
comment|//    @Test
comment|//    public void testSendAndReceiveExactlyOnce() throws Exception {
comment|//        addAMQPConnector(brokerService);
comment|//        brokerService.start();
comment|//        AMQP publisher = createAMQPConnection();
comment|//        BlockingConnection pubConnection = publisher.blockingConnection();
comment|//
comment|//        pubConnection.connect();
comment|//
comment|//        AMQP subscriber = createAMQPConnection();
comment|//        BlockingConnection subConnection = subscriber.blockingConnection();
comment|//
comment|//        subConnection.connect();
comment|//
comment|//        Topic[] topics = {new Topic(utf8("foo"), QoS.EXACTLY_ONCE)};
comment|//        subConnection.subscribe(topics);
comment|//        for (int i = 0; i< numberOfMessages; i++) {
comment|//            String payload = "Test Message: " + i;
comment|//            pubConnection.publish("foo", payload.getBytes(), QoS.EXACTLY_ONCE, false);
comment|//            Message message = subConnection.receive();
comment|//            message.ack();
comment|//            assertEquals(payload, new String(message.getPayload()));
comment|//        }
comment|//        subConnection.disconnect();
comment|//        pubConnection.disconnect();
comment|//    }
comment|//
comment|//    @Test
comment|//    public void testSendAndReceiveLargeMessages() throws Exception {
comment|//        byte[] payload = new byte[1024 * 32];
comment|//        for (int i = 0; i< payload.length; i++){
comment|//            payload[i] = '2';
comment|//        }
comment|//        addAMQPConnector(brokerService);
comment|//        brokerService.start();
comment|//
comment|//        AMQP publisher = createAMQPConnection();
comment|//        BlockingConnection pubConnection = publisher.blockingConnection();
comment|//
comment|//        pubConnection.connect();
comment|//
comment|//        AMQP subscriber = createAMQPConnection();
comment|//        BlockingConnection subConnection = subscriber.blockingConnection();
comment|//
comment|//        subConnection.connect();
comment|//
comment|//        Topic[] topics = {new Topic(utf8("foo"), QoS.AT_LEAST_ONCE)};
comment|//        subConnection.subscribe(topics);
comment|//        for (int i = 0; i< 10; i++) {
comment|//            pubConnection.publish("foo", payload, QoS.AT_LEAST_ONCE, false);
comment|//            Message message = subConnection.receive();
comment|//            message.ack();
comment|//            assertArrayEquals(payload, message.getPayload());
comment|//        }
comment|//        subConnection.disconnect();
comment|//        pubConnection.disconnect();
comment|//    }
comment|//
comment|//
comment|//    @Test
comment|//    public void testSendAMQPReceiveJMS() throws Exception {
comment|//        addAMQPConnector(brokerService);
comment|//        brokerService.addConnector(ActiveMQConnectionFactory.DEFAULT_BROKER_BIND_URL);
comment|//        brokerService.start();
comment|//        AMQP amqp = createAMQPConnection();
comment|//        BlockingConnection connection = amqp.blockingConnection();
comment|//        final String DESTINATION_NAME = "foo.*";
comment|//        connection.connect();
comment|//
comment|//        ActiveMQConnection activeMQConnection = (ActiveMQConnection) new ActiveMQConnectionFactory().createConnection();
comment|//        activeMQConnection.start();
comment|//        Session s = activeMQConnection.createSession(false, Session.AUTO_ACKNOWLEDGE);
comment|//        javax.jms.Topic jmsTopic = s.createTopic(DESTINATION_NAME);
comment|//        MessageConsumer consumer = s.createConsumer(jmsTopic);
comment|//
comment|//        for (int i = 0; i< numberOfMessages; i++) {
comment|//            String payload = "Test Message: " + i;
comment|//            connection.publish("foo/bah", payload.getBytes(), QoS.AT_LEAST_ONCE, false);
comment|//            ActiveMQMessage message = (ActiveMQMessage) consumer.receive();
comment|//            ByteSequence bs = message.getContent();
comment|//            assertEquals(payload, new String(bs.data, bs.offset, bs.length));
comment|//        }
comment|//
comment|//
comment|//        activeMQConnection.close();
comment|//        connection.disconnect();
comment|//    }
comment|//
comment|//    @Test
comment|//    public void testSendJMSReceiveAMQP() throws Exception {
comment|//        addAMQPConnector(brokerService);
comment|//        brokerService.addConnector(ActiveMQConnectionFactory.DEFAULT_BROKER_BIND_URL);
comment|//        brokerService.start();
comment|//        AMQP amqp = createAMQPConnection();
comment|//        amqp.setKeepAlive(Short.MAX_VALUE);
comment|//        BlockingConnection connection = amqp.blockingConnection();
comment|//        connection.connect();
comment|//
comment|//        ActiveMQConnection activeMQConnection = (ActiveMQConnection) new ActiveMQConnectionFactory().createConnection();
comment|//        activeMQConnection.start();
comment|//        Session s = activeMQConnection.createSession(false, Session.AUTO_ACKNOWLEDGE);
comment|//        javax.jms.Topic jmsTopic = s.createTopic("foo.far");
comment|//        MessageProducer producer = s.createProducer(jmsTopic);
comment|//
comment|//        Topic[] topics = {new Topic(utf8("foo/+"), QoS.AT_MOST_ONCE)};
comment|//        connection.subscribe(topics);
comment|//        for (int i = 0; i< numberOfMessages; i++) {
comment|//            String payload = "This is Test Message: " + i;
comment|//            TextMessage sendMessage = s.createTextMessage(payload);
comment|//            producer.send(sendMessage);
comment|//            Message message = connection.receive();
comment|//            message.ack();
comment|//            assertEquals(payload, new String(message.getPayload()));
comment|//        }
comment|//        connection.disconnect();
comment|//    }
comment|//
comment|//
block|}
end_class

end_unit

