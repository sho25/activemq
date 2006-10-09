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
name|ArrayList
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
name|org
operator|.
name|apache
operator|.
name|activeio
operator|.
name|journal
operator|.
name|RecordLocation
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
name|JournalTopicAck
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
name|transaction
operator|.
name|Synchronization
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
specifier|static
specifier|final
name|Log
name|log
init|=
name|LogFactory
operator|.
name|getLog
argument_list|(
name|RapidTopicMessageStore
operator|.
name|class
argument_list|)
decl_stmt|;
specifier|private
name|HashMap
name|ackedLastAckLocations
init|=
operator|new
name|HashMap
argument_list|()
decl_stmt|;
specifier|private
specifier|final
name|MapContainer
name|subscriberContainer
decl_stmt|;
specifier|private
specifier|final
name|MapContainer
name|ackContainer
decl_stmt|;
specifier|private
specifier|final
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
name|RapidTopicMessageStore
parameter_list|(
name|RapidPersistenceAdapter
name|adapter
parameter_list|,
name|ActiveMQTopic
name|destination
parameter_list|,
name|MapContainer
name|messageContainer
parameter_list|,
name|MapContainer
name|subsContainer
parameter_list|,
name|MapContainer
name|ackContainer
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
argument_list|)
expr_stmt|;
name|this
operator|.
name|subscriberContainer
operator|=
name|subsContainer
expr_stmt|;
name|this
operator|.
name|ackContainer
operator|=
name|ackContainer
expr_stmt|;
name|this
operator|.
name|store
operator|=
name|adapter
operator|.
name|getStore
argument_list|()
expr_stmt|;
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
name|void
name|recoverSubscription
parameter_list|(
name|String
name|clientId
parameter_list|,
name|String
name|subscriptionName
parameter_list|,
specifier|final
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
name|Object
name|msg
init|=
name|messageContainer
operator|.
name|get
argument_list|(
name|i
operator|.
name|next
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
name|Object
name|msg
init|=
name|messageContainer
operator|.
name|get
argument_list|(
name|i
operator|.
name|next
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
operator|!
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
name|lastMessageId
operator|==
literal|null
condition|)
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
elseif|else
if|if
condition|(
operator|!
name|startFound
operator|&&
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
name|String
name|id
init|=
name|message
operator|.
name|getMessageId
argument_list|()
operator|.
name|toString
argument_list|()
decl_stmt|;
name|ackContainer
operator|.
name|put
argument_list|(
name|id
argument_list|,
operator|new
name|AtomicInteger
argument_list|(
name|subscriberCount
argument_list|)
argument_list|)
expr_stmt|;
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
name|id
argument_list|)
expr_stmt|;
block|}
name|super
operator|.
name|addMessage
argument_list|(
name|context
argument_list|,
name|message
argument_list|)
expr_stmt|;
block|}
block|}
comment|/**      */
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
specifier|final
name|MessageId
name|messageId
parameter_list|)
throws|throws
name|IOException
block|{
specifier|final
name|boolean
name|debug
init|=
name|log
operator|.
name|isDebugEnabled
argument_list|()
decl_stmt|;
name|JournalTopicAck
name|ack
init|=
operator|new
name|JournalTopicAck
argument_list|()
decl_stmt|;
name|ack
operator|.
name|setDestination
argument_list|(
name|destination
argument_list|)
expr_stmt|;
name|ack
operator|.
name|setMessageId
argument_list|(
name|messageId
argument_list|)
expr_stmt|;
name|ack
operator|.
name|setMessageSequenceId
argument_list|(
name|messageId
operator|.
name|getBrokerSequenceId
argument_list|()
argument_list|)
expr_stmt|;
name|ack
operator|.
name|setSubscritionName
argument_list|(
name|subscriptionName
argument_list|)
expr_stmt|;
name|ack
operator|.
name|setClientId
argument_list|(
name|clientId
argument_list|)
expr_stmt|;
name|ack
operator|.
name|setTransactionId
argument_list|(
name|context
operator|.
name|getTransaction
argument_list|()
operator|!=
literal|null
condition|?
name|context
operator|.
name|getTransaction
argument_list|()
operator|.
name|getTransactionId
argument_list|()
else|:
literal|null
argument_list|)
expr_stmt|;
specifier|final
name|RecordLocation
name|location
init|=
name|peristenceAdapter
operator|.
name|writeCommand
argument_list|(
name|ack
argument_list|,
literal|false
argument_list|)
decl_stmt|;
specifier|final
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
if|if
condition|(
operator|!
name|context
operator|.
name|isInTransaction
argument_list|()
condition|)
block|{
if|if
condition|(
name|debug
condition|)
name|log
operator|.
name|debug
argument_list|(
literal|"Journalled acknowledge for: "
operator|+
name|messageId
operator|+
literal|", at: "
operator|+
name|location
argument_list|)
expr_stmt|;
name|acknowledge
argument_list|(
name|messageId
argument_list|,
name|location
argument_list|,
name|key
argument_list|)
expr_stmt|;
block|}
else|else
block|{
if|if
condition|(
name|debug
condition|)
name|log
operator|.
name|debug
argument_list|(
literal|"Journalled transacted acknowledge for: "
operator|+
name|messageId
operator|+
literal|", at: "
operator|+
name|location
argument_list|)
expr_stmt|;
synchronized|synchronized
init|(
name|this
init|)
block|{
name|inFlightTxLocations
operator|.
name|add
argument_list|(
name|location
argument_list|)
expr_stmt|;
block|}
name|transactionStore
operator|.
name|acknowledge
argument_list|(
name|this
argument_list|,
name|ack
argument_list|,
name|location
argument_list|)
expr_stmt|;
name|context
operator|.
name|getTransaction
argument_list|()
operator|.
name|addSynchronization
argument_list|(
operator|new
name|Synchronization
argument_list|()
block|{
specifier|public
name|void
name|afterCommit
parameter_list|()
throws|throws
name|Exception
block|{
if|if
condition|(
name|debug
condition|)
name|log
operator|.
name|debug
argument_list|(
literal|"Transacted acknowledge commit for: "
operator|+
name|messageId
operator|+
literal|", at: "
operator|+
name|location
argument_list|)
expr_stmt|;
synchronized|synchronized
init|(
name|RapidTopicMessageStore
operator|.
name|this
init|)
block|{
name|inFlightTxLocations
operator|.
name|remove
argument_list|(
name|location
argument_list|)
expr_stmt|;
name|acknowledge
argument_list|(
name|messageId
argument_list|,
name|location
argument_list|,
name|key
argument_list|)
expr_stmt|;
block|}
block|}
specifier|public
name|void
name|afterRollback
parameter_list|()
throws|throws
name|Exception
block|{
if|if
condition|(
name|debug
condition|)
name|log
operator|.
name|debug
argument_list|(
literal|"Transacted acknowledge rollback for: "
operator|+
name|messageId
operator|+
literal|", at: "
operator|+
name|location
argument_list|)
expr_stmt|;
synchronized|synchronized
init|(
name|RapidTopicMessageStore
operator|.
name|this
init|)
block|{
name|inFlightTxLocations
operator|.
name|remove
argument_list|(
name|location
argument_list|)
expr_stmt|;
block|}
block|}
block|}
argument_list|)
expr_stmt|;
block|}
block|}
specifier|public
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
name|subscritionName
parameter_list|,
name|MessageId
name|messageId
parameter_list|)
block|{
try|try
block|{
synchronized|synchronized
init|(
name|this
init|)
block|{
name|String
name|subcriberId
init|=
name|getSubscriptionKey
argument_list|(
name|clientId
argument_list|,
name|subscritionName
argument_list|)
decl_stmt|;
name|String
name|id
init|=
name|messageId
operator|.
name|toString
argument_list|()
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
comment|// container.remove(id);
name|container
operator|.
name|removeFirst
argument_list|()
expr_stmt|;
name|AtomicInteger
name|count
init|=
operator|(
name|AtomicInteger
operator|)
name|ackContainer
operator|.
name|remove
argument_list|(
name|id
argument_list|)
decl_stmt|;
if|if
condition|(
name|count
operator|!=
literal|null
condition|)
block|{
if|if
condition|(
name|count
operator|.
name|decrementAndGet
argument_list|()
operator|>
literal|0
condition|)
block|{
name|ackContainer
operator|.
name|put
argument_list|(
name|id
argument_list|,
name|count
argument_list|)
expr_stmt|;
block|}
else|else
block|{
comment|// no more references to message messageContainer so remove it
name|messageContainer
operator|.
name|remove
argument_list|(
name|messageId
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|}
block|}
catch|catch
parameter_list|(
name|Throwable
name|e
parameter_list|)
block|{
name|log
operator|.
name|debug
argument_list|(
literal|"Could not replay acknowledge for message '"
operator|+
name|messageId
operator|+
literal|"'.  Message may have already been acknowledged. reason: "
operator|+
name|e
argument_list|)
expr_stmt|;
block|}
block|}
comment|/**      * @param messageId      * @param location      * @param key      */
specifier|private
name|void
name|acknowledge
parameter_list|(
name|MessageId
name|messageId
parameter_list|,
name|RecordLocation
name|location
parameter_list|,
name|SubscriptionKey
name|key
parameter_list|)
block|{
synchronized|synchronized
init|(
name|this
init|)
block|{
name|lastLocation
operator|=
name|location
expr_stmt|;
name|ackedLastAckLocations
operator|.
name|put
argument_list|(
name|key
argument_list|,
name|messageId
argument_list|)
expr_stmt|;
name|String
name|subcriberId
init|=
name|getSubscriptionKey
argument_list|(
name|key
operator|.
name|getClientId
argument_list|()
argument_list|,
name|key
operator|.
name|getSubscriptionName
argument_list|()
argument_list|)
decl_stmt|;
name|String
name|id
init|=
name|messageId
operator|.
name|toString
argument_list|()
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
comment|// container.remove(id);
name|container
operator|.
name|removeFirst
argument_list|()
expr_stmt|;
name|AtomicInteger
name|count
init|=
operator|(
name|AtomicInteger
operator|)
name|ackContainer
operator|.
name|remove
argument_list|(
name|id
argument_list|)
decl_stmt|;
if|if
condition|(
name|count
operator|!=
literal|null
condition|)
block|{
if|if
condition|(
name|count
operator|.
name|decrementAndGet
argument_list|()
operator|>
literal|0
condition|)
block|{
name|ackContainer
operator|.
name|put
argument_list|(
name|id
argument_list|,
name|count
argument_list|)
expr_stmt|;
block|}
else|else
block|{
comment|// no more references to message messageContainer so remove it
name|messageContainer
operator|.
name|remove
argument_list|(
name|messageId
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|}
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
specifier|public
name|RecordLocation
name|checkpoint
parameter_list|()
throws|throws
name|IOException
block|{
name|ArrayList
name|cpAckedLastAckLocations
decl_stmt|;
comment|// swap out the hash maps..
synchronized|synchronized
init|(
name|this
init|)
block|{
name|cpAckedLastAckLocations
operator|=
operator|new
name|ArrayList
argument_list|(
name|this
operator|.
name|ackedLastAckLocations
operator|.
name|values
argument_list|()
argument_list|)
expr_stmt|;
name|this
operator|.
name|ackedLastAckLocations
operator|=
operator|new
name|HashMap
argument_list|()
expr_stmt|;
block|}
name|RecordLocation
name|rc
init|=
name|super
operator|.
name|checkpoint
argument_list|()
decl_stmt|;
if|if
condition|(
operator|!
name|cpAckedLastAckLocations
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
name|Collections
operator|.
name|sort
argument_list|(
name|cpAckedLastAckLocations
argument_list|)
expr_stmt|;
name|RecordLocation
name|t
init|=
operator|(
name|RecordLocation
operator|)
name|cpAckedLastAckLocations
operator|.
name|get
argument_list|(
literal|0
argument_list|)
decl_stmt|;
if|if
condition|(
name|rc
operator|==
literal|null
operator|||
name|t
operator|.
name|compareTo
argument_list|(
name|rc
argument_list|)
operator|<
literal|0
condition|)
block|{
name|rc
operator|=
name|t
expr_stmt|;
block|}
block|}
return|return
name|rc
return|;
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
name|String
name|id
init|=
name|i
operator|.
name|next
argument_list|()
operator|.
name|toString
argument_list|()
decl_stmt|;
name|AtomicInteger
name|count
init|=
operator|(
name|AtomicInteger
operator|)
name|ackContainer
operator|.
name|remove
argument_list|(
name|id
argument_list|)
decl_stmt|;
if|if
condition|(
name|count
operator|!=
literal|null
condition|)
block|{
if|if
condition|(
name|count
operator|.
name|decrementAndGet
argument_list|()
operator|>
literal|0
condition|)
block|{
name|ackContainer
operator|.
name|put
argument_list|(
name|id
argument_list|,
name|count
argument_list|)
expr_stmt|;
block|}
else|else
block|{
comment|// no more references to message messageContainer so remove it
name|messageContainer
operator|.
name|remove
argument_list|(
name|id
argument_list|)
expr_stmt|;
block|}
block|}
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
literal|"durable-subs"
argument_list|)
decl_stmt|;
name|Marshaller
name|marshaller
init|=
operator|new
name|StringMarshaller
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
name|MessageId
name|getNextMessageIdToDeliver
parameter_list|(
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
name|MessageId
name|result
init|=
literal|null
decl_stmt|;
name|boolean
name|getNext
init|=
literal|false
decl_stmt|;
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
name|String
name|id
init|=
name|i
operator|.
name|next
argument_list|()
operator|.
name|toString
argument_list|()
decl_stmt|;
if|if
condition|(
name|id
operator|.
name|equals
argument_list|(
name|messageId
operator|.
name|toString
argument_list|()
argument_list|)
condition|)
block|{
name|getNext
operator|=
literal|true
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|getNext
condition|)
block|{
name|result
operator|=
operator|new
name|MessageId
argument_list|(
name|id
argument_list|)
expr_stmt|;
break|break;
block|}
block|}
return|return
name|result
return|;
block|}
specifier|public
name|MessageId
name|getPreviousMessageIdToDeliver
parameter_list|(
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
name|MessageId
name|result
init|=
literal|null
decl_stmt|;
name|String
name|previousId
init|=
literal|null
decl_stmt|;
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
name|String
name|id
init|=
name|i
operator|.
name|next
argument_list|()
operator|.
name|toString
argument_list|()
decl_stmt|;
if|if
condition|(
name|id
operator|.
name|equals
argument_list|(
name|messageId
operator|.
name|toString
argument_list|()
argument_list|)
condition|)
block|{
if|if
condition|(
name|previousId
operator|!=
literal|null
condition|)
block|{
name|result
operator|=
operator|new
name|MessageId
argument_list|(
name|previousId
argument_list|)
expr_stmt|;
block|}
break|break;
block|}
name|previousId
operator|=
name|id
expr_stmt|;
block|}
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
specifier|public
name|void
name|resetBatching
parameter_list|(
name|String
name|clientId
parameter_list|,
name|String
name|subscriptionName
parameter_list|,
name|MessageId
name|nextId
parameter_list|)
block|{     }
block|}
end_class

end_unit

