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
name|container
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
name|Collection
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
name|java
operator|.
name|util
operator|.
name|Map
operator|.
name|Entry
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
name|ContainerId
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
name|IndexMBean
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
name|data
operator|.
name|Item
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
name|Index
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
name|IndexItem
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
name|IndexLinkedList
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
name|IndexManager
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
name|VMIndex
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
name|hash
operator|.
name|HashIndex
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
name|IOHelper
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
comment|/**  * Implementation of a MapContainer  *   * @version $Revision: 1.2 $  */
end_comment

begin_class
specifier|public
specifier|final
class|class
name|MapContainerImpl
extends|extends
name|BaseContainerImpl
implements|implements
name|MapContainer
block|{
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
name|MapContainerImpl
operator|.
name|class
argument_list|)
decl_stmt|;
specifier|protected
name|Index
name|index
decl_stmt|;
specifier|protected
name|Marshaller
name|keyMarshaller
init|=
name|Store
operator|.
name|OBJECT_MARSHALLER
decl_stmt|;
specifier|protected
name|Marshaller
name|valueMarshaller
init|=
name|Store
operator|.
name|OBJECT_MARSHALLER
decl_stmt|;
specifier|protected
name|File
name|directory
decl_stmt|;
specifier|private
name|int
name|indexBinSize
init|=
name|HashIndex
operator|.
name|DEFAULT_BIN_SIZE
decl_stmt|;
specifier|private
name|int
name|indexKeySize
init|=
name|HashIndex
operator|.
name|DEFAULT_KEY_SIZE
decl_stmt|;
specifier|private
name|int
name|indexPageSize
init|=
name|HashIndex
operator|.
name|DEFAULT_PAGE_SIZE
decl_stmt|;
specifier|public
name|MapContainerImpl
parameter_list|(
name|File
name|directory
parameter_list|,
name|ContainerId
name|id
parameter_list|,
name|IndexItem
name|root
parameter_list|,
name|IndexManager
name|indexManager
parameter_list|,
name|DataManager
name|dataManager
parameter_list|,
name|boolean
name|persistentIndex
parameter_list|)
block|{
name|super
argument_list|(
name|id
argument_list|,
name|root
argument_list|,
name|indexManager
argument_list|,
name|dataManager
argument_list|,
name|persistentIndex
argument_list|)
expr_stmt|;
name|this
operator|.
name|directory
operator|=
name|directory
expr_stmt|;
block|}
specifier|public
specifier|synchronized
name|void
name|init
parameter_list|()
block|{
name|super
operator|.
name|init
argument_list|()
expr_stmt|;
if|if
condition|(
name|index
operator|==
literal|null
condition|)
block|{
if|if
condition|(
name|persistentIndex
condition|)
block|{
name|String
name|name
init|=
name|containerId
operator|.
name|getDataContainerName
argument_list|()
operator|+
literal|"_"
operator|+
name|containerId
operator|.
name|getKey
argument_list|()
decl_stmt|;
name|name
operator|=
name|IOHelper
operator|.
name|toFileSystemSafeName
argument_list|(
name|name
argument_list|)
expr_stmt|;
try|try
block|{
name|HashIndex
name|hashIndex
init|=
operator|new
name|HashIndex
argument_list|(
name|directory
argument_list|,
name|name
argument_list|,
name|indexManager
argument_list|)
decl_stmt|;
name|hashIndex
operator|.
name|setNumberOfBins
argument_list|(
name|getIndexBinSize
argument_list|()
argument_list|)
expr_stmt|;
name|hashIndex
operator|.
name|setKeySize
argument_list|(
name|getIndexKeySize
argument_list|()
argument_list|)
expr_stmt|;
name|hashIndex
operator|.
name|setPageSize
argument_list|(
name|getIndexPageSize
argument_list|()
argument_list|)
expr_stmt|;
name|this
operator|.
name|index
operator|=
name|hashIndex
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
name|LOG
operator|.
name|error
argument_list|(
literal|"Failed to create HashIndex"
argument_list|,
name|e
argument_list|)
expr_stmt|;
throw|throw
operator|new
name|RuntimeException
argument_list|(
name|e
argument_list|)
throw|;
block|}
block|}
else|else
block|{
name|this
operator|.
name|index
operator|=
operator|new
name|VMIndex
argument_list|(
name|indexManager
argument_list|)
expr_stmt|;
block|}
block|}
name|index
operator|.
name|setKeyMarshaller
argument_list|(
name|keyMarshaller
argument_list|)
expr_stmt|;
block|}
comment|/*      * (non-Javadoc)      *       * @see org.apache.activemq.kaha.MapContainer#load()      */
specifier|public
specifier|synchronized
name|void
name|load
parameter_list|()
block|{
name|checkClosed
argument_list|()
expr_stmt|;
if|if
condition|(
operator|!
name|loaded
condition|)
block|{
if|if
condition|(
operator|!
name|loaded
condition|)
block|{
name|loaded
operator|=
literal|true
expr_stmt|;
try|try
block|{
name|init
argument_list|()
expr_stmt|;
name|index
operator|.
name|load
argument_list|()
expr_stmt|;
name|long
name|nextItem
init|=
name|root
operator|.
name|getNextItem
argument_list|()
decl_stmt|;
while|while
condition|(
name|nextItem
operator|!=
name|Item
operator|.
name|POSITION_NOT_SET
condition|)
block|{
name|IndexItem
name|item
init|=
name|indexManager
operator|.
name|getIndex
argument_list|(
name|nextItem
argument_list|)
decl_stmt|;
name|StoreLocation
name|data
init|=
name|item
operator|.
name|getKeyDataItem
argument_list|()
decl_stmt|;
name|Object
name|key
init|=
name|dataManager
operator|.
name|readItem
argument_list|(
name|keyMarshaller
argument_list|,
name|data
argument_list|)
decl_stmt|;
if|if
condition|(
name|index
operator|.
name|isTransient
argument_list|()
condition|)
block|{
name|index
operator|.
name|store
argument_list|(
name|key
argument_list|,
name|item
argument_list|)
expr_stmt|;
block|}
name|indexList
operator|.
name|add
argument_list|(
name|item
argument_list|)
expr_stmt|;
name|nextItem
operator|=
name|item
operator|.
name|getNextItem
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
name|LOG
operator|.
name|error
argument_list|(
literal|"Failed to load container "
operator|+
name|getId
argument_list|()
argument_list|,
name|e
argument_list|)
expr_stmt|;
throw|throw
operator|new
name|RuntimeStoreException
argument_list|(
name|e
argument_list|)
throw|;
block|}
block|}
block|}
block|}
comment|/*      * (non-Javadoc)      *       * @see org.apache.activemq.kaha.MapContainer#unload()      */
specifier|public
specifier|synchronized
name|void
name|unload
parameter_list|()
block|{
name|checkClosed
argument_list|()
expr_stmt|;
if|if
condition|(
name|loaded
condition|)
block|{
name|loaded
operator|=
literal|false
expr_stmt|;
try|try
block|{
name|index
operator|.
name|unload
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
name|LOG
operator|.
name|warn
argument_list|(
literal|"Failed to unload the index"
argument_list|,
name|e
argument_list|)
expr_stmt|;
block|}
name|indexList
operator|.
name|clear
argument_list|()
expr_stmt|;
block|}
block|}
specifier|public
specifier|synchronized
name|void
name|setKeyMarshaller
parameter_list|(
name|Marshaller
name|keyMarshaller
parameter_list|)
block|{
name|checkClosed
argument_list|()
expr_stmt|;
name|this
operator|.
name|keyMarshaller
operator|=
name|keyMarshaller
expr_stmt|;
if|if
condition|(
name|index
operator|!=
literal|null
condition|)
block|{
name|index
operator|.
name|setKeyMarshaller
argument_list|(
name|keyMarshaller
argument_list|)
expr_stmt|;
block|}
block|}
specifier|public
specifier|synchronized
name|void
name|setValueMarshaller
parameter_list|(
name|Marshaller
name|valueMarshaller
parameter_list|)
block|{
name|checkClosed
argument_list|()
expr_stmt|;
name|this
operator|.
name|valueMarshaller
operator|=
name|valueMarshaller
expr_stmt|;
block|}
comment|/*      * (non-Javadoc)      *       * @see org.apache.activemq.kaha.MapContainer#size()      */
specifier|public
specifier|synchronized
name|int
name|size
parameter_list|()
block|{
name|load
argument_list|()
expr_stmt|;
return|return
name|indexList
operator|.
name|size
argument_list|()
return|;
block|}
comment|/*      * (non-Javadoc)      *       * @see org.apache.activemq.kaha.MapContainer#isEmpty()      */
specifier|public
specifier|synchronized
name|boolean
name|isEmpty
parameter_list|()
block|{
name|load
argument_list|()
expr_stmt|;
return|return
name|indexList
operator|.
name|isEmpty
argument_list|()
return|;
block|}
comment|/*      * (non-Javadoc)      *       * @see org.apache.activemq.kaha.MapContainer#containsKey(java.lang.Object)      */
specifier|public
specifier|synchronized
name|boolean
name|containsKey
parameter_list|(
name|Object
name|key
parameter_list|)
block|{
name|load
argument_list|()
expr_stmt|;
try|try
block|{
return|return
name|index
operator|.
name|containsKey
argument_list|(
name|key
argument_list|)
return|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
name|LOG
operator|.
name|error
argument_list|(
literal|"Failed trying to find key: "
operator|+
name|key
argument_list|,
name|e
argument_list|)
expr_stmt|;
throw|throw
operator|new
name|RuntimeException
argument_list|(
name|e
argument_list|)
throw|;
block|}
block|}
comment|/*      * (non-Javadoc)      *       * @see org.apache.activemq.kaha.MapContainer#get(java.lang.Object)      */
specifier|public
specifier|synchronized
name|Object
name|get
parameter_list|(
name|Object
name|key
parameter_list|)
block|{
name|load
argument_list|()
expr_stmt|;
name|Object
name|result
init|=
literal|null
decl_stmt|;
name|StoreEntry
name|item
init|=
literal|null
decl_stmt|;
try|try
block|{
name|item
operator|=
name|index
operator|.
name|get
argument_list|(
name|key
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
name|LOG
operator|.
name|error
argument_list|(
literal|"Failed trying to get key: "
operator|+
name|key
argument_list|,
name|e
argument_list|)
expr_stmt|;
throw|throw
operator|new
name|RuntimeException
argument_list|(
name|e
argument_list|)
throw|;
block|}
if|if
condition|(
name|item
operator|!=
literal|null
condition|)
block|{
name|result
operator|=
name|getValue
argument_list|(
name|item
argument_list|)
expr_stmt|;
block|}
return|return
name|result
return|;
block|}
comment|/**      * Get the StoreEntry associated with the key      *       * @param key      * @return the StoreEntry      */
specifier|public
specifier|synchronized
name|StoreEntry
name|getEntry
parameter_list|(
name|Object
name|key
parameter_list|)
block|{
name|load
argument_list|()
expr_stmt|;
name|StoreEntry
name|item
init|=
literal|null
decl_stmt|;
try|try
block|{
name|item
operator|=
name|index
operator|.
name|get
argument_list|(
name|key
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
name|LOG
operator|.
name|error
argument_list|(
literal|"Failed trying to get key: "
operator|+
name|key
argument_list|,
name|e
argument_list|)
expr_stmt|;
throw|throw
operator|new
name|RuntimeException
argument_list|(
name|e
argument_list|)
throw|;
block|}
return|return
name|item
return|;
block|}
comment|/*      * (non-Javadoc)      *       * @see org.apache.activemq.kaha.MapContainer#containsValue(java.lang.Object)      */
specifier|public
specifier|synchronized
name|boolean
name|containsValue
parameter_list|(
name|Object
name|o
parameter_list|)
block|{
name|load
argument_list|()
expr_stmt|;
name|boolean
name|result
init|=
literal|false
decl_stmt|;
if|if
condition|(
name|o
operator|!=
literal|null
condition|)
block|{
name|IndexItem
name|item
init|=
name|indexList
operator|.
name|getFirst
argument_list|()
decl_stmt|;
while|while
condition|(
name|item
operator|!=
literal|null
condition|)
block|{
name|Object
name|value
init|=
name|getValue
argument_list|(
name|item
argument_list|)
decl_stmt|;
if|if
condition|(
name|value
operator|!=
literal|null
operator|&&
name|value
operator|.
name|equals
argument_list|(
name|o
argument_list|)
condition|)
block|{
name|result
operator|=
literal|true
expr_stmt|;
break|break;
block|}
name|item
operator|=
name|indexList
operator|.
name|getNextEntry
argument_list|(
name|item
argument_list|)
expr_stmt|;
block|}
block|}
return|return
name|result
return|;
block|}
comment|/*      * (non-Javadoc)      *       * @see org.apache.activemq.kaha.MapContainer#putAll(java.util.Map)      */
specifier|public
specifier|synchronized
name|void
name|putAll
parameter_list|(
name|Map
name|t
parameter_list|)
block|{
name|load
argument_list|()
expr_stmt|;
if|if
condition|(
name|t
operator|!=
literal|null
condition|)
block|{
for|for
control|(
name|Iterator
name|i
init|=
name|t
operator|.
name|entrySet
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
name|Map
operator|.
name|Entry
name|entry
init|=
operator|(
name|Map
operator|.
name|Entry
operator|)
name|i
operator|.
name|next
argument_list|()
decl_stmt|;
name|put
argument_list|(
name|entry
operator|.
name|getKey
argument_list|()
argument_list|,
name|entry
operator|.
name|getValue
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
block|}
comment|/*      * (non-Javadoc)      *       * @see org.apache.activemq.kaha.MapContainer#keySet()      */
specifier|public
specifier|synchronized
name|Set
name|keySet
parameter_list|()
block|{
name|load
argument_list|()
expr_stmt|;
return|return
operator|new
name|ContainerKeySet
argument_list|(
name|this
argument_list|)
return|;
block|}
comment|/*      * (non-Javadoc)      *       * @see org.apache.activemq.kaha.MapContainer#values()      */
specifier|public
specifier|synchronized
name|Collection
name|values
parameter_list|()
block|{
name|load
argument_list|()
expr_stmt|;
return|return
operator|new
name|ContainerValueCollection
argument_list|(
name|this
argument_list|)
return|;
block|}
comment|/*      * (non-Javadoc)      *       * @see org.apache.activemq.kaha.MapContainer#entrySet()      */
specifier|public
specifier|synchronized
name|Set
name|entrySet
parameter_list|()
block|{
name|load
argument_list|()
expr_stmt|;
return|return
operator|new
name|ContainerEntrySet
argument_list|(
name|this
argument_list|)
return|;
block|}
comment|/*      * (non-Javadoc)      *       * @see org.apache.activemq.kaha.MapContainer#put(java.lang.Object,      *      java.lang.Object)      */
specifier|public
specifier|synchronized
name|Object
name|put
parameter_list|(
name|Object
name|key
parameter_list|,
name|Object
name|value
parameter_list|)
block|{
name|load
argument_list|()
expr_stmt|;
name|Object
name|result
init|=
name|remove
argument_list|(
name|key
argument_list|)
decl_stmt|;
name|IndexItem
name|item
init|=
name|write
argument_list|(
name|key
argument_list|,
name|value
argument_list|)
decl_stmt|;
try|try
block|{
name|index
operator|.
name|store
argument_list|(
name|key
argument_list|,
name|item
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
name|LOG
operator|.
name|error
argument_list|(
literal|"Failed trying to insert key: "
operator|+
name|key
argument_list|,
name|e
argument_list|)
expr_stmt|;
throw|throw
operator|new
name|RuntimeException
argument_list|(
name|e
argument_list|)
throw|;
block|}
name|indexList
operator|.
name|add
argument_list|(
name|item
argument_list|)
expr_stmt|;
return|return
name|result
return|;
block|}
comment|/*      * (non-Javadoc)      *       * @see org.apache.activemq.kaha.MapContainer#remove(java.lang.Object)      */
specifier|public
specifier|synchronized
name|Object
name|remove
parameter_list|(
name|Object
name|key
parameter_list|)
block|{
name|load
argument_list|()
expr_stmt|;
try|try
block|{
name|Object
name|result
init|=
literal|null
decl_stmt|;
name|IndexItem
name|item
init|=
operator|(
name|IndexItem
operator|)
name|index
operator|.
name|remove
argument_list|(
name|key
argument_list|)
decl_stmt|;
if|if
condition|(
name|item
operator|!=
literal|null
condition|)
block|{
comment|// refresh the index
name|item
operator|=
operator|(
name|IndexItem
operator|)
name|indexList
operator|.
name|refreshEntry
argument_list|(
name|item
argument_list|)
expr_stmt|;
name|result
operator|=
name|getValue
argument_list|(
name|item
argument_list|)
expr_stmt|;
name|IndexItem
name|prev
init|=
name|indexList
operator|.
name|getPrevEntry
argument_list|(
name|item
argument_list|)
decl_stmt|;
name|IndexItem
name|next
init|=
name|indexList
operator|.
name|getNextEntry
argument_list|(
name|item
argument_list|)
decl_stmt|;
name|indexList
operator|.
name|remove
argument_list|(
name|item
argument_list|)
expr_stmt|;
name|delete
argument_list|(
name|item
argument_list|,
name|prev
argument_list|,
name|next
argument_list|)
expr_stmt|;
block|}
return|return
name|result
return|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
name|LOG
operator|.
name|error
argument_list|(
literal|"Failed trying to remove key: "
operator|+
name|key
argument_list|,
name|e
argument_list|)
expr_stmt|;
throw|throw
operator|new
name|RuntimeException
argument_list|(
name|e
argument_list|)
throw|;
block|}
block|}
specifier|public
specifier|synchronized
name|boolean
name|removeValue
parameter_list|(
name|Object
name|o
parameter_list|)
block|{
name|load
argument_list|()
expr_stmt|;
name|boolean
name|result
init|=
literal|false
decl_stmt|;
if|if
condition|(
name|o
operator|!=
literal|null
condition|)
block|{
name|IndexItem
name|item
init|=
name|indexList
operator|.
name|getFirst
argument_list|()
decl_stmt|;
while|while
condition|(
name|item
operator|!=
literal|null
condition|)
block|{
name|Object
name|value
init|=
name|getValue
argument_list|(
name|item
argument_list|)
decl_stmt|;
if|if
condition|(
name|value
operator|!=
literal|null
operator|&&
name|value
operator|.
name|equals
argument_list|(
name|o
argument_list|)
condition|)
block|{
name|result
operator|=
literal|true
expr_stmt|;
comment|// find the key
name|Object
name|key
init|=
name|getKey
argument_list|(
name|item
argument_list|)
decl_stmt|;
if|if
condition|(
name|key
operator|!=
literal|null
condition|)
block|{
name|remove
argument_list|(
name|key
argument_list|)
expr_stmt|;
block|}
break|break;
block|}
name|item
operator|=
name|indexList
operator|.
name|getNextEntry
argument_list|(
name|item
argument_list|)
expr_stmt|;
block|}
block|}
return|return
name|result
return|;
block|}
specifier|protected
specifier|synchronized
name|void
name|remove
parameter_list|(
name|IndexItem
name|item
parameter_list|)
block|{
name|Object
name|key
init|=
name|getKey
argument_list|(
name|item
argument_list|)
decl_stmt|;
if|if
condition|(
name|key
operator|!=
literal|null
condition|)
block|{
name|remove
argument_list|(
name|key
argument_list|)
expr_stmt|;
block|}
block|}
comment|/*      * (non-Javadoc)      *       * @see org.apache.activemq.kaha.MapContainer#clear()      */
specifier|public
specifier|synchronized
name|void
name|clear
parameter_list|()
block|{
name|checkClosed
argument_list|()
expr_stmt|;
name|loaded
operator|=
literal|true
expr_stmt|;
name|init
argument_list|()
expr_stmt|;
if|if
condition|(
name|index
operator|!=
literal|null
condition|)
block|{
try|try
block|{
name|index
operator|.
name|clear
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
name|LOG
operator|.
name|error
argument_list|(
literal|"Failed trying clear index"
argument_list|,
name|e
argument_list|)
expr_stmt|;
throw|throw
operator|new
name|RuntimeException
argument_list|(
name|e
argument_list|)
throw|;
block|}
block|}
name|super
operator|.
name|clear
argument_list|()
expr_stmt|;
name|doClear
argument_list|()
expr_stmt|;
block|}
comment|/**      * Add an entry to the Store Map      *       * @param key      * @param value      * @return the StoreEntry associated with the entry      */
specifier|public
specifier|synchronized
name|StoreEntry
name|place
parameter_list|(
name|Object
name|key
parameter_list|,
name|Object
name|value
parameter_list|)
block|{
name|load
argument_list|()
expr_stmt|;
try|try
block|{
name|remove
argument_list|(
name|key
argument_list|)
expr_stmt|;
name|IndexItem
name|item
init|=
name|write
argument_list|(
name|key
argument_list|,
name|value
argument_list|)
decl_stmt|;
name|index
operator|.
name|store
argument_list|(
name|key
argument_list|,
name|item
argument_list|)
expr_stmt|;
name|indexList
operator|.
name|add
argument_list|(
name|item
argument_list|)
expr_stmt|;
return|return
name|item
return|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
name|LOG
operator|.
name|error
argument_list|(
literal|"Failed trying to place key: "
operator|+
name|key
argument_list|,
name|e
argument_list|)
expr_stmt|;
throw|throw
operator|new
name|RuntimeException
argument_list|(
name|e
argument_list|)
throw|;
block|}
block|}
comment|/**      * Remove an Entry from ther Map      *       * @param entry      * @throws IOException      */
specifier|public
specifier|synchronized
name|void
name|remove
parameter_list|(
name|StoreEntry
name|entry
parameter_list|)
block|{
name|load
argument_list|()
expr_stmt|;
name|IndexItem
name|item
init|=
operator|(
name|IndexItem
operator|)
name|entry
decl_stmt|;
if|if
condition|(
name|item
operator|!=
literal|null
condition|)
block|{
name|Object
name|key
init|=
name|getKey
argument_list|(
name|item
argument_list|)
decl_stmt|;
try|try
block|{
name|index
operator|.
name|remove
argument_list|(
name|key
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
name|LOG
operator|.
name|error
argument_list|(
literal|"Failed trying to remove entry: "
operator|+
name|entry
argument_list|,
name|e
argument_list|)
expr_stmt|;
throw|throw
operator|new
name|RuntimeException
argument_list|(
name|e
argument_list|)
throw|;
block|}
name|IndexItem
name|prev
init|=
name|indexList
operator|.
name|getPrevEntry
argument_list|(
name|item
argument_list|)
decl_stmt|;
name|IndexItem
name|next
init|=
name|indexList
operator|.
name|getNextEntry
argument_list|(
name|item
argument_list|)
decl_stmt|;
name|indexList
operator|.
name|remove
argument_list|(
name|item
argument_list|)
expr_stmt|;
name|delete
argument_list|(
name|item
argument_list|,
name|prev
argument_list|,
name|next
argument_list|)
expr_stmt|;
block|}
block|}
specifier|public
specifier|synchronized
name|StoreEntry
name|getFirst
parameter_list|()
block|{
name|load
argument_list|()
expr_stmt|;
return|return
name|indexList
operator|.
name|getFirst
argument_list|()
return|;
block|}
specifier|public
specifier|synchronized
name|StoreEntry
name|getLast
parameter_list|()
block|{
name|load
argument_list|()
expr_stmt|;
return|return
name|indexList
operator|.
name|getLast
argument_list|()
return|;
block|}
specifier|public
specifier|synchronized
name|StoreEntry
name|getNext
parameter_list|(
name|StoreEntry
name|entry
parameter_list|)
block|{
name|load
argument_list|()
expr_stmt|;
name|IndexItem
name|item
init|=
operator|(
name|IndexItem
operator|)
name|entry
decl_stmt|;
return|return
name|indexList
operator|.
name|getNextEntry
argument_list|(
name|item
argument_list|)
return|;
block|}
specifier|public
specifier|synchronized
name|StoreEntry
name|getPrevious
parameter_list|(
name|StoreEntry
name|entry
parameter_list|)
block|{
name|load
argument_list|()
expr_stmt|;
name|IndexItem
name|item
init|=
operator|(
name|IndexItem
operator|)
name|entry
decl_stmt|;
return|return
name|indexList
operator|.
name|getPrevEntry
argument_list|(
name|item
argument_list|)
return|;
block|}
specifier|public
specifier|synchronized
name|StoreEntry
name|refresh
parameter_list|(
name|StoreEntry
name|entry
parameter_list|)
block|{
name|load
argument_list|()
expr_stmt|;
return|return
name|indexList
operator|.
name|getEntry
argument_list|(
name|entry
argument_list|)
return|;
block|}
comment|/**      * Get the value from it's location      *       * @param item      * @return the value associated with the store entry      */
specifier|public
specifier|synchronized
name|Object
name|getValue
parameter_list|(
name|StoreEntry
name|item
parameter_list|)
block|{
name|load
argument_list|()
expr_stmt|;
name|Object
name|result
init|=
literal|null
decl_stmt|;
if|if
condition|(
name|item
operator|!=
literal|null
condition|)
block|{
try|try
block|{
comment|// ensure this value is up to date
comment|// item=indexList.getEntry(item);
name|StoreLocation
name|data
init|=
name|item
operator|.
name|getValueDataItem
argument_list|()
decl_stmt|;
name|result
operator|=
name|dataManager
operator|.
name|readItem
argument_list|(
name|valueMarshaller
argument_list|,
name|data
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
name|LOG
operator|.
name|error
argument_list|(
literal|"Failed to get value for "
operator|+
name|item
argument_list|,
name|e
argument_list|)
expr_stmt|;
throw|throw
operator|new
name|RuntimeStoreException
argument_list|(
name|e
argument_list|)
throw|;
block|}
block|}
return|return
name|result
return|;
block|}
comment|/**      * Get the Key object from it's location      *       * @param item      * @return the Key Object associated with the StoreEntry      */
specifier|public
specifier|synchronized
name|Object
name|getKey
parameter_list|(
name|StoreEntry
name|item
parameter_list|)
block|{
name|load
argument_list|()
expr_stmt|;
name|Object
name|result
init|=
literal|null
decl_stmt|;
if|if
condition|(
name|item
operator|!=
literal|null
condition|)
block|{
try|try
block|{
name|StoreLocation
name|data
init|=
name|item
operator|.
name|getKeyDataItem
argument_list|()
decl_stmt|;
name|result
operator|=
name|dataManager
operator|.
name|readItem
argument_list|(
name|keyMarshaller
argument_list|,
name|data
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
name|LOG
operator|.
name|error
argument_list|(
literal|"Failed to get key for "
operator|+
name|item
argument_list|,
name|e
argument_list|)
expr_stmt|;
throw|throw
operator|new
name|RuntimeStoreException
argument_list|(
name|e
argument_list|)
throw|;
block|}
block|}
return|return
name|result
return|;
block|}
specifier|protected
name|IndexLinkedList
name|getItemList
parameter_list|()
block|{
return|return
name|indexList
return|;
block|}
specifier|protected
specifier|synchronized
name|IndexItem
name|write
parameter_list|(
name|Object
name|key
parameter_list|,
name|Object
name|value
parameter_list|)
block|{
name|IndexItem
name|index
init|=
literal|null
decl_stmt|;
try|try
block|{
name|index
operator|=
name|indexManager
operator|.
name|createNewIndex
argument_list|()
expr_stmt|;
name|StoreLocation
name|data
init|=
name|dataManager
operator|.
name|storeDataItem
argument_list|(
name|keyMarshaller
argument_list|,
name|key
argument_list|)
decl_stmt|;
name|index
operator|.
name|setKeyData
argument_list|(
name|data
argument_list|)
expr_stmt|;
if|if
condition|(
name|value
operator|!=
literal|null
condition|)
block|{
name|data
operator|=
name|dataManager
operator|.
name|storeDataItem
argument_list|(
name|valueMarshaller
argument_list|,
name|value
argument_list|)
expr_stmt|;
name|index
operator|.
name|setValueData
argument_list|(
name|data
argument_list|)
expr_stmt|;
block|}
name|IndexItem
name|prev
init|=
name|indexList
operator|.
name|getLast
argument_list|()
decl_stmt|;
name|prev
operator|=
name|prev
operator|!=
literal|null
condition|?
name|prev
else|:
name|indexList
operator|.
name|getRoot
argument_list|()
expr_stmt|;
name|IndexItem
name|next
init|=
name|indexList
operator|.
name|getNextEntry
argument_list|(
name|prev
argument_list|)
decl_stmt|;
name|prev
operator|.
name|setNextItem
argument_list|(
name|index
operator|.
name|getOffset
argument_list|()
argument_list|)
expr_stmt|;
name|index
operator|.
name|setPreviousItem
argument_list|(
name|prev
operator|.
name|getOffset
argument_list|()
argument_list|)
expr_stmt|;
name|updateIndexes
argument_list|(
name|prev
argument_list|)
expr_stmt|;
if|if
condition|(
name|next
operator|!=
literal|null
condition|)
block|{
name|next
operator|.
name|setPreviousItem
argument_list|(
name|index
operator|.
name|getOffset
argument_list|()
argument_list|)
expr_stmt|;
name|index
operator|.
name|setNextItem
argument_list|(
name|next
operator|.
name|getOffset
argument_list|()
argument_list|)
expr_stmt|;
name|updateIndexes
argument_list|(
name|next
argument_list|)
expr_stmt|;
block|}
name|storeIndex
argument_list|(
name|index
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
name|LOG
operator|.
name|error
argument_list|(
literal|"Failed to write "
operator|+
name|key
operator|+
literal|" , "
operator|+
name|value
argument_list|,
name|e
argument_list|)
expr_stmt|;
throw|throw
operator|new
name|RuntimeStoreException
argument_list|(
name|e
argument_list|)
throw|;
block|}
return|return
name|index
return|;
block|}
specifier|public
name|int
name|getIndexBinSize
parameter_list|()
block|{
return|return
name|indexBinSize
return|;
block|}
specifier|public
name|void
name|setIndexBinSize
parameter_list|(
name|int
name|indexBinSize
parameter_list|)
block|{
name|this
operator|.
name|indexBinSize
operator|=
name|indexBinSize
expr_stmt|;
block|}
specifier|public
name|int
name|getIndexKeySize
parameter_list|()
block|{
return|return
name|indexKeySize
return|;
block|}
specifier|public
name|void
name|setIndexKeySize
parameter_list|(
name|int
name|indexKeySize
parameter_list|)
block|{
name|this
operator|.
name|indexKeySize
operator|=
name|indexKeySize
expr_stmt|;
block|}
specifier|public
name|int
name|getIndexPageSize
parameter_list|()
block|{
return|return
name|indexPageSize
return|;
block|}
specifier|public
name|void
name|setIndexPageSize
parameter_list|(
name|int
name|indexPageSize
parameter_list|)
block|{
name|this
operator|.
name|indexPageSize
operator|=
name|indexPageSize
expr_stmt|;
block|}
specifier|public
name|IndexMBean
name|getIndexMBean
parameter_list|()
block|{
return|return
operator|(
name|IndexMBean
operator|)
name|index
return|;
block|}
specifier|public
name|String
name|toString
parameter_list|()
block|{
name|load
argument_list|()
expr_stmt|;
name|StringBuffer
name|buf
init|=
operator|new
name|StringBuffer
argument_list|()
decl_stmt|;
name|buf
operator|.
name|append
argument_list|(
literal|"{"
argument_list|)
expr_stmt|;
name|Iterator
name|i
init|=
name|entrySet
argument_list|()
operator|.
name|iterator
argument_list|()
decl_stmt|;
name|boolean
name|hasNext
init|=
name|i
operator|.
name|hasNext
argument_list|()
decl_stmt|;
while|while
condition|(
name|hasNext
condition|)
block|{
name|Map
operator|.
name|Entry
name|e
init|=
operator|(
name|Entry
operator|)
name|i
operator|.
name|next
argument_list|()
decl_stmt|;
name|Object
name|key
init|=
name|e
operator|.
name|getKey
argument_list|()
decl_stmt|;
name|Object
name|value
init|=
name|e
operator|.
name|getValue
argument_list|()
decl_stmt|;
name|buf
operator|.
name|append
argument_list|(
name|key
argument_list|)
expr_stmt|;
name|buf
operator|.
name|append
argument_list|(
literal|"="
argument_list|)
expr_stmt|;
name|buf
operator|.
name|append
argument_list|(
name|value
argument_list|)
expr_stmt|;
name|hasNext
operator|=
name|i
operator|.
name|hasNext
argument_list|()
expr_stmt|;
if|if
condition|(
name|hasNext
condition|)
name|buf
operator|.
name|append
argument_list|(
literal|", "
argument_list|)
expr_stmt|;
block|}
name|buf
operator|.
name|append
argument_list|(
literal|"}"
argument_list|)
expr_stmt|;
return|return
name|buf
operator|.
name|toString
argument_list|()
return|;
block|}
block|}
end_class

end_unit

