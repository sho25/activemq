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
name|Iterator
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|transaction
operator|.
name|xa
operator|.
name|XAException
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
name|java
operator|.
name|util
operator|.
name|concurrent
operator|.
name|ConcurrentHashMap
import|;
end_import

begin_comment
comment|/**  * Provides a TransactionStore implementation that can create transaction aware  * MessageStore objects from non transaction aware MessageStore objects.  *   * @version $Revision: 1.4 $  */
end_comment

begin_class
specifier|public
class|class
name|MemoryTransactionStore
implements|implements
name|TransactionStore
block|{
name|ConcurrentHashMap
name|inflightTransactions
init|=
operator|new
name|ConcurrentHashMap
argument_list|()
decl_stmt|;
name|ConcurrentHashMap
name|preparedTransactions
init|=
operator|new
name|ConcurrentHashMap
argument_list|()
decl_stmt|;
specifier|private
name|boolean
name|doingRecover
decl_stmt|;
specifier|public
specifier|static
class|class
name|Tx
block|{
specifier|private
name|ArrayList
name|messages
init|=
operator|new
name|ArrayList
argument_list|()
decl_stmt|;
specifier|private
name|ArrayList
name|acks
init|=
operator|new
name|ArrayList
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
operator|(
name|AddMessageCommand
operator|)
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
operator|(
name|RemoveMessageCommand
operator|)
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
comment|// Do all the message adds.
for|for
control|(
name|Iterator
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
operator|(
name|AddMessageCommand
operator|)
name|iter
operator|.
name|next
argument_list|()
decl_stmt|;
name|cmd
operator|.
name|run
argument_list|()
expr_stmt|;
block|}
comment|// And removes..
for|for
control|(
name|Iterator
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
operator|(
name|RemoveMessageCommand
operator|)
name|iter
operator|.
name|next
argument_list|()
decl_stmt|;
name|cmd
operator|.
name|run
argument_list|()
expr_stmt|;
block|}
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
name|void
name|run
parameter_list|()
throws|throws
name|IOException
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
parameter_list|()
throws|throws
name|IOException
function_decl|;
block|}
specifier|public
name|MessageStore
name|proxy
parameter_list|(
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
name|getDelegate
argument_list|()
argument_list|,
name|send
argument_list|)
expr_stmt|;
block|}
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
block|}
return|;
block|}
specifier|public
name|TopicMessageStore
name|proxy
parameter_list|(
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
name|getDelegate
argument_list|()
argument_list|,
name|send
argument_list|)
expr_stmt|;
block|}
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
block|}
return|;
block|}
comment|/**      * @see org.apache.activemq.store.TransactionStore#prepare(TransactionId)      */
specifier|public
name|void
name|prepare
parameter_list|(
name|TransactionId
name|txid
parameter_list|)
block|{
name|Tx
name|tx
init|=
operator|(
name|Tx
operator|)
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
return|return;
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
operator|(
name|Tx
operator|)
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
operator|(
name|Tx
operator|)
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
operator|(
name|Tx
operator|)
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
return|return;
name|tx
operator|.
name|commit
argument_list|()
expr_stmt|;
block|}
comment|/**      * @see org.apache.activemq.store.TransactionStore#rollback(TransactionId)      */
specifier|public
name|void
name|rollback
parameter_list|(
name|TransactionId
name|txid
parameter_list|)
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
specifier|synchronized
specifier|public
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
operator|(
name|Object
operator|)
name|iter
operator|.
name|next
argument_list|()
decl_stmt|;
name|Tx
name|tx
init|=
operator|(
name|Tx
operator|)
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
return|return;
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
specifier|public
name|Message
name|getMessage
parameter_list|()
block|{
return|return
name|message
return|;
block|}
specifier|public
name|void
name|run
parameter_list|()
throws|throws
name|IOException
block|{
name|destination
operator|.
name|addMessage
argument_list|(
literal|null
argument_list|,
name|message
argument_list|)
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
literal|null
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
return|return;
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
specifier|public
name|MessageAck
name|getMessageAck
parameter_list|()
block|{
return|return
name|ack
return|;
block|}
specifier|public
name|void
name|run
parameter_list|()
throws|throws
name|IOException
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

