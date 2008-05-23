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
name|Collections
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
name|usage
operator|.
name|SystemUsage
import|;
end_import

begin_comment
comment|/**  * Abstract method holder for pending message (messages awaiting disptach to a  * consumer) cursor  *   * @version $Revision$  */
end_comment

begin_class
specifier|public
class|class
name|AbstractPendingMessageCursor
implements|implements
name|PendingMessageCursor
block|{
specifier|protected
name|int
name|memoryUsageHighWaterMark
init|=
literal|70
decl_stmt|;
specifier|protected
name|int
name|maxBatchSize
init|=
literal|100
decl_stmt|;
specifier|protected
name|SystemUsage
name|systemUsage
decl_stmt|;
specifier|protected
name|int
name|maxProducersToAudit
init|=
literal|1024
decl_stmt|;
specifier|protected
name|int
name|maxAuditDepth
init|=
literal|1000
decl_stmt|;
specifier|protected
name|boolean
name|enableAudit
init|=
literal|true
decl_stmt|;
specifier|protected
name|ActiveMQMessageAudit
name|audit
decl_stmt|;
specifier|protected
name|boolean
name|useCache
init|=
literal|false
decl_stmt|;
specifier|private
name|boolean
name|started
init|=
literal|false
decl_stmt|;
specifier|public
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
name|started
operator|&&
name|enableAudit
operator|&&
name|audit
operator|==
literal|null
condition|)
block|{
name|audit
operator|=
operator|new
name|ActiveMQMessageAudit
argument_list|(
name|maxAuditDepth
argument_list|,
name|maxProducersToAudit
argument_list|)
expr_stmt|;
block|}
name|started
operator|=
literal|true
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
name|audit
operator|=
literal|null
expr_stmt|;
name|gc
argument_list|()
expr_stmt|;
block|}
specifier|public
name|void
name|add
parameter_list|(
name|ConnectionContext
name|context
parameter_list|,
name|Destination
name|destination
parameter_list|)
throws|throws
name|Exception
block|{     }
annotation|@
name|SuppressWarnings
argument_list|(
literal|"unchecked"
argument_list|)
specifier|public
name|List
argument_list|<
name|MessageReference
argument_list|>
name|remove
parameter_list|(
name|ConnectionContext
name|context
parameter_list|,
name|Destination
name|destination
parameter_list|)
throws|throws
name|Exception
block|{
return|return
name|Collections
operator|.
name|EMPTY_LIST
return|;
block|}
specifier|public
name|boolean
name|isRecoveryRequired
parameter_list|()
block|{
return|return
literal|true
return|;
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
block|{     }
specifier|public
name|void
name|addMessageLast
parameter_list|(
name|MessageReference
name|node
parameter_list|)
throws|throws
name|Exception
block|{     }
specifier|public
name|void
name|addRecoveredMessage
parameter_list|(
name|MessageReference
name|node
parameter_list|)
throws|throws
name|Exception
block|{
name|addMessageLast
argument_list|(
name|node
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|clear
parameter_list|()
block|{     }
specifier|public
name|boolean
name|hasNext
parameter_list|()
block|{
return|return
literal|false
return|;
block|}
specifier|public
name|boolean
name|isEmpty
parameter_list|()
block|{
return|return
literal|false
return|;
block|}
specifier|public
name|boolean
name|isEmpty
parameter_list|(
name|Destination
name|destination
parameter_list|)
block|{
return|return
name|isEmpty
argument_list|()
return|;
block|}
specifier|public
name|MessageReference
name|next
parameter_list|()
block|{
return|return
literal|null
return|;
block|}
specifier|public
name|void
name|remove
parameter_list|()
block|{     }
specifier|public
name|void
name|reset
parameter_list|()
block|{     }
specifier|public
name|int
name|size
parameter_list|()
block|{
return|return
literal|0
return|;
block|}
specifier|public
name|int
name|getMaxBatchSize
parameter_list|()
block|{
return|return
name|maxBatchSize
return|;
block|}
specifier|public
name|void
name|setMaxBatchSize
parameter_list|(
name|int
name|maxBatchSize
parameter_list|)
block|{
name|this
operator|.
name|maxBatchSize
operator|=
name|maxBatchSize
expr_stmt|;
block|}
specifier|protected
name|void
name|fillBatch
parameter_list|()
throws|throws
name|Exception
block|{     }
specifier|public
name|void
name|resetForGC
parameter_list|()
block|{
name|reset
argument_list|()
expr_stmt|;
block|}
specifier|public
name|void
name|remove
parameter_list|(
name|MessageReference
name|node
parameter_list|)
block|{     }
specifier|public
name|void
name|gc
parameter_list|()
block|{     }
specifier|public
name|void
name|setSystemUsage
parameter_list|(
name|SystemUsage
name|usageManager
parameter_list|)
block|{
name|this
operator|.
name|systemUsage
operator|=
name|usageManager
expr_stmt|;
block|}
specifier|public
name|boolean
name|hasSpace
parameter_list|()
block|{
return|return
name|systemUsage
operator|!=
literal|null
condition|?
operator|(
name|systemUsage
operator|.
name|getMemoryUsage
argument_list|()
operator|.
name|getPercentUsage
argument_list|()
operator|<
name|memoryUsageHighWaterMark
operator|)
else|:
literal|true
return|;
block|}
specifier|public
name|boolean
name|isFull
parameter_list|()
block|{
return|return
name|systemUsage
operator|!=
literal|null
condition|?
name|systemUsage
operator|.
name|getMemoryUsage
argument_list|()
operator|.
name|isFull
argument_list|()
else|:
literal|false
return|;
block|}
specifier|public
name|void
name|release
parameter_list|()
block|{     }
specifier|public
name|boolean
name|hasMessagesBufferedToDeliver
parameter_list|()
block|{
return|return
literal|false
return|;
block|}
comment|/**      * @return the memoryUsageHighWaterMark      */
specifier|public
name|int
name|getMemoryUsageHighWaterMark
parameter_list|()
block|{
return|return
name|this
operator|.
name|memoryUsageHighWaterMark
return|;
block|}
comment|/**      * @param memoryUsageHighWaterMark the memoryUsageHighWaterMark to set      */
specifier|public
name|void
name|setMemoryUsageHighWaterMark
parameter_list|(
name|int
name|memoryUsageHighWaterMark
parameter_list|)
block|{
name|this
operator|.
name|memoryUsageHighWaterMark
operator|=
name|memoryUsageHighWaterMark
expr_stmt|;
block|}
comment|/**      * @return the usageManager      */
specifier|public
name|SystemUsage
name|getSystemUsage
parameter_list|()
block|{
return|return
name|this
operator|.
name|systemUsage
return|;
block|}
comment|/**      * destroy the cursor      *       * @throws Exception      */
specifier|public
name|void
name|destroy
parameter_list|()
throws|throws
name|Exception
block|{
name|stop
argument_list|()
expr_stmt|;
block|}
comment|/**      * Page in a restricted number of messages      *       * @param maxItems      * @return a list of paged in messages      */
specifier|public
name|LinkedList
name|pageInList
parameter_list|(
name|int
name|maxItems
parameter_list|)
block|{
throw|throw
operator|new
name|RuntimeException
argument_list|(
literal|"Not supported"
argument_list|)
throw|;
block|}
comment|/**      * @return the maxProducersToAudit      */
specifier|public
name|int
name|getMaxProducersToAudit
parameter_list|()
block|{
return|return
name|maxProducersToAudit
return|;
block|}
comment|/**      * @param maxProducersToAudit the maxProducersToAudit to set      */
specifier|public
specifier|synchronized
name|void
name|setMaxProducersToAudit
parameter_list|(
name|int
name|maxProducersToAudit
parameter_list|)
block|{
name|this
operator|.
name|maxProducersToAudit
operator|=
name|maxProducersToAudit
expr_stmt|;
if|if
condition|(
name|audit
operator|!=
literal|null
condition|)
block|{
name|this
operator|.
name|audit
operator|.
name|setMaximumNumberOfProducersToTrack
argument_list|(
name|maxProducersToAudit
argument_list|)
expr_stmt|;
block|}
block|}
comment|/**      * @return the maxAuditDepth      */
specifier|public
name|int
name|getMaxAuditDepth
parameter_list|()
block|{
return|return
name|this
operator|.
name|maxAuditDepth
return|;
block|}
comment|/**      * @param maxAuditDepth the maxAuditDepth to set      */
specifier|public
specifier|synchronized
name|void
name|setMaxAuditDepth
parameter_list|(
name|int
name|maxAuditDepth
parameter_list|)
block|{
name|this
operator|.
name|maxAuditDepth
operator|=
name|maxAuditDepth
expr_stmt|;
if|if
condition|(
name|audit
operator|!=
literal|null
condition|)
block|{
name|this
operator|.
name|audit
operator|.
name|setAuditDepth
argument_list|(
name|maxAuditDepth
argument_list|)
expr_stmt|;
block|}
block|}
comment|/**      * @return the enableAudit      */
specifier|public
name|boolean
name|isEnableAudit
parameter_list|()
block|{
return|return
name|this
operator|.
name|enableAudit
return|;
block|}
comment|/**      * @param enableAudit the enableAudit to set      */
specifier|public
specifier|synchronized
name|void
name|setEnableAudit
parameter_list|(
name|boolean
name|enableAudit
parameter_list|)
block|{
name|this
operator|.
name|enableAudit
operator|=
name|enableAudit
expr_stmt|;
if|if
condition|(
name|this
operator|.
name|enableAudit
operator|&&
name|started
operator|&&
name|audit
operator|==
literal|null
condition|)
block|{
name|audit
operator|=
operator|new
name|ActiveMQMessageAudit
argument_list|(
name|maxAuditDepth
argument_list|,
name|maxProducersToAudit
argument_list|)
expr_stmt|;
block|}
block|}
specifier|public
name|boolean
name|isTransient
parameter_list|()
block|{
return|return
literal|false
return|;
block|}
comment|/**      * Mark a message as already dispatched      * @param message      */
specifier|public
name|void
name|dispatched
parameter_list|(
name|MessageReference
name|message
parameter_list|)
block|{
comment|//add it to the audit
name|isDuplicate
argument_list|(
name|message
operator|.
name|getMessageId
argument_list|()
argument_list|)
expr_stmt|;
block|}
comment|/**      * set the audit      * @param audit      */
specifier|public
name|void
name|setMessageAudit
parameter_list|(
name|ActiveMQMessageAudit
name|audit
parameter_list|)
block|{
name|this
operator|.
name|audit
operator|=
name|audit
expr_stmt|;
block|}
comment|/**      * @return the audit      */
specifier|public
name|ActiveMQMessageAudit
name|getMessageAudit
parameter_list|()
block|{
return|return
name|audit
return|;
block|}
specifier|public
name|boolean
name|isUseCache
parameter_list|()
block|{
return|return
name|useCache
return|;
block|}
specifier|public
name|void
name|setUseCache
parameter_list|(
name|boolean
name|useCache
parameter_list|)
block|{
name|this
operator|.
name|useCache
operator|=
name|useCache
expr_stmt|;
block|}
specifier|protected
specifier|synchronized
name|boolean
name|isDuplicate
parameter_list|(
name|MessageId
name|messageId
parameter_list|)
block|{
if|if
condition|(
operator|!
name|this
operator|.
name|enableAudit
operator|||
name|this
operator|.
name|audit
operator|==
literal|null
condition|)
block|{
return|return
literal|false
return|;
block|}
return|return
name|this
operator|.
name|audit
operator|.
name|isDuplicate
argument_list|(
name|messageId
argument_list|)
return|;
block|}
specifier|protected
specifier|synchronized
name|void
name|rollback
parameter_list|(
name|MessageId
name|id
parameter_list|)
block|{
if|if
condition|(
name|this
operator|.
name|audit
operator|!=
literal|null
condition|)
block|{
name|audit
operator|.
name|rollback
argument_list|(
name|id
argument_list|)
expr_stmt|;
block|}
block|}
specifier|protected
specifier|synchronized
name|boolean
name|isStarted
parameter_list|()
block|{
return|return
name|started
return|;
block|}
block|}
end_class

end_unit

