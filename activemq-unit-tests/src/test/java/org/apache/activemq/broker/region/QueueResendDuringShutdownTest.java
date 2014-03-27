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
name|region
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
import|import static
name|org
operator|.
name|junit
operator|.
name|matchers
operator|.
name|JUnitMatchers
operator|.
name|containsString
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
name|Queue
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
name|broker
operator|.
name|BrokerService
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
name|org
operator|.
name|junit
operator|.
name|*
import|;
end_import

begin_comment
comment|/**  * Confirm that the broker does not resend unacknowledged messages during a broker shutdown.  */
end_comment

begin_class
specifier|public
class|class
name|QueueResendDuringShutdownTest
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
name|QueueResendDuringShutdownTest
operator|.
name|class
argument_list|)
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|int
name|NUM_CONNECTION_TO_TEST
init|=
literal|8
decl_stmt|;
specifier|private
specifier|static
name|boolean
name|iterationFoundFailure
init|=
literal|false
decl_stmt|;
specifier|private
name|BrokerService
name|broker
decl_stmt|;
specifier|private
name|ActiveMQConnectionFactory
name|factory
decl_stmt|;
specifier|private
name|Connection
index|[]
name|connections
decl_stmt|;
specifier|private
name|Connection
name|producerConnection
decl_stmt|;
specifier|private
name|Queue
name|queue
decl_stmt|;
specifier|private
name|Object
name|messageReceiveSync
init|=
operator|new
name|Object
argument_list|()
decl_stmt|;
specifier|private
name|int
name|receiveCount
decl_stmt|;
annotation|@
name|Before
specifier|public
name|void
name|setUp
parameter_list|()
throws|throws
name|Exception
block|{
name|this
operator|.
name|receiveCount
operator|=
literal|0
expr_stmt|;
name|this
operator|.
name|broker
operator|=
operator|new
name|BrokerService
argument_list|()
expr_stmt|;
name|this
operator|.
name|broker
operator|.
name|setPersistent
argument_list|(
literal|false
argument_list|)
expr_stmt|;
name|this
operator|.
name|broker
operator|.
name|start
argument_list|()
expr_stmt|;
name|this
operator|.
name|broker
operator|.
name|waitUntilStarted
argument_list|()
expr_stmt|;
name|this
operator|.
name|factory
operator|=
operator|new
name|ActiveMQConnectionFactory
argument_list|(
name|broker
operator|.
name|getVmConnectorURI
argument_list|()
argument_list|)
expr_stmt|;
name|this
operator|.
name|queue
operator|=
operator|new
name|ActiveMQQueue
argument_list|(
literal|"TESTQUEUE"
argument_list|)
expr_stmt|;
name|connections
operator|=
operator|new
name|Connection
index|[
name|NUM_CONNECTION_TO_TEST
index|]
expr_stmt|;
name|int
name|iter
init|=
literal|0
decl_stmt|;
while|while
condition|(
name|iter
operator|<
name|NUM_CONNECTION_TO_TEST
condition|)
block|{
name|this
operator|.
name|connections
index|[
name|iter
index|]
operator|=
name|factory
operator|.
name|createConnection
argument_list|()
expr_stmt|;
name|iter
operator|++
expr_stmt|;
block|}
name|this
operator|.
name|producerConnection
operator|=
name|factory
operator|.
name|createConnection
argument_list|()
expr_stmt|;
name|this
operator|.
name|producerConnection
operator|.
name|start
argument_list|()
expr_stmt|;
block|}
annotation|@
name|After
specifier|public
name|void
name|cleanup
parameter_list|()
throws|throws
name|Exception
block|{
for|for
control|(
name|Connection
name|oneConnection
range|:
name|connections
control|)
block|{
if|if
condition|(
name|oneConnection
operator|!=
literal|null
condition|)
block|{
name|closeConnection
argument_list|(
name|oneConnection
argument_list|)
expr_stmt|;
block|}
block|}
name|connections
operator|=
literal|null
expr_stmt|;
if|if
condition|(
name|this
operator|.
name|producerConnection
operator|!=
literal|null
condition|)
block|{
name|closeConnection
argument_list|(
name|this
operator|.
name|producerConnection
argument_list|)
expr_stmt|;
name|this
operator|.
name|producerConnection
operator|=
literal|null
expr_stmt|;
block|}
name|this
operator|.
name|broker
operator|.
name|stop
argument_list|()
expr_stmt|;
name|this
operator|.
name|broker
operator|.
name|waitUntilStopped
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
argument_list|(
name|timeout
operator|=
literal|3000
argument_list|)
specifier|public
name|void
name|testRedeliverAtBrokerShutdownAutoAckMsgListenerIter1
parameter_list|()
throws|throws
name|Throwable
block|{
name|runTestIteration
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
argument_list|(
name|timeout
operator|=
literal|3000
argument_list|)
specifier|public
name|void
name|testRedeliverAtBrokerShutdownAutoAckMsgListenerIter2
parameter_list|()
throws|throws
name|Throwable
block|{
name|runTestIteration
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
argument_list|(
name|timeout
operator|=
literal|3000
argument_list|)
specifier|public
name|void
name|testRedeliverAtBrokerShutdownAutoAckMsgListenerIter3
parameter_list|()
throws|throws
name|Throwable
block|{
name|runTestIteration
argument_list|()
expr_stmt|;
block|}
comment|/**      * Run one iteration of the test, skipping it if a failure was found on a prior iteration since a single failure is      * enough.  Also keep track of the state of failure for the iteration.      */
specifier|protected
name|void
name|runTestIteration
parameter_list|()
throws|throws
name|Throwable
block|{
if|if
condition|(
name|iterationFoundFailure
condition|)
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"skipping test iteration; failure previously detected"
argument_list|)
expr_stmt|;
return|return;
block|}
try|try
block|{
name|testRedeliverAtBrokerShutdownAutoAckMsgListener
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Throwable
name|thrown
parameter_list|)
block|{
name|iterationFoundFailure
operator|=
literal|true
expr_stmt|;
throw|throw
name|thrown
throw|;
block|}
block|}
specifier|protected
name|void
name|testRedeliverAtBrokerShutdownAutoAckMsgListener
parameter_list|()
throws|throws
name|Exception
block|{
comment|// Start consumers on all of the connections
for|for
control|(
name|Connection
name|oneConnection
range|:
name|connections
control|)
block|{
name|MessageConsumer
name|consumer
init|=
name|startupConsumer
argument_list|(
name|oneConnection
argument_list|,
literal|false
argument_list|,
name|Session
operator|.
name|AUTO_ACKNOWLEDGE
argument_list|)
decl_stmt|;
name|configureMessageListener
argument_list|(
name|consumer
argument_list|)
expr_stmt|;
name|oneConnection
operator|.
name|start
argument_list|()
expr_stmt|;
block|}
comment|// Send one message to the Queue and wait a short time for the dispatch to occur.
name|this
operator|.
name|sendMessage
argument_list|()
expr_stmt|;
name|waitForMessage
argument_list|(
literal|1000
argument_list|)
expr_stmt|;
comment|// Verify one consumer received it
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|this
operator|.
name|receiveCount
argument_list|)
expr_stmt|;
comment|// Shutdown the broker
name|this
operator|.
name|broker
operator|.
name|stop
argument_list|()
expr_stmt|;
name|this
operator|.
name|broker
operator|.
name|waitUntilStopped
argument_list|()
expr_stmt|;
name|delay
argument_list|(
literal|100
argument_list|,
literal|"give queue time flush"
argument_list|)
expr_stmt|;
comment|// Verify still only one consumer received it
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|this
operator|.
name|receiveCount
argument_list|)
expr_stmt|;
block|}
comment|/**      * Start a consumer on the given connection using the session transaction and acknowledge settings given.      */
specifier|protected
name|MessageConsumer
name|startupConsumer
parameter_list|(
name|Connection
name|conn
parameter_list|,
name|boolean
name|transInd
parameter_list|,
name|int
name|ackMode
parameter_list|)
throws|throws
name|JMSException
block|{
name|Session
name|sess
decl_stmt|;
name|MessageConsumer
name|consumer
decl_stmt|;
name|sess
operator|=
name|conn
operator|.
name|createSession
argument_list|(
name|transInd
argument_list|,
name|ackMode
argument_list|)
expr_stmt|;
name|consumer
operator|=
name|sess
operator|.
name|createConsumer
argument_list|(
name|queue
argument_list|)
expr_stmt|;
return|return
name|consumer
return|;
block|}
comment|/**      * Mark the receipt of a message from one of the consumers.      */
specifier|protected
name|void
name|messageReceived
parameter_list|()
block|{
synchronized|synchronized
init|(
name|this
init|)
block|{
name|this
operator|.
name|receiveCount
operator|++
expr_stmt|;
synchronized|synchronized
init|(
name|this
operator|.
name|messageReceiveSync
init|)
block|{
name|this
operator|.
name|messageReceiveSync
operator|.
name|notifyAll
argument_list|()
expr_stmt|;
block|}
block|}
block|}
comment|/**      * Setup the MessageListener for the given consumer.  The listener uses a long delay on receiving the message to      * simulate the reported case of problems at shutdown caused by a message listener's connection closing while it is      * still processing.      */
specifier|protected
name|void
name|configureMessageListener
parameter_list|(
name|MessageConsumer
name|consumer
parameter_list|)
throws|throws
name|JMSException
block|{
specifier|final
name|MessageConsumer
name|fConsumer
init|=
name|consumer
decl_stmt|;
name|consumer
operator|.
name|setMessageListener
argument_list|(
operator|new
name|MessageListener
argument_list|()
block|{
specifier|public
name|void
name|onMessage
parameter_list|(
name|Message
name|msg
parameter_list|)
block|{
name|LOG
operator|.
name|debug
argument_list|(
literal|"got a message on consumer {}"
argument_list|,
name|fConsumer
argument_list|)
expr_stmt|;
name|messageReceived
argument_list|()
expr_stmt|;
comment|// Delay long enough for the consumer to get closed while this delay is active.
name|delay
argument_list|(
literal|3000
argument_list|,
literal|"pause so connection shutdown leads to unacked message redelivery"
argument_list|)
expr_stmt|;
block|}
block|}
argument_list|)
expr_stmt|;
block|}
comment|/**      * Send a test message now.      */
specifier|protected
name|void
name|sendMessage
parameter_list|()
throws|throws
name|JMSException
block|{
name|Session
name|sess
init|=
name|this
operator|.
name|producerConnection
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
name|queue
argument_list|)
decl_stmt|;
name|prod
operator|.
name|send
argument_list|(
name|sess
operator|.
name|createTextMessage
argument_list|(
literal|"X-TEST-MSG-X"
argument_list|)
argument_list|)
expr_stmt|;
name|prod
operator|.
name|close
argument_list|()
expr_stmt|;
name|sess
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
comment|/**      * Close the given connection safely and log any exception caught.      */
specifier|protected
name|void
name|closeConnection
parameter_list|(
name|Connection
name|conn
parameter_list|)
block|{
try|try
block|{
name|conn
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|JMSException
name|jmsExc
parameter_list|)
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"failed to cleanup connection"
argument_list|,
name|jmsExc
argument_list|)
expr_stmt|;
block|}
block|}
comment|/**      * Pause for the given length of time, in milliseconds, logging an interruption if one occurs.  Don't try to      * recover from interrupt - the test case does not support interrupting and such an occurrence likely means the      * test is being aborted.      */
specifier|protected
name|void
name|delay
parameter_list|(
name|long
name|delayMs
parameter_list|,
name|String
name|desc
parameter_list|)
block|{
try|try
block|{
name|Thread
operator|.
name|sleep
argument_list|(
name|delayMs
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|InterruptedException
name|intExc
parameter_list|)
block|{
name|LOG
operator|.
name|warn
argument_list|(
literal|"sleep interrupted: "
operator|+
name|desc
argument_list|,
name|intExc
argument_list|)
expr_stmt|;
block|}
block|}
comment|/**      * Wait up to the specified duration for a message to be received by any consumer.      */
specifier|protected
name|void
name|waitForMessage
parameter_list|(
name|long
name|delayMs
parameter_list|)
block|{
try|try
block|{
synchronized|synchronized
init|(
name|this
operator|.
name|messageReceiveSync
init|)
block|{
if|if
condition|(
name|this
operator|.
name|receiveCount
operator|==
literal|0
condition|)
block|{
name|this
operator|.
name|messageReceiveSync
operator|.
name|wait
argument_list|(
name|delayMs
argument_list|)
expr_stmt|;
block|}
block|}
block|}
catch|catch
parameter_list|(
name|InterruptedException
name|intExc
parameter_list|)
block|{
name|LOG
operator|.
name|warn
argument_list|(
literal|"sleep interrupted: wait for message to arrive"
argument_list|)
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

