begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *      http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
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
name|IndirectMessageReference
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
name|util
operator|.
name|ByteSequence
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
name|slf4j
operator|.
name|Logger
import|;
end_import

begin_import
import|import
name|org
operator|.
name|slf4j
operator|.
name|LoggerFactory
import|;
end_import

begin_comment
comment|/**  * persist pending messages pending message (messages awaiting dispatch to a  * consumer) cursor  */
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
name|Logger
name|LOG
init|=
name|LoggerFactory
operator|.
name|getLogger
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
name|PendingList
name|memoryList
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
comment|/**      * @param broker      * @param name      * @param prioritizedMessages      */
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
if|if
condition|(
name|this
operator|.
name|prioritizedMessages
condition|)
block|{
name|this
operator|.
name|memoryList
operator|=
operator|new
name|PrioritizedPendingList
argument_list|()
expr_stmt|;
block|}
else|else
block|{
name|this
operator|.
name|memoryList
operator|=
operator|new
name|OrderedPendingList
argument_list|()
expr_stmt|;
block|}
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
if|if
condition|(
name|this
operator|.
name|broker
operator|!=
literal|null
condition|)
block|{
name|wireFormat
operator|.
name|setVersion
argument_list|(
name|this
operator|.
name|broker
operator|.
name|getBrokerService
argument_list|()
operator|.
name|getStoreOpenWireVersion
argument_list|()
argument_list|)
expr_stmt|;
block|}
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
name|iter
operator|instanceof
name|DiskIterator
condition|)
block|{
operator|(
operator|(
name|DiskIterator
operator|)
name|iter
operator|)
operator|.
name|release
argument_list|()
expr_stmt|;
block|}
empty_stmt|;
if|if
condition|(
name|flushRequired
condition|)
block|{
name|flushRequired
operator|=
literal|false
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
block|}
block|}
comment|// ensure any memory ref is released
name|iter
operator|=
literal|null
expr_stmt|;
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
name|MessageReference
name|node
init|=
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
name|diskList
operator|!=
literal|null
condition|)
block|{
name|store
operator|.
name|removePList
argument_list|(
name|name
argument_list|)
expr_stmt|;
name|diskList
operator|=
literal|null
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
comment|/**      * add message to await dispatch      *      * @param node      * @throws Exception      */
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
operator|(
name|Destination
operator|)
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
name|addMessageLast
argument_list|(
name|node
argument_list|)
expr_stmt|;
name|node
operator|.
name|incrementReferenceCount
argument_list|()
expr_stmt|;
name|setCacheEnabled
argument_list|(
literal|true
argument_list|)
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
name|addMessageLast
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
literal|"Caught an Exception adding a message: {} first to FilePendingMessageCursor "
argument_list|,
name|node
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
name|discardExpiredMessage
argument_list|(
name|node
argument_list|)
expr_stmt|;
block|}
comment|//message expired
return|return
literal|true
return|;
block|}
comment|/**      * add message to await dispatch      *      * @param node      */
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
operator|(
name|Destination
operator|)
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
name|addMessageFirst
argument_list|(
name|node
argument_list|)
expr_stmt|;
name|node
operator|.
name|incrementReferenceCount
argument_list|()
expr_stmt|;
name|setCacheEnabled
argument_list|(
literal|true
argument_list|)
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
name|addMessageFirst
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
name|Object
name|locator
init|=
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
decl_stmt|;
name|node
operator|.
name|getMessageId
argument_list|()
operator|.
name|setPlistLocator
argument_list|(
name|locator
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
literal|"Caught an Exception adding a message: {} first to FilePendingMessageCursor "
argument_list|,
name|node
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
name|discardExpiredMessage
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
name|MessageReference
name|reference
init|=
name|iter
operator|.
name|next
argument_list|()
decl_stmt|;
name|last
operator|=
name|reference
expr_stmt|;
if|if
condition|(
operator|!
name|isDiskListEmpty
argument_list|()
condition|)
block|{
comment|// got from disk
name|reference
operator|.
name|getMessage
argument_list|()
operator|.
name|setRegionDestination
argument_list|(
name|regionDestination
argument_list|)
expr_stmt|;
name|reference
operator|.
name|getMessage
argument_list|()
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
name|reference
operator|.
name|incrementReferenceCount
argument_list|()
expr_stmt|;
return|return
name|reference
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
operator|!=
literal|null
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
name|getPlistLocator
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
operator|(
name|int
operator|)
name|getDiskList
argument_list|()
operator|.
name|size
argument_list|()
operator|)
return|;
block|}
annotation|@
name|Override
specifier|public
specifier|synchronized
name|long
name|messageSize
parameter_list|()
block|{
return|return
name|memoryList
operator|.
name|messageSize
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
name|messageSize
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
operator|!
name|isDiskListEmpty
argument_list|()
operator|&&
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
annotation|@
name|Override
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
name|List
argument_list|<
name|MessageReference
argument_list|>
name|expiredMessages
init|=
literal|null
decl_stmt|;
synchronized|synchronized
init|(
name|this
init|)
block|{
if|if
condition|(
operator|!
name|flushRequired
operator|&&
name|size
argument_list|()
operator|!=
literal|0
condition|)
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
name|expiredMessages
operator|=
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
if|if
condition|(
name|expiredMessages
operator|!=
literal|null
condition|)
block|{
for|for
control|(
name|MessageReference
name|node
range|:
name|expiredMessages
control|)
block|{
name|discardExpiredMessage
argument_list|(
name|node
argument_list|)
expr_stmt|;
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
specifier|private
specifier|synchronized
name|List
argument_list|<
name|MessageReference
argument_list|>
name|expireOldMessages
parameter_list|()
block|{
name|List
argument_list|<
name|MessageReference
argument_list|>
name|expired
init|=
operator|new
name|ArrayList
argument_list|<
name|MessageReference
argument_list|>
argument_list|()
decl_stmt|;
if|if
condition|(
operator|!
name|memoryList
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
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
operator|.
name|isExpired
argument_list|()
condition|)
block|{
name|node
operator|.
name|decrementReferenceCount
argument_list|()
expr_stmt|;
name|expired
operator|.
name|add
argument_list|(
name|node
argument_list|)
expr_stmt|;
name|iterator
operator|.
name|remove
argument_list|()
expr_stmt|;
block|}
block|}
block|}
return|return
name|expired
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
operator|&&
name|store
operator|!=
literal|null
condition|)
block|{
name|long
name|start
init|=
literal|0
decl_stmt|;
if|if
condition|(
name|LOG
operator|.
name|isTraceEnabled
argument_list|()
condition|)
block|{
name|start
operator|=
name|System
operator|.
name|currentTimeMillis
argument_list|()
expr_stmt|;
name|LOG
operator|.
name|trace
argument_list|(
literal|"{}, flushToDisk() mem list size: {} {}"
argument_list|,
operator|new
name|Object
index|[]
block|{
name|name
block|,
name|memoryList
operator|.
name|size
argument_list|()
block|,
operator|(
name|systemUsage
operator|!=
literal|null
condition|?
name|systemUsage
operator|.
name|getMemoryUsage
argument_list|()
else|:
literal|""
operator|)
block|}
argument_list|)
expr_stmt|;
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
name|setCacheEnabled
argument_list|(
literal|false
argument_list|)
expr_stmt|;
name|LOG
operator|.
name|trace
argument_list|(
literal|"{}, flushToDisk() done - {} ms {}"
argument_list|,
operator|new
name|Object
index|[]
block|{
name|name
block|,
operator|(
name|System
operator|.
name|currentTimeMillis
argument_list|()
operator|-
name|start
operator|)
block|,
operator|(
name|systemUsage
operator|!=
literal|null
condition|?
name|systemUsage
operator|.
name|getMemoryUsage
argument_list|()
else|:
literal|""
operator|)
block|}
argument_list|)
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
specifier|public
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
literal|"Caught an IO Exception getting the DiskList {}"
argument_list|,
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
specifier|private
name|void
name|discardExpiredMessage
parameter_list|(
name|MessageReference
name|reference
parameter_list|)
block|{
name|LOG
operator|.
name|debug
argument_list|(
literal|"Discarding expired message {}"
argument_list|,
name|reference
argument_list|)
expr_stmt|;
if|if
condition|(
name|reference
operator|.
name|isExpired
argument_list|()
operator|&&
name|broker
operator|.
name|isExpired
argument_list|(
name|reference
argument_list|)
condition|)
block|{
name|ConnectionContext
name|context
init|=
operator|new
name|ConnectionContext
argument_list|()
decl_stmt|;
name|context
operator|.
name|setBroker
argument_list|(
name|broker
argument_list|)
expr_stmt|;
operator|(
operator|(
name|Destination
operator|)
name|reference
operator|.
name|getRegionDestination
argument_list|()
operator|)
operator|.
name|messageExpired
argument_list|(
name|context
argument_list|,
literal|null
argument_list|,
operator|new
name|IndirectMessageReference
argument_list|(
name|reference
operator|.
name|getMessage
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
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
specifier|final
name|PList
operator|.
name|PListIterator
name|iterator
decl_stmt|;
name|DiskIterator
parameter_list|()
block|{
try|try
block|{
name|iterator
operator|=
name|getDiskList
argument_list|()
operator|.
name|iterator
argument_list|()
expr_stmt|;
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
annotation|@
name|Override
specifier|public
name|boolean
name|hasNext
parameter_list|()
block|{
return|return
name|iterator
operator|.
name|hasNext
argument_list|()
return|;
block|}
annotation|@
name|Override
specifier|public
name|MessageReference
name|next
parameter_list|()
block|{
try|try
block|{
name|PListEntry
name|entry
init|=
name|iterator
operator|.
name|next
argument_list|()
decl_stmt|;
name|Message
name|message
init|=
name|getMessage
argument_list|(
name|entry
operator|.
name|getByteSequence
argument_list|()
argument_list|)
decl_stmt|;
name|message
operator|.
name|getMessageId
argument_list|()
operator|.
name|setPlistLocator
argument_list|(
name|entry
operator|.
name|getLocator
argument_list|()
argument_list|)
expr_stmt|;
return|return
name|message
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
annotation|@
name|Override
specifier|public
name|void
name|remove
parameter_list|()
block|{
name|iterator
operator|.
name|remove
argument_list|()
expr_stmt|;
block|}
specifier|public
name|void
name|release
parameter_list|()
block|{
name|iterator
operator|.
name|release
argument_list|()
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

