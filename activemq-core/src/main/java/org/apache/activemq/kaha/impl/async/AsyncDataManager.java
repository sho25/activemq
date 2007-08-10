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
name|ByteArrayInputStream
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|ByteArrayOutputStream
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|DataInputStream
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|DataOutputStream
import|;
end_import

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
name|FilenameFilter
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
name|java
operator|.
name|util
operator|.
name|Collections
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|HashMap
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
name|Iterator
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|List
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
name|Set
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
name|kaha
operator|.
name|impl
operator|.
name|async
operator|.
name|DataFileAppender
operator|.
name|WriteCommand
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
name|DataFileAppender
operator|.
name|WriteKey
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
name|thread
operator|.
name|Scheduler
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
comment|/**  * Manages DataFiles  *   * @version $Revision: 1.1.1.1 $  */
end_comment

begin_class
specifier|public
specifier|final
class|class
name|AsyncDataManager
block|{
specifier|public
specifier|static
specifier|final
name|int
name|CONTROL_RECORD_MAX_LENGTH
init|=
literal|1024
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|int
name|ITEM_HEAD_RESERVED_SPACE
init|=
literal|21
decl_stmt|;
comment|// ITEM_HEAD_SPACE = length + type+ reserved space + SOR
specifier|public
specifier|static
specifier|final
name|int
name|ITEM_HEAD_SPACE
init|=
literal|4
operator|+
literal|1
operator|+
name|ITEM_HEAD_RESERVED_SPACE
operator|+
literal|3
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|int
name|ITEM_HEAD_OFFSET_TO_SOR
init|=
name|ITEM_HEAD_SPACE
operator|-
literal|3
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|int
name|ITEM_FOOT_SPACE
init|=
literal|3
decl_stmt|;
comment|// EOR
specifier|public
specifier|static
specifier|final
name|int
name|ITEM_HEAD_FOOT_SPACE
init|=
name|ITEM_HEAD_SPACE
operator|+
name|ITEM_FOOT_SPACE
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|byte
index|[]
name|ITEM_HEAD_SOR
init|=
operator|new
name|byte
index|[]
block|{
literal|'S'
block|,
literal|'O'
block|,
literal|'R'
block|}
decl_stmt|;
comment|//
specifier|public
specifier|static
specifier|final
name|byte
index|[]
name|ITEM_HEAD_EOR
init|=
operator|new
name|byte
index|[]
block|{
literal|'E'
block|,
literal|'O'
block|,
literal|'R'
block|}
decl_stmt|;
comment|//
specifier|public
specifier|static
specifier|final
name|byte
name|DATA_ITEM_TYPE
init|=
literal|1
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|byte
name|REDO_ITEM_TYPE
init|=
literal|2
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|String
name|DEFAULT_DIRECTORY
init|=
literal|"data"
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|String
name|DEFAULT_FILE_PREFIX
init|=
literal|"data-"
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|int
name|DEFAULT_MAX_FILE_LENGTH
init|=
literal|1024
operator|*
literal|1024
operator|*
literal|32
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|Log
name|LOG
init|=
name|LogFactory
operator|.
name|getLog
argument_list|(
name|AsyncDataManager
operator|.
name|class
argument_list|)
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
init|=
operator|new
name|ConcurrentHashMap
argument_list|<
name|WriteKey
argument_list|,
name|WriteCommand
argument_list|>
argument_list|()
decl_stmt|;
name|File
name|directory
init|=
operator|new
name|File
argument_list|(
name|DEFAULT_DIRECTORY
argument_list|)
decl_stmt|;
name|String
name|filePrefix
init|=
name|DEFAULT_FILE_PREFIX
decl_stmt|;
name|ControlFile
name|controlFile
decl_stmt|;
name|boolean
name|started
decl_stmt|;
name|boolean
name|useNio
init|=
literal|true
decl_stmt|;
specifier|private
name|int
name|maxFileLength
init|=
name|DEFAULT_MAX_FILE_LENGTH
decl_stmt|;
specifier|private
name|int
name|preferedFileLength
init|=
name|DEFAULT_MAX_FILE_LENGTH
operator|-
literal|1024
operator|*
literal|512
decl_stmt|;
specifier|private
name|DataFileAppender
name|appender
decl_stmt|;
specifier|private
name|DataFileAccessorPool
name|accessorPool
init|=
operator|new
name|DataFileAccessorPool
argument_list|(
name|this
argument_list|)
decl_stmt|;
specifier|private
name|Map
argument_list|<
name|Integer
argument_list|,
name|DataFile
argument_list|>
name|fileMap
init|=
operator|new
name|HashMap
argument_list|<
name|Integer
argument_list|,
name|DataFile
argument_list|>
argument_list|()
decl_stmt|;
specifier|private
name|DataFile
name|currentWriteFile
decl_stmt|;
specifier|private
name|Location
name|mark
decl_stmt|;
specifier|private
specifier|final
name|AtomicReference
argument_list|<
name|Location
argument_list|>
name|lastAppendLocation
init|=
operator|new
name|AtomicReference
argument_list|<
name|Location
argument_list|>
argument_list|()
decl_stmt|;
specifier|private
name|Runnable
name|cleanupTask
decl_stmt|;
annotation|@
name|SuppressWarnings
argument_list|(
literal|"unchecked"
argument_list|)
specifier|public
specifier|synchronized
name|void
name|start
parameter_list|()
throws|throws
name|IOException
block|{
if|if
condition|(
name|started
condition|)
block|{
return|return;
block|}
name|started
operator|=
literal|true
expr_stmt|;
name|directory
operator|.
name|mkdirs
argument_list|()
expr_stmt|;
synchronized|synchronized
init|(
name|this
init|)
block|{
name|controlFile
operator|=
operator|new
name|ControlFile
argument_list|(
operator|new
name|File
argument_list|(
name|directory
argument_list|,
name|filePrefix
operator|+
literal|"control"
argument_list|)
argument_list|,
name|CONTROL_RECORD_MAX_LENGTH
argument_list|)
expr_stmt|;
name|controlFile
operator|.
name|lock
argument_list|()
expr_stmt|;
block|}
name|ByteSequence
name|sequence
init|=
name|controlFile
operator|.
name|load
argument_list|()
decl_stmt|;
if|if
condition|(
name|sequence
operator|!=
literal|null
operator|&&
name|sequence
operator|.
name|getLength
argument_list|()
operator|>
literal|0
condition|)
block|{
name|unmarshallState
argument_list|(
name|sequence
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|useNio
condition|)
block|{
name|appender
operator|=
operator|new
name|NIODataFileAppender
argument_list|(
name|this
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|appender
operator|=
operator|new
name|DataFileAppender
argument_list|(
name|this
argument_list|)
expr_stmt|;
block|}
name|File
index|[]
name|files
init|=
name|directory
operator|.
name|listFiles
argument_list|(
operator|new
name|FilenameFilter
argument_list|()
block|{
specifier|public
name|boolean
name|accept
parameter_list|(
name|File
name|dir
parameter_list|,
name|String
name|n
parameter_list|)
block|{
return|return
name|dir
operator|.
name|equals
argument_list|(
name|directory
argument_list|)
operator|&&
name|n
operator|.
name|startsWith
argument_list|(
name|filePrefix
argument_list|)
return|;
block|}
block|}
argument_list|)
decl_stmt|;
if|if
condition|(
name|files
operator|!=
literal|null
condition|)
block|{
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
try|try
block|{
name|File
name|file
init|=
name|files
index|[
name|i
index|]
decl_stmt|;
name|String
name|n
init|=
name|file
operator|.
name|getName
argument_list|()
decl_stmt|;
name|String
name|numStr
init|=
name|n
operator|.
name|substring
argument_list|(
name|filePrefix
operator|.
name|length
argument_list|()
argument_list|,
name|n
operator|.
name|length
argument_list|()
argument_list|)
decl_stmt|;
name|int
name|num
init|=
name|Integer
operator|.
name|parseInt
argument_list|(
name|numStr
argument_list|)
decl_stmt|;
name|DataFile
name|dataFile
init|=
operator|new
name|DataFile
argument_list|(
name|file
argument_list|,
name|num
argument_list|,
name|preferedFileLength
argument_list|)
decl_stmt|;
name|fileMap
operator|.
name|put
argument_list|(
name|dataFile
operator|.
name|getDataFileId
argument_list|()
argument_list|,
name|dataFile
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|NumberFormatException
name|e
parameter_list|)
block|{
comment|// Ignore file that do not match the pattern.
block|}
block|}
comment|// Sort the list so that we can link the DataFiles together in the
comment|// right order.
name|List
argument_list|<
name|DataFile
argument_list|>
name|l
init|=
operator|new
name|ArrayList
argument_list|<
name|DataFile
argument_list|>
argument_list|(
name|fileMap
operator|.
name|values
argument_list|()
argument_list|)
decl_stmt|;
name|Collections
operator|.
name|sort
argument_list|(
name|l
argument_list|)
expr_stmt|;
name|currentWriteFile
operator|=
literal|null
expr_stmt|;
for|for
control|(
name|DataFile
name|df
range|:
name|l
control|)
block|{
if|if
condition|(
name|currentWriteFile
operator|!=
literal|null
condition|)
block|{
name|currentWriteFile
operator|.
name|linkAfter
argument_list|(
name|df
argument_list|)
expr_stmt|;
block|}
name|currentWriteFile
operator|=
name|df
expr_stmt|;
block|}
block|}
comment|// Need to check the current Write File to see if there was a partial
comment|// write to it.
if|if
condition|(
name|currentWriteFile
operator|!=
literal|null
condition|)
block|{
comment|// See if the lastSyncedLocation is valid..
name|Location
name|l
init|=
name|lastAppendLocation
operator|.
name|get
argument_list|()
decl_stmt|;
if|if
condition|(
name|l
operator|!=
literal|null
operator|&&
name|l
operator|.
name|getDataFileId
argument_list|()
operator|!=
name|currentWriteFile
operator|.
name|getDataFileId
argument_list|()
operator|.
name|intValue
argument_list|()
condition|)
block|{
name|l
operator|=
literal|null
expr_stmt|;
block|}
comment|// If we know the last location that was ok.. then we can skip lots
comment|// of checking
name|l
operator|=
name|recoveryCheck
argument_list|(
name|currentWriteFile
argument_list|,
name|l
argument_list|)
expr_stmt|;
name|lastAppendLocation
operator|.
name|set
argument_list|(
name|l
argument_list|)
expr_stmt|;
block|}
name|storeState
argument_list|(
literal|false
argument_list|)
expr_stmt|;
name|cleanupTask
operator|=
operator|new
name|Runnable
argument_list|()
block|{
specifier|public
name|void
name|run
parameter_list|()
block|{
name|cleanup
argument_list|()
expr_stmt|;
block|}
block|}
expr_stmt|;
name|Scheduler
operator|.
name|executePeriodically
argument_list|(
name|cleanupTask
argument_list|,
literal|1000
operator|*
literal|30
argument_list|)
expr_stmt|;
block|}
specifier|private
name|Location
name|recoveryCheck
parameter_list|(
name|DataFile
name|dataFile
parameter_list|,
name|Location
name|location
parameter_list|)
throws|throws
name|IOException
block|{
if|if
condition|(
name|location
operator|==
literal|null
condition|)
block|{
name|location
operator|=
operator|new
name|Location
argument_list|()
expr_stmt|;
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
name|location
operator|.
name|setOffset
argument_list|(
literal|0
argument_list|)
expr_stmt|;
block|}
name|DataFileAccessor
name|reader
init|=
name|accessorPool
operator|.
name|openDataFileAccessor
argument_list|(
name|dataFile
argument_list|)
decl_stmt|;
try|try
block|{
name|reader
operator|.
name|readLocationDetails
argument_list|(
name|location
argument_list|)
expr_stmt|;
while|while
condition|(
name|reader
operator|.
name|readLocationDetailsAndValidate
argument_list|(
name|location
argument_list|)
condition|)
block|{
name|location
operator|.
name|setOffset
argument_list|(
name|location
operator|.
name|getOffset
argument_list|()
operator|+
name|location
operator|.
name|getSize
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
finally|finally
block|{
name|accessorPool
operator|.
name|closeDataFileAccessor
argument_list|(
name|reader
argument_list|)
expr_stmt|;
block|}
name|dataFile
operator|.
name|setLength
argument_list|(
name|location
operator|.
name|getOffset
argument_list|()
argument_list|)
expr_stmt|;
return|return
name|location
return|;
block|}
specifier|private
name|void
name|unmarshallState
parameter_list|(
name|ByteSequence
name|sequence
parameter_list|)
throws|throws
name|IOException
block|{
name|ByteArrayInputStream
name|bais
init|=
operator|new
name|ByteArrayInputStream
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
decl_stmt|;
name|DataInputStream
name|dis
init|=
operator|new
name|DataInputStream
argument_list|(
name|bais
argument_list|)
decl_stmt|;
if|if
condition|(
name|dis
operator|.
name|readBoolean
argument_list|()
condition|)
block|{
name|mark
operator|=
operator|new
name|Location
argument_list|()
expr_stmt|;
name|mark
operator|.
name|readExternal
argument_list|(
name|dis
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|mark
operator|=
literal|null
expr_stmt|;
block|}
if|if
condition|(
name|dis
operator|.
name|readBoolean
argument_list|()
condition|)
block|{
name|Location
name|l
init|=
operator|new
name|Location
argument_list|()
decl_stmt|;
name|l
operator|.
name|readExternal
argument_list|(
name|dis
argument_list|)
expr_stmt|;
name|lastAppendLocation
operator|.
name|set
argument_list|(
name|l
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|lastAppendLocation
operator|.
name|set
argument_list|(
literal|null
argument_list|)
expr_stmt|;
block|}
block|}
specifier|private
specifier|synchronized
name|ByteSequence
name|marshallState
parameter_list|()
throws|throws
name|IOException
block|{
name|ByteArrayOutputStream
name|baos
init|=
operator|new
name|ByteArrayOutputStream
argument_list|()
decl_stmt|;
name|DataOutputStream
name|dos
init|=
operator|new
name|DataOutputStream
argument_list|(
name|baos
argument_list|)
decl_stmt|;
if|if
condition|(
name|mark
operator|!=
literal|null
condition|)
block|{
name|dos
operator|.
name|writeBoolean
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|mark
operator|.
name|writeExternal
argument_list|(
name|dos
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|dos
operator|.
name|writeBoolean
argument_list|(
literal|false
argument_list|)
expr_stmt|;
block|}
name|Location
name|l
init|=
name|lastAppendLocation
operator|.
name|get
argument_list|()
decl_stmt|;
if|if
condition|(
name|l
operator|!=
literal|null
condition|)
block|{
name|dos
operator|.
name|writeBoolean
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|l
operator|.
name|writeExternal
argument_list|(
name|dos
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|dos
operator|.
name|writeBoolean
argument_list|(
literal|false
argument_list|)
expr_stmt|;
block|}
name|byte
index|[]
name|bs
init|=
name|baos
operator|.
name|toByteArray
argument_list|()
decl_stmt|;
return|return
operator|new
name|ByteSequence
argument_list|(
name|bs
argument_list|,
literal|0
argument_list|,
name|bs
operator|.
name|length
argument_list|)
return|;
block|}
specifier|synchronized
name|DataFile
name|allocateLocation
parameter_list|(
name|Location
name|location
parameter_list|)
throws|throws
name|IOException
block|{
if|if
condition|(
name|currentWriteFile
operator|==
literal|null
operator|||
operator|(
operator|(
name|currentWriteFile
operator|.
name|getLength
argument_list|()
operator|+
name|location
operator|.
name|getSize
argument_list|()
operator|)
operator|>
name|maxFileLength
operator|)
condition|)
block|{
name|int
name|nextNum
init|=
name|currentWriteFile
operator|!=
literal|null
condition|?
name|currentWriteFile
operator|.
name|getDataFileId
argument_list|()
operator|.
name|intValue
argument_list|()
operator|+
literal|1
else|:
literal|1
decl_stmt|;
name|String
name|fileName
init|=
name|filePrefix
operator|+
name|nextNum
decl_stmt|;
name|DataFile
name|nextWriteFile
init|=
operator|new
name|DataFile
argument_list|(
operator|new
name|File
argument_list|(
name|directory
argument_list|,
name|fileName
argument_list|)
argument_list|,
name|nextNum
argument_list|,
name|preferedFileLength
argument_list|)
decl_stmt|;
name|fileMap
operator|.
name|put
argument_list|(
name|nextWriteFile
operator|.
name|getDataFileId
argument_list|()
argument_list|,
name|nextWriteFile
argument_list|)
expr_stmt|;
if|if
condition|(
name|currentWriteFile
operator|!=
literal|null
condition|)
block|{
name|currentWriteFile
operator|.
name|linkAfter
argument_list|(
name|nextWriteFile
argument_list|)
expr_stmt|;
if|if
condition|(
name|currentWriteFile
operator|.
name|isUnused
argument_list|()
condition|)
block|{
name|removeDataFile
argument_list|(
name|currentWriteFile
argument_list|)
expr_stmt|;
block|}
block|}
name|currentWriteFile
operator|=
name|nextWriteFile
expr_stmt|;
block|}
name|location
operator|.
name|setOffset
argument_list|(
name|currentWriteFile
operator|.
name|getLength
argument_list|()
argument_list|)
expr_stmt|;
name|location
operator|.
name|setDataFileId
argument_list|(
name|currentWriteFile
operator|.
name|getDataFileId
argument_list|()
operator|.
name|intValue
argument_list|()
argument_list|)
expr_stmt|;
name|currentWriteFile
operator|.
name|incrementLength
argument_list|(
name|location
operator|.
name|getSize
argument_list|()
argument_list|)
expr_stmt|;
name|currentWriteFile
operator|.
name|increment
argument_list|()
expr_stmt|;
return|return
name|currentWriteFile
return|;
block|}
name|DataFile
name|getDataFile
parameter_list|(
name|Location
name|item
parameter_list|)
throws|throws
name|IOException
block|{
name|Integer
name|key
init|=
name|Integer
operator|.
name|valueOf
argument_list|(
name|item
operator|.
name|getDataFileId
argument_list|()
argument_list|)
decl_stmt|;
name|DataFile
name|dataFile
init|=
name|fileMap
operator|.
name|get
argument_list|(
name|key
argument_list|)
decl_stmt|;
if|if
condition|(
name|dataFile
operator|==
literal|null
condition|)
block|{
name|LOG
operator|.
name|error
argument_list|(
literal|"Looking for key "
operator|+
name|key
operator|+
literal|" but not found in fileMap: "
operator|+
name|fileMap
argument_list|)
expr_stmt|;
throw|throw
operator|new
name|IOException
argument_list|(
literal|"Could not locate data file "
operator|+
name|filePrefix
operator|+
literal|"-"
operator|+
name|item
operator|.
name|getDataFileId
argument_list|()
argument_list|)
throw|;
block|}
return|return
name|dataFile
return|;
block|}
specifier|private
name|DataFile
name|getNextDataFile
parameter_list|(
name|DataFile
name|dataFile
parameter_list|)
block|{
return|return
operator|(
name|DataFile
operator|)
name|dataFile
operator|.
name|getNext
argument_list|()
return|;
block|}
specifier|public
specifier|synchronized
name|void
name|close
parameter_list|()
throws|throws
name|IOException
block|{
if|if
condition|(
operator|!
name|started
condition|)
block|{
return|return;
block|}
name|Scheduler
operator|.
name|cancel
argument_list|(
name|cleanupTask
argument_list|)
expr_stmt|;
name|accessorPool
operator|.
name|close
argument_list|()
expr_stmt|;
name|storeState
argument_list|(
literal|false
argument_list|)
expr_stmt|;
name|appender
operator|.
name|close
argument_list|()
expr_stmt|;
name|fileMap
operator|.
name|clear
argument_list|()
expr_stmt|;
name|controlFile
operator|.
name|unlock
argument_list|()
expr_stmt|;
name|controlFile
operator|.
name|dispose
argument_list|()
expr_stmt|;
name|started
operator|=
literal|false
expr_stmt|;
block|}
specifier|synchronized
name|void
name|cleanup
parameter_list|()
block|{
if|if
condition|(
name|accessorPool
operator|!=
literal|null
condition|)
block|{
name|accessorPool
operator|.
name|disposeUnused
argument_list|()
expr_stmt|;
block|}
block|}
specifier|public
specifier|synchronized
name|boolean
name|delete
parameter_list|()
throws|throws
name|IOException
block|{
comment|// Close all open file handles...
name|appender
operator|.
name|close
argument_list|()
expr_stmt|;
name|accessorPool
operator|.
name|close
argument_list|()
expr_stmt|;
name|boolean
name|result
init|=
literal|true
decl_stmt|;
for|for
control|(
name|Iterator
name|i
init|=
name|fileMap
operator|.
name|values
argument_list|()
operator|.
name|iterator
argument_list|()
init|;
name|i
operator|.
name|hasNext
argument_list|()
condition|;
control|)
block|{
name|DataFile
name|dataFile
init|=
operator|(
name|DataFile
operator|)
name|i
operator|.
name|next
argument_list|()
decl_stmt|;
name|result
operator|&=
name|dataFile
operator|.
name|delete
argument_list|()
expr_stmt|;
block|}
name|fileMap
operator|.
name|clear
argument_list|()
expr_stmt|;
name|lastAppendLocation
operator|.
name|set
argument_list|(
literal|null
argument_list|)
expr_stmt|;
name|mark
operator|=
literal|null
expr_stmt|;
name|currentWriteFile
operator|=
literal|null
expr_stmt|;
comment|// reopen open file handles...
name|accessorPool
operator|=
operator|new
name|DataFileAccessorPool
argument_list|(
name|this
argument_list|)
expr_stmt|;
if|if
condition|(
name|useNio
condition|)
block|{
name|appender
operator|=
operator|new
name|NIODataFileAppender
argument_list|(
name|this
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|appender
operator|=
operator|new
name|DataFileAppender
argument_list|(
name|this
argument_list|)
expr_stmt|;
block|}
return|return
name|result
return|;
block|}
specifier|public
specifier|synchronized
name|void
name|addInterestInFile
parameter_list|(
name|int
name|file
parameter_list|)
throws|throws
name|IOException
block|{
if|if
condition|(
name|file
operator|>=
literal|0
condition|)
block|{
name|Integer
name|key
init|=
name|Integer
operator|.
name|valueOf
argument_list|(
name|file
argument_list|)
decl_stmt|;
name|DataFile
name|dataFile
init|=
operator|(
name|DataFile
operator|)
name|fileMap
operator|.
name|get
argument_list|(
name|key
argument_list|)
decl_stmt|;
if|if
condition|(
name|dataFile
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|IOException
argument_list|(
literal|"That data file does not exist"
argument_list|)
throw|;
block|}
name|addInterestInFile
argument_list|(
name|dataFile
argument_list|)
expr_stmt|;
block|}
block|}
specifier|synchronized
name|void
name|addInterestInFile
parameter_list|(
name|DataFile
name|dataFile
parameter_list|)
block|{
if|if
condition|(
name|dataFile
operator|!=
literal|null
condition|)
block|{
name|dataFile
operator|.
name|increment
argument_list|()
expr_stmt|;
block|}
block|}
specifier|public
specifier|synchronized
name|void
name|removeInterestInFile
parameter_list|(
name|int
name|file
parameter_list|)
throws|throws
name|IOException
block|{
if|if
condition|(
name|file
operator|>=
literal|0
condition|)
block|{
name|Integer
name|key
init|=
name|Integer
operator|.
name|valueOf
argument_list|(
name|file
argument_list|)
decl_stmt|;
name|DataFile
name|dataFile
init|=
operator|(
name|DataFile
operator|)
name|fileMap
operator|.
name|get
argument_list|(
name|key
argument_list|)
decl_stmt|;
name|removeInterestInFile
argument_list|(
name|dataFile
argument_list|)
expr_stmt|;
block|}
block|}
specifier|synchronized
name|void
name|removeInterestInFile
parameter_list|(
name|DataFile
name|dataFile
parameter_list|)
throws|throws
name|IOException
block|{
if|if
condition|(
name|dataFile
operator|!=
literal|null
condition|)
block|{
if|if
condition|(
name|dataFile
operator|.
name|decrement
argument_list|()
operator|<=
literal|0
condition|)
block|{
name|removeDataFile
argument_list|(
name|dataFile
argument_list|)
expr_stmt|;
block|}
block|}
block|}
specifier|public
specifier|synchronized
name|void
name|consolidateDataFilesNotIn
parameter_list|(
name|Set
argument_list|<
name|Integer
argument_list|>
name|inUse
parameter_list|)
throws|throws
name|IOException
block|{
comment|// Substract and the difference is the set of files that are no longer
comment|// needed :)
name|Set
argument_list|<
name|Integer
argument_list|>
name|unUsed
init|=
operator|new
name|HashSet
argument_list|<
name|Integer
argument_list|>
argument_list|(
name|fileMap
operator|.
name|keySet
argument_list|()
argument_list|)
decl_stmt|;
name|unUsed
operator|.
name|removeAll
argument_list|(
name|inUse
argument_list|)
expr_stmt|;
name|List
argument_list|<
name|DataFile
argument_list|>
name|purgeList
init|=
operator|new
name|ArrayList
argument_list|<
name|DataFile
argument_list|>
argument_list|()
decl_stmt|;
for|for
control|(
name|Integer
name|key
range|:
name|unUsed
control|)
block|{
name|DataFile
name|dataFile
init|=
operator|(
name|DataFile
operator|)
name|fileMap
operator|.
name|get
argument_list|(
name|key
argument_list|)
decl_stmt|;
name|purgeList
operator|.
name|add
argument_list|(
name|dataFile
argument_list|)
expr_stmt|;
block|}
for|for
control|(
name|DataFile
name|dataFile
range|:
name|purgeList
control|)
block|{
name|removeDataFile
argument_list|(
name|dataFile
argument_list|)
expr_stmt|;
block|}
block|}
specifier|public
specifier|synchronized
name|void
name|consolidateDataFiles
parameter_list|()
throws|throws
name|IOException
block|{
name|List
argument_list|<
name|DataFile
argument_list|>
name|purgeList
init|=
operator|new
name|ArrayList
argument_list|<
name|DataFile
argument_list|>
argument_list|()
decl_stmt|;
for|for
control|(
name|DataFile
name|dataFile
range|:
name|fileMap
operator|.
name|values
argument_list|()
control|)
block|{
if|if
condition|(
name|dataFile
operator|.
name|isUnused
argument_list|()
condition|)
block|{
name|purgeList
operator|.
name|add
argument_list|(
name|dataFile
argument_list|)
expr_stmt|;
block|}
block|}
for|for
control|(
name|DataFile
name|dataFile
range|:
name|purgeList
control|)
block|{
name|removeDataFile
argument_list|(
name|dataFile
argument_list|)
expr_stmt|;
block|}
block|}
specifier|private
specifier|synchronized
name|void
name|removeDataFile
parameter_list|(
name|DataFile
name|dataFile
parameter_list|)
throws|throws
name|IOException
block|{
comment|// Make sure we don't delete too much data.
if|if
condition|(
name|dataFile
operator|==
name|currentWriteFile
operator|||
name|mark
operator|==
literal|null
operator|||
name|dataFile
operator|.
name|getDataFileId
argument_list|()
operator|>=
name|mark
operator|.
name|getDataFileId
argument_list|()
condition|)
block|{
return|return;
block|}
name|accessorPool
operator|.
name|disposeDataFileAccessors
argument_list|(
name|dataFile
argument_list|)
expr_stmt|;
name|fileMap
operator|.
name|remove
argument_list|(
name|dataFile
operator|.
name|getDataFileId
argument_list|()
argument_list|)
expr_stmt|;
name|dataFile
operator|.
name|unlink
argument_list|()
expr_stmt|;
name|boolean
name|result
init|=
name|dataFile
operator|.
name|delete
argument_list|()
decl_stmt|;
name|LOG
operator|.
name|debug
argument_list|(
literal|"discarding data file "
operator|+
name|dataFile
operator|+
operator|(
name|result
condition|?
literal|"successful "
else|:
literal|"failed"
operator|)
argument_list|)
expr_stmt|;
block|}
comment|/**      * @return the maxFileLength      */
specifier|public
name|int
name|getMaxFileLength
parameter_list|()
block|{
return|return
name|maxFileLength
return|;
block|}
comment|/**      * @param maxFileLength the maxFileLength to set      */
specifier|public
name|void
name|setMaxFileLength
parameter_list|(
name|int
name|maxFileLength
parameter_list|)
block|{
name|this
operator|.
name|maxFileLength
operator|=
name|maxFileLength
expr_stmt|;
block|}
specifier|public
name|String
name|toString
parameter_list|()
block|{
return|return
literal|"DataManager:("
operator|+
name|filePrefix
operator|+
literal|")"
return|;
block|}
specifier|public
specifier|synchronized
name|Location
name|getMark
parameter_list|()
throws|throws
name|IllegalStateException
block|{
return|return
name|mark
return|;
block|}
specifier|public
specifier|synchronized
name|Location
name|getNextLocation
parameter_list|(
name|Location
name|location
parameter_list|)
throws|throws
name|IOException
throws|,
name|IllegalStateException
block|{
name|Location
name|cur
init|=
literal|null
decl_stmt|;
while|while
condition|(
literal|true
condition|)
block|{
if|if
condition|(
name|cur
operator|==
literal|null
condition|)
block|{
if|if
condition|(
name|location
operator|==
literal|null
condition|)
block|{
name|DataFile
name|head
init|=
operator|(
name|DataFile
operator|)
name|currentWriteFile
operator|.
name|getHeadNode
argument_list|()
decl_stmt|;
name|cur
operator|=
operator|new
name|Location
argument_list|()
expr_stmt|;
name|cur
operator|.
name|setDataFileId
argument_list|(
name|head
operator|.
name|getDataFileId
argument_list|()
argument_list|)
expr_stmt|;
name|cur
operator|.
name|setOffset
argument_list|(
literal|0
argument_list|)
expr_stmt|;
comment|// DataFileAccessor reader =
comment|// accessorPool.openDataFileAccessor(head);
comment|// try {
comment|// if( !reader.readLocationDetailsAndValidate(cur) ) {
comment|// return null;
comment|// }
comment|// } finally {
comment|// accessorPool.closeDataFileAccessor(reader);
comment|// }
block|}
else|else
block|{
comment|// Set to the next offset..
name|cur
operator|=
operator|new
name|Location
argument_list|(
name|location
argument_list|)
expr_stmt|;
name|cur
operator|.
name|setOffset
argument_list|(
name|cur
operator|.
name|getOffset
argument_list|()
operator|+
name|cur
operator|.
name|getSize
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
else|else
block|{
name|cur
operator|.
name|setOffset
argument_list|(
name|cur
operator|.
name|getOffset
argument_list|()
operator|+
name|cur
operator|.
name|getSize
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|DataFile
name|dataFile
init|=
name|getDataFile
argument_list|(
name|cur
argument_list|)
decl_stmt|;
comment|// Did it go into the next file??
if|if
condition|(
name|dataFile
operator|.
name|getLength
argument_list|()
operator|<=
name|cur
operator|.
name|getOffset
argument_list|()
condition|)
block|{
name|dataFile
operator|=
name|getNextDataFile
argument_list|(
name|dataFile
argument_list|)
expr_stmt|;
if|if
condition|(
name|dataFile
operator|==
literal|null
condition|)
block|{
return|return
literal|null
return|;
block|}
else|else
block|{
name|cur
operator|.
name|setDataFileId
argument_list|(
name|dataFile
operator|.
name|getDataFileId
argument_list|()
operator|.
name|intValue
argument_list|()
argument_list|)
expr_stmt|;
name|cur
operator|.
name|setOffset
argument_list|(
literal|0
argument_list|)
expr_stmt|;
block|}
block|}
comment|// Load in location size and type.
name|DataFileAccessor
name|reader
init|=
name|accessorPool
operator|.
name|openDataFileAccessor
argument_list|(
name|dataFile
argument_list|)
decl_stmt|;
try|try
block|{
name|reader
operator|.
name|readLocationDetails
argument_list|(
name|cur
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|accessorPool
operator|.
name|closeDataFileAccessor
argument_list|(
name|reader
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|cur
operator|.
name|getType
argument_list|()
operator|==
literal|0
condition|)
block|{
return|return
literal|null
return|;
block|}
elseif|else
if|if
condition|(
name|cur
operator|.
name|getType
argument_list|()
operator|>
literal|0
condition|)
block|{
comment|// Only return user records.
return|return
name|cur
return|;
block|}
block|}
block|}
specifier|public
name|ByteSequence
name|read
parameter_list|(
name|Location
name|location
parameter_list|)
throws|throws
name|IOException
throws|,
name|IllegalStateException
block|{
name|DataFile
name|dataFile
init|=
name|getDataFile
argument_list|(
name|location
argument_list|)
decl_stmt|;
name|DataFileAccessor
name|reader
init|=
name|accessorPool
operator|.
name|openDataFileAccessor
argument_list|(
name|dataFile
argument_list|)
decl_stmt|;
name|ByteSequence
name|rc
init|=
literal|null
decl_stmt|;
try|try
block|{
name|rc
operator|=
name|reader
operator|.
name|readRecord
argument_list|(
name|location
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|accessorPool
operator|.
name|closeDataFileAccessor
argument_list|(
name|reader
argument_list|)
expr_stmt|;
block|}
return|return
name|rc
return|;
block|}
specifier|public
name|void
name|setMark
parameter_list|(
name|Location
name|location
parameter_list|,
name|boolean
name|sync
parameter_list|)
throws|throws
name|IOException
throws|,
name|IllegalStateException
block|{
synchronized|synchronized
init|(
name|this
init|)
block|{
name|mark
operator|=
name|location
expr_stmt|;
block|}
name|storeState
argument_list|(
name|sync
argument_list|)
expr_stmt|;
block|}
specifier|private
specifier|synchronized
name|void
name|storeState
parameter_list|(
name|boolean
name|sync
parameter_list|)
throws|throws
name|IOException
block|{
name|ByteSequence
name|state
init|=
name|marshallState
argument_list|()
decl_stmt|;
name|appender
operator|.
name|storeItem
argument_list|(
name|state
argument_list|,
name|Location
operator|.
name|MARK_TYPE
argument_list|,
name|sync
argument_list|)
expr_stmt|;
name|controlFile
operator|.
name|store
argument_list|(
name|state
argument_list|,
name|sync
argument_list|)
expr_stmt|;
block|}
specifier|public
specifier|synchronized
name|Location
name|write
parameter_list|(
name|ByteSequence
name|data
parameter_list|,
name|boolean
name|sync
parameter_list|)
throws|throws
name|IOException
throws|,
name|IllegalStateException
block|{
return|return
name|appender
operator|.
name|storeItem
argument_list|(
name|data
argument_list|,
name|Location
operator|.
name|USER_TYPE
argument_list|,
name|sync
argument_list|)
return|;
block|}
specifier|public
specifier|synchronized
name|Location
name|write
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
throws|,
name|IllegalStateException
block|{
return|return
name|appender
operator|.
name|storeItem
argument_list|(
name|data
argument_list|,
name|type
argument_list|,
name|sync
argument_list|)
return|;
block|}
specifier|public
name|void
name|update
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
throws|throws
name|IOException
block|{
name|DataFile
name|dataFile
init|=
name|getDataFile
argument_list|(
name|location
argument_list|)
decl_stmt|;
name|DataFileAccessor
name|updater
init|=
name|accessorPool
operator|.
name|openDataFileAccessor
argument_list|(
name|dataFile
argument_list|)
decl_stmt|;
try|try
block|{
name|updater
operator|.
name|updateRecord
argument_list|(
name|location
argument_list|,
name|data
argument_list|,
name|sync
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|accessorPool
operator|.
name|closeDataFileAccessor
argument_list|(
name|updater
argument_list|)
expr_stmt|;
block|}
block|}
specifier|public
name|File
name|getDirectory
parameter_list|()
block|{
return|return
name|directory
return|;
block|}
specifier|public
name|void
name|setDirectory
parameter_list|(
name|File
name|directory
parameter_list|)
block|{
name|this
operator|.
name|directory
operator|=
name|directory
expr_stmt|;
block|}
specifier|public
name|String
name|getFilePrefix
parameter_list|()
block|{
return|return
name|filePrefix
return|;
block|}
specifier|public
name|void
name|setFilePrefix
parameter_list|(
name|String
name|filePrefix
parameter_list|)
block|{
name|this
operator|.
name|filePrefix
operator|=
name|filePrefix
expr_stmt|;
block|}
specifier|public
name|Map
argument_list|<
name|WriteKey
argument_list|,
name|WriteCommand
argument_list|>
name|getInflightWrites
parameter_list|()
block|{
return|return
name|inflightWrites
return|;
block|}
specifier|public
name|Location
name|getLastAppendLocation
parameter_list|()
block|{
return|return
name|lastAppendLocation
operator|.
name|get
argument_list|()
return|;
block|}
specifier|public
name|void
name|setLastAppendLocation
parameter_list|(
name|Location
name|lastSyncedLocation
parameter_list|)
block|{
name|this
operator|.
name|lastAppendLocation
operator|.
name|set
argument_list|(
name|lastSyncedLocation
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

