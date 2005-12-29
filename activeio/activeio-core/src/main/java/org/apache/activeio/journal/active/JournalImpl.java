begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  *  * Copyright 2005-2006 The Apache Software Foundation  *  * Licensed under the Apache License, Version 2.0 (the "License");  * you may not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  * http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
package|package
name|org
operator|.
name|apache
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
name|InterruptedIOException
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
name|Journal
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
name|JournalEventListener
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
name|ByteBufferPacketPool
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
name|edu
operator|.
name|emory
operator|.
name|mathcs
operator|.
name|backport
operator|.
name|java
operator|.
name|util
operator|.
name|concurrent
operator|.
name|Callable
import|;
end_import

begin_import
import|import
name|edu
operator|.
name|emory
operator|.
name|mathcs
operator|.
name|backport
operator|.
name|java
operator|.
name|util
operator|.
name|concurrent
operator|.
name|ExecutionException
import|;
end_import

begin_import
import|import
name|edu
operator|.
name|emory
operator|.
name|mathcs
operator|.
name|backport
operator|.
name|java
operator|.
name|util
operator|.
name|concurrent
operator|.
name|FutureTask
import|;
end_import

begin_import
import|import
name|edu
operator|.
name|emory
operator|.
name|mathcs
operator|.
name|backport
operator|.
name|java
operator|.
name|util
operator|.
name|concurrent
operator|.
name|LinkedBlockingQueue
import|;
end_import

begin_import
import|import
name|edu
operator|.
name|emory
operator|.
name|mathcs
operator|.
name|backport
operator|.
name|java
operator|.
name|util
operator|.
name|concurrent
operator|.
name|ThreadFactory
import|;
end_import

begin_import
import|import
name|edu
operator|.
name|emory
operator|.
name|mathcs
operator|.
name|backport
operator|.
name|java
operator|.
name|util
operator|.
name|concurrent
operator|.
name|ThreadPoolExecutor
import|;
end_import

begin_import
import|import
name|edu
operator|.
name|emory
operator|.
name|mathcs
operator|.
name|backport
operator|.
name|java
operator|.
name|util
operator|.
name|concurrent
operator|.
name|TimeUnit
import|;
end_import

begin_comment
comment|/**  * A high speed Journal implementation. Inspired by the ideas of the<a  * href="http://howl.objectweb.org/">Howl</a> project but tailored to the needs  * of ActiveMQ.<p/>This Journal provides the following features:  *<ul>  *<li>Concurrent writes are batched into a single write/force done by a  * background thread.</li>  *<li>Uses preallocated logs to avoid disk fragmentation and performance  * degregation.</li>  *<li>The number and size of the preallocated logs are configurable.</li>  *<li>Uses direct ByteBuffers to write data to log files.</li>  *<li>Allows logs to grow in case of an overflow condition so that overflow  * exceptions are not not thrown. Grown logs that are inactivate (due to a new  * mark) are resized to their original size.</li>  *<li>No limit on the size of the record written to the journal</li>  *<li>Should be possible to extend so that multiple physical disk are used  * concurrently to increase throughput and decrease latency.</li>  *</ul>  *<p/>  *   * @version $Revision: 1.1 $  */
end_comment

begin_class
specifier|final
specifier|public
class|class
name|JournalImpl
implements|implements
name|Journal
block|{
specifier|public
specifier|static
specifier|final
name|int
name|DEFAULT_POOL_SIZE
init|=
name|Integer
operator|.
name|parseInt
argument_list|(
name|System
operator|.
name|getProperty
argument_list|(
literal|"org.apache.activeio.journal.active.DefaultPoolSize"
argument_list|,
literal|""
operator|+
operator|(
literal|5
operator|)
argument_list|)
argument_list|)
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|int
name|DEFAULT_PACKET_SIZE
init|=
name|Integer
operator|.
name|parseInt
argument_list|(
name|System
operator|.
name|getProperty
argument_list|(
literal|"org.apache.activeio.journal.active.DefaultPacketSize"
argument_list|,
literal|""
operator|+
operator|(
literal|1024
operator|*
literal|1024
operator|*
literal|4
operator|)
argument_list|)
argument_list|)
decl_stmt|;
specifier|static
specifier|final
specifier|private
name|int
name|OVERFLOW_RENOTIFICATION_DELAY
init|=
literal|500
decl_stmt|;
specifier|static
specifier|private
name|ByteBufferPacketPool
name|lastPool
decl_stmt|;
specifier|private
name|boolean
name|disposed
init|=
literal|false
decl_stmt|;
comment|// The id of the current log file that is being filled.
specifier|private
name|int
name|appendLogFileId
init|=
literal|0
decl_stmt|;
comment|// The offset in the current log file that is being filled.
specifier|private
name|int
name|appendLogFileOffset
init|=
literal|0
decl_stmt|;
comment|// Used to batch writes together.
specifier|private
name|BatchedWrite
name|pendingBatchWrite
decl_stmt|;
specifier|private
name|Location
name|lastMarkedLocation
decl_stmt|;
specifier|private
name|LogFileManager
name|file
decl_stmt|;
specifier|private
name|ThreadPoolExecutor
name|executor
decl_stmt|;
specifier|private
name|int
name|rolloverFence
decl_stmt|;
specifier|private
name|JournalEventListener
name|eventListener
decl_stmt|;
specifier|private
name|ByteBufferPacketPool
name|packetPool
decl_stmt|;
specifier|private
name|long
name|overflowNotificationTime
init|=
name|System
operator|.
name|currentTimeMillis
argument_list|()
decl_stmt|;
specifier|private
name|Packet
name|markPacket
init|=
operator|new
name|ByteArrayPacket
argument_list|(
operator|new
name|byte
index|[
name|Location
operator|.
name|SERIALIZED_SIZE
index|]
argument_list|)
decl_stmt|;
specifier|public
name|JournalImpl
parameter_list|(
name|File
name|logDirectory
parameter_list|)
throws|throws
name|IOException
block|{
name|this
argument_list|(
operator|new
name|LogFileManager
argument_list|(
name|logDirectory
argument_list|)
argument_list|)
expr_stmt|;
block|}
specifier|public
name|JournalImpl
parameter_list|(
name|File
name|logDirectory
parameter_list|,
name|int
name|logFileCount
parameter_list|,
name|int
name|logFileSize
parameter_list|)
throws|throws
name|IOException
block|{
name|this
argument_list|(
operator|new
name|LogFileManager
argument_list|(
name|logDirectory
argument_list|,
name|logFileCount
argument_list|,
name|logFileSize
argument_list|,
literal|null
argument_list|)
argument_list|)
expr_stmt|;
block|}
specifier|public
name|JournalImpl
parameter_list|(
name|File
name|logDirectory
parameter_list|,
name|int
name|logFileCount
parameter_list|,
name|int
name|logFileSize
parameter_list|,
name|File
name|archiveDirectory
parameter_list|)
throws|throws
name|IOException
block|{
name|this
argument_list|(
operator|new
name|LogFileManager
argument_list|(
name|logDirectory
argument_list|,
name|logFileCount
argument_list|,
name|logFileSize
argument_list|,
name|archiveDirectory
argument_list|)
argument_list|)
expr_stmt|;
block|}
specifier|public
name|JournalImpl
parameter_list|(
name|LogFileManager
name|logFile
parameter_list|)
block|{
name|this
operator|.
name|file
operator|=
name|logFile
expr_stmt|;
name|this
operator|.
name|packetPool
operator|=
name|createBufferPool
argument_list|()
expr_stmt|;
name|this
operator|.
name|executor
operator|=
operator|new
name|ThreadPoolExecutor
argument_list|(
literal|1
argument_list|,
literal|1
argument_list|,
literal|30
argument_list|,
name|TimeUnit
operator|.
name|SECONDS
argument_list|,
operator|new
name|LinkedBlockingQueue
argument_list|()
argument_list|,
operator|new
name|ThreadFactory
argument_list|()
block|{
specifier|public
name|Thread
name|newThread
parameter_list|(
name|Runnable
name|runnable
parameter_list|)
block|{
name|Thread
name|answer
init|=
operator|new
name|Thread
argument_list|(
name|runnable
argument_list|,
literal|"Journal Writer"
argument_list|)
decl_stmt|;
name|answer
operator|.
name|setPriority
argument_list|(
name|Thread
operator|.
name|MAX_PRIORITY
argument_list|)
expr_stmt|;
name|answer
operator|.
name|setDaemon
argument_list|(
literal|true
argument_list|)
expr_stmt|;
return|return
name|answer
return|;
block|}
block|}
argument_list|)
expr_stmt|;
name|executor
operator|.
name|allowCoreThreadTimeOut
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|lastMarkedLocation
operator|=
name|file
operator|.
name|getLastMarkedRecordLocation
argument_list|()
expr_stmt|;
name|Location
name|nextAppendLocation
init|=
name|file
operator|.
name|getNextAppendLocation
argument_list|()
decl_stmt|;
name|appendLogFileId
operator|=
name|nextAppendLocation
operator|.
name|getLogFileId
argument_list|()
expr_stmt|;
name|appendLogFileOffset
operator|=
name|nextAppendLocation
operator|.
name|getLogFileOffset
argument_list|()
expr_stmt|;
name|rolloverFence
operator|=
operator|(
name|file
operator|.
name|getInitialLogFileSize
argument_list|()
operator|/
literal|10
operator|)
operator|*
literal|9
expr_stmt|;
block|}
comment|/**      * When running unit tests we may not be able to create new pools fast enough      * since the old pools are not being gc'ed fast enough.  So we pool the pool.      * @return      */
specifier|synchronized
specifier|static
specifier|private
name|ByteBufferPacketPool
name|createBufferPool
parameter_list|()
block|{
if|if
condition|(
name|lastPool
operator|!=
literal|null
condition|)
block|{
name|ByteBufferPacketPool
name|rc
init|=
name|lastPool
decl_stmt|;
name|lastPool
operator|=
literal|null
expr_stmt|;
return|return
name|rc
return|;
block|}
else|else
block|{
return|return
operator|new
name|ByteBufferPacketPool
argument_list|(
name|DEFAULT_POOL_SIZE
argument_list|,
name|DEFAULT_PACKET_SIZE
argument_list|)
return|;
block|}
block|}
comment|/**      * When running unit tests we may not be able to create new pools fast enough      * since the old pools are not being gc'ed fast enough.  So we pool the pool.      * @return      */
specifier|synchronized
specifier|static
specifier|private
name|void
name|disposeBufferPool
parameter_list|(
name|ByteBufferPacketPool
name|pool
parameter_list|)
block|{
if|if
condition|(
name|lastPool
operator|!=
literal|null
condition|)
block|{
name|pool
operator|.
name|dispose
argument_list|()
expr_stmt|;
block|}
else|else
block|{
name|pool
operator|.
name|waitForPacketsToReturn
argument_list|()
expr_stmt|;
name|lastPool
operator|=
name|pool
expr_stmt|;
block|}
block|}
specifier|public
name|RecordLocation
name|write
parameter_list|(
name|Packet
name|data
parameter_list|,
name|boolean
name|sync
parameter_list|)
throws|throws
name|IOException
block|{
return|return
name|write
argument_list|(
name|LogFileManager
operator|.
name|DATA_RECORD_TYPE
argument_list|,
name|data
argument_list|,
name|sync
argument_list|,
literal|null
argument_list|)
return|;
block|}
specifier|private
name|Location
name|write
parameter_list|(
name|byte
name|recordType
parameter_list|,
name|Packet
name|data
parameter_list|,
name|boolean
name|sync
parameter_list|,
name|Location
name|mark
parameter_list|)
throws|throws
name|IOException
block|{
try|try
block|{
name|Location
name|location
decl_stmt|;
name|BatchedWrite
name|writeCommand
decl_stmt|;
name|Record
name|record
init|=
operator|new
name|Record
argument_list|(
name|recordType
argument_list|,
name|data
argument_list|,
name|mark
argument_list|)
decl_stmt|;
comment|// The following synchronized block is the bottle neck of the journal.  Make this
comment|// code faster and the journal should speed up.
synchronized|synchronized
init|(
name|this
init|)
block|{
if|if
condition|(
name|disposed
condition|)
block|{
throw|throw
operator|new
name|IOException
argument_list|(
literal|"Journal has been closed."
argument_list|)
throw|;
block|}
comment|// Create our record
name|location
operator|=
operator|new
name|Location
argument_list|(
name|appendLogFileId
argument_list|,
name|appendLogFileOffset
argument_list|)
expr_stmt|;
name|record
operator|.
name|setLocation
argument_list|(
name|location
argument_list|)
expr_stmt|;
comment|// Piggy back the packet on the pending write batch.
name|writeCommand
operator|=
name|addToPendingWriteBatch
argument_list|(
name|record
argument_list|,
name|mark
argument_list|,
name|sync
argument_list|)
expr_stmt|;
comment|// Update where the next record will land.
name|appendLogFileOffset
operator|+=
name|data
operator|.
name|limit
argument_list|()
operator|+
name|Record
operator|.
name|RECORD_BASE_SIZE
expr_stmt|;
name|rolloverCheck
argument_list|()
expr_stmt|;
block|}
if|if
condition|(
name|sync
condition|)
block|{
name|writeCommand
operator|.
name|waitForForce
argument_list|()
expr_stmt|;
block|}
return|return
name|location
return|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
throw|throw
name|e
throw|;
block|}
catch|catch
parameter_list|(
name|InterruptedException
name|e
parameter_list|)
block|{
throw|throw
operator|(
name|IOException
operator|)
operator|new
name|InterruptedIOException
argument_list|()
operator|.
name|initCause
argument_list|(
name|e
argument_list|)
throw|;
block|}
catch|catch
parameter_list|(
name|Throwable
name|e
parameter_list|)
block|{
throw|throw
operator|(
name|IOException
operator|)
operator|new
name|IOException
argument_list|(
literal|"Write failed: "
operator|+
name|e
argument_list|)
operator|.
name|initCause
argument_list|(
name|e
argument_list|)
throw|;
block|}
block|}
comment|/**      * @param record      * @return      * @throws InterruptedException      */
specifier|private
name|BatchedWrite
name|addToPendingWriteBatch
parameter_list|(
name|Record
name|record
parameter_list|,
name|Location
name|mark
parameter_list|,
name|boolean
name|force
parameter_list|)
throws|throws
name|InterruptedException
block|{
comment|// Load the write batch up with data from our record.
comment|// it may take more than one write batch if the record is large.
name|BatchedWrite
name|answer
init|=
literal|null
decl_stmt|;
while|while
condition|(
name|record
operator|.
name|hasRemaining
argument_list|()
condition|)
block|{
comment|// Do we need another BatchWrite?
name|boolean
name|queueTheWrite
init|=
literal|false
decl_stmt|;
if|if
condition|(
name|pendingBatchWrite
operator|==
literal|null
condition|)
block|{
name|pendingBatchWrite
operator|=
operator|new
name|BatchedWrite
argument_list|(
name|packetPool
operator|.
name|getPacket
argument_list|()
argument_list|)
expr_stmt|;
name|queueTheWrite
operator|=
literal|true
expr_stmt|;
block|}
name|answer
operator|=
name|pendingBatchWrite
expr_stmt|;
comment|// Can we continue to use the pendingBatchWrite?
name|boolean
name|full
init|=
operator|!
name|pendingBatchWrite
operator|.
name|append
argument_list|(
name|record
argument_list|,
name|mark
argument_list|,
name|force
argument_list|)
decl_stmt|;
if|if
condition|(
name|queueTheWrite
condition|)
block|{
specifier|final
name|BatchedWrite
name|queuedWrite
init|=
name|pendingBatchWrite
decl_stmt|;
name|executor
operator|.
name|execute
argument_list|(
operator|new
name|Runnable
argument_list|()
block|{
specifier|public
name|void
name|run
parameter_list|()
block|{
try|try
block|{
name|queuedWrite
argument_list|(
name|queuedWrite
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|InterruptedException
name|e
parameter_list|)
block|{                         }
block|}
block|}
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|full
condition|)
name|pendingBatchWrite
operator|=
literal|null
expr_stmt|;
block|}
return|return
name|answer
return|;
block|}
comment|/**      * This is a blocking call      *       * @param write      * @throws InterruptedException      */
specifier|private
name|void
name|queuedWrite
parameter_list|(
name|BatchedWrite
name|write
parameter_list|)
throws|throws
name|InterruptedException
block|{
comment|// Stop other threads from appending more pendingBatchWrite.
name|write
operator|.
name|flip
argument_list|()
expr_stmt|;
comment|// Do the write.
try|try
block|{
name|file
operator|.
name|append
argument_list|(
name|write
argument_list|)
expr_stmt|;
name|write
operator|.
name|forced
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Throwable
name|e
parameter_list|)
block|{
name|write
operator|.
name|writeFailed
argument_list|(
name|e
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|write
operator|.
name|getPacket
argument_list|()
operator|.
name|dispose
argument_list|()
expr_stmt|;
block|}
block|}
comment|/**      *       */
specifier|private
name|void
name|rolloverCheck
parameter_list|()
throws|throws
name|IOException
block|{
comment|// See if we need to issue an overflow notification.
if|if
condition|(
name|eventListener
operator|!=
literal|null
operator|&&
name|file
operator|.
name|isPastHalfActive
argument_list|()
operator|&&
name|overflowNotificationTime
operator|+
name|OVERFLOW_RENOTIFICATION_DELAY
operator|<
name|System
operator|.
name|currentTimeMillis
argument_list|()
condition|)
block|{
comment|// We need to send an overflow notification to free up
comment|// some logFiles.
name|Location
name|safeSpot
init|=
name|file
operator|.
name|getFirstRecordLocationOfSecondActiveLogFile
argument_list|()
decl_stmt|;
name|eventListener
operator|.
name|overflowNotification
argument_list|(
name|safeSpot
argument_list|)
expr_stmt|;
name|overflowNotificationTime
operator|=
name|System
operator|.
name|currentTimeMillis
argument_list|()
expr_stmt|;
block|}
comment|// Is it time to roll over?
if|if
condition|(
name|appendLogFileOffset
operator|>
name|rolloverFence
condition|)
block|{
comment|// Can we roll over?
if|if
condition|(
operator|!
name|file
operator|.
name|canActivateNextLogFile
argument_list|()
condition|)
block|{
comment|// don't delay the next overflow notification.
name|overflowNotificationTime
operator|-=
name|OVERFLOW_RENOTIFICATION_DELAY
expr_stmt|;
block|}
else|else
block|{
try|try
block|{
specifier|final
name|FutureTask
name|result
init|=
operator|new
name|FutureTask
argument_list|(
operator|new
name|Callable
argument_list|()
block|{
specifier|public
name|Object
name|call
parameter_list|()
throws|throws
name|Exception
block|{
return|return
name|queuedActivateNextLogFile
argument_list|()
return|;
block|}
block|}
argument_list|)
decl_stmt|;
name|executor
operator|.
name|execute
argument_list|(
name|result
argument_list|)
expr_stmt|;
name|Location
name|location
init|=
operator|(
name|Location
operator|)
name|result
operator|.
name|get
argument_list|()
decl_stmt|;
name|appendLogFileId
operator|=
name|location
operator|.
name|getLogFileId
argument_list|()
expr_stmt|;
name|appendLogFileOffset
operator|=
name|location
operator|.
name|getLogFileOffset
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
operator|(
name|IOException
operator|)
operator|new
name|IOException
argument_list|(
literal|"Interrupted."
argument_list|)
operator|.
name|initCause
argument_list|(
name|e
argument_list|)
throw|;
block|}
catch|catch
parameter_list|(
name|ExecutionException
name|e
parameter_list|)
block|{
throw|throw
name|handleExecutionException
argument_list|(
name|e
argument_list|)
throw|;
block|}
block|}
block|}
block|}
comment|/**      * This is a blocking call      */
specifier|private
name|Location
name|queuedActivateNextLogFile
parameter_list|()
throws|throws
name|IOException
block|{
name|file
operator|.
name|activateNextLogFile
argument_list|()
expr_stmt|;
return|return
name|file
operator|.
name|getNextAppendLocation
argument_list|()
return|;
block|}
comment|/**      * @param recordLocator      * @param force      * @return      * @throws InvalidRecordLocationException      * @throws IOException      * @throws InterruptedException      */
specifier|synchronized
specifier|public
name|void
name|setMark
parameter_list|(
name|RecordLocation
name|l
parameter_list|,
name|boolean
name|force
parameter_list|)
throws|throws
name|InvalidRecordLocationException
throws|,
name|IOException
block|{
name|Location
name|location
init|=
operator|(
name|Location
operator|)
name|l
decl_stmt|;
if|if
condition|(
name|location
operator|==
literal|null
condition|)
throw|throw
operator|new
name|InvalidRecordLocationException
argument_list|(
literal|"The location cannot be null."
argument_list|)
throw|;
if|if
condition|(
name|lastMarkedLocation
operator|!=
literal|null
operator|&&
name|location
operator|.
name|compareTo
argument_list|(
name|lastMarkedLocation
argument_list|)
operator|<
literal|0
condition|)
throw|throw
operator|new
name|InvalidRecordLocationException
argument_list|(
literal|"The location is less than the last mark."
argument_list|)
throw|;
name|markPacket
operator|.
name|clear
argument_list|()
expr_stmt|;
name|location
operator|.
name|writeToPacket
argument_list|(
name|markPacket
argument_list|)
expr_stmt|;
name|markPacket
operator|.
name|flip
argument_list|()
expr_stmt|;
name|write
argument_list|(
name|LogFileManager
operator|.
name|MARK_RECORD_TYPE
argument_list|,
name|markPacket
argument_list|,
name|force
argument_list|,
name|location
argument_list|)
expr_stmt|;
name|lastMarkedLocation
operator|=
name|location
expr_stmt|;
block|}
comment|/**      * @return      */
specifier|public
name|RecordLocation
name|getMark
parameter_list|()
block|{
return|return
name|lastMarkedLocation
return|;
block|}
comment|/**      * @param lastLocation      * @return      * @throws IOException      * @throws InvalidRecordLocationException      */
specifier|public
name|RecordLocation
name|getNextRecordLocation
parameter_list|(
specifier|final
name|RecordLocation
name|lastLocation
parameter_list|)
throws|throws
name|IOException
throws|,
name|InvalidRecordLocationException
block|{
if|if
condition|(
name|lastLocation
operator|==
literal|null
condition|)
block|{
if|if
condition|(
name|lastMarkedLocation
operator|!=
literal|null
condition|)
block|{
return|return
name|lastMarkedLocation
return|;
block|}
else|else
block|{
return|return
name|file
operator|.
name|getFirstActiveLogLocation
argument_list|()
return|;
block|}
block|}
comment|// Run this in the queued executor thread.
try|try
block|{
specifier|final
name|FutureTask
name|result
init|=
operator|new
name|FutureTask
argument_list|(
operator|new
name|Callable
argument_list|()
block|{
specifier|public
name|Object
name|call
parameter_list|()
throws|throws
name|Exception
block|{
return|return
name|queuedGetNextRecordLocation
argument_list|(
operator|(
name|Location
operator|)
name|lastLocation
argument_list|)
return|;
block|}
block|}
argument_list|)
decl_stmt|;
name|executor
operator|.
name|execute
argument_list|(
name|result
argument_list|)
expr_stmt|;
return|return
operator|(
name|Location
operator|)
name|result
operator|.
name|get
argument_list|()
return|;
block|}
catch|catch
parameter_list|(
name|InterruptedException
name|e
parameter_list|)
block|{
throw|throw
operator|(
name|IOException
operator|)
operator|new
name|IOException
argument_list|(
literal|"Interrupted."
argument_list|)
operator|.
name|initCause
argument_list|(
name|e
argument_list|)
throw|;
block|}
catch|catch
parameter_list|(
name|ExecutionException
name|e
parameter_list|)
block|{
throw|throw
name|handleExecutionException
argument_list|(
name|e
argument_list|)
throw|;
block|}
block|}
specifier|protected
name|IOException
name|handleExecutionException
parameter_list|(
name|ExecutionException
name|e
parameter_list|)
throws|throws
name|IOException
block|{
name|Throwable
name|cause
init|=
name|e
operator|.
name|getCause
argument_list|()
decl_stmt|;
if|if
condition|(
name|cause
operator|instanceof
name|IOException
condition|)
block|{
return|return
operator|(
name|IOException
operator|)
name|cause
return|;
block|}
else|else
block|{
return|return
operator|(
name|IOException
operator|)
operator|new
name|IOException
argument_list|(
name|cause
operator|.
name|getMessage
argument_list|()
argument_list|)
operator|.
name|initCause
argument_list|(
name|cause
argument_list|)
return|;
block|}
block|}
specifier|private
name|Location
name|queuedGetNextRecordLocation
parameter_list|(
name|Location
name|location
parameter_list|)
throws|throws
name|IOException
throws|,
name|InvalidRecordLocationException
block|{
return|return
name|file
operator|.
name|getNextDataRecordLocation
argument_list|(
name|location
argument_list|)
return|;
block|}
comment|/**      * @param location      * @return      * @throws InvalidRecordLocationException      * @throws IOException      */
specifier|public
name|Packet
name|read
parameter_list|(
specifier|final
name|RecordLocation
name|l
parameter_list|)
throws|throws
name|IOException
throws|,
name|InvalidRecordLocationException
block|{
specifier|final
name|Location
name|location
init|=
operator|(
name|Location
operator|)
name|l
decl_stmt|;
comment|// Run this in the queued executor thread.
try|try
block|{
specifier|final
name|FutureTask
name|result
init|=
operator|new
name|FutureTask
argument_list|(
operator|new
name|Callable
argument_list|()
block|{
specifier|public
name|Object
name|call
parameter_list|()
throws|throws
name|Exception
block|{
return|return
name|file
operator|.
name|readPacket
argument_list|(
name|location
argument_list|)
return|;
block|}
block|}
argument_list|)
decl_stmt|;
name|executor
operator|.
name|execute
argument_list|(
name|result
argument_list|)
expr_stmt|;
return|return
operator|(
name|Packet
operator|)
name|result
operator|.
name|get
argument_list|()
return|;
block|}
catch|catch
parameter_list|(
name|InterruptedException
name|e
parameter_list|)
block|{
throw|throw
operator|(
name|IOException
operator|)
operator|new
name|IOException
argument_list|(
literal|"Interrupted."
argument_list|)
operator|.
name|initCause
argument_list|(
name|e
argument_list|)
throw|;
block|}
catch|catch
parameter_list|(
name|ExecutionException
name|e
parameter_list|)
block|{
throw|throw
name|handleExecutionException
argument_list|(
name|e
argument_list|)
throw|;
block|}
block|}
specifier|public
name|void
name|setJournalEventListener
parameter_list|(
name|JournalEventListener
name|eventListener
parameter_list|)
block|{
name|this
operator|.
name|eventListener
operator|=
name|eventListener
expr_stmt|;
block|}
comment|/**      * @deprecated @see #dispose()      */
specifier|public
name|void
name|close
parameter_list|()
throws|throws
name|IOException
block|{
name|dispose
argument_list|()
expr_stmt|;
block|}
comment|/**      */
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
name|executor
operator|.
name|shutdown
argument_list|()
expr_stmt|;
name|file
operator|.
name|dispose
argument_list|()
expr_stmt|;
name|ByteBufferPacketPool
name|pool
init|=
name|packetPool
decl_stmt|;
name|packetPool
operator|=
literal|null
expr_stmt|;
name|disposeBufferPool
argument_list|(
name|pool
argument_list|)
expr_stmt|;
block|}
comment|/**      * @return      */
specifier|public
name|File
name|getLogDirectory
parameter_list|()
block|{
return|return
name|file
operator|.
name|getLogDirectory
argument_list|()
return|;
block|}
specifier|public
name|int
name|getInitialLogFileSize
parameter_list|()
block|{
return|return
name|file
operator|.
name|getInitialLogFileSize
argument_list|()
return|;
block|}
specifier|public
name|String
name|toString
parameter_list|()
block|{
return|return
literal|"Active Journal: using "
operator|+
name|file
operator|.
name|getOnlineLogFileCount
argument_list|()
operator|+
literal|" x "
operator|+
operator|(
name|file
operator|.
name|getInitialLogFileSize
argument_list|()
operator|/
operator|(
literal|1024
operator|*
literal|1024f
operator|)
operator|)
operator|+
literal|" Megs at: "
operator|+
name|getLogDirectory
argument_list|()
return|;
block|}
block|}
end_class

end_unit

