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
name|Service
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
name|usage
operator|.
name|SystemUsage
import|;
end_import

begin_comment
comment|/**  * Interface to pending message (messages awaiting disptach to a consumer)  * cursor  *   * @version $Revision$  */
end_comment

begin_interface
specifier|public
interface|interface
name|PendingMessageCursor
extends|extends
name|Service
block|{
comment|/**      * Add a destination      *       * @param context      * @param destination      * @throws Exception      */
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
function_decl|;
comment|/**      * remove a destination      *       * @param context      * @param destination      * @throws Exception      */
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
function_decl|;
comment|/**      * @return true if there are no pending messages      */
name|boolean
name|isEmpty
parameter_list|()
function_decl|;
comment|/**      * check if a Destination is Empty for this cursor      *       * @param destination      * @return true id the Destination is empty      */
name|boolean
name|isEmpty
parameter_list|(
name|Destination
name|destination
parameter_list|)
function_decl|;
comment|/**      * reset the cursor      */
name|void
name|reset
parameter_list|()
function_decl|;
comment|/**      * hint to the cursor to release any locks it might have grabbed after a      * reset      */
name|void
name|release
parameter_list|()
function_decl|;
comment|/**      * add message to await dispatch      *       * @param node      * @throws IOException      * @throws Exception      */
name|void
name|addMessageLast
parameter_list|(
name|MessageReference
name|node
parameter_list|)
throws|throws
name|Exception
function_decl|;
comment|/**      * add message to await dispatch      *       * @param node      * @throws Exception      */
name|void
name|addMessageFirst
parameter_list|(
name|MessageReference
name|node
parameter_list|)
throws|throws
name|Exception
function_decl|;
comment|/**      * Add a message recovered from a retroactive policy      *       * @param node      * @throws Exception      */
name|void
name|addRecoveredMessage
parameter_list|(
name|MessageReference
name|node
parameter_list|)
throws|throws
name|Exception
function_decl|;
comment|/**      * @return true if there pending messages to dispatch      */
name|boolean
name|hasNext
parameter_list|()
function_decl|;
comment|/**      * @return the next pending message      */
name|MessageReference
name|next
parameter_list|()
function_decl|;
comment|/**      * remove the message at the cursor position      */
name|void
name|remove
parameter_list|()
function_decl|;
comment|/**      * @return the number of pending messages      */
name|int
name|size
parameter_list|()
function_decl|;
comment|/**      * clear all pending messages      */
name|void
name|clear
parameter_list|()
function_decl|;
comment|/**      * Informs the Broker if the subscription needs to intervention to recover      * it's state e.g. DurableTopicSubscriber may do      *       * @return true if recovery required      */
name|boolean
name|isRecoveryRequired
parameter_list|()
function_decl|;
comment|/**      * @return the maximum batch size      */
name|int
name|getMaxBatchSize
parameter_list|()
function_decl|;
comment|/**      * Set the max batch size      *       * @param maxBatchSize      */
name|void
name|setMaxBatchSize
parameter_list|(
name|int
name|maxBatchSize
parameter_list|)
function_decl|;
comment|/**      * Give the cursor a hint that we are about to remove messages from memory      * only      */
name|void
name|resetForGC
parameter_list|()
function_decl|;
comment|/**      * remove a node      *       * @param node      */
name|void
name|remove
parameter_list|(
name|MessageReference
name|node
parameter_list|)
function_decl|;
comment|/**      * free up any internal buffers      */
name|void
name|gc
parameter_list|()
function_decl|;
comment|/**      * Set the UsageManager      *       * @param systemUsage      * @see org.apache.activemq.usage.SystemUsage      */
name|void
name|setSystemUsage
parameter_list|(
name|SystemUsage
name|systemUsage
parameter_list|)
function_decl|;
comment|/**      * @return the usageManager      */
name|SystemUsage
name|getSystemUsage
parameter_list|()
function_decl|;
comment|/**      * @return the memoryUsageHighWaterMark      */
name|int
name|getMemoryUsageHighWaterMark
parameter_list|()
function_decl|;
comment|/**      * @param memoryUsageHighWaterMark the memoryUsageHighWaterMark to set      */
name|void
name|setMemoryUsageHighWaterMark
parameter_list|(
name|int
name|memoryUsageHighWaterMark
parameter_list|)
function_decl|;
comment|/**      * @return true if the cursor is full      */
name|boolean
name|isFull
parameter_list|()
function_decl|;
comment|/**      * @return true if the cursor has buffered messages ready to deliver      */
name|boolean
name|hasMessagesBufferedToDeliver
parameter_list|()
function_decl|;
comment|/**      * destroy the cursor      *       * @throws Exception      */
name|void
name|destroy
parameter_list|()
throws|throws
name|Exception
function_decl|;
comment|/**      * Page in a restricted number of messages      *       * @param maxItems      * @return a list of paged in messages      */
name|LinkedList
name|pageInList
parameter_list|(
name|int
name|maxItems
parameter_list|)
function_decl|;
comment|/**      * set the maximum number of producers to track at one time      * @param value      */
name|void
name|setMaxProducersToAudit
parameter_list|(
name|int
name|value
parameter_list|)
function_decl|;
comment|/**      * @return the maximum number of producers to audit      */
name|int
name|getMaxProducersToAudit
parameter_list|()
function_decl|;
comment|/**      * Set the maximum depth of message ids to track      * @param depth       */
name|void
name|setMaxAuditDepth
parameter_list|(
name|int
name|depth
parameter_list|)
function_decl|;
comment|/**      * @return the audit depth      */
name|int
name|getMaxAuditDepth
parameter_list|()
function_decl|;
comment|/**      * @return the enableAudit      */
specifier|public
name|boolean
name|isEnableAudit
parameter_list|()
function_decl|;
comment|/**      * @param enableAudit the enableAudit to set      */
specifier|public
name|void
name|setEnableAudit
parameter_list|(
name|boolean
name|enableAudit
parameter_list|)
function_decl|;
comment|/**      * @return true if the underlying state of this cursor       * disappears when the broker shuts down      */
specifier|public
name|boolean
name|isTransient
parameter_list|()
function_decl|;
block|}
end_interface

end_unit

