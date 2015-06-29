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
name|File
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
name|java
operator|.
name|util
operator|.
name|LinkedList
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
name|network
operator|.
name|NetworkBrokerDetachTest
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
name|After
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|BeforeClass
import|;
end_import

begin_class
specifier|public
class|class
name|JDBCNetworkBrokerDetachTest
extends|extends
name|NetworkBrokerDetachTest
block|{
name|LinkedList
argument_list|<
name|EmbeddedDataSource
argument_list|>
name|dataSources
init|=
operator|new
name|LinkedList
argument_list|<>
argument_list|()
decl_stmt|;
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
name|JDBCPersistenceAdapter
name|jdbc
init|=
operator|new
name|JDBCPersistenceAdapter
argument_list|()
decl_stmt|;
try|try
block|{
name|EmbeddedDataSource
name|dataSource
init|=
operator|(
name|EmbeddedDataSource
operator|)
name|DataSourceServiceSupport
operator|.
name|createDataSource
argument_list|(
name|jdbc
operator|.
name|getDataDirectoryFile
argument_list|()
operator|.
name|getCanonicalPath
argument_list|()
argument_list|,
name|broker
operator|.
name|getBrokerName
argument_list|()
argument_list|)
decl_stmt|;
name|dataSource
operator|.
name|getConnection
argument_list|()
operator|.
name|close
argument_list|()
expr_stmt|;
comment|// ensure derby for brokerName is initialized
name|jdbc
operator|.
name|setDataSource
argument_list|(
name|dataSource
argument_list|)
expr_stmt|;
name|dataSources
operator|.
name|add
argument_list|(
name|dataSource
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|SQLException
name|e
parameter_list|)
block|{
name|e
operator|.
name|printStackTrace
argument_list|()
expr_stmt|;
name|Exception
name|n
init|=
name|e
operator|.
name|getNextException
argument_list|()
decl_stmt|;
while|while
condition|(
name|n
operator|!=
literal|null
condition|)
block|{
name|n
operator|.
name|printStackTrace
argument_list|()
expr_stmt|;
if|if
condition|(
name|n
operator|instanceof
name|SQLException
condition|)
block|{
name|n
operator|=
operator|(
operator|(
name|SQLException
operator|)
name|n
operator|)
operator|.
name|getNextException
argument_list|()
expr_stmt|;
block|}
block|}
throw|throw
name|e
throw|;
block|}
name|broker
operator|.
name|setPersistenceAdapter
argument_list|(
name|jdbc
argument_list|)
expr_stmt|;
name|broker
operator|.
name|setUseVirtualTopics
argument_list|(
literal|false
argument_list|)
expr_stmt|;
block|}
annotation|@
name|After
specifier|public
name|void
name|shutdownDataSources
parameter_list|()
throws|throws
name|Exception
block|{
for|for
control|(
name|EmbeddedDataSource
name|ds
range|:
name|dataSources
control|)
block|{
name|DataSourceServiceSupport
operator|.
name|shutdownDefaultDataSource
argument_list|(
name|ds
argument_list|)
expr_stmt|;
block|}
name|dataSources
operator|.
name|clear
argument_list|()
expr_stmt|;
block|}
annotation|@
name|BeforeClass
specifier|public
specifier|static
name|void
name|ensureDerbyHasCleanDirectory
parameter_list|()
throws|throws
name|Exception
block|{
name|IOHelper
operator|.
name|delete
argument_list|(
operator|new
name|File
argument_list|(
name|IOHelper
operator|.
name|getDefaultDataDirectory
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

