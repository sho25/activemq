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
name|ArrayList
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

begin_comment
comment|/**  * A {@link MessageBuffer} which evicts from the largest buffers first.  *   *   */
end_comment

begin_class
specifier|public
class|class
name|SizeBasedMessageBuffer
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
name|List
argument_list|<
name|MessageQueue
argument_list|>
name|bubbleList
init|=
operator|new
name|ArrayList
argument_list|<
name|MessageQueue
argument_list|>
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
name|SizeBasedMessageBuffer
parameter_list|()
block|{     }
specifier|public
name|SizeBasedMessageBuffer
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
name|MessageQueue
name|queue
init|=
operator|new
name|MessageQueue
argument_list|(
name|this
argument_list|)
decl_stmt|;
synchronized|synchronized
init|(
name|lock
init|)
block|{
name|queue
operator|.
name|setPosition
argument_list|(
name|bubbleList
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|bubbleList
operator|.
name|add
argument_list|(
name|queue
argument_list|)
expr_stmt|;
block|}
return|return
name|queue
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
name|bubbleUp
argument_list|(
name|queue
argument_list|,
name|queueSize
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
name|bubbleList
operator|.
name|get
argument_list|(
literal|0
argument_list|)
decl_stmt|;
name|size
operator|-=
name|biggest
operator|.
name|evictMessage
argument_list|()
expr_stmt|;
name|bubbleDown
argument_list|(
name|biggest
argument_list|,
literal|0
argument_list|)
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
argument_list|<
name|MessageQueue
argument_list|>
name|iter
init|=
name|bubbleList
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
specifier|protected
name|void
name|bubbleUp
parameter_list|(
name|MessageQueue
name|queue
parameter_list|,
name|int
name|queueSize
parameter_list|)
block|{
comment|// lets bubble up to head of queueif we need to
name|int
name|position
init|=
name|queue
operator|.
name|getPosition
argument_list|()
decl_stmt|;
while|while
condition|(
operator|--
name|position
operator|>=
literal|0
condition|)
block|{
name|MessageQueue
name|pivot
init|=
name|bubbleList
operator|.
name|get
argument_list|(
name|position
argument_list|)
decl_stmt|;
if|if
condition|(
name|pivot
operator|.
name|getSize
argument_list|()
operator|<
name|queueSize
condition|)
block|{
name|swap
argument_list|(
name|position
argument_list|,
name|pivot
argument_list|,
name|position
operator|+
literal|1
argument_list|,
name|queue
argument_list|)
expr_stmt|;
block|}
else|else
block|{
break|break;
block|}
block|}
block|}
specifier|protected
name|void
name|bubbleDown
parameter_list|(
name|MessageQueue
name|biggest
parameter_list|,
name|int
name|position
parameter_list|)
block|{
name|int
name|queueSize
init|=
name|biggest
operator|.
name|getSize
argument_list|()
decl_stmt|;
name|int
name|end
init|=
name|bubbleList
operator|.
name|size
argument_list|()
decl_stmt|;
for|for
control|(
name|int
name|second
init|=
name|position
operator|+
literal|1
init|;
name|second
operator|<
name|end
condition|;
name|second
operator|++
control|)
block|{
name|MessageQueue
name|pivot
init|=
name|bubbleList
operator|.
name|get
argument_list|(
name|second
argument_list|)
decl_stmt|;
if|if
condition|(
name|pivot
operator|.
name|getSize
argument_list|()
operator|>
name|queueSize
condition|)
block|{
name|swap
argument_list|(
name|position
argument_list|,
name|biggest
argument_list|,
name|second
argument_list|,
name|pivot
argument_list|)
expr_stmt|;
block|}
else|else
block|{
break|break;
block|}
name|position
operator|=
name|second
expr_stmt|;
block|}
block|}
specifier|protected
name|void
name|swap
parameter_list|(
name|int
name|firstPosition
parameter_list|,
name|MessageQueue
name|first
parameter_list|,
name|int
name|secondPosition
parameter_list|,
name|MessageQueue
name|second
parameter_list|)
block|{
name|bubbleList
operator|.
name|set
argument_list|(
name|firstPosition
argument_list|,
name|second
argument_list|)
expr_stmt|;
name|bubbleList
operator|.
name|set
argument_list|(
name|secondPosition
argument_list|,
name|first
argument_list|)
expr_stmt|;
name|first
operator|.
name|setPosition
argument_list|(
name|secondPosition
argument_list|)
expr_stmt|;
name|second
operator|.
name|setPosition
argument_list|(
name|firstPosition
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

