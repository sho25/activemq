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
name|store
operator|.
name|jdbc
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
name|sql
operator|.
name|Connection
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
name|AtomicBoolean
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
import|import static
name|junit
operator|.
name|framework
operator|.
name|Assert
operator|.
name|assertFalse
import|;
end_import

begin_import
import|import static
name|junit
operator|.
name|framework
operator|.
name|Assert
operator|.
name|assertTrue
import|;
end_import

begin_class
specifier|public
class|class
name|LeaseDatabaseLockerTest
block|{
name|JDBCPersistenceAdapter
name|jdbc
decl_stmt|;
name|BrokerService
name|brokerService
decl_stmt|;
name|EmbeddedDataSource
name|dataSource
decl_stmt|;
annotation|@
name|Before
specifier|public
name|void
name|setUpStore
parameter_list|()
throws|throws
name|Exception
block|{
name|dataSource
operator|=
operator|new
name|EmbeddedDataSource
argument_list|()
expr_stmt|;
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
operator|=
operator|new
name|JDBCPersistenceAdapter
argument_list|()
expr_stmt|;
name|jdbc
operator|.
name|setDataSource
argument_list|(
name|dataSource
argument_list|)
expr_stmt|;
name|brokerService
operator|=
operator|new
name|BrokerService
argument_list|()
expr_stmt|;
name|jdbc
operator|.
name|setBrokerService
argument_list|(
name|brokerService
argument_list|)
expr_stmt|;
name|jdbc
operator|.
name|getAdapter
argument_list|()
operator|.
name|doCreateTables
argument_list|(
name|jdbc
operator|.
name|getTransactionContext
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testLockInterleave
parameter_list|()
throws|throws
name|Exception
block|{
name|LeaseDatabaseLocker
name|lockerA
init|=
operator|new
name|LeaseDatabaseLocker
argument_list|()
decl_stmt|;
name|brokerService
operator|.
name|setBrokerName
argument_list|(
literal|"First"
argument_list|)
expr_stmt|;
name|lockerA
operator|.
name|setPersistenceAdapter
argument_list|(
name|jdbc
argument_list|)
expr_stmt|;
specifier|final
name|LeaseDatabaseLocker
name|lockerB
init|=
operator|new
name|LeaseDatabaseLocker
argument_list|()
decl_stmt|;
name|brokerService
operator|.
name|setBrokerName
argument_list|(
literal|"Second"
argument_list|)
expr_stmt|;
name|lockerB
operator|.
name|setPersistenceAdapter
argument_list|(
name|jdbc
argument_list|)
expr_stmt|;
specifier|final
name|AtomicBoolean
name|blocked
init|=
operator|new
name|AtomicBoolean
argument_list|(
literal|true
argument_list|)
decl_stmt|;
specifier|final
name|Connection
name|connection
init|=
name|dataSource
operator|.
name|getConnection
argument_list|()
decl_stmt|;
name|printLockTable
argument_list|(
name|connection
argument_list|)
expr_stmt|;
name|lockerA
operator|.
name|start
argument_list|()
expr_stmt|;
name|printLockTable
argument_list|(
name|connection
argument_list|)
expr_stmt|;
name|ExecutorService
name|executor
init|=
name|Executors
operator|.
name|newCachedThreadPool
argument_list|()
decl_stmt|;
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
name|lockerB
operator|.
name|start
argument_list|()
expr_stmt|;
name|blocked
operator|.
name|set
argument_list|(
literal|false
argument_list|)
expr_stmt|;
name|printLockTable
argument_list|(
name|connection
argument_list|)
expr_stmt|;
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
block|}
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
literal|"B is blocked"
argument_list|,
name|blocked
operator|.
name|get
argument_list|()
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
literal|"A is good"
argument_list|,
name|lockerA
operator|.
name|keepAlive
argument_list|()
argument_list|)
expr_stmt|;
name|printLockTable
argument_list|(
name|connection
argument_list|)
expr_stmt|;
name|lockerA
operator|.
name|stop
argument_list|()
expr_stmt|;
name|printLockTable
argument_list|(
name|connection
argument_list|)
expr_stmt|;
name|TimeUnit
operator|.
name|MILLISECONDS
operator|.
name|sleep
argument_list|(
literal|2
operator|*
name|lockerB
operator|.
name|getLockAcquireSleepInterval
argument_list|()
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
literal|"lockerB has the lock"
argument_list|,
name|blocked
operator|.
name|get
argument_list|()
argument_list|)
expr_stmt|;
name|lockerB
operator|.
name|stop
argument_list|()
expr_stmt|;
name|printLockTable
argument_list|(
name|connection
argument_list|)
expr_stmt|;
block|}
specifier|private
name|void
name|printLockTable
parameter_list|(
name|Connection
name|connection
parameter_list|)
throws|throws
name|IOException
block|{
comment|//((DefaultJDBCAdapter)jdbc.getAdapter()).printQuery(connection, "SELECT * from ACTIVEMQ_LOCK", System.err);
block|}
block|}
end_class

end_unit
