begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  *   * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements. See the NOTICE file distributed with this  * work for additional information regarding copyright ownership. The ASF  * licenses this file to You under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance with the License.  * You may obtain a copy of the License at  *   * http://www.apache.org/licenses/LICENSE-2.0  *   * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT  * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the  * License for the specific language governing permissions and limitations under  * the License.  */
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
name|Queue
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
name|MessageStore
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
comment|/**  * perist pending messages pending message (messages awaiting disptach to a  * consumer) cursor  *   * @version $Revision: 474985 $  */
end_comment

begin_class
class|class
name|QueueStorePrefetch
extends|extends
name|AbstractPendingMessageCursor
implements|implements
name|MessageRecoveryListener
block|{
specifier|static
specifier|private
specifier|final
name|Log
name|LOG
init|=
name|LogFactory
operator|.
name|getLog
argument_list|(
name|QueueStorePrefetch
operator|.
name|class
argument_list|)
decl_stmt|;
specifier|private
name|MessageStore
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
name|Destination
name|regionDestination
decl_stmt|;
specifier|private
name|int
name|size
decl_stmt|;
comment|/**      * @param topic      * @param clientId      * @param subscriberName      * @throws IOException      */
specifier|public
name|QueueStorePrefetch
parameter_list|(
name|Queue
name|queue
parameter_list|)
block|{
name|this
operator|.
name|regionDestination
operator|=
name|queue
expr_stmt|;
name|this
operator|.
name|store
operator|=
operator|(
name|MessageStore
operator|)
name|queue
operator|.
name|getMessageStore
argument_list|()
expr_stmt|;
block|}
specifier|public
name|void
name|start
parameter_list|()
throws|throws
name|Exception
block|{     }
specifier|public
name|void
name|stop
parameter_list|()
throws|throws
name|Exception
block|{
name|store
operator|.
name|resetBatching
argument_list|()
expr_stmt|;
name|gc
argument_list|()
expr_stmt|;
block|}
comment|/**      * @return true if there are no pending messages      */
specifier|public
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
specifier|synchronized
name|int
name|size
parameter_list|()
block|{
try|try
block|{
name|size
operator|=
name|store
operator|.
name|getMessageCount
argument_list|()
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
literal|"Failed to get message count"
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
return|return
name|size
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
name|size
operator|++
expr_stmt|;
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
name|size
operator|++
expr_stmt|;
block|}
specifier|public
name|void
name|remove
parameter_list|()
block|{
name|size
operator|--
expr_stmt|;
block|}
specifier|public
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
block|}
specifier|public
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
return|return
operator|!
name|batchList
operator|.
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
name|batchList
operator|.
name|removeFirst
argument_list|()
decl_stmt|;
name|result
operator|.
name|decrementReferenceCount
argument_list|()
expr_stmt|;
name|result
operator|.
name|setRegionDestination
argument_list|(
name|regionDestination
argument_list|)
expr_stmt|;
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
name|boolean
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
name|message
operator|.
name|incrementReferenceCount
argument_list|()
expr_stmt|;
name|batchList
operator|.
name|addLast
argument_list|(
name|message
argument_list|)
expr_stmt|;
return|return
literal|true
return|;
block|}
specifier|public
name|boolean
name|recoverMessageReference
parameter_list|(
name|MessageId
name|messageReference
parameter_list|)
throws|throws
name|Exception
block|{
name|Message
name|msg
init|=
name|store
operator|.
name|getMessage
argument_list|(
name|messageReference
argument_list|)
decl_stmt|;
if|if
condition|(
name|msg
operator|!=
literal|null
condition|)
block|{
return|return
name|recoverMessage
argument_list|(
name|msg
argument_list|)
return|;
block|}
else|else
block|{
name|String
name|err
init|=
literal|"Failed to retrieve message for id: "
operator|+
name|messageReference
decl_stmt|;
name|LOG
operator|.
name|error
argument_list|(
name|err
argument_list|)
expr_stmt|;
throw|throw
operator|new
name|IOException
argument_list|(
name|err
argument_list|)
throw|;
block|}
block|}
specifier|public
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
comment|// implementation
specifier|protected
name|void
name|fillBatch
parameter_list|()
throws|throws
name|Exception
block|{
name|store
operator|.
name|recoverNextMessages
argument_list|(
name|maxBatchSize
argument_list|,
name|this
argument_list|)
expr_stmt|;
block|}
specifier|public
name|String
name|toString
parameter_list|()
block|{
return|return
literal|"QueueStorePrefetch"
operator|+
name|System
operator|.
name|identityHashCode
argument_list|(
name|this
argument_list|)
return|;
block|}
block|}
end_class

end_unit

