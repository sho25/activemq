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
name|store
operator|.
name|kahadaptor
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
name|HashSet
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Set
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
name|locks
operator|.
name|Lock
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
name|locks
operator|.
name|ReentrantLock
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
name|ActiveMQMessageAudit
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
name|command
operator|.
name|ActiveMQDestination
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
name|MessageAck
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
name|kaha
operator|.
name|MapContainer
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
name|MessageAckWithLocation
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
name|StoreEntry
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
name|AbstractMessageStore
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
name|store
operator|.
name|ReferenceStore
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
comment|/**  * @author rajdavies  *  */
end_comment

begin_class
specifier|public
class|class
name|KahaReferenceStore
extends|extends
name|AbstractMessageStore
implements|implements
name|ReferenceStore
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
name|KahaReferenceStore
operator|.
name|class
argument_list|)
decl_stmt|;
specifier|protected
specifier|final
name|MapContainer
argument_list|<
name|MessageId
argument_list|,
name|ReferenceRecord
argument_list|>
name|messageContainer
decl_stmt|;
specifier|protected
name|KahaReferenceStoreAdapter
name|adapter
decl_stmt|;
comment|// keep track of dispatched messages so that duplicate sends that follow a successful
comment|// dispatch can be suppressed.
specifier|protected
name|ActiveMQMessageAudit
name|dispatchAudit
init|=
operator|new
name|ActiveMQMessageAudit
argument_list|()
decl_stmt|;
specifier|private
name|StoreEntry
name|batchEntry
decl_stmt|;
specifier|private
name|String
name|lastBatchId
decl_stmt|;
specifier|protected
specifier|final
name|Lock
name|lock
init|=
operator|new
name|ReentrantLock
argument_list|()
decl_stmt|;
specifier|public
name|KahaReferenceStore
parameter_list|(
name|KahaReferenceStoreAdapter
name|adapter
parameter_list|,
name|MapContainer
argument_list|<
name|MessageId
argument_list|,
name|ReferenceRecord
argument_list|>
name|container
parameter_list|,
name|ActiveMQDestination
name|destination
parameter_list|)
throws|throws
name|IOException
block|{
name|super
argument_list|(
name|destination
argument_list|)
expr_stmt|;
name|this
operator|.
name|adapter
operator|=
name|adapter
expr_stmt|;
name|this
operator|.
name|messageContainer
operator|=
name|container
expr_stmt|;
block|}
specifier|public
name|Lock
name|getStoreLock
parameter_list|()
block|{
return|return
name|lock
return|;
block|}
specifier|public
name|void
name|dispose
parameter_list|(
name|ConnectionContext
name|context
parameter_list|)
block|{
name|super
operator|.
name|dispose
argument_list|(
name|context
argument_list|)
expr_stmt|;
name|this
operator|.
name|messageContainer
operator|.
name|delete
argument_list|()
expr_stmt|;
name|this
operator|.
name|adapter
operator|.
name|removeReferenceStore
argument_list|(
name|this
argument_list|)
expr_stmt|;
block|}
specifier|protected
name|MessageId
name|getMessageId
parameter_list|(
name|Object
name|object
parameter_list|)
block|{
return|return
operator|new
name|MessageId
argument_list|(
operator|(
operator|(
name|ReferenceRecord
operator|)
name|object
operator|)
operator|.
name|getMessageId
argument_list|()
argument_list|)
return|;
block|}
specifier|public
name|void
name|addMessage
parameter_list|(
name|ConnectionContext
name|context
parameter_list|,
name|Message
name|message
parameter_list|)
throws|throws
name|IOException
block|{
throw|throw
operator|new
name|RuntimeException
argument_list|(
literal|"Use addMessageReference instead"
argument_list|)
throw|;
block|}
specifier|public
name|Message
name|getMessage
parameter_list|(
name|MessageId
name|identity
parameter_list|)
throws|throws
name|IOException
block|{
throw|throw
operator|new
name|RuntimeException
argument_list|(
literal|"Use addMessageReference instead"
argument_list|)
throw|;
block|}
specifier|protected
specifier|final
name|boolean
name|recoverReference
parameter_list|(
name|MessageRecoveryListener
name|listener
parameter_list|,
name|ReferenceRecord
name|record
parameter_list|)
throws|throws
name|Exception
block|{
name|MessageId
name|id
init|=
operator|new
name|MessageId
argument_list|(
name|record
operator|.
name|getMessageId
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
name|listener
operator|.
name|hasSpace
argument_list|()
condition|)
block|{
return|return
name|listener
operator|.
name|recoverMessageReference
argument_list|(
name|id
argument_list|)
return|;
block|}
return|return
literal|false
return|;
block|}
specifier|public
name|void
name|recover
parameter_list|(
name|MessageRecoveryListener
name|listener
parameter_list|)
throws|throws
name|Exception
block|{
name|lock
operator|.
name|lock
argument_list|()
expr_stmt|;
try|try
block|{
for|for
control|(
name|StoreEntry
name|entry
init|=
name|messageContainer
operator|.
name|getFirst
argument_list|()
init|;
name|entry
operator|!=
literal|null
condition|;
name|entry
operator|=
name|messageContainer
operator|.
name|getNext
argument_list|(
name|entry
argument_list|)
control|)
block|{
name|ReferenceRecord
name|record
init|=
name|messageContainer
operator|.
name|getValue
argument_list|(
name|entry
argument_list|)
decl_stmt|;
if|if
condition|(
operator|!
name|recoverReference
argument_list|(
name|listener
argument_list|,
name|record
argument_list|)
condition|)
block|{
break|break;
block|}
block|}
block|}
finally|finally
block|{
name|lock
operator|.
name|unlock
argument_list|()
expr_stmt|;
block|}
block|}
specifier|public
name|void
name|recoverNextMessages
parameter_list|(
name|int
name|maxReturned
parameter_list|,
name|MessageRecoveryListener
name|listener
parameter_list|)
throws|throws
name|Exception
block|{
name|lock
operator|.
name|lock
argument_list|()
expr_stmt|;
try|try
block|{
name|StoreEntry
name|entry
init|=
name|batchEntry
decl_stmt|;
if|if
condition|(
name|entry
operator|==
literal|null
condition|)
block|{
name|entry
operator|=
name|messageContainer
operator|.
name|getFirst
argument_list|()
expr_stmt|;
block|}
else|else
block|{
name|entry
operator|=
name|messageContainer
operator|.
name|refresh
argument_list|(
name|entry
argument_list|)
expr_stmt|;
if|if
condition|(
name|entry
operator|!=
literal|null
condition|)
block|{
name|entry
operator|=
name|messageContainer
operator|.
name|getNext
argument_list|(
name|entry
argument_list|)
expr_stmt|;
block|}
block|}
if|if
condition|(
name|entry
operator|!=
literal|null
condition|)
block|{
name|int
name|count
init|=
literal|0
decl_stmt|;
do|do
block|{
name|ReferenceRecord
name|msg
init|=
name|messageContainer
operator|.
name|getValue
argument_list|(
name|entry
argument_list|)
decl_stmt|;
if|if
condition|(
name|msg
operator|!=
literal|null
condition|)
block|{
if|if
condition|(
name|recoverReference
argument_list|(
name|listener
argument_list|,
name|msg
argument_list|)
condition|)
block|{
name|count
operator|++
expr_stmt|;
name|lastBatchId
operator|=
name|msg
operator|.
name|getMessageId
argument_list|()
expr_stmt|;
block|}
elseif|else
if|if
condition|(
operator|!
name|listener
operator|.
name|isDuplicate
argument_list|(
operator|new
name|MessageId
argument_list|(
name|msg
operator|.
name|getMessageId
argument_list|()
argument_list|)
argument_list|)
condition|)
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
name|destination
operator|.
name|getQualifiedName
argument_list|()
operator|+
literal|" did not recover (will retry) message: "
operator|+
name|msg
operator|.
name|getMessageId
argument_list|()
argument_list|)
expr_stmt|;
block|}
comment|// give usage limits a chance to reclaim
break|break;
block|}
else|else
block|{
comment|// skip duplicate and continue
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
name|destination
operator|.
name|getQualifiedName
argument_list|()
operator|+
literal|" skipping duplicate, "
operator|+
name|msg
operator|.
name|getMessageId
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
block|}
else|else
block|{
name|lastBatchId
operator|=
literal|null
expr_stmt|;
block|}
name|batchEntry
operator|=
name|entry
expr_stmt|;
name|entry
operator|=
name|messageContainer
operator|.
name|getNext
argument_list|(
name|entry
argument_list|)
expr_stmt|;
block|}
do|while
condition|(
name|entry
operator|!=
literal|null
operator|&&
name|count
operator|<
name|maxReturned
operator|&&
name|listener
operator|.
name|hasSpace
argument_list|()
condition|)
do|;
block|}
block|}
finally|finally
block|{
name|lock
operator|.
name|unlock
argument_list|()
expr_stmt|;
block|}
block|}
specifier|public
name|boolean
name|addMessageReference
parameter_list|(
name|ConnectionContext
name|context
parameter_list|,
name|MessageId
name|messageId
parameter_list|,
name|ReferenceData
name|data
parameter_list|)
throws|throws
name|IOException
block|{
name|boolean
name|uniqueueReferenceAdded
init|=
literal|false
decl_stmt|;
name|lock
operator|.
name|lock
argument_list|()
expr_stmt|;
try|try
block|{
if|if
condition|(
operator|!
name|isDuplicate
argument_list|(
name|messageId
argument_list|)
condition|)
block|{
name|ReferenceRecord
name|record
init|=
operator|new
name|ReferenceRecord
argument_list|(
name|messageId
operator|.
name|toString
argument_list|()
argument_list|,
name|data
argument_list|)
decl_stmt|;
name|messageContainer
operator|.
name|put
argument_list|(
name|messageId
argument_list|,
name|record
argument_list|)
expr_stmt|;
name|uniqueueReferenceAdded
operator|=
literal|true
expr_stmt|;
name|addInterest
argument_list|(
name|record
argument_list|)
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
name|destination
operator|.
name|getPhysicalName
argument_list|()
operator|+
literal|" add: "
operator|+
name|messageId
argument_list|)
expr_stmt|;
block|}
block|}
block|}
finally|finally
block|{
name|lock
operator|.
name|unlock
argument_list|()
expr_stmt|;
block|}
return|return
name|uniqueueReferenceAdded
return|;
block|}
specifier|protected
name|boolean
name|isDuplicate
parameter_list|(
specifier|final
name|MessageId
name|messageId
parameter_list|)
block|{
name|boolean
name|duplicate
init|=
name|messageContainer
operator|.
name|containsKey
argument_list|(
name|messageId
argument_list|)
decl_stmt|;
if|if
condition|(
operator|!
name|duplicate
condition|)
block|{
name|duplicate
operator|=
name|dispatchAudit
operator|.
name|isDuplicate
argument_list|(
name|messageId
argument_list|)
expr_stmt|;
if|if
condition|(
name|duplicate
condition|)
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
name|destination
operator|.
name|getPhysicalName
argument_list|()
operator|+
literal|" ignoring duplicated (add) message reference, already dispatched: "
operator|+
name|messageId
argument_list|)
expr_stmt|;
block|}
block|}
block|}
elseif|else
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
name|destination
operator|.
name|getPhysicalName
argument_list|()
operator|+
literal|" ignoring duplicated (add) message reference, already in store: "
operator|+
name|messageId
argument_list|)
expr_stmt|;
block|}
return|return
name|duplicate
return|;
block|}
specifier|public
name|ReferenceData
name|getMessageReference
parameter_list|(
name|MessageId
name|identity
parameter_list|)
throws|throws
name|IOException
block|{
name|lock
operator|.
name|lock
argument_list|()
expr_stmt|;
try|try
block|{
name|ReferenceRecord
name|result
init|=
name|messageContainer
operator|.
name|get
argument_list|(
name|identity
argument_list|)
decl_stmt|;
if|if
condition|(
name|result
operator|==
literal|null
condition|)
block|{
return|return
literal|null
return|;
block|}
return|return
name|result
operator|.
name|getData
argument_list|()
return|;
block|}
finally|finally
block|{
name|lock
operator|.
name|unlock
argument_list|()
expr_stmt|;
block|}
block|}
specifier|public
name|void
name|removeMessage
parameter_list|(
name|ConnectionContext
name|context
parameter_list|,
name|MessageAck
name|ack
parameter_list|)
throws|throws
name|IOException
block|{
name|lock
operator|.
name|lock
argument_list|()
expr_stmt|;
try|try
block|{
name|MessageId
name|msgId
init|=
name|ack
operator|.
name|getLastMessageId
argument_list|()
decl_stmt|;
name|StoreEntry
name|entry
init|=
name|messageContainer
operator|.
name|getEntry
argument_list|(
name|msgId
argument_list|)
decl_stmt|;
if|if
condition|(
name|entry
operator|!=
literal|null
condition|)
block|{
name|ReferenceRecord
name|rr
init|=
name|messageContainer
operator|.
name|remove
argument_list|(
name|msgId
argument_list|)
decl_stmt|;
if|if
condition|(
name|rr
operator|!=
literal|null
condition|)
block|{
name|removeInterest
argument_list|(
name|rr
argument_list|)
expr_stmt|;
if|if
condition|(
name|ack
operator|instanceof
name|MessageAckWithLocation
condition|)
block|{
name|recordAckFileReferences
argument_list|(
operator|(
name|MessageAckWithLocation
operator|)
name|ack
argument_list|,
name|rr
operator|.
name|getData
argument_list|()
operator|.
name|getFileId
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|dispatchAudit
operator|.
name|isDuplicate
argument_list|(
name|msgId
argument_list|)
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
name|destination
operator|.
name|getPhysicalName
argument_list|()
operator|+
literal|" remove reference: "
operator|+
name|msgId
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|messageContainer
operator|.
name|isEmpty
argument_list|()
operator|||
operator|(
name|lastBatchId
operator|!=
literal|null
operator|&&
name|lastBatchId
operator|.
name|equals
argument_list|(
name|msgId
operator|.
name|toString
argument_list|()
argument_list|)
operator|)
operator|||
operator|(
name|batchEntry
operator|!=
literal|null
operator|&&
name|batchEntry
operator|.
name|equals
argument_list|(
name|entry
argument_list|)
operator|)
condition|)
block|{
name|resetBatching
argument_list|()
expr_stmt|;
block|}
block|}
block|}
block|}
finally|finally
block|{
name|lock
operator|.
name|unlock
argument_list|()
expr_stmt|;
block|}
block|}
specifier|private
name|void
name|recordAckFileReferences
parameter_list|(
name|MessageAckWithLocation
name|ack
parameter_list|,
name|int
name|messageFileId
parameter_list|)
block|{
name|adapter
operator|.
name|recordAckFileReferences
argument_list|(
name|ack
operator|.
name|location
operator|.
name|getDataFileId
argument_list|()
argument_list|,
name|messageFileId
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|removeAllMessages
parameter_list|(
name|ConnectionContext
name|context
parameter_list|)
throws|throws
name|IOException
block|{
name|lock
operator|.
name|lock
argument_list|()
expr_stmt|;
try|try
block|{
name|Set
argument_list|<
name|MessageId
argument_list|>
name|tmpSet
init|=
operator|new
name|HashSet
argument_list|<
name|MessageId
argument_list|>
argument_list|(
name|messageContainer
operator|.
name|keySet
argument_list|()
argument_list|)
decl_stmt|;
name|MessageAck
name|ack
init|=
operator|new
name|MessageAck
argument_list|()
decl_stmt|;
for|for
control|(
name|MessageId
name|id
range|:
name|tmpSet
control|)
block|{
name|ack
operator|.
name|setLastMessageId
argument_list|(
name|id
argument_list|)
expr_stmt|;
name|removeMessage
argument_list|(
literal|null
argument_list|,
name|ack
argument_list|)
expr_stmt|;
block|}
name|resetBatching
argument_list|()
expr_stmt|;
name|messageContainer
operator|.
name|clear
argument_list|()
expr_stmt|;
block|}
finally|finally
block|{
name|lock
operator|.
name|unlock
argument_list|()
expr_stmt|;
block|}
block|}
specifier|public
name|void
name|delete
parameter_list|()
block|{
name|lock
operator|.
name|lock
argument_list|()
expr_stmt|;
try|try
block|{
name|messageContainer
operator|.
name|clear
argument_list|()
expr_stmt|;
block|}
finally|finally
block|{
name|lock
operator|.
name|unlock
argument_list|()
expr_stmt|;
block|}
block|}
specifier|public
name|void
name|resetBatching
parameter_list|()
block|{
name|lock
operator|.
name|lock
argument_list|()
expr_stmt|;
try|try
block|{
name|batchEntry
operator|=
literal|null
expr_stmt|;
name|lastBatchId
operator|=
literal|null
expr_stmt|;
block|}
finally|finally
block|{
name|lock
operator|.
name|unlock
argument_list|()
expr_stmt|;
block|}
block|}
specifier|public
name|int
name|getMessageCount
parameter_list|()
block|{
return|return
name|messageContainer
operator|.
name|size
argument_list|()
return|;
block|}
specifier|public
name|boolean
name|isSupportForCursors
parameter_list|()
block|{
return|return
literal|true
return|;
block|}
specifier|public
name|boolean
name|supportsExternalBatchControl
parameter_list|()
block|{
return|return
literal|true
return|;
block|}
name|void
name|removeInterest
parameter_list|(
name|ReferenceRecord
name|rr
parameter_list|)
block|{
name|adapter
operator|.
name|removeInterestInRecordFile
argument_list|(
name|rr
operator|.
name|getData
argument_list|()
operator|.
name|getFileId
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|void
name|addInterest
parameter_list|(
name|ReferenceRecord
name|rr
parameter_list|)
block|{
name|adapter
operator|.
name|addInterestInRecordFile
argument_list|(
name|rr
operator|.
name|getData
argument_list|()
operator|.
name|getFileId
argument_list|()
argument_list|)
expr_stmt|;
block|}
comment|/**      * @param startAfter      * @see org.apache.activemq.store.ReferenceStore#setBatch(org.apache.activemq.command.MessageId)      */
specifier|public
name|void
name|setBatch
parameter_list|(
name|MessageId
name|startAfter
parameter_list|)
block|{
name|lock
operator|.
name|lock
argument_list|()
expr_stmt|;
try|try
block|{
name|batchEntry
operator|=
name|messageContainer
operator|.
name|getEntry
argument_list|(
name|startAfter
argument_list|)
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
literal|"setBatch: "
operator|+
name|startAfter
argument_list|)
expr_stmt|;
block|}
block|}
finally|finally
block|{
name|lock
operator|.
name|unlock
argument_list|()
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

