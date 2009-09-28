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
name|File
import|;
end_import

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
name|Semaphore
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
name|TimeoutException
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
name|AtomicBoolean
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
name|DeliveryMode
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
name|VMPendingQueueMessageStoragePolicy
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
name|junit
operator|.
name|framework
operator|.
name|TestCase
import|;
end_import

begin_comment
comment|/**  * An AMQ-1282 Test  *   */
end_comment

begin_class
specifier|public
class|class
name|AMQ2413Test
extends|extends
name|CombinationTestSupport
implements|implements
name|MessageListener
block|{
name|BrokerService
name|broker
decl_stmt|;
specifier|private
name|ActiveMQConnectionFactory
name|factory
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|int
name|HANG_THRESHOLD
init|=
literal|5
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|int
name|SEND_COUNT
init|=
literal|10000
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|int
name|RECEIVER_THINK_TIME
init|=
literal|1
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|int
name|CONSUMER_COUNT
init|=
literal|1
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|int
name|PRODUCER_COUNT
init|=
literal|50
decl_stmt|;
specifier|public
name|int
name|deliveryMode
init|=
name|DeliveryMode
operator|.
name|NON_PERSISTENT
decl_stmt|;
specifier|public
name|int
name|ackMode
init|=
name|Session
operator|.
name|DUPS_OK_ACKNOWLEDGE
decl_stmt|;
specifier|public
name|boolean
name|useVMCursor
init|=
literal|false
decl_stmt|;
specifier|public
name|boolean
name|useOptimizeAcks
init|=
literal|false
decl_stmt|;
specifier|private
name|ArrayList
argument_list|<
name|Service
argument_list|>
name|services
init|=
operator|new
name|ArrayList
argument_list|<
name|Service
argument_list|>
argument_list|(
name|CONSUMER_COUNT
operator|+
name|PRODUCER_COUNT
argument_list|)
decl_stmt|;
name|AtomicInteger
name|count
init|=
operator|new
name|AtomicInteger
argument_list|(
literal|0
argument_list|)
decl_stmt|;
name|Semaphore
name|receivedMessages
decl_stmt|;
name|AtomicBoolean
name|running
init|=
operator|new
name|AtomicBoolean
argument_list|(
literal|false
argument_list|)
decl_stmt|;
specifier|public
name|void
name|initCombos
parameter_list|()
block|{
name|addCombinationValues
argument_list|(
literal|"deliveryMode"
argument_list|,
operator|new
name|Object
index|[]
block|{
name|DeliveryMode
operator|.
name|PERSISTENT
block|,
name|DeliveryMode
operator|.
name|NON_PERSISTENT
block|}
argument_list|)
expr_stmt|;
name|addCombinationValues
argument_list|(
literal|"ackMode"
argument_list|,
operator|new
name|Object
index|[]
block|{
name|Session
operator|.
name|DUPS_OK_ACKNOWLEDGE
block|,
name|Session
operator|.
name|AUTO_ACKNOWLEDGE
block|}
argument_list|)
expr_stmt|;
name|addCombinationValues
argument_list|(
literal|"useVMCursor"
argument_list|,
operator|new
name|Object
index|[]
block|{
literal|true
block|,
literal|false
block|}
argument_list|)
expr_stmt|;
comment|//addCombinationValues("useOptimizeAcks", new Object[] {true, false});
block|}
specifier|protected
name|void
name|setUp
parameter_list|()
throws|throws
name|Exception
block|{
name|broker
operator|=
operator|new
name|BrokerService
argument_list|()
expr_stmt|;
name|broker
operator|.
name|setDataDirectory
argument_list|(
literal|"target"
operator|+
name|File
operator|.
name|separator
operator|+
literal|"test-data"
operator|+
name|File
operator|.
name|separator
operator|+
literal|"AMQ2401Test"
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
name|addConnector
argument_list|(
literal|"tcp://0.0.0.0:2401"
argument_list|)
expr_stmt|;
name|PolicyMap
name|policies
init|=
operator|new
name|PolicyMap
argument_list|()
decl_stmt|;
name|PolicyEntry
name|entry
init|=
operator|new
name|PolicyEntry
argument_list|()
decl_stmt|;
name|entry
operator|.
name|setMemoryLimit
argument_list|(
literal|1024
operator|*
literal|1024
argument_list|)
expr_stmt|;
name|entry
operator|.
name|setProducerFlowControl
argument_list|(
literal|true
argument_list|)
expr_stmt|;
if|if
condition|(
name|useVMCursor
condition|)
block|{
name|entry
operator|.
name|setPendingQueuePolicy
argument_list|(
operator|new
name|VMPendingQueueMessageStoragePolicy
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|entry
operator|.
name|setQueue
argument_list|(
literal|">"
argument_list|)
expr_stmt|;
name|policies
operator|.
name|setDefaultEntry
argument_list|(
name|entry
argument_list|)
expr_stmt|;
name|broker
operator|.
name|setDestinationPolicy
argument_list|(
name|policies
argument_list|)
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
name|count
operator|.
name|set
argument_list|(
literal|0
argument_list|)
expr_stmt|;
name|receivedMessages
operator|=
operator|new
name|Semaphore
argument_list|(
literal|0
argument_list|)
expr_stmt|;
name|factory
operator|=
operator|new
name|ActiveMQConnectionFactory
argument_list|(
literal|"tcp://0.0.0.0:2401"
argument_list|)
expr_stmt|;
comment|//factory = new ActiveMQConnectionFactory("vm://localhost?broker.useJmx=false&broker.persistent=false");
name|super
operator|.
name|setUp
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
name|running
operator|.
name|set
argument_list|(
literal|false
argument_list|)
expr_stmt|;
for|for
control|(
name|Service
name|service
range|:
name|services
control|)
block|{
name|service
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
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
name|super
operator|.
name|tearDown
argument_list|()
expr_stmt|;
block|}
specifier|public
name|void
name|initCombosForTestFoo
parameter_list|()
block|{
name|addCombinationValues
argument_list|(
literal|"age"
argument_list|,
operator|new
name|Object
index|[]
block|{
operator|new
name|Integer
argument_list|(
literal|21
argument_list|)
block|,
operator|new
name|Integer
argument_list|(
literal|30
argument_list|)
block|}
argument_list|)
expr_stmt|;
name|addCombinationValues
argument_list|(
literal|"color"
argument_list|,
operator|new
name|Object
index|[]
block|{
literal|"blue"
block|,
literal|"green"
block|}
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|testReceipt
parameter_list|()
throws|throws
name|Exception
block|{
name|running
operator|.
name|set
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|TestProducer
name|p
init|=
literal|null
decl_stmt|;
name|TestConsumer
name|c
init|=
literal|null
decl_stmt|;
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
name|CONSUMER_COUNT
condition|;
name|i
operator|++
control|)
block|{
name|TestConsumer
name|consumer
init|=
operator|new
name|TestConsumer
argument_list|()
decl_stmt|;
name|consumer
operator|.
name|start
argument_list|()
expr_stmt|;
name|services
operator|.
name|add
argument_list|(
name|consumer
argument_list|)
expr_stmt|;
block|}
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|PRODUCER_COUNT
condition|;
name|i
operator|++
control|)
block|{
name|TestProducer
name|producer
init|=
operator|new
name|TestProducer
argument_list|(
name|i
argument_list|)
decl_stmt|;
name|producer
operator|.
name|start
argument_list|()
expr_stmt|;
name|services
operator|.
name|add
argument_list|(
name|producer
argument_list|)
expr_stmt|;
block|}
name|waitForMessageReceipt
argument_list|(
literal|300000
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
if|if
condition|(
name|p
operator|!=
literal|null
condition|)
block|{
name|p
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
if|if
condition|(
name|c
operator|!=
literal|null
condition|)
block|{
name|c
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
block|}
comment|/*      * (non-Javadoc)      *       * @see javax.jms.MessageListener#onMessage(javax.jms.Message)      */
specifier|public
name|void
name|onMessage
parameter_list|(
name|Message
name|message
parameter_list|)
block|{
name|receivedMessages
operator|.
name|release
argument_list|()
expr_stmt|;
if|if
condition|(
name|count
operator|.
name|incrementAndGet
argument_list|()
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
literal|"Received message "
operator|+
name|count
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|RECEIVER_THINK_TIME
operator|>
literal|0
condition|)
block|{
try|try
block|{
name|Thread
operator|.
name|currentThread
argument_list|()
operator|.
name|sleep
argument_list|(
name|RECEIVER_THINK_TIME
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|InterruptedException
name|e
parameter_list|)
block|{
name|Thread
operator|.
name|currentThread
argument_list|()
operator|.
name|interrupt
argument_list|()
expr_stmt|;
block|}
block|}
block|}
comment|/**      * @throws InterruptedException      * @throws TimeoutException      *       */
specifier|private
name|void
name|waitForMessageReceipt
parameter_list|(
name|long
name|timeout
parameter_list|)
throws|throws
name|InterruptedException
throws|,
name|TimeoutException
block|{
comment|// TODO Auto-generated method stub
try|try
block|{
while|while
condition|(
name|count
operator|.
name|get
argument_list|()
operator|<
name|SEND_COUNT
condition|)
block|{
if|if
condition|(
operator|!
name|receivedMessages
operator|.
name|tryAcquire
argument_list|(
name|HANG_THRESHOLD
argument_list|,
name|TimeUnit
operator|.
name|SECONDS
argument_list|)
condition|)
block|{
if|if
condition|(
name|count
operator|.
name|get
argument_list|()
operator|==
name|SEND_COUNT
condition|)
break|break;
throw|throw
operator|new
name|TimeoutException
argument_list|(
literal|"Message not received for more than "
operator|+
name|HANG_THRESHOLD
operator|+
literal|" seconds"
argument_list|)
throw|;
block|}
block|}
block|}
finally|finally
block|{
name|running
operator|.
name|set
argument_list|(
literal|false
argument_list|)
expr_stmt|;
block|}
block|}
specifier|private
interface|interface
name|Service
block|{
specifier|public
name|void
name|start
parameter_list|()
throws|throws
name|Exception
function_decl|;
specifier|public
name|void
name|close
parameter_list|()
function_decl|;
block|}
specifier|private
class|class
name|TestProducer
implements|implements
name|Runnable
implements|,
name|Service
block|{
name|Thread
name|thread
decl_stmt|;
name|BytesMessage
name|message
decl_stmt|;
name|int
name|id
decl_stmt|;
name|Connection
name|connection
decl_stmt|;
name|Session
name|session
decl_stmt|;
name|MessageProducer
name|producer
decl_stmt|;
name|TestProducer
parameter_list|(
name|int
name|id
parameter_list|)
throws|throws
name|Exception
block|{
name|this
operator|.
name|id
operator|=
name|id
expr_stmt|;
name|thread
operator|=
operator|new
name|Thread
argument_list|(
name|this
argument_list|,
literal|"TestProducer-"
operator|+
name|id
argument_list|)
expr_stmt|;
name|connection
operator|=
name|factory
operator|.
name|createConnection
argument_list|()
expr_stmt|;
name|connection
operator|.
name|start
argument_list|()
expr_stmt|;
name|session
operator|=
name|connection
operator|.
name|createSession
argument_list|(
literal|false
argument_list|,
name|Session
operator|.
name|DUPS_OK_ACKNOWLEDGE
argument_list|)
expr_stmt|;
name|producer
operator|=
name|session
operator|.
name|createProducer
argument_list|(
name|session
operator|.
name|createQueue
argument_list|(
literal|"AMQ2401Test"
argument_list|)
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|start
parameter_list|()
block|{
name|thread
operator|.
name|start
argument_list|()
expr_stmt|;
block|}
specifier|public
name|void
name|run
parameter_list|()
block|{
name|int
name|count
init|=
name|SEND_COUNT
operator|/
name|PRODUCER_COUNT
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|1
init|;
name|i
operator|<=
name|count
condition|;
name|i
operator|++
control|)
block|{
try|try
block|{
if|if
condition|(
operator|+
name|i
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
name|thread
operator|.
name|currentThread
argument_list|()
operator|.
name|getName
argument_list|()
operator|+
literal|" Sending message "
operator|+
name|i
argument_list|)
expr_stmt|;
block|}
name|message
operator|=
name|session
operator|.
name|createBytesMessage
argument_list|()
expr_stmt|;
name|message
operator|.
name|writeBytes
argument_list|(
operator|new
name|byte
index|[
literal|1024
index|]
argument_list|)
expr_stmt|;
name|producer
operator|.
name|setDeliveryMode
argument_list|(
name|deliveryMode
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
catch|catch
parameter_list|(
name|JMSException
name|jmse
parameter_list|)
block|{
name|jmse
operator|.
name|printStackTrace
argument_list|()
expr_stmt|;
break|break;
block|}
block|}
block|}
specifier|public
name|void
name|close
parameter_list|()
block|{
try|try
block|{
name|connection
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|JMSException
name|e
parameter_list|)
block|{
comment|// TODO Auto-generated catch block
name|e
operator|.
name|printStackTrace
argument_list|()
expr_stmt|;
block|}
block|}
block|}
specifier|private
class|class
name|TestConsumer
implements|implements
name|Runnable
implements|,
name|Service
block|{
name|ActiveMQConnection
name|connection
decl_stmt|;
name|Session
name|session
decl_stmt|;
name|MessageConsumer
name|consumer
decl_stmt|;
name|Thread
name|thread
decl_stmt|;
name|TestConsumer
parameter_list|()
throws|throws
name|Exception
block|{
name|factory
operator|.
name|setOptimizeAcknowledge
argument_list|(
literal|false
argument_list|)
expr_stmt|;
name|connection
operator|=
operator|(
name|ActiveMQConnection
operator|)
name|factory
operator|.
name|createConnection
argument_list|()
expr_stmt|;
if|if
condition|(
name|useOptimizeAcks
condition|)
block|{
name|connection
operator|.
name|setOptimizeAcknowledge
argument_list|(
literal|true
argument_list|)
expr_stmt|;
block|}
name|session
operator|=
name|connection
operator|.
name|createSession
argument_list|(
literal|false
argument_list|,
name|ackMode
argument_list|)
expr_stmt|;
name|consumer
operator|=
name|session
operator|.
name|createConsumer
argument_list|(
name|session
operator|.
name|createQueue
argument_list|(
literal|"AMQ2401Test"
argument_list|)
argument_list|)
expr_stmt|;
name|consumer
operator|.
name|setMessageListener
argument_list|(
name|AMQ2413Test
operator|.
name|this
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|start
parameter_list|()
throws|throws
name|Exception
block|{
name|connection
operator|.
name|start
argument_list|()
expr_stmt|;
block|}
specifier|public
name|void
name|close
parameter_list|()
block|{
try|try
block|{
name|connection
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|JMSException
name|e
parameter_list|)
block|{
comment|// TODO Auto-generated catch block
name|e
operator|.
name|printStackTrace
argument_list|()
expr_stmt|;
block|}
block|}
comment|/*          * (non-Javadoc)          *           * @see java.lang.Runnable#run()          */
specifier|public
name|void
name|run
parameter_list|()
block|{
while|while
condition|(
name|running
operator|.
name|get
argument_list|()
condition|)
block|{
try|try
block|{
name|onMessage
argument_list|(
name|consumer
operator|.
name|receive
argument_list|()
argument_list|)
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
block|}
block|}
block|}
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
name|AMQ2413Test
operator|.
name|class
argument_list|)
return|;
block|}
block|}
end_class

end_unit

