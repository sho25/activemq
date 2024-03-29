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
name|bugs
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
name|atomic
operator|.
name|AtomicInteger
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
name|DeliveryMode
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
name|ExceptionListener
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
name|util
operator|.
name|Wait
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
comment|/**  * Test for AMQ-3965.  * A consumer may be stalled in case it uses optimizeAcknowledge and receives  * a number of messages that expire before being dispatched to application code.  * See for more details.  *  */
end_comment

begin_class
specifier|public
class|class
name|OptimizeAcknowledgeWithExpiredMsgsTest
block|{
specifier|private
specifier|final
specifier|static
name|Logger
name|LOG
init|=
name|LoggerFactory
operator|.
name|getLogger
argument_list|(
name|OptimizeAcknowledgeWithExpiredMsgsTest
operator|.
name|class
argument_list|)
decl_stmt|;
specifier|private
name|BrokerService
name|broker
init|=
literal|null
decl_stmt|;
specifier|private
name|String
name|connectionUri
decl_stmt|;
comment|/**      * Creates a broker instance but does not start it.      *      * @param brokerUri - transport uri of broker      * @param brokerName - name for the broker      * @return a BrokerService instance with transport uri and broker name set      * @throws Exception      */
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
name|setDeleteAllMessagesOnStartup
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|broker
operator|.
name|setUseJmx
argument_list|(
literal|false
argument_list|)
expr_stmt|;
name|connectionUri
operator|=
name|broker
operator|.
name|addConnector
argument_list|(
literal|"tcp://localhost:0"
argument_list|)
operator|.
name|getPublishableConnectString
argument_list|()
expr_stmt|;
return|return
name|broker
return|;
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
name|broker
operator|=
name|createBroker
argument_list|()
expr_stmt|;
name|broker
operator|.
name|start
argument_list|()
expr_stmt|;
name|broker
operator|.
name|waitUntilStarted
argument_list|()
expr_stmt|;
block|}
annotation|@
name|After
specifier|public
name|void
name|tearDown
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
block|{
name|broker
operator|.
name|stop
argument_list|()
expr_stmt|;
name|broker
operator|.
name|waitUntilStopped
argument_list|()
expr_stmt|;
name|broker
operator|=
literal|null
expr_stmt|;
block|}
block|}
comment|/**      * Tests for AMQ-3965      * Creates connection into broker using optimzeAcknowledge and prefetch=100      * Creates producer and consumer. Producer sends 45 msgs that will expire      * at consumer (but before being dispatched to app code).      * Producer then sends 60 msgs without expiry.      *      * Consumer receives msgs using a MessageListener and increments a counter.      * Main thread sleeps for 5 seconds and checks the counter value.      * If counter != 60 msgs (the number of msgs that should get dispatched      * to consumer) the test fails.      */
annotation|@
name|Test
specifier|public
name|void
name|testOptimizedAckWithExpiredMsgs
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
name|connectionUri
operator|+
literal|"?jms.optimizeAcknowledge=true&jms.prefetchPolicy.all=100"
argument_list|)
decl_stmt|;
comment|// Create JMS resources
name|Connection
name|connection
init|=
name|connectionFactory
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
name|Destination
name|destination
init|=
name|session
operator|.
name|createQueue
argument_list|(
literal|"TEST.FOO"
argument_list|)
decl_stmt|;
comment|// ***** Consumer code *****
name|MessageConsumer
name|consumer
init|=
name|session
operator|.
name|createConsumer
argument_list|(
name|destination
argument_list|)
decl_stmt|;
specifier|final
name|MyMessageListener
name|listener
init|=
operator|new
name|MyMessageListener
argument_list|()
decl_stmt|;
name|connection
operator|.
name|setExceptionListener
argument_list|(
operator|(
name|ExceptionListener
operator|)
name|listener
argument_list|)
expr_stmt|;
comment|// ***** Producer Code *****
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
name|producer
operator|.
name|setDeliveryMode
argument_list|(
name|DeliveryMode
operator|.
name|NON_PERSISTENT
argument_list|)
expr_stmt|;
name|String
name|text
init|=
literal|"Hello world! From: "
operator|+
name|Thread
operator|.
name|currentThread
argument_list|()
operator|.
name|getName
argument_list|()
operator|+
literal|" : "
operator|+
name|this
operator|.
name|hashCode
argument_list|()
decl_stmt|;
name|TextMessage
name|message
decl_stmt|;
comment|// Produce msgs that will expire quickly
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
literal|45
condition|;
name|i
operator|++
control|)
block|{
name|message
operator|=
name|session
operator|.
name|createTextMessage
argument_list|(
name|text
argument_list|)
expr_stmt|;
name|producer
operator|.
name|send
argument_list|(
name|message
argument_list|,
literal|1
argument_list|,
literal|1
argument_list|,
literal|100
argument_list|)
expr_stmt|;
name|LOG
operator|.
name|trace
argument_list|(
literal|"Sent message: "
operator|+
name|message
operator|.
name|getJMSMessageID
argument_list|()
operator|+
literal|" with expiry 10 msec"
argument_list|)
expr_stmt|;
block|}
comment|// Produce msgs that don't expire
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
literal|60
condition|;
name|i
operator|++
control|)
block|{
name|message
operator|=
name|session
operator|.
name|createTextMessage
argument_list|(
name|text
argument_list|)
expr_stmt|;
name|producer
operator|.
name|send
argument_list|(
name|message
argument_list|,
literal|1
argument_list|,
literal|1
argument_list|,
literal|60000
argument_list|)
expr_stmt|;
comment|// producer.send(message);
name|LOG
operator|.
name|trace
argument_list|(
literal|"Sent message: "
operator|+
name|message
operator|.
name|getJMSMessageID
argument_list|()
operator|+
literal|" with expiry 30 sec"
argument_list|)
expr_stmt|;
block|}
name|consumer
operator|.
name|setMessageListener
argument_list|(
name|listener
argument_list|)
expr_stmt|;
name|sleep
argument_list|(
literal|1000
argument_list|)
expr_stmt|;
comment|// let the batch of 45 expire.
name|connection
operator|.
name|start
argument_list|()
expr_stmt|;
name|assertTrue
argument_list|(
literal|"Should receive all expected messages, counter at "
operator|+
name|listener
operator|.
name|getCounter
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
name|listener
operator|.
name|getCounter
argument_list|()
operator|==
literal|60
return|;
block|}
block|}
argument_list|)
argument_list|)
expr_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"Received all expected messages with counter at: "
operator|+
name|listener
operator|.
name|getCounter
argument_list|()
argument_list|)
expr_stmt|;
comment|// Cleanup
name|producer
operator|.
name|close
argument_list|()
expr_stmt|;
name|consumer
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
block|}
annotation|@
name|Test
specifier|public
name|void
name|testOptimizedAckWithExpiredMsgsSync
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
name|connectionUri
operator|+
literal|"?jms.optimizeAcknowledge=true&jms.prefetchPolicy.all=100"
argument_list|)
decl_stmt|;
comment|// Create JMS resources
name|Connection
name|connection
init|=
name|connectionFactory
operator|.
name|createConnection
argument_list|()
decl_stmt|;
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
literal|false
argument_list|,
name|Session
operator|.
name|AUTO_ACKNOWLEDGE
argument_list|)
decl_stmt|;
name|Destination
name|destination
init|=
name|session
operator|.
name|createQueue
argument_list|(
literal|"TEST.FOO"
argument_list|)
decl_stmt|;
comment|// ***** Consumer code *****
name|MessageConsumer
name|consumer
init|=
name|session
operator|.
name|createConsumer
argument_list|(
name|destination
argument_list|)
decl_stmt|;
comment|// ***** Producer Code *****
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
name|producer
operator|.
name|setDeliveryMode
argument_list|(
name|DeliveryMode
operator|.
name|NON_PERSISTENT
argument_list|)
expr_stmt|;
name|String
name|text
init|=
literal|"Hello world! From: "
operator|+
name|Thread
operator|.
name|currentThread
argument_list|()
operator|.
name|getName
argument_list|()
operator|+
literal|" : "
operator|+
name|this
operator|.
name|hashCode
argument_list|()
decl_stmt|;
name|TextMessage
name|message
decl_stmt|;
comment|// Produce msgs that will expire quickly
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
literal|45
condition|;
name|i
operator|++
control|)
block|{
name|message
operator|=
name|session
operator|.
name|createTextMessage
argument_list|(
name|text
argument_list|)
expr_stmt|;
name|producer
operator|.
name|send
argument_list|(
name|message
argument_list|,
literal|1
argument_list|,
literal|1
argument_list|,
literal|10
argument_list|)
expr_stmt|;
name|LOG
operator|.
name|trace
argument_list|(
literal|"Sent message: "
operator|+
name|message
operator|.
name|getJMSMessageID
argument_list|()
operator|+
literal|" with expiry 10 msec"
argument_list|)
expr_stmt|;
block|}
comment|// Produce msgs that don't expire
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
literal|60
condition|;
name|i
operator|++
control|)
block|{
name|message
operator|=
name|session
operator|.
name|createTextMessage
argument_list|(
name|text
argument_list|)
expr_stmt|;
name|producer
operator|.
name|send
argument_list|(
name|message
argument_list|,
literal|1
argument_list|,
literal|1
argument_list|,
literal|30000
argument_list|)
expr_stmt|;
comment|// producer.send(message);
name|LOG
operator|.
name|trace
argument_list|(
literal|"Sent message: "
operator|+
name|message
operator|.
name|getJMSMessageID
argument_list|()
operator|+
literal|" with expiry 30 sec"
argument_list|)
expr_stmt|;
block|}
name|sleep
argument_list|(
literal|200
argument_list|)
expr_stmt|;
name|int
name|counter
init|=
literal|1
decl_stmt|;
for|for
control|(
init|;
name|counter
operator|<=
literal|60
condition|;
operator|++
name|counter
control|)
block|{
name|assertNotNull
argument_list|(
name|consumer
operator|.
name|receive
argument_list|(
literal|2000
argument_list|)
argument_list|)
expr_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"counter at "
operator|+
name|counter
argument_list|)
expr_stmt|;
block|}
name|LOG
operator|.
name|info
argument_list|(
literal|"Received all expected messages with counter at: "
operator|+
name|counter
argument_list|)
expr_stmt|;
comment|// Cleanup
name|producer
operator|.
name|close
argument_list|()
expr_stmt|;
name|consumer
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
block|}
annotation|@
name|Test
specifier|public
name|void
name|testOptimizedAckWithExpiredMsgsSync2
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
name|connectionUri
operator|+
literal|"?jms.optimizeAcknowledge=true&jms.prefetchPolicy.all=100"
argument_list|)
decl_stmt|;
comment|// Create JMS resources
name|Connection
name|connection
init|=
name|connectionFactory
operator|.
name|createConnection
argument_list|()
decl_stmt|;
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
literal|false
argument_list|,
name|Session
operator|.
name|AUTO_ACKNOWLEDGE
argument_list|)
decl_stmt|;
name|Destination
name|destination
init|=
name|session
operator|.
name|createQueue
argument_list|(
literal|"TEST.FOO"
argument_list|)
decl_stmt|;
comment|// ***** Consumer code *****
name|MessageConsumer
name|consumer
init|=
name|session
operator|.
name|createConsumer
argument_list|(
name|destination
argument_list|)
decl_stmt|;
comment|// ***** Producer Code *****
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
name|producer
operator|.
name|setDeliveryMode
argument_list|(
name|DeliveryMode
operator|.
name|NON_PERSISTENT
argument_list|)
expr_stmt|;
name|String
name|text
init|=
literal|"Hello world! From: "
operator|+
name|Thread
operator|.
name|currentThread
argument_list|()
operator|.
name|getName
argument_list|()
operator|+
literal|" : "
operator|+
name|this
operator|.
name|hashCode
argument_list|()
decl_stmt|;
name|TextMessage
name|message
decl_stmt|;
comment|// Produce msgs that don't expire
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
literal|56
condition|;
name|i
operator|++
control|)
block|{
name|message
operator|=
name|session
operator|.
name|createTextMessage
argument_list|(
name|text
argument_list|)
expr_stmt|;
name|producer
operator|.
name|send
argument_list|(
name|message
argument_list|,
literal|1
argument_list|,
literal|1
argument_list|,
literal|30000
argument_list|)
expr_stmt|;
comment|// producer.send(message);
name|LOG
operator|.
name|trace
argument_list|(
literal|"Sent message: "
operator|+
name|message
operator|.
name|getJMSMessageID
argument_list|()
operator|+
literal|" with expiry 30 sec"
argument_list|)
expr_stmt|;
block|}
comment|// Produce msgs that will expire quickly
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
literal|44
condition|;
name|i
operator|++
control|)
block|{
name|message
operator|=
name|session
operator|.
name|createTextMessage
argument_list|(
name|text
argument_list|)
expr_stmt|;
name|producer
operator|.
name|send
argument_list|(
name|message
argument_list|,
literal|1
argument_list|,
literal|1
argument_list|,
literal|10
argument_list|)
expr_stmt|;
name|LOG
operator|.
name|trace
argument_list|(
literal|"Sent message: "
operator|+
name|message
operator|.
name|getJMSMessageID
argument_list|()
operator|+
literal|" with expiry 10 msec"
argument_list|)
expr_stmt|;
block|}
comment|// Produce some moremsgs that don't expire
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
literal|4
condition|;
name|i
operator|++
control|)
block|{
name|message
operator|=
name|session
operator|.
name|createTextMessage
argument_list|(
name|text
argument_list|)
expr_stmt|;
name|producer
operator|.
name|send
argument_list|(
name|message
argument_list|,
literal|1
argument_list|,
literal|1
argument_list|,
literal|30000
argument_list|)
expr_stmt|;
comment|// producer.send(message);
name|LOG
operator|.
name|trace
argument_list|(
literal|"Sent message: "
operator|+
name|message
operator|.
name|getJMSMessageID
argument_list|()
operator|+
literal|" with expiry 30 sec"
argument_list|)
expr_stmt|;
block|}
name|sleep
argument_list|(
literal|200
argument_list|)
expr_stmt|;
name|int
name|counter
init|=
literal|1
decl_stmt|;
for|for
control|(
init|;
name|counter
operator|<=
literal|60
condition|;
operator|++
name|counter
control|)
block|{
name|assertNotNull
argument_list|(
name|consumer
operator|.
name|receive
argument_list|(
literal|2000
argument_list|)
argument_list|)
expr_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"counter at "
operator|+
name|counter
argument_list|)
expr_stmt|;
block|}
name|LOG
operator|.
name|info
argument_list|(
literal|"Received all expected messages with counter at: "
operator|+
name|counter
argument_list|)
expr_stmt|;
comment|// Cleanup
name|producer
operator|.
name|close
argument_list|()
expr_stmt|;
name|consumer
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
block|}
specifier|private
name|void
name|sleep
parameter_list|(
name|int
name|milliSecondTime
parameter_list|)
block|{
try|try
block|{
name|Thread
operator|.
name|sleep
argument_list|(
name|milliSecondTime
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|InterruptedException
name|igonred
parameter_list|)
block|{         }
block|}
comment|/**      * Standard JMS MessageListener      */
specifier|private
class|class
name|MyMessageListener
implements|implements
name|MessageListener
implements|,
name|ExceptionListener
block|{
specifier|private
name|AtomicInteger
name|counter
init|=
operator|new
name|AtomicInteger
argument_list|(
literal|0
argument_list|)
decl_stmt|;
specifier|public
name|void
name|onMessage
parameter_list|(
specifier|final
name|Message
name|message
parameter_list|)
block|{
try|try
block|{
name|LOG
operator|.
name|trace
argument_list|(
literal|"Got Message "
operator|+
name|message
operator|.
name|getJMSMessageID
argument_list|()
argument_list|)
expr_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"counter at "
operator|+
name|counter
operator|.
name|incrementAndGet
argument_list|()
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
specifier|final
name|Exception
name|e
parameter_list|)
block|{             }
block|}
specifier|public
name|int
name|getCounter
parameter_list|()
block|{
return|return
name|counter
operator|.
name|get
argument_list|()
return|;
block|}
specifier|public
specifier|synchronized
name|void
name|onException
parameter_list|(
name|JMSException
name|ex
parameter_list|)
block|{
name|LOG
operator|.
name|error
argument_list|(
literal|"JMS Exception occured.  Shutting down client."
argument_list|)
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

