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
name|kahadb
package|;
end_package

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|File
import|;
end_import

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
name|Date
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
name|Set
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|TreeSet
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
name|Future
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
name|TransactionId
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
name|XATransactionId
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
name|AbstractMessageStore
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
name|MessageStore
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
name|ProxyMessageStore
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
name|ProxyTopicMessageStore
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
name|TransactionRecoveryListener
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
name|TransactionStore
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
name|kahadb
operator|.
name|data
operator|.
name|KahaCommitCommand
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
name|kahadb
operator|.
name|data
operator|.
name|KahaEntryType
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
name|kahadb
operator|.
name|data
operator|.
name|KahaPrepareCommand
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
name|kahadb
operator|.
name|data
operator|.
name|KahaTraceCommand
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
name|IOHelper
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|kahadb
operator|.
name|journal
operator|.
name|Journal
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|kahadb
operator|.
name|journal
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
name|kahadb
operator|.
name|util
operator|.
name|DataByteArrayInputStream
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|kahadb
operator|.
name|util
operator|.
name|DataByteArrayOutputStream
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

begin_class
specifier|public
class|class
name|MultiKahaDBTransactionStore
implements|implements
name|TransactionStore
block|{
specifier|static
specifier|final
name|Logger
name|LOG
init|=
name|LoggerFactory
operator|.
name|getLogger
argument_list|(
name|MultiKahaDBTransactionStore
operator|.
name|class
argument_list|)
decl_stmt|;
specifier|final
name|MultiKahaDBPersistenceAdapter
name|multiKahaDBPersistenceAdapter
decl_stmt|;
specifier|final
name|ConcurrentHashMap
argument_list|<
name|TransactionId
argument_list|,
name|Tx
argument_list|>
name|inflightTransactions
init|=
operator|new
name|ConcurrentHashMap
argument_list|<
name|TransactionId
argument_list|,
name|Tx
argument_list|>
argument_list|()
decl_stmt|;
specifier|final
name|Set
argument_list|<
name|TransactionId
argument_list|>
name|recoveredPendingCommit
init|=
operator|new
name|HashSet
argument_list|<
name|TransactionId
argument_list|>
argument_list|()
decl_stmt|;
specifier|private
name|Journal
name|journal
decl_stmt|;
specifier|private
name|int
name|journalMaxFileLength
init|=
name|Journal
operator|.
name|DEFAULT_MAX_FILE_LENGTH
decl_stmt|;
specifier|private
name|int
name|journalWriteBatchSize
init|=
name|Journal
operator|.
name|DEFAULT_MAX_WRITE_BATCH_SIZE
decl_stmt|;
specifier|public
name|MultiKahaDBTransactionStore
parameter_list|(
name|MultiKahaDBPersistenceAdapter
name|multiKahaDBPersistenceAdapter
parameter_list|)
block|{
name|this
operator|.
name|multiKahaDBPersistenceAdapter
operator|=
name|multiKahaDBPersistenceAdapter
expr_stmt|;
block|}
specifier|public
name|MessageStore
name|proxy
parameter_list|(
specifier|final
name|TransactionStore
name|transactionStore
parameter_list|,
name|MessageStore
name|messageStore
parameter_list|)
block|{
return|return
operator|new
name|ProxyMessageStore
argument_list|(
name|messageStore
argument_list|)
block|{
annotation|@
name|Override
specifier|public
name|void
name|addMessage
parameter_list|(
name|ConnectionContext
name|context
parameter_list|,
specifier|final
name|Message
name|send
parameter_list|)
throws|throws
name|IOException
block|{
name|MultiKahaDBTransactionStore
operator|.
name|this
operator|.
name|addMessage
argument_list|(
name|transactionStore
argument_list|,
name|context
argument_list|,
name|getDelegate
argument_list|()
argument_list|,
name|send
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
specifier|final
name|Message
name|send
parameter_list|,
name|boolean
name|canOptimizeHint
parameter_list|)
throws|throws
name|IOException
block|{
name|MultiKahaDBTransactionStore
operator|.
name|this
operator|.
name|addMessage
argument_list|(
name|transactionStore
argument_list|,
name|context
argument_list|,
name|getDelegate
argument_list|()
argument_list|,
name|send
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|Future
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
name|MultiKahaDBTransactionStore
operator|.
name|this
operator|.
name|asyncAddQueueMessage
argument_list|(
name|transactionStore
argument_list|,
name|context
argument_list|,
name|getDelegate
argument_list|()
argument_list|,
name|message
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|Future
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
name|MultiKahaDBTransactionStore
operator|.
name|this
operator|.
name|asyncAddQueueMessage
argument_list|(
name|transactionStore
argument_list|,
name|context
argument_list|,
name|getDelegate
argument_list|()
argument_list|,
name|message
argument_list|)
return|;
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
specifier|final
name|MessageAck
name|ack
parameter_list|)
throws|throws
name|IOException
block|{
name|MultiKahaDBTransactionStore
operator|.
name|this
operator|.
name|removeMessage
argument_list|(
name|transactionStore
argument_list|,
name|context
argument_list|,
name|getDelegate
argument_list|()
argument_list|,
name|ack
argument_list|)
expr_stmt|;
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
name|MultiKahaDBTransactionStore
operator|.
name|this
operator|.
name|removeAsyncMessage
argument_list|(
name|transactionStore
argument_list|,
name|context
argument_list|,
name|getDelegate
argument_list|()
argument_list|,
name|ack
argument_list|)
expr_stmt|;
block|}
block|}
return|;
block|}
specifier|public
name|TopicMessageStore
name|proxy
parameter_list|(
specifier|final
name|TransactionStore
name|transactionStore
parameter_list|,
specifier|final
name|TopicMessageStore
name|messageStore
parameter_list|)
block|{
return|return
operator|new
name|ProxyTopicMessageStore
argument_list|(
name|messageStore
argument_list|)
block|{
annotation|@
name|Override
specifier|public
name|void
name|addMessage
parameter_list|(
name|ConnectionContext
name|context
parameter_list|,
specifier|final
name|Message
name|send
parameter_list|,
name|boolean
name|canOptimizeHint
parameter_list|)
throws|throws
name|IOException
block|{
name|MultiKahaDBTransactionStore
operator|.
name|this
operator|.
name|addMessage
argument_list|(
name|transactionStore
argument_list|,
name|context
argument_list|,
name|getDelegate
argument_list|()
argument_list|,
name|send
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
specifier|final
name|Message
name|send
parameter_list|)
throws|throws
name|IOException
block|{
name|MultiKahaDBTransactionStore
operator|.
name|this
operator|.
name|addMessage
argument_list|(
name|transactionStore
argument_list|,
name|context
argument_list|,
name|getDelegate
argument_list|()
argument_list|,
name|send
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|Future
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
name|MultiKahaDBTransactionStore
operator|.
name|this
operator|.
name|asyncAddTopicMessage
argument_list|(
name|transactionStore
argument_list|,
name|context
argument_list|,
name|getDelegate
argument_list|()
argument_list|,
name|message
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|Future
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
name|MultiKahaDBTransactionStore
operator|.
name|this
operator|.
name|asyncAddTopicMessage
argument_list|(
name|transactionStore
argument_list|,
name|context
argument_list|,
name|getDelegate
argument_list|()
argument_list|,
name|message
argument_list|)
return|;
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
specifier|final
name|MessageAck
name|ack
parameter_list|)
throws|throws
name|IOException
block|{
name|MultiKahaDBTransactionStore
operator|.
name|this
operator|.
name|removeMessage
argument_list|(
name|transactionStore
argument_list|,
name|context
argument_list|,
name|getDelegate
argument_list|()
argument_list|,
name|ack
argument_list|)
expr_stmt|;
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
name|MultiKahaDBTransactionStore
operator|.
name|this
operator|.
name|removeAsyncMessage
argument_list|(
name|transactionStore
argument_list|,
name|context
argument_list|,
name|getDelegate
argument_list|()
argument_list|,
name|ack
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
name|MultiKahaDBTransactionStore
operator|.
name|this
operator|.
name|acknowledge
argument_list|(
name|transactionStore
argument_list|,
name|context
argument_list|,
operator|(
name|TopicMessageStore
operator|)
name|getDelegate
argument_list|()
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
block|}
return|;
block|}
specifier|public
name|void
name|deleteAllMessages
parameter_list|()
block|{
name|IOHelper
operator|.
name|deleteChildren
argument_list|(
name|getDirectory
argument_list|()
argument_list|)
expr_stmt|;
block|}
specifier|public
name|int
name|getJournalMaxFileLength
parameter_list|()
block|{
return|return
name|journalMaxFileLength
return|;
block|}
specifier|public
name|void
name|setJournalMaxFileLength
parameter_list|(
name|int
name|journalMaxFileLength
parameter_list|)
block|{
name|this
operator|.
name|journalMaxFileLength
operator|=
name|journalMaxFileLength
expr_stmt|;
block|}
specifier|public
name|int
name|getJournalMaxWriteBatchSize
parameter_list|()
block|{
return|return
name|journalWriteBatchSize
return|;
block|}
specifier|public
name|void
name|setJournalMaxWriteBatchSize
parameter_list|(
name|int
name|journalWriteBatchSize
parameter_list|)
block|{
name|this
operator|.
name|journalWriteBatchSize
operator|=
name|journalWriteBatchSize
expr_stmt|;
block|}
specifier|public
class|class
name|Tx
block|{
specifier|private
specifier|final
name|Set
argument_list|<
name|TransactionStore
argument_list|>
name|stores
init|=
operator|new
name|HashSet
argument_list|<
name|TransactionStore
argument_list|>
argument_list|()
decl_stmt|;
specifier|private
name|int
name|prepareLocationId
init|=
literal|0
decl_stmt|;
specifier|public
name|void
name|trackStore
parameter_list|(
name|TransactionStore
name|store
parameter_list|)
block|{
name|stores
operator|.
name|add
argument_list|(
name|store
argument_list|)
expr_stmt|;
block|}
specifier|public
name|Set
argument_list|<
name|TransactionStore
argument_list|>
name|getStores
parameter_list|()
block|{
return|return
name|stores
return|;
block|}
specifier|public
name|void
name|trackPrepareLocation
parameter_list|(
name|Location
name|location
parameter_list|)
block|{
name|this
operator|.
name|prepareLocationId
operator|=
name|location
operator|.
name|getDataFileId
argument_list|()
expr_stmt|;
block|}
specifier|public
name|int
name|getPreparedLocationId
parameter_list|()
block|{
return|return
name|prepareLocationId
return|;
block|}
block|}
specifier|public
name|Tx
name|getTx
parameter_list|(
name|TransactionId
name|txid
parameter_list|)
block|{
name|Tx
name|tx
init|=
name|inflightTransactions
operator|.
name|get
argument_list|(
name|txid
argument_list|)
decl_stmt|;
if|if
condition|(
name|tx
operator|==
literal|null
condition|)
block|{
name|tx
operator|=
operator|new
name|Tx
argument_list|()
expr_stmt|;
name|inflightTransactions
operator|.
name|put
argument_list|(
name|txid
argument_list|,
name|tx
argument_list|)
expr_stmt|;
block|}
return|return
name|tx
return|;
block|}
specifier|public
name|Tx
name|removeTx
parameter_list|(
name|TransactionId
name|txid
parameter_list|)
block|{
return|return
name|inflightTransactions
operator|.
name|remove
argument_list|(
name|txid
argument_list|)
return|;
block|}
specifier|public
name|void
name|prepare
parameter_list|(
name|TransactionId
name|txid
parameter_list|)
throws|throws
name|IOException
block|{
name|Tx
name|tx
init|=
name|getTx
argument_list|(
name|txid
argument_list|)
decl_stmt|;
for|for
control|(
name|TransactionStore
name|store
range|:
name|tx
operator|.
name|getStores
argument_list|()
control|)
block|{
name|store
operator|.
name|prepare
argument_list|(
name|txid
argument_list|)
expr_stmt|;
block|}
block|}
specifier|public
name|void
name|commit
parameter_list|(
name|TransactionId
name|txid
parameter_list|,
name|boolean
name|wasPrepared
parameter_list|,
name|Runnable
name|preCommit
parameter_list|,
name|Runnable
name|postCommit
parameter_list|)
throws|throws
name|IOException
block|{
if|if
condition|(
name|preCommit
operator|!=
literal|null
condition|)
block|{
name|preCommit
operator|.
name|run
argument_list|()
expr_stmt|;
block|}
name|Tx
name|tx
init|=
name|getTx
argument_list|(
name|txid
argument_list|)
decl_stmt|;
if|if
condition|(
name|wasPrepared
condition|)
block|{
for|for
control|(
name|TransactionStore
name|store
range|:
name|tx
operator|.
name|getStores
argument_list|()
control|)
block|{
name|store
operator|.
name|commit
argument_list|(
name|txid
argument_list|,
literal|true
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|)
expr_stmt|;
block|}
block|}
else|else
block|{
comment|// can only do 1pc on a single store
if|if
condition|(
name|tx
operator|.
name|getStores
argument_list|()
operator|.
name|size
argument_list|()
operator|==
literal|1
condition|)
block|{
for|for
control|(
name|TransactionStore
name|store
range|:
name|tx
operator|.
name|getStores
argument_list|()
control|)
block|{
name|store
operator|.
name|commit
argument_list|(
name|txid
argument_list|,
literal|false
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|)
expr_stmt|;
block|}
block|}
else|else
block|{
comment|// need to do local 2pc
for|for
control|(
name|TransactionStore
name|store
range|:
name|tx
operator|.
name|getStores
argument_list|()
control|)
block|{
name|store
operator|.
name|prepare
argument_list|(
name|txid
argument_list|)
expr_stmt|;
block|}
name|persistOutcome
argument_list|(
name|tx
argument_list|,
name|txid
argument_list|)
expr_stmt|;
for|for
control|(
name|TransactionStore
name|store
range|:
name|tx
operator|.
name|getStores
argument_list|()
control|)
block|{
name|store
operator|.
name|commit
argument_list|(
name|txid
argument_list|,
literal|true
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|)
expr_stmt|;
block|}
name|persistCompletion
argument_list|(
name|txid
argument_list|)
expr_stmt|;
block|}
block|}
name|removeTx
argument_list|(
name|txid
argument_list|)
expr_stmt|;
if|if
condition|(
name|postCommit
operator|!=
literal|null
condition|)
block|{
name|postCommit
operator|.
name|run
argument_list|()
expr_stmt|;
block|}
block|}
specifier|public
name|void
name|persistOutcome
parameter_list|(
name|Tx
name|tx
parameter_list|,
name|TransactionId
name|txid
parameter_list|)
throws|throws
name|IOException
block|{
name|tx
operator|.
name|trackPrepareLocation
argument_list|(
name|store
argument_list|(
operator|new
name|KahaPrepareCommand
argument_list|()
operator|.
name|setTransactionInfo
argument_list|(
name|multiKahaDBPersistenceAdapter
operator|.
name|transactionIdTransformer
operator|.
name|transform
argument_list|(
name|txid
argument_list|)
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|persistCompletion
parameter_list|(
name|TransactionId
name|txid
parameter_list|)
throws|throws
name|IOException
block|{
name|store
argument_list|(
operator|new
name|KahaCommitCommand
argument_list|()
operator|.
name|setTransactionInfo
argument_list|(
name|multiKahaDBPersistenceAdapter
operator|.
name|transactionIdTransformer
operator|.
name|transform
argument_list|(
name|txid
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
specifier|private
name|Location
name|store
parameter_list|(
name|JournalCommand
argument_list|<
name|?
argument_list|>
name|data
parameter_list|)
throws|throws
name|IOException
block|{
name|int
name|size
init|=
name|data
operator|.
name|serializedSizeFramed
argument_list|()
decl_stmt|;
name|DataByteArrayOutputStream
name|os
init|=
operator|new
name|DataByteArrayOutputStream
argument_list|(
name|size
operator|+
literal|1
argument_list|)
decl_stmt|;
name|os
operator|.
name|writeByte
argument_list|(
name|data
operator|.
name|type
argument_list|()
operator|.
name|getNumber
argument_list|()
argument_list|)
expr_stmt|;
name|data
operator|.
name|writeFramed
argument_list|(
name|os
argument_list|)
expr_stmt|;
name|Location
name|location
init|=
name|journal
operator|.
name|write
argument_list|(
name|os
operator|.
name|toByteSequence
argument_list|()
argument_list|,
literal|true
argument_list|)
decl_stmt|;
name|journal
operator|.
name|setLastAppendLocation
argument_list|(
name|location
argument_list|)
expr_stmt|;
return|return
name|location
return|;
block|}
specifier|public
name|void
name|rollback
parameter_list|(
name|TransactionId
name|txid
parameter_list|)
throws|throws
name|IOException
block|{
name|Tx
name|tx
init|=
name|removeTx
argument_list|(
name|txid
argument_list|)
decl_stmt|;
if|if
condition|(
name|tx
operator|!=
literal|null
condition|)
block|{
for|for
control|(
name|TransactionStore
name|store
range|:
name|tx
operator|.
name|getStores
argument_list|()
control|)
block|{
name|store
operator|.
name|rollback
argument_list|(
name|txid
argument_list|)
expr_stmt|;
block|}
block|}
block|}
specifier|public
name|void
name|start
parameter_list|()
throws|throws
name|Exception
block|{
name|journal
operator|=
operator|new
name|Journal
argument_list|()
block|{
annotation|@
name|Override
specifier|protected
name|void
name|cleanup
parameter_list|()
block|{
name|super
operator|.
name|cleanup
argument_list|()
expr_stmt|;
name|txStoreCleanup
argument_list|()
expr_stmt|;
block|}
block|}
expr_stmt|;
name|journal
operator|.
name|setDirectory
argument_list|(
name|getDirectory
argument_list|()
argument_list|)
expr_stmt|;
name|journal
operator|.
name|setMaxFileLength
argument_list|(
name|journalMaxFileLength
argument_list|)
expr_stmt|;
name|journal
operator|.
name|setWriteBatchSize
argument_list|(
name|journalWriteBatchSize
argument_list|)
expr_stmt|;
name|IOHelper
operator|.
name|mkdirs
argument_list|(
name|journal
operator|.
name|getDirectory
argument_list|()
argument_list|)
expr_stmt|;
name|journal
operator|.
name|start
argument_list|()
expr_stmt|;
name|recoverPendingLocalTransactions
argument_list|()
expr_stmt|;
name|store
argument_list|(
operator|new
name|KahaTraceCommand
argument_list|()
operator|.
name|setMessage
argument_list|(
literal|"LOADED "
operator|+
operator|new
name|Date
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
specifier|private
name|void
name|txStoreCleanup
parameter_list|()
block|{
name|Set
argument_list|<
name|Integer
argument_list|>
name|knownDataFileIds
init|=
operator|new
name|TreeSet
argument_list|<
name|Integer
argument_list|>
argument_list|(
name|journal
operator|.
name|getFileMap
argument_list|()
operator|.
name|keySet
argument_list|()
argument_list|)
decl_stmt|;
for|for
control|(
name|Tx
name|tx
range|:
name|inflightTransactions
operator|.
name|values
argument_list|()
control|)
block|{
name|knownDataFileIds
operator|.
name|remove
argument_list|(
name|tx
operator|.
name|getPreparedLocationId
argument_list|()
argument_list|)
expr_stmt|;
block|}
try|try
block|{
name|journal
operator|.
name|removeDataFiles
argument_list|(
name|knownDataFileIds
argument_list|)
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
name|this
operator|+
literal|", Failed to remove tx journal datafiles "
operator|+
name|knownDataFileIds
argument_list|)
expr_stmt|;
block|}
block|}
specifier|private
name|File
name|getDirectory
parameter_list|()
block|{
return|return
operator|new
name|File
argument_list|(
name|multiKahaDBPersistenceAdapter
operator|.
name|getDirectory
argument_list|()
argument_list|,
literal|"txStore"
argument_list|)
return|;
block|}
specifier|public
name|void
name|stop
parameter_list|()
throws|throws
name|Exception
block|{
name|journal
operator|.
name|close
argument_list|()
expr_stmt|;
name|journal
operator|=
literal|null
expr_stmt|;
block|}
specifier|private
name|void
name|recoverPendingLocalTransactions
parameter_list|()
throws|throws
name|IOException
block|{
name|Location
name|location
init|=
name|journal
operator|.
name|getNextLocation
argument_list|(
literal|null
argument_list|)
decl_stmt|;
while|while
condition|(
name|location
operator|!=
literal|null
condition|)
block|{
name|process
argument_list|(
name|load
argument_list|(
name|location
argument_list|)
argument_list|)
expr_stmt|;
name|location
operator|=
name|journal
operator|.
name|getNextLocation
argument_list|(
name|location
argument_list|)
expr_stmt|;
block|}
name|recoveredPendingCommit
operator|.
name|addAll
argument_list|(
name|inflightTransactions
operator|.
name|keySet
argument_list|()
argument_list|)
expr_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"pending local transactions: "
operator|+
name|recoveredPendingCommit
argument_list|)
expr_stmt|;
block|}
specifier|public
name|JournalCommand
argument_list|<
name|?
argument_list|>
name|load
parameter_list|(
name|Location
name|location
parameter_list|)
throws|throws
name|IOException
block|{
name|DataByteArrayInputStream
name|is
init|=
operator|new
name|DataByteArrayInputStream
argument_list|(
name|journal
operator|.
name|read
argument_list|(
name|location
argument_list|)
argument_list|)
decl_stmt|;
name|byte
name|readByte
init|=
name|is
operator|.
name|readByte
argument_list|()
decl_stmt|;
name|KahaEntryType
name|type
init|=
name|KahaEntryType
operator|.
name|valueOf
argument_list|(
name|readByte
argument_list|)
decl_stmt|;
if|if
condition|(
name|type
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|IOException
argument_list|(
literal|"Could not load journal record. Invalid location: "
operator|+
name|location
argument_list|)
throw|;
block|}
name|JournalCommand
argument_list|<
name|?
argument_list|>
name|message
init|=
operator|(
name|JournalCommand
argument_list|<
name|?
argument_list|>
operator|)
name|type
operator|.
name|createMessage
argument_list|()
decl_stmt|;
name|message
operator|.
name|mergeFramed
argument_list|(
name|is
argument_list|)
expr_stmt|;
return|return
name|message
return|;
block|}
specifier|public
name|void
name|process
parameter_list|(
name|JournalCommand
argument_list|<
name|?
argument_list|>
name|command
parameter_list|)
throws|throws
name|IOException
block|{
switch|switch
condition|(
name|command
operator|.
name|type
argument_list|()
condition|)
block|{
case|case
name|KAHA_PREPARE_COMMAND
case|:
name|KahaPrepareCommand
name|prepareCommand
init|=
operator|(
name|KahaPrepareCommand
operator|)
name|command
decl_stmt|;
name|getTx
argument_list|(
name|TransactionIdConversion
operator|.
name|convert
argument_list|(
name|prepareCommand
operator|.
name|getTransactionInfo
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
break|break;
case|case
name|KAHA_COMMIT_COMMAND
case|:
name|KahaCommitCommand
name|commitCommand
init|=
operator|(
name|KahaCommitCommand
operator|)
name|command
decl_stmt|;
name|removeTx
argument_list|(
name|TransactionIdConversion
operator|.
name|convert
argument_list|(
name|commitCommand
operator|.
name|getTransactionInfo
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
break|break;
case|case
name|KAHA_TRACE_COMMAND
case|:
break|break;
default|default:
throw|throw
operator|new
name|IOException
argument_list|(
literal|"Unexpected command in transaction journal: "
operator|+
name|command
argument_list|)
throw|;
block|}
block|}
specifier|public
specifier|synchronized
name|void
name|recover
parameter_list|(
specifier|final
name|TransactionRecoveryListener
name|listener
parameter_list|)
throws|throws
name|IOException
block|{
for|for
control|(
specifier|final
name|KahaDBPersistenceAdapter
name|adapter
range|:
name|multiKahaDBPersistenceAdapter
operator|.
name|adapters
control|)
block|{
name|adapter
operator|.
name|createTransactionStore
argument_list|()
operator|.
name|recover
argument_list|(
operator|new
name|TransactionRecoveryListener
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|void
name|recover
parameter_list|(
name|XATransactionId
name|xid
parameter_list|,
name|Message
index|[]
name|addedMessages
parameter_list|,
name|MessageAck
index|[]
name|acks
parameter_list|)
block|{
try|try
block|{
name|getTx
argument_list|(
name|xid
argument_list|)
operator|.
name|trackStore
argument_list|(
name|adapter
operator|.
name|createTransactionStore
argument_list|()
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
name|LOG
operator|.
name|error
argument_list|(
literal|"Failed to access transaction store: "
operator|+
name|adapter
operator|+
literal|" for prepared xa tid: "
operator|+
name|xid
argument_list|,
name|e
argument_list|)
expr_stmt|;
block|}
name|listener
operator|.
name|recover
argument_list|(
name|xid
argument_list|,
name|addedMessages
argument_list|,
name|acks
argument_list|)
expr_stmt|;
block|}
block|}
argument_list|)
expr_stmt|;
block|}
try|try
block|{
name|Broker
name|broker
init|=
name|multiKahaDBPersistenceAdapter
operator|.
name|getBrokerService
argument_list|()
operator|.
name|getBroker
argument_list|()
decl_stmt|;
comment|// force completion of local xa
for|for
control|(
name|TransactionId
name|txid
range|:
name|broker
operator|.
name|getPreparedTransactions
argument_list|(
literal|null
argument_list|)
control|)
block|{
if|if
condition|(
name|multiKahaDBPersistenceAdapter
operator|.
name|isLocalXid
argument_list|(
name|txid
argument_list|)
condition|)
block|{
try|try
block|{
if|if
condition|(
name|recoveredPendingCommit
operator|.
name|contains
argument_list|(
name|txid
argument_list|)
condition|)
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"delivering pending commit outcome for tid: "
operator|+
name|txid
argument_list|)
expr_stmt|;
name|broker
operator|.
name|commitTransaction
argument_list|(
literal|null
argument_list|,
name|txid
argument_list|,
literal|false
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"delivering rollback outcome to store for tid: "
operator|+
name|txid
argument_list|)
expr_stmt|;
name|broker
operator|.
name|forgetTransaction
argument_list|(
literal|null
argument_list|,
name|txid
argument_list|)
expr_stmt|;
block|}
name|persistCompletion
argument_list|(
name|txid
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|ex
parameter_list|)
block|{
name|LOG
operator|.
name|error
argument_list|(
literal|"failed to deliver pending outcome for tid: "
operator|+
name|txid
argument_list|,
name|ex
argument_list|)
expr_stmt|;
block|}
block|}
block|}
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
literal|"failed to resolve pending local transactions"
argument_list|,
name|e
argument_list|)
expr_stmt|;
block|}
block|}
name|void
name|addMessage
parameter_list|(
specifier|final
name|TransactionStore
name|transactionStore
parameter_list|,
name|ConnectionContext
name|context
parameter_list|,
specifier|final
name|MessageStore
name|destination
parameter_list|,
specifier|final
name|Message
name|message
parameter_list|)
throws|throws
name|IOException
block|{
if|if
condition|(
name|message
operator|.
name|getTransactionId
argument_list|()
operator|!=
literal|null
condition|)
block|{
name|getTx
argument_list|(
name|message
operator|.
name|getTransactionId
argument_list|()
argument_list|)
operator|.
name|trackStore
argument_list|(
name|transactionStore
argument_list|)
expr_stmt|;
block|}
name|destination
operator|.
name|addMessage
argument_list|(
name|context
argument_list|,
name|message
argument_list|)
expr_stmt|;
block|}
name|Future
argument_list|<
name|Object
argument_list|>
name|asyncAddQueueMessage
parameter_list|(
specifier|final
name|TransactionStore
name|transactionStore
parameter_list|,
name|ConnectionContext
name|context
parameter_list|,
specifier|final
name|MessageStore
name|destination
parameter_list|,
specifier|final
name|Message
name|message
parameter_list|)
throws|throws
name|IOException
block|{
if|if
condition|(
name|message
operator|.
name|getTransactionId
argument_list|()
operator|!=
literal|null
condition|)
block|{
name|getTx
argument_list|(
name|message
operator|.
name|getTransactionId
argument_list|()
argument_list|)
operator|.
name|trackStore
argument_list|(
name|transactionStore
argument_list|)
expr_stmt|;
name|destination
operator|.
name|addMessage
argument_list|(
name|context
argument_list|,
name|message
argument_list|)
expr_stmt|;
return|return
name|AbstractMessageStore
operator|.
name|FUTURE
return|;
block|}
else|else
block|{
return|return
name|destination
operator|.
name|asyncAddQueueMessage
argument_list|(
name|context
argument_list|,
name|message
argument_list|)
return|;
block|}
block|}
name|Future
argument_list|<
name|Object
argument_list|>
name|asyncAddTopicMessage
parameter_list|(
specifier|final
name|TransactionStore
name|transactionStore
parameter_list|,
name|ConnectionContext
name|context
parameter_list|,
specifier|final
name|MessageStore
name|destination
parameter_list|,
specifier|final
name|Message
name|message
parameter_list|)
throws|throws
name|IOException
block|{
if|if
condition|(
name|message
operator|.
name|getTransactionId
argument_list|()
operator|!=
literal|null
condition|)
block|{
name|getTx
argument_list|(
name|message
operator|.
name|getTransactionId
argument_list|()
argument_list|)
operator|.
name|trackStore
argument_list|(
name|transactionStore
argument_list|)
expr_stmt|;
name|destination
operator|.
name|addMessage
argument_list|(
name|context
argument_list|,
name|message
argument_list|)
expr_stmt|;
return|return
name|AbstractMessageStore
operator|.
name|FUTURE
return|;
block|}
else|else
block|{
return|return
name|destination
operator|.
name|asyncAddTopicMessage
argument_list|(
name|context
argument_list|,
name|message
argument_list|)
return|;
block|}
block|}
specifier|final
name|void
name|removeMessage
parameter_list|(
specifier|final
name|TransactionStore
name|transactionStore
parameter_list|,
name|ConnectionContext
name|context
parameter_list|,
specifier|final
name|MessageStore
name|destination
parameter_list|,
specifier|final
name|MessageAck
name|ack
parameter_list|)
throws|throws
name|IOException
block|{
if|if
condition|(
name|ack
operator|.
name|getTransactionId
argument_list|()
operator|!=
literal|null
condition|)
block|{
name|getTx
argument_list|(
name|ack
operator|.
name|getTransactionId
argument_list|()
argument_list|)
operator|.
name|trackStore
argument_list|(
name|transactionStore
argument_list|)
expr_stmt|;
block|}
name|destination
operator|.
name|removeMessage
argument_list|(
name|context
argument_list|,
name|ack
argument_list|)
expr_stmt|;
block|}
specifier|final
name|void
name|removeAsyncMessage
parameter_list|(
specifier|final
name|TransactionStore
name|transactionStore
parameter_list|,
name|ConnectionContext
name|context
parameter_list|,
specifier|final
name|MessageStore
name|destination
parameter_list|,
specifier|final
name|MessageAck
name|ack
parameter_list|)
throws|throws
name|IOException
block|{
if|if
condition|(
name|ack
operator|.
name|getTransactionId
argument_list|()
operator|!=
literal|null
condition|)
block|{
name|getTx
argument_list|(
name|ack
operator|.
name|getTransactionId
argument_list|()
argument_list|)
operator|.
name|trackStore
argument_list|(
name|transactionStore
argument_list|)
expr_stmt|;
block|}
name|destination
operator|.
name|removeAsyncMessage
argument_list|(
name|context
argument_list|,
name|ack
argument_list|)
expr_stmt|;
block|}
specifier|final
name|void
name|acknowledge
parameter_list|(
specifier|final
name|TransactionStore
name|transactionStore
parameter_list|,
name|ConnectionContext
name|context
parameter_list|,
specifier|final
name|TopicMessageStore
name|destination
parameter_list|,
specifier|final
name|String
name|clientId
parameter_list|,
specifier|final
name|String
name|subscriptionName
parameter_list|,
specifier|final
name|MessageId
name|messageId
parameter_list|,
specifier|final
name|MessageAck
name|ack
parameter_list|)
throws|throws
name|IOException
block|{
if|if
condition|(
name|ack
operator|.
name|getTransactionId
argument_list|()
operator|!=
literal|null
condition|)
block|{
name|getTx
argument_list|(
name|ack
operator|.
name|getTransactionId
argument_list|()
argument_list|)
operator|.
name|trackStore
argument_list|(
name|transactionStore
argument_list|)
expr_stmt|;
block|}
name|destination
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
block|}
end_class

end_unit

