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
name|disk
operator|.
name|journal
package|;
end_package

begin_import
import|import static
name|org
operator|.
name|junit
operator|.
name|Assert
operator|.
name|assertFalse
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|junit
operator|.
name|Assert
operator|.
name|assertTrue
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
name|TimeUnit
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
name|disk
operator|.
name|journal
operator|.
name|Journal
operator|.
name|JournalDiskSyncStrategy
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
name|Rule
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
name|org
operator|.
name|junit
operator|.
name|rules
operator|.
name|TemporaryFolder
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|rules
operator|.
name|Timeout
import|;
end_import

begin_class
specifier|public
class|class
name|DataFileAppenderSyncStrategyTest
block|{
annotation|@
name|Rule
specifier|public
name|TemporaryFolder
name|dataFileDir
init|=
operator|new
name|TemporaryFolder
argument_list|(
operator|new
name|File
argument_list|(
literal|"target"
argument_list|)
argument_list|)
decl_stmt|;
annotation|@
name|Rule
specifier|public
name|Timeout
name|globalTimeout
init|=
operator|new
name|Timeout
argument_list|(
literal|10
argument_list|,
name|TimeUnit
operator|.
name|SECONDS
argument_list|)
decl_stmt|;
specifier|private
name|KahaDBStore
name|store
decl_stmt|;
specifier|private
name|int
name|defaultJournalLength
init|=
literal|10
operator|*
literal|1024
decl_stmt|;
annotation|@
name|After
specifier|public
name|void
name|after
parameter_list|()
throws|throws
name|Exception
block|{
if|if
condition|(
name|store
operator|!=
literal|null
condition|)
block|{
name|store
operator|.
name|stop
argument_list|()
expr_stmt|;
block|}
block|}
annotation|@
name|Test
specifier|public
name|void
name|testPeriodicSync
parameter_list|()
throws|throws
name|Exception
block|{
name|store
operator|=
name|configureStore
argument_list|(
name|JournalDiskSyncStrategy
operator|.
name|PERIODIC
argument_list|)
expr_stmt|;
name|store
operator|.
name|start
argument_list|()
expr_stmt|;
specifier|final
name|Journal
name|journal
init|=
name|store
operator|.
name|getJournal
argument_list|()
decl_stmt|;
name|DataFileAppender
name|appender
init|=
operator|(
name|DataFileAppender
operator|)
name|journal
operator|.
name|appender
decl_stmt|;
name|assertTrue
argument_list|(
name|appender
operator|.
name|periodicSync
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testAlwaysSync
parameter_list|()
throws|throws
name|Exception
block|{
name|store
operator|=
name|configureStore
argument_list|(
name|JournalDiskSyncStrategy
operator|.
name|ALWAYS
argument_list|)
expr_stmt|;
name|store
operator|.
name|start
argument_list|()
expr_stmt|;
specifier|final
name|Journal
name|journal
init|=
name|store
operator|.
name|getJournal
argument_list|()
decl_stmt|;
name|DataFileAppender
name|appender
init|=
operator|(
name|DataFileAppender
operator|)
name|journal
operator|.
name|appender
decl_stmt|;
name|assertFalse
argument_list|(
name|appender
operator|.
name|periodicSync
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testNeverSync
parameter_list|()
throws|throws
name|Exception
block|{
name|store
operator|=
name|configureStore
argument_list|(
name|JournalDiskSyncStrategy
operator|.
name|NEVER
argument_list|)
expr_stmt|;
name|store
operator|.
name|start
argument_list|()
expr_stmt|;
specifier|final
name|Journal
name|journal
init|=
name|store
operator|.
name|getJournal
argument_list|()
decl_stmt|;
name|DataFileAppender
name|appender
init|=
operator|(
name|DataFileAppender
operator|)
name|journal
operator|.
name|appender
decl_stmt|;
name|assertFalse
argument_list|(
name|appender
operator|.
name|periodicSync
argument_list|)
expr_stmt|;
block|}
specifier|private
name|KahaDBStore
name|configureStore
parameter_list|(
name|JournalDiskSyncStrategy
name|strategy
parameter_list|)
throws|throws
name|Exception
block|{
name|KahaDBStore
name|store
init|=
operator|new
name|KahaDBStore
argument_list|()
decl_stmt|;
name|store
operator|.
name|setJournalMaxFileLength
argument_list|(
name|defaultJournalLength
argument_list|)
expr_stmt|;
name|store
operator|.
name|deleteAllMessages
argument_list|()
expr_stmt|;
name|store
operator|.
name|setDirectory
argument_list|(
name|dataFileDir
operator|.
name|getRoot
argument_list|()
argument_list|)
expr_stmt|;
if|if
condition|(
name|strategy
operator|!=
literal|null
condition|)
block|{
name|store
operator|.
name|setJournalDiskSyncStrategy
argument_list|(
name|strategy
operator|.
name|name
argument_list|()
argument_list|)
expr_stmt|;
block|}
return|return
name|store
return|;
block|}
block|}
end_class

end_unit

