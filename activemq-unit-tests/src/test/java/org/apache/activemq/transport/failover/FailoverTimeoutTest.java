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
import|import static
name|org
operator|.
name|junit
operator|.
name|Assert
operator|.
name|fail
import|;
end_import

begin_import
import|import
name|java
operator|.
name|net
operator|.
name|Socket
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
name|command
operator|.
name|Command
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
name|LocalTransactionId
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
name|MessageAck
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
name|TransactionInfo
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
name|FailoverTimeoutTest
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
name|FailoverTimeoutTest
operator|.
name|class
argument_list|)
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|String
name|QUEUE_NAME
init|=
literal|"test.failovertimeout"
decl_stmt|;
name|BrokerService
name|bs
decl_stmt|;
name|URI
name|tcpUri
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
name|bs
operator|=
operator|new
name|BrokerService
argument_list|()
expr_stmt|;
name|bs
operator|.
name|setUseJmx
argument_list|(
literal|false
argument_list|)
expr_stmt|;
name|bs
operator|.
name|addConnector
argument_list|(
name|getTransportUri
argument_list|()
argument_list|)
expr_stmt|;
name|bs
operator|.
name|start
argument_list|()
expr_stmt|;
name|tcpUri
operator|=
name|bs
operator|.
name|getTransportConnectors
argument_list|()
operator|.
name|get
argument_list|(
literal|0
argument_list|)
operator|.
name|getConnectUri
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
name|bs
operator|!=
literal|null
condition|)
block|{
name|bs
operator|.
name|stop
argument_list|()
expr_stmt|;
block|}
block|}
specifier|protected
name|String
name|getTransportUri
parameter_list|()
block|{
return|return
literal|"tcp://localhost:0"
return|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testTimoutDoesNotFailConnectionAttempts
parameter_list|()
throws|throws
name|Exception
block|{
name|bs
operator|.
name|stop
argument_list|()
expr_stmt|;
name|long
name|timeout
init|=
literal|1000
decl_stmt|;
name|long
name|startTime
init|=
name|System
operator|.
name|currentTimeMillis
argument_list|()
decl_stmt|;
name|ActiveMQConnectionFactory
name|cf
init|=
operator|new
name|ActiveMQConnectionFactory
argument_list|(
literal|"failover:("
operator|+
name|tcpUri
operator|+
literal|")"
operator|+
literal|"?timeout="
operator|+
name|timeout
operator|+
literal|"&useExponentialBackOff=false"
operator|+
literal|"&maxReconnectAttempts=5"
operator|+
literal|"&initialReconnectDelay=1000"
argument_list|)
decl_stmt|;
name|Connection
name|connection
init|=
name|cf
operator|.
name|createConnection
argument_list|()
decl_stmt|;
try|try
block|{
name|connection
operator|.
name|start
argument_list|()
expr_stmt|;
name|fail
argument_list|(
literal|"Should have failed to connect"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|JMSException
name|ex
parameter_list|)
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"Caught exception on call to start: {}"
argument_list|,
name|ex
operator|.
name|getMessage
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|long
name|endTime
init|=
name|System
operator|.
name|currentTimeMillis
argument_list|()
decl_stmt|;
name|long
name|duration
init|=
name|endTime
operator|-
name|startTime
decl_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"Time spent waiting to connect: {} ms"
argument_list|,
name|duration
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|duration
operator|>
literal|3000
argument_list|)
expr_stmt|;
name|safeClose
argument_list|(
name|connection
argument_list|)
expr_stmt|;
block|}
specifier|private
name|void
name|safeClose
parameter_list|(
name|Connection
name|connection
parameter_list|)
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
name|Exception
name|ignored
parameter_list|)
block|{}
block|}
annotation|@
name|Test
specifier|public
name|void
name|testTimeout
parameter_list|()
throws|throws
name|Exception
block|{
name|long
name|timeout
init|=
literal|1000
decl_stmt|;
name|ActiveMQConnectionFactory
name|cf
init|=
operator|new
name|ActiveMQConnectionFactory
argument_list|(
literal|"failover:("
operator|+
name|tcpUri
operator|+
literal|")?timeout="
operator|+
name|timeout
operator|+
literal|"&useExponentialBackOff=false"
argument_list|)
decl_stmt|;
name|Connection
name|connection
init|=
name|cf
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
name|MessageProducer
name|producer
init|=
name|session
operator|.
name|createProducer
argument_list|(
name|session
operator|.
name|createQueue
argument_list|(
name|QUEUE_NAME
argument_list|)
argument_list|)
decl_stmt|;
name|TextMessage
name|message
init|=
name|session
operator|.
name|createTextMessage
argument_list|(
literal|"Test message"
argument_list|)
decl_stmt|;
name|producer
operator|.
name|send
argument_list|(
name|message
argument_list|)
expr_stmt|;
name|bs
operator|.
name|stop
argument_list|()
expr_stmt|;
try|try
block|{
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
name|assertEquals
argument_list|(
literal|"Failover timeout of "
operator|+
name|timeout
operator|+
literal|" ms reached."
argument_list|,
name|jmse
operator|.
name|getMessage
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|bs
operator|=
operator|new
name|BrokerService
argument_list|()
expr_stmt|;
name|bs
operator|.
name|setUseJmx
argument_list|(
literal|false
argument_list|)
expr_stmt|;
name|bs
operator|.
name|addConnector
argument_list|(
name|tcpUri
argument_list|)
expr_stmt|;
name|bs
operator|.
name|start
argument_list|()
expr_stmt|;
name|bs
operator|.
name|waitUntilStarted
argument_list|()
expr_stmt|;
name|producer
operator|.
name|send
argument_list|(
name|message
argument_list|)
expr_stmt|;
name|bs
operator|.
name|stop
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
name|testInterleaveAckAndException
parameter_list|()
throws|throws
name|Exception
block|{
name|ActiveMQConnectionFactory
name|cf
init|=
operator|new
name|ActiveMQConnectionFactory
argument_list|(
literal|"failover:("
operator|+
name|tcpUri
operator|+
literal|")?maxReconnectAttempts=0"
argument_list|)
decl_stmt|;
specifier|final
name|ActiveMQConnection
name|connection
init|=
operator|(
name|ActiveMQConnection
operator|)
name|cf
operator|.
name|createConnection
argument_list|()
decl_stmt|;
name|doTestInterleaveAndException
argument_list|(
name|connection
argument_list|,
operator|new
name|MessageAck
argument_list|()
argument_list|)
expr_stmt|;
name|safeClose
argument_list|(
name|connection
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testInterleaveTxAndException
parameter_list|()
throws|throws
name|Exception
block|{
name|ActiveMQConnectionFactory
name|cf
init|=
operator|new
name|ActiveMQConnectionFactory
argument_list|(
literal|"failover:("
operator|+
name|tcpUri
operator|+
literal|")?maxReconnectAttempts=0"
argument_list|)
decl_stmt|;
specifier|final
name|ActiveMQConnection
name|connection
init|=
operator|(
name|ActiveMQConnection
operator|)
name|cf
operator|.
name|createConnection
argument_list|()
decl_stmt|;
name|TransactionInfo
name|tx
init|=
operator|new
name|TransactionInfo
argument_list|()
decl_stmt|;
name|tx
operator|.
name|setConnectionId
argument_list|(
name|connection
operator|.
name|getConnectionInfo
argument_list|()
operator|.
name|getConnectionId
argument_list|()
argument_list|)
expr_stmt|;
name|tx
operator|.
name|setTransactionId
argument_list|(
operator|new
name|LocalTransactionId
argument_list|(
name|tx
operator|.
name|getConnectionId
argument_list|()
argument_list|,
literal|1
argument_list|)
argument_list|)
expr_stmt|;
name|doTestInterleaveAndException
argument_list|(
name|connection
argument_list|,
name|tx
argument_list|)
expr_stmt|;
name|safeClose
argument_list|(
name|connection
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|doTestInterleaveAndException
parameter_list|(
specifier|final
name|ActiveMQConnection
name|connection
parameter_list|,
specifier|final
name|Command
name|command
parameter_list|)
throws|throws
name|Exception
block|{
name|connection
operator|.
name|start
argument_list|()
expr_stmt|;
name|connection
operator|.
name|setExceptionListener
argument_list|(
operator|new
name|ExceptionListener
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|void
name|onException
parameter_list|(
name|JMSException
name|exception
parameter_list|)
block|{
try|try
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"Deal with exception - invoke op that may block pending outstanding oneway"
argument_list|)
expr_stmt|;
comment|// try and invoke on connection as part of handling exception
name|connection
operator|.
name|asyncSendPacket
argument_list|(
name|command
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{                 }
block|}
block|}
argument_list|)
expr_stmt|;
specifier|final
name|ExecutorService
name|executorService
init|=
name|Executors
operator|.
name|newCachedThreadPool
argument_list|()
decl_stmt|;
specifier|final
name|int
name|NUM_TASKS
init|=
literal|200
decl_stmt|;
specifier|final
name|CountDownLatch
name|enqueueOnExecutorDone
init|=
operator|new
name|CountDownLatch
argument_list|(
name|NUM_TASKS
argument_list|)
decl_stmt|;
comment|// let a few tasks delay a bit
specifier|final
name|AtomicLong
name|sleepMillis
init|=
operator|new
name|AtomicLong
argument_list|(
literal|1000
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
name|NUM_TASKS
condition|;
name|i
operator|++
control|)
block|{
name|executorService
operator|.
name|submit
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
name|TimeUnit
operator|.
name|MILLISECONDS
operator|.
name|sleep
argument_list|(
name|Math
operator|.
name|max
argument_list|(
literal|0
argument_list|,
name|sleepMillis
operator|.
name|addAndGet
argument_list|(
operator|-
literal|50
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|connection
operator|.
name|asyncSendPacket
argument_list|(
name|command
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{                     }
finally|finally
block|{
name|enqueueOnExecutorDone
operator|.
name|countDown
argument_list|()
expr_stmt|;
block|}
block|}
block|}
argument_list|)
expr_stmt|;
block|}
while|while
condition|(
name|enqueueOnExecutorDone
operator|.
name|getCount
argument_list|()
operator|>
operator|(
name|NUM_TASKS
operator|-
literal|10
operator|)
condition|)
block|{
name|enqueueOnExecutorDone
operator|.
name|await
argument_list|(
literal|20
argument_list|,
name|TimeUnit
operator|.
name|MILLISECONDS
argument_list|)
expr_stmt|;
block|}
comment|// force IOException
specifier|final
name|Socket
name|socket
init|=
name|connection
operator|.
name|getTransport
argument_list|()
operator|.
name|narrow
argument_list|(
name|Socket
operator|.
name|class
argument_list|)
decl_stmt|;
name|socket
operator|.
name|close
argument_list|()
expr_stmt|;
name|executorService
operator|.
name|shutdown
argument_list|()
expr_stmt|;
name|assertTrue
argument_list|(
literal|"all ops finish"
argument_list|,
name|enqueueOnExecutorDone
operator|.
name|await
argument_list|(
literal|15
argument_list|,
name|TimeUnit
operator|.
name|SECONDS
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testUpdateUris
parameter_list|()
throws|throws
name|Exception
block|{
name|ActiveMQConnectionFactory
name|cf
init|=
operator|new
name|ActiveMQConnectionFactory
argument_list|(
literal|"failover:("
operator|+
name|tcpUri
operator|+
literal|")?useExponentialBackOff=false"
argument_list|)
decl_stmt|;
name|ActiveMQConnection
name|connection
init|=
operator|(
name|ActiveMQConnection
operator|)
name|cf
operator|.
name|createConnection
argument_list|()
decl_stmt|;
name|connection
operator|.
name|start
argument_list|()
expr_stmt|;
name|FailoverTransport
name|failoverTransport
init|=
name|connection
operator|.
name|getTransport
argument_list|()
operator|.
name|narrow
argument_list|(
name|FailoverTransport
operator|.
name|class
argument_list|)
decl_stmt|;
name|URI
index|[]
name|bunchOfUnknownAndOneKnown
init|=
operator|new
name|URI
index|[]
block|{
operator|new
name|URI
argument_list|(
literal|"tcp://unknownHost:"
operator|+
name|tcpUri
operator|.
name|getPort
argument_list|()
argument_list|)
block|,
operator|new
name|URI
argument_list|(
literal|"tcp://unknownHost2:"
operator|+
name|tcpUri
operator|.
name|getPort
argument_list|()
argument_list|)
block|,
operator|new
name|URI
argument_list|(
literal|"tcp://localhost:2222"
argument_list|)
block|}
decl_stmt|;
name|failoverTransport
operator|.
name|add
argument_list|(
literal|false
argument_list|,
name|bunchOfUnknownAndOneKnown
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

