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
name|util
operator|.
name|Wait
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
name|Before
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
name|slf4j
operator|.
name|Logger
import|;
end_import

begin_import
import|import
name|org
operator|.
name|slf4j
operator|.
name|LoggerFactory
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
name|io
operator|.
name|RandomAccessFile
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Date
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

begin_class
specifier|public
class|class
name|KahaDBDeleteLockTest
block|{
specifier|static
specifier|final
name|Logger
name|LOG
init|=
name|LoggerFactory
operator|.
name|getLogger
argument_list|(
name|KahaDBDeleteLockTest
operator|.
name|class
argument_list|)
decl_stmt|;
specifier|protected
name|BrokerService
name|master
decl_stmt|;
specifier|protected
name|KahaDBPersistenceAdapter
name|masterPersistenceAdapter
init|=
operator|new
name|KahaDBPersistenceAdapter
argument_list|()
decl_stmt|;
specifier|private
specifier|final
name|File
name|testDataDir
init|=
operator|new
name|File
argument_list|(
literal|"target/activemq-data/KahaDBDeleteLockTest"
argument_list|)
decl_stmt|;
specifier|private
specifier|final
name|File
name|kahaDataDir
init|=
operator|new
name|File
argument_list|(
name|testDataDir
argument_list|,
literal|"kahadb"
argument_list|)
decl_stmt|;
annotation|@
name|Before
specifier|public
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
name|setDeleteAllMessagesOnStartup
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|master
operator|.
name|setBrokerName
argument_list|(
literal|"Master"
argument_list|)
expr_stmt|;
name|master
operator|.
name|setDataDirectoryFile
argument_list|(
name|testDataDir
argument_list|)
expr_stmt|;
name|masterPersistenceAdapter
operator|.
name|setDirectory
argument_list|(
name|kahaDataDir
argument_list|)
expr_stmt|;
name|masterPersistenceAdapter
operator|.
name|setLockKeepAlivePeriod
argument_list|(
literal|500
argument_list|)
expr_stmt|;
comment|// ensure broker creates the file
name|File
name|lockFile
init|=
operator|new
name|File
argument_list|(
name|kahaDataDir
argument_list|,
literal|"lock"
argument_list|)
decl_stmt|;
if|if
condition|(
name|lockFile
operator|.
name|exists
argument_list|()
condition|)
block|{
name|lockFile
operator|.
name|delete
argument_list|()
expr_stmt|;
block|}
name|master
operator|.
name|setPersistenceAdapter
argument_list|(
name|masterPersistenceAdapter
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
block|}
annotation|@
name|After
specifier|public
name|void
name|stopBrokerJustInCase
parameter_list|()
throws|throws
name|Exception
block|{
if|if
condition|(
name|master
operator|!=
literal|null
condition|)
block|{
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
comment|/**      * Deletes the lock file and makes sure that the broken stops.      * @throws Exception      */
annotation|@
name|Test
specifier|public
name|void
name|testLockFileDelete
parameter_list|()
throws|throws
name|Exception
block|{
name|assertTrue
argument_list|(
name|master
operator|.
name|isStarted
argument_list|()
argument_list|)
expr_stmt|;
comment|//Delete the lock file
name|File
name|lockFile
init|=
operator|new
name|File
argument_list|(
name|kahaDataDir
argument_list|,
literal|"lock"
argument_list|)
decl_stmt|;
if|if
condition|(
name|lockFile
operator|.
name|exists
argument_list|()
condition|)
block|{
name|lockFile
operator|.
name|delete
argument_list|()
expr_stmt|;
block|}
name|assertTrue
argument_list|(
literal|"Master stops on lock file delete"
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
block|}
comment|/**      * Modifies the lock file so that the last modified date is not the same when the broker obtained the lock.      * This should force the broker to stop.      * @throws Exception      */
annotation|@
name|Test
specifier|public
name|void
name|testModifyLockFile
parameter_list|()
throws|throws
name|Exception
block|{
name|assertTrue
argument_list|(
name|master
operator|.
name|isStarted
argument_list|()
argument_list|)
expr_stmt|;
specifier|final
name|File
name|lockFile
init|=
operator|new
name|File
argument_list|(
name|kahaDataDir
argument_list|,
literal|"lock"
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
literal|"lock file exists via modification time"
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
name|LOG
operator|.
name|info
argument_list|(
literal|"Lock file "
operator|+
name|lockFile
operator|.
name|getAbsolutePath
argument_list|()
operator|+
literal|", last mod at: "
operator|+
operator|new
name|Date
argument_list|(
name|lockFile
operator|.
name|lastModified
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
return|return
name|lockFile
operator|.
name|lastModified
argument_list|()
operator|>
literal|0
return|;
block|}
block|}
argument_list|)
argument_list|)
expr_stmt|;
comment|// ensure modification will be seen, second granularity on some nix
name|TimeUnit
operator|.
name|SECONDS
operator|.
name|sleep
argument_list|(
literal|2
argument_list|)
expr_stmt|;
name|RandomAccessFile
name|file
init|=
operator|new
name|RandomAccessFile
argument_list|(
name|lockFile
argument_list|,
literal|"rw"
argument_list|)
decl_stmt|;
name|file
operator|.
name|write
argument_list|(
literal|4
argument_list|)
expr_stmt|;
name|file
operator|.
name|getChannel
argument_list|()
operator|.
name|force
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|file
operator|.
name|close
argument_list|()
expr_stmt|;
name|assertTrue
argument_list|(
literal|"Master stops on lock file modification"
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
argument_list|,
literal|10000
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

