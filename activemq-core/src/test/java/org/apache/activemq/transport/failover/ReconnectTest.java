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
name|transport
operator|.
name|failover
package|;
end_package

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|IOException
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
name|URISyntaxException
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
name|AtomicInteger
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
name|junit
operator|.
name|framework
operator|.
name|TestCase
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
name|transport
operator|.
name|TransportListener
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
name|transport
operator|.
name|mock
operator|.
name|MockTransport
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
name|ServiceStopper
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
name|ReconnectTest
extends|extends
name|TestCase
block|{
specifier|public
specifier|static
specifier|final
name|int
name|MESSAGES_PER_ITTERATION
init|=
literal|10
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|int
name|WORKER_COUNT
init|=
literal|10
decl_stmt|;
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
name|ReconnectTest
operator|.
name|class
argument_list|)
decl_stmt|;
specifier|private
name|BrokerService
name|bs
decl_stmt|;
specifier|private
name|URI
name|tcpUri
decl_stmt|;
specifier|private
specifier|final
name|AtomicInteger
name|resumedCount
init|=
operator|new
name|AtomicInteger
argument_list|()
decl_stmt|;
specifier|private
specifier|final
name|AtomicInteger
name|interruptedCount
init|=
operator|new
name|AtomicInteger
argument_list|()
decl_stmt|;
specifier|private
name|Worker
index|[]
name|workers
decl_stmt|;
class|class
name|Worker
implements|implements
name|Runnable
block|{
specifier|public
name|AtomicInteger
name|iterations
init|=
operator|new
name|AtomicInteger
argument_list|()
decl_stmt|;
specifier|public
name|CountDownLatch
name|stopped
init|=
operator|new
name|CountDownLatch
argument_list|(
literal|1
argument_list|)
decl_stmt|;
specifier|private
specifier|final
name|ActiveMQConnection
name|connection
decl_stmt|;
specifier|private
specifier|final
name|AtomicBoolean
name|stop
init|=
operator|new
name|AtomicBoolean
argument_list|(
literal|false
argument_list|)
decl_stmt|;
specifier|private
name|Throwable
name|error
decl_stmt|;
specifier|private
specifier|final
name|String
name|name
decl_stmt|;
specifier|public
name|Worker
parameter_list|(
specifier|final
name|String
name|name
parameter_list|)
throws|throws
name|URISyntaxException
throws|,
name|JMSException
block|{
name|this
operator|.
name|name
operator|=
name|name
expr_stmt|;
name|URI
name|uri
init|=
operator|new
name|URI
argument_list|(
literal|"failover://(mock://("
operator|+
name|tcpUri
operator|+
literal|"))?updateURIsSupported=false"
argument_list|)
decl_stmt|;
name|ActiveMQConnectionFactory
name|factory
init|=
operator|new
name|ActiveMQConnectionFactory
argument_list|(
name|uri
argument_list|)
decl_stmt|;
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
name|connection
operator|.
name|addTransportListener
argument_list|(
operator|new
name|TransportListener
argument_list|()
block|{
specifier|public
name|void
name|onCommand
parameter_list|(
name|Object
name|command
parameter_list|)
block|{                 }
specifier|public
name|void
name|onException
parameter_list|(
name|IOException
name|error
parameter_list|)
block|{
name|setError
argument_list|(
name|error
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|transportInterupted
parameter_list|()
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"Worker "
operator|+
name|name
operator|+
literal|" was interrupted..."
argument_list|)
expr_stmt|;
name|interruptedCount
operator|.
name|incrementAndGet
argument_list|()
expr_stmt|;
block|}
specifier|public
name|void
name|transportResumed
parameter_list|()
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"Worker "
operator|+
name|name
operator|+
literal|" was resummed..."
argument_list|)
expr_stmt|;
name|resumedCount
operator|.
name|incrementAndGet
argument_list|()
expr_stmt|;
block|}
block|}
argument_list|)
expr_stmt|;
name|connection
operator|.
name|start
argument_list|()
expr_stmt|;
block|}
specifier|public
name|void
name|failConnection
parameter_list|()
block|{
name|MockTransport
name|mockTransport
init|=
name|connection
operator|.
name|getTransportChannel
argument_list|()
operator|.
name|narrow
argument_list|(
name|MockTransport
operator|.
name|class
argument_list|)
decl_stmt|;
name|mockTransport
operator|.
name|onException
argument_list|(
operator|new
name|IOException
argument_list|(
literal|"Simulated error"
argument_list|)
argument_list|)
expr_stmt|;
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
block|{
name|stop
operator|.
name|set
argument_list|(
literal|true
argument_list|)
expr_stmt|;
try|try
block|{
if|if
condition|(
operator|!
name|stopped
operator|.
name|await
argument_list|(
literal|5
argument_list|,
name|TimeUnit
operator|.
name|SECONDS
argument_list|)
condition|)
block|{
name|connection
operator|.
name|close
argument_list|()
expr_stmt|;
name|stopped
operator|.
name|await
argument_list|(
literal|5
argument_list|,
name|TimeUnit
operator|.
name|SECONDS
argument_list|)
expr_stmt|;
block|}
else|else
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
block|}
block|}
specifier|public
name|void
name|run
parameter_list|()
block|{
try|try
block|{
name|ActiveMQQueue
name|queue
init|=
operator|new
name|ActiveMQQueue
argument_list|(
literal|"FOO_"
operator|+
name|name
argument_list|)
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
name|MessageConsumer
name|consumer
init|=
name|session
operator|.
name|createConsumer
argument_list|(
name|queue
argument_list|)
decl_stmt|;
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
name|producer
operator|.
name|setDeliveryMode
argument_list|(
name|DeliveryMode
operator|.
name|PERSISTENT
argument_list|)
expr_stmt|;
while|while
condition|(
operator|!
name|stop
operator|.
name|get
argument_list|()
condition|)
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
name|MESSAGES_PER_ITTERATION
condition|;
name|i
operator|++
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
literal|"TEST:"
operator|+
name|i
argument_list|)
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
name|MESSAGES_PER_ITTERATION
condition|;
name|i
operator|++
control|)
block|{
name|consumer
operator|.
name|receive
argument_list|()
expr_stmt|;
block|}
name|iterations
operator|.
name|incrementAndGet
argument_list|()
expr_stmt|;
block|}
name|session
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
name|setError
argument_list|(
name|e
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|stopped
operator|.
name|countDown
argument_list|()
expr_stmt|;
block|}
block|}
specifier|public
specifier|synchronized
name|Throwable
name|getError
parameter_list|()
block|{
return|return
name|error
return|;
block|}
specifier|public
specifier|synchronized
name|void
name|setError
parameter_list|(
name|Throwable
name|error
parameter_list|)
block|{
name|this
operator|.
name|error
operator|=
name|error
expr_stmt|;
block|}
specifier|public
specifier|synchronized
name|void
name|assertNoErrors
parameter_list|()
block|{
if|if
condition|(
name|error
operator|!=
literal|null
condition|)
block|{
name|error
operator|.
name|printStackTrace
argument_list|()
expr_stmt|;
name|fail
argument_list|(
literal|"Worker "
operator|+
name|name
operator|+
literal|" got Exception: "
operator|+
name|error
argument_list|)
expr_stmt|;
block|}
block|}
block|}
specifier|public
name|void
name|testReconnects
parameter_list|()
throws|throws
name|Exception
block|{
for|for
control|(
name|int
name|k
init|=
literal|1
init|;
name|k
operator|<
literal|10
condition|;
name|k
operator|++
control|)
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"Test run: "
operator|+
name|k
argument_list|)
expr_stmt|;
comment|// Wait for at least one iteration to occur...
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|WORKER_COUNT
condition|;
name|i
operator|++
control|)
block|{
name|int
name|c
init|=
literal|0
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
literal|30
condition|;
name|j
operator|++
control|)
block|{
name|c
operator|=
name|workers
index|[
name|i
index|]
operator|.
name|iterations
operator|.
name|getAndSet
argument_list|(
literal|0
argument_list|)
expr_stmt|;
if|if
condition|(
name|c
operator|!=
literal|0
condition|)
block|{
break|break;
block|}
name|workers
index|[
name|i
index|]
operator|.
name|assertNoErrors
argument_list|()
expr_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"Test run "
operator|+
name|k
operator|+
literal|": Waiting for worker "
operator|+
name|i
operator|+
literal|" to finish an iteration."
argument_list|)
expr_stmt|;
name|Thread
operator|.
name|sleep
argument_list|(
literal|1000
argument_list|)
expr_stmt|;
block|}
name|assertTrue
argument_list|(
literal|"Test run "
operator|+
name|k
operator|+
literal|": Worker "
operator|+
name|i
operator|+
literal|" never completed an interation."
argument_list|,
name|c
operator|!=
literal|0
argument_list|)
expr_stmt|;
name|workers
index|[
name|i
index|]
operator|.
name|assertNoErrors
argument_list|()
expr_stmt|;
block|}
name|LOG
operator|.
name|info
argument_list|(
literal|"Simulating transport error to cause reconnect."
argument_list|)
expr_stmt|;
comment|// Simulate a transport failure.
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|WORKER_COUNT
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
name|failConnection
argument_list|()
expr_stmt|;
block|}
name|assertTrue
argument_list|(
literal|"Timed out waiting for all connections to be interrupted."
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
specifier|public
name|boolean
name|isSatisified
parameter_list|()
throws|throws
name|Exception
block|{
name|LOG
operator|.
name|debug
argument_list|(
literal|"Test run waiting for connections to get interrupted.. at: "
operator|+
name|interruptedCount
operator|.
name|get
argument_list|()
argument_list|)
expr_stmt|;
return|return
name|interruptedCount
operator|.
name|get
argument_list|()
operator|==
name|WORKER_COUNT
return|;
block|}
block|}
argument_list|,
name|TimeUnit
operator|.
name|SECONDS
operator|.
name|toMillis
argument_list|(
literal|60
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
comment|// Wait for the connections to re-establish...
name|assertTrue
argument_list|(
literal|"Timed out waiting for all connections to be resumed."
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
specifier|public
name|boolean
name|isSatisified
parameter_list|()
throws|throws
name|Exception
block|{
name|LOG
operator|.
name|debug
argument_list|(
literal|"Test run waiting for connections to get resumed.. at: "
operator|+
name|resumedCount
operator|.
name|get
argument_list|()
argument_list|)
expr_stmt|;
return|return
name|resumedCount
operator|.
name|get
argument_list|()
operator|>=
name|WORKER_COUNT
return|;
block|}
block|}
argument_list|,
name|TimeUnit
operator|.
name|SECONDS
operator|.
name|toMillis
argument_list|(
literal|60
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
comment|// Reset the counters..
name|interruptedCount
operator|.
name|set
argument_list|(
literal|0
argument_list|)
expr_stmt|;
name|resumedCount
operator|.
name|set
argument_list|(
literal|0
argument_list|)
expr_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|WORKER_COUNT
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
name|iterations
operator|.
name|set
argument_list|(
literal|0
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
name|bs
operator|=
operator|new
name|BrokerService
argument_list|()
expr_stmt|;
name|bs
operator|.
name|setPersistent
argument_list|(
literal|false
argument_list|)
expr_stmt|;
name|bs
operator|.
name|setUseJmx
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|TransportConnector
name|connector
init|=
name|bs
operator|.
name|addConnector
argument_list|(
literal|"tcp://localhost:0"
argument_list|)
decl_stmt|;
name|bs
operator|.
name|start
argument_list|()
expr_stmt|;
name|tcpUri
operator|=
name|connector
operator|.
name|getConnectUri
argument_list|()
expr_stmt|;
name|workers
operator|=
operator|new
name|Worker
index|[
name|WORKER_COUNT
index|]
expr_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|WORKER_COUNT
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
literal|""
operator|+
name|i
argument_list|)
expr_stmt|;
name|workers
index|[
name|i
index|]
operator|.
name|start
argument_list|()
expr_stmt|;
block|}
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
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|WORKER_COUNT
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
name|stop
argument_list|()
expr_stmt|;
block|}
operator|new
name|ServiceStopper
argument_list|()
operator|.
name|stop
argument_list|(
name|bs
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

