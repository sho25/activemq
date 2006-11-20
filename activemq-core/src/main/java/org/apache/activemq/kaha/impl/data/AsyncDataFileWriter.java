begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  *  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  * http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
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
name|data
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
name|concurrent
operator|.
name|ConcurrentHashMap
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
name|Marshaller
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
name|StoreLocation
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
name|memory
operator|.
name|UsageManager
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
name|java
operator|.
name|util
operator|.
name|LinkedList
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

begin_comment
comment|/**  * Optimized Store writer that uses an async thread do batched writes to   * the datafile.  *   * @version $Revision: 1.1.1.1 $  */
end_comment

begin_class
specifier|final
class|class
name|AsyncDataFileWriter
implements|implements
name|DataFileWriter
block|{
comment|//    static final Log log = LogFactory.getLog(AsyncDataFileWriter.class);
specifier|private
specifier|static
specifier|final
name|String
name|SHUTDOWN_COMMAND
init|=
literal|"SHUTDOWN"
decl_stmt|;
specifier|static
specifier|public
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
name|StoreLocation
name|item
parameter_list|)
block|{
name|file
operator|=
name|item
operator|.
name|getFile
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
block|}
specifier|public
specifier|static
class|class
name|WriteCommand
block|{
specifier|public
specifier|final
name|StoreLocation
name|location
decl_stmt|;
specifier|public
specifier|final
name|RandomAccessFile
name|dataFile
decl_stmt|;
specifier|public
specifier|final
name|byte
index|[]
name|data
decl_stmt|;
specifier|public
specifier|final
name|CountDownLatch
name|latch
decl_stmt|;
specifier|public
name|WriteCommand
parameter_list|(
name|StoreLocation
name|location
parameter_list|,
name|RandomAccessFile
name|dataFile
parameter_list|,
name|byte
index|[]
name|data
parameter_list|,
name|CountDownLatch
name|latch
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
name|dataFile
operator|=
name|dataFile
expr_stmt|;
name|this
operator|.
name|data
operator|=
name|data
expr_stmt|;
name|this
operator|.
name|latch
operator|=
name|latch
expr_stmt|;
block|}
specifier|public
name|String
name|toString
parameter_list|()
block|{
return|return
literal|"write: "
operator|+
name|location
operator|+
literal|", latch = "
operator|+
name|System
operator|.
name|identityHashCode
argument_list|(
name|latch
argument_list|)
return|;
block|}
block|}
specifier|private
name|DataManager
name|dataManager
decl_stmt|;
specifier|private
specifier|final
name|Object
name|enqueueMutex
init|=
operator|new
name|Object
argument_list|()
decl_stmt|;
specifier|private
specifier|final
name|LinkedList
name|queue
init|=
operator|new
name|LinkedList
argument_list|()
decl_stmt|;
comment|// Maps WriteKey -> WriteCommand for all the writes that still have not landed on
comment|// disk.
specifier|private
specifier|final
name|ConcurrentHashMap
name|inflightWrites
init|=
operator|new
name|ConcurrentHashMap
argument_list|()
decl_stmt|;
specifier|private
specifier|final
name|UsageManager
name|usage
init|=
operator|new
name|UsageManager
argument_list|()
decl_stmt|;
specifier|private
name|CountDownLatch
name|latchAssignedToNewWrites
init|=
operator|new
name|CountDownLatch
argument_list|(
literal|1
argument_list|)
decl_stmt|;
specifier|private
name|boolean
name|running
decl_stmt|;
specifier|private
name|boolean
name|shutdown
decl_stmt|;
specifier|private
name|IOException
name|firstAsyncException
decl_stmt|;
specifier|private
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
comment|/**      * Construct a Store writer      *       * @param file      */
name|AsyncDataFileWriter
parameter_list|(
name|DataManager
name|fileManager
parameter_list|)
block|{
name|this
operator|.
name|dataManager
operator|=
name|fileManager
expr_stmt|;
name|this
operator|.
name|usage
operator|.
name|setLimit
argument_list|(
literal|1024
operator|*
literal|1024
operator|*
literal|8
argument_list|)
expr_stmt|;
comment|// Allow about 8 megs of concurrent data to be queued up
block|}
specifier|public
name|void
name|force
parameter_list|(
specifier|final
name|DataFile
name|dataFile
parameter_list|)
throws|throws
name|IOException
block|{
try|try
block|{
name|CountDownLatch
name|latch
init|=
literal|null
decl_stmt|;
synchronized|synchronized
init|(
name|enqueueMutex
init|)
block|{
name|latch
operator|=
operator|(
name|CountDownLatch
operator|)
name|dataFile
operator|.
name|getWriterData
argument_list|()
expr_stmt|;
block|}
if|if
condition|(
name|latch
operator|==
literal|null
condition|)
block|{
return|return;
block|}
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
block|}
comment|/**      * @param marshaller      * @param payload      * @param type       * @return      * @throws IOException      */
specifier|public
specifier|synchronized
name|DataItem
name|storeItem
parameter_list|(
name|Marshaller
name|marshaller
parameter_list|,
name|Object
name|payload
parameter_list|,
name|byte
name|type
parameter_list|)
throws|throws
name|IOException
block|{
comment|// We may need to slow down if we are pounding the async thread too
comment|// hard..
try|try
block|{
name|usage
operator|.
name|waitForSpace
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
comment|// Write the packet our internal buffer.
specifier|final
name|DataByteArrayOutputStream
name|buffer
init|=
operator|new
name|DataByteArrayOutputStream
argument_list|()
decl_stmt|;
name|buffer
operator|.
name|position
argument_list|(
name|DataManager
operator|.
name|ITEM_HEAD_SIZE
argument_list|)
expr_stmt|;
name|marshaller
operator|.
name|writePayload
argument_list|(
name|payload
argument_list|,
name|buffer
argument_list|)
expr_stmt|;
specifier|final
name|int
name|size
init|=
name|buffer
operator|.
name|size
argument_list|()
decl_stmt|;
name|int
name|payloadSize
init|=
name|size
operator|-
name|DataManager
operator|.
name|ITEM_HEAD_SIZE
decl_stmt|;
name|buffer
operator|.
name|reset
argument_list|()
expr_stmt|;
name|buffer
operator|.
name|writeByte
argument_list|(
name|type
argument_list|)
expr_stmt|;
name|buffer
operator|.
name|writeInt
argument_list|(
name|payloadSize
argument_list|)
expr_stmt|;
specifier|final
name|DataItem
name|item
init|=
operator|new
name|DataItem
argument_list|()
decl_stmt|;
name|item
operator|.
name|setSize
argument_list|(
name|payloadSize
argument_list|)
expr_stmt|;
name|usage
operator|.
name|increaseUsage
argument_list|(
name|size
argument_list|)
expr_stmt|;
comment|// Locate datafile and enqueue into the executor in sychronized block so that
comment|// writes get equeued onto the executor in order that they were assigned by
comment|// the data manager (which is basically just appending)
name|WriteCommand
name|write
decl_stmt|;
synchronized|synchronized
init|(
name|enqueueMutex
init|)
block|{
comment|// Find the position where this item will land at.
specifier|final
name|DataFile
name|dataFile
init|=
name|dataManager
operator|.
name|findSpaceForData
argument_list|(
name|item
argument_list|)
decl_stmt|;
name|dataManager
operator|.
name|addInterestInFile
argument_list|(
name|dataFile
argument_list|)
expr_stmt|;
name|dataFile
operator|.
name|setWriterData
argument_list|(
name|latchAssignedToNewWrites
argument_list|)
expr_stmt|;
name|write
operator|=
operator|new
name|WriteCommand
argument_list|(
name|item
argument_list|,
name|dataFile
operator|.
name|getRandomAccessFile
argument_list|()
argument_list|,
name|buffer
operator|.
name|getData
argument_list|()
argument_list|,
name|latchAssignedToNewWrites
argument_list|)
expr_stmt|;
name|enqueue
argument_list|(
name|write
argument_list|)
expr_stmt|;
block|}
name|inflightWrites
operator|.
name|put
argument_list|(
operator|new
name|WriteKey
argument_list|(
name|item
argument_list|)
argument_list|,
name|write
argument_list|)
expr_stmt|;
return|return
name|item
return|;
block|}
comment|/**      *       */
specifier|public
name|void
name|updateItem
parameter_list|(
specifier|final
name|DataItem
name|item
parameter_list|,
name|Marshaller
name|marshaller
parameter_list|,
name|Object
name|payload
parameter_list|,
name|byte
name|type
parameter_list|)
throws|throws
name|IOException
block|{
comment|// We may need to slow down if we are pounding the async thread too
comment|// hard..
try|try
block|{
name|usage
operator|.
name|waitForSpace
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
synchronized|synchronized
init|(
name|enqueueMutex
init|)
block|{
comment|// Write the packet our internal buffer.
specifier|final
name|DataByteArrayOutputStream
name|buffer
init|=
operator|new
name|DataByteArrayOutputStream
argument_list|()
decl_stmt|;
name|buffer
operator|.
name|position
argument_list|(
name|DataManager
operator|.
name|ITEM_HEAD_SIZE
argument_list|)
expr_stmt|;
name|marshaller
operator|.
name|writePayload
argument_list|(
name|payload
argument_list|,
name|buffer
argument_list|)
expr_stmt|;
specifier|final
name|int
name|size
init|=
name|buffer
operator|.
name|size
argument_list|()
decl_stmt|;
name|int
name|payloadSize
init|=
name|size
operator|-
name|DataManager
operator|.
name|ITEM_HEAD_SIZE
decl_stmt|;
name|buffer
operator|.
name|reset
argument_list|()
expr_stmt|;
name|buffer
operator|.
name|writeByte
argument_list|(
name|type
argument_list|)
expr_stmt|;
name|buffer
operator|.
name|writeInt
argument_list|(
name|payloadSize
argument_list|)
expr_stmt|;
name|item
operator|.
name|setSize
argument_list|(
name|payloadSize
argument_list|)
expr_stmt|;
specifier|final
name|DataFile
name|dataFile
init|=
name|dataManager
operator|.
name|getDataFile
argument_list|(
name|item
argument_list|)
decl_stmt|;
name|usage
operator|.
name|increaseUsage
argument_list|(
name|size
argument_list|)
expr_stmt|;
name|WriteCommand
name|write
init|=
operator|new
name|WriteCommand
argument_list|(
name|item
argument_list|,
name|dataFile
operator|.
name|getRandomAccessFile
argument_list|()
argument_list|,
name|buffer
operator|.
name|getData
argument_list|()
argument_list|,
name|latchAssignedToNewWrites
argument_list|)
decl_stmt|;
comment|// Equeue the write to an async thread.
synchronized|synchronized
init|(
name|enqueueMutex
init|)
block|{
name|dataFile
operator|.
name|setWriterData
argument_list|(
name|latchAssignedToNewWrites
argument_list|)
expr_stmt|;
name|enqueue
argument_list|(
name|write
argument_list|)
expr_stmt|;
block|}
name|inflightWrites
operator|.
name|put
argument_list|(
operator|new
name|WriteKey
argument_list|(
name|item
argument_list|)
argument_list|,
name|write
argument_list|)
expr_stmt|;
block|}
block|}
specifier|private
name|void
name|enqueue
parameter_list|(
name|Object
name|command
parameter_list|)
throws|throws
name|IOException
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
literal|"Async Writter Thread Shutdown"
argument_list|)
throw|;
block|}
if|if
condition|(
name|firstAsyncException
operator|!=
literal|null
condition|)
throw|throw
name|firstAsyncException
throw|;
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
name|Thread
name|thread
init|=
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
decl_stmt|;
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
block|}
name|queue
operator|.
name|addLast
argument_list|(
name|command
argument_list|)
expr_stmt|;
name|enqueueMutex
operator|.
name|notify
argument_list|()
expr_stmt|;
block|}
specifier|private
name|Object
name|dequeue
parameter_list|()
block|{
synchronized|synchronized
init|(
name|enqueueMutex
init|)
block|{
while|while
condition|(
name|queue
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
name|inflightWrites
operator|.
name|clear
argument_list|()
expr_stmt|;
try|try
block|{
name|enqueueMutex
operator|.
name|wait
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|InterruptedException
name|e
parameter_list|)
block|{
return|return
name|SHUTDOWN_COMMAND
return|;
block|}
block|}
return|return
name|queue
operator|.
name|removeFirst
argument_list|()
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
name|shutdown
operator|==
literal|false
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
name|queue
operator|.
name|add
argument_list|(
name|SHUTDOWN_COMMAND
argument_list|)
expr_stmt|;
name|enqueueMutex
operator|.
name|notify
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
name|boolean
name|isShutdown
parameter_list|()
block|{
synchronized|synchronized
init|(
name|enqueueMutex
init|)
block|{
return|return
name|shutdown
return|;
block|}
block|}
comment|/**      * The async processing loop that writes to the data files and      * does the force calls.        *       * Since the file sync() call is the slowest of all the operations,       * this algorithm tries to 'batch' or group together several file sync() requests       * into a single file sync() call. The batching is accomplished attaching the       * same CountDownLatch instance to every force request in a group.      *       */
specifier|private
name|void
name|processQueue
parameter_list|()
block|{
comment|//    	log.debug("Async thread startup");
try|try
block|{
name|CountDownLatch
name|currentBatchLatch
init|=
literal|null
decl_stmt|;
name|RandomAccessFile
name|currentBatchDataFile
init|=
literal|null
decl_stmt|;
while|while
condition|(
literal|true
condition|)
block|{
comment|// Block till we get a command.
name|Object
name|o
init|=
name|dequeue
argument_list|()
decl_stmt|;
comment|//        		log.debug("Processing: "+o);
if|if
condition|(
name|o
operator|==
name|SHUTDOWN_COMMAND
condition|)
block|{
if|if
condition|(
name|currentBatchLatch
operator|!=
literal|null
condition|)
block|{
name|currentBatchDataFile
operator|.
name|getFD
argument_list|()
operator|.
name|sync
argument_list|()
expr_stmt|;
name|currentBatchLatch
operator|.
name|countDown
argument_list|()
expr_stmt|;
block|}
break|break;
block|}
elseif|else
if|if
condition|(
name|o
operator|.
name|getClass
argument_list|()
operator|==
name|CountDownLatch
operator|.
name|class
condition|)
block|{
comment|// The CountDownLatch is used as the end of batch indicator.
comment|// Must match..
if|if
condition|(
name|o
operator|==
name|currentBatchLatch
condition|)
block|{
name|currentBatchDataFile
operator|.
name|getFD
argument_list|()
operator|.
name|sync
argument_list|()
expr_stmt|;
name|currentBatchLatch
operator|.
name|countDown
argument_list|()
expr_stmt|;
name|currentBatchLatch
operator|=
literal|null
expr_stmt|;
name|currentBatchDataFile
operator|=
literal|null
expr_stmt|;
block|}
else|else
block|{
operator|new
name|IOException
argument_list|(
literal|"Got an out of sequence end of end of batch indicator."
argument_list|)
expr_stmt|;
block|}
block|}
elseif|else
if|if
condition|(
name|o
operator|.
name|getClass
argument_list|()
operator|==
name|WriteCommand
operator|.
name|class
condition|)
block|{
name|WriteCommand
name|write
init|=
operator|(
name|WriteCommand
operator|)
name|o
decl_stmt|;
if|if
condition|(
name|currentBatchDataFile
operator|==
literal|null
condition|)
name|currentBatchDataFile
operator|=
name|write
operator|.
name|dataFile
expr_stmt|;
comment|// We may need to prematurely sync if the batch
comment|// if user is switching between data files.
if|if
condition|(
name|currentBatchDataFile
operator|!=
name|write
operator|.
name|dataFile
condition|)
block|{
name|currentBatchDataFile
operator|.
name|getFD
argument_list|()
operator|.
name|sync
argument_list|()
expr_stmt|;
name|currentBatchDataFile
operator|=
name|write
operator|.
name|dataFile
expr_stmt|;
block|}
comment|// Write to the data..
name|int
name|size
init|=
name|write
operator|.
name|location
operator|.
name|getSize
argument_list|()
operator|+
name|DataManager
operator|.
name|ITEM_HEAD_SIZE
decl_stmt|;
synchronized|synchronized
init|(
name|write
operator|.
name|dataFile
init|)
block|{
name|write
operator|.
name|dataFile
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
name|write
operator|.
name|dataFile
operator|.
name|write
argument_list|(
name|write
operator|.
name|data
argument_list|,
literal|0
argument_list|,
name|size
argument_list|)
expr_stmt|;
block|}
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
name|usage
operator|.
name|decreaseUsage
argument_list|(
name|size
argument_list|)
expr_stmt|;
comment|// Start of a batch..
if|if
condition|(
name|currentBatchLatch
operator|==
literal|null
condition|)
block|{
name|currentBatchLatch
operator|=
name|write
operator|.
name|latch
expr_stmt|;
synchronized|synchronized
init|(
name|enqueueMutex
init|)
block|{
comment|// get the request threads to start using a new latch..
comment|// write commands allready in the queue should have the
comment|// same latch assigned.
name|latchAssignedToNewWrites
operator|=
operator|new
name|CountDownLatch
argument_list|(
literal|1
argument_list|)
expr_stmt|;
if|if
condition|(
operator|!
name|shutdown
condition|)
block|{
name|enqueue
argument_list|(
name|currentBatchLatch
argument_list|)
expr_stmt|;
block|}
block|}
block|}
elseif|else
if|if
condition|(
name|currentBatchLatch
operator|!=
name|write
operator|.
name|latch
condition|)
block|{
comment|// the latch on subsequent writes should match.
operator|new
name|IOException
argument_list|(
literal|"Got an out of sequence write"
argument_list|)
expr_stmt|;
block|}
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
comment|//			log.debug("Aync thread shutdown due to error: "+e,e);
block|}
finally|finally
block|{
comment|//			log.debug("Aync thread shutdown");
name|shutdownDone
operator|.
name|countDown
argument_list|()
expr_stmt|;
block|}
block|}
specifier|public
specifier|synchronized
name|ConcurrentHashMap
name|getInflightWrites
parameter_list|()
block|{
return|return
name|inflightWrites
return|;
block|}
block|}
end_class

end_unit

