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
name|java
operator|.
name|io
operator|.
name|InterruptedIOException
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
name|concurrent
operator|.
name|Callable
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
name|FutureTask
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
name|command
operator|.
name|TransactionId
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

begin_comment
comment|/**  * Keeps track of all the actions the need to be done when a transaction does a  * commit or rollback.  *   *   */
end_comment

begin_class
specifier|public
specifier|abstract
class|class
name|Transaction
block|{
specifier|public
specifier|static
specifier|final
name|byte
name|START_STATE
init|=
literal|0
decl_stmt|;
comment|// can go to: 1,2,3
specifier|public
specifier|static
specifier|final
name|byte
name|IN_USE_STATE
init|=
literal|1
decl_stmt|;
comment|// can go to: 2,3
specifier|public
specifier|static
specifier|final
name|byte
name|PREPARED_STATE
init|=
literal|2
decl_stmt|;
comment|// can go to: 3
specifier|public
specifier|static
specifier|final
name|byte
name|FINISHED_STATE
init|=
literal|3
decl_stmt|;
specifier|private
specifier|final
name|ArrayList
argument_list|<
name|Synchronization
argument_list|>
name|synchronizations
init|=
operator|new
name|ArrayList
argument_list|<
name|Synchronization
argument_list|>
argument_list|()
decl_stmt|;
specifier|private
name|byte
name|state
init|=
name|START_STATE
decl_stmt|;
specifier|protected
name|FutureTask
argument_list|<
name|?
argument_list|>
name|preCommitTask
init|=
operator|new
name|FutureTask
argument_list|<
name|Object
argument_list|>
argument_list|(
operator|new
name|Callable
argument_list|<
name|Object
argument_list|>
argument_list|()
block|{
specifier|public
name|Object
name|call
parameter_list|()
throws|throws
name|Exception
block|{
name|doPreCommit
argument_list|()
expr_stmt|;
return|return
literal|null
return|;
block|}
block|}
argument_list|)
decl_stmt|;
specifier|protected
name|FutureTask
argument_list|<
name|?
argument_list|>
name|postCommitTask
init|=
operator|new
name|FutureTask
argument_list|<
name|Object
argument_list|>
argument_list|(
operator|new
name|Callable
argument_list|<
name|Object
argument_list|>
argument_list|()
block|{
specifier|public
name|Object
name|call
parameter_list|()
throws|throws
name|Exception
block|{
name|doPostCommit
argument_list|()
expr_stmt|;
return|return
literal|null
return|;
block|}
block|}
argument_list|)
decl_stmt|;
specifier|public
name|byte
name|getState
parameter_list|()
block|{
return|return
name|state
return|;
block|}
specifier|public
name|void
name|setState
parameter_list|(
name|byte
name|state
parameter_list|)
block|{
name|this
operator|.
name|state
operator|=
name|state
expr_stmt|;
block|}
specifier|public
name|void
name|addSynchronization
parameter_list|(
name|Synchronization
name|r
parameter_list|)
block|{
name|synchronizations
operator|.
name|add
argument_list|(
name|r
argument_list|)
expr_stmt|;
if|if
condition|(
name|state
operator|==
name|START_STATE
condition|)
block|{
name|state
operator|=
name|IN_USE_STATE
expr_stmt|;
block|}
block|}
specifier|public
name|Synchronization
name|findMatching
parameter_list|(
name|Synchronization
name|r
parameter_list|)
block|{
name|int
name|existing
init|=
name|synchronizations
operator|.
name|indexOf
argument_list|(
name|r
argument_list|)
decl_stmt|;
if|if
condition|(
name|existing
operator|!=
operator|-
literal|1
condition|)
block|{
return|return
name|synchronizations
operator|.
name|get
argument_list|(
name|existing
argument_list|)
return|;
block|}
return|return
literal|null
return|;
block|}
specifier|public
name|void
name|removeSynchronization
parameter_list|(
name|Synchronization
name|r
parameter_list|)
block|{
name|synchronizations
operator|.
name|remove
argument_list|(
name|r
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|prePrepare
parameter_list|()
throws|throws
name|Exception
block|{
comment|// Is it ok to call prepare now given the state of the
comment|// transaction?
switch|switch
condition|(
name|state
condition|)
block|{
case|case
name|START_STATE
case|:
case|case
name|IN_USE_STATE
case|:
break|break;
default|default:
name|XAException
name|xae
init|=
operator|new
name|XAException
argument_list|(
literal|"Prepare cannot be called now."
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
comment|// // Run the prePrepareTasks
comment|// for (Iterator iter = prePrepareTasks.iterator(); iter.hasNext();) {
comment|// Callback r = (Callback) iter.next();
comment|// r.execute();
comment|// }
block|}
specifier|protected
name|void
name|fireBeforeCommit
parameter_list|()
throws|throws
name|Exception
block|{
for|for
control|(
name|Iterator
argument_list|<
name|Synchronization
argument_list|>
name|iter
init|=
name|synchronizations
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
name|Synchronization
name|s
init|=
name|iter
operator|.
name|next
argument_list|()
decl_stmt|;
name|s
operator|.
name|beforeCommit
argument_list|()
expr_stmt|;
block|}
block|}
specifier|protected
name|void
name|fireAfterCommit
parameter_list|()
throws|throws
name|Exception
block|{
for|for
control|(
name|Iterator
argument_list|<
name|Synchronization
argument_list|>
name|iter
init|=
name|synchronizations
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
name|Synchronization
name|s
init|=
name|iter
operator|.
name|next
argument_list|()
decl_stmt|;
name|s
operator|.
name|afterCommit
argument_list|()
expr_stmt|;
block|}
block|}
specifier|public
name|void
name|fireAfterRollback
parameter_list|()
throws|throws
name|Exception
block|{
name|Collections
operator|.
name|reverse
argument_list|(
name|synchronizations
argument_list|)
expr_stmt|;
for|for
control|(
name|Iterator
argument_list|<
name|Synchronization
argument_list|>
name|iter
init|=
name|synchronizations
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
name|Synchronization
name|s
init|=
name|iter
operator|.
name|next
argument_list|()
decl_stmt|;
name|s
operator|.
name|afterRollback
argument_list|()
expr_stmt|;
block|}
block|}
annotation|@
name|Override
specifier|public
name|String
name|toString
parameter_list|()
block|{
return|return
name|super
operator|.
name|toString
argument_list|()
operator|+
literal|"[synchronizations="
operator|+
name|synchronizations
operator|+
literal|"]"
return|;
block|}
specifier|public
specifier|abstract
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
function_decl|;
specifier|public
specifier|abstract
name|void
name|rollback
parameter_list|()
throws|throws
name|XAException
throws|,
name|IOException
function_decl|;
specifier|public
specifier|abstract
name|int
name|prepare
parameter_list|()
throws|throws
name|XAException
throws|,
name|IOException
function_decl|;
specifier|public
specifier|abstract
name|TransactionId
name|getTransactionId
parameter_list|()
function_decl|;
specifier|public
specifier|abstract
name|Logger
name|getLog
parameter_list|()
function_decl|;
specifier|public
name|boolean
name|isPrepared
parameter_list|()
block|{
return|return
name|getState
argument_list|()
operator|==
name|PREPARED_STATE
return|;
block|}
specifier|public
name|int
name|size
parameter_list|()
block|{
return|return
name|synchronizations
operator|.
name|size
argument_list|()
return|;
block|}
specifier|protected
name|void
name|waitPostCommitDone
parameter_list|(
name|FutureTask
argument_list|<
name|?
argument_list|>
name|postCommitTask
parameter_list|)
throws|throws
name|XAException
throws|,
name|IOException
block|{
try|try
block|{
name|postCommitTask
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
throw|throw
operator|new
name|InterruptedIOException
argument_list|(
name|e
operator|.
name|toString
argument_list|()
argument_list|)
throw|;
block|}
catch|catch
parameter_list|(
name|ExecutionException
name|e
parameter_list|)
block|{
name|Throwable
name|t
init|=
name|e
operator|.
name|getCause
argument_list|()
decl_stmt|;
if|if
condition|(
name|t
operator|instanceof
name|XAException
condition|)
block|{
throw|throw
operator|(
name|XAException
operator|)
name|t
throw|;
block|}
elseif|else
if|if
condition|(
name|t
operator|instanceof
name|IOException
condition|)
block|{
throw|throw
operator|(
name|IOException
operator|)
name|t
throw|;
block|}
else|else
block|{
throw|throw
operator|new
name|XAException
argument_list|(
name|e
operator|.
name|toString
argument_list|()
argument_list|)
throw|;
block|}
block|}
block|}
specifier|protected
name|void
name|doPreCommit
parameter_list|()
throws|throws
name|XAException
block|{
try|try
block|{
name|fireBeforeCommit
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
name|getLog
argument_list|()
operator|.
name|warn
argument_list|(
literal|"PRE COMMIT FAILED: "
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
literal|"PRE COMMIT FAILED"
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
specifier|protected
name|void
name|doPostCommit
parameter_list|()
throws|throws
name|XAException
block|{
try|try
block|{
name|fireAfterCommit
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
name|getLog
argument_list|()
operator|.
name|warn
argument_list|(
literal|"POST COMMIT FAILED: "
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
literal|"POST COMMIT FAILED"
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
block|}
end_class

end_unit
