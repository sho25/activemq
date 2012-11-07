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
name|kaha
operator|.
name|impl
operator|.
name|async
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
name|activeio
operator|.
name|journal
operator|.
name|InvalidRecordLocationException
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
name|RecordLocation
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
name|packet
operator|.
name|ByteArrayPacket
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
name|packet
operator|.
name|Packet
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
name|kaha
operator|.
name|impl
operator|.
name|async
operator|.
name|JournalFacade
operator|.
name|RecordLocationFacade
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

begin_comment
comment|/**  * Tests the AsyncDataManager based Journal  *   *   */
end_comment

begin_class
specifier|public
class|class
name|JournalImplTest
extends|extends
name|TestCase
block|{
name|Logger
name|log
init|=
name|LoggerFactory
operator|.
name|getLogger
argument_list|(
name|JournalImplTest
operator|.
name|class
argument_list|)
decl_stmt|;
name|int
name|size
init|=
literal|1024
operator|*
literal|10
decl_stmt|;
name|int
name|logFileCount
init|=
literal|2
decl_stmt|;
name|File
name|logDirectory
init|=
operator|new
name|File
argument_list|(
literal|"target/dm-data2"
argument_list|)
decl_stmt|;
specifier|private
name|JournalFacade
name|journal
decl_stmt|;
comment|/**      * @see junit.framework.TestCase#setUp()      */
specifier|protected
name|void
name|setUp
parameter_list|()
throws|throws
name|Exception
block|{
if|if
condition|(
name|logDirectory
operator|.
name|exists
argument_list|()
condition|)
block|{
name|deleteDir
argument_list|(
name|logDirectory
argument_list|)
expr_stmt|;
block|}
name|assertTrue
argument_list|(
literal|"Could not delete directory: "
operator|+
name|logDirectory
operator|.
name|getCanonicalPath
argument_list|()
argument_list|,
operator|!
name|logDirectory
operator|.
name|exists
argument_list|()
argument_list|)
expr_stmt|;
name|AsyncDataManager
name|dm
init|=
operator|new
name|AsyncDataManager
argument_list|()
decl_stmt|;
name|dm
operator|.
name|setDirectory
argument_list|(
name|logDirectory
argument_list|)
expr_stmt|;
name|dm
operator|.
name|setMaxFileLength
argument_list|(
literal|1024
operator|*
literal|64
argument_list|)
expr_stmt|;
name|dm
operator|.
name|start
argument_list|()
expr_stmt|;
name|journal
operator|=
operator|new
name|JournalFacade
argument_list|(
name|dm
argument_list|)
expr_stmt|;
block|}
comment|/**      */
specifier|private
name|void
name|deleteDir
parameter_list|(
name|File
name|f
parameter_list|)
block|{
name|File
index|[]
name|files
init|=
name|f
operator|.
name|listFiles
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
operator|<
name|files
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|File
name|file
init|=
name|files
index|[
name|i
index|]
decl_stmt|;
name|file
operator|.
name|delete
argument_list|()
expr_stmt|;
block|}
name|f
operator|.
name|delete
argument_list|()
expr_stmt|;
block|}
specifier|protected
name|void
name|tearDown
parameter_list|()
throws|throws
name|Exception
block|{
name|journal
operator|.
name|close
argument_list|()
expr_stmt|;
if|if
condition|(
name|logDirectory
operator|.
name|exists
argument_list|()
condition|)
block|{
name|deleteDir
argument_list|(
name|logDirectory
argument_list|)
expr_stmt|;
block|}
comment|// assertTrue( !logDirectory.exists() );
block|}
specifier|public
name|void
name|testLogFileCreation
parameter_list|()
throws|throws
name|IOException
block|{
name|RecordLocation
name|mark
init|=
name|journal
operator|.
name|getMark
argument_list|()
decl_stmt|;
name|assertNull
argument_list|(
name|mark
argument_list|)
expr_stmt|;
block|}
annotation|@
name|SuppressWarnings
argument_list|(
literal|"unchecked"
argument_list|)
specifier|public
name|void
name|testAppendAndRead
parameter_list|()
throws|throws
name|InvalidRecordLocationException
throws|,
name|InterruptedException
throws|,
name|IOException
block|{
name|Packet
name|data1
init|=
name|createPacket
argument_list|(
literal|"Hello World 1"
argument_list|)
decl_stmt|;
name|RecordLocation
name|location1
init|=
name|journal
operator|.
name|write
argument_list|(
name|data1
argument_list|,
literal|false
argument_list|)
decl_stmt|;
name|Packet
name|data2
init|=
name|createPacket
argument_list|(
literal|"Hello World 2"
argument_list|)
decl_stmt|;
name|RecordLocation
name|location2
init|=
name|journal
operator|.
name|write
argument_list|(
name|data2
argument_list|,
literal|false
argument_list|)
decl_stmt|;
name|Packet
name|data3
init|=
name|createPacket
argument_list|(
literal|"Hello World 3"
argument_list|)
decl_stmt|;
name|RecordLocation
name|location3
init|=
name|journal
operator|.
name|write
argument_list|(
name|data3
argument_list|,
literal|false
argument_list|)
decl_stmt|;
comment|// Thread.sleep(1000);
comment|// Now see if we can read that data.
name|Packet
name|data
decl_stmt|;
name|data
operator|=
name|journal
operator|.
name|read
argument_list|(
name|location2
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|data2
argument_list|,
name|data
argument_list|)
expr_stmt|;
name|data
operator|=
name|journal
operator|.
name|read
argument_list|(
name|location1
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|data1
argument_list|,
name|data
argument_list|)
expr_stmt|;
name|data
operator|=
name|journal
operator|.
name|read
argument_list|(
name|location3
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|data3
argument_list|,
name|data
argument_list|)
expr_stmt|;
comment|// Can we cursor the data?
name|RecordLocation
name|l
init|=
name|journal
operator|.
name|getNextRecordLocation
argument_list|(
literal|null
argument_list|)
decl_stmt|;
name|int
name|t
init|=
name|l
operator|.
name|compareTo
argument_list|(
name|location1
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|t
argument_list|)
expr_stmt|;
name|data
operator|=
name|journal
operator|.
name|read
argument_list|(
name|l
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|data1
argument_list|,
name|data
argument_list|)
expr_stmt|;
name|l
operator|=
name|journal
operator|.
name|getNextRecordLocation
argument_list|(
name|l
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|l
operator|.
name|compareTo
argument_list|(
name|location2
argument_list|)
argument_list|)
expr_stmt|;
name|data
operator|=
name|journal
operator|.
name|read
argument_list|(
name|l
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|data2
argument_list|,
name|data
argument_list|)
expr_stmt|;
name|l
operator|=
name|journal
operator|.
name|getNextRecordLocation
argument_list|(
name|l
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|l
operator|.
name|compareTo
argument_list|(
name|location3
argument_list|)
argument_list|)
expr_stmt|;
name|data
operator|=
name|journal
operator|.
name|read
argument_list|(
name|l
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|data3
argument_list|,
name|data
argument_list|)
expr_stmt|;
name|l
operator|=
name|journal
operator|.
name|getNextRecordLocation
argument_list|(
name|l
argument_list|)
expr_stmt|;
name|assertNull
argument_list|(
name|l
argument_list|)
expr_stmt|;
name|log
operator|.
name|info
argument_list|(
name|journal
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|testReadOnlyRead
parameter_list|()
throws|throws
name|InvalidRecordLocationException
throws|,
name|InterruptedException
throws|,
name|IOException
block|{
name|Packet
name|data1
init|=
name|createPacket
argument_list|(
literal|"Hello World 1"
argument_list|)
decl_stmt|;
name|RecordLocation
name|location1
init|=
name|journal
operator|.
name|write
argument_list|(
name|data1
argument_list|,
literal|false
argument_list|)
decl_stmt|;
name|Packet
name|data2
init|=
name|createPacket
argument_list|(
literal|"Hello World 2"
argument_list|)
decl_stmt|;
name|RecordLocation
name|location2
init|=
name|journal
operator|.
name|write
argument_list|(
name|data2
argument_list|,
literal|false
argument_list|)
decl_stmt|;
name|Packet
name|data3
init|=
name|createPacket
argument_list|(
literal|"Hello World 3"
argument_list|)
decl_stmt|;
name|RecordLocation
name|location3
init|=
name|journal
operator|.
name|write
argument_list|(
name|data3
argument_list|,
literal|false
argument_list|)
decl_stmt|;
name|Packet
name|packet
decl_stmt|;
name|packet
operator|=
name|journal
operator|.
name|read
argument_list|(
name|location2
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|data2
argument_list|,
name|packet
argument_list|)
expr_stmt|;
name|packet
operator|=
name|journal
operator|.
name|read
argument_list|(
name|location1
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|data1
argument_list|,
name|packet
argument_list|)
expr_stmt|;
name|packet
operator|=
name|journal
operator|.
name|read
argument_list|(
name|location3
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|data3
argument_list|,
name|packet
argument_list|)
expr_stmt|;
name|ArrayList
argument_list|<
name|File
argument_list|>
name|data
init|=
operator|new
name|ArrayList
argument_list|<
name|File
argument_list|>
argument_list|()
decl_stmt|;
name|data
operator|.
name|add
argument_list|(
name|logDirectory
argument_list|)
expr_stmt|;
name|ReadOnlyAsyncDataManager
name|rodm
init|=
operator|new
name|ReadOnlyAsyncDataManager
argument_list|(
name|data
argument_list|)
decl_stmt|;
name|rodm
operator|.
name|start
argument_list|()
expr_stmt|;
try|try
block|{
for|for
control|(
name|Location
name|curr
init|=
name|rodm
operator|.
name|getFirstLocation
argument_list|()
init|;
name|curr
operator|!=
literal|null
condition|;
name|curr
operator|=
name|rodm
operator|.
name|getNextLocation
argument_list|(
name|curr
argument_list|)
control|)
block|{
name|ByteSequence
name|bs
init|=
name|rodm
operator|.
name|read
argument_list|(
name|curr
argument_list|)
decl_stmt|;
name|assertNotNull
argument_list|(
name|bs
argument_list|)
expr_stmt|;
block|}
block|}
finally|finally
block|{
name|rodm
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
specifier|public
name|void
name|testCanReadFromArchivedLogFile
parameter_list|()
throws|throws
name|InvalidRecordLocationException
throws|,
name|InterruptedException
throws|,
name|IOException
block|{
name|Packet
name|data1
init|=
name|createPacket
argument_list|(
literal|"Hello World 1"
argument_list|)
decl_stmt|;
name|RecordLocationFacade
name|location1
init|=
operator|(
name|RecordLocationFacade
operator|)
name|journal
operator|.
name|write
argument_list|(
name|data1
argument_list|,
literal|false
argument_list|)
decl_stmt|;
name|RecordLocationFacade
name|pos
decl_stmt|;
name|int
name|counter
init|=
literal|0
decl_stmt|;
do|do
block|{
name|Packet
name|p
init|=
name|createPacket
argument_list|(
literal|"<<<data>>>"
argument_list|)
decl_stmt|;
name|pos
operator|=
operator|(
name|RecordLocationFacade
operator|)
name|journal
operator|.
name|write
argument_list|(
name|p
argument_list|,
literal|false
argument_list|)
expr_stmt|;
if|if
condition|(
name|counter
operator|++
operator|%
literal|1000
operator|==
literal|0
condition|)
block|{
name|journal
operator|.
name|setMark
argument_list|(
name|pos
argument_list|,
literal|false
argument_list|)
expr_stmt|;
block|}
block|}
do|while
condition|(
name|pos
operator|.
name|getLocation
argument_list|()
operator|.
name|getDataFileId
argument_list|()
operator|<
literal|5
condition|)
do|;
comment|// Now see if we can read that first packet.
name|Packet
name|data
decl_stmt|;
name|data
operator|=
name|journal
operator|.
name|read
argument_list|(
name|location1
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|data1
argument_list|,
name|data
argument_list|)
expr_stmt|;
block|}
comment|/**      * @param string      * @return      */
specifier|private
name|Packet
name|createPacket
parameter_list|(
name|String
name|string
parameter_list|)
block|{
return|return
operator|new
name|ByteArrayPacket
argument_list|(
name|string
operator|.
name|getBytes
argument_list|()
argument_list|)
return|;
block|}
specifier|public
specifier|static
name|void
name|assertEquals
parameter_list|(
name|Packet
name|arg0
parameter_list|,
name|Packet
name|arg1
parameter_list|)
block|{
name|assertEquals
argument_list|(
name|arg0
operator|.
name|sliceAsBytes
argument_list|()
argument_list|,
name|arg1
operator|.
name|sliceAsBytes
argument_list|()
argument_list|)
expr_stmt|;
block|}
specifier|public
specifier|static
name|void
name|assertEquals
parameter_list|(
name|byte
index|[]
name|arg0
parameter_list|,
name|byte
index|[]
name|arg1
parameter_list|)
block|{
comment|// System.out.println("Comparing: "+new String(arg0)+" and "+new
comment|// String(arg1));
if|if
condition|(
name|arg0
operator|==
literal|null
operator|^
name|arg1
operator|==
literal|null
condition|)
block|{
name|fail
argument_list|(
literal|"Not equal: "
operator|+
name|arg0
operator|+
literal|" != "
operator|+
name|arg1
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|arg0
operator|==
literal|null
condition|)
block|{
return|return;
block|}
if|if
condition|(
name|arg0
operator|.
name|length
operator|!=
name|arg1
operator|.
name|length
condition|)
block|{
name|fail
argument_list|(
literal|"Array lenght not equal: "
operator|+
name|arg0
operator|.
name|length
operator|+
literal|" != "
operator|+
name|arg1
operator|.
name|length
argument_list|)
expr_stmt|;
block|}
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|arg0
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
if|if
condition|(
name|arg0
index|[
name|i
index|]
operator|!=
name|arg1
index|[
name|i
index|]
condition|)
block|{
name|fail
argument_list|(
literal|"Array item not equal at index "
operator|+
name|i
operator|+
literal|": "
operator|+
name|arg0
index|[
name|i
index|]
operator|+
literal|" != "
operator|+
name|arg1
index|[
name|i
index|]
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|}
end_class

end_unit

