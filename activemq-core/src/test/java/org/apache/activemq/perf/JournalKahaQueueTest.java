begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  *  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  * http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
package|package
name|org
operator|.
name|apache
operator|.
name|activemq
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
name|activeio
operator|.
name|journal
operator|.
name|active
operator|.
name|JournalImpl
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
name|store
operator|.
name|journal
operator|.
name|JournalPersistenceAdapter
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
name|kahadaptor
operator|.
name|KahaPersistenceAdapter
import|;
end_import

begin_comment
comment|/**  * @version $Revision: 1.3 $  */
end_comment

begin_class
specifier|public
class|class
name|JournalKahaQueueTest
extends|extends
name|SimpleQueueTest
block|{
specifier|protected
name|void
name|configureBroker
parameter_list|(
name|BrokerService
name|answer
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
literal|"target/test-amq-data/perfTest"
argument_list|)
decl_stmt|;
name|File
name|journalDir
init|=
operator|new
name|File
argument_list|(
name|dataFileDir
argument_list|,
literal|"journal"
argument_list|)
operator|.
name|getCanonicalFile
argument_list|()
decl_stmt|;
name|JournalImpl
name|journal
init|=
operator|new
name|JournalImpl
argument_list|(
name|journalDir
argument_list|,
literal|3
argument_list|,
literal|1024
operator|*
literal|1024
operator|*
literal|20
argument_list|)
decl_stmt|;
name|KahaPersistenceAdapter
name|kahaAdaptor
init|=
operator|new
name|KahaPersistenceAdapter
argument_list|(
operator|new
name|File
argument_list|(
name|dataFileDir
argument_list|,
literal|"kaha"
argument_list|)
argument_list|)
decl_stmt|;
name|JournalPersistenceAdapter
name|journalAdaptor
init|=
operator|new
name|JournalPersistenceAdapter
argument_list|(
name|journal
argument_list|,
name|kahaAdaptor
argument_list|,
name|answer
operator|.
name|getTaskRunnerFactory
argument_list|()
argument_list|)
decl_stmt|;
name|journalAdaptor
operator|.
name|setMaxCheckpointWorkers
argument_list|(
literal|1
argument_list|)
expr_stmt|;
name|answer
operator|.
name|setPersistenceAdapter
argument_list|(
name|journalAdaptor
argument_list|)
expr_stmt|;
name|answer
operator|.
name|addConnector
argument_list|(
name|bindAddress
argument_list|)
expr_stmt|;
name|answer
operator|.
name|setDeleteAllMessagesOnStartup
argument_list|(
literal|true
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

