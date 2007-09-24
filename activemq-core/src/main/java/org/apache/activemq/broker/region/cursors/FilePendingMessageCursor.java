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
name|io
operator|.
name|IOException
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
name|LinkedList
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|concurrent
operator|.
name|atomic
operator|.
name|AtomicBoolean
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|concurrent
operator|.
name|atomic
operator|.
name|AtomicLong
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
name|Destination
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
name|Message
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
name|CommandMarshaller
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
name|Store
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
name|openwire
operator|.
name|OpenWireFormat
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
name|usage
operator|.
name|SystemUsage
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
name|usage
operator|.
name|Usage
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
name|usage
operator|.
name|UsageListener
import|;
end_import

begin_comment
comment|/**  * persist pending messages pending message (messages awaiting dispatch to a  * consumer) cursor  *   * @version $Revision$  */
end_comment

begin_class
specifier|public
class|class
name|FilePendingMessageCursor
extends|extends
name|AbstractPendingMessageCursor
implements|implements
name|UsageListener
block|{
specifier|private
specifier|static
specifier|final
name|AtomicLong
name|NAME_COUNT
init|=
operator|new
name|AtomicLong
argument_list|()
decl_stmt|;
specifier|private
name|Store
name|store
decl_stmt|;
specifier|private
name|String
name|name
decl_stmt|;
specifier|private
name|LinkedList
argument_list|<
name|MessageReference
argument_list|>
name|memoryList
init|=
operator|new
name|LinkedList
argument_list|<
name|MessageReference
argument_list|>
argument_list|()
decl_stmt|;
specifier|private
name|ListContainer
argument_list|<
name|MessageReference
argument_list|>
name|diskList
decl_stmt|;
specifier|private
name|Iterator
name|iter
decl_stmt|;
specifier|private
name|Destination
name|regionDestination
decl_stmt|;
specifier|private
name|boolean
name|iterating
decl_stmt|;
specifier|private
name|boolean
name|flushRequired
decl_stmt|;
specifier|private
name|AtomicBoolean
name|started
init|=
operator|new
name|AtomicBoolean
argument_list|()
decl_stmt|;
specifier|private
name|MessageReference
name|last
init|=
literal|null
decl_stmt|;
comment|/**      * @param name      * @param store      */
specifier|public
name|FilePendingMessageCursor
parameter_list|(
name|String
name|name
parameter_list|,
name|Store
name|store
parameter_list|)
block|{
name|this
operator|.
name|name
operator|=
name|NAME_COUNT
operator|.
name|incrementAndGet
argument_list|()
operator|+
literal|"_"
operator|+
name|name
expr_stmt|;
name|this
operator|.
name|store
operator|=
name|store
expr_stmt|;
block|}
specifier|public
name|void
name|start
parameter_list|()
block|{
if|if
condition|(
name|started
operator|.
name|compareAndSet
argument_list|(
literal|false
argument_list|,
literal|true
argument_list|)
condition|)
block|{
if|if
condition|(
name|systemUsage
operator|!=
literal|null
condition|)
block|{
name|systemUsage
operator|.
name|getMemoryUsage
argument_list|()
operator|.
name|addUsageListener
argument_list|(
name|this
argument_list|)
expr_stmt|;
block|}
block|}
block|}
specifier|public
name|void
name|stop
parameter_list|()
block|{
if|if
condition|(
name|started
operator|.
name|compareAndSet
argument_list|(
literal|true
argument_list|,
literal|false
argument_list|)
condition|)
block|{
name|gc
argument_list|()
expr_stmt|;
if|if
condition|(
name|systemUsage
operator|!=
literal|null
condition|)
block|{
name|systemUsage
operator|.
name|getMemoryUsage
argument_list|()
operator|.
name|removeUsageListener
argument_list|(
name|this
argument_list|)
expr_stmt|;
block|}
block|}
block|}
comment|/**      * @return true if there are no pending messages      */
specifier|public
specifier|synchronized
name|boolean
name|isEmpty
parameter_list|()
block|{
name|boolean
name|result
init|=
name|memoryList
operator|.
name|isEmpty
argument_list|()
operator|&&
name|isDiskListEmpty
argument_list|()
decl_stmt|;
return|return
name|result
return|;
block|}
comment|/**      * reset the cursor      */
specifier|public
specifier|synchronized
name|void
name|reset
parameter_list|()
block|{
name|iterating
operator|=
literal|true
expr_stmt|;
name|last
operator|=
literal|null
expr_stmt|;
name|iter
operator|=
name|isDiskListEmpty
argument_list|()
condition|?
name|memoryList
operator|.
name|iterator
argument_list|()
else|:
name|getDiskList
argument_list|()
operator|.
name|listIterator
argument_list|()
expr_stmt|;
block|}
specifier|public
specifier|synchronized
name|void
name|release
parameter_list|()
block|{
name|iterating
operator|=
literal|false
expr_stmt|;
if|if
condition|(
name|flushRequired
condition|)
block|{
name|flushRequired
operator|=
literal|false
expr_stmt|;
name|flushToDisk
argument_list|()
expr_stmt|;
block|}
block|}
specifier|public
specifier|synchronized
name|void
name|destroy
parameter_list|()
block|{
name|stop
argument_list|()
expr_stmt|;
for|for
control|(
name|Iterator
argument_list|<
name|MessageReference
argument_list|>
name|i
init|=
name|memoryList
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
name|Message
name|node
init|=
operator|(
name|Message
operator|)
name|i
operator|.
name|next
argument_list|()
decl_stmt|;
name|node
operator|.
name|decrementReferenceCount
argument_list|()
expr_stmt|;
block|}
name|memoryList
operator|.
name|clear
argument_list|()
expr_stmt|;
if|if
condition|(
operator|!
name|isDiskListEmpty
argument_list|()
condition|)
block|{
name|getDiskList
argument_list|()
operator|.
name|clear
argument_list|()
expr_stmt|;
block|}
block|}
specifier|public
specifier|synchronized
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
name|LinkedList
argument_list|<
name|MessageReference
argument_list|>
name|result
init|=
operator|new
name|LinkedList
argument_list|<
name|MessageReference
argument_list|>
argument_list|()
decl_stmt|;
name|int
name|count
init|=
literal|0
decl_stmt|;
for|for
control|(
name|Iterator
argument_list|<
name|MessageReference
argument_list|>
name|i
init|=
name|memoryList
operator|.
name|iterator
argument_list|()
init|;
name|i
operator|.
name|hasNext
argument_list|()
operator|&&
name|count
operator|<
name|maxItems
condition|;
control|)
block|{
name|result
operator|.
name|add
argument_list|(
name|i
operator|.
name|next
argument_list|()
argument_list|)
expr_stmt|;
name|count
operator|++
expr_stmt|;
block|}
if|if
condition|(
name|count
operator|<
name|maxItems
operator|&&
operator|!
name|isDiskListEmpty
argument_list|()
condition|)
block|{
for|for
control|(
name|Iterator
argument_list|<
name|MessageReference
argument_list|>
name|i
init|=
name|getDiskList
argument_list|()
operator|.
name|iterator
argument_list|()
init|;
name|i
operator|.
name|hasNext
argument_list|()
operator|&&
name|count
operator|<
name|maxItems
condition|;
control|)
block|{
name|Message
name|message
init|=
operator|(
name|Message
operator|)
name|i
operator|.
name|next
argument_list|()
decl_stmt|;
name|message
operator|.
name|setRegionDestination
argument_list|(
name|regionDestination
argument_list|)
expr_stmt|;
name|message
operator|.
name|incrementReferenceCount
argument_list|()
expr_stmt|;
name|result
operator|.
name|add
argument_list|(
name|message
argument_list|)
expr_stmt|;
name|count
operator|++
expr_stmt|;
block|}
block|}
return|return
name|result
return|;
block|}
comment|/**      * add message to await dispatch      *       * @param node      */
specifier|public
specifier|synchronized
name|void
name|addMessageLast
parameter_list|(
name|MessageReference
name|node
parameter_list|)
block|{
try|try
block|{
name|regionDestination
operator|=
name|node
operator|.
name|getMessage
argument_list|()
operator|.
name|getRegionDestination
argument_list|()
expr_stmt|;
if|if
condition|(
name|isSpaceInMemoryList
argument_list|()
condition|)
block|{
name|memoryList
operator|.
name|add
argument_list|(
name|node
argument_list|)
expr_stmt|;
name|node
operator|.
name|incrementReferenceCount
argument_list|()
expr_stmt|;
block|}
else|else
block|{
name|flushToDisk
argument_list|()
expr_stmt|;
name|node
operator|.
name|decrementReferenceCount
argument_list|()
expr_stmt|;
name|systemUsage
operator|.
name|getTempUsage
argument_list|()
operator|.
name|waitForSpace
argument_list|()
expr_stmt|;
name|getDiskList
argument_list|()
operator|.
name|addLast
argument_list|(
name|node
argument_list|)
expr_stmt|;
block|}
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|RuntimeException
argument_list|(
name|e
argument_list|)
throw|;
block|}
block|}
comment|/**      * add message to await dispatch      *       * @param node      */
specifier|public
specifier|synchronized
name|void
name|addMessageFirst
parameter_list|(
name|MessageReference
name|node
parameter_list|)
block|{
try|try
block|{
name|regionDestination
operator|=
name|node
operator|.
name|getMessage
argument_list|()
operator|.
name|getRegionDestination
argument_list|()
expr_stmt|;
if|if
condition|(
name|isSpaceInMemoryList
argument_list|()
condition|)
block|{
name|memoryList
operator|.
name|addFirst
argument_list|(
name|node
argument_list|)
expr_stmt|;
name|node
operator|.
name|incrementReferenceCount
argument_list|()
expr_stmt|;
block|}
else|else
block|{
name|flushToDisk
argument_list|()
expr_stmt|;
name|systemUsage
operator|.
name|getTempUsage
argument_list|()
operator|.
name|waitForSpace
argument_list|()
expr_stmt|;
name|node
operator|.
name|decrementReferenceCount
argument_list|()
expr_stmt|;
name|getDiskList
argument_list|()
operator|.
name|addFirst
argument_list|(
name|node
argument_list|)
expr_stmt|;
block|}
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|RuntimeException
argument_list|(
name|e
argument_list|)
throw|;
block|}
block|}
comment|/**      * @return true if there pending messages to dispatch      */
specifier|public
specifier|synchronized
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
specifier|synchronized
name|MessageReference
name|next
parameter_list|()
block|{
name|Message
name|message
init|=
operator|(
name|Message
operator|)
name|iter
operator|.
name|next
argument_list|()
decl_stmt|;
name|last
operator|=
name|message
expr_stmt|;
if|if
condition|(
operator|!
name|isDiskListEmpty
argument_list|()
condition|)
block|{
comment|// got from disk
name|message
operator|.
name|setRegionDestination
argument_list|(
name|regionDestination
argument_list|)
expr_stmt|;
name|message
operator|.
name|incrementReferenceCount
argument_list|()
expr_stmt|;
block|}
return|return
name|message
return|;
block|}
comment|/**      * remove the message at the cursor position      */
specifier|public
specifier|synchronized
name|void
name|remove
parameter_list|()
block|{
name|iter
operator|.
name|remove
argument_list|()
expr_stmt|;
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
block|}
comment|/**      * @param node      * @see org.apache.activemq.broker.region.cursors.AbstractPendingMessageCursor#remove(org.apache.activemq.broker.region.MessageReference)      */
specifier|public
specifier|synchronized
name|void
name|remove
parameter_list|(
name|MessageReference
name|node
parameter_list|)
block|{
if|if
condition|(
name|memoryList
operator|.
name|remove
argument_list|(
name|node
argument_list|)
condition|)
block|{
name|node
operator|.
name|decrementReferenceCount
argument_list|()
expr_stmt|;
block|}
if|if
condition|(
operator|!
name|isDiskListEmpty
argument_list|()
condition|)
block|{
name|getDiskList
argument_list|()
operator|.
name|remove
argument_list|(
name|node
argument_list|)
expr_stmt|;
block|}
block|}
comment|/**      * @return the number of pending messages      */
specifier|public
specifier|synchronized
name|int
name|size
parameter_list|()
block|{
return|return
name|memoryList
operator|.
name|size
argument_list|()
operator|+
operator|(
name|isDiskListEmpty
argument_list|()
condition|?
literal|0
else|:
name|getDiskList
argument_list|()
operator|.
name|size
argument_list|()
operator|)
return|;
block|}
comment|/**      * clear all pending messages      */
specifier|public
specifier|synchronized
name|void
name|clear
parameter_list|()
block|{
name|memoryList
operator|.
name|clear
argument_list|()
expr_stmt|;
if|if
condition|(
operator|!
name|isDiskListEmpty
argument_list|()
condition|)
block|{
name|getDiskList
argument_list|()
operator|.
name|clear
argument_list|()
expr_stmt|;
block|}
name|last
operator|=
literal|null
expr_stmt|;
block|}
specifier|public
specifier|synchronized
name|boolean
name|isFull
parameter_list|()
block|{
comment|// we always have space - as we can persist to disk
return|return
literal|false
return|;
block|}
specifier|public
name|boolean
name|hasMessagesBufferedToDeliver
parameter_list|()
block|{
return|return
operator|!
name|isEmpty
argument_list|()
return|;
block|}
specifier|public
name|void
name|setSystemUsage
parameter_list|(
name|SystemUsage
name|usageManager
parameter_list|)
block|{
name|super
operator|.
name|setSystemUsage
argument_list|(
name|usageManager
argument_list|)
expr_stmt|;
name|usageManager
operator|.
name|getMemoryUsage
argument_list|()
operator|.
name|addUsageListener
argument_list|(
name|this
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|onUsageChanged
parameter_list|(
name|Usage
name|usage
parameter_list|,
name|int
name|oldPercentUsage
parameter_list|,
name|int
name|newPercentUsage
parameter_list|)
block|{
if|if
condition|(
name|newPercentUsage
operator|>=
name|getMemoryUsageHighWaterMark
argument_list|()
condition|)
block|{
synchronized|synchronized
init|(
name|this
init|)
block|{
name|flushRequired
operator|=
literal|true
expr_stmt|;
if|if
condition|(
operator|!
name|iterating
condition|)
block|{
name|flushToDisk
argument_list|()
expr_stmt|;
name|flushRequired
operator|=
literal|false
expr_stmt|;
block|}
block|}
block|}
block|}
specifier|protected
name|boolean
name|isSpaceInMemoryList
parameter_list|()
block|{
return|return
name|hasSpace
argument_list|()
operator|&&
name|isDiskListEmpty
argument_list|()
return|;
block|}
specifier|protected
specifier|synchronized
name|void
name|flushToDisk
parameter_list|()
block|{
if|if
condition|(
operator|!
name|memoryList
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
while|while
condition|(
operator|!
name|memoryList
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
name|MessageReference
name|node
init|=
name|memoryList
operator|.
name|removeFirst
argument_list|()
decl_stmt|;
name|node
operator|.
name|decrementReferenceCount
argument_list|()
expr_stmt|;
name|getDiskList
argument_list|()
operator|.
name|addLast
argument_list|(
name|node
argument_list|)
expr_stmt|;
block|}
name|memoryList
operator|.
name|clear
argument_list|()
expr_stmt|;
block|}
block|}
specifier|protected
name|boolean
name|isDiskListEmpty
parameter_list|()
block|{
return|return
name|diskList
operator|==
literal|null
operator|||
name|diskList
operator|.
name|isEmpty
argument_list|()
return|;
block|}
specifier|protected
name|ListContainer
argument_list|<
name|MessageReference
argument_list|>
name|getDiskList
parameter_list|()
block|{
if|if
condition|(
name|diskList
operator|==
literal|null
condition|)
block|{
try|try
block|{
name|diskList
operator|=
name|store
operator|.
name|getListContainer
argument_list|(
name|name
argument_list|,
literal|"TopicSubscription"
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|diskList
operator|.
name|setMarshaller
argument_list|(
operator|new
name|CommandMarshaller
argument_list|(
operator|new
name|OpenWireFormat
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
name|e
operator|.
name|printStackTrace
argument_list|()
expr_stmt|;
throw|throw
operator|new
name|RuntimeException
argument_list|(
name|e
argument_list|)
throw|;
block|}
block|}
return|return
name|diskList
return|;
block|}
block|}
end_class

end_unit

