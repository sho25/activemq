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
name|broker
operator|.
name|jmx
package|;
end_package

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|BufferedReader
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|InputStreamReader
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
name|net
operator|.
name|URL
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|HashMap
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Map
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|jms
operator|.
name|BytesMessage
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
name|javax
operator|.
name|management
operator|.
name|MBeanServer
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|management
operator|.
name|MBeanServerInvocationHandler
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|management
operator|.
name|MalformedObjectNameException
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|management
operator|.
name|ObjectName
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|management
operator|.
name|openmbean
operator|.
name|CompositeData
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|management
operator|.
name|openmbean
operator|.
name|TabularData
import|;
end_import

begin_import
import|import
name|junit
operator|.
name|textui
operator|.
name|TestRunner
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
name|ActiveMQSession
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
name|BlobMessage
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
name|EmbeddedBrokerTestSupport
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
name|BaseDestination
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
name|broker
operator|.
name|region
operator|.
name|policy
operator|.
name|SharedDeadLetterStrategy
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
name|ActiveMQBlobMessage
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
name|ActiveMQDestination
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
name|command
operator|.
name|ActiveMQTempQueue
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
name|JMXSupport
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
comment|/**  * A test case of the various MBeans in ActiveMQ. If you want to look at the  * various MBeans after the test has been run then run this test case as a  * command line application.  *  *  */
end_comment

begin_class
specifier|public
class|class
name|MBeanTest
extends|extends
name|EmbeddedBrokerTestSupport
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
name|MBeanTest
operator|.
name|class
argument_list|)
decl_stmt|;
specifier|private
specifier|static
name|boolean
name|waitForKeyPress
decl_stmt|;
specifier|protected
name|MBeanServer
name|mbeanServer
decl_stmt|;
specifier|protected
name|String
name|domain
init|=
literal|"org.apache.activemq"
decl_stmt|;
specifier|protected
name|String
name|clientID
init|=
literal|"foo"
decl_stmt|;
specifier|protected
name|Connection
name|connection
decl_stmt|;
specifier|protected
name|boolean
name|transacted
decl_stmt|;
specifier|protected
name|int
name|authMode
init|=
name|Session
operator|.
name|AUTO_ACKNOWLEDGE
decl_stmt|;
specifier|protected
specifier|static
specifier|final
name|int
name|MESSAGE_COUNT
init|=
literal|2
operator|*
name|BaseDestination
operator|.
name|MAX_PAGE_SIZE
decl_stmt|;
comment|/**      * When you run this test case from the command line it will pause before      * terminating so that you can look at the MBeans state for debugging      * purposes.      */
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
name|waitForKeyPress
operator|=
literal|true
expr_stmt|;
name|TestRunner
operator|.
name|run
argument_list|(
name|MBeanTest
operator|.
name|class
argument_list|)
expr_stmt|;
block|}
comment|//    public void testConnectors() throws Exception{
comment|//        ObjectName brokerName = assertRegisteredObjectName(domain + ":Type=Broker,BrokerName=localhost");
comment|//        BrokerViewMBean broker = (BrokerViewMBean)MBeanServerInvocationHandler.newProxyInstance(mbeanServer, brokerName, BrokerViewMBean.class, true);
comment|//        assertEquals("openwire URL port doesn't equal bind Address", new URI(broker.getOpenWireURL()).getPort(), new URI(this.bindAddress).getPort());
comment|//
comment|//    }
comment|//
comment|//    public void testMBeans() throws Exception {
comment|//        connection = connectionFactory.createConnection();
comment|//        useConnection(connection);
comment|//
comment|//        // test all the various MBeans now we have a producer, consumer and
comment|//        // messages on a queue
comment|//        assertSendViaMBean();
comment|//        assertQueueBrowseWorks();
comment|//        assertCreateAndDestroyDurableSubscriptions();
comment|//        assertConsumerCounts();
comment|//        assertProducerCounts();
comment|//    }
comment|//
comment|//    public void testMoveMessages() throws Exception {
comment|//        connection = connectionFactory.createConnection();
comment|//        useConnection(connection);
comment|//
comment|//        ObjectName queueViewMBeanName = assertRegisteredObjectName(domain + ":Type=Queue,Destination=" + getDestinationString() + ",BrokerName=localhost");
comment|//
comment|//        QueueViewMBean queue = (QueueViewMBean)MBeanServerInvocationHandler.newProxyInstance(mbeanServer, queueViewMBeanName, QueueViewMBean.class, true);
comment|//
comment|//        CompositeData[] compdatalist = queue.browse();
comment|//        int initialQueueSize = compdatalist.length;
comment|//        if (initialQueueSize == 0) {
comment|//            fail("There is no message in the queue:");
comment|//        }
comment|//        else {
comment|//            echo("Current queue size: " + initialQueueSize);
comment|//        }
comment|//        int messageCount = initialQueueSize;
comment|//        String[] messageIDs = new String[messageCount];
comment|//        for (int i = 0; i< messageCount; i++) {
comment|//            CompositeData cdata = compdatalist[i];
comment|//            String messageID = (String) cdata.get("JMSMessageID");
comment|//            assertNotNull("Should have a message ID for message " + i, messageID);
comment|//            messageIDs[i] = messageID;
comment|//        }
comment|//
comment|//        assertTrue("dest has some memory usage", queue.getMemoryPercentUsage()> 0);
comment|//
comment|//        echo("About to move " + messageCount + " messages");
comment|//
comment|//        String newDestination = getSecondDestinationString();
comment|//        for (String messageID : messageIDs) {
comment|//            echo("Moving message: " + messageID);
comment|//            queue.moveMessageTo(messageID, newDestination);
comment|//        }
comment|//
comment|//        echo("Now browsing the queue");
comment|//        compdatalist = queue.browse();
comment|//        int actualCount = compdatalist.length;
comment|//        echo("Current queue size: " + actualCount);
comment|//        assertEquals("Should now have empty queue but was", initialQueueSize - messageCount, actualCount);
comment|//
comment|//        echo("Now browsing the second queue");
comment|//
comment|//        queueViewMBeanName = assertRegisteredObjectName(domain + ":Type=Queue,Destination=" + newDestination + ",BrokerName=localhost");
comment|//        QueueViewMBean queueNew = (QueueViewMBean)MBeanServerInvocationHandler.newProxyInstance(mbeanServer, queueViewMBeanName, QueueViewMBean.class, true);
comment|//
comment|//        long newQueuesize = queueNew.getQueueSize();
comment|//        echo("Second queue size: " + newQueuesize);
comment|//        assertEquals("Unexpected number of messages ",messageCount, newQueuesize);
comment|//
comment|//        // check memory usage migration
comment|//        assertTrue("new dest has some memory usage", queueNew.getMemoryPercentUsage()> 0);
comment|//        assertEquals("old dest has no memory usage", 0, queue.getMemoryPercentUsage());
comment|//        assertTrue("use cache", queueNew.isUseCache());
comment|//        assertTrue("cache enabled", queueNew.isCacheEnabled());
comment|//    }
comment|//
comment|//    public void testRemoveMessages() throws Exception {
comment|//        ObjectName brokerName = assertRegisteredObjectName(domain + ":Type=Broker,BrokerName=localhost");
comment|//        BrokerViewMBean broker = (BrokerViewMBean)MBeanServerInvocationHandler.newProxyInstance(mbeanServer, brokerName, BrokerViewMBean.class, true);
comment|//        broker.addQueue(getDestinationString());
comment|//
comment|//        ObjectName queueViewMBeanName = assertRegisteredObjectName(domain + ":Type=Queue,Destination=" + getDestinationString() + ",BrokerName=localhost");
comment|//
comment|//        QueueViewMBean queue = (QueueViewMBean)MBeanServerInvocationHandler.newProxyInstance(mbeanServer, queueViewMBeanName, QueueViewMBean.class, true);
comment|//        String msg1 = queue.sendTextMessage("message 1");
comment|//        String msg2 = queue.sendTextMessage("message 2");
comment|//
comment|//        assertTrue(queue.removeMessage(msg2));
comment|//
comment|//        connection = connectionFactory.createConnection();
comment|//        connection.start();
comment|//        Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
comment|//        ActiveMQDestination dest = createDestination();
comment|//
comment|//        MessageConsumer consumer = session.createConsumer(dest);
comment|//        Message message = consumer.receive(1000);
comment|//        assertNotNull(message);
comment|//        assertEquals(msg1, message.getJMSMessageID());
comment|//
comment|//        String msg3 = queue.sendTextMessage("message 3");
comment|//        message = consumer.receive(1000);
comment|//        assertNotNull(message);
comment|//        assertEquals(msg3, message.getJMSMessageID());
comment|//
comment|//        message = consumer.receive(1000);
comment|//        assertNull(message);
comment|//
comment|//    }
comment|//
comment|//    public void testRetryMessages() throws Exception {
comment|//        // lets speed up redelivery
comment|//        ActiveMQConnectionFactory factory = (ActiveMQConnectionFactory) connectionFactory;
comment|//        factory.getRedeliveryPolicy().setCollisionAvoidancePercent((short) 0);
comment|//        factory.getRedeliveryPolicy().setMaximumRedeliveries(1);
comment|//        factory.getRedeliveryPolicy().setInitialRedeliveryDelay(0);
comment|//        factory.getRedeliveryPolicy().setUseCollisionAvoidance(false);
comment|//        factory.getRedeliveryPolicy().setUseExponentialBackOff(false);
comment|//        factory.getRedeliveryPolicy().setBackOffMultiplier((short) 0);
comment|//
comment|//        connection = connectionFactory.createConnection();
comment|//        useConnection(connection);
comment|//
comment|//
comment|//        ObjectName queueViewMBeanName = assertRegisteredObjectName(domain + ":Type=Queue,Destination=" + getDestinationString() + ",BrokerName=localhost");
comment|//        QueueViewMBean queue = (QueueViewMBean)MBeanServerInvocationHandler.newProxyInstance(mbeanServer, queueViewMBeanName, QueueViewMBean.class, true);
comment|//
comment|//        long initialQueueSize = queue.getQueueSize();
comment|//        echo("current queue size: " + initialQueueSize);
comment|//        assertTrue("dest has some memory usage", queue.getMemoryPercentUsage()> 0);
comment|//
comment|//        // lets create a duff consumer which keeps rolling back...
comment|//        Session session = connection.createSession(true, Session.SESSION_TRANSACTED);
comment|//        MessageConsumer consumer = session.createConsumer(new ActiveMQQueue(getDestinationString()));
comment|//        Message message = consumer.receive(5000);
comment|//        while (message != null) {
comment|//            echo("Message: " + message.getJMSMessageID() + " redelivered " + message.getJMSRedelivered() + " counter " + message.getObjectProperty("JMSXDeliveryCount"));
comment|//            session.rollback();
comment|//            message = consumer.receive(2000);
comment|//        }
comment|//        consumer.close();
comment|//        session.close();
comment|//
comment|//
comment|//        // now lets get the dead letter queue
comment|//        Thread.sleep(1000);
comment|//
comment|//        ObjectName dlqQueueViewMBeanName = assertRegisteredObjectName(domain + ":Type=Queue,Destination=" + SharedDeadLetterStrategy.DEFAULT_DEAD_LETTER_QUEUE_NAME + ",BrokerName=localhost");
comment|//        QueueViewMBean dlq = (QueueViewMBean)MBeanServerInvocationHandler.newProxyInstance(mbeanServer, dlqQueueViewMBeanName, QueueViewMBean.class, true);
comment|//
comment|//        long initialDlqSize = dlq.getQueueSize();
comment|//        CompositeData[] compdatalist = dlq.browse();
comment|//        int dlqQueueSize = compdatalist.length;
comment|//        if (dlqQueueSize == 0) {
comment|//            fail("There are no messages in the queue:");
comment|//        }
comment|//        else {
comment|//            echo("Current DLQ queue size: " + dlqQueueSize);
comment|//        }
comment|//        int messageCount = dlqQueueSize;
comment|//        String[] messageIDs = new String[messageCount];
comment|//        for (int i = 0; i< messageCount; i++) {
comment|//            CompositeData cdata = compdatalist[i];
comment|//            String messageID = (String) cdata.get("JMSMessageID");
comment|//            assertNotNull("Should have a message ID for message " + i, messageID);
comment|//            messageIDs[i] = messageID;
comment|//        }
comment|//
comment|//        int dlqMemUsage = dlq.getMemoryPercentUsage();
comment|//        assertTrue("dlq has some memory usage", dlqMemUsage> 0);
comment|//        assertEquals("dest has no memory usage", 0, queue.getMemoryPercentUsage());
comment|//
comment|//
comment|//        echo("About to retry " + messageCount + " messages");
comment|//
comment|//        for (String messageID : messageIDs) {
comment|//            echo("Retrying message: " + messageID);
comment|//            dlq.retryMessage(messageID);
comment|//        }
comment|//
comment|//        long queueSize = queue.getQueueSize();
comment|//        compdatalist = queue.browse();
comment|//        int actualCount = compdatalist.length;
comment|//        echo("Orginal queue size is now " + queueSize);
comment|//        echo("Original browse queue size: " + actualCount);
comment|//
comment|//        long dlqSize = dlq.getQueueSize();
comment|//        echo("DLQ size: " + dlqSize);
comment|//
comment|//        assertEquals("DLQ size", initialDlqSize - messageCount, dlqSize);
comment|//        assertEquals("queue size", initialQueueSize, queueSize);
comment|//        assertEquals("browse queue size", initialQueueSize, actualCount);
comment|//
comment|//        assertEquals("dest has some memory usage", dlqMemUsage, queue.getMemoryPercentUsage());
comment|//    }
comment|//
comment|//    public void testMoveMessagesBySelector() throws Exception {
comment|//        connection = connectionFactory.createConnection();
comment|//        useConnection(connection);
comment|//
comment|//        ObjectName queueViewMBeanName = assertRegisteredObjectName(domain + ":Type=Queue,Destination=" + getDestinationString() + ",BrokerName=localhost");
comment|//
comment|//        QueueViewMBean queue = (QueueViewMBean)MBeanServerInvocationHandler.newProxyInstance(mbeanServer, queueViewMBeanName, QueueViewMBean.class, true);
comment|//
comment|//        String newDestination = getSecondDestinationString();
comment|//        queue.moveMatchingMessagesTo("counter> 2", newDestination);
comment|//
comment|//        queueViewMBeanName = assertRegisteredObjectName(domain + ":Type=Queue,Destination=" + newDestination + ",BrokerName=localhost");
comment|//
comment|//        queue = (QueueViewMBean)MBeanServerInvocationHandler.newProxyInstance(mbeanServer, queueViewMBeanName, QueueViewMBean.class, true);
comment|//        int movedSize = MESSAGE_COUNT-3;
comment|//        assertEquals("Unexpected number of messages ",movedSize,queue.getQueueSize());
comment|//
comment|//        // now lets remove them by selector
comment|//        queue.removeMatchingMessages("counter> 2");
comment|//
comment|//        assertEquals("Should have no more messages in the queue: " + queueViewMBeanName, 0, queue.getQueueSize());
comment|//        assertEquals("dest has no memory usage", 0, queue.getMemoryPercentUsage());
comment|//    }
comment|//
comment|//    public void testCopyMessagesBySelector() throws Exception {
comment|//        connection = connectionFactory.createConnection();
comment|//        useConnection(connection);
comment|//
comment|//        ObjectName queueViewMBeanName = assertRegisteredObjectName(domain + ":Type=Queue,Destination=" + getDestinationString() + ",BrokerName=localhost");
comment|//
comment|//        QueueViewMBean queue = (QueueViewMBean)MBeanServerInvocationHandler.newProxyInstance(mbeanServer, queueViewMBeanName, QueueViewMBean.class, true);
comment|//
comment|//        String newDestination = getSecondDestinationString();
comment|//        long queueSize = queue.getQueueSize();
comment|//        queue.copyMatchingMessagesTo("counter> 2", newDestination);
comment|//
comment|//
comment|//
comment|//        queueViewMBeanName = assertRegisteredObjectName(domain + ":Type=Queue,Destination=" + newDestination + ",BrokerName=localhost");
comment|//
comment|//        queue = (QueueViewMBean)MBeanServerInvocationHandler.newProxyInstance(mbeanServer, queueViewMBeanName, QueueViewMBean.class, true);
comment|//
comment|//        LOG.info("Queue: " + queueViewMBeanName + " now has: " + queue.getQueueSize() + " message(s)");
comment|//        assertEquals("Expected messages in a queue: " + queueViewMBeanName, MESSAGE_COUNT-3, queue.getQueueSize());
comment|//        // now lets remove them by selector
comment|//        queue.removeMatchingMessages("counter> 2");
comment|//
comment|//        assertEquals("Should have no more messages in the queue: " + queueViewMBeanName, 0, queue.getQueueSize());
comment|//        assertEquals("dest has no memory usage", 0, queue.getMemoryPercentUsage());
comment|//    }
specifier|protected
name|void
name|assertSendViaMBean
parameter_list|()
throws|throws
name|Exception
block|{
name|String
name|queueName
init|=
name|getDestinationString
argument_list|()
operator|+
literal|".SendMBBean"
decl_stmt|;
name|ObjectName
name|brokerName
init|=
name|assertRegisteredObjectName
argument_list|(
name|domain
operator|+
literal|":Type=Broker,BrokerName=localhost"
argument_list|)
decl_stmt|;
name|echo
argument_list|(
literal|"Create QueueView MBean..."
argument_list|)
expr_stmt|;
name|BrokerViewMBean
name|broker
init|=
operator|(
name|BrokerViewMBean
operator|)
name|MBeanServerInvocationHandler
operator|.
name|newProxyInstance
argument_list|(
name|mbeanServer
argument_list|,
name|brokerName
argument_list|,
name|BrokerViewMBean
operator|.
name|class
argument_list|,
literal|true
argument_list|)
decl_stmt|;
name|broker
operator|.
name|addQueue
argument_list|(
name|queueName
argument_list|)
expr_stmt|;
name|ObjectName
name|queueViewMBeanName
init|=
name|assertRegisteredObjectName
argument_list|(
name|domain
operator|+
literal|":Type=Queue,Destination="
operator|+
name|queueName
operator|+
literal|",BrokerName=localhost"
argument_list|)
decl_stmt|;
name|echo
argument_list|(
literal|"Create QueueView MBean..."
argument_list|)
expr_stmt|;
name|QueueViewMBean
name|proxy
init|=
operator|(
name|QueueViewMBean
operator|)
name|MBeanServerInvocationHandler
operator|.
name|newProxyInstance
argument_list|(
name|mbeanServer
argument_list|,
name|queueViewMBeanName
argument_list|,
name|QueueViewMBean
operator|.
name|class
argument_list|,
literal|true
argument_list|)
decl_stmt|;
name|proxy
operator|.
name|purge
argument_list|()
expr_stmt|;
name|int
name|count
init|=
literal|5
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
name|String
name|body
init|=
literal|"message:"
operator|+
name|i
decl_stmt|;
name|Map
name|headers
init|=
operator|new
name|HashMap
argument_list|()
decl_stmt|;
name|headers
operator|.
name|put
argument_list|(
literal|"JMSCorrelationID"
argument_list|,
literal|"MyCorrId"
argument_list|)
expr_stmt|;
name|headers
operator|.
name|put
argument_list|(
literal|"JMSDeliveryMode"
argument_list|,
name|Boolean
operator|.
name|FALSE
argument_list|)
expr_stmt|;
name|headers
operator|.
name|put
argument_list|(
literal|"JMSXGroupID"
argument_list|,
literal|"MyGroupID"
argument_list|)
expr_stmt|;
name|headers
operator|.
name|put
argument_list|(
literal|"JMSXGroupSeq"
argument_list|,
literal|1234
argument_list|)
expr_stmt|;
name|headers
operator|.
name|put
argument_list|(
literal|"JMSPriority"
argument_list|,
name|i
operator|+
literal|1
argument_list|)
expr_stmt|;
name|headers
operator|.
name|put
argument_list|(
literal|"JMSType"
argument_list|,
literal|"MyType"
argument_list|)
expr_stmt|;
name|headers
operator|.
name|put
argument_list|(
literal|"MyHeader"
argument_list|,
name|i
argument_list|)
expr_stmt|;
name|headers
operator|.
name|put
argument_list|(
literal|"MyStringHeader"
argument_list|,
literal|"StringHeader"
operator|+
name|i
argument_list|)
expr_stmt|;
name|proxy
operator|.
name|sendTextMessage
argument_list|(
name|headers
argument_list|,
name|body
argument_list|)
expr_stmt|;
block|}
name|CompositeData
index|[]
name|compdatalist
init|=
name|proxy
operator|.
name|browse
argument_list|()
decl_stmt|;
if|if
condition|(
name|compdatalist
operator|.
name|length
operator|==
literal|0
condition|)
block|{
name|fail
argument_list|(
literal|"There is no message in the queue:"
argument_list|)
expr_stmt|;
block|}
name|String
index|[]
name|messageIDs
init|=
operator|new
name|String
index|[
name|compdatalist
operator|.
name|length
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
name|compdatalist
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|CompositeData
name|cdata
init|=
name|compdatalist
index|[
name|i
index|]
decl_stmt|;
if|if
condition|(
name|i
operator|==
literal|0
condition|)
block|{
name|echo
argument_list|(
literal|"Columns: "
operator|+
name|cdata
operator|.
name|getCompositeType
argument_list|()
operator|.
name|keySet
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|assertComplexData
argument_list|(
name|i
argument_list|,
name|cdata
argument_list|,
literal|"JMSCorrelationID"
argument_list|,
literal|"MyCorrId"
argument_list|)
expr_stmt|;
name|assertComplexData
argument_list|(
name|i
argument_list|,
name|cdata
argument_list|,
literal|"JMSPriority"
argument_list|,
name|i
operator|+
literal|1
argument_list|)
expr_stmt|;
name|assertComplexData
argument_list|(
name|i
argument_list|,
name|cdata
argument_list|,
literal|"JMSType"
argument_list|,
literal|"MyType"
argument_list|)
expr_stmt|;
name|assertComplexData
argument_list|(
name|i
argument_list|,
name|cdata
argument_list|,
literal|"JMSCorrelationID"
argument_list|,
literal|"MyCorrId"
argument_list|)
expr_stmt|;
name|assertComplexData
argument_list|(
name|i
argument_list|,
name|cdata
argument_list|,
literal|"JMSDeliveryMode"
argument_list|,
literal|"NON-PERSISTENT"
argument_list|)
expr_stmt|;
name|String
name|expected
init|=
literal|"{MyStringHeader=StringHeader"
operator|+
name|i
operator|+
literal|", MyHeader="
operator|+
name|i
operator|+
literal|"}"
decl_stmt|;
comment|// The order of the properties is different when using the ibm jdk.
if|if
condition|(
name|System
operator|.
name|getProperty
argument_list|(
literal|"java.vendor"
argument_list|)
operator|.
name|equals
argument_list|(
literal|"IBM Corporation"
argument_list|)
condition|)
block|{
name|expected
operator|=
literal|"{MyHeader="
operator|+
name|i
operator|+
literal|", MyStringHeader=StringHeader"
operator|+
name|i
operator|+
literal|"}"
expr_stmt|;
block|}
name|assertComplexData
argument_list|(
name|i
argument_list|,
name|cdata
argument_list|,
literal|"PropertiesText"
argument_list|,
name|expected
argument_list|)
expr_stmt|;
name|Map
name|intProperties
init|=
name|CompositeDataHelper
operator|.
name|getTabularMap
argument_list|(
name|cdata
argument_list|,
name|CompositeDataConstants
operator|.
name|INT_PROPERTIES
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|"intProperties size()"
argument_list|,
literal|1
argument_list|,
name|intProperties
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"intProperties.MyHeader"
argument_list|,
name|i
argument_list|,
name|intProperties
operator|.
name|get
argument_list|(
literal|"MyHeader"
argument_list|)
argument_list|)
expr_stmt|;
name|Map
name|stringProperties
init|=
name|CompositeDataHelper
operator|.
name|getTabularMap
argument_list|(
name|cdata
argument_list|,
name|CompositeDataConstants
operator|.
name|STRING_PROPERTIES
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|"stringProperties size()"
argument_list|,
literal|1
argument_list|,
name|stringProperties
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"stringProperties.MyHeader"
argument_list|,
literal|"StringHeader"
operator|+
name|i
argument_list|,
name|stringProperties
operator|.
name|get
argument_list|(
literal|"MyStringHeader"
argument_list|)
argument_list|)
expr_stmt|;
name|Map
name|properties
init|=
name|CompositeDataHelper
operator|.
name|getMessageUserProperties
argument_list|(
name|cdata
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|"properties size()"
argument_list|,
literal|2
argument_list|,
name|properties
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"properties.MyHeader"
argument_list|,
name|i
argument_list|,
name|properties
operator|.
name|get
argument_list|(
literal|"MyHeader"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"properties.MyHeader"
argument_list|,
literal|"StringHeader"
operator|+
name|i
argument_list|,
name|properties
operator|.
name|get
argument_list|(
literal|"MyStringHeader"
argument_list|)
argument_list|)
expr_stmt|;
name|assertComplexData
argument_list|(
name|i
argument_list|,
name|cdata
argument_list|,
literal|"JMSXGroupSeq"
argument_list|,
literal|1234
argument_list|)
expr_stmt|;
name|assertComplexData
argument_list|(
name|i
argument_list|,
name|cdata
argument_list|,
literal|"JMSXGroupID"
argument_list|,
literal|"MyGroupID"
argument_list|)
expr_stmt|;
name|assertComplexData
argument_list|(
name|i
argument_list|,
name|cdata
argument_list|,
literal|"Text"
argument_list|,
literal|"message:"
operator|+
name|i
argument_list|)
expr_stmt|;
block|}
block|}
specifier|protected
name|void
name|assertComplexData
parameter_list|(
name|int
name|messageIndex
parameter_list|,
name|CompositeData
name|cdata
parameter_list|,
name|String
name|name
parameter_list|,
name|Object
name|expected
parameter_list|)
block|{
name|Object
name|value
init|=
name|cdata
operator|.
name|get
argument_list|(
name|name
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|"Message "
operator|+
name|messageIndex
operator|+
literal|" CData field: "
operator|+
name|name
argument_list|,
name|expected
argument_list|,
name|value
argument_list|)
expr_stmt|;
block|}
specifier|protected
name|void
name|assertQueueBrowseWorks
parameter_list|()
throws|throws
name|Exception
block|{
name|Integer
name|mbeancnt
init|=
name|mbeanServer
operator|.
name|getMBeanCount
argument_list|()
decl_stmt|;
name|echo
argument_list|(
literal|"Mbean count :"
operator|+
name|mbeancnt
argument_list|)
expr_stmt|;
name|ObjectName
name|queueViewMBeanName
init|=
name|assertRegisteredObjectName
argument_list|(
name|domain
operator|+
literal|":Type=Queue,Destination="
operator|+
name|getDestinationString
argument_list|()
operator|+
literal|",BrokerName=localhost"
argument_list|)
decl_stmt|;
name|echo
argument_list|(
literal|"Create QueueView MBean..."
argument_list|)
expr_stmt|;
name|QueueViewMBean
name|proxy
init|=
operator|(
name|QueueViewMBean
operator|)
name|MBeanServerInvocationHandler
operator|.
name|newProxyInstance
argument_list|(
name|mbeanServer
argument_list|,
name|queueViewMBeanName
argument_list|,
name|QueueViewMBean
operator|.
name|class
argument_list|,
literal|true
argument_list|)
decl_stmt|;
name|long
name|concount
init|=
name|proxy
operator|.
name|getConsumerCount
argument_list|()
decl_stmt|;
name|echo
argument_list|(
literal|"Consumer Count :"
operator|+
name|concount
argument_list|)
expr_stmt|;
name|long
name|messcount
init|=
name|proxy
operator|.
name|getQueueSize
argument_list|()
decl_stmt|;
name|echo
argument_list|(
literal|"current number of messages in the queue :"
operator|+
name|messcount
argument_list|)
expr_stmt|;
comment|// lets browse
name|CompositeData
index|[]
name|compdatalist
init|=
name|proxy
operator|.
name|browse
argument_list|()
decl_stmt|;
if|if
condition|(
name|compdatalist
operator|.
name|length
operator|==
literal|0
condition|)
block|{
name|fail
argument_list|(
literal|"There is no message in the queue:"
argument_list|)
expr_stmt|;
block|}
name|String
index|[]
name|messageIDs
init|=
operator|new
name|String
index|[
name|compdatalist
operator|.
name|length
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
name|compdatalist
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|CompositeData
name|cdata
init|=
name|compdatalist
index|[
name|i
index|]
decl_stmt|;
if|if
condition|(
name|i
operator|==
literal|0
condition|)
block|{
name|echo
argument_list|(
literal|"Columns: "
operator|+
name|cdata
operator|.
name|getCompositeType
argument_list|()
operator|.
name|keySet
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|messageIDs
index|[
name|i
index|]
operator|=
operator|(
name|String
operator|)
name|cdata
operator|.
name|get
argument_list|(
literal|"JMSMessageID"
argument_list|)
expr_stmt|;
name|echo
argument_list|(
literal|"message "
operator|+
name|i
operator|+
literal|" : "
operator|+
name|cdata
operator|.
name|values
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|TabularData
name|table
init|=
name|proxy
operator|.
name|browseAsTable
argument_list|()
decl_stmt|;
name|echo
argument_list|(
literal|"Found tabular data: "
operator|+
name|table
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
literal|"Table should not be empty!"
argument_list|,
name|table
operator|.
name|size
argument_list|()
operator|>
literal|0
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"Queue size"
argument_list|,
name|MESSAGE_COUNT
argument_list|,
name|proxy
operator|.
name|getQueueSize
argument_list|()
argument_list|)
expr_stmt|;
name|String
name|messageID
init|=
name|messageIDs
index|[
literal|0
index|]
decl_stmt|;
name|String
name|newDestinationName
init|=
literal|"queue://dummy.test.cheese"
decl_stmt|;
name|echo
argument_list|(
literal|"Attempting to copy: "
operator|+
name|messageID
operator|+
literal|" to destination: "
operator|+
name|newDestinationName
argument_list|)
expr_stmt|;
name|proxy
operator|.
name|copyMessageTo
argument_list|(
name|messageID
argument_list|,
name|newDestinationName
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"Queue size"
argument_list|,
name|MESSAGE_COUNT
argument_list|,
name|proxy
operator|.
name|getQueueSize
argument_list|()
argument_list|)
expr_stmt|;
name|messageID
operator|=
name|messageIDs
index|[
literal|1
index|]
expr_stmt|;
name|echo
argument_list|(
literal|"Attempting to remove: "
operator|+
name|messageID
argument_list|)
expr_stmt|;
name|proxy
operator|.
name|removeMessage
argument_list|(
name|messageID
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"Queue size"
argument_list|,
name|MESSAGE_COUNT
operator|-
literal|1
argument_list|,
name|proxy
operator|.
name|getQueueSize
argument_list|()
argument_list|)
expr_stmt|;
name|echo
argument_list|(
literal|"Worked!"
argument_list|)
expr_stmt|;
block|}
specifier|protected
name|void
name|assertCreateAndDestroyDurableSubscriptions
parameter_list|()
throws|throws
name|Exception
block|{
comment|// lets create a new topic
name|ObjectName
name|brokerName
init|=
name|assertRegisteredObjectName
argument_list|(
name|domain
operator|+
literal|":Type=Broker,BrokerName=localhost"
argument_list|)
decl_stmt|;
name|echo
argument_list|(
literal|"Create QueueView MBean..."
argument_list|)
expr_stmt|;
name|BrokerViewMBean
name|broker
init|=
operator|(
name|BrokerViewMBean
operator|)
name|MBeanServerInvocationHandler
operator|.
name|newProxyInstance
argument_list|(
name|mbeanServer
argument_list|,
name|brokerName
argument_list|,
name|BrokerViewMBean
operator|.
name|class
argument_list|,
literal|true
argument_list|)
decl_stmt|;
name|broker
operator|.
name|addTopic
argument_list|(
name|getDestinationString
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"Durable subscriber count"
argument_list|,
literal|0
argument_list|,
name|broker
operator|.
name|getDurableTopicSubscribers
argument_list|()
operator|.
name|length
argument_list|)
expr_stmt|;
name|String
name|topicName
init|=
name|getDestinationString
argument_list|()
decl_stmt|;
name|String
name|selector
init|=
literal|null
decl_stmt|;
name|ObjectName
name|name1
init|=
name|broker
operator|.
name|createDurableSubscriber
argument_list|(
name|clientID
argument_list|,
literal|"subscriber1"
argument_list|,
name|topicName
argument_list|,
name|selector
argument_list|)
decl_stmt|;
name|broker
operator|.
name|createDurableSubscriber
argument_list|(
name|clientID
argument_list|,
literal|"subscriber2"
argument_list|,
name|topicName
argument_list|,
name|selector
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"Durable subscriber count"
argument_list|,
literal|2
argument_list|,
name|broker
operator|.
name|getInactiveDurableTopicSubscribers
argument_list|()
operator|.
name|length
argument_list|)
expr_stmt|;
name|assertNotNull
argument_list|(
literal|"Should have created an mbean name for the durable subscriber!"
argument_list|,
name|name1
argument_list|)
expr_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"Created durable subscriber with name: "
operator|+
name|name1
argument_list|)
expr_stmt|;
comment|// now lets try destroy it
name|broker
operator|.
name|destroyDurableSubscriber
argument_list|(
name|clientID
argument_list|,
literal|"subscriber1"
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"Durable subscriber count"
argument_list|,
literal|1
argument_list|,
name|broker
operator|.
name|getInactiveDurableTopicSubscribers
argument_list|()
operator|.
name|length
argument_list|)
expr_stmt|;
block|}
specifier|protected
name|void
name|assertConsumerCounts
parameter_list|()
throws|throws
name|Exception
block|{
name|ObjectName
name|brokerName
init|=
name|assertRegisteredObjectName
argument_list|(
name|domain
operator|+
literal|":Type=Broker,BrokerName=localhost"
argument_list|)
decl_stmt|;
name|BrokerViewMBean
name|broker
init|=
operator|(
name|BrokerViewMBean
operator|)
name|MBeanServerInvocationHandler
operator|.
name|newProxyInstance
argument_list|(
name|mbeanServer
argument_list|,
name|brokerName
argument_list|,
name|BrokerViewMBean
operator|.
name|class
argument_list|,
literal|true
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
literal|"broker is not a slave"
argument_list|,
operator|!
name|broker
operator|.
name|isSlave
argument_list|()
argument_list|)
expr_stmt|;
comment|// create 2 topics
name|broker
operator|.
name|addTopic
argument_list|(
name|getDestinationString
argument_list|()
operator|+
literal|"1"
argument_list|)
expr_stmt|;
name|broker
operator|.
name|addTopic
argument_list|(
name|getDestinationString
argument_list|()
operator|+
literal|"2"
argument_list|)
expr_stmt|;
name|ObjectName
name|topicObjName1
init|=
name|assertRegisteredObjectName
argument_list|(
name|domain
operator|+
literal|":Type=Topic,BrokerName=localhost,Destination="
operator|+
name|getDestinationString
argument_list|()
operator|+
literal|"1"
argument_list|)
decl_stmt|;
name|ObjectName
name|topicObjName2
init|=
name|assertRegisteredObjectName
argument_list|(
name|domain
operator|+
literal|":Type=Topic,BrokerName=localhost,Destination="
operator|+
name|getDestinationString
argument_list|()
operator|+
literal|"2"
argument_list|)
decl_stmt|;
name|TopicViewMBean
name|topic1
init|=
operator|(
name|TopicViewMBean
operator|)
name|MBeanServerInvocationHandler
operator|.
name|newProxyInstance
argument_list|(
name|mbeanServer
argument_list|,
name|topicObjName1
argument_list|,
name|TopicViewMBean
operator|.
name|class
argument_list|,
literal|true
argument_list|)
decl_stmt|;
name|TopicViewMBean
name|topic2
init|=
operator|(
name|TopicViewMBean
operator|)
name|MBeanServerInvocationHandler
operator|.
name|newProxyInstance
argument_list|(
name|mbeanServer
argument_list|,
name|topicObjName2
argument_list|,
name|TopicViewMBean
operator|.
name|class
argument_list|,
literal|true
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|"topic1 Durable subscriber count"
argument_list|,
literal|0
argument_list|,
name|topic1
operator|.
name|getConsumerCount
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"topic2 Durable subscriber count"
argument_list|,
literal|0
argument_list|,
name|topic2
operator|.
name|getConsumerCount
argument_list|()
argument_list|)
expr_stmt|;
name|String
name|topicName
init|=
name|getDestinationString
argument_list|()
decl_stmt|;
name|String
name|selector
init|=
literal|null
decl_stmt|;
comment|// create 1 subscriber for each topic
name|broker
operator|.
name|createDurableSubscriber
argument_list|(
name|clientID
argument_list|,
literal|"topic1.subscriber1"
argument_list|,
name|topicName
operator|+
literal|"1"
argument_list|,
name|selector
argument_list|)
expr_stmt|;
name|broker
operator|.
name|createDurableSubscriber
argument_list|(
name|clientID
argument_list|,
literal|"topic2.subscriber1"
argument_list|,
name|topicName
operator|+
literal|"2"
argument_list|,
name|selector
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"topic1 Durable subscriber count"
argument_list|,
literal|1
argument_list|,
name|topic1
operator|.
name|getConsumerCount
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"topic2 Durable subscriber count"
argument_list|,
literal|1
argument_list|,
name|topic2
operator|.
name|getConsumerCount
argument_list|()
argument_list|)
expr_stmt|;
comment|// create 1 more subscriber for topic1
name|broker
operator|.
name|createDurableSubscriber
argument_list|(
name|clientID
argument_list|,
literal|"topic1.subscriber2"
argument_list|,
name|topicName
operator|+
literal|"1"
argument_list|,
name|selector
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"topic1 Durable subscriber count"
argument_list|,
literal|2
argument_list|,
name|topic1
operator|.
name|getConsumerCount
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"topic2 Durable subscriber count"
argument_list|,
literal|1
argument_list|,
name|topic2
operator|.
name|getConsumerCount
argument_list|()
argument_list|)
expr_stmt|;
comment|// destroy topic1 subscriber
name|broker
operator|.
name|destroyDurableSubscriber
argument_list|(
name|clientID
argument_list|,
literal|"topic1.subscriber1"
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"topic1 Durable subscriber count"
argument_list|,
literal|1
argument_list|,
name|topic1
operator|.
name|getConsumerCount
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"topic2 Durable subscriber count"
argument_list|,
literal|1
argument_list|,
name|topic2
operator|.
name|getConsumerCount
argument_list|()
argument_list|)
expr_stmt|;
comment|// destroy topic2 subscriber
name|broker
operator|.
name|destroyDurableSubscriber
argument_list|(
name|clientID
argument_list|,
literal|"topic2.subscriber1"
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"topic1 Durable subscriber count"
argument_list|,
literal|1
argument_list|,
name|topic1
operator|.
name|getConsumerCount
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"topic2 Durable subscriber count"
argument_list|,
literal|0
argument_list|,
name|topic2
operator|.
name|getConsumerCount
argument_list|()
argument_list|)
expr_stmt|;
comment|// destroy remaining topic1 subscriber
name|broker
operator|.
name|destroyDurableSubscriber
argument_list|(
name|clientID
argument_list|,
literal|"topic1.subscriber2"
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"topic1 Durable subscriber count"
argument_list|,
literal|0
argument_list|,
name|topic1
operator|.
name|getConsumerCount
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"topic2 Durable subscriber count"
argument_list|,
literal|0
argument_list|,
name|topic2
operator|.
name|getConsumerCount
argument_list|()
argument_list|)
expr_stmt|;
block|}
specifier|protected
name|void
name|assertProducerCounts
parameter_list|()
throws|throws
name|Exception
block|{
name|ObjectName
name|brokerName
init|=
name|assertRegisteredObjectName
argument_list|(
name|domain
operator|+
literal|":Type=Broker,BrokerName=localhost"
argument_list|)
decl_stmt|;
name|BrokerViewMBean
name|broker
init|=
operator|(
name|BrokerViewMBean
operator|)
name|MBeanServerInvocationHandler
operator|.
name|newProxyInstance
argument_list|(
name|mbeanServer
argument_list|,
name|brokerName
argument_list|,
name|BrokerViewMBean
operator|.
name|class
argument_list|,
literal|true
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
literal|"broker is not a slave"
argument_list|,
operator|!
name|broker
operator|.
name|isSlave
argument_list|()
argument_list|)
expr_stmt|;
comment|// create 2 topics
name|broker
operator|.
name|addTopic
argument_list|(
name|getDestinationString
argument_list|()
operator|+
literal|"1"
argument_list|)
expr_stmt|;
name|broker
operator|.
name|addTopic
argument_list|(
name|getDestinationString
argument_list|()
operator|+
literal|"2"
argument_list|)
expr_stmt|;
name|ObjectName
name|topicObjName1
init|=
name|assertRegisteredObjectName
argument_list|(
name|domain
operator|+
literal|":Type=Topic,BrokerName=localhost,Destination="
operator|+
name|getDestinationString
argument_list|()
operator|+
literal|"1"
argument_list|)
decl_stmt|;
name|ObjectName
name|topicObjName2
init|=
name|assertRegisteredObjectName
argument_list|(
name|domain
operator|+
literal|":Type=Topic,BrokerName=localhost,Destination="
operator|+
name|getDestinationString
argument_list|()
operator|+
literal|"2"
argument_list|)
decl_stmt|;
name|TopicViewMBean
name|topic1
init|=
operator|(
name|TopicViewMBean
operator|)
name|MBeanServerInvocationHandler
operator|.
name|newProxyInstance
argument_list|(
name|mbeanServer
argument_list|,
name|topicObjName1
argument_list|,
name|TopicViewMBean
operator|.
name|class
argument_list|,
literal|true
argument_list|)
decl_stmt|;
name|TopicViewMBean
name|topic2
init|=
operator|(
name|TopicViewMBean
operator|)
name|MBeanServerInvocationHandler
operator|.
name|newProxyInstance
argument_list|(
name|mbeanServer
argument_list|,
name|topicObjName2
argument_list|,
name|TopicViewMBean
operator|.
name|class
argument_list|,
literal|true
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|"topic1 Producer count"
argument_list|,
literal|0
argument_list|,
name|topic1
operator|.
name|getProducerCount
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"topic2 Producer count"
argument_list|,
literal|0
argument_list|,
name|topic2
operator|.
name|getProducerCount
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"broker Topic Producer count"
argument_list|,
literal|0
argument_list|,
name|broker
operator|.
name|getTopicProducers
argument_list|()
operator|.
name|length
argument_list|)
expr_stmt|;
comment|// create 1 producer for each topic
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
name|Destination
name|dest1
init|=
name|session
operator|.
name|createTopic
argument_list|(
name|getDestinationString
argument_list|()
operator|+
literal|"1"
argument_list|)
decl_stmt|;
name|Destination
name|dest2
init|=
name|session
operator|.
name|createTopic
argument_list|(
name|getDestinationString
argument_list|()
operator|+
literal|"2"
argument_list|)
decl_stmt|;
name|MessageProducer
name|producer1
init|=
name|session
operator|.
name|createProducer
argument_list|(
name|dest1
argument_list|)
decl_stmt|;
name|MessageProducer
name|producer2
init|=
name|session
operator|.
name|createProducer
argument_list|(
name|dest2
argument_list|)
decl_stmt|;
name|Thread
operator|.
name|sleep
argument_list|(
literal|500
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"topic1 Producer count"
argument_list|,
literal|1
argument_list|,
name|topic1
operator|.
name|getProducerCount
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"topic2 Producer count"
argument_list|,
literal|1
argument_list|,
name|topic2
operator|.
name|getProducerCount
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"broker Topic Producer count"
argument_list|,
literal|2
argument_list|,
name|broker
operator|.
name|getTopicProducers
argument_list|()
operator|.
name|length
argument_list|)
expr_stmt|;
comment|// create 1 more producer for topic1
name|MessageProducer
name|producer3
init|=
name|session
operator|.
name|createProducer
argument_list|(
name|dest1
argument_list|)
decl_stmt|;
name|Thread
operator|.
name|sleep
argument_list|(
literal|500
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"topic1 Producer count"
argument_list|,
literal|2
argument_list|,
name|topic1
operator|.
name|getProducerCount
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"topic2 Producer count"
argument_list|,
literal|1
argument_list|,
name|topic2
operator|.
name|getProducerCount
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"broker Topic Producer count"
argument_list|,
literal|3
argument_list|,
name|broker
operator|.
name|getTopicProducers
argument_list|()
operator|.
name|length
argument_list|)
expr_stmt|;
comment|// destroy topic1 producer
name|producer1
operator|.
name|close
argument_list|()
expr_stmt|;
name|Thread
operator|.
name|sleep
argument_list|(
literal|500
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"topic1 Producer count"
argument_list|,
literal|1
argument_list|,
name|topic1
operator|.
name|getProducerCount
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"topic2 Producer count"
argument_list|,
literal|1
argument_list|,
name|topic2
operator|.
name|getProducerCount
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"broker Topic Producer count"
argument_list|,
literal|2
argument_list|,
name|broker
operator|.
name|getTopicProducers
argument_list|()
operator|.
name|length
argument_list|)
expr_stmt|;
comment|// destroy topic2 producer
name|producer2
operator|.
name|close
argument_list|()
expr_stmt|;
name|Thread
operator|.
name|sleep
argument_list|(
literal|500
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"topic1 Producer count"
argument_list|,
literal|1
argument_list|,
name|topic1
operator|.
name|getProducerCount
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"topic2 Producer count"
argument_list|,
literal|0
argument_list|,
name|topic2
operator|.
name|getProducerCount
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"broker Topic Producer count"
argument_list|,
literal|1
argument_list|,
name|broker
operator|.
name|getTopicProducers
argument_list|()
operator|.
name|length
argument_list|)
expr_stmt|;
comment|// destroy remaining topic1 producer
name|producer3
operator|.
name|close
argument_list|()
expr_stmt|;
name|Thread
operator|.
name|sleep
argument_list|(
literal|500
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"topic1 Producer count"
argument_list|,
literal|0
argument_list|,
name|topic1
operator|.
name|getProducerCount
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"topic2 Producer count"
argument_list|,
literal|0
argument_list|,
name|topic2
operator|.
name|getProducerCount
argument_list|()
argument_list|)
expr_stmt|;
name|MessageProducer
name|producer4
init|=
name|session
operator|.
name|createProducer
argument_list|(
literal|null
argument_list|)
decl_stmt|;
name|Thread
operator|.
name|sleep
argument_list|(
literal|500
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|broker
operator|.
name|getDynamicDestinationProducers
argument_list|()
operator|.
name|length
argument_list|)
expr_stmt|;
name|producer4
operator|.
name|close
argument_list|()
expr_stmt|;
name|Thread
operator|.
name|sleep
argument_list|(
literal|500
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"broker Topic Producer count"
argument_list|,
literal|0
argument_list|,
name|broker
operator|.
name|getTopicProducers
argument_list|()
operator|.
name|length
argument_list|)
expr_stmt|;
block|}
specifier|protected
name|ObjectName
name|assertRegisteredObjectName
parameter_list|(
name|String
name|name
parameter_list|)
throws|throws
name|MalformedObjectNameException
throws|,
name|NullPointerException
block|{
name|ObjectName
name|objectName
init|=
operator|new
name|ObjectName
argument_list|(
name|name
argument_list|)
decl_stmt|;
if|if
condition|(
name|mbeanServer
operator|.
name|isRegistered
argument_list|(
name|objectName
argument_list|)
condition|)
block|{
name|echo
argument_list|(
literal|"Bean Registered: "
operator|+
name|objectName
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|fail
argument_list|(
literal|"Could not find MBean!: "
operator|+
name|objectName
argument_list|)
expr_stmt|;
block|}
return|return
name|objectName
return|;
block|}
specifier|protected
name|void
name|setUp
parameter_list|()
throws|throws
name|Exception
block|{
name|bindAddress
operator|=
literal|"tcp://localhost:61616"
expr_stmt|;
name|useTopic
operator|=
literal|false
expr_stmt|;
name|super
operator|.
name|setUp
argument_list|()
expr_stmt|;
name|mbeanServer
operator|=
name|broker
operator|.
name|getManagementContext
argument_list|()
operator|.
name|getMBeanServer
argument_list|()
expr_stmt|;
block|}
specifier|protected
name|void
name|tearDown
parameter_list|()
throws|throws
name|Exception
block|{
if|if
condition|(
name|waitForKeyPress
condition|)
block|{
comment|// We are running from the command line so let folks browse the
comment|// mbeans...
name|System
operator|.
name|out
operator|.
name|println
argument_list|()
expr_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"Press enter to terminate the program."
argument_list|)
expr_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"In the meantime you can use your JMX console to view the current MBeans"
argument_list|)
expr_stmt|;
name|BufferedReader
name|reader
init|=
operator|new
name|BufferedReader
argument_list|(
operator|new
name|InputStreamReader
argument_list|(
name|System
operator|.
name|in
argument_list|)
argument_list|)
decl_stmt|;
name|reader
operator|.
name|readLine
argument_list|()
expr_stmt|;
block|}
if|if
condition|(
name|connection
operator|!=
literal|null
condition|)
block|{
name|connection
operator|.
name|close
argument_list|()
expr_stmt|;
name|connection
operator|=
literal|null
expr_stmt|;
block|}
name|super
operator|.
name|tearDown
argument_list|()
expr_stmt|;
block|}
specifier|protected
name|BrokerService
name|createBroker
parameter_list|()
throws|throws
name|Exception
block|{
name|BrokerService
name|answer
init|=
operator|new
name|BrokerService
argument_list|()
decl_stmt|;
name|answer
operator|.
name|setPersistent
argument_list|(
literal|false
argument_list|)
expr_stmt|;
name|answer
operator|.
name|setDeleteAllMessagesOnStartup
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|answer
operator|.
name|setUseJmx
argument_list|(
literal|true
argument_list|)
expr_stmt|;
comment|// apply memory limit so that %usage is visible
name|PolicyMap
name|policyMap
init|=
operator|new
name|PolicyMap
argument_list|()
decl_stmt|;
name|PolicyEntry
name|defaultEntry
init|=
operator|new
name|PolicyEntry
argument_list|()
decl_stmt|;
name|defaultEntry
operator|.
name|setMemoryLimit
argument_list|(
literal|1024
operator|*
literal|1024
operator|*
literal|4
argument_list|)
expr_stmt|;
name|policyMap
operator|.
name|setDefaultEntry
argument_list|(
name|defaultEntry
argument_list|)
expr_stmt|;
name|answer
operator|.
name|setDestinationPolicy
argument_list|(
name|policyMap
argument_list|)
expr_stmt|;
name|answer
operator|.
name|addConnector
argument_list|(
name|bindAddress
argument_list|)
expr_stmt|;
return|return
name|answer
return|;
block|}
specifier|protected
name|void
name|useConnection
parameter_list|(
name|Connection
name|connection
parameter_list|)
throws|throws
name|Exception
block|{
name|connection
operator|.
name|setClientID
argument_list|(
name|clientID
argument_list|)
expr_stmt|;
name|connection
operator|.
name|start
argument_list|()
expr_stmt|;
name|Session
name|session
init|=
name|connection
operator|.
name|createSession
argument_list|(
name|transacted
argument_list|,
name|authMode
argument_list|)
decl_stmt|;
name|destination
operator|=
name|createDestination
argument_list|()
expr_stmt|;
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
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|MESSAGE_COUNT
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
literal|"Message: "
operator|+
name|i
argument_list|)
decl_stmt|;
name|message
operator|.
name|setIntProperty
argument_list|(
literal|"counter"
argument_list|,
name|i
argument_list|)
expr_stmt|;
name|message
operator|.
name|setJMSCorrelationID
argument_list|(
literal|"MyCorrelationID"
argument_list|)
expr_stmt|;
name|message
operator|.
name|setJMSReplyTo
argument_list|(
operator|new
name|ActiveMQQueue
argument_list|(
literal|"MyReplyTo"
argument_list|)
argument_list|)
expr_stmt|;
name|message
operator|.
name|setJMSType
argument_list|(
literal|"MyType"
argument_list|)
expr_stmt|;
name|message
operator|.
name|setJMSPriority
argument_list|(
literal|5
argument_list|)
expr_stmt|;
name|producer
operator|.
name|send
argument_list|(
name|message
argument_list|)
expr_stmt|;
block|}
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
name|useConnectionWithBlobMessage
parameter_list|(
name|Connection
name|connection
parameter_list|)
throws|throws
name|Exception
block|{
name|connection
operator|.
name|setClientID
argument_list|(
name|clientID
argument_list|)
expr_stmt|;
name|connection
operator|.
name|start
argument_list|()
expr_stmt|;
name|ActiveMQSession
name|session
init|=
operator|(
name|ActiveMQSession
operator|)
name|connection
operator|.
name|createSession
argument_list|(
name|transacted
argument_list|,
name|authMode
argument_list|)
decl_stmt|;
name|destination
operator|=
name|createDestination
argument_list|()
expr_stmt|;
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
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|MESSAGE_COUNT
condition|;
name|i
operator|++
control|)
block|{
name|BlobMessage
name|message
init|=
name|session
operator|.
name|createBlobMessage
argument_list|(
operator|new
name|URL
argument_list|(
literal|"http://foo.bar/test"
argument_list|)
argument_list|)
decl_stmt|;
name|message
operator|.
name|setIntProperty
argument_list|(
literal|"counter"
argument_list|,
name|i
argument_list|)
expr_stmt|;
name|message
operator|.
name|setJMSCorrelationID
argument_list|(
literal|"MyCorrelationID"
argument_list|)
expr_stmt|;
name|message
operator|.
name|setJMSReplyTo
argument_list|(
operator|new
name|ActiveMQQueue
argument_list|(
literal|"MyReplyTo"
argument_list|)
argument_list|)
expr_stmt|;
name|message
operator|.
name|setJMSType
argument_list|(
literal|"MyType"
argument_list|)
expr_stmt|;
name|message
operator|.
name|setJMSPriority
argument_list|(
literal|5
argument_list|)
expr_stmt|;
name|producer
operator|.
name|send
argument_list|(
name|message
argument_list|)
expr_stmt|;
block|}
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
name|useConnectionWithByteMessage
parameter_list|(
name|Connection
name|connection
parameter_list|)
throws|throws
name|Exception
block|{
name|connection
operator|.
name|setClientID
argument_list|(
name|clientID
argument_list|)
expr_stmt|;
name|connection
operator|.
name|start
argument_list|()
expr_stmt|;
name|ActiveMQSession
name|session
init|=
operator|(
name|ActiveMQSession
operator|)
name|connection
operator|.
name|createSession
argument_list|(
name|transacted
argument_list|,
name|authMode
argument_list|)
decl_stmt|;
name|destination
operator|=
name|createDestination
argument_list|()
expr_stmt|;
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
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|MESSAGE_COUNT
condition|;
name|i
operator|++
control|)
block|{
name|BytesMessage
name|message
init|=
name|session
operator|.
name|createBytesMessage
argument_list|()
decl_stmt|;
name|message
operator|.
name|writeBytes
argument_list|(
operator|(
literal|"Message: "
operator|+
name|i
operator|)
operator|.
name|getBytes
argument_list|()
argument_list|)
expr_stmt|;
name|message
operator|.
name|setIntProperty
argument_list|(
literal|"counter"
argument_list|,
name|i
argument_list|)
expr_stmt|;
name|message
operator|.
name|setJMSCorrelationID
argument_list|(
literal|"MyCorrelationID"
argument_list|)
expr_stmt|;
name|message
operator|.
name|setJMSReplyTo
argument_list|(
operator|new
name|ActiveMQQueue
argument_list|(
literal|"MyReplyTo"
argument_list|)
argument_list|)
expr_stmt|;
name|message
operator|.
name|setJMSType
argument_list|(
literal|"MyType"
argument_list|)
expr_stmt|;
name|message
operator|.
name|setJMSPriority
argument_list|(
literal|5
argument_list|)
expr_stmt|;
name|producer
operator|.
name|send
argument_list|(
name|message
argument_list|)
expr_stmt|;
block|}
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
name|echo
parameter_list|(
name|String
name|text
parameter_list|)
block|{
name|LOG
operator|.
name|info
argument_list|(
name|text
argument_list|)
expr_stmt|;
block|}
specifier|protected
name|String
name|getSecondDestinationString
parameter_list|()
block|{
return|return
literal|"test.new.destination."
operator|+
name|getClass
argument_list|()
operator|+
literal|"."
operator|+
name|getName
argument_list|()
return|;
block|}
specifier|public
name|void
name|testDynamicProducerView
parameter_list|()
throws|throws
name|Exception
block|{
name|connection
operator|=
name|connectionFactory
operator|.
name|createConnection
argument_list|()
expr_stmt|;
name|ObjectName
name|brokerName
init|=
name|assertRegisteredObjectName
argument_list|(
name|domain
operator|+
literal|":Type=Broker,BrokerName=localhost"
argument_list|)
decl_stmt|;
name|BrokerViewMBean
name|broker
init|=
operator|(
name|BrokerViewMBean
operator|)
name|MBeanServerInvocationHandler
operator|.
name|newProxyInstance
argument_list|(
name|mbeanServer
argument_list|,
name|brokerName
argument_list|,
name|BrokerViewMBean
operator|.
name|class
argument_list|,
literal|true
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
literal|"broker is not a slave"
argument_list|,
operator|!
name|broker
operator|.
name|isSlave
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|broker
operator|.
name|getDynamicDestinationProducers
argument_list|()
operator|.
name|length
argument_list|)
expr_stmt|;
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
literal|null
argument_list|)
decl_stmt|;
name|Destination
name|dest1
init|=
name|session
operator|.
name|createTopic
argument_list|(
literal|"DynamicDest-1"
argument_list|)
decl_stmt|;
name|Destination
name|dest2
init|=
name|session
operator|.
name|createTopic
argument_list|(
literal|"DynamicDest-2"
argument_list|)
decl_stmt|;
name|Destination
name|dest3
init|=
name|session
operator|.
name|createQueue
argument_list|(
literal|"DynamicDest-3"
argument_list|)
decl_stmt|;
comment|// Wait a bit to let the producer get registered.
name|Thread
operator|.
name|sleep
argument_list|(
literal|100
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|broker
operator|.
name|getDynamicDestinationProducers
argument_list|()
operator|.
name|length
argument_list|)
expr_stmt|;
name|ObjectName
name|viewName
init|=
name|broker
operator|.
name|getDynamicDestinationProducers
argument_list|()
index|[
literal|0
index|]
decl_stmt|;
name|assertNotNull
argument_list|(
name|viewName
argument_list|)
expr_stmt|;
name|ProducerViewMBean
name|view
init|=
operator|(
name|ProducerViewMBean
operator|)
name|MBeanServerInvocationHandler
operator|.
name|newProxyInstance
argument_list|(
name|mbeanServer
argument_list|,
name|viewName
argument_list|,
name|ProducerViewMBean
operator|.
name|class
argument_list|,
literal|true
argument_list|)
decl_stmt|;
name|assertNotNull
argument_list|(
name|view
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"NOTSET"
argument_list|,
name|view
operator|.
name|getDestinationName
argument_list|()
argument_list|)
expr_stmt|;
name|producer
operator|.
name|send
argument_list|(
name|dest1
argument_list|,
name|session
operator|.
name|createTextMessage
argument_list|(
literal|"Test Message 1"
argument_list|)
argument_list|)
expr_stmt|;
name|Thread
operator|.
name|sleep
argument_list|(
literal|200
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
operator|(
operator|(
name|ActiveMQDestination
operator|)
name|dest1
operator|)
operator|.
name|getPhysicalName
argument_list|()
argument_list|,
name|view
operator|.
name|getDestinationName
argument_list|()
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|view
operator|.
name|isDestinationTopic
argument_list|()
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|view
operator|.
name|isDestinationQueue
argument_list|()
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|view
operator|.
name|isDestinationTemporary
argument_list|()
argument_list|)
expr_stmt|;
name|producer
operator|.
name|send
argument_list|(
name|dest2
argument_list|,
name|session
operator|.
name|createTextMessage
argument_list|(
literal|"Test Message 2"
argument_list|)
argument_list|)
expr_stmt|;
name|Thread
operator|.
name|sleep
argument_list|(
literal|200
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
operator|(
operator|(
name|ActiveMQDestination
operator|)
name|dest2
operator|)
operator|.
name|getPhysicalName
argument_list|()
argument_list|,
name|view
operator|.
name|getDestinationName
argument_list|()
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|view
operator|.
name|isDestinationTopic
argument_list|()
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|view
operator|.
name|isDestinationQueue
argument_list|()
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|view
operator|.
name|isDestinationTemporary
argument_list|()
argument_list|)
expr_stmt|;
name|producer
operator|.
name|send
argument_list|(
name|dest3
argument_list|,
name|session
operator|.
name|createTextMessage
argument_list|(
literal|"Test Message 3"
argument_list|)
argument_list|)
expr_stmt|;
name|Thread
operator|.
name|sleep
argument_list|(
literal|200
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
operator|(
operator|(
name|ActiveMQDestination
operator|)
name|dest3
operator|)
operator|.
name|getPhysicalName
argument_list|()
argument_list|,
name|view
operator|.
name|getDestinationName
argument_list|()
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|view
operator|.
name|isDestinationQueue
argument_list|()
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|view
operator|.
name|isDestinationTopic
argument_list|()
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|view
operator|.
name|isDestinationTemporary
argument_list|()
argument_list|)
expr_stmt|;
name|producer
operator|.
name|close
argument_list|()
expr_stmt|;
name|Thread
operator|.
name|sleep
argument_list|(
literal|200
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|broker
operator|.
name|getDynamicDestinationProducers
argument_list|()
operator|.
name|length
argument_list|)
expr_stmt|;
block|}
comment|//    public void testTempQueueJMXDelete() throws Exception {
comment|//        connection = connectionFactory.createConnection();
comment|//
comment|//        connection.setClientID(clientID);
comment|//        connection.start();
comment|//        Session session = connection.createSession(transacted, authMode);
comment|//        ActiveMQTempQueue tQueue = (ActiveMQTempQueue) session.createTemporaryQueue();
comment|//        Thread.sleep(1000);
comment|//        ObjectName queueViewMBeanName = assertRegisteredObjectName(domain + ":Type="+  JMXSupport.encodeObjectNamePart(tQueue.getDestinationTypeAsString())+",Destination=" + JMXSupport.encodeObjectNamePart(tQueue.getPhysicalName()) + ",BrokerName=localhost");
comment|//
comment|//        // should not throw an exception
comment|//        mbeanServer.getObjectInstance(queueViewMBeanName);
comment|//
comment|//        tQueue.delete();
comment|//        Thread.sleep(1000);
comment|//        try {
comment|//            // should throw an exception
comment|//            mbeanServer.getObjectInstance(queueViewMBeanName);
comment|//
comment|//            fail("should be deleted already!");
comment|//        } catch (Exception e) {
comment|//            // expected!
comment|//        }
comment|//
comment|//    }
comment|//
comment|//    // Test for AMQ-3029
comment|//    public void testBrowseBlobMessages() throws Exception {
comment|//        connection = connectionFactory.createConnection();
comment|//        useConnectionWithBlobMessage(connection);
comment|//
comment|//        ObjectName queueViewMBeanName = assertRegisteredObjectName(domain + ":Type=Queue,Destination=" + getDestinationString() + ",BrokerName=localhost");
comment|//
comment|//        QueueViewMBean queue = (QueueViewMBean)MBeanServerInvocationHandler.newProxyInstance(mbeanServer, queueViewMBeanName, QueueViewMBean.class, true);
comment|//
comment|//        CompositeData[] compdatalist = queue.browse();
comment|//        int initialQueueSize = compdatalist.length;
comment|//        if (initialQueueSize == 0) {
comment|//            fail("There is no message in the queue:");
comment|//        }
comment|//        else {
comment|//            echo("Current queue size: " + initialQueueSize);
comment|//        }
comment|//        int messageCount = initialQueueSize;
comment|//        String[] messageIDs = new String[messageCount];
comment|//        for (int i = 0; i< messageCount; i++) {
comment|//            CompositeData cdata = compdatalist[i];
comment|//            String messageID = (String) cdata.get("JMSMessageID");
comment|//            assertNotNull("Should have a message ID for message " + i, messageID);
comment|//
comment|//            messageIDs[i] = messageID;
comment|//        }
comment|//
comment|//        assertTrue("dest has some memory usage", queue.getMemoryPercentUsage()> 0);
comment|//    }
comment|//
comment|//    public void testBrowseBytesMessages() throws Exception {
comment|//        connection = connectionFactory.createConnection();
comment|//        useConnectionWithByteMessage(connection);
comment|//
comment|//        ObjectName queueViewMBeanName = assertRegisteredObjectName(domain + ":Type=Queue,Destination=" + getDestinationString() + ",BrokerName=localhost");
comment|//
comment|//        QueueViewMBean queue = (QueueViewMBean)MBeanServerInvocationHandler.newProxyInstance(mbeanServer, queueViewMBeanName, QueueViewMBean.class, true);
comment|//
comment|//        CompositeData[] compdatalist = queue.browse();
comment|//        int initialQueueSize = compdatalist.length;
comment|//        if (initialQueueSize == 0) {
comment|//            fail("There is no message in the queue:");
comment|//        }
comment|//        else {
comment|//            echo("Current queue size: " + initialQueueSize);
comment|//        }
comment|//        int messageCount = initialQueueSize;
comment|//        String[] messageIDs = new String[messageCount];
comment|//        for (int i = 0; i< messageCount; i++) {
comment|//            CompositeData cdata = compdatalist[i];
comment|//            String messageID = (String) cdata.get("JMSMessageID");
comment|//            assertNotNull("Should have a message ID for message " + i, messageID);
comment|//            messageIDs[i] = messageID;
comment|//
comment|//            Byte[] preview = (Byte[]) cdata.get(CompositeDataConstants.BODY_PREVIEW);
comment|//            assertNotNull("should be a preview", preview);
comment|//            assertTrue("not empty", preview.length> 0);
comment|//        }
comment|//
comment|//        assertTrue("dest has some memory usage", queue.getMemoryPercentUsage()> 0);
comment|//
comment|//        // consume all the messages
comment|//        echo("Attempting to consume all bytes messages from: " + destination);
comment|//        Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
comment|//        MessageConsumer consumer = session.createConsumer(destination);
comment|//        for (int i=0; i<MESSAGE_COUNT; i++) {
comment|//            Message message = consumer.receive(5000);
comment|//            assertNotNull(message);
comment|//            assertTrue(message instanceof BytesMessage);
comment|//        }
comment|//        consumer.close();
comment|//        session.close();
comment|//    }
block|}
end_class

end_unit

