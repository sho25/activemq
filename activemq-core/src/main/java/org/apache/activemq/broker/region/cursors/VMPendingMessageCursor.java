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
name|broker
operator|.
name|region
operator|.
name|cursors
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
name|broker
operator|.
name|region
operator|.
name|MessageReference
import|;
end_import

begin_comment
comment|/**  * hold pending messages in a linked list (messages awaiting disptach to a  * consumer) cursor  *   * @version $Revision$  */
end_comment

begin_class
specifier|public
class|class
name|VMPendingMessageCursor
extends|extends
name|AbstractPendingMessageCursor
block|{
specifier|private
name|LinkedList
argument_list|<
name|MessageReference
argument_list|>
name|list
init|=
operator|new
name|LinkedList
argument_list|<
name|MessageReference
argument_list|>
argument_list|()
decl_stmt|;
specifier|private
name|Iterator
argument_list|<
name|MessageReference
argument_list|>
name|iter
decl_stmt|;
specifier|private
name|MessageReference
name|last
decl_stmt|;
comment|/**      * @return true if there are no pending messages      */
specifier|public
name|boolean
name|isEmpty
parameter_list|()
block|{
if|if
condition|(
name|list
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
return|return
literal|true
return|;
block|}
else|else
block|{
for|for
control|(
name|Iterator
argument_list|<
name|MessageReference
argument_list|>
name|iterator
init|=
name|list
operator|.
name|iterator
argument_list|()
init|;
name|iterator
operator|.
name|hasNext
argument_list|()
condition|;
control|)
block|{
name|MessageReference
name|node
init|=
name|iterator
operator|.
name|next
argument_list|()
decl_stmt|;
if|if
condition|(
operator|!
name|node
operator|.
name|isDropped
argument_list|()
condition|)
block|{
return|return
literal|false
return|;
block|}
comment|// We can remove dropped references.
name|iterator
operator|.
name|remove
argument_list|()
expr_stmt|;
block|}
return|return
literal|true
return|;
block|}
block|}
comment|/**      * reset the cursor      */
specifier|public
name|void
name|reset
parameter_list|()
block|{
name|iter
operator|=
name|list
operator|.
name|listIterator
argument_list|()
expr_stmt|;
name|last
operator|=
literal|null
expr_stmt|;
block|}
comment|/**      * add message to await dispatch      *       * @param node      */
specifier|public
name|void
name|addMessageLast
parameter_list|(
name|MessageReference
name|node
parameter_list|)
block|{
name|node
operator|.
name|incrementReferenceCount
argument_list|()
expr_stmt|;
name|list
operator|.
name|addLast
argument_list|(
name|node
argument_list|)
expr_stmt|;
block|}
comment|/**      * add message to await dispatch      *       * @param position      * @param node      */
specifier|public
name|void
name|addMessageFirst
parameter_list|(
name|MessageReference
name|node
parameter_list|)
block|{
name|node
operator|.
name|incrementReferenceCount
argument_list|()
expr_stmt|;
name|list
operator|.
name|addFirst
argument_list|(
name|node
argument_list|)
expr_stmt|;
block|}
comment|/**      * @return true if there pending messages to dispatch      */
specifier|public
name|boolean
name|hasNext
parameter_list|()
block|{
return|return
name|iter
operator|.
name|hasNext
argument_list|()
return|;
block|}
comment|/**      * @return the next pending message      */
specifier|public
name|MessageReference
name|next
parameter_list|()
block|{
name|last
operator|=
operator|(
name|MessageReference
operator|)
name|iter
operator|.
name|next
argument_list|()
expr_stmt|;
return|return
name|last
return|;
block|}
comment|/**      * remove the message at the cursor position      */
specifier|public
name|void
name|remove
parameter_list|()
block|{
if|if
condition|(
name|last
operator|!=
literal|null
condition|)
block|{
name|last
operator|.
name|decrementReferenceCount
argument_list|()
expr_stmt|;
block|}
name|iter
operator|.
name|remove
argument_list|()
expr_stmt|;
block|}
comment|/**      * @return the number of pending messages      */
specifier|public
name|int
name|size
parameter_list|()
block|{
return|return
name|list
operator|.
name|size
argument_list|()
return|;
block|}
comment|/**      * clear all pending messages      */
specifier|public
name|void
name|clear
parameter_list|()
block|{
name|list
operator|.
name|clear
argument_list|()
expr_stmt|;
block|}
specifier|public
name|void
name|remove
parameter_list|(
name|MessageReference
name|node
parameter_list|)
block|{
for|for
control|(
name|Iterator
argument_list|<
name|MessageReference
argument_list|>
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
name|MessageReference
name|ref
init|=
name|i
operator|.
name|next
argument_list|()
decl_stmt|;
if|if
condition|(
name|node
operator|.
name|getMessageId
argument_list|()
operator|.
name|equals
argument_list|(
name|ref
operator|.
name|getMessageId
argument_list|()
argument_list|)
condition|)
block|{
name|ref
operator|.
name|decrementReferenceCount
argument_list|()
expr_stmt|;
name|i
operator|.
name|remove
argument_list|()
expr_stmt|;
break|break;
block|}
block|}
block|}
comment|/**      * Page in a restricted number of messages      *       * @param maxItems      * @return a list of paged in messages      */
specifier|public
name|LinkedList
argument_list|<
name|MessageReference
argument_list|>
name|pageInList
parameter_list|(
name|int
name|maxItems
parameter_list|)
block|{
return|return
name|list
return|;
block|}
block|}
end_class

end_unit

