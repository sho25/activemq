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
package|;
end_package

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Random
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
name|Executor
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
name|SynchronousQueue
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
name|ThreadPoolExecutor
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
name|javax
operator|.
name|jms
operator|.
name|Topic
import|;
end_import

begin_comment
comment|/**  *   */
end_comment

begin_class
specifier|public
class|class
name|JmsConnectionStartStopTest
extends|extends
name|TestSupport
block|{
specifier|private
specifier|static
specifier|final
name|org
operator|.
name|apache
operator|.
name|commons
operator|.
name|logging
operator|.
name|Log
name|LOG
init|=
name|org
operator|.
name|apache
operator|.
name|commons
operator|.
name|logging
operator|.
name|LogFactory
operator|.
name|getLog
argument_list|(
name|JmsConnectionStartStopTest
operator|.
name|class
argument_list|)
decl_stmt|;
specifier|private
name|Connection
name|startedConnection
decl_stmt|;
specifier|private
name|Connection
name|stoppedConnection
decl_stmt|;
comment|/**      * @see junit.framework.TestCase#setUp()      */
specifier|protected
name|void
name|setUp
parameter_list|()
throws|throws
name|Exception
block|{
name|LOG
operator|.
name|info
argument_list|(
name|getClass
argument_list|()
operator|.
name|getClassLoader
argument_list|()
operator|.
name|getResource
argument_list|(
literal|"log4j.properties"
argument_list|)
argument_list|)
expr_stmt|;
name|ActiveMQConnectionFactory
name|factory
init|=
name|createConnectionFactory
argument_list|()
decl_stmt|;
name|startedConnection
operator|=
name|factory
operator|.
name|createConnection
argument_list|()
expr_stmt|;
name|startedConnection
operator|.
name|start
argument_list|()
expr_stmt|;
name|stoppedConnection
operator|=
name|factory
operator|.
name|createConnection
argument_list|()
expr_stmt|;
block|}
comment|/**      * @see junit.framework.TestCase#tearDown()      */
specifier|protected
name|void
name|tearDown
parameter_list|()
throws|throws
name|Exception
block|{
name|stoppedConnection
operator|.
name|close
argument_list|()
expr_stmt|;
name|startedConnection
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
comment|/**      * Tests if the consumer receives the messages that were sent before the      * connection was started.      *       * @throws JMSException      */
specifier|public
name|void
name|testStoppedConsumerHoldsMessagesTillStarted
parameter_list|()
throws|throws
name|JMSException
block|{
name|Session
name|startedSession
init|=
name|startedConnection
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
name|Session
name|stoppedSession
init|=
name|stoppedConnection
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
comment|// Setup the consumers.
name|Topic
name|topic
init|=
name|startedSession
operator|.
name|createTopic
argument_list|(
literal|"test"
argument_list|)
decl_stmt|;
name|MessageConsumer
name|startedConsumer
init|=
name|startedSession
operator|.
name|createConsumer
argument_list|(
name|topic
argument_list|)
decl_stmt|;
name|MessageConsumer
name|stoppedConsumer
init|=
name|stoppedSession
operator|.
name|createConsumer
argument_list|(
name|topic
argument_list|)
decl_stmt|;
comment|// Send the message.
name|MessageProducer
name|producer
init|=
name|startedSession
operator|.
name|createProducer
argument_list|(
name|topic
argument_list|)
decl_stmt|;
name|TextMessage
name|message
init|=
name|startedSession
operator|.
name|createTextMessage
argument_list|(
literal|"Hello"
argument_list|)
decl_stmt|;
name|producer
operator|.
name|send
argument_list|(
name|message
argument_list|)
expr_stmt|;
comment|// Test the assertions.
name|Message
name|m
init|=
name|startedConsumer
operator|.
name|receive
argument_list|(
literal|1000
argument_list|)
decl_stmt|;
name|assertNotNull
argument_list|(
name|m
argument_list|)
expr_stmt|;
name|m
operator|=
name|stoppedConsumer
operator|.
name|receive
argument_list|(
literal|1000
argument_list|)
expr_stmt|;
name|assertNull
argument_list|(
name|m
argument_list|)
expr_stmt|;
name|stoppedConnection
operator|.
name|start
argument_list|()
expr_stmt|;
name|m
operator|=
name|stoppedConsumer
operator|.
name|receive
argument_list|(
literal|5000
argument_list|)
expr_stmt|;
name|assertNotNull
argument_list|(
name|m
argument_list|)
expr_stmt|;
name|startedSession
operator|.
name|close
argument_list|()
expr_stmt|;
name|stoppedSession
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
comment|/**      * Tests if the consumer is able to receive messages eveb when the      * connecction restarts multiple times.      *       * @throws Exception      */
specifier|public
name|void
name|testMultipleConnectionStops
parameter_list|()
throws|throws
name|Exception
block|{
name|testStoppedConsumerHoldsMessagesTillStarted
argument_list|()
expr_stmt|;
name|stoppedConnection
operator|.
name|stop
argument_list|()
expr_stmt|;
name|testStoppedConsumerHoldsMessagesTillStarted
argument_list|()
expr_stmt|;
name|stoppedConnection
operator|.
name|stop
argument_list|()
expr_stmt|;
name|testStoppedConsumerHoldsMessagesTillStarted
argument_list|()
expr_stmt|;
block|}
specifier|public
name|void
name|testConcurrentSessionCreateWithStart
parameter_list|()
throws|throws
name|Exception
block|{
name|ThreadPoolExecutor
name|executor
init|=
operator|new
name|ThreadPoolExecutor
argument_list|(
literal|50
argument_list|,
name|Integer
operator|.
name|MAX_VALUE
argument_list|,
literal|60L
argument_list|,
name|TimeUnit
operator|.
name|SECONDS
argument_list|,
operator|new
name|SynchronousQueue
argument_list|<
name|Runnable
argument_list|>
argument_list|()
argument_list|)
decl_stmt|;
specifier|final
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
specifier|final
name|Random
name|rand
init|=
operator|new
name|Random
argument_list|()
decl_stmt|;
name|Runnable
name|createSessionTask
init|=
operator|new
name|Runnable
argument_list|()
block|{
specifier|public
name|void
name|run
parameter_list|()
block|{
try|try
block|{
name|TimeUnit
operator|.
name|MILLISECONDS
operator|.
name|sleep
argument_list|(
name|rand
operator|.
name|nextInt
argument_list|(
literal|10
argument_list|)
argument_list|)
expr_stmt|;
name|stoppedConnection
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
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
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
name|Runnable
name|startStopTask
init|=
operator|new
name|Runnable
argument_list|()
block|{
specifier|public
name|void
name|run
parameter_list|()
block|{
try|try
block|{
name|TimeUnit
operator|.
name|MILLISECONDS
operator|.
name|sleep
argument_list|(
name|rand
operator|.
name|nextInt
argument_list|(
literal|10
argument_list|)
argument_list|)
expr_stmt|;
name|stoppedConnection
operator|.
name|start
argument_list|()
expr_stmt|;
name|stoppedConnection
operator|.
name|stop
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
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
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
literal|1000
condition|;
name|i
operator|++
control|)
block|{
name|executor
operator|.
name|execute
argument_list|(
name|createSessionTask
argument_list|)
expr_stmt|;
name|executor
operator|.
name|execute
argument_list|(
name|startStopTask
argument_list|)
expr_stmt|;
block|}
name|executor
operator|.
name|shutdown
argument_list|()
expr_stmt|;
name|assertTrue
argument_list|(
literal|"executor terminated"
argument_list|,
name|executor
operator|.
name|awaitTermination
argument_list|(
literal|30
argument_list|,
name|TimeUnit
operator|.
name|SECONDS
argument_list|)
argument_list|)
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
block|}
end_class

end_unit

