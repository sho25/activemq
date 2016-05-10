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
import|import static
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
operator|.
name|OrderedPendingList
operator|.
name|getValues
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|ArrayDeque
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
name|Deque
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
name|Iterator
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
name|java
operator|.
name|util
operator|.
name|Map
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
name|management
operator|.
name|SizeStatisticImpl
import|;
end_import

begin_class
specifier|public
class|class
name|PrioritizedPendingList
implements|implements
name|PendingList
block|{
specifier|private
specifier|static
specifier|final
name|Integer
name|MAX_PRIORITY
init|=
literal|10
decl_stmt|;
specifier|private
specifier|final
name|OrderedPendingList
index|[]
name|lists
init|=
operator|new
name|OrderedPendingList
index|[
name|MAX_PRIORITY
index|]
decl_stmt|;
specifier|private
specifier|final
name|Map
argument_list|<
name|MessageId
argument_list|,
name|PendingNode
argument_list|>
name|map
init|=
operator|new
name|HashMap
argument_list|<
name|MessageId
argument_list|,
name|PendingNode
argument_list|>
argument_list|()
decl_stmt|;
specifier|private
specifier|final
name|SizeStatisticImpl
name|messageSize
decl_stmt|;
specifier|private
specifier|final
name|PendingMessageHelper
name|pendingMessageHelper
decl_stmt|;
specifier|public
name|PrioritizedPendingList
parameter_list|()
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
name|MAX_PRIORITY
condition|;
name|i
operator|++
control|)
block|{
name|this
operator|.
name|lists
index|[
name|i
index|]
operator|=
operator|new
name|OrderedPendingList
argument_list|()
expr_stmt|;
block|}
name|messageSize
operator|=
operator|new
name|SizeStatisticImpl
argument_list|(
literal|"messageSize"
argument_list|,
literal|"The size in bytes of the pending messages"
argument_list|)
expr_stmt|;
name|messageSize
operator|.
name|setEnabled
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|pendingMessageHelper
operator|=
operator|new
name|PendingMessageHelper
argument_list|(
name|map
argument_list|,
name|messageSize
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|PendingNode
name|addMessageFirst
parameter_list|(
name|MessageReference
name|message
parameter_list|)
block|{
name|PendingNode
name|node
init|=
name|getList
argument_list|(
name|message
argument_list|)
operator|.
name|addMessageFirst
argument_list|(
name|message
argument_list|)
decl_stmt|;
name|this
operator|.
name|pendingMessageHelper
operator|.
name|addToMap
argument_list|(
name|message
argument_list|,
name|node
argument_list|)
expr_stmt|;
return|return
name|node
return|;
block|}
annotation|@
name|Override
specifier|public
name|PendingNode
name|addMessageLast
parameter_list|(
name|MessageReference
name|message
parameter_list|)
block|{
name|PendingNode
name|node
init|=
name|getList
argument_list|(
name|message
argument_list|)
operator|.
name|addMessageLast
argument_list|(
name|message
argument_list|)
decl_stmt|;
name|this
operator|.
name|pendingMessageHelper
operator|.
name|addToMap
argument_list|(
name|message
argument_list|,
name|node
argument_list|)
expr_stmt|;
return|return
name|node
return|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|clear
parameter_list|()
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
name|MAX_PRIORITY
condition|;
name|i
operator|++
control|)
block|{
name|this
operator|.
name|lists
index|[
name|i
index|]
operator|.
name|clear
argument_list|()
expr_stmt|;
block|}
name|this
operator|.
name|map
operator|.
name|clear
argument_list|()
expr_stmt|;
name|this
operator|.
name|messageSize
operator|.
name|reset
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|isEmpty
parameter_list|()
block|{
return|return
name|this
operator|.
name|map
operator|.
name|isEmpty
argument_list|()
return|;
block|}
annotation|@
name|Override
specifier|public
name|Iterator
argument_list|<
name|MessageReference
argument_list|>
name|iterator
parameter_list|()
block|{
return|return
operator|new
name|PrioritizedPendingListIterator
argument_list|()
return|;
block|}
annotation|@
name|Override
specifier|public
name|PendingNode
name|remove
parameter_list|(
name|MessageReference
name|message
parameter_list|)
block|{
name|PendingNode
name|node
init|=
literal|null
decl_stmt|;
if|if
condition|(
name|message
operator|!=
literal|null
condition|)
block|{
name|node
operator|=
name|this
operator|.
name|pendingMessageHelper
operator|.
name|removeFromMap
argument_list|(
name|message
argument_list|)
expr_stmt|;
if|if
condition|(
name|node
operator|!=
literal|null
condition|)
block|{
name|node
operator|.
name|getList
argument_list|()
operator|.
name|removeNode
argument_list|(
name|node
argument_list|)
expr_stmt|;
block|}
block|}
return|return
name|node
return|;
block|}
annotation|@
name|Override
specifier|public
name|int
name|size
parameter_list|()
block|{
return|return
name|this
operator|.
name|map
operator|.
name|size
argument_list|()
return|;
block|}
annotation|@
name|Override
specifier|public
name|long
name|messageSize
parameter_list|()
block|{
return|return
name|this
operator|.
name|messageSize
operator|.
name|getTotalSize
argument_list|()
return|;
block|}
annotation|@
name|Override
specifier|public
name|String
name|toString
parameter_list|()
block|{
return|return
literal|"PrioritizedPendingList("
operator|+
name|System
operator|.
name|identityHashCode
argument_list|(
name|this
argument_list|)
operator|+
literal|")"
return|;
block|}
specifier|protected
name|int
name|getPriority
parameter_list|(
name|MessageReference
name|message
parameter_list|)
block|{
name|int
name|priority
init|=
name|javax
operator|.
name|jms
operator|.
name|Message
operator|.
name|DEFAULT_PRIORITY
decl_stmt|;
if|if
condition|(
name|message
operator|.
name|getMessageId
argument_list|()
operator|!=
literal|null
condition|)
block|{
name|priority
operator|=
name|Math
operator|.
name|max
argument_list|(
name|message
operator|.
name|getMessage
argument_list|()
operator|.
name|getPriority
argument_list|()
argument_list|,
literal|0
argument_list|)
expr_stmt|;
name|priority
operator|=
name|Math
operator|.
name|min
argument_list|(
name|priority
argument_list|,
literal|9
argument_list|)
expr_stmt|;
block|}
return|return
name|priority
return|;
block|}
specifier|protected
name|OrderedPendingList
name|getList
parameter_list|(
name|MessageReference
name|msg
parameter_list|)
block|{
return|return
name|lists
index|[
name|getPriority
argument_list|(
name|msg
argument_list|)
index|]
return|;
block|}
specifier|private
specifier|final
class|class
name|PrioritizedPendingListIterator
implements|implements
name|Iterator
argument_list|<
name|MessageReference
argument_list|>
block|{
specifier|private
specifier|final
name|Deque
argument_list|<
name|Iterator
argument_list|<
name|MessageReference
argument_list|>
argument_list|>
name|iterators
init|=
operator|new
name|ArrayDeque
argument_list|<
name|Iterator
argument_list|<
name|MessageReference
argument_list|>
argument_list|>
argument_list|()
decl_stmt|;
specifier|private
name|Iterator
argument_list|<
name|MessageReference
argument_list|>
name|current
decl_stmt|;
specifier|private
name|MessageReference
name|currentMessage
decl_stmt|;
name|PrioritizedPendingListIterator
parameter_list|()
block|{
for|for
control|(
name|OrderedPendingList
name|list
range|:
name|lists
control|)
block|{
if|if
condition|(
operator|!
name|list
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
name|iterators
operator|.
name|push
argument_list|(
name|list
operator|.
name|iterator
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
name|current
operator|=
name|iterators
operator|.
name|poll
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|hasNext
parameter_list|()
block|{
while|while
condition|(
name|current
operator|!=
literal|null
condition|)
block|{
if|if
condition|(
name|current
operator|.
name|hasNext
argument_list|()
condition|)
block|{
return|return
literal|true
return|;
block|}
else|else
block|{
name|current
operator|=
name|iterators
operator|.
name|poll
argument_list|()
expr_stmt|;
block|}
block|}
return|return
literal|false
return|;
block|}
annotation|@
name|Override
specifier|public
name|MessageReference
name|next
parameter_list|()
block|{
name|MessageReference
name|result
init|=
literal|null
decl_stmt|;
while|while
condition|(
name|current
operator|!=
literal|null
condition|)
block|{
if|if
condition|(
name|current
operator|.
name|hasNext
argument_list|()
condition|)
block|{
name|result
operator|=
name|currentMessage
operator|=
name|current
operator|.
name|next
argument_list|()
expr_stmt|;
break|break;
block|}
else|else
block|{
name|current
operator|=
name|iterators
operator|.
name|poll
argument_list|()
expr_stmt|;
block|}
block|}
return|return
name|result
return|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|remove
parameter_list|()
block|{
if|if
condition|(
name|currentMessage
operator|!=
literal|null
condition|)
block|{
name|pendingMessageHelper
operator|.
name|removeFromMap
argument_list|(
name|currentMessage
argument_list|)
expr_stmt|;
name|current
operator|.
name|remove
argument_list|()
expr_stmt|;
name|currentMessage
operator|=
literal|null
expr_stmt|;
block|}
block|}
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|contains
parameter_list|(
name|MessageReference
name|message
parameter_list|)
block|{
if|if
condition|(
name|message
operator|!=
literal|null
condition|)
block|{
return|return
name|this
operator|.
name|map
operator|.
name|containsKey
argument_list|(
name|message
operator|.
name|getMessageId
argument_list|()
argument_list|)
return|;
block|}
return|return
literal|false
return|;
block|}
annotation|@
name|Override
specifier|public
name|Collection
argument_list|<
name|MessageReference
argument_list|>
name|values
parameter_list|()
block|{
return|return
name|getValues
argument_list|(
name|this
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|addAll
parameter_list|(
name|PendingList
name|pendingList
parameter_list|)
block|{
for|for
control|(
name|MessageReference
name|messageReference
range|:
name|pendingList
control|)
block|{
name|addMessageLast
argument_list|(
name|messageReference
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Override
specifier|public
name|MessageReference
name|get
parameter_list|(
name|MessageId
name|messageId
parameter_list|)
block|{
name|PendingNode
name|node
init|=
name|map
operator|.
name|get
argument_list|(
name|messageId
argument_list|)
decl_stmt|;
if|if
condition|(
name|node
operator|!=
literal|null
condition|)
block|{
return|return
name|node
operator|.
name|getMessage
argument_list|()
return|;
block|}
return|return
literal|null
return|;
block|}
block|}
end_class

end_unit

