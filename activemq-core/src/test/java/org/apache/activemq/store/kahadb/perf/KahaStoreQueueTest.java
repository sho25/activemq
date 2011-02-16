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
name|kahadb
operator|.
name|perf
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
name|perf
operator|.
name|SimpleQueueTest
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
name|kahadb
operator|.
name|KahaDBStore
import|;
end_import

begin_comment
comment|/**  *   */
end_comment

begin_class
specifier|public
class|class
name|KahaStoreQueueTest
extends|extends
name|SimpleQueueTest
block|{
specifier|protected
name|void
name|configureBroker
parameter_list|(
name|BrokerService
name|answer
parameter_list|,
name|String
name|uri
parameter_list|)
throws|throws
name|Exception
block|{
name|File
name|dataFileDir
init|=
operator|new
name|File
argument_list|(
literal|"target/test-amq-data/perfTest/amqdb"
argument_list|)
decl_stmt|;
name|dataFileDir
operator|.
name|mkdirs
argument_list|()
expr_stmt|;
name|answer
operator|.
name|setDeleteAllMessagesOnStartup
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|KahaDBStore
name|adaptor
init|=
operator|new
name|KahaDBStore
argument_list|()
decl_stmt|;
name|adaptor
operator|.
name|setDirectory
argument_list|(
name|dataFileDir
argument_list|)
expr_stmt|;
name|answer
operator|.
name|setDataDirectoryFile
argument_list|(
name|dataFileDir
argument_list|)
expr_stmt|;
name|answer
operator|.
name|setPersistenceAdapter
argument_list|(
name|adaptor
argument_list|)
expr_stmt|;
name|answer
operator|.
name|addConnector
argument_list|(
name|uri
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

