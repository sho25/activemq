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

begin_comment
comment|/**  * An AsyncDataFileAppender that uses NIO ByteBuffers and File chanels to more  * efficently copy data to files.  *   * @version $Revision: 1.1.1.1 $  */
end_comment

begin_class
class|class
name|NIODataFileAppender
extends|extends
name|DataFileAppender
block|{
specifier|public
name|NIODataFileAppender
parameter_list|(
name|AsyncDataManager
name|fileManager
parameter_list|)
block|{
name|super
argument_list|(
name|fileManager
argument_list|)
expr_stmt|;
block|}
comment|/**      * The async processing loop that writes to the data files and does the      * force calls.      *       * Since the file sync() call is the slowest of all the operations, this      * algorithm tries to 'batch' or group together several file sync() requests      * into a single file sync() call. The batching is accomplished attaching      * the same CountDownLatch instance to every force request in a group.      *       */
specifier|protected
name|void
name|processQueue
parameter_list|()
block|{
name|DataFile
name|dataFile
init|=
literal|null
decl_stmt|;
name|RandomAccessFile
name|file
init|=
literal|null
decl_stmt|;
name|FileChannel
name|channel
init|=
literal|null
decl_stmt|;
try|try
block|{
name|ByteBuffer
name|header
init|=
name|ByteBuffer
operator|.
name|allocateDirect
argument_list|(
name|AsyncDataManager
operator|.
name|ITEM_HEAD_SPACE
argument_list|)
decl_stmt|;
name|ByteBuffer
name|footer
init|=
name|ByteBuffer
operator|.
name|allocateDirect
argument_list|(
name|AsyncDataManager
operator|.
name|ITEM_FOOT_SPACE
argument_list|)
decl_stmt|;
name|ByteBuffer
name|buffer
init|=
name|ByteBuffer
operator|.
name|allocateDirect
argument_list|(
name|MAX_WRITE_BATCH_SIZE
argument_list|)
decl_stmt|;
comment|// Populate the static parts of the headers and footers..
name|header
operator|.
name|putInt
argument_list|(
literal|0
argument_list|)
expr_stmt|;
comment|// size
name|header
operator|.
name|put
argument_list|(
operator|(
name|byte
operator|)
literal|0
argument_list|)
expr_stmt|;
comment|// type
name|header
operator|.
name|put
argument_list|(
name|RESERVED_SPACE
argument_list|)
expr_stmt|;
comment|// reserved
name|header
operator|.
name|put
argument_list|(
name|AsyncDataManager
operator|.
name|ITEM_HEAD_SOR
argument_list|)
expr_stmt|;
name|footer
operator|.
name|put
argument_list|(
name|AsyncDataManager
operator|.
name|ITEM_HEAD_EOR
argument_list|)
expr_stmt|;
while|while
condition|(
literal|true
condition|)
block|{
name|Object
name|o
init|=
literal|null
decl_stmt|;
comment|// Block till we get a command.
synchronized|synchronized
init|(
name|enqueueMutex
init|)
block|{
while|while
condition|(
literal|true
condition|)
block|{
if|if
condition|(
name|shutdown
condition|)
block|{
name|o
operator|=
name|SHUTDOWN_COMMAND
expr_stmt|;
break|break;
block|}
if|if
condition|(
name|nextWriteBatch
operator|!=
literal|null
condition|)
block|{
name|o
operator|=
name|nextWriteBatch
expr_stmt|;
name|nextWriteBatch
operator|=
literal|null
expr_stmt|;
break|break;
block|}
name|enqueueMutex
operator|.
name|wait
argument_list|()
expr_stmt|;
block|}
name|enqueueMutex
operator|.
name|notify
argument_list|()
expr_stmt|;
block|}
if|if
condition|(
name|o
operator|==
name|SHUTDOWN_COMMAND
condition|)
block|{
break|break;
block|}
name|WriteBatch
name|wb
init|=
operator|(
name|WriteBatch
operator|)
name|o
decl_stmt|;
if|if
condition|(
name|dataFile
operator|!=
name|wb
operator|.
name|dataFile
condition|)
block|{
if|if
condition|(
name|file
operator|!=
literal|null
condition|)
block|{
name|dataFile
operator|.
name|closeRandomAccessFile
argument_list|(
name|file
argument_list|)
expr_stmt|;
block|}
name|dataFile
operator|=
name|wb
operator|.
name|dataFile
expr_stmt|;
name|file
operator|=
name|dataFile
operator|.
name|openRandomAccessFile
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|channel
operator|=
name|file
operator|.
name|getChannel
argument_list|()
expr_stmt|;
block|}
name|WriteCommand
name|write
init|=
name|wb
operator|.
name|first
decl_stmt|;
comment|// Write all the data.
comment|// Only need to seek to first location.. all others
comment|// are in sequence.
name|file
operator|.
name|seek
argument_list|(
name|write
operator|.
name|location
operator|.
name|getOffset
argument_list|()
argument_list|)
expr_stmt|;
comment|//
comment|// is it just 1 big write?
if|if
condition|(
name|wb
operator|.
name|size
operator|==
name|write
operator|.
name|location
operator|.
name|getSize
argument_list|()
condition|)
block|{
name|header
operator|.
name|clear
argument_list|()
expr_stmt|;
name|header
operator|.
name|putInt
argument_list|(
name|write
operator|.
name|location
operator|.
name|getSize
argument_list|()
argument_list|)
expr_stmt|;
name|header
operator|.
name|put
argument_list|(
name|write
operator|.
name|location
operator|.
name|getType
argument_list|()
argument_list|)
expr_stmt|;
name|header
operator|.
name|clear
argument_list|()
expr_stmt|;
name|transfer
argument_list|(
name|header
argument_list|,
name|channel
argument_list|)
expr_stmt|;
name|ByteBuffer
name|source
init|=
name|ByteBuffer
operator|.
name|wrap
argument_list|(
name|write
operator|.
name|data
operator|.
name|getData
argument_list|()
argument_list|,
name|write
operator|.
name|data
operator|.
name|getOffset
argument_list|()
argument_list|,
name|write
operator|.
name|data
operator|.
name|getLength
argument_list|()
argument_list|)
decl_stmt|;
name|transfer
argument_list|(
name|source
argument_list|,
name|channel
argument_list|)
expr_stmt|;
name|footer
operator|.
name|clear
argument_list|()
expr_stmt|;
name|transfer
argument_list|(
name|footer
argument_list|,
name|channel
argument_list|)
expr_stmt|;
block|}
else|else
block|{
comment|// Combine the smaller writes into 1 big buffer
while|while
condition|(
name|write
operator|!=
literal|null
condition|)
block|{
name|header
operator|.
name|clear
argument_list|()
expr_stmt|;
name|header
operator|.
name|putInt
argument_list|(
name|write
operator|.
name|location
operator|.
name|getSize
argument_list|()
argument_list|)
expr_stmt|;
name|header
operator|.
name|put
argument_list|(
name|write
operator|.
name|location
operator|.
name|getType
argument_list|()
argument_list|)
expr_stmt|;
name|header
operator|.
name|clear
argument_list|()
expr_stmt|;
name|copy
argument_list|(
name|header
argument_list|,
name|buffer
argument_list|)
expr_stmt|;
assert|assert
operator|!
name|header
operator|.
name|hasRemaining
argument_list|()
assert|;
name|ByteBuffer
name|source
init|=
name|ByteBuffer
operator|.
name|wrap
argument_list|(
name|write
operator|.
name|data
operator|.
name|getData
argument_list|()
argument_list|,
name|write
operator|.
name|data
operator|.
name|getOffset
argument_list|()
argument_list|,
name|write
operator|.
name|data
operator|.
name|getLength
argument_list|()
argument_list|)
decl_stmt|;
name|copy
argument_list|(
name|source
argument_list|,
name|buffer
argument_list|)
expr_stmt|;
assert|assert
operator|!
name|source
operator|.
name|hasRemaining
argument_list|()
assert|;
name|footer
operator|.
name|clear
argument_list|()
expr_stmt|;
name|copy
argument_list|(
name|footer
argument_list|,
name|buffer
argument_list|)
expr_stmt|;
assert|assert
operator|!
name|footer
operator|.
name|hasRemaining
argument_list|()
assert|;
name|write
operator|=
operator|(
name|WriteCommand
operator|)
name|write
operator|.
name|getNext
argument_list|()
expr_stmt|;
block|}
comment|// Fully write out the buffer..
name|buffer
operator|.
name|flip
argument_list|()
expr_stmt|;
name|transfer
argument_list|(
name|buffer
argument_list|,
name|channel
argument_list|)
expr_stmt|;
name|buffer
operator|.
name|clear
argument_list|()
expr_stmt|;
block|}
name|file
operator|.
name|getChannel
argument_list|()
operator|.
name|force
argument_list|(
literal|false
argument_list|)
expr_stmt|;
name|WriteCommand
name|lastWrite
init|=
operator|(
name|WriteCommand
operator|)
name|wb
operator|.
name|first
operator|.
name|getTailNode
argument_list|()
decl_stmt|;
name|dataManager
operator|.
name|setLastAppendLocation
argument_list|(
name|lastWrite
operator|.
name|location
argument_list|)
expr_stmt|;
comment|// Signal any waiting threads that the write is on disk.
if|if
condition|(
name|wb
operator|.
name|latch
operator|!=
literal|null
condition|)
block|{
name|wb
operator|.
name|latch
operator|.
name|countDown
argument_list|()
expr_stmt|;
block|}
comment|// Now that the data is on disk, remove the writes from the in
comment|// flight
comment|// cache.
name|write
operator|=
name|wb
operator|.
name|first
expr_stmt|;
while|while
condition|(
name|write
operator|!=
literal|null
condition|)
block|{
if|if
condition|(
operator|!
name|write
operator|.
name|sync
condition|)
block|{
name|inflightWrites
operator|.
name|remove
argument_list|(
operator|new
name|WriteKey
argument_list|(
name|write
operator|.
name|location
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|write
operator|=
operator|(
name|WriteCommand
operator|)
name|write
operator|.
name|getNext
argument_list|()
expr_stmt|;
block|}
block|}
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
synchronized|synchronized
init|(
name|enqueueMutex
init|)
block|{
name|firstAsyncException
operator|=
name|e
expr_stmt|;
block|}
block|}
catch|catch
parameter_list|(
name|InterruptedException
name|e
parameter_list|)
block|{         }
finally|finally
block|{
try|try
block|{
if|if
condition|(
name|file
operator|!=
literal|null
condition|)
block|{
name|dataFile
operator|.
name|closeRandomAccessFile
argument_list|(
name|file
argument_list|)
expr_stmt|;
block|}
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{             }
name|shutdownDone
operator|.
name|countDown
argument_list|()
expr_stmt|;
block|}
block|}
comment|/**      * Copy the bytes in header to the channel.      *       * @param header - source of data      * @param channel - destination where the data will be written.      * @throws IOException      */
specifier|private
name|void
name|transfer
parameter_list|(
name|ByteBuffer
name|header
parameter_list|,
name|FileChannel
name|channel
parameter_list|)
throws|throws
name|IOException
block|{
while|while
condition|(
name|header
operator|.
name|hasRemaining
argument_list|()
condition|)
block|{
name|channel
operator|.
name|write
argument_list|(
name|header
argument_list|)
expr_stmt|;
block|}
block|}
specifier|private
name|int
name|copy
parameter_list|(
name|ByteBuffer
name|src
parameter_list|,
name|ByteBuffer
name|dest
parameter_list|)
block|{
name|int
name|rc
init|=
name|Math
operator|.
name|min
argument_list|(
name|dest
operator|.
name|remaining
argument_list|()
argument_list|,
name|src
operator|.
name|remaining
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
name|rc
operator|>
literal|0
condition|)
block|{
comment|// Adjust our limit so that we don't overflow the dest buffer.
name|int
name|limit
init|=
name|src
operator|.
name|limit
argument_list|()
decl_stmt|;
name|src
operator|.
name|limit
argument_list|(
name|src
operator|.
name|position
argument_list|()
operator|+
name|rc
argument_list|)
expr_stmt|;
name|dest
operator|.
name|put
argument_list|(
name|src
argument_list|)
expr_stmt|;
comment|// restore the limit.
name|src
operator|.
name|limit
argument_list|(
name|limit
argument_list|)
expr_stmt|;
block|}
return|return
name|rc
return|;
block|}
block|}
end_class

end_unit

