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
comment|/**  * Store based Cursor for Queues  *   * @version $Revision: 474985 $  */
end_comment

begin_class
specifier|public
class|class
name|StoreQueueCursor
extends|extends
name|AbstractPendingMessageCursor
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
name|StoreQueueCursor
operator|.
name|class
argument_list|)
decl_stmt|;
specifier|private
name|int
name|pendingCount
init|=
literal|0
decl_stmt|;
specifier|private
name|Queue
name|queue
decl_stmt|;
specifier|private
name|Store
name|tmpStore
decl_stmt|;
specifier|private
name|PendingMessageCursor
name|nonPersistent
decl_stmt|;
specifier|private
name|QueueStorePrefetch
name|persistent
decl_stmt|;
specifier|private
name|boolean
name|started
decl_stmt|;
specifier|private
name|PendingMessageCursor
name|currentCursor
decl_stmt|;
comment|/**      * Construct      *       * @param queue      * @param tmpStore      */
specifier|public
name|StoreQueueCursor
parameter_list|(
name|Queue
name|queue
parameter_list|,
name|Store
name|tmpStore
parameter_list|)
block|{
name|this
operator|.
name|queue
operator|=
name|queue
expr_stmt|;
name|this
operator|.
name|tmpStore
operator|=
name|tmpStore
expr_stmt|;
name|this
operator|.
name|persistent
operator|=
operator|new
name|QueueStorePrefetch
argument_list|(
name|queue
argument_list|)
expr_stmt|;
block|}
specifier|public
specifier|synchronized
name|void
name|start
parameter_list|()
throws|throws
name|Exception
block|{
name|started
operator|=
literal|true
expr_stmt|;
if|if
condition|(
name|nonPersistent
operator|==
literal|null
condition|)
block|{
name|nonPersistent
operator|=
operator|new
name|FilePendingMessageCursor
argument_list|(
name|queue
operator|.
name|getDestination
argument_list|()
argument_list|,
name|tmpStore
argument_list|)
expr_stmt|;
name|nonPersistent
operator|.
name|setMaxBatchSize
argument_list|(
name|getMaxBatchSize
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|nonPersistent
operator|.
name|start
argument_list|()
expr_stmt|;
name|persistent
operator|.
name|start
argument_list|()
expr_stmt|;
name|pendingCount
operator|=
name|persistent
operator|.
name|size
argument_list|()
expr_stmt|;
block|}
specifier|public
specifier|synchronized
name|void
name|stop
parameter_list|()
throws|throws
name|Exception
block|{
name|started
operator|=
literal|false
expr_stmt|;
if|if
condition|(
name|nonPersistent
operator|!=
literal|null
condition|)
block|{
name|nonPersistent
operator|.
name|stop
argument_list|()
expr_stmt|;
block|}
name|persistent
operator|.
name|stop
argument_list|()
expr_stmt|;
name|pendingCount
operator|=
literal|0
expr_stmt|;
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
name|Message
name|msg
init|=
name|node
operator|.
name|getMessage
argument_list|()
decl_stmt|;
if|if
condition|(
name|started
condition|)
block|{
name|pendingCount
operator|++
expr_stmt|;
if|if
condition|(
operator|!
name|msg
operator|.
name|isPersistent
argument_list|()
condition|)
block|{
name|nonPersistent
operator|.
name|addMessageLast
argument_list|(
name|node
argument_list|)
expr_stmt|;
block|}
block|}
if|if
condition|(
name|msg
operator|.
name|isPersistent
argument_list|()
condition|)
block|{
name|persistent
operator|.
name|addMessageLast
argument_list|(
name|node
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
name|boolean
name|result
init|=
name|pendingCount
operator|>
literal|0
decl_stmt|;
if|if
condition|(
name|result
condition|)
block|{
try|try
block|{
name|currentCursor
operator|=
name|getNextCursor
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
literal|"Failed to get current cursor "
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
name|result
operator|=
name|currentCursor
operator|!=
literal|null
condition|?
name|currentCursor
operator|.
name|hasNext
argument_list|()
else|:
literal|false
expr_stmt|;
block|}
return|return
name|result
return|;
block|}
specifier|public
specifier|synchronized
name|MessageReference
name|next
parameter_list|()
block|{
return|return
name|currentCursor
operator|!=
literal|null
condition|?
name|currentCursor
operator|.
name|next
argument_list|()
else|:
literal|null
return|;
block|}
specifier|public
specifier|synchronized
name|void
name|remove
parameter_list|()
block|{
if|if
condition|(
name|currentCursor
operator|!=
literal|null
condition|)
block|{
name|currentCursor
operator|.
name|remove
argument_list|()
expr_stmt|;
block|}
name|pendingCount
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
name|pendingCount
operator|--
expr_stmt|;
block|}
specifier|public
specifier|synchronized
name|void
name|reset
parameter_list|()
block|{
name|nonPersistent
operator|.
name|reset
argument_list|()
expr_stmt|;
block|}
specifier|public
name|int
name|size
parameter_list|()
block|{
return|return
name|pendingCount
return|;
block|}
specifier|public
specifier|synchronized
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
comment|/**      * Informs the Broker if the subscription needs to intervention to recover it's state e.g. DurableTopicSubscriber      * may do      *       * @see org.apache.activemq.region.cursors.PendingMessageCursor      * @return true if recovery required      */
specifier|public
name|boolean
name|isRecoveryRequired
parameter_list|()
block|{
return|return
literal|false
return|;
block|}
comment|/**      * @return the nonPersistent Cursor      */
specifier|public
name|PendingMessageCursor
name|getNonPersistent
parameter_list|()
block|{
return|return
name|this
operator|.
name|nonPersistent
return|;
block|}
comment|/**      * @param nonPersistent cursor to set      */
specifier|public
name|void
name|setNonPersistent
parameter_list|(
name|PendingMessageCursor
name|nonPersistent
parameter_list|)
block|{
name|this
operator|.
name|nonPersistent
operator|=
name|nonPersistent
expr_stmt|;
block|}
specifier|public
name|void
name|setMaxBatchSize
parameter_list|(
name|int
name|maxBatchSize
parameter_list|)
block|{
name|persistent
operator|.
name|setMaxBatchSize
argument_list|(
name|maxBatchSize
argument_list|)
expr_stmt|;
if|if
condition|(
name|nonPersistent
operator|!=
literal|null
condition|)
block|{
name|nonPersistent
operator|.
name|setMaxBatchSize
argument_list|(
name|maxBatchSize
argument_list|)
expr_stmt|;
block|}
name|super
operator|.
name|setMaxBatchSize
argument_list|(
name|maxBatchSize
argument_list|)
expr_stmt|;
block|}
specifier|protected
specifier|synchronized
name|PendingMessageCursor
name|getNextCursor
parameter_list|()
throws|throws
name|Exception
block|{
if|if
condition|(
name|currentCursor
operator|==
literal|null
operator|||
name|currentCursor
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
name|currentCursor
operator|=
name|currentCursor
operator|==
name|persistent
condition|?
name|nonPersistent
else|:
name|persistent
expr_stmt|;
block|}
return|return
name|currentCursor
return|;
block|}
block|}
end_class

end_unit

