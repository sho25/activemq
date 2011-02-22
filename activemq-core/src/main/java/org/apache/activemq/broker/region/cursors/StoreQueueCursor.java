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
name|usage
operator|.
name|SystemUsage
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
comment|/**  * Store based Cursor for Queues  *   *   */
end_comment

begin_class
specifier|public
class|class
name|StoreQueueCursor
extends|extends
name|AbstractPendingMessageCursor
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
name|StoreQueueCursor
operator|.
name|class
argument_list|)
decl_stmt|;
specifier|private
specifier|final
name|Broker
name|broker
decl_stmt|;
specifier|private
name|int
name|pendingCount
decl_stmt|;
specifier|private
specifier|final
name|Queue
name|queue
decl_stmt|;
specifier|private
name|PendingMessageCursor
name|nonPersistent
decl_stmt|;
specifier|private
specifier|final
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
comment|/**      * Construct      * @param broker       * @param queue      */
specifier|public
name|StoreQueueCursor
parameter_list|(
name|Broker
name|broker
parameter_list|,
name|Queue
name|queue
parameter_list|)
block|{
name|super
argument_list|(
operator|(
name|queue
operator|!=
literal|null
condition|?
name|queue
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
name|broker
operator|=
name|broker
expr_stmt|;
name|this
operator|.
name|queue
operator|=
name|queue
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
name|currentCursor
operator|=
name|persistent
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
name|super
operator|.
name|start
argument_list|()
expr_stmt|;
if|if
condition|(
name|nonPersistent
operator|==
literal|null
condition|)
block|{
if|if
condition|(
name|broker
operator|.
name|getBrokerService
argument_list|()
operator|.
name|isPersistent
argument_list|()
condition|)
block|{
name|nonPersistent
operator|=
operator|new
name|FilePendingMessageCursor
argument_list|(
name|broker
argument_list|,
name|queue
operator|.
name|getName
argument_list|()
argument_list|,
name|this
operator|.
name|prioritizedMessages
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|nonPersistent
operator|=
operator|new
name|VMPendingMessageCursor
argument_list|(
name|this
operator|.
name|prioritizedMessages
argument_list|)
expr_stmt|;
block|}
name|nonPersistent
operator|.
name|setMaxBatchSize
argument_list|(
name|getMaxBatchSize
argument_list|()
argument_list|)
expr_stmt|;
name|nonPersistent
operator|.
name|setSystemUsage
argument_list|(
name|systemUsage
argument_list|)
expr_stmt|;
name|nonPersistent
operator|.
name|setEnableAudit
argument_list|(
name|isEnableAudit
argument_list|()
argument_list|)
expr_stmt|;
name|nonPersistent
operator|.
name|setMaxAuditDepth
argument_list|(
name|getMaxAuditDepth
argument_list|()
argument_list|)
expr_stmt|;
name|nonPersistent
operator|.
name|setMaxProducersToAudit
argument_list|(
name|getMaxProducersToAudit
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|nonPersistent
operator|.
name|setMessageAudit
argument_list|(
name|getMessageAudit
argument_list|()
argument_list|)
expr_stmt|;
name|nonPersistent
operator|.
name|start
argument_list|()
expr_stmt|;
name|persistent
operator|.
name|setMessageAudit
argument_list|(
name|getMessageAudit
argument_list|()
argument_list|)
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
operator|+
name|nonPersistent
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
name|nonPersistent
operator|.
name|gc
argument_list|()
expr_stmt|;
block|}
name|persistent
operator|.
name|stop
argument_list|()
expr_stmt|;
name|persistent
operator|.
name|gc
argument_list|()
expr_stmt|;
name|super
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
name|addMessageFirst
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
name|addMessageFirst
argument_list|(
name|node
argument_list|)
expr_stmt|;
block|}
block|}
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
try|try
block|{
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
name|LOG
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
return|return
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
return|;
block|}
specifier|public
specifier|synchronized
name|MessageReference
name|next
parameter_list|()
block|{
name|MessageReference
name|result
init|=
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
decl_stmt|;
return|return
name|result
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
operator|!
name|node
operator|.
name|isPersistent
argument_list|()
condition|)
block|{
name|nonPersistent
operator|.
name|remove
argument_list|(
name|node
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|persistent
operator|.
name|remove
argument_list|(
name|node
argument_list|)
expr_stmt|;
block|}
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
name|persistent
operator|.
name|reset
argument_list|()
expr_stmt|;
name|pendingCount
operator|=
name|persistent
operator|.
name|size
argument_list|()
operator|+
name|nonPersistent
operator|.
name|size
argument_list|()
expr_stmt|;
block|}
specifier|public
name|void
name|release
parameter_list|()
block|{
name|nonPersistent
operator|.
name|release
argument_list|()
expr_stmt|;
name|persistent
operator|.
name|release
argument_list|()
expr_stmt|;
block|}
specifier|public
specifier|synchronized
name|int
name|size
parameter_list|()
block|{
if|if
condition|(
name|pendingCount
operator|<
literal|0
condition|)
block|{
name|pendingCount
operator|=
name|persistent
operator|.
name|size
argument_list|()
operator|+
name|nonPersistent
operator|.
name|size
argument_list|()
expr_stmt|;
block|}
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
comment|// if negative, more messages arrived in store since last reset so non empty
return|return
name|pendingCount
operator|==
literal|0
return|;
block|}
comment|/**      * Informs the Broker if the subscription needs to intervention to recover      * it's state e.g. DurableTopicSubscriber may do      *       * @see org.apache.activemq.broker.region.cursors.PendingMessageCursor      * @return true if recovery required      */
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
specifier|public
name|void
name|setMaxProducersToAudit
parameter_list|(
name|int
name|maxProducersToAudit
parameter_list|)
block|{
name|super
operator|.
name|setMaxProducersToAudit
argument_list|(
name|maxProducersToAudit
argument_list|)
expr_stmt|;
if|if
condition|(
name|persistent
operator|!=
literal|null
condition|)
block|{
name|persistent
operator|.
name|setMaxProducersToAudit
argument_list|(
name|maxProducersToAudit
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|nonPersistent
operator|!=
literal|null
condition|)
block|{
name|nonPersistent
operator|.
name|setMaxProducersToAudit
argument_list|(
name|maxProducersToAudit
argument_list|)
expr_stmt|;
block|}
block|}
specifier|public
name|void
name|setMaxAuditDepth
parameter_list|(
name|int
name|maxAuditDepth
parameter_list|)
block|{
name|super
operator|.
name|setMaxAuditDepth
argument_list|(
name|maxAuditDepth
argument_list|)
expr_stmt|;
if|if
condition|(
name|persistent
operator|!=
literal|null
condition|)
block|{
name|persistent
operator|.
name|setMaxAuditDepth
argument_list|(
name|maxAuditDepth
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|nonPersistent
operator|!=
literal|null
condition|)
block|{
name|nonPersistent
operator|.
name|setMaxAuditDepth
argument_list|(
name|maxAuditDepth
argument_list|)
expr_stmt|;
block|}
block|}
specifier|public
name|void
name|setEnableAudit
parameter_list|(
name|boolean
name|enableAudit
parameter_list|)
block|{
name|super
operator|.
name|setEnableAudit
argument_list|(
name|enableAudit
argument_list|)
expr_stmt|;
if|if
condition|(
name|persistent
operator|!=
literal|null
condition|)
block|{
name|persistent
operator|.
name|setEnableAudit
argument_list|(
name|enableAudit
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|nonPersistent
operator|!=
literal|null
condition|)
block|{
name|nonPersistent
operator|.
name|setEnableAudit
argument_list|(
name|enableAudit
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Override
specifier|public
name|void
name|setUseCache
parameter_list|(
name|boolean
name|useCache
parameter_list|)
block|{
name|super
operator|.
name|setUseCache
argument_list|(
name|useCache
argument_list|)
expr_stmt|;
if|if
condition|(
name|persistent
operator|!=
literal|null
condition|)
block|{
name|persistent
operator|.
name|setUseCache
argument_list|(
name|useCache
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|nonPersistent
operator|!=
literal|null
condition|)
block|{
name|nonPersistent
operator|.
name|setUseCache
argument_list|(
name|useCache
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Override
specifier|public
name|void
name|setMemoryUsageHighWaterMark
parameter_list|(
name|int
name|memoryUsageHighWaterMark
parameter_list|)
block|{
name|super
operator|.
name|setMemoryUsageHighWaterMark
argument_list|(
name|memoryUsageHighWaterMark
argument_list|)
expr_stmt|;
if|if
condition|(
name|persistent
operator|!=
literal|null
condition|)
block|{
name|persistent
operator|.
name|setMemoryUsageHighWaterMark
argument_list|(
name|memoryUsageHighWaterMark
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|nonPersistent
operator|!=
literal|null
condition|)
block|{
name|nonPersistent
operator|.
name|setMemoryUsageHighWaterMark
argument_list|(
name|memoryUsageHighWaterMark
argument_list|)
expr_stmt|;
block|}
block|}
specifier|public
specifier|synchronized
name|void
name|gc
parameter_list|()
block|{
if|if
condition|(
name|persistent
operator|!=
literal|null
condition|)
block|{
name|persistent
operator|.
name|gc
argument_list|()
expr_stmt|;
block|}
if|if
condition|(
name|nonPersistent
operator|!=
literal|null
condition|)
block|{
name|nonPersistent
operator|.
name|gc
argument_list|()
expr_stmt|;
block|}
name|pendingCount
operator|=
name|persistent
operator|.
name|size
argument_list|()
operator|+
name|nonPersistent
operator|.
name|size
argument_list|()
expr_stmt|;
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
if|if
condition|(
name|persistent
operator|!=
literal|null
condition|)
block|{
name|persistent
operator|.
name|setSystemUsage
argument_list|(
name|usageManager
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|nonPersistent
operator|!=
literal|null
condition|)
block|{
name|nonPersistent
operator|.
name|setSystemUsage
argument_list|(
name|usageManager
argument_list|)
expr_stmt|;
block|}
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
operator|!
name|currentCursor
operator|.
name|hasMessagesBufferedToDeliver
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
comment|// sanity check
if|if
condition|(
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
block|}
return|return
name|currentCursor
return|;
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|isCacheEnabled
parameter_list|()
block|{
name|boolean
name|cacheEnabled
init|=
name|isUseCache
argument_list|()
decl_stmt|;
if|if
condition|(
name|cacheEnabled
condition|)
block|{
if|if
condition|(
name|persistent
operator|!=
literal|null
condition|)
block|{
name|cacheEnabled
operator|&=
name|persistent
operator|.
name|isCacheEnabled
argument_list|()
expr_stmt|;
block|}
if|if
condition|(
name|nonPersistent
operator|!=
literal|null
condition|)
block|{
name|cacheEnabled
operator|&=
name|nonPersistent
operator|.
name|isCacheEnabled
argument_list|()
expr_stmt|;
block|}
name|setCacheEnabled
argument_list|(
name|cacheEnabled
argument_list|)
expr_stmt|;
block|}
return|return
name|cacheEnabled
return|;
block|}
block|}
end_class

end_unit

