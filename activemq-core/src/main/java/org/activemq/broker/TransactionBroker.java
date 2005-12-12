begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/** *<a href="http://activemq.org">ActiveMQ: The Open Source Message Fabric</a> * * Copyright 2005 (C) LogicBlaze, Inc. http://www.logicblaze.com * * Licensed under the Apache License, Version 2.0 (the "License"); * you may not use this file except in compliance with the License. * You may obtain a copy of the License at * * http://www.apache.org/licenses/LICENSE-2.0 * * Unless required by applicable law or agreed to in writing, software * distributed under the License is distributed on an "AS IS" BASIS, * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. * See the License for the specific language governing permissions and * limitations under the License. * **/
end_comment

begin_package
package|package
name|org
operator|.
name|activemq
operator|.
name|broker
package|;
end_package

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
name|org
operator|.
name|activemq
operator|.
name|command
operator|.
name|ConnectionInfo
import|;
end_import

begin_import
import|import
name|org
operator|.
name|activemq
operator|.
name|command
operator|.
name|LocalTransactionId
import|;
end_import

begin_import
import|import
name|org
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
name|activemq
operator|.
name|transaction
operator|.
name|LocalTransaction
import|;
end_import

begin_import
import|import
name|org
operator|.
name|activemq
operator|.
name|transaction
operator|.
name|Transaction
import|;
end_import

begin_import
import|import
name|org
operator|.
name|activemq
operator|.
name|transaction
operator|.
name|XATransaction
import|;
end_import

begin_import
import|import
name|org
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
name|activemq
operator|.
name|util
operator|.
name|WrappedException
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
name|javax
operator|.
name|jms
operator|.
name|JMSException
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
name|Map
import|;
end_import

begin_comment
comment|/**  * This broker filter handles the transaction related operations in the Broker interface.  *   * @version $Revision: 1.10 $  */
end_comment

begin_class
specifier|public
class|class
name|TransactionBroker
extends|extends
name|BrokerFilter
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
name|TransactionBroker
operator|.
name|class
argument_list|)
decl_stmt|;
comment|// The prepared XA transactions.
specifier|private
name|TransactionStore
name|transactionStore
decl_stmt|;
specifier|private
name|ConcurrentHashMap
name|xaTransactions
init|=
operator|new
name|ConcurrentHashMap
argument_list|()
decl_stmt|;
specifier|public
name|TransactionBroker
parameter_list|(
name|Broker
name|next
parameter_list|,
name|TransactionStore
name|transactionStore
parameter_list|)
block|{
name|super
argument_list|(
name|next
argument_list|)
expr_stmt|;
name|this
operator|.
name|transactionStore
operator|=
name|transactionStore
expr_stmt|;
block|}
comment|//////////////////////////////////////////////////////////////////////////////
comment|//
comment|// Life cycle Methods
comment|//
comment|//////////////////////////////////////////////////////////////////////////////
comment|/**      * Recovers any prepared transactions.      */
specifier|public
name|void
name|start
parameter_list|()
throws|throws
name|Exception
block|{
name|next
operator|.
name|start
argument_list|()
expr_stmt|;
name|transactionStore
operator|.
name|start
argument_list|()
expr_stmt|;
try|try
block|{
specifier|final
name|ConnectionContext
name|context
init|=
operator|new
name|ConnectionContext
argument_list|()
decl_stmt|;
name|context
operator|.
name|setBroker
argument_list|(
name|this
argument_list|)
expr_stmt|;
name|context
operator|.
name|setInRecoveryMode
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|context
operator|.
name|setTransactions
argument_list|(
operator|new
name|ConcurrentHashMap
argument_list|()
argument_list|)
expr_stmt|;
name|context
operator|.
name|setProducerFlowControl
argument_list|(
literal|false
argument_list|)
expr_stmt|;
name|transactionStore
operator|.
name|recover
argument_list|(
operator|new
name|TransactionRecoveryListener
argument_list|()
block|{
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
name|aks
parameter_list|)
block|{
try|try
block|{
name|beginTransaction
argument_list|(
name|context
argument_list|,
name|xid
argument_list|)
expr_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|addedMessages
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|send
argument_list|(
name|context
argument_list|,
name|addedMessages
index|[
name|i
index|]
argument_list|)
expr_stmt|;
block|}
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|aks
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|acknowledge
argument_list|(
name|context
argument_list|,
name|aks
index|[
name|i
index|]
argument_list|)
expr_stmt|;
block|}
name|prepareTransaction
argument_list|(
name|context
argument_list|,
name|xid
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Throwable
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|WrappedException
argument_list|(
name|e
argument_list|)
throw|;
block|}
block|}
block|}
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|WrappedException
name|e
parameter_list|)
block|{
name|Throwable
name|cause
init|=
name|e
operator|.
name|getCause
argument_list|()
decl_stmt|;
throw|throw
name|IOExceptionSupport
operator|.
name|create
argument_list|(
literal|"Recovery Failed: "
operator|+
name|cause
operator|.
name|getMessage
argument_list|()
argument_list|,
name|cause
argument_list|)
throw|;
block|}
block|}
specifier|public
name|void
name|stop
parameter_list|()
throws|throws
name|Exception
block|{
name|transactionStore
operator|.
name|stop
argument_list|()
expr_stmt|;
name|next
operator|.
name|stop
argument_list|()
expr_stmt|;
block|}
comment|//////////////////////////////////////////////////////////////////////////////
comment|//
comment|// BrokerFilter overrides
comment|//
comment|//////////////////////////////////////////////////////////////////////////////
specifier|public
name|TransactionId
index|[]
name|getPreparedTransactions
parameter_list|(
name|ConnectionContext
name|context
parameter_list|)
throws|throws
name|Throwable
block|{
name|ArrayList
name|txs
init|=
operator|new
name|ArrayList
argument_list|()
decl_stmt|;
for|for
control|(
name|Iterator
name|iter
init|=
name|xaTransactions
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
name|Transaction
name|tx
init|=
operator|(
name|Transaction
operator|)
name|iter
operator|.
name|next
argument_list|()
decl_stmt|;
if|if
condition|(
name|tx
operator|.
name|isPrepared
argument_list|()
condition|)
name|txs
operator|.
name|add
argument_list|(
name|tx
operator|.
name|getTransactionId
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|XATransactionId
name|rc
index|[]
init|=
operator|new
name|XATransactionId
index|[
name|txs
operator|.
name|size
argument_list|()
index|]
decl_stmt|;
name|txs
operator|.
name|toArray
argument_list|(
name|rc
argument_list|)
expr_stmt|;
return|return
name|rc
return|;
block|}
specifier|public
name|void
name|beginTransaction
parameter_list|(
name|ConnectionContext
name|context
parameter_list|,
name|TransactionId
name|xid
parameter_list|)
throws|throws
name|Throwable
block|{
comment|// the transaction may have already been started.
if|if
condition|(
name|xid
operator|.
name|isXATransaction
argument_list|()
condition|)
block|{
name|Transaction
name|transaction
init|=
operator|(
name|Transaction
operator|)
name|xaTransactions
operator|.
name|get
argument_list|(
name|xid
argument_list|)
decl_stmt|;
if|if
condition|(
name|transaction
operator|!=
literal|null
condition|)
return|return;
name|transaction
operator|=
operator|new
name|XATransaction
argument_list|(
name|transactionStore
argument_list|,
operator|(
name|XATransactionId
operator|)
name|xid
argument_list|,
name|this
argument_list|)
expr_stmt|;
name|xaTransactions
operator|.
name|put
argument_list|(
name|xid
argument_list|,
name|transaction
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|Map
name|transactionMap
init|=
name|context
operator|.
name|getTransactions
argument_list|()
decl_stmt|;
name|Transaction
name|transaction
init|=
operator|(
name|Transaction
operator|)
name|transactionMap
operator|.
name|get
argument_list|(
name|xid
argument_list|)
decl_stmt|;
if|if
condition|(
name|transaction
operator|!=
literal|null
condition|)
throw|throw
operator|new
name|JMSException
argument_list|(
literal|"Transaction '"
operator|+
name|xid
operator|+
literal|"' has already been started."
argument_list|)
throw|;
name|transaction
operator|=
operator|new
name|LocalTransaction
argument_list|(
name|transactionStore
argument_list|,
operator|(
name|LocalTransactionId
operator|)
name|xid
argument_list|,
name|context
argument_list|)
expr_stmt|;
name|transactionMap
operator|.
name|put
argument_list|(
name|xid
argument_list|,
name|transaction
argument_list|)
expr_stmt|;
block|}
block|}
specifier|public
name|int
name|prepareTransaction
parameter_list|(
name|ConnectionContext
name|context
parameter_list|,
name|TransactionId
name|xid
parameter_list|)
throws|throws
name|Throwable
block|{
name|Transaction
name|transaction
init|=
name|getTransaction
argument_list|(
name|context
argument_list|,
name|xid
argument_list|,
literal|false
argument_list|)
decl_stmt|;
return|return
name|transaction
operator|.
name|prepare
argument_list|()
return|;
block|}
specifier|public
name|void
name|commitTransaction
parameter_list|(
name|ConnectionContext
name|context
parameter_list|,
name|TransactionId
name|xid
parameter_list|,
name|boolean
name|onePhase
parameter_list|)
throws|throws
name|Throwable
block|{
name|Transaction
name|transaction
init|=
name|getTransaction
argument_list|(
name|context
argument_list|,
name|xid
argument_list|,
literal|true
argument_list|)
decl_stmt|;
name|transaction
operator|.
name|commit
argument_list|(
name|onePhase
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|rollbackTransaction
parameter_list|(
name|ConnectionContext
name|context
parameter_list|,
name|TransactionId
name|xid
parameter_list|)
throws|throws
name|Throwable
block|{
name|Transaction
name|transaction
init|=
name|getTransaction
argument_list|(
name|context
argument_list|,
name|xid
argument_list|,
literal|true
argument_list|)
decl_stmt|;
name|transaction
operator|.
name|rollback
argument_list|()
expr_stmt|;
block|}
specifier|public
name|void
name|forgetTransaction
parameter_list|(
name|ConnectionContext
name|context
parameter_list|,
name|TransactionId
name|xid
parameter_list|)
throws|throws
name|Throwable
block|{
name|Transaction
name|transaction
init|=
name|getTransaction
argument_list|(
name|context
argument_list|,
name|xid
argument_list|,
literal|true
argument_list|)
decl_stmt|;
name|transaction
operator|.
name|rollback
argument_list|()
expr_stmt|;
block|}
specifier|public
name|void
name|acknowledge
parameter_list|(
name|ConnectionContext
name|context
parameter_list|,
name|MessageAck
name|ack
parameter_list|)
throws|throws
name|Throwable
block|{
comment|// This method may be invoked recursively.
comment|// Track original tx so that it can be restored.
name|Transaction
name|originalTx
init|=
name|context
operator|.
name|getTransaction
argument_list|()
decl_stmt|;
name|Transaction
name|transaction
init|=
literal|null
decl_stmt|;
if|if
condition|(
name|ack
operator|.
name|isInTransaction
argument_list|()
condition|)
block|{
name|transaction
operator|=
name|getTransaction
argument_list|(
name|context
argument_list|,
name|ack
operator|.
name|getTransactionId
argument_list|()
argument_list|,
literal|false
argument_list|)
expr_stmt|;
block|}
name|context
operator|.
name|setTransaction
argument_list|(
name|transaction
argument_list|)
expr_stmt|;
try|try
block|{
name|next
operator|.
name|acknowledge
argument_list|(
name|context
argument_list|,
name|ack
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|context
operator|.
name|setTransaction
argument_list|(
name|originalTx
argument_list|)
expr_stmt|;
block|}
block|}
specifier|public
name|void
name|send
parameter_list|(
name|ConnectionContext
name|context
parameter_list|,
name|Message
name|message
parameter_list|)
throws|throws
name|Throwable
block|{
comment|// This method may be invoked recursively.
comment|// Track original tx so that it can be restored.
name|Transaction
name|originalTx
init|=
name|context
operator|.
name|getTransaction
argument_list|()
decl_stmt|;
name|Transaction
name|transaction
init|=
literal|null
decl_stmt|;
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
name|transaction
operator|=
name|getTransaction
argument_list|(
name|context
argument_list|,
name|message
operator|.
name|getTransactionId
argument_list|()
argument_list|,
literal|false
argument_list|)
expr_stmt|;
block|}
name|context
operator|.
name|setTransaction
argument_list|(
name|transaction
argument_list|)
expr_stmt|;
try|try
block|{
name|next
operator|.
name|send
argument_list|(
name|context
argument_list|,
name|message
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|context
operator|.
name|setTransaction
argument_list|(
name|originalTx
argument_list|)
expr_stmt|;
block|}
block|}
specifier|public
name|void
name|removeConnection
parameter_list|(
name|ConnectionContext
name|context
parameter_list|,
name|ConnectionInfo
name|info
parameter_list|,
name|Throwable
name|error
parameter_list|)
throws|throws
name|Throwable
block|{
for|for
control|(
name|Iterator
name|iter
init|=
name|context
operator|.
name|getTransactions
argument_list|()
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
try|try
block|{
name|Transaction
name|transaction
init|=
operator|(
name|Transaction
operator|)
name|iter
operator|.
name|next
argument_list|()
decl_stmt|;
name|transaction
operator|.
name|rollback
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
name|log
operator|.
name|warn
argument_list|(
literal|"ERROR Rolling back disconnected client's transactions: "
argument_list|,
name|e
argument_list|)
expr_stmt|;
block|}
name|iter
operator|.
name|remove
argument_list|()
expr_stmt|;
block|}
name|next
operator|.
name|removeConnection
argument_list|(
name|context
argument_list|,
name|info
argument_list|,
name|error
argument_list|)
expr_stmt|;
block|}
comment|//////////////////////////////////////////////////////////////////////////////
comment|//
comment|// Implementation help methods.
comment|//
comment|//////////////////////////////////////////////////////////////////////////////
specifier|public
name|Transaction
name|getTransaction
parameter_list|(
name|ConnectionContext
name|context
parameter_list|,
name|TransactionId
name|xid
parameter_list|,
name|boolean
name|mightBePrepared
parameter_list|)
throws|throws
name|JMSException
throws|,
name|XAException
block|{
name|Map
name|transactionMap
init|=
name|xid
operator|.
name|isXATransaction
argument_list|()
condition|?
name|xaTransactions
else|:
name|context
operator|.
name|getTransactions
argument_list|()
decl_stmt|;
name|Transaction
name|transaction
init|=
operator|(
name|Transaction
operator|)
name|transactionMap
operator|.
name|get
argument_list|(
name|xid
argument_list|)
decl_stmt|;
if|if
condition|(
name|transaction
operator|!=
literal|null
condition|)
return|return
name|transaction
return|;
if|if
condition|(
name|xid
operator|.
name|isXATransaction
argument_list|()
condition|)
block|{
name|XAException
name|e
init|=
operator|new
name|XAException
argument_list|(
literal|"Transaction '"
operator|+
name|xid
operator|+
literal|"' has not been started."
argument_list|)
decl_stmt|;
name|e
operator|.
name|errorCode
operator|=
name|XAException
operator|.
name|XAER_NOTA
expr_stmt|;
throw|throw
name|e
throw|;
block|}
else|else
block|{
throw|throw
operator|new
name|JMSException
argument_list|(
literal|"Transaction '"
operator|+
name|xid
operator|+
literal|"' has not been started."
argument_list|)
throw|;
block|}
block|}
specifier|public
name|void
name|removeTransaction
parameter_list|(
name|XATransactionId
name|xid
parameter_list|)
block|{
name|xaTransactions
operator|.
name|remove
argument_list|(
name|xid
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

