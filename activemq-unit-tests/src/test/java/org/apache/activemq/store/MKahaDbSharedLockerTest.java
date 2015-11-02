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
name|util
operator|.
name|LinkedList
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|List
import|;
end_import

begin_import
import|import
name|junit
operator|.
name|framework
operator|.
name|TestCase
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
name|FilteredKahaDBPersistenceAdapter
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
name|store
operator|.
name|kahadb
operator|.
name|MultiKahaDBPersistenceAdapter
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
name|Wait
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
name|io
operator|.
name|FileUtils
import|;
end_import

begin_class
specifier|public
class|class
name|MKahaDbSharedLockerTest
extends|extends
name|TestCase
block|{
specifier|public
name|void
name|testBrokerShutdown
parameter_list|()
throws|throws
name|Exception
block|{
specifier|final
name|BrokerService
name|master
init|=
operator|new
name|BrokerService
argument_list|()
decl_stmt|;
name|master
operator|.
name|setBrokerName
argument_list|(
literal|"master"
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
name|MultiKahaDBPersistenceAdapter
name|mKahaDB
init|=
operator|new
name|MultiKahaDBPersistenceAdapter
argument_list|()
decl_stmt|;
name|mKahaDB
operator|.
name|setDirectory
argument_list|(
operator|new
name|File
argument_list|(
literal|"target/test/kahadb"
argument_list|)
argument_list|)
expr_stmt|;
name|List
name|adapters
init|=
operator|new
name|LinkedList
argument_list|<
name|FilteredKahaDBPersistenceAdapter
argument_list|>
argument_list|()
decl_stmt|;
name|FilteredKahaDBPersistenceAdapter
name|defaultEntry
init|=
operator|new
name|FilteredKahaDBPersistenceAdapter
argument_list|()
decl_stmt|;
name|defaultEntry
operator|.
name|setPersistenceAdapter
argument_list|(
operator|new
name|KahaDBPersistenceAdapter
argument_list|()
argument_list|)
expr_stmt|;
name|defaultEntry
operator|.
name|setPerDestination
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|adapters
operator|.
name|add
argument_list|(
name|defaultEntry
argument_list|)
expr_stmt|;
name|mKahaDB
operator|.
name|setFilteredPersistenceAdapters
argument_list|(
name|adapters
argument_list|)
expr_stmt|;
name|master
operator|.
name|setPersistenceAdapter
argument_list|(
name|mKahaDB
argument_list|)
expr_stmt|;
name|SharedFileLocker
name|sharedFileLocker
init|=
operator|new
name|SharedFileLocker
argument_list|()
decl_stmt|;
name|mKahaDB
operator|.
name|setLockKeepAlivePeriod
argument_list|(
literal|1000
argument_list|)
expr_stmt|;
name|mKahaDB
operator|.
name|setLocker
argument_list|(
name|sharedFileLocker
argument_list|)
expr_stmt|;
name|master
operator|.
name|start
argument_list|()
expr_stmt|;
name|master
operator|.
name|waitUntilStarted
argument_list|()
expr_stmt|;
name|FileUtils
operator|.
name|forceDelete
argument_list|(
operator|new
name|File
argument_list|(
literal|"target/test/kahadb/lock"
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
literal|"broker should be stopped now"
argument_list|,
name|Wait
operator|.
name|waitFor
argument_list|(
operator|new
name|Wait
operator|.
name|Condition
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|boolean
name|isSatisified
parameter_list|()
throws|throws
name|Exception
block|{
return|return
name|master
operator|.
name|isStopped
argument_list|()
return|;
block|}
block|}
argument_list|)
argument_list|)
expr_stmt|;
name|master
operator|.
name|stop
argument_list|()
expr_stmt|;
name|master
operator|.
name|waitUntilStopped
argument_list|()
expr_stmt|;
block|}
block|}
end_class

end_unit

