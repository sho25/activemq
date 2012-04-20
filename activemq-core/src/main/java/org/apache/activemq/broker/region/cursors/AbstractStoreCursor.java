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
name|store
operator|.
name|MessageRecoveryListener
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
comment|/**  *  Store based cursor  *  */
end_comment

begin_class
specifier|public
specifier|abstract
class|class
name|AbstractStoreCursor
extends|extends
name|AbstractPendingMessageCursor
implements|implements
name|MessageRecoveryListener
block|{
specifier|private
specifier|static
specifier|final
name|Logger
name|LOG
init|=
name|LoggerFactory
operator|.
name|getLogger
argument_list|(
name|AbstractStoreCursor
operator|.
name|class
argument_list|)
decl_stmt|;
specifier|protected
specifier|final
name|Destination
name|regionDestination
decl_stmt|;
specifier|protected
specifier|final
name|PendingList
name|batchList
decl_stmt|;
specifier|private
name|Iterator
argument_list|<
name|MessageReference
argument_list|>
name|iterator
init|=
literal|null
decl_stmt|;
specifier|protected
name|boolean
name|batchResetNeeded
init|=
literal|true
decl_stmt|;
specifier|private
name|boolean
name|storeHasMessages
init|=
literal|false
decl_stmt|;
specifier|protected
name|int
name|size
decl_stmt|;
specifier|private
name|MessageId
name|lastCachedId
decl_stmt|;
specifier|private
name|boolean
name|hadSpace
init|=
literal|false
decl_stmt|;
specifier|protected
name|AbstractStoreCursor
parameter_list|(
name|Destination
name|destination
parameter_list|)
block|{
name|super
argument_list|(
operator|(
name|destination
operator|!=
literal|null
condition|?
name|destination
operator|.
name|isPrioritizedMessages
argument_list|()
else|:
literal|false
operator|)
argument_list|)
expr_stmt|;
name|this
operator|.
name|regionDestination
operator|=
name|destination
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
name|batchList
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
name|batchList
operator|=
operator|new
name|OrderedPendingList
argument_list|()
expr_stmt|;
block|}
block|}
specifier|public
specifier|final
specifier|synchronized
name|void
name|start
parameter_list|()
throws|throws
name|Exception
block|{
if|if
condition|(
operator|!
name|isStarted
argument_list|()
condition|)
block|{
name|clear
argument_list|()
expr_stmt|;
name|super
operator|.
name|start
argument_list|()
expr_stmt|;
name|resetBatch
argument_list|()
expr_stmt|;
name|resetSize
argument_list|()
expr_stmt|;
name|setCacheEnabled
argument_list|(
operator|!
name|this
operator|.
name|storeHasMessages
operator|&&
name|useCache
argument_list|)
expr_stmt|;
block|}
block|}
specifier|protected
name|void
name|resetSize
parameter_list|()
block|{
if|if
condition|(
name|isStarted
argument_list|()
condition|)
block|{
name|this
operator|.
name|size
operator|=
name|getStoreSize
argument_list|()
expr_stmt|;
block|}
name|this
operator|.
name|storeHasMessages
operator|=
name|this
operator|.
name|size
operator|>
literal|0
expr_stmt|;
block|}
specifier|public
specifier|final
specifier|synchronized
name|void
name|stop
parameter_list|()
throws|throws
name|Exception
block|{
name|resetBatch
argument_list|()
expr_stmt|;
name|super
operator|.
name|stop
argument_list|()
expr_stmt|;
name|gc
argument_list|()
expr_stmt|;
block|}
specifier|public
specifier|final
name|boolean
name|recoverMessage
parameter_list|(
name|Message
name|message
parameter_list|)
throws|throws
name|Exception
block|{
return|return
name|recoverMessage
argument_list|(
name|message
argument_list|,
literal|false
argument_list|)
return|;
block|}
specifier|public
specifier|synchronized
name|boolean
name|recoverMessage
parameter_list|(
name|Message
name|message
parameter_list|,
name|boolean
name|cached
parameter_list|)
throws|throws
name|Exception
block|{
name|boolean
name|recovered
init|=
literal|false
decl_stmt|;
if|if
condition|(
name|recordUniqueId
argument_list|(
name|message
operator|.
name|getMessageId
argument_list|()
argument_list|)
condition|)
block|{
if|if
condition|(
operator|!
name|cached
condition|)
block|{
name|message
operator|.
name|setRegionDestination
argument_list|(
name|regionDestination
argument_list|)
expr_stmt|;
if|if
condition|(
name|message
operator|.
name|getMemoryUsage
argument_list|()
operator|==
literal|null
condition|)
block|{
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
block|}
name|message
operator|.
name|incrementReferenceCount
argument_list|()
expr_stmt|;
name|batchList
operator|.
name|addMessageLast
argument_list|(
name|message
argument_list|)
expr_stmt|;
name|clearIterator
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|recovered
operator|=
literal|true
expr_stmt|;
name|storeHasMessages
operator|=
literal|true
expr_stmt|;
block|}
else|else
block|{
comment|/*              * we should expect to get these - as the message is recorded as it before it goes into              * the cache. If subsequently, we pull out that message from the store (before its deleted)              * it will be a duplicate - but should be ignored              */
if|if
condition|(
name|LOG
operator|.
name|isTraceEnabled
argument_list|()
condition|)
block|{
name|LOG
operator|.
name|trace
argument_list|(
name|this
operator|+
literal|" - cursor got duplicate: "
operator|+
name|message
operator|.
name|getMessageId
argument_list|()
operator|+
literal|", "
operator|+
name|message
operator|.
name|getPriority
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
return|return
name|recovered
return|;
block|}
specifier|public
specifier|final
specifier|synchronized
name|void
name|reset
parameter_list|()
block|{
if|if
condition|(
name|batchList
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
try|try
block|{
name|fillBatch
argument_list|()
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
name|this
operator|+
literal|" - Failed to fill batch"
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
name|clearIterator
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|size
argument_list|()
expr_stmt|;
block|}
specifier|public
specifier|synchronized
name|void
name|release
parameter_list|()
block|{
name|clearIterator
argument_list|(
literal|false
argument_list|)
expr_stmt|;
block|}
specifier|private
specifier|synchronized
name|void
name|clearIterator
parameter_list|(
name|boolean
name|ensureIterator
parameter_list|)
block|{
name|boolean
name|haveIterator
init|=
name|this
operator|.
name|iterator
operator|!=
literal|null
decl_stmt|;
name|this
operator|.
name|iterator
operator|=
literal|null
expr_stmt|;
if|if
condition|(
name|haveIterator
operator|&&
name|ensureIterator
condition|)
block|{
name|ensureIterator
argument_list|()
expr_stmt|;
block|}
block|}
specifier|private
specifier|synchronized
name|void
name|ensureIterator
parameter_list|()
block|{
if|if
condition|(
name|this
operator|.
name|iterator
operator|==
literal|null
condition|)
block|{
name|this
operator|.
name|iterator
operator|=
name|this
operator|.
name|batchList
operator|.
name|iterator
argument_list|()
expr_stmt|;
block|}
block|}
specifier|public
specifier|final
name|void
name|finished
parameter_list|()
block|{     }
specifier|public
specifier|final
specifier|synchronized
name|boolean
name|hasNext
parameter_list|()
block|{
if|if
condition|(
name|batchList
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
try|try
block|{
name|fillBatch
argument_list|()
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
name|this
operator|+
literal|" - Failed to fill batch"
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
name|ensureIterator
argument_list|()
expr_stmt|;
return|return
name|this
operator|.
name|iterator
operator|.
name|hasNext
argument_list|()
return|;
block|}
specifier|public
specifier|final
specifier|synchronized
name|MessageReference
name|next
parameter_list|()
block|{
name|MessageReference
name|result
init|=
literal|null
decl_stmt|;
if|if
condition|(
operator|!
name|this
operator|.
name|batchList
operator|.
name|isEmpty
argument_list|()
operator|&&
name|this
operator|.
name|iterator
operator|.
name|hasNext
argument_list|()
condition|)
block|{
name|result
operator|=
name|this
operator|.
name|iterator
operator|.
name|next
argument_list|()
expr_stmt|;
block|}
name|last
operator|=
name|result
expr_stmt|;
if|if
condition|(
name|result
operator|!=
literal|null
condition|)
block|{
name|result
operator|.
name|incrementReferenceCount
argument_list|()
expr_stmt|;
block|}
return|return
name|result
return|;
block|}
specifier|public
specifier|final
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
name|boolean
name|disableCache
init|=
literal|false
decl_stmt|;
if|if
condition|(
name|hasSpace
argument_list|()
condition|)
block|{
if|if
condition|(
operator|!
name|isCacheEnabled
argument_list|()
operator|&&
name|size
operator|==
literal|0
operator|&&
name|isStarted
argument_list|()
operator|&&
name|useCache
condition|)
block|{
if|if
condition|(
name|LOG
operator|.
name|isTraceEnabled
argument_list|()
condition|)
block|{
name|LOG
operator|.
name|trace
argument_list|(
name|this
operator|+
literal|" - enabling cache for empty store "
operator|+
name|node
operator|.
name|getMessageId
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|setCacheEnabled
argument_list|(
literal|true
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|isCacheEnabled
argument_list|()
condition|)
block|{
if|if
condition|(
name|recoverMessage
argument_list|(
name|node
operator|.
name|getMessage
argument_list|()
argument_list|,
literal|true
argument_list|)
condition|)
block|{
name|lastCachedId
operator|=
name|node
operator|.
name|getMessageId
argument_list|()
expr_stmt|;
block|}
else|else
block|{
comment|// failed to recover, possible duplicate from concurrent dispatchPending,
comment|// lets not recover further in case of out of order
name|disableCache
operator|=
literal|true
expr_stmt|;
block|}
block|}
block|}
else|else
block|{
name|disableCache
operator|=
literal|true
expr_stmt|;
block|}
if|if
condition|(
name|disableCache
operator|&&
name|isCacheEnabled
argument_list|()
condition|)
block|{
name|setCacheEnabled
argument_list|(
literal|false
argument_list|)
expr_stmt|;
comment|// sync with store on disabling the cache
if|if
condition|(
name|lastCachedId
operator|!=
literal|null
condition|)
block|{
if|if
condition|(
name|LOG
operator|.
name|isTraceEnabled
argument_list|()
condition|)
block|{
name|LOG
operator|.
name|trace
argument_list|(
name|this
operator|+
literal|" - disabling cache"
operator|+
literal|", lastCachedId: "
operator|+
name|lastCachedId
operator|+
literal|" current node Id: "
operator|+
name|node
operator|.
name|getMessageId
argument_list|()
operator|+
literal|" batchList size: "
operator|+
name|batchList
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|setBatch
argument_list|(
name|lastCachedId
argument_list|)
expr_stmt|;
name|lastCachedId
operator|=
literal|null
expr_stmt|;
block|}
block|}
name|this
operator|.
name|storeHasMessages
operator|=
literal|true
expr_stmt|;
name|size
operator|++
expr_stmt|;
block|}
specifier|protected
name|void
name|setBatch
parameter_list|(
name|MessageId
name|messageId
parameter_list|)
throws|throws
name|Exception
block|{     }
specifier|public
specifier|final
specifier|synchronized
name|void
name|addMessageFirst
parameter_list|(
name|MessageReference
name|node
parameter_list|)
throws|throws
name|Exception
block|{
name|setCacheEnabled
argument_list|(
literal|false
argument_list|)
expr_stmt|;
name|size
operator|++
expr_stmt|;
block|}
specifier|public
specifier|final
specifier|synchronized
name|void
name|remove
parameter_list|()
block|{
name|size
operator|--
expr_stmt|;
if|if
condition|(
name|iterator
operator|!=
literal|null
condition|)
block|{
name|iterator
operator|.
name|remove
argument_list|()
expr_stmt|;
block|}
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
specifier|public
specifier|final
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
name|batchList
operator|.
name|remove
argument_list|(
name|node
argument_list|)
operator|!=
literal|null
condition|)
block|{
name|size
operator|--
expr_stmt|;
name|setCacheEnabled
argument_list|(
literal|false
argument_list|)
expr_stmt|;
block|}
block|}
specifier|public
specifier|final
specifier|synchronized
name|void
name|clear
parameter_list|()
block|{
name|gc
argument_list|()
expr_stmt|;
block|}
specifier|public
specifier|synchronized
name|void
name|gc
parameter_list|()
block|{
for|for
control|(
name|Iterator
argument_list|<
name|MessageReference
argument_list|>
name|i
init|=
name|batchList
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
name|msg
init|=
name|i
operator|.
name|next
argument_list|()
decl_stmt|;
name|rollback
argument_list|(
name|msg
operator|.
name|getMessageId
argument_list|()
argument_list|)
expr_stmt|;
name|msg
operator|.
name|decrementReferenceCount
argument_list|()
expr_stmt|;
block|}
name|batchList
operator|.
name|clear
argument_list|()
expr_stmt|;
name|clearIterator
argument_list|(
literal|false
argument_list|)
expr_stmt|;
name|batchResetNeeded
operator|=
literal|true
expr_stmt|;
comment|// wonder do we need to determine size here, it may change before restart
name|resetSize
argument_list|()
expr_stmt|;
name|setCacheEnabled
argument_list|(
literal|false
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|hasSpace
parameter_list|()
block|{
name|hadSpace
operator|=
name|super
operator|.
name|hasSpace
argument_list|()
expr_stmt|;
return|return
name|hadSpace
return|;
block|}
specifier|protected
specifier|final
specifier|synchronized
name|void
name|fillBatch
parameter_list|()
block|{
if|if
condition|(
name|LOG
operator|.
name|isTraceEnabled
argument_list|()
condition|)
block|{
name|LOG
operator|.
name|trace
argument_list|(
name|this
operator|+
literal|" - fillBatch"
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|batchResetNeeded
condition|)
block|{
name|resetBatch
argument_list|()
expr_stmt|;
name|this
operator|.
name|batchResetNeeded
operator|=
literal|false
expr_stmt|;
block|}
if|if
condition|(
name|this
operator|.
name|batchList
operator|.
name|isEmpty
argument_list|()
operator|&&
name|this
operator|.
name|storeHasMessages
operator|&&
name|this
operator|.
name|size
operator|>
literal|0
condition|)
block|{
try|try
block|{
name|doFillBatch
argument_list|()
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
name|this
operator|+
literal|" - Failed to fill batch"
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
name|this
operator|.
name|storeHasMessages
operator|=
operator|!
name|this
operator|.
name|batchList
operator|.
name|isEmpty
argument_list|()
operator|||
operator|!
name|hadSpace
expr_stmt|;
block|}
block|}
specifier|public
specifier|final
specifier|synchronized
name|boolean
name|isEmpty
parameter_list|()
block|{
comment|// negative means more messages added to store through queue.send since last reset
return|return
name|size
operator|==
literal|0
return|;
block|}
specifier|public
specifier|final
specifier|synchronized
name|boolean
name|hasMessagesBufferedToDeliver
parameter_list|()
block|{
return|return
operator|!
name|batchList
operator|.
name|isEmpty
argument_list|()
return|;
block|}
specifier|public
specifier|final
specifier|synchronized
name|int
name|size
parameter_list|()
block|{
if|if
condition|(
name|size
operator|<
literal|0
condition|)
block|{
name|this
operator|.
name|size
operator|=
name|getStoreSize
argument_list|()
expr_stmt|;
block|}
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
return|return
name|super
operator|.
name|toString
argument_list|()
operator|+
literal|":"
operator|+
name|regionDestination
operator|.
name|getActiveMQDestination
argument_list|()
operator|.
name|getPhysicalName
argument_list|()
operator|+
literal|",batchResetNeeded="
operator|+
name|batchResetNeeded
operator|+
literal|",storeHasMessages="
operator|+
name|this
operator|.
name|storeHasMessages
operator|+
literal|",size="
operator|+
name|this
operator|.
name|size
operator|+
literal|",cacheEnabled="
operator|+
name|isCacheEnabled
argument_list|()
return|;
block|}
specifier|protected
specifier|abstract
name|void
name|doFillBatch
parameter_list|()
throws|throws
name|Exception
function_decl|;
specifier|protected
specifier|abstract
name|void
name|resetBatch
parameter_list|()
function_decl|;
specifier|protected
specifier|abstract
name|int
name|getStoreSize
parameter_list|()
function_decl|;
specifier|protected
specifier|abstract
name|boolean
name|isStoreEmpty
parameter_list|()
function_decl|;
block|}
end_class

end_unit

