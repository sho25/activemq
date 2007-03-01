begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  *   * Licensed to the Apache Software Foundation (ASF) under one or more contributor license agreements. See the NOTICE  * file distributed with this work for additional information regarding copyright ownership. The ASF licenses this file  * to You under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the  * License. You may obtain a copy of the License at  *   * http://www.apache.org/licenses/LICENSE-2.0  *   * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on  * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the  * specific language governing permissions and limitations under the License.  */
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
name|index
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
name|FileNotFoundException
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
name|util
operator|.
name|LinkedList
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
name|StoreEntry
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
comment|/**  * Optimized Store reader  *   * @version $Revision: 1.1.1.1 $  */
end_comment

begin_class
specifier|public
specifier|final
class|class
name|IndexManager
block|{
specifier|public
specifier|static
specifier|final
name|String
name|NAME_PREFIX
init|=
literal|"index-"
decl_stmt|;
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
name|IndexManager
operator|.
name|class
argument_list|)
decl_stmt|;
specifier|private
specifier|final
name|String
name|name
decl_stmt|;
specifier|private
name|File
name|directory
decl_stmt|;
specifier|private
name|File
name|file
decl_stmt|;
specifier|private
name|RandomAccessFile
name|indexFile
decl_stmt|;
specifier|private
name|StoreIndexReader
name|reader
decl_stmt|;
specifier|private
name|StoreIndexWriter
name|writer
decl_stmt|;
specifier|private
name|DataManager
name|redoLog
decl_stmt|;
specifier|private
name|String
name|mode
decl_stmt|;
specifier|private
name|long
name|length
init|=
literal|0
decl_stmt|;
specifier|private
name|IndexItem
name|firstFree
decl_stmt|;
specifier|private
name|IndexItem
name|lastFree
decl_stmt|;
specifier|private
name|boolean
name|dirty
decl_stmt|;
specifier|public
name|IndexManager
parameter_list|(
name|File
name|directory
parameter_list|,
name|String
name|name
parameter_list|,
name|String
name|mode
parameter_list|,
name|DataManager
name|redoLog
parameter_list|)
throws|throws
name|IOException
block|{
name|this
operator|.
name|directory
operator|=
name|directory
expr_stmt|;
name|this
operator|.
name|name
operator|=
name|name
expr_stmt|;
name|this
operator|.
name|mode
operator|=
name|mode
expr_stmt|;
name|this
operator|.
name|redoLog
operator|=
name|redoLog
expr_stmt|;
name|initialize
argument_list|()
expr_stmt|;
block|}
specifier|public
specifier|synchronized
name|boolean
name|isEmpty
parameter_list|()
block|{
return|return
name|lastFree
operator|==
literal|null
operator|&&
name|length
operator|==
literal|0
return|;
block|}
specifier|public
specifier|synchronized
name|IndexItem
name|getIndex
parameter_list|(
name|long
name|offset
parameter_list|)
throws|throws
name|IOException
block|{
return|return
name|reader
operator|.
name|readItem
argument_list|(
name|offset
argument_list|)
return|;
block|}
specifier|public
specifier|synchronized
name|IndexItem
name|refreshIndex
parameter_list|(
name|IndexItem
name|item
parameter_list|)
throws|throws
name|IOException
block|{
name|reader
operator|.
name|updateIndexes
argument_list|(
name|item
argument_list|)
expr_stmt|;
return|return
name|item
return|;
block|}
specifier|public
specifier|synchronized
name|void
name|freeIndex
parameter_list|(
name|IndexItem
name|item
parameter_list|)
throws|throws
name|IOException
block|{
name|item
operator|.
name|reset
argument_list|()
expr_stmt|;
name|item
operator|.
name|setActive
argument_list|(
literal|false
argument_list|)
expr_stmt|;
if|if
condition|(
name|lastFree
operator|==
literal|null
condition|)
block|{
name|firstFree
operator|=
name|lastFree
operator|=
name|item
expr_stmt|;
block|}
else|else
block|{
name|lastFree
operator|.
name|setNextItem
argument_list|(
name|item
operator|.
name|getOffset
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|writer
operator|.
name|updateIndexes
argument_list|(
name|item
argument_list|)
expr_stmt|;
name|dirty
operator|=
literal|true
expr_stmt|;
block|}
specifier|public
specifier|synchronized
name|void
name|storeIndex
parameter_list|(
name|IndexItem
name|index
parameter_list|)
throws|throws
name|IOException
block|{
name|writer
operator|.
name|storeItem
argument_list|(
name|index
argument_list|)
expr_stmt|;
name|dirty
operator|=
literal|true
expr_stmt|;
block|}
specifier|public
specifier|synchronized
name|void
name|updateIndexes
parameter_list|(
name|IndexItem
name|index
parameter_list|)
throws|throws
name|IOException
block|{
try|try
block|{
name|writer
operator|.
name|updateIndexes
argument_list|(
name|index
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Throwable
name|e
parameter_list|)
block|{
name|log
operator|.
name|error
argument_list|(
name|name
operator|+
literal|" error updating indexes "
argument_list|,
name|e
argument_list|)
expr_stmt|;
block|}
name|dirty
operator|=
literal|true
expr_stmt|;
block|}
specifier|public
specifier|synchronized
name|void
name|redo
parameter_list|(
specifier|final
name|RedoStoreIndexItem
name|redo
parameter_list|)
throws|throws
name|IOException
block|{
name|writer
operator|.
name|redoStoreItem
argument_list|(
name|redo
argument_list|)
expr_stmt|;
name|dirty
operator|=
literal|true
expr_stmt|;
block|}
specifier|public
specifier|synchronized
name|IndexItem
name|createNewIndex
parameter_list|()
throws|throws
name|IOException
block|{
name|IndexItem
name|result
init|=
name|getNextFreeIndex
argument_list|()
decl_stmt|;
if|if
condition|(
name|result
operator|==
literal|null
condition|)
block|{
comment|// allocate one
name|result
operator|=
operator|new
name|IndexItem
argument_list|()
expr_stmt|;
name|result
operator|.
name|setOffset
argument_list|(
name|length
argument_list|)
expr_stmt|;
name|length
operator|+=
name|IndexItem
operator|.
name|INDEX_SIZE
expr_stmt|;
block|}
return|return
name|result
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
name|indexFile
operator|!=
literal|null
condition|)
block|{
name|indexFile
operator|.
name|close
argument_list|()
expr_stmt|;
name|indexFile
operator|=
literal|null
expr_stmt|;
block|}
block|}
specifier|public
specifier|synchronized
name|void
name|force
parameter_list|()
throws|throws
name|IOException
block|{
if|if
condition|(
name|indexFile
operator|!=
literal|null
operator|&&
name|dirty
condition|)
block|{
name|indexFile
operator|.
name|getFD
argument_list|()
operator|.
name|sync
argument_list|()
expr_stmt|;
name|dirty
operator|=
literal|false
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
name|firstFree
operator|=
name|lastFree
operator|=
literal|null
expr_stmt|;
if|if
condition|(
name|indexFile
operator|!=
literal|null
condition|)
block|{
name|indexFile
operator|.
name|close
argument_list|()
expr_stmt|;
name|indexFile
operator|=
literal|null
expr_stmt|;
block|}
return|return
name|file
operator|.
name|delete
argument_list|()
return|;
block|}
specifier|private
specifier|synchronized
name|IndexItem
name|getNextFreeIndex
parameter_list|()
throws|throws
name|IOException
block|{
name|IndexItem
name|result
init|=
literal|null
decl_stmt|;
if|if
condition|(
name|firstFree
operator|!=
literal|null
condition|)
block|{
if|if
condition|(
name|firstFree
operator|.
name|equals
argument_list|(
name|lastFree
argument_list|)
condition|)
block|{
name|result
operator|=
name|firstFree
expr_stmt|;
name|firstFree
operator|=
name|lastFree
operator|=
literal|null
expr_stmt|;
block|}
else|else
block|{
name|result
operator|=
name|firstFree
expr_stmt|;
name|firstFree
operator|=
name|getIndex
argument_list|(
name|firstFree
operator|.
name|getNextItem
argument_list|()
argument_list|)
expr_stmt|;
if|if
condition|(
name|firstFree
operator|==
literal|null
condition|)
block|{
name|lastFree
operator|=
literal|null
expr_stmt|;
block|}
block|}
name|result
operator|.
name|reset
argument_list|()
expr_stmt|;
block|}
return|return
name|result
return|;
block|}
name|long
name|getLength
parameter_list|()
block|{
return|return
name|length
return|;
block|}
specifier|public
specifier|synchronized
name|void
name|setLength
parameter_list|(
name|long
name|value
parameter_list|)
block|{
name|this
operator|.
name|length
operator|=
name|value
expr_stmt|;
block|}
specifier|public
specifier|synchronized
name|FileLock
name|getLock
parameter_list|()
throws|throws
name|IOException
block|{
return|return
name|indexFile
operator|.
name|getChannel
argument_list|()
operator|.
name|tryLock
argument_list|()
return|;
block|}
specifier|public
name|String
name|toString
parameter_list|()
block|{
return|return
literal|"IndexManager:("
operator|+
name|NAME_PREFIX
operator|+
name|name
operator|+
literal|")"
return|;
block|}
specifier|protected
name|void
name|initialize
parameter_list|()
throws|throws
name|IOException
block|{
name|file
operator|=
operator|new
name|File
argument_list|(
name|directory
argument_list|,
name|NAME_PREFIX
operator|+
name|name
argument_list|)
expr_stmt|;
name|indexFile
operator|=
operator|new
name|RandomAccessFile
argument_list|(
name|file
argument_list|,
name|mode
argument_list|)
expr_stmt|;
name|reader
operator|=
operator|new
name|StoreIndexReader
argument_list|(
name|indexFile
argument_list|)
expr_stmt|;
name|writer
operator|=
operator|new
name|StoreIndexWriter
argument_list|(
name|indexFile
argument_list|,
name|name
argument_list|,
name|redoLog
argument_list|)
expr_stmt|;
name|long
name|offset
init|=
literal|0
decl_stmt|;
while|while
condition|(
operator|(
name|offset
operator|+
name|IndexItem
operator|.
name|INDEX_SIZE
operator|)
operator|<=
name|indexFile
operator|.
name|length
argument_list|()
condition|)
block|{
name|IndexItem
name|index
init|=
name|reader
operator|.
name|readItem
argument_list|(
name|offset
argument_list|)
decl_stmt|;
if|if
condition|(
operator|!
name|index
operator|.
name|isActive
argument_list|()
condition|)
block|{
name|index
operator|.
name|reset
argument_list|()
expr_stmt|;
if|if
condition|(
name|lastFree
operator|!=
literal|null
condition|)
block|{
name|lastFree
operator|.
name|setNextItem
argument_list|(
name|index
operator|.
name|getOffset
argument_list|()
argument_list|)
expr_stmt|;
name|updateIndexes
argument_list|(
name|lastFree
argument_list|)
expr_stmt|;
name|lastFree
operator|=
name|index
expr_stmt|;
block|}
else|else
block|{
name|lastFree
operator|=
name|firstFree
operator|=
name|index
expr_stmt|;
block|}
block|}
name|offset
operator|+=
name|IndexItem
operator|.
name|INDEX_SIZE
expr_stmt|;
block|}
name|length
operator|=
name|offset
expr_stmt|;
block|}
block|}
end_class

end_unit

