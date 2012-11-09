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
name|ArrayList
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

begin_class
specifier|public
class|class
name|OrderedPendingList
implements|implements
name|PendingList
block|{
specifier|private
name|PendingNode
name|root
init|=
literal|null
decl_stmt|;
specifier|private
name|PendingNode
name|tail
init|=
literal|null
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
operator|new
name|PendingNode
argument_list|(
name|this
argument_list|,
name|message
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
name|node
expr_stmt|;
name|tail
operator|=
name|node
expr_stmt|;
block|}
else|else
block|{
name|root
operator|.
name|linkBefore
argument_list|(
name|node
argument_list|)
expr_stmt|;
name|root
operator|=
name|node
expr_stmt|;
block|}
name|this
operator|.
name|map
operator|.
name|put
argument_list|(
name|message
operator|.
name|getMessageId
argument_list|()
argument_list|,
name|node
argument_list|)
expr_stmt|;
return|return
name|node
return|;
block|}
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
operator|new
name|PendingNode
argument_list|(
name|this
argument_list|,
name|message
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
name|node
expr_stmt|;
block|}
else|else
block|{
name|tail
operator|.
name|linkAfter
argument_list|(
name|node
argument_list|)
expr_stmt|;
block|}
name|tail
operator|=
name|node
expr_stmt|;
name|this
operator|.
name|map
operator|.
name|put
argument_list|(
name|message
operator|.
name|getMessageId
argument_list|()
argument_list|,
name|node
argument_list|)
expr_stmt|;
return|return
name|node
return|;
block|}
specifier|public
name|void
name|clear
parameter_list|()
block|{
name|this
operator|.
name|root
operator|=
literal|null
expr_stmt|;
name|this
operator|.
name|tail
operator|=
literal|null
expr_stmt|;
name|this
operator|.
name|map
operator|.
name|clear
argument_list|()
expr_stmt|;
block|}
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
name|Iterator
argument_list|<
name|MessageReference
argument_list|>
argument_list|()
block|{
specifier|private
name|PendingNode
name|current
init|=
literal|null
decl_stmt|;
specifier|private
name|PendingNode
name|next
init|=
name|root
decl_stmt|;
specifier|public
name|boolean
name|hasNext
parameter_list|()
block|{
return|return
name|next
operator|!=
literal|null
return|;
block|}
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
name|this
operator|.
name|current
operator|=
name|this
operator|.
name|next
expr_stmt|;
name|result
operator|=
name|this
operator|.
name|current
operator|.
name|getMessage
argument_list|()
expr_stmt|;
name|this
operator|.
name|next
operator|=
operator|(
name|PendingNode
operator|)
name|this
operator|.
name|next
operator|.
name|getNext
argument_list|()
expr_stmt|;
return|return
name|result
return|;
block|}
specifier|public
name|void
name|remove
parameter_list|()
block|{
if|if
condition|(
name|this
operator|.
name|current
operator|!=
literal|null
operator|&&
name|this
operator|.
name|current
operator|.
name|getMessage
argument_list|()
operator|!=
literal|null
condition|)
block|{
name|map
operator|.
name|remove
argument_list|(
name|this
operator|.
name|current
operator|.
name|getMessage
argument_list|()
operator|.
name|getMessageId
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|removeNode
argument_list|(
name|this
operator|.
name|current
argument_list|)
expr_stmt|;
block|}
block|}
return|;
block|}
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
name|map
operator|.
name|remove
argument_list|(
name|message
operator|.
name|getMessageId
argument_list|()
argument_list|)
expr_stmt|;
name|removeNode
argument_list|(
name|node
argument_list|)
expr_stmt|;
block|}
return|return
name|node
return|;
block|}
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
name|void
name|removeNode
parameter_list|(
name|PendingNode
name|node
parameter_list|)
block|{
if|if
condition|(
name|node
operator|!=
literal|null
condition|)
block|{
name|map
operator|.
name|remove
argument_list|(
name|node
operator|.
name|getMessage
argument_list|()
operator|.
name|getMessageId
argument_list|()
argument_list|)
expr_stmt|;
if|if
condition|(
name|root
operator|==
name|node
condition|)
block|{
name|root
operator|=
operator|(
name|PendingNode
operator|)
name|node
operator|.
name|getNext
argument_list|()
expr_stmt|;
block|}
if|if
condition|(
name|tail
operator|==
name|node
condition|)
block|{
name|tail
operator|=
operator|(
name|PendingNode
operator|)
name|node
operator|.
name|getPrevious
argument_list|()
expr_stmt|;
block|}
name|node
operator|.
name|unlink
argument_list|()
expr_stmt|;
block|}
block|}
name|List
argument_list|<
name|PendingNode
argument_list|>
name|getAsList
parameter_list|()
block|{
name|List
argument_list|<
name|PendingNode
argument_list|>
name|result
init|=
operator|new
name|ArrayList
argument_list|<
name|PendingNode
argument_list|>
argument_list|(
name|size
argument_list|()
argument_list|)
decl_stmt|;
name|PendingNode
name|node
init|=
name|root
decl_stmt|;
while|while
condition|(
name|node
operator|!=
literal|null
condition|)
block|{
name|result
operator|.
name|add
argument_list|(
name|node
argument_list|)
expr_stmt|;
name|node
operator|=
operator|(
name|PendingNode
operator|)
name|node
operator|.
name|getNext
argument_list|()
expr_stmt|;
block|}
return|return
name|result
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
literal|"OrderedPendingList("
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
for|for
control|(
name|PendingNode
name|value
range|:
name|map
operator|.
name|values
argument_list|()
control|)
block|{
if|if
condition|(
name|value
operator|.
name|getMessage
argument_list|()
operator|.
name|equals
argument_list|(
name|message
argument_list|)
condition|)
block|{
return|return
literal|true
return|;
block|}
block|}
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
name|List
argument_list|<
name|MessageReference
argument_list|>
name|messageReferences
init|=
operator|new
name|ArrayList
argument_list|<
name|MessageReference
argument_list|>
argument_list|()
decl_stmt|;
for|for
control|(
name|PendingNode
name|pendingNode
range|:
name|map
operator|.
name|values
argument_list|()
control|)
block|{
name|messageReferences
operator|.
name|add
argument_list|(
name|pendingNode
operator|.
name|getMessage
argument_list|()
argument_list|)
expr_stmt|;
block|}
return|return
name|messageReferences
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
if|if
condition|(
name|pendingList
operator|!=
literal|null
condition|)
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
block|}
block|}
end_class

end_unit
