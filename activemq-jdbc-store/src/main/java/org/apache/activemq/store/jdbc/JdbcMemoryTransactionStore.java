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
name|memory
operator|.
name|MemoryTransactionStore
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
name|DataByteArrayInputStream
import|;
end_import

begin_comment
comment|/**  * respect 2pc prepare  * uses local transactions to maintain prepared state  * xid column provides transaction flag for additions and removals  * a commit clears that context and completes the work  * a rollback clears the flag and removes the additions  * Essentially a prepare is an insert&| update transaction  *  commit|rollback is an update&| remove  */
end_comment

begin_class
specifier|public
class|class
name|JdbcMemoryTransactionStore
extends|extends
name|MemoryTransactionStore
block|{
specifier|private
name|HashMap
argument_list|<
name|ActiveMQDestination
argument_list|,
name|MessageStore
argument_list|>
name|topicStores
init|=
operator|new
name|HashMap
argument_list|<
name|ActiveMQDestination
argument_list|,
name|MessageStore
argument_list|>
argument_list|()
decl_stmt|;
specifier|public
name|JdbcMemoryTransactionStore
parameter_list|(
name|JDBCPersistenceAdapter
name|jdbcPersistenceAdapter
parameter_list|)
block|{
name|super
argument_list|(
name|jdbcPersistenceAdapter
argument_list|)
expr_stmt|;
block|}
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
name|ConnectionContext
name|ctx
init|=
operator|new
name|ConnectionContext
argument_list|()
decl_stmt|;
comment|// setting the xid modifies the add/remove to be pending transaction outcome
name|ctx
operator|.
name|setXid
argument_list|(
operator|(
name|XATransactionId
operator|)
name|txid
argument_list|)
expr_stmt|;
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
name|tx
operator|.
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
name|tx
operator|.
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
name|ctx
operator|.
name|setXid
argument_list|(
literal|null
argument_list|)
expr_stmt|;
comment|// setup for commit outcome
name|ArrayList
argument_list|<
name|AddMessageCommand
argument_list|>
name|updateFromPreparedStateCommands
init|=
operator|new
name|ArrayList
argument_list|<
name|AddMessageCommand
argument_list|>
argument_list|()
decl_stmt|;
for|for
control|(
name|Iterator
argument_list|<
name|AddMessageCommand
argument_list|>
name|iter
init|=
name|tx
operator|.
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
specifier|final
name|AddMessageCommand
name|addMessageCommand
init|=
name|iter
operator|.
name|next
argument_list|()
decl_stmt|;
name|updateFromPreparedStateCommands
operator|.
name|add
argument_list|(
operator|new
name|AddMessageCommand
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|Message
name|getMessage
parameter_list|()
block|{
return|return
name|addMessageCommand
operator|.
name|getMessage
argument_list|()
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
name|addMessageCommand
operator|.
name|getMessageStore
argument_list|()
return|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|run
parameter_list|(
name|ConnectionContext
name|context
parameter_list|)
throws|throws
name|IOException
block|{
name|JDBCPersistenceAdapter
name|jdbcPersistenceAdapter
init|=
operator|(
name|JDBCPersistenceAdapter
operator|)
name|persistenceAdapter
decl_stmt|;
name|Message
name|message
init|=
name|addMessageCommand
operator|.
name|getMessage
argument_list|()
decl_stmt|;
name|jdbcPersistenceAdapter
operator|.
name|commitAdd
argument_list|(
name|context
argument_list|,
name|message
operator|.
name|getMessageId
argument_list|()
argument_list|)
expr_stmt|;
operator|(
operator|(
name|JDBCMessageStore
operator|)
name|addMessageCommand
operator|.
name|getMessageStore
argument_list|()
operator|)
operator|.
name|onAdd
argument_list|(
name|message
operator|.
name|getMessageId
argument_list|()
argument_list|,
operator|(
name|Long
operator|)
name|message
operator|.
name|getMessageId
argument_list|()
operator|.
name|getEntryLocator
argument_list|()
argument_list|,
name|message
operator|.
name|getPriority
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
argument_list|)
expr_stmt|;
block|}
name|tx
operator|.
name|messages
operator|=
name|updateFromPreparedStateCommands
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
name|tx
operator|=
name|preparedTransactions
operator|.
name|remove
argument_list|(
name|txid
argument_list|)
expr_stmt|;
if|if
condition|(
name|tx
operator|!=
literal|null
condition|)
block|{
comment|// undo prepare work
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
for|for
control|(
name|Iterator
argument_list|<
name|AddMessageCommand
argument_list|>
name|iter
init|=
name|tx
operator|.
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
specifier|final
name|Message
name|message
init|=
name|iter
operator|.
name|next
argument_list|()
operator|.
name|getMessage
argument_list|()
decl_stmt|;
comment|// need to delete the row
operator|(
operator|(
name|JDBCPersistenceAdapter
operator|)
name|persistenceAdapter
operator|)
operator|.
name|commitRemove
argument_list|(
name|ctx
argument_list|,
operator|new
name|MessageAck
argument_list|(
name|message
argument_list|,
name|MessageAck
operator|.
name|STANDARD_ACK_TYPE
argument_list|,
literal|1
argument_list|)
argument_list|)
expr_stmt|;
block|}
for|for
control|(
name|Iterator
argument_list|<
name|RemoveMessageCommand
argument_list|>
name|iter
init|=
name|tx
operator|.
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
name|removeMessageCommand
init|=
name|iter
operator|.
name|next
argument_list|()
decl_stmt|;
if|if
condition|(
name|removeMessageCommand
operator|instanceof
name|LastAckCommand
condition|)
block|{
operator|(
operator|(
name|LastAckCommand
operator|)
name|removeMessageCommand
operator|)
operator|.
name|rollback
argument_list|(
name|ctx
argument_list|)
expr_stmt|;
block|}
else|else
block|{
comment|// need to unset the txid flag on the existing row
operator|(
operator|(
name|JDBCPersistenceAdapter
operator|)
name|persistenceAdapter
operator|)
operator|.
name|commitAdd
argument_list|(
name|ctx
argument_list|,
name|removeMessageCommand
operator|.
name|getMessageAck
argument_list|()
operator|.
name|getLastMessageId
argument_list|()
argument_list|)
expr_stmt|;
block|}
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
block|}
annotation|@
name|Override
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
operator|(
operator|(
name|JDBCPersistenceAdapter
operator|)
name|persistenceAdapter
operator|)
operator|.
name|recover
argument_list|(
name|this
argument_list|)
expr_stmt|;
name|super
operator|.
name|recover
argument_list|(
name|listener
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|recoverAdd
parameter_list|(
name|long
name|id
parameter_list|,
name|byte
index|[]
name|messageBytes
parameter_list|)
throws|throws
name|IOException
block|{
specifier|final
name|Message
name|message
init|=
call|(
name|Message
call|)
argument_list|(
operator|(
name|JDBCPersistenceAdapter
operator|)
name|persistenceAdapter
argument_list|)
operator|.
name|getWireFormat
argument_list|()
operator|.
name|unmarshal
argument_list|(
operator|new
name|ByteSequence
argument_list|(
name|messageBytes
argument_list|)
argument_list|)
decl_stmt|;
name|message
operator|.
name|getMessageId
argument_list|()
operator|.
name|setEntryLocator
argument_list|(
name|id
argument_list|)
expr_stmt|;
name|Tx
name|tx
init|=
name|getPreparedTx
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
literal|null
return|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|run
parameter_list|(
name|ConnectionContext
name|context
parameter_list|)
throws|throws
name|IOException
block|{
operator|(
operator|(
name|JDBCPersistenceAdapter
operator|)
name|persistenceAdapter
operator|)
operator|.
name|commitAdd
argument_list|(
literal|null
argument_list|,
name|message
operator|.
name|getMessageId
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|recoverAck
parameter_list|(
name|long
name|id
parameter_list|,
name|byte
index|[]
name|xid
parameter_list|,
name|byte
index|[]
name|message
parameter_list|)
throws|throws
name|IOException
block|{
name|Message
name|msg
init|=
call|(
name|Message
call|)
argument_list|(
operator|(
name|JDBCPersistenceAdapter
operator|)
name|persistenceAdapter
argument_list|)
operator|.
name|getWireFormat
argument_list|()
operator|.
name|unmarshal
argument_list|(
operator|new
name|ByteSequence
argument_list|(
name|message
argument_list|)
argument_list|)
decl_stmt|;
name|msg
operator|.
name|getMessageId
argument_list|()
operator|.
name|setEntryLocator
argument_list|(
name|id
argument_list|)
expr_stmt|;
name|Tx
name|tx
init|=
name|getPreparedTx
argument_list|(
operator|new
name|XATransactionId
argument_list|(
name|xid
argument_list|)
argument_list|)
decl_stmt|;
specifier|final
name|MessageAck
name|ack
init|=
operator|new
name|MessageAck
argument_list|(
name|msg
argument_list|,
name|MessageAck
operator|.
name|STANDARD_ACK_TYPE
argument_list|,
literal|1
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
name|context
parameter_list|)
throws|throws
name|IOException
block|{
operator|(
operator|(
name|JDBCPersistenceAdapter
operator|)
name|persistenceAdapter
operator|)
operator|.
name|commitRemove
argument_list|(
name|context
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
literal|null
return|;
block|}
block|}
argument_list|)
expr_stmt|;
block|}
interface|interface
name|LastAckCommand
extends|extends
name|RemoveMessageCommand
block|{
name|void
name|rollback
parameter_list|(
name|ConnectionContext
name|context
parameter_list|)
throws|throws
name|IOException
function_decl|;
name|String
name|getClientId
parameter_list|()
function_decl|;
name|String
name|getSubName
parameter_list|()
function_decl|;
name|long
name|getSequence
parameter_list|()
function_decl|;
name|byte
name|getPriority
parameter_list|()
function_decl|;
name|void
name|setMessageStore
parameter_list|(
name|JDBCTopicMessageStore
name|jdbcTopicMessageStore
parameter_list|)
function_decl|;
block|}
specifier|public
name|void
name|recoverLastAck
parameter_list|(
name|byte
index|[]
name|encodedXid
parameter_list|,
specifier|final
name|ActiveMQDestination
name|destination
parameter_list|,
specifier|final
name|String
name|subName
parameter_list|,
specifier|final
name|String
name|clientId
parameter_list|)
throws|throws
name|IOException
block|{
name|Tx
name|tx
init|=
name|getPreparedTx
argument_list|(
operator|new
name|XATransactionId
argument_list|(
name|encodedXid
argument_list|)
argument_list|)
decl_stmt|;
name|DataByteArrayInputStream
name|inputStream
init|=
operator|new
name|DataByteArrayInputStream
argument_list|(
name|encodedXid
argument_list|)
decl_stmt|;
name|inputStream
operator|.
name|skipBytes
argument_list|(
literal|1
argument_list|)
expr_stmt|;
comment|// +|-
specifier|final
name|long
name|lastAck
init|=
name|inputStream
operator|.
name|readLong
argument_list|()
decl_stmt|;
specifier|final
name|byte
name|priority
init|=
name|inputStream
operator|.
name|readByte
argument_list|()
decl_stmt|;
specifier|final
name|MessageAck
name|ack
init|=
operator|new
name|MessageAck
argument_list|()
decl_stmt|;
name|ack
operator|.
name|setDestination
argument_list|(
name|destination
argument_list|)
expr_stmt|;
name|tx
operator|.
name|add
argument_list|(
operator|new
name|LastAckCommand
argument_list|()
block|{
name|JDBCTopicMessageStore
name|jdbcTopicMessageStore
decl_stmt|;
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
name|MessageStore
name|getMessageStore
parameter_list|()
block|{
return|return
name|jdbcTopicMessageStore
return|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|run
parameter_list|(
name|ConnectionContext
name|context
parameter_list|)
throws|throws
name|IOException
block|{
operator|(
operator|(
name|JDBCPersistenceAdapter
operator|)
name|persistenceAdapter
operator|)
operator|.
name|commitLastAck
argument_list|(
name|context
argument_list|,
name|lastAck
argument_list|,
name|priority
argument_list|,
name|destination
argument_list|,
name|subName
argument_list|,
name|clientId
argument_list|)
expr_stmt|;
name|jdbcTopicMessageStore
operator|.
name|complete
argument_list|(
name|clientId
argument_list|,
name|subName
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|rollback
parameter_list|(
name|ConnectionContext
name|context
parameter_list|)
throws|throws
name|IOException
block|{
operator|(
operator|(
name|JDBCPersistenceAdapter
operator|)
name|persistenceAdapter
operator|)
operator|.
name|rollbackLastAck
argument_list|(
name|context
argument_list|,
name|priority
argument_list|,
name|jdbcTopicMessageStore
operator|.
name|getDestination
argument_list|()
argument_list|,
name|subName
argument_list|,
name|clientId
argument_list|)
expr_stmt|;
name|jdbcTopicMessageStore
operator|.
name|complete
argument_list|(
name|clientId
argument_list|,
name|subName
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|String
name|getClientId
parameter_list|()
block|{
return|return
name|clientId
return|;
block|}
annotation|@
name|Override
specifier|public
name|String
name|getSubName
parameter_list|()
block|{
return|return
name|subName
return|;
block|}
annotation|@
name|Override
specifier|public
name|long
name|getSequence
parameter_list|()
block|{
return|return
name|lastAck
return|;
block|}
annotation|@
name|Override
specifier|public
name|byte
name|getPriority
parameter_list|()
block|{
return|return
name|priority
return|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|setMessageStore
parameter_list|(
name|JDBCTopicMessageStore
name|jdbcTopicMessageStore
parameter_list|)
block|{
name|this
operator|.
name|jdbcTopicMessageStore
operator|=
name|jdbcTopicMessageStore
expr_stmt|;
block|}
block|}
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|protected
name|void
name|onProxyTopicStore
parameter_list|(
name|ProxyTopicMessageStore
name|proxyTopicMessageStore
parameter_list|)
block|{
name|topicStores
operator|.
name|put
argument_list|(
name|proxyTopicMessageStore
operator|.
name|getDestination
argument_list|()
argument_list|,
name|proxyTopicMessageStore
operator|.
name|getDelegate
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|protected
name|void
name|onRecovered
parameter_list|(
name|Tx
name|tx
parameter_list|)
block|{
for|for
control|(
name|RemoveMessageCommand
name|removeMessageCommand
range|:
name|tx
operator|.
name|acks
control|)
block|{
if|if
condition|(
name|removeMessageCommand
operator|instanceof
name|LastAckCommand
condition|)
block|{
name|LastAckCommand
name|lastAckCommand
init|=
operator|(
name|LastAckCommand
operator|)
name|removeMessageCommand
decl_stmt|;
name|JDBCTopicMessageStore
name|jdbcTopicMessageStore
init|=
operator|(
name|JDBCTopicMessageStore
operator|)
name|topicStores
operator|.
name|get
argument_list|(
name|lastAckCommand
operator|.
name|getMessageAck
argument_list|()
operator|.
name|getDestination
argument_list|()
argument_list|)
decl_stmt|;
name|jdbcTopicMessageStore
operator|.
name|pendingCompletion
argument_list|(
name|lastAckCommand
operator|.
name|getClientId
argument_list|()
argument_list|,
name|lastAckCommand
operator|.
name|getSubName
argument_list|()
argument_list|,
name|lastAckCommand
operator|.
name|getSequence
argument_list|()
argument_list|,
name|lastAckCommand
operator|.
name|getPriority
argument_list|()
argument_list|)
expr_stmt|;
name|lastAckCommand
operator|.
name|setMessageStore
argument_list|(
name|jdbcTopicMessageStore
argument_list|)
expr_stmt|;
block|}
else|else
block|{
comment|// when reading the store we ignore messages with non null XIDs but should include those with XIDS starting in - (pending acks in an xa transaction),
comment|// but the sql is non portable to match BLOB with LIKE etc
comment|// so we make up for it when we recover the ack
operator|(
operator|(
name|JDBCPersistenceAdapter
operator|)
name|persistenceAdapter
operator|)
operator|.
name|getBrokerService
argument_list|()
operator|.
name|getRegionBroker
argument_list|()
operator|.
name|getDestinationMap
argument_list|()
operator|.
name|get
argument_list|(
name|removeMessageCommand
operator|.
name|getMessageAck
argument_list|()
operator|.
name|getDestination
argument_list|()
argument_list|)
operator|.
name|getDestinationStatistics
argument_list|()
operator|.
name|getMessages
argument_list|()
operator|.
name|increment
argument_list|()
expr_stmt|;
block|}
block|}
block|}
annotation|@
name|Override
specifier|public
name|void
name|acknowledge
parameter_list|(
specifier|final
name|TopicMessageStore
name|topicMessageStore
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
name|LastAckCommand
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
parameter_list|(
name|ConnectionContext
name|ctx
parameter_list|)
throws|throws
name|IOException
block|{
name|topicMessageStore
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
name|topicMessageStore
return|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|rollback
parameter_list|(
name|ConnectionContext
name|context
parameter_list|)
throws|throws
name|IOException
block|{
name|JDBCTopicMessageStore
name|jdbcTopicMessageStore
init|=
operator|(
name|JDBCTopicMessageStore
operator|)
name|topicMessageStore
decl_stmt|;
operator|(
operator|(
name|JDBCPersistenceAdapter
operator|)
name|persistenceAdapter
operator|)
operator|.
name|rollbackLastAck
argument_list|(
name|context
argument_list|,
name|jdbcTopicMessageStore
argument_list|,
name|ack
argument_list|,
name|subscriptionName
argument_list|,
name|clientId
argument_list|)
expr_stmt|;
name|jdbcTopicMessageStore
operator|.
name|complete
argument_list|(
name|clientId
argument_list|,
name|subscriptionName
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|String
name|getClientId
parameter_list|()
block|{
return|return
name|clientId
return|;
block|}
annotation|@
name|Override
specifier|public
name|String
name|getSubName
parameter_list|()
block|{
return|return
name|subscriptionName
return|;
block|}
annotation|@
name|Override
specifier|public
name|long
name|getSequence
parameter_list|()
block|{
throw|throw
operator|new
name|IllegalStateException
argument_list|(
literal|"Sequence id must be inferred from ack"
argument_list|)
throw|;
block|}
annotation|@
name|Override
specifier|public
name|byte
name|getPriority
parameter_list|()
block|{
throw|throw
operator|new
name|IllegalStateException
argument_list|(
literal|"Priority must be inferred from ack or row"
argument_list|)
throw|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|setMessageStore
parameter_list|(
name|JDBCTopicMessageStore
name|jdbcTopicMessageStore
parameter_list|)
block|{
throw|throw
operator|new
name|IllegalStateException
argument_list|(
literal|"message store already known!"
argument_list|)
throw|;
block|}
block|}
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|topicMessageStore
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
block|}
end_class

end_unit

