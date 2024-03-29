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
name|jdbc
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
name|sql
operator|.
name|SQLException
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Arrays
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
name|LinkedHashMap
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
name|java
operator|.
name|util
operator|.
name|concurrent
operator|.
name|locks
operator|.
name|ReentrantReadWriteLock
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
name|ActiveMQMessageAudit
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
name|MessageStoreSubscriptionStatistics
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
name|ByteSequence
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
name|IOExceptionSupport
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
name|wireformat
operator|.
name|WireFormat
import|;
end_import

begin_import
import|import
name|org
operator|.
name|slf4j
operator|.
name|Logger
import|;
end_import

begin_import
import|import
name|org
operator|.
name|slf4j
operator|.
name|LoggerFactory
import|;
end_import

begin_comment
comment|/**  *  */
end_comment

begin_class
specifier|public
class|class
name|JDBCTopicMessageStore
extends|extends
name|JDBCMessageStore
implements|implements
name|TopicMessageStore
block|{
specifier|private
specifier|static
specifier|final
name|Logger
name|LOG
init|=
name|LoggerFactory
operator|.
name|getLogger
argument_list|(
name|JDBCTopicMessageStore
operator|.
name|class
argument_list|)
decl_stmt|;
specifier|private
name|Map
argument_list|<
name|String
argument_list|,
name|LastRecovered
argument_list|>
name|subscriberLastRecoveredMap
init|=
operator|new
name|ConcurrentHashMap
argument_list|<
name|String
argument_list|,
name|LastRecovered
argument_list|>
argument_list|()
decl_stmt|;
specifier|private
name|Set
argument_list|<
name|String
argument_list|>
name|pendingCompletion
init|=
operator|new
name|HashSet
argument_list|<
name|String
argument_list|>
argument_list|()
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|String
name|PROPERTY_SEQUENCE_ID_CACHE_SIZE
init|=
literal|"org.apache.activemq.store.jdbc.SEQUENCE_ID_CACHE_SIZE"
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|int
name|SEQUENCE_ID_CACHE_SIZE
init|=
name|Integer
operator|.
name|parseInt
argument_list|(
name|System
operator|.
name|getProperty
argument_list|(
name|PROPERTY_SEQUENCE_ID_CACHE_SIZE
argument_list|,
literal|"1000"
argument_list|)
argument_list|,
literal|10
argument_list|)
decl_stmt|;
specifier|private
specifier|final
name|ReentrantReadWriteLock
name|sequenceIdCacheSizeLock
init|=
operator|new
name|ReentrantReadWriteLock
argument_list|()
decl_stmt|;
specifier|private
name|Map
argument_list|<
name|MessageId
argument_list|,
name|long
index|[]
argument_list|>
name|sequenceIdCache
init|=
operator|new
name|LinkedHashMap
argument_list|<
name|MessageId
argument_list|,
name|long
index|[]
argument_list|>
argument_list|()
block|{
annotation|@
name|Override
specifier|protected
name|boolean
name|removeEldestEntry
parameter_list|(
name|Map
operator|.
name|Entry
argument_list|<
name|MessageId
argument_list|,
name|long
index|[]
argument_list|>
name|eldest
parameter_list|)
block|{
return|return
name|size
argument_list|()
operator|>
name|SEQUENCE_ID_CACHE_SIZE
return|;
block|}
block|}
decl_stmt|;
specifier|public
name|JDBCTopicMessageStore
parameter_list|(
name|JDBCPersistenceAdapter
name|persistenceAdapter
parameter_list|,
name|JDBCAdapter
name|adapter
parameter_list|,
name|WireFormat
name|wireFormat
parameter_list|,
name|ActiveMQTopic
name|topic
parameter_list|,
name|ActiveMQMessageAudit
name|audit
parameter_list|)
throws|throws
name|IOException
block|{
name|super
argument_list|(
name|persistenceAdapter
argument_list|,
name|adapter
argument_list|,
name|wireFormat
argument_list|,
name|topic
argument_list|,
name|audit
argument_list|)
expr_stmt|;
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
if|if
condition|(
name|ack
operator|!=
literal|null
operator|&&
name|ack
operator|.
name|isUnmatchedAck
argument_list|()
condition|)
block|{
if|if
condition|(
name|LOG
operator|.
name|isTraceEnabled
argument_list|()
condition|)
block|{
name|LOG
operator|.
name|trace
argument_list|(
literal|"ignoring unmatched selector ack for: "
operator|+
name|messageId
operator|+
literal|", cleanup will get to this message after subsequent acks."
argument_list|)
expr_stmt|;
block|}
return|return;
block|}
name|TransactionContext
name|c
init|=
name|persistenceAdapter
operator|.
name|getTransactionContext
argument_list|(
name|context
argument_list|)
decl_stmt|;
try|try
block|{
name|long
index|[]
name|res
init|=
name|getCachedStoreSequenceId
argument_list|(
name|c
argument_list|,
name|destination
argument_list|,
name|messageId
argument_list|)
decl_stmt|;
if|if
condition|(
name|this
operator|.
name|isPrioritizedMessages
argument_list|()
condition|)
block|{
name|adapter
operator|.
name|doSetLastAckWithPriority
argument_list|(
name|c
argument_list|,
name|destination
argument_list|,
name|context
operator|!=
literal|null
condition|?
name|context
operator|.
name|getXid
argument_list|()
else|:
literal|null
argument_list|,
name|clientId
argument_list|,
name|subscriptionName
argument_list|,
name|res
index|[
literal|0
index|]
argument_list|,
name|res
index|[
literal|1
index|]
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|adapter
operator|.
name|doSetLastAck
argument_list|(
name|c
argument_list|,
name|destination
argument_list|,
name|context
operator|!=
literal|null
condition|?
name|context
operator|.
name|getXid
argument_list|()
else|:
literal|null
argument_list|,
name|clientId
argument_list|,
name|subscriptionName
argument_list|,
name|res
index|[
literal|0
index|]
argument_list|,
name|res
index|[
literal|1
index|]
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|LOG
operator|.
name|isTraceEnabled
argument_list|()
condition|)
block|{
name|LOG
operator|.
name|trace
argument_list|(
name|clientId
operator|+
literal|":"
operator|+
name|subscriptionName
operator|+
literal|" ack, seq: "
operator|+
name|res
index|[
literal|0
index|]
operator|+
literal|", priority: "
operator|+
name|res
index|[
literal|1
index|]
operator|+
literal|" mid:"
operator|+
name|messageId
argument_list|)
expr_stmt|;
block|}
block|}
catch|catch
parameter_list|(
name|SQLException
name|e
parameter_list|)
block|{
name|JDBCPersistenceAdapter
operator|.
name|log
argument_list|(
literal|"JDBC Failure: "
argument_list|,
name|e
argument_list|)
expr_stmt|;
throw|throw
name|IOExceptionSupport
operator|.
name|create
argument_list|(
literal|"Failed to store acknowledgment for: "
operator|+
name|clientId
operator|+
literal|" on message "
operator|+
name|messageId
operator|+
literal|" in container: "
operator|+
name|e
argument_list|,
name|e
argument_list|)
throw|;
block|}
finally|finally
block|{
name|c
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
specifier|public
name|long
index|[]
name|getCachedStoreSequenceId
parameter_list|(
name|TransactionContext
name|transactionContext
parameter_list|,
name|ActiveMQDestination
name|destination
parameter_list|,
name|MessageId
name|messageId
parameter_list|)
throws|throws
name|SQLException
throws|,
name|IOException
block|{
name|long
index|[]
name|val
init|=
literal|null
decl_stmt|;
name|sequenceIdCacheSizeLock
operator|.
name|readLock
argument_list|()
operator|.
name|lock
argument_list|()
expr_stmt|;
try|try
block|{
name|val
operator|=
name|sequenceIdCache
operator|.
name|get
argument_list|(
name|messageId
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|sequenceIdCacheSizeLock
operator|.
name|readLock
argument_list|()
operator|.
name|unlock
argument_list|()
expr_stmt|;
block|}
if|if
condition|(
name|val
operator|==
literal|null
condition|)
block|{
name|val
operator|=
name|adapter
operator|.
name|getStoreSequenceId
argument_list|(
name|transactionContext
argument_list|,
name|destination
argument_list|,
name|messageId
argument_list|)
expr_stmt|;
block|}
return|return
name|val
return|;
block|}
comment|/**      * @throws Exception      */
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
specifier|final
name|MessageRecoveryListener
name|listener
parameter_list|)
throws|throws
name|Exception
block|{
name|TransactionContext
name|c
init|=
name|persistenceAdapter
operator|.
name|getTransactionContext
argument_list|()
decl_stmt|;
try|try
block|{
name|adapter
operator|.
name|doRecoverSubscription
argument_list|(
name|c
argument_list|,
name|destination
argument_list|,
name|clientId
argument_list|,
name|subscriptionName
argument_list|,
operator|new
name|JDBCMessageRecoveryListener
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|boolean
name|recoverMessage
parameter_list|(
name|long
name|sequenceId
parameter_list|,
name|byte
index|[]
name|data
parameter_list|)
throws|throws
name|Exception
block|{
name|Message
name|msg
init|=
operator|(
name|Message
operator|)
name|wireFormat
operator|.
name|unmarshal
argument_list|(
operator|new
name|ByteSequence
argument_list|(
name|data
argument_list|)
argument_list|)
decl_stmt|;
name|msg
operator|.
name|getMessageId
argument_list|()
operator|.
name|setBrokerSequenceId
argument_list|(
name|sequenceId
argument_list|)
expr_stmt|;
return|return
name|listener
operator|.
name|recoverMessage
argument_list|(
name|msg
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|recoverMessageReference
parameter_list|(
name|String
name|reference
parameter_list|)
throws|throws
name|Exception
block|{
return|return
name|listener
operator|.
name|recoverMessageReference
argument_list|(
operator|new
name|MessageId
argument_list|(
name|reference
argument_list|)
argument_list|)
return|;
block|}
block|}
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|SQLException
name|e
parameter_list|)
block|{
name|JDBCPersistenceAdapter
operator|.
name|log
argument_list|(
literal|"JDBC Failure: "
argument_list|,
name|e
argument_list|)
expr_stmt|;
throw|throw
name|IOExceptionSupport
operator|.
name|create
argument_list|(
literal|"Failed to recover subscription: "
operator|+
name|clientId
operator|+
literal|". Reason: "
operator|+
name|e
argument_list|,
name|e
argument_list|)
throw|;
block|}
finally|finally
block|{
name|c
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
specifier|private
class|class
name|LastRecovered
implements|implements
name|Iterable
argument_list|<
name|LastRecoveredEntry
argument_list|>
block|{
name|LastRecoveredEntry
index|[]
name|perPriority
init|=
operator|new
name|LastRecoveredEntry
index|[
literal|10
index|]
decl_stmt|;
name|LastRecovered
parameter_list|()
block|{
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|perPriority
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|perPriority
index|[
name|i
index|]
operator|=
operator|new
name|LastRecoveredEntry
argument_list|(
name|i
argument_list|)
expr_stmt|;
block|}
block|}
specifier|public
name|void
name|updateStored
parameter_list|(
name|long
name|sequence
parameter_list|,
name|int
name|priority
parameter_list|)
block|{
name|perPriority
index|[
name|priority
index|]
operator|.
name|stored
operator|=
name|sequence
expr_stmt|;
block|}
specifier|public
name|LastRecoveredEntry
name|defaultPriority
parameter_list|()
block|{
return|return
name|perPriority
index|[
literal|0
index|]
return|;
block|}
annotation|@
name|Override
specifier|public
name|String
name|toString
parameter_list|()
block|{
return|return
name|Arrays
operator|.
name|deepToString
argument_list|(
name|perPriority
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|Iterator
argument_list|<
name|LastRecoveredEntry
argument_list|>
name|iterator
parameter_list|()
block|{
return|return
operator|new
name|PriorityIterator
argument_list|()
return|;
block|}
class|class
name|PriorityIterator
implements|implements
name|Iterator
argument_list|<
name|LastRecoveredEntry
argument_list|>
block|{
name|int
name|current
init|=
literal|9
decl_stmt|;
annotation|@
name|Override
specifier|public
name|boolean
name|hasNext
parameter_list|()
block|{
for|for
control|(
name|int
name|i
init|=
name|current
init|;
name|i
operator|>=
literal|0
condition|;
name|i
operator|--
control|)
block|{
if|if
condition|(
name|perPriority
index|[
name|i
index|]
operator|.
name|hasMessages
argument_list|()
condition|)
block|{
name|current
operator|=
name|i
expr_stmt|;
return|return
literal|true
return|;
block|}
block|}
return|return
literal|false
return|;
block|}
annotation|@
name|Override
specifier|public
name|LastRecoveredEntry
name|next
parameter_list|()
block|{
return|return
name|perPriority
index|[
name|current
index|]
return|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|remove
parameter_list|()
block|{
throw|throw
operator|new
name|RuntimeException
argument_list|(
literal|"not implemented"
argument_list|)
throw|;
block|}
block|}
block|}
specifier|private
class|class
name|LastRecoveredEntry
block|{
specifier|final
name|int
name|priority
decl_stmt|;
name|long
name|recovered
init|=
literal|0
decl_stmt|;
name|long
name|stored
init|=
name|Integer
operator|.
name|MAX_VALUE
decl_stmt|;
specifier|public
name|LastRecoveredEntry
parameter_list|(
name|int
name|priority
parameter_list|)
block|{
name|this
operator|.
name|priority
operator|=
name|priority
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|String
name|toString
parameter_list|()
block|{
return|return
name|priority
operator|+
literal|"-"
operator|+
name|stored
operator|+
literal|":"
operator|+
name|recovered
return|;
block|}
specifier|public
name|void
name|exhausted
parameter_list|()
block|{
name|stored
operator|=
name|recovered
expr_stmt|;
block|}
specifier|public
name|boolean
name|hasMessages
parameter_list|()
block|{
return|return
name|stored
operator|>
name|recovered
return|;
block|}
block|}
class|class
name|LastRecoveredAwareListener
implements|implements
name|JDBCMessageRecoveryListener
block|{
specifier|final
name|MessageRecoveryListener
name|delegate
decl_stmt|;
specifier|final
name|int
name|maxMessages
decl_stmt|;
name|LastRecoveredEntry
name|lastRecovered
decl_stmt|;
name|int
name|recoveredCount
decl_stmt|;
name|int
name|recoveredMarker
decl_stmt|;
specifier|public
name|LastRecoveredAwareListener
parameter_list|(
name|MessageRecoveryListener
name|delegate
parameter_list|,
name|int
name|maxMessages
parameter_list|)
block|{
name|this
operator|.
name|delegate
operator|=
name|delegate
expr_stmt|;
name|this
operator|.
name|maxMessages
operator|=
name|maxMessages
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|recoverMessage
parameter_list|(
name|long
name|sequenceId
parameter_list|,
name|byte
index|[]
name|data
parameter_list|)
throws|throws
name|Exception
block|{
if|if
condition|(
name|delegate
operator|.
name|hasSpace
argument_list|()
operator|&&
name|recoveredCount
operator|<
name|maxMessages
condition|)
block|{
name|Message
name|msg
init|=
operator|(
name|Message
operator|)
name|wireFormat
operator|.
name|unmarshal
argument_list|(
operator|new
name|ByteSequence
argument_list|(
name|data
argument_list|)
argument_list|)
decl_stmt|;
name|msg
operator|.
name|getMessageId
argument_list|()
operator|.
name|setBrokerSequenceId
argument_list|(
name|sequenceId
argument_list|)
expr_stmt|;
name|lastRecovered
operator|.
name|recovered
operator|=
name|sequenceId
expr_stmt|;
if|if
condition|(
name|delegate
operator|.
name|recoverMessage
argument_list|(
name|msg
argument_list|)
condition|)
block|{
name|recoveredCount
operator|++
expr_stmt|;
return|return
literal|true
return|;
block|}
block|}
return|return
literal|false
return|;
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|recoverMessageReference
parameter_list|(
name|String
name|reference
parameter_list|)
throws|throws
name|Exception
block|{
return|return
name|delegate
operator|.
name|recoverMessageReference
argument_list|(
operator|new
name|MessageId
argument_list|(
name|reference
argument_list|)
argument_list|)
return|;
block|}
specifier|public
name|void
name|setLastRecovered
parameter_list|(
name|LastRecoveredEntry
name|lastRecovered
parameter_list|)
block|{
name|this
operator|.
name|lastRecovered
operator|=
name|lastRecovered
expr_stmt|;
name|recoveredMarker
operator|=
name|recoveredCount
expr_stmt|;
block|}
specifier|public
name|boolean
name|complete
parameter_list|()
block|{
return|return
operator|!
name|delegate
operator|.
name|hasSpace
argument_list|()
operator|||
name|recoveredCount
operator|==
name|maxMessages
return|;
block|}
specifier|public
name|boolean
name|stalled
parameter_list|()
block|{
return|return
name|recoveredMarker
operator|==
name|recoveredCount
return|;
block|}
block|}
annotation|@
name|Override
specifier|public
specifier|synchronized
name|void
name|recoverNextMessages
parameter_list|(
specifier|final
name|String
name|clientId
parameter_list|,
specifier|final
name|String
name|subscriptionName
parameter_list|,
specifier|final
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
comment|//Duration duration = new Duration("recoverNextMessages");
name|TransactionContext
name|c
init|=
name|persistenceAdapter
operator|.
name|getTransactionContext
argument_list|()
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
if|if
condition|(
operator|!
name|subscriberLastRecoveredMap
operator|.
name|containsKey
argument_list|(
name|key
argument_list|)
condition|)
block|{
name|subscriberLastRecoveredMap
operator|.
name|put
argument_list|(
name|key
argument_list|,
operator|new
name|LastRecovered
argument_list|()
argument_list|)
expr_stmt|;
block|}
specifier|final
name|LastRecovered
name|lastRecovered
init|=
name|subscriberLastRecoveredMap
operator|.
name|get
argument_list|(
name|key
argument_list|)
decl_stmt|;
name|LastRecoveredAwareListener
name|recoveredAwareListener
init|=
operator|new
name|LastRecoveredAwareListener
argument_list|(
name|listener
argument_list|,
name|maxReturned
argument_list|)
decl_stmt|;
try|try
block|{
if|if
condition|(
name|LOG
operator|.
name|isTraceEnabled
argument_list|()
condition|)
block|{
name|LOG
operator|.
name|trace
argument_list|(
name|this
operator|+
literal|", "
operator|+
name|key
operator|+
literal|" existing last recovered: "
operator|+
name|lastRecovered
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|isPrioritizedMessages
argument_list|()
condition|)
block|{
name|Iterator
argument_list|<
name|LastRecoveredEntry
argument_list|>
name|it
init|=
name|lastRecovered
operator|.
name|iterator
argument_list|()
decl_stmt|;
for|for
control|(
init|;
name|it
operator|.
name|hasNext
argument_list|()
operator|&&
operator|!
name|recoveredAwareListener
operator|.
name|complete
argument_list|()
condition|;
control|)
block|{
name|LastRecoveredEntry
name|entry
init|=
name|it
operator|.
name|next
argument_list|()
decl_stmt|;
name|recoveredAwareListener
operator|.
name|setLastRecovered
argument_list|(
name|entry
argument_list|)
expr_stmt|;
comment|//Duration microDuration = new Duration("recoverNextMessages:loop");
name|adapter
operator|.
name|doRecoverNextMessagesWithPriority
argument_list|(
name|c
argument_list|,
name|destination
argument_list|,
name|clientId
argument_list|,
name|subscriptionName
argument_list|,
name|entry
operator|.
name|recovered
argument_list|,
name|entry
operator|.
name|priority
argument_list|,
name|maxReturned
argument_list|,
name|recoveredAwareListener
argument_list|)
expr_stmt|;
comment|//microDuration.end(new String(entry + " recoveredCount:" + recoveredAwareListener.recoveredCount));
if|if
condition|(
name|recoveredAwareListener
operator|.
name|stalled
argument_list|()
condition|)
block|{
if|if
condition|(
name|recoveredAwareListener
operator|.
name|complete
argument_list|()
condition|)
block|{
break|break;
block|}
else|else
block|{
name|entry
operator|.
name|exhausted
argument_list|()
expr_stmt|;
block|}
block|}
block|}
block|}
else|else
block|{
name|LastRecoveredEntry
name|last
init|=
name|lastRecovered
operator|.
name|defaultPriority
argument_list|()
decl_stmt|;
name|recoveredAwareListener
operator|.
name|setLastRecovered
argument_list|(
name|last
argument_list|)
expr_stmt|;
name|adapter
operator|.
name|doRecoverNextMessages
argument_list|(
name|c
argument_list|,
name|destination
argument_list|,
name|clientId
argument_list|,
name|subscriptionName
argument_list|,
name|last
operator|.
name|recovered
argument_list|,
literal|0
argument_list|,
name|maxReturned
argument_list|,
name|recoveredAwareListener
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|LOG
operator|.
name|isTraceEnabled
argument_list|()
condition|)
block|{
name|LOG
operator|.
name|trace
argument_list|(
name|key
operator|+
literal|" last recovered: "
operator|+
name|lastRecovered
argument_list|)
expr_stmt|;
block|}
comment|//duration.end();
block|}
catch|catch
parameter_list|(
name|SQLException
name|e
parameter_list|)
block|{
name|JDBCPersistenceAdapter
operator|.
name|log
argument_list|(
literal|"JDBC Failure: "
argument_list|,
name|e
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|c
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
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
if|if
condition|(
operator|!
name|pendingCompletion
operator|.
name|contains
argument_list|(
name|key
argument_list|)
condition|)
block|{
name|subscriberLastRecoveredMap
operator|.
name|remove
argument_list|(
name|key
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|LOG
operator|.
name|trace
argument_list|(
name|this
operator|+
literal|", skip resetBatch during pending completion for: "
operator|+
name|key
argument_list|)
expr_stmt|;
block|}
block|}
specifier|public
name|void
name|pendingCompletion
parameter_list|(
name|String
name|clientId
parameter_list|,
name|String
name|subscriptionName
parameter_list|,
name|long
name|sequenceId
parameter_list|,
name|byte
name|priority
parameter_list|)
block|{
specifier|final
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
name|LastRecovered
name|recovered
init|=
operator|new
name|LastRecovered
argument_list|()
decl_stmt|;
name|recovered
operator|.
name|perPriority
index|[
name|priority
index|]
operator|.
name|recovered
operator|=
name|sequenceId
expr_stmt|;
name|subscriberLastRecoveredMap
operator|.
name|put
argument_list|(
name|key
argument_list|,
name|recovered
argument_list|)
expr_stmt|;
name|pendingCompletion
operator|.
name|add
argument_list|(
name|key
argument_list|)
expr_stmt|;
name|LOG
operator|.
name|trace
argument_list|(
name|this
operator|+
literal|", pending completion: "
operator|+
name|key
operator|+
literal|", last: "
operator|+
name|recovered
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|complete
parameter_list|(
name|String
name|clientId
parameter_list|,
name|String
name|subscriptionName
parameter_list|)
block|{
name|pendingCompletion
operator|.
name|remove
argument_list|(
name|getSubscriptionKey
argument_list|(
name|clientId
argument_list|,
name|subscriptionName
argument_list|)
argument_list|)
expr_stmt|;
name|LOG
operator|.
name|trace
argument_list|(
name|this
operator|+
literal|", completion for: "
operator|+
name|getSubscriptionKey
argument_list|(
name|clientId
argument_list|,
name|subscriptionName
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|protected
name|void
name|onAdd
parameter_list|(
name|Message
name|message
parameter_list|,
name|long
name|sequenceId
parameter_list|,
name|byte
name|priority
parameter_list|)
block|{
comment|// update last recovered state
for|for
control|(
name|LastRecovered
name|last
range|:
name|subscriberLastRecoveredMap
operator|.
name|values
argument_list|()
control|)
block|{
name|last
operator|.
name|updateStored
argument_list|(
name|sequenceId
argument_list|,
name|priority
argument_list|)
expr_stmt|;
block|}
name|sequenceIdCacheSizeLock
operator|.
name|writeLock
argument_list|()
operator|.
name|lock
argument_list|()
expr_stmt|;
try|try
block|{
name|sequenceIdCache
operator|.
name|put
argument_list|(
name|message
operator|.
name|getMessageId
argument_list|()
argument_list|,
operator|new
name|long
index|[]
block|{
name|sequenceId
block|,
name|priority
block|}
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|sequenceIdCacheSizeLock
operator|.
name|writeLock
argument_list|()
operator|.
name|unlock
argument_list|()
expr_stmt|;
block|}
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
name|TransactionContext
name|c
init|=
name|persistenceAdapter
operator|.
name|getTransactionContext
argument_list|()
decl_stmt|;
try|try
block|{
name|c
operator|=
name|persistenceAdapter
operator|.
name|getTransactionContext
argument_list|()
expr_stmt|;
name|adapter
operator|.
name|doSetSubscriberEntry
argument_list|(
name|c
argument_list|,
name|subscriptionInfo
argument_list|,
name|retroactive
argument_list|,
name|isPrioritizedMessages
argument_list|()
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|SQLException
name|e
parameter_list|)
block|{
name|JDBCPersistenceAdapter
operator|.
name|log
argument_list|(
literal|"JDBC Failure: "
argument_list|,
name|e
argument_list|)
expr_stmt|;
throw|throw
name|IOExceptionSupport
operator|.
name|create
argument_list|(
literal|"Failed to lookup subscription for info: "
operator|+
name|subscriptionInfo
operator|.
name|getClientId
argument_list|()
operator|+
literal|". Reason: "
operator|+
name|e
argument_list|,
name|e
argument_list|)
throw|;
block|}
finally|finally
block|{
name|c
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
comment|/**      * @see org.apache.activemq.store.TopicMessageStore#lookupSubscription(String,      *      String)      */
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
name|TransactionContext
name|c
init|=
name|persistenceAdapter
operator|.
name|getTransactionContext
argument_list|()
decl_stmt|;
try|try
block|{
return|return
name|adapter
operator|.
name|doGetSubscriberEntry
argument_list|(
name|c
argument_list|,
name|destination
argument_list|,
name|clientId
argument_list|,
name|subscriptionName
argument_list|)
return|;
block|}
catch|catch
parameter_list|(
name|SQLException
name|e
parameter_list|)
block|{
name|JDBCPersistenceAdapter
operator|.
name|log
argument_list|(
literal|"JDBC Failure: "
argument_list|,
name|e
argument_list|)
expr_stmt|;
throw|throw
name|IOExceptionSupport
operator|.
name|create
argument_list|(
literal|"Failed to lookup subscription for: "
operator|+
name|clientId
operator|+
literal|". Reason: "
operator|+
name|e
argument_list|,
name|e
argument_list|)
throw|;
block|}
finally|finally
block|{
name|c
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
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
name|TransactionContext
name|c
init|=
name|persistenceAdapter
operator|.
name|getTransactionContext
argument_list|()
decl_stmt|;
try|try
block|{
name|adapter
operator|.
name|doDeleteSubscription
argument_list|(
name|c
argument_list|,
name|destination
argument_list|,
name|clientId
argument_list|,
name|subscriptionName
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|SQLException
name|e
parameter_list|)
block|{
name|JDBCPersistenceAdapter
operator|.
name|log
argument_list|(
literal|"JDBC Failure: "
argument_list|,
name|e
argument_list|)
expr_stmt|;
throw|throw
name|IOExceptionSupport
operator|.
name|create
argument_list|(
literal|"Failed to remove subscription for: "
operator|+
name|clientId
operator|+
literal|". Reason: "
operator|+
name|e
argument_list|,
name|e
argument_list|)
throw|;
block|}
finally|finally
block|{
name|c
operator|.
name|close
argument_list|()
expr_stmt|;
name|resetBatching
argument_list|(
name|clientId
argument_list|,
name|subscriptionName
argument_list|)
expr_stmt|;
block|}
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
name|TransactionContext
name|c
init|=
name|persistenceAdapter
operator|.
name|getTransactionContext
argument_list|()
decl_stmt|;
try|try
block|{
return|return
name|adapter
operator|.
name|doGetAllSubscriptions
argument_list|(
name|c
argument_list|,
name|destination
argument_list|)
return|;
block|}
catch|catch
parameter_list|(
name|SQLException
name|e
parameter_list|)
block|{
name|JDBCPersistenceAdapter
operator|.
name|log
argument_list|(
literal|"JDBC Failure: "
argument_list|,
name|e
argument_list|)
expr_stmt|;
throw|throw
name|IOExceptionSupport
operator|.
name|create
argument_list|(
literal|"Failed to lookup subscriptions. Reason: "
operator|+
name|e
argument_list|,
name|e
argument_list|)
throw|;
block|}
finally|finally
block|{
name|c
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
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
comment|//Duration duration = new Duration("getMessageCount");
name|int
name|result
init|=
literal|0
decl_stmt|;
name|TransactionContext
name|c
init|=
name|persistenceAdapter
operator|.
name|getTransactionContext
argument_list|()
decl_stmt|;
try|try
block|{
name|result
operator|=
name|adapter
operator|.
name|doGetDurableSubscriberMessageCount
argument_list|(
name|c
argument_list|,
name|destination
argument_list|,
name|clientId
argument_list|,
name|subscriberName
argument_list|,
name|isPrioritizedMessages
argument_list|()
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|SQLException
name|e
parameter_list|)
block|{
name|JDBCPersistenceAdapter
operator|.
name|log
argument_list|(
literal|"JDBC Failure: "
argument_list|,
name|e
argument_list|)
expr_stmt|;
throw|throw
name|IOExceptionSupport
operator|.
name|create
argument_list|(
literal|"Failed to get Message Count: "
operator|+
name|clientId
operator|+
literal|". Reason: "
operator|+
name|e
argument_list|,
name|e
argument_list|)
throw|;
block|}
finally|finally
block|{
name|c
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
if|if
condition|(
name|LOG
operator|.
name|isTraceEnabled
argument_list|()
condition|)
block|{
name|LOG
operator|.
name|trace
argument_list|(
name|clientId
operator|+
literal|":"
operator|+
name|subscriberName
operator|+
literal|", messageCount: "
operator|+
name|result
argument_list|)
expr_stmt|;
block|}
comment|//duration.end();
return|return
name|result
return|;
block|}
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
literal|0
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
specifier|private
specifier|final
name|MessageStoreSubscriptionStatistics
name|stats
init|=
operator|new
name|MessageStoreSubscriptionStatistics
argument_list|(
literal|false
argument_list|)
decl_stmt|;
annotation|@
name|Override
specifier|public
name|MessageStoreSubscriptionStatistics
name|getMessageStoreSubStatistics
parameter_list|()
block|{
return|return
name|stats
return|;
block|}
block|}
end_class

end_unit

