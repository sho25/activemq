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
name|Iterator
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
comment|/**  * Values collection iterator for the MapContainer  *   *   */
end_comment

begin_class
specifier|public
class|class
name|ContainerValueCollectionIterator
implements|implements
name|Iterator
block|{
specifier|protected
name|BaseContainerImpl
name|container
decl_stmt|;
specifier|protected
name|IndexLinkedList
name|list
decl_stmt|;
specifier|protected
name|IndexItem
name|nextItem
decl_stmt|;
specifier|protected
name|IndexItem
name|currentItem
decl_stmt|;
name|ContainerValueCollectionIterator
parameter_list|(
name|BaseContainerImpl
name|container
parameter_list|,
name|IndexLinkedList
name|list
parameter_list|,
name|IndexItem
name|start
parameter_list|)
block|{
name|this
operator|.
name|container
operator|=
name|container
expr_stmt|;
name|this
operator|.
name|list
operator|=
name|list
expr_stmt|;
name|this
operator|.
name|currentItem
operator|=
name|start
expr_stmt|;
name|this
operator|.
name|nextItem
operator|=
name|list
operator|.
name|getNextEntry
argument_list|(
operator|(
name|IndexItem
operator|)
name|list
operator|.
name|refreshEntry
argument_list|(
name|start
argument_list|)
argument_list|)
expr_stmt|;
block|}
specifier|public
name|boolean
name|hasNext
parameter_list|()
block|{
return|return
name|nextItem
operator|!=
literal|null
return|;
block|}
specifier|public
name|Object
name|next
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
name|currentItem
operator|=
name|nextItem
expr_stmt|;
name|Object
name|result
init|=
name|container
operator|.
name|getValue
argument_list|(
name|nextItem
argument_list|)
decl_stmt|;
name|nextItem
operator|=
name|list
operator|.
name|getNextEntry
argument_list|(
name|nextItem
argument_list|)
expr_stmt|;
return|return
name|result
return|;
block|}
block|}
specifier|public
name|void
name|remove
parameter_list|()
block|{
synchronized|synchronized
init|(
name|container
init|)
block|{
if|if
condition|(
name|currentItem
operator|!=
literal|null
condition|)
block|{
name|currentItem
operator|=
operator|(
name|IndexItem
operator|)
name|list
operator|.
name|refreshEntry
argument_list|(
name|currentItem
argument_list|)
expr_stmt|;
name|container
operator|.
name|remove
argument_list|(
name|currentItem
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|}
end_class

end_unit
