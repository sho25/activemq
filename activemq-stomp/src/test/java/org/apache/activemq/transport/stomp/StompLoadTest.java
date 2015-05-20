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
name|stomp
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
name|ArrayList
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|HashMap
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
name|ThreadFactory
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
name|AtomicInteger
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
name|StompLoadTest
extends|extends
name|StompTestSupport
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
name|StompLoadTest
operator|.
name|class
argument_list|)
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|int
name|TASK_COUNT
init|=
literal|100
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|int
name|MSG_COUNT
init|=
literal|250
decl_stmt|;
comment|// AMQ-3819: Above 250 or so and the CPU goes bonkers with NOI+SSL.
specifier|private
name|ExecutorService
name|executor
decl_stmt|;
specifier|private
name|CountDownLatch
name|started
decl_stmt|;
specifier|private
name|CountDownLatch
name|ready
decl_stmt|;
specifier|private
name|AtomicInteger
name|receiveCount
decl_stmt|;
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
name|stompConnect
argument_list|()
expr_stmt|;
name|stompConnection
operator|.
name|connect
argument_list|(
literal|"system"
argument_list|,
literal|"manager"
argument_list|)
expr_stmt|;
name|executor
operator|=
name|Executors
operator|.
name|newFixedThreadPool
argument_list|(
name|TASK_COUNT
argument_list|,
operator|new
name|ThreadFactory
argument_list|()
block|{
specifier|private
name|long
name|i
init|=
literal|0
decl_stmt|;
annotation|@
name|Override
specifier|public
name|Thread
name|newThread
parameter_list|(
name|Runnable
name|runnable
parameter_list|)
block|{
name|this
operator|.
name|i
operator|++
expr_stmt|;
specifier|final
name|Thread
name|t
init|=
operator|new
name|Thread
argument_list|(
name|runnable
argument_list|,
literal|"Test Worker "
operator|+
name|this
operator|.
name|i
argument_list|)
decl_stmt|;
return|return
name|t
return|;
block|}
block|}
argument_list|)
expr_stmt|;
name|started
operator|=
operator|new
name|CountDownLatch
argument_list|(
name|TASK_COUNT
argument_list|)
expr_stmt|;
name|ready
operator|=
operator|new
name|CountDownLatch
argument_list|(
literal|1
argument_list|)
expr_stmt|;
name|receiveCount
operator|=
operator|new
name|AtomicInteger
argument_list|(
literal|0
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|tearDown
parameter_list|()
throws|throws
name|Exception
block|{
try|try
block|{
name|executor
operator|.
name|shutdownNow
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{         }
finally|finally
block|{
name|super
operator|.
name|tearDown
argument_list|()
expr_stmt|;
block|}
block|}
annotation|@
name|Test
argument_list|(
name|timeout
operator|=
literal|5
operator|*
literal|60
operator|*
literal|1000
argument_list|)
specifier|public
name|void
name|testStompUnloadLoad
parameter_list|()
throws|throws
name|Exception
block|{
specifier|final
name|List
argument_list|<
name|StompConnection
argument_list|>
name|taskConnections
init|=
operator|new
name|ArrayList
argument_list|<>
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
name|TASK_COUNT
condition|;
operator|++
name|i
control|)
block|{
name|executor
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
name|LOG
operator|.
name|debug
argument_list|(
literal|"Receive Thread Connecting to Broker."
argument_list|)
expr_stmt|;
name|int
name|numReceived
init|=
literal|0
decl_stmt|;
name|StompConnection
name|connection
init|=
operator|new
name|StompConnection
argument_list|()
decl_stmt|;
try|try
block|{
name|stompConnect
argument_list|(
name|connection
argument_list|)
expr_stmt|;
name|connection
operator|.
name|connect
argument_list|(
literal|"system"
argument_list|,
literal|"manager"
argument_list|)
expr_stmt|;
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
literal|"Caught Exception while connecting: "
operator|+
name|e
operator|.
name|getMessage
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|taskConnections
operator|.
name|add
argument_list|(
name|connection
argument_list|)
expr_stmt|;
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
literal|10
condition|;
name|i
operator|++
control|)
block|{
name|connection
operator|.
name|subscribe
argument_list|(
literal|"/queue/test-"
operator|+
name|i
argument_list|,
literal|"auto"
argument_list|)
expr_stmt|;
name|connection
operator|.
name|subscribe
argument_list|(
literal|"/topic/test-"
operator|+
name|i
argument_list|,
literal|"auto"
argument_list|)
expr_stmt|;
block|}
name|HashMap
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|headers
init|=
operator|new
name|HashMap
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
argument_list|()
decl_stmt|;
name|headers
operator|.
name|put
argument_list|(
literal|"activemq.prefetchSize"
argument_list|,
literal|"1"
argument_list|)
expr_stmt|;
name|connection
operator|.
name|subscribe
argument_list|(
literal|"/topic/"
operator|+
name|getTopicName
argument_list|()
argument_list|,
literal|"auto"
argument_list|,
name|headers
argument_list|)
expr_stmt|;
name|ready
operator|.
name|await
argument_list|()
expr_stmt|;
comment|// Now that the main test thread is ready we wait a bit to let the tasks
comment|// all subscribe and the CPU to settle a bit.
name|TimeUnit
operator|.
name|SECONDS
operator|.
name|sleep
argument_list|(
literal|3
argument_list|)
expr_stmt|;
name|started
operator|.
name|countDown
argument_list|()
expr_stmt|;
while|while
condition|(
name|receiveCount
operator|.
name|get
argument_list|()
operator|!=
name|TASK_COUNT
operator|*
name|MSG_COUNT
condition|)
block|{
comment|// Read Timeout ends this task, we override the default here since there
comment|// are so many threads running and we don't know how slow the test box is.
name|StompFrame
name|frame
init|=
name|connection
operator|.
name|receive
argument_list|(
name|TimeUnit
operator|.
name|SECONDS
operator|.
name|toMillis
argument_list|(
literal|60
argument_list|)
argument_list|)
decl_stmt|;
name|assertNotNull
argument_list|(
name|frame
argument_list|)
expr_stmt|;
name|numReceived
operator|++
expr_stmt|;
if|if
condition|(
name|LOG
operator|.
name|isDebugEnabled
argument_list|()
operator|&&
operator|(
name|numReceived
operator|%
literal|50
operator|)
operator|==
literal|0
operator|||
name|numReceived
operator|==
name|MSG_COUNT
condition|)
block|{
name|LOG
operator|.
name|debug
argument_list|(
literal|"Receiver thread got message: "
operator|+
name|frame
operator|.
name|getHeaders
argument_list|()
operator|.
name|get
argument_list|(
literal|"message-id"
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|receiveCount
operator|.
name|incrementAndGet
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
if|if
condition|(
name|numReceived
operator|!=
name|MSG_COUNT
condition|)
block|{
name|LOG
operator|.
name|warn
argument_list|(
literal|"Receive task caught exception after receipt of ["
operator|+
name|numReceived
operator|+
literal|"] messages: "
operator|+
name|e
operator|.
name|getMessage
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|}
argument_list|)
expr_stmt|;
block|}
name|ready
operator|.
name|countDown
argument_list|()
expr_stmt|;
name|assertTrue
argument_list|(
literal|"Timed out waiting for receivers to start."
argument_list|,
name|started
operator|.
name|await
argument_list|(
literal|5
argument_list|,
name|TimeUnit
operator|.
name|MINUTES
argument_list|)
argument_list|)
expr_stmt|;
name|String
name|frame
decl_stmt|;
comment|// Lets still wait a bit to make sure all subscribers get a fair shake at
comment|// getting online before we send.  Account for slow Hudson machines
name|TimeUnit
operator|.
name|SECONDS
operator|.
name|sleep
argument_list|(
literal|5
argument_list|)
expr_stmt|;
for|for
control|(
name|int
name|ix
init|=
literal|0
init|;
name|ix
operator|<
name|MSG_COUNT
condition|;
name|ix
operator|++
control|)
block|{
name|frame
operator|=
literal|"SEND\n"
operator|+
literal|"destination:/topic/"
operator|+
name|getTopicName
argument_list|()
operator|+
literal|"\nid:"
operator|+
name|ix
operator|+
literal|"\ncontent-length:5"
operator|+
literal|" \n\n"
operator|+
literal|"\u0001\u0002\u0000\u0004\u0005"
operator|+
name|Stomp
operator|.
name|NULL
expr_stmt|;
name|stompConnection
operator|.
name|sendFrame
argument_list|(
name|frame
argument_list|)
expr_stmt|;
block|}
name|LOG
operator|.
name|info
argument_list|(
literal|"All "
operator|+
name|MSG_COUNT
operator|+
literal|" message have been sent, awaiting receipt."
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
literal|"Should get ["
operator|+
name|TASK_COUNT
operator|*
name|MSG_COUNT
operator|+
literal|"] message but was: "
operator|+
name|receiveCount
operator|.
name|get
argument_list|()
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
annotation|@
name|Override
specifier|public
name|boolean
name|isSatisified
parameter_list|()
throws|throws
name|Exception
block|{
return|return
name|receiveCount
operator|.
name|get
argument_list|()
operator|==
name|TASK_COUNT
operator|*
name|MSG_COUNT
return|;
block|}
block|}
argument_list|,
name|TimeUnit
operator|.
name|MINUTES
operator|.
name|toMillis
argument_list|(
literal|10
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"Test Completed and all messages received, shutting down."
argument_list|)
expr_stmt|;
for|for
control|(
name|StompConnection
name|taskConnection
range|:
name|taskConnections
control|)
block|{
try|try
block|{
name|taskConnection
operator|.
name|disconnect
argument_list|()
expr_stmt|;
name|taskConnection
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|ex
parameter_list|)
block|{             }
block|}
name|executor
operator|.
name|shutdown
argument_list|()
expr_stmt|;
name|executor
operator|.
name|awaitTermination
argument_list|(
literal|2
argument_list|,
name|TimeUnit
operator|.
name|MINUTES
argument_list|)
expr_stmt|;
name|stompDisconnect
argument_list|()
expr_stmt|;
block|}
block|}
end_class

end_unit

