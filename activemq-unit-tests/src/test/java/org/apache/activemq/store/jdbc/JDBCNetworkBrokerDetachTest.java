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
name|JDBCNetworkBrokerDetachTest
extends|extends
name|NetworkBrokerDetachTest
block|{
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
name|EmbeddedDataSource
name|dataSource
init|=
operator|(
name|EmbeddedDataSource
operator|)
name|jdbc
operator|.
name|getDataSource
argument_list|()
decl_stmt|;
name|dataSource
operator|.
name|setDatabaseName
argument_list|(
name|broker
operator|.
name|getBrokerName
argument_list|()
argument_list|)
expr_stmt|;
name|dataSource
operator|.
name|getConnection
argument_list|()
operator|.
name|close
argument_list|()
expr_stmt|;
comment|// ensure derby for brokerName is initialized
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
block|}
end_class

end_unit

