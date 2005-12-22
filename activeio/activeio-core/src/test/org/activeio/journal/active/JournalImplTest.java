begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  *  * Copyright 2004 The Apache Software Foundation  *  * Licensed under the Apache License, Version 2.0 (the "License");  * you may not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  * http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
package|package
name|org
operator|.
name|activeio
operator|.
name|journal
operator|.
name|active
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
name|activeio
operator|.
name|journal
operator|.
name|Journal
import|;
end_import

begin_import
import|import
name|org
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
name|commons
operator|.
name|logging
operator|.
name|Log
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
name|logging
operator|.
name|LogFactory
import|;
end_import

begin_comment
comment|/**  * Tests the JournalImpl  *   * @version $Revision: 1.1 $  */
end_comment

begin_class
specifier|public
class|class
name|JournalImplTest
extends|extends
name|TestCase
block|{
name|Log
name|log
init|=
name|LogFactory
operator|.
name|getLog
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
literal|"test-logfile"
argument_list|)
decl_stmt|;
specifier|private
name|Journal
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
name|journal
operator|=
operator|new
name|JournalImpl
argument_list|(
name|logDirectory
argument_list|,
name|logFileCount
argument_list|,
name|size
argument_list|,
name|logDirectory
argument_list|)
expr_stmt|;
block|}
comment|/** 	 */
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
name|deleteDir
argument_list|(
name|logDirectory
argument_list|)
expr_stmt|;
comment|//assertTrue( !logDirectory.exists() );
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
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|l
operator|.
name|compareTo
argument_list|(
name|location1
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
argument_list|)
expr_stmt|;
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
name|Location
name|pos
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
name|Location
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
do|while
condition|(
name|pos
operator|.
name|getLogFileId
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
if|if
condition|(
name|arg0
operator|==
literal|null
condition|)
return|return;
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

