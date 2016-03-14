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
name|java
operator|.
name|util
operator|.
name|zip
operator|.
name|Adler32
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|zip
operator|.
name|Checksum
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
name|store
operator|.
name|kahadb
operator|.
name|disk
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
name|store
operator|.
name|kahadb
operator|.
name|disk
operator|.
name|util
operator|.
name|LinkedNodeList
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
name|RecoverableRandomAccessFile
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
comment|/**  * An optimized writer to do batch appends to a data file. This object is thread  * safe and gains throughput as you increase the number of concurrent writes it  * does.  */
end_comment

begin_class
class|class
name|DataFileAppender
implements|implements
name|FileAppender
block|{
specifier|private
specifier|static
specifier|final
name|Logger
name|logger
init|=
name|LoggerFactory
operator|.
name|getLogger
argument_list|(
name|DataFileAppender
operator|.
name|class
argument_list|)
decl_stmt|;
specifier|protected
specifier|final
name|Journal
name|journal
decl_stmt|;
specifier|protected
specifier|final
name|Map
argument_list|<
name|Journal
operator|.
name|WriteKey
argument_list|,
name|Journal
operator|.
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
decl_stmt|;
specifier|protected
specifier|final
name|boolean
name|syncOnComplete
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
name|LinkedNodeList
argument_list|<
name|Journal
operator|.
name|WriteCommand
argument_list|>
name|writes
init|=
operator|new
name|LinkedNodeList
argument_list|<
name|Journal
operator|.
name|WriteCommand
argument_list|>
argument_list|()
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
specifier|protected
specifier|final
name|int
name|offset
decl_stmt|;
specifier|public
name|int
name|size
init|=
name|Journal
operator|.
name|BATCH_CONTROL_RECORD_SIZE
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
name|int
name|offset
parameter_list|)
block|{
name|this
operator|.
name|dataFile
operator|=
name|dataFile
expr_stmt|;
name|this
operator|.
name|offset
operator|=
name|offset
expr_stmt|;
name|this
operator|.
name|dataFile
operator|.
name|incrementLength
argument_list|(
name|Journal
operator|.
name|BATCH_CONTROL_RECORD_SIZE
argument_list|)
expr_stmt|;
name|this
operator|.
name|size
operator|=
name|Journal
operator|.
name|BATCH_CONTROL_RECORD_SIZE
expr_stmt|;
name|journal
operator|.
name|addToTotalLength
argument_list|(
name|Journal
operator|.
name|BATCH_CONTROL_RECORD_SIZE
argument_list|)
expr_stmt|;
block|}
specifier|public
name|WriteBatch
parameter_list|(
name|DataFile
name|dataFile
parameter_list|,
name|int
name|offset
parameter_list|,
name|Journal
operator|.
name|WriteCommand
name|write
parameter_list|)
throws|throws
name|IOException
block|{
name|this
argument_list|(
name|dataFile
argument_list|,
name|offset
argument_list|)
expr_stmt|;
name|append
argument_list|(
name|write
argument_list|)
expr_stmt|;
block|}
specifier|public
name|boolean
name|canAppend
parameter_list|(
name|Journal
operator|.
name|WriteCommand
name|write
parameter_list|)
block|{
name|int
name|newSize
init|=
name|size
operator|+
name|write
operator|.
name|location
operator|.
name|getSize
argument_list|()
decl_stmt|;
if|if
condition|(
name|newSize
operator|>=
name|maxWriteBatchSize
operator|||
name|offset
operator|+
name|newSize
operator|>
name|journal
operator|.
name|getMaxFileLength
argument_list|()
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
name|Journal
operator|.
name|WriteCommand
name|write
parameter_list|)
throws|throws
name|IOException
block|{
name|this
operator|.
name|writes
operator|.
name|addLast
argument_list|(
name|write
argument_list|)
expr_stmt|;
name|write
operator|.
name|location
operator|.
name|setDataFileId
argument_list|(
name|dataFile
operator|.
name|getDataFileId
argument_list|()
argument_list|)
expr_stmt|;
name|write
operator|.
name|location
operator|.
name|setOffset
argument_list|(
name|offset
operator|+
name|size
argument_list|)
expr_stmt|;
name|int
name|s
init|=
name|write
operator|.
name|location
operator|.
name|getSize
argument_list|()
decl_stmt|;
name|size
operator|+=
name|s
expr_stmt|;
name|dataFile
operator|.
name|incrementLength
argument_list|(
name|s
argument_list|)
expr_stmt|;
name|journal
operator|.
name|addToTotalLength
argument_list|(
name|s
argument_list|)
expr_stmt|;
block|}
block|}
comment|/**      * Construct a Store writer      */
specifier|public
name|DataFileAppender
parameter_list|(
name|Journal
name|dataManager
parameter_list|)
block|{
name|this
operator|.
name|journal
operator|=
name|dataManager
expr_stmt|;
name|this
operator|.
name|inflightWrites
operator|=
name|this
operator|.
name|journal
operator|.
name|getInflightWrites
argument_list|()
expr_stmt|;
name|this
operator|.
name|maxWriteBatchSize
operator|=
name|this
operator|.
name|journal
operator|.
name|getWriteBatchSize
argument_list|()
expr_stmt|;
name|this
operator|.
name|syncOnComplete
operator|=
name|this
operator|.
name|journal
operator|.
name|isEnableAsyncDiskSync
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Override
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
name|Journal
operator|.
name|RECORD_HEAD_SPACE
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
name|Journal
operator|.
name|WriteCommand
name|write
init|=
operator|new
name|Journal
operator|.
name|WriteCommand
argument_list|(
name|location
argument_list|,
name|data
argument_list|,
name|sync
argument_list|)
decl_stmt|;
name|WriteBatch
name|batch
init|=
name|enqueue
argument_list|(
name|write
argument_list|)
decl_stmt|;
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
annotation|@
name|Override
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
name|Journal
operator|.
name|RECORD_HEAD_SPACE
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
name|Journal
operator|.
name|WriteCommand
name|write
init|=
operator|new
name|Journal
operator|.
name|WriteCommand
argument_list|(
name|location
argument_list|,
name|data
argument_list|,
name|onComplete
argument_list|)
decl_stmt|;
name|WriteBatch
name|batch
init|=
name|enqueue
argument_list|(
name|write
argument_list|)
decl_stmt|;
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
name|Journal
operator|.
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
if|if
condition|(
name|shutdown
condition|)
block|{
throw|throw
operator|new
name|IOException
argument_list|(
literal|"Async Writer Thread Shutdown"
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
annotation|@
name|Override
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
while|while
condition|(
literal|true
condition|)
block|{
if|if
condition|(
name|nextWriteBatch
operator|==
literal|null
condition|)
block|{
name|DataFile
name|file
init|=
name|journal
operator|.
name|getOrCreateCurrentWriteFile
argument_list|()
decl_stmt|;
if|if
condition|(
name|file
operator|.
name|getLength
argument_list|()
operator|+
name|write
operator|.
name|location
operator|.
name|getSize
argument_list|()
operator|>=
name|journal
operator|.
name|getMaxFileLength
argument_list|()
condition|)
block|{
name|file
operator|=
name|journal
operator|.
name|rotateWriteFile
argument_list|()
expr_stmt|;
block|}
name|nextWriteBatch
operator|=
name|newWriteBatch
argument_list|(
name|write
argument_list|,
name|file
argument_list|)
expr_stmt|;
name|enqueueMutex
operator|.
name|notifyAll
argument_list|()
expr_stmt|;
break|break;
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
break|break;
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
specifier|final
name|long
name|start
init|=
name|System
operator|.
name|currentTimeMillis
argument_list|()
decl_stmt|;
name|enqueueMutex
operator|.
name|wait
argument_list|()
expr_stmt|;
if|if
condition|(
name|maxStat
operator|>
literal|0
condition|)
block|{
name|logger
operator|.
name|info
argument_list|(
literal|"Waiting for write to finish with full batch... millis: "
operator|+
operator|(
name|System
operator|.
name|currentTimeMillis
argument_list|()
operator|-
name|start
operator|)
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
literal|"Async Writer Thread Shutdown"
argument_list|)
throw|;
block|}
block|}
block|}
block|}
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
name|put
argument_list|(
operator|new
name|Journal
operator|.
name|WriteKey
argument_list|(
name|write
operator|.
name|location
argument_list|)
argument_list|,
name|write
argument_list|)
expr_stmt|;
block|}
return|return
name|nextWriteBatch
return|;
block|}
block|}
specifier|protected
name|WriteBatch
name|newWriteBatch
parameter_list|(
name|Journal
operator|.
name|WriteCommand
name|write
parameter_list|,
name|DataFile
name|file
parameter_list|)
throws|throws
name|IOException
block|{
return|return
operator|new
name|WriteBatch
argument_list|(
name|file
argument_list|,
name|file
operator|.
name|getLength
argument_list|()
argument_list|,
name|write
argument_list|)
return|;
block|}
annotation|@
name|Override
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
name|int
name|statIdx
init|=
literal|0
decl_stmt|;
name|int
index|[]
name|stats
init|=
operator|new
name|int
index|[
name|maxStat
index|]
decl_stmt|;
specifier|final
name|byte
index|[]
name|end
init|=
operator|new
name|byte
index|[]
block|{
literal|0
block|}
decl_stmt|;
comment|/**      * The async processing loop that writes to the data files and does the      * force calls. Since the file sync() call is the slowest of all the      * operations, this algorithm tries to 'batch' or group together several      * file sync() requests into a single file sync() call. The batching is      * accomplished attaching the same CountDownLatch instance to every force      * request in a group.      */
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
name|RecoverableRandomAccessFile
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
init|(
name|DataByteArrayOutputStream
name|buff
init|=
operator|new
name|DataByteArrayOutputStream
argument_list|(
name|maxWriteBatchSize
argument_list|)
init|;
init|)
block|{
while|while
condition|(
literal|true
condition|)
block|{
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
name|wb
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
name|notifyAll
argument_list|()
expr_stmt|;
block|}
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
argument_list|()
expr_stmt|;
comment|// pre allocate on first open of new file (length==0)
comment|// note dataFile.length cannot be used because it is updated in enqueue
if|if
condition|(
name|file
operator|.
name|length
argument_list|()
operator|==
literal|0l
condition|)
block|{
name|journal
operator|.
name|preallocateEntireJournalDataFile
argument_list|(
name|file
argument_list|)
expr_stmt|;
block|}
block|}
name|Journal
operator|.
name|WriteCommand
name|write
init|=
name|wb
operator|.
name|writes
operator|.
name|getHead
argument_list|()
decl_stmt|;
comment|// Write an empty batch control record.
name|buff
operator|.
name|reset
argument_list|()
expr_stmt|;
name|buff
operator|.
name|writeInt
argument_list|(
name|Journal
operator|.
name|BATCH_CONTROL_RECORD_SIZE
argument_list|)
expr_stmt|;
name|buff
operator|.
name|writeByte
argument_list|(
name|Journal
operator|.
name|BATCH_CONTROL_RECORD_TYPE
argument_list|)
expr_stmt|;
name|buff
operator|.
name|write
argument_list|(
name|Journal
operator|.
name|BATCH_CONTROL_RECORD_MAGIC
argument_list|)
expr_stmt|;
name|buff
operator|.
name|writeInt
argument_list|(
literal|0
argument_list|)
expr_stmt|;
name|buff
operator|.
name|writeLong
argument_list|(
literal|0
argument_list|)
expr_stmt|;
name|boolean
name|forceToDisk
init|=
literal|false
decl_stmt|;
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
operator|(
name|syncOnComplete
operator|&&
name|write
operator|.
name|onComplete
operator|!=
literal|null
operator|)
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
name|write
operator|=
name|write
operator|.
name|getNext
argument_list|()
expr_stmt|;
block|}
comment|// append 'unset' next batch (5 bytes) so read can always find eof
name|buff
operator|.
name|writeInt
argument_list|(
literal|0
argument_list|)
expr_stmt|;
name|buff
operator|.
name|writeByte
argument_list|(
literal|0
argument_list|)
expr_stmt|;
name|ByteSequence
name|sequence
init|=
name|buff
operator|.
name|toByteSequence
argument_list|()
decl_stmt|;
comment|// Now we can fill in the batch control record properly.
name|buff
operator|.
name|reset
argument_list|()
expr_stmt|;
name|buff
operator|.
name|skip
argument_list|(
literal|5
operator|+
name|Journal
operator|.
name|BATCH_CONTROL_RECORD_MAGIC
operator|.
name|length
argument_list|)
expr_stmt|;
name|buff
operator|.
name|writeInt
argument_list|(
name|sequence
operator|.
name|getLength
argument_list|()
operator|-
name|Journal
operator|.
name|BATCH_CONTROL_RECORD_SIZE
operator|-
literal|5
argument_list|)
expr_stmt|;
if|if
condition|(
name|journal
operator|.
name|isChecksum
argument_list|()
condition|)
block|{
name|Checksum
name|checksum
init|=
operator|new
name|Adler32
argument_list|()
decl_stmt|;
name|checksum
operator|.
name|update
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
operator|+
name|Journal
operator|.
name|BATCH_CONTROL_RECORD_SIZE
argument_list|,
name|sequence
operator|.
name|getLength
argument_list|()
operator|-
name|Journal
operator|.
name|BATCH_CONTROL_RECORD_SIZE
operator|-
literal|5
argument_list|)
expr_stmt|;
name|buff
operator|.
name|writeLong
argument_list|(
name|checksum
operator|.
name|getValue
argument_list|()
argument_list|)
expr_stmt|;
block|}
comment|// Now do the 1 big write.
name|file
operator|.
name|seek
argument_list|(
name|wb
operator|.
name|offset
argument_list|)
expr_stmt|;
if|if
condition|(
name|maxStat
operator|>
literal|0
condition|)
block|{
if|if
condition|(
name|statIdx
operator|<
name|maxStat
condition|)
block|{
name|stats
index|[
name|statIdx
operator|++
index|]
operator|=
name|sequence
operator|.
name|getLength
argument_list|()
expr_stmt|;
block|}
else|else
block|{
name|long
name|all
init|=
literal|0
decl_stmt|;
for|for
control|(
init|;
name|statIdx
operator|>
literal|0
condition|;
control|)
block|{
name|all
operator|+=
name|stats
index|[
operator|--
name|statIdx
index|]
expr_stmt|;
block|}
name|logger
operator|.
name|info
argument_list|(
literal|"Ave writeSize: "
operator|+
name|all
operator|/
name|maxStat
argument_list|)
expr_stmt|;
block|}
block|}
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
name|ReplicationTarget
name|replicationTarget
init|=
name|journal
operator|.
name|getReplicationTarget
argument_list|()
decl_stmt|;
if|if
condition|(
name|replicationTarget
operator|!=
literal|null
condition|)
block|{
name|replicationTarget
operator|.
name|replicate
argument_list|(
name|wb
operator|.
name|writes
operator|.
name|getHead
argument_list|()
operator|.
name|location
argument_list|,
name|sequence
argument_list|,
name|forceToDisk
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|forceToDisk
condition|)
block|{
name|file
operator|.
name|sync
argument_list|()
expr_stmt|;
block|}
name|Journal
operator|.
name|WriteCommand
name|lastWrite
init|=
name|wb
operator|.
name|writes
operator|.
name|getTail
argument_list|()
decl_stmt|;
name|journal
operator|.
name|setLastAppendLocation
argument_list|(
name|lastWrite
operator|.
name|location
argument_list|)
expr_stmt|;
name|signalDone
argument_list|(
name|wb
argument_list|)
expr_stmt|;
block|}
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
name|logger
operator|.
name|info
argument_list|(
literal|"Journal failed while writing at: "
operator|+
name|wb
operator|.
name|offset
argument_list|)
expr_stmt|;
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
name|exception
operator|.
name|set
argument_list|(
name|e
argument_list|)
expr_stmt|;
name|wb
operator|.
name|latch
operator|.
name|countDown
argument_list|()
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
name|exception
operator|.
name|set
argument_list|(
name|e
argument_list|)
expr_stmt|;
name|nextWriteBatch
operator|.
name|latch
operator|.
name|countDown
argument_list|()
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
name|running
operator|=
literal|false
expr_stmt|;
block|}
block|}
specifier|protected
name|void
name|signalDone
parameter_list|(
name|WriteBatch
name|wb
parameter_list|)
block|{
comment|// Now that the data is on disk, remove the writes from the in
comment|// flight
comment|// cache.
name|Journal
operator|.
name|WriteCommand
name|write
init|=
name|wb
operator|.
name|writes
operator|.
name|getHead
argument_list|()
decl_stmt|;
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
name|Journal
operator|.
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
name|logger
operator|.
name|info
argument_list|(
literal|"Add exception was raised while executing the run command for onComplete"
argument_list|,
name|e
argument_list|)
expr_stmt|;
block|}
block|}
name|write
operator|=
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
end_class

end_unit

