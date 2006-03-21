begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  *   * Copyright 2005-2006 The Apache Software Foundation  *   * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with  * the License. You may obtain a copy of the License at  *   * http://www.apache.org/licenses/LICENSE-2.0  *   * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on  * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the  * specific language governing permissions and limitations under the License.  */
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
name|LinkedList
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
name|ObjectMarshaller
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
class|class
name|MapContainerImpl
implements|implements
name|MapContainer
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
name|MapContainerImpl
operator|.
name|class
argument_list|)
decl_stmt|;
specifier|protected
name|StoreImpl
name|store
decl_stmt|;
specifier|protected
name|LocatableItem
name|root
decl_stmt|;
specifier|protected
name|Object
name|id
decl_stmt|;
specifier|protected
name|Map
name|map
init|=
operator|new
name|HashMap
argument_list|()
decl_stmt|;
specifier|protected
name|Map
name|valueToKeyMap
init|=
operator|new
name|HashMap
argument_list|()
decl_stmt|;
specifier|protected
name|LinkedList
name|list
init|=
operator|new
name|LinkedList
argument_list|()
decl_stmt|;
specifier|protected
name|boolean
name|loaded
init|=
literal|false
decl_stmt|;
specifier|protected
name|Marshaller
name|keyMarshaller
init|=
operator|new
name|ObjectMarshaller
argument_list|()
decl_stmt|;
specifier|protected
name|Marshaller
name|valueMarshaller
init|=
operator|new
name|ObjectMarshaller
argument_list|()
decl_stmt|;
specifier|protected
specifier|final
name|Object
name|mutex
init|=
operator|new
name|Object
argument_list|()
decl_stmt|;
specifier|protected
name|boolean
name|closed
init|=
literal|false
decl_stmt|;
specifier|protected
name|MapContainerImpl
parameter_list|(
name|Object
name|id
parameter_list|,
name|StoreImpl
name|rfs
parameter_list|,
name|LocatableItem
name|root
parameter_list|)
throws|throws
name|IOException
block|{
name|this
operator|.
name|id
operator|=
name|id
expr_stmt|;
name|this
operator|.
name|store
operator|=
name|rfs
expr_stmt|;
name|this
operator|.
name|root
operator|=
name|root
expr_stmt|;
block|}
comment|/*      * (non-Javadoc)      *       * @see org.apache.activemq.kaha.MapContainer#load()      */
specifier|public
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
name|loaded
operator|=
literal|true
expr_stmt|;
synchronized|synchronized
init|(
name|mutex
init|)
block|{
try|try
block|{
name|long
name|start
init|=
name|root
operator|.
name|getNextItem
argument_list|()
decl_stmt|;
if|if
condition|(
name|start
operator|!=
name|Item
operator|.
name|POSITION_NOT_SET
condition|)
block|{
name|long
name|nextItem
init|=
name|start
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
name|LocatableItem
name|item
init|=
operator|new
name|LocatableItem
argument_list|()
decl_stmt|;
name|item
operator|.
name|setOffset
argument_list|(
name|nextItem
argument_list|)
expr_stmt|;
name|Object
name|key
init|=
name|store
operator|.
name|readItem
argument_list|(
name|keyMarshaller
argument_list|,
name|item
argument_list|)
decl_stmt|;
name|map
operator|.
name|put
argument_list|(
name|key
argument_list|,
name|item
argument_list|)
expr_stmt|;
name|valueToKeyMap
operator|.
name|put
argument_list|(
name|item
argument_list|,
name|key
argument_list|)
expr_stmt|;
name|list
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
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
name|log
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
synchronized|synchronized
init|(
name|mutex
init|)
block|{
name|map
operator|.
name|clear
argument_list|()
expr_stmt|;
name|valueToKeyMap
operator|.
name|clear
argument_list|()
expr_stmt|;
name|list
operator|.
name|clear
argument_list|()
expr_stmt|;
block|}
block|}
block|}
specifier|public
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
specifier|public
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
block|}
specifier|public
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
comment|/*      * (non-Javadoc)      *       * @see org.apache.activemq.kaha.MapContainer#isLoaded()      */
specifier|public
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
comment|/*      * (non-Javadoc)      *       * @see org.apache.activemq.kaha.MapContainer#getId()      */
specifier|public
name|Object
name|getId
parameter_list|()
block|{
name|checkClosed
argument_list|()
expr_stmt|;
return|return
name|id
return|;
block|}
comment|/*      * (non-Javadoc)      *       * @see org.apache.activemq.kaha.MapContainer#size()      */
specifier|public
name|int
name|size
parameter_list|()
block|{
name|checkClosed
argument_list|()
expr_stmt|;
name|checkLoaded
argument_list|()
expr_stmt|;
return|return
name|map
operator|.
name|size
argument_list|()
return|;
block|}
comment|/*      * (non-Javadoc)      *       * @see org.apache.activemq.kaha.MapContainer#isEmpty()      */
specifier|public
name|boolean
name|isEmpty
parameter_list|()
block|{
name|checkClosed
argument_list|()
expr_stmt|;
name|checkLoaded
argument_list|()
expr_stmt|;
return|return
name|map
operator|.
name|isEmpty
argument_list|()
return|;
block|}
comment|/*      * (non-Javadoc)      *       * @see org.apache.activemq.kaha.MapContainer#containsKey(java.lang.Object)      */
specifier|public
name|boolean
name|containsKey
parameter_list|(
name|Object
name|key
parameter_list|)
block|{
name|checkClosed
argument_list|()
expr_stmt|;
name|checkLoaded
argument_list|()
expr_stmt|;
synchronized|synchronized
init|(
name|mutex
init|)
block|{
return|return
name|map
operator|.
name|containsKey
argument_list|(
name|key
argument_list|)
return|;
block|}
block|}
comment|/*      * (non-Javadoc)      *       * @see org.apache.activemq.kaha.MapContainer#get(java.lang.Object)      */
specifier|public
name|Object
name|get
parameter_list|(
name|Object
name|key
parameter_list|)
block|{
name|checkClosed
argument_list|()
expr_stmt|;
name|checkLoaded
argument_list|()
expr_stmt|;
name|Object
name|result
init|=
literal|null
decl_stmt|;
name|LocatableItem
name|item
init|=
literal|null
decl_stmt|;
synchronized|synchronized
init|(
name|mutex
init|)
block|{
name|item
operator|=
operator|(
name|LocatableItem
operator|)
name|map
operator|.
name|get
argument_list|(
name|key
argument_list|)
expr_stmt|;
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
comment|/*      * (non-Javadoc)      *       * @see org.apache.activemq.kaha.MapContainer#containsValue(java.lang.Object)      */
specifier|public
name|boolean
name|containsValue
parameter_list|(
name|Object
name|o
parameter_list|)
block|{
name|checkClosed
argument_list|()
expr_stmt|;
name|checkLoaded
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
synchronized|synchronized
init|(
name|list
init|)
block|{
for|for
control|(
name|Iterator
name|i
init|=
name|list
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
name|LocatableItem
name|item
init|=
operator|(
name|LocatableItem
operator|)
name|i
operator|.
name|next
argument_list|()
decl_stmt|;
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
block|}
block|}
block|}
return|return
name|result
return|;
block|}
comment|/*      * (non-Javadoc)      *       * @see org.apache.activemq.kaha.MapContainer#putAll(java.util.Map)      */
specifier|public
name|void
name|putAll
parameter_list|(
name|Map
name|t
parameter_list|)
block|{
name|checkClosed
argument_list|()
expr_stmt|;
name|checkLoaded
argument_list|()
expr_stmt|;
if|if
condition|(
name|t
operator|!=
literal|null
condition|)
block|{
synchronized|synchronized
init|(
name|mutex
init|)
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
block|}
comment|/*      * (non-Javadoc)      *       * @see org.apache.activemq.kaha.MapContainer#keySet()      */
specifier|public
name|Set
name|keySet
parameter_list|()
block|{
name|checkClosed
argument_list|()
expr_stmt|;
name|checkLoaded
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
name|Collection
name|values
parameter_list|()
block|{
name|checkClosed
argument_list|()
expr_stmt|;
name|checkLoaded
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
name|Set
name|entrySet
parameter_list|()
block|{
name|checkClosed
argument_list|()
expr_stmt|;
name|checkLoaded
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
comment|/*      * (non-Javadoc)      *       * @see org.apache.activemq.kaha.MapContainer#put(java.lang.Object, java.lang.Object)      */
specifier|public
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
name|checkClosed
argument_list|()
expr_stmt|;
name|checkLoaded
argument_list|()
expr_stmt|;
name|Object
name|result
init|=
literal|null
decl_stmt|;
synchronized|synchronized
init|(
name|mutex
init|)
block|{
if|if
condition|(
name|map
operator|.
name|containsKey
argument_list|(
name|key
argument_list|)
condition|)
block|{
name|result
operator|=
name|remove
argument_list|(
name|key
argument_list|)
expr_stmt|;
block|}
name|LocatableItem
name|item
init|=
name|write
argument_list|(
name|key
argument_list|,
name|value
argument_list|)
decl_stmt|;
name|map
operator|.
name|put
argument_list|(
name|key
argument_list|,
name|item
argument_list|)
expr_stmt|;
name|valueToKeyMap
operator|.
name|put
argument_list|(
name|item
argument_list|,
name|key
argument_list|)
expr_stmt|;
name|list
operator|.
name|add
argument_list|(
name|item
argument_list|)
expr_stmt|;
block|}
return|return
name|result
return|;
block|}
comment|/*      * (non-Javadoc)      *       * @see org.apache.activemq.kaha.MapContainer#remove(java.lang.Object)      */
specifier|public
name|Object
name|remove
parameter_list|(
name|Object
name|key
parameter_list|)
block|{
name|checkClosed
argument_list|()
expr_stmt|;
name|checkLoaded
argument_list|()
expr_stmt|;
name|Object
name|result
init|=
literal|null
decl_stmt|;
synchronized|synchronized
init|(
name|mutex
init|)
block|{
name|LocatableItem
name|item
init|=
operator|(
name|LocatableItem
operator|)
name|map
operator|.
name|get
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
name|map
operator|.
name|remove
argument_list|(
name|key
argument_list|)
expr_stmt|;
name|valueToKeyMap
operator|.
name|remove
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
name|int
name|index
init|=
name|list
operator|.
name|indexOf
argument_list|(
name|item
argument_list|)
decl_stmt|;
name|LocatableItem
name|prev
init|=
name|index
operator|>
literal|0
condition|?
operator|(
name|LocatableItem
operator|)
name|list
operator|.
name|get
argument_list|(
name|index
operator|-
literal|1
argument_list|)
else|:
name|root
decl_stmt|;
name|LocatableItem
name|next
init|=
name|index
operator|<
operator|(
name|list
operator|.
name|size
argument_list|()
operator|-
literal|1
operator|)
condition|?
operator|(
name|LocatableItem
operator|)
name|list
operator|.
name|get
argument_list|(
name|index
operator|+
literal|1
argument_list|)
else|:
literal|null
decl_stmt|;
name|list
operator|.
name|remove
argument_list|(
name|index
argument_list|)
expr_stmt|;
block|{
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
name|item
operator|=
literal|null
expr_stmt|;
block|}
block|}
return|return
name|result
return|;
block|}
specifier|public
name|boolean
name|removeValue
parameter_list|(
name|Object
name|o
parameter_list|)
block|{
name|checkClosed
argument_list|()
expr_stmt|;
name|checkLoaded
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
synchronized|synchronized
init|(
name|list
init|)
block|{
for|for
control|(
name|Iterator
name|i
init|=
name|list
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
name|LocatableItem
name|item
init|=
operator|(
name|LocatableItem
operator|)
name|i
operator|.
name|next
argument_list|()
decl_stmt|;
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
name|valueToKeyMap
operator|.
name|get
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
block|}
block|}
block|}
return|return
name|result
return|;
block|}
specifier|protected
name|void
name|remove
parameter_list|(
name|LocatableItem
name|item
parameter_list|)
block|{
name|Object
name|key
init|=
name|valueToKeyMap
operator|.
name|get
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
name|void
name|clear
parameter_list|()
block|{
name|checkClosed
argument_list|()
expr_stmt|;
synchronized|synchronized
init|(
name|mutex
init|)
block|{
name|loaded
operator|=
literal|true
expr_stmt|;
synchronized|synchronized
init|(
name|mutex
init|)
block|{
name|map
operator|.
name|clear
argument_list|()
expr_stmt|;
name|valueToKeyMap
operator|.
name|clear
argument_list|()
expr_stmt|;
name|list
operator|.
name|clear
argument_list|()
expr_stmt|;
comment|// going to re-use this
try|try
block|{
name|long
name|start
init|=
name|root
operator|.
name|getNextItem
argument_list|()
decl_stmt|;
if|if
condition|(
name|start
operator|!=
name|Item
operator|.
name|POSITION_NOT_SET
condition|)
block|{
name|long
name|nextItem
init|=
name|start
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
name|LocatableItem
name|item
init|=
operator|new
name|LocatableItem
argument_list|()
decl_stmt|;
name|item
operator|.
name|setOffset
argument_list|(
name|nextItem
argument_list|)
expr_stmt|;
name|list
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
name|root
operator|.
name|setNextItem
argument_list|(
name|Item
operator|.
name|POSITION_NOT_SET
argument_list|)
expr_stmt|;
name|store
operator|.
name|updateItem
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
name|list
operator|.
name|size
argument_list|()
condition|;
name|i
operator|++
control|)
block|{
name|LocatableItem
name|item
init|=
operator|(
name|LocatableItem
operator|)
name|list
operator|.
name|get
argument_list|(
name|i
argument_list|)
decl_stmt|;
if|if
condition|(
name|item
operator|.
name|getReferenceItem
argument_list|()
operator|!=
name|Item
operator|.
name|POSITION_NOT_SET
condition|)
block|{
name|Item
name|value
init|=
operator|new
name|Item
argument_list|()
decl_stmt|;
name|value
operator|.
name|setOffset
argument_list|(
name|item
operator|.
name|getReferenceItem
argument_list|()
argument_list|)
expr_stmt|;
name|store
operator|.
name|removeItem
argument_list|(
name|value
argument_list|)
expr_stmt|;
block|}
name|store
operator|.
name|removeItem
argument_list|(
name|item
argument_list|)
expr_stmt|;
block|}
name|list
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
name|log
operator|.
name|error
argument_list|(
literal|"Failed to clear MapContainer "
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
specifier|protected
name|Set
name|getInternalKeySet
parameter_list|()
block|{
return|return
operator|new
name|HashSet
argument_list|(
name|map
operator|.
name|keySet
argument_list|()
argument_list|)
return|;
block|}
specifier|protected
name|LinkedList
name|getItemList
parameter_list|()
block|{
return|return
name|list
return|;
block|}
specifier|protected
name|Object
name|getValue
parameter_list|(
name|LocatableItem
name|item
parameter_list|)
block|{
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
operator|&&
name|item
operator|.
name|getReferenceItem
argument_list|()
operator|!=
name|Item
operator|.
name|POSITION_NOT_SET
condition|)
block|{
name|Item
name|rec
init|=
operator|new
name|Item
argument_list|()
decl_stmt|;
name|rec
operator|.
name|setOffset
argument_list|(
name|item
operator|.
name|getReferenceItem
argument_list|()
argument_list|)
expr_stmt|;
try|try
block|{
name|result
operator|=
name|store
operator|.
name|readItem
argument_list|(
name|valueMarshaller
argument_list|,
name|rec
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
name|log
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
specifier|protected
name|LocatableItem
name|write
parameter_list|(
name|Object
name|key
parameter_list|,
name|Object
name|value
parameter_list|)
block|{
name|long
name|pos
init|=
name|Item
operator|.
name|POSITION_NOT_SET
decl_stmt|;
name|LocatableItem
name|item
init|=
literal|null
decl_stmt|;
try|try
block|{
if|if
condition|(
name|value
operator|!=
literal|null
condition|)
block|{
name|Item
name|valueItem
init|=
operator|new
name|Item
argument_list|()
decl_stmt|;
name|pos
operator|=
name|store
operator|.
name|storeItem
argument_list|(
name|valueMarshaller
argument_list|,
name|value
argument_list|,
name|valueItem
argument_list|)
expr_stmt|;
block|}
name|LocatableItem
name|last
init|=
name|list
operator|.
name|isEmpty
argument_list|()
condition|?
literal|null
else|:
operator|(
name|LocatableItem
operator|)
name|list
operator|.
name|getLast
argument_list|()
decl_stmt|;
name|last
operator|=
name|last
operator|==
literal|null
condition|?
name|root
else|:
name|last
expr_stmt|;
name|long
name|prev
init|=
name|last
operator|.
name|getOffset
argument_list|()
decl_stmt|;
name|long
name|next
init|=
name|Item
operator|.
name|POSITION_NOT_SET
decl_stmt|;
name|item
operator|=
operator|new
name|LocatableItem
argument_list|(
name|prev
argument_list|,
name|next
argument_list|,
name|pos
argument_list|)
expr_stmt|;
name|next
operator|=
name|store
operator|.
name|storeItem
argument_list|(
name|keyMarshaller
argument_list|,
name|key
argument_list|,
name|item
argument_list|)
expr_stmt|;
if|if
condition|(
name|last
operator|!=
literal|null
condition|)
block|{
name|last
operator|.
name|setNextItem
argument_list|(
name|next
argument_list|)
expr_stmt|;
name|store
operator|.
name|updateItem
argument_list|(
name|last
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
name|e
operator|.
name|printStackTrace
argument_list|()
expr_stmt|;
name|log
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
name|item
return|;
block|}
specifier|protected
name|void
name|delete
parameter_list|(
name|LocatableItem
name|key
parameter_list|,
name|LocatableItem
name|prev
parameter_list|,
name|LocatableItem
name|next
parameter_list|)
block|{
try|try
block|{
name|prev
operator|=
name|prev
operator|==
literal|null
condition|?
name|root
else|:
name|prev
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
name|store
operator|.
name|updateItem
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
name|store
operator|.
name|updateItem
argument_list|(
name|prev
argument_list|)
expr_stmt|;
if|if
condition|(
name|key
operator|.
name|getReferenceItem
argument_list|()
operator|!=
name|Item
operator|.
name|POSITION_NOT_SET
condition|)
block|{
name|Item
name|value
init|=
operator|new
name|Item
argument_list|()
decl_stmt|;
name|value
operator|.
name|setOffset
argument_list|(
name|key
operator|.
name|getReferenceItem
argument_list|()
argument_list|)
expr_stmt|;
name|store
operator|.
name|removeItem
argument_list|(
name|value
argument_list|)
expr_stmt|;
block|}
name|store
operator|.
name|removeItem
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
name|log
operator|.
name|error
argument_list|(
literal|"Failed to delete "
operator|+
name|key
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
specifier|final
name|void
name|checkLoaded
parameter_list|()
block|{
if|if
condition|(
operator|!
name|loaded
condition|)
block|{
throw|throw
operator|new
name|RuntimeStoreException
argument_list|(
literal|"The container is not loaded"
argument_list|)
throw|;
block|}
block|}
block|}
end_class

end_unit

