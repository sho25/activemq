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
name|InterruptedIOException
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
name|Map
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
name|atomic
operator|.
name|AtomicReference
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
name|DataByteArrayOutputStream
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
name|LinkedNode
import|;
end_import

begin_comment
comment|/**  * An optimized writer to do batch appends to a data file. This object is thread  * safe and gains throughput as you increase the number of concurrent writes it  * does.  *   * @version $Revision: 1.1.1.1 $  */
end_comment

begin_class
class|class
name|DataFileAppender
block|{
specifier|protected
specifier|static
specifier|final
name|byte
index|[]
name|RESERVED_SPACE
init|=
operator|new
name|byte
index|[
name|AsyncDataManager
operator|.
name|ITEM_HEAD_RESERVED_SPACE
index|]
decl_stmt|;
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
specifier|protected
specifier|final
name|AsyncDataManager
name|dataManager
decl_stmt|;
specifier|protected
specifier|final
name|Map
argument_list|<
name|WriteKey
argument_list|,
name|WriteCommand
argument_list|>
name|inflightWrites
decl_stmt|;
specifier|protected
specifier|final
name|Object
name|enqueueMutex
init|=
operator|new
name|Object
argument_list|()
block|{}
decl_stmt|;
specifier|protected
name|WriteBatch
name|nextWriteBatch
decl_stmt|;
specifier|protected
name|boolean
name|shutdown
decl_stmt|;
specifier|protected
name|IOException
name|firstAsyncException
decl_stmt|;
specifier|protected
specifier|final
name|CountDownLatch
name|shutdownDone
init|=
operator|new
name|CountDownLatch
argument_list|(
literal|1
argument_list|)
decl_stmt|;
specifier|protected
name|int
name|maxWriteBatchSize
init|=
name|DEFAULT_MAX_BATCH_SIZE
decl_stmt|;
specifier|protected
name|boolean
name|running
decl_stmt|;
specifier|private
name|Thread
name|thread
decl_stmt|;
specifier|public
specifier|static
class|class
name|WriteKey
block|{
specifier|private
specifier|final
name|int
name|file
decl_stmt|;
specifier|private
specifier|final
name|long
name|offset
decl_stmt|;
specifier|private
specifier|final
name|int
name|hash
decl_stmt|;
specifier|public
name|WriteKey
parameter_list|(
name|Location
name|item
parameter_list|)
block|{
name|file
operator|=
name|item
operator|.
name|getDataFileId
argument_list|()
expr_stmt|;
name|offset
operator|=
name|item
operator|.
name|getOffset
argument_list|()
expr_stmt|;
comment|// TODO: see if we can build a better hash
name|hash
operator|=
call|(
name|int
call|)
argument_list|(
name|file
operator|^
name|offset
argument_list|)
expr_stmt|;
block|}
specifier|public
name|int
name|hashCode
parameter_list|()
block|{
return|return
name|hash
return|;
block|}
specifier|public
name|boolean
name|equals
parameter_list|(
name|Object
name|obj
parameter_list|)
block|{
if|if
condition|(
name|obj
operator|instanceof
name|WriteKey
condition|)
block|{
name|WriteKey
name|di
init|=
operator|(
name|WriteKey
operator|)
name|obj
decl_stmt|;
return|return
name|di
operator|.
name|file
operator|==
name|file
operator|&&
name|di
operator|.
name|offset
operator|==
name|offset
return|;
block|}
return|return
literal|false
return|;
block|}
block|}
specifier|public
class|class
name|WriteBatch
block|{
specifier|public
specifier|final
name|DataFile
name|dataFile
decl_stmt|;
specifier|public
specifier|final
name|WriteCommand
name|first
decl_stmt|;
specifier|public
specifier|final
name|CountDownLatch
name|latch
init|=
operator|new
name|CountDownLatch
argument_list|(
literal|1
argument_list|)
decl_stmt|;
specifier|public
name|int
name|size
decl_stmt|;
specifier|public
name|AtomicReference
argument_list|<
name|IOException
argument_list|>
name|exception
init|=
operator|new
name|AtomicReference
argument_list|<
name|IOException
argument_list|>
argument_list|()
decl_stmt|;
specifier|public
name|WriteBatch
parameter_list|(
name|DataFile
name|dataFile
parameter_list|,
name|WriteCommand
name|write
parameter_list|)
throws|throws
name|IOException
block|{
name|this
operator|.
name|dataFile
operator|=
name|dataFile
expr_stmt|;
name|this
operator|.
name|first
operator|=
name|write
expr_stmt|;
name|size
operator|+=
name|write
operator|.
name|location
operator|.
name|getSize
argument_list|()
expr_stmt|;
block|}
specifier|public
name|boolean
name|canAppend
parameter_list|(
name|DataFile
name|dataFile
parameter_list|,
name|WriteCommand
name|write
parameter_list|)
block|{
if|if
condition|(
name|dataFile
operator|!=
name|this
operator|.
name|dataFile
condition|)
block|{
return|return
literal|false
return|;
block|}
if|if
condition|(
name|size
operator|+
name|write
operator|.
name|location
operator|.
name|getSize
argument_list|()
operator|>=
name|maxWriteBatchSize
condition|)
block|{
return|return
literal|false
return|;
block|}
return|return
literal|true
return|;
block|}
specifier|public
name|void
name|append
parameter_list|(
name|WriteCommand
name|write
parameter_list|)
throws|throws
name|IOException
block|{
name|this
operator|.
name|first
operator|.
name|getTailNode
argument_list|()
operator|.
name|linkAfter
argument_list|(
name|write
argument_list|)
expr_stmt|;
name|size
operator|+=
name|write
operator|.
name|location
operator|.
name|getSize
argument_list|()
expr_stmt|;
block|}
block|}
specifier|public
specifier|static
class|class
name|WriteCommand
extends|extends
name|LinkedNode
block|{
specifier|public
specifier|final
name|Location
name|location
decl_stmt|;
specifier|public
specifier|final
name|ByteSequence
name|data
decl_stmt|;
specifier|final
name|boolean
name|sync
decl_stmt|;
specifier|public
specifier|final
name|Runnable
name|onComplete
decl_stmt|;
specifier|public
name|WriteCommand
parameter_list|(
name|Location
name|location
parameter_list|,
name|ByteSequence
name|data
parameter_list|,
name|boolean
name|sync
parameter_list|)
block|{
name|this
operator|.
name|location
operator|=
name|location
expr_stmt|;
name|this
operator|.
name|data
operator|=
name|data
expr_stmt|;
name|this
operator|.
name|sync
operator|=
name|sync
expr_stmt|;
name|this
operator|.
name|onComplete
operator|=
literal|null
expr_stmt|;
block|}
specifier|public
name|WriteCommand
parameter_list|(
name|Location
name|location
parameter_list|,
name|ByteSequence
name|data
parameter_list|,
name|Runnable
name|onComplete
parameter_list|)
block|{
name|this
operator|.
name|location
operator|=
name|location
expr_stmt|;
name|this
operator|.
name|data
operator|=
name|data
expr_stmt|;
name|this
operator|.
name|onComplete
operator|=
name|onComplete
expr_stmt|;
name|this
operator|.
name|sync
operator|=
literal|false
expr_stmt|;
block|}
block|}
comment|/**      * Construct a Store writer      *       * @param fileId      */
specifier|public
name|DataFileAppender
parameter_list|(
name|AsyncDataManager
name|dataManager
parameter_list|)
block|{
name|this
operator|.
name|dataManager
operator|=
name|dataManager
expr_stmt|;
name|this
operator|.
name|inflightWrites
operator|=
name|this
operator|.
name|dataManager
operator|.
name|getInflightWrites
argument_list|()
expr_stmt|;
block|}
comment|/**      * @param type      * @param marshaller      * @param payload      * @param type      * @param sync      * @return      * @throws IOException      * @throws      * @throws      */
specifier|public
name|Location
name|storeItem
parameter_list|(
name|ByteSequence
name|data
parameter_list|,
name|byte
name|type
parameter_list|,
name|boolean
name|sync
parameter_list|)
throws|throws
name|IOException
block|{
comment|// Write the packet our internal buffer.
name|int
name|size
init|=
name|data
operator|.
name|getLength
argument_list|()
operator|+
name|AsyncDataManager
operator|.
name|ITEM_HEAD_FOOT_SPACE
decl_stmt|;
specifier|final
name|Location
name|location
init|=
operator|new
name|Location
argument_list|()
decl_stmt|;
name|location
operator|.
name|setSize
argument_list|(
name|size
argument_list|)
expr_stmt|;
name|location
operator|.
name|setType
argument_list|(
name|type
argument_list|)
expr_stmt|;
name|WriteBatch
name|batch
decl_stmt|;
name|WriteCommand
name|write
init|=
operator|new
name|WriteCommand
argument_list|(
name|location
argument_list|,
name|data
argument_list|,
name|sync
argument_list|)
decl_stmt|;
comment|// Locate datafile and enqueue into the executor in sychronized block so
comment|// that writes get equeued onto the executor in order that they were assigned
comment|// by the data manager (which is basically just appending)
synchronized|synchronized
init|(
name|this
init|)
block|{
comment|// Find the position where this item will land at.
name|DataFile
name|dataFile
init|=
name|dataManager
operator|.
name|allocateLocation
argument_list|(
name|location
argument_list|)
decl_stmt|;
if|if
condition|(
operator|!
name|sync
condition|)
block|{
name|inflightWrites
operator|.
name|put
argument_list|(
operator|new
name|WriteKey
argument_list|(
name|location
argument_list|)
argument_list|,
name|write
argument_list|)
expr_stmt|;
block|}
name|batch
operator|=
name|enqueue
argument_list|(
name|dataFile
argument_list|,
name|write
argument_list|)
expr_stmt|;
block|}
name|location
operator|.
name|setLatch
argument_list|(
name|batch
operator|.
name|latch
argument_list|)
expr_stmt|;
if|if
condition|(
name|sync
condition|)
block|{
try|try
block|{
name|batch
operator|.
name|latch
operator|.
name|await
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|InterruptedException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|InterruptedIOException
argument_list|()
throw|;
block|}
name|IOException
name|exception
init|=
name|batch
operator|.
name|exception
operator|.
name|get
argument_list|()
decl_stmt|;
if|if
condition|(
name|exception
operator|!=
literal|null
condition|)
block|{
throw|throw
name|exception
throw|;
block|}
block|}
return|return
name|location
return|;
block|}
specifier|public
name|Location
name|storeItem
parameter_list|(
name|ByteSequence
name|data
parameter_list|,
name|byte
name|type
parameter_list|,
name|Runnable
name|onComplete
parameter_list|)
throws|throws
name|IOException
block|{
comment|// Write the packet our internal buffer.
name|int
name|size
init|=
name|data
operator|.
name|getLength
argument_list|()
operator|+
name|AsyncDataManager
operator|.
name|ITEM_HEAD_FOOT_SPACE
decl_stmt|;
specifier|final
name|Location
name|location
init|=
operator|new
name|Location
argument_list|()
decl_stmt|;
name|location
operator|.
name|setSize
argument_list|(
name|size
argument_list|)
expr_stmt|;
name|location
operator|.
name|setType
argument_list|(
name|type
argument_list|)
expr_stmt|;
name|WriteBatch
name|batch
decl_stmt|;
name|WriteCommand
name|write
init|=
operator|new
name|WriteCommand
argument_list|(
name|location
argument_list|,
name|data
argument_list|,
name|onComplete
argument_list|)
decl_stmt|;
comment|// Locate datafile and enqueue into the executor in sychronized block so
comment|// that writes get equeued onto the executor in order that they were assigned
comment|// by the data manager (which is basically just appending)
synchronized|synchronized
init|(
name|this
init|)
block|{
comment|// Find the position where this item will land at.
name|DataFile
name|dataFile
init|=
name|dataManager
operator|.
name|allocateLocation
argument_list|(
name|location
argument_list|)
decl_stmt|;
name|inflightWrites
operator|.
name|put
argument_list|(
operator|new
name|WriteKey
argument_list|(
name|location
argument_list|)
argument_list|,
name|write
argument_list|)
expr_stmt|;
name|batch
operator|=
name|enqueue
argument_list|(
name|dataFile
argument_list|,
name|write
argument_list|)
expr_stmt|;
block|}
name|location
operator|.
name|setLatch
argument_list|(
name|batch
operator|.
name|latch
argument_list|)
expr_stmt|;
return|return
name|location
return|;
block|}
specifier|private
name|WriteBatch
name|enqueue
parameter_list|(
name|DataFile
name|dataFile
parameter_list|,
name|WriteCommand
name|write
parameter_list|)
throws|throws
name|IOException
block|{
synchronized|synchronized
init|(
name|enqueueMutex
init|)
block|{
name|WriteBatch
name|rc
init|=
literal|null
decl_stmt|;
if|if
condition|(
name|shutdown
condition|)
block|{
throw|throw
operator|new
name|IOException
argument_list|(
literal|"Async Writter Thread Shutdown"
argument_list|)
throw|;
block|}
if|if
condition|(
operator|!
name|running
condition|)
block|{
name|running
operator|=
literal|true
expr_stmt|;
name|thread
operator|=
operator|new
name|Thread
argument_list|()
block|{
specifier|public
name|void
name|run
parameter_list|()
block|{
name|processQueue
argument_list|()
expr_stmt|;
block|}
block|}
expr_stmt|;
name|thread
operator|.
name|setPriority
argument_list|(
name|Thread
operator|.
name|MAX_PRIORITY
argument_list|)
expr_stmt|;
name|thread
operator|.
name|setDaemon
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|thread
operator|.
name|setName
argument_list|(
literal|"ActiveMQ Data File Writer"
argument_list|)
expr_stmt|;
name|thread
operator|.
name|start
argument_list|()
expr_stmt|;
name|firstAsyncException
operator|=
literal|null
expr_stmt|;
block|}
if|if
condition|(
name|firstAsyncException
operator|!=
literal|null
condition|)
block|{
throw|throw
name|firstAsyncException
throw|;
block|}
if|if
condition|(
name|nextWriteBatch
operator|==
literal|null
condition|)
block|{
name|nextWriteBatch
operator|=
operator|new
name|WriteBatch
argument_list|(
name|dataFile
argument_list|,
name|write
argument_list|)
expr_stmt|;
name|rc
operator|=
name|nextWriteBatch
expr_stmt|;
name|enqueueMutex
operator|.
name|notify
argument_list|()
expr_stmt|;
block|}
else|else
block|{
comment|// Append to current batch if possible..
if|if
condition|(
name|nextWriteBatch
operator|.
name|canAppend
argument_list|(
name|dataFile
argument_list|,
name|write
argument_list|)
condition|)
block|{
name|nextWriteBatch
operator|.
name|append
argument_list|(
name|write
argument_list|)
expr_stmt|;
name|rc
operator|=
name|nextWriteBatch
expr_stmt|;
block|}
else|else
block|{
comment|// Otherwise wait for the queuedCommand to be null
try|try
block|{
while|while
condition|(
name|nextWriteBatch
operator|!=
literal|null
condition|)
block|{
name|enqueueMutex
operator|.
name|wait
argument_list|()
expr_stmt|;
block|}
block|}
catch|catch
parameter_list|(
name|InterruptedException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|InterruptedIOException
argument_list|()
throw|;
block|}
if|if
condition|(
name|shutdown
condition|)
block|{
throw|throw
operator|new
name|IOException
argument_list|(
literal|"Async Writter Thread Shutdown"
argument_list|)
throw|;
block|}
comment|// Start a new batch.
name|nextWriteBatch
operator|=
operator|new
name|WriteBatch
argument_list|(
name|dataFile
argument_list|,
name|write
argument_list|)
expr_stmt|;
name|rc
operator|=
name|nextWriteBatch
expr_stmt|;
name|enqueueMutex
operator|.
name|notify
argument_list|()
expr_stmt|;
block|}
block|}
return|return
name|rc
return|;
block|}
block|}
specifier|public
name|void
name|close
parameter_list|()
throws|throws
name|IOException
block|{
synchronized|synchronized
init|(
name|enqueueMutex
init|)
block|{
if|if
condition|(
operator|!
name|shutdown
condition|)
block|{
name|shutdown
operator|=
literal|true
expr_stmt|;
if|if
condition|(
name|running
condition|)
block|{
name|enqueueMutex
operator|.
name|notifyAll
argument_list|()
expr_stmt|;
block|}
else|else
block|{
name|shutdownDone
operator|.
name|countDown
argument_list|()
expr_stmt|;
block|}
block|}
block|}
try|try
block|{
name|shutdownDone
operator|.
name|await
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|InterruptedException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|InterruptedIOException
argument_list|()
throw|;
block|}
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
name|WriteBatch
name|wb
init|=
literal|null
decl_stmt|;
try|try
block|{
name|DataByteArrayOutputStream
name|buff
init|=
operator|new
name|DataByteArrayOutputStream
argument_list|(
name|maxWriteBatchSize
argument_list|)
decl_stmt|;
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
if|if
condition|(
name|shutdown
condition|)
block|{
return|return;
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
name|wb
operator|=
operator|(
name|WriteBatch
operator|)
name|o
expr_stmt|;
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
name|boolean
name|forceToDisk
init|=
literal|false
decl_stmt|;
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
name|forceToDisk
operator|=
name|write
operator|.
name|sync
operator||
name|write
operator|.
name|onComplete
operator|!=
literal|null
expr_stmt|;
comment|// Just write it directly..
name|file
operator|.
name|writeInt
argument_list|(
name|write
operator|.
name|location
operator|.
name|getSize
argument_list|()
argument_list|)
expr_stmt|;
name|file
operator|.
name|writeByte
argument_list|(
name|write
operator|.
name|location
operator|.
name|getType
argument_list|()
argument_list|)
expr_stmt|;
name|file
operator|.
name|write
argument_list|(
name|RESERVED_SPACE
argument_list|)
expr_stmt|;
name|file
operator|.
name|write
argument_list|(
name|AsyncDataManager
operator|.
name|ITEM_HEAD_SOR
argument_list|)
expr_stmt|;
name|file
operator|.
name|write
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
expr_stmt|;
name|file
operator|.
name|write
argument_list|(
name|AsyncDataManager
operator|.
name|ITEM_HEAD_EOR
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
name|forceToDisk
operator||=
name|write
operator|.
name|sync
operator||
name|write
operator|.
name|onComplete
operator|!=
literal|null
expr_stmt|;
name|buff
operator|.
name|writeInt
argument_list|(
name|write
operator|.
name|location
operator|.
name|getSize
argument_list|()
argument_list|)
expr_stmt|;
name|buff
operator|.
name|writeByte
argument_list|(
name|write
operator|.
name|location
operator|.
name|getType
argument_list|()
argument_list|)
expr_stmt|;
name|buff
operator|.
name|write
argument_list|(
name|RESERVED_SPACE
argument_list|)
expr_stmt|;
name|buff
operator|.
name|write
argument_list|(
name|AsyncDataManager
operator|.
name|ITEM_HEAD_SOR
argument_list|)
expr_stmt|;
name|buff
operator|.
name|write
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
expr_stmt|;
name|buff
operator|.
name|write
argument_list|(
name|AsyncDataManager
operator|.
name|ITEM_HEAD_EOR
argument_list|)
expr_stmt|;
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
comment|// Now do the 1 big write.
name|ByteSequence
name|sequence
init|=
name|buff
operator|.
name|toByteSequence
argument_list|()
decl_stmt|;
name|file
operator|.
name|write
argument_list|(
name|sequence
operator|.
name|getData
argument_list|()
argument_list|,
name|sequence
operator|.
name|getOffset
argument_list|()
argument_list|,
name|sequence
operator|.
name|getLength
argument_list|()
argument_list|)
expr_stmt|;
name|buff
operator|.
name|reset
argument_list|()
expr_stmt|;
block|}
if|if
condition|(
name|forceToDisk
condition|)
block|{
name|file
operator|.
name|getFD
argument_list|()
operator|.
name|sync
argument_list|()
expr_stmt|;
block|}
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
if|if
condition|(
name|write
operator|.
name|onComplete
operator|!=
literal|null
condition|)
block|{
try|try
block|{
name|write
operator|.
name|onComplete
operator|.
name|run
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Throwable
name|e
parameter_list|)
block|{
name|e
operator|.
name|printStackTrace
argument_list|()
expr_stmt|;
block|}
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
comment|// Signal any waiting threads that the write is on disk.
name|wb
operator|.
name|latch
operator|.
name|countDown
argument_list|()
expr_stmt|;
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
if|if
condition|(
name|wb
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
name|wb
operator|.
name|exception
operator|.
name|set
argument_list|(
name|e
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|nextWriteBatch
operator|!=
literal|null
condition|)
block|{
name|nextWriteBatch
operator|.
name|latch
operator|.
name|countDown
argument_list|()
expr_stmt|;
name|nextWriteBatch
operator|.
name|exception
operator|.
name|set
argument_list|(
name|e
argument_list|)
expr_stmt|;
block|}
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
name|Throwable
name|ignore
parameter_list|)
block|{             }
name|shutdownDone
operator|.
name|countDown
argument_list|()
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

