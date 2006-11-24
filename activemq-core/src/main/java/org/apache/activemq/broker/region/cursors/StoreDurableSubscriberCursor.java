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
name|HashMap
import|;
end_import

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
name|Map
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
name|activemq
operator|.
name|memory
operator|.
name|UsageManager
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
comment|/**  * perist pending messages pending message (messages awaiting disptach to a consumer) cursor  *   * @version $Revision$  */
end_comment

begin_class
specifier|public
class|class
name|StoreDurableSubscriberCursor
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
name|StoreDurableSubscriberCursor
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
name|String
name|clientId
decl_stmt|;
specifier|private
name|String
name|subscriberName
decl_stmt|;
specifier|private
name|Map
name|topics
init|=
operator|new
name|HashMap
argument_list|()
decl_stmt|;
specifier|private
name|LinkedList
name|storePrefetches
init|=
operator|new
name|LinkedList
argument_list|()
decl_stmt|;
specifier|private
name|boolean
name|started
decl_stmt|;
specifier|private
name|PendingMessageCursor
name|nonPersistent
decl_stmt|;
specifier|private
name|PendingMessageCursor
name|currentCursor
decl_stmt|;
comment|/**      * @param topic      * @param clientId      * @param subscriberName      * @throws IOException      */
specifier|public
name|StoreDurableSubscriberCursor
parameter_list|(
name|String
name|clientId
parameter_list|,
name|String
name|subscriberName
parameter_list|,
name|Store
name|store
parameter_list|,
name|int
name|maxBatchSize
parameter_list|)
block|{
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
name|this
operator|.
name|nonPersistent
operator|=
operator|new
name|FilePendingMessageCursor
argument_list|(
name|clientId
operator|+
name|subscriberName
argument_list|,
name|store
argument_list|)
expr_stmt|;
name|storePrefetches
operator|.
name|add
argument_list|(
name|nonPersistent
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
for|for
control|(
name|Iterator
name|i
init|=
name|storePrefetches
operator|.
name|iterator
argument_list|()
init|;
name|i
operator|.
name|hasNext
argument_list|()
condition|;
control|)
block|{
name|PendingMessageCursor
name|tsp
init|=
operator|(
name|PendingMessageCursor
operator|)
name|i
operator|.
name|next
argument_list|()
decl_stmt|;
name|tsp
operator|.
name|start
argument_list|()
expr_stmt|;
name|pendingCount
operator|+=
name|tsp
operator|.
name|size
argument_list|()
expr_stmt|;
block|}
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
for|for
control|(
name|Iterator
name|i
init|=
name|storePrefetches
operator|.
name|iterator
argument_list|()
init|;
name|i
operator|.
name|hasNext
argument_list|()
condition|;
control|)
block|{
name|PendingMessageCursor
name|tsp
init|=
operator|(
name|PendingMessageCursor
operator|)
name|i
operator|.
name|next
argument_list|()
decl_stmt|;
name|tsp
operator|.
name|stop
argument_list|()
expr_stmt|;
block|}
name|pendingCount
operator|=
literal|0
expr_stmt|;
block|}
comment|/**      * Add a destination      *       * @param context      * @param destination      * @throws Exception      */
specifier|public
specifier|synchronized
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
block|{
name|TopicStorePrefetch
name|tsp
init|=
operator|new
name|TopicStorePrefetch
argument_list|(
operator|(
name|Topic
operator|)
name|destination
argument_list|,
name|clientId
argument_list|,
name|subscriberName
argument_list|)
decl_stmt|;
name|tsp
operator|.
name|setMaxBatchSize
argument_list|(
name|getMaxBatchSize
argument_list|()
argument_list|)
expr_stmt|;
name|tsp
operator|.
name|setUsageManager
argument_list|(
name|usageManager
argument_list|)
expr_stmt|;
name|topics
operator|.
name|put
argument_list|(
name|destination
argument_list|,
name|tsp
argument_list|)
expr_stmt|;
name|storePrefetches
operator|.
name|add
argument_list|(
name|tsp
argument_list|)
expr_stmt|;
if|if
condition|(
name|started
condition|)
block|{
name|tsp
operator|.
name|start
argument_list|()
expr_stmt|;
name|pendingCount
operator|+=
name|tsp
operator|.
name|size
argument_list|()
expr_stmt|;
block|}
block|}
comment|/**      * remove a destination      *       * @param context      * @param destination      * @throws Exception      */
specifier|public
specifier|synchronized
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
block|{
name|Object
name|tsp
init|=
name|topics
operator|.
name|remove
argument_list|(
name|destination
argument_list|)
decl_stmt|;
if|if
condition|(
name|tsp
operator|!=
literal|null
condition|)
block|{
name|storePrefetches
operator|.
name|remove
argument_list|(
name|tsp
argument_list|)
expr_stmt|;
block|}
block|}
comment|/**      * @return true if there are no pending messages      */
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
name|Destination
name|dest
init|=
name|msg
operator|.
name|getRegionDestination
argument_list|()
decl_stmt|;
name|TopicStorePrefetch
name|tsp
init|=
operator|(
name|TopicStorePrefetch
operator|)
name|topics
operator|.
name|get
argument_list|(
name|dest
argument_list|)
decl_stmt|;
if|if
condition|(
name|tsp
operator|!=
literal|null
condition|)
block|{
name|tsp
operator|.
name|addMessageLast
argument_list|(
name|node
argument_list|)
expr_stmt|;
block|}
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
for|for
control|(
name|Iterator
name|i
init|=
name|storePrefetches
operator|.
name|iterator
argument_list|()
init|;
name|i
operator|.
name|hasNext
argument_list|()
condition|;
control|)
block|{
name|AbstractPendingMessageCursor
name|tsp
init|=
operator|(
name|AbstractPendingMessageCursor
operator|)
name|i
operator|.
name|next
argument_list|()
decl_stmt|;
name|tsp
operator|.
name|reset
argument_list|()
expr_stmt|;
block|}
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
name|void
name|setMaxBatchSize
parameter_list|(
name|int
name|maxBatchSize
parameter_list|)
block|{
for|for
control|(
name|Iterator
name|i
init|=
name|storePrefetches
operator|.
name|iterator
argument_list|()
init|;
name|i
operator|.
name|hasNext
argument_list|()
condition|;
control|)
block|{
name|AbstractPendingMessageCursor
name|tsp
init|=
operator|(
name|AbstractPendingMessageCursor
operator|)
name|i
operator|.
name|next
argument_list|()
decl_stmt|;
name|tsp
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
specifier|synchronized
name|void
name|gc
parameter_list|()
block|{
for|for
control|(
name|Iterator
name|i
init|=
name|storePrefetches
operator|.
name|iterator
argument_list|()
init|;
name|i
operator|.
name|hasNext
argument_list|()
condition|;
control|)
block|{
name|PendingMessageCursor
name|tsp
init|=
operator|(
name|PendingMessageCursor
operator|)
name|i
operator|.
name|next
argument_list|()
decl_stmt|;
name|tsp
operator|.
name|gc
argument_list|()
expr_stmt|;
block|}
block|}
specifier|public
specifier|synchronized
name|void
name|setUsageManager
parameter_list|(
name|UsageManager
name|usageManager
parameter_list|)
block|{
name|super
operator|.
name|setUsageManager
argument_list|(
name|usageManager
argument_list|)
expr_stmt|;
for|for
control|(
name|Iterator
name|i
init|=
name|storePrefetches
operator|.
name|iterator
argument_list|()
init|;
name|i
operator|.
name|hasNext
argument_list|()
condition|;
control|)
block|{
name|PendingMessageCursor
name|tsp
init|=
operator|(
name|PendingMessageCursor
operator|)
name|i
operator|.
name|next
argument_list|()
decl_stmt|;
name|tsp
operator|.
name|setUsageManager
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
name|currentCursor
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
name|currentCursor
operator|=
literal|null
expr_stmt|;
for|for
control|(
name|Iterator
name|i
init|=
name|storePrefetches
operator|.
name|iterator
argument_list|()
init|;
name|i
operator|.
name|hasNext
argument_list|()
condition|;
control|)
block|{
name|AbstractPendingMessageCursor
name|tsp
init|=
operator|(
name|AbstractPendingMessageCursor
operator|)
name|i
operator|.
name|next
argument_list|()
decl_stmt|;
if|if
condition|(
name|tsp
operator|.
name|hasNext
argument_list|()
condition|)
block|{
name|currentCursor
operator|=
name|tsp
expr_stmt|;
break|break;
block|}
block|}
comment|// round-robin
name|storePrefetches
operator|.
name|addLast
argument_list|(
name|storePrefetches
operator|.
name|removeFirst
argument_list|()
argument_list|)
expr_stmt|;
block|}
return|return
name|currentCursor
return|;
block|}
block|}
end_class

end_unit

