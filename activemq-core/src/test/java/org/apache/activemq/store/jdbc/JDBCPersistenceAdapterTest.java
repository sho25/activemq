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
name|junit
operator|.
name|framework
operator|.
name|AssertionFailedError
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
name|PersistenceAdapter
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
name|PersistenceAdapterTestSupport
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
name|JDBCPersistenceAdapterTest
extends|extends
name|PersistenceAdapterTestSupport
block|{
specifier|protected
name|PersistenceAdapter
name|createPersistenceAdapter
parameter_list|(
name|boolean
name|delete
parameter_list|)
throws|throws
name|IOException
block|{
name|JDBCPersistenceAdapter
name|jdbc
init|=
operator|new
name|JDBCPersistenceAdapter
argument_list|()
decl_stmt|;
name|brokerService
operator|.
name|setSchedulerSupport
argument_list|(
literal|false
argument_list|)
expr_stmt|;
name|brokerService
operator|.
name|setPersistenceAdapter
argument_list|(
name|jdbc
argument_list|)
expr_stmt|;
name|jdbc
operator|.
name|setBrokerService
argument_list|(
name|brokerService
argument_list|)
expr_stmt|;
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
if|if
condition|(
name|delete
condition|)
block|{
name|jdbc
operator|.
name|deleteAllMessages
argument_list|()
expr_stmt|;
block|}
return|return
name|jdbc
return|;
block|}
specifier|public
name|void
name|testAuditOff
parameter_list|()
throws|throws
name|Exception
block|{
name|pa
operator|.
name|stop
argument_list|()
expr_stmt|;
name|pa
operator|=
name|createPersistenceAdapter
argument_list|(
literal|true
argument_list|)
expr_stmt|;
operator|(
operator|(
name|JDBCPersistenceAdapter
operator|)
name|pa
operator|)
operator|.
name|setEnableAudit
argument_list|(
literal|false
argument_list|)
expr_stmt|;
name|pa
operator|.
name|start
argument_list|()
expr_stmt|;
name|boolean
name|failed
init|=
literal|true
decl_stmt|;
try|try
block|{
name|testStoreCanHandleDupMessages
argument_list|()
expr_stmt|;
name|failed
operator|=
literal|false
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|AssertionFailedError
name|e
parameter_list|)
block|{     	}
if|if
condition|(
operator|!
name|failed
condition|)
block|{
name|fail
argument_list|(
literal|"Should have failed with audit turned off"
argument_list|)
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

