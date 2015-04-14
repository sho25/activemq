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
name|broker
package|;
end_package

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
name|LinkedHashMap
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
name|ConcurrentHashMap
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
name|jmx
operator|.
name|ManagedRegionBroker
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
name|region
operator|.
name|Destination
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
name|BaseCommand
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
name|ConnectionInfo
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
name|LocalTransactionId
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
name|ProducerInfo
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
name|state
operator|.
name|ProducerState
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
name|transaction
operator|.
name|LocalTransaction
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
name|transaction
operator|.
name|Transaction
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
name|XATransaction
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
name|util
operator|.
name|WrappedException
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
comment|/**  * This broker filter handles the transaction related operations in the Broker  * interface.  *   *   */
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
name|Logger
name|LOG
init|=
name|LoggerFactory
operator|.
name|getLogger
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
name|Map
argument_list|<
name|TransactionId
argument_list|,
name|XATransaction
argument_list|>
name|xaTransactions
init|=
operator|new
name|LinkedHashMap
argument_list|<
name|TransactionId
argument_list|,
name|XATransaction
argument_list|>
argument_list|()
decl_stmt|;
specifier|private
name|ActiveMQMessageAudit
name|audit
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
comment|// ////////////////////////////////////////////////////////////////////////////
comment|//
comment|// Life cycle Methods
comment|//
comment|// ////////////////////////////////////////////////////////////////////////////
comment|/**      * Recovers any prepared transactions.      */
specifier|public
name|void
name|start
parameter_list|()
throws|throws
name|Exception
block|{
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
argument_list|<
name|TransactionId
argument_list|,
name|Transaction
argument_list|>
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
specifier|final
name|ProducerBrokerExchange
name|producerExchange
init|=
operator|new
name|ProducerBrokerExchange
argument_list|()
decl_stmt|;
name|producerExchange
operator|.
name|setMutable
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|producerExchange
operator|.
name|setConnectionContext
argument_list|(
name|context
argument_list|)
expr_stmt|;
name|producerExchange
operator|.
name|setProducerState
argument_list|(
operator|new
name|ProducerState
argument_list|(
operator|new
name|ProducerInfo
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
specifier|final
name|ConsumerBrokerExchange
name|consumerExchange
init|=
operator|new
name|ConsumerBrokerExchange
argument_list|()
decl_stmt|;
name|consumerExchange
operator|.
name|setConnectionContext
argument_list|(
name|context
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
name|XATransaction
name|transaction
init|=
operator|(
name|XATransaction
operator|)
name|getTransaction
argument_list|(
name|context
argument_list|,
name|xid
argument_list|,
literal|false
argument_list|)
decl_stmt|;
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
name|forceDestinationWakeupOnCompletion
argument_list|(
name|context
argument_list|,
name|transaction
argument_list|,
name|addedMessages
index|[
name|i
index|]
operator|.
name|getDestination
argument_list|()
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
name|forceDestinationWakeupOnCompletion
argument_list|(
name|context
argument_list|,
name|transaction
argument_list|,
name|aks
index|[
name|i
index|]
operator|.
name|getDestination
argument_list|()
argument_list|,
name|aks
index|[
name|i
index|]
argument_list|)
expr_stmt|;
block|}
name|transaction
operator|.
name|setState
argument_list|(
name|Transaction
operator|.
name|PREPARED_STATE
argument_list|)
expr_stmt|;
name|registerMBean
argument_list|(
name|transaction
argument_list|)
expr_stmt|;
name|LOG
operator|.
name|debug
argument_list|(
literal|"recovered prepared transaction: {}"
argument_list|,
name|transaction
operator|.
name|getTransactionId
argument_list|()
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
name|next
operator|.
name|start
argument_list|()
expr_stmt|;
block|}
specifier|private
name|void
name|registerMBean
parameter_list|(
name|XATransaction
name|transaction
parameter_list|)
block|{
if|if
condition|(
name|getBrokerService
argument_list|()
operator|.
name|getRegionBroker
argument_list|()
operator|instanceof
name|ManagedRegionBroker
condition|)
block|{
name|ManagedRegionBroker
name|managedRegionBroker
init|=
operator|(
name|ManagedRegionBroker
operator|)
name|getBrokerService
argument_list|()
operator|.
name|getRegionBroker
argument_list|()
decl_stmt|;
name|managedRegionBroker
operator|.
name|registerRecoveredTransactionMBean
argument_list|(
name|transaction
argument_list|)
expr_stmt|;
block|}
block|}
specifier|private
name|void
name|forceDestinationWakeupOnCompletion
parameter_list|(
name|ConnectionContext
name|context
parameter_list|,
name|Transaction
name|transaction
parameter_list|,
name|ActiveMQDestination
name|amqDestination
parameter_list|,
name|BaseCommand
name|ack
parameter_list|)
throws|throws
name|Exception
block|{
name|Destination
name|destination
init|=
name|addDestination
argument_list|(
name|context
argument_list|,
name|amqDestination
argument_list|,
literal|false
argument_list|)
decl_stmt|;
name|registerSync
argument_list|(
name|destination
argument_list|,
name|transaction
argument_list|,
name|ack
argument_list|)
expr_stmt|;
block|}
specifier|private
name|void
name|registerSync
parameter_list|(
name|Destination
name|destination
parameter_list|,
name|Transaction
name|transaction
parameter_list|,
name|BaseCommand
name|command
parameter_list|)
block|{
name|Synchronization
name|sync
init|=
operator|new
name|PreparedDestinationCompletion
argument_list|(
name|destination
argument_list|,
name|command
operator|.
name|isMessage
argument_list|()
argument_list|)
decl_stmt|;
comment|// ensure one per destination in the list
name|Synchronization
name|existing
init|=
name|transaction
operator|.
name|findMatching
argument_list|(
name|sync
argument_list|)
decl_stmt|;
if|if
condition|(
name|existing
operator|!=
literal|null
condition|)
block|{
operator|(
operator|(
name|PreparedDestinationCompletion
operator|)
name|existing
operator|)
operator|.
name|incrementOpCount
argument_list|()
expr_stmt|;
block|}
else|else
block|{
name|transaction
operator|.
name|addSynchronization
argument_list|(
name|sync
argument_list|)
expr_stmt|;
block|}
block|}
specifier|static
class|class
name|PreparedDestinationCompletion
extends|extends
name|Synchronization
block|{
specifier|final
name|Destination
name|destination
decl_stmt|;
specifier|final
name|boolean
name|messageSend
decl_stmt|;
name|int
name|opCount
init|=
literal|1
decl_stmt|;
specifier|public
name|PreparedDestinationCompletion
parameter_list|(
specifier|final
name|Destination
name|destination
parameter_list|,
name|boolean
name|messageSend
parameter_list|)
block|{
name|this
operator|.
name|destination
operator|=
name|destination
expr_stmt|;
comment|// rollback relevant to acks, commit to sends
name|this
operator|.
name|messageSend
operator|=
name|messageSend
expr_stmt|;
block|}
specifier|public
name|void
name|incrementOpCount
parameter_list|()
block|{
name|opCount
operator|++
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|int
name|hashCode
parameter_list|()
block|{
return|return
name|System
operator|.
name|identityHashCode
argument_list|(
name|destination
argument_list|)
operator|+
name|System
operator|.
name|identityHashCode
argument_list|(
name|Boolean
operator|.
name|valueOf
argument_list|(
name|messageSend
argument_list|)
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|equals
parameter_list|(
name|Object
name|other
parameter_list|)
block|{
return|return
name|other
operator|instanceof
name|PreparedDestinationCompletion
operator|&&
name|destination
operator|.
name|equals
argument_list|(
operator|(
operator|(
name|PreparedDestinationCompletion
operator|)
name|other
operator|)
operator|.
name|destination
argument_list|)
operator|&&
name|messageSend
operator|==
operator|(
operator|(
name|PreparedDestinationCompletion
operator|)
name|other
operator|)
operator|.
name|messageSend
return|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|afterRollback
parameter_list|()
throws|throws
name|Exception
block|{
if|if
condition|(
operator|!
name|messageSend
condition|)
block|{
name|destination
operator|.
name|clearPendingMessages
argument_list|()
expr_stmt|;
name|LOG
operator|.
name|debug
argument_list|(
literal|"cleared pending from afterRollback: {}"
argument_list|,
name|destination
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Override
specifier|public
name|void
name|afterCommit
parameter_list|()
throws|throws
name|Exception
block|{
if|if
condition|(
name|messageSend
condition|)
block|{
name|destination
operator|.
name|clearPendingMessages
argument_list|()
expr_stmt|;
name|destination
operator|.
name|getDestinationStatistics
argument_list|()
operator|.
name|getEnqueues
argument_list|()
operator|.
name|add
argument_list|(
name|opCount
argument_list|)
expr_stmt|;
name|destination
operator|.
name|getDestinationStatistics
argument_list|()
operator|.
name|getMessages
argument_list|()
operator|.
name|add
argument_list|(
name|opCount
argument_list|)
expr_stmt|;
name|LOG
operator|.
name|debug
argument_list|(
literal|"cleared pending from afterCommit: {}"
argument_list|,
name|destination
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|destination
operator|.
name|getDestinationStatistics
argument_list|()
operator|.
name|getDequeues
argument_list|()
operator|.
name|add
argument_list|(
name|opCount
argument_list|)
expr_stmt|;
name|destination
operator|.
name|getDestinationStatistics
argument_list|()
operator|.
name|getMessages
argument_list|()
operator|.
name|subtract
argument_list|(
name|opCount
argument_list|)
expr_stmt|;
block|}
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
comment|// ////////////////////////////////////////////////////////////////////////////
comment|//
comment|// BrokerFilter overrides
comment|//
comment|// ////////////////////////////////////////////////////////////////////////////
specifier|public
name|TransactionId
index|[]
name|getPreparedTransactions
parameter_list|(
name|ConnectionContext
name|context
parameter_list|)
throws|throws
name|Exception
block|{
name|List
argument_list|<
name|TransactionId
argument_list|>
name|txs
init|=
operator|new
name|ArrayList
argument_list|<
name|TransactionId
argument_list|>
argument_list|()
decl_stmt|;
synchronized|synchronized
init|(
name|xaTransactions
init|)
block|{
for|for
control|(
name|Iterator
argument_list|<
name|XATransaction
argument_list|>
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
block|{
name|LOG
operator|.
name|debug
argument_list|(
literal|"prepared transaction: {}"
argument_list|,
name|tx
operator|.
name|getTransactionId
argument_list|()
argument_list|)
expr_stmt|;
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
block|}
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
name|LOG
operator|.
name|debug
argument_list|(
literal|"prepared transaction list size: {}"
argument_list|,
name|rc
operator|.
name|length
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
name|Exception
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
name|XATransaction
name|transaction
init|=
literal|null
decl_stmt|;
synchronized|synchronized
init|(
name|xaTransactions
init|)
block|{
name|transaction
operator|=
name|xaTransactions
operator|.
name|get
argument_list|(
name|xid
argument_list|)
expr_stmt|;
if|if
condition|(
name|transaction
operator|!=
literal|null
condition|)
block|{
return|return;
block|}
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
argument_list|,
name|context
operator|.
name|getConnectionId
argument_list|()
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
block|}
else|else
block|{
name|Map
argument_list|<
name|TransactionId
argument_list|,
name|Transaction
argument_list|>
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
block|{
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
block|}
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
name|Exception
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
name|Exception
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
name|Exception
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
name|Exception
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
name|ConsumerBrokerExchange
name|consumerExchange
parameter_list|,
name|MessageAck
name|ack
parameter_list|)
throws|throws
name|Exception
block|{
comment|// This method may be invoked recursively.
comment|// Track original tx so that it can be restored.
specifier|final
name|ConnectionContext
name|context
init|=
name|consumerExchange
operator|.
name|getConnectionContext
argument_list|()
decl_stmt|;
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
name|consumerExchange
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
name|ProducerBrokerExchange
name|producerExchange
parameter_list|,
specifier|final
name|Message
name|message
parameter_list|)
throws|throws
name|Exception
block|{
comment|// This method may be invoked recursively.
comment|// Track original tx so that it can be restored.
specifier|final
name|ConnectionContext
name|context
init|=
name|producerExchange
operator|.
name|getConnectionContext
argument_list|()
decl_stmt|;
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
name|Synchronization
name|sync
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
if|if
condition|(
name|transaction
operator|!=
literal|null
condition|)
block|{
name|sync
operator|=
operator|new
name|Synchronization
argument_list|()
block|{
specifier|public
name|void
name|afterRollback
parameter_list|()
block|{
if|if
condition|(
name|audit
operator|!=
literal|null
condition|)
block|{
name|audit
operator|.
name|rollback
argument_list|(
name|message
argument_list|)
expr_stmt|;
block|}
block|}
block|}
expr_stmt|;
name|transaction
operator|.
name|addSynchronization
argument_list|(
name|sync
argument_list|)
expr_stmt|;
block|}
block|}
if|if
condition|(
name|audit
operator|==
literal|null
operator|||
operator|!
name|audit
operator|.
name|isDuplicate
argument_list|(
name|message
argument_list|)
condition|)
block|{
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
name|producerExchange
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
else|else
block|{
if|if
condition|(
name|sync
operator|!=
literal|null
operator|&&
name|transaction
operator|!=
literal|null
condition|)
block|{
name|transaction
operator|.
name|removeSynchronization
argument_list|(
name|sync
argument_list|)
expr_stmt|;
block|}
name|LOG
operator|.
name|debug
argument_list|(
literal|"IGNORING duplicate message {}"
argument_list|,
name|message
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
name|Exception
block|{
for|for
control|(
name|Iterator
argument_list|<
name|Transaction
argument_list|>
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
name|LOG
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
synchronized|synchronized
init|(
name|xaTransactions
init|)
block|{
comment|// first find all txs that belongs to the connection
name|ArrayList
argument_list|<
name|XATransaction
argument_list|>
name|txs
init|=
operator|new
name|ArrayList
argument_list|<
name|XATransaction
argument_list|>
argument_list|()
decl_stmt|;
for|for
control|(
name|XATransaction
name|tx
range|:
name|xaTransactions
operator|.
name|values
argument_list|()
control|)
block|{
if|if
condition|(
name|tx
operator|.
name|getConnectionId
argument_list|()
operator|!=
literal|null
operator|&&
name|tx
operator|.
name|getConnectionId
argument_list|()
operator|.
name|equals
argument_list|(
name|info
operator|.
name|getConnectionId
argument_list|()
argument_list|)
operator|&&
operator|!
name|tx
operator|.
name|isPrepared
argument_list|()
condition|)
block|{
name|txs
operator|.
name|add
argument_list|(
name|tx
argument_list|)
expr_stmt|;
block|}
block|}
comment|// then remove them
comment|// two steps needed to avoid ConcurrentModificationException, from removeTransaction()
for|for
control|(
name|XATransaction
name|tx
range|:
name|txs
control|)
block|{
try|try
block|{
name|tx
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
name|LOG
operator|.
name|warn
argument_list|(
literal|"ERROR Rolling back disconnected client's xa transactions: "
argument_list|,
name|e
argument_list|)
expr_stmt|;
block|}
block|}
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
comment|// ////////////////////////////////////////////////////////////////////////////
comment|//
comment|// Implementation help methods.
comment|//
comment|// ////////////////////////////////////////////////////////////////////////////
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
literal|null
decl_stmt|;
synchronized|synchronized
init|(
name|xaTransactions
init|)
block|{
name|transactionMap
operator|=
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
expr_stmt|;
block|}
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
block|{
return|return
name|transaction
return|;
block|}
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
name|XATransaction
operator|.
name|newXAException
argument_list|(
literal|"Transaction '"
operator|+
name|xid
operator|+
literal|"' has not been started."
argument_list|,
name|XAException
operator|.
name|XAER_NOTA
argument_list|)
decl_stmt|;
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
synchronized|synchronized
init|(
name|xaTransactions
init|)
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
specifier|public
specifier|synchronized
name|void
name|brokerServiceStarted
parameter_list|()
block|{
name|super
operator|.
name|brokerServiceStarted
argument_list|()
expr_stmt|;
if|if
condition|(
name|audit
operator|==
literal|null
condition|)
block|{
name|audit
operator|=
operator|new
name|ActiveMQMessageAudit
argument_list|()
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

