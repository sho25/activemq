begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  *   * Copyright 2005 LogicBlaze, Inc. http://www.logicblaze.com  *   * Licensed under the Apache License, Version 2.0 (the "License");   * you may not use this file except in compliance with the License.   * You may obtain a copy of the License at   *   * http://www.apache.org/licenses/LICENSE-2.0  *   * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,   * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.   * See the License for the specific language governing permissions and   * limitations under the License.   *   **/
end_comment

begin_package
package|package
name|org
operator|.
name|activemq
operator|.
name|memory
operator|.
name|list
package|;
end_package

begin_import
import|import
name|org
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
name|activemq
operator|.
name|broker
operator|.
name|region
operator|.
name|Subscription
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
name|LinkedList
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

begin_comment
comment|/**  * A simple fixed size {@link MessageList} where there is a single, fixed size  * list that all messages are added to for simplicity. Though this  * will lead to possibly slow recovery times as many more messages  * than is necessary will have to be iterated through for each subscription.  *   * @version $Revision: 1.1 $  */
end_comment

begin_class
specifier|public
class|class
name|SimpleMessageList
implements|implements
name|MessageList
block|{
specifier|private
name|LinkedList
name|list
init|=
operator|new
name|LinkedList
argument_list|()
decl_stmt|;
specifier|private
name|int
name|maximumSize
init|=
literal|100
operator|*
literal|64
operator|*
literal|1024
decl_stmt|;
specifier|private
name|int
name|size
decl_stmt|;
specifier|private
name|Object
name|lock
init|=
operator|new
name|Object
argument_list|()
decl_stmt|;
specifier|public
name|SimpleMessageList
parameter_list|()
block|{     }
specifier|public
name|SimpleMessageList
parameter_list|(
name|int
name|maximumSize
parameter_list|)
block|{
name|this
operator|.
name|maximumSize
operator|=
name|maximumSize
expr_stmt|;
block|}
specifier|public
name|void
name|add
parameter_list|(
name|MessageReference
name|node
parameter_list|)
block|{
name|int
name|delta
init|=
name|node
operator|.
name|getMessageHardRef
argument_list|()
operator|.
name|getSize
argument_list|()
decl_stmt|;
synchronized|synchronized
init|(
name|lock
init|)
block|{
name|list
operator|.
name|add
argument_list|(
name|node
argument_list|)
expr_stmt|;
name|size
operator|+=
name|delta
expr_stmt|;
while|while
condition|(
name|size
operator|>
name|maximumSize
condition|)
block|{
name|MessageReference
name|evicted
init|=
operator|(
name|MessageReference
operator|)
name|list
operator|.
name|removeFirst
argument_list|()
decl_stmt|;
name|size
operator|-=
name|evicted
operator|.
name|getMessageHardRef
argument_list|()
operator|.
name|getSize
argument_list|()
expr_stmt|;
block|}
block|}
block|}
specifier|public
name|List
name|getMessages
parameter_list|(
name|Subscription
name|sub
parameter_list|)
block|{
return|return
name|getList
argument_list|()
return|;
block|}
comment|/**      * Returns a copy of the list      */
specifier|public
name|List
name|getList
parameter_list|()
block|{
synchronized|synchronized
init|(
name|lock
init|)
block|{
return|return
operator|new
name|ArrayList
argument_list|(
name|list
argument_list|)
return|;
block|}
block|}
specifier|public
name|int
name|getSize
parameter_list|()
block|{
synchronized|synchronized
init|(
name|lock
init|)
block|{
return|return
name|size
return|;
block|}
block|}
specifier|public
name|void
name|clear
parameter_list|()
block|{
synchronized|synchronized
init|(
name|lock
init|)
block|{
name|list
operator|.
name|clear
argument_list|()
expr_stmt|;
name|size
operator|=
literal|0
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

