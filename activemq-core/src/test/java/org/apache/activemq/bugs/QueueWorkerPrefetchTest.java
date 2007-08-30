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
import|import
name|java
operator|.
name|io
operator|.
name|Serializable
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
name|Queue
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
name|MessageConsumer
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
name|ActiveMQPrefetchPolicy
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
name|junit
operator|.
name|framework
operator|.
name|TestCase
import|;
end_import

begin_comment
comment|/**  * Test case demonstrating situation where messages are not delivered to consumers.  */
end_comment

begin_class
specifier|public
class|class
name|QueueWorkerPrefetchTest
extends|extends
name|TestCase
implements|implements
name|MessageListener
block|{
comment|/** The connection URL. */
specifier|private
specifier|static
specifier|final
name|String
name|CONNECTION_URL
init|=
literal|"tcp://localhost:61616"
decl_stmt|;
comment|/** The queue prefetch size to use. A value greater than 1 seems to make things work. */
specifier|private
specifier|static
specifier|final
name|int
name|QUEUE_PREFETCH_SIZE
init|=
literal|1
decl_stmt|;
comment|/** The number of workers to use.  A single worker with a prefetch of 1 works. */
specifier|private
specifier|static
specifier|final
name|int
name|NUM_WORKERS
init|=
literal|2
decl_stmt|;
comment|/** Embedded JMS broker. */
specifier|private
name|BrokerService
name|broker
decl_stmt|;
comment|/** The master's producer object for creating work items. */
specifier|private
name|MessageProducer
name|workItemProducer
decl_stmt|;
comment|/** The master's consumer object for consuming ack messages from workers. */
specifier|private
name|MessageConsumer
name|masterItemConsumer
decl_stmt|;
comment|/** The number of acks received by the master. */
specifier|private
name|long
name|acksReceived
decl_stmt|;
comment|/** The expected number of acks the master should receive. */
specifier|private
name|long
name|expectedCount
decl_stmt|;
comment|/** Messages sent to the work-item queue. */
specifier|private
specifier|static
class|class
name|WorkMessage
implements|implements
name|Serializable
block|{     }
comment|/**      * The worker process.  Consume messages from the work-item queue, possibly creating      * more messages to submit to the work-item queue.  For each work item, send an ack      * to the master.      */
specifier|private
specifier|static
class|class
name|Worker
implements|implements
name|MessageListener
block|{
comment|/** Counter shared between workers to decided when new work-item messages are created. */
specifier|private
specifier|static
name|Integer
name|counter
init|=
operator|new
name|Integer
argument_list|(
literal|0
argument_list|)
decl_stmt|;
comment|/** Session to use. */
specifier|private
name|Session
name|session
decl_stmt|;
comment|/** Producer for sending ack messages to the master. */
specifier|private
name|MessageProducer
name|masterItemProducer
decl_stmt|;
comment|/** Producer for sending new work items to the work-items queue. */
specifier|private
name|MessageProducer
name|workItemProducer
decl_stmt|;
specifier|public
name|Worker
parameter_list|(
name|Session
name|session
parameter_list|)
throws|throws
name|JMSException
block|{
name|this
operator|.
name|session
operator|=
name|session
expr_stmt|;
name|masterItemProducer
operator|=
name|session
operator|.
name|createProducer
argument_list|(
name|session
operator|.
name|createQueue
argument_list|(
literal|"master-item"
argument_list|)
argument_list|)
expr_stmt|;
name|Queue
name|workItemQueue
init|=
name|session
operator|.
name|createQueue
argument_list|(
literal|"work-item"
argument_list|)
decl_stmt|;
name|workItemProducer
operator|=
name|session
operator|.
name|createProducer
argument_list|(
name|workItemQueue
argument_list|)
expr_stmt|;
name|MessageConsumer
name|workItemConsumer
init|=
name|session
operator|.
name|createConsumer
argument_list|(
name|workItemQueue
argument_list|)
decl_stmt|;
name|workItemConsumer
operator|.
name|setMessageListener
argument_list|(
name|this
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|onMessage
parameter_list|(
name|javax
operator|.
name|jms
operator|.
name|Message
name|message
parameter_list|)
block|{
try|try
block|{
name|boolean
name|sendMessage
init|=
literal|false
decl_stmt|;
comment|// Don't create a new work item for every 1000th message. */
synchronized|synchronized
init|(
name|counter
init|)
block|{
name|counter
operator|++
expr_stmt|;
if|if
condition|(
name|counter
operator|%
literal|1000
operator|!=
literal|0
condition|)
block|{
name|sendMessage
operator|=
literal|true
expr_stmt|;
block|}
block|}
if|if
condition|(
name|sendMessage
condition|)
block|{
comment|// Send new work item to work-item queue.
name|workItemProducer
operator|.
name|send
argument_list|(
name|session
operator|.
name|createObjectMessage
argument_list|(
operator|new
name|WorkMessage
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|// Send ack to master.
name|masterItemProducer
operator|.
name|send
argument_list|(
name|session
operator|.
name|createObjectMessage
argument_list|(
operator|new
name|WorkMessage
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|JMSException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|IllegalStateException
argument_list|(
literal|"Something has gone wrong"
argument_list|,
name|e
argument_list|)
throw|;
block|}
block|}
comment|/** Close of JMS resources used by worker. */
specifier|public
name|void
name|close
parameter_list|()
throws|throws
name|JMSException
block|{
name|masterItemProducer
operator|.
name|close
argument_list|()
expr_stmt|;
name|workItemProducer
operator|.
name|close
argument_list|()
expr_stmt|;
name|session
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
comment|/** Master message handler.  Process ack messages. */
specifier|public
specifier|synchronized
name|void
name|onMessage
parameter_list|(
name|javax
operator|.
name|jms
operator|.
name|Message
name|message
parameter_list|)
block|{
name|acksReceived
operator|++
expr_stmt|;
if|if
condition|(
name|acksReceived
operator|==
name|expectedCount
condition|)
block|{
comment|// If expected number of acks are received, wake up the main process.
name|notify
argument_list|()
expr_stmt|;
block|}
if|if
condition|(
name|acksReceived
operator|%
literal|100
operator|==
literal|0
condition|)
block|{
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"Master now has ack count of: "
operator|+
name|acksReceived
argument_list|)
expr_stmt|;
block|}
block|}
specifier|protected
name|void
name|setUp
parameter_list|()
throws|throws
name|Exception
block|{
comment|// Create the message broker.
name|super
operator|.
name|setUp
argument_list|()
expr_stmt|;
name|broker
operator|=
operator|new
name|BrokerService
argument_list|()
expr_stmt|;
name|broker
operator|.
name|setPersistent
argument_list|(
literal|false
argument_list|)
expr_stmt|;
name|broker
operator|.
name|setUseJmx
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|broker
operator|.
name|addConnector
argument_list|(
name|CONNECTION_URL
argument_list|)
expr_stmt|;
name|broker
operator|.
name|start
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
comment|// Shut down the message broker.
name|broker
operator|.
name|deleteAllMessages
argument_list|()
expr_stmt|;
name|broker
operator|.
name|stop
argument_list|()
expr_stmt|;
name|super
operator|.
name|tearDown
argument_list|()
expr_stmt|;
block|}
specifier|public
specifier|synchronized
name|void
name|testActiveMQ
parameter_list|()
throws|throws
name|Exception
block|{
comment|// Create the connection to the broker.
name|ActiveMQConnectionFactory
name|connectionFactory
init|=
operator|new
name|ActiveMQConnectionFactory
argument_list|(
name|CONNECTION_URL
argument_list|)
decl_stmt|;
name|ActiveMQPrefetchPolicy
name|prefetchPolicy
init|=
operator|new
name|ActiveMQPrefetchPolicy
argument_list|()
decl_stmt|;
name|prefetchPolicy
operator|.
name|setQueuePrefetch
argument_list|(
name|QUEUE_PREFETCH_SIZE
argument_list|)
expr_stmt|;
name|connectionFactory
operator|.
name|setPrefetchPolicy
argument_list|(
name|prefetchPolicy
argument_list|)
expr_stmt|;
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
name|masterSession
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
name|workItemProducer
operator|=
name|masterSession
operator|.
name|createProducer
argument_list|(
name|masterSession
operator|.
name|createQueue
argument_list|(
literal|"work-item"
argument_list|)
argument_list|)
expr_stmt|;
name|masterItemConsumer
operator|=
name|masterSession
operator|.
name|createConsumer
argument_list|(
name|masterSession
operator|.
name|createQueue
argument_list|(
literal|"master-item"
argument_list|)
argument_list|)
expr_stmt|;
name|masterItemConsumer
operator|.
name|setMessageListener
argument_list|(
name|this
argument_list|)
expr_stmt|;
comment|// Create the workers.
name|Worker
index|[]
name|workers
init|=
operator|new
name|Worker
index|[
name|NUM_WORKERS
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
name|NUM_WORKERS
condition|;
name|i
operator|++
control|)
block|{
name|workers
index|[
name|i
index|]
operator|=
operator|new
name|Worker
argument_list|(
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
argument_list|)
expr_stmt|;
block|}
comment|// Send a message to the work queue, and wait for the 1000 acks from the workers.
name|expectedCount
operator|=
literal|1000
expr_stmt|;
name|acksReceived
operator|=
literal|0
expr_stmt|;
name|workItemProducer
operator|.
name|send
argument_list|(
name|masterSession
operator|.
name|createObjectMessage
argument_list|(
operator|new
name|WorkMessage
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
while|while
condition|(
name|acksReceived
operator|!=
name|expectedCount
condition|)
block|{
name|wait
argument_list|()
expr_stmt|;
block|}
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"First batch received"
argument_list|)
expr_stmt|;
comment|// Send another message to the work queue, and wait for the next 1000 acks.  It is
comment|// at this point where the workers never get notified of this message, as they
comment|// have a large pending queue.  Creating a new worker at this point however will
comment|// receive this new message.
name|expectedCount
operator|=
literal|2000
expr_stmt|;
name|workItemProducer
operator|.
name|send
argument_list|(
name|masterSession
operator|.
name|createObjectMessage
argument_list|(
operator|new
name|WorkMessage
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
while|while
condition|(
name|acksReceived
operator|!=
name|expectedCount
condition|)
block|{
name|wait
argument_list|()
expr_stmt|;
block|}
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"Second batch received"
argument_list|)
expr_stmt|;
comment|// Cleanup all JMS resources.
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|NUM_WORKERS
condition|;
name|i
operator|++
control|)
block|{
name|workers
index|[
name|i
index|]
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
name|masterSession
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
block|}
end_class

end_unit

