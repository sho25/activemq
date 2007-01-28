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
name|memory
operator|.
name|UsageManager
import|;
end_import

begin_comment
comment|/**  * Abstract method holder for pending message (messages awaiting disptach to a consumer) cursor  *   * @version $Revision$  */
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
literal|90
decl_stmt|;
specifier|protected
name|int
name|maxBatchSize
init|=
literal|100
decl_stmt|;
specifier|protected
name|UsageManager
name|usageManager
decl_stmt|;
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
specifier|public
name|void
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
block|{     }
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
name|setUsageManager
parameter_list|(
name|UsageManager
name|usageManager
parameter_list|)
block|{
name|this
operator|.
name|usageManager
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
name|usageManager
operator|!=
literal|null
condition|?
operator|(
name|usageManager
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
name|usageManager
operator|!=
literal|null
condition|?
name|usageManager
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
block|{             }
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
name|UsageManager
name|getUsageManager
parameter_list|()
block|{
return|return
name|this
operator|.
name|usageManager
return|;
block|}
comment|/**      * destroy the cursor      * @throws Exception       */
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
comment|/**      * Page in a restricted number of messages      * @param maxItems      * @return a list of paged in messages      */
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
block|}
end_class

end_unit

