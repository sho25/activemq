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
name|command
operator|.
name|ActiveMQDestination
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
name|EmbeddedXADataSource
import|;
end_import

begin_class
specifier|public
class|class
name|JdbcXARecoveryBrokerTest
extends|extends
name|XARecoveryBrokerTest
block|{
name|EmbeddedXADataSource
name|dataSource
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
name|dataSource
operator|=
operator|new
name|EmbeddedXADataSource
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
name|super
operator|.
name|setUp
argument_list|()
expr_stmt|;
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
name|super
operator|.
name|tearDown
argument_list|()
expr_stmt|;
name|stopDerby
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Override
specifier|protected
name|void
name|configureBroker
parameter_list|(
name|BrokerService
name|broker
parameter_list|)
throws|throws
name|Exception
block|{
name|super
operator|.
name|configureBroker
argument_list|(
name|broker
argument_list|)
expr_stmt|;
name|JDBCPersistenceAdapter
name|jdbc
init|=
operator|new
name|JDBCPersistenceAdapter
argument_list|()
decl_stmt|;
name|jdbc
operator|.
name|setDataSource
argument_list|(
name|dataSource
argument_list|)
expr_stmt|;
name|broker
operator|.
name|setPersistenceAdapter
argument_list|(
name|jdbc
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|protected
name|void
name|restartBroker
parameter_list|()
throws|throws
name|Exception
block|{
name|broker
operator|.
name|stop
argument_list|()
expr_stmt|;
name|stopDerby
argument_list|()
expr_stmt|;
name|dataSource
operator|=
operator|new
name|EmbeddedXADataSource
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
name|broker
operator|=
name|createRestartedBroker
argument_list|()
expr_stmt|;
name|broker
operator|.
name|start
argument_list|()
expr_stmt|;
block|}
specifier|private
name|void
name|stopDerby
parameter_list|()
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"STOPPING DB!@!!!!"
argument_list|)
expr_stmt|;
specifier|final
name|EmbeddedDataSource
name|ds
init|=
name|dataSource
decl_stmt|;
try|try
block|{
name|ds
operator|.
name|setShutdownDatabase
argument_list|(
literal|"shutdown"
argument_list|)
expr_stmt|;
name|ds
operator|.
name|getConnection
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|ignored
parameter_list|)
block|{         }
block|}
specifier|public
specifier|static
name|Test
name|suite
parameter_list|()
block|{
return|return
name|suite
argument_list|(
name|JdbcXARecoveryBrokerTest
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
annotation|@
name|Override
specifier|protected
name|ActiveMQDestination
name|createDestination
parameter_list|()
block|{
return|return
operator|new
name|ActiveMQQueue
argument_list|(
literal|"test,special"
argument_list|)
return|;
block|}
block|}
end_class

end_unit
