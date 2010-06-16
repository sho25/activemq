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
name|DataInputStream
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
name|java
operator|.
name|util
operator|.
name|List
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
name|CancellationException
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
name|ExecutionException
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
name|openwire
operator|.
name|OpenWireFormat
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
name|protobuf
operator|.
name|Buffer
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
name|MessageDatabase
operator|.
name|AddOpperation
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
name|MessageDatabase
operator|.
name|Operation
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
name|MessageDatabase
operator|.
name|RemoveOpperation
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
name|KahaRollbackCommand
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
name|KahaTransactionInfo
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
comment|/**  * Provides a TransactionStore implementation that can create transaction aware  * MessageStore objects from non transaction aware MessageStore objects.  *   * @version $Revision: 1.4 $  */
end_comment

begin_class
specifier|public
class|class
name|KahaDBTransactionStore
implements|implements
name|TransactionStore
block|{
specifier|static
specifier|final
name|Log
name|LOG
init|=
name|LogFactory
operator|.
name|getLog
argument_list|(
name|KahaDBTransactionStore
operator|.
name|class
argument_list|)
decl_stmt|;
name|ConcurrentHashMap
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
specifier|private
specifier|final
name|WireFormat
name|wireFormat
init|=
operator|new
name|OpenWireFormat
argument_list|()
decl_stmt|;
specifier|private
specifier|final
name|KahaDBStore
name|theStore
decl_stmt|;
specifier|public
name|KahaDBTransactionStore
parameter_list|(
name|KahaDBStore
name|theStore
parameter_list|)
block|{
name|this
operator|.
name|theStore
operator|=
name|theStore
expr_stmt|;
block|}
specifier|public
class|class
name|Tx
block|{
specifier|private
specifier|final
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
specifier|private
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
comment|/**          * @return true if something to commit          * @throws IOException          */
specifier|public
name|List
argument_list|<
name|Future
argument_list|<
name|Object
argument_list|>
argument_list|>
name|commit
parameter_list|()
throws|throws
name|IOException
block|{
name|List
argument_list|<
name|Future
argument_list|<
name|Object
argument_list|>
argument_list|>
name|results
init|=
operator|new
name|ArrayList
argument_list|<
name|Future
argument_list|<
name|Object
argument_list|>
argument_list|>
argument_list|()
decl_stmt|;
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
name|results
operator|.
name|add
argument_list|(
name|cmd
operator|.
name|run
argument_list|()
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
argument_list|()
expr_stmt|;
name|results
operator|.
name|add
argument_list|(
name|cmd
operator|.
name|run
argument_list|()
argument_list|)
expr_stmt|;
block|}
return|return
name|results
return|;
block|}
block|}
specifier|public
specifier|abstract
class|class
name|AddMessageCommand
block|{
specifier|private
specifier|final
name|ConnectionContext
name|ctx
decl_stmt|;
name|AddMessageCommand
parameter_list|(
name|ConnectionContext
name|ctx
parameter_list|)
block|{
name|this
operator|.
name|ctx
operator|=
name|ctx
expr_stmt|;
block|}
specifier|abstract
name|Message
name|getMessage
parameter_list|()
function_decl|;
name|Future
argument_list|<
name|Object
argument_list|>
name|run
parameter_list|()
throws|throws
name|IOException
block|{
return|return
name|run
argument_list|(
name|this
operator|.
name|ctx
argument_list|)
return|;
block|}
specifier|abstract
name|Future
argument_list|<
name|Object
argument_list|>
name|run
parameter_list|(
name|ConnectionContext
name|ctx
parameter_list|)
throws|throws
name|IOException
function_decl|;
block|}
specifier|public
specifier|abstract
class|class
name|RemoveMessageCommand
block|{
specifier|private
specifier|final
name|ConnectionContext
name|ctx
decl_stmt|;
name|RemoveMessageCommand
parameter_list|(
name|ConnectionContext
name|ctx
parameter_list|)
block|{
name|this
operator|.
name|ctx
operator|=
name|ctx
expr_stmt|;
block|}
specifier|abstract
name|MessageAck
name|getMessageAck
parameter_list|()
function_decl|;
name|Future
argument_list|<
name|Object
argument_list|>
name|run
parameter_list|()
throws|throws
name|IOException
block|{
return|return
name|run
argument_list|(
name|this
operator|.
name|ctx
argument_list|)
return|;
block|}
specifier|abstract
name|Future
argument_list|<
name|Object
argument_list|>
name|run
parameter_list|(
name|ConnectionContext
name|context
parameter_list|)
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
name|KahaDBTransactionStore
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
name|KahaDBTransactionStore
operator|.
name|this
operator|.
name|asyncAddQueueMessage
argument_list|(
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
name|KahaDBTransactionStore
operator|.
name|this
operator|.
name|removeMessage
argument_list|(
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
name|KahaDBTransactionStore
operator|.
name|this
operator|.
name|removeAsyncMessage
argument_list|(
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
parameter_list|)
throws|throws
name|IOException
block|{
name|KahaDBTransactionStore
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
name|KahaDBTransactionStore
operator|.
name|this
operator|.
name|asyncAddTopicMessage
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
name|AbstractMessageStore
operator|.
name|FUTURE
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
name|KahaDBTransactionStore
operator|.
name|this
operator|.
name|removeMessage
argument_list|(
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
name|KahaDBTransactionStore
operator|.
name|this
operator|.
name|removeAsyncMessage
argument_list|(
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
name|inflightTransactions
operator|.
name|remove
argument_list|(
name|txid
argument_list|)
expr_stmt|;
name|KahaTransactionInfo
name|info
init|=
name|getTransactionInfo
argument_list|(
name|txid
argument_list|)
decl_stmt|;
name|theStore
operator|.
name|store
argument_list|(
operator|new
name|KahaPrepareCommand
argument_list|()
operator|.
name|setTransactionInfo
argument_list|(
name|info
argument_list|)
argument_list|,
literal|true
argument_list|,
literal|null
argument_list|,
literal|null
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
name|txid
operator|!=
literal|null
condition|)
block|{
if|if
condition|(
operator|!
name|txid
operator|.
name|isXATransaction
argument_list|()
operator|&&
name|theStore
operator|.
name|isConcurrentStoreAndDispatchTransactions
argument_list|()
condition|)
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
operator|!=
literal|null
condition|)
block|{
name|List
argument_list|<
name|Future
argument_list|<
name|Object
argument_list|>
argument_list|>
name|results
init|=
name|tx
operator|.
name|commit
argument_list|()
decl_stmt|;
name|boolean
name|doneSomething
init|=
literal|false
decl_stmt|;
for|for
control|(
name|Future
argument_list|<
name|Object
argument_list|>
name|result
range|:
name|results
control|)
block|{
try|try
block|{
name|result
operator|.
name|get
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|InterruptedException
name|e
parameter_list|)
block|{
name|theStore
operator|.
name|brokerService
operator|.
name|handleIOException
argument_list|(
operator|new
name|IOException
argument_list|(
name|e
argument_list|)
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|ExecutionException
name|e
parameter_list|)
block|{
name|theStore
operator|.
name|brokerService
operator|.
name|handleIOException
argument_list|(
operator|new
name|IOException
argument_list|(
name|e
argument_list|)
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|CancellationException
name|e
parameter_list|)
block|{                         }
if|if
condition|(
operator|!
name|result
operator|.
name|isCancelled
argument_list|()
condition|)
block|{
name|doneSomething
operator|=
literal|true
expr_stmt|;
block|}
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
if|if
condition|(
name|doneSomething
condition|)
block|{
name|KahaTransactionInfo
name|info
init|=
name|getTransactionInfo
argument_list|(
name|txid
argument_list|)
decl_stmt|;
name|theStore
operator|.
name|store
argument_list|(
operator|new
name|KahaCommitCommand
argument_list|()
operator|.
name|setTransactionInfo
argument_list|(
name|info
argument_list|)
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
comment|//The Tx will be null for failed over clients - lets run their post commits
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
block|}
else|else
block|{
name|KahaTransactionInfo
name|info
init|=
name|getTransactionInfo
argument_list|(
name|txid
argument_list|)
decl_stmt|;
name|theStore
operator|.
name|store
argument_list|(
operator|new
name|KahaCommitCommand
argument_list|()
operator|.
name|setTransactionInfo
argument_list|(
name|info
argument_list|)
argument_list|,
literal|true
argument_list|,
name|preCommit
argument_list|,
name|postCommit
argument_list|)
expr_stmt|;
block|}
block|}
else|else
block|{
name|LOG
operator|.
name|error
argument_list|(
literal|"Null transaction passed on commit"
argument_list|)
expr_stmt|;
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
if|if
condition|(
name|txid
operator|.
name|isXATransaction
argument_list|()
operator|||
name|theStore
operator|.
name|isConcurrentStoreAndDispatchTransactions
argument_list|()
condition|)
block|{
name|KahaTransactionInfo
name|info
init|=
name|getTransactionInfo
argument_list|(
name|txid
argument_list|)
decl_stmt|;
name|theStore
operator|.
name|store
argument_list|(
operator|new
name|KahaRollbackCommand
argument_list|()
operator|.
name|setTransactionInfo
argument_list|(
name|info
argument_list|)
argument_list|,
literal|false
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|inflightTransactions
operator|.
name|remove
argument_list|(
name|txid
argument_list|)
expr_stmt|;
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
comment|// All the inflight transactions get rolled back..
comment|// inflightTransactions.clear();
for|for
control|(
name|Map
operator|.
name|Entry
argument_list|<
name|TransactionId
argument_list|,
name|ArrayList
argument_list|<
name|Operation
argument_list|>
argument_list|>
name|entry
range|:
name|theStore
operator|.
name|preparedTransactions
operator|.
name|entrySet
argument_list|()
control|)
block|{
name|XATransactionId
name|xid
init|=
operator|(
name|XATransactionId
operator|)
name|entry
operator|.
name|getKey
argument_list|()
decl_stmt|;
name|ArrayList
argument_list|<
name|Message
argument_list|>
name|messageList
init|=
operator|new
name|ArrayList
argument_list|<
name|Message
argument_list|>
argument_list|()
decl_stmt|;
name|ArrayList
argument_list|<
name|MessageAck
argument_list|>
name|ackList
init|=
operator|new
name|ArrayList
argument_list|<
name|MessageAck
argument_list|>
argument_list|()
decl_stmt|;
for|for
control|(
name|Operation
name|op
range|:
name|entry
operator|.
name|getValue
argument_list|()
control|)
block|{
if|if
condition|(
name|op
operator|.
name|getClass
argument_list|()
operator|==
name|AddOpperation
operator|.
name|class
condition|)
block|{
name|AddOpperation
name|addOp
init|=
operator|(
name|AddOpperation
operator|)
name|op
decl_stmt|;
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
name|DataInputStream
argument_list|(
name|addOp
operator|.
name|getCommand
argument_list|()
operator|.
name|getMessage
argument_list|()
operator|.
name|newInput
argument_list|()
argument_list|)
argument_list|)
decl_stmt|;
name|messageList
operator|.
name|add
argument_list|(
name|msg
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|RemoveOpperation
name|rmOp
init|=
operator|(
name|RemoveOpperation
operator|)
name|op
decl_stmt|;
name|Buffer
name|ackb
init|=
name|rmOp
operator|.
name|getCommand
argument_list|()
operator|.
name|getAck
argument_list|()
decl_stmt|;
name|MessageAck
name|ack
init|=
operator|(
name|MessageAck
operator|)
name|wireFormat
operator|.
name|unmarshal
argument_list|(
operator|new
name|DataInputStream
argument_list|(
name|ackb
operator|.
name|newInput
argument_list|()
argument_list|)
argument_list|)
decl_stmt|;
name|ackList
operator|.
name|add
argument_list|(
name|ack
argument_list|)
expr_stmt|;
block|}
block|}
name|Message
index|[]
name|addedMessages
init|=
operator|new
name|Message
index|[
name|messageList
operator|.
name|size
argument_list|()
index|]
decl_stmt|;
name|MessageAck
index|[]
name|acks
init|=
operator|new
name|MessageAck
index|[
name|ackList
operator|.
name|size
argument_list|()
index|]
decl_stmt|;
name|messageList
operator|.
name|toArray
argument_list|(
name|addedMessages
argument_list|)
expr_stmt|;
name|ackList
operator|.
name|toArray
argument_list|(
name|acks
argument_list|)
expr_stmt|;
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
comment|/**      * @param message      * @throws IOException      */
name|void
name|addMessage
parameter_list|(
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
if|if
condition|(
name|message
operator|.
name|getTransactionId
argument_list|()
operator|.
name|isXATransaction
argument_list|()
operator|||
name|theStore
operator|.
name|isConcurrentStoreAndDispatchTransactions
argument_list|()
operator|==
literal|false
condition|)
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
else|else
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
argument_list|(
name|context
argument_list|)
block|{
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
name|Future
argument_list|<
name|Object
argument_list|>
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
return|return
name|AbstractMessageStore
operator|.
name|FUTURE
return|;
block|}
block|}
argument_list|)
expr_stmt|;
block|}
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
name|Future
argument_list|<
name|Object
argument_list|>
name|asyncAddQueueMessage
parameter_list|(
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
if|if
condition|(
name|message
operator|.
name|getTransactionId
argument_list|()
operator|.
name|isXATransaction
argument_list|()
operator|||
name|theStore
operator|.
name|isConcurrentStoreAndDispatchTransactions
argument_list|()
operator|==
literal|false
condition|)
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
return|return
name|AbstractMessageStore
operator|.
name|FUTURE
return|;
block|}
else|else
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
argument_list|(
name|context
argument_list|)
block|{
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
name|Future
argument_list|<
name|Object
argument_list|>
name|run
parameter_list|(
name|ConnectionContext
name|ctx
parameter_list|)
throws|throws
name|IOException
block|{
return|return
name|destination
operator|.
name|asyncAddQueueMessage
argument_list|(
name|ctx
argument_list|,
name|message
argument_list|)
return|;
block|}
block|}
argument_list|)
expr_stmt|;
return|return
name|AbstractMessageStore
operator|.
name|FUTURE
return|;
block|}
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
if|if
condition|(
name|message
operator|.
name|getTransactionId
argument_list|()
operator|.
name|isXATransaction
argument_list|()
operator|||
name|theStore
operator|.
name|isConcurrentStoreAndDispatchTransactions
argument_list|()
operator|==
literal|false
condition|)
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
return|return
name|AbstractMessageStore
operator|.
name|FUTURE
return|;
block|}
else|else
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
argument_list|(
name|context
argument_list|)
block|{
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
name|Future
name|run
parameter_list|(
name|ConnectionContext
name|ctx
parameter_list|)
throws|throws
name|IOException
block|{
return|return
name|destination
operator|.
name|asyncAddTopicMessage
argument_list|(
name|ctx
argument_list|,
name|message
argument_list|)
return|;
block|}
block|}
argument_list|)
expr_stmt|;
return|return
name|AbstractMessageStore
operator|.
name|FUTURE
return|;
block|}
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
comment|/**      * @param ack      * @throws IOException      */
specifier|final
name|void
name|removeMessage
parameter_list|(
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
name|isInTransaction
argument_list|()
condition|)
block|{
if|if
condition|(
name|ack
operator|.
name|getTransactionId
argument_list|()
operator|.
name|isXATransaction
argument_list|()
operator|||
name|theStore
operator|.
name|isConcurrentStoreAndDispatchTransactions
argument_list|()
operator|==
literal|false
condition|)
block|{
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
else|else
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
argument_list|(
name|context
argument_list|)
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
name|Future
argument_list|<
name|Object
argument_list|>
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
return|return
name|AbstractMessageStore
operator|.
name|FUTURE
return|;
block|}
block|}
argument_list|)
expr_stmt|;
block|}
block|}
else|else
block|{
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
block|}
specifier|final
name|void
name|removeAsyncMessage
parameter_list|(
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
name|isInTransaction
argument_list|()
condition|)
block|{
if|if
condition|(
name|ack
operator|.
name|getTransactionId
argument_list|()
operator|.
name|isXATransaction
argument_list|()
operator|||
name|theStore
operator|.
name|isConcurrentStoreAndDispatchTransactions
argument_list|()
operator|==
literal|false
condition|)
block|{
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
else|else
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
argument_list|(
name|context
argument_list|)
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
name|Future
argument_list|<
name|Object
argument_list|>
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
return|return
name|AbstractMessageStore
operator|.
name|FUTURE
return|;
block|}
block|}
argument_list|)
expr_stmt|;
block|}
block|}
else|else
block|{
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
block|}
specifier|private
name|KahaTransactionInfo
name|getTransactionInfo
parameter_list|(
name|TransactionId
name|txid
parameter_list|)
block|{
return|return
name|theStore
operator|.
name|createTransactionInfo
argument_list|(
name|txid
argument_list|)
return|;
block|}
block|}
end_class

end_unit

