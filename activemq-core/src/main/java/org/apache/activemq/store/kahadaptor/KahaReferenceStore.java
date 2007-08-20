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
name|apache
operator|.
name|activemq
operator|.
name|usage
operator|.
name|MemoryUsage
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

begin_class
specifier|public
class|class
name|KahaReferenceStore
implements|implements
name|ReferenceStore
block|{
specifier|protected
specifier|final
name|ActiveMQDestination
name|destination
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
specifier|private
name|StoreEntry
name|batchEntry
decl_stmt|;
specifier|private
name|String
name|lastBatchId
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
name|this
operator|.
name|destination
operator|=
name|destination
expr_stmt|;
block|}
specifier|public
name|void
name|start
parameter_list|()
block|{     }
specifier|public
name|void
name|stop
parameter_list|()
block|{     }
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
specifier|synchronized
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
specifier|synchronized
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
if|if
condition|(
name|listener
operator|.
name|hasSpace
argument_list|()
condition|)
block|{
name|listener
operator|.
name|recoverMessageReference
argument_list|(
operator|new
name|MessageId
argument_list|(
name|record
operator|.
name|getMessageId
argument_list|()
argument_list|)
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
specifier|public
specifier|synchronized
name|void
name|recover
parameter_list|(
name|MessageRecoveryListener
name|listener
parameter_list|)
throws|throws
name|Exception
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
specifier|public
specifier|synchronized
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
name|recoverReference
argument_list|(
name|listener
argument_list|,
name|msg
argument_list|)
expr_stmt|;
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
specifier|public
specifier|synchronized
name|void
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
name|addInterest
argument_list|(
name|record
argument_list|)
expr_stmt|;
block|}
specifier|public
specifier|synchronized
name|ReferenceData
name|getMessageReference
parameter_list|(
name|MessageId
name|identity
parameter_list|)
throws|throws
name|IOException
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
specifier|public
name|void
name|addReferenceFileIdsInUse
parameter_list|()
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
name|msg
init|=
operator|(
name|ReferenceRecord
operator|)
name|messageContainer
operator|.
name|getValue
argument_list|(
name|entry
argument_list|)
decl_stmt|;
name|addInterest
argument_list|(
name|msg
argument_list|)
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
name|removeMessage
argument_list|(
name|ack
operator|.
name|getLastMessageId
argument_list|()
argument_list|)
expr_stmt|;
block|}
specifier|public
specifier|synchronized
name|void
name|removeMessage
parameter_list|(
name|MessageId
name|msgId
parameter_list|)
throws|throws
name|IOException
block|{
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
specifier|public
specifier|synchronized
name|void
name|removeAllMessages
parameter_list|(
name|ConnectionContext
name|context
parameter_list|)
throws|throws
name|IOException
block|{
name|messageContainer
operator|.
name|clear
argument_list|()
expr_stmt|;
block|}
specifier|public
name|ActiveMQDestination
name|getDestination
parameter_list|()
block|{
return|return
name|destination
return|;
block|}
specifier|public
specifier|synchronized
name|void
name|delete
parameter_list|()
block|{
name|messageContainer
operator|.
name|clear
argument_list|()
expr_stmt|;
block|}
specifier|public
specifier|synchronized
name|void
name|resetBatching
parameter_list|()
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
name|void
name|setMemoryUsage
parameter_list|(
name|MemoryUsage
name|memoryUsage
parameter_list|)
block|{     }
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
block|{     }
block|}
end_class

end_unit

