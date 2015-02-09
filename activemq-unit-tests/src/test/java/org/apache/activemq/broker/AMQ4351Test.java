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
package|;
end_package

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
name|util
operator|.
name|IOHelper
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|derby
operator|.
name|jdbc
operator|.
name|EmbeddedDataSource
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
name|*
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
name|AtomicLong
import|;
end_import

begin_comment
comment|/**  * Implements the test case attached to:  * https://issues.apache.org/jira/browse/AMQ-4351  *  * This version avoids the spring deps.  */
end_comment

begin_class
specifier|public
class|class
name|AMQ4351Test
extends|extends
name|BrokerTestSupport
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
name|AMQ4351Test
operator|.
name|class
argument_list|)
decl_stmt|;
specifier|public
specifier|static
name|Test
name|suite
parameter_list|()
block|{
return|return
name|suite
argument_list|(
name|AMQ4351Test
operator|.
name|class
argument_list|)
return|;
block|}
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
name|junit
operator|.
name|textui
operator|.
name|TestRunner
operator|.
name|run
argument_list|(
name|suite
argument_list|()
argument_list|)
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
name|broker
init|=
operator|new
name|BrokerService
argument_list|()
decl_stmt|;
comment|// Lets clean up often.
name|broker
operator|.
name|setOfflineDurableSubscriberTaskSchedule
argument_list|(
literal|500
argument_list|)
expr_stmt|;
name|broker
operator|.
name|setOfflineDurableSubscriberTimeout
argument_list|(
literal|2000
argument_list|)
expr_stmt|;
comment|// lets delete durable subs much faster.
name|System
operator|.
name|setProperty
argument_list|(
literal|"derby.system.home"
argument_list|,
operator|new
name|File
argument_list|(
name|IOHelper
operator|.
name|getDefaultDataDirectory
argument_list|()
argument_list|)
operator|.
name|getCanonicalPath
argument_list|()
argument_list|)
expr_stmt|;
name|JDBCPersistenceAdapter
name|jdbc
init|=
operator|new
name|JDBCPersistenceAdapter
argument_list|()
decl_stmt|;
name|EmbeddedDataSource
name|dataSource
init|=
operator|new
name|EmbeddedDataSource
argument_list|()
decl_stmt|;
name|dataSource
operator|.
name|setDatabaseName
argument_list|(
literal|"derbyDb"
argument_list|)
expr_stmt|;
name|dataSource
operator|.
name|setCreateDatabase
argument_list|(
literal|"create"
argument_list|)
expr_stmt|;
name|jdbc
operator|.
name|setDataSource
argument_list|(
name|dataSource
argument_list|)
expr_stmt|;
name|jdbc
operator|.
name|deleteAllMessages
argument_list|()
expr_stmt|;
name|broker
operator|.
name|setPersistenceAdapter
argument_list|(
name|jdbc
argument_list|)
expr_stmt|;
return|return
name|broker
return|;
block|}
name|ActiveMQConnectionFactory
name|connectionFactory
decl_stmt|;
name|ActiveMQTopic
name|destination
init|=
operator|new
name|ActiveMQTopic
argument_list|(
literal|"TEST"
argument_list|)
decl_stmt|;
annotation|@
name|Override
specifier|protected
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
name|connectionFactory
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
block|}
class|class
name|ProducingClient
implements|implements
name|Runnable
block|{
specifier|final
name|AtomicLong
name|size
init|=
operator|new
name|AtomicLong
argument_list|()
decl_stmt|;
specifier|final
name|AtomicBoolean
name|done
init|=
operator|new
name|AtomicBoolean
argument_list|()
decl_stmt|;
name|CountDownLatch
name|doneLatch
init|=
operator|new
name|CountDownLatch
argument_list|(
literal|1
argument_list|)
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
name|ProducingClient
parameter_list|()
throws|throws
name|JMSException
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
name|Session
operator|.
name|AUTO_ACKNOWLEDGE
argument_list|)
expr_stmt|;
name|producer
operator|=
name|session
operator|.
name|createProducer
argument_list|(
name|destination
argument_list|)
expr_stmt|;
block|}
specifier|private
name|void
name|sendMessage
parameter_list|()
block|{
try|try
block|{
name|producer
operator|.
name|send
argument_list|(
name|session
operator|.
name|createTextMessage
argument_list|(
literal|"Test"
argument_list|)
argument_list|)
expr_stmt|;
name|long
name|i
init|=
name|size
operator|.
name|incrementAndGet
argument_list|()
decl_stmt|;
if|if
condition|(
operator|(
name|i
operator|%
literal|1000
operator|)
operator|==
literal|0
condition|)
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"produced "
operator|+
name|i
operator|+
literal|"."
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
block|}
block|}
specifier|public
name|void
name|start
parameter_list|()
block|{
operator|new
name|Thread
argument_list|(
name|this
argument_list|,
literal|"ProducingClient"
argument_list|)
operator|.
name|start
argument_list|()
expr_stmt|;
block|}
specifier|public
name|void
name|stop
parameter_list|()
throws|throws
name|InterruptedException
block|{
name|done
operator|.
name|set
argument_list|(
literal|true
argument_list|)
expr_stmt|;
if|if
condition|(
operator|!
name|doneLatch
operator|.
name|await
argument_list|(
literal|20
argument_list|,
name|TimeUnit
operator|.
name|MILLISECONDS
argument_list|)
condition|)
block|{
try|try
block|{
name|connection
operator|.
name|close
argument_list|()
expr_stmt|;
name|doneLatch
operator|.
name|await
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|JMSException
name|e
parameter_list|)
block|{                 }
block|}
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
try|try
block|{
while|while
condition|(
operator|!
name|done
operator|.
name|get
argument_list|()
condition|)
block|{
name|sendMessage
argument_list|()
expr_stmt|;
name|Thread
operator|.
name|sleep
argument_list|(
literal|10
argument_list|)
expr_stmt|;
block|}
block|}
finally|finally
block|{
name|connection
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
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
name|done
operator|.
name|set
argument_list|(
literal|true
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|doneLatch
operator|.
name|countDown
argument_list|()
expr_stmt|;
block|}
block|}
block|}
class|class
name|ConsumingClient
implements|implements
name|Runnable
block|{
specifier|final
name|String
name|name
decl_stmt|;
specifier|final
name|AtomicLong
name|size
init|=
operator|new
name|AtomicLong
argument_list|()
decl_stmt|;
specifier|final
name|AtomicBoolean
name|done
init|=
operator|new
name|AtomicBoolean
argument_list|()
decl_stmt|;
name|CountDownLatch
name|doneLatch
init|=
operator|new
name|CountDownLatch
argument_list|(
literal|1
argument_list|)
decl_stmt|;
name|CountDownLatch
name|started
decl_stmt|;
name|CountDownLatch
name|finished
decl_stmt|;
specifier|public
name|ConsumingClient
parameter_list|(
name|String
name|name
parameter_list|,
name|CountDownLatch
name|started
parameter_list|,
name|CountDownLatch
name|finished
parameter_list|)
block|{
name|this
operator|.
name|name
operator|=
name|name
expr_stmt|;
name|this
operator|.
name|started
operator|=
name|started
expr_stmt|;
name|this
operator|.
name|finished
operator|=
name|finished
expr_stmt|;
block|}
specifier|public
name|void
name|start
parameter_list|()
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"Starting JMS listener "
operator|+
name|name
argument_list|)
expr_stmt|;
operator|new
name|Thread
argument_list|(
name|this
argument_list|,
literal|"ConsumingClient: "
operator|+
name|name
argument_list|)
operator|.
name|start
argument_list|()
expr_stmt|;
block|}
specifier|public
name|void
name|stopAsync
parameter_list|()
block|{
name|finished
operator|.
name|countDown
argument_list|()
expr_stmt|;
name|done
operator|.
name|set
argument_list|(
literal|true
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|stop
parameter_list|()
throws|throws
name|InterruptedException
block|{
name|stopAsync
argument_list|()
expr_stmt|;
name|doneLatch
operator|.
name|await
argument_list|()
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
name|setClientID
argument_list|(
name|name
argument_list|)
expr_stmt|;
name|connection
operator|.
name|start
argument_list|()
expr_stmt|;
try|try
block|{
name|Session
name|session
init|=
name|connection
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
name|MessageConsumer
name|consumer
init|=
name|session
operator|.
name|createDurableSubscriber
argument_list|(
name|destination
argument_list|,
name|name
argument_list|,
literal|null
argument_list|,
literal|false
argument_list|)
decl_stmt|;
name|started
operator|.
name|countDown
argument_list|()
expr_stmt|;
while|while
condition|(
operator|!
name|done
operator|.
name|get
argument_list|()
condition|)
block|{
name|Message
name|msg
init|=
name|consumer
operator|.
name|receive
argument_list|(
literal|100
argument_list|)
decl_stmt|;
if|if
condition|(
name|msg
operator|!=
literal|null
condition|)
block|{
name|size
operator|.
name|incrementAndGet
argument_list|()
expr_stmt|;
name|session
operator|.
name|commit
argument_list|()
expr_stmt|;
block|}
block|}
block|}
finally|finally
block|{
name|connection
operator|.
name|close
argument_list|()
expr_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"Stopped JMS listener "
operator|+
name|name
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
name|e
operator|.
name|printStackTrace
argument_list|()
expr_stmt|;
name|done
operator|.
name|set
argument_list|(
literal|true
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|doneLatch
operator|.
name|countDown
argument_list|()
expr_stmt|;
block|}
block|}
block|}
specifier|public
name|void
name|testAMQ4351
parameter_list|()
throws|throws
name|InterruptedException
throws|,
name|JMSException
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"Start test."
argument_list|)
expr_stmt|;
name|int
name|subs
init|=
literal|100
decl_stmt|;
name|CountDownLatch
name|startedLatch
init|=
operator|new
name|CountDownLatch
argument_list|(
name|subs
operator|-
literal|1
argument_list|)
decl_stmt|;
name|CountDownLatch
name|shutdownLatch
init|=
operator|new
name|CountDownLatch
argument_list|(
name|subs
operator|-
literal|4
argument_list|)
decl_stmt|;
name|ProducingClient
name|producer
init|=
operator|new
name|ProducingClient
argument_list|()
decl_stmt|;
name|ConsumingClient
name|listener1
init|=
operator|new
name|ConsumingClient
argument_list|(
literal|"subscriber-1"
argument_list|,
name|startedLatch
argument_list|,
name|shutdownLatch
argument_list|)
decl_stmt|;
name|ConsumingClient
name|listener2
init|=
operator|new
name|ConsumingClient
argument_list|(
literal|"subscriber-2"
argument_list|,
name|startedLatch
argument_list|,
name|shutdownLatch
argument_list|)
decl_stmt|;
name|ConsumingClient
name|listener3
init|=
operator|new
name|ConsumingClient
argument_list|(
literal|"subscriber-3"
argument_list|,
name|startedLatch
argument_list|,
name|shutdownLatch
argument_list|)
decl_stmt|;
try|try
block|{
name|listener1
operator|.
name|start
argument_list|()
expr_stmt|;
name|listener2
operator|.
name|start
argument_list|()
expr_stmt|;
name|listener3
operator|.
name|start
argument_list|()
expr_stmt|;
name|List
argument_list|<
name|ConsumingClient
argument_list|>
name|subscribers
init|=
operator|new
name|ArrayList
argument_list|<
name|ConsumingClient
argument_list|>
argument_list|(
name|subs
argument_list|)
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|4
init|;
name|i
operator|<
name|subs
condition|;
name|i
operator|++
control|)
block|{
name|ConsumingClient
name|client
init|=
operator|new
name|ConsumingClient
argument_list|(
literal|"subscriber-"
operator|+
name|i
argument_list|,
name|startedLatch
argument_list|,
name|shutdownLatch
argument_list|)
decl_stmt|;
name|subscribers
operator|.
name|add
argument_list|(
name|client
argument_list|)
expr_stmt|;
name|client
operator|.
name|start
argument_list|()
expr_stmt|;
block|}
name|startedLatch
operator|.
name|await
argument_list|(
literal|10
argument_list|,
name|TimeUnit
operator|.
name|SECONDS
argument_list|)
expr_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"All subscribers started."
argument_list|)
expr_stmt|;
name|producer
operator|.
name|sendMessage
argument_list|()
expr_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"Stopping 97 subscribers...."
argument_list|)
expr_stmt|;
for|for
control|(
name|ConsumingClient
name|client
range|:
name|subscribers
control|)
block|{
name|client
operator|.
name|stopAsync
argument_list|()
expr_stmt|;
block|}
name|shutdownLatch
operator|.
name|await
argument_list|(
literal|10
argument_list|,
name|TimeUnit
operator|.
name|SECONDS
argument_list|)
expr_stmt|;
comment|// Start producing messages for 10 minutes, at high rate
name|LOG
operator|.
name|info
argument_list|(
literal|"Starting mass message producer..."
argument_list|)
expr_stmt|;
name|producer
operator|.
name|start
argument_list|()
expr_stmt|;
name|long
name|lastSize
init|=
name|listener1
operator|.
name|size
operator|.
name|get
argument_list|()
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
literal|10
condition|;
name|i
operator|++
control|)
block|{
name|Thread
operator|.
name|sleep
argument_list|(
literal|1000
argument_list|)
expr_stmt|;
name|long
name|size
init|=
name|listener1
operator|.
name|size
operator|.
name|get
argument_list|()
decl_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"Listener 1: consumed: "
operator|+
operator|(
name|size
operator|-
name|lastSize
operator|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|size
operator|>
name|lastSize
argument_list|)
expr_stmt|;
name|lastSize
operator|=
name|size
expr_stmt|;
block|}
block|}
finally|finally
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"Stopping clients"
argument_list|)
expr_stmt|;
name|listener1
operator|.
name|stop
argument_list|()
expr_stmt|;
name|listener2
operator|.
name|stop
argument_list|()
expr_stmt|;
name|listener3
operator|.
name|stop
argument_list|()
expr_stmt|;
name|producer
operator|.
name|stop
argument_list|()
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

