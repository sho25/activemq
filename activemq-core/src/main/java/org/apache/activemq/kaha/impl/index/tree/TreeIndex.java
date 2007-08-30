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
name|index
operator|.
name|tree
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
name|atomic
operator|.
name|AtomicBoolean
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
name|util
operator|.
name|DataByteArrayInputStream
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
name|IOHelper
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
name|LRUCache
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
comment|/**  * BTree implementation  *   * @version $Revision: 1.1.1.1 $  */
end_comment

begin_class
specifier|public
class|class
name|TreeIndex
implements|implements
name|Index
block|{
specifier|private
specifier|static
specifier|final
name|String
name|NAME_PREFIX
init|=
literal|"tree-index-"
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|int
name|DEFAULT_PAGE_SIZE
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|int
name|DEFAULT_KEY_SIZE
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
name|TreeIndex
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
name|IndexManager
name|indexManager
decl_stmt|;
specifier|private
name|int
name|pageSize
init|=
name|DEFAULT_PAGE_SIZE
decl_stmt|;
specifier|private
name|int
name|keySize
init|=
name|DEFAULT_KEY_SIZE
decl_stmt|;
specifier|private
name|int
name|keysPerPage
init|=
name|pageSize
operator|/
name|keySize
decl_stmt|;
specifier|private
name|TreePage
name|root
decl_stmt|;
specifier|private
name|LRUCache
argument_list|<
name|Long
argument_list|,
name|TreePage
argument_list|>
name|pageCache
decl_stmt|;
specifier|private
name|DataByteArrayInputStream
name|dataIn
decl_stmt|;
specifier|private
name|DataByteArrayOutputStream
name|dataOut
decl_stmt|;
specifier|private
name|byte
index|[]
name|readBuffer
decl_stmt|;
specifier|private
name|Marshaller
name|keyMarshaller
decl_stmt|;
specifier|private
name|long
name|length
decl_stmt|;
specifier|private
name|TreePage
name|firstFree
decl_stmt|;
specifier|private
name|TreePage
name|lastFree
decl_stmt|;
specifier|private
name|AtomicBoolean
name|loaded
init|=
operator|new
name|AtomicBoolean
argument_list|()
decl_stmt|;
specifier|private
name|boolean
name|enablePageCaching
init|=
literal|true
decl_stmt|;
specifier|private
name|int
name|pageCacheSize
init|=
literal|10
decl_stmt|;
comment|/**      * Constructor      *       * @param directory      * @param name      * @param indexManager      * @throws IOException      */
specifier|public
name|TreeIndex
parameter_list|(
name|File
name|directory
parameter_list|,
name|String
name|name
parameter_list|,
name|IndexManager
name|indexManager
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
name|indexManager
operator|=
name|indexManager
expr_stmt|;
name|pageCache
operator|=
operator|new
name|LRUCache
argument_list|<
name|Long
argument_list|,
name|TreePage
argument_list|>
argument_list|(
name|pageCacheSize
argument_list|,
name|pageCacheSize
argument_list|,
literal|0.75f
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|openIndexFile
argument_list|()
expr_stmt|;
block|}
comment|/**      * Set the marshaller for key objects      *       * @param marshaller      */
specifier|public
name|void
name|setKeyMarshaller
parameter_list|(
name|Marshaller
name|marshaller
parameter_list|)
block|{
if|if
condition|(
name|loaded
operator|.
name|get
argument_list|()
condition|)
block|{
throw|throw
operator|new
name|RuntimeException
argument_list|(
literal|"Pages already loaded - can't set marshaller now"
argument_list|)
throw|;
block|}
name|this
operator|.
name|keyMarshaller
operator|=
name|marshaller
expr_stmt|;
block|}
comment|/**      * @return the keySize      */
specifier|public
name|int
name|getKeySize
parameter_list|()
block|{
return|return
name|this
operator|.
name|keySize
return|;
block|}
comment|/**      * @param keySize the keySize to set      */
specifier|public
name|void
name|setKeySize
parameter_list|(
name|int
name|keySize
parameter_list|)
block|{
name|this
operator|.
name|keySize
operator|=
name|keySize
expr_stmt|;
if|if
condition|(
name|loaded
operator|.
name|get
argument_list|()
condition|)
block|{
throw|throw
operator|new
name|RuntimeException
argument_list|(
literal|"Pages already loaded - can't reset key size"
argument_list|)
throw|;
block|}
block|}
comment|/**      * @return the pageSize      */
specifier|public
name|int
name|getPageSize
parameter_list|()
block|{
return|return
name|this
operator|.
name|pageSize
return|;
block|}
comment|/**      * @param pageSize the pageSize to set      */
specifier|public
name|void
name|setPageSize
parameter_list|(
name|int
name|pageSize
parameter_list|)
block|{
if|if
condition|(
name|loaded
operator|.
name|get
argument_list|()
operator|&&
name|pageSize
operator|!=
name|this
operator|.
name|pageSize
condition|)
block|{
throw|throw
operator|new
name|RuntimeException
argument_list|(
literal|"Pages already loaded - can't reset page size"
argument_list|)
throw|;
block|}
name|this
operator|.
name|pageSize
operator|=
name|pageSize
expr_stmt|;
block|}
specifier|public
name|boolean
name|isTransient
parameter_list|()
block|{
return|return
literal|false
return|;
block|}
comment|/**      * @return the enablePageCaching      */
specifier|public
name|boolean
name|isEnablePageCaching
parameter_list|()
block|{
return|return
name|this
operator|.
name|enablePageCaching
return|;
block|}
comment|/**      * @param enablePageCaching the enablePageCaching to set      */
specifier|public
name|void
name|setEnablePageCaching
parameter_list|(
name|boolean
name|enablePageCaching
parameter_list|)
block|{
name|this
operator|.
name|enablePageCaching
operator|=
name|enablePageCaching
expr_stmt|;
block|}
comment|/**      * @return the pageCacheSize      */
specifier|public
name|int
name|getPageCacheSize
parameter_list|()
block|{
return|return
name|this
operator|.
name|pageCacheSize
return|;
block|}
comment|/**      * @param pageCacheSize the pageCacheSize to set      */
specifier|public
name|void
name|setPageCacheSize
parameter_list|(
name|int
name|pageCacheSize
parameter_list|)
block|{
name|this
operator|.
name|pageCacheSize
operator|=
name|pageCacheSize
expr_stmt|;
name|pageCache
operator|.
name|setMaxCacheSize
argument_list|(
name|pageCacheSize
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|load
parameter_list|()
block|{
if|if
condition|(
name|loaded
operator|.
name|compareAndSet
argument_list|(
literal|false
argument_list|,
literal|true
argument_list|)
condition|)
block|{
name|keysPerPage
operator|=
name|pageSize
operator|/
name|keySize
expr_stmt|;
name|dataIn
operator|=
operator|new
name|DataByteArrayInputStream
argument_list|()
expr_stmt|;
name|dataOut
operator|=
operator|new
name|DataByteArrayOutputStream
argument_list|(
name|pageSize
argument_list|)
expr_stmt|;
name|readBuffer
operator|=
operator|new
name|byte
index|[
name|pageSize
index|]
expr_stmt|;
try|try
block|{
name|openIndexFile
argument_list|()
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
name|pageSize
operator|)
operator|<=
name|indexFile
operator|.
name|length
argument_list|()
condition|)
block|{
name|indexFile
operator|.
name|seek
argument_list|(
name|offset
argument_list|)
expr_stmt|;
name|indexFile
operator|.
name|readFully
argument_list|(
name|readBuffer
argument_list|,
literal|0
argument_list|,
name|TreePage
operator|.
name|PAGE_HEADER_SIZE
argument_list|)
expr_stmt|;
name|dataIn
operator|.
name|restart
argument_list|(
name|readBuffer
argument_list|)
expr_stmt|;
name|TreePage
name|page
init|=
operator|new
name|TreePage
argument_list|(
name|keysPerPage
argument_list|)
decl_stmt|;
name|page
operator|.
name|setTree
argument_list|(
name|this
argument_list|)
expr_stmt|;
name|page
operator|.
name|setId
argument_list|(
name|offset
argument_list|)
expr_stmt|;
name|page
operator|.
name|readHeader
argument_list|(
name|dataIn
argument_list|)
expr_stmt|;
if|if
condition|(
operator|!
name|page
operator|.
name|isActive
argument_list|()
condition|)
block|{
if|if
condition|(
name|lastFree
operator|!=
literal|null
condition|)
block|{
name|lastFree
operator|.
name|setNextFreePageId
argument_list|(
name|offset
argument_list|)
expr_stmt|;
name|indexFile
operator|.
name|seek
argument_list|(
name|lastFree
operator|.
name|getId
argument_list|()
argument_list|)
expr_stmt|;
name|dataOut
operator|.
name|reset
argument_list|()
expr_stmt|;
name|lastFree
operator|.
name|writeHeader
argument_list|(
name|dataOut
argument_list|)
expr_stmt|;
name|indexFile
operator|.
name|write
argument_list|(
name|dataOut
operator|.
name|getData
argument_list|()
argument_list|,
literal|0
argument_list|,
name|TreePage
operator|.
name|PAGE_HEADER_SIZE
argument_list|)
expr_stmt|;
name|lastFree
operator|=
name|page
expr_stmt|;
block|}
else|else
block|{
name|lastFree
operator|=
name|page
expr_stmt|;
name|firstFree
operator|=
name|page
expr_stmt|;
block|}
block|}
elseif|else
if|if
condition|(
name|root
operator|==
literal|null
operator|&&
name|page
operator|.
name|isRoot
argument_list|()
condition|)
block|{
name|root
operator|=
name|getFullPage
argument_list|(
name|offset
argument_list|)
expr_stmt|;
block|}
name|offset
operator|+=
name|pageSize
expr_stmt|;
block|}
name|length
operator|=
name|offset
expr_stmt|;
if|if
condition|(
name|root
operator|==
literal|null
condition|)
block|{
name|root
operator|=
name|createRoot
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
literal|"Failed to load index "
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
block|}
specifier|public
name|void
name|unload
parameter_list|()
throws|throws
name|IOException
block|{
if|if
condition|(
name|loaded
operator|.
name|compareAndSet
argument_list|(
literal|true
argument_list|,
literal|false
argument_list|)
condition|)
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
name|pageCache
operator|.
name|clear
argument_list|()
expr_stmt|;
name|root
operator|=
literal|null
expr_stmt|;
name|firstFree
operator|=
literal|null
expr_stmt|;
name|lastFree
operator|=
literal|null
expr_stmt|;
block|}
block|}
block|}
specifier|public
name|void
name|store
parameter_list|(
name|Object
name|key
parameter_list|,
name|StoreEntry
name|value
parameter_list|)
throws|throws
name|IOException
block|{
name|TreeEntry
name|entry
init|=
operator|new
name|TreeEntry
argument_list|()
decl_stmt|;
name|entry
operator|.
name|setKey
argument_list|(
operator|(
name|Comparable
operator|)
name|key
argument_list|)
expr_stmt|;
name|entry
operator|.
name|setIndexOffset
argument_list|(
name|value
operator|.
name|getOffset
argument_list|()
argument_list|)
expr_stmt|;
name|root
operator|.
name|put
argument_list|(
name|entry
argument_list|)
expr_stmt|;
block|}
specifier|public
name|StoreEntry
name|get
parameter_list|(
name|Object
name|key
parameter_list|)
throws|throws
name|IOException
block|{
name|TreeEntry
name|entry
init|=
operator|new
name|TreeEntry
argument_list|()
decl_stmt|;
name|entry
operator|.
name|setKey
argument_list|(
operator|(
name|Comparable
operator|)
name|key
argument_list|)
expr_stmt|;
name|TreeEntry
name|result
init|=
name|root
operator|.
name|find
argument_list|(
name|entry
argument_list|)
decl_stmt|;
return|return
name|result
operator|!=
literal|null
condition|?
name|indexManager
operator|.
name|getIndex
argument_list|(
name|result
operator|.
name|getIndexOffset
argument_list|()
argument_list|)
else|:
literal|null
return|;
block|}
specifier|public
name|StoreEntry
name|remove
parameter_list|(
name|Object
name|key
parameter_list|)
throws|throws
name|IOException
block|{
name|TreeEntry
name|entry
init|=
operator|new
name|TreeEntry
argument_list|()
decl_stmt|;
name|entry
operator|.
name|setKey
argument_list|(
operator|(
name|Comparable
operator|)
name|key
argument_list|)
expr_stmt|;
name|TreeEntry
name|result
init|=
name|root
operator|.
name|remove
argument_list|(
name|entry
argument_list|)
decl_stmt|;
return|return
name|result
operator|!=
literal|null
condition|?
name|indexManager
operator|.
name|getIndex
argument_list|(
name|result
operator|.
name|getIndexOffset
argument_list|()
argument_list|)
else|:
literal|null
return|;
block|}
specifier|public
name|boolean
name|containsKey
parameter_list|(
name|Object
name|key
parameter_list|)
throws|throws
name|IOException
block|{
name|TreeEntry
name|entry
init|=
operator|new
name|TreeEntry
argument_list|()
decl_stmt|;
name|entry
operator|.
name|setKey
argument_list|(
operator|(
name|Comparable
operator|)
name|key
argument_list|)
expr_stmt|;
return|return
name|root
operator|.
name|find
argument_list|(
name|entry
argument_list|)
operator|!=
literal|null
return|;
block|}
specifier|public
name|void
name|clear
parameter_list|()
throws|throws
name|IOException
block|{
name|unload
argument_list|()
expr_stmt|;
name|delete
argument_list|()
expr_stmt|;
name|openIndexFile
argument_list|()
expr_stmt|;
name|load
argument_list|()
expr_stmt|;
block|}
specifier|public
name|void
name|delete
parameter_list|()
throws|throws
name|IOException
block|{
name|unload
argument_list|()
expr_stmt|;
if|if
condition|(
name|file
operator|.
name|exists
argument_list|()
condition|)
block|{
name|boolean
name|result
init|=
name|file
operator|.
name|delete
argument_list|()
decl_stmt|;
block|}
name|length
operator|=
literal|0
expr_stmt|;
block|}
comment|/**      * @return the root      */
name|TreePage
name|getRoot
parameter_list|()
block|{
return|return
name|this
operator|.
name|root
return|;
block|}
name|TreePage
name|lookupPage
parameter_list|(
name|long
name|pageId
parameter_list|)
throws|throws
name|IOException
block|{
name|TreePage
name|result
init|=
literal|null
decl_stmt|;
if|if
condition|(
name|pageId
operator|>=
literal|0
condition|)
block|{
if|if
condition|(
name|root
operator|!=
literal|null
operator|&&
name|root
operator|.
name|getId
argument_list|()
operator|==
name|pageId
condition|)
block|{
name|result
operator|=
name|root
expr_stmt|;
block|}
else|else
block|{
name|result
operator|=
name|getFromCache
argument_list|(
name|pageId
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|result
operator|==
literal|null
condition|)
block|{
name|result
operator|=
name|getFullPage
argument_list|(
name|pageId
argument_list|)
expr_stmt|;
if|if
condition|(
name|result
operator|!=
literal|null
condition|)
block|{
if|if
condition|(
name|result
operator|.
name|isActive
argument_list|()
condition|)
block|{
name|addToCache
argument_list|(
name|result
argument_list|)
expr_stmt|;
block|}
else|else
block|{
throw|throw
operator|new
name|IllegalStateException
argument_list|(
literal|"Trying to access an inactive page: "
operator|+
name|pageId
operator|+
literal|" root is "
operator|+
name|root
argument_list|)
throw|;
block|}
block|}
block|}
block|}
return|return
name|result
return|;
block|}
name|TreePage
name|createRoot
parameter_list|()
throws|throws
name|IOException
block|{
name|TreePage
name|result
init|=
name|createPage
argument_list|(
operator|-
literal|1
argument_list|)
decl_stmt|;
name|root
operator|=
name|result
expr_stmt|;
return|return
name|result
return|;
block|}
name|TreePage
name|createPage
parameter_list|(
name|long
name|parentId
parameter_list|)
throws|throws
name|IOException
block|{
name|TreePage
name|result
init|=
name|getNextFreePage
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
name|TreePage
argument_list|(
name|keysPerPage
argument_list|)
expr_stmt|;
name|result
operator|.
name|setId
argument_list|(
name|length
argument_list|)
expr_stmt|;
name|result
operator|.
name|setTree
argument_list|(
name|this
argument_list|)
expr_stmt|;
name|result
operator|.
name|setParentId
argument_list|(
name|parentId
argument_list|)
expr_stmt|;
name|writePage
argument_list|(
name|result
argument_list|)
expr_stmt|;
name|length
operator|+=
name|pageSize
expr_stmt|;
name|indexFile
operator|.
name|seek
argument_list|(
name|length
argument_list|)
expr_stmt|;
name|indexFile
operator|.
name|write
argument_list|(
name|TreeEntry
operator|.
name|NOT_SET
argument_list|)
expr_stmt|;
block|}
name|addToCache
argument_list|(
name|result
argument_list|)
expr_stmt|;
return|return
name|result
return|;
block|}
name|void
name|releasePage
parameter_list|(
name|TreePage
name|page
parameter_list|)
throws|throws
name|IOException
block|{
name|removeFromCache
argument_list|(
name|page
argument_list|)
expr_stmt|;
name|page
operator|.
name|reset
argument_list|()
expr_stmt|;
name|page
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
name|page
expr_stmt|;
name|lastFree
operator|=
name|page
expr_stmt|;
block|}
else|else
block|{
name|lastFree
operator|.
name|setNextFreePageId
argument_list|(
name|page
operator|.
name|getId
argument_list|()
argument_list|)
expr_stmt|;
name|writePage
argument_list|(
name|lastFree
argument_list|)
expr_stmt|;
block|}
name|writePage
argument_list|(
name|page
argument_list|)
expr_stmt|;
block|}
specifier|private
name|TreePage
name|getNextFreePage
parameter_list|()
throws|throws
name|IOException
block|{
name|TreePage
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
literal|null
expr_stmt|;
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
name|getPage
argument_list|(
name|firstFree
operator|.
name|getNextFreePageId
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
name|setActive
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|result
operator|.
name|reset
argument_list|()
expr_stmt|;
name|result
operator|.
name|saveHeader
argument_list|()
expr_stmt|;
block|}
return|return
name|result
return|;
block|}
name|void
name|writeFullPage
parameter_list|(
name|TreePage
name|page
parameter_list|)
throws|throws
name|IOException
block|{
name|dataOut
operator|.
name|reset
argument_list|()
expr_stmt|;
name|page
operator|.
name|write
argument_list|(
name|keyMarshaller
argument_list|,
name|dataOut
argument_list|)
expr_stmt|;
if|if
condition|(
name|dataOut
operator|.
name|size
argument_list|()
operator|>
name|pageSize
condition|)
block|{
throw|throw
operator|new
name|IOException
argument_list|(
literal|"Page Size overflow: pageSize is "
operator|+
name|pageSize
operator|+
literal|" trying to write "
operator|+
name|dataOut
operator|.
name|size
argument_list|()
argument_list|)
throw|;
block|}
name|indexFile
operator|.
name|seek
argument_list|(
name|page
operator|.
name|getId
argument_list|()
argument_list|)
expr_stmt|;
name|indexFile
operator|.
name|write
argument_list|(
name|dataOut
operator|.
name|getData
argument_list|()
argument_list|,
literal|0
argument_list|,
name|dataOut
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|void
name|writePage
parameter_list|(
name|TreePage
name|page
parameter_list|)
throws|throws
name|IOException
block|{
name|dataOut
operator|.
name|reset
argument_list|()
expr_stmt|;
name|page
operator|.
name|writeHeader
argument_list|(
name|dataOut
argument_list|)
expr_stmt|;
name|indexFile
operator|.
name|seek
argument_list|(
name|page
operator|.
name|getId
argument_list|()
argument_list|)
expr_stmt|;
name|indexFile
operator|.
name|write
argument_list|(
name|dataOut
operator|.
name|getData
argument_list|()
argument_list|,
literal|0
argument_list|,
name|TreePage
operator|.
name|PAGE_HEADER_SIZE
argument_list|)
expr_stmt|;
block|}
name|TreePage
name|getFullPage
parameter_list|(
name|long
name|id
parameter_list|)
throws|throws
name|IOException
block|{
name|indexFile
operator|.
name|seek
argument_list|(
name|id
argument_list|)
expr_stmt|;
name|indexFile
operator|.
name|readFully
argument_list|(
name|readBuffer
argument_list|,
literal|0
argument_list|,
name|pageSize
argument_list|)
expr_stmt|;
name|dataIn
operator|.
name|restart
argument_list|(
name|readBuffer
argument_list|)
expr_stmt|;
name|TreePage
name|page
init|=
operator|new
name|TreePage
argument_list|(
name|keysPerPage
argument_list|)
decl_stmt|;
name|page
operator|.
name|setId
argument_list|(
name|id
argument_list|)
expr_stmt|;
name|page
operator|.
name|setTree
argument_list|(
name|this
argument_list|)
expr_stmt|;
name|page
operator|.
name|read
argument_list|(
name|keyMarshaller
argument_list|,
name|dataIn
argument_list|)
expr_stmt|;
return|return
name|page
return|;
block|}
name|TreePage
name|getPage
parameter_list|(
name|long
name|id
parameter_list|)
throws|throws
name|IOException
block|{
name|indexFile
operator|.
name|seek
argument_list|(
name|id
argument_list|)
expr_stmt|;
name|indexFile
operator|.
name|readFully
argument_list|(
name|readBuffer
argument_list|,
literal|0
argument_list|,
name|TreePage
operator|.
name|PAGE_HEADER_SIZE
argument_list|)
expr_stmt|;
name|dataIn
operator|.
name|restart
argument_list|(
name|readBuffer
argument_list|)
expr_stmt|;
name|TreePage
name|page
init|=
operator|new
name|TreePage
argument_list|(
name|keysPerPage
argument_list|)
decl_stmt|;
name|page
operator|.
name|setId
argument_list|(
name|id
argument_list|)
expr_stmt|;
name|page
operator|.
name|setTree
argument_list|(
name|this
argument_list|)
expr_stmt|;
name|page
operator|.
name|readHeader
argument_list|(
name|dataIn
argument_list|)
expr_stmt|;
return|return
name|page
return|;
block|}
specifier|private
name|TreePage
name|getFromCache
parameter_list|(
name|long
name|pageId
parameter_list|)
block|{
name|TreePage
name|result
init|=
literal|null
decl_stmt|;
if|if
condition|(
name|enablePageCaching
condition|)
block|{
name|result
operator|=
name|pageCache
operator|.
name|get
argument_list|(
name|pageId
argument_list|)
expr_stmt|;
block|}
return|return
name|result
return|;
block|}
specifier|private
name|void
name|addToCache
parameter_list|(
name|TreePage
name|page
parameter_list|)
block|{
if|if
condition|(
name|enablePageCaching
condition|)
block|{
name|pageCache
operator|.
name|put
argument_list|(
name|page
operator|.
name|getId
argument_list|()
argument_list|,
name|page
argument_list|)
expr_stmt|;
block|}
block|}
specifier|private
name|void
name|removeFromCache
parameter_list|(
name|TreePage
name|page
parameter_list|)
block|{
if|if
condition|(
name|enablePageCaching
condition|)
block|{
name|pageCache
operator|.
name|remove
argument_list|(
name|page
operator|.
name|getId
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
specifier|protected
name|void
name|openIndexFile
parameter_list|()
throws|throws
name|IOException
block|{
if|if
condition|(
name|indexFile
operator|==
literal|null
condition|)
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
name|IOHelper
operator|.
name|toFileSystemSafeName
argument_list|(
name|name
argument_list|)
argument_list|)
expr_stmt|;
name|indexFile
operator|=
operator|new
name|RandomAccessFile
argument_list|(
name|file
argument_list|,
literal|"rw"
argument_list|)
expr_stmt|;
block|}
block|}
static|static
block|{
name|DEFAULT_PAGE_SIZE
operator|=
name|Integer
operator|.
name|parseInt
argument_list|(
name|System
operator|.
name|getProperty
argument_list|(
literal|"defaultPageSize"
argument_list|,
literal|"16384"
argument_list|)
argument_list|)
expr_stmt|;
name|DEFAULT_KEY_SIZE
operator|=
name|Integer
operator|.
name|parseInt
argument_list|(
name|System
operator|.
name|getProperty
argument_list|(
literal|"defaultKeySize"
argument_list|,
literal|"96"
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

