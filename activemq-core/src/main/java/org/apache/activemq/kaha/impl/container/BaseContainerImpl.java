begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  *   * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements. See the NOTICE file distributed with this  * work for additional information regarding copyright ownership. The ASF  * licenses this file to You under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance with the License.  * You may obtain a copy of the License at  *   * http://www.apache.org/licenses/LICENSE-2.0  *   * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT  * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the  * License for the specific language governing permissions and limitations under  * the License.  */
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
name|List
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
name|DiskIndexLinkedList
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
name|VMIndexLinkedList
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
comment|/**  * Implementation of a ListContainer  *   * @version $Revision: 1.2 $  */
end_comment

begin_class
specifier|public
specifier|abstract
class|class
name|BaseContainerImpl
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
name|BaseContainerImpl
operator|.
name|class
argument_list|)
decl_stmt|;
specifier|protected
name|IndexItem
name|root
decl_stmt|;
specifier|protected
name|IndexLinkedList
name|indexList
decl_stmt|;
specifier|protected
name|IndexManager
name|indexManager
decl_stmt|;
specifier|protected
name|DataManager
name|dataManager
decl_stmt|;
specifier|protected
name|ContainerId
name|containerId
decl_stmt|;
specifier|protected
name|boolean
name|loaded
decl_stmt|;
specifier|protected
name|boolean
name|closed
decl_stmt|;
specifier|protected
name|boolean
name|initialized
decl_stmt|;
specifier|protected
name|boolean
name|persistentIndex
decl_stmt|;
specifier|protected
name|BaseContainerImpl
parameter_list|(
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
name|this
operator|.
name|containerId
operator|=
name|id
expr_stmt|;
name|this
operator|.
name|root
operator|=
name|root
expr_stmt|;
name|this
operator|.
name|indexManager
operator|=
name|indexManager
expr_stmt|;
name|this
operator|.
name|dataManager
operator|=
name|dataManager
expr_stmt|;
name|this
operator|.
name|persistentIndex
operator|=
name|persistentIndex
expr_stmt|;
block|}
specifier|public
name|ContainerId
name|getContainerId
parameter_list|()
block|{
return|return
name|containerId
return|;
block|}
specifier|public
specifier|synchronized
name|void
name|init
parameter_list|()
block|{
if|if
condition|(
operator|!
name|initialized
condition|)
block|{
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
if|if
condition|(
name|this
operator|.
name|indexList
operator|==
literal|null
condition|)
block|{
if|if
condition|(
name|persistentIndex
condition|)
block|{
name|this
operator|.
name|indexList
operator|=
operator|new
name|DiskIndexLinkedList
argument_list|(
name|indexManager
argument_list|,
name|root
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|this
operator|.
name|indexList
operator|=
operator|new
name|VMIndexLinkedList
argument_list|(
name|root
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|}
block|}
specifier|public
specifier|synchronized
name|void
name|clear
parameter_list|()
block|{
if|if
condition|(
name|indexList
operator|!=
literal|null
condition|)
block|{
name|indexList
operator|.
name|clear
argument_list|()
expr_stmt|;
block|}
block|}
comment|/**      * @return the indexList      */
specifier|public
name|IndexLinkedList
name|getList
parameter_list|()
block|{
return|return
name|indexList
return|;
block|}
comment|/**      * @param indexList the indexList to set      */
specifier|public
name|void
name|setList
parameter_list|(
name|IndexLinkedList
name|indexList
parameter_list|)
block|{
name|this
operator|.
name|indexList
operator|=
name|indexList
expr_stmt|;
block|}
specifier|public
specifier|abstract
name|void
name|unload
parameter_list|()
function_decl|;
specifier|public
specifier|abstract
name|void
name|load
parameter_list|()
function_decl|;
specifier|public
specifier|abstract
name|int
name|size
parameter_list|()
function_decl|;
specifier|protected
specifier|abstract
name|Object
name|getValue
parameter_list|(
name|StoreEntry
name|currentItem
parameter_list|)
function_decl|;
specifier|protected
specifier|abstract
name|void
name|remove
parameter_list|(
name|IndexItem
name|currentItem
parameter_list|)
function_decl|;
specifier|protected
specifier|synchronized
specifier|final
name|IndexLinkedList
name|getInternalList
parameter_list|()
block|{
return|return
name|indexList
return|;
block|}
specifier|public
specifier|synchronized
specifier|final
name|void
name|close
parameter_list|()
block|{
name|unload
argument_list|()
expr_stmt|;
name|closed
operator|=
literal|true
expr_stmt|;
block|}
comment|/*      * (non-Javadoc)      *       * @see org.apache.activemq.kaha.ListContainer#isLoaded()      */
specifier|public
specifier|synchronized
specifier|final
name|boolean
name|isLoaded
parameter_list|()
block|{
name|checkClosed
argument_list|()
expr_stmt|;
return|return
name|loaded
return|;
block|}
comment|/*      * (non-Javadoc)      *       * @see org.apache.activemq.kaha.ListContainer#getId()      */
specifier|public
specifier|final
name|Object
name|getId
parameter_list|()
block|{
name|checkClosed
argument_list|()
expr_stmt|;
return|return
name|containerId
operator|.
name|getKey
argument_list|()
return|;
block|}
specifier|public
name|DataManager
name|getDataManager
parameter_list|()
block|{
return|return
name|dataManager
return|;
block|}
specifier|public
name|IndexManager
name|getIndexManager
parameter_list|()
block|{
return|return
name|indexManager
return|;
block|}
specifier|public
specifier|synchronized
specifier|final
name|void
name|expressDataInterest
parameter_list|()
throws|throws
name|IOException
block|{
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
name|item
operator|.
name|setOffset
argument_list|(
name|nextItem
argument_list|)
expr_stmt|;
name|dataManager
operator|.
name|addInterestInFile
argument_list|(
name|item
operator|.
name|getKeyFile
argument_list|()
argument_list|)
expr_stmt|;
name|dataManager
operator|.
name|addInterestInFile
argument_list|(
name|item
operator|.
name|getValueFile
argument_list|()
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
specifier|protected
specifier|final
name|void
name|doClear
parameter_list|()
block|{
name|checkClosed
argument_list|()
expr_stmt|;
name|loaded
operator|=
literal|true
expr_stmt|;
name|List
name|indexList
init|=
operator|new
name|ArrayList
argument_list|()
decl_stmt|;
try|try
block|{
name|init
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
operator|new
name|IndexItem
argument_list|()
decl_stmt|;
name|item
operator|.
name|setOffset
argument_list|(
name|nextItem
argument_list|)
expr_stmt|;
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
name|root
operator|.
name|setNextItem
argument_list|(
name|Item
operator|.
name|POSITION_NOT_SET
argument_list|)
expr_stmt|;
name|storeIndex
argument_list|(
name|root
argument_list|)
expr_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|indexList
operator|.
name|size
argument_list|()
condition|;
name|i
operator|++
control|)
block|{
name|IndexItem
name|item
init|=
operator|(
name|IndexItem
operator|)
name|indexList
operator|.
name|get
argument_list|(
name|i
argument_list|)
decl_stmt|;
name|dataManager
operator|.
name|removeInterestInFile
argument_list|(
name|item
operator|.
name|getKeyFile
argument_list|()
argument_list|)
expr_stmt|;
name|dataManager
operator|.
name|removeInterestInFile
argument_list|(
name|item
operator|.
name|getValueFile
argument_list|()
argument_list|)
expr_stmt|;
name|indexManager
operator|.
name|freeIndex
argument_list|(
name|item
argument_list|)
expr_stmt|;
block|}
name|indexList
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
literal|"Failed to clear Container "
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
specifier|protected
specifier|final
name|void
name|delete
parameter_list|(
specifier|final
name|IndexItem
name|keyItem
parameter_list|,
specifier|final
name|IndexItem
name|prevItem
parameter_list|,
specifier|final
name|IndexItem
name|nextItem
parameter_list|)
block|{
if|if
condition|(
name|keyItem
operator|!=
literal|null
condition|)
block|{
try|try
block|{
name|IndexItem
name|prev
init|=
name|prevItem
operator|==
literal|null
condition|?
name|root
else|:
name|prevItem
decl_stmt|;
name|IndexItem
name|next
init|=
name|nextItem
operator|!=
name|root
condition|?
name|nextItem
else|:
literal|null
decl_stmt|;
name|dataManager
operator|.
name|removeInterestInFile
argument_list|(
name|keyItem
operator|.
name|getKeyFile
argument_list|()
argument_list|)
expr_stmt|;
name|dataManager
operator|.
name|removeInterestInFile
argument_list|(
name|keyItem
operator|.
name|getValueFile
argument_list|()
argument_list|)
expr_stmt|;
if|if
condition|(
name|next
operator|!=
literal|null
condition|)
block|{
name|prev
operator|.
name|setNextItem
argument_list|(
name|next
operator|.
name|getOffset
argument_list|()
argument_list|)
expr_stmt|;
name|next
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
name|next
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|prev
operator|.
name|setNextItem
argument_list|(
name|Item
operator|.
name|POSITION_NOT_SET
argument_list|)
expr_stmt|;
block|}
name|updateIndexes
argument_list|(
name|prev
argument_list|)
expr_stmt|;
name|indexManager
operator|.
name|freeIndex
argument_list|(
name|keyItem
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
literal|"Failed to delete "
operator|+
name|keyItem
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
specifier|protected
specifier|final
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
name|void
name|storeIndex
parameter_list|(
name|IndexItem
name|item
parameter_list|)
throws|throws
name|IOException
block|{
name|indexManager
operator|.
name|storeIndex
argument_list|(
name|item
argument_list|)
expr_stmt|;
block|}
specifier|protected
name|void
name|updateIndexes
parameter_list|(
name|IndexItem
name|item
parameter_list|)
throws|throws
name|IOException
block|{
name|indexManager
operator|.
name|updateIndexes
argument_list|(
name|item
argument_list|)
expr_stmt|;
block|}
specifier|protected
specifier|final
name|boolean
name|isRoot
parameter_list|(
name|StoreEntry
name|item
parameter_list|)
block|{
return|return
name|item
operator|!=
literal|null
operator|&&
name|root
operator|!=
literal|null
operator|&&
operator|(
name|root
operator|==
name|item
operator|||
name|root
operator|.
name|getOffset
argument_list|()
operator|==
name|item
operator|.
name|getOffset
argument_list|()
operator|)
return|;
comment|// return item != null&& indexRoot != null&& indexRoot == item;
block|}
block|}
end_class

end_unit

