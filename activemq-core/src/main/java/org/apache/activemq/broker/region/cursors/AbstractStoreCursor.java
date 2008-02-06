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
name|LinkedHashMap
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
name|java
operator|.
name|util
operator|.
name|Map
operator|.
name|Entry
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
implements|,
name|UsageListener
block|{
specifier|private
specifier|static
specifier|final
name|Log
name|LOG
init|=
name|LogFactory
operator|.
name|getLog
argument_list|(
name|AbstractStoreCursor
operator|.
name|class
argument_list|)
decl_stmt|;
specifier|protected
specifier|static
specifier|final
name|int
name|MAX_FILL_ATTEMPTS
init|=
literal|3
decl_stmt|;
specifier|protected
specifier|final
name|Destination
name|regionDestination
decl_stmt|;
specifier|protected
specifier|final
name|LinkedHashMap
argument_list|<
name|MessageId
argument_list|,
name|Message
argument_list|>
name|batchList
init|=
operator|new
name|LinkedHashMap
argument_list|<
name|MessageId
argument_list|,
name|Message
argument_list|>
argument_list|()
decl_stmt|;
specifier|protected
name|boolean
name|cacheEnabled
init|=
literal|false
decl_stmt|;
specifier|protected
name|boolean
name|batchResetNeeded
init|=
literal|true
decl_stmt|;
specifier|protected
name|boolean
name|storeHasMessages
init|=
literal|false
decl_stmt|;
specifier|protected
name|int
name|size
decl_stmt|;
specifier|protected
name|AbstractStoreCursor
parameter_list|(
name|Destination
name|destination
parameter_list|)
block|{
name|this
operator|.
name|regionDestination
operator|=
name|destination
expr_stmt|;
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
name|this
operator|.
name|size
operator|=
name|getStoreSize
argument_list|()
expr_stmt|;
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
if|if
condition|(
operator|!
name|this
operator|.
name|storeHasMessages
operator|&&
name|useCache
condition|)
block|{
name|cacheEnabled
operator|=
literal|true
expr_stmt|;
block|}
block|}
name|super
operator|.
name|start
argument_list|()
expr_stmt|;
name|clear
argument_list|()
expr_stmt|;
name|resetBatch
argument_list|()
expr_stmt|;
name|getSystemUsage
argument_list|()
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
specifier|final
specifier|synchronized
name|void
name|stop
parameter_list|()
throws|throws
name|Exception
block|{
name|getSystemUsage
argument_list|()
operator|.
name|getMemoryUsage
argument_list|()
operator|.
name|removeUsageListener
argument_list|(
name|this
argument_list|)
expr_stmt|;
name|resetBatch
argument_list|()
expr_stmt|;
name|gc
argument_list|()
expr_stmt|;
name|super
operator|.
name|stop
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
if|if
condition|(
operator|!
name|isDuplicate
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
name|batchList
operator|.
name|put
argument_list|(
name|message
operator|.
name|getMessageId
argument_list|()
argument_list|,
name|message
argument_list|)
expr_stmt|;
block|}
else|else
block|{
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
literal|"Ignoring batched duplicated from store: "
operator|+
name|message
argument_list|)
expr_stmt|;
block|}
name|storeHasMessages
operator|=
literal|true
expr_stmt|;
block|}
return|return
literal|true
return|;
block|}
specifier|public
specifier|final
name|void
name|reset
parameter_list|()
block|{     }
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
literal|"Failed to fill batch"
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
name|boolean
name|result
init|=
operator|!
name|batchList
operator|.
name|isEmpty
argument_list|()
decl_stmt|;
return|return
name|result
return|;
block|}
specifier|public
specifier|final
specifier|synchronized
name|MessageReference
name|next
parameter_list|()
block|{
name|Message
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
condition|)
block|{
name|Iterator
argument_list|<
name|Entry
argument_list|<
name|MessageId
argument_list|,
name|Message
argument_list|>
argument_list|>
name|i
init|=
name|this
operator|.
name|batchList
operator|.
name|entrySet
argument_list|()
operator|.
name|iterator
argument_list|()
decl_stmt|;
name|result
operator|=
name|i
operator|.
name|next
argument_list|()
operator|.
name|getValue
argument_list|()
expr_stmt|;
name|i
operator|.
name|remove
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
if|if
condition|(
name|cacheEnabled
operator|&&
name|hasSpace
argument_list|()
condition|)
block|{
name|recoverMessage
argument_list|(
name|node
operator|.
name|getMessage
argument_list|()
argument_list|,
literal|true
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|cacheEnabled
operator|=
literal|false
expr_stmt|;
block|}
name|size
operator|++
expr_stmt|;
block|}
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
name|cacheEnabled
operator|=
literal|false
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
name|size
operator|==
literal|0
operator|&&
name|isStarted
argument_list|()
operator|&&
name|cacheEnabled
condition|)
block|{
name|cacheEnabled
operator|=
literal|true
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
name|size
operator|--
expr_stmt|;
name|cacheEnabled
operator|=
literal|false
expr_stmt|;
block|}
specifier|public
specifier|final
specifier|synchronized
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
name|oldPercentUsage
operator|>
name|newPercentUsage
operator|&&
name|oldPercentUsage
operator|>=
name|memoryUsageHighWaterMark
condition|)
block|{
name|storeHasMessages
operator|=
literal|true
expr_stmt|;
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
literal|"Failed to fill batch "
argument_list|,
name|e
argument_list|)
expr_stmt|;
block|}
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
specifier|final
specifier|synchronized
name|void
name|gc
parameter_list|()
block|{
for|for
control|(
name|Message
name|msg
range|:
name|batchList
operator|.
name|values
argument_list|()
control|)
block|{
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
name|batchResetNeeded
operator|=
literal|true
expr_stmt|;
name|this
operator|.
name|cacheEnabled
operator|=
literal|false
expr_stmt|;
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
comment|//we may have to move the store cursor past messages that have
comment|//already been delivered - but we also don't want it to spin
name|int
name|fillAttempts
init|=
literal|0
decl_stmt|;
while|while
condition|(
name|fillAttempts
operator|<
name|MAX_FILL_ATTEMPTS
operator|&&
name|this
operator|.
name|batchList
operator|.
name|isEmpty
argument_list|()
operator|&&
operator|(
name|this
operator|.
name|storeHasMessages
operator|||
name|this
operator|.
name|size
operator|>
literal|0
operator|)
condition|)
block|{
name|this
operator|.
name|storeHasMessages
operator|=
literal|false
expr_stmt|;
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
literal|"Failed to fill batch"
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
if|if
condition|(
operator|!
name|this
operator|.
name|batchList
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
name|this
operator|.
name|storeHasMessages
operator|=
literal|true
expr_stmt|;
block|}
name|fillAttempts
operator|++
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
return|return
name|size
operator|<=
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
name|isStarted
argument_list|()
condition|)
block|{
return|return
name|size
return|;
block|}
name|this
operator|.
name|size
operator|=
name|getStoreSize
argument_list|()
expr_stmt|;
return|return
name|size
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
block|}
end_class

end_unit

