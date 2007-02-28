begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  *   * Licensed to the Apache Software Foundation (ASF) under one or more contributor license agreements. See the NOTICE  * file distributed with this work for additional information regarding copyright ownership. The ASF licenses this file  * to You under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the  * License. You may obtain a copy of the License at  *   * http://www.apache.org/licenses/LICENSE-2.0  *   * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on  * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the  * specific language governing permissions and limitations under the License.  */
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
name|LinkedList
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
name|Topic
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
name|store
operator|.
name|TopicMessageStore
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
comment|/**  * perist pendingCount messages pendingCount message (messages awaiting disptach to a consumer) cursor  *   * @version $Revision$  */
end_comment

begin_class
class|class
name|TopicStorePrefetch
extends|extends
name|AbstractPendingMessageCursor
implements|implements
name|MessageRecoveryListener
block|{
specifier|static
specifier|private
specifier|final
name|Log
name|log
init|=
name|LogFactory
operator|.
name|getLog
argument_list|(
name|TopicStorePrefetch
operator|.
name|class
argument_list|)
decl_stmt|;
specifier|private
name|TopicMessageStore
name|store
decl_stmt|;
specifier|private
specifier|final
name|LinkedList
argument_list|<
name|Message
argument_list|>
name|batchList
init|=
operator|new
name|LinkedList
argument_list|<
name|Message
argument_list|>
argument_list|()
decl_stmt|;
specifier|private
name|String
name|clientId
decl_stmt|;
specifier|private
name|String
name|subscriberName
decl_stmt|;
specifier|private
name|Destination
name|regionDestination
decl_stmt|;
specifier|private
name|MessageId
name|firstMessageId
decl_stmt|;
specifier|private
name|MessageId
name|lastMessageId
decl_stmt|;
specifier|private
name|int
name|pendingCount
decl_stmt|;
specifier|private
name|boolean
name|started
decl_stmt|;
comment|/**      * @param topic      * @param clientId      * @param subscriberName      */
specifier|public
name|TopicStorePrefetch
parameter_list|(
name|Topic
name|topic
parameter_list|,
name|String
name|clientId
parameter_list|,
name|String
name|subscriberName
parameter_list|)
block|{
name|this
operator|.
name|regionDestination
operator|=
name|topic
expr_stmt|;
name|this
operator|.
name|store
operator|=
operator|(
name|TopicMessageStore
operator|)
name|topic
operator|.
name|getMessageStore
argument_list|()
expr_stmt|;
name|this
operator|.
name|clientId
operator|=
name|clientId
expr_stmt|;
name|this
operator|.
name|subscriberName
operator|=
name|subscriberName
expr_stmt|;
block|}
specifier|public
specifier|synchronized
name|void
name|start
parameter_list|()
block|{
if|if
condition|(
operator|!
name|started
condition|)
block|{
name|started
operator|=
literal|true
expr_stmt|;
name|pendingCount
operator|=
name|getStoreSize
argument_list|()
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
name|log
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
block|}
specifier|public
specifier|synchronized
name|void
name|stop
parameter_list|()
block|{
if|if
condition|(
name|started
condition|)
block|{
name|started
operator|=
literal|false
expr_stmt|;
name|store
operator|.
name|resetBatching
argument_list|(
name|clientId
argument_list|,
name|subscriberName
argument_list|)
expr_stmt|;
name|gc
argument_list|()
expr_stmt|;
block|}
block|}
comment|/**      * @return true if there are no pendingCount messages      */
specifier|public
name|boolean
name|isEmpty
parameter_list|()
block|{
return|return
name|pendingCount
operator|<=
literal|0
return|;
block|}
specifier|public
specifier|synchronized
name|int
name|size
parameter_list|()
block|{
return|return
name|getPendingCount
argument_list|()
return|;
block|}
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
if|if
condition|(
name|node
operator|!=
literal|null
condition|)
block|{
if|if
condition|(
name|isEmpty
argument_list|()
operator|&&
name|started
condition|)
block|{
name|firstMessageId
operator|=
name|node
operator|.
name|getMessageId
argument_list|()
expr_stmt|;
block|}
name|lastMessageId
operator|=
name|node
operator|.
name|getMessageId
argument_list|()
expr_stmt|;
name|node
operator|.
name|decrementReferenceCount
argument_list|()
expr_stmt|;
name|pendingCount
operator|++
expr_stmt|;
block|}
block|}
specifier|public
name|void
name|addMessageFirst
parameter_list|(
name|MessageReference
name|node
parameter_list|)
throws|throws
name|Exception
block|{
if|if
condition|(
name|node
operator|!=
literal|null
condition|)
block|{
if|if
condition|(
name|started
condition|)
block|{
name|firstMessageId
operator|=
name|node
operator|.
name|getMessageId
argument_list|()
expr_stmt|;
block|}
name|node
operator|.
name|decrementReferenceCount
argument_list|()
expr_stmt|;
name|pendingCount
operator|++
expr_stmt|;
block|}
block|}
specifier|public
specifier|synchronized
name|void
name|remove
parameter_list|()
block|{
name|pendingCount
operator|--
expr_stmt|;
block|}
specifier|public
specifier|synchronized
name|void
name|remove
parameter_list|(
name|MessageReference
name|node
parameter_list|)
block|{
name|pendingCount
operator|--
expr_stmt|;
block|}
specifier|public
specifier|synchronized
name|void
name|clear
parameter_list|()
block|{
name|pendingCount
operator|=
literal|0
expr_stmt|;
block|}
specifier|public
specifier|synchronized
name|boolean
name|hasNext
parameter_list|()
block|{
return|return
operator|!
name|isEmpty
argument_list|()
return|;
block|}
specifier|public
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
name|isEmpty
argument_list|()
condition|)
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
specifier|final
name|Exception
name|e
parameter_list|)
block|{
name|log
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
name|batchList
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
return|return
literal|null
return|;
block|}
block|}
if|if
condition|(
operator|!
name|batchList
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
name|result
operator|=
name|batchList
operator|.
name|removeFirst
argument_list|()
expr_stmt|;
if|if
condition|(
name|lastMessageId
operator|!=
literal|null
condition|)
block|{
if|if
condition|(
name|result
operator|.
name|getMessageId
argument_list|()
operator|.
name|equals
argument_list|(
name|lastMessageId
argument_list|)
condition|)
block|{
comment|//pendingCount=0;
block|}
block|}
name|result
operator|.
name|setRegionDestination
argument_list|(
name|regionDestination
argument_list|)
expr_stmt|;
block|}
block|}
return|return
name|result
return|;
block|}
specifier|public
name|void
name|reset
parameter_list|()
block|{     }
comment|// MessageRecoveryListener implementation
specifier|public
name|void
name|finished
parameter_list|()
block|{     }
specifier|public
specifier|synchronized
name|void
name|recoverMessage
parameter_list|(
name|Message
name|message
parameter_list|)
throws|throws
name|Exception
block|{
name|message
operator|.
name|setRegionDestination
argument_list|(
name|regionDestination
argument_list|)
expr_stmt|;
comment|// only increment if count is zero (could have been cached)
if|if
condition|(
name|message
operator|.
name|getReferenceCount
argument_list|()
operator|==
literal|0
condition|)
block|{
name|message
operator|.
name|incrementReferenceCount
argument_list|()
expr_stmt|;
block|}
name|batchList
operator|.
name|addLast
argument_list|(
name|message
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|recoverMessageReference
parameter_list|(
name|MessageId
name|messageReference
parameter_list|)
throws|throws
name|Exception
block|{
comment|// shouldn't get called
throw|throw
operator|new
name|RuntimeException
argument_list|(
literal|"Not supported"
argument_list|)
throw|;
block|}
comment|// implementation
specifier|protected
specifier|synchronized
name|void
name|fillBatch
parameter_list|()
throws|throws
name|Exception
block|{
if|if
condition|(
operator|!
name|isEmpty
argument_list|()
condition|)
block|{
name|store
operator|.
name|recoverNextMessages
argument_list|(
name|clientId
argument_list|,
name|subscriberName
argument_list|,
name|maxBatchSize
argument_list|,
name|this
argument_list|)
expr_stmt|;
if|if
condition|(
name|firstMessageId
operator|!=
literal|null
condition|)
block|{
name|int
name|pos
init|=
literal|0
decl_stmt|;
for|for
control|(
name|Message
name|msg
range|:
name|batchList
control|)
block|{
if|if
condition|(
name|msg
operator|.
name|getMessageId
argument_list|()
operator|.
name|equals
argument_list|(
name|firstMessageId
argument_list|)
condition|)
block|{
name|firstMessageId
operator|=
literal|null
expr_stmt|;
break|break;
block|}
name|pos
operator|++
expr_stmt|;
block|}
if|if
condition|(
name|pos
operator|>
literal|0
condition|)
block|{
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|pos
operator|&&
operator|!
name|batchList
operator|.
name|isEmpty
argument_list|()
condition|;
name|i
operator|++
control|)
block|{
name|batchList
operator|.
name|removeFirst
argument_list|()
expr_stmt|;
block|}
if|if
condition|(
name|batchList
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
name|log
operator|.
name|debug
argument_list|(
literal|"Refilling batch - haven't got past first message = "
operator|+
name|firstMessageId
argument_list|)
expr_stmt|;
name|fillBatch
argument_list|()
expr_stmt|;
block|}
block|}
block|}
block|}
block|}
specifier|protected
specifier|synchronized
name|int
name|getPendingCount
parameter_list|()
block|{
if|if
condition|(
name|pendingCount
operator|<=
literal|0
condition|)
block|{
name|pendingCount
operator|=
name|getStoreSize
argument_list|()
expr_stmt|;
block|}
return|return
name|pendingCount
return|;
block|}
specifier|protected
specifier|synchronized
name|int
name|getStoreSize
parameter_list|()
block|{
try|try
block|{
return|return
name|store
operator|.
name|getMessageCount
argument_list|(
name|clientId
argument_list|,
name|subscriberName
argument_list|)
return|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
name|log
operator|.
name|error
argument_list|(
name|this
operator|+
literal|" Failed to get the outstanding message count from the store"
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
control|)
block|{
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
block|}
specifier|public
name|String
name|toString
parameter_list|()
block|{
return|return
literal|"TopicStorePrefetch"
operator|+
name|System
operator|.
name|identityHashCode
argument_list|(
name|this
argument_list|)
operator|+
literal|"("
operator|+
name|clientId
operator|+
literal|","
operator|+
name|subscriberName
operator|+
literal|")"
return|;
block|}
block|}
end_class

end_unit

