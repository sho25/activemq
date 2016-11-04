begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *      http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
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
name|amqp
operator|.
name|profile
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
name|BytesMessage
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
name|org
operator|.
name|apache
operator|.
name|activemq
operator|.
name|transport
operator|.
name|amqp
operator|.
name|JMSClientTestSupport
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|qpid
operator|.
name|jms
operator|.
name|JmsConnection
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|qpid
operator|.
name|jms
operator|.
name|JmsConnectionFactory
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
name|Ignore
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
annotation|@
name|Ignore
argument_list|(
literal|"Use for profiling and memory testing"
argument_list|)
specifier|public
class|class
name|JmsSendReceiveStressTest
extends|extends
name|JMSClientTestSupport
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
name|JmsSendReceiveStressTest
operator|.
name|class
argument_list|)
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|int
name|PAYLOAD_SIZE
init|=
literal|64
operator|*
literal|1024
decl_stmt|;
specifier|private
specifier|final
name|byte
index|[]
name|payload
init|=
operator|new
name|byte
index|[
name|PAYLOAD_SIZE
index|]
decl_stmt|;
specifier|private
specifier|final
name|int
name|parallelProducer
init|=
literal|1
decl_stmt|;
specifier|private
specifier|final
name|int
name|parallelConsumer
init|=
literal|1
decl_stmt|;
specifier|private
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
specifier|private
name|JmsConnectionFactory
name|factory
decl_stmt|;
specifier|private
specifier|final
name|long
name|NUM_SENDS
init|=
literal|1000000
decl_stmt|;
annotation|@
name|Override
annotation|@
name|Before
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
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|PAYLOAD_SIZE
condition|;
operator|++
name|i
control|)
block|{
name|payload
index|[
name|i
index|]
operator|=
call|(
name|byte
call|)
argument_list|(
name|i
operator|%
literal|255
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Test
specifier|public
name|void
name|testProduceConsume
parameter_list|()
throws|throws
name|Exception
block|{
name|factory
operator|=
operator|new
name|JmsConnectionFactory
argument_list|(
name|getAmqpURI
argument_list|(
name|getAmqpConnectionURIOptions
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|factory
operator|.
name|setForceAsyncAcks
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|factory
operator|.
name|setForceAsyncSend
argument_list|(
literal|false
argument_list|)
expr_stmt|;
name|factory
operator|.
name|setForceSyncSend
argument_list|(
literal|false
argument_list|)
expr_stmt|;
specifier|final
name|AtomicLong
name|sharedSendCount
init|=
operator|new
name|AtomicLong
argument_list|(
name|NUM_SENDS
argument_list|)
decl_stmt|;
specifier|final
name|AtomicLong
name|sharedReceiveCount
init|=
operator|new
name|AtomicLong
argument_list|(
name|NUM_SENDS
argument_list|)
decl_stmt|;
name|Thread
operator|.
name|sleep
argument_list|(
literal|2000
argument_list|)
expr_stmt|;
name|long
name|start
init|=
name|System
operator|.
name|currentTimeMillis
argument_list|()
decl_stmt|;
name|ExecutorService
name|executorService
init|=
name|Executors
operator|.
name|newFixedThreadPool
argument_list|(
name|parallelConsumer
operator|+
name|parallelProducer
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
name|parallelConsumer
condition|;
name|i
operator|++
control|)
block|{
name|executorService
operator|.
name|execute
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
name|consumeMessages
argument_list|(
name|sharedReceiveCount
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Throwable
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
name|parallelProducer
condition|;
name|i
operator|++
control|)
block|{
name|executorService
operator|.
name|execute
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
name|publishMessages
argument_list|(
name|sharedSendCount
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Throwable
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
argument_list|)
expr_stmt|;
block|}
name|executorService
operator|.
name|shutdown
argument_list|()
expr_stmt|;
name|executorService
operator|.
name|awaitTermination
argument_list|(
literal|30
argument_list|,
name|TimeUnit
operator|.
name|MINUTES
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
literal|"Producers done in time"
argument_list|,
name|executorService
operator|.
name|isTerminated
argument_list|()
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
literal|"No exceptions: "
operator|+
name|exceptions
argument_list|,
name|exceptions
operator|.
name|isEmpty
argument_list|()
argument_list|)
expr_stmt|;
name|double
name|duration
init|=
name|System
operator|.
name|currentTimeMillis
argument_list|()
operator|-
name|start
decl_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"Duration:            "
operator|+
name|duration
operator|+
literal|"ms"
argument_list|)
expr_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"Rate:                "
operator|+
operator|(
name|NUM_SENDS
operator|*
literal|1000
operator|/
name|duration
operator|)
operator|+
literal|"m/s"
argument_list|)
expr_stmt|;
block|}
specifier|private
name|void
name|consumeMessages
parameter_list|(
name|AtomicLong
name|count
parameter_list|)
throws|throws
name|Exception
block|{
name|JmsConnection
name|connection
init|=
operator|(
name|JmsConnection
operator|)
name|factory
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
name|ActiveMQSession
operator|.
name|AUTO_ACKNOWLEDGE
argument_list|)
decl_stmt|;
name|Queue
name|queue
init|=
name|session
operator|.
name|createQueue
argument_list|(
name|getDestinationName
argument_list|()
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
name|long
name|v
decl_stmt|;
while|while
condition|(
operator|(
name|v
operator|=
name|count
operator|.
name|decrementAndGet
argument_list|()
operator|)
operator|>
literal|0
condition|)
block|{
if|if
condition|(
operator|(
name|count
operator|.
name|get
argument_list|()
operator|%
literal|10000
operator|)
operator|==
literal|0
condition|)
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"Received message: {}"
argument_list|,
name|NUM_SENDS
operator|-
name|count
operator|.
name|get
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|assertNotNull
argument_list|(
literal|"got message "
operator|+
name|v
argument_list|,
name|consumer
operator|.
name|receive
argument_list|(
literal|15000
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|LOG
operator|.
name|info
argument_list|(
literal|"Received message: {}"
argument_list|,
name|NUM_SENDS
argument_list|)
expr_stmt|;
name|consumer
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
specifier|private
name|void
name|publishMessages
parameter_list|(
name|AtomicLong
name|count
parameter_list|)
throws|throws
name|Exception
block|{
name|JmsConnection
name|connection
init|=
operator|(
name|JmsConnection
operator|)
name|factory
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
name|Queue
name|queue
init|=
name|session
operator|.
name|createQueue
argument_list|(
name|getDestinationName
argument_list|()
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
name|NON_PERSISTENT
argument_list|)
expr_stmt|;
while|while
condition|(
name|count
operator|.
name|getAndDecrement
argument_list|()
operator|>
literal|0
condition|)
block|{
name|BytesMessage
name|message
init|=
name|session
operator|.
name|createBytesMessage
argument_list|()
decl_stmt|;
name|message
operator|.
name|writeBytes
argument_list|(
name|payload
argument_list|)
expr_stmt|;
name|producer
operator|.
name|send
argument_list|(
name|message
argument_list|)
expr_stmt|;
if|if
condition|(
operator|(
name|count
operator|.
name|get
argument_list|()
operator|%
literal|10000
operator|)
operator|==
literal|0
condition|)
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"Sent message: {}"
argument_list|,
name|NUM_SENDS
operator|-
name|count
operator|.
name|get
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
name|producer
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
name|Override
specifier|protected
name|void
name|performAdditionalConfiguration
parameter_list|(
name|BrokerService
name|brokerService
parameter_list|)
throws|throws
name|Exception
block|{
name|PolicyEntry
name|policyEntry
init|=
operator|new
name|PolicyEntry
argument_list|()
decl_stmt|;
name|policyEntry
operator|.
name|setPendingQueuePolicy
argument_list|(
operator|new
name|VMPendingQueueMessageStoragePolicy
argument_list|()
argument_list|)
expr_stmt|;
name|policyEntry
operator|.
name|setPrioritizedMessages
argument_list|(
literal|false
argument_list|)
expr_stmt|;
name|policyEntry
operator|.
name|setExpireMessagesPeriod
argument_list|(
literal|0
argument_list|)
expr_stmt|;
name|policyEntry
operator|.
name|setEnableAudit
argument_list|(
literal|false
argument_list|)
expr_stmt|;
name|policyEntry
operator|.
name|setOptimizedDispatch
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|policyEntry
operator|.
name|setQueuePrefetch
argument_list|(
literal|1
argument_list|)
expr_stmt|;
comment|// ensure no contention on add with
comment|// matched producer/consumer
name|PolicyMap
name|policyMap
init|=
operator|new
name|PolicyMap
argument_list|()
decl_stmt|;
name|policyMap
operator|.
name|setDefaultEntry
argument_list|(
name|policyEntry
argument_list|)
expr_stmt|;
name|brokerService
operator|.
name|setDestinationPolicy
argument_list|(
name|policyMap
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|protected
name|String
name|getAmqpTransformer
parameter_list|()
block|{
return|return
literal|"jms"
return|;
block|}
specifier|private
name|String
name|getAmqpConnectionURIOptions
parameter_list|()
block|{
return|return
literal|"jms.presettlePolicy.presettleAll=false"
return|;
block|}
block|}
end_class

end_unit

