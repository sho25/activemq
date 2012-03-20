begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *   http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
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
name|assertEquals
import|;
end_import

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
name|concurrent
operator|.
name|LinkedBlockingQueue
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
name|AtomicLong
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
name|broker
operator|.
name|BrokerService
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

begin_class
specifier|public
class|class
name|AMQ3732Test
block|{
specifier|private
specifier|static
name|Logger
name|LOG
init|=
name|LoggerFactory
operator|.
name|getLogger
argument_list|(
name|AMQ3732Test
operator|.
name|class
argument_list|)
decl_stmt|;
specifier|private
name|ActiveMQConnectionFactory
name|connectionFactory
decl_stmt|;
specifier|private
name|Connection
name|connection
decl_stmt|;
specifier|private
name|Session
name|session
decl_stmt|;
specifier|private
name|BrokerService
name|broker
decl_stmt|;
specifier|private
name|String
name|connectionUri
decl_stmt|;
specifier|private
specifier|final
name|Random
name|pause
init|=
operator|new
name|Random
argument_list|()
decl_stmt|;
specifier|private
specifier|final
name|long
name|NUM_MESSAGES
init|=
literal|25000
decl_stmt|;
specifier|private
specifier|final
name|AtomicLong
name|totalConsumed
init|=
operator|new
name|AtomicLong
argument_list|()
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
name|broker
operator|=
operator|new
name|BrokerService
argument_list|()
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
name|setPersistent
argument_list|(
literal|false
argument_list|)
expr_stmt|;
name|broker
operator|.
name|setUseJmx
argument_list|(
literal|false
argument_list|)
expr_stmt|;
name|broker
operator|.
name|addConnector
argument_list|(
literal|"tcp://0.0.0.0:0"
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
name|connectionUri
operator|=
name|broker
operator|.
name|getTransportConnectors
argument_list|()
operator|.
name|get
argument_list|(
literal|0
argument_list|)
operator|.
name|getPublishableConnectString
argument_list|()
expr_stmt|;
name|connectionFactory
operator|=
operator|new
name|ActiveMQConnectionFactory
argument_list|(
name|connectionUri
argument_list|)
expr_stmt|;
name|connectionFactory
operator|.
name|getPrefetchPolicy
argument_list|()
operator|.
name|setAll
argument_list|(
literal|0
argument_list|)
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
name|connection
operator|.
name|close
argument_list|()
expr_stmt|;
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
block|}
annotation|@
name|Test
argument_list|(
name|timeout
operator|=
literal|1200000
argument_list|)
specifier|public
name|void
name|testInterruptionAffects
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
name|ActiveMQSession
operator|.
name|INDIVIDUAL_ACKNOWLEDGE
argument_list|)
expr_stmt|;
name|Queue
name|queue
init|=
name|session
operator|.
name|createQueue
argument_list|(
literal|"AMQ3732Test"
argument_list|)
decl_stmt|;
specifier|final
name|LinkedBlockingQueue
argument_list|<
name|Message
argument_list|>
name|workQueue
init|=
operator|new
name|LinkedBlockingQueue
argument_list|<
name|Message
argument_list|>
argument_list|()
decl_stmt|;
specifier|final
name|MessageConsumer
name|consumer1
init|=
name|session
operator|.
name|createConsumer
argument_list|(
name|queue
argument_list|)
decl_stmt|;
specifier|final
name|MessageConsumer
name|consumer2
init|=
name|session
operator|.
name|createConsumer
argument_list|(
name|queue
argument_list|)
decl_stmt|;
specifier|final
name|MessageProducer
name|producer
init|=
name|session
operator|.
name|createProducer
argument_list|(
name|queue
argument_list|)
decl_stmt|;
name|Thread
name|consumer1Thread
init|=
operator|new
name|Thread
argument_list|(
operator|new
name|Runnable
argument_list|()
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
while|while
condition|(
name|totalConsumed
operator|.
name|get
argument_list|()
operator|<
name|NUM_MESSAGES
condition|)
block|{
name|Message
name|message
init|=
name|consumer1
operator|.
name|receiveNoWait
argument_list|()
decl_stmt|;
if|if
condition|(
name|message
operator|!=
literal|null
condition|)
block|{
name|workQueue
operator|.
name|add
argument_list|(
name|message
argument_list|)
expr_stmt|;
block|}
block|}
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
name|LOG
operator|.
name|error
argument_list|(
literal|"Caught an unexpected error: "
argument_list|,
name|e
argument_list|)
expr_stmt|;
block|}
block|}
block|}
argument_list|)
decl_stmt|;
name|consumer1Thread
operator|.
name|start
argument_list|()
expr_stmt|;
name|Thread
name|consumer2Thread
init|=
operator|new
name|Thread
argument_list|(
operator|new
name|Runnable
argument_list|()
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
while|while
condition|(
name|totalConsumed
operator|.
name|get
argument_list|()
operator|<
name|NUM_MESSAGES
condition|)
block|{
name|Message
name|message
init|=
name|consumer2
operator|.
name|receive
argument_list|(
literal|50
argument_list|)
decl_stmt|;
if|if
condition|(
name|message
operator|!=
literal|null
condition|)
block|{
name|workQueue
operator|.
name|add
argument_list|(
name|message
argument_list|)
expr_stmt|;
block|}
block|}
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
name|LOG
operator|.
name|error
argument_list|(
literal|"Caught an unexpected error: "
argument_list|,
name|e
argument_list|)
expr_stmt|;
block|}
block|}
block|}
argument_list|)
decl_stmt|;
name|consumer2Thread
operator|.
name|start
argument_list|()
expr_stmt|;
name|Thread
name|producerThread
init|=
operator|new
name|Thread
argument_list|(
operator|new
name|Runnable
argument_list|()
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
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|NUM_MESSAGES
condition|;
operator|++
name|i
control|)
block|{
name|producer
operator|.
name|send
argument_list|(
name|session
operator|.
name|createTextMessage
argument_list|(
literal|"TEST"
argument_list|)
argument_list|)
expr_stmt|;
name|TimeUnit
operator|.
name|MILLISECONDS
operator|.
name|sleep
argument_list|(
name|pause
operator|.
name|nextInt
argument_list|(
literal|10
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
name|LOG
operator|.
name|error
argument_list|(
literal|"Caught an unexpected error: "
argument_list|,
name|e
argument_list|)
expr_stmt|;
block|}
block|}
block|}
argument_list|)
decl_stmt|;
name|producerThread
operator|.
name|start
argument_list|()
expr_stmt|;
name|Thread
name|ackingThread
init|=
operator|new
name|Thread
argument_list|(
operator|new
name|Runnable
argument_list|()
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
while|while
condition|(
name|totalConsumed
operator|.
name|get
argument_list|()
operator|<
name|NUM_MESSAGES
condition|)
block|{
name|Message
name|message
init|=
name|workQueue
operator|.
name|take
argument_list|()
decl_stmt|;
name|message
operator|.
name|acknowledge
argument_list|()
expr_stmt|;
name|totalConsumed
operator|.
name|incrementAndGet
argument_list|()
expr_stmt|;
if|if
condition|(
operator|(
name|totalConsumed
operator|.
name|get
argument_list|()
operator|%
literal|100
operator|)
operator|==
literal|0
condition|)
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"Consumed "
operator|+
name|totalConsumed
operator|.
name|get
argument_list|()
operator|+
literal|" messages so far."
argument_list|)
expr_stmt|;
block|}
block|}
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
name|LOG
operator|.
name|error
argument_list|(
literal|"Caught an unexpected error: "
argument_list|,
name|e
argument_list|)
expr_stmt|;
block|}
block|}
block|}
argument_list|)
decl_stmt|;
name|ackingThread
operator|.
name|start
argument_list|()
expr_stmt|;
name|producerThread
operator|.
name|join
argument_list|()
expr_stmt|;
name|consumer1Thread
operator|.
name|join
argument_list|()
expr_stmt|;
name|consumer2Thread
operator|.
name|join
argument_list|()
expr_stmt|;
name|ackingThread
operator|.
name|join
argument_list|()
expr_stmt|;
name|assertEquals
argument_list|(
name|NUM_MESSAGES
argument_list|,
name|totalConsumed
operator|.
name|get
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

