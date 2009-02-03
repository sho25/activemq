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
name|kahadb
operator|.
name|util
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

begin_comment
comment|/**  * Provides a list of LinkedNode objects.   *   * @author chirino  */
end_comment

begin_class
specifier|public
class|class
name|LinkedNodeList
parameter_list|<
name|T
extends|extends
name|LinkedNode
parameter_list|<
name|T
parameter_list|>
parameter_list|>
block|{
name|T
name|head
decl_stmt|;
name|int
name|size
decl_stmt|;
specifier|public
name|LinkedNodeList
parameter_list|()
block|{     }
specifier|public
name|boolean
name|isEmpty
parameter_list|()
block|{
return|return
name|head
operator|==
literal|null
return|;
block|}
specifier|public
name|void
name|addLast
parameter_list|(
name|T
name|node
parameter_list|)
block|{
name|node
operator|.
name|linkToTail
argument_list|(
name|this
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|addFirst
parameter_list|(
name|T
name|node
parameter_list|)
block|{
name|node
operator|.
name|linkToHead
argument_list|(
name|this
argument_list|)
expr_stmt|;
block|}
specifier|public
name|T
name|getHead
parameter_list|()
block|{
return|return
name|head
return|;
block|}
specifier|public
name|T
name|getTail
parameter_list|()
block|{
return|return
name|head
operator|.
name|prev
return|;
block|}
specifier|public
name|void
name|clear
parameter_list|()
block|{
while|while
condition|(
name|head
operator|!=
literal|null
condition|)
block|{
name|head
operator|.
name|unlink
argument_list|()
expr_stmt|;
block|}
block|}
specifier|public
name|void
name|addLast
parameter_list|(
name|LinkedNodeList
argument_list|<
name|T
argument_list|>
name|list
parameter_list|)
block|{
if|if
condition|(
name|list
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
return|return;
block|}
if|if
condition|(
name|head
operator|==
literal|null
condition|)
block|{
name|head
operator|=
name|list
operator|.
name|head
expr_stmt|;
name|reparent
argument_list|(
name|list
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|getTail
argument_list|()
operator|.
name|linkAfter
argument_list|(
name|list
argument_list|)
expr_stmt|;
block|}
block|}
specifier|public
name|void
name|addFirst
parameter_list|(
name|LinkedNodeList
argument_list|<
name|T
argument_list|>
name|list
parameter_list|)
block|{
if|if
condition|(
name|list
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
return|return;
block|}
if|if
condition|(
name|head
operator|==
literal|null
condition|)
block|{
name|reparent
argument_list|(
name|list
argument_list|)
expr_stmt|;
name|head
operator|=
name|list
operator|.
name|head
expr_stmt|;
name|list
operator|.
name|head
operator|=
literal|null
expr_stmt|;
block|}
else|else
block|{
name|getHead
argument_list|()
operator|.
name|linkBefore
argument_list|(
name|list
argument_list|)
expr_stmt|;
block|}
block|}
specifier|public
name|T
name|reparent
parameter_list|(
name|LinkedNodeList
argument_list|<
name|T
argument_list|>
name|list
parameter_list|)
block|{
name|size
operator|+=
name|list
operator|.
name|size
expr_stmt|;
name|T
name|n
init|=
name|list
operator|.
name|head
decl_stmt|;
do|do
block|{
name|n
operator|.
name|list
operator|=
name|this
expr_stmt|;
name|n
operator|=
name|n
operator|.
name|next
expr_stmt|;
block|}
do|while
condition|(
name|n
operator|!=
name|list
operator|.
name|head
condition|)
do|;
name|list
operator|.
name|head
operator|=
literal|null
expr_stmt|;
name|list
operator|.
name|size
operator|=
literal|0
expr_stmt|;
return|return
name|n
return|;
block|}
comment|/**      * Move the head to the tail and returns the new head node.      *       * @return      */
specifier|public
name|T
name|rotate
parameter_list|()
block|{
if|if
condition|(
name|head
operator|==
literal|null
condition|)
return|return
literal|null
return|;
return|return
name|head
operator|=
name|head
operator|.
name|getNextCircular
argument_list|()
return|;
block|}
comment|/**      * Move the head to the tail and returns the new head node.      *       * @return      */
specifier|public
name|void
name|rotateTo
parameter_list|(
name|T
name|head
parameter_list|)
block|{
assert|assert
name|head
operator|!=
literal|null
operator|:
literal|"Cannot rotate to a null head"
assert|;
assert|assert
name|head
operator|.
name|list
operator|==
name|this
operator|:
literal|"Cannot rotate to a node not linked to this list"
assert|;
name|this
operator|.
name|head
operator|=
name|head
expr_stmt|;
block|}
specifier|public
name|int
name|size
parameter_list|()
block|{
return|return
name|size
return|;
block|}
annotation|@
name|Override
specifier|public
name|String
name|toString
parameter_list|()
block|{
name|StringBuilder
name|sb
init|=
operator|new
name|StringBuilder
argument_list|()
decl_stmt|;
name|sb
operator|.
name|append
argument_list|(
literal|"["
argument_list|)
expr_stmt|;
name|boolean
name|first
init|=
literal|true
decl_stmt|;
name|T
name|cur
init|=
name|getHead
argument_list|()
decl_stmt|;
while|while
condition|(
name|cur
operator|!=
literal|null
condition|)
block|{
if|if
condition|(
operator|!
name|first
condition|)
block|{
name|sb
operator|.
name|append
argument_list|(
literal|", "
argument_list|)
expr_stmt|;
block|}
name|sb
operator|.
name|append
argument_list|(
name|cur
argument_list|)
expr_stmt|;
name|first
operator|=
literal|false
expr_stmt|;
name|cur
operator|=
name|cur
operator|.
name|getNext
argument_list|()
expr_stmt|;
block|}
name|sb
operator|.
name|append
argument_list|(
literal|"]"
argument_list|)
expr_stmt|;
return|return
name|sb
operator|.
name|toString
argument_list|()
return|;
block|}
comment|/**      * Copies the nodes of the LinkedNodeList to an ArrayList.      * @return      */
specifier|public
name|ArrayList
argument_list|<
name|T
argument_list|>
name|toArrayList
parameter_list|()
block|{
name|ArrayList
argument_list|<
name|T
argument_list|>
name|rc
init|=
operator|new
name|ArrayList
argument_list|<
name|T
argument_list|>
argument_list|(
name|size
argument_list|)
decl_stmt|;
name|T
name|cur
init|=
name|head
decl_stmt|;
while|while
condition|(
name|cur
operator|!=
literal|null
condition|)
block|{
name|rc
operator|.
name|add
argument_list|(
name|cur
argument_list|)
expr_stmt|;
name|cur
operator|=
name|cur
operator|.
name|getNext
argument_list|()
expr_stmt|;
block|}
return|return
name|rc
return|;
block|}
block|}
end_class

end_unit

