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
name|transaction
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
name|javax
operator|.
name|transaction
operator|.
name|xa
operator|.
name|XAResource
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
name|TransactionBroker
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
name|ConnectionId
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
name|TransactionStore
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
comment|/**  *   */
end_comment

begin_class
specifier|public
class|class
name|XATransaction
extends|extends
name|Transaction
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
name|XATransaction
operator|.
name|class
argument_list|)
decl_stmt|;
specifier|private
specifier|final
name|TransactionStore
name|transactionStore
decl_stmt|;
specifier|private
specifier|final
name|XATransactionId
name|xid
decl_stmt|;
specifier|private
specifier|final
name|TransactionBroker
name|broker
decl_stmt|;
specifier|private
specifier|final
name|ConnectionId
name|connectionId
decl_stmt|;
specifier|public
name|XATransaction
parameter_list|(
name|TransactionStore
name|transactionStore
parameter_list|,
name|XATransactionId
name|xid
parameter_list|,
name|TransactionBroker
name|broker
parameter_list|,
name|ConnectionId
name|connectionId
parameter_list|)
block|{
name|this
operator|.
name|transactionStore
operator|=
name|transactionStore
expr_stmt|;
name|this
operator|.
name|xid
operator|=
name|xid
expr_stmt|;
name|this
operator|.
name|broker
operator|=
name|broker
expr_stmt|;
name|this
operator|.
name|connectionId
operator|=
name|connectionId
expr_stmt|;
if|if
condition|(
name|LOG
operator|.
name|isDebugEnabled
argument_list|()
condition|)
block|{
name|LOG
operator|.
name|debug
argument_list|(
literal|"XA Transaction new/begin : "
operator|+
name|xid
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Override
specifier|public
name|void
name|commit
parameter_list|(
name|boolean
name|onePhase
parameter_list|)
throws|throws
name|XAException
throws|,
name|IOException
block|{
if|if
condition|(
name|LOG
operator|.
name|isDebugEnabled
argument_list|()
condition|)
block|{
name|LOG
operator|.
name|debug
argument_list|(
literal|"XA Transaction commit onePhase:"
operator|+
name|onePhase
operator|+
literal|", xid: "
operator|+
name|xid
argument_list|)
expr_stmt|;
block|}
switch|switch
condition|(
name|getState
argument_list|()
condition|)
block|{
case|case
name|START_STATE
case|:
comment|// 1 phase commit, no work done.
name|checkForPreparedState
argument_list|(
name|onePhase
argument_list|)
expr_stmt|;
name|setStateFinished
argument_list|()
expr_stmt|;
break|break;
case|case
name|IN_USE_STATE
case|:
comment|// 1 phase commit, work done.
name|checkForPreparedState
argument_list|(
name|onePhase
argument_list|)
expr_stmt|;
name|doPrePrepare
argument_list|()
expr_stmt|;
name|setStateFinished
argument_list|()
expr_stmt|;
name|storeCommit
argument_list|(
name|getTransactionId
argument_list|()
argument_list|,
literal|false
argument_list|,
name|preCommitTask
argument_list|,
name|postCommitTask
argument_list|)
expr_stmt|;
break|break;
case|case
name|PREPARED_STATE
case|:
comment|// 2 phase commit, work done.
comment|// We would record commit here.
name|setStateFinished
argument_list|()
expr_stmt|;
name|storeCommit
argument_list|(
name|getTransactionId
argument_list|()
argument_list|,
literal|true
argument_list|,
name|preCommitTask
argument_list|,
name|postCommitTask
argument_list|)
expr_stmt|;
break|break;
default|default:
name|illegalStateTransition
argument_list|(
literal|"commit"
argument_list|)
expr_stmt|;
block|}
block|}
specifier|private
name|void
name|storeCommit
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
name|XAException
throws|,
name|IOException
block|{
try|try
block|{
name|transactionStore
operator|.
name|commit
argument_list|(
name|getTransactionId
argument_list|()
argument_list|,
name|wasPrepared
argument_list|,
name|preCommitTask
argument_list|,
name|postCommitTask
argument_list|)
expr_stmt|;
name|waitPostCommitDone
argument_list|(
name|postCommitTask
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|XAException
name|xae
parameter_list|)
block|{
throw|throw
name|xae
throw|;
block|}
catch|catch
parameter_list|(
name|Throwable
name|t
parameter_list|)
block|{
name|LOG
operator|.
name|warn
argument_list|(
literal|"Store COMMIT FAILED: "
argument_list|,
name|t
argument_list|)
expr_stmt|;
name|rollback
argument_list|()
expr_stmt|;
name|XAException
name|xae
init|=
operator|new
name|XAException
argument_list|(
literal|"STORE COMMIT FAILED: Transaction rolled back."
argument_list|)
decl_stmt|;
name|xae
operator|.
name|errorCode
operator|=
name|XAException
operator|.
name|XA_RBOTHER
expr_stmt|;
name|xae
operator|.
name|initCause
argument_list|(
name|t
argument_list|)
expr_stmt|;
throw|throw
name|xae
throw|;
block|}
block|}
specifier|private
name|void
name|illegalStateTransition
parameter_list|(
name|String
name|callName
parameter_list|)
throws|throws
name|XAException
block|{
name|XAException
name|xae
init|=
operator|new
name|XAException
argument_list|(
literal|"Cannot call "
operator|+
name|callName
operator|+
literal|" now."
argument_list|)
decl_stmt|;
name|xae
operator|.
name|errorCode
operator|=
name|XAException
operator|.
name|XAER_PROTO
expr_stmt|;
throw|throw
name|xae
throw|;
block|}
specifier|private
name|void
name|checkForPreparedState
parameter_list|(
name|boolean
name|onePhase
parameter_list|)
throws|throws
name|XAException
block|{
if|if
condition|(
operator|!
name|onePhase
condition|)
block|{
name|XAException
name|xae
init|=
operator|new
name|XAException
argument_list|(
literal|"Cannot do 2 phase commit if the transaction has not been prepared."
argument_list|)
decl_stmt|;
name|xae
operator|.
name|errorCode
operator|=
name|XAException
operator|.
name|XAER_PROTO
expr_stmt|;
throw|throw
name|xae
throw|;
block|}
block|}
specifier|private
name|void
name|doPrePrepare
parameter_list|()
throws|throws
name|XAException
throws|,
name|IOException
block|{
try|try
block|{
name|prePrepare
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|XAException
name|e
parameter_list|)
block|{
throw|throw
name|e
throw|;
block|}
catch|catch
parameter_list|(
name|Throwable
name|e
parameter_list|)
block|{
name|LOG
operator|.
name|warn
argument_list|(
literal|"PRE-PREPARE FAILED: "
argument_list|,
name|e
argument_list|)
expr_stmt|;
name|rollback
argument_list|()
expr_stmt|;
name|XAException
name|xae
init|=
operator|new
name|XAException
argument_list|(
literal|"PRE-PREPARE FAILED: Transaction rolled back."
argument_list|)
decl_stmt|;
name|xae
operator|.
name|errorCode
operator|=
name|XAException
operator|.
name|XA_RBOTHER
expr_stmt|;
name|xae
operator|.
name|initCause
argument_list|(
name|e
argument_list|)
expr_stmt|;
throw|throw
name|xae
throw|;
block|}
block|}
annotation|@
name|Override
specifier|public
name|void
name|rollback
parameter_list|()
throws|throws
name|XAException
throws|,
name|IOException
block|{
if|if
condition|(
name|LOG
operator|.
name|isDebugEnabled
argument_list|()
condition|)
block|{
name|LOG
operator|.
name|debug
argument_list|(
literal|"XA Transaction rollback: "
operator|+
name|xid
argument_list|)
expr_stmt|;
block|}
switch|switch
condition|(
name|getState
argument_list|()
condition|)
block|{
case|case
name|START_STATE
case|:
comment|// 1 phase rollback no work done.
name|setStateFinished
argument_list|()
expr_stmt|;
break|break;
case|case
name|IN_USE_STATE
case|:
comment|// 1 phase rollback work done.
name|setStateFinished
argument_list|()
expr_stmt|;
name|transactionStore
operator|.
name|rollback
argument_list|(
name|getTransactionId
argument_list|()
argument_list|)
expr_stmt|;
name|doPostRollback
argument_list|()
expr_stmt|;
break|break;
case|case
name|PREPARED_STATE
case|:
comment|// 2 phase rollback work done.
name|setStateFinished
argument_list|()
expr_stmt|;
name|transactionStore
operator|.
name|rollback
argument_list|(
name|getTransactionId
argument_list|()
argument_list|)
expr_stmt|;
name|doPostRollback
argument_list|()
expr_stmt|;
break|break;
case|case
name|FINISHED_STATE
case|:
comment|// failure to commit
name|transactionStore
operator|.
name|rollback
argument_list|(
name|getTransactionId
argument_list|()
argument_list|)
expr_stmt|;
name|doPostRollback
argument_list|()
expr_stmt|;
break|break;
default|default:
throw|throw
operator|new
name|XAException
argument_list|(
literal|"Invalid state"
argument_list|)
throw|;
block|}
block|}
specifier|private
name|void
name|doPostRollback
parameter_list|()
throws|throws
name|XAException
block|{
try|try
block|{
name|fireAfterRollback
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Throwable
name|e
parameter_list|)
block|{
comment|// I guess this could happen. Post commit task failed
comment|// to execute properly.
name|LOG
operator|.
name|warn
argument_list|(
literal|"POST ROLLBACK FAILED: "
argument_list|,
name|e
argument_list|)
expr_stmt|;
name|XAException
name|xae
init|=
operator|new
name|XAException
argument_list|(
literal|"POST ROLLBACK FAILED"
argument_list|)
decl_stmt|;
name|xae
operator|.
name|errorCode
operator|=
name|XAException
operator|.
name|XAER_RMERR
expr_stmt|;
name|xae
operator|.
name|initCause
argument_list|(
name|e
argument_list|)
expr_stmt|;
throw|throw
name|xae
throw|;
block|}
block|}
annotation|@
name|Override
specifier|public
name|int
name|prepare
parameter_list|()
throws|throws
name|XAException
throws|,
name|IOException
block|{
if|if
condition|(
name|LOG
operator|.
name|isDebugEnabled
argument_list|()
condition|)
block|{
name|LOG
operator|.
name|debug
argument_list|(
literal|"XA Transaction prepare: "
operator|+
name|xid
argument_list|)
expr_stmt|;
block|}
switch|switch
condition|(
name|getState
argument_list|()
condition|)
block|{
case|case
name|START_STATE
case|:
comment|// No work done.. no commit/rollback needed.
name|setStateFinished
argument_list|()
expr_stmt|;
return|return
name|XAResource
operator|.
name|XA_RDONLY
return|;
case|case
name|IN_USE_STATE
case|:
comment|// We would record prepare here.
name|doPrePrepare
argument_list|()
expr_stmt|;
name|setState
argument_list|(
name|Transaction
operator|.
name|PREPARED_STATE
argument_list|)
expr_stmt|;
name|transactionStore
operator|.
name|prepare
argument_list|(
name|getTransactionId
argument_list|()
argument_list|)
expr_stmt|;
return|return
name|XAResource
operator|.
name|XA_OK
return|;
default|default:
name|illegalStateTransition
argument_list|(
literal|"prepare"
argument_list|)
expr_stmt|;
return|return
name|XAResource
operator|.
name|XA_RDONLY
return|;
block|}
block|}
specifier|private
name|void
name|setStateFinished
parameter_list|()
block|{
name|setState
argument_list|(
name|Transaction
operator|.
name|FINISHED_STATE
argument_list|)
expr_stmt|;
name|broker
operator|.
name|removeTransaction
argument_list|(
name|xid
argument_list|)
expr_stmt|;
block|}
specifier|public
name|ConnectionId
name|getConnectionId
parameter_list|()
block|{
return|return
name|connectionId
return|;
block|}
annotation|@
name|Override
specifier|public
name|TransactionId
name|getTransactionId
parameter_list|()
block|{
return|return
name|xid
return|;
block|}
annotation|@
name|Override
specifier|public
name|Logger
name|getLog
parameter_list|()
block|{
return|return
name|LOG
return|;
block|}
block|}
end_class

end_unit

