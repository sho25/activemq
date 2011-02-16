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
name|io
operator|.
name|RandomAccessFile
import|;
end_import

begin_import
import|import
name|java
operator|.
name|nio
operator|.
name|channels
operator|.
name|FileLock
import|;
end_import

begin_import
import|import
name|java
operator|.
name|nio
operator|.
name|channels
operator|.
name|OverlappingFileLockException
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
name|IOExceptionSupport
import|;
end_import

begin_comment
comment|/**  * Use to reliably store fixed sized state data. It stores the state in record  * that is versioned and repeated twice in the file so that a failure in the  * middle of the write of the first or second record do not not result in an  * unknown state.  *   *   */
end_comment

begin_class
specifier|public
specifier|final
class|class
name|ControlFile
block|{
specifier|private
specifier|static
specifier|final
name|boolean
name|DISABLE_FILE_LOCK
init|=
literal|"true"
operator|.
name|equals
argument_list|(
name|System
operator|.
name|getProperty
argument_list|(
literal|"java.nio.channels.FileLock.broken"
argument_list|,
literal|"false"
argument_list|)
argument_list|)
decl_stmt|;
specifier|private
specifier|final
name|File
name|file
decl_stmt|;
comment|/** The File that holds the control data. */
specifier|private
specifier|final
name|RandomAccessFile
name|randomAccessFile
decl_stmt|;
specifier|private
specifier|final
name|int
name|maxRecordSize
decl_stmt|;
specifier|private
specifier|final
name|int
name|firstRecordStart
decl_stmt|;
specifier|private
specifier|final
name|int
name|secondRecordStart
decl_stmt|;
specifier|private
specifier|final
name|int
name|firstRecordEnd
decl_stmt|;
specifier|private
specifier|final
name|int
name|secondRecordEnd
decl_stmt|;
specifier|private
name|long
name|version
decl_stmt|;
specifier|private
name|FileLock
name|lock
decl_stmt|;
specifier|private
name|boolean
name|disposed
decl_stmt|;
specifier|public
name|ControlFile
parameter_list|(
name|File
name|file
parameter_list|,
name|int
name|recordSize
parameter_list|)
throws|throws
name|IOException
block|{
name|this
operator|.
name|file
operator|=
name|file
expr_stmt|;
name|this
operator|.
name|maxRecordSize
operator|=
name|recordSize
operator|+
literal|4
expr_stmt|;
comment|// Calculate where the records start and end.
name|this
operator|.
name|firstRecordStart
operator|=
literal|8
expr_stmt|;
name|this
operator|.
name|secondRecordStart
operator|=
literal|8
operator|+
name|maxRecordSize
operator|+
literal|8
operator|+
literal|8
expr_stmt|;
name|this
operator|.
name|firstRecordEnd
operator|=
name|firstRecordStart
operator|+
name|maxRecordSize
expr_stmt|;
name|this
operator|.
name|secondRecordEnd
operator|=
name|secondRecordStart
operator|+
name|maxRecordSize
expr_stmt|;
name|randomAccessFile
operator|=
operator|new
name|RandomAccessFile
argument_list|(
name|file
argument_list|,
literal|"rw"
argument_list|)
expr_stmt|;
block|}
comment|/**      * Locks the control file.      *       * @throws IOException      */
specifier|public
name|void
name|lock
parameter_list|()
throws|throws
name|IOException
block|{
if|if
condition|(
name|DISABLE_FILE_LOCK
condition|)
block|{
return|return;
block|}
if|if
condition|(
name|lock
operator|==
literal|null
condition|)
block|{
try|try
block|{
name|lock
operator|=
name|randomAccessFile
operator|.
name|getChannel
argument_list|()
operator|.
name|tryLock
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|OverlappingFileLockException
name|e
parameter_list|)
block|{
throw|throw
name|IOExceptionSupport
operator|.
name|create
argument_list|(
literal|"Control file '"
operator|+
name|file
operator|+
literal|"' could not be locked."
argument_list|,
name|e
argument_list|)
throw|;
block|}
if|if
condition|(
name|lock
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|IOException
argument_list|(
literal|"Control file '"
operator|+
name|file
operator|+
literal|"' could not be locked."
argument_list|)
throw|;
block|}
block|}
block|}
comment|/**      * Un locks the control file.      *       * @throws IOException      */
specifier|public
name|void
name|unlock
parameter_list|()
throws|throws
name|IOException
block|{
if|if
condition|(
name|DISABLE_FILE_LOCK
condition|)
block|{
return|return;
block|}
if|if
condition|(
name|lock
operator|!=
literal|null
condition|)
block|{
name|lock
operator|.
name|release
argument_list|()
expr_stmt|;
name|lock
operator|=
literal|null
expr_stmt|;
block|}
block|}
specifier|public
name|void
name|dispose
parameter_list|()
block|{
if|if
condition|(
name|disposed
condition|)
block|{
return|return;
block|}
name|disposed
operator|=
literal|true
expr_stmt|;
try|try
block|{
name|unlock
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|ignore
parameter_list|)
block|{         }
try|try
block|{
name|randomAccessFile
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|ignore
parameter_list|)
block|{         }
block|}
specifier|public
specifier|synchronized
name|ByteSequence
name|load
parameter_list|()
throws|throws
name|IOException
block|{
name|long
name|l
init|=
name|randomAccessFile
operator|.
name|length
argument_list|()
decl_stmt|;
if|if
condition|(
name|l
operator|<
name|maxRecordSize
condition|)
block|{
return|return
literal|null
return|;
block|}
name|randomAccessFile
operator|.
name|seek
argument_list|(
name|firstRecordStart
operator|-
literal|8
argument_list|)
expr_stmt|;
name|long
name|v1
init|=
name|randomAccessFile
operator|.
name|readLong
argument_list|()
decl_stmt|;
name|randomAccessFile
operator|.
name|seek
argument_list|(
name|firstRecordEnd
argument_list|)
expr_stmt|;
name|long
name|v1check
init|=
name|randomAccessFile
operator|.
name|readLong
argument_list|()
decl_stmt|;
name|randomAccessFile
operator|.
name|seek
argument_list|(
name|secondRecordStart
operator|-
literal|8
argument_list|)
expr_stmt|;
name|long
name|v2
init|=
name|randomAccessFile
operator|.
name|readLong
argument_list|()
decl_stmt|;
name|randomAccessFile
operator|.
name|seek
argument_list|(
name|secondRecordEnd
argument_list|)
expr_stmt|;
name|long
name|v2check
init|=
name|randomAccessFile
operator|.
name|readLong
argument_list|()
decl_stmt|;
name|byte
index|[]
name|data
init|=
literal|null
decl_stmt|;
if|if
condition|(
name|v2
operator|==
name|v2check
condition|)
block|{
name|version
operator|=
name|v2
expr_stmt|;
name|randomAccessFile
operator|.
name|seek
argument_list|(
name|secondRecordStart
argument_list|)
expr_stmt|;
name|int
name|size
init|=
name|randomAccessFile
operator|.
name|readInt
argument_list|()
decl_stmt|;
name|data
operator|=
operator|new
name|byte
index|[
name|size
index|]
expr_stmt|;
name|randomAccessFile
operator|.
name|readFully
argument_list|(
name|data
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|v1
operator|==
name|v1check
condition|)
block|{
name|version
operator|=
name|v1
expr_stmt|;
name|randomAccessFile
operator|.
name|seek
argument_list|(
name|firstRecordStart
argument_list|)
expr_stmt|;
name|int
name|size
init|=
name|randomAccessFile
operator|.
name|readInt
argument_list|()
decl_stmt|;
name|data
operator|=
operator|new
name|byte
index|[
name|size
index|]
expr_stmt|;
name|randomAccessFile
operator|.
name|readFully
argument_list|(
name|data
argument_list|)
expr_stmt|;
block|}
else|else
block|{
comment|// Bummer.. Both checks are screwed. we don't know
comment|// if any of the two buffer are ok. This should
comment|// only happen is data got corrupted.
throw|throw
operator|new
name|IOException
argument_list|(
literal|"Control data corrupted."
argument_list|)
throw|;
block|}
return|return
operator|new
name|ByteSequence
argument_list|(
name|data
argument_list|,
literal|0
argument_list|,
name|data
operator|.
name|length
argument_list|)
return|;
block|}
specifier|public
name|void
name|store
parameter_list|(
name|ByteSequence
name|data
parameter_list|,
name|boolean
name|sync
parameter_list|)
throws|throws
name|IOException
block|{
name|version
operator|++
expr_stmt|;
name|randomAccessFile
operator|.
name|setLength
argument_list|(
operator|(
name|maxRecordSize
operator|*
literal|2
operator|)
operator|+
literal|32
argument_list|)
expr_stmt|;
name|randomAccessFile
operator|.
name|seek
argument_list|(
literal|0
argument_list|)
expr_stmt|;
comment|// Write the first copy of the control data.
name|randomAccessFile
operator|.
name|writeLong
argument_list|(
name|version
argument_list|)
expr_stmt|;
name|randomAccessFile
operator|.
name|writeInt
argument_list|(
name|data
operator|.
name|getLength
argument_list|()
argument_list|)
expr_stmt|;
name|randomAccessFile
operator|.
name|write
argument_list|(
name|data
operator|.
name|getData
argument_list|()
argument_list|)
expr_stmt|;
name|randomAccessFile
operator|.
name|seek
argument_list|(
name|firstRecordEnd
argument_list|)
expr_stmt|;
name|randomAccessFile
operator|.
name|writeLong
argument_list|(
name|version
argument_list|)
expr_stmt|;
comment|// Write the second copy of the control data.
name|randomAccessFile
operator|.
name|writeLong
argument_list|(
name|version
argument_list|)
expr_stmt|;
name|randomAccessFile
operator|.
name|writeInt
argument_list|(
name|data
operator|.
name|getLength
argument_list|()
argument_list|)
expr_stmt|;
name|randomAccessFile
operator|.
name|write
argument_list|(
name|data
operator|.
name|getData
argument_list|()
argument_list|)
expr_stmt|;
name|randomAccessFile
operator|.
name|seek
argument_list|(
name|secondRecordEnd
argument_list|)
expr_stmt|;
name|randomAccessFile
operator|.
name|writeLong
argument_list|(
name|version
argument_list|)
expr_stmt|;
if|if
condition|(
name|sync
condition|)
block|{
name|randomAccessFile
operator|.
name|getFD
argument_list|()
operator|.
name|sync
argument_list|()
expr_stmt|;
block|}
block|}
specifier|public
name|boolean
name|isDisposed
parameter_list|()
block|{
return|return
name|disposed
return|;
block|}
block|}
end_class

end_unit

