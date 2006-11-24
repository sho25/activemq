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
name|store
operator|.
name|rapid
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
name|org
operator|.
name|apache
operator|.
name|activeio
operator|.
name|journal
operator|.
name|active
operator|.
name|Location
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
name|store
operator|.
name|kahadaptor
operator|.
name|ConsumerMessageRef
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
name|kahadaptor
operator|.
name|ConsumerMessageRefMarshaller
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
name|kahadaptor
operator|.
name|TopicSubAck
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
name|kahadaptor
operator|.
name|TopicSubContainer
import|;
end_import

begin_comment
comment|/**  * A MessageStore that uses a Journal to store it's messages.  *   * @version $Revision: 1.13 $  */
end_comment

begin_class
specifier|public
class|class
name|RapidTopicMessageStore
extends|extends
name|RapidMessageStore
implements|implements
name|TopicMessageStore
block|{
specifier|private
name|ListContainer
name|ackContainer
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
name|subscriberMessages
init|=
operator|new
name|ConcurrentHashMap
argument_list|()
decl_stmt|;
specifier|public
name|RapidTopicMessageStore
parameter_list|(
name|RapidPersistenceAdapter
name|adapter
parameter_list|,
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
parameter_list|,
name|int
name|maximumCacheSize
parameter_list|)
throws|throws
name|IOException
block|{
name|super
argument_list|(
name|adapter
argument_list|,
name|destination
argument_list|,
name|messageContainer
argument_list|,
name|maximumCacheSize
argument_list|)
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
name|addSubscriberMessageContainer
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
name|subscriberMessages
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
specifier|final
name|Location
name|location
init|=
name|peristenceAdapter
operator|.
name|writeCommand
argument_list|(
name|message
argument_list|,
name|message
operator|.
name|isResponseRequired
argument_list|()
argument_list|)
decl_stmt|;
specifier|final
name|RapidMessageReference
name|md
init|=
operator|new
name|RapidMessageReference
argument_list|(
name|message
argument_list|,
name|location
argument_list|)
decl_stmt|;
name|StoreEntry
name|messageEntry
init|=
name|messageContainer
operator|.
name|placeLast
argument_list|(
name|md
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
name|setMessageEntry
argument_list|(
name|messageEntry
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
name|subscriberMessages
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
name|TopicSubContainer
name|container
init|=
operator|(
name|TopicSubContainer
operator|)
name|i
operator|.
name|next
argument_list|()
decl_stmt|;
name|ConsumerMessageRef
name|ref
init|=
operator|new
name|ConsumerMessageRef
argument_list|()
decl_stmt|;
name|ref
operator|.
name|setAckEntry
argument_list|(
name|ackEntry
argument_list|)
expr_stmt|;
name|ref
operator|.
name|setMessageEntry
argument_list|(
name|messageEntry
argument_list|)
expr_stmt|;
name|container
operator|.
name|getListContainer
argument_list|()
operator|.
name|add
argument_list|(
name|ref
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
name|TopicSubContainer
name|container
init|=
operator|(
name|TopicSubContainer
operator|)
name|subscriberMessages
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
name|ConsumerMessageRef
name|ref
init|=
operator|(
name|ConsumerMessageRef
operator|)
name|container
operator|.
name|getListContainer
argument_list|()
operator|.
name|removeFirst
argument_list|()
decl_stmt|;
if|if
condition|(
name|ref
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
name|ref
operator|.
name|getAckEntry
argument_list|()
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
name|ref
operator|.
name|getAckEntry
argument_list|()
argument_list|)
expr_stmt|;
name|messageContainer
operator|.
name|remove
argument_list|(
name|tsa
operator|.
name|getMessageEntry
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
name|ref
operator|.
name|getAckEntry
argument_list|()
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
name|addSubscriberMessageContainer
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
name|TopicSubContainer
name|container
init|=
operator|(
name|TopicSubContainer
operator|)
name|subscriberMessages
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
name|container
operator|.
name|getListContainer
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
name|ConsumerMessageRef
name|ref
init|=
operator|(
name|ConsumerMessageRef
operator|)
name|i
operator|.
name|next
argument_list|()
decl_stmt|;
if|if
condition|(
name|ref
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
name|ref
operator|.
name|getAckEntry
argument_list|()
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
name|ref
operator|.
name|getAckEntry
argument_list|()
argument_list|)
expr_stmt|;
name|messageContainer
operator|.
name|remove
argument_list|(
name|tsa
operator|.
name|getMessageEntry
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
name|ref
operator|.
name|getAckEntry
argument_list|()
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
name|TopicSubContainer
name|container
init|=
operator|(
name|TopicSubContainer
operator|)
name|subscriberMessages
operator|.
name|get
argument_list|(
name|key
argument_list|)
decl_stmt|;
if|if
condition|(
name|container
operator|!=
literal|null
condition|)
block|{
for|for
control|(
name|Iterator
name|i
init|=
name|container
operator|.
name|getListContainer
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
name|ConsumerMessageRef
name|ref
init|=
operator|(
name|ConsumerMessageRef
operator|)
name|i
operator|.
name|next
argument_list|()
decl_stmt|;
name|RapidMessageReference
name|messageReference
init|=
operator|(
name|RapidMessageReference
operator|)
name|messageContainer
operator|.
name|get
argument_list|(
name|ref
operator|.
name|getMessageEntry
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
name|messageReference
operator|!=
literal|null
condition|)
block|{
name|Message
name|m
init|=
operator|(
name|Message
operator|)
name|peristenceAdapter
operator|.
name|readCommand
argument_list|(
name|messageReference
operator|.
name|getLocation
argument_list|()
argument_list|)
decl_stmt|;
name|listener
operator|.
name|recoverMessage
argument_list|(
name|m
argument_list|)
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
name|TopicSubContainer
name|container
init|=
operator|(
name|TopicSubContainer
operator|)
name|subscriberMessages
operator|.
name|get
argument_list|(
name|key
argument_list|)
decl_stmt|;
if|if
condition|(
name|container
operator|!=
literal|null
condition|)
block|{
name|int
name|count
init|=
literal|0
decl_stmt|;
name|StoreEntry
name|entry
init|=
name|container
operator|.
name|getBatchEntry
argument_list|()
decl_stmt|;
if|if
condition|(
name|entry
operator|==
literal|null
condition|)
block|{
name|entry
operator|=
name|container
operator|.
name|getListContainer
argument_list|()
operator|.
name|getFirst
argument_list|()
expr_stmt|;
block|}
else|else
block|{
name|entry
operator|=
name|container
operator|.
name|getListContainer
argument_list|()
operator|.
name|refresh
argument_list|(
name|entry
argument_list|)
expr_stmt|;
name|entry
operator|=
name|container
operator|.
name|getListContainer
argument_list|()
operator|.
name|getNext
argument_list|(
name|entry
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|entry
operator|!=
literal|null
condition|)
block|{
do|do
block|{
name|ConsumerMessageRef
name|consumerRef
init|=
operator|(
name|ConsumerMessageRef
operator|)
name|container
operator|.
name|getListContainer
argument_list|()
operator|.
name|get
argument_list|(
name|entry
argument_list|)
decl_stmt|;
name|RapidMessageReference
name|messageReference
init|=
operator|(
name|RapidMessageReference
operator|)
name|messageContainer
operator|.
name|get
argument_list|(
name|consumerRef
operator|.
name|getMessageEntry
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
name|messageReference
operator|!=
literal|null
condition|)
block|{
name|Message
name|m
init|=
operator|(
name|Message
operator|)
name|peristenceAdapter
operator|.
name|readCommand
argument_list|(
name|messageReference
operator|.
name|getLocation
argument_list|()
argument_list|)
decl_stmt|;
name|listener
operator|.
name|recoverMessage
argument_list|(
name|m
argument_list|)
expr_stmt|;
name|count
operator|++
expr_stmt|;
block|}
name|container
operator|.
name|setBatchEntry
argument_list|(
name|entry
argument_list|)
expr_stmt|;
name|entry
operator|=
name|container
operator|.
name|getListContainer
argument_list|()
operator|.
name|getNext
argument_list|(
name|entry
argument_list|)
expr_stmt|;
block|}
do|while
condition|(
name|entry
operator|!=
literal|null
operator|&&
name|count
operator|<
name|maxReturned
operator|&&
name|listener
operator|.
name|hasSpace
argument_list|()
condition|)
do|;
block|}
block|}
name|listener
operator|.
name|finished
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
name|addSubscriberMessageContainer
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
name|ConsumerMessageRefMarshaller
argument_list|()
decl_stmt|;
name|container
operator|.
name|setMarshaller
argument_list|(
name|marshaller
argument_list|)
expr_stmt|;
name|TopicSubContainer
name|tsc
init|=
operator|new
name|TopicSubContainer
argument_list|(
name|container
argument_list|)
decl_stmt|;
name|subscriberMessages
operator|.
name|put
argument_list|(
name|key
argument_list|,
name|tsc
argument_list|)
expr_stmt|;
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
name|TopicSubContainer
name|container
init|=
operator|(
name|TopicSubContainer
operator|)
name|subscriberMessages
operator|.
name|get
argument_list|(
name|key
argument_list|)
decl_stmt|;
return|return
name|container
operator|.
name|getListContainer
argument_list|()
operator|.
name|size
argument_list|()
return|;
block|}
comment|/**      * @param context      * @param messageId      * @param expirationTime      * @param messageRef      * @throws IOException      * @see org.apache.activemq.store.MessageStore#addMessageReference(org.apache.activemq.broker.ConnectionContext,      *      org.apache.activemq.command.MessageId, long, java.lang.String)      */
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
throw|throw
operator|new
name|IOException
argument_list|(
literal|"Not supported"
argument_list|)
throw|;
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
comment|/**      * @param context      * @throws IOException      * @see org.apache.activemq.store.MessageStore#removeAllMessages(org.apache.activemq.broker.ConnectionContext)      */
specifier|public
specifier|synchronized
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
name|ackContainer
operator|.
name|clear
argument_list|()
expr_stmt|;
for|for
control|(
name|Iterator
name|i
init|=
name|subscriberMessages
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
name|TopicSubContainer
name|container
init|=
operator|(
name|TopicSubContainer
operator|)
name|i
operator|.
name|next
argument_list|()
decl_stmt|;
name|container
operator|.
name|getListContainer
argument_list|()
operator|.
name|clear
argument_list|()
expr_stmt|;
block|}
block|}
specifier|public
specifier|synchronized
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
name|TopicSubContainer
name|topicSubContainer
init|=
operator|(
name|TopicSubContainer
operator|)
name|subscriberMessages
operator|.
name|get
argument_list|(
name|key
argument_list|)
decl_stmt|;
if|if
condition|(
name|topicSubContainer
operator|!=
literal|null
condition|)
block|{
name|topicSubContainer
operator|.
name|reset
argument_list|()
expr_stmt|;
block|}
block|}
specifier|public
name|Location
name|checkpoint
parameter_list|()
throws|throws
name|IOException
block|{
return|return
literal|null
return|;
block|}
specifier|public
specifier|synchronized
name|void
name|replayAcknowledge
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
name|TopicSubContainer
name|container
init|=
operator|(
name|TopicSubContainer
operator|)
name|subscriberMessages
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
name|ConsumerMessageRef
name|ref
init|=
operator|(
name|ConsumerMessageRef
operator|)
name|container
operator|.
name|getListContainer
argument_list|()
operator|.
name|removeFirst
argument_list|()
decl_stmt|;
if|if
condition|(
name|ref
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
name|ref
operator|.
name|getAckEntry
argument_list|()
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
name|ref
operator|.
name|getAckEntry
argument_list|()
argument_list|)
expr_stmt|;
name|messageContainer
operator|.
name|remove
argument_list|(
name|tsa
operator|.
name|getMessageEntry
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
name|ref
operator|.
name|getAckEntry
argument_list|()
argument_list|,
name|tsa
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|}
block|}
block|}
end_class

end_unit

