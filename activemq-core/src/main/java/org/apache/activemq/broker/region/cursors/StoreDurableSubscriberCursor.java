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
name|Collections
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
name|List
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
name|concurrent
operator|.
name|CopyOnWriteArrayList
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
name|advisory
operator|.
name|AdvisorySupport
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
name|Subscription
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
name|usage
operator|.
name|SystemUsage
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
comment|/**  * persist pending messages pending message (messages awaiting dispatch to a  * consumer) cursor  *   * @version $Revision$  */
end_comment

begin_class
specifier|public
class|class
name|StoreDurableSubscriberCursor
extends|extends
name|AbstractPendingMessageCursor
block|{
specifier|private
specifier|static
specifier|final
name|Log
name|LOG
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
name|String
name|clientId
decl_stmt|;
specifier|private
name|String
name|subscriberName
decl_stmt|;
specifier|private
name|Map
argument_list|<
name|Destination
argument_list|,
name|TopicStorePrefetch
argument_list|>
name|topics
init|=
operator|new
name|HashMap
argument_list|<
name|Destination
argument_list|,
name|TopicStorePrefetch
argument_list|>
argument_list|()
decl_stmt|;
specifier|private
name|List
argument_list|<
name|PendingMessageCursor
argument_list|>
name|storePrefetches
init|=
operator|new
name|CopyOnWriteArrayList
argument_list|<
name|PendingMessageCursor
argument_list|>
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
specifier|private
name|Subscription
name|subscription
decl_stmt|;
comment|/**      * @param broker       * @param topic      * @param clientId      * @param subscriberName      * @param maxBatchSize       * @param subscription       * @throws IOException      */
specifier|public
name|StoreDurableSubscriberCursor
parameter_list|(
name|Broker
name|broker
parameter_list|,
name|String
name|clientId
parameter_list|,
name|String
name|subscriberName
parameter_list|,
name|int
name|maxBatchSize
parameter_list|,
name|Subscription
name|subscription
parameter_list|)
block|{
name|this
operator|.
name|subscription
operator|=
name|subscription
expr_stmt|;
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
name|this
operator|.
name|nonPersistent
operator|=
operator|new
name|FilePendingMessageCursor
argument_list|(
name|broker
argument_list|,
name|clientId
operator|+
name|subscriberName
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|this
operator|.
name|nonPersistent
operator|=
operator|new
name|VMPendingMessageCursor
argument_list|()
expr_stmt|;
block|}
name|this
operator|.
name|nonPersistent
operator|.
name|setMaxBatchSize
argument_list|(
name|getMaxBatchSize
argument_list|()
argument_list|)
expr_stmt|;
name|this
operator|.
name|nonPersistent
operator|.
name|setSystemUsage
argument_list|(
name|systemUsage
argument_list|)
expr_stmt|;
name|this
operator|.
name|nonPersistent
operator|.
name|setEnableAudit
argument_list|(
name|isEnableAudit
argument_list|()
argument_list|)
expr_stmt|;
name|this
operator|.
name|nonPersistent
operator|.
name|setMaxAuditDepth
argument_list|(
name|getMaxAuditDepth
argument_list|()
argument_list|)
expr_stmt|;
name|this
operator|.
name|nonPersistent
operator|.
name|setMaxProducersToAudit
argument_list|(
name|getMaxProducersToAudit
argument_list|()
argument_list|)
expr_stmt|;
name|this
operator|.
name|storePrefetches
operator|.
name|add
argument_list|(
name|this
operator|.
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
if|if
condition|(
operator|!
name|started
condition|)
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
for|for
control|(
name|PendingMessageCursor
name|tsp
range|:
name|storePrefetches
control|)
block|{
name|tsp
operator|.
name|setMessageAudit
argument_list|(
name|getMessageAudit
argument_list|()
argument_list|)
expr_stmt|;
name|tsp
operator|.
name|start
argument_list|()
expr_stmt|;
block|}
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
if|if
condition|(
name|started
condition|)
block|{
name|started
operator|=
literal|false
expr_stmt|;
name|super
operator|.
name|stop
argument_list|()
expr_stmt|;
for|for
control|(
name|PendingMessageCursor
name|tsp
range|:
name|storePrefetches
control|)
block|{
name|tsp
operator|.
name|stop
argument_list|()
expr_stmt|;
block|}
block|}
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
if|if
condition|(
name|destination
operator|!=
literal|null
operator|&&
operator|!
name|AdvisorySupport
operator|.
name|isAdvisoryTopic
argument_list|(
name|destination
operator|.
name|getActiveMQDestination
argument_list|()
argument_list|)
condition|)
block|{
name|TopicStorePrefetch
name|tsp
init|=
operator|new
name|TopicStorePrefetch
argument_list|(
name|this
operator|.
name|subscription
argument_list|,
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
name|setSystemUsage
argument_list|(
name|systemUsage
argument_list|)
expr_stmt|;
name|tsp
operator|.
name|setEnableAudit
argument_list|(
name|isEnableAudit
argument_list|()
argument_list|)
expr_stmt|;
name|tsp
operator|.
name|setMaxAuditDepth
argument_list|(
name|getMaxAuditDepth
argument_list|()
argument_list|)
expr_stmt|;
name|tsp
operator|.
name|setMaxProducersToAudit
argument_list|(
name|getMaxProducersToAudit
argument_list|()
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
block|}
block|}
block|}
comment|/**      * remove a destination      *       * @param context      * @param destination      * @throws Exception      */
specifier|public
specifier|synchronized
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
return|return
name|Collections
operator|.
name|EMPTY_LIST
return|;
block|}
comment|/**      * @return true if there are no pending messages      */
specifier|public
specifier|synchronized
name|boolean
name|isEmpty
parameter_list|()
block|{
for|for
control|(
name|PendingMessageCursor
name|tsp
range|:
name|storePrefetches
control|)
block|{
if|if
condition|(
operator|!
name|tsp
operator|.
name|isEmpty
argument_list|()
condition|)
return|return
literal|false
return|;
block|}
return|return
literal|true
return|;
block|}
specifier|public
specifier|synchronized
name|boolean
name|isEmpty
parameter_list|(
name|Destination
name|destination
parameter_list|)
block|{
name|boolean
name|result
init|=
literal|true
decl_stmt|;
name|TopicStorePrefetch
name|tsp
init|=
name|topics
operator|.
name|get
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
name|result
operator|=
name|tsp
operator|.
name|isEmpty
argument_list|()
expr_stmt|;
block|}
return|return
name|result
return|;
block|}
comment|/**      * Informs the Broker if the subscription needs to intervention to recover      * it's state e.g. DurableTopicSubscriber may do      *       * @see org.apache.activemq.region.cursors.PendingMessageCursor      * @return true if recovery required      */
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
specifier|synchronized
name|void
name|addRecoveredMessage
parameter_list|(
name|MessageReference
name|node
parameter_list|)
throws|throws
name|Exception
block|{
name|nonPersistent
operator|.
name|addMessageLast
argument_list|(
name|node
argument_list|)
expr_stmt|;
block|}
specifier|public
specifier|synchronized
name|void
name|clear
parameter_list|()
block|{
for|for
control|(
name|PendingMessageCursor
name|tsp
range|:
name|storePrefetches
control|)
block|{
name|tsp
operator|.
name|clear
argument_list|()
expr_stmt|;
block|}
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
literal|true
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
block|}
specifier|public
specifier|synchronized
name|void
name|reset
parameter_list|()
block|{
for|for
control|(
name|Iterator
argument_list|<
name|PendingMessageCursor
argument_list|>
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
specifier|synchronized
name|void
name|release
parameter_list|()
block|{
for|for
control|(
name|Iterator
argument_list|<
name|PendingMessageCursor
argument_list|>
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
name|release
argument_list|()
expr_stmt|;
block|}
block|}
specifier|public
specifier|synchronized
name|int
name|size
parameter_list|()
block|{
name|int
name|pendingCount
init|=
literal|0
decl_stmt|;
for|for
control|(
name|PendingMessageCursor
name|tsp
range|:
name|storePrefetches
control|)
block|{
name|pendingCount
operator|+=
name|tsp
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
argument_list|<
name|PendingMessageCursor
argument_list|>
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
argument_list|<
name|PendingMessageCursor
argument_list|>
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
for|for
control|(
name|Iterator
argument_list|<
name|PendingMessageCursor
argument_list|>
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
name|i
operator|.
name|next
argument_list|()
decl_stmt|;
name|tsp
operator|.
name|setSystemUsage
argument_list|(
name|usageManager
argument_list|)
expr_stmt|;
block|}
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
for|for
control|(
name|PendingMessageCursor
name|cursor
range|:
name|storePrefetches
control|)
block|{
name|cursor
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
for|for
control|(
name|PendingMessageCursor
name|cursor
range|:
name|storePrefetches
control|)
block|{
name|cursor
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
for|for
control|(
name|PendingMessageCursor
name|cursor
range|:
name|storePrefetches
control|)
block|{
name|cursor
operator|.
name|setEnableAudit
argument_list|(
name|enableAudit
argument_list|)
expr_stmt|;
block|}
block|}
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
for|for
control|(
name|PendingMessageCursor
name|cursor
range|:
name|storePrefetches
control|)
block|{
name|cursor
operator|.
name|setUseCache
argument_list|(
name|useCache
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
argument_list|<
name|PendingMessageCursor
argument_list|>
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
if|if
condition|(
name|storePrefetches
operator|.
name|size
argument_list|()
operator|>
literal|1
condition|)
block|{
name|PendingMessageCursor
name|first
init|=
name|storePrefetches
operator|.
name|remove
argument_list|(
literal|0
argument_list|)
decl_stmt|;
name|storePrefetches
operator|.
name|add
argument_list|(
name|first
argument_list|)
expr_stmt|;
block|}
block|}
return|return
name|currentCursor
return|;
block|}
specifier|public
name|String
name|toString
parameter_list|()
block|{
return|return
literal|"StoreDurableSubscriber("
operator|+
name|clientId
operator|+
literal|":"
operator|+
name|subscriberName
operator|+
literal|")"
return|;
block|}
block|}
end_class

end_unit

