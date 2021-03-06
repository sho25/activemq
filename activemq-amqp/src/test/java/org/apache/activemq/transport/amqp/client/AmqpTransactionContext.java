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
name|transport
operator|.
name|amqp
operator|.
name|client
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
name|LinkedHashSet
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
name|org
operator|.
name|apache
operator|.
name|activemq
operator|.
name|transport
operator|.
name|amqp
operator|.
name|client
operator|.
name|util
operator|.
name|AsyncResult
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
name|transport
operator|.
name|amqp
operator|.
name|client
operator|.
name|util
operator|.
name|ClientFuture
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
name|transport
operator|.
name|amqp
operator|.
name|client
operator|.
name|util
operator|.
name|ClientFutureSynchronization
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
comment|/**  * Defines a context under which resources in a given session  * will operate inside transaction scoped boundaries.  */
end_comment

begin_class
specifier|public
class|class
name|AmqpTransactionContext
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
name|AmqpTransactionContext
operator|.
name|class
argument_list|)
decl_stmt|;
specifier|private
specifier|final
name|AmqpSession
name|session
decl_stmt|;
specifier|private
specifier|final
name|Set
argument_list|<
name|AmqpReceiver
argument_list|>
name|txReceivers
init|=
operator|new
name|LinkedHashSet
argument_list|<
name|AmqpReceiver
argument_list|>
argument_list|()
decl_stmt|;
specifier|private
name|AmqpTransactionCoordinator
name|coordinator
decl_stmt|;
specifier|private
name|AmqpTransactionId
name|transactionId
decl_stmt|;
specifier|public
name|AmqpTransactionContext
parameter_list|(
name|AmqpSession
name|session
parameter_list|)
block|{
name|this
operator|.
name|session
operator|=
name|session
expr_stmt|;
block|}
comment|/**      * Begins a new transaction scoped to the target session.      *      * @param txId      *      The transaction Id to use for this new transaction.      *      * @throws Exception if an error occurs while starting the transaction.      */
specifier|public
name|void
name|begin
parameter_list|()
throws|throws
name|Exception
block|{
if|if
condition|(
name|transactionId
operator|!=
literal|null
condition|)
block|{
throw|throw
operator|new
name|IOException
argument_list|(
literal|"Begin called while a TX is still Active."
argument_list|)
throw|;
block|}
specifier|final
name|AmqpTransactionId
name|txId
init|=
name|session
operator|.
name|getConnection
argument_list|()
operator|.
name|getNextTransactionId
argument_list|()
decl_stmt|;
specifier|final
name|ClientFuture
name|request
init|=
operator|new
name|ClientFuture
argument_list|(
operator|new
name|ClientFutureSynchronization
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|void
name|onPendingSuccess
parameter_list|()
block|{
name|transactionId
operator|=
name|txId
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|onPendingFailure
parameter_list|(
name|Throwable
name|cause
parameter_list|)
block|{
name|transactionId
operator|=
literal|null
expr_stmt|;
block|}
block|}
argument_list|)
decl_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"Attempting to Begin TX:[{}]"
argument_list|,
name|txId
argument_list|)
expr_stmt|;
name|session
operator|.
name|getScheduler
argument_list|()
operator|.
name|execute
argument_list|(
operator|new
name|Runnable
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|void
name|run
parameter_list|()
block|{
if|if
condition|(
name|coordinator
operator|==
literal|null
operator|||
name|coordinator
operator|.
name|isClosed
argument_list|()
condition|)
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"Creating new Coordinator for TX:[{}]"
argument_list|,
name|txId
argument_list|)
expr_stmt|;
name|coordinator
operator|=
operator|new
name|AmqpTransactionCoordinator
argument_list|(
name|session
argument_list|)
expr_stmt|;
name|coordinator
operator|.
name|open
argument_list|(
operator|new
name|AsyncResult
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|void
name|onSuccess
parameter_list|()
block|{
try|try
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"Attempting to declare TX:[{}]"
argument_list|,
name|txId
argument_list|)
expr_stmt|;
name|coordinator
operator|.
name|declare
argument_list|(
name|txId
argument_list|,
name|request
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
name|request
operator|.
name|onFailure
argument_list|(
name|e
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Override
specifier|public
name|void
name|onFailure
parameter_list|(
name|Throwable
name|result
parameter_list|)
block|{
name|request
operator|.
name|onFailure
argument_list|(
name|result
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|isComplete
parameter_list|()
block|{
return|return
name|request
operator|.
name|isComplete
argument_list|()
return|;
block|}
block|}
argument_list|)
expr_stmt|;
block|}
else|else
block|{
try|try
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"Attempting to declare TX:[{}]"
argument_list|,
name|txId
argument_list|)
expr_stmt|;
name|coordinator
operator|.
name|declare
argument_list|(
name|txId
argument_list|,
name|request
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
name|request
operator|.
name|onFailure
argument_list|(
name|e
argument_list|)
expr_stmt|;
block|}
block|}
name|session
operator|.
name|pumpToProtonTransport
argument_list|(
name|request
argument_list|)
expr_stmt|;
block|}
block|}
argument_list|)
expr_stmt|;
name|request
operator|.
name|sync
argument_list|()
expr_stmt|;
block|}
comment|/**      * Commit this transaction which then ends the lifetime of the transacted operation.      *      * @throws Exception if an error occurs while performing the commit      */
specifier|public
name|void
name|commit
parameter_list|()
throws|throws
name|Exception
block|{
if|if
condition|(
name|transactionId
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|IllegalStateException
argument_list|(
literal|"Commit called with no active Transaction."
argument_list|)
throw|;
block|}
name|preCommit
argument_list|()
expr_stmt|;
specifier|final
name|ClientFuture
name|request
init|=
operator|new
name|ClientFuture
argument_list|(
operator|new
name|ClientFutureSynchronization
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|void
name|onPendingSuccess
parameter_list|()
block|{
name|transactionId
operator|=
literal|null
expr_stmt|;
name|postCommit
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|onPendingFailure
parameter_list|(
name|Throwable
name|cause
parameter_list|)
block|{
name|transactionId
operator|=
literal|null
expr_stmt|;
name|postCommit
argument_list|()
expr_stmt|;
block|}
block|}
argument_list|)
decl_stmt|;
name|LOG
operator|.
name|debug
argument_list|(
literal|"Commit on TX[{}] initiated"
argument_list|,
name|transactionId
argument_list|)
expr_stmt|;
name|session
operator|.
name|getScheduler
argument_list|()
operator|.
name|execute
argument_list|(
operator|new
name|Runnable
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|void
name|run
parameter_list|()
block|{
try|try
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"Attempting to commit TX:[{}]"
argument_list|,
name|transactionId
argument_list|)
expr_stmt|;
name|coordinator
operator|.
name|discharge
argument_list|(
name|transactionId
argument_list|,
name|request
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|session
operator|.
name|pumpToProtonTransport
argument_list|(
name|request
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
name|request
operator|.
name|onFailure
argument_list|(
name|e
argument_list|)
expr_stmt|;
block|}
block|}
block|}
argument_list|)
expr_stmt|;
name|request
operator|.
name|sync
argument_list|()
expr_stmt|;
block|}
comment|/**      * Rollback any transacted work performed under the current transaction.      *      * @throws Exception if an error occurs during the rollback operation.      */
specifier|public
name|void
name|rollback
parameter_list|()
throws|throws
name|Exception
block|{
if|if
condition|(
name|transactionId
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|IllegalStateException
argument_list|(
literal|"Rollback called with no active Transaction."
argument_list|)
throw|;
block|}
name|preRollback
argument_list|()
expr_stmt|;
specifier|final
name|ClientFuture
name|request
init|=
operator|new
name|ClientFuture
argument_list|(
operator|new
name|ClientFutureSynchronization
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|void
name|onPendingSuccess
parameter_list|()
block|{
name|transactionId
operator|=
literal|null
expr_stmt|;
name|postRollback
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|onPendingFailure
parameter_list|(
name|Throwable
name|cause
parameter_list|)
block|{
name|transactionId
operator|=
literal|null
expr_stmt|;
name|postRollback
argument_list|()
expr_stmt|;
block|}
block|}
argument_list|)
decl_stmt|;
name|LOG
operator|.
name|debug
argument_list|(
literal|"Rollback on TX[{}] initiated"
argument_list|,
name|transactionId
argument_list|)
expr_stmt|;
name|session
operator|.
name|getScheduler
argument_list|()
operator|.
name|execute
argument_list|(
operator|new
name|Runnable
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|void
name|run
parameter_list|()
block|{
try|try
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"Attempting to roll back TX:[{}]"
argument_list|,
name|transactionId
argument_list|)
expr_stmt|;
name|coordinator
operator|.
name|discharge
argument_list|(
name|transactionId
argument_list|,
name|request
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|session
operator|.
name|pumpToProtonTransport
argument_list|(
name|request
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
name|request
operator|.
name|onFailure
argument_list|(
name|e
argument_list|)
expr_stmt|;
block|}
block|}
block|}
argument_list|)
expr_stmt|;
name|request
operator|.
name|sync
argument_list|()
expr_stmt|;
block|}
comment|//----- Internal access to context properties ----------------------------//
name|AmqpTransactionCoordinator
name|getCoordinator
parameter_list|()
block|{
return|return
name|coordinator
return|;
block|}
name|AmqpTransactionId
name|getTransactionId
parameter_list|()
block|{
return|return
name|transactionId
return|;
block|}
name|boolean
name|isInTransaction
parameter_list|()
block|{
return|return
name|transactionId
operator|!=
literal|null
return|;
block|}
name|void
name|registerTxConsumer
parameter_list|(
name|AmqpReceiver
name|consumer
parameter_list|)
block|{
name|txReceivers
operator|.
name|add
argument_list|(
name|consumer
argument_list|)
expr_stmt|;
block|}
comment|//----- Transaction pre / post completion --------------------------------//
specifier|private
name|void
name|preCommit
parameter_list|()
block|{
for|for
control|(
name|AmqpReceiver
name|receiver
range|:
name|txReceivers
control|)
block|{
name|receiver
operator|.
name|preCommit
argument_list|()
expr_stmt|;
block|}
block|}
specifier|private
name|void
name|preRollback
parameter_list|()
block|{
for|for
control|(
name|AmqpReceiver
name|receiver
range|:
name|txReceivers
control|)
block|{
name|receiver
operator|.
name|preRollback
argument_list|()
expr_stmt|;
block|}
block|}
specifier|private
name|void
name|postCommit
parameter_list|()
block|{
for|for
control|(
name|AmqpReceiver
name|receiver
range|:
name|txReceivers
control|)
block|{
name|receiver
operator|.
name|postCommit
argument_list|()
expr_stmt|;
block|}
name|txReceivers
operator|.
name|clear
argument_list|()
expr_stmt|;
block|}
specifier|private
name|void
name|postRollback
parameter_list|()
block|{
for|for
control|(
name|AmqpReceiver
name|receiver
range|:
name|txReceivers
control|)
block|{
name|receiver
operator|.
name|postRollback
argument_list|()
expr_stmt|;
block|}
name|txReceivers
operator|.
name|clear
argument_list|()
expr_stmt|;
block|}
block|}
end_class

end_unit

