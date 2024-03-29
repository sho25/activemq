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
name|RecoverableRandomAccessFile
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
name|IOException
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|ArrayList
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Collections
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|HashSet
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
name|java
operator|.
name|util
operator|.
name|concurrent
operator|.
name|ConcurrentLinkedQueue
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
name|assertEquals
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
import|import static
name|org
operator|.
name|junit
operator|.
name|Assert
operator|.
name|fail
import|;
end_import

begin_class
specifier|public
class|class
name|DataFileAppenderNoSpaceNoBatchTest
block|{
specifier|private
specifier|static
specifier|final
name|Logger
name|LOG
init|=
name|LoggerFactory
operator|.
name|getLogger
argument_list|(
name|DataFileAppenderNoSpaceNoBatchTest
operator|.
name|class
argument_list|)
decl_stmt|;
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
specifier|private
name|DataFileAppender
name|underTest
decl_stmt|;
annotation|@
name|Test
specifier|public
name|void
name|testNoSpaceNextWriteSameBatch
parameter_list|()
throws|throws
name|Exception
block|{
specifier|final
name|List
argument_list|<
name|Long
argument_list|>
name|seekPositions
init|=
name|Collections
operator|.
name|synchronizedList
argument_list|(
operator|new
name|ArrayList
argument_list|<
name|Long
argument_list|>
argument_list|()
argument_list|)
decl_stmt|;
specifier|final
name|DataFile
name|currentDataFile
init|=
operator|new
name|DataFile
argument_list|(
name|dataFileDir
operator|.
name|newFile
argument_list|()
argument_list|,
literal|0
argument_list|)
block|{
specifier|public
name|RecoverableRandomAccessFile
name|appendRandomAccessFile
parameter_list|()
throws|throws
name|IOException
block|{
return|return
operator|new
name|RecoverableRandomAccessFile
argument_list|(
name|dataFileDir
operator|.
name|newFile
argument_list|()
argument_list|,
literal|"rw"
argument_list|)
block|{
specifier|public
name|void
name|seek
parameter_list|(
name|long
name|pos
parameter_list|)
throws|throws
name|IOException
block|{
name|seekPositions
operator|.
name|add
argument_list|(
name|pos
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|write
parameter_list|(
name|byte
index|[]
name|bytes
parameter_list|,
name|int
name|offset
parameter_list|,
name|int
name|len
parameter_list|)
throws|throws
name|IOException
block|{
throw|throw
operator|new
name|IOException
argument_list|(
literal|"No space on device"
argument_list|)
throw|;
block|}
block|}
return|;
block|}
empty_stmt|;
block|}
decl_stmt|;
name|underTest
operator|=
operator|new
name|DataFileAppender
argument_list|(
operator|new
name|Journal
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|DataFile
name|getCurrentDataFile
parameter_list|(
name|int
name|capacity
parameter_list|)
throws|throws
name|IOException
block|{
return|return
name|currentDataFile
return|;
block|}
empty_stmt|;
block|}
argument_list|)
expr_stmt|;
specifier|final
name|ByteSequence
name|byteSequence
init|=
operator|new
name|ByteSequence
argument_list|(
operator|new
name|byte
index|[
literal|4
operator|*
literal|1024
index|]
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
literal|2
condition|;
name|i
operator|++
control|)
block|{
try|try
block|{
name|underTest
operator|.
name|storeItem
argument_list|(
name|byteSequence
argument_list|,
operator|(
name|byte
operator|)
literal|1
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|fail
argument_list|(
literal|"expect no space"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|expected
parameter_list|)
block|{
name|underTest
operator|.
name|shutdown
operator|=
literal|false
expr_stmt|;
block|}
block|}
name|assertEquals
argument_list|(
literal|"got 2 seeks: "
operator|+
name|seekPositions
argument_list|,
literal|2
argument_list|,
name|seekPositions
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"offset is reused"
argument_list|,
name|seekPositions
operator|.
name|get
argument_list|(
literal|0
argument_list|)
argument_list|,
name|seekPositions
operator|.
name|get
argument_list|(
literal|1
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testSingleNoSpaceNextWriteSameBatch
parameter_list|()
throws|throws
name|Exception
block|{
specifier|final
name|List
argument_list|<
name|Long
argument_list|>
name|seekPositions
init|=
name|Collections
operator|.
name|synchronizedList
argument_list|(
operator|new
name|ArrayList
argument_list|<
name|Long
argument_list|>
argument_list|()
argument_list|)
decl_stmt|;
specifier|final
name|DataFile
name|currentDataFile
init|=
operator|new
name|DataFile
argument_list|(
name|dataFileDir
operator|.
name|newFile
argument_list|()
argument_list|,
literal|0
argument_list|)
block|{
specifier|public
name|RecoverableRandomAccessFile
name|appendRandomAccessFile
parameter_list|()
throws|throws
name|IOException
block|{
return|return
operator|new
name|RecoverableRandomAccessFile
argument_list|(
name|dataFileDir
operator|.
name|newFile
argument_list|()
argument_list|,
literal|"rw"
argument_list|)
block|{
specifier|public
name|void
name|seek
parameter_list|(
name|long
name|pos
parameter_list|)
throws|throws
name|IOException
block|{
name|seekPositions
operator|.
name|add
argument_list|(
name|pos
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|write
parameter_list|(
name|byte
index|[]
name|bytes
parameter_list|,
name|int
name|offset
parameter_list|,
name|int
name|len
parameter_list|)
throws|throws
name|IOException
block|{
throw|throw
operator|new
name|IOException
argument_list|(
literal|"No space on device"
argument_list|)
throw|;
block|}
block|}
return|;
block|}
empty_stmt|;
block|}
decl_stmt|;
name|underTest
operator|=
operator|new
name|DataFileAppender
argument_list|(
operator|new
name|Journal
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|DataFile
name|getCurrentDataFile
parameter_list|(
name|int
name|capacity
parameter_list|)
throws|throws
name|IOException
block|{
return|return
name|currentDataFile
return|;
block|}
empty_stmt|;
block|}
argument_list|)
expr_stmt|;
specifier|final
name|ByteSequence
name|byteSequence
init|=
operator|new
name|ByteSequence
argument_list|(
operator|new
name|byte
index|[
literal|4
operator|*
literal|1024
index|]
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
literal|2
condition|;
name|i
operator|++
control|)
block|{
try|try
block|{
name|underTest
operator|.
name|storeItem
argument_list|(
name|byteSequence
argument_list|,
operator|(
name|byte
operator|)
literal|1
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|fail
argument_list|(
literal|"expect no space"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|expected
parameter_list|)
block|{             }
block|}
name|assertEquals
argument_list|(
literal|"got 1 seeks: "
operator|+
name|seekPositions
argument_list|,
literal|1
argument_list|,
name|seekPositions
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
argument_list|(
name|timeout
operator|=
literal|10000
argument_list|)
specifier|public
name|void
name|testNoSpaceNextWriteSameBatchAsync
parameter_list|()
throws|throws
name|Exception
block|{
specifier|final
name|List
argument_list|<
name|Long
argument_list|>
name|seekPositions
init|=
name|Collections
operator|.
name|synchronizedList
argument_list|(
operator|new
name|ArrayList
argument_list|<
name|Long
argument_list|>
argument_list|()
argument_list|)
decl_stmt|;
specifier|final
name|DataFile
name|currentDataFile
init|=
operator|new
name|DataFile
argument_list|(
name|dataFileDir
operator|.
name|newFile
argument_list|()
argument_list|,
literal|0
argument_list|)
block|{
specifier|public
name|RecoverableRandomAccessFile
name|appendRandomAccessFile
parameter_list|()
throws|throws
name|IOException
block|{
return|return
operator|new
name|RecoverableRandomAccessFile
argument_list|(
name|dataFileDir
operator|.
name|newFile
argument_list|()
argument_list|,
literal|"rw"
argument_list|)
block|{
specifier|public
name|void
name|seek
parameter_list|(
name|long
name|pos
parameter_list|)
throws|throws
name|IOException
block|{
name|seekPositions
operator|.
name|add
argument_list|(
name|pos
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|write
parameter_list|(
name|byte
index|[]
name|bytes
parameter_list|,
name|int
name|offset
parameter_list|,
name|int
name|len
parameter_list|)
throws|throws
name|IOException
block|{
if|if
condition|(
name|seekPositions
operator|.
name|size
argument_list|()
operator|==
literal|2
condition|)
block|{
throw|throw
operator|new
name|IOException
argument_list|(
literal|"No space on device: "
operator|+
name|seekPositions
operator|.
name|size
argument_list|()
argument_list|)
throw|;
block|}
block|}
block|}
return|;
block|}
empty_stmt|;
block|}
decl_stmt|;
name|underTest
operator|=
operator|new
name|DataFileAppender
argument_list|(
operator|new
name|Journal
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|DataFile
name|getCurrentDataFile
parameter_list|(
name|int
name|capacity
parameter_list|)
throws|throws
name|IOException
block|{
return|return
name|currentDataFile
return|;
block|}
empty_stmt|;
annotation|@
name|Override
specifier|public
name|int
name|getWriteBatchSize
parameter_list|()
block|{
comment|// force multiple async batches
return|return
literal|4
operator|*
literal|1024
return|;
block|}
block|}
argument_list|)
expr_stmt|;
specifier|final
name|ByteSequence
name|byteSequence
init|=
operator|new
name|ByteSequence
argument_list|(
operator|new
name|byte
index|[
literal|1024
index|]
argument_list|)
decl_stmt|;
name|ConcurrentLinkedQueue
argument_list|<
name|Location
argument_list|>
name|locations
init|=
operator|new
name|ConcurrentLinkedQueue
argument_list|<
name|Location
argument_list|>
argument_list|()
decl_stmt|;
name|HashSet
argument_list|<
name|CountDownLatch
argument_list|>
name|latches
init|=
operator|new
name|HashSet
argument_list|<
name|CountDownLatch
argument_list|>
argument_list|()
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<=
literal|20
condition|;
name|i
operator|++
control|)
block|{
try|try
block|{
name|Location
name|location
init|=
name|underTest
operator|.
name|storeItem
argument_list|(
name|byteSequence
argument_list|,
operator|(
name|byte
operator|)
literal|1
argument_list|,
literal|false
argument_list|)
decl_stmt|;
name|locations
operator|.
name|add
argument_list|(
name|location
argument_list|)
expr_stmt|;
name|latches
operator|.
name|add
argument_list|(
name|location
operator|.
name|getLatch
argument_list|()
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|expected
parameter_list|)
block|{
name|underTest
operator|.
name|shutdown
operator|=
literal|false
expr_stmt|;
block|}
block|}
for|for
control|(
name|CountDownLatch
name|latch
range|:
name|latches
control|)
block|{
name|assertTrue
argument_list|(
literal|"write complete"
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
name|boolean
name|someExceptions
init|=
literal|false
decl_stmt|;
for|for
control|(
name|Location
name|location
range|:
name|locations
control|)
block|{
name|someExceptions
operator||=
operator|(
name|location
operator|.
name|getException
argument_list|()
operator|.
name|get
argument_list|()
operator|!=
literal|null
operator|)
expr_stmt|;
block|}
name|assertTrue
argument_list|(
name|someExceptions
argument_list|)
expr_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"Latches count: "
operator|+
name|latches
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"Seeks: "
operator|+
name|seekPositions
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
literal|"got more than on latch: "
operator|+
name|latches
operator|.
name|size
argument_list|()
argument_list|,
name|latches
operator|.
name|size
argument_list|()
operator|>
literal|1
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
literal|"got seeks: "
operator|+
name|seekPositions
argument_list|,
name|seekPositions
operator|.
name|size
argument_list|()
operator|>
literal|2
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"no duplicates: "
operator|+
name|seekPositions
argument_list|,
name|seekPositions
operator|.
name|size
argument_list|()
argument_list|,
operator|new
name|HashSet
argument_list|<
name|Long
argument_list|>
argument_list|(
name|seekPositions
argument_list|)
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

