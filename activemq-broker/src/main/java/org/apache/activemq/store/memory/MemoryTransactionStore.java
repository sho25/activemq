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
name|ConcurrentMap
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
name|InlineListenableFuture
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
name|ListenableFuture
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
name|PersistenceAdapter
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

begin_comment
comment|/**  * Provides a TransactionStore implementation that can create transaction aware  * MessageStore objects from non transaction aware MessageStore objects.  *  *  */
end_comment

begin_class
specifier|public
class|class
name|MemoryTransactionStore
implements|implements
name|TransactionStore
block|{
specifier|protected
name|ConcurrentMap
argument_list|<
name|Object
argument_list|,
name|Tx
argument_list|>
name|inflightTransactions
init|=
operator|new
name|ConcurrentHashMap
argument_list|<
name|Object
argument_list|,
name|Tx
argument_list|>
argument_list|()
decl_stmt|;
specifier|protected
name|Map
argument_list|<
name|TransactionId
argument_list|,
name|Tx
argument_list|>
name|preparedTransactions
init|=
name|Collections
operator|.
name|synchronizedMap
argument_list|(
operator|new
name|LinkedHashMap
argument_list|<
name|TransactionId
argument_list|,
name|Tx
argument_list|>
argument_list|()
argument_list|)
decl_stmt|;
specifier|protected
specifier|final
name|PersistenceAdapter
name|persistenceAdapter
decl_stmt|;
specifier|private
name|boolean
name|doingRecover
decl_stmt|;
specifier|public
class|class
name|Tx
block|{
specifier|public
name|ArrayList
argument_list|<
name|AddMessageCommand
argument_list|>
name|messages
init|=
operator|new
name|ArrayList
argument_list|<
name|AddMessageCommand
argument_list|>
argument_list|()
decl_stmt|;
specifier|public
specifier|final
name|ArrayList
argument_list|<
name|RemoveMessageCommand
argument_list|>
name|acks
init|=
operator|new
name|ArrayList
argument_list|<
name|RemoveMessageCommand
argument_list|>
argument_list|()
decl_stmt|;
specifier|public
name|void
name|add
parameter_list|(
name|AddMessageCommand
name|msg
parameter_list|)
block|{
name|messages
operator|.
name|add
argument_list|(
name|msg
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|add
parameter_list|(
name|RemoveMessageCommand
name|ack
parameter_list|)
block|{
name|acks
operator|.
name|add
argument_list|(
name|ack
argument_list|)
expr_stmt|;
block|}
specifier|public
name|Message
index|[]
name|getMessages
parameter_list|()
block|{
name|Message
name|rc
index|[]
init|=
operator|new
name|Message
index|[
name|messages
operator|.
name|size
argument_list|()
index|]
decl_stmt|;
name|int
name|count
init|=
literal|0
decl_stmt|;
for|for
control|(
name|Iterator
argument_list|<
name|AddMessageCommand
argument_list|>
name|iter
init|=
name|messages
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
name|AddMessageCommand
name|cmd
init|=
name|iter
operator|.
name|next
argument_list|()
decl_stmt|;
name|rc
index|[
name|count
operator|++
index|]
operator|=
name|cmd
operator|.
name|getMessage
argument_list|()
expr_stmt|;
block|}
return|return
name|rc
return|;
block|}
specifier|public
name|MessageAck
index|[]
name|getAcks
parameter_list|()
block|{
name|MessageAck
name|rc
index|[]
init|=
operator|new
name|MessageAck
index|[
name|acks
operator|.
name|size
argument_list|()
index|]
decl_stmt|;
name|int
name|count
init|=
literal|0
decl_stmt|;
for|for
control|(
name|Iterator
argument_list|<
name|RemoveMessageCommand
argument_list|>
name|iter
init|=
name|acks
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
name|RemoveMessageCommand
name|cmd
init|=
name|iter
operator|.
name|next
argument_list|()
decl_stmt|;
name|rc
index|[
name|count
operator|++
index|]
operator|=
name|cmd
operator|.
name|getMessageAck
argument_list|()
expr_stmt|;
block|}
return|return
name|rc
return|;
block|}
comment|/**          * @throws IOException          */
specifier|public
name|void
name|commit
parameter_list|()
throws|throws
name|IOException
block|{
name|ConnectionContext
name|ctx
init|=
operator|new
name|ConnectionContext
argument_list|()
decl_stmt|;
name|persistenceAdapter
operator|.
name|beginTransaction
argument_list|(
name|ctx
argument_list|)
expr_stmt|;
try|try
block|{
comment|// Do all the message adds.
for|for
control|(
name|Iterator
argument_list|<
name|AddMessageCommand
argument_list|>
name|iter
init|=
name|messages
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
name|AddMessageCommand
name|cmd
init|=
name|iter
operator|.
name|next
argument_list|()
decl_stmt|;
name|cmd
operator|.
name|run
argument_list|(
name|ctx
argument_list|)
expr_stmt|;
block|}
comment|// And removes..
for|for
control|(
name|Iterator
argument_list|<
name|RemoveMessageCommand
argument_list|>
name|iter
init|=
name|acks
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
name|RemoveMessageCommand
name|cmd
init|=
name|iter
operator|.
name|next
argument_list|()
decl_stmt|;
name|cmd
operator|.
name|run
argument_list|(
name|ctx
argument_list|)
expr_stmt|;
block|}
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
name|persistenceAdapter
operator|.
name|rollbackTransaction
argument_list|(
name|ctx
argument_list|)
expr_stmt|;
throw|throw
name|e
throw|;
block|}
name|persistenceAdapter
operator|.
name|commitTransaction
argument_list|(
name|ctx
argument_list|)
expr_stmt|;
block|}
block|}
specifier|public
interface|interface
name|AddMessageCommand
block|{
name|Message
name|getMessage
parameter_list|()
function_decl|;
name|MessageStore
name|getMessageStore
parameter_list|()
function_decl|;
name|void
name|run
parameter_list|(
name|ConnectionContext
name|context
parameter_list|)
throws|throws
name|IOException
function_decl|;
name|void
name|setMessageStore
parameter_list|(
name|MessageStore
name|messageStore
parameter_list|)
function_decl|;
block|}
specifier|public
interface|interface
name|RemoveMessageCommand
block|{
name|MessageAck
name|getMessageAck
parameter_list|()
function_decl|;
name|void
name|run
parameter_list|(
name|ConnectionContext
name|context
parameter_list|)
throws|throws
name|IOException
function_decl|;
name|MessageStore
name|getMessageStore
parameter_list|()
function_decl|;
block|}
specifier|public
name|MemoryTransactionStore
parameter_list|(
name|PersistenceAdapter
name|persistenceAdapter
parameter_list|)
block|{
name|this
operator|.
name|persistenceAdapter
operator|=
name|persistenceAdapter
expr_stmt|;
block|}
specifier|public
name|MessageStore
name|proxy
parameter_list|(
name|MessageStore
name|messageStore
parameter_list|)
block|{
name|ProxyMessageStore
name|proxyMessageStore
init|=
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
name|MemoryTransactionStore
operator|.
name|this
operator|.
name|addMessage
argument_list|(
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
name|canOptimize
parameter_list|)
throws|throws
name|IOException
block|{
name|MemoryTransactionStore
operator|.
name|this
operator|.
name|addMessage
argument_list|(
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
name|MemoryTransactionStore
operator|.
name|this
operator|.
name|addMessage
argument_list|(
name|context
argument_list|,
name|getDelegate
argument_list|()
argument_list|,
name|message
argument_list|)
expr_stmt|;
return|return
operator|new
name|InlineListenableFuture
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
name|asyncAddQueueMessage
parameter_list|(
name|ConnectionContext
name|context
parameter_list|,
name|Message
name|message
parameter_list|,
name|boolean
name|canoptimize
parameter_list|)
throws|throws
name|IOException
block|{
name|MemoryTransactionStore
operator|.
name|this
operator|.
name|addMessage
argument_list|(
name|context
argument_list|,
name|getDelegate
argument_list|()
argument_list|,
name|message
argument_list|)
expr_stmt|;
return|return
operator|new
name|InlineListenableFuture
argument_list|()
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
name|MemoryTransactionStore
operator|.
name|this
operator|.
name|removeMessage
argument_list|(
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
name|MemoryTransactionStore
operator|.
name|this
operator|.
name|removeMessage
argument_list|(
name|getDelegate
argument_list|()
argument_list|,
name|ack
argument_list|)
expr_stmt|;
block|}
block|}
decl_stmt|;
name|onProxyQueueStore
argument_list|(
name|proxyMessageStore
argument_list|)
expr_stmt|;
return|return
name|proxyMessageStore
return|;
block|}
specifier|protected
name|void
name|onProxyQueueStore
parameter_list|(
name|ProxyMessageStore
name|proxyMessageStore
parameter_list|)
block|{     }
specifier|public
name|TopicMessageStore
name|proxy
parameter_list|(
name|TopicMessageStore
name|messageStore
parameter_list|)
block|{
name|ProxyTopicMessageStore
name|proxyTopicMessageStore
init|=
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
parameter_list|)
throws|throws
name|IOException
block|{
name|MemoryTransactionStore
operator|.
name|this
operator|.
name|addMessage
argument_list|(
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
name|canOptimize
parameter_list|)
throws|throws
name|IOException
block|{
name|MemoryTransactionStore
operator|.
name|this
operator|.
name|addMessage
argument_list|(
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
name|MemoryTransactionStore
operator|.
name|this
operator|.
name|addMessage
argument_list|(
name|context
argument_list|,
name|getDelegate
argument_list|()
argument_list|,
name|message
argument_list|)
expr_stmt|;
return|return
operator|new
name|InlineListenableFuture
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
parameter_list|,
name|boolean
name|canOptimize
parameter_list|)
throws|throws
name|IOException
block|{
name|MemoryTransactionStore
operator|.
name|this
operator|.
name|addMessage
argument_list|(
name|context
argument_list|,
name|getDelegate
argument_list|()
argument_list|,
name|message
argument_list|)
expr_stmt|;
return|return
operator|new
name|InlineListenableFuture
argument_list|()
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
name|MemoryTransactionStore
operator|.
name|this
operator|.
name|removeMessage
argument_list|(
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
name|MemoryTransactionStore
operator|.
name|this
operator|.
name|removeMessage
argument_list|(
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
name|MemoryTransactionStore
operator|.
name|this
operator|.
name|acknowledge
argument_list|(
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
decl_stmt|;
name|onProxyTopicStore
argument_list|(
name|proxyTopicMessageStore
argument_list|)
expr_stmt|;
return|return
name|proxyTopicMessageStore
return|;
block|}
specifier|protected
name|void
name|onProxyTopicStore
parameter_list|(
name|ProxyTopicMessageStore
name|proxyTopicMessageStore
parameter_list|)
block|{     }
comment|/**      * @see org.apache.activemq.store.TransactionStore#prepare(TransactionId)      */
annotation|@
name|Override
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
name|inflightTransactions
operator|.
name|remove
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
return|return;
block|}
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
specifier|public
name|Tx
name|getTx
parameter_list|(
name|Object
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
name|getPreparedTx
parameter_list|(
name|TransactionId
name|txid
parameter_list|)
block|{
name|Tx
name|tx
init|=
name|preparedTransactions
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
return|return
name|tx
return|;
block|}
annotation|@
name|Override
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
decl_stmt|;
if|if
condition|(
name|wasPrepared
condition|)
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
else|else
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
name|tx
operator|.
name|commit
argument_list|()
expr_stmt|;
block|}
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
comment|/**      * @see org.apache.activemq.store.TransactionStore#rollback(TransactionId)      */
annotation|@
name|Override
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
name|preparedTransactions
operator|.
name|remove
argument_list|(
name|txid
argument_list|)
expr_stmt|;
name|inflightTransactions
operator|.
name|remove
argument_list|(
name|txid
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
block|{     }
annotation|@
name|Override
specifier|public
name|void
name|stop
parameter_list|()
throws|throws
name|Exception
block|{     }
annotation|@
name|Override
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
comment|// All the inflight transactions get rolled back..
name|inflightTransactions
operator|.
name|clear
argument_list|()
expr_stmt|;
name|this
operator|.
name|doingRecover
operator|=
literal|true
expr_stmt|;
try|try
block|{
for|for
control|(
name|Iterator
argument_list|<
name|TransactionId
argument_list|>
name|iter
init|=
name|preparedTransactions
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
name|Tx
name|tx
init|=
name|preparedTransactions
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
name|onRecovered
argument_list|(
name|tx
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
specifier|protected
name|void
name|onRecovered
parameter_list|(
name|Tx
name|tx
parameter_list|)
block|{     }
comment|/**      * @param message      * @throws IOException      */
name|void
name|addMessage
parameter_list|(
specifier|final
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
name|doingRecover
condition|)
block|{
return|return;
block|}
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
name|Tx
name|tx
init|=
name|getTx
argument_list|(
name|message
operator|.
name|getTransactionId
argument_list|()
argument_list|)
decl_stmt|;
name|tx
operator|.
name|add
argument_list|(
operator|new
name|AddMessageCommand
argument_list|()
block|{
name|MessageStore
name|messageStore
init|=
name|destination
decl_stmt|;
annotation|@
name|Override
specifier|public
name|Message
name|getMessage
parameter_list|()
block|{
return|return
name|message
return|;
block|}
annotation|@
name|Override
specifier|public
name|MessageStore
name|getMessageStore
parameter_list|()
block|{
return|return
name|destination
return|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|run
parameter_list|(
name|ConnectionContext
name|ctx
parameter_list|)
throws|throws
name|IOException
block|{
name|destination
operator|.
name|addMessage
argument_list|(
name|ctx
argument_list|,
name|message
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|setMessageStore
parameter_list|(
name|MessageStore
name|messageStore
parameter_list|)
block|{
name|this
operator|.
name|messageStore
operator|=
name|messageStore
expr_stmt|;
block|}
block|}
argument_list|)
expr_stmt|;
block|}
else|else
block|{
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
block|}
comment|/**      * @param ack      * @throws IOException      */
specifier|final
name|void
name|removeMessage
parameter_list|(
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
name|doingRecover
condition|)
block|{
return|return;
block|}
if|if
condition|(
name|ack
operator|.
name|isInTransaction
argument_list|()
condition|)
block|{
name|Tx
name|tx
init|=
name|getTx
argument_list|(
name|ack
operator|.
name|getTransactionId
argument_list|()
argument_list|)
decl_stmt|;
name|tx
operator|.
name|add
argument_list|(
operator|new
name|RemoveMessageCommand
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|MessageAck
name|getMessageAck
parameter_list|()
block|{
return|return
name|ack
return|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|run
parameter_list|(
name|ConnectionContext
name|ctx
parameter_list|)
throws|throws
name|IOException
block|{
name|destination
operator|.
name|removeMessage
argument_list|(
name|ctx
argument_list|,
name|ack
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|MessageStore
name|getMessageStore
parameter_list|()
block|{
return|return
name|destination
return|;
block|}
block|}
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|destination
operator|.
name|removeMessage
argument_list|(
literal|null
argument_list|,
name|ack
argument_list|)
expr_stmt|;
block|}
block|}
specifier|public
name|void
name|acknowledge
parameter_list|(
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
name|doingRecover
condition|)
block|{
return|return;
block|}
if|if
condition|(
name|ack
operator|.
name|isInTransaction
argument_list|()
condition|)
block|{
name|Tx
name|tx
init|=
name|getTx
argument_list|(
name|ack
operator|.
name|getTransactionId
argument_list|()
argument_list|)
decl_stmt|;
name|tx
operator|.
name|add
argument_list|(
operator|new
name|RemoveMessageCommand
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|MessageAck
name|getMessageAck
parameter_list|()
block|{
return|return
name|ack
return|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|run
parameter_list|(
name|ConnectionContext
name|ctx
parameter_list|)
throws|throws
name|IOException
block|{
name|destination
operator|.
name|acknowledge
argument_list|(
name|ctx
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
name|MessageStore
name|getMessageStore
parameter_list|()
block|{
return|return
name|destination
return|;
block|}
block|}
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|destination
operator|.
name|acknowledge
argument_list|(
literal|null
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
specifier|public
name|void
name|delete
parameter_list|()
block|{
name|inflightTransactions
operator|.
name|clear
argument_list|()
expr_stmt|;
name|preparedTransactions
operator|.
name|clear
argument_list|()
expr_stmt|;
name|doingRecover
operator|=
literal|false
expr_stmt|;
block|}
block|}
end_class

end_unit

