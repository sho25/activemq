begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *<p>  * http://www.apache.org/licenses/LICENSE-2.0  *<p>  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
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
name|org
operator|.
name|apache
operator|.
name|activemq
operator|.
name|ActiveMQXAConnection
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
name|ActiveMQXAConnectionFactory
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
name|commons
operator|.
name|dbcp2
operator|.
name|BasicDataSource
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
name|apache
operator|.
name|derby
operator|.
name|jdbc
operator|.
name|EmbeddedDriver
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
name|XASession
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|sql
operator|.
name|DataSource
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|transaction
operator|.
name|xa
operator|.
name|XAResource
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|transaction
operator|.
name|xa
operator|.
name|Xid
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
import|import static
name|junit
operator|.
name|framework
operator|.
name|TestCase
operator|.
name|assertTrue
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|apache
operator|.
name|activemq
operator|.
name|util
operator|.
name|TestUtils
operator|.
name|createXid
import|;
end_import

begin_class
specifier|public
class|class
name|JDBCCleanupLimitedPoolTest
block|{
name|BrokerService
name|broker
decl_stmt|;
name|JDBCPersistenceAdapter
name|jdbcPersistenceAdapter
decl_stmt|;
name|BasicDataSource
name|pool
decl_stmt|;
name|EmbeddedDataSource
name|derby
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
name|derby
operator|=
operator|new
name|EmbeddedDataSource
argument_list|()
expr_stmt|;
name|derby
operator|.
name|setDatabaseName
argument_list|(
literal|"derbyDb"
argument_list|)
expr_stmt|;
name|derby
operator|.
name|setCreateDatabase
argument_list|(
literal|"create"
argument_list|)
expr_stmt|;
name|broker
operator|=
name|createBroker
argument_list|()
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
name|broker
operator|.
name|stop
argument_list|()
expr_stmt|;
name|pool
operator|.
name|close
argument_list|()
expr_stmt|;
name|DataSourceServiceSupport
operator|.
name|shutdownDefaultDataSource
argument_list|(
name|derby
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
name|broker
operator|=
operator|new
name|BrokerService
argument_list|()
expr_stmt|;
name|broker
operator|.
name|setUseJmx
argument_list|(
literal|false
argument_list|)
expr_stmt|;
name|jdbcPersistenceAdapter
operator|=
operator|new
name|JDBCPersistenceAdapter
argument_list|()
expr_stmt|;
name|jdbcPersistenceAdapter
operator|.
name|deleteAllMessages
argument_list|()
expr_stmt|;
name|jdbcPersistenceAdapter
operator|.
name|setCleanupPeriod
argument_list|(
literal|0
argument_list|)
expr_stmt|;
name|jdbcPersistenceAdapter
operator|.
name|setUseLock
argument_list|(
literal|false
argument_list|)
expr_stmt|;
name|pool
operator|=
operator|new
name|BasicDataSource
argument_list|()
expr_stmt|;
name|pool
operator|.
name|setDriverClassName
argument_list|(
name|EmbeddedDriver
operator|.
name|class
operator|.
name|getCanonicalName
argument_list|()
argument_list|)
expr_stmt|;
name|pool
operator|.
name|setUrl
argument_list|(
literal|"jdbc:derby:derbyDb;create=false"
argument_list|)
expr_stmt|;
name|pool
operator|.
name|setUsername
argument_list|(
literal|"uid"
argument_list|)
expr_stmt|;
name|pool
operator|.
name|setPassword
argument_list|(
literal|"pwd"
argument_list|)
expr_stmt|;
name|pool
operator|.
name|setMaxTotal
argument_list|(
literal|2
argument_list|)
expr_stmt|;
name|jdbcPersistenceAdapter
operator|.
name|setDataSource
argument_list|(
name|pool
argument_list|)
expr_stmt|;
name|broker
operator|.
name|setPersistenceAdapter
argument_list|(
name|jdbcPersistenceAdapter
argument_list|)
expr_stmt|;
name|broker
operator|.
name|addConnector
argument_list|(
literal|"tcp://0.0.0.0:0"
argument_list|)
expr_stmt|;
return|return
name|broker
return|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testNoDeadlockOnXaPoolExhaustion
parameter_list|()
throws|throws
name|Exception
block|{
specifier|final
name|CountDownLatch
name|done
init|=
operator|new
name|CountDownLatch
argument_list|(
literal|1
argument_list|)
decl_stmt|;
specifier|final
name|CountDownLatch
name|doneCommit
init|=
operator|new
name|CountDownLatch
argument_list|(
literal|2000
argument_list|)
decl_stmt|;
specifier|final
name|ActiveMQXAConnectionFactory
name|factory
init|=
operator|new
name|ActiveMQXAConnectionFactory
argument_list|(
name|broker
operator|.
name|getTransportConnectorByScheme
argument_list|(
literal|"tcp"
argument_list|)
operator|.
name|getPublishableConnectString
argument_list|()
argument_list|)
decl_stmt|;
name|ExecutorService
name|executorService
init|=
name|Executors
operator|.
name|newCachedThreadPool
argument_list|()
decl_stmt|;
comment|// some contention over pool of 2
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
literal|3
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
name|ActiveMQXAConnection
name|conn
init|=
operator|(
name|ActiveMQXAConnection
operator|)
name|factory
operator|.
name|createXAConnection
argument_list|()
decl_stmt|;
name|conn
operator|.
name|start
argument_list|()
expr_stmt|;
name|XASession
name|sess
init|=
name|conn
operator|.
name|createXASession
argument_list|()
decl_stmt|;
while|while
condition|(
name|done
operator|.
name|getCount
argument_list|()
operator|>
literal|0
operator|&&
name|doneCommit
operator|.
name|getCount
argument_list|()
operator|>
literal|0
condition|)
block|{
name|Xid
name|xid
init|=
name|createXid
argument_list|()
decl_stmt|;
name|sess
operator|.
name|getXAResource
argument_list|()
operator|.
name|start
argument_list|(
name|xid
argument_list|,
name|XAResource
operator|.
name|TMNOFLAGS
argument_list|)
expr_stmt|;
name|MessageProducer
name|producer
init|=
name|sess
operator|.
name|createProducer
argument_list|(
name|sess
operator|.
name|createQueue
argument_list|(
literal|"test"
argument_list|)
argument_list|)
decl_stmt|;
name|producer
operator|.
name|send
argument_list|(
name|sess
operator|.
name|createTextMessage
argument_list|(
literal|"test"
argument_list|)
argument_list|)
expr_stmt|;
name|sess
operator|.
name|getXAResource
argument_list|()
operator|.
name|end
argument_list|(
name|xid
argument_list|,
name|XAResource
operator|.
name|TMSUCCESS
argument_list|)
expr_stmt|;
name|sess
operator|.
name|getXAResource
argument_list|()
operator|.
name|prepare
argument_list|(
name|xid
argument_list|)
expr_stmt|;
name|sess
operator|.
name|getXAResource
argument_list|()
operator|.
name|commit
argument_list|(
name|xid
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|doneCommit
operator|.
name|countDown
argument_list|()
expr_stmt|;
block|}
name|conn
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
block|{
name|ignored
operator|.
name|printStackTrace
argument_list|()
expr_stmt|;
block|}
block|}
block|}
argument_list|)
expr_stmt|;
block|}
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
while|while
condition|(
operator|!
name|done
operator|.
name|await
argument_list|(
literal|10
argument_list|,
name|TimeUnit
operator|.
name|MILLISECONDS
argument_list|)
operator|&&
name|doneCommit
operator|.
name|getCount
argument_list|()
operator|>
literal|0
condition|)
block|{
name|jdbcPersistenceAdapter
operator|.
name|cleanup
argument_list|()
expr_stmt|;
block|}
block|}
catch|catch
parameter_list|(
name|Exception
name|ignored
parameter_list|)
block|{                 }
block|}
block|}
argument_list|)
expr_stmt|;
name|executorService
operator|.
name|shutdown
argument_list|()
expr_stmt|;
name|boolean
name|allComplete
init|=
name|executorService
operator|.
name|awaitTermination
argument_list|(
literal|20
argument_list|,
name|TimeUnit
operator|.
name|SECONDS
argument_list|)
decl_stmt|;
name|done
operator|.
name|countDown
argument_list|()
expr_stmt|;
name|assertTrue
argument_list|(
literal|"all complete"
argument_list|,
name|allComplete
argument_list|)
expr_stmt|;
name|executorService
operator|.
name|shutdownNow
argument_list|()
expr_stmt|;
name|assertTrue
argument_list|(
literal|"xa tx done"
argument_list|,
name|doneCommit
operator|.
name|await
argument_list|(
literal|10
argument_list|,
name|TimeUnit
operator|.
name|SECONDS
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

