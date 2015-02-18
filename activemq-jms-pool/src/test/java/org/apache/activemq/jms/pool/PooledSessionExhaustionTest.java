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
name|jms
operator|.
name|pool
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
name|CyclicBarrier
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
name|ConnectionFactory
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
name|log4j
operator|.
name|Logger
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

begin_class
specifier|public
class|class
name|PooledSessionExhaustionTest
extends|extends
name|JmsPoolTestSupport
block|{
specifier|private
specifier|static
specifier|final
name|String
name|QUEUE
init|=
literal|"FOO"
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|int
name|NUM_MESSAGES
init|=
literal|500
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|Logger
name|LOG
init|=
name|Logger
operator|.
name|getLogger
argument_list|(
name|PooledSessionExhaustionTest
operator|.
name|class
argument_list|)
decl_stmt|;
specifier|private
name|ActiveMQConnectionFactory
name|factory
decl_stmt|;
specifier|private
name|PooledConnectionFactory
name|pooledFactory
decl_stmt|;
specifier|private
name|String
name|connectionUri
decl_stmt|;
specifier|private
name|int
name|numReceived
init|=
literal|0
decl_stmt|;
specifier|private
specifier|final
name|List
argument_list|<
name|Exception
argument_list|>
name|exceptionList
init|=
operator|new
name|ArrayList
argument_list|<
name|Exception
argument_list|>
argument_list|()
decl_stmt|;
annotation|@
name|Before
annotation|@
name|Override
specifier|public
name|void
name|setUp
parameter_list|()
throws|throws
name|Exception
block|{
name|super
operator|.
name|setUp
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
name|setUseJmx
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
name|setSchedulerSupport
argument_list|(
literal|false
argument_list|)
expr_stmt|;
name|TransportConnector
name|connector
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
name|connectionUri
operator|=
name|connector
operator|.
name|getPublishableConnectString
argument_list|()
expr_stmt|;
name|factory
operator|=
operator|new
name|ActiveMQConnectionFactory
argument_list|(
name|connectionUri
argument_list|)
expr_stmt|;
name|pooledFactory
operator|=
operator|new
name|PooledConnectionFactory
argument_list|()
expr_stmt|;
name|pooledFactory
operator|.
name|setConnectionFactory
argument_list|(
name|factory
argument_list|)
expr_stmt|;
name|pooledFactory
operator|.
name|setMaxConnections
argument_list|(
literal|1
argument_list|)
expr_stmt|;
name|pooledFactory
operator|.
name|setBlockIfSessionPoolIsFull
argument_list|(
literal|false
argument_list|)
expr_stmt|;
name|pooledFactory
operator|.
name|setMaximumActiveSessionPerConnection
argument_list|(
literal|1
argument_list|)
expr_stmt|;
block|}
class|class
name|TestRunner
implements|implements
name|Runnable
block|{
name|CyclicBarrier
name|barrier
decl_stmt|;
name|TestRunner
parameter_list|(
name|CyclicBarrier
name|barrier
parameter_list|)
block|{
name|this
operator|.
name|barrier
operator|=
name|barrier
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
name|barrier
operator|.
name|await
argument_list|()
expr_stmt|;
name|sendMessages
argument_list|(
name|pooledFactory
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
name|exceptionList
operator|.
name|add
argument_list|(
name|e
argument_list|)
expr_stmt|;
throw|throw
operator|new
name|RuntimeException
argument_list|(
name|e
argument_list|)
throw|;
block|}
block|}
block|}
specifier|public
name|void
name|sendMessages
parameter_list|(
name|ConnectionFactory
name|connectionFactory
parameter_list|)
throws|throws
name|Exception
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
name|i
operator|++
control|)
block|{
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
name|QUEUE
argument_list|)
decl_stmt|;
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
name|String
name|msgTo
init|=
literal|"hello"
decl_stmt|;
name|TextMessage
name|message
init|=
name|session
operator|.
name|createTextMessage
argument_list|(
name|msgTo
argument_list|)
decl_stmt|;
name|producer
operator|.
name|send
argument_list|(
name|message
argument_list|)
expr_stmt|;
name|connection
operator|.
name|close
argument_list|()
expr_stmt|;
name|LOG
operator|.
name|debug
argument_list|(
literal|"sent "
operator|+
name|i
operator|+
literal|" messages using "
operator|+
name|connectionFactory
operator|.
name|getClass
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Test
argument_list|(
name|timeout
operator|=
literal|60000
argument_list|)
specifier|public
name|void
name|testCanExhaustSessions
parameter_list|()
throws|throws
name|Exception
block|{
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
annotation|@
name|Override
specifier|public
name|void
name|run
parameter_list|()
block|{
try|try
block|{
name|ConnectionFactory
name|connectionFactory
init|=
operator|new
name|ActiveMQConnectionFactory
argument_list|(
name|connectionUri
argument_list|)
decl_stmt|;
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
name|QUEUE
argument_list|)
decl_stmt|;
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
name|Message
name|msg
init|=
name|consumer
operator|.
name|receive
argument_list|(
literal|5000
argument_list|)
decl_stmt|;
if|if
condition|(
name|msg
operator|==
literal|null
condition|)
block|{
return|return;
block|}
name|numReceived
operator|++
expr_stmt|;
if|if
condition|(
name|numReceived
operator|%
literal|20
operator|==
literal|0
condition|)
block|{
name|LOG
operator|.
name|debug
argument_list|(
literal|"received "
operator|+
name|numReceived
operator|+
literal|" messages "
argument_list|)
expr_stmt|;
name|System
operator|.
name|runFinalization
argument_list|()
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
throw|throw
operator|new
name|RuntimeException
argument_list|(
name|e
argument_list|)
throw|;
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
name|ExecutorService
name|threads
init|=
name|Executors
operator|.
name|newFixedThreadPool
argument_list|(
literal|2
argument_list|)
decl_stmt|;
specifier|final
name|CyclicBarrier
name|barrier
init|=
operator|new
name|CyclicBarrier
argument_list|(
literal|2
argument_list|,
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
name|LOG
operator|.
name|trace
argument_list|(
literal|"Starting threads to send messages!"
argument_list|)
expr_stmt|;
block|}
block|}
argument_list|)
decl_stmt|;
name|threads
operator|.
name|execute
argument_list|(
operator|new
name|TestRunner
argument_list|(
name|barrier
argument_list|)
argument_list|)
expr_stmt|;
name|threads
operator|.
name|execute
argument_list|(
operator|new
name|TestRunner
argument_list|(
name|barrier
argument_list|)
argument_list|)
expr_stmt|;
name|thread
operator|.
name|join
argument_list|()
expr_stmt|;
comment|// we should expect that one of the threads will die because it cannot acquire a session,
comment|// will throw an exception
name|assertEquals
argument_list|(
name|NUM_MESSAGES
argument_list|,
name|numReceived
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|exceptionList
operator|.
name|size
argument_list|()
argument_list|,
literal|1
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

