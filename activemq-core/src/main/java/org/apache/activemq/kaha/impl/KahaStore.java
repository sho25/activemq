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
name|org
operator|.
name|apache
operator|.
name|activemq
operator|.
name|kaha
operator|.
name|ListContainer
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
name|MapContainer
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
name|RuntimeStoreException
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
name|Store
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
name|ConcurrentHashMap
import|;
end_import

begin_comment
comment|/**  * Optimized Store writer  *   * @version $Revision: 1.1.1.1 $  */
end_comment

begin_class
specifier|public
class|class
name|KahaStore
implements|implements
name|Store
block|{
specifier|private
specifier|static
specifier|final
name|String
name|DEFAULT_CONTAINER_NAME
init|=
literal|"kaha"
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
name|KahaStore
operator|.
name|class
argument_list|)
decl_stmt|;
specifier|private
name|File
name|directory
decl_stmt|;
specifier|private
name|IndexRootContainer
name|mapsContainer
decl_stmt|;
specifier|private
name|IndexRootContainer
name|listsContainer
decl_stmt|;
specifier|private
name|Map
name|lists
init|=
operator|new
name|ConcurrentHashMap
argument_list|()
decl_stmt|;
specifier|private
name|Map
name|maps
init|=
operator|new
name|ConcurrentHashMap
argument_list|()
decl_stmt|;
specifier|private
name|Map
name|dataManagers
init|=
operator|new
name|ConcurrentHashMap
argument_list|()
decl_stmt|;
specifier|private
name|Map
name|indexManagers
init|=
operator|new
name|ConcurrentHashMap
argument_list|()
decl_stmt|;
specifier|private
name|IndexManager
name|rootIndexManager
decl_stmt|;
comment|//contains all the root indexes
specifier|private
name|boolean
name|closed
init|=
literal|false
decl_stmt|;
specifier|private
name|String
name|name
decl_stmt|;
specifier|private
name|String
name|mode
decl_stmt|;
specifier|private
name|boolean
name|initialized
decl_stmt|;
specifier|private
name|boolean
name|logIndexChanges
init|=
literal|false
decl_stmt|;
specifier|public
name|KahaStore
parameter_list|(
name|String
name|name
parameter_list|,
name|String
name|mode
parameter_list|)
throws|throws
name|IOException
block|{
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
name|initialize
argument_list|()
expr_stmt|;
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
name|closed
condition|)
block|{
name|closed
operator|=
literal|true
expr_stmt|;
if|if
condition|(
name|initialized
condition|)
block|{
for|for
control|(
name|Iterator
name|iter
init|=
name|indexManagers
operator|.
name|values
argument_list|()
operator|.
name|iterator
argument_list|()
init|;
name|iter
operator|.
name|hasNext
argument_list|()
condition|;
control|)
block|{
name|IndexManager
name|im
init|=
operator|(
name|IndexManager
operator|)
name|iter
operator|.
name|next
argument_list|()
decl_stmt|;
name|im
operator|.
name|close
argument_list|()
expr_stmt|;
name|iter
operator|.
name|remove
argument_list|()
expr_stmt|;
block|}
for|for
control|(
name|Iterator
name|iter
init|=
name|dataManagers
operator|.
name|values
argument_list|()
operator|.
name|iterator
argument_list|()
init|;
name|iter
operator|.
name|hasNext
argument_list|()
condition|;
control|)
block|{
name|DataManager
name|dm
init|=
operator|(
name|DataManager
operator|)
name|iter
operator|.
name|next
argument_list|()
decl_stmt|;
name|dm
operator|.
name|close
argument_list|()
expr_stmt|;
name|iter
operator|.
name|remove
argument_list|()
expr_stmt|;
block|}
block|}
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
name|initialized
condition|)
block|{
for|for
control|(
name|Iterator
name|iter
init|=
name|indexManagers
operator|.
name|values
argument_list|()
operator|.
name|iterator
argument_list|()
init|;
name|iter
operator|.
name|hasNext
argument_list|()
condition|;
control|)
block|{
name|IndexManager
name|im
init|=
operator|(
name|IndexManager
operator|)
name|iter
operator|.
name|next
argument_list|()
decl_stmt|;
name|im
operator|.
name|force
argument_list|()
expr_stmt|;
block|}
for|for
control|(
name|Iterator
name|iter
init|=
name|dataManagers
operator|.
name|values
argument_list|()
operator|.
name|iterator
argument_list|()
init|;
name|iter
operator|.
name|hasNext
argument_list|()
condition|;
control|)
block|{
name|DataManager
name|dm
init|=
operator|(
name|DataManager
operator|)
name|iter
operator|.
name|next
argument_list|()
decl_stmt|;
name|dm
operator|.
name|force
argument_list|()
expr_stmt|;
block|}
block|}
block|}
specifier|public
specifier|synchronized
name|void
name|clear
parameter_list|()
throws|throws
name|IOException
block|{
name|initialize
argument_list|()
expr_stmt|;
for|for
control|(
name|Iterator
name|i
init|=
name|maps
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
name|BaseContainerImpl
name|container
init|=
operator|(
name|BaseContainerImpl
operator|)
name|i
operator|.
name|next
argument_list|()
decl_stmt|;
name|container
operator|.
name|clear
argument_list|()
expr_stmt|;
block|}
for|for
control|(
name|Iterator
name|i
init|=
name|lists
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
name|BaseContainerImpl
name|container
init|=
operator|(
name|BaseContainerImpl
operator|)
name|i
operator|.
name|next
argument_list|()
decl_stmt|;
name|container
operator|.
name|clear
argument_list|()
expr_stmt|;
block|}
name|lists
operator|.
name|clear
argument_list|()
expr_stmt|;
name|maps
operator|.
name|clear
argument_list|()
expr_stmt|;
block|}
specifier|public
specifier|synchronized
name|boolean
name|delete
parameter_list|()
throws|throws
name|IOException
block|{
name|initialize
argument_list|()
expr_stmt|;
name|clear
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
name|iter
init|=
name|indexManagers
operator|.
name|values
argument_list|()
operator|.
name|iterator
argument_list|()
init|;
name|iter
operator|.
name|hasNext
argument_list|()
condition|;
control|)
block|{
name|IndexManager
name|im
init|=
operator|(
name|IndexManager
operator|)
name|iter
operator|.
name|next
argument_list|()
decl_stmt|;
name|result
operator|&=
name|im
operator|.
name|delete
argument_list|()
expr_stmt|;
name|iter
operator|.
name|remove
argument_list|()
expr_stmt|;
block|}
for|for
control|(
name|Iterator
name|iter
init|=
name|dataManagers
operator|.
name|values
argument_list|()
operator|.
name|iterator
argument_list|()
init|;
name|iter
operator|.
name|hasNext
argument_list|()
condition|;
control|)
block|{
name|DataManager
name|dm
init|=
operator|(
name|DataManager
operator|)
name|iter
operator|.
name|next
argument_list|()
decl_stmt|;
name|result
operator|&=
name|dm
operator|.
name|delete
argument_list|()
expr_stmt|;
name|iter
operator|.
name|remove
argument_list|()
expr_stmt|;
block|}
comment|// now delete all the files - containers that don't use the standard DataManager
comment|// and IndexManager will not have initialized the files - so these will be left around
comment|// unless we do this
if|if
condition|(
name|directory
operator|!=
literal|null
operator|&&
name|directory
operator|.
name|isDirectory
argument_list|()
condition|)
block|{
name|File
index|[]
name|files
init|=
name|directory
operator|.
name|listFiles
argument_list|()
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
if|if
condition|(
operator|!
name|file
operator|.
name|isDirectory
argument_list|()
condition|)
block|{
name|result
operator|&=
name|file
operator|.
name|delete
argument_list|()
expr_stmt|;
block|}
block|}
block|}
block|}
name|initialized
operator|=
literal|false
expr_stmt|;
return|return
name|result
return|;
block|}
specifier|public
name|boolean
name|doesMapContainerExist
parameter_list|(
name|Object
name|id
parameter_list|)
throws|throws
name|IOException
block|{
name|initialize
argument_list|()
expr_stmt|;
return|return
name|maps
operator|.
name|containsKey
argument_list|(
name|id
argument_list|)
return|;
block|}
specifier|public
name|MapContainer
name|getMapContainer
parameter_list|(
name|Object
name|id
parameter_list|)
throws|throws
name|IOException
block|{
return|return
name|getMapContainer
argument_list|(
name|id
argument_list|,
name|DEFAULT_CONTAINER_NAME
argument_list|)
return|;
block|}
specifier|public
specifier|synchronized
name|MapContainer
name|getMapContainer
parameter_list|(
name|Object
name|id
parameter_list|,
name|String
name|dataContainerName
parameter_list|)
throws|throws
name|IOException
block|{
name|initialize
argument_list|()
expr_stmt|;
name|MapContainerImpl
name|result
init|=
operator|(
name|MapContainerImpl
operator|)
name|maps
operator|.
name|get
argument_list|(
name|id
argument_list|)
decl_stmt|;
if|if
condition|(
name|result
operator|==
literal|null
condition|)
block|{
name|DataManager
name|dm
init|=
name|getDataManager
argument_list|(
name|dataContainerName
argument_list|)
decl_stmt|;
name|IndexManager
name|im
init|=
name|getIndexManager
argument_list|(
name|dm
argument_list|,
name|dataContainerName
argument_list|)
decl_stmt|;
name|ContainerId
name|containerId
init|=
operator|new
name|ContainerId
argument_list|()
decl_stmt|;
name|containerId
operator|.
name|setKey
argument_list|(
name|id
argument_list|)
expr_stmt|;
name|containerId
operator|.
name|setDataContainerName
argument_list|(
name|dataContainerName
argument_list|)
expr_stmt|;
name|IndexItem
name|root
init|=
name|mapsContainer
operator|.
name|getRoot
argument_list|(
name|containerId
argument_list|)
decl_stmt|;
if|if
condition|(
name|root
operator|==
literal|null
condition|)
block|{
name|root
operator|=
name|mapsContainer
operator|.
name|addRoot
argument_list|(
name|containerId
argument_list|)
expr_stmt|;
block|}
name|result
operator|=
operator|new
name|MapContainerImpl
argument_list|(
name|containerId
argument_list|,
name|root
argument_list|,
name|rootIndexManager
argument_list|,
name|im
argument_list|,
name|dm
argument_list|)
expr_stmt|;
name|result
operator|.
name|expressDataInterest
argument_list|()
expr_stmt|;
name|maps
operator|.
name|put
argument_list|(
name|containerId
operator|.
name|getKey
argument_list|()
argument_list|,
name|result
argument_list|)
expr_stmt|;
block|}
return|return
name|result
return|;
block|}
specifier|public
name|void
name|deleteMapContainer
parameter_list|(
name|Object
name|id
parameter_list|)
throws|throws
name|IOException
block|{
name|initialize
argument_list|()
expr_stmt|;
name|MapContainerImpl
name|container
init|=
operator|(
name|MapContainerImpl
operator|)
name|maps
operator|.
name|remove
argument_list|(
name|id
argument_list|)
decl_stmt|;
if|if
condition|(
name|container
operator|!=
literal|null
condition|)
block|{
name|container
operator|.
name|clear
argument_list|()
expr_stmt|;
name|mapsContainer
operator|.
name|removeRoot
argument_list|(
name|container
operator|.
name|getContainerId
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
specifier|public
name|Set
name|getMapContainerIds
parameter_list|()
throws|throws
name|IOException
block|{
name|initialize
argument_list|()
expr_stmt|;
return|return
name|maps
operator|.
name|keySet
argument_list|()
return|;
block|}
specifier|public
name|boolean
name|doesListContainerExist
parameter_list|(
name|Object
name|id
parameter_list|)
throws|throws
name|IOException
block|{
name|initialize
argument_list|()
expr_stmt|;
return|return
name|lists
operator|.
name|containsKey
argument_list|(
name|id
argument_list|)
return|;
block|}
specifier|public
name|ListContainer
name|getListContainer
parameter_list|(
name|Object
name|id
parameter_list|)
throws|throws
name|IOException
block|{
return|return
name|getListContainer
argument_list|(
name|id
argument_list|,
name|DEFAULT_CONTAINER_NAME
argument_list|)
return|;
block|}
specifier|public
specifier|synchronized
name|ListContainer
name|getListContainer
parameter_list|(
name|Object
name|id
parameter_list|,
name|String
name|containerName
parameter_list|)
throws|throws
name|IOException
block|{
name|initialize
argument_list|()
expr_stmt|;
name|ListContainerImpl
name|result
init|=
operator|(
name|ListContainerImpl
operator|)
name|lists
operator|.
name|get
argument_list|(
name|id
argument_list|)
decl_stmt|;
if|if
condition|(
name|result
operator|==
literal|null
condition|)
block|{
name|DataManager
name|dm
init|=
name|getDataManager
argument_list|(
name|containerName
argument_list|)
decl_stmt|;
name|IndexManager
name|im
init|=
name|getIndexManager
argument_list|(
name|dm
argument_list|,
name|containerName
argument_list|)
decl_stmt|;
name|ContainerId
name|containerId
init|=
operator|new
name|ContainerId
argument_list|()
decl_stmt|;
name|containerId
operator|.
name|setKey
argument_list|(
name|id
argument_list|)
expr_stmt|;
name|containerId
operator|.
name|setDataContainerName
argument_list|(
name|containerName
argument_list|)
expr_stmt|;
name|IndexItem
name|root
init|=
name|listsContainer
operator|.
name|getRoot
argument_list|(
name|containerId
argument_list|)
decl_stmt|;
if|if
condition|(
name|root
operator|==
literal|null
condition|)
block|{
name|root
operator|=
name|listsContainer
operator|.
name|addRoot
argument_list|(
name|containerId
argument_list|)
expr_stmt|;
block|}
name|result
operator|=
operator|new
name|ListContainerImpl
argument_list|(
name|containerId
argument_list|,
name|root
argument_list|,
name|rootIndexManager
argument_list|,
name|im
argument_list|,
name|dm
argument_list|)
expr_stmt|;
name|result
operator|.
name|expressDataInterest
argument_list|()
expr_stmt|;
name|lists
operator|.
name|put
argument_list|(
name|containerId
operator|.
name|getKey
argument_list|()
argument_list|,
name|result
argument_list|)
expr_stmt|;
block|}
return|return
name|result
return|;
block|}
specifier|public
name|void
name|deleteListContainer
parameter_list|(
name|Object
name|id
parameter_list|)
throws|throws
name|IOException
block|{
name|initialize
argument_list|()
expr_stmt|;
name|ListContainerImpl
name|container
init|=
operator|(
name|ListContainerImpl
operator|)
name|lists
operator|.
name|remove
argument_list|(
name|id
argument_list|)
decl_stmt|;
if|if
condition|(
name|container
operator|!=
literal|null
condition|)
block|{
name|container
operator|.
name|clear
argument_list|()
expr_stmt|;
name|listsContainer
operator|.
name|removeRoot
argument_list|(
name|container
operator|.
name|getContainerId
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
specifier|public
name|Set
name|getListContainerIds
parameter_list|()
throws|throws
name|IOException
block|{
name|initialize
argument_list|()
expr_stmt|;
return|return
name|lists
operator|.
name|keySet
argument_list|()
return|;
block|}
specifier|protected
name|void
name|checkClosed
parameter_list|()
block|{
if|if
condition|(
name|closed
condition|)
block|{
throw|throw
operator|new
name|RuntimeStoreException
argument_list|(
literal|"The store is closed"
argument_list|)
throw|;
block|}
block|}
specifier|protected
specifier|synchronized
name|void
name|initialize
parameter_list|()
throws|throws
name|IOException
block|{
if|if
condition|(
name|closed
condition|)
throw|throw
operator|new
name|IOException
argument_list|(
literal|"Store has been closed."
argument_list|)
throw|;
if|if
condition|(
operator|!
name|initialized
condition|)
block|{
name|initialized
operator|=
literal|true
expr_stmt|;
name|directory
operator|=
operator|new
name|File
argument_list|(
name|name
argument_list|)
expr_stmt|;
name|directory
operator|.
name|mkdirs
argument_list|()
expr_stmt|;
name|log
operator|.
name|info
argument_list|(
literal|"Kaha Store using data directory "
operator|+
name|directory
argument_list|)
expr_stmt|;
name|DataManager
name|defaultDM
init|=
name|getDataManager
argument_list|(
name|DEFAULT_CONTAINER_NAME
argument_list|)
decl_stmt|;
name|rootIndexManager
operator|=
name|getIndexManager
argument_list|(
name|defaultDM
argument_list|,
name|DEFAULT_CONTAINER_NAME
argument_list|)
expr_stmt|;
name|IndexItem
name|mapRoot
init|=
operator|new
name|IndexItem
argument_list|()
decl_stmt|;
name|IndexItem
name|listRoot
init|=
operator|new
name|IndexItem
argument_list|()
decl_stmt|;
if|if
condition|(
name|rootIndexManager
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
name|mapRoot
operator|.
name|setOffset
argument_list|(
literal|0
argument_list|)
expr_stmt|;
name|rootIndexManager
operator|.
name|updateIndex
argument_list|(
name|mapRoot
argument_list|)
expr_stmt|;
name|listRoot
operator|.
name|setOffset
argument_list|(
name|IndexItem
operator|.
name|INDEX_SIZE
argument_list|)
expr_stmt|;
name|rootIndexManager
operator|.
name|updateIndex
argument_list|(
name|listRoot
argument_list|)
expr_stmt|;
name|rootIndexManager
operator|.
name|setLength
argument_list|(
name|IndexItem
operator|.
name|INDEX_SIZE
operator|*
literal|2
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|mapRoot
operator|=
name|rootIndexManager
operator|.
name|getIndex
argument_list|(
literal|0
argument_list|)
expr_stmt|;
name|listRoot
operator|=
name|rootIndexManager
operator|.
name|getIndex
argument_list|(
name|IndexItem
operator|.
name|INDEX_SIZE
argument_list|)
expr_stmt|;
block|}
name|mapsContainer
operator|=
operator|new
name|IndexRootContainer
argument_list|(
name|mapRoot
argument_list|,
name|rootIndexManager
argument_list|,
name|defaultDM
argument_list|)
expr_stmt|;
name|listsContainer
operator|=
operator|new
name|IndexRootContainer
argument_list|(
name|listRoot
argument_list|,
name|rootIndexManager
argument_list|,
name|defaultDM
argument_list|)
expr_stmt|;
for|for
control|(
name|Iterator
name|i
init|=
name|dataManagers
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
name|DataManager
name|dm
init|=
operator|(
name|DataManager
operator|)
name|i
operator|.
name|next
argument_list|()
decl_stmt|;
name|dm
operator|.
name|consolidateDataFiles
argument_list|()
expr_stmt|;
block|}
block|}
block|}
specifier|protected
name|DataManager
name|getDataManager
parameter_list|(
name|String
name|name
parameter_list|)
throws|throws
name|IOException
block|{
name|DataManager
name|dm
init|=
operator|(
name|DataManager
operator|)
name|dataManagers
operator|.
name|get
argument_list|(
name|name
argument_list|)
decl_stmt|;
if|if
condition|(
name|dm
operator|==
literal|null
condition|)
block|{
name|dm
operator|=
operator|new
name|DataManager
argument_list|(
name|directory
argument_list|,
name|name
argument_list|)
expr_stmt|;
name|recover
argument_list|(
name|dm
argument_list|)
expr_stmt|;
name|dataManagers
operator|.
name|put
argument_list|(
name|name
argument_list|,
name|dm
argument_list|)
expr_stmt|;
block|}
return|return
name|dm
return|;
block|}
specifier|protected
name|IndexManager
name|getIndexManager
parameter_list|(
name|DataManager
name|dm
parameter_list|,
name|String
name|name
parameter_list|)
throws|throws
name|IOException
block|{
name|IndexManager
name|im
init|=
operator|(
name|IndexManager
operator|)
name|indexManagers
operator|.
name|get
argument_list|(
name|name
argument_list|)
decl_stmt|;
if|if
condition|(
name|im
operator|==
literal|null
condition|)
block|{
name|im
operator|=
operator|new
name|IndexManager
argument_list|(
name|directory
argument_list|,
name|name
argument_list|,
name|mode
argument_list|,
name|logIndexChanges
condition|?
name|dm
else|:
literal|null
argument_list|)
expr_stmt|;
name|indexManagers
operator|.
name|put
argument_list|(
name|name
argument_list|,
name|im
argument_list|)
expr_stmt|;
block|}
return|return
name|im
return|;
block|}
specifier|private
name|void
name|recover
parameter_list|(
specifier|final
name|DataManager
name|dm
parameter_list|)
throws|throws
name|IOException
block|{
name|dm
operator|.
name|recoverRedoItems
argument_list|(
operator|new
name|RedoListener
argument_list|()
block|{
specifier|public
name|void
name|onRedoItem
parameter_list|(
name|DataItem
name|item
parameter_list|,
name|Object
name|o
parameter_list|)
throws|throws
name|Exception
block|{
name|RedoStoreIndexItem
name|redo
init|=
operator|(
name|RedoStoreIndexItem
operator|)
name|o
decl_stmt|;
comment|//IndexManager im = getIndexManager(dm, redo.getIndexName());
name|IndexManager
name|im
init|=
name|getIndexManager
argument_list|(
name|dm
argument_list|,
name|dm
operator|.
name|getName
argument_list|()
argument_list|)
decl_stmt|;
name|im
operator|.
name|redo
argument_list|(
name|redo
argument_list|)
expr_stmt|;
block|}
block|}
argument_list|)
expr_stmt|;
block|}
specifier|public
name|boolean
name|isLogIndexChanges
parameter_list|()
block|{
return|return
name|logIndexChanges
return|;
block|}
specifier|public
name|void
name|setLogIndexChanges
parameter_list|(
name|boolean
name|logIndexChanges
parameter_list|)
block|{
name|this
operator|.
name|logIndexChanges
operator|=
name|logIndexChanges
expr_stmt|;
block|}
block|}
end_class

end_unit

