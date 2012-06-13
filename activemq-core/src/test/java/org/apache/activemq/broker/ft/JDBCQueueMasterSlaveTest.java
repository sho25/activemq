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
operator|.
name|ft
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
name|InetSocketAddress
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
name|sql
operator|.
name|Connection
import|;
end_import

begin_import
import|import
name|java
operator|.
name|sql
operator|.
name|SQLException
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
name|BrokerInfo
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
name|DataSourceSupport
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
name|transport
operator|.
name|TransportAcceptListener
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
name|TransportServer
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

begin_class
specifier|public
class|class
name|JDBCQueueMasterSlaveTest
extends|extends
name|QueueMasterSlaveTest
block|{
specifier|protected
name|EmbeddedDataSource
name|sharedDs
decl_stmt|;
specifier|protected
name|String
name|MASTER_URL
init|=
literal|"tcp://localhost:62001"
decl_stmt|;
specifier|protected
name|String
name|SLAVE_URL
init|=
literal|"tcp://localhost:62002"
decl_stmt|;
specifier|protected
name|void
name|setUp
parameter_list|()
throws|throws
name|Exception
block|{
comment|// startup db
name|sharedDs
operator|=
operator|(
name|EmbeddedDataSource
operator|)
operator|new
name|DataSourceSupport
argument_list|()
operator|.
name|getDataSource
argument_list|()
expr_stmt|;
name|super
operator|.
name|setUp
argument_list|()
expr_stmt|;
block|}
specifier|protected
name|void
name|createMaster
parameter_list|()
throws|throws
name|Exception
block|{
name|master
operator|=
operator|new
name|BrokerService
argument_list|()
expr_stmt|;
name|master
operator|.
name|setBrokerName
argument_list|(
literal|"master"
argument_list|)
expr_stmt|;
name|master
operator|.
name|addConnector
argument_list|(
name|MASTER_URL
argument_list|)
expr_stmt|;
name|master
operator|.
name|setUseJmx
argument_list|(
literal|false
argument_list|)
expr_stmt|;
name|master
operator|.
name|setPersistent
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|master
operator|.
name|setDeleteAllMessagesOnStartup
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|JDBCPersistenceAdapter
name|persistenceAdapter
init|=
operator|new
name|JDBCPersistenceAdapter
argument_list|()
decl_stmt|;
name|persistenceAdapter
operator|.
name|setDataSource
argument_list|(
name|getExistingDataSource
argument_list|()
argument_list|)
expr_stmt|;
name|configureJdbcPersistenceAdapter
argument_list|(
name|persistenceAdapter
argument_list|)
expr_stmt|;
name|master
operator|.
name|setPersistenceAdapter
argument_list|(
name|persistenceAdapter
argument_list|)
expr_stmt|;
name|configureBroker
argument_list|(
name|master
argument_list|)
expr_stmt|;
name|master
operator|.
name|start
argument_list|()
expr_stmt|;
block|}
specifier|protected
name|void
name|configureBroker
parameter_list|(
name|BrokerService
name|master
parameter_list|)
block|{     }
specifier|protected
name|void
name|createSlave
parameter_list|()
throws|throws
name|Exception
block|{
comment|// use a separate thread as the slave will block waiting for
comment|// the exclusive db lock
name|Thread
name|t
init|=
operator|new
name|Thread
argument_list|()
block|{
specifier|public
name|void
name|run
parameter_list|()
block|{
try|try
block|{
name|BrokerService
name|broker
init|=
operator|new
name|BrokerService
argument_list|()
decl_stmt|;
name|broker
operator|.
name|setBrokerName
argument_list|(
literal|"slave"
argument_list|)
expr_stmt|;
name|TransportConnector
name|connector
init|=
operator|new
name|TransportConnector
argument_list|()
decl_stmt|;
name|connector
operator|.
name|setUri
argument_list|(
operator|new
name|URI
argument_list|(
name|SLAVE_URL
argument_list|)
argument_list|)
expr_stmt|;
name|broker
operator|.
name|addConnector
argument_list|(
name|connector
argument_list|)
expr_stmt|;
comment|// no need for broker.setMasterConnectorURI(masterConnectorURI)
comment|// as the db lock provides the slave/master initialisation
name|broker
operator|.
name|setUseJmx
argument_list|(
literal|false
argument_list|)
expr_stmt|;
name|broker
operator|.
name|setPersistent
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|JDBCPersistenceAdapter
name|persistenceAdapter
init|=
operator|new
name|JDBCPersistenceAdapter
argument_list|()
decl_stmt|;
name|persistenceAdapter
operator|.
name|setDataSource
argument_list|(
name|getExistingDataSource
argument_list|()
argument_list|)
expr_stmt|;
name|persistenceAdapter
operator|.
name|setCreateTablesOnStartup
argument_list|(
literal|false
argument_list|)
expr_stmt|;
name|broker
operator|.
name|setPersistenceAdapter
argument_list|(
name|persistenceAdapter
argument_list|)
expr_stmt|;
name|configureJdbcPersistenceAdapter
argument_list|(
name|persistenceAdapter
argument_list|)
expr_stmt|;
name|configureBroker
argument_list|(
name|broker
argument_list|)
expr_stmt|;
name|broker
operator|.
name|start
argument_list|()
expr_stmt|;
name|slave
operator|.
name|set
argument_list|(
name|broker
argument_list|)
expr_stmt|;
name|slaveStarted
operator|.
name|countDown
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IllegalStateException
name|expectedOnShutdown
parameter_list|)
block|{                 }
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
name|fail
argument_list|(
literal|"failed to start slave broker, reason:"
operator|+
name|e
argument_list|)
expr_stmt|;
block|}
block|}
block|}
decl_stmt|;
name|t
operator|.
name|start
argument_list|()
expr_stmt|;
block|}
specifier|protected
name|void
name|configureJdbcPersistenceAdapter
parameter_list|(
name|JDBCPersistenceAdapter
name|persistenceAdapter
parameter_list|)
throws|throws
name|IOException
block|{
name|persistenceAdapter
operator|.
name|setLockKeepAlivePeriod
argument_list|(
literal|500
argument_list|)
expr_stmt|;
name|persistenceAdapter
operator|.
name|setLockAcquireSleepInterval
argument_list|(
literal|500
argument_list|)
expr_stmt|;
block|}
specifier|protected
name|EmbeddedDataSource
name|getExistingDataSource
parameter_list|()
throws|throws
name|Exception
block|{
return|return
name|sharedDs
return|;
block|}
block|}
end_class

end_unit

