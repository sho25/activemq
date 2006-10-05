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
name|store
operator|.
name|kahadaptor
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
name|kaha
operator|.
name|ListContainer
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
name|MapContainer
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
name|Marshaller
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
name|kaha
operator|.
name|StoreEntry
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
name|StringMarshaller
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
name|edu
operator|.
name|emory
operator|.
name|mathcs
operator|.
name|backport
operator|.
name|java
operator|.
name|util
operator|.
name|concurrent
operator|.
name|ConcurrentHashMap
import|;
end_import

begin_import
import|import
name|edu
operator|.
name|emory
operator|.
name|mathcs
operator|.
name|backport
operator|.
name|java
operator|.
name|util
operator|.
name|concurrent
operator|.
name|atomic
operator|.
name|AtomicInteger
import|;
end_import

begin_comment
comment|/**  * @version $Revision: 1.5 $  */
end_comment

begin_class
specifier|public
class|class
name|KahaTopicMessageStore
implements|implements
name|TopicMessageStore
block|{
specifier|private
name|ActiveMQDestination
name|destination
decl_stmt|;
specifier|private
name|ListContainer
name|ackContainer
decl_stmt|;
specifier|private
name|ListContainer
name|messageContainer
decl_stmt|;
specifier|private
name|Map
name|subscriberContainer
decl_stmt|;
specifier|private
name|Store
name|store
decl_stmt|;
specifier|private
name|Map
name|subscriberAcks
init|=
operator|new
name|ConcurrentHashMap
argument_list|()
decl_stmt|;
specifier|public
name|KahaTopicMessageStore
parameter_list|(
name|Store
name|store
parameter_list|,
name|ListContainer
name|messageContainer
parameter_list|,
name|ListContainer
name|ackContainer
parameter_list|,
name|MapContainer
name|subsContainer
parameter_list|,
name|ActiveMQDestination
name|destination
parameter_list|)
throws|throws
name|IOException
block|{
name|this
operator|.
name|messageContainer
operator|=
name|messageContainer
expr_stmt|;
name|this
operator|.
name|destination
operator|=
name|destination
expr_stmt|;
name|this
operator|.
name|store
operator|=
name|store
expr_stmt|;
name|this
operator|.
name|ackContainer
operator|=
name|ackContainer
expr_stmt|;
name|subscriberContainer
operator|=
name|subsContainer
expr_stmt|;
comment|// load all the Ack containers
for|for
control|(
name|Iterator
name|i
init|=
name|subscriberContainer
operator|.
name|keySet
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
name|Object
name|key
init|=
name|i
operator|.
name|next
argument_list|()
decl_stmt|;
name|addSubscriberAckContainer
argument_list|(
name|key
argument_list|)
expr_stmt|;
block|}
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
name|int
name|subscriberCount
init|=
name|subscriberAcks
operator|.
name|size
argument_list|()
decl_stmt|;
if|if
condition|(
name|subscriberCount
operator|>
literal|0
condition|)
block|{
name|StoreEntry
name|entry
init|=
name|messageContainer
operator|.
name|placeLast
argument_list|(
name|message
argument_list|)
decl_stmt|;
name|TopicSubAck
name|tsa
init|=
operator|new
name|TopicSubAck
argument_list|()
decl_stmt|;
name|tsa
operator|.
name|setCount
argument_list|(
name|subscriberCount
argument_list|)
expr_stmt|;
name|tsa
operator|.
name|setStoreEntry
argument_list|(
name|entry
argument_list|)
expr_stmt|;
name|StoreEntry
name|ackEntry
init|=
name|ackContainer
operator|.
name|placeLast
argument_list|(
name|tsa
argument_list|)
decl_stmt|;
for|for
control|(
name|Iterator
name|i
init|=
name|subscriberAcks
operator|.
name|keySet
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
name|Object
name|key
init|=
name|i
operator|.
name|next
argument_list|()
decl_stmt|;
name|ListContainer
name|container
init|=
name|store
operator|.
name|getListContainer
argument_list|(
name|key
argument_list|,
literal|"durable-subs"
argument_list|)
decl_stmt|;
name|container
operator|.
name|add
argument_list|(
name|ackEntry
argument_list|)
expr_stmt|;
block|}
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
parameter_list|)
throws|throws
name|IOException
block|{
name|String
name|subcriberId
init|=
name|getSubscriptionKey
argument_list|(
name|clientId
argument_list|,
name|subscriptionName
argument_list|)
decl_stmt|;
name|ListContainer
name|container
init|=
operator|(
name|ListContainer
operator|)
name|subscriberAcks
operator|.
name|get
argument_list|(
name|subcriberId
argument_list|)
decl_stmt|;
if|if
condition|(
name|container
operator|!=
literal|null
condition|)
block|{
name|StoreEntry
name|ackEntry
init|=
operator|(
name|StoreEntry
operator|)
name|container
operator|.
name|removeFirst
argument_list|()
decl_stmt|;
if|if
condition|(
name|ackEntry
operator|!=
literal|null
condition|)
block|{
name|TopicSubAck
name|tsa
init|=
operator|(
name|TopicSubAck
operator|)
name|ackContainer
operator|.
name|get
argument_list|(
name|ackEntry
argument_list|)
decl_stmt|;
if|if
condition|(
name|tsa
operator|!=
literal|null
condition|)
block|{
if|if
condition|(
name|tsa
operator|.
name|decrementCount
argument_list|()
operator|<=
literal|0
condition|)
block|{
name|ackContainer
operator|.
name|remove
argument_list|(
name|ackEntry
argument_list|)
expr_stmt|;
name|messageContainer
operator|.
name|remove
argument_list|(
name|tsa
operator|.
name|getStoreEntry
argument_list|()
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|ackContainer
operator|.
name|update
argument_list|(
name|ackEntry
argument_list|,
name|tsa
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|}
block|}
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
operator|(
name|SubscriptionInfo
operator|)
name|subscriberContainer
operator|.
name|get
argument_list|(
name|getSubscriptionKey
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
name|String
name|clientId
parameter_list|,
name|String
name|subscriptionName
parameter_list|,
name|String
name|selector
parameter_list|,
name|boolean
name|retroactive
parameter_list|)
throws|throws
name|IOException
block|{
name|SubscriptionInfo
name|info
init|=
operator|new
name|SubscriptionInfo
argument_list|()
decl_stmt|;
name|info
operator|.
name|setDestination
argument_list|(
name|destination
argument_list|)
expr_stmt|;
name|info
operator|.
name|setClientId
argument_list|(
name|clientId
argument_list|)
expr_stmt|;
name|info
operator|.
name|setSelector
argument_list|(
name|selector
argument_list|)
expr_stmt|;
name|info
operator|.
name|setSubcriptionName
argument_list|(
name|subscriptionName
argument_list|)
expr_stmt|;
name|String
name|key
init|=
name|getSubscriptionKey
argument_list|(
name|clientId
argument_list|,
name|subscriptionName
argument_list|)
decl_stmt|;
comment|// if already exists - won't add it again as it causes data files
comment|// to hang around
if|if
condition|(
operator|!
name|subscriberContainer
operator|.
name|containsKey
argument_list|(
name|key
argument_list|)
condition|)
block|{
name|subscriberContainer
operator|.
name|put
argument_list|(
name|key
argument_list|,
name|info
argument_list|)
expr_stmt|;
block|}
name|addSubscriberAckContainer
argument_list|(
name|key
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
name|String
name|key
init|=
name|getSubscriptionKey
argument_list|(
name|clientId
argument_list|,
name|subscriptionName
argument_list|)
decl_stmt|;
name|subscriberContainer
operator|.
name|remove
argument_list|(
name|key
argument_list|)
expr_stmt|;
name|ListContainer
name|list
init|=
operator|(
name|ListContainer
operator|)
name|subscriberAcks
operator|.
name|get
argument_list|(
name|key
argument_list|)
decl_stmt|;
for|for
control|(
name|Iterator
name|i
init|=
name|list
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
name|StoreEntry
name|ackEntry
init|=
operator|(
name|StoreEntry
operator|)
name|i
operator|.
name|next
argument_list|()
decl_stmt|;
if|if
condition|(
name|ackEntry
operator|!=
literal|null
condition|)
block|{
name|TopicSubAck
name|tsa
init|=
operator|(
name|TopicSubAck
operator|)
name|ackContainer
operator|.
name|get
argument_list|(
name|ackEntry
argument_list|)
decl_stmt|;
if|if
condition|(
name|tsa
operator|!=
literal|null
condition|)
block|{
if|if
condition|(
name|tsa
operator|.
name|decrementCount
argument_list|()
operator|<=
literal|0
condition|)
block|{
name|ackContainer
operator|.
name|remove
argument_list|(
name|ackEntry
argument_list|)
expr_stmt|;
name|messageContainer
operator|.
name|remove
argument_list|(
name|tsa
operator|.
name|getStoreEntry
argument_list|()
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|ackContainer
operator|.
name|update
argument_list|(
name|ackEntry
argument_list|,
name|tsa
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|}
block|}
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
name|String
name|key
init|=
name|getSubscriptionKey
argument_list|(
name|clientId
argument_list|,
name|subscriptionName
argument_list|)
decl_stmt|;
name|ListContainer
name|list
init|=
operator|(
name|ListContainer
operator|)
name|subscriberAcks
operator|.
name|get
argument_list|(
name|key
argument_list|)
decl_stmt|;
if|if
condition|(
name|list
operator|!=
literal|null
condition|)
block|{
for|for
control|(
name|Iterator
name|i
init|=
name|list
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
name|TopicSubAck
name|tsa
init|=
operator|(
name|TopicSubAck
operator|)
name|i
operator|.
name|next
argument_list|()
decl_stmt|;
name|Object
name|msg
init|=
name|messageContainer
operator|.
name|get
argument_list|(
name|tsa
operator|.
name|getStoreEntry
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
name|msg
operator|!=
literal|null
condition|)
block|{
if|if
condition|(
name|msg
operator|.
name|getClass
argument_list|()
operator|==
name|String
operator|.
name|class
condition|)
block|{
name|listener
operator|.
name|recoverMessageReference
argument_list|(
operator|(
name|String
operator|)
name|msg
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|listener
operator|.
name|recoverMessage
argument_list|(
operator|(
name|Message
operator|)
name|msg
argument_list|)
expr_stmt|;
block|}
block|}
name|listener
operator|.
name|finished
argument_list|()
expr_stmt|;
block|}
block|}
else|else
block|{
name|listener
operator|.
name|finished
argument_list|()
expr_stmt|;
block|}
block|}
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
name|MessageId
name|lastMessageId
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
name|String
name|key
init|=
name|getSubscriptionKey
argument_list|(
name|clientId
argument_list|,
name|subscriptionName
argument_list|)
decl_stmt|;
name|ListContainer
name|list
init|=
operator|(
name|ListContainer
operator|)
name|subscriberAcks
operator|.
name|get
argument_list|(
name|key
argument_list|)
decl_stmt|;
if|if
condition|(
name|list
operator|!=
literal|null
condition|)
block|{
name|boolean
name|startFound
init|=
literal|false
decl_stmt|;
name|int
name|count
init|=
literal|0
decl_stmt|;
for|for
control|(
name|Iterator
name|i
init|=
name|list
operator|.
name|iterator
argument_list|()
init|;
name|i
operator|.
name|hasNext
argument_list|()
operator|&&
name|count
operator|<
name|maxReturned
condition|;
control|)
block|{
name|TopicSubAck
name|tsa
init|=
operator|(
name|TopicSubAck
operator|)
name|i
operator|.
name|next
argument_list|()
decl_stmt|;
name|Object
name|msg
init|=
name|messageContainer
operator|.
name|get
argument_list|(
name|tsa
operator|.
name|getStoreEntry
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
name|msg
operator|!=
literal|null
condition|)
block|{
if|if
condition|(
name|msg
operator|.
name|getClass
argument_list|()
operator|==
name|String
operator|.
name|class
condition|)
block|{
name|String
name|ref
init|=
name|msg
operator|.
name|toString
argument_list|()
decl_stmt|;
if|if
condition|(
name|startFound
operator|||
name|lastMessageId
operator|==
literal|null
condition|)
block|{
name|listener
operator|.
name|recoverMessageReference
argument_list|(
name|ref
argument_list|)
expr_stmt|;
name|count
operator|++
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|startFound
operator|||
name|ref
operator|.
name|equals
argument_list|(
name|lastMessageId
operator|.
name|toString
argument_list|()
argument_list|)
condition|)
block|{
name|startFound
operator|=
literal|true
expr_stmt|;
block|}
block|}
else|else
block|{
name|Message
name|message
init|=
operator|(
name|Message
operator|)
name|msg
decl_stmt|;
if|if
condition|(
name|startFound
operator|||
name|message
operator|.
name|getMessageId
argument_list|()
operator|.
name|equals
argument_list|(
name|lastMessageId
argument_list|)
condition|)
block|{
name|startFound
operator|=
literal|true
expr_stmt|;
block|}
else|else
block|{
name|listener
operator|.
name|recoverMessage
argument_list|(
name|message
argument_list|)
expr_stmt|;
name|count
operator|++
expr_stmt|;
block|}
block|}
block|}
name|listener
operator|.
name|finished
argument_list|()
expr_stmt|;
block|}
block|}
else|else
block|{
name|listener
operator|.
name|finished
argument_list|()
expr_stmt|;
block|}
block|}
specifier|public
name|void
name|delete
parameter_list|()
block|{
name|messageContainer
operator|.
name|clear
argument_list|()
expr_stmt|;
name|ackContainer
operator|.
name|clear
argument_list|()
expr_stmt|;
name|subscriberContainer
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
operator|(
name|SubscriptionInfo
index|[]
operator|)
name|subscriberContainer
operator|.
name|values
argument_list|()
operator|.
name|toArray
argument_list|(
operator|new
name|SubscriptionInfo
index|[
name|subscriberContainer
operator|.
name|size
argument_list|()
index|]
argument_list|)
return|;
block|}
specifier|protected
name|String
name|getSubscriptionKey
parameter_list|(
name|String
name|clientId
parameter_list|,
name|String
name|subscriberName
parameter_list|)
block|{
name|String
name|result
init|=
name|clientId
operator|+
literal|":"
decl_stmt|;
name|result
operator|+=
name|subscriberName
operator|!=
literal|null
condition|?
name|subscriberName
else|:
literal|"NOT_SET"
expr_stmt|;
return|return
name|result
return|;
block|}
specifier|protected
name|void
name|addSubscriberAckContainer
parameter_list|(
name|Object
name|key
parameter_list|)
throws|throws
name|IOException
block|{
name|ListContainer
name|container
init|=
name|store
operator|.
name|getListContainer
argument_list|(
name|key
argument_list|,
literal|"topic-subs"
argument_list|)
decl_stmt|;
name|Marshaller
name|marshaller
init|=
operator|new
name|StoreEntryMarshaller
argument_list|()
decl_stmt|;
name|container
operator|.
name|setMarshaller
argument_list|(
name|marshaller
argument_list|)
expr_stmt|;
name|subscriberAcks
operator|.
name|put
argument_list|(
name|key
argument_list|,
name|container
argument_list|)
expr_stmt|;
block|}
specifier|public
name|Message
name|getNextMessageToDeliver
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
name|String
name|key
init|=
name|getSubscriptionKey
argument_list|(
name|clientId
argument_list|,
name|subscriptionName
argument_list|)
decl_stmt|;
name|ListContainer
name|list
init|=
operator|(
name|ListContainer
operator|)
name|subscriberAcks
operator|.
name|get
argument_list|(
name|key
argument_list|)
decl_stmt|;
name|Iterator
name|iter
init|=
name|list
operator|.
name|iterator
argument_list|()
decl_stmt|;
name|TopicSubAck
name|tsa
init|=
operator|(
name|TopicSubAck
operator|)
name|list
operator|.
name|get
argument_list|(
literal|0
argument_list|)
decl_stmt|;
name|Message
name|msg
init|=
operator|(
name|Message
operator|)
name|messageContainer
operator|.
name|get
argument_list|(
name|tsa
operator|.
name|getStoreEntry
argument_list|()
argument_list|)
decl_stmt|;
return|return
name|msg
return|;
block|}
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
name|String
name|key
init|=
name|getSubscriptionKey
argument_list|(
name|clientId
argument_list|,
name|subscriberName
argument_list|)
decl_stmt|;
name|ListContainer
name|list
init|=
operator|(
name|ListContainer
operator|)
name|subscriberAcks
operator|.
name|get
argument_list|(
name|key
argument_list|)
decl_stmt|;
return|return
name|list
operator|.
name|size
argument_list|()
return|;
block|}
comment|/**      * @param context      * @param messageId      * @param expirationTime      * @param messageRef      * @throws IOException      * @see org.apache.activemq.store.MessageStore#addMessageReference(org.apache.activemq.broker.ConnectionContext, org.apache.activemq.command.MessageId, long, java.lang.String)      */
specifier|public
name|void
name|addMessageReference
parameter_list|(
name|ConnectionContext
name|context
parameter_list|,
name|MessageId
name|messageId
parameter_list|,
name|long
name|expirationTime
parameter_list|,
name|String
name|messageRef
parameter_list|)
throws|throws
name|IOException
block|{
name|messageContainer
operator|.
name|add
argument_list|(
name|messageRef
argument_list|)
expr_stmt|;
block|}
comment|/**      * @return the destination      * @see org.apache.activemq.store.MessageStore#getDestination()      */
specifier|public
name|ActiveMQDestination
name|getDestination
parameter_list|()
block|{
return|return
name|destination
return|;
block|}
comment|/**      * @param identity      * @return the Message      * @throws IOException      * @see org.apache.activemq.store.MessageStore#getMessage(org.apache.activemq.command.MessageId)      */
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
name|Message
name|result
init|=
literal|null
decl_stmt|;
for|for
control|(
name|Iterator
name|i
init|=
name|messageContainer
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
name|Message
name|msg
init|=
operator|(
name|Message
operator|)
name|i
operator|.
name|next
argument_list|()
decl_stmt|;
if|if
condition|(
name|msg
operator|.
name|getMessageId
argument_list|()
operator|.
name|equals
argument_list|(
name|identity
argument_list|)
condition|)
block|{
name|result
operator|=
name|msg
expr_stmt|;
break|break;
block|}
block|}
return|return
name|result
return|;
block|}
comment|/**      * @param identity      * @return String      * @throws IOException      * @see org.apache.activemq.store.MessageStore#getMessageReference(org.apache.activemq.command.MessageId)      */
specifier|public
name|String
name|getMessageReference
parameter_list|(
name|MessageId
name|identity
parameter_list|)
throws|throws
name|IOException
block|{
return|return
literal|null
return|;
block|}
comment|/**      * @throws Exception      * @see org.apache.activemq.store.MessageStore#recover(org.apache.activemq.store.MessageRecoveryListener)      */
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
for|for
control|(
name|Iterator
name|iter
init|=
name|messageContainer
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
name|Object
name|msg
init|=
name|iter
operator|.
name|next
argument_list|()
decl_stmt|;
if|if
condition|(
name|msg
operator|.
name|getClass
argument_list|()
operator|==
name|String
operator|.
name|class
condition|)
block|{
name|listener
operator|.
name|recoverMessageReference
argument_list|(
operator|(
name|String
operator|)
name|msg
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|listener
operator|.
name|recoverMessage
argument_list|(
operator|(
name|Message
operator|)
name|msg
argument_list|)
expr_stmt|;
block|}
block|}
name|listener
operator|.
name|finished
argument_list|()
expr_stmt|;
block|}
comment|/**      * @param context      * @throws IOException      * @see org.apache.activemq.store.MessageStore#removeAllMessages(org.apache.activemq.broker.ConnectionContext)      */
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
name|messageContainer
operator|.
name|clear
argument_list|()
expr_stmt|;
block|}
comment|/**      * @param context      * @param ack      * @throws IOException      * @see org.apache.activemq.store.MessageStore#removeMessage(org.apache.activemq.broker.ConnectionContext, org.apache.activemq.command.MessageAck)      */
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
for|for
control|(
name|Iterator
name|i
init|=
name|messageContainer
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
name|Message
name|msg
init|=
operator|(
name|Message
operator|)
name|i
operator|.
name|next
argument_list|()
decl_stmt|;
if|if
condition|(
name|msg
operator|.
name|getMessageId
argument_list|()
operator|.
name|equals
argument_list|(
name|ack
operator|.
name|getLastMessageId
argument_list|()
argument_list|)
condition|)
block|{
name|i
operator|.
name|remove
argument_list|()
expr_stmt|;
break|break;
block|}
block|}
block|}
comment|/**      * @param usageManager      * @see org.apache.activemq.store.MessageStore#setUsageManager(org.apache.activemq.memory.UsageManager)      */
specifier|public
name|void
name|setUsageManager
parameter_list|(
name|UsageManager
name|usageManager
parameter_list|)
block|{
comment|// TODO Auto-generated method stub
block|}
comment|/**      * @throws Exception      * @see org.apache.activemq.Service#start()      */
specifier|public
name|void
name|start
parameter_list|()
throws|throws
name|Exception
block|{
comment|// TODO Auto-generated method stub
block|}
comment|/**      * @throws Exception      * @see org.apache.activemq.Service#stop()      */
specifier|public
name|void
name|stop
parameter_list|()
throws|throws
name|Exception
block|{
comment|// TODO Auto-generated method stub
block|}
block|}
end_class

end_unit

