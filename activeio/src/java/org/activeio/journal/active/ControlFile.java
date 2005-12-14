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
name|Properties
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Set
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

begin_import
import|import
name|org
operator|.
name|activeio
operator|.
name|Packet
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
name|ByteBufferPacket
import|;
end_import

begin_comment
comment|/**  * Control file holds the last known good state of the journal.  It stores the state in   * record that is versioned and repeated twice in the file so that a failure in the  * middle of the write of the first or second record do not not result in an unknown  * state.   *   * @version $Revision: 1.1 $  */
end_comment

begin_class
specifier|final
specifier|public
class|class
name|ControlFile
implements|implements
name|Disposable
block|{
comment|/** The File that holds the control data. */
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
specifier|private
specifier|final
name|ByteBufferPacket
name|controlData
decl_stmt|;
specifier|private
name|long
name|controlDataVersion
init|=
literal|0
decl_stmt|;
specifier|private
name|FileLock
name|lock
decl_stmt|;
specifier|private
name|boolean
name|disposed
decl_stmt|;
specifier|private
specifier|static
name|Set
name|lockSet
decl_stmt|;
specifier|private
name|String
name|canonicalPath
decl_stmt|;
specifier|public
name|ControlFile
parameter_list|(
name|File
name|fileName
parameter_list|,
name|int
name|controlDataSize
parameter_list|)
throws|throws
name|IOException
block|{
name|canonicalPath
operator|=
name|fileName
operator|.
name|getCanonicalPath
argument_list|()
expr_stmt|;
name|boolean
name|existed
init|=
name|fileName
operator|.
name|exists
argument_list|()
decl_stmt|;
name|file
operator|=
operator|new
name|RandomAccessFile
argument_list|(
name|fileName
argument_list|,
literal|"rw"
argument_list|)
expr_stmt|;
name|channel
operator|=
name|file
operator|.
name|getChannel
argument_list|()
expr_stmt|;
name|controlData
operator|=
operator|new
name|ByteBufferPacket
argument_list|(
name|ByteBuffer
operator|.
name|allocateDirect
argument_list|(
name|controlDataSize
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|/**      * Locks the control file.      * @throws IOException       */
specifier|public
name|void
name|lock
parameter_list|()
throws|throws
name|IOException
block|{
name|Set
name|set
init|=
name|getVmLockSet
argument_list|()
decl_stmt|;
synchronized|synchronized
init|(
name|set
init|)
block|{
if|if
condition|(
name|lock
operator|==
literal|null
condition|)
block|{
if|if
condition|(
operator|!
name|set
operator|.
name|add
argument_list|(
name|canonicalPath
argument_list|)
condition|)
block|{
throw|throw
operator|new
name|IOException
argument_list|(
literal|"Journal is already opened by this application."
argument_list|)
throw|;
block|}
name|lock
operator|=
name|channel
operator|.
name|tryLock
argument_list|()
expr_stmt|;
if|if
condition|(
name|lock
operator|==
literal|null
condition|)
block|{
name|set
operator|.
name|remove
argument_list|(
name|canonicalPath
argument_list|)
expr_stmt|;
throw|throw
operator|new
name|IOException
argument_list|(
literal|"Journal is already opened by another application"
argument_list|)
throw|;
block|}
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
name|Set
name|set
init|=
name|getVmLockSet
argument_list|()
decl_stmt|;
synchronized|synchronized
init|(
name|set
init|)
block|{
if|if
condition|(
name|lock
operator|!=
literal|null
condition|)
block|{
name|set
operator|.
name|remove
argument_list|(
name|canonicalPath
argument_list|)
expr_stmt|;
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
block|}
specifier|static
specifier|private
name|Set
name|getVmLockSet
parameter_list|()
block|{
if|if
condition|(
name|lockSet
operator|==
literal|null
condition|)
block|{
name|Properties
name|properties
init|=
name|System
operator|.
name|getProperties
argument_list|()
decl_stmt|;
synchronized|synchronized
init|(
name|properties
init|)
block|{
name|lockSet
operator|=
operator|(
name|Set
operator|)
name|properties
operator|.
name|get
argument_list|(
literal|"org.activeio.journal.active.lockMap"
argument_list|)
expr_stmt|;
if|if
condition|(
name|lockSet
operator|==
literal|null
condition|)
block|{
name|lockSet
operator|=
operator|new
name|HashSet
argument_list|()
expr_stmt|;
block|}
name|properties
operator|.
name|put
argument_list|(
literal|"org.activeio.journal.active.lockMap"
argument_list|,
name|lockSet
argument_list|)
expr_stmt|;
block|}
block|}
return|return
name|lockSet
return|;
block|}
specifier|public
name|boolean
name|load
parameter_list|()
throws|throws
name|IOException
block|{
name|long
name|l
init|=
name|file
operator|.
name|length
argument_list|()
decl_stmt|;
if|if
condition|(
name|l
operator|<
name|controlData
operator|.
name|capacity
argument_list|()
condition|)
block|{
name|controlDataVersion
operator|=
literal|0
expr_stmt|;
name|controlData
operator|.
name|position
argument_list|(
literal|0
argument_list|)
expr_stmt|;
name|controlData
operator|.
name|limit
argument_list|(
literal|0
argument_list|)
expr_stmt|;
return|return
literal|false
return|;
block|}
else|else
block|{
name|file
operator|.
name|seek
argument_list|(
literal|0
argument_list|)
expr_stmt|;
name|long
name|v1
init|=
name|file
operator|.
name|readLong
argument_list|()
decl_stmt|;
name|file
operator|.
name|seek
argument_list|(
name|controlData
operator|.
name|capacity
argument_list|()
operator|+
literal|8
argument_list|)
expr_stmt|;
name|long
name|v1check
init|=
name|file
operator|.
name|readLong
argument_list|()
decl_stmt|;
name|file
operator|.
name|seek
argument_list|(
name|controlData
operator|.
name|capacity
argument_list|()
operator|+
literal|16
argument_list|)
expr_stmt|;
name|long
name|v2
init|=
name|file
operator|.
name|readLong
argument_list|()
decl_stmt|;
name|file
operator|.
name|seek
argument_list|(
operator|(
name|controlData
operator|.
name|capacity
argument_list|()
operator|*
literal|2
operator|)
operator|+
literal|24
argument_list|)
expr_stmt|;
name|long
name|v2check
init|=
name|file
operator|.
name|readLong
argument_list|()
decl_stmt|;
if|if
condition|(
name|v2
operator|==
name|v2check
condition|)
block|{
name|controlDataVersion
operator|=
name|v2
expr_stmt|;
name|file
operator|.
name|seek
argument_list|(
name|controlData
operator|.
name|capacity
argument_list|()
operator|+
literal|24
argument_list|)
expr_stmt|;
name|controlData
operator|.
name|clear
argument_list|()
expr_stmt|;
name|channel
operator|.
name|read
argument_list|(
name|controlData
operator|.
name|getByteBuffer
argument_list|()
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
name|controlDataVersion
operator|=
name|v1
expr_stmt|;
name|file
operator|.
name|seek
argument_list|(
name|controlData
operator|.
name|capacity
argument_list|()
operator|+
literal|8
argument_list|)
expr_stmt|;
name|controlData
operator|.
name|clear
argument_list|()
expr_stmt|;
name|channel
operator|.
name|read
argument_list|(
name|controlData
operator|.
name|getByteBuffer
argument_list|()
argument_list|)
expr_stmt|;
block|}
else|else
block|{
comment|// Bummer.. Both checks are screwed. we don't know
comment|// if any of the two buffer are ok.  This should
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
literal|true
return|;
block|}
block|}
specifier|public
name|void
name|store
parameter_list|()
throws|throws
name|IOException
block|{
name|controlDataVersion
operator|++
expr_stmt|;
name|file
operator|.
name|setLength
argument_list|(
operator|(
name|controlData
operator|.
name|capacity
argument_list|()
operator|*
literal|2
operator|)
operator|+
literal|32
argument_list|)
expr_stmt|;
name|file
operator|.
name|seek
argument_list|(
literal|0
argument_list|)
expr_stmt|;
comment|// Write the first copy of the control data.
name|file
operator|.
name|writeLong
argument_list|(
name|controlDataVersion
argument_list|)
expr_stmt|;
name|controlData
operator|.
name|clear
argument_list|()
expr_stmt|;
name|channel
operator|.
name|write
argument_list|(
name|controlData
operator|.
name|getByteBuffer
argument_list|()
argument_list|)
expr_stmt|;
name|file
operator|.
name|writeLong
argument_list|(
name|controlDataVersion
argument_list|)
expr_stmt|;
comment|// Write the second copy of the control data.
name|file
operator|.
name|writeLong
argument_list|(
name|controlDataVersion
argument_list|)
expr_stmt|;
name|controlData
operator|.
name|clear
argument_list|()
expr_stmt|;
name|channel
operator|.
name|write
argument_list|(
name|controlData
operator|.
name|getByteBuffer
argument_list|()
argument_list|)
expr_stmt|;
name|file
operator|.
name|writeLong
argument_list|(
name|controlDataVersion
argument_list|)
expr_stmt|;
name|channel
operator|.
name|force
argument_list|(
literal|false
argument_list|)
expr_stmt|;
block|}
specifier|public
name|Packet
name|getControlData
parameter_list|()
block|{
name|controlData
operator|.
name|clear
argument_list|()
expr_stmt|;
return|return
name|controlData
return|;
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
name|unlock
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{         }
try|try
block|{
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
block|{         }
block|}
block|}
end_class

end_unit

