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
name|JournalTransaction
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

begin_comment
comment|/**  */
end_comment

begin_class
specifier|public
class|class
name|AMQTransactionStore
implements|implements
name|TransactionStore
block|{
specifier|private
specifier|final
name|AMQPersistenceAdapter
name|peristenceAdapter
decl_stmt|;
name|Map
argument_list|<
name|TransactionId
argument_list|,
name|AMQTx
argument_list|>
name|inflightTransactions
init|=
operator|new
name|LinkedHashMap
argument_list|<
name|TransactionId
argument_list|,
name|AMQTx
argument_list|>
argument_list|()
decl_stmt|;
name|Map
argument_list|<
name|TransactionId
argument_list|,
name|AMQTx
argument_list|>
name|preparedTransactions
init|=
operator|new
name|LinkedHashMap
argument_list|<
name|TransactionId
argument_list|,
name|AMQTx
argument_list|>
argument_list|()
decl_stmt|;
specifier|private
name|boolean
name|doingRecover
decl_stmt|;
specifier|public
name|AMQTransactionStore
parameter_list|(
name|AMQPersistenceAdapter
name|adapter
parameter_list|)
block|{
name|this
operator|.
name|peristenceAdapter
operator|=
name|adapter
expr_stmt|;
block|}
comment|/**      * @throws IOException      * @see org.apache.activemq.store.TransactionStore#prepare(TransactionId)      */
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
name|AMQTx
name|tx
init|=
literal|null
decl_stmt|;
synchronized|synchronized
init|(
name|inflightTransactions
init|)
block|{
name|tx
operator|=
name|inflightTransactions
operator|.
name|remove
argument_list|(
name|txid
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|tx
operator|==
literal|null
condition|)
block|{
return|return;
block|}
name|peristenceAdapter
operator|.
name|writeCommand
argument_list|(
operator|new
name|JournalTransaction
argument_list|(
name|JournalTransaction
operator|.
name|XA_PREPARE
argument_list|,
name|txid
argument_list|,
literal|false
argument_list|)
argument_list|,
literal|true
argument_list|)
expr_stmt|;
synchronized|synchronized
init|(
name|preparedTransactions
init|)
block|{
name|preparedTransactions
operator|.
name|put
argument_list|(
name|txid
argument_list|,
name|tx
argument_list|)
expr_stmt|;
block|}
block|}
comment|/**      * @throws IOException      * @see org.apache.activemq.store.TransactionStore#prepare(TransactionId)      */
specifier|public
name|void
name|replayPrepare
parameter_list|(
name|TransactionId
name|txid
parameter_list|)
throws|throws
name|IOException
block|{
name|AMQTx
name|tx
init|=
literal|null
decl_stmt|;
synchronized|synchronized
init|(
name|inflightTransactions
init|)
block|{
name|tx
operator|=
name|inflightTransactions
operator|.
name|remove
argument_list|(
name|txid
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|tx
operator|==
literal|null
condition|)
block|{
return|return;
block|}
synchronized|synchronized
init|(
name|preparedTransactions
init|)
block|{
name|preparedTransactions
operator|.
name|put
argument_list|(
name|txid
argument_list|,
name|tx
argument_list|)
expr_stmt|;
block|}
block|}
specifier|public
name|AMQTx
name|getTx
parameter_list|(
name|TransactionId
name|txid
parameter_list|,
name|Location
name|location
parameter_list|)
block|{
name|AMQTx
name|tx
init|=
literal|null
decl_stmt|;
synchronized|synchronized
init|(
name|inflightTransactions
init|)
block|{
name|tx
operator|=
name|inflightTransactions
operator|.
name|get
argument_list|(
name|txid
argument_list|)
expr_stmt|;
block|}
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
name|AMQTx
argument_list|(
name|location
argument_list|)
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
comment|/**      * @throws XAException      * @see org.apache.activemq.store.TransactionStore#commit(org.apache.activemq.service.Transaction)      */
specifier|public
name|void
name|commit
parameter_list|(
name|TransactionId
name|txid
parameter_list|,
name|boolean
name|wasPrepared
parameter_list|)
throws|throws
name|IOException
block|{
name|AMQTx
name|tx
decl_stmt|;
if|if
condition|(
name|wasPrepared
condition|)
block|{
synchronized|synchronized
init|(
name|preparedTransactions
init|)
block|{
name|tx
operator|=
name|preparedTransactions
operator|.
name|remove
argument_list|(
name|txid
argument_list|)
expr_stmt|;
block|}
block|}
else|else
block|{
synchronized|synchronized
init|(
name|inflightTransactions
init|)
block|{
name|tx
operator|=
name|inflightTransactions
operator|.
name|remove
argument_list|(
name|txid
argument_list|)
expr_stmt|;
block|}
block|}
if|if
condition|(
name|tx
operator|==
literal|null
condition|)
block|{
return|return;
block|}
if|if
condition|(
name|txid
operator|.
name|isXATransaction
argument_list|()
condition|)
block|{
name|peristenceAdapter
operator|.
name|writeCommand
argument_list|(
operator|new
name|JournalTransaction
argument_list|(
name|JournalTransaction
operator|.
name|XA_COMMIT
argument_list|,
name|txid
argument_list|,
name|wasPrepared
argument_list|)
argument_list|,
literal|true
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|peristenceAdapter
operator|.
name|writeCommand
argument_list|(
operator|new
name|JournalTransaction
argument_list|(
name|JournalTransaction
operator|.
name|LOCAL_COMMIT
argument_list|,
name|txid
argument_list|,
name|wasPrepared
argument_list|)
argument_list|,
literal|true
argument_list|)
expr_stmt|;
block|}
block|}
comment|/**      * @throws XAException      * @see org.apache.activemq.store.TransactionStore#commit(org.apache.activemq.service.Transaction)      */
specifier|public
name|AMQTx
name|replayCommit
parameter_list|(
name|TransactionId
name|txid
parameter_list|,
name|boolean
name|wasPrepared
parameter_list|)
throws|throws
name|IOException
block|{
if|if
condition|(
name|wasPrepared
condition|)
block|{
synchronized|synchronized
init|(
name|preparedTransactions
init|)
block|{
return|return
name|preparedTransactions
operator|.
name|remove
argument_list|(
name|txid
argument_list|)
return|;
block|}
block|}
else|else
block|{
synchronized|synchronized
init|(
name|inflightTransactions
init|)
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
block|}
block|}
comment|/**      * @throws IOException      * @see org.apache.activemq.store.TransactionStore#rollback(TransactionId)      */
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
name|AMQTx
name|tx
init|=
literal|null
decl_stmt|;
synchronized|synchronized
init|(
name|inflightTransactions
init|)
block|{
name|tx
operator|=
name|inflightTransactions
operator|.
name|remove
argument_list|(
name|txid
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|tx
operator|!=
literal|null
condition|)
block|{
synchronized|synchronized
init|(
name|preparedTransactions
init|)
block|{
name|tx
operator|=
name|preparedTransactions
operator|.
name|remove
argument_list|(
name|txid
argument_list|)
expr_stmt|;
block|}
block|}
if|if
condition|(
name|tx
operator|!=
literal|null
condition|)
block|{
if|if
condition|(
name|txid
operator|.
name|isXATransaction
argument_list|()
condition|)
block|{
name|peristenceAdapter
operator|.
name|writeCommand
argument_list|(
operator|new
name|JournalTransaction
argument_list|(
name|JournalTransaction
operator|.
name|XA_ROLLBACK
argument_list|,
name|txid
argument_list|,
literal|false
argument_list|)
argument_list|,
literal|true
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|peristenceAdapter
operator|.
name|writeCommand
argument_list|(
operator|new
name|JournalTransaction
argument_list|(
name|JournalTransaction
operator|.
name|LOCAL_ROLLBACK
argument_list|,
name|txid
argument_list|,
literal|false
argument_list|)
argument_list|,
literal|true
argument_list|)
expr_stmt|;
block|}
block|}
block|}
comment|/**      * @throws IOException      * @see org.apache.activemq.store.TransactionStore#rollback(TransactionId)      */
specifier|public
name|void
name|replayRollback
parameter_list|(
name|TransactionId
name|txid
parameter_list|)
throws|throws
name|IOException
block|{
name|boolean
name|inflight
init|=
literal|false
decl_stmt|;
synchronized|synchronized
init|(
name|inflightTransactions
init|)
block|{
name|inflight
operator|=
name|inflightTransactions
operator|.
name|remove
argument_list|(
name|txid
argument_list|)
operator|!=
literal|null
expr_stmt|;
block|}
if|if
condition|(
name|inflight
condition|)
block|{
synchronized|synchronized
init|(
name|preparedTransactions
init|)
block|{
name|preparedTransactions
operator|.
name|remove
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
block|{     }
specifier|public
name|void
name|stop
parameter_list|()
throws|throws
name|Exception
block|{     }
specifier|public
specifier|synchronized
name|void
name|recover
parameter_list|(
name|TransactionRecoveryListener
name|listener
parameter_list|)
throws|throws
name|IOException
block|{
comment|// All the in-flight transactions get rolled back..
synchronized|synchronized
init|(
name|inflightTransactions
init|)
block|{
name|inflightTransactions
operator|.
name|clear
argument_list|()
expr_stmt|;
block|}
name|this
operator|.
name|doingRecover
operator|=
literal|true
expr_stmt|;
try|try
block|{
name|Map
argument_list|<
name|TransactionId
argument_list|,
name|AMQTx
argument_list|>
name|txs
init|=
literal|null
decl_stmt|;
synchronized|synchronized
init|(
name|preparedTransactions
init|)
block|{
name|txs
operator|=
operator|new
name|LinkedHashMap
argument_list|<
name|TransactionId
argument_list|,
name|AMQTx
argument_list|>
argument_list|(
name|preparedTransactions
argument_list|)
expr_stmt|;
block|}
for|for
control|(
name|Iterator
argument_list|<
name|TransactionId
argument_list|>
name|iter
init|=
name|txs
operator|.
name|keySet
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
name|Object
name|txid
init|=
name|iter
operator|.
name|next
argument_list|()
decl_stmt|;
name|AMQTx
name|tx
init|=
name|txs
operator|.
name|get
argument_list|(
name|txid
argument_list|)
decl_stmt|;
name|listener
operator|.
name|recover
argument_list|(
operator|(
name|XATransactionId
operator|)
name|txid
argument_list|,
name|tx
operator|.
name|getMessages
argument_list|()
argument_list|,
name|tx
operator|.
name|getAcks
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
finally|finally
block|{
name|this
operator|.
name|doingRecover
operator|=
literal|false
expr_stmt|;
block|}
block|}
comment|/**      * @param message      * @throws IOException      */
name|void
name|addMessage
parameter_list|(
name|AMQMessageStore
name|store
parameter_list|,
name|Message
name|message
parameter_list|,
name|Location
name|location
parameter_list|)
throws|throws
name|IOException
block|{
name|AMQTx
name|tx
init|=
name|getTx
argument_list|(
name|message
operator|.
name|getTransactionId
argument_list|()
argument_list|,
name|location
argument_list|)
decl_stmt|;
name|tx
operator|.
name|add
argument_list|(
name|store
argument_list|,
name|message
argument_list|,
name|location
argument_list|)
expr_stmt|;
block|}
comment|/**      * @param ack      * @throws IOException      */
specifier|public
name|void
name|removeMessage
parameter_list|(
name|AMQMessageStore
name|store
parameter_list|,
name|MessageAck
name|ack
parameter_list|,
name|Location
name|location
parameter_list|)
throws|throws
name|IOException
block|{
name|AMQTx
name|tx
init|=
name|getTx
argument_list|(
name|ack
operator|.
name|getTransactionId
argument_list|()
argument_list|,
name|location
argument_list|)
decl_stmt|;
name|tx
operator|.
name|add
argument_list|(
name|store
argument_list|,
name|ack
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|acknowledge
parameter_list|(
name|AMQTopicMessageStore
name|store
parameter_list|,
name|JournalTopicAck
name|ack
parameter_list|,
name|Location
name|location
parameter_list|)
block|{
name|AMQTx
name|tx
init|=
name|getTx
argument_list|(
name|ack
operator|.
name|getTransactionId
argument_list|()
argument_list|,
name|location
argument_list|)
decl_stmt|;
name|tx
operator|.
name|add
argument_list|(
name|store
argument_list|,
name|ack
argument_list|)
expr_stmt|;
block|}
specifier|public
name|Location
name|checkpoint
parameter_list|()
throws|throws
name|IOException
block|{
comment|// Nothing really to checkpoint.. since, we don't
comment|// checkpoint tx operations in to long term store until they are
comment|// committed.
comment|// But we keep track of the first location of an operation
comment|// that was associated with an active tx. The journal can not
comment|// roll over active tx records.
name|Location
name|rc
init|=
literal|null
decl_stmt|;
synchronized|synchronized
init|(
name|inflightTransactions
init|)
block|{
for|for
control|(
name|Iterator
argument_list|<
name|AMQTx
argument_list|>
name|iter
init|=
name|inflightTransactions
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
name|AMQTx
name|tx
init|=
name|iter
operator|.
name|next
argument_list|()
decl_stmt|;
name|Location
name|location
init|=
name|tx
operator|.
name|getLocation
argument_list|()
decl_stmt|;
if|if
condition|(
name|rc
operator|==
literal|null
operator|||
name|rc
operator|.
name|compareTo
argument_list|(
name|location
argument_list|)
operator|<
literal|0
condition|)
block|{
name|rc
operator|=
name|location
expr_stmt|;
block|}
block|}
block|}
synchronized|synchronized
init|(
name|preparedTransactions
init|)
block|{
for|for
control|(
name|Iterator
argument_list|<
name|AMQTx
argument_list|>
name|iter
init|=
name|preparedTransactions
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
name|AMQTx
name|tx
init|=
name|iter
operator|.
name|next
argument_list|()
decl_stmt|;
name|Location
name|location
init|=
name|tx
operator|.
name|getLocation
argument_list|()
decl_stmt|;
if|if
condition|(
name|rc
operator|==
literal|null
operator|||
name|rc
operator|.
name|compareTo
argument_list|(
name|location
argument_list|)
operator|<
literal|0
condition|)
block|{
name|rc
operator|=
name|location
expr_stmt|;
block|}
block|}
return|return
name|rc
return|;
block|}
block|}
specifier|public
name|boolean
name|isDoingRecover
parameter_list|()
block|{
return|return
name|doingRecover
return|;
block|}
comment|/**      * @return the preparedTransactions      */
specifier|public
name|Map
argument_list|<
name|TransactionId
argument_list|,
name|AMQTx
argument_list|>
name|getPreparedTransactions
parameter_list|()
block|{
return|return
name|this
operator|.
name|preparedTransactions
return|;
block|}
comment|/**      * @param preparedTransactions the preparedTransactions to set      */
specifier|public
name|void
name|setPreparedTransactions
parameter_list|(
name|Map
argument_list|<
name|TransactionId
argument_list|,
name|AMQTx
argument_list|>
name|preparedTransactions
parameter_list|)
block|{
if|if
condition|(
name|preparedTransactions
operator|!=
literal|null
condition|)
block|{
name|this
operator|.
name|preparedTransactions
operator|.
name|clear
argument_list|()
expr_stmt|;
name|this
operator|.
name|preparedTransactions
operator|.
name|putAll
argument_list|(
name|preparedTransactions
argument_list|)
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

