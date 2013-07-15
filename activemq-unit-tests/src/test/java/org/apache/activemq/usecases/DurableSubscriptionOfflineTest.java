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
name|util
operator|.
name|ArrayList
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|List
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
name|ExecutorService
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
name|Executors
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
name|JMSException
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
name|broker
operator|.
name|BrokerFactory
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
name|apache
operator|.
name|activemq
operator|.
name|command
operator|.
name|ActiveMQTopic
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
name|MessageId
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
name|jdbc
operator|.
name|JDBCPersistenceAdapter
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
name|store
operator|.
name|kahadb
operator|.
name|disk
operator|.
name|journal
operator|.
name|Journal
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
name|DurableSubscriptionOfflineTest
extends|extends
name|org
operator|.
name|apache
operator|.
name|activemq
operator|.
name|TestSupport
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
name|DurableSubscriptionOfflineTest
operator|.
name|class
argument_list|)
decl_stmt|;
specifier|public
name|boolean
name|usePrioritySupport
init|=
name|Boolean
operator|.
name|TRUE
decl_stmt|;
specifier|public
name|int
name|journalMaxFileLength
init|=
name|Journal
operator|.
name|DEFAULT_MAX_FILE_LENGTH
decl_stmt|;
specifier|public
name|boolean
name|keepDurableSubsActive
init|=
literal|true
decl_stmt|;
specifier|private
name|BrokerService
name|broker
decl_stmt|;
specifier|private
name|ActiveMQTopic
name|topic
decl_stmt|;
specifier|private
specifier|final
name|List
argument_list|<
name|Throwable
argument_list|>
name|exceptions
init|=
operator|new
name|ArrayList
argument_list|<
name|Throwable
argument_list|>
argument_list|()
decl_stmt|;
annotation|@
name|Override
specifier|protected
name|ActiveMQConnectionFactory
name|createConnectionFactory
parameter_list|()
throws|throws
name|Exception
block|{
name|ActiveMQConnectionFactory
name|connectionFactory
init|=
operator|new
name|ActiveMQConnectionFactory
argument_list|(
literal|"vm://"
operator|+
name|getName
argument_list|(
literal|true
argument_list|)
argument_list|)
decl_stmt|;
name|connectionFactory
operator|.
name|setWatchTopicAdvisories
argument_list|(
literal|false
argument_list|)
expr_stmt|;
return|return
name|connectionFactory
return|;
block|}
annotation|@
name|Override
specifier|protected
name|Connection
name|createConnection
parameter_list|()
throws|throws
name|Exception
block|{
return|return
name|createConnection
argument_list|(
literal|"cliName"
argument_list|)
return|;
block|}
specifier|protected
name|Connection
name|createConnection
parameter_list|(
name|String
name|name
parameter_list|)
throws|throws
name|Exception
block|{
name|Connection
name|con
init|=
name|super
operator|.
name|createConnection
argument_list|()
decl_stmt|;
name|con
operator|.
name|setClientID
argument_list|(
name|name
argument_list|)
expr_stmt|;
name|con
operator|.
name|start
argument_list|()
expr_stmt|;
return|return
name|con
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
name|DurableSubscriptionOfflineTest
operator|.
name|class
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|protected
name|void
name|setUp
parameter_list|()
throws|throws
name|Exception
block|{
name|setAutoFail
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|setMaxTestTime
argument_list|(
literal|2
operator|*
literal|60
operator|*
literal|1000
argument_list|)
expr_stmt|;
name|exceptions
operator|.
name|clear
argument_list|()
expr_stmt|;
name|topic
operator|=
operator|(
name|ActiveMQTopic
operator|)
name|createDestination
argument_list|()
expr_stmt|;
name|createBroker
argument_list|()
expr_stmt|;
name|super
operator|.
name|setUp
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Override
specifier|protected
name|void
name|tearDown
parameter_list|()
throws|throws
name|Exception
block|{
name|super
operator|.
name|tearDown
argument_list|()
expr_stmt|;
name|destroyBroker
argument_list|()
expr_stmt|;
block|}
specifier|private
name|void
name|createBroker
parameter_list|()
throws|throws
name|Exception
block|{
name|createBroker
argument_list|(
literal|true
argument_list|)
expr_stmt|;
block|}
specifier|private
name|void
name|createBroker
parameter_list|(
name|boolean
name|deleteAllMessages
parameter_list|)
throws|throws
name|Exception
block|{
name|broker
operator|=
name|BrokerFactory
operator|.
name|createBroker
argument_list|(
literal|"broker:(vm://"
operator|+
name|getName
argument_list|(
literal|true
argument_list|)
operator|+
literal|")"
argument_list|)
expr_stmt|;
name|broker
operator|.
name|setBrokerName
argument_list|(
name|getName
argument_list|(
literal|true
argument_list|)
argument_list|)
expr_stmt|;
name|broker
operator|.
name|setDeleteAllMessagesOnStartup
argument_list|(
name|deleteAllMessages
argument_list|)
expr_stmt|;
name|broker
operator|.
name|getManagementContext
argument_list|()
operator|.
name|setCreateConnector
argument_list|(
literal|false
argument_list|)
expr_stmt|;
name|broker
operator|.
name|setAdvisorySupport
argument_list|(
literal|false
argument_list|)
expr_stmt|;
name|broker
operator|.
name|setKeepDurableSubsActive
argument_list|(
name|keepDurableSubsActive
argument_list|)
expr_stmt|;
name|broker
operator|.
name|addConnector
argument_list|(
literal|"tcp://0.0.0.0:0"
argument_list|)
expr_stmt|;
if|if
condition|(
name|usePrioritySupport
condition|)
block|{
name|PolicyEntry
name|policy
init|=
operator|new
name|PolicyEntry
argument_list|()
decl_stmt|;
name|policy
operator|.
name|setPrioritizedMessages
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|PolicyMap
name|policyMap
init|=
operator|new
name|PolicyMap
argument_list|()
decl_stmt|;
name|policyMap
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
name|policyMap
argument_list|)
expr_stmt|;
block|}
name|setDefaultPersistenceAdapter
argument_list|(
name|broker
argument_list|)
expr_stmt|;
if|if
condition|(
name|broker
operator|.
name|getPersistenceAdapter
argument_list|()
operator|instanceof
name|JDBCPersistenceAdapter
condition|)
block|{
comment|// ensure it kicks in during tests
operator|(
operator|(
name|JDBCPersistenceAdapter
operator|)
name|broker
operator|.
name|getPersistenceAdapter
argument_list|()
operator|)
operator|.
name|setCleanupPeriod
argument_list|(
literal|2
operator|*
literal|1000
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|broker
operator|.
name|getPersistenceAdapter
argument_list|()
operator|instanceof
name|KahaDBPersistenceAdapter
condition|)
block|{
comment|// have lots of journal files
operator|(
operator|(
name|KahaDBPersistenceAdapter
operator|)
name|broker
operator|.
name|getPersistenceAdapter
argument_list|()
operator|)
operator|.
name|setJournalMaxFileLength
argument_list|(
name|journalMaxFileLength
argument_list|)
expr_stmt|;
block|}
name|broker
operator|.
name|start
argument_list|()
expr_stmt|;
block|}
specifier|private
name|void
name|destroyBroker
parameter_list|()
throws|throws
name|Exception
block|{
if|if
condition|(
name|broker
operator|!=
literal|null
condition|)
name|broker
operator|.
name|stop
argument_list|()
expr_stmt|;
block|}
comment|//    public void initCombosForTestConsumeOnlyMatchedMessages() throws Exception {
comment|//        this.addCombinationValues("defaultPersistenceAdapter",
comment|//                new Object[]{ PersistenceAdapterChoice.KahaDB, PersistenceAdapterChoice.LevelDB, PersistenceAdapterChoice.JDBC});
comment|//        this.addCombinationValues("usePrioritySupport",
comment|//                new Object[]{ Boolean.TRUE, Boolean.FALSE});
comment|//    }
comment|//
comment|//    public void testConsumeOnlyMatchedMessages() throws Exception {
comment|//        // create durable subscription
comment|//        Connection con = createConnection();
comment|//        Session session = con.createSession(false, Session.AUTO_ACKNOWLEDGE);
comment|//        session.createDurableSubscriber(topic, "SubsId", "filter = 'true'", true);
comment|//        session.close();
comment|//        con.close();
comment|//
comment|//        // send messages
comment|//        con = createConnection();
comment|//        session = con.createSession(false, Session.AUTO_ACKNOWLEDGE);
comment|//        MessageProducer producer = session.createProducer(null);
comment|//
comment|//        int sent = 0;
comment|//        for (int i = 0; i< 10; i++) {
comment|//            boolean filter = i % 2 == 1;
comment|//            if (filter)
comment|//                sent++;
comment|//
comment|//            Message message = session.createMessage();
comment|//            message.setStringProperty("filter", filter ? "true" : "false");
comment|//            producer.send(topic, message);
comment|//        }
comment|//
comment|//        session.close();
comment|//        con.close();
comment|//
comment|//        // consume messages
comment|//        con = createConnection();
comment|//        session = con.createSession(false, Session.AUTO_ACKNOWLEDGE);
comment|//        MessageConsumer consumer = session.createDurableSubscriber(topic, "SubsId", "filter = 'true'", true);
comment|//        Listener listener = new Listener();
comment|//        consumer.setMessageListener(listener);
comment|//
comment|//        Thread.sleep(3 * 1000);
comment|//
comment|//        session.close();
comment|//        con.close();
comment|//
comment|//        assertEquals(sent, listener.count);
comment|//    }
comment|//
comment|//     public void testConsumeAllMatchedMessages() throws Exception {
comment|//         // create durable subscription
comment|//         Connection con = createConnection();
comment|//         Session session = con.createSession(false, Session.AUTO_ACKNOWLEDGE);
comment|//         session.createDurableSubscriber(topic, "SubsId", "filter = 'true'", true);
comment|//         session.close();
comment|//         con.close();
comment|//
comment|//         // send messages
comment|//         con = createConnection();
comment|//         session = con.createSession(false, Session.AUTO_ACKNOWLEDGE);
comment|//         MessageProducer producer = session.createProducer(null);
comment|//
comment|//         int sent = 0;
comment|//         for (int i = 0; i< 10; i++) {
comment|//             sent++;
comment|//             Message message = session.createMessage();
comment|//             message.setStringProperty("filter", "true");
comment|//             producer.send(topic, message);
comment|//         }
comment|//
comment|//         Thread.sleep(1 * 1000);
comment|//
comment|//         session.close();
comment|//         con.close();
comment|//
comment|//         // consume messages
comment|//         con = createConnection();
comment|//         session = con.createSession(false, Session.AUTO_ACKNOWLEDGE);
comment|//         MessageConsumer consumer = session.createDurableSubscriber(topic, "SubsId", "filter = 'true'", true);
comment|//         Listener listener = new Listener();
comment|//         consumer.setMessageListener(listener);
comment|//
comment|//         Thread.sleep(3 * 1000);
comment|//
comment|//         session.close();
comment|//         con.close();
comment|//
comment|//         assertEquals(sent, listener.count);
comment|//     }
comment|//
comment|//    public void initCombosForTestVerifyAllConsumedAreAcked() throws Exception {
comment|//        this.addCombinationValues("defaultPersistenceAdapter",
comment|//               new Object[]{ PersistenceAdapterChoice.KahaDB, PersistenceAdapterChoice.LevelDB, PersistenceAdapterChoice.JDBC});
comment|//        this.addCombinationValues("usePrioritySupport",
comment|//                new Object[]{ Boolean.TRUE, Boolean.FALSE});
comment|//    }
comment|//
comment|//     public void testVerifyAllConsumedAreAcked() throws Exception {
comment|//         // create durable subscription
comment|//         Connection con = createConnection();
comment|//         Session session = con.createSession(false, Session.AUTO_ACKNOWLEDGE);
comment|//         session.createDurableSubscriber(topic, "SubsId", "filter = 'true'", true);
comment|//         session.close();
comment|//         con.close();
comment|//
comment|//         // send messages
comment|//         con = createConnection();
comment|//         session = con.createSession(false, Session.AUTO_ACKNOWLEDGE);
comment|//         MessageProducer producer = session.createProducer(null);
comment|//
comment|//         int sent = 0;
comment|//         for (int i = 0; i< 10; i++) {
comment|//             sent++;
comment|//             Message message = session.createMessage();
comment|//             message.setStringProperty("filter", "true");
comment|//             producer.send(topic, message);
comment|//         }
comment|//
comment|//         Thread.sleep(1 * 1000);
comment|//
comment|//         session.close();
comment|//         con.close();
comment|//
comment|//         // consume messages
comment|//         con = createConnection();
comment|//         session = con.createSession(false, Session.AUTO_ACKNOWLEDGE);
comment|//         MessageConsumer consumer = session.createDurableSubscriber(topic, "SubsId", "filter = 'true'", true);
comment|//         Listener listener = new Listener();
comment|//         consumer.setMessageListener(listener);
comment|//
comment|//         Thread.sleep(3 * 1000);
comment|//
comment|//         session.close();
comment|//         con.close();
comment|//
comment|//         LOG.info("Consumed: " + listener.count);
comment|//         assertEquals(sent, listener.count);
comment|//
comment|//         // consume messages again, should not get any
comment|//         con = createConnection();
comment|//         session = con.createSession(false, Session.AUTO_ACKNOWLEDGE);
comment|//         consumer = session.createDurableSubscriber(topic, "SubsId", "filter = 'true'", true);
comment|//         listener = new Listener();
comment|//         consumer.setMessageListener(listener);
comment|//
comment|//         Thread.sleep(3 * 1000);
comment|//
comment|//         session.close();
comment|//         con.close();
comment|//
comment|//         assertEquals(0, listener.count);
comment|//     }
comment|//
comment|//    public void testTwoOfflineSubscriptionCanConsume() throws Exception {
comment|//        // create durable subscription 1
comment|//        Connection con = createConnection("cliId1");
comment|//        Session session = con.createSession(false, Session.AUTO_ACKNOWLEDGE);
comment|//        session.createDurableSubscriber(topic, "SubsId", "filter = 'true'", true);
comment|//        session.close();
comment|//        con.close();
comment|//
comment|//        // create durable subscription 2
comment|//        Connection con2 = createConnection("cliId2");
comment|//        Session session2 = con2.createSession(false, Session.AUTO_ACKNOWLEDGE);
comment|//        MessageConsumer consumer2 = session2.createDurableSubscriber(topic, "SubsId", "filter = 'true'", true);
comment|//        Listener listener2 = new Listener();
comment|//        consumer2.setMessageListener(listener2);
comment|//
comment|//        // send messages
comment|//        con = createConnection();
comment|//        session = con.createSession(false, Session.AUTO_ACKNOWLEDGE);
comment|//        MessageProducer producer = session.createProducer(null);
comment|//
comment|//        int sent = 0;
comment|//        for (int i = 0; i< 10; i++) {
comment|//            sent++;
comment|//            Message message = session.createMessage();
comment|//            message.setStringProperty("filter", "true");
comment|//            producer.send(topic, message);
comment|//        }
comment|//
comment|//        Thread.sleep(1 * 1000);
comment|//        session.close();
comment|//        con.close();
comment|//
comment|//        // test online subs
comment|//        Thread.sleep(3 * 1000);
comment|//        session2.close();
comment|//        con2.close();
comment|//
comment|//        assertEquals(sent, listener2.count);
comment|//
comment|//        // consume messages
comment|//        con = createConnection("cliId1");
comment|//        session = con.createSession(false, Session.AUTO_ACKNOWLEDGE);
comment|//        MessageConsumer consumer = session.createDurableSubscriber(topic, "SubsId", "filter = 'true'", true);
comment|//        Listener listener = new Listener();
comment|//        consumer.setMessageListener(listener);
comment|//
comment|//        Thread.sleep(3 * 1000);
comment|//
comment|//        session.close();
comment|//        con.close();
comment|//
comment|//        assertEquals("offline consumer got all", sent, listener.count);
comment|//    }
comment|//
comment|//    public void initCombosForTestJMXCountersWithOfflineSubs() throws Exception {
comment|//        this.addCombinationValues("keepDurableSubsActive",
comment|//                new Object[]{Boolean.TRUE, Boolean.FALSE});
comment|//    }
comment|//
comment|//    public void testJMXCountersWithOfflineSubs() throws Exception {
comment|//        // create durable subscription 1
comment|//        Connection con = createConnection("cliId1");
comment|//        Session session = con.createSession(false, Session.AUTO_ACKNOWLEDGE);
comment|//        session.createDurableSubscriber(topic, "SubsId", null, true);
comment|//        session.close();
comment|//        con.close();
comment|//
comment|//        // restart broker
comment|//        broker.stop();
comment|//        createBroker(false /*deleteAllMessages*/);
comment|//
comment|//        // send messages
comment|//        con = createConnection();
comment|//        session = con.createSession(false, Session.AUTO_ACKNOWLEDGE);
comment|//        MessageProducer producer = session.createProducer(null);
comment|//
comment|//        int sent = 0;
comment|//        for (int i = 0; i< 10; i++) {
comment|//            sent++;
comment|//            Message message = session.createMessage();
comment|//            producer.send(topic, message);
comment|//        }
comment|//        session.close();
comment|//        con.close();
comment|//
comment|//        // consume some messages
comment|//        con = createConnection("cliId1");
comment|//        session = con.createSession(false, Session.AUTO_ACKNOWLEDGE);
comment|//        MessageConsumer consumer = session.createDurableSubscriber(topic, "SubsId", null, true);
comment|//
comment|//        for (int i=0; i<sent/2; i++) {
comment|//            Message m =  consumer.receive(4000);
comment|//            assertNotNull("got message: " + i, m);
comment|//            LOG.info("Got :" + i + ", " + m);
comment|//        }
comment|//
comment|//        // check some counters while active
comment|//        ObjectName activeDurableSubName = broker.getAdminView().getDurableTopicSubscribers()[0];
comment|//        LOG.info("active durable sub name: " + activeDurableSubName);
comment|//        final DurableSubscriptionViewMBean durableSubscriptionView = (DurableSubscriptionViewMBean)
comment|//                broker.getManagementContext().newProxyInstance(activeDurableSubName, DurableSubscriptionViewMBean.class, true);
comment|//
comment|//        assertTrue("is active", durableSubscriptionView.isActive());
comment|//        assertEquals("all enqueued", keepDurableSubsActive ? 10 : 0, durableSubscriptionView.getEnqueueCounter());
comment|//        assertTrue("correct waiting acks", Wait.waitFor(new Wait.Condition() {
comment|//            @Override
comment|//            public boolean isSatisified() throws Exception {
comment|//                return 5 == durableSubscriptionView.getMessageCountAwaitingAcknowledge();
comment|//            }
comment|//        }));
comment|//        assertEquals("correct dequeue", 5, durableSubscriptionView.getDequeueCounter());
comment|//
comment|//
comment|//        ObjectName destinationName = broker.getAdminView().getTopics()[0];
comment|//        TopicViewMBean topicView = (TopicViewMBean) broker.getManagementContext().newProxyInstance(destinationName, TopicViewMBean.class, true);
comment|//        assertEquals("correct enqueue", 10, topicView.getEnqueueCount());
comment|//        assertEquals("still zero dequeue, we don't decrement on each sub ack to stop exceeding the enqueue count with multiple subs", 0, topicView.getDequeueCount());
comment|//        assertEquals("inflight", 5, topicView.getInFlightCount());
comment|//
comment|//        session.close();
comment|//        con.close();
comment|//
comment|//        // check some counters when inactive
comment|//        ObjectName inActiveDurableSubName = broker.getAdminView().getInactiveDurableTopicSubscribers()[0];
comment|//        LOG.info("inactive durable sub name: " + inActiveDurableSubName);
comment|//        DurableSubscriptionViewMBean durableSubscriptionView1 = (DurableSubscriptionViewMBean)
comment|//                broker.getManagementContext().newProxyInstance(inActiveDurableSubName, DurableSubscriptionViewMBean.class, true);
comment|//
comment|//        assertTrue("is not active", !durableSubscriptionView1.isActive());
comment|//        assertEquals("all enqueued", keepDurableSubsActive ? 10 : 0, durableSubscriptionView1.getEnqueueCounter());
comment|//        assertEquals("correct awaiting ack", 0, durableSubscriptionView1.getMessageCountAwaitingAcknowledge());
comment|//        assertEquals("correct dequeue", keepDurableSubsActive ? 5 : 0, durableSubscriptionView1.getDequeueCounter());
comment|//
comment|//        // destination view
comment|//        assertEquals("correct enqueue", 10, topicView.getEnqueueCount());
comment|//        assertEquals("still zero dequeue, we don't decrement on each sub ack to stop exceeding the enqueue count with multiple subs", 0, topicView.getDequeueCount());
comment|//        assertEquals("inflight back to 0 after deactivate", 0, topicView.getInFlightCount());
comment|//
comment|//        // consume the rest
comment|//        con = createConnection("cliId1");
comment|//        session = con.createSession(false, Session.AUTO_ACKNOWLEDGE);
comment|//        consumer = session.createDurableSubscriber(topic, "SubsId", null, true);
comment|//
comment|//        for (int i=0; i<sent/2;i++) {
comment|//            Message m =  consumer.receive(30000);
comment|//            assertNotNull("got message: " + i, m);
comment|//            LOG.info("Got :" + i + ", " + m);
comment|//        }
comment|//
comment|//        activeDurableSubName = broker.getAdminView().getDurableTopicSubscribers()[0];
comment|//        LOG.info("durable sub name: " + activeDurableSubName);
comment|//        final DurableSubscriptionViewMBean durableSubscriptionView2 = (DurableSubscriptionViewMBean)
comment|//                broker.getManagementContext().newProxyInstance(activeDurableSubName, DurableSubscriptionViewMBean.class, true);
comment|//
comment|//        assertTrue("is active", durableSubscriptionView2.isActive());
comment|//        assertEquals("all enqueued", keepDurableSubsActive ? 10 : 0, durableSubscriptionView2.getEnqueueCounter());
comment|//        assertTrue("correct dequeue", Wait.waitFor(new Wait.Condition() {
comment|//            @Override
comment|//            public boolean isSatisified() throws Exception {
comment|//                long val = durableSubscriptionView2.getDequeueCounter();
comment|//                LOG.info("dequeue count:" + val);
comment|//                return 10 == val;
comment|//            }
comment|//        }));
comment|//    }
comment|//
comment|//    public void initCombosForTestOfflineSubscriptionCanConsumeAfterOnlineSubs() throws Exception {
comment|//        this.addCombinationValues("defaultPersistenceAdapter",
comment|//                new Object[]{ PersistenceAdapterChoice.KahaDB, PersistenceAdapterChoice.LevelDB, PersistenceAdapterChoice.JDBC});
comment|//        this.addCombinationValues("usePrioritySupport",
comment|//                new Object[]{ Boolean.TRUE, Boolean.FALSE});
comment|//    }
comment|//
comment|//    public void testOfflineSubscriptionCanConsumeAfterOnlineSubs() throws Exception {
comment|//        Connection con = createConnection("offCli1");
comment|//        Session session = con.createSession(false, Session.AUTO_ACKNOWLEDGE);
comment|//        session.createDurableSubscriber(topic, "SubsId", "filter = 'true'", true);
comment|//        session.close();
comment|//        con.close();
comment|//
comment|//        con = createConnection("offCli2");
comment|//        session = con.createSession(false, Session.AUTO_ACKNOWLEDGE);
comment|//        session.createDurableSubscriber(topic, "SubsId", "filter = 'true'", true);
comment|//        session.close();
comment|//        con.close();
comment|//
comment|//        Connection con2 = createConnection("onlineCli1");
comment|//        Session session2 = con2.createSession(false, Session.AUTO_ACKNOWLEDGE);
comment|//        MessageConsumer consumer2 = session2.createDurableSubscriber(topic, "SubsId", "filter = 'true'", true);
comment|//        Listener listener2 = new Listener();
comment|//        consumer2.setMessageListener(listener2);
comment|//
comment|//        // send messages
comment|//        con = createConnection();
comment|//        session = con.createSession(false, Session.AUTO_ACKNOWLEDGE);
comment|//        MessageProducer producer = session.createProducer(null);
comment|//
comment|//        int sent = 0;
comment|//        for (int i = 0; i< 10; i++) {
comment|//            sent++;
comment|//            Message message = session.createMessage();
comment|//            message.setStringProperty("filter", "true");
comment|//            producer.send(topic, message);
comment|//        }
comment|//
comment|//        Thread.sleep(1 * 1000);
comment|//        session.close();
comment|//        con.close();
comment|//
comment|//        // test online subs
comment|//        Thread.sleep(3 * 1000);
comment|//        session2.close();
comment|//        con2.close();
comment|//        assertEquals(sent, listener2.count);
comment|//
comment|//        // restart broker
comment|//        broker.stop();
comment|//        createBroker(false /*deleteAllMessages*/);
comment|//
comment|//        // test offline
comment|//        con = createConnection("offCli1");
comment|//        session = con.createSession(false, Session.AUTO_ACKNOWLEDGE);
comment|//        MessageConsumer consumer = session.createDurableSubscriber(topic, "SubsId", "filter = 'true'", true);
comment|//
comment|//        Connection con3 = createConnection("offCli2");
comment|//        Session session3 = con3.createSession(false, Session.AUTO_ACKNOWLEDGE);
comment|//        MessageConsumer consumer3 = session3.createDurableSubscriber(topic, "SubsId", "filter = 'true'", true);
comment|//
comment|//        Listener listener = new Listener();
comment|//        consumer.setMessageListener(listener);
comment|//        Listener listener3 = new Listener();
comment|//        consumer3.setMessageListener(listener3);
comment|//
comment|//        Thread.sleep(3 * 1000);
comment|//
comment|//        session.close();
comment|//        con.close();
comment|//        session3.close();
comment|//        con3.close();
comment|//
comment|//        assertEquals(sent, listener.count);
comment|//        assertEquals(sent, listener3.count);
comment|//    }
comment|//
comment|//    public void initCombosForTestInterleavedOfflineSubscriptionCanConsume() throws Exception {
comment|//        this.addCombinationValues("defaultPersistenceAdapter",
comment|//                new Object[]{PersistenceAdapterChoice.KahaDB, PersistenceAdapterChoice.LevelDB, PersistenceAdapterChoice.JDBC});
comment|//    }
comment|//
comment|//    public void testInterleavedOfflineSubscriptionCanConsume() throws Exception {
comment|//        // create durable subscription 1
comment|//        Connection con = createConnection("cliId1");
comment|//        Session session = con.createSession(false, Session.AUTO_ACKNOWLEDGE);
comment|//        session.createDurableSubscriber(topic, "SubsId", "filter = 'true'", true);
comment|//        session.close();
comment|//        con.close();
comment|//
comment|//        // send messages
comment|//        con = createConnection();
comment|//        session = con.createSession(false, Session.AUTO_ACKNOWLEDGE);
comment|//        MessageProducer producer = session.createProducer(null);
comment|//
comment|//        int sent = 0;
comment|//        for (int i = 0; i< 10; i++) {
comment|//            sent++;
comment|//            Message message = session.createMessage();
comment|//            message.setStringProperty("filter", "true");
comment|//            producer.send(topic, message);
comment|//        }
comment|//
comment|//        Thread.sleep(1 * 1000);
comment|//
comment|//        // create durable subscription 2
comment|//        Connection con2 = createConnection("cliId2");
comment|//        Session session2 = con2.createSession(false, Session.AUTO_ACKNOWLEDGE);
comment|//        MessageConsumer consumer2 = session2.createDurableSubscriber(topic, "SubsId", "filter = 'true'", true);
comment|//        Listener listener2 = new Listener();
comment|//        consumer2.setMessageListener(listener2);
comment|//
comment|//        assertEquals(0, listener2.count);
comment|//        session2.close();
comment|//        con2.close();
comment|//
comment|//        // send some more
comment|//        for (int i = 0; i< 10; i++) {
comment|//            sent++;
comment|//            Message message = session.createMessage();
comment|//            message.setStringProperty("filter", "true");
comment|//            producer.send(topic, message);
comment|//        }
comment|//
comment|//        Thread.sleep(1 * 1000);
comment|//        session.close();
comment|//        con.close();
comment|//
comment|//        con2 = createConnection("cliId2");
comment|//        session2 = con2.createSession(false, Session.AUTO_ACKNOWLEDGE);
comment|//        consumer2 = session2.createDurableSubscriber(topic, "SubsId", "filter = 'true'", true);
comment|//        listener2 = new Listener("cliId2");
comment|//        consumer2.setMessageListener(listener2);
comment|//        // test online subs
comment|//        Thread.sleep(3 * 1000);
comment|//
comment|//        assertEquals(10, listener2.count);
comment|//
comment|//        // consume all messages
comment|//        con = createConnection("cliId1");
comment|//        session = con.createSession(false, Session.AUTO_ACKNOWLEDGE);
comment|//        MessageConsumer consumer = session.createDurableSubscriber(topic, "SubsId", "filter = 'true'", true);
comment|//        Listener listener = new Listener("cliId1");
comment|//        consumer.setMessageListener(listener);
comment|//
comment|//        Thread.sleep(3 * 1000);
comment|//
comment|//        session.close();
comment|//        con.close();
comment|//
comment|//        assertEquals("offline consumer got all", sent, listener.count);
comment|//    }
comment|//
comment|//    public void initCombosForTestMixOfOnLineAndOfflineSubsGetAllMatched() throws Exception {
comment|//        this.addCombinationValues("defaultPersistenceAdapter",
comment|//                new Object[]{ PersistenceAdapterChoice.KahaDB, PersistenceAdapterChoice.LevelDB, PersistenceAdapterChoice.JDBC});
comment|//    }
comment|//
comment|//    private static String filter = "$a='A1' AND (($b=true AND $c=true) OR ($d='D1' OR $d='D2'))";
comment|//    public void testMixOfOnLineAndOfflineSubsGetAllMatched() throws Exception {
comment|//        // create offline subs 1
comment|//        Connection con = createConnection("offCli1");
comment|//        Session session = con.createSession(false, Session.AUTO_ACKNOWLEDGE);
comment|//        session.createDurableSubscriber(topic, "SubsId", filter, true);
comment|//        session.close();
comment|//        con.close();
comment|//
comment|//        // create offline subs 2
comment|//        con = createConnection("offCli2");
comment|//        session = con.createSession(false, Session.AUTO_ACKNOWLEDGE);
comment|//        session.createDurableSubscriber(topic, "SubsId", filter, true);
comment|//        session.close();
comment|//        con.close();
comment|//
comment|//        // create online subs
comment|//        Connection con2 = createConnection("onlineCli1");
comment|//        Session session2 = con2.createSession(false, Session.AUTO_ACKNOWLEDGE);
comment|//        MessageConsumer consumer2 = session2.createDurableSubscriber(topic, "SubsId", filter, true);
comment|//        Listener listener2 = new Listener();
comment|//        consumer2.setMessageListener(listener2);
comment|//
comment|//        // create non-durable consumer
comment|//        Connection con4 = createConnection("nondurableCli");
comment|//        Session session4 = con4.createSession(false, Session.AUTO_ACKNOWLEDGE);
comment|//        MessageConsumer consumer4 = session4.createConsumer(topic, filter, true);
comment|//        Listener listener4 = new Listener();
comment|//        consumer4.setMessageListener(listener4);
comment|//
comment|//        // send messages
comment|//        con = createConnection();
comment|//        session = con.createSession(false, Session.AUTO_ACKNOWLEDGE);
comment|//        MessageProducer producer = session.createProducer(null);
comment|//
comment|//        boolean hasRelevant = false;
comment|//        int filtered = 0;
comment|//        for (int i = 0; i< 100; i++) {
comment|//            int postf = (int) (Math.random() * 9) + 1;
comment|//            String d = "D" + postf;
comment|//
comment|//            if ("D1".equals(d) || "D2".equals(d)) {
comment|//                hasRelevant = true;
comment|//                filtered++;
comment|//            }
comment|//
comment|//            Message message = session.createMessage();
comment|//            message.setStringProperty("$a", "A1");
comment|//            message.setStringProperty("$d", d);
comment|//            producer.send(topic, message);
comment|//        }
comment|//
comment|//        Message message = session.createMessage();
comment|//        message.setStringProperty("$a", "A1");
comment|//        message.setBooleanProperty("$b", true);
comment|//        message.setBooleanProperty("$c", hasRelevant);
comment|//        producer.send(topic, message);
comment|//
comment|//        if (hasRelevant)
comment|//            filtered++;
comment|//
comment|//        Thread.sleep(1 * 1000);
comment|//        session.close();
comment|//        con.close();
comment|//
comment|//        Thread.sleep(3 * 1000);
comment|//
comment|//        // test non-durable consumer
comment|//        session4.close();
comment|//        con4.close();
comment|//        assertEquals(filtered, listener4.count); // succeeded!
comment|//
comment|//        // test online subs
comment|//        session2.close();
comment|//        con2.close();
comment|//        assertEquals(filtered, listener2.count); // succeeded!
comment|//
comment|//        // test offline 1
comment|//        con = createConnection("offCli1");
comment|//        session = con.createSession(false, Session.AUTO_ACKNOWLEDGE);
comment|//        MessageConsumer consumer = session.createDurableSubscriber(topic, "SubsId", filter, true);
comment|//        Listener listener = new FilterCheckListener();
comment|//        consumer.setMessageListener(listener);
comment|//
comment|//        Thread.sleep(3 * 1000);
comment|//        session.close();
comment|//        con.close();
comment|//
comment|//        assertEquals(filtered, listener.count);
comment|//
comment|//        // test offline 2
comment|//        Connection con3 = createConnection("offCli2");
comment|//        Session session3 = con3.createSession(false, Session.AUTO_ACKNOWLEDGE);
comment|//        MessageConsumer consumer3 = session3.createDurableSubscriber(topic, "SubsId", filter, true);
comment|//        Listener listener3 = new FilterCheckListener();
comment|//        consumer3.setMessageListener(listener3);
comment|//
comment|//        Thread.sleep(3 * 1000);
comment|//        session3.close();
comment|//        con3.close();
comment|//
comment|//        assertEquals(filtered, listener3.count);
comment|//        assertTrue("no unexpected exceptions: " + exceptions, exceptions.isEmpty());
comment|//    }
comment|//
comment|//    public void testRemovedDurableSubDeletes() throws Exception {
comment|//        // create durable subscription 1
comment|//        Connection con = createConnection("cliId1");
comment|//        Session session = con.createSession(false, Session.AUTO_ACKNOWLEDGE);
comment|//        session.createDurableSubscriber(topic, "SubsId", "filter = 'true'", true);
comment|//        session.close();
comment|//        con.close();
comment|//
comment|//        // send messages
comment|//        con = createConnection();
comment|//        session = con.createSession(false, Session.AUTO_ACKNOWLEDGE);
comment|//        MessageProducer producer = session.createProducer(null);
comment|//
comment|//        for (int i = 0; i< 10; i++) {
comment|//            Message message = session.createMessage();
comment|//            message.setStringProperty("filter", "true");
comment|//            producer.send(topic, message);
comment|//        }
comment|//
comment|//        Thread.sleep(1 * 1000);
comment|//
comment|//        Connection con2 = createConnection("cliId1");
comment|//        Session session2 = con2.createSession(false, Session.AUTO_ACKNOWLEDGE);
comment|//        session2.unsubscribe("SubsId");
comment|//        session2.close();
comment|//        con2.close();
comment|//
comment|//        // see if retroactive can consumer any
comment|//        topic = new ActiveMQTopic(topic.getPhysicalName() + "?consumer.retroactive=true");
comment|//        con = createConnection("offCli2");
comment|//        session = con.createSession(false, Session.AUTO_ACKNOWLEDGE);
comment|//        MessageConsumer consumer = session.createDurableSubscriber(topic, "SubsId", filter, true);
comment|//        Listener listener = new Listener();
comment|//        consumer.setMessageListener(listener);
comment|//        session.close();
comment|//        con.close();
comment|//        assertEquals(0, listener.count);
comment|//    }
comment|//
comment|//    public void testRemovedDurableSubDeletesFromIndex() throws Exception {
comment|//
comment|//        if (! (broker.getPersistenceAdapter() instanceof KahaDBPersistenceAdapter)) {
comment|//            return;
comment|//        }
comment|//
comment|//        final int numMessages = 2750;
comment|//
comment|//        KahaDBPersistenceAdapter kahaDBPersistenceAdapter = (KahaDBPersistenceAdapter)broker.getPersistenceAdapter();
comment|//        PageFile pageFile = kahaDBPersistenceAdapter.getStore().getPageFile();
comment|//        LOG.info("PageCount " + pageFile.getPageCount() + " f:" + pageFile.getFreePageCount() + ", fileSize:" + pageFile.getFile().length());
comment|//
comment|//        long lastDiff = 0;
comment|//        for (int repeats=0; repeats<2; repeats++) {
comment|//
comment|//            LOG.info("Iteration: "+ repeats  + " Count:" + pageFile.getPageCount() + " f:" + pageFile.getFreePageCount());
comment|//
comment|//            Connection con = createConnection("cliId1" + "-" + repeats);
comment|//            Session session = con.createSession(false, Session.AUTO_ACKNOWLEDGE);
comment|//            session.createDurableSubscriber(topic, "SubsId", "filter = 'true'", true);
comment|//            session.close();
comment|//            con.close();
comment|//
comment|//            // send messages
comment|//            con = createConnection();
comment|//            session = con.createSession(false, Session.AUTO_ACKNOWLEDGE);
comment|//            MessageProducer producer = session.createProducer(null);
comment|//
comment|//            for (int i = 0; i< numMessages; i++) {
comment|//                Message message = session.createMessage();
comment|//                message.setStringProperty("filter", "true");
comment|//                producer.send(topic, message);
comment|//            }
comment|//            con.close();
comment|//
comment|//            Connection con2 = createConnection("cliId1" + "-" + repeats);
comment|//            Session session2 = con2.createSession(false, Session.AUTO_ACKNOWLEDGE);
comment|//            session2.unsubscribe("SubsId");
comment|//            session2.close();
comment|//            con2.close();
comment|//
comment|//            LOG.info("PageCount " + pageFile.getPageCount() + " f:" + pageFile.getFreePageCount() +  " diff: " + (pageFile.getPageCount() - pageFile.getFreePageCount()) + " fileSize:" + pageFile.getFile().length());
comment|//
comment|//            if (lastDiff != 0) {
comment|//                assertEquals("Only use X pages per iteration: " + repeats, lastDiff, pageFile.getPageCount() - pageFile.getFreePageCount());
comment|//            }
comment|//            lastDiff = pageFile.getPageCount() - pageFile.getFreePageCount();
comment|//        }
comment|//    }
comment|//
comment|//    public void initCombosForTestOfflineSubscriptionWithSelectorAfterRestart() throws Exception {
comment|//        this.addCombinationValues("defaultPersistenceAdapter",
comment|//                new Object[]{ PersistenceAdapterChoice.KahaDB, PersistenceAdapterChoice.LevelDB, PersistenceAdapterChoice.JDBC});
comment|//    }
comment|//
comment|//    public void testOfflineSubscriptionWithSelectorAfterRestart() throws Exception {
comment|//
comment|//        if (PersistenceAdapterChoice.LevelDB == defaultPersistenceAdapter) {
comment|//            // https://issues.apache.org/jira/browse/AMQ-4296
comment|//            return;
comment|//        }
comment|//
comment|//        // create offline subs 1
comment|//        Connection con = createConnection("offCli1");
comment|//        Session session = con.createSession(false, Session.AUTO_ACKNOWLEDGE);
comment|//        session.createDurableSubscriber(topic, "SubsId", "filter = 'true'", true);
comment|//        session.close();
comment|//        con.close();
comment|//
comment|//        // create offline subs 2
comment|//        con = createConnection("offCli2");
comment|//        session = con.createSession(false, Session.AUTO_ACKNOWLEDGE);
comment|//        session.createDurableSubscriber(topic, "SubsId", "filter = 'true'", true);
comment|//        session.close();
comment|//        con.close();
comment|//
comment|//        // send messages
comment|//        con = createConnection();
comment|//        session = con.createSession(false, Session.AUTO_ACKNOWLEDGE);
comment|//        MessageProducer producer = session.createProducer(null);
comment|//
comment|//        int filtered = 0;
comment|//        for (int i = 0; i< 10; i++) {
comment|//            boolean filter = (int) (Math.random() * 2)>= 1;
comment|//            if (filter)
comment|//                filtered++;
comment|//
comment|//            Message message = session.createMessage();
comment|//            message.setStringProperty("filter", filter ? "true" : "false");
comment|//            producer.send(topic, message);
comment|//        }
comment|//
comment|//        LOG.info("sent: " + filtered);
comment|//        Thread.sleep(1 * 1000);
comment|//        session.close();
comment|//        con.close();
comment|//
comment|//        // restart broker
comment|//        Thread.sleep(3 * 1000);
comment|//        broker.stop();
comment|//        createBroker(false /*deleteAllMessages*/);
comment|//
comment|//        // send more messages
comment|//        con = createConnection();
comment|//        session = con.createSession(false, Session.AUTO_ACKNOWLEDGE);
comment|//        producer = session.createProducer(null);
comment|//
comment|//        for (int i = 0; i< 10; i++) {
comment|//            boolean filter = (int) (Math.random() * 2)>= 1;
comment|//            if (filter)
comment|//                filtered++;
comment|//
comment|//            Message message = session.createMessage();
comment|//            message.setStringProperty("filter", filter ? "true" : "false");
comment|//            producer.send(topic, message);
comment|//        }
comment|//
comment|//        LOG.info("after restart, total sent with filter='true': " + filtered);
comment|//        Thread.sleep(1 * 1000);
comment|//        session.close();
comment|//        con.close();
comment|//
comment|//        // test offline subs
comment|//        con = createConnection("offCli1");
comment|//        session = con.createSession(false, Session.AUTO_ACKNOWLEDGE);
comment|//        MessageConsumer consumer = session.createDurableSubscriber(topic, "SubsId", "filter = 'true'", true);
comment|//        Listener listener = new Listener("1>");
comment|//        consumer.setMessageListener(listener);
comment|//
comment|//        Connection con3 = createConnection("offCli2");
comment|//        Session session3 = con3.createSession(false, Session.AUTO_ACKNOWLEDGE);
comment|//        MessageConsumer consumer3 = session3.createDurableSubscriber(topic, "SubsId", "filter = 'true'", true);
comment|//        Listener listener3 = new Listener();
comment|//        consumer3.setMessageListener(listener3);
comment|//
comment|//        Thread.sleep(3 * 1000);
comment|//
comment|//        session.close();
comment|//        con.close();
comment|//        session3.close();
comment|//        con3.close();
comment|//
comment|//        assertEquals(filtered, listener.count);
comment|//        assertEquals(filtered, listener3.count);
comment|//    }
comment|//
comment|//    public void initCombosForTestOfflineAfterRestart() throws Exception {
comment|//        this.addCombinationValues("defaultPersistenceAdapter",
comment|//                new Object[]{ PersistenceAdapterChoice.KahaDB, PersistenceAdapterChoice.LevelDB, PersistenceAdapterChoice.JDBC});
comment|//    }
comment|//
comment|//    public void testOfflineSubscriptionAfterRestart() throws Exception {
comment|//        // create offline subs 1
comment|//        Connection con = createConnection("offCli1");
comment|//        Session session = con.createSession(false, Session.AUTO_ACKNOWLEDGE);
comment|//        MessageConsumer consumer = session.createDurableSubscriber(topic, "SubsId", null, false);
comment|//        Listener listener = new Listener();
comment|//        consumer.setMessageListener(listener);
comment|//
comment|//        // send messages
comment|//        MessageProducer producer = session.createProducer(null);
comment|//
comment|//        int sent = 0;
comment|//        for (int i = 0; i< 10; i++) {
comment|//            sent++;
comment|//            Message message = session.createMessage();
comment|//            message.setStringProperty("filter", "false");
comment|//            producer.send(topic, message);
comment|//        }
comment|//
comment|//        LOG.info("sent: " + sent);
comment|//        Thread.sleep(5 * 1000);
comment|//        session.close();
comment|//        con.close();
comment|//
comment|//        assertEquals(sent, listener.count);
comment|//
comment|//        // restart broker
comment|//        Thread.sleep(3 * 1000);
comment|//        broker.stop();
comment|//        createBroker(false /*deleteAllMessages*/);
comment|//
comment|//        // send more messages
comment|//        con = createConnection();
comment|//        session = con.createSession(false, Session.AUTO_ACKNOWLEDGE);
comment|//        producer = session.createProducer(null);
comment|//
comment|//        for (int i = 0; i< 10; i++) {
comment|//            sent++;
comment|//            Message message = session.createMessage();
comment|//            message.setStringProperty("filter", "false");
comment|//            producer.send(topic, message);
comment|//        }
comment|//
comment|//        LOG.info("after restart, sent: " + sent);
comment|//        Thread.sleep(1 * 1000);
comment|//        session.close();
comment|//        con.close();
comment|//
comment|//        // test offline subs
comment|//        con = createConnection("offCli1");
comment|//        session = con.createSession(false, Session.AUTO_ACKNOWLEDGE);
comment|//        consumer = session.createDurableSubscriber(topic, "SubsId", null, true);
comment|//        consumer.setMessageListener(listener);
comment|//
comment|//        Thread.sleep(3 * 1000);
comment|//
comment|//        session.close();
comment|//        con.close();
comment|//
comment|//        assertEquals(sent, listener.count);
comment|//    }
comment|//
comment|//    public void testInterleavedOfflineSubscriptionCanConsumeAfterUnsub() throws Exception {
comment|//        // create offline subs 1
comment|//        Connection con = createConnection("offCli1");
comment|//        Session session = con.createSession(false, Session.AUTO_ACKNOWLEDGE);
comment|//        session.createDurableSubscriber(topic, "SubsId", "filter = 'true'", true);
comment|//        session.close();
comment|//        con.close();
comment|//
comment|//        // create offline subs 2
comment|//        con = createConnection("offCli2");
comment|//        session = con.createSession(false, Session.AUTO_ACKNOWLEDGE);
comment|//        session.createDurableSubscriber(topic, "SubsId", null, true);
comment|//        session.close();
comment|//        con.close();
comment|//
comment|//        // send messages
comment|//        con = createConnection();
comment|//        session = con.createSession(false, Session.AUTO_ACKNOWLEDGE);
comment|//        MessageProducer producer = session.createProducer(null);
comment|//
comment|//        int sent = 0;
comment|//        for (int i = 0; i< 10; i++) {
comment|//            boolean filter = (int) (Math.random() * 2)>= 1;
comment|//
comment|//            sent++;
comment|//
comment|//            Message message = session.createMessage();
comment|//            message.setStringProperty("filter", filter ? "true" : "false");
comment|//            producer.send(topic, message);
comment|//        }
comment|//
comment|//        Thread.sleep(1 * 1000);
comment|//
comment|//        Connection con2 = createConnection("offCli1");
comment|//        Session session2 = con2.createSession(false, Session.AUTO_ACKNOWLEDGE);
comment|//        session2.unsubscribe("SubsId");
comment|//        session2.close();
comment|//        con2.close();
comment|//
comment|//        // consume all messages
comment|//        con = createConnection("offCli2");
comment|//        session = con.createSession(false, Session.AUTO_ACKNOWLEDGE);
comment|//        MessageConsumer consumer = session.createDurableSubscriber(topic, "SubsId", null, true);
comment|//        Listener listener = new Listener("SubsId");
comment|//        consumer.setMessageListener(listener);
comment|//
comment|//        Thread.sleep(3 * 1000);
comment|//
comment|//        session.close();
comment|//        con.close();
comment|//
comment|//        assertEquals("offline consumer got all", sent, listener.count);
comment|//    }
comment|//
comment|//    public void testNoDuplicateOnConcurrentSendTranCommitAndActivate() throws Exception {
comment|//        final int messageCount = 1000;
comment|//        Connection con = null;
comment|//        Session session = null;
comment|//        final int numConsumers = 10;
comment|//        for (int i = 0; i<= numConsumers; i++) {
comment|//            con = createConnection("cli" + i);
comment|//            session = con.createSession(false, Session.AUTO_ACKNOWLEDGE);
comment|//            session.createDurableSubscriber(topic, "SubsId", null, true);
comment|//            session.close();
comment|//            con.close();
comment|//        }
comment|//
comment|//        class CheckForDupsClient implements Runnable {
comment|//            HashSet<Long> ids = new HashSet<Long>();
comment|//            final int id;
comment|//
comment|//            public CheckForDupsClient(int id) {
comment|//                this.id = id;
comment|//            }
comment|//
comment|//            @Override
comment|//            public void run() {
comment|//                try {
comment|//                    Connection con = createConnection("cli" + id);
comment|//                    Session session = con.createSession(false, Session.AUTO_ACKNOWLEDGE);
comment|//                    for (int j=0;j<2;j++) {
comment|//                        MessageConsumer consumer = session.createDurableSubscriber(topic, "SubsId", null, true);
comment|//                        for (int i = 0; i< messageCount/2; i++) {
comment|//                            Message message = consumer.receive(4000);
comment|//                            assertNotNull(message);
comment|//                            long producerSequenceId = new MessageId(message.getJMSMessageID()).getProducerSequenceId();
comment|//                            assertTrue("ID=" + id + " not a duplicate: " + producerSequenceId, ids.add(producerSequenceId));
comment|//                        }
comment|//                        consumer.close();
comment|//                    }
comment|//
comment|//                    // verify no duplicates left
comment|//                    MessageConsumer consumer = session.createDurableSubscriber(topic, "SubsId", null, true);
comment|//                    Message message = consumer.receive(4000);
comment|//                    if (message != null) {
comment|//                        long producerSequenceId = new MessageId(message.getJMSMessageID()).getProducerSequenceId();
comment|//                        assertTrue("ID=" + id + " not a duplicate: " + producerSequenceId, ids.add(producerSequenceId));
comment|//                    }
comment|//                    assertNull(message);
comment|//
comment|//
comment|//                    session.close();
comment|//                    con.close();
comment|//                } catch (Throwable e) {
comment|//                    e.printStackTrace();
comment|//                    exceptions.add(e);
comment|//                }
comment|//            }
comment|//        }
comment|//
comment|//        final String payLoad = new String(new byte[1000]);
comment|//        con = createConnection();
comment|//        final Session sendSession = con.createSession(true, Session.SESSION_TRANSACTED);
comment|//        MessageProducer producer = sendSession.createProducer(topic);
comment|//        for (int i = 0; i< messageCount; i++) {
comment|//            producer.send(sendSession.createTextMessage(payLoad));
comment|//        }
comment|//
comment|//        ExecutorService executorService = Executors.newCachedThreadPool();
comment|//
comment|//        // concurrent commit and activate
comment|//        executorService.execute(new Runnable() {
comment|//            @Override
comment|//            public void run() {
comment|//                try {
comment|//                    sendSession.commit();
comment|//                } catch (JMSException e) {
comment|//                    e.printStackTrace();
comment|//                    exceptions.add(e);
comment|//                }
comment|//            }
comment|//        });
comment|//        for (int i = 0; i< numConsumers; i++) {
comment|//            executorService.execute(new CheckForDupsClient(i));
comment|//        }
comment|//
comment|//        executorService.shutdown();
comment|//        executorService.awaitTermination(5, TimeUnit.MINUTES);
comment|//        con.close();
comment|//
comment|//        assertTrue("no exceptions: " + exceptions, exceptions.isEmpty());
comment|//    }
comment|//
comment|//    public void testOrderOnActivateDeactivate() throws Exception {
comment|//        for (int i=0;i<10;i++) {
comment|//            LOG.info("Iteration: " + i);
comment|//            doTestOrderOnActivateDeactivate();
comment|//            broker.stop();
comment|//            createBroker(true /*deleteAllMessages*/);
comment|//        }
comment|//    }
specifier|public
name|void
name|doTestOrderOnActivateDeactivate
parameter_list|()
throws|throws
name|Exception
block|{
specifier|final
name|int
name|messageCount
init|=
literal|1000
decl_stmt|;
name|Connection
name|con
init|=
literal|null
decl_stmt|;
name|Session
name|session
init|=
literal|null
decl_stmt|;
specifier|final
name|int
name|numConsumers
init|=
literal|4
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<=
name|numConsumers
condition|;
name|i
operator|++
control|)
block|{
name|con
operator|=
name|createConnection
argument_list|(
literal|"cli"
operator|+
name|i
argument_list|)
expr_stmt|;
name|session
operator|=
name|con
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
name|session
operator|.
name|createDurableSubscriber
argument_list|(
name|topic
argument_list|,
literal|"SubsId"
argument_list|,
literal|null
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|session
operator|.
name|close
argument_list|()
expr_stmt|;
name|con
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
specifier|final
name|String
name|url
init|=
literal|"failover:(tcp://localhost:"
operator|+
operator|(
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
operator|)
operator|.
name|getPort
argument_list|()
operator|+
literal|"?wireFormat.maxInactivityDuration=0)?"
operator|+
literal|"jms.watchTopicAdvisories=false&"
operator|+
literal|"jms.alwaysSyncSend=true&jms.dispatchAsync=true&"
operator|+
literal|"jms.sendAcksAsync=true&"
operator|+
literal|"initialReconnectDelay=100&maxReconnectDelay=30000&"
operator|+
literal|"useExponentialBackOff=true"
decl_stmt|;
specifier|final
name|ActiveMQConnectionFactory
name|clientFactory
init|=
operator|new
name|ActiveMQConnectionFactory
argument_list|(
name|url
argument_list|)
decl_stmt|;
class|class
name|CheckOrderClient
implements|implements
name|Runnable
block|{
specifier|final
name|int
name|id
decl_stmt|;
name|int
name|runCount
init|=
literal|0
decl_stmt|;
specifier|public
name|CheckOrderClient
parameter_list|(
name|int
name|id
parameter_list|)
block|{
name|this
operator|.
name|id
operator|=
name|id
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|run
parameter_list|()
block|{
try|try
block|{
synchronized|synchronized
init|(
name|this
init|)
block|{
name|Connection
name|con
init|=
name|clientFactory
operator|.
name|createConnection
argument_list|()
decl_stmt|;
name|con
operator|.
name|setClientID
argument_list|(
literal|"cli"
operator|+
name|id
argument_list|)
expr_stmt|;
name|con
operator|.
name|start
argument_list|()
expr_stmt|;
name|Session
name|session
init|=
name|con
operator|.
name|createSession
argument_list|(
literal|false
argument_list|,
name|Session
operator|.
name|CLIENT_ACKNOWLEDGE
argument_list|)
decl_stmt|;
name|MessageConsumer
name|consumer
init|=
name|session
operator|.
name|createDurableSubscriber
argument_list|(
name|topic
argument_list|,
literal|"SubsId"
argument_list|,
literal|null
argument_list|,
literal|true
argument_list|)
decl_stmt|;
name|int
name|nextId
init|=
literal|0
decl_stmt|;
operator|++
name|runCount
expr_stmt|;
name|int
name|i
init|=
literal|0
decl_stmt|;
for|for
control|(
init|;
name|i
operator|<
name|messageCount
operator|/
literal|2
condition|;
name|i
operator|++
control|)
block|{
name|Message
name|message
init|=
name|consumer
operator|.
name|receiveNoWait
argument_list|()
decl_stmt|;
if|if
condition|(
name|message
operator|==
literal|null
condition|)
block|{
break|break;
block|}
name|long
name|producerSequenceId
init|=
operator|new
name|MessageId
argument_list|(
name|message
operator|.
name|getJMSMessageID
argument_list|()
argument_list|)
operator|.
name|getProducerSequenceId
argument_list|()
decl_stmt|;
name|assertEquals
argument_list|(
name|id
operator|+
literal|" expected order: runCount: "
operator|+
name|runCount
operator|+
literal|" id: "
operator|+
name|message
operator|.
name|getJMSMessageID
argument_list|()
argument_list|,
operator|++
name|nextId
argument_list|,
name|producerSequenceId
argument_list|)
expr_stmt|;
block|}
name|LOG
operator|.
name|info
argument_list|(
name|con
operator|.
name|getClientID
argument_list|()
operator|+
literal|" peeked "
operator|+
name|i
argument_list|)
expr_stmt|;
name|session
operator|.
name|close
argument_list|()
expr_stmt|;
name|con
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
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
name|exceptions
operator|.
name|add
argument_list|(
name|e
argument_list|)
expr_stmt|;
block|}
block|}
block|}
name|Runnable
name|producer
init|=
operator|new
name|Runnable
argument_list|()
block|{
specifier|final
name|String
name|payLoad
init|=
operator|new
name|String
argument_list|(
operator|new
name|byte
index|[
literal|600
index|]
argument_list|)
decl_stmt|;
annotation|@
name|Override
specifier|public
name|void
name|run
parameter_list|()
block|{
try|try
block|{
name|Connection
name|con
init|=
name|createConnection
argument_list|()
decl_stmt|;
specifier|final
name|Session
name|sendSession
init|=
name|con
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
name|MessageProducer
name|producer
init|=
name|sendSession
operator|.
name|createProducer
argument_list|(
name|topic
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
name|messageCount
condition|;
name|i
operator|++
control|)
block|{
name|producer
operator|.
name|send
argument_list|(
name|sendSession
operator|.
name|createTextMessage
argument_list|(
name|payLoad
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|LOG
operator|.
name|info
argument_list|(
literal|"About to commit: "
operator|+
name|messageCount
argument_list|)
expr_stmt|;
name|sendSession
operator|.
name|commit
argument_list|()
expr_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"committed: "
operator|+
name|messageCount
argument_list|)
expr_stmt|;
name|con
operator|.
name|close
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
name|exceptions
operator|.
name|add
argument_list|(
name|e
argument_list|)
expr_stmt|;
block|}
block|}
block|}
decl_stmt|;
name|ExecutorService
name|executorService
init|=
name|Executors
operator|.
name|newCachedThreadPool
argument_list|()
decl_stmt|;
comment|// concurrent commit and activate
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|numConsumers
condition|;
name|i
operator|++
control|)
block|{
specifier|final
name|CheckOrderClient
name|client
init|=
operator|new
name|CheckOrderClient
argument_list|(
name|i
argument_list|)
decl_stmt|;
for|for
control|(
name|int
name|j
init|=
literal|0
init|;
name|j
operator|<
literal|100
condition|;
name|j
operator|++
control|)
block|{
name|executorService
operator|.
name|execute
argument_list|(
name|client
argument_list|)
expr_stmt|;
block|}
block|}
name|executorService
operator|.
name|execute
argument_list|(
name|producer
argument_list|)
expr_stmt|;
name|executorService
operator|.
name|shutdown
argument_list|()
expr_stmt|;
name|executorService
operator|.
name|awaitTermination
argument_list|(
literal|5
argument_list|,
name|TimeUnit
operator|.
name|MINUTES
argument_list|)
expr_stmt|;
name|con
operator|.
name|close
argument_list|()
expr_stmt|;
name|assertTrue
argument_list|(
literal|"no exceptions: "
operator|+
name|exceptions
argument_list|,
name|exceptions
operator|.
name|isEmpty
argument_list|()
argument_list|)
expr_stmt|;
block|}
comment|//    public void testUnmatchedSubUnsubscribeDeletesAll() throws Exception {
comment|//        // create offline subs 1
comment|//        Connection con = createConnection("offCli1");
comment|//        Session session = con.createSession(false, Session.AUTO_ACKNOWLEDGE);
comment|//        session.createDurableSubscriber(topic, "SubsId", "filter = 'true'", true);
comment|//        session.close();
comment|//        con.close();
comment|//
comment|//        // send messages
comment|//        con = createConnection();
comment|//        session = con.createSession(false, Session.AUTO_ACKNOWLEDGE);
comment|//        MessageProducer producer = session.createProducer(null);
comment|//
comment|//        int filtered = 0;
comment|//        for (int i = 0; i< 10; i++) {
comment|//            boolean filter = (i %2 == 0); //(int) (Math.random() * 2)>= 1;
comment|//            if (filter)
comment|//                filtered++;
comment|//
comment|//            Message message = session.createMessage();
comment|//            message.setStringProperty("filter", filter ? "true" : "false");
comment|//            producer.send(topic, message);
comment|//        }
comment|//
comment|//        LOG.info("sent: " + filtered);
comment|//        Thread.sleep(1 * 1000);
comment|//        session.close();
comment|//        con.close();
comment|//
comment|//        // test offline subs
comment|//        con = createConnection("offCli1");
comment|//        session = con.createSession(false, Session.AUTO_ACKNOWLEDGE);
comment|//        session.unsubscribe("SubsId");
comment|//        session.close();
comment|//        con.close();
comment|//
comment|//        con = createConnection("offCli1");
comment|//        session = con.createSession(false, Session.AUTO_ACKNOWLEDGE);
comment|//        MessageConsumer consumer = session.createDurableSubscriber(topic, "SubsId", "filter = 'true'", true);
comment|//        Listener listener = new Listener();
comment|//        consumer.setMessageListener(listener);
comment|//
comment|//        Thread.sleep(3 * 1000);
comment|//
comment|//        session.close();
comment|//        con.close();
comment|//
comment|//        assertEquals(0, listener.count);
comment|//    }
comment|//
comment|//    public void testAllConsumed() throws Exception {
comment|//        final String filter = "filter = 'true'";
comment|//        Connection con = createConnection("cli1");
comment|//        Session session = con.createSession(false, Session.AUTO_ACKNOWLEDGE);
comment|//        session.createDurableSubscriber(topic, "SubsId", filter, true);
comment|//        session.close();
comment|//        con.close();
comment|//
comment|//        con = createConnection("cli2");
comment|//        session = con.createSession(false, Session.AUTO_ACKNOWLEDGE);
comment|//        session.createDurableSubscriber(topic, "SubsId", filter, true);
comment|//        session.close();
comment|//        con.close();
comment|//
comment|//        // send messages
comment|//        con = createConnection();
comment|//        session = con.createSession(false, Session.AUTO_ACKNOWLEDGE);
comment|//        MessageProducer producer = session.createProducer(null);
comment|//
comment|//        int sent = 0;
comment|//        for (int i = 0; i< 10; i++) {
comment|//            Message message = session.createMessage();
comment|//            message.setStringProperty("filter", "true");
comment|//            producer.send(topic, message);
comment|//            sent++;
comment|//        }
comment|//
comment|//        LOG.info("sent: " + sent);
comment|//        Thread.sleep(1 * 1000);
comment|//        session.close();
comment|//        con.close();
comment|//
comment|//        con = createConnection("cli1");
comment|//        session = con.createSession(false, Session.AUTO_ACKNOWLEDGE);
comment|//        MessageConsumer consumer = session.createDurableSubscriber(topic, "SubsId", filter, true);
comment|//        Listener listener = new Listener();
comment|//        consumer.setMessageListener(listener);
comment|//        Thread.sleep(3 * 1000);
comment|//        session.close();
comment|//        con.close();
comment|//
comment|//        assertEquals(sent, listener.count);
comment|//
comment|//        LOG.info("cli2 pull 2");
comment|//        con = createConnection("cli2");
comment|//        session = con.createSession(false, Session.AUTO_ACKNOWLEDGE);
comment|//        consumer = session.createDurableSubscriber(topic, "SubsId", filter, true);
comment|//        assertNotNull("got message", consumer.receive(2000));
comment|//        assertNotNull("got message", consumer.receive(2000));
comment|//        session.close();
comment|//        con.close();
comment|//
comment|//        // send messages
comment|//        con = createConnection();
comment|//        session = con.createSession(false, Session.AUTO_ACKNOWLEDGE);
comment|//        producer = session.createProducer(null);
comment|//
comment|//        sent = 0;
comment|//        for (int i = 0; i< 2; i++) {
comment|//            Message message = session.createMessage();
comment|//            message.setStringProperty("filter", i==1 ? "true" : "false");
comment|//            producer.send(topic, message);
comment|//            sent++;
comment|//        }
comment|//        LOG.info("sent: " + sent);
comment|//        Thread.sleep(1 * 1000);
comment|//        session.close();
comment|//        con.close();
comment|//
comment|//        LOG.info("cli1 again, should get 1 new ones");
comment|//        con = createConnection("cli1");
comment|//        session = con.createSession(false, Session.AUTO_ACKNOWLEDGE);
comment|//        consumer = session.createDurableSubscriber(topic, "SubsId", filter, true);
comment|//        listener = new Listener();
comment|//        consumer.setMessageListener(listener);
comment|//        Thread.sleep(3 * 1000);
comment|//        session.close();
comment|//        con.close();
comment|//
comment|//        assertEquals(1, listener.count);
comment|//    }
comment|//
comment|//    // https://issues.apache.org/jira/browse/AMQ-3190
comment|//    public void testNoMissOnMatchingSubAfterRestart() throws Exception {
comment|//
comment|//        final String filter = "filter = 'true'";
comment|//        Connection con = createConnection("cli1");
comment|//        Session session = con.createSession(false, Session.AUTO_ACKNOWLEDGE);
comment|//        session.createDurableSubscriber(topic, "SubsId", filter, true);
comment|//        session.close();
comment|//        con.close();
comment|//
comment|//        // send unmatched messages
comment|//        con = createConnection();
comment|//        session = con.createSession(false, Session.AUTO_ACKNOWLEDGE);
comment|//        MessageProducer producer = session.createProducer(null);
comment|//
comment|//        int sent = 0;
comment|//        // message for cli1 to keep it interested
comment|//        Message message = session.createMessage();
comment|//        message.setStringProperty("filter", "true");
comment|//        message.setIntProperty("ID", 0);
comment|//        producer.send(topic, message);
comment|//        sent++;
comment|//
comment|//        for (int i = sent; i< 10; i++) {
comment|//            message = session.createMessage();
comment|//            message.setStringProperty("filter", "false");
comment|//            message.setIntProperty("ID", i);
comment|//            producer.send(topic, message);
comment|//            sent++;
comment|//        }
comment|//        con.close();
comment|//        LOG.info("sent: " + sent);
comment|//
comment|//        // new sub at id 10
comment|//        con = createConnection("cli2");
comment|//        session = con.createSession(false, Session.AUTO_ACKNOWLEDGE);
comment|//        session.createDurableSubscriber(topic, "SubsId", filter, true);
comment|//        session.close();
comment|//        con.close();
comment|//
comment|//        destroyBroker();
comment|//        createBroker(false);
comment|//
comment|//        con = createConnection();
comment|//        session = con.createSession(false, Session.AUTO_ACKNOWLEDGE);
comment|//        producer = session.createProducer(null);
comment|//
comment|//        for (int i = sent; i< 30; i++) {
comment|//            message = session.createMessage();
comment|//            message.setStringProperty("filter", "true");
comment|//            message.setIntProperty("ID", i);
comment|//            producer.send(topic, message);
comment|//            sent++;
comment|//        }
comment|//        con.close();
comment|//        LOG.info("sent: " + sent);
comment|//
comment|//        // pick up the first of the next twenty messages
comment|//        con = createConnection("cli2");
comment|//        session = con.createSession(false, Session.AUTO_ACKNOWLEDGE);
comment|//        MessageConsumer consumer = session.createDurableSubscriber(topic, "SubsId", filter, true);
comment|//        Message m = consumer.receive(3000);
comment|//        assertEquals("is message 10", 10, m.getIntProperty("ID"));
comment|//
comment|//        session.close();
comment|//        con.close();
comment|//
comment|//        // pick up the first few messages for client1
comment|//        con = createConnection("cli1");
comment|//        session = con.createSession(false, Session.AUTO_ACKNOWLEDGE);
comment|//        consumer = session.createDurableSubscriber(topic, "SubsId", filter, true);
comment|//        m = consumer.receive(3000);
comment|//        assertEquals("is message 0", 0, m.getIntProperty("ID"));
comment|//        m = consumer.receive(3000);
comment|//        assertEquals("is message 10", 10, m.getIntProperty("ID"));
comment|//
comment|//        session.close();
comment|//        con.close();
comment|//    }
comment|// use very small journal to get lots of files to cleanup
specifier|public
name|void
name|initCombosForTestCleanupDeletedSubAfterRestart
parameter_list|()
throws|throws
name|Exception
block|{
name|this
operator|.
name|addCombinationValues
argument_list|(
literal|"journalMaxFileLength"
argument_list|,
operator|new
name|Object
index|[]
block|{
operator|new
name|Integer
argument_list|(
literal|64
operator|*
literal|1024
argument_list|)
block|}
argument_list|)
expr_stmt|;
name|this
operator|.
name|addCombinationValues
argument_list|(
literal|"keepDurableSubsActive"
argument_list|,
operator|new
name|Object
index|[]
block|{
name|Boolean
operator|.
name|TRUE
block|,
name|Boolean
operator|.
name|FALSE
block|}
argument_list|)
expr_stmt|;
block|}
comment|// https://issues.apache.org/jira/browse/AMQ-3206
specifier|public
name|void
name|testCleanupDeletedSubAfterRestart
parameter_list|()
throws|throws
name|Exception
block|{
name|Connection
name|con
init|=
name|createConnection
argument_list|(
literal|"cli1"
argument_list|)
decl_stmt|;
name|Session
name|session
init|=
name|con
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
name|session
operator|.
name|createDurableSubscriber
argument_list|(
name|topic
argument_list|,
literal|"SubsId"
argument_list|,
literal|null
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|session
operator|.
name|close
argument_list|()
expr_stmt|;
name|con
operator|.
name|close
argument_list|()
expr_stmt|;
name|con
operator|=
name|createConnection
argument_list|(
literal|"cli2"
argument_list|)
expr_stmt|;
name|session
operator|=
name|con
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
name|session
operator|.
name|createDurableSubscriber
argument_list|(
name|topic
argument_list|,
literal|"SubsId"
argument_list|,
literal|null
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|session
operator|.
name|close
argument_list|()
expr_stmt|;
name|con
operator|.
name|close
argument_list|()
expr_stmt|;
name|con
operator|=
name|createConnection
argument_list|()
expr_stmt|;
name|session
operator|=
name|con
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
name|MessageProducer
name|producer
init|=
name|session
operator|.
name|createProducer
argument_list|(
literal|null
argument_list|)
decl_stmt|;
specifier|final
name|int
name|toSend
init|=
literal|500
decl_stmt|;
specifier|final
name|String
name|payload
init|=
operator|new
name|byte
index|[
literal|40
operator|*
literal|1024
index|]
operator|.
name|toString
argument_list|()
decl_stmt|;
name|int
name|sent
init|=
literal|0
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
name|sent
init|;
name|i
operator|<
name|toSend
condition|;
name|i
operator|++
control|)
block|{
name|Message
name|message
init|=
name|session
operator|.
name|createTextMessage
argument_list|(
name|payload
argument_list|)
decl_stmt|;
name|message
operator|.
name|setStringProperty
argument_list|(
literal|"filter"
argument_list|,
literal|"false"
argument_list|)
expr_stmt|;
name|message
operator|.
name|setIntProperty
argument_list|(
literal|"ID"
argument_list|,
name|i
argument_list|)
expr_stmt|;
name|producer
operator|.
name|send
argument_list|(
name|topic
argument_list|,
name|message
argument_list|)
expr_stmt|;
name|sent
operator|++
expr_stmt|;
block|}
name|con
operator|.
name|close
argument_list|()
expr_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"sent: "
operator|+
name|sent
argument_list|)
expr_stmt|;
comment|// kill off cli1
name|con
operator|=
name|createConnection
argument_list|(
literal|"cli1"
argument_list|)
expr_stmt|;
name|session
operator|=
name|con
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
name|session
operator|.
name|unsubscribe
argument_list|(
literal|"SubsId"
argument_list|)
expr_stmt|;
name|destroyBroker
argument_list|()
expr_stmt|;
name|createBroker
argument_list|(
literal|false
argument_list|)
expr_stmt|;
name|con
operator|=
name|createConnection
argument_list|(
literal|"cli2"
argument_list|)
expr_stmt|;
name|session
operator|=
name|con
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
name|MessageConsumer
name|consumer
init|=
name|session
operator|.
name|createDurableSubscriber
argument_list|(
name|topic
argument_list|,
literal|"SubsId"
argument_list|,
literal|null
argument_list|,
literal|true
argument_list|)
decl_stmt|;
specifier|final
name|Listener
name|listener
init|=
operator|new
name|Listener
argument_list|()
decl_stmt|;
name|consumer
operator|.
name|setMessageListener
argument_list|(
name|listener
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
literal|"got all sent"
argument_list|,
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
name|LOG
operator|.
name|info
argument_list|(
literal|"Want: "
operator|+
name|toSend
operator|+
literal|", current: "
operator|+
name|listener
operator|.
name|count
argument_list|)
expr_stmt|;
return|return
name|listener
operator|.
name|count
operator|==
name|toSend
return|;
block|}
block|}
argument_list|)
argument_list|)
expr_stmt|;
name|session
operator|.
name|close
argument_list|()
expr_stmt|;
name|con
operator|.
name|close
argument_list|()
expr_stmt|;
name|destroyBroker
argument_list|()
expr_stmt|;
name|createBroker
argument_list|(
literal|false
argument_list|)
expr_stmt|;
specifier|final
name|KahaDBPersistenceAdapter
name|pa
init|=
operator|(
name|KahaDBPersistenceAdapter
operator|)
name|broker
operator|.
name|getPersistenceAdapter
argument_list|()
decl_stmt|;
name|assertTrue
argument_list|(
literal|"Should have less than three journal files left but was: "
operator|+
name|pa
operator|.
name|getStore
argument_list|()
operator|.
name|getJournal
argument_list|()
operator|.
name|getFileMap
argument_list|()
operator|.
name|size
argument_list|()
argument_list|,
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
name|pa
operator|.
name|getStore
argument_list|()
operator|.
name|getJournal
argument_list|()
operator|.
name|getFileMap
argument_list|()
operator|.
name|size
argument_list|()
operator|<=
literal|3
return|;
block|}
block|}
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|//    // https://issues.apache.org/jira/browse/AMQ-3768
comment|//    public void testPageReuse() throws Exception {
comment|//        Connection con = null;
comment|//        Session session = null;
comment|//
comment|//        final int numConsumers = 115;
comment|//        for (int i=0; i<=numConsumers;i++) {
comment|//            con = createConnection("cli" + i);
comment|//            session = con.createSession(false, Session.AUTO_ACKNOWLEDGE);
comment|//            session.createDurableSubscriber(topic, "SubsId", null, true);
comment|//            session.close();
comment|//            con.close();
comment|//        }
comment|//
comment|//        // populate ack locations
comment|//        con = createConnection();
comment|//        session = con.createSession(false, Session.AUTO_ACKNOWLEDGE);
comment|//        MessageProducer producer = session.createProducer(null);
comment|//        Message message = session.createTextMessage(new byte[10].toString());
comment|//        producer.send(topic, message);
comment|//        con.close();
comment|//
comment|//        // we have a split, remove all but the last so that
comment|//        // the head pageid changes in the acklocations listindex
comment|//        for (int i=0; i<=numConsumers -1; i++) {
comment|//            con = createConnection("cli" + i);
comment|//            session = con.createSession(false, Session.AUTO_ACKNOWLEDGE);
comment|//            session.unsubscribe("SubsId");
comment|//            session.close();
comment|//            con.close();
comment|//        }
comment|//
comment|//        destroyBroker();
comment|//        createBroker(false);
comment|//
comment|//        // create a bunch more subs to reuse the freed page and get us in a knot
comment|//        for (int i=1; i<=numConsumers;i++) {
comment|//            con = createConnection("cli" + i);
comment|//            session = con.createSession(false, Session.AUTO_ACKNOWLEDGE);
comment|//            session.createDurableSubscriber(topic, "SubsId", filter, true);
comment|//            session.close();
comment|//            con.close();
comment|//        }
comment|//    }
comment|//
comment|//    public void testRedeliveryFlag() throws Exception {
comment|//
comment|//        Connection con;
comment|//        Session session;
comment|//        final int numClients = 2;
comment|//        for (int i=0; i<numClients; i++) {
comment|//            con = createConnection("cliId" + i);
comment|//            session = con.createSession(false, Session.CLIENT_ACKNOWLEDGE);
comment|//            session.createDurableSubscriber(topic, "SubsId", "filter = 'true'", true);
comment|//            session.close();
comment|//            con.close();
comment|//        }
comment|//
comment|//        final Random random = new Random();
comment|//
comment|//        // send messages
comment|//        con = createConnection();
comment|//        session = con.createSession(false, Session.AUTO_ACKNOWLEDGE);
comment|//        MessageProducer producer = session.createProducer(null);
comment|//
comment|//        final int count = 1000;
comment|//        for (int i = 0; i< count; i++) {
comment|//            Message message = session.createMessage();
comment|//            message.setStringProperty("filter", "true");
comment|//            producer.send(topic, message);
comment|//        }
comment|//        session.close();
comment|//        con.close();
comment|//
comment|//        class Client implements Runnable {
comment|//            Connection con;
comment|//            Session session;
comment|//            String clientId;
comment|//            Client(String id) {
comment|//                this.clientId = id;
comment|//            }
comment|//
comment|//            @Override
comment|//            public void run() {
comment|//                MessageConsumer consumer = null;
comment|//                Message message = null;
comment|//
comment|//                try {
comment|//                    for (int i = -1; i< random.nextInt(10); i++) {
comment|//                        // go online and take none
comment|//                        con = createConnection(clientId);
comment|//                        session = con.createSession(false, Session.CLIENT_ACKNOWLEDGE);
comment|//                        consumer = session.createDurableSubscriber(topic, "SubsId", "filter = 'true'", true);
comment|//                        session.close();
comment|//                        con.close();
comment|//                    }
comment|//
comment|//                    // consume 1
comment|//                    con = createConnection(clientId);
comment|//                    session = con.createSession(false, Session.CLIENT_ACKNOWLEDGE);
comment|//                    consumer = session.createDurableSubscriber(topic, "SubsId", "filter = 'true'", true);
comment|//                    message = consumer.receive(4000);
comment|//                    assertNotNull("got message", message);
comment|//                    // it is not reliable as it depends on broker dispatch rather than client receipt
comment|//                    // and delivered ack
comment|//                    //  assertFalse("not redelivered", message.getJMSRedelivered());
comment|//                    message.acknowledge();
comment|//                    session.close();
comment|//                    con.close();
comment|//
comment|//                    // peek all
comment|//                    for (int j = -1; j< random.nextInt(10); j++) {
comment|//                        con = createConnection(clientId);
comment|//                        session = con.createSession(false, Session.CLIENT_ACKNOWLEDGE);
comment|//                        consumer = session.createDurableSubscriber(topic, "SubsId", "filter = 'true'", true);
comment|//
comment|//                        for (int i = 0; i< count - 1; i++) {
comment|//                            assertNotNull("got message", consumer.receive(4000));
comment|//                        }
comment|//                        // no ack
comment|//                        session.close();
comment|//                        con.close();
comment|//                    }
comment|//
comment|//                    // consume remaining
comment|//                    con = createConnection(clientId);
comment|//                    session = con.createSession(false, Session.CLIENT_ACKNOWLEDGE);
comment|//                    consumer = session.createDurableSubscriber(topic, "SubsId", "filter = 'true'", true);
comment|//
comment|//                    for (int i = 0; i< count - 1; i++) {
comment|//                        message = consumer.receive(4000);
comment|//                        assertNotNull("got message", message);
comment|//                        assertTrue("is redelivered", message.getJMSRedelivered());
comment|//                    }
comment|//                    message.acknowledge();
comment|//                    session.close();
comment|//                    con.close();
comment|//
comment|//                    con = createConnection(clientId);
comment|//                    session = con.createSession(false, Session.CLIENT_ACKNOWLEDGE);
comment|//                    consumer = session.createDurableSubscriber(topic, "SubsId", "filter = 'true'", true);
comment|//                    assertNull("no message left", consumer.receive(2000));
comment|//                } catch (Throwable throwable) {
comment|//                    throwable.printStackTrace();
comment|//                    exceptions.add(throwable);
comment|//                }
comment|//            }
comment|//        }
comment|//        ExecutorService executorService = Executors.newCachedThreadPool();
comment|//        for (int i=0; i<numClients; i++) {
comment|//            executorService.execute(new Client("cliId" + i));
comment|//        }
comment|//        executorService.shutdown();
comment|//        executorService.awaitTermination(10, TimeUnit.MINUTES);
comment|//        assertTrue("No exceptions expected, but was: " + exceptions, exceptions.isEmpty());
comment|//    }
specifier|public
specifier|static
class|class
name|Listener
implements|implements
name|MessageListener
block|{
name|int
name|count
init|=
literal|0
decl_stmt|;
name|String
name|id
init|=
literal|null
decl_stmt|;
name|Listener
parameter_list|()
block|{         }
name|Listener
parameter_list|(
name|String
name|id
parameter_list|)
block|{
name|this
operator|.
name|id
operator|=
name|id
expr_stmt|;
block|}
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
name|count
operator|++
expr_stmt|;
if|if
condition|(
name|id
operator|!=
literal|null
condition|)
block|{
try|try
block|{
name|LOG
operator|.
name|info
argument_list|(
name|id
operator|+
literal|", "
operator|+
name|message
operator|.
name|getJMSMessageID
argument_list|()
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|ignored
parameter_list|)
block|{}
block|}
block|}
block|}
specifier|public
class|class
name|FilterCheckListener
extends|extends
name|Listener
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
name|count
operator|++
expr_stmt|;
try|try
block|{
name|Object
name|b
init|=
name|message
operator|.
name|getObjectProperty
argument_list|(
literal|"$b"
argument_list|)
decl_stmt|;
if|if
condition|(
name|b
operator|!=
literal|null
condition|)
block|{
name|boolean
name|c
init|=
name|message
operator|.
name|getBooleanProperty
argument_list|(
literal|"$c"
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
literal|""
argument_list|,
name|c
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|String
name|d
init|=
name|message
operator|.
name|getStringProperty
argument_list|(
literal|"$d"
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
literal|""
argument_list|,
literal|"D1"
operator|.
name|equals
argument_list|(
name|d
argument_list|)
operator|||
literal|"D2"
operator|.
name|equals
argument_list|(
name|d
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
catch|catch
parameter_list|(
name|JMSException
name|e
parameter_list|)
block|{
name|e
operator|.
name|printStackTrace
argument_list|()
expr_stmt|;
name|exceptions
operator|.
name|add
argument_list|(
name|e
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|}
end_class

end_unit

