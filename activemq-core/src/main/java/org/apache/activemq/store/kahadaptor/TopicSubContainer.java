begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  *  * Licensed to the Apache Software Foundation (ASF) under one or more contributor license agreements. See the NOTICE  * file distributed with this work for additional information regarding copyright ownership. The ASF licenses this file  * to You under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the  * License. You may obtain a copy of the License at  *  * http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on  * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the  * specific language governing permissions and limitations under the License.  */
end_comment

begin_package
package|package
name|org
operator|.
name|apache
operator|.
name|activemq
operator|.
name|store
operator|.
name|kahadaptor
package|;
end_package

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|activemq
operator|.
name|command
operator|.
name|MessageId
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
name|StoreEntry
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

begin_comment
comment|/**  * Holds information for the subscriber  *  * @version $Revision: 1.10 $  */
end_comment

begin_class
specifier|public
class|class
name|TopicSubContainer
block|{
specifier|private
specifier|transient
name|ListContainer
name|listContainer
decl_stmt|;
specifier|private
specifier|transient
name|StoreEntry
name|batchEntry
decl_stmt|;
specifier|private
specifier|transient
name|String
name|lastBatchId
decl_stmt|;
specifier|public
name|TopicSubContainer
parameter_list|(
name|ListContainer
name|container
parameter_list|)
block|{
name|this
operator|.
name|listContainer
operator|=
name|container
expr_stmt|;
block|}
comment|/**      * @return the batchEntry      */
specifier|public
name|StoreEntry
name|getBatchEntry
parameter_list|()
block|{
return|return
name|this
operator|.
name|batchEntry
return|;
block|}
comment|/**      * @param batchEntry the batchEntry to set      */
specifier|public
name|void
name|setBatchEntry
parameter_list|(
name|String
name|id
parameter_list|,
name|StoreEntry
name|batchEntry
parameter_list|)
block|{
name|this
operator|.
name|lastBatchId
operator|=
name|id
expr_stmt|;
name|this
operator|.
name|batchEntry
operator|=
name|batchEntry
expr_stmt|;
block|}
specifier|public
name|void
name|reset
parameter_list|()
block|{
name|lastBatchId
operator|=
literal|null
expr_stmt|;
name|batchEntry
operator|=
literal|null
expr_stmt|;
block|}
specifier|public
name|boolean
name|isEmpty
parameter_list|()
block|{
return|return
name|listContainer
operator|.
name|isEmpty
argument_list|()
return|;
block|}
specifier|public
name|StoreEntry
name|add
parameter_list|(
name|ConsumerMessageRef
name|ref
parameter_list|)
block|{
return|return
name|listContainer
operator|.
name|placeLast
argument_list|(
name|ref
argument_list|)
return|;
block|}
specifier|public
name|ConsumerMessageRef
name|remove
parameter_list|(
name|MessageId
name|id
parameter_list|)
block|{
name|ConsumerMessageRef
name|result
init|=
literal|null
decl_stmt|;
if|if
condition|(
operator|!
name|listContainer
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
name|StoreEntry
name|entry
init|=
name|listContainer
operator|.
name|getFirst
argument_list|()
decl_stmt|;
while|while
condition|(
name|entry
operator|!=
literal|null
condition|)
block|{
name|ConsumerMessageRef
name|ref
init|=
operator|(
name|ConsumerMessageRef
operator|)
name|listContainer
operator|.
name|get
argument_list|(
name|entry
argument_list|)
decl_stmt|;
name|listContainer
operator|.
name|remove
argument_list|(
name|entry
argument_list|)
expr_stmt|;
if|if
condition|(
name|ref
operator|!=
literal|null
operator|&&
name|ref
operator|.
name|getMessageId
argument_list|()
operator|.
name|equals
argument_list|(
name|id
argument_list|)
condition|)
block|{
name|result
operator|=
name|ref
expr_stmt|;
if|if
condition|(
name|listContainer
operator|!=
literal|null
operator|&&
name|batchEntry
operator|!=
literal|null
operator|&&
operator|(
name|listContainer
operator|.
name|isEmpty
argument_list|()
operator|||
name|batchEntry
operator|.
name|equals
argument_list|(
name|entry
argument_list|)
operator|)
condition|)
block|{
name|reset
argument_list|()
expr_stmt|;
block|}
break|break;
block|}
name|entry
operator|=
name|listContainer
operator|.
name|getFirst
argument_list|()
expr_stmt|;
block|}
block|}
return|return
name|result
return|;
block|}
specifier|public
name|ConsumerMessageRef
name|get
parameter_list|(
name|StoreEntry
name|entry
parameter_list|)
block|{
return|return
operator|(
name|ConsumerMessageRef
operator|)
name|listContainer
operator|.
name|get
argument_list|(
name|entry
argument_list|)
return|;
block|}
specifier|public
name|StoreEntry
name|getEntry
parameter_list|()
block|{
return|return
name|listContainer
operator|.
name|getFirst
argument_list|()
return|;
block|}
specifier|public
name|StoreEntry
name|refreshEntry
parameter_list|(
name|StoreEntry
name|entry
parameter_list|)
block|{
return|return
name|listContainer
operator|.
name|refresh
argument_list|(
name|entry
argument_list|)
return|;
block|}
specifier|public
name|StoreEntry
name|getNextEntry
parameter_list|(
name|StoreEntry
name|entry
parameter_list|)
block|{
return|return
name|listContainer
operator|.
name|getNext
argument_list|(
name|entry
argument_list|)
return|;
block|}
specifier|public
name|Iterator
name|iterator
parameter_list|()
block|{
return|return
name|listContainer
operator|.
name|iterator
argument_list|()
return|;
block|}
specifier|public
name|int
name|size
parameter_list|()
block|{
return|return
name|listContainer
operator|.
name|size
argument_list|()
return|;
block|}
specifier|public
name|void
name|clear
parameter_list|()
block|{
name|reset
argument_list|()
expr_stmt|;
name|listContainer
operator|.
name|clear
argument_list|()
expr_stmt|;
block|}
block|}
end_class

end_unit

