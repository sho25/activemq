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
name|policy
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
name|DurableTopicSubscription
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
name|cursors
operator|.
name|PendingMessageCursor
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
name|cursors
operator|.
name|StoreDurableSubscriberCursor
import|;
end_import

begin_comment
comment|/**  * Creates a PendingMessageCursor that access the persistent store to retrieve  * messages  *   * @org.apache.xbean.XBean element="storeDurableSubscriberCursor"  *                         description="Pending messages for a durable  *                         subscriber are referenced from the Store"  *   */
end_comment

begin_class
specifier|public
class|class
name|StorePendingDurableSubscriberMessageStoragePolicy
implements|implements
name|PendingDurableSubscriberMessageStoragePolicy
block|{
name|boolean
name|immediatePriorityDispatch
init|=
literal|true
decl_stmt|;
name|boolean
name|useCache
init|=
literal|true
decl_stmt|;
specifier|public
name|boolean
name|isImmediatePriorityDispatch
parameter_list|()
block|{
return|return
name|immediatePriorityDispatch
return|;
block|}
comment|/**      * Ensure that new higher priority messages will get an immediate dispatch      * rather than wait for the end of the current cursor batch.      * Useful when there is a large message backlog and intermittent high priority messages.      *      * @param immediatePriorityDispatch      */
specifier|public
name|void
name|setImmediatePriorityDispatch
parameter_list|(
name|boolean
name|immediatePriorityDispatch
parameter_list|)
block|{
name|this
operator|.
name|immediatePriorityDispatch
operator|=
name|immediatePriorityDispatch
expr_stmt|;
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
comment|/**      * Retrieve the configured pending message storage cursor;      * @param broker       *       * @param clientId      * @param name      * @param maxBatchSize      * @param sub       * @return the Pending Message cursor      */
specifier|public
name|PendingMessageCursor
name|getSubscriberPendingMessageCursor
parameter_list|(
name|Broker
name|broker
parameter_list|,
name|String
name|clientId
parameter_list|,
name|String
name|name
parameter_list|,
name|int
name|maxBatchSize
parameter_list|,
name|DurableTopicSubscription
name|sub
parameter_list|)
block|{
name|StoreDurableSubscriberCursor
name|cursor
init|=
operator|new
name|StoreDurableSubscriberCursor
argument_list|(
name|broker
argument_list|,
name|clientId
argument_list|,
name|name
argument_list|,
name|maxBatchSize
argument_list|,
name|sub
argument_list|)
decl_stmt|;
name|cursor
operator|.
name|setUseCache
argument_list|(
name|isUseCache
argument_list|()
argument_list|)
expr_stmt|;
name|cursor
operator|.
name|setImmediatePriorityDispatch
argument_list|(
name|isImmediatePriorityDispatch
argument_list|()
argument_list|)
expr_stmt|;
return|return
name|cursor
return|;
block|}
block|}
end_class

end_unit

