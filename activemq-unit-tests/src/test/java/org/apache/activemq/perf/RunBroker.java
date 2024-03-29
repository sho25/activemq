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
name|store
operator|.
name|kahadb
operator|.
name|KahaDBPersistenceAdapter
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

begin_class
specifier|public
class|class
name|RunBroker
block|{
specifier|public
specifier|static
name|void
name|main
parameter_list|(
name|String
name|arg
index|[]
parameter_list|)
block|{
try|try
block|{
name|KahaDBPersistenceAdapter
name|kahaDB
init|=
operator|new
name|KahaDBPersistenceAdapter
argument_list|()
decl_stmt|;
name|File
name|dataFileDir
init|=
operator|new
name|File
argument_list|(
literal|"target/test-amq-data/perfTest/kahadb"
argument_list|)
decl_stmt|;
name|IOHelper
operator|.
name|deleteChildren
argument_list|(
name|dataFileDir
argument_list|)
expr_stmt|;
name|kahaDB
operator|.
name|setDirectory
argument_list|(
name|dataFileDir
argument_list|)
expr_stmt|;
comment|// The setEnableJournalDiskSyncs(false) setting is a little
comment|// dangerous right now, as I have not verified
comment|// what happens if the index is updated but a journal update is
comment|// lost.
comment|// Index is going to be in consistent, but can it be repaired?
comment|// kaha.setEnableJournalDiskSyncs(false);
comment|// Using a bigger journal file size makes he take fewer spikes as it
comment|// is not switching files as often.
comment|// kaha.setJournalMaxFileLength(1024*1024*100);
comment|// small batch means more frequent and smaller writes
name|kahaDB
operator|.
name|setIndexWriteBatchSize
argument_list|(
literal|1000
argument_list|)
expr_stmt|;
name|kahaDB
operator|.
name|setIndexCacheSize
argument_list|(
literal|10000
argument_list|)
expr_stmt|;
comment|// do the index write in a separate thread
comment|// kahaDB.setEnableIndexWriteAsync(true);
name|BrokerService
name|broker
init|=
operator|new
name|BrokerService
argument_list|()
decl_stmt|;
name|broker
operator|.
name|setUseJmx
argument_list|(
literal|false
argument_list|)
expr_stmt|;
comment|// broker.setPersistenceAdapter(adaptor);
name|broker
operator|.
name|setPersistenceAdapter
argument_list|(
name|kahaDB
argument_list|)
expr_stmt|;
comment|// broker.setPersistent(false);
name|broker
operator|.
name|setDeleteAllMessagesOnStartup
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|broker
operator|.
name|addConnector
argument_list|(
literal|"tcp://0.0.0.0:61616"
argument_list|)
expr_stmt|;
name|broker
operator|.
name|start
argument_list|()
expr_stmt|;
name|System
operator|.
name|err
operator|.
name|println
argument_list|(
literal|"Running"
argument_list|)
expr_stmt|;
name|Thread
operator|.
name|sleep
argument_list|(
name|Long
operator|.
name|MAX_VALUE
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Throwable
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
end_class

end_unit

