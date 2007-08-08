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
name|data
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
name|HashMap
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
name|kaha
operator|.
name|impl
operator|.
name|DataManager
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
name|index
operator|.
name|RedoStoreIndexItem
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
name|DataManagerImpl
implements|implements
name|DataManager
block|{
specifier|private
specifier|static
specifier|final
name|Log
name|log
init|=
name|LogFactory
operator|.
name|getLog
argument_list|(
name|DataManagerImpl
operator|.
name|class
argument_list|)
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|long
name|MAX_FILE_LENGTH
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
name|String
name|NAME_PREFIX
init|=
literal|"data-"
decl_stmt|;
specifier|private
specifier|final
name|File
name|directory
decl_stmt|;
specifier|private
specifier|final
name|String
name|name
decl_stmt|;
specifier|private
name|SyncDataFileReader
name|reader
decl_stmt|;
specifier|private
name|SyncDataFileWriter
name|writer
decl_stmt|;
specifier|private
name|DataFile
name|currentWriteFile
decl_stmt|;
specifier|private
name|long
name|maxFileLength
init|=
name|MAX_FILE_LENGTH
decl_stmt|;
name|Map
name|fileMap
init|=
operator|new
name|HashMap
argument_list|()
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|int
name|ITEM_HEAD_SIZE
init|=
literal|5
decl_stmt|;
comment|// type + length
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
name|Marshaller
name|redoMarshaller
init|=
name|RedoStoreIndexItem
operator|.
name|MARSHALLER
decl_stmt|;
specifier|private
name|String
name|dataFilePrefix
decl_stmt|;
specifier|public
name|DataManagerImpl
parameter_list|(
name|File
name|dir
parameter_list|,
specifier|final
name|String
name|name
parameter_list|)
block|{
name|this
operator|.
name|directory
operator|=
name|dir
expr_stmt|;
name|this
operator|.
name|name
operator|=
name|name
expr_stmt|;
name|dataFilePrefix
operator|=
name|NAME_PREFIX
operator|+
name|name
operator|+
literal|"-"
expr_stmt|;
comment|// build up list of current dataFiles
name|File
index|[]
name|files
init|=
name|dir
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
name|dataFilePrefix
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
name|dataFilePrefix
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
argument_list|)
decl_stmt|;
name|fileMap
operator|.
name|put
argument_list|(
name|dataFile
operator|.
name|getNumber
argument_list|()
argument_list|,
name|dataFile
argument_list|)
expr_stmt|;
if|if
condition|(
name|currentWriteFile
operator|==
literal|null
operator|||
name|currentWriteFile
operator|.
name|getNumber
argument_list|()
operator|.
name|intValue
argument_list|()
operator|<
name|num
condition|)
block|{
name|currentWriteFile
operator|=
name|dataFile
expr_stmt|;
block|}
block|}
block|}
block|}
specifier|private
name|DataFile
name|createAndAddDataFile
parameter_list|(
name|int
name|num
parameter_list|)
block|{
name|String
name|fileName
init|=
name|dataFilePrefix
operator|+
name|num
decl_stmt|;
name|File
name|file
init|=
operator|new
name|File
argument_list|(
name|directory
argument_list|,
name|fileName
argument_list|)
decl_stmt|;
name|DataFile
name|result
init|=
operator|new
name|DataFile
argument_list|(
name|file
argument_list|,
name|num
argument_list|)
decl_stmt|;
name|fileMap
operator|.
name|put
argument_list|(
name|result
operator|.
name|getNumber
argument_list|()
argument_list|,
name|result
argument_list|)
expr_stmt|;
return|return
name|result
return|;
block|}
comment|/*      * (non-Javadoc)      *       * @see org.apache.activemq.kaha.impl.data.IDataManager#getName()      */
specifier|public
name|String
name|getName
parameter_list|()
block|{
return|return
name|name
return|;
block|}
specifier|synchronized
name|DataFile
name|findSpaceForData
parameter_list|(
name|DataItem
name|item
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
name|item
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
name|getNumber
argument_list|()
operator|.
name|intValue
argument_list|()
operator|+
literal|1
else|:
literal|1
decl_stmt|;
if|if
condition|(
name|currentWriteFile
operator|!=
literal|null
operator|&&
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
name|currentWriteFile
operator|=
name|createAndAddDataFile
argument_list|(
name|nextNum
argument_list|)
expr_stmt|;
block|}
name|item
operator|.
name|setOffset
argument_list|(
name|currentWriteFile
operator|.
name|getLength
argument_list|()
argument_list|)
expr_stmt|;
name|item
operator|.
name|setFile
argument_list|(
name|currentWriteFile
operator|.
name|getNumber
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
name|item
operator|.
name|getSize
argument_list|()
operator|+
name|ITEM_HEAD_SIZE
argument_list|)
expr_stmt|;
return|return
name|currentWriteFile
return|;
block|}
name|DataFile
name|getDataFile
parameter_list|(
name|StoreLocation
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
name|getFile
argument_list|()
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
name|log
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
name|NAME_PREFIX
operator|+
name|name
operator|+
literal|"-"
operator|+
name|item
operator|.
name|getFile
argument_list|()
argument_list|)
throw|;
block|}
return|return
name|dataFile
return|;
block|}
comment|/*      * (non-Javadoc)      *       * @see org.apache.activemq.kaha.impl.data.IDataManager#readItem(org.apache.activemq.kaha.Marshaller,      *      org.apache.activemq.kaha.StoreLocation)      */
specifier|public
specifier|synchronized
name|Object
name|readItem
parameter_list|(
name|Marshaller
name|marshaller
parameter_list|,
name|StoreLocation
name|item
parameter_list|)
throws|throws
name|IOException
block|{
return|return
name|getReader
argument_list|()
operator|.
name|readItem
argument_list|(
name|marshaller
argument_list|,
name|item
argument_list|)
return|;
block|}
comment|/*      * (non-Javadoc)      *       * @see org.apache.activemq.kaha.impl.data.IDataManager#storeDataItem(org.apache.activemq.kaha.Marshaller,      *      java.lang.Object)      */
specifier|public
specifier|synchronized
name|StoreLocation
name|storeDataItem
parameter_list|(
name|Marshaller
name|marshaller
parameter_list|,
name|Object
name|payload
parameter_list|)
throws|throws
name|IOException
block|{
return|return
name|getWriter
argument_list|()
operator|.
name|storeItem
argument_list|(
name|marshaller
argument_list|,
name|payload
argument_list|,
name|DATA_ITEM_TYPE
argument_list|)
return|;
block|}
comment|/*      * (non-Javadoc)      *       * @see org.apache.activemq.kaha.impl.data.IDataManager#storeRedoItem(java.lang.Object)      */
specifier|public
specifier|synchronized
name|StoreLocation
name|storeRedoItem
parameter_list|(
name|Object
name|payload
parameter_list|)
throws|throws
name|IOException
block|{
return|return
name|getWriter
argument_list|()
operator|.
name|storeItem
argument_list|(
name|redoMarshaller
argument_list|,
name|payload
argument_list|,
name|REDO_ITEM_TYPE
argument_list|)
return|;
block|}
comment|/*      * (non-Javadoc)      *       * @see org.apache.activemq.kaha.impl.data.IDataManager#updateItem(org.apache.activemq.kaha.StoreLocation,      *      org.apache.activemq.kaha.Marshaller, java.lang.Object)      */
specifier|public
specifier|synchronized
name|void
name|updateItem
parameter_list|(
name|StoreLocation
name|location
parameter_list|,
name|Marshaller
name|marshaller
parameter_list|,
name|Object
name|payload
parameter_list|)
throws|throws
name|IOException
block|{
name|getWriter
argument_list|()
operator|.
name|updateItem
argument_list|(
operator|(
name|DataItem
operator|)
name|location
argument_list|,
name|marshaller
argument_list|,
name|payload
argument_list|,
name|DATA_ITEM_TYPE
argument_list|)
expr_stmt|;
block|}
comment|/*      * (non-Javadoc)      *       * @see org.apache.activemq.kaha.impl.data.IDataManager#recoverRedoItems(org.apache.activemq.kaha.impl.data.RedoListener)      */
specifier|public
specifier|synchronized
name|void
name|recoverRedoItems
parameter_list|(
name|RedoListener
name|listener
parameter_list|)
throws|throws
name|IOException
block|{
comment|// Nothing to recover if there is no current file.
if|if
condition|(
name|currentWriteFile
operator|==
literal|null
condition|)
return|return;
name|DataItem
name|item
init|=
operator|new
name|DataItem
argument_list|()
decl_stmt|;
name|item
operator|.
name|setFile
argument_list|(
name|currentWriteFile
operator|.
name|getNumber
argument_list|()
operator|.
name|intValue
argument_list|()
argument_list|)
expr_stmt|;
name|item
operator|.
name|setOffset
argument_list|(
literal|0
argument_list|)
expr_stmt|;
while|while
condition|(
literal|true
condition|)
block|{
name|byte
name|type
decl_stmt|;
try|try
block|{
name|type
operator|=
name|getReader
argument_list|()
operator|.
name|readDataItemSize
argument_list|(
name|item
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|ignore
parameter_list|)
block|{
name|log
operator|.
name|trace
argument_list|(
literal|"End of data file reached at (header was invalid): "
operator|+
name|item
argument_list|)
expr_stmt|;
return|return;
block|}
if|if
condition|(
name|type
operator|==
name|REDO_ITEM_TYPE
condition|)
block|{
comment|// Un-marshal the redo item
name|Object
name|object
decl_stmt|;
try|try
block|{
name|object
operator|=
name|readItem
argument_list|(
name|redoMarshaller
argument_list|,
name|item
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e1
parameter_list|)
block|{
name|log
operator|.
name|trace
argument_list|(
literal|"End of data file reached at (payload was invalid): "
operator|+
name|item
argument_list|)
expr_stmt|;
return|return;
block|}
try|try
block|{
name|listener
operator|.
name|onRedoItem
argument_list|(
name|item
argument_list|,
name|object
argument_list|)
expr_stmt|;
comment|// in case the listener is holding on to item references,
comment|// copy it
comment|// so we don't change it behind the listener's back.
name|item
operator|=
name|item
operator|.
name|copy
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
throw|throw
name|IOExceptionSupport
operator|.
name|create
argument_list|(
literal|"Recovery handler failed: "
operator|+
name|e
argument_list|,
name|e
argument_list|)
throw|;
block|}
block|}
comment|// Move to the next item.
name|item
operator|.
name|setOffset
argument_list|(
name|item
operator|.
name|getOffset
argument_list|()
operator|+
name|ITEM_HEAD_SIZE
operator|+
name|item
operator|.
name|getSize
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
comment|/*      * (non-Javadoc)      *       * @see org.apache.activemq.kaha.impl.data.IDataManager#close()      */
specifier|public
specifier|synchronized
name|void
name|close
parameter_list|()
throws|throws
name|IOException
block|{
name|getWriter
argument_list|()
operator|.
name|close
argument_list|()
expr_stmt|;
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
name|getWriter
argument_list|()
operator|.
name|force
argument_list|(
name|dataFile
argument_list|)
expr_stmt|;
name|dataFile
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
name|fileMap
operator|.
name|clear
argument_list|()
expr_stmt|;
block|}
comment|/*      * (non-Javadoc)      *       * @see org.apache.activemq.kaha.impl.data.IDataManager#force()      */
specifier|public
specifier|synchronized
name|void
name|force
parameter_list|()
throws|throws
name|IOException
block|{
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
name|getWriter
argument_list|()
operator|.
name|force
argument_list|(
name|dataFile
argument_list|)
expr_stmt|;
block|}
block|}
comment|/*      * (non-Javadoc)      *       * @see org.apache.activemq.kaha.impl.data.IDataManager#delete()      */
specifier|public
specifier|synchronized
name|boolean
name|delete
parameter_list|()
throws|throws
name|IOException
block|{
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
return|return
name|result
return|;
block|}
comment|/*      * (non-Javadoc)      *       * @see org.apache.activemq.kaha.impl.data.IDataManager#addInterestInFile(int)      */
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
name|dataFile
operator|=
name|createAndAddDataFile
argument_list|(
name|file
argument_list|)
expr_stmt|;
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
comment|/*      * (non-Javadoc)      *       * @see org.apache.activemq.kaha.impl.data.IDataManager#removeInterestInFile(int)      */
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
if|if
condition|(
name|dataFile
operator|!=
name|currentWriteFile
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
block|}
comment|/*      * (non-Javadoc)      *       * @see org.apache.activemq.kaha.impl.data.IDataManager#consolidateDataFiles()      */
specifier|public
specifier|synchronized
name|void
name|consolidateDataFiles
parameter_list|()
throws|throws
name|IOException
block|{
name|List
name|purgeList
init|=
operator|new
name|ArrayList
argument_list|()
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
if|if
condition|(
name|dataFile
operator|.
name|isUnused
argument_list|()
operator|&&
name|dataFile
operator|!=
name|currentWriteFile
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
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|purgeList
operator|.
name|size
argument_list|()
condition|;
name|i
operator|++
control|)
block|{
name|DataFile
name|dataFile
init|=
operator|(
name|DataFile
operator|)
name|purgeList
operator|.
name|get
argument_list|(
name|i
argument_list|)
decl_stmt|;
name|removeDataFile
argument_list|(
name|dataFile
argument_list|)
expr_stmt|;
block|}
block|}
specifier|private
name|void
name|removeDataFile
parameter_list|(
name|DataFile
name|dataFile
parameter_list|)
throws|throws
name|IOException
block|{
name|fileMap
operator|.
name|remove
argument_list|(
name|dataFile
operator|.
name|getNumber
argument_list|()
argument_list|)
expr_stmt|;
if|if
condition|(
name|writer
operator|!=
literal|null
condition|)
block|{
name|writer
operator|.
name|force
argument_list|(
name|dataFile
argument_list|)
expr_stmt|;
block|}
name|boolean
name|result
init|=
name|dataFile
operator|.
name|delete
argument_list|()
decl_stmt|;
name|log
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
comment|/*      * (non-Javadoc)      *       * @see org.apache.activemq.kaha.impl.data.IDataManager#getRedoMarshaller()      */
specifier|public
name|Marshaller
name|getRedoMarshaller
parameter_list|()
block|{
return|return
name|redoMarshaller
return|;
block|}
comment|/*      * (non-Javadoc)      *       * @see org.apache.activemq.kaha.impl.data.IDataManager#setRedoMarshaller(org.apache.activemq.kaha.Marshaller)      */
specifier|public
name|void
name|setRedoMarshaller
parameter_list|(
name|Marshaller
name|redoMarshaller
parameter_list|)
block|{
name|this
operator|.
name|redoMarshaller
operator|=
name|redoMarshaller
expr_stmt|;
block|}
comment|/**      * @return the maxFileLength      */
specifier|public
name|long
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
name|long
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
name|NAME_PREFIX
operator|+
name|name
operator|+
literal|")"
return|;
block|}
specifier|public
specifier|synchronized
name|SyncDataFileReader
name|getReader
parameter_list|()
block|{
if|if
condition|(
name|reader
operator|==
literal|null
condition|)
block|{
name|reader
operator|=
name|createReader
argument_list|()
expr_stmt|;
block|}
return|return
name|reader
return|;
block|}
specifier|protected
specifier|synchronized
name|SyncDataFileReader
name|createReader
parameter_list|()
block|{
return|return
operator|new
name|SyncDataFileReader
argument_list|(
name|this
argument_list|)
return|;
block|}
specifier|public
specifier|synchronized
name|void
name|setReader
parameter_list|(
name|SyncDataFileReader
name|reader
parameter_list|)
block|{
name|this
operator|.
name|reader
operator|=
name|reader
expr_stmt|;
block|}
specifier|public
specifier|synchronized
name|SyncDataFileWriter
name|getWriter
parameter_list|()
block|{
if|if
condition|(
name|writer
operator|==
literal|null
condition|)
block|{
name|writer
operator|=
name|createWriter
argument_list|()
expr_stmt|;
block|}
return|return
name|writer
return|;
block|}
specifier|private
name|SyncDataFileWriter
name|createWriter
parameter_list|()
block|{
return|return
operator|new
name|SyncDataFileWriter
argument_list|(
name|this
argument_list|)
return|;
block|}
specifier|public
specifier|synchronized
name|void
name|setWriter
parameter_list|(
name|SyncDataFileWriter
name|writer
parameter_list|)
block|{
name|this
operator|.
name|writer
operator|=
name|writer
expr_stmt|;
block|}
block|}
end_class

end_unit

