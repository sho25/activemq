begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  *  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  * http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
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
name|Iterator
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|jms
operator|.
name|InvalidSelectorException
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|jms
operator|.
name|JMSException
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
name|cursors
operator|.
name|FilePendingMessageCursor
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
name|ConsumerInfo
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
name|command
operator|.
name|MessageAck
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
name|MessageDispatch
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
name|util
operator|.
name|SubscriptionKey
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
name|ConcurrentHashMap
import|;
end_import

begin_class
specifier|public
class|class
name|DurableTopicSubscription
extends|extends
name|PrefetchSubscription
block|{
specifier|private
specifier|final
name|ConcurrentHashMap
name|redeliveredMessages
init|=
operator|new
name|ConcurrentHashMap
argument_list|()
decl_stmt|;
specifier|private
specifier|final
name|ConcurrentHashMap
name|destinations
init|=
operator|new
name|ConcurrentHashMap
argument_list|()
decl_stmt|;
specifier|private
specifier|final
name|SubscriptionKey
name|subscriptionKey
decl_stmt|;
specifier|private
specifier|final
name|boolean
name|keepDurableSubsActive
decl_stmt|;
specifier|private
name|boolean
name|active
init|=
literal|false
decl_stmt|;
specifier|public
name|DurableTopicSubscription
parameter_list|(
name|Broker
name|broker
parameter_list|,
name|ConnectionContext
name|context
parameter_list|,
name|ConsumerInfo
name|info
parameter_list|,
name|boolean
name|keepDurableSubsActive
parameter_list|,
name|PendingMessageCursor
name|cursor
parameter_list|)
throws|throws
name|InvalidSelectorException
block|{
name|super
argument_list|(
name|broker
argument_list|,
name|context
argument_list|,
name|info
argument_list|,
name|cursor
argument_list|)
expr_stmt|;
name|this
operator|.
name|keepDurableSubsActive
operator|=
name|keepDurableSubsActive
expr_stmt|;
name|subscriptionKey
operator|=
operator|new
name|SubscriptionKey
argument_list|(
name|context
operator|.
name|getClientId
argument_list|()
argument_list|,
name|info
operator|.
name|getSubscriptionName
argument_list|()
argument_list|)
expr_stmt|;
block|}
specifier|synchronized
specifier|public
name|boolean
name|isActive
parameter_list|()
block|{
return|return
name|active
return|;
block|}
specifier|protected
name|boolean
name|isFull
parameter_list|()
block|{
return|return
operator|!
name|active
operator|||
name|super
operator|.
name|isFull
argument_list|()
return|;
block|}
specifier|synchronized
specifier|public
name|void
name|gc
parameter_list|()
block|{     }
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
block|{
name|super
operator|.
name|add
argument_list|(
name|context
argument_list|,
name|destination
argument_list|)
expr_stmt|;
name|destinations
operator|.
name|put
argument_list|(
name|destination
operator|.
name|getActiveMQDestination
argument_list|()
argument_list|,
name|destination
argument_list|)
expr_stmt|;
if|if
condition|(
name|active
operator|||
name|keepDurableSubsActive
condition|)
block|{
name|Topic
name|topic
init|=
operator|(
name|Topic
operator|)
name|destination
decl_stmt|;
name|topic
operator|.
name|activate
argument_list|(
name|context
argument_list|,
name|this
argument_list|)
expr_stmt|;
block|}
name|dispatchMatched
argument_list|()
expr_stmt|;
block|}
specifier|public
name|void
name|activate
parameter_list|(
name|ConnectionContext
name|context
parameter_list|,
name|ConsumerInfo
name|info
parameter_list|)
throws|throws
name|Exception
block|{
if|if
condition|(
operator|!
name|active
condition|)
block|{
name|this
operator|.
name|active
operator|=
literal|true
expr_stmt|;
name|this
operator|.
name|context
operator|=
name|context
expr_stmt|;
name|this
operator|.
name|info
operator|=
name|info
expr_stmt|;
if|if
condition|(
operator|!
name|keepDurableSubsActive
condition|)
block|{
for|for
control|(
name|Iterator
name|iter
init|=
name|destinations
operator|.
name|values
argument_list|()
operator|.
name|iterator
argument_list|()
init|;
name|iter
operator|.
name|hasNext
argument_list|()
condition|;
control|)
block|{
name|Topic
name|topic
init|=
operator|(
name|Topic
operator|)
name|iter
operator|.
name|next
argument_list|()
decl_stmt|;
name|topic
operator|.
name|activate
argument_list|(
name|context
argument_list|,
name|this
argument_list|)
expr_stmt|;
block|}
block|}
synchronized|synchronized
init|(
name|pending
init|)
block|{
name|pending
operator|.
name|start
argument_list|()
expr_stmt|;
block|}
name|dispatchMatched
argument_list|()
expr_stmt|;
block|}
block|}
specifier|synchronized
specifier|public
name|void
name|deactivate
parameter_list|(
name|boolean
name|keepDurableSubsActive
parameter_list|)
throws|throws
name|Exception
block|{
name|active
operator|=
literal|false
expr_stmt|;
synchronized|synchronized
init|(
name|pending
init|)
block|{
name|pending
operator|.
name|stop
argument_list|()
expr_stmt|;
block|}
if|if
condition|(
operator|!
name|keepDurableSubsActive
condition|)
block|{
for|for
control|(
name|Iterator
name|iter
init|=
name|destinations
operator|.
name|values
argument_list|()
operator|.
name|iterator
argument_list|()
init|;
name|iter
operator|.
name|hasNext
argument_list|()
condition|;
control|)
block|{
name|Topic
name|topic
init|=
operator|(
name|Topic
operator|)
name|iter
operator|.
name|next
argument_list|()
decl_stmt|;
name|topic
operator|.
name|deactivate
argument_list|(
name|context
argument_list|,
name|this
argument_list|)
expr_stmt|;
block|}
block|}
synchronized|synchronized
init|(
name|dispatched
init|)
block|{
for|for
control|(
name|Iterator
name|iter
init|=
name|dispatched
operator|.
name|iterator
argument_list|()
init|;
name|iter
operator|.
name|hasNext
argument_list|()
condition|;
control|)
block|{
comment|// Mark the dispatched messages as redelivered for next time.
name|MessageReference
name|node
init|=
operator|(
name|MessageReference
operator|)
name|iter
operator|.
name|next
argument_list|()
decl_stmt|;
name|Integer
name|count
init|=
operator|(
name|Integer
operator|)
name|redeliveredMessages
operator|.
name|get
argument_list|(
name|node
operator|.
name|getMessageId
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
name|count
operator|!=
literal|null
condition|)
block|{
name|redeliveredMessages
operator|.
name|put
argument_list|(
name|node
operator|.
name|getMessageId
argument_list|()
argument_list|,
operator|new
name|Integer
argument_list|(
name|count
operator|.
name|intValue
argument_list|()
operator|+
literal|1
argument_list|)
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|redeliveredMessages
operator|.
name|put
argument_list|(
name|node
operator|.
name|getMessageId
argument_list|()
argument_list|,
operator|new
name|Integer
argument_list|(
literal|1
argument_list|)
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|keepDurableSubsActive
condition|)
block|{
synchronized|synchronized
init|(
name|pending
init|)
block|{
name|pending
operator|.
name|addMessageFirst
argument_list|(
name|node
argument_list|)
expr_stmt|;
block|}
block|}
else|else
block|{
name|node
operator|.
name|decrementReferenceCount
argument_list|()
expr_stmt|;
block|}
name|iter
operator|.
name|remove
argument_list|()
expr_stmt|;
block|}
block|}
if|if
condition|(
operator|!
name|keepDurableSubsActive
condition|)
block|{
synchronized|synchronized
init|(
name|pending
init|)
block|{
try|try
block|{
name|pending
operator|.
name|reset
argument_list|()
expr_stmt|;
while|while
condition|(
name|pending
operator|.
name|hasNext
argument_list|()
condition|)
block|{
name|MessageReference
name|node
init|=
name|pending
operator|.
name|next
argument_list|()
decl_stmt|;
name|node
operator|.
name|decrementReferenceCount
argument_list|()
expr_stmt|;
name|pending
operator|.
name|remove
argument_list|()
expr_stmt|;
block|}
block|}
finally|finally
block|{
name|pending
operator|.
name|release
argument_list|()
expr_stmt|;
block|}
block|}
block|}
name|prefetchExtension
operator|=
literal|0
expr_stmt|;
block|}
specifier|protected
name|MessageDispatch
name|createMessageDispatch
parameter_list|(
name|MessageReference
name|node
parameter_list|,
name|Message
name|message
parameter_list|)
block|{
name|MessageDispatch
name|md
init|=
name|super
operator|.
name|createMessageDispatch
argument_list|(
name|node
argument_list|,
name|message
argument_list|)
decl_stmt|;
name|Integer
name|count
init|=
operator|(
name|Integer
operator|)
name|redeliveredMessages
operator|.
name|get
argument_list|(
name|node
operator|.
name|getMessageId
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
name|count
operator|!=
literal|null
condition|)
block|{
name|md
operator|.
name|setRedeliveryCounter
argument_list|(
name|count
operator|.
name|intValue
argument_list|()
argument_list|)
expr_stmt|;
block|}
return|return
name|md
return|;
block|}
specifier|public
name|void
name|add
parameter_list|(
name|MessageReference
name|node
parameter_list|)
throws|throws
name|Exception
block|{
if|if
condition|(
operator|!
name|active
operator|&&
operator|!
name|keepDurableSubsActive
condition|)
block|{
return|return;
block|}
name|node
operator|.
name|incrementReferenceCount
argument_list|()
expr_stmt|;
name|super
operator|.
name|add
argument_list|(
name|node
argument_list|)
expr_stmt|;
block|}
specifier|protected
name|void
name|doAddRecoveredMessage
parameter_list|(
name|MessageReference
name|message
parameter_list|)
throws|throws
name|Exception
block|{
name|pending
operator|.
name|addRecoveredMessage
argument_list|(
name|message
argument_list|)
expr_stmt|;
block|}
specifier|public
name|int
name|getPendingQueueSize
parameter_list|()
block|{
if|if
condition|(
name|active
operator|||
name|keepDurableSubsActive
condition|)
block|{
return|return
name|super
operator|.
name|getPendingQueueSize
argument_list|()
return|;
block|}
comment|//TODO: need to get from store
return|return
literal|0
return|;
block|}
specifier|public
name|void
name|setSelector
parameter_list|(
name|String
name|selector
parameter_list|)
throws|throws
name|InvalidSelectorException
block|{
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|(
literal|"You cannot dynamically change the selector for durable topic subscriptions"
argument_list|)
throw|;
block|}
specifier|protected
name|boolean
name|canDispatch
parameter_list|(
name|MessageReference
name|node
parameter_list|)
block|{
return|return
name|active
return|;
block|}
specifier|protected
name|void
name|acknowledge
parameter_list|(
name|ConnectionContext
name|context
parameter_list|,
name|MessageAck
name|ack
parameter_list|,
name|MessageReference
name|node
parameter_list|)
throws|throws
name|IOException
block|{
name|node
operator|.
name|getRegionDestination
argument_list|()
operator|.
name|acknowledge
argument_list|(
name|context
argument_list|,
name|this
argument_list|,
name|ack
argument_list|,
name|node
argument_list|)
expr_stmt|;
name|redeliveredMessages
operator|.
name|remove
argument_list|(
name|node
operator|.
name|getMessageId
argument_list|()
argument_list|)
expr_stmt|;
name|node
operator|.
name|decrementReferenceCount
argument_list|()
expr_stmt|;
block|}
specifier|public
name|String
name|getSubscriptionName
parameter_list|()
block|{
return|return
name|subscriptionKey
operator|.
name|getSubscriptionName
argument_list|()
return|;
block|}
specifier|public
name|String
name|toString
parameter_list|()
block|{
return|return
literal|"DurableTopicSubscription:"
operator|+
literal|" consumer="
operator|+
name|info
operator|.
name|getConsumerId
argument_list|()
operator|+
literal|", destinations="
operator|+
name|destinations
operator|.
name|size
argument_list|()
operator|+
literal|", dispatched="
operator|+
name|dispatched
operator|.
name|size
argument_list|()
operator|+
literal|", delivered="
operator|+
name|this
operator|.
name|prefetchExtension
operator|+
literal|", pending="
operator|+
name|getPendingQueueSize
argument_list|()
return|;
block|}
specifier|public
name|String
name|getClientId
parameter_list|()
block|{
return|return
name|subscriptionKey
operator|.
name|getClientId
argument_list|()
return|;
block|}
specifier|public
name|SubscriptionKey
name|getSubscriptionKey
parameter_list|()
block|{
return|return
name|subscriptionKey
return|;
block|}
comment|/**      * Release any references that we are holding.      */
specifier|public
name|void
name|destroy
parameter_list|()
block|{
try|try
block|{
synchronized|synchronized
init|(
name|pending
init|)
block|{
name|pending
operator|.
name|reset
argument_list|()
expr_stmt|;
while|while
condition|(
name|pending
operator|.
name|hasNext
argument_list|()
condition|)
block|{
name|MessageReference
name|node
init|=
name|pending
operator|.
name|next
argument_list|()
decl_stmt|;
name|node
operator|.
name|decrementReferenceCount
argument_list|()
expr_stmt|;
block|}
block|}
block|}
finally|finally
block|{
name|pending
operator|.
name|release
argument_list|()
expr_stmt|;
name|pending
operator|.
name|clear
argument_list|()
expr_stmt|;
block|}
for|for
control|(
name|Iterator
name|iter
init|=
name|dispatched
operator|.
name|iterator
argument_list|()
init|;
name|iter
operator|.
name|hasNext
argument_list|()
condition|;
control|)
block|{
name|MessageReference
name|node
init|=
operator|(
name|MessageReference
operator|)
name|iter
operator|.
name|next
argument_list|()
decl_stmt|;
name|node
operator|.
name|decrementReferenceCount
argument_list|()
expr_stmt|;
block|}
name|dispatched
operator|.
name|clear
argument_list|()
expr_stmt|;
block|}
block|}
end_class

end_unit

