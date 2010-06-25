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
name|Broker
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
name|ConnectionContext
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
name|broker
operator|.
name|region
operator|.
name|QueueMessageReference
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
name|filter
operator|.
name|NonCachedMessageEvaluationContext
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
name|store
operator|.
name|kahadb
operator|.
name|plist
operator|.
name|PList
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
name|store
operator|.
name|kahadb
operator|.
name|plist
operator|.
name|PListEntry
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
name|store
operator|.
name|kahadb
operator|.
name|plist
operator|.
name|PListStore
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

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|activemq
operator|.
name|wireformat
operator|.
name|WireFormat
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|commons
operator|.
name|logging
operator|.
name|Log
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|commons
operator|.
name|logging
operator|.
name|LogFactory
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|kahadb
operator|.
name|util
operator|.
name|ByteSequence
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
specifier|static
specifier|final
name|Log
name|LOG
init|=
name|LogFactory
operator|.
name|getLog
argument_list|(
name|FilePendingMessageCursor
operator|.
name|class
argument_list|)
decl_stmt|;
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
specifier|protected
name|Broker
name|broker
decl_stmt|;
specifier|private
specifier|final
name|PListStore
name|store
decl_stmt|;
specifier|private
specifier|final
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
name|PList
name|diskList
decl_stmt|;
specifier|private
name|Iterator
argument_list|<
name|MessageReference
argument_list|>
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
specifier|final
name|AtomicBoolean
name|started
init|=
operator|new
name|AtomicBoolean
argument_list|()
decl_stmt|;
specifier|private
specifier|final
name|WireFormat
name|wireFormat
init|=
operator|new
name|OpenWireFormat
argument_list|()
decl_stmt|;
comment|/**      * @param broker      * @param name      * @param prioritizedMessages       * @param store      */
specifier|public
name|FilePendingMessageCursor
parameter_list|(
name|Broker
name|broker
parameter_list|,
name|String
name|name
parameter_list|,
name|boolean
name|prioritizedMessages
parameter_list|)
block|{
name|super
argument_list|(
name|prioritizedMessages
argument_list|)
expr_stmt|;
name|this
operator|.
name|useCache
operator|=
literal|false
expr_stmt|;
name|this
operator|.
name|broker
operator|=
name|broker
expr_stmt|;
comment|// the store can be null if the BrokerService has persistence
comment|// turned off
name|this
operator|.
name|store
operator|=
name|broker
operator|.
name|getTempDataStore
argument_list|()
expr_stmt|;
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
block|}
annotation|@
name|Override
specifier|public
name|void
name|start
parameter_list|()
throws|throws
name|Exception
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
name|super
operator|.
name|start
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
name|addUsageListener
argument_list|(
name|this
argument_list|)
expr_stmt|;
block|}
block|}
block|}
annotation|@
name|Override
specifier|public
name|void
name|stop
parameter_list|()
throws|throws
name|Exception
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
name|super
operator|.
name|stop
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
annotation|@
name|Override
specifier|public
specifier|synchronized
name|boolean
name|isEmpty
parameter_list|()
block|{
if|if
condition|(
name|memoryList
operator|.
name|isEmpty
argument_list|()
operator|&&
name|isDiskListEmpty
argument_list|()
condition|)
block|{
return|return
literal|true
return|;
block|}
for|for
control|(
name|Iterator
argument_list|<
name|MessageReference
argument_list|>
name|iterator
init|=
name|memoryList
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
name|node
operator|==
name|QueueMessageReference
operator|.
name|NULL_MESSAGE
condition|)
block|{
continue|continue;
block|}
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
name|isDiskListEmpty
argument_list|()
return|;
block|}
comment|/**      * reset the cursor      */
annotation|@
name|Override
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
if|if
condition|(
name|isDiskListEmpty
argument_list|()
condition|)
block|{
name|this
operator|.
name|iter
operator|=
name|this
operator|.
name|memoryList
operator|.
name|iterator
argument_list|()
expr_stmt|;
block|}
else|else
block|{
name|this
operator|.
name|iter
operator|=
operator|new
name|DiskIterator
argument_list|()
expr_stmt|;
block|}
block|}
annotation|@
name|Override
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
annotation|@
name|Override
specifier|public
specifier|synchronized
name|void
name|destroy
parameter_list|()
throws|throws
name|Exception
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
name|destroyDiskList
argument_list|()
expr_stmt|;
block|}
specifier|private
name|void
name|destroyDiskList
parameter_list|()
throws|throws
name|Exception
block|{
if|if
condition|(
operator|!
name|isDiskListEmpty
argument_list|()
condition|)
block|{
name|store
operator|.
name|removePList
argument_list|(
name|name
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Override
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
name|MessageReference
name|ref
init|=
name|i
operator|.
name|next
argument_list|()
decl_stmt|;
name|ref
operator|.
name|incrementReferenceCount
argument_list|()
expr_stmt|;
name|result
operator|.
name|add
argument_list|(
name|ref
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
operator|new
name|DiskIterator
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
name|setMemoryUsage
argument_list|(
name|this
operator|.
name|getSystemUsage
argument_list|()
operator|.
name|getMemoryUsage
argument_list|()
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
comment|/**      * add message to await dispatch      *       * @param node      * @throws Exception       */
annotation|@
name|Override
specifier|public
specifier|synchronized
name|void
name|addMessageLast
parameter_list|(
name|MessageReference
name|node
parameter_list|)
throws|throws
name|Exception
block|{
name|tryAddMessageLast
argument_list|(
name|node
argument_list|,
literal|0
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
specifier|synchronized
name|boolean
name|tryAddMessageLast
parameter_list|(
name|MessageReference
name|node
parameter_list|,
name|long
name|maxWaitTime
parameter_list|)
throws|throws
name|Exception
block|{
if|if
condition|(
operator|!
name|node
operator|.
name|isExpired
argument_list|()
condition|)
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
name|isDiskListEmpty
argument_list|()
condition|)
block|{
if|if
condition|(
name|hasSpace
argument_list|()
operator|||
name|this
operator|.
name|store
operator|==
literal|null
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
return|return
literal|true
return|;
block|}
block|}
if|if
condition|(
operator|!
name|hasSpace
argument_list|()
condition|)
block|{
if|if
condition|(
name|isDiskListEmpty
argument_list|()
condition|)
block|{
name|expireOldMessages
argument_list|()
expr_stmt|;
if|if
condition|(
name|hasSpace
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
return|return
literal|true
return|;
block|}
else|else
block|{
name|flushToDisk
argument_list|()
expr_stmt|;
block|}
block|}
block|}
if|if
condition|(
name|systemUsage
operator|.
name|getTempUsage
argument_list|()
operator|.
name|waitForSpace
argument_list|(
name|maxWaitTime
argument_list|)
condition|)
block|{
name|ByteSequence
name|bs
init|=
name|getByteSequence
argument_list|(
name|node
operator|.
name|getMessage
argument_list|()
argument_list|)
decl_stmt|;
name|getDiskList
argument_list|()
operator|.
name|addLast
argument_list|(
name|node
operator|.
name|getMessageId
argument_list|()
operator|.
name|toString
argument_list|()
argument_list|,
name|bs
argument_list|)
expr_stmt|;
return|return
literal|true
return|;
block|}
return|return
literal|false
return|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
name|LOG
operator|.
name|error
argument_list|(
literal|"Caught an Exception adding a message: "
operator|+
name|node
operator|+
literal|" first to FilePendingMessageCursor "
argument_list|,
name|e
argument_list|)
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
else|else
block|{
name|discard
argument_list|(
name|node
argument_list|)
expr_stmt|;
block|}
return|return
literal|false
return|;
block|}
comment|/**      * add message to await dispatch      *       * @param node      */
annotation|@
name|Override
specifier|public
specifier|synchronized
name|void
name|addMessageFirst
parameter_list|(
name|MessageReference
name|node
parameter_list|)
block|{
if|if
condition|(
operator|!
name|node
operator|.
name|isExpired
argument_list|()
condition|)
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
name|isDiskListEmpty
argument_list|()
condition|)
block|{
if|if
condition|(
name|hasSpace
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
return|return;
block|}
block|}
if|if
condition|(
operator|!
name|hasSpace
argument_list|()
condition|)
block|{
if|if
condition|(
name|isDiskListEmpty
argument_list|()
condition|)
block|{
name|expireOldMessages
argument_list|()
expr_stmt|;
if|if
condition|(
name|hasSpace
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
return|return;
block|}
else|else
block|{
name|flushToDisk
argument_list|()
expr_stmt|;
block|}
block|}
block|}
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
name|ByteSequence
name|bs
init|=
name|getByteSequence
argument_list|(
name|node
operator|.
name|getMessage
argument_list|()
argument_list|)
decl_stmt|;
name|getDiskList
argument_list|()
operator|.
name|addFirst
argument_list|(
name|node
operator|.
name|getMessageId
argument_list|()
operator|.
name|toString
argument_list|()
argument_list|,
name|bs
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
name|LOG
operator|.
name|error
argument_list|(
literal|"Caught an Exception adding a message: "
operator|+
name|node
operator|+
literal|" first to FilePendingMessageCursor "
argument_list|,
name|e
argument_list|)
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
else|else
block|{
name|discard
argument_list|(
name|node
argument_list|)
expr_stmt|;
block|}
block|}
comment|/**      * @return true if there pending messages to dispatch      */
annotation|@
name|Override
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
annotation|@
name|Override
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
name|setMemoryUsage
argument_list|(
name|this
operator|.
name|getSystemUsage
argument_list|()
operator|.
name|getMemoryUsage
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|message
operator|.
name|incrementReferenceCount
argument_list|()
expr_stmt|;
return|return
name|message
return|;
block|}
comment|/**      * remove the message at the cursor position      */
annotation|@
name|Override
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
annotation|@
name|Override
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
try|try
block|{
name|getDiskList
argument_list|()
operator|.
name|remove
argument_list|(
name|node
operator|.
name|getMessageId
argument_list|()
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
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
block|}
comment|/**      * @return the number of pending messages      */
annotation|@
name|Override
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
annotation|@
name|Override
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
try|try
block|{
name|getDiskList
argument_list|()
operator|.
name|destroy
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
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
name|last
operator|=
literal|null
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
specifier|synchronized
name|boolean
name|isFull
parameter_list|()
block|{
return|return
name|super
operator|.
name|isFull
argument_list|()
operator|||
operator|(
name|systemUsage
operator|!=
literal|null
operator|&&
name|systemUsage
operator|.
name|getTempUsage
argument_list|()
operator|.
name|isFull
argument_list|()
operator|)
return|;
block|}
annotation|@
name|Override
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
annotation|@
name|Override
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
name|expireOldMessages
argument_list|()
expr_stmt|;
if|if
condition|(
operator|!
name|hasSpace
argument_list|()
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
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|isTransient
parameter_list|()
block|{
return|return
literal|true
return|;
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
name|expireOldMessages
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
name|LinkedList
argument_list|<
name|MessageReference
argument_list|>
name|tmpList
init|=
operator|new
name|LinkedList
argument_list|<
name|MessageReference
argument_list|>
argument_list|(
name|this
operator|.
name|memoryList
argument_list|)
decl_stmt|;
name|this
operator|.
name|memoryList
operator|=
operator|new
name|LinkedList
argument_list|<
name|MessageReference
argument_list|>
argument_list|()
expr_stmt|;
while|while
condition|(
operator|!
name|tmpList
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
name|MessageReference
name|node
init|=
name|tmpList
operator|.
name|removeFirst
argument_list|()
decl_stmt|;
if|if
condition|(
name|node
operator|.
name|isExpired
argument_list|()
condition|)
block|{
name|discard
argument_list|(
name|node
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|memoryList
operator|.
name|add
argument_list|(
name|node
argument_list|)
expr_stmt|;
block|}
block|}
block|}
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
name|ByteSequence
name|bs
decl_stmt|;
try|try
block|{
name|bs
operator|=
name|getByteSequence
argument_list|(
name|node
operator|.
name|getMessage
argument_list|()
argument_list|)
expr_stmt|;
name|getDiskList
argument_list|()
operator|.
name|addLast
argument_list|(
name|node
operator|.
name|getMessageId
argument_list|()
operator|.
name|toString
argument_list|()
argument_list|,
name|bs
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
name|LOG
operator|.
name|error
argument_list|(
literal|"Failed to write to disk list"
argument_list|,
name|e
argument_list|)
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
name|PList
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
name|getPList
argument_list|(
name|name
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
name|LOG
operator|.
name|error
argument_list|(
literal|"Caught an IO Exception getting the DiskList "
operator|+
name|name
argument_list|,
name|e
argument_list|)
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
specifier|protected
name|void
name|discard
parameter_list|(
name|MessageReference
name|message
parameter_list|)
block|{
name|message
operator|.
name|decrementReferenceCount
argument_list|()
expr_stmt|;
if|if
condition|(
name|LOG
operator|.
name|isDebugEnabled
argument_list|()
condition|)
block|{
name|LOG
operator|.
name|debug
argument_list|(
literal|"Discarding message "
operator|+
name|message
argument_list|)
expr_stmt|;
block|}
name|broker
operator|.
name|getRoot
argument_list|()
operator|.
name|sendToDeadLetterQueue
argument_list|(
operator|new
name|ConnectionContext
argument_list|(
operator|new
name|NonCachedMessageEvaluationContext
argument_list|()
argument_list|)
argument_list|,
name|message
argument_list|)
expr_stmt|;
block|}
specifier|protected
name|ByteSequence
name|getByteSequence
parameter_list|(
name|Message
name|message
parameter_list|)
throws|throws
name|IOException
block|{
name|org
operator|.
name|apache
operator|.
name|activemq
operator|.
name|util
operator|.
name|ByteSequence
name|packet
init|=
name|wireFormat
operator|.
name|marshal
argument_list|(
name|message
argument_list|)
decl_stmt|;
return|return
operator|new
name|ByteSequence
argument_list|(
name|packet
operator|.
name|data
argument_list|,
name|packet
operator|.
name|offset
argument_list|,
name|packet
operator|.
name|length
argument_list|)
return|;
block|}
specifier|protected
name|Message
name|getMessage
parameter_list|(
name|ByteSequence
name|bs
parameter_list|)
throws|throws
name|IOException
block|{
name|org
operator|.
name|apache
operator|.
name|activemq
operator|.
name|util
operator|.
name|ByteSequence
name|packet
init|=
operator|new
name|org
operator|.
name|apache
operator|.
name|activemq
operator|.
name|util
operator|.
name|ByteSequence
argument_list|(
name|bs
operator|.
name|getData
argument_list|()
argument_list|,
name|bs
operator|.
name|getOffset
argument_list|()
argument_list|,
name|bs
operator|.
name|getLength
argument_list|()
argument_list|)
decl_stmt|;
return|return
operator|(
name|Message
operator|)
name|this
operator|.
name|wireFormat
operator|.
name|unmarshal
argument_list|(
name|packet
argument_list|)
return|;
block|}
specifier|final
class|class
name|DiskIterator
implements|implements
name|Iterator
argument_list|<
name|MessageReference
argument_list|>
block|{
specifier|private
name|PListEntry
name|next
init|=
literal|null
decl_stmt|;
specifier|private
name|PListEntry
name|current
init|=
literal|null
decl_stmt|;
name|PList
name|list
decl_stmt|;
name|DiskIterator
parameter_list|()
block|{
try|try
block|{
name|this
operator|.
name|list
operator|=
name|getDiskList
argument_list|()
expr_stmt|;
synchronized|synchronized
init|(
name|this
operator|.
name|list
init|)
block|{
name|this
operator|.
name|current
operator|=
name|this
operator|.
name|list
operator|.
name|getFirst
argument_list|()
expr_stmt|;
name|this
operator|.
name|next
operator|=
name|this
operator|.
name|current
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
specifier|public
name|boolean
name|hasNext
parameter_list|()
block|{
return|return
name|this
operator|.
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
name|this
operator|.
name|current
operator|=
name|next
expr_stmt|;
try|try
block|{
name|ByteSequence
name|bs
init|=
name|this
operator|.
name|current
operator|.
name|getByteSequence
argument_list|()
decl_stmt|;
synchronized|synchronized
init|(
name|this
operator|.
name|list
init|)
block|{
name|this
operator|.
name|current
operator|=
name|this
operator|.
name|list
operator|.
name|refresh
argument_list|(
name|this
operator|.
name|current
argument_list|)
expr_stmt|;
name|this
operator|.
name|next
operator|=
name|this
operator|.
name|list
operator|.
name|getNext
argument_list|(
name|this
operator|.
name|current
argument_list|)
expr_stmt|;
block|}
return|return
name|getMessage
argument_list|(
name|bs
argument_list|)
return|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
name|LOG
operator|.
name|error
argument_list|(
literal|"I/O error"
argument_list|,
name|e
argument_list|)
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
specifier|public
name|void
name|remove
parameter_list|()
block|{
try|try
block|{
synchronized|synchronized
init|(
name|this
operator|.
name|list
init|)
block|{
name|this
operator|.
name|current
operator|=
name|this
operator|.
name|list
operator|.
name|refresh
argument_list|(
name|this
operator|.
name|current
argument_list|)
expr_stmt|;
name|this
operator|.
name|list
operator|.
name|remove
argument_list|(
name|this
operator|.
name|current
argument_list|)
expr_stmt|;
block|}
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
name|LOG
operator|.
name|error
argument_list|(
literal|"I/O error"
argument_list|,
name|e
argument_list|)
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
block|}
block|}
end_class

end_unit

