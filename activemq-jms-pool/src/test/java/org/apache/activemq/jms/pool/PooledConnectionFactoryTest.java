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
import|import
name|java
operator|.
name|util
operator|.
name|concurrent
operator|.
name|Callable
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
name|ConcurrentHashMap
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
name|ConcurrentLinkedQueue
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
name|Future
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
name|Session
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

begin_import
import|import
name|junit
operator|.
name|framework
operator|.
name|TestSuite
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
name|command
operator|.
name|ConnectionId
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
name|apache
operator|.
name|log4j
operator|.
name|Logger
import|;
end_import

begin_comment
comment|/**  * Checks the behavior of the PooledConnectionFactory when the maximum amount of  * sessions is being reached.  *  * Older versions simply block in the call to Connection.getSession(), which  * isn't good. An exception being returned is the better option, so JMS clients  * don't block. This test succeeds if an exception is returned and fails if the  * call to getSession() blocks.  */
end_comment

begin_class
specifier|public
class|class
name|PooledConnectionFactoryTest
extends|extends
name|TestCase
block|{
specifier|public
specifier|final
specifier|static
name|Logger
name|LOG
init|=
name|Logger
operator|.
name|getLogger
argument_list|(
name|PooledConnectionFactoryTest
operator|.
name|class
argument_list|)
decl_stmt|;
comment|/**      * Create the test case      *      * @param testName      *            name of the test case      */
specifier|public
name|PooledConnectionFactoryTest
parameter_list|(
name|String
name|testName
parameter_list|)
block|{
name|super
argument_list|(
name|testName
argument_list|)
expr_stmt|;
block|}
comment|/**      * @return the suite of tests being tested      */
specifier|public
specifier|static
name|Test
name|suite
parameter_list|()
block|{
return|return
operator|new
name|TestSuite
argument_list|(
name|PooledConnectionFactoryTest
operator|.
name|class
argument_list|)
return|;
block|}
specifier|public
name|void
name|testClearAllConnections
parameter_list|()
throws|throws
name|Exception
block|{
name|ActiveMQConnectionFactory
name|amq
init|=
operator|new
name|ActiveMQConnectionFactory
argument_list|(
literal|"vm://broker1?marshal=false&broker.persistent=false"
argument_list|)
decl_stmt|;
name|PooledConnectionFactory
name|cf
init|=
operator|new
name|PooledConnectionFactory
argument_list|()
decl_stmt|;
name|cf
operator|.
name|setConnectionFactory
argument_list|(
name|amq
argument_list|)
expr_stmt|;
name|cf
operator|.
name|setMaxConnections
argument_list|(
literal|3
argument_list|)
expr_stmt|;
name|PooledConnection
name|conn1
init|=
operator|(
name|PooledConnection
operator|)
name|cf
operator|.
name|createConnection
argument_list|()
decl_stmt|;
name|PooledConnection
name|conn2
init|=
operator|(
name|PooledConnection
operator|)
name|cf
operator|.
name|createConnection
argument_list|()
decl_stmt|;
name|PooledConnection
name|conn3
init|=
operator|(
name|PooledConnection
operator|)
name|cf
operator|.
name|createConnection
argument_list|()
decl_stmt|;
name|assertNotSame
argument_list|(
name|conn1
operator|.
name|getConnection
argument_list|()
argument_list|,
name|conn2
operator|.
name|getConnection
argument_list|()
argument_list|)
expr_stmt|;
name|assertNotSame
argument_list|(
name|conn1
operator|.
name|getConnection
argument_list|()
argument_list|,
name|conn3
operator|.
name|getConnection
argument_list|()
argument_list|)
expr_stmt|;
name|assertNotSame
argument_list|(
name|conn2
operator|.
name|getConnection
argument_list|()
argument_list|,
name|conn3
operator|.
name|getConnection
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|3
argument_list|,
name|cf
operator|.
name|getNumConnections
argument_list|()
argument_list|)
expr_stmt|;
name|cf
operator|.
name|clear
argument_list|()
expr_stmt|;
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|cf
operator|.
name|getNumConnections
argument_list|()
argument_list|)
expr_stmt|;
name|conn1
operator|=
operator|(
name|PooledConnection
operator|)
name|cf
operator|.
name|createConnection
argument_list|()
expr_stmt|;
name|conn2
operator|=
operator|(
name|PooledConnection
operator|)
name|cf
operator|.
name|createConnection
argument_list|()
expr_stmt|;
name|conn3
operator|=
operator|(
name|PooledConnection
operator|)
name|cf
operator|.
name|createConnection
argument_list|()
expr_stmt|;
name|assertNotSame
argument_list|(
name|conn1
operator|.
name|getConnection
argument_list|()
argument_list|,
name|conn2
operator|.
name|getConnection
argument_list|()
argument_list|)
expr_stmt|;
name|assertNotSame
argument_list|(
name|conn1
operator|.
name|getConnection
argument_list|()
argument_list|,
name|conn3
operator|.
name|getConnection
argument_list|()
argument_list|)
expr_stmt|;
name|assertNotSame
argument_list|(
name|conn2
operator|.
name|getConnection
argument_list|()
argument_list|,
name|conn3
operator|.
name|getConnection
argument_list|()
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|testMaxConnectionsAreCreated
parameter_list|()
throws|throws
name|Exception
block|{
name|ActiveMQConnectionFactory
name|amq
init|=
operator|new
name|ActiveMQConnectionFactory
argument_list|(
literal|"vm://broker1?marshal=false&broker.persistent=false"
argument_list|)
decl_stmt|;
name|PooledConnectionFactory
name|cf
init|=
operator|new
name|PooledConnectionFactory
argument_list|()
decl_stmt|;
name|cf
operator|.
name|setConnectionFactory
argument_list|(
name|amq
argument_list|)
expr_stmt|;
name|cf
operator|.
name|setMaxConnections
argument_list|(
literal|3
argument_list|)
expr_stmt|;
name|PooledConnection
name|conn1
init|=
operator|(
name|PooledConnection
operator|)
name|cf
operator|.
name|createConnection
argument_list|()
decl_stmt|;
name|PooledConnection
name|conn2
init|=
operator|(
name|PooledConnection
operator|)
name|cf
operator|.
name|createConnection
argument_list|()
decl_stmt|;
name|PooledConnection
name|conn3
init|=
operator|(
name|PooledConnection
operator|)
name|cf
operator|.
name|createConnection
argument_list|()
decl_stmt|;
name|assertNotSame
argument_list|(
name|conn1
operator|.
name|getConnection
argument_list|()
argument_list|,
name|conn2
operator|.
name|getConnection
argument_list|()
argument_list|)
expr_stmt|;
name|assertNotSame
argument_list|(
name|conn1
operator|.
name|getConnection
argument_list|()
argument_list|,
name|conn3
operator|.
name|getConnection
argument_list|()
argument_list|)
expr_stmt|;
name|assertNotSame
argument_list|(
name|conn2
operator|.
name|getConnection
argument_list|()
argument_list|,
name|conn3
operator|.
name|getConnection
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|3
argument_list|,
name|cf
operator|.
name|getNumConnections
argument_list|()
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|testConnectionsAreRotated
parameter_list|()
throws|throws
name|Exception
block|{
name|ActiveMQConnectionFactory
name|amq
init|=
operator|new
name|ActiveMQConnectionFactory
argument_list|(
literal|"vm://broker1?marshal=false&broker.persistent=false"
argument_list|)
decl_stmt|;
name|PooledConnectionFactory
name|cf
init|=
operator|new
name|PooledConnectionFactory
argument_list|()
decl_stmt|;
name|cf
operator|.
name|setConnectionFactory
argument_list|(
name|amq
argument_list|)
expr_stmt|;
name|cf
operator|.
name|setMaxConnections
argument_list|(
literal|10
argument_list|)
expr_stmt|;
name|Connection
name|previous
init|=
literal|null
decl_stmt|;
comment|// Front load the pool.
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
operator|++
name|i
control|)
block|{
name|cf
operator|.
name|createConnection
argument_list|()
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
literal|100
condition|;
operator|++
name|i
control|)
block|{
name|Connection
name|current
init|=
operator|(
operator|(
name|PooledConnection
operator|)
name|cf
operator|.
name|createConnection
argument_list|()
operator|)
operator|.
name|getConnection
argument_list|()
decl_stmt|;
name|assertNotSame
argument_list|(
name|previous
argument_list|,
name|current
argument_list|)
expr_stmt|;
name|previous
operator|=
name|current
expr_stmt|;
block|}
block|}
specifier|public
name|void
name|testConnectionsArePooled
parameter_list|()
throws|throws
name|Exception
block|{
name|ActiveMQConnectionFactory
name|amq
init|=
operator|new
name|ActiveMQConnectionFactory
argument_list|(
literal|"vm://broker1?marshal=false&broker.persistent=false"
argument_list|)
decl_stmt|;
name|PooledConnectionFactory
name|cf
init|=
operator|new
name|PooledConnectionFactory
argument_list|()
decl_stmt|;
name|cf
operator|.
name|setConnectionFactory
argument_list|(
name|amq
argument_list|)
expr_stmt|;
name|cf
operator|.
name|setMaxConnections
argument_list|(
literal|1
argument_list|)
expr_stmt|;
name|PooledConnection
name|conn1
init|=
operator|(
name|PooledConnection
operator|)
name|cf
operator|.
name|createConnection
argument_list|()
decl_stmt|;
name|PooledConnection
name|conn2
init|=
operator|(
name|PooledConnection
operator|)
name|cf
operator|.
name|createConnection
argument_list|()
decl_stmt|;
name|PooledConnection
name|conn3
init|=
operator|(
name|PooledConnection
operator|)
name|cf
operator|.
name|createConnection
argument_list|()
decl_stmt|;
name|assertSame
argument_list|(
name|conn1
operator|.
name|getConnection
argument_list|()
argument_list|,
name|conn2
operator|.
name|getConnection
argument_list|()
argument_list|)
expr_stmt|;
name|assertSame
argument_list|(
name|conn1
operator|.
name|getConnection
argument_list|()
argument_list|,
name|conn3
operator|.
name|getConnection
argument_list|()
argument_list|)
expr_stmt|;
name|assertSame
argument_list|(
name|conn2
operator|.
name|getConnection
argument_list|()
argument_list|,
name|conn3
operator|.
name|getConnection
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|cf
operator|.
name|getNumConnections
argument_list|()
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|testConnectionsArePooledAsyncCreate
parameter_list|()
throws|throws
name|Exception
block|{
specifier|final
name|ActiveMQConnectionFactory
name|amq
init|=
operator|new
name|ActiveMQConnectionFactory
argument_list|(
literal|"vm://broker1?marshal=false&broker.persistent=false"
argument_list|)
decl_stmt|;
specifier|final
name|PooledConnectionFactory
name|cf
init|=
operator|new
name|PooledConnectionFactory
argument_list|()
decl_stmt|;
name|cf
operator|.
name|setConnectionFactory
argument_list|(
name|amq
argument_list|)
expr_stmt|;
name|cf
operator|.
name|setMaxConnections
argument_list|(
literal|1
argument_list|)
expr_stmt|;
specifier|final
name|ConcurrentLinkedQueue
argument_list|<
name|PooledConnection
argument_list|>
name|connections
init|=
operator|new
name|ConcurrentLinkedQueue
argument_list|<
name|PooledConnection
argument_list|>
argument_list|()
decl_stmt|;
specifier|final
name|PooledConnection
name|primary
init|=
operator|(
name|PooledConnection
operator|)
name|cf
operator|.
name|createConnection
argument_list|()
decl_stmt|;
specifier|final
name|ExecutorService
name|executor
init|=
name|Executors
operator|.
name|newFixedThreadPool
argument_list|(
literal|10
argument_list|)
decl_stmt|;
specifier|final
name|int
name|numConnections
init|=
literal|100
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
name|numConnections
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
try|try
block|{
name|connections
operator|.
name|add
argument_list|(
operator|(
name|PooledConnection
operator|)
name|cf
operator|.
name|createConnection
argument_list|()
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|JMSException
name|e
parameter_list|)
block|{                     }
block|}
block|}
argument_list|)
expr_stmt|;
block|}
name|assertTrue
argument_list|(
literal|""
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
name|connections
operator|.
name|size
argument_list|()
operator|==
name|numConnections
return|;
block|}
block|}
argument_list|)
argument_list|)
expr_stmt|;
name|executor
operator|.
name|shutdown
argument_list|()
expr_stmt|;
name|assertTrue
argument_list|(
name|executor
operator|.
name|awaitTermination
argument_list|(
literal|5
argument_list|,
name|TimeUnit
operator|.
name|SECONDS
argument_list|)
argument_list|)
expr_stmt|;
for|for
control|(
name|PooledConnection
name|connection
range|:
name|connections
control|)
block|{
name|assertSame
argument_list|(
name|primary
operator|.
name|getConnection
argument_list|()
argument_list|,
name|connection
operator|.
name|getConnection
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|connections
operator|.
name|clear
argument_list|()
expr_stmt|;
name|cf
operator|.
name|stop
argument_list|()
expr_stmt|;
block|}
specifier|public
name|void
name|testConcurrentCreateGetsUniqueConnectionCreateOnDemand
parameter_list|()
throws|throws
name|Exception
block|{
name|doTestConcurrentCreateGetsUniqueConnection
argument_list|(
literal|false
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|testConcurrentCreateGetsUniqueConnectionCreateOnStart
parameter_list|()
throws|throws
name|Exception
block|{
name|doTestConcurrentCreateGetsUniqueConnection
argument_list|(
literal|true
argument_list|)
expr_stmt|;
block|}
specifier|private
name|void
name|doTestConcurrentCreateGetsUniqueConnection
parameter_list|(
name|boolean
name|createOnStart
parameter_list|)
throws|throws
name|Exception
block|{
specifier|final
name|int
name|numConnections
init|=
literal|50
decl_stmt|;
specifier|final
name|ActiveMQConnectionFactory
name|amq
init|=
operator|new
name|ActiveMQConnectionFactory
argument_list|(
literal|"vm://broker1?marshal=false&broker.persistent=false"
argument_list|)
decl_stmt|;
specifier|final
name|PooledConnectionFactory
name|cf
init|=
operator|new
name|PooledConnectionFactory
argument_list|()
decl_stmt|;
name|cf
operator|.
name|setConnectionFactory
argument_list|(
name|amq
argument_list|)
expr_stmt|;
name|cf
operator|.
name|setMaxConnections
argument_list|(
name|numConnections
argument_list|)
expr_stmt|;
name|cf
operator|.
name|setCreateConnectionOnStartup
argument_list|(
name|createOnStart
argument_list|)
expr_stmt|;
specifier|final
name|ConcurrentHashMap
argument_list|<
name|ConnectionId
argument_list|,
name|Connection
argument_list|>
name|connections
init|=
operator|new
name|ConcurrentHashMap
argument_list|<
name|ConnectionId
argument_list|,
name|Connection
argument_list|>
argument_list|()
decl_stmt|;
specifier|final
name|ExecutorService
name|executor
init|=
name|Executors
operator|.
name|newFixedThreadPool
argument_list|(
name|numConnections
operator|/
literal|2
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
name|numConnections
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
try|try
block|{
name|PooledConnection
name|pooled
init|=
operator|(
name|PooledConnection
operator|)
name|cf
operator|.
name|createConnection
argument_list|()
decl_stmt|;
name|ActiveMQConnection
name|amq
init|=
operator|(
name|ActiveMQConnection
operator|)
name|pooled
operator|.
name|getConnection
argument_list|()
decl_stmt|;
name|connections
operator|.
name|put
argument_list|(
name|amq
operator|.
name|getConnectionInfo
argument_list|()
operator|.
name|getConnectionId
argument_list|()
argument_list|,
name|pooled
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|JMSException
name|e
parameter_list|)
block|{                     }
block|}
block|}
argument_list|)
expr_stmt|;
block|}
name|assertTrue
argument_list|(
literal|"Should have all unique connections"
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
name|connections
operator|.
name|size
argument_list|()
operator|==
name|numConnections
return|;
block|}
block|}
argument_list|)
argument_list|)
expr_stmt|;
name|executor
operator|.
name|shutdown
argument_list|()
expr_stmt|;
name|assertTrue
argument_list|(
name|executor
operator|.
name|awaitTermination
argument_list|(
literal|5
argument_list|,
name|TimeUnit
operator|.
name|SECONDS
argument_list|)
argument_list|)
expr_stmt|;
name|connections
operator|.
name|clear
argument_list|()
expr_stmt|;
name|cf
operator|.
name|stop
argument_list|()
expr_stmt|;
block|}
comment|/**      * Tests the behavior of the sessionPool of the PooledConnectionFactory when      * maximum number of sessions are reached.      */
specifier|public
name|void
name|testApp
parameter_list|()
throws|throws
name|Exception
block|{
comment|// using separate thread for testing so that we can interrupt the test
comment|// if the call to get a new session blocks.
comment|// start test runner thread
name|ExecutorService
name|executor
init|=
name|Executors
operator|.
name|newSingleThreadExecutor
argument_list|()
decl_stmt|;
name|Future
argument_list|<
name|Boolean
argument_list|>
name|result
init|=
name|executor
operator|.
name|submit
argument_list|(
operator|new
name|TestRunner
argument_list|()
argument_list|)
decl_stmt|;
comment|// test should not take> 5secs, so test fails i
name|Thread
operator|.
name|sleep
argument_list|(
literal|5
operator|*
literal|1000
argument_list|)
expr_stmt|;
if|if
condition|(
operator|!
name|result
operator|.
name|isDone
argument_list|()
operator|||
operator|!
name|result
operator|.
name|get
argument_list|()
operator|.
name|booleanValue
argument_list|()
condition|)
block|{
name|PooledConnectionFactoryTest
operator|.
name|LOG
operator|.
name|error
argument_list|(
literal|"2nd call to createSession() "
operator|+
literal|"is blocking but should have returned an error instead."
argument_list|)
expr_stmt|;
name|executor
operator|.
name|shutdownNow
argument_list|()
expr_stmt|;
name|fail
argument_list|(
literal|"SessionPool inside PooledConnectionFactory is blocking if "
operator|+
literal|"limit is exceeded but should return an exception instead."
argument_list|)
expr_stmt|;
block|}
block|}
specifier|static
class|class
name|TestRunner
implements|implements
name|Callable
argument_list|<
name|Boolean
argument_list|>
block|{
specifier|public
specifier|final
specifier|static
name|Logger
name|LOG
init|=
name|Logger
operator|.
name|getLogger
argument_list|(
name|TestRunner
operator|.
name|class
argument_list|)
decl_stmt|;
comment|/**          * @return true if test succeeded, false otherwise          */
annotation|@
name|Override
specifier|public
name|Boolean
name|call
parameter_list|()
block|{
name|Connection
name|conn
init|=
literal|null
decl_stmt|;
name|Session
name|one
init|=
literal|null
decl_stmt|;
comment|// wait at most 5 seconds for the call to createSession
try|try
block|{
name|ActiveMQConnectionFactory
name|amq
init|=
operator|new
name|ActiveMQConnectionFactory
argument_list|(
literal|"vm://broker1?marshal=false&broker.persistent=false"
argument_list|)
decl_stmt|;
name|PooledConnectionFactory
name|cf
init|=
operator|new
name|PooledConnectionFactory
argument_list|()
decl_stmt|;
name|cf
operator|.
name|setConnectionFactory
argument_list|(
name|amq
argument_list|)
expr_stmt|;
name|cf
operator|.
name|setMaxConnections
argument_list|(
literal|3
argument_list|)
expr_stmt|;
name|cf
operator|.
name|setMaximumActiveSessionPerConnection
argument_list|(
literal|1
argument_list|)
expr_stmt|;
name|cf
operator|.
name|setBlockIfSessionPoolIsFull
argument_list|(
literal|false
argument_list|)
expr_stmt|;
name|conn
operator|=
name|cf
operator|.
name|createConnection
argument_list|()
expr_stmt|;
name|one
operator|=
name|conn
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
name|Session
name|two
init|=
literal|null
decl_stmt|;
try|try
block|{
comment|// this should raise an exception as we called
comment|// setMaximumActive(1)
name|two
operator|=
name|conn
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
name|two
operator|.
name|close
argument_list|()
expr_stmt|;
name|LOG
operator|.
name|error
argument_list|(
literal|"Expected JMSException wasn't thrown."
argument_list|)
expr_stmt|;
name|fail
argument_list|(
literal|"seconds call to Connection.createSession() was supposed"
operator|+
literal|"to raise an JMSException as internal session pool"
operator|+
literal|"is exhausted. This did not happen and indiates a problem"
argument_list|)
expr_stmt|;
return|return
operator|new
name|Boolean
argument_list|(
literal|false
argument_list|)
return|;
block|}
catch|catch
parameter_list|(
name|JMSException
name|ex
parameter_list|)
block|{
if|if
condition|(
name|ex
operator|.
name|getCause
argument_list|()
operator|.
name|getClass
argument_list|()
operator|==
name|java
operator|.
name|util
operator|.
name|NoSuchElementException
operator|.
name|class
condition|)
block|{
comment|// expected, ignore but log
name|LOG
operator|.
name|info
argument_list|(
literal|"Caught expected "
operator|+
name|ex
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|LOG
operator|.
name|error
argument_list|(
name|ex
argument_list|)
expr_stmt|;
return|return
operator|new
name|Boolean
argument_list|(
literal|false
argument_list|)
return|;
block|}
block|}
finally|finally
block|{
if|if
condition|(
name|one
operator|!=
literal|null
condition|)
name|one
operator|.
name|close
argument_list|()
expr_stmt|;
if|if
condition|(
name|conn
operator|!=
literal|null
condition|)
name|conn
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
catch|catch
parameter_list|(
name|Exception
name|ex
parameter_list|)
block|{
name|LOG
operator|.
name|error
argument_list|(
name|ex
operator|.
name|getMessage
argument_list|()
argument_list|)
expr_stmt|;
return|return
operator|new
name|Boolean
argument_list|(
literal|false
argument_list|)
return|;
block|}
comment|// all good, test succeeded
return|return
operator|new
name|Boolean
argument_list|(
literal|true
argument_list|)
return|;
block|}
block|}
block|}
end_class

end_unit

