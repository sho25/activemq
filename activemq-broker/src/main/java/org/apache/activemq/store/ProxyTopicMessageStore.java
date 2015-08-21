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
name|usage
operator|.
name|MemoryUsage
import|;
end_import

begin_comment
comment|/**  * A simple proxy that delegates to another MessageStore.  */
end_comment

begin_class
specifier|public
class|class
name|ProxyTopicMessageStore
implements|implements
name|TopicMessageStore
block|{
specifier|final
name|TopicMessageStore
name|delegate
decl_stmt|;
specifier|public
name|ProxyTopicMessageStore
parameter_list|(
name|TopicMessageStore
name|delegate
parameter_list|)
block|{
name|this
operator|.
name|delegate
operator|=
name|delegate
expr_stmt|;
block|}
specifier|public
name|MessageStore
name|getDelegate
parameter_list|()
block|{
return|return
name|delegate
return|;
block|}
annotation|@
name|Override
specifier|public
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
name|delegate
operator|.
name|addMessage
argument_list|(
name|context
argument_list|,
name|message
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|addMessage
parameter_list|(
name|ConnectionContext
name|context
parameter_list|,
name|Message
name|message
parameter_list|,
name|boolean
name|canOptimizeHint
parameter_list|)
throws|throws
name|IOException
block|{
name|delegate
operator|.
name|addMessage
argument_list|(
name|context
argument_list|,
name|message
argument_list|,
name|canOptimizeHint
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|Message
name|getMessage
parameter_list|(
name|MessageId
name|identity
parameter_list|)
throws|throws
name|IOException
block|{
return|return
name|delegate
operator|.
name|getMessage
argument_list|(
name|identity
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|recover
parameter_list|(
name|MessageRecoveryListener
name|listener
parameter_list|)
throws|throws
name|Exception
block|{
name|delegate
operator|.
name|recover
argument_list|(
name|listener
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|removeAllMessages
parameter_list|(
name|ConnectionContext
name|context
parameter_list|)
throws|throws
name|IOException
block|{
name|delegate
operator|.
name|removeAllMessages
argument_list|(
name|context
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|removeMessage
parameter_list|(
name|ConnectionContext
name|context
parameter_list|,
name|MessageAck
name|ack
parameter_list|)
throws|throws
name|IOException
block|{
name|delegate
operator|.
name|removeMessage
argument_list|(
name|context
argument_list|,
name|ack
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|start
parameter_list|()
throws|throws
name|Exception
block|{
name|delegate
operator|.
name|start
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|stop
parameter_list|()
throws|throws
name|Exception
block|{
name|delegate
operator|.
name|stop
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
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
name|delegate
operator|.
name|lookupSubscription
argument_list|(
name|clientId
argument_list|,
name|subscriptionName
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
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
name|delegate
operator|.
name|acknowledge
argument_list|(
name|context
argument_list|,
name|clientId
argument_list|,
name|subscriptionName
argument_list|,
name|messageId
argument_list|,
name|ack
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|addSubscription
parameter_list|(
name|SubscriptionInfo
name|subscriptionInfo
parameter_list|,
name|boolean
name|retroactive
parameter_list|)
throws|throws
name|IOException
block|{
name|delegate
operator|.
name|addSubscription
argument_list|(
name|subscriptionInfo
argument_list|,
name|retroactive
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|deleteSubscription
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
name|delegate
operator|.
name|deleteSubscription
argument_list|(
name|clientId
argument_list|,
name|subscriptionName
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
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
name|delegate
operator|.
name|recoverSubscription
argument_list|(
name|clientId
argument_list|,
name|subscriptionName
argument_list|,
name|listener
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
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
name|delegate
operator|.
name|recoverNextMessages
argument_list|(
name|clientId
argument_list|,
name|subscriptionName
argument_list|,
name|maxReturned
argument_list|,
name|listener
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
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
name|delegate
operator|.
name|resetBatching
argument_list|(
name|clientId
argument_list|,
name|subscriptionName
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|ActiveMQDestination
name|getDestination
parameter_list|()
block|{
return|return
name|delegate
operator|.
name|getDestination
argument_list|()
return|;
block|}
annotation|@
name|Override
specifier|public
name|SubscriptionInfo
index|[]
name|getAllSubscriptions
parameter_list|()
throws|throws
name|IOException
block|{
return|return
name|delegate
operator|.
name|getAllSubscriptions
argument_list|()
return|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|setMemoryUsage
parameter_list|(
name|MemoryUsage
name|memoryUsage
parameter_list|)
block|{
name|delegate
operator|.
name|setMemoryUsage
argument_list|(
name|memoryUsage
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
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
return|return
name|delegate
operator|.
name|getMessageCount
argument_list|(
name|clientId
argument_list|,
name|subscriberName
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|int
name|getMessageCount
parameter_list|()
throws|throws
name|IOException
block|{
return|return
name|delegate
operator|.
name|getMessageCount
argument_list|()
return|;
block|}
annotation|@
name|Override
specifier|public
name|long
name|getMessageSize
parameter_list|()
throws|throws
name|IOException
block|{
return|return
name|delegate
operator|.
name|getMessageSize
argument_list|()
return|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|recoverNextMessages
parameter_list|(
name|int
name|maxReturned
parameter_list|,
name|MessageRecoveryListener
name|listener
parameter_list|)
throws|throws
name|Exception
block|{
name|delegate
operator|.
name|recoverNextMessages
argument_list|(
name|maxReturned
argument_list|,
name|listener
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|dispose
parameter_list|(
name|ConnectionContext
name|context
parameter_list|)
block|{
name|delegate
operator|.
name|dispose
argument_list|(
name|context
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|resetBatching
parameter_list|()
block|{
name|delegate
operator|.
name|resetBatching
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|setBatch
parameter_list|(
name|MessageId
name|messageId
parameter_list|)
throws|throws
name|Exception
block|{
name|delegate
operator|.
name|setBatch
argument_list|(
name|messageId
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|isEmpty
parameter_list|()
throws|throws
name|Exception
block|{
return|return
name|delegate
operator|.
name|isEmpty
argument_list|()
return|;
block|}
annotation|@
name|Override
specifier|public
name|ListenableFuture
argument_list|<
name|Object
argument_list|>
name|asyncAddTopicMessage
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
return|return
name|delegate
operator|.
name|asyncAddTopicMessage
argument_list|(
name|context
argument_list|,
name|message
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|ListenableFuture
argument_list|<
name|Object
argument_list|>
name|asyncAddTopicMessage
parameter_list|(
name|ConnectionContext
name|context
parameter_list|,
name|Message
name|message
parameter_list|,
name|boolean
name|canOptimizeHint
parameter_list|)
throws|throws
name|IOException
block|{
return|return
name|delegate
operator|.
name|asyncAddTopicMessage
argument_list|(
name|context
argument_list|,
name|message
argument_list|,
name|canOptimizeHint
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|ListenableFuture
argument_list|<
name|Object
argument_list|>
name|asyncAddQueueMessage
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
return|return
name|delegate
operator|.
name|asyncAddQueueMessage
argument_list|(
name|context
argument_list|,
name|message
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|ListenableFuture
argument_list|<
name|Object
argument_list|>
name|asyncAddQueueMessage
parameter_list|(
name|ConnectionContext
name|context
parameter_list|,
name|Message
name|message
parameter_list|,
name|boolean
name|canOptimizeHint
parameter_list|)
throws|throws
name|IOException
block|{
return|return
name|delegate
operator|.
name|asyncAddQueueMessage
argument_list|(
name|context
argument_list|,
name|message
argument_list|,
name|canOptimizeHint
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|removeAsyncMessage
parameter_list|(
name|ConnectionContext
name|context
parameter_list|,
name|MessageAck
name|ack
parameter_list|)
throws|throws
name|IOException
block|{
name|delegate
operator|.
name|removeAsyncMessage
argument_list|(
name|context
argument_list|,
name|ack
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|setPrioritizedMessages
parameter_list|(
name|boolean
name|prioritizedMessages
parameter_list|)
block|{
name|delegate
operator|.
name|setPrioritizedMessages
argument_list|(
name|prioritizedMessages
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|isPrioritizedMessages
parameter_list|()
block|{
return|return
name|delegate
operator|.
name|isPrioritizedMessages
argument_list|()
return|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|updateMessage
parameter_list|(
name|Message
name|message
parameter_list|)
throws|throws
name|IOException
block|{
name|delegate
operator|.
name|updateMessage
argument_list|(
name|message
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|registerIndexListener
parameter_list|(
name|IndexListener
name|indexListener
parameter_list|)
block|{
name|delegate
operator|.
name|registerIndexListener
argument_list|(
name|indexListener
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|MessageStoreStatistics
name|getMessageStoreStatistics
parameter_list|()
block|{
return|return
name|delegate
operator|.
name|getMessageStoreStatistics
argument_list|()
return|;
block|}
comment|/* (non-Javadoc)      * @see org.apache.activemq.store.TopicMessageStore#getMessageSize(java.lang.String, java.lang.String)      */
annotation|@
name|Override
specifier|public
name|long
name|getMessageSize
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
return|return
name|delegate
operator|.
name|getMessageSize
argument_list|(
name|clientId
argument_list|,
name|subscriberName
argument_list|)
return|;
block|}
block|}
end_class

end_unit

