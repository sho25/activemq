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
operator|.
name|memory
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
name|Map
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Map
operator|.
name|Entry
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
name|command
operator|.
name|ActiveMQDestination
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
name|store
operator|.
name|MessageRecoveryListener
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
name|TopicMessageStore
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
name|LRUCache
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

begin_comment
comment|/**  *   */
end_comment

begin_class
specifier|public
class|class
name|MemoryTopicMessageStore
extends|extends
name|MemoryMessageStore
implements|implements
name|TopicMessageStore
block|{
specifier|private
name|Map
argument_list|<
name|SubscriptionKey
argument_list|,
name|SubscriptionInfo
argument_list|>
name|subscriberDatabase
decl_stmt|;
specifier|private
name|Map
argument_list|<
name|SubscriptionKey
argument_list|,
name|MemoryTopicSub
argument_list|>
name|topicSubMap
decl_stmt|;
specifier|public
name|MemoryTopicMessageStore
parameter_list|(
name|ActiveMQDestination
name|destination
parameter_list|)
block|{
name|this
argument_list|(
name|destination
argument_list|,
operator|new
name|LRUCache
argument_list|<
name|MessageId
argument_list|,
name|Message
argument_list|>
argument_list|(
literal|100
argument_list|,
literal|100
argument_list|,
literal|0.75f
argument_list|,
literal|false
argument_list|)
argument_list|,
name|makeSubscriptionInfoMap
argument_list|()
argument_list|)
expr_stmt|;
block|}
specifier|public
name|MemoryTopicMessageStore
parameter_list|(
name|ActiveMQDestination
name|destination
parameter_list|,
name|Map
argument_list|<
name|MessageId
argument_list|,
name|Message
argument_list|>
name|messageTable
parameter_list|,
name|Map
argument_list|<
name|SubscriptionKey
argument_list|,
name|SubscriptionInfo
argument_list|>
name|subscriberDatabase
parameter_list|)
block|{
name|super
argument_list|(
name|destination
argument_list|,
name|messageTable
argument_list|)
expr_stmt|;
name|this
operator|.
name|subscriberDatabase
operator|=
name|subscriberDatabase
expr_stmt|;
name|this
operator|.
name|topicSubMap
operator|=
name|makeSubMap
argument_list|()
expr_stmt|;
block|}
specifier|protected
specifier|static
name|Map
argument_list|<
name|SubscriptionKey
argument_list|,
name|SubscriptionInfo
argument_list|>
name|makeSubscriptionInfoMap
parameter_list|()
block|{
return|return
name|Collections
operator|.
name|synchronizedMap
argument_list|(
operator|new
name|HashMap
argument_list|<
name|SubscriptionKey
argument_list|,
name|SubscriptionInfo
argument_list|>
argument_list|()
argument_list|)
return|;
block|}
specifier|protected
specifier|static
name|Map
argument_list|<
name|SubscriptionKey
argument_list|,
name|MemoryTopicSub
argument_list|>
name|makeSubMap
parameter_list|()
block|{
return|return
name|Collections
operator|.
name|synchronizedMap
argument_list|(
operator|new
name|HashMap
argument_list|<
name|SubscriptionKey
argument_list|,
name|MemoryTopicSub
argument_list|>
argument_list|()
argument_list|)
return|;
block|}
specifier|public
specifier|synchronized
name|void
name|addMessage
parameter_list|(
name|ConnectionContext
name|context
parameter_list|,
name|Message
name|message
parameter_list|)
throws|throws
name|IOException
block|{
name|super
operator|.
name|addMessage
argument_list|(
name|context
argument_list|,
name|message
argument_list|)
expr_stmt|;
for|for
control|(
name|Iterator
argument_list|<
name|MemoryTopicSub
argument_list|>
name|i
init|=
name|topicSubMap
operator|.
name|values
argument_list|()
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
name|MemoryTopicSub
name|sub
init|=
name|i
operator|.
name|next
argument_list|()
decl_stmt|;
name|sub
operator|.
name|addMessage
argument_list|(
name|message
operator|.
name|getMessageId
argument_list|()
argument_list|,
name|message
argument_list|)
expr_stmt|;
block|}
block|}
specifier|public
specifier|synchronized
name|void
name|acknowledge
parameter_list|(
name|ConnectionContext
name|context
parameter_list|,
name|String
name|clientId
parameter_list|,
name|String
name|subscriptionName
parameter_list|,
name|MessageId
name|messageId
parameter_list|,
name|MessageAck
name|ack
parameter_list|)
throws|throws
name|IOException
block|{
name|SubscriptionKey
name|key
init|=
operator|new
name|SubscriptionKey
argument_list|(
name|clientId
argument_list|,
name|subscriptionName
argument_list|)
decl_stmt|;
name|MemoryTopicSub
name|sub
init|=
name|topicSubMap
operator|.
name|get
argument_list|(
name|key
argument_list|)
decl_stmt|;
if|if
condition|(
name|sub
operator|!=
literal|null
condition|)
block|{
name|sub
operator|.
name|removeMessage
argument_list|(
name|messageId
argument_list|)
expr_stmt|;
block|}
block|}
specifier|public
specifier|synchronized
name|SubscriptionInfo
name|lookupSubscription
parameter_list|(
name|String
name|clientId
parameter_list|,
name|String
name|subscriptionName
parameter_list|)
throws|throws
name|IOException
block|{
return|return
name|subscriberDatabase
operator|.
name|get
argument_list|(
operator|new
name|SubscriptionKey
argument_list|(
name|clientId
argument_list|,
name|subscriptionName
argument_list|)
argument_list|)
return|;
block|}
specifier|public
specifier|synchronized
name|void
name|addSubsciption
parameter_list|(
name|SubscriptionInfo
name|info
parameter_list|,
name|boolean
name|retroactive
parameter_list|)
throws|throws
name|IOException
block|{
name|SubscriptionKey
name|key
init|=
operator|new
name|SubscriptionKey
argument_list|(
name|info
argument_list|)
decl_stmt|;
name|MemoryTopicSub
name|sub
init|=
operator|new
name|MemoryTopicSub
argument_list|()
decl_stmt|;
name|topicSubMap
operator|.
name|put
argument_list|(
name|key
argument_list|,
name|sub
argument_list|)
expr_stmt|;
if|if
condition|(
name|retroactive
condition|)
block|{
for|for
control|(
name|Iterator
name|i
init|=
name|messageTable
operator|.
name|entrySet
argument_list|()
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
name|Map
operator|.
name|Entry
name|entry
init|=
operator|(
name|Entry
operator|)
name|i
operator|.
name|next
argument_list|()
decl_stmt|;
name|sub
operator|.
name|addMessage
argument_list|(
operator|(
name|MessageId
operator|)
name|entry
operator|.
name|getKey
argument_list|()
argument_list|,
operator|(
name|Message
operator|)
name|entry
operator|.
name|getValue
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
name|subscriberDatabase
operator|.
name|put
argument_list|(
name|key
argument_list|,
name|info
argument_list|)
expr_stmt|;
block|}
specifier|public
specifier|synchronized
name|void
name|deleteSubscription
parameter_list|(
name|String
name|clientId
parameter_list|,
name|String
name|subscriptionName
parameter_list|)
block|{
name|org
operator|.
name|apache
operator|.
name|activemq
operator|.
name|util
operator|.
name|SubscriptionKey
name|key
init|=
operator|new
name|SubscriptionKey
argument_list|(
name|clientId
argument_list|,
name|subscriptionName
argument_list|)
decl_stmt|;
name|subscriberDatabase
operator|.
name|remove
argument_list|(
name|key
argument_list|)
expr_stmt|;
name|topicSubMap
operator|.
name|remove
argument_list|(
name|key
argument_list|)
expr_stmt|;
block|}
specifier|public
specifier|synchronized
name|void
name|recoverSubscription
parameter_list|(
name|String
name|clientId
parameter_list|,
name|String
name|subscriptionName
parameter_list|,
name|MessageRecoveryListener
name|listener
parameter_list|)
throws|throws
name|Exception
block|{
name|MemoryTopicSub
name|sub
init|=
name|topicSubMap
operator|.
name|get
argument_list|(
operator|new
name|SubscriptionKey
argument_list|(
name|clientId
argument_list|,
name|subscriptionName
argument_list|)
argument_list|)
decl_stmt|;
if|if
condition|(
name|sub
operator|!=
literal|null
condition|)
block|{
name|sub
operator|.
name|recoverSubscription
argument_list|(
name|listener
argument_list|)
expr_stmt|;
block|}
block|}
specifier|public
specifier|synchronized
name|void
name|delete
parameter_list|()
block|{
name|super
operator|.
name|delete
argument_list|()
expr_stmt|;
name|subscriberDatabase
operator|.
name|clear
argument_list|()
expr_stmt|;
name|topicSubMap
operator|.
name|clear
argument_list|()
expr_stmt|;
block|}
specifier|public
name|SubscriptionInfo
index|[]
name|getAllSubscriptions
parameter_list|()
throws|throws
name|IOException
block|{
return|return
name|subscriberDatabase
operator|.
name|values
argument_list|()
operator|.
name|toArray
argument_list|(
operator|new
name|SubscriptionInfo
index|[
name|subscriberDatabase
operator|.
name|size
argument_list|()
index|]
argument_list|)
return|;
block|}
specifier|public
specifier|synchronized
name|int
name|getMessageCount
parameter_list|(
name|String
name|clientId
parameter_list|,
name|String
name|subscriberName
parameter_list|)
throws|throws
name|IOException
block|{
name|int
name|result
init|=
literal|0
decl_stmt|;
name|MemoryTopicSub
name|sub
init|=
name|topicSubMap
operator|.
name|get
argument_list|(
operator|new
name|SubscriptionKey
argument_list|(
name|clientId
argument_list|,
name|subscriberName
argument_list|)
argument_list|)
decl_stmt|;
if|if
condition|(
name|sub
operator|!=
literal|null
condition|)
block|{
name|result
operator|=
name|sub
operator|.
name|size
argument_list|()
expr_stmt|;
block|}
return|return
name|result
return|;
block|}
specifier|public
specifier|synchronized
name|void
name|recoverNextMessages
parameter_list|(
name|String
name|clientId
parameter_list|,
name|String
name|subscriptionName
parameter_list|,
name|int
name|maxReturned
parameter_list|,
name|MessageRecoveryListener
name|listener
parameter_list|)
throws|throws
name|Exception
block|{
name|MemoryTopicSub
name|sub
init|=
name|this
operator|.
name|topicSubMap
operator|.
name|get
argument_list|(
operator|new
name|SubscriptionKey
argument_list|(
name|clientId
argument_list|,
name|subscriptionName
argument_list|)
argument_list|)
decl_stmt|;
if|if
condition|(
name|sub
operator|!=
literal|null
condition|)
block|{
name|sub
operator|.
name|recoverNextMessages
argument_list|(
name|maxReturned
argument_list|,
name|listener
argument_list|)
expr_stmt|;
block|}
block|}
specifier|public
name|void
name|resetBatching
parameter_list|(
name|String
name|clientId
parameter_list|,
name|String
name|subscriptionName
parameter_list|)
block|{
name|MemoryTopicSub
name|sub
init|=
name|topicSubMap
operator|.
name|get
argument_list|(
operator|new
name|SubscriptionKey
argument_list|(
name|clientId
argument_list|,
name|subscriptionName
argument_list|)
argument_list|)
decl_stmt|;
if|if
condition|(
name|sub
operator|!=
literal|null
condition|)
block|{
name|sub
operator|.
name|resetBatching
argument_list|()
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

