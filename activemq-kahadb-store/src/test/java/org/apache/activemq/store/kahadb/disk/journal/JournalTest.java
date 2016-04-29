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
name|CountDownLatch
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
name|Executors
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
name|util
operator|.
name|ByteSequence
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
name|JournalTest
extends|extends
name|TestCase
block|{
specifier|protected
specifier|static
specifier|final
name|int
name|DEFAULT_MAX_BATCH_SIZE
init|=
literal|1024
operator|*
literal|1024
operator|*
literal|4
decl_stmt|;
name|Journal
name|dataManager
decl_stmt|;
name|File
name|dir
decl_stmt|;
annotation|@
name|Override
specifier|public
name|void
name|setUp
parameter_list|()
throws|throws
name|Exception
block|{
name|dir
operator|=
operator|new
name|File
argument_list|(
literal|"target/tests/DataFileAppenderTest"
argument_list|)
expr_stmt|;
name|dir
operator|.
name|mkdirs
argument_list|()
expr_stmt|;
name|dataManager
operator|=
operator|new
name|Journal
argument_list|()
expr_stmt|;
name|dataManager
operator|.
name|setDirectory
argument_list|(
name|dir
argument_list|)
expr_stmt|;
name|configure
argument_list|(
name|dataManager
argument_list|)
expr_stmt|;
name|dataManager
operator|.
name|start
argument_list|()
expr_stmt|;
block|}
specifier|protected
name|void
name|configure
parameter_list|(
name|Journal
name|dataManager
parameter_list|)
block|{     }
annotation|@
name|Override
specifier|public
name|void
name|tearDown
parameter_list|()
throws|throws
name|Exception
block|{
name|dataManager
operator|.
name|close
argument_list|()
expr_stmt|;
name|IOHelper
operator|.
name|delete
argument_list|(
name|dir
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|testBatchWriteCallbackCompleteAfterTimeout
parameter_list|()
throws|throws
name|Exception
block|{
specifier|final
name|int
name|iterations
init|=
literal|10
decl_stmt|;
specifier|final
name|CountDownLatch
name|latch
init|=
operator|new
name|CountDownLatch
argument_list|(
name|iterations
argument_list|)
decl_stmt|;
name|ByteSequence
name|data
init|=
operator|new
name|ByteSequence
argument_list|(
literal|"DATA"
operator|.
name|getBytes
argument_list|()
argument_list|)
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|iterations
condition|;
name|i
operator|++
control|)
block|{
name|dataManager
operator|.
name|write
argument_list|(
name|data
argument_list|,
operator|new
name|Runnable
argument_list|()
block|{
specifier|public
name|void
name|run
parameter_list|()
block|{
name|latch
operator|.
name|countDown
argument_list|()
expr_stmt|;
block|}
block|}
argument_list|)
expr_stmt|;
block|}
comment|// at this point most probably dataManager.getInflightWrites().size()>= 0
comment|// as the Thread created in DataFileAppender.enqueue() may not have caught up.
name|assertTrue
argument_list|(
literal|"queued data is written"
argument_list|,
name|latch
operator|.
name|await
argument_list|(
literal|5
argument_list|,
name|TimeUnit
operator|.
name|SECONDS
argument_list|)
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|testBatchWriteCallbackCompleteAfterClose
parameter_list|()
throws|throws
name|Exception
block|{
specifier|final
name|int
name|iterations
init|=
literal|10
decl_stmt|;
specifier|final
name|CountDownLatch
name|latch
init|=
operator|new
name|CountDownLatch
argument_list|(
name|iterations
argument_list|)
decl_stmt|;
name|ByteSequence
name|data
init|=
operator|new
name|ByteSequence
argument_list|(
literal|"DATA"
operator|.
name|getBytes
argument_list|()
argument_list|)
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|iterations
condition|;
name|i
operator|++
control|)
block|{
name|dataManager
operator|.
name|write
argument_list|(
name|data
argument_list|,
operator|new
name|Runnable
argument_list|()
block|{
specifier|public
name|void
name|run
parameter_list|()
block|{
name|latch
operator|.
name|countDown
argument_list|()
expr_stmt|;
block|}
block|}
argument_list|)
expr_stmt|;
block|}
name|dataManager
operator|.
name|close
argument_list|()
expr_stmt|;
name|assertTrue
argument_list|(
literal|"queued data is written"
argument_list|,
name|dataManager
operator|.
name|getInflightWrites
argument_list|()
operator|.
name|isEmpty
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"none written"
argument_list|,
literal|0
argument_list|,
name|latch
operator|.
name|getCount
argument_list|()
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|testBatchWriteCompleteAfterClose
parameter_list|()
throws|throws
name|Exception
block|{
name|ByteSequence
name|data
init|=
operator|new
name|ByteSequence
argument_list|(
literal|"DATA"
operator|.
name|getBytes
argument_list|()
argument_list|)
decl_stmt|;
specifier|final
name|int
name|iterations
init|=
literal|10
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|iterations
condition|;
name|i
operator|++
control|)
block|{
name|dataManager
operator|.
name|write
argument_list|(
name|data
argument_list|,
literal|false
argument_list|)
expr_stmt|;
block|}
name|dataManager
operator|.
name|close
argument_list|()
expr_stmt|;
name|assertTrue
argument_list|(
literal|"queued data is written:"
operator|+
name|dataManager
operator|.
name|getInflightWrites
argument_list|()
operator|.
name|size
argument_list|()
argument_list|,
name|dataManager
operator|.
name|getInflightWrites
argument_list|()
operator|.
name|isEmpty
argument_list|()
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|testBatchWriteToMaxMessageSize
parameter_list|()
throws|throws
name|Exception
block|{
specifier|final
name|int
name|iterations
init|=
literal|4
decl_stmt|;
specifier|final
name|CountDownLatch
name|latch
init|=
operator|new
name|CountDownLatch
argument_list|(
name|iterations
argument_list|)
decl_stmt|;
name|Runnable
name|done
init|=
operator|new
name|Runnable
argument_list|()
block|{
specifier|public
name|void
name|run
parameter_list|()
block|{
name|latch
operator|.
name|countDown
argument_list|()
expr_stmt|;
block|}
block|}
decl_stmt|;
name|int
name|messageSize
init|=
name|DEFAULT_MAX_BATCH_SIZE
operator|/
name|iterations
decl_stmt|;
name|byte
index|[]
name|message
init|=
operator|new
name|byte
index|[
name|messageSize
index|]
decl_stmt|;
name|ByteSequence
name|data
init|=
operator|new
name|ByteSequence
argument_list|(
name|message
argument_list|)
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|iterations
condition|;
name|i
operator|++
control|)
block|{
name|dataManager
operator|.
name|write
argument_list|(
name|data
argument_list|,
name|done
argument_list|)
expr_stmt|;
block|}
comment|// write may take some time
name|assertTrue
argument_list|(
literal|"all callbacks complete"
argument_list|,
name|latch
operator|.
name|await
argument_list|(
literal|10
argument_list|,
name|TimeUnit
operator|.
name|SECONDS
argument_list|)
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|testNoBatchWriteWithSync
parameter_list|()
throws|throws
name|Exception
block|{
name|ByteSequence
name|data
init|=
operator|new
name|ByteSequence
argument_list|(
literal|"DATA"
operator|.
name|getBytes
argument_list|()
argument_list|)
decl_stmt|;
specifier|final
name|int
name|iterations
init|=
literal|10
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|iterations
condition|;
name|i
operator|++
control|)
block|{
name|dataManager
operator|.
name|write
argument_list|(
name|data
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
literal|"queued data is written"
argument_list|,
name|dataManager
operator|.
name|getInflightWrites
argument_list|()
operator|.
name|isEmpty
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

