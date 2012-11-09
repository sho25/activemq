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
name|util
operator|.
name|ListIterator
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

begin_comment
comment|/**  *   */
end_comment

begin_class
specifier|public
class|class
name|ContainerListIterator
extends|extends
name|ContainerValueCollectionIterator
implements|implements
name|ListIterator
block|{
specifier|protected
name|ContainerListIterator
parameter_list|(
name|ListContainerImpl
name|container
parameter_list|,
name|IndexLinkedList
name|list
parameter_list|,
name|IndexItem
name|start
parameter_list|)
block|{
name|super
argument_list|(
name|container
argument_list|,
name|list
argument_list|,
name|start
argument_list|)
expr_stmt|;
block|}
comment|/*      * (non-Javadoc)      *       * @see java.util.ListIterator#hasPrevious()      */
specifier|public
name|boolean
name|hasPrevious
parameter_list|()
block|{
synchronized|synchronized
init|(
name|container
init|)
block|{
name|nextItem
operator|=
operator|(
name|IndexItem
operator|)
name|list
operator|.
name|refreshEntry
argument_list|(
name|nextItem
argument_list|)
expr_stmt|;
return|return
name|list
operator|.
name|getPrevEntry
argument_list|(
name|nextItem
argument_list|)
operator|!=
literal|null
return|;
block|}
block|}
comment|/*      * (non-Javadoc)      *       * @see java.util.ListIterator#previous()      */
specifier|public
name|Object
name|previous
parameter_list|()
block|{
synchronized|synchronized
init|(
name|container
init|)
block|{
name|nextItem
operator|=
operator|(
name|IndexItem
operator|)
name|list
operator|.
name|refreshEntry
argument_list|(
name|nextItem
argument_list|)
expr_stmt|;
name|nextItem
operator|=
name|list
operator|.
name|getPrevEntry
argument_list|(
name|nextItem
argument_list|)
expr_stmt|;
return|return
name|nextItem
operator|!=
literal|null
condition|?
name|container
operator|.
name|getValue
argument_list|(
name|nextItem
argument_list|)
else|:
literal|null
return|;
block|}
block|}
comment|/*      * (non-Javadoc)      *       * @see java.util.ListIterator#nextIndex()      */
specifier|public
name|int
name|nextIndex
parameter_list|()
block|{
name|int
name|result
init|=
operator|-
literal|1
decl_stmt|;
if|if
condition|(
name|nextItem
operator|!=
literal|null
condition|)
block|{
synchronized|synchronized
init|(
name|container
init|)
block|{
name|nextItem
operator|=
operator|(
name|IndexItem
operator|)
name|list
operator|.
name|refreshEntry
argument_list|(
name|nextItem
argument_list|)
expr_stmt|;
name|StoreEntry
name|next
init|=
name|list
operator|.
name|getNextEntry
argument_list|(
name|nextItem
argument_list|)
decl_stmt|;
if|if
condition|(
name|next
operator|!=
literal|null
condition|)
block|{
name|result
operator|=
name|container
operator|.
name|getInternalList
argument_list|()
operator|.
name|indexOf
argument_list|(
name|next
argument_list|)
expr_stmt|;
block|}
block|}
block|}
return|return
name|result
return|;
block|}
comment|/*      * (non-Javadoc)      *       * @see java.util.ListIterator#previousIndex()      */
specifier|public
name|int
name|previousIndex
parameter_list|()
block|{
name|int
name|result
init|=
operator|-
literal|1
decl_stmt|;
if|if
condition|(
name|nextItem
operator|!=
literal|null
condition|)
block|{
synchronized|synchronized
init|(
name|container
init|)
block|{
name|nextItem
operator|=
operator|(
name|IndexItem
operator|)
name|list
operator|.
name|refreshEntry
argument_list|(
name|nextItem
argument_list|)
expr_stmt|;
name|StoreEntry
name|prev
init|=
name|list
operator|.
name|getPrevEntry
argument_list|(
name|nextItem
argument_list|)
decl_stmt|;
if|if
condition|(
name|prev
operator|!=
literal|null
condition|)
block|{
name|result
operator|=
name|container
operator|.
name|getInternalList
argument_list|()
operator|.
name|indexOf
argument_list|(
name|prev
argument_list|)
expr_stmt|;
block|}
block|}
block|}
return|return
name|result
return|;
block|}
comment|/*      * (non-Javadoc)      *       * @see java.util.ListIterator#set(E)      */
specifier|public
name|void
name|set
parameter_list|(
name|Object
name|o
parameter_list|)
block|{
name|IndexItem
name|item
init|=
operator|(
operator|(
name|ListContainerImpl
operator|)
name|container
operator|)
operator|.
name|internalSet
argument_list|(
name|previousIndex
argument_list|()
operator|+
literal|1
argument_list|,
name|o
argument_list|)
decl_stmt|;
name|nextItem
operator|=
name|item
expr_stmt|;
block|}
comment|/*      * (non-Javadoc)      *       * @see java.util.ListIterator#add(E)      */
specifier|public
name|void
name|add
parameter_list|(
name|Object
name|o
parameter_list|)
block|{
name|IndexItem
name|item
init|=
operator|(
operator|(
name|ListContainerImpl
operator|)
name|container
operator|)
operator|.
name|internalAdd
argument_list|(
name|previousIndex
argument_list|()
operator|+
literal|1
argument_list|,
name|o
argument_list|)
decl_stmt|;
name|nextItem
operator|=
name|item
expr_stmt|;
block|}
block|}
end_class

end_unit
