begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  *  * Copyright 2004 Hiram Chirino  *  * Licensed under the Apache License, Version 2.0 (the "License");  * you may not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  * http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  *  **/
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
name|FileOutputStream
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
name|ByteBuffer
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
name|FileChannel
import|;
end_import

begin_import
import|import
name|org
operator|.
name|activeio
operator|.
name|Disposable
import|;
end_import

begin_comment
comment|/**  * Allows read/append access to a LogFile.  *   * @version $Revision: 1.1 $  */
end_comment

begin_class
specifier|final
specifier|public
class|class
name|LogFile
implements|implements
name|Disposable
block|{
specifier|private
specifier|final
name|RandomAccessFile
name|file
decl_stmt|;
specifier|private
specifier|final
name|FileChannel
name|channel
decl_stmt|;
comment|/** Prefered size. The size that the log file is set to when initilaized. */
specifier|private
specifier|final
name|int
name|initialSize
decl_stmt|;
comment|/** Where the we are in the file right now */
specifier|private
name|int
name|currentOffset
decl_stmt|;
specifier|private
name|boolean
name|disposed
decl_stmt|;
specifier|public
name|LogFile
parameter_list|(
name|File
name|file
parameter_list|,
name|int
name|initialSize
parameter_list|)
throws|throws
name|IOException
block|{
name|this
operator|.
name|initialSize
operator|=
name|initialSize
expr_stmt|;
name|boolean
name|initializationNeeeded
init|=
operator|!
name|file
operator|.
name|exists
argument_list|()
decl_stmt|;
name|this
operator|.
name|file
operator|=
operator|new
name|RandomAccessFile
argument_list|(
name|file
argument_list|,
literal|"rw"
argument_list|)
expr_stmt|;
name|channel
operator|=
name|this
operator|.
name|file
operator|.
name|getChannel
argument_list|()
expr_stmt|;
if|if
condition|(
name|initializationNeeeded
condition|)
name|resize
argument_list|()
expr_stmt|;
name|channel
operator|.
name|position
argument_list|(
literal|0
argument_list|)
expr_stmt|;
name|reloadCurrentOffset
argument_list|()
expr_stmt|;
block|}
comment|/**      * To avoid doing un-needed seeks.      */
specifier|private
name|void
name|seek
parameter_list|(
name|int
name|offset
parameter_list|)
throws|throws
name|IOException
block|{
if|if
condition|(
name|offset
operator|==
name|currentOffset
condition|)
block|{
if|if
condition|(
name|currentOffset
operator|!=
name|channel
operator|.
name|position
argument_list|()
condition|)
throw|throw
operator|new
name|RuntimeException
argument_list|(
literal|" "
operator|+
name|currentOffset
operator|+
literal|", "
operator|+
name|channel
operator|.
name|position
argument_list|()
argument_list|)
throw|;
return|return;
block|}
name|channel
operator|.
name|position
argument_list|(
name|offset
argument_list|)
expr_stmt|;
name|currentOffset
operator|=
name|offset
expr_stmt|;
block|}
specifier|private
name|void
name|reloadCurrentOffset
parameter_list|()
throws|throws
name|IOException
block|{
name|currentOffset
operator|=
operator|(
name|int
operator|)
name|channel
operator|.
name|position
argument_list|()
expr_stmt|;
block|}
specifier|private
name|void
name|addToCurrentOffset
parameter_list|(
name|int
name|rc
parameter_list|)
block|{
name|currentOffset
operator|+=
name|rc
expr_stmt|;
block|}
specifier|public
name|boolean
name|loadAndCheckRecord
parameter_list|(
name|int
name|offset
parameter_list|,
name|Record
name|record
parameter_list|)
throws|throws
name|IOException
block|{
try|try
block|{
comment|// Read the next header
name|seek
argument_list|(
name|offset
argument_list|)
expr_stmt|;
name|record
operator|.
name|readHeader
argument_list|(
name|file
argument_list|)
expr_stmt|;
if|if
condition|(
name|Record
operator|.
name|isChecksumingEnabled
argument_list|()
condition|)
block|{
name|record
operator|.
name|checksum
argument_list|(
name|file
argument_list|)
expr_stmt|;
block|}
comment|// Load the footer.
name|seek
argument_list|(
name|offset
operator|+
name|record
operator|.
name|getPayloadLength
argument_list|()
operator|+
name|Record
operator|.
name|RECORD_HEADER_SIZE
argument_list|)
expr_stmt|;
name|record
operator|.
name|readFooter
argument_list|(
name|file
argument_list|)
expr_stmt|;
name|addToCurrentOffset
argument_list|(
name|record
operator|.
name|getRecordLength
argument_list|()
argument_list|)
expr_stmt|;
return|return
literal|true
return|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
name|reloadCurrentOffset
argument_list|()
expr_stmt|;
return|return
literal|false
return|;
block|}
block|}
specifier|public
name|void
name|resize
parameter_list|()
throws|throws
name|IOException
block|{
name|file
operator|.
name|setLength
argument_list|(
name|initialSize
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|force
parameter_list|()
throws|throws
name|IOException
block|{
name|channel
operator|.
name|force
argument_list|(
literal|false
argument_list|)
expr_stmt|;
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
return|return;
name|disposed
operator|=
literal|true
expr_stmt|;
try|try
block|{
name|this
operator|.
name|file
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{ 		}
block|}
specifier|public
name|void
name|write
parameter_list|(
name|int
name|offset
parameter_list|,
name|ByteBuffer
name|buffer
parameter_list|)
throws|throws
name|IOException
block|{
try|try
block|{
name|int
name|size
init|=
name|buffer
operator|.
name|remaining
argument_list|()
decl_stmt|;
name|seek
argument_list|(
name|offset
argument_list|)
expr_stmt|;
while|while
condition|(
name|buffer
operator|.
name|hasRemaining
argument_list|()
condition|)
block|{
name|channel
operator|.
name|write
argument_list|(
name|buffer
argument_list|)
expr_stmt|;
block|}
name|addToCurrentOffset
argument_list|(
name|size
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
name|reloadCurrentOffset
argument_list|()
expr_stmt|;
block|}
block|}
specifier|public
name|void
name|readRecordHeader
parameter_list|(
name|int
name|offset
parameter_list|,
name|Record
name|record
parameter_list|)
throws|throws
name|IOException
block|{
name|seek
argument_list|(
name|offset
argument_list|)
expr_stmt|;
try|try
block|{
name|record
operator|.
name|readHeader
argument_list|(
name|file
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
name|reloadCurrentOffset
argument_list|()
expr_stmt|;
throw|throw
name|e
throw|;
block|}
name|addToCurrentOffset
argument_list|(
name|Record
operator|.
name|RECORD_HEADER_SIZE
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|read
parameter_list|(
name|int
name|offset
parameter_list|,
name|byte
index|[]
name|answer
parameter_list|)
throws|throws
name|IOException
block|{
name|seek
argument_list|(
name|offset
argument_list|)
expr_stmt|;
name|file
operator|.
name|readFully
argument_list|(
name|answer
argument_list|)
expr_stmt|;
name|addToCurrentOffset
argument_list|(
name|answer
operator|.
name|length
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|copyTo
parameter_list|(
name|File
name|location
parameter_list|)
throws|throws
name|IOException
block|{
name|FileOutputStream
name|fos
init|=
operator|new
name|FileOutputStream
argument_list|(
name|location
argument_list|)
decl_stmt|;
name|channel
operator|.
name|transferTo
argument_list|(
literal|0
argument_list|,
name|channel
operator|.
name|size
argument_list|()
argument_list|,
name|fos
operator|.
name|getChannel
argument_list|()
argument_list|)
expr_stmt|;
name|fos
operator|.
name|getChannel
argument_list|()
operator|.
name|force
argument_list|(
literal|false
argument_list|)
expr_stmt|;
name|fos
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
end_class

end_unit

