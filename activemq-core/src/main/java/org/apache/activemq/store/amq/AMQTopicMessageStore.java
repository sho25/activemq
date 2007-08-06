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
name|amq
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
name|io
operator|.
name|InterruptedIOException
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
name|impl
operator|.
name|async
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
name|TopicReferenceStore
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
name|Callback
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

begin_comment
comment|/**  * A MessageStore that uses a Journal to store it's messages.  *   * @version $Revision: 1.13 $  */
end_comment

begin_class
specifier|public
class|class
name|AMQTopicMessageStore
extends|extends
name|AMQMessageStore
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
name|AMQTopicMessageStore
operator|.
name|class
argument_list|)
decl_stmt|;
specifier|private
name|TopicReferenceStore
name|topicReferenceStore
decl_stmt|;
specifier|private
name|HashMap
argument_list|<
name|SubscriptionKey
argument_list|,
name|MessageId
argument_list|>
name|ackedLastAckLocations
init|=
operator|new
name|HashMap
argument_list|<
name|SubscriptionKey
argument_list|,
name|MessageId
argument_list|>
argument_list|()
decl_stmt|;
specifier|public
name|AMQTopicMessageStore
parameter_list|(
name|AMQPersistenceAdapter
name|adapter
parameter_list|,
name|TopicReferenceStore
name|topicReferenceStore
parameter_list|,
name|ActiveMQTopic
name|destinationName
parameter_list|)
block|{
name|super
argument_list|(
name|adapter
argument_list|,
name|topicReferenceStore
argument_list|,
name|destinationName
argument_list|)
expr_stmt|;
name|this
operator|.
name|topicReferenceStore
operator|=
name|topicReferenceStore
expr_stmt|;
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
name|flush
argument_list|()
expr_stmt|;
name|topicReferenceStore
operator|.
name|recoverSubscription
argument_list|(
name|clientId
argument_list|,
name|subscriptionName
argument_list|,
operator|new
name|RecoveryListenerAdapter
argument_list|(
name|this
argument_list|,
name|listener
argument_list|)
argument_list|)
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
specifier|final
name|MessageRecoveryListener
name|listener
parameter_list|)
throws|throws
name|Exception
block|{
name|RecoveryListenerAdapter
name|recoveryListener
init|=
operator|new
name|RecoveryListenerAdapter
argument_list|(
name|this
argument_list|,
name|listener
argument_list|)
decl_stmt|;
name|topicReferenceStore
operator|.
name|recoverNextMessages
argument_list|(
name|clientId
argument_list|,
name|subscriptionName
argument_list|,
name|maxReturned
argument_list|,
name|recoveryListener
argument_list|)
expr_stmt|;
if|if
condition|(
name|recoveryListener
operator|.
name|size
argument_list|()
operator|==
literal|0
condition|)
block|{
name|flush
argument_list|()
expr_stmt|;
name|topicReferenceStore
operator|.
name|recoverNextMessages
argument_list|(
name|clientId
argument_list|,
name|subscriptionName
argument_list|,
name|maxReturned
argument_list|,
name|recoveryListener
argument_list|)
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
name|topicReferenceStore
operator|.
name|lookupSubscription
argument_list|(
name|clientId
argument_list|,
name|subscriptionName
argument_list|)
return|;
block|}
specifier|public
name|void
name|addSubsciption
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
name|topicReferenceStore
operator|.
name|addSubsciption
argument_list|(
name|subscriptionInfo
argument_list|,
name|retroactive
argument_list|)
expr_stmt|;
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
name|Location
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
name|AMQTopicMessageStore
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
name|AMQTopicMessageStore
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
name|boolean
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
name|SubscriptionInfo
name|sub
init|=
name|topicReferenceStore
operator|.
name|lookupSubscription
argument_list|(
name|clientId
argument_list|,
name|subscritionName
argument_list|)
decl_stmt|;
if|if
condition|(
name|sub
operator|!=
literal|null
condition|)
block|{
name|topicReferenceStore
operator|.
name|acknowledge
argument_list|(
name|context
argument_list|,
name|clientId
argument_list|,
name|subscritionName
argument_list|,
name|messageId
argument_list|)
expr_stmt|;
return|return
literal|true
return|;
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
return|return
literal|false
return|;
block|}
comment|/**      * @param messageId      * @param location      * @param key      * @throws InterruptedIOException      */
specifier|protected
name|void
name|acknowledge
parameter_list|(
name|MessageId
name|messageId
parameter_list|,
name|Location
name|location
parameter_list|,
name|SubscriptionKey
name|key
parameter_list|)
throws|throws
name|InterruptedIOException
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
block|}
try|try
block|{
name|asyncWriteTask
operator|.
name|wakeup
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|InterruptedException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|InterruptedIOException
argument_list|()
throw|;
block|}
block|}
annotation|@
name|Override
specifier|protected
name|Location
name|doAsyncWrite
parameter_list|()
throws|throws
name|IOException
block|{
specifier|final
name|HashMap
argument_list|<
name|SubscriptionKey
argument_list|,
name|MessageId
argument_list|>
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
name|this
operator|.
name|ackedLastAckLocations
expr_stmt|;
name|this
operator|.
name|ackedLastAckLocations
operator|=
operator|new
name|HashMap
argument_list|<
name|SubscriptionKey
argument_list|,
name|MessageId
argument_list|>
argument_list|()
expr_stmt|;
block|}
name|Location
name|location
init|=
name|super
operator|.
name|doAsyncWrite
argument_list|()
decl_stmt|;
if|if
condition|(
name|cpAckedLastAckLocations
operator|!=
literal|null
condition|)
block|{
name|transactionTemplate
operator|.
name|run
argument_list|(
operator|new
name|Callback
argument_list|()
block|{
specifier|public
name|void
name|execute
parameter_list|()
throws|throws
name|Exception
block|{
comment|// Checkpoint the acknowledged messages.
name|Iterator
argument_list|<
name|SubscriptionKey
argument_list|>
name|iterator
init|=
name|cpAckedLastAckLocations
operator|.
name|keySet
argument_list|()
operator|.
name|iterator
argument_list|()
decl_stmt|;
while|while
condition|(
name|iterator
operator|.
name|hasNext
argument_list|()
condition|)
block|{
name|SubscriptionKey
name|subscriptionKey
init|=
name|iterator
operator|.
name|next
argument_list|()
decl_stmt|;
name|MessageId
name|identity
init|=
name|cpAckedLastAckLocations
operator|.
name|get
argument_list|(
name|subscriptionKey
argument_list|)
decl_stmt|;
name|topicReferenceStore
operator|.
name|acknowledge
argument_list|(
name|transactionTemplate
operator|.
name|getContext
argument_list|()
argument_list|,
name|subscriptionKey
operator|.
name|clientId
argument_list|,
name|subscriptionKey
operator|.
name|subscriptionName
argument_list|,
name|identity
argument_list|)
expr_stmt|;
block|}
block|}
block|}
argument_list|)
expr_stmt|;
block|}
return|return
name|location
return|;
block|}
comment|/**      * @return Returns the longTermStore.      */
specifier|public
name|TopicReferenceStore
name|getTopicReferenceStore
parameter_list|()
block|{
return|return
name|topicReferenceStore
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
name|topicReferenceStore
operator|.
name|deleteSubscription
argument_list|(
name|clientId
argument_list|,
name|subscriptionName
argument_list|)
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
name|topicReferenceStore
operator|.
name|getAllSubscriptions
argument_list|()
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
name|flush
argument_list|()
expr_stmt|;
return|return
name|topicReferenceStore
operator|.
name|getMessageCount
argument_list|(
name|clientId
argument_list|,
name|subscriberName
argument_list|)
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
parameter_list|)
block|{
name|topicReferenceStore
operator|.
name|resetBatching
argument_list|(
name|clientId
argument_list|,
name|subscriptionName
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

