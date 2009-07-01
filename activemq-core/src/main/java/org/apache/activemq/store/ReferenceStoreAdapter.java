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
name|Map
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
name|org
operator|.
name|apache
operator|.
name|activemq
operator|.
name|command
operator|.
name|ActiveMQQueue
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
name|ActiveMQTopic
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
name|SubscriptionInfo
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
name|TransactionId
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
name|amq
operator|.
name|AMQTx
import|;
end_import

begin_comment
comment|/**  * Adapter to the actual persistence mechanism used with ActiveMQ  *   * @version $Revision: 1.3 $  */
end_comment

begin_interface
specifier|public
interface|interface
name|ReferenceStoreAdapter
extends|extends
name|PersistenceAdapter
block|{
comment|/**      * Factory method to create a new queue message store with the given      * destination name      *       * @param destination      * @return the QueueReferenceStore      * @throws IOException      */
name|ReferenceStore
name|createQueueReferenceStore
parameter_list|(
name|ActiveMQQueue
name|destination
parameter_list|)
throws|throws
name|IOException
function_decl|;
comment|/**      * Factory method to create a new topic message store with the given      * destination name      *       * @param destination      * @return the TopicRefererenceStore      * @throws IOException      */
name|TopicReferenceStore
name|createTopicReferenceStore
parameter_list|(
name|ActiveMQTopic
name|destination
parameter_list|)
throws|throws
name|IOException
function_decl|;
comment|/**      * @return Set of File ids in use      * @throws IOException      */
name|Set
argument_list|<
name|Integer
argument_list|>
name|getReferenceFileIdsInUse
parameter_list|()
throws|throws
name|IOException
function_decl|;
comment|/**      * If the store isn't valid, it can be recoverd at start-up      *       * @return true if the reference store is in a consistent state      */
name|boolean
name|isStoreValid
parameter_list|()
function_decl|;
comment|/**      * called by recover to clear out message references      *       * @throws IOException      */
name|void
name|clearMessages
parameter_list|()
throws|throws
name|IOException
function_decl|;
comment|/**      * recover any state      *       * @throws IOException      */
name|void
name|recoverState
parameter_list|()
throws|throws
name|IOException
function_decl|;
comment|/**      * Save prepared transactions      *       * @param map      * @throws IOException      */
name|void
name|savePreparedState
parameter_list|(
name|Map
argument_list|<
name|TransactionId
argument_list|,
name|AMQTx
argument_list|>
name|map
parameter_list|)
throws|throws
name|IOException
function_decl|;
comment|/**      * @return saved prepared transactions      * @throws IOException      */
name|Map
argument_list|<
name|TransactionId
argument_list|,
name|AMQTx
argument_list|>
name|retrievePreparedState
parameter_list|()
throws|throws
name|IOException
function_decl|;
comment|/**      * @return the maxDataFileLength      */
name|long
name|getMaxDataFileLength
parameter_list|()
function_decl|;
comment|/**      * set the max data length of a reference data log - if used      * @param maxDataFileLength      */
name|void
name|setMaxDataFileLength
parameter_list|(
name|long
name|maxDataFileLength
parameter_list|)
function_decl|;
comment|/**      * Recover particular subscription. Used for recovery of durable consumers      * @param info      * @throws IOException      */
name|void
name|recoverSubscription
parameter_list|(
name|SubscriptionInfo
name|info
parameter_list|)
throws|throws
name|IOException
function_decl|;
block|}
end_interface

end_unit

