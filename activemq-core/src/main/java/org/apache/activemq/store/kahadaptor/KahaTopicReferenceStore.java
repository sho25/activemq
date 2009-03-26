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
name|HashSet
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
name|Set
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
name|TopicReferenceStore
import|;
end_import

begin_class
specifier|public
class|class
name|KahaTopicReferenceStore
extends|extends
name|KahaReferenceStore
implements|implements
name|TopicReferenceStore
block|{
specifier|protected
name|ListContainer
argument_list|<
name|TopicSubAck
argument_list|>
name|ackContainer
decl_stmt|;
specifier|protected
name|Map
argument_list|<
name|String
argument_list|,
name|TopicSubContainer
argument_list|>
name|subscriberMessages
init|=
operator|new
name|ConcurrentHashMap
argument_list|<
name|String
argument_list|,
name|TopicSubContainer
argument_list|>
argument_list|()
decl_stmt|;
specifier|private
name|MapContainer
argument_list|<
name|String
argument_list|,
name|SubscriptionInfo
argument_list|>
name|subscriberContainer
decl_stmt|;
specifier|private
name|Store
name|store
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|String
name|TOPIC_SUB_NAME
init|=
literal|"tsn"
decl_stmt|;
specifier|public
name|KahaTopicReferenceStore
parameter_list|(
name|Store
name|store
parameter_list|,
name|KahaReferenceStoreAdapter
name|adapter
parameter_list|,
name|MapContainer
argument_list|<
name|MessageId
argument_list|,
name|ReferenceRecord
argument_list|>
name|messageContainer
parameter_list|,
name|ListContainer
argument_list|<
name|TopicSubAck
argument_list|>
name|ackContainer
parameter_list|,
name|MapContainer
argument_list|<
name|String
argument_list|,
name|SubscriptionInfo
argument_list|>
name|subsContainer
parameter_list|,
name|ActiveMQDestination
name|destination
parameter_list|)
throws|throws
name|IOException
block|{
name|super
argument_list|(
name|adapter
argument_list|,
name|messageContainer
argument_list|,
name|destination
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
argument_list|<
name|SubscriptionInfo
argument_list|>
name|i
init|=
name|subscriberContainer
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
name|SubscriptionInfo
name|info
init|=
name|i
operator|.
name|next
argument_list|()
decl_stmt|;
name|addSubscriberMessageContainer
argument_list|(
name|info
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
block|}
specifier|public
name|void
name|dispose
parameter_list|(
name|ConnectionContext
name|context
parameter_list|)
block|{
name|super
operator|.
name|dispose
argument_list|(
name|context
argument_list|)
expr_stmt|;
name|subscriberContainer
operator|.
name|delete
argument_list|()
expr_stmt|;
block|}
specifier|protected
name|MessageId
name|getMessageId
parameter_list|(
name|Object
name|object
parameter_list|)
block|{
return|return
operator|new
name|MessageId
argument_list|(
operator|(
operator|(
name|ReferenceRecord
operator|)
name|object
operator|)
operator|.
name|getMessageId
argument_list|()
argument_list|)
return|;
block|}
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
throw|throw
operator|new
name|RuntimeException
argument_list|(
literal|"Use addMessageReference instead"
argument_list|)
throw|;
block|}
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
throw|throw
operator|new
name|RuntimeException
argument_list|(
literal|"Use addMessageReference instead"
argument_list|)
throw|;
block|}
specifier|public
name|boolean
name|addMessageReference
parameter_list|(
specifier|final
name|ConnectionContext
name|context
parameter_list|,
specifier|final
name|MessageId
name|messageId
parameter_list|,
specifier|final
name|ReferenceData
name|data
parameter_list|)
block|{
name|lock
operator|.
name|lock
argument_list|()
expr_stmt|;
try|try
block|{
specifier|final
name|ReferenceRecord
name|record
init|=
operator|new
name|ReferenceRecord
argument_list|(
name|messageId
operator|.
name|toString
argument_list|()
argument_list|,
name|data
argument_list|)
decl_stmt|;
specifier|final
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
name|StoreEntry
name|messageEntry
init|=
name|messageContainer
operator|.
name|place
argument_list|(
name|messageId
argument_list|,
name|record
argument_list|)
decl_stmt|;
name|addInterest
argument_list|(
name|record
argument_list|)
expr_stmt|;
specifier|final
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
specifier|final
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
specifier|final
name|Iterator
argument_list|<
name|TopicSubContainer
argument_list|>
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
specifier|final
name|TopicSubContainer
name|container
init|=
name|i
operator|.
name|next
argument_list|()
decl_stmt|;
specifier|final
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
name|ref
operator|.
name|setMessageId
argument_list|(
name|messageId
argument_list|)
expr_stmt|;
name|container
operator|.
name|add
argument_list|(
name|ref
argument_list|)
expr_stmt|;
block|}
block|}
block|}
finally|finally
block|{
name|lock
operator|.
name|unlock
argument_list|()
expr_stmt|;
block|}
return|return
literal|true
return|;
block|}
specifier|public
name|ReferenceData
name|getMessageReference
parameter_list|(
specifier|final
name|MessageId
name|identity
parameter_list|)
throws|throws
name|IOException
block|{
specifier|final
name|ReferenceRecord
name|result
init|=
name|messageContainer
operator|.
name|get
argument_list|(
name|identity
argument_list|)
decl_stmt|;
if|if
condition|(
name|result
operator|==
literal|null
condition|)
block|{
return|return
literal|null
return|;
block|}
return|return
name|result
operator|.
name|getData
argument_list|()
return|;
block|}
specifier|public
name|void
name|addReferenceFileIdsInUse
parameter_list|()
block|{
for|for
control|(
name|StoreEntry
name|entry
init|=
name|ackContainer
operator|.
name|getFirst
argument_list|()
init|;
name|entry
operator|!=
literal|null
condition|;
name|entry
operator|=
name|ackContainer
operator|.
name|getNext
argument_list|(
name|entry
argument_list|)
control|)
block|{
name|TopicSubAck
name|subAck
init|=
name|ackContainer
operator|.
name|get
argument_list|(
name|entry
argument_list|)
decl_stmt|;
if|if
condition|(
name|subAck
operator|.
name|getCount
argument_list|()
operator|>
literal|0
condition|)
block|{
name|ReferenceRecord
name|rr
init|=
name|messageContainer
operator|.
name|getValue
argument_list|(
name|subAck
operator|.
name|getMessageEntry
argument_list|()
argument_list|)
decl_stmt|;
name|addInterest
argument_list|(
name|rr
argument_list|)
expr_stmt|;
block|}
block|}
block|}
specifier|protected
name|MapContainer
name|addSubscriberMessageContainer
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
name|containerName
init|=
name|getSubscriptionContainerName
argument_list|(
name|getSubscriptionKey
argument_list|(
name|clientId
argument_list|,
name|subscriptionName
argument_list|)
argument_list|)
decl_stmt|;
name|MapContainer
name|container
init|=
name|store
operator|.
name|getMapContainer
argument_list|(
name|containerName
argument_list|,
name|containerName
argument_list|)
decl_stmt|;
name|container
operator|.
name|setKeyMarshaller
argument_list|(
name|Store
operator|.
name|MESSAGEID_MARSHALLER
argument_list|)
expr_stmt|;
name|Marshaller
name|marshaller
init|=
operator|new
name|ConsumerMessageRefMarshaller
argument_list|()
decl_stmt|;
name|container
operator|.
name|setValueMarshaller
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
name|getSubscriptionKey
argument_list|(
name|clientId
argument_list|,
name|subscriptionName
argument_list|)
argument_list|,
name|tsc
argument_list|)
expr_stmt|;
return|return
name|container
return|;
block|}
specifier|public
name|boolean
name|acknowledgeReference
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
name|boolean
name|removeMessage
init|=
literal|false
decl_stmt|;
name|lock
operator|.
name|lock
argument_list|()
expr_stmt|;
try|try
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
name|ConsumerMessageRef
name|ref
init|=
literal|null
decl_stmt|;
if|if
condition|(
operator|(
name|ref
operator|=
name|container
operator|.
name|remove
argument_list|(
name|messageId
argument_list|)
operator|)
operator|!=
literal|null
condition|)
block|{
name|StoreEntry
name|entry
init|=
name|ref
operator|.
name|getAckEntry
argument_list|()
decl_stmt|;
comment|//ensure we get up to-date pointers
name|entry
operator|=
name|ackContainer
operator|.
name|refresh
argument_list|(
name|entry
argument_list|)
expr_stmt|;
name|TopicSubAck
name|tsa
init|=
name|ackContainer
operator|.
name|get
argument_list|(
name|entry
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
name|entry
argument_list|)
expr_stmt|;
name|ReferenceRecord
name|rr
init|=
name|messageContainer
operator|.
name|get
argument_list|(
name|messageId
argument_list|)
decl_stmt|;
if|if
condition|(
name|rr
operator|!=
literal|null
condition|)
block|{
name|entry
operator|=
name|tsa
operator|.
name|getMessageEntry
argument_list|()
expr_stmt|;
name|entry
operator|=
name|messageContainer
operator|.
name|refresh
argument_list|(
name|entry
argument_list|)
expr_stmt|;
name|messageContainer
operator|.
name|remove
argument_list|(
name|entry
argument_list|)
expr_stmt|;
name|removeInterest
argument_list|(
name|rr
argument_list|)
expr_stmt|;
name|removeMessage
operator|=
literal|true
expr_stmt|;
block|}
block|}
else|else
block|{
name|ackContainer
operator|.
name|update
argument_list|(
name|entry
argument_list|,
name|tsa
argument_list|)
expr_stmt|;
block|}
block|}
block|}
else|else
block|{
if|if
condition|(
name|ackContainer
operator|.
name|isEmpty
argument_list|()
operator|||
name|isUnreferencedBySubscribers
argument_list|(
name|subscriberMessages
argument_list|,
name|messageId
argument_list|)
condition|)
block|{
comment|// no message reference held
name|removeMessage
operator|=
literal|true
expr_stmt|;
block|}
block|}
block|}
block|}
finally|finally
block|{
name|lock
operator|.
name|unlock
argument_list|()
expr_stmt|;
block|}
return|return
name|removeMessage
return|;
block|}
comment|// verify that no subscriber has a reference to this message. In the case where the subscribers
comment|// references are persisted but more than the persisted consumers get the message, the ack from the non
comment|// persisted consumer would remove the message in error
comment|//
comment|// see: https://issues.apache.org/activemq/browse/AMQ-2123
specifier|private
name|boolean
name|isUnreferencedBySubscribers
parameter_list|(
name|Map
argument_list|<
name|String
argument_list|,
name|TopicSubContainer
argument_list|>
name|subscriberContainers
parameter_list|,
name|MessageId
name|messageId
parameter_list|)
block|{
name|boolean
name|isUnreferenced
init|=
literal|true
decl_stmt|;
for|for
control|(
name|TopicSubContainer
name|container
range|:
name|subscriberContainers
operator|.
name|values
argument_list|()
control|)
block|{
if|if
condition|(
operator|!
name|container
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
for|for
control|(
name|Iterator
name|i
init|=
name|container
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
name|messageId
operator|.
name|equals
argument_list|(
name|ref
operator|.
name|getMessageId
argument_list|()
argument_list|)
condition|)
block|{
name|isUnreferenced
operator|=
literal|false
expr_stmt|;
break|break;
block|}
block|}
block|}
block|}
return|return
name|isUnreferenced
return|;
block|}
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
parameter_list|)
throws|throws
name|IOException
block|{
name|acknowledgeReference
argument_list|(
name|context
argument_list|,
name|clientId
argument_list|,
name|subscriptionName
argument_list|,
name|messageId
argument_list|)
expr_stmt|;
block|}
specifier|public
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
name|String
name|key
init|=
name|getSubscriptionKey
argument_list|(
name|info
operator|.
name|getClientId
argument_list|()
argument_list|,
name|info
operator|.
name|getSubscriptionName
argument_list|()
argument_list|)
decl_stmt|;
name|lock
operator|.
name|lock
argument_list|()
expr_stmt|;
try|try
block|{
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
name|adapter
operator|.
name|addSubscriberState
argument_list|(
name|info
argument_list|)
expr_stmt|;
block|}
comment|// add the subscriber
name|addSubscriberMessageContainer
argument_list|(
name|info
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
if|if
condition|(
name|retroactive
condition|)
block|{
comment|/*                  * for(StoreEntry                  * entry=ackContainer.getFirst();entry!=null;entry=ackContainer.getNext(entry)){                  * TopicSubAck tsa=(TopicSubAck)ackContainer.get(entry);                  * ConsumerMessageRef ref=new ConsumerMessageRef();                  * ref.setAckEntry(entry);                  * ref.setMessageEntry(tsa.getMessageEntry()); container.add(ref); }                  */
block|}
block|}
finally|finally
block|{
name|lock
operator|.
name|unlock
argument_list|()
expr_stmt|;
block|}
block|}
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
name|lock
operator|.
name|lock
argument_list|()
expr_stmt|;
try|try
block|{
name|SubscriptionInfo
name|info
init|=
name|lookupSubscription
argument_list|(
name|clientId
argument_list|,
name|subscriptionName
argument_list|)
decl_stmt|;
if|if
condition|(
name|info
operator|!=
literal|null
condition|)
block|{
name|adapter
operator|.
name|removeSubscriberState
argument_list|(
name|info
argument_list|)
expr_stmt|;
block|}
name|removeSubscriberMessageContainer
argument_list|(
name|clientId
argument_list|,
name|subscriptionName
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|lock
operator|.
name|unlock
argument_list|()
expr_stmt|;
block|}
block|}
specifier|public
name|SubscriptionInfo
index|[]
name|getAllSubscriptions
parameter_list|()
throws|throws
name|IOException
block|{
name|SubscriptionInfo
index|[]
name|result
init|=
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
decl_stmt|;
return|return
name|result
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
name|TopicSubContainer
name|container
init|=
name|subscriberMessages
operator|.
name|get
argument_list|(
name|key
argument_list|)
decl_stmt|;
return|return
name|container
operator|!=
literal|null
condition|?
name|container
operator|.
name|size
argument_list|()
else|:
literal|0
return|;
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
name|lock
operator|.
name|lock
argument_list|()
expr_stmt|;
try|try
block|{
name|TopicSubContainer
name|container
init|=
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
name|getEntry
argument_list|()
expr_stmt|;
block|}
else|else
block|{
name|entry
operator|=
name|container
operator|.
name|refreshEntry
argument_list|(
name|entry
argument_list|)
expr_stmt|;
if|if
condition|(
name|entry
operator|!=
literal|null
condition|)
block|{
name|entry
operator|=
name|container
operator|.
name|getNextEntry
argument_list|(
name|entry
argument_list|)
expr_stmt|;
block|}
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
name|container
operator|.
name|get
argument_list|(
name|entry
argument_list|)
decl_stmt|;
name|ReferenceRecord
name|msg
init|=
name|messageContainer
operator|.
name|getValue
argument_list|(
name|consumerRef
operator|.
name|getMessageEntry
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
name|recoverReference
argument_list|(
name|listener
argument_list|,
name|msg
argument_list|)
condition|)
block|{
name|count
operator|++
expr_stmt|;
name|container
operator|.
name|setBatchEntry
argument_list|(
name|msg
operator|.
name|getMessageId
argument_list|()
argument_list|,
name|entry
argument_list|)
expr_stmt|;
block|}
else|else
block|{
break|break;
block|}
block|}
else|else
block|{
name|container
operator|.
name|reset
argument_list|()
expr_stmt|;
block|}
name|entry
operator|=
name|container
operator|.
name|getNextEntry
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
block|}
finally|finally
block|{
name|lock
operator|.
name|unlock
argument_list|()
expr_stmt|;
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
name|ReferenceRecord
name|msg
init|=
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
name|msg
operator|!=
literal|null
condition|)
block|{
if|if
condition|(
operator|!
name|recoverReference
argument_list|(
name|listener
argument_list|,
name|msg
argument_list|)
condition|)
block|{
break|break;
block|}
block|}
block|}
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
name|lock
operator|.
name|lock
argument_list|()
expr_stmt|;
try|try
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
finally|finally
block|{
name|lock
operator|.
name|unlock
argument_list|()
expr_stmt|;
block|}
block|}
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
name|lock
operator|.
name|lock
argument_list|()
expr_stmt|;
try|try
block|{
name|Set
argument_list|<
name|String
argument_list|>
name|tmpSet
init|=
operator|new
name|HashSet
argument_list|<
name|String
argument_list|>
argument_list|(
name|subscriberContainer
operator|.
name|keySet
argument_list|()
argument_list|)
decl_stmt|;
for|for
control|(
name|String
name|key
range|:
name|tmpSet
control|)
block|{
name|TopicSubContainer
name|container
init|=
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
name|container
operator|.
name|clear
argument_list|()
expr_stmt|;
block|}
block|}
name|ackContainer
operator|.
name|clear
argument_list|()
expr_stmt|;
block|}
finally|finally
block|{
name|lock
operator|.
name|unlock
argument_list|()
expr_stmt|;
block|}
name|super
operator|.
name|removeAllMessages
argument_list|(
name|context
argument_list|)
expr_stmt|;
block|}
specifier|protected
name|void
name|removeSubscriberMessageContainer
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
name|subscriberKey
init|=
name|getSubscriptionKey
argument_list|(
name|clientId
argument_list|,
name|subscriptionName
argument_list|)
decl_stmt|;
name|String
name|containerName
init|=
name|getSubscriptionContainerName
argument_list|(
name|subscriberKey
argument_list|)
decl_stmt|;
name|subscriberContainer
operator|.
name|remove
argument_list|(
name|subscriberKey
argument_list|)
expr_stmt|;
name|TopicSubContainer
name|container
init|=
name|subscriberMessages
operator|.
name|remove
argument_list|(
name|subscriberKey
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
name|store
operator|.
name|deleteMapContainer
argument_list|(
name|containerName
argument_list|,
name|containerName
argument_list|)
expr_stmt|;
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
name|StringBuffer
name|buffer
init|=
operator|new
name|StringBuffer
argument_list|()
decl_stmt|;
name|buffer
operator|.
name|append
argument_list|(
name|clientId
argument_list|)
operator|.
name|append
argument_list|(
literal|":"
argument_list|)
expr_stmt|;
name|String
name|name
init|=
name|subscriberName
operator|!=
literal|null
condition|?
name|subscriberName
else|:
literal|"NOT_SET"
decl_stmt|;
return|return
name|buffer
operator|.
name|append
argument_list|(
name|name
argument_list|)
operator|.
name|toString
argument_list|()
return|;
block|}
specifier|private
name|String
name|getSubscriptionContainerName
parameter_list|(
name|String
name|subscriptionKey
parameter_list|)
block|{
name|StringBuffer
name|result
init|=
operator|new
name|StringBuffer
argument_list|(
name|TOPIC_SUB_NAME
argument_list|)
decl_stmt|;
name|result
operator|.
name|append
argument_list|(
name|destination
operator|.
name|getQualifiedName
argument_list|()
argument_list|)
expr_stmt|;
name|result
operator|.
name|append
argument_list|(
name|subscriptionKey
argument_list|)
expr_stmt|;
return|return
name|result
operator|.
name|toString
argument_list|()
return|;
block|}
block|}
end_class

end_unit

