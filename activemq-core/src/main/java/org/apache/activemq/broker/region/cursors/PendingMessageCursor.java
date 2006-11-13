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

begin_comment
comment|/**  * Interface to pending message (messages awaiting disptach to a consumer) cursor  *   * @version $Revision$  */
end_comment

begin_interface
specifier|public
interface|interface
name|PendingMessageCursor
extends|extends
name|Service
block|{
comment|/**      * Add a destination      * @param context      * @param destination      * @throws Exception      */
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
function_decl|;
comment|/**      * remove a destination      * @param context      * @param destination      * @throws Exception      */
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
function_decl|;
comment|/**      * @return true if there are no pending messages      */
specifier|public
name|boolean
name|isEmpty
parameter_list|()
function_decl|;
comment|/**      * reset the cursor      *      */
specifier|public
name|void
name|reset
parameter_list|()
function_decl|;
comment|/**      * add message to await dispatch      * @param node      * @throws IOException       * @throws Exception       */
specifier|public
name|void
name|addMessageLast
parameter_list|(
name|MessageReference
name|node
parameter_list|)
throws|throws
name|Exception
function_decl|;
comment|/**      * add message to await dispatch      * @param node      * @throws Exception       */
specifier|public
name|void
name|addMessageFirst
parameter_list|(
name|MessageReference
name|node
parameter_list|)
throws|throws
name|Exception
function_decl|;
comment|/**      * @return true if there pending messages to dispatch      */
specifier|public
name|boolean
name|hasNext
parameter_list|()
function_decl|;
comment|/**      * @return the next pending message      */
specifier|public
name|MessageReference
name|next
parameter_list|()
function_decl|;
comment|/**      * remove the message at the cursor position      *       */
specifier|public
name|void
name|remove
parameter_list|()
function_decl|;
comment|/**      * @return the number of pending messages      */
specifier|public
name|int
name|size
parameter_list|()
function_decl|;
comment|/**      * clear all pending messages      *       */
specifier|public
name|void
name|clear
parameter_list|()
function_decl|;
comment|/**      * Informs the Broker if the subscription needs to intervention to recover it's state      * e.g. DurableTopicSubscriber may do      * @return true if recovery required      */
specifier|public
name|boolean
name|isRecoveryRequired
parameter_list|()
function_decl|;
comment|/**      * @return the maximum batch size      */
specifier|public
name|int
name|getMaxBatchSize
parameter_list|()
function_decl|;
comment|/**      * Set the max batch size      * @param maxBatchSize      */
specifier|public
name|void
name|setMaxBatchSize
parameter_list|(
name|int
name|maxBatchSize
parameter_list|)
function_decl|;
comment|/**      * Give the cursor a hint that we are about to remove      * messages from memory only      */
specifier|public
name|void
name|resetForGC
parameter_list|()
function_decl|;
comment|/**      * remove a node      * @param node      */
specifier|public
name|void
name|remove
parameter_list|(
name|MessageReference
name|node
parameter_list|)
function_decl|;
block|}
end_interface

end_unit

