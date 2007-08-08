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
name|memory
operator|.
name|buffer
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

begin_comment
comment|/**  * A {@link MessageBuffer} which evicts messages in arrival order so the oldest  * messages are removed first.  *   * @version $Revision: 1.1 $  */
end_comment

begin_class
specifier|public
class|class
name|OrderBasedMessageBuffer
implements|implements
name|MessageBuffer
block|{
specifier|private
name|int
name|limit
init|=
literal|100
operator|*
literal|64
operator|*
literal|1024
decl_stmt|;
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
name|OrderBasedMessageBuffer
parameter_list|()
block|{     }
specifier|public
name|OrderBasedMessageBuffer
parameter_list|(
name|int
name|limit
parameter_list|)
block|{
name|this
operator|.
name|limit
operator|=
name|limit
expr_stmt|;
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
comment|/**      * Creates a new message queue instance      */
specifier|public
name|MessageQueue
name|createMessageQueue
parameter_list|()
block|{
return|return
operator|new
name|MessageQueue
argument_list|(
name|this
argument_list|)
return|;
block|}
comment|/**      * After a message queue has changed we may need to perform some evictions      *       * @param delta      * @param queueSize      */
specifier|public
name|void
name|onSizeChanged
parameter_list|(
name|MessageQueue
name|queue
parameter_list|,
name|int
name|delta
parameter_list|,
name|int
name|queueSize
parameter_list|)
block|{
synchronized|synchronized
init|(
name|lock
init|)
block|{
name|list
operator|.
name|addLast
argument_list|(
name|queue
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
name|limit
condition|)
block|{
name|MessageQueue
name|biggest
init|=
operator|(
name|MessageQueue
operator|)
name|list
operator|.
name|removeFirst
argument_list|()
decl_stmt|;
name|size
operator|-=
name|biggest
operator|.
name|evictMessage
argument_list|()
expr_stmt|;
block|}
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
for|for
control|(
name|Iterator
name|iter
init|=
name|list
operator|.
name|iterator
argument_list|()
init|;
name|iter
operator|.
name|hasNext
argument_list|()
condition|;
control|)
block|{
name|MessageQueue
name|queue
init|=
operator|(
name|MessageQueue
operator|)
name|iter
operator|.
name|next
argument_list|()
decl_stmt|;
name|queue
operator|.
name|clear
argument_list|()
expr_stmt|;
block|}
name|size
operator|=
literal|0
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

